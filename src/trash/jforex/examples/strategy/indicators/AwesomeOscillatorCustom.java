/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package trash.jforex.examples.strategy.indicators;

import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class AwesomeOscillatorCustom implements IIndicator {
    private IIndicator fastMa;
    private IIndicator slowMa;
    private int fastTimePeriod = 5;
    private int slowTimePeriod = 34;

    private IIndicatorsProvider indicatorsProvider;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private double[][] inputs = new double[1][];
    private double[][] outputs = new double[3][];

    public void onStart(IIndicatorContext context) {
        indicatorsProvider = context.getIndicatorsProvider();

        indicatorInfo = new IndicatorInfo("AwesomeCustom", "Awesome Oscillators", "Bill Williams", false, false, false, 1, 4, 3);
        // need to be set Median by default
        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
		};

        int[] maValues = new int[IIndicators.MaType.values().length - 2];
        String[] maNames = new String[IIndicators.MaType.values().length - 2];
        for (int i = 0; i < maValues.length; i++) {
            maValues[i] = i;
            maNames[i] = IIndicators.MaType.values()[i].name();
        }

        optInputParameterInfos = new OptInputParameterInfo[] {
    		new OptInputParameterInfo("FasterMA Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(5, 2, 2000, 1)),
    		new OptInputParameterInfo("FasterMA Type", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames)),
    		new OptInputParameterInfo("SlowerMA Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(34, 2, 2000, 1)),
    		new OptInputParameterInfo("SlowerMA Type", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames))
		};
        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("Zero", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE),
    		new OutputParameterInfo("Positive", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
    		new OutputParameterInfo("Negative", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM)
		};
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        //calculating startIndex taking into account lookback value
        if (startIndex - getLookback() < 0) {
            startIndex -= startIndex - getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] dmax;
        double[] dmin;

        dmax = new double[endIndex - startIndex + 2 + slowMa.getLookback()];
        dmin = new double[endIndex - startIndex + 2 + fastMa.getLookback()];

        slowMa.setInputParameter(0, inputs[0]);
        fastMa.setInputParameter(0, inputs[0]);

        slowMa.setOutputParameter(0, dmax);
        fastMa.setOutputParameter(0, dmin);

        IndicatorResult dMaxSmaResult = slowMa.calculate(startIndex - 1, endIndex);
        IndicatorResult dMinSmaResult = fastMa.calculate(startIndex - 1, endIndex);

        if (dMinSmaResult.getFirstValueIndex() != dMaxSmaResult.getFirstValueIndex() || dMaxSmaResult.getNumberOfElements() != dMinSmaResult.getNumberOfElements()) {
            throw new RuntimeException("Something wrong in ma calculation");
        }

        double value, valueLast;
        int i, k;
        for (i = 1, k = dMaxSmaResult.getNumberOfElements(); i < k; i++) {
            double dminNow = dmin[i];
            double dminPrev = dmin[i - 1];
            double dmaxNow = dmax[i];
            double dmaxPrev = dmax[i - 1];
            valueLast = dminPrev - dmaxPrev;
            value = dminNow - dmaxNow;
            outputs[0][i - 1] = 0;
            if (value >= valueLast) {
                outputs[2][i - 1] = Double.NaN;
                outputs[1][i - 1] = value;
            } else {
                outputs[1][i - 1] = Double.NaN;
                outputs[2][i - 1] = value;
            }
        }

        return new IndicatorResult(startIndex, i - 1);
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
        return (Math.max(slowMa.getLookback(), fastMa.getLookback()) + 1);
    }

    public int getLookforward() {
    	return 0;
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

    public void setOutputParameter(int index, Object array) {
        switch (index) {
            case 0:
                outputs[index] = (double[]) array;
                break;
            case 1:
                outputs[index] = (double[]) array;
                break;
            case 2:
                outputs[index] = (double[]) array;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        if (index <= optInputParameterInfos.length) {
            return optInputParameterInfos[index];
        }
        return null;
    }

    public void setOptInputParameter(int index, Object value) {
        switch (index) {
            case 0:
                fastTimePeriod = (Integer) value;
                if (fastMa != null) {
                    fastMa.setOptInputParameter(0, fastTimePeriod);
                }
                break;
            case 1:
                int fastMaType = (Integer) value;
                fastMa = indicatorsProvider.getIndicator(IIndicators.MaType.values()[fastMaType].name());
                fastMa.setOptInputParameter(0, fastTimePeriod);
                break;
            case 2:
                slowTimePeriod = (Integer) value;
                if (slowMa != null) {
                    slowMa.setOptInputParameter(0, slowTimePeriod);
                }
                break;
            case 3:
                int slowMaType = (Integer) value;
                slowMa = indicatorsProvider.getIndicator(IIndicators.MaType.values()[slowMaType].name());
                slowMa.setOptInputParameter(0, slowTimePeriod);
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }
}