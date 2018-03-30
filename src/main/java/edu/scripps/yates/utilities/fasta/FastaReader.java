package edu.scripps.yates.utilities.fasta;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import gnu.trove.set.hash.THashSet;

public class FastaReader {

	public static final char FIRSTCHAROFDEFLINE = '>';
	public static final int DEFAULTSEQENCELENGTH = 1024;
	protected final String fastaFileName;
	private Integer numberFastas;

	public FastaReader(String fastaFileName) {
		this.fastaFileName = fastaFileName;
	}

	// Becareful, might need lots of memory
	public List<Fasta> getFastaList() throws IOException {
		List<Fasta> fastaList = new LinkedList<Fasta>();
		for (Iterator<Fasta> fastas = getFastas(); fastas.hasNext();) {
			fastaList.add(fastas.next());
		}
		return fastaList;
	}

	/**
	 * Parse fasta file to get total number of fasta entries
	 *
	 * @param fastaFileName
	 * @return total num of fasta entries
	 * @throws IOException
	 */
	public int getNumberFastas() throws IOException {
		if (numberFastas == null) {
			FileInputStream fis = new FileInputStream(fastaFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line;
			int total = 0;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty() && line.charAt(0) == FIRSTCHAROFDEFLINE) {
					++total;
				}
			}
			br.close();
			numberFastas = total;
		}
		return numberFastas;
	}

	public Iterator<Fasta> getFastas() throws IOException {
		FileInputStream is = new FileInputStream(fastaFileName);
		return new Iterator<Fasta>() {
			private String lastLine = ""; // remember the last line read
			private BufferedReader br;

			{
				br = new BufferedReader(new InputStreamReader(is));
				// remove the potential empty lines and get the first defline
				while ((lastLine = br.readLine()) != null && lastLine.equals(""))
					;

				if (lastLine.charAt(0) != FIRSTCHAROFDEFLINE) {
					throw new IOException();
				}
			}

			@Override
			public boolean hasNext() {
				return lastLine != null;
			}

			@Override
			public Fasta next() {

				Fasta fasta = null;
				try {
					fasta = getFasta();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return fasta;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Not supported");
			}

			private Fasta getFasta() throws IOException {

				final StringBuilder sb = new StringBuilder(DEFAULTSEQENCELENGTH);
				String defline = lastLine;

				// if the line read is a empty string, ignore it
				while ((lastLine = br.readLine()) != null
						&& (lastLine.equals("") || lastLine.charAt(0) != FIRSTCHAROFDEFLINE)) {
					// System.out.println(lastLine);
					if (!lastLine.equals("")) {
						String line = lastLine.trim();
						sb.append(line);
					}
				}

				// the lastLine should be the defline
				// and sb.toString should be the sequence
				return new FastaImpl(defline, sb.toString());
			}

			@Override
			protected void finalize() throws IOException {
				try {
					super.finalize();
				} catch (Throwable ex) {
					Logger.getLogger(FastaReader.class.getName()).log(Level.SEVERE, null, ex);
				}

				br.close();
				// System.out.println("Finalized");
			}
		};
	}

	public static void main(String args[]) throws IOException {
		/*
		 * for (Iterator<Fasta> itr = FastaReader.getFastas(new
		 * FileInputStream(args[0])); itr.hasNext(); ) { Fasta fasta =
		 * itr.next(); String defline = fasta.getDefline(); if(defline.contains(
		 * "Escherichia coli")) { System.out.println(">" + defline);
		 * System.out.println(fasta.getSequence()); } }
		 */

		if (true)
			return;

		int numEntries = 0;
		Set<String> accessions = new THashSet<String>(1000000);
		Set<String> sequestLikeAccs = new THashSet<String>(1000000);
		for (Iterator itr = new FastaReader(args[0]).getFastas(); itr.hasNext();) {
			FastaImpl fasta = (FastaImpl) itr.next();
			// System.out.println(fasta.getSequestLikeAccession());
			numEntries++;

			String defLine = fasta.getDefline();
			String seq = fasta.getSequence();

			accessions.add(fasta.getSequestLikeAccession());
			String sequestlikeac = fasta.getSequestLikeAccession();
			if (sequestlikeac.length() > 40) {
				sequestlikeac = fasta.getSequestLikeAccession().substring(0, 41);
			}
			sequestLikeAccs.add(sequestlikeac);
		}

		System.out.println("In fasta file " + args[0] + ":");
		System.out.println("Number of protein entries: " + numEntries);
		System.out.println("Number of unique accessions: " + accessions.size());
		System.out.println("Number of unique SEQUEST like accessions: " + sequestLikeAccs.size());

		/*
		 * for (Iterator itr = FastaReader.getFastas(new
		 * FileInputStream(args[0])); itr.hasNext(); ) { Fasta fasta = (Fasta)
		 * itr.next(); String defLine = fasta.getDefline(); String seq =
		 * fasta.getSequence(); if(defLine.startsWith("Rever")) {
		 * //System.out.println("Reversed: " + defLine); if(seq.endsWith("M")) {
		 * seq = seq.substring(0, seq.length() -1); } } else {
		 * if(seq.startsWith("M")) { seq = seq.substring(1, seq.length());
		 * 
		 * } //System.out.println("Regular: " + defLine); }
		 * 
		 * System.out.println(">" + defLine); System.out.println(seq); }
		 */

	}
}
