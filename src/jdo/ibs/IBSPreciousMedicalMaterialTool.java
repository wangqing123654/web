package jdo.ibs;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * <p>
 * Title: ��������ͳ����ϸ
 * </p>
 * 
 * <p>
 * Description: ��������ͳ����ϸ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author wufc 2012-8-10����10:24:34
 * @version 1.0
 */
public class IBSPreciousMedicalMaterialTool extends TJDOTool {
	private static IBSPreciousMedicalMaterialTool instance;

	private IBSPreciousMedicalMaterialTool() {
	}

	public static IBSPreciousMedicalMaterialTool getInstance() {
		if (instance == null) {
			instance = new IBSPreciousMedicalMaterialTool();
		}
		return instance;
	}

	/**
	 * ������ʼ���ڲ�ѯ��ֵ�Ĳķ�����ϸ
	 * 
	 * @return
	 */
	public TParm onQuery(TParm parm) {
		
		TParm result = new TParm();
		String startDateStr = (String) parm.getData("startDate");
		String endDateStr = (String) parm.getData("endDate");
		String sqlSuppliesType ="";	
		String AdmType = parm.getData("ADM_TYPE").toString();
		String sqlorder_code = "";
		String sqlorder_code_i = "";
//		String sqlAdmType = "";
		String sqlMrno = "";
		String exeDept = "";
		String sql = null;
		if(parm.getData("ORDER_CODE")!=null && !parm.getData("ORDER_CODE").equals("")){
			sqlorder_code = "AND A.ORDER_CODE = '"+parm.getData("ORDER_CODE")+"'";
		}
		if(parm.getData("ORDER_CODE")!=null && !parm.getData("ORDER_CODE").equals("")&&AdmType.equals("I")){
			sqlorder_code_i = "AND B.ORDER_CODE = '"+parm.getData("ORDER_CODE")+"'";
		}
		if(parm.getData("SUPPLIES_TYPE") !=null && !parm.getData("SUPPLIES_TYPE").equals("")){
			sqlSuppliesType = " AND C.SUPPLIES_TYPE = '"+parm.getData("SUPPLIES_TYPE")+"'" ;
		}
		if(parm.getData("MR_NO") != null && !parm.getData("MR_NO").equals("")) {
			sqlMrno = " AND A.MR_NO = '"+parm.getData("MR_NO")+"'";
		}
		if(parm.getData("EXE_DEPT_CODE") != null && !parm.getData("EXE_DEPT_CODE").equals("")) {
			exeDept = " AND H.DEPT_CODE = '"+parm.getData("EXE_DEPT_CODE")+"'";
		}
		if(AdmType.equals("E")) {
			sql = "SELECT A.DEPT_CODE,A.BILL_DATE,A.SPECIFICATION,A.MR_NO AS MR_NO,"
                  +"  D.PAT_NAME AS PAT_NAME,"
                 +"   F.USER_NAME AS USER_NAME,"
                 +"   A.ORDER_CODE AS ORDER_CODE,"
                 +"   C.ORDER_DESC || ' ' || "
                 +"   CASE WHEN C.SPECIFICATION IS NOT NULL THEN C.SPECIFICATION"
                 +"   ELSE ''  END AS ORDER_DESC,"
                 +"  A.DOSAGE_QTY AS DOSAGE_QTY,"
                 +"   G.UNIT_CHN_DESC AS UNIT_CHN_DESC,"
                 +"   A.OWN_PRICE AS OWN_PRICE,"
//                 +"   A.OWN_AMT AS OWN_AMT,"
                 +"   A.DOSAGE_QTY * A.OWN_PRICE  AS OWN_AMT,"
                 +"   H.DEPT_CHN_DESC AS DEPT_CHN_DESC, "
                 +"   M.DEPT_CHN_DESC AS DEPT_DESC "
                 +" FROM OPD_ORDER A,"
                 +"   SYS_FEE C,"
                 +"   SYS_PATINFO D ,       "          
                 +"   SYS_OPERATOR F,"
                 +"   SYS_UNIT G,"
                 +"   SYS_DEPT H, "
                 +"   SYS_DEPT M "
                 +" WHERE A.BILL_DATE BETWEEN TO_DATE('"+startDateStr+"','yyyymmddhh24miss')"
				 +  " AND TO_DATE('"+endDateStr+"','yyyymmddhh24miss')"
				 +  sqlorder_code
				 +   sqlSuppliesType
				 +   sqlMrno
				 +   exeDept
                 +" AND C.ORDER_CODE = A.ORDER_CODE"
                 +"   AND D.MR_NO = A.MR_NO"
                 +"   AND F.USER_ID = A.DR_CODE"
                 +"   AND G.UNIT_CODE = A.DOSAGE_UNIT"
                 +"   AND H.DEPT_CODE = A.EXEC_DEPT_CODE "
                 +"   AND M.DEPT_CODE = A.DEPT_CODE "
                 +"   AND C.ORDER_CAT1_CODE = 'MAT' "
                 +"   ORDER BY A.ORDER_CODE";
		}else if(AdmType.equals("H")) {
			sql = "SELECT A.DEPT_CODE,A.BILL_DATE,A.SPECIFICATION,A.MR_NO AS MR_NO,"
	                  +"  D.PAT_NAME AS PAT_NAME,"
	                  +"   F.USER_NAME AS USER_NAME,"
	                  +"   A.ORDER_CODE AS ORDER_CODE,"
	                  +"   C.ORDER_DESC || ' ' || "
	                  +"   CASE WHEN C.SPECIFICATION IS NOT NULL THEN C.SPECIFICATION"
	                  +"   ELSE ''  END AS ORDER_DESC,"
	                  +"  A.MEDI_QTY AS DOSAGE_QTY,"
	                  +"   A.MEDI_UNIT AS UNIT_CHN_DESC,"
	                  +"   A.OWN_PRICE AS OWN_PRICE,"
//	                  +"   A.OWN_AMT AS OWN_AMT,"
	                  +"   A.OWN_PRICE * A.MEDI_QTY  AS OWN_AMT,"
	                  +"   H.DEPT_CHN_DESC AS DEPT_CHN_DESC ,"
	                  +"   M.DEPT_CHN_DESC AS DEPT_DESC "
	                  +" FROM HRM_ORDER A,"
	                  +"   SYS_FEE C,"
	                  +"   SYS_PATINFO D ,"          
	                  +"   SYS_OPERATOR F,"
	                  +"   SYS_DEPT H ,"
	                  +"   SYS_DEPT M "
	                  +" WHERE A.BILL_DATE BETWEEN TO_DATE('"+startDateStr+"','yyyymmddhh24miss')"
	 				 +  " AND TO_DATE('"+endDateStr+"','yyyymmddhh24miss')"
	 				 +   sqlorder_code
	 				 +   sqlSuppliesType
	 				 +   sqlMrno
	 				 +   exeDept
	                  +" AND C.ORDER_CODE = A.ORDER_CODE"
	                  +"   AND D.MR_NO = A.MR_NO"
	                  +"   AND F.USER_ID = A.DR_CODE"
//	                  +"   AND G.UNIT_CODE = A.DOSAGE_UNIT"
	                  +"   AND H.DEPT_CODE = A.EXEC_DEPT_CODE "
	                  +"   AND M.DEPT_CODE = A.DEPT_CODE "
	                  +"   AND C.ORDER_CAT1_CODE = 'MAT' "
	                  +"   ORDER BY A.ORDER_CODE";
		}else if(AdmType.equals("I")) {
			sql ="SELECT A.DEPT_CODE,B.BILL_DATE,C.SPECIFICATION,A.MR_NO AS MR_NO,"
					+    " D.PAT_NAME AS PAT_NAME,"
					+    " E.STATION_DESC AS STATION_DESC,"
					+    " F.USER_NAME AS USER_NAME,"
					+    " B.ORDER_CODE AS ORDER_CODE,"
					+    " C.ORDER_DESC || ' ' || "
					+    " CASE WHEN C.SPECIFICATION IS NOT NULL THEN C.SPECIFICATION"
					+    " ELSE ''  END AS ORDER_DESC,"
					+    " B.DOSAGE_QTY AS DOSAGE_QTY,"
					+    " G.UNIT_CHN_DESC AS UNIT_CHN_DESC,"
					+    " B.OWN_PRICE AS OWN_PRICE,"
//					+    " B.OWN_AMT AS OWN_AMT,"
					+    " B.DOSAGE_QTY * B.OWN_PRICE AS OWN_AMT,"
					+    " H.DEPT_CHN_DESC AS DEPT_CHN_DESC, "
					+  "   M.DEPT_CHN_DESC AS DEPT_DESC "
					+ " FROM IBS_ORDM A,"
					+    " IBS_ORDD B,"
					+    " SYS_FEE C,"
					+    " SYS_PATINFO D,"
					+    " SYS_STATION E,"
					+    " SYS_OPERATOR F,"
					+    " SYS_UNIT G,"
					+    " SYS_DEPT H, "
//					+	 " SYS_BED  I,"
					+"   SYS_DEPT M "
					+ " WHERE A.BILL_DATE BETWEEN TO_DATE('"+startDateStr+"','yyyymmddhh24miss')"
					+    " AND  TO_DATE('"+endDateStr+"','yyyymmddhh24miss')"
					+    " AND A.CASE_NO = B.CASE_NO"
					+    " AND A.CASE_NO_SEQ = B.CASE_NO_SEQ"
					+    " AND C.ORDER_CODE = B.ORDER_CODE "
					+    sqlorder_code_i
					+    sqlSuppliesType
					+    sqlMrno
					+    exeDept
					+    " AND D.MR_NO = A.MR_NO"
					+    " AND E.STATION_CODE = B.STATION_CODE"
					+    " AND F.USER_ID = B.DR_CODE"
					+    " AND G.UNIT_CODE = B.DOSAGE_UNIT"
					+    " AND H.DEPT_CODE = B.EXE_DEPT_CODE "
//					+	 " AND I.BED_NO = A.BED_NO"
					+"   AND M.DEPT_CODE = A.DEPT_CODE " 
					+"   AND C.ORDER_CAT1_CODE = 'MAT' "
					+    " ORDER BY B.ORDER_CODE,A.BILL_DATE";	
		}
		result = new TParm(TJDODBTool.getInstance().select(sql));
	    //String sql2 = "insert into "
		return result;
	}
  
