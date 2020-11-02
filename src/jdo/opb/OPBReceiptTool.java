package jdo.opb;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;

import jdo.reg.PatAdmTool;
import jdo.sys.SystemTool;
import com.dongyang.db.TConnection;

public class OPBReceiptTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static OPBReceiptTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return OPBReceiptTool
     */
    public static OPBReceiptTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPBReceiptTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public OPBReceiptTool() {
        setModuleName("bil\\BILOpbRecepModule.x");
        onInit();
    }

    /**
     * Ʊ�ݱ������
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm opbSave(TParm parm, TConnection connection) {
        TParm result = new TParm();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            result = initReceipt(parm.getRow(i), connection);
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return result;
            }
        }
        return result;
    }

    public TParm insertReceiptMemPrint(TParm parm, TConnection connection) {
    	TParm result = update("insertReceiptMemPrint", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ������Ʊ��
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm initReceipt(TParm parm, TConnection connection) {
        // ȡ��ԭ��õ�Ʊ�ݺ�
        String receiptNo = "";
        if (null != parm.getValue("FLG") && parm.getValue("FLG").equals("Y")) {
        } else {
            // ȡ��ԭ��õ�Ʊ�ݺ�
            receiptNo = SystemTool.getInstance().getNo("ALL", "OPB",
                    "RECEIPT_NO", "RECEIPT_NO");
            parm.setData("RECEIPT_NO", receiptNo);
        }

        TParm result = update("insertReceipt", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        result.setData("RECEIPT_NO", receiptNo); //
        return result;
    }
    
    /**
     * ����Ʊ�����ײͱ��   add by huangtt 20141216
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm updateReceiptMemPackFlg(TParm parm, TConnection connection) {      
        TParm result = update("updateReceiptMemPackFlg", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * �����˷���Ʊ��(flg true: ���һ���µ��վ�  false ���һ����������)
     * @param parm TParm
     * @param flg boolean
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertBackReceipt(TParm parm, boolean flg,
                                   TConnection connection) {
        // ȡ��ԭ��õ�Ʊ�ݺ�
    	String receiptNo="";
    	if(parm.getValue("RECEIPT_NO").length()<=0){
    		receiptNo = SystemTool.getInstance().getNo("ALL", "OPB",
                "RECEIPT_NO", "RECEIPT_NO");
    		// Ʊ����ˮ��
    		parm.setData("RECEIPT_NO", receiptNo);
    	}else{
    		receiptNo=parm.getValue("RECEIPT_NO");
    	}
        // ���Ʊ�ݺ�
        //===zhangp 20120319
//		if (flg) {
//			parm.setData("PRINT_USER","");
//		}else{
//		parm.setData("RESET_RECEIPT_NO", receiptNo);
        parm.setData("RESET_RECEIPT_NO", "");
//		parm.setData("PRINT_USER",null==parm.getValue("PRINT_USER") ||parm.getValue("PRINT_USER").length()<=0 ?"":parm.getValue("PRINT_USER"));
//		}
        TParm result = update("insertReceipt", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }      
        // ����������Ʊ����ˮ��
        result.setData("RECEIPT_NO", receiptNo);
        return result;
    }

    /**
     * �����˷�Ʊ����Ϣ
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm updateBackReceipt(TParm parm, TConnection connection) {
        TParm result = update("updateBackReceipt", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * ҽ�ƿ������˷�Ʊ����Ϣ
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */

    public TParm updateBackReceiptOne(TParm parm, TConnection connection) {
        TParm result = update("updateBackReceiptOne", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * ���Ҳ����ķ����б�:�����˲��ң�û��ִ�н�������������˷�
     *
     * @param caseNo
     *            String
     * @return TParm =======================pangben 20110823
     */
    public TParm getContractReceipt(String caseNo) {
        TParm result = new TParm();
        if (caseNo == null || caseNo.length() == 0) {
            //System.out.println("�������Ϊ��");
            return result.newErrParm( -1, "��������Ϊ��");
        }
        result.setData("CASE_NO", caseNo);
        result = query("getContractReceipt", result);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * ���Ҳ����ķ����б�
     *
     * @param caseNo
     *            String
     * @return TParm
     */
    public TParm getReceipt(String caseNo) {
        TParm result = new TParm();
        if (caseNo == null || caseNo.length() == 0) {
            //System.out.println("�������Ϊ��");
            return result.newErrParm( -1, "��������Ϊ��");
        }
        result.setData("CASE_NO", caseNo);
        result = query("getReceipt", result);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * �õ�һ������Ʊ�ݵ���Ϣ
     *
     * @param receiptNo
     *            String
     * @return TParm
     */
    public TParm getOneReceipt(String receiptNo) {
        TParm result = new TParm();
        if (receiptNo == null || receiptNo.length() == 0) {
            //System.out.println("�������Ϊ��");
            return result.newErrParm( -1, "��������Ϊ��");
        }
        result.setData("RECEIPT_NO", receiptNo);
        result = query("getOneReceipt", result);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * �õ�һ������Ʊ�ݵ���Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm getOneReceipt(TParm parm) {
        TParm result = query("getOneReceipt", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * ���²�ӡƱ��ʱƱ�ݺ�:�ֽ�
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm updatePrintNO(TParm parm, TConnection connection) {
        TParm result = update("updatePrintNO", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * ���²�ӡƱ��ʱƱ�ݺţ�ҽ�ƿ�
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm updateEKTPrintNO(TParm parm, TConnection connection) {
        TParm result = update("updateEKTPrintNO", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * �õ��ս���
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm getSumAramt(TParm parm) {
        TParm result = query("getSumAramt", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * �õ��ս���OEH
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm getSumAramtAll(TParm parm) {
        TParm result = query("getSumAramtAll", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * �õ��ս�Ʊ������
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm getReceiptCount(TParm parm) {
        TParm result = query("getReceiptCount", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * �ս�Ʊ��
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm saveAcconntReceipt(TParm parm, TConnection connection) {
        TParm result = update("account", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * �ս�Ʊ��o e h
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm saveAcconntReceiptAll(TParm parm, TConnection connection) {
        TParm result = update("accountAll", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * �õ��ս��ӡ����
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm getAccountPrint(TParm parm) {
        TParm result = query("getAccountPrint", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * ҽ�ƿ���Ʊ����,���Ʊ�ݽ��
     *
     * @param parm
     *            TParm
     * @return TParm ================pangben 20111024
     */
    public TParm getSumEKTFee(TParm parm) {
        TParm result = query("getSumEKTFee", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * ҽ�ƿ���Ʊ����,�ж��Ƿ����Ʊ��
     *
     * @param parm
     *            TParm
     * @return TParm ================pangben 20111024
     */
    public TParm getCountEKT(TParm parm) {
        TParm result = query("getCountEKT", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * ҽ�ƿ��˷�����վ�Ʊ��================pangben 20111028
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateUnPrintNo(TParm parm, TConnection connection) {
        TParm result = update("updateUnPrintNo", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * ҽ��ʹ��ҽ�ƿ���ɫͨ�� flg true: ������ ҽ���ۿ�ҽ�ƿ� �˷Ѳ��� false �������� ҽ���˷� ҽ�ƿ��ۿ�
     * @param parm TParm
     * @param result TParm
     * @param flg boolean
     * @param connection TConnection
     * @return TParm
     */
    private TParm greenPay(TParm parm, TParm result, boolean flg,
                           TConnection connection) {
        double green = 0.00;
        boolean temp = true;
        if (flg) {

            green = parm.getDouble("GREEN_BALANCE")
                    + parm.getDouble("APPLY_AMT");
            temp = green <= parm.getDouble("GREEN_PATH_TOTAL") ? true : false;
        } else {
            green = parm.getDouble("GREEN_BALANCE")
                    - parm.getDouble("APPLY_AMT");
            temp = green >= 0 ? true : false; // �ж���ɫͨ���Ƿ�ۿ���Ч
        }
        if (temp) { // ʹ����ɫͨ�� �˻�ȫ�����
            if (flg) {
                parm.setData("PAY_OTHER1", result.getDouble("PAY_OTHER1", 0)
                             + parm.getDouble("APPLY_AMT"));
                parm.setData("PAY_MEDICAL_CARD", result.getDouble(
                        "PAY_MEDICAL_CARD", 0));
                parm.setData("GREEN_BALANCE", green);
            } else {
                parm.setData("PAY_OTHER1", result.getDouble("PAY_OTHER1", 0)
                             - parm.getDouble("APPLY_AMT"));
                parm.setData("PAY_MEDICAL_CARD", result.getDouble(
                        "PAY_MEDICAL_CARD", 0));
                parm.setData("GREEN_BALANCE", green);
            }

        } else {
            if (flg) { // ʹ����ɫͨ���˻����ֽ�� ҽ�ƿ��лس����
                parm.setData("PAY_OTHER1", parm.getDouble("GREEN_PATH_TOTAL"));
                parm.setData("PAY_MEDICAL_CARD", result.getDouble(
                        "PAY_MEDICAL_CARD", 0)
                             + parm.getDouble("GREEN_PATH_TOTAL") - green);
            } else {
                result.setErr( -1, "��ɫͨ���޸�������");
                return result;
            }
        }
        TParm updateGreen = PatAdmTool.getInstance().updateEKTGreen(parm,
                connection);
        if (updateGreen.getErrCode() < 0) {
            updateGreen.setErr( -1, "��ɫͨ���޸�������");
            return updateGreen;
        }
        return updateGreen;
    }

    /**
     * ���������շ�ҽ���ۿ��޸�BIL_OPB_RECP ����ҽ���������ֵ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm concelINSAmtPrint(TParm parm, TConnection connection) {
        TParm result = selectMedicalCardAmt(parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        if (result.getCount() <= 0) {
            return null;
        }

        // ʹ����ɫͨ��
        if (null != parm.getValue("GREEN_BALANCE")
            && parm.getValue("GREEN_BALANCE").length() > 0) {
            if (parm.getDouble("GREEN_BALANCE") < parm
                .getDouble("GREEN_PATH_TOTAL")) { // ʹ����ɫͨ��
                if (greenPay(parm, result, false, connection).getErrCode() < 0) {
                    result.setErr( -1, "��ɫͨ���޸�������");
                    return result;
                }
            } else {
                // û��ʹ����ɫͨ��
                parm.setData("PAY_MEDICAL_CARD", result.getDouble(
                        "PAY_MEDICAL_CARD", 0)
                             + parm.getDouble("APPLY_AMT"));
                parm.setData("PAY_OTHER1", result.getDouble("PAY_OTHER1", 0));
            }
        } else {
            parm.setData("PAY_MEDICAL_CARD", result.getDouble(
                    "PAY_MEDICAL_CARD", 0)
                         + parm.getDouble("APPLY_AMT"));
            parm.setData("PAY_OTHER1", result.getDouble("PAY_OTHER1", 0));
        }
        result = update("updateINSAmtPrint", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * ����վ�ҽ�ƿ���� �����շ�ҽ���ۿ��޸�BIL_OPB_RECP ����ҽ���������ֵʹ��
     * @param parm TParm
     * @return TParm
     */
    private TParm selectMedicalCardAmt(TParm parm) {
        TParm result = query("selectMedicalCardAmt", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * ͨ��PRINT_NO ��ѯ�˴ξ����Ƿ���ҽ�ƿ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm seletEktState(TParm parm) {
        TParm result = query("seletEktState", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * �����ս�Ʊ������
     * ===zhangp 20120319
     * @param parm TParm
     * @return TParm
     */
    public TParm getOpbRecpCount(TParm parm) {
        TParm result = query("getOpbRecpCount", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     *
     * @param parm TParm
     * @return TParm
     */
    public TParm getOpbResetCount(TParm parm) {
        TParm result = query("getOpbResetCount", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }


    /**
     * ����BIL_OPB_RECEIPT��charge01-30,�Զ����Ʊ
     * @param parm
     * @return
     */
    public TParm updateOpbReceiptCharge(TParm parm){
    	String sql = "UPDATE BIL_OPB_RECP "+
                     "SET  CHARGE01="+parm.getValue("CHARGE01")+",CHARGE02="+parm.getValue("CHARGE02")+",CHARGE03="+parm.getValue("CHARGE03")+",CHARGE04="+parm.getValue("CHARGE04")+"," +
                          "CHARGE05="+parm.getValue("CHARGE05")+",CHARGE06="+parm.getValue("CHARGE06")+",CHARGE07="+parm.getValue("CHARGE07")+",CHARGE08="+parm.getValue("CHARGE08")+"," +
                          "CHARGE09="+parm.getValue("CHARGE09")+",CHARGE10="+parm.getValue("CHARGE10")+",CHARGE11="+parm.getValue("CHARGE11")+",CHARGE12="+parm.getValue("CHARGE12")+"," +
                          "CHARGE13="+parm.getValue("CHARGE13")+",CHARGE14="+parm.getValue("CHARGE14")+",CHARGE15="+parm.getValue("CHARGE15")+",CHARGE16="+parm.getValue("CHARGE16")+"," +
                          "CHARGE17="+parm.getValue("CHARGE17")+",CHARGE18="+parm.getValue("CHARGE18")+",CHARGE19="+parm.getValue("CHARGE19")+",CHARGE20="+parm.getValue("CHARGE20")+"," +
                          "CHARGE21="+parm.getValue("CHARGE21")+",CHARGE22="+parm.getValue("CHARGE22")+",CHARGE23="+parm.getValue("CHARGE23")+",CHARGE24="+parm.getValue("CHARGE24")+"," +
                          "CHARGE25="+parm.getValue("CHARGE25")+",CHARGE26="+parm.getValue("CHARGE26")+",CHARGE27="+parm.getValue("CHARGE27")+",CHARGE28="+parm.getValue("CHARGE28")+"," +
                          "CHARGE29="+parm.getValue("CHARGE29")+",CHARGE30="+parm.getValue("CHARGE30")+",AR_AMT="+parm.getValue("AR_AMT")+" ,TOT_AMT="+parm.getValue("TOT_AMT")+ "   "+
                     "WHERE RECEIPT_NO='"+parm.getValue("RECEIPT_NO")+"' AND CASE_NO='"+parm.getValue("CASE_NO")+"' ";
    	
    	TParm result = new TParm(TJDODBTool.getInstance().update(sql));
    	
    	return result ;
    }
    
    /**
     * ���ö���֧����ʽ,�˷�ʱ,���¶���֧����ʽ��Ǯ��  add by huangtt 20140902
     * @param parm
     * @param connection
     * @return
     */
    public TParm updateReceipt(TParm parm, TConnection connection){
    	 TParm result = update("updateReceipt", parm, connection);
         if (result.getErrCode() < 0)
             err(result.getErrCode() + " " + result.getErrText());
         return result;
    }
}
