package jdo.opd;

import java.sql.Timestamp;

import jdo.sys.Pat;

import com.dongyang.data.StringValue;
import com.dongyang.data.TModifiedData;
import com.dongyang.data.TParm;
import com.dongyang.data.TimestampValue;

/**
 *
 * <p>Title: ����ʷ</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author lizk
 * @version 1.0
 */
public class Medhistory extends TModifiedData{
    private Pat pat;
    /**
     * ������ϴ���
     */
    private StringValue icdCode = new StringValue(this);	
    /**
     * ������
     */	
    private StringValue admDate= new StringValue(this);
    /**
     * ������ ��ҽ:W ��ҽ:C
     */
    private StringValue icdType = new StringValue(this);
    /**
     * ��ע
     */
    private StringValue description = new StringValue(this);
    /**
     * ���Ŵ���
     */
    private StringValue deptCode = new StringValue(this);
    /**
     * ҽ������
     */
    private StringValue drCode = new StringValue(this);
    /**
     * �ż�ס��
     */
    private StringValue admType = new StringValue(this);
    /**
     * �����
     */
    private StringValue caseNo = new StringValue(this);
    /**
     * �����û�
     */
    private StringValue optUser = new StringValue(this);
    /**
     * ��������
     */
    private TimestampValue optDate= new TimestampValue(this) ;
    /**
     * ������ĩ
     */
    private StringValue optTerm = new StringValue(this);
    /**
     * ���ò�������
     * @param pat
     */
    public void setPat(Pat pat)
    {
        this.pat = pat;
    }
    /**
     * �õ���������
     * @return Pat
     */
    public Pat getPat()
    {
        return pat;
    }
   /**
    * �õ������
    * @return icdCode String
    */
    public String getIcdCode() {
		return icdCode.getValue();
	}
    /**
     * ���ü��������
     * @param icdCode String
     */
	public void setIcdCode(String icdCode) {
		this.icdCode.setValue(icdCode);
	}
	/**
	 * �޸ļ��������
	 * @param icdCode String
	 */
	public void modifyIcdCode(String icdCode){
		this.icdCode.modifyValue(icdCode);
	}
	/**
	 * �õ��������
	 * @return admdate String
	 */
	public String getAdmDate() {
		return admDate.getValue();
	}
	/**
	 * �����������
	 * @param admDate String
	 */
	public void setAdmDateTime(String admDate) {
		this.admDate.setValue(admDate);
	}
	/**
	 * �޸��������
	 * @param admDate String
	 */
	public void modifyAdmDate(String admDate){
		this.admDate.modifyValue(admDate);
	}
	/**
	 * �õ��������
	 * @return icdType String
	 */
	public String getIcdType() {
		return icdType.getValue();
	}
	/**
	 * �����������
	 * @param icdType String
	 */
	public void setIcdType(String icdType) {
		this.icdType.setValue(icdType);
	}
	/**
	 * �޸��������
	 * @param icdType String
	 */
	public void modifyIcdType(String icdType){
		this.icdType.modifyValue(icdType);
	}
	/**
	 * �õ���ע
	 * @return description String
	 */
	public String getDescription() {
		return description.getValue();
	}
	/**
	 * ���ñ�ע
	 * @param description String
	 */
	public void setDescription(String description) {
		this.description.setValue(description);
	}
	/**
	 * �޸ı�ע
	 * @param description String
	 */
	public void modifyDescription(String description){
		this.description.modifyValue(description);
	}
	/**
	 * �õ����Ŵ���
	 * @return deptCode String
	 */
	public String getDeptCode() {
		return deptCode.getValue();
	}
	/**
	 * ���ò��Ŵ���
	 * @param deptCode String
	 */
	public void setDeptCode(String deptCode) {
		this.deptCode.setValue(deptCode);
	}
	/**
	 * �޸Ĳ��Ŵ���
	 * @param deptCode String
	 */
	public void modifyDeptCode(String deptCode){
		this.deptCode.modifyValue(deptCode);
	}
	/**
	 * �õ�ҽʦ����
	 * @return drCode String
	 */
	public String getDrCode() {
		return drCode.getValue();
	}
	/**
	 * ����ҽʦ����
	 * @param drCode String
	 */
	public void setDrCode(String drCode) {
		this.drCode.setValue(drCode);
	}
	/**
	 * �޸�ҽʦ����
	 * @param drCode String
	 */
	public void modifyDrCode(String drCode){
		this.drCode.modifyValue(drCode);
	}
	/**
	 * �õ��ż���
	 * @return admType String
	 */
	public String getAdmType() {
		return admType.getValue();
	}
	/**
	 * �����ż���
	 * @param admType String
	 */
	public void setAdmType(String admType) {
		this.admType.setValue(admType);
	}
	/**
	 * �޸��ż���
	 * @param admType String
	 */
	public void modifyAdmType(String admType){
		this.admType.modifyValue(admType);
	}
	/**
	 * �õ������
	 * @return caseNo String
	 */
	public String getCaseNo() {
		return caseNo.getValue();
	}
	/**
	 * ���ÿ����
	 * @param caseNo String
	 */
	public void setCaseNo(String caseNo) {
		this.caseNo.setValue(caseNo);
	}
	/**
	 * �޸Ŀ����
	 * @param caseNo String
	 */
	public void modifyCaseNo(String caseNo){
		this.caseNo.modifyValue(caseNo);
	}
	/**
	 * �õ������û�
	 * @return OptUser String
	 */
	public String getOptUser() {
		return optUser.getValue();
	}
	/**
	 * ���ò����û�
	 * @param optUser String
	 */
	public void setOptUser(String optUser) {
		this.optUser.setValue(optUser);
	}
	/**
	 * �޸Ĳ����û�
	 * @param optUser String
	 */
	public void modifyOptUser(String optUser){
		this.optUser.modifyValue(optUser);
	}
	/**
	 * �õ�����ʱ��
	 * @return Optdate Timestamp
	 */
	public Timestamp getOptDate() {
		return optDate.getValue();
	}
	/**
	 * ���ò���ʱ��
	 * @param optDate Timestamp
	 */
	public void setOptDate(Timestamp optDate) {
		this.optDate.setValue(optDate);
	}
	/**
	 * �޸Ĳ���ʱ��
	 * @param optDate Timestamp
	 */
	public void modifyOptDate(Timestamp optDate) {
		this.optDate.modifyValue(optDate);
	}
	/**
	 * �õ�������ĩ
	 * @return OptTerm String
	 */
	public String getOptTerm() {
		return optTerm.getValue();
	}
	/**
	 * ���ò�����ĩ
	 * @param optTerm String
	 */
	public void setOptTerm(String optTerm) {
		this.optTerm.setValue(optTerm);
	}
	/**
	 * �޸Ĳ�����ĩ
	 * @param optTerm String
	 */
	public void modifyOptTerm(String optTerm){
		this.optTerm.modifyValue(optTerm);
	}
	public TParm getParm()
    {
        TParm result = super.getParm();
        if(getPat() != null)
            result.setData("MR_NO",pat.getMrNo());
        return result;
    }
	
}
