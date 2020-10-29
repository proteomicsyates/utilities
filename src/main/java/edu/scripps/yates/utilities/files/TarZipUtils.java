package edu.scripps.yates.utilities.files;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.log4j.Logger;

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
	private final static Logger log = Logger.getLogger(TarZipUtils.class);
	private final OutputStream outputStream;
	private final boolean compress;
	private final File folderToCompress;
	private boolean logFileCopyProgress = false;

	/**
	 * 
	 * @param folderToCompress
	 * @param outputFile
	 * @param compress         if true, zip compression will be applied and a tar.gz
	 *                         file will be created. If false, a .tar file will be
	 *                         created with no compression.
	 * @throws IOException
	 */
	public TarZipUtils(File folderToCompress, File outputFile, boolean compress) throws IOException {
		outputStream = Files.newOutputStream(outputFile.toPath());
		this.compress = compress;
		this.folderToCompress = folderToCompress;
	}

	/**
	 * 
	 * @param folderToCompress
	 * @param outputStream
	 * @param compress         if true, zip compression will be applied and a tar.gz
	 *                         file will be created. If false, a .tar file will be
	 *                         created with no compression.
	 */
	public TarZipUtils(File folderToCompress, OutputStream outputStream, boolean compress) {
		this.folderToCompress = folderToCompress;
		this.outputStream = outputStream;
		this.compress = compress;
	}

	public long transfer() throws IOException {
		final TarArchiveOutputStream tarOut;
		final long sent = 0l;

		final BufferedOutputStream buffOut = new BufferedOutputStream(outputStream);
		if (compress) {
			final GzipCompressorOutputStream gzipzos = new GzipCompressorOutputStream(buffOut);
			tarOut = new TarArchiveOutputStream(gzipzos);
		} else {
			tarOut = new TarArchiveOutputStream(buffOut);
		}
		final Path source = folderToCompress.toPath();
		Files.walkFileTree(source, new FileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				Objects.requireNonNull(dir);
				Objects.requireNonNull(attrs);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Objects.requireNonNull(file);
				Objects.requireNonNull(attrs);
				if (attrs.isSymbolicLink()) {
					return FileVisitResult.CONTINUE;
				}
//				if (file.getFileName().toString().contains("analy")) {
//					return FileVisitResult.CONTINUE;
//				}
				final Path targetFile = source.relativize(file);
				try {
					final TarArchiveEntry tarEntry = new TarArchiveEntry(file.toFile(), targetFile.toString());

					tarOut.putArchiveEntry(tarEntry);
					copy(file, tarOut);

					tarOut.closeArchiveEntry();

					System.out.printf("file : %s%n", file);

				} catch (final IOException e) {
					System.err.printf("Unable to tar.gz : %s%n%s%n", file, e);
				}

				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				System.err.printf("Unable to tar.gz : %s%n%s%n", file, exc);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Objects.requireNonNull(dir);
				if (exc != null)
					throw exc;
				return FileVisitResult.CONTINUE;
			}
		});

		tarOut.finish();
		tarOut.close();
		System.out.println("Folder successfully processed");
		return sent;

	}

	protected long copy(Path file, TarArchiveOutputStream tarOut) throws IOException {
		final long total = file.toFile().length();
		final ProgressCounter counter = new ProgressCounter(total, ProgressPrintingType.PERCENTAGE_STEPS, 0, true);
		counter.setSuffix(file.toString());
//		Files.copy(file, tarOut);

		final InputStream in = file.getFileSystem().provider().newInputStream(file);
		final byte[] buffer = new byte[4 * 1024];
		int read = 0;
		long totalRead = 0l;
		while ((read = in.read(buffer)) > 0) {
			counter.increment(read);
			final String printIfNecessary = counter.printIfNecessary();
			if (logFileCopyProgress && !"".equals(printIfNecessary)) {
				log.info(printIfNecessary);
			}
			tarOut.write(buffer, 0, read);
			totalRead += read;
		}
		return totalRead;
	}

	public void setLogFileCopyProgress(boolean log) {
		logFileCopyProgress = log;
	}
}
