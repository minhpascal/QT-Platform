package trash.jforex.examples.strategy.indicators;

import java.awt.Color;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.dukascopy.api.*;
import com.dukascopy.api.IIndicators.MaType;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;

/**
 * The following strategy shows how one can use a custom, multi-output indicator by:
 * - parsing and logging its outputs,
 * - plotting it on the chart.
 * 
 * There are two calculation variants used:
 *  - by shift (single value)
 *  - by candle interval (value array)
 *
 */
@RequiresFullAccess
public class CustomIndicatorPlotAndLogValues2 implements IStrategy {
	private IConsole console;
	private IIndicators indicators;
	private IChart chart;

	@Configurable("")
	public File indicatorJfxFile = new File("C:/temp/AwesomeOscillatorCustom.jfx");
	@Configurable("Instrument")
	public Instrument instrument = Instrument.EURUSD;
	@Configurable("Period")
	public Period period = Period.TEN_SECS;
	@Configurable("Printable bar count")
	public int candleCount = 20;
	@Configurable("Shift")
	public int shift = 2;
	
	//optional inputs according to optInputParameterInfos of the indicator
	@Configurable("")
	public int FasterMATimePeriod = 5;
	@Configurable("")
	public MaType FasterMaType = MaType.SMA;
	@Configurable("")
	public int SlowerMATimePeriod = 10;
	@Configurable("")
	public MaType SlowerMAType = MaType.SMA;
	
	//outputs according to outputParameterInfos of the indicator 
	private double[] ZeroOutput;
	private double[] PositiveOutput;
	private double[] NegativeOutput;
	
	private double ZeroByShift;
	private double PositiveByShift;
	private double NegativeByShift;
	
	private int outCount = 3;
	private String indicatorName = "AwesomeCustom";
	Object[] optionalInputArray;

	DecimalFormat df = new DecimalFormat("0.00000##");
	@SuppressWarnings("serial")
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss") {
		{
			setTimeZone(TimeZone.getTimeZone("GMT"));
		}
	};

	public void onStart(IContext context) throws JFException {
		this.console = context.getConsole();
		this.indicators = context.getIndicators();
		this.chart = context.getChart(instrument);
		
		this.indicators.registerCustomIndicator(indicatorJfxFile);		

		//note that we retrieve int value of MaType enum
		optionalInputArray = new Object[] { FasterMATimePeriod, FasterMaType.ordinal(), SlowerMATimePeriod, SlowerMAType.ordinal() };

		IIndicator awesomeCustom = indicators.getIndicator(indicatorName);
		
		chart.addIndicator(awesomeCustom, optionalInputArray, new Color[] { Color.BLACK, Color.BLUE.darker(), Color.MAGENTA.brighter() },
				new DrawingStyle[] { DrawingStyle.DASHDOT_LINE, DrawingStyle.HISTOGRAM ,DrawingStyle.HISTOGRAM }, new int[] { 1, 1, 1 });
	}

	private void print(Object o) {
		console.getOut().println(o);
	}

	public void onAccount(IAccount account) throws JFException {	}
	public void onMessage(IMessage message) throws JFException {	}
	public void onStop() throws JFException {	
		chart.removeAll();
	}
	public void onTick(Instrument instrument, ITick tick) throws JFException {	}

	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
	
		if (instrument != this.instrument || period != this.period)
			return;


		Object[] resultArr = indicators.calculateIndicator(this.instrument, this.period, new OfferSide[] { OfferSide.BID },
				indicatorName, new IIndicators.AppliedPrice[] { IIndicators.AppliedPrice.CLOSE }, optionalInputArray,
				Filter.NO_FILTER, candleCount, bidBar.getTime(), 0);
		
		Object[] resultByShift = indicators.calculateIndicator(this.instrument, this.period, new OfferSide[] { OfferSide.BID },
				indicatorName, new IIndicators.AppliedPrice[] { IIndicators.AppliedPrice.CLOSE }, optionalInputArray,
				shift);

		
		//this array is to hold all outputs
		double[][] outputArrays = new double[outCount][];
		double[] outputsByShift = new double[outCount];
		
		for (int i = 0; i < outCount; i++) {
			outputArrays[i] = (double[]) resultArr[i];
			outputsByShift[i] = Double.valueOf(resultByShift[i].toString());
		}
		
		//for convenience assign the results to meaningful variables
		ZeroOutput = outputArrays[0];
		PositiveOutput = outputArrays[1];
		NegativeOutput = outputArrays[2];
		
		ZeroByShift = outputsByShift[0];
		PositiveByShift = outputsByShift[1];
		NegativeByShift = outputsByShift[2];		

		print(sdf.format(bidBar.getTime()) + " Outputs for the last " +candleCount+" bars:");
		print("ZeroOutput: " + arrayToStringLast(ZeroOutput));
		print("PositiveOutput: " + getMaxInfo(PositiveOutput) + arrayToStringLast(PositiveOutput));
		print("NegativeOutput: " + getMinInfo(NegativeOutput) + arrayToStringLast(NegativeOutput));
		print(String.format("Outputs by shift=%s: Zero=%s Positive=%s Negative=%s",
				shift, ZeroByShift, df.format(PositiveByShift), df.format(NegativeByShift)));
	} 
	
	private String getMaxInfo(double[] arr){
		double value = Double.MIN_VALUE;
		int index = -1;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > value) {
				index = i;
				value = arr[i];
			}
		}
		return "Max value " + df.format(value) + " index: " + index + "; ";
	}
	
	private String getMinInfo(double[] arr){
		double value = Double.MAX_VALUE;
		int index = -1;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] < value) {
				index = i;
				value = arr[i];
			}
		}
		return "Min value " + df.format(value) + " index: " + index + "; ";
	}

	private String arrayToStringLast(double[] arr) {
		String str = "";
		for (int r = 0; r < arr.length; r++) {
			str += " [" + r + "]" + df.format(arr[r]) + ",";
		}
		return str;
	}
}
