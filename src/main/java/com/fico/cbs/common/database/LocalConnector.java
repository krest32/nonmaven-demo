package com.fico.cbs.common.database;

import com.fico.cbs.common.config.Config;
import com.fico.cbs.common.config.SystemConfig;
import com.fico.cbs.common.log.Logger;
import com.fico.cbs.common.log.LoggerFactory;
import com.fico.cbs.utils.StringUtils;


import javax.naming.ConfigurationException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

public class LocalConnector extends Connector {
    private Map _metadata = new HashMap();
    private Logger _log;

    public LocalConnector() {
    }

    @Override
    public Connection getConnection() {
        this._log = LoggerFactory.getLogger(LocalConnector.class.getName());
        Connection conn = null;

        try {
            this._log.info("Connecting to DataBase......");
            Config sysConf = SystemConfig.getSystemConfig();
            Config dbConf = sysConf.getSubPackage("common").getSubPackage("database");
            String driver = dbConf.getConfigItem("driverClass");
            String url = dbConf.getConfigItem("url");
            String user = dbConf.getConfigItem("user");
            String pwd = dbConf.getConfigItem("password");
//            AES aes = new AES();
//            String password = aes.decryptnew(pwd);
            String password =  dbConf.getConfigItem("password");
            this._metadata.put("driver", driver);
            this._metadata.put("url", url);
            this._metadata.put("user", user);
            this._metadata.put("password", password);
            if (StringUtils.isNull(driver)) {
                throw new ConfigurationException("driver class name not found.");
            }

            if (StringUtils.isNull(url)) {
                throw new ConfigurationException("url not found.");
            }

            if (StringUtils.isNull(user)) {
                throw new ConfigurationException("user not found.");
            }

            if (StringUtils.isNull(password)) {
                throw new ConfigurationException("Missing password of database.");
            }

            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception var10) {
            this._log.error("DataBase Connection Fail.");
            this._log.error(var10.toString(), var10);
            var10.printStackTrace();
        }

        if (conn != null) {
            this._log.info("DataBase Connection Succeeded.");
        }

        return conn;
    }

    @Override
    public Map getMetaData() {
        return this._metadata;
    }
}
