package edu.scripps.yates.utilities.remote;

import java.io.InputStream;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

public class RemoteInputStreamChannelSftp implements RemoteInputStream {
	private final ChannelSftp sftp;
	private final String fileName;

	public RemoteInputStreamChannelSftp(ChannelSftp sftp, String fileName) {
		this.sftp = sftp;
		this.fileName = fileName;

	}

	@Override
	public InputStream getInputStream() {
		try {
			return sftp.get(fileName);
		} catch (SftpException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void close() {
		sftp.disconnect();
	}

}
