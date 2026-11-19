package TUHH.AutomataForestDataSets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import de.learnlib.datastructure.pta.pta.AbstractBlueFringePTA;
import de.learnlib.datastructure.pta.pta.BlueFringePTAState;
import de.learnlib.datastructure.pta.pta.RedBlueMerge;
import net.automatalib.automata.fsa.DFA;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

/**
 * Learns a team of DFAs using Generalized Red-Blue Merging (GRBM).
 *
 * <p>Each team member is learned from the full labeled sample, but uses an
 * independently randomized order for selecting blue states and red states.
 * Unlabeled states in the resulting prefix-tree automata are rejecting.</p>
 */
public class AutomataTeamGRBM {

	public enum ClassificationStrategy {
		/** Each automaton has one vote; a tie rejects. */
		FAIR_VOTE,
		/** Vote weight is the inverse of the automaton's state count. */
		WEIGHTED_BY_SIZE,
		SMALLEST_AUTOMATON,
		/** Vote weight is the inverse square of the automaton's state count. */
		WEIGHTED_BY_SQUARED_SIZE,
		/** Only automata smaller than the team average vote, weighted by inverse square size. */
		SMALLER_THAN_AVERAGE_WEIGHTED_BY_SQUARED_SIZE
	}

	private final Alphabet<Character> alphabet;
	private final List<DFA<?, Character>> team;

	/**
	 * Learns a team using a reproducible seed from the shared experiment seed
	 * sequence.
	 *
	 * @param alphabet input alphabet
	 * @param inputSamples sample words
	 * @param acceptanceLabels corresponding positive/negative labels
	 * @param teamSize number of independently randomized GRBM runs
	 */
	public AutomataTeamGRBM(Alphabet<Character> alphabet, List<String> inputSamples,
			List<Boolean> acceptanceLabels, int teamSize) {
		this(alphabet, inputSamples, acceptanceLabels, teamSize, HelperFunctions.getSeed());
	}

	/**
	 * Learns a team using the supplied seed, allowing identical teams to be
	 * reproduced.
	 */
	public AutomataTeamGRBM(Alphabet<Character> alphabet, List<String> inputSamples,
			List<Boolean> acceptanceLabels, int teamSize, long seed) {
		this.alphabet = Objects.requireNonNull(alphabet, "alphabet");
		if (alphabet.isEmpty()) {
			throw new IllegalArgumentException("alphabet must not be empty");
		}
		if (inputSamples == null || acceptanceLabels == null
				|| inputSamples.size() != acceptanceLabels.size()) {
			throw new IllegalArgumentException("input samples and acceptance labels must have the same size");
		}
		if (inputSamples.isEmpty()) {
			throw new IllegalArgumentException("training samples must not be empty");
		}
		if (teamSize <= 0) {
			throw new IllegalArgumentException("team size must be positive");
		}
		validateSamples(inputSamples, acceptanceLabels);

		this.team = new ArrayList<>(teamSize);
		Random seedGenerator = new Random(seed);
		for (int i = 0; i < teamSize; i++) {
			Random ordering = new Random(seedGenerator.nextLong());
			GRBMPTA model = GRBMPTA.fromSamples(alphabet, inputSamples, acceptanceLabels);
			team.add(learnOne(model, ordering));
		}
	}

	public List<DFA<?, Character>> getTeam() {
		return Collections.unmodifiableList(new ArrayList<>(team));
	}

	public int size() {
		return team.size();
	}

	public boolean accepts(String input) {
		return accepts(input, ClassificationStrategy.FAIR_VOTE);
	}

	public boolean accepts(String input, ClassificationStrategy strategy) {
		Objects.requireNonNull(input, "input");
		Objects.requireNonNull(strategy, "strategy");
		switch (strategy) {
		case FAIR_VOTE:
			return fairVote(input);
		case WEIGHTED_BY_SIZE:
			return weightedVote(input, false, false);
		case SMALLEST_AUTOMATON:
			return smallestAutomaton().accepts(Word.fromString(input));
		case WEIGHTED_BY_SQUARED_SIZE:
			return weightedVote(input, true, false);
		case SMALLER_THAN_AVERAGE_WEIGHTED_BY_SQUARED_SIZE:
			return weightedVote(input, true, true);
		default:
			throw new IllegalArgumentException("Unsupported classification strategy: " + strategy);
		}
	}

	public boolean acceptsFairVote(String input) {
		return accepts(input, ClassificationStrategy.FAIR_VOTE);
	}

	public boolean acceptsWeightedVote(String input) {
		return accepts(input, ClassificationStrategy.WEIGHTED_BY_SIZE);
	}

	public boolean acceptsSmallestAutomaton(String input) {
		return accepts(input, ClassificationStrategy.SMALLEST_AUTOMATON);
	}

	public boolean acceptsSquaredWeightedVote(String input) {
		return accepts(input, ClassificationStrategy.WEIGHTED_BY_SQUARED_SIZE);
	}

