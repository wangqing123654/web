package jdo.inf;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class INFOpeQKStaUITool extends TJDOTool{
	
	public static INFOpeQKStaUITool instanceObject;
		
		public static INFOpeQKStaUITool getInstance(){
		if(instanceObject==null){
			instanceObject=new INFOpeQKStaUITool();
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
	if(!"".equals(parm.getValue("HEALTH_LEVEL"))&& parm.getValue("HEALTH_LEVEL")!=null){
		phaRevercode=" AND B.HEALTH_LEVEL='"+parm.getValue("HEALTH_LEVEL")+"' ";
	}
	String sql="SELECT  A.PAT_NAME,TO_CHAR(A.OUT_DATE,'yyyy/MM/dd HH:mm:ss') AS OUT_DATE, " +
			           "A.MAIN_SUGEON,A.MR_NO,A.OUT_DEPT, "+
				       "B.OP_DESC,B.HEALTH_LEVEL  "+
				       "FROM " +
				       "MRO_RECORD A, " +
				       "MRO_RECORD_OP B " +
				       "WHERE A.CASE_NO=B.CASE_NO " +
				       "AND A.OP_CODE=B.OP_CODE " +
				       "AND A.OUT_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		         	   "AND A.OP_DATE BETWEEN TO_DATE ('"+parm.getValue("OPES_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   "AND TO_DATE ('"+parm.getValue("OPEE_DATE")+"', 'YYYYMMDDHH24MISS') " +
		         	    outDept+mainSugeon+phaRevercode +"ORDER BY A.CASE_NO ";
	          //System.out.println("sql::::::::sssss::"+sql);
	           
	result = new TParm(TJDODBTool.getInstance().select(sql));
	if (result.getErrCode() < 0) {
		err("ERR:M " + result.getErrCode() + result.getErrText()
				+ result.getErrName());
		return result;
	}
	return result;
}
}
