package jdo.inf;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title:×¡Ôº²¡ÈËÄÍÒ©¾úÖê¼à²â¼ÇÂ¼</p>
 *
 * <p>Description:×¡Ôº²¡ÈËÄÍÒ©¾úÖê¼à²â¼ÇÂ¼</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2014</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author 2014.03.11
 * @version 1.0
 */
public class INFAntCuAccountUITool extends TJDOTool{
	
	private  String tableA;
	private  String tableB;
	private  String tableC;
	
	public static INFAntCuAccountUITool instanceObject;
		
		public static INFAntCuAccountUITool getInstance(){
		if(instanceObject==null){
			instanceObject=new INFAntCuAccountUITool();
		}
		   return instanceObject;
		}
		
		
public void setTableString(TParm parm){
	String mrNo="";
	String deptCode="";
	String sessionCode="";
	
	//¿ÆÊÒ
	if(!"".equals(parm.getValue("MR_NO"))&&parm.getValue("MR_NO")!=null){
		mrNo=" AND MR_NO='"+parm.getValue("MR_NO")+"' ";
	}
	//¿ÆÊÒ
	if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
		  deptCode=" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
	}
	//¿ÆÊÒ
	if(!"".equals(parm.getValue("STATION_CODE"))&&parm.getValue("STATION_CODE")!=null){
		sessionCode=" AND STATION_CODE='"+parm.getValue("STATION_CODE")+"' ";
	}
	
	String sqlA= 
		   "(SELECT DISTINCT CASE_NO,DEPT_CODE FROM ADM_INP " +
	            "WHERE " +
	            "IN_DATE BETWEEN TO_DATE ('"+parm.getValue("INS_DATE")+"','YYYYMMDDHH24MISS') "+
         	    "AND TO_DATE ('"+parm.getValue("INE_DATE")+"', 'YYYYMMDDHH24MISS') "+
         	    "AND DS_DATE BETWEEN TO_DATE ('"+parm.getValue("OUTS_DATE")+"','YYYYMMDDHH24MISS') "+
         	    "AND TO_DATE ('"+parm.getValue("OUTE_DATE")+"', 'YYYYMMDDHH24MISS') "
         	   +mrNo+deptCode+sessionCode+")A," ;
	
	       tableA= sqlA; 
	
         	String sqlB="(SELECT CASE_NO,MR_NO,PAT_NAME,DEPT_CODE,MAX(REPORT_DATE) AS REPORT_DATE " +
				" FROM MED_APPLY  " +
				" WHERE REPORT_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
				" AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
				" GROUP BY CASE_NO,MR_NO,PAT_NAME,DEPT_CODE) B ";
	        
         	tableB=sqlB;
         	String sqlC="(SELECT CASE_NO,MR_NO,PAT_NAME,DEPT_CODE,CAT1_TYPE,APPLICATION_NO,MAX(REPORT_DATE) AS REPORT_DATE " +
         	" FROM MED_APPLY  " +
         	" WHERE REPORT_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
         	" AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
         	" GROUP BY CASE_NO,MR_NO,PAT_NAME,DEPT_CODE,CAT1_TYPE,APPLICATION_NO) B, ";
         	
         	tableC=sqlC;
	
	
}


public TParm getselectTable(){
	TParm result=new TParm();
	String sql="SELECT A.CASE_NO,B.DEPT_CODE,B.PAT_NAME,B.MR_NO, " +
			   "TO_CHAR(B.REPORT_DATE,'yyyy/MM/dd HH:mm:ss') AS REPORT_DATE " +
				"FROM "+tableA+tableB+
		        "WHERE A.CASE_NO=B.CASE_NO "+
	            "AND A.DEPT_CODE=B.DEPT_CODE  ORDER BY A.DEPT_CODE,A.CASE_NO ";
	 //System.out.println("xxxxxxxxxxx"+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	
}
/**
 * ±ê±¾
 * @param parm
 * @return
 */

