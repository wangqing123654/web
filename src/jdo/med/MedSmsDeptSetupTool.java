package jdo.med;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

public class MedSmsDeptSetupTool  extends TJDOTool {
	public MedSmsDeptSetupTool() {
		setModuleName("med\\MEDSmsDeptSetupModule.x");
		onInit();
	}

	/**
	 * ʵ��
	 */
	public static MedSmsDeptSetupTool instanceObject;

	/**
	 * �õ�ʵ��
	 *
	 * @return SYSRegionTool
	 */
	public static MedSmsDeptSetupTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MedSmsDeptSetupTool();
		return instanceObject;
	}
	
	/**
	 * ����������ѯ
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQuery(TParm parm) {
		TParm result = this.query("selectdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ���������
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsert(TParm parm) {
		TParm result = this.update("insertdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * �޸�
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onUpdate(TParm parm) {
		TParm result = this.update("updatedata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * ɾ��
	 *
	 * @param <any>
	 *            TParm
	 * @return TParm
	 */
	public TParm onDelete(TParm parm) {
		TParm result = this.update("deletedata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}


}
