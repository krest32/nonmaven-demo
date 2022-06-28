package com.fico.cbs;



import com.fico.cbs.common.config.SystemConfig;
import com.fico.cbs.common.database.DataAccess;
import com.fico.cbs.common.database.DataAccessFactory;
import com.fico.cbs.common.database.storage.DataMap;
import com.fico.cbs.common.message.Response;
import com.fico.cbs.common.message.ResultMessage;
import com.fico.cbs.others.Blaze_Inte_CBS_Constant;
import com.fico.cbs.others.Blaze_Inte_CBS_Tables;
import com.fico.cbs.service.BlazeCBSService;
import com.fico.cbs.utils.FileHandleUtil;
import com.fico.cbs.utils.JacksonUtil;
import com.fico.cbs.utils.XmlCBSUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

public class CBSInteApp {

    /***
     * 生成一个日志的对象，用来操作日子
     */
    private Logger _logger = Logger.getLogger(CBSInteApp.class);

    private String _AppId = "";
    private String _ApplicationDate = "";
    private DataAccess _dbAccess;

    // 配置参数
    private String _sysCode="";
    private String _inputFilePath = "";
    private String _outputFilePath = "";

    private String _mapParamFile = "";


    /**
     *     构造方法中，实现初始化操作
     */
    public CBSInteApp(String appId,String applicationDate) {

        // 程序的初始化操作
        try {
            this._AppId = appId;
            this._ApplicationDate = applicationDate;

            // 从XMl中读取到系统配置参数，然后进行赋值
            this._sysCode = SystemConfig.getConfigByPath("cbs.sysCode");
            // 感觉是个无用的参数
            this._inputFilePath = SystemConfig.getConfigByPath("cbs.input_directory");
            this._outputFilePath = SystemConfig.getConfigByPath("cbs.output_directory");
            this._mapParamFile = SystemConfig.getConfigByPath("cbs.data-map-file");

            // 校验系统的参数配置 sysCode 是否为null
            if (StringUtils.isBlank(this._sysCode)) {
                ResultMessage sysCodeMessage = new ResultMessage("cbs","CBS_Maj_310");
                _logger.error(sysCodeMessage.getDescription());
            }

            // 建立数据库链接
            _dbAccess = DataAccessFactory.getDataAccess(this._AppId, CBSInteApp.class.getName());
            _dbAccess.setAutoCommit(false);

        } catch (Exception ex) {
            _logger.error(ex.toString(), ex);
        }
    }


