package edu.scripps.yates.utilities.strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gnu.trove.list.array.TIntArrayList;

/**
 * Class that provides a {@link String} comparator with some additional features
 *
 * @author Salva
 *
 */
public class StringUtils {
	/**
	 * Comparison between two {@link String} with several options
	 *
	 * @param text1
	 * @param text2
	 * @param ignoreCase
	 *            the case will be ignored
	 * @param performContains
	 *            if true, it will return true if one String contains the other
	 *            (see reverseComparison paremeter)
	 * @param reverseComparison
	 *            if false, the containing check only will return true if text1
	 *            contains text2. If true, it will also return true if text2
	 *            contains text1. This parameter will be ignored if
	 *            performContains is false.
	 * @return
	 */
	public static boolean compareStrings(String text1, String text2, boolean ignoreCase, boolean performContains,
			boolean reverseComparison) {
		if (text1 == null && text2 == null)
			return true;
		if ((text1 == null && text2 != null) || (text1 != null && text2 == null))
			return false;

		if (ignoreCase && text1.equalsIgnoreCase(text2))
			return true;
		if (!ignoreCase && text1.equals(text2))
			return true;
		if (performContains) {
			if (ignoreCase && text1.toLowerCase().contains(text2.toLowerCase()))
				return true;
			if (!ignoreCase && text1.contains(text2))
				return true;
			if (reverseComparison) {
				if (ignoreCase && text2.toLowerCase().contains(text1.toLowerCase()))
					return true;
				if (!ignoreCase && text2.contains(text1))
					return true;
			}
		}
		return false;
	}

	/**
	 * Search the targetString in the source string and returns the positions
	 * (starting by 1) in which the targetString appears in the sourceString
	 *
	 * @param sourceString
	 * @param targetString
	 * @return the list of positions or an empty list if not found
	 */
	public static TIntArrayList allPositionsOf(String sourceString, String targetString) {
		final TIntArrayList ret = new TIntArrayList();
		if (sourceString != null && targetString != null && !"".equals(sourceString) && !"".equals(targetString)) {
			final Pattern p = Pattern.compile(targetString, Pattern.LITERAL);
			final Matcher m = p.matcher(sourceString);
			int start = 0;
			while (m.find(start)) {
				start = m.start() + 1;
				ret.add(start);
			}
		}
		return ret;
	}

	/**
	 * Search the targetString in the source string and returns the positions
	 * (starting by 1) in which the targetString appears in the sourceString
	 *
	 * @param sourceString
	 * @param targetString
	 * @return
	 */
	public static TIntArrayList allPositionsOf(String sourceString, char targetCharacter) {
		return allPositionsOf(sourceString, String.valueOf(targetCharacter));
	}

	/**
	 * Gives a sorted list of positions in which some of the {@link Character}
	 * in chars is found in the sourceString
	 * 
	 * @param sourceString
	 * @param quantifiedAAs
	 * @return
	 */
	public static TIntArrayList getPositions(String sourceString, char[] chars) {
		final TIntArrayList ret = new TIntArrayList();

		for (int index = 0; index < sourceString.length(); index++) {
			for (final char c : chars) {
				if (c == sourceString.charAt(index)) {
					final int position = index + 1;
					ret.add(position);
					break;
				}
			}
		}
		ret.sort();

		return ret;
	}

	public static String convertStreamToString(InputStream is, int bufferSize, String encoding) throws IOException {

		final Reader reader = new BufferedReader(new InputStreamReader(is, encoding));
		final StringBuffer content = new StringBuffer();
		final char[] buffer = new char[bufferSize];
		int n;

		while ((n = reader.read(buffer)) != -1) {
			content.append(buffer, 0, n);
		}

		return content.toString();
	}

	public static String getSeparatedValueStringFromChars(char[] chars, String separator) {
		final StringBuilder sb = new StringBuilder();
		for (final char c : chars) {
			if (!"".equals(sb.toString())) {
				sb.append(separator);
			}
			sb.append(c);
		}
		return sb.toString();
	}

	public static String getCommonBeginning(String string1, String string2) {
		final StringBuilder ret = new StringBuilder();
		for (int index = 0; index < Math.min(string1.length(), string2.length()); index++) {

			final char charAt = string1.charAt(index);
			if (charAt == string2.charAt(index)) {
				ret.append(charAt);
			} else {
				break;
			}
		}
		return ret.toString();
	}
}
