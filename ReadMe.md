# 整个项目的搭建

## 项目介绍

### 项目结构->基于Idea

测试svn



![image-20210702102646080](img/image-20210702102646080.png)

### 配置文件详解

`demo-java.iml`

~~~xml
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
    // idea 项目借口配置
    <component name="NewModuleRootManager">
        // 配置 编译好的Class文件的输出目录
        <output url="file://$MODULE_DIR$/bin" />
        <exclude-output />
        // 配置项目的主要文件目录结构
        <content url="file://$MODULE_DIR$">
            <sourceFolder url="file://$MODULE_DIR$/src/main/java" isTestSource="false" />
            <sourceFolder url="file://$MODULE_DIR$/src/main/resources" isTestSource="false" />
            <sourceFolder url="file://$MODULE_DIR$/src/test/java" isTestSource="false" />
            <sourceFolder url="file://$MODULE_DIR$/src/test/resources" isTestSource="false" />
        </content>
        
        <orderEntry type="sourceFolder" forTests="false" />
        
        // 配置Java Jdk的版本
        <orderEntry type="jdk" jdkName="jdk1.8.0_77" jdkType="JavaSDK" />
		
        // 配置项目Library目录，每一个Jar包都要进行配置
        
        <orderEntry type="module-library">
            <library name="commons-lang-2.5.jar">
                <CLASSES>
                    <root url="jar://$MODULE_DIR$/lib/commons-lang-2.5.jar!/" />
                </CLASSES>
                <JAVADOC />
                <SOURCES />
            </library>
        </orderEntry>

        <orderEntry type="module-library">
            <library name="dom4j-1.6.1.jar">
                <CLASSES>
                    <root url="jar://$MODULE_DIR$/lib/dom4j-1.6.1.jar!/" />
                </CLASSES>
                <JAVADOC />
                <SOURCES />
            </library>
        </orderEntry>

        <orderEntry type="module-library">
            <library name="httpclient-4.5.3.jar">
                <CLASSES>
                    <root url="jar://$MODULE_DIR$/lib/httpclient-4.5.3.jar!/" />
                </CLASSES>
                <JAVADOC />
                <SOURCES />
            </library>
        </orderEntry>


    </component>
</module>

~~~



## 程序运行流程

提取XML中的数据，制作成Json数据，访问服务器，服务器返回String字符串，我们提取成为Json格式的数据

### 1. 读取配置（构造函数）

~~~
this._sysCode = SystemConfig.getConfigByPath("cbs.sysCode");
this._inputFilePath = SystemConfig.getConfigByPath("cbs.input_directory");
this._outputFilePath = SystemConfig.getConfigByPath("cbs.output_directory");
this._mapParamFile = SystemConfig.getConfigByPath("cbs.data-map-file");
~~~

~~~xml
<cbs>
    <sysCode>Blaze_CBS</sysCode>
    <input_directory>D:/Blaze_Inte_CBS/BlazeIntegrationFiles/#Date#/CBS/</input_directory>
    <output_directory>D:/Blaze_Inte_CBS/BlazeIntegrationFiles/#Date#/CBS/</output_directory
	<data-map-file>E:/project/demo-java/src/main/resources/config/Blaze2CBS.xml</data-map-file>
</cbs>
~~~



### 2. 连接数据库（构造函数）

~~~java
// 建立数据库链接
//_AppId 方便在日志记录中，记录这个id工作的进度 class也是一个class的名称，方便记录
_dbAccess = DataAccessFactory.getDataAccess(this._AppId, CBSInteApp.class.getName());
_dbAccess.setAutoCommit(false);

~~~

#### 数据库代码

~~~
public static DataAccess getDataAccess(String id, String clazz) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        int i;
        for(i = 0; i < MAX_CONNECTION_NUM; ++i) {
            _logger.info("========111========");
            _logger.info(id);
            _logger.info(_ids[i]);
            if (id.equalsIgnoreCase(_ids[i])) {
                _dataaccess[i].setLog(LoggerFactory.getLogger(id, clazz));
                return _dataaccess[i];
            }
        }

        for(i = 0; i < MAX_CONNECTION_NUM; ++i) {
            _logger.info("========222========");
            _logger.info(id);
            _logger.info(_ids[i]);
            if (_ids[i] == null || "".equals(_ids[i])) {
                _ids[i] = id;
                _dataaccess[i].setLog(LoggerFactory.getLogger(id, clazz));
                return _dataaccess[i];
            }
        }

        _logger.info("=====return null=====");
        return null;
    }
~~~



### 3. 业务流程`invokeCBS`

#### 方法传入参数

~~~
String app_num,  // 业务唯一id
String callType,	// 请求类型
String callRound, 
Document bomXmlDoc	// 请求的文件对象
~~~

示例

