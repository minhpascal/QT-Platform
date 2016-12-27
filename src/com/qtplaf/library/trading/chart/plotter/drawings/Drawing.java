/**
 * 
 */
package com.qtplaf.library.trading.chart.plotter.drawings;

import java.awt.Shape;

import com.qtplaf.library.trading.chart.plotter.Plotter;

/**
 * Base class of all drawings. As a general rule a drawing should store the variables that define it, like indexes and
 * values, and return the shapes using a <i>Plotter</i> the retrieve their points coordinates.
 * 
 * @author Miquel Sas
 */
public abstract class Drawing {

	/**
	 * Default constructor.
	 */
	public Drawing() {
	}

	/**
	 * Returns this drawing shape.
	 * 
	 * @param plotter The plotter.
	 * @return The shape.
	 */
	public abstract Shape getShape(Plotter plotter);
}
