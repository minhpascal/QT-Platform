/**
 * 
 */
package incubator.persistence;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.OfferSide;

/**
 * An <i>OHLCV</i> file implementation.
 * 
 * @author Miquel Sas
 */
public class OHLCVFile extends ChunkFile {

	/**
	 * Returns a standard file name for the arguments.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The standard file name.
	 */
	public static String getFileName(Instrument instrument, Period period) {
		return getFileName(instrument, period, null);
	}

	/**
	 * Returns a standard file name for the arguments.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param offerSide The offer side.
	 * @return The standard file name.
	 */
	public static String getFileName(Instrument instrument, Period period, OfferSide offerSide) {
		StringBuilder b = new StringBuilder();
		b.append(instrument.getId());
		b.append("-");
		b.append(period.getSize());
		b.append("-");
		b.append(period.getUnit().name());
		if (offerSide != null) {
			b.append("-");
			b.append(offerSide.name());
		}
		b.append(".dat");
		return b.toString();
	}

	/**
	 * Constructor aimed for reading/writing an already created pair of descriptor/data files.
	 * 
	 * @param descriptorFile The descriptor file.
	 * @param dataFile Tha data file.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public OHLCVFile(File descriptorFile, File dataFile) throws IllegalStateException, IOException, SAXException, ParserConfigurationException {
		super(descriptorFile, dataFile);
		// Validate that the descriptor is data type.
		if (!getDescriptor().isOHLCV()) {
			throw new IllegalStateException("OHLCV file descriptor expected.");
		}
	}

	/**
	 * Constructor aimed to initialize the descriptor and data files, for further reading/writing. The descriptor file
	 * is created using the data file name with an XML extension.
	 * 
	 * @param descriptor The data file descriptor definition.
	 * @param dataFile The data file.
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public OHLCVFile(Descriptor descriptor, File dataFile) throws IllegalStateException, IOException {
		super(descriptor, dataFile);
		// Validate that the descriptor is data type.
		if (!getDescriptor().isOHLCV()) {
			throw new IllegalStateException("OHLCV file descriptor expected.");
		}
	}

	/**
	 * Read an OHLCV element and moves the position to the next element.
	 * 
	 * @return The OHLCV.
	 * @throws IOException
	 */
	public OHLCV read() throws IOException {
		Chunk chunk = readChunk();
		if (chunk == null) {
			return null;
		}
		return fromChunk(chunk);
	}

	/**
	 * Returns the first OHLCV element and moves the position to the next element.
	 * 
	 * @return The first OHLCV element and moves the position to the next element.
	 * @throws IOException
	 */
	public OHLCV first() throws IOException {
		Chunk chunk = firstChunk();
		if (chunk == null) {
			return null;
		}
		return fromChunk(chunk);
	}

	/**
	 * Returns the last OHLCV element and moves the position to the next element.
	 * 
	 * @return The last OHLCV element and moves the position to the next element.
	 * @throws IOException
	 */
	public OHLCV last() throws IOException {
		Chunk chunk = lastChunk();
		if (chunk == null) {
			return null;
		}
		return fromChunk(chunk);
	}

	/**
	 * Returns the OHLCV element which time is greater than or equal to the argument time.
	 * 
	 * @param time The seek time.
	 * @return The OHLCV element which time is greater than or equal to the argument time.
	 * @throws IOException
	 */
	public OHLCV seek(long time) throws IOException {
		Chunk chunk = seekChunk(time);
		if (chunk == null) {
			return null;
		}
		return fromChunk(chunk);
	}

	/**
	 * Writes an OHLCV element at the current position.
	 * 
	 * @param ohlcv The OHLCV element.
	 * @throws IOException
	 */
	public void write(OHLCV ohlcv) throws IOException {
		writeChunk(toChunk(ohlcv));
	}

	/**
	 * Adds an OHLCV element at the end of the file.
	 * 
	 * @param ohlcv The OHLCV element.
	 * @throws IOException
	 */
	public void add(OHLCV ohlcv) throws IOException {
		addChunk(toChunk(ohlcv));
	}

	/**
	 * Returns a chunk of data given the OHLCV.
	 * 
	 * @param ohlcv The data.
	 * @return The chunk of data.
	 */
	private Chunk toChunk(OHLCV ohlcv) {
		Chunk chunk = new Chunk();
		chunk.time = ohlcv.getTime();
		chunk.values = ohlcv.getData();
		chunk.valid = ohlcv.isValid();
		return chunk;
	}

	/**
	 * Returns an <i>OHLCV</i> from a chunk of data.
	 * 
	 * @param chunk The chunk of data.
	 * @return The <i>OHLCV</i> element.
	 */
	private OHLCV fromChunk(Chunk chunk) {
		OHLCV ohlcv = new OHLCV();
		ohlcv.setTime(chunk.time);
		ohlcv.setValues(chunk.values);
		ohlcv.setValid(chunk.valid);
		return ohlcv;
	}

}
