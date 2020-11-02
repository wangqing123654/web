package action.bil;


import jdo.bil.BILContractDTool;
import jdo.bil.BILContractMTool;
import jdo.bil.BILContractRecordTool;
import jdo.bil.BILContractTool;
import jdo.bil.BILInvoiceTool;
import jdo.bil.BILInvrcptTool;
import jdo.bil.BILREGRecpTool;
import jdo.bil.BILTool;
import jdo.opb.OPBReceiptTool;
import jdo.opd.OrderTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.util.StringTool;

/**
 * <p>Title:��ͬ��λaction </p>
 *
 * <p>Description:��ͬ��λaction </p>
 *
 * <p>Copyright: Copyright (c) bluecore</p>
 *
 * <p>Company:bluecore </p>
 *
 * @author caowl 20130114
 * @version 1.0
 */
public class BILContractAction extends TAction {
	/**
     * ��ͬ��λ��Ϣ����
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveAcctionCompany(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"����Ϊ��");
        TConnection connection = getConnection();
        result=BILContractMTool.getInstance().insertData(parm,connection);
        if (result==null||result.getErrCode() < 0) {
        	connection.rollback();
        	connection.close();
             return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * ���º�ͬ��λ��Ϣ
     * */
    public TParm onUpdAcctionCompany(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"����Ϊ��");
        TConnection connection = getConnection();
        result=BILContractMTool.getInstance().updateData(parm,connection);
        if (result==null||result.getErrCode() < 0) {
        	connection.rollback();
        	connection.close();
             return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * ɾ����ͬ��λ��Ϣ
     * */
    public TParm onDelAcctionCompany(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"����Ϊ��");
        TConnection connection = getConnection();
        result=BILContractMTool.getInstance().deleteData(parm,connection);
        if (result==null||result.getErrCode() < 0) {
        	connection.rollback();
        	connection.close();
             return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * ������Ϣ����
     * */
    public TParm onSaveAcctionPat(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"����Ϊ��");
        TConnection connection = getConnection();
        result=BILContractDTool.getInstance().insertDataPat(parm,connection);
        if (result==null||result.getErrCode() < 0) {
        	connection.rollback();
        	connection.close();
             return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * ������Ϣ����
     * */
    public TParm onUpdAcctionPat(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"����Ϊ��");
        TConnection connection = getConnection();
        result=BILContractDTool.getInstance().updateDataPat(parm,connection);
        if (result==null||result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
             return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * ������Ϣɾ��
     * */
    public TParm onDelAcctionPat(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"����Ϊ��");
        TConnection connection = getConnection();
        result=BILContractDTool.getInstance().deleteDataPat(parm,connection);
        if (result==null||result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
             return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
   
    /**
     * ��ͬ��λ��Ԥ����
     * @param parm TParm
     * @return TParm
     */
    public TParm onContractBillPay(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = BILContractTool.getInstance().onContractBillPay(parm, connection);
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
     * ��ͬ��λ��Ԥ����
     * */
    public TParm onReturnBillContractPay(TParm parm){
    	  TConnection connection = getConnection();
          TParm result = new TParm();
          result = BILContractTool.getInstance().onReturnBillContractPay(parm, connection);
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
     * ��ͬ��λ����Ԥ����
     * */
    public TParm onReturnContractPay(TParm parm, TConnection connection){
          TParm result = new TParm();
          result = BILContractTool.getInstance().onReturnContractPay(parm, connection);
          if (result.getErrCode() < 0) {
              return result;
          }
          return result;
    }
    /**
     * ��ͬ��λ���˲���
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveContract(TParm parm) {
    	TConnection connection = getConnection();
        TParm result = null;
        TParm recodeParm=parm.getParm("recodeParm");
        TParm payParm = parm.getParm("payParm");
        TParm regParm = new TParm();
        //System.out.println("recodeParm:::"+recodeParm);
        //1.���REG��Ʊ��
        //��õ�ǰƱ�ݺ�
        TParm parms = new TParm();
        parms.setData("RECP_TYPE", "REG");
        parms.setData("CASHIER_CODE", parm.getValue("OPT_USER"));
        parms.setData("STATUS", "0");
        parms.setData("TERM_IP", parm.getValue("OPT_TERM"));
        TParm noParm = BILInvoiceTool.getInstance().selectNowReceipt(parms);
               
        //2.���OPB��Ʊ��
        TParm parmsS = new TParm();
        parmsS.setData("RECP_TYPE", "OPB");
        parmsS.setData("CASHIER_CODE", parm.getValue("OPT_USER"));
        parmsS.setData("STATUS", "0");
        parmsS.setData("TERM_IP", parm.getValue("OPT_TERM"));
        TParm noParmS = BILInvoiceTool.getInstance().selectNowReceipt(parmsS);      
        boolean flg = false;
        boolean opbFlg = false;
        for (int i = 0; i < recodeParm.getCount(); i++) {
            // ��ѡ��ִ�в���
            if (recodeParm.getBoolean("FLG", i)) {
                TParm recode=recodeParm.getRow(i);                
                recode.setData("OPT_USER",parm.getValue("OPT_USER"));
                recode.setData("OPT_TERM",parm.getValue("OPT_TERM"));
                //�ҺŲ���
                if ("REG".equals(recode.getValue("RECEIPT_TYPE"))) {
                	flg = true;
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
                	//regParm.addData("recode", recode.getData());
                    //result = regSave(recode, connection);
                } else {
                	opbFlg = true;
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
                    opbReceipt.setData("PRINT_NO", noParmS.getValue("UPDATE_NO",0));
                    opbReceipt.setData("CASE_NO", parm.getValue("CASE_NO"));
                    opbReceipt.setData("OPT_TERM", parm.getValue("OPT_TERM"));
                    opbReceipt.setData("OPT_USER", parm.getValue("OPT_USER"));
                    result = OPBReceiptTool.getInstance().updatePrintNO(opbReceipt,
                            connection);
                    //�ƷѲ���
                    //result = opbSave(recode, connection);
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
           
        }
       
        if(flg){
        	//�޸�Ʊ��������
            TParm printReceipt = noParm.getRow(0);
            printReceipt.setData("UPDATE_NO",
                                 StringTool.addString(noParm.getValue("UPDATE_NO",0)));
            //System.out.println("22222222222222");
            //����дƱ
            result = BILInvoiceTool.getInstance().updateDatePrint(
                    printReceipt,
                    connection);
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
        }else if(opbFlg){
        	//�޸�Ʊ��������
            TParm printReceipt = noParmS.getRow(0);
            printReceipt.setData("UPDATE_NO",
                                 StringTool.addString(noParmS.getValue("UPDATE_NO",0)));
            //System.out.println("22222222222222");
            //����дƱ
            result = BILInvoiceTool.getInstance().updateDatePrint(
                    printReceipt,
                    connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                connection.close();
                return result;
            }
            //�����ӡ��Ʊ��BIL_Invrcpt
            TParm bilInvrcpt = new TParm();
            bilInvrcpt.setData("RECP_TYPE", "REG");
            bilInvrcpt.setData("INV_NO", noParmS.getValue("UPDATE_NO",0));
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
        }
        
        //����Ԥ����
      int count = payParm.getCount("RECEIPT_NO"); 
      //System.out.println("����Ԥ����ı���count::::"+count);
	  for (int j = 0; j < count; j++) {
			// System.out.println("Ԥ�������ݣ�����"+parm.getParm("payParm").getRow(j));
		 result = this.onReturnContractPay(
				parm.getParm("payParm").getRow(j), connection);
		 if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		 }
	  }
      connection.commit();
      connection.close();
      return result;
    }
  
}
