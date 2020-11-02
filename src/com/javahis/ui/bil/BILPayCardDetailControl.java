package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DecimalFormat;
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
 * Title: 财务报表
 * </p>
 * 
 * <p>
 * Description: 财务报表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author huangtt 2016.07.04
 * @version 1.0
 */
public class BILPayCardDetailControl extends TControl{
	
	TTable table1;
	TTable table2;
	TTable table3;
	String whereU="";
	String businessNo="";
	String businessNo2="";
	String s="";
	String remark="";
	public void onInit(){
		setStartEndDate();
		table1 = (TTable) this.getComponent("TABLE1");
		table2 = (TTable) this.getComponent("TABLE2");
		table3 = (TTable) this.getComponent("TABLE3");

		
	}
	
	public void onQuery(){
		boolean flg=false;
		DecimalFormat df1 = new DecimalFormat("####.00");
		//==start== modify by kangy20171011 显示所有支付方式，微信支付宝显示交易号
		String typeSql =
            " SELECT ID, CHN_DESC AS NAME  " +
            "   FROM SYS_DICTIONARY  WHERE GROUP_ID = 'BIL_PAYTYPE' ORDER BY ID";
    	TParm typeParm=new TParm(TJDODBTool.getInstance().select(typeSql));
    	TParm typeStringParm=new TParm();
    	typeStringParm.setData("C0","A.PAY_TYPE01");
    	typeStringParm.setData("C1","A.PAY_TYPE02");
    	typeStringParm.setData("T0","A.PAY_TYPE06");
    	typeStringParm.setData("C4","A.PAY_TYPE04");
    	typeStringParm.setData("EKT","A.PAY_MEDICAL_CARD-A.PAY_OTHER4-A.PAY_OTHER3");
    	typeStringParm.setData("GBE","A.PAY_OTHER1-A.PAY_OTHER4-A.PAY_OTHER3");
    	typeStringParm.setData("LPK","A.PAY_OTHER3+A.PAY_TYPE05");
    	typeStringParm.setData("XJZKQ","A.PAY_OTHER4+A.PAY_TYPE07");
    	typeStringParm.setData("BXZF","A.PAY_TYPE08");
    	typeStringParm.setData("TC","A.MEM_PACK_FLG ='Y' AND A.TOT_AMT");
    	typeStringParm.setData("WX","A.PAY_TYPE09");
    	typeStringParm.setData("ZFB","A.PAY_TYPE10");
    	whereU="";
		String startTime = getValueString("S_DATE");
		String endTime = getValueString("E_DATE");
		startTime = startTime.substring(0, 4)+startTime.substring(5, 7)+startTime.substring(8, 10)+startTime.substring(11, 13)+startTime.substring(14, 16)+startTime.substring(17, 19);
		endTime = endTime.substring(0, 4)+endTime.substring(5, 7)+endTime.substring(8, 10)+endTime.substring(11, 13)+endTime.substring(14, 16)+endTime.substring(17, 19);
		String sql1="SELECT * FROM (";
		for(int i=0;i<typeParm.getCount();i++){
		if (typeParm.getValue("ID",i).equals("TCZK") ||
				typeParm.getValue("ID",i).equals("YJJ")
				||typeParm.getValue("ID",i).equals("JM")) {
			continue;
		}
		if (null!=this.getValue("BIL_PAYTYPE")&&this.getValueString("BIL_PAYTYPE").length()>0) {
    		if (!typeParm.getValue("ID",i).equals(this.getValue("BIL_PAYTYPE"))) {
    			continue;
			}
		}
		if("C0".equals(typeParm.getValue("ID",i)))//现金
			remark=" A.REMARK01 ";
		if("C1".equals(typeParm.getValue("ID",i)))//刷卡
			remark=" A.REMARK02 ";
		if("T0".equals(typeParm.getValue("ID",i)))//支票
			remark=" A.REMARK06 ";
		if("LPK".equals(typeParm.getValue("ID",i)))//礼品卡
			remark=" A.REMARK05 ";
		if("XJZKQ".equals(typeParm.getValue("ID",i)))//现金折扣券
			remark=" A.REMARK07 ";
		if("EKT".equals(typeParm.getValue("ID",i)))//医疗卡
			remark=" '' ";
		if("GBK".equals(typeParm.getValue("ID",i)))//特批款
			remark=" '' ";
		if("BXZF".equals(typeParm.getValue("ID",i)))//保险支付
			remark=" A.REMARK08 ";
		if("C4".equals(typeParm.getValue("ID",i)))//医院垫付
			remark=" A.REMARK04 ";
		if("TC".equals(typeParm.getValue("ID",i)))//套餐结转
			remark=" '' ";
		if("WX".equals(typeParm.getValue("ID",i)))//微信
			remark=" A.REMARK09 ";
		if("ZFB".equals(typeParm.getValue("ID",i)))//支付宝
			remark=" A.REMARK10 ";
		if (typeParm.getValue("ID",i).equals("TC")) {
		if(flg)
			whereU=" UNION ALL ";
		flg=true;
	
		sql1 +=whereU+ "SELECT A.MR_NO ,B.PAT_NAME , " +
		" TO_CHAR(A.BILL_DATE,'YYYY/MM/DD HH24:MI:SS') BILL_DATE ," +
		" '"+typeParm.getValue("NAME",i)+"' BILL_TYPE,  A.TOT_AMT AMT , "+remark+" MEMO,"+businessNo +
		" FROM BIL_OPB_RECP A, SYS_PATINFO B" +
		" WHERE A.MR_NO = B.MR_NO AND "+typeStringParm.getValue(typeParm.getValue("ID",i))+" > 0" +
		" AND A.RESET_RECEIPT_NO IS NULL" +
		" AND A.BILL_DATE BETWEEN TO_DATE('"+startTime+"','YYYYMMDDHH24MISS') " +
		" AND TO_DATE('"+endTime+"','YYYYMMDDHH24MISS')"+
		" AND "+typeStringParm.getValue(typeParm.getValue("ID",i))+">0 ";
				
		}else{
			if(flg)
				whereU=" UNION ALL ";
			flg=true;
			if("WX".equals(typeParm.getValue("ID",i))||"ZFB".equals(typeParm.getValue("ID",i))){
				businessNo=" A."+typeParm.getValue("ID",i)+"_BUSINESS_NO BUSINESS_NO ";
			}else{
				businessNo=" '' BUSINESS_NO ";
			}
				sql1 +=whereU+ "SELECT A.MR_NO ,B.PAT_NAME , " +
					" TO_CHAR(A.BILL_DATE,'YYYY/MM/DD HH24:MI:SS') BILL_DATE ," +
					" '"+typeParm.getValue("NAME",i)+"' BILL_TYPE,  "+typeStringParm.getValue(typeParm.getValue("ID",i))+" AMT , "+remark+" MEMO,"+businessNo +
					" FROM BIL_OPB_RECP A, SYS_PATINFO B" +
					" WHERE A.MR_NO = B.MR_NO AND "+typeStringParm.getValue(typeParm.getValue("ID",i))+" > 0" +
					" AND A.RESET_RECEIPT_NO IS NULL" +
					" AND A.BILL_DATE BETWEEN TO_DATE('"+startTime+"','YYYYMMDDHH24MISS') " +
					" AND TO_DATE('"+endTime+"','YYYYMMDDHH24MISS')";
		}
		} 
		sql1+=")  ORDER BY BILL_DATE";
		flg=false;
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
		if(parm1.getCount() < 0){
			table1.removeRowAll();			
		}
		typeStringParm=new TParm();
		/*typeStringParm.setData("C0","A.PAY_TYPE01");
		typeStringParm.setData("C1","A.PAY_TYPE02");
		typeStringParm.setData("T0","A.PAY_CHECK");
		typeStringParm.setData("LPK","A.PAY_GIFT_CARD");
		typeStringParm.setData("XJZKQ","A.PAY_DISCNT_CARD");
		typeStringParm.setData("BXZF","A.PAY_BXZF");
		typeStringParm.setData("C4","A.PAY_DEBIT");
		typeStringParm.setData("WX","A.PAY_TYPE09");
		typeStringParm.setData("ZFB","A.PAY_TYPE10");*/
		
	   	typeStringParm.setData("C0","A.PAY_TYPE01");
    	typeStringParm.setData("C1","A.PAY_TYPE02");
    	typeStringParm.setData("T0","A.PAY_TYPE06");
    	typeStringParm.setData("C4","A.PAY_TYPE04");
    	typeStringParm.setData("LPK","A.PAY_TYPE05");
    	typeStringParm.setData("XJZKQ","A.PAY_TYPE07");
    	typeStringParm.setData("BXZF","A.PAY_TYPE08");
    	typeStringParm.setData("WX","A.PAY_TYPE09");
    	typeStringParm.setData("ZFB","A.PAY_TYPE10");
		
		String sql2="SELECT * FROM (";
		whereU="";
		for(int i=0;i<typeParm.getCount();i++){
			if (null==typeStringParm.getValue(typeParm.getValue("ID",i))||
					typeStringParm.getValue(typeParm.getValue("ID",i)).length()<=0) {
				continue;
			}
			if (null!=this.getValue("BIL_PAYTYPE")&&this.getValueString("BIL_PAYTYPE").length()>0) {
	    		if (!typeParm.getValue("ID",i).equals(this.getValue("BIL_PAYTYPE"))) {
	    			continue;
				}
	    		}
			if(flg)
				whereU=" UNION ALL ";
			flg=true;
			if("WX".equals(typeParm.getValue("ID",i))||"ZFB".equals(typeParm.getValue("ID",i))){
				businessNo=" A."+typeParm.getValue("ID",i)+"_BUSINESS_NO BUSINESS_NO ";
			}else{
				businessNo=" '' BUSINESS_NO ";
			}
			 s=typeStringParm.getValue(typeParm.getValue("ID",i));
			if(!s.substring(s.length()-2,s.length()-1).equals("0")){
				s=s.substring(s.length()-2);
			}else{
				s=s.substring(s.length()-1);
			}
		 sql2 +=whereU+ "SELECT  A.TRADE_NO, A.MR_NO , C.PAT_NAME ," +
				" TO_CHAR(A.BILL_DATE,'YYYY/MM/DD HH24:MI:SS') BILL_DATE ," +
				" '"+typeParm.getValue("NAME",i)+"' BILL_TYPE, "+typeStringParm.getValue(typeParm.getValue("ID",i))+" AMT, A.MEMO"+s+
						" MEMO,"+businessNo +
				" FROM MEM_PACKAGE_TRADE_M A,SYS_PATINFO C " +
				" WHERE A.MR_NO = C.MR_NO" +
				" AND "+typeStringParm.getValue(typeParm.getValue("ID",i))+" <> 0" +
				" AND A.BILL_DATE BETWEEN TO_DATE('"+startTime+"','YYYYMMDDHH24MISS') " +
				" AND TO_DATE('"+endTime+"','YYYYMMDDHH24MISS')";
				
		 }
		sql2+=") ORDER BY BILL_DATE";
		flg=false;
		TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
		if(parm2.getCount() < 0){
			table2.removeRowAll();			
		}
		typeStringParm=new TParm();
		typeStringParm.setData("C0","A.PAY_CASH");
		typeStringParm.setData("C1","A.PAY_BANK_CARD");
		typeStringParm.setData("T0","A.PAY_CHECK");
		typeStringParm.setData("LPK","A.PAY_GIFT_CARD");
		typeStringParm.setData("XJZKQ","A.PAY_DISCNT_CARD");
		typeStringParm.setData("YJJ","A.PAY_BILPAY");
		typeStringParm.setData("BXZF","A.PAY_BXZF");
		typeStringParm.setData("C4","A.PAY_DEBIT");
		typeStringParm.setData("WX","A.PAY_TYPE09");
		typeStringParm.setData("ZFB","A.PAY_TYPE10");
		String sql3="SELECT * FROM (";
		 whereU="";
		for(int i=0;i<typeParm.getCount();i++){
			if (null==typeStringParm.getValue(typeParm.getValue("ID",i))||
					typeStringParm.getValue(typeParm.getValue("ID",i)).length()<=0) {
				continue;
			}
			if (null!=this.getValue("BIL_PAYTYPE")&&this.getValueString("BIL_PAYTYPE").length()>0) {
	    		if (!typeParm.getValue("ID",i).equals(this.getValue("BIL_PAYTYPE"))) {
	    			continue;
				}
	    		}
			if(flg)
				whereU=" UNION ALL ";
			flg=true;
			if("WX".equals(typeParm.getValue("ID",i))||"ZFB".equals(typeParm.getValue("ID",i))){
				businessNo=" A."+typeParm.getValue("ID",i)+"_BUSINESS_NO BUSINESS_NO ";
				businessNo2=" ,A."+typeParm.getValue("ID",i)+"_BUSINESS_NO ";
			}else{
				businessNo=" '' BUSINESS_NO ";
				businessNo2="";

			}
			if("C0".equals(typeParm.getValue("ID",i)))//现金
				remark=" A.MEMO1 ";
			if("C1".equals(typeParm.getValue("ID",i)))//刷卡
				//remark=" SUBSTR (A.MEMO2, INSTR (A.MEMO2, '#') + 1) ";
				remark=" A.MEMO2 ";
			if("T0".equals(typeParm.getValue("ID",i)))//支票
				remark=" A.MEMO6 ";
			if("LPK".equals(typeParm.getValue("ID",i)))//礼品卡
				remark=" A.MEMO5 ";
			if("XJZKQ".equals(typeParm.getValue("ID",i)))//现金折扣券
				remark=" A.MEMO7 ";
			if("YJJ".equals(typeParm.getValue("ID",i)))//预交金
				remark=" A.MEMO3 ";
			if("BXZF".equals(typeParm.getValue("ID",i)))//保险支付
				remark=" A.MEMO8 ";
			if("C4".equals(typeParm.getValue("ID",i)))//医院垫付
				remark=" A.MEMO4 ";
			if("WX".equals(typeParm.getValue("ID",i)))//微信
				remark=" A.MEMO9 ";
			if("ZFB".equals(typeParm.getValue("ID",i)))//支付宝
				remark=" A.MEMO10 ";		
			
			sql3+=whereU+ "SELECT MR_NO, PAT_NAME," +
				" TO_CHAR (ACCOUNT_DATE, 'YYYY/MM/DD HH24:MI:SS') BILL_DATE," +
				" '"+typeParm.getValue("NAME",i)+"' AS BILL_TYPE, PAY AS AMT,  MEMO, BUSINESS_NO " +
				" FROM (  SELECT A.MR_NO, B.PAT_NAME, A.ACCOUNT_DATE," +
				" SUM ("+typeStringParm.getValue(typeParm.getValue("ID",i))+") AS PAY, "+remark+" MEMO,"+businessNo +
				" FROM BIL_IBS_RECPM A, SYS_PATINFO B" +
				" WHERE  CHARGE_DATE BETWEEN TO_DATE ('"+startTime+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+endTime+"','YYYYMMDDHH24MISS')" +
				" AND A.ADM_TYPE = 'I' AND A.MR_NO = B.MR_NO" +
				" AND A.AR_AMT > 0 AND A.RESET_RECEIPT_NO IS NULL" +
				" AND "+typeStringParm.getValue(typeParm.getValue("ID",i))+" > 0" +
				" GROUP BY A.MR_NO, B.PAT_NAME, A.ACCOUNT_DATE, "+remark+businessNo2+" ) WHERE PAY > 0";
			remark=" ";
		}
		sql3+=") A ORDER BY  BILL_DATE ";
		flg=false;
		//==end== modify by kangy20171011 显示所有支付方式，微信支付宝显示交易号
		TParm parm3 = new TParm(TJDODBTool.getInstance().select(sql3));
		if(parm3.getCount() < 0){
			table3.removeRowAll();			
		}
		
		if(parm1.getCount() < 0 && parm2.getCount() < 0 && parm3.getCount() < 0 ){
			this.messageBox("没有要查询的数据");
			return;
		}
		
		table1.setParmValue(parm1);
		table2.setParmValue(parm2);
		table3.setParmValue(parm3);
		
	}
	
