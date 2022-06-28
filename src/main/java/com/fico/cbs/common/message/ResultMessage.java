package com.fico.cbs.common.message;

import com.fico.cbs.utils.ResourceUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.Iterator;

public class ResultMessage {
    private String _status;
    private String _code;
    private String _name;
    private String _des;

    public ResultMessage(String code, String name, String desc) {
        this._code = code;
        this._name = name;
        this._des = desc;
    }

    public ResultMessage(String pkg, String code) {
        SAXReader reader = new SAXReader();
        Document doc = null;

        try {
            doc = reader.read(ResourceUtils.getResourceAsStream("messages.xml"));
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        Element root = doc.getRootElement();
        Element pack = root.element(pkg);
        if (pack != null) {
            Iterator iter = pack.elementIterator();

            while(iter.hasNext()) {
                Element e = (Element)iter.next();
                if (code.equalsIgnoreCase(e.attributeValue("code"))) {
                    this._code = code;
                    this._name = e.attributeValue("name");
                    this._des = e.attributeValue("des");
                    break;
                }
            }
        }

    }

    public String getStatus() {
        return this._status;
    }

    public void setStatus(String _status) {
        this._status = _status;
    }

    public String getCode() {
        return this._code;
    }

    public void setCode(String code) {
        this._code = code;
    }

    public String getName() {
        return this._name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getDescription() {
        return this._des;
    }

    public String getDescription(String... info) {
        if (this._des != null && info != null) {
            while(this._des.indexOf("{ ") >= 0) {
                this._des = this._des.replace("{ ", "{");
            }

            while(this._des.indexOf(" }") >= 0) {
                this._des = this._des.replace(" }", "}");
            }

            for(int i = 0; i < info.length; ++i) {
                this._des = this._des.replace("{" + (i + 1) + "}", info[i]);
            }
        }

        return this._des;
    }

    public void setDescription(String des) {
        this._des = des;
    }
}

