/**
 * 
 */
package trash.jforex.learning.indicators;

import java.awt.Color;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * Signal arrows in the chart when RSI reaches certain values.
 * 
 * @author Miquel Sas
 */
public class RSISignalArrows implements IIndicator {

	private IndicatorInfo indicatorInfo;
	private InputParameterInfo[] inputParameterInfos;
	private OptInputParameterInfo[] optInputParameterInfos;
	private OutputParameterInfo[] outputParameterInfos;

	// Price includes 5 arrays: open, close, high, low, volume
	private double[][][] inputsPriceArr = new double[1][][];
	// Price array depending on AppliedPrice
	private double[][] inputsDouble = new double[1][];
	private double[][] outputs = new double[2][];

	private IIndicator rsiIndicator;
	private int rsiTimePeriod = 14;

	// Output indices
	private static final int DOWN = 0;
	private static final int UP = 1;

	// Input indices
	private static final int HIGH = 2;
	private static final int LOW = 3;

	/**
	 * Constructor.
	 */
	public RSISignalArrows() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#onStart(com.dukascopy.api.indicators.IIndicatorContext)
	 */
	@Override
	public void onStart(IIndicatorContext context) {
		indicatorInfo = new IndicatorInfo("RSI_Signals", "RSI signals", "Test indicators", true, false, false, 2, 1, 2);

		inputParameterInfos =
				new InputParameterInfo[] {
						new InputParameterInfo("Price arrays", InputParameterInfo.Type.PRICE),
						new InputParameterInfo("Price double", InputParameterInfo.Type.DOUBLE) };

		optInputParameterInfos =
				new OptInputParameterInfo[] { new OptInputParameterInfo(
						"Rsi time period",
						OptInputParameterInfo.Type.OTHER,
						new IntegerRangeDescription(rsiTimePeriod, 1, 200, 1)) };

		outputParameterInfos =
				new OutputParameterInfo[] {
						new OutputParameterInfo(
								"Maximums",
								OutputParameterInfo.Type.DOUBLE,
								OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_DOWN) {
							{
								setColor(Color.RED);
							}
						},
						new OutputParameterInfo(
								"Minimums",
								OutputParameterInfo.Type.DOUBLE,
								OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_UP) {
							{
								setColor(Color.GREEN);
							}
						} };

		rsiIndicator = context.getIndicatorsProvider().getIndicator("RSI");
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
		if (index == 0)
			inputsPriceArr[0] = (double[][]) array;
		else if (index == 1)
			inputsDouble[0] = (double[]) array;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#setOptInputParameter(int, java.lang.Object)
	 */
	@Override
	public void setOptInputParameter(int index, Object value) {
		if (index == 0) {
			// set rsi time period
			rsiTimePeriod = (Integer) value;
			rsiIndicator.setOptInputParameter(0, rsiTimePeriod);
		}
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
		return rsiTimePeriod;
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
		if (startIndex - getLookback() < 0) {
			startIndex -= startIndex - getLookback();
		}
		
		// Calculating RSI
		double[] rsiOutput = new double[endIndex - startIndex + 1];
		rsiIndicator.setInputParameter(0, inputsDouble[0]);
		rsiIndicator.setOutputParameter(0, rsiOutput);
		rsiIndicator.calculate(startIndex, endIndex);

		int i, j;
		for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
			// place down signal on the high price of the corresponding bar
			outputs[DOWN][j] = rsiOutput[j] < 30 ? inputsPriceArr[0][HIGH][i] : Double.NaN;
			// place up signal on the low price of the corresponding bar
			outputs[UP][j] = rsiOutput[j] > 70 ? inputsPriceArr[0][LOW][i] : Double.NaN;
		}

		return new IndicatorResult(startIndex, endIndex - startIndex + 1);
	}

}
