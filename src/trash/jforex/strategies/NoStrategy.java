/**
 * 
 */
package trash.jforex.strategies;

import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.Period;

/**
 * A strategy that does nothing
 * 
 * @author Miquel Sas
 */
public class NoStrategy implements IStrategy {

	/**
	 * 
	 */
	public NoStrategy() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.IStrategy#onStart(com.dukascopy.api.IContext)
	 */
	@Override
	public void onStart(IContext context) throws JFException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.IStrategy#onTick(com.dukascopy.api.Instrument, com.dukascopy.api.ITick)
	 */
	@Override
	public void onTick(Instrument instrument, ITick tick) throws JFException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.IStrategy#onBar(com.dukascopy.api.Instrument, com.dukascopy.api.Period,
	 * com.dukascopy.api.IBar, com.dukascopy.api.IBar)
	 */
	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.IStrategy#onMessage(com.dukascopy.api.IMessage)
	 */
	@Override
	public void onMessage(IMessage message) throws JFException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.IStrategy#onAccount(com.dukascopy.api.IAccount)
	 */
	@Override
	public void onAccount(IAccount account) throws JFException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.IStrategy#onStop()
	 */
	@Override
	public void onStop() throws JFException {
		// TODO Auto-generated method stub

	}

}
