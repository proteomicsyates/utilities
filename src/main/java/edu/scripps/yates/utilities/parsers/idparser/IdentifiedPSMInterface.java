package edu.scripps.yates.utilities.parsers.idparser;

import java.util.Set;

public interface IdentifiedPSMInterface {
	public Set<IdentifiedProteinInterface> getProteins();

	public void addProtein(IdentifiedProteinInterface protein);

	public String getFullSequence();

	public String getRawFileName();
}
