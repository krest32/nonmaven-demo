package com.fico.cbs.common.config;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class XMLConfigParser {
    private Config _config = new Config();

    public XMLConfigParser() {
    }

    public Config getConfig(String configFile) {
        SAXReader sr = new SAXReader();
        sr.setEncoding("UTF-8");

        try {
            File file = new File(configFile);
            InputStream in = new FileInputStream(file);
            Document document = sr.read(in);
            Element root = document.getRootElement();
            this._config = this.parseNode(root);
        } catch (FileNotFoundException var7) {
            var7.printStackTrace();
        } catch (DocumentException var8) {
            var8.printStackTrace();
        }

        return this._config;
    }

    public Config getConfig(InputStream is) {
        SAXReader sr = new SAXReader();
        sr.setEncoding("UTF-8");

        try {
            Document document = sr.read(is);
            Element root = document.getRootElement();
            this._config = this.parseNode(root);
        } catch (DocumentException var5) {
            var5.printStackTrace();
        }

        return this._config;
    }

    private Config parseNode(Element node) {
       Config config = new Config();
        List<Element> list = node.elements();
        Iterator iter = list.iterator();

        while(iter.hasNext()) {
            Element elem = (Element)iter.next();
            List<Element> eList = elem.elements();
            if (eList.size() == 0) {
                config.putConfigItem(elem.getName(), elem.getTextTrim());
            } else {
                config.putSubPackage(elem.getName(), this.parseNode(elem));
            }
        }

        return config;
    }
}
