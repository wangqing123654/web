package com.javahis.bsm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: ����ֵ�Ķ���</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.05.14 
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD)  
@XmlRootElement(name = "ROOT")
public class ReturnBean {
	private  String  RETVAL ;
	private  String  RETMSG;     //������Ϣ
	private  int  RETCODE ;   //0����ʧ�ܣ�1����ɹ�
	public String getRETVAL() {
		return RETVAL;
	}
	public void setRETVAL(String rETVAL) {
		RETVAL = rETVAL;
	}
	/**
	 * ������Ϣ
	 * @return
	 */
	public String getRETMSG() {
		return RETMSG;
	}
	/**
	 * ������Ϣ
	 * @param rETMSG
	 */
	public void setRETMSG(String rETMSG) {
		RETMSG = rETMSG;
	}
	/**
	 * 0����ʧ�ܣ�1����ɹ�
	 * @return
	 */
	public int getRETCODE() {
		return RETCODE;
	}
	/**
	 * 0����ʧ�ܣ�1����ɹ�
	 * @param rETCODE
	 */
	public void setRETCODE(int rETCODE) {
		RETCODE = rETCODE;
	}
	

}
