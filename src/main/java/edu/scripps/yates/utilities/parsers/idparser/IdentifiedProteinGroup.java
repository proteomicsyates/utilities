package edu.scripps.yates.utilities.parsers.idparser;

import java.util.ArrayList;

public class IdentifiedProteinGroup extends ArrayList<IdentifiedProteinInterface> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1051891453675105485L;

	/**
	 * This assumes that the {@link IdentifiedProteinInterface} only belongs to
	 * one group
	 */
	@Override
	public boolean add(IdentifiedProteinInterface e) {
		if (super.contains(e)) {
			return false;
		}
		e.addProteinGroup(this);
		return super.add(e);
	}

}
