package TUHH_Krumnow.AutomataForestDataSets;

import java.util.*;
import java.util.regex.Pattern;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.MinimizationOperations;
import dk.brics.automaton.State;
import net.automatalib.automata.fsa.DFA;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.util.automata.equivalence.CharacterizingSets;
import net.automatalib.util.automata.equivalence.DeterministicEquivalenceTest;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import org.apache.commons.collections.collection.CompositeCollection;

public class TestEnvironment {
	
	private List<List<String>> DFATrainSet;
	private List<List<String>> DFATestSet;
	private List<String> MealyTrainSet;
	private List<String> MealyTestSet;
	private ArrayList<double[]> OutputForCSV = new ArrayList<double[]>();
	
	private RandomMealy refMealy;
	private Pattern refRegex;
	private TestTimer timer;
	private CSVTestWriter CSVWriter;
	
	private DFA<?,Character> ModelSingleDFA;
	
	public TestEnvironment() {
		timer = new TestTimer();
		CSVWriter = new CSVTestWriter();
	}
	
	public void startSingleExaminationDFA(Pattern regx, int posGenerTrain, int negGenerTrain, int posGenerTest, int negGenerTest,
			int stringMaxLength,int numBags, double ratio, int repOnSameTestSet, int repTrain, int repTest, int cores) throws InterruptedException {
			GeneratingSets generator = new GeneratingSets(regx, (negGenerTrain+negGenerTest)*(repTrain+repTest),stringMaxLength);
			Alphabet<Character> sigma = generator.getAlphabet();
			CSVWriter.initWriteSingleDFA();
			int iterationcounter = 1;
			for(int a = 1; a <= repTrain; a++) {
				List<String> posTrainSample = HelperFunctions.getRandomFractionOfList(generator.getPositiveSample(), posGenerTrain);
				List<String> negTrainSample = HelperFunctions.getRandomFractionOfList(generator.getNegativeSample(), negGenerTest);
				for(int b = 1; b <= repTest; b++) {
					List<String> posTestSample = HelperFunctions.getRandomExclusiveFractionOfList(generator.getPositiveSample(), posTrainSample, posGenerTest);
					List<String> negTestSample = HelperFunctions.getRandomExclusiveFractionOfList(generator.getNegativeSample(), negTrainSample, negGenerTest);
					for(int c = 1; c <= repOnSameTestSet; c++) {
						CSVWriter.writeDataSingleDFA(generateOutputDFA(numBags,ratio,sigma,0.0,0.0,posTrainSample,negTrainSample,
								posTestSample,negTestSample,stringMaxLength,cores));
						System.out.println("done with step: " + iterationcounter + " from: " + 
								repTrain*repTest*repOnSameTestSet + " steps");
						iterationcounter++;
						
					}
				}
			}
			CSVWriter.closeWriter();
			System.out.println("Done!");
	}
	
	public void startRiseTrainAlpha(int states,Alphabet<Character> SigmaIn,int numBags,int ratioStart,int ratioEnd,int ratioStep,
			int testSize,int trainWLength, int trainLengthIter,
			int testWLength,int repOnSameDFA,int maxreps, int cores) throws InterruptedException {
		CSVWriter.initWriteRiseTrainAlpha(states, SigmaIn.size(),trainLengthIter);
		RandomDFA target = new RandomDFA(states,SigmaIn);
		CharacteristicSetDFA characTarget = new CharacteristicSetDFA(target.randomDFA,SigmaIn);
		int charctSize = characTarget.characSet.get(0).size()+characTarget.characSet.get(1).size();
		int dataSize = trainLengthIter;
		int trainIter = dataSize;
		List<List<String>> train = target.getRandomDataSets(dataSize, trainWLength);
		List<List<String>> test = target.getTestSet(train, testSize, testWLength);
		double alpha = 0.0;
		int run = 0;
		do {
			for(int ratio = ratioStart; ratio <= ratioEnd; ratio = ratio + ratioStep) {
			alpha = (double)ratio/100.0;
				for(int i = 0; i < repOnSameDFA; i++) {
					CSVWriter.writeDataSingleDFA(generateOutputDFA(numBags,alpha,SigmaIn,0.0,0.0,train.get(0),train.get(1),
							test.get(0),test.get(1),trainWLength,cores));
				}
				System.out.println("trainSet size: " + trainIter + " alpha: " + ratio);
			}
			target.getRandomDataSets(train, dataSize, trainWLength); //add new training data of size dataSize
			test = target.getTestSet(train, testSize, testWLength); //redo testset which is mutually excl. from new trainset
			trainIter += dataSize;
			run++;
			//System.out.println("charact size: " + charctSize + "  trainSet size: " + trainIter);
		} while(!characTarget.oracleCheck(ModelSingleDFA) && run < maxreps);
		CSVWriter.closeWriter();
		System.out.println("Done!");
	}
	
