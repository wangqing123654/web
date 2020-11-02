package jdo.spc;

import java.io.Serializable;

import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;

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

	public SpcOpdOrderReturnDto (){
		
	}
	
	public SpcOpdOrderReturnDto(String checkDate,String checkCode,String dosageDate ,String dosageCode,
			String dispenseDate, String dispenseCode){
		this.phaCheckCode = checkDate;
		this.phaCheckCode = checkCode;
		this.phaDosageDate = dosageDate;
		this.phaDosageCode = dosageCode;
		this.phaDispenseDate = dispenseDate;
		this.phaDispenseCode = dispenseCode;
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
}
