package trash.jforex.examples.strategy.chart_objects;

import java.util.ArrayList;
import java.util.List;

import com.dukascopy.api.*;
import com.dukascopy.api.drawings.IHorizontalLineChartObject;

/**
 * The strategy by default draws 10 level lines with 2 pip step starting from
 * the last ask price and then prints their keys and prices. 
 * The level lines get removed on strategy stop.
 *
 */

@RequiresFullAccess
public class LevelLines implements IStrategy {

	private IChart chart;
	private IHistory history;
	private IConsole console;
	
	@Configurable("")
	public Instrument instrument = Instrument.EURUSD;
	@Configurable("HLine count")
	public int hlineCount = 10;
	@Configurable("HLine step (in pips)")
	public int stepInPips = 2;
	
	List<IHorizontalLineChartObject> hLines = new ArrayList<IHorizontalLineChartObject>();	
	
	@Override
	public void onStart(IContext context) throws JFException {
		this.chart = context.getChart(instrument);
		this.history = context.getHistory();
		this.console = context.getConsole();
		
		ITick tick = history.getLastTick(instrument);
		double basePrice = tick.getBid(); //base price - the last bid price
		
		//draw HLines
		double price;
		for(int i=0; i< hlineCount; i++){
			price = basePrice + instrument.getPipValue() * stepInPips * i;
			IHorizontalLineChartObject hLine = chart.getChartObjectFactory().createHorizontalLine();
			hLine.setPrice(0, price);
			hLine.setText(String.valueOf(i));
			chart.addToMainChartUnlocked(hLine);
			hLines.add(hLine);
		}	
		
		//log some information
		for(IHorizontalLineChartObject hLine : hLines){
			console.getOut().println(String.format("hline %s: key=%s, price=%.5f",hLine.getText(), hLine.getKey(), hLine.getPrice(0)));
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
	public void onStop() throws JFException {
		chart.removeAll();
	}

}
