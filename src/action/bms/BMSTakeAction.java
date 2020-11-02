package action.bms;

import java.util.ArrayList;
import java.util.List;
import jdo.bms.BMSTakeTool;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 * 
 * @author shibl
 * 
 */
public class BMSTakeAction extends TAction {
	public BMSTakeAction() {

	}

	/**
	 * ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsert(TParm parm) {
		TConnection conn = getConnection();
		TParm result = onInsertBMSTake(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
			+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onUpdate(TParm parm) {
		TConnection conn = getConnection();
		TParm result = onDeleteBMSTake(parm.getParm("MTABLE"), conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
			+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		result = onInsertBMSTake(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
			+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	public TParm onDelete(TParm parm) {
		TConnection conn = getConnection();
		TParm result = onDeleteBMSTake(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
			+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * ɾ����װsql
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	// modified by WangQing at 20170213
	// 1.���η� public->private 2.˽�з������ùر�����
	private TParm onDeleteBMSTake(TParm parm, TConnection conn) {
		String sqlM = BMSTakeTool.getInstance().getMdelsql(parm);
		String[] arrayM = new String[] { sqlM };
		String sqlD = BMSTakeTool.getInstance().getDdelsql(parm);
		String[] arrayD = new String[] { sqlD };
		String[] arraysql = StringTool.copyArray(arrayM, arrayD);
		TParm result = new TParm(TJDODBTool.getInstance()
				.update(arraysql, conn));
		if (result.getErrCode() < 0) {
			
			err("ERR:" + result.getErrCode() + result.getErrText()
			+ result.getErrName());
			return result;
		}
		
		return result;
	}

	/**
	 * ������װsql
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	// modified by WangQing at 20170213
	// 1.���η� public->private 2.˽�з������ùر�����
	private TParm onInsertBMSTake(TParm parm, TConnection conn) {
		TParm parmM = parm.getParm("MTABLE");
		String sqlM = BMSTakeTool.getInstance().getMInsertsql(parmM);
		String[] arrayM = new String[] { sqlM };
		TParm parmD = parm.getParm("DTABLE");
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < parmD.getCount(); i++) {
			TParm parmRow = parmD.getRow(i);
			String sqlD = BMSTakeTool.getInstance().getDInsertsql(parmRow);
			list.add(sqlD);
		}
		String[] arrayD = list.toArray(new String[list.size()]);
		String[] arraysql = StringTool.copyArray(arrayM, arrayD);
		TParm result = new TParm(TJDODBTool.getInstance()
				.update(arraysql, conn));
		if (result.getErrCode() < 0) {
		
			err("ERR:" + result.getErrCode() + result.getErrText()
			+ result.getErrName());
			return result;
		}
		
		return result;
	}
}
