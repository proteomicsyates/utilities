package edu.scripps.yates.utilities.progresscounter;

import java.text.DecimalFormat;

public class ProgressCounter {
	private final int total;
	private final MutableInteger count;
	private String previousPercentage = "";
	private final ProgressPrintingType progressPrintingType;
	private final DecimalFormat df;

	public ProgressCounter(int total, ProgressPrintingType progressPrintingType, int numDecimals) {
		this.total = total;
		count = new MutableInteger(0);
		this.progressPrintingType = progressPrintingType;
		StringBuilder sb = new StringBuilder("#");
		for (int i = 0; i < numDecimals; i++) {
			if ("#".equals(sb.toString())) {
				sb.append(".");
			}
			sb.append("#");
		}
		df = new DecimalFormat(sb.toString());
	}

	public void increment() {
		count.set(count.get() + 1);
	}

	public String printIfNecessary() {

		if (progressPrintingType == ProgressPrintingType.EVERY_STEP) {
			double percentage = count.get() * 100.0 / total;
			String ret = new StringBuilder().append(count.get()).append("/").append(total).append(" (")
					.append(df.format(percentage)).append("%)").toString();
			return ret;
		} else if (progressPrintingType == ProgressPrintingType.PERCENTAGE_STEPS) {
			String percentage = df.format(Double.valueOf(count.get() * 100.0 / total));
			if (!percentage.equals(previousPercentage)) {
				String ret = new StringBuilder().append(count.get()).append("/").append(total).append(" (")
						.append(percentage).append("%)").toString();
				previousPercentage = percentage;
				return ret;
			}
		}
		return "";
	}

	public int getCount() {
		return count.get();
	}
}
