package liquibase.change;

import liquibase.database.sql.SqlStatement;
import liquibase.database.sql.UpdateStatement;
import liquibase.database.sql.DeleteStatement;
import liquibase.database.Database;
import liquibase.database.structure.DatabaseObject;
import liquibase.database.structure.Table;
import liquibase.exception.UnsupportedChangeException;
import liquibase.util.StringUtils;

import java.util.*;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class DeleteDataChange extends AbstractChange {

    private String schemaName;
    private String tableName;
    private String whereClause;

    public DeleteDataChange() {
        super("delete", "Delete Data");
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

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public SqlStatement[] generateStatements(Database database) throws UnsupportedChangeException {

        DeleteStatement statement = new DeleteStatement(getSchemaName() == null ? database.getDefaultSchemaName() : getSchemaName(), getTableName());

        statement.setWhereClause(whereClause);

        return new SqlStatement[]{
                statement
        };
    }

    public String getConfirmationMessage() {
        return "Data deleted from " + getTableName();
    }

    public Element createNode(Document currentChangeLogFileDOM) {
        Element node = currentChangeLogFileDOM.createElement("delete");
        if (getSchemaName() != null) {
            node.setAttribute("schemaName", getSchemaName());
        }

        node.setAttribute("tableName", getTableName());

        if (StringUtils.trimToNull(getWhereClause()) != null) {
            Element whereClause = currentChangeLogFileDOM.createElement("where");
            whereClause.appendChild(currentChangeLogFileDOM.createTextNode(getWhereClause()));
            node.appendChild(whereClause);
        }
        return node;
    }

    public Set<DatabaseObject> getAffectedDatabaseObjects() {
        Table dbObject = new Table(getTableName());

        return new HashSet<DatabaseObject>(Arrays.asList(dbObject));
    }

}
