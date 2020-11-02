package action.sum;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sum.SUMVitalSignTool;

/**
 * <p>
 * Title:����(���������µ�)
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
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class SUMVitalSignAction extends TAction {
	public SUMVitalSignAction() {
	}

	/**
	 * �������
	 * 
	 * @param parm
	 * @return TParm
	 */
	public TParm onSave(TParm parm) {
		TParm masterParm = parm.getParm("MASET");
		TParm detailParm = parm.getParm("DETAIL");
		TParm HWeightParm = parm.getParm("HW");// ���� ���
		boolean insertFlg = parm.getBoolean("I");

		TParm result = new TParm();
		// ����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
		TConnection connection = getConnection();
		if (insertFlg) {// ����
			result = SUMVitalSignTool.getInstance().insertVitalSign(masterParm,
					connection);
			if (result.getErrCode() < 0) {
				System.out.println(result.getErrText());
				connection.close();
				return result;
			}
			result = SUMVitalSignTool.getInstance().insertVitalSignDtl(
					detailParm, connection);
			if (result.getErrCode() < 0) {
				System.out.println(result.getErrText());
				connection.close();
				return result;
			}
			// ����ADM_INP���������
			result = SUMVitalSignTool.getInstance().updateHeightAndWeight(
					HWeightParm, connection);
			if (result.getErrCode() < 0) {
				System.out.println(result.getErrText());
				connection.close();
				return result;
			}

		} else {// ����
			result = SUMVitalSignTool.getInstance().updateVitalSign(masterParm,
					connection);
			if (result.getErrCode() < 0) {
				System.out.println(result.getErrText());
				connection.close();
				return result;
			}
			result = SUMVitalSignTool.getInstance().updateVitalSignDtl(
					detailParm, connection);
			if (result.getErrCode() < 0) {
				System.out.println(result.getErrText());
				connection.close();
				return result;
			}
			// ����ADM_INP���������
			result = SUMVitalSignTool.getInstance().updateHeightAndWeight(
					HWeightParm, connection);
			if (result.getErrCode() < 0) {
				System.out.println(result.getErrText());
				connection.close();
				return result;
			}

		}
		connection.commit();
		connection.close();
		return result;
	}
    /**
     * ��ͯ���µ�����
     * @param parm
     * @return
     */
	public TParm onCorNSave(TParm parm) {
		TParm masterParm = parm.getParm("MASET");
		TParm detailParm = parm.getParm("DETAIL");
		TParm HWeightParm = parm.getParm("HW");// ���� ���
		boolean insertFlg = parm.getBoolean("I");

		TParm result = new TParm();
		// ����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
		TConnection connection = getConnection();
		if (insertFlg) {// ����
			result = SUMVitalSignTool.getInstance().insertCorNVitalSign(masterParm,
					connection);
			if (result.getErrCode() < 0) {
				System.out.println(result.getErrText());
				connection.close();
				return result;
			}
			result = SUMVitalSignTool.getInstance().insertVitalSignDtl(
					detailParm, connection);
			if (result.getErrCode() < 0) {
				System.out.println(result.getErrText());
				connection.close();
				return result;
			}
			// ����ADM_INP���������
			result = SUMVitalSignTool.getInstance().updateHeightAndWeight(
					HWeightParm, connection);
			if (result.getErrCode() < 0) {
				System.out.println(result.getErrText());
				connection.close();
				return result;
			}

		} else {// ����
			result = SUMVitalSignTool.getInstance().updateCorNVitalSign(masterParm,
					connection);
			if (result.getErrCode() < 0) {
				System.out.println(result.getErrText());
				connection.close();
				return result;
			}
			result = SUMVitalSignTool.getInstance().updateVitalSignDtl(
					detailParm, connection);
			if (result.getErrCode() < 0) {
				System.out.println(result.getErrText());
				connection.close();
				return result;
			}
			// ����ADM_INP���������
			result = SUMVitalSignTool.getInstance().updateHeightAndWeight(
					HWeightParm, connection);
			if (result.getErrCode() < 0) {
				System.out.println(result.getErrText());
				connection.close();
				return result;
			}

		}
		connection.commit();
		connection.close();
		return result;
	}
}
