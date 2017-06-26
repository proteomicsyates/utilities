package edu.scripps.yates.utilities.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class FileUtils {
	private final static Logger log = Logger.getLogger(FileUtils.class);
	private static final DecimalFormat df = new DecimalFormat("#.#");

	public static void mergeFiles(Collection<File> files, File mergedFile, boolean skipHeaderOfNotFirstFiles) {
		File[] fileArray = new File[files.size()];
		int i = 0;
		for (File file : files) {
			fileArray[i] = file;
			i++;
		}
		mergeFiles(fileArray, mergedFile, skipHeaderOfNotFirstFiles);
	}

	public static void mergeFiles(File[] files, File mergedFile, boolean skipHeaderOfNotFirstFiles) {
		// if the file already exists, delete it
		if (mergedFile.exists()) {
			mergedFile.delete();
		}
		FileWriter fstream = null;
		BufferedWriter out = null;
		try {
			try {
				fstream = new FileWriter(mergedFile, true);
				out = new BufferedWriter(fstream);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			boolean firstFile = true;
			for (File f : files) {
				log.debug("merging: " + f.getName());
				FileInputStream fis;
				BufferedReader in = null;
				try {
					fis = new FileInputStream(f);
					in = new BufferedReader(new InputStreamReader(fis));

					String aLine;
					boolean firstLine = true;
					while ((aLine = in.readLine()) != null) {
						if (skipHeaderOfNotFirstFiles && (!firstFile && firstLine)) {
							firstLine = false;
							continue;
						}

						out.write(aLine);
						out.newLine();

					}
					firstLine = false;

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
							log.error(e);
						}
					}

				}
				firstFile = false;
			}
		} finally {
			try {
				if (out != null) {
					out.close();
					log.debug("File merged at: " + mergedFile.getAbsolutePath());
				}
			} catch (IOException e) {
				e.printStackTrace();
				log.error(e);
			}
		}

	}

	/**
	 * Delete a folder if it is empty. if recursivelly is true, also delete the
	 * parent folders if they are empty.
	 *
	 * @param folder
	 * @param recursivelly
	 */
	public static void removeFolderIfEmtpy(File folder, boolean recursivelly) {
		if (folder == null || !folder.isDirectory())
			return;
		if (folder.exists() && folder.list().length == 0) {
			folder.delete();

			if (recursivelly) {
				removeFolderIfEmtpy(folder.getParentFile(), recursivelly);
			}
		}
	}

	/**
	 * By default File#delete fails for non-empty directories, it works like
	 * "rm". We need something a little more brutual - this does the equivalent
	 * of "rm -r"
	 *
	 * @param path
	 *            Root File Path
	 * @return true iff the file and all sub files/directories have been removed
	 * @throws FileNotFoundException
	 */
	public static boolean deleteFolderRecursive(File path) throws FileNotFoundException {
		if (!path.exists())
			throw new FileNotFoundException(path.getAbsolutePath());
		boolean ret = true;
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				ret = ret && FileUtils.deleteFolderRecursive(f);
			}
		}
		return ret && path.delete();
	}

	public static String getDescriptiveSizeFromBytes(long sizeInBytes) {
		if (sizeInBytes < 1024) {
			return df.format(sizeInBytes) + " bytes";
		}
		double sizeInKBytes = sizeInBytes / 1024;
		if (sizeInKBytes < 1024) {
			return df.format(sizeInKBytes) + " Kb";
		}
		double sizeInMBytes = sizeInKBytes / 1024;
		if (sizeInMBytes < 1024) {
			return df.format(sizeInMBytes) + " Mb";
		}
		double sizeInGBytes = sizeInMBytes / 1024;
		if (sizeInGBytes < 1024) {
			return df.format(sizeInGBytes) + " Gb";
		}
		double sizeInTBytes = sizeInGBytes / 1024;
		if (sizeInTBytes < 1024) {
			return df.format(sizeInTBytes) + " Tb";
		}
		double sizeInPBytes = sizeInTBytes / 1024;
		// if (sizeInPBytes < 1024) {
		return df.format(sizeInPBytes) + " Pb";
		// }
	}

	/**
	 * returns a {@link File} object where having an input file like
	 * /path/myfile.extension, it returns /path/prefixmyfilesuffix.extension
	 *
	 * @param file
	 * @param prefix
	 * @param sufix
	 * @return
	 */
	public static File appendToFileName(File file, String prefix, String suffix) {
		if (file != null) {
			final String absolutePath = file.getAbsolutePath();
			final String baseName = FilenameUtils.getBaseName(absolutePath);
			final String extension = FilenameUtils.getExtension(absolutePath);
			String prefix2 = prefix != null ? prefix : "";
			String suffix2 = suffix != null ? suffix : "";
			return new File(file.getParentFile().getAbsolutePath() + File.separator + prefix2 + baseName + suffix2 + "."
					+ extension);
		}
		return null;
	}

	public static List<String> readColumnFromTextFile(File inputFile, final String separator, int columnIndex,
			String startAfterLineBegginingBy) throws IOException {
		int index = 0;

		Iterator<String> iterator = Files.lines(Paths.get(inputFile.getAbsolutePath()), Charset.defaultCharset())
				.iterator();
		while (iterator.hasNext()) {
			index++;
			String line = iterator.next();
			if (line.startsWith(startAfterLineBegginingBy)) {
				break;
			}
		}

		return readColumnFromTextFile(inputFile, separator, columnIndex, index);
	}

	public static List<String> readColumnFromTextFile(File inputFile, final String separator, int columnIndex,
			int skipFirstLines) throws IOException {
		try {
			Stream<String> lines = Files.lines(Paths.get(inputFile.getAbsolutePath()), Charset.defaultCharset());

			lines = lines.skip(skipFirstLines);

			final List<String> collect = lines.map(line -> line.split(separator)[columnIndex])
					.collect(Collectors.toList());
			return collect;
		} catch (IndexOutOfBoundsException e) {
			log.error(e);
			return null;
		}
	}

	public static List<String> readColumnFromTextFile(File inputFile, final String separator, int columnIndex,
			boolean skipHeader) throws IOException {
		try {
			Stream<String> lines = Files.lines(Paths.get(inputFile.getAbsolutePath()), Charset.defaultCharset());
			if (skipHeader) {
				lines = lines.skip(1);
			}
			final List<String> collect = lines.map(line -> line.split(separator)[columnIndex])
					.filter(line -> skipHeader).collect(Collectors.toList());
			return collect;
		} catch (IndexOutOfBoundsException e) {
			log.error(e);
			return null;
		}
	}

	public static File getFileFromInputStream(InputStream is) throws IOException {
		File tempFile = File.createTempFile("temp", "tmp");
		return getFileFromInputStream(is, tempFile);
	}

	public static File getFileFromInputStream(InputStream is, File targetFile) throws IOException {

		Files.copy(is, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		IOUtils.closeQuietly(is);
		return targetFile;
	}
}
