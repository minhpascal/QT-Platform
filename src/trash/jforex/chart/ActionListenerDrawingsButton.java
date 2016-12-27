package trash.jforex.chart;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import com.qtplaf.library.util.ImageIconUtils;

/**
 * The action listener for the drawings button.
 * 
 * @author Miquel Sas
 */
class ActionListenerDrawingsButton implements ActionListener {
	/** The chart manager. */
	private ChartManager chartManager;

	/**
	 * Constructor assigning the chart manager.
	 * 
	 * @param chartManager The chart manager.
	 */
	ActionListenerDrawingsButton(ChartManager chartManager) {
		this.chartManager = chartManager;
	}

	/**
	 * Responds on action performed.
	 */
	public void actionPerformed(ActionEvent e) {

		try {
			// Create the pop up menu
			JPopupMenu popupMenu = new JPopupMenu();

			ActionListenerDrawingsMenuItems listenerItems = new ActionListenerDrawingsMenuItems(chartManager);

			// Short line menu item
			JMenuItem menuItemSortLine = new JMenuItem("Short line", 
				ImageIconUtils.getImageIcon("images/png/drawing_line_short_active.png"));
			menuItemSortLine.setName("ShortLine");
			menuItemSortLine.addActionListener(listenerItems);
			popupMenu.add(menuItemSortLine);

			// Long line menu item
			JMenuItem menuItemLongLine = new JMenuItem("Long line", 
				ImageIconUtils.getImageIcon("images/png/drawing_line_long_active.png"));
			menuItemLongLine.setName("LongLine");
			menuItemLongLine.addActionListener(listenerItems);
			popupMenu.add(menuItemLongLine);

			// Poly line menu item
			JMenuItem menuItemPolyLine = new JMenuItem("Poly line", 
				ImageIconUtils.getImageIcon("images/png/drawing_line_poly_active.png"));
			menuItemPolyLine.setName("PolyLine");
			menuItemPolyLine.addActionListener(listenerItems);
			popupMenu.add(menuItemPolyLine);

			// Ray line menu item
			JMenuItem menuItemRayLine = new JMenuItem("Ray line", 
				ImageIconUtils.getImageIcon("images/png/drawing_line_poly_active.png"));
			menuItemRayLine.setName("RayLine");
			menuItemRayLine.addActionListener(listenerItems);
			popupMenu.add(menuItemRayLine);

			// Separator
			popupMenu.add(new JSeparator());

			// Horizontal line menu item
			JMenuItem menuItemHorizontalLine =
				new JMenuItem("Horizontal line", 
					ImageIconUtils.getImageIcon("images/png/drawing_line_horizontal_active.png"));
			menuItemHorizontalLine.setName("HorizontalLine");
			menuItemHorizontalLine.addActionListener(listenerItems);
			popupMenu.add(menuItemHorizontalLine);

			// Vertical line menu item
			JMenuItem menuItemVerticalLine =
				new JMenuItem("Vertical line", 
					ImageIconUtils.getImageIcon("images/png/drawing_line_vertical_active.png"));
			menuItemVerticalLine.setName("VerticalLine");
			menuItemVerticalLine.addActionListener(listenerItems);
			popupMenu.add(menuItemVerticalLine);

			// Separator
			popupMenu.add(new JSeparator());

			// Rectangle menu item
			JMenuItem menuItemRectangle = new JMenuItem("Rectangle", 
				ImageIconUtils.getImageIcon("images/png/drawing_rectangle_active.png"));
			menuItemRectangle.setName("Rectangle");
			menuItemRectangle.addActionListener(listenerItems);
			popupMenu.add(menuItemRectangle);

			// Ellipse menu item
			JMenuItem menuItemEllipse = new JMenuItem("Ellipse", 
				ImageIconUtils.getImageIcon("images/png/drawing_ellipse_active.png"));
			menuItemEllipse.setName("Ellipse");
			menuItemEllipse.addActionListener(listenerItems);
			popupMenu.add(menuItemEllipse);

			// Triangle menu item
			JMenuItem menuItemTriangle = new JMenuItem("Triangle", 
				ImageIconUtils.getImageIcon("images/png/drawing_triangle_active.png"));
			menuItemTriangle.setName("Triangle");
			menuItemTriangle.addActionListener(listenerItems);
			popupMenu.add(menuItemTriangle);

			// Separator
			popupMenu.add(new JSeparator());

			// Fibonacci retracements menu item
			JMenuItem menuItemFiboRetracements = new JMenuItem("Fibonacci retracements");
			menuItemFiboRetracements.setName("FiboRetracements");
			menuItemFiboRetracements.addActionListener(listenerItems);
			popupMenu.add(menuItemFiboRetracements);

			// Fibonacci expansion menu item
			JMenuItem menuItemFiboExpansion = new JMenuItem("Fibonacci expansion");
			menuItemFiboExpansion.setName("FiboExpansion");
			menuItemFiboExpansion.addActionListener(listenerItems);
			popupMenu.add(menuItemFiboExpansion);

			// Show the pop up menu
			Component component = (Component) e.getSource();
			popupMenu.show(component, 0, component.getSize().height);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

}