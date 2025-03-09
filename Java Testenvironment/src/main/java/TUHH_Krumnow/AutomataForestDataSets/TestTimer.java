package TUHH_Krumnow.AutomataForestDataSets;



public class TestTimer {
	
	private long StartTime;
	private long CurrTime;
	
	public TestTimer() {}
	
	private long elapsedTime() {
		return CurrTime - StartTime;
	}
	
	public void startTimer() {
		StartTime = System.nanoTime();
	}
	
	public void CurrTimer() {
		CurrTime = System.nanoTime();
	}
	
	public void PrintCurrentDuration() {
		System.out.println("one run takes approx.: " + DurationInSec() + " seconds time.");
	}
	
	public void PrintCurrentEstimationOfDuration(int currIteration, int maxIteration) {
		double IterToGo = (double) maxIteration / (double) currIteration;
		System.out.println("run approx. still needs: " + DurationInHours()*IterToGo + " hours time.");
	}
	
	private double DurationInSec() {
		return (double) elapsedTime() / 1_000_000_000.0;
	}
	
	public double getDurationInSec() {
		return DurationInSec();
	}
	

	
	private double DurationInHours() {
		return (double) elapsedTime() / 3_600_000_000_000.0;
	}
}
