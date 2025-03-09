package TUHH_Krumnow.AutomataForestDataSets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.Alphabets;

public class TestEnvironmentConsoleInteractions{
	
	private int format;
	private TestEnvironment test;
	private final Pattern regx1 = Pattern.compile("[abcdefg][hij][abc][abc][g][c][kl]{1,3}[xyz]");
	private final Pattern regx2 = Pattern.compile("[0123]([abc]|[efg]{1,2})[h][0123][a]{1,4}(b)[c][a]{1,4}(b)[c]");
	private final Pattern regx3 = Pattern.compile("([abcdefg][hij][abc][abc][g][c][kl]{1,3}[xyz])|([a]{1,4}(b)[c][a]{1,4}(b)[c][a]{1,4}(b)[c][a]{1,4}(b)[c])|([0123]([abc]|[efg]{1,2})[h][0123][a]{1,4}(b)[c][a]{1,4}(b)[c])|(^[a]{1,40}[b])");
	
	
	
	
	public TestEnvironmentConsoleInteractions(int _format) throws IOException {
		TestEnvironment test = new TestEnvironment();
		
		format = _format;
		switch(format) {
		case 1:
			startSingleMealyEval(test);
			break;
		case 2:
			startSingleDFAEval(test);
			break;
		case 3:
			startNumBagMealy(test);
			break;
		case 4:
			startalphaMealy(test);
			break;
		case 5:
			startNumBagDFA(test);
			break;
		case 6:
			startalphaDFA(test);
			break;
		case 7:
			startWhiteNoiseDFA(test);
			break;
		case 8:
			startWhiteNoiseMealy(test);
			break;
		case 10:
			startRiseTrainAlpha(test);
			break;
		case 11:
			startRiseTrainCharact(test);
			break;
		default:
			System.out.println("given input is not defined!");
			break;
		}
			
	}
	
