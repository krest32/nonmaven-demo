package com.fico.cbs.common.config;


import com.fico.cbs.utils.ResourceUtils;

public class SystemConfig{

    private static Config _config = null;

    static {
        XMLConfigParser parser = new XMLConfigParser();

        try {
            _config = parser.getConfig(ResourceUtils.getResourceAsStream("system_config.xml"));
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public SystemConfig() {
    }

    public static Config getSystemConfig() {
        return _config;
    }

    public static String getConfigByPath(String path) {
        return _config == null ? null : _config.getConfigByPath(path);
    }
}
