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
package com.qtplaf.library.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.qtplaf.library.database.rdbms.DBEngine;
import com.qtplaf.library.database.rdbms.DBUtils;

/**
 * A manager of the underlying database engine JDBC meta data.
 * 
 * @author Miquel Sas
 */
public class MetaData {
	
	public static final String TableCatalog = "TABLE_CAT";
	public static final String TableSchema = "TABLE_SCHEM";
	public static final String TableName = "TABLE_NAME";
	public static final String ColumnName = "COLUMN_NAME";
	public static final String DataType = "DATA_TYPE";
	public static final String TypeName = "TYPE_NAME";

	/**
	 * Helper to rapidly create fields.
	 */
	private static Field createField(
		String name, String description, Types type, int length, int decimals, boolean primaryKey) {
		Field field = new Field();
		field.setName(name);
		field.setAlias(name);
		field.setDescription(description);
		field.setType(type);
		field.setLength(length);
		field.setDecimals(decimals);
		return field;
	}

	/**
	 * Returns The CATALOG INFO field list.
	 * 
	 * @return The CATALOG INFO field list
	 */
	public static FieldList getFieldListCatalogInfo() {
		FieldList fieldList = new FieldList();
		fieldList.addField(createField(TableCatalog, "Catalog", Types.String, 40, 0, true));
		return fieldList;
	}

	/**
	 * Returns the COLUMN INFO field list.
	 * 
	 * @return The COLUMN INFO field list.
	 */
	public static FieldList getFieldListColumnInfo() {
		FieldList fieldList = new FieldList();
		fieldList.addField(createField(TableCatalog, "Catalog", Types.String, 40, 0, true));
		fieldList.addField(createField(TableSchema, "Schema", Types.String, 40, 0, true));
		fieldList.addField(createField(TableName, "Table", Types.String, 40, 0, false));
		fieldList.addField(createField(ColumnName, "Column", Types.String, 40, 0, false));
		fieldList.addField(createField(DataType, "Data type", Types.Integer, 0, 0, false));
		fieldList.addField(createField(TypeName, "Type name", Types.String, 20, 0, false));
		fieldList.addField(createField("COLUMN_SIZE", "Column size", Types.Long, 0, 0, false));
		fieldList.addField(createField("BUFFER_LENGTH", "Buffer length", Types.Integer, 0, 0, false));
		fieldList.addField(createField("DECIMAL_DIGITS", "Decimal digits", Types.Integer, 0, 0, false));
		fieldList.addField(createField("NUM_PREC_RADIX", "Num prec radix", Types.Integer, 0, 0, false));
		fieldList.addField(createField("NULLABLE", "Nullable", Types.Integer, 0, 0, false));
		fieldList.addField(createField("REMARKS", "Remarks", Types.String, 128, 0, false));
		fieldList.addField(createField("COLUMN_DEF", "Column def", Types.String, 128, 0, false));
		fieldList.addField(createField("SQL_DATA_TYPE", "SQL data type", Types.Integer, 0, 0, false));
		fieldList.addField(createField("SQL_DATETIME_SUB", "SQL date time sub", Types.Integer, 0, 0, false));
		fieldList.addField(createField("CHAR_OCTET_LENGTH", "Char octet length", Types.Long, 0, 0, false));
		fieldList.addField(createField("ORDINAL_POSITION", "Ordinal position", Types.Integer, 0, 0, true));
		fieldList.addField(createField("IS_NULLABLE", "Is nullable", Types.String, 5, 0, false));
		fieldList.addField(createField("DATA_TYPE_NAME", "Data type name", Types.String, 20, 0, false));
		return fieldList;
	}

