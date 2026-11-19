package TUHH.AutomataForestDataSets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.automatalib.automata.fsa.DFA;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

/**
 * Utilities for generating random DFA targets and labeled word samples.
 */
public final class RandomDFA {

	private RandomDFA() {
	}

	public static DFA<?, Character> constructGeneric(int numberOfStates, Alphabet<Character> alphabet) {
		if (numberOfStates <= 0) {
			throw new IllegalArgumentException("number of states must be positive");
		}
		if (alphabet == null || alphabet.isEmpty()) {
			throw new IllegalArgumentException("alphabet must not be empty");
		}

		CompactDFA<Character> dfa = new CompactDFA<>(alphabet);
		List<Integer> states = new ArrayList<>();
		Random random = new Random(HelperFunctions.getSeed());
		for (int i = 0; i < numberOfStates; i++) {
			states.add(dfa.addState(random.nextBoolean()));
		}
		dfa.setInitialState(states.get(0));

		for (int state : states) {
			for (int symbol = 0; symbol < alphabet.size(); symbol++) {
				int target = random.nextInt(numberOfStates);
				dfa.setTransition(state, symbol, states.get(target));
			}
		}
		return dfa;
	}

	public static LabeledDataSet getRandomDataSet(DFA<?, Character> target, Alphabet<Character> alphabet,
			int dataSize, int maxStringLength) {
		validateSampleSize(alphabet, dataSize, maxStringLength);
		List<String> inputs = new ArrayList<>();
		for (int i = 0; i < dataSize; i++) {
			inputs.add(HelperFunctions.RandomStringFromAplhabet(alphabet,
					HelperFunctions.getRandomNumInRange(1, maxStringLength)));
		}
		return label(target, inputs);
	}

	public static LabeledDataSet getTestDataSet(DFA<?, Character> target, Alphabet<Character> alphabet,
			List<String> trainingInputs, int dataSize, int maxStringLength) {
		validateSampleSize(alphabet, dataSize, maxStringLength);
		if (dataSize > 0 && !hasAvailableTestWord(alphabet, trainingInputs, maxStringLength)) {
			throw new IllegalArgumentException("no test words remain outside the training set");
		}
		List<String> inputs = new ArrayList<>();
		while (inputs.size() < dataSize) {
			String input = HelperFunctions.RandomStringFromAplhabet(alphabet,
					HelperFunctions.getRandomNumInRange(1, maxStringLength));
			if (!trainingInputs.contains(input)) {
				inputs.add(input);
			}
		}
		return label(target, inputs);
	}

	private static LabeledDataSet label(DFA<?, Character> target, List<String> inputs) {
		List<Boolean> acceptance = new ArrayList<>();
		for (String input : inputs) {
			acceptance.add(target.accepts(Word.fromString(input)));
		}
		return new LabeledDataSet(inputs, acceptance);
	}

	private static void validateSampleSize(Alphabet<Character> alphabet, int dataSize, int maxStringLength) {
		if (alphabet == null || alphabet.isEmpty()) {
			throw new IllegalArgumentException("alphabet must not be empty");
		}
		if (dataSize < 0) {
			throw new IllegalArgumentException("sample size must not be negative");
		}
		if (maxStringLength <= 0) {
			throw new IllegalArgumentException("maximum word length must be positive");
		}
	}

	private static boolean hasAvailableTestWord(Alphabet<Character> alphabet, List<String> trainingInputs,
			int maxStringLength) {
		long possibleWords;
		if (alphabet.size() == 1) {
			possibleWords = maxStringLength;
		} else {
			possibleWords = 0;
			long wordsOfLength = 1;
			for (int length = 1; length <= maxStringLength; length++) {
				if (wordsOfLength > Long.MAX_VALUE / alphabet.size()) {
					possibleWords = Long.MAX_VALUE;
					break;
				}
				wordsOfLength *= alphabet.size();
				if (possibleWords > Long.MAX_VALUE - wordsOfLength) {
					possibleWords = Long.MAX_VALUE;
					break;
				}
				possibleWords += wordsOfLength;
			}
		}

		Set<Character> symbols = new HashSet<>();
		for (int i = 0; i < alphabet.size(); i++) {
			symbols.add(alphabet.getSymbol(i));
		}
		Set<String> validTrainingWords = new HashSet<>();
		for (String input : trainingInputs) {
			if (input.length() > 0 && input.length() <= maxStringLength && usesOnlySymbols(input, symbols)) {
				validTrainingWords.add(input);
			}
		}
		return possibleWords > validTrainingWords.size();
	}

	private static boolean usesOnlySymbols(String input, Set<Character> symbols) {
		for (int i = 0; i < input.length(); i++) {
			if (!symbols.contains(input.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static final class LabeledDataSet {
		private final List<String> inputs;
		private final List<Boolean> acceptanceLabels;

		private LabeledDataSet(List<String> inputs, List<Boolean> acceptanceLabels) {
			this.inputs = inputs;
			this.acceptanceLabels = acceptanceLabels;
		}

		public List<String> getInputs() {
			return inputs;
		}

		public List<Boolean> getAcceptanceLabels() {
			return acceptanceLabels;
		}
	}
}
