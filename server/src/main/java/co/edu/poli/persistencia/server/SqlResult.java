package co.edu.poli.persistencia.server;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlResult {

    private ResultSet resultSet;

    private Integer updateCount;

    private SQLException exception;

    private List<String> resultsList;

    public SqlResult(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public SqlResult() {
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public void setResults(List<String> results) {
        this.resultsList = results;
    }

    public Integer getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(Integer updateCount) {
        this.updateCount = updateCount;
    }

    public SQLException getException() {
        return exception;
    }

    public void setException(SQLException exception) {
        this.exception = exception;
    }

    public List<String> getResultsList() {
        return resultsList;
    }

    public void setResultsList(List<String> resultsList) {
        this.resultsList = resultsList;
    }

    List<String> resultSetToList() throws SQLException {
        List<String> list = new ArrayList<>();
        if (resultSet == null) {
            return list;
        }

        ResultSetMetaData md = resultSet.getMetaData();
        int columns = md.getColumnCount();
        while (resultSet.next()) {
            StringBuilder row = new StringBuilder();
            for (int i = 1; i <= columns; ++i) {
                row.append(resultSet.getObject(i)).append(" ");
            }
            list.add(row.toString().trim());
        }
        return list;
    }
}