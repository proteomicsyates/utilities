package edu.scripps.yates.maths;

import org.junit.Test;

import edu.scripps.yates.utilities.maths.PValueCorrection;
import edu.scripps.yates.utilities.maths.PValueCorrectionResult;
import edu.scripps.yates.utilities.maths.PValueCorrectionType;
import edu.scripps.yates.utilities.maths.PValuesCollection;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.hash.TObjectDoubleHashMap;

public class PValueCorrectionTests {
	final double[] pvalues = new double[] { 4.533744e-01, 7.296024e-01, 9.936026e-02, 9.079658e-02, 1.801962e-01,
			8.752257e-01, 2.922222e-01, 9.115421e-01, 4.355806e-01, 5.324867e-01, 4.926798e-01, 5.802978e-01,
			3.485442e-01, 7.883130e-01, 2.729308e-01, 8.502518e-01, 4.268138e-01, 6.442008e-01, 3.030266e-01,
			5.001555e-02, 3.194810e-01, 7.892933e-01, 9.991834e-01, 1.745691e-01, 9.037516e-01, 1.198578e-01,
			3.966083e-01, 1.403837e-02, 7.328671e-01, 6.793476e-02, 4.040730e-03, 3.033349e-04, 1.125147e-02,
			2.375072e-02, 5.818542e-04, 3.075482e-04, 8.251272e-03, 1.356534e-03, 1.360696e-02, 3.764588e-04,
			1.801145e-05, 2.504456e-07, 3.310253e-02, 9.427839e-03, 8.791153e-04, 2.177831e-04, 9.693054e-04,
			6.610250e-05, 2.900813e-02, 5.735490e-03 };

	final double[][] correctAnswers = new double[][] {
			new double[] { // Benjamini-Hochberg
					6.126681e-01, 8.521710e-01, 1.987205e-01, 1.891595e-01, 3.217789e-01, 9.301450e-01, 4.870370e-01,
					9.301450e-01, 6.049731e-01, 6.826753e-01, 6.482629e-01, 7.253722e-01, 5.280973e-01, 8.769926e-01,
					4.705703e-01, 9.241867e-01, 6.049731e-01, 7.856107e-01, 4.887526e-01, 1.136717e-01, 4.991891e-01,
					8.769926e-01, 9.991834e-01, 3.217789e-01, 9.301450e-01, 2.304958e-01, 5.832475e-01, 3.899547e-02,
					8.521710e-01, 1.476843e-01, 1.683638e-02, 2.562902e-03, 3.516084e-02, 6.250189e-02, 3.636589e-03,
					2.562902e-03, 2.946883e-02, 6.166064e-03, 3.899547e-02, 2.688991e-03, 4.502862e-04, 1.252228e-05,
					7.881555e-02, 3.142613e-02, 4.846527e-03, 2.562902e-03, 4.846527e-03, 1.101708e-03, 7.252032e-02,
					2.205958e-02 },
			new double[] { // Benjamini & Yekutieli
					1.000000e+00, 1.000000e+00, 8.940844e-01, 8.510676e-01, 1.000000e+00, 1.000000e+00, 1.000000e+00,
					1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00,
					1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 5.114323e-01, 1.000000e+00,
					1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.754486e-01,
					1.000000e+00, 6.644618e-01, 7.575031e-02, 1.153102e-02, 1.581959e-01, 2.812089e-01, 1.636176e-02,
					1.153102e-02, 1.325863e-01, 2.774239e-02, 1.754486e-01, 1.209832e-02, 2.025930e-03, 5.634031e-05,
					3.546073e-01, 1.413926e-01, 2.180552e-02, 1.153102e-02, 2.180552e-02, 4.956812e-03, 3.262838e-01,
					9.925057e-02 },
			new double[] { // Bonferroni
					1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00,
					1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00,
					1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00,
					1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 7.019185e-01,
					1.000000e+00, 1.000000e+00, 2.020365e-01, 1.516674e-02, 5.625735e-01, 1.000000e+00, 2.909271e-02,
					1.537741e-02, 4.125636e-01, 6.782670e-02, 6.803480e-01, 1.882294e-02, 9.005725e-04, 1.252228e-05,
					1.000000e+00, 4.713920e-01, 4.395577e-02, 1.088915e-02, 4.846527e-02, 3.305125e-03, 1.000000e+00,
					2.867745e-01 },
			new double[] { // Hochberg
					9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01,
					9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01,
					9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01,
					9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 4.632662e-01,
					9.991834e-01, 9.991834e-01, 1.575885e-01, 1.383967e-02, 3.938014e-01, 7.600230e-01, 2.501973e-02,
					1.383967e-02, 3.052971e-01, 5.426136e-02, 4.626366e-01, 1.656419e-02, 8.825610e-04, 1.252228e-05,
					9.930759e-01, 3.394022e-01, 3.692284e-02, 1.023581e-02, 3.974152e-02, 3.172920e-03, 8.992520e-01,
					2.179486e-01 },
			new double[] { // Holm
					1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00,
					1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00,
					1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00,
					1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 1.000000e+00, 4.632662e-01,
					1.000000e+00, 1.000000e+00, 1.575885e-01, 1.395341e-02, 3.938014e-01, 7.600230e-01, 2.501973e-02,
					1.395341e-02, 3.052971e-01, 5.426136e-02, 4.626366e-01, 1.656419e-02, 8.825610e-04, 1.252228e-05,
					9.930759e-01, 3.394022e-01, 3.692284e-02, 1.023581e-02, 3.974152e-02, 3.172920e-03, 8.992520e-01,
					2.179486e-01 },
			new double[] { // Hommel
					9.991834e-01, 9.991834e-01, 9.991834e-01, 9.987624e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01,
					9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01,
					9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.595180e-01, 9.991834e-01,
					9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 9.991834e-01, 4.351895e-01,
					9.991834e-01, 9.766522e-01, 1.414256e-01, 1.304340e-02, 3.530937e-01, 6.887709e-01, 2.385602e-02,
					1.322457e-02, 2.722920e-01, 5.426136e-02, 4.218158e-01, 1.581127e-02, 8.825610e-04, 1.252228e-05,
					8.743649e-01, 3.016908e-01, 3.516461e-02, 9.582456e-03, 3.877222e-02, 3.172920e-03, 8.122276e-01,
					1.950067e-01 } };

