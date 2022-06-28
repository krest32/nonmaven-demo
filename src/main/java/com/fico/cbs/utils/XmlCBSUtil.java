package com.fico.cbs.utils;

import com.fico.cbs.common.database.DataAccess;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 抽取BOM数据中的CBS输入参数
 * 
 * @author logan.chen
 * @date 2018-06-19s
 */
public class XmlCBSUtil {
 
	private DB_PBOCUtil _pbocDbAccess; // 数据库访问对象
	
	private String _debugMode = "false";
	
	public XmlCBSUtil(DataAccess access, String debugMode) {
		this._pbocDbAccess = new DB_PBOCUtil(access);
		
		this._debugMode = debugMode;
	}
	
	/**
	 * 按CBS输入映射配置参数抽取BOM中的数据，形成对象模型
	 * 
	 * @param app_num 案子编号
	 * @param callType 调用类型
	 * @param callRound 调用次数
	 * @param mapParams 输入映射参数
	 * @param bomData BOM数据
	 * @return 对象模型
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")

	public Map<String, Object> extractCBSData(String app_num, String callType, String callRound,
                                              Document mapParams, Document bomData) throws Exception {

		if (null == mapParams) {
			throw new Exception("Blaze2CBS.xml 错误：为空，或者不存在");
		}
		if (null == bomData) {
			throw new Exception("用户请求的Xml信息不存在，或者为空");
		}
		
		// 准备输入参数通用数据
		Map<String, String> generalInputData = loadGeneralInputData(app_num, callType, callRound, bomData);
		
		Map<String, Object> resultData = new LinkedHashMap<String, Object>();

		// 节点属性变量
		String nameKey = "";
		String valueData = "";
		String nodeType = "";
		String nodePath = "";
		
		// 处理输入映射参数
		Element rootMapNode = mapParams.getRootElement();
		if (rootMapNode != null && hasChild(rootMapNode)) {
			Element selectBomNode = bomData.getRootElement();
			
			// ChenMQ 2020-07-16 modify 删除配置参数文件中根节点的nodePath属性处理内容

			List<Element> elements = rootMapNode.elements();
			for(Element element : elements) {
				// 获得节点的名称及属性
				nameKey = element.getName();
				valueData = element.getTextTrim();
				nodeType = element.attributeValue("nodeType");
				nodePath = element.attributeValue("nodePath");
				
				System.out.println(String.format("extractCBSData method: node name %s, node text %s", nameKey, valueData));
				
				if (StringUtils.isNotBlank(nodeType)) {
					if ("child".equals(nodeType) && StringUtils.isNotBlank(nodePath)) {
						Element bomNode = (Element)bomData.selectSingleNode(nodePath);
						if (null != bomNode) {
							Map<String, Object> nodeData = loadMapChild(element, bomNode, generalInputData);
							if (nodeData != null && nodeData.size() > 0) {
								resultData.put(nameKey, nodeData);
							}
						}
					} else if ("object".equals(nodeType) && StringUtils.isNotBlank(nodePath)) {
						Element bomNode = (Element)bomData.selectSingleNode(nodePath);
						if (bomNode != null) {
							Map<String, Object> childDataResult = loadMapNodes(element, bomNode, generalInputData);
							if (childDataResult != null && childDataResult.size() > 0) {
								resultData.put(nameKey, childDataResult);
							}
						}
					} else if ("list".equals(nodeType) && StringUtils.isNotBlank(nodePath)) {
						List<?> bomNodeList = bomData.selectNodes(nodePath);
						if (bomNodeList != null && bomNodeList.size() > 0) {
							List<Map<String, Object>> paramList = new ArrayList<Map<String, Object>>();
							for(Iterator<?> bomNodeIt = bomNodeList.iterator(); bomNodeIt.hasNext();) {
								Element bomNode = (Element)bomNodeIt.next();
								Map<String, Object> childDataResult = loadMapNodes(element, bomNode, generalInputData);
								if (childDataResult != null) {
									paramList.add(childDataResult);
								}
							}
							if (paramList.size() > 0) {
								resultData.put(nameKey, paramList);
							}
						}
					} else if ("general".equals(nodeType)) {
						String itemText = "";
						if (resultData.get("first_thi_nme") != null) {
							itemText = getItemText(String.format(valueData, resultData.get("first_thi_nme")), generalInputData);
						} else {
							itemText = getItemText(valueData, generalInputData);
						}
						
						if (StringUtils.isNotBlank(itemText)) {
							resultData.put(nameKey, itemText);
						}
					} else if ("db".equals(nodeType)) {
						String funcName = element.attributeValue("funcName");
						if ("queryPBOCInhabitancy".equals(funcName)) {
							String reportNo = (String)resultData.get("ReportNo");
							List<Map<String, String>> inhabList = _pbocDbAccess.queryPBOCInhabitancy(reportNo);
							if (inhabList != null && inhabList.size() > 0) {
								resultData.put(nameKey, inhabList);
							}
						} else if ("queryPBOCOccuption".equals(funcName)) {
							String reportNo = (String)resultData.get("ReportNo");
							List<Map<String, String>> occupList = _pbocDbAccess.queryPBOCOccuption(reportNo);
							if (occupList != null && occupList.size() > 0) {
								resultData.put(nameKey, occupList);
							}
						} else if ("queryPBOCDeclare".equals(funcName)) {
							String reportNo = (String)resultData.get("ReportNo");
							List<Map<String, String>> declareList = _pbocDbAccess.queryPBOCDeclare(reportNo);
							if (declareList != null && declareList.size() > 0) {
								resultData.put(nameKey, declareList);
							}
						} else if ("queryPBOCObjectionlabel".equals(funcName)) {
							String reportNo = (String)resultData.get("ReportNo");
							List<Map<String, String>> labelList = _pbocDbAccess.queryPBOCObjectionlabel(reportNo);
							if (labelList != null && labelList.size() > 0) {
								resultData.put(nameKey, labelList);
							}
						}
					}
				} else {
					// ChenMQ 2020-07-16 modify 配置参数文件中的根节点下无BOM数据的子节点丢弃
					if (StringUtils.isNotBlank(valueData)) {
						Element childNode = selectBomNode.element(valueData);
						String bomNodeText = getNodeText(childNode);

						if (StringUtils.isNotBlank(bomNodeText)) {
							resultData.put(nameKey, bomNodeText);
						} 
					}
				}
			}
		}
		
		return resultData;
	}
	
	/**
	 * 加载输入映射参数节点对应的BOM数据,输入参数节点的子节点可以叶子节点、child类型节点、object类型节点、list类型节点
	 * 
	 * @param paramNode 参数节点
	 * @param bomNode BOM数据节点
	 * @param generalData 通用数据
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> loadMapNodes(Element paramNode, Element bomNode, Map<String, String> generalData) throws Exception {
		if (null == paramNode) return null;
		if (null == bomNode) return null;
		
		Map<String, Object> resultData = new LinkedHashMap<String, Object>();
		
		// 子节点属性定义
		String childNameKey = "";
		String childValueData = "";
		String childNodeType = "";
		String childNodePath = "";
		String childNodeMethod = "";
		String ifNodeName = paramNode.attributeValue("ifNodeName");
		String employeeType = "";
		
		// ChenMQ 2020-07-16 modify 删除代码，与业务处理无关

		List<?> childParamNodes = paramNode.elements();
		for(Iterator<?> childNodeIt = childParamNodes.iterator(); childNodeIt.hasNext();) {
			Element childParamNode = (Element)childNodeIt.next();
			
			// 获得子节点的属性
			childNameKey = childParamNode.getName();
			childValueData = childParamNode.getText();
			childNodeType = childParamNode.attributeValue("nodeType");
			childNodePath = childParamNode.attributeValue("nodePath");
			childNodeMethod = childParamNode.attributeValue("method");
			
			if (StringUtils.isNotBlank(ifNodeName) && "years".equals(childNameKey)) { //
				Element bomChildNode = bomNode.element("employer_type");
				employeeType = getNodeText(bomChildNode);
			}
			if (StringUtils.isNotBlank(childNodeType)) {
				if ("child".equals(childNodeType) && StringUtils.isNotBlank(childNodePath)) {
					Element selectBomNode = (Element)bomNode.element(childNodePath);
					if (selectBomNode != null) {
						Map<String, Object> nodeData = loadMapChild(childParamNode, selectBomNode, generalData);
						if (nodeData != null && nodeData.size() > 0) {
							resultData.put(childNameKey, nodeData);
						}
					}
				} else if ("object".equals(childNodeType) && StringUtils.isNotBlank(childNodePath)) {
					Element selectBomNode = (Element)bomNode.element(childNodePath);
					if (selectBomNode != null) {
						Map<String, Object> childDataResult = loadMapNodes(childParamNode, selectBomNode, generalData);
						if (childDataResult != null && childDataResult.size() > 0) {
							resultData.put(childNameKey, childDataResult);
						}
					}
				} else if ("parent".equals(childNodeType) && StringUtils.isNotBlank(childNodePath)) {
					Map<String, Object> childDataResult = loadMapNodes(childParamNode, bomNode, generalData);
					if (childDataResult != null && childDataResult.size() > 0) {
						resultData.put(childNameKey, childDataResult);
					} 
				} else if ("list".equals(childNodeType) && StringUtils.isNotBlank(childNodePath)) {
					List<?> bomNodeList = bomNode.elements(childNodePath);
					if (bomNodeList != null && bomNodeList.size() > 0) {
						List<Map<String, Object>> paramList = new ArrayList<Map<String, Object>>();
						for(Iterator<?> bomNodeIt = bomNodeList.iterator(); bomNodeIt.hasNext();) {
							Element selectBomNode = (Element)bomNodeIt.next();
							Map<String, Object> childDataResult = loadMapNodes(childParamNode, selectBomNode, generalData);
							if (childDataResult != null) {
								paramList.add(childDataResult);
							}
						}
						if (paramList.size() > 0) {
							resultData.put(childNameKey, paramList);
						} 
					}
				} else if ("virtual".equals(childNodeType)) {
					Element selectBomNode = (Element)bomNode.element(childNodePath);
					if (selectBomNode != null) {
						Map<String, Object> childDataResult = loadMapNodes(childParamNode, selectBomNode, generalData);
						if (childDataResult != null && childDataResult.size() > 0) {
							resultData.putAll(childDataResult);
						} 
					}
				} else if ("db".equals(childNodeType)) {
					String funcName = childParamNode.attributeValue("funcName");
					if ("queryPBOCInhabitancy".equals(funcName)) {
						String reportNo = (String)resultData.get("ReportNo");
						List<Map<String, String>> inhabList = _pbocDbAccess.queryPBOCInhabitancy(reportNo);
						if (inhabList != null && inhabList.size() > 0) {
							resultData.put(childNameKey, inhabList);
						}
					} else if ("queryPBOCOccuption".equals(funcName)) {
						String reportNo = (String)resultData.get("ReportNo");
						List<Map<String, String>> occupList = _pbocDbAccess.queryPBOCOccuption(reportNo);
						if (occupList != null && occupList.size() > 0) {
							resultData.put(childNameKey, occupList);
						}
					} else if ("queryPBOCDeclare".equals(funcName)) {
						String reportNo = (String)resultData.get("ReportNo");
						List<Map<String, String>> declareList = _pbocDbAccess.queryPBOCDeclare(reportNo);
						if (declareList != null && declareList.size() > 0) {
							resultData.put(childNameKey, declareList);
						}
					} else if ("queryPBOCObjectionlabel".equals(funcName)) {
						String reportNo = (String)resultData.get("ReportNo");
						List<Map<String, String>> labelList = _pbocDbAccess.queryPBOCObjectionlabel(reportNo);
						if (labelList != null && labelList.size() > 0) {
							resultData.put(childNameKey, labelList);
						}
					}
				} else if ("general".equals(childNodeType)) {
					String itemText = "";
					if ( resultData.get("first_thi_nme") != null) {
						itemText = getItemText(String.format(childValueData, resultData.get("first_thi_nme")), generalData);
					} else {
						itemText = getItemText(childValueData, generalData);
					}
					if (StringUtils.isNotBlank(itemText)) {
						resultData.put(childNameKey, itemText);
					}
				} 
			} else {
				if (StringUtils.isBlank(employeeType)) {
					Element bomChildNode = bomNode.element(childValueData);
					String bomNodeText = getNodeText(bomChildNode);

					if ("adjustDateFormat".equalsIgnoreCase(childNodeMethod)) {
						bomNodeText = adjustDateFormat(bomNodeText);
					} else if ("adjustLoanType".equalsIgnoreCase(childNodeMethod)) {
						bomNodeText = adjustLoanType(bomNodeText);
					} else if ("adjustTradeType".equalsIgnoreCase(childNodeMethod)) {
						bomNodeText = adjustTradeType(bomNodeText);
					} else if ("handleNumDefault0".equalsIgnoreCase(childNodeMethod)) {
						bomNodeText = handleNumDefault0(bomNodeText);
					}
					
					if (StringUtils.isNotBlank(bomNodeText))
						resultData.put(childNameKey, bomNodeText);
				} else {
					if ("00007".equals(employeeType) || "00006".equals(employeeType)) {
						// submit_date - established_since
//						for(Iterator<?> childNodeYY = childNodes.iterator(); childNodeYY.hasNext();) {
//							Element childItemYY = (Element)childNodeYY.next();
//							if ("established_since".equals(childItemYY.getName())){
//								
//							}
//						}
						Element bomChildNode = bomNode.element(ifNodeName);
						String bomNodeText = getNodeText(bomChildNode);
						if (StringUtils.isNotBlank(bomNodeText)) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							
							//String submit_date = generalData.get("submit_date");  //Jerry Lau 2019-06-15
							String submit_date = getCurrentDate();  //Jerry Lau 2019-06-15

							Date submitDate = sdf.parse(submit_date);
							Date sinceDate = sdf.parse(bomNodeText);							
							
							//System.out.println(XmlCBSUtil.calculateYears(submitDate,sinceDate));
							
							//resultData.put(childNameKey, XmlCBSUtil.calInterval(sinceDate,submitDate,"y"));  //Jerry Lau 2019-06-15
							resultData.put(childNameKey, XmlCBSUtil.calculateYears(submitDate,sinceDate));   //Jerry Lau 2019-06-15
						}
						
						employeeType = "";
					} else {
						
						Element bomChildNode = bomNode.element(childValueData);
						String bomNodeText = getNodeText(bomChildNode);

						if ("adjustDateFormat".equalsIgnoreCase(childNodeMethod)) {
							bomNodeText = adjustDateFormat(bomNodeText);
						} else if ("adjustLoanType".equalsIgnoreCase(childNodeMethod)) {
							bomNodeText = adjustLoanType(bomNodeText);
						} else if ("adjustTradeType".equalsIgnoreCase(childNodeMethod)) {
							bomNodeText = adjustTradeType(bomNodeText);
						} else if ("handleNumDefault0".equalsIgnoreCase(childNodeMethod)) {
							bomNodeText = handleNumDefault0(bomNodeText);
						}
						
						if (StringUtils.isNotBlank(bomNodeText))
							resultData.put(childNameKey, bomNodeText);
					}
				}
			}
		}

		return resultData;
	}
	
	/**
	 * 加载输入映射参数节点对应的BOM数据,输入参数节点的子节点皆为叶子节点
	 * 
	 * @param paramNode 参数节点
	 * @param bomNode BOM数据节点
	 * @return 如果
	 * 
	 * @throws Exception
	 */
	private Map<String, Object> loadMapChild(Element paramNode, Element bomNode, Map<String, String> generalData) throws Exception {
		if (paramNode == null) return null;
		if (bomNode == null) return null;
		
		Map<String, Object> resultData = new LinkedHashMap<String, Object>();
		
		//子节点皆为叶子节点,通过XPath方式获得数据
		List<?> paramChilds = paramNode.elements();
		if (paramChilds == null || paramChilds.size() == 0) return null;
		
		String nodeName = "";
		String nodeData = "";
		String nodeType = "";
		String nodeMethod = "";
		
		for(Iterator<?> childIt=paramChilds.iterator(); childIt.hasNext();) {
			Element paramChildNode = (Element)childIt.next();
			
			nodeName = paramChildNode.getName();
			nodeData = paramChildNode.getText();
			nodeType = paramChildNode.attributeValue("nodeType");
			nodeMethod = paramChildNode.attributeValue("method");
			
			if (StringUtils.isBlank(nodeType)) {
				String bomNodeText = getNodeText(bomNode.element(nodeData));

				if ("adjustDateFormat".equalsIgnoreCase(nodeMethod)) {
					bomNodeText = adjustDateFormat(bomNodeText);
				} else if ("adjustLoanType".equalsIgnoreCase(nodeMethod)) {
					bomNodeText = adjustLoanType(bomNodeText);
				} else if ("adjustTradeType".equalsIgnoreCase(nodeMethod)) {
					bomNodeText = adjustTradeType(bomNodeText);
				} else if ("handleNumDefault0".equalsIgnoreCase(nodeMethod)) {
					bomNodeText = handleNumDefault0(bomNodeText);
				}
				
				if (StringUtils.isNotBlank(bomNodeText))
					resultData.put(nodeName, bomNodeText);
			} else if ("general".equals(nodeType)) {
				String itemText = getItemText(nodeData, generalData);
				if (StringUtils.isNotBlank(itemText)) {
					resultData.put(nodeName, itemText);
				} 
			}
		}
		
		return resultData;
	}
	
