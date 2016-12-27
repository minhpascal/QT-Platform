/**
 * 
 */
package trash.jforex.indicators;

import com.dukascopy.api.IBar;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * Calculates the strength of the movement by comparing the advance with the path already developed.
 * 
 * @author Miquel Sas
 */
public class PeriodStrength implements IIndicator {

	/**
	 * Indicator info. - Name : MSCSTRENGTH - Title : Period strength - Group : Test indicators - Chart : false -
	 * Volumes : false - Unstable: false - Inputs : 1 - Optional: 1 - Outputs : 1
	 */
	private IndicatorInfo indicatorInfo;
	/**
	 * Input parameter information: type BAR.
	 */
	private InputParameterInfo[] inputParameterInfos;
	/**
	 * Optional parameters information: one, the number of bars ago (period).
	 */
	private OptInputParameterInfo[] optInputParameterInfos;
	/**
	 * Output parameters information: ont output of type double, plotted with line.
	 */
	private OutputParameterInfo[] outputParameterInfos;
	/**
	 * Container for inputs (input parameters).
	 */
	private IBar[][] inputs = new IBar[1][];
	/**
	 * Period, number of bars ago.
	 */
	private int period = 4;
	/**
	 * Container for outputs (output parameters)
	 */
	private double[][] outputs = new double[1][];

	/**
	 * Constructor.
	 */
	public PeriodStrength() {
	}

	/**
	 * Called on start of the indicator.
	 */
	@Override
	public void onStart(IIndicatorContext context) {

		// Indicator info
		indicatorInfo =
			new IndicatorInfo("PERIODSTRENGTH", "Sums of previous values", "Test indicators", false, false, false, 1, 1, 1);

		// Input of type BAR
		inputParameterInfos =
			new InputParameterInfo[] { new InputParameterInfo("Input data", InputParameterInfo.Type.BAR) };

		// Optional parameter: Period
		// Type: integer, default value: 13, minimum value: 2, maximum value: 100, incremental step: 1.
		optInputParameterInfos = new OptInputParameterInfo[] {
			new OptInputParameterInfo("Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(13, 2, 100, 1))
		};

		// Output of type double, output is displayed as a line.
		outputParameterInfos = new OutputParameterInfo[] {
			new OutputParameterInfo("out", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
		};
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
		inputs[index] = (IBar[]) array;
	}

	/**
	 * Sets the optional input parameter value given the index.
	 */
	@Override
	public void setOptInputParameter(int index, Object value) {
		period = (Integer) value;
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
		return period;
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
			startIndex -= startIndex - getLookback();
		}
		int i, j;
		for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
			
			// Calculate net path: Abs(end_close - start_open)
			double startValue = inputs[0][i-period].getOpen();
			double endValue = inputs[0][i].getClose();
			double netPath = Math.abs(endValue-startValue);
			
			// Calculate total path
			double totalPath = 0;
			for (int k = period; k >= 0; k--) {
				IBar bar = inputs[0][i - k];
				totalPath += Math.abs(bar.getClose()-bar.getOpen());
			}
			
			// Calculate the value as a percentage
			double value = 100*netPath/totalPath;
			outputs[0][j] = value;
		}

		return new IndicatorResult(startIndex, j);
	}

}
