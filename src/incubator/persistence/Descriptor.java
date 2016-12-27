/**
 * 
 */
package incubator.persistence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Currency;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.Unit;
import com.qtplaf.library.trading.server.OfferSide;
import com.qtplaf.library.util.xml.Parser;
import com.qtplaf.library.util.xml.ParserHandler;

/**
 * A chunk data file descriptor.
 * 
 * @author Miquel Sas
 */
public class Descriptor {

	/**
	 * The XML parser handler to parse this descriptor..
	 */
	class Handler extends ParserHandler {
		/**
		 * Called to notify an element start.
		 */
		public void elementStart(String namespace, String elementName, String path, Attributes attributes) throws SAXException {

			try {

				// element: instrument
				if (path.equals("descriptor/instrument")) {
					instrument = new Instrument();
					// id
					String id = attributes.getValue("id");
					if (id != null) {
						instrument.setId(id);
					}
					// description
					String description = attributes.getValue("description");
					if (description != null) {
						instrument.setDescription(description);
					}
					// pip-value
					String pipValue = attributes.getValue("pip-value");
					if (pipValue != null) {
						instrument.setPipValue(Double.parseDouble(pipValue));
					}
					// pip-scale
					String pipScale = attributes.getValue("pip-scale");
					if (pipScale != null) {
						instrument.setPipScale(Integer.parseInt(pipScale));
					}
					// tick-value
					String tickValue = attributes.getValue("tick-value");
					if (tickValue != null) {
						instrument.setTickValue(Double.parseDouble(tickValue));
					}
					// tick-scale
					String tickScale = attributes.getValue("tick-scale");
					if (tickScale != null) {
						instrument.setTickScale(Integer.parseInt(tickScale));
					}
					// volume-scale
					String volumeScale = attributes.getValue("volume-scale");
					if (volumeScale != null) {
						instrument.setVolumeScale(Integer.parseInt(volumeScale));
					}
					// primary-currency
					String primaryCurrency = attributes.getValue("primary-currency");
					if (primaryCurrency != null) {
						instrument.setPrimaryCurrency(Currency.getInstance(primaryCurrency));
					}
					// secondary-currency
					String secondaryCurrency = attributes.getValue("secondary-currency");
					if (secondaryCurrency != null) {
						instrument.setSecondaryCurrency(Currency.getInstance(secondaryCurrency));
					}
				}
				// element: period
				if (path.equals("descriptor/period")) {
					Unit unit = Unit.valueOf(attributes.getValue("unit"));
					int size = Integer.parseInt(attributes.getValue("size"));
					period = new Period(unit, size);
				}
				// element: offer-side
				if (path.equals("descriptor/offer-side")) {
					offerSide = OfferSide.valueOf(attributes.getValue("value"));
				}
				// element: tick (flag)
				if (path.equals("descriptor/tick")) {
					tick = Boolean.parseBoolean(attributes.getValue("value"));
				}
				// element: OHLCV (flag)
				if (path.equals("descriptor/ohlcv")) {
					ohlcv = Boolean.parseBoolean(attributes.getValue("value"));
				}
				// element: size
				if (path.equals("descriptor/size")) {
					size = Integer.parseInt(attributes.getValue("value"));
				}

			} catch (Exception exc) {
				throw new SAXException(exc);
			}
		}
	}

	/**
	 * The underlying instrument.
	 */
	private Instrument instrument;
	/**
	 * The period or null if the underlying data is tick data.
	 */
	private Period period;
	/**
	 * The offer side, or null if the underlying data is tick or the offer side does not apply.
	 */
	private OfferSide offerSide;
	/**
	 * A boolean that indicates if the unlerlying data is <i>Tick</i>.
	 */
	private boolean tick = false;
	/**
	 * A booleanthat indicates if the underlying data is ohlcv data.
	 */
	private boolean ohlcv = false;
	/**
	 * The size or number of double values, not includng the long value of the time that is the first one stored. For
	 * ticks the number of values is always four (4), for OHLCV data five (5) and for a generic data item it can be any
	 * number of values.
	 */
	private int size;

	/**
	 * Default constructor.
	 */
	public Descriptor() {
		super();
	}

	/**
	 * Returns the data instrument.
	 * 
	 * @return The data instrument.
	 */
	public Instrument getInstrument() {
		return instrument;
	}

	/**
	 * Sets the data instrument.
	 * 
	 * @param instrument The data instrument.
	 */
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	/**
	 * Returns the data period.
	 * 
	 * @return The data period.
	 */
	public Period getPeriod() {
		return period;
	}

	/**
	 * Sets the data period.
	 * 
	 * @param period The data period.
	 */
	public void setPeriod(Period period) {
		this.period = period;
	}

	/**
	 * Returns the offer side or null if no applicable.
	 * 
	 * @return The offer side or null if no applicable.
	 */
	public OfferSide getOfferSide() {
		return offerSide;
	}

	/**
	 * Sets the offer side.
	 * 
	 * @param offerSide The offer side.
	 */
	public void setOfferSide(OfferSide offerSide) {
		this.offerSide = offerSide;
	}

	/**
	 * Returns a boolean indicating if the underlying data is tick.
	 * 
	 * @return A boolean indicating if the underlying data is tick.
	 */
	public boolean isTick() {
		return tick;
	}

	/**
	 * Returns a boolean indicating if the underlying data are <i>Data</i> items.
	 * 
	 * @return A boolean indicating if the underlying data are <i>Data</i> items.
	 */
	public boolean isData() {
		return !isTick();
	}

