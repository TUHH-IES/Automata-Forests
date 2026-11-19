package TUHH.AutomataForestDataSets;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.automatalib.automata.fsa.DFA;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

/**
 * Bagged forest of DFA acceptors learned with Blue-Fringe RPNI.
 */
public class AutomataForestDFAMachine {

	private final Alphabet<Character> alphabet;
	private final int numberOfBags;
	private final List<List<String>> trainingInputs = new ArrayList<>();
	private final List<List<Boolean>> trainingLabels = new ArrayList<>();
	private final List<List<String>> crossTestInputs = new ArrayList<>();
	private final List<List<Boolean>> crossTestLabels = new ArrayList<>();
	private final List<DFA<?, Character>> models = new ArrayList<>();
	private DFA<?, Character> selectedModel;
	private int maxScoreCounter;

	/**
	 * Builds a forest from labeled examples. Each bag trains one DFA; its
	 * out-of-bag examples are used to select the best individual DFA.
	 *
	 * @param alphabet symbols the learned DFAs read
	 * @param inputSet input words
	 * @param acceptanceSet corresponding acceptance labels
	 * @param ratio fraction of examples placed in each training bag
	 * @param numBags number of DFAs in the forest
	 */
	public AutomataForestDFAMachine(Alphabet<Character> alphabet, List<String> inputSet,
			List<Boolean> acceptanceSet, double ratio, int numBags) {
		if (inputSet.size() != acceptanceSet.size()) {
			throw new IllegalArgumentException("input and acceptance samples must be the same size");
		}
		if (inputSet.isEmpty()) {
			throw new IllegalArgumentException("training samples must not be empty");
		}
		if (acceptanceSet.contains(null)) {
			throw new IllegalArgumentException("acceptance labels must not be null");
		}
		if (numBags <= 0) {
			throw new IllegalArgumentException("number of bags must be positive");
		}
		this.alphabet = Objects.requireNonNull(alphabet, "alphabet");
		this.numberOfBags = numBags;
		int bagSize = determineBagSize(inputSet.size(), ratio);
		if (bagSize == 0) {
			throw new IllegalArgumentException("ratio produces an empty training bag");
		}
		fillBaggingSetsAndTestSets(inputSet, acceptanceSet, bagSize);
		trainForest();
	}

	public List<DFA<?, Character>> getModelList() {
		return new ArrayList<>(models);
	}

	/**
	 * Returns the forest member with the most correct out-of-bag predictions.
	 * Ties are resolved in favor of the earliest member.
	 */
	public DFA<?, Character> getOutputCV_maxWords() {
		int bestScore = -1;
		for (int i = 0; i < models.size(); i++) {
			int score = HelperFunctions.CompareWithModelWithTestSet(
					models.get(i), crossTestInputs.get(i), crossTestLabels.get(i));
			if (score > bestScore) {
				bestScore = score;
				selectedModel = models.get(i);
			}
		}
		maxScoreCounter = Math.max(bestScore, 0);
		return selectedModel;
	}

	public int getMaxScoreCounter() {
		return maxScoreCounter;
	}

	/**
	 * Classifies an input by strict majority vote; ties are classified as
	 * rejected.
	 */
	public boolean accepts(String input) {
		int acceptingVotes = 0;
		for (DFA<?, Character> model : models) {
			if (model.accepts(Word.fromString(input))) {
				acceptingVotes++;
			}
		}
		return acceptingVotes > models.size() / 2;
	}

	private int determineBagSize(int setSize, double ratio) {
		if (Double.isNaN(ratio) || ratio <= 0 || ratio > 1) {
			throw new IllegalArgumentException("ratio must be between 0 and 1");
		}
		return (int) Math.round(setSize * ratio);
	}

	private void fillBaggingSetsAndTestSets(List<String> inputSet, List<Boolean> acceptanceSet, int bagSize) {
		for (int i = 0; i < numberOfBags; i++) {
			List<String> trainingInput = new ArrayList<>();
			List<Boolean> trainingLabel = new ArrayList<>();
			List<String> testInput = new ArrayList<>();
			List<Boolean> testLabel = new ArrayList<>();
			int start = inputSet.size() == 1
					? 0
					: HelperFunctions.getRandomNumInRange(0, inputSet.size() - 1);
			for (int j = 0; j < inputSet.size(); j++) {
				int index = (start + j) % inputSet.size();
				if (j < bagSize) {
					trainingInput.add(inputSet.get(index));
					trainingLabel.add(acceptanceSet.get(index));
				} else {
					testInput.add(inputSet.get(index));
					testLabel.add(acceptanceSet.get(index));
				}
			}
			trainingInputs.add(trainingInput);
			trainingLabels.add(trainingLabel);
			crossTestInputs.add(testInput);
			crossTestLabels.add(testLabel);
		}
	}

	private void trainForest() {
		for (int i = 0; i < numberOfBags; i++) {
			models.add(HelperFunctions.computeModelDFABlueFringe(
					alphabet, trainingInputs.get(i), trainingLabels.get(i)));
		}
	}
}
