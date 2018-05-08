package edu.scripps.yates.utilities.progresscounter;

import java.text.DecimalFormat;

import org.apache.commons.lang.mutable.MutableLong;
import org.apache.log4j.Logger;

import edu.scripps.yates.utilities.dates.DatesUtil;

public class ProgressCounter {
	private final static Logger log = Logger.getLogger(ProgressCounter.class);
	private long total;
	private MutableLong count;
	private String previousPercentage = "";
	private final ProgressPrintingType progressPrintingType;
	private final DecimalFormat df;
	private final boolean showRemainingTime;
	private long t1 = -1;
	private ProgressNumberFormatter formatter;

	public ProgressCounter(long total, ProgressPrintingType progressPrintingType, int numDecimals) {
		this(total, progressPrintingType, numDecimals, false);
	}

	public ProgressCounter(long total, ProgressPrintingType progressPrintingType, int numDecimals,
			boolean showTimeRemaining) {
		this.total = total;
		count = new MutableLong(0);
		this.progressPrintingType = progressPrintingType;
		final StringBuilder sb = new StringBuilder("#");
		for (int i = 0; i < numDecimals; i++) {
			if ("#".equals(sb.toString())) {
				sb.append(".");
			}
			sb.append("#");
		}
		df = new DecimalFormat(sb.toString());
		showRemainingTime = showTimeRemaining;
	}

	public void increment(long increment) {
		count.add(increment);
		startTimeWithFirstIncrement();

	}

	public void setProgress(long progress) {
		count.setValue(progress);
		startTimeWithFirstIncrement();

	}

	public void increment() {
		count.add(1);
		startTimeWithFirstIncrement();

	}

	private void startTimeWithFirstIncrement() {
		// start time with the first increment
		if (showRemainingTime && t1 == -1) {
			log.info("taking time at the first increment");
			t1 = System.currentTimeMillis();
		}
	}

	private String getRemainingTime() {
		if (showRemainingTime) {
			final long currentTimeMillis = System.currentTimeMillis();
			final long timeConsumed = currentTimeMillis - t1;
			final double timeConsumedPerItem = 1.0 * timeConsumed / getCount();
			final double estimatedRemainingTime = timeConsumedPerItem * (1.0 * total - getCount());
			return DatesUtil.getDescriptiveTimeFromMillisecs(estimatedRemainingTime);
		}
		return "";
	}

	public String printIfNecessary() {

		final StringBuilder sb = new StringBuilder();
		if (progressPrintingType == ProgressPrintingType.EVERY_STEP) {
			final double percentage = getPercentage();
			sb.append(getFormatter().format(count.longValue())).append("/").append(getFormatter().format(total))
					.append(" (").append(df.format(percentage)).append("%)").toString();
			if (showRemainingTime) {
				sb.append(" (" + getRemainingTime() + " remaining...)");
			}
			return sb.toString();
		} else if (progressPrintingType == ProgressPrintingType.PERCENTAGE_STEPS) {
			final String percentage = df.format(getPercentage());
			if (!percentage.equals(previousPercentage)) {
				sb.append(getFormatter().format(count.longValue())).append("/").append(getFormatter().format(total))
						.append(" (").append(percentage).append("%)").toString();
				if (showRemainingTime) {
					sb.append(" (" + getRemainingTime() + " remaining...)");
				}
				previousPercentage = percentage;
				return sb.toString();
			}
		}
		return "";
	}

	public long getCount() {
		return count.longValue();
	}

	public double getPercentage() {
		return count.longValue() * 100.0 / total;
	}

	public void setTotal(long max) {
		total = max;
		count = new MutableLong(0);
		t1 = -1;
	}

	public void setCount(long count2) {
		count.setValue(count2);
		if (count2 > 0) {
			startTimeWithFirstIncrement();
		}
	}

	public void addCount(long count2) {
		count.add(count2);
		startTimeWithFirstIncrement();
	}

	public void setProgressNumberFormatter(ProgressNumberFormatter formatter) {
		this.formatter = formatter;
	}

	public ProgressNumberFormatter getFormatter() {
		if (formatter == null) {
			formatter = new ProgressNumberFormatter() {

				@Override
				public String format(long number) {
					return String.valueOf(number);
				}
			};
		}
		return formatter;
	}
}
