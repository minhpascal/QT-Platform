/**
 * 
 */
package com.qtplaf.library.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Static system utilities.
 * 
 * @author Miquel Sas
 */
public class SystemUtils {
	/**
	 * Returns the entry in a jar file.
	 * 
	 * @param fileName The jar file name.
	 * @param entryName The name (relative path) of the entry.
	 * @return The entry as a byte array.
	 * @throws IOException If an IO error occurs.
	 */
	public static byte[] getJarEntry(String fileName, String entryName) throws IOException {
		File file = getFileFromClassPathEntries(fileName);
		if (file != null && file.isFile()) {
			return getJarEntry(file, entryName);
		}
		return null;
	}
	/**
	 * Returns the entry in a jar file.
	 * 
	 * @param file The jar file.
	 * @param entryName The name (relative path) of the entry.
	 * @return The entry as a byte array.
	 * @throws IOException If an IO error occurs.
	 */
	public static byte[] getJarEntry(File file, String entryName) throws IOException {
		JarFile jarFile = new JarFile(file);
		try {
			JarEntry entry = jarFile.getJarEntry(entryName);
			if (entry == null) {
				return null;
			}
			int size = (int) entry.getSize();
			byte[] bytes = new byte[size];
			InputStream is = jarFile.getInputStream(entry);
			is.read(bytes, 0, size);
			is.close();
			return bytes;
		} finally {
			jarFile.close();
		}
	}

	/**
	 * Reads entries of a jar file.
	 * 
	 * @param file The jar file.
	 * @param names The names (relative paths) of the entries.
	 * @return The entries stored in a 2 dimension byte array.
	 * @throws IOException If an IO error occurs.
	 */
	public static byte[][] getJarEntries(File file, String... names) throws IOException {

		byte[][] bytes = new byte[names.length][];

		JarFile jarFile = new JarFile(file);
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			JarEntry entry = jarFile.getJarEntry(name);
			int size = (int) entry.getSize();
			bytes[i] = new byte[size];
			InputStream is = jarFile.getInputStream(entry);
			is.read(bytes[i], 0, size);
			is.close();
		}
		jarFile.close();

