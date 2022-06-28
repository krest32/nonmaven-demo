package com.fico.cbs;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fico.cbs.common.message.Response;
import com.fico.cbs.utils.FileHandleUtil;
import com.fico.cbs.utils.JacksonUtil;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CBSInteAppTest {

    @Test
    public void CBSTest() {
        String filename = "e:\\2.xml";
        Document document = null;
        try {
            String xmlString = FileHandleUtil.loadXml(filename);
            System.out.println(xmlString);
            SAXReader saxReader = new SAXReader();
            saxReader.setEncoding("utf-8");
            // 读取XML文件,获得document对象
            InputStream is = new ByteArrayInputStream(xmlString.getBytes("utf-8"));
            document = saxReader.read(is);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String app_num = "MB1230022005250101-0";
        String callType = "call1";
        String callRound= "1";
        String appid = "MB1010022106241299-0";
        String applicationDate = "20210-06-21 16:07:00";

        CBSInteApp cbsInteApp = new CBSInteApp(appid,applicationDate);
        Response response = cbsInteApp.invokeCBS(app_num, callType, callRound, document);
        System.out.println(response.getFields().get("resultCode"));
    }

    /**
     * 将Xml转化为Json
     */
    @Test
    public void  loadXml() throws JsonProcessingException {
        String filename = "e:\\2.xml";
        Document document = null;
        try {
            String xmlString = FileHandleUtil.loadXml(filename);
            System.out.println(xmlString);
            SAXReader saxReader = new SAXReader();
            saxReader.setEncoding("utf-8");
            // 读取XML文件,获得document对象
            InputStream is = new ByteArrayInputStream(xmlString.getBytes("utf-8"));
            document = saxReader.read(is);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Map<String, Object> loadResult = new HashMap<String, Object>();
        User user = new User();
        user.setId("123");
        user.setName("杜鑫");
        loadResult.put("用户信息",user);

        Optional<Element> submitDateOpt = Optional.ofNullable((Element)document.selectSingleNode("/Application/Applicant/Applicant_Individual/i_first_thi_nme"));
        submitDateOpt.ifPresent(submitDate -> {
            String submitDateVal = submitDate.getText();
            if (StringUtils.isNotBlank(submitDateVal)) {
                loadResult.put("book_title", submitDateVal);
            }
        });
        String s = JacksonUtil.bean2Str(loadResult);
        System.out.println(s);

    }

    /**
     * map集合转换Json字符串
     */
    @Test
    public void testJson() throws JsonProcessingException {

        User user = new User();
        user.setId("123");
        user.setName("杜鑫");
        Map<String,Object> map = new HashMap<>();
        map.put("abs",user);
        String s = JacksonUtil.bean2Str(map);
        System.out.println(s);

    }



}
