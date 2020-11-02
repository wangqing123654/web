package jdo.inf;


import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title: 	医院感染病例统计表</p>
 *
 * <p>Description:医院感染病例统计表表</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2014</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author 2014.03.07
 * @version 1.0
 */
public class INFPatStaUITool  extends TJDOTool{
	
	public static INFPatStaUITool instanceObject;
		
		public static INFPatStaUITool getInstance(){
		if(instanceObject==null){
			instanceObject=new INFPatStaUITool();
		}
		   return instanceObject;
		}
		
		/**
		 * 查询住院人数
		 * @param parm
		 * @return
		 */
		public TParm getSelectInpTotnum(TParm parm){
			TParm result=new TParm();
			String sql=" SELECT COUNT(DISTINCT CASE_NO) AS INPTOT_NUM,DEPT_CODE " +
					   " FROM ADM_INP " +
					   " WHERE  DS_DATE IS NULL " +
					   " AND IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		         	   " GROUP BY DEPT_CODE ORDER BY DEPT_CODE"; 
			
			//System.out.println("住总人数:::::"+sql);
			
			
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
			
		}
		/**
		 * 查询出院总人数
		 * @param parm
		 * @return
		 */
		public TParm getSelectOutTotnum(TParm parm){
			TParm result=new TParm();
			String sql=" SELECT COUNT(DISTINCT CASE_NO) AS OUTTOT_NUM,DEPT_CODE " +
					   " FROM ADM_INP " +
					   " WHERE  DS_DATE IS NOT NULL " +
					   " AND DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		         	   " GROUP BY DEPT_CODE ORDER BY DEPT_CODE"; 
			
			//System.out.println("出总人数:::::"+sql);
			
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
			
		}
		/**
		 * 检测病历总数
		 * @param parm
		 * @return
		 */
		public TParm getselectTotnum(TParm parm){
			TParm result = new TParm();
			String sql=" SELECT COUNT(DISTINCT CASE_NO) AS TOT_NUM,DEPT_CODE " +
			           " FROM ADM_INP " +
					   " WHERE  " +
					   " IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		      	       " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       " OR DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       " GROUP BY DEPT_CODE ORDER BY DEPT_CODE"; 
			
			//System.out.println("检测：：：：：："+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		/**
		 * 总感染人数
		 * @param parm
		 * @return
		 */
		public TParm getselectInfnum( TParm parm){
			String deptCode="";
			String rfDr="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				   deptCode=" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
			}
			if(!"".equals(parm.getValue("INF_DR"))&&parm.getValue("INF_DR")!=null){
				   rfDr=" AND INF_DR='"+parm.getValue("INF_DR")+"' ";
			}
			TParm result = new TParm();
			String sql=" SELECT COUNT (DISTINCT A.CASE_NO) AS INF_TONNUM,A.DEPT_CODE FROM (SELECT DISTINCT CASE_NO,DEPT_CODE " +
			           " FROM ADM_INP " +
					   " WHERE  " +
					   " IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		      	       " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       " OR DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       "  ORDER BY DEPT_CODE )A, "+
					   " (SELECT DISTINCT CASE_NO,DEPT_CODE " +
						"FROM INF_CASE  "+
					    "WHERE " +
					    "REGISTER_DATE BETWEEN TO_DATE ('"+parm.getValue("RS_DATE")+"','YYYYMMDDHH24MISS') "+
						"AND TO_DATE ('"+parm.getValue("RE_DATE")+"', 'YYYYMMDDHH24MISS') "+
					     deptCode +rfDr +
						"ORDER BY DEPT_CODE )B WHERE A.CASE_NO=B.CASE_NO AND A.DEPT_CODE=B.DEPT_CODE  GROUP BY A.DEPT_CODE   ORDER BY A.DEPT_CODE ";
				
