package com.javahis.ui.ekt;

import java.text.DecimalFormat;
import java.util.Date;

import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 新版医疗卡充值打印版本
 * </p>
 * 
 * <p>
 * Description: 新版医疗卡充值打印版本
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author sunqy 20140611
 * @version 1.0
 */

public class EKTReceiptPrintControl{
	
	private static EKTReceiptPrintControl print;
	private String returnFee;//退费的"退"
	private String returnO;//退费的"o"
	public EKTReceiptPrintControl() {
	}
	
	public static EKTReceiptPrintControl getInstance(){
		if(print == null){
			print = new EKTReceiptPrintControl();
		}
		return print;
	}
	public void onPrint(TTable table,TParm parmSum,String copy,int row,Pat pat,boolean flg, TControl tc){
//		System.out.println(SystemTool.getInstance().getDate());
		DecimalFormat df=new DecimalFormat("#0.00");
		TParm parm = new TParm();
		parm.setData("TITLE", "TEXT", parmSum.getData("TITLE"));
//		if (null == bil_business_no)
//			bil_business_no = EKTTool.getInstance().getBillBusinessNo();// 补印操作
//		parm.setData("RecNO", "TEXT", bil_business_no);// 收据号
		parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); // 姓名
		parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); // 病案号
		String sql = "SELECT B.CHN_DESC GATHER_TYPE,A.BIL_BUSINESS_NO RECNO,A.DESCRIPTION,A.CARD_TYPE,A.GATHER_TYPE TYPE,A.PRINT_NO " +
				"FROM SYS_DICTIONARY B,EKT_BIL_PAY A,EKT_ISSUELOG C " + 
				"WHERE A.GATHER_TYPE = B.ID " +
				"AND B.GROUP_ID = 'GATHER_TYPE' " +
				"AND A.CARD_NO = C.CARD_NO " +
				" AND A.CARD_NO='"+parmSum.getValue("CARD_NO")+"' AND A.BIL_BUSINESS_NO='"+parmSum.getValue("BIL_BUSINESS_NO")+"'"+
				"ORDER BY A.BIL_BUSINESS_NO DESC";
		//System.out.println("sql:::"+sql);
//		if(pat.getRcntIpdDept().length()>0){
//			sql += " AND A.DEPT_CODE = '"+pat.getRcntIpdDept()+"'";
//		}
//		if(parmSum.getValue("GATHER_TYPE").length()>0){
//			sql += " AND B.ID = '"+parmSum.getValue("GATHER_TYPE")+"'";
//		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		result = result.getRow(0);
//		tc.messageBox("result = "+result);
		parm.setData("RecNO", "TEXT", SystemTool.getInstance().getNo("ALL", "EKT", "MEM_NO", "MEM_NO")); //票据号
		parm.setData("DEPT_CODE", "TEXT", result.getValue("DEPT_CODE"));// 科别
		double money = parmSum.getDouble("BUSINESS_AMT")==0.0 ? parmSum.getDouble("AMT") : Double.parseDouble(parmSum.getValue("BUSINESS_AMT"));
//		tc.messageBox("money0==="+parmSum.getDouble("BUSINESS_AMT"));
//		tc.messageBox("money1==="+Double.parseDouble(parmSum.getValue("AMT")));
//		tc.messageBox("money2==="+money);
		parm.setData("MONEY", "TEXT", df.format(StringTool.round(money, 2))+"元"); // 金额
		parm.setData("CAPITAL", "TEXT", StringUtil.getInstance().numberToWord(money)); // 大写金额
