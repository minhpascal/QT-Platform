/**
 * 
 */
package incubator.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * A data manager for the persistence of <i>Data</i> or <i>Tick</i> items. Although, as a general rule, it is a good
 * practice to give data files names sufficently descriptive, like 'EURUSD-60min.dat', a <i>Descriptor</i> is used to
 * kwnow the content and format of the data file.
 * 
 * @author Miquel Sas
 */
public class ChunkFile {

	/**
	 * Returns a suitable descriptor file name given the data file name.
	 * 
	 * @param dataFileName The data file name.
	 * @return The descriptor file name.
	 */
	public static String getDescriptorFileName(String dataFileName) {
		int index = dataFileName.lastIndexOf('.');
		if (index == -1) {
			throw new IllegalStateException("Invalid data file name.");
		}
		String rootName = dataFileName.substring(0, index);
		// The descriptor file name.
		String descriptorFileName = rootName + ".xml";
		return descriptorFileName;
	}

	/**
	 * A data structure to handle chuck data.
	 */
	public class Chunk {
		public long time;
		public double[] values;
		public boolean valid;
	}

	/**
	 * The byte stored at the end of the chunk for <i>Data</i> and <i>OHLCV</i> data for <i>true</i>.
	 */
	private static final byte byteTrue = 1;
	/**
	 * The byte stored at the end of the chunk for <i>Data</i> and <i>OHLCV</i> data for <i>false</i>.
	 */
	private static final byte byteFalse = 0;

