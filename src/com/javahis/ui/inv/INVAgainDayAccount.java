package com.javahis.ui.inv;

import jdo.inv.INVsettlementTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:�����ս�
 * </p>    
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author chenxi  2013.08.08
 * @version 4.0
 */
public class INVAgainDayAccount extends TControl{

	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit() ;
		onInitPage() ;
	}
	/**
	 * �����ս�����
	 */
	public void onSave(){
		TParm parm = this.getSearchParm() ;
		//�ս�������ddstockΪ������ڣ������������ս�
		String message = this.check(parm) ;
		if(!message.equals("SUCCESS")){
			this.messageBox(message) ;
			return ;
		}		
		TParm result = TIOM_AppServer.executeAction("action.inv.INVsettlementAction",
                "onAgainDayAccount", parm);
    	if(result.getErrCode()<0){
    		this.messageBox("E0001") ;
    		return ;
    	}
    	this.messageBox("P0001") ;
	}
	  /**
     * ��ȡ��ѯ��������
     * @return
     * */
 	private TParm getSearchParm() {
 		TParm searchParm = new TParm();
 		String closeDate = getValueString("DATE").substring(0, 10).replace("-", "");
 		String yesterday = (Integer.parseInt(closeDate.substring(0, 8))-1)+"" ;
 		searchParm.setData("START_DATE",closeDate+"000000"); 
 		searchParm.setData("END_DATE",closeDate+"235959");  
 		searchParm.setData("OPT_USER",Operator.getID()); 
 		searchParm.setData("OPT_TERM",Operator.getIP()); 
 		searchParm.setData("YESTERDAY",yesterday); 
 		if(this.getValueString("ORG_CODE").length()>0){
 			searchParm.setData("ORG_CODE", this.getValueString("ORG_CODE")) ;
 		}
 		return searchParm;
 	}
 	/**
 	 * ��ʼ��ҳ��
 	 */
 	public void onInitPage(){
 		String now = SystemTool.getInstance().getDate().toString().replace("-", "") ;
		this.setValue("DATE", StringTool.getTimestamp(now, "yyyyMMdd")) ; 
 	}
 	/**
 	 * У��
 	 * @param parm
 	 * @return
 	 */
 	public  String check(TParm parm){
 		String tranDate = parm.getValue("START_DATE").substring(0, 8) ;
 		String yesterday = parm.getValue("YESTERDAY") ;
 		//�ж�ѡ��������Ƿ����ս�
 		TParm result = INVsettlementTool.getInstance().selectTranDate(parm.getValue("START_DATE").substring(0, 8)) ;
 		if(result.getCount()>0){
 			return "���ս�" ;
 		}
 		result=INVsettlementTool.getInstance().maxTranDate();
 		if(result.getCount()<0){
 			return "SUCCESS" ;
 		}
 		if(Integer.parseInt(tranDate)>Integer.parseInt(result.getRow(0).getValue("TRANDATE"))){
 			return "���ڹ���" ;
 		}
 		result =INVsettlementTool.getInstance().yesterdayTranDate(yesterday);
 		System.out.println("result======="+result);
 		if(result.getCount()<0){
 			return "���ս�"+yesterday ;
 		}
 		return "SUCCESS" ;
 	}
}
