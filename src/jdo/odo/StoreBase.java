package jdo.odo;

import java.sql.Timestamp;

import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
 *
 * <p>Title: ���ݴ洢������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.2.11
 * @version 1.0
 */
public class StoreBase extends TDataStore {
	/**
     * �����
     */
    private String mrNo;//yanjing 20131205
    /**
     * �����
     */
    private String caseNo;
    /**
     * ���ݿ⹤��
     */
    private TJDODBTool dbTool = new TJDODBTool();
    /**
     * ������
     */
    public StoreBase(){
    }
    //==========yanjing 20131205 ��Ӳ�����
    /**
     * �õ�������
     * @return String
     */
    public String getMrNo(){
        return this.mrNo;
    }
    /**
     * ���ò�����
     * @param caseNo String
     */
    public void setMrNo(String mrNo){	
        this.mrNo = mrNo;
    }
  //==========yanjing 20131205 ��Ӳ�����
    /**
     * �õ������
     * @return String
     */
    public String getCaseNo(){
        return this.caseNo;
    }
    /**
     * ���þ����
     * @param caseNo String
     */
    public void setCaseNo(String caseNo){
        this.caseNo = caseNo;
    }
    /**
     * �õ�SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "";
    }
    /**
     * �õ����ݿ����
     * @return TJDODBTool
     */
    public TJDODBTool getDbTool(){
    	return this.dbTool;
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
     * ��Ᵽ��
     * @return boolean
     */
    public boolean checkSave()
    {
        return true;
    }
    /**
     * ���ò����û�
     * @param optUser String �����û�
     * @param optDate Timestamp ����ʱ��
     * @param optTerm String ����IP
     * @return boolean true �ɹ� false ʧ��
     */
    public boolean setOperator(String optUser,Timestamp optDate,String optTerm)
    {
        String storeName = isFilter()?FILTER:PRIMARY;
        int rows[] = getModifiedRows(storeName);
        for(int i = 0;i < rows.length;i++)
        {
            setItem(rows[i],"OPT_USER",optUser,storeName);
            setItem(rows[i],"OPT_DATE",optDate,storeName);
            setItem(rows[i],"OPT_TERM",optTerm,storeName);
        }
        return true;
        
    }

    /**
     * ��������
     * @param row int
     * @return int
     */
    public int insertRow(int row){
        int newRow = super.insertRow(row);
        if(newRow==-1)
            return -1;
        setItem(newRow,"CASE_NO",getCaseNo());
        setItem(newRow,"MR_NO",getMrNo());//YANJING 20131205
        setActive(newRow,false);
        return newRow;
    }
   
    public boolean deleteRow(int row){
    	super.deleteRow(row);
    	return true;
    }
}