	/**
	 * The data file.
	 */
	private File dataFile;
	/**
	 * Th descriptor file.
	 */
	private File descriptorFile;
	/**
	 * The descriptor.
	 */
	private Descriptor descriptor;
	/**
	 * The byte buffer to read chunks of data.
	 */
	private ByteBuffer chunkBuffer;
	/**
	 * The file channel.
	 */
	private FileChannel channel;
	/**
	 * A boolean that indicates if the valid flag of the chunk should be read/saved, mainly for performance avoiding to
	 * ask the descriptor at every operation.
	 */
	private boolean validFlag = false;
	/**
	 * A boolean that indicates if the channel is closed.
	 */
	private boolean closed;

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
	public ChunkFile(File descriptorFile, File dataFile)
		throws IllegalStateException, IOException, SAXException, ParserConfigurationException {
		super();
		this.descriptorFile = descriptorFile;
		this.dataFile = dataFile;

		// Initialize the descriptor.
		descriptor = new Descriptor();
		descriptor.fromFile(descriptorFile);

		// Initialize the chunk buffer and valid flag.
		chunkBuffer = ByteBuffer.allocate(descriptor.getChunkSize());
		validFlag = descriptor.isData();
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
	public ChunkFile(Descriptor descriptor, File dataFile) throws IllegalStateException, IOException {
		super();
		this.descriptor = descriptor;
		this.dataFile = dataFile;

		// The data file name.
		String dataFileName = dataFile.getName();
		String descriptorFileName = getDescriptorFileName(dataFileName);
		this.descriptorFile = new File(dataFile.getParentFile(), descriptorFileName);
		// Create the descriptor file.
		this.descriptor.toFile(descriptorFile);

		// Initialize the chunk buffer and valid flag.
		chunkBuffer = ByteBuffer.allocate(descriptor.getChunkSize());
		validFlag = descriptor.isData();
	}

	/**
	 * Open the file for read/write operations.
	 * 
	 * @throws IOException
	 */
	public void open() throws IOException {
		// Open the fle channel.
		OpenOption CREATE = StandardOpenOption.CREATE;
		OpenOption READ = StandardOpenOption.READ;
		OpenOption WRITE = StandardOpenOption.WRITE;
		channel = FileChannel.open(dataFile.toPath(), CREATE, READ, WRITE);
		// The channel is open (not closed).
		closed = false;
	}

	/**
	 * Closes the file (the underlying file channel).
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (closed) {
			throw new UnsupportedOperationException("The file channel is already closed.");
		}
		channel.close();
		closed = true;
	}

	/**
	 * Truncate the file so it has a zero length.
	 * 
	 * @throws IOException
	 */
	public void truncate() throws IOException {
		channel.truncate(0);
	}

	/**
	 * Returns a boolean indicating if the file (channel) is closed.
	 * 
	 * @return A boolean indicating if the file (channel) is closed.
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * Returns a long indicating the size or number of data chunks in the underlying file.
	 * 
	 * @return A long indicating the size or number of data chunks in the underlying file.
	 * @throws IOException
	 */
	public long size() throws IOException {
		int chunkSize = descriptor.getChunkSize();
		long fileSize = (isClosed() ? dataFile.length() : channel.size());
		return fileSize / chunkSize;
	}

	/**
	 * Returns the descriptor.
	 * 
	 * @return The descriptor.
	 */
	protected Descriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * Read a chunk of data and move the position to the next chunk, or null if at the end of the file.
	 * 
	 * @return The chunk of data.
	 * @throws IOException
	 */
	protected Chunk readChunk() throws IOException {
		chunkBuffer.rewind();
		int nread;
		do {
			nread = channel.read(chunkBuffer);
		} while (nread != -1 && chunkBuffer.hasRemaining());
		if (nread == -1) {
			return null;
		}
		chunkBuffer.rewind();
		Chunk chunk = new Chunk();
		chunk.time = chunkBuffer.getLong();
		int size = descriptor.getSize();
		chunk.values = new double[size];
		for (int i = 0; i < size; i++) {
			chunk.values[i] = chunkBuffer.getDouble();
		}
		if (validFlag) {
			byte b = chunkBuffer.get();
			chunk.valid = (b == byteTrue ? true : false);
		}
		return chunk;
	}

	/**
	 * Returns the first chunk of the file.
	 * 
	 * @return The first chunk of the file.
	 * @throws IOException
	 */
	protected Chunk firstChunk() throws IOException {
		// Move to the begining of the file.
		channel.position(0);
		// Return the chunk.
		return readChunk();
	}

	/**
	 * Returns the last chunk of the file.
	 * 
	 * @return The last chunk of the file.
	 * @throws IOException
	 */
	protected Chunk lastChunk() throws IOException {
		// Move to the start of last chunk of the file.
		int chunkSize = descriptor.getChunkSize();
		channel.position(channel.size() - chunkSize);
		// Return the chunk.
		return readChunk();
	}

	/**
	 * Returns the chunk which time is greater than or equal to the argument time.
	 * 
	 * @param time The time to seek.
	 * @return The chunk which time is greater than or equal to the argument time.
	 * @throws IOException
	 */
	protected Chunk seekChunk(long time) throws IOException {
		long firstTime = firstChunk().time;
		long lastTime = lastChunk().time;

		// If out of range return null.
		if (time < firstTime || time > lastTime) {
			return null;
		}

		// Factor
		double factor = ((double)time - (double)firstTime) / ((double)lastTime - (double)firstTime);
		long element = (long) ((size() * factor)) - 10;
		if (element < 10) {
			element = 0;
		}
		
		long position = element * descriptor.getChunkSize();

		// Do seek.
		channel.position(position);
		while (true) {
			Chunk chunk = readChunk();
			if (chunk == null) {
				break;
			}
			long chunkTime = chunk.time;
			if (chunkTime >= time) {
				return chunk;
			}
		}

		return null;
	}

	/**
	 * Writes a chunk of data from the current position.
	 * 
	 * @param chunk The chunk to write.
	 * @throws IOException
	 */
	protected void writeChunk(Chunk chunk) throws IOException {
		chunkBuffer.rewind();
		// Write the data into the buffer.
		chunkBuffer.putLong(chunk.time);
		for (double value : chunk.values) {
			chunkBuffer.putDouble(value);
		}
		if (validFlag) {
			chunkBuffer.put(chunk.valid ? byteTrue : byteFalse);
		}
		// Write the chunk to the channel.
		chunkBuffer.rewind();
		while (chunkBuffer.hasRemaining()) {
			channel.write(chunkBuffer);
		}
	}

	/**
	 * Adds a chunk of data at the end of the file.
	 * 
	 * @param chunk The chunk to write.
	 * @throws IOException
	 */
	protected void addChunk(Chunk chunk) throws IOException {
		// Move to the end of the file.
		channel.position(channel.size());
		// Do write.
		writeChunk(chunk);
	}
}