	/**
	 * 获得节点的内容
	 * 
	 * @param node 节点
	 * @return 节点内容
	 */
	private String getNodeText(Element node) {
		// ChenMQ 2020-07-16 modify
		Optional<Element> nodeOpt = Optional.ofNullable(node);
		
		return nodeOpt.map(e -> e.getTextTrim()).orElse("");
	}

	/**
	 * 获得Map的数据项内容
	 * 
	 * @param itemKey 数据项键
	 * @param mapData Map对象
	 * @return 数据项内容
	 */
	private String getItemText(String itemKey, Map<String, String> mapData) {
		// ChenMQ 2020-07-16 modify
		Optional<String> itemTextOpt = Optional.ofNullable(mapData.get(itemKey));
		
		return itemTextOpt.map(t -> t).orElse("");
	}
	
	/**
	 * 检查指定元素是否有子节点
	 * 
	 * @param element 指定节点
	 * @return 如果有子节点,则返回true;否则返回false
	 */
	public boolean hasChild(Element element) {
		if (element == null) return false;
		Iterator<?> iterator = element.elementIterator(); 
		return iterator.hasNext();
	}
	
	/**
	 * 加载BOM的通用节点数据及外部传入参数到输入数据公用部分
	 * 
	 * @param app_num 案子编号
	 * @param calltype 调用类型
	 * @param callround 调用次数
	 * @param bomData BOM数据
	 * 
	 * @return 输入参数通用数据部分
	 */
	private Map<String, String> loadGeneralInputData(String app_num, String calltype, String callround, Document bomData) {
		Map<String, String> loadResult = new HashMap<String, String>();
		
		loadResult.put("application_number", app_num);
		loadResult.put("calltype", calltype);
		loadResult.put("callround", callround);
		
		Optional<Element> submitDateOpt = Optional.ofNullable((Element)bomData.selectSingleNode("/Application/submit_date"));
		submitDateOpt.ifPresent(submitDate -> {
			String submitDateVal = submitDate.getText();
			if (StringUtils.isNotBlank(submitDateVal)) {
				loadResult.put("submit_date", submitDateVal);
			}
		});

		Optional<Element> borrowerOpt = Optional.ofNullable((Element)bomData.selectSingleNode("/Application/CallResult/BORROWER_Score"));
		borrowerOpt.ifPresent(borrower -> {
			String borrowerVal = borrower.getText();
			if (StringUtils.isNotBlank(borrowerVal)) {
				loadResult.put("borrower_score", borrowerVal);
			}
		});
		
		
		Optional<Element> coborrowerOpt = Optional.ofNullable((Element)bomData.selectSingleNode("/Application/CallResult/CO_BORROWER_Score"));
		coborrowerOpt.ifPresent(coborrower -> {
			String coborrowerVal = coborrower.getText();
			if (StringUtils.isNotBlank(coborrowerVal)) {
				loadResult.put("co_borrower_score", coborrowerVal);
			}
		});
		
		Element guarantor1 = (Element)bomData.selectSingleNode("/Application/CallResult/reservedn2");
		Element guarantor1_name = (Element)bomData.selectSingleNode("/Application/CallResult/GUARANTOR1_Name");
		if (guarantor1 != null && guarantor1_name != null && !StringUtils.isBlank(guarantor1_name.getText())) {
			String guarantor1Val = guarantor1.getText();
			if (StringUtils.isNotBlank(guarantor1Val)) {
				loadResult.put(String.format("guarantor%s_score",guarantor1_name.getText()), guarantor1Val);
			} else {
				loadResult.put("guarantor1_score", "未加载数据.");
			}
		} else {
			loadResult.put("guarantor1_score", "数据项为空.");
		}
		
		Element guarantor2 = (Element)bomData.selectSingleNode("/Application/CallResult/reservedn3");
		Element guarantor2_name = (Element)bomData.selectSingleNode("/Application/CallResult/GUARANTOR2_Name");
		if (guarantor2 != null && guarantor2_name != null && !StringUtils.isBlank(guarantor2_name.getText())) {
			String guarantor2Val = guarantor2.getText();
			if (StringUtils.isNotBlank(guarantor2Val)) {
				loadResult.put(String.format("guarantor%s_score",guarantor2_name.getText()), guarantor2Val);
			} else {
				loadResult.put("guarantor2_score", "未加载数据.");
			}
		} else {
			loadResult.put("guarantor2_score", "数据项为空.");
		}

		Element guarantor3 = (Element)bomData.selectSingleNode("/Application/CallResult/reservedn4");
		Element guarantor3_name = (Element)bomData.selectSingleNode("/Application/CallResult/GUARANTOR3_Name");
		if (guarantor3 != null && guarantor3_name != null && !StringUtils.isBlank(guarantor3_name.getText())) {
			String guarantor3Val = guarantor3.getText();
			if (StringUtils.isNotBlank(guarantor3Val)) {
				loadResult.put(String.format("guarantor%s_score",guarantor3_name.getText()), guarantor3Val);
			} else {
				loadResult.put("guarantor3_score", "未加载数据.");
			}
		} else {
			loadResult.put("guarantor3_score", "数据项为空.");
		}
		
		return loadResult;
	}

