package TUHH_Krumnow.AutomataForestDataSets;

import java.lang.Object;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ThreadLocalRandom;


import com.mifmif.common.regex.Generex;
import com.mifmif.common.regex.util.Iterator;

import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;

//Klasse des Generators
//dieser erzeugt alle Samples für die Algorithmen
//das Paket Generex wird benutzt

//es wird empfohlen den zweiten Konstruktor zu Beginn zu benutzen
public class GeneratingSets {

	//private Parameter der Klasse:
	//die beiden Length Parameter sind für die Länge der Positiven Samples zuständig:
	//length gibt die Anzahl der positiven Samples
	private int length;
	//gibt einen bestimmten Wert für die positiven Samples
	private int setLengthMax;
	//regex zur generierung
	private Pattern regex; 
	//Alphabet für die RPNI Algorithmen zurechtgeschnitten
	private Alphabet<Character> alphabet;
	private ArrayList<Character> alphabetAsArray;
	private char alphstartchar;
	private char alphendchar;
	//return Samples
	private List<String> PositiveSamples;
	private List<String> NegativeSamples;
	private int WordLengthMax;
	private double negMultiplier;
	
	//Konstruktor mit: - festgelegter Länge der positiven Samples
	//				   - regex auf der Samples erzeugt werden
	//				   - Multiplikator über dem die Anzahl der negativen Samples festgelegt wird 
	public GeneratingSets(int _length, Pattern regx, double multiplier) {
		this.setLengthMax = _length;
		this.regex = regx;
		this.negMultiplier = multiplier;
		generateAllPositiveSamples();
		generateAlphabet();
		getWordLengthMax();
		generateNegativeSamples();
		//generateAllNegativeSamples();
	}
	
	//Konstruktor mit: - regex auf der Samples erzeugt werden
	//   			   - Multiplikator über dem die Anzahl der negativen Samples festgelegt wird 
	public GeneratingSets(Pattern regx,double multiplier) {
		this.setLengthMax = 0;
		this.regex = regx;
		this.negMultiplier = multiplier;
		generateAllPositiveSamples();
		generateAlphabet();
		getWordLengthMax();
		generateNegativeSamples();

	}
	
	//Konstruktor mit: - regex auf der Samples erzeugt werden
	//   			   - Multiplikator über dem die Anzahl der negativen Samples festgelegt wird 
	//				   - Bool für regex7 ähnliche Sprachen. ohne den Bool gab es Probleme bei der Generierung
	public GeneratingSets(Pattern regx,double multiplier,Boolean forregx7Type) {
		this.setLengthMax = 0;
		this.regex = regx;
		this.negMultiplier = multiplier;
		generateAllPositiveSamples();
		generateAlphabet();
		getWordLengthMax();
		generateNegativeSamples(forregx7Type);

	}
	
	public GeneratingSets(Pattern regx,int negSample, int stringMaxLength) {
		this.setLengthMax = 0;
		this.regex = regx;
		this.WordLengthMax = stringMaxLength;
		generateAllPositiveSamples();
		generateAlphabet();
		generateNegativeSamples(negSample);
	}
	
	//Konstruktor mit: - regex auf der Samples erzeugt werden
	//   			   - Bool der angibt ob eine regex alle negativen Samples bis zu einer maxWortlänge der positiven Samples+1 erzeugt
	//			Ist mit Vorsicht zu genießen!
	//		Laufzeit ist bei schwereren regexAusdrücken enorm und es werden Fehler in der Rekursion auftreten
	//    sehr Erroranfällig
	//bitte nur für kleine Sprachen benutzen
	public GeneratingSets(Pattern regx, Boolean allnegSets) {
		this.setLengthMax = 0;
		this.regex = regx;
		
		generateAllPositiveSamples();
		generateAlphabet();
		getWordLengthMax();
		if(allnegSets == true) {
			generateAllNegativeSamples();
		}else {
			generateNegativeSamples();
		}
	}
	
