package com.fico.cbs.common.database;

import com.fico.cbs.common.config.SystemConfig;
import com.fico.cbs.common.log.Logger;
import com.fico.cbs.common.log.LoggerFactory;



import java.sql.SQLException;


/**
 * 数据库连接工厂
 */
public class DataAccessFactory {
    private static String[] _ids = null;
    private static DataAccess[] _dataaccess = null;
    public static int MAX_CONNECTION_NUM = 5;
    public static Logger _logger = null;

    static {
        String mt = SystemConfig.getConfigByPath("common.message_listener.max_thread_num");
        if (mt != null && !"".equals(mt)) {
            MAX_CONNECTION_NUM = Integer.parseInt(mt);
        }

        _ids = new String[MAX_CONNECTION_NUM];
        _dataaccess = new DataAccess[MAX_CONNECTION_NUM];
        _logger = LoggerFactory.getLogger(DataAccessFactory.class.getName());

        for(int i = 0; i < MAX_CONNECTION_NUM; ++i) {
            try {
                _dataaccess[i] = new DataAccess(LoggerFactory.getLogger(DataAccessFactory.class.getName()));
            } catch (Exception var3) {
                _logger.error(var3.toString(), var3);
                var3.printStackTrace();
            }
        }

    }

    public DataAccessFactory() {
    }

    public static DataAccess getDataAccess(String id, String clazz) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        int i;
        for(i = 0; i < MAX_CONNECTION_NUM; ++i) {
            _logger.info("========111========");
            _logger.info(id);
            _logger.info(_ids[i]);
            if (id.equalsIgnoreCase(_ids[i])) {
                _dataaccess[i].setLog(LoggerFactory.getLogger(id, clazz));
                return _dataaccess[i];
            }
        }

        for(i = 0; i < MAX_CONNECTION_NUM; ++i) {
            _logger.info("========222========");
            _logger.info(id);
            _logger.info(_ids[i]);
            if (_ids[i] == null || "".equals(_ids[i])) {
                _ids[i] = id;
                _dataaccess[i].setLog(LoggerFactory.getLogger(id, clazz));
                return _dataaccess[i];
            }
        }

        _logger.info("=====return null=====");
        return null;
    }

    public static void closeDataAccess(String id) throws SQLException {
        for(int i = 0; i < MAX_CONNECTION_NUM; ++i) {
            if (id.equalsIgnoreCase(_ids[i])) {
                _ids[i] = null;
                return;
            }
        }

    }

    public static void commitDataAccess(String id) throws SQLException {
        for(int i = 0; i < MAX_CONNECTION_NUM; ++i) {
            if (id.equalsIgnoreCase(_ids[i])) {
                _dataaccess[i].commit();
                return;
            }
        }

    }

    public static void rollbackDataAccess(String id) throws SQLException {
        for(int i = 0; i < MAX_CONNECTION_NUM; ++i) {
            if (id.equalsIgnoreCase(_ids[i])) {
                _dataaccess[i].rollback();
                return;
            }
        }

    }
}
