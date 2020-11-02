package com.javahis.ui.hrm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
/**
 * <p> Title: �������������ÿ����� </p>
 * 
 * <p> Description: �������������ÿ�����  </p>
 * 
 * <p> Copyright: javahis 20100504 </p>
 * 
 * <p> Company:JavaHis </p>
 * 
 * @author ehui
 * @version 1.0
 */
public class HRMReportListControl extends TControl {
	//table
	private TTable table;
	//data
	private TDS tds;
	//SQL
	private final static String SQL="SELECT * FROM HRM_REPORTLIST ORDER BY LIST_CODE,DETAIL_SEQ";
	//��������Ƿ��ظ�
	private final static String CHECK_RESIST="SELECT COUNT(LIST_CODE) FROM HRM_REPORTLIST WHERE LIST_CODE='#' AND DETAIL_SEQ=#";
	 /**
     * ��ʼ��
     */
    public void onInit() {
    	onInitComponent();
    	onInitData();
    }
    /**
     * ��ʼ���ؼ�
     */
    public void onInitComponent(){
    	table=(TTable)this.getComponent("TABLE");
		//�ײ�����ֵ�ı��¼�
    	table.addEventListener("TABLE->"+TTableEvent.CHANGE_VALUE, this,
		"onValueChanged");
    }
    /**
     * ��ʼ������
     */
    public void onInitData(){
    	tds=new TDS();
    	tds.setSQL(SQL);
    	tds.retrieve();
    	tds.insertRow();
    	table.setDataStore(tds);
    	
    }
    /**
     * ����¼�
     */
    public void onClear(){
    	table.setDSValue();
    }
    /**
     * �����¼�
     */
    public void onSave(){
    	TParm result=new TParm(TJDODBTool.getInstance().update(tds.getUpdateSQL()));
    	if(result.getErrCode()!=0){
    		this.messageBox("E0002");
    		return;
    	}
    	this.messageBox("P0002");
    	tds.resetModify();
    }
    /**
     * ɾ���¼�
     */
    public void onDelete(){
    	if(table==null){
    		return;
    	}
    	int row=table.getSelectedRow();
    	if(row<0){
    		return;
    	}
    	tds.deleteRow(row);
    	onSave();
    }
    /**
     * ��ѯ�¼�
     */
    public void onQuery(){
    	
    }
    /**
	 * ֵ�ı��¼�
	 * @param tNode
	 * @return
	 */
	public boolean onValueChanged(TTableNode tNode){
		int row=tNode.getRow();
		int column=tNode.getColumn();
		String colName=tNode.getTable().getParmMap(column);
		tNode.getTable().acceptText();
		if("LIST_DESC".equalsIgnoreCase(colName)){
			String py1=StringUtil.getInstance().onCode(tNode.getValue()+"");
			table.getDataStore().setItem(row, "LIST_PY1", py1);
			table.setDSValue();
			table.getTable().grabFocus();
			table.setSelectedRow(row);
			table.setSelectedColumn(column+1);
			return false;
		}
		if("DETAIL_SEQ".equalsIgnoreCase(colName)){
			int seq=StringTool.getInt(tNode.getValue()+"");
			String listCode=tds.getItemString(row, "LIST_CODE");
			String sql=CHECK_RESIST.replaceFirst("#", listCode).replaceFirst("#", seq+"");
			TParm result=new TParm(TJDODBTool.getInstance().select(sql));
			if(result.getErrCode()!=0){
				this.messageBox_("�Ѵ�����ͬ�Ķ��д����ϸ�����");
				return true;
			}
		}
		if("DETAIL_DESC".equalsIgnoreCase(colName)){
			if(row==tds.rowCount()-1&&!StringUtil.isNullString(tds.getItemString(tds.rowCount()-1, "LIST_CODE"))){
				this.messageBox_("herer");
				tds.insertRow();
				table.setDSValue();
			}
			return false;
		}
		return false;
	}
}
