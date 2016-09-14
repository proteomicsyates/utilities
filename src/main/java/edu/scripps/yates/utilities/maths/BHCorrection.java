package edu.scripps.yates.utilities.maths;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class BHCorrection {
	private final static Logger log = Logger.getLogger(BHCorrection.class);

	public static PValuesCollection bhCorrection(final Map<String, Double> pvaluesMap) {
		Map<String, Double> ret = new HashMap<String, Double>();
		PValuesCollection pvalues = new PValuesCollection(pvaluesMap);

		log.info("Applying benjamini-hochberg procedure to " + pvalues.size() + " pvalues");
		List<String> keys = pvalues.getSortedKeysByPValue();
		// iterate from the last one
		final int size = keys.size();
		for (int rank = size; rank > 0; rank--) {
			Double correctedPvalue = null;
			String key = keys.get(rank - 1);
			double pvalue = pvalues.get(key);
			if (rank + 1 <= size) {
				String key_plus_1 = keys.get(rank - 1 + 1);
				double pvalue_plus_1 = pvalues.get(key_plus_1);
				double b = (size * 1.0 / rank) * pvalue;
				correctedPvalue = Math.min(pvalue_plus_1, b);
			} else {

				correctedPvalue = pvalue;
			}
			ret.put(key, correctedPvalue);
		}
		log.info("benjamini-hochberg procedure finished");
		return new PValuesCollection(ret);
	}

	public static PValuesCollection bhCorrection(final Collection<Double> pvalues) {
		Map<String, Double> map = new HashMap<String, Double>();
		int num = 1;
		for (Double pvalue : pvalues) {
			map.put(String.valueOf(num), pvalue);
		}
		return bhCorrection(map);
	}

	/**
	 * Apply bh correction to the data in a tab separated file, stating which is
	 * the column of the keys and which is the column of the pvalues
	 *
	 * @param file
	 * @param indexColumn
	 * @return
	 * @throws IOException
	 */
	public static PValuesCollection bhCorrection(File file, int keyColumnIndex, int pvalueColumnIndex,
			boolean containsHeader) throws IOException {
		return bhCorrection(file, keyColumnIndex, pvalueColumnIndex, "\t", containsHeader);
	}

	/**
	 * Apply bh correction to the data in a text file which its columns are
	 * separated by a columnSeparator, stating which is the column of the keys
	 * and which is the column of the pvalues
	 *
	 * @param file
	 * @param indexColumn
	 * @return
	 * @throws IOException
	 */
	public static PValuesCollection bhCorrection(File file, int keyColumnIndex, int pvalueColumnIndex,
			String columnSeparator, boolean containsHeader) throws IOException {
		Map<String, Double> pValues = new HashMap<String, Double>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));

			String line = null;
			int num = 0;
			while ((line = br.readLine()) != null) {
				num++;
				if (num == 1 && containsHeader) {
					continue;
				}

				final String[] split = line.split(columnSeparator);
				String key = String.valueOf(num++);
				if (keyColumnIndex >= 0) {
					key = split[keyColumnIndex];
				}
				double pvalue = Double.valueOf(split[pvalueColumnIndex]);
				pValues.put(key, pvalue);
			}
			return bhCorrection(pValues);
		} finally {

			if (br != null) {
				br.close();
			}

		}
	}

}