	public boolean acceptsSmallerThanAverageSquaredWeightedVote(String input) {
		return accepts(input, ClassificationStrategy.SMALLER_THAN_AVERAGE_WEIGHTED_BY_SQUARED_SIZE);
	}

	private DFA<?, Character> learnOne(GRBMPTA model, Random ordering) {
		model.init(transition -> { });
		while (true) {
			List<BlueFringePTAState<Boolean, Void>> blue = new ArrayList<>();
			for (BlueFringePTAState<Boolean, Void> state : model.getStates()) {
				if (state.isBlue()) {
					blue.add(state);
				}
			}
			if (blue.isEmpty()) {
				break;
			}
			Collections.shuffle(blue, ordering);
			BlueFringePTAState<Boolean, Void> blueState = blue.get(0);
			List<BlueFringePTAState<Boolean, Void>> redCandidates = new ArrayList<>(model.getRedStates());
			Collections.shuffle(redCandidates, ordering);

			boolean merged = false;
			for (BlueFringePTAState<Boolean, Void> redState : redCandidates) {
				RedBlueMerge<Boolean, Void, BlueFringePTAState<Boolean, Void>> proposal =
						model.tryMerge(redState, blueState);
				if (proposal != null) {
					proposal.apply(model, transition -> { });
					merged = true;
					break;
				}
			}
			if (!merged) {
				model.promote(blueState, transition -> { });
			}
		}

		CompactDFA<Character> dfa = new CompactDFA<>(alphabet);
		model.toAutomaton(dfa, alphabet, label -> Boolean.TRUE.equals(label), transition -> null);
		return dfa;
	}

	private boolean fairVote(String input) {
		int acceptingVotes = 0;
		for (DFA<?, Character> automaton : team) {
			if (automaton.accepts(Word.fromString(input))) {
				acceptingVotes++;
			}
		}
		return acceptingVotes > team.size() / 2;
	}

	private boolean weightedVote(String input, boolean squared, boolean smallerThanAverage) {
		double averageSize = 0;
		for (DFA<?, Character> automaton : team) {
			averageSize += automaton.size();
		}
		averageSize /= team.size();

		double acceptingWeight = 0;
		double rejectingWeight = 0;
		int voters = 0;
		for (DFA<?, Character> automaton : team) {
			double size = automaton.size();
			if (smallerThanAverage && size >= averageSize) {
				continue;
			}
			double weight = squared ? 1.0 / (size * size) : 1.0 / size;
			if (automaton.accepts(Word.fromString(input))) {
				acceptingWeight += weight;
			} else {
				rejectingWeight += weight;
			}
			voters++;
		}

		if (voters == 0) {
			return weightedVote(input, true, false);
		}
		return acceptingWeight > rejectingWeight;
	}

	private DFA<?, Character> smallestAutomaton() {
		DFA<?, Character> smallest = team.get(0);
		for (int i = 1; i < team.size(); i++) {
			if (team.get(i).size() < smallest.size()) {
				smallest = team.get(i);
			}
		}
		return smallest;
	}

	private void validateSamples(List<String> inputSamples, List<Boolean> labels) {
		Map<String, Boolean> seen = new HashMap<>();
		for (int i = 0; i < inputSamples.size(); i++) {
			String input = Objects.requireNonNull(inputSamples.get(i), "input sample");
			Boolean label = Objects.requireNonNull(labels.get(i), "acceptance label");
			for (int j = 0; j < input.length(); j++) {
				if (!alphabet.containsSymbol(input.charAt(j))) {
					throw new IllegalArgumentException("sample contains a symbol outside the alphabet: " + input);
				}
			}
			Boolean previous = seen.put(input, label);
			if (previous != null && !previous.equals(label)) {
				throw new IllegalArgumentException("a sample word cannot be both accepted and rejected: " + input);
			}
		}
	}

	private static final class GRBMPTA extends AbstractBlueFringePTA<Boolean, Void,
			BlueFringePTAState<Boolean, Void>> {
		private GRBMPTA(int alphabetSize) {
			super(alphabetSize, new GRBMState());
		}

		private static GRBMPTA fromSamples(Alphabet<Character> alphabet, List<String> inputs,
				List<Boolean> labels) {
			GRBMPTA pta = new GRBMPTA(alphabet.size());
			for (int i = 0; i < inputs.size(); i++) {
				String input = inputs.get(i);
				int[] word = new int[input.length()];
				for (int j = 0; j < input.length(); j++) {
					word[j] = alphabet.getSymbolIndex(input.charAt(j));
				}
				pta.addSample(word, labels.get(i));
			}
			return pta;
		}
	}

	private static final class GRBMState extends BlueFringePTAState<Boolean, Void> {
		@Override
		protected GRBMState createState() {
			return new GRBMState();
		}

		@Override
		public boolean tryMergeStateProperty(Boolean otherProperty) {
			if (property == null) {
				property = otherProperty;
				return true;
			}
			return otherProperty == null || property.equals(otherProperty);
		}
	}
}
