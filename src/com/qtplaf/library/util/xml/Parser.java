/**
 * 
 */
package com.qtplaf.library.util.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * Wraps the <i>SAXParser</i> to handler events with the more simple and convenient <i>ParserHandler</i>.
 * 
 * @author Miquel Sas
 */
public class Parser {

	/**
	 * Default constructor.
	 */
	public Parser() {
	}

	/**
	 * Parse the file handling event with the argument parser handler.
	 * 
	 * @param file The XML file to parse.
	 * @param handler The parser handler.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parse(File file, ParserHandler handler)
		throws ParserConfigurationException, SAXException, IOException {
		parse(new FileInputStream(file), handler);
	}

	/**
	 * Parse the file handling event with the argument parser handler.
	 * 
	 * @param is The input stream.
	 * @param handler The parser handler.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parse(InputStream is, ParserHandler handler)
		throws ParserConfigurationException, SAXException, IOException {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		InternalParserHandler internalHandler = new InternalParserHandler(handler);
		try {
			parser.parse(is, internalHandler);
		} finally {
			is.close();
		}
	}

}
