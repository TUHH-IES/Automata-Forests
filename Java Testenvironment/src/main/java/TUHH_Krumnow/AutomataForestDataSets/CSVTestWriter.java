package TUHH_Krumnow.AutomataForestDataSets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.opencsv.CSVWriter;

public class CSVTestWriter {
	
	private CSVWriter writer;
	private FileWriter outputfile;
	private String path;
	private String name;
	
	public CSVTestWriter() {}
	
	public CSVTestWriter(String path_, String name_) {
		path = path_;
		name = name_;
	}
	
	public void initWriteSingleDFA() {
		String testFile = "";
		try {
			testFile = new File(Project.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI()).getParent();

		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		File file = new File(getFilePath(testFile,"EvalSingleDFA"));
		try {
	        // create FileWriter object with file as parameter
	        outputfile = new FileWriter(file);
	        
	        // create CSVWriter object filewriter object as parameter
	        writer = new CSVWriter(outputfile);
	        
	        // adding header to csv; size=19 columns
	        String[] header = { "maxScore", "bagratio","numbags","posTrainSetSize", "negTrainSetSize","stringMaxLength",
	        		"noise pos. into neg.", "noise neg. into pos.",
	        		"RPNIScore","ForestCVScore","ForestMVScore","falsePosRPNI","falsePosFCV","falsePosFMV",
	        		"falseNegRPNI","falseNegFCV","falseNegFMV","compTimeRPNI","compTimeForest"};
	        writer.writeNext(header);
	        
		}
		catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void initWriteRiseTrainAlpha(int numStates, int sigmaSize, int dataSteps) {
		String testFile = "";
		try {
			testFile = new File(Project.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI()).getParent();

		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		File file = new File(getFilePath(testFile,"RiseTrainAlpha",numStates,sigmaSize,dataSteps));
		try {
	        // create FileWriter object with file as parameter
	        outputfile = new FileWriter(file);
	        
	        // create CSVWriter object filewriter object as parameter
	        writer = new CSVWriter(outputfile);
	        
	        // adding header to csv; size=19 columns
	        String[] header = { "maxScore", "bagratio","numbags","posTrainSetSize", "negTrainSetSize","stringMaxLength",
	        		"noise pos. into neg.", "noise neg into pos.",
	        		"RPNIScore","ForestCVScore","ForestMVScore","falsePosRPNI","falsePosFCV","falsePosFMV",
	        		"falseNegRPNI","falseNegFCV","falseNegFMV","compTimeRPNI","compTimeForest"};
	        writer.writeNext(header);
	        
		}
		catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	public void initWriteRiseTrainAlphaStruct(int numbag) {
		String testFile = "";
		try {
			testFile = new File(Project.class.getProtectionDomain().getCodeSource().getLocation()
					.toURI()).getParent();

		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		File file = new File(getFilePath(testFile,"RiseTrainAlphaStruct"));
		try {
			// create FileWriter object with file as parameter
			outputfile = new FileWriter(file);

			// create CSVWriter object filewriter object as parameter
			writer = new CSVWriter(outputfile);

			// adding header to csv; size=3*numbags+14 columns
			List<String> head = new ArrayList<String>();
			head.add("trainSet");
			head.add("bagratio");
			head.add("numbags");
			head.add("sigmaSize");
			head.add("testSize");
			head.add("trainmaxLength");
			head.add("CV Pos");
			head.add("targetStates");
			head.add("RPNIStateSize");
			for(int i = 0; i < numbag; i++){
				head.add("ForestInd"+i+"States");
			}
			//head.add("targetCharact");
			//head.add("RPNICharact");
			//for(int i = 0; i < numbag; i++){
			//	head.add("ForestInd"+i+"Charact");
			//}
			head.add("targetDepth");
			head.add("RPNIDepth");
			for(int i = 0; i < numbag; i++){
				head.add("ForestInd"+i+"Depth");
			}
			for(int i = 0; i < numbag; i++){
				head.add("ForestInd"+i+"ActivationPerc");
			}
			String[] header = new String[head.size()];
			head.toArray(header);
			writer.writeNext(header);

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initWriteNumBagDFA() {
		String testFile = "";
		try {
			testFile = new File(Project.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI()).getParent();

		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		File file = new File(getFilePath(testFile,"EvalNumBagDFA"));
		try {
	        // create FileWriter object with file as parameter
	        outputfile = new FileWriter(file);
	        
	        // create CSVWriter object filewriter object as parameter
	        writer = new CSVWriter(outputfile);
	        
	        // adding header to csv; size=19 columns
	        String[] header = { "maxScore", "bagratio","numbags","posTrainSetSize", "negTrainSetSize","stringMaxLength",
	        		"noise pos. into neg.", "noise neg into pos.",
	        		"RPNIScore","ForestCVScore","ForestMVScore","falsePosRPNI","falsePosFCV","falsePosFMV",
	        		"falseNegRPNI","falseNegFCV","falseNegFMV","compTimeRPNI","compTimeForest"};
	        writer.writeNext(header);
	        
		}
		catch (IOException e) {
	        e.printStackTrace();
	    }
	}


	
	public void initWriteRatioDFA() {
		String testFile = "";
		try {
			testFile = new File(Project.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI()).getParent();

		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		File file = new File(getFilePath(testFile,"EvalRatioDFA"));
		try {
	        // create FileWriter object with file as parameter
	        outputfile = new FileWriter(file);
	        
	        // create CSVWriter object filewriter object as parameter
	        writer = new CSVWriter(outputfile);
	        
	        // adding header to csv; size=19 columns
	        String[] header = { "maxScore", "bagratio","numbags","posTrainSetSize", "negTrainSetSize","stringMaxLength",
	        		"noise pos. into neg.", "noise neg. into pos.",
	        		"RPNIScore","ForestCVScore","ForestMVScore","falsePosRPNI","falsePosFCV","falsePosFMV",
	        		"falseNegRPNI","falseNegFCV","falseNegFMV","compTimeRPNI","compTimeForest"};
	        writer.writeNext(header);
	        
		}
		catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void initWriteNoiseDFA() {
		String testFile = "";
		try {
			testFile = new File(Project.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI()).getParent();

		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		File file = new File(getFilePath(testFile,"EvalSingleDFANoise"));
		try {
	        // create FileWriter object with file as parameter
	        outputfile = new FileWriter(file);
	        
	        // create CSVWriter object filewriter object as parameter
	        writer = new CSVWriter(outputfile);
	        
	        // adding header to csv; size=19 columns
	        String[] header = { "maxScore", "bagratio","numbags","posTrainSetSize", "negTrainSetSize","stringMaxLength",
	        		"noise pos. into neg.", "noise neg. into pos.",
	        		"RPNIScore","ForestCVScore","ForestMVScore","falsePosRPNI","falsePosFCV","falsePosFMV",
	        		"falseNegRPNI","falseNegFCV","falseNegFMV","compTimeRPNI","compTimeForest"};
	        writer.writeNext(header);
	        
		}
		catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void writeDataSingleDFA(double[] Input) {
		try {
			String[] datawrite = new String[Input.length];
			for(int i = 0; i < Input.length; i++){
				datawrite[i] = String.valueOf(Input[i]);
			}

			writer.writeNext(datawrite);
	    }catch (Exception e) {
		     e.printStackTrace();
		}
	}


	
	public void closeWriter() {
		try {
			writer.close();
		}catch (IOException e) {
		     e.printStackTrace();
		}
	}
	
	
	public void initWriteSingleMealy() {
		
        String testFile = "";
		try {
			testFile = new File(Project.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI()).getParent();

		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		File file = new File(getFilePath(testFile,"EvalSingleMealy"));
		try {
	        // create FileWriter object with file as parameter
	        outputfile = new FileWriter(file);
	        
	        // create CSVWriter object filewriter object as parameter
	        writer = new CSVWriter(outputfile);
	        
	        // adding header to csv; size=24 columns
	        String[] header = { "maxScore", "bagratio","numbags","trainSetSize","Sigma","OutSigma","States",
	        		"MaxWLengthTrain","MaxWLengthTest","noiseratio",
	        		"RPNI1Z1","FCV1Z1","FCVEditD1Z1","FMV1Z1",
	        		"RPNIEditD","FCVEditD","FCVEditDEditD","FMVEditD",
	        		"RPNILastChar","FCVLastChar","FCVEditLastChar","FMVLastChar","compTimeRPNI","compTimeForest"};
	        writer.writeNext(header);
		}catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
public void initWriteBagRatioMealy() {
		
        String testFile = "";
		try {
			testFile = new File(Project.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI()).getParent();

		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		File file = new File(getFilePath(testFile,"EvalRatioMealy"));
		try {
	        // create FileWriter object with file as parameter
	        outputfile = new FileWriter(file);
	        
	        // create CSVWriter object filewriter object as parameter
	        writer = new CSVWriter(outputfile);
	        
	        // adding header to csv; size=24 columns
	        String[] header = { "maxScore", "bagratio","numbags","trainSetSize","Sigma","OutSigma","States",
	        		"MaxWLengthTrain","MaxWLengthTest","noiseratio",
	        		"RPNI1Z1","FCV1Z1","FCVEditD1Z1","FMV1Z1",
	        		"RPNIEditD","FCVEditD","FCVEditDEditD","FMVEditD",
	        		"RPNILastChar","FCVLastChar","FCVEditLastChar","FMVLastChar","compTimeRPNI","compTimeForest"};
	        writer.writeNext(header);
		}catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
public void initWriteNumBagMealy() {
	
    String testFile = "";
	try {
		testFile = new File(Project.class.getProtectionDomain().getCodeSource().getLocation()
			    .toURI()).getParent();

	} catch (URISyntaxException e1) {
		e1.printStackTrace();
	}

	File file = new File(getFilePath(testFile,"EvalNumBagMealy"));
	try {
        // create FileWriter object with file as parameter
        outputfile = new FileWriter(file);
        
        // create CSVWriter object filewriter object as parameter
        writer = new CSVWriter(outputfile);
        
        // adding header to csv; size=24 columns
        String[] header = { "maxScore", "bagratio","numbags","trainSetSize","Sigma","OutSigma","States",
        		"MaxWLengthTrain","MaxWLengthTest","noiseratio",
        		"RPNI1Z1","FCV1Z1","FCVEditD1Z1","FMV1Z1",
        		"RPNIEditD","FCVEditD","FCVEditDEditD","FMVEditD",
        		"RPNILastChar","FCVLastChar","FCVEditLastChar","FMVLastChar","compTimeRPNI","compTimeForest"};
        writer.writeNext(header);
	}catch (IOException e) {
        e.printStackTrace();
    }
}

public void initWriteNoiseMealy() {
	
    String testFile = "";
	try {
		testFile = new File(Project.class.getProtectionDomain().getCodeSource().getLocation()
			    .toURI()).getParent();

	} catch (URISyntaxException e1) {
		e1.printStackTrace();
	}

	File file = new File(getFilePath(testFile,"EvalSingleMealyNoise"));
	try {
        // create FileWriter object with file as parameter
        outputfile = new FileWriter(file);
        
        // create CSVWriter object filewriter object as parameter
        writer = new CSVWriter(outputfile);
        
        // adding header to csv; size=24 columns
        String[] header = { "maxScore", "bagratio","numbags","trainSetSize","Sigma","OutSigma","States",
        		"MaxWLengthTrain","MaxWLengthTest","noiseratio",
        		"RPNI1Z1","FCV1Z1","FCVEditD1Z1","FMV1Z1",
        		"RPNIEditD","FCVEditD","FCVEditDEditD","FMVEditD",
        		"RPNILastChar","FCVLastChar","FCVEditLastChar","FMVLastChar","compTimeRPNI","compTimeForest"};
        writer.writeNext(header);
	}catch (IOException e) {
        e.printStackTrace();
    }
}
	
	public void writeDataSingleMealy(double[] Input)  {    
		try {
	        String[] datawrite = {"1","2","3","4","5","6","7","8","9","0","1","2","3","4","5","6","7","8","9","0","1","2","3","4"};
	        	datawrite[0] = String.valueOf(Input[0]);
	        	datawrite[1] = String.valueOf(Input[1]);
	        	datawrite[2] = String.valueOf(Input[2]);
	        	datawrite[3] = String.valueOf(Input[3]);
	        	datawrite[4] = String.valueOf(Input[4]);
	        	datawrite[5] = String.valueOf(Input[5]);
	        	datawrite[6] = String.valueOf(Input[6]);
	        	datawrite[7] = String.valueOf(Input[7]);
	        	datawrite[8] = String.valueOf(Input[8]);
	        	datawrite[9] = String.valueOf(Input[9]);
	        	datawrite[10] = String.valueOf(Input[10]);
	        	datawrite[11] = String.valueOf(Input[11]);
	        	datawrite[12] = String.valueOf(Input[12]);
	        	datawrite[13] = String.valueOf(Input[13]);
	        	datawrite[14] = String.valueOf(Input[14]);
	        	datawrite[15] = String.valueOf(Input[15]);
	        	datawrite[16] = String.valueOf(Input[16]);
	        	datawrite[17] = String.valueOf(Input[17]);
	        	datawrite[18] = String.valueOf(Input[18]);
	        	datawrite[19] = String.valueOf(Input[19]);
	        	datawrite[20] = String.valueOf(Input[20]);
	        	datawrite[21] = String.valueOf(Input[21]);
	        	datawrite[22] = String.valueOf(Input[22]);
	        	datawrite[23] = String.valueOf(Input[23]);
	        	
	        	writer.writeNext(datawrite);
	        }catch (Exception e) {
			     e.printStackTrace();
			}
		}
	        
	
	private String getDirPath() {
		String userDirectory = System.getProperty("user.dir");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(userDirectory);
		stringBuilder.append("\\src\\main\\java\\TUHH_Krumnow\\AutomataForestDataSets\\");
		return stringBuilder.toString();
	}
	


	private String getFilePath(String path, String name){
		StringBuilder stringBuilder = new StringBuilder(path);
		stringBuilder.append("\\"+name+".csv");
		return stringBuilder.toString();
	}

	private String getFilePath(String path, String name,int numStates,int sigmaSize,int dataSteps){
		StringBuilder stringBuilder = new StringBuilder(path);
		stringBuilder.append("\\"+name+"_States"+numStates+"_Simga"+sigmaSize+"_StepsSize"+dataSteps+".csv");
		return stringBuilder.toString();
	}

}
