package edu.scripps.yates.utilities.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

public class FTPUtils {
	private final static Logger log = Logger.getLogger(FTPUtils.class);
	private final static int timeoutInMillis = 10000;

	public static FTPClient loginFTPClient(String hostName, String userName, String password) throws IOException {
		return loginFTPClient(hostName, userName, password, -1);
	}

	public static FTPClient loginFTPClient(String hostName, String userName, String password, int port)
			throws IOException {

		FTPClient ftpClient = new FTPClient();
		ftpClient.setDefaultTimeout(timeoutInMillis);
		// connect
		if (port > 0) {
			ftpClient.connect(hostName, port);
		} else {
			ftpClient.connect(hostName);
		}
		int replyCode = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(replyCode)) {
			throw new IllegalArgumentException("Error while trying to connect to SFTP in IP2. Reply code: " + replyCode
					+ " message:" + ftpClient.getReplyString());
		}
		showServerReply(ftpClient);
		// login
		boolean success = ftpClient.login(userName, password);
		showServerReply(ftpClient);
		if (!success) {
			throw new IllegalArgumentException("Could not login to the IP2 server");
		} else {
			log.debug("login succesful");
		}
		// enter in passive mode
		ftpClient.enterLocalPassiveMode();
		showServerReply(ftpClient);
		return ftpClient;

	}

	public static Session loginSSHClient(String hostName, String userName, String password, int port)
			throws JSchException {
		JSch jsch = new JSch();
		Session session = jsch.getSession(userName, hostName);
		session.setConfig("StrictHostKeyChecking", "no");
		// non-interactive version. Relies in host key being in known-hosts file
		session.setPassword(password);
		// connect
		session.connect();

		return session;

	}

	public static ChannelSftp openSFTPChannel(Session session) throws JSchException {
		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp sftpChannel = (ChannelSftp) channel;
		return sftpChannel;
	}

	public static void showServerReply(FTPClient ftpClient) {
		showServerReply(ftpClient, null);
	}

	public static void showServerReply(FTPClient ftpClient, PrintStream out) {
		String[] replies = ftpClient.getReplyStrings();
		if (replies != null && replies.length > 0) {
			for (String aReply : replies) {
				if (out != null) {
					out.println("SERVER: " + aReply);
				}
				log.debug("SERVER: " + aReply);
			}
		}
	}

	/**
	 * Creates a nested directory structure on a FTP server
	 * 
	 * @param ftpClient
	 *            an instance of org.apache.commons.net.ftp.FTPClient class.
	 * @param dirPath
	 *            Path of the directory, i.e /projects/java/ftp/demo
	 * @return true if the directory was created successfully, false otherwise
	 * @throws IOException
	 *             if any error occurred during client-server communication
	 */
	public static boolean makeDirectories(FTPClient ftpClient, String dirPath, PrintStream log) throws IOException {
		String[] pathElements = dirPath.split("/");
		if (pathElements != null && pathElements.length > 0) {
			for (String singleDir : pathElements) {
				if (!"".equals(singleDir)) {
					boolean existed = ftpClient.changeWorkingDirectory(singleDir);
					if (!existed) {
						boolean created = ftpClient.makeDirectory(singleDir);
						if (created) {
							log.println("CREATED directory: " + singleDir);
							ftpClient.changeWorkingDirectory(singleDir);
						} else {
							log.println("COULD NOT create directory: " + singleDir);
							showServerReply(ftpClient);
							throw new IllegalArgumentException("Error creating folder '" + singleDir + " ' in server");
						}
					}
				}
			}
		}
		return true;
	}

	public static long getSize(FTPClient ftpClient, String filePath) throws IOException {
		FTPFile[] file = ftpClient.listFiles(filePath);
		if (file.length > 0) {
			return file[0].getSize();
		}
		return -1;
	}

	/**
	 * Determines whether a directory exists or not
	 * 
	 * @param dirPath
	 * @return true if exists, false otherwise
	 * @throws IOException
	 *             thrown if any I/O error occurred.
	 */
	public static boolean checkDirectoryExists(FTPClient ftpClient, String dirPath) throws IOException {
		ftpClient.changeWorkingDirectory(dirPath);
		int returnCode = ftpClient.getReplyCode();
		if (returnCode == 550) {
			return false;
		}
		return true;
	}

	/**
	 * Determines whether a file exists or not
	 * 
	 * @param filePath
	 * @return true if exists, false otherwise
	 * @throws IOException
	 *             thrown if any I/O error occurred.
	 */
	public static boolean checkFileExists(FTPClient ftpClient, String filePath) throws IOException {
		InputStream inputStream = ftpClient.retrieveFileStream(filePath);
		int returnCode = ftpClient.getReplyCode();
		if (inputStream == null || returnCode == 550) {
			return false;
		}
		return true;
	}

	/**
	 * "Getting files recursively from remote host located under folder
	 * folderPath with a certain extension
	 * 
	 * @param extension
	 * @return
	 * @throws IOException
	 * @throws SftpException
	 */
	public static List<FTPFile> getFilesInFolderByExtension(FTPClient ftpClient, String folderPath, String extension)
			throws IOException {
		log.info("Getting files recursively from remote host located under folder: " + folderPath + " with extension "
				+ extension);
		List<FTPFile> ret = new ArrayList<FTPFile>();
		FTPFile[] listFiles = ftpClient.listFiles(folderPath);
		for (FTPFile ftpFile : listFiles) {
			if (FilenameUtils.getExtension(ftpFile.getName()).equalsIgnoreCase(extension)) {
				ret.add(ftpFile);
			}
		}

		return ret;

	}

	public static List<String> getFileNamesInFolderByExtension(FTPClient ftpClient, String folderPath, String extension)
			throws IOException {
		List<FTPFile> ftpFiles = getFilesInFolderByExtension(ftpClient, folderPath, extension);
		List<String> ret = new ArrayList<String>();
		for (FTPFile ftpFile : ftpFiles) {
			ret.add(ftpFile.getName());
		}
		return ret;
	}

	public static List<LsEntry> getFilesInFolderByExtension(Session sshClient, String folderPath, String extension)
			throws JSchException, SftpException {
		ChannelSftp sftpClient = null;
		try {
			sftpClient = openSFTPChannel(sshClient);
			List<LsEntry> sftpFiles = sftpClient.ls(folderPath);
			List<LsEntry> ret = new ArrayList<LsEntry>();
			for (LsEntry sftpFile : sftpFiles) {
				if (FilenameUtils.getExtension(sftpFile.getFilename()).equalsIgnoreCase(extension)) {
					ret.add(sftpFile);
				}
			}
			return ret;
		} finally {
			if (sftpClient != null) {
				sftpClient.quit();
			}
		}
	}

	public static long getSize(Session sshClient, String filePath) throws JSchException, SftpException {
		ChannelSftp sftpClient = null;
		try {
			sftpClient = openSFTPChannel(sshClient);
			SftpATTRS stat = sftpClient.stat(filePath);
			if (stat != null) {
				return stat.getSize();
			}
		} finally {
			if (sftpClient != null) {
				sftpClient.quit();
			}
		}
		return -1;
	}
}