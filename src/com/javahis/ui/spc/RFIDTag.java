package com.javahis.ui.spc;

import java.io.Serializable;

import com.javahis.device.Uitltool;
/**
 * <p>
 * Title:RFID
 * </p>
 * 
 * <p>
 * Description:RFID
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author fuwj 2012.10.23
 * @version 1.0
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
