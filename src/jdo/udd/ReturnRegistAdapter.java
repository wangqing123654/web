package jdo.udd;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TObserverAdapter;

public class ReturnRegistAdapter extends TObserverAdapter{
	/**
	 * ����SQL��û�е��е���TABLE���е��е�ֵ�ı��¼�
	 */
	public boolean setOtherColumnValue(TDS ds,TParm parm,int row,String column,Object value){
		if("EXEC".equalsIgnoreCase(column)){
			
		}
		return false;
	}
	public int setItem(int row,String column,Object value){
		TDS d=getDS();
		setItem(row, column, value);
		d.setActive(row,true);
		d.setItem(row, column, value);
		return 0;
	}
	public int insertRow(){
		TDS d=getDS();
		int row=d.insertRow();
		d.setActive(row,true);
		
		return row;
	}
	
}
