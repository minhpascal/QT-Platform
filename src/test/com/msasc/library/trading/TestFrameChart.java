/**
 * 
 */
package test.com.msasc.library.trading;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.Indicator;
import com.qtplaf.library.trading.data.IndicatorSource;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.PlotScale;
import com.qtplaf.library.trading.data.PlotType;
import com.qtplaf.library.trading.data.Unit;
import com.qtplaf.library.trading.data.indicators.ExponentialMovingAverage;
import com.qtplaf.library.trading.data.indicators.GaussianSmoother;
import com.qtplaf.library.trading.data.indicators.MeanSquaredSmoothedMovingAverage;
import com.qtplaf.library.trading.data.indicators.SimpleMovingAverage;
import com.qtplaf.library.trading.data.indicators.WeightedMovingAverage;
import com.qtplaf.library.trading.data.readers.BarFileReader;
import com.qtplaf.library.util.SystemUtils;
import com.qtplaf.library.util.TextServer;

/**
 * Test chart panels.
 * 
 * @author Miquel Sas
 */
public class TestFrameChart {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Locale.setDefault(Locale.US);

		TextServer.addBaseResource("StringsLibrary.xml");

		JFrameChartTest frame = new JFrameChartTest();

		// PlotData plotDataPrice = getDataEURUS_60minWMA();
		PlotData plotDataPrice = getDataEURUS_60min();

		DataList dataList = plotDataPrice.get(0);
		PlotData plotDataIndicator = new PlotData();
		plotDataIndicator.add(getWMA(dataList, 20, Color.RED, OHLCV.Index.Close.getIndex()));
		plotDataIndicator.add(getSMA(dataList, 200, Color.BLACK, OHLCV.Index.Close.getIndex()));
		plotDataIndicator.setPlotScale(plotDataPrice.getPlotScale());

		frame.getChart().getPlotParameters().setChartCrossCursorWidth(-1);
		frame.getChart().getPlotParameters().setChartCrossCursorHeight(-1);
		frame.getChart().getPlotParameters().setChartCrossCursorCircleRadius(-1);
		frame.getChart().getPlotParameters().setChartCrossCursorStroke(new BasicStroke());
//		frame.getChart().getPlotParameters().setChartCursorType(CursorType.ChartCross);

