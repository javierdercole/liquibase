package liquibase.change;

import liquibase.database.Database;
import liquibase.database.sql.AddUniqueConstraintStatement;
import liquibase.database.sql.SqlStatement;
import liquibase.database.structure.Column;
import liquibase.database.structure.DatabaseObject;
import liquibase.database.structure.Table;
import liquibase.exception.UnsupportedChangeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashSet;
import java.util.Set;

/**
 * Adds a unique constraint to an existing column.
 */
public class AddUniqueConstraintChange extends AbstractChange {

    private String schemaName;
    private String tableName;
    private String columnNames;
    private String constraintName;
    private String tablespace;

    public AddUniqueConstraintChange() {
        super("addUniqueConstraint", "Add Unique Constraint");
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String columnNames) {
        this.columnNames = columnNames;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }


    public String getTablespace() {
        return tablespace;
    }

    public void setTablespace(String tablespace) {
        this.tablespace = tablespace;
    }

    public SqlStatement[] generateStatements(Database database) throws UnsupportedChangeException {
        AddUniqueConstraintStatement statement = new AddUniqueConstraintStatement(getSchemaName() == null?database.getDefaultSchemaName():getSchemaName(), getTableName(), getColumnNames(), getConstraintName());
        statement.setTablespace(getTablespace());

        return new SqlStatement[] { statement };
    }

    public String getConfirmationMessage() {
        return "Unique constraint added to "+getTableName()+"("+getColumnNames()+")";
    }

    protected Change[] createInverses() {
        DropUniqueConstraintChange inverse = new DropUniqueConstraintChange();
        inverse.setSchemaName(getSchemaName());
        inverse.setTableName(getTableName());
        inverse.setConstraintName(getConstraintName());

        return new Change[]{
                inverse,
        };
    }

    public Element createNode(Document currentChangeLogFileDOM) {
        Element element = currentChangeLogFileDOM.createElement(getTagName());
        if (getSchemaName() != null) {
            element.setAttribute("schemaName", getSchemaName());
        }
        if (getTablespace() != null) {
            element.setAttribute("tablespace", getTablespace());            
        }

        element.setAttribute("tableName", getTableName());
        element.setAttribute("columnNames", getColumnNames());
        element.setAttribute("constraintName", getConstraintName());

        return element;
    }

    public Set<DatabaseObject> getAffectedDatabaseObjects() {

        Set<DatabaseObject> returnSet = new HashSet<DatabaseObject>();

        Table table = new Table(getTableName());
        returnSet.add(table);

        for (String columnName : getColumnNames().split(",")) {
            Column column = new Column();
            column.setTable(table);
            column.setName(columnName.trim());

            returnSet.add(column);
        }

        return returnSet;

    }

}
