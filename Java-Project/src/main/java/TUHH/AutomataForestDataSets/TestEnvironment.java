package TUHH.AutomataForestDataSets;

import java.util.*;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.words.Alphabet;

public class TestEnvironment {

	private TestTimer timer;
	public ExperimentConfig config;

	public TestEnvironment(ExperimentConfig config) {
		this.config = config;
		timer = new TestTimer();
	}
	
	public void convergenceMealy(MealyMachine<?,Character,?,Character> target,
			ExperimentConfig config,
			Alphabet<Character> sigmaIn, Alphabet<Character> sigmaOut) {
		int States = config.getNumStates();		
		int numBags = config.getSizeForest().get(0);
		double bagratio = config.getBagRatio().get(0);
		int testSize = config.getSizeTest();
		int wordLength = config.getLengthWords();
		int repetitions = config.getRepetitions();
		
		CSVTestWriter CSVWriter = new CSVTestWriter();
		CSVWriter.initWriteSingleMealy(config.getName() + "_all");
		int iterationcounter = 1;
		
		for (int trainSize: config.getSizeLearn()) {	
			for (int b = 1; b <= repetitions; b++) {
				// get TrainSet on target Mealy
				List<List<String>> trainSet = RandomMealy.getRandomDataSets(target, sigmaIn, trainSize, wordLength);
				// getTestSet on target Mealy
				List<List<String>> testSet = RandomMealy.getTestSet(target, sigmaIn, trainSet.get(0), testSize, wordLength);

				MealyMachine<?, Character, ?, Character> SingleMealy = HelperFunctions.computeModelMealyBlueFringe(sigmaIn,
						trainSet.get(0), trainSet.get(1));
				AutomataForestMealyMachine forest = new AutomataForestMealyMachine(sigmaIn, sigmaOut, trainSet.get(0), trainSet.get(1),
						bagratio, numBags);
				MealyMachine<?, Character, ?, Character> forestPM = forest.getOutputCV_maxWords();
				for(MealyMachine<?, Character, ?, Character> instance: forest.getModelList()) {
					CSVWriter.writeDataSingleMealy(evaluateAutomaton(instance, config, sigmaIn, trainSet.get(0), trainSet.get(1), sigmaOut, testSet.get(0), testSet.get(1), States, "instance"));
				}
				CSVWriter.writeDataSingleMealy(evaluateAutomaton(SingleMealy, config, sigmaIn, trainSet.get(0), trainSet.get(1), sigmaOut, testSet.get(0), testSet.get(1), States, "RPNI"));
				ExperimentResults forestResults = evaluateAutomaton(forestPM, config, sigmaIn, trainSet.get(0), trainSet.get(1), sigmaOut, testSet.get(0), testSet.get(1), States, "PM");
				forestResults.setCorrectWordsMV(HelperFunctions.CompareWithModelWithTestSet(forest, testSet.get(0), testSet.get(1)));
				CSVWriter.writeDataSingleMealy(forestResults);
				System.out.println("done with step: " + iterationcounter + " from: "
						+ repetitions*config.getSizeLearn().size()
						+ " steps");
				iterationcounter++;
			}
		}
		CSVWriter.closeWriter();
		System.out.println("Done!");
	}

	public void startSingleExaminationMealy(MealyMachine<?,Character,?,Character> target,
			ExperimentConfig config,
			Alphabet<Character> sigmaIn, Alphabet<Character> sigmaOut) {
		int States = config.getNumStates();		
		int numBags = config.getSizeForest().get(0);
		double bagratio = config.getBagRatio().get(0);
		int trainSize = config.getSizeLearn().get(0);
		int testSize = config.getSizeTest();
		int wordLength = config.getLengthWords();
		int repetitions = config.getRepetitions();
		
		CSVTestWriter CSVWriter = new CSVTestWriter();
		CSVWriter.initWriteSingleMealy(config.getName());
		int iterationcounter = 1;

		for (int b = 1; b <= repetitions; b++) {
			// get TrainSet on target Mealy
			List<List<String>> trainSet = RandomMealy.getRandomDataSets(target, sigmaIn, trainSize, wordLength);
			// getTestSet on target Mealy
			List<List<String>> testSet = RandomMealy.getTestSet(target, sigmaIn, trainSet.get(0), testSize, wordLength);
			CSVWriter.writeDataSingleMealy(evaluateForest(numBags, bagratio, sigmaIn, trainSet.get(0),
					trainSet.get(1), sigmaOut, testSet.get(0), testSet.get(1), wordLength, States));
			System.out.println("done with step: " + iterationcounter + " from: "
					+ repetitions
					+ " steps");
			iterationcounter++;
		}
		CSVWriter.closeWriter();
		System.out.println("Done!");
	}

