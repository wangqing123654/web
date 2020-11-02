package com.javahis.ui.odi;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;

/**
 * 西药审核-给药原因说明
 * @author duzhw-20131121
 *
 */
public class ODIOrderDrugDescControl extends TControl {
	
	//页面控件
	private TComboBox pasphaCombo;
	private TTextField pasphaNote;
	
	//参数
	private String caseNo = "";
	private String mrNo = "";
	private String orderNo = "";
	private String orderSeq = "";
	private String orderCode = "";
	
	TParm acceptData = new TParm(); // 接参
	
	public void onInit() {
		super.onInit();
		initComponent();
		Object obj = this.getParameter();

        acceptData = (TParm)this.getParameter();
        caseNo = acceptData.getValue("CASE_NO");
        orderNo = acceptData.getValue("ORDER_NO");
        orderSeq = acceptData.getValue("ORDER_SEQ");
        orderCode = acceptData.getValue("ORDER_CODE");
        String sql = "select paspha_code,paspha_note from odi_order " +
        		"where case_no = '"+caseNo+"' and order_no = '"+orderNo+"' and " +
        		" order_seq = '"+orderSeq+"' and order_code  = '"+orderCode+"'";
        
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        
        this.setValue("PASPHA_CODE", parm.getValue("PASPHA_CODE", 0));
        this.setValue("PASPHA_NOTE", parm.getValue("PASPHA_NOTE", 0));
	}
	//保存确定按钮
	public void confirm(){
		String pasphaCode=this.pasphaCombo.getValue();
		String pasphaNote=this.pasphaNote.getValue();
		String sql= "update odi_order set paspha_code = '"+pasphaCode+"' ,paspha_note = '"+pasphaNote+"' " +
				"where case_no='"+caseNo+"' and order_no='"+orderNo+"' and order_seq='"+orderSeq+"' and order_code='"+orderCode+"'";
		
		//System.out.println("sql="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
            this.messageBox("E0005"); //失败
		}else{
            this.messageBox("P0005"); //成功
            onInit();
            this.closeWindow();
       }
	}
	//取消按钮
	public void onCan(){
        this.closeWindow();
    }
	
	/**
    *
    * 初始化页面控件便于程序调用
    */
   private void initComponent(){
	   pasphaCombo=(TComboBox)this.getComponent("PASPHA_CODE");
	   this.pasphaNote=(TTextField)this.getComponent("PASPHA_NOTE");
   }

}
