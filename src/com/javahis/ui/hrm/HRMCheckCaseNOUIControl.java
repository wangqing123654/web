package com.javahis.ui.hrm;

import com.dongyang.control.*;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class HRMCheckCaseNOUIControl extends TControl {
    private static String TABLE = "TABLE";
    private String mrNo = "" ;
    private String name = "" ;
    private String sexCode = "" ;
    private String age = "" ;
    public void onInit(){
        //TABLE˫���¼�
        callFunction("UI|" + TABLE + "|addEventListener",
             "TABLE" + "->" + TTableEvent.DOUBLE_CLICKED, this, "onTableDoubled");
        initPage();

        this.setValue("MR_NO", this.mrNo) ;
        this.setValue("NAME", this.name) ;
        this.setValue("SEX_CODE", this.sexCode) ;
        this.setValue("AGE", this.age) ;
    }
    /**
     * ��ʼ������
     */
    public void initPage(){
        Object obj = this.getParameter();
        if(obj!=null){
            TParm parm = (TParm)obj;
            this.mrNo = parm.getValue("MR_NO");
//            this.name = (String)parm.getData("NAME",0);
//            this.sexCode = (String)parm.getData("SEX_CODE",0);
            //this.age = parm.getValue("AGE");
            String sql = "SELECT CASE_NO,PAT_NAME,SEX_CODE,COMPANY_CODE,CONTRACT_CODE,PACKAGE_CODE,MR_NO FROM HRM_PATADM WHERE MR_NO='"+mrNo+"'";
            TParm result = new TParm(getDBTool().select(sql));
            this.name = (String)result.getData("PAT_NAME",0);
            this.sexCode = (String)result.getData("SEX_CODE",0);
            this.callFunction("UI|TABLE|setParmValue", result);
        }
    }
    /** 
     * ˫���¼�
     * @param row int
     */
    public void onTableDoubled(int row){
        this.setReturnValue(getTTable("TABLE").getParmValue().getRow(row));
        this.closeWindow();
    }

    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    /**
     * �õ�TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
    
    /**
     * ��ѯ
     */
    public void onQuery(){
    	String startDate = this.getValueString("START_DATE") ;
    	String endDate = this.getValueString("END_DATE") ;
    	
    	if("".equals(startDate) || "".equals(endDate)){
    		messageBox("��������������.") ;
    		return ;
    	}
    	
    	String sql = "SELECT CASE_NO,PAT_NAME,SEX_CODE,COMPANY_CODE,CONTRACT_CODE,PACKAGE_CODE,MR_NO " +
    			     " FROM HRM_PATADM " +
    			     " WHERE MR_NO='"+mrNo+"'" +
    			     "  AND TO_CHAR(REPORT_DATE,'YYYYMMDD') BETWEEN '"+changeDate(startDate)+"' AND '"+changeDate(endDate)+"'" ;

    	TParm result = new TParm(getDBTool().select(sql));
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		if (result.getCount() <= 0) {
			messageBox("��������");
			this.callFunction("UI|TABLE|setParmValue", new TParm());
			return;
		}	
		this.callFunction("UI|TABLE|setParmValue", result);
    }
    
    private String changeDate(String date){
    	String[] dateArray = date.split(" ") ;
    	String[] newDateArray = dateArray[0].split("-") ;
    	return newDateArray[0]+newDateArray[1]+newDateArray[2] ;
    }

}
