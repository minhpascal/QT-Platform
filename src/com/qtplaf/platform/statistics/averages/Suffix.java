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

package com.qtplaf.platform.statistics.averages;

/**
 * Suffixes of values.
 *
 * @author Miquel Sas
 */
public interface Suffix {
	String raw = "raw";		// Raw
	String nrm = "nrm";		// Normalize continuous
	String dsc = "dsc";		// Normalize discrete
	String in = "in";		// Input 
	String out = "out";		// Output
	String min = "min";		// Min
	String max = "max";		// Max
}
