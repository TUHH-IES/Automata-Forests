package TUHH.AutomataForestDataSets;

import java.util.List;

public class ExperimentConfig {
	public enum AutomatonType {
		DFA,
		MEALY
	}

    private String name;
    private String example; // coffee or random
    private AutomatonType automatonType = AutomatonType.MEALY;

    private int numStates;
    private int numInputs;
    private int numOutputs;
    private List<Integer> sizeForest;
    private List<Double> bagRatio;
    private List<Integer> sizeLearn;
	private int grbmTeamSize = 11;
	private AutomataTeamGRBM.ClassificationStrategy grbmStrategy =
			AutomataTeamGRBM.ClassificationStrategy.FAIR_VOTE;
    private int sizeTest;
    private int lengthWords;
    private int repetitions;
    
	public int getNumStates() {
		return numStates;
	}
	public void setNumStates(int numStates) {
		this.numStates = numStates;
	}
	public int getNumInputs() {
		return numInputs;
	}
	public void setNumInputs(int numInputs) {
		this.numInputs = numInputs;
	}
	public int getNumOutputs() {
		return numOutputs;
	}
	public void setNumOutputs(int numOutputs) {
		this.numOutputs = numOutputs;
	}
	public List<Integer> getSizeForest() {
		return sizeForest;
	}
	public void setSizeForest(List<Integer> sizeForest) {
		this.sizeForest = sizeForest;
	}
	public List<Double> getBagRatio() {
		return bagRatio;
	}
	public void setBagRatio(List<Double> bagRatio) {
		this.bagRatio = bagRatio;
	}
	public List<Integer> getSizeLearn() {
		return sizeLearn;
	}
	public void setSizeLearn(List<Integer> sizeLearn) {
		this.sizeLearn = sizeLearn;
	}
	public int getGrbmTeamSize() {
		return grbmTeamSize;
	}
	public void setGrbmTeamSize(int grbmTeamSize) {
		this.grbmTeamSize = grbmTeamSize;
	}
	public AutomataTeamGRBM.ClassificationStrategy getGrbmStrategy() {
		return grbmStrategy;
	}
	public void setGrbmStrategy(AutomataTeamGRBM.ClassificationStrategy grbmStrategy) {
		this.grbmStrategy = grbmStrategy;
	}
	public int getSizeTest() {
		return sizeTest;
	}
	public void setSizeTest(int sizeTest) {
		this.sizeTest = sizeTest;
	}
	public int getLengthWords() {
		return lengthWords;
	}
	public void setLengthWords(int lengthWords) {
		this.lengthWords = lengthWords;
	}
	public int getRepetitions() {
		return repetitions;
	}
	public void setRepetitions(int repetitions) {
		this.repetitions = repetitions;
	}

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	public AutomatonType getAutomatonType() {
		return automatonType;
	}

	public void setAutomatonType(AutomatonType automatonType) {
		this.automatonType = automatonType;
	}
}
