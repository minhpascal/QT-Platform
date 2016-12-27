package trash.jforex.examples.strategy.chart_objects;

import com.dukascopy.api.*;
import com.dukascopy.api.drawings.IHorizontalLineChartObject;

/**
 * The strategy plots a horizontal line and after 5 seconds removes it from the chart
 * The strategy demonstrates two approaches:
 * 1) removal by key
 * 2) removal by reference
 *
 */
@RequiresFullAccess
public class HLineKeyRemove implements IStrategy {

	private IChart chart;
	private IHistory history;
	private IConsole console;
	
	@Override
	public void onStart(IContext context) throws JFException {
		this.chart = context.getChart(Instrument.EURUSD);
		this.history = context.getHistory();
		console = context.getConsole();

		plotAndRemoveHLine(false);
		plotAndRemoveHLine(true);
	}
	
	private void plotAndRemoveHLine(boolean byKey) throws JFException{
		ITick tick = history.getLastTick(Instrument.EURUSD);	
		
		String key = "hLineKey";
		IHorizontalLineChartObject hLine = chart.getChartObjectFactory().createHorizontalLine(key, tick.getBid());  
		chart.addToMainChartUnlocked(hLine);
		
		//wait 5 secs and then remove
		console.getOut().println("wait 5 secs and remove object by " + (byKey ? "key" : "reference"));
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace(console.getErr());
		}
		if(byKey){
			chart.remove(key);
		} else {
			chart.remove(hLine);
		}
	}
	

	@Override
	public void onTick(Instrument instrument, ITick tick) throws JFException {	}

	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

	@Override
	public void onMessage(IMessage message) throws JFException {}

	@Override
	public void onAccount(IAccount account) throws JFException {}

	@Override
	public void onStop() throws JFException {}

}
