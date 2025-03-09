package TUHH_Krumnow.AutomataForestDataSets;

import java.util.*;

import net.automatalib.automata.fsa.DFA;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.util.automata.builders.AutomatonBuilders;
import net.automatalib.util.automata.builders.DFABuilder;
import net.automatalib.visualization.Visualization;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

public class RandomDFA {
	
	public DFA<?,Character> randomDFA;
	public Alphabet<Character> sigma;
	
	public RandomDFA(int states, Alphabet<Character> _sigma) {
		sigma = _sigma;
		randomDFA = constructGeneric(states);
	}
	
	private DFA<?,Character> constructGeneric2(int states){
		SplittableRandom rand_state = new SplittableRandom(HelperFunctions.getSeed());
		SplittableRandom rand_accpt = new SplittableRandom(HelperFunctions.getSeed());
        CompactDFA<Character> cd = new CompactDFA<>(sigma); 
    	DFABuilder<Integer, Character, CompactDFA<Character>>.DFABuilder__1 builder = AutomatonBuilders.forDFA(cd)
                .withInitial("q0");
    	ArrayList<Integer> seq = new ArrayList<Integer>(); //List of States
		//ArrayList<Integer> notSeen = new ArrayList<Integer>();
		Boolean control = true;
		Boolean complDFA = false;
		int rndState = 0;
		while(control || !complDFA) {
			control = true;
			complDFA = false;
			seq = new ArrayList<Integer>();
			cd = new CompactDFA<>(sigma); 
			builder = AutomatonBuilders.forDFA(cd)
	                .withInitial("q0");
			for(int i = 0; i < states; i++) {
	    		//notSeen.add(i);
	    		seq.add(i);
	    	}
			for(int i = 0; i < seq.size(); i++) { //loop to lay transitions
				for(int j = 0; j < sigma.size(); j++) {
					rndState = rand_state.nextInt(states);
        			builder.from("q" + (i)).on(sigma.getSymbol(j)).to("q" + rndState);
				}
			}
			for(int i = 0; i < seq.size(); i++) { //loop to decide if state is accepting
        		if(rand_accpt.nextBoolean()) {
        			builder.withAccepting("q" + i);
        			control = false; //variable to make sure automaton has atleast one accepting state
        		}
        	}
			complDFA = checkTestStates_CanonicOrder(states, builder.create());
		}
		randomDFA = builder.create();
		return randomDFA;
	}
	
	private DFA<?,Character> constructGeneric(int states){
		ArrayList<Integer> seq = new ArrayList<Integer>(); //List of States
		ArrayList<Integer> notSeen = new ArrayList<Integer>();
    	for(int i = 0; i < states; i++) {
    		notSeen.add(i);
    		seq.add(i);
    	}
    	
    	CompactDFA<Character> cd = new CompactDFA<>(sigma); 
    	DFABuilder<Integer, Character, CompactDFA<Character>>.DFABuilder__1 builder = AutomatonBuilders.forDFA(cd)
                .withInitial("q0");
    	
    	//random numbers for next state and accepting state
        Random rand_state = new Random(HelperFunctions.getSeed());
        Random rand_accpt = new Random(HelperFunctions.getSeed());
        Random rand_alpha = new Random(HelperFunctions.getSeed());
        int alpha = 0;
        int rndState = 0;
        Boolean accpt;
        Boolean control = true;
        while(control || !notSeen.isEmpty()) { //repeat process until there exist accepting states & every state can be reached
        	//i.e. both control = false (accpt exist) and notSeen is empty => while there exist no accpt or while notSeen is not empty
        	
        	alpha = 0;
        	rndState = 0;
        	control = true;
        	cd = new CompactDFA<>(sigma);
        	builder = AutomatonBuilders.forDFA(cd).withInitial("q0");
        	
        	notSeen = new ArrayList<Integer>();
        	seq = new ArrayList<Integer>();
        	for(int i = 0; i < states; i++) {
        		notSeen.add(i);
        		seq.add(i);
        	}
        	
        	for(int i = 1; i <= seq.size(); i++) {
        		alpha = rand_alpha.nextInt(sigma.size());

        		//loop: jeder input mit zufälligem output zu zufälligem zustand	
        		for(int j = 0; j < sigma.size(); j++) {
        			if(j==alpha) {
        				if(!notSeen.isEmpty()) {
        					rndState = rand_state.nextInt(notSeen.size()); //nimmt zufälligen index aus notSeen
        					rndState = notSeen.get(rndState); //nimmt den Statevalue aus notSeen (wir wissen den outputState)
        					
        					builder.from("q" + (i-1)).on(sigma.getSymbol(j)).to("q" + rndState);
        					if(rndState != i-1) { //if targetstate is notSeen & selftransition occurs => notSeen can't remove 
        						//state as its not seen outside of its self
        						notSeen.remove(notSeen.indexOf(rndState));
        					}
        				}else {
        					rndState = rand_state.nextInt(states);
                			//outsym = (char) (rand_out.nextInt(numOutSymb) + 97);
                			builder.from("q" + (i-1)).on(sigma.getSymbol(j)).to("q" + rndState);
        				}
        			}else{
        				rndState = rand_state.nextInt(states);
            			//outsym = (char) (rand_out.nextInt(numOutSymb) + 97);
            			builder.from("q" + (i-1)).on(sigma.getSymbol(j)).to("q" + rndState);
            			if(notSeen.contains(rndState)) {
            				notSeen.remove(notSeen.indexOf(rndState));
            			}
        			}
        		}
        	}
        	for(int i = 0; i < seq.size(); i++) { //loop to decide if state is accepting
        		if(rand_accpt.nextBoolean()) {
        			builder.withAccepting("q" + i);
        			control = false; //variable to make sure automaton has atleast one accepting state
        		}
        	}
        	randomDFA = builder.create();
        }

        return builder.create();
	}
	
	
	
