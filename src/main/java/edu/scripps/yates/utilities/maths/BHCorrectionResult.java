package edu.scripps.yates.utilities.maths;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BHCorrectionResult {
	private PValuesCollection originalPValues;
	private PValuesCollection correctedPValues;

	/**
	 * @return the originalPValues
	 */
	public PValuesCollection getOriginalPValues() {
		return originalPValues;
	}

	/**
	 * @param originalPValues
	 *            the originalPValues to set
	 */
	public void setOriginalPValues(PValuesCollection originalPValues) {
		this.originalPValues = originalPValues;
	}

	/**
	 * @param originalPValues
	 *            the originalPValues to set
	 */
	public void setOriginalPValues(Map<String, Double> originalPValues) {
		setOriginalPValues(new PValuesCollection(originalPValues));
	}

	/**
	 * @return the correctedPValues
	 */
	public PValuesCollection getCorrectedPValues() {
		return correctedPValues;
	}

	/**
	 * @param correctedPValues
	 *            the correctedPValues to set
	 */
	public void setCorrectedPValues(PValuesCollection correctedPValues) {
		this.correctedPValues = correctedPValues;
	}

	/**
	 * @param correctedPValues
	 *            the correctedPValues to set
	 */
	public void setCorrectedPValues(Map<String, Double> pValues) {
		setCorrectedPValues(new PValuesCollection(pValues));
	}

	public List<String> getSortedKeysByCorrectedPValue() {
		return getCorrectedPValues().getSortedKeysByPValue();
	}

	public List<String> getSortedKeysByOriginalPValue() {
		return getOriginalPValues().getSortedKeysByPValue();
	}

	public List<String> getSortedKeysByKeys() {
		List<String> keys = getSortedKeysByCorrectedPValue();
		Collections.sort(keys);
		return keys;
	}
}