	public void startRiseTrainAlphaStruct(int states,Alphabet<Character> sigIn,int numBags,double alpha,int testSize,
			int trainWLength,int testWLength,int startingTrainL,int trainLengthIter,
			int repOnSameDFA,int maxreps, int cores) throws InterruptedException {
		int temp = numBags;
		if (temp % 2 == 0) {
			temp = temp + 1;
		}
		CSVWriter.initWriteRiseTrainAlphaStruct(temp);
		RandomDFA target = new RandomDFA(states,sigIn);
		CharacteristicSetDFA characTarget = new CharacteristicSetDFA(target.randomDFA,sigIn);
		int charctDepth = characTarget.getDepth();
		int trainIter = startingTrainL;
		List<List<String>> train = target.getRandomDataSets(trainIter, trainWLength);
		List<List<String>> test = target.getTestSet(train, testSize, testWLength);
		int targetStates = target.randomDFA.size();
		int run = 0;
		do{
			for(int i = 0; i < repOnSameDFA; i++){
				CSVWriter.writeDataSingleDFA(generateOutputDFACharact(numBags,alpha,sigIn,train.get(0),train.get(1),
						test.get(0),test.get(1),trainWLength,targetStates,charctDepth,cores));
			}
			trainIter += trainLengthIter;
			target.getRandomDataSets(train, trainLengthIter, trainWLength); //add new training data of size dataSize
			test = target.getTestSet(train, testSize, testWLength); //redo testset which is mutually excl. from new trainset
			System.out.println("trainSet size: "+trainIter);
			run++;
		}while(!characTarget.oracleCheck(ModelSingleDFA) && run < maxreps);
		CSVWriter.closeWriter();
		System.out.println("Done!");
	}
	
	public void startNumBagEvalDFA(Pattern regx, int posGenerTrain, int negGenerTrain, int posGenerTest, int negGenerTest,
			int stringMaxLength,int numStartBags, int numEndBags,int numBagsStepSize, double ratio, int repOnSameTestSet, int repTrain, int repTest, int cores) throws InterruptedException {
			GeneratingSets generator = new GeneratingSets(regx, (negGenerTrain+negGenerTest)*(repTrain+repTest),stringMaxLength);
			Alphabet<Character> sigma = generator.getAlphabet();
			CSVWriter.initWriteNumBagDFA();
			int numbagStepInfo = (int) (numEndBags-numStartBags)/numBagsStepSize+1; 
			int iterationcounter = 1;
			for(int a = 1; a <= repTrain; a++) {
				List<String> posTrainSample = HelperFunctions.getRandomFractionOfList(generator.getPositiveSample(), posGenerTrain);
				List<String> negTrainSample = HelperFunctions.getRandomFractionOfList(generator.getNegativeSample(), negGenerTest);
				for(int b = 1; b <= repTest; b++) {
					List<String> posTestSample = HelperFunctions.getRandomExclusiveFractionOfList(generator.getPositiveSample(), posTrainSample, posGenerTest);
					List<String> negTestSample = HelperFunctions.getRandomExclusiveFractionOfList(generator.getNegativeSample(), negTrainSample, negGenerTest);
					for(int numBags = numStartBags; numBags <= numEndBags; numBags = numBags + numBagsStepSize){
						for(int c = 1; c <= repOnSameTestSet; c++) {
							CSVWriter.writeDataSingleDFA(generateOutputDFA(numBags,ratio,sigma,0.0,0.0,posTrainSample,negTrainSample,
									posTestSample,negTestSample,stringMaxLength,cores));
							System.out.println("done with step: " + iterationcounter + " from: " + 
								repTrain*repTest*repOnSameTestSet*numbagStepInfo + " steps");
							iterationcounter++;
						}
					}
				}
			}
			CSVWriter.closeWriter();
			System.out.println("Done!");
	}
	
