package action.bil;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import jdo.bil.BILContractRecordTool;
import com.dongyang.db.TConnection;
import jdo.sys.SystemTool;
import jdo.bil.BILInvoiceTool;
import com.dongyang.util.StringTool;
import jdo.bil.BILInvrcptTool;
import jdo.bil.BilInvoice;
import jdo.bil.BILREGRecpTool;
import jdo.sys.Operator;
import jdo.opd.OrderTool;
import jdo.opb.OPBReceiptTool;

/**
 * <p>Title: ���˵�λ�������</p>
 *
 * <p>Description: ���˵�λ�������</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110817
 * @version 1.0
 */
public class BILContractRecordAction extends TAction {

    public BILContractRecordAction() {
    }

    /**
     * ��ӷ���
     * @param parm TParm
     * @return TParm
     */
    public TParm insertRecode(TParm parm) {
        TConnection connection = getConnection();
        TParm result = BILContractRecordTool.getInstance().insertRecode(parm,
                connection);
        if (result == null || result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ִ�б������
     * @param parm TParm
     * @return TParm
     */
    public TParm onSave(TParm parm) {
        TConnection connection = getConnection();
        TParm result = null;
        TParm recodeParm=parm.getParm("recodeParm");
        for (int i = 0; i < recodeParm.getCount(); i++) {
            // ��ѡ��ִ�в���
            if (recodeParm.getBoolean("FLG", i)) {
                TParm recode=recodeParm.getRow(i);
                recode.setData("OPT_USER",parm.getValue("OPT_USER"));
                recode.setData("OPT_TERM",parm.getValue("OPT_TERM"));
                //�ҺŲ���
                if ("REG".equals(recode.getValue("RECEIPT_TYPE"))) {
                    result = regSave(recode, connection);
                } else {
                    //�ƷѲ���
                    result = opbSave(recode, connection);
                }
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    connection.close();
                    return result;
                }

                //�޸����״̬
                TParm parmOne=recodeParm.getRow(i);
                parmOne.setData("BIL_STATUS","2");
                parmOne.setData("RECEIPT_FLG","1");
                parmOne.setData("OPT_USER",parm.getValue("OPT_USER"));
                parmOne.setData("OPT_TERM",parm.getValue("OPT_TERM"));
                result = BILContractRecordTool.getInstance().updateRecode(
                        parmOne, connection);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    connection.close();
                    return result;
                }

            }
            connection.commit();
        }
        connection.close();
        return result;
    }