public TParm getselectBnum(){
	TParm  result=new TParm();
	String sql= "SELECT COUNT(A.CASE_NO)AS B_NUM ,A.CASE_NO,A.DEPT_CODE " +
				"FROM "+tableA+tableC +
				"MED_LIS_ANTITEST C ,"+
				"MED_LIS_MAP D "+
			    "WHERE A.CASE_NO=B.CASE_NO "+
			    "AND A.DEPT_CODE=B.DEPT_CODE "+
			    "AND B.CAT1_TYPE = C.CAT1_TYPE "+
			    "AND B.APPLICATION_NO = C.APPLICATION_NO " +
			    "AND C.CULTURE_CODE = D.LIS_ID "+
			    "AND D.MAP_ID = 'D01' "+
			    "GROUP BY A.DEPT_CODE,A.CASE_NO ORDER BY A.DEPT_CODE,A.CASE_NO  ";

				
				
	
	result = new TParm(TJDODBTool.getInstance().select(sql));
	if (result.getErrCode() < 0) {
		err("ERR:M " + result.getErrCode() + result.getErrText()
				+ result.getErrName());
		return result;
	}
	return result;
}
/**
 * Í­ÂÌ
 * @param parm
 * @return
 */

public TParm getselectTnum(){
	TParm  result=new TParm();
	String sql= "SELECT COUNT(A.CASE_NO)AS T_NUM ,A.CASE_NO,A.DEPT_CODE " +
				"FROM "+tableA+tableC +
				"MED_LIS_ANTITEST C ,"+
				"MED_LIS_MAP D "+
			    "WHERE A.CASE_NO=B.CASE_NO "+
			    "AND A.DEPT_CODE=B.DEPT_CODE "+
			    "AND B.CAT1_TYPE = C.CAT1_TYPE "+
			    "AND B.APPLICATION_NO = C.APPLICATION_NO " +
			    "AND C.CULTURE_CODE = D.LIS_ID "+
			    "AND D.MAP_ID = 'D01' "+
			    "GROUP BY A.DEPT_CODE,A.CASE_NO ORDER BY A.DEPT_CODE,A.CASE_NO  ";
	
	
	
	//System.out.println("xxxxxxxxxxx"+sql);
	result = new TParm(TJDODBTool.getInstance().select(sql));
	if (result.getErrCode() < 0) {
		err("ERR:M " + result.getErrCode() + result.getErrText()
				+ result.getErrName());
		return result;
	}
	return result;
}
/**
 * ±«Âü²»¶¯
 * @param parm
 * @return
 */

public TParm getselectBMnum(){
	TParm  result=new TParm();
	
	String sql= "SELECT COUNT(A.CASE_NO)AS BM_NUM ,A.CASE_NO,A.DEPT_CODE " +
				"FROM "+tableA+tableC +
				"MED_LIS_ANTITEST C ,"+
				"MED_LIS_MAP D "+
				"WHERE A.CASE_NO=B.CASE_NO "+
				"AND A.DEPT_CODE=B.DEPT_CODE "+
				"AND B.CAT1_TYPE = C.CAT1_TYPE "+
				"AND B.APPLICATION_NO = C.APPLICATION_NO " +
				"AND C.CULTURE_CODE = D.LIS_ID "+
				"AND D.MAP_ID = 'D01' "+
				"GROUP BY A.DEPT_CODE,A.CASE_NO ORDER BY A.DEPT_CODE,A.CASE_NO  ";

	
	
	
	//System.out.println("xxxxxxxxxxx"+sql);
	result = new TParm(TJDODBTool.getInstance().select(sql));
	if (result.getErrCode() < 0) {
		err("ERR:M " + result.getErrCode() + result.getErrText()
				+ result.getErrName());
		return result;
	}
	return result;
}
/**
 * ½ð¾ú
 * @param parm
 * @return
 */