	private void startalphaMealy(TestEnvironment test) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
	    System.out.print("Enter Number of Mealy States: (default in Paper 35)");
        int states = Integer.parseInt(br.readLine());    
	    System.out.print("Enter amount of input symbols: (default in Paper 62, max=62)");
        int sigIn = Integer.parseInt(br.readLine());
	    char endsymbolIn = (char) (sigIn + 64);	 
	    Alphabet<Character> SigmaIn = Alphabets.characters('A', endsymbolIn);
	    System.out.print("Enter amount of output symbols: (default in Paper 26)");
	    int sigOut = Integer.parseInt(br.readLine());
	    System.out.print("Enter parameter m in forests: (default in Paper 101)");
	    int numBags = Integer.parseInt(br.readLine());
	    System.out.print("Enter starting alpha for forests in percentage (integer): (default in Paper 1)");
	    int ratioStart = Integer.parseInt(br.readLine());
	    System.out.print("Enter ending alpha for forests in percentage (integer):");
	    int ratioEnd = Integer.parseInt(br.readLine());
	    System.out.print("Enter stepsize for alpha in percentage (integer): (default in Paper 5)");
	    int ratioStep = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of Strings for training: (default in Paper 500)");
	    int trainSize = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of Strings for testing: (default in Paper 1000)");
	    int testSize = Integer.parseInt(br.readLine());
	    System.out.print("Enter max. length of training Strings: (default in Paper 2*number of states)");
	    int trainWLength = Integer.parseInt(br.readLine());
	    System.out.print("Enter max. length of test Strings: (default in Paper 2*number of states)");
	    int testWLength = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of tests on different Mealy's: (default in Paper 10)");
	    int repOnDiffMealy = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of tests on the same Mealy: (default in Paper 100)");
	    int repOnSameMealy = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of different variations of training data: (default in Paper 3)");
	    int repTrain = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of different variations of test data: (default in Paper 3)");
	    int repTest = Integer.parseInt(br.readLine());
	    
	    
	    test.startRatioEvalMealy(states, sigOut, SigmaIn, numBags,ratioStart,ratioEnd, ratioStep, 
	    trainSize, testSize, trainWLength, testWLength, repOnSameMealy, repOnDiffMealy, 
	    repTrain, repTest);
		} catch(NumberFormatException nfe) {
	      System.err.println("Invalid Format!");
	    }	
	}
	
	private void startNumBagMealy(TestEnvironment test) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
	    System.out.print("Enter Number of Mealy States: (default in Paper 35)");
        int states = Integer.parseInt(br.readLine());    
	    System.out.print("Enter amount of input symbols: (default in Paper 62, max=62)");
        int sigIn = Integer.parseInt(br.readLine());
	    char endsymbolIn = (char) (sigIn + 64);	 
	    Alphabet<Character> SigmaIn = Alphabets.characters('A', endsymbolIn);
	    System.out.print("Enter amount of output symbols: (default in Paper 26)");
	    int sigOut = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of startbags in forests: (default in Paper 1)");
	    int numStartBags = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of endbags in forests: (default in Paper 402)");
	    int numEndBags = Integer.parseInt(br.readLine());
	    System.out.print("Enter stepsize for parameter m in forest: (default in Paper 10)");
	    int numBagsStepSize = Integer.parseInt(br.readLine());
	    System.out.print("Enter alpha for forests in percentage (integer): (default in Paper 80)");
	    int bagrat = Integer.parseInt(br.readLine());
	    double alpha = (double) bagrat/100.0;
	    System.out.print("Enter amount of Strings for training: (default in Paper 500)");
	    int trainSize = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of Strings for testing: (default in Paper 1000)");
	    int testSize = Integer.parseInt(br.readLine());
	    System.out.print("Enter max. length of training Strings: (default in Paper 2*number of states)");
	    int trainWLength = Integer.parseInt(br.readLine());
	    System.out.print("Enter max. length of test Strings: (default in Paper 2*number of states)");
	    int testWLength = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of tests on different Mealy's: (default in Paper 10)");
	    int repOnDiffMealy = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of tests on the same Mealy: (default in Paper 100)");
	    int repOnSameMealy = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of different variations of training data: (default in Paper 3)");
	    int repTrain = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of different variations of test data: (default in Paper 3)");
	    int repTest = Integer.parseInt(br.readLine());
	    
	    
	    test.startNumBagEvalMealy(states, sigOut, SigmaIn, numStartBags,numEndBags,numBagsStepSize, alpha, 
	    trainSize, testSize, trainWLength, testWLength, repOnSameMealy, repOnDiffMealy, 
	    repTrain, repTest);
		} catch(NumberFormatException nfe) {
	      System.err.println("Invalid Format!");
	    }	
	}
	
	private void startSingleMealyEval(TestEnvironment test) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
	    System.out.print("Enter Number of Mealy States: (default in Paper 35)");
        int states = Integer.parseInt(br.readLine());    
	    System.out.print("Enter amount of input symbols: (default in Paper 62, max=62)");
        int sigIn = Integer.parseInt(br.readLine());
	    char endsymbolIn = (char) (sigIn + 64);	 
	    Alphabet<Character> SigmaIn = Alphabets.characters('A', endsymbolIn);
	    System.out.print("Enter amount of output symbols: (default in Paper 26)");
	    int sigOut = Integer.parseInt(br.readLine());
	    System.out.print("Enter parameter m in forests: (default in Paper 101)");
	    int numBags = Integer.parseInt(br.readLine());
	    System.out.print("Enter alpha for forests in percentage (integer): (default in Paper 80)");
	    int bagrat = Integer.parseInt(br.readLine());
	    double alpha = (double) bagrat/100.0;
	    System.out.print("Enter amount of Strings for training: (default in Paper 500)");
	    int trainSize = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of Strings for testing: (default in Paper 1000)");
	    int testSize = Integer.parseInt(br.readLine());
	    System.out.print("Enter max. length of training Strings: (default in Paper 2*number of states)");
	    int trainWLength = Integer.parseInt(br.readLine());
	    System.out.print("Enter max. length of test Strings: (default in Paper 2*number of states)");
	    int testWLength = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of tests on different Mealy's: (default in Paper 10)");
	    int repOnDiffMealy = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of tests on the same Mealy: (default in Paper 100)");
	    int repOnSameMealy = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of different variations of training data: (default in Paper 3)");
	    int repTrain = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of different variations of test data: (default in Paper 3)");
	    int repTest = Integer.parseInt(br.readLine());
	    
	    
	    test.startSingleExaminationMealy(states, sigOut, SigmaIn, numBags, alpha, 
	    trainSize, testSize, trainWLength, testWLength, repOnSameMealy, repOnDiffMealy, 
	    repTrain, repTest);
		} catch(NumberFormatException nfe) {
	      System.err.println("Invalid Format!");
	    }	
	}
	
	private void startNumBagDFA(TestEnvironment test) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
		System.out.println("press Number according to the regular expression you want to choose:");
		System.out.println("press 1 for: (maximum amount of possible postive words = 7938)");
		System.out.println(regx1);
		System.out.println("press 2 for: (maximum amount of possible postive words = 3840)");
		System.out.println(regx2);
		System.out.println("press 3 for: (maximum amount of possible postive words = 12074)");
		System.out.println(regx3);
        int regex = Integer.parseInt(br.readLine());    
	    System.out.print("Enter amount of positive training strings: ");
        int posGenerTrain = Integer.parseInt(br.readLine()); 
	    System.out.print("Enter amount of negative training strings:");
	    int negGenerTrain = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of positive test strings: ");
        int posGenerTest = Integer.parseInt(br.readLine()); 
	    System.out.print("Enter amount of negative test strings:");
	    int negGenerTest = Integer.parseInt(br.readLine());
	    System.out.print("Enter maximum length of the generated strings:");
	    int stringMaxLength = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of startbags in forests: (default in Paper 1)");
	    int numStartBags = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of endbags in forests: (default in Paper 402)");
	    int numEndBags = Integer.parseInt(br.readLine());
	    System.out.print("Enter stepsize for parameter m in forest: (default in Paper 10)");
	    int numBagsStepSize = Integer.parseInt(br.readLine());
	    System.out.print("Enter alpha for forests in percentage (integer): (default in Paper 80)");
	    int bagrat = Integer.parseInt(br.readLine());
	    double alpha = (double) bagrat/100.0;

	    System.out.print("Enter amount of tests on the same testset: (default in Paper 100)");
	    int repOnSameTestSet = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of different variations of training data:");
	    int repTrain = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of different variations of test data:");
	    int repTest = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of available threads (default 1):");
	    int cores = Integer.parseInt(br.readLine());
	    
	    
	    test.startNumBagEvalDFA(getRegex(regex), posGenerTrain, negGenerTrain, posGenerTest,negGenerTest,stringMaxLength,numStartBags,
	    		numEndBags, numBagsStepSize,alpha, 
	    		repOnSameTestSet,repTrain,repTest,cores);
		} catch(NumberFormatException nfe) {
	      System.err.println("Invalid Format!");
	    } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
	
	private void startalphaDFA(TestEnvironment test) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
		System.out.println("press Number according to the regular expression you want to choose:");
		System.out.println("press 1 for: (maximum amount of possible postive words = 7938)");
		System.out.println(regx1);
		System.out.println("press 2 for: (maximum amount of possible postive words = 3840)");
		System.out.println(regx2);
		System.out.println("press 3 for: (maximum amount of possible postive words = 12074)");
		System.out.println(regx3);
        int regex = Integer.parseInt(br.readLine());    
	    System.out.print("Enter amount of positive training strings: ");
        int posGenerTrain = Integer.parseInt(br.readLine()); 
	    System.out.print("Enter amount of negative training strings:");
	    int negGenerTrain = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of positive test strings: ");
        int posGenerTest = Integer.parseInt(br.readLine()); 
	    System.out.print("Enter amount of negative test strings:");
	    int negGenerTest = Integer.parseInt(br.readLine());
	    System.out.print("Enter maximum length of the generated strings:");
	    int stringMaxLength = Integer.parseInt(br.readLine());
	    System.out.print("Enter parameter m in forests: (default in Paper 101)");
	    int numBags = Integer.parseInt(br.readLine());
	    System.out.print("Enter starting alpha for forests in percentage (integer): (default in Paper 1)");
	    int ratioStart = Integer.parseInt(br.readLine());
	    System.out.print("Enter ending alpha for forests in percentage (integer):");
	    int ratioEnd = Integer.parseInt(br.readLine());
	    System.out.print("Enter stepsize for alpha in percentage (integer): (default in Paper 5)");
	    int ratioStep = Integer.parseInt(br.readLine());

	    System.out.print("Enter amount of tests on the same testset: (default in Paper 100)");
	    int repOnSameTestSet = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of different variations of training data:");
	    int repTrain = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of different variations of test data:");
	    int repTest = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of available threads (default 1):");
	    int cores = Integer.parseInt(br.readLine());
	    
	    test.startRatioEvalDFA(getRegex(regex), posGenerTrain, negGenerTrain, posGenerTest,negGenerTest,stringMaxLength,numBags, ratioStart,
	    		ratioEnd,ratioStep,repOnSameTestSet,repTrain,repTest,cores);
		} catch(NumberFormatException nfe) {
	      System.err.println("Invalid Format!");
	    } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
	
	private void startSingleDFAEval(TestEnvironment test) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
		System.out.println("press Number according to the regular expression you want to choose:");
		System.out.println("press 1 for: (maximum amount of possible postive words = 7938)");
		System.out.println(regx1);
		System.out.println("press 2 for: (maximum amount of possible postive words = 3840)");
		System.out.println(regx2);
		System.out.println("press 3 for: (maximum amount of possible postive words = 12074)");
		System.out.println(regx3);
        int regex = Integer.parseInt(br.readLine());    
	    System.out.print("Enter amount of positive training strings: ");
        int posGenerTrain = Integer.parseInt(br.readLine()); 
	    System.out.print("Enter amount of negative training strings:");
	    int negGenerTrain = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of positive test strings: ");
        int posGenerTest = Integer.parseInt(br.readLine()); 
	    System.out.print("Enter amount of negative test strings:");
	    int negGenerTest = Integer.parseInt(br.readLine());
	    System.out.print("Enter maximum length of the generated strings:");
	    int stringMaxLength = Integer.parseInt(br.readLine());
	    System.out.print("Enter parameter m in forests: (default in Paper 101)");
	    int numBags = Integer.parseInt(br.readLine());
	    System.out.print("Enter alpha for forests in percentage (integer): (default in Paper 80)");
	    int bagrat = Integer.parseInt(br.readLine());
	    double alpha = (double) bagrat/100.0;

	    System.out.print("Enter amount of tests on the same testset: (default in Paper 100)");
	    int repOnSameTestSet = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of different variations of training data:");
	    int repTrain = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of different variations of test data:");
	    int repTest = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of available threads (default 1):");
	    int cores = Integer.parseInt(br.readLine());
	    
	    
	    test.startSingleExaminationDFA(getRegex(regex), posGenerTrain, negGenerTrain, posGenerTest,negGenerTest,stringMaxLength,numBags, alpha, 
	    		repOnSameTestSet,repTrain,repTest,cores);
		} catch(NumberFormatException nfe) {
	      System.err.println("Invalid Format!");
	    } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
	
	private void startWhiteNoiseDFA(TestEnvironment test) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
		System.out.println("press Number according to the regular expression you want to choose:");
		System.out.println("press 1 for: (maximum amount of possible postive words = 7938)");
		System.out.println(regx1);
		System.out.println("press 2 for: (maximum amount of possible postive words = 3840)");
		System.out.println(regx2);
		System.out.println("press 3 for: (maximum amount of possible postive words = 12074)");
		System.out.println(regx3);
        int regex = Integer.parseInt(br.readLine());    
	    System.out.print("Enter amount of positive training strings: ");
        int posGenerTrain = Integer.parseInt(br.readLine()); 
	    System.out.print("Enter amount of negative training strings:");
	    int negGenerTrain = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of positive test strings: ");
        int posGenerTest = Integer.parseInt(br.readLine()); 
	    System.out.print("Enter amount of negative test strings:");
	    int negGenerTest = Integer.parseInt(br.readLine());
	    System.out.print("Enter maximum length of the generated strings:");
	    int stringMaxLength = Integer.parseInt(br.readLine());
	    System.out.print("Enter parameter m in forests: (default in Paper 101)");
	    int numBags = Integer.parseInt(br.readLine());
	    System.out.print("Enter alpha for forests in percentage (integer): (default in Paper 80)");
	    int bagrat = Integer.parseInt(br.readLine());
	    double alpha = (double) bagrat/100.0;
	    System.out.print("Enter ratio of positive strings to swapped into negative training strings:");
	    int posInNegInt = Integer.parseInt(br.readLine());
	    double posInNeg = (double)posInNegInt/100.0;
	    System.out.print("Enter ratio of negative strings to swapped into positive training strings:");
	    int negInPosInt = Integer.parseInt(br.readLine());
	    double negInPos = (double)negInPosInt/100.0;
	    System.out.print("Enter repetition of different noisy data");
	    int repNoise = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of tests on the same testset: (default in Paper 100)");
	    int repOnSameTestSet = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of different variations of training data:");
	    int repTrain = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of different variations of test data:");
	    int repTest = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of available threads (default 1):");
	    int cores = Integer.parseInt(br.readLine());
	    
	    
	    test.startNoiseDFA(getRegex(regex), posGenerTrain, negGenerTrain, posGenerTest,negGenerTest,stringMaxLength,numBags, alpha, 
	    		posInNeg, negInPos,repNoise,
	    		repOnSameTestSet,repTrain,repTest,cores);
		} catch(NumberFormatException nfe) {
	      System.err.println("Invalid Format!");
	    } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
	
	private void startWhiteNoiseMealy(TestEnvironment test) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
	    System.out.print("Enter Number of Mealy States: (default in Paper 35)");
        int states = Integer.parseInt(br.readLine());    
	    System.out.print("Enter amount of input symbols: (default in Paper 62, max=62)");
        int sigIn = Integer.parseInt(br.readLine());
	    char endsymbolIn = (char) (sigIn + 64);	 
	    Alphabet<Character> SigmaIn = Alphabets.characters('A', endsymbolIn);
	    System.out.print("Enter amount of output symbols: (default in Paper 26)");
	    int sigOut = Integer.parseInt(br.readLine());
	    System.out.print("Enter parameter m in forests: (default in Paper 101)");
	    int numBags = Integer.parseInt(br.readLine());
	    System.out.print("Enter alpha for forests in percentage (integer): (default in Paper 80)");
	    int bagrat = Integer.parseInt(br.readLine());
	    double alpha = (double) bagrat/100.0;
	    System.out.print("Enter amount of Strings for training: (default in Paper 500)");
	    int trainSize = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of Strings for testing: (default in Paper 1000)");
	    int testSize = Integer.parseInt(br.readLine());
	    System.out.print("Enter max. length of training Strings: (default in Paper 2*number of states)");
	    int trainWLength = Integer.parseInt(br.readLine());
	    System.out.print("Enter max. length of test Strings: (default in Paper 2*number of states)");
	    int testWLength = Integer.parseInt(br.readLine());
	    System.out.print("Enter peracentage of noise happening:");
	    int noiseInt = Integer.parseInt(br.readLine());
	    double noiseRatio = (double) noiseInt/100.0;
	    System.out.print("Enter amount of repetitions of noisy data analysis:");
	    int repNoise = Integer.parseInt(br.readLine());
	    
	    System.out.print("Enter amount of tests on different Mealy's: (default in Paper 10)");
	    int repOnDiffMealy = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of tests on the same Mealy: (default in Paper 100)");
	    int repOnSameMealy = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of different variations of training data: (default in Paper 3)");
	    int repTrain = Integer.parseInt(br.readLine());
	    System.out.print("Enter amount of different variations of test data: (default in Paper 3)");
	    int repTest = Integer.parseInt(br.readLine());
	    
	    
	    test.startNoiseMealy(states, sigOut, SigmaIn, numBags, alpha, 
	    trainSize, testSize, trainWLength, testWLength, noiseRatio, repNoise, repOnSameMealy, repOnDiffMealy, 
	    repTrain, repTest);
		} catch(NumberFormatException nfe) {
	      System.err.println("Invalid Format!");
	    }	
	}
	
	private void startRiseTrainAlpha(TestEnvironment test) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.print("Enter Number of DFA States:");
	        int states = Integer.parseInt(br.readLine());    
		    System.out.print("Enter amount of input symbols:");
	        int sigIn = Integer.parseInt(br.readLine());
		    char endsymbolIn = (char) (sigIn + 64);	 
		    Alphabet<Character> SigmaIn = Alphabets.characters('A', endsymbolIn);
		    System.out.print("Enter parameter m in forests: (default in Paper 101)");
		    int numBags = Integer.parseInt(br.readLine());
		    System.out.print("Enter starting alpha for forests in percentage (integer): (default in Paper 1)");
		    int ratioStart = Integer.parseInt(br.readLine());
		    int ratioEnd = 100;
		    System.out.print("Enter stepsize for alpha in percentage (integer): (default in Paper 3)");
		    int ratioStep = Integer.parseInt(br.readLine());
		    System.out.print("Enter amount of Strings for testing: (default in Paper 1000)");
		    int testSize = Integer.parseInt(br.readLine());
		    System.out.print("Enter max. length of training Strings: (default in Paper 2*number of states)");
		    int trainWLength = Integer.parseInt(br.readLine());
		    System.out.print("Enter max. length of test Strings: (default in Paper 2*number of states)");
		    int testWLength = Integer.parseInt(br.readLine());
		    System.out.print("Enter datasize to be added in each check for characteristic set.(for recreation: view datastep parameter in file name)");
		    int trainLengthIter = Integer.parseInt(br.readLine());

		    System.out.print("Enter amount of tests on the same DFA: (default in Paper 100)");
		    int repOnSameDFA = Integer.parseInt(br.readLine());
			System.out.print("Enter amount of max additions of new training data: (default in Paper 50 - corresponds to stop criteria)");
			int maxreps = Integer.parseInt(br.readLine());
			System.out.print("Enter amount of available threads (default 1):");
		    int cores = Integer.parseInt(br.readLine());
			br.close();
			test.startRiseTrainAlpha(states,SigmaIn,numBags,ratioStart,ratioEnd,ratioStep,testSize,trainWLength,trainLengthIter,
					testWLength,repOnSameDFA,maxreps,cores);
		}catch(NumberFormatException e) {
			System.err.println("Invalid Format!");
		} catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
	
	private void startRiseTrainCharact(TestEnvironment test) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.print("Enter Number of DFA States:");
	        int states = Integer.parseInt(br.readLine());    
		    System.out.print("Enter amount of input symbols:");
	        int sigIn = Integer.parseInt(br.readLine());
		    char endsymbolIn = (char) (sigIn + 64);	 
		    Alphabet<Character> SigmaIn = Alphabets.characters('A', endsymbolIn);
		    System.out.print("Enter parameter m in forests: (default in Paper 101)");
		    int numBags = Integer.parseInt(br.readLine());
		    System.out.print("Enter alpha for forests in percentage (integer): (default in Paper 1)");
		    int ratio = Integer.parseInt(br.readLine());
		    double alpha = (double) ratio / 100.0;
		    System.out.print("Enter amount of Strings for testing: (default in Paper 1000)");
		    int testSize = Integer.parseInt(br.readLine());
		    System.out.print("Enter max. length of training Strings: (default in Paper 2*number of states)");
		    int trainWLength = Integer.parseInt(br.readLine());
		    System.out.print("Enter max. length of test Strings: (default in Paper 2*number of states)");
		    int testWLength = Integer.parseInt(br.readLine());
			System.out.print("Enter datasize which is the starting datasize.(default 100)");
			int startTrainSize = Integer.parseInt(br.readLine());
		    System.out.print("Enter datasize to be added in each check for characteristic set.(default 1)");
		    int trainLengthIter = Integer.parseInt(br.readLine());

		    System.out.print("Enter amount of tests on the same DFA: (default in Paper 1)");
		    int repOnSameDFA = Integer.parseInt(br.readLine());
			System.out.print("Enter amount of max additions of new training data: (default in Paper 5.000)");
			int maxreps = Integer.parseInt(br.readLine());
			System.out.print("Enter amount of available threads (default 1):");
		    int cores = Integer.parseInt(br.readLine());
			br.close();
			test.startRiseTrainAlphaStruct(states,SigmaIn,numBags,alpha,testSize,trainWLength,testWLength,startTrainSize,trainLengthIter,
					repOnSameDFA,maxreps,cores);
		}catch(NumberFormatException e) {
			System.err.println("Invalid Format!");
		} catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
	

	private void startTestInterface(TestEnvironment test) {
		TestingInterface debug = new TestingInterface(test);
		debug.startTests();
	}
	
	private Pattern getRegex(int regxCase) {
		switch(regxCase) {
		case 1:
			return regx1;
		case 2:
			return regx2;
		case 3:
			return regx3;
		default:
			System.out.println("given input regex is not defined");
			return null;
		}
	}
}
