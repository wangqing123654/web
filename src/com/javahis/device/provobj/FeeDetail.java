package com.javahis.device.provobj;
/**
 * ������ϸ
 * @author lixiang
 *
 */
public class FeeDetail {
	float pcRate;   //O����֧������[N(5,4)]
	float pcSelfFee; //O�Ը�����[N(10, 4)] 
	float pcDeduct;  //O�Էѷ���[N(10, 4)]
	public float getPcRate() {
		return pcRate;
	}
	public void setPcRate(float pcRate) {
		this.pcRate = pcRate;
	}
	public float getPcSelfFee() {
		return pcSelfFee;
	}
	public void setPcSelfFee(float pcSelfFee) {
		this.pcSelfFee = pcSelfFee;
	}
	public float getPcDeduct() {
		return pcDeduct;
	}
	public void setPcDeduct(float pcDeduct) {
		this.pcDeduct = pcDeduct;
	}
	
	
}
