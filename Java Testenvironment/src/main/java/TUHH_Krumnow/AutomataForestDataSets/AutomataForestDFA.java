package TUHH_Krumnow.AutomataForestDataSets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.automatalib.automata.fsa.DFA;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

/**
 * unites the ForestCV and ForestMV classes and their respective DFA implementation into 1 class.
 * @author Arne Krumnow
 *
 */
public class AutomataForestDFA {

	//forest parameters
	private Alphabet<Character> alphabet;
	private int numberOfBags;
	private List<List<String>> positiveSets;
	private List<List<String>> negativeSets;
	private List<DFA<?,Character>> ModelList;
	private int bagSizePositive;
	private int bagSizeNegative;
	//cross validation
	private List<Integer> scoreCounter;
	private List<List<String>> CrossTestSetPositive;
	private List<List<String>> CrossTestSetNegative;
	private DFA<?,Character> finalModelCV;
	private int MaxScoreCounterCV;
	
	//evaluation of characteristics:
	private List<Integer> numberCharact;
	private List<Integer> numberOfStates; 
	private List<Integer> numberOfMajorities;
	private List<Boolean> decisionList;
	private int cvPosition;

	//threading resources:
	ExecutorService executorService;
	private int cores;
	
	public AutomataForestDFA(Alphabet<Character> _alphabet, List<String> positiveSet, List<String> negativeSet, double ratio,
			int numBags, int cores) throws InterruptedException {
		this.alphabet = _alphabet;
		this.cores = cores;
		DetermineNumberOfBags(numBags);
		this.bagSizePositive = DetermineBagSize(positiveSet.size(), ratio);
		this.bagSizeNegative = DetermineBagSize(negativeSet.size(), ratio);
		FillBaggingSetsAndTestSets(positiveSet, negativeSet);
		trainingOfTheForest();
		this.numberOfMajorities = new ArrayList<Integer>();
		for(int i = 0; i < this.numberOfBags; i++) {
			numberOfMajorities.add(0);
		}
	}
	
	public AutomataForestDFA(Alphabet<Character> _alphabet, List<String> positiveSet, List<String> negativeSet, double ratio,
			int numBags, String methodName, int cores) {
		this.alphabet = _alphabet;
		this.cores = cores;
		DetermineNumberOfBags(numBags);
		this.bagSizePositive = DetermineBagSize(positiveSet.size(), ratio);
		this.bagSizeNegative = DetermineBagSize(negativeSet.size(), ratio);
		FillBaggingSetsAndTestSets(positiveSet, negativeSet);
		trainingOfTheForest(methodName);
	}
	
