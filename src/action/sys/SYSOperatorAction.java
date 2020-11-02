package action.sys;

import jdo.sys.SYSLicenseTool;
import jdo.sys.SYSOperatorDeptTool;
import jdo.sys.SYSOperatorStationTool;
import jdo.sys.SYSOperatorTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 *
 * <p>
 * Title: ҽ��վ������
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author ehui 20081112
 * @version 1.0
 */
public class SYSOperatorAction extends TAction {
	/**
	 * ��ѯ
	 *
	 * @param parm
	 * @return result TParm
	 */
	public TParm onQuery(TParm parm) {
		TParm result = new TParm();
		// ��ѯ�û���Ϣ
		TParm operator = SYSOperatorTool.getInstance().selecedataByParameters(
				parm);

		if (operator.getErrCode() != 0) {
			return err(operator);
		}
		//System.out.println("after operator");
		result.setData("Operator", operator.getData());
		//System.out.println("result.operator==============" + result);
		// ��ѯ����
		TParm dept = SYSOperatorDeptTool.getInstance().select(parm);

		if (dept.getErrCode() != 0) {
			return err(dept);
		}
		//System.out.println("after dept");
		result.setData("Dept", dept.getData());
		// ��ѯ֤��
		TParm license = SYSLicenseTool.getInstance().select(parm);

		if (license.getErrCode() != 0) {
			return err(license);
		}
		//System.out.println("after license");
		result.setData("License", license.getData());

		TParm station = SYSOperatorStationTool.getInstance().select(parm);

		if (station.getErrCode() != 0) {
			return err(station);
		}
		//System.out.println("after station");
		result.setData("Station", station.getData());
		return result;
	}

	/**
	 * �ż�ҽ��վ�������
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onSave(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();

		// �����û�
		TParm operator = parm.getParm("Operator");
		result = SYSOperatorTool.getInstance().onSave(operator, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// ���沿��
		TParm dept = parm.getParm("Dept");
		result = SYSOperatorDeptTool.getInstance().onSave(dept, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// ���湤��վ
		TParm station = parm.getParm("Station");
		result = SYSOperatorStationTool.getInstance().onSave(station,
				connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// ����֤��
		TParm license = parm.getParm("License");
		result = SYSLicenseTool.getInstance().onSave(license, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}

		// UPDATE �Һ��������е�SEE_DRע�ǣ�����ע������Ϊ�ѿ���

		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * �����û���Ϣ
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onSaveOperator(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		result = SYSOperatorTool.getInstance().onSaveOperator(parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
}