~~~json
requestParam:{
  "Application" : {
    "application_number" : "MB1230022005250101-0",
    "calltype" : "call1",
    "callround" : "1",
    "General" : {
      "dealer_id" : "851",
      "vehicle_type_cde" : "00001"
    },
    "Assets_Finacial_Term" : {
      "asset_make_cde" : "00001",
      "condition" : "N",
      "usage" : "00001",
      "total_amt_payable" : "356527.64",
      "asset_model_cde" : "11342",
      "asset_subtype" : "00029",
      "new_price" : "429800",
      "amount_financed" : "176950",
      "subsidy_rate" : "8.34",
      "lease_term_in_month" : "18",
      "balloon_percentage" : "50",
      "contract_interest_rate" : "0.99",
      "asset_vehicle_cost_amount" : "353900",
      "down_payment_percentage" : "50"
    },
    "Applicant_Exposure" : {
      "total_exposure" : "176950"
    },
    "Applicant_Borrower_I" : {
      "id_card_typ" : "00001",
      "id_card_nbr" : "110101199209013637",
      "first_thi_nme" : "万俟怀慕",
      "citizenship_dsc" : "00035",
      "sex" : "M",
      "isnew" : "true",
      "pre_decline_flag" : "false",
      "verified_income" : "20000",
      "pboc_report_status" : "X1_record",
      "borrower_score" : "410.0",
      "Address" : [ {
        "address_type_cde" : "00001",
        "city" : "成都市",
        "state_cde" : "四川省",
        "time_in_year" : "0",
        "time_in_month" : "5",
        "property_type_cde" : "00004",
        "PhoneFax" : [ {
          "phone_type_cde" : "00003",
          "country_code" : "1088",
          "phone_number" : "12311111111"
        }, {
          "phone_type_cde" : "00002",
          "country_code" : "1088",
          "phone_number" : "12311111111"
        } ]
      } ],
      "Employment" : [ {
        "employer_type" : "00001",
        "name" : "简阳市人民医院",
        "city" : "成都市",
        "province" : "四川省",
        "years" : "10"
      } ],
      "PBOC_I" : {
        "ReportNo" : "2020011614084973168839",
        "Identity" : {
          "CustName" : "万俟怀慕"
        },
        "Summary_QueryHis" : {
          "PostLoanQueryNum" : "0",
          "MNCQueryNum" : "0",
          "CLQueryOrgs" : "1",
          "CCRQueryOrgs" : "0",
          "CLQueryNum" : "1",
          "CCRQueryNum" : "0",
          "PQueryNum" : "0",
          "GCQQueryNum" : "0"
        },
        "Detail_Query" : [ {
          "KeyNo" : "1",
          "QueryDate" : "2020.01.15",
          "QueryReas" : "贷款审批"
        } ]
      }
    },
    "CallResult" : {
      "dbr" : "0.73",
      "finalscore" : "410.0",
      "creditlevel" : "B",
      "worstruleaction" : "REMINDER_CONSIDER",
      "finaldecision" : "REFER_RECOMMENDATION"
    }
  }
}
~~~



#### 构建返回结果的对象

```
Response respResult = new Response();
```



#### 加载XML配置文件

~~~JAva
String inputMapParamStr = FileHandleUtil.loadXml(_mapParamFile);
//_mapParamFile -> Blaze2CBS.xml
~~~



#### 构建请求参数

~~~
// 抽取参数信息，生成一个 Map 集合请求对象
// mapParams -> Blaze2CBS.xml   bomXmlDoc-> 输入的文件对象
inputParams = xmlUtil.extractCBSData(app_num, callType, callRound, mapParams, bomXmlDoc);
Map<String, Object> dataResult = new HashMap<String, Object>();

dataResult.put("Application", inputParams);
// 请求参数转化为字符串
requestParam = JacksonUtil.bean2Str(dataResult);
// 保存调用CBS输入数据文件
~~~



#### 抽取数据的方法

~~~
inputParams = xmlUtil.extractCBSData(app_num, callType, callRound, mapParams, bomXmlDoc);
~~~

~~~Java
 Optional<Element> submitDateOpt = Optional.ofNullable((Element)document.selectSingleNode("/Application/Applicant/Applicant_Individual/i_first_thi_nme"));
        submitDateOpt.ifPresent(submitDate -> {
            String submitDateVal = submitDate.getText();
            if (StringUtils.isNotBlank(submitDateVal)) {
                loadResult.put("book_title", submitDateVal);
            }
        });
~~~

