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

package com.qtplaf.library.swing.action;

import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.ActionGroup;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;

/**
 * Default action to be extended to perform the operation.
 * 
 * @author Miquel Sas
 */
public abstract class DefaultActionStop extends AbstractAction {

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public DefaultActionStop(Session session) {
		super();
		ActionUtils.setSourceName(this, session.getString("actionStopName"));
		ActionUtils.setShortDescription(this, session.getString("actionStopName"));
		ActionUtils.setAcceleratorKey(this, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK));
		ActionUtils.setSession(this, session);
		ActionUtils.setActionGroup(this, ActionGroup.Operation);
		ActionUtils.setSmallIcon(this, ImageIconUtils.getImageIcon(Icons.app_16x16_stop));
	}
}
