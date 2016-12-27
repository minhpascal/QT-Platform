/**
 * 
 */
package com.qtplaf.library.trading.data;

/**
 * Periods of trading.
 * 
 * @author Miquel Sas
 */
public class Period implements Comparable<Period> {

	/**
	 * Unit.
	 */
	private Unit unit;
	/**
	 * The number of units or size.
	 */
	private int size = -1;

	/**
	 * Constructor assigning unit and size.
	 * 
	 * @param unit The unit.
	 * @param size The size or number of units.
	 */
	public Period(Unit unit, int size) {
		super();
		this.unit = unit;
		this.size = size;
	}

	/**
	 * Returns a string key that uniquely identifies this period.
	 * 
	 * @return A string key.
	 */
	public String getKey() {
		StringBuilder b = new StringBuilder();
		b.append(getUnit().name());
		b.append("(");
		b.append(getSize());
		b.append(")");
		return b.toString();
	}

	/**
	 * Returns the unit.
	 * 
	 * @return The unit.
	 */
	public Unit getUnit() {
		return unit;
	}

	/**
	 * Returns the size or number of units.
	 * 
	 * @return The size or number of units.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Compares this period with the argument object. Returns 0 if they are equal, -1 if this value is less than the
	 * argument, and 1 if it is greater.
	 *
	 * @param p The period to compare with.
	 * @return An integer.
	 */
	public int compareTo(Period p) {

		// Unit equals, the size decides.
		if (getUnit().equals(p.getUnit())) {
			if (getSize() < p.getSize()) {
				return -1;
			} else if (getSize() > p.getSize()) {
				return 1;
			} else {
				return 0;
			}
		}

		// The ordinal of the unit decides.
		if (getUnit().ordinal() < p.getUnit().ordinal()) {
			return -1;
		} else if (getUnit().ordinal() > p.getUnit().ordinal()) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 *
	 * @return A boolean.
	 * @param o The object to compare with.
	 */
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof Period) {
			Period p = (Period) o;
			return compareTo(p) == 0;
		}
		return false;
	}

	/**
	 * Returns a string representation of this period.
	 * 
	 * @return A string representation of this period.
	 */
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getSize());
		b.append(" ");
		b.append(getUnit().name());
		if (getSize() > 1) {
			b.append("s");
		}
		return b.toString();
	}
	
	/**
	 * Returns an XML element representation of this period.
	 * 
	 * @return An XML element representation of this period.
	 */
	public String toXML() {
		StringBuilder b = new StringBuilder();
		b.append("<period");
		b.append(" unit=\"" + getUnit().name() + "\"");
		b.append(" size=\"" + getSize() + "\"");
		b.append("/>");
		return b.toString();
	}
}
