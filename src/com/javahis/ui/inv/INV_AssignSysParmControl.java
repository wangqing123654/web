package com.javahis.ui.inv;


import java.util.Date;

import jdo.inv.INVSQL;
import jdo.inv.INV_AssignSysParmTool;
import jdo.inv.InvRequestDTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.util.StringTool;
  
 
/**
 * <p>Title: ���ʲ��������趨</p>----------------------INV_SYSPARM,inv_org?
 *
 * <p>Description: </p>
 * 
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: bluecore</p>  
 *
 * @author  fux   
 * @version 1.0
 */               
public class INV_AssignSysParmControl extends TControl {
    
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit() ;
		this.onQuery() ;
	}
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		String sql = "SELECT *  FROM  INV_SYSPARM "  ;
		TParm  parm = new TParm(TJDODBTool.getInstance().select(sql)) ;
		String fixadmoutFlg = parm.getValue("FIXEDAMOUNT_FLG", 0) ; 
		String autoFillType = parm.getValue("AUTO_FILL_TYPE", 0) ; 
		if(fixadmoutFlg.equals("0"))
			getRadioButton("FIXEDAMOUNT_0").setSelected(true) ;
		else if(fixadmoutFlg.equals("1"))
			getRadioButton("FIXEDAMOUNT_1").setSelected(true) ;
		else  getRadioButton("FIXEDAMOUNT_2").setSelected(true) ;
		
		if(autoFillType.equals("1"))
			getRadioButton("AUTO_FILL_TYPE_0").setSelected(true) ;
		else  getRadioButton("AUTO_FILL_TYPE_1").setSelected(true) ;
	}
	/**
	 * ����
	 */
	public void onSave(){  
		String fixadmoutFlg = ""; 
		String autoFillType = ""; 
		//���ڰ�ȫ����
		if(getRadioButton("FIXEDAMOUNT_1").isSelected()){
				  fixadmoutFlg = "1";
		}
		//������Ϳ����
		if(getRadioButton("FIXEDAMOUNT_2").isSelected()){ 
			      fixadmoutFlg = "2";
	    }
		//���ڲ���
		if(getRadioButton("FIXEDAMOUNT_0").isSelected()){
			      fixadmoutFlg = "0";
	    }
		//����(���ò�����)���� ?
		if(getRadioButton("AUTO_FILL_TYPE_0").isSelected()){
				  autoFillType = "1";
		} 
		//������ߴ���  ? 
		if(getRadioButton("AUTO_FILL_TYPE_1").isSelected()){
				  autoFillType = "2";
		} 
		TParm parm = new TParm(); 
		parm.setData("FIXEDAMOUNT_FLG", fixadmoutFlg);  
		parm.setData("AUTO_FILL_TYPE", autoFillType);  
		parm.setData("OPT_USER", Operator.getName());  
		parm.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
		parm.setData("OPT_TERM", Operator.getIP());
		TParm  sysParm = TIOM_AppServer.executeAction(
                "action.inv.INVBaseAction", "onSysParmUpdate", parm);
        if (sysParm == null || sysParm.getErrCode() < 0) {
            this.messageBox("E0001");
            return;   
        }         
		this.messageBox("P0001");  
		}
  
	/**
     * �õ�TCheckBox����
     * @param tagName String
     * @return TCheckBox
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }
}