	public void startRatioEvalDFA(Pattern regx, int posGenerTrain, int negGenerTrain, int posGenerTest, int negGenerTest,
			int stringMaxLength,int numbags,int bagratioStart,int bagratioEnd ,int bagratioStep, int repOnSameTestSet, int repTrain, int repTest, int cores) throws InterruptedException {
			GeneratingSets generator = new GeneratingSets(regx, (negGenerTrain+negGenerTest)*(repTrain+repTest),stringMaxLength);
			Alphabet<Character> sigma = generator.getAlphabet();
			CSVWriter.initWriteRatioDFA();
			int ratioStepInfo = (int) (bagratioEnd-bagratioStart)/bagratioStep+1; 
			int iterationcounter = 1;
			for(int a = 1; a <= repTrain; a++) {
				List<String> posTrainSample = HelperFunctions.getRandomFractionOfList(generator.getPositiveSample(), posGenerTrain);
				List<String> negTrainSample = HelperFunctions.getRandomFractionOfList(generator.getNegativeSample(), negGenerTest);
				for(int b = 1; b <= repTest; b++) {
					List<String> posTestSample = HelperFunctions.getRandomExclusiveFractionOfList(generator.getPositiveSample(), posTrainSample, posGenerTest);
					List<String> negTestSample = HelperFunctions.getRandomExclusiveFractionOfList(generator.getNegativeSample(), negTrainSample, negGenerTest);
					for(int ratio = bagratioStart ; ratio <= bagratioEnd; ratio = ratio + bagratioStep){
						//get Testresults
						double bagratio = (double)ratio/100.0;
						for(int c = 1; c <= repOnSameTestSet; c++) {
							CSVWriter.writeDataSingleDFA(generateOutputDFA(numbags,bagratio,sigma,0.0,0.0,posTrainSample,negTrainSample,
									posTestSample,negTestSample,stringMaxLength,cores));
							System.out.println("done with step: " + iterationcounter + " from: " + 
								repTrain*repTest*repOnSameTestSet*ratioStepInfo + " steps");
							iterationcounter++;
						}
					}
				}
			}
			CSVWriter.closeWriter();
			System.out.println("Done!");
	}
	
	public void startSingleExaminationMealy(int States, int OutputSigma, Alphabet<Character> Sigma, 
			int numBags, double bagratio, int trainSize, int testSize, int trainWordLength, int testWordLength, 
			int repetitionOnSameMealy, int repetitionOfDiffMealys, int repetitionsOfDiffTrainSets,
			int repetitionsOfDiffTestSets) {
		CSVWriter.initWriteSingleMealy();
		int iterationcounter = 1;
		for(int a = 1; a <= repetitionOfDiffMealys; a++) {
			//generate target Mealy
			RandomMealy refMealy = new RandomMealy(States,OutputSigma,Sigma);
			Alphabet<Character> outputSigma = refMealy.outputsigma;
			for(int b = 1; b <= repetitionsOfDiffTrainSets; b++) {
				//get TrainSet on target Mealy
				List<List<String>> trainSet = refMealy.getRandomDataSets(trainSize, trainWordLength);
				for(int c = 1; c <= repetitionsOfDiffTestSets; c++) {
					//getTestSet on target Mealy
					List<List<String>> testSet = refMealy.getTestSet(trainSet.get(0), testSize, testWordLength);
					for(int d = 1; d <= repetitionOnSameMealy; d++) {
						//get Testresults
						CSVWriter.writeDataSingleMealy(generateOutputMealy(numBags,bagratio,Sigma,0.0,trainSet.get(0),trainSet.get(1),
							outputSigma,testSet.get(0),testSet.get(1),trainWordLength,testWordLength,States));
						System.out.println("done with step: " + iterationcounter + " from: " + 
								repetitionOfDiffMealys*repetitionsOfDiffTrainSets*
								repetitionsOfDiffTestSets*repetitionOnSameMealy + " steps");
						iterationcounter++;

					}
				}
			}
		}
		CSVWriter.closeWriter();
		System.out.println("Done!");
	}
	
	public void startNumBagEvalMealy(int States, int OutputSigma, Alphabet<Character> Sigma, 
			int numStartBags, int numEndBags,int numBagsStepSize,double bagratio, int trainSize, int testSize, int trainWordLength, int testWordLength, 
			int repetitionOnSameMealy, int repetitionOfDiffMealys, int repetitionsOfDiffTrainSets,
			int repetitionsOfDiffTestSets) {
		CSVWriter.initWriteNumBagMealy();
		int iterationcounter = 1;
		int numbagStepInfo = (int) (numEndBags-numStartBags)/numBagsStepSize+1; 
		for(int a = 1; a <= repetitionOfDiffMealys; a++) {
			//generate target Mealy
			RandomMealy refMealy = new RandomMealy(States,OutputSigma,Sigma);
			Alphabet<Character> outputSigma = refMealy.outputsigma;
			for(int b = 1; b <= repetitionsOfDiffTrainSets; b++) {
				//get TrainSet on target Mealy
				List<List<String>> trainSet = refMealy.getRandomDataSets(trainSize, trainWordLength);
				for(int c = 1; c <= repetitionsOfDiffTestSets; c++) {
					//getTestSet on target Mealy
					List<List<String>> testSet = refMealy.getTestSet(trainSet.get(0), testSize, testWordLength);
					for(int numBags = numStartBags; numBags <= numEndBags; numBags = numBags + numBagsStepSize){
						//get Testresults
						for(int d = 1; d <= repetitionOnSameMealy; d++)  {
						CSVWriter.writeDataSingleMealy(generateOutputMealy(numBags,bagratio,Sigma,0.0,trainSet.get(0),trainSet.get(1),
							outputSigma,testSet.get(0),testSet.get(1),trainWordLength,testWordLength,States));
						System.out.println("done with step: " + iterationcounter + " from: " + 
								repetitionOfDiffMealys*repetitionsOfDiffTrainSets*
								repetitionsOfDiffTestSets*repetitionOnSameMealy*numbagStepInfo + " steps");
						iterationcounter++;
						}
					}
				}
			}
		}
		CSVWriter.closeWriter();
		System.out.println("Done!");
	}
	
