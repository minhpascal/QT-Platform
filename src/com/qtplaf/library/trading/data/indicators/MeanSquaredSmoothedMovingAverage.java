/**
 * 
 */
package com.qtplaf.library.trading.data.indicators;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Types;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.math.Calculator;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.DataType;
import com.qtplaf.library.trading.data.Indicator;
import com.qtplaf.library.trading.data.IndicatorSource;
import com.qtplaf.library.trading.data.indicators.validators.DoubleValidator;
import com.qtplaf.library.trading.data.indicators.validators.IntegerValidator;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.trading.data.info.IndicatorInfo;
import com.qtplaf.library.trading.data.info.InputInfo;
import com.qtplaf.library.trading.data.info.ParameterInfo;

/**
 * A moving average that minimizes the mean squared error and optionally smoothes the result by averaging it.
 * <p>
 * <b>Parameters</b>
 * <p>
 * <i><b>ParamPeriods</b></i> (PERIODS): The list of periods to smooth the average, for instance 8, 5, 3.
 * <p>
 * <i><b>ParamAvgType</b></i> (AVGTYPE): The average type, either SMA or WMA.
 * <p>
 * <i><b>ParamMeanSquaredPeriod</b></i> (MQPERIOD): The period to displace the smoothed average to minimze the mean
 * square error.
 * <p>
 * <i><b>ParamSmoothPeriod</b></i> (SMPERIOD): The period to smooth the final displaced average.
 * <p>
 * <i><b>ParamLearningFactor</b></i> (LEARNFACTOR): learning factor, default 0.01. A higher learning factor will perform
 * faster but can produce instability, while a smaller one will require mre iterations to exit by minimum error.
 * <p>
 * <i><b>ParamMaximumError</b></i> (MAXERROR): Maximum error to exit the optimizing loop. The maximum error depends on
 * the instrument scale, but the default 0.000001 will perform fine for the majority.
 * <p>
 * <i><b>ParamMaximumIterations</b></i> (MAXITER): Maximum number of iterations in the optimizing loop. Default is 10000
 * that will perform fine for the majority.
 * 
 * @author Miquel Sas
 */
public class MeanSquaredSmoothedMovingAverage extends Indicator {

	/**
	 * Parameter name: average periods.
	 */
	public static final String ParamPeriods = "PERIODS";
	/**
	 * Parameter name: average type.
	 */
	public static final String ParamAvgType = "AVGTYPE";
	/**
	 * Parameter name: mean squared period.
	 */
	public static final String ParamMeanSquaredPeriod = "MQPERIOD";
	/**
	 * Parameter name: final smooth period.
	 */
	public static final String ParamSmoothPeriod = "SMPERIOD";
	/**
	 * Parameter name: learning factor.
	 */
	public static final String ParamLearningFactor = "LEARNFACTOR";
	/**
	 * Parameter name: maximum error.
	 */
	public static final String ParamMaximumError = "MAXERROR";
	/**
	 * Parameter name: maximum iterations.
	 */
	public static final String ParamMaximumIterations = "MAXITER";

	/**
	 * Enumerates the possible average types.
	 */
	private enum AverageType {
		SMA,
		WMA;
	}

	/**
	 * Period.
	 */
	private List<Integer> periods;
	/**
	 * Averaget type.
	 */
	private AverageType avgType;
	/**
	 * Mean squared period.
	 */
	private int meanSquaredPeriod;
	/**
	 * Smoothing period.
	 */
	private int smoothPeriod;
	/**
	 * Learning factor.
	 */
	private double learningFactor;
	/**
	 * Maximum error.
	 */
	private double maximumError;
	/**
	 * Maximum iterations.
	 */
	private int maximumIterations;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public MeanSquaredSmoothedMovingAverage(Session session) {
		super(session);

		// Indicator info to be configured.
		IndicatorInfo info = getIndicatorInfo();

		// Name and title.
		info.setName("MQMA");
		info.setTitle("Mean squared smoothed moving average");

		// Setup input information. Uses an unique input source, with one output value, of any data type.
		info.addInput(getInputInfo());

		// Setup input parameters.
		info.addParameter(getPeriodParameter());
		info.addParameter(getAverageTypeParameter());
		info.addParameter(getMeanSquaredPeriodParameter());
		info.addParameter(getSmoothPeriodParameter());
		info.addParameter(getLearningFactorParameter());
		info.addParameter(getMaximumErrorParameter());
		info.addParameter(getMaximumIterationsParameter());
	}

