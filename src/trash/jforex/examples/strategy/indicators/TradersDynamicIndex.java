package trash.jforex.examples.strategy.indicators;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.List;
import java.util.Map;

import com.dukascopy.api.IConsole;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IIndicators.MaType;
import com.dukascopy.api.indicators.BooleanOptInputDescription;
import com.dukascopy.api.indicators.IDrawingIndicator;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorDrawingSupport;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

//+------------------------------------------------------------------+
//|                                    Traders Dynamic Index.mq4     |
//|                                    Copyright © 2006, Dean Malone |
//|                                    www.compassfx.com             |
//+------------------------------------------------------------------+
//+------------------------------------------------------------------+
//|                                                                  |
//|                     Traders Dynamic Index                        |
//|                                                                  |
//|  This hybrid indicator is developed to assist traders in their   |
//|  ability to decipher and monitor market conditions related to    |
//|  trend direction, market strength, and market volatility.        |
//|                                                                  | 
//|  Even though comprehensive, the T.D.I. is easy to read and use.  |
//|                                                                  |
//|  Green line  = RSI Price line                                    |
//|  Red line    = Trade Signal line                                 |
//|  Blue lines  = Volatility Band                                   | 
//|  Yellow line = Market Base Line                                  |  
//|                                                                  |
//|  Trend Direction - Immediate and Overall                         |
//|   Immediate = Green over Red...price action is moving up.        |
//|               Red over Green...price action is moving down.      |
//|                                                                  |   
//|   Overall = Yellow line trends up and down generally between the |
//|             lines 32 & 68. Watch for Yellow line to bounces off  |
//|             these lines for market reversal. Trade long when     |
//|             price is above the Yellow line, and trade short when |
//|             price is below.                                      |        
//|                                                                  |
//|  Market Strength & Volatility - Immediate and Overall            |
//|   Immediate = Green Line - Strong = Steep slope up or down.      | 
//|                            Weak = Moderate to Flat slope.        |
//|                                                                  |               
//|   Overall = Blue Lines - When expanding, market is strong and    |
//|             trending. When constricting, market is weak and      |
//|             in a range. When the Blue lines are extremely tight  |                                                       
//|             in a narrow range, expect an economic announcement   | 
//|             or other market condition to spike the market.       |
//|                                                                  |               
//|                                                                  |
//|  Entry conditions                                                |
//|   Scalping  - Long = Green over Red, Short = Red over Green      |
//|   Active    - Long = Green over Red & Yellow lines               |
//|               Short = Red over Green & Yellow lines              |    
//|   Moderate -  Long = Green over Red, Yellow, & 50 lines          |
//|               Short= Red over Green, Green below Yellow & 50 line|
//|                                                                  |
//|  Exit conditions*                                                |   
//|   Long  = Green crosses below Red                                |
//|   Short = Green crosses above Red                                |
//|   * If Green crosses either Blue lines, consider exiting when    |
//|     when the Green line crosses back over the Blue line.         |
//|                                                                  |
//|                                                                  |
//|  IMPORTANT: The default settings are well tested and proven.     |
//|             But, you can change the settings to fit your         |
//|             trading style.                                       |
//|                                                                  |
//|  Price & Line Type settings:                                     |                
//|   RSI Price settings                                             |               
//|   0 = Close price     [DEFAULT]                                  |               
//|   1 = Open price.                                                |               
//|   2 = High price.                                                |               
//|   3 = Low price.                                                 |               
//|   4 = Median price, (high+low)/2.                                |               
//|   5 = Typical price, (high+low+close)/3.                         |               
//|   6 = Weighted close price, (high+low+close+close)/4.            |               
//|                                                                  |               
//|   RSI Price Line & Signal Line Type settings                     |               
//|   0 = Simple moving average       [DEFAULT]                      |               
//|   1 = Exponential moving average                                 |               
//|   2 = Smoothed moving average                                    |               
//|   3 = Linear weighted moving average                             |               
//|                                                                  |
//|   Good trading,                                                  |   
//|                                                                  |
//|   Dean                                                           |                              
//+------------------------------------------------------------------+

/**
 *  This is an attemp to port Dean Malones's TDI indicator to JForex. 
 */
public class TradersDynamicIndex implements IIndicator, IDrawingIndicator 
{
	private static final int NUMBER_OF_INPUTS = 1;
    private static final int NUMBER_OF_OPTIONAL_INPUTS = 8;
	private static final int NUMBER_OF_OUPUTS = 5;
	
