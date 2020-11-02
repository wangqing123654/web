package com.javahis.ui.ekt;

import java.sql.Timestamp;

import jdo.ekt.EKTTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.control.TControl;

/**
 * <p>Title: ���ｨ��Ԥ���������</p>
 *
 * <p>Description: ���ｨ��Ԥ��������� </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author WangLong 20120920 
 * @version 1.0
 */
public class EKTCCBTradeQueryControl extends TControl {
	
	/**
     * ��ʼ������
     */
    public void onInit() {
    	setValue("OPT_USER", Operator.getID());
    	Timestamp today = SystemTool.getInstance().getDate();
    	String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", today);
    }
    
    /**
     * ��ѯ����
     */
	public void onQuery(){
		String startDate = this.getValueString("START_DATE");
		String endDate = this.getValueString("END_DATE");
		String optUser = this.getValue("OPT_USER")+"";
		TParm parm = new TParm();
		parm.setData("START_DATE", startDate);
		parm.setData("END_DATE", endDate);
		parm.setData("OPT_USER", optUser);
		TParm ccbTrade = EKTTool.getInstance().queryCCBTrade(parm);
		if (ccbTrade.getErrCode() < 0) {
			messageBox(ccbTrade.getErrText());
			return;
		}
		if (ccbTrade.getCount() <= 0) {
			messageBox("E0008");
			this.callFunction("UI|TABLE|setParmValue", new TParm());
			return;
		}
		this.callFunction("UI|TABLE|setParmValue", new TParm());
		this.callFunction("UI|TABLE|setParmValue", ccbTrade);
	}
	
	/**
	 * ��ӡ
	 */
	public void onPrint(){
		TParm data = (TParm) this.callFunction("UI|TABLE|getShowParmValue");
		if(data.getCount("USER_NAME")==0) return;
		double income = 0.0;
		double expense = 0.0;
		for (int i = 0; i < data.getCount("USER_NAME"); i++) {
			if (data.getDouble("AMT", i) >= 0) {
				income += data.getDouble("AMT", i);
			} else
				expense += data.getDouble("AMT", i);
		}
		data.addData("OPT_DATE","");
		data.addData("USER_NAME","");
		data.addData("STATE","");
		data.addData("BUSINESS_NO","");
		data.addData("MR_NO","");
		data.addData("CARD_NO","");
		data.addData("PAT_NAME","");
		data.addData("AMT","");
		data.addData("OTHER","");
		data.addData("OPT_DATE","");
		data.addData("USER_NAME","");
		data.addData("STATE","�շѺϼ�:");
		data.addData("BUSINESS_NO","");
		data.addData("MR_NO","");
		data.addData("CARD_NO","");
		data.addData("PAT_NAME","");
		data.addData("AMT"," "+income+"0");
		data.addData("OTHER","");
		data.addData("OPT_DATE","");
		data.addData("USER_NAME","");
		data.addData("STATE","�˷Ѻϼ�:");
		data.addData("BUSINESS_NO","");
		data.addData("MR_NO","");
		data.addData("CARD_NO","");
		data.addData("PAT_NAME","");
		data.addData("AMT",""+expense+"0");
		data.addData("OTHER","");
		data.addData("OPT_DATE","");
		data.addData("USER_NAME","");
		data.addData("STATE","�ܼ�:");
		data.addData("BUSINESS_NO","");
		data.addData("MR_NO","");
		data.addData("CARD_NO","");
		data.addData("PAT_NAME","");
		data.addData("AMT",""+(income+expense)+"0");
		data.addData("OTHER","");
		data.setCount(data.getCount("OPT_USER"));
		data.addData("SYSTEM", "COLUMNS", "OPT_DATE");
		data.addData("SYSTEM", "COLUMNS", "USER_NAME");
		data.addData("SYSTEM", "COLUMNS", "STATE");
		data.addData("SYSTEM", "COLUMNS", "BUSINESS_NO");
		data.addData("SYSTEM", "COLUMNS", "MR_NO");
		data.addData("SYSTEM", "COLUMNS", "CARD_NO");
		data.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		data.addData("SYSTEM", "COLUMNS", "AMT");
		data.addData("SYSTEM", "COLUMNS", "OTHER");
		data.setCount(data.getCount("AMT"));
		TParm tableParm = new TParm();
		String date = SystemTool.getInstance().getDate().toString();
		tableParm.setData("TITLE1", "TEXT", Operator.getHospitalCHNFullName());
		tableParm.setData("PRINTDATE", "TEXT", "��ӡ����: "+date.substring(0, 4)+
    			"/"+date.substring(5, 7)+"/"+date.substring(8, 10));
		tableParm.setData("TABLE", data.getData());//�ѱ��������ӽ�Ҫ��ӡ��parm
		this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTCCBTradeQuery.jhw",tableParm);
	}
	
	/**
	 * ���
	 */
	public void onClear(){
		clearValue("OPT_USER");
		callFunction("UI|TABLE|setParmValue", new TParm());
	}
}
