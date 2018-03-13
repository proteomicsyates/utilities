package edu.scripps.yates.utilities.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

public class ZipManager {
	private static Logger log = Logger.getLogger("log4j.logger.org.proteored");
	private static final int BUFFER_SIZE = 8192;

	/**
	 * Compress a file in a gzip file
	 * 
	 * @throws IOException
	 */

	public static File compressGZipFile(File inputFile) throws IOException {

		return ZipManager.compressGZipFile(inputFile, null);

	}

	public static File compressZipFile(File inputFile) throws IOException {

		return ZipManager.compressZipFile(inputFile, null);

	}

	public static File compressGZipFile(File inputFile, File outputFile) throws IOException {
		// if it is already compressed, return the input file
		if (FilenameUtils.getExtension(inputFile.getName()).equalsIgnoreCase("gz"))
			return inputFile;

		if (outputFile == null)
			outputFile = new File(inputFile.getAbsolutePath() + ".gz");

		// Create the GZIP file
		BufferedOutputStream bufferedOut = new BufferedOutputStream(
				new GZIPOutputStream(new FileOutputStream(outputFile)));
		BufferedInputStream bufferedIn = new BufferedInputStream(new FileInputStream(inputFile));

		copyInputStream(bufferedIn, bufferedOut);

		return outputFile;

	}

	public static File compressZipFile(File inputFile, File outputFile) throws IOException {
		// if it is already compressed, return the input file
		if (FilenameUtils.getExtension(inputFile.getName()).equalsIgnoreCase("gz"))
			return inputFile;

		if (outputFile == null)
			outputFile = new File(inputFile.getAbsolutePath() + ".gz");

		// Create the GZIP file
		BufferedOutputStream bufferedOut = new BufferedOutputStream(
				new ZipOutputStream(new FileOutputStream(outputFile)));
		BufferedInputStream bufferedIn = new BufferedInputStream(new FileInputStream(inputFile));

		copyInputStream(bufferedIn, bufferedOut);

		return outputFile;

	}

	/**
	 * Decompress the FIRST entry of a zip file in the same folder as the zip,
	 * or in a temp file if there is some problem writting in that folder
	 * 
	 * @param file
	 * @return the first entry of the zipped file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */

	public static File decompressGZipFile(File file) throws FileNotFoundException, IOException {
		log.debug("processing GZip file: " + file.getAbsolutePath());

		FileInputStream fin = new FileInputStream(file);
		BufferedInputStream bufferedIn = new BufferedInputStream(new GZIPInputStream(fin));

		String outputName = FilenameUtils.getFullPath(file.getAbsolutePath())
				+ FilenameUtils.getBaseName(file.getAbsolutePath());
		if (outputName.equals(file.getAbsolutePath()))
			outputName = outputName + "_";
		BufferedOutputStream bufferedOut = new BufferedOutputStream(new FileOutputStream(outputName));
		log.info("Decompressing to : " + outputName);

		copyInputStream(bufferedIn, bufferedOut);

		return new File(outputName);

	}

