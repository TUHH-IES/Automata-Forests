package TUHH.AutomataForestDataSets;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import de.learnlib.algorithms.rpni.BlueFringeEDSMDFA;
import de.learnlib.algorithms.rpni.BlueFringeRPNIDFA;
import de.learnlib.algorithms.rpni.BlueFringeRPNIMealy;
import de.learnlib.api.algorithm.PassiveLearningAlgorithm.PassiveDFALearner;
import de.learnlib.api.algorithm.PassiveLearningAlgorithm.PassiveMealyLearner;
import net.automatalib.automata.fsa.DFA;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

/**
 * static class that helps at implementing and testing the classes of automata
 * forests ForestCV and ForestMV. This class is supposed to be the common
 * denominator of theses cases when it comes to shared methods.
 * 
 * @author Arne Krumnow
 *
 */
public final class HelperFunctions {

	private static Long seed; // seed that is used for every randomization outside of the inner RPNI to help
								// with reproducibility
	private static Long dataseed; // seed that is used for every randomization considering the generation of the
									// dataset in the rising trainset, to check
	// beforehand how many strings are needed for the testrun .

	private HelperFunctions() {
	} // hidden constructor for static behaviour

	/**
	 * Splits input and output list for ForestCV into parts which contain the
	 * training set and the Cross validation test set. Same indexes in obtained
	 * lists for inputs correspond to the output words in the output lists for
	 * training and CV.
	 * 
	 * @param Input  as the list of input words to be splitted
	 * @param Output as the corresponding list of output words to the input words
	 * @param size   as the size of the training set obtained from the input list
	 * @return as a list of list of strings, where the first index (0) corresponds
	 *         to the training set of input words and 1 to the CV test set of input
	 *         words, 2 to the output words for training and 3 to the output words
	 *         for CV
	 */
	public static List<List<String>> getCrossFractionOfTwoLists(List<String> Input, List<String> Output, int size) {
		if (size > Input.size()) {
			throw new IllegalArgumentException("Input is smaller than size");
		}
		List<List<String>> ret = new ArrayList<List<String>>();
		List<String> OutputTrain = new ArrayList<String>();
		List<String> OutputTest = new ArrayList<String>();
		List<String> InputTrain = new ArrayList<String>();
		List<String> InputTest = new ArrayList<String>();
		ret.add(InputTrain);
		ret.add(InputTest);
		ret.add(OutputTrain);
		ret.add(OutputTest);
		int tempit = HelperFunctions.getRandomNumInRange(0, Input.size() - 1);

		for (int i = 0; i < Input.size(); i++) {
			if (tempit >= (Input.size() - 1)) {
				tempit = 0;
			}
			if (i < size) {

				ret.get(0).add(Input.get(tempit));
				ret.get(2).add(Output.get(tempit));
				tempit++;

			} else {
				ret.get(1).add(Input.get(tempit));
				ret.get(3).add(Output.get(tempit));
				tempit++;
			}
		}
		return ret;
	}

	/**
	 * changing a single word to a string using iterators ("IT"). Safe method to
	 * change the word type to a string type. No undetermined behaviour has yet
	 * occured using this method
	 * 
	 * @param word as the input to be transformed
	 * @return the input as a string
	 */
	public static String WordToStringIT(Word<Character> word) {
		StringBuilder builder = new StringBuilder();
		Iterator<Character> it = word.iterator();
		while (it.hasNext()) {
			builder.append(it.next());
		}
		return builder.toString();
	}

	/**
	 * method to call the standard RPNI algorithm for Mealy machines using List of
	 * strings
	 * 
	 * @param <I>
	 * @param alphabet     as the alphabet for the inputs of the returned Mealy
	 *                     machine
	 * @param inputSample  as the training sample of input words
	 * @param outputSample as the training sample of corresponding output words to
	 *                     the input sample
	 * @return learned Mealy machine
	 */
	@SuppressWarnings("unchecked")
	public static <I> MealyMachine<?, I, ?, I> computeModelMealyBlueFringe(Alphabet<I> alphabet,
			List<String> inputSample, List<String> outputSample) {
		final PassiveMealyLearner<I, I> learner = new BlueFringeRPNIMealy<>(alphabet);
		for (int i = 0; i < inputSample.size(); i++) {
			learner.addSample((Word<I>) Word.fromString(inputSample.get(i)),
					(Word<I>) Word.fromString(outputSample.get(i)));
		}
		return learner.computeModel();
	}

