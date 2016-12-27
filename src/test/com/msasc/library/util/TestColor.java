package test.com.msasc.library.util;

import java.awt.Color;

import javax.swing.JColorChooser;

public class TestColor {

	public static void main(String[] args) {
		Color color = JColorChooser.showDialog(null, "Choose color", Color.RED);
		System.out.println(color);
	}

}
