package trash.jforex.examples.strategy.historical_data;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.dukascopy.api.*;


public class GetBarsExample implements IStrategy {
	private IContext context;
	private IConsole console;
	private IHistory history;

	public void onStart(IContext context) throws JFException {
		this.context = context;
		console = context.getConsole();
		history = context.getHistory();
        context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
	}

	public void onAccount(IAccount account) throws JFException {
	}

	public void onMessage(IMessage message) throws JFException {
	}

	public void onStop() throws JFException {
	}

	public void onTick(Instrument instrument, ITick tick) throws JFException {
		console.getOut().println("Loading bars and saving them in file");
		long prevBarTime = history.getPreviousBarStart(Period.TEN_SECS, tick.getTime());
		List<IBar> bars = history.getBars(instrument, Period.TEN_SECS, OfferSide.BID, history.getTimeForNBarsBack(Period.TEN_SECS, prevBarTime, 10), prevBarTime);
		File dirFile = context.getFilesDir();
		if (!dirFile.exists()) {
			console.getErr().println("Please create files directory in My Strategies");
			context.stop();
		}
		File file = new File(dirFile, "last10bars.txt");
		console.getOut().println("Writing to file " + file);
		try {
			PrintWriter pw = new PrintWriter(file);
			for (IBar bar : bars) {
				pw.println(bar.getTime() + "," + bar.getOpen() + "," + bar.getClose() + "," + bar.getHigh() + "," + bar.getLow() + "," + bar.getVolume());
			}
			pw.close();
		} catch (IOException e) {
			e.printStackTrace(console.getErr());
		}
		console.getOut().println("File saved, exiting");
		context.stop();
	}

    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }
}