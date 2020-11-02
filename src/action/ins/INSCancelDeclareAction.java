package action.ins;

import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import com.dongyang.action.TAction;
import jdo.ins.INSCancelDeclareTool;
import jdo.ins.InsManager;
import jdo.sys.Operator;

/**
 *
 * <p>
 * Title: ȡ���걨
 * </p>
 *
 * <p>
 * Description:ȡ���걨
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) xueyf
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author xueyf 2012.02.11
 * @version 1.0
 */
public class INSCancelDeclareAction extends TAction {
    /**
     * ��ѯȡ���걨����
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "����Ϊ��");
        TConnection connection = null;
        try {
            connection = getConnection();
            result = INSCancelDeclareTool.getInstance().selectdata(parm);
            if (result == null || result.getErrCode() < 0) {
                return result;
            }
            connection.commit();
        } catch (Exception ex) {

        } finally {
            if (connection != null) {
                connection.close();
            }

        }
        return result;

    }

    /**
     * ��ѯȡ���걨����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateData(TParm parm, TConnection connection) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "����Ϊ��");
        result = INSCancelDeclareTool.getInstance().updateINSStatus(parm);
        if (result == null || result.getErrCode() < 0) {
            return result;
        }
        result = INSCancelDeclareTool.getInstance().updateUploadFlg(parm);
        if (result == null || result.getErrCode() < 0) {
            return result;
        }
        return result;

    }

    /**
     * ȡ���걨 ����������ְ
     * @param tableData TParm
     * @return TParm
     */
    public TParm cancelByCZ(TParm tableData) {

        TParm result = new TParm();

        if (tableData == null)
            return result.newErrParm( -1, "����Ϊ��");
        TConnection connection = null;
        try {
            connection = getConnection();
            TParm updateParm = new TParm();

            for (int i = 0; i < tableData.getCount(); i++) {

                TParm parm = new TParm();
                parm.addData("ADM_SEQ", (String) tableData
                             .getData("ADM_SEQ", i)); // �������
//                if ((tableData.getDouble("ACCOUNT_PAY_AMT", i)) > 0) {
                // �����ʻ�֧����Ϊ��ʱ

                parm.addData("NHI_HOSP_NO", tableData
                             .getValue("HOSP_NHI_NO")); // ҽԺ����
                parm.addData("ACCOUNT_PAY_AMT", tableData.getDouble(
                        "ACCOUNT_PAY_AMT", i)); // �����ʻ�֧�����
                parm.addData("PARM_COUNT", 3);
                parm.setData("PIPELINE", "DataDown_sp");
                parm.setData("PLOT_TYPE", "E9");
                TParm data = InsManager.getInstance().safe(parm);
                if (data == null || data.getErrCode() < 0) {
                    return data;
                }
//                }
                parm.removeData("ACCOUNT_PAY_AMT");
                parm.removeData("PARM_COUNT");
                parm.removeData("NHI_HOSP_NO");
                parm.addData("HOSP_NHI_NO", (String) tableData
                             .getValue("HOSP_NHI_NO")); // ҽԺ����
                parm.addData("CONFIRM_NO", (String) tableData.getData(
                        "ADM_SEQ", i));

                parm.addData("PARM_COUNT", 2);
                parm.setData("PIPELINE", "DataDown_sp");
                parm.setData("PLOT_TYPE", "H");
                TParm data1 = InsManager.getInstance().safe(parm);
                if (data1 == null || data1.getErrCode() < 0) {
                    return data;
                }
                updateParm.setData("OPT_USER", tableData.getValue("OPT_USER"));
                updateParm.setData("OPT_TERM", tableData.getValue("OPT_TERM"));
                updateParm.setData("CONFIRM_NO", (String) tableData.getData(
                        "CONFIRM_NO", i));
                updateParm.setData("YEAR_MON", (String) tableData.getData(
                        "YEAR_MON", i));
                result = updateData(updateParm, connection);
                if (result == null || result.getErrCode() < 0) {
                    return result;
                }
                throw new Exception("�������ݿ�δ�ɹ����������£�" + parm);
            }

            connection.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }

        }
        return result;
    }

    /**
     * ȡ���걨 ���������Ǿ�
     * @param tableData TParm
     * @return TParm
     */
    public TParm cancelByCJ(TParm tableData) {
        TParm result = new TParm();

        if (tableData == null)
            return result.newErrParm( -1, "����Ϊ��");
        TConnection connection = null;
        try {
            connection = getConnection();
            TParm updateParm = new TParm();
//            System.out.println("ǰ̨table����"+tableData);
            updateParm.setData("OPT_USER", Operator.getID());
            updateParm.setData("OPT_TERM", Operator.getIP());
            for (int i = 0; i < tableData.getCount(); i++) {

                TParm parm = new TParm();
                parm.addData("ADM_SEQ", (String) tableData
                             .getData("ADM_SEQ", i)); // �������
                parm.addData("HOSP_NHI_CODE", (String) tableData
                             .getValue("HOSP_NHI_NO")); // ҽԺ����
                parm.addData("PARM_COUNT", 2);
                parm.setData("PIPELINE", "DataDown_czys");
                parm.setData("PLOT_TYPE", "I");
//                System.out.println("���������˻����"+parm);
                TParm data = InsManager.getInstance().safe(parm);
                if (data == null || data.getErrCode() < 0) {
                    return data;
                }

                updateParm.setData("CONFIRM_NO", (String) tableData.getData(
                        "CONFIRM_NO", i));
                updateParm.setData("YEAR_MON", (String) tableData.getData(
                        "YEAR_MON", i));
                updateParm.setData("OPT_USER", tableData.getValue("OPT_USER"));
                updateParm.setData("OPT_TERM", tableData.getValue("OPT_TERM"));
                result = updateData(updateParm, connection);
                if (result == null || result.getErrCode() < 0) {
                    return result;
                }
                throw new Exception("�������ݿ�δ�ɹ����������£�" + parm);
            }

            connection.commit();
        } catch (Exception ex) {

        } finally {
            if (connection != null) {
                connection.close();
            }

        }
        return result;
    }
}