	/**
	 * Returns an input info for Price, Volume and Indicator.
	 * 
	 * @return An input info for Price, Volume and Indicator.
	 */
	protected InputInfo getInputInfo() {
		InputInfo inputInfo = new InputInfo();
		inputInfo.addPossibleInputSource(DataType.Price, 1);
		inputInfo.addPossibleInputSource(DataType.Volume, 1);
		inputInfo.addPossibleInputSource(DataType.Indicator, 1);
		return inputInfo;
	}

	/**
	 * Returns a suitable period parameter.
	 * 
	 * @return A suitable period parameter.
	 */
	protected ParameterInfo getPeriodParameter() {
		Field period = new Field();
		period.setName(ParamPeriods);
		period.setAlias(ParamPeriods);
		period.setLabel("Average periods");
		period.setTitle("Average periods");
		period.setType(Types.Integer);
		period.setValidator(new IntegerValidator("Periods", 1, Integer.MAX_VALUE));
		ParameterInfo parameter = new ParameterInfo(getSession());
		parameter.setField(period);
		parameter.setMaximumValues(-1);
		return parameter;
	}

	/**
	 * Returns a suitable average type parameter.
	 * 
	 * @return A suitable average type parameter.
	 */
	protected ParameterInfo getAverageTypeParameter() {
		Field avgType = new Field();
		avgType.setName(ParamAvgType);
		avgType.setAlias(ParamAvgType);
		avgType.setLabel("Average type");
		avgType.setTitle("Average type");
		avgType.setType(Types.String);

		Value vSMA = new Value("SMA");
		vSMA.setLabel("Simple moving average");
		avgType.addPossibleValue(vSMA);

		Value vWMA = new Value("WMA");
		vWMA.setLabel("Weighted moving average");
		avgType.addPossibleValue(vWMA);

		ParameterInfo parameter = new ParameterInfo(getSession());
		parameter.setField(avgType);
		parameter.setValue(vWMA);
		return parameter;
	}

	/**
	 * Returns a suitable mean squared period parameter.
	 * 
	 * @return A suitable mean squared period parameter.
	 */
	protected ParameterInfo getMeanSquaredPeriodParameter() {
		Field period = new Field();
		period.setName(ParamMeanSquaredPeriod);
		period.setAlias(ParamMeanSquaredPeriod);
		period.setLabel("Mean squared period");
		period.setTitle("Mean squared period");
		period.setType(Types.Integer);
		period.setValidator(new IntegerValidator("Mean squared period", 1, Integer.MAX_VALUE));
		Value value = period.getDefaultValue();
		value.setInteger(3);
		ParameterInfo parameter = new ParameterInfo(getSession());
		parameter.setField(period);
		parameter.setValue(value);
		return parameter;
	}

	/**
	 * Returns a suitable smoothing period parameter.
	 * 
	 * @return A suitable smoothing period parameter.
	 */
	protected ParameterInfo getSmoothPeriodParameter() {
		Field period = new Field();
		period.setName(ParamSmoothPeriod);
		period.setAlias(ParamSmoothPeriod);
		period.setLabel("Smoothing period");
		period.setTitle("Smoothing period");
		period.setType(Types.Integer);
		period.setValidator(new IntegerValidator("Smoothing period", 1, Integer.MAX_VALUE));
		Value value = period.getDefaultValue();
		value.setInteger(3);
		ParameterInfo parameter = new ParameterInfo(getSession());
		parameter.setField(period);
		parameter.setValue(value);
		return parameter;
	}

	/**
	 * Returns a suitable learning factor parameter.
	 * 
	 * @return A suitable learning factor parameter.
	 */
	protected ParameterInfo getLearningFactorParameter() {
		Field learning = new Field();
		learning.setName(ParamLearningFactor);
		learning.setAlias(ParamLearningFactor);
		learning.setLabel("Learning factor");
		learning.setTitle("Learning factor");
		learning.setType(Types.Double);
		learning.setValidator(new DoubleValidator("Learning factor", 0.000001, Double.MAX_VALUE));
		Value value = learning.getDefaultValue();
		value.setDouble(0.01);
		ParameterInfo parameter = new ParameterInfo(getSession());
		parameter.setField(learning);
		parameter.setValue(value);
		return parameter;
	}

	/**
	 * Returns a suitable maximum error parameter.
	 * 
	 * @return A suitable maximum error parameter.
	 */
	protected ParameterInfo getMaximumErrorParameter() {
		Field maxError = new Field();
		maxError.setName(ParamMaximumError);
		maxError.setAlias(ParamMaximumError);
		maxError.setLabel("Maximum error");
		maxError.setTitle("Maximum error");
		maxError.setType(Types.Double);
		maxError.setValidator(new DoubleValidator(
			"Maximum error",
			Double.MIN_VALUE,
			Double.MAX_VALUE));
		Value value = maxError.getDefaultValue();
		value.setDouble(Double.MIN_VALUE);
		ParameterInfo parameter = new ParameterInfo(getSession());
		parameter.setField(maxError);
		parameter.setValue(value);
		return parameter;
	}

