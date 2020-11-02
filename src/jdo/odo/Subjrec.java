package jdo.odo;

import java.sql.Timestamp;

/**
 *
 * <p>Title: ���߿��ߴ洢����</p>
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
public class Subjrec extends StoreBase{
    /**
     * �ż�ס��
     */
    private String admType;
    /**
     * ������
     */
    private String mrNo;
    /**
     * ҽʦ
     */
    private String drCode;
    /**
     * �����ż�ס��
     * @param admType String
     */
    public void setAdmType(String admType)
    {
        this.admType = admType;
    }
    /**
     * �õ��ż�ס��
     * @return String
     */
    public String getAdmType()
    {
        return admType;
        
    }
    /**
     * ���ò�����
     * @param admType String
     */
    public void setMrNo(String mrNo)
    {
        this.mrNo = mrNo;
    }
    /**
     * �õ��ż�ס��
     * @return String
     */
    public String getMrNo()
    {
        return mrNo;
    }
    /**
     * ����ҽʦ
     * @param drCode String
     */
    public void setDrCode(String drCode)
    {
        this.drCode = drCode;
    }
    /**
     * �õ�ҽʦ
     * @return String
     */
    public String getDrCode()
    {
        return drCode;
    }
    /**
     * �õ�SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "SELECT * FROM OPD_SUBJREC WHERE CASE_NO = '" + getCaseNo() + "'";
    }
    /**
     * ��������
     * @param row int
     * @return int
     */
    public int insertRow(int row){
        int newRow = super.insertRow(row);
        if(newRow==-1){
//        	//System.out.println("insert subjuect failed");
        	return -1;
        }
            
        //�����ż�ס��
        setItem(newRow,"MR_NO",getMrNo());
        setItem(newRow,"ADM_TYPE",getAdmType());
        setItem(newRow,"DR_CODE",getDrCode());
        return newRow;
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
        int rows[] = getNewRows(storeName);
        for(int i = 0;i < rows.length;i++)
            setItem(rows[i],"ORDER_DATE",optDate,storeName);
        return super.setOperator(optUser,optDate,optTerm);
    }
}