	@Test
	public void test1() {
		final PValueCorrectionType[] types = PValueCorrectionType.values();
		for (int type = 0; type < types.length; ++type) {
			final PValueCorrectionType method = types[type];
			final double[] q = PValueCorrection.pAdjust(pvalues, method);
			double error = 0.0;
			for (int i = 0; i < pvalues.length; ++i) {
				error += Math.abs(q[i] - correctAnswers[type][i]);
			}
			PValueCorrection.doubleSay(q);
			System.out.printf("\ntype %d = '%s' has a cumulative error of %g\n", type, method, error);
		}
	}

	@Test
	public void test2() {
		final PValueCorrectionType[] types = PValueCorrectionType.values();
		for (int type = 0; type < types.length; ++type) {
			final PValueCorrectionType method = types[type];
			final TObjectDoubleHashMap<String> map = new TObjectDoubleHashMap<String>();
			int k = 0;
			for (final double p : pvalues) {
				map.put(String.valueOf(k++), p);
			}
			final PValuesCollection pValuesCollection = new PValuesCollection(map);
			final PValueCorrectionResult q = PValueCorrection.pAdjust(pValuesCollection, method);
			double error = 0.0;
			final TDoubleArrayList correctedPValues = new TDoubleArrayList();
			for (int i = 0; i < pvalues.length; ++i) {
				final Double correctedPValue = q.getCorrectedPValues().getPValue(String.valueOf(i));
				correctedPValues.add(correctedPValue);
				error += Math.abs(correctedPValue - correctAnswers[type][i]);
			}
			PValueCorrection.doubleSay(correctedPValues.toArray());
			System.out.printf("\ntype %d = '%s' has a cumulative error of %g\n", type, method, error);
		}
	}

}
