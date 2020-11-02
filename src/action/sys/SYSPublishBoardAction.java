package action.sys;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.SYSPublishBoardTool;

/**
 * <p>Title: ������ҵ����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author li.xiang@790130@gmail.com
 * @version 1.0
 */
public class SYSPublishBoardAction
    extends TAction {
    public SYSPublishBoardAction() {
    }

    /**
     * ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm onPublishMessage(TParm parm) {
        //����Tool��ȡ��Ӧ�Ľ��յ��û�getReceiveUsers;
        TParm users = SYSPublishBoardTool.getInstance().getReceiveUsers(
            parm);
        //System.out.println("in control users size++++++++++++" + users.getCount());
        parm.setData("RECEIVE_USERS", users);

        //�����񷢲�����
        TConnection connection = getConnection();
        TParm result = new TParm();
        //����Tool���淽����
        result = SYSPublishBoardTool.getInstance().sendPublishMessage(parm,
            connection);

        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;

    }

    /**
     * ����ɾ������
     * @param parm TParm
     * @return TParm
     */
    public TParm onBatchDeletePublishMessage(TParm parm){
        TParm result = new TParm();
        //�����񷢲�����
        TConnection connection = getConnection();
        //����Tool���淽����
        result = SYSPublishBoardTool.getInstance().batchDeletePublishMessage(parm,
            connection);

        if (result.getErrCode() < 0) {
        	connection.rollback();
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
    public TParm onDeletePublishMessage(TParm parm) {
        TParm result = new TParm();
        //�����񷢲�����
        TConnection connection = getConnection();
        //����Tool���淽����
        result = SYSPublishBoardTool.getInstance().deletePublishMessage(parm,
            connection);

        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ��������ϸ����,�����¶���Ǽ���Ӧ��
     * @param parm TParm
     * @return TParm
     */
    public TParm onReadMessage(TParm parm) {
        TParm result = new TParm();
        //�����񷢲�����
        TConnection connection = getConnection();
        //����Tool���淽����
        result = SYSPublishBoardTool.getInstance().readMessage(parm,
            connection);

        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ��ѯĳ�˵Ľ������� add by wangqing 20171114
     * @param parm
     * @return
     */
    public TParm selectReceiveData(TParm parm){
    	TParm result = SYSPublishBoardTool.getInstance().selectReceiveData(parm);
    	 if (result.getErrCode() < 0) {
             return result;
         }
    	 return result;
    }


}
