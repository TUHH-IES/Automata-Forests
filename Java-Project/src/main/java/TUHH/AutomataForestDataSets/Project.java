package TUHH.AutomataForestDataSets;

import java.io.FileInputStream;
import java.io.IOException;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class Project {
	public static void main(String[] args) throws IOException {
		ExperimentConfig config = new ExperimentConfig();
		Yaml yaml = new Yaml();
        try{
        	InputStream file = new FileInputStream("C:\\Users\\swant\\IES\\Modelllernen\\Automata-Forests\\Java-Project\\config-random-dfa-10.yaml");
            config = yaml.loadAs(file, ExperimentConfig.class);
            System.out.println(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
		try {
			TestSetup.TestEnvironmentConsoleInteraction(config);
		} catch (NumberFormatException nfe) {
			System.err.println("Invalid Format!");
		}
	}
}