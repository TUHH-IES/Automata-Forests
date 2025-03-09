package TUHH_Krumnow.AutomataForestDataSets;

import java.util.List;
import java.util.Objects;

import net.automatalib.automata.fsa.DFA;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.visualization.Visualization;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;

public class TestingInterface {
	
	private TestEnvironment test;
	
	public TestingInterface(TestEnvironment _test) {
		test = _test;
	}
	
	public void startTests() {
		Alphabet<Character> sigma = Alphabets.characters('0', '9');
		//RandomMealy test = new RandomMealy(10,10,sigma);
		//test.VisualizeTheRandomMealy();
		//System.out.println(test.RandomMealy.getInitialState());
		//CharacteristicSetMealy charact = new CharacteristicSetMealy(test.RandomMealy,sigma);
		//List<List<String>> setTarget = charact.getCharacteristicSet();
		////for(int i = 0; i < setTarget.get(0).size(); i++) {
		////	System.out.println(setTarget.get(0).get(i) + "  +  " + setTarget.get(1).get(i));
		////}
		//MealyMachine<?,Character,?,Character> testRPNI = HelperFunctions.computeModelMealyBlueFringe(sigma, 
		//		setTarget.get(0), setTarget.get(1));
		//if(testRPNI.equals(test.RandomMealy)) {
		//	System.out.println("");
		//}
		//System.out.println(testRPNI.getStates().size() + "  " + test.RandomMealy.getStates().size());
		//List<List<String>> testCase  = test.getRandomDataSets(1000, 40);
		//int score = 0;
		//score = HelperFunctions.CompareWithModelWithTestSet(testRPNI, testCase.get(0), testCase.get(1));
		
		
		//RandomDFA test = new RandomDFA(10,sigma);
		//test.VisualizeTheRandomMealy();
		
		//CharacteristicSetDFA charact = new CharacteristicSetDFA(test.randomDFA,sigma);
		//List<List<String>> setTarget = charact.getCharacteristicSet();
		//DFA<?, Character> testDFA = HelperFunctions.computeModelBlueFringe(sigma, HelperFunctions.transformFromListToCollection(setTarget.get(0)),
		//		HelperFunctions.transformFromListToCollection(setTarget.get(1)));
		//if(testDFA.equals(test.randomDFA)) {
		//	System.out.println("");
		//}
		//System.out.println(testDFA.getStates().size() + "  " + test.randomDFA.getStates().size());
		//List<List<String>> testCase  = test.getRandomDataSets(1000000, 40); //1.000.000
		int score = 0;
		int eqCharacSet = 0;
		for(int i = 0; i < 1000; i++) { //1000

			RandomDFA test = new RandomDFA(6,sigma);
			test.VisualizeTheRandomDFA();
			CharacteristicSetDFA charact = new CharacteristicSetDFA(test.randomDFA,sigma);
			List<List<String>> setTarget = charact.getCharacteristicSet();
			
			DFA<?, Character> testDFA = HelperFunctions.computeModelBlueFringe(sigma, HelperFunctions.transformFromListToCollection(setTarget.get(0)),
					HelperFunctions.transformFromListToCollection(setTarget.get(1)));
			
			//DFA<?, Character> testDFA = HelperFunctions.computeModelBlueFringeEDSM(sigma, HelperFunctions.transformFromListToCollection(setTarget.get(0)),
			//		HelperFunctions.transformFromListToCollection(setTarget.get(1)));
			//AutomataForestDFA forest = new AutomataForestDFA(sigma, setTarget.get(0),setTarget.get(1),
			//		0.6,100);
			//AutomataForestDFA forest2 = new AutomataForestDFA(sigma, setTarget.get(0),setTarget.get(1),
			//		0.6,100,"EDSM");
			List<List<String>> testCase  = test.getRandomDataSets(10000, 40); //10.000
			CharacteristicSetDFA charact2 = new CharacteristicSetDFA(testDFA,sigma);
			List<List<String>> setTarget2 = charact2.getCharacteristicSet();
			if(charact.checkListForCharacSet(setTarget2.get(0), setTarget2.get(1)) && 
					charact2.checkListForCharacSet(setTarget.get(0), setTarget.get(1))) {
				eqCharacSet++; //equivalence check for charact. sets (RPNI and target have same charact. set) 
				//funktioniert wahrscheinlich nicht immer, da target nicht zwingend minimal ist.
			}
			if(10000 == HelperFunctions.CompareWithModelWithTestSet(testDFA, testCase.get(0), testCase.get(1))) {
				score++;
				System.out.println(i);
			}else {
				System.out.println("not: " + i);
			}
		}
		System.out.println("Done! :)");
		System.out.println("test score: "+score);
		System.out.println("equal charact. set: "+eqCharacSet);
	}
	
	
}
