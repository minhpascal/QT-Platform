package com.qtplaf.library.swing;

/**
 * Action ggroups are used to group actions.
 * 
 * @author Miquel Sas
 */
public class ActionGroup implements Comparable<ActionGroup> {

	/**
	 * Predefined default action group.
	 */
	public final static ActionGroup Default = new ActionGroup("Default", Integer.MAX_VALUE - 100);
	/**
	 * Predefined edit action group (New, Modify, Delete...)
	 */
	public final static ActionGroup Edit = new ActionGroup("Edit", 100);
	/**
	 * Predefine configure action group.
	 */
	public final static ActionGroup Configure = new ActionGroup("Configure", 200);
	/**
	 * Predefined action group for intput actions.
	 */
	public final static ActionGroup Intput = new ActionGroup("Intput", 300);
	/**
	 * Predefined action group for output actions.
	 */
	public final static ActionGroup Output = new ActionGroup("Output", 400);
	/**
	 * Predefined action group for detail actions.
	 */
	public final static ActionGroup Detail = new ActionGroup("Detail", 500);
	/**
	 * Predefined action group for lookups.
	 */
	public final static ActionGroup Lookup = new ActionGroup("Lookup", 600);
	/**
	 * Predefined action group for undetermined operations.
	 */
	public final static ActionGroup Operation = new ActionGroup("Lookup", 700);
	/**
	 * Predefined exit action group.
	 */
	public final static ActionGroup Exit = new ActionGroup("Exit", Integer.MAX_VALUE);

	/**
	 * The name or identifier of the group.
	 */
	private String name;
	/**
	 * The index to sort the group within a list of groups.
	 */
	private int sortIndex = -1;

	/**
	 * Constructor assigning the name.
	 * 
	 * @param name This group name.
	 * @param sortIndex The sort index.
	 */
	public ActionGroup(String name, int sortIndex) {
		super();
		this.name = name;
		this.sortIndex = sortIndex;
	}

	/**
	 * Get this group name.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set this group name.
	 * 
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Check wheter the argument object is equal to this action group.
	 *
	 * @param obj The object to compare
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ActionGroup)) {
			return false;
		}
		ActionGroup group = (ActionGroup) obj;
		return getName().equals(group.getName());
	}

	/**
	 * Returns the hash code for this field.
	 *
	 * @return The hash code
	 */
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	/**
	 * Gets a string representation of the field.
	 * 
	 * @return A string representation of this field.
	 */
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Returns the sort index.
	 * 
	 * @return The sort index.
	 */
	public int getSortIndex() {
		return sortIndex;
	}

	/**
	 * Sets the sort index.
	 * 
	 * @param sortIndex The sort index to set.
	 */
	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

	/**
	 * Returns a negative integer, zero, or a positive integer as this value is less than, equal to, or greater than the
	 * specified value.
	 * <p>
	 * A field is considered to be equal to another field if the alias, type, length and decimals are the same.
	 *
	 * @param o The object to compare.
	 * @return The comparison integer.
	 */
	@Override
	public int compareTo(ActionGroup actionGroup) {
		return new Integer(getSortIndex()).compareTo(actionGroup.getSortIndex());
	}

}
