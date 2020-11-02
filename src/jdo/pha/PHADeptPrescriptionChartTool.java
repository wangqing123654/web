package jdo.pha;

/**
 * <p>
 * Title: 处方量统计报表Tool
 * </p>
 *
 * <p>
 * Description: 处方量统计报表Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author guangl 2016.05.23
 * @version 1.0
 */

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class PHADeptPrescriptionChartTool extends TJDOTool {

	private static PHADeptPrescriptionChartTool instance = null;

	public static PHADeptPrescriptionChartTool getInstance() {
		if (instance == null) {
			instance = new PHADeptPrescriptionChartTool();
		}
		return instance;
	}

	private PHADeptPrescriptionChartTool() {

	}


	
	public TParm getQueryData(TParm parm){
		//默认查询条件
		String dose = "";
		String hexp = "";
		String horm = "";   //add by wukai on 20161013
		String pha_c = "";
		String pha_w ="";
		//添加查询条件
		if("".equals(parm.getValue("PHA_TYPE"))){
			pha_c += "PHA_C";
			pha_w += "PHA_W";
		}else if("PHA_C".equals(parm.getValue("PHA_TYPE"))){
			pha_c += "PHA_C";
		}else{
			pha_w += "PHA_W";
		}
		if(parm.getBoolean("DOSE_FLAG")){
			dose+="1";
		}
		if(parm.getBoolean("HEXP_FLAG")){
			hexp+="1.1";
		}
		if(parm.getBoolean("_FLAG")) {
			horm += "1.2";
		}
		//时间查询条件
		String date = " AND TO_CHAR(A.BILL_DATE,'YYYY-MM-DD HH:MM:SS') BETWEEN '" + parm.getValue("START_DATE") + "' AND '" + parm.getValue("END_DATE") + "' ";
		String sql2 = 
				"SELECT  T.DEPT_CODE , " +
				        "T.DR_CODE , " +
				        "S1.TOTAL AS PHA_W_NUM, " +
				        "S1.AMOUNT AS PHA_W_AMOUNT, " + 
				        "S2.TOTAL AS PHA_C_NUM, " + 
				        "S2.AMOUNT AS PHA_C_AMOUNT, " + 
				        "S3.TOTAL AS HEXP_NUM, " + 
				        "S3.AMOUNT AS HEXP_AMOUNT, " + 
				        "S4.TOTAL AS DOSE_NUM, " + 
				        "S4.AMOUNT AS DOSE_AMOUNT, " +
				        "S5.TOTAL AS HORM_NUM, " +
				        "S5.AMOUNT AS HORM_AMOUNT " +
				    "FROM(   SELECT A.DEPT_CODE , A.DR_CODE " + 
				            "FROM OPD_ORDER A, " +
				                 "SYS_DEPT B, " +
				                 "SYS_OPERATOR C, " +
				                 "PHA_BASE D " +
				            "WHERE    A.DEPT_CODE = B.DEPT_CODE " +
				                "AND A.DR_CODE = C.USER_ID " +
				                 "AND A.ORDER_CODE = D.ORDER_CODE " +
				                 "AND A.BILL_FLG = 'Y'" +
				            "GROUP BY A.DR_CODE, A.DEPT_CODE) T " +
				            "LEFT JOIN " +
				            "(   SELECT S.DEPT_CODE , S.DR_CODE , COUNT(S.CODE) AS TOTAL , SUM(S.AMT) AS AMOUNT " +
				                "FROM (" +
				                	"SELECT A.DEPT_CODE , A.DR_CODE , A.RX_NO||B.CLASSIFY_TYPE AS CODE , SUM(A.AR_AMT) AS AMT " +
				                	"FROM OPD_ORDER A LEFT JOIN SYS_PHAROUTE B ON A.ROUTE_CODE = B.ROUTE_CODE ,  SYS_DEPT C , SYS_OPERATOR D " +
				                	"WHERE   A.ORDER_CAT1_CODE = '" + pha_w +"' " +
					                 "AND A.DEPT_CODE = C.DEPT_CODE " +
					                 "AND A.DR_CODE = D.USER_ID " + 
					                 "AND A.BILL_FLG = 'Y'" +
					                 date +
				                	"GROUP BY A.RX_NO , B.CLASSIFY_TYPE , A.DEPT_CODE , A.DR_CODE" +
				                	") S GROUP BY S.DEPT_CODE , S.DR_CODE" +
				            ") S1 ON T.DEPT_CODE = S1.DEPT_CODE AND T.DR_CODE = S1.DR_CODE " +
				        "LEFT JOIN " +
				        "(   SELECT S.DEPT_CODE , S.DR_CODE , COUNT(S.CODE) AS TOTAL , SUM(S.AMT) AS AMOUNT " +
		                "FROM (" +
		                "SELECT A.DEPT_CODE , A.DR_CODE , A.RX_NO||B.CLASSIFY_TYPE AS CODE , SUM(A.AR_AMT) AS AMT " +
	                	"FROM OPD_ORDER A LEFT JOIN SYS_PHAROUTE B ON A.ROUTE_CODE = B.ROUTE_CODE ,  SYS_DEPT C , SYS_OPERATOR D " +
	                	"WHERE   A.ORDER_CAT1_CODE = '" + pha_c +"' " +
		                 "AND A.DEPT_CODE = C.DEPT_CODE " +
		                 "AND A.DR_CODE = D.USER_ID " + 
		                 "AND A.BILL_FLG = 'Y'" +
		                 date +
	                	"GROUP BY A.RX_NO , B.CLASSIFY_TYPE , A.DEPT_CODE , A.DR_CODE" +
		                	") S GROUP BY S.DEPT_CODE , S.DR_CODE" +
		            ") S2 ON T.DEPT_CODE = S2.DEPT_CODE AND T.DR_CODE = S2.DR_CODE " +
				        "LEFT JOIN " +
				        "(   SELECT S.DEPT_CODE , S.DR_CODE , COUNT(S.CODE) AS TOTAL , SUM(S.AMT) AS AMOUNT " +
		                "FROM (" +
		                "SELECT A.DEPT_CODE , A.DR_CODE , A.RX_NO||B.CLASSIFY_TYPE AS CODE , SUM(A.AR_AMT) AS AMT " +
	                	"FROM OPD_ORDER A LEFT JOIN SYS_PHAROUTE B ON A.ROUTE_CODE = B.ROUTE_CODE ,  SYS_DEPT C , SYS_OPERATOR D " +
	                	"WHERE   A.HEXP_CODE = '" + hexp +"' " +
		                 "AND A.DEPT_CODE = C.DEPT_CODE " +
		                 "AND A.DR_CODE = D.USER_ID " + 
		                 "AND A.BILL_FLG = 'Y'" +
		                 date +
	                	"GROUP BY A.RX_NO , B.CLASSIFY_TYPE , A.DEPT_CODE , A.DR_CODE" +
		                	") S GROUP BY S.DEPT_CODE , S.DR_CODE" +
		            ") S3 ON T.DEPT_CODE = S3.DEPT_CODE AND T.DR_CODE = S3.DR_CODE " +
				        "LEFT JOIN " +
				        "(   SELECT S.DEPT_CODE , S.DR_CODE , COUNT(S.CODE) AS TOTAL , SUM(S.AMT) AS AMOUNT " +
		                "FROM (" +
		                "SELECT A.DEPT_CODE , A.DR_CODE , A.RX_NO||B.CLASSIFY_TYPE AS CODE , SUM(A.AR_AMT) AS AMT " +
		                "FROM OPD_ORDER A LEFT JOIN SYS_PHAROUTE B ON A.ROUTE_CODE = B.ROUTE_CODE ,  SYS_DEPT C , SYS_OPERATOR D " +
	                	"WHERE   '1'= '"+dose+"' AND B.CLASSIFY_TYPE IN ('I' , 'F') " +
		                 "AND A.DEPT_CODE = C.DEPT_CODE " +
		                 "AND A.DR_CODE = D.USER_ID " + 
		                 "AND A.BILL_FLG = 'Y'" +
		                 date +
	                	"GROUP BY A.RX_NO , B.CLASSIFY_TYPE , A.DEPT_CODE , A.DR_CODE" +
		                	") S GROUP BY S.DEPT_CODE , S.DR_CODE" +
		            ") S4 ON T.DEPT_CODE = S4.DEPT_CODE AND T.DR_CODE = S4.DR_CODE " +
				            "LEFT JOIN " +
				            "(   SELECT A.DEPT_CODE , A.DR_CODE , COUNT(A.CASE_NO) AS TOTAL , SUM(A.AR_AMT) AS AMOUNT " +
				            	 "FROM OPD_ORDER A, " +
				                 "SYS_DEPT B, " +
				                 "SYS_OPERATOR C, " +
				                 "PHA_BASE D " +
				            "WHERE    A.HEXP_CODE = '"+ horm +"' " +    
				            	//modify by wukai on 20161013 =========start
				                 "AND A.ORDER_CODE LIKE '1K%' "  +
				               //modify by wukai on 20161013 =========stop
				                 "AND A.ORDER_CODE = D.ORDER_CODE " +
				                 "AND A.DEPT_CODE = B.DEPT_CODE " +
				                 "AND A.DR_CODE = C.USER_ID " +
				                 "AND A.BILL_FLG = 'Y'" +
				                 date +
				        "GROUP BY A.DEPT_CODE , A.DR_CODE) S5 ON T.DEPT_CODE = S5.DEPT_CODE AND T.DR_CODE = S5.DR_CODE " +
				"WHERE T.DEPT_CODE LIKE '%" + parm.getValue("DEPT_CODE") +"%' AND T.DR_CODE LIKE '%" +  parm.getValue("DR_CODE") + "%' " +
				"AND (S1.TOTAL>0 OR S2.TOTAL>0 OR S3.TOTAL>0 OR S4.TOTAL>0 OR S5.TOTAL>0) " +
				"ORDER BY T.DEPT_CODE , T.DR_CODE ";
		System.out.println(sql2);
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql2));
		
		return result;
	}

}