	/**
	 * 获取Blaze返回的最终决策结果
	 * 
	 * @param bomData BOM数据
	 * @return 决策结果
	 * 
	 */
	public static String getBlazeDecision(Document bomData) {
		String decisionResult = "";
		Element finalDecisionNode = (Element)bomData.selectSingleNode("//CallResult/finaldecision");
		if (finalDecisionNode != null) {
			decisionResult = finalDecisionNode.getText();
		} 
		return decisionResult;
	}
	/**
	 * 获取BOM中的案子申请日期
	 * 
	 * @param bomData BOM数据
	 * @return 申请日期
	 * 
	 */
	public static String getApplicationDate(Document bomData) {
		String dateResult = "";
		Element applicationDateNode = (Element)bomData.selectSingleNode("//General/date");
		if (applicationDateNode != null) {
			dateResult = applicationDateNode.getText();
		}
		return dateResult;
	}
	
	public static String getCurrentDate(){  
	    String sCurrentDate="";  
	    Date dt = new Date();  
	    //最后的aa表示“上午”或“下午”    HH表示24小时制    如果换成hh表示12小时制  
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	    sCurrentDate=sdf.format(dt);  	   
	    return sCurrentDate;  
	}  
	
	private String adjustDateFormat(String pbocDate) {
		String tmpDateStr = pbocDate;
		if (tmpDateStr.trim().length() <= 4) return tmpDateStr;

		tmpDateStr = pbocDate.replaceAll("-",".");
		return tmpDateStr;
	}

