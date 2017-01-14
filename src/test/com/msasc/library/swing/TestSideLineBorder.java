package test.com.msasc.library.swing;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.qtplaf.library.swing.core.LineBorderSides;
import com.qtplaf.library.swing.core.SwingUtils;

public class TestSideLineBorder {

	public static void main(String[] args) {
		JFrame frame = new JFrame("Test sided line border");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBorder(new LineBorderSides(Color.BLACK, 5, true, true, true, true));
		
		frame.getContentPane().add(panel);
		frame.pack();
		SwingUtils.setSizeAndCenterOnSreen(frame, 0.8, 0.8);
		frame.setVisible(true);
	}

}
