package com.javahis.device.card;


/**
 *
 * @author whaosoft
 *
 */
public class CardDTO {

	//
	private String seq;
	private String mrno;
	private String ctype;
	private String balance;

	/**
	 *
	 * @param seq ÐòºÅ
	 * @param mrno ²¡°¸ºÅ
	 * @param ctype ¿¨Àà±ð
	 * @param balance Óà¶î
	 */
	public CardDTO(String seq, String mrno, String ctype, String balance) {

		this.seq = seq;
		this.mrno = mrno;
		this.ctype = ctype;
		this.balance = balance;
	}

	//
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getMrno() {
		return mrno;
	}
	public void setMrNo(String mrno) {
		this.mrno = mrno;
	}
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}


}