	public static File decompressZipFile(File file) throws FileNotFoundException, IOException {
		log.debug("processing Zip file: " + file.getAbsolutePath());
		ZipFile zip = null;
		try {
			zip = new ZipFile(file);
			for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements();) {
				ZipEntry entry = e.nextElement();
				if (!entry.isDirectory()) {
					InputStream inputStream = zip.getInputStream(entry);
					BufferedInputStream bufferedIn = new BufferedInputStream(inputStream);

					String outputName = FilenameUtils.getFullPath(file.getAbsolutePath())
							+ FilenameUtils.getBaseName(file.getAbsolutePath());
					if (outputName.equals(file.getAbsolutePath()))
						outputName = outputName + "_";
					BufferedOutputStream bufferedOut = new BufferedOutputStream(new FileOutputStream(outputName));
					log.debug("Decompressing to : " + outputName);

					copyInputStream(bufferedIn, bufferedOut);
					return new File(outputName);
				}
			}
			throw new IOException("Error decompressing zip file");
		} finally {
			if (zip != null) {
				zip.close();
			}
		}
	}

	public static final void copyInputStream(BufferedInputStream bufferedIn, BufferedOutputStream bufferedOut)
			throws IOException {
		int totalAmountRead = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		try {

			while (true) {
				int amountRead = bufferedIn.read(buffer);
				if (amountRead == -1) {
					break;
				}
				totalAmountRead += amountRead;
				bufferedOut.write(buffer, 0, amountRead);
			}
		} finally {
			// CLose streams
			if (bufferedIn != null)
				bufferedIn.close();
			if (bufferedOut != null)
				bufferedOut.close();
			if (totalAmountRead == 0) {
				throw new IOException("Error copying files between streams");
			}

		}
	}

	public static File decompressGZipFileIfNeccessary(File file) {
		log.info("Decompressing file " + file.getAbsolutePath());

		try {
			return ZipManager.decompressGZipFile(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.info("The file " + file.getAbsolutePath() + " has not found");
		} catch (IOException e) {
			log.info("The file " + file.getAbsolutePath() + " is not a zipped file. Returning the original file");
		}

		return file;
	}

	public static File decompressFileIfNeccessary(File file) {
		log.debug("Trying to decompressing file " + file.getAbsolutePath());
		String originalPath = file.getAbsolutePath();
		try {

			try {
				return ZipManager.decompressZipFile(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				log.warn("The file " + file.getAbsolutePath() + " has not found");
				return file;
			} catch (IOException e) {
				log.debug("The file " + file.getAbsolutePath() + " is not a zipped file.");
			}

			try {
				return ZipManager.decompressGZipFile(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				log.warn("The file " + file.getAbsolutePath() + " has not found");
				return file;
			} catch (IOException e) {
				log.debug("The  file " + file.getAbsolutePath() + " is not a zipped file.");
			}
			return file;
		} finally {
			if (!originalPath.equals(file.getAbsolutePath())) {
				log.info("File " + originalPath + " has been decompressed to " + file.getAbsolutePath());
			}
		}
	}

	public static File decompressZipFileIfNeccessary(File file) {
		log.info("Decompressing file " + file.getAbsolutePath());

		try {
			return ZipManager.decompressZipFile(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.info("The file " + file.getAbsolutePath() + " has not found");
		} catch (IOException e) {
			log.info("The file " + file.getAbsolutePath() + " is not a zipped file. Returning the original file");
		}

		return file;
	}

	/**
	 * 
	 * @param file
	 * @param deleteOnExit
	 *            indicates if the decompressed file (if the source is is a gzip
	 *            file) will be delete on exit or not
	 * @return
	 */
	public static File decompressGZipFileIfNeccessary(File file, boolean deleteOnExit) {
		log.info("Decompressing file " + file.getAbsolutePath());

		try {
			File decompressGZipFile = ZipManager.decompressGZipFile(file);
			if (decompressGZipFile != null && deleteOnExit) {
				if (decompressGZipFile.getAbsolutePath() != file.getAbsolutePath()) {
					log.info("Setting 'deleteOnExit' to file: '" + decompressGZipFile.getAbsolutePath() + "'");
					decompressGZipFile.deleteOnExit();
					file.deleteOnExit();
				}
			}
			return decompressGZipFile;
		} catch (Exception e) {
			log.info("The file " + file.getAbsolutePath() + " is not a zipped file. Returning the original file");
			log.warn(e.getMessage());
		}

		return file;
	}

	/**
	 * 
	 * @param file
	 * @param deleteOnExit
	 *            indicates if the decompressed file (if the source is is a gzip
	 *            file) will be delete on exit or not
	 * @return
	 */
	public static File decompressZipFileIfNeccessary(File file, boolean deleteOnExit) {
		log.info("Decompressing file " + file.getAbsolutePath());

		try {
			File decompressZipFile = ZipManager.decompressZipFile(file);
			if (decompressZipFile != null && deleteOnExit) {
				if (decompressZipFile.getAbsolutePath() != file.getAbsolutePath()) {
					log.info("Setting 'deleteOnExit' to file: '" + decompressZipFile.getAbsolutePath() + "'");
					decompressZipFile.deleteOnExit();
					file.deleteOnExit();
				}
			}
			return decompressZipFile;
		} catch (Exception e) {
			log.info("The file " + file.getAbsolutePath() + " is not a zipped file. Returning the original file");
			log.warn(e.getMessage());
		}

		return file;
	}

	/**
	 * 
	 * @param file
	 * @param deleteOnExit
	 *            indicates if the decompressed file (if the source is is a gzip
	 *            file) will be delete on exit or not
	 * @return
	 */
	public static File decompressFileIfNeccessary(File file, boolean deleteOnExit) {
		log.info("Decompressing file " + file.getAbsolutePath());

		try {
			File decompressZipFile = ZipManager.decompressZipFile(file);
			if (decompressZipFile != null && deleteOnExit) {
				if (decompressZipFile.getAbsolutePath() != file.getAbsolutePath()) {
					log.info("Setting 'deleteOnExit' to file: '" + decompressZipFile.getAbsolutePath() + "'");
					decompressZipFile.deleteOnExit();
					file.deleteOnExit();
				}
			}
			return decompressZipFile;
		} catch (Exception e) {
			log.info("The file " + file.getAbsolutePath() + " is not a zipped file. Returning the original file");
			log.warn(e.getMessage());
		}
		try {
			File decompressGZipFile = ZipManager.decompressGZipFile(file);
			if (decompressGZipFile != null && deleteOnExit) {
				if (decompressGZipFile.getAbsolutePath() != file.getAbsolutePath()) {
					log.info("Setting 'deleteOnExit' to file: '" + decompressGZipFile.getAbsolutePath() + "'");
					decompressGZipFile.deleteOnExit();
					file.deleteOnExit();
				}
			}
			return decompressGZipFile;
		} catch (Exception e) {
			log.info("The file " + file.getAbsolutePath() + " is not a zipped file. Returning the original file");
			log.warn(e.getMessage());
		}
		return file;
	}

	public static String getExtension(String name) {
		if (name == null || "".equals(name)) {
			return null;
		}
		return FilenameUtils.getExtension(name);
	}

}
