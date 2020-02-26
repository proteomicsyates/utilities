package edu.scripps.yates.utilities.proteomicsmodel.factories;

import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.scripps.yates.utilities.proteomicsmodel.AbstractPeptide;

public class PeptideEx extends AbstractPeptide implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6127166815987338850L;

	private static Logger log = Logger.getLogger(PeptideEx.class);

	public PeptideEx(String fullSequence) {
		super(fullSequence);
		setFullSequence(fullSequence);

	}

}
