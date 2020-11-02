package jdo.bil;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class BILInvoiceTool
    extends TJDOTool {
    private static final String GET_USING_INVOICE_RANGE_SQL =
        "SELECT START_INVNO,END_INVNO " +
        "	FROM BIL_INVOICE " +
        "	WHERE RECP_TYPE='#' AND STATUS <>'3'";
    /**
     * ʵ��
     */
    public static BILInvoiceTool instanceObject;
    /**
     * �õ�ʵ��
     * @return BILInvoiceTool
     */
    public static BILInvoiceTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILInvoiceTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BILInvoiceTool() {
        setModuleName("bil\\BILInvoiceModule.x");
        onInit();
    }

    /**
     * ��ѯȫ������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectAllData(TParm parm) {
        TParm result = query("selectAllData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ��ѯƱ������
     * @param parm TParm
     * @return TParm
     */
    public TParm selDate(TParm parm) {
        TParm result = query("selData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * ��Ʊ��������������
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm) {
        TParm result = update("insertData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ���˸�����������
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updataData(TParm parm, TConnection connection) {
        TParm result = update("updataData", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ������������
     * @param parm TParm
     * @return TParm
     */
    public TParm updataData(TParm parm) {
        TParm result = update("updataData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ���ظ�����������
     * @param parm TParm
     * @return TParm
     */
    public TParm updatainData(TParm parm) {
        TParm result = update("updatainData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ���Ʊ���Ƿ�ʹ�ù�
     * @param recpType String
     * @return boolean
     */
    public boolean checkData(String recpType) {
        String sql = this.GET_USING_INVOICE_RANGE_SQL.replaceFirst("#",
            recpType);
        TParm usingParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (usingParm.getErrCode() != 0) {
            return false;
        }
        //todo

        return true;
    }

    /**
     * ��˸����Ʊ���ֶ��Ƿ�����Ҫ��˵�Ʊ���ֶ��ڣ������򷵻��棬�粻����Ҫ��˵�Ʊ���ֶ��ڣ����ؼ�
     * @param startInvo String
     * @param endInvo String
     * @param checkStart String
     * @param checkEnd String
     * @return boolean
     */
    private boolean checkInRange(String startInvo, String endInvo,
                                 String checkStart, String checkEnd) {
        if (startInvo == null || startInvo.trim().length() == 0) {
            return false;
        }
        if (endInvo == null || endInvo.trim().length() == 0) {
            return false;
        }
        if (checkStart == null || checkStart.trim().length() == 0) {
            return false;
        }
        if (checkEnd == null || checkEnd.trim().length() == 0) {
            return false;
        }

        return true;
    }

    /**
     * ��ӡƱ�ݸ��µ�ǰƱ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateDatePrint(TParm parm, TConnection connection) {
        TParm result = update("updateDatePrint", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �õ���ǰƱ�ź���ʼƱ��
     * @param parm TParm
     * @return String[]
     */
    public String[] getUpdateUpdateNo(TParm parm) {
        TParm result = query("selectUpdateNo", parm);
        String[] error = new String[] {
            "-1", "-1"};
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return error;
        }
        String[] returndata = new String[] {
            result.getValue("UPDATE_NO", 0), result.getValue("START_INVNO", 0)};
        return returndata;

    }

    /**
     * �õ���ǰʹ�õ�Ʊ��
     * @param parm TParm
     * @return String[]
     */
    public TParm selectNowReceipt(TParm parm) {
        TParm result = query("selectNowReceipt", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ����Ʊ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm upadjustData(TParm parm, TConnection connection) {
        TParm result = update("upadjustData", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    
    
    /**
     * �ж�����Ʊ�����Ƿ�����Ѵ���Ʊ��  2016/11/15 yanmm
     * @param parm TParm
     * @return TParm
     */
    
    public TParm getTearPrintRepNo(TParm parm) {
        TParm result = query("getTearPrintRepNo", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    

}
