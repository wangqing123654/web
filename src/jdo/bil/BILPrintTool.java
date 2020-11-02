package jdo.bil;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.opb.OPBReceiptTool;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import com.dongyang.data.TNull;
import jdo.opd.OrderTool;

public class BILPrintTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static BILPrintTool instanceObject;
    /**
     * �õ�ʵ��
     * @return BILPrintTool
     */
    public static BILPrintTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILPrintTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BILPrintTool() {
        onInit();
    }

    /**
     * ����ҽ�ƿ���Ʊ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm saveEktOpb(TParm parm, TConnection connection) {
        //���µ�ǰƱ��BIL_Invoice
        TParm printReceipt = (TParm) parm.getParm("BILINVOICE");
//        //ȡ��ԭ��õ�Ʊ�ݺ�
//        String receiptNo = SystemTool.getInstance().getNo("ALL", "OPB",
//                "RECEIPT_NO",
//                "RECEIPT_NO");
        String receiptNo = parm.getValue("RECEIPT_NO");
//        String receiptNo = 
        //System.out.println("��ȡ����˳��š�������" + receiptNo);
        //����дƱ
        TParm result = BILInvoiceTool.getInstance().updateDatePrint(
                printReceipt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //�����ӡ��Ʊ��BIL_Invrcpt
        TParm bilInvrcpt = (TParm) parm.getParm("BILINVRICPT");
        //�վ���ˮ��
        bilInvrcpt.setData("RECEIPT_NO", receiptNo);
        bilInvrcpt.setData("CANCEL_FLG", "0");
        bilInvrcpt.setData("STATUS", "0");
        bilInvrcpt.setData("ADM_TYPE", parm.getValue("ADM_TYPE")); //====pangben 2012-3-19
        //����дƱ����ϸ
        result = BILInvrcptTool.getInstance().insertData(bilInvrcpt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result.addData("RECEIPT_NO", receiptNo);
        return result;
    }

    /**
     * �����շ�
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm saveOpb(TParm parm, TConnection connection) {
//        System.out.println("�����շ�Ʊ��"+parm);
        //�õ�һ��Ʊ��
        TParm result = null;
        TParm receiptOne = (TParm) parm.getParm("OPBRECEIPT");
        double arAmt = receiptOne.getDouble("AR_AMT");
        String billFlg = parm.getValue("billFlg"); //==========pangben modify 20110818 ���˲��� N ���ˣ�Y ������
        String contractCode = parm.getValue("CONTRACT_CODE"); //==========pangben modify 20110818 ���˵�λ
//        if (arAmt <= 0) {//======pangben 2014-8-22 ע��
//            receiptOne.setData("PRINT_NO", new TNull(String.class));
//        }
        //============= ���˲��� ������Ʊ�źʹ�ӡʱ��
        if ("N".equals(billFlg)) {
            receiptOne.setData("PRINT_NO", new TNull(String.class));
            receiptOne.setData("PRINT_DATE", new TNull(String.class));
            receiptOne.setData("PRINT_USER", new TNull(String.class));
        }
        //���ô洢Ʊ��
        result = OPBReceiptTool.getInstance().initReceipt(receiptOne,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        //��̨ȡ��ԭ��ȡ���Ĵ�Ʊ��
//        String receiptNo = result.getData("RECEIPT_NO").toString();
        String receiptNo = receiptOne.getData("RECEIPT_NO").toString();
        //=========pangben 20110818 ���˲����������
        if ("N".equals(billFlg)) {
            receiptOne.setData("RECEIPT_NO", receiptNo);
            result = insertRecode(receiptOne, contractCode, connection);
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return result;
            }
            //��OPD_ORDER ���еĴ�ӡƱ�� PRINT_FLG�� �ĳ�û�� N ��ʾû�д�Ʊ
            TParm p = new TParm();
            p.setData("CASE_NO", receiptOne.getValue("CASE_NO"));
            p.setData("PRINT_FLG", "N");
            result = OrderTool.getInstance().updateForRecode(p, connection);
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return result;
            }
        }
        //=========pangben 20110818 stop
        if (arAmt >= 0) {//====pangben 2014-8-22 �޸��ֽ���⣬arAmt=0 ����Ʊ����
            if ("Y".equals(billFlg)) { //=============pangben 20110818 ������� ���Ƿ���� Y �����ˣ���ӡƱ��  N ����
                //���µ�ǰƱ��BIL_Invoice
                TParm printReceipt = (TParm) parm.getParm("BILINVOICE");
                //����дƱ
                result = BILInvoiceTool.getInstance().updateDatePrint(
                        printReceipt,
                        connection);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    return result;
                }
                //�����ӡ��Ʊ��BIL_Invrcpt
                TParm bilInvrcpt = (TParm) parm.getParm("BILINVRICPT");
                //�վ���ˮ��
                bilInvrcpt.setData("RECEIPT_NO", receiptNo);
                bilInvrcpt.setData("CANCEL_FLG", "0");
                bilInvrcpt.setData("STATUS", "0");
                bilInvrcpt.setData("ADM_TYPE", parm.getValue("ADM_TYPE")); //=====pangben 2012-3-19
                //����дƱ����ϸ
                result = BILInvrcptTool.getInstance().insertData(bilInvrcpt,
                        connection);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    return result;
                }
            }
        } else {
            result.addData("PRINT_NOFEE", "Y");
        }
        //�洢���̵õ���Ʊ�ݺŷ���
        result.addData("RECEIPT_NO", receiptNo);
        return result;
    }

    /**
     * ���BIL_CONTRACT_RECODE ������
     * @param parm TParm
     * @param CONTRACT_CODE String
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertRecode(TParm parm, String CONTRACT_CODE,
                              TConnection connection) {
        //=================pangben 20110817
        TParm reslut = new TParm();
        reslut.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO")); //�վݺ�
        reslut.setData("CONTRACT_CODE", CONTRACT_CODE); //���˵�λ
        reslut.setData("ADM_TYPE", parm.getValue("ADM_TYPE")); //�ż�ס��
        reslut.setData("REGION_CODE", parm.getValue("REGION_CODE")); //Ժ��
        reslut.setData("CASHIER_CODE", parm.getValue("CASHIER_CODE")); //�շ���Ա
        reslut.setData("CHARGE_DATE", SystemTool.getInstance().getDate()); //�շ�����ʱ��
        reslut.setData("RECEIPT_TYPE", "OPB"); //Ʊ�����ͣ�REG ��OPB
        reslut.setData("DATA_TYPE", "OPB"); //�ۿ���Դ REG OPB  HRM
        reslut.setData("CASE_NO", parm.getValue("CASE_NO")); //�����
        reslut.setData("MR_NO", parm.getValue("MR_NO"));
        reslut.setData("AR_AMT", parm.getValue("AR_AMT")); //Ӧ�ɽ��
        reslut.setData("BIL_STATUS", "1"); //����״̬1 ����  2 �������д��  =1 ���˵�λ�ɷ�ʱ�� update =2
        reslut.setData("OPT_USER", parm.getValue("OPT_USER"));
        reslut.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        reslut.setData("RECEIPT_FLG", "1"); //״̬��1 �շ� 2���˷�
        return BILContractRecordTool.getInstance().insertRecode(reslut,
                connection);
    }

    /**
     * Ԥ���𱣴�
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm saveBilPay(TParm parm, TConnection connection) {
        //�õ�һ��Ʊ��
        TParm result = null;
        //��̨ȡ��ԭ��ȡ���Ĵ�Ʊ��
        String receiptNo = SystemTool.getInstance().getNo("ALL", "BIL",
                "RECEIPT_NO", "RECEIPT_NO");
        parm.setData("RECEIPT_NO", receiptNo);
        // System.out.println("Ԥ���𱣴�parm"+parm);
        parm.setData("PRINT_NO", parm.getValue("INV_NO"));
        parm.setData("RESET_RECP_NO", new TNull(String.class));
        //��Ԥ����
        result = BILPayTool.getInstance().insertData(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        //���µ�ǰƱ��BIL_Invoice
        TParm printReceipt = (TParm) parm.getParm("BILINVOICE");
        //����дƱ
        result = BILInvoiceTool.getInstance().updateDatePrint(printReceipt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //�����ӡ��Ʊ��BIL_Invrcpt
        TParm bilInvrcpt = (TParm) parm.getParm("BILINVRICPT");
        //�վ���ˮ��
        bilInvrcpt.setData("RECEIPT_NO", receiptNo);
        bilInvrcpt.setData("CANCEL_FLG", "0");
        bilInvrcpt.setData("STATUS", "0");
        bilInvrcpt.setData("ADM_TYPE", "I");
        //����дƱ����ϸ
        result = BILInvrcptTool.getInstance().insertData(bilInvrcpt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //�洢���̵õ���Ʊ�ݺŷ���
        result.addData("RECEIPT_NO", receiptNo);
        return result;
    }

    /**
     * �Һ��շ�
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm saveReg(TParm parm, TConnection connection) {
        //�õ�һ��Ʊ��
        TParm result = null;
        TParm receiptOne = parm.getParm("REG_RECEIPT");
        //����Щ�վ�
        result = BILREGRecpTool.getInstance().insertBill(receiptOne,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            connection.close();
            return result;
        }
        //���˲�ִ���޸�дƱ��������ִ�б���Ʊ�ݲ���
        //============pangben 20110819
        if (null != receiptOne.getValue("PRINT_NO") &&
            receiptOne.getValue("PRINT_NO").length() > 0 &&
            !receiptOne.getValue("PRINT_NO").equals("<TNULL>")) {
            TParm printReceipt = parm.getParm("BIL_INVOICE");
            printReceipt.setData("UPDATE_NO",
                                 StringTool.addString(printReceipt.
                    getData("UPDATE_NO").toString()));
            //����дƱ

            result = BILInvoiceTool.getInstance().updateDatePrint(printReceipt,
                    connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                connection.close();
                return result;
            }
//            System.out.println("����дƱ::"+parm.getParm("BIL_INVRCP"));
            //�����ӡ��Ʊ��BIL_Invrcpt
            TParm bilInvrcpt = parm.getParm("BIL_INVRCP");
            bilInvrcpt.setData("CANCEL_FLG", "0");
            bilInvrcpt.setData("STATUS", "0");
            bilInvrcpt.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));
            //����дƱ����ϸ
            result = BILInvrcptTool.getInstance().insertData(bilInvrcpt,
                    connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                connection.close();
                return result;
            }
//            System.out.println("����дƱ����ϸ");
        }
        return result;
    }

    /**
     * �ɷ���ҵ����Ʊ�ݵ�
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm saveIBSRecp(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //�����ӡ��Ʊ������BIL_Invrcpt
        TParm printReceipt = parm.getParm("BIL_INVOICE");
        printReceipt.setData("UPDATE_NO",
                             StringTool.addString(printReceipt.
                                                  getData("UPDATE_NO").toString()));
        //����дƱ
        result = BILInvoiceTool.getInstance().updateDatePrint(printReceipt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        //�����ӡ��Ʊ����ϸ��BIL_Invrcpt
        TParm bilInvrcpt = parm.getParm("BIL_INVRCP");
        bilInvrcpt.setData("CANCEL_FLG", "0");
        bilInvrcpt.setData("STATUS", "0");
        bilInvrcpt.setData("ADM_TYPE", "I");
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
     * ��÷��õ������� 
     * @param admType �ż�ס��
     * @return
     * ����TParm��ÿ�������������£���CHARGE01=�����ء�
     */
    public TParm getChargeDesc(String admType) {//add by wanglong 20130306
        String descSql = "SELECT ID, CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_CHARGE'";
        TParm descParm = new TParm(TJDODBTool.getInstance().select(descSql));
        String chargeSql = "SELECT * FROM BIL_RECPPARM A WHERE A.ADM_TYPE = '" + admType + "'";
        TParm chargeParm = new TParm(TJDODBTool.getInstance().select(chargeSql));
        TParm result = new TParm();
        for (int i = 1; i <= 20; i++) {
            String rexpCode = chargeParm.getValue("CHARGE" + StringTool.fill0(i + "", 2), 0);
            for (int j = 0; j < descParm.getCount(); j++) {
                if (descParm.getValue("ID", j).equals(rexpCode)) {
                    result.setData("CHARGE" + StringTool.fill0(i + "", 2),
                                   descParm.getValue("CHN_DESC", j).replaceAll("\\(ס\\)", "")
                                           .replaceAll("��ס��", ""));
                }
            }
        }
        return result;
    }
}