	/**
	 * Learns a DFA from input words and their acceptance labels using LearnLib's
	 * Blue-Fringe RPNI implementation.
	 *
	 * @param alphabet input alphabet
	 * @param inputSample sample words
	 * @param acceptanceSample whether each corresponding word is accepted
	 * @return learned DFA
	 */
	@SuppressWarnings("unchecked")
	public static <I> DFA<?, I> computeModelDFABlueFringe(Alphabet<I> alphabet,
			List<String> inputSample, List<Boolean> acceptanceSample) {
		if (inputSample.size() != acceptanceSample.size()) {
			throw new IllegalArgumentException("input and acceptance samples must be the same size");
		}
		if (acceptanceSample.contains(null)) {
			throw new IllegalArgumentException("acceptance labels must not be null");
		}
		final PassiveDFALearner<I> learner = new BlueFringeRPNIDFA<>(alphabet);
		for (int i = 0; i < inputSample.size(); i++) {
			learner.addSample((Word<I>) Word.fromString(inputSample.get(i)), acceptanceSample.get(i));
		}
		return learner.computeModel();
	}

	/**
	 * Learns a DFA using LearnLib's Blue-Fringe EDSM implementation.
	 */
	@SuppressWarnings("unchecked")
	public static <I> DFA<?, I> computeModelDFABlueFringeEDSM(Alphabet<I> alphabet,
			List<String> inputSample, List<Boolean> acceptanceSample) {
		if (inputSample.size() != acceptanceSample.size()) {
			throw new IllegalArgumentException("input and acceptance samples must be the same size");
		}
		if (acceptanceSample.contains(null)) {
			throw new IllegalArgumentException("acceptance labels must not be null");
		}
		final BlueFringeEDSMDFA<I> learner = new BlueFringeEDSMDFA<>(alphabet);
		for (int i = 0; i < inputSample.size(); i++) {
			learner.addSample((Word<I>) Word.fromString(inputSample.get(i)), acceptanceSample.get(i));
		}
		return learner.computeModel();
	}

	/**
	 * Function to extract some random number in an interval. Due to this work being
	 * of academic purposes, this method is seeded for reproducibility and is used
	 * in all methods that need a random number.
	 * 
	 * @param min as the lower limit of the interval
	 * @param max as the upper limit of the interval
	 * @return random number in interval including the boundaries
	 */
	public static int getRandomNumInRange(int min, int max) {

		if (min > max) {
			throw new IllegalArgumentException("max must not be less than min");
		}
		if (seed == null) {
			seed = (long) 1;
		}
		Random r = new Random(seed);
		seed++;
		return r.nextInt((max - min) + 1) + min;
	}

	public static int getRandomNumInRange(int min, int max, boolean isData) {
		if (!isData) {
			return getRandomNumInRange(min, max);
		}

		if (min > max) {
			throw new IllegalArgumentException("max must not be less than min");
		}
		if (dataseed == null) {
			dataseed = (long) 1;
		}
		Random r = new Random(dataseed);
		dataseed++;
		return r.nextInt((max - min) + 1) + min;
	}

	/**
	 * compares a Mealy machine with a test set to check how many words are
	 * correctly translated
	 * 
	 * @param model      as the Mealy machine to be analysed
	 * @param inputTest  as the input words given to the model, generating output
	 *                   words from the model
	 * @param outputTest as the list of output words from the target/correct Mealy
	 *                   machine. Words are correctly translated if the obtained
	 *                   words from model are exact to this list
	 * @return as the number of correct translated words
	 */
	public static int CompareWithModelWithTestSet(MealyMachine<?, Character, ?, Character> model,
			List<String> inputTest, List<String> outputTest) {
		int score = 0;

		for (int i = 0; i < inputTest.size(); i++) {
			if (Objects.equals(HelperFunctions.WordToStringIT(model.computeOutput(Word.fromString(inputTest.get(i)))),
					outputTest.get(i))) {
				score++;
			}
		}

		return score;
	}

