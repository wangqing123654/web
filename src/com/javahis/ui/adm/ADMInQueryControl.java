package com.javahis.ui.adm;

import java.text.DecimalFormat;

import jdo.adm.ADMResvTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
/**
 * <p>Title:住院预约病患查询 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2014</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author pangben 2014-7-30
 * @version 4.5
 */
public class ADMInQueryControl extends TControl {
	int selectRow = -1;
	TParm acceptData = new TParm(); // 接参
	TTable table;
	public void onInit() {
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			acceptData = (TParm) obj;
		}
		table=(TTable)this.getComponent("TABLE");
		initUI();
	}

	/**
	 * 初始化界面
	 */
	public void initUI() {
		this.setValue("MR_NO", acceptData.getValue("MR_NO"));
		this.setValue("PAT_NAME", acceptData.getValue("PAT_NAME"));
		this.setValue("SEX_CODE1", acceptData.getValue("SEX_CODE"));
		TParm parm=new TParm();
		parm.setData("MR_NO",acceptData.getValue("MR_NO"));
		TParm result=ADMResvTool.getInstance().selectallByPatInfo(parm);
		DecimalFormat formatObject = new DecimalFormat("###########0.00");
		String lumpworkSql=" SELECT LUMPWORK_CODE, LUMPWORK_DESC AS NAME,FEE FROM MEM_LUMPWORK ";
		TParm lumpworkParm=new  TParm(TJDODBTool.getInstance().select(lumpworkSql));
		for (int i = 0; i < result.getCount(); i++) {
			if (result.getValue("LUMPWORK_CODE",i).length()>0) {//查询包干套餐金额
				for (int j = 0; j < lumpworkParm.getCount(); j++) {
					if (result.getValue("LUMPWORK_CODE",i).equals(lumpworkParm.getValue("LUMPWORK_CODE",j))) {
						result.setData("LUMPWORK_AMT",i,formatObject.format(lumpworkParm.getDouble("FEE",j)));
						break;
					}
				}
			}	
		}
		table.setParmValue(result);
	}

	/**
	 * 传回MR_NO;IPD_NO;PAT_NAME;IDNO;TEL_HOME;BIRTH_DATE
	 */
	public void onSave() {
		TTable table = (TTable) this.callFunction("UI|TABLE|getThis");
		int row = table.getSelectedRow();
		TParm backData = table.getParmValue().getRow(row);
		this.setReturnValue(backData);
		this.closeWindow();
	}
}