	/**
	 * Returns the INDEX INFO field list.
	 * 
	 * @return The INDEX INFO field list
	 */
	public static FieldList getFieldListIndexInfo() {
		FieldList fieldList = new FieldList();
		fieldList.addField(createField(TableCatalog, "Catalog", Types.String, 40, 0, true));
		fieldList.addField(createField(TableSchema, "Schema", Types.String, 40, 0, true));
		fieldList.addField(createField(TableName, "Table", Types.String, 40, 0, true));
		fieldList.addField(createField("NON_UNIQUE", "Non unique", Types.Boolean, 1, 0, false));
		fieldList.addField(createField("INDEX_QUALIFIER", "Index qualifier", Types.String, 40, 0, false));
		fieldList.addField(createField("INDEX_NAME", "Index name", Types.String, 40, 0, false));
		fieldList.addField(createField("TYPE", "Index type", Types.Integer, 40, 0, false));
		fieldList.addField(createField("ORDINAL_POSITION", "Ordinal position", Types.Integer, 0, 0, false));
		fieldList.addField(createField(ColumnName, "Column name", Types.String, 0, 0, false));
		fieldList.addField(createField("ASC_OR_DESC", "Asc/desc", Types.String, 2, 0, false));
		fieldList.addField(createField("CARDINALITY", "Cardinality", Types.Integer, 0, 0, false));
		fieldList.addField(createField("PAGES", "Pages", Types.Integer, 0, 0, false));
		fieldList.addField(createField("FILTER_CONDITION", "Filter condition", Types.String, 128, 0, false));
		fieldList.addField(createField("INDEX_TYPE_DESC", "Index type desc", Types.String, 20, 0, false));
		return fieldList;
	}

	/**
	 * Returns the TABLE INFO field list.
	 * 
	 * @return The TABLE INFO field list.
	 */
	public static FieldList getFieldListTableInfo() {
		FieldList fieldList = new FieldList();
		fieldList.addField(createField(TableCatalog, "Catalog", Types.String, 40, 0, false));
		fieldList.addField(createField(TableSchema, "Schema", Types.String, 40, 0, false));
		fieldList.addField(createField(TableName, "Table", Types.String, 40, 0, false));
		fieldList.addField(createField("TABLE_TYPE", "Table type", Types.String, 40, 0, false));
		fieldList.addField(createField("REMARKS", "Remarks", Types.String, 128, 0, false));
		return fieldList;
	}

	/**
	 * Returns the PRIMARY KEY INFO field list.
	 * 
	 * @return The PRIMARY KEY INFO field list.
	 */
	public static FieldList getFieldListPrimaryKeyInfo() {
		FieldList fieldList = new FieldList();
		fieldList.addField(createField(TableCatalog, "Catalog", Types.String, 40, 0, true));
		fieldList.addField(createField(TableSchema, "Schema", Types.String, 40, 0, true));
		fieldList.addField(createField(TableName, "Table", Types.String, 40, 0, true));
		fieldList.addField(createField(ColumnName, "Column", Types.String, 40, 0, true));
		fieldList.addField(createField("KEY_SEQ", "Key seq", Types.Integer, 4, 0, true));
		fieldList.addField(createField("PK_NAME", "PK name", Types.String, 40, 0, false));
		return fieldList;
	}

	/**
	 * Returns the SCHEMA INFO field list.
	 * 
	 * @return The SCHEMA INFO field list.
	 */
	public static FieldList getFieldListSchemaInfo() {
		FieldList fieldList = new FieldList();
		fieldList.addField(createField(TableSchema, "Schema", Types.String, 40, 0, true));
		return fieldList;
	}

	/**
	 * Returns the TYPE INFO field list.
	 * 
	 * @return The TYPE INFO field list.
	 */
	public static FieldList getFieldListTypeInfo() {
		FieldList fieldList = new FieldList();
		fieldList.addField(createField(TypeName, "Type name", Types.String, 20, 0, true));
		fieldList.addField(createField(DataType, "Data type", Types.Integer, 0, 0, false));
		fieldList.addField(createField("PRECISION", "Precision", Types.Long, 0, 0, false));
		fieldList.addField(createField("LITERAL_PREFIX", "Literal prefix", Types.String, 5, 0, false));
		fieldList.addField(createField("LITERAL_SUFFIX", "Literal suffix", Types.String, 5, 0, false));
		fieldList.addField(createField("CREATE_PARAMS", "Create params", Types.String, 20, 0, false));
		fieldList.addField(createField("NULLABLE", "Nullable", Types.Integer, 0, 0, false));
		fieldList.addField(createField("CASE_SENSITIVE", "Case sensitive", Types.Boolean, 0, 0, false));
		fieldList.addField(createField("SEARCHABLE", "Searcheable", Types.Integer, 0, 0, false));
		fieldList.addField(createField("UNSIGNED_ATTRIBUTE", "Unsigned attribute", Types.Boolean, 0, 0, false));
		fieldList.addField(createField("FIXED_PREC_SCALE", "Fixed prec scale", Types.Boolean, 0, 0, false));
		fieldList.addField(createField("AUTO_INCREMENT", "Auto increment", Types.Boolean, 0, 0, false));
		fieldList.addField(createField("LOCAL_TYPE_NAME", "Local type name", Types.String, 20, 0, false));
		fieldList.addField(createField("MINIMUM_SCALE", "Minimum scale", Types.Integer, 0, 0, false));
		fieldList.addField(createField("MAXIMUM_SCALE", "Maximum scale", Types.Integer, 0, 0, false));
		fieldList.addField(createField("SQL_DATA_TYPE", "SQL data type", Types.Integer, 0, 0, false));
		fieldList.addField(createField("SQL_DATETIME_SUB", "SQL date time sub", Types.Integer, 0, 0, false));
		fieldList.addField(createField("NUM_PREC_RADIX", "Num prec radix", Types.Integer, 0, 0, false));
		fieldList.addField(createField("DATA_TYPE_NAME", "Data type name", Types.String, 20, 0, false));
		fieldList.addField(createField("NULLABLE_DESC", "Nullable desc", Types.String, 20, 0, false));
		return fieldList;
	}

