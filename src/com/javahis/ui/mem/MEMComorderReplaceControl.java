package com.javahis.ui.mem;

import java.math.BigDecimal;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.ui.testOpb.tools.OrderTool;

/**
 * <p>Title: 套餐模板医嘱替换</p>
 *
 * <p>Description: 套餐模板医嘱替换</p>
 *
 * <p>Copyright: </p>
 *
 * <p>Company: bluecore</p>
 *
 * @author huangtt 20150710
 * @version 1.0
 */
public class MEMComorderReplaceControl extends TControl{
	private static String URL_SYSFEEPOPUP = "%ROOT%\\config\\sys\\SYSFeePopup.x";
	private static String TAG_ORDER_CODE = "ORDER_CODE";
	private static String TAG_ORDER_DESC = "ORDER_DESC";
	private static String TAG_NEW_ORDER_CODE = "NEW_ORDER_CODE";
	private static String TAG_NEW_ORDER_DESC = "NEW_ORDER_DESC";
	private static TTable table;
	private static TParm tableParm = new TParm();
	private static TParm newOrderParm = new TParm();
	private TParm orderParm = new TParm();  //主项
	private TParm fineOrderParm = new TParm(); //细项
	
	public void onInit(){
		table = (TTable) this.getComponent("TABLE");
		callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
				URL_SYSFEEPOPUP);// 医嘱代码
		callFunction("UI|ORDER_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturnOrder");
		callFunction("UI|NEW_ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
				URL_SYSFEEPOPUP);// 医嘱代码
		callFunction("UI|NEW_ORDER_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturnNewOrder");
	}
	
	
	public void popReturnOrder(String tag, Object obj) {
		TParm parm = (TParm) obj;
		setValue(TAG_ORDER_CODE, parm.getValue("ORDER_CODE"));
		setValue(TAG_ORDER_DESC, parm.getValue("ORDER_DESC"));
	}
	
	public void popReturnNewOrder(String tag, Object obj) {
		orderParm = (TParm) obj;
		setValue(TAG_NEW_ORDER_CODE, orderParm.getValue("ORDER_CODE"));
		setValue(TAG_NEW_ORDER_DESC, orderParm.getValue("ORDER_DESC"));
		if(orderParm.getBoolean("ORDERSET_FLG")){
			//主项价格
			String sql = "SELECT SUM(B.OWN_PRICE) PRICE " +
			" FROM SYS_ORDERSETDETAIL A,SYS_FEE B" +
			" WHERE A.ORDER_CODE = B.ORDER_CODE" +
			" AND A.ORDERSET_CODE='"+orderParm.getValue("ORDER_CODE")+"'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			orderParm.setData("OWN_PRICE", parm.getDouble("PRICE", 0));
			//细项
			sql = "SELECT B.ORDER_CODE,B.ORDER_DESC,B.UNIT_CODE,B.OWN_PRICE " +
			" FROM SYS_ORDERSETDETAIL A,SYS_FEE B" +
			" WHERE A.ORDER_CODE = B.ORDER_CODE" +
			" AND A.ORDERSET_CODE='"+orderParm.getValue("ORDER_CODE")+"'";
			parm = new TParm(TJDODBTool.getInstance().select(sql));
			fineOrderParm = new TParm();
			for (int i = 0; i < parm.getCount(); i++) {

				fineOrderParm.addData("ORDER_CODE", parm.getValue("ORDER_CODE",i));
				fineOrderParm.addData("ORDERSET_CODE", parm.getValue("ORDER_CODE"));
				fineOrderParm.addData("ORDER_DESC", parm.getValue("ORDER_DESC",i));
				fineOrderParm.addData("UNIT_CODE", parm.getValue("UNIT_CODE",i));
				fineOrderParm.addData("UNIT_PRICE", parm.getValue("OWN_PRICE",i));

			}
	
		}
	}
	
	//批量替换医嘱
	public void onBatchReplace(){
		newOrderParm = new TParm();
		String time =  OrderTool.getInstance().getSystemTime();
		for (int i = 0; i < tableParm.getCount(); i++) {
			
			tableParm.setData("VERSION_NUMBER", i,time);
			tableParm.setData("NEW_ORDER_CODE", i, orderParm.getValue("ORDER_CODE"));
			tableParm.setData("NEW_ORDER_DESC", i, orderParm.getValue("ORDER_DESC"));
			tableParm.setData("NEW_UNIT_CODE", i, orderParm.getValue("UNIT_CODE"));
			tableParm.setData("NEW_UNIT_PRICE", i, orderParm.getValue("OWN_PRICE"));
			tableParm.setData("NEW_SUM_PRICE", i, orderParm.getDouble("OWN_PRICE")*tableParm.getDouble("ORDER_NUM",i));
			tableParm.setData("ORDERSET_FLG", i, orderParm.getValue("ORDERSET_FLG"));
			tableParm.setData("OPT_TERM", i,  Operator.getIP());
			tableParm.setData("OPT_USER", i, Operator.getID());
			
			newOrderParm.addData("VERSION_NUMBER", time);
			newOrderParm.addData("ID", tableParm.getValue("ID", i));
			newOrderParm.addData("TRADE_NO", tableParm.getValue("TRADE_NO", i));
			newOrderParm.addData("PACKAGE_CODE", tableParm.getValue("PACKAGE_CODE", i));
			newOrderParm.addData("SECTION_CODE", tableParm.getValue("SECTION_CODE", i));
			newOrderParm.addData("SECTION_DESC", tableParm.getValue("SECTION_DESC", i));
			newOrderParm.addData("ORDERSET_GROUP_NO", tableParm.getValue("ORDERSET_GROUP_NO", i));
			newOrderParm.addData("RETAIL_PRICE", tableParm.getValue("RETAIL_PRICE", i));
			newOrderParm.addData("HIDE_FLG", "N");
			newOrderParm.addData("ORDER_CODE", orderParm.getValue("ORDER_CODE"));
			newOrderParm.addData("ORDERSET_CODE", orderParm.getValue("ORDER_CODE"));
			newOrderParm.addData("ORDER_DESC", orderParm.getValue("ORDER_DESC"));
			newOrderParm.addData("UNIT_CODE", orderParm.getValue("UNIT_CODE"));
			newOrderParm.addData("UNIT_PRICE", orderParm.getValue("OWN_PRICE"));
			newOrderParm.addData("SUM_PRICE", orderParm.getDouble("OWN_PRICE")*tableParm.getDouble("ORDER_NUM",i));
			newOrderParm.addData("ORDER_NUM", tableParm.getInt("ORDER_NUM",i));
			newOrderParm.addData("OPT_TERM", Operator.getIP());
			newOrderParm.addData("OPT_USER", Operator.getID());
			newOrderParm.addData("UN_NUM_FLG", tableParm.getValue("UN_NUM_FLG", i));
			
					
			BigDecimal sumPrice =new BigDecimal(0);
			BigDecimal retailPrice =new BigDecimal(tableParm.getDouble("RETAIL_PRICE", i));
			if(orderParm.getBoolean("ORDERSET_FLG")){
				newOrderParm.addData("SETMAIN_FLG", "Y");
				double price =0;
				for (int j = 0; j < fineOrderParm.getCount("ORDER_CODE"); j++) {
					newOrderParm.addData("VERSION_NUMBER", time);
					newOrderParm.addData("ID", "");
					newOrderParm.addData("TRADE_NO", tableParm.getValue("TRADE_NO", i));
					newOrderParm.addData("PACKAGE_CODE", tableParm.getValue("PACKAGE_CODE", i));
					newOrderParm.addData("SECTION_CODE", tableParm.getValue("SECTION_CODE", i));
					newOrderParm.addData("SECTION_DESC", tableParm.getValue("SECTION_DESC", i));
					newOrderParm.addData("ORDERSET_GROUP_NO", tableParm.getValue("ORDERSET_GROUP_NO", i));				
					newOrderParm.addData("SETMAIN_FLG", "N");
					newOrderParm.addData("HIDE_FLG", "Y");
					newOrderParm.addData("ORDER_CODE", fineOrderParm.getValue("ORDER_CODE",j));
					newOrderParm.addData("ORDERSET_CODE", orderParm.getValue("ORDER_CODE"));
					newOrderParm.addData("ORDER_DESC", fineOrderParm.getValue("ORDER_DESC",j));
					newOrderParm.addData("UNIT_CODE", fineOrderParm.getValue("UNIT_CODE",j));
					newOrderParm.addData("UNIT_PRICE", fineOrderParm.getValue("UNIT_PRICE",j));
					newOrderParm.addData("SUM_PRICE", tableParm.getDouble("ORDER_NUM",i)*fineOrderParm.getDouble("UNIT_PRICE",j));
					newOrderParm.addData("ORDER_NUM", tableParm.getInt("ORDER_NUM",i));
					
					BigDecimal a = new BigDecimal(tableParm.getDouble("RETAIL_PRICE", i));
					BigDecimal b = new BigDecimal(fineOrderParm.getDouble("UNIT_PRICE", j));
					BigDecimal c = new BigDecimal(tableParm.getDouble("NEW_UNIT_PRICE",i));					
					BigDecimal tmp = b.multiply(a);
					BigDecimal r = tmp.divide(c,2,BigDecimal.ROUND_HALF_UP);
					sumPrice = sumPrice.add(r);					
					price = r.doubleValue();					
					newOrderParm.addData("RETAIL_PRICE", price);
					newOrderParm.addData("OPT_TERM", Operator.getIP());
					newOrderParm.addData("OPT_USER", Operator.getID());
					newOrderParm.addData("UN_NUM_FLG", tableParm.getValue("UN_NUM_FLG", i));
				}
				
				int row = newOrderParm.getCount("ORDER_CODE")-1;
				BigDecimal pr = new BigDecimal(price);				
				retailPrice = retailPrice.add(pr);
				sumPrice = sumPrice.negate();
				retailPrice = retailPrice.add(sumPrice);
				newOrderParm.setData("RETAIL_PRICE", row , retailPrice.doubleValue());
				
			}else{
				newOrderParm.addData("SETMAIN_FLG", "N");
			}
			
			
			
		}
		table.setParmValue(tableParm);

	}
	
	public void onQuery(){
		String orderCode = this.getValueString(TAG_ORDER_CODE);
		if(orderCode.length() == 0){
			this.messageBox("请填写查询条件");
			return;
		}
		String sql = "SELECT A.TRADE_NO,A.MR_NO,B.PAT_NAME,A.VERSION_NUMBER,A.PACKAGE_CODE," +
				" C.PACKAGE_DESC,A.SECTION_CODE,A.SECTION_DESC," +
				" A.ID,A.ORDER_CODE,A.ORDER_DESC ,A.UNIT_CODE,A.UNIT_PRICE,A.ORDER_NUM," +
				" A.UNIT_PRICE*A.ORDER_NUM SUM_PRICE, '' NEW_SUM_PRICE," +
				" A.RETAIL_PRICE,A.SETMAIN_FLG,A.HIDE_FLG,A.ORDERSET_GROUP_NO," +
				" '' NEW_ORDER_CODE, '' NEW_ORDER_DESC,'' NEW_UNIT_CODE,'' NEW_UNIT_PRICE," +
				" A.OPT_USER,A.OPT_DATE,A.OPT_TERM,'N' ORDERSET_FLG,A.ORDERSET_CODE,A.UN_NUM_FLG" +
				" FROM MEM_PAT_PACKAGE_SECTION_D A,SYS_PATINFO B,MEM_PACKAGE C" +
				" WHERE A.PACKAGE_CODE = C.PACKAGE_CODE AND A.MR_NO = B.MR_NO AND A.USED_FLG='0' " +
				" AND A.REST_TRADE_NO IS NULL " +
				" AND A.ORDER_CODE='"+orderCode+"'" +
				" ORDER BY A.PACKAGE_CODE,A.SECTION_CODE,A.ID";
		
//		System.out.println(sql);
		
		tableParm = new TParm(TJDODBTool.getInstance().select(sql));
		if(tableParm.getCount() < 0){
			this.messageBox("没有要查询的数据");
			return;
		}
		table.setParmValue(tableParm);
	}
	
	/**
     * 右击MENU弹出事件
     * @param tableName
     */
    public void showPopMenu() {
    	table.acceptText();
    	int row = table.getSelectedRow();
    	if(tableParm.getBoolean("ORDERSET_FLG", row)){
    		table.setPopupMenuSyntax("显示新集合医嘱细项,onOrderSetShow");
    	}else{
    		table.setPopupMenuSyntax("");
    	}
    	
    }
    
    /**
     * 修改医嘱细相，套餐细相TABLE右击事件，调出细相列表，允许修改细相信息
     */
    public void onOrderSetShow() {
    	TParm parm = new TParm();
    	TParm fineParm = new TParm();
    	table.acceptText();
    	int row = table.getSelectedRow();
    	for (int i = 0; i < newOrderParm.getCount("ORDER_CODE"); i++) {
			if(newOrderParm.getValue("ORDERSET_CODE", i).equals(tableParm.getValue("NEW_ORDER_CODE", row)) &&
					newOrderParm.getValue("TRADE_NO", i).equals(tableParm.getValue("TRADE_NO", row)) &&
					newOrderParm.getValue("PACKAGE_CODE", i).equals(tableParm.getValue("PACKAGE_CODE", row)) &&
					newOrderParm.getValue("SECTION_CODE", i).equals(tableParm.getValue("SECTION_CODE", row)) &&
					newOrderParm.getValue("ORDERSET_GROUP_NO", i).equals(tableParm.getValue("ORDERSET_GROUP_NO", row))){
				if(newOrderParm.getBoolean("HIDE_FLG", i)){
					fineParm.addData("TRADE_NO", newOrderParm.getValue("TRADE_NO", i));
					fineParm.addData("ORDER_DESC", newOrderParm.getValue("ORDER_DESC", i));
					fineParm.addData("ORDER_CODE", newOrderParm.getValue("ORDER_CODE", i));
					fineParm.addData("ORDERSET_CODE", newOrderParm.getValue("ORDERSET_CODE", i));
					fineParm.addData("ORDERSET_GROUP_NO", newOrderParm.getValue("ORDERSET_GROUP_NO", i));
					fineParm.addData("PACKAGE_CODE", newOrderParm.getValue("PACKAGE_CODE", i));
					fineParm.addData("SECTION_CODE", newOrderParm.getValue("SECTION_CODE", i));
					fineParm.addData("UNIT_CODE", newOrderParm.getValue("UNIT_CODE", i));
					fineParm.addData("UNIT_PRICE", newOrderParm.getValue("UNIT_PRICE", i));
					fineParm.addData("RETAIL_PRICE", newOrderParm.getValue("RETAIL_PRICE", i));
				}else{
					parm.setData("RETAIL_PRICE_SUM", newOrderParm.getValue("RETAIL_PRICE", i));
				}
				
			} 
		}
    	parm.setData("fineParm", fineParm.getData());
//    	System.out.println("parm==="+parm);
    	TParm reParm =  (TParm) this.openDialog("%ROOT%\\config\\mem\\MEMComOrderSetShow.x", parm);
    	if(reParm != null){
    		for (int i = 0; i < reParm.getCount("ORDER_CODE"); i++) {
				for (int j = 0; j < newOrderParm.getCount("ORDER_CODE"); j++) {
					if(reParm.getValue("ORDER_CODE", i).equals(newOrderParm.getValue("ORDER_CODE", j)) &&
							reParm.getValue("ORDER_CODE", i).equals(newOrderParm.getValue("ORDER_CODE", j)) &&
							reParm.getValue("TRADE_NO", i).equals(newOrderParm.getValue("TRADE_NO", j)) &&
							reParm.getValue("ORDERSET_CODE", i).equals(newOrderParm.getValue("ORDERSET_CODE", j)) &&
							reParm.getValue("SECTION_CODE", i).equals(newOrderParm.getValue("SECTION_CODE", j)) &&
							reParm.getValue("ORDERSET_GROUP_NO", i).equals(newOrderParm.getValue("ORDERSET_GROUP_NO", j)) &&
							reParm.getValue("PACKAGE_CODE", i).equals(newOrderParm.getValue("PACKAGE_CODE", j))){
						newOrderParm.setData("RETAIL_PRICE", j, reParm.getValue("RETAIL_PRICE", i));
					}
				}
			}
    	}
    }
    
    public void onClear(){
    	table.removeRowAll();
    	this.clearText(TAG_NEW_ORDER_CODE);
    	this.clearText(TAG_NEW_ORDER_DESC);
    	this.clearText(TAG_ORDER_CODE);
    	this.clearText(TAG_ORDER_DESC);
    }
    
    public void onUpdate(){    	
    	
    	for (int i = 0; i < newOrderParm.getCount("ORDER_CODE") ; i++) {
			if(newOrderParm.getBoolean("HIDE_FLG",i)){
				String id = SystemTool.getInstance().getNo("ALL", "EKT", "MEM_NO","MEM_NO");
				newOrderParm.setData("ID", i, id);
			}
		}
    	TParm parm = new TParm();
    	parm.setData("newOrderParm", newOrderParm.getData());
    	parm.setData("tableParm", tableParm.getData());
    	TParm result = TIOM_AppServer.executeAction("action.mem.MEMPackageSectionAction",
				"onSaveComorderRelpace", parm);
    	if(result.getErrCode()<0){
			this.messageBox("更新失败！");
			
		}else{
			this.messageBox("更新成功！");
		}
    	
    }
	
}
