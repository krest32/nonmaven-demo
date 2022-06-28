package com.fico.cbs.common.log;

public class LoggerFactory {
    public LoggerFactory() {
    }

    public static Logger getLogger(String id, String className) {
//        String loggerClass = SystemConfig.getConfigByPath("common.logger");
        String loggerClass ="com.fico.cbs.common.log.Log4JLogger";
        if (loggerClass == null || "".equals(loggerClass)) {
            loggerClass = "com.fico.common.log.Log4JLogger";
        }

        try {
            Logger logger = (Logger)Class.forName(loggerClass).newInstance();
            logger.init(id, className);
            return logger;
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static Logger getLogger(String className) {
        String loggerClass = "com.fico.cbs.common.log.Log4JLogger";
//        String loggerClass = SystemConfig.getConfigByPath("common.logger");
        if (loggerClass == null || "".equals(loggerClass)) {
            loggerClass = "com.fico.cbs.common.log.Log4JLogger";
        }

        try {
            Logger logger = (Logger)Class.forName(loggerClass).newInstance();
//            Logger logger =  LoggerFactory.getLogger(loggerClass);
            logger.init("system", className);
            return logger;
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }
}
