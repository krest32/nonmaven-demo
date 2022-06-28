package com.fico.cbs.common.message;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private ResultMessage _result_message;
    private Map<String, Object> _fields = new HashMap();

    public Response() {
    }

    public ResultMessage getResultMessage() {
        return this._result_message;
    }

    public void setResultMessage(ResultMessage resultMessage) {
        this._result_message = resultMessage;
    }

    public Map<String, Object> getFields() {
        return this._fields;
    }

    public void setFields(Map<String, Object> fields) {
        this._fields = fields;
    }

    public void setField(String key, Object value) {
        this._fields.put(key, value);
    }

    public Object getField(String key) {
        return this._fields.get(key);
    }
}

