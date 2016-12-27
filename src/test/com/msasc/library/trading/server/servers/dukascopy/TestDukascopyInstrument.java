package test.com.msasc.library.trading.server.servers.dukascopy;

import com.dukascopy.api.Instrument;
import com.qtplaf.library.trading.server.servers.dukascopy.DkUtilities;

public class TestDukascopyInstrument {

	public static void main(String[] args) {
		
		Instrument[] dkInstruments = Instrument.values();
		for (Instrument dkInstrument : dkInstruments) {
			com.qtplaf.library.trading.data.Instrument instrument = DkUtilities.fromDkInstrument(dkInstrument);
			System.out.println(
			dkInstrument.name()+", "+
			dkInstrument.getPipValue()+", "+
			dkInstrument.getPipScale()+", "+
			dkInstrument.getPrimaryJFCurrency()+", "+
			dkInstrument.getSecondaryJFCurrency()+", "+
			dkInstrument.getPrimaryJFCurrency().getJavaCurrency()+", "+
			dkInstrument.getSecondaryJFCurrency().getJavaCurrency()+", "+
			dkInstrument.toString());
//			System.out.println(
//			dkInstrument.name()+", "+
//			dkInstrument.getPipValue()+", "+
//			dkInstrument.getPipScale()+", "+
//			dkInstrument.getPrimaryJFCurrency()+", "+
//			dkInstrument.getSecondaryJFCurrency()+", "+
//			dkInstrument.getPrimaryJFCurrency().getJavaCurrency()+", "+
//			dkInstrument.getSecondaryJFCurrency().getJavaCurrency()+", "+
//			dkInstrument.toString());
//			System.out.println(dkInstrument.toString());
			System.out.println(instrument.toString());
		}
	}

}
