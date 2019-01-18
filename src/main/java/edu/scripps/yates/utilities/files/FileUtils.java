package edu.scripps.yates.utilities.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.scripps.yates.utilities.dates.DatesUtil;

public class FileUtils {
	private final static Logger log = Logger.getLogger(FileUtils.class);
	private static DecimalFormat df;

	public static void mergeFiles(Collection<File> files, File mergedFile, boolean skipHeaderOfNotFirstFiles) {
		final File[] fileArray = new File[files.size()];
		int i = 0;
		for (final File file : files) {
			fileArray[i] = file;
			i++;
		}
		mergeFiles(fileArray, mergedFile, skipHeaderOfNotFirstFiles);
	}

	private static DecimalFormat getDecimalFormat() {
		if (df == null) {
			df = new DecimalFormat("#.#");
		}
		return df;
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
			} catch (final IOException e1) {
				e1.printStackTrace();
			}

			boolean firstFile = true;
			for (final File f : files) {
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

				} catch (final IOException e) {
					e.printStackTrace();
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (final IOException e) {
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
			} catch (final IOException e) {
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
	 * By default File#delete fails for non-empty directories, it works like "rm".
	 * We need something a little more brutual - this does the equivalent of "rm -r"
	 *
	 * @param path Root File Path
	 * @return true iff the file and all sub files/directories have been removed
	 * @throws FileNotFoundException
	 */
	public static boolean deleteFolderRecursive(File path) throws FileNotFoundException {
		if (!path.exists())
			throw new FileNotFoundException(path.getAbsolutePath());
		boolean ret = true;
		if (path.isDirectory()) {
			for (final File f : path.listFiles()) {
				ret = ret && FileUtils.deleteFolderRecursive(f);
			}
		}
		return ret && path.delete();
	}

	public static String getDescriptiveSizeFromBytes(long sizeInBytes) {
		log.debug("Getting file descriptive string from a number of bytes: " + sizeInBytes);
		if (sizeInBytes < 1024) {
			return getDecimalFormat().format(sizeInBytes) + " bytes";
		}
		final double sizeInKBytes = sizeInBytes / 1024;
		if (sizeInKBytes < 1024) {
			return getDecimalFormat().format(sizeInKBytes) + " Kb";
		}
		final double sizeInMBytes = sizeInKBytes / 1024;
		if (sizeInMBytes < 1024) {
			return getDecimalFormat().format(sizeInMBytes) + " Mb";
		}
		final double sizeInGBytes = sizeInMBytes / 1024;
		if (sizeInGBytes < 1024) {
			return getDecimalFormat().format(sizeInGBytes) + " Gb";
		}
		final double sizeInTBytes = sizeInGBytes / 1024;
		if (sizeInTBytes < 1024) {
			return getDecimalFormat().format(sizeInTBytes) + " Tb";
		}
		final double sizeInPBytes = sizeInTBytes / 1024;
		// if (sizeInPBytes < 1024) {
		return getDecimalFormat().format(sizeInPBytes) + " Pb";
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
			final String prefix2 = prefix != null ? prefix : "";
			final String suffix2 = suffix != null ? suffix : "";
			return new File(file.getParentFile().getAbsolutePath() + File.separator + prefix2 + baseName + suffix2 + "."
					+ extension);
		}
		return null;
	}

	public static List<String> readColumnFromTextFile(File inputFile, final String separator, int columnIndex,
			String startAfterLineBegginingBy) throws IOException {
		int index = 0;

		Stream<String> stream = null;
		try {
			stream = Files.lines(Paths.get(inputFile.getAbsolutePath()), Charset.defaultCharset());
			final Iterator<String> iterator = stream.iterator();
			while (iterator.hasNext()) {
				index++;
				final String line = iterator.next();
				if (line.startsWith(startAfterLineBegginingBy)) {
					break;
				}
			}
		} finally {
			if (stream != null) {
				stream.close();
			}
		}

		return readColumnFromTextFile(inputFile, separator, columnIndex, index);
	}

	public static List<String> readColumnFromTextFile(File inputFile, final String separator, int columnIndex,
			int skipFirstLines) throws IOException {
		Stream<String> lines = null;
		try {
			lines = Files.lines(Paths.get(inputFile.getAbsolutePath()), Charset.defaultCharset());

			lines = lines.skip(skipFirstLines);

			final List<String> collect = lines.map(line -> line.split(separator)[columnIndex])
					.collect(Collectors.toList());
			return collect;
		} catch (final IndexOutOfBoundsException e) {
			log.error(e);
			return null;
		} finally {
			if (lines != null) {
				lines.close();
			}
		}
	}

	public static List<String> readColumnFromTextFile(File inputFile, final String separator, int columnIndex,
			boolean skipHeader) throws IOException {
		Stream<String> lines = null;
		try {
			lines = Files.lines(Paths.get(inputFile.getAbsolutePath()), Charset.defaultCharset());
			if (skipHeader) {
				lines = lines.skip(1);
			}
			final List<String> collect = lines.map(line -> line.split(separator)[columnIndex])
					.collect(Collectors.toList());
			return collect;
		} catch (final IndexOutOfBoundsException e) {
			log.error(e);
			return null;
		} finally {
			if (lines != null) {
				lines.close();
			}
		}
	}

	public static File getFileFromInputStream(InputStream is) throws IOException {
		final File tempFile = File.createTempFile("temp", "tmp");
		return getFileFromInputStream(is, tempFile);
	}

	public static File getFileFromInputStream(InputStream is, File targetFile) throws IOException {

		Files.copy(is, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		IOUtils.closeQuietly(is);
		return targetFile;
	}

	public static List<String> readFirstLines(InputStream inputStream, long maxLines) {
		final long t1 = System.currentTimeMillis();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			final List<String> ret = new ArrayList<String>();
			while ((line = reader.readLine()) != null) {
				ret.add(line);
				if (ret.size() == maxLines) {
					break;
				}
			}
			return ret;
		} catch (final IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
			log.info("File readed in " + DatesUtil.getDescriptiveTimeFromMillisecs((System.currentTimeMillis() - t1)));
		}
	}

	/**
	 * Read the first (maxLines) lines from the file.<br>
	 * It supports GZIP and ZIP files
	 * 
	 * @param file
	 * @param maxLines
	 * @return
	 */
	public static List<String> readFirstLines(File file, long maxLines) {

		InputStream inputStream;
		try {
			inputStream = getInputStream(file);
			if (inputStream != null) {
				return readFirstLines(inputStream, maxLines);
			}
		} catch (final FileNotFoundException e) {

		}

		return Collections.emptyList();
	}

	/**
	 * Gets the appropriate {@link InputStream} from a {@link File}, taking into
	 * account GZIP or ZIP files
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static InputStream getInputStream(File file) throws FileNotFoundException {
		try {
			return new GZIPInputStream(new FileInputStream(file));
		} catch (final IOException e) {
			ZipFile zip = null;
			try {
				zip = new ZipFile(file);
				final Enumeration<? extends ZipEntry> entries = zip.entries();
				while (entries.hasMoreElements()) {
					final ZipEntry zipEntry = entries.nextElement();
					if (!zipEntry.isDirectory()) {
						return zip.getInputStream(zipEntry);
					}
				}
			} catch (final IOException e2) {
			} finally {
				// do not close zip, because that would close the stream
			}
			return new FileInputStream(file);

		}
	}

	/**
	 * Convert a TSV or CSV to Excel XLSX format
	 * 
	 * @param csvFilePath
	 * @param outputXlsFilePath
	 * @param separator
	 * @param sheetName         if null, a new sheet will be created with standard
	 *                          name "sheet1", "sheet2"...
	 * @throws IOException
	 */
	public static XSSFWorkbook separatedValuesToXLSX(String csvFilePath, String outputXlsFilePath, String separator,
			String sheetName) throws IOException {
		log.info("Converting file '" + csvFilePath + "' to Excel file at '" + outputXlsFilePath + "'");
		final boolean createNewExcelFile = !(new File(outputXlsFilePath).exists());
		if (createNewExcelFile) {
			log.info("File " + outputXlsFilePath + " already exists. The new sheet will be added");
		}
		XSSFWorkbook workBook = null;

		// if the excel file already exist, then create the XSSFWorkbook
		// with the constructor using the path,
		if (!createNewExcelFile) {
			workBook = new XSSFWorkbook(new FileInputStream(outputXlsFilePath));
		} else {
			workBook = new XSSFWorkbook();
		}

		BufferedReader br = null;
		FileOutputStream fileOutputStream = null;
		try {
			XSSFSheet sheet = null;
			if (sheetName == null) {
				// figure out what is the next sheet name available
				for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
					sheetName = "sheet" + (i + 1);
					if (workBook.getSheet(sheetName) == null) {
						break;
					}
				}
			}
			sheet = workBook.createSheet(sheetName);

			String currentLine = null;
			int RowNum = -1;
			br = new BufferedReader(new FileReader(csvFilePath));
			while ((currentLine = br.readLine()) != null) {
				final String str[] = currentLine.split(separator);
				RowNum++;
				final XSSFRow currentRow = sheet.createRow(RowNum);
				for (int i = 0; i < str.length; i++) {
					currentRow.createCell(i).setCellValue(str[i]);
				}
			}

			fileOutputStream = new FileOutputStream(outputXlsFilePath);
			workBook.write(fileOutputStream);
			fileOutputStream.close();
			log.info("Excel file written successfully at: " + outputXlsFilePath);

		} catch (final IOException ex) {
			log.error(ex);
			throw ex;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}

		}
		return workBook;
	}

	public static void separatedValuesToXLSX(String csvFilePath, String outputXlsFilePath, String separator)
			throws IOException {
		separatedValuesToXLSX(csvFilePath, outputXlsFilePath, separator, "sheet1");
	}
}
