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
 * Title: ���jdo
 * </p>
 * 
 * <p>
 * Description:���jdo
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
public class DiagRec extends TModifiedData {
	/**
	 * ������Ϣ
	 */
	private Pat pat;
	/**
	 * �������
	 */
	private StringValue caseNo = new StringValue(this);
	/**
	 * �����
	 */
	private StringValue icdCode = new StringValue(this);
	/**
	 * ������ ��ҽ:W ��ҽ:C'
	 */
	private StringValue icdType = new StringValue(this);
	/**
	 * ���\���]ӛ
	 */
	private StringValue mainDiagFlg= new StringValue(this);
	/**
	 * �ż�ס��
	 */
	private StringValue admType= new StringValue(this);
	/**
	 * �\�฽�]
	 */
	private StringValue diagNote = new StringValue(this);
	/**
	 * ����ҽʦ
	 */
	private StringValue drCode = new StringValue(this);
	/**
	 * ����ʱ��
	 */
	private TimestampValue orderDate = new TimestampValue(this);
	/**
	 * �ṹ���������濨���(MR_FILE_INDEX.FILE_NO)
	 */
	private StringValue fileNO= new StringValue(this);
	/**
	 * �����ˆT
	 */
	private StringValue optUser= new StringValue(this);
	/**
	 * ��������
	 */
	private TimestampValue optDate= new TimestampValue(this);
	/**
	 * ������ĩ
	 */
	private StringValue optTerm = new StringValue(this);

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

	/**
	 * �������
	 * 
	 * @return the cASE_NO String
	 */
	public String getCaseNo() {
		return caseNo.getValue();
	}

	/**
	 * �������
	 * 
	 * @param case_no
	 */
	public void setCaseNo(String caseNo) {
		this.caseNo.setValue(caseNo);
	}

	/**
	 * �������
	 * 
	 * @param case_no
	 */
	public void modifyCaseNo(String caseNo) {
		this.caseNo.modifyValue(caseNo);
	}

	/**
	 * �����
	 * 
	 * @return the iCD_CODE String
	 */
	public String getIcdCode() {
		return icdCode.getValue();
	}

	/**
	 * �����
	 * 
	 * @param icd_code
	 */
	public void setIcdCode(String icdCode) {
		this.icdCode.setValue(icdCode);
	}

	/**
	 * �����
	 * 
	 * @param icd_code
	 */
	public void modifyIcdCode(String ii) {
		this.icdCode.modifyValue(ii);
	}

	/**
	 * ������ ��ҽ:W ��ҽ:C
	 * 
	 * @return the icdType String
	 */
	public String getIcdType() {
		return icdType.getValue();
	}

	/**
	 * ������ ��ҽ:W ��ҽ:C
	 * 
	 * @param icdType
	 */
	public void setIcdType(String icdType) {
		this.icdType.setValue(icdType);
	}

	/**
	 * ������ ��ҽ:W ��ҽ:C
	 * 
	 * @param icdType
	 */
	public void modifyIcdType(String icdType) {
		this.icdType.modifyValue(icdType);
	}

	/**
	 * ���\���]ӛ
	 * 
	 * @return the mainDiagFlg String
	 */
	public String getMainDiagFlg() {
		return mainDiagFlg.getValue();
	}

	/**
	 * ���\���]ӛ
	 * 
	 * @param mainDiagFlg
	 */
	public void setMainDiagFlg(String mainDiagFlg) {
		this.mainDiagFlg.setValue(mainDiagFlg);
	}

	/**
	 * ���\���]ӛ
	 * 
	 * @param mainDiagFlg
	 */
	public void modifyMainDiagFlg(String mainDiagFlg) {
		this.mainDiagFlg.modifyValue(mainDiagFlg);
	}

	/**
	 * �ż�ס��
	 * 
	 * @return the admType String
	 */
	public String getAdmType() {
		return admType.getValue();
	}

	/**
	 * �ż�ס��
	 * 
	 * @param admType
	 */
	public void setAdmType(String admType) {
		this.admType.setValue(admType);
	}

