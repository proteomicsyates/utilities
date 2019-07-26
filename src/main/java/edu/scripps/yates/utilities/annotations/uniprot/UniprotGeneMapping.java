package edu.scripps.yates.utilities.annotations.uniprot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import edu.scripps.yates.utilities.files.FileUtils;
import edu.scripps.yates.utilities.files.ZipManager;
import edu.scripps.yates.utilities.ftp.FTPUtils;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

public class UniprotGeneMapping {

	private static final String SEPARATOR = "\t";
	/**
	 * Gene name to Uniprot accession map.
	 */
	private final Map<Gene, Set<String>> geneNameToAccession = new THashMap<Gene, Set<String>>();
	private final Map<String, Set<Gene>> accessionToGeneNames = new THashMap<String, Set<Gene>>();
	private boolean loaded = false;
	private final File uniprotPath;
	private final String taxonomy;
	private static final String uniprotHostName = "ftp.uniprot.org";
	private static Map<String, UniprotGeneMapping> instances = new THashMap<String, UniprotGeneMapping>();
	private static String uniprotFTPRemoteFolder = "/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/";
	private static Logger log = Logger.getLogger(UniprotGeneMapping.class);
	public static final String GENE_SYNONM = "Gene_Synonym";
	public static final String GENE_NAME = "Gene_Name";
	public static final String ENSEMBL = "Ensembl";

	public UniprotGeneMapping(File uniprotPath, String taxonomy) {
		this.uniprotPath = uniprotPath;
		this.taxonomy = taxonomy;
	}

	public static UniprotGeneMapping getInstance(File uniprotPath, String taxonomy) {
		if (!instances.containsKey(taxonomy)) {
			instances.put(taxonomy, new UniprotGeneMapping(uniprotPath, taxonomy));
		}
		return instances.get(taxonomy);
	}

	public Set<String> mapGeneToUniprotACC(String geneNameOrID) throws IOException {
		if (!loaded) {
			importFromFile(getMappingFile());
		}
		if (geneNameToAccession.containsKey(geneNameOrID)) {
			return geneNameToAccession.get(geneNameOrID);
		}
		return Collections.emptySet();
	}

	/**
	 * Gets a Set of all gene names mapped to this uniprot acc
	 * 
	 * @param uniprotACC
	 * @return
	 * @throws IOException
	 */
	public Set<String> mapUniprotACCToGene(String uniprotACC) throws IOException {
		if (!loaded) {
			importFromFile(getMappingFile());
		}
		if (accessionToGeneNames.containsKey(uniprotACC)) {
			final Set<Gene> genes = accessionToGeneNames.get(uniprotACC);
			return genes.stream().map(gene -> gene.getName()).collect(Collectors.toSet());

		}
		return Collections.emptySet();
	}

	/**
	 * Gets a map of all gene names mapped to this uniprot acc mapped by its
	 * gene type
	 * 
	 * @param uniprotACC
	 * @return
	 * @throws IOException
	 */
	public Map<String, String> mapUniprotACCToGeneByType(String uniprotACC) throws IOException {
		if (!loaded) {
			importFromFile(getMappingFile());
		}
		if (accessionToGeneNames.containsKey(uniprotACC)) {
			final Set<Gene> genes = accessionToGeneNames.get(uniprotACC);
			final Map<String, String> map = new THashMap<String, String>();
			genes.forEach(gene -> map.put(gene.getType(), gene.getName()));
			return map;

		}
		return Collections.emptyMap();
	}

