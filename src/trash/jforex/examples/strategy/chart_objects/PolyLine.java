package trash.jforex.examples.strategy.chart_objects;

import java.util.ArrayList;
import java.util.List;

import com.dukascopy.api.*;
import com.dukascopy.api.drawings.*;

/**
 * The strategy on every 10 second bar draws a zigzag style poly line between the tick bid/ask prices 
 * of the the previous 10 sec bar. The strategy maintains the last three poly lines.
 *
 */

public class PolyLine implements IStrategy {
	private IConsole console;
	private IHistory history;
	private IChart chart;
	
	public Instrument instrument = Instrument.EURUSD;
	public Period selectedPeriod = Period.TEN_SECS;
	public Filter indicatorFilter = Filter.NO_FILTER;
	
	private List<IPolyLineChartObject> polyLines = new ArrayList<IPolyLineChartObject>();

	public void onStart(IContext context) throws JFException {
		this.console = context.getConsole();
		this.history = context.getHistory();
		this.chart = context.getChart(instrument);
	}
	
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
		if (!instrument.equals(this.instrument) || !period.equals(this.selectedPeriod))
			return;

		drawPoly(bidBar.getTime());
	}
	private void drawPoly(long time) throws JFException{

        List<ITick> ticks = history.getTicks(instrument, time, time + 10000);
        IPolyLineChartObject polyLine = chart.getChartObjectFactory().createPolyLine();
        print("add poly line with key: " + polyLine.getKey() + ", tick count: " + ticks.size());
        
        boolean isAsk = false;
        for(ITick t : ticks){
        	//make a zigzag
        	polyLine.addNewPoint(t.getTime(), isAsk ? t.getAsk() : t.getBid());
        	isAsk = isAsk ? false : true;
        }
        chart.addToMainChart(polyLine);
        
        //maintain 3 last poly lines
        polyLines.add(polyLine);
        if(polyLines.size() > 3){
        	chart.remove(polyLines.get(0));
        	polyLines.remove(0);
        }
        
	}

	private void print(String message) {
		console.getOut().println(message);
	}
	
	public void onAccount(IAccount account) throws JFException {	}
	public void onMessage(IMessage message) throws JFException {	}
	public void onStop() throws JFException {	}
	public void onTick(Instrument instrument, ITick tick) throws JFException {	}
}
