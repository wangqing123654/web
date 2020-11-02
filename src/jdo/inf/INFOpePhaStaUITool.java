package jdo.inf;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title: 	围手术期抗菌药物使用评价表</p>
 *
 * <p>Description:围手术期抗菌药物使用评价表</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2014</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author 2014.03.07
 * @version 1.0
 */
public class INFOpePhaStaUITool extends TJDOTool{
	
	public static INFOpePhaStaUITool instanceObject;
		
		public static INFOpePhaStaUITool getInstance(){
		if(instanceObject==null){
			instanceObject=new INFOpePhaStaUITool();
		}
		   return instanceObject;
		}
		
		
public TParm getselectInum(TParm parm){
	TParm  result=new TParm();
	String outDept="";//出院科室
	String mainSugeon="";//手术医师
	
	//科室
	if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
		outDept=" AND A.OUT_DEPT='"+parm.getValue("DEPT_CODE")+"' ";
	}
	//医生
	if(!"".equals(parm.getValue("OPE_CODE"))&&parm.getValue("OPE_CODE")!=null){
		mainSugeon=" AND A.MAIN_SUGEON='"+parm.getValue("OPE_CODE")+"' ";
	}
	String sql="SELECT  A.CASE_NO, A.PAT_NAME,A.MR_NO, CASE WHEN A.SEX='1' THEN '男' ELSE '女' END SEX , " +
			           "A.AGE,A.MAIN_SUGEON, "+
				       "B.OP_DESC ,B.ANA_WAY ,A.OUT_DEPT, " +
				       "TO_CHAR(A.OP_DATE,'yyyy/MM/dd HH:mm:ss') AS OP_DATE,"  +
				       "TO_CHAR(A.OP_END_DATE,'yyyy/MM/dd HH:mm:ss') AS OP_END_DATE, "+
				       "B.HEALTH_LEVEL,'' NOT_USE,'' ANT_BEF0RE,'' ANT_CENTER,'' ANT_AFTER "+
				       "FROM " +
				       "MRO_RECORD A, " +
				       "MRO_RECORD_OP B " +
				       "WHERE  "+
				       "A.CASE_NO=B.CASE_NO " +
				       "AND A.OP_CODE=B.OP_CODE " +
				       "AND A.OUT_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		         	   "AND A.OP_DATE BETWEEN TO_DATE ('"+parm.getValue("OPES_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   "AND TO_DATE ('"+parm.getValue("OPEE_DATE")+"', 'YYYYMMDDHH24MISS') " +
		         	    outDept+mainSugeon +"ORDER BY A.OUT_DEPT, A.CASE_NO ";
	         //System.out.println("sql::::::::sssss::"+sql);
	           
	result = new TParm(TJDODBTool.getInstance().select(sql));
	if (result.getErrCode() < 0) {
		err("ERR:M " + result.getErrCode() + result.getErrText()
				+ result.getErrName());
		return result;
	}
	return result;
}
/**
 * 抗菌药品
 * @param parm
 * @return
 */

public TParm getselectAntnum(String caseno,TParm parm){
	TParm  result=new TParm();
	String sql="SELECT  DISTINCT B.ORDER_CODE,B.ORDER_DESC " +
				"FROM MRO_RECORD A,ODI_ORDER B " +
				"WHERE A.OUT_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
			    "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
			    "AND A.OP_DATE BETWEEN TO_DATE ('"+parm.getValue("OPES_DATE")+"','YYYYMMDDHH24MISS') "+
	      	    "AND TO_DATE ('"+parm.getValue("OPEE_DATE")+"', 'YYYYMMDDHH24MISS') " +
			    "AND A.CASE_NO='"+caseno+"' "+
				"AND A.CASE_NO=B.CASE_NO "+
		        "AND B.ANTIBIOTIC_CODE IS NOT NULL ";
	
	//System.out.println("xxxxxxxxxxx"+sql);
	result = new TParm(TJDODBTool.getInstance().select(sql));
	if (result.getErrCode() < 0) {
		err("ERR:M " + result.getErrCode() + result.getErrText()
				+ result.getErrName());
		return result;
	}
	return result;
}

}
