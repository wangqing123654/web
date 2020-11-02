package jdo.bil;


import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title:住院病人费用表</p>
 *
 * <p>Description:住院病人费用表</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2014</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author 2014.04.22
 * @version 1.0
 */
public class BILInCostTool extends TJDOTool{
	public static BILInCostTool instanceObject;
	
	public static BILInCostTool getInstance(){
	if(instanceObject==null){
		instanceObject=new BILInCostTool();
	}
	   return instanceObject;
	}
	//上次未结算总计 金额
	public TParm getSelectSordd(TParm parm){
		TParm  result=new TParm();
		 String sql="SELECT SUM (A.TOT_AMT) AS STOT_AMT ,A.REXP_CODE "+
			        "FROM IBS_ORDD A ,ADM_INP B "+
			        "WHERE  A.BILL_DATE < TO_DATE('"+parm.getValue("INS_DATE")+"','YYYYMMDDHH24MISS')"+
					"AND B.DS_DATE IS  NULL " +
					"AND A.CASE_NO=B.CASE_NO " +
					"GROUP BY A.REXP_CODE ";
 //  System.out.println("sql11::"+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	//上次未结算未作废的金额
	public TParm getSelectSrecp(TParm parm){
		TParm  result=new TParm();
		 String sql= "SELECT SUM(B.WRT_OFF_AMT) AS SW_AMT,B.REXP_CODE FROM BIL_IBS_RECPM A,BIL_IBS_RECPD B,ADM_INP C "+
					 "WHERE B.OPT_DATE  < TO_DATE('"+parm.getValue("INS_DATE")+"','YYYYMMDDHH24MISS') "+
					 "AND A.RECEIPT_NO=B.RECEIPT_NO "+
					 "AND A.CASE_NO=C.CASE_NO "+
					 "AND A.RESET_RECEIPT_NO IS NULL "+
					 "AND A.AR_AMT>0 "+
					 "AND C.DS_DATE IS NULL "+
					 "GROUP BY b.REXP_CODE ";
		// System.out.println("sq22222::"+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	//日应收总计
	public TParm getSelectDSordd(TParm parm){
		TParm  result=new TParm();
		 String sql="SELECT SUM (A.TOT_AMT) AS STOT_AMT ,A.REXP_CODE "+
			        "FROM IBS_ORDD A ,ADM_INP B "+
			        "WHERE  A.BILL_DATE BETWEEN TO_DATE ('"+parm.getValue("INS_DATE")+"','YYYYMMDDHH24MISS') "+
		         	 "AND TO_DATE ('"+parm.getValue("INE_DATE")+"', 'YYYYMMDDHH24MISS') "+
					"AND B.DS_DATE IS  NULL " +
					"AND A.CASE_NO=B.CASE_NO " +
					"GROUP BY A.REXP_CODE ";
 //  System.out.println("SQL33333::"+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	//日应收未作废的金额
	public TParm getSelectDSrecp(TParm parm){
		TParm  result=new TParm();
		 String sql= "SELECT SUM(B.WRT_OFF_AMT) AS SW_AMT,B.REXP_CODE FROM BIL_IBS_RECPM A,BIL_IBS_RECPD B,ADM_INP C "+
					 "WHERE B.OPT_DATE  BETWEEN TO_DATE ('"+parm.getValue("INS_DATE")+"','YYYYMMDDHH24MISS') "+
					 "AND TO_DATE ('"+parm.getValue("INE_DATE")+"', 'YYYYMMDDHH24MISS') "+
					 "AND A.RECEIPT_NO=B.RECEIPT_NO "+
					 "AND A.CASE_NO=C.CASE_NO "+
					 "AND A.RESET_RECEIPT_NO IS NULL "+
					 "AND A.AR_AMT>0 "+
					 "AND C.DS_DATE IS NULL "+
					 "GROUP BY B.REXP_CODE ";
		 
		// System.out.println("SQL444444::"+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	//日出院费用总计
	public TParm getSelectFSordd(TParm parm){
		TParm  result=new TParm();
		String sql= "SELECT SUM (A.TOT_AMT) AS STOT_AMT ,A.REXP_CODE "+
					"FROM IBS_ORDD A ,ADM_INP B "+
					"WHERE  A.BILL_DATE BETWEEN TO_DATE ('"+parm.getValue("INS_DATE")+"','YYYYMMDDHH24MISS') "+
					"AND TO_DATE ('"+parm.getValue("INE_DATE")+"', 'YYYYMMDDHH24MISS') "+
					"AND B.DS_DATE IS NOT NULL " +
					"AND A.CASE_NO=B.CASE_NO " +
					"GROUP BY A.REXP_CODE ";
		//System.out.println("SQL55555555555555::"+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	//日出院未作废的金额
	public TParm getSelectDFrecp(TParm parm){
		TParm  result=new TParm();
		 String sql= "SELECT SUM(B.WRT_OFF_AMT) AS SW_AMT,B.REXP_CODE FROM BIL_IBS_RECPM A,BIL_IBS_RECPD B,ADM_INP C "+
					 "WHERE B.OPT_DATE  BETWEEN TO_DATE ('"+parm.getValue("INS_DATE")+"','YYYYMMDDHH24MISS') "+
					 "AND TO_DATE ('"+parm.getValue("INE_DATE")+"', 'YYYYMMDDHH24MISS') "+
					 "AND A.RECEIPT_NO=B.RECEIPT_NO "+
					 "AND A.CASE_NO=C.CASE_NO "+
					 "AND A.RESET_RECEIPT_NO IS NULL "+
					 "AND A.AR_AMT>0 "+
					 "AND C.DS_DATE IS NOT NULL "+
					 "GROUP BY B.REXP_CODE ";
		//System.out.println("SQL6666666666666666::"+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
