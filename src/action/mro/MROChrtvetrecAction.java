package action.mro;

import com.dongyang.action.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import jdo.mro.MROChrActionTool;
import jdo.mro.MROChrtvetrecTool;
import jdo.mro.MROQlayControlMTool;

/**
 * <p>Title: �������Action</p>
 *
 * <p>Description: �������Action</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-4
 * @version 1.0
 */
public class MROChrtvetrecAction
extends TAction {
	// modified by WangQing at 20170209 -start
	// ɾ��connection��Ա����,ÿ�������и���ʹ���Լ���connection
	//    TConnection connection;
	// modified by WangQing at 20170209 -end
	public MROChrtvetrecAction() {
	}
	/**
	 * ���������Ϣ
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm insertdata(TParm parm){
		TParm result = new TParm();
		if(parm==null){
			result.setErr(-1,"���������ڣ�");
			return result;
		}
		// modified by WangQing at 20170209 -start
		// �½�connection����
		TConnection connection = this.getConnection();
		// modified by WangQing at 20170209 -end

		//===========add by wanglong 20130819
		result = MROQlayControlMTool.getInstance().onInsert(parm, connection);// �����ʿؼ�¼����
		if(result.getErrCode()<0){
			err("ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			connection.close();
			return result;
		}
		//===========add end
		result = MROChrActionTool.getInstance().insertdata(parm,connection);
		if(result.getErrCode()<0){
			err("ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			connection.rollback();//add by wanglong 20130819
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ��������
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm updatedata(TParm parm){
		TParm result = new TParm();
		if(parm==null){
			result.setErr(-1,"���������ڣ�");
			return result;
		}
		// modified by WangQing at 20170209 -start
		// �½�connection����
		TConnection connection = this.getConnection();
		// modified by WangQing at 20170209 -end
		result = MROChrActionTool.getInstance().updatedata(parm,connection);
		if(result.getErrCode()<0){
			err("ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ɾ������
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm deletedata(TParm parm){
		// modified by WangQing at 20170209 -start
		// �½�connection����
		TConnection connection = this.getConnection();
		// modified by WangQing at 20170209 -end
		TParm result = new TParm();
		result = MROChrActionTool.getInstance().deletedata(parm,connection);
		if(result.getErrCode()<0){
			err("ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			connection.close();
			return result;
		}
		//add by wanglong 20130819
		result = MROQlayControlMTool.getInstance().onDelete(parm,connection);
		if(result.getErrCode()<0){
			err("ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			connection.rollback();
			connection.close();
			return result;
		}
		//add end
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * �޸Ĳ������ע�ǣ�����MR_RECORD��ADM_INP���ű�ģ�
	 * @param parm TParm ���������CASE_NO��MRO_CHAT_FLG��0��δ���   1�������   2�����
	 * @return TParm
	 */
	public TParm updateMRO_CHAT_FLG(TParm parm){
		// modified by WangQing at 20170209 -start
		// �½�connection����
		TConnection connection = this.getConnection();
		// modified by WangQing at 20170209 -end
		TParm result = new TParm();
		result = MROChrtvetrecTool.getInstance().updateMRO_CHAT_FLG(parm,connection);
		if(result.getErrCode()<0){
			err("ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
}
