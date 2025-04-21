package com.ibm.wssvt.acme.common.log;

import java.io.IOException;
import java.util.logging.FileHandler;
/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class AcmeLogFileHandler extends FileHandler {

	private String fileNamePattern;

	public AcmeLogFileHandler(String pattern, int limit, int count,
			boolean append) throws IOException, SecurityException {
		super(pattern, limit, count, append);
		this.fileNamePattern = pattern;		
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((fileNamePattern == null) ? 0 : fileNamePattern.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AcmeLogFileHandler other = (AcmeLogFileHandler) obj;
		if (fileNamePattern == null) {
			if (other.fileNamePattern != null)
				return false;
		} else if (!fileNamePattern.equals(other.fileNamePattern))
			return false;
		return true;
	}

	public String getFileNamePattern() {
		return fileNamePattern;
	}

}