~~~json
requestParam:{
  "Application" : {
    "application_number" : "MB1230022005250101-0",
    "calltype" : "call1",
    "callround" : "1",
    "General" : {
      "dealer_id" : "851",
      "vehicle_type_cde" : "00001"
    },
    "Assets_Finacial_Term" : {
      "asset_make_cde" : "00001",
      "condition" : "N",
      "usage" : "00001",
      "total_amt_payable" : "356527.64",
      "asset_model_cde" : "11342",
      "asset_subtype" : "00029",
      "new_price" : "429800",
      "amount_financed" : "176950",
      "subsidy_rate" : "8.34",
      "lease_term_in_month" : "18",
      "balloon_percentage" : "50",
      "contract_interest_rate" : "0.99",
      "asset_vehicle_cost_amount" : "353900",
      "down_payment_percentage" : "50"
    },
    "Applicant_Exposure" : {
      "total_exposure" : "176950"
    },
    "Applicant_Borrower_I" : {
      "id_card_typ" : "00001",
      "id_card_nbr" : "110101199209013637",
      "first_thi_nme" : "万俟怀慕",
      "citizenship_dsc" : "00035",
      "sex" : "M",
      "isnew" : "true",
      "pre_decline_flag" : "false",
      "verified_income" : "20000",
      "pboc_report_status" : "X1_record",
      "borrower_score" : "410.0",
      "Address" : [ {
        "address_type_cde" : "00001",
        "city" : "成都市",
        "state_cde" : "四川省",
        "time_in_year" : "0",
        "time_in_month" : "5",
        "property_type_cde" : "00004",
        "PhoneFax" : [ {
          "phone_type_cde" : "00003",
          "country_code" : "1088",
          "phone_number" : "12311111111"
        }, {
          "phone_type_cde" : "00002",
          "country_code" : "1088",
          "phone_number" : "12311111111"
        } ]
      } ],
      "Employment" : [ {
        "employer_type" : "00001",
        "name" : "简阳市人民医院",
        "city" : "成都市",
        "province" : "四川省",
        "years" : "10"
      } ],
      "PBOC_I" : {
        "ReportNo" : "2020011614084973168839",
        "Identity" : {
          "CustName" : "万俟怀慕"
        },
        "Summary_QueryHis" : {
          "PostLoanQueryNum" : "0",
          "MNCQueryNum" : "0",
          "CLQueryOrgs" : "1",
          "CCRQueryOrgs" : "0",
          "CLQueryNum" : "1",
          "CCRQueryNum" : "0",
          "PQueryNum" : "0",
          "GCQQueryNum" : "0"
        },
        "Detail_Query" : [ {
          "KeyNo" : "1",
          "QueryDate" : "2020.01.15",
          "QueryReas" : "贷款审批"
        } ]
      }
    },
    "CallResult" : {
      "dbr" : "0.73",
      "finalscore" : "410.0",
      "creditlevel" : "B",
      "worstruleaction" : "REMINDER_CONSIDER",
      "finaldecision" : "REFER_RECOMMENDATION"
    }
  }
}
~~~

#### 将数据转化为Map集合

~~~java
    Map<String, Object> loadResult = new HashMap<String, Object>();
    User user = new User();
    user.setId("123");
    user.setName("杜鑫");
    loadResult.put("用户信息",user);
~~~



#### Map集合转换为 `Json`字符串

~~~
 String s = JacksonUtil.bean2Str(loadResult);
~~~



#### 访问服务器，得到返回数据

