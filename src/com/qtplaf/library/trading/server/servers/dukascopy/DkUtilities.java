/*
 * Copyright (C) 2015 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.qtplaf.library.trading.server.servers.dukascopy;

import java.util.Currency;

import com.dukascopy.api.IBar;
import com.dukascopy.api.ITick;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.Tick;
import com.qtplaf.library.trading.data.Unit;
import com.qtplaf.library.trading.server.Filter;
import com.qtplaf.library.trading.server.OfferSide;
import com.qtplaf.library.trading.server.OrderCommand;
import com.qtplaf.library.trading.server.OrderState;

/**
 * Utilities to convert Dukascopy definitions from/to system definitions.
 * 
 * @author Miquel Sas
 */
public class DkUtilities {

	/**
	 * Convert from a Dukascopy instrument to a system instrument.
	 * 
	 * @param dkInstrument The Dukascopy instrument.
	 * @return This system corresponding instrument.
	 */
	public static Instrument fromDkInstrument(com.dukascopy.api.Instrument dkInstrument) {
		Instrument instrument = new Instrument();

		// Identifier.
		instrument.setId(dkInstrument.name());

		// Dukascopy instruments use the JFCurrency as currency. For indexes and raw materials the primary currency is a
		// pseudo-currency like DEU.IDX and its java currency is null.
		boolean pair = dkInstrument.getPrimaryJFCurrency().getJavaCurrency() != null;

		// Description:
		instrument.setDescription(dkInstrument.toString());

		// Primary currency:
		// - If it is a pair, use the Dukascopy primary currency.
		// - If not, use the secondary currency.
		Currency primaryCurrency = dkInstrument.getPrimaryJFCurrency().getJavaCurrency();
		Currency secondaryCurrency = dkInstrument.getSecondaryJFCurrency().getJavaCurrency();
		// Correct Dukascopy bug in secondary currency with CNY.
		if (dkInstrument.getSecondaryJFCurrency().toString().equals("CNH")) {
			secondaryCurrency = Currency.getInstance("CNY");
		}
		instrument.setPrimaryCurrency(pair ? primaryCurrency : secondaryCurrency);

		// Secondary currency.
		instrument.setSecondaryCurrency(secondaryCurrency);

		// Pip value.
		instrument.setPipValue(dkInstrument.getPipValue());

		// Pip scale.
		instrument.setPipScale(dkInstrument.getPipScale());

		// Tick scale, by default one order higher than pip scale.
		int tickScale = instrument.getPipScale() + 1;
		instrument.setTickScale(tickScale);

		// TODO: Verify tick value for all instruments.
		// Tick value:
		// - For pairs tick scale unit.
		// - For the rest, half pip value.
		double tickValue = (1 / Math.pow(10, tickScale)) * (pair ? 1 : 5);
		instrument.setTickValue(tickValue);

		// TODO: Verify volume scale for all instruments.
		// Volume scale.
		instrument.setVolumeScale(0);

		return instrument;
	}

	/**
	 * Returns the Dukascopy instrument given the system instrument.
	 * 
	 * @param instrument The system instrument.
	 * @return The Dukascopy instrument.
	 */
	public static com.dukascopy.api.Instrument toDkInstrument(Instrument instrument) {
		com.dukascopy.api.Instrument[] dukascopyInstruments = com.dukascopy.api.Instrument.values();
		for (com.dukascopy.api.Instrument dukascopyInstrument : dukascopyInstruments) {
			if (dukascopyInstrument.name().equals(instrument.getId())) {
				return dukascopyInstrument;
			}
		}
		// Should never come here.
		throw new IllegalArgumentException("Instrument not supported: " + instrument.getId());
	}

	/**
	 * Returns the corresponding unit given the system unit.
	 * 
	 * @param unit The system unit.
	 * @return The Dukascopy unit.
	 */
	public static com.dukascopy.api.Unit toDkUnit(Unit unit) {
		switch (unit) {
		case Millisecond:
			return com.dukascopy.api.Unit.Millisecond;
		case Second:
			return com.dukascopy.api.Unit.Second;
		case Minute:
			return com.dukascopy.api.Unit.Minute;
		case Hour:
			return com.dukascopy.api.Unit.Hour;
		case Day:
			return com.dukascopy.api.Unit.Day;
		case Week:
			return com.dukascopy.api.Unit.Week;
		case Month:
			return com.dukascopy.api.Unit.Month;
		case Year:
			return com.dukascopy.api.Unit.Year;
		}
		// Should never come here.
		throw new IllegalArgumentException("Unit not supported: " + unit.name());
	}

