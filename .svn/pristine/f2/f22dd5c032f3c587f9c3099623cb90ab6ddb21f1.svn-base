package com.fico.cbs.service;

import com.fico.cbs.common.config.SystemConfig;
import com.fico.cbs.common.log.Logger;
import com.fico.cbs.common.log.LoggerFactory;
import com.fico.cbs.common.message.Response;
import com.fico.cbs.common.message.ResultMessage;
import com.fico.cbs.result.TestResult;
import com.fico.integration.http.client.FicoHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.LinkedHashMap;
import java.util.Map;

public class BlazeCBSService {

    private String _sysCode = "";
    private String _url = "";
    private String _apiKey = "";
    private String _timeout = "";

    private Logger _logger = null;

    /**
     * 从Xml中读取CBS服务器的配置，然后来提交请求，最后返回数据
     */
    public BlazeCBSService(String sysCode) throws Exception {
        this._sysCode = sysCode;

        _logger = LoggerFactory.getLogger(_sysCode, BlazeCBSService.class.getName());

        this._url = SystemConfig.getConfigByPath("cbs.url");
        this._apiKey = SystemConfig.getConfigByPath("cbs.api-key");

        System.out.println(this._apiKey);

//		AES encryptAES = new AES();
//		this._apiKey = encryptAES.decryptnew(this._apiKey);

        this._timeout = SystemConfig.getConfigByPath("cbs.timeout");

        if(_sysCode==null || _sysCode.equals("")) {
            ResultMessage paramExceptMessage = new ResultMessage("cbs","CBS_Maj_310");

            _logger.error(String.format(paramExceptMessage.getDescription(), "sysCode"));
        } else {
            _logger.info("Blaze Integration Application Config Parameter[sysCode] " + _sysCode);
        }

        if(_sysCode==null || _sysCode.equals("")) {
            ResultMessage paramExceptMessage = new ResultMessage("cbs","CBS_Maj_310");

            _logger.error(String.format(paramExceptMessage.getDescription(), "url"));
        } else {
            _logger.info("Blaze Integration Application Config Parameter[url] " + _url);
        }
    }

    /**
     * 接收请求，构建并返回的Response结果
     */
    public Response invokeCBSInterfce(String inputJsonParams){
        Response res = new Response();

        //请求参数
        _logger.info("Request CBS Parameter：" + inputJsonParams);

        try
        {
            StringBuffer cbsRequestParam = new StringBuffer();
            cbsRequestParam.append("{\r")
                    .append("\"sysCode\":\"").append(_sysCode).append("\",\r")
                    .append("\"inputParams\":").append(inputJsonParams).append("\r")
                    .append("}");

            Map<String,String> dataParams = new LinkedHashMap<String,String>();
            dataParams.put("pm", cbsRequestParam.toString());

            FicoHttpClient client = new FicoHttpClient(_url, _apiKey);
            client.setTimeout(Integer.parseInt(_timeout));

            // 建立HttpClient对象
            CloseableHttpClient httpClient = client.getHttpClient();
            // 使用HttpClient进行数据提交
            // String result = client.execute(httpClient, dataParams);
            String result = TestResult.testRes;
            // res就是一个HashMap<String,Object>
            res.setField("result", result);

        }catch(Exception ex) {
            ResultMessage  resultMessage = new ResultMessage("cbs","CBS_Maj_281");
            res.setResultMessage(resultMessage);
            _logger.error(ex.getMessage(), ex);
        }

        return res;
    }

}


