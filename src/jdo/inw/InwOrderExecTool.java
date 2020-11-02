package jdo.inw;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.data.TNull;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import jdo.odi.OdiMainTool;
import jdo.sys.Operator;

import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: סԺ��ʿվִ����Tool
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ZangJH
 * @version 1.0
 */
public class InwOrderExecTool extends TJDOTool {

	private String bilpoint;// �Ʒѵ� 1��ʿִ�мƷ� 2ҽ������ִ��

	/**
	 * ʵ��
	 */
	private static InwOrderExecTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PatTool
	 */
	public static InwOrderExecTool getInstance() {
		if (instanceObject == null)
			instanceObject = new InwOrderExecTool();
		return instanceObject;
	}

	public InwOrderExecTool() {
	}

	/**
	 * ִ��
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onExec(TParm parm, TConnection connection) {

		// �ñ�ǣ���ʾ�������á�����Ŀ���Ƿ��ڻ�ʿվִ�мƷ�----PS:x�ļ��õ�������Ϣ����
		// boolean
		// chargeFlg=StringTool.getBoolean(TConfig.getSystemValue("Action.INWFee"));
		bilpoint = (String) OdiMainTool.getInstance().getOdiSysParmData(
		"BIL_POINT");
		boolean chargeFlg = true;// ���ε�����X�ļ��ܿش��üƷѵ�
		TParm result = new TParm();
		// ǰ̨��������
		int count = parm.getCount();
		int ibsRows = 0;
		TParm forIBSParm = new TParm();
		for (int i = 0; i < count; i++) {
			boolean dcFlg = (Boolean) parm.getData("DC_ORDER", i);
			String caseNo = parm.getValue("CASE_NO", i);
			String orderNo = parm.getValue("ORDER_NO", i);
			String orderSeq = parm.getValue("ORDER_SEQ", i);
			String setMainFlg = parm.getValue("SETMAIN_FLG", i);
			String orderSerGroup = parm.getValue("ORDERSET_GROUP_NO", i);
			String startDttm = parm.getValue("START_DTTM", i);
			String endDttm = parm.getValue("END_DTTM", i);
			// ����ǰ̨��CASE_NO,ORDER_NO,ORDER_SEQ�ҳ�һ��ҽ��������SETMAIN_FLG�����Ƿ�Ϊ����ҽ��
			TParm orderPram = getAnOrder(caseNo, orderNo, orderSeq, setMainFlg,
					orderSerGroup, startDttm, endDttm);
			for (int j = 0; j < orderPram.getCount(); j++) {
				TParm execData = new TParm();
				execData.setData("CASE_NO", orderPram.getData("CASE_NO", j));
				execData.setData("ORDER_NO", orderPram.getData("ORDER_NO", j));
				execData
						.setData("ORDER_SEQ", orderPram.getData("ORDER_SEQ", j));
				execData.setData("NS_EXEC_CODE", parm.getData("OPT_USER", i));
				execData.setData("NS_EXEC_DATE", parm
						.getData("NS_EXEC_DATE", i));
				execData.setData("NS_EXEC_DC_CODE", dcFlg ? parm.getData(
						"OPT_USER", i) : new TNull(String.class));
				execData.setData("NS_EXEC_DC_DATE", dcFlg ? parm.getData(
						"OPT_DATE", i) : new TNull(Timestamp.class));
				execData.setData("OPT_USER", parm.getData("OPT_USER", i));
				execData.setData("OPT_TERM", parm.getData("OPT_TERM", i));
				execData.setData("OPT_DATE", parm.getData("OPT_DATE", i));
				execData.setData("START_DTTM", orderPram.getData("START_DTTM",
						j));
				execData.setData("END_DTTM", orderPram.getData("END_DTTM", j));
				result = InwForOdiTool.getInstance().updateOdiDspndForExec(
						execData, connection);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}

				result = InwForOdiTool.getInstance().updateOdiDspnmForExec(
						execData, connection);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}

				// --------���ÿ��ҽ������ҹ���---------------------------------------
				TParm actionCode = getActionName(orderPram.getValue(
						"ORDER_CODE", j));
				TParm actionParm = orderPram.getRow(j);
				actionParm.setData("ACTION_CODE", actionCode.getValue(
						"ACTION_CODE", 0));
				if (actionCode != null) {
					TJDODBTool.getInstance().exeIOAction(
							"jdo.adm.ADMNursingActionTool", actionParm,
							connection);
				}
				// ---------end-----------------------------------------------------

				// ������ODI��󣬴���IBS�����Ʒ�
				String cat1Type = (String) orderPram.getData("CAT1_TYPE", j);
				// ��PHA����Ҫִ�мƷ� �󶨷��õ���Ŀ��Ҫִ�мƷ�
				if (!"PHA".equals(cat1Type) && this.bilpoint.equals("1")) {
					forIBSParm.addData("CASE_NO", orderPram.getData("CASE_NO",
							j));
					forIBSParm.addData("ORDER_NO", orderPram.getData(
							"ORDER_NO", j));
					forIBSParm.addData("ORDER_SEQ", orderPram.getData(
							"ORDER_SEQ", j));
					forIBSParm.addData("START_DTTM", orderPram.getData(
							"START_DTTM", j));
					// ������
					ibsRows++;
				} else if (!"PHA".equals(cat1Type) && !"LIS".equals(cat1Type)
						&& !"RIS".equals(cat1Type) && this.bilpoint.equals("2")) {
					forIBSParm.addData("CASE_NO", orderPram.getData("CASE_NO",
							j));
					forIBSParm.addData("ORDER_NO", orderPram.getData(
							"ORDER_NO", j));
					forIBSParm.addData("ORDER_SEQ", orderPram.getData(
							"ORDER_SEQ", j));
					forIBSParm.addData("START_DTTM", orderPram.getData(
							"START_DTTM", j));
					// ������
					ibsRows++;
				}
			}
		}

		// �õ���ʿ��ע����ODI_DSPND
		TParm nurseNote = (TParm) parm.getParm("EXECNOTE");
		if (parm.existData("EXECNOTE")) {
			int noteCount = nurseNote.getCount();
			for (int i = 0; i < noteCount; i++) {
				result = InwForOdiTool.getInstance().updateOdiDspndToNsNot(
						nurseNote.getRow(i), connection);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
				result = InwForOdiTool.getInstance().updateOdiOrderForSkin(
						nurseNote.getRow(i), connection);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
//				//====Ƥ��ҩƷ��MED_NODIFY�������һ������ yanjing 20140714
//				String admInpSql = "SELECT A.IPD_NO,A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.BED_NO,A.IPD_NO,A.STATION_CODE,C.ORDER_CODE,C.ORDER_DEPT_CODE,C.ORDER_DR_CODE  " +
//        		"FROM ADM_INP A,SYS_PATINFO B ,ODI_ORDER C " +
//        		"WHERE A.MR_NO = B.MR_NO AND A.CASE_NO = C.CASE_NO  AND A.DS_DATE IS NULL AND " +
//        		"C.ORDER_NO = '"+nurseNote.getRow(i).getValue("ORDER_NO")+"' AND C.ORDER_SEQ = '"+nurseNote.getRow(i).getValue("ORDER_SEQ")+"' ";
//                TParm admInpParm =  new TParm(TJDODBTool.getInstance().select(admInpSql));
//				//��ѯѡ��ҩ���Ƿ�ΪƤ��ҩƷ
//				String sql = "SELECT A.SKINTEST_FLG, A.ANTIBIOTIC_CODE,MAX(B.OPT_DATE)," +
//						"B.BATCH_NO,B.SKINTEST_NOTE" +
//							" FROM PHA_BASE A,PHA_ANTI B  WHERE A.ORDER_CODE = B.ORDER_CODE " +
//							"AND A.ORDER_CODE = '"+admInpParm.getValue("ORDER_CODE", 0)+"' AND B.CASE_NO = '"+nurseNote.getRow(i).getValue("CASE_NO")+"' " +
//							"GROUP BY B.BATCH_NO ,B.SKINTEST_NOTE,B.OPT_DATE,A.SKINTEST_FLG, A.ANTIBIOTIC_CODE " +
//							"ORDER BY B.OPT_DATE DESC";
////				System.out.println("Ƥ��ҩƷ sql is����"+sql);
//				TParm result1= new TParm(TJDODBTool.getInstance().select(sql));
////				System.out.println("Ƥ��ҩƷresult1 is����"+result1);
//				if(result1.getCount() >0&&result1.getValue("SKINTEST_FLG",0).equals("Y")){
//					//����Ҫ����Ĳ���
//					TParm medNodifyParm = new TParm ();
//					//��ǰ������ʱ��
//			        Timestamp date = TJDODBTool.getInstance().getDBTime();
//			        
//					medNodifyParm.setData("ORDER_DATE", nurseNote.getRow(i).getValue("ORDER_DATE"));
//					medNodifyParm.setData("ORDER_DATETIME", nurseNote.getRow(i).getValue("ORDER_DATETIME"));
//					medNodifyParm.setData("CASE_NO", nurseNote.getRow(i).getValue("CASE_NO"));
//					medNodifyParm.setData("MED_NOTIFY_CODE",java.util.UUID.randomUUID().toString());//��������֤Ψһ����
//					medNodifyParm.setData("SEQ",1);//����
//					medNodifyParm.setData("ADM_TYPE","I");//�ż�ס���
//					medNodifyParm.setData("MR_NO",admInpParm.getValue("MR_NO", 0));//������
//					medNodifyParm.setData("PAT_NAME",admInpParm.getValue("PAT_NAME", 0));//��������
//					medNodifyParm.setData("SEX_CODE",admInpParm.getValue("SEX_CODE", 0));//�Ա𣺱���
//					medNodifyParm.setData("BED_NO",admInpParm.getValue("BED_NO", 0));//���ţ�����
//					medNodifyParm.setData("IPD_NO",admInpParm.getValue("IPD_NO", 0));//סԺ��
//					medNodifyParm.setData("DEPT_CODE",admInpParm.getValue("ORDER_DEPT_CODE", 0));//��������
//					medNodifyParm.setData("BILLING_DOCTORS",admInpParm.getValue("ORDER_DR_CODE", 0));//����ҽ��
//					medNodifyParm.setData("CAT1_TYPE","PS");//Ĭ�ϣ�PS
//					medNodifyParm.setData("SYSTEM_CODE","");//Ĭ��Ϊ��
//					medNodifyParm.setData("APPLICATION_NO","");//Ĭ��Ϊ��
//					medNodifyParm.setData("ORDER_CODE",admInpParm.getValue("ORDER_CODE", 0));//ҽ������
//					medNodifyParm.setData("ORDER_NO", nurseNote.getRow(i).getValue("ORDER_NO"));//ҽ����
//					medNodifyParm.setData("ORDER_SEQ", nurseNote.getRow(i).getValue("ORDER_SEQ"));//˳���
//					medNodifyParm.setData("SEND_STAT","1");//״̬��ǣ�Ĭ��Ϊ1
//					medNodifyParm.setData("CRTCLLWLMT","N");//Σ��ֵ��Ĭ�ϣ�N
//					medNodifyParm.setData("STATION_CODE",admInpParm.getValue("STATION_CODE", 0));//����
//					medNodifyParm.setData("CLINICAREA_CODE","");//����
//					medNodifyParm.setData("SEND_DATE",date);//�������ڣ�Ĭ�ϵ�ǰ��������
//					medNodifyParm.setData("REMARK", nurseNote.getRow(i).getValue("EXEC_NOTE"));//Ƥ�Խ��
//					medNodifyParm.setData("OPT_USER", nurseNote.getRow(i).getValue("OPT_USER"));//������Ա
//					medNodifyParm.setData("OPT_DATE", date);//��������
//					medNodifyParm.setData("OPT_TERM", nurseNote.getRow(i).getValue("OPT_TERM"));//����ip
//					result = InwForOdiTool.getInstance().insertIntoMedNodify(
//							medNodifyParm, connection);
//					
//				}
			}
		}
		// ---------------------------IBS�Ƽ۲���---------------------------------
		TParm orderToIbs = null;
		// һ�εõ�������Ҫ�Ʒѵ�����
		for (int i = 0; i < ibsRows; i++) {
			TParm date = getAnOrder(forIBSParm.getData("CASE_NO", i) + "",
					forIBSParm.getData("ORDER_NO", i) + "", forIBSParm.getData(
							"ORDER_SEQ", i)
							+ "", forIBSParm.getData("START_DTTM", i) + "");
			if (date.getCount() <= 0)
				continue;
			if (orderToIbs == null)
				orderToIbs = date;
			else
				orderToIbs.addParm(date);
		}
		// ���chargeFlg��ʿִ�мƷѱ��Ϊ�沢�Ҵ���ִ�мƷ���Ŀ
		if (chargeFlg && (orderToIbs != null)) {
			orderToIbs.setCount(ibsRows);
			result.setData("FORIBS", orderToIbs);
		}
		return result;
	}

	/**
	 * ����caseNo�õ��ò��˵ĳ��ڴ��� PS:����֧�ֶಡ����˵�ʱ��ÿ��ֻ��һ��caseNo
	 */
	private TParm getAnOrder(String caseNo, String orderNo, String orderSeq,
			String setMainFlg, String orderSetGroupNo, String startDttm,
			String endDttm) {
		TParm result = new TParm();
		String SelSql = "";
		// �Ƿ��Ǽ���ҽ������
		boolean isSetOrder = "Y".equals(setMainFlg);
		String startDttmSQL = "";
		if (startDttm.length() != 0)
			startDttmSQL = " AND START_DTTM = '" + startDttm + "'";
		// ------------������ҽ��--------------start----------------------------
		// �������Ϊ����ҽ���ҳ���������ϸ��һ�𱣴�
		if (isSetOrder) {
			SelSql = "SELECT * FROM ODI_DSPNM WHERE CASE_NO='" + caseNo
					+ "' AND ORDER_NO='" + orderNo + "' AND ORDERSET_GROUP_NO="
					+ Integer.parseInt(orderSetGroupNo) + startDttmSQL;
		} // ----------------------------------end--------------------------------
		else { // ��ͨҽ��
			SelSql = "SELECT * FROM ODI_DSPNM WHERE CASE_NO='" + caseNo
					+ "' AND ORDER_NO='" + orderNo + "' AND ORDER_SEQ='"
					+ orderSeq + "'" + startDttmSQL;

		}
		// �õ��ò������и�ִ��չ���Ĵ���
		result = new TParm(TJDODBTool.getInstance().select(SelSql));

		return result;
	}

