/**
 * 
 */
package incubator.persistence;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.qtplaf.library.trading.data.Data;

/**
 * A <i>Data</i> file implementation.
 * 
 * @author Miquel Sas
 */
public class DataFile extends ChunkFile {

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
	public DataFile(File descriptorFile, File dataFile) throws IllegalStateException, IOException, SAXException, ParserConfigurationException {
		super(descriptorFile, dataFile);
		// Validate that the descriptor is data type.
		if (!getDescriptor().isData() || getDescriptor().isOHLCV()) {
			throw new IllegalStateException("Data file descriptor expected.");
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
	public DataFile(Descriptor descriptor, File dataFile) throws IllegalStateException, IOException {
		super(descriptor, dataFile);
		// Validate that the descriptor is data type.
		if (!getDescriptor().isData() || getDescriptor().isOHLCV()) {
			throw new IllegalStateException("Data file descriptor expected.");
		}
	}

	/**
	 * Read a data element and moves the position to the next element.
	 * 
	 * @return The data.
	 * @throws IOException
	 */
	public Data read() throws IOException {
		Chunk chunk = readChunk();
		if (chunk == null) {
			return null;
		}
		return fromChunk(chunk);
	}

	/**
	 * Returns the first data element and moves the position to the next element.
	 * 
	 * @return The first data element and moves the position to the next element.
	 * @throws IOException
	 */
	public Data first() throws IOException {
		Chunk chunk = firstChunk();
		if (chunk == null) {
			return null;
		}
		return fromChunk(chunk);
	}

	/**
	 * Returns the last data element and moves the position to the next element.
	 * 
	 * @return The last data element and moves the position to the next element.
	 * @throws IOException
	 */
	public Data last() throws IOException {
		Chunk chunk = lastChunk();
		if (chunk == null) {
			return null;
		}
		return fromChunk(chunk);
	}

	/**
	 * Returns the data element which time is greater than or equal to the argument time.
	 * 
	 * @param time The seek time.
	 * @return The data element which time is greater than or equal to the argument time.
	 * @throws IOException
	 */
	public Data seek(long time) throws IOException {
		Chunk chunk = seekChunk(time);
		if (chunk == null) {
			return null;
		}
		return fromChunk(chunk);
	}

	/**
	 * Writes a data element at the current position.
	 * 
	 * @param data The data element.
	 * @throws IOException
	 */
	public void write(Data data) throws IOException {
		writeChunk(toChunk(data));
	}

	/**
	 * Adds a data element at the end of the file.
	 * 
	 * @param data The data element.
	 * @throws IOException
	 */
	public void add(Data data) throws IOException {
		addChunk(toChunk(data));
	}

	/**
	 * Returns a chunk of data given the data.
	 * 
	 * @param data The data.
	 * @return The chunk of data.
	 */
	private Chunk toChunk(Data data) {
		Chunk chunk = new Chunk();
		chunk.time = data.getTime();
		chunk.values = data.getData();
		chunk.valid = data.isValid();
		return chunk;
	}

	/**
	 * Returns a <i>Data</i> from a chunk of data.
	 * 
	 * @param chunk The chunk of data.
	 * @return The <i>Data</i> element.
	 */
	private Data fromChunk(Chunk chunk) {
		Data data = new Data();
		data.setTime(chunk.time);
		data.setValues(chunk.values);
		data.setValid(chunk.valid);
		return data;
	}

}
