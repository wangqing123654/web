package jdo.med;

import com.dongyang.data.*;
import com.dongyang.jdo.*;

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
public class ExecRecordDataStore
    extends TDataStore {
    private String mrNo;
    private String caseNo;
    private String rxNo;
    private String setMainFlg;
    private String hideFlg;
    private String orderSetGroupNo;
    private String orderCode;
    private String seqNo;
    private String orderSetCode;
    /**
     * �õ�SQL
     * @return String
     */
    protected String getQuerySQL(String type){
        String sql="";
        String where = "";
        if (type!=null && (type.equals("OPO")||type.equals("OPE"))) {
        	 sql = "SELECT * FROM EXM_EXEC_RECORD WHERE CASE_NO='"+this.getCaseNo()+"' AND RX_NO='"+
        	 this.getRxNo()+"' AND ORDER_CODE='"+this.getOrderCode()+"' AND SEQ_NO='"+this.getSeqNo()+"' ORDER BY CASE_NO,RX_NO,SEQ_NO,EXEC_NO";
        	 //modify by huangtt 20141209 SEQ_NO='"+this.getSeqNo()+"
		}else{
	        if(this.getOrderSetCode()==null||this.getOrderSetCode().length()==0){
	            where += " IS NULL";
	        }else{
	            where +="='"+this.getOrderSetCode()+"'";
	        }
	        if("0".equals(this.getOrderSetGroupNo())){
	            sql = "SELECT * FROM EXM_EXEC_RECORD WHERE MR_NO='"+this.getMrNo()+"' AND CASE_NO='"+this.getCaseNo()+"' AND RX_NO='"+this.getRxNo()+"' AND ORDERSET_CODE"+where+" AND ORDERSET_GROUP_NO='"+this.getOrderSetGroupNo()+"' AND ORDER_CODE='"+this.getOrderCode()+"' ORDER BY CASE_NO,RX_NO,SEQ_NO,EXEC_NO";
	        }else{
	            sql = "SELECT * FROM EXM_EXEC_RECORD WHERE MR_NO='"+this.getMrNo()+"' AND CASE_NO='"+this.getCaseNo()+"' AND RX_NO='"+this.getRxNo()+"' AND ORDERSET_CODE"+where+" AND ORDERSET_GROUP_NO='"+this.getOrderSetGroupNo()+"' ORDER BY CASE_NO,RX_NO,SEQ_NO,EXEC_NO";
	        }
		}
        return sql;
    }
    /**
    * ��ѯ
    * @return boolean
    */
   public boolean onQuery(String type){
       if(!setSQL(getQuerySQL(type)))
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
        return false;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public String getHideFlg() {
        return hideFlg;
    }

    public String getMrNo() {
        return mrNo;
    }

    public String getOrderSetGroupNo() {
        return orderSetGroupNo;
    }

    public String getRxNo() {
        return rxNo;
    }

    public String getSetMainFlg() {
        return setMainFlg;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public String getOrderSetCode() {
        return orderSetCode;
    }


    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public void setHideFlg(String hideFlg) {
        this.hideFlg = hideFlg;
    }

    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    public void setOrderSetGroupNo(String orderSetGroupNo) {
        this.orderSetGroupNo = orderSetGroupNo;
    }

    public void setRxNo(String rxNo) {
        this.rxNo = rxNo;
    }

    public void setSetMainFlg(String setMainFlg) {
        this.setMainFlg = setMainFlg;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public void setOrderSetCode(String orderSetCode) {
        this.orderSetCode = orderSetCode;
    }

}