    public Response invokeCBS(String app_num, String callType, String callRound, Document bomXmlDoc){
        // 构建返回结果
        Response respResult = new Response();
        _logger.info("Blaze access CBS main process start .....");
        // log开始记录时间
        long beginTime = System.currentTimeMillis();
        _logger.info("1.检查Blaze集成应用调用参数。");

        // 1.判断输入程序的参数是否为空 app_num、callType、callRound、bomXmlDoc
        if(StringUtils.isBlank(app_num) || StringUtils.isBlank(callType) ||
                StringUtils.isBlank(callRound) || bomXmlDoc == null) {
            // 构建错误的返回信息 包含： status、code、 message
            respResult = buildResponse(Blaze_Inte_CBS_Constant.INTE_CBS_RESPONSE_STATUS_FAIL,"305","CBS_Maj_305");
            _logger.error(respResult.getResultMessage());
            return respResult;
        }

        // 2. 加载数据映射参数配置文件，构建请求参数映射数据文档
        try{
            _logger.info("2.装载BOM数据抽取映射项参数文件。");
            // Dom4j 将整个Xml文档作为一个对象
            Document mapParams = null;
            try {
                // 加载 Blaze2CBS.xml 文件，将文件转化为字符串
                String inputMapParamStr = FileHandleUtil.loadXml(_mapParamFile);
                SAXReader sax = new SAXReader();
                sax.setEncoding("utf-8");
                // 将字符串转化为 XML 操作对象
                InputStream is = new ByteArrayInputStream(inputMapParamStr.getBytes("utf-8"));
                mapParams = sax.read(is);

                _logger.info("生成Xml操作对象");

            } catch(Exception ex) {
                respResult = buildResponse(Blaze_Inte_CBS_Constant.INTE_CBS_RESPONSE_STATUS_FAIL,"306","CBS_Maj_306");

                _logger.error(respResult.getResultMessage());
                _logger.error(ex.getMessage(), ex);

                return respResult;
            }

            // 3.抽取CBS请求参数
            
            String requestParam = "";
            try {

                _logger.info("3.抽取BOM对象中的数据项,组织CBS的请求参数.");

                // 获取数据库操作对象和程序运行环境参数
                XmlCBSUtil xmlUtil = new XmlCBSUtil(this._dbAccess, SystemConfig.getConfigByPath("cbs.execute-mode"));

                Map<String, Object> inputParams = new LinkedHashMap<String, Object>();
                // 抽取参数信息，生成一个 Map 集合请求对象

                inputParams = xmlUtil.extractCBSData(app_num, callType, callRound, mapParams, bomXmlDoc);

                Map<String, Object> dataResult = new HashMap<String, Object>();

                dataResult.put("Application", inputParams);
                // 请求参数转化为字符串
                requestParam = JacksonUtil.bean2Str(dataResult);
                // 保存调用CBS输入数据文件

                System.out.println("requestParam:"+  requestParam);

                _logger.info("保存请求到本地");

                saveCBSInputFile(app_num, callType, callRound, requestParam);

            } catch(Exception ex) {

                respResult = buildResponse(Blaze_Inte_CBS_Constant.INTE_CBS_RESPONSE_STATUS_FAIL,"307","CBS_Maj_307");
                _logger.error(respResult.getResultMessage());
                _logger.error(ex.getMessage(), ex);

                return respResult;
            }

            // 4.请求CBS服务,并接收响应，将返回信息保存到本地
            try {
                _logger.info("4.请求CBS服务,并接收响应.");
                // 获取请求服务器的对象：url+key
                BlazeCBSService cbsService = new BlazeCBSService(this._sysCode);
                // 提交字符串的请求，并且接收响应
                Response invokeResp = cbsService.invokeCBSInterfce(requestParam);
                //  获取响应结果的String值
                String cbsOutputData = (String)invokeResp.getField("result");

                if (StringUtils.isNotBlank(cbsOutputData)) {
                    // 将信息保存到本地进行存储
                    saveCBSOutputFile(app_num, callType, callRound, cbsOutputData);
                    // 构建返回对象信息
                    respResult = parseResponse(XmlCBSUtil.getBlazeDecision(bomXmlDoc),cbsOutputData);
                    // 将结果保存为Map集合
                    DataMap responseData = (DataMap)respResult.getField("responseData");
                    if (responseData != null) {
                        // 提取打分的结果
                        DataMap scoreData = (DataMap)responseData.get("decision");
                        scoreData.put("aiResonList", responseData.get("reasons"));
                        respResult.setField("aiResult", scoreData);
                    }
                } else {
                    respResult.setField("status", Blaze_Inte_CBS_Constant.INTE_CBS_RESPONSE_STATUS_FAIL);
                    respResult.setField("resultCode", invokeResp.getField("resultCode"));
                    respResult.setResultMessage(invokeResp.getResultMessage());
                }
            } catch(Exception ex) {
                respResult = buildResponse(Blaze_Inte_CBS_Constant.INTE_CBS_RESPONSE_STATUS_FAIL,"cbs","CBS_Maj_284");
                respResult.setField("resultCode", 284);
                _logger.error(respResult.getResultMessage());
                _logger.error(ex.getMessage(), ex);
                return respResult;
            }


            try {
                // 5. 生成调用日志
                DataMap logData = buildLogData(app_num, callType,
                        Integer.valueOf(callRound),
                        String.valueOf(respResult.getField("resultCode")));
                // 将日志信息保存导数据库当中
                singleInsert(Blaze_Inte_CBS_Tables.call_log, logData);
                // 手动提交
                this._dbAccess.commit();

            } catch(SQLException ex) {
                respResult =  buildResponse(Blaze_Inte_CBS_Constant.INTE_CBS_RESPONSE_STATUS_FAIL,"399","CBS_Maj_399");
                this._logger.error(respResult.getResultMessage());
                this._logger.error(ex.getMessage(), ex);
                this._dbAccess.rollback();
            }


            // 结束
        } catch(Exception ex) {
            respResult = buildResponse(Blaze_Inte_CBS_Constant.INTE_CBS_RESPONSE_STATUS_FAIL,"399","CBS_Maj_399");
            _logger.error(respResult.getResultMessage());
            _logger.error(ex.getMessage(), ex);
        } finally {
            // 流程只能完毕，记录日志
            long endTime = System.currentTimeMillis();
            _logger.info("Blaze access CBS elapsed time:"+(endTime-beginTime)*0.001+" second.");
            _logger.info("Blaze access CBS main process end .");
        }

        System.out.println("respResult:"+respResult);

        return respResult;

    }

