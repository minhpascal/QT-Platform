package trash.jforex.examples.strategy.extend_user_interface;

import javax.swing.*;
import com.dukascopy.api.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The strategy adds a new tab in JForex Client which contains a tick counter
 * which can be reset by pressing a button
 */
public class TickCountingTabSimple implements IStrategy {
    private IUserInterface userInterface;
    private JLabel labelTickCount;
    
    private int tickCount;
    private int resets;
    private final String tabName = "TickCounter";
    
    public void onStart(IContext context) throws JFException {
        this.userInterface = context.getUserInterface();
        
        //add a bottom tab and add to it a label and a reset button
        JPanel myTab = userInterface.getBottomTab(tabName);
        labelTickCount = new JLabel();
        final JButton btn = new JButton("Reset");

        myTab.add(labelTickCount);
        myTab.add(btn);
        
        //"Reset" click resets the tick counter and changes the label's color
        btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tickCount = 0;
				labelTickCount.setForeground((resets % 2 == 0 ? Color.RED : Color.BLUE));
				btn.setText("Reset (" + ++resets +")");
			}
        }); 
    }
    
    public void onTick(Instrument instrument, ITick tick) throws JFException {
    	if(labelTickCount != null)
    		labelTickCount.setText("ticks since reset: " + tickCount++);     
    }

    public void onStop() throws JFException {
        userInterface.removeBottomTab(tabName);
    }
    
    public void onAccount(IAccount account) throws JFException {    }
    public void onMessage(IMessage message) throws JFException {    }    
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {    }
}