	public void startRatioEvalMealy(int States, int OutputSigma, Alphabet<Character> Sigma, 
	int numbags,int bagratioStart,int bagratioEnd ,int bagratioStep, int trainSize, int testSize, int trainWordLength, int testWordLength, 
	int repetitionOnSameMealy, int repetitionOfDiffMealys, int repetitionsOfDiffTrainSets,
	int repetitionsOfDiffTestSets) {
	CSVWriter.initWriteBagRatioMealy();
	int ratioStepInfo = (int) (bagratioEnd-bagratioStart)/bagratioStep+1; 
	int iterationcounter = 1;
	for(int a = 1; a <= repetitionOfDiffMealys; a++) {
	//generate target Mealy
	RandomMealy refMealy = new RandomMealy(States,OutputSigma,Sigma);
	Alphabet<Character> outputSigma = refMealy.outputsigma;
	for(int b = 1; b <= repetitionsOfDiffTrainSets; b++) {
		//get TrainSet on target Mealy
		List<List<String>> trainSet = refMealy.getRandomDataSets(trainSize, trainWordLength);
		for(int c = 1; c <= repetitionsOfDiffTestSets; c++) {
			//getTestSet on target Mealy
			List<List<String>> testSet = refMealy.getTestSet(trainSet.get(0), testSize, testWordLength);
			for(int ratio = bagratioStart ; ratio <= bagratioEnd; ratio = ratio + bagratioStep){
				//get Testresults
				double bagratio = (double)ratio/100.0;
				for(int d = 1; d <= repetitionOnSameMealy; d++)  {
						CSVWriter.writeDataSingleMealy(generateOutputMealy(numbags,bagratio,Sigma,0.0,trainSet.get(0),trainSet.get(1),
							outputSigma,testSet.get(0),testSet.get(1),trainWordLength,testWordLength,States));
						System.out.println("done with step: " + iterationcounter + " from: " + 
						repetitionOfDiffMealys*repetitionsOfDiffTrainSets*
						repetitionsOfDiffTestSets*repetitionOnSameMealy*ratioStepInfo + " steps");
						iterationcounter++;
						}
					}
				}
			}
		}
		CSVWriter.closeWriter();
		System.out.println("Done!");
	}
	
