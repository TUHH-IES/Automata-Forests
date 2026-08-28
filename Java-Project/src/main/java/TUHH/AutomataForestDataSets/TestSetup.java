package TUHH.AutomataForestDataSets;

import java.io.IOException;

import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.Alphabets;

public class TestSetup {
	public static void TestEnvironmentConsoleInteraction(ExperimentConfig config) throws IOException {
		TestEnvironment test = new TestEnvironment(config);
		
		try {
			MealyMachine<?, Character, ?, Character> target = null;
			Alphabet<Character> sigmaIn = null;
			Alphabet<Character> sigmaOut = null;

			switch (test.config.getExample()) {
			case "coffee":
				target = RandomMealy.constructCoffeeCopy();
				final Character IN_WATER = 'w';
				final Character IN_POD = 'p';
				final Character IN_BUTTON = 'b';
				final Character IN_CLEAN = 'c';
				Character[] in = { IN_WATER, IN_POD, IN_BUTTON, IN_CLEAN };
				sigmaIn = Alphabets.fromArray(in);
				final Character OUT_OK = '1';
				final Character OUT_ERROR = '2';
				final Character OUT_COFFEE = '3';
				Character[] out = {OUT_OK, OUT_ERROR, OUT_COFFEE};
				sigmaOut = Alphabets.fromArray(out);
				break;
			case "random":
				int sigIn = test.config.getNumInputs();
				char endsymbolIn = (char) (sigIn + 64);
				sigmaIn = Alphabets.characters('A', endsymbolIn);
				char endsymbol = (char) (test.config.getNumOutputs() + 96);
		        sigmaOut = Alphabets.characters('a', endsymbol);
				target = RandomMealy.constructGeneric(test.config.getNumStates(),
						test.config.getNumOutputs(), sigmaIn);
				break;
			default:
				System.out.println("Invalid example");
				return;
			}
			if(config.getSizeLearn().size() > 1) {
				test.convergenceMealy(target, config, sigmaIn, sigmaOut);
			}
			else if(config.getBagRatio().size() > 1) {
				//TODO
			}
			else {
				test.startSingleExaminationMealy(target, config, sigmaIn, sigmaOut);
			}
		} catch (NumberFormatException nfe) {
			System.err.println("Invalid Format!");
		}
	}
}
