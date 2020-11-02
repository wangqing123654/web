package com.javahis.ui.mem;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
/**
 * <p>Title: 套餐模板医嘱替换修改医嘱细相</p>
 *
 * <p>Description: 套餐模板医嘱替换修改医嘱细相</p>
 *
 * <p>Copyright: </p>
 *
 * <p>Company: bluecore</p>
 *
 * @author huangtt 20150710
 * @version 1.0
 */
public class MEMComOrderSetShowControl extends TControl{
	private TTable table;
	private TParm reParm = new TParm();
	private double priceSum = 0;
	
	   public void onInit() {
		   super.onInit();
	        //初始化控件
		   table = (TTable) this.getComponent("TABLE");
	        initParam();
	    }
	 
	   
	    
	    /**
		 * 初始化参数
		 */
		public void initParam() {
			TParm parm=(TParm)this.getParameter();
			table.acceptText();
			priceSum = parm.getDouble("RETAIL_PRICE_SUM");
			reParm = parm.getParm("fineParm");
			table.setParmValue(reParm);
			this.setValue("PRICE", priceSum);

		}
		
		public void onSave(){
			TParm tableParm = table.getParmValue();
			double sum = 0;
			for (int i = 0; i < tableParm.getCount("ORDER_CODE"); i++) {
				sum += tableParm.getDouble("RETAIL_PRICE", i);
				
			}
			if(sum != priceSum){
				this.messageBox("各细项销售价格之和与总价不等");
				return;
			}
			
			for (int i = 0; i < tableParm.getCount("ORDER_CODE"); i++) {
				if(reParm.getValue("ORDER_CODE", i).equals(tableParm.getValue("ORDER_CODE", i))){
					reParm.setData("RETAIL_PRICE", i, tableParm.getValue("RETAIL_PRICE", i));
				}
				
			}
			this.messageBox("保存成功");
			this.setReturnValue(reParm);
			this.closeWindow();
		}

}
