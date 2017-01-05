/**
 * 
 */
package com.qtplaf.platform.database.tables;

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Types;

/**
 * Downloaded and analyzed instruments.
 * 
 * @author Miquel Sas
 */
public class Instruments extends Table {

	/**
	 * Constructor.
	 */
	public Instruments() {
		super();
		configure();
	}
	/**
	 * Configure this table definition.
	 */
	private void configure() {
		
		setName("INSTRUMENTS");
		setAlias("INSTRUMENTS");
		setPersistent(true);
		setPersistentConstraints(false);
		
		Field code = new Field();
		code.setName("CODE");
		code.setAlias("CODE");
		code.setType(Types.String);
		code.setLength(20);
		code.setPrimaryKey(true);
		addField(code);
		
		Field description = new Field();
		description.setName("DESCRIPTION");
		description.setAlias("DESCRIPTION");
		description.setType(Types.String);
		description.setLength(80);
		addField(description);
		
		Field pipValue = new Field();
		pipValue.setName("PIP_VALUE");
		pipValue.setAlias("PIP_VALUE");
		pipValue.setType(Types.Double);
		addField(pipValue);
		
		Field pipScale = new Field();
		pipScale.setName("PIP_SCALE");
		pipScale.setAlias("PIP_SCALE");
		pipScale.setType(Types.Integer);
		pipScale.setLength(2);
		addField(pipScale);
		
	}

}
