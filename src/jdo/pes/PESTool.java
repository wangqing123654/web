package jdo.pes;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ��������������
 * </p>
 * 
 * <p>
 * Description: ��������������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp
 * @version 1.0
 */
public class PESTool extends TJDOTool {

	/**
	 * ������
	 */
	public PESTool() {
		setModuleName("pes\\PESModule.x");
		onInit();
	}

	/**
	 * ʵ��
	 */
	private static PESTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PESTool
	 */
	public static PESTool getInstance() {
		if (instanceObject == null)
			instanceObject = new PESTool();
		return instanceObject;
	}

	// /**
	// * �õ����׺�
	// *
	// * @return String
	// */
	// public String getBusinessNo() {
	// return SystemTool.getInstance().getNo("ALL", "EKT", "BUSINESS_NO",
	// "BUSINESS_NO");
	// }

	/**
	 * ��ѯ��ѡ�б�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectRxNo(TParm parm) {
		String whereSql="";
		if(parm.getValue("DEPT_CODE")!=null&&!"".equals(parm.getValue("DEPT_CODE"))){
			whereSql +=" AND A.DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
		}
		if(parm.getValue("DR_CODE")!=null&&!"".equals(parm.getValue("DR_CODE"))){
			whereSql +=" AND A.DR_CODE='"+parm.getValue("DR_CODE")+"' ";
		}
		
		if(parm.getValue("ANTIBIOTIC_CODE")!=null&&!"".equals(parm.getValue("ANTIBIOTIC_CODE"))){
			whereSql +=" AND D.ANTIBIOTIC_CODE='"+parm.getValue("ANTIBIOTIC_CODE")+"' ";
		}
		
		if(parm.getValue("PES_TYPE")!=null&&!"".equals(parm.getValue("PES_TYPE"))){
			whereSql +=" AND D.ANTIBIOTIC_CODE IS NOT NULL ";
		}
		
		if(parm.getValue("ADM_TYPE")!=null&&!"".equals(parm.getValue("ADM_TYPE"))){
			whereSql +=" AND A.ADM_TYPE IN ('O','E') ";
		}
		if(parm.getValue("ADM_TYPE_E")!=null&&!"".equals(parm.getValue("ADM_TYPE_E"))){
			whereSql +=" AND A.ADM_TYPE='E'";
		}
		if(parm.getValue("ADM_TYPE_O")!=null&&!"".equals(parm.getValue("ADM_TYPE_O"))){
			whereSql +=" AND A.ADM_TYPE='O' "; 
		}
		

		String sql = "SELECT COUNT (RX_NO) RX_NO, MR_NO,PAT_NAME,CASE_NO,ADM_DATE, DEPT_CODE, DR_CODE,FLG FROM  " +
				" (SELECT DISTINCT (A.RX_NO||A.PRESRT_NO) RX_NO,C.MR_NO,C.PAT_NAME,A.CASE_NO," +
				" B.ADM_DATE,'N' FLG,A.DEPT_CODE,A.DR_CODE " +
				" FROM OPD_ORDER A, REG_PATADM B,SYS_PATINFO C ,PHA_BASE D " +
				" WHERE A.CASE_NO = B.CASE_NO AND A.RX_TYPE = 1 " +
				" AND A.MR_NO = C.MR_NO AND A.ORDER_CODE = D.ORDER_CODE " +
				whereSql+
				" AND B.ADM_DATE BETWEEN TO_DATE ('"+parm.getValue("START_DATE")+"', 'YYYYMMDDHH24MISS' ) " +
				" AND TO_DATE ('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS' ) " +
				" ) GROUP BY MR_NO, PAT_NAME,CASE_NO, ADM_DATE, DEPT_CODE, DR_CODE,FLG  ORDER BY CASE_NO";
		System.out.print(sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
		
//		TParm result = query("selectRxNo", parm);
//		if (result.getErrCode() < 0) {
//			err("ERR:M " + result.getErrCode() + result.getErrText()
//					+ result.getErrName());
//			return result;
//		}
//		return result;
	}

	/**
	 * ��rx_noȡPES_OPD������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectRxD(TParm parm) {
		TParm result = query("selectRxNoD", parm);
		
		System.out.println("xxxxxxxxxxxxxxxxxxxxxx"+result);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * add caoyong 20140214
	 * ��case_noȡODI_ORDER������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectIpdQ(TParm parm) {
		
		TParm result = query("selectIpdQ", parm);
		////System.out.println("nnnnnnnnnnnnnnnnn"+result);
		
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ��case_noȡ�ó�ȥ�������������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectZD(TParm parm) {
		TParm result = query("selectZD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����PES_OPDM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertPESOPDM(TParm parm, TConnection connection) {
		TParm result = update("insertPESOPDM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ����PES_OPDM
	 * ADD CAOYONG 2014214
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertIPDPESOPDM(TParm parm, TConnection connection) {
		TParm result = update("insertIPDPESOPDM" , parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����PES_OPDD
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertPESOPDD(TParm parm, TConnection connection) {
		TParm result = update("insertPESOPDD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ����PES_IDPOPDD
	 * add caoyong 20140218
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertPESIPDOPDD(TParm parm, TConnection connection) {
		TParm result = update("insertPESIPDOPDD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����PESRESULT
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertPESRESULT(TParm parm) {
		TParm result = update("insertPESRESULT", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ����PESRESULT
	 * add caoyong 20140218
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertIpdPESRESULT(TParm parm) {
		TParm result = update("insertIpdPESRESULT", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯOPDM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOPDM(TParm parm) {
		TParm result = query("selectOPDM", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	/**
	 * ��ѯPES_IPDOPDM
	 * ADD CAOYONG 20130219
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectIPDOPDM(TParm parm) {
		TParm result = query("selectIPDOPDM", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯOPDD
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOPDD(TParm parm) {
		TParm result = query("selectOPDD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ��ѯPES_IPDOPDD
	 * add caoyong 20140220
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectIPDOPDD(TParm parm) {
		TParm result = query("selectIPDOPDD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����PES_OPDM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatePESOPDM(TParm parm, TConnection connection) {
		TParm result = update("updatePESOPDM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ����PES_IPDOPDM
	 * add caoyong20140220
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatePESIPDOPDM(TParm parm, TConnection connection) {
		TParm result = update("updatePESIPDOPDM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����PES_OPDD
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatePESOPDD(TParm parm, TConnection connection) {
		TParm result = update("updatePESOPDD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ����PES_IDPOPDD
	 * add caoyong20140220
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatePESIDPOPDD(TParm parm, TConnection connection) {
		TParm result = update("updatePESIDPOPDD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����PESResult
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatePESResult(TParm parm) {
		TParm result = update("updatePESResult", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ����PESResult
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatePESIPDResult(TParm parm) {
		TParm result = update("updatePESIPDResult", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯOPDM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOPDMforPrint(TParm parm) {
		TParm result = query("selectOPDMforPrint", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯPESResult
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectPESResult(TParm parm) {
		TParm result = query("selectPESResult", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ��ѯסԺ
	 * add caoyong 20140213
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	
	public TParm selectinpNo(TParm parm) {
		//TParm result = query("selectinpNo", parm);
		String deptCode ="";
		String drCode="";
		String antType="";
		if(parm.getValue("DEPT_CODE")!=null&&!"".equals(parm.getValue("DEPT_CODE"))){
			deptCode=" AND C.DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
		}
		if(parm.getValue("DR_CODE")!=null&&!"".equals(parm.getValue("DR_CODE"))){
			drCode=" AND A.VS_DR_CODE='"+parm.getValue("DR_CODE")+"' ";
		}
		
		if (!"".equals(parm.getValue("ANTIBIOTIC_CODE"))&&parm.getValue("ANTIBIOTIC_CODE")!=null) {
			  antType="AND A.ANTIBIOTIC_CODE='"+parm.getValue("ANTIBIOTIC_CODE")+"' ";
		}
		String sql="SELECT DISTINCT A.CASE_NO,A.IPD_NO, B.MR_NO,'N' FLG," +
	               "A.IN_DATE, "+
			       "A.VS_DR_CODE,A.DEPT_CODE, "+
			       "D.PAT_NAME "+
			       "FROM ADM_INP A,OPE_OPBOOK B,ODI_ORDER C,SYS_PATINFO D "+
			       "WHERE A.CASE_NO = B.CASE_NO "+
			       "AND A.CASE_NO = C.CASE_NO "+
			       "AND A.MR_NO = D.MR_NO "+
			       "AND A.DS_DATE IS NOT NULL  "+
			       "AND C.RX_KIND='ST' "+
			       deptCode+drCode+antType +
			       "AND A.DS_DATE BETWEEN TO_DATE ('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') "+
		   		   "AND TO_DATE ('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS') ORDER BY IN_DATE";
		System.out.print(sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ����PES_OPDD
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertIpdPESOPDD(TParm parm, TConnection connection) {
		TParm result = update("insertIpdPESOPDD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ADD caoyong 20140217
	 * 
	 * @param parm
	 * @param table
	 * @return
	 */
	
