package trash.jforex.examples.strategy.historical_data;

import com.dukascopy.api.*;

/**
 * @author Dmitry Shohov
 */
public class ReadBarsExample implements IStrategy {
	private IContext context;
	private IConsole console;
	private IHistory history;
	private int numberOfBarsLoaded;
	private boolean executed;
	
	public void onStart(IContext context) throws JFException {
		this.context = context;
		this.history = context.getHistory();
		this.console = context.getConsole();
	}

	public void onAccount(IAccount account) throws JFException {
	}

	public void onMessage(IMessage message) throws JFException {
	}

	public void onStop() throws JFException {
	}

	public void onTick(Instrument instrument, ITick tick) throws JFException {
		if (!executed) {
			console.getOut().println("Calling readBars method");
			history.readBars(Instrument.EURUSD, Period.TEN_SECS, OfferSide.BID,
					history.getBarStart(Period.DAILY, tick.getTime()) - Period.DAILY.getInterval(), // yesterday start
					history.getBarStart(Period.DAILY, tick.getTime()) - Period.TEN_SECS.getInterval(), // yesterday end
					new LoadingDataListener() {
						public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
							//no ticks expected, because we are loading bars
						}
						public void newBar(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
							++numberOfBarsLoaded;
						}
					}, new LoadingProgressListener() {
						public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
							console.getOut().println(information);
						}
						public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime) {
							if (allDataLoaded) {
								console.getOut().println("All data loaded succesfully, number of bars loaded: " + numberOfBarsLoaded);
								context.stop();
							} else {
								console.getOut().println("For some reason loading failed or was canceled by the user");
								context.stop();
							}
						}
						public boolean stopJob() {
							return false;
						}
					});
			console.getOut().println("Exited readBars method, bars read - " + numberOfBarsLoaded);
			executed = true;
			context.stop();
		}
	}

    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }
}