package trash.jforex.examples.strategy.chart_objects;

import java.awt.Color;
import java.awt.Font;

import com.dukascopy.api.*;
import com.dukascopy.api.drawings.*;
import com.dukascopy.api.drawings.IScreenLabelChartObject.Corner;

/**
 * The strategy draws a screen label on all charts.
 * On strategy stop the strategy removes all screen labels from all charts
 * 
 */
public class DrawOnAllCharts implements IStrategy {

    private IHistory history;
    private IContext context;

    private static final String tickLabelPrefix = "tickLabel";    

    @Override
    public void onStart(IContext context) throws JFException {
        history = context.getHistory();
        this.context = context;
        int count = 0;
        //only charts for subscribed instruments can be opened
        for (Instrument instrument : context.getSubscribedInstruments()) {
        	//there can be multiple charts opened for the same instrument
            for (IChart chart : context.getCharts(instrument)) {
                ITick tick = history.getLastTick(instrument);
                IScreenLabelChartObject label = chart.getChartObjectFactory().createScreenLabel(tickLabelPrefix + count++);
                label.setCorner(Corner.TOP_RIGHT);
                label.setxDistance(50);
                label.setyDistance(50);
                label.setText("Label added on tick: " + tick, new Font(Font.SANS_SERIF, Font.BOLD, 12));
                label.setColor(Color.RED.darker());
                chart.addToMainChart(label);
            }
        }

    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }

    @Override
    public void onMessage(IMessage message) throws JFException {
    }

    @Override
    public void onAccount(IAccount account) throws JFException {
    }

    @Override
    public void onStop() throws JFException {
    	//find on all charts objects with the particular prefix and remove them
        for (Instrument instrument : context.getSubscribedInstruments()) {
            for (IChart chart : context.getCharts(instrument)) {
                for( IChartObject obj : chart.getAll())
                    if (obj.getKey().startsWith(tickLabelPrefix)) {
                        chart.remove(obj);
                    }
            }
        }
    }

}
