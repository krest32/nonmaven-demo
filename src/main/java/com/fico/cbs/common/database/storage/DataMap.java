package com.fico.cbs.common.database.storage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataMap extends HashMap<String, Object> {
    public DataMap() {
    }

    public boolean containsKey(String key) {
        return super.containsKey(key.toLowerCase());
    }

    public Object get(String key) {
        return super.get(key.toLowerCase());
    }

    public Object put(String key, Object value) {
        return super.put(key.toLowerCase(), value);
    }

    public void putAll(Map m) {
        Iterator iter = m.keySet().iterator();

        while(iter.hasNext()) {
            String o = (String)iter.next();
            this.put(o, m.get(o));
        }

    }

    public Object remove(String key) {
        return super.remove(key.toLowerCase());
    }

    public void print() {
        Iterator<String> iter = this.keySet().iterator();
        String keys = "";

        String key;
        for(String values = ""; iter.hasNext(); values = values + this.get(key) + " | ") {
            key = (String)iter.next();
            keys = keys + key + " | ";
        }

    }
}

