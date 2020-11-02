package com.javahis.ui.ind;

import java.sql.Timestamp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: ��Դ
 * </p>
 *
 * <p>
 * Description: ��Դ
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



public class INDNucleusSourceControl extends TControl {
	
	public INDNucleusSourceControl() {
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
						+ TypeTool.getString(date).substring(5, 7) + "/"
						+ TypeTool.getString(date).substring(8, 10)
						+ " 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (�ϸ������һ��)
		setValue("END_DATE", StringTool.rollDate(dateTime, 0));
		// ��ʼʱ��(�ϸ��µ�һ��)
		setValue("START_DATE",
				StringTool.rollDate(dateTime, -1).toString().substring(0, 4)
						+ "/"
						+ StringTool.rollDate(dateTime, -2).toString()
								.substring(5, 7) + "/01 00:00:00");
		

	}
	
	
	
	public void onQuery(){
		
		//��ȡҽ����
		String order_code=getValueString("ORDER_CODE");
		
		order_code="='"+order_code+"'";
		
		String sDate = this.getValueString("START_DATE");
    	String eDate = this.getValueString("END_DATE");
    	
    	String bill_date = "";
    	sDate = sDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	bill_date +="A.BILL_DATE BETWEEN TO_DATE('" + sDate +
        "','YYYYMMDDHH24MISS') ";
    	eDate = eDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	bill_date +=" AND TO_DATE('" + eDate +
        "','YYYYMMDDHH24MISS') ";
		
    	
    	//ҽ��Ĭ��ȫѡ
    	if("=''".equals(order_code)){
    		order_code="in ('S1006360','S1006329','S1005756','S1006357','S1006358','S1006359','S1005754','S1005755','S1005757','S1005758','S1005759','S1005760','S1005761','S1005762','S1005763','S1005764','S1005765','S1005766')";
    	}
    	
    	String sql_O="select a.MR_NO,a.ORDER_DESC,a.DOSAGE_QTY,a.OWN_AMT,a.BILL_DATE,A.SEQ_NO "
    			   +"FROM OPD_ORDER a,SYS_FEE c "
    			   +"WHERE "+bill_date
    			   +" AND a.ORDER_CODE "
    			   +order_code
    			   +" GROUP BY a.MR_NO ,a.ORDER_DESC,a.DOSAGE_QTY,a.OWN_AMT,a.BILL_DATE,A.SEQ_NO";
    	
    	
    	String sql_I="select b.MR_NO,c.ORDER_DESC,a.DOSAGE_QTY,a.OWN_AMT,a.BILL_DATE,A.SEQ_NO "
	 			   +"FROM IBS_ORDD a, SYS_FEE c,ODI_ORDER b "
	 			   +"WHERE "+bill_date
	 			   +" AND a.CASE_NO=b.CASE_NO"
	 			   +" AND a.ORDER_CODE=c.ORDER_CODE"
	 			   +" AND a.ORDER_CODE "
	 			   +order_code
	 			   +" GROUP BY b.MR_NO,c.ORDER_DESC,a.DOSAGE_QTY,a.OWN_AMT,a.BILL_DATE,A.SEQ_NO";
    	
    	
    	TParm parm=null;
		
		//סԺ
		if ("Y".equalsIgnoreCase(this.getValueString("Outpatient"))) {
			parm = new TParm(TJDODBTool.getInstance().select(sql_O));
			if(parm.getCount()<=0){
				setValue("Count","0");
				messageBox("��������");
				
			}else{
				
				setValue("Count", Integer.toString(parm.getCount()));
			}
			
		}
		//�ż���
		if ("Y".equalsIgnoreCase(this.getValueString("In_Hospital"))) {
			parm = new TParm(TJDODBTool.getInstance().select(sql_I));
			if(parm.getCount()<=0){
				setValue("Count","0");
				this.messageBox("��������");
				
			}else{
				
				setValue("Count", Integer.toString(parm.getCount()));
			}
		}
		//���
		if("Y".equalsIgnoreCase(this.getValueString("Examination"))){
			parm = new TParm(TJDODBTool.getInstance().select(sql_O));
			if(parm.getCount()<=0){
				setValue("Count", "0");
				this.messageBox("��������");
				
			}else{
				
				setValue("Count", Integer.toString(parm.getCount()));
			}
		}

        //System.out.println("parm:"+parm);
		TTable table=this.getTable("TTable");
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
        ExportExcelUtil.getInstance().exportExcel(table, "��Դ");
    }
	
    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "ORDER_CODE;START_DATE;END_DATE;Count";
        this.clearValue(clearStr);

        // ��ʼ��ͳ������
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        // ����ʱ��
        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
            substring(0, 4) + "/" + TypeTool.getString(date).
            substring(5, 7) + "/01 23:59:59", "yyyy/MM/dd HH:mm:ss");
        // (�ϸ������һ��)
        setValue("END_DATE", StringTool.rollDate(dateTime, 0));
        // ��ʼʱ��(�ϸ��µ�һ��)
        setValue("START_DATE",
                 StringTool.rollDate(dateTime, -3).toString().substring(0, 4) +
                 "/" +
                 StringTool.rollDate(dateTime, -1).toString().substring(5, 7) +
                 "/01 00:00:00");
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
