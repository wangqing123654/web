package com.javahis.ui.odi;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;

/**
 * 住院医生站-开药使用原因说明
 * @author duzhw
 *
 */
public class ODIOrderUseDescControl extends TControl {
	//页面控件
	private TComboBox pasdrCombo;
	private TTextField pasdrNote;
	
	//参数
	private String caseNo = "";
	private String mrNo = "";
	private String orderNo = "";
	private String orderSeq = "";
	private String orderCode = "";
	
	private String oper_flg = "update";
	
	TParm acceptData = new TParm(); // 接参
	
	public void onInit() {
		super.onInit();
		initComponent();
		//Object obj = this.getParameter();

        acceptData = (TParm)this.getParameter();
        //System.out.println("新页面数据acceptData="+acceptData);
        caseNo = acceptData.getValue("CASE_NO");
        mrNo = acceptData.getValue("MR_NO");
        orderCode = acceptData.getValue("ORDER_CODE");
        orderNo = acceptData.getValue("ORDER_NO");
        orderSeq = acceptData.getValue("ORDER_SEQ");
		String pasdrCode = acceptData.getValue("PASDR_CODE");
        String pasdrNote = acceptData.getValue("PASDR_NOTE");
        if("999999999999".equals(orderNo)){//新增
        	oper_flg = "add";
        }
        String sql = "select pasdr_code,pasdr_note from odi_order " +
        		" where mr_no = '"+mrNo+"' and case_no = '"+caseNo+"' and order_code  = '"+orderCode+"' " +
        		" and order_no = '"+orderNo+"' and order_seq = '"+orderSeq+"'";
        //System.out.println("sql="+sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()>0){
            this.setValue("PASDR_CODE", parm.getValue("PASDR_CODE", 0));
            this.setValue("PASDR_NOTE", parm.getValue("PASDR_NOTE", 0));
        }else{
            this.setValue("PASDR_CODE", pasdrCode);
            this.setValue("PASDR_NOTE", pasdrNote);
        }
        
        
	}
	//保存确定按钮
	public void confirm(){
		String pasdrCode=this.pasdrCombo.getValue();
		String pasdrNote=this.pasdrNote.getValue();
		TParm result = new TParm();
		String sql= "update odi_order set pasdr_code = '"+pasdrCode+"' ,pasdr_note = '"+pasdrNote+"' " +
				"where case_no='"+caseNo+"' and mr_no='"+mrNo+"' and order_code='"+orderCode+"'";
		
		if("update".equals(oper_flg)){//修改
			result = new TParm(TJDODBTool.getInstance().update(sql));
			result.setData("PASDR_CODE", pasdrCode);
			result.setData("PASDR_NOTE", pasdrNote);
			result.setData("OPER", "update");
			this.setReturnValue(result);
			if(result.getErrCode()<0){
	            this.messageBox("E0005"); //失败
			}else{
	            this.messageBox("P0005"); //成功
	            onInit();
	            this.closeWindow();
	       }
		}else if("add".equals(oper_flg)){//新增
			result.setData("PASDR_CODE", pasdrCode);
			result.setData("PASDR_NOTE", pasdrNote);
			result.setData("OPER", "add");
			this.setReturnValue(result);
	        this.closeWindow();
		}
		
		
	}
	//取消按钮
	public void onCan(){
		TParm result = new TParm();
		result.setData("OPER", "");
		this.setReturnValue(result);
        this.closeWindow();
    }
	
	/**
    *
    * 初始化页面控件便于程序调用
    */
   private void initComponent(){
	   pasdrCombo=(TComboBox)this.getComponent("PASDR_CODE");
	   this.pasdrNote=(TTextField)this.getComponent("PASDR_NOTE");
   }

}
