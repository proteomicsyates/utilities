package edu.scripps.yates.utilities.parsers.idparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import edu.scripps.yates.utilities.annotations.UniprotProteinLocalRetrieverInterface;
import edu.scripps.yates.utilities.annotations.uniprot.xml.Entry;
import edu.scripps.yates.utilities.fasta.FastaParser;
import edu.scripps.yates.utilities.fasta.dbindex.DBIndexInterface;
import edu.scripps.yates.utilities.files.FileUtils;
import edu.scripps.yates.utilities.ipi.IPI2UniprotACCMap;
import edu.scripps.yates.utilities.model.enums.AccessionType;
import edu.scripps.yates.utilities.model.factories.AccessionEx;
import edu.scripps.yates.utilities.parsers.Parser;
import edu.scripps.yates.utilities.progresscounter.ProgressCounter;
import edu.scripps.yates.utilities.progresscounter.ProgressPrintingType;
import edu.scripps.yates.utilities.proteomicsmodel.Accession;
import edu.scripps.yates.utilities.remote.RemoteSSHFileReference;
import edu.scripps.yates.utilities.util.Pair;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

public abstract class IdentificationsParser<G, Q extends IdentifiedProteinInterface, S extends IdentifiedPSMInterface>
		implements Parser {
	private static final Logger log = Logger.getLogger(IdentificationsParser.class);
	protected final Map<String, Q> proteinsByAccession = new THashMap<String, Q>();
	protected final Map<String, S> psmTableByPSMID = new THashMap<String, S>();
	protected final Map<String, Set<S>> psmTableByFullSequence = new THashMap<String, Set<S>>();
	protected final Set<G> proteinGroups = new THashSet<G>();
	protected String runPath;
	protected DBIndexInterface dbIndex;
	protected boolean processed = false;
	protected final Map<String, InputStream> fs;
	protected Pattern decoyPattern;
	protected final List<String> commandLineParameterStrings = new ArrayList<String>();
	protected final Set<String> searchEngines = new THashSet<String>();
	protected CommandLineParameters commandLineParameter;
	private String searchEngineVersion;
	protected String fastaPath;
	protected final Set<String> spectraFileNames = new THashSet<String>();
	protected String dtaSelectVersion;
	protected final Set<String> spectraFileFullPaths = new THashSet<String>();
	private UniprotProteinLocalRetrieverInterface uplr;
	private String uniprotVersion;
	protected boolean ignoreNotFoundPeptidesInDB;
	private boolean retrieveFastaIsoforms;
	private boolean ignoreTaxonomies;
	protected boolean ignoreACCFormat;
	protected boolean onlyReadParameters = false;
	protected boolean onlyReadProteins;

	public IdentificationsParser(URL u) throws IOException {
		this(u.getFile(), u.openStream());
	}

	public IdentificationsParser(String runid, RemoteSSHFileReference s) throws FileNotFoundException {
		this(runid, s.getRemoteInputStream());
	}

	public IdentificationsParser(Map<String, RemoteSSHFileReference> s) throws FileNotFoundException {
		fs = new THashMap<String, InputStream>();
		for (final String key : s.keySet()) {
			final RemoteSSHFileReference server = s.get(key);
			// final File remoteFile = server.getRemoteFile();
			fs.put(key, server.getRemoteInputStream());
		}

	}

	public IdentificationsParser(List<File> s) throws FileNotFoundException {
		fs = new THashMap<String, InputStream>();
		for (final File remoteFile : s) {
			fs.put(remoteFile.getAbsolutePath(), FileUtils.getInputStream(remoteFile));
		}

	}

	public IdentificationsParser(File file) throws FileNotFoundException {
		log.debug("Beggining of constructor with file " + file.getAbsolutePath());
		fs = new THashMap<String, InputStream>();
		fs.put(file.getAbsolutePath(), FileUtils.getInputStream(file));
		log.debug("end of constructor");
	}

	public IdentificationsParser(String runId, InputStream f) {
		fs = new THashMap<String, InputStream>();
		fs.put(runId, f);

	}

	private void process() throws IOException {
		process(false);
	}

	protected abstract void process(boolean checkFormat) throws IOException;

	protected abstract Q mergeProteins(Q destination, Q origin);

	protected void addToPSMTable(S psm) {
		final String psmKey = getPSMFullSequence(psm);
		if (psmTableByFullSequence.containsKey(psmKey)) {
			psmTableByFullSequence.get(psmKey).add(psm);
		} else {
			final Set<S> set = new THashSet<S>();
			set.add(psm);
			psmTableByFullSequence.put(psmKey, set);
		}

	}

	protected abstract String getPSMFullSequence(S psm);

	public Map<String, Q> getProteins() throws IOException {
		if (!processed)
			startProcess();

		return proteinsByAccession;
	}

	public Set<G> getProteinGroups() throws IOException {
		if (dbIndex != null)
			throw new IllegalArgumentException(
					"Reading proteins with a FASTA database, will not result in Protein groups");
		if (!processed)
			startProcess();
		return proteinGroups;
	}

	public Map<String, S> getPSMsByPSMID() throws IOException {
		if (!processed)
			startProcess();
		return psmTableByPSMID;
	}

	public Map<String, Set<S>> getPSMsByFullSequence() throws IOException {
		if (!processed)
			startProcess();
		return psmTableByFullSequence;
	}

	protected boolean isNumeric(String string) {
		return NumberUtils.isNumber(string);

	}

	/**
	 * @param dbIndex
	 *            the dbIndex to set
	 */
	public void setDbIndex(DBIndexInterface dbIndex) {
		this.dbIndex = dbIndex;
	}

	public void setDecoyPattern(String patternString) throws PatternSyntaxException {
		if (patternString != null) {
			decoyPattern = Pattern.compile(patternString);
		}
	}

	/**
	 * @return the runPath
	 * @throws IOException
	 */
	public String getRunPath() throws IOException {
		if (!processed)
			startProcess();
		return runPath;
	}

	/**
	 * @return the commandLineParameterStrings
	 * @throws IOException
	 */
	public List<String> getCommandLineParameterStrings() throws IOException {
		if (!processed)
			startProcess();
		return commandLineParameterStrings;
	}

	/**
	 * @return the searchEngines
	 * @throws IOException
	 */
	public Set<String> getSearchEngines() throws IOException {
		if (!processed)
			startProcess();
		return searchEngines;
	}

	/**
	 * @return the commandLineParameter
	 * @throws IOException
	 */
	public CommandLineParameters getCommandLineParameter() throws IOException {
		if (!processed)
			startProcess();
		return commandLineParameter;
	}

	/**
	 * @return the searchEngineVersion
	 * @throws IOException
	 */
	public String getSearchEngineVersion() throws IOException {
		if (!processed)
			startProcess();

		return searchEngineVersion;
	}

	/**
	 * @param searchEngineVersion
	 *            the searchEngineVersion to set
	 */
	public void setSearchEngineVersion(String searchEngineVersion) {
		this.searchEngineVersion = searchEngineVersion;
	}

	public String getFastaPath() throws IOException {
		if (!processed)
			startProcess();

		return fastaPath;
	}

	public Set<String> getSpectraFileNames() throws IOException {
		if (!processed)
			startProcess();

		return spectraFileNames;
	}

	public String getDecoyPattern() throws IOException {

		if (decoyPattern != null) {
			return decoyPattern.toString();
		}
		return null;
	}

	public String getSoftwareVersion() throws IOException {
		if (!processed)
			startProcess();

		return dtaSelectVersion;
	}

	public Set<String> getSpectraFileFullPaths() throws IOException {
		if (!processed)
			startProcess();
		return spectraFileFullPaths;
	}

	protected abstract String getProteinAcc(Q protein);

	private void mergeProteinsWithSecondaryAccessionsInParser() throws IOException {
		if (uplr == null) {
			return;
		}
		final Set<String> accessions = new THashSet<String>();
		final Map<String, Q> dtaSelectProteins = getProteins();
		for (final Q protein : dtaSelectProteins.values()) {
			final String accession = FastaParser.getACC(getProteinAcc(protein)).getFirstelement();
			accessions.add(accession);
			// just in case the accession has changed:
			dtaSelectProteins.put(accession, protein);
		}
		String latestVersion = "latestVersion";
		if (uniprotVersion != null) {
			latestVersion = "version " + uniprotVersion;
		}
		// split into chunks of 500 accessions in order to show progress
		final int chunckSize = 500;
		final List<Set<String>> listOfSets = new ArrayList<Set<String>>();
		Set<String> set = new THashSet<String>();
		for (final String accession : accessions) {
			set.add(accession);
			if (set.size() == chunckSize) {
				listOfSets.add(set);
				set = new THashSet<String>();
			}
		}
		listOfSets.add(set);

		int numObsoletes = 0;
		log.info("Merging proteins that have secondary accessions according to Uniprot " + latestVersion + "...");

		final int initialSize = accessions.size();
		final ProgressCounter counter = new ProgressCounter(initialSize, ProgressPrintingType.PERCENTAGE_STEPS, 0);
		for (final Set<String> accessionSet : listOfSets) {
			final Map<String, Entry> annotatedProteins = uplr.getAnnotatedProteins(uniprotVersion, accessionSet,
					retrieveFastaIsoforms);
			for (final String accession : accessionSet) {
				counter.increment();
				final String progress = counter.printIfNecessary();
				if (!"".contentEquals(progress)) {
					log.info(progress);
				}
				final Q protein = dtaSelectProteins.get(accession);
				final Entry entry = annotatedProteins.get(accession);
				if (entry != null && entry.getAccession() != null && !entry.getAccession().isEmpty()) {
					final String primaryAccession = entry.getAccession().get(0);
					if (!accession.equals(primaryAccession) && !accession.contains(primaryAccession)) {
						log.info("Replacing Uniprot accession " + accession + " by " + primaryAccession);
						setPrimaryAccession(primaryAccession, protein);

						if (dtaSelectProteins.containsKey(primaryAccession)) {
							// there was already a protein with that
							// primaryAccession
							final Q quantifiedProtein2 = dtaSelectProteins.get(primaryAccession);
							// merge quantifiedPRotein and quantifiedPRotein2
							mergeProteins(protein, quantifiedProtein2);

						} else {
							numObsoletes++;
						}
						// remove old/secondary accession
						dtaSelectProteins.remove(accession);
						dtaSelectProteins.put(primaryAccession, protein);

					}
				} else {
					// // remove the protein because is obsolete
					// log.info(quantifiedProtein.getAccession());
					// parser.getProteinMap().remove(accession);
				}
			}
		}
		final int finalSize = dtaSelectProteins.size();
		if (initialSize != finalSize) {
			log.info(finalSize - initialSize
					+ " proteins with secondary accessions were merged with the corresponding protein with primary accession");
		}
		if (numObsoletes > 0) {
			log.info("Obsolete accessions from " + numObsoletes + " proteins were changed to primary ones");
		}
	}

	protected abstract void setPrimaryAccession(String primaryAccession, Q protein);

	/**
	 * To be called after process().<br>
	 * If proteins have IPI accessions, look for the mapping from IPI 2 Uniprot.
	 * It adds new entries to the map, but it doesn't create any new
	 * {@link QuantifiedProteinInterface}
	 */
	private void mapIPI2Uniprot() {
		if (!proteinsByAccession.isEmpty()) {
			final int originalNumberOfEntries = proteinsByAccession.size();
			final Map<String, Q> newMap = new THashMap<String, Q>();
			for (final String accession : proteinsByAccession.keySet()) {

				final Pair<String, String> acc = FastaParser.getACC(accession);
				if (acc.getSecondElement().equals("IPI")) {
					final Q protein = proteinsByAccession.get(accession);
					Accession primaryAccession = new AccessionEx(accession, AccessionType.IPI);
					final Pair<Accession, Set<Accession>> pair = IPI2UniprotACCMap.getInstance()
							.getPrimaryAndSecondaryAccessionsFromIPI(primaryAccession);
					if (pair.getFirstelement() != null) {
						primaryAccession = pair.getFirstelement();
						if (!newMap.containsKey(primaryAccession)) {
							newMap.put(primaryAccession.getAccession(), protein);
						}
					}
					final Set<Accession> secondaryAccs = pair.getSecondElement();
					if (secondaryAccs != null) {
						for (final Accession secondaryAcc : secondaryAccs) {
							if (!newMap.containsKey(secondaryAcc.getAccession())) {
								newMap.put(secondaryAcc.getAccession(), protein);
							}
						}

					}
				}
			}
			for (final String acc : newMap.keySet()) {
				if (!proteinsByAccession.containsKey(acc)) {
					proteinsByAccession.put(acc, newMap.get(acc));
				}
			}
			if (originalNumberOfEntries != proteinsByAccession.size()) {
				log.info("Protein Map expanded from " + originalNumberOfEntries + " to " + proteinsByAccession.size());
			}
		}
	}

	private void startProcess() throws IOException {
		// first process
		process();
		// remove psms assigned to decoy proteins that were discarded
		removeDecoyPSMs();
		// second expand protein map
		mapIPI2Uniprot();
		// third merge proteins with secondary accessions
		mergeProteinsWithSecondaryAccessionsInParser();

		log.info(proteinsByAccession.size() + " proteins read in " + fs.size() + " DTASelect file(s).");
		log.info(psmTableByFullSequence.size() + " peptides read in " + fs.size() + " DTASelect file(s).");
		log.info(psmTableByPSMID.size() + " psms read in " + fs.size() + " DTASelect file(s).");

	}

	/**
	 * @return the ignoreNotFoundPeptidesInDB
	 */
	public boolean isIgnoreNotFoundPeptidesInDB() {
		return ignoreNotFoundPeptidesInDB;
	}

	/**
	 * @param ignoreNotFoundPeptidesInDB
	 *            the ignoreNotFoundPeptidesInDB to set
	 */
	public void setIgnoreNotFoundPeptidesInDB(boolean ignoreNotFoundPeptidesInDB) {
		this.ignoreNotFoundPeptidesInDB = ignoreNotFoundPeptidesInDB;
	}

	public void enableProteinMergingBySecondaryAccessions(UniprotProteinLocalRetrieverInterface uplr,
			String uniprotVersion) {
		this.uplr = uplr;
		this.uniprotVersion = uniprotVersion;
	}

	public Set<String> getInputFilePathes() {
		return fs.keySet();
	}

	public DBIndexInterface getDBIndex() {
		return dbIndex;
	}

	public Set<String> getUniprotAccSet() {
		final Set<String> ret = new THashSet<String>();
		try {
			final Set<String> keySet = getProteins().keySet();
			for (final String acc : keySet) {
				final String uniProtACC = FastaParser.getUniProtACC(acc);
				if (uniProtACC != null) {
					ret.add(uniProtACC);
				}
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return ret;
	}

	@Override
	public boolean canRead(File file) {
		try {
			fs.put("TMP***", FileUtils.getInputStream(file));
			if (!processed) {
				process(true);
			}
			if (!proteinsByAccession.isEmpty()) {
				if (!psmTableByPSMID.isEmpty()) {
					return true;
				}
			}
			return false;
		} catch (final Exception e) {
			return false;
		} finally {
			fs.remove("TMP***");
		}
	}

	public boolean isRetrieveFastaIsoforms() {
		return retrieveFastaIsoforms;
	}

	public void setRetrieveFastaIsoforms(boolean retrieveFastaIsoforms) {
		this.retrieveFastaIsoforms = retrieveFastaIsoforms;
	}

	public boolean isIgnoreTaxonomies() {
		return ignoreTaxonomies;
	}

	public void setIgnoreTaxonomies(boolean ignoreTaxonomies) {
		this.ignoreTaxonomies = ignoreTaxonomies;
	}

	public void setIgnoreACCFormat(boolean ignoreACCFormat) {
		this.ignoreACCFormat = ignoreACCFormat;
	}

	public boolean isIgnoreACCFormat() {
		return ignoreACCFormat;
	}

	public UniprotProteinLocalRetrieverInterface getUniprotProteinLocalRetriever() {
		return uplr;
	}

	public String getUniprotVersion() {
		return uniprotVersion;
	}

	public boolean isOnlyReadParameters() {
		return onlyReadParameters;
	}

	public void setOnlyReadParameters(boolean onlyReadParameters) {
		this.onlyReadParameters = onlyReadParameters;
	}

	public void setOnlyReadProteins(boolean onlyReadProteins) {
		this.onlyReadProteins = onlyReadProteins;
	}

	public boolean isOnlyReadProteins() {
		return onlyReadProteins;
	}

	/**
	 * Parse DTASelectProtein locus for getting the primary accession, parsing
	 * it accordingly
	 *
	 * @param protein
	 * @return
	 */
	public Accession getProteinAccessionFromProtein(Q protein) {
		if (protein == null)
			return null;
		return getProteinAccessionFromProtein(protein.getAccession(), protein.getDescription());
	}

	/**
	 * Parse DTASelectProtein locus for getting the primary accession, parsing
	 * it accordingly
	 *
	 * @param dtaSelectProtein
	 * @return
	 */
	private Accession getProteinAccessionFromProtein(String locus, String description) {

		if (locus == null || "".equals(locus))
			return null;
		AccessionEx primaryAccession = null;

		final Pair<String, String> acc = FastaParser.getACC(locus);
		AccessionType accType = AccessionType.fromValue(acc.getSecondElement());
		if (accType == null) {
			accType = AccessionType.IPI;
		}
		primaryAccession = new AccessionEx(acc.getFirstelement(), accType);

		primaryAccession.setDescription(description);
		return primaryAccession;
	}

	/**
	 * in case of decoyPattern is enabled, we may have some PSMs assigned to
	 * those decoy proteins that have not been saved, so we need to discard
	 * them.<br>
	 * We iterate over the psms, and we will remove the ones with no proteins
	 * 
	 */
	protected void removeDecoyPSMs() {
		if (decoyPattern != null) {
			// in case of decoyPattern is enabled, we may have some PSMs
			// assigned to
			// those decoy proteins that have not been saved,
			// so we need to discard them
			// We iterate over the psms, and we will remove the ones with no
			// proteins
			final Set<String> psmIdsToDelete = new THashSet<String>();
			for (final String psmID : psmTableByPSMID.keySet()) {
				if (psmTableByPSMID.get(psmID).getProteins().isEmpty()) {
					psmIdsToDelete.add(psmID);
				}
			}
			log.info("Removing " + psmIdsToDelete.size() + " PSMs assigned to decoy discarded proteins");
			for (final String psmID : psmIdsToDelete) {
				final S psm = psmTableByPSMID.get(psmID);
				if (!psm.getProteins().isEmpty()) {
					throw new IllegalArgumentException("This should not happen");
				}
				final Set<S> set = psmTableByFullSequence.get(psm.getFullSequence());
				final boolean removed = set.remove(psm);
				if (!removed) {
					throw new IllegalArgumentException("This should not happen");
				}
				if (set.isEmpty()) {
					// remove the entry by full sequence
					psmTableByFullSequence.remove(psm.getFullSequence());
				}
				// remove psmTableByPsmID
				psmTableByPSMID.remove(psmID);
			}

			log.info(psmIdsToDelete.size() + " PSMs discarded as decoy");
		}
	}
}
