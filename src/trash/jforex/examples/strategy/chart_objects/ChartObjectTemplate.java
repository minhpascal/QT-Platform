package trash.jforex.examples.strategy.chart_objects;

import java.awt.Color;

import com.dukascopy.api.*;
import com.dukascopy.api.drawings.*;

/**
 * The following strategy is a template to be used with snippets
 * from Chart object catalog
 *
 */
public class ChartObjectTemplate implements IStrategy {

    private IChart chart;
    private IHistory history;
    private IConsole console;
    
    @Configurable("")
    public Instrument instrument = Instrument.EURUSD;

    private OfferSide offerSide;
    private Period period;
    
    @Override
    public void onStart(IContext context) throws JFException {
        history = context.getHistory();
        console = context.getConsole();
        chart = context.getChart(instrument);
        
        if(chart == null){
            console.getErr().println("No chart opened for " + instrument);
            context.stop(); //stop the strategy
        }
        period = chart.getSelectedPeriod();
        offerSide = chart.getSelectedOfferSide();
        
        IChartObjectFactory factory = chart.getChartObjectFactory();     
        IBar bar10 = history.getBar(instrument, period, offerSide, 10);
        IBar bar30 = history.getBar(instrument, period, offerSide, 30);  

        //TODO: replace this with your own code
        IHorizontalLineChartObject hline = factory.createHorizontalLine("hLineKey", bar10.getClose());
        hline.setColor(Color.RED);
        chart.addToMainChart(hline);
        
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

    @Override
    public void onMessage(IMessage message) throws JFException {}

    @Override
    public void onAccount(IAccount account) throws JFException {}

    @Override
    public void onStop() throws JFException {}

}
