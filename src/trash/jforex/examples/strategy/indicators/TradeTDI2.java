package trash.jforex.examples.strategy.indicators;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.IIndicators.AppliedPrice;
import com.dukascopy.api.IIndicators.MaType;
import com.dukascopy.api.indicators.IIndicator;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * The strategy trades according to TradersDynamicIndex.jfx custom indicator.
 * It makes:
 * - a BUY order if green ("RSI Price Line") crosses above red ("Trade Signal Line"), 
 * - a SELL order - if green crosses below red.
 * 
 * On its start the strategy plots the indicator on the chart.
 * 
 * The strategy works only in determined market hours
 * 
 */
@RequiresFullAccess
public class TradeTDI2 implements IStrategy {
	
	private IConsole console;
	private IHistory history;
	private IIndicators indicators;
	private IEngine engine;
	private IIndicator indicator;
	private IChart chart;
	private IOrder order;
	
	@Configurable("TDI .jfx file")
	public File jfxFile = new File("C:\\temp\\TradersDynamicIndex.jfx");
	@Configurable("from time (HH:mm)")
	public String fromTimeStr = "08:00";
	@Configurable("to time (HH:mm)")
	public String toTimeStr = "15:00";
	@Configurable("")
	public Instrument instrument = Instrument.EURUSD;
	@Configurable("")
	public Period period = Period.FIFTEEN_MINS;
	@Configurable("")
	public OfferSide side = OfferSide.BID;
	@Configurable("")
	public AppliedPrice appliedPrice = AppliedPrice.CLOSE;
	@Configurable("")
	public double orderAmount = 0.001;
	@Configurable("SL (pips)")
	public int slPips = 20;
	@Configurable("TP (pips)")
	public int tpPips = 40;
	@Configurable("slippage (pips)")
	public int slippage = 5;

	private String indName = "TDI";
	private int counter;
	
	private Object[] optInputs;
	private int rsiPeriod = 13;
	private int rsiPriceLine = 2;
	private int priceMaType = MaType.SMA.ordinal();
	private int tradeSignalLine = 7;
	private int signalMaType = MaType.SMA.ordinal();
	private int volatilityBand = 34;
	private int volatility = MaType.SMA.ordinal();
	private boolean showBGLines = true;

	private SimpleDateFormat gmtSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Override
	public void onStart(IContext context) throws JFException {
		console = context.getConsole();
		history = context.getHistory();
		indicators = context.getIndicators();
		engine = context.getEngine();
		chart = context.getChart(instrument);

		gmtSdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        indicators.registerCustomIndicator(jfxFile);
		indicator = context.getIndicators().getIndicator(indName);

		optInputs = new Object[] { 
				rsiPeriod, rsiPriceLine, priceMaType, tradeSignalLine, signalMaType, volatilityBand, volatility, showBGLines
		};
		
		chart.addIndicator(indicator, optInputs);		
		
		//plot only if the chart matches the parameters that are used for indicator calculation
		if(chart.getSelectedPeriod() != this.period  ){
			printErr("For proper indicator values please change chart period to "+ this.period);
		}
		if(chart.getSelectedOfferSide() != this.side ){
			printErr("For proper indicator values please change chart side to "+ this.side);
		}
		
	}
	
	//use of string operations
	private boolean isValidTime() throws JFException {			

		boolean result = false;
		long lastTickTime = history.getLastTick(instrument).getTime();
		//you want to work with the date of the last tick - in a case you are back-testing
		String fromStr = gmtSdf.format(lastTickTime).substring(0, 11) + fromTimeStr + ":00";
		String toStr = gmtSdf.format(lastTickTime).substring(0, 11) + toTimeStr + ":00";
		try {
			long from = gmtSdf.parse(fromStr).getTime();
			long to = gmtSdf.parse(toStr).getTime();
			result = lastTickTime > from  && lastTickTime < to;			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
		if (period != this.period || instrument != this.instrument ||  !isValidTime()){
			return;
		}
		
		int candlesBefore = 2, candlesAfter = 0;
		long currBarTime = bidBar.getTime();		

		Object[] patternUni = indicators.calculateIndicator(instrument, period, new OfferSide[] { side }, indName,
				new AppliedPrice[] { appliedPrice }, optInputs, Filter.NO_FILTER, candlesBefore, currBarTime, candlesAfter);

		double[] rsiLine = (double[]) patternUni[4];
		double[] signalLine = (double[]) patternUni[3];
		
		int last = rsiLine.length - 1;
		
		print(String.format("%s rsi: last - %.5f, previous - %.5f; signal: last - %.5f, previous = %.5f", 
				gmtSdf.format(bidBar.getTime()), rsiLine[last], rsiLine[last-1], signalLine[last], signalLine[last-1]));
		
		//buy if green ("RSI Price Line") crosses above red ("Trade Signal Line")					
		if(rsiLine[last] > signalLine[last] && rsiLine[last - 1] <= signalLine[last - 1]){
			submitOrder(OrderCommand.BUY);
		}
		//sell at green crossing below red
		if(rsiLine[last] < signalLine[last] && rsiLine[last - 1] >= signalLine[last - 1]){
			submitOrder(OrderCommand.SELL);
		}
	}
	
	/**
	 * Submits an order at market price with SL and TP.
	 * 
	 * @param orderCmd
	 * @return
	 * @throws JFException
	 */
	private IOrder submitOrder(OrderCommand orderCmd) throws JFException {
		
		//send request to close the previous order
		if(order != null && order.getState() == IOrder.State.FILLED){
			order.close();
		}

		double bid = history.getLastTick(instrument).getBid();
		double ask = history.getLastTick(instrument).getAsk();
		double pip = instrument.getPipValue();		
		
		//calculate SL and TP prices
		double stopLossPrice = orderCmd.isLong() 
			? bid - slPips * pip 
			: ask + slPips * pip;
		double takeProfitPrice = orderCmd.isLong() 
			? bid + tpPips * pip 
			: ask - tpPips * pip;
		
		order = engine.submitOrder("order" + ++counter , instrument, orderCmd, orderAmount, 0, slippage, stopLossPrice, takeProfitPrice);

		return order;
	}

	private void print(Object o) {
		console.getOut().println(o);
	}
	
	private void printErr(Object o) {
		console.getErr().println(o);
	}

	@Override
	public void onTick(Instrument instrument, ITick tick) throws JFException {	}
	@Override
	public void onMessage(IMessage message) throws JFException {	}
	@Override
	public void onAccount(IAccount account) throws JFException {	}
	@Override
	public void onStop() throws JFException {	}

}