	/**
	 * Returns this system unit given the Dukaascopy unit.
	 * 
	 * @param dkUnit The Dukascopy unit.
	 * @return The system unit.
	 */
	public static Unit fromDkUnit(com.dukascopy.api.Unit dkUnit) {
		switch (dkUnit) {
		case Millisecond:
			return Unit.Millisecond;
		case Second:
			return Unit.Second;
		case Minute:
			return Unit.Minute;
		case Hour:
			return Unit.Hour;
		case Day:
			return Unit.Day;
		case Week:
			return Unit.Week;
		case Month:
			return Unit.Month;
		case Year:
			return Unit.Year;
		}
		// Should never come here.
		throw new IllegalArgumentException("Unit not supported: " + dkUnit.name());
	}

	/**
	 * Returns a suitable Dukascopy period give this system period.
	 * 
	 * @param period The period.
	 * @return The Dukascopy period.
	 */
	public static com.dukascopy.api.Period toDkPeriod(Period period) {
		com.dukascopy.api.Unit unit = toDkUnit(period.getUnit());
		int size = period.getSize();
		return com.dukascopy.api.Period.createCustomPeriod(unit, size);
	}

	/**
	 * Returns this system period given the Dukascopy period.
	 * 
	 * @param dkPeriod The Dukascopy period.
	 * @return This system period.
	 */
	public static Period fromDkPeriod(com.dukascopy.api.Period dkPeriod) {
		Unit unit = fromDkUnit(dkPeriod.getUnit());
		int size = dkPeriod.getNumOfUnits();
		return new Period(unit, size);
	}

	/**
	 * Returns a suitable Dukascopy filter given this system filter.
	 * 
	 * @param filter The filter.
	 * @return The Dukascopy filter.
	 */
	public static com.dukascopy.api.Filter toDkFilter(Filter filter) {
		switch (filter) {
		case AllFlats:
			return com.dukascopy.api.Filter.ALL_FLATS;
		case NoFilter:
			return com.dukascopy.api.Filter.NO_FILTER;
		case Weekends:
			return com.dukascopy.api.Filter.WEEKENDS;
		}
		// Should never come here.
		throw new IllegalArgumentException("Filter not supported: " + filter.name());
	}

	/**
	 * Returns this system <i>OrderCommand</i> given the Dukascopy order command.
	 * 
	 * @param orderCommand The Dukascopy order command.
	 * @param closed A boolean that indicates if the order is closed.
	 * @return This system <i>OrderCommand</i>.
	 */
	public static OrderCommand fromDkOrderCommand(
		com.dukascopy.api.IEngine.OrderCommand orderCommand, boolean closed) {
		switch (orderCommand) {
		case BUY:
			return (closed ? OrderCommand.Buy : OrderCommand.BuyMarket);
		case BUYLIMIT:
			return OrderCommand.BuyLimitAsk;
		case BUYLIMIT_BYBID:
			return OrderCommand.BuyLimitBid;
		case BUYSTOP:
			return OrderCommand.BuyStopAsk;
		case BUYSTOP_BYBID:
			return OrderCommand.BuyStopBid;
		case SELL:
			return (closed ? OrderCommand.Sell : OrderCommand.SellMarket);
		case SELLLIMIT:
			return OrderCommand.SellLimitBid;
		case SELLLIMIT_BYASK:
			return OrderCommand.SellLimitAsk;
		case SELLSTOP:
			return OrderCommand.SellStopBid;
		case SELLSTOP_BYASK:
			return OrderCommand.SellStopAsk;
		case PLACE_BID:
			return OrderCommand.PlaceBid;
		case PLACE_OFFER:
			return OrderCommand.PlaceAsk;
		}
		// Should never come here.
		throw new IllegalArgumentException("Order command not supported: " + orderCommand.name());
	}

