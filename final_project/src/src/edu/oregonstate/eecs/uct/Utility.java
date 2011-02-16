package edu.oregonstate.eecs.uct;

public class Utility {
	public static final double N95 = 1.959963984540;
	public static final double N98 = 2.326347874041;
	public static final double N99 = 2.575829303549;
	
	public static double computeMean(double[] data) {
    	double mean = 0;
    	for (double value: data)
    		mean += value;
    	return mean / data.length;
    }
    
    public static double computeStandardDeviation(double[] data) {
    	double mean = computeMean(data);
    	double sum = 0;
    	for (double value: data)
    		sum += (value - mean) * (value - mean);
    	return Math.sqrt(sum / data.length);
    }
    
    public static double computeStandardError(double standardDeviation, int sizeOfDataset) {
    	return standardDeviation / (Math.sqrt(sizeOfDataset));
    }
    
    /**
     * Assumes that data is normally distributed.
     * @param mean sample mean of the data
     * @param standardError standard error of the data
     * @param n the number of standard deviations away from mean to compute confidence interval
     * @return lower and upper bounds on the confidence interval
     */
    public static double[] computeConfidenceInterval(double mean, double standardError, double n) {
    	double interval = standardError * n;
    	return new double[] {mean - interval, mean + interval};
    }
}
