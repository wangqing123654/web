package jdo.odo;

import java.sql.Timestamp;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

/**
*
* <p>Title: 医嘱存储对象</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author ehui 2009.2.11
* @version 1.0
*/
public class OpdOrderHistory extends StoreBase {
	private static final String INIT="SELECT * FROM OPD_ORDER_HISTORY WHERE RX_NO='#'";
	/**
	 * 查询
	 */
	public boolean onQuery(){
		this.setSQL(INIT);
		this.retrieve();
		return true;
	}
	/**
	 * 根据给入的order对象插入
	 * @param order
	 * @return
	 */
	public boolean insert(TParm parm){
		if(parm==null){
			return false;
		}
		if(parm.getCount()<0){
			return false;
		}
		int count=parm.getCount();
		String[] names=parm.getNames();
		int countName=names.length;
		Timestamp now=this.getDBTime();
		//System.out.println("insert.count="+count);
		for(int i=0;i<count;i++){
			int row=this.insertRow();
			for(int j=0;j<countName;j++){
				this.setItem(row, names[j], parm.getValue(names[j],i));
			}
			this.setItem(row, "DC_ORDER_DATE", StringTool.getString(now,"yyyyMMddHHmmss"));
//			Timestamp orderDate=TypeTool.getTimestamp( this.getItemData(row, "ORDER_DATE"));
//			orderDate=StringTool.getTimestamp(StringTool.getString(orderDate, "yyyyMMddHHmmss"),"yyyy/MM/dd HH:mm:ss");
			String odStr = this.getItemData(row, "ORDER_DATE")+"";
			String bdStr = this.getItemData(row, "BILL_DATE")+"";
			
//			Timestamp billDate=TypeTool.getTimestamp( this.getItemData(row, "BILL_DATE"));
//			billDate=StringTool.getTimestamp(StringTool.getString(billDate, "yyyyMMddHHmmss"),"yyyy/MM/dd HH:mm:ss");
			
			this.setItem(row, "DC_DR_CODE", Operator.getID());
			if(odStr.length() > 0){
				this.setItem(row, "ORDER_DATE", stringToTimestamp(odStr));
			}else{
				this.setItem(row, "ORDER_DATE", "");
			}
			if(bdStr.length() > 0){
				this.setItem(row, "BILL_DATE", stringToTimestamp(bdStr));
			}else{
				this.setItem(row, "BILL_DATE", "");
			}
			
			//add by huangtt 20161017  start 药品 时间
			
			String phaCheckDate = this.getItemString(row, "PHA_CHECK_DATE");
			if(phaCheckDate.length() > 0){
				this.setItem(row, "PHA_CHECK_DATE", stringToTimestamp(phaCheckDate));
			}else{
				this.setItem(row, "PHA_CHECK_DATE", "");
			}
			String phaDosageDate = this.getItemString(row, "PHA_DOSAGE_DATE");
			if(phaDosageDate.length() > 0){
				this.setItem(row, "PHA_DOSAGE_DATE", stringToTimestamp(phaDosageDate));
			}else{
				this.setItem(row, "PHA_DOSAGE_DATE", "");
			}
			String phaDispenseDate = this.getItemString(row, "PHA_DISPENSE_DATE");
			if(phaDispenseDate.length() > 0){
				this.setItem(row, "PHA_DISPENSE_DATE", stringToTimestamp(phaDispenseDate));
			}else{
				this.setItem(row, "PHA_DISPENSE_DATE", "");
			}
			String phaRetnDate = this.getItemString(row, "PHA_RETN_DATE");
			if(phaRetnDate.length() > 0){
				this.setItem(row, "PHA_RETN_DATE", stringToTimestamp(phaRetnDate));
			}else{
				this.setItem(row, "PHA_RETN_DATE", "");
			}
			
			//add by huangtt 20161017  end 药品 时间
			
			this.setItem(row, "DC_DEPT_CODE", Operator.getDept());
			this.setItem(row, "OPT_USER", Operator.getID());
			this.setItem(row, "OPT_TERM", Operator.getIP());
			this.setItem(row, "OPT_DATE", now);
			this.setActive(row,true);
		}
		return true;
	}
	
	private Timestamp stringToTimestamp(String tsStr){
	    Timestamp ts = new Timestamp(System.currentTimeMillis());  
        try {  
        	tsStr = tsStr.substring(0, 19);
            ts = Timestamp.valueOf(tsStr);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        return ts;
	}
}