	private String adjustLoanType(String pbocLoanType) {
		String tmpLoanType = pbocLoanType;

		if (tmpLoanType.trim().length() == 0) return pbocLoanType;

		if ("其他个人消费贷款".equalsIgnoreCase(pbocLoanType)) {
			tmpLoanType = "个人消费贷款";
		} 
		return tmpLoanType;
	}

	private String adjustTradeType(String pbocTradeType) {
		String tmpTradeType = pbocTradeType;

		if (tmpTradeType.trim().length() == 0) return tmpTradeType;

		if ("提前结清".equalsIgnoreCase(pbocTradeType)) {
			tmpTradeType = "提前还款(全部)";
		} else if ("提前还款".equalsIgnoreCase(pbocTradeType)) {
			tmpTradeType = "提前还款(部分)";
		} else if ("展期".equalsIgnoreCase(pbocTradeType)) {
			tmpTradeType = "展期(延期)";
		} else if ("担保人（第三方）代偿".equalsIgnoreCase(pbocTradeType)) {
			tmpTradeType = "担保人代还";
		} 
		return tmpTradeType;
	}
	
	private String handleNumDefault0(String num) {
		return null == num || num.trim().length() == 0 ? "0" : num;
	}
	
	/**
	 * 计算两个日期的相差年数
	 * @param startDate  当前日期
	 * @param endDate  以前日期
	 * @return
	 */
	private static int calculateYears(Date startDate, Date endDate)
	{
		int iYear = 0;
	
		Calendar  from  =  Calendar.getInstance();
	    from.setTime(startDate);
	    Calendar  to  =  Calendar.getInstance();
	    to.setTime(endDate);

	    int fromYear = from.get(Calendar.YEAR);
	    int fromMonth = from.get(Calendar.MONTH);
	    int fromDay = from.get(Calendar.DAY_OF_MONTH);

	    int toYear = to.get(Calendar.YEAR);
	    int toMonth = to.get(Calendar.MONTH);
	    int toDay = to.get(Calendar.DAY_OF_MONTH);
	    
	    
		//if(fromDay<> unknown and date2 <> unknown)then{
			if(fromMonth > toMonth){
				iYear = fromYear - toYear;
			}else if (fromMonth == toMonth){
				if(fromDay >= toDay){
					iYear = fromYear - toYear;
				}else{
					iYear = fromYear - toYear - 1;
				}
			}else{
				iYear = fromYear - toYear - 1;
			}
		//}
		return iYear;
	}