	public void startNoiseDFA(Pattern regx, int posGenerTrain, int negGenerTrain, int posGenerTest, int negGenerTest,
			int stringMaxLength,int numBags, double ratio, double posInNeg, double negInPos, int repNoise, 
			int repOnSameTestSet, int repTrain, int repTest,int cores) throws InterruptedException {
		GeneratingSets generator = new GeneratingSets(regx, (negGenerTrain+negGenerTest)*(repTrain+repTest),stringMaxLength);
		Alphabet<Character> sigma = generator.getAlphabet();
		CSVWriter.initWriteNoiseDFA();
		int iterationcounter = 1;
		for(int a = 1; a <= repTrain; a++) {
			List<String> origPosTrainSample = HelperFunctions.getRandomFractionOfList(generator.getPositiveSample(), posGenerTrain);
			List<String> origNegTrainSample = HelperFunctions.getRandomFractionOfList(generator.getNegativeSample(), negGenerTest);
			for(int b = 1; b <= repTest; b++) {
				List<String> posTestSample = HelperFunctions.getRandomExclusiveFractionOfList(generator.getPositiveSample(), origPosTrainSample, posGenerTest);
				List<String> negTestSample = HelperFunctions.getRandomExclusiveFractionOfList(generator.getNegativeSample(), origNegTrainSample, negGenerTest);
				for(int c = 1; c <= repNoise; c++) {
					List<String> posTrainSample = new ArrayList<String>();  //need to work with new and orig training set, else 
					posTrainSample.addAll(origPosTrainSample);				//the set would become more and more noisy
					List<String> negTrainSample = new ArrayList<String>();
					negTrainSample.addAll(origNegTrainSample);
					Collections.shuffle(posTrainSample, new Random(HelperFunctions.getSeed())); //shuffle to prevent repetition
					Collections.shuffle(negTrainSample, new Random(HelperFunctions.getSeed())); //of same noisy data each loop iteration
					HelperFunctions.PermutateWhiteNoisePosInNeg(posInNeg, posTrainSample, negTrainSample);
					HelperFunctions.PermutateWhiteNoiseNegInPos(negInPos, posTrainSample, negTrainSample);
					for(int d = 1; d <= repOnSameTestSet; d++) {
						CSVWriter.writeDataSingleDFA(generateOutputDFA(numBags,ratio,sigma,posInNeg,negInPos,posTrainSample,negTrainSample,
							posTestSample,negTestSample,stringMaxLength,cores));
						System.out.println("done with step: " + iterationcounter + " from: " + 
							repTrain*repTest*repOnSameTestSet + " steps");
						iterationcounter++;
					
					}
				}
			}
		}
		CSVWriter.closeWriter();
		System.out.println("Done!");
	}
	
	
	public void startNoiseMealy(int States, int OutputSigma, Alphabet<Character> Sigma, 
			int numBags, double bagratio, int trainSize, int testSize, int trainWordLength, int testWordLength,
			double noiseRatio, int repNoise,
			int repetitionOnSameMealy, int repetitionOfDiffMealys, int repetitionsOfDiffTrainSets,
			int repetitionsOfDiffTestSets) {
		CSVWriter.initWriteNoiseMealy();
		int iterationcounter = 1;
		Long shuffleSeed;
		for(int a = 1; a <= repetitionOfDiffMealys; a++) {
			//generate target Mealy
			RandomMealy refMealy = new RandomMealy(States,OutputSigma,Sigma);
			Alphabet<Character> outputSigma = refMealy.outputsigma;
			for(int b = 1; b <= repetitionsOfDiffTrainSets; b++) {
				//get TrainSet on target Mealy
				List<List<String>> origTrainSet = refMealy.getRandomDataSets(trainSize, trainWordLength);
				for(int c = 1; c <= repetitionsOfDiffTestSets; c++) {
					//getTestSet on target Mealy
					List<List<String>> testSet = refMealy.getTestSet(origTrainSet.get(0), testSize, testWordLength);
					for(int d = 1; d <= repNoise; d++) {
						List<List<String>> trainSet = new ArrayList<List<String>>(origTrainSet);  //need to work with new and orig training set, else 
							//the set would become more and more noisy
						shuffleSeed = HelperFunctions.getSeed();
						Collections.shuffle(trainSet.get(0), new Random(shuffleSeed));
						Collections.shuffle(trainSet.get(1), new Random(shuffleSeed));
						HelperFunctions.PermutateNoiseMealy(noiseRatio,trainSet,refMealy.outputsigma);
						for(int e = 1; e <= repetitionOnSameMealy; e++) {
							//get Testresults
							CSVWriter.writeDataSingleMealy(generateOutputMealy(numBags,bagratio,Sigma,noiseRatio,trainSet.get(0),trainSet.get(1),
									outputSigma,testSet.get(0),testSet.get(1),trainWordLength,testWordLength,States));
							System.out.println("done with step: " + iterationcounter + " from: " + 
									repetitionOfDiffMealys*repetitionsOfDiffTrainSets*
									repetitionsOfDiffTestSets*repetitionOnSameMealy + " steps");
							iterationcounter++;
						}

					}
				}
			}
		}
		CSVWriter.closeWriter();
		System.out.println("Done!");
	}
	