		return bytes;
	}
	/**
	 * Read a file and return it as a <code>byte[]</code>. The file length must be an integer.
	 * 
	 * @param fileName The file name.
	 * @return The array ofbytes.
	 * @throws IOException
	 */
	public static byte[] getFileBytes(String fileName) throws IOException {
		File file = getFileFromClassPathEntries(fileName);
		if (file != null && file.isFile()) {
			return getFileBytes(file);
		}
		return null;
	}

	/**
	 * Read a file and return it as a <code>byte[]</code>. The file length must be an integer.
	 * 
	 * @param file The file.
	 * @return The array ofbytes.
	 * @throws IOException
	 */
	public static byte[] getFileBytes(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		int size = (int) file.length();
		byte[] bytes = new byte[size];
		is.read(bytes, 0, size);
		is.close();
		return bytes;
	}

	/**
	 * Returns the system class path.
	 * 
	 * @return The system class path.
	 */
	public static String getSystemClassPath() {
		return System.getProperty("java.class.path");
	}

	/**
	 * Returns an array of system class path entries.
	 * 
	 * @return The array of system class path entries.
	 */
	public static String[] getClassPathEntries() {
		return getClassPathEntries(getSystemClassPath());
	}

	/**
	 * Returns an array of class path entries parsing the class path string.
	 * 
	 * @return An array of class path entries.
	 * @param classPath The class path.
	 */
	public static String[] getClassPathEntries(String classPath) {
		String pathSeparator = System.getProperty("path.separator");
		StringTokenizer tokenizer = new StringTokenizer(classPath, pathSeparator);
		ArrayList<String> entries = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			entries.add(tokenizer.nextToken());
		}
		return entries.toArray(new String[entries.size()]);
	}

	/**
	 * Searches and returns the first file with the given name in one of the directories of the system class path.
	 * 
	 * @return The file.
	 * @param fileName The file name.
	 * @throws FileNotFoundException If the file was not found.
	 */
	public static File getFileFromClassPathEntries(String fileName) throws FileNotFoundException {
		return getFileFromClassPathEntries(fileName, getClassPathEntries());
	}

	/**
	 * Searches and returns the first file with the given name in one of the directories of the class path.
	 * 
	 * @return The file.
	 * @param fileName The file name.
	 * @param classPathEntries An array of class path entries.
	 * @throws FileNotFoundException If the file was not found.
	 */
	public static File getFileFromClassPathEntries(String fileName, String[] classPathEntries)
		throws FileNotFoundException {

		// Direct files.
		File fileDirect = new File(fileName);
		if (fileDirect.exists()) {
			return fileDirect;
		}

		// Convert to relative
		fileName = fileName.replace('\\', '/');

		// ClassPath entries files.
		for (int i = 0; i < classPathEntries.length; i++) {
			File file = new File(classPathEntries[i]);
			if (file.exists() && file.isFile() && file.getName().equals(fileName)) {
				return file;
			}
			int len = classPathEntries[i].length();
			boolean fileSep = classPathEntries[i].substring(len - 1, len).equals(File.separator);
			String filePathName = classPathEntries[i] + (!fileSep ? File.separator : "") + fileName;
			file = new File(filePathName);
			if (file.exists()) {
				return file;
			}
		}
		throw new FileNotFoundException(fileName);
	}

	/**
	 * Gets the properties by loading the file.
	 *
	 * @return The properties.
	 * @param file The file.
	 * @throws IOException If an IO error occurs.
	 */
	public static Properties getProperties(File file) throws IOException {
		boolean xml = false;
		if (getFileExtension(file.getAbsolutePath()).toLowerCase().equals("xml")) {
			xml = true;
		}
		FileInputStream fileIn = new FileInputStream(file);
		BufferedInputStream buffer = new BufferedInputStream(fileIn, 4096);
		Properties properties = getProperties(buffer, xml);
		buffer.close();
		fileIn.close();
		return properties;
	}

	/**
	 * Gets the properties from the input stream.
	 * 
	 * @return The properties.
	 * @param stream The input stream.
	 * @throws IOException If an IO error occurs.
	 */
	public static Properties getProperties(InputStream stream) throws IOException {
		return getProperties(stream, false);
	}

	/**
	 * Gets the properties from the input stream.
	 * 
	 * @return The properties.
	 * @param stream The input stream.
	 * @param xml A boolean that indicates if the input stream has an xml format
	 * @throws IOException If an IO error occurs.
	 */
	public static Properties getProperties(InputStream stream, boolean xml) throws IOException {
		Properties properties = new Properties();
		if (xml) {
			properties.loadFromXML(stream);
		} else {
			properties.load(stream);
		}
		return properties;
	}

	/**
	 * Returns the localized file or the default given the locale, the file name and the extension.
	 * 
	 * @param locale The locale.
	 * @param name The file name.
	 * @param ext The file extension.
	 * @return The localized file or null if it does not exist.
	 */
	public static File getLocalizedFile(Locale locale, String name, String ext) {
		String fileName;
		File file = null;

		// Ensure that the extension is correct
		ext = ((ext == null || ext.length() == 0) ? "" : (ext.charAt(0) == '.') ? ext : "." + ext);

		// First attempt: language and country.
		if (!locale.getCountry().isEmpty()) {
			try {
				fileName = name + "_" + locale.getLanguage() + "_" + locale.getCountry() + ext;
				file = getFileFromClassPathEntries(fileName);
			} catch (FileNotFoundException e) {
			}
		}
		if (file != null) {
			return file;
		}

		// Second attempt: language only
		if (!locale.getLanguage().isEmpty()) {
			try {
				fileName = name + "_" + locale.getLanguage() + ext;
				file = getFileFromClassPathEntries(fileName);
			} catch (FileNotFoundException e) {
			}
		}
		if (file != null) {
			return file;
		}

		// Third attempt: no locale reference
		try {
			fileName = name + ext;
			file = getFileFromClassPathEntries(fileName);
		} catch (FileNotFoundException e) {
		}

		return file;
	}

	/**
	 * Return the name part of a file name, without the extension if present.
	 * 
	 * @param fileName The file name.
	 * @return The name part.
	 */
	public static String getFileName(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index == -1) {
			return fileName;
		}
		return fileName.substring(0, index);
	}

	/**
	 * Return the extension part of a file name.
	 * 
	 * @param fileName The file name.
	 * @return The extension part.
	 */
	public static String getFileExtension(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index == -1) {
			return "";
		}
		return fileName.substring(index + 1);
	}

}