	/**
	 * �ż�ס��
	 * 
	 * @param admType
	 */
	public void modifyAdmType(String admType) {
		this.admType.modifyValue(admType);
	}

	
	/**
	 * �\�฽�]
	 * 
	 * @return the diagNote String
	 */
	public String getDiagNote() {
		return diagNote.getValue();
	}

	/**
	 * �\�฽�]
	 * 
	 * @param diag_note
	 */
	public void setDiagNote(String diagNote) {
		this.diagNote.setValue(diagNote);
	}

	/**
	 * �\�฽�]
	 * 
	 * @param diagNote
	 */
	public void modifyDiagNote(String diagNote) {
		this.diagNote.modifyValue(diagNote);
	}

	/**
	 * ����ҽʦ
	 * 
	 * @return the drCode String
	 */
	public String getDrCode() {
		return drCode.getValue();
	}

	/**
	 * ����ҽʦ
	 * 
	 * @param drCode
	 */
	public void setDrCode(String drCode) {
		this.drCode.setValue(drCode);
	}

	/**
	 * ����ҽʦ
	 * 
	 * @param drCode
	 */
	public void modifyDrCode(String drCode) {
		this.drCode.modifyValue(drCode);
	}

	/**
	 * ����ʱ��
	 * 
	 * @return the orderDate Timestamp
	 */
	public Timestamp getOrderDate() {
		return orderDate.getValue();
	}

	/**
	 * ����ʱ��
	 * 
	 * @param orderDate Timestamp
	 */
	public void setOrderDate(Timestamp orderDate) {
		this.orderDate.setValue(orderDate);
	}

	/**
	 * ����ʱ��
	 * 
	 * @param orderDate Timestamp
	 */
	public void modifyOrderDate(Timestamp orderDate) {
		this.orderDate.modifyValue(orderDate);
	}

	/**
	 * �ṹ���������濨���(MR_FILE_INDEX.FILE_NO)
	 * 
	 * @return the fileNO String
	 */
	public String getFileNO() {
		return fileNO.getValue();
	}

	/**
	 * �ṹ���������濨���(MR_FILE_INDEX.FILE_NO)
	 * 
	 * @param fileNO
	 */
	public void setFileNO(String fileNO) {
		this.fileNO.setValue(fileNO);
	}

	/**
	 * �ṹ���������濨���(MR_FILE_INDEX.FILE_NO)
	 * 
	 * @param fileNO
	 */
	public void modifyFileNO(String fileNO) {
		this.fileNO.modifyValue(fileNO);
	}

	/**
	 * �����ˆT
	 * 
	 * @return the optUser String
	 */
	public String getOptUser() {
		return optUser.getValue();
	}

	/**
	 * �����ˆT
	 * 
	 * @param optUser
	 */
	public void setOptUser(String optUser) {
		this.optUser.setValue(optUser);
	}

	/**
	 * �����ˆT
	 * 
	 * @param optUser
	 */
	public void modifyOptUser(String optUser) {
		this.optUser.modifyValue(optUser);
	}

	/**
	 * ��������
	 * 
	 * @return the optDate String
	 */
	public Timestamp getOptDate() {
		return optDate.getValue();
	}

	/**
	 * ��������
	 * 
	 * @param optDate
	 */
	public void setOptDate(Timestamp optDate) {
		this.optDate.setValue(optDate);
	}

	/**
	 * ��������
	 * 
	 * @param optDate
	 */
	public void modifyOptDate(Timestamp optDate) {
		this.optDate.modifyValue(optDate);
	}

	/**
	 * ������ĩ
	 * 
	 * @return the optTerm String
	 */
	public String getOptTerm() {
		return optTerm.getValue();
	}

	/**
	 * ������ĩ
	 * 
	 * @param optTerm
	 */
	public void setOptTerm(String optTerm) {
		this.optTerm.setValue(optTerm);
	}

	/**
	 * ������ĩ
	 * 
	 * @param optTerm
	 */
	public void modifyOptTerm(String optTerm) {
		this.optTerm.modifyValue(optTerm);
	}
	/**
	 * �õ�parm
	 */
	public TParm getParm() {
		TParm result = super.getParm();
		if (getPat() != null)
			result.setData("MR_NO", pat.getMrNo());
		return result;
	}
}
