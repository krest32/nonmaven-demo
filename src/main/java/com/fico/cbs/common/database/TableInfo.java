package com.fico.cbs.common.database;

import java.util.Iterator;
import java.util.List;

public class TableInfo {
    private XMLParser _xml;
    private String _name;
    private final String CONFIG_SOURCE = "tables/";

    public TableInfo(String table) {
        this._name = table.trim().toLowerCase();

        try {
            this._xml = new XMLParser("tables//" + table + ".xml", "table", false);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public String getPrimaryKeys() {
        return this._xml.getProperty("table/" + this._name + "/primaryKeys");
    }

    public int getFieldType(String field) {
        return Integer.parseInt(this._xml.getProperty("table/" + this._name + "/fields/" + field.toLowerCase()));
    }

    public String getTableRemark() {
        return this._xml.getProperty("table/" + this._name + "/label");
    }

    public List<String> getFields() {
        return this._xml.getChildren("table/" + this._name + "/fields");
    }

    public boolean containField(String field) {
        List<String> fields = this.getFields();
        Iterator iter = fields.iterator();

        while(iter.hasNext()) {
            if (((String)iter.next()).equalsIgnoreCase(field)) {
                return true;
            }
        }

        return false;
    }

    public String getName() {
        return this._name;
    }
}

