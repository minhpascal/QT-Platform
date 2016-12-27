package com.qtplaf.library.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.SystemColor;

import javax.swing.Icon;

/**
 * An icon with an arrow inside.
 * 
 * @author Miquel Sas
 */
public class IconArrow implements Icon {

	/**
	 * Arrow directions.
	 */
	public enum Direction {
		/**
		 * North pointing direction.
		 */
		Up,
		/**
		 * South pointing direction.
		 */
		Down,
		/**
		 * West pointing direction.
		 */
		Left,
		/**
		 * East pointing direction.
		 */
		Right
	}

	private int size = 15;

	private Direction direction = Direction.Down;

	/**
	 * Default constructor.
	 */
	public IconArrow() {
		super();
	}

	/**
	 * Constructs a new arrow button with the direction given.
	 * 
	 * @param direction The direction of the arrow.
	 */
	public IconArrow(Direction direction) {
		super();
		setDirection(direction);
	}

	/**
	 * Set the arrow direction.
	 * 
	 * @param direction The direction of the arrow.
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * Returns the icon's height.
	 *
	 * @return an int specifying the fixed height of the icon.
	 */
	@Override
	public int getIconHeight() {
		return size;
	}

	/**
	 * Returns the icon's width.
	 *
	 * @return an int specifying the fixed width of the icon.
	 */
	@Override
	public int getIconWidth() {
		return size;
	}

	/**
	 * Draw the icon at the specified location. Icon implementations may use the Component argument to get properties
	 * useful for painting, e.g. the foreground or background color.
	 */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {

		boolean enabled = c.isEnabled();
		Color oldColor = g.getColor();

		// X
		int ox = 4;
		int dx = size - ox * 2;
		int minX = ox;
		int maxX = ox + dx - 1;
		int midX = size / 2;
		// Y
		int minY = minX;
		int maxY = maxX;

		g.translate(x, y);
		if (enabled) {
			g.setColor(SystemColor.controlDkShadow);
		} else {
			g.setColor(SystemColor.controlShadow);
		}

		if (g instanceof Graphics2D) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		Polygon p = new Polygon();
		switch (direction) {
		case Up:
			p.addPoint(minX, maxY);
			p.addPoint(maxX, maxY);
			p.addPoint(midX, minY);
			g.fillPolygon(p);
			break;
		case Down:
			// Perceptual correction
			p.addPoint(minX, minY + 1);
			p.addPoint(maxX, minY + 1);
			p.addPoint(midX, maxY + 1);
			g.fillPolygon(p);
			break;
		case Left:
			// Perceptual correction
			p.addPoint(maxY - 1, minX);
			p.addPoint(maxY - 1, maxX);
			p.addPoint(minY - 1, midX);
			g.fillPolygon(p);
			break;
		case Right:
			p.addPoint(minY, minX);
			p.addPoint(minY, maxX);
			p.addPoint(maxY, midX);
			g.fillPolygon(p);
			break;
		}
		g.translate(-x, -y);
		g.setColor(oldColor);
	}
}
