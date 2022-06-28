package com.fico.cbs.common.log;

import org.apache.log4j.Level;

public class Log4JLogger implements Logger {
    private String _id = null;
    private org.apache.log4j.Logger logger = null;

    public Log4JLogger() {
    }

    @Override
    public void info(String message) {
        this.logger.info("[" + this._id + "] " + message);
    }

    @Override
    public void info(String message, Throwable t) {
        this.logger.info("[" + this._id + "] " + message, t);
    }
    @Override
    public void debug(String message) {
        this.logger.debug("[" + this._id + "] " + message);
    }
    @Override
    public void debug(String message, Throwable t) {
        this.logger.debug("[" + this._id + "] " + message, t);
    }
    @Override
    public void error(String message) {
        this.logger.error("[" + this._id + "] " + message);
    }
    @Override
    public void error(String message, Throwable t) {
        this.logger.error("[" + this._id + "] " + message, t);
    }

    public void setLevel(Level level) {
        this.logger.setLevel(level);
    }
    @Override
    public void init(String id, String clazz) {
        this.logger = org.apache.log4j.Logger.getLogger(clazz);
        this._id = id;
    }

    public void init(String clazz) {
        this.logger = org.apache.log4j.Logger.getLogger(clazz);
        this._id = "sysid";
    }

    public void close() {
    }
}