	public TParm selectMaxsql(TParm parm,String table) {
		String SQL ="SELECT MAX(SEQ) AS SEQ FROM "+ table  +
					" WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' " +
				    " AND PES_NO='"+parm.getValue("PES_NO")+"' " +
				    " AND TYPE_CODE='"+parm.getValue("TYPE_CODE")+"' ";
		
		
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL));
		
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ADD caoyong 20140217
	 * 
	 * @param parm
	 * @param table
	 * @return
	 */
	public TParm selectMaxRsql(TParm parm,String table) {
		String SQL ="SELECT MAX(SEQ) AS SEQ FROM "+ table  +
					" WHERE  PES_NO='"+parm.getValue("PES_NO")+"' " +
				    " AND  TYPE_CODE='"+parm.getValue("TYPE_CODE")+"' ";
		
		
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL));
		
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/*public TParm selectMaxOpddSql(TParm parmD,TParm parm, int i,String table) {
		String SQL ="SELECT MAX(SEQ) AS SEQ FROM "+ table  +
					" WHERE CASE_NO='"+parmD.getValue("CASE_NO",i)+"' " +
					" AND  PES_NO='"+parm.getValue("PES_NO")+"' " +
		            " AND  ORDER_NO='"+parmD.getValue("ORDER_NO",i)+"' "+
					" AND  ORDER_SEQ='"+parmD.getValue("ORDER_SEQ",i)+"' ";
		
		
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL));
		
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}*/
}
