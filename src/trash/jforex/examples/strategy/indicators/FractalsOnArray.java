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
public class FractalsOnArray implements IStrategy {
	
	IIndicators indicators;
	IIndicator fractIndicator;
	IConsole console;
	
	public static DecimalFormat df = new DecimalFormat("0.00000");

	@Override
	public void onStart(IContext context) throws JFException {
		console = context.getConsole();
		indicators = context.getIndicators();
		fractIndicator = indicators.getIndicator("FRACTAL");
		
		//set optional inputs (number of bars = 2)
		fractIndicator.setOptInputParameter(0, 2);
		
		//set inputs (note conversion to double[][])
		double [] priceArr = new double [] { 0.1, 0.2, 0.3, 0.1, 0.1, 0.2, 0.3, 0.4, 0.2, 0.1 }; 
		double [][] arr = new double[5][priceArr.length];
		for (int i = 0; i < priceArr.length; i++) {
			//open=0, close=1, high=2, low=3, volume=4
			//assume that all prices are the same as the price in the input array (since it's the only value available)
			arr[0][i] = arr[1][i] =  arr[2][i] =  arr[3][i] = priceArr[i];
			arr[4][i] = 0;
		}
		
		fractIndicator.setInputParameter(0, arr);
		
		//set outputs
		double [] resultArrMax = new double [priceArr.length];
		double [] resultArrMin = new double [priceArr.length];
		fractIndicator.setOutputParameter(0, resultArrMax);
		fractIndicator.setOutputParameter(1, resultArrMin);
		
		//calculate
		fractIndicator.calculate(0, priceArr.length - 1);
		
		//print results
		console.getOut().println("max: " + arrayToString(resultArrMax));
		console.getOut().println("min: " + arrayToString(resultArrMin));
		
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
