package trash.jforex.examples.strategy.indicators;

import java.util.Arrays;

import com.dukascopy.api.*;
import com.dukascopy.api.IIndicators.AppliedPrice;
import com.dukascopy.indicators.SentimentIndexBarIndicator;

public class CallSentimentIndexAsInd implements IStrategy {

    private IConsole console;
    private IHistory history;
    private IIndicators indicators;

    @Configurable("")
    public Period period = Period.FOUR_HOURS;
    @Configurable("")
    public Instrument instrument = Instrument.EURUSD;
    @Configurable("")
    public int barCount = 10;

    @Override
    public void onStart(IContext context) throws JFException {
        console = context.getConsole();
        history = context.getHistory();
        indicators = context.getIndicators();
        
        long lastTickTime = history.getTimeOfLastTick(instrument);
        long from = history.getTimeForNBarsBack(period, lastTickTime, barCount);
        long to = history.getTimeForNBarsBack(period, lastTickTime, 1);
        Object[] values2 = indicators.calculateIndicator(instrument, period, new OfferSide[] { OfferSide.ASK }, "SentimentBar",
                new AppliedPrice[] { IIndicators.AppliedPrice.CLOSE },
                new Object[] { 
                        SentimentIndexBarIndicator.SentimentMode.CHART_PERIOD.ordinal(),
                        SentimentIndexBarIndicator.DataSource.INSTRUMENT.ordinal(), 
                        SentimentIndexBarIndicator.MAX_BARS_DEFAULT 
                }, from, to);

        IFXSentimentIndexBar[] sentimentBars = Arrays.copyOf((Object[]) values2[0], ((Object[]) values2[0]).length, IFXSentimentIndexBar[].class);
        for (IFXSentimentIndexBar sentimentBar : sentimentBars) {
            console.getOut().println(sentimentBar + ", close price: " + sentimentBar.getClose());
        }

    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }

    @Override
    public void onMessage(IMessage message) throws JFException {
    }

    @Override
    public void onAccount(IAccount account) throws JFException {
    }

    @Override
    public void onStop() throws JFException {
    }

}
