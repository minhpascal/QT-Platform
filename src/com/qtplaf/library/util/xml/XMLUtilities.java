/**
 * 
 */
package com.qtplaf.library.util.xml;

/**
 * Static XML utilities.
 * 
 * @author Miquel Sas
 */
public class XMLUtilities {
	/**
	 * Returns the argument input string conveniently escaped.
	 * 
	 * @param in The input string.
	 * @return The escaped output string.
	 */
	public static String getEscaped(String in) {
		StringBuilder out = new StringBuilder();
		int length = in.length();
		for (int i = 0; i < length; i++) {
			char c = in.charAt(i);
			switch (c) {
			case '&':
				out.append("&amp;");
				break;
			case '<':
				out.append("&lt;");
				break;
			case '>':
				out.append("&gt;");
				break;
			case '"':
				out.append("&quot;");
				break;
			case '\'':
				out.append("&apos;");
				break;
			default:
				out.append(c);
				break;
			}
		}
		return out.toString();
	}
}
