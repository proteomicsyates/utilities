package edu.scripps.yates.utilities.maths;

import gnu.trove.list.TDoubleList;

public class Histogram {
	public static int[] calcHistogram(TDoubleList data, int numBins) {
		return calcHistogram(data.toArray(), data.min(), data.max(), numBins);
	}

	public static int[] calcHistogram(double[] data, double min, double max, int numBins) {
		final int[] result = new int[numBins];
		final double binSize = getBinSize(min, max, numBins);

		for (final double d : data) {
			final int bin = (int) ((d - min) / binSize);
			if (bin < 0) {
				/* this data is smaller than min */ } else if (bin >= numBins) {
				/* this data point is bigger than max */ } else {
				result[bin] += 1;
			}
		}
		return result;
	}

	public static double getBinSize(double min, double max, int numBins) {
		return (max - min) / numBins;
	}

	public static double getBinSize(TDoubleList data, int numBins) {
		return (data.max() - data.min()) / numBins;
	}

	/**
	 * This returns the Sturgis rule which determines the number of bins in an
	 * histogram as 1+3.3Log10n.<br>
	 * https://www.me.psu.edu/cimbala/me345/Lectures/Histograms.pdf#:~:text=an%20integer%20number%20of%20bins%2C%20we%20would%20use,10.9%20bins%2C%20which%20we%20round%20to%2011%20bins.
	 * 
	 * @param n the number of data points in the histogram
	 * @return
	 */
	public static int getSturgisRuleForHistogramBins(int n) {
		final int numberOfBins = Long.valueOf(Math.round(1 + 3.3 * Math.log10(n))).intValue();
		return numberOfBins;
	}

	/**
	 * This returns the Rice rule which determines the number of bins in an
	 * histogram as 2*n^(1/3)<br>
	 * https://www.me.psu.edu/cimbala/me345/Lectures/Histograms.pdf#:~:text=an%20integer%20number%20of%20bins%2C%20we%20would%20use,10.9%20bins%2C%20which%20we%20round%20to%2011%20bins.
	 * 
	 * @param n the number of data points in the histogram
	 * @return
	 */
	public static int getRiceRuleForHistogramBins(int n) {
		final int numberOfBins = Long.valueOf(Math.round(2 * Math.pow(n, 1.0 / 3.0))).intValue();
		return numberOfBins;
	}
}
