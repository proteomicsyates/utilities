package edu.scripps.yates.utilities.matrix.sparse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

import org.apache.log4j.Logger;

import edu.scripps.yates.utilities.dates.DatesUtil;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public abstract class SparseMatrix<T> {
	private final static Logger log = Logger.getLogger(SparseMatrix.class);
	// Maps with the indexes of the rows and the columns
	private final TObjectIntHashMap<String> rowIndexes = new TObjectIntHashMap<String>();
	private final TObjectIntHashMap<String> colIndexes = new TObjectIntHashMap<String>();

	// data by rows, then by cols
	private final TIntObjectHashMap<TIntObjectHashMap<T>> data = new TIntObjectHashMap<TIntObjectHashMap<T>>();

	// indexes where to store new cols and rows
	private int maxRowIndex = -1;
	private int maxColIndex = -1;

	// number of non-zero values
	private int numValues;

	public int getRowIndex(String rowName) {
		if (rowIndexes.containsKey(rowName)) {
			return rowIndexes.get(rowName);
		}
		return -1;
	}

	public int getColIndex(String colName) {
		if (colIndexes.containsKey(colName)) {
			return colIndexes.get(colName);
		}
		return -1;
	}

	public void addData(String rowName, String colName, T datapoint) {
		int row = getRowIndex(rowName);
		if (row == -1) {
			row = ++maxRowIndex;
			rowIndexes.put(rowName, row);
		}
		int col = getColIndex(colName);
		if (col == -1) {
			col = ++maxColIndex;
			colIndexes.put(colName, col);
		}
		setValue(row, col, datapoint);
	}

	public void setValue(int rowIndex, int colIndex, T value) {
		if (data.containsKey(rowIndex)) {
			final T previousValue = data.get(rowIndex).put(colIndex, value);
			if (previousValue == null) {
				numValues++;
			}
		} else {
			final TIntObjectHashMap<T> valuesPerColumn = new TIntObjectHashMap<T>();
			valuesPerColumn.put(colIndex, value);
			data.put(rowIndex, valuesPerColumn);
			numValues++;
		}
		if (numValues % 1000000 == 0) {

			System.out.println(numValues + " values in matrix with " + maxRowIndex + " rows and " + maxColIndex
					+ " cols (" + percentageFormat.format(getSparsity()) + " sparse values)");
		}
	}

	private double getSparsity() {
		final double denseMatrixSize = maxColIndex * maxRowIndex;
		final double numZeros = denseMatrixSize - numValues;
		return numZeros / denseMatrixSize;
	}

	private final static DecimalFormat percentageFormat = new DecimalFormat("#.#%");

	public static void main(String[] args) {
		final SparseMatrix<Short> matr = new SparseMatrix<Short>() {

			@Override
			public Short readFromString(String stringRepresentationOfTheData) {
				return Short.valueOf(stringRepresentationOfTheData);
			}
		};
		try {
			matr.loadFromFile(new File(
					"C:\\Users\\salvador\\Desktop\\casimir\\SARS_Cov2\\singlecell_human\\singlecells_human_ALL_fullTable_test.txt.gz"));
			final int rowIndex = 6101;
			final int colIndex = 13921;
			final Short value = matr.getValue(rowIndex, colIndex);
			final String cell = matr.getReverseRowColIndexes(matr.rowIndexes).get(rowIndex);
			final String gene = matr.getReverseRowColIndexes(matr.colIndexes).get(colIndex);
			System.out.println(gene + "\t" + cell + "\t" + value);

			final Map<String, Short> cellExpressions = matr.getRow(cell);
			for (final String gene2 : cellExpressions.keySet()) {
				if (Short.compare(matr.getValue(cell, gene2), cellExpressions.get(gene2)) == 0) {
					System.out.println(cell + "\t" + gene2 + "\t" + cellExpressions.get(gene2));
				} else {
					System.err.println("ERROR");
				}
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		final int colIndex = matr.getColIndex("asdf");
		System.out.println(colIndex);
	}

	public T deleteValue(String rowName, String colName) {
		return deleteValue(getRowIndex(rowName), getColIndex(colName));
	}

	public T deleteValue(int rowIndex, int colIndex) {
		if (data.containsKey(rowIndex)) {
			if (data.get(rowIndex).containsKey(colIndex)) {
				final T ret = data.get(rowIndex).remove(colIndex);
				if (ret != null) {
					numValues--;
				}
				if (data.get(rowIndex).isEmpty()) {
					data.remove(rowIndex);
				}
				return ret;
			}
		}
		return null;
	}

	private Map<String, T> getRow(String rowName) {
		final TIntObjectHashMap<String> reverseColIndexes = getReverseRowColIndexes(colIndexes);
		final Map<String, T> ret = new THashMap<String, T>();
		final int rowIndex = getRowIndex(rowName);
		if (rowIndex >= 0) {
			final TIntObjectHashMap<T> row = data.get(rowIndex);
			for (final int colIndex : row.keys()) {
				final String colName = reverseColIndexes.get(colIndex);
				final T value = row.get(colIndex);
				ret.put(colName, value);
			}
		}
		return ret;
	}

	public T getValue(String rowName, String colName) {
		return getValue(getRowIndex(rowName), getColIndex(colName));

	}

	public T getValue(int rowIndex, int colIndex) {
		if (rowIndex < 0 || colIndex < 0) {
			return null;
		}
		if (data.containsKey(rowIndex)) {
			return data.get(rowIndex).get(colIndex);
		}
		return null;
	}

	public void printToFile(File outputFile, boolean gzip) throws IOException {
		BufferedWriter bw = null;
		try {
			if (gzip) {
				final GZIPOutputStream zip = new GZIPOutputStream(new FileOutputStream(outputFile));
				bw = new BufferedWriter(new OutputStreamWriter(zip, "UTF-8"));
			} else {
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
			}
			// first we print the row names in a line separated by tabs
			final TIntObjectHashMap<String> reverseRowIndexes = getReverseRowColIndexes(rowIndexes);
			for (int row = 0; row <= maxRowIndex; row++) {
				bw.write(reverseRowIndexes.get(row) + "\t");
			}
			bw.write("\n");
			// second we print the col names in a line separated by tabs
			final TIntObjectHashMap<String> reverseColIndexes = getReverseRowColIndexes(colIndexes);
			for (int col = 0; col <= maxColIndex; col++) {
				bw.write(reverseColIndexes.get(col) + "\t");
			}
			bw.write("\n");
			// third we print all the triplets: row, col, value, one per line

			final TIntList rows = new TIntArrayList(data.keys());
			rows.sort();
			for (final int row : rows.toArray()) {
				for (final int col : data.get(row).keys()) {
					final T value = data.get(row).get(col);
					bw.write(row + "\t" + col + "\t" + value + "\n");
				}
			}

		} finally {
			if (bw != null) {
				bw.close();
				log.info("Matrix with " + numValues + " values, " + maxRowIndex + " rows and " + maxColIndex
						+ " columns and " + percentageFormat.format(getSparsity()) + " sparse values");
				log.info("File writen at: " + outputFile.getAbsolutePath() + " ("
						+ edu.scripps.yates.utilities.files.FileUtils.getDescriptiveSizeFromBytes(outputFile.length())
						+ ")");
			}
		}
	}

	public void loadFromFile(File sparceMatrixFile) throws IOException {
		final long t0 = System.currentTimeMillis();
		log.info("Loading matrix from file " + sparceMatrixFile.getAbsolutePath());
		BufferedReader br = null;
		try {
			InputStreamReader isr = null;
			try {

				final GZIPInputStream gzipIS = new GZIPInputStream(new FileInputStream(sparceMatrixFile));
				isr = new InputStreamReader(gzipIS);
			} catch (final ZipException e) {
				// try with regular reader
				isr = new InputStreamReader(new FileInputStream(sparceMatrixFile));
			}
			br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if ("".equals(line) || line.startsWith("#")) {
					continue;
				}
				final String[] split = line.split("\t");
				// first line are the row names
				if (rowIndexes.isEmpty()) {
					for (int row = 0; row < split.length; row++) {
						final String rowName = split[row];
						rowIndexes.put(rowName, row);
						maxColIndex = row;
					}
					continue;
				}
				// second line are the column names
				if (colIndexes.isEmpty()) {
					for (int col = 0; col < split.length; col++) {
						final String colName = split[col];
						colIndexes.put(colName, col);
						maxRowIndex = col;
					}
					continue;
				}
				// then the data starts in triplets
				final int row = Integer.valueOf(split[0]);
				final int col = Integer.valueOf(split[1]);
				final T datapoint = readFromString(split[2]);
				setValue(row, col, datapoint);
			}
		} finally {
			if (br != null) {
				br.close();
				final long t1 = System.currentTimeMillis() - t0;
				log.info("Matrix read with " + numValues + " values, " + maxRowIndex + " rows and " + maxColIndex
						+ " columns and " + percentageFormat.format(getSparsity()) + " sparse values in "
						+ DatesUtil.getDescriptiveTimeFromMillisecs(t1));
			}
		}
	}

	private TIntObjectHashMap<String> getReverseRowColIndexes(TObjectIntHashMap<String> map) {
		final TIntObjectHashMap<String> reverseIndexes = new TIntObjectHashMap<String>();
		final Object[] rowNames = map.keys();
		for (final Object rowName : rowNames) {
			final int index = map.get(rowName);
			reverseIndexes.put(index, (String) rowName);
		}
		return reverseIndexes;
	}

	/**
	 * This method needs to be defined so that when reading the file the object is
	 * read properly
	 * 
	 * @param stringRepresentationOfTheData
	 * @return
	 */
	public abstract T readFromString(String stringRepresentationOfTheData);
}
