package jdo.odo;

import java.sql.Timestamp;

/**
*
* <p>Title: �ҺŴ洢����</p>
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
public class RegPatAdm extends StoreBase{

	/**
     * ������
     */
    private String mrNo;
    /**
     * �����
     */
    private String caseNo;
    /**
     * ���1
     */
    private String ctz1Code;
    /**
     * ���2
     */
    private String ctz2Code;
    /**
     * ���3
     */
    private String ctz3Code;
    /**
     * ����״̬
     */
    private String admStatus;
    /**
     * ����״̬
     */
    private String reportStatus;
    /**
     * �Ƿ���
     */
    private String seeDrFlg;
    
    /**
     *�Ƿ������� 
     *caowl
     * */
    private String firstFlg;
    /**
     *����״̬
     *huangtt
     * */
    private String visitState;
    
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
    	this.caseNo=caseNo;
    }
    /**
     * �������1
     * @param ctz1Code String
     */
    public void setCtz1Code(String ctz1Code)
    {
        this.ctz1Code = ctz1Code;
    }
    /**
     * �õ����1
     * @return ctz1Code String
     */
    public String getCtz1Code()
    {
        return ctz1Code;
    }
    /**
     * �������2
     * @param ctz2Code String
     */
    public void setCtz2Code(String ctz2Code)
    {
        this.ctz2Code = ctz2Code;
    }
    /**
     * �õ����2
     * @return ctz2Code String
     */
    public String getCtz2Code()
    {
        return ctz2Code;
    }
    /**
     * �������3
     * @param ctz3Code String
     */
    public void setCtz3Code(String ctz3Code)
    {
        this.ctz3Code = ctz3Code;
    }
    /**
     * �õ����3
     * @return ctz3Code String
     */
    public String getCtz3Code()
    {
        return ctz3Code;
    }
    /**
     * ���þ���״̬
     * @param admStatus String
     */
    public void setAdmStatus(String admStatus){
    	this.admStatus=admStatus;
    }
    /**
     * �õ�����״̬
     * @return admStatus String
     */
    public String getAdmStatus(){
    	return this.admStatus;
    }
    /**
     * ���ñ���״̬
     * @param reportStatus String
     */
    public void setReportStatus(String reportStatus){
    	this.reportStatus=reportStatus;
    }
    /**
     * �õ�����״̬
     * @return reportStatus String
     */
    public String getReportStatus(){
    	return this.reportStatus;
    }
    /**
     * �����Ƿ���
     * @param seeDrFlg String
     */
    public void setSeeDrFlg(String seeDrFlg){
    	this.seeDrFlg=seeDrFlg;
    }
    /**
     * �õ��Ƿ���
     * @return seeDrFlg String
     */
    public String getSeeDrFlg (){
    	return this.seeDrFlg;
    }
    /**
     * �õ��Ƿ�����
     * caowl
     * */
    public String getFirstFlg(){
    	return this.firstFlg;
    }
    /**
     * �����Ƿ�����
     * caowl
     * */
    public void setFirstFlg(String firstFlg){
    	this.firstFlg = firstFlg;
    }
    /**
     * �õ�����״̬
     * huangtt
     * */
    public String getVisitState(){
    	return this.visitState;
    }
    /**
     * ���þ���״̬
     * huangtt
     * */
    public void setVisitState(String visitState){
    	this.visitState = visitState;
    }
    /**
     * �õ�SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "SELECT * FROM REG_PATADM WHERE CASE_NO='" + getCaseNo() + "' ORDER BY CASE_NO";
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
