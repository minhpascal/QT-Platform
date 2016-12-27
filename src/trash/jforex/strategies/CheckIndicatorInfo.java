/**
 * 
 */
package trash.jforex.strategies;

import java.util.ArrayList;
import java.util.Collection;

import com.dukascopy.api.DataType;
import com.dukascopy.api.Filter;
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
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.qtplaf.library.util.StringUtils;

import trash.jforex.indicators.PercentagePriceAvgSpread;

/**
 * Used to check indicators information.
 * 
 * @author Miquel Sas
 */
public class CheckIndicatorInfo implements IStrategy {

	private IConsole console;
	private IIndicators indicators;

	/**
	 * Constructor.
	 */
	public CheckIndicatorInfo() {
	}

	/**
	 * Called on start of the strategy.
	 */
	public void onStart(IContext context) throws JFException {

		// Save console & indicators
		console = context.getConsole();
		indicators = context.getIndicators();

		indicators.registerCustomIndicator(PercentagePriceAvgSpread.class);
		
		Collection<String> names = indicators.getAllNames();
		for (String name : names) {
			if (name.equals("AVGPRICE") || name.equals("SMA")) {
				IIndicator indicator = indicators.getIndicator(name);
				printIndicator(indicator);
			}
		}
	}
	
	private String getSeparator() {
		return StringUtils.repeat("-", 120);
	}

	/**
	 * Print indicator information
	 * 
	 * @param indicator The indicator to print the information.
	 */
	private void printIndicator(IIndicator indicator) {

		// General indicator information
		IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
		console.getOut().println(getSeparator());
		console.getOut().println("Indicator: "+indicatorInfo.getName());
		console.getOut().println(getSeparator());
		
		// Printing general information
		printIndicatorInfo(indicatorInfo);
		
		// Input parameters
		int numberOfInputs = indicatorInfo.getNumberOfInputs();
		for (int i = 0; i < numberOfInputs; i++) {
			InputParameterInfo inputParameterInfo = indicator.getInputParameterInfo(i);
			console.getOut().println(getSeparator());
			console.getOut().println("Input parameter info: "+inputParameterInfo.getName());
			console.getOut().println(getSeparator());
			printInputParameterInfo(inputParameterInfo);
		}
	}
	
	private void printInputParameterInfo(InputParameterInfo inputParameterInfo) {
		
		String name = inputParameterInfo.getName();
		Instrument instrument = inputParameterInfo.getInstrument();
		Period period = inputParameterInfo.getPeriod();
		InputParameterInfo.Type type = inputParameterInfo.getType();
		OfferSide offerSide = inputParameterInfo.getOfferSide();
		Filter filter = inputParameterInfo.getFilter();
		boolean allowIndependentPeriod = inputParameterInfo.isAllowIndependentPeriod();
		boolean autoAdjustTimeZone = inputParameterInfo.isAutoAdjustTimeZone();
		IIndicators.AppliedPrice appliedPrice = inputParameterInfo.getAppliedPrice();
		
		console.getOut().println("Name: "+name);
		console.getOut().println("Instrument: "+instrument);
		console.getOut().println("Period: "+period);
		console.getOut().println("Type: "+type);
		console.getOut().println("Offer side: "+offerSide);
		console.getOut().println("Filter: "+filter);
		console.getOut().println("Allow independent period: "+allowIndependentPeriod);
		console.getOut().println("Auto adjust time zone: "+autoAdjustTimeZone);
		console.getOut().println("Applied price: "+appliedPrice);
		
	}

	private void printIndicatorInfo(IndicatorInfo indicatorInfo) {

		String name = indicatorInfo.getName();
		String groupName = indicatorInfo.getGroupName();
		String title = indicatorInfo.getTitle();
		int numberOfInputs = indicatorInfo.getNumberOfInputs();
		int numberOfOptionalInputs = indicatorInfo.getNumberOfOptionalInputs();
		int numberOfOutputs = indicatorInfo.getNumberOfOutputs();
		boolean overChart = indicatorInfo.isOverChart();
		boolean overVolumes = indicatorInfo.isOverVolumes();
		boolean recalculateAll = indicatorInfo.isRecalculateAll();
		boolean recalculateOnNewCandleOnly = indicatorInfo.isRecalculateOnNewCandleOnly();
		boolean sparseIndicator = indicatorInfo.isSparseIndicator();
		boolean unstablePeriod = indicatorInfo.isUnstablePeriod();

		DataType[] dataTypes = DataType.values();
		ArrayList<DataType> supportedDataTypes = new ArrayList<>();
		for (DataType dataType : dataTypes) {
			if (indicatorInfo.isDataTypeSupported(dataType)) {
				supportedDataTypes.add(dataType);
			}
		}

		console.getOut().println("Name: "+name);
		console.getOut().println("Group name: "+groupName);
		console.getOut().println("Title: "+title);
		console.getOut().println("Number of inputs: "+numberOfInputs);
		console.getOut().println("Number of optional inputs: "+numberOfOptionalInputs);
		console.getOut().println("Number of outputs: "+numberOfOutputs);
		console.getOut().println("Over chart: "+overChart);
		console.getOut().println("Over volumes: "+overVolumes);
		console.getOut().println("Recalculate all: "+recalculateAll);
		console.getOut().println("Recalculate on new candleonly: "+recalculateOnNewCandleOnly);
		console.getOut().println("Sparse indicator: "+sparseIndicator);
		console.getOut().println("Ustable period: "+unstablePeriod);
		
		console.getOut().println();
		console.getOut().println("Supported data types:");
		for (DataType dataType : supportedDataTypes) {
			console.getOut().println("\t"+dataType);
		}
		
	}

	/**
	 * Called n every tick of every subscribed instrument.
	 */
	public void onTick(Instrument instrument, ITick tick) throws JFException {
	}

	/**
	 * Called on bar completion.
	 */
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
	}

	/**
	 * Called n message reception.
	 */
	public void onMessage(IMessage message) throws JFException {
	}

	/**
	 * Called on new account information reception.
	 */
	public void onAccount(IAccount account) throws JFException {
	}

	/**
	 * Called on stop.
	 */
	public void onStop() throws JFException {
	}

}