	/**
	 * Returns a suitable maximum iterations parameter.
	 * 
	 * @return A suitable maximum iterations parameter.
	 */
	protected ParameterInfo getMaximumIterationsParameter() {
		Field maxIter = new Field();
		maxIter.setName(ParamMaximumIterations);
		maxIter.setAlias(ParamMaximumIterations);
		maxIter.setLabel("Maximum iterations");
		maxIter.setTitle("Maximum iterations");
		maxIter.setType(Types.Integer);
		maxIter.setValidator(new IntegerValidator("Maximum iterations", 100, Integer.MAX_VALUE));
		Value value = maxIter.getDefaultValue();
		value.setInteger(10000);
		ParameterInfo parameter = new ParameterInfo(getSession());
		parameter.setField(maxIter);
		parameter.setValue(value);
		return parameter;
	}

	/**
	 * Called before starting calculations to give the indicator the opportunity to initialize any internal resources.
	 * 
	 * @param indicatorSources The list of indicator sources.
	 */
	public void start(List<IndicatorSource> indicatorSources) {

		// Validate that there is only one indicator source with one index of data.
		if (indicatorSources.size() > 1) {
			throw new IllegalArgumentException("Only one indicator source is admited.");
		}
		if (indicatorSources.get(0).getIndexes().size() > 1) {
			throw new IllegalArgumentException("The indicator source only admits one output index.");
		}

		// Fill aditional info
		IndicatorInfo info = getIndicatorInfo();

		// Instrument, period and scale from the first source.
		DataInfo input = indicatorSources.get(0).getDataList().getDataInfo();
		info.setInstrument(input.getInstrument());
		info.setPeriod(input.getPeriod());
		info.setPipScale(input.getPipScale());
		info.setTickScale(input.getTickScale());

		// Parameters
		ParameterInfo paramPeriods = info.getParameter(ParamPeriods);
		if (paramPeriods.size() < 1) {
			throw new IllegalStateException("At least one period is required.");
		}
		periods = new ArrayList<>();
		for (int i = 0; i < paramPeriods.size(); i++) {
			periods.add(paramPeriods.getValue(i).getInteger());
		}
		if (info.getParameter(ParamAvgType).getValue().getString().equals("WMA")) {
			avgType = AverageType.WMA;
		} else if (info.getParameter(ParamAvgType).getValue().getString().equals("SMA")) {
			avgType = AverageType.SMA;
		} else {
			throw new IllegalArgumentException(
				"Invalid average type: " + info.getParameter(ParamAvgType).getValue().getString());
		}

		meanSquaredPeriod = info.getParameter(ParamMeanSquaredPeriod).getValue().getInteger();
		smoothPeriod = info.getParameter(ParamSmoothPeriod).getValue().getInteger();
		learningFactor = info.getParameter(ParamLearningFactor).getValue().getDouble();
		maximumError = info.getParameter(ParamMaximumError).getValue().getDouble();
		maximumIterations = info.getParameter(ParamMaximumIterations).getValue().getInteger();

		// Build the output info, only the final result will be plotted. Set the total number of indexes.
		int numIndexes = 0;
		// Result smoothed
		info.addOutput("Mean square smoothed", "MSQS", numIndexes++, true);
		// Result not smoothed.
		info.addOutput("Mean square not smoothed", "MSQN", numIndexes++, false);
		// One average for all periods
		for (int i = periods.size() - 1; i >= 0; i--) {
			int period = periods.get(i);
			info.addOutput("Average (" + period + ")", "AVG(" + period + ")", numIndexes++, false);
		}
		// Source
		info.addOutput("Source", "SRC", numIndexes++, false);

		// Set the number of output indexes for reference.
		setNumIndexes(numIndexes);
	}

