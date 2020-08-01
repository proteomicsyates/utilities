package edu.scripps.yates.fasta.notshare;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.compomics.dbtoolkit.io.implementations.FASTADBLoader;

import edu.scripps.yates.utilities.progresscounter.ProgressCounter;
import edu.scripps.yates.utilities.progresscounter.ProgressPrintingType;

public class FastaStatistics {
	@Test
	public void test() {
		System.out.println(Double.POSITIVE_INFINITY);
		System.out.println(Double.parseDouble("Infinity"));
		System.out.println(Double.NEGATIVE_INFINITY);
		System.out.println(Double.parseDouble("-Infinity"));
		System.out.println(Double.parseDouble("NEGATIVE_INFINITY"));
	}

	@Test
	public void asdf() {
		System.out.println(Double.valueOf("-Infinity"));
		System.out.println(Double.valueOf("Infinity"));
	}

	@Test
	public void countAAPairFrequency() {
		final File fasta = new File("D:\\Salva\\tmp\\sp_human_21March2018.fasta");
		final FASTADBLoader loader = new FASTADBLoader();
		final Map<String, Long> map = new HashMap<String, Long>();
		final List<String> pairs = new ArrayList<String>();
		if (loader.canReadFile(fasta)) {
			try {
				loader.load(fasta.getAbsolutePath());
				System.out.println("Fasta with " + loader.countNumberOfEntries() + " proteins");
				final ProgressCounter counter = new ProgressCounter(loader.countNumberOfEntries(),
						ProgressPrintingType.PERCENTAGE_STEPS, 0);
				long total = 0;
				com.compomics.util.protein.Protein protein;
				while ((protein = loader.nextProtein()) != null) {
					counter.increment();
					final String sequence = protein.getSequence().getSequence();
					final String progress = counter.printIfNecessary();

					for (int i = 2; i < sequence.length(); i++) {
						total += 1l;
						final String pair = sequence.substring(i - 2, i);
						if (!map.containsKey(pair)) {
							map.put(pair, 1l);
							pairs.add(pair);
						} else {
							map.put(pair, map.get(pair) + 1l);
						}
					}

				}
				System.out.println("fasta readed with " + pairs.size() + " different pairs");
				Collections.sort(pairs);
				final DecimalFormat df = new DecimalFormat("#.###");
				for (final String pair : pairs) {
					final double percentage = map.get(pair) * 1.0 / total * 100.0;
					System.out.println(pair + "\t" + map.get(pair) + "\t" + df.format(percentage) + "%");
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void countAAPairWithXaminacidsInBetweenFrequency() {
		final File fasta = new File("D:\\Salva\\tmp\\sp_human_21March2018.fasta");
		final FASTADBLoader loader = new FASTADBLoader();
		final Map<Integer, Map<String, Long>> maps = new HashMap<Integer, Map<String, Long>>();
		final Map<Integer, Long> totalMap = new HashMap<Integer, Long>();
		final Set<String> totalPairs = new HashSet<String>();
		for (int numInBetween = 1; numInBetween <= 4; numInBetween++) {
			final Map<String, Long> map = new HashMap<String, Long>();
			maps.put(numInBetween, map);
			final List<String> pairs = new ArrayList<String>();
			if (loader.canReadFile(fasta)) {
				try {
					loader.load(fasta.getAbsolutePath());
					System.out.println("Fasta with " + loader.countNumberOfEntries() + " proteins");
					final ProgressCounter counter = new ProgressCounter(loader.countNumberOfEntries(),
							ProgressPrintingType.PERCENTAGE_STEPS, 0);
					long total = 0;
					com.compomics.util.protein.Protein protein;
					while ((protein = loader.nextProtein()) != null) {
						counter.increment();
						final String sequence = protein.getSequence().getSequence();
						final String progress = counter.printIfNecessary();
						for (int i = 2 + numInBetween; i < sequence.length(); i++) {
							total += 1l;
							final String pair = sequence.substring(i - 2 - numInBetween, i);
							final String key = pair.substring(0, 1) + pair.substring(pair.length() - 1, pair.length());
							if (!map.containsKey(key)) {
								map.put(key, 1l);
								pairs.add(key);
							} else {
								map.put(key, map.get(key) + 1l);
							}
						}

					}
					totalPairs.addAll(pairs);
					totalMap.put(numInBetween, total);
					System.out.println("fasta readed with " + pairs.size() + " different pairs with " + numInBetween
							+ " X aminoacids in between");

				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		final DecimalFormat df = new DecimalFormat("#.###");
		System.out.print("AA pair ");
		for (int numInBetween = 1; numInBetween <= 4; numInBetween++) {
			System.out.print("\t frequency (" + numInBetween + ")\t percentaje (" + numInBetween + ")");
		}
		System.out.println();
		final List<String> totalPairsList = new ArrayList<String>();
		totalPairsList.addAll(totalPairs);
		Collections.sort(totalPairsList);
		for (final String pair : totalPairsList) {
			System.out.print(pair + "\t");
			for (int numInBetween = 1; numInBetween <= 4; numInBetween++) {
				final Map<String, Long> map = maps.get(numInBetween);
				final long total = totalMap.get(numInBetween);
				double percentage = 0;
				Long frequency = 0l;
				if (map.containsKey(pair)) {
					frequency = map.get(pair);
					percentage = frequency * 1.0 / total * 100.0;
				}
				System.out.print(frequency + "\t" + df.format(percentage) + "%\t");

			}
			System.out.println();

		}
	}
}
