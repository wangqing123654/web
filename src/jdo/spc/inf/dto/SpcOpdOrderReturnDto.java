package jdo.spc.inf.dto;

import java.io.Serializable;


/**
 * ������-��ҩDTO
 * @author liyanhui
 *
 */
public class SpcOpdOrderReturnDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String mrNo ;
	
	private String caseNo ;
	
	private String rxNo ;
	
	private String seqNO ;
	/*
	 * ���ʱ��
	 */
	private String phaCheckDate;
	
	/*
	 * ���ҩʦ
	 */
	private String phaCheckCode;
	
	/*
	 * ��ҩҩʦ
	 */
	private String phaDosageCode;
	
	/*
	 * ��ҩʱ��
	 */
	private String phaDosageDate;
	
	/*
	 * ��ҩʱ��
	 */
	private String phaDispenseDate;
	
	/*
	 * ��ҩҩʦ
	 */
	private String phaDispenseCode;
	
	/**
	 * ��ҩҩʦ
	 */
	private String phaRetnCode;
	
	/**
	 * ��ҩʱ��
	 */
	private String phaRetnDate;
	
	public SpcOpdOrderReturnDto (){
		
	}
	
	public SpcOpdOrderReturnDto(String checkDate,String checkCode,String dosageDate ,String dosageCode,
			String dispenseDate, String dispenseCode,String phaRetnDate,String phaRetnCode){
		this.phaCheckDate = checkDate;
		this.phaCheckCode = checkCode;
		this.phaDosageDate = dosageDate;
		this.phaDosageCode = dosageCode;
		this.phaDispenseDate = dispenseDate;
		this.phaDispenseCode = dispenseCode;
		this.phaRetnDate = phaRetnDate;
		this.phaRetnCode = phaRetnCode;
	}
	
	public String getPhaCheckDate() {
		return phaCheckDate;
	}
	public void setPhaCheckDate(String phaCheckDate) {
		this.phaCheckDate = phaCheckDate;
	}
	public String getPhaCheckCode() {
		return phaCheckCode;
	}
	public void setPhaCheckCode(String phaCheckCode) {
		this.phaCheckCode = phaCheckCode;
	}
	public String getPhaDosageCode() {
		return phaDosageCode;
	}
	public void setPhaDosageCode(String phaDosageCode) {
		this.phaDosageCode = phaDosageCode;
	}
	public String getPhaDosageDate() {
		return phaDosageDate;
	}
	public void setPhaDosageDate(String phaDosageDate) {
		this.phaDosageDate = phaDosageDate;
	}
	public String getPhaDispenseDate() {
		return phaDispenseDate;
	}
	public void setPhaDispenseDate(String phaDispenseDate) {
		this.phaDispenseDate = phaDispenseDate;
	}
	public String getPhaDispenseCode() {
		return phaDispenseCode;
	}
	public void setPhaDispenseCode(String phaDispenseCode) {
		this.phaDispenseCode = phaDispenseCode;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getMrNo() {
		return mrNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getRxNo() {
		return rxNo;
	}

	public void setRxNo(String rxNo) {
		this.rxNo = rxNo;
	}

	public String getSeqNO() {
		return seqNO;
	}

	public void setSeqNO(String seqNO) {
		this.seqNO = seqNO;
	}

	public String getPhaRetnCode() {
		return phaRetnCode;
	}

	public void setPhaRetnCode(String phaRetnCode) {
		this.phaRetnCode = phaRetnCode;
	}

	public String getPhaRetnDate() {
		return phaRetnDate;
	}

	public void setPhaRetnDate(String phaRetnDate) {
		this.phaRetnDate = phaRetnDate;
	}
}
