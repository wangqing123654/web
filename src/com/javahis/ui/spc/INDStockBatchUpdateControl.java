package com.javahis.ui.spc;

import java.sql.Timestamp;

import jdo.spc.IndStockBatchNewTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;

public class INDStockBatchUpdateControl extends TControl {
	
	public INDStockBatchUpdateControl() {
	    	
	}
	 
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		// ��ʼ����ѯ����
		this.setValue("DATE", date.toString().substring(0, 10)
				.replace('-', '/'));
	}
	
    /**
     * �����ս���ʱ���
     */
    public void onSave() {
        String org_code = this.getValueString("ORG_CODE");
        if ("".equals(org_code)) {
            this.messageBox("��ѡ�����ҩ��");
            return;
        }
        String date = this.getValueString("DATE");
        date = date.substring(0, 4) + date.substring(5, 7) +
            date.substring(8, 10);
        //��ѯ����ս���� ����
        Timestamp datetime = StringTool.getTimestamp(date, "yyyyMMdd");
        Timestamp lastdate = StringTool.rollDate(datetime, -1); 
        String last_date = StringTool.getString(lastdate, "yyyyMMdd");
        
        String resultString = IndStockBatchNewTool.getInstance().getLastTranDateByOrgCode(org_code);
        if(!"".equals(resultString) &&  !"-1".equals(resultString)){//-1��ʾû���ս���Ϣ
        	int lastDateInt = Integer.parseInt(last_date);
        	int resultInt = Integer.parseInt(resultString);
        	resultInt += 1;
        	if(resultInt < lastDateInt){
        		this.messageBox("�����ս�"+resultInt);
        		return ;
        	}   
        }			
        TParm parm = new TParm();
        parm.setData("ORG_CODE",org_code);
        parm.setData("DATE",date);
        parm.setData("LAST_DATE",last_date);
        TParm result = TIOM_AppServer.executeAction(
            "action.spc.INDStockBatchUpdateAction", "onIndDDStockBatch", parm);
        // �����ж�
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
    }
	
}
