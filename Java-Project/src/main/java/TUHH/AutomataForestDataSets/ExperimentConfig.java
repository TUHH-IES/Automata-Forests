package TUHH.AutomataForestDataSets;

import java.util.List;

public class ExperimentConfig {
    private String name;
    private String example; // coffee or random

    private int numStates;
    private int numInputs;
    private int numOutputs;
    private List<Integer> sizeForest;
    private List<Double> bagRatio;
    private List<Integer> sizeLearn;
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
}
