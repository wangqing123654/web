/**
 * 
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.ui.TTable;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author wu 2012-6-13����03:01:50
 * @version 1.0
 */
public class ReplaceMedicineTool extends TJDOTool {
	private static ReplaceMedicineTool instance;

	private ReplaceMedicineTool() {
		setModuleName("sys\\ReplaceMedicineModule.x");
		onInit();
	};

	public static ReplaceMedicineTool getInstance() {
		if (instance == null)
			instance = new ReplaceMedicineTool();
		return instance;
	}

	/**
	 * ��ѯ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQuery(TParm parm) {
		TParm result = this.query("queryAll", parm);
		if (result.getErrCode() < 0) {
			err("Err:" + result.getErrCode() + result.getErrName()
					+ result.getErrText());
		}
		return result;
	}

	/**
	 * ��ѯ ���SEQ
	 */
	public TParm onQuerySEQ() {
		TParm result = this.query("queryMaxSEQ");
		if (result.getErrCode() < 0) {
			err("Err:" + result.getErrCode() + result.getErrName()
					+ result.getErrText());
		}
		return result;
	}

	// ����ҩƷ��ź���ҩƷ����޸�
	public TParm onUpdateByCode(TParm parm) {
		TParm result = this.update("onUpdateByCode", parm);
		if (result.getErrCode() < 0) {
			err("Err" + result.getErrCode() + result.getErrName()
					+ result.getErrText());
		}
		return result;
	}
	// ����ҩƷ��ź���ҩƷ����޸�
	public TParm onQueryByCode(TParm parm) {
		TParm result = this.update("onQueryByCode", parm);
		System.out.println("-------����ҩƷ��ź���ҩƷ����޸�: parm: "+ result);
		if (result.getErrCode() < 0) {
			err("Err" + result.getErrCode() + result.getErrName()
					+ result.getErrText());
		}
		return result;
	}

	// ����
	public TParm onSave(TParm parm) {
		TParm result = this.update("onSave", parm);

		if (result.getErrCode() < 0) {
			err("Err:" + result.getErrCode() + result.getErrName()
					+ result.getErrText());
		}
		return result;
	}

	// ɾ��
	public TParm onDelete(TParm parm) {
		TParm result = this.update("onDelete", parm);
		if (result.getErrCode() < 0) {
			err("Err:" + result.getErrCode() + result.getErrName()
					+ result.getErrText());
		}
		return result;
	}
}
