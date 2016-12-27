/**
 * 
 */
package trash.jforex.learning.indicators;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * Indicator that uses other indicatros results.
 * 
 * @author Miquel Sas
 */
public class TripleEMAIndicator implements IIndicator {

	private IndicatorInfo indicatorInfo;
	private InputParameterInfo[] inputParameterInfos;
	private OptInputParameterInfo[] optInputParameterInfos;
	private OutputParameterInfo[] outputParameterInfos;
	private double[][] inputs = new double[1][];
	private int[] timePeriod = new int[3];
	private double[][] outputs = new double[3][];
	private IIndicator ema;

	/**
	 * Constructor.
	 */
	public TripleEMAIndicator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#onStart(com.dukascopy.api.indicators.IIndicatorContext)
	 */
	@Override
	public void onStart(IIndicatorContext context) {

		// Retrieve ema indicator
		ema = context.getIndicatorsProvider().getIndicator("EMA");

		indicatorInfo = new IndicatorInfo("THREEEMA", "Shows three different EMA indicators", "Test indicators", true,
			false, true, 1, 3, 3);

		inputParameterInfos = new InputParameterInfo[] { new InputParameterInfo("Input data",
			InputParameterInfo.Type.DOUBLE) };

		optInputParameterInfos = new OptInputParameterInfo[] {
			new OptInputParameterInfo("Time period EMA1", OptInputParameterInfo.Type.OTHER,
				new IntegerRangeDescription(5, 2, 1000, 1)),
			new OptInputParameterInfo("Time period EMA2", OptInputParameterInfo.Type.OTHER,
				new IntegerRangeDescription(10, 2, 1000, 1)),
			new OptInputParameterInfo("Time period EMA3", OptInputParameterInfo.Type.OTHER,
				new IntegerRangeDescription(20, 2, 1000, 1)) };

		outputParameterInfos = new OutputParameterInfo[] {
			new OutputParameterInfo("EMA1", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
			new OutputParameterInfo("EMA2", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
			new OutputParameterInfo("EMA3", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) };
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
		return inputParameterInfos[index];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#getOptInputParameterInfo(int)
	 */
	@Override
	public OptInputParameterInfo getOptInputParameterInfo(int index) {
		return optInputParameterInfos[index];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#getOutputParameterInfo(int)
	 */
	@Override
	public OutputParameterInfo getOutputParameterInfo(int index) {
		return outputParameterInfos[index];
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
		timePeriod[index] = (Integer) value;
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
		ema.setOptInputParameter(0, timePeriod[0]);
		int ema1Lookback = ema.getLookback();
		ema.setOptInputParameter(0, timePeriod[1]);
		int ema2Lookback = ema.getLookback();
		ema.setOptInputParameter(0, timePeriod[2]);
		int ema3Lookback = ema.getLookback();
		return Math.max(ema1Lookback, Math.max(ema2Lookback, ema3Lookback));
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
		// calculating startIndex taking into account look back value
		if (startIndex - getLookback() < 0) {
			startIndex -= startIndex - getLookback();
		}

		ema.setInputParameter(0, inputs[0]);

		// calculate first ema
		ema.setOptInputParameter(0, timePeriod[0]);
		ema.setOutputParameter(0, outputs[0]);
		ema.calculate(startIndex, endIndex);

		// calculate second ema
		ema.setOptInputParameter(0, timePeriod[1]);
		ema.setOutputParameter(0, outputs[1]);
		ema.calculate(startIndex, endIndex);

		// calculate third ema
		ema.setOptInputParameter(0, timePeriod[2]);
		ema.setOutputParameter(0, outputs[2]);
		return ema.calculate(startIndex, endIndex);
	}

}