	/**
	 * old function used for the paper "Using Forest Structures for Passive Automata Learning". Reuse for replica of paper data
	 * @param numbags
	 * @param bagratio
	 * @param sigma
	 * @param posTrain
	 * @param negTrain
	 * @param posTest
	 * @param negTest
	 * @param stringMaxLength
	 * @return
	 */
	private double[] generateOutputDFA2(int numbags, double bagratio, Alphabet<Character> sigma,
			List<String> posTrain, List<String> negTrain, List<String> posTest, List<String> negTest,int stringMaxLength){
		//ArrayList<double[]> OutputOfTests = new ArrayList<double[]>();
		double[] temp = new double[18];
		temp[0] = posTest.size()+negTest.size();
		temp[1] = bagratio;
		temp[2] = numbags;
		temp[3] = posTrain.size();
		temp[4] = negTrain.size();
		temp[5] = stringMaxLength;
		timer.startTimer();
		DFA<?,Character> ModelSingleDFA = HelperFunctions.computeModelBlueFringe
				(sigma,	HelperFunctions.transformFromListToCollection(posTrain),
				HelperFunctions.transformFromListToCollection(negTrain));
		timer.CurrTimer();
		temp[15] = timer.getDurationInSec();
		timer.startTimer();
		ForestCV forestCV = new ForestCV(sigma,posTrain,negTrain,bagratio,numbags);
		DFA<?,Character> modelCV = forestCV.getForestModel();
		timer.CurrTimer();
		temp[16] = timer.getDurationInSec();
		timer.startTimer();
		ForestMV forestMV = new ForestMV(sigma,posTrain,negTrain,bagratio,numbags);
		timer.CurrTimer();
		temp[17] = timer.getDurationInSec();
		int[] hypothesis = new int[3];
		hypothesis = HelperFunctions.CompareWithModelWithTestSetNullH(ModelSingleDFA, posTest, negTest);
		temp[6] = hypothesis[0]; 
		temp[9] = hypothesis[1]; //false positive: (reject positive result)
		temp[12] = hypothesis[2]; //false negative: (accept negative result)

		hypothesis = HelperFunctions.CompareWithModelWithTestSetNullH(modelCV, posTest, negTest);
		temp[7] = hypothesis[0];
		temp[10] = hypothesis[1];//false positive: (reject positive result)
		temp[13] = hypothesis[2];//false negative: (accept negative result)
		hypothesis = HelperFunctions.CompareWithModelWithTestSetNullH(forestMV, posTest, negTest);
		temp[8] = hypothesis[0];
		temp[11] = hypothesis[1];//false positive: (reject positive result)
		temp[14] = hypothesis[2];//false negative: (accept negative result)

		return temp;
	}
	
	private double[] generateOutputDFACharact(int numBags, double bagratio, Alphabet<Character> sigma, List<String> posTrain,
			List<String> negTrain, List<String> posTest, List<String> negTest,int trainMaxL, int targetStates, int targetDepth,int cores) throws InterruptedException {
		AutomataForestDFA forest = new AutomataForestDFA(sigma,posTrain,negTrain,bagratio,numBags,cores);
		int numbags = forest.getNumBags();
		double[] temp = new double[3*numbags+11];
		temp[0] = posTrain.size() + negTrain.size(); //trainingsset size = x axis

		temp[1] = bagratio;
		temp[2] = numbags;
		temp[3] = sigma.size(); 
		temp[4] = posTest.size() + negTest.size();
		temp[5] = trainMaxL;
		ModelSingleDFA = HelperFunctions.computeModelBlueFringe
				(sigma,	HelperFunctions.transformFromListToCollection(posTrain),
				HelperFunctions.transformFromListToCollection(negTrain));

		forest.getOutputCV();
		temp[6] = forest.getCVPosition();
		temp[7] = targetStates;
		List<Integer> stateList = forest.getNumberOfStates(); //number of states = y axis
		temp[8] = ModelSingleDFA.size();
		for(int i = 0; i < numbags; i++) {
			temp[i+9] = stateList.get(i);
		}
		CharacteristicSetDFA charct = new CharacteristicSetDFA(ModelSingleDFA, sigma);
		temp[9+numbags] = targetDepth;
		temp[10+numbags] = charct.getDepth();
		
		stateList = forest.getDepth(); //depth of automaton = z axis
		for(int i = 0; i < numbags; i++) {
			temp[i+11+numbags] = stateList.get(i);
		}
		
		for(int i = 0; i < posTest.size(); i++) {
			forest.acceptsCounter(posTest.get(i));
		}
		for(int i = 0; i < negTest.size(); i++) {
			forest.acceptsCounter(negTest.get(i));
		}
		List<Double> MVList = forest.getNumberOfMajorities(negTest.size()+posTest.size()); //colouring of points depending on 
		//impact within forest
		for(int i = 0; i < numbags; i++) {
			temp[i+11+2*numbags] = MVList.get(i);
		}
		
		
		return temp;
	}
	
