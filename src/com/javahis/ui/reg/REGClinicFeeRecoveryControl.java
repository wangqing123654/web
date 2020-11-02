package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
*
* <p>Title: 50元挂号费回收明细表</p>
*
* <p>Description: 50元挂号费回收明细表</p>
*
*
* <p>Company: bluecore</p>
*
* @author huangtt 20151118
* @version 4.0
*/
public class REGClinicFeeRecoveryControl extends TControl{
	
	TTable table;
	
	
	public void onInit(){
		table = (TTable) this.getComponent("TABLE");
		Timestamp today = SystemTool.getInstance().getDate();
		this.setValue("E_DATE",today.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        this.setValue("S_DATE", today.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		
	}
	
	
	public void onQuery(){
		
		boolean isDedug=true; //add by huangtt 20160505 日志输出
		try {
		
		String sDate = this.getValueString("S_DATE").replace("-", "").replace("/", "").replace(":","").replace(" ", "").substring(0,14 );
		String eDate = this.getValueString("E_DATE").replace("-", "").replace("/", "").replace(":","").replace(" ", "").substring(0,14 );
		String deptCode = this.getValueString("DEPT_CODE");
		String mrNo = this.getValueString("MR_NO");
		
		/*String sql = "  SELECT ROW_NUMBER () OVER (ORDER BY A.BILL_DATE) AS SEQ," +
				" B.MR_NO, C.PAT_NAME,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD HH24:MI:SS') BILL_DATE, COUNT (A.ORDER_CODE) USER_COUNT," +
				" A.AR_AMT, D.DEPT_CHN_DESC DEPT_DESC FROM OPD_ORDER A, REG_PATADM B," +
				" SYS_PATINFO C,SYS_DEPT D WHERE  A.ORDER_CODE = 'ZAB01017'" +
				" AND A.BILL_FLG = 'Y'" +
				" AND A.CASE_NO = B.CASE_NO" +
				" AND B.MR_NO = C.MR_NO" +
				" AND B.REALDEPT_CODE = D.DEPT_CODE" +
				" AND A.BILL_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+eDate+"', 'YYYYMMDDHH24MISS')" ;
		if(mrNo.length() > 0){
			sql += " AND B.MR_NO='"+mrNo+"'";
		}
		if(deptCode.length() > 0){
			sql += " AND B.REALDEPT_CODE='"+deptCode+"'";
		}
		sql += " GROUP BY B.MR_NO,C.PAT_NAME, A.BILL_DATE, A.AR_AMT, D.DEPT_CHN_DESC ORDER BY A.BILL_DATE";*/
		String sql = "  SELECT ROW_NUMBER () OVER (ORDER BY A.BILL_DATE) AS SEQ," +
		" A.MR_NO, C.PAT_NAME,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD HH24:MI:SS') BILL_DATE, COUNT (A.CASE_NO) USER_COUNT," +
		" A.PAY_TYPE07 AR_AMT, D.DEPT_CHN_DESC DEPT_DESC FROM BIL_OPB_RECP A, REG_PATADM B," +
		" SYS_PATINFO C,SYS_DEPT D WHERE  A.RESET_RECEIPT_NO IS NULL" +
		" AND A.PAY_TYPE07 > 0 " +
		" AND A.CASE_NO = B.CASE_NO" +
		" AND A.MR_NO = C.MR_NO" +
		" AND B.REALDEPT_CODE = D.DEPT_CODE" +
		" AND A.BILL_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDDHH24MISS')" +
		" AND TO_DATE ('"+eDate+"', 'YYYYMMDDHH24MISS')" ;
		if(mrNo.length() > 0){
			sql += " AND A.MR_NO='"+mrNo+"'";
		}
		if(deptCode.length() > 0){
			sql += " AND B.REALDEPT_CODE='"+deptCode+"'";
		}
		sql += " GROUP BY A.MR_NO,C.PAT_NAME, A.BILL_DATE, A.PAY_TYPE07, D.DEPT_CHN_DESC ORDER BY A.BILL_DATE";
		//System.out.println(sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()< 0){
			this.messageBox("没有要查询的数据");
			table.removeRowAll();
			return;
		}
		int count =0;
		double sum = 0;
		DecimalFormat df = new DecimalFormat("##########0.00");
		for (int i = 0; i < parm.getCount(); i++) {
			count += parm.getInt("USER_COUNT", i);
			sum += parm.getDouble("AR_AMT", i);
			parm.setData("AR_AMT", i, df.format(parm.getDouble("AR_AMT", i)));
		}
		String names[] = parm.getNames();
		for (int i = 0; i < names.length; i++) {
			parm.addData(names[i], "");
		}
		int row = parm.getCount("MR_NO");
		parm.setData("PAT_NAME", row-1, "总计");
		parm.setData("USER_COUNT", row-1, count);
		parm.setData("AR_AMT", row-1, df.format(sum));
		parm.setCount(row);
		table.setParmValue(parm);
		
		} catch (Exception e) {
			// TODO: handle exception
			if(isDedug){  
				System.out.println(" come in class: REGClinicFeeRecoveryControl.class ，method ：onQuery");
				e.printStackTrace();
			}
		}
		
	}
	
	public void onMrNo(){
		String mrNo = this.getValueString("MR_NO");
		this.setValue("MR_NO", PatTool.getInstance().checkMrno(mrNo));
		onQuery();
	}
	
	public void onClear(){
		this.clearValue("MR_NO;DEPT_CODE");
		Timestamp today = SystemTool.getInstance().getDate();
		this.setValue("E_DATE",today.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        this.setValue("S_DATE", today.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
        table.removeRowAll();
	}
	
	public void onPrint(){
		table.acceptText();
		TParm tableParm = table.getParmValue();
		if(tableParm.getCount("SEQ") < 0){
			this.messageBox("没有要打印的数据");
			return;
		}
		String sysDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		String sDate = StringTool.getString(
				TypeTool.getTimestamp(getValue("S_DATE")), "yyyy/MM/dd HH:mm:ss");
		String eDate = StringTool.getString(
				TypeTool.getTimestamp(getValue("E_DATE")), "yyyy/MM/dd HH:mm:ss");
		TParm printParm = new TParm();
		printParm.setData("TITLE1", "TEXT", Operator.getHospitalCHNShortName());
		printParm.setData("TITLE2", "TEXT", "代金券使用情况统计表");
		printParm.setData("DATE", "TEXT", sDate+" 至 "+eDate);
		printParm.setData("DEPT", "TEXT", this.getValueString("DEPT_CODE"));
		printParm.setData("DATE_PRINT", "TEXT", sysDate);
		printParm.setData("PRINT_USER", "TEXT", Operator.getName());
		String name[] = table.getParmMap().split(";");
		for (int i = 0; i < name.length; i++) {
			tableParm.addData("SYSTEM", "COLUMNS", name[i]);
		}
		printParm.setData("TABLE", tableParm.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\REG\\REGClinicFeeRecovery.jhw",printParm);
		
		
	}
	
	public void onExport(){
		table.acceptText();
		ExportExcelUtil.getInstance().exportExcel(table, "代金券使用情况统计表");
	}

}