	/**
	 * Returns the FOREIGN KEY INFO field list.
	 * 
	 * @return The FOREIGN KEY INFO field list.
	 */
	public static FieldList getFieldListForeignKeyInfo() {
		FieldList fieldList = new FieldList();
		fieldList.addField(createField("PKTABLE_CAT", "PK Table catalog", Types.String, 20, 0, true));
		fieldList.addField(createField("PKTABLE_SCHEM", "PK Table schema", Types.String, 20, 0, false));
		fieldList.addField(createField("PKTABLE_NAME", "PK Table name", Types.String, 20, 0, false));
		fieldList.addField(createField("PKCOLUMN_NAME", "PK Column name", Types.String, 20, 0, false));
		fieldList.addField(createField("FKTABLE_CAT", "FK Table catalog", Types.String, 20, 0, false));
		fieldList.addField(createField("FKTABLE_SCHEM", "FK Table schema", Types.String, 20, 0, false));
		fieldList.addField(createField("FKTABLE_NAME", "FK Table name", Types.String, 20, 0, false));
		fieldList.addField(createField("FKCOLUMN_NAME", "FK Column name", Types.String, 20, 0, false));
		fieldList.addField(createField("KEY_SEQ", "Key sequence", Types.Integer, 2, 0, false));
		fieldList.addField(createField("UPDATE_RULE", "Update rule", Types.Integer, 2, 0, false));
		fieldList.addField(createField("DELETE_RULE", "Delete rule", Types.Integer, 2, 0, false));
		fieldList.addField(createField("FK_NAME", "FK Name", Types.String, 20, 0, false));
		fieldList.addField(createField("PK_NAME", "PK Name", Types.String, 20, 0, false));
		fieldList.addField(createField("DEFERRABILITY", "Deferrability", Types.Integer, 20, 0, false));
		return fieldList;
	}

	/**
	 * The database engine.
	 */
	private DBEngine dbEngine;

	/**
	 * Constructor assigning the <i>DBEngine</i>.
	 * 
	 * @param dbEngine The <i>DBEngine</i>.
	 */
	public MetaData(DBEngine dbEngine) {
		super();
		this.dbEngine = dbEngine;
	}

	/**
	 * Reads the correspondent record set.
	 * 
	 * @param rs The JDBC result set
	 * @param fieldList The applying field list
	 * @return The record set.
	 * @throws SQLException
	 */
	private RecordSet readRecordSet(ResultSet rs, FieldList fieldList) throws SQLException {
		RecordSet recordSet = new RecordSet();
		recordSet.setFieldList(fieldList);
		while (rs.next()) {
			Record record = DBUtils.readRecord(fieldList, rs);
			recordSet.add(record);
		}
		rs.close();
		return recordSet;
	}

