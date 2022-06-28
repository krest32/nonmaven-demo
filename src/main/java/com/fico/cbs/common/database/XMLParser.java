package com.fico.cbs.common.database;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

public class XMLParser {
    private final int _cacheSize = 50;
    private int _cacheCursor = 0;
    private File _xmlFile = null;
    public Map _xmlCache = null;
    private String[] _cacheKeys = null;
    private Document _document = null;

    public XMLParser(String xmlPath, String rootNode) throws IOException, ParserConfigurationException, SAXException {
        this.getXMLFileInstance(xmlPath, rootNode);
        this._xmlCache = new HashMap();
        this._cacheKeys = new String[50];
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        this._document = builder.parse(this._xmlFile);
    }

    public XMLParser(String xmlPath, String rootNode, boolean write) throws IOException, ParserConfigurationException, SAXException {
        if (write) {
            this.getXMLFileInstance(xmlPath, rootNode);
        } else {
            this._xmlFile = new File(xmlPath);
        }

        this._xmlCache = new HashMap();
        this._cacheKeys = new String[50];
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        this._document = builder.parse(this._xmlFile);
    }

    private Element searchForElement(String key) {
        List eleList = this.parseElementName(key);
        Element element = this._document.getDocumentElement();
        if (element.getNodeName().trim().equals(key)) {
            return element;
        } else if (!element.getNodeName().trim().equals(eleList.get(0))) {
            return null;
        } else {
            for(int i = 1; i < eleList.size(); ++i) {
                NodeList children = element.getChildNodes();
                int childIndex;
                for(childIndex = 0; childIndex < children.getLength(); ++childIndex) {
                    Node tmpNode = children.item(childIndex);
                    if (tmpNode.getNodeName().trim().equals((String)eleList.get(i))) {
                        element = (Element)tmpNode;
                        break;
                    }
                }

                if (childIndex >= children.getLength()) {
                    return null;
                }
            }

            return element;
        }
    }

    public String getProperty(String key) {
        String value = null;
        Element element;
        if (this._xmlCache.containsKey(key)) {
            element = (Element)this._xmlCache.get(key);
        } else {
            element = this.searchForElement(key);
            this.addToCache(key, element);
        }

        if (element != null) {
            NodeList thisNode = element.getChildNodes();
            if (thisNode.getLength() > 1 && thisNode != null) {
                Node thisN = thisNode.item(1);
                value = thisN.getPreviousSibling().getTextContent().trim();
            } else {
                value = element.getTextContent().trim();
            }
        }

        return value;
    }

    public String getAttribute(String key, String attr) {
        if (this._xmlCache.containsKey(key)) {
            return ((Element)this._xmlCache.get(key)).getAttribute(attr);
        } else {
            Element e = this.searchForElement(key);
            this.addToCache(key, e);
            String attrValue = null;
            if (e != null) {
                attrValue = e.getAttribute(attr).trim();
            }

            return attrValue;
        }
    }

    public List<String> getChildren(String key) {
        List<String> children = new ArrayList();
        Element e = this.searchForElement(key);
        NodeList nl = e.getChildNodes();

        for(int i = 0; i < nl.getLength(); ++i) {
            Node n = nl.item(i);
            String name = n.getNodeName();
            if (!name.equals("#text")) {
                children.add(name);
            }
        }

        return children;
    }

    public void modifyProperty(String key, String newVal) throws UnsupportedEncodingException {
        Element elmt = this.searchForElement(key);
        if (elmt != null) {
            elmt.setTextContent(new String(newVal.getBytes(), this._document.getInputEncoding()));
        }

    }

    public void modifyAttribute(String key, String attrKey, String newVal) throws UnsupportedEncodingException {
        Element elmt = this.searchForElement(key);
        if (elmt != null) {
            elmt.setAttribute(attrKey, new String(newVal.getBytes(), this._document.getInputEncoding()));
        }

    }

    public void appendChild(String key, String cName, String value, Map attributies) throws UnsupportedEncodingException {
        Element curr = this.searchForElement(key);
        if (curr != null) {
            String encoding = this._document.getInputEncoding();
            Element child = this._document.createElement(cName);
            child.setTextContent(new String(value.getBytes(), encoding));
            if (attributies != null) {
                Set attrSet = attributies.keySet();
                Iterator iter = attrSet.iterator();

                while(iter.hasNext()) {
                    String attrKey = (String)iter.next();
                    String attrV = (String)attributies.get(attrKey);
                    child.setAttribute(attrKey, new String(attrV.getBytes(), encoding));
                }
            }

            curr.appendChild(child);
        }

    }

    public void applyChangesToFile() throws TransformerFactoryConfigurationError, TransformerException, IOException {
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(this._xmlFile));
        Source source = new DOMSource(this._document);
        Result result = new StreamResult(osw);
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.setOutputProperty("encoding", this._document.getInputEncoding());
        xformer.setOutputProperty("indent", "yes");
        xformer.transform(source, result);
        osw.close();
    }

    private List parseElementName(String key) {
        List eleList = new ArrayList();
        key.replaceAll(".", "/");
        int preLoc = 0;

        for(int i = 0; i < key.length(); ++i) {
            if (key.charAt(i) == '/' || key.charAt(i) == '\\') {
                eleList.add(key.substring(preLoc, i));
                preLoc = i + 1;
            }
        }

        eleList.add(key.substring(preLoc));
        return eleList;
    }

    private void addToCache(String key, Element value) {
        if (this._cacheCursor < 50) {
            this._cacheKeys[this._cacheCursor++] = key;
            this._xmlCache.put(key, value);
        } else {
            this._xmlCache.remove(this._cacheKeys[0]);

            for(int i = 0; i < 49; ++i) {
                this._cacheKeys[i] = this._cacheKeys[i + 1];
            }

            this._cacheKeys[this._cacheCursor - 1] = key;
            this._xmlCache.put(key, value);
        }

    }

    public void clear() throws Exception {
        this._cacheCursor = 0;
        this._xmlCache = new HashMap();
        this._cacheKeys = new String[50];
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        this._document = builder.newDocument();
    }

    private void getXMLFileInstance(String filePath, String rootNode) throws IOException {
        this._xmlFile = new File(filePath);
        if (!this._xmlFile.exists()) {
            if (rootNode == null || "".equals(rootNode)) {
                rootNode = "root";
            }

            FileOutputStream fos = new FileOutputStream(this._xmlFile);
            String headAndTail = "<?xml version=\"1.0\"  encoding=\"UTF-8\"?>\r\n<" + rootNode + ">\r\n</" + rootNode + ">";
            fos.write(headAndTail.getBytes());
            fos.close();
        }
    }
}
