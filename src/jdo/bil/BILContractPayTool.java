package jdo.bil;

import jdo.sys.SystemTool;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * ��ͬ��λԤ����
 * */
public class BILContractPayTool extends TJDOTool{
	 /**
     * ʵ��
     */
    public static BILContractPayTool instanceObject;
    /**
     * �õ�ʵ��
     * @return BILPrintTool
     */
    public static BILContractPayTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILContractPayTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BILContractPayTool() {
    	  setModuleName("bil\\BILContractPayModule.x");
	      onInit();
    }
    /**
     * ��ʾ����
     * @param parm TParm
     */
    public TParm selectData(String sqlName,TParm parm){
        TParm result = query(sqlName, parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ����Ԥ����
     * */
    public TParm updOffRecpNo(TParm parm, TConnection connection){
    	 TParm result = this.update("updOffRecpNo", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
    }
    /**
     * ��ͬ��λԤ���𱣴�
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
      
        parm.setData("PRINT_NO", parm.getValue("INV_NO"));
        parm.setData("RESET_RECP_NO", new TNull(String.class));
        //��Ԥ����  bil_contract_pay
        result = this.insertData(parm, connection);
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
        bilInvrcpt.setData("ADM_TYPE", "O");//
        //����дƱ����ϸ
        result = BILInvrcptTool.getInstance().insertData(bilInvrcpt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //����bil_contractm���Ԥ�������
        TParm contractmParm = (TParm)parm.getParm("BILCONTRACTM");
        result = BILContractMTool.getInstance().updPrePay(contractmParm,connection);
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
     * �����ͬ��λԤ����
     * @param parm TParm
    * @param connection 
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection connection) {
        TParm result = this.update("insertData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���º�ͬ��λԤ����
     * ����
     * */
    public TParm updataData(TParm parm, TConnection connection){
    	 TParm result = this.update("refundData", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
    }
}
