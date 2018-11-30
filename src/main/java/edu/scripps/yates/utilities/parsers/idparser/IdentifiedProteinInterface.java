package edu.scripps.yates.utilities.parsers.idparser;

import java.util.List;

public interface IdentifiedProteinInterface {

	public Integer getSpectrumCount();

	public Double getNsaf();

	public Double getEmpai();

	public String getAccession();

	public String getDescription();

	public String getSearchEngine();

	public void mergeWithProtein(IdentifiedProteinInterface protein);

	public double getCoverage();

	public Integer getLength();

	public String getLocus();

	public Double getMw();

	public Double getNsaf_norm();

	public double getPi();

	public void addProteinGroup(IdentifiedProteinGroup group);

	public List<IdentifiedProteinGroup> getProteinGroups();

	public List<IdentifiedPSMInterface> getPSMs();

	public void addPSM(IdentifiedPSMInterface psm);

}