~~~json
{
	"APPID": "MB1010022106241299-0",
	"CALLROUND": 1,
	"CALLTYPE": "call1",
	"AppType": "00001",
	"CREATEDATE": "20210-06-21 16:07:00",
	"Assets_Finacial_Term": {
		"AssetMake": "00001",
		"Condition": "N",
		"Usage": "00001",
		"TotalPayable": 200000.00,
		"AssetMod": "11171",
		"NewPrice": 346800,
		"DownPayPer": 50,
		"FinaAmt": 173400,
		"SubsidyRate": 8.8,
		"MLeaseTerm": 36,
		"BallPer": 0
	},
	"Applicant": [{
				"DataDate": "2021-06-21",
				"IDCardNO": "340406198608215881",
				"CustName": "刘先华",
				"CustRole": "borrower",
				"SexSign": "F",
				"IsNew": "false",
				"PreDeclineFlag": "true",
				"VerifiedIncome": 6000,
				"Address": [{
					"AddrType": "00001",
					"Living_City": "毫州市",
					"Living_StateCode": "安徽省",
					"Living_TimeInYear": 2,
					"PropertyType": "00004"
				}],
					"Employment": [{
						"EmplType": "00007",
						"EmprCity": "汕头市",
						"EmprProv": "广东省"
					}],
					"PBOC": {
						"ReportNO": "",
						"PBOCStatus": "",
						"PBOC_Summary_Data": {
							"BirthDay": "",
							"MarrSign": "",
							"EducSign": "",
							"DegreeSign": "",
							"LoanCorpAgenNum": 1,
							"LoanAgenNum": 1,
							"LoanNum": 1,
							"LoanContAmt": 1000,
							"LoanBal": 1000,
							"AverPayment": 100,
							"IssuCorpAccNum": 10,
							"IssuAgenNum": 10,
							"AccountNum": 10,
							"CreditAmt": 100.00,
							"MaxCredAmt": 100.00,
							"MinCredAmt": 100.00,
							"UsedAmt": 100.00,
							"AverUsedAmt": 100.00,
							"QCRIssuCorpAccNum": 1,
							"QCRIssuAgenNum": 1,
							"QCRAccountNum": 1,
							"QCRCreditAmt": 100.00,
							"QCRMaxCreditAmt": 100.00,
							"QCRMinCredAmt": 100.00,
							"QCROdrawBal": 100.00,
							"QCRAverOdrawBal": 100.00,
							"BadLoanNum": 1,
							"BadLoanBal": 100.00,
							"AssDispNum": 1,
							"AssDispBal": 100.00,
							"GuarPayNum": 1,
							"GuarPayBal": 10.00,
							"LoanOdueNum": 1,
							"LoanOdueMons": 1,
							"LoanMaxOdueAmt": 1000.00,
							"LoanMaxOdueMons": 2,
							"ICROdueNum": 2,
							"ICROdueMons": 2,
							"ICRMaxOdueAmt": 3,
							"ICRMaxOdueMons": 3,
							"QCROdrawNum": 3,
							"QCROdrawMons": 3,
							"QCRMaxOdrawBal": 1000.00,
							"QCRMaxOdrawMons": 2,
							"HouLoanNum": 1,
							"OthLoanNum": 1,
							"ICRAccNum": 1,
							"QCRAccNum": 1,
							"SelfDeclNum": 1,
							"ObjLabelNum": 1,
							"PersonHouLoanNum": 1,
							"BusinessHouLoanNum": 1,
							"AccountSum": 1,
							"BusTypeNum": 1,
							"PostLoanQueryNum": 1,
							"MNCQueryNum": 2,
							"CLQueryOrgs": 2,
							"CCRQueryOrgs": 2,
							"CCRQueryNum": 1,
							"CLQueryNum": 1,
							"PQueryNum": 1,
							"GCQQueryNum": 1,
							"PBOC_Score": 800
						},
						"PBOC_Detail": {
							"IndiNLoanDetail_HouLoan": [{
								"Loan_HouLoan_BeginDate": "",
								"LoanType_HouLoan": "",
								"LoanAmt_HouLoan": 1000.00,
								"LoanBal_HouLoan": 1000.00,
								"Loan_HouLoan_Account_Status": "1",
								"LoanClassResu_HouLoan": "关注",
								"Loan_HouLoan_Curr_Odue_Num": 1,
								"Loan_HouLoan_Curr_Odue_Amt": 1000.00,
								"KeyNo": 2
							}],
							"IndiNLoanDetail_PerBizLoan": [{
								"Loan_PerBizLoan_BeginDate": "2021-06-21 16:44:50",
								"LoanType_PerBizLoan": "",
								"LoanAmt_PerBizLoan": 1000.00,
								"LoanBal_PerBizLoan": 1000.00,
								"Loan_PerBizLoan_Account_Status": 1,
								"LoanClassResu_PerBizLoan": "",
								"Loan_PerBizLoan_Curr_Odue_Num": 1,
								"Loan_PerBizLoan_Curr_Odue_Amt": 1000.00,
								"KeyNo": 35
							}],
							"IndiNLoanDetail_OthLoan": [{
								"Loan_OthLoan_BeginDate": "2021-06-21 16:47:00",
								"LoanType_OthLoan": "",
								"LoanAmt_OthLoan": 1000.00,
								"LoanBal_OthLoan": 1000.00,
								"Loan_OthLoan_Account_Status": 1,
								"LoanClassResu_OthLoan": "关注",
								"Loan_OthLoan_Curr_Odue_Num": 1,
								"Loan_OthLoan_Curr_Odue_Amt": 1000.00
							}],
							"IndiNLoanDetail_PerConsumLoan": [{
								"Loan_PerConsumLoan_BeginDate": "",
								"LoanType_PerConsumLoan": "",
								"LoanAmt_PerConsumLoan": 1000.00,
								"LoanBal_PerConsumLoan": 1000.00,
								"Loan_PerConsumLoan_Account_Status": 1,
								"LoanClassResu_PerConsumLoan": "关注",
								"Loan_PerConsumLoan_Curr_Odue_Num": 1,
								"Loan_PerConsumLoan_Curr_Odue_Amt": 1000.00
							}],
							"IndiNLICRDetail_Not_RMB": [{
								"CCRD_BeginDate_Not_RMB": "",
								"CCRD_CredAmt_Not_RMB": 100.00,
								"CCRD_UsedAmt_Not_RMB": 100.00,
								"CCRD_Curr_Odue_Num_Not_RMB": 1,
								"CCRD_Curr_Odue_Amt_Not_RMB": 100.00,
								"CCRD_Account_Status_Not_RMB": 1,
								"KeyNo": 2
							}],
							"IndiNICRDetail_RMB": [{
								"CCRD_BeginDate_RMB": "",
								"CCRD_CredAmt_RMB": 1000.00,
								"CCRD_UsedAmt_RMB": 1000.00,
								"CCRD_Curr_Odue_Num_RMB": 1,
								"CCRD_Curr_Odue_Amt_RMB": 1000.00,
								"CCRD_Account_Status_RMB": 1,
								"KeyNo": 3
							}],
							"IndiNSpecTrade_Pay_BeforeBand": [{
								"SpecTrade_Type_Pay_Beforehand": "",
								"SpecTrad_Amt_Pay_Beforehand": 1000.00,
								"KeyNo": 2
							}],
							"IndiNSpecTrade_Extension": [{
								"SpecTrade_Type_Extension": "",
								"SpecTrad_Amt_Extension": 1000.00,
								"KeyNo": 5
							}],
							"IndiNSpecTrade_Other": [{
								"SpecTrade_Type_Other": "",
								"SpecTrad_Amt_Other": 1000.00,
								"KeyNo": 3
							}],
							"IndiNInhabitancy": [{
								"Inhabitancy_Status": "",
								"Inhabitancy_UpdateDate": "",
								"KeyNo": 3
							}],
							"IndiNOccupation_Decreasing_Order": [{
								"Occupation_Position": "",
								"Occupation_JobTitle": "",
								"Occupation_UpdateDate": "2021-06-22 10:10:10",
								"KeyNo": 3
							}],
							"IndiNGuarODetail": [{
								"Guaranteed_Loan_BeginDate": "",
								"GuarAmt": 1000.00,
								"Guaranteed_Loan_CapiBal": 1000.00,
								"Guaranteed_Loan_ClassResu": "",
								"KeyNo": 3
							}],
							"IndiNHousRes": [{
								"HouRes_BeginDate": "",
								"HouRes_EndMonth": 3,
								"HouRes_Status": 1,
								"HouRes_Monthly_PayAmt": 1000.00,
								"HouRes_Indi_PayPer": 1000.00,
								"HouRes_Comp_PayPer": 100.00,
								"KeyNo": 3
							}],
							"IndiNEndowInsuPay": [{
								"Pension_Pay_BeginDate": "",
								"Pension_Pay_Status": "3",
								"Pension_Pay_IndiPayRate": "",
								"Pension_CurrPayAmt": 1000.00
							}],
							"IndiNEndowInsuProv": [{
								"CurrProvAmt": 1000.00,
								"RetireSign": ""
							}],
							"IndiNAssDisp": [{
								"AssetDisp_DebtAmt": 1000.00,
								"AssetDisp_Balance": 1000.00,
								"KeyNo": 3
							}],
							"IndiNGuarRepay_Decreasing_Order": [{
								"GuarRepay_TotalAmt": 1000.00,
								"After_GuarRepay_Balance": 500.00,
								"KeyNo": 3
							}],
							"IndiNTelePay_Decreasing_Order": [{
								"TelePay_CurrOdueAmt": 1000.00,
								"TelePay_CurrOdueMons": 3,
								"TelePay_Status": "1",
								"KeyNo": 4
							}],
							"IndiNOwnTax": [{
								"OwnTax_Amt": 1000.00,
								"KeyNo": 3
							}],
							"IndiNEnforce_Decreasing_Order": [{
								"Enforced_Case_Status": 3,
								"Case_Applied_Amt": 1000.00,
								"Case_Enforced_Amt": 1000.00,
								"KeyNo": 3
							}],
							"IndiNCertificate": [{
								"CertLevel": A,
								"Cert_BeginDate": "2021-06-01",
								"Cert_EndDate": "2021-06-03",
								"Cert_RevokeDate": "2021-06-03",
								"KeyNo": 3
							}]
						}
					},
					"Bairong":{
					   "BR_Data": [
						  {
							"BRRuleId": "all_reminder",
							"BRRuleDesc": "0"
						  },
						  {
							"BRRuleId": "income_flag",
							"BRRuleDesc": "NO"
						  },
						  {
							"BRRuleId": "ir_reminder",
							"BRRuleDesc": "0"
						  },
						  {
							"BRRuleId": "sl_reminder",
							"BRRuleDesc": "0"
						  },
						  {
							"BRRuleId": "als_reminder",
							"BRRuleDesc": "0"
						  },
						  {
							"BRRuleId": "bv_reminder",
							"BRRuleDesc": "0"
						  }
						],
						"pcp_income": "[0,5]"
					}
				},
				{
				"DataDate": "2021-06-21",
				"IDCardNO": "620502197303076211",
				"CustName": "勿动谢谢",
				"CustRole": "co-borrower",
				"SexSign": "M",
				"IsNew": "false",
				"PreDeclineFlag": "true",
				"VerifiedIncome": 6000,
				"Address": [{
					"AddrType": "00001",
					"Living_City": "丽水市",
					"Living_StateCode": "浙江省",
					"Living_TimeInYear": 12,
					"PropertyType": "00004"
				}],
					"Employment": [{
						"EmplType": "00007",
						"EmprCity": "丽水市",
						"EmprProv": "浙江省"
					}],
					"PBOC": {
						"ReportNO": "2021062318112238966730",
						"PBOCStatus": "normal",
						"PBOC_Summary_Data": {
							"BirthDay": "1973-03-07",
							"MarrSign": "",
							"EducSign": "",
							"DegreeSign": "",
							"LoanCorpAgenNum": 1,
							"LoanAgenNum": 1,
							"LoanNum": 1,
							"LoanContAmt": 1000,
							"LoanBal": 1000,
							"AverPayment": 100,
							"IssuCorpAccNum": 10,
							"IssuAgenNum": 10,
							"AccountNum": 10,
							"CreditAmt": 100.00,
							"MaxCredAmt": 100.00,
							"MinCredAmt": 100.00,
							"UsedAmt": 100.00,
							"AverUsedAmt": 100.00,
							"QCRIssuCorpAccNum": 1,
							"QCRIssuAgenNum": 1,
							"QCRAccountNum": 1,
							"QCRCreditAmt": 100.00,
							"QCRMaxCreditAmt": 100.00,
							"QCRMinCredAmt": 100.00,
							"QCROdrawBal": 100.00,
							"QCRAverOdrawBal": 100.00,
							"BadLoanNum": 1,
							"BadLoanBal": 100.00,
							"AssDispNum": 1,
							"AssDispBal": 100.00,
							"GuarPayNum": 1,
							"GuarPayBal": 10.00,
							"LoanOdueNum": 1,
							"LoanOdueMons": 1,
							"LoanMaxOdueAmt": 1000.00,
							"LoanMaxOdueMons": 2,
							"ICROdueNum": 2,
							"ICROdueMons": 2,
							"ICRMaxOdueAmt": 3,
							"ICRMaxOdueMons": 3,
							"QCROdrawNum": 3,
							"QCROdrawMons": 3,
							"QCRMaxOdrawBal": 1000.00,
							"QCRMaxOdrawMons": 2,
							"HouLoanNum": 1,
							"OthLoanNum": 1,
							"ICRAccNum": 1,
							"QCRAccNum": 1,
							"SelfDeclNum": 1,
							"ObjLabelNum": 1,
							"PersonHouLoanNum": 1,
							"BusinessHouLoanNum": 1,
							"AccountSum": 1,
							"BusTypeNum": 1,
							"PostLoanQueryNum": 1,
							"MNCQueryNum": 2,
							"CLQueryOrgs": 2,
							"CCRQueryOrgs": 2,
							"CCRQueryNum": 1,
							"CLQueryNum": 1,
							"PQueryNum": 1,
							"GCQQueryNum": 1,
							"PBOC_Score": 800
						},
						"PBOC_Detail": {
							"IndiNLoanDetail_HouLoan": [{
								"Loan_HouLoan_BeginDate": "",
								"LoanType_HouLoan": "",
								"LoanAmt_HouLoan": 1000.00,
								"LoanBal_HouLoan": 1000.00,
								"Loan_HouLoan_Account_Status": "1",
								"LoanClassResu_HouLoan": "关注",
								"Loan_HouLoan_Curr_Odue_Num": 1,
								"Loan_HouLoan_Curr_Odue_Amt": 1000.00,
								"KeyNo": 2
							}],
							"IndiNLoanDetail_PerBizLoan": [{
								"Loan_PerBizLoan_BeginDate": "2021-06-21 16:44:50",
								"LoanType_PerBizLoan": "",
								"LoanAmt_PerBizLoan": 1000.00,
								"LoanBal_PerBizLoan": 1000.00,
								"Loan_PerBizLoan_Account_Status": 1,
								"LoanClassResu_PerBizLoan": "",
								"Loan_PerBizLoan_Curr_Odue_Num": 1,
								"Loan_PerBizLoan_Curr_Odue_Amt": 1000.00,
								"KeyNo": 35
							}],
							"IndiNLoanDetail_OthLoan": [{
								"Loan_OthLoan_BeginDate": "2021-06-21 16:47:00",
								"LoanType_OthLoan": "",
								"LoanAmt_OthLoan": 1000.00,
								"LoanBal_OthLoan": 1000.00,
								"Loan_OthLoan_Account_Status": 1,
								"LoanClassResu_OthLoan": "关注",
								"Loan_OthLoan_Curr_Odue_Num": 1,
								"Loan_OthLoan_Curr_Odue_Amt": 1000.00
							}],
							"IndiNLoanDetail_PerConsumLoan": [{
								"Loan_PerConsumLoan_BeginDate": "",
								"LoanType_PerConsumLoan": "",
								"LoanAmt_PerConsumLoan": 1000.00,
								"LoanBal_PerConsumLoan": 1000.00,
								"Loan_PerConsumLoan_Account_Status": 1,
								"LoanClassResu_PerConsumLoan": "关注",
								"Loan_PerConsumLoan_Curr_Odue_Num": 1,
								"Loan_PerConsumLoan_Curr_Odue_Amt": 1000.00
							}],
							"IndiNLICRDetail_Not_RMB": [{
								"CCRD_BeginDate_Not_RMB": "",
								"CCRD_CredAmt_Not_RMB": 100.00,
								"CCRD_UsedAmt_Not_RMB": 100.00,
								"CCRD_Curr_Odue_Num_Not_RMB": 1,
								"CCRD_Curr_Odue_Amt_Not_RMB": 100.00,
								"CCRD_Account_Status_Not_RMB": 1,
								"KeyNo": 2
							}],
							"IndiNICRDetail_RMB": [{
								"CCRD_BeginDate_RMB": "",
								"CCRD_CredAmt_RMB": 1000.00,
								"CCRD_UsedAmt_RMB": 1000.00,
								"CCRD_Curr_Odue_Num_RMB": 1,
								"CCRD_Curr_Odue_Amt_RMB": 1000.00,
								"CCRD_Account_Status_RMB": 1,
								"KeyNo": 3
							}],
							"IndiNSpecTrade_Pay_BeforeBand": [{
								"SpecTrade_Type_Pay_Beforehand": "",
								"SpecTrad_Amt_Pay_Beforehand": 1000.00,
								"KeyNo": 2
							}],
							"IndiNSpecTrade_Extension": [{
								"SpecTrade_Type_Extension": "",
								"SpecTrad_Amt_Extension": 1000.00,
								"KeyNo": 5
							}],
							"IndiNSpecTrade_Other": [{
								"SpecTrade_Type_Other": "",
								"SpecTrad_Amt_Other": 1000.00,
								"KeyNo": 3
							}],
							"IndiNInhabitancy": [{
								"Inhabitancy_Status": "",
								"Inhabitancy_UpdateDate": "",
								"KeyNo": 3
							}],
							"IndiNOccupation_Decreasing_Order": [{
								"Occupation_Position": "",
								"Occupation_JobTitle": "",
								"Occupation_UpdateDate": "2021-06-22 10:10:10",
								"KeyNo": 3
							}],
							"IndiNGuarODetail": [{
								"Guaranteed_Loan_BeginDate": "",
								"GuarAmt": 1000.00,
								"Guaranteed_Loan_CapiBal": 1000.00,
								"Guaranteed_Loan_ClassResu": "",
								"KeyNo": 3
							}],
							"IndiNHousRes": [{
								"HouRes_BeginDate": "",
								"HouRes_EndMonth": 3,
								"HouRes_Status": 1,
								"HouRes_Monthly_PayAmt": 1000.00,
								"HouRes_Indi_PayPer": 1000.00,
								"HouRes_Comp_PayPer": 100.00,
								"KeyNo": 3
							}],
							"IndiNEndowInsuPay": [{
								"Pension_Pay_BeginDate": "",
								"Pension_Pay_Status": "3",
								"Pension_Pay_IndiPayRate": "",
								"Pension_CurrPayAmt": 1000.00
							}],
							"IndiNEndowInsuProv": [{
								"CurrProvAmt": 1000.00,
								"RetireSign": ""
							}],
							"IndiNAssDisp": [{
								"AssetDisp_DebtAmt": 1000.00,
								"AssetDisp_Balance": 1000.00,
								"KeyNo": 3
							}],
							"IndiNGuarRepay_Decreasing_Order": [{
								"GuarRepay_TotalAmt": 1000.00,
								"After_GuarRepay_Balance": 500.00,
								"KeyNo": 3
							}],
							"IndiNTelePay_Decreasing_Order": [{
								"TelePay_CurrOdueAmt": 1000.00,
								"TelePay_CurrOdueMons": 3,
								"TelePay_Status": "1",
								"KeyNo": 4
							}],
							"IndiNOwnTax": [{
								"OwnTax_Amt": 1000.00,
								"KeyNo": 3
							}],
							"IndiNEnforce_Decreasing_Order": [{
								"Enforced_Case_Status": 3,
								"Case_Applied_Amt": 1000.00,
								"Case_Enforced_Amt": 1000.00,
								"KeyNo": 3
							}],
							"IndiNCertificate": [{
								"CertLevel": A,
								"Cert_BeginDate": "2021-06-01",
								"Cert_EndDate": "2021-06-03",
								"Cert_RevokeDate": "2021-06-03",
								"KeyNo": 3
							}]
						}
					},
					"Bairong":{
					   "BR_Data": [
						  {
							"BRRuleId": "all_reminder",
							"BRRuleDesc": "0"
						  },
						  {
							"BRRuleId": "income_flag",
							"BRRuleDesc": "NO"
						  },
						  {
							"BRRuleId": "ir_reminder",
							"BRRuleDesc": "0"
						  },
						  {
							"BRRuleId": "sl_reminder",
							"BRRuleDesc": "0"
						  },
						  {
							"BRRuleId": "als_reminder",
							"BRRuleDesc": "0"
						  },
						  {
							"BRRuleId": "bv_reminder",
							"BRRuleDesc": "0"
						  }
						],
						"pcp_income": "[0,5]"
					}
				},
				{
					"DataDate": "2021-06-21",
					"IDCardNO": "310101199603076852",
					"CustName": "一一",
					"CustRole": "guarantor",
					"SexSign": "M",
					"IsNew": "false",
					"PreDeclineFlag": "false",
					"VerifiedIncome": 20000,
					"Address": [{
						"AddrType": "00001",
						"Living_City": "毫州市",
						"Living_StateCode": "安徽省",
						"Living_TimeInYear": 12,
						"PropertyType": "00004"
					}],
					"Employment": [{
						"EmplType": "00001",
						"EmprCity": "毫州市",
						"EmprProv": "安徽省"
					}],
					"PBOC": {
						"ReportNO": "",
						"PBOCStatus": "X0_record"
					},
					"Bairong":{
					   "BR_Data": [
						  {
							"BRRuleId": "all_reminder",
							"BRRuleDesc": "0"
						  },
						  {
							"BRRuleId": "income_flag",
							"BRRuleDesc": "NO"
						  },
						  {
							"BRRuleId": "ir_reminder",
							"BRRuleDesc": "0"
						  },
						  {
							"BRRuleId": "sl_reminder",
							"BRRuleDesc": "0"
						  },
						  {
							"BRRuleId": "als_reminder",
							"BRRuleDesc": "0"
						  },
						  {
							"BRRuleId": "bv_reminder",
							"BRRuleDesc": "0"
						  }
						],
						"pcp_income": "[0,5]"
					}
				},
				{
				"DataDate": "2021-06-21",
				"IDCardNO": "110101199507108239",
				"CustName": "新四十一",
				"CustRole": "guarantor",
				"SexSign": "M",
				"IsNew": "false",
				"PreDeclineFlag": "true",
				"VerifiedIncome": 6000,
				"Address": [{
					"AddrType": "00001",
					"Living_City": "淮南市",
					"Living_StateCode": "安徽省",
					"Living_TimeInYear": 1,
					"PropertyType": "00004"
				}],
					"Employment": [{
						"EmplType": "00004",
						"EmprCity": "淮南市",
						"EmprProv": "安徽省"
					}],
					"PBOC": {
						"ReportNO": "2021060913504028167023",
						"PBOCStatus": "X1_record",
						"PBOC_Summary_Data": {
							"BirthDay": "1973-03-07",
							"MarrSign": "",
							"EducSign": "",
							"DegreeSign": "",
							"LoanCorpAgenNum": 0,
							"LoanAgenNum": 0,
							"LoanNum": 0,
							"LoanContAmt": 0,
							"LoanBal": 0,
							"AverPayment": 0,
							"IssuCorpAccNum": 0,
							"IssuAgenNum": 0,
							"AccountNum": 0,
							"CreditAmt": 0.00,
							"MaxCredAmt": 0.00,
							"MinCredAmt": 0.00,
							"UsedAmt": 0.00,
							"AverUsedAmt": 0.00,
							"QCRIssuCorpAccNum": 0,
							"QCRIssuAgenNum": 0,
							"QCRAccountNum": 0,
							"QCRCreditAmt": 0.00,
							"QCRMaxCreditAmt": 0.00,
							"QCRMinCredAmt": 0.00,
							"QCROdrawBal": 0.00,
							"QCRAverOdrawBal": 0.00,
							"BadLoanNum": 0,
							"BadLoanBal": 0.00,
							"AssDispNum": 0,
							"AssDispBal": 0.00,
							"GuarPayNum": 0,
							"GuarPayBal": 0.00,
							"LoanOdueNum": 0,
							"LoanOdueMons": 0,
							"LoanMaxOdueAmt": 0.00,
							"LoanMaxOdueMons": 0,
							"ICROdueNum": 0,
							"ICROdueMons": 0,
							"ICRMaxOdueAmt": 0,
							"ICRMaxOdueMons": 0,
							"QCROdrawNum": 0,
							"QCROdrawMons": 0,
							"QCRMaxOdrawBal": 0.00,
							"QCRMaxOdrawMons": 0,
							"HouLoanNum": 0,
							"OthLoanNum": 0,
							"ICRAccNum": 0,
							"QCRAccNum": 0,
							"SelfDeclNum": 0,
							"ObjLabelNum": 0,
							"PersonHouLoanNum": 0,
							"BusinessHouLoanNum": 0,
							"AccountSum": 0,
							"BusTypeNum": 0,
							"PostLoanQueryNum": 0,
							"MNCQueryNum": 0,
							"CLQueryOrgs": 0,
							"CCRQueryOrgs": 0,
							"CCRQueryNum": 0,
							"CLQueryNum": 0,
							"PQueryNum": 1,
							"GCQQueryNum": 0,
							"PBOC_Score": -1
						}					
					},
					"Bairong":{
					   "BR_Data": [
						  {
							"BRRuleId": "all_reminder",
							"BRRuleDesc": "0"
						  },
						  {
							"BRRuleId": "income_flag",
							"BRRuleDesc": "NO"
						  },
						  {
							"BRRuleId": "ir_reminder",
							"BRRuleDesc": "0"
						  },
						  {
							"BRRuleId": "sl_reminder",
							"BRRuleDesc": "0"
						  },
						  {
							"BRRuleId": "als_reminder",
							"BRRuleDesc": "0"
						  },
						  {
							"BRRuleId": "bv_reminder",
							"BRRuleDesc": "0"
						  }
						],
						"pcp_income": "[0,5]"
					}
				}]
			}
~~~