	public TParm onQuery1(TParm parm) {
		TParm result = new TParm();
		String startDateStr = (String) parm.getData("startDate");
		String endDateStr = (String) parm.getData("endDate");
		String sqlSuppliesType ="";	   //�Ĳ�
		String sqlAdmType = parm.getData("ADM_TYPE").toString();			//�������
		String sqlMrno = "";			//������
		String sqlExecode = "";			//ִ�п���
		String sqlorder_code = "";    //ҩ������ �ż������
		String sqlorder_code_i = ""; //ҩ������ סԺ
		if(parm.getData("ORDER_CODE")!=null && !parm.getData("ORDER_CODE").equals("")){
			sqlorder_code = "AND H.ORDER_CODE = '"+parm.getData("ORDER_CODE")+"'";
		}
		if(parm.getData("ORDER_CODE")!=null && !parm.getData("ORDER_CODE").equals("")&&sqlAdmType.equals("I")){
			sqlorder_code_i = "AND B.ORDER_CODE = '"+parm.getData("ORDER_CODE")+"'";
		}
		if(parm.getData("SUPPLIES_TYPE") !=null && !parm.getData("SUPPLIES_TYPE").equals("")){
			sqlSuppliesType = " AND C.SUPPLIES_TYPE = '"+parm.getData("SUPPLIES_TYPE")+"'" ;
		}
		if(parm.getData("EXE_DEPT_CODE") != null && !parm.getData("EXE_DEPT_CODE").equals("")) {
			sqlExecode = " AND S.DEPT_CODE = '"+parm.getData("EXE_DEPT_CODE")+"' ";
		}
		if(parm.getData("MR_NO") != null && !parm.getData("MR_NO").equals("")) {
			sqlMrno = "AND H.MR_NO = '"+parm.getData("MR_NO")+"'";
		}
		String sql = null;
		if(sqlAdmType.equals("H")) {
			sql = "SELECT S.DEPT_CHN_DESC,H.SPECIFICATION,H.ORDER_CODE,H.ORDER_DESC, "
				+"   H.OWN_PRICE AS OWN_PRICE,"
				+"  SUM(H.MEDI_QTY) AS DOSAGE_QTY,"
                +"   H.MEDI_UNIT AS UNIT_CHN_DESC,"
                +"   H.OWN_PRICE AS OWN_PRICE,"
                +"  H.OWN_PRICE * SUM(H.MEDI_QTY) AS OWN_AMT "
				+" FROM SYS_DEPT S,HRM_ORDER H,SYS_FEE C" 
				+" WHERE H.BILL_DATE BETWEEN TO_DATE('"+startDateStr+"','yyyymmddhh24miss')"
				+" AND TO_DATE('"+endDateStr+"','yyyymmddhh24miss') "
				+  sqlorder_code
				+ sqlSuppliesType 
				+ sqlExecode
				+ sqlMrno
//				+"   AND G.UNIT_CODE = H.DOSAGE_UNIT"
				+" AND S.DEPT_CODE=H.EXEC_DEPT_CODE AND C.ORDER_CODE=H.ORDER_CODE"
				+"   AND C.ORDER_CAT1_CODE = 'MAT' "
				+" GROUP BY H.OWN_PRICE,H.MEDI_UNIT, H.SPECIFICATION,H.ORDER_CODE,S.DEPT_CHN_DESC,H.ORDER_DESC ORDER BY S.DEPT_CHN_DESC";
		} else if(sqlAdmType.equals("E")) {
			sql = "SELECT S.DEPT_CHN_DESC,H.SPECIFICATION,H.ORDER_CODE,  H.ORDER_DESC ,"
				 +"  SUM(H.DOSAGE_QTY) AS DOSAGE_QTY,"
                 +"   G.UNIT_CHN_DESC AS UNIT_CHN_DESC,"
                 +"   H.OWN_PRICE AS OWN_PRICE,"
                 +"   H.OWN_PRICE * SUM(H.DOSAGE_QTY) AS OWN_AMT "
					+" FROM SYS_DEPT S,OPD_ORDER H,SYS_UNIT G,SYS_FEE C,sys_dept m,sys_patinfo d,sys_operator f" 
					+" WHERE H.BILL_DATE BETWEEN TO_DATE('"+startDateStr+"','yyyymmddhh24miss')"
					+" AND TO_DATE('"+endDateStr+"','yyyymmddhh24miss') "
					+  sqlorder_code
					+ sqlSuppliesType 
					+ sqlExecode
					+ sqlMrno
					+" AND d.mr_no = h.mr_no "
					+" AND f.user_id = h.dr_code"
					+" AND m.dept_code = h.dept_code"
					+"   AND G.UNIT_CODE = H.DOSAGE_UNIT"
					+" AND S.DEPT_CODE=H.EXEC_DEPT_CODE  AND C.ORDER_CODE=H.ORDER_CODE"
					+"   AND C.ORDER_CAT1_CODE = 'MAT' "
					+" GROUP BY UNIT_CHN_DESC, H.OWN_PRICE, H.SPECIFICATION,H.ORDER_CODE,S.DEPT_CHN_DESC,H.ORDER_DESC ORDER BY S.DEPT_CHN_DESC";
		} else if(sqlAdmType.equals("I")) {
			sql = "SELECT S.DEPT_CHN_DESC,C.SPECIFICATION,B.ORDER_CODE, C.ORDER_DESC ,"
				+    " SUM(B.DOSAGE_QTY) AS DOSAGE_QTY,"
				+    " G.UNIT_CHN_DESC AS UNIT_CHN_DESC,"
				+    " B.OWN_PRICE AS OWN_PRICE,"
				+    " B.OWN_PRICE * SUM(B.DOSAGE_QTY) AS OWN_AMT "
					+" FROM SYS_DEPT S,IBS_ORDM H,IBS_ORDD B,SYS_UNIT G,SYS_FEE C ,SYS_PATINFO D, SYS_STATION E, SYS_OPERATOR F,SYS_DEPT M " 
					+" WHERE H.BILL_DATE BETWEEN TO_DATE('"+startDateStr+"','yyyymmddhh24miss')"
					+" AND TO_DATE('"+endDateStr+"','yyyymmddhh24miss')"
					+" AND H.CASE_NO = B.CASE_NO AND H.CASE_NO_SEQ=B.CASE_NO_SEQ "
					+  sqlorder_code_i
					+  sqlSuppliesType 
					+  sqlExecode
					+  sqlMrno
					+    " AND D.MR_NO = H.MR_NO"
					+    " AND E.STATION_CODE = B.STATION_CODE"
					+    " AND F.USER_ID = B.DR_CODE"
//					+	 " AND I.BED_NO = H.BED_NO"
					+"   AND M.DEPT_CODE = H.DEPT_CODE " 
					+"   AND G.UNIT_CODE = B.DOSAGE_UNIT"
					+" AND S.DEPT_CODE=B.EXE_DEPT_CODE AND C.ORDER_CODE=B.ORDER_CODE "
					+"   AND C.ORDER_CAT1_CODE = 'MAT' "
					+" GROUP BY G.UNIT_CHN_DESC,B.OWN_PRICE,C.SPECIFICATION,B.ORDER_CODE,S.DEPT_CHN_DESC,C.ORDER_DESC ORDER BY S.DEPT_CHN_DESC";
			
		}
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
}
