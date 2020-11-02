package com.javahis.ui.dev;

import java.sql.Timestamp;
import jdo.dev.DevRFIDTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;


/**
 * <p>Title: RFID��־��ѯ</p>
 *
 * <p>Description: RFID��־��ѯ</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BLUECORE</p>
 *
 * @author WangLong 20121204
 * @version 1.0
 */
public class DevRFIDLogControl extends TControl {
	
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String dateStr = StringTool.getString(sysDate,"yyyyMMdd");
        this.setValue("START_DATE",StringTool.getTimestamp(dateStr+" 00:00:00","yyyyMMdd hh:mm:ss"));
        this.setValue("END_DATE",sysDate);
        //=========================================================
//        TParm parm=new TParm();
//		parm.setData("RECORD_DATE", new Timestamp(new java.util.Date().getTime()));
//		parm.setData("IP_ADDRESS", "123.123.123.123");
//		parm.setData("RFID_MESSAGE", "����");
//		parm.setData("OPT_USER", Operator.getID());
//		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
//		parm.setData("OPT_TERM", Operator.getIP());
//    	TParm result = DevRFIDTool.getInstance().insertRFIDLog(parm);
    }

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm parm = new TParm();
		Timestamp startDate = (Timestamp)this.getValue("START_DATE");// ��ʼʱ��
		Timestamp endDate = (Timestamp)this.getValue("END_DATE");// ��������
		parm.setData("START_DATE", startDate);
		parm.setData("END_DATE", endDate);
		if(!this.getValueString("IP_ADDRESS").trim().equals("")){
			if (StringTool.isIP(this.getValueString("IP_ADDRESS"))) {
				parm.setData("IP_ADDRESS", this.getValueString("IP_ADDRESS"));// ip��ַ
			} else {
				messageBox("IP��ַ��ʽ����");
				return;
			}
		}
		TParm result = DevRFIDTool.getInstance().selectRFIDLogs(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + "" + result.getErrText());
		}
		this.callFunction("UI|TABLE|setParmValue", new TParm());
		if (result.getCount() <= 0) {
			messageBox("E0008");// ��������
			return;
		}
		this.callFunction("UI|TABLE|setParmValue", result);
	}
 

    /**
     * ɾ��
     */
	public void onDelete() {
		TTable table = (TTable) this.getComponent("TABLE");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			messageBox("û��ѡ���κμ�¼��");
			return;
		}
		table.acceptText();
		TParm tableParm = table.getParmValue();
		TParm showParm = table.getShowParmValue();
		TParm parm = new TParm();
		for (int i = 0; i < showParm.getCount(); i++) {
			if (showParm.getValue("FLG", i).equals("Y")) {
				parm.addData("ROW", i);// �к�
				parm.addData("RFID_LOG_CODE", showParm.getValue("RFID_LOG_CODE", i));
			}
		}
		if (parm.getCount("RFID_LOG_CODE") < 1) {
			parm.addData("ROW", selRow);// �к�
			parm.addData("RFID_LOG_CODE", showParm.getValue("RFID_LOG_CODE", selRow));
		}
		if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 2) == 0) {
			boolean successFlg = true;
			TParm result = new TParm();
			String logCode = "";
			for (int i = 0; i < parm.getCount("RFID_LOG_CODE"); i++) {
				logCode = parm.getValue("RFID_LOG_CODE", i);
				result = DevRFIDTool.getInstance().deleteRFIDLog(logCode);
				if (result.getErrCode() < 0) {
					successFlg = false;
				}else{
					for (int j = 0; j < tableParm.getCount();j++) {
						if(tableParm.getValue("RFID_LOG_CODE", j).equals(logCode)){
							tableParm.removeRow(j);
							break;
						}
					}
				}
			}
			table.clearSelection();
			table.setParmValue(tableParm);
			if (successFlg == true) {
				this.messageBox("P0003");// ɾ���ɹ�
			}
		}
	}
    

	/**
	 * ȫѡ�¼�
	 */
	public void onAllChoice() {
		TTable table = (TTable) this.getComponent("TABLE");
		String select = getValueString("ALL_FLG");
		boolean flg = false;
		if (select.equals("Y")) {
			flg = true;
		}
		TParm parm = table.getParmValue();
		if (parm.getCount() > 0) {
			for (int i = 0; i < parm.getCount("FLG"); i++) {
				parm.setData("FLG", i, flg);
			}
			table.setParmValue(parm);
		}
	}
	
    /**
     * ���
     */
	public void onClear() {
		this.clearValue("IP_ADDRESS");
		callFunction("UI|TABLE|setParmValue", new TParm());
	}
    

}