	//generiert alle positiven Samples mittels generex
	//falls die Anzahl bestimmt wurde, entfernt die Methode zum Ende überschüssige Strings
	public void generateAllPositiveSamples(){
		Generex generex = new Generex(this.regex.toString());
		
		List<String> matchedStrings = generex.getAllMatchedStrings();
		Collections.shuffle(matchedStrings, new Random(HelperFunctions.getSeed()));
		if(this.setLengthMax == 0) {
			this.length = matchedStrings.size();
			this.PositiveSamples = matchedStrings;
			
		}
		else if(setLengthMax > matchedStrings.size()) {
			System.out.println("given max size of positive samples is bigger than possible samples");
			System.out.println("new size of generated positive strings is: " + matchedStrings.size());
			this.length = matchedStrings.size();
			this.PositiveSamples = matchedStrings;
		}else {
			this.length = this.setLengthMax;
			while(matchedStrings.size() > length) {
				matchedStrings.remove(HelperFunctions.getRandomNumInRange(0, matchedStrings.size() - 1));
			}
			
			this.PositiveSamples = new ArrayList<String>();
			this.PositiveSamples = matchedStrings;
			
		}
		
	}
	
	//generiert über Rekursion sämtliche negativen Samples
	//mit Vorsicht genießen!
	//siehe Konstruktor mit dem Bool allnegSets
	public void generateAllNegativeSamples() {
		List<String> negativeSample = new ArrayList<String>();
		
		Matcher search;
		char[] alphabet = new char[this.alphabetAsArray.size()];
		for(int i = 0;i<this.alphabetAsArray.size();i++) {
			alphabet[i] = this.alphabetAsArray.get(i);
		}
		
		for(int i = 1;i<this.WordLengthMax;i++) {
			getAlphabetPermutationRec(alphabet,"",i,alphabet.length,negativeSample);
			System.out.println("Rekursiver Aufruf an Stelle " + i + " von: " + this.WordLengthMax + " überstanden!");
		}
		for(int i = 0;i<negativeSample.size();i++) {
			search = this.regex.matcher(negativeSample.get(i));
			if (true==search.find()) {
				negativeSample.remove(i);
				i=i-1;
			}
			System.out.println("Regex überprüfung an stelle: " + i + " von: " + negativeSample.size());
		}
		
		Collections.shuffle(negativeSample, new Random(HelperFunctions.getSeed()));
		this.NegativeSamples = new ArrayList<String>();
		this.NegativeSamples = negativeSample;
	}
	
	//generiert die negativen Samples in Abhängigkeit vom Multiplier
	public void generateNegativeSamples(){
		List<String> negativeSample = new ArrayList<String>();
		String temp = "";
		List<Character> alph= this.alphabetAsArray;
		int randomWordLength;
		Matcher search;
		
		
		while(negativeSample.size() < (int) this.length*this.negMultiplier) {
			randomWordLength = HelperFunctions.getRandomNumInRange(1, this.WordLengthMax);
			for(int i = 0; i < randomWordLength; i++) {
				temp = temp + alph.get(HelperFunctions.getRandomNumInRange(0, alph.size() - 1));
			}
			search = this.regex.matcher(temp);
			if (false == search.find() && negativeSample.contains(temp) == false) {
				negativeSample.add(temp);
			}
			temp = "";
		}
		Collections.shuffle(negativeSample, new Random(HelperFunctions.getSeed()));
		this.NegativeSamples = new ArrayList<String>();
		this.NegativeSamples = negativeSample;
	}
	
	public void generateNegativeSamples(int negSampleSize){
		List<String> negativeSample = new ArrayList<String>();
		String temp = "";
		List<Character> alph= this.alphabetAsArray;
		int randomWordLength;
		Matcher search;
		
		
		while(negativeSample.size() < negSampleSize) {
			randomWordLength = HelperFunctions.getRandomNumInRange(1, this.WordLengthMax);
			for(int i = 0; i < randomWordLength; i++) {
				temp = temp + alph.get(HelperFunctions.getRandomNumInRange(0, alph.size() - 1));
			}
			search = this.regex.matcher(temp);
			if (false == search.find() && negativeSample.contains(temp) == false) {
				negativeSample.add(temp);
			}
			temp = "";
		}
		Collections.shuffle(negativeSample, new Random(HelperFunctions.getSeed()));
		this.NegativeSamples = new ArrayList<String>();
		this.NegativeSamples = negativeSample;
	}
	
