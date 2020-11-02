package jdo.sys;

import java.sql.Timestamp;

import com.dongyang.data.StringValue;
import com.dongyang.data.TModifiedData;
import com.dongyang.data.TParm;
import com.dongyang.data.TimestampValue;

/**
 * 
 * <p>
 * Title: �û�JDO
 * </p>
 * 
 * <p>
 * Description:�û�JDO
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
public class JDOOperator extends TModifiedData {
	private JDODeptList deptlist;
	/**
	 * �û�����
	 */
	private StringValue userId=new StringValue(this);
	/**
	 * �û�����
	 */
	private StringValue userName=new StringValue(this);
	/**
	 * ƴ��1
	 */
	private StringValue py1=new StringValue(this);
	/**
	 * ƴ��2
	 */
	private StringValue py2=new StringValue(this);
	/*
	 * ������ʾ��
	 */
	private StringValue deptCode=new StringValue(this);
	/**
	 * ���֤
	 */
	private StringValue idCode=new StringValue(this);
	/**
	 * �Ա����
	 */
	private StringValue sexCode=new StringValue(this);
	/**
	 * ����
	 */
	private StringValue userPassword=new StringValue(this);
	/**
	 * ְ�����
	 */
	private StringValue posCode=new StringValue(this);
	/**
	 * ��ɫ����
	 */
	private StringValue roleId=new StringValue(this);
	/**
	 * ��Ч����
	 */
	private TimestampValue activeDate=new TimestampValue(this);
	/**
	 * ʧЧ����
	 */
	private TimestampValue endDate=new TimestampValue(this);
	/**
	 * Ĭ�Ͻ������
	 */
	private StringValue pubFunction=new StringValue(this);
	/**
	 * EMAIL
	 */
	private StringValue email=new StringValue(this);
	/**
	 * ֤�պ�
	 */
	private StringValue lcsNo=new StringValue(this);
	/**
	 * ֤����Ч����
	 */
	private TimestampValue effLcsDate=new TimestampValue(this);
	/**
	 * ֤��ʧЧ����
	 */
	private TimestampValue endLcsDate=new TimestampValue(this);
	/**
	 * ȫְע��
	 */
	private StringValue fullTimeFlg=new StringValue(this);
	/**
	 * ����ҩƷȨ��
	 */
	private StringValue ctrlFlg=new StringValue(this);
	/**
	 * �������
	 */
	private StringValue regionCode=new StringValue(this);
	/**
	 * ����������
	 */
	private TimestampValue rcntLoginDate=new TimestampValue(this);
	/**
	 * ���ǳ�����
	 */
	private TimestampValue rcntLogoutDate=new TimestampValue(this);
	/**
	 * ���ǳ�IP
	 */
	private StringValue rcntIp=new StringValue(this);
	/**
	 * ������Ա
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
	 * �����û�����
	 * @parm userId String
	 */
	public void modifyUserId(String userId) {
		this.userId.modifyValue(userId);
	}
	/**
	 * �õ��û�����
	 * @return userName String
	 */
	public String getUserName() {
		return userName.getValue();
	}
	/**
	 * �����û�����
	 * @parm userName String
	 */
	public void setUserName(String userName) {
		this.userName.setValue(userName);
	}
	/**
	 * �޸��û�����
	 * @parm userName String
	 */
	public void modifyUserName(String userName) {
		this.userName.modifyValue(userName);
	}
	/**
	 * �õ�ƴ��1
	 * @return py1 String
	 */
	public String getPy1() {
		return py1.getValue();
	}
	/**
	 * ����ƴ��1
	 * @parm py1 String
	 */
	public void setPy1(String py1) {
		this.py1.setValue(py1);
	}
	/**
	 * �޸�ƴ��1
	 * @parm py1 String
	 */
	public void modifyPy1(String py1) {
		this.py1.modifyValue(py1);
	}
	/**
	 * �õ�ƴ��2
	 * @return py2 String
	 */
	public String getPy2() {
		return py2.getValue();
	}
	/**
	 * ����ƴ��2
	 * @parm py2 String
	 */
	public void setPy2(String py2) {
		this.py2.setValue(py2);
	}
	/**
	 * �޸�ƴ��2
	 * @parm py2 String
	 */
	public void modifyPy2(String py2) {
		this.py2.modifyValue(py2);
	}
	/**
	 * �õ����֤
	 * @return idCode String
	 */
	public String getIdCode() {
		return idCode.getValue();
	}
	/**
	 * �������֤
	 * @parm idCode String
	 */
	public void setIdCode(String idCode) {
		this.idCode.setValue(idCode);
	}
	/**
	 * �޸����֤
	 * @parm idCode String
	 */
	public void modifyIdCode(String idCode) {
		this.idCode.modifyValue(idCode);
	}
	/**
	 * �õ��Ա����
	 * @return sexCode String
	 */
	public String getSexCode() {
		return sexCode.getValue();
	}
	/**
	 * �����Ա����
	 * @parm sexCode String
	 */
	public void setSexCode(String sexCode) {
		this.sexCode.setValue(sexCode);
	}
	/**
	 * �޸��Ա����
	 * @parm sexCode String
	 */
	public void modifySexCode(String sexCode) {
		this.sexCode.modifyValue(sexCode);
	}
	/**
	 * �õ�����
	 * @return userPassword String
	 */
	public String getUserPassword() {
		return userPassword.getValue();
	}
	/**
	 * ��������
	 * @parm userPassword String
	 */
	public void setUserPassword(String userPassword) {
		this.userPassword.setValue(userPassword);
	}
	/**
	 * �޸�����
	 * @parm userPassword String
	 */
	public void modifyUserPassword(String userPassword) {
		this.userPassword.modifyValue(userPassword);
	}
	/**
	 * �õ�ְ�����
	 * @return posCode String
	 */
	public String getPosCode() {
		return posCode.getValue();
	}
	/**
	 * ����ְ�����
	 * @parm posCode String
	 */
	public void setPosCode(String posCode) {
		this.posCode.setValue(posCode);
	}
	/**
	 * �޸�ְ�����
	 * @parm posCode String
	 */
	public void modifyPosCode(String posCode) {
		this.posCode.modifyValue(posCode);
	}
	/**
	 * �õ���ɫ����
	 * @return roleId String
	 */
	public String getRoleId() {
		return roleId.getValue();
	}
	/**
	 * ���ý�ɫ����
	 * @parm roleId String
	 */
	public void setRoleId(String roleId) {
		this.roleId.setValue(roleId);
	}
	/**
	 * �޸Ľ�ɫ����
	 * @parm roleId String
	 */
	public void modifyRoleId(String roleId) {
		this.roleId.modifyValue(roleId);
	}
	/**
	 * �õ���Ч����
	 * @return activeDate Timestamp
	 */
	public Timestamp getActiveDate() {
		return activeDate.getValue();
	}
	/**
	 * ������Ч����
	 * @parm activeDate Timestamp
	 */
	public void setActiveDate(Timestamp activeDate) {
		this.activeDate.setValue(activeDate);
	}
	/**
	 * �޸���Ч����
	 * @parm activeDate Timestamp
	 */
	public void modifyActiveDate(Timestamp activeDate) {
		this.activeDate.modifyValue(activeDate);
	}
	/**
	 * �õ�ʧЧ����
	 * @return endDate Timestamp
	 */
	public Timestamp getEndDate() {
		return endDate.getValue();
	}
	/**
	 * ����ʧЧ����
	 * @parm endDate Timestamp
	 */
	public void setEndDate(Timestamp endDate) {
		this.endDate.setValue(endDate);
	}
	/**
	 * �޸�ʧЧ����
	 * @parm endDate Timestamp
	 */
	public void modifyEndDate(Timestamp endDate) {
		this.endDate.modifyValue(endDate);
	}
	/**
	 * �õ�Ĭ�Ͻ������
	 * @return pubFunction String
	 */
	public String getPubFunction() {
		return pubFunction.getValue();
	}
	/**
	 * ����Ĭ�Ͻ������
	 * @parm pubFunction String
	 */
	public void setPubFunction(String pubFunction) {
		this.pubFunction.setValue(pubFunction);
	}
	/**
	 * �޸�Ĭ�Ͻ������
	 * @parm pubFunction String
	 */
	public void modifyPubFunction(String pubFunction) {
		this.pubFunction.modifyValue(pubFunction);
	}
	/**
	 * �õ�EMAIL
	 * @return email String
	 */
	public String getEmail() {
		return email.getValue();
	}
	/**
	 * ����EMAIL
	 * @parm email String
	 */
	public void setEmail(String email) {
		this.email.setValue(email);
	}
	/**
	 * �޸�EMAIL
	 * @parm email String
	 */
	public void modifyEmail(String email) {
		this.email.modifyValue(email);
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
	 * �õ�֤����Ч����
	 * @return effLcsDate Timestamp
	 */
	public Timestamp getEffLcsDate() {
		return effLcsDate.getValue();
	}
	/**
	 * ����֤����Ч����
	 * @parm effLcsDate Timestamp
	 */
	public void setEffLcsDate(Timestamp effLcsDate) {
		this.effLcsDate.setValue(effLcsDate);
	}
	/**
	 * �޸�֤����Ч����
	 * @parm effLcsDate Timestamp
	 */
	public void modifyEffLcsDate(Timestamp effLcsDate) {
		this.effLcsDate.modifyValue(effLcsDate);
	}
	/**
	 * �õ�֤��ʧЧ����
	 * @return endLcsDate Timestamp
	 */
	public Timestamp getEndLcsDate() {
		return endLcsDate.getValue();
	}
	/**
	 * ����֤��ʧЧ����
	 * @parm endLcsDate Timestamp
	 */
	public void setEndLcsDate(Timestamp endLcsDate) {
		this.endLcsDate.setValue(endLcsDate);
	}
	/**
	 * �޸�֤��ʧЧ����
	 * @parm endLcsDate Timestamp
	 */
	public void modifyEndLcsDate(Timestamp endLcsDate) {
		this.endLcsDate.modifyValue(endLcsDate);
	}
	/**
	 * �õ�ȫְע��
	 * @return fullTimeFlg String
	 */
	public String getFullTimeFlg() {
		return fullTimeFlg.getValue();
	}
	/**
	 * ����ȫְע��
	 * @parm fullTimeFlg String
	 */
	public void setFullTimeFlg(String fullTimeFlg) {
		this.fullTimeFlg.setValue(fullTimeFlg);
	}
	/**
	 * �޸�ȫְע��
	 * @parm fullTimeFlg String
	 */
	public void modifyFullTimeFlg(String fullTimeFlg) {
		this.fullTimeFlg.modifyValue(fullTimeFlg);
	}
	/**
	 * �õ�����ҩƷȨ��
	 * @return ctrlFlg String
	 */
	public String getCtrlFlg() {
		return ctrlFlg.getValue();
	}
	/**
	 * ���ÿ���ҩƷȨ��
	 * @parm ctrlFlg String
	 */
	public void setCtrlFlg(String ctrlFlg) {
		this.ctrlFlg.setValue(ctrlFlg);
	}
	/**
	 * �޸Ŀ���ҩƷȨ��
	 * @parm ctrlFlg String
	 */
	public void modifyCtrlFlg(String ctrlFlg) {
		this.ctrlFlg.modifyValue(ctrlFlg);
	}
	/**
	 * �õ��������
	 * @return regionCode String
	 */
	public String getRegionCode() {
		return regionCode.getValue();
	}
	/**
	 * �����������
	 * @parm regionCode String
	 */
	public void setRegionCode(String regionCode) {
		this.regionCode.setValue(regionCode);
	}
	/**
	 * �޸��������
	 * @parm regionCode String
	 */
	public void modifyRegionCode(String regionCode) {
		this.regionCode.modifyValue(regionCode);
	}
	/**
	 * �õ�����������
	 * @return rcntLoginDate Timestamp
	 */
	public Timestamp getRcntLoginDate() {
		return rcntLoginDate.getValue();
	}
	/**
	 * ��������������
	 * @parm rcntLoginDate Timestamp
	 */
	public void setRcntLoginDate(Timestamp rcntLoginDate) {
		this.rcntLoginDate.setValue(rcntLoginDate);
	}
	/**
	 * �޸�����������
	 * @parm rcntLoginDate Timestamp
	 */
	public void modifyRcntLoginDate(Timestamp rcntLoginDate) {
		this.rcntLoginDate.modifyValue(rcntLoginDate);
	}
	/**
	 * �õ����ǳ�����
	 * @return rcntLogoutDate Timestamp
	 */
	public Timestamp getRcntLogoutDate() {
		return rcntLogoutDate.getValue();
	}
	/**
	 * �������ǳ�����
	 * @parm rcntLogoutDate Timestamp
	 */
	public void setRcntLogoutDate(Timestamp rcntLogoutDate) {
		this.rcntLogoutDate.setValue(rcntLogoutDate);
	}
	/**
	 * �޸����ǳ�����
	 * @parm rcntLogoutDate Timestamp
	 */
	public void modifyRcntLogoutDate(Timestamp rcntLogoutDate) {
		this.rcntLogoutDate.modifyValue(rcntLogoutDate);
	}
	/**
	 * �õ�����½IP
	 * @return rcntIp String
	 */
	public String getRcntIp() {
		return rcntIp.getValue();
	}
	/**
	 * ��������½IP
	 * @parm rcntIp String
	 */
	public void setRcntIp(String rcntIp) {
		this.rcntIp.setValue(rcntIp);
	}
	/**
	 * �޸�����½IP
	 * @parm rcntIp String
	 */
	public void modifyRcntIp(String rcntIp) {
		this.rcntIp.modifyValue(rcntIp);
	}
	/**
	 * �õ�������Ա
	 * @return optUser String
	 */
	public String getOptUser() {
		return optUser.getValue();
	}
	/**
	 * ���ò�����Ա
	 * @parm optUser String
	 */
	public void setOptUser(String optUser) {
		this.optUser.setValue(optUser);
	}
	/**
	 * �޸Ĳ�����Ա
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
	 * �õ����ţ���ʾ�ã�
	 * @return deptCode String
	 */
	public String getDeptCode(){
		return this.deptCode.getValue();
	}
	/**
	 * ���ò���
	 * @param deptCode String
	 */
	public void setDeptCode(String deptCode){
		this.deptCode.setValue(deptCode);
	}
	public JDODeptList getJDODeptList(){
		return this.deptlist;
	}
	public void setJDODeptList(JDODeptList deptlist){
		this.deptlist=deptlist;
	}
	/**
	 * �õ�parm
	 */
	public TParm getParm() {
		TParm result = super.getParm();
		return result;
	}
}
