package jdo.sys;

import com.dongyang.data.StringValue;
import com.dongyang.data.TModifiedData;
import com.dongyang.data.TParm;

/**
 * 
 * <p>
 * Title: ����jdo
 * </p>
 * 
 * <p>
 * Description:����jdo
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
public class JDODept extends TModifiedData  {
	/**
	 * �û�ID
	 */
	private StringValue userId=new StringValue(this);
	/**
	 * ���Ŵ���
	 */
	private StringValue deptCode=new StringValue(this);
	/**
	 * ������ע��
	 */
	private StringValue mainFlg=new StringValue(this);
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
	 * �õ����Ŵ���
	 * @return deptCode String
	 */
	public String getDeptCode() {
		return deptCode.getValue();
	}
	/**
	 * ���ò��Ŵ���
	 * @parm deptCode String
	 */
	public void setDeptCode(String deptCode) {
		this.deptCode.setValue(deptCode);
	}
	/**
	 * �޸Ĳ��Ŵ���
	 * @parm deptCode String
	 */
	public void modifyDeptCode(String deptCode) {
		this.deptCode.modifyValue(deptCode);
	}
	/**
	 * �õ�������ע��
	 * @return mainFlg String
	 */
	public String getMainFlg() {
		return mainFlg.getValue();
	}
	/**
	 * ����������ע��
	 * @parm mainFlg String
	 */
	public void setMainFlg(String mainFlg) {
		this.mainFlg.setValue(mainFlg);
	}
	/**
	 * �޸�������ע��
	 * @parm mainFlg String
	 */
	public void modifyMainFlg(String mainFlg) {
		this.mainFlg.modifyValue(mainFlg);
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