	/**
	 * Compares a DFA with labeled test words.
	 *
	 * @return number of words classified correctly
	 */
	public static int CompareWithModelWithTestSet(DFA<?, Character> model,
			List<String> inputTest, List<Boolean> acceptanceTest) {
		if (inputTest.size() != acceptanceTest.size()) {
			throw new IllegalArgumentException("input and acceptance test sets must be the same size");
		}
		if (acceptanceTest.contains(null)) {
			throw new IllegalArgumentException("acceptance labels must not be null");
		}
		int score = 0;
		for (int i = 0; i < inputTest.size(); i++) {
			if (model.accepts(Word.fromString(inputTest.get(i))) == acceptanceTest.get(i)) {
				score++;
			}
		}
		return score;
	}

	/**
	 * Compares the majority-vote predictions of a DFA forest with labeled test
	 * words.
	 *
	 * @return number of words classified correctly
	 */
	public static int CompareWithModelWithTestSet(AutomataForestDFAMachine model,
			List<String> inputTest, List<Boolean> acceptanceTest) {
		if (inputTest.size() != acceptanceTest.size()) {
			throw new IllegalArgumentException("input and acceptance test sets must be the same size");
		}
		if (acceptanceTest.contains(null)) {
			throw new IllegalArgumentException("acceptance labels must not be null");
		}
		int score = 0;
		for (int i = 0; i < inputTest.size(); i++) {
			if (model.accepts(inputTest.get(i)) == acceptanceTest.get(i)) {
				score++;
			}
		}
		return score;
	}

	/**
	 * compares a AutomataForestMealyMachine with a test set to check how many words
	 * are correctly translated using MV
	 * 
	 * @param model      as the ForestMV with Mealy machines to be analysed
	 * @param inputTest  as the input words given to the model, generating output
	 *                   words from the model
	 * @param outputTest as the list of output words from the target/correct Mealy
	 *                   machine. Words are correctly translated if the obtained
	 *                   words from model are exact to this list
	 * @return as the number of correct translated words
	 */
	public static int CompareWithModelWithTestSet(AutomataForestMealyMachine model, List<String> inputTest,
			List<String> outputTest) {
		int score = 0;
		for (int i = 0; i < inputTest.size(); i++) {
			if (Objects.equals(model.OutputMealy(inputTest.get(i)), outputTest.get(i))) {
				score++;
			}
		}
		return score;
	}

	/**
	 * method used to generate input strings for training and testing of DFA and
	 * Mealy machines. The outputs are obtained by giving these generated strings to
	 * an oracle/target DFA/Mealy machine. It is supposed to mimic sampling real
	 * world data.
	 * 
	 * @param sigma        as the alphabet on which a string is generated
	 * @param stringlength as the length of the return string
	 * @return randomly generated string
	 */
	public static String RandomStringFromAplhabet(Alphabet<Character> sigma, int stringlength) {
		List<Character> sigmaList = new ArrayList<Character>();

		for (int i = 0; i < sigma.size(); i++) {

			sigmaList.add(sigma.getSymbol(i));
		}

		String ret = new String();
		int temp;
		for (int i = 0; i < stringlength; i++) {
			temp = HelperFunctions.getRandomNumInRange(0, sigmaList.size() - 1);
			ret = ret + sigmaList.get(temp);
		}
		return ret;
	}

	/**
	 * Hamming Distance as implemented per definition from Wikipedia => only for
	 * strings of same length only checks substitutions. Used to analyse how "close"
	 * generated strings from ForestCV/ForestMV are to the target Mealy machine.
	 * 
	 * @param str1 as the input string from a Mealy machine
	 * @param str2 as the comparison case from the target Mealy machine
	 * @return Hamming Distance of the two strings
	 */
	public static int EditDistanceHamming(String str1, String str2) {
		int ret = str1.length();

		for (int i = 0; i < str1.length(); i++) {

			if (str1.charAt(i) == str2.charAt(i)) {
				ret -= 1;
			}
		}
		return ret + Math.abs(str1.length() - str2.length());
	}