	/**
	 * Calculates the indicator.
	 * 
	 * @param index The data index.
	 * @param inputSources The list of input sources.
	 * @param inputIndexes The list of input indexes to be considered.
	 * @param indicatorData This indicator already calculated data.
	 * @return The result data.
	 */
	public Data calculate(int index, List<IndicatorSource> indicatorSources, DataList indicatorData) {

		// If index < 0 do nothing.
		if (index < 0) {
			return null;
		}

		// Source data list and index.
		DataList sourceData = indicatorSources.get(0).getDataList();
		int sourceIndex = indicatorSources.get(0).getIndexes().get(0);

		// Values.
		int numIndexes = getNumIndexes();
		double[] values = new double[numIndexes];

		// Set the source value.
		values[numIndexes - 1] = sourceData.get(index).getValue(sourceIndex);

		// Build the result data
		Data data = new Data();
		data.setData(values);
		data.setTime(sourceData.get(sourceIndex).getTime());

		// Temporarily add the output to the indicator data, to calculate the indicator on it.
		indicatorData.add(data);

		// Variables
		int inputIndex, outputIndex, resultIndex, startIndex, endIndex;

		// Calculate inverse recurrent averages for the list of periods.
		inputIndex = numIndexes - 1;
		outputIndex = inputIndex - 1;
		int size = periods.size();
		for (int i = 0; i < size; i++) {
			int period = periods.get(i);
			startIndex = Math.max(0, index - period + 1);
			endIndex = index;
			if (avgType.equals(AverageType.SMA)) {
				values[outputIndex] = getSMA(startIndex, endIndex, indicatorData, inputIndex);
			} else {
				values[outputIndex] = getWMA(startIndex, endIndex, indicatorData, inputIndex);
			}
			inputIndex--;
			outputIndex--;
		}

		// Calculate the mean square.
		inputIndex = numIndexes - 1;
		outputIndex = 2;
		resultIndex = 1;
		startIndex = Math.max(0, index - meanSquaredPeriod + 1);
		endIndex = index;
		meanSquared(startIndex, endIndex, indicatorData, resultIndex, outputIndex, inputIndex);

		// Calculate the smoothed result.
		inputIndex = 1;
		outputIndex = 0;
		startIndex = Math.max(0, index - smoothPeriod + 1);
		endIndex = index;
		if (avgType.equals(AverageType.SMA)) {
			values[outputIndex] = getSMA(startIndex, endIndex, indicatorData, inputIndex);
		} else {
			values[outputIndex] = getWMA(startIndex, endIndex, indicatorData, inputIndex);
		}

		// Remove the temporarily added data.
		indicatorData.remove(indicatorData.size() - 1);

		return data;
	}

	/**
	 * Returns the average SMA.
	 * 
	 * @param startIndex Start index.
	 * @param endIndex End index.
	 * @param indicatorData Source data.
	 * @param indicatorIndex Source index.
	 * @return
	 */
	protected double getSMA(int startIndex, int endIndex, DataList indicatorData, int indicatorIndex) {
		double average = 0d;
		for (int i = startIndex; i <= endIndex; i++) {
			average += indicatorData.get(i).getValue(indicatorIndex);
		}
		average = average / (double) (endIndex - startIndex + 1);
		return average;
	}

	/**
	 * Returns the average WMA.
	 * 
	 * @param startIndex Start index.
	 * @param endIndex End index.
	 * @param indicatorData Source data.
	 * @param indicatorIndex Source index.
	 * @return
	 */
	protected double getWMA(int startIndex, int endIndex, DataList indicatorData, int indicatorIndex) {
		double average = 0d;
		double weight = 1d;
		double totalWeight = 0d;
		for (int i = startIndex; i <= endIndex; i++) {
			average += (indicatorData.get(i).getValue(indicatorIndex) * weight);
			totalWeight += weight;
			weight += 1;
		}
		average = average / totalWeight;
		return average;
	}

	/**
	 * Sets the adapted values that minimizes the mean squared error.
	 * 
	 * @param startIndex Start index.
	 * @param endIndex End index.
	 * @param indicatorData Source data.
	 * @param outputIndex Output index.
	 * @param inputIndex source index.
	 * @return The adapted value that minimizes the average error.
	 */
	protected void meanSquared(
		int startIndex,
		int endIndex,
		DataList indicatorData,
		int resultIndex,
		int outputIndex,
		int inputIndex) {

		int size = endIndex - startIndex + 1;
		double[] output = new double[size];
		double[] input = new double[size];
		int index = 0;
		for (int i = startIndex; i <= endIndex; i++) {
			output[index] = indicatorData.get(i).getValue(outputIndex);
			input[index] = indicatorData.get(i).getValue(inputIndex);
			index++;
		}
		double[] result = Calculator.meanSquaredMinimum(output, input, learningFactor, maximumError, maximumIterations);
		index = 0;
		for (int i = startIndex; i <= endIndex; i++) {
			indicatorData.get(i).setValue(resultIndex, result[index++]);
		}
	}
}
