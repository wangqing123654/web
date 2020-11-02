package jdo.dev;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DevReceiptDDataStore extends TDataStore {
    /**
     * ���յ���
     */
    private String receiptNo;
    /**
     * �õ��빺����
     * @return String
     */
    public String getReceiptNo() {
        return receiptNo;
    }
    /**
     * �����빺����
     * @param requestNo String
     */
    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }
    /**
     * �õ�SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "SELECT * FROM DEV_RECEIPTD WHERE RECEIPT_NO='"+this.getReceiptNo()+"' ORDER BY SEQ_NO";
    }
    /**
    * ��ѯ
    * @return boolean
    */
   public boolean onQuery(){
       if(!setSQL(getQuerySQL()))
           return false;
       if(retrieve()==-1)
           return false;
       return true;
   }
    /**
     * �õ�δ֪������
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TParm parm, int row, String column) {
        System.out.println("parm:"+parm+"��:"+row+"column:"+column);
        if("XJ".equals(column)){
            return parm.getDouble("UNIT_PRICE",row)*parm.getDouble("RECEIPT_QTY",row);
        }
        return null;
    }
    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

}