	private double[] generateOutputDFA(int numbags, double bagratio, Alphabet<Character> sigma, double posInNegNoise, double negInPosNoise, 
			List<String> posTrain, List<String> negTrain, List<String> posTest, List<String> negTest,int stringMaxLength, int cores) throws InterruptedException {
		//ArrayList<double[]> OutputOfTests = new ArrayList<double[]>();
		double[] temp = new double[19];
		temp[0] = posTest.size()+negTest.size();
		temp[1] = bagratio;
		temp[2] = numbags;
		temp[3] = posTrain.size();
		temp[4] = negTrain.size();
		temp[5] = stringMaxLength;
		temp[6] = posInNegNoise;
		temp[7] = negInPosNoise;
		timer.startTimer();
		ModelSingleDFA = HelperFunctions.computeModelBlueFringe
				(sigma,	HelperFunctions.transformFromListToCollection(posTrain),
				HelperFunctions.transformFromListToCollection(negTrain));
		timer.CurrTimer();
		temp[17] = timer.getDurationInSec();
		timer.startTimer();
		AutomataForestDFA forest = new AutomataForestDFA(sigma,posTrain,negTrain,bagratio,numbags,cores);
		timer.CurrTimer();
		temp[18] = timer.getDurationInSec();
		DFA<?,Character> modelCV = forest.getOutputCV();
		
		int[] hypothesis = new int[3];
		hypothesis = HelperFunctions.CompareWithModelWithTestSetNullH(ModelSingleDFA, posTest, negTest);
		temp[8] = hypothesis[0]; 
		temp[11] = hypothesis[1]; //false positive: (reject positive result)
		temp[14] = hypothesis[2]; //false negative: (accept negative result)

		hypothesis = HelperFunctions.CompareWithModelWithTestSetNullH(modelCV, posTest, negTest);
		temp[9] = hypothesis[0];
		temp[12] = hypothesis[1];//false positive: (reject positive result)
		temp[15] = hypothesis[2];//false negative: (accept negative result)
		hypothesis = HelperFunctions.CompareWithModelWithTestSetNullH(forest, posTest, negTest);
		temp[10] = hypothesis[0];
		temp[13] = hypothesis[1];//false positive: (reject positive result)
		temp[16] = hypothesis[2];//false negative: (accept negative result)

		return temp;
	}
	
	/**
	 * old function used for the paper "Using Forest Structures for Passive Automata Learning". Reuse for replica of paper data
	 * @param numbags
	 * @param bagratio
	 * @param sigma
	 * @param trainInput
	 * @param trainOutput
	 * @param outputsigma
	 * @param InputsMealyToTest
	 * @param OutputsMealyToTest
	 * @param trainWordLength
	 * @param testWordLength
	 * @param States
	 * @return
	 */
	private double[] generateOutputMealy2(int numbags, double bagratio, Alphabet<Character> sigma,
			List<String> trainInput, List<String> trainOutput,Alphabet<Character> outputsigma,
			List<String> InputsMealyToTest, List<String> OutputsMealyToTest,int trainWordLength, int testWordLength,
			int States) {
		
			//initialising Algorithms:
			double[] temp = new double[25];
			temp[0] = (double) InputsMealyToTest.size();
			temp[1] = bagratio;
			temp[2] = (double) numbags;
			
			temp[3] = (double)trainInput.size();
			temp[4] = (double)sigma.size();
			temp[5] = (double)outputsigma.size();
			temp[6] = (double)States;
			temp[7] = (double)trainWordLength;
			temp[8] = (double)testWordLength;
			timer.startTimer();
			MealyMachine<?,Character,?,Character> SingleMealy = HelperFunctions.computeModelMealyBlueFringe(sigma,trainInput,trainOutput);
			timer.CurrTimer();
			temp[21] = timer.getDurationInSec();
			timer.startTimer();
			ForestCV forestcv = new ForestCV(sigma, trainInput, trainOutput, bagratio, numbags,true,false);
			timer.CurrTimer();
			temp[22] = timer.getDurationInSec();
			timer.startTimer();
			ForestCV forestcvEditDistance = new ForestCV(sigma, trainInput, trainOutput, bagratio, numbags,true,true);
			timer.CurrTimer();
			temp[23] = timer.getDurationInSec();
			timer.startTimer();
			ForestMV forestmv = new ForestMV(sigma, outputsigma, trainInput, trainOutput, bagratio, numbags,true);
			timer.CurrTimer();
			temp[24] = timer.getDurationInSec();
			
			//1-1StringComparison:
			temp[9] = (double) HelperFunctions.CompareWithModelWithTestSet(SingleMealy, InputsMealyToTest, OutputsMealyToTest);
			temp[10] = (double) HelperFunctions.CompareWithModelWithTestSet(forestcv.getForestModelM(),InputsMealyToTest, OutputsMealyToTest);
			temp[11] = (double) HelperFunctions.CompareWithModelWithTestSet(forestcvEditDistance.getForestModelM(),InputsMealyToTest, OutputsMealyToTest);
			temp[12] = (double) HelperFunctions.CompareWithModelWithTestSet(forestmv, InputsMealyToTest, OutputsMealyToTest, true);

			//Hamming Edit-Distance:
			temp[13] = HelperFunctions.getAverageEditDistanceOfList(SingleMealy, InputsMealyToTest, OutputsMealyToTest);
			temp[14] = HelperFunctions.getAverageEditDistanceOfList(forestcv.getForestModelM(), InputsMealyToTest, OutputsMealyToTest);
			temp[15] = HelperFunctions.getAverageEditDistanceOfList(forestcvEditDistance.getForestModelM(), InputsMealyToTest, OutputsMealyToTest);
			temp[16] = HelperFunctions.getAverageEditDistanceOfList(forestmv, InputsMealyToTest, OutputsMealyToTest);

			//last OutputSymbol:
			temp[17] = (double) HelperFunctions.compareLastOutputSymbols(SingleMealy,InputsMealyToTest, OutputsMealyToTest);
			temp[18] = (double) HelperFunctions.compareLastOutputSymbols(forestcv.getForestModelM(),InputsMealyToTest, OutputsMealyToTest);
			temp[19] = (double) HelperFunctions.compareLastOutputSymbols(forestcvEditDistance.getForestModelM(),InputsMealyToTest, OutputsMealyToTest);
			temp[20] = (double) HelperFunctions.compareLastOutputSymbols(forestmv, InputsMealyToTest, OutputsMealyToTest);
			
		return temp;
	}
	
