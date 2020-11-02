package jdo.inf;


import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class INFAntPhaStaUITool  extends TJDOTool{
	
	public static INFAntPhaStaUITool instanceObject;
		
		public static INFAntPhaStaUITool getInstance(){
		if(instanceObject==null){
			instanceObject=new INFAntPhaStaUITool();
		}
		   return instanceObject;
		}
		
		/**
		 * 查询全部出院人员
		 * @param parm
		 * @return
		 */
		public TParm getSelectTotnum(TParm parm){
			TParm result=new TParm();
			String deptCode="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				deptCode=" AND OUT_DEPT='"+parm.getValue("DEPT_CODE")+"' ";
			}
			String sql="SELECT COUNT(DISTINCT CASE_NO) AS TOT_NUM,OUT_DEPT " +
					   "FROM MRO_RECORD  " +
					   " WHERE  OUT_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		         	    deptCode+
		         	   " GROUP BY OUT_DEPT ";//ORDER BY OUT_DEPT"; 
			
			//System.out.println("出院总人数:::::"+sql);
			
			
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
			
		}
		/**
		 * 查询使用抗菌药物人数
		 * @param parm
		 * @return
		 */
		public TParm getselectAntnum(TParm parm){
			TParm result = new TParm();
			String deptCode="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				deptCode=" AND A.OUT_DEPT='"+parm.getValue("DEPT_CODE")+"' ";
			}
			
			String sql=  "SELECT COUNT (DISTINCT A.CASE_NO ) AS ANTI_NUM , " +
			             "OUT_DEPT "+
					     "FROM MRO_RECORD A ,ODI_ORDER B "+
					     "WHERE A.CASE_NO=B.CASE_NO  " +
					     "AND B.ANTIBIOTIC_CODE IS NOT NULL "+
					     "AND B.ORDER_CAT1_CODE='PHA_W' "+
					     deptCode +
		                 "AND A.OUT_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         		 "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		         		 "GROUP BY A.OUT_DEPT  ";//ORDER BY OUT_DEPT";
			
			//System.out.println("抗菌药人数:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		/**
		 * 查询治疗抗菌药物人数
		 * @param parm
		 * @return
		 */
		public TParm getselectWayTwonum(TParm parm){
			TParm result = new TParm();
			String deptCode="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				deptCode=" AND A.OUT_DEPT='"+parm.getValue("DEPT_CODE")+"' ";
			}
			
			String sql=  "SELECT COUNT (DISTINCT A.CASE_NO ) AS WAYII_NUM , " +
			"OUT_DEPT "+
			"FROM MRO_RECORD A ,ODI_ORDER B "+
			"WHERE A.CASE_NO=B.CASE_NO  " +
			"AND B.ANTIBIOTIC_CODE IS NOT NULL "+
			"AND B.ORDER_CAT1_CODE='PHA_W' "+
			"AND B.ANTIBIOTIC_WAY='02' "+
			deptCode +
			"AND A.OUT_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
			"AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
			"GROUP BY A.OUT_DEPT ";// ORDER BY A.OUT_DEPT";
			//System.out.println("治疗抗菌药物人数:::::"+sql);
			
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		/**
		 * 查询预防抗菌药物人数
		 * @param parm
		 * @return
		 */
		public TParm getselectWayOnenum(TParm parm){
			TParm result = new TParm();
			String deptCode="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				deptCode=" AND A.OUT_DEPT='"+parm.getValue("DEPT_CODE")+"' ";
			}
			
			String sql=  "SELECT COUNT (DISTINCT A.CASE_NO ) AS WAYI_NUM , " +
			"OUT_DEPT "+
			"FROM MRO_RECORD A ,ODI_ORDER B "+
			"WHERE A.CASE_NO=B.CASE_NO  " +
			"AND B.ANTIBIOTIC_CODE IS NOT NULL "+
			"AND B.ORDER_CAT1_CODE='PHA_W' "+
			"AND B.ANTIBIOTIC_WAY='01' "+
			deptCode +
			"AND A.OUT_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
			"AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
			"GROUP BY A.OUT_DEPT  ";//ORDER BY A.OUT_DEPT";
			
			//System.out.println("预防抗菌药物:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		/**
		 * 查询药敏实人数
		 * @param parm
		 * @return
		 */
		public TParm getselectMednum(TParm parm){
			TParm result = new TParm();
			String deptCode="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				deptCode=" AND A.OUT_DEPT='"+parm.getValue("DEPT_CODE")+"' ";
			}
			
			String sql=  "SELECT COUNT (DISTINCT A.CASE_NO ) AS MED_NUM , " +
			"OUT_DEPT "+
			"FROM MRO_RECORD A ,ODI_ORDER B,SYS_FEE C "+
			"WHERE A.CASE_NO=B.CASE_NO  " +
			"AND B.ORDER_CODE=C.ORDER_CODE "+
			"AND B.ANTIBIOTIC_CODE IS NOT NULL "+
			"AND B.ORDER_CAT1_CODE='PHA_W' "+
			"AND B.ANTIBIOTIC_WAY='01' "+
			"AND C.MEDANT_FLG='Y'  "+
			deptCode +
			"AND A.OUT_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
			"AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
			"GROUP BY A.OUT_DEPT  ";//ORDER BY A.OUT_DEPT";
			//System.out.println("查询药敏实人数:::::"+sql);
			
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		/**
		 * 预防+治疗
		 * @param parm
		 * @return
		 */
public TParm getaddResult(TParm parm){
			
			
			TParm result = new TParm();
			String deptCode="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				deptCode=" AND A.OUT_DEPT='"+parm.getValue("DEPT_CODE")+"' ";
			}
			
			String sql=" SELECT COUNT (DISTINCT A.CASE_NO) AS TOT_ANTNUM ,A.OUT_DEPT FROM  (SELECT A.CASE_NO,A.OUT_DEPT FROM "+
			            "MRO_RECORD A,ODI_ORDER B "+
			            "WHERE "+
			            "A.OUT_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
						"AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+deptCode +
			            "AND  A.CASE_NO=B.CASE_NO "+
			            "AND A.OUT_DEPT=B.DEPT_CODE "+
			            "AND B.ANTIBIOTIC_CODE IS NOT NULL "+
			            "AND B.ANTIBIOTIC_WAY='02' "+
			            "ORDER BY A.OUT_DEPT ,B.CASE_NO )A, "+
			            "(SELECT DISTINCT A.CASE_NO,A.OUT_DEPT FROM "+
			            "MRO_RECORD A,ODI_ORDER B "+
			            "WHERE "+
			            "A.OUT_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
						 "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+deptCode +
			            "AND A.CASE_NO=B.CASE_NO "+
			            "AND A.OUT_DEPT=B.DEPT_CODE "+
			            "AND B.ANTIBIOTIC_CODE IS NOT NULL "+
			            "AND B.ANTIBIOTIC_WAY='01' ORDER BY A.OUT_DEPT ,B.CASE_NO )B "+
			            "WHERE A.CASE_NO=B.CASE_NO AND A.OUT_DEPT=B.OUT_DEPT "+
			            "GROUP BY A.OUT_DEPT ";//ORDER BY A.OUT_DEPT ";
			//System.out.println("swlllll"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
	}
