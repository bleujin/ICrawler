package net.ion.icrawler.utils;

import java.io.File;

import org.apache.commons.lang.SystemUtils;

/**
 * Base object of file persistence.
 */
public class FilePersistentBase {

	protected String path;

	public static String PATH_SEPERATOR = SystemUtils.FILE_SEPARATOR;

	public void setPath(String path) {
		if (!path.endsWith(PATH_SEPERATOR)) {
			path += PATH_SEPERATOR;
		}
		this.path = path;
	}

	public File getFile(String fullName) {
		checkAndMakeParentDirecotry(fullName);
		return new File(fullName);
	}

	public void checkAndMakeParentDirecotry(String fullName) {
		int index = fullName.lastIndexOf(PATH_SEPERATOR);
		if (index > 0) {
			String path = fullName.substring(0, index);
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
	}

	public String getPath() {
		return path;
	}
}
