package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

public class INSChargeAprroveControl extends TControl{
	
	TTable table;
	
	public void onInit() {
		table = (TTable)this.getComponent("TABLE");
    }
    /**
     * ��ѯ
     */
    public void onQuery(){
    	String yearmon = getValueString("YEAR_MON");
    	if(yearmon.equals("")){
    		messageBox("�������ں�");
    		return;
    	}
    	String sql = 
    		"Select i.Year_Mon,i.Report_Code,s.CATEGORY_CHN_DESC,c.Ctz_desc," +
    		"i.Ctz_Count,i.Total_Amt,i.Own_Amt,i.Addpay_Amt,i.Total_Nhi_Amt," +
    		"i.ACTURAL_STANDARD_AMT,i.OWN_AMT_UPON_STANDARD, i.BLOOD_OWN_AMT" +
    		"  from  INS_IBS_PAY i, SYS_CATEGORY s , sys_ctz c " +
    		"where i.NHI_PAY_TYPE=s.CATEGORY_CODE And  i.ctz_code=c.ctz_code and i.year_mon='"+yearmon+"'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    	}
    	if(result.getCount()<0){
    		messageBox("��������");
    	}
    	this.callFunction("UI|TABLE|setParmValue", result);
    }
    
    /**
     * ���Excel
     */
    public void onExport() {
    	table = (TTable)this.getComponent("TABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "ҽ��֧��������");
    }
    /**
     * ���
     */
    public void onClear(){
    	clearValue("YEAR_MON");
    }

}