	private double[] generateOutputMealy(int numbags, double bagratio, Alphabet<Character> sigma, double noiseRatio,
			List<String> trainInput, List<String> trainOutput,Alphabet<Character> outputsigma,
			List<String> InputsMealyToTest, List<String> OutputsMealyToTest,int trainWordLength, int testWordLength,
			int States) {
		
			//initialising Algorithms:
			double[] temp = new double[24];
			temp[0] = (double) InputsMealyToTest.size();
			temp[1] = bagratio;
			temp[2] = (double) numbags;
			
			temp[3] = (double)trainInput.size();
			temp[4] = (double)sigma.size();
			temp[5] = (double)outputsigma.size();
			temp[6] = (double)States;
			temp[7] = (double)trainWordLength;
			temp[8] = (double)testWordLength;
			temp[9] = noiseRatio;
			timer.startTimer();
			MealyMachine<?,Character,?,Character> SingleMealy = HelperFunctions.computeModelMealyBlueFringe(sigma,trainInput,trainOutput);
			timer.CurrTimer();
			temp[22] = timer.getDurationInSec();
			timer.startTimer();
			AutomataForestMealyMachine forest = new AutomataForestMealyMachine(sigma,outputsigma, trainInput, trainOutput, bagratio, numbags);
			timer.CurrTimer();
			temp[23] = timer.getDurationInSec();
			
			//1-1StringComparison:
			temp[10] = (double) HelperFunctions.CompareWithModelWithTestSet(SingleMealy, InputsMealyToTest, OutputsMealyToTest);
			temp[11] = (double) HelperFunctions.CompareWithModelWithTestSet(forest.getOutputCV_maxWords(),InputsMealyToTest, OutputsMealyToTest);
			temp[12] = (double) HelperFunctions.CompareWithModelWithTestSet(forest.getOutputCV_minED(),InputsMealyToTest, OutputsMealyToTest);
			temp[13] = (double) HelperFunctions.CompareWithModelWithTestSet(forest, InputsMealyToTest, OutputsMealyToTest);

			//Hamming Edit-Distance:
			temp[14] = HelperFunctions.getAverageEditDistanceOfList(SingleMealy, InputsMealyToTest, OutputsMealyToTest);
			temp[15] = HelperFunctions.getAverageEditDistanceOfList(forest.getOutputCV_maxWords(), InputsMealyToTest, OutputsMealyToTest);
			temp[16] = HelperFunctions.getAverageEditDistanceOfList(forest.getOutputCV_minED(), InputsMealyToTest, OutputsMealyToTest);
			temp[17] = HelperFunctions.getAverageEditDistanceOfList(forest, InputsMealyToTest, OutputsMealyToTest);

			//last OutputSymbol:
			temp[18] = (double) HelperFunctions.compareLastOutputSymbols(SingleMealy,InputsMealyToTest, OutputsMealyToTest);
			temp[19] = (double) HelperFunctions.compareLastOutputSymbols(forest.getOutputCV_maxWords(),InputsMealyToTest, OutputsMealyToTest);
			temp[20] = (double) HelperFunctions.compareLastOutputSymbols(forest.getOutputCV_minED(),InputsMealyToTest, OutputsMealyToTest);
			temp[21] = (double) HelperFunctions.compareLastOutputSymbols(forest, InputsMealyToTest, OutputsMealyToTest);
			
		return temp;
	}
	
	
}