/**
 * 
 */
package trash.jforex.indicators;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * Spread, in percentage, between price and a moving average (WMA, EMA or SMA).
 * 
 * @author Miquel Sas
 */
public class PercentagePriceAvgSpread implements IIndicator {

	/** General indicator information. */
	private IndicatorInfo indicatorInfo;
	/** Input parameter information. */
	private InputParameterInfo[] inputParameterInfos;
	/** Optional input parameter information. */
	private OptInputParameterInfo[] optInputParameterInfos;
	/** Output parameter information. */
	private OutputParameterInfo[] outputParameterInfos;

	/** Indicator input used in calculations: one input of type DOUBLE. */
	private double[][] inputs = new double[1][];
	/** Default value of optional parameter (Average period) */
	private int averagePeriod = 8;
	/** Default value for average type: WMA. */
	private String averageType = "WMA";
	
	/** Indicator output values: only one value. */
	private double[][] outputs = new double[1][];

	/** The list of average names. */
	private String[] averageTypes = new String[] { "WMA", "EMA", "SMA" };
	
	/** The average indicator. */
	private IIndicator averageIndicator;
	
	/** Save the context to change the indicator uppon parameter  selection. */
	private IIndicatorContext context;

	/**
	 * Default constructor.
	 */
	public PercentagePriceAvgSpread() {
	}

	/**
	 * Called on start of the indicator.
	 */
	@Override
	public void onStart(IIndicatorContext context) {
		
		// Save the context
		this.context = context;

		// Indicator info
		indicatorInfo =
			new IndicatorInfo("PPOPRICEAVG",
				"Percentage price oscillator - price over average", "Test indicators", false, false, false, 1, 2, 1);

		// Input of type double
		inputParameterInfos = new InputParameterInfo[] {
			new InputParameterInfo("Input data", InputParameterInfo.Type.DOUBLE)
		};

		// Optional input parameter description: average period.
		IntegerRangeDescription optInputDescAvgPeriod = new IntegerRangeDescription(8, 3, 2400, 1);
		// Optional input parameter description: average type.
		IntegerListDescription optInputDescAvgType =
			new IntegerListDescription(0, new int[] { 0, 1, 2 }, averageTypes);

		optInputParameterInfos = new OptInputParameterInfo[] {
			new OptInputParameterInfo("Average period", OptInputParameterInfo.Type.OTHER, optInputDescAvgPeriod),
			new OptInputParameterInfo("Average type", OptInputParameterInfo.Type.OTHER, optInputDescAvgType)
		};

		// Output of type double, output is displayed as a line.
		outputParameterInfos = new OutputParameterInfo[] {
			new OutputParameterInfo("out", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
		};
		
		averageIndicator = context.getIndicatorsProvider().getIndicator(averageType);
	}

	/**
	 * Returns the indicator info.
	 */
	@Override
	public IndicatorInfo getIndicatorInfo() {
		return indicatorInfo;
	}

	/**
	 * Returns the input parameter information given the index.
	 */
	@Override
	public InputParameterInfo getInputParameterInfo(int index) {
		return inputParameterInfos[index];
	}

	/**
	 * Returns the optional input parameter information given the index.
	 */
	@Override
	public OptInputParameterInfo getOptInputParameterInfo(int index) {
		return optInputParameterInfos[index];
	}

	/**
	 * Returns the output parameter information given the index.
	 */
	@Override
	public OutputParameterInfo getOutputParameterInfo(int index) {
		return outputParameterInfos[index];
	}

	/**
	 * Sets the input parameter value given the index.
	 */
	@Override
	public void setInputParameter(int index, Object array) {
		inputs[index] = (double[]) array;
	}

	/**
	 * Sets the optional input parameter value given the index.
	 */
	@Override
	public void setOptInputParameter(int index, Object value) {
		if (index == 0) {
			averagePeriod = (Integer) value;
		}
		if (index == 1) {
			int typeIndex = (Integer) value;
			averageType = averageTypes[typeIndex];
			averageIndicator = context.getIndicatorsProvider().getIndicator(averageType);
		}
	}

	/**
	 * Sets the output parameter value given the index.
	 */
	@Override
	public void setOutputParameter(int index, Object array) {
		outputs[index] = (double[]) array;
	}

	/**
	 * Returns the look back, the number of bars ago necessary to calculate the indicator.
	 */
	@Override
	public int getLookback() {
		return averagePeriod;
	}

	/**
	 * Returns the look forward, the number of bars forwar necessary to calculate the indicator.
	 */
	@Override
	public int getLookforward() {
		return 0;
	}

	/**
	 * Calculates the indicator.
	 */
	@Override
	public IndicatorResult calculate(int startIndex, int endIndex) {
		
		// Calculating startIndex taking into an account the look back value
		if (startIndex - getLookback() < 0) {
			startIndex -= startIndex - getLookback(); // startIndex = 0;
		}
		
		// Calculating average, prior to move any start index (the average is supposed to move it by itself)
		double[] averageOutput = new double[endIndex - startIndex + 1];
		averageIndicator.setInputParameter(0, inputs[0]);
		averageIndicator.setOptInputParameter(0, averagePeriod);
		averageIndicator.setOutputParameter(0, averageOutput);
		averageIndicator.calculate(startIndex, endIndex);
		
		// Calculate the percentages
		int i, j;
		for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
			double price = inputs[0][i];
			double average = averageOutput[j];
			double percentage = (average != 0 ? 100*((price/average)-1): 0);
			outputs[0][j] = percentage;
		}
		
		return new IndicatorResult(startIndex, j);
	}

}
