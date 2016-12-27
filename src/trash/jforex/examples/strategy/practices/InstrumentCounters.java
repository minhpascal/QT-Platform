package trash.jforex.examples.strategy.practices;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.dukascopy.api.*;

/**
 * The strategy shows how one can us Map functionality to
 * conveniently bind JForex objects to values, in the particular case
 * we map each instrument to its tick count since the start of the strategy.
 *
 */
@RequiresFullAccess
public class InstrumentCounters implements IStrategy {

	private IConsole console;
	private IContext context;
	
	Set<Instrument> instruments = new HashSet<Instrument>();
	Map<Instrument, Integer> counters = new HashMap<Instrument, Integer>();
	
	@SuppressWarnings("serial")
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS") 
		{{ setTimeZone(TimeZone.getTimeZone("GMT")); }};
	
	@Override
	public void onStart(IContext context) throws JFException {
		
		console = context.getConsole();
		this.context = context;
		
		instruments.add(Instrument.EURUSD);
		instruments.add(Instrument.GBPUSD);
		instruments.add(Instrument.USDCAD);
		
		for(Instrument instrument : instruments){
			counters.put(instrument, 0);
		}

		subscribeInstruments(instruments);
	}

	@Override
	public void onTick(Instrument instrument, ITick tick) throws JFException {
		//work only with our set of instruments
		if(!counters.keySet().contains(instrument)){
			return;
		}
		//increase counter by 1
		counters.put(instrument, counters.get(instrument) + 1);

		console.getOut().println(instrument + " tick arrived");
		for(Map.Entry<Instrument, Integer> entry : counters.entrySet()){
			console.getOut().println(String.format("%s %s tick count: %s", sdf.format(tick.getTime()), entry.getKey(), entry.getValue()));
		}
	}
	
	private void subscribeInstruments(Set<Instrument> instruments){
		context.setSubscribedInstruments(instruments);
		 
		// wait max 1 second for the instruments to get subscribed
		int i = 10;
		while (!context.getSubscribedInstruments().containsAll(instruments)) {
		    try {
		        console.getOut().println("Instruments not subscribed yet " + i);
		        Thread.sleep(100);
		    } catch (InterruptedException e) {
		        console.getOut().println(e.getMessage());
		    }
		    i--;
		}
	}

	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}
	@Override
	public void onMessage(IMessage message) throws JFException {}
	@Override
	public void onAccount(IAccount account) throws JFException {}
	@Override
	public void onStop() throws JFException {}

}
