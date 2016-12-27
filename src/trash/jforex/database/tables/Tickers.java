/**
 * 
 */
package trash.jforex.database.tables;

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Types;

/**
 * A table with downloaded instrument (ticker) data from Dukascopy.
 * 
 * @author Miquel Sas
 */
public class Tickers extends Table {
	
	/**
	 * Constructor.
	 */
	public Tickers() {
		super();
		configure();
	}

	/**
	 * Configure this table definition.
	 */
	private void configure() {
		
		setName("TICKERS");
		setAlias("TICKERS");
		
		Field instrumentCode = new Field();
		instrumentCode.setName("INSTRUMENT_CODE");
		instrumentCode.setAlias("INSTRUMENT_CODE");
		instrumentCode.setType(Types.String);
		instrumentCode.setLength(20);
		instrumentCode.setPrimaryKey(true);
		addField(instrumentCode);
		
		Field period = new Field();
		period.setName("PERIOD");
		period.setAlias("PERIOD");
		period.setTitle("Period or time frame");
		period.setType(Types.String);
		period.setLength(20);
		period.setPrimaryKey(true);
		addField(period);
		
		Field timestamp = new Field();
		timestamp.setName("TIMESTAMP");
		timestamp.setAlias("TIMESTAMP");
		timestamp.setTitle("Time stamp");
		timestamp.setType(Types.Long);
		timestamp.setPrimaryKey(true);
		addField(timestamp);
		
		Field open = new Field();
		open.setName("VOPEN");
		open.setAlias("VOPEN");
		open.setTitle("Open value");
		open.setType(Types.Double);
		addField(open);
		
		Field high = new Field();
		high.setName("VHIGH");
		high.setAlias("VHIGH");
		high.setTitle("High value");
		high.setType(Types.Double);
		addField(high);
		
		Field low = new Field();
		low.setName("VLOW");
		low.setAlias("VLOW");
		low.setTitle("Low value");
		low.setType(Types.Double);
		addField(low);
		
		Field close = new Field();
		close.setName("VCLOSE");
		close.setAlias("VCLOSE");
		close.setTitle("Close value");
		close.setType(Types.Double);
		addField(close);
		
		Field volume = new Field();
		volume.setName("VOLUME");
		volume.setAlias("VOLUME");
		volume.setTitle("Volume value");
		volume.setType(Types.Double);
		addField(volume);
		
	}
}