	// INPUT INDEXES
	private static final int RSI_PERIOD_IDX = 0;
    private static final int PRICE_PERIOD_IDX = 1;
	private static final int PRICE_MATYPE_IDX = 2;
    private static final int SIGNAL_PERIOD_IDX = 3;
    private static final int SIGNAL_MATYPE_IDX = 4;
    private static final int VOLATILITY_BAND_IDX = 5;
    private static final int VOLATILITY_MATYPE_IDX = 6;
    private static final int SHOW_BG_LINES_IDX = 7;

    // OUTPUT INDEXES
	private static final int VB_HIGH = 0;
	private static final int VB_LOW = 1;
	private static final int MARKET_BASE_LINE = 2;	
	private static final int SIGNAL_LINE = 3;
	private static final int PRICE_LINE = 4;
	
	// Background guide lines coordinates 
	private static final int OVERBOUGHT_LINE_Y = 68;
	private static final int MIDDLE_LINE_Y = 50;
	private static final int OVERSOLD_LINE_Y = 32;
	
	// Define a Stroke for background lines
	private static final Stroke bgLineStroke = new BasicStroke(1.0f, 
			BasicStroke.CAP_SQUARE, 				
			BasicStroke.JOIN_MITER, 1.0f, 
			new float[] {3.0f, 4.0f},
			0.0f);
	
