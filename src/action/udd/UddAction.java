package action.udd;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jdo.ibs.IBSTool;
import jdo.ind.INDSQL;
import jdo.ind.INDTool;
import jdo.sys.SystemTool;
//import jdo.udd.UDDIvaTool;
import jdo.udd.UddChnCheckTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import action.udd.client.SpcIndStock;
import action.udd.client.SpcOdiDspnd;
import action.udd.client.SpcOdiDspnm;
import action.udd.client.SpcOdiDspnms;
import action.udd.client.SpcOdiService_SpcOdiServiceImplPort_Client;

/**
 * 
 * <p>
 * Title: סԺҩ����ҩ��˶�����
 * </p>
 * 
 * <p>
 * Description: סԺҩ����ҩ��˶�����
 * </p>
 *  
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:javahis
 * </p>
 * 
 * @author ehui 20090307
 * @version 1.0
 */
public class UddAction extends TAction {
	// ����סԺ�Ƽ۽ӿ�д������������
	private static final String IBS_DATA_TYPE = "2";

	/**
	 * ��˱���
	 * 
	 * @param parm
	 *            TParm
	 * @return
	 */
	public TParm onUpdateCheck(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		int count = parm.getCount("CASE_NO");
		if (count < 1) {
			return result;
		}
		for (int i = 0; i < count; i++) {
			TParm parmRow = parm.getRow(i);
			result = UddChnCheckTool.getInstance().onUpdateOCheck(parmRow,
					connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
			result = UddChnCheckTool.getInstance().onUpdateMCheck(parmRow,
					connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ȡ�����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onUpdateUnCheck(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		int count = parm.getCount("CASE_NO");
		if (count < 1) {
			return result;
		}
		for (int i = 0; i < count; i++) {
			TParm parmRow = parm.getRow(i);
			result = UddChnCheckTool.getInstance().onUpdateOUnCheck(parmRow,
					connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
			result = UddChnCheckTool.getInstance().onUpdateMUnCheck(parmRow,
					connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
			
			//20150413 wangjingchun add start
			String sql = "SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,"
						+ " A.START_DTTM,A.DSPN_KIND,B.ORDER_DATE,B.ORDER_DATETIME,'"
						+parmRow.getValue("PN_FLG")
						+ "' AS PN_FLG "
						+ " FROM ODI_DSPNM A,ODI_DSPND B WHERE A.CASE_NO = '"
						+parmRow.getValue("CASE_NO")
						+ "' AND A.ORDER_NO='"
						+parmRow.getValue("ORDER_NO")
						+ "' AND A.ORDER_SEQ='"
						+parmRow.getValue("ORDER_SEQ")
						+ "' AND A.DSPN_KIND='"
						+parmRow.getValue("RX_KIND")
						+ "' AND A.CASE_NO=B.CASE_NO "
						+ " AND A.ORDER_NO=B.ORDER_NO "
						+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
						+ " AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN "
						+ " A.START_DTTM AND  A.END_DTTM ";
//				System.out.println("sql====="+sql);
			TParm updateParm = new TParm(TJDODBTool.getInstance().select(sql));
			
			for(int j=0;j<updateParm.getCount("CASE_NO");j++){
				if(updateParm.getValue("DSPN_KIND", j).equals("ST") 
						|| updateParm.getValue("DSPN_KIND", j).equals("F")){
//					System.out.println("-----"+updateParm.getRow(j));
					result = UddChnCheckTool.getInstance().onCancelIvaDSTFMes(updateParm.getRow(j),
							connection);
					if (result.getErrCode() < 0) {
						connection.close();
						return result;
					}
				}
			}
			//20150413 wangjingchun add end
		}
		connection.commit();
		connection.close();
		return result;

	}

	/**
	 * ��ҩ����
	 * 
	 * @param parm
	 *            TParm
	 * @return
	 */
	public TParm onUpdateDispense(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			TParm parmRow = parm.getRow(i);
			result = UddChnCheckTool.getInstance().onUpdateMDispense(parmRow,
					connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;

	}

	/**
	 * ��ҩ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onUpdateMDosage(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			TParm parmRow = parm.getRow(i);
			result = UddChnCheckTool.getInstance().onUpdateMDosage(parmRow,
					connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;

	}

	/**
	 * ��ҩ��˸���
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onUpdateMedCheck(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		// System.out.println("count->---------------"+parm.getCount("CASE_NO"));
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			TParm parmRow = parm.getRow(i);
			// System.out.println("parmRow->"+parmRow);
			result = UddChnCheckTool.getInstance().onUpdateMedODosage(parmRow,
					connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
			parmRow = parm.getRow(i);
			// System.out.println("UddAction->onUpdateMedCheck->parmRow"+parmRow);
			result = UddChnCheckTool.getInstance().onUpdateMCheck(parmRow,
					connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
			
			//20150413 wangjingchun add start
			String sql = "SELECT A.CASE_NO, A.ORDER_NO, A.ORDER_SEQ,A.ROUTE_CODE,"
						+ " A.START_DTTM,A.DSPN_KIND,B.ORDER_DATE,B.ORDER_DATETIME,'"
						+parmRow.getValue("PN_FLG")
						+ "' AS PN_FLG "
						+ " FROM ODI_DSPNM A,ODI_DSPND B WHERE A.CASE_NO = '"
						+parmRow.getValue("CASE_NO")
						+ "' AND A.ORDER_NO='"
						+parmRow.getValue("ORDER_NO")
						+ "' AND A.ORDER_SEQ='"
						+parmRow.getValue("ORDER_SEQ")
						+ "' AND A.DSPN_KIND='"
						//alert by wukai on 20170323 start �������޷��������
						+ ("UD".equals(parmRow.getValue("RX_KIND")) ? "F" : parmRow.getValue("RX_KIND"))
						//alert by wukai on 20170323 end �������޷��������
						+ "' AND A.CASE_NO=B.CASE_NO "
						+ " AND A.ORDER_NO=B.ORDER_NO "
						+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
						+ " AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN "
						+ " A.START_DTTM AND  A.END_DTTM ";
				System.out.println("sql====="+sql);
			TParm updateParm = new TParm(TJDODBTool.getInstance().select(sql));
			
			for(int j=0;j<updateParm.getCount("CASE_NO");j++){
				if(updateParm.getValue("DSPN_KIND", j).equals("ST") 
						|| updateParm.getValue("DSPN_KIND", j).equals("F") 
						|| (updateParm.getValue("DSPN_KIND", j).equals("OP")
								&& updateParm.getValue("ROUTE_CODE", j).equals("CA"))){
//					System.out.println("-----"+updateParm.getRow(j));
					result = UddChnCheckTool.getInstance().onUpdateIvaDSTFMes(updateParm.getRow(j),
							connection);
					if (result.getErrCode() < 0) {
						connection.close();
						return result;
					}
					result = UddChnCheckTool.getInstance().onUpdateIvaMMes(updateParm.getRow(j),
							connection);
					if (result.getErrCode() < 0) {
						connection.close();
						return result;
					}
				}
			}
			//20150413 wangjingchun add end
		}
		connection.commit();
		connection.close();
		return result;

	}

	/**
	 * ��ҩ�䷢����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onUpdateMedDispense(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		Timestamp now = SystemTool.getInstance().getDate();
		// SHIBL 20120403 ADD (�޸�ʱ�䲻ͳһ)
		for (int j = 0; j < parm.getCount(); j++) {
			parm.addData("OPT_DATE", now);
		}
		// System.out.println("onUpdateMedDosage.count->---------------"+parm.getCount("CASE_NO"));
		// System.out.println("out parm============"+parm);
		// �ۿ�ӿ�
		int count = parm.getCount("OPT_TERM");
		String charge = parm.getValue("CHARGE");
		if (!"DISPENSE".equalsIgnoreCase(charge)) {
			for (int i = 0; i < count; i++) {
				TParm parmRow = parm.getRow(i);
				result = UddChnCheckTool.getInstance()
						.onUpdateUnChargeMedDDispense(parmRow, connection);
				if (result.getErrCode() < 0) {
					connection.close();
					return result;
				}
				result = UddChnCheckTool.getInstance()
						.onUpdateUnChargeMedMDispense(parmRow, connection);
				if (result.getErrCode() < 0) {
					connection.close();
					return result;
				}
			}
			connection.commit();
			connection.close();
			return result;
		}

		TParm lackParm = new TParm();
		Map pat = UddChnCheckTool.getInstance().groupByPatParm(parm);
		Iterator it = pat.values().iterator();
		while (it.hasNext()) {
			TParm patParm = (TParm) it.next();
			// �ۿ�ӿ�
			TParm dosageParm = UddChnCheckTool.getInstance().groupByStockParm(
					patParm);
			// System.out.println("�ۿ�ӿ�==dosageParm=" + dosageParm);
			count = dosageParm.getCount("KEY");
			TParm reduceStock = INDTool.getInstance().reduceIndStockByStation(
					dosageParm, connection);
			// System.out.println("�ۿ�ӿ�==reduceStock=" + reduceStock);
			// if (reduceStock.getErrCode() != 0) {
			// //
			// System.out.println("STOCK=============="+reduceStock.getErrText());
			// lackParm.addData("MSG", dosageParm.getValue("PAT_NAME", 0)
			// + "�������ţ�" + dosageParm.getValue("MR_NO", 0) + "��" + "��"
			// + dosageParm.getValue("ORDER_DESC", 0) + "��治��,��ҩʧ��");
			// lackParm.setCount(lackParm.getCount("MSG"));
			// connection.rollback();
			// continue;
			// }

			// �Ƽ۽ӿ�
			TParm inParmIbs = new TParm();
			count = patParm.getCount("CTZ1_CODE");
			String flg = parm.getValue("FLG");
			// zhangyong20110516 �������REGION_CODE
			inParmIbs.setData("REGION_CODE", parm.getValue("REGION_CODE"));
			// System.out.println("dispense_flg="+flg);
			inParmIbs.setData("M", patParm.getData());
			inParmIbs.setData("FLG", flg);
			inParmIbs.setData("CTZ1_CODE", patParm.getValue("CTZ1_CODE", 0));
			inParmIbs.setData("CTZ2_CODE", patParm.getValue("CTZ2_CODE", 0));
			inParmIbs.setData("CTZ3_CODE", patParm.getValue("CTZ3_CODE", 0));
			TParm resultIbs = IBSTool.getInstance().getIBSOrderData(inParmIbs);
			// System.out.println("�Ƽ۽ӿ�==resultIbs=" + resultIbs);
			// System.out.println("resultIbs==============="+resultIbs);
			if (resultIbs.getErrCode() != 0) {
				connection.rollback();
				// System.out.println("ibs method1 return ========="+resultIbs);
				lackParm.addData("MSG", dosageParm.getValue("PAT_NAME", 0)
						+ "�������ţ�" + dosageParm.getValue("MR_NO") + "��" + "��"
						+ dosageParm.getValue("ORDER_DESC") + "�Ƽ�ʧ��,��ҩʧ��");
				lackParm.setCount(lackParm.getCount("MSG"));
				continue;
			}
			TParm inIbs = new TParm();
			inIbs.setData("M", resultIbs.getData());
			inIbs.setData("FLG", flg);
			inIbs.setData("DATA_TYPE", IBS_DATA_TYPE);
			// System.out.println("inIbs----+"+inIbs);
			TParm resultIbsM2 = IBSTool.getInstance().insertIBSOrder(inIbs,
					connection);
			// System.out.println("�Ƽ۽ӿ�==resultIbsM2=" + resultIbsM2);
			if (resultIbsM2.getErrCode() != 0) {
				connection.rollback();
				// System.out.println("ibs method2 return ========="+resultIbsM2);
				lackParm.addData("MSG", dosageParm.getValue("PAT_NAME", 0)
						+ "�������ţ�" + dosageParm.getValue("MR_NO") + "��" + "��"
						+ dosageParm.getValue("ORDER_DESC") + "�Ƽ�ʧ��,��ҩʧ��");
				lackParm.setCount(lackParm.getCount("MSG"));
				continue;
			}
			String optUser = parm.getValue("OPT_USER", 0);

			for (int i = 0; i < count; i++) {
				TParm parmRow = patParm.getRow(i);
				parmRow.setData("CASHIER_CODE", optUser);
				parmRow.setData("CASHIER_USER", optUser);
				// BILL_FLG
				parmRow.setData("BILL_FLG", resultIbs.getData("BILL_FLG", i));

				// m
				parmRow.setData("IBS_CASE_NO_SEQ", resultIbs.getData(
						"CASE_NO_SEQ", i));
				parmRow.setData("IBS_SEQ_NO", resultIbs.getData("SEQ_NO", i));
				// d
				parmRow.setData("IBS_CASE_NO", resultIbs.getData("CASE_NO_SEQ",
						i));
				parmRow.setData("IBS_CASE_NO_SEQ", resultIbs.getData("SEQ_NO",
						i));
				result = UddChnCheckTool.getInstance().onUpdateMedDDispense(
						parmRow, connection);
				// System.out.println("��ҩD==onUpdateMedDDispense=" + result);
				if (result.getErrCode() < 0) {
					connection.rollback();
					lackParm.addData("MSG", dosageParm.getValue("PAT_NAME", 0)
							+ "�������ţ�" + dosageParm.getValue("MR_NO") + "��"
							+ "��" + dosageParm.getValue("ORDER_CODE")
							+ "��дʧ��,��ҩʧ��");
					lackParm.setCount(lackParm.getCount("MSG"));
					continue;
				}
				result = UddChnCheckTool.getInstance().onUpdateMedMDispense(
						parmRow, connection);
				// System.out.println("��ҩM==onUpdateMedMDispense=" + result);
				if (result.getErrCode() < 0) {
					connection.rollback();
					lackParm.addData("MSG", dosageParm.getValue("PAT_NAME", 0)
							+ "�������ţ�" + dosageParm.getValue("MR_NO") + "��"
							+ "��" + dosageParm.getValue("ORDER_CODE")
							+ "��дʧ��,��ҩʧ��");
					lackParm.setCount(lackParm.getCount("MSG"));
					continue;
				}
			}
			connection.commit();
		}
		if (!connection.isClosed()) {
			connection.close();
		}
		// System.out.println("lackParm="+lackParm);
		if (lackParm.getCount() > 0) {
			result.setData("ERR_LIST", lackParm.getData());
		}
		return result;

	}

	/**
	 * ��ҩ��ҩ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onUpdateMedDosage(TParm parm) {
		TParm lackParm = new TParm();
		TConnection connection = getConnection();
		Timestamp now = SystemTool.getInstance().getDate();
		TParm result = new TParm();
		
		//==== ���������޸�  alert by wukai (ע�͵�)
		//TParm ivaParm = UDDIvaTool.getInstance().getPivasFlg();
//		String pivasFlg = ivaParm.getValue("PIVAS_FLG", 0);
//		ivaParm = UDDIvaTool.getInstance().getOdiSysparm();
//		String ivaStat = ivaParm.getValue("IVA_STAT",0);
//		String ivaFirst = ivaParm.getValue("IVA_FIRST",0);
//		String ivaUd = ivaParm.getValue("IVA_UD",0);
//		ivaParm = UDDIvaTool.getInstance().getIvaFlg();
//		String ivaFlg = ivaParm.getValue("IVA_FLG",0);
//		String caseNo = "";
//		String orderNo = "";
//		String orderSeq = "";
//		String startDttm = "";
//		String endDttm = "";
//		String orderDateTime = "";
//		String batchCode = "";
//		String linkNo = "";
//		String barCode = "";
//		String routeCode = "";
//		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
//			TParm parmRow = parm.getRow(i);
//			String dspnKind = parmRow.getValue("DSPN_KIND"); 
//			caseNo = parmRow.getValue("CASE_NO"); 
//			orderNo = parmRow.getValue("ORDER_NO"); 
//			orderSeq = parmRow.getValue("ORDER_SEQ"); 
//			startDttm = parmRow.getValue("START_DTTM"); 
//			endDttm = parmRow.getValue("END_DTTM"); 
//			linkNo = parmRow.getValue("LINK_NO");
//			routeCode = parmRow.getValue("ROUTE_CODE");
//			barCode = orderNo+startDttm.substring(8, 12)+linkNo;
//			if(linkNo==null||"".equals(linkNo)) {
//				continue;
//			}
//			if("ST".equals(dspnKind)&&"N".equals(ivaStat)) {
//				continue;
//			}
//			else if("UD".equals(dspnKind)&&"N".equals(ivaUd)) {
//				continue;
//			}
//			else if("F".equals(dspnKind)&&"N".equals(ivaFirst)){
//				continue;
//			}
//			String sql = "SELECT * FROM SYS_PHAROUTE WHERE IVA_FLG='Y' AND ROUTE_CODE='"+routeCode+"'";
//			TParm resultParm  = new TParm(TJDODBTool.getInstance().select(sql));
//			if(resultParm.getErrCode() < 0) {
//				connection.close();
//				return resultParm; 
//			}  
//			if(resultParm.getCount()<=0) {
//				continue;
//			}
//			sql = "SELECT ORDER_DATETIME FROM ODI_DSPND WHERE CASE_NO='"+caseNo+"' AND ORDER_NO='"+orderNo+"' AND ORDER_SEQ='"+orderSeq+"' AND TO_DATE (ORDER_DATE || ORDER_DATETIME, 'YYYYMMDDHH24MISS') BETWEEN TO_DATE('"+startDttm+"', 'YYYYMMDDHH24MISS') AND TO_DATE('"+endDttm+"', 'YYYYMMDDHH24MISS')";
//			resultParm  = new TParm(TJDODBTool.getInstance().select(sql));
//			if(resultParm.getErrCode() < 0) {
//				connection.close();
//				return resultParm; 
//			}  
//			//=== alert by wukai on 20170320 start  === ������Һ
//			for(int j = 0; j < resultParm.getCount(); j++) {
//				orderDateTime = resultParm.getValue("ORDER_DATETIME", j);
//				//dspnKind
//				sql = "SELECT BATCH_CODE FROM ODI_BATCHTIME WHERE '"+orderDateTime+"' BETWEEN TREAT_START_TIME AND TREAT_END_TIME" 
//						+ " AND  (STAT_FLG = '" + dspnKind    //��ʱ
//									+ "' OR UD_FLG = '" + dspnKind    //����
//								    + "' OR FIRST_FLG = '" + dspnKind  //������
//								    + "' OR OP_FLG = '" + dspnKind  //����
//								    + "' OR PN_FLG = '" + dspnKind  //Ӫ����
//						            + "' )";
//				TParm batchCodeParm  = new TParm(TJDODBTool.getInstance().select(sql));
//				if(batchCodeParm.getErrCode() < 0) {
//					connection.close();
//					return batchCodeParm; 
//				}  
//				if(batchCodeParm.getCount()<=0) {
//					break;						
//				}
//				batchCode = batchCodeParm.getValue("BATCH_CODE", 0);
//				sql = "UPDATE ODI_DSPND SET IVA_BARCODE='"+barCode+"' , BATCH_CODE='"+batchCode+"' , IVA_FLG='Y' WHERE CASE_NO='"+caseNo+"' AND ORDER_NO='"+orderNo+"' AND ORDER_SEQ='"+orderSeq+"' AND ORDER_DATETIME='"+orderDateTime+"'";
//				TParm resultUpdateD  = new TParm(TJDODBTool.getInstance().update(sql));
//				if(resultUpdateD.getErrCode() < 0) {			
//					connection.close();			
//					return resultUpdateD;         			        
//				}  
//				sql = "UPDATE ODI_DSPNM SET IVA_FLG='Y' , BATCH_CODE='"+batchCode+"' ,IVA_BARCODE='"+barCode+"'  WHERE CASE_NO='"+caseNo+"' AND ORDER_NO='"+orderNo+"' AND ORDER_SEQ='"+orderSeq+"' AND START_DTTM='"+startDttm+"'";
//				TParm resultUpdateM  = new TParm(TJDODBTool.getInstance().update(sql));
//				if(resultUpdateM.getErrCode() < 0) {
//					connection.close();   
//					return resultUpdateM; 
//				}  
//			}
//			//=== alert by wukai on 20170320 end === ������Һ
//			
//		}
		//==== ���������޸�  alert by wukai  end (ע�͵�)
		
		// SHIBL 20120403 ADD (�޸�ʱ�䲻ͳһ)
		for (int j = 0; j < parm.getCount(); j++) {
			parm.addData("OPT_DATE", now);
			TParm parmRow = parm.getRow(j);
			String dspnKind = parmRow.getValue("DSPN_KIND"); 
			if("ST".equals(dspnKind)) {
				String batchNo = parmRow.getValue("BATCH_NO");
				if (null == batchNo || batchNo.length() <= 0) {
					continue;
				}
				Double dosage_qty = parmRow.getDouble("DOSAGE_QTY");
				//String order_code = parmRow.getValue("ORDER_CODE");
				String CASE_NO = parmRow.getValue("CASE_NO");
				String ORDER_CODE = parmRow.getValue("ORDER_CODE");
				String ORG_CODE = parmRow.getValue("EXEC_DEPT_CODE");
				String sql = "SELECT A.PHA_SEQ,A.SEQ_NO FROM PHA_ANTI A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CASE_NO='"+CASE_NO+"' AND A.FREQ_CODE = 'STAT' AND A.ORDER_CODE='"+ORDER_CODE+"' AND B.ANTIBIOTIC_CODE IS NOT NULL ORDER BY A.ORDER_DATE DESC";
				TParm searchResult  = new TParm(TJDODBTool.getInstance().select(sql));
				if(searchResult.getCount()<=0) {
					continue;	
				}
				sql = "SELECT SUM(STOCK_QTY) AS STOCK_QTY FROM IND_STOCK  WHERE ORG_CODE='"+ORG_CODE+"' AND SYSDATE < VALID_DATE AND BATCH_NO='"+batchNo+"' AND ORDER_CODE='"+ORDER_CODE+"' GROUP BY ORDER_CODE ";
				result  = new TParm(TJDODBTool.getInstance().select(sql));
				Double STOCK_QTY = result.getDouble("STOCK_QTY",0);					
				if(dosage_qty>STOCK_QTY) {
					lackParm.addData("MSG", "Ƥ��ҩƷ:"+parmRow.getValue("ORDER_DESC")+" ���ţ�"+batchNo+" ��治��");
					lackParm.setCount(lackParm.getCount("MSG"));
					connection.rollback();				
					result.setData("ERR_LIST", lackParm.getData());
					result.setErrCode(-1);
					return result;   
				}
				String PHA_SEQ = searchResult.getValue("PHA_SEQ",0);
				int SEQ_NO = searchResult.getInt("SEQ_NO",0);				
				result = UddChnCheckTool.getInstance()							
				.onUpdatePhaAnti(parmRow,PHA_SEQ,SEQ_NO, connection);		
				if (result.getErrCode() < 0) {
					connection.close();
					return result;
				}
			} else if("UD".equals(dspnKind)||"F".equals(dspnKind)) {
				String batchNo = parmRow.getValue("BATCH_NO");
				if (null == batchNo || batchNo.length() <= 0) {
					continue;
				}
				Double dosage_qty = parmRow.getDouble("DOSAGE_QTY");
				String ORG_CODE = parmRow.getValue("ORG_CODE");
				//String order_code = parmRow.getValue("ORDER_CODE");
				String CASE_NO = parmRow.getValue("CASE_NO");
				String ORDER_CODE = parmRow.getValue("ORDER_CODE");
				String sql = "SELECT A.PHA_SEQ,A.SEQ_NO FROM PHA_ANTI A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CASE_NO='"+CASE_NO+"' AND A.ORDER_CODE='"+ORDER_CODE+"' AND B.ANTIBIOTIC_CODE IS NOT NULL ORDER BY A.ORDER_DATE DESC";
				TParm searchResult  = new TParm(TJDODBTool.getInstance().select(sql));
				if(searchResult.getCount()<=0) {
					continue;	
				}  				
				sql = "SELECT SUM(STOCK_QTY) AS STOCK_QTY FROM IND_STOCK WHERE ORG_CODE='"+ORG_CODE+"' AND BATCH_NO='"+batchNo+"' AND ORDER_CODE='"+ORDER_CODE+"' GROUP BY ORDER_CODE ";
				result  = new TParm(TJDODBTool.getInstance().select(sql));
				Double STOCK_QTY = result.getDouble("STOCK_QTY",0);		
				if(dosage_qty>STOCK_QTY) {
					lackParm.addData("MSG", "Ƥ��ҩƷ:"+parmRow.getValue("ORDER_DESC")+" ���ţ�"+batchNo+" ��治��");
					lackParm.setCount(lackParm.getCount("MSG"));
					connection.rollback();				
					result.setData("ERR_LIST", lackParm.getData());
					result.setErrCode(-1);
					return result;   
				}
			}
		}
		String charge = parm.getValue("CHARGE");
		String spcflg = parm.getValue("SPC_FLG");// ��ҩ���� shibl add 20130423
		int count = parm.getCount("OPT_TERM");
		if (!"DOSAGE".equalsIgnoreCase(charge)) {
			for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
				TParm parmRow = parm.getRow(i);
				// System.out.println("parmRow->"+parmRow);
				result = UddChnCheckTool.getInstance()
						.onUpdateUnChargeMedDDosage(parmRow, connection);
				if (result.getErrCode() < 0) {
					connection.close();
					// System.out.println("onUpdateMedDDosage is wrong ");
					return result;
				}
				result = UddChnCheckTool.getInstance()
						.onUpdateUnChargeMedMDosage(parmRow, connection);
				if (result.getErrCode() < 0) {
					connection.close();
					// System.out.println("onUpdateMedMDosage is wrong");
					return result;
				}
			}
			connection.commit();
			connection.close(); 
			return result;
		}

		Map pat = UddChnCheckTool.getInstance().groupByPatParm(parm);
		Iterator it = pat.values().iterator();
		// System.out.println("pat===="+pat);
		//�������龫�ۿ⿪�� by liyh 20130601
		String isReduFlg = "0";
		TParm indSysParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getINDSysParm()));
		if (null != indSysParm ) {
			isReduFlg = indSysParm.getValue("IS_REDU_DRUG", 0);
		}
//		System.out.println("spcflg:"+spcflg+"-----�龫����-------isReduFlg:"+isReduFlg);
		while (it.hasNext()) {
			TParm patParm = (TParm) it.next();
			count = patParm.getCount("CASE_NO");
//			System.out.println("--------------isAllMjFlg:"+isAllMjFlg);
			// �ۿ�ӿ�
			TParm dosageParm = UddChnCheckTool.getInstance().groupByStockParm(
					patParm);
			if ("1".equals(isReduFlg) && "Y".equals(spcflg)) {//����龫�ۿ⣬�������������ǿ�(���ǲ��ۿ⣩
//				System.out.println("------------------------�龫��ʼ�ۿ�1");
				// ����ֵΪʵ�ʿۿ��order��Ϣ
				TParm reduceStockDrug = INDTool.getInstance()
						.reduceIndStockByStationOfDrug(dosageParm, connection);
//				System.out.println("------------------reduceStockDrug:"+reduceStockDrug);

			}			
			if (spcflg.equals("N")) {
//				System.out.println("<<<<<<<<<<<<<!!!!!!!!!!!!1");
				// System.out.println("dosageParm---"+dosageParm);
				count = dosageParm.getCount("KEY");
				// System.out.println("dosageParm-- -count"+count);
				// System.out.println("dosageParm="+dosageParm);
				// ����ֵΪʵ�ʿۿ��order��Ϣ
				TParm reduceStock = INDTool.getInstance()
						.reduceIndStockByStation(dosageParm, connection);

				if (reduceStock.getErrCode() != 0) {
					String order_code = reduceStock.getValue("ORDER_CODE");
					//
					// System.out.println("STOCK=============="+reduceStock.getErrText());
					// reduceStock.setErrText(text)		
					lackParm.addData("MSG", dosageParm.getValue("PAT_NAME", 0)
							+ "�������ţ�" + dosageParm.getValue("MR_NO", 0) + "��"
							+ "��" + order_code + ", ��治��,��ҩʧ��");
					lackParm.setCount(lackParm.getCount("MSG"));
					connection.rollback();
					continue;
				}
				// ****************************************************************
				// luhai add 2012-1-28 ���ݷ��صķ�ҩ��Ϣ�Ͳ����ķ�ҩ��Ϣ��дסԺ��ҩ����begin
				// ****************************************************************
				// reduceStock order_code batch_seq org_code dispense_qty
				// patParm ORDER_CODE DOSAGE_QTY MR_NO CASE_NO ORDER_NO
				// ORDER_SEQ
				// START_DTTM
				for (int k = 0; k < patParm.getCount("ORDER_CODE"); k++) {
					TParm updateDispenseParm = new TParm();
					updateDispenseParm.addData("CASE_NO", patParm.getValue(
							"CASE_NO", k));
					updateDispenseParm.addData("ORDER_NO", patParm.getValue(
							"ORDER_NO", k));
					updateDispenseParm.addData("ORDER_SEQ", patParm.getValue(
							"ORDER_SEQ", k));
					updateDispenseParm.addData("START_DTTM", patParm.getValue(
							"START_DTTM", k));
					double dosageQty = patParm.getDouble("DOSAGE_QTY", k);
					int index = 1;// ��ҩ��¼index��1-3��
					for (int j = 0; j < reduceStock.getCount("ORDER_CODE"); j++) {
						boolean isbreak = true;
						if (!reduceStock.getValue("ORDER_CODE", j).equals(
								patParm.getValue("ORDER_CODE", k))) {
							continue;
						}
						String tmpBatch_seq = "";
						String tmpDispenseQty = "";
						String tmpVerifyin_price = "";
						double dispenseQty = reduceStock.getDouble(
								"DISPENSE_QTY", j);
						tmpBatch_seq = reduceStock.getValue("BATCH_SEQ", j);
						tmpVerifyin_price = reduceStock.getValue(
								"VERIFYIN_PRICE", j);
						// ʵ�ʷ�ҩ���ڸĿۿ�����
						if (dosageQty > dispenseQty) {
							tmpDispenseQty = dispenseQty + "";
							reduceStock.setData("DISPENSE_QTY", j, 0);
							dispenseQty = dispenseQty - dispenseQty;
							isbreak = false;
						} else if (dosageQty == dispenseQty) {
							tmpDispenseQty = dispenseQty + "";
							reduceStock.setData("DISPENSE_QTY", j, 0);
							dispenseQty = 0;
						} else {
							tmpDispenseQty = dosageQty + "";
							reduceStock.setData("DISPENSE_QTY", j, dispenseQty
									- dosageQty);
							dispenseQty = 0;
						}
						// ����¼�ķ�ҩ��Ϣд�뵽TParm �� begin
						if (index <= 3) {
							updateDispenseParm.addData("BATCH_SEQ" + index,
									tmpBatch_seq);
							updateDispenseParm
									.addData("VERIFYIN_PRICE" + index,
											tmpVerifyin_price);
							updateDispenseParm.addData("DISPENSE_QTY" + index,
									tmpDispenseQty);
						}
						index++;
						if (isbreak) {
							break;
						} else {
							continue;
						}
					}
					// ��û��ʹ��ȫ��ҩ�������ֶΣ���Ҫ��������ֵ
					while ((index - 1) < 3) {
						updateDispenseParm.addData("BATCH_SEQ" + index, -1);
						updateDispenseParm.addData("VERIFYIN_PRICE" + index,
								"0");
						updateDispenseParm.addData("DISPENSE_QTY" + index, "0");
						index++;
					}
					// ���¸ñʷ�ҩ��¼����ϸ��Ϣ
					TParm updateUDDDispenseDetail = UddChnCheckTool
							.getInstance().updateUDDDispenseDetail(
									updateDispenseParm.getRow(0), connection);
					if (updateUDDDispenseDetail.getErrCode() < 0) {
						connection.rollback();
						continue;
					}
				}
			}else{//��ҩ�����    shibl  add  20130712 
				SpcOdiDspnms Dspnms = new SpcOdiDspnms();	
				List<SpcIndStock> StockList = new ArrayList<SpcIndStock>();
				for (int i = 0; i < count; i++) {
					TParm parmRow = patParm.getRow(i);
					SpcIndStock sis = new SpcIndStock();
					sis.setDosageQty(parmRow.getDouble("DOSAGE_QTY"));
					sis.setOptDate(StringTool.getString(parmRow.getTimestamp("OPT_DATE"), "yyyy-MM-dd HH:mm:ss"));
					sis.setOptTerm(parmRow.getValue("OPT_TERM"));
					sis.setOptUser(parmRow.getValue("OPT_USER"));
					sis.setOrderCode(parmRow.getValue("ORDER_CODE"));
					sis.setOrderDesc(parmRow.getValue("ORDER_DESC"));
					sis.setOrgCode(parmRow.getValue("EXEC_DEPT_CODE"));
					sis.setServiceLevel(parmRow.getValue("SERVICE_LEVEL"));
					sis.setSendatcFlg(parmRow.getValue("SENDATC_FLG"));
					StockList.add(sis);
				}
				Dspnms.setSpcIndStocks(StockList);
				String  resultline=SpcOdiService_SpcOdiServiceImplPort_Client.onCheckStockQty(Dspnms);
				if(!resultline.toLowerCase().equals("success")){
					lackParm.addData("MSG", dosageParm.getValue("PAT_NAME", 0)
							+ "�������ţ�"+dosageParm.getValue("MR_NO", 0)+")"+ "\r\n"+ resultline);
					lackParm.setCount(lackParm.getCount("MSG"));
					connection.rollback();
					continue;
				}
			}
			// ****************************************************************
			// luhai add 2012-1-28 ���ݷ��صķ�ҩ��Ϣ�Ͳ����ķ�ҩ��Ϣ��дסԺ��ҩ����end
			// ****************************************************************
			// �Ƽ۽ӿ�
			TParm inParmIbs = new TParm();
			count = patParm.getCount("CTZ1_CODE");  
			String flg = parm.getValue("FLG");
			// zhangyong20110516 �������REGION_CODE
			inParmIbs.setData("REGION_CODE", parm.getValue("REGION_CODE"));
			// System.out.println("uddAction.flg==="+flg);
			inParmIbs.setData("M", patParm.getData());
			inParmIbs.setData("FLG", flg);
			inParmIbs.setData("CTZ1_CODE", patParm.getValue("CTZ1_CODE", 0));
			inParmIbs.setData("CTZ2_CODE", patParm.getValue("CTZ2_CODE", 0));
			inParmIbs.setData("CTZ3_CODE", patParm.getValue("CTZ3_CODE", 0));
			//fux 20160118 need modify
		    //System.out.println("inParmIbs===>" + inParmIbs);
			//fux 20160118 ���޸�   
			TParm resultIbs = IBSTool.getInstance().getIBSOrderData(inParmIbs);
			// System.out.println("resultIbs===============" + resultIbs);
			if (resultIbs.getErrCode() != 0) {
				connection.rollback();
				// System.out.println("ibs method1 return ========="+resultIbs);
				lackParm.addData("MSG", dosageParm.getValue("PAT_NAME", 0)
						+ "�������ţ�" + dosageParm.getValue("MR_NO", 0) + "��" + "��"
						+ "\r\n" + dosageParm.getValue("ORDER_CODE") + "\r\n"
						+ "�Ƽ�ʧ��,��ҩʧ��,��������ҩ");
				lackParm.setCount(lackParm.getCount("MSG"));
				continue;
			}
			TParm inIbs = new TParm();  
			inIbs.setData("M", resultIbs.getData());
			inIbs.setData("DATA_TYPE", IBS_DATA_TYPE);
			inIbs.setData("FLG", flg);
			System.out.println("inIbs111111111111111111===>"+inIbs);  
			//fux 20160118 need modify
			TParm resultIbsM2 = IBSTool.getInstance().insertIBSOrder(inIbs,
					connection);
			//System.out.println("resultIbsM2=============="+resultIbsM2);
			if (resultIbsM2.getErrCode() != 0) {
				connection.rollback();
				//System.out.println("onUpdateMed  insertIBSOrder  is wrong ");
				// System.out.println("ibs method2 return ========="+resultIbsM2);
				lackParm.addData("MSG", dosageParm.getValue("PAT_NAME", 0)
						+ "�������ţ�" + dosageParm.getValue("MR_NO", 0) + "��" + "��"
						+ "\r\n" + dosageParm.getValue("ORDER_CODE") + "\r\n"
						+ "�Ƽ�ʧ��,��ҩʧ��,��������ҩ");
				lackParm.setCount(lackParm.getCount("MSG"));
				continue;
			}
			String optUser = parm.getValue("OPT_USER", 0);
			List<SpcOdiDspnm> spcOdiDspnmList = new ArrayList<SpcOdiDspnm>();// add
			// by
			// wanglong
			// 20130111
			List<SpcIndStock> spcIndStockList = new ArrayList<SpcIndStock>(); // add
			// by
			// wanglong
			// 20130111
			for (int i = 0; i < count; i++) {
				TParm parmRow = patParm.getRow(i);
				// System.out.println("parmRow---"+parmRow);
				parmRow.setData("CASHIER_CODE", optUser);
				parmRow.setData("CASHIER_USER", optUser);
				// BILL_FLG
				parmRow.setData("BILL_FLG", resultIbs.getData("BILL_FLG", i));  
                //fux 20160118 need modify ���� odi_dspnm�� IBS_CASE_NO_SEQֵ
				// m  
				parmRow.setData("IBS_CASE_NO_SEQ_M", resultIbs.getData("CASE_NO_SEQ", i));
				parmRow.setData("IBS_SEQ_NO", resultIbs.getData("SEQ_NO", i));
				// d
				parmRow.setData("IBS_CASE_NO", resultIbs.getData("CASE_NO_SEQ",
						i));
				parmRow.setData("IBS_CASE_NO_SEQ", resultIbs.getData("SEQ_NO",i));
				parmRow.setData("PHA_DISPENSE_NO", resultIbs.getData(
						"PHA_DISPENSE_NO", i));
		        parmRow.setData("OWN_PRICE", resultIbs.getData("OWN_PRICE", i));//add by wanglong 20130702
				//System.out.println("parmRow->"+parmRow);
				result = UddChnCheckTool.getInstance().onUpdateMedDDosage(
						parmRow, connection);
				if (result.getErrCode() < 0) {
					connection.rollback();
					System.out.println("onUpdateMedDDosage is wrong ");
					lackParm.addData("MSG", dosageParm.getValue("PAT_NAME", 0)
							+ "�������ţ�" + dosageParm.getValue("MR_NO", 0) + "��"
							+ "��" + "\r\n" + dosageParm.getValue("ORDER_CODE")
							+ "\r\n" + "��дʧ��,��ҩʧ��,�����³�����ҩ");
					lackParm.setCount(lackParm.getCount("MSG"));
					continue;
				}
				result = UddChnCheckTool.getInstance().onUpdateMedMDosage(
						parmRow, connection);
				if (result.getErrCode() < 0) {
					connection.rollback();
					System.out.println("onUpdateMedMDosage is wrong");
					lackParm.addData("MSG", dosageParm.getValue("PAT_NAME", 0)
							+ "�������ţ�" + dosageParm.getValue("MR_NO", 0) + "��"
							+ "��" + "\r\n" + dosageParm.getValue("ORDER_CODE")
							+ "\r\n" + "��дʧ��,��ҩʧ��,�����³�����ҩ");
					lackParm.setCount(lackParm.getCount("MSG"));
					continue;  
				}
				if(spcflg.equals("N"))//�ǹ�ҩ���̷���   shibl  add  20130423  
					continue;
				// ================��ҩ������װ===================add by wanglong
				// 20130111
				String sql = "SELECT A.ANTIBIOTIC_CODE, A.BATCH_SEQ1, A.BATCH_SEQ2, A.BATCH_SEQ3, A.DC_DR_CODE,"
						+ " A.DC_NS_CHECK_DATE, A.DECOCT_DATE, A.DECOCT_REMARK, A.DECOCT_USER, A.DISPENSE_FLG,"
						+ " A.DISPENSE_QTY1, A.DISPENSE_QTY2, A.DISPENSE_QTY3, A.FINAL_TYPE, A.LIS_RE_DATE,"
						+ " A.LIS_RE_USER, A.PARENT_CASE_NO, A.PARENT_ORDER_NO, A.PARENT_ORDER_SEQ, A.PARENT_START_DTTM,"
						+ " A.PRESCRIPT_NO, A.PRESRT_NO, A.RETURN_QTY1, A.RETURN_QTY2, A.RETURN_QTY3, A.SEND_DCT_DATE,"
						+ " A.SEND_DCT_USER, A.SEND_ORG_DATE, A.SEND_ORG_USER, A.VERIFYIN_PRICE1, A.VERIFYIN_PRICE2,"
						+ " A.VERIFYIN_PRICE3, A.REGION_CODE,B.EXEC_NOTE, B.INV_CODE, B.NS_EXEC_CODE_REAL, B.NS_EXEC_DATE_REAL,"
						+ " B.NURSE_DISPENSE_FLG, B.ORDER_DATETIME, B.STOPCHECK_DATE, B.STOPCHECK_USER, B.TREAT_END_TIME, B.TREAT_START_TIME, "
						+ " B.ORDER_DATE,B.BILL_FLG,B.BAR_CODE,B.PHA_DISPENSE_CODE,B.PHA_DISPENSE_DATE, "
						+ " B.PHA_DISPENSE_NO, B.PHA_DOSAGE_CODE,B.PHA_DOSAGE_DATE ,B.TOT_AMT,B.ORDER_SEQ , "
						+ " B.NURSE_DISPENSE_FLG,B.DOSAGE_QTY,B.DOSAGE_UNIT,B.TAKEMED_ORG,B.INTGMED_NO,"
						+ " B.CASE_NO,B.ORDER_NO,B.OPT_USER,B.OPT_TERM,B.OPT_DATE,B.ORDER_CODE ,B.TAKEMED_NO  "
						+ " FROM ODI_DSPNM A, ODI_DSPND B "
						+ " WHERE A.CASE_NO = B.CASE_NO "
						+ " AND A.ORDER_NO = B.ORDER_NO "
						+ " AND A.ORDER_SEQ = B.ORDER_SEQ "
						+ " AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN A.START_DTTM AND A.END_DTTM"
						+ " AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN '"
						+ parmRow.getValue("START_DTTM") // shibl 20130225
						// modidfy ��Ϊ����ʹ�
						+ "' AND '"
						+ parmRow.getValue("END_DTTM")
						+ "'"
						+ " AND A.CASE_NO = '"
						+ parmRow.getValue("CASE_NO")
						+ "' "
						+ " AND A.ORDER_NO = '"
						+ parmRow.getValue("ORDER_NO")
						+ "' "
						+ " AND A.ORDER_SEQ = '"
						+ parmRow.getValue("ORDER_SEQ") + "'";
				TParm param = new TParm(TJDODBTool.getInstance().select(sql));
				// System.out.println("sql----:"+sql);
				List<SpcOdiDspnd> spcOdiDspndList = new ArrayList<SpcOdiDspnd>();
				for (int j = 0; j < param.getCount(); j++) {// M��Ӧ���D ѭ�� shibl
					// 20130225 modify
					TParm parmD = param.getRow(j);
					SpcOdiDspnd sodd = new SpcOdiDspnd();

					// ����
					sodd.setCaseNo(parmD.getValue("CASE_NO"));
					sodd.setOrderNo(parmD.getValue("ORDER_NO"));

					// ȡ����
					sodd.setOrderSeq(parmD.getInt("ORDER_SEQ"));
					sodd.setOrderDate(parmD.getValue("ORDER_DATE"));
					sodd.setOrderDatetime(parmD.getValue("ORDER_DATETIME"));

					sodd.setNurseDispenseFlg(parmD
							.getValue("NURSE_DISPENSE_FLG"));
					sodd.setOrderCode(parmD.getValue("ORDER_CODE"));
					sodd.setDosageQty(parmD.getDouble("DOSAGE_QTY"));
					sodd.setDosageUnit(parmD.getValue("DOSAGE_UNIT"));

					sodd.setBarCode(parmD.getValue("BAR_CODE"));
					// sodd.setBarcode1( );
					// sodd.setBarcode2( );
					// sodd.setBarcode3( );
					// sodd.setBatchCode( );
					// ��ҩĬ��Ϊ�շ�
					sodd.setBillFlg("Y");
					// sodd.setBoxEslId( );
					sodd.setCancelrsnCode(parmRow.getValue("CANCELRSN_CODE"));

					sodd.setCashierCode(parmRow.getValue("CASHIER_CODE"));
					sodd.setCashierDate(StringTool.getString(parmD
							.getTimestamp("CASHIER_DATE"),
							"yyyy-MM-dd HH:mm:ss"));
					sodd.setDcDate(StringTool.getString(parmD
							.getTimestamp("DC_DATE"), "yyyy-MM-dd HH:mm:ss"));
					sodd.setDcNsCheckDate(StringTool.getString(parmD
							.getTimestamp("DC_NS_CHECK_DATE"),
							"yyyy-MM-dd HH:mm:ss"));

					sodd.setExecDeptCode(parmD.getValue("EXEC_DEPT_CODE"));
					sodd.setExecNote(parmD.getValue("EXEC_NOTE"));
					sodd.setIbsCaseNo(parmRow.getValue("IBS_CASE_NO"));
					sodd.setIbsCaseNoSeq(parmRow.getValue("IBS_CASE_NO_SEQ"));

					sodd.setTakemedOrg(parmD.getValue("TAKEMED_ORG"));
					sodd.setIntgmedNo(parmD.getValue("INTGMED_NO"));
					sodd.setInvCode(parmD.getValue("INV_CODE"));
					sodd.setMediQty(parmD.getDouble("MEDI_QTY"));
					sodd.setMediUnit(parmD.getValue("MEDI_UNIT"));
					sodd.setNsExecCode(parmD.getValue("NS_EXEC_CODE"));
					sodd.setNsExecCodeReal("");
					sodd.setNsExecDate(StringTool.getString(parmD
							.getTimestamp("NS_EXEC_DATE"),
							"yyyy-MM-dd HH:mm:ss"));
					sodd.setNsExecDateReal("");
					sodd.setNsExecDcCode(parmD.getValue("NS_EXEC_DC_CODE"));
					sodd.setNsExecDcDate(StringTool.getString(parmD
							.getTimestamp("NS_EXEC_DC_DATE"),
							"yyyy-MM-dd HH:mm:ss"));
					sodd.setNsUser(parmD.getValue("NS_USER"));
					sodd.setNurseDispenseFlg(parmD
							.getValue("NURSE_DISPENSE_FLG"));
					sodd.setOptDate(StringTool.getString(parmRow
							.getTimestamp("OPT_DATE"), "yyyy-MM-dd HH:mm:ss"));
					sodd.setOptTerm(parmRow.getValue("OPT_TERM"));
					sodd.setOptUser(parmRow.getValue("OPT_USER"));

					sodd
							.setPhaDispenseCode(parmD
									.getValue("PHA_DISPENSE_CODE"));
					sodd
							.setPhaDispenseDate(parmD
									.getValue("PHA_DISPENSE_DATE"));
					sodd.setPhaDispenseNo(parmRow.getValue("PHA_DISPENSE_NO"));
					sodd.setPhaDosageCode(parmRow.getValue("OPT_USER"));
					sodd.setPhaDosageDate(StringTool.getString(parmRow
							.getTimestamp("OPT_DATE"), "yyyy-MM-dd HH:mm:ss"));
					sodd.setPhaRetnCode("");
					sodd.setPhaRetnDate("");
					// sodd.(parmRow.getValue("SERVICE_LEVEL"));
					sodd.setStopcheckDate("");
					sodd.setStopcheckUser("");

					sodd.setTotAmt(parmD.getDouble("TOT_AMT"));

					sodd.setTakemedNo(parmD.getValue("TAKEMED_NO"));
					// ��ҩ
					sodd.setTransmitRsnCode("");
					sodd.setTreatEndTime("");
					sodd.setTreatStartTime("");
					spcOdiDspndList.add(sodd);
				}
				//
				SpcOdiDspnm sodm = new SpcOdiDspnm();

				// ����ȡֵ��һ��
				param = param.getRow(0);
				// sodm.setAcumOutboundQty( );
				sodm.setAgencyOrgCode(parmRow.getValue("AGENCY_ORG_CODE"));
				sodm.setAntibioticCode(param.getValue("ANTIBIOTIC_CODE"));
				sodm.setAtcFlg(parmRow.getValue("ATC_FLG"));
				sodm.setBarCode(parmRow.getValue("BAR_CODE"));
				sodm.setBatchSeq1(param.getInt("BATCH_SEQ1"));
				sodm.setBatchSeq2(param.getInt("BATCH_SEQ2"));
				sodm.setBatchSeq3(param.getInt("BATCH_SEQ3"));
				sodm.setBedNo(parmRow.getValue("BED_NO"));
				sodm.setBillFlg(parmRow.getValue("BILL_FLG"));
				// sodm.setBoxEslId( );
				sodm.setCancelDosageQty(parmRow.getDouble("CANCEL_DOSAGE_QTY"));
				sodm.setCancelrsnCode(parmRow.getValue("CANCELRSN_CODE"));
				sodm.setCaseNo(parmRow.getValue("CASE_NO"));
				sodm.setCashierDate(StringTool.getString(parmRow
						.getTimestamp("CASHIER_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setCashierUser(parmRow.getValue("CASHIER_USER"));
				sodm.setCat1Type(parmRow.getValue("CAT1_TYPE"));
				sodm.setCtrldrugclassCode(parmRow
						.getValue("CTRLDRUGCLASS_CODE"));
				sodm.setDcDate(StringTool.getString(parmRow
						.getTimestamp("DC_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setDcDrCode(param.getValue("DC_DR_CODE"));
				sodm.setDcNsCheckDate(StringTool.getString(param
						.getTimestamp("DC_NS_CHECK_DATE"),
						"yyyy-MM-dd HH:mm:ss"));
				sodm.setDcTot(parmRow.getDouble("DC_TOT"));
				sodm.setDctTakeQty(parmRow.getInt("DCT_TAKE_QTY"));
				sodm.setDctagentCode(parmRow.getValue("DCTAGENT_CODE"));
				sodm.setDctagentFlg(parmRow.getValue("DCTAGENT_FLG"));
				sodm.setDctexcepCode(parmRow.getValue("DCTEXCEP_CODE"));
				sodm.setDecoctCode(parmRow.getValue("DECOCT_CODE"));
				sodm.setDecoctDate(StringTool.getString(param
						.getTimestamp("DECOCT_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setDecoctRemark(param.getValue("DECOCT_REMARK"));
				sodm.setDecoctUser(param.getValue("DECOCT_USER"));
				sodm.setDegreeCode(parmRow.getValue("DEGREE_CODE"));
				sodm.setDeptCode(parmRow.getValue("DEPT_CODE"));
				sodm.setDiscountRate(parmRow.getDouble("DISCOUNT_RATE"));
				sodm.setDispenseEffDate(StringTool.getString(parmRow
						.getTimestamp("DISPENSE_EFF_DATE"),
						"yyyy-MM-dd HH:mm:ss"));
				sodm.setDispenseEndDate(StringTool.getString(parmRow
						.getTimestamp("DISPENSE_END_DATE"),
						"yyyy-MM-dd HH:mm:ss"));
				sodm.setDispenseFlg(param.getValue("DISPENSE_FLG"));
				sodm.setDispenseQty(parmRow.getDouble("DISPENSE_QTY"));
				sodm.setDispenseQty1(param.getDouble("DISPENSE_QTY1"));
				sodm.setDispenseQty2(param.getDouble("DISPENSE_QTY2"));
				sodm.setDispenseQty3(param.getDouble("DISPENSE_QTY3"));
				sodm.setDispenseUnit(parmRow.getValue("DISPENSE_UNIT"));
				sodm.setDosageQty(parmRow.getDouble("DOSAGE_QTY"));
				sodm.setDosageUnit(parmRow.getValue("DOSAGE_UNIT"));
				sodm.setDoseType(parmRow.getValue("DOSE_TYPE"));
				sodm.setDrNote(parmRow.getValue("DR_NOTE"));
				sodm.setDspnDate(StringTool.getString(parmRow
						.getTimestamp("DSPN_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setDspnKind(parmRow.getValue("DSPN_KIND"));
				sodm.setDspnUser(parmRow.getValue("DSPN_USER"));
				sodm.setEndDttm(parmRow.getValue("END_DTTM"));
				sodm.setExecDeptCode(parmRow.getValue("EXEC_DEPT_CODE"));
				sodm.setFinalType(param.getValue("FINAL_TYPE"));
				sodm.setFreqCode(parmRow.getValue("FREQ_CODE"));
				sodm.setGiveboxFlg(parmRow.getValue("GIVEBOX_FLG"));
				sodm.setGoodsDesc(parmRow.getValue("GOODS_DESC"));
				sodm.setHideFlg(parmRow.getValue("HIDE_FLG"));
				sodm.setIbsCaseNoSeq(parmRow.getInt("IBS_CASE_NO_SEQ"));
				sodm.setIbsSeqNo(parmRow.getInt("IBS_SEQ_NO"));
				sodm.setInjpracGroup(parmRow.getInt("INJPRAC_GROUP"));

				// ͳҩ����
				// System.out.println("ͳҩ-----------��"+parmRow.getValue("INTGMED_NO")
				// );
				sodm.setIntgmedNo(parmRow.getValue("INTGMED_NO"));
				sodm.setIpdNo(parmRow.getValue("IPD_NO"));
				// sodm.setIsIntg( );
				sodm.setLinkNo(parmRow.getValue("LINK_NO"));
				sodm.setLinkmainFlg(parmRow.getValue("LINKMAIN_FLG"));
				sodm.setLisReDate(StringTool.getString(param
						.getTimestamp("LIS_RE_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setLisReUser(param.getValue("LIS_RE_USER"));
				sodm.setMediQty(parmRow.getDouble("MEDI_QTY"));
				sodm.setMediUnit(parmRow.getValue("MEDI_UNIT"));
				sodm.setMrNo(parmRow.getValue("MR_NO"));
				sodm.setNhiPrice(parmRow.getDouble("NHI_PRICE"));
				sodm.setNsExecCode(parmRow.getValue("NS_EXEC_CODE"));
				sodm.setNsExecDate(StringTool.getString(parmRow
						.getTimestamp("NS_EXEC_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setNsExecDcCode(parmRow.getValue("NS_EXEC_DC_CODE"));
				sodm.setNsExecDcDate(StringTool
						.getString(parmRow.getTimestamp("NS_EXEC_DC_DATE"),
								"yyyy-MM-dd HH:mm:ss"));
				sodm.setNsUser(parmRow.getValue("NS_USER"));
				sodm.setOptDate(StringTool.getString(parmRow
						.getTimestamp("OPT_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setOptTerm(parmRow.getValue("OPT_TERM"));
				sodm.setOptUser(parmRow.getValue("OPT_USER"));
				sodm.setOptitemCode(parmRow.getValue("OPTITEM_CODE"));
				sodm.setOrderCat1Code(parmRow.getValue("ORDER_CAT1_CODE"));
				sodm.setOrderCode(parmRow.getValue("ORDER_CODE"));
				sodm.setOrderDate(StringTool.getString(parmRow
						.getTimestamp("ORDER_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setOrderDeptCode(parmRow.getValue("ORDER_DEPT_CODE"));
				sodm.setOrderDesc(parmRow.getValue("ORDER_DESC"));
				sodm.setOrderDrCode(parmRow.getValue("ORDER_DR_CODE"));
				sodm.setOrderNo(parmRow.getValue("ORDER_NO"));
				sodm.setOrderSeq(parmRow.getInt("ORDER_SEQ"));
				sodm.setOrdersetCode(parmRow.getValue("ORDERSET_CODE"));
				sodm.setOrdersetGroupNo(parmRow.getValue("ORDERSET_GROUP_NO"));
				// sodm.setOrgCode(parmRow.getValue("EXEC_DEPT_CODE"));///////////////////////////
				sodm.setOwnAmt(parmRow.getDouble("OWN_AMT"));
				sodm.setOwnPrice(parmRow.getDouble("OWN_PRICE"));
				sodm.setPackageAmt(parmRow.getInt("PACKAGE_AMT"));
				sodm.setParentCaseNo(param.getValue("PARENT_CASE_NO"));
				sodm.setParentOrderNo(param.getValue("PARENT_ORDER_NO"));
				sodm.setParentOrderSeq(param.getInt("PARENT_ORDER_SEQ"));
				sodm.setParentStartDttm(param.getValue("PARENT_START_DTTM"));
				sodm.setPhaCheckCode(parmRow.getValue("PHA_CHECK_CODE"));
				sodm
						.setPhaCheckDate(StringTool.getString(parmRow
								.getTimestamp("PHA_CHECK_DATE"),
								"yyyy-MM-dd HH:mm:ss"));
				sodm.setPhaDispenseCode(parmRow.getValue("PHA_DISPENSE_CODE"));
				sodm.setPhaDispenseDate(StringTool.getString(parmRow
						.getTimestamp("PHA_DISPENSE_DATE"),
						"yyyy-MM-dd HH:mm:ss"));
				sodm.setPhaDispenseNo(parmRow.getValue("PHA_DISPENSE_NO"));
				sodm.setPhaDosageCode(parmRow.getValue("OPT_USER"));
				sodm.setPhaDosageDate(StringTool.getString(parmRow
						.getTimestamp("OPT_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setPhaRetnCode(parmRow.getValue("PHA_RETN_CODE"));
				sodm.setPhaRetnDate(StringTool.getString(parmRow
						.getTimestamp("PHA_RETN_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setPhaType(parmRow.getValue("PHA_TYPE"));
				sodm.setPrescriptNo(param.getValue("PRESCRIPT_NO"));
				sodm.setPresrtNo(param.getValue("PRESRT_NO"));
				sodm.setRegionCode(parmRow.getValue("REGION_CODE"));
				sodm.setReturnQty1(param.getDouble("RETURN_QTY1"));
				sodm.setReturnQty2(param.getDouble("RETURN_QTY2"));
				sodm.setReturnQty3(param.getDouble("RETURN_QTY3"));
				sodm.setRouteCode(parmRow.getValue("ROUTE_CODE"));
				sodm.setRpttypeCode(parmRow.getValue("RPTTYPE_CODE"));
				sodm.setRtnDosageQty(parmRow.getDouble("RTN_DOSAGE_QTY"));
				sodm.setRtnDosageUnit(parmRow.getValue("RTN_DOSAGE_UNIT"));
				sodm.setRtnNo(parmRow.getValue("RTN_NO"));
				sodm.setRtnNoSeq(parmRow.getInt("RTN_NO_SEQ"));
				sodm.setRxNo(parmRow.getValue("RX_NO"));
				sodm.setSendDctDate(StringTool.getString(param
						.getTimestamp("SEND_DCT_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setSendDctUser(param.getValue("SEND_DCT_USER"));
				sodm.setSendOrgDate(StringTool.getString(param
						.getTimestamp("SEND_ORG_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setSendOrgUser(param.getValue("SEND_ORG_USER"));
				sodm.setSendatcDttm(parmRow.getValue("SENDATC_DTTM"));
				sodm.setSendatcFlg(parmRow.getValue("SENDATC_FLG"));
				sodm.setSetmainFlg(parmRow.getValue("SETMAIN_FLG"));
				sodm.setSpecification(parmRow.getValue("SPECIFICATION"));
				sodm.setStartDttm(parmRow.getValue("START_DTTM"));
				sodm.setStationCode(parmRow.getValue("STATION_CODE"));
				sodm.setTakeDays(parmRow.getInt("TAKE_DAYS"));

				sodm.setRegionCode(param.getValue("REGION_CODE"));

				// ȡҩ����
				// System.out.println("TAKEMED_NO----:"+parmRow.getValue("TAKEMED_NO"));

				// �Ͱ�ҩ��ע��
				sodm.setSendatcFlg(parmRow.getValue("SENDATC_FLG"));
				sodm.setTakemedNo(parmRow.getValue("TAKEMED_NO"));
				sodm.setTakemedOrg(parmRow.getValue("TAKEMED_ORG"));

				sodm.setTotAmt(parmRow.getDouble("TOT_AMT"));
				sodm.setTransmitRsnCode(parmRow.getValue("TRANSMIT_RSN_CODE"));
				// sodm.setTurnEslId( );
				sodm.setUrgentFlg(parmRow.getValue("URGENT_FLG"));
				sodm.setVerifyinPrice1(param.getDouble("VERIFYIN_PRICE1"));
				sodm.setVerifyinPrice2(param.getDouble("VERIFYIN_PRICE2"));
				sodm.setVerifyinPrice3(param.getDouble("VERIFYIN_PRICE3"));
				sodm.setVsDrCode(parmRow.getValue("VS_DR_CODE"));
				sodm.setSpcOdiDspnds(spcOdiDspndList);
				// System.out.println("-------D������---------�����С"+spcOdiDspndList.size());

				//
				SpcIndStock sis = new SpcIndStock();
				sis.setDosageQty(parmRow.getDouble("DOSAGE_QTY"));
				sis.setOptDate(StringTool.getString(parmRow
						.getTimestamp("OPT_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sis.setOptTerm(parmRow.getValue("OPT_TERM"));
				sis.setOptUser(parmRow.getValue("OPT_USER"));
				sis.setOrderCode(parmRow.getValue("ORDER_CODE"));
				sis.setOrderDesc(parmRow.getValue("ORDER_DESC"));
				sis.setOrgCode(parmRow.getValue("EXEC_DEPT_CODE"));// /////////////////////////
				sis.setServiceLevel(parmRow.getValue("SERVICE_LEVEL"));
				sis.setSendatcFlg(parmRow.getValue("SENDATC_FLG"));
				// System.out.println("SENDATC_FLG--------------:"+parmRow.getValue("SENDATC_FLG"));
				spcOdiDspnmList.add(sodm);
				spcIndStockList.add(sis);
				// =============================================================================
			}
			connection.commit();// shibl modify 20130407
			if(spcflg.equals("N"))//�ǹ�ҩ���̷���   shibl  add  20130423
				continue;
			// ====================��ҩ��������=================add by wanglong
			// System.out.println("=============����������Services��ʼ=========");
			// 20130111
			SpcOdiDspnms odiDspnms = new SpcOdiDspnms();
			odiDspnms.setSpcOdiDspnms(spcOdiDspnmList);
			odiDspnms.setSpcIndStocks(spcIndStockList);
			// System.out.println("�������ݴ�С=====��"+odiDspnms.getSpcOdiDspnms().size());
			String returnStr = "";
			try {

				returnStr = SpcOdiService_SpcOdiServiceImplPort_Client
						.examine(odiDspnms);
				if (("success").equals(returnStr)) {
					// ��������
				} else {
					String errorStr = dosageParm.getValue("PAT_NAME", 0)
							+ "�������ţ�" + dosageParm.getValue("MR_NO", 0) + ")��"
							+ returnStr + " �Ʒѳɹ�������������Servicesʧ��,��ͬ������";
					// System.out.println("errorStr---:"+errorStr);
					// System.out.println("=============����������Servicesʧ��=========");
					lackParm.addData("MSG", errorStr);
					lackParm.setCount(lackParm.getCount("MSG"));
					// result.setErr(-1, "����������Servicesʧ��");
					// Ԥ������M���Ƿ���
					UddChnCheckTool.getInstance().updateGYWSSendFlg(patParm,
							"N");
					continue;
				}
			} catch (Exception e) {
				// TODO: handle exception
				// System.out.println("=============����������Services�쳣=========");
				lackParm.addData("MSG", dosageParm.getValue("PAT_NAME", 0)
						+ "�������ţ�" + dosageParm.getValue("MR_NO", 0)
						+ ")�Ʒѳɹ�������������Services�쳣,��ͬ������");
				lackParm.setCount(lackParm.getCount("MSG"));
				// result.setErr(-1, "����������Servicesʧ���쳣");
				// Ԥ������M���Ƿ���
				UddChnCheckTool.getInstance().updateGYWSSendFlg(patParm, "N");
				continue;
			}
			// System.out.println("=============����������Services����=========");
			// connection.commit();//delete by wanglong 20130111
			// =================================================
		}  
		if (!connection.isClosed()) {
			connection.close();
		}
		// System.out.println("ERR_LIST.errList=-==="+lackParm);
		if (lackParm.getCount() > 0) {
			result.setData("ERR_LIST", lackParm.getData());
		}
		return result;

	}

	/**
	 * ȡ����ҩ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onCnclUpdateMedDosage(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		// System.out.println("onUpdateMedDosage.count->---------------"+parm.getCount("CASE_NO"));
		// System.out.println("out parm============"+parm);
		String charge = parm.getValue("CHARGE");
		String service_level = "1";
		int count = parm.getCount("OPT_TERM");
		if (!"DOSAGE".equalsIgnoreCase(charge)) {
			for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
				TParm parmRow = parm.getRow(i);
				// System.out.println("parmRow->"+parmRow);
				result = UddChnCheckTool.getInstance()
						.onUpdateUnChargeCnclMedDDosage(parmRow, connection);
				if (result.getErrCode() < 0) {
					connection.close();
					System.out
							.println("onUpdateUnChargeCnclMedDDosage is wrong ");
					return result;
				}
				result = UddChnCheckTool.getInstance()
						.onUpdateUnChargeCnclMedMDosage(parmRow, connection);
				if (result.getErrCode() < 0) {
					connection.close();
					System.out
							.println("onUpdateUnChargeCnclMedMDosage is wrong");
					return result;
				}
			}

			connection.commit();
			connection.close();
			return result;
		}

		// ȡ����ҩ�ӿ�
		for (int i = 0; i < count; i++) {
			service_level = parm.getValue("SERVICE_LEVEL", i);
			parm.setData("ORG_CODE", i, parm.getValue("EXEC_DEPT_CODE", i));
			TParm reduceStock = INDTool.getInstance().unReduceIndStock(
					parm.getRow(i), service_level, connection);
			if (reduceStock.getErrCode() != 0) {
				connection.close();
				return reduceStock;
			}
		}

		// �Ƽ۽ӿ�
		TParm inParmIbs = new TParm();
		count = parm.getCount("CTZ1_CODE");
		String flg = parm.getValue("FLG");
		// zhangyong20110516 �������REGION_CODE
		inParmIbs.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		inParmIbs.setData("M", parm.getData());
		inParmIbs.setData("FLG", flg);
		inParmIbs.setData("CTZ1_CODE", parm.getValue("CTZ1_CODE", 0));
		inParmIbs.setData("CTZ2_CODE", parm.getValue("CTZ2_CODE", 0));
		inParmIbs.setData("CTZ3_CODE", parm.getValue("CTZ3_CODE", 0));
		TParm resultIbs = IBSTool.getInstance().getIBSOrderData(inParmIbs);
		// System.out.println("resultIbs==============="+resultIbs);
		if (resultIbs.getErrCode() != 0) {
			connection.close();
			// System.out.println("ibs method1 return =========" + resultIbs);
			return resultIbs;
		}
		TParm inIbs = new TParm();
		inIbs.setData("M", resultIbs.getData());
		inIbs.setData("DATA_TYPE", IBS_DATA_TYPE);
		inIbs.setData("FLG", flg);
		// System.out.println("resultIbs.getData"+inIbs);
		TParm resultIbsM2 = IBSTool.getInstance().insertIBSOrder(inIbs,
				connection);
		if (resultIbsM2.getErrCode() != 0) {
			connection.close();
			// System.out.println("ibs method2 return =========" + resultIbsM2);
			return resultIbsM2;
		}
		String optUser = parm.getValue("OPT_USER", 0);
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			TParm parmRow = parm.getRow(i);
			parmRow.setData("CASHIER_CODE", optUser);
			parmRow.setData("CASHIER_USER", optUser);
			// BILL_FLG
			parmRow.setData("BILL_FLG", resultIbs.getData("BILL_FLG", i));

			// m
			parmRow.setData("IBS_CASE_NO_SEQ", resultIbs.getData("CASE_NO_SEQ",
					i));
			parmRow.setData("IBS_SEQ_NO", resultIbs.getData("SEQ_NO", i));
			// d
			parmRow.setData("IBS_CASE_NO", resultIbs.getData("CASE_NO_SEQ", i));
			parmRow.setData("IBS_CASE_NO_SEQ", resultIbs.getData("SEQ_NO", i));
			// System.out.println("parmRow->"+parmRow);
			result = UddChnCheckTool.getInstance().onUpdateCnclMedDDosage(
					parmRow, connection);
			if (result.getErrCode() < 0) {
				connection.close();
				// System.out.println("onUpdateCnclMedDDosage is wrong ");
				return result;
			}
			result = UddChnCheckTool.getInstance().onUpdateCnclMedMDosage(
					parmRow, connection);
			if (result.getErrCode() < 0) {
				connection.close();
				// System.out.println("onUpdateCnclMedMDosage is wrong");
				return result;
			}
		}

		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ȡ����ҩ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onCnclUpdateMedDispense(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		// �ۿ�ӿ�
		int count = parm.getCount("OPT_TERM");
		String charge = parm.getValue("CHARGE");
		String service_level = "1";
		if (!"DISPENSE".equalsIgnoreCase(charge)) {
			for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
				TParm parmRow = parm.getRow(i);
				// System.out.println("parmRow->"+parmRow);onUpdateUnChargeMedMDispense
				result = UddChnCheckTool.getInstance()
						.onUpdateCnclMedMDispense(parmRow, connection);
				// result =
				// UddChnCheckTool.getInstance().onUpdateCnclMedDDispense(parmRow,
				// connection);
				if (result.getErrCode() < 0) {
					connection.close();
					// System.out.println("onUpdateMedDDosage is wrong ");
					return result;
				}

				// result =
				// UddChnCheckTool.getInstance().onUpdateCnclMedMDispense(parmRow,
				// connection);
				result = UddChnCheckTool.getInstance()
						.onUpdateCnclMedDDispense(parmRow, connection);
				if (result.getErrCode() < 0) {
					connection.close();
					// System.out.println("onUpdateMedMDosage is wrong");
					return result;
				}
			}

			connection.commit();
			connection.close();
			return result;
		}

		// ȡ����ҩ�ӿ�
		for (int i = 0; i < count; i++) {
			service_level = parm.getValue("SERVICE_LEVEL", i);
			TParm reduceStock = INDTool.getInstance().unReduceIndStock(
					parm.getRow(i), service_level, connection);
			if (reduceStock.getErrCode() != 0) {
				connection.close();
				return reduceStock;
			}
		}

		// �Ƽ۽ӿ�
		TParm inParmIbs = new TParm();
		count = parm.getCount("CTZ1_CODE");
		String flg = parm.getValue("FLG");
		// zhangyong20110516 �������REGION_CODE
		inParmIbs.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		inParmIbs.setData("M", parm.getData());
		inParmIbs.setData("FLG", flg);
		inParmIbs.setData("CTZ1_CODE", parm.getValue("CTZ1_CODE", 0));
		inParmIbs.setData("CTZ2_CODE", parm.getValue("CTZ2_CODE", 0));
		inParmIbs.setData("CTZ3_CODE", parm.getValue("CTZ3_CODE", 0));
		TParm resultIbs = IBSTool.getInstance().getIBSOrderData(inParmIbs);
		// System.out.println("resultIbs==============="+resultIbs);
		if (resultIbs.getErrCode() != 0) {
			connection.close();
			// System.out.println("ibs method1 return =========" + resultIbs);
			return resultIbs;
		}
		TParm inIbs = new TParm();
		inIbs.setData("M", resultIbs.getData());
		inIbs.setData("DATA_TYPE", IBS_DATA_TYPE);
		inIbs.setData("FLG", flg);
		// System.out.println("resultIbs.getData"+resultIbs.getData());
		TParm resultIbsM2 = IBSTool.getInstance().insertIBSOrder(inIbs,
				connection);
		if (resultIbsM2.getErrCode() != 0) {
			connection.close();
			// System.out.println("ibs method2 return ========="+resultIbsM2);
			return resultIbsM2;
		}
		String optUser = parm.getValue("OPT_USER", 0);
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			TParm parmRow = parm.getRow(i);
			parmRow.setData("CASHIER_CODE", optUser);
			parmRow.setData("CASHIER_USER", optUser);
			// BILL_FLG
			parmRow.setData("BILL_FLG", resultIbs.getData("BILL_FLG", i));

			// m
			parmRow.setData("IBS_CASE_NO_SEQ", resultIbs.getData("CASE_NO_SEQ",
					i));
			parmRow.setData("IBS_SEQ_NO", resultIbs.getData("SEQ_NO", i));
			// d
			parmRow.setData("IBS_CASE_NO", resultIbs.getData("CASE_NO_SEQ", i));
			parmRow.setData("IBS_CASE_NO_SEQ", resultIbs.getData("SEQ_NO", i));
			// System.out.println("parmRow->"+parmRow);
			result = UddChnCheckTool.getInstance().onUpdateCnclMedDDispense(
					parmRow, connection);
			if (result.getErrCode() < 0) {
				connection.close();
				// System.out.println("onUpdateUnChargeMedMDispense is wrong ");
				return result;
			}
			result = UddChnCheckTool.getInstance().onUpdateCnclMedMDispense(
					parmRow, connection);
			if (result.getErrCode() < 0) {
				connection.close();
				// System.out.println("onUpdateMedMDosage is wrong");
				return result;
			}
		}

		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ��ҩ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSaveDisbatch(TParm parm) {
		TConnection connection = getConnection();
		TParm Oparm = (TParm) parm.getData("ORDER");
		TParm Mparm = (TParm) parm.getData("DSPNM");
		TParm Dparm = (TParm) parm.getData("DSPND");
		TParm result = new TParm();
		int index = Oparm.getCount("ORDER_SEQ");
		for (int i = 0; i < index; i++) {
			TParm parmRow = Oparm.getRow(i);
			// System.out.println("parmRow->"+parmRow);
			result = UddChnCheckTool.getInstance().onUpdateO(parmRow,
					connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
		}
		index = Mparm.getCount("ORDER_SEQ");
		for (int i = 0; i < index; i++) {
			TParm parmRow = Mparm.getRow(i);
			// System.out.println("parmRow->"+parmRow);
			result = UddChnCheckTool.getInstance().onInsertM(parmRow,
					connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
		}
		index = Dparm.getCount("ORDER_SEQ");
		for (int i = 0; i < index; i++) {
			TParm parmRow = Dparm.getRow(i);
			// System.out.println("parmRow->"+parmRow);
			result = UddChnCheckTool.getInstance().onInsertD(parmRow,
					connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
		}
		return result;
	}
	
	
	/**
	 * �Ƿ����龫
	 * @param parm
	 * @return true �ǣ�false ��
	 */
	public static boolean isAllMj(TParm parm){
		String orderCodes = "";
		//�Ƿ�ȫ���龫��false ���ǣ�true, ȫ���龫
		boolean isMj = false;
		if (null != parm) {
			int count =  parm.getCount("ORDER_CODE");
			for (int i = 0; i < count; i++) {
				orderCodes += "'" + parm.getValue("ORDER_CODE", i) + "',";
			}
			//��װorder_code
			orderCodes = orderCodes.length()>1 ? orderCodes.substring(0, orderCodes.length()-1) : orderCodes;
			String sql = INDSQL.getCountByMj(orderCodes);
//			System.out.println("----�Ƿ�ȫ���龫:"+sql);
			TParm mjParm = new TParm(TJDODBTool.getInstance().select(sql));
			int mjCount = 0;
			if(null != mjParm && mjParm.getCount()>0){
				mjCount =  mjParm.getCount();
			}
			//�������һ�� ��ȫ���龫
//			System.out.println("-----------mjCount:"+mjCount+"-------count:"+count);			
			if(count == mjCount ){
				isMj = true;
			}			
		}
		return isMj;
	}
}