			//System.out.println("感染总人数:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		/**
		 * 泌尿道染人数
		 * @param parm
		 * @return
		 */
		public TParm getselectInf03num( TParm parm){
			
			String deptCode="";
			String rfDr="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				   deptCode=" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
			}
			if(!"".equals(parm.getValue("INF_DR"))&&parm.getValue("INF_DR")!=null){
				   rfDr=" AND INF_DR='"+parm.getValue("INF_DR")+"' ";
			}
			TParm result = new TParm();
			String sql=" SELECT COUNT (DISTINCT A.CASE_NO) AS INF_03TONNUM,A.DEPT_CODE FROM (SELECT DISTINCT CASE_NO,DEPT_CODE " +
			           " FROM ADM_INP " +
					   " WHERE  " +
					   " IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		      	       " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       " OR DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       "  ORDER BY DEPT_CODE )A, "+
					   " (SELECT DISTINCT CASE_NO,DEPT_CODE " +
						"FROM INF_CASE  "+
					    "WHERE " +
					    " INFPOSITION_CODE='03' "+
					    "AND REGISTER_DATE BETWEEN TO_DATE ('"+parm.getValue("RS_DATE")+"','YYYYMMDDHH24MISS') "+
						"AND TO_DATE ('"+parm.getValue("RE_DATE")+"', 'YYYYMMDDHH24MISS') "+
					     deptCode +rfDr +
						"ORDER BY DEPT_CODE )B WHERE A.CASE_NO=B.CASE_NO AND A.DEPT_CODE=B.DEPT_CODE  GROUP BY A.DEPT_CODE   ORDER BY A.DEPT_CODE ";
			
					
			
			//System.out.println("泌尿道染人数:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		/**
		 * 肠胃染人数
		 * @param parm
		 * @return
		 */
		public TParm getselectInf05num( TParm parm){
			String deptCode="";
			String rfDr="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				   deptCode=" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
			}
			if(!"".equals(parm.getValue("INF_DR"))&&parm.getValue("INF_DR")!=null){
				   rfDr=" AND INF_DR='"+parm.getValue("INF_DR")+"' ";
			}
			TParm result = new TParm();
			String sql=" SELECT COUNT (DISTINCT A.CASE_NO) AS INF_05TONNUM,A.DEPT_CODE FROM (SELECT DISTINCT CASE_NO,DEPT_CODE " +
			           " FROM ADM_INP " +
					   " WHERE  " +
					   " IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		      	       " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       " OR DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       "  ORDER BY DEPT_CODE )A, "+
					   " (SELECT DISTINCT CASE_NO,DEPT_CODE " +
						"FROM INF_CASE  "+
					    "WHERE " +
					    " INFPOSITION_CODE='05' "+
					    "AND REGISTER_DATE BETWEEN TO_DATE ('"+parm.getValue("RS_DATE")+"','YYYYMMDDHH24MISS') "+
						"AND TO_DATE ('"+parm.getValue("RE_DATE")+"', 'YYYYMMDDHH24MISS') "+
					     deptCode +rfDr +
						"ORDER BY DEPT_CODE )B WHERE A.CASE_NO=B.CASE_NO AND A.DEPT_CODE=B.DEPT_CODE  GROUP BY A.DEPT_CODE   ORDER BY A.DEPT_CODE  ";
			
			//System.out.println("肠胃染人数人数:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		/**
		 * 上呼吸道染人数
		 * @param parm
		 * @return
		 */
		public TParm getselectInf01num( TParm parm){
			String deptCode="";
			String rfDr="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				   deptCode=" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
			}
			if(!"".equals(parm.getValue("INF_DR"))&&parm.getValue("INF_DR")!=null){
				   rfDr=" AND INF_DR='"+parm.getValue("INF_DR")+"' ";
			}
			TParm result = new TParm();
			String sql=" SELECT COUNT (DISTINCT A.CASE_NO) AS INF_01TONNUM ,A.DEPT_CODE FROM (SELECT DISTINCT CASE_NO,DEPT_CODE " +
			           " FROM ADM_INP " +
					   " WHERE  " +
					   " IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		      	       " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       " OR DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       "  ORDER BY DEPT_CODE )A, "+
					   " (SELECT DISTINCT CASE_NO,DEPT_CODE " +
						"FROM INF_CASE  "+
					    "WHERE " +
					    " INFPOSITION_CODE='01' "+
					    "AND REGISTER_DATE BETWEEN TO_DATE ('"+parm.getValue("RS_DATE")+"','YYYYMMDDHH24MISS') "+
						"AND TO_DATE ('"+parm.getValue("RE_DATE")+"', 'YYYYMMDDHH24MISS') "+
					     deptCode +rfDr +
						"ORDER BY DEPT_CODE )B WHERE A.CASE_NO=B.CASE_NO AND A.DEPT_CODE=B.DEPT_CODE  GROUP BY A.DEPT_CODE   ORDER BY A.DEPT_CODE ";
			
			//System.out.println("上呼吸道染数人数:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		/**
		 * 下呼吸道染人数
		 * @param parm
		 * @return
		 */
		public TParm getselectInf02num( TParm parm){
			String deptCode="";
			String rfDr="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				   deptCode=" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
			}
			if(!"".equals(parm.getValue("INF_DR"))&&parm.getValue("INF_DR")!=null){
				   rfDr=" AND INF_DR='"+parm.getValue("INF_DR")+"' ";
			}
			TParm result = new TParm();
			String sql=" SELECT COUNT (DISTINCT A.CASE_NO) AS INF_02TONNUM ,A.DEPT_CODE FROM (SELECT DISTINCT CASE_NO,DEPT_CODE " +
			           " FROM ADM_INP " +
					   " WHERE  " +
					   " IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		      	       " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       " OR DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       "  ORDER BY DEPT_CODE )A, "+
					   " (SELECT DISTINCT CASE_NO,DEPT_CODE " +
						"FROM INF_CASE  "+
					    "WHERE " +
					    " INFPOSITION_CODE='02' "+
					    "AND REGISTER_DATE BETWEEN TO_DATE ('"+parm.getValue("RS_DATE")+"','YYYYMMDDHH24MISS') "+
						"AND TO_DATE ('"+parm.getValue("RE_DATE")+"', 'YYYYMMDDHH24MISS') "+
					     deptCode +rfDr +
						"ORDER BY DEPT_CODE )B WHERE A.CASE_NO=B.CASE_NO AND A.DEPT_CODE=B.DEPT_CODE  GROUP BY A.DEPT_CODE   ORDER BY A.DEPT_CODE ";
			
			//System.out.println("下呼吸道染人数:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		/**
		 *  切口感染人数
		 * @param parm
		 * @return
		 */
		public TParm getselectInf04num( TParm parm){
			String deptCode="";
			String rfDr="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				   deptCode=" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
			}
			if(!"".equals(parm.getValue("INF_DR"))&&parm.getValue("INF_DR")!=null){
				   rfDr=" AND INF_DR='"+parm.getValue("INF_DR")+"' ";
			}
			TParm result = new TParm();
			String sql=" SELECT COUNT (DISTINCT A.CASE_NO) AS INF_04TONNUM ,A.DEPT_CODE FROM (SELECT DISTINCT CASE_NO,DEPT_CODE " +
			           " FROM ADM_INP " +
					   " WHERE  " +
					   " IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		      	       " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       " OR DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       "  ORDER BY DEPT_CODE )A, "+
					   " (SELECT DISTINCT CASE_NO,DEPT_CODE " +
						"FROM INF_CASE  "+
					    "WHERE " +
					    " INFPOSITION_CODE='04' "+
					    "AND REGISTER_DATE BETWEEN TO_DATE ('"+parm.getValue("RS_DATE")+"','YYYYMMDDHH24MISS') "+
						"AND TO_DATE ('"+parm.getValue("RE_DATE")+"', 'YYYYMMDDHH24MISS') "+
					     deptCode +rfDr +
						"ORDER BY DEPT_CODE )B WHERE A.CASE_NO=B.CASE_NO AND A.DEPT_CODE=B.DEPT_CODE  GROUP BY A.DEPT_CODE   ORDER BY A.DEPT_CODE ";
			
			//System.out.println("切口感染染人数:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		/**
		 * 血液感染
		 * @return
		 */
		public TParm getselectInf06num( TParm parm){
			String deptCode="";
			String rfDr="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				   deptCode=" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
			}
			if(!"".equals(parm.getValue("INF_DR"))&&parm.getValue("INF_DR")!=null){
				   rfDr=" AND INF_DR='"+parm.getValue("INF_DR")+"' ";
			}
			TParm result = new TParm();
			String sql=" SELECT COUNT (DISTINCT A.CASE_NO) AS INF_06TONNUM,A.DEPT_CODE FROM (SELECT DISTINCT CASE_NO,DEPT_CODE " +
			           " FROM ADM_INP " +
					   " WHERE  " +
					   " IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		      	       " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       " OR DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       "  ORDER BY DEPT_CODE )A, "+
					   " (SELECT DISTINCT CASE_NO,DEPT_CODE " +
						"FROM INF_CASE  "+
					    "WHERE " +
					    " INFPOSITION_CODE='06' "+
					    "AND REGISTER_DATE BETWEEN TO_DATE ('"+parm.getValue("RS_DATE")+"','YYYYMMDDHH24MISS') "+
						"AND TO_DATE ('"+parm.getValue("RE_DATE")+"', 'YYYYMMDDHH24MISS') "+
					     deptCode +rfDr +
						"ORDER BY DEPT_CODE )B WHERE A.CASE_NO=B.CASE_NO AND A.DEPT_CODE=B.DEPT_CODE  GROUP BY A.DEPT_CODE   ORDER BY A.DEPT_CODE ";
			
			
			//System.out.println("血液感染人数:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		/**
		 * 其他感染
		 * @return
		 */
		public TParm getselectInf13num( TParm parm){
			String deptCode="";
			String rfDr="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				   deptCode=" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
			}
			if(!"".equals(parm.getValue("INF_DR"))&&parm.getValue("INF_DR")!=null){
				   rfDr=" AND INF_DR='"+parm.getValue("INF_DR")+"' ";
			}
			TParm result = new TParm();
			String sql=" SELECT COUNT (DISTINCT A.CASE_NO) AS INF_13TONNUM,A.DEPT_CODE FROM (SELECT DISTINCT CASE_NO,DEPT_CODE " +
			           " FROM ADM_INP " +
					   " WHERE  " +
					   " IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		      	       " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       " OR DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       "  ORDER BY DEPT_CODE )A, "+
					   " (SELECT DISTINCT CASE_NO,DEPT_CODE " +
						"FROM INF_CASE  "+
					    "WHERE " +
					    " INFPOSITION_CODE='13' "+
					    "AND REGISTER_DATE BETWEEN TO_DATE ('"+parm.getValue("RS_DATE")+"','YYYYMMDDHH24MISS') "+
						"AND TO_DATE ('"+parm.getValue("RE_DATE")+"', 'YYYYMMDDHH24MISS') "+
					     deptCode +rfDr +
						"ORDER BY DEPT_CODE )B WHERE A.CASE_NO=B.CASE_NO AND A.DEPT_CODE=B.DEPT_CODE  GROUP BY A.DEPT_CODE   ORDER BY A.DEPT_CODE ";
			
			//System.out.println("其他感染人数:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		
		/**
		 * 无影响
		 * @return
		 */
		public TParm getselectInfDIE1num( TParm parm){
			String deptCode="";
			String rfDr="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				   deptCode=" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
			}
			if(!"".equals(parm.getValue("INF_DR"))&&parm.getValue("INF_DR")!=null){
				   rfDr=" AND INF_DR='"+parm.getValue("INF_DR")+"' ";
			}
			TParm result = new TParm();
			String sql=" SELECT COUNT (DISTINCT A.CASE_NO) AS INF_DIE1TONNUM ,A.DEPT_CODE FROM (SELECT DISTINCT CASE_NO,DEPT_CODE " +
			           " FROM ADM_INP " +
					   " WHERE  " +
					   " IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		      	       " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       " OR DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       "  ORDER BY DEPT_CODE )A, "+
					   " (SELECT DISTINCT CASE_NO,DEPT_CODE " +
						"FROM INF_CASE  "+
					    "WHERE " +
					    "DIEINFLU_CODE='1' "+
					    "AND REGISTER_DATE BETWEEN TO_DATE ('"+parm.getValue("RS_DATE")+"','YYYYMMDDHH24MISS') "+
						"AND TO_DATE ('"+parm.getValue("RE_DATE")+"', 'YYYYMMDDHH24MISS') "+
					     deptCode +rfDr +
						"ORDER BY DEPT_CODE )B WHERE A.CASE_NO=B.CASE_NO AND A.DEPT_CODE=B.DEPT_CODE  GROUP BY A.DEPT_CODE   ORDER BY A.DEPT_CODE ";
			
			//System.out.println("无影响人数:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		
		/**
		 * 促进死亡人数
		 * @return
		 */
		public TParm getselectInfD3num( TParm parm){
			String deptCode="";
			String rfDr="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				   deptCode=" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
			}
			if(!"".equals(parm.getValue("INF_DR"))&&parm.getValue("INF_DR")!=null){
				   rfDr=" AND INF_DR='"+parm.getValue("INF_DR")+"' ";
			}
			TParm result = new TParm();
			String sql=" SELECT COUNT (DISTINCT A.CASE_NO) AS INF_DIE3TONNUM,A.DEPT_CODE FROM (SELECT DISTINCT CASE_NO,DEPT_CODE " +
			           " FROM ADM_INP " +
					   " WHERE  " +
					   " IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		      	       " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       " OR DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       "  ORDER BY DEPT_CODE )A, "+
					   " (SELECT DISTINCT CASE_NO,DEPT_CODE " +
						"FROM INF_CASE  "+
					    "WHERE " +
					    "DIEINFLU_CODE='3' "+
					    "AND REGISTER_DATE BETWEEN TO_DATE ('"+parm.getValue("RS_DATE")+"','YYYYMMDDHH24MISS') "+
						"AND TO_DATE ('"+parm.getValue("RE_DATE")+"', 'YYYYMMDDHH24MISS') "+
					     deptCode +rfDr +
						"ORDER BY DEPT_CODE )B WHERE A.CASE_NO=B.CASE_NO AND A.DEPT_CODE=B.DEPT_CODE  GROUP BY A.DEPT_CODE   ORDER BY A.DEPT_CODE ";
			
			//System.out.println("促进死亡人数:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		
		/**
		 * 直接死亡
		 * @return
		 */
		public TParm getselectInfD4num( TParm parm){
			String deptCode="";
			String rfDr="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				   deptCode=" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
			}
			if(!"".equals(parm.getValue("INF_DR"))&&parm.getValue("INF_DR")!=null){
				   rfDr=" AND INF_DR='"+parm.getValue("INF_DR")+"' ";
			}
			TParm result = new TParm();
			String sql=" SELECT COUNT (DISTINCT A.CASE_NO) AS INF_DIE4TONNUM,A.DEPT_CODE FROM (SELECT DISTINCT CASE_NO,DEPT_CODE " +
			           " FROM ADM_INP " +
					   " WHERE  " +
					   " IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		      	       " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       " OR DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       "  ORDER BY DEPT_CODE )A, "+
					   " (SELECT DISTINCT CASE_NO,DEPT_CODE " +
						"FROM INF_CASE  "+
					    "WHERE " +
					    "DIEINFLU_CODE='4' "+
					    "AND REGISTER_DATE BETWEEN TO_DATE ('"+parm.getValue("RS_DATE")+"','YYYYMMDDHH24MISS') "+
						"AND TO_DATE ('"+parm.getValue("RE_DATE")+"', 'YYYYMMDDHH24MISS') "+
					     deptCode +rfDr +
						"ORDER BY DEPT_CODE )B WHERE A.CASE_NO=B.CASE_NO AND A.DEPT_CODE=B.DEPT_CODE  GROUP BY A.DEPT_CODE   ORDER BY A.DEPT_CODE";
			
			//System.out.println("直接死亡:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		/**
		 * 漏报
		 * @return
		 */
		public TParm getselectT5num( TParm parm){
			String deptCode="";
			String rfDr="";
			if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
				deptCode=" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
			}
			if(!"".equals(parm.getValue("INF_DR"))&&parm.getValue("INF_DR")!=null){
				rfDr=" AND INF_DR='"+parm.getValue("INF_DR")+"' ";
			}
			TParm result = new TParm();
			String sql=" SELECT COUNT (DISTINCT A.CASE_NO) AS INF_DIE4TONNUM,A.DEPT_CODE FROM (SELECT DISTINCT CASE_NO,DEPT_CODE " +
			" FROM ADM_INP " +
			" WHERE  " +
			" IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
			" AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
			" OR DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
			" AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
			"  ORDER BY DEPT_CODE )A, "+
			" (SELECT DISTINCT CASE_NO,DEPT_CODE " +
			"FROM INF_CASE  "+
			"WHERE " +
			"REPORT_DATE IS NULL "+
			"AND REGISTER_DATE BETWEEN TO_DATE ('"+parm.getValue("RS_DATE")+"','YYYYMMDDHH24MISS') "+
			"AND TO_DATE ('"+parm.getValue("RE_DATE")+"', 'YYYYMMDDHH24MISS') "+
			deptCode +rfDr +
			"ORDER BY DEPT_CODE )B WHERE A.CASE_NO=B.CASE_NO AND A.DEPT_CODE=B.DEPT_CODE  GROUP BY A.DEPT_CODE   ORDER BY A.DEPT_CODE";
			
			//System.out.println("直接死亡:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		/**
		 * 铜绿
		 * @param parm
		 * @return
		 */
		public TParm getselectTnum(TParm parm){
			TParm result = new TParm();
			
			String sql=" SELECT COUNT (DISTINCT A.CASE_NO) AS T1_NUM,A.DEPT_CODE FROM (SELECT DISTINCT CASE_NO,DEPT_CODE " +
			           " FROM ADM_INP " +
					   " WHERE  " +
					   " IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		      	       " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       " OR DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
		         	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
		      	       "  ORDER BY DEPT_CODE )A, "+
		      	       " (SELECT DISTINCT A.CASE_NO, A.DEPT_CODE " +
		      	       " FROM MED_APPLY A,MED_LIS_ANTITEST B,MED_LIS_MAP C" +
		      	       " WHERE A.CAT1_TYPE=B.CAT1_TYPE "+
		      	       " AND A.APPLICATION_NO=B.APPLICATION_NO "+
		      	       " AND B.CULTURE_CODE=C.LIS_ID "+
		      	       " AND C.MAP_ID='A01' )B "+
		      	       " GROUP BY A.DEPT_CODE   ORDER BY A.DEPT_CODE";
		 			
			//System.out.println("铜绿:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		/**
		 * 金菌
		 * @param parm
		 * @return
		 */
		public TParm getselectJnum(TParm parm){
			TParm result = new TParm();
			
			String sql=" SELECT COUNT (DISTINCT A.CASE_NO) AS T2_NUM,A.DEPT_CODE FROM (SELECT DISTINCT CASE_NO,DEPT_CODE " +
	           " FROM ADM_INP " +
			   " WHERE  " +
			   " IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
	   	       " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
	   	       " OR DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
	      	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
	   	       "  ORDER BY DEPT_CODE )A, "+
	   	       " (SELECT DISTINCT A.CASE_NO, A.DEPT_CODE " +
	   	       " FROM MED_APPLY A,MED_LIS_ANTITEST B,MED_LIS_MAP C" +
	   	       " WHERE A.CAT1_TYPE=B.CAT1_TYPE "+
	   	       " AND A.APPLICATION_NO=B.APPLICATION_NO "+
	   	       " AND B.CULTURE_CODE=C.LIS_ID "+
	   	       " AND C.MAP_ID='B01' )B "+
	   	       " GROUP BY A.DEPT_CODE   ORDER BY A.DEPT_CODE";
			//System.out.println("金菌:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		/**
		 * 霉菌
		 * @param parm
		 * @return
		 */
		public TParm getselectMnum(TParm parm){
			TParm result = new TParm();
			

			String sql=" SELECT COUNT (DISTINCT A.CASE_NO) AS T3_NUM,A.DEPT_CODE FROM (SELECT DISTINCT CASE_NO,DEPT_CODE " +
	           " FROM ADM_INP " +
			   " WHERE  " +
			   " IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
	   	       " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
	   	       " OR DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
	      	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
	   	       "  ORDER BY DEPT_CODE )A, "+
	   	       " (SELECT DISTINCT A.CASE_NO, A.DEPT_CODE " +
	   	       " FROM MED_APPLY A,MED_LIS_ANTITEST B,MED_LIS_MAP C" +
	   	       " WHERE A.CAT1_TYPE=B.CAT1_TYPE "+
	   	       " AND A.APPLICATION_NO=B.APPLICATION_NO "+
	   	       " AND B.CULTURE_CODE=C.LIS_ID "+
	   	       " AND C.MAP_ID='C01' )B "+
	   	       " GROUP BY A.DEPT_CODE   ORDER BY A.DEPT_CODE";
			//System.out.println("霉菌:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		
		/**
		 * 其他
		 * @param parm
		 * @return
		 */
		public TParm getselecOtherTnum(TParm parm){
			TParm result = new TParm();
			

			String sql=" SELECT COUNT (DISTINCT A.CASE_NO) AS T3_NUM,A.DEPT_CODE FROM (SELECT DISTINCT CASE_NO,DEPT_CODE " +
	           " FROM ADM_INP " +
			   " WHERE  " +
			   " IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
	   	       " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
	   	       " OR DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
	      	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
	   	       "  ORDER BY DEPT_CODE )A, "+
	   	       " (SELECT DISTINCT A.CASE_NO, A.DEPT_CODE " +
	   	       " FROM MED_APPLY A,MED_LIS_ANTITEST B,MED_LIS_MAP C" +
	   	       " WHERE A.CAT1_TYPE=B.CAT1_TYPE "+
	   	       " AND A.APPLICATION_NO=B.APPLICATION_NO "+
	   	       " AND B.CULTURE_CODE=C.LIS_ID "+
	   	       " AND C.MAP_ID='D01' )B "+
	   	       " GROUP BY A.DEPT_CODE   ORDER BY A.DEPT_CODE";
			//System.out.println("其他:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
	}
