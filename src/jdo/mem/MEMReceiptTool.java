package jdo.mem;


import jdo.bil.BILAccountTool;
import jdo.bil.BILInvrcptTool;
import jdo.opb.OPBReceiptTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class MEMReceiptTool extends TJDOTool{
	
	 /**
     * ʵ��
     */
    public static MEMReceiptTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return OPBReceiptTool
     */
    public static MEMReceiptTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MEMReceiptTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public MEMReceiptTool() {
        setModuleName("bil\\BILMemRecepModule.x");
        onInit();
    }
	

	 /**
     * �����ײͽ�ת�����վ�
     * @param parm
     * @param connection
     * @return
     */
    public TParm insertMemReceipt(TParm parm, TConnection connection) {
       
        TParm result = update("insertMemReceipt", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    
    /**
     * //������Ϊʹ��״̬
     * @param parm
     * @param connection
     * @return
     */
    public TParm updateMemPackSessionD(TParm parm, TConnection connection){
    	 TParm result = update("updateMemPackSessionD", parm, connection);
         if (result.getErrCode() < 0) {
             err(result.getErrCode() + " " + result.getErrText());
             return result;
         }
         return result;
    }
    
    /**
     * �����ս�Ʊ������
     *
     * @param parm TParm
     * @return TParm
     */
    public TParm getMemRecpCount(TParm parm) {
        TParm result = query("getMemRecpCount", parm);
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
     * �õ��˷�Ʊ��Ʊ��(�ս�ǰ)
     * @param parm TParm
     * @return String
     */
    public  String getBackPrintNo(TParm parm) {
        if (parm == null)
            return "";
        String sql =
                "SELECT PRINT_NO,AR_AMT FROM BIL_MEM_RECP " +
                " WHERE PRINT_DATE<TO_DATE('" + parm.getValue("PRINT_DATE") +
                "','YYYYMMDDHH24MISS')" +
                "   AND CASHIER_CODE = '" + parm.getValue("CASHIER_CODE") +
                "' " +
                "   AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL) " +
                "   AND AR_AMT < 0 " +
                " AND (PRINT_NO IS NOT NULL OR PRINT_NO <> '') " +
                " AND ADM_TYPE = '" + parm.getValue("ADM_TYPE") + "'";
        return sql;
    }
    
    
    /**
     * �����ս�
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onSaveAccountMem(TParm parm, TConnection connection) {
        TParm saveParm = parm.getParm("ACCOUNT");
        TParm result = new TParm();
        //ȡ��ԭ��õ��ս��
        String accountSeq = SystemTool.getInstance().getNo("ALL", "MEM",
                "ACCOUNT_SEQ",
                "ACCOUNT_SEQ");
        saveParm.setData("ACCOUNT_SEQ", accountSeq);
        if (accountSeq == null || accountSeq.equals("")) {
            out("�ս��ȡ�ô���!");
            return result.newErrParm( -1, "�ս��ȡ�ô���!");
        }
        //�ս�Ʊ�ݵ�
        TParm receiptParm = new TParm();
        receiptParm.setData("ACCOUNT_SEQ", saveParm.getValue("ACCOUNT_SEQ"));
        receiptParm.setData("ACCOUNT_USER", saveParm.getValue("ACCOUNT_USER"));
        receiptParm.setData("ADM_TYPE", saveParm.getValue("ADM_TYPE"));
        receiptParm.setData("CASHIER_CODE", saveParm.getValue("ACCOUNT_USER"));
        receiptParm.setData("BILL_DATE", saveParm.getData("ACCOUNT_DATA"));
        if (receiptParm.getValue("ADM_TYPE").equals("") ||
            receiptParm.getValue("ADM_TYPE") == null) {
            result = saveAcconntReceiptAll(receiptParm,connection);
        } else {
            result = saveAcconntReceipt( receiptParm, connection);
        }
        if (result.getErrCode() < 0) {
            err("�ս�receipt���� " + result.getErrText());
            return result;
        }
        //�ս��ӡƱ�ݵ�
        TParm invrcptParm = new TParm();
        invrcptParm.setData("ACCOUNT_SEQ", saveParm.getValue("ACCOUNT_SEQ"));
        invrcptParm.setData("ACCOUNT_USER", saveParm.getValue("ACCOUNT_USER"));
        invrcptParm.setData("RECP_TYPE", saveParm.getValue("RECP_TYPE"));
        invrcptParm.setData("ADM_TYPE", "M");
        invrcptParm.setData("CASHIER_CODE", saveParm.getValue("ACCOUNT_USER"));
        invrcptParm.setData("BILL_DATE", saveParm.getData("ACCOUNT_DATA"));
        invrcptParm.setData("PRINT_DATE", saveParm.getData("ACCOUNT_DATA"));
        if (invrcptParm.getValue("ADM_TYPE").equals("") ||
            invrcptParm.getValue("ADM_TYPE") == null) {
            result = BILInvrcptTool.getInstance().accountAll(invrcptParm,
                    connection);
        } else {
            result = BILInvrcptTool.getInstance().account(invrcptParm,
                    connection);
        }
        if (result.getErrCode() < 0) {
            err("�ս�invrcpt���� " + result.getErrText());
            return result;
        }
        //====zhangp 20120327 start
        result = updateRestInvrcp(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //====zhangp 20120327 end
        //�ս���ս�
        TParm accountParm = new TParm();
        accountParm.setData("ACCOUNT_SEQ", saveParm.getValue("ACCOUNT_SEQ"));
        accountParm.setData("ACCOUNT_USER", saveParm.getValue("ACCOUNT_USER"));
        accountParm.setData("ACCOUNT_TYPE", saveParm.getValue("RECP_TYPE"));
        accountParm.setData("ACCOUNT_DATE", saveParm.getData("ACCOUNT_DATA"));
        accountParm.setData("ADM_TYPE", saveParm.getValue("ADM_TYPE"));
        accountParm.setData("AR_AMT", saveParm.getValue("AR_AMT"));
        accountParm.setData("OPT_USER", saveParm.getValue("ACCOUNT_USER"));
        accountParm.setData("OPT_TERM", saveParm.getValue("OPT_TERM"));
        accountParm.setData("STATUS", saveParm.getValue("STATUS"));
        accountParm.setData("INVALID_COUNT", saveParm.getValue("INVALID_COUNT"));
        accountParm.setData("REGION_CODE", saveParm.getValue("REGION_CODE"));
        if (accountParm.getValue("ADM_TYPE").equals("") ||
            accountParm.getValue("ADM_TYPE") == null) {
            accountParm.setData("ADM_TYPE", "M");
            result = BILAccountTool.getInstance().insertAccount(accountParm,
                    connection);
           
        } else {
            result = BILAccountTool.getInstance().insertAccount(accountParm,
                    connection);
        }
        if (result.getErrCode() < 0) {
            err("�ս�����ս����ݴ��� " + result.getErrText());
            return result;
        }
        //===zhangp 20120619 start
        result = updateReprintInvrcp(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //===zhangp 20120619 end
        return result;

    }
    
    /**
     * ���¸������ϣ���ӡ���շ��ս�
     * ====zhangp 20120617
     * @param parm
     * @param connection
     * @return
     */
    public TParm updateReprintInvrcp(TParm parm, TConnection connection){
//    	System.out.println("���¸������ϣ���ӡ���շ��ս�");
    	parm = parm.getParm("ACCOUNT");
    	String billDate = parm.getData("ACCOUNT_DATA").toString();
    	String date = billDate.substring(0, 8) + "000000";
    	String sql = 
    		" SELECT INV_NO" +
    		" FROM BIL_INVRCP" +
    		" WHERE RECP_TYPE = 'MEM'" +
    		" AND CANCEL_USER = '" + parm.getData("ACCOUNT_USER") + "'" +
    		" AND CANCEL_FLG IN ('1','3')" +
    		" AND ADM_TYPE = '" + parm.getData("ADM_TYPE") + "'" +
    		" AND ACCOUNT_SEQ <> '" + parm.getData("ACCOUNT_SEQ") + "'" +
    		" AND ACCOUNT_DATE < TO_DATE ('" + date + "', 'YYYYMMDDHH24MISS')" +
    		" AND ACCOUNT_DATE < CANCEL_DATE";
//    	System.out.println("���¸������ϣ���ӡ���շ��ս�sql==="+sql);
        TParm temp = new TParm(TJDODBTool.getInstance().select(sql));
        if (temp.getErrCode() < 0) {
            return temp;
        }
        if (temp.getCount() < 0) {
            return temp;
        }
        String printNos = "";
        for (int i = 0; i < temp.getCount(); i++) {
            printNos += ",'" + temp.getData("INV_NO", i) + "'";
        }
        printNos = printNos.substring(1, printNos.length());
        sql =
            "UPDATE BIL_INVRCP SET ACCOUNT_SEQ = '" +
            parm.getData("ACCOUNT_SEQ") + "',ACCOUNT_USER = '" +
            parm.getData("ACCOUNT_USER") +
            "',ACCOUNT_DATE = TO_DATE('" + billDate +
            "','yyyyMMddHH24miss') WHERE INV_NO IN (" + printNos +
            ") AND RECP_TYPE = 'MEM'";
//        System.out.println("���¸������ϣ���ӡ���շ��ս�updatesql==="+sql);
    TParm result = new TParm(TJDODBTool.getInstance().update(sql,
            connection));
    return result;
    }
    


    
    /**
     * ����������Ʊ�ݵ�accountSeq===zhangp 20120327
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateRestInvrcp(TParm parm, TConnection connection) {
        parm = parm.getParm("ACCOUNT");
        String billDate = parm.getData("ACCOUNT_DATA").toString();
        String sql =
                "SELECT PRINT_NO FROM BIL_MEM_RECP WHERE AR_AMT < 0 AND (ACCOUNT_FLG IS NULL OR ACCOUNT_FLG = 'N') " +
                " AND ADM_TYPE = '" + parm.getData("ADM_TYPE") +
                "' AND CASHIER_CODE = '" + parm.getData("ACCOUNT_USER") + "' " +
                " AND BILL_DATE < TO_DATE('" + billDate +
                "','yyyyMMddHH24miss') " +
                " AND RESET_RECEIPT_NO IS NULL";
        TParm temp = new TParm(TJDODBTool.getInstance().select(sql));
        if (temp.getErrCode() < 0) {
            return temp;
        }
        if (temp.getCount() < 0) {
            return temp;
        }
        String printNos = "";
        for (int i = 0; i < temp.getCount(); i++) {
            printNos += ",'" + temp.getData("PRINT_NO", i) + "'";
        }
        printNos = printNos.substring(1, printNos.length());
        String accntDate = parm.getData("ACCOUNT_DATA").toString();
        sql =
                "UPDATE BIL_INVRCP SET ACCOUNT_SEQ = '" +
                parm.getData("ACCOUNT_SEQ") + "',ACCOUNT_USER = '" +
                parm.getData("ACCOUNT_USER") +
                "',ACCOUNT_DATE = TO_DATE('" + accntDate +
                "','yyyyMMddHH24miss') WHERE INV_NO IN (" + printNos +
                ") AND RECP_TYPE = 'MEM'";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        return result;
    }
    
    
}
