package edu.scripps.yates.utilities.files;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FilenameUtils;

import edu.scripps.yates.utilities.progresscounter.ProgressCounter;
import edu.scripps.yates.utilities.progresscounter.ProgressPrintingType;

/**
 * Use this as<br>
 * <code>
 * 		final ZipUtils appZip = new ZipUtils();<br>
		appZip.generateFileList(new File(SOURCE_FOLDER));<br>
		appZip.zipIt(OUTPUT_ZIP_FILE);
 * </code>
 * 
 * @author salvador
 *
 */
public class TarZipUtils {
	private final List<String> fileList;
	private final File OUTPUT_ZIP_FILE;
	private final File SOURCE_FOLDER; // SourceFolder path
	private final boolean compress;

	/**
	 * 
	 * @param folderToCompress
	 * @param outputFile
	 * @param compress         if true, zip compression will be applied and a tar.gz
	 *                         file will be created. If false, a .tar file will be
	 *                         created with no compression.
	 */
	public TarZipUtils(File folderToCompress, File outputFile, boolean compress) {
		fileList = new ArrayList<String>();
		OUTPUT_ZIP_FILE = outputFile;
		SOURCE_FOLDER = folderToCompress;
		this.compress = compress;
		generateFileList(folderToCompress);
	}

	public void zipIt() throws IOException {
		final String source = SOURCE_FOLDER.getName();
		OutputStream os = null;
		BufferedOutputStream buffOut = null;
		GzipCompressorOutputStream gzipzos = null;
		TarArchiveOutputStream tarOut = null;
		try {
			os = Files.newOutputStream(OUTPUT_ZIP_FILE.toPath());
			buffOut = new BufferedOutputStream(os);
			if (compress) {
				gzipzos = new GzipCompressorOutputStream(os);
				tarOut = new TarArchiveOutputStream(gzipzos);
			} else {
				tarOut = new TarArchiveOutputStream(buffOut);
			}

			System.out.println("Output to Zip : " + OUTPUT_ZIP_FILE.getAbsolutePath());
			FileInputStream in = null;
			int numFile = 1;
			for (final String file : fileList) {
				System.out.println("File Added : " + file);

//				final ZipEntry ze = new ZipEntry(source + File.separator + file);
//				zos.putNextEntry(ze);
				try {
					final File inputFile = new File(SOURCE_FOLDER.getAbsolutePath() + File.separator + file);

					final TarArchiveEntry tarEntry = new TarArchiveEntry(inputFile, source + File.separator + file);
					tarOut.putArchiveEntry(tarEntry);

					in = new FileInputStream(inputFile);
					copy(inputFile.length(), FilenameUtils.getName(inputFile.getAbsolutePath()) + " (" + numFile + "/"
							+ fileList.size() + ")", in, tarOut);

				} finally {
					in.close();
					tarOut.closeArchiveEntry();
					numFile++;
				}
			}

//			zos.closeEntry();
			System.out.println("Folder successfully processed");

		} finally {
			try {
//				zos.close();
				tarOut.finish();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	private long copy(long total, String suffix, FileInputStream input, OutputStream output) throws IOException {
		final int buffersize = 8024;
		final byte[] buffer = new byte[buffersize];
		int n = 0;
		long count = 0;
		final ProgressCounter c = new ProgressCounter(total, ProgressPrintingType.PERCENTAGE_STEPS, 0, true);
		c.setSuffix(suffix);
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			c.increment(n);
			final String printIfNecessary = c.printIfNecessary();
			if (!"".equals(printIfNecessary)) {
				System.out.println(printIfNecessary);
			}
			count += n;
		}
		return count;

	}

	private void generateFileList(File node) {
		// add file only
		if (node.isFile()) {
			fileList.add(generateZipEntry(node.getAbsolutePath()));
		}

		if (node.isDirectory()) {
			final String[] subNote = node.list();
			for (final String filename : subNote) {
				generateFileList(new File(node, filename));
			}
		}
	}

	private String generateZipEntry(String file) {
		final String ret = file.substring(SOURCE_FOLDER.getAbsolutePath().length() + 1, file.length());
		return ret;
	}
}
