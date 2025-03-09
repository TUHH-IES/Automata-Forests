package TUHH_Krumnow.AutomataForestDataSets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

/**
 * unites the ForestCV and ForestMV classes and their respective Mealy Machine implementation into 1 class.
 * @author Arne Krumnow
 *
 */
public class AutomataForestMealyMachine {

	//forest parameters
	private Alphabet<Character> alphabet;
	private int numberOfBags;
	public int lastLength = 0;
	
	private List<List<String>> inputSet;
	private List<List<String>> outputSet;
	private List<MealyMachine<?, Character, ?, Character>> ModelListM;
	private int bagSizeMealy;
	private Alphabet<Character> outputSigma;
	//CV
	private List<List<String>> CrossTestSetInput;
	private List<List<String>> CrossTestSetOutput;
	private List<Double> scoreCounterEditDist;
	private List<Integer> scoreCounter;
	private MealyMachine<?,Character,?,Character> finalModelW;
	private MealyMachine<?,Character,?,Character> finalModelED;
	public int MaxScoreCounter;
	
	/**
	 * Constructor for AutomataForest for Mealy machines
	 * @param _alphabet as the alphabet of the to be learned DFA; where _alphabet must include all characters inside the training set.
	 * @param _outputSigma as the expected alphabet that gets produced for the output strings of the target automaton. _outputSigma is needed to set boundaries of ForestMV in which to choose characters
	 * @param inputSet as the training set a List of strings representing the input strings for a Mealy machine. 
	 * @param outputSet as the training set a List of strings representing the corresponding output strings to the given input strings.
	 * @param ratio as the ratio to determine the amount of strings each RPNI algorithm inside ForestMV gets. This calculates 
	 * by multiplying the ratio times the size of the initial training set.
	 * @param numBags as the number of automata to be built inside the forest.
	 */
	public AutomataForestMealyMachine(Alphabet<Character> _alphabet, Alphabet<Character> _outputSigma, List<String> inputSet,
			List<String> outputSet, double ratio, int numBags) {
		if (inputSet.size() != outputSet.size()) {
			throw new IllegalArgumentException("input and output must be the same size!");
		}
		this.alphabet = _alphabet;
		this.outputSigma = _outputSigma;
		numberOfBags = numBags;
		this.bagSizeMealy = DetermineBagSize(inputSet.size(), ratio);
		FillBaggingSetsAndTestSets(inputSet, outputSet);
		trainingOfTheForest();
	}
	
	/**
	 * method to determine the amount of strings each inner RPNI gets to learn its automaton.
	 * @param SetSize initial amount of training strings given to the constructor
	 * @param ratio forest parameter
	 * @return amount of strings for RPNI to be trained on
	 */
	private int DetermineBagSize(int SetSize, double ratio) {
		if (ratio <= 0 || ratio > 1) {
			throw new IllegalArgumentException("ratio must be between 0 and 1");
		}
		return (int) Math.round((SetSize) * ratio);
	}
	
