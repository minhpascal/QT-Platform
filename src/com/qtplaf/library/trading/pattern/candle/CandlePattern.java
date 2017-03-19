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

package com.qtplaf.library.trading.pattern.candle;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.ai.fuzzy.Control;
import com.qtplaf.library.ai.fuzzy.Segment;
import com.qtplaf.library.ai.fuzzy.function.Linear;
import com.qtplaf.library.trading.pattern.Pattern;

/**
 * Root class of candlestick patterns. To correctly calculate proportions and ranges, it receives the average range
 * (hig-low) and its standard deviation.
 *
 * @author Miquel Sas
 */
public abstract class CandlePattern extends Pattern {

	/**
	 * Enum the proportional sizes.
	 */
	public interface Size {
		String VeryBig = "very_big";
		String Big = "big";
		String Medium = "medium";
		String Small = "small";
		String VerySmall = "very_small";
	}
	
	/**
	 * Enum proportional positions.
	 */
	public interface Position {
		String Top = "top";
		String NearTop = "near_top";
		String Middle = "middle";
		String NearBottom = "near_bottom";
		String Bottom = "bottom";
	}

	/** Average range statistically calculated for the time frame. */
	private double rangeAverage;
	/** Range standard deviation also statistically calculated for the time frame. */
	private double rangeStdDev;
	/** The fuzzy size control to check sizes in a range of 0 to 1. */
	private Control sizeControl;
	/** The fuzzy position control to check positions in a range of 0 to 1. */
	private Control positionControl;

	/**
	 * Contructor.
	 */
	public CandlePattern() {
		super();
	}

	/**
	 * Returns the maximum range for calculations, a rangeAverage + (2 * rangeStdDev)
	 * 
	 * @return The maximum range for calculations.
	 */
	public double getMaximumRange() {
		return rangeAverage + (2 * rangeStdDev);
	}

	/**
	 * Returns the fuzzy control to check positions in a range of 0 to 1, normally used with factors like range, body,
	 * shadow factors.
	 * 
	 * @return The fuzzy control.
	 */
	public Control getPositionControl() {
		if (positionControl == null) {
			List<Segment> segments = new ArrayList<>();
			segments.add(new Segment(Position.Bottom, 0.15, 0.00, -1, new Linear()));
			segments.add(new Segment(Position.NearBottom, 0.35, Math.nextUp(0.15), -1, new Linear()));
			segments.add(new Segment(Position.Middle, 0.65, Math.nextUp(0.35), 0, new Linear()));
			segments.add(new Segment(Position.NearTop, 0.85, Math.nextUp(0.65), 1, new Linear()));
			segments.add(new Segment(Position.Top, 1.00, Math.nextUp(0.85), 1, new Linear()));
			positionControl = new Control(segments);
		}
		return positionControl;
	}

	/**
	 * Returns the fuzzy control to check sizes in a range of 0 to 1, normally used with factors like range, body,
	 * shadow factors.
	 * 
	 * @return The fuzzy control.
	 */
	public Control getSizeControl() {
		if (sizeControl == null) {
			List<Segment> segments = new ArrayList<>();
			segments.add(new Segment(Size.VerySmall, 0.10, 0.00, -1, new Linear()));
			segments.add(new Segment(Size.Small, 0.35, Math.nextUp(0.10), -1, new Linear()));
			segments.add(new Segment(Size.Medium, 0.65, Math.nextUp(0.35), 0, new Linear()));
			segments.add(new Segment(Size.Big, 0.90, Math.nextUp(0.65), 1, new Linear()));
			segments.add(new Segment(Size.VeryBig, 1.00, Math.nextUp(0.90), 1, new Linear()));
			sizeControl = new Control(segments);
		}
		return sizeControl;
	}

	/**
	 * Returns the average range.
	 * 
	 * @return The average range.
	 */
	public double getRangeAverage() {
		return rangeAverage;
	}

	/**
	 * Set the average range.
	 * 
	 * @param rangeAverage The average range.
	 */
	public void setRangeAverage(double rangeAverage) {
		this.rangeAverage = rangeAverage;
	}

	/**
	 * Returns the range standard deviation.
	 * 
	 * @return The range standard deviation.
	 */
	public double getRangeStdDev() {
		return rangeStdDev;
	}

	/**
	 * Set the range standard deviation.
	 * 
	 * @param rangeStdDev The range standard deviation.
	 */
	public void setRangeStdDev(double rangeStdDev) {
		this.rangeStdDev = rangeStdDev;
	}
}
