package com.javahis.ui.mem;


import jdo.mem.MEMGiftCardSettingTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;

public class MEMGiftCardSettingControl extends TControl{
	/**
	*
	* <p>Title:礼品卡设定</p>
	*
	* <p>Description: 礼品卡设定</p>
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
    	  if("".equals(getValueString("GIFTCARD_CODE"))&&getValueString("GIFTCARD_CODE")!=null){
    		  this.messageBox("请输入礼品卡代码");
    		  this.grabFocus("MEM_CODE");
    		  return;
    	  }
    	  
    	  if(this.getValueDouble("FACE_VALUE")<0 || this.getValueDouble("RETAIL_PRICE") < 0){
    		  this.messageBox("金额不能为负数");
    		 return;
    	  }
    	  
    	   parm.setData("GIFTCARD_CODE",this.getValueString("GIFTCARD_CODE"));//
    	   parm.setData("GIFTCARD_DESC",this.getValueString("GIFTCARD_DESC"));
    	   parm.setData("GIFTCARD_ENG_DESC",this.getValueString("GIFTCARD_ENG_DESC") );//
    	   parm.setData("PY1",this.getValueString("PY1") );
    	   parm.setData("CARD_TYPE",this.getValueString("CARD_TYPE") );
    	   parm.setData("RETAIL_PRICE",this.getValueString("RETAIL_PRICE") );
    	   parm.setData("FACE_VALUE",this.getValueString("FACE_VALUE") );
    	   parm.setData("DESCRIPTION",this.getValueString("DESCRIPTION") );
    	   parm.setData("SEQ",this.getValueString("SEQ") );
    	   parm.setData("OPT_DATE",SystemTool.getInstance().getDate());
    	   parm.setData("OPT_USER",Operator.getID() );
    	   parm.setData("OPT_TERM",Operator.getIP() );
    	   TParm sreslt= MEMGiftCardSettingTool.getInstance().selectCard(this.getValueString("GIFTCARD_CODE"));
    	   if(sreslt.getCount()>0){
    		 result=MEMGiftCardSettingTool.getInstance().updatedata(parm);//修改
       		 if(result.getErrCode()<0){
             		 this.messageBox("修改失败");
             		 return ;
             	     }
             		 this.messageBox("修改成功");
             		  inPage();
             		 
    		   
    	   }else{
    	   
    	    result= MEMGiftCardSettingTool.getInstance().insertCard(parm);
				   if(result.getErrCode()<0){
			   		 this.messageBox("添加失败");
			   		 return ;
			   	     }
			   		 this.messageBox("添加成功");
			   		 onClear();
			   		 inPage();
    	   }
    	   onQuery();
    	   
    }
    /**
	 * *增加对Table的监听
	 */
	public void onTABLEClicked(int row){
		
		
	   TParm tparm = table.getParmValue().getRow(row);
	   callFunction("UI|GIFTCARD_CODE|setEnabled", false);
	   this.setValue("GIFTCARD_CODE",tparm.getValue("GIFTCARD_CODE"));
	   this.setValue("GIFTCARD_DESC",tparm.getValue("GIFTCARD_DESC"));
	   this.setValue("GIFTCARD_ENG_DESC",tparm.getValue("GIFTCARD_ENG_DESC") );
	   this.setValue("PY1",tparm.getValue("PY1") );
	   this.setValue("CARD_TYPE",tparm.getValue("CARD_TYPE") );
	   this.setValue("FACE_VALUE",tparm.getValue("FACE_VALUE") );
	   this.setValue("RETAIL_PRICE",tparm.getValue("RETAIL_PRICE") );
	   this.setValue("SEQ",tparm.getValue("SEQ") );
	   this.setValue("DESCRIPTION",tparm.getValue("DESCRIPTION") );
	}
	/**
	 * 初始化
	 */
	public void inPage(){
		
		this.setValue("SEQ", String.valueOf(getMaxSeq()+1));
		TParm selectCardall=MEMGiftCardSettingTool.getInstance().selectCardall();
		table.setParmValue(selectCardall);
	}
	/**
	 * 名称回车事件
	 */
	public void onUserPY1() {
		String userName = getValueString("GIFTCARD_DESC");
		String py = TMessage.getPy(userName);
		setValue("PY1", py);
		((TTextField) getComponent("PY1")).grabFocus();
	}
//	/**
//	 * 名称回车事件
//	 */
//	public void onUserPY2() {
//		String userName = getValueString("GIFTCARD_ENG_DESC");
//		String py = TMessage.getPy(userName);
//		setValue("PY1", py);
//		((TTextField) getComponent("PY1")).grabFocus();
//	}
	
	/**
	 * 查询
	 */
	public void onQuery() {
		 TParm parm=new TParm();
		 
		 parm.setData("GIFTCARD_CODE",this.getValueString("GIFTCARD_CODE"));//会员代码
  	     parm.setData("GIFTCARD_DESC",this.getValueString("GIFTCARD_DESC"));//会员名称
  	     parm.setData("GIFTCARD_ENG_DESC",this.getValueString("GIFTCARD_ENG_DESC") );//英文
		 TParm result=MEMGiftCardSettingTool.getInstance().selectdata(parm);
		 if(result.getErrCode()<0){
			 this.messageBox("查询出错");
			 return;
		 }
		 if(result.getCount()<=0){
			 this.messageBox("没有查询数据");
			 table.removeRowAll();
			 return;
		 }
//		 System.out.println(result);
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
		  dparm.setData("GIFTCARD_CODE", tparm.getValue("GIFTCARD_CODE"));
		  result=MEMGiftCardSettingTool.getInstance().deletedata(dparm);
		
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
		callFunction("UI|GIFTCARD_CODE|setEnabled", true);
		
		String clearString="GIFTCARD_CODE;GIFTCARD_DESC;GIFTCARD_ENG_DESC;PY1;CARD_TYPE;FACE_VALUE;RETAIL_PRICE;SEQ;DESCRIPTION";
		clearValue(clearString);
		this.setValue("SEQ", String.valueOf(getMaxSeq()+1));
		table.removeRowAll();
		//this.onInit();
		
	}
	/**
	 * 取得序号最大值
	 * @return
	 */
	public int getMaxSeq(){
		String sql="SELECT MAX(SEQ) AS SEQ FROM MEM_CASH_CARD_INFO ";
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result.getInt("SEQ",0);
	}
	  

}
