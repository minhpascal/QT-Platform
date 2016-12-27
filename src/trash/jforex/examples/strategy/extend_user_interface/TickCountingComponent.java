package trash.jforex.examples.strategy.extend_user_interface;

import javax.swing.*;

import com.dukascopy.api.*;
import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The strategy creates a customized GUI component in JForex client 
 * which contains a tick counter that can be reset by pressing a button. 
 * User can choose either to show the element either in a new bottom tab
 * or a new window. The component gets closed on strategy stop.
 */
public class TickCountingComponent implements IStrategy {
	private IUserInterface userInterface;
	private JTextField labelTickCount;

	@Configurable("Use tab")
	public boolean useTab = false;

	private int tickCount;
	private int resets;
	private final String tabName = "TickCounter";
	private Container container;

	public void onStart(IContext context) throws JFException {
		this.userInterface = context.getUserInterface();

		//build either a new tab or a new window
		container = useTab ? buildBottomTab() : buildWindow();

	}

	private void addComponentsToPane(Container pane) {

		pane.setLayout(null);
		
		labelTickCount = new JTextField();
		final JButton btn = new JButton("Reset");

		pane.add(labelTickCount);
		pane.add(btn);

		// "Reset" click resets the tick counter and changes the label's color
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tickCount = 0;
				labelTickCount.setForeground((resets % 2 == 0 ? Color.RED : Color.BLUE));
				btn.setText("Reset (" + ++resets + ")");
			}
		});

		
		//customized size and placement of the label and the button
		Insets insets = pane.getInsets();
		btn.setBounds(25 + insets.left, 5 + insets.top, 100, 25);
		labelTickCount.setBounds(150 + insets.left, 15 + insets.top, 150, 25);

	}

	private JFrame buildWindow() {
		// Create and set up the window.
		JFrame frame = new JFrame(tabName + " window with absolute layout");

		addComponentsToPane(frame);

		// Size and display the window.
		Insets insets = frame.getInsets();
		frame.setSize(400 + insets.left + insets.right, 150 + insets.top + insets.bottom);
		frame.setVisible(true);
		return frame;
	}

	private JPanel buildBottomTab() {
		JPanel tab = userInterface.getBottomTab(tabName);
		
		addComponentsToPane(tab);
		return tab;
	}

	public void onTick(Instrument instrument, ITick tick) throws JFException {
		if (labelTickCount != null)
			labelTickCount.setText("ticks since reset: " + tickCount++);
	}

	public void onStop() throws JFException {
		//remove tab or dispose the window
		if (useTab){
			userInterface.removeBottomTab(tabName);
		} else {
			((JFrame)container).dispose();
		}		
	}

	public void onAccount(IAccount account) throws JFException {
	}

	public void onMessage(IMessage message) throws JFException {
	}

	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
	}
}