	private File getMappingFile() throws IOException {
		final File mappingFolder = new File(uniprotPath.getAbsolutePath() + File.separator + "genemap");
		final File[] listFiles = mappingFolder.listFiles();
		if (listFiles != null) {
			for (final File file : listFiles) {
				if (file.length() > 0l) {
					if (FilenameUtils.getExtension(file.getAbsolutePath()).equals("dat")) {
						if (FilenameUtils.getBaseName(file.getAbsolutePath().toLowerCase())
								.contains(taxonomy.toLowerCase())) {
							return file;
						}
					}
				}
			}
		}
		log.info("mapping file for taxonomy '" + taxonomy + "' not found. Going to uniprot at "
				+ uniprotFTPRemoteFolder);
		final FTPClient ftpClient = FTPUtils.loginFTPClient(uniprotHostName, "anonymous", "");
		// if not found, go to remote location
		final List<FTPFile> filesInFolderByExtension = FTPUtils.getFilesInFolderByExtension(ftpClient,
				uniprotFTPRemoteFolder, "gz");
		for (final FTPFile ftpFile : filesInFolderByExtension) {
			final String name = ftpFile.getName();
			if (name.toLowerCase().contains(taxonomy.toLowerCase())) {
				log.info("File for taxonomy '" + taxonomy + "' found in server. Now downloading.");
				final String remoteFilePath = uniprotFTPRemoteFolder + ftpFile.getName();
				final File mappinfFileCompressed = new File(mappingFolder.getAbsolutePath() + File.separator + name);
				if (!mappinfFileCompressed.getParentFile().exists()) {
					mappinfFileCompressed.getParentFile().mkdirs();
				}
				mappinfFileCompressed.createNewFile();
				final OutputStream outputStream = new FileOutputStream(mappinfFileCompressed, false);
				FTPUtils.downloadFile(uniprotHostName, "anonymous", "", remoteFilePath, outputStream);
				outputStream.close();
				log.info("File of " + FileUtils.getDescriptiveSizeFromBytes(mappinfFileCompressed.length())
						+ " downloaded to  " + mappinfFileCompressed.getAbsolutePath());
				log.info("Decompressing file...");
				final File file = ZipManager.decompressGZipFile(mappinfFileCompressed);
				log.info("File decompressed to: " + file.getAbsolutePath() + " - "
						+ FileUtils.getDescriptiveSizeFromBytes(file.length()));
				if (file != null && file.exists()) {
					log.info("Deleting file " + mappinfFileCompressed.getAbsolutePath());
					final boolean delete = mappinfFileCompressed.delete();
					if (delete) {
						log.info("File deleted");
					} else {
						log.warn("File cannot be deleted!");
					}
					return file;
				}

			}
		}
		return null;
	}

	private void importFromFile(File file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("Mapping file for taxonomy " + taxonomy + " not available");
		}
		log.info("Loading mapping file: " + file.getAbsolutePath());
		// read the species list
		final FileReader r = new FileReader(file);
		try {
			final BufferedReader br = new BufferedReader(r);
			try {

				String line;

				while ((line = br.readLine()) != null) {

					final String[] splittedLine = line.split(SEPARATOR);

					if (splittedLine.length == 3 && !splittedLine[0].equals("") && !splittedLine[1].equals("")) {

						final String accession = splittedLine[0];
						final String type = splittedLine[1];
						String geneName = null;
						if (type.equals(GENE_NAME)) {
							geneName = splittedLine[2];
							if (geneName != null) {
								final Gene gene = new Gene(geneName, type);
								if (!geneNameToAccession.containsKey(gene)) {
									geneNameToAccession.put(gene, new THashSet<String>());
								}
								geneNameToAccession.get(gene).add(accession);
							}
						}

						if (type.equals(GENE_SYNONM)) {
							geneName = splittedLine[2];
							if (geneName != null) {
								final Gene gene = new Gene(geneName, type);
								if (!geneNameToAccession.containsKey(gene)) {
									geneNameToAccession.put(gene, new THashSet<String>());
								}
								geneNameToAccession.get(gene).add(accession);
							}
						}

						if (type.equals(ENSEMBL)) {
							geneName = splittedLine[2];
							if (geneName != null) {
								final Gene gene = new Gene(geneName, type);
								if (!geneNameToAccession.containsKey(gene)) {
									geneNameToAccession.put(gene, new THashSet<String>());
								}
								geneNameToAccession.get(gene).add(accession);
							}
						}
					}

				}
			} finally {
				br.close();
				loaded = true;
				log.info(geneNameToAccession.size() + " genes mapped to uniprot accessions");
				getReverseMapping();
				log.info(accessionToGeneNames.size() + " uniprot accessions mapped to genes");
			}

		} finally {
			r.close();
		}
	}

	private void getReverseMapping() {
		for (final Gene gene : geneNameToAccession.keySet()) {
			final Set<String> accs = geneNameToAccession.get(gene);
			for (final String acc : accs) {
				if (!accessionToGeneNames.containsKey(acc)) {
					accessionToGeneNames.put(acc, new THashSet<Gene>());
				}
				accessionToGeneNames.get(acc).add(gene);
			}
		}

	}
}
