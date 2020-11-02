package jdo.spc;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class SpcCommonDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mrNo;
	private String rxNo ;
	private String caseNo;
	
	
	//SEQ_NO
	private int seqNo;
	
	//ҩʦ��ҩ��Ա
	private String phaDispenseCode ;
	
	//ҩʦ��ҩʱ��
	private Timestamp  phaDispenseDate ;
	
	//EXEC_FLG
	private String execFlg ;
	
	
	public SpcCommonDto (){
		
	}
	
	public String getMrNo() {
		return mrNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	public String getRxNo() {
		return rxNo;
	}

	public void setRxNo(String rxNo) {
		this.rxNo = rxNo;
	}

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	
	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public String getPhaDispenseCode() {
		return phaDispenseCode;
	}

	public void setPhaDispenseCode(String phaDispenseCode) {
		this.phaDispenseCode = phaDispenseCode;
	}

	@XmlJavaTypeAdapter(TimestampAdapter.class)
	public Timestamp getPhaDispenseDate() {
		return phaDispenseDate;
	}

	public void setPhaDispenseDate(@XmlJavaTypeAdapter(TimestampAdapter.class) Timestamp phaDispenseDate) {
		this.phaDispenseDate = phaDispenseDate;
	}

	public String getExecFlg() {
		return execFlg;
	}

	public void setExecFlg(String execFlg) {
		this.execFlg = execFlg;
	}
	
	
	
	
	
}