	// Regula indicator machinery
	private IConsole console;
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][] inputs = new double[1][];
    private double[][] outputs = new double[NUMBER_OF_OUPUTS][];
    
    // Indicators for calculating TDI stuff
    private IIndicator rsiIndicator;
    private IIndicator maRSI;
    private IIndicator maSignal;
    private IIndicator maBase;
        
    // Default values
    private int volatility_band = 34;
    private int price_ma_type = 0;
    private int volatility_ma_type = 0;
	private int signal_ma_type = 0;
	private boolean showBGLines = true;
    
    
    public void onStart(IIndicatorContext context) 
    {
    	console = context.getConsole();

        // getting interfaces of RSI and MA and BBands indicators
        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        rsiIndicator = indicatorsProvider.getIndicator("RSI");
        maRSI = indicatorsProvider.getIndicator("MA");
        maSignal = indicatorsProvider.getIndicator("MA");
        maBase = indicatorsProvider.getIndicator("BBANDS");
        maBase.setOptInputParameter(1, 1.6185);
        maBase.setOptInputParameter(2, 1.6185);
        
        // Defining arrays to hold values for MAType combo boxes.
        int[] maValues = new int[IIndicators.MaType.values().length];
        String[] maNames = new String[IIndicators.MaType.values().length];
        for (int i = 0; i < maValues.length; i++) 
        {
            maValues[i] = i;
            maNames[i] = IIndicators.MaType.values()[i].name();
        }
        
        
        // Indicator declaration
        indicatorInfo = new IndicatorInfo(
        		"TDI", "Traders Dynamic Index", "My indicators",
                false, false, true, 
                NUMBER_OF_INPUTS, 
                NUMBER_OF_OPTIONAL_INPUTS, 
                NUMBER_OF_OUPUTS);
        
        // Main source for this indicator 
        inputParameterInfos = new InputParameterInfo[] {new InputParameterInfo("Input data", InputParameterInfo.Type.DOUBLE)};
        
        // optional params, one for every indicator
        optInputParameterInfos = new OptInputParameterInfo[] {
                new OptInputParameterInfo("RSI Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(13, 2, 100, 1)), 
                new OptInputParameterInfo("RSI Price Line", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription( 2, 2, 100, 1)),
                new OptInputParameterInfo("Price MAType", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(MaType.SMA.ordinal(), maValues, maNames)),
        		new OptInputParameterInfo("Trade Signal Line", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription( 7, 2, 100, 1)),
                new OptInputParameterInfo("Signal MAType", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(MaType.SMA.ordinal(), maValues, maNames)),
                new OptInputParameterInfo("Volatility Band", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription( volatility_band, 2, 100, 1)),
                new OptInputParameterInfo("Volatility MAType", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(MaType.SMA.ordinal(), maValues, maNames)),
                new OptInputParameterInfo("Show BG Lines", OptInputParameterInfo.Type.OTHER, new BooleanOptInputDescription(showBGLines))
        };
        

        // output arrays, one for RSI and one for each line being draw.
        outputParameterInfos = new OutputParameterInfo[NUMBER_OF_OUPUTS];
//        outputParameterInfos[OVER_X_LINE] = new OutputParameterInfo("Backgrnd Lines", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
//        		{{
//        			setColor(new Color(102, 51, 0));
//        			setShowValueOnChart(false);
//        			setDrawnByIndicator(true);
//        			setDrawingStyle(DrawingStyle.DASH_LINE);        
//        			setShowValueOnChart(false);        	
//        			
//        		}};
		outputParameterInfos[MARKET_BASE_LINE] = new OutputParameterInfo("Market Base Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        		{{
        			setColor(new Color(255,200,0));
        			setShowValueOnChart(false);
        		}};
		outputParameterInfos[VB_HIGH] = new OutputParameterInfo("VB High", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        		{{
        			setColor(Color.blue);
        			setDrawingStyle(DrawingStyle.LINE);
        			setShowValueOnChart(false);
        		}};
		outputParameterInfos[VB_LOW] = new OutputParameterInfo("VB Low", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        		{{
        			setColor(Color.blue);
        			setDrawingStyle(DrawingStyle.LINE);
        			setShowValueOnChart(false);
        		}};
		outputParameterInfos[PRICE_LINE] = new OutputParameterInfo("RSI Price Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        		{{
        			setColor(Color.green.darker());
        			setShowValueOnChart(false);
        		}};
		outputParameterInfos[SIGNAL_LINE] = new OutputParameterInfo("Trade Signal Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        		{{
        			setColor(new Color(222,57,57));        			
        			setShowValueOnChart(false);        	
        			setDrawnByIndicator(true);
        		}};
    		
    }

    /**
     *  Loopback must be main period for the first input, plus the least period 
     *  of the other inputs. 
     *  
     *  This took a while to get sorted out....
     */
    public int getLookback() {
    	return rsiIndicator.getLookback()
    	       + Math.min(maRSI.getLookback(), 
    	    		   Math.min(maSignal.getLookback(), maBase.getLookback()));
    }


    public IndicatorResult calculate(int startIndex, int endIndex) 
    {        
    	int rsiLookback = rsiIndicator.getLookback();
        
        if (startIndex > endIndex || rsiLookback > endIndex) 
        {    
            return new IndicatorResult(0, 0);
        } 

        int l = endIndex - startIndex + 1;
        if (rsiLookback > startIndex)
    	l =  endIndex - rsiLookback + 1;
        
        // Allocate a buffer for the RSI over price. 
        // All the other calculation will be done over these values
        double[] rsiOutput = new double[l];
        
        //init rsi indicator with input data and array for output
        rsiIndicator.setInputParameter(0, inputs[0]);
        rsiIndicator.setOutputParameter(0, rsiOutput);
        IndicatorResult rsiResult = rsiIndicator.calculate(startIndex, endIndex);
        
        if (rsiResult.getNumberOfElements() < maRSI.getLookback()) 
        {
            //not enough data to calculate sma
            return new IndicatorResult(0, 0);
        }
        
        //calculating ma's
        // Calculate the Price Line
        maRSI.setInputParameter(0, rsiOutput);
        maRSI.setOutputParameter(0, outputs[PRICE_LINE]);
        IndicatorResult sma2Res = maRSI.calculate(0, rsiResult.getNumberOfElements() - 1);

        // Calculate the Signal Line
        maSignal.setInputParameter(0, rsiOutput);
        maSignal.setOutputParameter(0, outputs[SIGNAL_LINE]);
        IndicatorResult sma7Res = maSignal.calculate(0, rsiResult.getNumberOfElements() - 1);

        // Calculate the Market Base line and Volatility Bands
        maBase.setInputParameter(0, rsiOutput);
        maBase.setOptInputParameter(0, volatility_band);
        maBase.setOutputParameter(0, outputs[VB_HIGH]);
        maBase.setOutputParameter(1, outputs[MARKET_BASE_LINE]);
        maBase.setOutputParameter(2, outputs[VB_LOW]);
        IndicatorResult sma34Res = maBase.calculate(0, rsiResult.getNumberOfElements() - 1);

        // Move values to the end of output buffers. 
        // This is a clumsy way to do it, but its working. though i'm not happy with it. 
        int i,j;        
        
        for(i = outputs[SIGNAL_LINE].length - 1, j = sma7Res.getNumberOfElements() - 1; j > 0; i--, j--)
        {
        	outputs[SIGNAL_LINE][i] = outputs[SIGNAL_LINE][j];        	
        }
        
        for(i = outputs[MARKET_BASE_LINE].length - 1, j = sma34Res.getNumberOfElements() - 1; j > 0; i--, j--)
        {
        	outputs[MARKET_BASE_LINE][i] = outputs[MARKET_BASE_LINE][j];
        	outputs[VB_HIGH][i] = outputs[VB_HIGH][j];
        	outputs[VB_LOW][i] = outputs[VB_LOW][j];
        }
        
        int iStart = rsiResult.getFirstValueIndex() + Math.min(sma2Res.getFirstValueIndex(), 
        		Math.min(sma7Res.getFirstValueIndex(), sma34Res.getFirstValueIndex()));
        
        int iEnd = Math.max(sma2Res.getNumberOfElements(), Math.max(sma7Res.getNumberOfElements(), sma34Res.getNumberOfElements()));
        IndicatorResult result = new IndicatorResult(iStart, iEnd);

        return result;
    }

    public IndicatorInfo getIndicatorInfo() 
    {
        return indicatorInfo;
    }

    public InputParameterInfo getInputParameterInfo(int index) 
    {
        if (index <= inputParameterInfos.length) {
            return inputParameterInfos[index];
        }
        return null;
    }

    public int getLookforward() {
        return 0;
    }

    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        if (index < optInputParameterInfos.length) {
            return optInputParameterInfos[index];
        }
        return null;
    }

    public OutputParameterInfo getOutputParameterInfo(int index) 
    {
        if (index < outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    public void setInputParameter(int index, Object array) {
        inputs[index] = (double[]) array;
    }

    public void setOptInputParameter(int index, Object value) 
    {
        switch (index) 
        {
            case RSI_PERIOD_IDX:
                rsiIndicator.setOptInputParameter(0, value);
                break;
                
            case PRICE_PERIOD_IDX:
                maRSI.setOptInputParameter(0, value);
                break;
                
            case SIGNAL_PERIOD_IDX:
                maSignal.setOptInputParameter(0, value);
                break;
        
            case VOLATILITY_BAND_IDX:
            	volatility_band = (Integer)value;
                maBase.setOptInputParameter(0, volatility_band);
                break;
                
            case PRICE_MATYPE_IDX:
            	price_ma_type = (Integer)value;
            	maRSI.setOptInputParameter(1, IIndicators.MaType.values()[price_ma_type].ordinal());            	
                break;
                
            case SIGNAL_MATYPE_IDX:
            	signal_ma_type = (Integer)value;
            	maSignal.setOptInputParameter(1, IIndicators.MaType.values()[signal_ma_type].ordinal());
                break;
                
            case VOLATILITY_MATYPE_IDX:
            	volatility_ma_type = (Integer)value;
            	maBase.setOptInputParameter(3, IIndicators.MaType.values()[volatility_ma_type].ordinal());
                break;
                
            case SHOW_BG_LINES_IDX:
            	showBGLines = (Boolean)value;
                break;                
        }
    }

    public void setOutputParameter(int index, Object array) 
    {
        outputs[index] = (double[]) array;
    }
    
    
    private void print(String s)
    {
    	console.getOut().println(s);
    }
    
    private void printArray (double [] arr) 
    {
        if (arr != null) 
        {
            if (arr.length > 300) 
            {
                return;
            }
            
            print("***** Start Array Output *****");
            
            for (int i=0; i<arr.length; i++) 
            {
                print("arr[" + i + "] = " + arr[i]);
            }
            
            print("***** End Array Output *******");
        }
    }
    

	
    @Override
    public Point drawOutput(Graphics g, int outputIdx, Object values,
    		Color color, Stroke stroke,
    		IIndicatorDrawingSupport indicatorDrawingSupport,
    		List<Shape> shapes, Map<Color, List<Point>> handles) 
    {
    	if (values == null) return null;
    
    	Graphics2D g2 = (Graphics2D)g;
    	
    	if (showBGLines)
    	{
	    	int y32 = (int)indicatorDrawingSupport.getYForValue(OVERSOLD_LINE_Y);
	    	int y50 = (int)indicatorDrawingSupport.getYForValue(MIDDLE_LINE_Y);
	    	int y68 = (int)indicatorDrawingSupport.getYForValue(OVERBOUGHT_LINE_Y);
    	
	    	g.setColor(Color.darkGray);
	    	g2.setStroke(bgLineStroke);
	    	g.drawLine(0, y32, indicatorDrawingSupport.getChartWidth(), y32);
	    	g.drawLine(0, y50, indicatorDrawingSupport.getChartWidth(), y50);
	    	g.drawLine(0, y68, indicatorDrawingSupport.getChartWidth(), y68);
    	}
    	
    	g.setColor(color);
    	g2.setStroke(stroke);
    	double[] v = (double[])values;
    	int[] xPoints = new int[indicatorDrawingSupport.getNumberOfCandlesOnScreen()];
    	int[] yPoints = new int[indicatorDrawingSupport.getNumberOfCandlesOnScreen()];
    	    	
    	int i = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen();
    	int k = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen() + indicatorDrawingSupport.getNumberOfCandlesOnScreen();
    	
    	for (int j = i; j < k; j++) 
    	{
   			yPoints[j-i] = (int)indicatorDrawingSupport.getYForValue(v[j]);
   			xPoints[j-i] = (int)indicatorDrawingSupport.getMiddleOfCandle(j);    		
    	}
    	
    	g.drawPolyline(xPoints, yPoints, xPoints.length);
    	
    	return null;
    }
}