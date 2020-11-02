package com.javahis.ui.inv;

import java.io.Serializable;

import com.javahis.device.Uitltool;
/**
 *   
 * @author lixiang
 *
 */
public class RFIDTag implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 419882076939852969L;
	/**
	 * epc��ֵ
	 */
	private String epc;
	/**
	 * rfidֵ
	 */
	private String readerid;
	/**
	 * ɨ�赽������
	 */
	private String antenna;
	/**
	 * ����  + �룬  -Ϊ��
	 */
	private String direction;
	/**
	 *  ����ʱ��
	 */
	private String time;
	/**
	 * 
	 * @return
	 */
	public String getEpc() {
		return Uitltool.decode(epc);
	}

	public void setEpc(String epc) {
		this.epc = epc;
	}

	public String getReaderid() {
		return readerid;
	}

	public void setReaderid(String readerid) {
		this.readerid = readerid;
	}

	public String getAntenna() {
		return antenna;
	}

	public void setAntenna(String antenna) {
		this.antenna = antenna;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	
}
