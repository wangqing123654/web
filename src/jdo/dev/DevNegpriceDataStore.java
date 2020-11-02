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
 * @author Miracle
 * @version 1.0
 */
public class DevNegpriceDataStore extends TDataStore {
    /**
      * �빺����
      */
     private String requestNo;
     /**
      * �õ��빺����
      * @return String
      */
     public String getRequestNo() {
         return requestNo;
     }
     /**
      * �����빺����
      * @param requestNo String
      */
     public void setRequestNo(String requestNo) {
         this.requestNo = requestNo;
     }
     /**
      * �õ�SQL
      * @return String
      */
     protected String getQuerySQL(){
         return "SELECT * FROM DEV_NEGPRICE WHERE REQUEST_NO='"+this.getRequestNo()+"' ORDER BY SEQ_NO";
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
      * SEQ_NO;CHOOSE_FLG;SUP_CODE;SUP_SALES1_TEL;SUP_SALES1;TOT_AMT;MAN_CODE;REMARK
      */
     public Object getOtherColumnValue(TParm parm, int row, String column) {
         //system.out.println("parm:"+parm+"��:"+row+"column:"+column);
         if("SUP_TEL".equals(column)){
            String supCode = parm.getValue("SUP_CODE",row);
            return getSupSaleS1TelTel(supCode);
         }
         if("SUP_SALES1".equals(column)){
             String supCode = parm.getValue("SUP_CODE",row);
             return getSupSales1(supCode);
         }
         return null;
     }
     /**
      * �õ��绰
      * @param supCode String
      * @return String
      */
     public String getSupSaleS1TelTel(String supCode){
         TParm parm = new TParm(this.getDBTool().select("SELECT SUP_SALES1_TEL FROM SYS_SUPPLIER WHERE SUP_CODE='"+supCode+"'"));
         if(parm.getCount()<0)
             return "";
         return parm.getValue("SUP_SALES1_TEL",0);
     }
     /**
      * �õ���ϵ��
      * @param supCode String
      * @return String
      */
     public String getSupSales1(String supCode){
         TParm parm = new TParm(this.getDBTool().select("SELECT SUP_SALES1 FROM SYS_SUPPLIER WHERE SUP_CODE='"+supCode+"'"));
         if(parm.getCount()<0)
             return "";
         return parm.getValue("SUP_SALES1",0);
     }

     /**
      * �������ݿ��������
      * @return TJDODBTool
      */
     public TJDODBTool getDBTool() {
         return TJDODBTool.getInstance();
     }
     /**
      * ����һ������
      * @return boolean
      */
     public String[] insertRow(TParm parm){
         int seqNo = this.rowCount()+1;
         int row = this.insertRow();
         String columns[] = this.getColumns();
         for(String temp:columns){
             if("SEQ_NO".equals(temp)){
                 this.setItem(row,temp,seqNo);
                 continue;
             }
             this.setItem(row,temp,parm.getData(temp));
         }
         return this.getUpdateSQL();
     }
     /**
      * ������
      * @param row int
      * @param parm TParm
      * @return boolean
      */
     public boolean updateRow(int row,TParm parm){
         String columns[] = this.getColumns();
         for(String temp:columns){
             if("SEQ_NO".equals(temp)){
                 continue;
             }
             this.setItem(row,temp,parm.getData(temp));
         }
         return this.update();
     }
     /**
      * ɾ����
      * @param row int
      * @return String[]
      */
     public String[] deleteRows(int row){
         this.deleteRow(row);
         //system.out.println("======="+this.getDeleteCount());
         this.showDebug();
         return this.getUpdateSQL();
     }
     /**
      * ����δ֪��ֵ
      * @param parm TParm
      * @param row int
      * @param column String
      * @param value Object
      * @return boolean
      */
     public boolean setOtherColumnValue(TParm parm, int row, String column, Object value) {
         return false;
     }

}
