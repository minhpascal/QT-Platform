package trash.jforex.learning.indicators;

import com.dukascopy.api.indicators.*;
import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;

/**
 * Indicator that uses the output of another indicator.
 * <p>
 * The indicator has one input, two outputs for both SMA and RSI indicators and two optional parameters that let you set
 * a separate time period for each indicator. IIndicatorsProvider is used to get access to indicators.
 * 
 * @author Miquel Sas
 *
 */
public class SMAOverRSIIndicator implements IIndicator {
	private IndicatorInfo indicatorInfo;
	private InputParameterInfo[] inputParameterInfos;
	private OptInputParameterInfo[] optInputParameterInfos;
	private OutputParameterInfo[] outputParameterInfos;
	private double[][] inputs = new double[1][];
	private double[][] outputs = new double[2][];
	private IIndicator rsiIndicator;
	private IIndicator smaIndicator;

	public void onStart(IIndicatorContext context) {

		// Getting interfaces of RSI and SMA indicators
		rsiIndicator = context.getIndicatorsProvider().getIndicator("RSI");
		smaIndicator = context.getIndicatorsProvider().getIndicator("SMA");

		// Indicator with one input, two optional params and two outputs
		indicatorInfo = new IndicatorInfo("SMA_RSI", "SMA over RSI", "Test indicators", false, false, false, 1, 2, 2);

		// One input array of doubles
		InputParameterInfo inputDataInfo = new InputParameterInfo("Input data", InputParameterInfo.Type.DOUBLE);
		inputParameterInfos = new InputParameterInfo[] { inputDataInfo };

		// Two optional parameters, one for every indicator
		IntegerRangeDescription rangeRSI = new IntegerRangeDescription(14, 2, 100, 1);
		OptInputParameterInfo paramInfoRSI = new OptInputParameterInfo("RSI Time Period", OptInputParameterInfo.Type.OTHER, rangeRSI);

		IntegerRangeDescription rangeSMA = new IntegerRangeDescription(14, 2, 100, 1);
		OptInputParameterInfo paramInfoSMA =
				new OptInputParameterInfo("SMA Time Period", OptInputParameterInfo.Type.OTHER, rangeSMA);
		optInputParameterInfos = new OptInputParameterInfo[] { paramInfoRSI, paramInfoSMA };

		// Two output arrays, one for RSI and one for SMA over RSI
		OutputParameterInfo outputInfoRSI =
				new OutputParameterInfo("RSI line", OutputParameterInfo.Type.DOUBLE, DrawingStyle.LINE);
		OutputParameterInfo outputInfoSMA =
				new OutputParameterInfo("RSI line", OutputParameterInfo.Type.DOUBLE, DrawingStyle.LINE);
		outputParameterInfos = new OutputParameterInfo[] { outputInfoRSI, outputInfoSMA };
	}

	public IndicatorResult calculate(int startIndex, int endIndex) {

		// Calculating RSI. Needs rsiLookback elements to be calculated
		int rsiLookback = rsiIndicator.getLookback();

		// If end index is less that RSI lookback, return a null result.
		if (startIndex > endIndex || rsiLookback > endIndex) {
			return new IndicatorResult(0, 0);
		}

		// First allocate buffer for RSI results: take the greater value between start index and look back.
		double[] rsiOutput = new double[endIndex - (rsiLookback > startIndex ? rsiLookback : startIndex) + 1];

		// Initialize RSI indicator with input data and array for output
		rsiIndicator.setInputParameter(0, inputs[0]);
		rsiIndicator.setOutputParameter(0, rsiOutput);
		IndicatorResult rsiResult = rsiIndicator.calculate(startIndex, endIndex);

		// If RSI number of elements is less than SMA look back, not enough data to calculate SMA.
		if (rsiResult.getNumberOfElements() < smaIndicator.getLookback()) {
			return new IndicatorResult(0, 0);
		}

		// Calculating SMA
		smaIndicator.setInputParameter(0, rsiOutput);
		smaIndicator.setOutputParameter(0, outputs[1]);
		IndicatorResult smaResult = smaIndicator.calculate(0, rsiResult.getNumberOfElements() - 1);

		// If SMA returned 0 values...
		if (smaResult.getNumberOfElements() == 0) {
			return new IndicatorResult(0, 0);
		}

		// Copy RSI values to output excluding first values used for SMA lookback
		System.arraycopy(rsiOutput, smaResult.getFirstValueIndex(), outputs[0], 0, smaResult.getNumberOfElements());

		// Creating result, first value index for our input is FVI for rsi + FVI for SMA, because we calculated SMA
		// starting from 0 element
		int firstValueIndex = rsiResult.getFirstValueIndex() + smaResult.getFirstValueIndex();
		int numberOfElements = smaResult.getNumberOfElements();
		IndicatorResult result = new IndicatorResult(firstValueIndex, numberOfElements);
		return result;
	}

	public IndicatorInfo getIndicatorInfo() {
		return indicatorInfo;
	}

	public InputParameterInfo getInputParameterInfo(int index) {
		if (index <= inputParameterInfos.length) {
			return inputParameterInfos[index];
		}
		return null;
	}

	public int getLookback() {
		return rsiIndicator.getLookback() + smaIndicator.getLookback();
	}

	public int getLookforward() {
		return 0;
	}

	public OptInputParameterInfo getOptInputParameterInfo(int index) {
		if (index <= optInputParameterInfos.length) {
			return optInputParameterInfos[index];
		}
		return null;
	}

	public OutputParameterInfo getOutputParameterInfo(int index) {
		if (index <= outputParameterInfos.length) {
			return outputParameterInfos[index];
		}
		return null;
	}

	public void setInputParameter(int index, Object array) {
		inputs[index] = (double[]) array;
	}

	public void setOptInputParameter(int index, Object value) {
		// set optional params in indicators
		switch (index) {
		case 0:
			rsiIndicator.setOptInputParameter(0, value);
			break;
		case 1:
			smaIndicator.setOptInputParameter(0, value);
			break;
		}
	}

	public void setOutputParameter(int index, Object array) {
		outputs[index] = (double[]) array;
	}
}