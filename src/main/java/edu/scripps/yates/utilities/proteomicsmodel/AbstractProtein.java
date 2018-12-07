package edu.scripps.yates.utilities.proteomicsmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import edu.scripps.yates.utilities.grouping.GroupablePeptide;
import edu.scripps.yates.utilities.grouping.ProteinEvidence;
import edu.scripps.yates.utilities.grouping.ProteinGroup;
import edu.scripps.yates.utilities.proteomicsmodel.enums.AccessionType;
import edu.scripps.yates.utilities.proteomicsmodel.factories.AccessionEx;
import gnu.trove.set.hash.THashSet;

public abstract class AbstractProtein implements Protein {

	private Set<Score> scores;
	private Set<Ratio> ratios;
	private Set<Amount> amounts;
	private Set<Condition> conditions;
	private List<PSM> psms;
	protected Integer spectrumCount;
	private Set<MSRun> msRuns;
	private ProteinGroup proteinGroup;
	private ProteinEvidence evidence;
	private Accession primaryAccession;
	private Set<Accession> secondaryAccessions;
	private Set<Gene> genes;
	private Set<ProteinAnnotation> annotations;
	private Set<Threshold> thresholds;
	private Set<Peptide> peptides;
	private Double coverage;
	private Double empai;
	private Double mw;
	private Double nsaf_norm;
	private Double nsaf;
	private String searchEngine;
	private Double pi;
	protected Integer length;
	private Organism organism;
	private String sequence;

	@Override
	public Set<Score> getScores() {
		return scores;
	}

	@Override
	public boolean addScore(Score score) {
		if (score != null) {
			if (scores == null) {
				scores = new THashSet<Score>();
			}
			return scores.add(score);
		}
		return false;
	}

	@Override
	public Set<Ratio> getRatios() {
		return ratios;
	}

	@Override
	public boolean addRatio(Ratio ratio) {
		if (ratio != null) {
			if (ratios == null) {
				ratios = new THashSet<Ratio>();
			}
			return ratios.add(ratio);
		}
		return false;
	}

	@Override
	public Set<Amount> getAmounts() {
		return amounts;
	}

	@Override
	public boolean addAmount(Amount amount) {
		if (amount != null) {
			if (amounts == null) {
				amounts = new THashSet<Amount>();
			}
			return amounts.add(amount);
		}
		return false;
	}

	@Override
	public Set<Condition> getConditions() {
		return conditions;
	}

	@Override
	public boolean addCondition(Condition condition) {
		if (condition != null) {
			if (conditions == null) {
				conditions = new THashSet<Condition>();
			}
			return conditions.add(condition);
		}
		return false;
	}

	@Override
	public List<PSM> getPSMs() {
		if (psms == null) {
			psms = new ArrayList<PSM>();
		}
		return psms;
	}

	@Override
	public boolean addPSM(PSM psm, boolean recursively) {
		if (psm != null) {
			if (psms == null) {
				psms = new ArrayList<PSM>();
			}
			final boolean ret = psms.add(psm);
			if (recursively) {
				addPeptide(psm.getPeptide(), false);
				if (psm.getPeptide() != null) {
					psm.getPeptide().addProtein(this, false);
				}
				psm.addProtein(this, false);
			}
			return ret;
		}
		return false;
	}

	@Override
	public Integer getSpectrumCount() {
		if (spectrumCount != null) {
			return spectrumCount;
		}
		return getPSMs().size();
	}

	@Override
	public Set<MSRun> getMSRuns() {
		return msRuns;
	}

	@Override
	public boolean addMSRun(MSRun msRun) {
		if (msRun != null) {
			if (msRuns == null) {
				msRuns = new THashSet<MSRun>();
			}
			return msRuns.add(msRun);
		}
		return false;
	}

	@Override
	public List<GroupablePeptide> getGroupablePeptides() {
		final List<GroupablePeptide> ret = new ArrayList<GroupablePeptide>();
		for (final GroupablePeptide groupablePeptide : getPSMs()) {
			ret.add(groupablePeptide);
		}
		return ret;
	}

	@Override
	public ProteinGroup getProteinGroup() {
		return proteinGroup;
	}

	@Override
	public int getUniqueID() {
		return HashCodeBuilder.reflectionHashCode(this, false);
	}

	@Override
	public String getAccession() {
		return getPrimaryAccession().getAccession();
	}

	@Override
	public void setEvidence(ProteinEvidence evidence) {
		this.evidence = evidence;
	}

	@Override
	public ProteinEvidence getEvidence() {
		return evidence;
	}

	@Override
	public void setProteinGroup(ProteinGroup proteinGroup) {
		this.proteinGroup = proteinGroup;
	}

	@Override
	public Accession getPrimaryAccession() {
		return primaryAccession;
	}

	@Override
	public Set<Accession> getSecondaryAccessions() {
		return secondaryAccessions;
	}

	@Override
	public Set<Gene> getGenes() {
		return genes;
	}

	@Override
	public Set<ProteinAnnotation> getAnnotations() {
		return annotations;
	}

	@Override
	public Set<Threshold> getThresholds() {
		return thresholds;
	}

	@Override
	public boolean addThreshold(Threshold threshold) {
		if (threshold != null) {
			if (thresholds == null) {
				thresholds = new THashSet<Threshold>();
			}
			return thresholds.add(threshold);
		}
		return false;
	}

	@Override
	public Boolean passThreshold(String thresholdName) {
		if (thresholds != null) {
			for (final Threshold threshold : thresholds) {
				if (threshold.getName().equalsIgnoreCase(thresholdName)) {
					return threshold.isPassThreshold();
				}
			}
		}
		return null;
	}