	/**
	 * Returns a boolean that indicates if the underlying data is OHLCV data.
	 * 
	 * @return A boolean that indicates if the underlying data is OHLCV data.
	 */
	public boolean isOHLCV() {
		return isData() && ohlcv;
	}

	/**
	 * Sets a boolean indicating if the underlying data is tick.
	 * 
	 * @param tick A boolean indicating if the underlying data is tick.
	 */
	public void setTick(boolean tick) {
		this.tick = tick;
	}

	/**
	 * Sets a boolean indicating if the underlying data are <i>Data</i> items.
	 * 
	 * @param data A boolean indicating if the underlying data are <i>Data</i> items.
	 */
	public void setData(boolean data) {
		this.tick = !data;
	}

	/**
	 * Set if the underlying data is OHLCV data.
	 * 
	 * @param ohlcv A boolean that indicates if the underlying data is OHLCV data.
	 */
	public void setOHLCV(boolean ohlcv) {
		this.tick = false;
		this.ohlcv = ohlcv;
		setSize(5);
	}

	/**
	 * Returns the size or number of double values.
	 * 
	 * @return The size or number of double values.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns the chunk size or size of each data block or record.
	 * 
	 * @return The chunk size or size of each data block or record.
	 */
	public int getChunkSize() {
		int chunkSize = 0;
		// 8 bytes for the long that indicates the time.
		chunkSize += 8;
		// 8 bytes for each data value indicated by the size.
		chunkSize += (8 * size);
		// 1 byte for the valid flag in case of non tick data.
		if (!tick) {
			chunkSize++;
		}
		return chunkSize;
	}

	/**
	 * Sets the size or number of double values.
	 * 
	 * @param size The size or number of double values.
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Validates that this data file descriptor is configurated correctly.
	 * 
	 * @throws IllegalStateException
	 */
	public void validate() throws IllegalStateException {
		if (getSize() <= 0) {
			throw new IllegalStateException("The size must me greaterthan zero: " + getSize());
		}
		if (isTick() && getSize() != 4) {
			throw new IllegalStateException("Invalid size for tick data: " + getSize());
		}
		if (isTick() && getPeriod() != null) {
			throw new IllegalStateException("Tick data does not have period.");
		}
		if (isTick() && getOfferSide() != null) {
			throw new IllegalStateException("Tick data does not have offer side.");
		}
		if (isTick() && isOHLCV()) {
			throw new IllegalStateException("Tick and OHLCV data types are not both possible.");
		}
	}

	public String toXML() {
		StringBuilder b = new StringBuilder();
		// XML declaration.
		b.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		b.append("\n");
		
		// Descriptor
		b.append("<descriptor>");
		b.append("\n");

		// Instrument element.
		if (instrument != null) {
			b.append("\t");
			b.append("<instrument");
			b.append(" id=\"" + instrument.getId() + "\"");
			b.append(" description=\"" + instrument.getDescription() + "\"");
			b.append(" pip-value=\"" + instrument.getPipValueAsBigDecimal().toPlainString() + "\"");
			b.append(" pip-scale=\"" + instrument.getPipScale() + "\"");
			b.append(" tick-value=\"" + instrument.getTickValueAsBigDecimal().toPlainString() + "\"");
			b.append(" tick-scale=\"" + instrument.getTickScale() + "\"");
			b.append(" volume-scale=\"" + instrument.getVolumeScale() + "\"");
			b.append(" primary-currency=\"" + instrument.getPrimaryCurrency().getCurrencyCode() + "\"");
			b.append(" secondary-currency=\"" + instrument.getSecondaryCurrency().getCurrencyCode() + "\"");
			b.append("/>");
			b.append("\n");
		}

		// Period element.
		if (period != null) {
			b.append("\t");
			b.append("<period");
			b.append(" unit=\"" + period.getUnit().name() + "\"");
			b.append(" size=\"" + period.getSize() + "\"");
			b.append("/>");
			b.append("\n");
		}

		// Offer side element.
		if (offerSide != null) {
			b.append("\t");
			b.append("<offer-side value=\"" + offerSide.name() + "\"/>");
			b.append("\n");
		}

		// Tick/data flag.
		b.append("\t");
		b.append("<tick value=\"" + Boolean.toString(tick) + "\"/>");
		b.append("\n");

		// OHLCV/data flag.
		b.append("\t");
		b.append("<ohlcv value=\"" + Boolean.toString(ohlcv) + "\"/>");
		b.append("\n");

		// Size.
		b.append("\t");
		b.append("<size value=\"" + Integer.toString(size) + "\"/>");
		b.append("\n");
		
		// End descriptor.
		b.append("</descriptor>");
		
		return b.toString();
	}

	/**
	 * Saves this data descriptor to a file, as a rule an .xml file.
	 * 
	 * @param file The file to save the descriptor.
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public void toFile(File file) throws IllegalStateException, IOException {

		// Validate the descriptor.
		validate();

		// Save it.
		FileWriter fw = new FileWriter(file);
		fw.write(toXML());
		fw.close();
	}

	/**
	 * Restore this data descriptor from an XML file.
	 * 
	 * @param file The XML file.
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public void fromFile(File file) 
		throws IllegalStateException, IOException, SAXException, ParserConfigurationException {
		Parser parser = new Parser();
		parser.parse(file, new Handler());
		validate();
	}
}
