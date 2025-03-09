package TUHH_Krumnow.AutomataForestDataSets;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.TreeSet;

import de.learnlib.algorithms.rpni.BlueFringeEDSMDFA;
import de.learnlib.algorithms.rpni.BlueFringeMDLDFA;
import de.learnlib.algorithms.rpni.BlueFringeRPNIDFA;
import de.learnlib.algorithms.rpni.BlueFringeRPNIMealy;
import de.learnlib.api.algorithm.PassiveLearningAlgorithm.PassiveDFALearner;
import de.learnlib.api.algorithm.PassiveLearningAlgorithm.PassiveMealyLearner;
import de.learnlib.datastructure.pta.pta.BasePTA;
import de.learnlib.datastructure.pta.pta.BlueFringePTA;
import net.automatalib.automata.fsa.DFA;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

/**
 * static class that helps at implementing and testing the classes of this project. 
 * @author Arne Krumnow
 *
 */
public final class HelperFunctions {
	
	private static Long seed; //seed that is used for every randomization outside of the inner RPNI to help with reproducibility
	private static Long dataseed; //seed that is used for every randomization considering the generation of the dataset in the rising trainset, to check
	//beforehand how many strings are needed for the testrun .
	
	private HelperFunctions() {} //hidden constructor for static behaviour
	
	/**
	 * splits a given list at random and returns the obtained sublist
	 * @param Input as an initial List
	 * @param ratio as the ratio on how big the sublist is gonna be
	 * @return sublist of Input
	 */
	public static List<String> getRandomFractionOfList(List<String> Input, double ratio){
		List<String> Output = new ArrayList<String>();
		int temp = HelperFunctions.getRandomNumInRange(0, Input.size()-1);
		int size = (int) (Input.size() * ratio);
		for(int i = 0;i< size;i++) {
			if(temp>(Input.size()-1)) { //dealing with wraparound
				temp = 0;
			}
			Output.add(Input.get(temp));
			temp++;
		}
		return Output;
	}
	
	/**
	 * splits a given list at random and returns the obtained sublist
	 * @param Input as an initial List
	 * @param size as the size of the returned sublist
	 * @return sublist of Input
	 */
	public static List<String> getRandomFractionOfList(List<String> Input, int size){
		if(size>Input.size()) {
			throw new IllegalArgumentException("Input is smaller than size");
		}
		List<String> Output = new ArrayList<String>();
		int temp = HelperFunctions.getRandomNumInRange(0, Input.size()-1);
		for(int i = 0;i< size;i++) {
			if(temp>=(Input.size()-1)) {
				temp = 0;
			}
			Output.add(Input.get(temp));
			temp++;
		}
		return Output;
	}
	
	/**
	 * splits a given list at random and returns the obtained sublist, where the returned list has no element inside comp list
	 * @param Input as an initial List
	 * @param comp as a comparison list, where the returned sublist must be mutually exclusive from comp
	 * @param size as the size of the returned sublist
	 * @return sublist of Input, mutually exclusive from comp
	 */
	public static List<String> getRandomExclusiveFractionOfList(List<String> Input, List<String> comp, int size){
		if(size>Input.size()) {
			throw new IllegalArgumentException("Input is smaller than size");
		}
		List<String> Output = new ArrayList<String>();
		int temp = HelperFunctions.getRandomNumInRange(0, Input.size()-1);
		int availability=0; //counter to check if a new mutually exclusive string can be found
		for(int i = 0;i< size;i++) {
			if(temp>=(Input.size()-1)) { //dealing with wraparound
				temp = 0;
			}
			if(availability==Input.size()) { //no new mutually exclusive string can be found inside Input
				throw new IllegalArgumentException("couldn't find enough unique elements. Try lowering param. size!");
			}
			if(!comp.contains(Input.get(temp))) {
				Output.add(Input.get(temp)); //add current element to return list
				temp++;
			}else {
				temp++; //current element cannot be added to return list
				i--;
			}
			availability++; //increment check variable
		}
		return Output;
	}
	
	/**
	 * splits the given lists at random and returns the obtained sublists, where both input lists are mapped together, 
	 * s.t. any index i at Input corresponds to index i at Output (method used for Mealy machines)
	 * @param Input as a given list of Input words
	 * @param Output as the corresponding list of Output words
	 * @param size as the size of the returned sublist
	 * @return a List of List of strings, where the outer list has 2 entries where index 0 is the sublist from Input and
	 * index 1 as the sublist from Output 
	 */
	public static List<List<String>> getRandomFractionOfTwoLists(List<String> Input,List<String> Output, int size){
		if(size>Input.size()) {
			throw new IllegalArgumentException("Input is smaller than size");
		}
		List<List<String>> temp = new ArrayList<List<String>>();
		temp.add(new ArrayList<String>());
		temp.add(new ArrayList<String>());
		int tempit = HelperFunctions.getRandomNumInRange(0, Input.size()-1);
		for(int i = 0;i< size;i++) {
			if(tempit>=(Input.size()-1)) {
				tempit = 0;
			}
			temp.get(0).add(Input.get(tempit));
			temp.get(1).add(Output.get(tempit));
			tempit++;
		}
		return temp;
	}
	
