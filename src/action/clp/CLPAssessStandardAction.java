package action.clp;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.clp.CLPAssessStandardTool;

/**
 * <p>Title: �ٴ�·��������׼Action</p>
 *
 * <p>Description: �ٴ�·��������׼</p>
 *
 * <p>Copyright: Copyright (c) Javahis 2011</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPAssessStandardAction extends TAction {

    public CLPAssessStandardAction() {
        super();
    }

    public TParm deleteCLP_EVL_CAT(TParm parm) {
//        System.out.println("parm��--------" + parm);
        TConnection conn = this.getConnection();
        TParm result = null;
        //ɾ����һ�����е�����
        if (parm.getValue("INDEX").equals("2")) {
//            System.out.println("ɾ��1-----------�����-------------------");
            //ɾ����1�����е�����
            result = CLPAssessStandardTool.getInstance().deleteCLP_EVL_CAT(
                    "deleteCLP_EVL_CAT1", parm, conn);
            if (result.getErrCode() < 0) {
                conn.rollback();
                conn.close();
                return result;
            }
            //ɾ����2�����е�����
            result = CLPAssessStandardTool.getInstance().deleteCLP_EVL_CAT(
                    "deleteCLP_EVL_CAT2WithParentID1", parm, conn);
            if (result.getErrCode() < 0) {
                conn.rollback();
                conn.close();
                return result;
            }
            //ɾ����3�����е�����
            result = CLPAssessStandardTool.getInstance().deleteCLP_EVL_CAT(
                    "deleteCLP_EVL_CAT3WithParentID1", parm, conn);
            if (result.getErrCode() < 0) {
                conn.rollback();
                conn.close();
                return result;
            }
        } else if (parm.getValue("INDEX").equals("3")) {
//            System.out.println("ɾ��2------------�з���------------------");
            result = CLPAssessStandardTool.getInstance().deleteCLP_EVL_CAT(
                    "deleteCLP_EVL_CAT2", parm, conn);
            if (result.getErrCode() < 0) {
                conn.rollback();
                conn.close();
                return result;
            }
            //ɾ����3�����е�����
            result = CLPAssessStandardTool.getInstance().deleteCLP_EVL_CAT(
                    "deleteCLP_EVL_CAT3WithParentID2", parm, conn);
            if (result.getErrCode() < 0) {
                conn.rollback();
                conn.close();
                return result;
            }

        }else{
            result = CLPAssessStandardTool.getInstance().deleteCLP_EVL_CAT(
                    "deleteCLP_EVL_CAT3", parm, conn);
            if (result.getErrCode() < 0) {
                conn.rollback();
                conn.close();
                return result;
            }
        }
        conn.commit();
        conn.close();
        return result;
    }
}
