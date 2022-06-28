package com.fico.cbs.utils;

import com.fico.cbs.common.database.DataAccess;
import com.fico.cbs.common.database.storage.DataMap;
import com.fico.cbs.common.database.storage.ListResult;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作数据库获取用户信息
 */
public class DB_PBOCUtil {
	private Logger _logger = Logger.getLogger(DB_PBOCUtil.class);
	
	private DataAccess _access;
	
	public DB_PBOCUtil(DataAccess access) {
		this._access = access;
	}
	
//	public List<Map<String, String>> queryPBOCLoanDetail(String reportNo) {
//		
//	}
	
	/**
	 * 查询征信报告的个人基本信息
	 * 
	 * @param reportNo 征信报告编号
	 * @return 如果征信报告个人基本信息,则返回；否则返回null
	 * 
	 */
//	public List<Map<String, String>> queryPBOCIdentity(String reportNo) {
//		String tableName = "IndiNIdentity";
//		String columnList = "";
//		
//		return null;
//	}
	
	/**
	 * 查询征信报告的对外担保信息汇总
	 * 
	 * @param reportNo 征信报告编号
	 * @return 如果征信报告具有对外担保,则返回; 否则返回null
	 */
//	public List<Map<String, String>> queryPBOCGuarOSum(String reportNo) {
//		String tableName = "IndiNGuarOSum";
//		String columnList = "";
//		
//		return null;
//	}
	
//	public List<Map<String, String>> queryPBOCHousRes(String reportNo) {
//		String tableName = "IndiNHousRes";
//		String columnList = "";
//		
//		return null;
//	}
	
	/**
	 * 查询征信报告的居住信息
	 * 
	 * @param reportNo 征信报告编号
	 * @return 如果征信报告具有居住信息,则返回；否则返回null
	 * 
	 */
	public List<Map<String, String>> queryPBOCInhabitancy(String reportNo) {
		if (this._access == null) return null;
		
		if (StringUtils.isBlank(reportNo)) return null;

		StringBuffer querySql = new StringBuffer();
		// 构建查询语句
		querySql.append("select KeyNo,INHABADDR,status,DataDate from ")
			.append("IndiNInhabitancy")
			.append(" where ReportNo= '").append(reportNo).append("' ")
			.append(" order by KeyNo  asc");
		try {
			ListResult listResult=this._access.queryForList(querySql.toString());
			if (listResult != null && listResult.rowCount()>0) {
				List<Map<String, String>> inhabResult = new ArrayList<Map<String, String>>();
				String keyNo = "";
				String inhabAddr = "";
				String statSign = "";
				String dataDate = "";
				for(int i=0;i<listResult.rowCount();i++){
					DataMap singleResult= listResult.getRow(i+1);
					inhabAddr = (String)singleResult.get("inhabaddr");
					statSign = (String)singleResult.get("status");
					dataDate = (String)singleResult.get("datadate");
					keyNo = (String)singleResult.get("keyno");  //Jerry Lau 2019-03-04
					Map<String, String> itemData = new HashMap<String, String>();
					if (StringUtils.isNotBlank(inhabAddr)) {
						itemData.put("InhabAddr", inhabAddr);
					}
					if (StringUtils.isNotBlank(statSign)) {
						itemData.put("Status", statSign);
					}
					if (StringUtils.isNotBlank(dataDate)) {
						itemData.put("DataDate", dataDate.replaceAll("-","."));  // chenmq 2020-04-02
					}
					//Jerry Lau 2019-03-04
					if (StringUtils.isNotBlank(keyNo)) {
						itemData.put("KeyNo", keyNo);
					}
					if (itemData.size() > 0) {
						inhabResult.add(itemData);
					}
				}
				
				if (inhabResult.size() > 0) {
					return inhabResult;
				}
			}
		} catch (SQLException ex) {		
			_logger.error(ex.getMessage(), ex);
		}
		
		return null;
	}
	