	/**
	 * Building of the general forest structure for Mealy machines, where every instance gets assigned the given partion of input strings 
	 * with the corresponding list of output strings. Additionaly similar to the DFA case, 
	 * instances gets assigned their corresponding training and cross validation 
	 * test set, which are mutually exclusive by position in the constructor input.
	 * @param inputSet as the list of input strings from the constructor
	 * @param outputSet as the list of output strings from the constructor
	 */
	private void FillBaggingSetsAndTestSets(List<String> inputSet, List<String> outputSet) {
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
	 * training of the prepared instances with the RPNI algorithm for Mealy machines
	 */
	private void trainingOfTheForest() {
		this.ModelListM = new ArrayList<MealyMachine<?,Character,?,Character>>();
		for(int i = 0;i<this.numberOfBags;i++) {
			this.ModelListM.add(HelperFunctions.computeModelMealyBlueFringe(
					this.alphabet,
					this.inputSet.get(i),
					this.outputSet.get(i)));
		}
	}
	
	public MealyMachine<?,Character,?,Character> getOutputCV_maxWords(){
		ScoreBoardEvaluationOfTheForest();
		FindFinalModel();
		return finalModelW;
	}
	
	public MealyMachine<?,Character,?,Character> getOutputCV_minED(){
		MinEditDistEvalOfTheForest();
		FindFinalModelFromEditDist();
		return finalModelED;
	}
	
	/**
	 * evaluation of ForestCV_W for Mealy machines using the scoreboard only counting correct translated words. Here the scoreCounter list gets filled where every index correlates 
	 * to one Mealy machine inside the forest. The score gets computed by adding one point per correct translated word starting at 0.
	 */
	private void ScoreBoardEvaluationOfTheForest() {
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
	 * evaluation of the scoreboard for Mealy machines and locking in the winning index, i.e., index with the most correct translated words
	 * as the Mealy machine that can be returned to the user as the best Mealy machine inside the forest.
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
		this.finalModelW = this.ModelListM.get(tempPosition);
	}
	
	/**
	 * evaluation of ForestCV_ED for Mealy machines using the scoreboard and averaging the edit distance of each translated word in the cross validation test set
	 * corresponding to the given Mealy machine. 
	 */
	private void MinEditDistEvalOfTheForest(){
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
	 * evaluation of the scorebpard for Mealy machines using the average edit distance. The method finds the index in the ForestCV_ED
	 * with the lowest average edit distance, i.e., the Mealy machine whose output words where on average the closest to the target Mealy machine.
	 */
	private void FindFinalModelFromEditDist() {
		double tempValue = this.scoreCounterEditDist.get(0);
		int tempPosition = 0;
		for (int i = 1;i<this.numberOfBags;i++) {
			if(this.scoreCounterEditDist.get(i) < tempValue) {
				tempValue = this.scoreCounterEditDist.get(i);
				tempPosition = i;
			}
		}
		this.MaxScoreCounter = (int) tempValue;
		this.finalModelED = this.ModelListM.get(tempPosition);
	}
	
	/**
	 * main method to let a user interact with ForestMV for Mealy machines.
	 * @param <I>
	 * @param input input string to be translated to output string
	 * @return translated output string
	 */
	public <I> String OutputMealy(String input) {
		if (this.ModelListM == null) { //check if ForestMV is already built/initialized
			return null;
		}
		String ret = new String(); //init output string
		Character temp; //init character var to save current character of given Mealy machine on current input character position
		int tempindex; //init temp var to get index of most picked char
		int tempval; //init temp var to save current most picked char
		String output = new String();  //init string to save strings outputted by the Mealy machines

		Map<Character, Integer> CharacCounter = new HashMap<>();
		// create hashmap over output sigma that counts how often a given output
		// character was chosen at a given input position
		for (int i = 0; i < this.outputSigma.size(); i++) { 
			CharacCounter.put(this.outputSigma.getSymbol(i), 0);
		}

		//computing all output words beforehand and saving them in a List to save computation time
		List<String> outputStrings = new ArrayList<String>();
		for (int i = 0; i < ModelListM.size(); i++) {
			output = HelperFunctions.WordToStringIT(ModelListM.get(i).computeOutput(Word.fromString(input)));
			outputStrings.add(output);
		}

		// iteration across input string
		for (int i = 0; i < input.length(); i++) {
			// initialisation of the hashmap with 0 on every output char
			if (i != 0) {
				for (int j = 0; j < CharacCounter.size(); j++) {
					CharacCounter.replace(this.outputSigma.getSymbol(j), 0);
				}
			}
			// filling of the hashmap by iterating across the list of produced output words
			for (int j = 0; j < outputStrings.size(); j++) {
				//if a Mealy machine did not get trained on certain parts of the alphabet it breaks (terminates after a learned prefix), 
				//therefore a string may be
				//shorter than the input string => if a produced output string has ended, it gets removed from the list to circumvent null exceptions/
				//do not let it influence the output anymore.
				if (outputStrings.get(j).length() - 1 < i) { 
					outputStrings.remove(j);
				} else {
					temp = outputStrings.get(j).charAt(i);
					CharacCounter.replace(temp, CharacCounter.get(temp) + 1); //increment found character in hashmap
				}
			}

			// finding the character in hashmap, with the highest occurence
			tempindex = 0;
			tempval = CharacCounter.get(this.outputSigma.getSymbol(0));
			for (int x = 0; x < CharacCounter.size(); x++) {
				if (CharacCounter.get(this.outputSigma.getSymbol(x)) > tempval) {
					tempval = CharacCounter.get(this.outputSigma.getSymbol(x));
					tempindex = x;
				}
			}
			ret = ret + this.outputSigma.getSymbol(tempindex); //adding found character on return string
		}
		this.lastLength = ret.length(); //check to circumvent possible null exception in HelperFunctions.compareLastOutputSymbol()
		return ret;
	}
}