public TParm getselectJnum(){
	TParm  result=new TParm();
	String sql= "SELECT COUNT(A.CASE_NO)AS J_NUM ,A.CASE_NO,A.DEPT_CODE " +
				"FROM "+tableA+tableC +
				"MED_LIS_ANTITEST C ,"+
				"MED_LIS_MAP D "+
				"WHERE A.CASE_NO=B.CASE_NO "+
				"AND A.DEPT_CODE=B.DEPT_CODE "+
				"AND B.CAT1_TYPE = C.CAT1_TYPE "+
				"AND B.APPLICATION_NO = C.APPLICATION_NO " +
				"AND C.CULTURE_CODE = D.LIS_ID "+
				"AND D.MAP_ID = 'D01' "+
				"GROUP BY A.DEPT_CODE,A.CASE_NO ORDER BY A.DEPT_CODE,A.CASE_NO  ";
	
				
	
	
	//System.out.println("xxxxxxxxxxx"+sql);
	result = new TParm(TJDODBTool.getInstance().select(sql));
	if (result.getErrCode() < 0) {
		err("ERR:M " + result.getErrCode() + result.getErrText()
				+ result.getErrName());
		return result;
	}
	return result;
}
/**
 *³¦Çò¾ú
 * @param parm
 * @return
 */

public TParm getselectCnum(){
	TParm  result=new TParm();
	String sql= "SELECT COUNT(A.CASE_NO)AS 	C_NUM ,A.CASE_NO,A.DEPT_CODE " +
				"FROM "+tableA+tableC +
				"MED_LIS_ANTITEST C ,"+
				"MED_LIS_MAP D "+
				"WHERE A.CASE_NO=B.CASE_NO "+
				"AND A.DEPT_CODE=B.DEPT_CODE "+
				"AND B.CAT1_TYPE = C.CAT1_TYPE "+
				"AND B.APPLICATION_NO = C.APPLICATION_NO " +
				"AND C.CULTURE_CODE = D.LIS_ID "+
				"AND D.MAP_ID = 'D01' "+
				"GROUP BY A.DEPT_CODE,A.CASE_NO ORDER BY A.DEPT_CODE,A.CASE_NO  ";
	
	
	
	//System.out.println("xxxxxxxxxxx"+sql);
	result = new TParm(TJDODBTool.getInstance().select(sql));
	if (result.getErrCode() < 0) {
		err("ERR:M " + result.getErrCode() + result.getErrText()
				+ result.getErrName());
		return result;
	}
	return result;
}
/**
 *·ÎÑ×
 * @param parm
 * @return
 */

public TParm getselectFnum(){
	TParm  result=new TParm();
	String sql= "SELECT COUNT(A.CASE_NO)AS 	F_NUM ,A.CASE_NO,A.DEPT_CODE " +
				"FROM "+tableA+tableC +
				"MED_LIS_ANTITEST C ,"+
				"MED_LIS_MAP D "+
				"WHERE A.CASE_NO=B.CASE_NO "+
				"AND A.DEPT_CODE=B.DEPT_CODE "+
				"AND B.CAT1_TYPE = C.CAT1_TYPE "+
				"AND B.APPLICATION_NO = C.APPLICATION_NO " +
				"AND C.CULTURE_CODE = D.LIS_ID "+
				"AND D.MAP_ID = 'D01' "+
				"GROUP BY A.DEPT_CODE,A.CASE_NO ORDER BY A.DEPT_CODE,A.CASE_NO  ";
							
	
	
	//System.out.println("xxxxxxxxxxx"+sql);
	result = new TParm(TJDODBTool.getInstance().select(sql));
	if (result.getErrCode() < 0) {
		err("ERR:M " + result.getErrCode() + result.getErrText()
				+ result.getErrName());
		return result;
	}
	return result;
}
/**
 * esbls
 * @param parm
 * @return
 */

