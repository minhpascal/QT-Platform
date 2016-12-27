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

package test.com.msasc.library.util.file;

import java.util.Locale;

import com.qtplaf.library.util.file.FileUtils;

/**
 * Test file size utilities.
 * 
 * @author Miquel Sas
 */
public class TestFileSize {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(FileUtils.Size.KiloByte.getSize()/FileUtils.Size.Byte.getSize());
		System.out.println(FileUtils.Size.MegaByte.getSize()/FileUtils.Size.KiloByte.getSize());
		System.out.println(FileUtils.Size.GigaByte.getSize()/FileUtils.Size.MegaByte.getSize());
		System.out.println(FileUtils.Size.TeraByte.getSize()/FileUtils.Size.GigaByte.getSize());
		
		System.out.println(FileUtils.getSizeLabel(1200, 1, Locale.UK));
		System.out.println(FileUtils.getSizeLabel(18000000000L, 1, Locale.UK));
	}

}
