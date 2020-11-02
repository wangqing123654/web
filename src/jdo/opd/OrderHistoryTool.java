package jdo.opd;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.javahis.util.JavaHisDebug;
/**
*
* <p>Title: ҽ����ʷtool
*
* <p>Description: ҽ����ʷtool</p>
*
* <p>Copyright: Copyright (c) Liu dongyang 2008</p>
*
* <p>Company: javahis
*
* @author ehui 20080911
* @version 1.0
*/
public class OrderHistoryTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static OrderHistoryTool instanceObject;

	/**
	 * �õ�ʵ��
	 * @return OrderTool
	 */
	public static OrderHistoryTool getInstance() {
		if (instanceObject == null)
			instanceObject = new OrderHistoryTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public OrderHistoryTool() {
		setModuleName("opd\\OPDOrderHistoryModule.x");

		onInit();
	}

	/**
	 * ����ҽ��
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm insertdata(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("insertdata", parm,connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ����ҽ��
	 * ================pangben 2012-4-15
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm insertOpdOrderHistory(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("insertOpdOrderHistory", parm,connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * �ж��Ƿ��������
	 * @param TParm parm
	 * @return boolean TRUE ���� FALSE ������
	 */
	public boolean existsOrder(TParm parm) {
		return getResultInt(query("existsOrder", parm), "COUNT") > 0;
	}

	/**
	 * ��������
	 * @param parm
	 * @return
	 */
	public TParm updatedata(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("updatedata", parm,connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ɾ������
	 * @param parm
	 * @return
	 */
	public TParm deletedata(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("deletedata", parm,connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��������
	 * @param parm
	 * @return
	 */
	public TParm selectdata(TParm parm) {
//		TParm parm = new TParm();
//		parm.setData("CASE_NO", caseNo);
		TParm data = query("selectdata", parm);
		if (data.getErrCode() < 0) {
			err("ERR:" + data.getErrCode() + data.getErrText()
					+ data.getErrName());
			return data;
		}
		//System.out.println("orderhistory=" + data);
		TParm result = new TParm();

		int count = data.getCount();
		TParm groupParm = null;
		TParm orderList = null;
		String oldRxType = "";
		String oldRxNo = "";
		for (int i = 0; i < count; i++) {
			//��������
			String rxType = data.getValue("RX_TYPE", i);
			if (!rxType.equalsIgnoreCase(oldRxType)) {
				groupParm = new TParm();
				groupParm.setData("NAME", rxType);
				result.addData("GROUP", groupParm.getData());
				result.setData("ACTION", "COUNT", result.getCount("GROUP"));
				oldRxType = rxType;
				oldRxNo = "";
			}

			//������
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
	 * ɾ��
	 * @param parm
	 * @return result
	 */
	public TParm onDelete(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.deletedata(inParm,connection);
			if (result.getErrCode() < 0)
				return result;
		}

		return result;
	}
	/**
	 * ����ҽ�� ��������
	 * ================pangben 2012-4-15
	 * @param parm
	 * @return result
	 */
	public TParm onInsertHistory(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.insertOpdOrderHistory(inParm,connection);
			if (result.getErrCode() < 0)
				return result;
		}
		return result;
	}
	/**
	 * ����
	 * @param parm
	 * @return result
	 */
	public TParm onInsert(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.insertdata(inParm,connection);
			if (result.getErrCode() < 0)
				return result;
		}
		return result;
	}

	/**
	 * ����
	 * @param parm
	 * @return result
	 */
	public TParm onUpdate(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.updatedata(inParm,connection);
			if (result.getErrCode() < 0)
				return result;
		}
		return result;
	}

	/**
	 * odo�춯�����
	 * @param parm
	 * @param connection
	 * @return result ������
	 */
	public TParm onSave(TParm parm, TConnection connection) {
		TParm result = onDelete(parm.getParm(OrderList.DELETED),connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = onInsert(parm.getParm(OrderList.NEW),connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = onUpdate(parm.getParm(OrderList.MODIFIED),connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public static void main(String args[]) {
		JavaHisDebug.initClient();
		//System.out.println(OrderTool.getInstance().query("ABC"));

	}
}
