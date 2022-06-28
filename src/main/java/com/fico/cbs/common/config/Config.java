package com.fico.cbs.common.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Config {
    private Map<String, Config> _sub_package = new HashMap();
    private Map<String, String> _config_item = new HashMap();

    public Config() {
    }

    public void putSubPackage(String key, Config sub) {
        this._sub_package.put(key.toLowerCase(), sub);
    }

    public Config getSubPackage(String key) {
        return key == null ? null : (Config)this._sub_package.get(key.toLowerCase());
    }

    public void putConfigItem(String key, String value) {
        this._config_item.put(key.toLowerCase(), value);
    }

    public String getConfigItem(String key) {
        return key == null ? null : (String)this._config_item.get(key.toLowerCase());
    }

    public Iterator<String> subPackageItertor() {
        return this._sub_package.keySet().iterator();
    }

    public Iterator<String> configItemItertor() {
        return this._config_item.keySet().iterator();
    }

    public int subPackageCount() {
        return this._sub_package.size();
    }

    public int configItemCount() {
        return this._config_item.size();
    }

    public String getConfigByPath(String path) {
        Config conf = this;

        while(path.indexOf(".") >= 0) {
            String key = path.substring(0, path.indexOf(46));
            path = path.substring(path.indexOf(46) + 1);
            Config subc = conf.getSubPackage(key);
            if (subc != null) {
                conf = subc;
            }
        }

        return conf.getConfigItem(path);
    }

    public void clear() {
        this._sub_package.clear();
        this._config_item.clear();
    }
}
