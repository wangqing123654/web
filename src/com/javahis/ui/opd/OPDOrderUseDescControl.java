package com.javahis.ui.opd;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;

/**
 * 门诊医生站-开药使用原因说明
 * @author duzhw 20131209
 *
 */
public class OPDOrderUseDescControl extends TControl {
	//页面控件
	private TComboBox pasdrCombo;
	private TTextField pasdrNote;
	
	//参数
	private String caseNo = "";
	private String mrNo = "";
	//private String orderSeq = "";
	private String orderCode = "";
	private String seqNo = "";
	
	private String oper_flg = "update";
	
	TParm acceptData = new TParm(); // 接参
	
	public void onInit() {
		super.onInit();
		initComponent();
		//Object obj = this.getParameter();

        acceptData = (TParm)this.getParameter();
        caseNo = acceptData.getValue("CASE_NO");
        mrNo = acceptData.getValue("MR_NO");
        orderCode = acceptData.getValue("ORDER_CODE");
        seqNo = acceptData.getValue("SEQ_NO");
        String sql = "select pasdr_code,pasdr_note from opd_order " +
        		" where case_no = '"+caseNo+"' and mr_no = '"+mrNo+"' and order_code  = '"+orderCode+"' " +
        		" and seq_no = '"+seqNo+"' ";
        //System.out.println("sql="+sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        
        this.setValue("PASDR_CODE", parm.getValue("PASDR_CODE", 0));
        this.setValue("PASDR_NOTE", parm.getValue("PASDR_NOTE", 0));
	}
	//保存确定按钮
	public void confirm(){
		String pasdrCode=this.pasdrCombo.getValue();
		String pasdrNote=this.pasdrNote.getValue();
		
		TParm result = new TParm();

		result.setData("PASDR_CODE", pasdrCode);
		result.setData("PASDR_NOTE", pasdrNote);
		result.setData("OPER", "CONFIRM");
		this.setReturnValue(result);
	    this.closeWindow();

		
		
	}
	//取消按钮
	public void onCan(){
		TParm result = new TParm();
		result.setData("OPER", "CANCLE");
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