public TParm getselectESBLsnum(){
	TParm  result=new TParm();
	
	
	
	String sql= "SELECT COUNT(A.CASE_NO)AS 	ESBLS_NUM ,A.CASE_NO,A.DEPT_CODE " +
				"FROM "+tableA+tableC +
				"MED_LIS_ANTITEST C ,"+
				"MED_LIS_MAP D "+
				"WHERE A.CASE_NO=B.CASE_NO "+
				"AND A.DEPT_CODE=B.DEPT_CODE "+
				"AND B.CAT1_TYPE = C.CAT1_TYPE "+
				"AND B.APPLICATION_NO = C.APPLICATION_NO " +
				"AND C.CULTURE_CODE = D.LIS_ID "+
				"AND D.MAP_ID = 'D01' "+
				"GROUP BY A.DEPT_CODE,A.CASE_NO ORDER BY A.DEPT_CODE,A.CASE_NO   ";
	
				
				
				//System.out.println("xxxxxxxxxxx"+sql);
				result = new TParm(TJDODBTool.getInstance().select(sql));
				if (result.getErrCode() < 0) {
					err("ERR:M " + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
				return result;
}
/**
 * ÆäËû
 * @param parm
 * @return
 */

public TParm getselectOthernum(){
	TParm  result=new TParm();
	String sql= "SELECT COUNT(A.CASE_NO)AS 	OTHER_NUM ,A.CASE_NO,A.DEPT_CODE " +
				"FROM "+tableA+tableC +
				"MED_LIS_ANTITEST C ,"+
				"MED_LIS_MAP D "+
				"WHERE A.CASE_NO=B.CASE_NO "+
				"AND A.DEPT_CODE=B.DEPT_CODE "+
				"AND B.CAT1_TYPE = C.CAT1_TYPE "+
				"AND B.APPLICATION_NO = C.APPLICATION_NO " +
				"AND C.CULTURE_CODE = D.LIS_ID "+
				"AND D.MAP_ID = 'D01' "+
				"GROUP BY A.DEPT_CODE,A.CASE_NO ORDER BY A.DEPT_CODE,A.CASE_NO  ";
	

	
	
	//System.out.println("xxxxxxxxxxx"+sql);
	result = new TParm(TJDODBTool.getInstance().select(sql));
	if (result.getErrCode() < 0) {
		err("ERR:M " + result.getErrCode() + result.getErrText()
				+ result.getErrName());
		return result;
	}
	return result;
}
/**
 * ¸ú×Ù·´À¡
 * @param parm
 * @return
 */

public TParm getselectGFnum(){
	TParm  result=new TParm();
	
	String sql= "SELECT COUNT(A.CASE_NO)AS 	GF_NUM ,A.CASE_NO,A.DEPT_CODE " +
				"FROM "+tableA+tableC +
				"MED_LIS_ANTITEST C ,"+
				"MED_LIS_MAP D "+
				"WHERE A.CASE_NO=B.CASE_NO "+
				"AND A.DEPT_CODE=B.DEPT_CODE "+
				"AND B.CAT1_TYPE = C.CAT1_TYPE "+
				"AND B.APPLICATION_NO = C.APPLICATION_NO " +
				"AND C.CULTURE_CODE = D.LIS_ID "+
				"AND D.MAP_ID = 'D01' "+
				"GROUP BY A.DEPT_CODE,A.CASE_NO ORDER BY A.DEPT_CODE,A.CASE_NO  ";
	

	
	
	//System.out.println("xxxxxxxxxxx"+sql);
	result = new TParm(TJDODBTool.getInstance().select(sql));
	if (result.getErrCode() < 0) {
		err("ERR:M " + result.getErrCode() + result.getErrText()
				+ result.getErrName());
		return result;
	}
	return result;
}
/**
 * ¸ú×Ù·´À¡
 * @param parm
 * @return
 *//*

public TParm getselectnewFnum(TParm parm){
	TParm  result=new TParm();
	String sql= " (SELECT CASE_NO,MR_NO,PAT_NAME,DEPT_CODE,MAX(REPORT_DATE) AS REPORT_DATE " +
				" FROM MED_APPLY  " +
				" WHERE REPORT_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
				" AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
				" GROUP BY CASE_NO,MR_NO,PAT_NAME,DEPT_CODE)B ";
				
	
	
	
	
	//System.out.println("xxxxxxxxxxx"+sql);
	result = new TParm(TJDODBTool.getInstance().select(sql));
	if (result.getErrCode() < 0) {
		err("ERR:M " + result.getErrCode() + result.getErrText()
				+ result.getErrName());
		return result;
	}
	return result;
}*/
}
