package com.qtplaf.library.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A panel to contain an arbitrary number of buttons defined by actions, wrapping them as necessary depending on the
 * number and the panel size.
 * 
 * @author Miquel Sas
 */
public class JPanelButtons extends JPanel {

	/**
	 * List of actions.
	 */
	private ActionList actions = new ActionList();
	/**
	 * The separator width.
	 */
	private int separatorWidth = 12;

	/**
	 * Default constructor.
	 */
	public JPanelButtons() {
		super();
		setLayout(WrapLayout.CENTER);
		setOpaque(false);
	}

	/**
	 * Constructor assigning the alignment.
	 * 
	 * @param align The alignment.
	 */
	public JPanelButtons(int align) {
		super();
		setLayout(align);
		setOpaque(false);
	}

	/**
	 * Add an action.
	 * 
	 * @param action The action.
	 */
	public void add(Action action) {
		actions.add(action);
		layoutButtons();
	}

	/**
	 * Remove the argument action.
	 * 
	 * @param action The action to remove.
	 */
	public void remove(Action action) {
		actions.remove(action);
		layoutButtons();
	}

	/**
	 * Returns a copy of the list of actions.
	 * 
	 * @return The list of actions.
	 */
	public List<Action> getActions() {
		return actions.getActions();
	}

	/**
	 * Returns the action of the given class.
	 * 
	 * @param actionClass The action class.
	 * @return The first action with the argument class if exists.
	 */
	public Action getAction(Class<? extends Action> actionClass) {
		return actions.getAction(actionClass);
	}

	/**
	 * Assign alignment and gaps.
	 * 
	 * @param align Alignment.
	 */
	public void setLayout(int align) {
		setLayout(align, 2, 2);
	}

	/**
	 * Assign alignment and gaps.
	 * 
	 * @param align Alignment.
	 * @param hgap Horizontal gap.
	 * @param vgap Vertical gap.
	 */
	public void setLayout(int align, int hgap, int vgap) {
		setLayout(new WrapLayout(align, hgap, vgap));
	}

	/**
	 * @throws UnsupportedOperationException to deactivate the method.
	 */
	public Component add(String name, Component comp) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Return the preferred layout size.
	 * 
	 * @return The size.
	 */
	public Dimension getPreferredLayoutSize() {
		return ((WrapLayout) getLayout()).preferredLayoutSize(this);
	}

	/**
	 * Returns the separator width.
	 * 
	 * @return The separator width.
	 */
	public int getSeparatorWidth() {
		return separatorWidth;
	}

	/**
	 * Sets the separator width.
	 * 
	 * @param separatorWidth The separator width.
	 */
	public void setSeparatorWidth(int separatorWidth) {
		this.separatorWidth = separatorWidth;
	}

	/**
	 * Returns the separator label.
	 * 
	 * @return The separator label.
	 */
	private JLabel getSeparator() {
		JLabel separator = new JLabel();
		Dimension size = new Dimension(getSeparatorWidth(), 1);
		separator.setMinimumSize(size);
		separator.setPreferredSize(size);
		return separator;
	}

	/**
	 * Layout the buttons in the proper order determined by action groups and sort indexes.
	 */
	private void layoutButtons() {
		removeAll();
		List<JButton> buttons = actions.getButtonsVisibleInButtonsPanel();
		for (int i = 0; i < buttons.size(); i++) {
			JButton currentButton = buttons.get(i);
			if (i == 0) {
				super.add(currentButton);
				continue;
			}
			JButton previousButton = buttons.get(i - 1);
			Action previousAction = previousButton.getAction();
			Action currentAction = currentButton.getAction();
			if (previousAction == null && currentAction == null) {
				super.add(currentButton);
				continue;
			}
			if (previousAction == null && currentAction != null) {
				super.add(getSeparator());
				super.add(currentButton);
				continue;
			}
			if (previousAction != null && currentAction == null) {
				super.add(getSeparator());
				super.add(currentButton);
				continue;
			}
			ActionGroup previousActionGroup = ActionUtils.getActionGroup(previousAction);
			ActionGroup currentActionGroup = ActionUtils.getActionGroup(currentAction);
			if (!previousActionGroup.equals(currentActionGroup)) {
				super.add(getSeparator());
			}
			super.add(currentButton);
		}
		SwingUtils.setMnemonics(buttons);
	}
}