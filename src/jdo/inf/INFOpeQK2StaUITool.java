package jdo.inf;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class INFOpeQK2StaUITool extends TJDOTool{
	
	public static INFOpeQK2StaUITool instanceObject;
		
		public static INFOpeQK2StaUITool getInstance(){
		if(instanceObject==null){
			instanceObject=new INFOpeQK2StaUITool();
		}
		   return instanceObject;
		}
		
		
public TParm getselectInum(TParm parm){
	TParm  result=new TParm();
	String outDept="";//出院科室
	String mainSugeon="";//手术医师
	String phaRevercode="";//切口类型
	
	//科室
	if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
		outDept=" AND A.OUT_DEPT='"+parm.getValue("DEPT_CODE")+"' ";
	}
	//医生
	if(!"".equals(parm.getValue("OPE_CODE"))&&parm.getValue("OPE_CODE")!=null){
		mainSugeon=" AND A.MAIN_SUGEON='"+parm.getValue("OPE_CODE")+"' ";
	}
	if(!"".equals(parm.getValue("PHA_PREVENCODE"))&& parm.getValue("PHA_PREVENCODE")!=null){
		phaRevercode=" AND E.PHA_PREVENCODE='"+parm.getValue("PHA_PREVENCODE")+"' ";
	}
	String sql="SELECT DISTINCT A.CASE_NO, A.PAT_NAME,A.MR_NO, CASE WHEN A.SEX='1' THEN '男' ELSE '女' END SEX , " +
			           "A.AGE,TO_CHAR(A.OUT_DATE,'yyyy/MM/dd HH:mm:ss') AS OUT_DATE, " +
			           "TO_CHAR(A.IN_DATE,'yyyy/MM/dd HH:mm:ss') AS IN_DATE,A.MAIN_SUGEON, "+
				       "C.ICD_DESC ,B.OP_DESC ,B.ANA_WAY ,A.OUT_DEPT,F.WEIGHT||'kg' WEIGHT, " +
				       "TO_CHAR(A.OP_DATE,'yyyy/MM/dd HH:mm:ss') AS OP_DATE,"  +//D.ORDER_DESC,
				       "TO_CHAR(A.OP_END_DATE,'yyyy/MM/dd HH:mm:ss') AS OP_END_DATE, "+
				       "B.HEALTH_LEVEL AS PHA_PREVENCODE,'' ZRW,'' XZJG "+
				       "FROM " +
				       "MRO_RECORD A, " +
				       "MRO_RECORD_OP B, " +
				       "MRO_RECORD_DIAG C, " +
				       "ADM_INP F "+
				       "WHERE  "+
				       "A.CASE_NO=B.CASE_NO " +
				       "AND A.OUT_DIAG_CODE1=C.ICD_CODE "+
				       "AND A.CASE_NO=C.CASE_NO " +
				       "AND A.CASE_NO=F.CASE_NO " +
				       "AND A.OP_CODE=B.OP_CODE " +
				       "AND A.OUT_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		         	   "AND A.OP_DATE BETWEEN TO_DATE ('"+parm.getValue("OPES_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   "AND TO_DATE ('"+parm.getValue("OPEE_DATE")+"', 'YYYYMMDDHH24MISS') " +
		         	    outDept+mainSugeon+phaRevercode +"ORDER BY A.CASE_NO ";
	          // System.out.println("sql::::::::sssss::"+sql);
	           
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
	        "AND B.ANTIBIOTIC_CODE IS NOT NULL  ORDER BY A.CASE_NO";
	
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
