package trash.jforex.examples.strategy.chart_objects;

import java.awt.Color;
import javax.swing.SwingConstants;
import com.dukascopy.api.*;
import com.dukascopy.api.drawings.IOhlcChartObject;
 
/**
 * The strategy on its start plots an OHLC object on chart 
 * and on every tick updates the customized user info on it.
 * On strategy stop the object gets removed
 *
 */
public class TestOHLC2 implements IStrategy {
    public IChart chart;
 
    private static int count = 0;
    IOhlcChartObject ohlc;
 
	public void onStart(IContext context) throws JFException {
        chart = context.getChart(Instrument.EURUSD);
 
        ohlc =  chart.getChartObjectFactory().createOhlcInformer();
        ohlc.getParamVisibility(IOhlcChartObject.CandleInfoParams.TIME);
 
        chart.addToMainChartUnlocked(ohlc);
	}
	
	public void onTick(Instrument instrument, ITick tick) throws JFException {
        ohlc.clearUserMessages();
        ohlc.addUserMessage("Ticks count: " + count, Color.ORANGE, SwingConstants.LEFT, true);
        ohlc.addUserMessage("Ticks count: " + count, Color.ORANGE, SwingConstants.CENTER, false);
        ohlc.addUserMessage("Ticks count: " + count, Color.ORANGE, SwingConstants.RIGHT, false);
        ohlc.addUserMessage("Ticks ", ""+count, Color.RED);
        count++;
	}
	
	public void onAccount(IAccount account) throws JFException {}
 
	public void onMessage(IMessage message) throws JFException {}
 
	public void onStop() throws JFException {
        chart.remove(ohlc);
	}
	
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {    }
}