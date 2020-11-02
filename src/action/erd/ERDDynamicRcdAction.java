package action.erd;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.erd.ErdDynamicRcdTool;
import jdo.erd.ErdForBedAndRecordTool;

/**
 * <p>Title: �������۶�̬��¼Action</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-11-12
 * @version 1.0
 */
public class ERDDynamicRcdAction
    extends TAction {
    public ERDDynamicRcdAction() {
    }

    /**
     * ת��
     * @param parm
     * @return TParm
     */
    public TParm onTransfer(TParm parm) {

        TParm result = new TParm();
        //����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
        TConnection connection = getConnection();

        result = ErdDynamicRcdTool.getInstance().onTransfer(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }

        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ���ô�
     * @param parm
     * @return TParm
     */
    public TParm setBed(TParm parm) {

        TParm result = new TParm();
        //����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
        TConnection connection = getConnection();

        result = ErdDynamicRcdTool.getInstance().setErdBed(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        if(!parm.getBoolean("IORU_FLG")){
            result = ErdForBedAndRecordTool.getInstance().insertErdRecord(parm.
                getParm("PAT_INFO"), connection);
        }else{
            result = ErdForBedAndRecordTool.getInstance().updateErdRecord(parm.
                getParm("PAT_INFO"), connection);
        }
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }


        result = ErdDynamicRcdTool.getInstance().updateAdmStatus(parm,
            connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }

        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ת��
     * @param parm
     * @return TParm
     */
    public TParm setPatRecord(TParm parm) {

        TParm result = new TParm();
        //����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
        TConnection connection = getConnection();

        result = ErdDynamicRcdTool.getInstance().setErdRecord(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        result = ErdDynamicRcdTool.getInstance().updateAdmStatus(parm,
            connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }

        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ���²���ERD_RECORD
     * @param parm
     * @return TParm
     */
    public TParm updatePatRecord(TParm parm) {

        TParm result = new TParm();
        //����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
        TConnection connection = getConnection();

        result = ErdDynamicRcdTool.getInstance().updateErdRecord(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        result = ErdDynamicRcdTool.getInstance().updateAdmStatus(parm,
            connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        //���������������ۺ��崲
        if (parm.getBoolean("OUT_FLG")) {
            result = ErdDynamicRcdTool.getInstance().clearErdBed(parm,
                connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        }
        if (parm.getBoolean("RETURN_FLG")) {
            TParm inData = new TParm();
            inData.setData("ERD_REGION_CODE", parm.getData("ERD_REGION_CODE_R"));
            inData.setData("BED_NO", parm.getData("BED_NO_R"));
            inData.setData("CASE_NO",  parm.getData("CASE_NO"));
            inData.setData("MR_NO", parm.getData("MR_NO"));
            inData.setData("OCCUPY_FLG", "Y");
            inData.setData("OPT_USER", parm.getData("OPT_USER"));
            inData.setData("OPT_TERM", parm.getData("OPT_TERM"));
            result = ErdForBedAndRecordTool.getInstance().updateErdBed(inData,connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }


}
