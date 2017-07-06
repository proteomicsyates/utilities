package edu.scripps.yates.utilities.maths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import gnu.trove.map.hash.THashMap;

public class PValuesCollection {
	private final Map<String, Double> pValues = new THashMap<String, Double>();
	private List<String> sortedKeysByPValue;

	public PValuesCollection(Map<String, Double> pValues) {
		this.pValues.putAll(pValues);
		process();
	}

	private void process() {
		sortedKeysByPValue = new ArrayList<String>();
		sortedKeysByPValue.addAll(pValues.keySet());
		// sort pvalues
		Collections.sort(sortedKeysByPValue, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {

				final Double d1 = pValues.get(o1);
				final Double d2 = pValues.get(o2);
				double num1 = d1 != null ? d1 : Double.MAX_VALUE;
				double num2 = d2 != null ? d2 : Double.MAX_VALUE;
				return Double.compare(num1, num2);
			}
		});

	}

	public List<String> getSortedKeysByPValue() {
		return sortedKeysByPValue;
	}

	public int size() {
		return pValues.size();
	}

	public Double getPValue(String key) {
		return pValues.get(key);
	}

	public Map<String, Double> getPValues() {
		return pValues;
	}

}
