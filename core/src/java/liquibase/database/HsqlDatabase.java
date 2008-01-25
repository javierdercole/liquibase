package liquibase.database;

import liquibase.database.sql.RawSqlStatement;
import liquibase.database.sql.SqlStatement;
import liquibase.exception.DateParseException;
import liquibase.exception.JDBCException;
import liquibase.util.ISODateFormat;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class HsqlDatabase extends AbstractDatabase {
    private static String START_CONCAT = "CONCAT(";
    private static String END_CONCAT = ")";
    private static String SEP_CONCAT = ", ";


    public boolean isCorrectDatabaseImplementation(Connection conn) throws JDBCException {
        return "HSQL Database Engine".equalsIgnoreCase(getDatabaseProductName(conn));
    }

    public String getDefaultDriver(String url) {
        if (url.startsWith("jdbc:hsqldb:")) {
            return "org.hsqldb.jdbcDriver";
        }
        return null;
    }


    public String getProductName() {
        return "Hsqldb";
    }

    public String getTypeName() {
        return "hsqldb";
    }

    public boolean supportsSequences() {
        return true;
    }

    public boolean supportsInitiallyDeferrableColumns() {
        return false;
    }

    protected String getDefaultDatabaseSchemaName() throws JDBCException {
        return "PUBLIC";
    }

    public String getBooleanType() {
        return "BOOLEAN";
    }

    public String getCurrencyType() {
        return "DECIMAL";
    }

    public String getUUIDType() {
        return "VARCHAR(36)";
    }

    public String getClobType() {
        return "LONGVARCHAR";
    }

    public String getBlobType() {
        return "LONGVARBINARY";
    }

    public String getDateTimeType() {
        return "DATETIME";
    }

    public String getCurrentDateTimeFunction() {
        return "NOW()";
    }

    public String getAutoIncrementClause() {
        return "GENERATED BY DEFAULT AS IDENTITY IDENTITY";
    }

    public String getConcatSql(String... values) {
        if (values == null) {
            return null;
        }

        return getConcatSql(Arrays.asList(values));
    }

    /**
     * Recursive way of building CONCAT instruction
     *
     * @param values a non null List of String
     * @return a String containing the CONCAT instruction with all elements, or only a value if there is only one element in the list
     */
    private String getConcatSql(List<String> values) {
        if (values.size() == 1) {
            return values.get(0);
        } else {
            return START_CONCAT + values.get(0) + SEP_CONCAT + getConcatSql(values.subList(1, values.size())) + END_CONCAT;
        }
    }

    public String getDateLiteral(String isoDate) {
        String returnString = isoDate;
        try {
            if (isDateTime(isoDate)) {
                ISODateFormat isoTimestampFormat = new ISODateFormat();
                DateFormat dbTimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                returnString = dbTimestampFormat.format(isoTimestampFormat.parse(isoDate));
            }
        } catch (ParseException e) {
            throw new RuntimeException("Unexpected date format: "+isoDate, e);
        }
        return "'" + returnString + "'";
    }

    protected Date parseDate(String dateAsString) throws DateParseException {
        try {
            if (dateAsString.indexOf(" ") > 0) {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(dateAsString);
            } else {
                if (dateAsString.indexOf(":") > 0) {
                    return new SimpleDateFormat("HH:mm:ss").parse(dateAsString);
                } else {
                    return new SimpleDateFormat("yyyy-MM-dd").parse(dateAsString);
                }
            }
        } catch (ParseException e) {
            throw new DateParseException(dateAsString);
        }
    }

    public SqlStatement createFindSequencesSQL(String schema) throws JDBCException {
        return new RawSqlStatement("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SYSTEM_SEQUENCES WHERE SEQUENCE_SCHEMA = '" + convertRequestedSchemaToSchema(schema) + "'");
    }

    public boolean supportsTablespaces() {
        return false;
    }


    public void setConnection(Connection conn) {
        super.setConnection(new HsqlConnectionDelegate(conn));
    }


    public SqlStatement getViewDefinitionSql(String schemaName, String name) throws JDBCException {
        return new RawSqlStatement("SELECT VIEW_DEFINITION FROM INFORMATION_SCHEMA.SYSTEM_VIEWS WHERE TABLE_NAME = '"+name+"' AND TABLE_SCHEMA='"+convertRequestedSchemaToSchema(schemaName)+"'");
    }

    public String convertRequestedSchemaToSchema(String requestedSchema) throws JDBCException {
        return super.convertRequestedSchemaToSchema(requestedSchema).toUpperCase();
    }
}
