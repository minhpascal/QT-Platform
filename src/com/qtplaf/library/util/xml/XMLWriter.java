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
package com.qtplaf.library.util.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import com.qtplaf.library.util.StringUtils;

/**
 * Utility class to write XML files.
 * 
 * @author Miquel Sas
 */
public class XMLWriter {

	/**
	 * XML version.
	 */
	private String version = "1.0";
	/**
	 * XML encoding.
	 */
	private String encoding = "UTF-8";
	/**
	 * Tab level to properly format the resulting file..
	 */
	private int tabLevel = 0;
	/**
	 * The print writer.
	 */
	private PrintWriter printWriter;
	/**
	 * The stack of non empty tags.
	 */
	private Deque<String> stack = new ArrayDeque<>();

	/**
	 * Constructor assigning the output stream.
	 * 
	 * @param outputStream The output stream.
	 * @throws UnsupportedEncodingException
	 */
	public XMLWriter(OutputStream outputStream) throws UnsupportedEncodingException {
		super();
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, getEncoding());
		printWriter = new PrintWriter(outputStreamWriter);
	}

	/**
	 * Constructor assigning the destination file
	 * 
	 * @param file The destination file.
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public XMLWriter(File file) throws UnsupportedEncodingException, FileNotFoundException {
		super();
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, getEncoding());
		printWriter = new PrintWriter(outputStreamWriter);
	}

	/**
	 * Flush and close this writer.
	 */
	public void close() {
		printWriter.flush();
		printWriter.close();
	}

	/**
	 * Increase the tab level.
	 */
	public void increaseTabLevel() {
		tabLevel++;
	}

	/**
	 * Decrease the tab level.
	 */
	public void decreaseTabLevel() {
		tabLevel--;
	}

	/**
	 * Returns the sequence of tabs that corresponds to the tab level.
	 * 
	 * @return The sequence of tabs.
	 */
	private String getTabs() {
		return StringUtils.repeat("\t", tabLevel);
	}

	/**
	 * Returns the XML version.
	 * 
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the XML version.
	 * 
	 * @param version The XML version.
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Returns the file encoding.
	 * 
	 * @return The file encoding.
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Sets the file encoding.
	 * 
	 * @param encoding The file encoding.
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Returns the first line header that indicates the version and the file encoding.
	 * 
	 * @return The header line.
	 */
	public String getVersionAndEncodingHeader() {
		return "<?xml version=\"" + getVersion() + "\" encoding=\"" + getEncoding() + "\"?>";
	}

	/**
	 * Print a string.
	 * 
	 * @param string The string to print.
	 */
	public void print(String string) {
		printWriter.print(string);
	}

	/**
	 * Print an empty new line.
	 */
	public void println() {
		printWriter.println();
	}

	/**
	 * Print a line inserting the corresponding tab level tabs.
	 * 
	 * @param line The line to print.
	 */
	public void println(String line) {
		printWriter.println(line);
	}

	/**
	 * Prints a start tag, with no attributes.
	 * 
	 * @param tag The tag to print.
	 */
	public void printStartTag(String tag) {
		printStartTag(tag, null);
	}

	/**
	 * Prints a start tag, with the attributes if any.
	 * 
	 * @param tag The tag to print.
	 * @param attributes The list of attributes.
	 */
	public void printStartTag(String tag, List<XMLAttribute> attributes) {
		printStartTag(tag, attributes, false);
	}

	/**
	 * Prints a start tag, with the attributes if any, closing it if it's an empty tag.
	 * 
	 * @param tag The tag to print.
	 * @param attributes The list of attributes.
	 * @param empty A boolean that indicates if the tag is empty.
	 */
	public void printStartTag(String tag, List<XMLAttribute> attributes, boolean empty) {

		// Push the tag onto the stack if the tag is not empty and should be closed after.
		if (!empty) {
			stack.push(tag);
		}

		// Start printing the tag.
		print(getTabs() + "<" + tag);

		// If there are attributes to print, print each one in a new line.
		if (attributes != null && !attributes.isEmpty()) {
			// Increase the tab level.
			increaseTabLevel();
			// Print each attribute in a new line.
			for (XMLAttribute attribute : attributes) {
				println();
				print(getTabs());
				print(attribute.getKey());
				print("=\"");
				print(XMLUtilities.getEscaped(attribute.getValue()));
				print("\"");
			}
		}

		// Finish printing the start tag...
		if (empty) {
			print("/");
		}
		print(">");
		println();

		// If the tab level was increased due to attributes printing, decrease it.
		if (attributes != null && !attributes.isEmpty()) {
			decreaseTabLevel();
		}
	}

	/**
	 * Print an end tag, getting it from the stack.
	 */
	public void printEndTag() {
		String tag = stack.pop();
		println(getTabs() + "</" + tag + ">");
	}

	/**
	 * Prints a long description.
	 * 
	 * @param tag The description tag.
	 * @param description The description.
	 */
	public void printDescription(String tag, String description) {
		printStartTag(tag);
		println(XMLUtilities.getEscaped(description));
		printEndTag();
	}

	/**
	 * Print an XML comment.
	 * 
	 * @param comment
	 */
	public void printComment(String comment) {
		print("<!-- ");
		print(comment);
		print(" -->");
		println();
	}

}
