package edu.scripps.yates.utilities.memory;

import java.text.DecimalFormat;

import edu.scripps.yates.utilities.files.FileUtils;

public class MemoryUsageReport {
	private final static DecimalFormat df = new DecimalFormat("#.#");
	public static final double RECOMMENDED_MINIMUM_MEMORY_PERCENTAGE = 10.0;

	public static String getMemoryUsageReport() {

		Runtime runtime = Runtime.getRuntime();
		long totalMemory = runtime.totalMemory();
		long maxMemory = runtime.maxMemory();
		long freeMemory = runtime.freeMemory();
		// long freeMemory = runtime.freeMemory();

		long usedMemory = totalMemory - freeMemory;
		long realFreeMemory = maxMemory - usedMemory;
		double usedPercentage = usedMemory * 100.0 / maxMemory;
		double realFreePercentage = realFreeMemory * 100.0 / maxMemory;
		return FileUtils.getDescriptiveSizeFromBytes(usedMemory) + " used (" + df.format(usedPercentage) + "%), free ("
				+ FileUtils.getDescriptiveSizeFromBytes(realFreeMemory) + " " + df.format(realFreePercentage) + "%)";
	}

	public static String getUsedMemoryDescriptiveString() {
		Runtime runtime = Runtime.getRuntime();
		long totalMemory = runtime.totalMemory();
		long maxMemory = runtime.maxMemory();
		long freeMemory = runtime.freeMemory();
		long usedMemory = totalMemory - freeMemory;
		long realFreeMemory = maxMemory - usedMemory;
		return FileUtils.getDescriptiveSizeFromBytes(realFreeMemory);
	}

	public static String getFreeMemoryDescriptiveString() {
		Runtime runtime = Runtime.getRuntime();
		long totalMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		long usedMemory = totalMemory - freeMemory;
		return FileUtils.getDescriptiveSizeFromBytes(usedMemory);
	}

	public static double getUsedMemoryPercentage() {
		Runtime runtime = Runtime.getRuntime();
		long totalMemory = runtime.totalMemory();
		long maxMemory = runtime.maxMemory();
		long freeMemory = runtime.freeMemory();

		long usedMemory = totalMemory - freeMemory;
		double usedPercentage = usedMemory * 100.0 / maxMemory;
		return usedPercentage;
	}

	public static double getFreeMemoryPercentage() {
		Runtime runtime = Runtime.getRuntime();
		long totalMemory = runtime.totalMemory();
		long maxMemory = runtime.maxMemory();
		long freeMemory = runtime.freeMemory();

		long usedMemory = totalMemory - freeMemory;
		long realFreeMemory = maxMemory - usedMemory;
		double freePercentage = realFreeMemory * 100.0 / maxMemory;
		return freePercentage;
	}
}