	@Override
	public Set<Peptide> getPeptides() {
		return peptides;
	}

	@Override
	public Integer getLength() {
		return length;
	}

	@Override
	public Double getPi() {
		return pi;
	}

	@Override
	public String getSequence() {
		return sequence;
	}

	@Override
	public Organism getOrganism() {
		return organism;
	}

	@Override
	public void setOrganism(Organism organism) {
		this.organism = organism;
	}

	@Override
	public void setMw(Double mw) {
		this.mw = mw;
	}

	@Override
	public void setPi(Double pi) {
		this.pi = pi;
	}

	@Override
	public void setLength(Integer length) {
		this.length = length;
	}

	@Override
	public boolean addPeptide(Peptide peptide, boolean recursively) {
		if (peptide != null) {
			if (peptides == null) {
				peptides = new THashSet<Peptide>();
			}
			final boolean ret = peptides.add(peptide);
			if (recursively) {
				for (final PSM psm : peptide.getPSMs()) {
					addPSM(psm, false);
					psm.addProtein(this, false);
				}
				peptide.addProtein(this, false);
			}
			return ret;
		}
		return false;
	}

	@Override
	public boolean addGene(Gene gene) {
		if (gene != null) {
			if (genes == null) {
				genes = new THashSet<Gene>();
			}
			return genes.add(gene);
		}
		return false;
	}

	@Override
	public void mergeWithProtein(Protein protein) {

		if (getCoverage() == -1.0) {
			setCoverage(protein.getCoverage());
		}
		final String description = protein.getDescription();
		if (getPrimaryAccession() != null) {
			getPrimaryAccession().setDescription(description);
		}
		if (protein.getEmpai() != null) {
			setEmpai(protein.getEmpai());
		}
		if (protein.getLength() != null) {
			setLength(protein.getLength());
		}

		if (getMw() == null) {
			setMw(protein.getMw());
		}
		if (protein.getNsaf() != null) {
			setNsaf(protein.getNsaf());
		}
		if (getNsaf_norm() == -1.0) {
			setNsaf_norm(protein.getNsaf_norm());
		}
		if (getPi() == null) {
			setPi(protein.getPi());
		}
		// protein may have to be grouped again since it contains new PSMs
		setProteinGroup(null);
		// spectrumCount may not be accurate now with new psms
		setSpectrumCount(null);

		if (!protein.getPSMs().isEmpty()) {
			for (final PSM psm : protein.getPSMs()) {
				addPSM(psm, true);
				psm.getProteins().remove(protein);
			}

		}

		if (getSearchEngine() == null) {
			setSearchEngine(protein.getSearchEngine());
		}
	}

	@Override
	public Double getCoverage() {
		return coverage;
	}

	@Override
	public Double getEmpai() {
		return empai;
	}

	@Override
	public String getDescription() {
		return getPrimaryAccession().getDescription();
	}

	@Override
	public Double getMw() {
		return mw;
	}

	@Override
	public Double getNsaf_norm() {
		return nsaf_norm;
	}

	@Override
	public Double getNsaf() {
		return nsaf;
	}

	@Override
	public String getSearchEngine() {
		return searchEngine;
	}

	@Override
	public void setCoverage(Double coverage) {
		this.coverage = coverage;
	}

	@Override
	public void setNsaf_norm(Double nsaf_norm) {
		this.nsaf_norm = nsaf_norm;
	}

	@Override
	public void setNsaf(Double nsaf) {
		this.nsaf = nsaf;
	}

	@Override
	public void setEmpai(Double empai) {
		this.empai = empai;
	}

	@Override
	public void setPrimaryAccession(Accession accession) {
		primaryAccession = accession;
	}

	@Override
	public void setSearchEngine(String searchEngine) {
		this.searchEngine = searchEngine;
	}

	@Override
	public void setSpectrumCount(Integer spc) {
		spectrumCount = spc;
	}

	@Override
	public void setPrimaryAccession(AccessionType accessionType, String accession) {
		final AccessionEx newAccession = new AccessionEx(accession, accessionType);
		setPrimaryAccession(newAccession);
	}

	@Override
	public boolean addSecondaryAccession(AccessionType accessionType, String accession) {

		if (accessionType != null && accession != null) {
			final AccessionEx newAccession = new AccessionEx(accession, accessionType);
			return addSecondaryAccession(newAccession);
		}
		return false;
	}

	@Override
	public boolean addSecondaryAccession(Accession accession) {
		if (accession != null) {
			if (secondaryAccessions == null) {
				secondaryAccessions = new THashSet<Accession>();
			}

			return secondaryAccessions.add(accession);
		}
		return false;
	}

	@Override
	public boolean addProteinAnnotation(ProteinAnnotation proteinAnnotation) {

		if (proteinAnnotation != null) {
			if (annotations == null)
				annotations = new THashSet<ProteinAnnotation>();
			return annotations.add(proteinAnnotation);
		}
		return false;
	}

	@Override
	public void addProteinAnnotations(Collection<ProteinAnnotation> proteinAnnotations) {
		for (final ProteinAnnotation annotation : proteinAnnotations) {
			addProteinAnnotation(annotation);
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		if (getMSRuns() != null) {
			sb.append(" in run(s): ");
			for (final MSRun msRun : getMSRuns()) {
				if (!"".equals(sb.toString())) {
					sb.append(",");
				}
				sb.append(msRun.getRunId());
			}
		}
		return getAccession() + sb.toString();
	}
}
