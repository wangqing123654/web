package com.javahis.ui.ind;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: ����Ѽ�Ч
 * </p>
 *
 * <p>
 * Description: ����Ѽ�Ч
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 *
 * <p>
 * Company: BlueCore
 * </p>
 *
 * @author chenhong 2013.03.25
 * @version 1.0
 */


public class INDConsultationFeePerfControl extends TControl {
	
	public INDConsultationFeePerfControl() {
		
	}
	
	public void onInit(){
		super.init();
	}
	
	
	
	/**
     * ����ҽ�����в�ѯ
     */
    public void onQuery(){
    	//��ȡҽ��
    	String order_code=null;
    	order_code=getValueString("ORDER_CODE");
    	order_code="='"+order_code+"'";
    	
    	if("=''".equals(order_code)){
    		order_code=" in	('A0000028',"
							+"'A0000029',"
							+"'A0000030',"
							+"'A0000031',"
							+"'A0000032')";
    	}
    	
    	String sql="SELECT CASE_NO,DEPT_CODE,STATION_CODE,DR_CODE,EXE_DEPT_CODE," 
    			+"EXE_STATION_CODE,EXE_DR_CODE,OWN_PRICE,NHI_PRICE,TOT_AMT "
    			+" FROM IBS_ORDD "
    			+"WHERE ORDER_CODE "
    			+order_code;
    							

    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	
    	if(parm.getErrCode() < 0 ){
    		this.messageBox(parm.getErrText());
    		return;
    	}
        if(parm.getCount() <= 0)
        {
        	this.messageBox("��������");
        }
    	       
      //��table����ʾ��ѯ��Ϣ
    	TTable  table = (TTable)this.getComponent("TTable") ;
    	
    	table.setParmValue(parm);
    	
    }
	
    
    
    
    /**
     * ���Excel
     */
    public void onExport() {
        TTable table = this.getTable("TTable");
        if (table.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "����Ѽ�Ч");
    }
	
    /**
     * ��շ���
     */
    public void onClear() {
    	String clearStr = "ORDER_CODE";
        this.clearValue(clearStr);
        
        TTable  table = this.getTable("TTable");

        table.removeRowAll();
    }
	
	
	
    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
	
	

}
