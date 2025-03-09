/*maybe copyright like Apache 2.0 for open source necessary to also further mention the: 
 * "distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied."
 * point.
 */

package TUHH_Krumnow.AutomataForestDataSets;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.lang.Math;
import net.automatalib.automata.fsa.DFA;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

/**
 * ForestCV is an implementation of an automata Forest. It is a different way to use the RPNI algorithm to generate
 * an automaton given strings element of a target regular language and strings not element of the target language (case DFA) and 
 * input strings with their corresponding output strings (case Mealy machine). The forest uses "Cross Validation" to choose the 
 * appropriate output automaton. <br><br>
 * 
 * General Approach: each inner RPNI gets different training sets picked randomly to build each automata inside the forest, 
 * where the amount of the input dataset given is determined by parameters "bagSizePositive", "bagSizeNegative" (DFA) and 
 * "bagSizeMealy" (Mealy machine) and is less than the input dataset. The amount 
 * of built DFA's gets determined by "numberOfBags". Strings not picked as training data gets saved as the test set for the 
 * cross validation (variables with prefix "crossTestSet-" indicate them).<br><br>
 * 
 * Approach DFA: to output the DFA chosen as best by cross validation, a "scoreCounter" is used to keep track of the evaluation.
 * DFA's now get their crossTestSet as input and get scored appropriate to the number of correct classified strings (correct string
 * equals point in scoreCounter). The outputDFA "finalModel" is the DFA with the highest score and is obtained using the "getForestModel()"
 * function.<br><br>
 * 
 * Approach Mealy machine: the same as for DFA's with the exception, that scoreCounter now counts the number of correct translated 
 * words (ForestCV_W) and in the case for Edit distances "scoreCounterEditDist" measures the average edit distance the output string
 * has, compared to the Crossvalidation output test string.
 * 
 * @author Arne Krumnow
 *
 */
public class ForestCV {
	
	//forest parameters
	private Alphabet<Character> alphabet;
	private int numberOfBags;
	///DFA
	private List<List<String>> positiveSets;
	private List<List<String>> negativeSets;
	private List<DFA<?,Character>> ModelList;
	private int bagSizePositive;
	private int bagSizeNegative;
	///Mealy
	private List<List<String>> inputSet;
	private List<List<String>> outputSet;
	private List<MealyMachine<?,Character,?,Character>> ModelListM;
	private int bagSizeMealy;
	
	//cross validation
	private List<Integer> scoreCounter;
	///DFA
	private List<List<String>> CrossTestSetPositive;
	private List<List<String>> CrossTestSetNegative;
	///Mealy
	private List<List<String>> CrossTestSetInput;
	private List<List<String>> CrossTestSetOutput;
	private List<Double> scoreCounterEditDist;
	
	//output automaton evaluation
	private DFA<?,Character> finalModel;
	private int MaxScoreCounter;
	private MealyMachine<?,Character,?,Character> finalModelM;
	
	/**
	 * Constructor used to build a ForestCV for DFAs.
	 * @param _alphabet as the alphabet of the to be learned DFA; where _alphabet must include all characters inside the training set.
	 * @param positiveSet as the list of strings used for positive inference , i.e., strings that are element of the target language.
	 * @param negativeSet as the list of strings used for negative inference , i.e., strings that are not element of the target language.
	 * @param ratio as the ratio to determine the amount of strings each RPNI algorithm inside ForestCV gets. This calculates 
	 * by multiplying the ratio times the size of the initial training set.
	 * @param numBags as the number of automata to be built inside the forest.
	 */
	public ForestCV(Alphabet<Character> _alphabet, List<String> positiveSet,
			List<String> negativeSet,double ratio, int numBags) {
		this.alphabet = _alphabet;
		DetermineNumberOfBags(numBags);
		this.bagSizePositive = DetermineBagSize(positiveSet.size(),ratio);
		this.bagSizeNegative = DetermineBagSize(negativeSet.size(),ratio);
		FillBaggingSetsAndTestSets(positiveSet,negativeSet);
		trainingOfTheForest();
		ScoreBoardEvaluationOfTheForest();
		FindFinalModel();	
	}
	
