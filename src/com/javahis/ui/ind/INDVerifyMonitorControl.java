package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.util.Calendar;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 2012��һ�������ȶ�ȱҩƷ�ɹ�����
 * </p>
 *
 * <p>
 * Description: 2012��һ�������ȶ�ȱҩƷ�ɹ�����
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


public class INDVerifyMonitorControl extends TControl {
	
	//ҽ��ʵ���ǿ���Ҫ������
	private int flag=0;
	
	public INDVerifyMonitorControl() {
	}
	
	//��ʼ��ҳ��
	public void onInit() {
		super.onInit();
		initPage();
	}
	
	
	//initPage
	public void initPage(){
		// ��ʼ��ͳ������
		Timestamp date = TJDODBTool.getInstance().getDBTime();

		// ����ʱ��
		Timestamp dateTime = StringTool.getTimestamp(
				TypeTool.getString(date).substring(0, 4) + "/"
						+ TypeTool.getString(date).substring(5, 7)
						+ "/25 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (����25)
		setValue("END_DATE", dateTime);

		// ��ʼʱ��(�ϸ���26)
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(date.getTime());
		cd.add(Calendar.MONTH, -1);
		Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());

		setValue("START_DATE", endDateTimestamp.toString().substring(0, 4)
				+ "/" + endDateTimestamp.toString().substring(5, 7)
				+ "/26 00:00:00");
		
		
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
		TTable  table = (TTable)this.getComponent("Table_Order") ;
        int delrow = table.getSelectedRow();
        table.removeRow(delrow);
        flag--;
    }
	
    
    /**
     * ����ҩ�������Լ�����ʱ����в�ѯ
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
    	
    	
    	String sDate = this.getValueString("START_DATE");
    	String eDate = this.getValueString("END_DATE");
    	
    	String opt_date = "";
    	sDate = sDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	opt_date +=" AND a.opt_date > TO_DATE('" + sDate +
        "','YYYYMMDDHH24MISS') ";
    	eDate = eDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	opt_date +=" AND a.opt_date < TO_DATE('" + eDate +
        "','YYYYMMDDHH24MISS') ";
    	
    	String sql="SELECT b.REGION_CODE,b.ORDER_DESC,b.SPECIFICATION,b.OWN_PRICE," +
	   		       "a.VERIFYIN_PRICE, a.MAN_CODE, SUM(a.VERIFYIN_QTY) as VERIFYIN_QTY " +
	   		       "FROM IND_VERIFYIND a, SYS_FEE b  " +
	   		       "WHERE a.ORDER_CODE "+order_code+
	   		       " AND a.ORDER_CODE = b.ORDER_CODE "+opt_date+
	   		       " GROUP BY b.ORDER_DESC,b.SPECIFICATION,b.OWN_PRICE, a.VERIFYIN_PRICE, a.MAN_CODE,b.REGION_CODE";
  
    	TParm newdata = new TParm(TJDODBTool.getInstance().select(sql));
    	
    	/*System.out.println("sql++++"+sql);*/
    	
    	if(newdata.getErrCode() < 0 ){
    		this.messageBox(newdata.getErrText());
    		return;
    	}
        if(newdata.getCount() <= 0)
        {
        	this.messageBox("��������");
        }
    	
        
      //��table����ʾ��ѯ��Ϣ
        table1.setParmValue(newdata);
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
        ExportExcelUtil.getInstance().exportExcel(table, "ҩƷ�ɹ����");
    }
    
    
    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "ORDER_CODE;ORDER_DESC;START_DATE;END_DATE";
        this.clearValue(clearStr);

		// ��ʼ��ͳ������
		Timestamp date = TJDODBTool.getInstance().getDBTime();

		// ����ʱ��
		Timestamp dateTime = StringTool.getTimestamp(
				TypeTool.getString(date).substring(0, 4) + "/"
						+ TypeTool.getString(date).substring(5, 7)
						+ "/25 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (����25)
		setValue("END_DATE", dateTime);

		// ��ʼʱ��(�ϸ���26)
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(date.getTime());
		cd.add(Calendar.MONTH, -1);
		Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());

		setValue("START_DATE", endDateTimestamp.toString().substring(0, 4)
				+ "/" + endDateTimestamp.toString().substring(5, 7)
				+ "/26 00:00:00");
		
        TTable  table = this.getTable("Table_Order");
        TTable  table1 = this.getTable("TTable");
        table.removeRowAll();
        table1.removeRowAll();
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
