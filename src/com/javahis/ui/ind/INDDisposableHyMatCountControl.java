package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.util.Calendar;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;


/**
 * <p>
 * Title: һ������������ͳ��
 * </p>
 *
 * <p>
 * Description: һ������������ͳ��
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

public class INDDisposableHyMatCountControl extends TControl {
	
	public INDDisposableHyMatCountControl() {
		
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
		
		//��������
		setValue("REGION_CODE", "H01");
	}
	
	
	
	/**
     * ���ݼ��������Լ�ҽ�����в�ѯ
     */
    public void onQuery(){
    	
    	String sDate = this.getValueString("START_DATE");
    	String eDate = this.getValueString("END_DATE");
    	
    	String bill_date = "";
    	sDate = sDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	bill_date +=" AND��A.BILL_DATE BETWEEN TO_DATE('" + sDate +
        "','YYYYMMDDHH24MISS') ";
    	eDate = eDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	bill_date +=" AND TO_DATE('" + eDate +
        "','YYYYMMDDHH24MISS') ";
    	
    	
    	String sql="SELECT REGION_CODE,ORDER_CODE AS ҽ������,ORDER_DESC AS ҽ������,SPECIFICATION AS ���,UNIT_CHN_DESC AS ��λ,"
    			+"OWN_PRICE AS ����,DOSAGE_QTY AS ����,TOT_AMT AS �ܼ�,COST_CENTER_CHN_DESC AS ִ�п���  "
    			+"FROM ("
    				+"SELECT B.REGION_CODE,A.ORDER_CODE,A.EXE_DEPT_CODE, B.ORDER_DESC,B.SPECIFICATION, D.UNIT_CHN_DESC, A.OWN_PRICE , "
    				+"SUM (A.DOSAGE_QTY) AS DOSAGE_QTY,sum( A.OWN_PRICE * A.DOSAGE_QTY) AS TOT_AMT,C.COST_CENTER_CHN_DESC AS COST_CENTER_CHN_DESC "
    				+" FROM IBS_ORDD A, SYS_FEE B,SYS_COST_CENTER C,SYS_UNIT D"
    				+" WHERE A.ORDER_CODE  LIKE 'S1%'"
    				+" AND A.OWN_PRICE >= 2000 "
    				+" AND A.ORDER_CODE = B.ORDER_CODE "
    				+" AND A.EXE_DEPT_CODE = C.COST_CENTER_CODE"
    				+" AND A.MEDI_UNIT = D.UNIT_CODE "
    				+bill_date
    				+" GROUP BY A.ORDER_CODE,A.EXE_DEPT_CODE,B.ORDER_DESC,B.SPECIFICATION,B.REGION_CODE,"
    				+" D.UNIT_CHN_DESC,A.OWN_PRICE,C.COST_CENTER_CHN_DESC "
    				+" ORDER BY A.ORDER_CODE)";
    							

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
        ExportExcelUtil.getInstance().exportExcel(table, "һ������������ͳ��");
    }
	
    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "START_DATE;END_DATE;ORDER_CODE";
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
