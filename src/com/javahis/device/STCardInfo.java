package com.javahis.device;
/**
 * �����ͼ����ں���Ϣ
 * @author lixiang
 * 
 */
public class STCardInfo {
	 byte cardType;//0x0a:CPU��;0x1A:M1����0xDF:Desfire����������
	 byte uidLen[]=new byte[4];
	 byte uid[]=new byte[64];
	 byte atsLen[]=new byte[4];
	 byte ats[]=new byte[64];
	 
	public byte getCardType() {
		return cardType;
	}
	public void setCardType(byte cardType) {
		this.cardType = cardType;
	}

	public byte[] getUid() {
		return uid;
	}
	public void setUid(byte[] uid) {
		this.uid = uid;
	}

	public byte[] getAts() {
		return ats;
	}
	public void setAts(byte[] ats) {
		this.ats = ats;
	}
	public byte[] getUidLen() {
		return uidLen;
	}
	public void setUidLen(byte[] uidLen) {
		this.uidLen = uidLen;
	}
	public byte[] getAtsLen() {
		return atsLen;
	}
	public void setAtsLen(byte[] atsLen) {
		this.atsLen = atsLen;
	}
	
	
	
	
	
	
}