	/**
	 * Constructor used to build a ForestCV for Mealy machines evaluating the number of correct translated words.
	 * @param _alphabet as the alphabet of the to be learned DFA; where @param _alpabet must include all characters inside the training set.
	 * @param inputSet as the list of strings sampled as input strings from a target Mealy machine.
	 * @param outputSet as the corresponding sampled list of output strings to parameter inputSet.
	 * @param ratio as the ratio to determine the amount of strings each RPNI algorithm inside ForestCV gets. This calculates 
	 * by multiplying the ratio times the size of the initial training set.
	 * @param numBags as the number of automata to be built inside the forest.
	 * @param isMealy as a boolean decider to switch from DFA to Mealy machines, where true equals Mealy machine and false defaults to build a DFA.
	 */
	public ForestCV(Alphabet<Character> _alphabet, List<String> inputSet,
			List<String> outputSet, double ratio, int numBags, Boolean isMealy) {
		if(inputSet.size()!=outputSet.size()) {
			throw new IllegalArgumentException("input and output must be the same size!");
		}
		if(isMealy) {
			this.alphabet = _alphabet;
			DetermineNumberOfBags(numBags);
			this.bagSizeMealy = DetermineBagSize(inputSet.size(),ratio);
			FillBaggingSetsAndTestSetsM(inputSet,outputSet);
			trainingOfTheForestM();
			ScoreBoardEvaluationOfTheForestM();
			FindFinalModelM();
		}else {
			new ForestCV(_alphabet,inputSet,outputSet,ratio,numBags);
		}
	}
	
	/**
	 * Constructor used to build a ForestCV for Mealy machines evaluating the average edit distance of translated words.
	 * @param _alphabet as the alphabet of the to be learned DFA; where @param _alpabet must include all characters inside the training set.
	 * @param inputSet as the list of strings sampled as input strings from a target Mealy machine.
	 * @param outputSet as the corresponding sampled list of output strings to parameter inputSet.
	 * @param ratio as the ratio to determine the amount of strings each RPNI algorithm inside ForestCV gets. This calculates 
	 * by multiplying the ratio times the size of the initial training set.
	 * @param numBags as the number of automata to be built inside the forest.
	 * @param isMealy as a boolean decider to switch from DFA to Mealy machines, where true equals Mealy machine and false defaults to build a DFA.
	 * @param minEditDist as a boolean decider to enable cross validation with miniminzation of the average edit distance.
	 * To enable, also "isMealy" must evaluate to true, else the standard ForestCV for DFA's is executed.
	 */
	public ForestCV(Alphabet<Character> _alphabet, List<String> inputSet,
			List<String> outputSet, double ratio, int numBags, Boolean isMealy, Boolean minEditDist) {
		if(inputSet.size()!=outputSet.size()) {
			throw new IllegalArgumentException("input and output must be the same size!");
		}
		if(isMealy) {
			if(minEditDist) {
				this.alphabet = _alphabet;
				DetermineNumberOfBags(numBags);
				this.bagSizeMealy = DetermineBagSize(inputSet.size(),ratio);
				FillBaggingSetsAndTestSetsM(inputSet,outputSet);
				trainingOfTheForestM();
				MinEditDistEvalOfTheForestM();
				FindFinalModelFromEditDistM();
				
			}else {
				this.alphabet = _alphabet;
				DetermineNumberOfBags(numBags);
				this.bagSizeMealy = DetermineBagSize(inputSet.size(),ratio);
				FillBaggingSetsAndTestSetsM(inputSet,outputSet);
				trainingOfTheForestM();
				ScoreBoardEvaluationOfTheForestM();
				FindFinalModelM();
			}
		}else {
			new ForestCV(_alphabet,inputSet,outputSet,ratio,numBags);
		}
	}
	
	
	/**
	 * setter for number of automata inside forest
	 * @param numBags
	 */
	private void DetermineNumberOfBags(int numBags) {
		this.numberOfBags = numBags;
	}
	
	/**
	 * calculates the amount of strings used for training with respect to the input ratio
	 * @param SetSize as amount of training strings given to the constructor
	 * @param ratio as forest parameter given in constructor
	 * @return amount of strings one inner RPNI gets as training set
	 */
	private int DetermineBagSize(int SetSize,double ratio) {
		if(ratio <= 0 || ratio >= 1) {
			throw new IllegalArgumentException("ratio must be smaller than 1");
		}
		return (int) Math.round((SetSize) * ratio);
	}
	
	/**
	 * Building of the general forest structure for DFA, where every instance gets assigned their corresponding training and cross validation 
	 * test set. Both sets are mutually exclusive with respect to the position they hold in the input to the constructor, i.e., 
	 * if data cleaning beforehand eliminated duplicates, both sets are mutually exclusive.  
	 * @param inputSetPos as the list of strings for positive inference.
	 * @param inputSetNeg as the list of strings for negative inference.
	 */
	private void FillBaggingSetsAndTestSets(List<String> inputSetPos, List<String> inputSetNeg) {
		this.positiveSets = new ArrayList<List<String>>();
		this.negativeSets = new ArrayList<List<String>>();
		this.CrossTestSetPositive = new ArrayList<List<String>>();
		this.CrossTestSetNegative = new ArrayList<List<String>>();
		
		
		for (int i = 0; i < this.numberOfBags;i++) { //filling every bag
			this.positiveSets.add(new  ArrayList<String>());
			this.negativeSets.add(new  ArrayList<String>());
			this.CrossTestSetPositive.add(new ArrayList<String>());
			this.CrossTestSetNegative.add(new ArrayList<String>());
			List<List<String>> temppos = HelperFunctions.getCrossFractionOfList(inputSetPos, this.bagSizePositive);
			this.positiveSets.get(i).addAll(temppos.get(0));
			this.CrossTestSetPositive.get(i).addAll(temppos.get(1));
			List<List<String>> tempneg =  HelperFunctions.getCrossFractionOfList(inputSetNeg, this.bagSizeNegative);
			this.negativeSets.get(i).addAll(tempneg.get(0));
			this.CrossTestSetNegative.get(i).addAll(tempneg.get(1));
		}
	}
	
