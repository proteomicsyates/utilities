package edu.scripps.yates.fasta;

import org.junit.Test;

import edu.scripps.yates.utilities.fasta.FastaParser;
import edu.scripps.yates.utilities.proteomicsmodel.Accession;
import junit.framework.Assert;

public class FastaParserTest {
	@Test
	public void testingAccessions() {
		Accession acc = FastaParser.getACC("tr|A0A0K8RYP3|A0A0K8RYP3_CROHD");
		Assert.assertEquals("A0A0K8RYP3", acc.getAccession());
		Assert.assertEquals("UNIPROT", acc.getAccessionType().name());
		acc = FastaParser.getACC("sw|A0A0K8RYP3|A0A0K8RYP3_CROHD");
		Assert.assertEquals("A0A0K8RYP3", acc.getAccession());
		Assert.assertEquals("UNIPROT", acc.getAccessionType().name());
		acc = FastaParser.getACC("sp|A0A0K8RYP3|A0A0K8RYP3_CROHD");
		Assert.assertEquals("A0A0K8RYP3", acc.getAccession());
		Assert.assertEquals("UNIPROT", acc.getAccessionType().name());
		acc = FastaParser.getACC("sw|A0A0K8RYP3|A0A0K8RYP3_CROHD ");
		Assert.assertEquals("A0A0K8RYP3", acc.getAccession());
		Assert.assertEquals("UNIPROT", acc.getAccessionType().name());
		acc = FastaParser.getACC(" A0A0K8RYP3|A0A0K8RYP3_CROHD");
		Assert.assertEquals("A0A0K8RYP3", acc.getAccession());
		Assert.assertEquals("UNIPROT", acc.getAccessionType().name());
		acc = FastaParser.getACC("sw|A0A0K8|A0A0K8RYP3_CROHD");
		Assert.assertEquals("A0A0K8", acc.getAccession());
		Assert.assertEquals("UNIPROT", acc.getAccessionType().name());
	}

}
