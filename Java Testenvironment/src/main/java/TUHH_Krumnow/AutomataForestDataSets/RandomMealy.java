package TUHH_Krumnow.AutomataForestDataSets;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.automata.transducers.impl.compact.CompactMealy;
import net.automatalib.automata.transducers.impl.compact.CompactMealyTransition;
import net.automatalib.util.automata.builders.AutomatonBuilders;
import net.automatalib.util.automata.builders.MealyBuilder;
import net.automatalib.visualization.Visualization;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;

public class RandomMealy {
	
	public MealyMachine<?,Character,?,Character> RandomMealy;
	public Alphabet<Character> sigma;
	public Alphabet<Character> outputsigma;
	
	public RandomMealy(int N, int numOutSymb, Alphabet<Character> _sigma) {
		this.RandomMealy = constructGeneric(N,numOutSymb,_sigma);
		this.sigma = _sigma;
	}
	
	public List<List<String>> getRandomDataSets(int DataSize, int maxStringLength){
		List<List<String>> ret = new ArrayList<List<String>>();
		ret.add(new ArrayList<String>()); //index 0 = Input
		ret.add(new ArrayList<String>());//index 1 = Output
		String tempString;
		for(int i = 0; i < DataSize; i++) {
			tempString = HelperFunctions.RandomStringFromAplhabet(sigma,
					HelperFunctions.getRandomNumInRange(1, maxStringLength));
			ret.get(0).add(tempString);
			ret.get(1).add(HelperFunctions.WordToStringIT(RandomMealy.computeOutput(Word.fromString(tempString))));
		}
		return ret;
	}
	
	public void VisualizeTheRandomMealy() {
		Visualization.visualize(RandomMealy, sigma);
	}
	
	/***
	 * Function which returns a Testset, which is distinct from the trainingsset
	 * @param trainingsset
	 * @param DataSize
	 * @param maxStringlength
	 * @return Testset
	 */
	public List<List<String>> getTestSet(List<String> trainingsset, int DataSize,int maxStringLength){
		List<List<String>> ret = new ArrayList<List<String>>();
		ret.add(new ArrayList<String>()); //index 0 = Input
		ret.add(new ArrayList<String>());//index 1 = Output
		String tempString;
		for(int i = 0; i < DataSize; i++) {
			tempString = HelperFunctions.RandomStringFromAplhabet(sigma,
					HelperFunctions.getRandomNumInRange(1, maxStringLength));
			if(!CompareIsStringElemList(trainingsset, tempString)) {
				ret.get(0).add(tempString);
				//ret.get(1).add(HelperFunctions.FormatMealyString(RandomMealy.computeOutput(Word.fromString(tempString)).toString()));
				ret.get(1).add(HelperFunctions.WordToStringIT(RandomMealy.computeOutput(Word.fromString(tempString))));
			}else {
				i--;
			}
			}
		return ret;
	}
	
	public Boolean CompareIsStringElemList(List<String> list, String comp) {
		for(int j = 0; j < list.size(); j++) {
			if(Objects.equals(comp, list.get(j))) {
				return true;
			}
		}
		return false;
	}
	
	public MealyMachine<?,Character,?,Character> constructGeneric(int N, int numOutSymb, Alphabet<Character> _sigma) {
    	Alphabet<Character> sigma = _sigma;
    	char endsymbol = (char) (numOutSymb + 96);
        this.outputsigma = Alphabets.characters('a', endsymbol);
		ArrayList<Integer> seq = new ArrayList<Integer>(); //List of States
		ArrayList<Integer> notSeen = new ArrayList<Integer>();
    	for(int i = 0; i < N; i++) {
    		notSeen.add(i);
    		seq.add(i);
    	}
    	

        // create automaton
        final CompactMealy<Character, Character> cm = new CompactMealy<>(sigma);
        MealyBuilder<Integer, Character, CompactMealyTransition<Character>, Character, CompactMealy<Character, Character>>.MealyBuilder__1 builder = AutomatonBuilders.forMealy(cm)
                .withInitial("q0");
        
        //random numbers for next state and next out sym
        Random rand_state = new Random(HelperFunctions.getSeed());
        Random rand_out = new Random(HelperFunctions.getSeed());
        Random rand_alpha = new Random(HelperFunctions.getSeed());
        int alpha = 0;
        int rndState = 0;
        char outsym;
        for(int i = 1; i <= seq.size(); i++) {
        	alpha = rand_alpha.nextInt(sigma.size());

        	//loop: jeder input mit zufälligem output zu zufälligem zustand	
        	for(int j = 0; j < sigma.size(); j++) {
        		if(j==alpha) {
        			if(!notSeen.isEmpty()) {
        				outsym = (char) (rand_out.nextInt(numOutSymb) + 97);
        				rndState = rand_state.nextInt(notSeen.size()); //nimmt zufälligen index aus notSeen
        				rndState = notSeen.get(rndState); //nimmt den Statevalue aus notSeen (wir wissen den outputState)
        				builder.from("q" + (i-1)).on(sigma.getSymbol(j)).withOutput(outsym).to("q" + rndState);
        				notSeen.remove(notSeen.indexOf(rndState));
        			}else {
        				rndState = rand_state.nextInt(N);
                		outsym = (char) (rand_out.nextInt(numOutSymb) + 97);
                		builder.from("q" + (i-1)).on(sigma.getSymbol(j)).withOutput(outsym).to("q" + rndState);
        			}
        		}else{
        			rndState = rand_state.nextInt(N);
            		outsym = (char) (rand_out.nextInt(numOutSymb) + 97);
            		builder.from("q" + (i-1)).on(sigma.getSymbol(j)).withOutput(outsym).to("q" + rndState);
            		if(notSeen.contains(rndState)) {
            			notSeen.remove(notSeen.indexOf(rndState));
            		}
        		}
        	}
        }
        
        return builder.create();
    }
}