	/**
	 * Building of the general forest structure for Mealy machines, where every instance gets assigned the given partion of input strings 
	 * with the corresponding list of output strings. Additionaly similar to the DFA case, 
	 * instances gets assigned their corresponding training and cross validation 
	 * test set, which are mutually exclusive by position in the constructor input.
	 * @param inputSet as the list of input strings from the constructor
	 * @param outputSet as the list of output strings from the constructor
	 */
	private void FillBaggingSetsAndTestSetsM(List<String> inputSet, List<String> outputSet) {
		this.inputSet = new ArrayList<List<String>>();
		this.outputSet = new ArrayList<List<String>>();
		this.CrossTestSetInput= new ArrayList<List<String>>();
		this.CrossTestSetOutput = new ArrayList<List<String>>();
				
		for (int i = 0; i < this.numberOfBags;i++) { //filling of the bags
			this.inputSet.add(new  ArrayList<String>());
			this.outputSet.add(new  ArrayList<String>());
			this.CrossTestSetInput.add(new ArrayList<String>());
			this.CrossTestSetOutput.add(new ArrayList<String>());
			List<List<String>> tempList = HelperFunctions.getCrossFractionOfTwoLists(inputSet,outputSet, this.bagSizeMealy);
			this.inputSet.get(i).addAll(tempList.get(0));
			this.CrossTestSetInput.get(i).addAll(tempList.get(1));
			this.outputSet.get(i).addAll(tempList.get(2));
			this.CrossTestSetOutput.get(i).addAll(tempList.get(3));
		}
	}
	
	
	/**
	 * training of the prepared instances with the RPNI algorithm for DFAs
	 */
	private void trainingOfTheForest() {
		this.ModelList = new ArrayList<DFA<?,Character>>();
		for(int i = 0;i<this.numberOfBags;i++) {
			this.ModelList.add(HelperFunctions.computeModelBlueFringe
					(this.alphabet, 
					HelperFunctions.transformFromListToCollection(this.positiveSets.get(i)),
					HelperFunctions.transformFromListToCollection(this.negativeSets.get(i))));
		}
	}
	
	/**
	 * training of the prepared instances with the RPNI algorithm for Mealy machines
	 */
	private void trainingOfTheForestM() {
		this.ModelListM = new ArrayList<MealyMachine<?,Character,?,Character>>();
		for(int i = 0;i<this.numberOfBags;i++) {
			this.ModelListM.add(HelperFunctions.computeModelMealyBlueFringe(
					this.alphabet,
					this.inputSet.get(i),
					this.outputSet.get(i)));
		}
	}
	
	/**
	 * evaluation of ForestCV for DFA using the scoreboard. Here the scoreCounter list gets filled where every index correlates 
	 * to one DFA inside the forest. The score gets computed by adding one point per correct classified word starting at 0.
	 */
	private void ScoreBoardEvaluationOfTheForest() {
		this.scoreCounter = new ArrayList<Integer>();
		for(int i = 0; i<this.ModelList.size();i++) {
			this.scoreCounter.add(0);
			for(int j = 0; j< this.CrossTestSetPositive.get(i).size();j++) {
				if(this.ModelList.get(i).accepts(Word.fromString(this.CrossTestSetPositive.get(i).get(j)))) {
					this.scoreCounter.set(i, this.scoreCounter.get(i) + 1);
				}
			}
			for(int j = 0; j< this.CrossTestSetNegative.get(i).size(); j++) {
				if(false == this.ModelList.get(i).accepts(Word.fromString(this.CrossTestSetNegative.get(i).get(j)))) {
					this.scoreCounter.set(i, this.scoreCounter.get(i) + 1);
				}
			}
		}
	}
	
