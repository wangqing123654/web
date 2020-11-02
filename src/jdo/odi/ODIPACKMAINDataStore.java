package jdo.odi;

import com.dongyang.data.*;
import com.dongyang.jdo.*;
import jdo.sys.*;

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
public class ODIPACKMAINDataStore extends TDataStore {
    /**
     * �ײʹ���
     */
    private String packCode;
    /**
     * �ײ����
     */
    private String deptOrDr;
    /**
     * ������Ա
     */
    private String deptOrDrCode;
    /**
     * ҽ�����
     */
    private String rxKind;
    /**
     * ��ʼ���������� I�����ݲ���Ψһ�����ײͱ�����г�ʼ�� Q�ǲ�ѯ
     */
    private String retrieveType="I";
    /**
     * �õ��ײͱ���
     * @return String
     */
    public String getPackCode() {
        return packCode;
    }

    public String getDeptOrDr() {
        return deptOrDr;
    }

    public String getDeptOrDrCode() {
        return deptOrDrCode;
    }

    public String getRxKind() {
        return rxKind;
    }

    public String getRetrieveType() {
        return retrieveType;
    }

    /**
     * �����빺����
     * @param requestNo String
     */
    public void setPackCode(String packCode) {
        this.packCode = packCode;
    }

    public void setDeptOrDr(String deptOrDr) {
        this.deptOrDr = deptOrDr;
    }

    public void setDeptOrDrCode(String deptOrDrCode) {
        this.deptOrDrCode = deptOrDrCode;
    }

    public void setRxKind(String rxKind) {
        this.rxKind = rxKind;
    }

    public void setRetrieveType(String retrieveType) {
        this.retrieveType = retrieveType;
    }

    /**
     * �õ�SQL
     * @return String
     */
    protected String getQuerySQL(){
        String sql = "SELECT PACK_CODE,DEPT_OR_DR,DEPTORDR_CODE,RX_KIND,PACK_DESC,GFREQ_CODE,GROUTE_CODE,GDCTAGENT_CODE,OPT_USER,OPT_TERM,OPT_DATE FROM ODI_PACK_MAIN WHERE PACK_CODE='"+this.getPackCode()+"'"+this.getWhereString();
        if("I".equals(this.getRetrieveType())){
            //System.out.println("SQL==="+sql);
            return sql;
        }
        if("Q".equals(this.getRetrieveType())){
            if(this.getWhereQString().length()>0){
                sql = "SELECT PACK_CODE,DEPT_OR_DR,DEPTORDR_CODE,RX_KIND,PACK_DESC,GFREQ_CODE,GROUTE_CODE,GDCTAGENT_CODE,OPT_USER,OPT_TERM,OPT_DATE FROM ODI_PACK_MAIN WHERE "+this.getWhereQString();
                //System.out.println("SQL==="+sql);
                return sql;
            }else{
                //System.out.println("SQL==="+"SELECT PACK_CODE,DEPT_OR_DR,DEPTORDR_CODE,RX_KIND,PACK_DESC,GFREQ_CODE,GROUTE_CODE,GDCTAGENT_CODE,OPT_USER,OPT_TERM,OPT_DATE FROM ODI_PACK_MAIN "+this.getWhereQString());
                return "SELECT PACK_CODE,DEPT_OR_DR,DEPTORDR_CODE,RX_KIND,PACK_DESC,GFREQ_CODE,GROUTE_CODE,GDCTAGENT_CODE,OPT_USER,OPT_TERM,OPT_DATE FROM ODI_PACK_MAIN "+this.getWhereQString();
            }
        }
        return "SELECT PACK_CODE,DEPT_OR_DR,DEPTORDR_CODE,RX_KIND,PACK_DESC,GFREQ_CODE,GROUTE_CODE,GDCTAGENT_CODE,OPT_USER,OPT_TERM,OPT_DATE FROM ODI_PACK_MAIN WHERE PACK_CODE='"+this.getPackCode()+"'"+this.getWhereString();
    }
    /**
     * �õ���ѯ����
     * @return String
     */
    public String getWhereQString(){
        int count=0;
        String whereStr = "";
        if(this.getPackCode()!=null&&this.getPackCode().length()!=0){
            whereStr+=" PACK_CODE='"+this.getPackCode()+"'";
            count++;
        }
        if(this.getDeptOrDr()!=null&&this.getDeptOrDr().length()!=0){
            if(count>0){
                whereStr+=" AND DEPT_OR_DR='"+this.getDeptOrDr()+"'";
            }else{
                whereStr+=" DEPT_OR_DR='"+this.getDeptOrDr()+"'";
            }
            count++;
        }
        if(this.getDeptOrDrCode()!=null&&this.getDeptOrDrCode().length()!=0){
            if(count>0){
                whereStr+=" AND DEPTORDR_CODE='"+this.getDeptOrDrCode()+"'";
            }else{
                whereStr+=" DEPTORDR_CODE='"+this.getDeptOrDrCode()+"'";
            }
            count++;
        }
        if(this.getRxKind()!=null&&this.getRxKind().length()!=0){
            if(count>0){
                whereStr+=" AND RX_KIND='"+this.getRxKind()+"'";
            }else{
                whereStr+=" RX_KIND='"+this.getRxKind()+"'";
            }
            count++;
        }
        return whereStr;
    }

