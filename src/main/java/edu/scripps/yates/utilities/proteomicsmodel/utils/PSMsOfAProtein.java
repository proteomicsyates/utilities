package edu.scripps.yates.utilities.proteomicsmodel.utils;

import edu.scripps.yates.utilities.collections.ObservableArrayList;
import edu.scripps.yates.utilities.proteomicsmodel.PSM;
import edu.scripps.yates.utilities.proteomicsmodel.Protein;

public class PSMsOfAProtein extends ObservableArrayList<PSM> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5984394901348424280L;

	public PSMsOfAProtein(Protein protein) {
		super(new PSMsOfAProteinCollectionObserver(protein));
	}
}
