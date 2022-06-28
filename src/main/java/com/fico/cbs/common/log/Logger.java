package com.fico.cbs.common.log;

public interface Logger {

    void init(String var1, String var2);

    void info(String var1);

    void info(String var1, Throwable var2);

    void debug(String var1);

    void debug(String var1, Throwable var2);

    void error(String var1);

    void error(String var1, Throwable var2);

    void close();
}