    /**
     * ����һ��
     * @param parm TParm
     */
    public void onInsertRow(TParm parm){
        //���ô洢�����õ��ײͱ��
        String packCodestr = SystemTool.getInstance().getNo("ALL", "ODI",
            "ODIPACK_NO", "ODIPACK_NO");
        int row = this.insertRow();
        this.setItem(row,"DEPT_OR_DR",parm.getValue("DEPT_OR_DR"));
        this.setItem(row,"DEPTORDR_CODE",parm.getValue("DEPTORDR_CODE"));
        this.setItem(row,"RX_KIND",parm.getValue("RX_KIND"));
        this.setItem(row,"PACK_CODE",packCodestr);
        if("IG".equals(parm.getValue("RX_KIND"))){
            this.setItem(row, "GFREQ_CODE", parm.getValue("GFREQ_CODE"));
            this.setItem(row, "GROUTE_CODE", parm.getValue("GROUTE_CODE"));
            this.setItem(row, "GDCTAGENT_CODE", parm.getValue("GDCTAGENT_CODE"));
        }
        this.setActive(row,false);
    }
    /**
     * �õ���ѯ����
     * @return String
     */
    public String getWhereString(){
        String whereStr = "";
        if(this.getDeptOrDr()!=null&&this.getDeptOrDr().length()!=0){
            whereStr+=" AND DEPT_OR_DR='"+this.getDeptOrDr()+"'";
        }
        if(this.getDeptOrDrCode()!=null&&this.getDeptOrDrCode().length()!=0){
            whereStr+=" AND DEPTORDR_CODE='"+this.getDeptOrDrCode()+"'";
        }
        if(this.getRxKind()!=null&&this.getRxKind().length()!=0){
            whereStr+=" AND RX_KIND='"+this.getRxKind()+"'";
        }
        return whereStr;
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
        if("DEPTUSER".equals(column)){
            TParm temp = parm.getRow(row);
            if("1".equals(temp.getValue("DEPT_OR_DR"))||"3".equals(temp.getValue("DEPT_OR_DR"))){
                TParm d = new TParm(getDBTool().select("SELECT DEPT_CHN_DESC,DEPT_ENG_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+temp.getValue("DEPTORDR_CODE")+"'"));
                return d.getValue("DEPT_CHN_DESC",0);
            }
            if("2".equals(temp.getValue("DEPT_OR_DR"))||"4".equals(temp.getValue("DEPT_OR_DR"))){
                TParm d = new TParm(getDBTool().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"+temp.getValue("DEPTORDR_CODE")+"'"));
                return d.getValue("USER_NAME",0);
            }
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