	/**
	 * Returns the catalogs recordset.
	 *
	 * @return The recordset.
	 * @throws SQLException
	 */
	public RecordSet getRecordSetCatalogs() throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getCatalogs();
			recordSet = readRecordSet(rs, getFieldListCatalogInfo());
			return recordSet;
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
	}

	/**
	 * Returns the schema recordset.
	 *
	 * @return The recordset.
	 * @throws SQLException
	 */
	public RecordSet getRecordSetSchemas() throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getSchemas();
			recordSet = readRecordSet(rs, getFieldListSchemaInfo());
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
		return recordSet;
	}

	/**
	 * Returns the table recordset (only TABLE types).
	 *
	 * @param schema The table schema or null
	 * @return A recordset with table definition.
	 * @throws SQLException
	 */
	public RecordSet getRecordSetTables(String schema) throws SQLException {
		return getRecordSetTables(null, schema, null, "TABLE");
	}

	/**
	 * Returns the table recordset (only TABLE types).
	 *
	 * @param schema The table schema or null
	 * @param table The table name prefix or null
	 * @return A recordset with table definition.
	 * @throws SQLException
	 */
	public RecordSet getRecordSetTables(String schema, String table) throws SQLException {
		return getRecordSetTables(null, schema, table, "TABLE");
	}

	/**
	 * Returns the table recordset.
	 *
	 * @param catalog The table catalog or null
	 * @param schema The table schema or null
	 * @param table The table name prefix or null
	 * @param types An array of possible table types
	 * @return A recordset with table definition.
	 * @throws SQLException
	 */
	public RecordSet getRecordSetTables(String catalog, String schema, String table, String... types) throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getTables(catalog, schema, table, types);
			recordSet = readRecordSet(rs, getFieldListTableInfo());
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
		return recordSet;
	}

	/**
	 * Returns a record set with information about the columns of a table.
	 *
	 * @return The record set.
	 * @param catalog The catalog name
	 * @param schema The schema name.
	 * @param table The table name.
	 * @throws SQLException
	 */
	public RecordSet getRecordSetColumns(String catalog, String schema, String table) throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getColumns(catalog, schema, table, null);
			recordSet = readRecordSet(rs, getFieldListColumnInfo());
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
		return recordSet;
	}

	/**
	 * Returns a record set with information about the indexes of a table.
	 *
	 * @return The record set.
	 * @param catalog The catalog name
	 * @param schema The schema name.
	 * @param table The table name.
	 * @throws SQLException
	 */
	public RecordSet getRecordSetIndexes(String catalog, String schema, String table) throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getIndexInfo(catalog, schema, table, false, false);
			recordSet = readRecordSet(rs, getFieldListIndexInfo());
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
		return recordSet;
	}

	/**
	 * Returns a record set with information about the primary key of a table.
	 *
	 * @param catalog
	 * @param schema
	 * @param table
	 * @return the RecordSet
	 * @throws SQLException
	 */
	public RecordSet getRecordSetPrimaryKey(String catalog, String schema, String table) throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getPrimaryKeys(catalog, schema, table);
			recordSet = readRecordSet(rs, getFieldListPrimaryKeyInfo());
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
		return recordSet;
	}

	/**
	 * Returns a record set with information about the imported keys of a table. In our db system theese are foreign
	 * keys of the argument table.
	 * 
	 * @return The record set.
	 * @param catalog The catalog name
	 * @param schema The schema name.
	 * @param table The table name.
	 * @throws SQLException
	 */
	public RecordSet getRecordSetImportedKeys(String catalog, String schema, String table) throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getImportedKeys(catalog, schema, table);
			recordSet = readRecordSet(rs, getFieldListForeignKeyInfo());
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
		return recordSet;
	}

	/**
	 * Returns a record set with information about the exported keys of a table. In our db system theese are referrers
	 * of the argument table.
	 * 
	 * @return The record set.
	 * @param catalog The catalog name
	 * @param schema The schema name.
	 * @param table The table name.
	 * @throws SQLException
	 */
	public RecordSet getRecordSetExportedKeys(String catalog, String schema, String table) throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getExportedKeys(catalog, schema, table);
			recordSet = readRecordSet(rs, getFieldListForeignKeyInfo());
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
		return recordSet;
	}

	/**
	 * Returns a record set with information about the database types.
	 *
	 * @return The record set.
	 * @throws SQLException
	 */
	public RecordSet getRecordSetTypes() throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getTypeInfo();
			recordSet = readRecordSet(rs, getFieldListTypeInfo());
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
		return recordSet;
	}
}
