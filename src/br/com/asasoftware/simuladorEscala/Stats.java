package br.com.asasoftware.simuladorEscala;

public class Stats {

	public static double getMean(int[] x) {
		double sum = 0.0;
		for (int xi: x)
			sum += (double) xi;
		return sum / x.length;
	}
	
	public static double getStdev(double xMean, int[] x) {
		double sum = 0.0;
		for (int xi: x)
			sum += (double) (xi - xMean) * (xi - xMean);
		return Math.sqrt(sum / x.length);
	}
	
	public static double getMean(double[] x) {
		double sum = 0.0;
		for (double xi: x)
			sum += xi;
		return sum / x.length;
	}
	
	public static double getStdev(double xMean, double[] x) {
		double sum = 0.0;
		for (double xi: x)
			sum += (xi - xMean) * (xi - xMean);
		return Math.sqrt(sum / x.length);
	}
}
