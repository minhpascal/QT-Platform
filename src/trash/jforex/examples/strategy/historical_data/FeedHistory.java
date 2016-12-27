package trash.jforex.examples.strategy.historical_data;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.dukascopy.api.*;
import com.dukascopy.api.feed.*;
import com.dukascopy.api.feed.util.*;
import com.dukascopy.api.util.DateUtils;


public class FeedHistory implements IStrategy, IFeedListener {

    private IConsole console;
    private IHistory history;
    int dataCount = 0;

    @Configurable(value = "feed type", description = "choose any type of feed (except ticks) in the strategy parameters dialog")
    public IFeedDescriptor feedDescriptor = new RangeBarFeedDescriptor(Instrument.EURUSD, PriceRange.TWO_PIPS, OfferSide.ASK);    

    @Override
    public void onStart(final IContext context) throws JFException {

        history = context.getHistory();
        console = context.getConsole();

        context.setSubscribedInstruments(java.util.Collections.singleton(feedDescriptor.getInstrument()), true);
        //the subscription important for enabling feed caching - hence history method performance
        context.subscribeToFeed(feedDescriptor, this); 
        
        ITimedData lastFeedData = history.getFeedData(feedDescriptor, 0); //currently forming feed element
        List<ITimedData> feedDataList = history.getFeedData(feedDescriptor, 3, lastFeedData.getTime(), 0);

        console.getOut().format("%s current=%s \n previous 3 elements=%s", 
        		feedDescriptor.getDataType(), lastFeedData, feedDataList).println();
        

       final long from = lastFeedData.getTime() - TimeUnit.DAYS.toMillis(1),
                to = lastFeedData.getTime();
        history.readFeedData(feedDescriptor, from, to, 
            new IFeedListener(){
                @Override
                public void onFeedData(IFeedDescriptor feedDescriptor, ITimedData feedData) {
                    dataCount++;
                }
            }, 
            new LoadingProgressListener(){
                @Override
                public void dataLoaded(long start, long end, long currentPosition, String information) {
                }

                @Override
                public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
                    if(allDataLoaded){
                        console.getOut().format("%s - %s %s %s feed elements loaded", 
                                DateUtils.format(from), DateUtils.format(from), dataCount, feedDescriptor.getDataType()).println();
                        context.stop();
                    }
                }

                @Override
                public boolean stopJob() {
                    return false;
                }
            }
        );

    }

    @Override
    public void onFeedData(IFeedDescriptor feedDescriptor, ITimedData feedData) {}

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {    }

    @Override
    public void onMessage(IMessage message) throws JFException {    }

    @Override
    public void onAccount(IAccount account) throws JFException {    }

    @Override
    public void onStop() throws JFException {    }

}
