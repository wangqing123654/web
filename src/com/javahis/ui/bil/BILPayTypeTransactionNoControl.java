package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TDialog;
/**
 * <p>Title: ���סԺ��Ʊ����֧����ʽУ��</p>
 *
 * <p>Description: ΢�š�֧����֧����ʽ������ӽ��׺�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author pangben
 * @version 1.0
 */
public class BILPayTypeTransactionNoControl extends TControl{
	 TParm parm=null;
	 public void onInit() {
	     super.onInit();
	     // ȥ���˵���
	     //TDialog F = (TDialog) this.getComponent("UI");
	     //F.setUndecorated(true);
	     onPage();
	 }
	 private void onPage(){
		 parm = (TParm)this.getParameter();
		 if(null!=parm.getValue("WX_FLG") && parm.getValue("WX_FLG").equals("Y")){
			 callFunction("UI|WX_LBL|setVisible", true);
			 callFunction("UI|WX_TEXT|setVisible", true);
			 callFunction("UI|WX_AMT|setVisible", true);
			 this.setValue("WX_AMT", parm.getDouble("WX_AMT")+"Ԫ");
		 }else{
			 callFunction("UI|WX_LBL|setVisible", false);
			 callFunction("UI|WX_TEXT|setVisible", false);
			 callFunction("UI|WX_AMT|setVisible", false);
		 }
		 if(null!=parm.getValue("ZFB_FLG") && parm.getValue("ZFB_FLG").equals("Y")){
			 callFunction("UI|ZFB_LBL|setVisible", true);
			 callFunction("UI|ZFB_TEXT|setVisible", true);
			 callFunction("UI|ZFB_AMT|setVisible", true);
			 this.setValue("ZFB_AMT", parm.getDouble("ZFB_AMT")+"Ԫ");
		 }else{
			 callFunction("UI|ZFB_LBL|setVisible", false);
			 callFunction("UI|ZFB_TEXT|setVisible", false);
			 callFunction("UI|ZFB_AMT|setVisible", false);
		 }
	 }
	 public void onSave(){
		 TParm result=new TParm();
		 if(null!=parm.getValue("WX_FLG") && parm.getValue("WX_FLG").equals("Y")){
			 if(this.getValueString("WX_TEXT").length()<=0){
				 this.messageBox("΢�Ž��׺Ų�����Ϊ��");
				 return;
			 }
			 result.setData("WX",this.getValueString("WX_TEXT"));
		 }
		 if(null!=parm.getValue("ZFB_FLG") && parm.getValue("ZFB_FLG").equals("Y")){
			 if(this.getValueString("ZFB_TEXT").length()<=0){
				 this.messageBox("֧�������׺Ų�����Ϊ��");
				 return;
			 }
			 result.setData("ZFB",this.getValueString("ZFB_TEXT"));
		 }
		 setReturnValue(result);
		 this.closeWindow();
	 }
}
