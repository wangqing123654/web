package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

public class REGOrderInfoControl extends TControl {
	private String mrNo;
	private String admDate;
	private String deptCode;
	private String deCode;
	private String time;
	private TTable table;
	private TParm acceptData;
	private String clinictypeCode; 
	private String crmId;// add by wangbin 2015/1/8 Ôö¼ÓCRMID
	
	public void onInit() {
		table = (TTable) this.getComponent("TABLE");
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			acceptData = (TParm) obj;
//			mrNo = acceptData.getValue("MR_NO");
		}
//		this.callFunction("UI|TABLE|addEventListener",
//				TTableEvent.CLICKED, this,
//				"onTableClicked"); 
//		TParm parmCRM = new TParm();
//		parmCRM.setData("MR_NO", mrNo);
//		TParm orderParm = TIOM_AppServer.executeAction("action.reg.REGCRMAction","orderInfo",parmCRM);
//		System.out.println("orderParm=="+orderParm);
//		if(orderParm.getCount("ADM_DATE")>0){
			table.setParmValue(acceptData);
//		}
		
	}
	
	public void onTableClicked(){
//		table = (TTable) obj;
		int row = table.getSelectedRow();
//		this.messageBox(row+"");
		TParm parm = table.getParmValue();
		admDate = parm.getValue("ADM_DATE", row);
		deptCode = parm.getValue("DEPT_CODE", row);
		deCode = parm.getValue("DR_CODE", row);
		clinictypeCode = parm.getValue("CLINICTYPE_CODE", row);
		time = parm.getValue("START_TIME", row);
		crmId = parm.getValue("CRM_ID", row);
//		System.out.println("time=="+time);
		
	}
	
	public void onTableDoubleClicked(){
		int row = table.getSelectedRow();
		TParm parm = table.getParmValue();
		admDate = parm.getValue("ADM_DATE", row);
		deptCode = parm.getValue("DEPT_CODE", row);
		deCode = parm.getValue("DR_CODE", row);
		clinictypeCode = parm.getValue("CLINICTYPE_CODE", row);
		time = parm.getValue("START_TIME", row);
		crmId = parm.getValue("CRM_ID", row);
//		System.out.println("time=="+time);
		this.onOk();
	}
	
	
	
	public void onOk(){
		TParm retDate = new TParm();
		retDate.setData("admDate", admDate);
		retDate.setData("deptCode", deptCode);
		retDate.setData("deCode", deCode);
		retDate.setData("clinictypeCode", clinictypeCode);
		retDate.setData("time", time.substring(0,5).replace(":", ""));
		retDate.setData("crmId", crmId);
//		System.out.println("retDate=="+retDate);
		this.setReturnValue(retDate);
		this.closeWindow();
	}
	
	public void onC(){
		this.closeWindow();
	}
	
	

}
