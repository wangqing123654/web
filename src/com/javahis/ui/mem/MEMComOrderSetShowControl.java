package com.javahis.ui.mem;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
/**
 * <p>Title: �ײ�ģ��ҽ���滻�޸�ҽ��ϸ��</p>
 *
 * <p>Description: �ײ�ģ��ҽ���滻�޸�ҽ��ϸ��</p>
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
	        //��ʼ���ؼ�
		   table = (TTable) this.getComponent("TABLE");
	        initParam();
	    }
	 
	   
	    
	    /**
		 * ��ʼ������
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
				this.messageBox("��ϸ�����ۼ۸�֮�����ܼ۲���");
				return;
			}
			
			for (int i = 0; i < tableParm.getCount("ORDER_CODE"); i++) {
				if(reParm.getValue("ORDER_CODE", i).equals(tableParm.getValue("ORDER_CODE", i))){
					reParm.setData("RETAIL_PRICE", i, tableParm.getValue("RETAIL_PRICE", i));
				}
				
			}
			this.messageBox("����ɹ�");
			this.setReturnValue(reParm);
			this.closeWindow();
		}

}
