package TUHH.AutomataForestDataSets;

public class ExperimentResults {
	private int testSize;
	private double bagRatio;
	private int forestSize;
	private int trainingSize;
	private int inputAlphabetSize;
	private int outputAlphabetSize;
	private int targetSize;
	private int wordLength;
	private double timeRPNI;
	private double timeForest;
	private int correctWordsRPNI;
	private int correctWordsPM;
	private int correctWordsMV;
	private int modelSizeRPNI;
	private int modelSizePM;
	private Integer modelSizeEDSM;
	private Double timeEDSM;
	private Integer correctWordsEDSM;
	private Integer grbmTeamSize;
	private String grbmStrategy;
	private Long totalModelSizeGRBM;
	private Double timeGRBM;
	private Integer correctWordsGRBM;
	
	public int getTestSize() {
		return testSize;
	}
	public void setTestSize(int testSize) {
		this.testSize = testSize;
	}
	public double getBagRatio() {
		return bagRatio;
	}
	public void setBagRatio(double bagRatio) {
		this.bagRatio = bagRatio;
	}
	public int getForestSize() {
		return forestSize;
	}
	public void setForestSize(int forestSize) {
		this.forestSize = forestSize;
	}
	public int getTrainingSize() {
		return trainingSize;
	}
	public void setTrainingSize(int trainingSize) {
		this.trainingSize = trainingSize;
	}
	public int getInputAlphabetSize() {
		return inputAlphabetSize;
	}
	public void setInputAlphabetSize(int inputAlphabetSize) {
		this.inputAlphabetSize = inputAlphabetSize;
	}
	public int getOutputAlphabetSize() {
		return outputAlphabetSize;
	}
	public void setOutputAlphabetSize(int outputAlphabetSize) {
		this.outputAlphabetSize = outputAlphabetSize;
	}
	public int getTargetSize() {
		return targetSize;
	}
	public void setTargetSize(int targetSize) {
		this.targetSize = targetSize;
	}
	public int getWordLength() {
		return wordLength;
	}
	public void setWordLength(int wordLength) {
		this.wordLength = wordLength;
	}
	public double getTimeRPNI() {
		return timeRPNI;
	}
	public void setTimeRPNI(double timeRPNI) {
		this.timeRPNI = timeRPNI;
	}
	public double getTimeForest() {
		return timeForest;
	}
	public void setTimeForest(double timeForest) {
		this.timeForest = timeForest;
	}
	public int getCorrectWordsRPNI() {
		return correctWordsRPNI;
	}
	public void setCorrectWordsRPNI(int correctWordsRPNI) {
		this.correctWordsRPNI = correctWordsRPNI;
	}
	public int getCorrectWordsPM() {
		return correctWordsPM;
	}
	public void setCorrectWordsPM(int correctWordsPM) {
		this.correctWordsPM = correctWordsPM;
	}
	public int getCorrectWordsMV() {
		return correctWordsMV;
	}
	public void setCorrectWordsMV(int correctWordsMV) {
		this.correctWordsMV = correctWordsMV;
	}
	public int getModelSizeRPNI() {
		return modelSizeRPNI;
	}
	public void setModelSizeRPNI(int modelSizeRPNI) {
		this.modelSizeRPNI = modelSizeRPNI;
	}
	public int getModelSizePM() {
		return modelSizePM;
	}
	public void setModelSizePM(int modelSizePM) {
		this.modelSizePM = modelSizePM;
	}

	public Integer getModelSizeEDSM() {
		return modelSizeEDSM;
	}

	public void setModelSizeEDSM(Integer modelSizeEDSM) {
		this.modelSizeEDSM = modelSizeEDSM;
	}

	public Double getTimeEDSM() {
		return timeEDSM;
	}

	public void setTimeEDSM(Double timeEDSM) {
		this.timeEDSM = timeEDSM;
	}

	public Integer getCorrectWordsEDSM() {
		return correctWordsEDSM;
	}

	public void setCorrectWordsEDSM(Integer correctWordsEDSM) {
		this.correctWordsEDSM = correctWordsEDSM;
	}

	public Integer getGrbmTeamSize() {
		return grbmTeamSize;
	}

	public void setGrbmTeamSize(Integer grbmTeamSize) {
		this.grbmTeamSize = grbmTeamSize;
	}

	public String getGrbmStrategy() {
		return grbmStrategy;
	}

	public void setGrbmStrategy(String grbmStrategy) {
		this.grbmStrategy = grbmStrategy;
	}

	public Long getTotalModelSizeGRBM() {
		return totalModelSizeGRBM;
	}

	public void setTotalModelSizeGRBM(Long totalModelSizeGRBM) {
		this.totalModelSizeGRBM = totalModelSizeGRBM;
	}

	public Double getTimeGRBM() {
		return timeGRBM;
	}

	public void setTimeGRBM(Double timeGRBM) {
		this.timeGRBM = timeGRBM;
	}

	public Integer getCorrectWordsGRBM() {
		return correctWordsGRBM;
	}

	public void setCorrectWordsGRBM(Integer correctWordsGRBM) {
		this.correctWordsGRBM = correctWordsGRBM;
	}
	
	@Override
	public String toString() {
	    return testSize + ", " +
	           bagRatio + ", " +
	           forestSize + ", " +
	           trainingSize + ", " +
	           inputAlphabetSize + ", " +
	           outputAlphabetSize + ", " +
	           targetSize + ", " +
	           wordLength + ", " +
	           timeRPNI + ", " +
	           timeForest + ", " +
	           correctWordsRPNI + ", " +
	           correctWordsPM + ", " +
	           correctWordsMV;
	}
}
