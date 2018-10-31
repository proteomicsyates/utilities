package edu.scripps.yates.utilities.maths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import gnu.trove.map.hash.TObjectDoubleHashMap;

public class PValuesCollection {
	private final TObjectDoubleHashMap<String> pValues = new TObjectDoubleHashMap<String>();
	private List<String> sortedKeysByPValue;

	public PValuesCollection(TObjectDoubleHashMap<String> pValues) {
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
				final double num1 = d1 != null ? d1 : Double.MAX_VALUE;
				final double num2 = d2 != null ? d2 : Double.MAX_VALUE;
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
		if (pValues.containsKey(key)) {
			return pValues.get(key);
		} else {
			return null;
		}
	}

	public TObjectDoubleHashMap<String> getPValues() {
		return pValues;
	}

}