	/**
	 * comparison method for Mealy machines. It calculates the average Hamming
	 * Distance of a Mealy machine, given some input test strings and comparing to
	 * some output strings sampled by the target Mealy machine.
	 * 
	 * @param model as the Mealy machine to be analysed
	 * @param in    as the list of input strings, given to model
	 * @param out   as the correct output strings sampled by the target Mealy
	 *              machine
	 * @return average Hamming Distance of the Model to the target Mealy machine,
	 *         given some input words with their corresponding sampled output words
	 */
	public static double getAverageEditDistanceOfList(MealyMachine<?, Character, ?, Character> model, List<String> in,
			List<String> out) {
		long ret = 0;
		for (int i = 0; i < in.size(); i++) {
			ret += EditDistanceHamming(WordToStringIT(model.computeOutput(Word.fromString(in.get(i)))), out.get(i));
		}
		return (double) (ret) / (double) in.size();
	}


	/**
	 * comparison method for AutomataForestMealyMachine. It calculates the average
	 * Hamming Distance of MV with Mealy machines, given some input test strings and
	 * comparing to some output strings sampled by the target Mealy machine.
	 * 
	 * @param model as the ForestMV with Mealy machines to be analysed
	 * @param in    as the list of input strings, given to model
	 * @param out   as the correct output strings sampled by the target Mealy
	 *              machine
	 * @return average Hamming Distance of ForestMV to the target Mealy machine,
	 *         given some input words with their corresponding sampled output words
	 */
	public static Double getAverageEditDistanceOfList(AutomataForestMealyMachine model, List<String> in,
			List<String> out) {
		long ret = 0;
		for (int i = 0; i < in.size(); i++) {
			ret += EditDistanceHamming(model.OutputMealy(in.get(i)), out.get(i));
		}
		return (double) ret / (double) in.size();
	}

	/**
	 * comparison method to analyse how well a Mealy machine predicts the last
	 * output symbol correctly, i.e., analyse Mealy machines if the last output
	 * symbol is of high significance
	 * 
	 * @param model         as the Mealy machine to be analysed
	 * @param inputStrings  as the input strings given to model
	 * @param outputStrings as the correct output strings, where the last symbol is
	 *                      extracted
	 * @return number of correctly translated last output symbols
	 */
	public static int compareLastOutputSymbols(MealyMachine<?, Character, ?, Character> model,
			List<String> inputStrings, List<String> outputStrings) {
		int ret = 0;
		String temp = new String();
		for (int i = 0; i < inputStrings.size(); i++) {
			temp = HelperFunctions.WordToStringIT(model.computeOutput(Word.fromString(inputStrings.get(i))));
			if (temp.length() == outputStrings.get(i).length()) {
				if (temp.charAt(temp.length() - 1) == outputStrings.get(i).charAt(temp.length() - 1)) {
					ret++;
				}
			}
		}
		return ret;
	}

	/**
	 * comparison method to analyse how well a AutomataForestMealyMachine using MV
	 * predicts the last output symbol correctly, i.e., analyse ForestMV if the last
	 * output symbol is of high significance
	 * 
	 * @param model         as the ForestMV with Mealy machines to be analysed
	 * @param inputStrings  as the input strings given to model
	 * @param outputStrings as the correct output strings, where the last symbol is
	 *                      extracted
	 * @return number of correctly translated last output symbols
	 */
	public static int compareLastOutputSymbols(AutomataForestMealyMachine model, List<String> inputStrings,
			List<String> outputStrings) {
		int ret = 0;
		for (int i = 0; i < inputStrings.size(); i++) {
			if (model.OutputMealy(inputStrings.get(i)).charAt(model.lastLength - 1) == outputStrings.get(i)
					.charAt(outputStrings.get(i).length() - 1) && model.lastLength == outputStrings.get(i).length()) {
				ret++;
			}
		}
		return ret;
	}

	/**
	 * getter for the local seed in "HelperFunctions.java" (making it reachable for
	 * outside classes). It additionally increments the inner seed to circumvent
	 * continuous repition of the seed, which may otherwise lead to endless loops.
	 * 
	 * @return inner seed of the class
	 */
	public static Long getSeed() {
		if (seed == null) {
			seed = (long) 1;
		}
		seed++;
		return seed;
	}

}
