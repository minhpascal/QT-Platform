package test.com.msasc.jforex.feed;

import com.dukascopy.api.IBar;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Period;
import com.qtplaf.library.util.Timestamp;

public class Util {
	public static void print(Period period, IBar bar, String str) {
		StringBuilder b = new StringBuilder();
		b.append(str+" - ");
		b.append(period+" - ");
		b.append(new Timestamp(bar.getTime())+", ");
		b.append(bar.getOpen()+", ");
		b.append(bar.getHigh()+", ");
		b.append(bar.getLow()+", ");
		b.append(bar.getClose()+", ");
		b.append(bar.getVolume());
		System.out.println(b);
	}
	public static void print(ITick tick, String str) {
		StringBuilder b = new StringBuilder();
		b.append(str+" - ");
		b.append(new Timestamp(tick.getTime())+", ");
		b.append(tick.getBid()+", ");
		b.append(tick.getAsk()+", ");
		System.out.println(b);
	}
}