//		if(!result.getValue("CARD_TYPE").equals("")){
//			System.out.println("select chn_desc from sys_dictionary where GROUP_ID='SYS_CARDTYPE' and id='"+result.getValue("CARD_TYPE")+"'");
//			TParm data=new TParm(TJDODBTool.getInstance().select("select chn_desc from sys_dictionary where GROUP_ID='SYS_CARDTYPE' and id='"+result.getValue("CARD_TYPE")+"'"));
//			data.getRow(0);
//			System.out.println(":::"+data.getValue("chn_desc"));
//			parm.setData("CARD_TYPE","TEXT",data.getValue("CHN_DESC"));
//		}
		//System.out.println(":::"+result.getValue("TYPE"));
		if("C1".equals(result.getValue("TYPE"))){//由于退费没有卡类型，所以要和充值区分开，add by huangjw 20150115
			/*if(parmSum.getData("UnFeeFLG")!=null && parmSum.getData("UnFeeFLG").equals("Y")){
				parm.setData("ACOUNT_NO", "TEXT", result.getValue("DESCRIPTION"));// 账号
			}else{*/
				String cardSql="select chn_desc from sys_dictionary where group_id='SYS_CARDTYPE' and id='"+result.getValue("CARD_TYPE")+"'";
				TParm param=new TParm(TJDODBTool.getInstance().select(cardSql));
				parm.setData("CARD_TYPE","TEXT",param.getValue("CHN_DESC",0));//添加卡类型 add by huangjw 20141231
				parm.setData("ACOUNT_NO", "TEXT", result.getValue("DESCRIPTION"));// 账号
			//}
			
		}
		//==start=== add by kangy 20171024 微信支付宝交易添加卡类型备注交易号
		if("WX".equals(result.getValue("TYPE"))||"ZFB".equals(result.getValue("TYPE"))){
			String cardSql="select chn_desc from sys_dictionary where group_id='SYS_CARDTYPE' and id='"+result.getValue("CARD_TYPE")+"'";
			TParm param=new TParm(TJDODBTool.getInstance().select(cardSql));
			if(result.getValue("DESCRIPTION").length()>0){
				parm.setData("CARD_TYPE","TEXT",param.getValue("CHN_DESC",0)+" 备注:"+result.getValue("DESCRIPTION"));
			}else
			parm.setData("CARD_TYPE","TEXT",param.getValue("CHN_DESC",0));
			parm.setData("ACOUNT_NO", "TEXT", "交易号:"+result.getValue("PRINT_NO"));
		}
		//==end=== add by kangy 20171024 微信支付宝交易添加卡类型备注交易号
		
		if (table!=null && table.getRowCount() > 0) {
//			tc.messageBox("有表格数据");
			if(parmSum.getData("UnFeeFLG")!=null && parmSum.getData("UnFeeFLG").equals("Y")){
				String payType = result.getValue("GATHER_TYPE") + "：" + df.format(table.getValueAt(row, 3))+"元";// 将得到的支付方式与支付金额合并
				parm.setData("PAY_TYPE", "TEXT", payType);// 支付方式
			}else{
				String payType = result.getValue("GATHER_TYPE") + "：" + df.format(table.getValueAt(row, 3))+"元";// 将得到的支付方式与支付金额合并
				parm.setData("PAY_TYPE", "TEXT", payType);// 支付方式
			}
		}else{
//			tc.messageBox("有表格数据");
			if(parmSum.getValue("UnFeeFLG").equals("Y")){
				String payType = result.getValue("GATHER_TYPE") + "：" + df.format(tc.getValueDouble("UN_FEE"))+"元";
				parm.setData("PAY_TYPE", "TEXT", payType);// 支付方式
			}else{
				if(row!=-1){
					String payType = result.getValue("GATHER_TYPE") + "：" + df.format(tc.getValueDouble("TOP_UPFEE"))+"元";// 将得到的支付方式与支付金额合并
					parm.setData("PAY_TYPE", "TEXT", payType);// 支付方式
				}else{//门诊挂号处新增病患传入设定row==-1
					if(tc.getValue("PROCEDURE_PRICE1")!=null){//医疗卡补卡/挂失处收费
						String payType = result.getValue("GATHER_TYPE") + "：" + df.format(tc.getValueDouble("TOP_UP_PRICE"))+"元";// 将得到的支付方式与支付金额合并
						parm.setData("PAY_TYPE", "TEXT", payType);// 支付方式
					}else{
						String payType = result.getValue("GATHER_TYPE") + "：" + df.format(tc.getValueDouble("GATHER_PRICE"))+"元";// 将得到的支付方式与支付金额合并
						parm.setData("PAY_TYPE", "TEXT", payType);// 支付方式
					}
				}
			}
		}
		String date = StringTool.getTimestamp(new Date()).toString().substring(
				0, 19).replace('-', '/');
		parm.setData("DATE", "TEXT", date);// 日期
		parm.setData("OP_NAME", "TEXT", Operator.getID()); // 收款人
		if(flg == false){//是否为退费操作判断标识
			returnFee = "";
			returnO = "";
		}else{
			returnFee = "退";
			returnO = "o";
		}
		parm.setData("RETURN", "TEXT", returnFee); // 退
		parm.setData("o", "TEXT", returnO);// 退
		parm.setData("COPY", "TEXT", copy); // 补印注记
//		tc.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTReceiptV45.jhw",
//				parm, true);
		parm.setData("HOSP_NAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalCHNFullName() : "所有医院");
        parm.setData("HOSP_ENAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalENGFullName() : "ALL HOSPITALS");
		String prtSwitch=IReportTool.getInstance().getPrintSwitch("EKTReceiptV45.prtSwitch");
//		System.out.println(SystemTool.getInstance().getDate());
		if(IReportTool.ON.equals(prtSwitch)){			
			tc.openPrintWindow(IReportTool.getInstance().getReportPath(
			"EKTReceiptV45.jhw"), IReportTool.getInstance().getReportParm("EKTReceiptV45.class",parm));
//			System.out.println(SystemTool.getInstance().getDate());
		}
		
		// add by sunqy 201406010 ----end----
	}
}