	/**
	 * splits the input list and returns a sublist, where all elements in the input list get removed from it, s.t. 
	 * both input and output are mutually exclusive
	 * @param input as the input list to be splitted
	 * @param splitLength as the size of the sublist
	 * @return sublist mutually exclusive from the input list
	 */
	public static List<String> splitListRandom(List<String> input, int splitLength){
		List<String> ret = new ArrayList<>();
		
		for(int i = 0; i < splitLength; i++) {
			int rand = HelperFunctions.getRandomNumInRange(0,input.size()-1);
			ret.add(input.get(rand));
			input.remove(rand);
		}
		return ret;
	}
	
	
	/**
	 * Splits input list for ForestCV into parts which contain the training set and the Cross validation test set
	 * @param Input as the list to be splitted into parts
	 * @param size as the size of the training set obtained from the input list
	 * @return as a list of list of strings, where the first index (0) corresponds to the training set and the second (1) to the CV test set
	 */
	public static List<List<String>> getCrossFractionOfList(List<String> Input, int size){
		if(size>Input.size()) {
			throw new IllegalArgumentException("Input is smaller than size");
		}
		List<List<String>> ret = new ArrayList<List<String>>();
		ret.add(new ArrayList<String>()); //training set
		ret.add(new ArrayList<String>()); //cv/test set
		
		List<String> temp = new ArrayList<String>(Input);
		int sample = 0;
		for(int i = 0; i < size; i++){
			sample = getRandomNumInRange(0,temp.size()-1);
			ret.get(0).add(temp.get(sample));
			temp.remove(sample);
		}
		ret.get(1).addAll(temp);
		return ret;
	}
	
