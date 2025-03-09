package TUHH_Krumnow.AutomataForestDataSets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.automatalib.automata.fsa.DFA;
import net.automatalib.graphs.Graph;
import net.automatalib.util.automata.equivalence.DeterministicEquivalenceTest;
import net.automatalib.visualization.Visualization;
import net.automatalib.words.Alphabet;

/**
 * Class that extracts a characteristic Set of strings, given some known input DFA.
 * The algorithms are extracted from the paper: "Polynomial characteristic sets for DFA identification;
 * Pedro García, Damián López, Manuel Vázquez de Parga"
 * @author Arne Krumnow
 *
 */
public class CharacteristicSetDFA {
	protected DFA<?,Character> target;
	protected List<String> testStates;
	protected Alphabet<Character> alphabet;
	protected List<String> sPrime;
	public List<List<String>> characSet;
	private int depth;
	
	public CharacteristicSetDFA(DFA<?,Character> _target, Alphabet<Character> _alphabet) {
		target = _target;
		alphabet = _alphabet;
		sPrime = new ArrayList<String>();
		characSet = new ArrayList<List<String>>();
		characSet.add(new ArrayList<String>()); //positive set
		characSet.add(new ArrayList<String>()); //negative set
		depth = 0;
	}
	
	private void fillTestStates_CanonicOrder() {
		testStates = new ArrayList<String>();
		testStates.add(""); //supposed to be empty word to get initial state into testStates 
		int testInd = 0;
		Boolean stateR;
		while(target.size() > testStates.size()) { //iterate till all states reached / "while not all states are reached"
			
			for(int i = 0; i < alphabet.size(); i++) { //iterate over alphabet
				stateR = false;
				//Bugfixing here:
				
				StringBuilder next = new StringBuilder(testStates.get(testInd));
				next.append(alphabet.getSymbol(i));
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
		depth = testStates.get(testStates.size() - 1).length();
	}
	
	private void fillTestStates_PrefixClosedOrder() {
		testStates.add(""); //supposed to be empty word to get initial state into testStates => needs to be tested
		int testInd = 0;
		Boolean stateR = false;
		while(target.size() >= testStates.size()) { //iterate till all states reached
			
		}
	}
	
	private void obtainSPrime(){
		for(int i = 0; i < testStates.size(); i++) { //double loop to obtain: (S + Sigma) / S
			for(int j = 0; j < alphabet.size(); j++) {
				StringBuilder s = new StringBuilder(testStates.get(i));
				s.append(alphabet.getSymbol(j));
				if(!(testStates.contains(s.toString()))) {
					sPrime.add(s.toString());
				}
			}
		}
	}
	
	/**
	 * Algorithm 3.2 to obtain the characteristic set for a language L. From: "Polynomial characteristic sets for DFA identification;
	 * Pedro García"
	 * @return 
	 */
	public List<List<String>> getCharacteristicSet(){
		fillTestStates_CanonicOrder();
		obtainSPrime();
		List<List<Boolean>> T = buildMatrixT();
		int i = 1;
		while(checkExistUndistinguishedStrings(T)) {
			addElemToT(T,getSymbolChainA(i));
			i++;
		}
		fillCharacteristicSet(T,characSet);
		return characSet;
	}
	
	private List<List<Boolean>> buildMatrixT(){ //builds starting matrix T
		List<List<Boolean>> T = new ArrayList<List<Boolean>>();
		for(int i = 0; i < testStates.size(); i++) {
			T.add(new ArrayList<Boolean>()); //index represents strings in S; List gets filled with {0,1} elem E
			T.get(i).add(target.accepts(HelperFunctions.StringToWord(testStates.get(i)))); //adds lambda as first column
		}
		for(int i = 0; i < sPrime.size(); i++) {
			T.add(new ArrayList<Boolean>()); //index represents strings in SPrime; List gets filled with {0,1} elem E
			T.get(i + testStates.size()).add(target.accepts(HelperFunctions.StringToWord(sPrime.get(i)))); //adds lambda as first column
		}
		return T;
	}
	
	private Boolean singleRowCheck(List<List<Boolean>> matrix, int ind1, int ind2) { //checks if two rows are the same
		for(int i = 0; i < matrix.get(ind1).size(); i++) {
			if(matrix.get(ind1).get(i) != matrix.get(ind2).get(i)) { //if there exist 2 diff. indeces in rows => rows are unequal
				return false;
			}
		}
		return true; //rows are equal
	}
	
	private void addElemToT(List<List<Boolean>> T, String a) { //adds a character column to T
		String temp = null;
		for(int i = 0; i < T.size(); i++) {
			if(i < testStates.size()) {
				temp = testStates.get(i) + a;
				T.get(i).add(target.accepts(HelperFunctions.StringToWord(temp)));
			}else {
				temp = sPrime.get(i - testStates.size()) + a;
				T.get(i).add(target.accepts(HelperFunctions.StringToWord(temp)));
			}
		}
	}
	
	private String getSymbolChainA(int index) {
		StringBuilder ret = new StringBuilder();
		int temp = 0;
		
		while(index >= 1) {
			temp = (index - 1) % alphabet.size();
			ret.append(alphabet.getSymbol(temp));
			index = (int) (index / alphabet.size());
		}
		return ret.reverse().toString();
	}
	
	private Boolean checkExistUndistinguishedStrings(List<List<Boolean>> T) {
		for(int i = 0; i < testStates.size() - 1; i++) {
			for(int j = i + 1; j < testStates.size(); j++) {
				if(singleRowCheck(T,i,j)) { 
					return true; //there exist 2 indistinguished rows: continue while loop
				}
			}
		}
		return false; //all rows are distinguished => extract charact. set
	}
	
	private void fillCharacteristicSet(List<List<Boolean>> T, List<List<String>> Data) {
		//filling D out of T
		for(int i  = 0; i < T.size(); i++) { //iterating x axis
			for(int j = 0; j < T.get(0).size(); j++) { //iterating y axis
				if(i < testStates.size()) {
					if(T.get(i).get(j) == true) {
						Data.get(0).add(testStates.get(i) + getSymbolChainA(j));
					}else {
						Data.get(1).add(testStates.get(i) + getSymbolChainA(j));
					}
				}else {
					if(T.get(i).get(j) == true) {
						Data.get(0).add(sPrime.get(i - testStates.size()) + getSymbolChainA(j));
					}else {
						Data.get(1).add(sPrime.get(i - testStates.size()) + getSymbolChainA(j));
					}
				}
			}
		}
		//checking for duplicates
		deleteDuplicatesInList(Data.get(0));
		deleteDuplicatesInList(Data.get(1));
	}
	
	private void deleteDuplicatesInList(List<String> input) {
		Set<String> set = new HashSet<String>();
		int max = input.size();
        for(int i = 0; i < max ; i++) {
            if (!set.add(input.get(i))) { //item i is already present in list
            	input.remove(i); //removes item i
            	max = input.size(); //shifts max position for the loop, avoid oob exception
            	i--; //decrease position pointer after removal
            }
        }
	}
	
	public Boolean checkListForCharacSet(List<String> pos, List<String> neg) {
		for(int i = 0; i < characSet.get(0).size(); i++) { //iterates over positive set
			if(!isPrefixInList(pos, characSet.get(0).get(i))) { 
				return false; //if the characteristic string i is not prefix in assumpted positive set: return false.
			}
		}
		for(int i = 0; i < characSet.get(1).size(); i++) { //same for negative set
			if(!isPrefixInList(neg, characSet.get(1).get(i))) {
				return false;
			}
		}
		return true;
	}
	
	private Boolean isPrefixInList(List<String> input, String prefix) {
		for(int i = 0; i < input.size(); i++) {
			if(input.get(i).startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}
	
	public Integer getNumberOfCharact() {
		return this.characSet.get(0).size() + this.characSet.get(1).size();
	}
	
	public Boolean oracleCheck(DFA<?,Character> input) {
		//DeterministicEquivalenceTest<Character> check = new DeterministicEquivalenceTest<Character>(target);
		if(null == DeterministicEquivalenceTest.findSeparatingWord(target,input, alphabet)) {
			return true;
		}
		return false;
	}

	public int getDepth(){
		fillTestStates_CanonicOrder();
		return depth;
	}
	
}
