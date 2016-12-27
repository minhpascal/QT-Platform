/**
 * 
 */
package trash.jforex.learning.indicators;

import com.dukascopy.api.IConsole;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * SimpleIndicator pratice
 * 
 * @author Miquel Sas
 */
public class SimpleIndicator implements IIndicator {

	private IndicatorInfo indicatorInfo;
	private InputParameterInfo[] inputParameterInfos;
	private OptInputParameterInfo[] optInputParameterInfos;
	private OutputParameterInfo[] outputParameterInfos;

	// Indicator input used in calculations
	private double[][] inputs = new double[1][];
	// Default value of optional parameter
	private int timePeriod = 4;
	// Array of indicator output values
	private double[][] outputs = new double[1][];

	private IConsole console;

	/**
	 * Default constructor.
	 */
	public SimpleIndicator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#onStart(com.dukascopy.api.indicators.IIndicatorContext)
	 */
	@Override
	public void onStart(IIndicatorContext context) {

		// Set the indicator information:
		// - Indicator name is [SIMPLEIND]
		// - Indicator title is [Sum of previous values]
		// - Indicator group is [Test indicators]
		// - Indicator is displayed in a sub-window [!overChart]
		// - Indicator doesn't have an unstable period
		// - Indicator has one input
		// - Indicator has one optional parameter
		// - Indicator has one output
		indicatorInfo = new IndicatorInfo("SIMPLEIND", "Sums of previous values", "Test indicators", false, false,
				false, 1, 1, 1);

		// Input of type double
		inputParameterInfos = new InputParameterInfo[] { new InputParameterInfo("Input data",
				InputParameterInfo.Type.DOUBLE) };

		// Type: integer, default value: 4, minimum value: 2, maximum value: 100, incremental step: 1.
		optInputParameterInfos = new OptInputParameterInfo[] { new OptInputParameterInfo("Time period",
				OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(4, 2, 100, 1)) };

		// Output of type double, output is displayed as a line.
		outputParameterInfos = new OutputParameterInfo[] { new OutputParameterInfo("out",
				OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) };

		console = context.getConsole();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#getIndicatorInfo()
	 */
	@Override
	public IndicatorInfo getIndicatorInfo() {
		return indicatorInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#getInputParameterInfo(int)
	 */
	@Override
	public InputParameterInfo getInputParameterInfo(int index) {
		if (index <= inputParameterInfos.length) {
			return inputParameterInfos[index];
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#getOptInputParameterInfo(int)
	 */
	@Override
	public OptInputParameterInfo getOptInputParameterInfo(int index) {
		if (index <= optInputParameterInfos.length) {
			return optInputParameterInfos[index];
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#getOutputParameterInfo(int)
	 */
	@Override
	public OutputParameterInfo getOutputParameterInfo(int index) {
		if (index <= outputParameterInfos.length) {
			return outputParameterInfos[index];
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#setInputParameter(int, java.lang.Object)
	 */
	@Override
	public void setInputParameter(int index, Object array) {
		inputs[index] = (double[]) array;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#setOptInputParameter(int, java.lang.Object)
	 */
	@Override
	public void setOptInputParameter(int index, Object value) {
		timePeriod = (Integer) value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#setOutputParameter(int, java.lang.Object)
	 */
	@Override
	public void setOutputParameter(int index, Object array) {
		outputs[index] = (double[]) array;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#getLookback()
	 */
	@Override
	public int getLookback() {
		return timePeriod;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#getLookforward()
	 */
	@Override
	public int getLookforward() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#calculate(int, int)
	 */
	@Override
	public IndicatorResult calculate(int startIndex, int endIndex) {
		// Calculating startIndex taking into an account the look back value
		if (startIndex - getLookback() < 0) {
			startIndex -= startIndex - getLookback(); // startIndex = 0;
		}
		int i, j;
		for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
			double value = 0;
			// Sum values
			for (int k = timePeriod; k > 0; k--) {
				value += inputs[0][i - k];
			}
			outputs[0][j] = value;
		}
		return new IndicatorResult(startIndex, j);
	}

}
