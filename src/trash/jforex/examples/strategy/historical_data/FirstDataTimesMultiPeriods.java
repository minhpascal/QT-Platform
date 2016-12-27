package trash.jforex.examples.strategy.historical_data;

import java.util.List;

import com.dukascopy.api.*;
import com.dukascopy.api.util.DateUtils;
import static com.dukascopy.api.Period.*;

/**
 * The strategy retrieves first historical data for:
 * - all periods that get fed in IStrategy.onBar
 */
public class FirstDataTimesMultiPeriods implements IStrategy {
        
    private IDataService dataService;
    private IHistory history;
    private IConsole console;
    
    private Period[] periods = new Period[]{
             TEN_SECS, ONE_MIN, FIVE_MINS, TEN_MINS, FIFTEEN_MINS, THIRTY_MINS, ONE_HOUR, FOUR_HOURS, DAILY, WEEKLY, MONTHLY 
    };

    @Override
    public void onStart(IContext context) throws JFException {
        console = context.getConsole();
        history = context.getHistory();
        dataService = context.getDataService();

        for(Period period : periods){
            long timeCandle = dataService.getTimeOfFirstCandle(Instrument.EURUSD, period); 
            List<IBar> bars = null;
            try{
                long from = history.getBarStart(period, timeCandle + period.getInterval());
                long to = history.getBarStart(period, timeCandle + 2 * period.getInterval());
                bars = history.getBars(Instrument.EURUSD, period, OfferSide.BID, from, to);
            } catch (JFException e){
                console.getErr().println(e);
            }
            console.getOut().format("daily candle=%s bars from history: %s", DateUtils.format(timeCandle), bars ).println();
        }
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
