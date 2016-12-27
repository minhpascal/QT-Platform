/**
 * 
 */
package com.qtplaf.library.util;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.ImageIcon;

/**
 * Static image icon utilities.
 * 
 * @author Miquel Sas
 */
public class ImageIconUtils {

	/**
	 * The list of jar files that contains images.
	 */
	private static ArrayList<String> iconImagesFiles = new ArrayList<>();
	/**
	 * The map that will contain images loaded from jar files or the file system.
	 */
	private static HashMap<String, ImageIcon> iconImagesMap = new HashMap<>();

	/**
	 * Add a file name to the list of file name that contain icon images. Default is images.jar
	 * 
	 * @param fileName The file name to add.
	 */
	synchronized public static void addIconImageFile(String fileName) {
		iconImagesFiles.add(fileName);
	}

	/**
	 * Returns the image icon scanning a list of jar files that contain images and if not found finally scanning the
	 * file system.
	 * 
	 * @param imageName The image path name.
	 * @return The ImageIcon or null if the image was not found or an IO exception was thrown.
	 */
	synchronized public static ImageIcon getImageIcon(String imageName) {
		try {
			ImageIcon imageIcon = iconImagesMap.get(imageName);
			if (imageIcon != null) {
				return imageIcon;
			}
			if (iconImagesFiles.isEmpty()) {
				addIconImageFile("images.jar");
			}
			for (String fileName : iconImagesFiles) {
				byte[] bytes = SystemUtils.getJarEntry(fileName, imageName);
				if (bytes != null) {
					imageIcon = new ImageIcon(bytes);
					iconImagesMap.put(imageName, imageIcon);
					return imageIcon;
				}
			}
			byte[] bytes = SystemUtils.getFileBytes(imageName);
			if (bytes != null) {
				imageIcon = new ImageIcon(bytes);
				iconImagesMap.put(imageName, imageIcon);
				return imageIcon;
			}
			String error = TextServer.getString("exceptionImageNotFound", Locale.UK);
			throw new IOException(MessageFormat.format(error, imageName));
		} catch (IOException ioExc) {
			ioExc.printStackTrace();
		}
		return null;
	}
}
