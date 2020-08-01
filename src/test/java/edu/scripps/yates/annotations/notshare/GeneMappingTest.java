package edu.scripps.yates.annotations.notshare;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import edu.scripps.yates.utilities.annotations.uniprot.UniprotGeneMapping;

public class GeneMappingTest {
	private final static String uniprotpath = "Z:\\share\\Salva\\data\\uniprotKB";

	@Test
	public void testingGeneMapping() {
		final UniprotGeneMapping instance = UniprotGeneMapping.getInstance(new File(uniprotpath), "Human");
		try {
			final Set<String> uniprots = instance.mapGeneToUniprotACC("CDK9");
			for (final String string : uniprots) {
				System.out.println(string);
			}
		} catch (final IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}
