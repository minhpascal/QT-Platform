/**
 * 
 */
package com.qtplaf.library.trading.server;

import java.util.List;

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.Tick;

/**
 * Interface responsible to provide information about the history of data and orders.
 * 
 * @author Miquel Sas
 */
public interface HistoryManager {

	/**
	 * Returns the last tick.
	 * 
	 * @param instrument The instrument.
	 * @return The last tick.
	 * @throws ServerException
	 */
	Tick getLastTick(Instrument instrument) throws ServerException;

	/**
	 * Returns the tick data.
	 * 
	 * @param instrument The instrument.
	 * @param from From time.
	 * @param to To time.
	 * @return The tick data.
	 * @throws ServerException
	 */
	List<Tick> getTickData(Instrument instrument, long from, long to) throws ServerException;

	/**
	 * Returns ticks for the instrument.
	 * 
	 * @param instrument The instrument.
	 * @param time Time of the last one second tick interval in period specified in <i>oneSecondIntervalsBefore</i>
	 *        parameter or/and time of the one second tick interval prior first one second tick interval in period
	 *        specified with <i>oneSecondIntervalsAfter</i> parameter.
	 * @param oneSecondIntervalsBefore Number of one second ticks intervals to include before and including the time
	 *        parameter.
	 * @param oneSecondIntervalsAfter Number of one second ticks intervals to include after, not including the time
	 *        parameter.
	 * @return The list of ticks.
	 * @throws ServerException
	 */
	List<Tick> getTickData(
		Instrument instrument, long time, int oneSecondIntervalsBefore, int oneSecondIntervalsAfter)
		throws ServerException;

	/**
	 * Returns a long indicating the first of OHLCV data available for the given instrument and period.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return A long indicating the first of data available for the given instrument and period.
	 * @throws ServerException
	 */
	long getTimeOfFirstOHLCVData(Instrument instrument, Period period) throws ServerException;

	/**
	 * Returns a long indicating the time of the first order for the given instrument.
	 * 
	 * @param instrument The instrument.
	 * @return A long indicating the time of the first order for the given instrument.
	 * @throws ServerException
	 */
	long getTimeOfFirstOrder(Instrument instrument) throws ServerException;

	/**
	 * Returns a long indicating the time of the first tick for the given instrument.
	 * 
	 * @param instrument The instrument.
	 * @return A long indicating the time of the first tick for the given instrument.
	 * @throws ServerException
	 */
	long getTimeOfFirstTick(Instrument instrument) throws ServerException;
	
	/**
	 * Returns the last OHLCV data element.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The OHLCV data element.
	 * @throws ServerException
	 */
	OHLCV getLastOHLCV(Instrument instrument, Period period) throws ServerException;

	/**
	 * Returns the OHLCV data element.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param shift Shift, 0 for current, 1 for previous, and so n.
	 * @return The OHLCV data element.
	 * @throws ServerException
	 */
	OHLCV getOHLCV(Instrument instrument, Period period, int shift) throws ServerException;

	/**
	 * Returns the OHLCV data element.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param offerSide The offer side.
	 * @param shift Shift, 0 for current, 1 for previous, and so n.
	 * @return The OHLCV data element.
	 * @throws ServerException
	 */
	OHLCV getOHLCV(Instrument instrument, Period period, OfferSide offerSide, int shift) throws ServerException;

	/**
	 * Returns the list of OHLCV data.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param filter The filter to apply.
	 * @param from From time.
	 * @param to To time.
	 * @return The list of OHLCV data.
	 * @throws ServerException
	 */
	List<OHLCV> getOHLCVData(
		Instrument instrument, Period period, Filter filter, long from, long to) throws ServerException;

	/**
	 * Returns the list of OHLCV data.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param offerSide The offer side.
	 * @param filter The filter to apply.
	 * @param from From time.
	 * @param to To time.
	 * @return The list of OHLCV data.
	 * @throws ServerException
	 */
	List<OHLCV> getOHLCVData(
		Instrument instrument, Period period, OfferSide offerSide, Filter filter, long from, long to)
		throws ServerException;

	/**
	 * Returns OHLCV data items for the sprecified parameters.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param filter The filter to apply.
	 * @param time Reference time.
	 * @param periodsBefore Number of periods before including the period starting at the reference time.
	 * @param periodsAfter Number of periods after not including the period starting at the reference time.
	 * @return The list of OHLCV data items.
	 * @throws ServerException
	 */
	List<OHLCV> getOHLCVData(
		Instrument instrument, Period period, Filter filter, long time, int periodsBefore, int periodsAfter)
		throws ServerException;

	/**
	 * Returns OHLCV data items for the sprecified parameters.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param offerSide The offer side.
	 * @param filter The filter to apply.
	 * @param time Reference time.
	 * @param periodsBefore Number of periods before including the period starting at the reference time.
	 * @param periodsAfter Number of periods after not including the period starting at the reference time.
	 * @return The list of OHLCV data items.
	 * @throws ServerException
	 */
	List<OHLCV> getOHLCVData(
		Instrument instrument, Period period, OfferSide offerSide, Filter filter,
		long time, int periodsBefore, int periodsAfter) throws ServerException;

	/**
	 * Returns an OHLCV iterator aimed to download huge amounts of data.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param filter The filter to apply.
	 * @param from From time.
	 * @param to To time.
	 * @return The OHLCV iterator.
	 * @throws ServerException
	 */
	OHLCVIterator getOHLCVIterator(
		Instrument instrument, Period period, Filter filter, long from, long to)
		throws ServerException;

	/**
	 * Returns an OHLCV iterator aimed to download huge amounts of data.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param offerSide The offer side.
	 * @param filter The filter to apply.
	 * @param from From time.
	 * @param to To time.
	 * @return The OHLCV iterator.
	 * @throws ServerException
	 */
	OHLCVIterator getOHLCVIterator(
		Instrument instrument, Period period, OfferSide offerSide, Filter filter, long from, long to)
		throws ServerException;

	/**
	 * Returns a list with the closed orders.
	 * 
	 * @param instrument The instrument.
	 * @param from From time.
	 * @param to To time.
	 * @return The list of closed orders.
	 * @throws ServerException
	 */
	List<Order> getOrders(Instrument instrument, long from, long to) throws ServerException;
}