	/**
	 *  Splits input and output list for ForestCV into parts which contain the training set and the Cross validation test set. Same indexes
	 *  in obtained lists for inputs correspond to the output words in the output lists for training and CV.
	 * @param Input as the list of input words to be splitted
	 * @param Output as the corresponding list of output words to the input words
	 * @param size as the size of the training set obtained from the input list 
	 * @return as a list of list of strings, where the first index (0) corresponds to the training set of input words and 1 to the CV test set
	 * of input words, 2 to the output words for training and 3 to the output words for CV
	 */
	public static List<List<String>> getCrossFractionOfTwoLists(List<String> Input,List<String> Output, int size){
		if(size>Input.size()) {
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
		int tempit = HelperFunctions.getRandomNumInRange(0, Input.size()-1);

		for(int i = 0;i< Input.size();i++) {
			if(tempit>=(Input.size()-1)) {
				tempit = 0;
			}
			if(i<size) {
			
				ret.get(0).add(Input.get(tempit));
				ret.get(2).add(Output.get(tempit));
				tempit++;

			}else {
				ret.get(1).add(Input.get(tempit));
				ret.get(3).add(Output.get(tempit));
				tempit++;
			}
		}
		return ret;
	}

	/**
	 * changing an input list of strings to a collection of words, which is used to train the RPNI learner which take Words and not strings as input
	 * @param input as list of strings
	 * @return input transformed to a collection of words
	 */
	public static Collection<Word<Character>> transformFromListToCollection(List<String> input){
		Collection<Word<Character>> returned = new ArrayList<Word<Character>>();
		
		for (int i = 0; i<input.size();i++) {
			returned.add(Word.fromCharSequence(input.get(i)));
		}
		
		return returned;
	}
	
	
	/**
	 * changing a single word to a string. not recommended to use, because the Learnlib method can cause undetermined behaviour.
	 * Better use the method "WordToStringIT"
	 * @param word as the input to be transformed
	 * @return the input as a string
	 */
	public static String WordToString(Word<Character> word) {
		return word.toString();
	}
	
	/**
	 * changing a single word to a string using iterators ("IT"). Safe method to change the word type to a string type.
	 * No undetermined behaviour has yet occured using this method 
	 * @param word as the input to be transformed
	 * @return the input as a string
	 */
	public static String WordToStringIT(Word<Character> word) {
		StringBuilder builder = new StringBuilder();
		Iterator<Character> it = word.iterator();
		while(it.hasNext()) {
			builder.append(it.next());
		}
		return builder.toString();
	}
	
	/**
	 * changing a single string to a word as used by the Learnlib framework
	 * @param str as the input string to be transformed
	 * @return the input in the word datatype
	 */
	public static Word<Character> StringToWord(String str){
		//Word<Character> ret = Word.fromCharSequence("");
		//for(int i = 0 ; i<str.length();i++) {
		//	ret.append(str.charAt(i));
		//}
		return Word.fromCharSequence(str);
		//return ret;
	}
	

	/**
	 * method to call the standard RPNI algorithm for DFA
	 * @param <I>
	 * @param alphabet as the input alphabet of the returned DFA
	 * @param positiveSamples as the words used for positive inference
	 * @param negativeSamples as the words used for negative inference
	 * @return learned DFA
	 */
	public static <I> DFA<?,I> computeModelBlueFringe(Alphabet<I> alphabet, Collection<Word<I>> positiveSamples,Collection<Word<I>> negativeSamples){
		final PassiveDFALearner<I> learner = new BlueFringeRPNIDFA<>(alphabet);
		learner.addPositiveSamples(positiveSamples);
		learner.addNegativeSamples(negativeSamples);
		return learner.computeModel();
	}
	
	/**
	 * method to call the standard RPNI algorithm for Mealy machines using Collection of words
	 * @param <I>
	 * @param alphabet as the alphabet for the inputs of the returned Mealy machine
	 * @param inputSample as the training sample of input words
	 * @param outputSample as the training sample of corresponding output words to the input sample
	 * @return learned Mealy machine
	 */
		@SuppressWarnings("unchecked")
	public static <I> MealyMachine<?, I, ?, I> computeModelMealyBlueFringe(Alphabet<I> alphabet, Collection<Word<I>> inputSample,Collection<Word<I>> outputSample){
		final PassiveMealyLearner<I,I> learner = new BlueFringeRPNIMealy<>(alphabet);
		learner.addSamples((Word<I>) inputSample,outputSample);
		return learner.computeModel();
	}
		

	/**
	 * method to call the standard RPNI algorithm for Mealy machines using List of strings
	 * @param <I>
	 * @param alphabet as the alphabet for the inputs of the returned Mealy machine
	 * @param inputSample as the training sample of input words
	 * @param outputSample as the training sample of corresponding output words to the input sample
	 * @return learned Mealy machine
	 */
				@SuppressWarnings("unchecked")
	public static <I> MealyMachine<?, I, ?, I> computeModelMealyBlueFringe(Alphabet<I> alphabet, List<String> inputSample,List<String> outputSample){
		final PassiveMealyLearner<I,I> learner = new BlueFringeRPNIMealy<>(alphabet);
		for(int i = 0; i < inputSample.size();i++) {
			learner.addSample((Word<I>)Word.fromString(inputSample.get(i)), 
					(Word<I>) Word.fromString(outputSample.get(i)));
		}
		return learner.computeModel();
	}
	
	/**
	 * method to call the RPNI-EDSM algorithm for DFA. Currently not used because of uncertainty why errors occur using this algorithm
	 * @param <I>
	 * @param alphabet as the input alphabet of the returned DFA
	 * @param positiveSamples as the words used for positive inference
	 * @param negativeSamples as the words used for negative inference
	 * @return learned DFA
	 */
	public static <I> DFA<?,I> computeModelBlueFringeEDSM(Alphabet<I> alphabet, Collection<Word<I>> positiveSamples,Collection<Word<I>> negativeSamples){
		final PassiveDFALearner<I> learner = new BlueFringeEDSMDFA<>(alphabet);
		learner.addPositiveSamples(positiveSamples);
		learner.addNegativeSamples(negativeSamples);
		return learner.computeModel();
	}
	
	/**
	 * method to call the RPNI-MDL algorithm for DFA. Currently not used because of uncertainty why errors occur using this algorithm
	 * @param <I>
	 * @param alphabet as the input alphabet of the returned DFA
	 * @param positiveSamples as the words used for positive inference
	 * @param negativeSamples as the words used for negative inference
	 * @return learned DFA
	 */
	public static <I> DFA<?,I> computeModelBlueFringeMDLDFA(Alphabet<I> alphabet, Collection<Word<I>> positiveSamples,Collection<Word<I>> negativeSamples){
		final PassiveDFALearner<I> learner = new BlueFringeMDLDFA<>(alphabet);
		learner.addPositiveSamples(positiveSamples);
		learner.addNegativeSamples(negativeSamples);
		return learner.computeModel();
	}
	
	/**
	 * Function to extract some random number in an interval. Due to this work being of academic purposes, this method is seeded
	 * for reproducibility and is used in all methods that need a random number.  
	 * @param min as the lower limit of the interval
	 * @param max as the upper limit of the interval
	 * @return random number in interval including the boundaries
	 */
	public static int getRandomNumInRange(int min, int max) {
		
		
		if (min > max) { 
			throw new IllegalArgumentException("max must be greater or equal than min");
		}
		if(seed==null) {
			seed=(long) 1;
		}
		Random r = new Random(seed);
		seed++;
		return r.nextInt((max - min) + 1) + min;
	}

	public static int getRandomNumInRange(int min, int max, boolean isData) {
		if(!isData){
			return getRandomNumInRange(min,max);
		}

		if (min > max) {
			throw new IllegalArgumentException("max must be greater or equal than min");
		}
		if(dataseed==null) {
			dataseed=(long) 1;
		}
		Random r = new Random(dataseed);
		dataseed++;
		return r.nextInt((max - min) + 1) + min;
	}
	
	
	/**
	 * splitting method used for testing. Method extracts amount of input ratio from the input list and puts it into return list.
	 * The method is used to obtain mutually exclusive lists for analytical purposes (testing the resulting automata)
	 * @param Input as the input list where a new list is to be extracted from
	 * @param ratio as the percentage amount of strings to be extracted
	 * @return list of strings that are extracted from the input list
	 */
	public static List<String> getTrainingSetAndFormTestSet(List<String> Input, double ratio){
		if(ratio <= 0 || ratio >= 1) {
			throw new IllegalArgumentException("parameter out of bounds");
		}
		List<String> trainingSet = new ArrayList<String>();
		int size = (int) (Input.size()*ratio);
		for(int i = 0;i<size;i++) {
			trainingSet.add(Input.get(0));
			Input.remove(0);
		}
		return trainingSet;
	}
	
	
	

	/**
	 * compares a DFA with a test set to check how many words are correctly classified
	 * @param model as the DFA to be analysed
	 * @param positiveTest as the test set containing words that should be accepted by the DFA
	 * @param negativeTest as the test set containing words that should be rejected by the DFA
	 * @return the amount of correctly classified words
	 */
	public static int CompareWithModelWithTestSet(DFA<?,Character> model, List<String> positiveTest,List<String> negativeTest) {
		int score = 0;
		for(int i = 0; i<positiveTest.size();i++) {
			if(model.accepts(Word.fromString(positiveTest.get(i)))) {
				score = score +1;
			}
		}
		for(int i = 0; i<negativeTest.size();i++) {
			if(false == model.accepts(Word.fromString(negativeTest.get(i)))) {
				score = score +1;
			}
		}
		return score;
	}
	
	/**
	 * compares a DFA with a test set to check how many words are correctly classified and additionaly checks for false positives and false negatives
	 * @param model as the DFA to be analysed
	 * @param positiveTest as the test set containing words that should be accepted by the DFA
	 * @param negativeTest as the test set containing words that should be rejected by the DFA
	 * @return an integer array containing the amount of correctly classified words (index 0), false positive (1), false negative (2)
	 */
	public static int[] CompareWithModelWithTestSetNullH(DFA<?,Character> model, List<String> positiveTest,List<String> negativeTest) {
		int[] score = new int[3];
		score[0] = 0; //normal test score
		score[1] = 0; //false positive
		score[2] = 0; //false negative
		for(int i = 0; i<positiveTest.size();i++) {
			if(model.accepts(Word.fromString(positiveTest.get(i)))) {
				score[0] = score[0] +1; //right classification
			}else {
				score[1] = score[1] + 1; //false positive: reject correct result
			}
		}
		for(int i = 0; i<negativeTest.size();i++) {
			if(false == model.accepts(Word.fromString(negativeTest.get(i)))) {
				score[0] = score[0] +1; //right classification
			}else {
				score[2] = score[2] +1; //false negative: accept false result
			}
		}
		return score;
	}
	
	/**
	 * compares a ForestMV with a test set to check how many words are correctly classified and additionaly checks for false positives and false negatives
	 * @param model as the DFA to be analysed
	 * @param positiveTest as the test set containing words that should be accepted by the DFA
	 * @param negativeTest as the test set containing words that should be rejected by the DFA
	 * @return an integer array containing the amount of correctly classified words (index 0), false positive (1), false negative (2)
	 */
	public static int[] CompareWithModelWithTestSetNullH(ForestMV model, List<String> positiveTest,List<String> negativeTest) {
		int[] score = new int[3];
		score[0] = 0; //normal test score
		score[1] = 0; //false positive
		score[2] = 0; //false negative
		for(int i = 0; i<positiveTest.size();i++) {
			if(model.accepts(positiveTest.get(i))) {
				score[0] = score[0] +1; //right classification
			}else {
				score[1] = score[1] + 1; //false positive: reject correct result
			}
		}
		for(int i = 0; i<negativeTest.size();i++) {
			if(false == model.accepts(negativeTest.get(i))) {
				score[0] = score[0] +1; //right classification
			}else {
				score[2] = score[2] +1; //false negative: accept false result
			}
		}
		return score;
	}
	
	/**
	 * compares a AutomataForestDFA with a test set to check how many words are correctly classified using MV and additionaly checks for false positives and false negatives
	 * @param model as the AutomataForestDFA to be analysed on Majority voting
	 * @param positiveTest as the test set containing words that should be accepted by the DFA
	 * @param negativeTest as the test set containing words that should be rejected by the DFA
	 * @return an integer array containing the amount of correctly classified words (index 0), false positive (1), false negative (2)
	 */
	public static int[] CompareWithModelWithTestSetNullH(AutomataForestDFA model, List<String> positiveTest,List<String> negativeTest) {
		int[] score = new int[3];
		score[0] = 0; //normal test score
		score[1] = 0; //false positive
		score[2] = 0; //false negative
		for(int i = 0; i<positiveTest.size();i++) {
			if(model.accepts(positiveTest.get(i))) {
				score[0] = score[0] +1; //right classification
			}else {
				score[1] = score[1] + 1; //false positive: reject correct result
			}
		}
		for(int i = 0; i<negativeTest.size();i++) {
			if(false == model.accepts(negativeTest.get(i))) {
				score[0] = score[0] +1; //right classification
			}else {
				score[2] = score[2] +1; //false negative: accept false result
			}
		}
		return score;
	}

	/**
	 * compares a Mealy machine with a test set to check how many words are correctly translated
	 * @param model as the Mealy machine to be analysed
	 * @param inputTest as the input words given to the model, generating output words from the model
	 * @param outputTest as the list of output words from the target/correct Mealy machine. Words are correctly translated if
	 * the obtained words from model are exact to this list
	 * @return as the number of correct translated words
	 */
	public static int CompareWithModelWithTestSet(MealyMachine<?,Character,?,Character> model, List<String> inputTest,List<String> outputTest) {
		int score = 0;
		
		for(int i = 0; i < inputTest.size(); i++) {
			if(Objects.equals(HelperFunctions.WordToStringIT(
					model.computeOutput(Word.fromString(inputTest.get(i)))),
					outputTest.get(i))){
				score++;
			}
		}
		
		return score;
	}
	
	/**
	 * compares a ForestMV for DFA with a test set to check how many words are correctly classified
	 * @param model as the ForestMV model to be analysed
	 * @param positiveTest as the test set containing words that should be accepted by the DFA
	 * @param negativeTest as the test set containing words that should be rejected by the DFA
	 * @return the amount of correctly classified words
	 */
	public static int CompareWithModelWithTestSet(ForestMV model, List<String> positiveTest,List<String> negativeTest) {
		int score = 0;
		for(int i = 0; i<positiveTest.size();i++) {
			if(model.accepts(positiveTest.get(i))) {
				score = score +1;
			}
		}
		for(int i = 0; i<negativeTest.size();i++) {
			if(false == model.accepts(negativeTest.get(i))) {
				score = score +1;
			}
		}
		return score;
	}
	
	/**
	 * compares a ForestMV with Mealy machines with a test set to check how many words are correctly translated
	 * @param model as the ForestMV with Mealy machines to be analysed
	 * @param inputTest as the input words given to the model, generating output words from the model
	 * @param outputTest as the list of output words from the target/correct Mealy machine. Words are correctly translated if
	 * the obtained words from model are exact to this list
	 * @return as the number of correct translated words
	 */
	public static int CompareWithModelWithTestSet(ForestMV model, List<String> inputTest,List<String> outputTest, Boolean isMealy) {
		if(!isMealy) {
			return CompareWithModelWithTestSet(model,inputTest,outputTest);
		}
		int score = 0;
		for(int i = 0; i < inputTest.size(); i++) {
			if(Objects.equals(model.OutputMealy(inputTest.get(i)),
					outputTest.get(i))){
				score++;
			}
		}
		return score;
	}
	
	/**
	 * compares a AutomataForestMealyMachine with a test set to check how many words are correctly translated using MV
	 * @param model as the ForestMV with Mealy machines to be analysed
	 * @param inputTest as the input words given to the model, generating output words from the model
	 * @param outputTest as the list of output words from the target/correct Mealy machine. Words are correctly translated if
	 * the obtained words from model are exact to this list
	 * @return as the number of correct translated words
	 */
	public static int CompareWithModelWithTestSet(AutomataForestMealyMachine model, List<String> inputTest,List<String> outputTest) {
		int score = 0;
		for(int i = 0; i < inputTest.size(); i++) {
			if(Objects.equals(model.OutputMealy(inputTest.get(i)),
					outputTest.get(i))){
				score++;
			}
		}
		return score;
	}
	

	/**
	 * used for analysing DFA produced by the RPNI by returning the amount of states
	 * @param model as the DFA to be analysed
	 * @return amount of states in model
	 */
	public static int getNumberOfStates(DFA<?,Character> model) {
		return model.getStates().size();
	}
	
	/**
	 * helper to calculate the average of a list of integer values
	 * @param Input as a list of integer
	 * @return average of list
	 */
	public static Double getAVG(List<Integer> Input) {
		Double ret = 0.0;
		for(int i =0;i<Input.size();i++) {
			ret = ret + Input.get(i);
		}
		return ret/Input.size();
	}
	

	/**
	 * helper to calculate the variance of a list of integer values which might be helpful for analyzing.
	 * @param Input as a list of integer
	 * @param avg as the previously calculated average of Input
	 * @return the variance of the list
	 */
	public static Double getVariance(List<Integer> Input, Double avg) {
		Double ret = 0.0;
		for(Integer val : Input) {
			double diff = val - avg;
			diff *= diff;
			ret += diff; 
		}
		return ret/(Input.size()-1);
	}
	

	/**
	 * function for swapping to elements. Used for generating noise on the training set, by, e.g., swapping positive and negative words for DFA
	 * @param input1 as one input list to have the first string swapped
	 * @param input2 as one input list to have the first string swapped
	 */
	public static void swapFirstElem(List<String> input1,List<String> input2) {
		input1.add(input2.get(0));
		input2.add(input1.get(0));
		input1.remove(0);
		input2.remove(0);
	}
	
	/**
	 * function to generate white noise on a training set for DFA, where the lists are evenly swapped
	 * @param noiseratio as the percentage of training data to be made noisy
	 * @param input1 as one input list to have the specified amount of strings swapped
	 * @param input2 as one input list to have the specified amount of strings swapped
	 */
	public static void PermutateWhiteNoise(double noiseratio,List<String> input1, List<String> input2) {
	if(noiseratio <= 0 || noiseratio >= 1) {
		throw new IllegalArgumentException("parameter not correctly chosen");
	}
	int ratio = (int) (input1.size()*noiseratio)+1;
	for(int i = 0;i<ratio;i++) {
		HelperFunctions.swapFirstElem(input1, input2);
		}
	}
	
	/**
	 * function to generate white noise on a training set for DFA, where the words from positive inference are inserted 
	 * into the negative training set
	 * @param noiseratio as the percentage of training data to be made noisy
	 * @param inputPos as the training list used for positive inference
	 * @param inputNeg as the training list used for negative inference
	 */
	public static void PermutateWhiteNoisePosInNeg(double noiseratio, List<String> inputPos, List<String> inputNeg) {
		if(noiseratio < 0 || noiseratio >= 1) {
			throw new IllegalArgumentException("parameter not correctly chosen");
		}
		if(noiseratio == 0) {return;} //case no swap wished
		int ratio = (int) (inputPos.size()*noiseratio)+1;
		for(int i = 0; i<ratio;i++) {
			inputNeg.add(inputPos.get(0));
			inputPos.remove(0);
		}
	}
	
	/**
	 * function to generate white noise on a training set for DFA, where the words from negative inference are inserted 
	 * into the positive training set
	 * @param noiseratio as the percentage of training data to be made noisy
	 * @param inputPos as the training list used for positive inference
	 * @param inputNeg as the training list used for negative inference
	 */
	public static void PermutateWhiteNoiseNegInPos(double noiseratio, List<String> inputPos, List<String> inputNeg) {
		if(noiseratio < 0 || noiseratio >= 1) {
			throw new IllegalArgumentException("parameter not correctly chosen");
		}
		if(noiseratio == 0) {return;} //case no swap wished
		int ratio = (int) (inputNeg.size()*noiseratio)+1;
		for(int i = 0; i<ratio;i++) {
			inputPos.add(inputNeg.get(0));
			inputNeg.remove(0);
		}
	}
	
	
	public static List<List<String>> PermutateNoiseMealy(double noiseratio, List<List<String>> trainSet,Alphabet<Character> outputSigma){
		if(noiseratio < 0 || noiseratio >= 1) {
			throw new IllegalArgumentException("parameter not correctly chosen");
		}
		if(noiseratio == 0) {return trainSet;} //case no swap wished
		
		
		
		List<Integer> marking = markUniqueChars(trainSet.get(0));
		
		//methodology 1 for % of noise to int:
		//number of noiseable chars inside marking set
		int noiseChars = getUniqueMealyNoiseChars(trainSet,marking);
		int noise = (int) (noiseChars * noiseratio);
		//methodology 2 for % of noise to int:
		//# of noise calculated in relation to number of words
		//int noise = (int) (noiseratio*trainSet.get(0).size()) + 1; 
		
		applyNoiseMealy(trainSet,marking,noise,noiseChars,outputSigma);
		
		return trainSet;
	}

	/**
	 *  
	 * @param trainSet as original trainingset
	 * @param marking list of changeable output characters in each string in the trainingsset
	 * @return integer of changeable chars
	 */
	public static int getUniqueMealyNoiseChars(List<List<String>> trainSet,List<Integer> marking) {
		int noiseChars = 0;
		for(int i = 0; i < marking.size(); i++) {
			if(marking.get(i)!=-1) { //-1 means string not noiseable without error/destroying determinism
				noiseChars = noiseChars + (trainSet.get(0).get(i).length() - marking.get(i)); //check = counting amount of possible chars to
			}
		}
		//permutate. Example: input: {dove, dog, duck, bird} => {d,o} is shared 
		//1. => check = 0 + (4 - 2) == 2
		//2. => check = 2 + (3 - 2) == 3 
		//3. => check = 3 + (4 - 1) == 6
		//4. => check = 6 + (4 - 0) == 10 => 10 outputchars are changeable without altering determinism
		return noiseChars;
	}
	
	/**
	 * colouring of unique characters in the input training set for Mealy machines
	 * @param trainSet as the trainSet of input words to extract unique char posititions
	 * @return List<Integer> where the list index represents every word in the trainSet and the integer represents
	 * the char position that starts a unique suffix within the training set.
	 * e.g.,: ret.get(0) == 10 => the first input word is uniquely defined starting at char position 10 
	 * meaning these characters do not occur within the training set at these positions with the same prefix
	 * If no unique suffix can be found (meaning: the word is already in the training set) the integer = -1.
	 */
	private static List<Integer> markUniqueChars(List<String> inputSet){
		List<Integer> ret = new ArrayList<Integer>();
		
		Trie trieRoot = new Trie();
		
		for(int i = 0; i < inputSet.size(); i++) {
			trieRoot.insert(inputSet.get(i)); //construct PTA
		}
		
		for(int i = 0; i < inputSet.size(); i++) {
			ret.add(trieRoot.getUniqueMarking(inputSet.get(i))); //receive unique markings from PTA
		}
		
		return ret;
	}
	
	/**
	 * iteratively noising all chars and determine char position to be noised (for further functions)
	 * @param trainSet
	 * @param markings
	 * @param noise
	 * @param noiseChars
	 * @param outputSigma
	 */
	private static void applyNoiseMealy(List<List<String>> trainSet, List<Integer> markings, int noise, int noiseChars,Alphabet<Character> outputSigma) {
		
		int index = 0;
		List<Integer> mutExc = new ArrayList<Integer>(); //checker list if char is already noised
		for(int i = 0; i < noise; i++) {
			index = getRandomNumInRange(0, noiseChars - 1);
			if(!mutExc.contains(index)) { 
				mutExc.add(index);
				noiseMealy(trainSet, markings, index, outputSigma);
			}
			else {
				i--;
			}
			
		}
	}
	
	/**
	 * determine output symbol that noise changes the target to
	 * @param output
	 * @param charPos
	 * @param outputSigma
	 * @return
	 */
	private static String applyNoiseMealySwitch(String output, int charPos, Alphabet<Character> outputSigma) {
		char temp1 = output.charAt(charPos);
		Boolean flag = true;
		char temp2;
		String switched = null;
		while(flag) {
			temp2 = outputSigma.getSymbol(getRandomNumInRange(0,outputSigma.size()-1));
			if(temp2!=temp1) {
				switched = switchChars(output, charPos,temp2);
				flag = false;
			}
		}
		return switched;
	}
	
	/**
	 * switch original char into "wrong"/noised output char 
	 * @param output
	 * @param charPos
	 * @param replace
	 * @return
	 */
	private static String switchChars(String output, int charPos, char replace) {
		StringBuilder str = new StringBuilder(output);
		str.setCharAt(charPos, replace);
		return new String(str.toString());
	}
	
	/**
	 * determine the string and char position in string to be altered, dependent on the index position determined beforehand
	 * @param trainSet
	 * @param markings
	 * @param index
	 * @param outputSigma
	 */
	private static void noiseMealy(List<List<String>> trainSet, List<Integer> markings, int index, Alphabet<Character> outputSigma) {
		List<Integer> counter = new ArrayList<Integer>(); //amount of permutable chars in each stringposition
		for(int i = 0; i < markings.size(); i++) {
			if(markings.get(i)== -1) {
				counter.add(0);
			}else {
				counter.add(trainSet.get(1).get(i).length() - markings.get(i) );
			}
		}
		int diff = 0; //increment of permutable chars
		int checker = 0; //iterable of string position in trainingset
		while(diff <= index && checker < markings.size()) {  
			diff += counter.get(checker);
			checker++;
		} 
		String output = trainSet.get(1).get(checker-1); //outputstring to be changed
		int pos = trainSet.get(1).get(checker-1).length()- (diff-index);// charposition in outputstring to be changed
		
		trainSet.get(1).set(checker-1, applyNoiseMealySwitch(output, pos, outputSigma));
	}
	
	
	/**
	 * method used to generate input strings for training and testing of DFA and Mealy machines. The outputs are obtained by 
	 * giving these generated strings to an oracle/target DFA/Mealy machine. It is supposed to mimic sampling real world data.
	 * @param sigma as the alphabet on which a string is generated
	 * @param stringlength as the length of the return string 
	 * @return randomly generated string
	 */
	public static String RandomStringFromAplhabet(Alphabet<Character> sigma, int stringlength) {
		List<Character> sigmaList = new ArrayList<Character>();

		for(int i = 0 ; i < sigma.size(); i++) {

			sigmaList.add(sigma.getSymbol(i));
		}

		String ret = new String();
		int temp;
		for(int i = 0; i < stringlength; i++) {
			temp = HelperFunctions.getRandomNumInRange(0, sigmaList.size()-1);
			ret = ret + sigmaList.get(temp); 
		}
		return ret;
	}

	public static String RandomStringFromAplhabet(Alphabet<Character> sigma, int stringlength, boolean isData) {
		if(!isData){
			return RandomStringFromAplhabet(sigma,stringlength);
		}

		List<Character> sigmaList = new ArrayList<Character>();

		for(int i = 0 ; i < sigma.size(); i++) {

			sigmaList.add(sigma.getSymbol(i));
		}

		String ret = new String();
		int temp;
		for(int i = 0; i < stringlength; i++) {
			temp = HelperFunctions.getRandomNumInRange(0, sigmaList.size()-1,true);
			ret = ret + sigmaList.get(temp);
		}
		return ret;
	}
	
	
	/**
	 * Hamming Distance as implemented per definition from Wikipedia => only for strings of same length
	 * only checks substitutions. Used to analyse how "close" generated strings from ForestCV/ForestMV are to the target Mealy machine.
	 * @param str1 as the input string from a Mealy machine
	 * @param str2 as the comparison case from the target Mealy machine
	 * @return Hamming Distance of the two strings
	 */
	public static int EditDistanceHamming(String str1, String str2) {
		int ret = 0;
		
		for(int i = 0; i < str1.length();i++) {
			
			if(str1.charAt(i) != str2.charAt(i)) {
				ret += 1;
			}
		}
		return ret;
	}
	
	/**
	 * comparison method for Mealy machines. It calculates the average Hamming Distance of a Mealy machine, given some input test
	 * strings and comparing to some output strings sampled by the target Mealy machine.
	 * @param model as the Mealy machine to be analysed
	 * @param in as the list of input strings, given to model
	 * @param out as the correct output strings sampled by the target Mealy machine
	 * @return average Hamming Distance of the Model to the target Mealy machine, given some input words with their corresponding sampled output words
	 */
	public static double getAverageEditDistanceOfList(MealyMachine<?,Character,?,Character> model, List<String> in, List<String> out) {
		long ret = 0;
		for(int i = 0; i < in.size(); i++) {
			ret += EditDistanceHamming(WordToStringIT(model.computeOutput(Word.fromString(in.get(i)))), out.get(i));
		}
		return (double) (ret) / (double) in.size();
	}
	
	/**
	 * comparison method for ForestMV. It calculates the average Hamming Distance of ForestMV with Mealy machines, given some input test
	 * strings and comparing to some output strings sampled by the target Mealy machine.
	 * @param model as the ForestMV with Mealy machines to be analysed
	 * @param in as the list of input strings, given to model
	 * @param out as the correct output strings sampled by the target Mealy machine
	 * @return average Hamming Distance of ForestMV to the target Mealy machine, given some input words with their corresponding sampled output words
	 */
	public static Double getAverageEditDistanceOfList(ForestMV model, List<String> in, List<String> out) {
		long ret = 0;
		for(int i = 0; i < in.size(); i++) {
			ret += EditDistanceHamming(model.OutputMealy(in.get(i)),
					out.get(i));
		}
		return (double) ret/ (double) in.size();
	}
	
	/**
	 * comparison method for AutomataForestMealyMachine. It calculates the average Hamming Distance of MV with Mealy machines, given some input test
	 * strings and comparing to some output strings sampled by the target Mealy machine.
	 * @param model as the ForestMV with Mealy machines to be analysed
	 * @param in as the list of input strings, given to model
	 * @param out as the correct output strings sampled by the target Mealy machine
	 * @return average Hamming Distance of ForestMV to the target Mealy machine, given some input words with their corresponding sampled output words
	 */
	public static Double getAverageEditDistanceOfList(AutomataForestMealyMachine model, List<String> in, List<String> out) {
		long ret = 0;
		for(int i = 0; i < in.size(); i++) {
			ret += EditDistanceHamming(model.OutputMealy(in.get(i)),
					out.get(i));
		}
		return (double) ret/ (double) in.size();
	}
	
	/**
	 * comparison method to analyse how well a Mealy machine predicts the last output symbol correctly, i.e., analyse Mealy machines
	 * if the last output symbol is of high significance
	 * @param model as the Mealy machine to be analysed
	 * @param inputStrings as the input strings given to model
	 * @param outputStrings as the correct output strings, where the last symbol is extracted
	 * @return number of correctly translated last output symbols
	 */
	public static int compareLastOutputSymbols(MealyMachine<?,Character,?,Character> model,List<String> inputStrings, List<String> outputStrings) {
		int ret = 0;
		String temp = new String();
		for(int i = 0; i < inputStrings.size();i++) {
			temp = HelperFunctions.WordToStringIT(model.computeOutput(Word.fromString(inputStrings.get(i))));
			if(temp.length() == outputStrings.get(i).length()) {
				if(temp.charAt(temp.length()-1) == outputStrings.get(i).charAt(temp.length()-1)) {
					ret++;
				}
			}
		}
		return ret;
	}
	
	/**
	 * comparison method to analyse how well a ForestMV for Mealy machines predicts the last output symbol correctly, i.e., analyse ForestMV
	 * if the last output symbol is of high significance
	 * @param model as the ForestMV with Mealy machines to be analysed
	 * @param inputStrings as the input strings given to model
	 * @param outputStrings as the correct output strings, where the last symbol is extracted
	 * @return number of correctly translated last output symbols
	 */
	public static int compareLastOutputSymbols(ForestMV model,List<String> inputStrings, List<String> outputStrings) {
		int ret = 0;
		for(int i = 0; i < inputStrings.size(); i++) {
			if(model.OutputMealy(inputStrings.get(i)).charAt(model.lastLength-1) 
					== outputStrings.get(i).charAt(outputStrings.get(i).length()-1)
					&& model.lastLength == outputStrings.get(i).length()) {	
				ret++;
				}
		}
		return ret;
	}
	
	/**
	 * comparison method to analyse how well a AutomataForestMealyMachine using MV predicts the last output symbol correctly, i.e., analyse ForestMV
	 * if the last output symbol is of high significance
	 * @param model as the ForestMV with Mealy machines to be analysed
	 * @param inputStrings as the input strings given to model
	 * @param outputStrings as the correct output strings, where the last symbol is extracted
	 * @return number of correctly translated last output symbols
	 */
	public static int compareLastOutputSymbols(AutomataForestMealyMachine model,List<String> inputStrings, List<String> outputStrings) {
		int ret = 0;
		for(int i = 0; i < inputStrings.size(); i++) {
			if(model.OutputMealy(inputStrings.get(i)).charAt(model.lastLength-1) 
					== outputStrings.get(i).charAt(outputStrings.get(i).length()-1)
					&& model.lastLength == outputStrings.get(i).length()) {	
				ret++;
				}
		}
		return ret;
	}
	
	/**
	 * getter for the local seed in "HelperFunctions.java" (making it reachable for outside classes). 
	 * It additionally increments the inner seed to circumvent continuous repition of the seed, which may otherwise lead to
	 * endless loops.
	 * @return inner seed of the class
	 */
	public static Long getSeed() {
		if(seed==null) {
			seed = (long) 1;
		}
		seed++;
		return seed;
	}
	
}
