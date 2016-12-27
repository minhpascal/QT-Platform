package trash.jforex.examples.strategy.indicators;

import java.text.DecimalFormat;

import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.Period;
import com.dukascopy.api.IIndicators.MaType;
import com.dukascopy.api.indicators.IIndicator;

/**
 * The following strategy demonstrates how to calculate an indicator
 * on a custom price array.
 *
 */
public class MAOnArray implements IStrategy {
	
	IIndicators indicators;
	IIndicator maIndicator;
	IConsole console;
	
	public static DecimalFormat df = new DecimalFormat("0.00000");

	@Override
	public void onStart(IContext context) throws JFException {
		console = context.getConsole();
		indicators = context.getIndicators();
		maIndicator = indicators.getIndicator("MA");
		
		//set optional inputs
		maIndicator.setOptInputParameter(0, 3);
		maIndicator.setOptInputParameter(1, MaType.SMA.ordinal());
		
		//set inputs
		double [] priceArr = new double [] { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1};
		maIndicator.setInputParameter(0, priceArr);
		
		//set outputs
		double [] resultArr = new double [priceArr.length];
		maIndicator.setOutputParameter(0, resultArr);
		
		//calculate
		maIndicator.calculate(0, priceArr.length - 1);
		
		//print results
		console.getOut().println(arrayToString(resultArr));
		
		context.stop();
	}
	
	public static String arrayToString(double[] arr) {
		String str = "";
		for (int r = 0; r < arr.length; r++) {
			str += "[" + r + "] " + df.format(arr[r]) + "; ";
		}
		return str;
	}

	@Override
	public void onTick(Instrument instrument, ITick tick) throws JFException {}
	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}
	@Override
	public void onMessage(IMessage message) throws JFException {}
	@Override
	public void onAccount(IAccount account) throws JFException {}
	@Override
	public void onStop() throws JFException {}
}