	public List<List<String>> getRandomDataSets(int DataSize, int maxStringLength){
		List<List<String>> ret = new ArrayList<List<String>>();
		ret.add(new ArrayList<String>()); //index 0 = positive dataset
		ret.add(new ArrayList<String>());//index 1 = negative dataset
		//adding the empty string initially:
		if(getEmptyString()) {
			ret.get(0).add("");
		}else {
			ret.get(1).add("");
		}
		String tempString;
		while(ret.get(0).size()+ret.get(1).size() < DataSize) {
			tempString = HelperFunctions.RandomStringFromAplhabet(sigma,
					HelperFunctions.getRandomNumInRange(1, maxStringLength,true),true);
			
			if(!CompareIsStringElemList(ret, tempString)) {
				if(randomDFA.accepts(Word.fromCharSequence(tempString))) {
					ret.get(0).add(tempString);
				}else {
					ret.get(1).add(tempString);
				}
			}
		}
		return ret;
	}
	
	public void getRandomDataSets(List<List<String>> train, int DataSize, int maxStringLength){
		int count = 0;
		String tempString;
		while(count < DataSize) {
			tempString = HelperFunctions.RandomStringFromAplhabet(sigma,
					HelperFunctions.getRandomNumInRange(1, maxStringLength,true),true);
			if(!CompareIsStringElemList(train, tempString)) {
				if(randomDFA.accepts(Word.fromCharSequence(tempString))) {
					train.get(0).add(tempString);
					count++;
				}else {
					train.get(1).add(tempString);
					count++;
				}
			}
		}
	}
	
	public Boolean getEmptyString() {
		if(randomDFA.accepts(Word.fromString(""))) {
			return true;
		}else {
			return false;
		}
	}
	
	public void VisualizeTheRandomDFA() {
		Visualization.visualize(randomDFA, sigma);
	}
	
	/***
	 * Function which returns a Testset, which is distinct from the trainingsset
	 * @param trainingsset
	 * @param DataSize
	 * @param maxStringlength
	 * @return Testset
	 */
	public List<List<String>> getTestSet(List<List<String>> trainingsset, int DataSize,int maxStringLength){
		List<List<String>> ret = new ArrayList<List<String>>();
		ret.add(new ArrayList<String>()); //index 0 = postive test words
		ret.add(new ArrayList<String>());//index 1 = negative test words
		String tempString;
		for(int i = 0; i < DataSize; i++) {
			tempString = HelperFunctions.RandomStringFromAplhabet(sigma,
					HelperFunctions.getRandomNumInRange(1, maxStringLength));
			if(!CompareIsStringElemList(trainingsset, tempString)) {
				if(randomDFA.accepts(Word.fromCharSequence(tempString))) {
					ret.get(0).add(tempString);
				}else {
					ret.get(1).add(tempString);
				}
			}else {
				i--;
			}
			}
		return ret;
	}
	
	private Boolean CompareIsStringElemList(List<List<String>> list, String comp) {
		for(int j = 0; j < list.get(0).size(); j++) {
			if(Objects.equals(comp, list.get(0).get(j))) {
				return true;
			}
		}
		for(int j = 0; j < list.get(1).size(); j++) {
			if(Objects.equals(comp, list.get(1).get(j))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * check if all states can be reached starting from the initial state
	 * @param states
	 * @param target
	 * @return
	 */
	private Boolean checkTestStates_CanonicOrder(int states, DFA<?,Character> target) {
		List<String> testStates = new ArrayList<String>();
		testStates.add(""); //supposed to be empty word to get initial state into testStates 
		int testInd = 0;
		Boolean stateR;
		while(states > testStates.size() 
				&& testStates.get(testStates.size() - 1).length() < states + 1) { //iterate till all states reached / "while not all states are reached"
			if(testStates.size() <= testInd) {
				return false;
			}
			for(int i = 0; i < sigma.size(); i++) { //iterate over alphabet
				stateR = false;
				StringBuilder next = new StringBuilder(testStates.get(testInd));
				next.append(sigma.getSymbol(i));
				//at index, get string, where testState.get(testInd).length <= testState.get(testInd++).length
				for(int j = 0; j < testStates.size(); j++) { //check if state is reached within known testStates
					if(target.getState(HelperFunctions.StringToWord(testStates.get(j))) == 
							target.getState(HelperFunctions.StringToWord(next.toString()))) {
						stateR = true; //if state can already be reached
						break;
					}
				}
				if(!stateR) { //state cannot be reached with already added testState Strings
					testStates.add(next.toString());
				}
			}
			testInd++;
		}
		if(states == testStates.size()) {
			return true;
		}else {
			return false;
		}
	}
}
