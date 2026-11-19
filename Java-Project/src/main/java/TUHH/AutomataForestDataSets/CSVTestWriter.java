package TUHH.AutomataForestDataSets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import com.opencsv.CSVWriter;

public class CSVTestWriter {

	private CSVWriter writer;
	private FileWriter outputfile;

	public CSVTestWriter() {
	}

	public void closeWriter() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initWriteSingleExperiment(String filename) {

		String testFile = "";
		try {
			testFile = new File(Project.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();

		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		File file = new File(getFilePath(testFile, filename));
		try {
			// create FileWriter object with file as parameter
			outputfile = new FileWriter(file);

			// create CSVWriter object filewriter object as parameter
			writer = new CSVWriter(outputfile);

			// adding header to csv; size=24 columns
			String[] header = { "testSize", "bagRatio", "forestSize", "trainSize", "SigmaIn", "SigmaOut", "TargetSize",
					"WordLength", "ModelSizeRPNI", "ModelSizePM", "TimeRPNI", "TimeForest", "RPNI", "FPM", "FMV"};
			writer.writeNext(header);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeDataSingleExperiment(ExperimentResults results) {
		try {
			String[] line = {
					Integer.toString(results.getTestSize()),
					Double.toString(results.getBagRatio()),
					Integer.toString(results.getForestSize()),
					Integer.toString(results.getTrainingSize()),
					Integer.toString(results.getInputAlphabetSize()),
					Integer.toString(results.getOutputAlphabetSize()),
					Integer.toString(results.getTargetSize()),
					Integer.toString(results.getWordLength()),
					Integer.toString(results.getModelSizeRPNI()),
					Integer.toString(results.getModelSizePM()),
					Double.toString(results.getTimeRPNI()),
					Double.toString(results.getTimeForest()),
					Integer.toString(results.getCorrectWordsRPNI()),
					Integer.toString(results.getCorrectWordsPM()),
					Integer.toString(results.getCorrectWordsMV())
			};
			writer.writeNext(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getFilePath(String path, String name) {
		StringBuilder stringBuilder = new StringBuilder(path);
		stringBuilder.append("\\" + name + ".csv");
		return stringBuilder.toString();
	}

}
