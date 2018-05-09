package edu.scripps.yates.utilities.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.compomics.util.protein.Enzyme;

public class MyEnzyme extends Enzyme {
	private final Map<String, List<String>> cleavagesBySequence = new HashMap<String, List<String>>();
	private boolean cacheEnabled = false;

	public MyEnzyme(String aTitle, String aCleavage, String aRestrict, String aPosition, int aMiscleavages) {
		this(aTitle, aCleavage, aRestrict, aPosition, false);
	}

	public MyEnzyme(String aTitle, String aCleavage, String aRestrict, String aPosition, int aMiscleavages,
			boolean cacheEnabled) {
		super(aTitle, aCleavage, aRestrict, aPosition, aMiscleavages);
		this.cacheEnabled = cacheEnabled;
	}

	public MyEnzyme(com.compomics.util.experiment.biology.Enzyme enzyme, int maxMissedCleavages) {
		this(enzyme, maxMissedCleavages, false);
	}

	public MyEnzyme(com.compomics.util.experiment.biology.Enzyme enzyme, int maxMissedCleavages, boolean cacheEnabled) {
		super(enzyme, maxMissedCleavages);
		this.cacheEnabled = cacheEnabled;
	}

	public MyEnzyme(String aTitle, String aCleavage, String aRestrict, String aPosition) {
		this(aTitle, aCleavage, aRestrict, aPosition, false);
	}

	public MyEnzyme(String aTitle, String aCleavage, String aRestrict, String aPosition, boolean cacheEnabled) {
		super(aTitle, aCleavage, aRestrict, aPosition);
		this.cacheEnabled = cacheEnabled;
	}

	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	public void setCacheEnabled(boolean cacheEnabled) {
		this.cacheEnabled = cacheEnabled;
	}

	public void clearCache() {
		cleavagesBySequence.clear();
	}

	/**
	 * This method is the focus of the Enzyme instance. It can perform an
	 * <i>in-silico</i> digest of a Protein sequence according to the
	 * specifications detailed in the construction or via the setters. Only
	 * returns peptides between the minimum and maximum peptide lengths.
	 *
	 * @param aProtein
	 *            Protein instance to cleave.
	 * @param minPeptideLength
	 *            The minimum peptide length to consider
	 * @param maxPeptideLength
	 *            The maximum peptide length to consider
	 * @return Protein[] with the resultant peptides.
	 */
	public List<String> cleave(String aProtein, int minPeptideLength, int maxPeptideLength) {
		if (cacheEnabled && cleavagesBySequence.containsKey(aProtein)) {
			return cleavagesBySequence.get(aProtein);
		}
		final List<String> result = new ArrayList<String>();

		// We'll need a lot of stuff here.
		// - a Vector for all the startindices
		// - a Vector for the stopindices
		// - a Vector of intermediate results.
		final Vector startIndices = new Vector(20, 10);
		final Vector endIndices = new Vector(20, 10);
		final Vector interMed = new Vector(20, 10);

		// We will also feed the current Protein sequence into a
		// char[] for easy iteration.
		final char[] sequence = aProtein.toCharArray();

		// Check for a header that contains locations.
		final int headerStart = 0;

		// Okay, I guess we've set the stage now.
		// Let's start cleaving!
		int walkingIndex = 0;

		for (int i = 0; i < sequence.length; i++) {
			// Transform the current char into the corresponding wrapper.
			final Character current = Character.valueOf(sequence[i]);

			// See whether it is a cleavable residu!
			if (iCleavables.get(current) != null) {
				// Okay, this should be cleavable.
				// First of all however, we need to check
				// for the possible presence of a restrictor!
				// (And, of course, first check to see whether there is a
				// next character at all!)
				if ((i + 1) < sequence.length) {
					final Character next = Character.valueOf(sequence[i + 1]);
					if (iRestrictors.get(next) != null) {
						// It is a restrictor!
						// Just let the loop continue!
						continue;
					}
				}

				// Since we've gotten to here, we need to cleave here!
				// So do it!
				// Oh yeah, and mind the position of cleaving!
				String temp = null;
				int start = -1;
				int end = -1;

				if (getPosition() == Enzyme.CTERM) {
					// Take the part, starting from walkingIndex up to the
					// current
					// as a new peptide and store it in the interMed Vector.
					temp = new String(sequence, walkingIndex, ((i - walkingIndex) + 1));
					// Start index is human-readable (starting from 1),
					// hence the '+1'.
					start = headerStart + walkingIndex + 1;
					end = headerStart + i + 1;
					// Start the next peptide after the current one.
					// An index that so happens to
					walkingIndex = i + 1;
				} else if (getPosition() == Enzyme.NTERM) {
					temp = new String(sequence, walkingIndex, (i - walkingIndex));
					// Start index is human readable: starting from 1.
					start = headerStart + walkingIndex + 1;
					end = headerStart + i;
					walkingIndex = i;
				}

				// Add each retrieved value to the correct
				// Vector.
				interMed.add(temp);
				startIndices.add(Integer.valueOf(start));
				endIndices.add(Integer.valueOf(end));
			}
		}

		// Add this point, we should check whether we have
		// the entire sequence.
		// We probably don't, because the last cleavable residu will
		// probably not have been the last residu in the sequence.
		// That's why we should append the 'remainder' of our cleavage
		// as well (and the corresponding indices as well, of course).
		if (walkingIndex < sequence.length) {
			interMed.add(new String(sequence, walkingIndex, (sequence.length - walkingIndex)));
			startIndices.add(Integer.valueOf(headerStart + walkingIndex + 1));
			endIndices.add(Integer.valueOf(headerStart + sequence.length));
		}

		// Allright, now we should have all the individual peptides.
		// Now we should take into account the specified number of miscleavages.

		// Get all the sequences up to now.
		final String[] imSequences = (String[]) interMed.toArray(new String[interMed.size()]);

		// Cycle the current sequences.
		for (int j = 0; j < imSequences.length; j++) {

			String temp = imSequences[j];

			// Apply the number of allowed missed cleavages sequentially from
			// this sequence.
			for (int k = 0; k < getMiscleavages(); k++) {

				// If we fall outside of the range of current sequences
				// (for instance if we try to apply a second allowed missed
				// cleavage to the penultimate peptide, we fall outside of
				// the available peptides!)
				// we break the loop.
				if ((j + k + 1) >= imSequences.length) {
					break;
				}

				// Add our constructed sequence.
				temp += imSequences[j + k + 1];
				interMed.add(temp);
				startIndices.add(startIndices.get(j));
				endIndices.add(endIndices.get(j + k + 1));
			}
		}

		// Cycle all to check for

		// We've got all sequences.
		// Let's construct the Protein instances for them and
		// then return them!
		final int liSize = interMed.size();

		// Create the Proteins and store them.
		for (int i = 0; i < liSize; i++) {

			// If the sequence comes from a translation, it will contain an '_'
			// if a stopcodon is present.
			// Omit all sequences containing these.
			final String pepSequence = (String) interMed.get(i);
			if (pepSequence.indexOf("_") < 0) {

				// only include peptides within the min and max peptide lengths
				if (pepSequence.length() >= minPeptideLength && pepSequence.length() <= maxPeptideLength) {

					result.add(pepSequence);
				}
			}
		}
		if (cacheEnabled) {
			cleavagesBySequence.put(aProtein, result);
		}
		return result;
	}

}
