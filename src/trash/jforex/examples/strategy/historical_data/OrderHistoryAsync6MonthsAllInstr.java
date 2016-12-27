package trash.jforex.examples.strategy.historical_data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.dukascopy.api.*;

/**
 * The strategy asynchronously reads the historical orders
 * for all instruments over the period of last 6 months.
 * 
 * The strategy stops once all orders have been read to the local cache.  
 * 
 */
@RequiresFullAccess
public class OrderHistoryAsync6MonthsAllInstr implements IStrategy {
    
    private IConsole console;
    private IHistory history;
    private IContext context;

    private Set<Instrument> instruments = new HashSet<Instrument>(Arrays.asList(Instrument.values()));

    private Map<Instrument, List<IOrder>> histOrderMap = new HashMap<Instrument, List<IOrder>>();
    private List<Instrument> loadingFinishedInstruments = new ArrayList<Instrument>();

    @SuppressWarnings("serial")
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS") {
        {
            setTimeZone(TimeZone.getTimeZone("GMT"));
        }
    };

    @Override
    public void onStart(IContext context) throws JFException {
        console = context.getConsole();
        history = context.getHistory();
        this.context = context;

        context.setSubscribedInstruments(instruments, true);
        instruments = context.getSubscribedInstruments();

        long to = System.currentTimeMillis();
        long from = to - 6 * Period.MONTHLY.getInterval();

        console.getOut().println("Reading order history for period " + sdf.format(from) + " - " + sdf.format(to));
        
        for (final Instrument instrument : instruments) {
            
            history.readOrdersHistory(instrument, from, to, new LoadingOrdersListener() {

                @Override
                public void newOrder(Instrument instrument, IOrder orderData) {
                    List<IOrder> histOrderList = histOrderMap.get(instrument);
                    if (histOrderList == null) {
                        histOrderList = new ArrayList<IOrder>();
                        histOrderMap.put(instrument, histOrderList);
                    }
                    histOrderList.add(orderData);

                }
            }, new LoadingProgressListener() {

                @Override
                public void dataLoaded(long start, long end, long currentPosition, String information) {
                }

                @Override
                public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
                    if (allDataLoaded) {
                        loadingFinishedInstruments.add(instrument);
                        console.getInfo().println("finished reading orders for " + instrument);
                    }
                }

                @Override
                public boolean stopJob() {
                    return false;
                }
            });

        }
    }

    public void onTick(Instrument instrument, ITick tick) throws JFException {
        if (instrument != Instrument.EURUSD) { // check completion only on EURUSD ticks
            return;
        }
        if (!loadingFinishedInstruments.containsAll(instruments)) {
            console.getOut().println("Loading not finished yet.");
            return;
        }

        console.getOut().println("Loading finished for all instruments");

        for (Map.Entry<Instrument, List<IOrder>> entry : histOrderMap.entrySet()) {
            console.getOut().println("Order history: " + entry);
        }
        context.stop();

    }

    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }

    public void onMessage(IMessage message) throws JFException {
    }

    public void onAccount(IAccount account) throws JFException {
    }

    public void onStop() throws JFException {
    }

}
