package com.javahis.ui.mro;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;



public class MROUploadHeadControl extends TControl {
	private String districtCode;//���������ִ���
	private String organizationCode;//��֯��������
	private String organizationName;//��������
	private String leader;//��λ������
	private String submitter;//���
	private String submitterTel;//��˵绰
	private String mail;//�������
	private String mobile;//�ֻ�
	
	/**
     * ��ʼ������
     */
    public void onInit() {
    	onQuery();    	
    }
    
    public void onQuery(){
    	String querySql = " SELECT * FROM MRO_UPLOADHEAD ";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(querySql));
    	this.setValue("DISTRICT_CODE", parm.getValue("DISTRICT_CODE", 0));
    	this.setValue("ORGANIZATION_CODE", parm.getValue("ORGANIZATION_CODE", 0));
    	this.setValue("ORGANIZATION_NAME", parm.getValue("ORGANIZATION_NAME", 0));
    	this.setValue("LEADER_NAME", parm.getValue("LEADER_NAME", 0));
    	this.setValue("SUBMITTER_NAME", parm.getValue("SUBMITTER_NAME", 0));
    	this.setValue("SUBMITTER_TEL", parm.getValue("SUBMITTER_TEL", 0));
    	this.setValue("MAIL", parm.getValue("MAIL", 0));
    	this.setValue("MOBILE", parm.getValue("MOBILE", 0));
    }
    
    public void onSave(){
    	districtCode = this.getValueString("DISTRICT_CODE");
    	organizationCode = this.getValueString("ORGANIZATION_CODE");
    	organizationName = this.getValueString("ORGANIZATION_NAME");
    	leader = this.getValueString("LEADER_NAME");
    	submitter = this.getValueString("SUBMITTER_NAME");
    	submitterTel = this.getValueString("SUBMITTER_TEL");
    	mail = this.getValueString("MAIL");
    	mobile = this.getValueString("MOBILE");
    	String updateSql = " UPDATE MRO_UPLOADHEAD SET DISTRICT_CODE = '"+districtCode+"',ORGANIZATION_CODE = '"+organizationCode+"'," +
    					   " ORGANIZATION_NAME = '"+organizationName+"',LEADER_NAME = '"+leader+"',SUBMITTER_NAME = '"+submitter+"'," +
    					   " SUBMITTER_TEL = '"+submitterTel+"',MAIL = '"+mail+"',MOBILE = '"+mobile+"' ";
    	TParm parm = new TParm(TJDODBTool.getInstance().update(updateSql));
    	if(parm.getErrCode()<0){
    		this.messageBox("����ʧ�ܣ�");
    	}else{
    		this.messageBox("����ɹ���");
    	}
    }
    public void onClear(){
    	this.setValue("DISTRICT_CODE", "");
    	this.setValue("ORGANIZATION_CODE", "");
    	this.setValue("ORGANIZATION_NAME", "");
    	this.setValue("LEADER_NAME", "");
    	this.setValue("SUBMITTER_NAME", "");
    	this.setValue("SUBMITTER_TEL", "");
    	this.setValue("MAIL", "");
    	this.setValue("MOBILE", "");
    }
	
}
