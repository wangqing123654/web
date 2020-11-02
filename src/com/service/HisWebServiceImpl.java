package com.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.jws.WebService;

import jdo.reg.SchDayTool;
import jdo.sys.Pat;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
@WebService
public class HisWebServiceImpl implements HisWebService {

	@Override
	public String hisWebFunction(String function, String xml) {
		String xml1 = "";
		System.out.println("function::::"+function);
		if (function.equals("SYS_DEPT_LOAD")) {
			// TODO Auto-generated method stub
//			xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>"
//					+ "<TRANSFERINFO><HEADINFO></HEADINFO><RESULT><ROW><DEPT_CODE>1012</DEPT_CODE>"
//					+ "<DEPT_NAME>内科</DEPT_NAME></ROW><ROW><DEPT_CODE>1013</DEPT_CODE><DEPT_NAME>外科</DEPT_NAME>"
//					+ "</ROW></RESULT><ERRCODE>1</ERRCODE><ERRMSG></ERRMSG></TRANSFERINFO>";
			xml1 = sysDeptLoad(xml);
		} else if (function.equals("SYS_PATINFO_INSERT")) {
			xml1 = sysPatinfoInsert(xml);
		} else if (function.equals("SYS_PATINFO_LOAD")) {
			xml1 = sysPatinfoLoad(xml);
		}else if(function.equals("REG_SCHD_QUERY")){//排班查询
			xml1 = regSchdQuery(xml);
		}else if(function.equals("REG_DRSCHD_VIPQUERY")){
			xml1 = regDrSchdVipQuery(xml);
		}else if(function.equals("REG_SCHD_INSERT")){
			xml1 = regSchdInsert(xml);
		}else if(function.equals("REG_SCHD_VIP_INSERT")){
			xml1 = regSchdVipInsert(xml);
		}else if (function.equals("SYS_DRINFO_LOAD")) {
			xml1 = sysDrinfoLoad(xml);
		} 
		return xml1;
	}
	private String regSchdVipInsert(String xml){
		// 
		List<String> list = new ArrayList<String>();				
		list.add("ADM_DATE");
		list.add("ADM_TYPE");
		list.add("SESSION_CODE");
		list.add("DEPT_CODE");
		list.add("DR_CODE");
		list.add("CLINICROOM_NO");
		list.add("MR_NO");
		list.add("SESSION_DATE");
		return regSchdComm(list, xml, true);
	}
	
