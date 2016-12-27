/**
 * 
 */
package com.qtplaf.library.util.xml;

/**
 * An XML attribute packs a key and a value.
 * 
 * @author Miquel Sas
 */
public class XMLAttribute {

	/**
	 * The key.
	 */
	private String key;
	/**
	 * The value.
	 */
	private String value;

	/**
	 * Default constructor.
	 */
	public XMLAttribute() {
		super();
	}

	/**
	 * Constructor assigning the key and the value.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public XMLAttribute(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	/**
	 * Get the key.
	 * 
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Set the key.
	 * 
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Get the value.
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the value.
	 * 
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns the hash code.
	 */
	public int hashCode() {
		return key.hashCode();
	}

	/**
	 * Check for equality.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof XMLAttribute) {
			XMLAttribute attr = (XMLAttribute)obj;
			return key.equals(attr.key);
		}
		return false;
	}

	/**
	 * Returns a string representation.
	 */
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(key);
		b.append(",");
		b.append(value);
		b.append("]");
		return b.toString();
	}

}
