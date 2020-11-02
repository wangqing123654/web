package jdo.opd;

import java.sql.Timestamp;

import jdo.sys.Pat;

import com.dongyang.data.StringValue;
import com.dongyang.data.TModifiedData;
import com.dongyang.data.TParm;
import com.dongyang.data.TimestampValue;

/**
 * 
 * <p>
 * Title: ����jdo
 * </p>
 * 
 * <p>
 * Description:����jdo
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author ehui 200800909
 * @version 1.0
 */
public class DrugAllergy extends TModifiedData {
	/**
	 * {@literal������Ϣ}
	 */
	private Pat pat;
	/**
	 * ���v̖
	 */
	private StringValue mrNo = new StringValue(this);
	/**
	 * ������
	 */
	private StringValue admDate = new StringValue(this);
	/**
	 * �������� A���ɷ� B��ҩƷ C������
	 */
	private StringValue drugType = new StringValue(this);
	/**
	 * ����Դ
	 */
	private StringValue drugOringrdCode= new StringValue(this);
	/**
	 * �ż�ס��
	 */
	private StringValue admType = new StringValue(this);
	/**
	 * �������
	 */
	private StringValue caseNo= new StringValue(this);
	/**
	 * �Ʊ����
	 */
	private StringValue deptCode= new StringValue(this);
	/**
	 * ҽʦ����
	 */
	private StringValue drCode= new StringValue(this);
	/**
	 * �^������
	 */
	private StringValue allergyNote= new StringValue(this);
	/**
	 * �����ˆT
	 */
	private StringValue optUser= new StringValue(this);
	/**
	 * ��������
	 */
	private TimestampValue optDate=new TimestampValue(this);
	/**
	 * ������ĩ
	 */
	private StringValue optTerm= new StringValue(this);
	/**
	 * ������Ϣ
	 * 
	 * @param pat
	 */
	public void setPat(Pat pat) {
		this.pat = pat;
	}

	/**
	 * ������Ϣ
	 * 
	 * @return Pat
	 */
	public Pat getPat() {
		return pat;
	}

	public String getMrNo() {
		return mrNo.getValue();
	}

	public void setMrNO(String mrNO) {
		this.mrNo.setValue(mrNO);
	}

	public String getAdmDate() {
		return admDate.getValue();
	}

	public void setAdmDate(String admDate) {
		this.admDate.setValue(admDate);
	}

	public String getDrugType() {
		return drugType.getValue();
	}

	public void setDrugType(String drugType) {
		this.drugType.setValue(drugType);
	}

	public String getDrugOringrdCode() {
		return drugOringrdCode.getValue();
	}

	public void setDrugOringrdCode(String drugOringrdCode) {
		this.drugOringrdCode.setValue(drugOringrdCode);
	}


	public String getAdmType() {
		return admType.getValue();
	}

	public void setAdmType(String admType) {
		this.admType.setValue(admType);
	}

	public String getCaseNo() {
		return caseNo.getValue();
	}

	public void setCaseNo(String caseNo) {
		this.caseNo.setValue(caseNo);
	}

	public String getDeptCode() {
		return deptCode.getValue();
	}

	public void setDeptCode(String deptCode) {
		this.deptCode.setValue(deptCode);
	}

	public String getDrCode() {
		return drCode.getValue();
	}

	public void setDrCode(String drCode) {
		this.drCode.setValue(drCode);
	}

	public String getAllergyNote() {
		return allergyNote.getValue();
	}

	public void setAllergyNote(String allergyNote) {
		this.allergyNote.setValue(allergyNote);
	}

	public String getOptUser() {
		return optUser.getValue();
	}

	public void setOptUser(String optUser) {
		this.optUser.setValue(optUser);
	}

	public Timestamp getOptDate() {
		return optDate.getValue();
	}

	public void setOptDate(Timestamp optDate) {
		this.optDate.setValue(optDate);
	}

	public String getOptTerm() {
		return optTerm.getValue();
	}

	public void setOptTerm(String optTerm) {
		this.optTerm.setValue(optTerm);
	}

	/**
	 * ���v̖
	 * @param mr_no 
	 */
	public void modifyMrNo(String mrNo) {
		this.mrNo.modifyValue(mrNo);
	}

	/**
	 * ������
	 * @param adm_date 
	 */
	public void modifyAdmDate(String admDate) {
		this.admDate.modifyValue(admDate);
	}

	/**
	 * �������� A���ɷ� B��ҩƷ C������
	 * @param drug_type 
	 */
	public void modifyDrugType(String drugType) {
		this.drugType.modifyValue(drugType);
	}

	/**
	 * ����Դ
	 * @param drugoringrd_code 
	 */
	public void modifyDrugoringrdCode(String drugoringrdCode) {
		this.drugOringrdCode.modifyValue(drugoringrdCode);
	}

	/**
	 * �ż�ס��
	 * @param adm_type 
	 */
	public void modifyAdmType(String admType) {
		this.admType.modifyValue(admType);
	}

	/**
	 * �������
	 * @param case_no 
	 */
	public void modifyCaseNo(String caseNo) {
		this.caseNo.modifyValue(caseNo);
	}

	/**
	 * �Ʊ����
	 * @param dept_code 
	 */
	public void modifyDeptCode(String deptCode) {
		this.deptCode.modifyValue(deptCode);
	}

	/**
	 * ҽʦ����
	 * @param dr_code 
	 */
	public void modifyDrCode(String drCode) {
		this.drCode.modifyValue(drCode);
	}

	/**
	 * �^������
	 * @param allergy_note 
	 */
	public void modifyAllergyNote(String allergyNote) {
		this.allergyNote.modifyValue(allergyNote);
	}

	/**
	 * �����ˆT
	 * @param opt_user 
	 */
	public void modifyOptUser(String optUser) {
		this.optUser.modifyValue(optUser);
	}

	/**
	 * ��������
	 * @param opt_date 
	 */
	public void modifyOptDate(Timestamp optDate) {
		this.optDate.modifyValue(optDate);
	}

	/**
	 * ������ĩ
	 * @param opt_term 
	 */
	public void modifyOptTerm(String optTerm) {
		this.optTerm.modifyValue(optTerm);
	}
	/**
	 * ȡ��pram
	 */
	public TParm getParm()
    {
        TParm result = super.getParm();
        if(getPat() != null)
            result.setData("MR_NO",pat.getMrNo());
        return result;
    }
	
}