	//Sonderfall der Generierung nach regex7 ähnlichen Fällen
	//wenn ein Element nicht im positiven Set ist, gehört es ins negative
	//nicht 100% sicher ob diese Funktion in Bezug auf reguläre Sprachen richtig ist
	public void generateNegativeSamples(Boolean forRegx7Type){
		if(false==forRegx7Type) {
			generateNegativeSamples();
		}else {
			List<String> negativeSample = new ArrayList<String>();
			String temp = "";
			List<Character> alph= this.alphabetAsArray;
			int randomWordLength;
		
			while(negativeSample.size() < (int) this.length*this.negMultiplier) {
				randomWordLength = HelperFunctions.getRandomNumInRange(1, this.WordLengthMax);
				for(int i = 0; i < randomWordLength; i++) {
					temp = temp + alph.get(HelperFunctions.getRandomNumInRange(0, alph.size() - 1));
				}
				if (this.PositiveSamples.contains(temp) == false && negativeSample.contains(temp) == false) {
					negativeSample.add(temp);
				}
				temp = "";
			}
			Collections.shuffle(negativeSample, new Random(HelperFunctions.getSeed()));
			this.NegativeSamples = new ArrayList<String>();
			this.NegativeSamples = negativeSample;
		}
	}
	
	//setzt die maximale Wortlänge der negativen Samples
	//diese ist die Länge des Längsten positiven Wortes+1
	public void getWordLengthMax() {
		int wordLengthMax = 0;
		String temp;
		for (int i = 0;i<this.PositiveSamples.size();i++) {
			temp = this.PositiveSamples.get(i);
			if(wordLengthMax<temp.length()) {
				wordLengthMax = temp.length();
			}
		}
		this.WordLengthMax = wordLengthMax + 1;
	}
	
	//getter
	public int getMaxWordLength() {
		return this.WordLengthMax;
	}
	
	//Generiert die Variable des Alphabets für die RPNI Algorithmen
	public void generateAlphabet() {
		ArrayList<Character> temp = new ArrayList<Character>();
		
		for(int i=0;i<this.PositiveSamples.size();i++) {
			for(int j = 0;j<this.PositiveSamples.get(i).length();j++) {
				if(false == temp.contains(this.PositiveSamples.get(i).charAt(j))) {
					temp.add(this.PositiveSamples.get(i).charAt(j)); 
				}
			}
		}
	    Collections.sort(temp);
	    Alphabet<Character> alph = Alphabets.characters(temp.get(0), temp.get(temp.size() - 1));
	    this.alphabet = alph;
	    this.alphstartchar = temp.get(0);
		this.alphendchar = temp.get(temp.size()-1);
		this.alphabetAsArray = temp;
	    
	}
	
	
	//bin mir nicht sicher wo die Funktion Einsatz findet
	//wird wahrscheinlich nicht verwendet
	private static void getAlphabetPermutationRec(char[] alphabet, String recurs,int i,int j,List<String> ret) {
		if(i==0) {
			ret.add(recurs);
			return;
		}
		for(int k=0;k<j;++k) {
			String temp = recurs + alphabet[k];
			getAlphabetPermutationRec(alphabet,temp,i-1,j,ret);
		}
	}
	
	 
	//getter
	public Alphabet<Character> getAlphabet(){
		return this.alphabet;
	}
	
	//getter für den ersten und letzten Buchstaben des Alphabets als String concatted
	public String getAlphabetboundaries() {
		String ret = new StringBuilder().append(this.alphstartchar).append(this.alphendchar).toString();
		return ret;
	} 
	
	//getter
	public List<String> getPositiveSample(){
		Collections.shuffle(this.PositiveSamples, new Random(HelperFunctions.getSeed()));
		return this.PositiveSamples;
	}
	
	//getter
	public List<String> getNegativeSample(){
		Collections.shuffle(this.NegativeSamples, new Random(HelperFunctions.getSeed()));
		return this.NegativeSamples;
	}
	 
	
	
	
	
}