	private String regSchdComm(List<String> list ,String xml,boolean vipFlg){
		List<Map<String, Object>> xmlList = XMLParamUtils.onGetXmlResultList(
				list, xml);
		String xmlResult = XMLParamUtils.onCheckError(xmlList, "解析失败");
		if (!xmlResult.equals("success")) {
			return xmlResult;
		}
		TConnection connection = TDBPoolManager.getInstance().getConnection();
		String sql = "SELECT REGION_CODE FROM SYS_REGION ";
		TParm sysRegion = new TParm(TJDODBTool.getInstance().select(sql)); 
		//普通挂号获得
		sql="SELECT QUE_NO,MAX_QUE,REG_CLINICAREA,CLINICTYPE_CODE FROM REG_SCHDAY  WHERE "
				+ "REGION_CODE = '"+sysRegion.getRow(0).getValue("REGION_CODE")
				+"'  AND ADM_TYPE = '"+xmlList.get(0).get("ADM_TYPE").toString()
				+"'  AND ADM_DATE = '"+xmlList.get(0).get("ADM_DATE").toString().replace("-", "")
				+"'  AND SESSION_CODE = '"+xmlList.get(0).get("SESSION_CODE").toString()
				+"' AND CLINICROOM_NO='"+xmlList.get(0).get("CLINICROOM_NO").toString()+"'";
		TParm regSchdParm = new TParm(TJDODBTool.getInstance().select(sql)); 
		if(regSchdParm.getErrCode()<0){
			  return XMLParamUtils.sendError("-1", "预约挂号失败");
		}
		if(regSchdParm.getCount()<=0){
			  return XMLParamUtils.sendError("-1", "未获得挂号信息");
		}
		if (regSchdParm.getInt("QUE_NO",0) >regSchdParm.getInt("MAX_QUE",0)) {
			 return XMLParamUtils.sendError("-1", "挂号已满,不可以操作预约");
		}
		int queNo =regSchdParm.getInt("QUE_NO",0);
		if (queNo == 0) {
			 return XMLParamUtils.sendError("-1", "未获得就诊号");
		}
		String startTime= "";
		//VIP挂号查询
		if (vipFlg) {
			String sessionDate =  xmlList.get(0).get("SESSION_DATE").toString();
			String starTime =  sessionDate.substring(0, sessionDate.lastIndexOf("-")).replace(":", "");
			String endTime =  sessionDate.substring(sessionDate.lastIndexOf("-")+1, sessionDate.length()).replace(":", "");
			String vipSql = "SELECT QUE_NO,START_TIME FROM REG_CLINICQUE "
					+ "WHERE ADM_TYPE='" + xmlList.get(0).get("ADM_TYPE").toString() + "' AND ADM_DATE='"
					+ xmlList.get(0).get("ADM_DATE").toString().replace("-", "") + "'" + " AND SESSION_CODE='"
					+xmlList.get(0).get("SESSION_CODE").toString() + "' AND CLINICROOM_NO='"
					+xmlList.get(0).get("CLINICROOM_NO").toString()
					+ "' AND  QUE_STATUS='N' AND START_TIME BETWEEN "+starTime+" AND "+endTime +" ORDER BY QUE_NO";
			TParm result = new TParm(TJDODBTool.getInstance().select(vipSql));
			if (result.getErrCode() < 0) {
				 return XMLParamUtils.sendError("-1", "预约挂号失败");
			}
			if (result.getCount()<=0) {
				 return XMLParamUtils.sendError("-1", "当前时段挂号已满,不可以操作预约");
			}
			queNo = result.getInt("QUE_NO",0);
			startTime  = result.getValue("START_TIME",0);
		}
		
		sql = "SELECT CASE_NO FROM REG_PATADM WHERE MR_NO='"+xmlList.get(0).get("MR_NO").toString()+"'";
		TParm regPatadm = new TParm(TJDODBTool.getInstance().select(sql));
		if (regPatadm.getErrCode()<0) {
			 return XMLParamUtils.sendError("-1", "预约挂号失败");
		}
		String newCaseNo = SystemTool.getInstance().getNo("ALL", "REG",
                  "CASE_NO", "CASE_NO");
		if (newCaseNo == null || newCaseNo.length() == 0) {
            return XMLParamUtils.sendError("-1", "预约挂号失败");
        }
		String visitCode ="0";//初复诊
		if(regPatadm.getCount()>0){
			visitCode = "1";
		}
		String sqlQue="UPDATE REG_SCHDAY SET QUE_NO=QUE_NO + 1    WHERE REGION_CODE = '"+sysRegion.getRow(0).getValue("REGION_CODE")+"' "+
		          " AND ADM_TYPE = '"+xmlList.get(0).get("ADM_TYPE").toString()+"'  AND ADM_DATE = '"+xmlList.get(0).get("ADM_DATE").toString().replace("-", "")
		          +"'  AND SESSION_CODE = '"+xmlList.get(0).get("SESSION_CODE").toString()+"' "+
			  " AND CLINICROOM_NO='"+xmlList.get(0).get("CLINICROOM_NO")+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().update(sqlQue,connection)); // 获得是否可以打票注记
		if(result.getErrCode()<0){
			connection.rollback();
			connection.close();
			return XMLParamUtils.sendError("-1", "预约挂号失败");
		}
		if(vipFlg){
			sqlQue="UPDATE REG_CLINICQUE   SET QUE_STATUS='Y'  WHERE  ADM_TYPE = '"+xmlList.get(0).get("ADM_TYPE").toString()
					+"'  AND ADM_DATE = '"+xmlList.get(0).get("ADM_DATE").toString().replace("-", "")
					+"'  AND SESSION_CODE = '"+xmlList.get(0).get("SESSION_CODE").toString()+"' "+
					" AND CLINICROOM_NO='"+xmlList.get(0).get("CLINICROOM_NO")+"' AND QUE_NO="+queNo;
			result = new TParm(TJDODBTool.getInstance().update(sqlQue,connection)); // 获得是否可以打票注记
			if(result.getErrCode()<0){
				connection.rollback();
				connection.close();
				return XMLParamUtils.sendError("-1", "预约挂号失败");
			}
		}
		sql="INSERT INTO REG_PATADM(CASE_NO,ADM_TYPE,MR_NO,REGION_CODE,ADM_DATE,REG_DATE,SESSION_CODE,"
				+ "CLINICAREA_CODE,CLINICROOM_NO,VIP_FLG,CLINICTYPE_CODE,QUE_NO,DEPT_CODE,"
				+ "DR_CODE,REALDEPT_CODE,REALDR_CODE,APPT_CODE,VISIT_CODE,REGMETHOD_CODE,CTZ1_CODE"
				+ ",ARRIVE_FLG,ADM_REGION,ADM_STATUS,REPORT_STATUS,SERVICE_LEVEL,OPT_USER,OPT_DATE,OPT_TERM,REG_ADM_TIME)"
				+ " VALUES('"+newCaseNo+"','"+xmlList.get(0).get("ADM_TYPE").toString()
				+"','"+xmlList.get(0).get("MR_NO").toString()+"','"+sysRegion.getRow(0).getValue("REGION_CODE")
				+"',TO_DATE('"+xmlList.get(0).get("ADM_DATE").toString()+"','YYYY/MM/DD'),SYSDATE,'"
				+xmlList.get(0).get("SESSION_CODE").toString()+"','"+regSchdParm.getValue("REG_CLINICAREA",0)+"','"
				+xmlList.get(0).get("CLINICROOM_NO").toString()+"','N','"+regSchdParm.getValue("CLINICTYPE_CODE",0)+"',"+queNo+",'"+
				xmlList.get(0).get("DEPT_CODE").toString()+"','"+
				xmlList.get(0).get("DR_CODE").toString()+"','"+
				xmlList.get(0).get("DEPT_CODE").toString()+"','"+
				xmlList.get(0).get("DR_CODE").toString()+"','Y','"+visitCode+"','N','99','N','"+
				sysRegion.getRow(0).getValue("REGION_CODE")
				+"','1','1','1','D001',SYSDATE,'127.0.0.1','"+startTime+"')";
		result = new TParm(TJDODBTool.getInstance().update(sql,connection)); // 获得是否可以打票注记
		if(result.getErrCode()<0){
			connection.rollback();
			connection.close();
			return XMLParamUtils.sendError("-1", "预约挂号失败");
		}
		connection.commit();
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		map.put("CASE_NO", newCaseNo);
		map.put("QUE_NO", queNo);
		map.put("REG_ADM_TIME", startTime);
		resultList.add(map);
		String [] listName = null;
		if(!vipFlg){
			listName = new String[] {"CASE_NO","QUE_NO"};
		}else{
			listName = new String[] {"CASE_NO","QUE_NO","REG_ADM_TIME"};
		}
		
