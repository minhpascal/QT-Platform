package trash.jforex.examples.strategy.indicators;

import java.util.Arrays;

import com.dukascopy.api.DataType;
import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IChart;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IHistory;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IIndicators.AppliedPrice;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.ITimedData;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorAppearanceInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * The strategy calculates indicators with the same parameters
 * as they appear on the last selected chart
 *
 */
public class CalculateIndicatorsFromChart implements IStrategy {

    private IIndicators indicators;
    private IHistory history;
    private IConsole console;
    private IChart chart;
    
    private int dataCount = 3;

    @Override
    public void onStart(IContext context) throws JFException {
        indicators = context.getIndicators();
        history = context.getHistory();
        console = context.getConsole();
        chart = context.getLastActiveChart();
        if (chart == null) {
            console.getErr().println("No chart opened!");
            return;
        }
        IFeedDescriptor feedDescriptor = chart.getFeedDescriptor();
        if(feedDescriptor.getDataType() == DataType.TICKS){
            console.getWarn().println("Tick charts need to get calculate with from-to method");
            return;
        }
        
        for (IIndicatorAppearanceInfo info : chart.getIndicatorApperanceInfos()) {
            AppliedPrice[] appliedPrices = new AppliedPrice[info.getDrawingStyles().length];
            Arrays.fill(appliedPrices, AppliedPrice.CLOSE);
            OfferSide[] offerSides = new OfferSide[info.getDrawingStyles().length];
            Arrays.fill(offerSides, chart.getSelectedOfferSide());
            IIndicator indicator = indicators.getIndicator(info.getName());
            ITimedData feedData = history.getFeedData(feedDescriptor, 0);
            Object[] result = indicators.calculateIndicator(feedDescriptor, offerSides, info.getName(), appliedPrices, info.getOptParams(), dataCount, feedData
                    .getTime(), 0);
            for (int i = 0; i < indicator.getIndicatorInfo().getNumberOfOutputs(); i++) {
                OutputParameterInfo.Type outputType = indicator.getOutputParameterInfo(i).getType();
                String resultStr = 
                        outputType == OutputParameterInfo.Type.DOUBLE ? Arrays.toString((double[]) result[i])
                        : outputType == OutputParameterInfo.Type.INT ? Arrays.toString((int[]) result[i])
                        : "object outputs need special processing";
                console.getOut().format("%s %s last %s values: %s", info.getName(), indicator.getOutputParameterInfo(i).getName(), dataCount, resultStr).println();
            }

        }
        context.stop();
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {}

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

    @Override
    public void onMessage(IMessage message) throws JFException {}

    @Override
    public void onAccount(IAccount account) throws JFException {}

    @Override
    public void onStop() throws JFException {}

}
