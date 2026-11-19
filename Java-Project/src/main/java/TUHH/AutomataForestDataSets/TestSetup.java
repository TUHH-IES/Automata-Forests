package TUHH.AutomataForestDataSets;

import java.io.IOException;

import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.Alphabets;

public class TestSetup {
	public static void TestEnvironmentConsoleInteraction(ExperimentConfig config) throws IOException {
		TestEnvironment test = new TestEnvironment(config);
		
		try {
			if (config.getAutomatonType() == null) {
				throw new IllegalArgumentException("automatonType must be DFA or MEALY");
			}
			Alphabet<Character> sigmaIn = null;
			Alphabet<Character> sigmaOut = null;

			if ("coffee".equals(test.config.getExample())) {
				if (config.getAutomatonType() == ExperimentConfig.AutomatonType.DFA) {
					throw new IllegalArgumentException(
							"The coffee example is a Mealy machine and cannot be used as a DFA target");
				}
				MealyMachine<?, Character, ?, Character> target = RandomMealy.constructCoffeeCopy();
				final Character[] in = { 'w', 'p', 'b', 'c' };
				sigmaIn = Alphabets.fromArray(in);
				final Character[] out = { '1', '2', '3' };
				sigmaOut = Alphabets.fromArray(out);
				test.startExperiment(target, sigmaIn, sigmaOut);
				return;
			}

			if (!"random".equals(test.config.getExample())) {
				throw new IllegalArgumentException("example must be random or coffee");
			}
			int sigIn = test.config.getNumInputs();
			if (sigIn <= 0) {
				throw new IllegalArgumentException("numInputs must be positive");
			}
			char endsymbolIn = (char) (sigIn + 64);
			sigmaIn = Alphabets.characters('A', endsymbolIn);

			if (config.getAutomatonType() == ExperimentConfig.AutomatonType.DFA) {
				net.automatalib.automata.fsa.DFA<?, Character> target =
						RandomDFA.constructGeneric(config.getNumStates(), sigmaIn);
				test.startExperiment(target, sigmaIn);
			} else {
				int sigOut = test.config.getNumOutputs();
				if (sigOut <= 0) {
					throw new IllegalArgumentException("numOutputs must be positive for a Mealy machine");
				}
				char endsymbol = (char) (sigOut + 96);
				sigmaOut = Alphabets.characters('a', endsymbol);
				MealyMachine<?, Character, ?, Character> target = RandomMealy.constructGeneric(
						test.config.getNumStates(), sigOut, sigmaIn);
				test.startExperiment(target, sigmaIn, sigmaOut);
			}
		} catch (NumberFormatException nfe) {
			System.err.println("Invalid Format!");
		}
	}
}
