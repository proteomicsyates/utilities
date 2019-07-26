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
}
