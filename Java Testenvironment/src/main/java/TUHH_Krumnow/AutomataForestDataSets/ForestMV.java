package TUHH_Krumnow.AutomataForestDataSets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import net.automatalib.automata.fsa.DFA;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;



/**
 * ForestMV is an implementation of an automata Forest. It is a different way to use the RPNI algorithm to generate
 * automaton-like behaviour given strings element of a target regular language and strings not element of the target language (case DFA) and 
 * input strings with their corresponding output strings (case Mealy machine). The forest uses "majority voting" to choose an 
 * appropriate output. ForestMV stays intact as one data structure and takes as input a string and outputs a boolean (case DFA)
 * or a string (case Mealy machine). <br><br>
 * 
 * General Approach: each inner RPNI gets different training sets picked randomly to build each automata inside the forest, 
 * where the amount of the input dataset given is determined by parameters "bagSizePositive", "bagSizeNegative" (DFA) and 
 * "bagSizeMealy" (Mealy machine) and is less than the input dataset. The amount 
 * of built DFA's gets determined by "numberOfBags", where "numberOfBags" is chosen to always be uneven in the case of DFA to 
 * force an unambiguous decision. <br><br>
 * 
 * Approach DFA: after the forest is built and each automata, using the RPNI, got trained the forest waits for input words/strings.
 * At input the string gets fed to all inner automata and the resulting output gets marked in a majority check. Due to having uneven
 * automata, after getting in all results, a distinct decision can be made if the input word belongs to the target language (output==true)
 * or not (output==false). <br><br>
 * 
 *  Approach Mealy: at input of a string ForestMV iterates characterwise across all automata and marks the most picked character as the 
 *  output character of the forest at the given character position. The resulting string containing all marked characters at their 
 *  respecting positions is the output string of ForestMV for a given input string.
 * @author Arne Krumnow
 *
 */
public class ForestMV {

	// forest parameters
	private Alphabet<Character> alphabet;
	private int numberOfBags;
	public int lastLength = 0;
	///DFA
	private List<List<String>> positiveSets;
	private List<List<String>> negativeSets;
	private List<DFA<?, Character>> ModelList;
	private int bagSizePositive;
	private int bagSizeNegative;
	///Mealy machine
	private List<List<String>> inputSet;
	private List<List<String>> outputSet;
	private List<MealyMachine<?, Character, ?, Character>> ModelListM;
	private int bagSizeMealy;
	private Alphabet<Character> outputSigma;


	/**
	 * Constructor for ForestMV for DFAs
	 * @param _alphabet as the alphabet of the to be learned DFA; where _alphabet must include all characters inside the training set.
	 * @param positiveSet as the list of strings used for positive inference , i.e., strings that are element of the target language.
	 * @param negativeSet as the list of strings used for negative inference , i.e., strings that are not element of the target language.
	 * @param ratio as the ratio to determine the amount of strings each RPNI algorithm inside ForestMV gets. This calculates 
	 * by multiplying the ratio times the size of the initial training set.
	 * @param numBags as the number of automata to be built inside the forest.
	 */
	public ForestMV(Alphabet<Character> _alphabet, List<String> positiveSet, List<String> negativeSet, double ratio,
			int numBags) {
		this.alphabet = _alphabet;
		DetermineNumberOfBags(numBags);
		this.bagSizePositive = DetermineBagSize(positiveSet.size(), ratio);
		this.bagSizeNegative = DetermineBagSize(negativeSet.size(), ratio);
		FillBaggingSetsAndTestSets(positiveSet, negativeSet);
		trainingOfTheForest();
	}