	/**
	 * method to select numberOfBags as uneven to force unambiguous behaviour.
	 * @param numBags
	 */
	private void DetermineNumberOfBags(int numBags) {
		int temp = numBags;
		if (temp % 2 == 0) {
			temp = temp + 1;
		}
		this.numberOfBags = temp;
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

	private <T> void threadTraining(List<T> modelList, Alphabet<Character> sigma, List<List<String>> posList, List<List<String>> negList, int cores) throws InterruptedException {
	    executorService = Executors.newFixedThreadPool(cores); // Limit thread pool size
		for(int i = 0; i < this.numberOfBags; i++){
			Callable computeModel = new HelperForestThreadsDFA(sigma,posList.get(i),negList.get(i));
			int index = i;
			executorService.submit(() -> {
				try{
					T result = (T) computeModel.call();
					synchronized (modelList){
						modelList.set(index,result);
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			});
		}
		executorService.shutdown();
		executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MICROSECONDS);
	}

	/**
	 * training of the prepared instances with the RPNI algorithm for DFAs
	 */
	private void trainingOfTheForest() throws InterruptedException {
		this.ModelList = new ArrayList<DFA<?,Character>>();
		for(int i = 0;i<this.numberOfBags;i++) {
			this.ModelList.add(null);
		}
		threadTraining(ModelList,alphabet,positiveSets,negativeSets,this.cores);
	}
	
	private void trainingOfTheForest(String methodName) {
		this.ModelList = new ArrayList<DFA<?,Character>>();
		if(Objects.equals(methodName, "EDSM")) {
			for(int i = 0;i<this.numberOfBags;i++) {
				this.ModelList.add(HelperFunctions.computeModelBlueFringeEDSM
						(this.alphabet, 
						HelperFunctions.transformFromListToCollection(this.positiveSets.get(i)),
						HelperFunctions.transformFromListToCollection(this.negativeSets.get(i))));
			}
		}else {
			for(int i = 0;i<this.numberOfBags;i++) {
				this.ModelList.add(HelperFunctions.computeModelBlueFringe
						(this.alphabet, 
						HelperFunctions.transformFromListToCollection(this.positiveSets.get(i)),
						HelperFunctions.transformFromListToCollection(this.negativeSets.get(i))));
			}
		}
	}
	
	/**
	 * getter for DFA chosen by Cross validation 
	 * @return DFA
	 */
	public DFA<?,Character> getOutputCV(){
		ScoreBoardEvaluationOfTheForest();
		FindFinalModel();	
		return finalModelCV;
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
		this.MaxScoreCounterCV = tempValue;
		this.finalModelCV = this.ModelList.get(tempPosition);
		this.cvPosition = tempPosition;
	}
	
	/**
	 * main method to let a user interact with ForestMV for DFA.
	 * @param <I>
	 * @param input as a string on which the forest takes a majority vote if the string belongs to the target language.
	 * @return Bool if the input string belongs to the language of the forest.
	 */
	public <I> Boolean accepts(String input) {
		if (this.ModelList == null) { //check if ForestMV is already trained/initialized
			return null;
		}

		int temp = 0; //init checkCounter

		for (int i = 0; i < this.ModelList.size(); i++) {
			if (ModelList.get(i).accepts(Word.fromString(input))) {
				temp = temp + 1; //increment if input element of ForestMV's language

			} else {
				temp = temp - 1; //decrement if input is not element of ForestMV's language

			}
		}
		if (temp == 0) { //due to choosing numberOfBags uneven, temp can only be 0 if some non deterministic behaviour has occured
			throw new IllegalArgumentException(
					"Error: no clear decision could be made");
		}

		if (temp < 0) { //return based on sign of temp
			return false;
		} else {
			return true;
		}
	}
	
	public <I> Boolean acceptsCounter(String input) {
		if (this.ModelList == null) { //check if ForestMV is already trained/initialized
			return null;
		}
		this.decisionList = new ArrayList<Boolean>();
		
		int temp = 0; //init checkCounter

		for (int i = 0; i < this.ModelList.size(); i++) {
			if (ModelList.get(i).accepts(Word.fromString(input))) {
				temp = temp + 1; //increment if input element of ForestMV's language
				this.decisionList.add(true);
			} else {
				temp = temp - 1; //decrement if input is not element of ForestMV's language
				this.decisionList.add(false);
			}
		}
		if (temp == 0) { //due to choosing numberOfBags uneven, temp can only be 0 if some non deterministic behaviour has occured
			throw new IllegalArgumentException(
					"Error: no clear decision could be made");
		}

		if (temp < 0) { //return based on sign of temp
			setMajorityList(false);
			return false;
		} else {
			setMajorityList(true);
			return true;
		}
	}
	
	private void setMajorityList(Boolean result) {
		for(int i = 0; i < ModelList.size(); i++) {
			if(decisionList.get(i) == result) {
				numberOfMajorities.set(i, numberOfMajorities.get(i) + 1);
			}
		}
	}
	
	public List<Double> getNumberOfMajorities(int testSize){
		List<Double> ret = new ArrayList<Double>();
		for(int i = 0; i < numberOfMajorities.size(); i++) {
			ret.add((double)this.numberOfMajorities.get(i)/(double)testSize); //ret has the percentage of times a given automaton
			//was part of a Majority in the voting 
		}
		return ret;
	}
	
	public List<Integer> getNumberOfCharacteristics(){
		List<Integer> ret = new ArrayList<Integer>();
		for(int i = 0; i < ModelList.size(); i++) {
			CharacteristicSetDFA temp = new CharacteristicSetDFA(this.ModelList.get(i), this.alphabet);
			ret.add(temp.getNumberOfCharact());
		}
		return ret;
	}

	public List<Integer> getDepth(){
		List<Integer> ret = new ArrayList<Integer>();
		for(int i = 0; i < ModelList.size(); i++) {
			CharacteristicSetDFA temp = new CharacteristicSetDFA(this.ModelList.get(i), this.alphabet);
			ret.add(temp.getDepth());
		}
		return ret;
	}
	
	public List<Integer> getNumberOfStates(){
		List<Integer> ret = new ArrayList<Integer>();
		for(int i = 0; i < this.ModelList.size(); i++) {
			ret.add(this.ModelList.get(i).size());
		}
		this.numberOfStates = ret;
		return ret;
	}
	
	public Integer getCVPosition() {
		return cvPosition;
	}

	public Integer getNumBags(){
		return ModelList.size();
	}
	
}
