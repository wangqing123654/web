package jdo.inw;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title: סԺ��ʿվ�����ܽӿ�    
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * @author ZangJH
 * 
 * @version 1.0
 */
public class InwForOutSideTool extends TJDODBTool {

	/**
	 * ʵ��
	 */
	private static InwForOutSideTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PatTool
	 */
	public static InwForOutSideTool getInstance() {
		if (instanceObject == null)
			instanceObject = new InwForOutSideTool();
		return instanceObject;
	}

	public InwForOutSideTool() {
	}
   //=============================================   chenxi modify 20130321
	/**
	 * ���ò���ҽ���Ƿ���δִ�е�ҽ��
	 * 
	 * @return boolean true-����Ϊִ�е� false-û��
	 */
	public boolean checkOrderisEXETool(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_DSPNM WHERE CASE_NO='"
				+ caseNo
				+ "' AND (NS_EXEC_CODE IS NULL OR (DC_DR_CODE IS NOT NULL AND NS_EXEC_DC_CODE IS NULL)) "//��ӳ���ҽ��ͣ��ִ��У��
				+ "AND DSPN_KIND<>'RT' AND DC_NS_CHECK_DATE IS NULL";//shibl  20121101 add ��ʿȡ����˵�ʱ��
		TParm result = new TParm(select(checkSql));
		// ���û��Ϊִ�е����ݷ������ݼ�����Ϊ0
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	   //=============================================   chenxi modify 20130321
	/**
	 * ���ò����Ƿ���ҩ��δ��˵�ҽ��
	 * 
	 * @return boolean true-����Ϊִ�е� false-û��
	 */
	public boolean checkDrug(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_ORDER WHERE CASE_NO='"
				+ caseNo
				+ "' AND NS_CHECK_CODE IS NOT NULL AND RX_KIND IN ('UD','ST','DS') " 
				+"   AND DC_NS_CHECK_DATE IS NULL " 
				+"   AND (ORDER_CAT1_CODE='PHA_W' OR ORDER_CAT1_CODE='PHA_C')" 
				//��������ҽ�� wanglong add 20150410
			    + "  AND  (OPBOOK_SEQ IS NULL OR (OPBOOK_SEQ IS NOT NULL AND EXEC_DEPT_CODE IN (SELECT DEPT_CODE FROM SYS_DEPT WHERE CLASSIFY='2'))) "
	            +"   AND  DISPENSE_FLG='N' " 
				+"   AND  PHA_CHECK_DATE IS NULL 	";
//		System.out.println("sql=========11111==="+checkSql);
		TParm result = new TParm(select(checkSql));
		// ���û��Ϊִ�е����ݷ������ݼ�����Ϊ0
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	/**
	 * ���ò����Ƿ���ҩ��δ��ҩ��ҽ��
	 * 
	 * @return boolean true-����δִ�е� false-û��
	 */
	public boolean exeDrug(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
        String checkSql =
                "SELECT COUNT(A.CASE_NO) AS COUNT             "
                        + " FROM ODI_DSPNM A, SYS_FEE B                "
                        + "WHERE A.ORDER_CODE = B.ORDER_CODE "// wanglong add 20140725 ���˵�Ϊ��ע��ҽ�������ֶ���Ĭ��ֵ�������ʾ�Ǳ�ע��
                        + "   AND (B.IS_REMARK <> 'Y' OR B.IS_REMARK IS NULL) AND A.CASE_NO='"
                        + caseNo
                        + "'  AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')"
                        + "   AND A.PHA_DOSAGE_CODE IS NULL AND A.PHA_CHECK_CODE IS NOT NULL "
                        + "   AND A.DC_DATE IS NULL AND A.DC_NS_CHECK_DATE IS NULL"
                        + "   AND A.DISPENSE_FLG='N'";
//		System.out.println("sql=========222222==="+checkSql);
		TParm result = new TParm(select(checkSql));
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	   //=============================================   chenxi modify 20130321
	/**
	 * ���ò���ҽ���Ƿ���δ��˵�ҽ��
	 * 
	 * @return boolean true-����Ϊ��˵� false-û��
	 */
	public boolean checkOrderisCHECKTool(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_ORDER WHERE CASE_NO='"
				+ caseNo + "' AND (NS_CHECK_CODE IS NULL OR (DC_DR_CODE IS NOT NULL AND DC_NS_CHECK_CODE IS NULL))"
				         //fux modify 20190227 �ǿ����ز�У��
						 + "  AND  ANTIBIOTIC_CODE IS NULL  ";
		//wanglong add 20150106
        checkSql+=" AND OPBOOK_SEQ IS NULL ";//��������ҽ��
		TParm result = new TParm(select(checkSql));    
//		System.out.println("checkSql>>>>>" + checkSql);  
//		System.out.println("result>>>>>" + result.getCount());
//		System.out.println("COUNT>>>>>" + result.getInt("COUNT", 0));
		// ���û��Ϊִ�е����ݷ������ݼ�����Ϊ0
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	/**
	 * ���ò����Ƿ���ҽ��
	 * 
	 * @return boolean true
	 */
	public boolean checkOrderisEXISTTool(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_ORDER WHERE CASE_NO='"
				+ caseNo + "' ";
		TParm result = new TParm(select(checkSql));
		// ���û��Ϊִ�е����ݷ������ݼ�����Ϊ0
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	/**
	 * У��ҽ���Ʒ�
	 * @param parm
	 * @return
	 */
	public TParm checkExmOrderFee(TParm parm){
		String caseNo = parm.getValue("CASE_NO");
		String sql=" SELECT A.ORDER_DESC FROM ODI_DSPNM A, ODI_DSPNM B "
				+ " WHERE     A.CASE_NO = B.CASE_NO "
				+ "  AND A.ORDER_NO = B.ORDER_NO AND A.ORDER_SEQ = B.ORDER_SEQ "
				+ "  AND A.HIDE_FLG='N' AND A.NS_EXEC_CODE IS NOT NULL AND A.CAT1_TYPE IN ('LIS','RIS')"
				+ "  AND (A.BILL_FLG='N' OR A.BILL_FLG IS NULL) AND A.CASE_NO='"+ caseNo + "' ";
//		System.out.println("============="+sql);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
	    if(result.getCount()>0||result.getErrCode()<0){
	    	return result;
	    }
	    return result;
	}
	/**
	 * ���ò����Ƿ񻹴�����ִ��ҽ��
	 * 
	 * @return boolean true
	 */
	public boolean checkOrderisExistExec(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_DSPNM WHERE CASE_NO='"
				+ caseNo + "' AND NS_EXEC_CODE IS NOT NULL AND DC_NS_CHECK_DATE IS NULL  ";
		if(!Parm.getValue("IN_DEPT_CODE").equals("")){
			checkSql+=" AND  DEPT_CODE='"+Parm.getValue("IN_DEPT_CODE")+"'";
		}
		if(!Parm.getValue("IN_STATION_CODE").equals("")){
			checkSql+=" AND  STATION_CODE='"+Parm.getValue("IN_STATION_CODE")+"'";
		}
		if(!Parm.getValue("IN_DATE").equals("")){
			checkSql+=" AND  ORDER_DATE>=TO_DATE('"+Parm.getValue("IN_DATE")+"','YYYYMMDD HH24MISS')";
		}
		TParm result = new TParm(select(checkSql));
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	/**
	 * ���ò����Ƿ񻹴��������ҽ��
	 * 
	 * @return boolean true
	 */
	public boolean checkOrderisExistCheck(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_ORDER WHERE CASE_NO='"
				+ caseNo + "' AND NS_CHECK_CODE IS NOT NULL  ";
		if(!Parm.getValue("IN_DEPT_CODE").equals("")){
			checkSql+=" AND  DEPT_CODE='"+Parm.getValue("IN_DEPT_CODE")+"'";
		}
		if(!Parm.getValue("IN_STATION_CODE").equals("")){
			checkSql+=" AND  STATION_CODE='"+Parm.getValue("IN_STATION_CODE")+"'";
		}
		if(!Parm.getValue("IN_DATE").equals("")){
			checkSql+=" AND  ORDER_DATE>=TO_DATE('"+Parm.getValue("IN_DATE")+"','YYYYMMDD HH24MISS')";
		}
		TParm result = new TParm(select(checkSql));
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	/**
	 * ���ò����Ƿ񻹴���ҽ��
	 * 
	 * @return boolean true
	 */
	public boolean checkOrderisExist(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_ORDER WHERE CASE_NO='"
				+ caseNo + "' ";
		if(!Parm.getValue("IN_DEPT_CODE").equals("")){
			checkSql+=" AND  DEPT_CODE='"+Parm.getValue("IN_DEPT_CODE")+"'";
		}
		if(!Parm.getValue("IN_STATION_CODE").equals("")){
			checkSql+=" AND  STATION_CODE='"+Parm.getValue("IN_STATION_CODE")+"'";
		}
		if(!Parm.getValue("IN_DATE").equals("")){
			checkSql+=" AND  ORDER_DATE>=TO_DATE('"+Parm.getValue("IN_DATE")+"','YYYYMMDD HH24MISS')";
		}
		TParm result = new TParm(select(checkSql));
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	/**
	 * ���ò����Ƿ���ҽ��
	 * 
	 * @return boolean true
	 */
	public TParm checkOrderFee(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT SUM(TOT_AMT) AS TOT_AMT FROM IBS_ORDD WHERE CASE_NO='"
				+ caseNo + "' ";
		if(!Parm.getValue("IN_DEPT_CODE").equals("")){
			checkSql+=" AND  DEPT_CODE='"+Parm.getValue("IN_DEPT_CODE")+"'";
		}
		if(!Parm.getValue("IN_STATION_CODE").equals("")){
			checkSql+=" AND  STATION_CODE='"+Parm.getValue("IN_STATION_CODE")+"'";
		}
		if(!Parm.getValue("IN_DATE").equals("")){
			checkSql+=" AND  BILL_DATE>=TO_DATE('"+Parm.getValue("IN_DATE")+"','YYYYMMDD HH24MISS')";
		}
		TParm result = new TParm(select(checkSql));
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return result;
		return result;
	}
}
