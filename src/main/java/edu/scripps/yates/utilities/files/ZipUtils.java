package edu.scripps.yates.utilities.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
public class ZipUtils {
	private final List<String> fileList;
	private static final String OUTPUT_ZIP_FILE = "Folder.zip";
	private static final String SOURCE_FOLDER = "D:\\Reports"; // SourceFolder path

	public ZipUtils() {
		fileList = new ArrayList<String>();
	}

	public static void main(String[] args) {
		final ZipUtils appZip = new ZipUtils();
		appZip.generateFileList(new File(SOURCE_FOLDER));
		appZip.zipIt(OUTPUT_ZIP_FILE);
	}

	public void zipIt(String zipFile) {
		final byte[] buffer = new byte[1024];
		final String source = new File(SOURCE_FOLDER).getName();
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(zipFile);
			zos = new ZipOutputStream(fos);

			System.out.println("Output to Zip : " + zipFile);
			FileInputStream in = null;

			for (final String file : this.fileList) {
				System.out.println("File Added : " + file);
				final ZipEntry ze = new ZipEntry(source + File.separator + file);
				zos.putNextEntry(ze);
				try {
					in = new FileInputStream(SOURCE_FOLDER + File.separator + file);
					int len;
					while ((len = in.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}
				} finally {
					in.close();
				}
			}

			zos.closeEntry();
			System.out.println("Folder successfully compressed");

		} catch (final IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				zos.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void generateFileList(File node) {
		// add file only
		if (node.isFile()) {
			fileList.add(generateZipEntry(node.toString()));
		}

		if (node.isDirectory()) {
			final String[] subNote = node.list();
			for (final String filename : subNote) {
				generateFileList(new File(node, filename));
			}
		}
	}

	private String generateZipEntry(String file) {
		return file.substring(SOURCE_FOLDER.length() + 1, file.length());
	}
}
