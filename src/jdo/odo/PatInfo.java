package jdo.odo;

import java.sql.Timestamp;

/**
*
* <p>Title: �����洢����</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author ehui 2009.2.11
* @version 1.0
*/
public class PatInfo extends StoreBase{


	/**
     * ������
     */
    private String mrNo;

    /**
     * �õ�������
     * @return String
     */
    public String getMrNo()
    {
        return this.mrNo;
    }
    /**
     * ���ò�����
     * @param mrNo String
     */
    public void setMrNo(String mrNo)
    {
        this.mrNo = mrNo;
    }
    /**
     * �õ�SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "SELECT * FROM SYS_PATINFO WHERE MR_NO='" + getMrNo() + "' ";
    }
    /**
     * ��������
     * @param row int
     * @return int
     */
    public int insertRow(int row){
//        int newRow = super.insertRow(row);
//        if(newRow==-1)
//            return -1;
        //�����ż�ס��
//        setItem(0,"CTZ1_CODE",this.getCtz1Code());
//        setItem(0,"CTZ2_CODE",this.getCtz2Code());
//        setItem(0,"CTZ3_CODE",this.getCtz3Code());
//        setItem(0,"ADM_STATUS",this.getAdmStatus());
//        setItem(0,"REPORT_STATUS",this.getReportStatus());
//        setItem(0,"SEE_DR_FLG",this.getSeeDrFlg());
        return 0;
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
        return super.setOperator(optUser,optDate,optTerm);
    }



}
