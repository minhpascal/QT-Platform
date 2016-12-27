/**
 * 
 */
package com.qtplaf.library.trading.chart.plotter.drawings;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import com.qtplaf.library.trading.chart.plotter.Plotter;

/**
 * A cross cursor drawing, used to plot the cross cursor.
 * 
 * @author Miquel Sas
 */
public class CrossCursor extends Drawing {

	/**
	 * The cursor point.
	 */
	private Point point;
	/**
	 * The width of the cursor. If the width is -1, then the width of the plotter will be used.
	 */
	private int width = 50;
	/**
	 * The height of the cursor. If the height is -1, then the height of the plotter will be used.
	 */
	private int height = 50;
	/**
	 * The stroke.
	 */
	private Stroke stroke =
		new BasicStroke(0.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 3.0f, new float[] { 3.0f }, 0.0f);
	/**
	 * The color.
	 */
	private Color color = Color.GRAY;
	/**
	 * A circle radius, less or equals to zero paints no circle.
	 */
	private int radius = -1;

	/**
	 * Constructor assigning the point, width default values for width, heigh, stroke, color and circle radius.
	 * 
	 * @param point The cursor point.
	 */
	public CrossCursor(Point point) {
		this.point = point;
	}

	/**
	 * Returns the cursor point.
	 * 
	 * @return The cursor point.
	 */
	public Point getPoint() {
		return point;
	}

	/**
	 * Sets the cursor point.
	 * 
	 * @param point The cursor point.
	 */
	public void setPoint(Point point) {
		this.point = point;
	}

	/**
	 * Returns the width.
	 * 
	 * @return The width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the width.
	 * 
	 * @param width The width.
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * returns the height.
	 * 
	 * @return The height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the height.
	 * 
	 * @param height The height.
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Returns the stroke.
	 * 
	 * @return The stroke.
	 */
	public Stroke getStroke() {
		return stroke;
	}

	/**
	 * Sets the stroke.
	 * 
	 * @param stroke The stroke.
	 */
	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}

	/**
	 * Returns the color.
	 * 
	 * @return The color.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the color.
	 * 
	 * @param color The color.
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Returns the circle radius.
	 * 
	 * @return The radius.
	 */
	public int getRadius() {
		return radius;
	}

	/**
	 * sets the circle radius.
	 * 
	 * @param radius The circle radius.
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}

	/**
	 * Returns this drawing shape.
	 * 
	 * @param plotter The plotter.
	 * @return The shape.
	 */
	public Shape getShape(Plotter plotter) {

		int x = point.x;
		int y = point.y;
		int width = (this.width < 0 ? plotter.getChartSize().width : this.width);
		int height = (this.height < 0 ? plotter.getChartSize().height : this.height);

		GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 2);
		boolean horizontalLine = false;
		if (y >= 0) {
			if (this.width < 0) {
				shape.moveTo(0, y);
				shape.lineTo(width, y);
				horizontalLine = true;
			} else if (this.width >= 2) {
				shape.moveTo(x - (width / 2), y);
				shape.lineTo(x + (width / 2), y);
				horizontalLine = true;
			}
		}
		boolean verticalLine = false;
		if (this.height < 0) {
			shape.moveTo(x, 0);
			shape.lineTo(x, height);
			verticalLine = true;
		} else if (this.height >= 2) {
			shape.moveTo(x, y - (height / 2));
			shape.lineTo(x, y + (height / 2));
			verticalLine = true;
		}

		if (horizontalLine && verticalLine && radius > 0) {
			Ellipse2D circle = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
			shape.append(circle, false);
		}
		return shape;
	}
}