	/**
	 * 查询征信报告的职业信息
	 * 
	 * @param reportNo 征信报告编号
	 * @return 如果征信报告具有职业信息,则返回; 否则返回null
	 * 
	 */
	public List<Map<String, String>> queryPBOCOccuption(String reportNo) {
		if (this._access == null) return null;
		
		if (StringUtils.isBlank(reportNo)) return null;

		StringBuffer querySql = new StringBuffer();
		querySql.append("select KeyNo,WorkUnit,CorpAddr,DataDate from ")
			.append("IndiNOccupation")
			.append(" where ReportNo= '").append(reportNo).append("' ")
			.append(" order by KeyNo  asc");
		try {
			ListResult listResult=this._access.queryForList(querySql.toString());
			if (listResult != null && listResult.rowCount()>0) {
				List<Map<String, String>> occupResult = new ArrayList<Map<String, String>>();
				String keyNo = "";
				String workCorp = "";
				String corpAddr = "";
				String dataDate = "";
				for(int i=0;i<listResult.rowCount();i++){
					DataMap singleResult= listResult.getRow(i+1);
					workCorp = (String)singleResult.get("workunit");
					corpAddr = (String)singleResult.get("corpaddr");
					dataDate = (String)singleResult.get("datadate");
					keyNo = (String)singleResult.get("keyno");  //Jerry Lau 2019-03-04
					Map<String, String> itemData = new HashMap<String, String>();
					if (StringUtils.isNotBlank(workCorp)) {
						itemData.put("WorkUnit", workCorp);
					}
					if (StringUtils.isNotBlank(corpAddr)) {
						itemData.put("CorpAddr", corpAddr);
					}
					if (StringUtils.isNotBlank(dataDate)) {
						itemData.put("DataDate", dataDate.replaceAll("-", ".")); // chenmq 2020-04-02
					}
					//Jerry Lau 2019-03-04
					if (StringUtils.isNotBlank(keyNo)) {
						itemData.put("KeyNo", keyNo);
					}
					if (itemData.size() > 0) {
						occupResult.add(itemData);
					}
				}
				
				if (occupResult.size() > 0) {
					return occupResult;
				}
			}
		} catch (SQLException ex) {		
			_logger.error(ex.getMessage(), ex);
		}
		
		return null;
	}

	/**
	 * 查询征信报告的本人声明信息
	 * 
	 * @param reportNo 征信报告编号
	 * @return 如果征信报告具有本人声明信息,则返回; 否则返回null
	 * 
	 */
	public List<Map<String, String>> queryPBOCDeclare(String reportNo) {
		if (this._access == null) return null;
		
		if (StringUtils.isBlank(reportNo)) return null;

		StringBuffer querySql = new StringBuffer();
		querySql.append("select Declaration,AddDate from ")
			.append("IndiNSelfDecl")
			.append(" where ReportNo= '").append(reportNo).append("' ")
			.append(" order by KeyNo  asc");
		try {
			ListResult listResult=this._access.queryForList(querySql.toString());
			if (listResult != null && listResult.rowCount()>0) {
				List<Map<String, String>> declareResult = new ArrayList<Map<String, String>>();

				String declare = "";
				String addDate = "";
				for(int i=0;i<listResult.rowCount();i++){
					DataMap singleResult= listResult.getRow(i+1);
					declare = (String)singleResult.get("declaration");
					addDate = (String)singleResult.get("adddate");

					Map<String, String> itemData = new HashMap<String, String>();
					if (StringUtils.isNotBlank(declare)) {
						itemData.put("Declaration", declare);
					}
					if (StringUtils.isNotBlank(addDate)) {
						itemData.put("AddDate", addDate);
					}
					if (itemData.size() > 0) {
						declareResult.add(itemData);
					}
				}
				
				if (declareResult.size() > 0) {
					return declareResult;
				}
			}
		} catch (SQLException ex) {		
			_logger.error(ex.getMessage(), ex);
		}
		
		return null;
	}
	
	/**
	 * 查询征信报告的异议标注信息
	 * 
	 * @param reportNo 征信报告编号
	 * @return 如果征信报告具有异议标注信息,则返回; 否则返回null
	 */
	public List<Map<String, String>> queryPBOCObjectionlabel(String reportNo) {
		if (this._access == null) return null;
		
		if (StringUtils.isBlank(reportNo)) return null;

		StringBuffer querySql = new StringBuffer();
		querySql.append("select ObjLabel,AddDate from ")
			.append("IndiNObjLabel")
			.append(" where ReportNo= '").append(reportNo).append("' ")
			.append(" order by KeyNo  asc");
		try {
			ListResult listResult=this._access.queryForList(querySql.toString());
			if (listResult != null && listResult.rowCount()>0) {
				List<Map<String, String>> labelResult = new ArrayList<Map<String, String>>();

				String objLabel = "";
				String addDate = "";
				for(int i=0;i<listResult.rowCount();i++){
					DataMap singleResult= listResult.getRow(i+1);
					objLabel = (String)singleResult.get("objlabel");
					addDate = (String)singleResult.get("adddate");

					Map<String, String> itemData = new HashMap<String, String>();
					if (StringUtils.isNotBlank(objLabel)) {
						itemData.put("ObjLabel", objLabel);
					}
					if (StringUtils.isNotBlank(addDate)) {
						itemData.put("AddDate", addDate);
					}
					if (itemData.size() > 0) {
						labelResult.add(itemData);
					}
				}
				
				if (labelResult.size() > 0) {
					return labelResult;
				}
			}
		} catch (SQLException ex) {		
			_logger.error(ex.getMessage(), ex);
		}
		
		return null;
	}

	
	
	
}