		frame.getChart().addPlotData(plotDataPrice);
		frame.getChart().addPlotData(plotDataIndicator);
	}

	private static PlotData getDataEURUS_60min() throws Exception {
		File file = SystemUtils.getFileFromClassPathEntries("EURUSD-60min.txt");
		Instrument instrument = new Instrument(
			"EUR/USD",
			"EUR/USD Spot from VisualChart",
			Currency.getInstance("EUR"),
			Currency.getInstance("USD"),
			0.0001,
			4,
			0.00001,
			5,
			0);
		Period period = new Period(Unit.Minute, 60);
		String instrumentId = "EURUS";
		// DataList dataList = BarFileReader.read(file, instrument, period, instrumentId, 1000);
		DataList dataList = BarFileReader.read(file, instrument, period, instrumentId, -1);
		dataList.setPlotType(PlotType.Candlestick);
		dataList.initializePlotProperties();
		dataList.getPlotProperties(0).setColorRaised(false);
		// dataList.getPlotProperties(0).setColorBullishOdd(Color.WHITE);

		PlotData plotDataPrice = new PlotData();
		plotDataPrice.add(dataList);
//		plotDataPrice.add(getMQMA(dataList, OHLCV.Index.Close.getIndex(), "WMA", 21, 5, 13, 5));
		// plotDataPrice.add(getSMMA(dataList, OHLCV.Index.Close.getIndex(), 1.2, 0.0, 8, 5, 3));
		// plotDataPrice.add(getSMMA(dataList, OHLCV.Index.Close.getIndex(), 1.2, 0.0, 100, 8, 3));
		 plotDataPrice.add(getSMA(dataList, 10, null, OHLCV.Index.Close.getIndex()));
		// plotDataPrice.add(getWMA(getWMA(getWMA(getWMA(dataList, 8, null, OHLCV.Index.Close.getIndex()), 4, null, 0),
		// 2, null, 0), 2, Color.RED, 0));
		// plotDataPrice.add(getSMA(getSMA(getSMA(getSMA(dataList, 8, null, OHLCV.Index.Close.getIndex()), 4, null, 0),
		// 2, null, 0), 2, Color.BLUE, 0));
		// plotDataPrice.add(getEMA(getEMA(dataList, 50, null, OHLCV.Index.Close.getIndex()), 10, null, 0));
		return plotDataPrice;
	}

	public static PlotData getDataEURUS_60minGaussian() throws Exception {
		File file = SystemUtils.getFileFromClassPathEntries("EURUSD-60min.txt");
		Instrument instrument = new Instrument(
			"EUR/USD",
			"EUR/USD Spot from VisualChart",
			Currency.getInstance("EUR"),
			Currency.getInstance("USD"),
			0.0001,
			4,
			0.00001,
			5,
			0);
		Period period = new Period(Unit.Minute, 60);
		String instrumentId = "EURUS";
		DataList dataList = BarFileReader.read(file, instrument, period, instrumentId, 1000);
		dataList.setPlotType(PlotType.Candlestick);
		dataList.initializePlotProperties();

		PlotData plotDataPrice = new PlotData();
		plotDataPrice.add(dataList);
		plotDataPrice.add(getWMA(getGaussian(dataList, 21, OHLCV.Index.Close.getIndex(), Color.RED), 5, Color.RED, 0));
		return plotDataPrice;
	}

	public static PlotData getDataEURUS_60minWMA() throws Exception {
		File file = SystemUtils.getFileFromClassPathEntries("EURUSD-60min.txt");
		Instrument instrument = new Instrument(
			"EUR/USD",
			"EUR/USD Spot from VisualChart",
			Currency.getInstance("EUR"),
			Currency.getInstance("USD"),
			0.0001,
			4,
			0.00001,
			5,
			0);
		Period period = new Period(Unit.Minute, 60);
		String instrumentId = "EURUS";
		DataList dataList = BarFileReader.read(file, instrument, period, instrumentId, 1000);
		dataList.setPlotType(PlotType.Candlestick);
		dataList.initializePlotProperties();

		PlotData plotDataPrice = new PlotData();
		plotDataPrice.add(dataList);
		plotDataPrice.add(getWMA(getWMA(dataList, 8, Color.RED, OHLCV.Index.Close.getIndex()), 5, Color.RED, 0));
		return plotDataPrice;
	}

	public static PlotData getDataEURUS_60minLine() throws Exception {
		File file = SystemUtils.getFileFromClassPathEntries("EURUSD-60min.txt");
		Instrument instrument = new Instrument(
			"EUR/USD",
			"EUR/USD Spot from VisualChart",
			Currency.getInstance("EUR"),
			Currency.getInstance("USD"),
			0.0001,
			4,
			0.00001,
			5,
			0);
		Period period = new Period(Unit.Minute, 60);
		String instrumentId = "EURUS";
		DataList dataList = BarFileReader.read(file, instrument, period, instrumentId, -1);
		dataList.setPlotType(PlotType.Line);
		dataList.setIndexOHLCV(OHLCV.Index.Close);
		dataList.initializePlotProperties();

		PlotData plotDataPrice = new PlotData();
		plotDataPrice.add(dataList);
		return plotDataPrice;
	}

	public static PlotData getDataEURUS_60minBar() throws Exception {
		File file = SystemUtils.getFileFromClassPathEntries("EURUSD-60min.txt");
		Instrument instrument = new Instrument(
			"EUR/USD",
			"EUR/USD Spot from VisualChart",
			Currency.getInstance("EUR"),
			Currency.getInstance("USD"),
			0.0001,
			4,
			0.00001,
			5,
			0);
		Period period = new Period(Unit.Minute, 60);
		String instrumentId = "EURUS";
		DataList dataList = BarFileReader.read(file, instrument, period, instrumentId, -1);
		dataList.setPlotType(PlotType.Candlestick);
		dataList.initializePlotProperties();

		PlotData plotDataPrice = new PlotData();
		plotDataPrice.add(dataList);
		return plotDataPrice;
	}

	public static PlotData getDataEURUS_1min() throws Exception {
		File file = SystemUtils.getFileFromClassPathEntries("EURUSD-1min.txt");
		Instrument instrument = new Instrument(
			"EUR/USD",
			"EUR/USD Spot from VisualChart",
			Currency.getInstance("EUR"),
			Currency.getInstance("USD"),
			0.0001,
			4,
			0.00001,
			5,
			0);
		Period period = new Period(Unit.Minute, 5);
		String instrumentId = "EURUS";
		DataList dataList = BarFileReader.read(file, instrument, period, instrumentId, 12000);
		dataList.setPlotType(PlotType.Candlestick);
		dataList.initializePlotProperties();

		PlotData plotDataPrice = new PlotData();
		plotDataPrice.add(dataList);
		plotDataPrice.add(getSMA(dataList, 100, null, OHLCV.Index.Close.getIndex()));
		return plotDataPrice;
	}

	public static PlotData getDataDJIADay() throws Exception {
		File file = SystemUtils.getFileFromClassPathEntries("DJIA-1D.txt");
		Instrument instrument = new Instrument(
			"DJIA",
			"DJIA from VisualChart",
			Currency.getInstance("USD"),
			Currency.getInstance("USD"),
			0.1,
			1,
			0.01,
			2,
			0);
		Period period = new Period(Unit.Day, 1);
		String instrumentId = ".DJI";
		DataList dataList = BarFileReader.read(file, instrument, period, instrumentId, -1);
		dataList.setPlotType(PlotType.Candlestick);
		dataList.initializePlotProperties();

		PlotData plotDataPrice = new PlotData();
		plotDataPrice.add(dataList);
		plotDataPrice.add(getSMA(dataList, 100, null, OHLCV.Index.Close.getIndex()));
		plotDataPrice.setPlotScale(PlotScale.Logarithmic);
		return plotDataPrice;
	}

	public static PlotData getDataDJIAWeek() throws Exception {
		File file = SystemUtils.getFileFromClassPathEntries("DJIA-1W.txt");
		Instrument instrument = new Instrument(
			"DJIA",
			"DJIA from VisualChart",
			Currency.getInstance("USD"),
			Currency.getInstance("USD"),
			0.1,
			1,
			0.01,
			2,
			0);
		Period period = new Period(Unit.Week, 1);
		String instrumentId = ".DJI";
		DataList dataList = BarFileReader.read(file, instrument, period, instrumentId, -1);
		dataList.setPlotType(PlotType.Candlestick);
		dataList.initializePlotProperties();

		PlotData plotDataPrice = new PlotData();
		plotDataPrice.add(dataList);
		plotDataPrice.add(getSMA(dataList, 100, null, OHLCV.Index.Close.getIndex()));
		plotDataPrice.setPlotScale(PlotScale.Logarithmic);
		return plotDataPrice;
	}

	public static DataList getSMA(DataList dataList, int period, Color color, int... ndx) {

		List<Integer> indexes = new ArrayList<>();
		for (int i : ndx) {
			indexes.add(i);
		}

		IndicatorSource source = new IndicatorSource(dataList, indexes);

		SimpleMovingAverage sma = new SimpleMovingAverage(new Session(Locale.UK));
		sma.getIndicatorInfo().getParameter(0).getValue().setInteger(period);
		DataList avgList = Indicator.calculate(new Session(Locale.UK), sma, Arrays.asList(source));

		if (color != null) {
			avgList.getPlotProperties(0).setColorBullishEven(color);
			avgList.getPlotProperties(0).setColorBearishEven(color);
			avgList.getPlotProperties(0).setColorBullishOdd(color);
			avgList.getPlotProperties(0).setColorBearishOdd(color);
		} else {
			avgList.getPlotProperties(0).setColorBullishEven(Color.BLACK);
			avgList.getPlotProperties(0).setColorBearishEven(Color.BLACK);
			avgList.getPlotProperties(0).setColorBullishOdd(Color.BLACK);
			avgList.getPlotProperties(0).setColorBearishOdd(Color.BLACK);
		}
		avgList.getPlotProperties(0).setStroke(new BasicStroke(
			0.0f,
			BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_MITER,
			3.0f,
			new float[] { 3.0f },
			0.0f));
		avgList.getPlotProperties(0).setStroke(new BasicStroke());
		return avgList;
	}

	public static DataList getWMA(DataList dataList, int period, Color color, int... ndx) {

		List<Integer> indexes = new ArrayList<>();
		for (int i : ndx) {
			indexes.add(i);
		}

		IndicatorSource source = new IndicatorSource(dataList, indexes);

		WeightedMovingAverage sma = new WeightedMovingAverage(new Session(Locale.UK));
		sma.getIndicatorInfo().getParameter(0).getValue().setInteger(period);
		DataList avgList = Indicator.calculate(new Session(Locale.UK), sma, Arrays.asList(source));

		if (color != null) {
			avgList.getPlotProperties(0).setColorBullishEven(color);
			avgList.getPlotProperties(0).setColorBearishEven(color);
			avgList.getPlotProperties(0).setColorBullishOdd(color);
			avgList.getPlotProperties(0).setColorBearishOdd(color);
		} else {
			avgList.getPlotProperties(0).setColorBullishEven(Color.BLACK);
			avgList.getPlotProperties(0).setColorBearishEven(Color.BLACK);
			avgList.getPlotProperties(0).setColorBullishOdd(Color.BLACK);
			avgList.getPlotProperties(0).setColorBearishOdd(Color.BLACK);
		}
		avgList.getPlotProperties(0).setStroke(new BasicStroke(
			0.0f,
			BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_MITER,
			3.0f,
			new float[] { 3.0f },
			0.0f));
		avgList.getPlotProperties(0).setStroke(new BasicStroke());
		return avgList;
	}

	public static DataList getGaussian(DataList dataList, int period, int index, Color color) {

		List<Integer> indexes = new ArrayList<>();
		indexes.add(index);

		IndicatorSource source = new IndicatorSource(dataList, indexes);

		GaussianSmoother sma = new GaussianSmoother(new Session(Locale.UK));
		sma.getIndicatorInfo().getParameter(0).getValue().setInteger(period);
		DataList avgList = Indicator.calculate(new Session(Locale.UK), sma, Arrays.asList(source));

		if (color != null) {
			avgList.getPlotProperties(0).setColorBullishEven(color);
			avgList.getPlotProperties(0).setColorBearishEven(color);
			avgList.getPlotProperties(0).setColorBullishOdd(color);
			avgList.getPlotProperties(0).setColorBearishOdd(color);
		} else {
			avgList.getPlotProperties(0).setColorBullishEven(Color.BLACK);
			avgList.getPlotProperties(0).setColorBearishEven(Color.BLACK);
			avgList.getPlotProperties(0).setColorBullishOdd(Color.BLACK);
			avgList.getPlotProperties(0).setColorBearishOdd(Color.BLACK);
		}
		avgList.getPlotProperties(0).setStroke(new BasicStroke(
			0.0f,
			BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_MITER,
			3.0f,
			new float[] { 3.0f },
			0.0f));
		avgList.getPlotProperties(0).setStroke(new BasicStroke());
		return avgList;
	}

	public static DataList getEMA(DataList dataList, int period, Color color, int... ndx) {

		List<Integer> indexes = new ArrayList<>();
		for (int i : ndx) {
			indexes.add(i);
		}

		IndicatorSource source = new IndicatorSource(dataList, indexes);

		ExponentialMovingAverage ema = new ExponentialMovingAverage(new Session(Locale.UK));
		ema.getIndicatorInfo().getParameter(0).getValue().setInteger(period);
		DataList avgList = Indicator.calculate(new Session(Locale.UK), ema, Arrays.asList(source));

		if (color != null) {
			avgList.getPlotProperties(0).setColorBullishEven(color);
			avgList.getPlotProperties(0).setColorBearishEven(color);
			avgList.getPlotProperties(0).setColorBullishOdd(color);
			avgList.getPlotProperties(0).setColorBearishOdd(color);
		} else {
			avgList.getPlotProperties(0).setColorBullishEven(Color.BLACK);
			avgList.getPlotProperties(0).setColorBearishEven(Color.BLACK);
			avgList.getPlotProperties(0).setColorBullishOdd(Color.BLACK);
			avgList.getPlotProperties(0).setColorBearishOdd(Color.BLACK);
		}
		avgList.getPlotProperties(0).setStroke(new BasicStroke(
			0.0f,
			BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_MITER,
			3.0f,
			new float[] { 3.0f },
			0.0f));
		avgList.getPlotProperties(0).setStroke(new BasicStroke());
		return avgList;
	}

	public static DataList getMQMA(
		DataList dataList,
		int index,
		String avgType,
		int meanSquarePeriod,
		int smoothPeriod,
		int... periods) {

		List<Integer> indexes = new ArrayList<>();
		indexes.add(index);

		IndicatorSource source = new IndicatorSource(dataList, indexes);

		MeanSquaredSmoothedMovingAverage smma = new MeanSquaredSmoothedMovingAverage(new Session(Locale.UK));
		smma.getIndicatorInfo().getParameter(MeanSquaredSmoothedMovingAverage.ParamAvgType).getValue().setString(
			avgType);
		smma
			.getIndicatorInfo()
			.getParameter(MeanSquaredSmoothedMovingAverage.ParamMeanSquaredPeriod)
			.getValue()
			.setDouble(meanSquarePeriod);
		smma.getIndicatorInfo().getParameter(MeanSquaredSmoothedMovingAverage.ParamSmoothPeriod).getValue().setDouble(
			smoothPeriod);
		for (int period : periods) {
			smma.getIndicatorInfo().getParameter(MeanSquaredSmoothedMovingAverage.ParamPeriods).addValue(
				new Value(period));
		}

		DataList avgList = Indicator.calculate(new Session(Locale.UK), smma, Arrays.asList(source));

		avgList.getPlotProperties(0).setColorBullishEven(Color.RED);
		avgList.getPlotProperties(0).setColorBearishEven(Color.RED);
		avgList.getPlotProperties(0).setColorBullishOdd(Color.RED);
		avgList.getPlotProperties(0).setColorBearishOdd(Color.RED);

		avgList.getPlotProperties(0).setStroke(new BasicStroke());
		return avgList;
	}

}
