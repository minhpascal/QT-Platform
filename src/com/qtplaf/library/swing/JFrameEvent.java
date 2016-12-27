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

package com.qtplaf.library.swing;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.qtplaf.library.app.Session;

/**
 * Base frame class that handles key, mouse, window focus and window events. Extenders only need to overwrite the
 * appropriate listener methods to handle those events. Normally implementors will overwrite the
 * <code>windowClosing</code> method.
 * 
 * @author Miquel Sas
 */
public class JFrameEvent extends JFrame implements KeyListener, MouseListener, WindowFocusListener, WindowListener {

	/**
	 * Listener implementor to avoid errors like not calling the super method when overwriting listener methods,
	 * specially when some high level task is required. Listener methods must do their tasks and finally call the parent
	 * method.
	 * 
	 * @author Miquel Sas
	 */
	private final class Listener implements KeyListener, MouseListener, WindowFocusListener, WindowListener {
		/**
		 * Invoked the first time a window is made visible.
		 */
		@Override
		public void windowOpened(WindowEvent e) {
			JFrameEvent.this.windowOpened(e);
		}

		/**
		 * Invoked when the user attempts to close the window from the window's system menu.
		 */
		@Override
		public void windowClosing(WindowEvent e) {
			JFrameEvent.this.windowClosing(e);
		}

		/**
		 * Invoked when a window has been closed as the result of calling dispose on the window.
		 */
		@Override
		public void windowClosed(WindowEvent e) {
			JFrameEvent.this.windowClosed(e);
		}

		/**
		 * Invoked when a window is changed from a normal to a minimized state. For many platforms, a minimized window
		 * is displayed as the icon specified in the window's iconImage property.
		 * 
		 * @see java.awt.Frame#setIconImage
		 */
		@Override
		public void windowIconified(WindowEvent e) {
			JFrameEvent.this.windowIconified(e);
		}

		/**
		 * Invoked when a window is changed from a minimized to a normal state.
		 */
		@Override
		public void windowDeiconified(WindowEvent e) {
			JFrameEvent.this.windowDeiconified(e);
		}

		/**
		 * Invoked when the Window is set to be the active Window. Only a Frame or a Dialog can be the active Window.
		 * The native windowing system may denote the active Window or its children with special decorations, such as a
		 * highlighted title bar. The active Window is always either the focused Window, or the first Frame or Dialog
		 * that is an owner of the focused Window.
		 */
		@Override
		public void windowActivated(WindowEvent e) {
			JFrameEvent.this.windowActivated(e);
		}

		/**
		 * Invoked when a Window is no longer the active Window. Only a Frame or a Dialog can be the active Window. The
		 * native windowing system may denote the active Window or its children with special decorations, such as a
		 * highlighted title bar. The active Window is always either the focused Window, or the first Frame or Dialog
		 * that is an owner of the focused Window.
		 */
		@Override
		public void windowDeactivated(WindowEvent e) {
			JFrameEvent.this.windowDeactivated(e);
		}

		/**
		 * Invoked when the Window is set to be the focused Window, which means that the Window, or one of its
		 * subcomponents, will receive keyboard events.
		 */
		@Override
		public void windowGainedFocus(WindowEvent e) {
			JFrameEvent.this.windowGainedFocus(e);
		}

		/**
		 * Invoked when the Window is no longer the focused Window, which means that keyboard events will no longer be
		 * delivered to the Window or any of its subcomponents.
		 */
		@Override
		public void windowLostFocus(WindowEvent e) {
			JFrameEvent.this.windowLostFocus(e);
		}

		/**
		 * Invoked when the mouse button has been clicked (pressed and released) on a component.
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			JFrameEvent.this.mouseClicked(e);
		}

		/**
		 * Invoked when a mouse button has been pressed on a component.
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			JFrameEvent.this.mousePressed(e);
		}

		/**
		 * Invoked when a mouse button has been released on a component.
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			JFrameEvent.this.mouseReleased(e);
		}

		/**
		 * Invoked when the mouse enters a component.
		 */
		@Override
		public void mouseEntered(MouseEvent e) {
			JFrameEvent.this.mouseEntered(e);
		}

		/**
		 * Invoked when the mouse exits a component.
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			JFrameEvent.this.mouseExited(e);
		}

		/**
		 * Invoked when a key has been typed. See the class description for {@link KeyEvent} for a definition of a key
		 * typed event.
		 */
		@Override
		public void keyTyped(KeyEvent e) {
			JFrameEvent.this.keyTyped(e);
		}

