/**
 * 
 */
package incubator.persistence;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.qtplaf.library.trading.data.Tick;
import com.qtplaf.library.trading.data.Tick.Pair;

/**
 * A <i>Tick</i> data file implementation.
 * 
 * @author Miquel Sas
 */
public class TickFile extends ChunkFile {

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
	public TickFile(File descriptorFile, File dataFile) throws IllegalStateException, IOException, SAXException, ParserConfigurationException {
		super(descriptorFile, dataFile);
		// Validate that the descriptor is tick.
		if (!getDescriptor().isTick()) {
			throw new IllegalStateException("Tick file descriptor expected.");
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
	public TickFile(Descriptor descriptor, File dataFile) throws IllegalStateException, IOException {
		super(descriptor, dataFile);
		// Validate that the descriptor is tick.
		if (!getDescriptor().isTick()) {
			throw new IllegalStateException("Tick file descriptor expected.");
		}
	}

	/**
	 * Read a tick of data and move the position to the next tick.
	 * 
	 * @return The tick of data.
	 * @throws IOException
	 */
	public Tick read() throws IOException {
		Chunk chunk = readChunk();
		if (chunk == null) {
			return null;
		}
		return fromChunk(chunk);
	}

	/**
	 * Returns the first tick element and moves the position to the next element.
	 * 
	 * @return The first tick element and moves the position to the next element.
	 * @throws IOException
	 */
	public Tick first() throws IOException {
		Chunk chunk = firstChunk();
		if (chunk == null) {
			return null;
		}
		return fromChunk(chunk);
	}

	/**
	 * Returns the last tick element and moves the position to the next element.
	 * 
	 * @return The last tick element and moves the position to the next element.
	 * @throws IOException
	 */
	public Tick last() throws IOException {
		Chunk chunk = lastChunk();
		if (chunk == null) {
			return null;
		}
		return fromChunk(chunk);
	}

	/**
	 * Returns the tick element which time is greater than or equal to the argument time.
	 * 
	 * @param time The seek time.
	 * @return The tick element which time is greater than or equal to the argument time.
	 * @throws IOException
	 */
	public Tick seek(long time) throws IOException {
		Chunk chunk = seekChunk(time);
		if (chunk == null) {
			return null;
		}
		return fromChunk(chunk);
	}

	/**
	 * Writes a tick of data from the current position.
	 * 
	 * @param tick The tick of data.
	 * @throws IOException
	 */
	public void write(Tick tick) throws IOException {
		writeChunk(toChunk(tick));
	}

	/**
	 * Adds a tick of data at the end of the file.
	 * 
	 * @param tick The tick of data.
	 * @throws IOException
	 */
	public void add(Tick tick) throws IOException {
		addChunk(toChunk(tick));
	}

	/**
	 * Returns a chunk of data given the tick.
	 * 
	 * @param tick The tick.
	 * @return The chunk of data.
	 */
	private Chunk toChunk(Tick tick) {
		Pair ask = tick.getAsk();
		Pair bid = tick.getBid();
		Chunk chunk = new Chunk();
		chunk.time = tick.getTime();
		chunk.values = new double[] { ask.getValue(), ask.getVolume(), bid.getValue(), bid.getVolume() };
		return chunk;
	}

	/**
	 * Returns a tick from a chunk of data.
	 * 
	 * @param chunk The chunk of tick data.
	 * @return The tick.
	 */
	private Tick fromChunk(Chunk chunk) {
		Tick tick = new Tick();
		tick.setTime(chunk.time);
		tick.addAsk(chunk.values[0], chunk.values[1]);
		tick.addBid(chunk.values[2], chunk.values[3]);
		return tick;
	}

}
