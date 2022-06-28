package com.fico.cbs.common.database;



import com.fico.cbs.common.config.SystemConfig;
import com.fico.cbs.common.database.storage.DataMap;
import com.fico.cbs.common.database.storage.ListResult;
import com.fico.cbs.common.database.storage.SingleResult;
import com.fico.cbs.common.log.Logger;
import com.fico.cbs.common.log.LoggerFactory;
import com.fico.cbs.utils.StringUtils;



import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataAccess {
    Logger _logger = null;
    private Connection _connection = null;
    private boolean _allow_update = true;

    public DataAccess() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        this._logger = LoggerFactory.getLogger("DataAccess", DataAccess.class.getName());
        this.getConnection();
    }

    public DataAccess(Logger logger) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        this._logger = logger;
        this.getConnection();
    }

    public void setLog(Logger logger) {
        this._logger = logger;
    }

    public void getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        this._logger.info("========getConnection========");
        if (this._connection == null) {
            String sConn = SystemConfig.getConfigByPath("common.database.connector");
//            Connector connector = (Connector)Class.forName(sConn).newInstance();

            this._connection =  new LocalConnector().getConnection();;
            this._connection.setTransactionIsolation(2);
            this._logger.info("=====new connection=====");
        }

    }

    public SingleResult querySingle(String table, DataMap para) throws SQLException {
        String sql = this.getSelectStatement(table, para);
        this._logger.info(sql);
        this.checkConnection();
        new SingleResult();
        PreparedStatement pstmt = this._connection.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        SingleResult sr = this.generateSingle(rs);
        rs.close();
        pstmt.close();
        this.printSingleResult(sr);
        return sr;
    }

    public int batchInsert(String table, ArrayList<DataMap> list) throws SQLException {
        TableInfo ti = new TableInfo(table);
        List<String> fields = ti.getFields();
        StringBuffer StrBuff = new StringBuffer();
        String sql = "insert into " + table + " (";
        String values = "";

        for(int i = 0; i < fields.size(); ++i) {
            if (i == fields.size() - 1) {
                sql = sql + (String)fields.get(i);
            } else {
                sql = sql + (String)fields.get(i) + ",";
            }

            values = values + "?,";
        }

        String temp_sql_str = sql;
        sql = sql.substring(0, sql.length()) + ") values (" + values.substring(0, values.length() - 1) + ")";
        int affectedRowCount = 0;
        if (this._allow_update) {
            this.checkConnection();
            PreparedStatement pstmt = this._connection.prepareStatement(sql);

            for(int i = 0; i < list.size(); ++i) {
                DataMap dm = (DataMap)list.get(i);
                StrBuff.append(temp_sql_str + ") values (");

                for(int j = 0; j < fields.size(); ++j) {
                    if (this.sqlData(ti.getFieldType((String)fields.get(j)), dm.get((String)fields.get(j))) == null && "".equals(this.sqlData(ti.getFieldType((String)fields.get(j)), dm.get((String)fields.get(j))))) {
                        if (ti.getFieldType((String)fields.get(j)) == -9) {
                            pstmt.setNull(j + 1, 0);
                        }

                        if (ti.getFieldType((String)fields.get(j)) == 12) {
                            pstmt.setNull(j + 1, 0);
                        } else {
                            pstmt.setNull(j + 1, 0);
                        }
                    } else {
                        pstmt.setObject(j + 1, this.sqlData(ti.getFieldType((String)fields.get(j)), dm.get((String)fields.get(j))));
                    }

                    if (j + 1 == fields.size()) {
                        if (ti.getFieldType((String)fields.get(j)) == -9) {
                            StrBuff.append("'" + this.sqlData(ti.getFieldType((String)fields.get(j)), dm.get((String)fields.get(j))) + "' );");
                        } else {
                            StrBuff.append(this.sqlData(ti.getFieldType((String)fields.get(j)), dm.get((String)fields.get(j))) + ");");
                        }
                    } else if (ti.getFieldType((String)fields.get(j)) == -9) {
                        StrBuff.append("'" + this.sqlData(ti.getFieldType((String)fields.get(j)), dm.get((String)fields.get(j))) + "' , ");
                    } else {
                        StrBuff.append(this.sqlData(ti.getFieldType((String)fields.get(j)), dm.get((String)fields.get(j))) + " , ");
                    }
                }

                pstmt.addBatch();
                this._logger.info(StrBuff.toString());
                StrBuff.delete(0, StrBuff.length());
            }

            int[] cs = pstmt.executeBatch();

            for(int i = 0; i < cs.length; ++i) {
                affectedRowCount += cs[i];
            }

            pstmt.close();
        }

        this._logger.info(affectedRowCount + " rows affected");
        return affectedRowCount;
    }

    public int batchInsert(String table, DataMap[] data) throws SQLException {
        TableInfo ti = new TableInfo(table);
        List<String> fields = ti.getFields();
        StringBuffer StrBuff = new StringBuffer();
        String sql = "insert into " + table + " (";
        String values = "";

        int affectedRowCount;
        for(affectedRowCount = 0; affectedRowCount < fields.size(); ++affectedRowCount) {
            sql = sql + (String)fields.get(affectedRowCount) + ",";
            values = values + "?,";
        }

        StrBuff.append(sql + ") values (");
        sql = sql.substring(0, sql.length() - 1) + ") values (" + values.substring(0, values.length() - 1) + ")";
        affectedRowCount = 0;
        if (this._allow_update) {
            this.checkConnection();
            PreparedStatement pstmt = this._connection.prepareStatement(sql);

            int j;
            for(int i = 0; i < data.length; ++i) {
                for(j = 0; j < fields.size(); ++j) {
                    pstmt.setObject(j + 1, data[i].get((String)fields.get(j)));
                    if (j + 1 == fields.size()) {
                        if (ti.getFieldType((String)fields.get(j)) == -9) {
                            StrBuff.append("'" + data[i].get((String)fields.get(j)) + "' );");
                        } else {
                            StrBuff.append(data[i].get((String)fields.get(j)) + ");");
                        }
                    } else if (ti.getFieldType((String)fields.get(j)) == -9) {
                        StrBuff.append("'" + data[i].get((String)fields.get(j)) + "' , ");
                    } else {
                        StrBuff.append(data[i].get((String)fields.get(j)) + " , ");
                    }
                }

                pstmt.addBatch();
            }

            this._logger.info(StrBuff.toString());
            int[] cs = pstmt.executeBatch();

            for(j = 0; j < cs.length; ++j) {
                affectedRowCount += cs[j];
            }

            pstmt.close();
        }

        this._logger.info(affectedRowCount + " rows affected");
        return affectedRowCount;
    }

    public int insert(String table, DataMap para) throws SQLException {
        String sql = this.getInsertStatement(table, para);
        this._logger.info(sql);
        int affectedRowCount = 0;
        if (this._allow_update) {
            this.checkConnection();
            PreparedStatement pstmt = this._connection.prepareStatement(sql);
            affectedRowCount = pstmt.executeUpdate();
            pstmt.close();
        }

        this._logger.info(affectedRowCount + " rows affected");
        return affectedRowCount;
    }

    public int update(String table, DataMap para) throws SQLException {
        int affectedRowCount = 0;
        String sql = this.getUpdateStatement(table, para);
        this._logger.info(sql);
        if (this._allow_update) {
            this.checkConnection();
            PreparedStatement pstmt = this._connection.prepareStatement(sql);
            affectedRowCount = pstmt.executeUpdate();
            pstmt.close();
        }

        this._logger.info(affectedRowCount + " rows affected");
        return affectedRowCount;
    }

    public int update(String table, DataMap fields, DataMap cond) throws SQLException {
        int affectedRowCount = 0;
        String sql = this.getUpdateStatement(table, fields, cond);
        this._logger.info(sql);
        if (this._allow_update) {
            this.checkConnection();
            PreparedStatement pstmt = this._connection.prepareStatement(sql);
            affectedRowCount = pstmt.executeUpdate();
            pstmt.close();
        }

        this._logger.info(affectedRowCount + " rows affected");
        return affectedRowCount;
    }

    public int delete(String table, DataMap cond) throws SQLException {
        int affectedRowCount = 0;
        String sql = this.getDeleteStatement(table, cond);
        this._logger.info(sql);
        if (this._allow_update) {
            this.checkConnection();
            PreparedStatement pstmt = this._connection.prepareStatement(sql);
            affectedRowCount = pstmt.executeUpdate();
            pstmt.close();
        }

        this._logger.info(affectedRowCount + " rows affected");
        return affectedRowCount;
    }

    public int delete(String sql) throws SQLException {
        int affectedRowCount = 0;
        this._logger.info(sql);
        if (this._allow_update) {
            this.checkConnection();
            PreparedStatement pstmt = this._connection.prepareStatement(sql);
            affectedRowCount = pstmt.executeUpdate();
            pstmt.close();
        }

        this._logger.info(affectedRowCount + " rows affected");
        return affectedRowCount;
    }

    public int clearTable(String table) throws SQLException {
        String sql = "delete from " + table;
        this._logger.info(sql);
        int affectedRowCount = 0;
        if (this._allow_update) {
            this.checkConnection();
            PreparedStatement pstmt = this._connection.prepareStatement(sql);
            affectedRowCount = pstmt.executeUpdate();
            pstmt.close();
        }

        this._logger.info(affectedRowCount + " rows affected");
        return affectedRowCount;
    }

    public ListResult queryForList(String sql) throws SQLException {
        this._logger.info(sql);
        this.checkConnection();
        PreparedStatement pstmt = this._connection.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        ListResult lr = this.generateList(rs);
        rs.close();
        pstmt.close();
        this.printListResult(lr);
        return lr;
    }

    public SingleResult querySingle(String sql) throws SQLException {
        this._logger.info(sql);
        this.checkConnection();
        PreparedStatement pstmt = this._connection.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        SingleResult sr = this.generateSingle(rs);
        rs.close();
        pstmt.close();
        this.printSingleResult(sr);
        return sr;
    }

    public int executeUpdate(String sql) throws SQLException {
        int affectedRowCount = 0;
        this._logger.info(sql);
        if (this._allow_update) {
            this.checkConnection();
            PreparedStatement pstmt = this._connection.prepareStatement(sql);
            affectedRowCount = pstmt.executeUpdate();
            pstmt.close();
        }

        this._logger.info(affectedRowCount + " rows affected");
        return affectedRowCount;
    }

    private String getInsertStatement(String table, DataMap para) {
        TableInfo ti = new TableInfo(table);
        StringBuffer sb = new StringBuffer("insert into ");
        StringBuffer values = new StringBuffer();
        sb.append(table).append("(");
        List<String> fields = ti.getFields();
        Iterator iter = fields.iterator();

        String sql;
        while(iter.hasNext()) {
            sql = (String)iter.next();
            Object value = para.get(sql);
            sb.append(sql).append(",");
            values.append(this.sqlData(sql, ti.getFieldType(sql), value)).append(",");
        }

        values = values.deleteCharAt(values.length() - 1);
        sb.deleteCharAt(sb.length() - 1).append(") values (");
        sb.append(values).append(")");
        sql = sb.toString();
        return sql;
    }

    private String getDeleteStatement(String table, DataMap cond) {
        TableInfo ti = new TableInfo(table);
        StringBuffer sb = new StringBuffer("delete from ");
        sb.append(table).append(" where ");
        Iterator ci = cond.keySet().iterator();

        String field;
        while(ci.hasNext()) {
            field = (String)ci.next();
            if (ti.containField(field)) {
                sb.append(this.cond(field, ti.getFieldType(field), cond.get(field)));
            }

            if (ci.hasNext()) {
                sb.append(" and ");
            }
        }

        field = sb.toString();
        return field;
    }

    private String getSelectStatement(String table, DataMap para) {
        TableInfo ti = new TableInfo(table);
        StringBuffer sb = new StringBuffer("select top 1 ");
        List<String> fields = ti.getFields();
        Iterator iter = fields.iterator();

        while(iter.hasNext()) {
            String field = (String)iter.next();
            sb.append(field).append(",");
        }

        sb.deleteCharAt(sb.length() - 1).append(" from ").append(table).append(" where ");
        Iterator pi = para.keySet().iterator();

        String field;
        while(pi.hasNext()) {
            field = (String)pi.next();
            if (ti.containField(field)) {
                sb.append(this.cond(field, ti.getFieldType(field), para.get(field)));
            }

            if (pi.hasNext()) {
                sb.append(" and ");
            }
        }

        field = sb.toString();
        return field;
    }

    private String getUpdateStatement(String table, DataMap data, DataMap cond) {
        TableInfo ti = new TableInfo(table);
        StringBuffer sb = new StringBuffer("update ");
        List<String> fields = ti.getFields();
        sb.append(table).append(" set ");
        Iterator pi = data.keySet().iterator();

        while(pi.hasNext()) {
            String field = pi.next().toString().toLowerCase();
            if (fields.contains(field)) {
                sb.append(field).append("=").append(this.sqlData(field, ti.getFieldType(field), data.get(field))).append(",");
            }
        }

        sb.deleteCharAt(sb.length() - 1).append(" where ");
        Iterator ci = cond.keySet().iterator();

        String field;
        while(ci.hasNext()) {
            field = (String)ci.next();
            if (ti.containField(field)) {
                sb.append(this.cond(field, ti.getFieldType(field), cond.get(field)));
            }

            if (ci.hasNext()) {
                sb.append(" and ");
            }
        }

        field = sb.toString();
        return field;
    }

    private String getUpdateStatement(String table, DataMap para) throws SQLException {
        TableInfo ti = new TableInfo(table);
        String pri = ti.getPrimaryKeys();
        if (pri != null && !"".equals(pri)) {
            StringBuffer sb = new StringBuffer("update ");
            List<String> fields = ti.getFields();
            sb.append(table).append(" set ");
            Iterator pi = para.keySet().iterator();

            String sql;
            while(pi.hasNext()) {
                sql = pi.next().toString().toLowerCase();
                if (fields.contains(sql) && pri.indexOf(sql) < 0) {
                    sb.append(sql).append("=").append(this.sqlData(sql, ti.getFieldType(sql), para.get(sql))).append(",");
                }
            }

            sb.deleteCharAt(sb.length() - 1).append(" where ");

            while(pri.indexOf(",") >= 0) {
                sql = pri.substring(0, pri.indexOf(44));
                sb.append(this.cond(sql, ti.getFieldType(sql), para.get(sql))).append(" and ");
                pri = pri.substring(pri.indexOf(",") + 1);
            }

            sb.append(this.cond(pri, ti.getFieldType(pri), para.get(pri)));
            sql = sb.toString();
            return sql;
        } else {
            throw new SQLException("No Primary keys defined for table " + table);
        }
    }

    private String cond(String field, int type, Object value) {
        return value != null && !"".equals(value) ? field + "=" + this.sqlData(field, type, value.toString()) : field + " is null";
    }

    private String sqlData(String field, int type, Object value) {
        if (value == null) {
            return "null";
        } else {
            String v = String.valueOf(value);
            switch(type) {
                case -16:
                case -15:
                case -9:
                case -1:
                case 1:
                case 12:
                case 91:
                case 92:
                case 93:
                    return "'" + v.trim() + "'";
                default:
                    if (!"".equals(v) && !"".equals(StringUtils.replaceAll(StringUtils.replaceAll(StringUtils.replaceAll(v, ",", ""), "-", ""), "*", ""))) {
                        return v.equals("0.00") ? "0" : (v.equalsIgnoreCase("o") ? "0" : v.replaceAll(",", ""));
                    } else {
                        return "null";
                    }
            }
        }
    }

    private Object sqlData(int type, Object value) {
        if (value == null) {
            return null;
        } else {
            String v = String.valueOf(value);
            switch(type) {
                case -16:
                case -15:
                case -9:
                case -1:
                case 1:
                case 12:
                case 91:
                case 92:
                case 93:
                    return new String(v.trim());
                case 3:
                    if (!"".equals(v) && !"".equals(StringUtils.replaceAll(StringUtils.replaceAll(StringUtils.replaceAll(v, ",", ""), "-", ""), "*", ""))) {
                        return Double.valueOf(v.replaceAll(",", ""));
                    }

                    return 0;
                default:
                    return !"".equals(v) && !"".equals(StringUtils.replaceAll(StringUtils.replaceAll(StringUtils.replaceAll(v, ",", ""), "-", ""), "*", "")) ? Integer.valueOf(v.equalsIgnoreCase("o") ? "0" : (v.indexOf(".") > 0 ? v.substring(0, v.indexOf(".")).replaceAll(",", "") : v.replaceAll(",", ""))) : 0;
            }
        }
    }

    private SingleResult generateSingle(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        SingleResult sr = new SingleResult();
        if (rs.next()) {
            for(int i = 1; i <= rsmd.getColumnCount(); ++i) {
                sr.put(rsmd.getColumnName(i), rs.getObject(i));
            }
        }

        return sr;
    }

    private ListResult generateList(ResultSet rs) throws SQLException {
        ListResult lr = new ListResult();
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();

        for(int i = 0; i < colCount; ++i) {
            lr.addColumn(rsmd.getColumnName(i + 1));
        }

        while(rs.next()) {
            String[] coldata = new String[colCount];

            for(int i = 0; i < colCount; ++i) {
                coldata[i] = rs.getString(i + 1);
            }

            lr.addRow(coldata);
        }

        return lr;
    }

    public void printListResult(ListResult lr) {
        if (lr.rowCount() <= 0) {
            this._logger.info("0 row returned.");
        } else {
            StringBuffer info = new StringBuffer(lr.rowCount() + " rows returned\r\n");

            int i;
            for(i = 1; i < lr.columnCount(); ++i) {
                info = info.append(lr.getColumnNameAtIndex(i)).append(" | ");
            }

            info = info.delete(info.length() - 2, info.length());
            info = info.append("\r\n--------------------------------------------------------\r\n");

            for(i = 1; i <= lr.rowCount() && i <= 100; ++i) {
                for(int j = 1; j <= lr.columnCount(); ++j) {
                    info = info.append(lr.getField(j, i)).append(" | ");
                }

                info = info.delete(info.length() - 2, info.length()).append("\r\n");
            }

            if (lr.rowCount() > 100) {
                info.append("......");
            }

            this._logger.info(info.toString());
        }

    }

    public void printSingleResult(SingleResult sr) {
        if (sr.isEmpty()) {
            this._logger.info("0 row returned.");
        } else {
            StringBuffer info = new StringBuffer("1 row returned\r\n");
            Iterator<String> iter = sr.keySet().iterator();
            String keys = "";

            String values;
            String key;
            for(values = ""; iter.hasNext(); values = values + sr.get(key) + " | ") {
                key = (String)iter.next();
                keys = keys + key + " | ";
            }

            info.append(keys.substring(0, keys.length() - 2));
            info.append("\r\n--------------------------------------------------------\r\n");
            info.append(values.substring(0, values.length() - 2));
            this._logger.info(info.toString());
        }

    }

    public boolean getAutoCommit() throws SQLException {
        return this._connection.getAutoCommit();
    }

    public int getTransactionIsolation() throws SQLException {
        return this._connection.getTransactionIsolation();
    }

    public void setAutoCommit(boolean auto) throws SQLException {
        this._connection.setAutoCommit(auto);
    }

    public void setSavepoint() throws SQLException {
        this._connection.setSavepoint();
    }

    public void setSavepoint(String name) throws SQLException {
        this._connection.setSavepoint(name);
    }

    public void rollback(String savePoint) throws SQLException {
        Savepoint sp = this._connection.setSavepoint(savePoint);
        this._connection.rollback(sp);
    }

    public void commit() throws SQLException {
        this._connection.commit();
    }

    public void setAllowUpdate(boolean isAllow) {
        this._allow_update = isAllow;
    }

    public boolean allowUpdate() {
        return this._allow_update;
    }

    public void close() throws SQLException {
        this._connection.close();
        this._connection = null;
        this._logger.info("=====connection close=====");
    }

    public boolean available() {
        if (this._connection == null) {
            return false;
        } else {
            boolean isClosed = false;

            try {
                isClosed = this._connection.isClosed();
            } catch (Exception var3) {
                var3.printStackTrace();
            }

            return !isClosed;
        }
    }

    private void checkConnection() throws SQLException {
        if (this._connection == null || this._connection.isClosed()) {
            throw new SQLException("No connection with DataBase found.");
        }
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return this._connection.getMetaData();
    }

    public boolean isClosed() throws SQLException {
        return this._connection.isClosed();
    }

    public boolean isValid(int timeout) throws SQLException {
        return this._connection.isValid(timeout);
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this._connection.releaseSavepoint(savepoint);
    }

    public void rollback() throws SQLException {
        this._connection.rollback();
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        this._connection.rollback(savepoint);
    }

    public void setTransactionIsolation(int level) throws SQLException {
        this._connection.setTransactionIsolation(level);
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        this._connection.setReadOnly(readOnly);
    }

    public boolean isReadOnly() throws SQLException {
        return this._connection.isReadOnly();
    }
}
