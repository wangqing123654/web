package action.sta;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import jdo.sta.STAOpdDailyTool;
import com.dongyang.db.TConnection;
import jdo.sta.STAStationDailyTool;

/**
 * <p>Title: �м����Ϣ����</p>
 *
 * <p>Description: �м����Ϣ����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-27
 * @version 1.0
 */
public class STADailyAction
extends TAction {
	public STADailyAction() {
	}
	/**
	 * ���� �ż����м䵵 ����
	 * @param parm TParm  �������������֣�"SQL"��ʾSQL������  ��DEPT����ʾ�����б�
	 * @return TParm
	 */
	public TParm insertSTA_OPD_DAILY(TParm parm) {
		TParm result = new TParm();
		// modified by WangQing at 20170209 -start
		// �������ж��Ƶ�conn����֮ǰ
		if (parm == null) {
			result.setErr( -1, "����Ϊ�գ�");
			return result;
		}
		// modified by WangQing at 20170209 -end
		TConnection conn = this.getConnection();

		result = STAOpdDailyTool.getInstance().insertData(parm,conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			// modified by WangQing at 20170209 -start
			// conn rollback
			conn.rollback();
			// modified by WangQing at 20170209 -end
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * ���� Ժ���м䵵 ����
	 * @param parm TParm  �������������֣�"SQL"��ʾSQL������  ��DEPT����ʾ�����б�
	 * @return TParm
	 */
	public TParm insertStation_Daily(TParm parm){
		TParm result = new TParm();
		// modified by WangQing at 20170209 -start
		// �������ж��Ƶ�conn����֮ǰ
		if (parm == null) {
			result.setErr( -1, "����Ϊ�գ�");
			return result;
		}
		// modified by WangQing at 20170209 -end
		TConnection conn = this.getConnection();

		result = STAStationDailyTool.getInstance().insertData(parm,conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			// modified by WangQing at 20170209 -start
			// conn rollback
			conn.rollback();
			// modified by WangQing at 20170209 -end
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * �Զ����ε��� �ż����м䵵 ����
	 * @param parm TParm  ����Ϊ��TParm ����
	 * @return TParm
	 */
	public TParm batchSTA_OPD_DAILY(TParm parm) {
		TParm result = new TParm();
		// modified by WangQing at 20170209 -start
		// �������ж��Ƶ�conn����֮ǰ
		if (parm == null) {
			result.setErr( -1, "����Ϊ�գ�");
			return result;
		}
		// modified by WangQing at 20170209 -end
		TConnection conn = this.getConnection();

		result = STAOpdDailyTool.getInstance().batchData(parm,conn);
		if (result.getErrCode() < 0) {
			err("STA��������ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			// modified by WangQing at 20170209 -start
			// conn rollback
			conn.rollback();
			// modified by WangQing at 20170209 -end
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * �Զ����ε��� Ժ���м䵵 ����
	 * @param parm TParm  ����Ϊ��TParm ����
	 * @return TParm
	 */
	public TParm batchStation_Daily(TParm parm){
		TParm result = new TParm();
		// modified by WangQing at 20170209 -start
		// �������ж��Ƶ�conn����֮ǰ
		if (parm == null) {
			result.setErr( -1, "����Ϊ�գ�");
			return result;
		}
		// modified by WangQing at 20170209 -end
		TConnection conn = this.getConnection();

		result = STAStationDailyTool.getInstance().batchData(parm,conn);
		if (result.getErrCode() < 0) {
			err("STAסԺ����ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			// modified by WangQing at 20170209 -start
			// conn rollback
			conn.rollback();
			// modified by WangQing at 20170209 -end
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

}
