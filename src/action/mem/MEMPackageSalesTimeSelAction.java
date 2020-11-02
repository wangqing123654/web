package action.mem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jdo.ekt.EKTTool;
import jdo.mem.MEMTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 套餐销售
 * </p>
 * 
 * <p>
 * Description: 套餐销售
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author duzhw 2013.12.26
 * @version 4.5
 */
public class MEMPackageSalesTimeSelAction extends TAction {
	
	/**
	 * 执行保存事物
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm) {
		TConnection conn = getConnection();//表外键约束，无法事务控制
		TParm result = new TParm();
    	TParm payCashParm=parm.getParm("payCashParm");//微信支付宝交易号保存
		boolean isDedug=true; //add by huangtt 20160505 日志输出
		try {
			//医疗卡扣款
			if(parm.getData("ektSql") != null){
				List<String> ektSql = (List<String>) parm.getData("ektSql");
				for (int i = 0; i < ektSql.size(); i++) {
					result = new TParm(TJDODBTool.getInstance().update(ektSql.get(i), conn));
					if (result.getErrCode() < 0) {
						if(isDedug){  
							System.out.println(" come in class:MEMPackageSalesTimeSelAction.class ，method ：onSave");
							System.out.println("错误内容："+result);
						}

						
						conn.rollback();
						conn.close();
						return result;
					}
				}
			}
			//1:插入销售交易表 
			TParm tradeParm = parm.getParm("TRADEDATA");
//			System.out.println("tradeParm======--="+tradeParm);
			for (int i = 0; i < tradeParm.getCount("TRADE_NO"); i++) {
				TParm uparm=tradeParm.getRow(i);
				String tradeSql=this.getTradeSql(uparm);
				result = new TParm(TJDODBTool.getInstance().update(tradeSql, conn));
				if (result.getErrCode() < 0) {
					if(isDedug){  
						System.out.println(" come in class:MEMPackageSalesTimeSelAction.class ，method ：onSave");
						System.out.println("错误内容："+result);
					}

					
					conn.rollback();
					conn.close();
					return result;
				}
				//微信支付宝保存交易号码
		    	if(null!=payCashParm){
		    		TParm tempParm=new TParm();
		    		tempParm.setData("TRADE_NO",uparm.getValue("TRADE_NO"));
		    		if(null!=payCashParm.getValue("WX")&&payCashParm.getValue("WX").length()>0){
		    			tempParm.setData("WX_BUSINESS_NO",payCashParm.getValue("WX"));
					}else{
						tempParm.setData("WX_BUSINESS_NO","");
					}
					if(null!=payCashParm.getValue("ZFB")&&payCashParm.getValue("ZFB").length()>0){
						tempParm.setData("ZFB_BUSINESS_NO",payCashParm.getValue("ZFB"));
					}else{
						tempParm.setData("ZFB_BUSINESS_NO","");
					}
		    		result = MEMTool.getInstance().updateCashTypeMemPackTradeMBusinessNo(tempParm, conn);
		        	if (result.getErrCode() < 0) {
		        		conn.rollback();
		        		conn.close();
		    			return result;
		    		}
		    	}
			}
			//2:插入时程销售表
			TParm patSectionParm = parm.getParm("SECTIONDATA");
			for (int i = 0; i < patSectionParm.getCount("ID"); i++) {
				TParm uparm=patSectionParm.getRow(i);
				String sectionSql=this.getSectionSql(uparm);
				result = new TParm(TJDODBTool.getInstance().update(sectionSql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			//3:插入明细销售表
			TParm patSectionDParm = parm.getParm("SECTIONDDATA");
			for (int i = 0; i < patSectionDParm.getCount("ID"); i++) {
				TParm uparm=patSectionDParm.getRow(i);
				String sectionDSql=this.getSectionDSql(uparm);
				result = new TParm(TJDODBTool.getInstance().update(sectionDSql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			
//			delete by sunqy 20140722既有支付方式控件  则不走医疗卡扣费
//			//如果支付方式选择医疗卡-医疗卡支付
//			TParm parmEkt = parm.getParm("PARMEKT");
//			//System.out.println("医疗卡信息："+parmEkt);
//			String payType = tradeParm.getValue("PAY_TYPE", 0);
//			if("2".equals(payType)){
////				TParm ektParm = new TParm();
////				ektParm.setData("MR_NO", parmEkt.getValue("MR_NO", 0));
////				ektParm.setData("CARD_NO", parmEkt.getValue("CARD_NO", 0));
////				ektParm.setData("OPT_USER", parmEkt.getValue("OPT_USER", 0));
////				ektParm.setData("OPT_TERM", parmEkt.getValue("OPT_TERM", 0));
////				ektParm.setData("AMT", tradeParm.getValue("AR_AMT", 0));
////				ektParm.setData("MR_NO", "000000000333");
////				ektParm.setData("CARD_NO", "000000000333002");
////				ektParm.setData("OPT_USER", "D001");
////				ektParm.setData("OPT_TERM", "192.168.8.231");
////				ektParm.setData("AMT", "100");
//				//System.out.println("ektParm===>"+ektParm);
////				EKTAction aa = new EKTAction();
////				result = aa.ektPay(ektParm);
//				String mrNo = parmEkt.getValue("MR_NO");
//				String inCardNo = parmEkt.getValue("PK_CARD_NO");
//				double amt = tradeParm.getDouble("AR_AMT", 0);
//				String optUser = parmEkt.getValue("OPT_USER");
//				String optTerm = parmEkt.getValue("OPT_TERM");
//				double currentBalance = 0;
//				double newBalance = 0;
//				String cardNo = "";
//				String tradeNo = "";
//				String name = "";
//				String sql = 
//					" SELECT A.CARD_NO, B.CURRENT_BALANCE, B.NAME" +
//					" FROM EKT_ISSUELOG A, EKT_MASTER B" +
//					" WHERE     A.MR_NO = '" + mrNo + "'" +
//					" AND A.WRITE_FLG = 'Y'" +
//					" AND A.CARD_NO = B.CARD_NO";
//				System.out.println("查询医疗卡sql="+sql);
//				TParm cardParm = new TParm(TJDODBTool.getInstance().select(sql));
//				if(cardParm.getCount("CARD_NO") < 0){
//					cardParm.setErr(-1, "医疗卡余额0");
//					conn.rollback();
//					conn.close();
//					return cardParm;
//				}
//				cardNo = cardParm.getValue("CARD_NO", 0);
//				if(!inCardNo.equals(cardNo)){
//					result = new TParm();
//					result.setErr(-1, "此卡无效");
//					conn.rollback();
//					conn.close();
//					return result;
//				}
//				name = cardParm.getValue("NAME", 0);
//				currentBalance = cardParm.getDouble("CURRENT_BALANCE", 0);
//				newBalance = currentBalance - amt;
//				if(newBalance<0){
//					result = new TParm();
//					result.setErr(-1, "卡内余额不足");
//					conn.rollback();
//					conn.close();
//					return result;
//				}
//				sql = 
//					" UPDATE EKT_MASTER" +
//					" SET CURRENT_BALANCE = " + newBalance + "" +
//					" WHERE CARD_NO = '" + cardNo + "'";
//				//System.out.println("sql2="+sql);
//				result = new TParm(TJDODBTool.getInstance().update(sql, conn));
//				if(result.getErrCode() < 0 ){
//					conn.rollback();
//					conn.close();
//					return result;
//				}
//				tradeNo = EKTTool.getInstance().getTradeNo();
//				sql =
//					" INSERT INTO EKT_TRADE" +
//					" (TRADE_NO, CARD_NO, MR_NO, CASE_NO, PAT_NAME, " +
//					" OLD_AMT, AMT, STATE, BUSINESS_TYPE, OPT_USER, " +
//					" OPT_DATE, OPT_TERM, GREEN_BALANCE, GREEN_BUSINESS_AMT, PAY_OTHER3, " +
//					" PAY_OTHER4)" +
//					" VALUES" +
//					" ('" + tradeNo + "', '" + cardNo + "', '" + mrNo + "', '', '" + name + "', " +
//					" " + currentBalance + ", " + amt + ", '1', 'PAY', '" + optUser + "', " +
//					" SYSDATE, '" + optTerm + "', 0, 0, 0, " +
//					" 0)";
//				//System.out.println("sql3="+sql);
//				result = new TParm(TJDODBTool.getInstance().update(sql, conn));
//				if (result.getErrCode() < 0) {
//					err("ERR:" + result.getErrCode() + result.getErrText() +
//			                result.getErrName());
//					conn.rollback();
//					conn.close();
//					return result;
//				}
//			}  
			
		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, e.getMessage());
			err(e.getMessage());
		}
		
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * 查询明细医嘱细项
	 */
	public TParm onQueryDetail(TParm parm) {
		TParm result = new TParm();
		try {
//			String sectionCode = parm.getValue("SECTION_CODE");
//			String packageCode = parm.getValue("PACKAGE_CODE");
			String sql=this.onQueryDetailSql(parm);
			result = new TParm(TJDODBTool.getInstance().select(sql));
		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, e.getMessage());
			err(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 查询明细医嘱sql
	 */
	public String onQueryDetailSql(TParm parm) {
																				//add by lich 增加英文医嘱字段  TRADE_ENG_DESC 20141010

		String sql = "SELECT A.ID,A.SEQ,A.SECTION_DESC,A.ORDER_CODE,A.ORDER_DESC,A.ORDER_NUM,A.TRADE_ENG_DESC," +
				" A.UNIT_CODE,A.UNIT_PRICE,B.RETAIL_PRICE,A.DESCRIPTION,A.OPT_DATE,A.OPT_USER,A.OPT_TERM," +
				" A.SECTION_CODE,A.PACKAGE_CODE,A.SETMAIN_FLG,A.ORDERSET_CODE,A.ORDERSET_GROUP_NO,A.HIDE_FLG" +
				" FROM MEM_PACKAGE_SECTION_D A, MEM_PACKAGE_SECTION_D_PRICE B " +
				" WHERE A.PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE", 0)+"' " +
				" AND A.SECTION_CODE = '"+parm.getValue("SECTION_CODE", 0)+"'" +
				" AND A.PACKAGE_CODE = B.PACKAGE_CODE" +
				" AND A.SECTION_CODE = B.SECTION_CODE" +
				" AND A.ID = B.ID" +
				" AND B.PRICE_TYPE = '"+parm.getValue("PRICE_TYPE", 0)+"'" +
				" ORDER BY A.SEQ ";

		//System.out.println("查询明细医嘱sql="+sql);
		return sql;
	}
	/**
	 * 获取插入交易主表sql
	 */
	public String getTradeSql(TParm parm) {
		
		String sql = "INSERT INTO MEM_PACKAGE_TRADE_M(TRADE_NO,MR_NO,ORIGINAL_PRICE,RETAIL_PRICE," +
				" DISCOUNT_RATE,DISCOUNT_REASON,DISCOUNT_APPROVER,AR_AMT," +
				" DESCRIPTION,INTRODUCER1,INTRODUCER2,INTRODUCER3,OPT_DATE,OPT_USER,CASHIER_CODE,OPT_TERM," +
				" PAY_TYPE01,PAY_TYPE02,PAY_TYPE03,PAY_TYPE04,PAY_TYPE05,PAY_TYPE06,PAY_TYPE07," +
				" PAY_TYPE08,PAY_TYPE09,PAY_TYPE10,MEMO1,MEMO2,MEMO3,MEMO4,MEMO5,MEMO6,MEMO7,MEMO8,MEMO9,MEMO10,DISCOUNT_TYPE," +
				" CARD_TYPE,START_DATE,END_DATE,BILL_DATE,PAY_TYPE11,MEMO11,BILL_TYPE,PAY_MEDICAL_CARD" +
				" ) VALUES(" +
				"'"+parm.getValue("TRADE_NO")+"','"+parm.getValue("MR_NO")+"'," +
				"'"+parm.getValue("ORIGINAL_PRICE")+"','"+parm.getValue("RETAIL_PRICE")+"'," +
				"'"+parm.getValue("DISCOUNT_RATE")+"','"+parm.getValue("DISCOUNT_REASON")+"'," +
				"'"+parm.getValue("DISCOUNT_APPROVER")+"','"+parm.getValue("AR_AMT")+"'," +
				"'"+parm.getValue("DESCRIPTION")+"','"+parm.getValue("INTRODUCER1")+"'," +
				"'"+parm.getValue("INTRODUCER2")+"','"+parm.getValue("INTRODUCER3")+"',sysdate," +
				"'"+parm.getValue("OPT_USER")+"','"+parm.getValue("OPT_USER")+"','"+parm.getValue("OPT_TERM")+"'," +
				"'"+parm.getValue("PAY_TYPE01")+"','"+parm.getValue("PAY_TYPE02")+"'," +
				"'"+parm.getValue("PAY_TYPE03")+"','"+parm.getValue("PAY_TYPE04")+"'," +
				"'"+parm.getValue("PAY_TYPE05")+"','"+parm.getValue("PAY_TYPE06")+"'," +
				"'"+parm.getValue("PAY_TYPE07")+"','"+parm.getValue("PAY_TYPE08")+"'," +
				"'"+parm.getValue("PAY_TYPE09")+"','"+parm.getValue("PAY_TYPE10")+"'," +
				"'"+parm.getValue("MEMO1")+"','"+parm.getValue("MEMO2")+"'," +
				"'"+parm.getValue("MEMO3")+"','"+parm.getValue("MEMO4")+"'," +
				"'"+parm.getValue("MEMO5")+"','"+parm.getValue("MEMO6")+"'," +
				"'"+parm.getValue("MEMO7")+"','"+parm.getValue("MEMO8")+"'," +
				"'"+parm.getValue("MEMO9")+"','"+parm.getValue("MEMO10")+"',"+
				"'"+parm.getValue("DISCOUNT_TYPE")+"',"+
				"'"+parm.getValue("CARD_TYPE")+"'," +
				"TO_DATE('"+parm.getValue("START_DATE")+"','yyyyMMdd hh24miss'),TO_DATE('"+parm.getValue("END_DATE")+"','yyyyMMdd hh24miss'),sysdate" +
				",'"+parm.getValue("PAY_TYPE11")+"','"+parm.getValue("MEMO11")+"'" +
				",'"+parm.getValue("BILL_TYPE")+"','"+parm.getValue("PAY_MEDICAL_CARD")+"'" +
				")";
//		System.out.println("插入sql:"+sql);
		
			
		return sql;
	}
	/**
	 * 获取插入时程销售表sql
	 */
	public String getSectionSql(TParm parm) {
		String sql = "INSERT INTO MEM_PAT_PACKAGE_SECTION(ID,TRADE_NO,PACKAGE_CODE,SECTION_CODE," +
				" CASE_NO,MR_NO,PACKAGE_DESC,SECTION_DESC," +
				" ORIGINAL_PRICE,SECTION_PRICE,AR_AMT,DESCRIPTION,USED_FLG,REST_TRADE_NO) VALUES(" +
				"'"+parm.getValue("ID")+"','"+parm.getValue("TRADE_NO")+"'," +
				"'"+parm.getValue("PACKAGE_CODE")+"','"+parm.getValue("SECTION_CODE")+"'," +
				"'"+parm.getValue("CASE_NO")+"','"+parm.getValue("MR_NO")+"'," +
				"'"+parm.getValue("PACKAGE_DESC")+"','"+parm.getValue("SECTION_DESC")+"'," +
				"'"+parm.getValue("ORIGINAL_PRICE")+"','"+parm.getValue("SECTION_PRICE")+"'," +
				""+parm.getDouble("AR_AMT")+",'"+parm.getValue("DESCRIPTION")+"'," +
				"'"+parm.getValue("USED_FLG")+"','"+parm.getValue("REST_TRADE_NO")+"')";
		System.out.println("时程销售表sql:"+sql);
		return sql;
	}
	
	/**
	 * 获取插入时程明细销售表sql
	 */
	public String getSectionDSql(TParm parm) {
		String sql = "INSERT INTO MEM_PAT_PACKAGE_SECTION_D(ID,TRADE_NO,PACKAGE_CODE,SECTION_CODE," +
				" PACKAGE_DESC,SECTION_DESC,CASE_NO,MR_NO," +
				" SEQ,ORDER_CODE,ORDER_DESC,ORDER_NUM," +
				" UNIT_CODE,UNIT_PRICE,RETAIL_PRICE,DESCRIPTION," +			//add by lich 增加英文医嘱字段TRADE_ENG_DESC  PACKAGE_ENG_DESC  20141010
				" OPT_DATE,OPT_USER,OPT_TERM,SETMAIN_FLG,ORDERSET_CODE,ORDERSET_GROUP_NO,HIDE_FLG," +
				"TRADE_ENG_DESC,PACKAGE_ENG_DESC,USED_FLG,REST_TRADE_NO,ORDERSET_ID,UN_NUM_FLG) VALUES(" +
				"'"+SystemTool.getInstance().getNo("ALL", "EKT", "MEM_NO","MEM_NO")+"','"+parm.getValue("TRADE_NO")+"'," +
				"'"+parm.getValue("PACKAGE_CODE")+"','"+parm.getValue("SECTION_CODE")+"'," +
				"'"+parm.getValue("PACKAGE_DESC")+"','"+parm.getValue("SECTION_DESC")+"'," +
				"'"+parm.getValue("CASE_NO")+"','"+parm.getValue("MR_NO")+"'," +
				"'"+parm.getValue("SEQ")+"','"+parm.getValue("ORDER_CODE")+"'," +
				"'"+parm.getValue("ORDER_DESC")+"','"+parm.getValue("ORDER_NUM")+"'," +
				"'"+parm.getValue("UNIT_CODE")+"','"+parm.getValue("UNIT_PRICE")+"'," +
				"'"+parm.getValue("RETAIL_PRICE")+"','"+parm.getValue("DESCRIPTION")+"'," +
				"sysdate,'"+parm.getValue("OPT_USER")+"','"+parm.getValue("OPT_TERM")+"'," +
				"'"+parm.getValue("SETMAIN_FLG")+"','"+parm.getValue("ORDERSET_CODE")+"'," +
				"'"+parm.getValue("ORDERSET_GROUP_NO")+"','"+parm.getValue("HIDE_FLG")+"'," +
				//add by lich 增加英文医嘱字段TRADE_ENG_DESC 20141010
				"'"+parm.getValue("TRADE_ENG_DESC")+"','"+parm.getValue("PACKAGE_ENG_DESC")+"'," +
				//add by lich 增加英文医嘱字段TRADE_ENG_DESC 20141011
//				"'"+parm.getValue("PACKAGE_ENG_DESC")+"','"+parm.getValue("PACKAGE_ENG_DESC")+"'," +
				"'"+parm.getValue("USED_FLG")+"','"+parm.getValue("REST_TRADE_NO")+"','"
				+parm.getValue("ORDERSET_ID")+"','"+parm.getValue("UN_NUM_FLG")+"')";
		//System.out.println("时程明细销售表sql:"+sql);
				
		return sql;
	}
	
}
