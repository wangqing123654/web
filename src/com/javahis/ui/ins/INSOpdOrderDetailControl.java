package com.javahis.ui.ins;

import java.text.DecimalFormat;

import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: ����ҽ�Ʒ�����ϸ��</p>
 *
 * <p>Description:����ҽ�Ʒ�����ϸ��</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author lim 
 * @version 1.0
 */
public class INSOpdOrderDetailControl extends TControl {
	
	private String caseNo = "" ;
	
	private String confirmNo = "" ;
	
	private String hospArea = "" ;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		
	}

	/**
	 * ��ѯ����.
	 */
	public void onQuery() {
		String mrNo = this.getValueString("MR_NO");
		String Sqlstr1 = " SELECT A.MR_NO,A.PAT_NAME,B.CHN_DESC,C.CTZ_DESC,A.CASE_NO,A.CONFIRM_NO,A.REGION_CODE "
				+ " FROM INS_MZ_CONFIRM A,SYS_DICTIONARY B,SYS_CTZ C"
				+ " WHERE A.MR_NO='" + mrNo + "'" + " AND B.GROUP_ID='SYS_SEX'"
				+ " AND A.SEX_CODE = B.ID" + " AND A.CTZ_CODE = C.CTZ_CODE";

		TParm result1 = new TParm(TJDODBTool.getInstance().select(Sqlstr1));
		if (result1.getErrCode() < 0) {
			messageBox(result1.getErrText());
			return;
		}
		if (result1.getCount() <= 0) {
			messageBox("��������");
			return;
		}
		this.setValue("NAME", result1.getData("PAT_NAME", 0));
		this.setValue("SEX", result1.getData("CHN_DESC", 0));
		this.setValue("IDENTITY", result1.getData("CTZ_DESC", 0));
		
		this.setCaseNo(result1.getValue("CASE_NO",0)) ;
		this.setConfirmNo(result1.getValue("CONFIRM_NO", 0)) ;
		this.setHospArea(result1.getValue("REGION_CODE",0)) ;
		
		String Sqlstr2 = " SELECT INS_DATE,CASE_NO,TOT_AMT,OWN_AMT,NHI_AMT,OTOT_AMT,ACCOUNT_PAY_AMT,CONFIRM_NO"
			+ " FROM INS_OPD" + " WHERE MR_NO='" + mrNo + "'";		

		TParm result2 = new TParm(TJDODBTool.getInstance().select(Sqlstr2));

		if (result2.getErrCode() < 0) {
			messageBox(result2.getErrText());
			return;
		}
		if (result2.getCount() <= 0) {
			this.callFunction("UI|TTABLE|setParmValue", new TParm());
			messageBox("��������");
			return;
		}
		
		this.callFunction("UI|TTABLE|setParmValue", result2);
	}

    /**
     * �����Ų�ѯ(�س��¼�)
     */
	public void onMrNoAction(){
		String mrNo = this.getValueString("MR_NO") ;
		this.setValue("MR_NO", PatTool.getInstance().checkMrno(mrNo)) ;		
	}

	/**
	 * ��ӡ����.
	 */
	public void onPrint() {
        String Sqlstr1 =
            " SELECT B.PAT_NAME,C.CHN_DESC,TO_CHAR(B.PAT_AGE,'FM9999999') AS PAT_AGE,B.IDNO,A.MZ_DIAG,A.DRUG_DAYS," +
            " (CASE WHEN A.UNACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.UNACCOUNT_PAY_AMT END)+(CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT  END)+(CASE WHEN A.OTOT_AMT IS NULL THEN 0 ELSE A.OTOT_AMT  END) AS SUM1," +
            " A.PHA_AMT,A.EXM_AMT,A.TREAT_AMT,A.OP_AMT,A.OTHER_AMT,A.UNACCOUNT_PAY_AMT,A.OWN_AMT,A.ACCOUNT_PAY_AMT,A.OTOT_AMT," +
            " (CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT  END)+(CASE WHEN A.OTOT_AMT IS NULL THEN 0 ELSE A.OTOT_AMT  END) AS SUM2,F.USER_NAME" +
            " FROM INS_OPD A,INS_MZ_CONFIRM B,SYS_DICTIONARY C,REG_PATADM D,SYS_OPERATOR F" +
            " WHERE A.CASE_NO='" + this.getCaseNo() + "'" +
            " AND A.CONFIRM_NO='" + this.getConfirmNo() + "'" +
            " AND A.REGION_CODE = '" + this.getHospArea() + "'" +
            " AND A.CASE_NO = B.CASE_NO" +
            " AND C.GROUP_ID = 'SYS_SEX'"+
            " AND B.SEX_CODE = C.ID" +
            " AND A.CASE_NO = D.CASE_NO" +
            " AND D.DR_CODE = F.USER_ID" ;		
		TParm result1 = new TParm(TJDODBTool.getInstance().select(Sqlstr1)) ;
    	if(result1.getCount()<=0){
    		this.messageBox("û�д�ӡ����");
    		return;
    	}

    	DecimalFormat df = new DecimalFormat("##########0.00");
    	TParm data1 = new TParm();// ��ӡ������
    	data1.setData("PAT_NAME","TEXT",result1.getValue("PAT_NAME", 0)) ;
    	data1.setData("CHN_DESC","TEXT",result1.getValue("CHN_DESC", 0)) ;
    	data1.setData("PAT_AGE","TEXT",result1.getValue("PAT_AGE", 0)+"��") ;
    	data1.setData("IDNO","TEXT",result1.getValue("IDNO", 0)) ;
    	data1.setData("MZ_DIAG","TEXT",result1.getValue("MZ_DIAG", 0)) ;
    	data1.setData("DRUG_DAYS","TEXT",result1.getValue("DRUG_DAYS", 0)+"��") ;
    	data1.setData("SUM1","TEXT",df.format(result1.getData("SUM1", 0))) ;
    	data1.setData("PHA_AMT","TEXT",df.format(result1.getData("PHA_AMT", 0))) ;
    	data1.setData("EXM_AMT","TEXT",df.format(result1.getData("EXM_AMT", 0))) ;
    	data1.setData("TREAT_AMT","TEXT",df.format(result1.getData("TREAT_AMT", 0))) ;
    	data1.setData("OP_AMT","TEXT",df.format(result1.getData("OP_AMT", 0))) ;
    	data1.setData("OTHER_AMT","TEXT",df.format(result1.getData("OTHER_AMT", 0))) ;
    	data1.setData("UNACCOUNT_PAY_AMT","TEXT",df.format(result1.getData("UNACCOUNT_PAY_AMT", 0))) ;
    	data1.setData("OWN_AMT","TEXT",df.format(result1.getData("OWN_AMT", 0))) ;
    	data1.setData("ACCOUNT_PAY_AMT","TEXT",df.format(result1.getData("ACCOUNT_PAY_AMT", 0))) ;
    	data1.setData("OTOT_AMT","TEXT",df.format(result1.getData("OTOT_AMT", 0))) ;
    	data1.setData("SUM2","TEXT",df.format(result1.getData("SUM2", 0))) ;
    	
        String Sqlstr2 = " SELECT ORDER_DESC,STANDARD,DOSE_CODE,PRICE,QTY,TOTAL_AMT" +
        				 " FROM INS_OPD_ORDER" +
        				 " WHERE CASE_NO='" + this.getCaseNo() + "'" +
        				 " AND CONFIRM_NO='" + this.getConfirmNo() + "'" +
        				 " AND REGION_CODE = '" + this.getHospArea() + "'";
        TParm result2 = new TParm(TJDODBTool.getInstance().select(Sqlstr2)) ;
        TParm data2 = new TParm();
        DecimalFormat df2 = new DecimalFormat("##########0.0000");
        for (int i = 0; i < result2.getCount(); i++) {
        	data2.addData("ORDER_DESC", result2.getData("ORDER_DESC", i)) ;
        	data2.addData("STANDARD", result2.getData("STANDARD", i)) ;
        	data2.addData("DOSE_CODE", result2.getData("DOSE_CODE", i)) ;
        	data2.addData("PRICE", df2.format(result2.getData("PRICE", i))) ;
        	data2.addData("QTY", df.format(result2.getData("QTY", i))) ;
        	data2.addData("TOTAL_AMT", result1.getData("SUM2", 0)) ;
		}
        data2.setCount(data2.getCount("ORDER_DESC")) ;
        data2.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        data2.addData("SYSTEM", "COLUMNS", "STANDARD");
        data2.addData("SYSTEM", "COLUMNS", "DOSE_CODE");
        data2.addData("SYSTEM", "COLUMNS", "PRICE");
        data2.addData("SYSTEM", "COLUMNS", "QTY");
        data2.addData("SYSTEM", "COLUMNS", "TOTAL_AMT");
        data1.setData("DynamicTable", data2.getData()) ; 
        data1.setData("Doctor","TEXT",result1.getData("USER_NAME", 0)) ;
        
        this.openPrintDialog("%ROOT%\\config\\prt\\INS\\INSOpdOrderDetail.jhw",data1);
	}

	/**
	 * ��շ���.
	 */
	public void onClear() {
		this.setValue("MR_NO", "") ;
		this.setValue("NAME", "");
		this.setValue("SEX", "");
		this.setValue("IDENTITY", "");
		this.callFunction("UI|TTABLE|setParmValue",new TParm());
	}

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getConfirmNo() {
		return confirmNo;
	}

	public void setConfirmNo(String confirmNo) {
		this.confirmNo = confirmNo;
	}

	public String getHospArea() {
		return hospArea;
	}

	public void setHospArea(String hospArea) {
		this.hospArea = hospArea;
	}
}
