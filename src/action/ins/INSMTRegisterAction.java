package action.ins;

import jdo.ins.INSMTRegisterTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
 * 
 * Title: ���صǼ� \���
 * 
 * Description:���صǼ� \���:���صǼǿ��߻����� \��˲�ѯ
 * 
 * Copyright: BlueCore (c) 2011
 * 
 * @author pangben 2012-1-19
 * @version 2.0
 */
public class INSMTRegisterAction extends TAction{
	/**
	 * ����޸�ִ��״̬
	 * @param parm
	 * @return
	 */
	public TParm updateStutsType(TParm parm){
		String optUser=parm.getValue("OPT_USER");//ID
		String optTerm=parm.getValue("OPT_TERM");//IP
		String statusType=parm.getValue("STATUS_TYPE_FLG");//״̬�޸�
		TConnection connection = getConnection();
		TParm result=new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			TParm tempParm=parm.getRow(i);
			tempParm.setData("OPT_USER",optUser);//ID
			tempParm.setData("OPT_TERM",optTerm);//IP
			tempParm.setData("STATUS_TYPE",statusType);//���״̬STATUS_TYPE
			result=INSMTRegisterTool.getInstance().updateStutsType(tempParm);
			if (result.getErrCode()<0) {
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}
}
