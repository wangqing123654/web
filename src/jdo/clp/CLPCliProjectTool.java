package jdo.clp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>
 * Title:�ٴ��о���Ŀ��̨���ݿ⹤��
 * </p>
 * 
 * <p>
 * Description:�ٴ��о���Ŀ��̨���ݿ⹤��
 * </p>
 *
 */
public class CLPCliProjectTool extends TJDOTool{
	
	private static CLPCliProjectTool mInstance;
	
	public static CLPCliProjectTool getNewInstance() {
		if(mInstance == null) {
			mInstance = new CLPCliProjectTool();
		}
		return mInstance;
	}

	public CLPCliProjectTool() {
		this.setModuleName("clp\\CLPCliProjectModule.x");
        onInit();
	}
	
	/**
	 * ��ѯ
	 * @param parm
	 */
	public TParm onQuery(TParm parm) {
		parm = this.query("query", parm);
		return parm;
	}
	
	/**
	 * ɾ��
	 */
	public boolean onDelete(TParm parm) {
		if(update("delete", parm).getErrCode() <0) {
			return false;
		}
		return true;
	}
	/**
	 * ����
	 * @param parm : ���µ�Ԫ��
	 */
	public boolean onUpdate(TParm parm) {
		TParm p = update("update", parm);
		if(p.getErrCode() < 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * ����
	 * @param parm �� �����Ԫ��
	 */
	public boolean onSave(TParm parm) {
		TParm p = update("save", parm);
		if(p.getErrCode() < 0) {
			return false;
		}
		return true;
	}
}
