package trash.jforex.examples.strategy.indicators;

import com.dukascopy.api.Configurable;
import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.ITimedData;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.feed.IFeedListener;
import com.dukascopy.api.feed.util.RenkoFeedDescriptor;
import com.dukascopy.api.util.DateUtils;

public class RenkoStochShiftAndCandleInterval implements IStrategy, IFeedListener {
    private IConsole console;
    private IIndicators indicators;

    @Configurable("")
    public IFeedDescriptor feedDescriptor = new RenkoFeedDescriptor(Instrument.EURUSD, PriceRange.TWO_PIPS, OfferSide.BID);
    
    @Configurable("")
    public int stochPeriod = 15;
    @Configurable("")
    public int stochSlowKPrd = 5;
    @Configurable("")
    public int stochSlowDPrd = 5;
    @Configurable("")
    public IIndicators.MaType stochSlowKMAType = IIndicators.MaType.EMA;
    @Configurable("")
    public IIndicators.MaType stochSlowDMAType = IIndicators.MaType.EMA;

    private static final int K = 0;
    private static final int D = 1;

    public void onStart(IContext context) throws JFException {
        this.console = context.getConsole();
        this.indicators = context.getIndicators();

        context.setSubscribedInstruments(java.util.Collections.singleton(feedDescriptor.getInstrument()), true);
        context.subscribeToFeed(feedDescriptor, this);
    }

    public void onFeedData(IFeedDescriptor feedDescriptor, ITimedData feedData) {

        try {
            double[][] stoch = indicators
                    .stoch(feedDescriptor, feedDescriptor.getOfferSide(), stochPeriod, stochSlowKPrd, stochSlowKMAType, stochSlowDPrd, stochSlowDMAType)
                    .calculate(3, feedData.getTime(), 0);       
            console.getOut().format("Last 3 STOCH k= %s slow d=%s time=%s", toString(stoch[K]), toString(stoch[D]),DateUtils.format(feedData.getTime())).println();
            
            double[] stochLast = indicators
                    .stoch(feedDescriptor, feedDescriptor.getOfferSide(), stochPeriod, stochSlowKPrd, stochSlowKMAType, stochSlowDPrd, stochSlowDMAType)
                    .calculate(1); 
            console.getOut().format("Last STOCH slow k=%.5f slow d=%.5f \n-------", stochLast[K], stochLast[D]).println();

        } catch (JFException e) {
            console.getErr().println(e);
            e.printStackTrace();
        }
    }

    private static String toString(double[] arr) {
        StringBuffer sb = new StringBuffer();
        for (int r = 0; r < arr.length; r++) {
            sb.append(String.format("[%s] %.5f; ", r, arr[r]));
        }
        return sb.toString();
    }

    public void onAccount(IAccount account) throws JFException {
    }

    public void onMessage(IMessage message) throws JFException {
    }

    public void onStop() throws JFException {
    }

    public void onTick(Instrument instrument, ITick tick) throws JFException {
    }

    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }
}