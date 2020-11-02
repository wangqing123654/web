package action.pha;
import java.util.Iterator;
import java.util.Map;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.pha.TXNewATCTool;
import jdo.sys.PHATXATCTool;
import jdo.udd.UddChnCheckTool;

public class PHAATCAction extends TAction {

	/**
	 * �����ҩ��
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onATCO(TParm parm) {
		TParm result = new TParm();
		/**
		 * 1�ɰ�ҩ�� 2�°�ҩ��
		 */
		String type = parm.getValue("TYPE");
		if (type.equals("1")) {
			if (PHATXATCTool.getInstance().generateOldATCTxtO(parm))
				result.setErrCode(0);
			else
				result.setErrCode(-1);
		} else if (type.equals("2")) {
			if (TXNewATCTool.getInstance().OInsertData(parm))
				result.setErrCode(0);
			else
				result.setErrCode(-1);
		}
		return result;
	}

	/**
	 * סԺ��ҩ��
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onATCI(TParm parm) {
		TParm result = new TParm();
		/**
		 * 1�ɰ�ҩ�� 2�°�ҩ��
		 */
		String type = parm.getValue("TYPE");
		if (type.equals("1")) {
			if (PHATXATCTool.getInstance().generateOldATCTxtI(parm))
				result.setErrCode(0);
			else
				result.setErrCode(-1);
		} else if (type.equals("2")) {
			TParm inparm = parm.getParm("DRUG_LIST_PARM");
			Map pat = TXNewATCTool.getInstance().groupByPatParm(inparm);
			Iterator it = pat.values().iterator();
			while (it.hasNext()) {
				TParm patParm = (TParm) it.next();
				if (TXNewATCTool.getInstance().IInsertData(patParm)) {
					result = updateUddBarCode(patParm);
					if (result.getErrCode() < 0) {
						result.setErrCode(-2);
						return result;
					}
				} else{
					result.setErrCode(-1);
				    return result;
				}
			}
		}
		return result;
	}

	/**
	 * ����BAR_CODE�ֶ�
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateUddBarCode(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		int count = parm.getCount("BAR_CODE");
		for (int i = 0; i < count; i++) {
			TParm parmRow = parm.getRow(i);
			result = UddChnCheckTool.getInstance().updateUddMBarCode(parmRow,connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				result.setErrCode(-1);
				return result;
			}
			result = UddChnCheckTool.getInstance().updateUddDBarCode(parmRow,connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				result.setErrCode(-1);
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}
}