		/**
		 * Invoked when a key has been pressed. See the class description for {@link KeyEvent} for a definition of a key
		 * pressed event.
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			JFrameEvent.this.keyPressed(e);
		}

		/**
		 * Invoked when a key has been released. See the class description for {@link KeyEvent} for a definition of a
		 * key released event.
		 */
		@Override
		public void keyReleased(KeyEvent e) {
			JFrameEvent.this.keyReleased(e);
		}
	}

	/**
	 * The working session.
	 */
	private Session session;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public JFrameEvent(Session session) {
		super();
		this.session = session;
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setListeners();
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Sets the listeners to this window.
	 */
	private void setListeners() {
		Listener listener = new Listener();
		addWindowFocusListener(listener);
		addWindowListener(listener);
		addKeyListener(listener);
		addMouseListener(listener);
	}

	/**
	 * Sets the size based on factor of the screen size.
	 * 
	 * @param widthFactor The width factor relative to the screen.
	 * @param heightFactor The height factor relative to the screen.
	 */
	public void setSize(double widthFactor, double heightFactor) {
		setSize(SwingUtils.factorScreenDimension(this, widthFactor, heightFactor));
	}

	/**
	 * Moves this dialog to the desired location.
	 * 
	 * @param widthFactor
	 * @param heightFactor
	 */
	public void setLocation(double widthFactor, double heightFactor) {
		setLocation(SwingUtils.moveWindowOnScreen(this, widthFactor, heightFactor));
	}

	/**
	 * Invoked the first time a window is made visible.
	 */
	@Override
	public void windowOpened(WindowEvent e) {
	}

	/**
	 * Invoked when the user attempts to close the window from the window's system menu.
	 */
	@Override
	public void windowClosing(WindowEvent e) {
	}

	/**
	 * Invoked when a window has been closed as the result of calling dispose on the window.
	 */
	@Override
	public void windowClosed(WindowEvent e) {
	}

	/**
	 * Invoked when a window is changed from a normal to a minimized state. For many platforms, a minimized window is
	 * displayed as the icon specified in the window's iconImage property.
	 * 
	 * @see java.awt.Frame#setIconImage
	 */
	@Override
	public void windowIconified(WindowEvent e) {
	}

	/**
	 * Invoked when a window is changed from a minimized to a normal state.
	 */
	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	/**
	 * Invoked when the Window is set to be the active Window. Only a Frame or a Dialog can be the active Window. The
	 * native windowing system may denote the active Window or its children with special decorations, such as a
	 * highlighted title bar. The active Window is always either the focused Window, or the first Frame or Dialog that
	 * is an owner of the focused Window.
	 */
	@Override
	public void windowActivated(WindowEvent e) {
	}

	/**
	 * Invoked when a Window is no longer the active Window. Only a Frame or a Dialog can be the active Window. The
	 * native windowing system may denote the active Window or its children with special decorations, such as a
	 * highlighted title bar. The active Window is always either the focused Window, or the first Frame or Dialog that
	 * is an owner of the focused Window.
	 */
	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	/**
	 * Invoked when the Window is set to be the focused Window, which means that the Window, or one of its
	 * subcomponents, will receive keyboard events.
	 */
	@Override
	public void windowGainedFocus(WindowEvent e) {
	}

	/**
	 * Invoked when the Window is no longer the focused Window, which means that keyboard events will no longer be
	 * delivered to the Window or any of its subcomponents.
	 */
	@Override
	public void windowLostFocus(WindowEvent e) {
	}

	/**
	 * Invoked when the mouse button has been clicked (pressed and released) on a component.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * Invoked when a mouse button has been pressed on a component.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
	}

	/**
	 * Invoked when a mouse button has been released on a component.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * Invoked when the mouse enters a component.
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Invoked when the mouse exits a component.
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Invoked when a key has been typed. See the class description for {@link KeyEvent} for a definition of a key typed
	 * event.
	 */
	@Override
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * Invoked when a key has been pressed. See the class description for {@link KeyEvent} for a definition of a key
	 * pressed event.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
	}

	/**
	 * Invoked when a key has been released. See the class description for {@link KeyEvent} for a definition of a key
	 * released event.
	 */
	@Override
	public void keyReleased(KeyEvent e) {
	}
}
