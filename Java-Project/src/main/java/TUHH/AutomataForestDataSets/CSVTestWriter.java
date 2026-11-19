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

			// DFA-only EDSM and GRBM metrics are left blank for Mealy experiments.
			String[] header = { "testSize", "bagRatio", "forestSize", "trainSize", "SigmaIn", "SigmaOut", "TargetSize",
					"WordLength", "ModelSizeRPNI", "ModelSizeEDSM", "ModelSizePM", "TimeRPNI", "TimeEDSM",
					"TimeForest", "RPNI", "EDSM", "FPM", "FMV", "GRBMTeamSize", "GRBMStrategy",
					"TotalModelSizeGRBM", "TimeGRBM", "GRBM"};
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
					results.getModelSizeEDSM() == null ? "" : results.getModelSizeEDSM().toString(),
					Integer.toString(results.getModelSizePM()),
					Double.toString(results.getTimeRPNI()),
					results.getTimeEDSM() == null ? "" : results.getTimeEDSM().toString(),
					Double.toString(results.getTimeForest()),
					Integer.toString(results.getCorrectWordsRPNI()),
					results.getCorrectWordsEDSM() == null ? "" : results.getCorrectWordsEDSM().toString(),
					Integer.toString(results.getCorrectWordsPM()),
					Integer.toString(results.getCorrectWordsMV()),
					results.getGrbmTeamSize() == null ? "" : results.getGrbmTeamSize().toString(),
					results.getGrbmStrategy() == null ? "" : results.getGrbmStrategy(),
					results.getTotalModelSizeGRBM() == null ? "" : results.getTotalModelSizeGRBM().toString(),
					results.getTimeGRBM() == null ? "" : results.getTimeGRBM().toString(),
					results.getCorrectWordsGRBM() == null ? "" : results.getCorrectWordsGRBM().toString()
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
