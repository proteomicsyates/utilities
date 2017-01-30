package edu.scripps.yates.utilities.remote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class RemoteInputStreamFile implements RemoteInputStream {
	private final File file;
	private InputStream inputStream;

	public RemoteInputStreamFile(File file) {
		this.file = file;
	}

	@Override
	public InputStream getInputStream() {
		try {
			inputStream = new FileInputStream(file);
			return inputStream;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void close() {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