	public void onClear(){
		setStartEndDate();
		table1.removeRowAll();			
		table2.removeRowAll();			
		table3.removeRowAll();			
	}
	
	public void onExport(){
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TABLE1|getThis");
		
		ExportExcelUtil.getInstance().exportExcel(table, "门急诊客户收费明细表");
		table = (TTable) callFunction("UI|TABLE2|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "套餐销售收费明细表");
		table = (TTable) callFunction("UI|TABLE3|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "住院客户收费明细表");
	}
	
	
	
	/**
    *
    * 设置起始时间和结束时间，上月26-本月25
    */
   private void setStartEndDate(){
   	Timestamp date = TJDODBTool.getInstance().getDBTime();
       // 结束时间(本月的25)
       Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
                                                    substring(0, 4) + "/" +
                                                    TypeTool.getString(date).
                                                    substring(5, 7) +
                                                    "/25 23:59:59",
                                                    "yyyy/MM/dd HH:mm:ss");
       setValue("E_DATE", dateTime);
       // 起始时间(上个月26)
       Calendar cd = Calendar.getInstance();
       cd.setTimeInMillis(date.getTime());
       cd.add(Calendar.MONTH, -1);
       Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());
       setValue("S_DATE",
       		endDateTimestamp.toString().substring(0, 4) +
                "/" +
                endDateTimestamp.toString().substring(5, 7) +
                "/26 00:00:00");
   }

}