	/**
	 * ������������
	 * 
	 * @param parm
	 * @return
	 */
	public Map groupByPatParm(TParm parm) {
		Map result = new HashMap();
		if (parm == null) {
			return null;
		}
		int count = parm.getCount();
		if (count < 1) {
			return null;
		}
		TParm temp = new TParm();
		String[] names = parm.getNames();
		if (names == null) {
			return null;
		}
		if (names.length < 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (String name : names) {
			sb.append(name).append(";");
		}
		try {
			sb.replace(sb.lastIndexOf(";"), sb.length(), "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		TParm tranParm = new TParm();
		for (int i = 0; i < count; i++) {
			String orderNo = parm.getValue("CASE_NO", i);
			if (result.get(orderNo) == null) {
				temp = new TParm();
				temp.addRowData(parm, i, sb.toString());
				result.put(orderNo, temp);
			} else {
				tranParm = (TParm) result.get(orderNo);
				tranParm.addRowData(parm, i, sb.toString());
				result.put(orderNo, tranParm);
			}
		}
		return result;
	}

	/**
	 * ִ��
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUndoExec(TParm parm, TConnection connection) {
		bilpoint = (String) OdiMainTool.getInstance().getOdiSysParmData(
		"BIL_POINT");
		TParm result = new TParm();
		// ǰ̨��������
		int count = parm.getCount();
		int ibsRows = 0;
		TParm forIBSParm = new TParm();
		boolean isOrNotForIBS = true;
		for (int i = 0; i < count; i++) {
			// ȡ������
			String caseNo = parm.getValue("CASE_NO", i);
			String orderNo = parm.getValue("ORDER_NO", i);
			String orderSeq = parm.getValue("ORDER_SEQ", i);
			String setMainFlg = parm.getValue("SETMAIN_FLG", i);
			String orderSerGroup = parm.getValue("ORDERSET_GROUP_NO", i);
			String startDttm = parm.getValue("START_DTTM", i);
			String endDttm = parm.getValue("END_DTTM", i);
			// ����ǰ̨��CASE_NO,ORDER_NO,ORDER_SEQ�ҳ�һ��ҽ��������SETMAIN_FLG�����Ƿ�Ϊ����ҽ��
			TParm orderPram = getAnOrder(caseNo, orderNo, orderSeq, setMainFlg,
					orderSerGroup, startDttm, endDttm);
			for (int j = 0; j < orderPram.getCount(); j++) {
				TParm execData = new TParm();
				execData.setData("CASE_NO", orderPram.getData("CASE_NO", j));
				execData.setData("ORDER_NO", orderPram.getData("ORDER_NO", j));
				execData
						.setData("ORDER_SEQ", orderPram.getData("ORDER_SEQ", j));
				String dcDate = parm.getData("DC_DATE", i) + "";
				// ��DCʱ���ʱ����ȡ��ִ��DC����ô����Ҫ�����˷ѽӿ�
				isOrNotForIBS = "null".equals(dcDate);
				// û��DCʱ�䡪��ȡ��ִ�� ��DCʱ�䡪��ȡ��DC
				execData.setData("NS_EXEC_CODE",
						"null".equals(dcDate) ? new TNull(String.class) : parm
								.getData("NS_EXEC_CODE", i));
				execData.setData("NS_EXEC_DATE",
						"null".equals(dcDate) ? new TNull(Timestamp.class)
								: parm.getData("NS_EXEC_DATE", i));
				execData.setData("NS_EXEC_DC_CODE", new TNull(String.class));
				execData.setData("NS_EXEC_DC_DATE", new TNull(Timestamp.class));
				execData.setData("OPT_USER", parm.getData("OPT_USER", i));
				execData.setData("OPT_TERM", parm.getData("OPT_TERM", i));
				execData.setData("OPT_DATE", parm.getData("OPT_DATE", i));
				execData.setData("START_DTTM", orderPram.getData("START_DTTM",
						j));
				execData.setData("END_DTTM", orderPram.getData("END_DTTM", j));
				execData.setData("EXEC_NOTE", "");
				if(orderPram.getValue("CAT1_TYPE", j).equals("PHA")&&this.isPSPhaRoute(orderPram.getValue("ROUTE_CODE", j))){
					execData.setData("SKIN_RESULT", "");
					result = InwForOdiTool.getInstance().updateOdiDspndForExecSkin(
							execData, connection);
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						return result;
					}
					result = InwForOdiTool.getInstance().updateOdiOrderForSkin(
							execData, connection);
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						return result;
					}
				}else{
					// �õ���ʿ��ע����ODI_DSPND
					result = InwForOdiTool.getInstance().updateOdiDspndForExec1(
							execData, connection);
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						return result;
					}
					result = InwForOdiTool.getInstance().updateOdiOrderForNote(
							execData, connection);
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						return result;
					}
				}
				result = InwForOdiTool.getInstance().updateOdiDspnmForExec(
						execData, connection);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
				// ������ODI��󣬴���IBS�����Ʒ�
				String cat1Type = (String) orderPram.getData("CAT1_TYPE", j);
				// ��PHA����Ҫִ�мƷ� �󶨷��õ���Ŀ��Ҫִ�мƷ�
				if (!"PHA".equals(cat1Type) && this.bilpoint.equals("1")) {
					forIBSParm.addData("CASE_NO", orderPram.getData("CASE_NO",
							j));
					forIBSParm.addData("ORDER_NO", orderPram.getData(
							"ORDER_NO", j));
					forIBSParm.addData("ORDER_SEQ", orderPram.getData(
							"ORDER_SEQ", j));
					forIBSParm.addData("START_DTTM", orderPram.getData(
							"START_DTTM", j));
					// ������
					ibsRows++;
				} else if (!"PHA".equals(cat1Type) && !"LIS".equals(cat1Type)
						&& !"RIS".equals(cat1Type) && this.bilpoint.equals("2")) {
					forIBSParm.addData("CASE_NO", orderPram.getData("CASE_NO",
							j));
					forIBSParm.addData("ORDER_NO", orderPram.getData(
							"ORDER_NO", j));
					forIBSParm.addData("ORDER_SEQ", orderPram.getData(
							"ORDER_SEQ", j));
					forIBSParm.addData("START_DTTM", orderPram.getData(
							"START_DTTM", j));
					// ������
					ibsRows++;
				}
			}
		}
		TParm orderToIbs = null;
		// һ�εõ�������Ҫ�Ʒѵ�����
		for (int i = 0; i < ibsRows; i++) {
			TParm date = getUnAnOrder(forIBSParm.getData("CASE_NO", i) + "",
					forIBSParm.getData("ORDER_NO", i) + "", forIBSParm.getData(
							"ORDER_SEQ", i)
							+ "", forIBSParm.getData("START_DTTM", i) + "");
			if (date.getCount() <= 0)
				continue;
			if (orderToIbs == null)
				orderToIbs = date;
			else
				orderToIbs.addParm(date);
		}
		if (orderToIbs != null && isOrNotForIBS) {
			orderToIbs.setCount(ibsRows);
			result.setData("FORIBS", orderToIbs);
		}

		return result;
	}

	/**
	 * ����caseNo�õ��ò��˵ĳ��ڴ��� PS:����֧�ֶಡ����˵�ʱ��ÿ��ֻ��һ��caseNo
	 */
	private TParm getUnAnOrder(String caseNo, String orderNo, String orderSeq,
			String startDttm) {
		TParm result = new TParm();
		String startDttmSQL = "";
		if (startDttm.length() != 0)
			startDttmSQL = " AND START_DTTM = '" + startDttm + "'";
		String SelSql = "";
		SelSql = "SELECT * FROM ODI_DSPNM WHERE CASE_NO='" + caseNo
				+ "' AND ORDER_NO='" + orderNo + "' AND ORDER_SEQ='" + orderSeq
				+ "'" + startDttmSQL
				+ " AND (BILL_FLG='Y' AND NS_EXEC_CODE IS NOT NULL)";// shibl
		// 20130123
		// add
		// �����������ҽ����ѯδ�շ�
		// �õ��ò������и�ִ��չ���Ĵ���
		result = new TParm(TJDODBTool.getInstance().select(SelSql));
		return result;
	}

	/**
	 * ����BAR_CODE
	 * 
	 * @param parm
	 * @return
	 */
	public TParm GeneratIFBarcode(TParm parm) {
		TParm result = new TParm();
		// ǰ̨��������
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			TParm execData = new TParm();
			execData.setData("CASE_NO", parm.getData("CASE_NO", i));
			execData.setData("ORDER_NO", parm.getData("ORDER_NO", i));
			execData.setData("ORDER_SEQ", parm.getData("ORDER_SEQ", i));
			execData.setData("ORDER_DATE", parm.getData("ORDER_DATE", i));
			execData.setData("ORDER_DATETIME", parm
					.getData("ORDER_DATETIME", i));
			execData.setData("BAR_CODE", parm.getData("BAR_CODE", i));
			execData.setData("OPT_USER", parm.getData("OPT_USER", i));
			execData.setData("OPT_TERM", parm.getData("OPT_TERM", i));
			execData.setData("OPT_DATE", parm.getData("OPT_DATE", i));
			result = InwForOdiTool.getInstance()
					.updateOdidspnDBarCode(execData);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * ����caseNo�õ��ò��˵ĳ��ڴ��� PS:����֧�ֶಡ����˵�ʱ��ÿ��ֻ��һ��caseNo
	 */
	private TParm getAnOrder(String caseNo, String orderNo, String orderSeq,
			String startDttm) {
		TParm result = new TParm();
		String startDttmSQL = "";
		if (startDttm.length() != 0)
			startDttmSQL = " AND START_DTTM = '" + startDttm + "'";
		String SelSql = "";
		SelSql = "SELECT * FROM ODI_DSPNM WHERE CASE_NO='"
				+ caseNo
				+ "' AND ORDER_NO='"
				+ orderNo
				+ "' AND ORDER_SEQ='"
				+ orderSeq
				+ "'"
				+ startDttmSQL
				+ " AND (((BILL_FLG='N' OR BILL_FLG IS NULL) AND DC_DATE IS NULL) "
				+ "OR (BILL_FLG='Y' AND DC_DATE IS NOT NULL AND NS_EXEC_DC_CODE IS NULL))";// shibl
		// 20130105
		// add
		// �����������ҽ����ѯδ�շ�
		// �õ��ò������и�ִ��չ���Ĵ���
		result = new TParm(TJDODBTool.getInstance().select(SelSql));
		return result;
	}

	/**
	 * ִ��
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateOdiDspnmByIBS(TParm parm, TConnection connection) {
		TParm result = new TParm();
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			result = InwForOdiTool.getInstance().updateOdiDspnmByIBS(
					parm.getRow(i), connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}

		return result;
	}

	/**
	 * ִ��
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateOdiDspndByIBS(TParm parm, TConnection connection) {
		TParm result = new TParm();
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			result = InwForOdiTool.getInstance().updateOdiDspndByIBS(
					parm.getRow(i), connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}

		}
		return result;
	}

	/**
	 * �õ���ҽ����SYS_FEE�����õ���Ҷ���������
	 * 
	 * @param orderCode
	 *            String
	 * @return String
	 */
	private TParm getActionName(String orderCode) {
		String sql = " SELECT ACTION_CODE " + " FROM SYS_FEE "
				+ " WHERE ORDER_CODE='" + orderCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() == 0)
			return null;
		return result;
	}

	/**
	 * �õ���ҽ����SYS_FEE�����õ���Ҷ���������
	 * 
	 * @param orderCode
	 *            String
	 * @return String
	 */
	public TParm getDCOrder(TParm parm) {
		String dCDate = ("" + parm.getData("DC_DATE")).replace("-", "");
		dCDate = dCDate.replace(" ", "");
		dCDate = dCDate.replace(":", "");
		dCDate = dCDate.substring(0, 14);
		String SQL = 
				//modify by yangjj 20150610
				" SELECT SUM(MEDI_QTY) DC_QYT" + " FROM ODI_DSPND"
				//" SELECT COUNT(CASE_NO) DC_QYT" + " FROM ODI_DSPND"
				
				+ " WHERE CASE_NO = '" + parm.getValue("CASE_NO") + "'"
				+ " AND   ORDER_NO = '" + parm.getValue("ORDER_NO") + "'"
				+ " AND   ORDER_SEQ = '" + parm.getValue("ORDER_SEQ") + "'"
				+ " AND   (ORDER_DATE || ORDER_DATETIME) BETWEEN '"
				+ parm.getValue("START_DTTM") + "' "
				+ "                                      AND     '"
				+ parm.getValue("END_DTTM") + "'"
				+ " AND   (ORDER_DATE || ORDER_DATETIME) >= '" + dCDate + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL));
		
		return result;
	}
	/**
	 * �Ƿ�ΪƤ���÷�
	 * @param code
	 * @return
	 */
	public boolean isPSPhaRoute(String code){
		if(code.equals("")){
			return false;
		}
		String sql="SELECT PS_FLG FROM SYS_PHAROUTE WHERE ROUTE_CODE='"+code+"'";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()<0){
			return false;
		}
		return parm.getBoolean("PS_FLG", 0);
	}
	/**
	 * ���벹��ҽ�� add caoyong 20131107
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getInsetDetailed(TParm parm) {

		TParm result = new TParm();
		// ǰ̨��������

		result = InwForOdiTool.getInstance().getInsetDetailedAll(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
