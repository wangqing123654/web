package com.javahis.ui.mem;

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
* <p>Title: 套餐销售/退费明细报表</p>
*
* <p>Description: 套餐销售/退费明细报表</p>
*
*
* <p>Company: bluecore</p>
*
* @author huangtt 20151118
* @version 4.0
*/
public class MEMPackageSalesOrReFeeControl extends TControl{
	
	TTable table;
	public void onInit(){
		table = (TTable) this.getComponent("TABLE");
		this.setValue("STATUS", "0");
		Timestamp today = SystemTool.getInstance().getDate();
		this.setValue("E_DATE",today.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        this.setValue("S_DATE", today.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
        initTable();
	}
	
	public void initTable(){
		String sql = "SELECT  A.PAYTYPE,A.GATHER_TYPE,B.CHN_DESC FROM BIL_GATHERTYPE_PAYTYPE A ,SYS_DICTIONARY B" +
		" WHERE A.GATHER_TYPE= B.ID AND B.GROUP_ID = 'GATHER_TYPE'"; 
		TParm payTypeParm = new TParm(TJDODBTool.getInstance().select(sql));
		String header = "序号,40;套餐编码,80;套餐名称,200;病案号,80;姓名,100;身份,150;客户来源,120,CUSTOMER_SOURCE;状态,60;购买/退费时间,140;购买/退费金额,100";
		String parmMap = "SEQ;PACKAGE_CODE;PACKAGE_DESC;MR_NO;PAT_NAME;CTZ_DESC;CUSTOMER_SOURCE;STATUS;BILL_DATE;AR_AMT";
		String aa = "1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,right;10,left";
		for (int i = 0; i < payTypeParm.getCount(); i++) {
			header = header + ";"+payTypeParm.getValue("CHN_DESC", i)+",100";
			parmMap = parmMap +";"+payTypeParm.getValue("PAYTYPE", i);
			aa = aa +";"+(10+i)+",right";
		}
		header += ";备注,150";
		parmMap += ";DESCRIPTION";
		aa = aa +";"+(10+payTypeParm.getCount())+",left";
		table.setHeader(header);
		table.setParmMap(parmMap);
		table.setColumnHorizontalAlignmentData(aa);
	}
	
	public void onClear(){
		table.removeRowAll();
		this.clearValue("MR_NO;PACKAGE_CODE;STATUS");
		this.setValue("STATUS", "0");
		Timestamp today = SystemTool.getInstance().getDate();
		this.setValue("E_DATE",today.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        this.setValue("S_DATE", today.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		
	}
	
	public void onMrNo(){
		String mrNo = this.getValueString("MR_NO");
		this.setValue("MR_NO", PatTool.getInstance().checkMrno(mrNo));
		onQuery();
	}
	
	
	public void onQuery(){
		boolean isDedug=true; //add by huangtt 20160505 日志输出
		try {
			
		
		String sDate = this.getValueString("S_DATE").replace("-", "").replace("/", "").replace(":","").replace(" ", "").substring(0,14 );
		String eDate = this.getValueString("E_DATE").replace("-", "").replace("/", "").replace(":","").replace(" ", "").substring(0,14 );
		String packageCode = this.getValueString("PACKAGE_CODE");
		String mrNo = this.getValueString("MR_NO");
		int status =Integer.parseInt(this.getValueString("STATUS")) ; // 0  全部   1 购买   2 退费
		
		String whereSql = "";
		if(mrNo.length() > 0){
			whereSql += " AND A.MR_NO='"+mrNo+"'";
		}
		if(packageCode.length() > 0){
			whereSql += " AND B.PACKAGE_CODE='"+packageCode+"'";
		}
		
		//购买套餐
		String salesSql = "SELECT DISTINCT B.PACKAGE_CODE,  C.PACKAGE_DESC,A.MR_NO,D.PAT_NAME," +
				" G.CTZ_DESC, E.CUSTOMER_SOURCE," +
				" CASE WHEN A.REST_FLAG = 'Y' THEN '退费' ELSE '购买' END STATUS," +
				" TO_CHAR(A.BILL_DATE,'YYYY/MM/DD HH24:MI:SS') BILL_DATE," +
				" A.AR_AMT, A.DESCRIPTION,A.PAY_TYPE01,A.PAY_TYPE02,A.PAY_TYPE03," +
				" A.PAY_TYPE04,A.PAY_TYPE05,A.PAY_TYPE06,A.PAY_TYPE07,A.PAY_TYPE08,A.PAY_TYPE09," +
				" A.PAY_TYPE10,A.PAY_TYPE11,A.PAY_MEDICAL_CARD" +
				" FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B,MEM_PACKAGE C,SYS_PATINFO D,MEM_PATINFO E, SYS_CTZ G" +
				" WHERE A.MR_NO =D.MR_NO AND A.TRADE_NO = B.TRADE_NO" +
				" AND D.CTZ1_CODE=G.CTZ_CODE(+) AND D.MR_NO = E.MR_NO" +
				"  AND B.PACKAGE_CODE = C.PACKAGE_CODE" +
				"  AND A.REST_FLAG = 'N'" +
				" AND A.BILL_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+eDate+"','YYYYMMDDHH24MISS')"+whereSql;
		
		//退费套餐
		String reFeeSql = " SELECT DISTINCT B.PACKAGE_CODE, C.PACKAGE_DESC, A.MR_NO,D.PAT_NAME," +
				" G.CTZ_DESC, E.CUSTOMER_SOURCE," +
				" CASE WHEN A.REST_FLAG = 'Y' THEN '退费' ELSE '购买' END STATUS," +
				" TO_CHAR(A.BILL_DATE,'YYYY/MM/DD HH24:MI:SS') BILL_DATE," +
				" A.AR_AMT, A.DESCRIPTION,A.PAY_TYPE01,A.PAY_TYPE02,A.PAY_TYPE03," +
				" A.PAY_TYPE04,A.PAY_TYPE05,A.PAY_TYPE06,A.PAY_TYPE07,A.PAY_TYPE08,A.PAY_TYPE09," +
				" A.PAY_TYPE10,A.PAY_TYPE11,A.PAY_MEDICAL_CARD" +
				" FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B,MEM_PACKAGE C,SYS_PATINFO D,MEM_PATINFO E, SYS_CTZ G" +
				" WHERE A.MR_NO =D.MR_NO AND A.TRADE_NO = B.REST_TRADE_NO AND A.REST_FLAG = 'Y'" +
				" AND D.CTZ1_CODE=G.CTZ_CODE(+) AND D.MR_NO = E.MR_NO" +
				"  AND B.PACKAGE_CODE = C.PACKAGE_CODE" +
				"  AND A.BILL_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+eDate+"', 'YYYYMMDDHH24MISS')"+whereSql;
		
		String sql = "";
		if(status == 0){
			sql = " SELECT ROW_NUMBER () OVER (ORDER BY A.MR_NO) AS SEQ, A.* FROM (" +
		       salesSql+" UNION ALL" +reFeeSql+" ) A ORDER BY A.MR_NO, A.BILL_DATE";
		}
		if(status == 1){
			sql = " SELECT ROW_NUMBER () OVER (ORDER BY A.MR_NO) AS SEQ, A.* FROM (" +
		       salesSql+" ) A ORDER BY A.MR_NO, A.BILL_DATE";
		}
		if(status == 2){
			sql = " SELECT ROW_NUMBER () OVER (ORDER BY A.MR_NO) AS SEQ, A.* FROM (" +
		       reFeeSql+" ) A ORDER BY A.MR_NO, A.BILL_DATE";
		}
		
//		System.out.println(sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() < 0){
			this.messageBox("没有要查询的数据");
			table.removeRowAll();
			return;
		}
		String names[] = parm.getNames();
		TParm tableParm = new TParm();
		int count = parm.getCount();
		DecimalFormat df = new DecimalFormat("##########0.00");
		double sum=0;
		double payType01=0;
		double payType02=0;
		double payType03=0;
		double payType04=0;
		double payType05=0;
		double payType06=0;
		double payType07=0;
		double payType08=0;
		double payType09=0;
		double payType10=0;
		double payType11=0;
		double sumAmt = 0;
		double sumPayType01=0;
		double sumPayType02=0;
		double sumPayType03=0;
		double sumPayType04=0;
		double sumPayType05=0;
		double sumPayType06=0;
		double sumPayType07=0;
		double sumPayType08=0;
		double sumPayType09=0;
		double sumPayType10=0;
		double sumPayType11=0;
		TParm rowParm = new TParm();
		for (int i = 0; i < count-1; i++) {
			parm.setData("AR_AMT", i, df.format(parm.getDouble("AR_AMT", i)));
			parm.setData("PAY_TYPE01", i, df.format(parm.getDouble("PAY_TYPE01", i)));
			parm.setData("PAY_TYPE02", i, df.format(parm.getDouble("PAY_TYPE02", i)));
			parm.setData("PAY_TYPE03", i, df.format(parm.getDouble("PAY_TYPE03", i)));
			parm.setData("PAY_TYPE04", i, df.format(parm.getDouble("PAY_TYPE04", i)));
			parm.setData("PAY_TYPE05", i, df.format(parm.getDouble("PAY_TYPE05", i)));
			parm.setData("PAY_TYPE06", i, df.format(parm.getDouble("PAY_TYPE06", i)));
			parm.setData("PAY_TYPE07", i, df.format(parm.getDouble("PAY_TYPE07", i)));
			parm.setData("PAY_TYPE08", i, df.format(parm.getDouble("PAY_TYPE08", i)));
			parm.setData("PAY_TYPE09", i, df.format(parm.getDouble("PAY_TYPE09", i)));
			parm.setData("PAY_TYPE10", i, df.format(parm.getDouble("PAY_TYPE10", i)));
			parm.setData("PAY_TYPE11", i, df.format(parm.getDouble("PAY_TYPE11", i)+parm.getDouble("PAY_MEDICAL_CARD", i)));
			sumAmt +=  parm.getDouble("AR_AMT", i);
			sumPayType01 +=  parm.getDouble("PAY_TYPE01", i);
			sumPayType02 +=  parm.getDouble("PAY_TYPE02", i);
			sumPayType03 +=  parm.getDouble("PAY_TYPE03", i);
			sumPayType04 +=  parm.getDouble("PAY_TYPE04", i);
			sumPayType05 +=  parm.getDouble("PAY_TYPE05", i);
			sumPayType06 +=  parm.getDouble("PAY_TYPE06", i);
			sumPayType07 +=  parm.getDouble("PAY_TYPE07", i);
			sumPayType08 +=  parm.getDouble("PAY_TYPE08", i);
			sumPayType09 +=  parm.getDouble("PAY_TYPE09", i);
			sumPayType10 +=  parm.getDouble("PAY_TYPE10", i);
			sumPayType11 +=  parm.getDouble("PAY_TYPE11", i);
			sumPayType11 +=  parm.getDouble("PAY_MEDICAL_CARD", i);
			tableParm.addRowData(parm, i);
			sum += parm.getDouble("AR_AMT", i);
			payType01 +=  parm.getDouble("PAY_TYPE01", i);
			payType02 +=  parm.getDouble("PAY_TYPE02", i);
			payType03 +=  parm.getDouble("PAY_TYPE03", i);
			payType04 +=  parm.getDouble("PAY_TYPE04", i);
			payType05 +=  parm.getDouble("PAY_TYPE05", i);
			payType06 +=  parm.getDouble("PAY_TYPE06", i);
			payType07 +=  parm.getDouble("PAY_TYPE07", i);
			payType08 +=  parm.getDouble("PAY_TYPE08", i);
			payType09 +=  parm.getDouble("PAY_TYPE09", i);
			payType10 +=  parm.getDouble("PAY_TYPE10", i);
			payType11 +=  parm.getDouble("PAY_TYPE11", i);
			payType11 +=  parm.getDouble("PAY_MEDICAL_CARD", i);
			if(!parm.getValue("MR_NO", i).equals(parm.getValue("MR_NO", i+1))){
				rowParm = new TParm();
				for (int j = 0; j < names.length; j++) {
					String s="";
					if("MR_NO".equals(names[j])){
						s="小计";
					}
					if("AR_AMT".equals(names[j])){
						s=df.format(sum)+"";
					}
					if("PAY_TYPE01".equals(names[j])){
						s=df.format(payType01)+"";
					}
					if("PAY_TYPE02".equals(names[j])){
						s=df.format(payType02)+"";
					}
					if("PAY_TYPE03".equals(names[j])){
						s=df.format(payType03)+"";
					}
					if("PAY_TYPE04".equals(names[j])){
						s=df.format(payType04)+"";
					}
					if("PAY_TYPE05".equals(names[j])){
						s=df.format(payType05)+"";
					}
					if("PAY_TYPE06".equals(names[j])){
						s=df.format(payType06)+"";
					}
					if("PAY_TYPE07".equals(names[j])){
						s=df.format(payType07)+"";
					}
					if("PAY_TYPE08".equals(names[j])){
						s=df.format(payType08)+"";
					}
					if("PAY_TYPE09".equals(names[j])){
						s=df.format(payType09)+"";
					}
					if("PAY_TYPE10".equals(names[j])){
						s=df.format(payType10)+"";
					}
					if("PAY_TYPE11".equals(names[j])){
						s=df.format(payType11)+"";
					}
										
					rowParm.addData(names[j], s);
				}
				tableParm.addRowData(rowParm, 0);
				sum=0;
				payType01=0;
				payType02=0;
				payType03=0;
				payType04=0;
				payType05=0;
				payType06=0;
				payType07=0;
				payType08=0;
				payType09=0;
				payType10=0;
				payType11=0;
			}	
		}
		parm.setData("AR_AMT", count-1, df.format(parm.getDouble("AR_AMT", count-1)));
		for (int i = 1; i < 11; i++) {
			String payName = "PAY_TYPE";
			if(i < 10 ){
				payName = payName + "0"+i;
			}else{
				payName = payName +i;
			}
			parm.setData(payName, count-1, df.format(parm.getDouble(payName, count-1)));
		}
		
		parm.setData("PAY_TYPE11", count-1, df.format(parm.getDouble("PAY_TYPE11", count-1)+parm.getDouble("PAY_MEDICAL_CARD", count-1)));
		sumAmt +=  parm.getDouble("AR_AMT", count-1);
		sumPayType01 +=  parm.getDouble("PAY_TYPE01", count-1);
		sumPayType02 +=  parm.getDouble("PAY_TYPE02", count-1);
		sumPayType03 +=  parm.getDouble("PAY_TYPE03", count-1);
		sumPayType04 +=  parm.getDouble("PAY_TYPE04", count-1);
		sumPayType05 +=  parm.getDouble("PAY_TYPE05", count-1);
		sumPayType06 +=  parm.getDouble("PAY_TYPE06", count-1);
		sumPayType07 +=  parm.getDouble("PAY_TYPE07", count-1);
		sumPayType08 +=  parm.getDouble("PAY_TYPE08", count-1);
		sumPayType09 +=  parm.getDouble("PAY_TYPE09", count-1);
		sumPayType10 +=  parm.getDouble("PAY_TYPE10", count-1);
		sumPayType11 +=  parm.getDouble("PAY_TYPE11", count-1);
		sumPayType11 +=  parm.getDouble("PAY_MEDICAL_CARD", count-1);
		tableParm.addRowData(parm, count-1);
		sum += parm.getDouble("AR_AMT", count-1);
		payType01 +=  parm.getDouble("PAY_TYPE01", count-1);
		payType02 +=  parm.getDouble("PAY_TYPE02", count-1);
		payType03 +=  parm.getDouble("PAY_TYPE03", count-1);
		payType04 +=  parm.getDouble("PAY_TYPE04", count-1);
		payType05 +=  parm.getDouble("PAY_TYPE05", count-1);
		payType06 +=  parm.getDouble("PAY_TYPE06", count-1);
		payType07 +=  parm.getDouble("PAY_TYPE07", count-1);
		payType08 +=  parm.getDouble("PAY_TYPE08", count-1);
		payType09 +=  parm.getDouble("PAY_TYPE09", count-1);
		payType10 +=  parm.getDouble("PAY_TYPE10", count-1);
		payType11 +=  parm.getDouble("PAY_TYPE11", count-1);
		payType11 +=  parm.getDouble("PAY_MEDICAL_CARD", count-1);
		rowParm = new TParm();
		for (int j = 0; j < names.length; j++) {
			String s="";
			if("MR_NO".equals(names[j])){
				s="小计";
			}
			if("AR_AMT".equals(names[j])){
				s=df.format(sum)+"";
			}
			if("PAY_TYPE01".equals(names[j])){
				s=df.format(payType01)+"";
			}
			if("PAY_TYPE02".equals(names[j])){
				s=df.format(payType02)+"";
			}
			if("PAY_TYPE03".equals(names[j])){
				s=df.format(payType03)+"";
			}
			if("PAY_TYPE04".equals(names[j])){
				s=df.format(payType04)+"";
			}
			if("PAY_TYPE05".equals(names[j])){
				s=df.format(payType05)+"";
			}
			if("PAY_TYPE06".equals(names[j])){
				s=df.format(payType06)+"";
			}
			if("PAY_TYPE07".equals(names[j])){
				s=df.format(payType07)+"";
			}
			if("PAY_TYPE08".equals(names[j])){
				s=df.format(payType08)+"";
			}
			if("PAY_TYPE09".equals(names[j])){
				s=df.format(payType09)+"";
			}
			if("PAY_TYPE10".equals(names[j])){
				s=df.format(payType10)+"";
			}
			if("PAY_TYPE11".equals(names[j])){
				s=df.format(payType11)+"";
			}
			rowParm.addData(names[j], s);
		}
		tableParm.addRowData(rowParm, 0);
		rowParm = new TParm();
		for (int j = 0; j < names.length; j++) {
			String s="";
			if("MR_NO".equals(names[j])){
				s="总计";
			}
			if("AR_AMT".equals(names[j])){
				s=df.format(sumAmt)+"";
			}
			if("PAY_TYPE01".equals(names[j])){
				s=df.format(sumPayType01)+"";
			}
			if("PAY_TYPE02".equals(names[j])){
				s=df.format(sumPayType02)+"";
			}
			if("PAY_TYPE03".equals(names[j])){
				s=df.format(sumPayType03)+"";
			}
			if("PAY_TYPE04".equals(names[j])){
				s=df.format(sumPayType04)+"";
			}
			if("PAY_TYPE05".equals(names[j])){
				s=df.format(sumPayType05)+"";
			}
			if("PAY_TYPE06".equals(names[j])){
				s=df.format(sumPayType06)+"";
			}
			if("PAY_TYPE07".equals(names[j])){
				s=df.format(sumPayType07)+"";
			}
			if("PAY_TYPE08".equals(names[j])){
				s=df.format(sumPayType08)+"";
			}
			if("PAY_TYPE09".equals(names[j])){
				s=df.format(sumPayType09)+"";
			}
			if("PAY_TYPE10".equals(names[j])){
				s=df.format(sumPayType10)+"";
			}
			if("PAY_TYPE11".equals(names[j])){
				s=df.format(sumPayType11)+"";
			}
			rowParm.addData(names[j], s);
		}
		tableParm.addRowData(rowParm, 0);
		table.setParmValue(tableParm);
		} catch (Exception e) {
			// TODO: handle exception
			if(isDedug){  
				System.out.println(" come in class: MEMPackageSalesOrReFeeControl ，method ：onQuery");
				e.printStackTrace();
			}
		}
	
	}
	
	public void onExport(){
		table.acceptText();
		
		ExportExcelUtil.getInstance().exportExcel(table, "套餐销售/退费明细表");
	}
	
	public void onPrint(){
		table.acceptText();
		TParm tableParm = table.getShowParmValue();
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
		printParm.setData("TITLE2", "TEXT", "套餐销售/退费明细表");
		printParm.setData("DATE", "TEXT", sDate+" 至 "+eDate);
		printParm.setData("DATE_PRINT", "TEXT", sysDate);
		printParm.setData("PRINT_USER", "TEXT", Operator.getName());
		String name[] = table.getParmMap().split(";");
		//SEQ;PACKAGE_CODE;PACKAGE_DESC;MR_NO;PAT_NAME;CTZ_DESC;CUSTOMER_SOURCE;STATUS;BILL_DATE;AR_AMT;DESCRIPTION
		for (int i = 0; i < name.length; i++) {
			
			tableParm.addData("SYSTEM", "COLUMNS", name[i]);
		}
		printParm.setData("TABLE", tableParm.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMPackageSalesOrReFee.jhw",printParm);
		
		
	}
}
