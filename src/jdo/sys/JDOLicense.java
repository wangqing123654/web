package jdo.sys;

import java.sql.Timestamp;

import com.dongyang.data.StringValue;
import com.dongyang.data.TModifiedData;
import com.dongyang.data.TParm;
import com.dongyang.data.TimestampValue;
/**
 * 
 * <p>
 * Title: ֤��jdo
 * </p>
 * 
 * <p>
 * Description:֤��jdo
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author ehui 20081111
 * @version 1.0
 */
public class JDOLicense extends TModifiedData {
	/**
	 * �û�ID
	 */
	private StringValue userId=new StringValue(this);
	/**
	 * ֤�����
	 */
	private StringValue lcsClassCode=new StringValue(this);
	/**
	 * ֤�պ�
	 */
	private StringValue lcsNo=new StringValue(this);
	/**
	 * ��Ч����
	 */
	private TimestampValue effLcsDate=new TimestampValue(this);
	/**
	 * ʧЧ����
	 */
	private TimestampValue endLcsDate=new TimestampValue(this);
	/**
	 * �����û�
	 */
	private StringValue optUser=new StringValue(this);
	/**
	 * ������ĩ
	 */
	private StringValue optTerm=new StringValue(this);
	/**
	 * �õ��û�����
	 * @return userId String
	 */
	public String getUserId() {
		return userId.getValue();
	}
	/**
	 * �����û�����
	 * @parm userId String
	 */
	public void setUserId(String userId) {
		this.userId.setValue(userId);
	}
	/**
	 * �޸��û�����
	 * @parm userId String
	 */
	public void modifyUserId(String userId) {
		this.userId.modifyValue(userId);
	}
	/**
	 * �õ�֤�����
	 * @return lcsClassCode String
	 */
	public String getLcsClassCode() {
		return lcsClassCode.getValue();
	}
	/**
	 * ����֤�����
	 * @parm lcsClassCode String
	 */
	public void setLcsClassCode(String lcsClassCode) {
		this.lcsClassCode.setValue(lcsClassCode);
	}
	/**
	 * �޸�֤�����
	 * @parm deptCode String
	 */
	public void modifyLcsClassCode(String lcsClassCode) {
		this.lcsClassCode.modifyValue(lcsClassCode);
	}
	/**
	 * �õ�֤�պ�
	 * @return lcsNo String
	 */
	public String getLcsNo() {
		return lcsNo.getValue();
	}
	/**
	 * ����֤�պ�
	 * @parm lcsNo String
	 */
	public void setLcsNo(String lcsNo) {
		this.lcsNo.setValue(lcsNo);
	}
	/**
	 * �޸�֤�պ�
	 * @parm lcsNo String
	 */
	public void modifyLcsNo(String lcsNo) {
		this.lcsNo.modifyValue(lcsNo);
	}
	/**
	 * �õ���Ч����
	 * @return endLcsDate Timestamp
	 */
	public Timestamp getEffLcsDate(){
		return this.effLcsDate.getValue();
	}
	/**
	 * ������Ч����
	 * @param effLcsDate Timestamp
	 */
	public void setEffLcsDate(Timestamp effLcsDate){
		this.effLcsDate.setValue(effLcsDate);
	}
	/**
	 * �޸���Ч����
	 * @param effLcsDate Timestamp
	 */
	public void modifyEffLcsDate(Timestamp effLcsDate){
		this.effLcsDate.modifyValue(effLcsDate);
	}
	/**
	 * �õ�ʧЧ����
	 * @return endLcsDate Timestamp
	 */
	public Timestamp getEndLcsDate(){
		return this.endLcsDate.getValue();
	}
	/**
	 * ����ʧЧ����
	 * @param endLcsDate Timestamp
	 */
	public void setEndLcsDate(Timestamp endLcsDate){
		this.endLcsDate.setValue(endLcsDate);
	}
	/**
	 * �޸�ʧЧ����
	 * @param endLcsDate Timestamp
	 */
	public void modifyEndLcsDate(Timestamp endLcsDate){
		this.endLcsDate.modifyValue(endLcsDate);
	}
	/**
	 * �õ������û�
	 * @return optUser String
	 */
	public String getOptUser() {
		return optUser.getValue();
	}
	/**
	 * ���ò����û�
	 * @parm optUser String
	 */
	public void setOptUser(String optUser) {
		this.optUser.setValue(optUser);
	}
	/**
	 * �޸Ĳ����û�
	 * @parm optUser String
	 */
	public void modifyOptUser(String optUser) {
		this.optUser.modifyValue(optUser);
	}
	/**
	 * �õ�������ĩ
	 * @return optTerm String
	 */
	public String getOptTerm() {
		return optTerm.getValue();
	}
	/**
	 * ���ò�����ĩ
	 * @parm optTerm String
	 */
	public void setOptTerm(String optTerm) {
		this.optTerm.setValue(optTerm);
	}
	/**
	 * �޸Ĳ�����ĩ
	 * @parm optTerm String
	 */
	public void modifyOptTerm(String optTerm) {
		this.optTerm.modifyValue(optTerm);
	}
	/**
	 * �õ�parm
	 */
	public TParm getParm() {
		TParm result = super.getParm();
		return result;
	}
}