    /**
     * �ҺŲ���
     * �޸�BIL_REG_RECP �� BIL_INVOICE ��
     * ��� BIL_INVRCP ��
     * @return TParm
     */
    private TParm regSave(TParm parm, TConnection connection) {
//        String receiptNo = SystemTool.getInstance().getNo("ALL", "REG",
//                "RECEIPT_NO",
//                "RECEIPT_NO");
        //��õ�ǰƱ�ݺ�
        TParm parms = new TParm();
        parms.setData("RECP_TYPE", "REG");
        parms.setData("CASHIER_CODE", parm.getValue("OPT_USER"));
        parms.setData("STATUS", "0");
        parms.setData("TERM_IP", parm.getValue("OPT_TERM"));
        TParm noParm = BILInvoiceTool.getInstance().selectNowReceipt(parms);
        //   return null;


        //�޸�Ʊ��������
        TParm printReceipt = noParm.getRow(0);
        printReceipt.setData("UPDATE_NO",
                             StringTool.addString(noParm.getValue("UPDATE_NO",0)));
        //����дƱ
        TParm result = BILInvoiceTool.getInstance().updateDatePrint(
                printReceipt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }

        //�޸ĹҺ��վݱ����Ʊ�š�Ʊ��ʱ��
        TParm newDataRecpParm = new TParm();
        newDataRecpParm.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
        newDataRecpParm.setData("PRINT_NO", noParm.getValue("UPDATE_NO",0));
        newDataRecpParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        newDataRecpParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        newDataRecpParm.setData("OPT_USER", parm.getValue("OPT_USER"));
        result = BILREGRecpTool.getInstance().upRecpForRePrint(
                newDataRecpParm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }

        //�����ӡ��Ʊ��BIL_Invrcpt
        TParm bilInvrcpt = new TParm();
        bilInvrcpt.setData("RECP_TYPE", "REG");
        bilInvrcpt.setData("INV_NO", noParm.getValue("UPDATE_NO",0));
        bilInvrcpt.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
        bilInvrcpt.setData("CASHIER_CODE", parm.getValue("OPT_USER"));
        bilInvrcpt.setData("AR_AMT", parm.getValue("AR_AMT"));
        bilInvrcpt.setData("OPT_USER", parm.getValue("OPT_USER"));
        bilInvrcpt.setData("CANCEL_FLG", "0");
        bilInvrcpt.setData("STATUS", "0");
        bilInvrcpt.setData("ADM_TYPE", "O");
        bilInvrcpt.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        //����дƱ����ϸ
        result = BILInvrcptTool.getInstance().insertData(bilInvrcpt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        return result;
    }

    /**
     * �Ʒѷ���
     * �޸� BIL_OPB_RECP �� BIL_INVOICE �� OPD_ORDER ��
     * ��� BIL_INVRCP ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    private TParm opbSave(TParm parm, TConnection connection) {

        TParm result = null;

        //BIL_INVOICE ��
        //��õ�ǰƱ�ݺ�
        TParm parms = new TParm();
        parms.setData("RECP_TYPE", "OPB");
        parms.setData("CASHIER_CODE", parm.getValue("OPT_USER"));
        parms.setData("STATUS", "0");
        parms.setData("TERM_IP", parm.getValue("OPT_TERM"));
        TParm noParm = BILInvoiceTool.getInstance().selectNowReceipt(parms);
        TParm printReceipt = noParm.getRow(0);
        printReceipt.setData("UPDATE_NO",
                             StringTool.addString(noParm.getValue("UPDATE_NO",0)));
        result = BILInvoiceTool.getInstance().updateDatePrint(
                printReceipt,
                connection);

        //OPD_ORDER ���޸�
        TParm p = new TParm();
        p.setData("CASE_NO", parm.getValue("CASE_NO"));
        p.setData("PRINT_FLG", "Y");
        p.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
        result = OrderTool.getInstance().updateForRecode(p, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        //BIL_OPB_RECP ��
        //�޸������վݱ����Ʊ�š�Ʊ��ʱ��
        TParm opbReceipt = new TParm();
        opbReceipt.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
        opbReceipt.setData("PRINT_NO", noParm.getValue("UPDATE_NO",0));
        opbReceipt.setData("CASE_NO", parm.getValue("CASE_NO"));
        opbReceipt.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        opbReceipt.setData("OPT_USER", parm.getValue("OPT_USER"));
        result = OPBReceiptTool.getInstance().updatePrintNO(opbReceipt,
                connection);
        //�����ӡ��Ʊ��BIL_Invrcpt
        TParm bilInvrcpt = new TParm();
        bilInvrcpt.setData("RECP_TYPE", "OPB");
        bilInvrcpt.setData("INV_NO", noParm.getValue("UPDATE_NO",0));
        bilInvrcpt.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
        bilInvrcpt.setData("CASHIER_CODE", parm.getValue("OPT_USER"));
        bilInvrcpt.setData("AR_AMT", parm.getValue("AR_AMT"));
        bilInvrcpt.setData("OPT_USER", parm.getValue("OPT_USER"));
        bilInvrcpt.setData("CANCEL_FLG", "0");
        bilInvrcpt.setData("STATUS", "0");
        bilInvrcpt.setData("ADM_TYPE", "O");
        bilInvrcpt.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        //����дƱ����ϸ
        result = BILInvrcptTool.getInstance().insertData(bilInvrcpt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        return result;
    }
}

