/**
 * 
 */
package trash.jforex.chart;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTabbedPane;

/**
 * Static functions used in this chartt package.
 * 
 * @author Miquel Sas
 */
public class ChartUtilities {
	/**
	 * Returns the first parent tab pane, usefull in listeners.
	 * @param e The event object
	 * @return The tab pane or null
	 */
	public static  JTabbedPane getParentTabbedPane(EventObject e) {
		JTabbedPane tabbedPane = null;
		Component component = (Component) e.getSource();
		while (component != null) {
			if (component instanceof JTabbedPane) {
				tabbedPane = (JTabbedPane) component;
				break;
			}
			component = component.getParent();
		}
		return tabbedPane;
	}
}
