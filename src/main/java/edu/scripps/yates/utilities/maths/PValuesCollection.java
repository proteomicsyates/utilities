package edu.scripps.yates.utilities.maths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PValuesCollection {
	private final Map<String, Double> pValues = new HashMap<String, Double>();
	private ArrayList<String> sortedKeysByPValue;

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
				return Double.compare(pValues.get(o1), pValues.get(o2));
			}
		});

	}

	public List<String> getSortedKeysByPValue() {
		return sortedKeysByPValue;
	}

	public int size() {
		return pValues.size();
	}

	public double get(String key) {
		return pValues.get(key);
	}
}