	/**
	 * 计算两个日期的时间间隔
	 * 
	 * @param sDate开始时间
	 * 
	 * @param eDate结束时间
	 * 
	 * @param type间隔类型("Y/y"--年  "M/m"--月  "D/d"--日)
	 * 
	 * @return interval时间间隔
	 * */
//	private static int calInterval(Date sDate, Date eDate, String type)
//	{
//		//时间间隔，初始为0
//		int interval = 0;
//		
//		/*比较两个日期的大小，如果开始日期更大，则交换两个日期*/
//		//标志两个日期是否交换过
//		boolean reversed = false;
//		if(compareDate(sDate, eDate) > 0)
//		{
//			Date dTest = sDate;
//			sDate = eDate;
//			eDate = dTest;
//			//修改交换标志
//			reversed = true;
//		}
//		
//		/*将两个日期赋给日历实例，并获取年、月、日相关字段值*/
//		Calendar sCalendar = Calendar.getInstance();
//		sCalendar.setTime(sDate);
//		int sYears = sCalendar.get(Calendar.YEAR);
//		int sMonths = sCalendar.get(Calendar.MONTH);
//		int sDays = sCalendar.get(Calendar.DAY_OF_YEAR);
//		
//		Calendar eCalendar = Calendar.getInstance();
//		eCalendar.setTime(eDate);
//		int eYears = eCalendar.get(Calendar.YEAR);
//		int eMonths = eCalendar.get(Calendar.MONTH);
//		int eDays = eCalendar.get(Calendar.DAY_OF_YEAR);
//		
//		//年
//		if(cTrim(type).equals("Y") || cTrim(type).equals("y"))
//		{
//			interval = eYears - sYears;
//			if(eMonths < sMonths)
//			{
//				--interval;
//			}
//		}
//		//月
//		else if(cTrim(type).equals("M") || cTrim(type).equals("m"))
//		{
//			interval = 12 * (eYears - sYears);
//			interval += (eMonths - sMonths);
//		}
//		//日
//		else if(cTrim(type).equals("D") || cTrim(type).equals("d"))
//		{
//			interval = 365 * (eYears - sYears);
//			interval += (eDays - sDays);
//			//除去闰年天数
//			while(sYears < eYears)
//			{
//				if(isLeapYear(sYears))
//				{
//					--interval;
//				}
//				++sYears;
//			}
//		}
//		//如果开始日期更大，则返回负值
//		if(reversed)
//		{
//			interval = -interval;
//		}
//		//返回计算结果
//		return interval;
//	}
	
