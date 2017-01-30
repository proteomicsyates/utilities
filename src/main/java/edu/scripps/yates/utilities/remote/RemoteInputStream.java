package edu.scripps.yates.utilities.remote;

import java.io.InputStream;

public interface RemoteInputStream {
	public InputStream getInputStream();

	public void close();
}
