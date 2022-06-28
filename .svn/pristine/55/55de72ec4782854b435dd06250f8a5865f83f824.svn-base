package com.fico.cbs.logTest;


import org.apache.log4j.Logger;
import org.junit.Test;


public class LogTest {

    private Logger _logger = Logger.getLogger(LogTest.class);

	/*
	@Ignore
	public void testLog_T() {
		if (_logger.isDebugEnabled()) {
			_logger.trace("trace");   // 当前项目中Logger无trace方法
		}
	}
	*/

    @Test
    public void testLog_D() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("debug");
        }
    }

    @Test
    public void testLog_I() {
        if (_logger.isDebugEnabled()) {
            _logger.info("info");
        }
    }

    @Test
    public void testLog_W() {
        if (_logger.isDebugEnabled()) {
            _logger.warn("warn");
        }
    }

    @Test
    public void testLog_E() {
        _logger.error("error");
    }

    @Test
    public void testLog_F() {
        _logger.fatal("fatal");
    }

    @Test
    public void testStringFormat() {
        String f_score = "guarantor%s_score";
        System.out.println(String.format(f_score, "张立武"));

    }
}
