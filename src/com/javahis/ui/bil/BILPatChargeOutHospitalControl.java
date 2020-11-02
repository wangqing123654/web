package com.javahis.ui.bil;

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
 * Title: ��Ժ����ҽ�Ʒ���
 * </p>
 *
 * <p>
 * Description: ��Ժ����ҽ�Ʒ���
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


public class BILPatChargeOutHospitalControl extends TControl {
	
	
	
	public BILPatChargeOutHospitalControl() {
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
		// (�������һ��)
		setValue("END_DATE", StringTool.rollDate(dateTime, 0));
		// ��ʼʱ��(�ϸ��µ�һ��)
		setValue("START_DATE",
				StringTool.rollDate(dateTime, -1).toString().substring(0, 4)
						+ "/"
						+ StringTool.rollDate(dateTime, -2).toString()
								.substring(5, 7) + "/01 00:00:00");
		

	}
	
	
	/**
     * ���ݳ�Ժ���ڽ��в�ѯ
     */
    public void onQuery(){
    	
    	String sDate = this.getValueString("START_DATE");
    	String eDate = this.getValueString("END_DATE");
    	
    	
    	String out_date = "";
    	sDate = sDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	out_date +=" AND OUT_DATE BETWEEN TO_DATE('" + sDate +
        "','YYYYMMDDHH24MISS') ";
    	eDate = eDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	out_date +=" AND TO_DATE('" + eDate +
        "','YYYYMMDDHH24MISS') ";
    	
    	
    	String sql="SELECT a.MR_NO,a.IPD_NO,CASE_NO,a.PAT_NAME,"
    			+"b.CTZ_DESC,c.ICD_CHN_DESC,d.DEPT_CHN_DESC,e.STATION_DESC,"
    			+"to_char(a.IN_DATE,'yyyy/mm/dd') as IN_DATE,to_char(a.OUT_DATE,'yyyy/mm/dd')  as OUT_DATE,"
    			+"a.SUM_TOT,a.CHARGE_01 "
    			+"FROM MRO_RECORD a,SYS_CTZ b,SYS_DIAGNOSIS c,SYS_DEPT d ,SYS_STATION e "
    			+"where a.CTZ1_CODE =b.CTZ_CODE "
    			+"and a.OUT_DIAG_CODE1=c.ICD_CODE "
    			+"and a.OUT_DEPT =d.DEPT_CODE "
    			+"and a.OUT_STATION =e.STATION_CODE "
    			+out_date
    			+" ORDER BY OUT_DATE";
       
    	
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
        ExportExcelUtil.getInstance().exportExcel(table, "��Ժ����ҽ�Ʒ���");
    }
	
    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "START_DATE;END_DATE";
        this.clearValue(clearStr);

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