	/**
	  * 判定某个年份是否是闰年
	  * 
	  * @param year待判定的年份
	  * 
	  * @return 判定结果
	  * */
//	 private static boolean isLeapYear(int year)
//	 {
//		 return (year%400 == 0 || (year%4 == 0 && year%100 != 0));
//	 }
	 
	/**
	* 
	* 字符串去除两头空格，如果为空，则返回""，如果不空，则返回该字符串去掉前后空格
	* 
	* @param tStr输入字符串
	* 
	* @return 如果为空，则返回""，如果不空，则返回该字符串去掉前后空格
	* 
	*/
	public static String cTrim(String tStr) 
	{
		String ttStr = "";
		if (tStr == null) 
		{} 
		else 
		{
			ttStr = tStr.trim();
		}
		return ttStr;
	}
	
	/**
	 * 比较两个Date类型的日期大小
	 * 
	 * @param sDate开始时间
	 * 
	 * @param eDate结束时间
	 * 
	 * @return result返回结果(0--相同  1--前者大  2--后者大)
	 * */
//	private static int compareDate(Date sDate, Date eDate)
//	{
//		int result = 0;
//		//将开始时间赋给日历实例
//		Calendar sC = Calendar.getInstance();
//		sC.setTime(sDate);
//		//将结束时间赋给日历实例
//		Calendar eC = Calendar.getInstance();
//		eC.setTime(eDate);
//		//比较
//		result = sC.compareTo(eC);
//		//返回结果
//		return result;
//	}
	
	/*public static void main(String[] args) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sSinceDate= "2016-06-16";
		String sSubmitDate = getCurrentDate();
		Date submitDate = sdf.parse(sSubmitDate);
		Date sinceDate = sdf.parse(sSinceDate);
		
		System.out.println(XmlCBSUtil.calculateYears(submitDate,sinceDate));
		
	}*/
}
