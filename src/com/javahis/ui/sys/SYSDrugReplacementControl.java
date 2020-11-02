/**
 * 
 */
package com.javahis.ui.sys;

import jdo.sys.Operator;
import jdo.sys.SYSDrugReplacementTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.event.TTableEvent;

/**
*
* <p>Title: ���ҩ</p>
*
* <p>Description:���ҩ </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui
* @version 1.0
*/
public class SYSDrugReplacementControl extends TControl {

	 TParm data;
	    int selectRow = -1;
	    public void onInit() {
	        super.onInit();
	        callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");
	        init();
	    }
	    /**
	     * ��ʼ�����棬��ѯ���е�����
	     * @return TParm
	     */
	    public void onQuery() {
	    	
			 String ORDER_CODE = String.valueOf(this.getValue("ORDER_CODE"));
			 String REPLACE_ORDER_CODE = String.valueOf(this.getValue("REPLACE_ORDER_CODE"));
			 if((ORDER_CODE==null||"".equals(ORDER_CODE))&&(REPLACE_ORDER_CODE==null||"".equals(REPLACE_ORDER_CODE))){
				 this.callFunction("UI|TABLE|onQuery");
			 }else {
//				 this.callFunction(message, parameters)
				 data = SYSDrugReplacementTool.getInstance().selectdata(ORDER_CODE,REPLACE_ORDER_CODE);
			        if(data.getErrCode() < 0)
			        {
			            messageBox(data.getErrText());
			            return;
			        }
			        this.callFunction("UI|TABLE|setParmValue",data,"SEQ;ORDER_CODE;ORDER_DESC;REPLACE_ORDER_CODE;REPLACE_ORDER_DESC;DESCRIPTION;OPT_USER;OPT_DATE");
			 }
		        
		}
	    public void onTABLEClicked(int row){

//	        System.out.println("row=" + row);

	        if (row < 0) {
	        	return;
	        }
//	        System.out.println("data"+data);
//	        setValueForParm("SEQ;EXCPHATYPE_CODE;EXCPHATYPE_CHN_DESC;PY1;PY2;EXCPHATYPE_ENG_DESC;DESCRIPTION;PRINT_FLG",data,row);
//	        
	       selectRow = row;
	       callFunction("UI|ORDER_CODE|setEnabled",false);
	       callFunction("UI|REPLACE_ORDER_CODE|setEnabled",false);
	       
		}
	    /**
	     *���
	     */
	    public void onClear() {
	        this.clearValue("SEQ;ORDER_CODE;ORDER_DESC;REPLACE_ORDER_CODE;REPLACE_ORDER_DESC;DESCRIPTION;OPT_USER;OPT_DATE");
	        
	        this.callFunction("UI|TABLE|onQuery");
	        selectRow = -1;
	        callFunction("UI|ORDER_CODE|setEnabled",true);
		    callFunction("UI|REPLACE_ORDER_CODE|setEnabled",true);
//		    callFunction("UI|TABLE|ClearSelection");
	    }
	    /**
	     * ����
	     */
	    public void onInsert() {
	    	if(!this.emptyTextCheck("ORDER_CODE,REPLACE_ORDER_CODE")){
	    		return;
	    	}
	    	
	    	TParm parameters=new TParm();
	    	parameters.setData("OPT_USER",Operator.getID());
	    	parameters.setData("OPT_TERM", Operator.getIP());
	    	 SystemTool st=new SystemTool();
	    	parameters.setData("OPT_DATE", st.getDate());
	    	
	    	this.callFunction("UI|TABLE|setModuleParmInsert",parameters);
	    	Boolean result=(Boolean)this.callFunction("UI|TABLE|onInsert");
	    	if(result.booleanValue()){
	    		this.messageBox("P0002");
	    		onClear();
	    	}else{
	    		this.messageBox("E0002");
	    		onClear();
	    	}
	    }
	    /**
	     * ����
	     */
	    public void onUpdate() {
	    	if(!this.emptyTextCheck("ORDER_CODE,REPLACE_ORDER_CODE")){
	    		return;
	    	}
	    	TParm parameters=new TParm();
	    	parameters.setData("OPT_USER",Operator.getID());
	    	parameters.setData("OPT_TERM", Operator.getIP());
	    	 SystemTool st=new SystemTool();
	    	parameters.setData("OPT_DATE", st.getDate());
	    	this.callFunction("UI|TABLE|setModuleParmUpdate", parameters);
	    	Boolean result=(Boolean)this.callFunction("UI|TABLE|onUpdate");
	    	if(result.booleanValue()){
	    		this.messageBox("P0001");
	    		onClear();
	    	}else{
	    		this.messageBox("E0001");
	    		onClear();
	    	}
	    }
	    /**
	     * ����
	     */
	    public void onSave() {
	    	if(!this.emptyTextCheck("ORDER_CODE,REPLACE_ORDER_CODE"))
	    		return;
	        if(selectRow == -1)
	        {
		    		onInsert();
		    		return;
	        }
	        onUpdate();
	    }


	    /**
	     * ɾ��
	     */
	    public void onDelete() {
	    	Boolean result;
//	        if(selectRow == -1)
//	            return;
	    	if(!this.emptyTextCheck("ORDER_CODE,REPLACE_ORDER_CODE")){
	    		return;
	    	}
	        if(this.messageBox("ѯ��","ȷ��ɾ��",2)==0){
	        	result=(Boolean)this.callFunction("UI|TABLE|onDelete");
	        }else{
	        	return ;
	        }
	        if(result){
	        	this.messageBox("P0003");
	        	onClear();
	        	return;
	        }
	        	this.messageBox("E0003");
	        	onClear();
	        
	   }
	    /**
	     * ��ʼ�����棬��ѯ���е�����
	     * @return TParm
	     */
	    public void init(){
	    	onClear();
	    }
	    /**
	     * ���ҩ�������ҩ��code �ı���ĵ���¼�����ѯ����ҩƷ������д�������ı���
	     */
	    public void onbeenOrderCode(){
	    	if(!this.emptyTextCheck("ORDER_CODE"))
	    		return;
	    	String ORDER_CODE=TCM_Transform.getString( this.getValue("ORDER_CODE"));
	    	TParm result = SYSDrugReplacementTool.getInstance().selectSysFee(ORDER_CODE);
	    	this.setValue("ORDER_DESC", result.getValue("ORDER_DESC",0));
	    }
	    /**
	     * ���ҩ�������ҩ��code �ı���ĵ���¼�����ѯ����ҩƷ������д�������ı���
	     */
	    public void onOrderCode(){
	    	if(!this.emptyTextCheck("REPLACE_ORDER_CODE"))
	    		return;
	    	String REPLACE_ORDER_CODE=TCM_Transform.getString( this.getValue("REPLACE_ORDER_CODE"));
	    	TParm result = SYSDrugReplacementTool.getInstance().selectSysFee(REPLACE_ORDER_CODE);
	    	this.setValue("REPLACE_ORDER_DESC", result.getValue("ORDER_DESC",0));
	    }

}
