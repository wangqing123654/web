package com.javahis.ui.mro;

import java.sql.Timestamp;
import java.util.Calendar;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

import jdo.sys.SystemTool;

/**
 * <p>
 * Title: �ż��ﻼ�������Ϣģ����ѯ����������
 * </p>
 *
 * <p>
 * Description: �ż��ﻼ�������Ϣģ����ѯ����������
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 * com.javahis.ui.mro.MRODiagSearchControl
 * 
 * @author yanglu
 * @version 1.0
 */

public class MRODiagToINPSearchControl extends TControl{
	/**
	 * ��ʾ���ݵ�Table
	 */
	private TTable table;
	public void onInit() {
		super.onInit();
		 initDate();
		table = getTable("tTable_1");
//		this.setValue("START_DATE", "");
//		this.setValue("END_DATE", "");
		this.setValue("ICD_CODE", "");
	}
	/**
	 * �õ�TABLE����
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
    /**
     * ���췽��
     */
    public MRODiagToINPSearchControl() {
        super();
    }
    
    public void onQuery(){
		if ("".equals(this.getValue("START_DATE"))
				|| this.getValue("START_DATE") == null) {
			this.messageBox("��ʼʱ�䲻��Ϊ�գ�");
			return;
		} else if ("".equals(this.getValue("END_DATE"))
				|| this.getValue("END_DATE") == null) {
			this.messageBox("����ʱ�䲻��Ϊ�գ�");
			return;
		}
//		if("".equals(this.getValue("ICD_CODE"))){
//			this.messageBox("��ϴ��벻��Ϊ�գ�");
//			return;
//		}
    	//���ʱ��ѡ��,��ʼʱ�����ǱȽ���ʱ���,�����ǲ������.��ʾ����ʼ��
        int subdate = getSubDate((Timestamp)getValue("START_DATE"), (Timestamp)getValue("END_DATE"));
        if(subdate < 0){
            this.messageBox("��ʼʱ�����С�ڽ���ʱ�䣬������ѡ��!");
            return ;
        }
      	 String startDate  = this.getValue("START_DATE").toString();
         startDate = startDate.substring(0,19);
         
         String endDate  = this.getValue("END_DATE").toString();
         endDate = endDate.substring(0,19);

        TParm param = this.getParmForTag("START_DATE;END_DATE;ICD_CODE");
        String icdCode =  this.getValue("ICD_CODE").toString();
        
        
        
//        String sql= "SELECT inp.DS_DATE,dept.DEPT_CHN_DESC AS DS_DEPT_CODE,inp.MR_NO,pat.PAT_NAME,pat.SEX_CODE AS SEX,adm.ICD_CODE,sys.ICD_CHN_DESC FROM ADM_INP inp ";
//        sql+=" INNER JOIN ADM_INPDIAG adm ON adm.CASE_NO = inp.CASE_NO";
//        sql+=" INNER JOIN SYS_DIAGNOSIS_ORG sys ON adm.ICD_CODE = sys.ICD_CODE";
//        sql+=" INNER JOIN SYS_PATINFO pat ON inp.MR_NO = pat.MR_NO ";
//        sql+=" INNER JOIN SYS_DEPT dept ON inp.DS_DEPT_CODE = dept.DEPT_CODE";
//        sql+=" WHERE adm.ICD_CODE LIKE '"+ icdCode+"%' ";
//        sql+="	AND inp.DS_DATE BETWEEN TO_DATE( '"+startDate+"', 'YYYY-MM-DD HH24:MI:SS' )";
//        sql+=" AND TO_DATE( '"+endDate+"', 'YYYY-MM-DD HH24:MI:SS' ) ";
//        sql+=" ORDER BY MR_NO, inp.DS_DATE";
        
        
        
        String sql = "SELECT\r\n" + 
        		"	adm.DS_DATE,\r\n" + 
        		"	dept.DEPT_CHN_DESC AS DS_DEPT_CODE,\r\n" + 
        		"	mro.MR_NO,\r\n" + 
        		"	pat.PAT_NAME,\r\n" + 
        		"	pat.SEX_CODE AS SEX,\r\n" + 
        		"	mro.ICD_CODE,\r\n" + 
        		"	mro.ICD_DESC AS ICD_CHN_DESC \r\n" + 
        		"FROM\r\n" + 
        		"	MRO_RECORD_DIAG mro\r\n" + 
        		"	LEFT JOIN ADM_INP adm ON adm.CASE_NO = mro.CASE_NO\r\n" + 
        		"	LEFT JOIN SYS_PATINFO pat ON pat.MR_NO = mro.MR_NO\r\n" + 
        		"	LEFT JOIN SYS_DEPT dept ON dept.DEPT_CODE = adm.DS_DEPT_CODE \r\n" + 
        		"WHERE\r\n" + 
        		"	mro.IO_TYPE = 'O' \r\n" + 
        		"	AND	mro.ICD_CODE LIKE '"+this.getValue("ICD_CODE")+"%' \r\n" + 
        		"	AND adm.DS_DATE BETWEEN TO_DATE ( '"+startDate+"', 'YYYY-MM-DD HH24:MI:SS' ) \r\n" + 
        		"	AND TO_DATE ( '"+endDate+"', 'YYYY-MM-DD HH24:MI:SS' ) \r\n" + 
        		"ORDER BY\r\n" + 
        		"	mro.MR_NO,\r\n" + 
        		"	adm.DS_DATE";
        
        
        
        TParm result = new TParm(
                TJDODBTool.getInstance().select(sql)
          );
        if(result.getErrCode() < 0 || result.getCount() <= 0){
            this.messageBox("û�в�ѯ������!");
            this.err(result.getErrName() + "    " + result.getErrText());
            table.removeRowAll();
            return ;
        }
        for(int i = 0 ;i<result.getCount();i++) {
        	if("1".equals(result.getValue("SEX",i))) {
        		result.setData("SEX",i,"��");
        	}else if("2".equals(result.getValue("SEX",i))) {
        		result.setData("SEX",i,"Ů");
        	}else {
        		result.setData("SEX",i,"δ֪");
        	}
        }
        table.setParmValue(result);
    }
    
    
    /**
     * ��������ʱ��ε��������,�����ʵ����������ڽ�������,���򷵻�-1
     * @param startDate Timestamp
     * @param endDate Timestamp
     * @return int �����������
     */
    private int getSubDate(Timestamp startDate, Timestamp endDate){
        //һ��ĺ�����
        long date = 24 * 60 * 60 * 1000;
        //��ʱ����һ��ĺ�����,������������
        return (int)((endDate.getTime() - startDate.getTime()) / date);
    }
    
    

