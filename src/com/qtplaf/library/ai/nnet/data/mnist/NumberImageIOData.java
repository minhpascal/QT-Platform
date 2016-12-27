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
package com.qtplaf.library.ai.nnet.data.mnist;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.math.Vector;

/**
 * MINIST sample image data: a 28*28 byte image matrix and its number (0 to 9)
 * 
 * @author Miquel Sas
 */
public class NumberImageIOData extends IOData {

	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -1772205670034912024L;

	/** Image rows. */
	public static final int ROWS = 28;
	/** Image columns. */
	public static final int COLUMNS = 28;

	/** The byte array of 28*28 = 784 elements. */
	private byte[][] image;

	/** The number. */
	private int number;

	/**
	 * Constructor assigning the number and the bytes.
	 * 
	 * @param number The represented number
	 * @param bytes The raw bytes list
	 */
	public NumberImageIOData(int number, byte[] bytes) {
		super();
		if (number < 0 || number > 9) {
			throw new IllegalArgumentException("Invalid number " + number);
		}
		if (bytes.length != ROWS * COLUMNS) {
			throw new IllegalArgumentException("Invalid number of bytes per image " + bytes.length);
		}
		this.number = number;
		image = new byte[ROWS][COLUMNS];
		int row = 0;
		int column = 0;
		for (int i = 0; i < bytes.length; i++) {
			image[row][column] = bytes[i];
			column++;
			if (column == COLUMNS) {
				column = 0;
				row++;
			}
		}
	}

	/**
	 * Returns the number.
	 * 
	 * @return The number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Returns the image as a two dimension byte array.
	 * 
	 * @return The image
	 */
	public byte[][] getImage() {
		return image;
	}

	/**
	 * Returns the inputs data.
	 * 
	 * @return The inputs
	 */
	public List<Double> getInputs() {
		List<Double> inputs = new ArrayList<>();
		for (int row = 0; row < ROWS; row++) {
			for (int column = 0; column < COLUMNS; column++) {
				double imageByte = 255 - Byte.toUnsignedInt(image[row][column]);
				double imageInput = imageByte / 255;
				inputs.add(imageInput);
			}
		}
		return inputs;
	}

	/**
	 * Returns the input vector.
	 * 
	 * @return The input vector.
	 */
	public Vector getInputVector() {
		double[][] vector = new double[ROWS * COLUMNS][1];
		int index = 0;
		for (int row = 0; row < ROWS; row++) {
			for (int column = 0; column < COLUMNS; column++) {
				double imageByte = 255 - Byte.toUnsignedInt(image[row][column]);
				double imageInput = imageByte / 255;
				vector[index][0] = imageInput;
				index++;
			}
		}
		return new Vector(vector);
	}

	/**
	 * Set the inputs data.
	 * 
	 * @param inputs The inputs
	 */
	public void setInputs(List<Double> inputs) {
		throw new UnsupportedOperationException("Not applicable in this class");
	}

	/**
	 * Returns the outputs data.
	 * 
	 * @return the outputs
	 */
	public List<Double> getOutputs() {
		List<Double> outputs = new ArrayList<>();
		for (int i = 0; i < number; i++) {
			outputs.add(0.0);
		}
		outputs.add(1.0);
		for (int i = number + 1; i < 10; i++) {
			outputs.add(0.0);
		}
		return outputs;
	}

	/**
	 * Returns the output vector.
	 * 
	 * @return The output vector.
	 */
	public Vector getOutputVector() {
		double[][] vector = new double[10][1];
		for (int i = 0; i < number; i++) {
			vector[i][0] = 0.0;
		}
		vector[number][0] = 1.0;
		for (int i = number + 1; i < 10; i++) {
			vector[i][0] = 0.0;
		}
		return new Vector(vector);
	}

	/**
	 * Set the outputs data.
	 * 
	 * @param outputs the outputs
	 */
	public void setOutputs(List<Double> outputs) {
		throw new UnsupportedOperationException("Not applicable in this class");
	}
}