	/**
	 * evaluation of ForestCV_W for Mealy machines using the scoreboard only counting correct translated words. Here the scoreCounter list gets filled where every index correlates 
	 * to one Mealy machine inside the forest. The score gets computed by adding one point per correct translated word starting at 0.
	 */
	private void ScoreBoardEvaluationOfTheForestM() {
		this.scoreCounter = new ArrayList<Integer>();
		for(int i = 0; i<this.ModelListM.size();i++) {
			this.scoreCounter.add(0);
			for(int j = 0; j< this.CrossTestSetInput.get(i).size();j++) {
				if(Objects.equals(HelperFunctions.WordToStringIT(
						this.ModelListM.get(i).computeOutput(Word.fromString(this.CrossTestSetInput.get(i).get(j)))),
						this.CrossTestSetOutput.get(i).get(j))){
					this.scoreCounter.set(i, this.scoreCounter.get(i) + 1);
				}
			}
		}
	}
	
	/**
	 * evaluation of ForestCV_ED for Mealy machines using the scoreboard and averaging the edit distance of each translated word in the cross validation test set
	 * corresponding to the given Mealy machine. 
	 */
	private void MinEditDistEvalOfTheForestM(){
		this.scoreCounterEditDist = new ArrayList<Double>();
		for(int i = 0; i<this.ModelListM.size();i++) {
			this.scoreCounterEditDist.add(0.0);
			for(int j = 0; j< this.CrossTestSetInput.get(i).size();j++) {
			this.scoreCounterEditDist.set(i, this.scoreCounterEditDist.get(i) + HelperFunctions.EditDistanceHamming(
					HelperFunctions.WordToStringIT(this.ModelListM.get(i).computeOutput(Word.fromString(this.CrossTestSetInput.get(i).get(j)))),
					this.CrossTestSetOutput.get(i).get(j)));
			}		
			this.scoreCounterEditDist.set(i, this.scoreCounterEditDist.get(i) / this.CrossTestSetOutput.size());
		}

	}
	
	
	/**
	 * evaluation of the scoreboard for DFA and locking in the winning index, i.e., index with the most correct classified words
	 * as the DFA that can be returned to the user as the best DFA inside the forest.
	 */
	private void FindFinalModel() {
		int tempValue = 0;
		int tempPosition = 0;
		for (int i = 0;i<this.numberOfBags;i++) {
			if(this.scoreCounter.get(i) > tempValue) {
				tempValue = this.scoreCounter.get(i);
				tempPosition = i;
			}
		}
		this.MaxScoreCounter = tempValue;
		this.finalModel = this.ModelList.get(tempPosition);
	}
	
	/**
	 * evaluation of the scoreboard for Mealy machines and locking in the winning index, i.e., index with the most correct translated words
	 * as the Mealy machine that can be returned to the user as the best Mealy machine inside the forest.
	 */
	private void FindFinalModelM() {
		int tempValue = 0;
		int tempPosition = 0;
		for (int i = 0;i<this.numberOfBags;i++) {
			if(this.scoreCounter.get(i) > tempValue) {
				tempValue = this.scoreCounter.get(i);
				tempPosition = i;
			}
		}
		this.MaxScoreCounter = tempValue;
		this.finalModelM = this.ModelListM.get(tempPosition);
	}
	
	/**
	 * evaluation of the scorebpard for Mealy machines using the average edit distance. The method finds the index in the ForestCV_ED
	 * with the lowest average edit distance, i.e., the Mealy machine whose output words where on average the closest to the target Mealy machine.
	 */
	private void FindFinalModelFromEditDistM() {
		double tempValue = this.scoreCounterEditDist.get(0);
		int tempPosition = 0;
		for (int i = 1;i<this.numberOfBags;i++) {
			if(this.scoreCounterEditDist.get(i) < tempValue) {
				tempValue = this.scoreCounterEditDist.get(i);
				tempPosition = i;
			}
		}
		this.MaxScoreCounter = (int) tempValue;
		this.finalModelM = this.ModelListM.get(tempPosition);
	}
	
	/**
	 * getter for calculated output DFA
	 * @return found output of the forest
	 */
	public DFA<?,Character> getForestModel(){
		return this.finalModel;
	}
	
	/**
	 * getter for calculated output Mealy machine
	 * @return found output of the forest ForestCV_W or ForestCV_ED
	 */
		public MealyMachine<?,Character,?,Character> getForestModelM(){
			return this.finalModelM;
		}
	
	/**
	 * helperfunction to gain inside knowledge about the forest, specifically the scoreboard evaluation
	 * @return minimal score reached by a DFA inside the forest
	 */
	public int getForestMinScore() {
		int tempValue = this.scoreCounter.get(0);
		for (int i = 0;i<this.scoreCounter.size();i++) {
			if(this.scoreCounter.get(i) < tempValue) {
				tempValue = this.scoreCounter.get(i);
			}
		}
		return tempValue;
	}
	
	/**
	 * helperfunction to gain inside knowledge about the forest, specifically the scoreboard evaluation
	 * @return maximum score reached by a DFA inside the forest
	 */
	public int getForestMaxScore() {
		return this.MaxScoreCounter;
	}
	
	
}