	/**
	 * Constructor for ForestMV for Mealy machines
	 * @param _alphabet as the alphabet of the to be learned DFA; where _alphabet must include all characters inside the training set.
	 * @param _outputSigma as the expected alphabet that gets produced for the output strings of the target automaton. _outputSigma is needed to set boundaries of ForestMV in which to choose characters
	 * @param inputSet as the training set a List of strings representing the input strings for a Mealy machine. 
	 * @param outputSet as the training set a List of strings representing the corresponding output strings to the given input strings.
	 * @param ratio as the ratio to determine the amount of strings each RPNI algorithm inside ForestMV gets. This calculates 
	 * by multiplying the ratio times the size of the initial training set.
	 * @param numBags as the number of automata to be built inside the forest.
	 * @param isMealy as a boolean decider to switch from DFA to Mealy machines, where true equals ForestMV for Mealy machines and false defaults to build ForestMV for DFA.
	 */
	public ForestMV(Alphabet<Character> _alphabet, Alphabet<Character> _outputSigma, List<String> inputSet,
			List<String> outputSet, double ratio, int numBags, Boolean isMealy) {
		if (inputSet.size() != outputSet.size()) {
			throw new IllegalArgumentException("input and output must be the same size!");
		}
		if (isMealy) {
			this.alphabet = _alphabet;
			this.outputSigma = _outputSigma;
			DetermineNumberOfBags(numBags);
			this.bagSizeMealy = DetermineBagSize(inputSet.size(), ratio);
			FillBaggingSetsAndTestSetsM(inputSet, outputSet);
			trainingOfTheForestM();
		} else {
			new ForestMV(_alphabet, inputSet, outputSet, ratio, numBags);
		}
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
	 * method for ForestMV for DFA which assigns every RPNI algorithm their fraction of the original training set to be used for training
	 * @param inputSetPos initial positive input set as given in the constructor
	 * @param inputSetNeg initial negative input set as given in the constructor
	 */
	private void FillBaggingSetsAndTestSets(List<String> inputSetPos, List<String> inputSetNeg) {
		this.positiveSets = new ArrayList<List<String>>();
		this.negativeSets = new ArrayList<List<String>>();
		for (int i = 0; i < this.numberOfBags; i++) { // filling every bag
			this.positiveSets.add(new ArrayList<String>());
			this.negativeSets.add(new ArrayList<String>());
			this.positiveSets.get(i).addAll(HelperFunctions.getRandomFractionOfList(inputSetPos, this.bagSizePositive));
			this.negativeSets.get(i).addAll(HelperFunctions.getRandomFractionOfList(inputSetNeg, this.bagSizeNegative));
		}
	}

	/**
	 * method for ForestMV for Mealy machines which assigns every RPNI algorithm their fraction of the original training set to be used for training
	 * @param _inputSet initial set of input strings as given in the constructor
	 * @param _outputSet corresponding initial set of output strings as given in the constructor
	 */
	private void FillBaggingSetsAndTestSetsM(List<String> _inputSet, List<String> _outputSet) {
		this.inputSet = new ArrayList<List<String>>();
		this.outputSet = new ArrayList<List<String>>();
		List<List<String>> temp;
		for (int i = 0; i < this.numberOfBags; i++) { // filling every bag
			this.inputSet.add(new ArrayList<String>());
			this.outputSet.add(new ArrayList<String>());
			temp = new ArrayList<List<String>>();
			temp = HelperFunctions.getRandomFractionOfTwoLists(_inputSet, _outputSet, this.bagSizeMealy);
			this.inputSet.get(i).addAll(temp.get(0));
			this.outputSet.get(i).addAll(temp.get(1));
		}
	}

	/**
	 * method to train every DFA inside the forest
	 */
	private void trainingOfTheForest() {
		this.ModelList = new ArrayList<DFA<?, Character>>();
		for (int i = 0; i < this.numberOfBags; i++) {
			this.ModelList.add(HelperFunctions.computeModelBlueFringe(this.alphabet,
					HelperFunctions.transformFromListToCollection(this.positiveSets.get(i)),
					HelperFunctions.transformFromListToCollection(this.negativeSets.get(i))));
		}
	}

	/** 
	 * method to train every Mealy machine inside the forest
	 */
	private void trainingOfTheForestM() {
		this.ModelListM = new ArrayList<MealyMachine<?, Character, ?, Character>>();
		for (int i = 0; i < this.numberOfBags; i++) {
			this.ModelListM.add(HelperFunctions.computeModelMealyBlueFringe(this.alphabet, this.inputSet.get(i),
					this.outputSet.get(i)));
		}
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

	/**
	 * method to analyse certain behaviour inside ForestMV for DFA
	 * @return average number of produced states within ForestMV
	 */
	public int getNumberOfStates() {
		int temp = 0;
		for (int i = 0; i < this.numberOfBags; i++) {
			temp = temp + this.ModelList.get(i).getStates().size();
		}
		return (int) temp / this.numberOfBags;
	}
}
