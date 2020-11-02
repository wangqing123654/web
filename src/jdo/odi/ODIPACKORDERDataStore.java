package jdo.odi;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: HISҽ��ϵͳ</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author WangM
 * @version 1.0
 */
public class ODIPACKORDERDataStore extends TDataStore {
    private String packCode;
    public String getPackCode() {
        return packCode;
    }
    public void setPackCode(String packCode) {
        this.packCode = packCode;
    }
    /**
     * �õ�SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "SELECT PACK_CODE,SEQ_NO,DESCRIPTION,SPECIFICATION,ORDER_DESC,TRADE_ENG_DESC,LINKMAIN_FLG,LINK_NO,ORDER_CODE,MEDI_QTY,MEDI_UNIT,DEPT_OR_DR,DEPTORDR_CODE,ROUTE_CODE,FREQ_CODE,DCTEXCEP_CODE,CAT1_TYPE,ORDER_CAT1_CODE,TAKE_DAYS,OPT_USER,OPT_TERM,OPT_DATE "+
                "FROM ODI_PACK_ORDER WHERE PACK_CODE='"+this.getPackCode()+"'"+
                " ORDER BY PACK_CODE,SEQ_NO";
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
        //System.out.println("����:"+parm+"��:"+row+"����:"+column);
        return null;
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
        //System.out.println("����:"+parm+"��:"+row+"����:"+column+"ֵ:"+value);
        return true;
    }



}
