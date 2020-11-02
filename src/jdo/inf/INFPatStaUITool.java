package jdo.inf;


import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title: 	ҽԺ��Ⱦ����ͳ�Ʊ�</p>
 *
 * <p>Description:ҽԺ��Ⱦ����ͳ�Ʊ��</p>
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
		 * ��ѯסԺ����
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
			
			//System.out.println("ס������:::::"+sql);
			
			
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
			
		}
		/**
		 * ��ѯ��Ժ������
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
			
			//System.out.println("��������:::::"+sql);
			
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
			
		}
		/**
		 * ��ⲡ������
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
			
			//System.out.println("��⣺����������"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		/**
		 * �ܸ�Ⱦ����
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
				
			//System.out.println("��Ⱦ������:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		/**
		 * �����Ⱦ����
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
			
					
			
			//System.out.println("�����Ⱦ����:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		/**
		 * ��θȾ����
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
			
			//System.out.println("��θȾ��������:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		/**
		 * �Ϻ�����Ⱦ����
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
			
			//System.out.println("�Ϻ�����Ⱦ������:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		/**
		 * �º�����Ⱦ����
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
			
			//System.out.println("�º�����Ⱦ����:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		/**
		 *  �пڸ�Ⱦ����
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
			
			//System.out.println("�пڸ�ȾȾ����:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		/**
		 * ѪҺ��Ⱦ
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
			
			
			//System.out.println("ѪҺ��Ⱦ����:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		/**
		 * ������Ⱦ
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
			
			//System.out.println("������Ⱦ����:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		
		/**
		 * ��Ӱ��
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
			
			//System.out.println("��Ӱ������:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		
		/**
		 * �ٽ���������
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
			
			//System.out.println("�ٽ���������:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		
		/**
		 * ֱ������
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
			
			//System.out.println("ֱ������:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		/**
		 * ©��
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
			
			//System.out.println("ֱ������:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		/**
		 * ͭ��
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
		 			
			//System.out.println("ͭ��:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		/**
		 * ���
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
			//System.out.println("���:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		/**
		 * ù��
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
			//System.out.println("ù��:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
		
		/**
		 * ����
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
			//System.out.println("����:::::"+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		}
		
	}
