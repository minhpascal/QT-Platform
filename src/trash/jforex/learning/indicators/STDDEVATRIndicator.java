/**
 * 
 */
package trash.jforex.learning.indicators;

import com.dukascopy.api.indicators.DoubleRangeDescription;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * Demonstrates how multiple inputs of different types may be defined and used in a single indicator.
 * <p>
 * We will create an indicator that calls both ATR and STDDEV indicators.
 * 
 * @author Miquel Sas
 */
public class STDDEVATRIndicator implements IIndicator {

	private IndicatorInfo indicatorInfo;
	private InputParameterInfo[] inputParameterInfos;
	private OptInputParameterInfo[] optInputParameterInfos;
	private OutputParameterInfo[] outputParameterInfos;

	// Define inputs for both indicators
	private double[][][] atrInputs = new double[1][][];
	private double[][] stddevInputs = new double[1][];
	private int atrTimePeriod = 2;
	private int stddevTimePeriod = 5;
	private double nbDev = 1;

	// Define two outputs
	private double[][] outputs = new double[2][];

	// Each indicator
	private IIndicator atrIndicator;
	private IIndicator stddevIndicator;

	/**
	 * Constructor.
	 */
	public STDDEVATRIndicator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#onStart(com.dukascopy.api.indicators.IIndicatorContext)
	 */
	@Override
	public void onStart(IIndicatorContext context) {

		// Indicators provider is used to fetch the indicators
		atrIndicator = context.getIndicatorsProvider().getIndicator("ATR");
		stddevIndicator = context.getIndicatorsProvider().getIndicator("STDDEV");

		// Indicator information:
		// - Name : ATRSTDDEV
		// - Title : ATR and STDDEV
		// - Group : Test indicators
		// - Chart : false
		// - Volumes : false
		// - Unstable: false
		// - Inputs : 2
		// - Optional: 3
		// - Outputs : 2
		indicatorInfo =
				new IndicatorInfo("ATRSTDDEV", "ATR and STDDEV", "Test indicators", false, false, false, 2, 3, 2);

		// Input parameters information
		InputParameterInfo paramInfoATR = new InputParameterInfo("ATR Input", InputParameterInfo.Type.PRICE);
		InputParameterInfo paramInfoSTDDEV = new InputParameterInfo("stddev Input", InputParameterInfo.Type.DOUBLE);
		inputParameterInfos = new InputParameterInfo[] { paramInfoATR, paramInfoSTDDEV };

		// Defining optional inputs for ATR and STDDEV indicators
		IntegerRangeDescription rangeATR = new IntegerRangeDescription(atrTimePeriod, 2, 100, 1);
		OptInputParameterInfo optInfoATR =
				new OptInputParameterInfo("ATR Time period", OptInputParameterInfo.Type.OTHER, rangeATR);

		IntegerRangeDescription rangeSTDDEV = new IntegerRangeDescription(stddevTimePeriod, 2, 100, 1);
		OptInputParameterInfo optInfoSTDDEV =
				new OptInputParameterInfo("STDDEV Time period", OptInputParameterInfo.Type.OTHER, rangeSTDDEV);

		DoubleRangeDescription rangeNBDev = new DoubleRangeDescription(nbDev, 1, 100, 0.2, 2);
		OptInputParameterInfo optInfoNBDev =
				new OptInputParameterInfo("Nb Dev", OptInputParameterInfo.Type.OTHER, rangeNBDev);

		optInputParameterInfos = new OptInputParameterInfo[] { optInfoATR, optInfoSTDDEV, optInfoNBDev };

		// Defining output information
		OutputParameterInfo outputATR =
				new OutputParameterInfo("ATR", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE);
		OutputParameterInfo outputSTDDEV =
				new OutputParameterInfo("STDDEV", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE);
		outputParameterInfos = new OutputParameterInfo[] { outputATR, outputSTDDEV };

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
			atrInputs[0] = (double[][]) array;
		else if (index == 1)
			stddevInputs[0] = (double[]) array;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.indicators.IIndicator#setOptInputParameter(int, java.lang.Object)
	 */
	@Override
	public void setOptInputParameter(int index, Object value) {
		switch (index) {
		case 0:
			atrTimePeriod = (Integer) value;
			atrIndicator.setOptInputParameter(0, atrTimePeriod);
			break;
		case 1:
			stddevTimePeriod = (Integer) value;
			stddevIndicator.setOptInputParameter(0, stddevTimePeriod);
			break;
		case 2:
			nbDev = (Double) value;
			stddevIndicator.setOptInputParameter(1, nbDev);
			break;
		default:
			throw new ArrayIndexOutOfBoundsException(index);
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
		// Fetching the greater of two look backs
		return Math.max(atrIndicator.getLookback(), stddevIndicator.getLookback());
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
		// Setting up input and output parameters for both indicators
		atrIndicator.setInputParameter(0, atrInputs[0]);
		atrIndicator.setOutputParameter(0, outputs[0]);
		stddevIndicator.setInputParameter(0, stddevInputs[0]);
		stddevIndicator.setOutputParameter(0, outputs[1]);
		// Calculating
		atrIndicator.calculate(startIndex, endIndex);
		return stddevIndicator.calculate(startIndex, endIndex);
	}

}
