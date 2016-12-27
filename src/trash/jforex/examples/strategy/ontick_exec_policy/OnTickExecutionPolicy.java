package trash.jforex.examples.strategy.ontick_exec_policy;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import com.dukascopy.api.*;

/**
 * 
 * The strategy displays onTick execution policy - cases when ticks get skipped and when persisted.
 * The onTick method is called on a tick unless it occurred more than 1 second ago or it is not 
 * among the last 3 ticks for the particular instrument. The last tick for each instrument always "survives", 
 * with an exception if it occurred during the execution of IOrder.waitForUpdate
 *
 */
public class OnTickExecutionPolicy implements IStrategy {

	private IConsole console;
	private IHistory history;

	private Instrument instrument = Instrument.EURUSD;

	private SimpleDateFormat sdf;
	private int printedTickCount;
	private long lastTickHistoryPrint;

	@Override
	public void onStart(IContext context) throws JFException {

		this.console = context.getConsole();
		this.history = context.getHistory();

		String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss SSS";
		sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

		print("Start");
		lastTickHistoryPrint = System.currentTimeMillis();

	}

	@Override
	public void onTick(Instrument instrument, ITick tick) throws JFException {

		if (!instrument.equals(this.instrument))
			return;

		printedTickCount++;
		print("onTick start; tick time: " + sdf.format(tick.getTime()) + ", current time: " + sdf.format(System.currentTimeMillis()));

		//sleep on every 4th tick
		if (printedTickCount % 4 == 0) {
			try {
				Thread.sleep(2000);
				print("slept for 2 seconds, current time: " + sdf.format(System.currentTimeMillis()));
			} catch (InterruptedException e) {
				print(e.getMessage());
			}
		}
		
		//full list of ticks on every 10th printed tick
		if (printedTickCount % 10 == 0) {
			printTickList(history.getTicks(instrument, lastTickHistoryPrint, tick.getTime()));
			lastTickHistoryPrint = tick.getTime();
		}
	}

	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(IMessage message) throws JFException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAccount(IAccount account) throws JFException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStop() throws JFException {
		// TODO Auto-generated method stub

	}

	private void print(Object message) {
		console.getOut().println(message);
	}

	private void printTickList(List<ITick> tickList) {
		print("TICK HISTORY START");
		for (ITick tick : tickList) {
			print(sdf.format(tick.getTime()));
		}
		print("TICK HISTORY END");
	}

}
