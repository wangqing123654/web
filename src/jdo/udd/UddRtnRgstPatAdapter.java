package jdo.udd;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.ui.TTable;

public class UddRtnRgstPatAdapter extends TObserverAdapter{
	int nowRow;
	TTable table;
	public UddRtnRgstPatAdapter(TTable table){
		this.table=table;
	}
	/**
	 * ����SQL��û�е��е���TABLE���е��е�ֵ�ı��¼�
	 */
	public boolean setOtherColumnValue(TDS ds,TParm parm,int row,String column,Object value){
		//System.out.println("row="+row);
		if("EXEC".equalsIgnoreCase(column)){
			//if(nowRow != row)
			//{
			//	ds.addOtherShowRowList(nowRow);
				nowRow=row;
			//}
			//���ݵ��谡��parmȡ����case
			//TDSObject jdoObj = (TDSObject)ds.getParent();
			//jdoObj.setAttribute("CASE_NO", "000011");
			TDS tds=(TDS)table.getDataStore();
			String caseNo=parm.getValue("CASE_NO",row);
			String orderNo=parm.getValue("ORDER_NO",row);
//			tds.setSQL("SELECT * FROM ODI_DSPNM WHERE CASE_NO")
			int rowTable=table.addRow();
			return true;
		}
		return false;
	}
	public Object getOtherColumnValue(TDS ds,TParm parm, int row,String column){
		//System.out.println("getOtherColumnValue " + column + " " + row);
		if("EXEC".equalsIgnoreCase(column)){
			//System.out.println(row + " " + nowRow);
			return row==nowRow;
		}
		return null;
	}
}
