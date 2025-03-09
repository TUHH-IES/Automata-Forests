package TUHH_Krumnow.AutomataForestDataSets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Project {
	 public static void main(String[] args) throws IOException {
		 
		 
		 BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	     System.out.print("Enter Test:\n (Press 1 for SingleExaminationMealy)");
	     System.out.print("\n (Press 2 for SingleExaminationDFA)");
	     System.out.print("\n (Press 3 for parameter evaluation of m in Mealy's)");
	     System.out.print("\n (Press 4 for parameter evaluation of alpha in Mealy's)");
	     System.out.print("\n (Press 5 for parameter evaluation of m in DFA's)");
	     System.out.print("\n (Press 6 for parameter evaluation of alpha in DFA's)");
	     System.out.print("\n (Press 7 for white noise analysis for DFA)");
	     System.out.print("\n (Press 8 for white noise analysis for Mealy machines)");
	     System.out.print("\n (Press 10 for DFA alpha analysis with rising training set)");
	     System.out.print("\n (Press 11 for DFA structural analysis with rising training set)");
	     try {
	       int testFormat = Integer.parseInt(br.readLine());
	       TestEnvironmentConsoleInteractions run = new TestEnvironmentConsoleInteractions(testFormat);
	     } catch(NumberFormatException nfe) {
	       System.err.println("Invalid Format!");
	     }
	 }
}
