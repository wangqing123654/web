package com.javahis.ui.clp;

import jdo.clp.CLPSingleDiseTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p> Title: ���ε����� ѡ�񴰿� </p>
 * 
 * <p> Description: ���ε����� ѡ�񴰿�  </p>
 * 
 * <p> Copyright: Copyright (c) 2012 </p>
 * 
 * <p> Company: BlueCore </p>
 * 
 * @author WangLong 20120926
 * @version 1.0
 */
public class CLPPatInfoUIControl extends TControl {
	private static String TABLE = "PAT_TABLE";
	private TTable patTable;
	private String mrNo;
	private TParm returnParm;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		Object obj = this.getParameter();
		TParm parm = new TParm();
		if (obj != null) {
			parm = (TParm) obj;
			mrNo = parm.getValue("MR_NO");
			if(mrNo.trim().equals("")){
				messageBox("E0024");//��ʼ������ʧ��
				return;
			}
		}else{
			messageBox("E0024");
			return;
		}
		TParm result = CLPSingleDiseTool.getInstance().queryEMRHistoryForMerge(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("��ʼ�����β�����Ϣ����" + result.getErrText());
		} else {
			this.callFunction("UI|" + TABLE + "|setParmValue", result);
		}
		callFunction("UI|" + TABLE + "|addEventListener", TABLE + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");// ע��Table����¼�
		patTable = (TTable) this.getComponent(TABLE);
	}

	/**
	 * TABLE�����¼�
	 * @param row int
	 */
	public void onTableClicked(int row) {
		returnParm = patTable.getParmValue().getRow(row);
	}

	/**
	 * �ϲ�
	 */
	public void onMerge() {
		if (patTable.getSelectedRow() < 0) {
			messageBox("��ѡ��һ����¼");
			return;
		}
		if(returnParm.getBoolean("MERGE_FLG")){
		    messageBox("�����ظ��ϲ�");
		    return;
		}
		this.setReturnValue(returnParm);
		this.closeWindow();
	}

	/**
	 * ȡ��
	 */
	public void onCancel() {
		this.closeWindow();
	}

}
