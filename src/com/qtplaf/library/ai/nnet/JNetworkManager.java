/**
 * 
 */
package com.qtplaf.library.ai.nnet;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * @author Miquel Sas
 *
 */
public class JNetworkManager extends JFrame {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 6633615459388701788L;

	/**
	 * Default constructor.
	 * 
	 * @throws HeadlessException if GraphicsEnvironment.isHeadless()
	 */
	public JNetworkManager() throws HeadlessException {
		super();
		setupFrame();
	}
	
	/**
	 * Setup the frame by adding its components.
	 */
	private void setupFrame() {
		getContentPane().setLayout(new GridBagLayout());
		
		GridBagConstraints constraintsMenuBar = new GridBagConstraints();
		constraintsMenuBar.anchor = GridBagConstraints.NORTH;
		constraintsMenuBar.fill = GridBagConstraints.HORIZONTAL;
		constraintsMenuBar.gridheight = 1;
		constraintsMenuBar.gridwidth = 1;
		constraintsMenuBar.weightx = 1;
		constraintsMenuBar.weighty = 0;
		constraintsMenuBar.gridx = 0;
		constraintsMenuBar.gridy = 0;
		constraintsMenuBar.insets = new Insets(1, 1, 1, 1);

		// A menu bar
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setPreferredSize(new Dimension(0, 20));
		menuBar.setMinimumSize(new Dimension(0, 20));
		JMenu menuFile = new JMenu("File");
		JMenuItem itemFileSave = new JMenuItem("Save");
		menuFile.add(itemFileSave);
		menuBar.add(menuFile);

		getContentPane().add(menuBar, constraintsMenuBar);
	}
}
