package com.javahis.device;
/**
 * ҽ����Ϣ
 * @author lixiang
 *
 */
public class NJYY {
	//:��������
	byte sickName[]=new byte[20];
	//���Ӳ�����
	byte erecordNo[]=new byte[30];
	//ʶ���
	byte flag[]=new byte[3];
	//ҽ����
	byte hspNo[]=new byte[9];
	//����λ
	byte rFu[]=new byte[2];
	
	public byte[] getSickName() {
		return sickName;
	}
	public void setSickName(byte[] sickName) {
		this.sickName = sickName;
	}
	public byte[] getErecordNo() {
		return erecordNo;
	}
	public void setErecordNo(byte[] erecordNo) {
		this.erecordNo = erecordNo;
	}
	public byte[] getFlag() {
		return flag;
	}
	public void setFlag(byte[] flag) {
		this.flag = flag;
	}
	public byte[] getHspNo() {
		return hspNo;
	}
	public void setHspNo(byte[] hspNo) {
		this.hspNo = hspNo;
	}
	public byte[] getrFu() {
		return rFu;
	}
	public void setrFu(byte[] rFu) {
		this.rFu = rFu;
	}
	
	
	
	
}
