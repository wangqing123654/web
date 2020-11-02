package com.javahis.ui.ibs;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;

public class IBSLumpworkAdjustmentControl extends TControl{
  private TParm tableYParm=new TParm();
  private TParm tableNParm=new TParm();
  private TTable tableY;
  private TTable tableN;
  private double tot_qty;
  private TParm initParm;
	public void onInit() {
	       super.onInit();
	   	Object obj = this.getParameter();
		if(obj instanceof TParm){
		 initParm = (TParm) obj;
		 tableY=(TTable) this.getComponent("TABLE_Y"); 
		 tableN=(TTable) this.getComponent("TABLE_N");
		 tableY.addEventListener("TABLE_Y->"
					+ TTableEvent.CHANGE_VALUE, this, "onTableChangeValue");
		 tableN.addEventListener("TABLE_N->"
				 + TTableEvent.CHANGE_VALUE, this, "onTableChangeValue");
	
		 tableY.onInit();
		 tableN.onInit();
		tot_qty=initParm.getDouble("DOSAGE_QTY");
		
			tableYParm.setData("INCLUDE_FLG",0,"Y");
			tableYParm.setData("DOSAGE_QTY",0,1.0);
			tableYParm.setData("TOT_AMT",0,initParm.getDouble("OWN_PRICE")*1.0);
			tableNParm.setData("INCLUDE_FLG",0,"N");
			tableNParm.setData("TOT_AMT",0,initParm.getDouble("OWN_PRICE")*(tot_qty-1.0)*initParm.getDouble("OWN_RATE"));
		
			tableYParm.setData("BILL_DATE",0,initParm.getData("BILL_DATE"));
			tableYParm.setData("ORDER_CODE",0,initParm.getData("ORDER_CODE"));
			tableYParm.setData("ORDER_DESC",0,initParm.getData("ORDER_DESC"));
			tableYParm.setData("OWN_FLG" ,0,initParm.getData("OWN_FLG"));
			tableYParm.setData("DOSAGE_UNIT",0,initParm.getData("DOSAGE_UNIT"));
			tableYParm.setData("OWN_PRICE",0,initParm.getData("OWN_PRICE"));
			tableYParm.setData("COST_CENTER_CODE",0,initParm.getData("COST_CENTER_CODE"));
			tableYParm.setData("OPT_DATE",0,initParm.getData("OPT_DATE"));
			tableYParm.setData("USER_NAME",0,initParm.getData("USER_NAME"));
			tableYParm.setData("ORDER_NO",0,initParm.getData("ORDER_NO"));
			tableYParm.setData("ORDER_SEQ",0,initParm.getData("ORDER_SEQ"));
			tableYParm.setData("OWN_RATE",0,initParm.getData("OWN_RATE"));
			tableYParm.setCount(1);
			tableY.setParmValue(tableYParm);
	   
			
			tableNParm.setData("BILL_DATE",0,initParm.getData("BILL_DATE"));
			tableNParm.setData("ORDER_CODE",0,initParm.getData("ORDER_CODE"));
			tableNParm.setData("ORDER_DESC",0,initParm.getData("ORDER_DESC"));
			tableNParm.setData("OWN_FLG" ,0,initParm.getData("OWN_FLG"));
			tableNParm.setData("DOSAGE_QTY",0,initParm.getDouble("DOSAGE_QTY")-1);
			tableNParm.setData("DOSAGE_UNIT",0,initParm.getData("DOSAGE_UNIT"));
			tableNParm.setData("OWN_PRICE",0,initParm.getData("OWN_PRICE"));
			tableNParm.setData("COST_CENTER_CODE",0,initParm.getData("COST_CENTER_CODE"));
			tableNParm.setData("OPT_DATE",0,initParm.getData("OPT_DATE"));
			tableNParm.setData("USER_NAME",0,initParm.getData("USER_NAME"));
			tableNParm.setData("ORDER_NO",0,initParm.getData("ORDER_NO"));
			tableNParm.setData("ORDER_SEQ",0,initParm.getData("ORDER_SEQ"));
			tableNParm.setData("OWN_RATE",0,initParm.getData("OWN_RATE"));
			tableNParm.setCount(1);
			tableN.setParmValue(tableNParm);
		
		}
	       }
	/**
	 * 医嘱数量改变
	*/
	public void onTableChangeValue(TTableNode tNode){
		tableY.acceptText();
		tableN.acceptText();
		double qty=Double.valueOf(tNode.getValue()+"");
		double old_qty=Double.valueOf(tNode.getOldValue()+"");
		if("TABLE_Y".equals(tNode.getTable().getTag()+"")){
			if(tot_qty-qty<=0||qty<=0){
				this.messageBox("医嘱数量不正确");
				tNode.setValue(old_qty);
				return;
			}
			tableNParm.setData("DOSAGE_QTY",0,tot_qty-qty);
			tableN.setParmValue(tableNParm);
			tNode.setValue(qty);
			}else{
				if(tot_qty-qty<=0||qty<=0){
					this.messageBox("医嘱数量不正确");
					tNode.setValue(old_qty);
					return;
				}
				tableYParm.setData("DOSAGE_QTY",0,tot_qty-qty);
				tableY.setParmValue(tableYParm);
				tNode.setValue(qty);
			}
		tableY.acceptText();
		tableN.acceptText();
	}

	/**
	 * 保存
	*/
	public void onSave(){
		TParm parm=new TParm();
		TParm result=new TParm();
		TParm tableYparm=tableY.getParmValue();
		TParm tableNparm=tableN.getParmValue();
		parm.setData("initParm", initParm.getData());
		parm.setData("tableYParm", tableYparm.getData());
		parm.setData("tableNParm", tableNparm.getData());
		result = TIOM_AppServer.executeAction("action.ibs.IBSPackageAdjust", "onPackageAdjust", parm);
		if(result.getErrCode()<0){
			this.messageBox("套餐调整失败");
			this.setReturnValue(result);
			return;
		}else{
		this.messageBox("套餐调整成功");
		this.setReturnValue(result);
		}
		this.closeWindow();
	}
}
