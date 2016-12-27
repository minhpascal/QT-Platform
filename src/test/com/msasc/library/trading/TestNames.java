package test.com.msasc.library.trading;

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.Unit;
import com.qtplaf.library.trading.server.Filter;
import com.qtplaf.library.trading.server.OfferSide;
import com.qtplaf.library.trading.server.database.Names;
import com.qtplaf.library.trading.server.servers.dukascopy.DkUtilities;

public class TestNames {

	public static void main(String[] args) {
		Instrument instrument = DkUtilities.fromDkInstrument(com.dukascopy.api.Instrument.EURUSD);
		Period period = new Period(Unit.Minute, 15);
		Filter filter = Filter.AllFlats;
		OfferSide offerSide = OfferSide.Ask;
		
		String name = Names.getName(null, instrument, period, filter, offerSide);
		System.out.println(name);
		System.exit(0);
	}

}