		return XMLParamUtils.onGetJsonResultXml(listName, resultList);
	}
	/**
	 * 普通预约挂号操作
	 * @param xml
	 * @return
	 */
	private String regSchdInsert(String xml){
		// 
		List<String> list = new ArrayList<String>();				
		list.add("ADM_DATE");
		list.add("ADM_TYPE");
		list.add("SESSION_CODE");
		list.add("DEPT_CODE");
		list.add("DR_CODE");
		list.add("CLINICROOM_NO");
		list.add("MR_NO");
		
		return regSchdComm(list, xml, false);
		
		
		
	}
	private String regDrSchdVipQuery(String xml){
		// 查询数据库中是否存在用户 根据身份证号查询
		List<String> list = new ArrayList<String>();				
		list.add("DR_CODE");
		list.add("DEPT_CODE");
		list.add("SESSION_CODE");
		list.add("ADM_DATE");
		list.add("ADM_TYPE");
		list.add("CLINICROOM_NO");
		list.add("SESSION_DATE_01");
		list.add("SESSION_DATE_02");
		list.add("SESSION_DATE_03");
		List<Map<String, Object>> xmlList = XMLParamUtils.onGetXmlResultList(
				list, xml);
		String result = XMLParamUtils.onCheckError(xmlList, "解析失败");
		if (!result.equals("success")) {
			return result;
		}
		//第一时段
		Map<String, Object> map = new HashMap<String, Object>();
		String sessionDate01 =  xmlList.get(0).get("SESSION_DATE_01").toString();
		String starTime =  sessionDate01.substring(0, sessionDate01.lastIndexOf("-")).replace(":", "");
		String endTime =  sessionDate01.substring(sessionDate01.lastIndexOf("-")+1, sessionDate01.length()).replace(":", "");
		String sql="SELECT COUNT(SESSION_CODE) LAST_NUM FROM REG_CLINICQUE WHERE ADM_DATE='"
		+xmlList.get(0).get("ADM_DATE").toString().replace("-", "")
		+"' AND ADM_TYPE='"+xmlList.get(0).get("ADM_TYPE")+"' AND SESSION_CODE='"
		+xmlList.get(0).get("SESSION_CODE")+"' AND CLINICROOM_NO='"
		+xmlList.get(0).get("CLINICROOM_NO")+"' AND QUE_STATUS='N' AND START_TIME BETWEEN "+starTime+" AND "+endTime;
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		//System.out.println("parm：：：：：345345345345：：：：D:::::"+parm);
		if (parm.getCount() <= 0) {
			return XMLParamUtils.sendError("-1", "未获得信息");
		}
		map.put("LAST_NUM_01", parm.getValue("LAST_NUM",0));
		
		//第二时段
		String sessionDate02 =  xmlList.get(0).get("SESSION_DATE_02").toString();
		starTime =  sessionDate02.substring(0, sessionDate02.lastIndexOf("-")).replace(":", "");
		endTime =  sessionDate02.substring(sessionDate02.lastIndexOf("-")+1, sessionDate02.length()).replace(":", "");
		sql="SELECT COUNT(SESSION_CODE) LAST_NUM FROM REG_CLINICQUE WHERE ADM_DATE='"
		+xmlList.get(0).get("ADM_DATE").toString().replace("-", "")
		+"' AND ADM_TYPE='"+xmlList.get(0).get("ADM_TYPE")+"' AND SESSION_CODE='"
		+xmlList.get(0).get("SESSION_CODE")+"' AND CLINICROOM_NO='"
		+xmlList.get(0).get("CLINICROOM_NO")+"' AND QUE_STATUS='N' AND START_TIME BETWEEN "+starTime+" AND "+endTime;
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		//System.out.println("parm：：：：：23423423423：：：：D:::::"+parm);
		if (parm.getCount() <= 0) {
			return XMLParamUtils.sendError("-1", "未获得信息");
		}
		map.put("LAST_NUM_02", parm.getValue("LAST_NUM",0));
		
		//第三时段
		String sessionDate03 =  xmlList.get(0).get("SESSION_DATE_03").toString();
		starTime =  sessionDate03.substring(0, sessionDate03.lastIndexOf("-")).replace(":", "");
		endTime =  sessionDate03.substring(sessionDate03.lastIndexOf("-")+1, sessionDate03.length()).replace(":", "");
		sql="SELECT COUNT(SESSION_CODE) LAST_NUM FROM REG_CLINICQUE WHERE ADM_DATE='"
		+xmlList.get(0).get("ADM_DATE").toString().replace("-", "")
		+"' AND ADM_TYPE='"+xmlList.get(0).get("ADM_TYPE")+"' AND SESSION_CODE='"
		+xmlList.get(0).get("SESSION_CODE")+"' AND CLINICROOM_NO='"
		+xmlList.get(0).get("CLINICROOM_NO")+"' AND QUE_STATUS='N' AND START_TIME BETWEEN "+starTime+" AND "+endTime;
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		//System.out.println("parm：：：：234234234：：：：：D:::::"+parm);
		if (parm.getCount() <= 0) {
			return XMLParamUtils.sendError("-1", "未获得信息");
		}
		map.put("LAST_NUM_03", parm.getValue("LAST_NUM",0));
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		resultList.add(map);
		String [] listName ={"LAST_NUM_01","LAST_NUM_02","LAST_NUM_03"};
		return XMLParamUtils.onGetJsonResultXml(listName, resultList);
	}
	private String regSchdQuery(String xml){
		
		// 查询数据库中是否存在用户 根据身份证号查询
		List<String> list = new ArrayList<String>();
		list.add("DEPT_CODE");
		list.add("DR_CODE");
		list.add("START_DATE");
		list.add("END_DATE");
		List<Map<String, Object>> xmlList = XMLParamUtils.onGetXmlResultList(
				list, xml);
		String result = XMLParamUtils.onCheckError(xmlList, "解析失败");
		if (!result.equals("success")) {
			return result;
		}
		
		String sql = "SELECT A.*,B.DEPT_CHN_DESC DEPT_NAME,C.USER_NAME DR_NAME FROM "
				+ "(SELECT substr(A.ADM_DATE, 0, 4)||'-'||substr(A.ADM_DATE, 5, 2)||'-'||substr(A.ADM_DATE, 7, 2)  ADM_DATE,A.VIP_FLG,"
				+ "A.CLINICTYPE_CODE,CASE WHEN A.REALDEPT_CODE IS NULL THEN A.DEPT_CODE  ELSE A.REALDEPT_CODE END DEPT_CODE, A.CLINICROOM_NO, "
				+ "CASE  WHEN A.REALDR_CODE IS NULL  THEN A.DR_CODE  ELSE A.REALDR_CODE END DR_CODE,A.SESSION_CODE,B.SESSION_DESC SESSION_NAME,C.CLINICTYPE_DESC CLINICTYPE_NAME,A.ADM_TYPE  "
				+ "FROM REG_SCHDAY A,REG_SESSION B ,REG_CLINICTYPE C  WHERE A.SESSION_CODE=B.SESSION_CODE AND A.ADM_TYPE=C.ADM_TYPE AND A.CLINICTYPE_CODE =C.CLINICTYPE_CODE AND A.STOP_SESSION='N' "
				+ " AND ADM_DATE BETWEEN '"+xmlList.get(0).get("START_DATE").toString().replace("-", "")+"' AND '"+xmlList.get(0).get("END_DATE").toString().replace("-", "")+"') A,SYS_DEPT B ,"
						+ "SYS_OPERATOR C WHERE A.DEPT_CODE=B.DEPT_CODE  AND A.DR_CODE= C.USER_ID ";   
		if (null !=xmlList.get(0).get("DEPT_CODE") && xmlList.get(0).get("DEPT_CODE").toString().length()>0) {
			sql += " AND A.DEPT_CODE = '"+xmlList.get(0).get("DEPT_CODE")+"'";
		}
		if (null !=xmlList.get(0).get("DR_CODE") && xmlList.get(0).get("DR_CODE").toString().length()>0) {
			sql += " AND A.DR_CODE = '"+xmlList.get(0).get("DR_CODE")+"'";
		}
		sql+= " ORDER BY A.DEPT_CODE,A.DR_CODE,A.ADM_TYPE,A.ADM_DATE,A.SESSION_CODE ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		System.out.println("排班sql：：："+sql);
		//System.out.println("parm：：：：：：：：：D:::::"+parm);
		if(parm.getErrCode()<0){
			return XMLParamUtils.sendError("-1", "查询信息失败");
		}
		if (parm.getCount() <= 0) {
			return XMLParamUtils.sendError("0", "未获得信息");
		}
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		Map<String, Object> map = null;
		for (int i = 0; i < parm.getCount(); i++) {
			map = new HashMap<String, Object>();
			for (int j = 0; j < parm.getNames().length; j++) {
				map.put(parm.getNames()[j],
						parm.getValue(parm.getNames()[j], i));
			}
			resultList.add(map);
		}
		return XMLParamUtils.onGetJsonResultXml(parm.getNames(), resultList);
	}
	
	private String sysDeptLoad(String xml){
		String sql = "SELECT B.DEPT_CODE ,B.DEPT_CHN_DESC DEPT_NAME, 'Y' ACTIVE_FLG FROM REG_SCHDAY A,SYS_DEPT B WHERE A.DEPT_CODE = B.DEPT_CODE "+
				"AND B.ACTIVE_FLG='Y' GROUP BY B.DEPT_CODE ,B.DEPT_CHN_DESC";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() <= 0) {
			return XMLParamUtils.sendError("0", "未获得信息");
		}
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = null;
		for (int i = 0; i < parm.getCount(); i++) {
			map = new HashMap<String, Object>();
			for (int j = 0; j < parm.getNames().length; j++) {
				map.put(parm.getNames()[j],
						parm.getValue(parm.getNames()[j], i));
			}
			resultList.add(map);
		}
		String xmlResult =XMLParamUtils.onGetJsonResultXml(parm.getNames(), resultList);
		//System.out.println("xmlResult:::"+xmlResult);
		return xmlResult;
	}
	//yanmm   医生
	private String sysDrinfoLoad(String xml){
		String sql = " SELECT B.USER_ID, B.USER_NAME FROM REG_SCHDAY A, SYS_OPERATOR B WHERE A.DR_CODE = B.USER_ID  GROUP BY B.USER_ID, B.USER_NAME";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() <= 0) {
			return XMLParamUtils.sendError("0", "未获得信息");
		}
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = null;
		for (int i = 0; i < parm.getCount(); i++) {
			map = new HashMap<String, Object>();
			for (int j = 0; j < parm.getNames().length; j++) {
				map.put(parm.getNames()[j],
						parm.getValue(parm.getNames()[j], i));
			}
			resultList.add(map);
		}
		String xmlResult =XMLParamUtils.onGetJsonResultXml(parm.getNames(), resultList);
		//System.out.println("xmlResult:::"+xmlResult);
		return xmlResult;
	}
	
	private String sysPatinfoLoad(String xml) {
		// 查询数据库中是否存在用户 根据身份证号查询
		List<String> list = new ArrayList<String>();
		list.add("MR_NO");
		list.add("PAT_NAME");
		list.add("IDNO");
		List<Map<String, Object>> xmlList = XMLParamUtils.onGetXmlResultList(
				list, xml);
		String result = XMLParamUtils.onCheckError(xmlList, "解析失败");
		if (!result.equals("success")) {
			return result;
		}
		if (null == xmlList.get(0) || null == xmlList.get(0).get("IDNO")
				|| xmlList.get(0).get("IDNO").toString().length() <= 0) {
			return XMLParamUtils.sendError("-1", "未获得基本信息:身份证号");
		}
		String sql = "SELECT MR_NO,PAT_NAME,SEX_CODE,IDNO,BIRTH_DATE,CELL_PHONE TEL,E_MAIL EMAIL FROM SYS_PATINFO WHERE IDNO='"
				+ xmlList.get(0).get("IDNO") + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() <= 0) {
			return XMLParamUtils.sendError("0", "未获得信息");
		}
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		Map<String, Object> map = null;
		for (int i = 0; i < parm.getCount(); i++) {
			map = new HashMap<String, Object>();
			for (int j = 0; j < parm.getNames().length; j++) {
				map.put(parm.getNames()[j],
						parm.getValue(parm.getNames()[j], i));
			}
			resultList.add(map);
		}
		return XMLParamUtils.onGetJsonResultXml(parm.getNames(), resultList);
	}
	private String sysPatinfoInsert(String xml){
		//Pat pat = new Pat();
		// 查询数据库中是否存在用户 根据身份证号查询
		List<String> list = new ArrayList<String>();
		list.add("SEX_CODE");
		list.add("PAT_NAME");
		list.add("IDNO");
		list.add("BIRTH_DATE");
		list.add("TEL");
		list.add("EMAIL");
		List<Map<String, Object>> xmlList = XMLParamUtils
				.onGetXmlResultList(list, xml);
		String py =SYSHzpyTool.getInstance().charToCode(
					TypeTool.getString(xmlList.get(0).get("PAT_NAME")));
//		pat.setName(xmlList.get(0).get("PAT_NAME").toString());
//		// 姓名拼音
//		pat.setPy1(py);
//		// 证件类型
//		pat.setIdType("99"); // add by
//		pat.setName1("");
//		// 身份证号
//		pat.setIdNo(xmlList.get(0).get("IDNO").toString());
//		// 外国人注记
//		pat.setForeignerFlg(false);
//		// 出生日期
//		pat.setBirthday(TypeTool.getTimestamp(xmlList.get(0).get("BIRTH_DATE").toString()));
//		// 性别
//		pat.setSexCode(xmlList.get(0).get("SEX_CODE").toString());
//		// 电话
//		// pat.setTelHome(TypeTool.getString(getValue("TEL_HOME")));
//		pat.setCellPhone(xmlList.get(0).get("TEL").toString());
//		// 邮编
//		pat.setPostCode("");
//		// 地址
//		pat.setResidAddress("");
//		// 现住址
//		pat.setCurrentAddress(""); // add
//		pat.setAddress(""); // add
//		// 身份1
//		pat.setCtz1Code("99");
//		// 医保卡市民卡
//		pat.setNhiNo(TypeTool.getString("")); //
//		
//		// 备注
//		pat.setRemarks(""); // add by
//		pat.setNationCode("86");//国籍 ：中国
//		pat.setSpeciesCode("");
//		pat.setMarriageCode("");
//		pat.setFirstName("");
//		pat.setLastName("");
//		pat.setCcbPersonNo("");
		String newMrNo = SystemTool.getInstance().getMrNo();
		if (newMrNo == null || newMrNo.length() == 0) {
			return XMLParamUtils.sendError("-1", "创建失败");
		}
		String sql="INSERT INTO SYS_PATINFO(MR_NO,PAT_NAME,IDNO,ID_TYPE,PY1,BIRTH_DATE,SEX_CODE,"
				+ "CELL_PHONE,CTZ1_CODE,NATION_CODE,OPT_USER,OPT_DATE,OPT_TERM) VALUES('"+
				newMrNo+"','"+xmlList.get(0).get("PAT_NAME").toString()+
				"','"+xmlList.get(0).get("IDNO").toString()+"','99','"+py+"',TO_DATE('"+
				xmlList.get(0).get("BIRTH_DATE").toString()+"','YYYY-MM-DD'),'"
				+xmlList.get(0).get("SEX_CODE").toString()+"','"
				+xmlList.get(0).get("TEL").toString()+"','99','86','WEB01',SYSDATE,'127.0.0.1')";
		//System.out.println("sql::::"+sql);
		try {
			TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
			if(parm.getErrCode()<0){
				return XMLParamUtils.sendError("-1", "创建失败");
			}
		} catch (Exception e) {
			// TODO: handle exception
			return XMLParamUtils.sendError("-1", "创建失败");
		}

		String[] name = {"PAT_NAME","IDNO","MR_NO"};
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("MR_NO", newMrNo);
		map.put("IDNO", xmlList.get(0).get("IDNO").toString());
		map.put("PAT_NAME",xmlList.get(0).get("PAT_NAME").toString());
		resultList.add(map);
		String xml1=XMLParamUtils.onGetJsonResultXml(name, resultList);
		return xml1.toString();
	}
}
