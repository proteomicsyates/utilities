package edu.scripps.yates.utilities.proteomicsmodel.factories;

import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.scripps.yates.utilities.proteomicsmodel.AbstractProtein;
import edu.scripps.yates.utilities.proteomicsmodel.Accession;
import edu.scripps.yates.utilities.proteomicsmodel.Organism;
import edu.scripps.yates.utilities.proteomicsmodel.enums.AccessionType;

public class ProteinEx extends AbstractProtein implements Serializable {
	private static final Logger log = Logger.getLogger(ProteinEx.class);
	/**
	 *
	 */
	private static final long serialVersionUID = -1435542806814270031L;
	protected static int numInstances = 0;

	public ProteinEx(AccessionType accessionType, String accession, Organism organism) {
		final AccessionEx accessionEx = new AccessionEx(accession, accessionType);
		setPrimaryAccession(accessionEx);
		setOrganism(organism);
		numInstances++;
		log.debug(numInstances + " proteinEx");
	}

	public ProteinEx(AccessionType accessionType, String accession) {
		this(accessionType, accession, null);
	}

	public ProteinEx(Accession accession) {
		this(accession.getAccessionType(), accession.getAccession());
	}

}
