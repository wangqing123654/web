package jdo.opd;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.med.MEDApplyTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.StringUtil;
import jdo.bil.BilInvoice;
/**
 * 
 * <p>
 * Title: ҽ��tool
 * 
 * <p>
 * Description: ҽ��tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * 
 * @author ehui 20080911
 * @version 1.0
 */
public class OrderTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static OrderTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return OrderTool
	 */
	public static OrderTool getInstance() {
		if (instanceObject == null)
			instanceObject = new OrderTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public OrderTool() {
		setModuleName("opd\\OPDOrderModule.x");

		onInit();
	}

	/**
	 * ����ҽ��
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertdata(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("insertdata", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ҽ��(For OPB)
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertdataForOPB(TParm parm, TConnection connection) {
		TParm result = new TParm();
		parm.setData("RX_TYPE", "7");
		parm.setData("OWN_AMT", StringTool.round(parm.getDouble("DOSAGE_QTY")*parm.getDouble("OWN_PRICE"),2));//====pangben 2013-8-30 ����ۿ۲����Էѽ���޸�
		result = update("insertdataForOPB", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ҽ��(For OPB)
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertdataForOPBEKT(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("insertdataForOPBEKT", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �ж��Ƿ��������
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public boolean existsOrder(TParm parm) {
		return getResultInt(query("existsOrder", parm), "COUNT") > 0;
	}

	/**
	 * ��������
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updatedata(TParm parm, TConnection connection) {
		TParm result = new TParm();
		if ("N".equals(parm.getValue("BILL_FLG"))) {
			parm.setData("BILL_DATE", new TNull(Timestamp.class));
			parm.setData("RECEIPT_FLG", "N");// �˷ѽ������޸�
		}
		parm.setData("UPDATE_TIME", SystemTool.getInstance().getUpdateTime()); //add by huangtt 20170323 huangtt
		result = update("updatedata", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��������(For�����շ�)
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm upForOPB(TParm parm, TConnection connection) {
		TParm result = new TParm();
		if ("N".equals(parm.getValue("BILL_FLG")))
			parm.setData("BILL_DATE", new TNull(Timestamp.class));
		result = update("upForOPB", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ɾ������
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm deletedata(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("deletedata", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��������
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public TParm query(String caseNo) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm data = query("query", parm);
		if (data.getErrCode() < 0) {
			err("Order+ERR:" + data.getErrCode() + data.getErrText()
					+ data.getErrName());
			return data;
		}
		TParm result = new TParm();

		int count = data.getCount();
		TParm groupParm = null;
		TParm orderList = null;
		String oldRxType = "";
		String oldRxNo = "";
		for (int i = 0; i < count; i++) {
			// ��������
			String rxType = data.getValue("RX_TYPE", i);
			if (!rxType.equalsIgnoreCase(oldRxType)) {
				groupParm = new TParm();
				groupParm.setData("NAME", rxType);
				result.addData("GROUP", groupParm.getData());
				result.setData("ACTION", "COUNT", result.getCount("GROUP"));
				oldRxType = rxType;
				oldRxNo = "";
			}
			// ������
			String rxNo = data.getValue("RX_NO", i);
			if (!rxNo.equalsIgnoreCase(oldRxNo)) {
				orderList = new TParm();
				groupParm.addData("LIST", orderList.getData());
				groupParm
						.setData("ACTION", "COUNT", groupParm.getCount("LIST"));
				oldRxNo = rxNo;
			}
			int row = orderList.insertRow(-1, StringTool.getString(data
					.getNames(), ";"));
			orderList.setRowData(row, data, i);
			orderList.setData("ACTION", "COUNT", row + 1);
		}
		return result;
	}

	/**
	 * ΪPHAר�õļ���������parm �еĲ���Ϊ
	 * 
	 * @param parm
	 *            TParm
	 * @return result TParm
	 */
	public TParm queryForPHA(TParm parm) {
		TParm data = query("selectdataforPha", parm);
		if (data.getErrCode() < 0) {
			err("ERR:" + data.getErrCode() + data.getErrText()
					+ data.getErrName());
			return data;
		}
		
		//add by huangtt 20171220 #6071 ������һ����ҩƷ������һ��ҩƷδ��ӡ ������������Ӧ�ò���� start

		List<String> seqList = new ArrayList<String>();
		for (int i = 0; i < data.getCount(); i++) {
			String aa = data.getValue("CASE_NO", i)+"#"+data.getValue("RX_PRESRT_NO", i);
			if(!seqList.contains(aa)){
				seqList.add(aa);
			}
		}
		
		TParm dataSel = new TParm();
		for (int i = 0; i < seqList.size(); i++) {
			String aa[] = seqList.get(i).split("#");
			String sql = "SELECT COUNT(CASE_NO) COUNT FROM OPD_ORDER " +
					"WHERE (MED_PRINT_FLG IS NULL OR MED_PRINT_FLG='N' ) " +
					"AND CASE_NO = '"+aa[0]+"' " +
					"AND RX_NO||PRESRT_NO='"+aa[1]+"'";
			TParm parmC = new TParm(TJDODBTool.getInstance().select(sql));
			if(parmC.getInt("COUNT", 0) == 0){
				for (int j = 0; j < data.getCount(); j++) {
					if(data.getValue("CASE_NO", j).equals(aa[0]) && 
							data.getValue("RX_PRESRT_NO", j).equals(aa[1])){
						dataSel.addRowData(data, j);
					}
				}
			}
			
		}
		dataSel.setCount(dataSel.getCount("CASE_NO"));
		data = dataSel;
		
		//add by huangtt 20171220  #6071 ������һ����ҩƷ������һ��ҩƷδ��ӡ ������������Ӧ�ò����   end
		
		TParm result = new TParm();
		
		int count = data.getCount();
		TParm orderList = null;
		String oldRxNo = "";
		String oldPresrtNo = ""; //add by huangtt 20150120 
		for (int i = 0; i < count; i++) {
			// ������
			String rxNo = data.getValue("RX_NO", i);
			String presrtNo = data.getValue("PRESRT_NO", i); //add by huangtt 20150120
			if (!rxNo.equalsIgnoreCase(oldRxNo)
					|| !presrtNo.equalsIgnoreCase(oldPresrtNo)) { //add by huangtt 20150120
				orderList = new TParm();
				result.addData("LIST", orderList.getData());
				result.setData("ACTION", "COUNT", orderList.getCount("LIST"));
				oldRxNo = rxNo;
				oldPresrtNo = presrtNo; //add by huangtt 20150120
			}
			int row = orderList.insertRow(-1, StringTool.getString(data
					.getNames(), ";"));
			orderList.setRowData(row, data, i);
			orderList.setData("ACTION", "COUNT", row + 1);
		}

		return result;
	}

	/**
	 * ΪPHAר����ҩ�ļ���������parm �еĲ���Ϊ
	 * 
	 * @param parm
	 *            TParm
	 * @return result TParm
	 */
	public TParm queryForPhaReturn(TParm parm) {
		TParm data = query("selectdataforPhaReturn", parm);
		if (data.getErrCode() < 0) {
			err("ERR:" + data.getErrCode() + data.getErrText()
					+ data.getErrName());
			return data;
		}
		TParm result = new TParm();

		int count = data.getCount();
		TParm orderList = null;
		String oldRxNo = "";
		for (int i = 0; i < count; i++) {
			// ������
			String rxNo = data.getValue("RX_NO", i);
			if (!rxNo.equalsIgnoreCase(oldRxNo)) {
				orderList = new TParm();
				result.addData("LIST", orderList.getData());
				result.setData("ACTION", "COUNT", orderList.getCount("LIST"));
				oldRxNo = rxNo;
			}
			int row = orderList.insertRow(-1, StringTool.getString(data
					.getNames(), ";"));
			orderList.setRowData(row, data, i);
			orderList.setData("ACTION", "COUNT", row + 1);
		}

		return result;
	}

	/**
	 * ɾ��
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onDelete(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		TParm resulthistory = new TParm();
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.deletedata(inParm, connection);
			if (result.getErrCode() < 0)
				return result;
		}

		return result;
	}

	/**
	 * ����
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onInsert(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		parm = getAppleNo(parm);
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.insertdata(inParm, connection);
			if (result.getErrCode() < 0)
				return result;
		}

		result = MEDApplyTool.getInstance().insertMedApply(parm, connection);
		if (result.getErrCode() < 0)
			return result;
		return result;
	}

	/**
	 * ����(For OPB)
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onInsertForOPB(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		parm = getAppleNo(parm);
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.insertdataForOPB(inParm, connection);
			if (result.getErrCode() < 0)
				return result;
		}

		result = MEDApplyTool.getInstance().insertMedApply(parm, connection);
		if (result.getErrCode() < 0)
			return result;
		return result;
	}

	/**
	 * ҽ�ƿ�ɾ��ҽ��������������ʹ��
	 * 
	 * @param parm
	 * @param connection
	 * @return ==============pangben 2012-3-7
	 */
	public TParm onInsertForOpbEkt(TParm parm, TConnection connection) {
		int count = parm.getCount("ORDER_CODE");
		TParm result = new TParm();
		parm = getAppleNo(parm);
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.insertdataForOPBEKT(inParm, connection);
			if (result.getErrCode() < 0)
				return result;
		}
		//========d=pangben 2012-6-6 �޸�����ҽ��վ�Ѿ��շ�ҽ�ƿ�����ɾ��������ִ�����MED_APPLY������
		if (null != parm.getValue("MED_FLG")
				&& parm.getValue("MED_FLG").equals("Y")) {

		} else {
			result = MEDApplyTool.getInstance()
					.insertMedApply(parm, connection);
			if (result.getErrCode() < 0)
				return result;
		}
		return result;

	}

	public TParm getAppleNo(TParm parm) {
		Map labMap = new HashMap();
		Map risNoMap = new HashMap();
		String labNo = "";
		String risNo = "";
		int count = parm.getCount("CASE_NO");
		for (int i = 0; i < count; i++) {
			if (parm.getValue("CAT1_TYPE", i).equals("LIS")
					&& parm.getBoolean("SETMAIN_FLG", i)) {
				String labMapKey = parm.getValue("DEV_CODE", i)
						+ parm.getValue("OPTITEM_CODE", i)
						+ parm.getValue("RPTTYPE_CODE", i);
				if (labMap.get(labMapKey) != null) {
					parm.setData("MED_APPLY_NO", i, labMap.get(labMapKey));
					// ������ҽ��ϸ������
					parm = setOrderSetListLabNo(parm, parm.getValue(
							"ORDERSET_CODE", i), parm.getValue(
							"ORDERSET_GROUP_NO", i), labMap.get(labMapKey)
							.toString());
					continue;
				}
				labNo = SystemTool.getInstance().getNo("ALL", "MED", "LABNO",
						"LABNO");
				// �����µ�LAB_NO
				labMap.put(labMapKey, labNo);
				parm.setData("MED_APPLY_NO", i, labNo);
				// ������ҽ��ϸ������
				parm = setOrderSetListLabNo(parm, parm.getValue(
						"ORDERSET_CODE", i), parm.getValue("ORDERSET_GROUP_NO",
						i), labNo);
			}
			if (parm.getValue("CAT1_TYPE", i).equals("RIS")
					&& parm.getBoolean("SETMAIN_FLG", i)) {
				String risMapKey = parm.getValue("ORDERSET_CODE", i)
						+ parm.getValue("ORDERSET_GROUP_NO", i);
				// ����о͸���ǰLISҽ����ֵLAB_NO
				if (risNoMap.get(risMapKey) != null) {
					continue;
				}
				risNo = SystemTool.getInstance().getNo("ALL", "MED", "LABNO",
						"LABNO");
				// �����µ�LAB_NO
				risNoMap.put(risMapKey, risNo);
				parm.setData("MED_APPLY_NO", i, risNo);
				// ������ҽ��ϸ������
				parm = setOrderSetListLabNo(parm, parm.getValue(
						"ORDERSET_CODE", i), parm.getValue("ORDERSET_GROUP_NO",
						i), risNo);
			}
		}
		return parm;
	}

	/**
	 * ������ҽ��ϸ�ֵ
	 * 
	 * @param parm
	 *            TParm
	 * @param orderSetCode
	 *            String
	 * @param groupNo
	 *            String
	 * @param labNo
	 *            String
	 * @return TParm
	 */
	public TParm setOrderSetListLabNo(TParm parm, String orderSetCode,
			String groupNo, String labNo) {
		int count = parm.getCount("CASE_NO");
		for (int i = 0; i < count; i++) {
			String risMapKey = parm.getValue("ORDERSET_CODE", i)
					+ parm.getValue("ORDERSET_GROUP_NO", i);
			// ��ͬ�ļ���ҽ����ֵ
			if (risMapKey.equals(orderSetCode + groupNo)) {
				// �����ų�
				if (parm.getBoolean("SETMAIN_FLG", i))
					continue;
				parm.setData("MED_APPLY_NO", i, labNo);
			}
		}
		return parm;
	}

	/**
	 * ����
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdate(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.updatedata(inParm, connection);
			if (result.getErrCode() < 0)
				return result;
			// System.out.println("OPB����med����"+inParm);
			result = MEDApplyTool.getInstance()
					.updateStauts(inParm.getValue("CASE_NO"),
							inParm.getValue("MED_APPLY_NO"),
							inParm.getValue("RX_NO"),
							inParm.getValue("SEQ_NO"), parm.getValue("FLG"),
							connection);
			if (result.getErrCode() < 0)
				return result;
		}
		return result;
	}
	
	public TParm checkUpdate(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
//			System.out.println("inParm----"+inParm);
			result = query("checkUpdateOpdOrder",inParm);
			if(result.getCount() == 0){
				connection.commit();
				result.setErrCode(-2);
				result.setErrText("�����ѱ�����������Ա���£���ˢ�����²���");
				break;
				
			}
			
		}
		return result;
	}
	

	/**
	 * ����forOPB
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdateOPB(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.upForOPB(inParm, connection);
			if (result.getErrCode() < 0)
				return result;
			// System.out.println("OPB����med����"+inParm);
			result = MEDApplyTool.getInstance()
					.updateStauts(inParm.getValue("CASE_NO"),
							inParm.getValue("MED_APPLY_NO"),
							inParm.getValue("RX_NO"),
							inParm.getValue("SEQ_NO"), parm.getValue("FLG"),
							connection);
			if (result.getErrCode() < 0)
				return result;
		}
		return result;
	}

	/**
	 * odo�춯�����
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onSave(TParm parm, TConnection connection) {
		TParm result = onDelete(parm.getParm(OrderList.DELETED), connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = onInsert(parm.getParm(OrderList.NEW), connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		TParm upParm = parm.getParm(OrderList.MODIFIED);
		upParm.setData("FLG", parm.getData("FLG"));
		result = onUpdate(upParm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * odo�춯�����(For OPB)
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onSaveOPB(TParm parm, TConnection connection) {
		TParm result = onDelete(parm.getParm(OrderList.DELETED), connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = onInsertForOPB(parm.getParm(OrderList.NEW), connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		TParm upParm = parm.getParm(OrderList.MODIFIED);
		upParm.setData("FLG", parm.getData("FLG"));
		
		//add by huangtt 20170322 У��ҽ���Ƿ��ѱ��޸� start
		
		result = checkUpdate(upParm, connection);
		if(result.getErrCode() < 0){
			err("ERR:" + result.getErrCode() + result.getErrText());
			return result;
		}
		//add by huangtt 20170322 У��ҽ���Ƿ��ѱ��޸� end
		
		result = onUpdate(upParm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * odo�춯�����(�����շѱ���)
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onOPBSave(TParm parm, TConnection connection) {
		TParm result = onDelete(parm.getParm(OrderList.DELETED), connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = onInsert(parm.getParm(OrderList.NEW), connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		TParm upParm = parm.getParm(OrderList.MODIFIED);
		upParm.setData("FLG", parm.getData("FLG"));
		result = onUpdateOPB(upParm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �������ҹ�����ͳ�Ʊ�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOpenDeptList(TParm parm) {
		TParm result = query("selectOpenDeptList", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * Ƥ��ҽ����ѯ �����ﻤʿվƤ��ִ�� ʹ�ã�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectPS(TParm parm) {
		TParm result = this.query("selectPS", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ʿִ�У����ﻤʿվ Ƥ��ִ�У�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatePS(TParm parm) {
		TParm result = this.update("updatePS", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯȫ�ֶ�����(�����շ�ҽ�ƿ���ӡ)��û���շ�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selDataForOPBEKT(TParm parm) {
		TParm result = query("selDataForOPBEKT", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯȫ�ֶ�����(�����շ�ҽ�ƿ���ӡ)���Ѿ��շ�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selDataForOPBEKTC(TParm parm) {
		TParm result = query("selDataForOPBEKTC", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * �ײʹ�Ʊʹ��
	 */
	public TParm selDataForMemPrint(TParm parm,BilInvoice bilInvoice){
		String sql =
				" SELECT A.CASE_NO,A.RX_NO,A.SEQ_NO,A.REGION_CODE," +
				" A.ORDER_CODE,A.MEDI_QTY," +
				" A.MEDI_UNIT,A.FREQ_CODE,A.ROUTE_CODE,A.TAKE_DAYS,A.DOSAGE_QTY," +
				" A.DISPENSE_QTY,A.DISPENSE_UNIT,A.GIVEBOX_FLG,A.OWN_PRICE,A.NHI_PRICE," +
				" A.DISCOUNT_RATE,A.OWN_AMT,A.AR_AMT,A.DR_NOTE,A.NS_NOTE," +
				" A.DR_CODE,A.ORDER_DATE,A.DEPT_CODE,A.DC_DR_CODE,A.DC_ORDER_DATE," +
				" A.DC_DEPT_CODE,A.EXEC_DEPT_CODE,A.SETMAIN_FLG,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE," +
				" A.HIDE_FLG,A.RECEIPT_NO,A.BILL_FLG,A.BILL_DATE," +
				" A.BILL_USER,A.PRINT_FLG,A.REXP_CODE,A.HEXP_CODE," +
				" A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.PHA_CHECK_CODE,A.PHA_CHECK_DATE," +
				" A.PHA_DOSAGE_CODE,A.PHA_DOSAGE_DATE,A.PHA_DISPENSE_CODE,A.PHA_DISPENSE_DATE,A.NS_EXEC_CODE," +
				" A.NS_EXEC_DATE,A.NS_EXEC_DEPT,A.DCTAGENT_CODE,A.DCTEXCEP_CODE,A.DCT_TAKE_QTY," +
				" A.PACKAGE_TOT,A.AGENCY_ORG_CODE,A.DCTAGENT_FLG,A.DECOCT_CODE,A.EXEC_FLG,A.RECEIPT_FLG,A.BILL_TYPE," +
				" C.NHI_CODE_O,C.NHI_CODE_E,C.NHI_CODE_I,B.DOSE_CODE,A.BUSINESS_NO,A.MEM_PACKAGE_ID,D.TRADE_NO " +  //add by huangtt 20141211 A.MEM_PACKAGE_ID
				" FROM OPD_ORDER A,PHA_BASE B,SYS_FEE C,MEM_PAT_PACKAGE_SECTION_D D " +
				" WHERE A.ORDER_CODE = B.ORDER_CODE(+) " +
				" AND A.ORDER_CODE = C.ORDER_CODE(+) " +
				" AND A.CASE_NO=D.CASE_NO AND A.MEM_PACKAGE_ID = D.ID "+
				" AND A.RELEASE_FLG <> 'Y' " +
				" AND A.RECEIPT_NO IS NULL " +
				" AND (A.PRINT_FLG IS NULL OR A.PRINT_FLG ='N' OR A.PRINT_FLG ='') " +
				" AND A.MEM_PACKAGE_ID IS NOT NULL" +
				" AND A.CASE_NO = '" + parm.getValue("CASE_NO") + "'" +
				" ORDER BY D.TRADE_NO";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		TParm bilExeParm = new TParm();
		TParm bilOpbRecpList = new TParm();
		TParm exeOrderList = new TParm();
		TParm bilInvrcpList = new TParm();
		TParm exeOrderParm = null;
		TParm bilRecpParm = null;
		TParm bilInvrcpParm = null;
		int index =0;
		String printNo = bilInvoice.getUpdateNo();
		if(result.getCount()>0){
			String tradeNo = result.getValue("TRADE_NO",0);
			exeOrderParm = new TParm();
			for(int i=0; i< result.getCount(); i++){
				if(tradeNo.equals(result.getValue("TRADE_NO",i))){
					exeOrderParm.setRowData(index,result,i);
					index++;
				}else{
					index=0;
					tradeNo = result.getValue("TRADE_NO",i);
					exeOrderParm.setCount(exeOrderParm.getCount("CASE_NO"));
					bilRecpParm = getBilOpbRecp(exeOrderParm,parm,parm.getValue("OPT_USER"),
							parm.getValue("OPT_TERM"),printNo,"Y");
					bilInvrcpParm = getBilInvrcp(bilRecpParm,parm.getValue("OPT_USER"),
							parm.getValue("OPT_TERM"),"Y");
					printNo = StringTool.addString(printNo);
					exeOrderList.addData("exeOrderParm",exeOrderParm.getData());
					bilOpbRecpList.addData("bilRecpParm",bilRecpParm.getData());
					bilInvrcpList.addData("bilInvrcpParm",bilInvrcpParm.getData());
					exeOrderParm = new TParm();
					exeOrderParm.setRowData(index,result,i);
					index++;
				}
				if(i == result.getCount()-1){
					exeOrderParm.setCount(exeOrderParm.getCount("CASE_NO"));
					exeOrderList.addData("exeOrderParm",exeOrderParm.getData());
					bilRecpParm = getBilOpbRecp(exeOrderParm,parm,parm.getValue("OPT_USER"),
							parm.getValue("OPT_TERM"),printNo,"Y");
					bilInvrcpParm = getBilInvrcp(bilRecpParm,parm.getValue("OPT_USER"),
							parm.getValue("OPT_TERM"),"Y");
					bilOpbRecpList.addData("bilRecpParm",bilRecpParm.getData());
					bilInvrcpList.addData("bilInvrcpParm",bilInvrcpParm.getData());
					printNo = StringTool.addString(printNo);
				}
			}
		}
		bilExeParm.setData("bilinvoiceParm",getBilInvoiceParm(bilInvoice,parm.getValue("OPT_USER"),
				parm.getValue("OPT_TERM"),printNo).getData());
		bilExeParm.setData("exeOrderList",exeOrderList.getData());
		bilExeParm.setData("bilInvrcpList",bilInvrcpList.getData());
		bilExeParm.setData("bilOpbRecpList",bilOpbRecpList.getData());
		return bilExeParm;
	}
	/**
	 * Ʊ���޸�����
	 * @param parm
	 * @param optUser
	 * @param optTerm
	 * @return
	 */
	private TParm getBilInvoiceParm(BilInvoice bilInvoice,String optUser,String optTerm,String updateNo){
		TParm bilInvoiceParm = new TParm();
		bilInvoiceParm.setData("RECP_TYPE", "OPB");
		bilInvoiceParm.setData("STATUS", "0");
		bilInvoiceParm.setData("CASHIER_CODE", optUser);
		bilInvoiceParm.setData("START_INVNO", bilInvoice.getStartInvno());
		bilInvoiceParm.setData("UPDATE_NO",updateNo);
        return bilInvoiceParm;
	}
	/**
	 * Ʊ������
	 * @param bilOpbRecpParm
	 * @return
	 */
	private TParm getBilInvrcp(TParm bilOpbRecpParm,String optUser,String optTerm,String memPackFlg){
		TParm bilInvricpt = new TParm();
        bilInvricpt.setData("RECP_TYPE", "OPB"); // ҽ�ƿ���Ʊ
        bilInvricpt.setData("RECEIPT_NO",bilOpbRecpParm.getValue("RECEIPT_NO"));
        bilInvricpt.setData("CANCEL_USER", new TNull(String.class));
        bilInvricpt.setData("CASHIER_CODE",optUser);
        bilInvricpt.setData("OPT_USER", optUser);
        bilInvricpt.setData("INV_NO", bilOpbRecpParm.getData("PRINT_NO"));
        bilInvricpt.setData("OPT_TERM", optTerm);
        bilInvricpt.setData("PRINT_USER", optUser);
        bilInvricpt.setData("CANCEL_DATE", new TNull(Timestamp.class));
        if(memPackFlg.equals("Y")){
        	bilInvricpt.setData("TOT_AMT",0.00);
            bilInvricpt.setData("AR_AMT", 0.00);
        }else{
        	bilInvricpt.setData("TOT_AMT",bilOpbRecpParm.getDouble("TOT_AMT"));
            bilInvricpt.setData("AR_AMT", bilOpbRecpParm.getDouble("TOT_AMT"));
        }
        bilInvricpt.setData("CANCEL_FLG", "0");
        bilInvricpt.setData("STATUS", "0");
        bilInvricpt.setData("ADM_TYPE", bilOpbRecpParm.getValue("ADM_TYPE")); //=====pangben 2012-3-19
        return bilInvricpt;
	}
	/**
	 * ���������������ã�
	 * @param exeOrderList
	 * @return
	 */
	private TParm getBilOpbRecp(TParm exeOrderList,TParm parm,
			String optUser,String optTerm,String printNo,String memPackFlg){
		TParm bilOpbRecpParm = new TParm();
      //  double[] sumAmt = chargeDouble(opdParm, chargeDouble, 2,parm.getValue("CASE_NO"));
        String sql = "SELECT CHARGE01, CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07,"
                + " CHARGE08, CHARGE09, CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14,CHARGE15, "
                + " CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24, "
                + " CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29,CHARGE30 "
                + " FROM BIL_RECPPARM WHERE ADM_TYPE ='O'";
        TParm bilRecpParm = new TParm(TJDODBTool.getInstance().select(sql));
        String chargeTemp = "CHARGE";
        bilRecpParm = bilRecpParm.getRow(0);
        double[] chargeAmt = new double[30];
		String[] chargeName = new String[30];
		for (int i = 0; i < chargeName.length; i++) {
			chargeAmt[i] = 0.00;
			if (i < 9) {
				chargeName[i] = isNull(bilRecpParm.getData(chargeTemp + "0" + (i + 1))) ? ""
						: bilRecpParm.getValue(chargeTemp + "0" + (i + 1)).toString();
			} else {
				chargeName[i] = isNull(bilRecpParm.getData(chargeTemp + (i + 1))) ? ""
						: bilRecpParm.getValue(chargeTemp + (i + 1)).toString();
			}
		}
		double totAmt = 0.00;
		for (int i = 0; i < exeOrderList.getCount("REXP_CODE"); i++) {
			for (int j = 0; j < chargeName.length; j++) {
				if (exeOrderList.getValue("REXP_CODE",i).equals(chargeName[j])) {
					chargeAmt[j] +=Double.parseDouble(exeOrderList.getValue("AR_AMT",i).toString());
					break;
				}
			}
			totAmt +=Double.parseDouble(exeOrderList.getValue("AR_AMT",i).toString());
		}
		//֧����ʽ��ʼ��
		for (int i = 1; i <= 11; i++) {
			if(i<=9) {
				bilOpbRecpParm.setData("PAY_TYPE0"+i,0.00);
				bilOpbRecpParm.setData("REMARK0"+i, "");
				bilOpbRecpParm.setData("INS0"+i,0.00);
			}else {
				bilOpbRecpParm.setData("PAY_TYPE"+i, 0.00);
				bilOpbRecpParm.setData("REMARK"+i, "");
				bilOpbRecpParm.setData("INS"+i,0.00);
			}
		}
		for (int i = 0; i < chargeAmt.length; i++) {
			if(i<9) {
				bilOpbRecpParm.setData("CHARGE0"+(i+1), chargeAmt[i]);
			}else {
				bilOpbRecpParm.setData("CHARGE"+(i+1), chargeAmt[i]);
			}
		}
		Timestamp date =  SystemTool.getInstance().getDate();
        bilOpbRecpParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        String receiptNo = SystemTool.getInstance().getNo("ALL", "OPB",
                "RECEIPT_NO", "RECEIPT_NO");
        
        bilOpbRecpParm.setData("RECEIPT_NO",receiptNo);
        bilOpbRecpParm.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));
        bilOpbRecpParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
        bilOpbRecpParm.setData("MR_NO", parm.getValue("MR_NO"));
        bilOpbRecpParm.setData("RESET_RECEIPT_NO", new TNull(String.class));
        bilOpbRecpParm.setData("PRINT_NO",printNo);
        bilOpbRecpParm.setData("BILL_DATE", date);
        bilOpbRecpParm.setData("CHARGE_DATE", date);
        bilOpbRecpParm.setData("PRINT_DATE", date);
        bilOpbRecpParm.setData("TOT_AMT", totAmt);
        bilOpbRecpParm.setData("REDUCE_REASON", new TNull(String.class));
        bilOpbRecpParm.setData("REDUCE_AMT", 0.00);
        bilOpbRecpParm.setData("REDUCE_NO", "");
        bilOpbRecpParm.setData("REDUCE_DATE", new TNull(Timestamp.class));
        bilOpbRecpParm.setData("REDUCE_DEPT_CODE", new TNull(String.class));
        bilOpbRecpParm.setData("REDUCE_RESPOND", new TNull(String.class));
        bilOpbRecpParm.setData("AR_AMT", 0.00);
        bilOpbRecpParm.setData("PAY_CASH", 0.00); // �۳�ҽ�����
        bilOpbRecpParm.setData("PAY_MEDICAL_CARD", 0.00);
        bilOpbRecpParm.setData("PAY_BANK_CARD", 0.00);
        bilOpbRecpParm.setData("PAY_INS_CARD", 0.00);
        bilOpbRecpParm.setData("PAY_CHECK", 0.00);
        bilOpbRecpParm.setData("PAY_DEBIT", 0.00);
        bilOpbRecpParm.setData("PAY_BILPAY", 0.00);
        bilOpbRecpParm.setData("PAY_INS", 0.00);
        bilOpbRecpParm.setData("PAY_OTHER1", 0.00);
        bilOpbRecpParm.setData("PAY_OTHER2", 0.00);
        bilOpbRecpParm.setData("ALIPAY", 0.00);
        bilOpbRecpParm.setData("PAY_REMARK", new TNull(String.class));
        bilOpbRecpParm.setData("CASHIER_CODE",optUser);
        bilOpbRecpParm.setData("OPT_USER",optUser);
        bilOpbRecpParm.setData("OPT_DATE", date);
        bilOpbRecpParm.setData("OPT_TERM",optTerm);
        bilOpbRecpParm.setData("PRINT_USER", optUser);
        bilOpbRecpParm.setData("MEM_PACK_FLG", memPackFlg);
        bilOpbRecpParm.setData("PAY_OTHER3", 0.00);
        bilOpbRecpParm.setData("PAY_OTHER4", 0.00);
        if (!isNull(parm.getData("TAX_FLG")) && parm.getValue("TAX_FLG").equals("Y")) {
        	bilOpbRecpParm.setData("TAX_FLG","Y");
        	bilOpbRecpParm.setData("TAX_DATE",SystemTool.getInstance().getDate());
        	bilOpbRecpParm.setData("TAX_USER",optUser);
		}else{
			bilOpbRecpParm.setData("TAX_FLG","N");
			bilOpbRecpParm.setData("TAX_DATE",new TNull(Timestamp.class));
			bilOpbRecpParm.setData("TAX_USER","");
		}
        return bilOpbRecpParm;
	}
	protected boolean isNull(Object object){
		if(null == object || object.toString().trim().length()<=0){
			return true;
		}
		return false;
	}
	/**
	 * ��ѯȫ�ֶ�����(�����շ�ҽ�ƿ���ӡ)���Ѿ��շ�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selDataForOPBEKTMem(TParm parm) {
//		TParm result = query("selDataForOPBEKTC", parm);
		String sql =
			" SELECT A.CASE_NO,A.RX_NO,A.SEQ_NO,A.OPT_USER,A.OPT_DATE," +
			" A.OPT_TERM,A.PRESRT_NO,A.REGION_CODE,A.MR_NO,A.ADM_TYPE," +
			" A.RX_TYPE,A.TEMPORARY_FLG,A.RELEASE_FLG,A.LINKMAIN_FLG,A.LINK_NO," +
			" A.ORDER_CODE,A.ORDER_DESC ||CASE" +
			" WHEN TRIM(A.SPECIFICATION) IS NOT NULL OR TRIM(A.SPECIFICATION) <>''" +
			" THEN '(' || A.SPECIFICATION || ')'" +
			" ELSE ''" +
			" END AS ORDER_DESC,A.SPECIFICATION,A.GOODS_DESC,A.ORDER_CAT1_CODE,A.MEDI_QTY," +
			" A.MEDI_UNIT,A.FREQ_CODE,A.ROUTE_CODE,A.TAKE_DAYS,A.DOSAGE_QTY," +
			" A.DISPENSE_QTY,A.DISPENSE_UNIT,A.GIVEBOX_FLG,A.OWN_PRICE,A.NHI_PRICE," +
			" A.DISCOUNT_RATE,A.OWN_AMT,A.AR_AMT,A.DR_NOTE,A.NS_NOTE," +
			" A.DR_CODE,A.ORDER_DATE,A.DEPT_CODE,A.DC_DR_CODE,A.DC_ORDER_DATE," +
			" A.DC_DEPT_CODE,A.EXEC_DEPT_CODE,A.SETMAIN_FLG,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE," +
			" A.HIDE_FLG,A.RPTTYPE_CODE,A.OPTITEM_CODE,A.DEV_CODE,A.MR_CODE," +
			" A.FILE_NO,A.DEGREE_CODE,A.URGENT_FLG,A.INSPAY_TYPE,A.PHA_TYPE," +
			" A.DOSE_TYPE,A.PRINTTYPEFLG_INFANT,A.EXPENSIVE_FLG,A.CTRLDRUGCLASS_CODE,A.PRESCRIPT_NO," +
			" A.ATC_FLG,A.SENDATC_DATE,A.RECEIPT_NO,A.BILL_FLG,A.BILL_DATE," +
			" A.BILL_USER,A.PRINT_FLG,A.REXP_CODE,A.HEXP_CODE,A.CONTRACT_CODE," +
			" A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.PHA_CHECK_CODE,A.PHA_CHECK_DATE," +
			" A.PHA_DOSAGE_CODE,A.PHA_DOSAGE_DATE,A.PHA_DISPENSE_CODE,A.PHA_DISPENSE_DATE,A.NS_EXEC_CODE," +
			" A.NS_EXEC_DATE,A.NS_EXEC_DEPT,A.DCTAGENT_CODE,A.DCTEXCEP_CODE,A.DCT_TAKE_QTY," +
			" A.PACKAGE_TOT,A.AGENCY_ORG_CODE,A.DCTAGENT_FLG,A.DECOCT_CODE,A.EXEC_FLG,A.RECEIPT_FLG,A.BILL_TYPE," +
			" C.NHI_CODE_O,C.NHI_CODE_E,C.NHI_CODE_I,B.DOSE_CODE,A.BUSINESS_NO,A.MEM_PACKAGE_ID " +  //add by huangtt 20141211 A.MEM_PACKAGE_ID
			" FROM OPD_ORDER A,PHA_BASE B,SYS_FEE C " +
			" WHERE A.ORDER_CODE = B.ORDER_CODE(+) " +
			" AND A.ORDER_CODE = C.ORDER_CODE(+) " +
			" AND A.RELEASE_FLG <> 'Y' " +
			" AND A.RECEIPT_NO IS NULL " +
			" AND (A.PRINT_FLG IS NULL OR A.PRINT_FLG ='N' OR A.PRINT_FLG ='') " +
			" AND A.MEM_PACKAGE_ID IS NOT NULL" +
			" AND A.CASE_NO = '" + parm.getValue("CASE_NO") + "'" +
			" ORDER BY A.RX_TYPE,A.RX_NO,A.SEQ_NO";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * ��ѯȫ�ֶ�����(�����շ��ֽ��ӡ)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selDataForOPBCash(TParm parm) {
		TParm result = query("selDataForOPBCash", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯȫ�ֶ�����(�����շ��ֽ��ӡ)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selDataForOPBCashIns(TParm parm) {
		TParm result = query("selDataForOPBCashIns", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ��ǰ�������ú�(For Reg)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selPatFeeForREG(TParm parm) {
		TParm result = query("selPatFeeForREG", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ����ҽ�ƿ��շѸ��£�����ҽ�ƿ��շѣ�
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateForOPBEKT(TParm parm, TConnection connection) {
		TParm result = new TParm();
		parm.setData("PRINT_FLG", "Y");
		result = update("updateForOPBEKT", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �����ֽ��շѸ��£������ֽ��շѣ�
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateForOPBCash(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateForOPBCash", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * �����ֽ��շѸ��£������ֽ��շѣ�  add by huangtt 20150525
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateForOPBCashRe(TParm parm) {
		TParm result = new TParm();
		result = update("updateForOPBCashRe", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �����շ�ҽ�ƿ������վݣ�ҽ�ƿ�
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm upForOPBEKTReturn(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("upForOPBEKTReturn", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �����շ������վ�:�ֽ�
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm upForOPBReturn(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("upForOPBReturn", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯȫ�ֶ�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm query(TParm parm) {
		TParm result = query("query", parm);
		if (result.getErrCode() < 0) {
			err("Order+ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * �ֽ�����嵥ʹ��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm ================pangben 20111014
	 */
	public TParm queryFill(TParm parm) {
		TParm result = query("queryFill", parm);
		if (result.getErrCode() < 0) {
			err("Order+ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * ҽ�ƿ������嵥ʹ��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm ================pangben 20111014
	 */
	public TParm queryFillEKT(TParm parm) {
		TParm result = query("queryFillEKT", parm);
		if (result.getErrCode() < 0) {
			err("Order+ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * ִ�м��˲��������ﲻ��Ʊ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm ===============pangben 20110818
	 */
	public TParm updateForRecode(TParm parm, TConnection connection) {
		TParm result = update("updateForRecode", parm, connection);
		if (result.getErrCode() < 0)
			err(result.getErrCode() + " " + result.getErrText());
		return result;

	}

	/**
	 * ҽ�ƿ��޸�ҽ����������
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm ======================pangben 2011915
	 */
	public TParm updateBillSets(TParm parm, TConnection connection) {
		TParm result = update("updateBillSets", parm, connection);
		if (result.getErrCode() < 0)
			err(result.getErrCode() + " " + result.getErrText());
		return result;
	}

	/**
	 * ����ҽ�ƿ��˷Ѳ�����������
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateBillSetsOne(TParm parm, TConnection connection) {
		TParm result = update("updateBillSetsOne", parm, connection);
		if (result.getErrCode() < 0)
			err(result.getErrCode() + " " + result.getErrText());
		return result;
	}

	/**
	 * ����ҽ��վ�޸�ҽ����Ҫ����һ���վݺ��� ���»���վݽ��
	 * 
	 * @param parm
	 * @param connection
	 *            ========pangben 2012-3-02
	 * @return
	 */
	public TParm updateForOPBEKTReceiptNo(TParm parm, TConnection connection) {
		TParm result = update("updateForOPBEKTReceiptNo", parm, connection);
		if (result.getErrCode() < 0)
			err(result.getErrCode() + " " + result.getErrText());
		return result;
	}

	/**
	 * ɾ��ҽ��(�����շѽ���) ====zhangp 20120414
	 * 
	 * @param order
	 * @return
	 */
	public TParm deleteOPBCharge(TParm parm, TConnection connection) {

		String sql = "DELETE FROM OPD_ORDER " + " WHERE CASE_NO = '"
				+ parm.getValue("CASE_NO") + "' " + "AND RX_NO = '"
				+ parm.getValue("RX_NO") + "' AND SEQ_NO = '"
				+ parm.getValue("SEQ_NO") + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,
				connection));
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		// ���ҽ����ʷ����============pangben 2012-4-15
		sql = "SELECT CASE_NO, RX_NO, SEQ_NO,DC_ORDER_DATE, PRESRT_NO, REGION_CODE, "
				+ " MR_NO, ADM_TYPE, RX_TYPE,  RELEASE_FLG, LINKMAIN_FLG, LINK_NO, "
				+ " ORDER_CODE, ORDER_DESC, GOODS_DESC, SPECIFICATION, ORDER_CAT1_CODE, MEDI_QTY, "
				+ " MEDI_UNIT, FREQ_CODE, ROUTE_CODE, TAKE_DAYS, DOSAGE_QTY, DOSAGE_UNIT, "
				+ " DISPENSE_QTY, DISPENSE_UNIT, GIVEBOX_FLG, OWN_PRICE, NHI_PRICE, DISCOUNT_RATE, "
				+ " OWN_AMT, AR_AMT, DR_NOTE, NS_NOTE, DR_CODE, ORDER_DATE, "
				+ " DEPT_CODE, DC_DR_CODE, DC_DEPT_CODE,  EXEC_DEPT_CODE, EXEC_DR_CODE, SETMAIN_FLG,"
				+ " ORDERSET_GROUP_NO, ORDERSET_CODE, HIDE_FLG,  RPTTYPE_CODE, OPTITEM_CODE, DEV_CODE, "
				+ " MR_CODE, FILE_NO, DEGREE_CODE,  URGENT_FLG, INSPAY_TYPE, PHA_TYPE, "
				+ " DOSE_TYPE, EXPENSIVE_FLG, PRINTTYPEFLG_INFANT, CTRLDRUGCLASS_CODE, PRESCRIPT_NO, HEXP_CODE, "
				+ " CONTRACT_CODE, CTZ1_CODE, CTZ2_CODE, CTZ3_CODE, NS_EXEC_CODE, NS_EXEC_DATE, "
				+ " NS_EXEC_DEPT, DCTAGENT_CODE, DCTEXCEP_CODE, DCT_TAKE_QTY, PACKAGE_TOT, OPT_USER, "
				+ " OPT_DATE, OPT_TERM FROM OPD_ORDER "
				+ " WHERE RELEASE_FLG <> 'Y' AND CASE_NO='"
				+ parm.getValue("CASE_NO")
				+ "' AND  RX_NO = '"
				+ parm.getValue("RX_NO")
				+ "' AND SEQ_NO = '"
				+ parm.getValue("SEQ_NO") + "'";
		TParm orderParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (orderParm.getCount()<=0) {
			return new TParm();
		}
		// ���ҽ����ʷ����============pangben 2012-4-15
		result = OrderHistoryTool.getInstance().insertOpdOrderHistory(
				orderParm.getRow(0), connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ɾ��ҽ������ϸ��(�����շѽ���) ====zhangp 20120416
	 * 
	 * @param order
	 * @return
	 */
	public TParm deleteOPBChargeSet(TParm parm, TConnection connection) {
		String sql = "DELETE FROM OPD_ORDER " + " WHERE CASE_NO = '"
				+ parm.getValue("CASE_NO") + "' " + "AND RX_NO = '"
				+ parm.getValue("RX_NO") + "' AND ORDERSET_CODE = '"
				+ parm.getValue("ORDERSET_CODE") + "' "
				+ "AND ORDERSET_GROUP_NO = '"
				+ parm.getValue("ORDERSET_GROUP_NO") + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,
				connection));
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		// ���ҽ����ʷ����============pangben 2012-4-15
		sql = "SELECT CASE_NO, RX_NO, SEQ_NO,DC_ORDER_DATE, PRESRT_NO, REGION_CODE, "
				+ " MR_NO, ADM_TYPE, RX_TYPE,  RELEASE_FLG, LINKMAIN_FLG, LINK_NO, "
				+ " ORDER_CODE, ORDER_DESC, GOODS_DESC, SPECIFICATION, ORDER_CAT1_CODE, MEDI_QTY, "
				+ " MEDI_UNIT, FREQ_CODE, ROUTE_CODE, TAKE_DAYS, DOSAGE_QTY, DOSAGE_UNIT, "
				+ " DISPENSE_QTY, DISPENSE_UNIT, GIVEBOX_FLG, OWN_PRICE, NHI_PRICE, DISCOUNT_RATE, "
				+ " OWN_AMT, AR_AMT, DR_NOTE, NS_NOTE, DR_CODE, ORDER_DATE, "
				+ " DEPT_CODE, DC_DR_CODE, DC_DEPT_CODE,  EXEC_DEPT_CODE, EXEC_DR_CODE, SETMAIN_FLG,"
				+ " ORDERSET_GROUP_NO, ORDERSET_CODE, HIDE_FLG,  RPTTYPE_CODE, OPTITEM_CODE, DEV_CODE, "
				+ " MR_CODE, FILE_NO, DEGREE_CODE,  URGENT_FLG, INSPAY_TYPE, PHA_TYPE, "
				+ " DOSE_TYPE, EXPENSIVE_FLG, PRINTTYPEFLG_INFANT, CTRLDRUGCLASS_CODE, PRESCRIPT_NO, HEXP_CODE, "
				+ " CONTRACT_CODE, CTZ1_CODE, CTZ2_CODE, CTZ3_CODE, NS_EXEC_CODE, NS_EXEC_DATE, "
				+ " NS_EXEC_DEPT, DCTAGENT_CODE, DCTEXCEP_CODE, DCT_TAKE_QTY, PACKAGE_TOT, OPT_USER, "
				+ " OPT_DATE, OPT_TERM FROM OPD_ORDER "
				+ " WHERE RELEASE_FLG <> 'Y' AND CASE_NO='"
				+ parm.getValue("CASE_NO")
				+ "' AND  RX_NO = '"
				+ parm.getValue("RX_NO")
				+ "' AND ORDERSET_CODE = '"
				+ parm.getValue("ORDERSET_CODE")
				+ "' AND ORDERSET_GROUP_NO = '"
				+ parm.getValue("ORDERSET_GROUP_NO") + "'";
		TParm orderParm = new TParm(TJDODBTool.getInstance().select(sql));
		// ��ѯ����ҽ������
		result = OrderHistoryTool.getInstance().onInsertHistory(orderParm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	public static void main(String args[]) {
		JavaHisDebug.initClient();
		TParm parm = new TParm();
		parm.setData("CASE_NO", "ABC");
	}
	/**
	 * ������д��ҽ�����ݣ�����ʹ��
	 * @return
	 * ===========pangben 2013-5-20
	 */
	public String insertOpdOrderPhaSpc(TParm parm){
		TParm result = update("insertOpdOrderSpc", parm);
		if (result.getErrCode() != 0)
			return "ERR:" + result.getErrText();
		return "SUCCESS";
	}
	/**
	 * ͨ������ǩɾ��������ҽ��������������
	 * @param rxNo 
	 * @return
	 * ===========pangben 2013-5-20
	 */
	public String deleteOpdOrderPhaSpc(String rxNo){
		String sql="DELETE FROM OPD_ORDER WHERE RX_NO IN("+rxNo+") AND CAT1_TYPE='PHA' AND BILL_FLG='Y'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() != 0)
			return "ERR:" + result.getErrText();
		return "SUCCESS";
	}
	/**
	 * ����ҩ��ִ��״̬�ı��
	 * yanjing
	 * 20130415
	 * @return TParm
	 */
	public TParm getFlgUpdateDate(TParm parm,TConnection conn) {
		TParm result = update("savedata", parm,conn);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ��������ô˴β�����ҽ����ͨ������ǩ���
	 * @return
	 * =============pangben 2013-5-21
	 */
	public TParm getSumOpdOrderByRxNo(TParm parm){
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT A.CASE_NO,A.RX_NO,A.SEQ_NO,A.PRESRT_NO,A.REGION_CODE,A.MR_NO,A.ADM_TYPE,A.RX_TYPE,A.TEMPORARY_FLG,")
		.append("A.RELEASE_FLG,A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_CODE,A.ORDER_DESC,A.GOODS_DESC,A.SPECIFICATION,")
		.append("A.ORDER_CAT1_CODE,A.MEDI_QTY,A.MEDI_UNIT,A.FREQ_CODE,A.ROUTE_CODE,A.TAKE_DAYS,A.DOSAGE_QTY,A.DOSAGE_UNIT,")
		.append("A.DISPENSE_QTY,A.DISPENSE_UNIT,A.GIVEBOX_FLG,A.OWN_PRICE,A.NHI_PRICE,A.DISCOUNT_RATE,A.OWN_AMT,A.AR_AMT,")
		.append("A.DR_NOTE,A.NS_NOTE,A.DR_CODE,A.ORDER_DATE,A.DEPT_CODE,A.DC_DR_CODE,A.DC_ORDER_DATE,A.DC_DEPT_CODE,A.EXEC_DEPT_CODE,")
		.append("A.EXEC_DR_CODE,A.SETMAIN_FLG,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,A.HIDE_FLG,A.RPTTYPE_CODE,A.OPTITEM_CODE,A.DEV_CODE,")
		.append("A.MR_CODE,A.FILE_NO,A.DEGREE_CODE,A.URGENT_FLG,A.INSPAY_TYPE,A.PHA_TYPE,A.DOSE_TYPE,A.EXPENSIVE_FLG,A.PRINTTYPEFLG_INFANT,")
		.append("A.CTRLDRUGCLASS_CODE,A.PRESCRIPT_NO,A.ATC_FLG,A.SENDATC_DATE,A.RECEIPT_NO,A.BILL_FLG,A.BILL_DATE,A.BILL_USER,A.PRINT_FLG,")
		.append("A.REXP_CODE,A.HEXP_CODE,A.CONTRACT_CODE,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.PHA_CHECK_CODE,A.PHA_CHECK_DATE,")
		.append( "A.PHA_DOSAGE_CODE,A.PHA_DOSAGE_DATE,A.PHA_DISPENSE_CODE,A.PHA_DISPENSE_DATE,A.PHA_RETN_CODE,A.PHA_RETN_DATE,")
		.append( "A.NS_EXEC_CODE,A.NS_EXEC_DATE,A.NS_EXEC_DEPT,A.DCTAGENT_CODE,A.DCTEXCEP_CODE,A.DCT_TAKE_QTY,A.PACKAGE_TOT,A.AGENCY_ORG_CODE,")
		.append("A.DCTAGENT_FLG,A.DECOCT_CODE,A.REQUEST_FLG,A.REQUEST_NO,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.MED_APPLY_NO,A.CAT1_TYPE,")
		.append("A.TRADE_ENG_DESC,A.PRINT_NO,A.COUNTER_NO,A.PSY_FLG,A.EXEC_FLG,A.RECEIPT_FLG,A.BILL_TYPE,A.FINAL_TYPE,A.DECOCT_REMARK,")
		.append("A.SEND_DCT_USER,A.SEND_DCT_DATE,A.DECOCT_USER,A.DECOCT_DATE,A.SEND_ORG_USER,A.SEND_ORG_DATE,A.EXM_EXEC_END_DATE,A.EXEC_DR_DESC,")
		.append("A.COST_AMT,A.COST_CENTER_CODE,A.BATCH_SEQ1,A.VERIFYIN_PRICE1,A.DISPENSE_QTY1,A.BATCH_SEQ2,A.VERIFYIN_PRICE2,A.DISPENSE_QTY2,")
		.append("A.BATCH_SEQ3,A.VERIFYIN_PRICE3,A.DISPENSE_QTY3,A.BUSINESS_NO,B.PAT_NAME,CASE ")
		.append("WHEN B.SEX_CODE ='1' ")
		.append("THEN '��' WHEN B.SEX_CODE='2' THEN 'Ů' ")
		.append(" ELSE 'δ֪' ")
		.append(" END AS SEX_TYPE,B.BIRTH_DATE FROM OPD_ORDER A,SYS_PATINFO B WHERE A.MR_NO=B.MR_NO AND A.BILL_FLG='Y'");
		if (parm.getValue("CASE_NO").length() > 0) {
			sql.append(" AND CASE_NO='").append(parm.getValue("CASE_NO")).append("'");
		}
		if(parm.getValue("RX_NO").length()>0){
			sql.append(" AND RX_NO IN (").append(parm.getValue("RX_NO")).append(")");
		}
		if (parm.getValue("CAT1_TYPE").length() > 0) {
			sql.append(" AND CAT1_TYPE='").append(parm.getValue("CAT1_TYPE")).append("'");
		}
		if (parm.getValue("RX_TYPE").length() > 0) {
			sql.append(" AND RX_TYPE<>'").append(parm.getValue("RX_TYPE")).append("'");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * У���棬�ż������ 
	 * @param parmBase  ҩƷ����
	 * @param parm
	 * @return
	 * ==========pangben 2013-11-6
	 */
	public double getDosAgeQty(TParm parmBase,TParm parm){
		double tMediQty = parmBase.getDouble("MEDI_QTY", 0);// ��ҩ����
		String tUnitCode = parmBase.getValue("MEDI_UNIT", 0);// ��ҩ��λ
		String tFreqCode = parmBase.getValue("FREQ_CODE", 0);// Ƶ��
		int tTakeDays = parmBase.getInt("TAKE_DAYS", 0);// ����
		parm.setData("TAKE_DAYS", tTakeDays);
		parm.setData("MEDI_QTY", tMediQty);
		parm.setData("FREQ_CODE", tFreqCode);
		parm.setData("MEDI_UNIT", tUnitCode);
		parm.setData("ORDER_DATE", SystemTool.getInstance().getDate());
		parm.setData("GIVEBOX_FLG",parmBase.getValue("GIVEBOX_FLG", 0));
		TParm qtyParm = TotQtyTool.getInstance().getTotQty(parm);
		double dosageQty=0.00;
		if ("Y".equalsIgnoreCase(parmBase.getValue("GIVEBOX_FLG", 0))) {
			dosageQty=qtyParm.getDouble("TOT_QTY");
		}else{
			dosageQty=qtyParm.getDouble("QTY");
		}
		return dosageQty;
	}
	/**
	 * ������������ѯҽ������
     * pangben 2013-11-7
	 * @param parm
	 * @return
	 */
	public TParm selectLockQtyCheckSumQty(TParm parm){
		TParm result = query("selectLockQtyCheckSumQty", parm);
		if (result.getErrCode() < 0)
			return result;
		return result;
	}
	
	/**
	 * ɾ������ʱ����ǲ�����ҩ��ɾ��
	 * @param parm
	 * @return
	 */
	public TParm selectOpdOrder(TParm parm) {
		TParm result = query("selectOpdOrder",parm);
		return result ;
	}
	
	/**
	 * ����ҽ��վҩƷУ�����䷢
	 * @param needExamineFlg ���ݿ�����
	 * @param order ҽ����Ϣ
	 * flg true ɾ��һ�� �����޸Ķ���ʹ��   false  ɾ������ǩʹ��
	 * @return
	 * ==========pangben 2014-1-1
	 */
	public int checkPhaIsExe(boolean needExamineFlg,TParm order,boolean flg){
		// ������������ ��ô�ж����ҽʦ�Ƿ�Ϊ��
		TParm result = query("checkPhaIsExe", order);
		if (result.getErrCode() < 0)
			return 1;
		if (result.getCount()<=0) {//û�в�ѯ��������˵����ҽ����û�б��浽���ݿ�
			return 0;
		}
		if (flg) {
			if (result.getValue("BILL_FLG",0).equals("Y")
					&&result.getValue("PHA_RETN_CODE",0).length()>0) {//���շѵ����Ѿ���ҩ״̬
				return 4;// �Ѿ��շѲ��������޸�
			}
		}else{
			if (result.getValue("BILL_FLG",0).equals("Y")
					&&result.getValue("PHA_RETN_CODE",0).length()>0) {//���շѵ����Ѿ���ҩ״̬
				return 3;// �Ѿ��շѲ��������޸�
			}
		}
		if (needExamineFlg) {
			// System.out.println("�����");
			// ��������Ա���� ��������ҩ��Ա ��ô��ʾҩƷ����� ���������޸�
			if (flg) {
				if (result.getValue("BILL_FLG",0).equals("N")
						&&result.getValue("PHA_RETN_CODE",0).length()>0){//����ҩ  δ�շ�
					return 5;
				}
				if (result.getValue("PHA_CHECK_CODE",0).length() > 0){
					return 1;
				}
			}else{
				if (result.getValue("PHA_CHECK_CODE",0).length() > 0
						&& result.getValue("PHA_RETN_CODE",0).length() == 0) {
					return 1;
				}
			}
		} else {// û��������� ֱ����ҩ
			// �ж��Ƿ�����ҩҩʦ
			// System.out.println("�����");
			if (result.getValue("PHA_DOSAGE_CODE",0).length() > 0
					&& result.getValue("PHA_RETN_CODE",0).length() == 0) {
				return 1;// �Ѿ���ҩ���������޸�
			}
		}
		
		if (result.getValue("BILL_FLG",0).equals("Y")) {
			return 2;// �Ѿ��շѲ��������޸�
		}
		return 0;
	}
	
	
	
	/**
	 * ��ѯ�Ƿ���Ƥ��ҩƷ
	 * @param parm
	 * @return
	 */
	public TParm onQuerySkintestPhaBase(String orderCode){
		 
		String sql = "SELECT SKINTEST_FLG FROM PHA_BASE  WHERE ORDER_CODE='"+orderCode+"' AND ANTIBIOTIC_CODE IS NOT NULL ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
}