    private DataMap buildLogData(String appId, String callType, Integer callRound, String responseCode) {
        DataMap resultData = new DataMap();
        resultData.put("AppID", appId);
        resultData.put("CallType", callType);
        resultData.put("CallRound", callRound);
        resultData.put("ResponseCode", responseCode);
        return resultData;
    }

    public void printInfo(Map df)
    {
        Iterator iter = df.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            _logger.info(key + "=" + val);
        }

    }

    private Response parseResponse(String blazeRecommendation, String jsonData) {
        Response parseResult = new Response();

        try {
            // Map转化为Jason字符串
            Map<String, Object> datas = JacksonUtil.str2Bean(jsonData);

            // 打印信息
            printInfo(datas);

            Object objResultCode = datas.get("resultCode");

            Integer resultCode = Integer.parseInt(objResultCode.toString());

            if (resultCode == 100) {

                Object objResult = datas.get("result");
                // 将String转化为Map集合
                Map<String, Object> mp = JacksonUtil.str2Bean(objResult.toString());
                DataMap saveDataMap = buildResponseData(blazeRecommendation,mp);

                printInfo(saveDataMap);

                DataMap decisionData = (DataMap)saveDataMap.get("decision");

                if (decisionData != null && decisionData.size() > 0) {
                    //decisionData.put("blaze_recommendation", blazeRecommendation);

                    singleInsert(Blaze_Inte_CBS_Tables.recommendation_result, decisionData);
                }

                List<DataMap> reasonList = (List<DataMap>)saveDataMap.get("reasons");
                if (reasonList != null && reasonList.size() > 0) {
                    batchInsert(Blaze_Inte_CBS_Tables.recommendation_reason, reasonList);
                }

                List<DataMap> variableList = (List<DataMap>)saveDataMap.get("persons");
                if (variableList != null && variableList.size() > 0) {
                    batchInsert(Blaze_Inte_CBS_Tables.person_variable, variableList);
                }

                parseResult.setField("responseData", saveDataMap);

                parseResult.setField("status", Blaze_Inte_CBS_Constant.INTE_CBS_RESPONSE_STATUS_SUCCESS);

                parseResult.setResultMessage(new ResultMessage("cbs","CBS_Nor_01"));

            } else {
                parseResult.setField("status", Blaze_Inte_CBS_Constant.INTE_CBS_RESPONSE_STATUS_FAIL);

                ResultMessage errorResp = new ResultMessage(String.valueOf(resultCode), "", (String)datas.get("result"));
                parseResult.setResultMessage(errorResp);
            }
            parseResult.setField("resultCode", resultCode);
        } catch(Exception ex) {
            parseResult.setField("status", Blaze_Inte_CBS_Constant.INTE_CBS_RESPONSE_STATUS_FAIL);
            parseResult.setField("resultCode", 284);
            parseResult.setResultMessage(new ResultMessage("cbs","CBS_Maj_284"));
            _logger.error(ex.getMessage(), ex);
        }
        return parseResult;
    }

    private void batchInsert(String recommendation_reason, List<DataMap> reasonList) {
    }

    private void singleInsert(String tableName, DataMap data) {
        
    }

    private DataMap buildResultData(String appId, String callType, Integer callRound,
                                    Double approveSubScore, Double declineSubScore, Double aiScore, String aiDecision, String recommendation) {
        DataMap resultData = new DataMap();
        resultData.put("AppID",appId);
        resultData.put("CallType",callType);
        resultData.put("CallRound",callRound);
        resultData.put("ApproveSubScore", approveSubScore);
        resultData.put("DeclineSubScore", declineSubScore);
        resultData.put("AIScore",aiScore);
        resultData.put("AIDecision",aiDecision);
        resultData.put("BlazeRecommendation",recommendation);
        return resultData;
    }

    private DataMap buildReasonData(String appId, String callType, Integer callRound,
                                    Integer reasonNo, String reasonCode, String reasonDesc) {
        DataMap resultData = new DataMap();
        resultData.put("AppID", appId);
        resultData.put("CallType", callType);
        resultData.put("CallRound", callRound);
        resultData.put("ReasonNO", reasonNo);
        resultData.put("ReasonCode", reasonCode);
        resultData.put("ReasonDescript", reasonDesc);
        return resultData;
    }

    private DataMap buildPersonData(String appId, String callType, Integer callRound,
                                    Integer personNo, String applicantType, String idCardType, String idCardNbr, String custName,
                                    Integer addrSameE, Integer addrSameP, Integer addrSameC, Integer addrDealerSameP, Integer addrDealerSameC,
                                    Integer sameMobile, Double avgHabitancy, Double lastHabitancy, Double avgOccup, Double lastOccup,
                                    Double similarity, Integer hasTrans, Integer hasExten, Integer hasRepay, Integer smallLoanNum,
                                    Integer cardLoadNum, Integer loanNum, Double loanAmt, Double loanBal, Integer icrNum,
                                    Double icrCredAmt, Double icrUsedAmt) {
        DataMap resultData = new DataMap();
        resultData.put("AppID", appId);
        resultData.put("CallType", callType);
        resultData.put("CallRound", callRound);
        resultData.put("PersonNO", personNo);
        resultData.put("ApplicantType", applicantType);
        resultData.put("IDCardType", idCardType);
        resultData.put("IDCardNO", idCardNbr);
        resultData.put("CustName", custName);
        resultData.put("AddrEmploymentSame", addrSameE);
        resultData.put("AddrDealerSameProvince", addrSameP);
        resultData.put("AddrDealerSameCity", addrSameC);
        resultData.put("EmploymentDealerSameProvince", addrDealerSameP);
        resultData.put("EmploymentDealerSameCity", addrDealerSameC);
        resultData.put("SameMobile", sameMobile);
        resultData.put("AvgHabitancyLength", avgHabitancy);
        resultData.put("LastHabitancyLength", lastHabitancy);
        resultData.put("AvgOccupLength", avgOccup);
        resultData.put("LastOccupLength", lastOccup);
        resultData.put("EmployerSimilarity", similarity);
        resultData.put("HasVehiTrans", hasTrans);
        resultData.put("HasExtension", hasExten);
        resultData.put("HasGuarantorRepay", hasRepay);
        resultData.put("SmallLoanNum", smallLoanNum);
        resultData.put("NewCarLoanNum", cardLoadNum);
        resultData.put("NewLoanNum", loanNum);
        resultData.put("NewLoanAmt", loanAmt);
        resultData.put("NewLoanBal", loanBal);
        resultData.put("NewICRNum", icrNum);
        resultData.put("NewCredAmtICR", icrCredAmt);
        resultData.put("NewUsedAmtICR", icrUsedAmt);
        return resultData;
    }

    private DataMap buildResponseData(String blazeDecision, Map<String, Object> datas) {
        if (datas == null || datas.size() == 0) return null;

        String appId = String.valueOf(datas.get("application_number"));
        String callType = String.valueOf(datas.get("calltype"));
        Integer callRound = (Integer)datas.get("callround");

        // 获得结果数据  CBS_Recommendation 必须大小写的
        DataMap decisionData = buildResultData(appId, callType, callRound,
                (Double)datas.get("ApproveSubScore"),
                (Double)datas.get("DeclineSubScore"),
                (Double)datas.get("ai_score"),
                String.valueOf(datas.get("CBS_Recommendation")),
                blazeDecision);

        // 获得原因数据
        List<Map<String, Object>> reasonList = new ArrayList<Map<String,Object>>();
        List<Map<String, Object>> detailReason = (List<Map<String, Object>>)datas.get("reason_list");
        if (detailReason != null && detailReason.size() > 0) {
            for(int rIndex = 0; rIndex < detailReason.size(); rIndex++) {
                Map<String, Object> reasonItem = buildReasonData(appId,callType,callRound,rIndex+1,
                        String.valueOf(detailReason.get(rIndex).get("reason_code")),
                        String.valueOf(detailReason.get(rIndex).get("reason_descript")));
                reasonList.add(reasonItem);
            }
        }

        // 获得人员数据
        List<Map<String, Object>> personList = new ArrayList<Map<String,Object>>();
        List<Map<String, Object>> detailSummary = (List<Map<String, Object>>)datas.get("detailedAISummary");
        if (detailSummary != null && detailSummary.size() > 0) {
            for(int pIndex=0; pIndex < detailSummary.size(); pIndex++) {
                DataMap resultItem = buildPersonData(appId,callType,callRound,pIndex+1,
                        String.valueOf(detailSummary.get(pIndex).get("applicant_type")),
                        String.valueOf(detailSummary.get(pIndex).get("id_card_typ")),
                        String.valueOf(detailSummary.get(pIndex).get("id_card_nbr")),
                        String.valueOf(detailSummary.get(pIndex).get("first_thi_name")),
                        (Integer)detailSummary.get(pIndex).get("addr_employment_same"),
                        (Integer)detailSummary.get(pIndex).get("addr_dealer_same_province"),
                        (Integer)detailSummary.get(pIndex).get("addr_dealer_same_city"),
                        (Integer)detailSummary.get(pIndex).get("employment_dealer_same_province"),
                        (Integer)detailSummary.get(pIndex).get("employment_dealer_same_city"),
                        (Integer)detailSummary.get(pIndex).get("same_mobile"),
                        ((Number)detailSummary.get(pIndex).get("avg_habitancy_length")).doubleValue(),
                        ((Number)detailSummary.get(pIndex).get("last_habitancy_length")).doubleValue(),
                        ((Number)detailSummary.get(pIndex).get("avg_occup_length")).doubleValue(),
                        ((Number)detailSummary.get(pIndex).get("last_occup_length")).doubleValue(),
                        ((Number)detailSummary.get(pIndex).get("employer_similarity")).doubleValue(),
                        (Integer)detailSummary.get(pIndex).get("has_vehitrans"),
                        (Integer)detailSummary.get(pIndex).get("has_extension"),
                        (Integer)detailSummary.get(pIndex).get("has_guarantor_repay"),
                        (Integer)detailSummary.get(pIndex).get("small_loan_num"),
                        (Integer)detailSummary.get(pIndex).get("new_car_loan_num"),
                        (Integer)detailSummary.get(pIndex).get("new_loan_num"),
                        ((Number)detailSummary.get(pIndex).get("new_loan_amt")).doubleValue(),
                        ((Number)detailSummary.get(pIndex).get("new_loan_bal")).doubleValue(),
                        (Integer)detailSummary.get(pIndex).get("new_icr_num"),
                        ((Number)detailSummary.get(pIndex).get("new_cred_amt_icr")).doubleValue(),
                        ((Number)detailSummary.get(pIndex).get("new_used_amt_icr")).doubleValue());

                personList.add(resultItem);
            }
        }

        // 处理提取出来响应数据
        DataMap resultData = new DataMap();
        if (decisionData != null && decisionData.size() > 0) {
            resultData.put("decision", decisionData);
        }
        if (reasonList != null && reasonList.size() > 0) {
            resultData.put("reasons", reasonList);
        }
        if (personList != null && personList.size() > 0) {
            resultData.put("persons", personList);
        }

        return resultData;
    }

    private void saveCBSOutputFile(String app_num, String callType, String callRound, String cbsOutputData) {
    }

    private void saveCBSInputFile(String app_num, String callType, String callRound, String requestParam) {
    }

    /**
     * 构建需要返回的信息
     */
    private Response buildResponse(String status, String resultCode, String messageCode) {
        Response result = new Response();

        result.setField("status", status);
        result.setField("resultCode", resultCode);
        if (!StringUtils.isBlank(messageCode)) {
            result.setResultMessage(new ResultMessage("cbs", messageCode));
        }
        return result;
    }

}
