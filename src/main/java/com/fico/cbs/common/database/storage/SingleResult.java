package com.fico.cbs.common.database.storage;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class SingleResult extends DataMap {

    public SingleResult() {
    }

    public SingleResult(ResultSet result) throws Exception {
        ResultSetMetaData rsmd = result.getMetaData();
        if (result.next()) {
            for(int i = 1; i <= rsmd.getColumnCount(); ++i) {
                this.put(rsmd.getColumnName(i), result.getObject(i));
            }
        }

    }
}
