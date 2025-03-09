package TUHH_Krumnow.AutomataForestDataSets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import de.learnlib.oracle.equivalence.SimulatorEQOracle;
import net.automatalib.util.automata.equivalence.DeterministicEquivalenceTest;
import de.learnlib.oracle.membership.SimulatorOracle;
import net.automatalib.automata.fsa.DFA;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

/**
 * Class that extracts a characteristic Set of strings, given some known input Mealy Machine.
 * The algorithms are extracted from the paper: "Polynomial characteristic sets for DFA identification;
 * Pedro García, Damián López, Manuel Vázquez de Parga"
 * In Difference to inference of a characteristic set for DFA, here in the implementation of algorithm 3.2
 * the matrix T stores the translations of words (in contrast to membership) and the algorithm continues (while loop), till
 * every output word is distinct in matrix T.
 * @author Arne Krumnow
 *
 */
public class CharacteristicSetMealy {
	private MealyMachine<?, Character, ?, Character> target;
	private Alphabet<Character> inAlphabet;
	private List<String> testStates;
	private List<String> sPrime;
	private List<List<String>> characSet;
	
	public CharacteristicSetMealy( MealyMachine<?, Character, ?, Character> _target, Alphabet<Character> _inAlphabet) {
		target = _target;
		inAlphabet = _inAlphabet;
		testStates = new ArrayList<String>();
		sPrime = new ArrayList<String>();
		characSet = new ArrayList<List<String>>();
		characSet.add(new ArrayList<String>());	//index 0 stores input words elem charact. set
		characSet.add(new ArrayList<String>());	//index 1 stores output words elem charact. set
	}
	
	
	private void fillTestStates_CanonicOrder() {
		testStates.add(""); //supposed to be empty word to get initial state into testStates 
		int testInd = 0;
		Boolean stateR;
		while(target.size() > testStates.size()) { //iterate till all states reached / "while not all states are reached"
			
			for(int i = 0; i < inAlphabet.size(); i++) { //iterate over alphabet
				stateR = false;
				StringBuilder next = new StringBuilder(testStates.get(testInd));
				next.append(inAlphabet.getSymbol(i));
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
	}
	
	private void obtainSPrime(){
		for(int i = 0; i < testStates.size(); i++) { //double loop to obtain: (S + Sigma) / S
			for(int j = 0; j < inAlphabet.size(); j++) {
				StringBuilder s = new StringBuilder(testStates.get(i));
				s.append(inAlphabet.getSymbol(j));
				if(!(testStates.contains(s.toString()))) {
					sPrime.add(s.toString());
				}
			}
		}
	}
	
	/**
	 * Algorithm 3.2 to obtain the characteristic set for a language L. From: "Polynomial characteristic sets for DFA identification;
	 * Pedro García"
	 * In Difference to inference of a characteristic set for DFA, here in the implementation of algorithm 3.2
	 * the matrix T stores the translations of words (in contrast to membership) and the algorithm continues (while loop), till
	 * every output word is distinct in matrix T.
	 * @return 
	 */
	public List<List<String>> getCharacteristicSet(){
		fillTestStates_CanonicOrder();
		obtainSPrime();
		List<List<String>> T = buildMatrixT();
		int i = 1;
		while(checkExistUndistinguishedStrings(T)) {
			addElemToT(T,getSymbolChainA(i));
			i++;
		}
		
		fillCharacteristicSet(T,characSet);
		return characSet;
	}
	
	private List<List<String>> buildMatrixT(){ //builds starting matrix T
		List<List<String>> T = new ArrayList<List<String>>();
		for(int i = 0; i < testStates.size(); i++) {
			T.add(new ArrayList<String>()); //index represents strings in S; List gets filled with output Strings elem E
			//adds lambda as first column:
			
			T.get(i).add(HelperFunctions.WordToStringIT(target.computeOutput(HelperFunctions.StringToWord(testStates.get(i))))); 
		}
		for(int i = 0; i < sPrime.size(); i++) {
			T.add(new ArrayList<String>()); //index represents strings in SPrime; List gets filled with output Strings elem E
			//adds lambda as first column:
			
			T.get(i + testStates.size()).add(HelperFunctions.WordToStringIT(target.computeOutput(HelperFunctions.StringToWord(sPrime.get(i)))));
		}
		return T;
	}
	
	private Boolean checkExistUndistinguishedStrings(List<List<String>> T) {
		for(int i = 0; i < testStates.size() - 1; i++) {
			for(int j = i + 1; j < testStates.size(); j++) {
				if(singleRowCheck(T,i,j)) {
					return true; //there exist 2 indistinguished rows: continue while loop
				}
			}
		}
		return false; //all rows are distinguished => extract charact. set
	}
	
	private Boolean singleRowCheck(List<List<String>> matrix, int ind1, int ind2) { //checks if two rows are the same
		for(int i = 0; i < matrix.get(ind1).size(); i++) {
			if(!Objects.equals(matrix.get(ind1).get(i),matrix.get(ind2).get(i))) { //if there exist 2 diff. indeces in rows => rows are unequal
				return false;
			}
		}
		return true; //rows are equal
	}
	
	private void addElemToT(List<List<String>> T, String a) { //adds a character column to T
		String temp = null;
		for(int i = 0; i < T.size(); i++) {
			if(i < testStates.size()) {
				temp = testStates.get(i) + a;
				T.get(i).add(HelperFunctions.WordToStringIT(target.computeOutput(HelperFunctions.StringToWord(temp))));
			}else {
				temp = sPrime.get(i - testStates.size()) + a;
				T.get(i).add(HelperFunctions.WordToStringIT(target.computeOutput(HelperFunctions.StringToWord(temp))));
			}
		}
	}
	
	private void fillCharacteristicSet(List<List<String>> T, List<List<String>> Data) {
		//filling D out of T		
		for(int i = 0; i < testStates.size(); i++) {
			for(int j = 0; j < T.get(i).size(); j++) {
				Data.get(0).add(testStates.get(i) + getSymbolChainA(j));
				Data.get(1).add(T.get(i).get(j));
			}
		}
		for(int i = 0; i < sPrime.size(); i++) {
			for(int j = 0; j < T.get(i + testStates.size()).size(); j++) {
				Data.get(0).add(sPrime.get(i) + getSymbolChainA(j));
				Data.get(1).add(T.get(testStates.size() + i).get(j));
			}
		}
		
		//checking for duplicates
		deleteDuplicatesInList(Data.get(0),Data.get(1));
	}
	
	private void deleteDuplicatesInList(List<String> input, List<String> output) {
		
		Set<String> set = new HashSet<String>();
		int max = input.size();
		for(int i = 0; i < max; i++) {
			if (!set.add(input.get(i))) { //item i is already present in list
		        input.remove(i); //removes item i
		        output.remove(i);
		        max = input.size(); //shifts max position for the loop, avoid oob exception
		       	i--; //decrease position pointer after removal
		    }
		}
		
	}
	
	public Boolean checkListForCharacSet(List<String> in) {
		for(int i = 0; i < characSet.get(0).size(); i++) { //iterates over positive set
			if(!isPrefixInList(in, characSet.get(0).get(i))) { 
				return false; //if the characteristic string i is not prefix in assumpted positive set: return false.
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
	
	private String getSymbolChainA(int index) {
		StringBuilder ret = new StringBuilder();
		int temp = 0;
		
		while(index >= 1) {
			temp = (index - 1) % inAlphabet.size();
			ret.append(inAlphabet.getSymbol(temp));
			index = (int) (index / inAlphabet.size());
		}
		return ret.reverse().toString();
	}
	
	
	
}
