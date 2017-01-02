/*
 * Copyright (C) 2015 Miquel Sas
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.qtplaf.library.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.qtplaf.library.util.FormatUtils;

/**
 * File utilities.
 * 
 * @author Miquel Sas
 */
public class FileUtils {

	/**
	 * Enum sizes.
	 */
	public enum Size {
		Byte("B", Math.pow(2, 10 * 0)),
		KiloByte("KB", Math.pow(2, 10 * 1)),
		MegaByte("MB", Math.pow(2, 10 * 2)),
		GigaByte("GB", Math.pow(2, 10 * 3)),
		TeraByte("TB", Math.pow(2, 10 * 4)),
		PetaByte("PB", Math.pow(2, 10 * 5)),
		ExaByte("EB", Math.pow(2, 10 * 6)),
		ZettaByte("ZB", Math.pow(2, 10 * 7)),
		YottaByte("YB", Math.pow(2, 10 * 8));

		/** Size. */
		private double size;
		/** Label. */
		private String label;

		/**
		 * Constructor.
		 * 
		 * @param label The label.
		 * @param size The size.
		 */
		private Size(String label, double size) {
			this.label = label;
			this.size = size;
		}

		/**
		 * Returns the size.
		 * 
		 * @return The size.
		 */
		public double getSize() {
			return size;
		}

		/**
		 * Returns the label.
		 * 
		 * @return The label.
		 */
		public String getLabel() {
			return label;
		}
	}

	/**
	 * Returns tje size label in the form of 25,6 KB, for example. The most approximated label with the argument number
	 * of decimals.
	 * 
	 * @param size The size in bytes to label.
	 * @return The number of decimals.
	 */
	public static String getSizeLabel(long size, int decimals, Locale locale) {
		for (Size value : Size.values()) {
			double v = size / value.getSize();
			if (v < 1024) {
				return FormatUtils.formattedFromDouble(v, decimals, locale) + " " + value.getLabel();
			}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Check if the parent argument file is effectively a parent of the child argument.
	 * 
	 * @param parent The parent file (a directory).
	 * @param child The child, either a sub-file or sub-directory.
	 * @return A boolean.
	 */
	public static boolean isParent(File parent, File child) {
		String parentPath = parent.getAbsolutePath();
		String childPath = child.getAbsolutePath();
		return childPath.startsWith(parentPath);
	}

	/**
	 * Check if the argument file is empty. If the file is a file it must have zero length. If it is a directory it must
	 * contain nothing.
	 * 
	 * @param file The file to check.
	 * @return A boolean indicating whether the file is empty.
	 */
	public static boolean isEmpty(File file) {
		if (file.isFile()) {
			return (file.length() == 0);
		}
		if (file.isDirectory()) {
			return (file.listFiles().length == 0);
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Returns the destination file in a mirror copy task, given the source directory that is the starting partial path
	 * of the source file, and the destination directory that will be the same partial path of the destination file.
	 * 
	 * @param sourceDirectory The source partial path directory.
	 * @param sourceFile The source file.
	 * @param destinationDirectory The destination partial path directory.
	 * @return The destination file.
	 */
	public static File getDestinationFile(File sourceDirectory, File sourceFile, File destinationDirectory) {
		String destinationName = sourceFile.getAbsolutePath().substring(sourceDirectory.getAbsolutePath().length());
		return new File(destinationDirectory, destinationName);
	}

	/**
	 * Returns the source file in a mirror copy task, given the destination directory that is the starting partial path
	 * of the destination file, and the source directory that will be the same partial path of the source file.
	 * 
	 * @param destinationDirectory The destination partial path directory.
	 * @param destinationFile The destination file.
	 * @param sourceDirectory The source partial path directory.
	 * @return The source file.
	 */
	public static File getSourceFile(File destinationDirectory, File destinationFile, File sourceDirectory) {
		return getDestinationFile(destinationDirectory, destinationFile, sourceDirectory);
	}

	/**
	 * Returns the path of the argument file as a list of files.
	 * 
	 * @param file The source file.
	 * @return The path as a list of directory files.
	 */
	public static List<File> getPath(File file) {
		List<File> path = new ArrayList<>();
		while (file.getParentFile() != null) {
			path.add(0, file.getParentFile());
			file = file.getParentFile();
		}
		return path;
	}

	/**
	 * List all files and directories in the source directory and sub-directories.
	 * 
	 * @param sourceDirectory The source directory.
	 * @return The list of files.
	 * @throws IOException
	 */
	public static List<File> list(File sourceDirectory) throws IOException {
		List<File> files = new ArrayList<>();
		fill(files, sourceDirectory, true);
		return files;
	}

	/**
	 * List all files and directories in the source directory and sub-directories.
	 * 
	 * @param sourceDirectory The source directory.
	 * @param subDirectories A boolean that indicates whether sub-directories should be scanned.
	 * @return The list of files.
	 * @throws IOException
	 */
	public static List<File> list(File sourceDirectory, boolean subDirectories) throws IOException {
		List<File> files = new ArrayList<>();
		fill(files, sourceDirectory, subDirectories);
		return files;
	}

	/**
	 * Fills the list with all files and directories in the source directory and sub-directories.
	 * 
	 * @param files The list of files to fill.
	 * @param sourceDirectory The source directory.
	 * @throws IOException
	 */
	private static void fill(List<File> files, File sourceDirectory, boolean subDirectories) throws IOException {
		File[] sources = sourceDirectory.listFiles();
		for (File source : sources) {
			files.add(source);
			if (source.isDirectory() && subDirectories) {
				fill(files, source, subDirectories);
			}
		}
	}

	/**
	 * Copy a source file to a destination file.
	 * 
	 * @param sourceFile The source file.
	 * @param destinationFile The destination file.
	 * @param bufferSize IO buffer size.
	 * @throws IOException
	 */
	public static void copy(File sourceFile, File destinationFile) throws IOException {
		copy(sourceFile, destinationFile, 8192);
	}

	/**
	 * Copy a source file to a destination file.
	 * 
	 * @param sourceFile The source file.
	 * @param destinationFile The destination file.
	 * @param bufferSize IO buffer size.
	 * @throws IOException
	 */
	public static void copy(File sourceFile, File destinationFile, int bufferSize) throws IOException {

		// Check source exists.
		if (!sourceFile.exists()) {
			throw new IOException("Invalid source file");
		}
		// Check source file is a file (not a directory)
		if (!sourceFile.isFile()) {
			throw new IOException("Source expected to be file");
		}
		// If the destination file does not exists, ensure that the parent directory exists.
		if (!destinationFile.exists()) {
			if (!destinationFile.getParentFile().exists()) {
				destinationFile.getParentFile().mkdirs();
			}
		}
		
		// Source.
		FileInputStream fi = new FileInputStream(sourceFile);
		BufferedInputStream bi = new BufferedInputStream(fi, bufferSize);

		// Destination.
		FileOutputStream fo = new FileOutputStream(destinationFile);
		BufferedOutputStream bo = new BufferedOutputStream(fo, bufferSize);

		// Do copy.
		int b;
		while ((b = bi.read()) >= 0) {
			bo.write(b);
		}

		// Close resources.
		bi.close();
		fi.close();
		bo.close();
		fo.close();
	}
}
