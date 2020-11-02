package com.javahis.ui.adm;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: �շ�Ա������ͳ��
 * </p>
 * 
 * <p>
 * Description:�շ�Ա������ͳ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012 BlueCore
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 2012-3-20
 * @version 4.0.1
 */
public class ADMWorkStatisticsControl extends TControl{
	private TTable table;
	private TTable tableDetail;//ϸ��
	public ADMWorkStatisticsControl() {

	}

	/*
	 * ��ʼ��
	 */
	public void onInit() {
		initPage();
	}
	private void initPage() {
		table = (TTable) this.getComponent("TABLE");
		tableDetail = (TTable) this.getComponent("TABLE_DETAIL");
		
		String now = StringTool.getString(SystemTool.getInstance().getDate(),
				"yyyyMMdd");
		this.setValue("START_DATE", StringTool.getTimestamp(now + "000000",
				"yyyyMMddHHmmss"));// ��ʼʱ��
		this.setValue("END_DATE", StringTool.getTimestamp(now + "235959",
				"yyyyMMddHHmmss"));// ����ʱ��
		this.setValue("USER_ID", Operator.getID());
		table.removeRowAll();
		tableDetail.removeRowAll();

	}
	 /**
     * ���
     */
    public void onClear(){
    	initPage();
    }
    /**
     * ���Excel
     */
    public void onExport() {

        // �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
        //TTable table = (TTable) callFunction("UI|Table|getThis");
    	TParm parm=tableDetail.getParmValue();
    	if (parm.getCount()<=0) {
    		this.messageBox("û����Ҫ����������");
			return;
		}
        if (tableDetail.getRowCount() > 0)
            ExportExcelUtil.getInstance().exportExcel(tableDetail, "�շ�Ա������ͳ��");
    }
    /**
     * ��ѯ
     */
    public void onQuery(){
    	
    	if (this.getValueString("START_DATE").length() == 0) {
			messageBox("��ʼʱ�䲻��ȷ!");
			return;
		}
		if (this.getValueString("END_DATE").length() == 0) {
			messageBox("����ʱ�䲻��ȷ!");
			return;
		}
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmmss");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMddHHmmss");
		
		getQueryParm(startTime, endTime);
    }
    /**
     * ��ѯ��һ��ҳǩ����
     * @param startTime
     * @param endTime
     */
    public void getQueryParm(String startTime,String endTime){
    	StringBuffer sql=new StringBuffer();
    	// Ԥ�������ݲ�ѯ
		sql.append("SELECT A.ADM_CLERK,COUNT(A.CASE_NO) AS COUNT ,B.USER_NAME AS PAT_NAME FROM ADM_INP A,SYS_OPERATOR B "
				+ "WHERE A.ADM_CLERK=B.USER_ID(+) AND IN_DATE BETWEEN TO_DATE('" + startTime
				+ "','YYYYMMDDHH24MISS') AND TO_DATE('" + endTime
				+ "','YYYYMMDDHH24MISS') ");
		// �շ���Ա
		if (this.getValue("USER_ID").toString().length() > 0) {
			sql.append(" AND ADM_CLERK ='").append(this.getValue("USER_ID"))
					.append("'");
		}
		sql.append(" GROUP BY A.ADM_CLERK,B.USER_NAME ");
    	TParm returnParm = new TParm(TJDODBTool.getInstance().select(
				sql.toString()));
    	if (returnParm.getErrCode()<0) {
    		this.messageBox("E0005");
			return;
		}
    	if (returnParm.getCount()<=0) {
			this.messageBox("û����Ҫ��ѯ������");
			table.removeRowAll();
			tableDetail.removeRowAll();
			return;
		}
    	table.setParmValue(returnParm);
    	tableDetail.removeRowAll();
    }
    /**
     * �����¼�
     */
    public void onTableClick(){
    	TParm parm=table.getParmValue();
    	int row=table.getSelectedRow();
    	if (row<0) {
    		this.messageBox("��ѡ������");
			return;
		}
    	String admClerk =parm.getValue("ADM_CLERK",row);//������Ա
    	//��ʼʱ��
    	String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmmss");
    	//����ʱ��
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMddHHmmss");
    	String sql="SELECT C.PAT_NAME,C.SEX_CODE,B.IN_DATE,"
		+ " B.MR_NO,B.IPD_NO"
		+ " FROM ADM_INP B,SYS_PATINFO C"
		+ " WHERE B.MR_NO=C.MR_NO(+)"+
		" AND B.IN_DATE BETWEEN TO_DATE('" + startTime
				+ "','YYYYMMDDHH24MISS') AND TO_DATE('" + endTime
				+ "','YYYYMMDDHH24MISS') "
		+ " AND B.ADM_CLERK='"+admClerk
		+ "'";
    	TParm returnParm = new TParm(TJDODBTool.getInstance().select(
				sql.toString()));
    	if (returnParm.getErrCode()<0) {
			return;
		}
    	tableDetail.setParmValue(returnParm);
    }
    /**
     * ��ӡ
     */
    public void onPrint(){
    	TParm parm=table.getParmValue();

    	if (parm==null || parm.getCount()<=0) {
    		this.messageBox("û����Ҫ��ӡ������");
			return;
		}
    	String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyy/MM/dd HH:mm:ss");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyy/MM/dd HH:mm:ss");
		
		TParm data2 = new TParm();
        for (int i = 0; i < parm.getCount(); i++) {
        	data2.addData("ADM_CLERK", parm.getData("ADM_CLERK", i)) ;
        	data2.addData("PAT_NAME", parm.getData("PAT_NAME", i)) ;
        	data2.addData("COUNT", parm.getData("COUNT", i)) ;
		}		
        data2.setCount(data2.getCount("ADM_CLERK")) ;
        data2.addData("SYSTEM", "COLUMNS", "ADM_CLERK");
        data2.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        data2.addData("SYSTEM", "COLUMNS", "COUNT");  

        TParm data1 = new TParm();// ��ӡ������
        data1.setData("DATATABLE", data2.getData()) ;
        data1.setData("TIME","TEXT",startTime+"~"+endTime) ;
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMWorkStatistics.jhw",data1);
    }
}
