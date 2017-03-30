/**
 * 
 */
package incubator.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.qtplaf.library.trading.data.Filter;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.Unit;
import com.qtplaf.library.trading.server.OfferSide;
import com.qtplaf.library.util.xml.Parser;
import com.qtplaf.library.util.xml.ParserHandler;

/**
 * Chunk data file descriptor. Chunk data files store data in fixed length records described by the list of types. Each
 * particular implementation will be reponsible for decodifying the chun of data.
 * <p>
 * Strings must be fixed length and are defined by a list of characters.
 * 
 * @author Miquel Sas
 */
public class ChunkDescriptor {

	/**
	 * Enumerates the supported types.
	 */
	public enum Type {
		Boolean,
		Byte,
		Character,
		Double,
		Float,
		Integer,
		Long,
		Short,
		String;
	}

	/**
	 * Definition of each part or block of a chunk.
	 */
	public class Block {
		/** Type. */
		private Type type;
		/** Internal size in bytes. */
		private int size;

		/** Constructor. */
		public Block(Type type, int size) {
			this.type = type;
			this.size = size;
		}

		/** Returns the type. */
		public Type getType() {
			return type;
		}

		/** Returns the size. */
		public int getSize() {
			return size;
		}
	}

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
				// element: filter
				if (path.equals("descriptor/filter")) {
					filter = Filter.valueOf(attributes.getValue("value"));
				}
				// element: offer-side
				if (path.equals("descriptor/offer-side")) {
					offerSide = OfferSide.valueOf(attributes.getValue("value"));
				}
				// element: block.
				if (path.equals("descriptor/blocks/block")) {
					Type type = Type.valueOf(attributes.getValue("type"));
					int size = Integer.parseInt(attributes.getValue("size"));
					switch (type) {
					case Boolean:
						addBoolean();
						break;
					case Byte:
						addByte();
						break;
					case Character:
						addCharacter();
						break;
					case Double:
						addDouble();
						break;
					case Float:
						addFloat();
						break;
					case Integer:
						addInteger();
						break;
					case Long:
						addLong();
						break;
					case Short:
						addShort();
						break;
					case String:
						addString(size / 2);
						break;
					}
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
	 * The filter if applicable.
	 */
	private Filter filter;
	/**
	 * The offer side, or null if the underlying data is tick or the offer side does not apply.
	 */
	private OfferSide offerSide;
	/**
	 * The list of blocks that define the values stored.
	 */
	private List<Block> blocks = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public ChunkDescriptor() {
		super();
	}

	/**
	 * Add a boolean to the values definition.
	 */
	public void addBoolean() {
		blocks.add(new Block(Type.Boolean, 1));
	}

	/**
	 * Add a byte to the values definition.
	 */
	public void addByte() {
		blocks.add(new Block(Type.Byte, 1));
	}

	/**
	 * Add a character to the values definition.
	 */
	public void addCharacter() {
		blocks.add(new Block(Type.Character, 2));
	}

	/**
	 * Add a double to the values definition.
	 */
	public void addDouble() {
		blocks.add(new Block(Type.Double, 8));
	}

	/**
	 * Add a float to the values definition.
	 */
	public void addFloat() {
		blocks.add(new Block(Type.Float, 4));
	}

	/**
	 * Add an integer to the values definition.
	 */
	public void addInteger() {
		blocks.add(new Block(Type.Integer, 4));
	}

	/**
	 * Add a long to the values definition.
	 */
	public void addLong() {
		blocks.add(new Block(Type.Long, 8));
	}

	/**
	 * Add a short to the values definition.
	 */
	public void addShort() {
		blocks.add(new Block(Type.Short, 2));
	}

	/**
	 * Add a string to the values definition.
	 * 
	 * @param length The length of the string.
	 */
	public void addString(int length) {
		blocks.add(new Block(Type.String, length * 2));
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
	 * Returns the filter or null if not applicable.
	 * 
	 * @return The filter or null if not applicable.
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * Sets the filter if not applicable.
	 * 
	 * @param filter The filter if not applicable.
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
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
	 * Returns the chunk size in bytes.
	 * 
	 * @return The chunk size in bytes.
	 */
	public int getChunkSize() {
		int size = 0;
		for (Block block : blocks) {
			size += block.getSize();
		}
		return size;
	}

	/**
	 * Returns an XML representation of this chunk descriptor.
	 * 
	 * @return An XML representation of this chunk descriptor.
	 */
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

		// Filter element.
		if (filter != null) {
			b.append("\t");
			b.append("<filter value=\"" + filter.name() + "\"/>");
			b.append("\n");
		}

		// List of blocks.
		b.append("\t");
		b.append("<blocks>");
		for (Block block : blocks) {
			b.append("\t");
			b.append("<block type=\"" + block.getType().name() + "\" size=\"" + block.getSize() + "\"/>");
		}
		b.append("\t");
		b.append("</blocks>");

		// End descriptor.
		b.append("</descriptor>");

		return b.toString();
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
	}
}
