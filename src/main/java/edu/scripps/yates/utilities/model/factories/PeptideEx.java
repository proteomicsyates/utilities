package edu.scripps.yates.utilities.model.factories;

import java.util.Set;

import org.apache.log4j.Logger;

import edu.scripps.yates.utilities.proteomicsmodel.Amount;
import edu.scripps.yates.utilities.proteomicsmodel.Condition;
import edu.scripps.yates.utilities.proteomicsmodel.MSRun;
import edu.scripps.yates.utilities.proteomicsmodel.PSM;
import edu.scripps.yates.utilities.proteomicsmodel.Peptide;
import edu.scripps.yates.utilities.proteomicsmodel.Protein;
import edu.scripps.yates.utilities.proteomicsmodel.Ratio;
import edu.scripps.yates.utilities.proteomicsmodel.Score;
import gnu.trove.set.hash.THashSet;

public class PeptideEx implements Peptide {

	private final Set<PSM> psms = new THashSet<PSM>();
	private final Set<Ratio> ratios = new THashSet<Ratio>();
	private final Set<Amount> amounts = new THashSet<Amount>();
	private Set<Score> scores;
	private final String sequence;
	private final Set<Protein> proteins = new THashSet<Protein>();
	private final Set<Condition> conditions = new THashSet<Condition>();
	private MSRun msRun;
	private static Logger log = Logger.getLogger(PeptideEx.class);

	public PeptideEx(String sequence, MSRun msrun) {
		this.sequence = sequence;
		msRun = msrun;
	}

	@Override
	public int getDBId() {
		return -1;
	}

	@Override
	public void addPSM(PSM psm) {
		if (psm != null) {
			String runId = psm.getMSRun().getRunId();

			for (PSM psm2 : psms) {
				if (!psm2.getMSRun().getRunId().equals(runId)) {
					throw new IllegalArgumentException("A peptide should belong to PSMs from the same RUN id\n"
							+ "Should be the same run id: '" + psm2.getMSRun().getRunId() + "'\t'" + runId + "'");
				}
			}
			if (!psms.contains(psm)) {
				psms.add(psm);
				psm.setPeptide(this);
			}
		}
	}

	@Override
	public Set<Ratio> getRatios() {
		return ratios;
	}

	@Override
	public void addRatio(Ratio ratio) {
		if (ratio != null && !ratios.contains(ratio)) {
			ratios.add(ratio);
		}
	}

	@Override
	public Set<Amount> getAmounts() {
		return amounts;
	}

	@Override
	public void addAmount(Amount amount) {
		if (amount != null && !amounts.contains(amount))
			amounts.add(amount);
	}

	@Override
	public Set<Score> getScores() {
		return scores;
	}

	@Override
	public void addScore(Score score) {
		if (scores == null) {
			scores = new THashSet<Score>();
		}
		if (score != null) {
			scores.add(score);
		}
	}

	@Override
	public String getSequence() {
		return sequence;
	}

	/**
	 * @param scores
	 *            the scores to set
	 */
	public void setScores(Set<Score> scores) {
		this.scores = scores;
	}

	@Override
	public Set<Protein> getProteins() {
		return proteins;
	}

	@Override
	public void addProtein(Protein protein) {

		if (protein != null && !proteins.contains(protein)) {
			proteins.add(protein);
			protein.addPeptide(this);
			for (PSM psm : getPSMs()) {
				protein.addPSM(psm);
				psm.addProtein(protein);
			}
		}
	}

	@Override
	public Set<Condition> getConditions() {
		return conditions;
	}

	@Override
	public void addCondition(Condition newCondition) {
		if (newCondition != null) {
			boolean found = false;
			for (Condition condition : conditions) {
				if (condition.getName().equals(newCondition.getName())) {
					if (condition.getProject().getName().equals(newCondition.getProject().getName()))
						found = true;
				}

			}
			if (!found) {
				conditions.add(newCondition);
				// set condition to amounts
				for (Amount amount : getAmounts()) {
					if (amount.getCondition() == null && amount instanceof AmountEx) {
						((AmountEx) amount).setCondition(newCondition);
					}
				}
			}
		}
	}

	@Override
	public MSRun getMSRun() {
		return msRun;
	}

	@Override
	public String toString() {
		return sequence + " in run: " + getMSRun().getRunId();
	}

	@Override
	public Set<PSM> getPSMs() {
		return psms;
	}

	@Override
	public void setMSRun(MSRun msRun) {
		this.msRun = msRun;

	}

}