	private ExperimentResults evaluateForest(int numbags, double bagratio, Alphabet<Character> sigmaIn,
			List<String> trainInput, List<String> trainOutput, Alphabet<Character> sigmaOut,
			List<String> InputsMealyToTest, List<String> OutputsMealyToTest, int wordLength,
			int States) {
		
		ExperimentResults results = new ExperimentResults();
		
		results.setTestSize(InputsMealyToTest.size());
		results.setBagRatio(bagratio);
		results.setForestSize(numbags);

		results.setTrainingSize(trainInput.size());
		results.setInputAlphabetSize(sigmaIn.size());
		results.setOutputAlphabetSize(sigmaOut.size());
		results.setTargetSize(States);
		results.setWordLength(wordLength);

		timer.startTimer();
		MealyMachine<?, Character, ?, Character> SingleMealy = HelperFunctions.computeModelMealyBlueFringe(sigmaIn,
				trainInput, trainOutput);
		timer.CurrTimer();
		results.setModelSizeRPNI(SingleMealy.size());
		results.setTimeRPNI(timer.getDurationInSec());
		timer.startTimer();
		AutomataForestMealyMachine forest = new AutomataForestMealyMachine(sigmaIn, sigmaOut, trainInput, trainOutput,
				bagratio, numbags);
		timer.CurrTimer();
		results.setTimeForest(timer.getDurationInSec());

		// 1-1StringComparison:
		results.setCorrectWordsRPNI(HelperFunctions.CompareWithModelWithTestSet(SingleMealy, InputsMealyToTest,
				OutputsMealyToTest));
		MealyMachine<?, Character, ?, Character> forestPM = forest.getOutputCV_maxWords();
		results.setCorrectWordsPM(HelperFunctions.CompareWithModelWithTestSet(forestPM,
				InputsMealyToTest, OutputsMealyToTest));
		results.setModelSizePM(forestPM.size());
		results.setCorrectWordsMV(HelperFunctions.CompareWithModelWithTestSet(forest, InputsMealyToTest, OutputsMealyToTest));
		
		return results;
	}
	
	private ExperimentResults evaluateAutomaton(MealyMachine<?, Character, ?, Character> model, ExperimentConfig config, Alphabet<Character> sigmaIn,
			List<String> trainInput, List<String> trainOutput, Alphabet<Character> sigmaOut,
			List<String> InputsMealyToTest, List<String> OutputsMealyToTest, int States, String type) {
		
		ExperimentResults results = new ExperimentResults();
		
		results.setTestSize(InputsMealyToTest.size());
		results.setBagRatio(config.getBagRatio().get(0));
		results.setForestSize(config.getSizeForest().get(0));
		results.setTrainingSize(trainInput.size());
		results.setInputAlphabetSize(sigmaIn.size());
		results.setOutputAlphabetSize(sigmaOut.size());
		results.setTargetSize(States);
		results.setWordLength(config.getLengthWords());
		results.setModelSizeRPNI(model.size());
		results.setCorrectWordsPM(0);
		
		results.setTimeRPNI(0.0);
		results.setTimeForest(0.0);
		int performance = HelperFunctions.CompareWithModelWithTestSet(model, InputsMealyToTest,
				OutputsMealyToTest);
		if(type == "instance") {
			results.setCorrectWordsRPNI(performance);
			results.setCorrectWordsPM(0);
			results.setCorrectWordsMV(0);
		}
		else if(type == "RPNI") {
			results.setBagRatio(1);
			results.setCorrectWordsRPNI(performance);
			results.setCorrectWordsPM(0);
			results.setCorrectWordsMV(0);
		}
		else if(type == "PM") {
			results.setCorrectWordsPM(performance);
			results.setCorrectWordsRPNI(0);
			results.setCorrectWordsMV(0);
		}
		
		
		return results;
	}

}