package com.javahis.ui.mem;


import jdo.mem.MEMEcouponTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;

public class MEMEcouponControl extends TControl{
	/**
	*
	* <p>Title:电子优惠券设定</p>
	*
	* <p>Description: 电子优惠券设定</p>
	*
	* <p>Copyright: Copyright (c) caoyong 20131225/p>
	*
	* <p>Company: BlueCore</p>
	*
	* @author caoyong
	* @version 1.0
	*/
private TTable table;
	
	
	/**
	 * 得到TABLE对象
	 * @param tagName
	 * @return
	 */
	 private TTable getTable(String tagName) {
			return (TTable) getComponent(tagName);
		}
	
	  /**
     * 初始化
     */
    public void onInit(){
    	table=this.getTable("TABLE");
    	callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");//单击事件
		// 时间间隔为1天
		// 初始化查询区间
        this.inPage();
    	
    }
    /**
     * 添加
     */
    
    public void onSave(){
    	  TParm parm=new TParm();
    	  TParm result=new TParm();
    	  if("".equals(getValueString("MEM_CODE"))&&getValueString("MEM_CODE")!=null){
    		  this.messageBox("请输入优惠券号");
    		  this.grabFocus("MEM_CODE");
    		  return;
    	  }
    	  
    	   parm.setData("MEM_CODE",this.getValueString("MEM_CODE"));//
    	   parm.setData("MEM_DESC",this.getValueString("MEM_DESC"));
    	   parm.setData("MEM_ENG_DESC",this.getValueString("MEM_ENG_DESC") );//
    	   parm.setData("PY1",this.getValueString("PY1") );
    	   parm.setData("MEM_TYPE",2 );
    	   parm.setData("DESCRIPTION",this.getValueString("DESCRIPTION") );
    	   parm.setData("VALID_DAYS",this.getValueString("VALID_DAYS") );
    	   parm.setData("OPT_DATE",SystemTool.getInstance().getDate());
    	   parm.setData("OPT_USER",Operator.getID() );
    	   parm.setData("OPT_TERM",Operator.getIP() );
    	   TParm sreslt= MEMEcouponTool.getInstance().selectCard(this.getValueString("MEM_CODE"));
    	   if(sreslt.getCount()>0){
    		 result=MEMEcouponTool.getInstance().updatedata(parm);//修改
       		 if(result.getErrCode()<0){
             		 this.messageBox("修改失败");
             		 return ;
             	     }
             		 this.messageBox("修改成功");
             		  inPage();
             		 
    		   
    	   }else{
    	   
    	    result= MEMEcouponTool.getInstance().insertCard(parm);
				   if(result.getErrCode()<0){
			   		 this.messageBox("添加失败");
			   		 return ;
			   	     }
			   		 this.messageBox("添加成功");
			   		 inPage();
    	   }
    	   
    }
    /**
	 * *增加对Table的监听
	 */
	public void onTABLEClicked(int row){
		
		
	   TParm tparm = table.getParmValue().getRow(row);
	   callFunction("UI|MEM_CODE|setEnabled", false);
	   this.setValue("MEM_CODE",tparm.getValue("MEM_CODE"));
	   this.setValue("MEM_DESC",tparm.getValue("MEM_DESC"));
	   this.setValue("MEM_ENG_DESC",tparm.getValue("MEM_ENG_DESC") );
	   this.setValue("PY1",tparm.getValue("PY1") );
	   this.setValue("DESCRIPTION",tparm.getValue("DESCRIPTION") );
	}
	/**
	 * 初始化
	 */
	public void inPage(){
		
		
		
		TParm selectCardall=MEMEcouponTool.getInstance().selectCardall();
		table.setParmValue(selectCardall);
	}
	/**
	 * 名称回车事件
	 */
	public void onUserPY1() {
		String userName = getValueString("MEM_DESC");
		String py = TMessage.getPy(userName);
		setValue("PY1", py);
		((TTextField) getComponent("PY1")).grabFocus();
	}
	/**
	 * 名称回车事件
	 */
	public void onUserPY2() {
		String userName = getValueString("MEM_ENG_DESC");
		String py = TMessage.getPy(userName);
		setValue("PY1", py);
		((TTextField) getComponent("PY1")).grabFocus();
	}
	
	/**
	 * 查询
	 */
	public void onQuery() {
		 TParm parm=new TParm();
		 
		 parm.setData("MEM_CODE",this.getValueString("MEM_CODE"));//会员代码
  	     parm.setData("MEM_DESC",this.getValueString("MEM_DESC"));//会员名称
  	     parm.setData("MEM_ENG_DESC",this.getValueString("MEM_ENG_DESC") );//英文
		 TParm result=MEMEcouponTool.getInstance().selectdata(parm);
		 if(result.getErrCode()<0){
			 this.messageBox("查询出错");
			 return;
		 }
		 if(result.getCount()<=0){
			 this.messageBox("没有查询数据");
			 table.removeRowAll();
			 return;
		 }
		    table.setParmValue(result);
	}
	/**
	 * 删除
	 */
	public void onDelete(){
		 TParm result =new TParm();
		
		 TParm parm=table.getParmValue();
		 if(parm.getCount()<=0){
			 
			this.messageBox("没有保存数据");
			return;
		 }
		int row=table.getSelectedRow();
		if(row<0){
			this.messageBox("请选择要删除的数据");
			return ;
		}
		if (this.messageBox("是否删除", "确认要删除吗？", 2) == 0) {
		  TParm tparm=table.getParmValue().getRow(row);
		  TParm dparm=new TParm();
		  dparm.setData("MEM_CODE", tparm.getValue("MEM_CODE"));
		  result=MEMEcouponTool.getInstance().deletedata(dparm);
		
		  if(result.getErrCode()<0){
			  this.messageBox(" 删除失败");
			  return;
		  }
		       this.messageBox("删除成功");
		       onClear();
		       this.inPage();
		  }
	 
	}
	
	
	  /**
	 * 清空
	 */
	public void onClear() {
		callFunction("UI|MEM_CODE|setEnabled", true);
		String clearString="MEM_CODE;MEM_DESC;MEM_ENG_DESC;PY1;MEM_TYPE;DESCRIPTION;VALID_DAYS;";
		clearValue(clearString);
		table.removeRowAll();
		//this.onInit();
		
	}
	
	
	  

}
