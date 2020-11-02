package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Vector;

import jdo.odi.OdiObject;

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
 * Title: ��ҽ������ҩƷ�ص���Ʒ��
 * </p>
 *
 * <p>
 * Description: ������ҩ��סԺ��ҩ
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


public class INDHealthCarePreMedMonitorControl extends TControl {
	
	//ҽ��ʵ���ǿ���Ҫ������
	private int flag=0;
	
	
	public INDHealthCarePreMedMonitorControl(){
		
	}
	
	public void onInit(){
		super.init();
		initPage();
	}
	
	// initPage
	public void initPage() {
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
		
		//��ʼ������
		setValue("Kind", "O");
		
		//��������
		setValue("REGION_CODE", "H01");
		
		
		//����һ�����������ò�������Ϊ��PHA������ҩƷ��
		TParm parmIn = new TParm();
        parmIn.setData("CAT1_TYPE","PHA");
        
		this.getTextField("ORDER_CODE").setPopupMenuParameter(
                "UD",
                getConfigParm().newConfig(
                    "%ROOT%\\config\\sys\\SYSFeePopup.x"), parmIn);
		//������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        
        
        //ɾ��ҽ���������ڵ�tableһ�м�¼
        TTable  table = (TTable)this.getComponent("Table_Order") ;
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
	
	
	/*public void onCheckBoxValueST(Object obj){
		TTable table=(TTable)obj;
		table.acceptText();
		int column=table.getSelectedColumn();
		String columnName=this.getTable("Table_Order").getDataStoreColumnName(column);
		int row=table.getSelectedRow();
		TParm linkParm=table.getDataStore().getRowParm(row);
		if("LINKMAIN_FLG".equals(columnName)){
			if("Y".equals(linkParm.getValue("LINKMAIN_FLG"))){
				if(linkParm.getValue("ORDER_CODE").length()==0){
					//�뿪������
					this.messageBox("E0152");
					return;
				}
				
				//��ѯ��������
				TParm linkP=new TParm();
				linkP.setData("OPERATE", linkParm.getValue("OPERATE"));
				linkP.setData("ORDER_CODE", linkParm.getValue("ORDER_CODE"));
				linkP.setData("ORDER_DESC", linkParm.getValue("ORDER_DESC"));
				
			}
		}
		
		
	}*/
	
	
	
	 /**
     * ����ҽ�������Լ��������ڽ��в�ѯ
     */
    public void onQuery(){
    	TTable  table1 = (TTable)this.getComponent("TTable") ;
    	TTable  table = (TTable)this.getComponent("Table_Order") ;
    	TParm newdata=null;
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
    	
    	 
    	//��ȡ�������
    	String kind=this.getValueString("Kind");
        
    	//����������û��ѡ��Ļ���Ĭ��Ϊ����
    	if(null==kind||"".equals(kind)){
    		kind="O";
    	}
    	
    	
    	String sDate = this.getValueString("START_DATE");
    	String eDate = this.getValueString("END_DATE");
    	
    	
    	String bill_date = "";
    	sDate = sDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	bill_date +=" AND A.BILL_DATE BETWEEN TO_DATE('" + sDate +
        "','YYYYMMDDHH24MISS') ";
    	eDate = eDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	bill_date +=" AND TO_DATE('" + eDate +
        "','YYYYMMDDHH24MISS') ";
    	
    	/* //�жϴ�ǰ̨�Ƿ�ѡ����ҽ�������Ϊ�գ�ѡ��һ��Ĭ��
    	if("=''".equals(order_code)){
    		
    		order_code=" in	('2C010009',"
    						+"'2C010007',"
    						+"'2G041030',"
    						+"'2C020013',"
    						+"'2A020009',"
    						+"'2W020040',"
    						+"'2N020015',"
    						+"'2S040013',"
    						+"'1N080013',"
    						+"'2A020007')";
    		
    		
    	}*/
    	
    		
    	//��ҽ������ҩƷ�ص���Ʒ�֣�סԺ��ҩ�����
    	String sql_I= "SELECT DISTINCT E.PAT_NAME,B.REGION_CODE,A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION, A.DOSAGE_QTY,"
    			  +"F.UNIT_CHN_DESC, A.OWN_PRICE,A.TOT_AMT,C.USER_NAME, D.DEPT_CHN_DESC,A.BILL_DATE "
    			  +"FROM (SELECT A.ORDER_CODE,A.DOSAGE_QTY,A.OWN_PRICE,A.TOT_AMT,A.BILL_DATE,A.DR_CODE,"
    			  +"A.DEPT_CODE,A.CASE_NO,A.DOSAGE_UNIT "
    			  +"FROM IBS_ORDD A "
    			  +"WHERE A.CAT1_TYPE = 'PHA' "
    			  +bill_date
    			  +") A,"
    			  +"SYS_FEE B,SYS_OPERATOR C,SYS_DEPT D,MRO_RECORD E,SYS_UNIT F "
    			  +"WHERE A.ORDER_CODE = B.ORDER_CODE AND A.DR_CODE = C.USER_ID AND A.DEPT_CODE = D.DEPT_CODE "
    			  +"AND A.CASE_NO = E.CASE_NO AND A.DOSAGE_UNIT = F.UNIT_CODE "
    			  +"AND B.ORDER_CODE "
    			  +order_code
    			  +" ORDER BY A.ORDER_CODE";
        
    	//��ҽ������ҩƷ�ص���Ʒ�֣�������ҩ�����
    	String sql_O= "SELECT DISTINCT E.PAT_NAME,B.REGION_CODE,A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION, A.DOSAGE_QTY,"
	  			  +"F.UNIT_CHN_DESC, A.OWN_PRICE,A.AR_AMT,C.USER_NAME, D.DEPT_CHN_DESC,A.BILL_DATE "
	  			  +"FROM (SELECT A.ORDER_CODE,A.DOSAGE_QTY,A.OWN_PRICE,A.AR_AMT,A.BILL_DATE,A.DR_CODE,"
	  			  +"A.DEPT_CODE,A.MR_NO,A.DOSAGE_UNIT "
	  			  +"FROM OPD_ORDER A "
	  			  +"WHERE A.CAT1_TYPE = 'PHA' "
	  			  +bill_date
	  			  +") A,"
	  			  +"SYS_FEE B,SYS_OPERATOR C,SYS_DEPT D,MRO_RECORD E,SYS_UNIT F "
	  			  +"WHERE A.ORDER_CODE = B.ORDER_CODE AND A.DR_CODE = C.USER_ID AND A.DEPT_CODE = D.DEPT_CODE "
	  			  +"AND A.MR_NO = E.MR_NO AND A.DOSAGE_UNIT = F.UNIT_CODE "
	  			  +"AND B.ORDER_CODE "
	  			  +order_code
	  			  +" ORDER BY A.ORDER_CODE";
    	
    	
    	if(kind.equals("I")){
    	     newdata = new TParm(TJDODBTool.getInstance().select(sql_I));
    	}else {
    		newdata = new TParm(TJDODBTool.getInstance().select(sql_O));
    	}
    	
    	
    	
    	if(newdata.getErrCode() < 0 ){
    		this.messageBox(newdata.getErrText());
    		return;
    	}
        if(newdata.getCount() <= 0)
        {
        	this.messageBox("��������");
        }
    	
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
        ExportExcelUtil.getInstance().exportExcel(table, "��ҽ������ҩƷ�ص���Ʒ��");
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
