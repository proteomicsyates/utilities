package edu.scripps.yates.utilities.maths;

import java.util.ArrayList;
import java.util.List;

import edu.scripps.yates.utilities.dates.DatesUtil;
import flanagan.analysis.Outliers;

public class ESDOutlierTest extends AbstractOutlierTest {
	private static double totalTime = 0.0;
	private static int numTests = 0;

	public ESDOutlierTest(double valueToTest, List<Double> populationValues) {
		super(valueToTest, populationValues);
	}

	@Override
	public OutlierTestResult test() {

		Outliers outlierDetector = new Outliers(array);
		outlierDetector.suppressDisplay();
		outlierDetector.suppressPrint();
		outlierDetector.resetNumberTeitjenMooreSimulations(10);

		long t1 = System.currentTimeMillis();
		ArrayList<Object> outlierResult = outlierDetector.outliersESD(2);
		long t2 = System.currentTimeMillis();
		addRunningTime(t1, t2);

		final OutlierTestResult outlierTestResult = new OutlierTestResult(valueToTest, populationValues, outlierResult);
		if (!outlierTestResult.isOutlier()) {
			t1 = System.currentTimeMillis();
			outlierResult = outlierDetector.outliersESD(1);
			t2 = System.currentTimeMillis();
			addRunningTime(t1, t2);
		}
		return outlierTestResult;
	}

	public void addRunningTime(double time) {
		numTests++;
		totalTime += time;
		log.info("average time of running time of " + this.getClass().getSimpleName() + " after " + numTests + " is "
				+ DatesUtil.getDescriptiveTimeFromMillisecs(totalTime / numTests * 1.0));

	}

	public void addRunningTime(long t1, long t2) {
		long time1 = t1;
		long time2 = t2;
		if (time1 > time2) {
			long tmp = time1;
			time1 = time2;
			time2 = tmp;
		}
		addRunningTime(Double.valueOf(time2 - time1));
	}
}
