package com.javahis.ui.ins;

import jdo.ins.INSSuffererReportTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import com.javahis.util.ExportExcelUtil;

/**
 * 
 * <p>
 * Title:ҽ�������걨��ѯ
 * </p>
 * 
 * <p>
 * Description:ҽ�������걨��ѯ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author xueyf 2012.02.06
 * @version 1.0
 */
public class INSSuffererReportControl extends TControl {
	int selectRow = -1;

	public void onInit() {
		super.onInit();
		callFunction("UI|TABLETYPE|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTABLEClicked");
		// onQuery();
	}

	/**
	 * ���Ӷ�TABLE�ļ���
	 * 
	 * @param row
	 *            int
	 */
	public void onTABLEClicked(int row) {

		if (row < 0)
			return;
		TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
		// setValueForParm("USERID;USER_NAME;ID_NO;DR_QUALIFY_CODE_NEW", data,
		// row);
		// selectRow = row;
	}

	/**
	 * ҽ�������걨��ѯ
	 */
	public void onQuery() {
		TParm parm = new TParm();
		TParm data = new TParm();
		// String USERID = (String) ((TTextFormat) getComponent("USERID"))
		// .getValue();
		parm.setData("region_code", Operator.getRegion());
		parm.setData("searchStatus", getText("searchStatus"));
		parm.setData("endemicArea", getValue("endemicArea"));
		parm.setData("startDate", getText("startDate"));
		parm.setData("endDate", getText("endDate"));
		parm.setData("total", getText("total"));
		if (parm.getValue("searchStatus") == null
				|| parm.getValue("searchStatus").equals("")) {
			this.messageBox("��ѯ״̬����Ϊ�ա�");
			return;
		}
		data = INSSuffererReportTool.getInstance().selectdata(parm);
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		((TTextField) getComponent("total"))
				.setValue((data.getCount() > 0 ? data.getCount() : 0) + "");
		this.callFunction("UI|TABLE|setParmValue", data);
	}
    /**
     *  ���
     */
    public void onExport(){
    	TTable table=(TTable)getComponent("TABLE");
	    if(table.getRowCount()<1){
	    	this.messageBox("��δ��ѯ�����ݣ������²�ѯ��");
	    	return;
	    }
        ExportExcelUtil.getInstance().exportExcel((TTable)getComponent("TABLE"), "ҽ�������걨����");
    }
	/**
	 *���
	 */
	public void onClear() {
		clearValue("searchStatus;endemicArea;startDate;endDate;total");
		this.callFunction("UI|TABLE|clearSelection");
		selectRow = -1;

	}

}
