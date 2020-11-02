package com.javahis.ui.opb;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

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


public class OPBConsultationFeePerfControl extends TControl {
	
	
	//ҽ��ʵ���ǿ���Ҫ������
	private int flag=0;
	
	public OPBConsultationFeePerfControl() {
		
	}
	
	public void onInit(){
		super.init();
		initPage();
	}
	
	public void initPage(){
		//��������
		setValue("REGION_CODE", "H01");


		// ����һ�����������ò�������Ϊ��PHA������ҩƷ��
		TParm parmIn = new TParm();
		parmIn.setData("CAT1_TYPE", "PHA");

		this.getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parmIn);
		// ������ܷ���ֵ����
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

		// ɾ��ҽ���������ڵ�tableһ�м�¼
		TTable table = (TTable) this.getComponent("Table_Order");
		callFunction("UI|" + table + "|addEventListener", table + "->"
				+ TTableEvent.CLICKED, this, "onDelete");

	}


	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("ORDER_CODE").setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("ORDER_DESC").setValue(order_desc);
	}

	  /*
		 * ��Table_Order�����ҽ��
		 * 
		 */
	    
		public void addOrder(){
			String ORDER_CODE=getValueString("ORDER_CODE");
			String ORDER_DESC=getValueString("ORDER_DESC");
			
			TParm tParm=new TParm();
			TTable  table = (TTable)this.getComponent("Table_Order") ;
			
			if(null==ORDER_CODE||"".equals(ORDER_CODE)){
				this.messageBox("δ����ҽ����");
				return;
			}
			
			if(flag==0){
				//���½����ݣ�Ȼ���������table.setData(tParm);��ӵ�table��
				tParm.setData("ORDER_CODE", ORDER_CODE);
				tParm.setData("ORDER_DESC", ORDER_DESC);
				table.addRow(tParm);
				table.update();
				flag++;
			}else {
					for(int j=0;j<flag;j++){
						if(table.getItemString(j, "ORDER_CODE").equals(ORDER_CODE)){
							this.messageBox("��ҽ������ӣ�");
							return;
						}
					}
						
					//���½����ݣ�Ȼ���������table.setData(tParm);��ӵ�table��
					tParm.setData("ORDER_CODE", ORDER_CODE);
					tParm.setData("ORDER_DESC", ORDER_DESC);
					table.addRow(tParm);
					table.update();
					flag++;
			}
			
		}
		
	/*
	 * ҽ���������ڵ�table�е�ɾ���ͻ�ɾ��һ����¼
	 */
	public void onDelete() {
		TTable table = (TTable) this.getComponent("Table_Order");
		int delrow = table.getSelectedRow();
		table.removeRow(delrow);
		flag--;

	}
	
	
	
	/**
     * ����ҽ�����в�ѯ
     */
    public void onQuery(){
    	TTable  table1 = (TTable)this.getComponent("TTable") ;
    	TTable  table = (TTable)this.getComponent("Table_Order") ;
    	
    	String order_code="";
    	//��ȡorder_code
    	if(flag==1){
    		order_code=" ='"+table.getItemString(0, "ORDER_CODE")+"' ";
    	}else{
    			for(int i=0;i<flag-1;i++){
    				order_code="'"+(String)table.getItemString(i, "ORDER_CODE")+"','";
    				}
    			order_code+=table.getItemString(flag-1, "ORDER_CODE")+"'";
    	    	order_code="in ( "+order_code+")";
    	}
    	
    	String sql="SELECT B.REGION_CODE,A.CASE_NO,A.DEPT_CODE,A.STATION_CODE,A.DR_CODE,A.EXE_DEPT_CODE," 
    			+"A.EXE_STATION_CODE,A.EXE_DR_CODE,A.OWN_PRICE,A.NHI_PRICE,A.TOT_AMT "
    			+" FROM IBS_ORDD A, IBS_ORDM B "
    			+"WHERE A.ORDER_CODE "
    			+order_code
    			+" AND A.CASE_NO = B.CASE_NO"
    			+" AND A.CASE_NO_SEQ = B.CASE_NO_SEQ";
    							
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
    	
    	table1.setParmValue(parm);
    	
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
    	String clearStr = "ORDER_CODE;ORDER_DESC";
        this.clearValue(clearStr);
        
        TTable  table1 = this.getTable("TTable");
        TTable  table = this.getTable("Table_Order");

        table1.removeRowAll();
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
	
    
    /**
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
     private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
     }

}
