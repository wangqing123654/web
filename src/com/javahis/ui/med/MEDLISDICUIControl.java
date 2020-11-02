package com.javahis.ui.med;

import java.util.Date;
import jdo.med.MEDLISDICUITool;
import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
/**
 * <p> Title:��Ŀ�������������Ӧ </p>
 * 
 * <p> Description: ��Ŀ�������������Ӧ </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 *@author 2014.03.10
 * @version 1.0
 */
public class MEDLISDICUIControl extends TControl {
	
	TTable table;
	public void onInit() {
		table=this.getTable("TABLE");
		this.callFunction("UI|TABLE|addEventListener", "TABLE->"
        		+ TTableEvent.CLICKED, this, "onTABLEClicked");
		onQueryAll();
		
	}

	/**
	 * �����¼�
	 */
	
 public void onTABLEClicked(int row) {
		 if(row<0){
			 return;
		 }
		 TParm parm=table.getParmValue().getRow(row);
		 callFunction("UI|MAP_ID|setEnabled", false);//
		 this.setValue("MAP_ID", parm.getValue("MAP_ID"));
		 this.setValue("MAP_DESC", parm.getValue("MAP_DESC"));
		 this.setValue("SEQ", parm.getValue("SEQ"));
		 this.setValue("LIS_ID", parm.getValue("LIS_ID"));
		 this.setValue("LIS_DESC", parm.getValue("LIS_DESC"));
		 this.setValue("TYPE", parm.getValue("TYPE"));
		 this.setValue("MAP_TYPE", parm.getValue("MAP_TYPE"));
		 this.setValue("DESCRIPTION", parm.getValue("DESCRIPTION"));
		 this.setValue("PY1", parm.getValue("PY1"));
		 
		 
	    }
	 
	 
	/**
	 * ����
	 */
	public void onSave() {
		if("".equals(this.getValueString("MAP_ID"))){
			this.messageBox("��Ŀ���벻��Ϊ��");
			return;
		}
		
		if("".equals(this.getValueString("MAP_DESC"))){
			this.messageBox("��Ŀ���Ʋ���Ϊ��");
			return;
		}
		if("".equals(this.getValueString("PY1"))){
			this.messageBox("ƴ������Ϊ��");
			return;
		}
			String upmess="";
			String errmess="";
			TParm parm=new TParm();
			TParm result=new TParm();
			parm.setData("MAP_ID", this.getValueString("MAP_ID"));
			parm.setData("MAP_DESC", this.getValueString("MAP_DESC"));
			parm.setData("SEQ", this.getValueString("SEQ"));
			parm.setData("LIS_ID", this.getValueString("LIS_ID"));
			parm.setData("LIS_DESC", this.getValueString("LIS_DESC"));
			parm.setData("TYPE", this.getValueString("TYPE"));
			parm.setData("MAP_TYPE", this.getValueString("MAP_TYPE"));
			parm.setData("PY1", this.getValueString("PY1"));
			parm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
			parm.setData("OPT_TERM", Operator.getID());
			result=MEDLISDICUITool.getInstance().selectData(parm);
			if(result.getCount()>0){//��������޸�
				result=MEDLISDICUITool.getInstance().updateData(parm);
				 upmess="�޸ĳɹ�";
				 errmess="�޸�ʧ��";
			}else{//���������
				result=MEDLISDICUITool.getInstance().insertData(parm);
				 upmess="����ɹ�";
				 errmess="����ʧ��";
			}
			
		
		if (result.getErrCode() < 0) {
			this.messageBox(errmess);
			onQuery();
			return;
		}
		this.messageBox(upmess);
		onQueryAll();
	}
	
	/**
	 * ���ƻس��¼�
	 */
	public void onUserNameAction() {
		String userName = getValueString("MAP_DESC");
		String py = TMessage.getPy(userName);
		setValue("PY1", py);
		((TTextField) getComponent("PY1")).grabFocus();
	}
	/**
	 * ɾ��
	 */
	public void onDelete() {
		TParm parm=new TParm();
		TParm parma=new TParm();
		TParm result=new TParm();
			 parm=table.getParmValue().getRow(table.getSelectedRow());
			 parma.setData("MAP_ID",parm.getValue("MAP_ID"));
			 result=MEDLISDICUITool.getInstance().deleteData(parma);
			
			if (result.getErrCode() < 0) {
				this.messageBox("ɾ��ʧ��");
				onQuery();
				return;
			}
			this.messageBox("ɾ���ɹ�");
			table.removeRow(table.getSelectedRow());
			onClear();
		    onQueryAll();
	}

	

	/**
	 * ��ѯ
	 */
	public void onQueryAll() {
		
		TParm parm = MEDLISDICUITool.getInstance().selectDataAll();
		this.table.setParmValue(parm);
	}
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		table.removeRowAll();
		TParm parm =new TParm();
			parm.setData("MAP_ID",this.getValueString("MAP_ID"));
			parm.setData("MAP_DESC",this.getValueString("MAP_DESC"));
			
		TParm result = MEDLISDICUITool.getInstance().selectDataid(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ����");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("û�в�ѯ����");
			return;
		}
		this.table.setParmValue(result);
	}

	/**
	 * ���
	 */
	public void onClear() {
		this.clearValue("MAP_ID;MAP_DESC;SEQ;LIS_ID;LIS_DESC;TYPE;MAP_TYPE;DESCRIPTION;PY1");
		table.removeRowAll();
		 callFunction("UI|MAP_ID|setEnabled", true);//
		 onQueryAll();
	}

	/**
	 * �õ�TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	

}