	/**
	 * Returns the Dukascopy order command given this system order command.
	 * 
	 * @param orderCommand This system order command.
	 * @return The Dukascopy order command.
	 */
	public static com.dukascopy.api.IEngine.OrderCommand toDkOrderCommand(OrderCommand orderCommand) {
		switch (orderCommand) {
		case Buy:
		case BuyMarket:
			return com.dukascopy.api.IEngine.OrderCommand.BUY;
		case BuyLimitAsk:
			return com.dukascopy.api.IEngine.OrderCommand.BUYLIMIT;
		case BuyLimitBid:
			return com.dukascopy.api.IEngine.OrderCommand.BUYLIMIT_BYBID;
		case BuyStopAsk:
			return com.dukascopy.api.IEngine.OrderCommand.BUYSTOP;
		case BuyStopBid:
			return com.dukascopy.api.IEngine.OrderCommand.BUYSTOP_BYBID;
		case Sell:
		case SellMarket:
			return com.dukascopy.api.IEngine.OrderCommand.SELL;
		case SellLimitBid:
			return com.dukascopy.api.IEngine.OrderCommand.SELLLIMIT;
		case SellLimitAsk:
			return com.dukascopy.api.IEngine.OrderCommand.SELLLIMIT_BYASK;
		case SellStopBid:
			return com.dukascopy.api.IEngine.OrderCommand.SELLSTOP;
		case SellStopAsk:
			return com.dukascopy.api.IEngine.OrderCommand.SELLSTOP_BYASK;
		case PlaceBid:
			return com.dukascopy.api.IEngine.OrderCommand.PLACE_BID;
		case PlaceAsk:
			return com.dukascopy.api.IEngine.OrderCommand.PLACE_OFFER;
		}
		// Should never come here.
		throw new IllegalArgumentException("Order command not supported: " + orderCommand.name());
	}

	/**
	 * Returns this system order state given the Dukascopy order state.
	 * 
	 * @param dkOrderState The Dukascopy order state.
	 * @return This system order state.
	 */
	public static OrderState fromDkOrderState(com.dukascopy.api.IOrder.State dkOrderState) {
		switch (dkOrderState) {
		case CANCELED:
			return OrderState.Cancelled;
		case CLOSED:
			return OrderState.Closed;
		case CREATED:
			return OrderState.Created;
		case FILLED:
			return OrderState.Filled;
		case OPENED:
			return OrderState.Opened;
		}
		// Should never come here.
		throw new IllegalArgumentException("Order state not supported: " + dkOrderState.name());
	}

	/**
	 * Returns this system offer side given the Dukascopy offer side.
	 * 
	 * @param dkOfferSide The Dukascopy offer side.
	 * @return This system offer side.
	 */
	public static OfferSide fromDkOfferSide(com.dukascopy.api.OfferSide dkOfferSide) {
		switch (dkOfferSide) {
		case ASK:
			return OfferSide.Ask;
		case BID:
			return OfferSide.Bid;
		}
		// Should never come here.
		throw new IllegalArgumentException("Offer side not supported: " + dkOfferSide.name());
	}

	/**
	 * Returns the Dukascopy offer side giventhis system offer side.
	 * 
	 * @param offerSide The offer side.
	 * @return The Dukascopy offer side.
	 */
	public static com.dukascopy.api.OfferSide toDkOfferSide(OfferSide offerSide) {
		switch (offerSide) {
		case Ask:
			return com.dukascopy.api.OfferSide.ASK;
		case Bid:
			return com.dukascopy.api.OfferSide.BID;
		}
		// Should never come here.
		throw new IllegalArgumentException("Offer side not supported: " + offerSide.name());
	}

	/**
	 * Returns this system tick given the Dukascopy tick.
	 * 
	 * @param dkTick The Dukscopy tick.
	 * @return This system tick.
	 */
	public static Tick fromDkTick(ITick dkTick) {
		Tick tick = new Tick();
		double[] askValues = dkTick.getAsks();
		double[] askVolumes = dkTick.getAskVolumes();
		int askSize = askValues.length;
		for (int i = 0; i < askSize; i++) {
			tick.addAsk(askValues[i], askVolumes[i]);
		}
		double[] bidValues = dkTick.getBids();
		double[] bidVolumes = dkTick.getBidVolumes();
		int bidSize = bidValues.length;
		for (int i = 0; i < bidSize; i++) {
			tick.addBid(bidValues[i], bidVolumes[i]);
		}
		tick.setTime(dkTick.getTime());
		return tick;
	}

	/**
	 * Returns this system OHLCV data item given the Dukascopy bar.
	 * 
	 * @param dkBar The Dukascopy bar.
	 * @return This system OHLCV data item.
	 */
	public static OHLCV fromDkBar(IBar dkBar) {
		OHLCV ohlcv = new OHLCV();
		ohlcv.setOpen(dkBar.getOpen());
		ohlcv.setHigh(dkBar.getHigh());
		ohlcv.setLow(dkBar.getLow());
		ohlcv.setClose(dkBar.getClose());
		ohlcv.setVolume(dkBar.getVolume());
		ohlcv.setTime(dkBar.getTime());
		return ohlcv;
	}
}