    /**
     * ������Xls
     */
    public void onExport(){
        if (table.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "��ʹ�ü����������-סԺ");

    }
    
    /**
     * �������
     */
    public void onClear(){
        this.clearValue("START_DATE;END_DATE;ICD_CODE");
        initDate();
        table.removeRowAll();

    }
    
    
    /**
     * ��ʼ��ʱ��,��ʼ��������һ�µ�1��,���������ǵ�ǰ�µ�1��
     */
    private void initDate(){
        Timestamp currentDate = SystemTool.getInstance().getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        //��ʼ������ʱ��,��ǰ������ʱ����
        this.setValue("END_DATE", new Timestamp(calendar.getTimeInMillis()));
        int year = calendar.get(Calendar.YEAR);
        //ע��: ��calendar��ȡ�õ��·��Ǵ�0��ʼ��,�����11
        int month = calendar.get(Calendar.MONTH);

        //��ʼ����ʼʱ��,ǰһ���µ�һ��
        int startyear = year;
        int startmonth = month;
        //��ʼ����ʼʱ��,���µĵ�һ��,���������1��,����ʼ��Ӧ������һ��.�����1��1��,����ʼ��Ӧ������һ���12��1��
        if(calendar.get(Calendar.DATE) == 1){
            //�жϵ�ǰ���ǲ���1��,�����1��,����ʼ��Ӧ������һ��
            startyear = month == 0 ? (year - 1) : year;
            //�����1��,����ʼ��Ӧ������һ������һ����
            startmonth = month == 0 ? 11 : month;
        }
        calendar.set(startyear, startmonth, 1, 0, 0, 0);
        this.setValue("START_DATE",new Timestamp(calendar.getTimeInMillis()));
    }
    

}
