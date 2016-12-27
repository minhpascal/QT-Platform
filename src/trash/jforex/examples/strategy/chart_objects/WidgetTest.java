package trash.jforex.examples.strategy.chart_objects;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.drawings.ICustomWidgetChartObject;

public class WidgetTest implements IStrategy {

    @Override
    public void onStart(final IContext context) throws JFException {
        final Instrument instrument = Instrument.EURUSD;
        final IChart chart = context.getChart(instrument);
        if (chart == null) {
        	context.getConsole().getErr().println("No chart opened!");
            context.stop();
        }
        ICustomWidgetChartObject obj = chart.getChartObjectFactory().createChartWidget();
        obj.setText("Price marker adder");
        
        obj.setFillOpacity(0.1f); //use 0f for transparent chart widget
        obj.setColor(Color.GREEN.darker());
        
        JPanel panel = obj.getContentPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        final JLabel label = new JLabel(chart.getAll().size() + " chart objects on chart");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton button = new JButton("Add price marker on last Ask");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    chart.add(chart.getChartObjectFactory().createPriceMarker(
                            "PriceMarker" + System.currentTimeMillis(), 
                            context.getHistory().getLastTick(instrument).getAsk())
                        );
                    label.setText(chart.getAll().size() + " chart objects on chart");
                } catch (JFException e1) {
                    e1.printStackTrace();
                }
            }});
        
        JButton buttonBuy = new JButton("Buy 0.1M");
        buttonBuy.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonBuy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                context.executeTask(new Callable<IOrder>() {
                    @Override
                    public IOrder call() throws Exception {
                        return context.getEngine().submitOrder("order" + System.currentTimeMillis(), instrument, OrderCommand.BUY, 0.1);
                    }
                });
            }
        });
        
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0,5)));
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0,5)));
        panel.add(buttonBuy);
        panel.setSize(new Dimension(250, 100));
        panel.setMinimumSize(new Dimension(250, 100));
        panel.setMaximumSize(new Dimension(350, 120));
        chart.add(obj);

    }

    public void onTick(Instrument instrument, ITick tick) throws JFException {}
    public void onMessage(IMessage message) throws JFException {}
    public void onAccount(IAccount account) throws JFException {}
    public void onStop() throws JFException { }
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}
}
