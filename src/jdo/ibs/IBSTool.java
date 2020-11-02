package jdo.ibs;

import java.sql.Timestamp;
import java.util.Vector;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.bil.BIL;
import jdo.bil.BILIBSRecpdTool;
import jdo.bil.BILIBSRecpmTool;
import jdo.bil.BILLumpWorkTool;
import jdo.bil.BILReduceTool;
import jdo.bil.BILTool;
import jdo.sys.SYSChargeHospCodeTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import jdo.bil.BILPrintTool;
import jdo.ins.TJINSRecpTool;
import jdo.mem.MEMTool;

import java.text.DecimalFormat;
import jdo.sys.SYSStationTool;
import jdo.sys.DeptTool;

/**
 *
 * <p>Title: 住院计价工具类</p>
 *
 * <p>Description: 住院计价工具类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 * 
 */
public class IBSTool extends TJDOTool {
    /**
     * 实例
     */
    public static IBSTool instanceObject;
    /**
     * 得到实例
     * @return IBSTool
     */
    public static IBSTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IBSTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public IBSTool() {
        onInit();
    }

    public int getselMaxSeq(String caseNo, String billNo) {
        TParm selMaxSeqParm = new TParm();
        selMaxSeqParm.setData("CASE_NO", caseNo);
        selMaxSeqParm.setData("BILL_NO", billNo);
        TParm selMaxSeq = IBSBillmTool.getInstance().selMaxSeq(
                selMaxSeqParm);
        if (selMaxSeq.getCount("BILL_SEQ") <= 0)
            return 1;
        return selMaxSeq.getInt("BILL_SEQ", 0) + 1;
    }
    /**
     * 修改身份，婴儿已经出院，自动生成账单
     */
	public TParm insertIBSBillDataTwo(TParm parm, TConnection connection) {
		TParm result = new TParm();
		if (parm.getValue("CASE_NO").length() <= 0) {
			result.setErr(-1, "未获得就诊号");
			return result;
		}
		String sql = "SELECT SUM(OWN_AMT) OWN_AMT,SUM(TOT_AMT) TOT_AMT,REXP_CODE FROM IBS_ORDD WHERE CASE_NO='"
				+ parm.getValue("CASE_NO") + "' AND BILL_NO IS NULL GROUP BY REXP_CODE ";
		TParm ibsParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (ibsParm.getErrCode() < 0) {
			return ibsParm;
		}
		double totAmt = 0.00;
		double ownAmt = 0.00;
		if (ibsParm.getCount() <= 0) {
			return new TParm();
		}
		for (int i = 0; i < ibsParm.getCount(); i++) {
			totAmt += ibsParm.getDouble("TOT_AMT", i);
			ownAmt += ibsParm.getDouble("OWN_AMT", i);
		}
		String billNo = SystemTool.getInstance().getNo("ALL", "IBS", "BILL_NO", "BILL_NO");
		TParm p = new TParm();
		p.setData("BILL_NO", billNo);
		int maxBillSeq = getselMaxSeq(parm.getValue("CASE_NO"), billNo);
	    p.setData("BILL_SEQ", maxBillSeq);
		p.setData("CASE_NO", parm.getValue("CASE_NO"));
		p.setData("IPD_NO", parm.getData("IPD_NO"));
		p.setData("MR_NO", parm.getData("MR_NO"));
		p.setData("BILL_DATE", SystemTool.getInstance().getDate());
		p.setData("REFUND_FLG", "N");
		p.setData("REFUND_BILL_NO", "");
		p.setData("RECEIPT_NO", "");
		p.setData("CHARGE_DATE", new TNull(Timestamp.class));
		p.setData("CTZ1_CODE", parm.getData("CTZ1_CODE") == null ? "" : parm.getData("CTZ1_CODE"));
		p.setData("CTZ2_CODE", parm.getData("CTZ2_CODE") == null ? "" : parm.getData("CTZ2_CODE"));
		p.setData("CTZ3_CODE", parm.getData("CTZ3_CODE") == null ? "" : parm.getData("CTZ3_CODE"));
		p.setData("BEGIN_DATE", parm.getTimestamp("IN_DATE"));
		p.setData("END_DATE", SystemTool.getInstance().getDate());
		p.setData("DISCHARGE_FLG", "N");
		p.setData("DEPT_CODE", parm.getData("DEPT_CODE"));
		p.setData("STATION_CODE", parm.getData("STATION_CODE"));
		p.setData("REGION_CODE", parm.getData("REGION_CODE"));
		p.setData("BED_NO", parm.getData("BED_NO") == null ? "" : parm.getData("BED_NO"));
		p.setData("OWN_AMT", StringTool.round(ownAmt, 2));// modify by wanglong
															// 20140314
		p.setData("NHI_AMT", 0.00);// modify by wanglong 20140314
		p.setData("APPROVE_FLG", "N");
		p.setData("REDUCE_REASON", "N");
		p.setData("REDUCE_AMT", ownAmt - totAmt);// ====pangben 2014-6-4折扣金额显示
		p.setData("REDUCE_DATE", "");
		p.setData("REDUCE_DEPT_CODE", "");
		p.setData("REDUCE_RESPOND", "");
		p.setData("AR_AMT", StringTool.round(totAmt, 2));// modify by wanglong
															// 20140314
		p.setData("PAY_AR_AMT", 0.00);
		p.setData("CANDEBT_CODE", "");
		p.setData("CANDEBT_PERSON", "");
		p.setData("REFUND_CODE", "");
		p.setData("REFUND_DATE", new TNull(Timestamp.class));
		p.setData("OPT_USER", parm.getData("OPT_USER"));
		p.setData("OPT_TERM", parm.getData("OPT_TERM"));
		// 插入ibs_billm
		result = IBSBillmTool.getInstance().insertdata(p, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		// 更新ibs_ordm
		result = IBSOrdermTool.getInstance().updateBillNO(p, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		// 更新ibs_ordd
		result = IBSOrderdTool.getInstance().updateBillNO(p, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		TParm dParmSingle = new TParm();
		// 插入IBS_BILLD表中数据
		for (int i = 0; i < ibsParm.getCount(); i++) {
			String rexpCode = ibsParm.getValue("REXP_CODE", i);
			double ownAmtD = ibsParm.getDouble("OWN_AMT", i);
			double arAmtD = ibsParm.getDouble("TOT_AMT", i);
			dParmSingle.addData("REXP_CODE", rexpCode);
			dParmSingle.addData("BILL_NO", billNo);
			dParmSingle.addData("BILL_SEQ", maxBillSeq);
			dParmSingle.addData("PAY_AR_AMT", 0);
			dParmSingle.addData("OPT_USER", parm.getData("OPT_USER"));
			dParmSingle.addData("OPT_TERM", parm.getData("OPT_TERM"));
			dParmSingle.addData("REFUND_BILL_NO", "");
			dParmSingle.addData("REFUND_FLG", "N");
			dParmSingle.addData("REFUND_CODE", "");
			dParmSingle.addData("REFUND_DATE", new TNull(Timestamp.class));
			dParmSingle.addData("OWN_AMT", StringTool.round(ownAmtD, 2));
			dParmSingle.addData("AR_AMT", StringTool.round(arAmtD, 2));
			dParmSingle.addData("OPT_USER", parm.getData("OPT_USER"));
			dParmSingle.addData("OPT_TERM", parm.getData("OPT_TERM"));
		}
		int inBilldCount = dParmSingle.getCount("BILL_NO");
		for (int j = 0; j < inBilldCount; j++) {
			TParm inBilldParm = dParmSingle.getRow(j);
			result = IBSBilldTool.getInstance().insertdata(inBilldParm, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}

		}
		return result;
	}
    /**
     * 逐个写入账单档
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertIBSBillDataOne(TParm parm, TConnection connection) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        String caseNo = parm.getValue("CASE_NO");
        TParm result = new TParm();
        TParm p = new TParm();
        String flg = parm.getValue("FLG");
        if ("Y".equals(flg))
            parm.setData("BILL_DATE", SystemTool.getInstance().getDate());
        TParm orderm = IBSOrdermTool.getInstance().selectdate(parm);
        //System.out.println("未生成账单数据集M======"+orderm);
        if (orderm.getErrCode() < 0) {
            err(orderm.getErrText());
            return orderm;
        }
        if ("ADM".equals(parm.getValue("TYPE")) && orderm.getCount() == 0)
            return parm;
        if (orderm.getCount() == 0)
            return parm;
      
        if (orderm.getCount() < 0) {
        	
            //置为出院无账单状态
            result = ADMTool.getInstance().updateBillStatus("1", caseNo,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrText());
                return result;
            }
            orderm.setErr( -1, "无费用数据");
            return orderm;
        }
        String billNo = SystemTool.getInstance().getNo("ALL", "IBS",
                "BILL_NO",
                "BILL_NO");
        int maxBillSeq = getselMaxSeq(caseNo, billNo);
        p.setData("BILL_SEQ", maxBillSeq);
        //插入IBS_BILLM表中数据
        p.setData("BILL_NO", billNo);
        p.setData("CASE_NO", caseNo);
        p.setData("IPD_NO", orderm.getData("IPD_NO", 0));
        p.setData("MR_NO", orderm.getData("MR_NO", 0));
        p.setData("BILL_DATE", parm.getData("BILL_DATE"));
        p.setData("REFUND_FLG", "N");
        p.setData("REFUND_BILL_NO", "");
        p.setData("RECEIPT_NO", "");
        p.setData("CHARGE_DATE", new TNull(Timestamp.class));
        p.setData("CTZ1_CODE",
                  orderm.getData("CTZ1_CODE", 0) == null ? "" :
                  orderm.getData("CTZ1_CODE", 0));
        p.setData("CTZ2_CODE",
                  orderm.getData("CTZ2_CODE", 0) == null ? "" :
                  orderm.getData("CTZ2_CODE", 0));
        p.setData("CTZ3_CODE",
                  orderm.getData("CTZ3_CODE", 0) == null ? "" :
                  orderm.getData("CTZ3_CODE", 0));
        TParm orderTimeData = new TParm();
        orderTimeData.setData("CASE_NO", orderm.getData("CASE_NO", 0));
        //modify by caowl 20130110 start
        String sql ="SELECT COUNT(*) AS COUNT FROM IBS_BILLM WHERE CASE_NO = '"+orderm.getData("CASE_NO", 0)+"'";
        TParm parmDate = new TParm(TJDODBTool.getInstance().select(sql));    
        if(parmDate.getInt("COUNT",0)==0){
        	//第一次生成账单 begin_date取adm_inp表中in_date时间,   end_date则与bill_date一致.
        	String sql_in = "SELECT IN_DATE FROM ADM_INP WHERE CASE_NO = '"+orderm.getData("CASE_NO", 0)+"'";
        	TParm dateParm = new TParm(TJDODBTool.getInstance().select(sql_in));       	
        	 p.setData("BEGIN_DATE",dateParm.getTimestamp("IN_DATE",0) );
        }else{
        	//不是第一次生成账单  取最后一次生成的账单中的end_date赋值给此账单的begin_date; end_date仍与此账单的bill_date一致.
        	String sqls = "SELECT MAX (END_DATE) AS BEGIN_DATE FROM IBS_BILLM WHERE CASE_NO = '"+orderm.getData("CASE_NO", 0)+"' ";
        	TParm beginParm = new TParm(TJDODBTool.getInstance().select(sqls));         	
        	p.setData("BEGIN_DATE", beginParm.getTimestamp("BEGIN_DATE",0));
        }        
        p.setData("END_DATE", parm.getData("BILL_DATE"));
        //modify by caowl 20130110 end 
        if ("Y".equals(flg)) {
            p.setData("DISCHARGE_FLG", "Y");
            //置为出院未缴费状态
            result = ADMTool.getInstance().updateBillStatus("2", caseNo,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrText());
                return result;
            }
        } else {
            p.setData("DISCHARGE_FLG", "N");

        }
        p.setData("DEPT_CODE", orderm.getData("DEPT_CODE", 0));
        p.setData("STATION_CODE", orderm.getData("STATION_CODE", 0));
        TParm stationInfo = SYSStationTool.getInstance().selStationRegion(
                orderm.getValue("STATION_CODE", 0));
        p.setData("REGION_CODE", stationInfo.getData("REGION_CODE", 0));
        //===zhangp 20120425 start
//      p.setData("BED_NO", orderm.getData("BED_NO", 0));
      p.setData("BED_NO", orderm.getData("BED_NO", 0)==null?"":orderm.getData("BED_NO", 0));
      //===zhangp 20120425 end
        TParm orderDData = new TParm();
        orderDData.setData("CASE_NO", orderm.getData("CASE_NO", 0));
        orderDData.setData("BILL_DATE", parm.getData("BILL_DATE"));
        if ("Y".equals(flg))
            orderDData.setData("BILL_DATE", SystemTool.getInstance().getDate());
//       System.out.println("时间！！！！！"+orderDData.getData("BILL_DATE"));
        TParm orderDParm = IBSOrderdTool.getInstance().onQueryIbsOrddSumTotAmt(
                orderDData);
        if (orderDParm.getErrCode() < 0) {
            err("查询未产生账单的费用明细出错:"+orderDParm.getErrText());
            return orderDParm;
        }
//        System.out.println("查询未产生账单的费用明细档所有数据DDDDD"+orderDParm);

        double ownAmt = StringTool.round(orderDParm.getDouble("OWN_AMT",0), 2) ;
        double nhiAmt = 0.00;
        double arAmt = StringTool.round(orderDParm.getDouble("TOT_AMT",0), 2) ;
      //  double reduceAmt = 0.00;
       
//      arAmt = StringTool.round(ownAmt - reduceAmt, 2);
        p.setData("OWN_AMT", StringTool.round(ownAmt, 2));//modify by wanglong 20140314
        p.setData("NHI_AMT", StringTool.round(nhiAmt, 2));//modify by wanglong 20140314
        p.setData("APPROVE_FLG", "N");
        p.setData("REDUCE_REASON", "N");
        p.setData("REDUCE_AMT", ownAmt-arAmt);//====pangben 2014-6-4折扣金额显示
        p.setData("REDUCE_DATE", "");
        p.setData("REDUCE_DEPT_CODE", "");
        p.setData("REDUCE_RESPOND", "");
        p.setData("AR_AMT", StringTool.round(arAmt, 2));//modify by wanglong 20140314
        p.setData("PAY_AR_AMT", 0.00);
        p.setData("CANDEBT_CODE", "");
        p.setData("CANDEBT_PERSON", "");
        p.setData("REFUND_CODE", "");
        p.setData("REFUND_DATE", new TNull(Timestamp.class));
        p.setData("OPT_USER", parm.getData("OPT_USER"));
        p.setData("OPT_TERM", parm.getData("OPT_TERM"));

        //插入ibs_billm
        result = IBSBillmTool.getInstance().insertdata(p, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        p.setData("BILL_DATE", parm.getData("BILL_DATE"));
        //更新ibs_ordm
        result = IBSOrdermTool.getInstance().updateBillNO(p, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //更新ibs_ordd
        result = IBSOrderdTool.getInstance().updateBillNO(p, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //准备插入IBS_BILLD的数据条数
//        System.out.println("准备插入IBS_BILLD的数据条数入参"+parm);
        TParm inDParm = IBSOrderdTool.getInstance().selectdata(parm);
//        System.out.println("准备插入IBS_BILLD的数据"+inDParm);
        int dCount = inDParm.getCount("CASE_NO");
        TParm dParmSingle = new TParm();
        //插入IBS_BILLD表中数据
        for (int i = 0; i < dCount; i++) {
            String rexpCode = inDParm.getValue("REXP_CODE", i);
            double ownAmtD = inDParm.getDouble("OWN_AMT", i);
            double arAmtD = inDParm.getDouble("AR_AMT", i);
            int row = -1;
            Vector endVct = (Vector) dParmSingle.getData("REXP_CODE");
            if (endVct != null) {
                row = endVct.indexOf(rexpCode);
            }
            if (row == -1) {
                dParmSingle.addData("REXP_CODE", rexpCode);
                dParmSingle.addData("BILL_NO", billNo);
                dParmSingle.addData("BILL_SEQ", maxBillSeq);
                dParmSingle.addData("PAY_AR_AMT", 0);
                dParmSingle.addData("OPT_USER", parm.getData("OPT_USER"));
                dParmSingle.addData("OPT_TERM", parm.getData("OPT_TERM"));
                dParmSingle.addData("REFUND_BILL_NO", "");
                dParmSingle.addData("REFUND_FLG", "N");
                dParmSingle.addData("REFUND_CODE", "");
                dParmSingle.addData("REFUND_DATE", new TNull(Timestamp.class));
                dParmSingle.addData("OWN_AMT", StringTool.round(ownAmtD, 2));
                dParmSingle.addData("AR_AMT", StringTool.round(arAmtD, 2));

            } else {
                dParmSingle.setData("OWN_AMT", row,
                                    StringTool.round(ownAmtD +
                        dParmSingle.getDouble("OWN_AMT", row), 2));
                dParmSingle.setData("AR_AMT", row,
                                    StringTool.round(arAmtD +
                        dParmSingle.getDouble("AR_AMT", row), 2));
            }
        }
        int inBilldCount = dParmSingle.getCount("BILL_NO");
        for (int j = 0; j < inBilldCount; j++) {
            TParm inBilldParm = dParmSingle.getRow(j);
            result = IBSBilldTool.getInstance().insertdata(inBilldParm,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }

        }

        return result;
    }

//    public TParm getOrdermParm(TParm parm)
//    {
//        TParm result = new TParm();
//        return result;
//    }
//    public TParm getOrderdParm(TParm parm)
//    {
//        TParm result = new TParm();
//        return result;
//    }
    /**
     * 新增账单(insert IBS_BILLM,IBS_BILLD,update IBS_ORDM,IBS_ORDD)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertIBSBillData(TParm parm, TConnection connection) {
        //System.out.println("新增账单"+parm);
        TParm result = new TParm();
        int count = parm.getCount();
        Vector v = (Vector) parm.getData("CASE_NO");
       // System.out.println("新增账单  参数："+parm);
        for (int i = 0; i < count; i++) {
            parm.setData("CASE_NO", v.get(i));
//            System.out.println("新增账单入参"+parm);
            //插入ibs_billm
            result = insertIBSBillDataOne(parm, connection);
            if (result.getErrCode() < 0) {
                return result;
            }
            result = ADMTool.getInstance().updateBillDate(parm.getValue(
                    "CASE_NO"), connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }

        }
        return result;
    }

    /**
     * 缴费作业保存
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertIBSChargeData(TParm parm, TConnection connection) {
        TParm result = new TParm();
        TParm recpmParm = parm.getParm("RECPM");
        String recpRecpNo = recpmParm.getValue("RECEIPT_NO");
        TParm bilPayParm = parm.getParm("BILPAY");
        TParm bilTableParm = parm.getParm("BILLTABLE");
        //减免数据      shibl  20140514  add
        TParm reduceParm=parm.getParm("BIL_REDUCE");
        //减免标记
        String  reduceflg=reduceParm.getValue("REDUCEFLG");
        reduceParm.setData("RECEIPT_NO", recpRecpNo);
        TParm paymentParm=reduceParm.getParm("PAYMENT_PARM");//减免支付方式
        //预交金注记 有true 没有 flase
        boolean payFlg = true;
        if (bilPayParm == null || bilPayParm.getCount("CASE_NO") <= 0)
            payFlg = false;
        //增加新生儿财务状态更新--xiongwg20150402 start
		for (int i=0;i < bilTableParm.getCount("CASE_NO");i++) {
			// caowl 20130205 start
			if("Y".equals(bilTableParm.getValue("PAY_SEL",i))){
				boolean flg = false;
				for(int j=0;j<i;j++){
					if(bilTableParm.getValue("CASE_NO",i)
							.equals(bilTableParm.getValue("CASE_NO",j))){
						flg = true;
					}
				}
				if(flg) continue;
				String sql = "SELECT BILL_STATUS FROM ADM_INP WHERE CASE_NO ='"
						+ bilTableParm.getValue("CASE_NO", i) + "'";
				TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
				String bill_status = selParm.getValue("BILL_STATUS", 0);
				if (!bill_status.equals("0")) {
					result = ADMTool.getInstance().updateBillStatus("4",
							bilTableParm.getValue("CASE_NO", i), connection);
				}
			}else{
				continue;
			}
			
		}
		//增加新生儿财务状态更新--xiongwg20150402 start
    	//caowl 20130205 end
//        result = ADMTool.getInstance().updateBillStatus("4",
//                recpmParm.getValue("CASE_NO"), connection);
        if (result.getErrCode() < 0) {
            err(result.getErrText());
            return result;
        }
        if (payFlg) {
            //预交金操作
            bilPayParm.setData("IBS_RECP_NO", recpRecpNo);
            result = BILTool.getInstance().onOffBilPay(bilPayParm, connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
        }
        if(reduceflg.equals("Y")){
        	double reduceAmt=reduceParm.getDouble("REDUCE_AMT");
        	double onwAmt=recpmParm.getDouble("OWN_AMT");
        	recpmParm.setData("OWN_AMT", (onwAmt-reduceAmt));
        	recpmParm.setData("REDUCE_AMT", reduceAmt);
        	recpmParm.setData("REDUCE_NO", reduceParm.getValue("REDUCE_NO"));
        	 //=================================shibl 20140514 add===============================
            result=BILReduceTool.getInstance().onInsertIBSBilReduce(reduceParm, connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
            if (null!=paymentParm) {//====pangben 2014-9-4 减免金额 支付方式赋值
            	String [] typeName={"C0","C1","T0","C4","LPK","XJZKQ","EKT","BXZF","WX","ZFB"};
            	String [] bilTypeName={"PAY_CASH","PAY_BANK_CARD","PAY_CHECK","PAY_DEBIT","PAY_GIFT_CARD",
           				"PAY_DISCNT_CARD","PAY_MEDICAL_CARD","PAY_BXZF","PAY_TYPE09","PAY_TYPE10"};
            	 for (int i = 0; i <paymentParm.getCount("PAY_TYPE"); i++) {
            		 for (int j = 0; j < typeName.length; j++) {
            			 if (paymentParm.getValue("PAY_TYPE",i).equals(typeName[j])) {//现金减免
          					recpmParm.setData(bilTypeName[j], recpmParm.getDouble(bilTypeName[j])-
          							paymentParm.getDouble("REDUCE_AMT",i));
          					break;
     					}
            		 }
     			}
			}
           
        }else{
        	recpmParm.setData("REDUCE_AMT", new TNull(Double.class));
        	recpmParm.setData("REDUCE_NO", new TNull(String.class));
        }
        //插入收据主档
        System.out.println("收据主档数据" + recpmParm);
        result = BILIBSRecpmTool.getInstance().insertData(recpmParm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //===zhangp 20130717 start
        //TODO
        TParm tjinsParm = parm.getParm("TJINS");
        tjinsParm.setData("CASE_NO", recpmParm.getValue("CASE_NO"));
        tjinsParm.setData("RECEIPT_NO", recpRecpNo);
        result = TJINSRecpTool.getInstance().updateRecpm(tjinsParm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //===zhangp 20130717 end
        //插入收据明细档
        TParm recpdParm = parm.getParm("RECPD");
//        System.out.println("细表数据"+recpdParm);
        TParm inRecpdParm = new TParm();
        int recpdCount = recpdParm.getCount("RECEIPT_NO");
        for (int i = 0; i < recpdCount; i++) {
            inRecpdParm.setData("RECEIPT_NO", recpdParm.getData("RECEIPT_NO", i));
            inRecpdParm.setData("BILL_NO", recpdParm.getData("BILL_NO", i));
            inRecpdParm.setData("REXP_CODE", recpdParm.getData("REXP_CODE", i));
            inRecpdParm.setData("WRT_OFF_AMT",
                                recpdParm.getData("WRT_OFF_AMT", i));
            inRecpdParm.setData("OPT_USER", recpdParm.getData("OPT_USER", i));
            inRecpdParm.setData("OPT_TERM", recpdParm.getData("OPT_TERM", i));
            result = BILIBSRecpdTool.getInstance().insertData(inRecpdParm,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
        }
        TParm billdParm = parm.getParm("BILLD");
        //更新收据号码
        TParm updataForRecpParm = new TParm();
        StringBuffer allBillNo = new StringBuffer();
        int blldCount = billdParm.getCount("BILL_NO");
        for (int j = 0; j < blldCount; j++) {
            String seq = "";
            seq = "'" + billdParm.getValue("BILL_NO", j) + "'";
            if (allBillNo.length() > 0)
                allBillNo.append(",");
            allBillNo.append(seq);
        }
        String allBillNoStr = allBillNo.toString();
        updataForRecpParm.setData("RECEIPT_NO",
                                  recpdParm.getData("RECEIPT_NO", 0));
        updataForRecpParm.setData("BILL_NO", allBillNoStr);
//        System.out.println("缴费作业更新billm入/参｝｝｝｝｝"+updataForRecpParm);
        result = IBSBillmTool.getInstance().updataForRecp(updataForRecpParm,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
       
        result = BILPrintTool.getInstance().saveIBSRecp(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //增加新生儿bill_date更新--xiongwg20150402 start
		for (int i = 0; i < bilTableParm.getCount("CASE_NO"); i++) {
			if("N".equals(bilTableParm.getValue("PAY_SEL",i)))
				continue;
			boolean flg = false;
			for(int j=0;j<i;j++){
				if(bilTableParm.getValue("CASE_NO",i)
						.equals(bilTableParm.getValue("CASE_NO",j))){
					flg = true;
				}
			}
			if(flg) continue;
			result = ADMTool.getInstance().updateBillDate(
					bilTableParm.getValue("CASE_NO",i), connection);
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}
		}
		//增加新生儿bill_date更新--xiongwg20150402 end
        return result;
    }

    /**
     * 新增费用(For INW , UDD,PATCH)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm dataType=3 护士站,dataType=2 药房,dataType=0 批次,dataType=4 膳食
     */
    public TParm insertIBSOrder(TParm parm, TConnection connection) {
//        System.out.println("新增费用(For INW , UDD,PATCH)"+parm);
        TParm result = new TParm();
        TParm mParm = parm.getParm("M");
        Timestamp billDate;
//        System.out.println("计价类别》》》》》》》》》》》"+parm.getValue("DATA_TYPE"));
        if ("0".equals(parm.getValue("DATA_TYPE")))
            billDate = parm.getTimestamp("BILL_DATE");
        else
            billDate = SystemTool.getInstance().getDate();
//        System.out.println("计费日期"+billDate);
        //=======pangben 2014-7-10 添加校验
        if (null!=mParm.getData("CASE_NO", 0)&&mParm.getValue("CASE_NO", 0).length()>0) {
        	TParm inMParm = new TParm();
            inMParm.setData("CASE_NO", mParm.getData("CASE_NO", 0));
            inMParm.setData("CASE_NO_SEQ", mParm.getData("CASE_NO_SEQ", 0));
            inMParm.setData("BILL_DATE", billDate);
            inMParm.setData("IPD_NO", mParm.getData("IPD_NO", 0));
            inMParm.setData("MR_NO", mParm.getData("MR_NO", 0));
            inMParm.setData("DEPT_CODE", mParm.getData("DEPT_CODE", 0));
            inMParm.setData("STATION_CODE", mParm.getData("STATION_CODE", 0));
            inMParm.setData("BED_NO", mParm.getData("BED_NO", 0));
            inMParm.setData("DATA_TYPE", parm.getData("DATA_TYPE"));
            inMParm.setData("BILL_NO", "");
            inMParm.setData("OPT_USER", mParm.getData("OPT_USER", 0));
            inMParm.setData("OPT_TERM", mParm.getData("OPT_TERM", 0));
            TParm stationInfo = SYSStationTool.getInstance().selStationRegion(mParm.
                    getValue("STATION_CODE", 0));
            inMParm.setData("REGION_CODE", stationInfo.getData("REGION_CODE", 0));
//            System.out.println("插入OrderM数据" + inMParm);
            result = IBSOrdermTool.getInstance().insertdata(inMParm, connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
		}
      //获得最大组号，集合医嘱适用，区分作废数据
		int maxGroupNo=IBSLumpWorkBatchTool.getInstance().selMaxOrderSetGroupNo(mParm.getValue("CASE_NO", 0));
		if (maxGroupNo==-1) {
			maxGroupNo=1;
		}else{
	        //=======pangben 2016-4-12医技管理、护士执行、药品配药公用逻辑，操作集合医嘱退费将组号重新获取最大号
	        maxGroupNo=maxGroupNo+1;
		}
//        System.out.println("插入orderM数据成功！！HHHHHHHHHH");
        int dCount = mParm.getCount("CASE_NO");
        double totalAmtForADM = 0.00;
        TParm inDParm = new TParm();
		String ordersetCode="";
		String groupNoSeq="";
		String orderNoSeq="";
		if(dCount>0){
			ordersetCode=mParm.getValue("ORDERSET_CODE", 0);
			groupNoSeq=mParm.getValue("ORDERSET_GROUP_NO", 0);
			orderNoSeq=mParm.getValue("CASE_NO_SEQ", 0);
		}
        for (int j = 0; j < dCount; j++) {
        	inDParm = new TParm();
        	//=========pangben 2014-7-10 添加校验
        	if (null==mParm.getData("CASE_NO", j)||mParm.getValue("CASE_NO", j).length()<=0) {
				continue;
			}
            inDParm.setData("CASE_NO",
                            mParm.getData("CASE_NO", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("CASE_NO", j));
            inDParm.setData("CASE_NO_SEQ",
                            mParm.getData("CASE_NO_SEQ", j) == null ? -1 :
                            mParm.getData("CASE_NO_SEQ", j));
            inDParm.setData("SEQ_NO",
                            mParm.getData("SEQ_NO", j) == null ? -1 :
                            mParm.getData("SEQ_NO", j));
            inDParm.setData("BILL_DATE", billDate);
            inDParm.setData("EXEC_DATE", billDate);//执行时间
            inDParm.setData("ORDER_NO",
                            mParm.getData("ORDER_NO", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("ORDER_NO", j));
            inDParm.setData("ORDER_SEQ",
                            mParm.getData("ORDER_SEQ", j) == null ?
                            "0" :
                            mParm.getData("ORDER_SEQ", j));
            inDParm.setData("ORDER_CODE",
                            mParm.getData("ORDER_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("ORDER_CODE", j));
            TParm sysFeeInfo = SYSFeeTool.getInstance().getFeeAllData(mParm.
                    getValue("ORDER_CODE", j));
            inDParm.setData("ORDER_CHN_DESC",
                            sysFeeInfo.getData("ORDER_DESC", 0) == null ?
                            new TNull(String.class) :
                            sysFeeInfo.getData("ORDER_DESC", 0));
            inDParm.setData("ORDER_CAT1_CODE",
                            mParm.getData("ORDER_CAT1_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("ORDER_CAT1_CODE", j));
            inDParm.setData("CAT1_TYPE",
                            mParm.getData("CAT1_TYPE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("CAT1_TYPE", j));
//            inDParm.setData("ORDERSET_GROUP_NO",
//                            mParm.getData("ORDERSET_GROUP_NO", j) == null ?
//                            new TNull(String.class) :
//                            mParm.getData("ORDERSET_GROUP_NO", j));
            
            inDParm.setData("ORDERSET_CODE",
                            mParm.getData("ORDERSET_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("ORDERSET_CODE", j));
            inDParm.setData("INDV_FLG",
                            mParm.getData("HIDE_FLG", j) == null ?
                            "N" :
                            mParm.getData("HIDE_FLG", j));
            inDParm.setData("DS_FLG",
                            mParm.getData("DS_FLG", j) == null ?
                            "N" :
                            mParm.getData("DS_FLG", j));
            inDParm.setData("DEPT_CODE",
                            mParm.getData("ORDER_DEPT_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("ORDER_DEPT_CODE", j));
            inDParm.setData("STATION_CODE",
                            mParm.getData("STATION_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("STATION_CODE", j));
            inDParm.setData("DR_CODE",
                            mParm.getData("ORDER_DR_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("ORDER_DR_CODE", j));
            inDParm.setData("EXE_DEPT_CODE",
                            mParm.getData("EXEC_DEPT_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("EXEC_DEPT_CODE", j));
            inDParm.setData("EXE_STATION_CODE",
                            mParm.getData("STATION_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("STATION_CODE", j));
            inDParm.setData("COST_CENTER_CODE",
                            DeptTool.getInstance().getCostCenter(inDParm.
                    getValue("EXEC_DEPT_CODE"),
                    inDParm.getValue("EXE_STATION_CODE")) == null ?
                            new TNull(String.class) :
                            DeptTool.getInstance().getCostCenter(inDParm.
                    getValue("EXEC_DEPT_CODE"),
                    inDParm.getValue("EXE_STATION_CODE")));

            inDParm.setData("EXE_DR_CODE",
                            mParm.getData("OPT_USER", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("OPT_USER", j));
            inDParm.setData("MEDI_QTY",
                            mParm.getData("MEDI_QTY", j) == null ? 0.00 :
                            mParm.getData("MEDI_QTY", j));
            inDParm.setData("MEDI_UNIT",
                            mParm.getData("MEDI_UNIT", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("MEDI_UNIT", j));
            inDParm.setData("DOSE_CODE", mParm.getData("DOSE_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("DOSE_CODE", j)); //剂型;剂型类型
            inDParm.setData("FREQ_CODE",
                            mParm.getData("FREQ_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("FREQ_CODE", j));
            inDParm.setData("TAKE_DAYS",
                            mParm.getData("TAKE_DAYS", j) == null ? 0 :
                            mParm.getData("TAKE_DAYS", j));
            inDParm.setData("DOSAGE_UNIT",
                            mParm.getData("DOSAGE_UNIT", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("DOSAGE_UNIT", j));
            inDParm.setData("OWN_PRICE",
                            mParm.getData("OWN_PRICE", j) == null ? 0.00 :
                            mParm.getData("OWN_PRICE", j));
            inDParm.setData("NHI_PRICE",
                            mParm.getData("NHI_PRICE", j) == null ? 0.00 :
                            mParm.getDouble("NHI_PRICE", j));
            inDParm.setData("OWN_FLG", "Y");
            inDParm.setData("BILL_FLG", "Y");
            inDParm.setData("REXP_CODE",
                            mParm.getData("REXP_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("REXP_CODE", j));
            inDParm.setData("BILL_NO", new TNull(String.class));
            inDParm.setData("HEXP_CODE",
                            mParm.getData("HEXP_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("HEXP_CODE", j));
            Timestamp startDate = mParm.getTimestamp("DISPENSE_EFF_DATE", j);
            Timestamp endDate = mParm.getTimestamp("DISPENSE_END_DATE", j);
//            System.out.println("开始时间" + startDate);
//            System.out.println("结束时间" + endDate);
            inDParm.setData("BEGIN_DATE",
                            startDate == null ? new TNull(Timestamp.class) :
                            startDate);
            inDParm.setData("END_DATE",
                            endDate == null ? new TNull(Timestamp.class) :
                            endDate);
            if ("0".equals(parm.getValue("DATA_TYPE"))) {
                billDate = parm.getTimestamp("BILL_DATE");
                inDParm.setData("BEGIN_DATE", billDate);
                inDParm.setData("END_DATE", billDate);

            }
            String flg = parm.getValue("FLG");
          //添加集合医嘱数据组号重新获得======pangben 2016-4-12
			if (null!=mParm.getData("ORDERSET_CODE", j)&&mParm.getValue("ORDERSET_CODE", j).length()>0) {//集合医嘱
				if (ordersetCode.equals(mParm.getValue("ORDERSET_CODE", j))&&
						groupNoSeq.equals(mParm.getValue("ORDERSET_GROUP_NO", j))&&
						orderNoSeq.equals(mParm.getValue("CASE_NO_SEQ", j))) {
					inDParm.setData("ORDERSET_GROUP_NO",maxGroupNo);
				}else{		
    				maxGroupNo=maxGroupNo+1;
    				inDParm.setData("ORDERSET_GROUP_NO",maxGroupNo);
					ordersetCode=mParm.getValue("ORDERSET_CODE", j);
					groupNoSeq=mParm.getValue("ORDERSET_GROUP_NO", j);
					orderNoSeq=mParm.getValue("CASE_NO_SEQ", j);
				}
			}else{
				inDParm.setData("ORDERSET_GROUP_NO",mParm.getData("ORDERSET_GROUP_NO", j) == null ? new TNull(String.class) :
					mParm.getData("ORDERSET_GROUP_NO", j));
			}
            if (flg.equals("ADD")) {
                inDParm.setData("DOSAGE_QTY",

                                mParm.getData("DOSAGE_QTY", j) == null ? 0.00 :
                                mParm.getDouble("DOSAGE_QTY", j));
                inDParm.setData("COST_AMT",
                                mParm.getData("COST_AMT", j) == null ? 0.00 :
                                mParm.getDouble("COST_AMT", j));
                inDParm.setData("OWN_AMT",
                                StringTool.round(mParm.getDouble("OWN_AMT", j),
                                                 4));
                inDParm.setData("TOT_AMT",
                                StringTool.round(mParm.getDouble("TOT_AMT", j),
                                                 2));
                totalAmtForADM = totalAmtForADM +
                                 StringTool.round(mParm.getDouble("TOT_AMT", j),
                                                  2);
              //添加集合医嘱数据组号重新获得======pangben 2016-2-22
//                inDParm.setData("ORDERSET_GROUP_NO",mParm.getData("ORDERSET_GROUP_NO", j) == null ? new TNull(String.class) :
//					mParm.getData("ORDERSET_GROUP_NO", j));
//                backWriteOdiParm.addData("BILL_FLG", "Y");
            } else {
            	
                inDParm.setData("DOSAGE_QTY",
                                mParm.getData("DOSAGE_QTY", j) == null ? 0.00 :
                                ( -1) * mParm.getDouble("DOSAGE_QTY", j));
                inDParm.setData("COST_AMT",
                                mParm.getData("COST_AMT", j) == null ? 0.00 :
                                ( -1) * mParm.getDouble("COST_AMT", j));
                inDParm.setData("OWN_AMT",
                                ( -1) *
                                StringTool.round(mParm.getDouble("OWN_AMT", j),
                                                 4));
                inDParm.setData("TOT_AMT",
                                ( -1) *
                                StringTool.round(mParm.getDouble("TOT_AMT", j),
                                                 2));
//                backWriteOdiParm.addData("BILL_FLG", "N");
                totalAmtForADM = totalAmtForADM +
                                 StringTool.round(mParm.getDouble("TOT_AMT", j),
                                                  2);
            }
            inDParm.setData("OWN_RATE",
                            mParm.getData("OWN_RATE", j) == null ? 0.00 :
                            mParm.getData("OWN_RATE", j));
            inDParm.setData("REQUEST_FLG", "N");
            inDParm.setData("REQUEST_NO", new TNull(String.class));
            inDParm.setData("INV_CODE", new TNull(String.class));
            inDParm.setData("OPT_USER", mParm.getData("OPT_USER", j));
            inDParm.setData("OPT_TERM", mParm.getData("OPT_TERM", j));
            
            inDParm.setData("EXEC_DATE", billDate);// shibl  add  20150114  同步字段
//            System.out.println("插入第" + j + "条细表数据" + inDParm);
            String schdCode = getSchdCode(mParm.getValue("CASE_NO", j),mParm.getValue("DSPN_KIND", j),
            		mParm.getValue("ORDER_NO", j),mParm.getValue("ORDER_SEQ", j));
            inDParm.setData("SCHD_CODE",schdCode);//====临床路径时程   yanjing 20140903
            inDParm.setData("CLNCPATH_CODE",this.getClncPathCode(mParm.getValue("CASE_NO", j)));//====临床路径  yanjing 20140903
            result = IBSOrderdTool.getInstance().insertdata(inDParm, connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
        }
        String caseNoForADM = mParm.getValue("CASE_NO", 0);
        TParm selADMAll = new TParm();
        selADMAll.setData("CASE_NO", caseNoForADM);
        TParm selADMAllData = ADMInpTool.getInstance().selectall(selADMAll);
        double totalAmt = selADMAllData.getDouble("TOTAL_AMT", 0);
        double curAmt = selADMAllData.getDouble("CUR_AMT", 0);
        String flg = parm.getValue("FLG");
        double patchAmt = 0.00;
        if (parm.getData("PATCH_AMT") != null) {
            patchAmt = parm.getDouble("PATCH_AMT");
//            System.out.println("批次金额"+patchAmt);
        }
        if (flg.equals("ADD")) {
            if ("0".equals(parm.getValue("DATA_TYPE"))) {
                //  System.out.println("批次执行医疗总金额"+(totalAmt+totalAmtForADM-patchAmt));
                result = ADMTool.getInstance().updateTOTAL_AMT("" +
                        (totalAmt + totalAmtForADM - patchAmt), caseNoForADM,
                        connection); //更新ADM中医疗总金额
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    return result;
                }
                // System.out.println("批次执行目前余额"+(curAmt - totalAmtForADM+patchAmt));
                result = ADMTool.getInstance().updateCUR_AMT("" +
                        (curAmt - totalAmtForADM + patchAmt), caseNoForADM,
                        connection); //更新ADM中目前余额
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    return result;
                }

            } else {
                //System.out.println("医疗总金额"+(totalAmt+totalAmtForADM));
                result = ADMTool.getInstance().updateTOTAL_AMT("" +
                        (totalAmt + totalAmtForADM), caseNoForADM, connection); //更新ADM中医疗总金额
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    return result;
                }
//        System.out.println("行目前余额"+(curAmt - totalAmtForADM));
                result = ADMTool.getInstance().updateCUR_AMT("" +
                        (curAmt - totalAmtForADM), caseNoForADM, connection); //更新ADM中目前余额
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    return result;
                }
  
            }
        } else {
            //System.out.println("退费医疗总金额"+(totalAmt+totalAmtForADM));
            result = ADMTool.getInstance().updateTOTAL_AMT("" +
                    (totalAmt - totalAmtForADM), caseNoForADM, connection); //更新ADM中医疗总金额
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
//            System.out.println("退费目前余额"+(curAmt + totalAmtForADM));
            result = ADMTool.getInstance().updateCUR_AMT("" +
                    (curAmt + totalAmtForADM), caseNoForADM, connection); //更新ADM中目前余额
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }

        }
//        System.out.println("计价结束》》》》》》》》》》》");
        return result;
    }
    /**
     * 查询病患的时程（区分长期和临时）
     */
    private String getSchdCode(String caseNo,String dspnKind,String orderNo,String seqNo){
    	String schdCode = "";
    	String sql = "";
//    	System.out.println("dspnKind dspnKind is :"+dspnKind);
    	if(dspnKind.equals("ST")){//临时医生下医嘱的时程
    		sql = "SELECT SCHD_CODE FROM ODI_ORDER WHERE CASE_NO = '"+caseNo+"' AND RX_KIND IN ('F','ST') " +
			"AND ORDER_NO = '"+orderNo+"' AND ORDER_SEQ = '"+seqNo+"' ";
    	}else{//长期时病人当前的时程
    		sql = "SELECT SCHD_CODE,CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"' ";
    	}
//    	if(dspnKind.equals("UD")||dspnKind.equals("F")){//长期时病人当前的时程
//    		sql = "SELECT SCHD_CODE,CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"' ";
//    	}else{//临时/首日量医生下医嘱的时程
//    		sql = "SELECT SCHD_CODE FROM ODI_ORDER WHERE CASE_NO = '"+caseNo+"' AND RX_KIND IN ('F','ST') " +
//    				"AND ORDER_NO = '"+orderNo+"' AND ORDER_SEQ = '"+seqNo+"' ";
//    	}
//    	System.out.println("----sqlsql sql is ::"+sql);
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getCount()>0){
    		schdCode = parm.getValue("SCHD_CODE", 0);
    	}
    	return schdCode;
    }
    /**
     * 查询病患的临床路径
     */
    private String getClncPathCode(String caseNo){
    	String clncpathCode = "";
//    	String sql = "";
//    		sql = "SELECT SCHD_CODE,CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"' ";
    	String sql =  "SELECT CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"' ";
//    	System.out.println("----sqlsql sql is ::"+sql);
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getCount()>0){
    		clncpathCode = parm.getValue("CLNCPATH_CODE", 0);
    	}
    	return clncpathCode;
    }
    /**
     * 
    * @Title: onCheckLumWorkNew
    * @Description: TODO(校验此病患是否为套餐患者包括新生儿)
    * @author pangben
    * @param caseNo
    * @return
    * @throws
     */
    private String onCheckLumWorkCaseNo(TParm newBobyParm,String caseNo){
    	String caseNoNew=caseNo;
    	if (newBobyParm.getValue("NEW_BORN_FLG",0).equals("Y")) {
    		caseNoNew=newBobyParm.getValue("M_CASE_NO",0);
		}
    	return caseNoNew;
    }
    /**
     * 得到账务序号,账务子序号,价格信息
     * @param parm TParm
     * @return TParm 加入
     */
    public TParm getIBSOrderData(TParm parm) {
//        System.out.println("getIBSOrderData方法入参" + parm);
        String flg = parm.getValue("FLG");
        TParm result = new TParm();
        TParm mParm = parm.getParm("M");
        result = mParm;
        TParm selLevelParm = new TParm();
        selLevelParm.setData("CASE_NO", mParm.getValue("CASE_NO", 0));
        TParm selLevel = ADMInpTool.getInstance().selectall(selLevelParm);
        String level = selLevel.getValue("SERVICE_LEVEL", 0);
        String lumpworkCode=selLevel.getValue("LUMPWORK_CODE", 0);//套餐代码
        double lumpworkRate=selLevel.getDouble("LUMPWORK_RATE", 0);//套餐折扣 住院登记时计算
        int dCount = mParm.getCount("CASE_NO");
        String caseNo = mParm.getValue("CASE_NO", 0);
        TParm odiParm = new TParm(TJDODBTool.getInstance().select("SELECT LUMPWORK_ORDER_CODE FROM ODI_SYSPARM"));
    	if (odiParm.getValue("LUMPWORK_ORDER_CODE",0).length()<=0) {
    		result.setErr(-1,"套餐差异医嘱字典设置错误");
    		return result;
		}
        for (int i = 0; i < dCount; i++) {
            if(!caseNo.equals(mParm.getValue("CASE_NO", i))){
                result.setErr(-1,"不能传多个人员");
             //   System.out.println(mParm.getValue("CASE_NO", i)+"不能传多个人员");
                return result;
            }
            String ctz1Code = parm.getValue("CTZ1_CODE") == null ? "" :
                              parm.getValue("CTZ1_CODE");
            String ctz2Code = parm.getValue("CTZ2_CODE") == null ? "" :
                              parm.getValue("CTZ2_CODE");
            String ctz3Code = parm.getValue("CTZ3_CODE") == null ? "" :
                              parm.getValue("CTZ3_CODE");
            String orderCode = mParm.getValue("ORDER_CODE", i);
            TParm feeParm = new TParm();
            double ownPrice = 0.0;
            double nhiPrice = 0.0;
            String chargeHospCode = "";
            String chargeCode;
            //如果是扣库扣费的话，从SYS_FEE查询单价、医保价、院内费用代码,药品血品需要从sysFEE表里获得
            if ("ADD".equalsIgnoreCase(flg)) {
        		feeParm = SYSFeeTool.getInstance().getFeeData(orderCode);
        		if ("2".equals(level)) {
                    ownPrice = feeParm.getDouble("OWN_PRICE2", 0);
                } else if ("3".equals(level)) {
                    ownPrice = feeParm.getDouble("OWN_PRICE3", 0);
                } else
                    ownPrice = feeParm.getDouble("OWN_PRICE", 0);
            	if(null!=lumpworkCode && lumpworkCode.length()>0){//套餐病患计算费用
            		String caseNoNew=onCheckLumWorkCaseNo(selLevel,caseNo);
            		if(null!=feeParm.getValue("CAT1_TYPE",0) && feeParm.getValue("CAT1_TYPE",0).equals("PHA")||
                			null!=feeParm.getValue("CHARGE_HOSP_CODE",0) && 
                			feeParm.getValue("CHARGE_HOSP_CODE",0).equals("RA")){
            		}else{
            			ownPrice=getLumpOrderOwnPriceOne(caseNoNew, lumpworkCode, orderCode, ownPrice);
            		}
            	}else{//正常计费
            	}
            	nhiPrice = ownPrice;
                chargeHospCode = feeParm.getValue("CHARGE_HOSP_CODE", 0);
                TParm inChargeParm = new TParm();
                inChargeParm.setData("CHARGE_HOSP_CODE", chargeHospCode);
                TParm chargeParm = SYSChargeHospCodeTool.getInstance().
                                   selectChargeCode(inChargeParm);
                chargeCode = chargeParm.getValue("IPD_CHARGE_CODE", 0);  
            }
            //如果是增库增费的话，从IBS_ORDD查询当时的单价、医保价、院内费用代码
            else {        
            	//mParm 这个要正确 ！  
                feeParm = getIbsHistoryParam(mParm.getRow(i));
                if (feeParm.getErrCode() != 0) {   
                    return result.newErrParm( -1, "查询历史数据错误");
                }
                ownPrice = feeParm.getDouble("OWN_PRICE", 0);
                nhiPrice = feeParm.getDouble("NHI_PRICE", 0);
                chargeHospCode = feeParm.getValue("CHARGE_HOSP_CODE", 0);
                TParm inChargeParm = new TParm();
                inChargeParm.setData("CHARGE_HOSP_CODE", chargeHospCode);
                TParm chargeParm = SYSChargeHospCodeTool.getInstance().
                                   selectChargeCode(inChargeParm);
                chargeCode = chargeParm.getValue("IPD_CHARGE_CODE", 0);
            }
            double ownRate=0.00;
            double dosageQty = TypeTool.getDouble(mParm.getData("DOSAGE_QTY", i));
            //=========pangben 2016-8-23 
            if(null!=lumpworkCode&&lumpworkCode.length()>0){//套餐患者计费根据入院登记计算的折扣操作
            	//药品、血费根据身份进行统计
            	//套餐内计费
            	if(null!=feeParm.getValue("CAT1_TYPE",0) && feeParm.getValue("CAT1_TYPE",0).equals("PHA")||
            			null!=feeParm.getValue("CHARGE_HOSP_CODE",0) && feeParm.getValue("CHARGE_HOSP_CODE",0).equals("RA")){
            		ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,
                            orderCode, level);   
            		
            	}else if(orderCode.equals(odiParm.getValue("LUMPWORK_ORDER_CODE",0))){
            		ownRate=1;
            	}else{
    				ownRate=lumpworkRate;
    			}
            }else{//套餐外计费
            	ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,
                                         orderCode, level);
            }
            if (ownRate < 0) {
                return result.newErrParm( -1, "自付比例错误");
            }
//            System.out.println("护士站测试用ownRate自付比例"+ownRate);
            double ownAmt = ownPrice * dosageQty;
            double totAmt = ownAmt * ownRate;

            result.addData("HEXP_CODE", chargeHospCode);

            if (flg.equals("ADD")) {
                result.setData("BILL_FLG", i, "Y");
            } else {  
                result.setData("BILL_FLG", i, "N");
            }
            result.addData("REXP_CODE", chargeCode);
            result.setData("OWN_PRICE", i, ownPrice);  
            result.setData("NHI_PRICE", i, nhiPrice);
            result.setData("OWN_AMT", i, ownAmt);  
            result.setData("TOT_AMT", i, totAmt);
            result.addData("OWN_RATE", ownRate);
            TParm maxCaseNoSeq = IBSOrdermTool.getInstance().selMaxCaseNoSeq(
                    mParm.getValue("CASE_NO", 0));
//            System.out.println("取得最大账务序号|||||||||||||"+maxCaseNoSeq);
            //fux 20160118 need modify 
            if (maxCaseNoSeq.getCount("CASE_NO_SEQ") == 0) {
                result.addData("CASE_NO_SEQ", 1);
            } else {  
                result.addData("CASE_NO_SEQ",
                               (maxCaseNoSeq.getInt("CASE_NO_SEQ", 0) + 1));
            }
            result.addData("SEQ_NO", 1 + i);

        }
//        System.out.println("getIBSOrderData方法出参" + result);
        return result;
    }
    
    
//    /**
//     * 得到账务序号,账务子序号,价格信息
//     * @param parm TParm
//     * @return TParm 加入
//     */
//    public TParm getIBSOrderDataUdd(TParm parm) {
////        System.out.println("getIBSOrderData方法入参" + parm);
//        String flg = parm.getValue("FLG");
//        TParm result = new TParm();
//        TParm mParm = parm.getParm("M");
//        result = mParm;
//        TParm selLevelParm = new TParm();
//        selLevelParm.setData("CASE_NO", mParm.getValue("CASE_NO", 0));
//        TParm selLevel = ADMInpTool.getInstance().selectall(selLevelParm);
//        String level = selLevel.getValue("SERVICE_LEVEL", 0);
//        int dCount = mParm.getCount("CASE_NO");
//        String caseNo = mParm.getValue("CASE_NO", 0);
//        for (int i = 0; i < dCount; i++) {
//            if(!caseNo.equals(mParm.getValue("CASE_NO", i))){
//                result.setErr(-1,"不能传多个人员");
//             //   System.out.println(mParm.getValue("CASE_NO", i)+"不能传多个人员");
//                return result;
//            }
//            String ctz1Code = parm.getValue("CTZ1_CODE") == null ? "" :
//                              parm.getValue("CTZ1_CODE");
//            String ctz2Code = parm.getValue("CTZ2_CODE") == null ? "" :
//                              parm.getValue("CTZ2_CODE");
//            String ctz3Code = parm.getValue("CTZ3_CODE") == null ? "" :
//                              parm.getValue("CTZ3_CODE");
//            String orderCode = mParm.getValue("ORDER_CODE", i);
//            TParm feeParm = new TParm();
//            double ownPrice = 0.0;
//            double nhiPrice = 0.0;
//            String chargeHospCode = "";
//            String chargeCode;
//            //如果是扣库扣费的话，从SYS_FEE查询单价、医保价、院内费用代码
//            System.out.println(""+flg);
//            if ("ADD".equalsIgnoreCase(flg)) {
//            	 System.out.println("1111111111111111111111");
//                feeParm = SYSFeeTool.getInstance().getFeeData(orderCode);
//                if ("2".equals(level)) {
//                    ownPrice = feeParm.getDouble("OWN_PRICE2", 0);
//                } else if ("3".equals(level)) {
//                    ownPrice = feeParm.getDouble("OWN_PRICE3", 0);
//                } else
//                    ownPrice = feeParm.getDouble("OWN_PRICE", 0);
//                nhiPrice = feeParm.getDouble("NHI_PRICE", 0);
//                chargeHospCode = feeParm.getValue("CHARGE_HOSP_CODE", 0);
//                TParm inChargeParm = new TParm();
//                inChargeParm.setData("CHARGE_HOSP_CODE", chargeHospCode);  
//                TParm chargeParm = SYSChargeHospCodeTool.getInstance().
//                                   selectChargeCode(inChargeParm);
//                chargeCode = chargeParm.getValue("IPD_CHARGE_CODE", 0);  
//            }
//            //如果是增库增费的话，从IBS_ORDD查询当时的单价、医保价、院内费用代码
//            else {     
//           	 System.out.println("2222222222222222222222222");
//            	//fux modify 20160113  
//                //feeParm = SYSFeeTool.getInstance().getFeeData(orderCode);
//            	//mParm 这个要正确 ！  
//                feeParm = IBSTool.getInstance().getIbsHistoryParam(mParm.getRow(
//                    i));
//            	
//                if (feeParm.getErrCode() != 0) {   
////                    System.out.println("查询历史数据错误=" + feeParm.getErrText());
//                    return result.newErrParm( -1, "查询历史数据错误");
//                }
//                if ("2".equals(level)) {
//                    ownPrice = feeParm.getDouble("OWN_PRICE2", 0);
//                } else if ("3".equals(level)) {
//                    ownPrice = feeParm.getDouble("OWN_PRICE3", 0);
//                } else
//                    ownPrice = feeParm.getDouble("OWN_PRICE", 0);
//                nhiPrice = feeParm.getDouble("NHI_PRICE", 0);
//                chargeHospCode = feeParm.getValue("CHARGE_HOSP_CODE", 0);
//                TParm inChargeParm = new TParm();
//                inChargeParm.setData("CHARGE_HOSP_CODE", chargeHospCode);
//                TParm chargeParm = SYSChargeHospCodeTool.getInstance().
//                                   selectChargeCode(inChargeParm);
//                chargeCode = chargeParm.getValue("IPD_CHARGE_CODE", 0);
//            }
//            double dosageQty = TypeTool.getDouble(mParm.getData("DOSAGE_QTY", i));
//            double ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,
//                                         orderCode, level);
//            if (ownRate < 0) {
//                return result.newErrParm( -1, "自付比例错误");
//            }
////            System.out.println("护士站测试用ownRate自付比例"+ownRate);
//            double ownAmt = ownPrice * dosageQty;
//            double totAmt = ownAmt * ownRate;
//
//            result.addData("HEXP_CODE", chargeHospCode);
//
//            if (flg.equals("ADD")) {
//                result.setData("BILL_FLG", i, "Y");
//            } else {
//                result.setData("BILL_FLG", i, "N");
//            }
//            result.addData("REXP_CODE", chargeCode);
//            result.setData("OWN_PRICE", i, ownPrice);
//            result.setData("NHI_PRICE", i, nhiPrice);
//            result.setData("OWN_AMT", i, ownAmt);
//            result.setData("TOT_AMT", i, totAmt);
//            result.addData("OWN_RATE", ownRate);
//            TParm maxCaseNoSeq = IBSOrdermTool.getInstance().selMaxCaseNoSeq(
//                    mParm.getValue("CASE_NO", 0));
////            System.out.println("取得最大账务序号|||||||||||||"+maxCaseNoSeq);
//            //fux 20160118 need modify 
//            if (maxCaseNoSeq.getCount("CASE_NO_SEQ") == 0) {
//                result.addData("CASE_NO_SEQ", 1);
//            } else {  
//                result.addData("CASE_NO_SEQ",
//                               (maxCaseNoSeq.getInt("CASE_NO_SEQ", 0) + 1));
//            }
//            result.addData("SEQ_NO", 1 + i);
//
//        }
////        System.out.println("getIBSOrderData方法出参" + result);
//        return result;
//    }
    
    //重点 问题  需要修改
    /**
     * 根据外部传入需要增费的数据，取得CASE_NO，ORDER_NO，ORDER_SEQ，取得ODI_DSPNM中的IBS_CASE_NO_SEQ,IBS_SEQ_NO
     * 再根据CASE_NO，CASE_NO_SEQ，ORDER_CODE查得IBS_ORDD中的OWN_PRICE，NHI_PRICE，REXP_CODE，HEXP_CODE
     * @param parm TParm
     * @return TParm
     */
    public TParm getIbsHistoryParam(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            return null;
        }
        String caseNo = parm.getValue("CASE_NO");
        String orderNo = parm.getValue("ORDER_NO");
        String orderCode = parm.getValue("ORDER_CODE");
        int orderSeq = TypeTool.getInt(parm.getData("ORDER_SEQ"));
        if (caseNo == null || "".equalsIgnoreCase(caseNo)) {
            return null;
        }
        if (orderNo == null || "".equalsIgnoreCase(orderNo)) {
            return null;
        }
        if (orderSeq < 0) {
            return null;
        }
        int ibsCaseNoSeq = TypeTool.getInt(parm.getData("IBS_CASE_NO_SEQ"));
        int ibsSeqNo = TypeTool.getInt(parm.getData("IBS_SEQ_NO"));
        if (ibsCaseNoSeq <= 0) {
            String sql =
                    "SELECT IBS_CASE_NO_SEQ,IBS_SEQ_NO " +
                    "	FROM ODI_DSPNM " +
                    "	WHERE CASE_NO='" + caseNo + "' " +
                    "		  AND ORDER_NO='" + orderNo + "' " +
                    "		  AND ORDER_SEQ=" + orderSeq +
                    "		  AND START_DTTM='" + parm.getValue("START_DTTM") + "'";
            //System.out.println("根据外部传入需要增费的数据，取得CASE_NO，ORDER_NO，ORDER_SEQ，取得ODI_DSPNM中的IBS_CASE_NO_SEQ,IBS_SEQ_NOsql-=-=-----" + sql);
            TParm dspnM = new TParm(TJDODBTool.getInstance().select(sql));
            if (dspnM.getErrCode() != 0) {
//                System.out.println("getIbsHistoryParam.dspnM.sql=" +
//                                   dspnM.getErrText());
                return null;  
            }
            ibsCaseNoSeq = TypeTool.getInt(dspnM.getData("IBS_CASE_NO_SEQ", 0));
            ibsSeqNo = TypeTool.getInt(dspnM.getData("IBS_SEQ_NO", 0));
            if (ibsCaseNoSeq < 0) {
                return null;  
            }
        }
        String ibsSql =
                "SELECT A.OWN_PRICE,A.NHI_PRICE,A.REXP_CODE,A.HEXP_CODE AS CHARGE_HOSP_CODE,B.CAT1_TYPE " +
                "		FROM IBS_ORDD A,SYS_FEE B" +
                "		WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CASE_NO='" + caseNo + "' " +
                "			  AND A.CASE_NO_SEQ= " + ibsCaseNoSeq +
                "			  AND A.SEQ_NO= " + ibsSeqNo +
                "			  AND A.ORDER_CODE='" + orderCode + "'";
        //System.out.println("ibsSql:"+ibsSql);
        result = new TParm(TJDODBTool.getInstance().select(ibsSql));

        return result;
    }

    /**
     * 删除
     * @param parm TParm
     * @return TParm CASE_NO,CASE_NO_SEQ
     */
    public TParm deletedata(TParm parm) {
        TParm result = new TParm();
        result = update("deletedata", parm);
        return result;
    }

    /**
     * 删除费用信息(For 批次)
     * @param caseNo String
     * @param billDate String
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteOrderForPatch(String caseNo, String billDate,
                                     TConnection connection) {
        TParm result = new TParm();
        TParm caseNoSeqParm = IBSOrdermTool.getInstance().selCaseNoSeqForPatchSum(
                caseNo, billDate);
        //修改IBS_ORDM表删除数据IBS_ORDD表没有删除数据问题=====pangben 2015-11-9
        result = IBSOrdermTool.getInstance().deleteOrderMPatch(caseNo, billDate,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //=====pangben 2015-11-9添加查询多条CASE_NO_SEQ数据
        String caseNoSeq="";
        for (int i = 0; i < caseNoSeqParm.getCount("CASE_NO_SEQ"); i++) {
        	caseNoSeq+=""+caseNoSeqParm.getValue("CASE_NO_SEQ",i)+",";
		}
        if (caseNoSeq.length()>0) {
        	caseNoSeq=caseNoSeq.substring(0,caseNoSeq.lastIndexOf(","));
            result = IBSOrderdTool.getInstance().deleteOrderDPatchSum(caseNo, billDate,
            		caseNoSeq, connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
            //查询批次待删除数据
            TParm selDelOrder = IBSOrderdTool.getInstance().seldelOrderDPatchSum(
                    caseNo,
                    billDate,
                    caseNoSeq);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
            result.setData("PATCH_AMT", selDelOrder.getDouble("TOT_AMT", 0));
		}else{
			 result.setData("PATCH_AMT", 0.00);
		}
        return result;
    }
    /**
     * 作废账单
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertBillReturn(TParm parm, TConnection connection) {
        //System.out.println("作废账单"+parm);
        TParm result = new TParm();
        TParm actionParm = parm.getParm("DATA");
        String billNo = actionParm.getValue("BILL_NO");
        String mrNo = actionParm.getValue("MR_NO");
        String ipdNo = actionParm.getValue("IPD_NO");
        String caseNo = actionParm.getValue("CASE_NO");
        String optUser = actionParm.getValue("OPT_USER");
        String optTerm = actionParm.getValue("OPT_TERM");
        TParm selBillmParm = new TParm();
        selBillmParm.setData("CASE_NO", caseNo);
        selBillmParm.setData("BILL_NO", billNo);
        //账单主档数据
        TParm selBillm = IBSBillmTool.getInstance().selBillData(
                selBillmParm);
        //账单流水号
        String newBillNo = SystemTool.getInstance().getNo("ALL", "IBS",
                "BILL_NO", "BILL_NO");
        TParm insertBillmParm = new TParm();
        TParm selMaxSeqParm = new TParm();
        selMaxSeqParm.setData("CASE_NO", caseNo);
        selMaxSeqParm.setData("BILL_NO", newBillNo);
        TParm selMaxSeq = IBSBillmTool.getInstance().selMaxSeq(selMaxSeqParm);
        int maxBillSeq = selMaxSeq.getInt("BILL_SEQ", 0);
        //插入IBS_BILLM表中数据
        insertBillmParm.setData("BILL_SEQ", maxBillSeq + 1);
        insertBillmParm.setData("BILL_NO", newBillNo);
        insertBillmParm.setData("CASE_NO", selBillm.getData("CASE_NO", 0));
        insertBillmParm.setData("IPD_NO", selBillm.getData("IPD_NO", 0));
        insertBillmParm.setData("MR_NO", selBillm.getData("MR_NO", 0));
        //caowl 20130411 start
        // 得到系统时间       
		Timestamp sysDate = SystemTool.getInstance().getDate();
        insertBillmParm.setData("BILL_DATE", sysDate);
        //caowl 20130411 end
        //============pangben 2011-11-17 修改作废账单
        if (null != parm.getValue("RETURN_FLG") &&
            parm.getValue("RETURN_FLG").equals("Y"))
            insertBillmParm.setData("REFUND_FLG", "Y");
        else
            insertBillmParm.setData("REFUND_FLG", "N");
        insertBillmParm.setData("REFUND_BILL_NO",
                                selBillm.getData("REFUND_BILL_NO", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("REFUND_BILL_NO", 0));
        insertBillmParm.setData("RECEIPT_NO", selBillm.getData("RECEIPT_NO", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("RECEIPT_NO", 0)
                );
        insertBillmParm.setData("CHARGE_DATE",  new TNull(Timestamp.class));
        
        insertBillmParm.setData("CTZ1_CODE",
                                selBillm.getData("CTZ1_CODE", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("CTZ1_CODE", 0));
        insertBillmParm.setData("CTZ2_CODE",
                                selBillm.getData("CTZ2_CODE", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("CTZ2_CODE", 0));
        insertBillmParm.setData("CTZ3_CODE",
                                selBillm.getData("CTZ3_CODE", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("CTZ3_CODE", 0));
        insertBillmParm.setData("BEGIN_DATE", selBillm.getData("BEGIN_DATE", 0));
        insertBillmParm.setData("END_DATE", selBillm.getData("END_DATE", 0));
        insertBillmParm.setData("DISCHARGE_FLG",
                                selBillm.getData("DISCHARGE_FLG", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("DISCHARGE_FLG", 0));
        insertBillmParm.setData("DEPT_CODE", selBillm.getData("DEPT_CODE", 0));
        insertBillmParm.setData("STATION_CODE",
                                selBillm.getData("STATION_CODE", 0));
        TParm stationInfo = SYSStationTool.getInstance().selStationRegion(
                selBillm.getValue("STATION_CODE", 0));
        insertBillmParm.setData("REGION_CODE",
                                stationInfo.getData("REGION_CODE", 0));
        insertBillmParm.setData("BED_NO", selBillm.getData("BED_NO", 0));
        insertBillmParm.setData("OWN_AMT", -selBillm.getDouble("OWN_AMT", 0));
        insertBillmParm.setData("NHI_AMT", -selBillm.getDouble("NHI_AMT", 0));
        insertBillmParm.setData("APPROVE_FLG", "N");
        insertBillmParm.setData("REDUCE_REASON",
                                selBillm.getData("REDUCE_REASON", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("REDUCE_REASON", 0));
        insertBillmParm.setData("REDUCE_AMT", selBillm.getData("REDUCE_AMT", 0));
        insertBillmParm.setData("REDUCE_DATE",
                                selBillm.getData("REDUCE_DATE", 0) == null ?
                                new TNull(Timestamp.class) :
                                selBillm.getData("REDUCE_DATE", 0));
        insertBillmParm.setData("REDUCE_DEPT_CODE",
                                selBillm.getData("REDUCE_DEPT_CODE", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("REDUCE_DEPT_CODE", 0));
        insertBillmParm.setData("REDUCE_RESPOND",
                                selBillm.getData("REDUCE_RESPOND", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("REDUCE_RESPOND", 0));
        insertBillmParm.setData("AR_AMT", -selBillm.getDouble("AR_AMT", 0));
        insertBillmParm.setData("PAY_AR_AMT",
                                selBillm.getDouble("PAY_AR_AMT", 0));
        insertBillmParm.setData("CANDEBT_CODE",
                                selBillm.getData("CANDEBT_CODE", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("CANDEBT_CODE", 0));
        insertBillmParm.setData("CANDEBT_PERSON",
                                selBillm.getData("CANDEBT_PERSON", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("CANDEBT_PERSON", 0));
        insertBillmParm.setData("REFUND_CODE",
                                selBillm.getData("REFUND_CODE", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("REFUND_CODE", 0));
        insertBillmParm.setData("REFUND_DATE",
                                selBillm.getData("REFUND_DATE", 0) == null ?
                                new TNull(Timestamp.class) :
                                selBillm.getData("REFUND_DATE", 0));
        insertBillmParm.setData("OPT_USER", optUser);
        insertBillmParm.setData("OPT_TERM", optTerm);
        result = IBSBillmTool.getInstance().insertdata(insertBillmParm,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //更新账单主档
        TParm updateBillmParm = new TParm();
        updateBillmParm.setData("BILL_NO", selBillm.getData("BILL_NO", 0));
        updateBillmParm.setData("REFUND_BILL_NO", newBillNo);
        updateBillmParm.setData("REFUND_FLG", "Y");
        updateBillmParm.setData("REFUND_CODE", optUser);
        result = IBSBillmTool.getInstance().updataDate(updateBillmParm,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //账单明细档数据
        TParm selBilldParm = new TParm();
        selBilldParm.setData("BILL_NO", selBillm.getData("BILL_NO", 0));
        selBilldParm.setData("BILL_SEQ", selBillm.getData("BILL_SEQ", 0));
        TParm selBliid = IBSBilldTool.getInstance().selectAllData(selBilldParm);
        //更新账单明细档
        TParm updateBliidParm = new TParm();
        updateBliidParm.setData("BILL_NO", selBillm.getData("BILL_NO", 0));
        updateBliidParm.setData("REFUND_BILL_NO", newBillNo);
        updateBliidParm.setData("REFUND_CODE", optUser);
        result = IBSBilldTool.getInstance().updataDate(updateBliidParm,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }

        int billDCount = selBliid.getCount("BILL_NO");
        TParm insertBilldParm = new TParm();
        for (int i = 0; i < billDCount; i++) {
            insertBilldParm.setData("BILL_NO", newBillNo);
            insertBilldParm.setData("BILL_SEQ",
                                    maxBillSeq + 1);
            insertBilldParm.setData("REXP_CODE",
                                    selBliid.getData("REXP_CODE", i));
            insertBilldParm.setData("OWN_AMT", -selBliid.getDouble("OWN_AMT", i));
            insertBilldParm.setData("AR_AMT", -selBliid.getDouble("AR_AMT", i));
            insertBilldParm.setData("PAY_AR_AMT",
                                    selBliid.getDouble("PAY_AR_AMT", i));
            insertBilldParm.setData("REFUND_BILL_NO",
                                    selBillm.getData("REFUND_BILL_NO", 0) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("REFUND_BILL_NO", 0));
            insertBilldParm.setData("REFUND_FLG", "Y");
            insertBilldParm.setData("REFUND_CODE", optUser);
            insertBilldParm.setData("REFUND_DATE",
                                    selBillm.getData("REFUND_DATE", 0) == null ?
                                    new TNull(Timestamp.class) :
                                    selBillm.getData("REFUND_DATE", 0));
            insertBilldParm.setData("OPT_USER", optUser);
            insertBilldParm.setData("OPT_TERM", optTerm);
            result = IBSBilldTool.getInstance().insertdata(insertBilldParm,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
        }
        //费用主档数据
        TParm selOrderm = new TParm();
        selOrderm.setData("BILL_NO", billNo);
        TParm ordermParm = IBSOrdermTool.getInstance().selBillReturnM(selOrderm);
//        System.out.println("费用主档数据"+ordermParm);
        if (ordermParm.getErrCode() < 0) {
            err(ordermParm.getErrName() + " " + ordermParm.getErrText());
            return ordermParm;
        } 
        TParm selMaxCaseNoSeq = IBSOrdermTool.getInstance().selMaxCaseNoSeq(
                caseNo);
        int caseNoSeq = selMaxCaseNoSeq.getInt("CASE_NO_SEQ", 0);
        String caseNoM = "";
        int caseNoSeqM = 0;
        TParm orderMOne = new TParm();
        int mCount = ordermParm.getCount("BILL_NO");
        for (int i = 0; i < mCount; i++) {
            orderMOne = ordermParm.getRow(i);
            caseNoM = orderMOne.getValue("CASE_NO");
            caseNoSeqM = orderMOne.getInt("CASE_NO_SEQ");
            orderMOne.setData("CASE_NO_SEQ", caseNoSeq + 1);
            //插入费用主档
            orderMOne.setData("BILL_DATE", orderMOne.getTimestamp("BILL_DATE") == null ?
                    new TNull(Timestamp.class) :
                    	orderMOne.getTimestamp("BILL_DATE"));//===zhangp 20121109
            orderMOne.setData("REGION_CODE",
                              stationInfo.getData("REGION_CODE", 0));
            result = IBSOrdermTool.getInstance().insertdata(orderMOne,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
            orderMOne.setData("CASE_NO_SEQ", caseNoSeq + 2);
            orderMOne.setData("BILL_NO", new TNull(String.class));
            //插入费用主档
            result = IBSOrdermTool.getInstance().insertdata(orderMOne,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
            //费用明细档数据
            TParm selOrderd = new TParm();
            selOrderd.setData("CASE_NO", caseNoM);
            selOrderd.setData("CASE_NO_SEQ", caseNoSeqM);
            TParm orderdParm = IBSOrderdTool.getInstance().selBillReturnD(
                    selOrderd);
//            System.out.println("费用明细档数据"+orderdParm);
            if (orderdParm.getErrCode() < 0) {
                err(orderdParm.getErrName() + " " + orderdParm.getErrText());
                return orderdParm;
            }
            int countD = orderdParm.getCount("BILL_NO");
            for (int j = 0; j < countD; j++) {
                TParm orderDOne = orderdParm.getRow(j);
                //===========pangben modify 20110630 start
                orderDOne.setData("BEGIN_DATE",
                                  orderDOne.getTimestamp("BEGIN_DATE") == null ?
                                  new TNull(Timestamp.class) :
                                  orderDOne.getTimestamp("BEGIN_DATE"));
                orderDOne.setData("END_DATE",
                                  orderDOne.getTimestamp("END_DATE") == null ?
                                  new TNull(Timestamp.class) :
                                  orderDOne.getTimestamp("END_DATE"));
                //===========pangben modify 20110630 stop
                String orderCode = orderDOne.getData("ORDER_CODE") == null ? "" :
                                   orderDOne.getValue("ORDER_CODE");
                TParm sysFeeInfo = SYSFeeTool.getInstance().getFeeAllData(
                        orderCode);
                orderDOne.setData("ORDER_CHN_DESC",
                                  sysFeeInfo.getData("ORDER_DESC", 0) == null ?
                                  new TNull(String.class) :
                                  sysFeeInfo.getData("ORDER_DESC", 0));

                orderDOne.setData("CASE_NO_SEQ", caseNoSeq + 2);
                orderDOne.setData("BILL_DATE", orderDOne.getTimestamp("BILL_DATE") == null ?
                        new TNull(Timestamp.class) :
                            orderDOne.getTimestamp("BILL_DATE"));//===zhangp 20121109
                orderDOne.setData("DOSE_CODE",
                                  orderDOne.getData("DOSE_CODE") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("DOSE_CODE"));
                orderDOne.setData("CAT1_TYPE", orderDOne.getData("CAT1_TYPE") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("CAT1_TYPE")
                        );
                orderDOne.setData("EXE_STATION_CODE",
                                  orderDOne.getData("EXE_STATION_CODE") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("EXE_STATION_CODE")
                        );
                orderDOne.setData("COST_CENTER_CODE",
                                  DeptTool.getInstance().getCostCenter(
                                          orderDOne.getValue("EXE_DEPT_CODE"),
                                          orderDOne.getValue("EXE_STATION_CODE")) == null ?
                                  new TNull(String.class) :
                                  DeptTool.getInstance().getCostCenter(
                                          orderDOne.getValue("EXE_DEPT_CODE"),
                                          orderDOne.getValue("EXE_STATION_CODE"))
                        );
                orderDOne.setData("REQUEST_FLG",
                                  orderDOne.getData("REQUEST_FLG") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("REQUEST_FLG")
                        );
                orderDOne.setData("INV_CODE", orderDOne.getData("INV_CODE") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("INV_CODE")
                        );
                orderDOne.setData("INDV_FLG", orderDOne.getData("INDV_FLG") == null ?
                                  "N" :
                                  orderDOne.getData("INDV_FLG")
                        );
                orderDOne.setData("ORDERSET_CODE",
                                  orderDOne.getData("ORDERSET_CODE") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("ORDERSET_CODE")
                        );
                orderDOne.setData("DOSAGE_UNIT",
                                  orderDOne.getData("DOSAGE_UNIT") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("DOSAGE_UNIT")
                        );
                orderDOne.setData("MEDI_UNIT", orderDOne.getData("MEDI_UNIT") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("MEDI_UNIT")
                        );
                orderDOne.setData("ORDER_NO", orderDOne.getData("ORDER_NO") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("ORDER_NO")
                        );
                orderDOne.setData("ORDER_CAT1_CODE",
                                  orderDOne.getData("ORDER_CAT1_CODE") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("ORDER_CAT1_CODE")
                        );
                orderDOne.setData("ORDERSET_GROUP_NO",
                                  orderDOne.getData("ORDERSET_GROUP_NO") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("ORDERSET_GROUP_NO")
                        );
                orderDOne.setData("BILL_FLG", orderDOne.getData("BILL_FLG") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("BILL_FLG")
                        );
                orderDOne.setData("REQUEST_NO", orderDOne.getData("REQUEST_NO") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("REQUEST_NO")
                        );
                orderDOne.setData("FREQ_CODE", orderDOne.getData("FREQ_CODE") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("FREQ_CODE")
                        );
                orderDOne.setData("COST_AMT", orderDOne.getData("COST_AMT") == null ?
                                  0.00 : orderDOne.getData("COST_AMT"));
                String oldBillno = orderDOne.getValue("BILL_NO");
                orderDOne.setData("BILL_NO", new TNull(String.class));
                //===ZHANGP 20120806 START
				orderDOne
						.setData(
								"DS_FLG",
								orderDOne.getData("DS_FLG") == null ? "N"
										: orderDOne.getData("DS_FLG"));
                //===ZHANGP 20120806 END
                // 插入费用明细主档
                result = IBSOrderdTool.getInstance().insertdata(orderDOne,
                        connection);
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    return result;
                }
                orderDOne.setData("BILL_NO", oldBillno);
                orderDOne.setData("CASE_NO_SEQ", caseNoSeq + 1);
                orderDOne.setData("TOT_AMT", -orderDOne.getDouble("TOT_AMT"));
                orderDOne.setData("OWN_AMT", -orderDOne.getDouble("OWN_AMT"));
                orderDOne.setData("MEDI_QTY", -orderDOne.getInt("MEDI_QTY"));
                orderDOne.setData("DOSAGE_QTY", -orderDOne.getInt("DOSAGE_QTY"));
                result = IBSOrderdTool.getInstance().insertdata(orderDOne,
                        connection);
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    return result;
                }

            }
            caseNoSeq = caseNoSeq + 2;
        }
        return result;
    }
    /**
     * 校验医嘱金额合计和账单金额合计是否相等
     * @param caseNo String
     * @return boolean
     */
    public boolean checkData(String caseNo) {
        TParm result = new TParm();
        String billSql =
            " SELECT SUM (AR_AMT) AS AR_AMT "+
            "   FROM IBS_BILLM "+
            "  WHERE CASE_NO = '"+caseNo+"' ";
        result = new TParm(TJDODBTool.getInstance().select(billSql));
        if (result.getErrCode() < 0) {
            err( -1, result.getErrName());
            return false;
        }
        double arAmt =  result.getDouble("AR_AMT",0);
        String orderSql =
            " SELECT SUM (TOT_AMT) AS TOT_AMT "+
            "   FROM IBS_ORDD "+
            "  WHERE CASE_NO = '"+caseNo+"' ";
        result = new TParm(TJDODBTool.getInstance().select(orderSql));
        if (result.getErrCode() < 0) {
            err( -1, result.getErrName());
            return false;
        }
        double totAmt =  result.getDouble("TOT_AMT",0);
        if(arAmt!=totAmt)
            return false;
        return true;
    }
    /**
     * 批次补执行
     * @return TParm
     */
    public TParm batchFillFee() {
        TParm result = new TParm();
        String mrCaseSql =
                " SELECT MR_NO, CASE_NO, MSEQ, DSEQ " +
                "   FROM (SELECT M.MR_NO, M.CASE_NO, MAX (M.CASE_NO_SEQ) AS MSEQ," +
                "                MAX (D.CASE_NO_SEQ) AS DSEQ " +
                "           FROM IBS_ORDM M, IBS_ORDD D " +
                "          WHERE M.CASE_NO = D.CASE_NO " +
                "       GROUP BY M.MR_NO, M.CASE_NO) AA " +
                "  WHERE AA.MSEQ <> AA.DSEQ "+
                "  ORDER BY CASE_NO ";
        TParm mrCaseParm = new TParm(TJDODBTool.getInstance().select(mrCaseSql));
        int rowMrCase = mrCaseParm.getCount();
        for (int i = 0; i < rowMrCase; i++) {
            String mrNo = "";
            String caseNo = "";
            mrNo = mrCaseParm.getValue("MR_NO",i);
            caseNo = mrCaseParm.getValue("CASE_NO",i);
            String caseNoSeqSql =
                    " SELECT DISTINCT (CASE_NO_SEQ) " +
                    "   FROM IBS_ORDD " +
                    "  WHERE CASE_NO = '" + caseNo + "' " +
                    "    AND CASE_NO_SEQ NOT IN (SELECT CASE_NO_SEQ " +
                    "                              FROM IBS_ORDM " +
                    "                             WHERE MR_NO = '" + mrNo +
                    "') " +
                    "  ORDER BY CASE_NO_SEQ ";
            TParm caseNoSeqParm = new TParm(TJDODBTool.getInstance().select(caseNoSeqSql));
            int caseNoSeqCount = caseNoSeqParm.getCount();
            for(int j=0;j<caseNoSeqCount;j++){
                int caseNoSeq =caseNoSeqParm.getInt("CASE_NO_SEQ",j);
                String insertData =
                        " SELECT A.CASE_NO, A.CASE_NO_SEQ, A.BILL_DATE, B.IPD_NO, B.MR_NO,"+
                        "        A.DEPT_CODE, A.STATION_CODE, B.BED_NO, '0' AS DATA_TYPE, A.BILL_NO,"+
                        "        A.OPT_USER, A.OPT_DATE, A.OPT_TERM, 'H01' AS REGION_CODE,"+
                        "   FROM IBS_ORDD A, ADM_INP B "+
                        "  WHERE A.CASE_NO = B.CASE_NO "+
                        "    AND A.CASE_NO = '"+caseNo+"' "+
                        "    AND A.CASE_NO_SEQ = "+caseNoSeq+" "+
                        "  ORDER BY OPT_DATE ";
                TParm insertDataParm = new TParm(TJDODBTool.getInstance().select(insertData));
                String insertMSql =
                        ""+
                        ""+
                        "";

            }
        }

        return result;

    }
    /**
	 * 查询最大账务序号
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 * caowl
	 */
	public TParm selMaxCaseNoSeq(String caseNo) {
		String sql = " SELECT MAX(CASE_NO_SEQ) AS CASE_NO_SEQ FROM IBS_ORDM WHERE CASE_NO = '"
				+ caseNo + "' ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
    /**
	 * 修改身份
	 * 
	 * @author caowl
	 * @param parm
	 *            TParm
	 *            newBodyflg 婴儿校验
	 *            checkLumpWorkFlg 套餐病人校验
	 *            odiParm 套餐差异医嘱操作
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updBill(TParm parm,boolean newBodyflg,boolean checkLumpWorkFlg,TParm odiParm, TConnection connection) {

		
		TParm result = new TParm();
		// 得到系统时间
		Timestamp sysDate = SystemTool.getInstance().getDate();

		// 得到界面传过来的参数
		String caseNo = parm.getValue("CASE_NO");
		String optUser = parm.getValue("OPT_USER");
		String optTerm = parm.getValue("OPT_TERM");
		//新的身份
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");
		result=getUpdBill(caseNo, optUser, optTerm, sysDate, 
				CTZ1, CTZ2, CTZ3, newBodyflg, checkLumpWorkFlg, odiParm, connection);
		if (result.getErrCode()<0) {
			return result;
		}
		//stept2:更新ADM_INP修改身份
		String updSql = "UPDATE ADM_INP SET CTZ1_CODE = '"+CTZ1+"' ,CTZ2_CODE =  '"+CTZ2+"',CTZ3_CODE = '"+CTZ3+"' ,OPT_USER = '"+optUser+"', OPT_DATE = SYSDATE ,OPT_TERM = '"+optTerm+"',BILL_DATE = SYSDATE  WHERE CASE_NO = '"+caseNo+"'";
		result = new TParm(TJDODBTool.getInstance().update(updSql));
		if (result.getErrCode()<0) {
			return result;
		}
		return result;
	}

	/**
	 * 修改身份 --查询病人信息
	 * 
	 * @author caowl
	 * @param parm
	 *            String
	 * @return TParm
	 */
	public TParm selBycaseNo(String caseNo) {
		TParm selParm = new TParm();
		String sql = "SELECT CASE_NO,IPD_NO,MR_NO,REGION_CODE,BED_NO,CTZ1_CODE,CTZ2_CODE,CTZ3_CODE FROM ADM_INP WHERE CASE_NO = '"
				+ caseNo + "'";
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		return selParm;
	}

	/**
	 * 修改身份 --查询日志
	 * 
	 * @author caowl
	 * @param parm
	 *            String
	 * @return int
	 */
	public int selCountOfCaseno(String caseNo) {
		String sql = "SELECT CASE_NO,SEQ_NO,MR_NO,IPD_NO,BED_NO,REGION_CODE,CTZ_CODE1_O,CTZ_CODE2_O,CTZ_CODE3_O,CTZ_CODE1_N,CTZ_CODE2_N,CTZ_CODE3_N,OPT_USER,OPT_DATE,OPT_TERM " +
				" FROM ADM_CTZ_LOG " +
				" WHERE CASE_NO = '" + caseNo
				+ "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount() == -1){
			return 0;
		}
		return result.getCount();
	}
	/**
	 * 
	* @Title: updateAdmTotAmt
	* @Description: TODO(修改ADM_INP表總金額)
	* @author pangben
	* @param parm
	* @return
	* @throws
	 */
	public TParm updateAdmTotAmt(TParm parm){
		//更新ADM_INP TOTAL_AMT、 CUR_AMT字段 解决预交金界面显示问题===pangben 2014-6-9
		String sql="SELECT SUM(TOT_AMT) TOT_AMT FROM IBS_ORDD WHERE CASE_NO='"+parm.getValue("CASE_NO")+"'";
		TParm ibsOrddParm = new TParm(TJDODBTool.getInstance().select(sql));
		sql="SELECT SUM(PRE_AMT) PRE_AMT FROM BIL_PAY WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND TRANSACT_TYPE IN('01','02')" ;
		TParm bilPayParm=new TParm(TJDODBTool.getInstance().select(sql));
		double totAmt=0.00;
		double ibsOrddAmt=0.00;
		if (ibsOrddParm.getCount()>0) {
			ibsOrddAmt=ibsOrddParm.getDouble("TOT_AMT",0);//总价
		}
		if (bilPayParm.getCount()>0) {
			totAmt=bilPayParm.getDouble("PRE_AMT",0)-ibsOrddAmt;//预交金剩余
		}
		sql = " UPDATE ADM_INP SET TOTAL_AMT = '" + ibsOrddAmt
		+ "',CUR_AMT = '" + totAmt + "' " + "  WHERE CASE_NO = '"
		+ parm.getValue("CASE_NO") + "'";
		TParm result=new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode()<0) {
			result.setErr(-1, "更新住院表金额出现问题");
			return result;
		}
		return result;
	}
	/**
	 * 
	* @Title: getLumpworkOrderStatus
	* @Description: TODO(查询套餐病患医嘱状态，套餐内OR套餐外)
	* @author pangben 2015-7-16
	* @return  true 套餐外  false 套餐内
	* @throws
	 */
	public boolean getLumpworkOrderStatus(String caseNo,String orderCode){
		String sql="SELECT CASE_NO,LUMPWORK_CODE,M_CASE_NO,NEW_BORN_FLG FROM ADM_INP WHERE CASE_NO='"+caseNo+"' AND LUMPWORK_CODE IS NOT NULL";
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode()<0) {
			result.setErr(-1, "更新住院表金额出现问题");
			return false;
		}
		if (result.getCount()>0) {
			if (result.getValue("NEW_BORN_FLG",0).equals("Y")) {//新生儿查询母亲的套餐明细
				caseNo=result.getValue("M_CASE_NO",0);
			}
			sql="SELECT ORDER_CODE FROM MEM_PAT_PACKAGE_SECTION_D WHERE CASE_NO='"
				+caseNo+"' AND PACKAGE_CODE='"+result.getValue("LUMPWORK_CODE",0)+"' AND ORDER_CODE='"+orderCode+"'";
			result=new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getCount()>0) {
				return false;
			}else{
				return true;
			}
		}else{
			return false;
		}
	}
	/**
	 * 
	* @Title: getUpdBill
	* @Description: TODO(修改身份操作)
	* @author pangben
	* @param caseNo
	* @param optUser
	* @param optTerm
	* @param sysDate
	* @param CTZ1
	* @param CTZ2
	* @param CTZ3
	* @param newBodyflg
	* @param checkLumpWorkFlg
	* @param odiParm
	* @param connection
	* @return
	* @throws
	 */
	public TParm getUpdBill(String caseNo,String optUser,String optTerm,Timestamp sysDate ,
			String CTZ1,String CTZ2,String CTZ3,
			boolean newBodyflg,boolean checkLumpWorkFlg ,TParm odiParm,TConnection connection){
		//step1:根据caseNo查询病人信息
		TParm selPatientParm = new TParm();
		selPatientParm = selBycaseNo(caseNo);
		// 设置查询条件--根据case_no查询主张单信息
		TParm selBillmParm = new TParm();
		selBillmParm.setData("CASE_NO", caseNo);		
		TParm result=new TParm();
		//step3: 账单主档数据--根据case_no查询作废账单的全部数据
		//修改处理状态
		String sql="UPDATE IBS_ORDD SET INCLUDE_EXEC_FLG='Y' WHERE CASE_NO='"+caseNo+"'";
		result=new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode()<0) {
			return result;
		}
		TParm selBillm = IBSBillmTool.getInstance().selByCase_no(selBillmParm);	
		int billmcount = selBillm.getCount();// 账单主档的数据数目
		TParm maxCaseNoSeqParm = selMaxCaseNoSeq(caseNo);
		int maxCaseNoSeq = maxCaseNoSeqParm.getInt("CASE_NO_SEQ", 0) + 1;
		//获得最大组号，集合医嘱适用，区分作废数据
		int maxGroupNo=IBSLumpWorkBatchTool.getInstance().selMaxOrderSetGroupNo(caseNo);
		if (maxGroupNo==-1) {
			maxGroupNo=1;
		}
		//循环主账单
		if(result.getErrCode() ==0){		
			TParm updateBillmParm = null;
		for (int i = 0; i < billmcount; i++) {
			
//			System.out.println("循环第"+i+"张主账单");
			// 账单流水号--根据取号原则得到账单号和账单序号
			String newBillNo = SystemTool.getInstance().getNo("ALL", "IBS",
					"BILL_NO", "BILL_NO");
//			String newBillNo1 = SystemTool.getInstance().getNo("ALL", "IBS",
//					"BILL_NO", "BILL_NO");
//			
//			TParm selMaxSeqParm = new TParm();


			//step4:更新账单主档
			updateBillmParm = new TParm();
			updateBillmParm.setData("BILL_NO", selBillm.getData("BILL_NO", i));
			updateBillmParm.setData("REFUND_FLG", "Y");
			updateBillmParm.setData("REFUND_BILL_NO",newBillNo);
			updateBillmParm.setData("REFUND_CODE",optUser);

			result = IBSBillmTool.getInstance().updataDateforCTZ(updateBillmParm,
					connection);

			
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}
			//step5:主账单插入负数据
			// 插入IBS_BILLM表中负数据
			TParm insertBillmNegativeParm = new TParm();
			insertBillmNegativeParm.setData("BILL_SEQ", 1);
			insertBillmNegativeParm.setData("BILL_NO", newBillNo);
			insertBillmNegativeParm.setData("CASE_NO",
					selBillm.getData("CASE_NO", i));
			insertBillmNegativeParm.setData("IPD_NO",
					selBillm.getData("IPD_NO", i));
			insertBillmNegativeParm.setData("MR_NO",
					selBillm.getData("MR_NO", i));
			insertBillmNegativeParm.setData("BILL_DATE", sysDate);
			insertBillmNegativeParm.setData("REFUND_FLG", "Y");//caowl 20130419

			insertBillmNegativeParm.setData("REFUND_BILL_NO","");//caowl 20130419

			insertBillmNegativeParm.setData("RECEIPT_NO", selBillm.getData(
					"RECEIPT_NO", i) == null ? new TNull(String.class)
					: selBillm.getData("RECEIPT_NO", i));

			insertBillmNegativeParm.setData("CHARGE_DATE", selBillm.getData(
					"CHARGE_DATE", i) == null ? new TNull(String.class)
					: selBillm.getData("CHARGE_DATE", i));

			insertBillmNegativeParm.setData("CTZ1_CODE", selBillm.getData(
					"CTZ1_CODE", i) == null ? new TNull(String.class)
					: selBillm.getData("CTZ1_CODE", i));
			insertBillmNegativeParm.setData("CTZ2_CODE", selBillm.getData(
					"CTZ2_CODE", i) == null ? new TNull(String.class)
					: selBillm.getData("CTZ2_CODE", i));
			insertBillmNegativeParm.setData("CTZ3_CODE", selBillm.getData(
					"CTZ3_CODE", i) == null ? new TNull(String.class)
					: selBillm.getData("CTZ3_CODE", i));
			insertBillmNegativeParm.setData("BEGIN_DATE",selBillm.getData("BEGIN_DATE", i));
			insertBillmNegativeParm.setData("END_DATE",selBillm.getData("END_DATE", i));
			insertBillmNegativeParm.setData("DISCHARGE_FLG", selBillm.getData(
					"DISCHARGE_FLG", i) == null ? new TNull(String.class)
					: selBillm.getData("DISCHARGE_FLG", i));

			insertBillmNegativeParm.setData("DEPT_CODE",
					selBillm.getData("DEPT_CODE", i));
			insertBillmNegativeParm.setData("STATION_CODE",
					selBillm.getData("STATION_CODE", i));
			insertBillmNegativeParm.setData("REGION_CODE",
					selBillm.getData("REGION_CODE", i));
			insertBillmNegativeParm.setData("BED_NO",
					selBillm.getData("BED_NO", i));
			insertBillmNegativeParm.setData("OWN_AMT",
					-selBillm.getDouble("OWN_AMT", i));
			insertBillmNegativeParm.setData("NHI_AMT",
					-selBillm.getDouble("NHI_AMT", i));
			insertBillmNegativeParm.setData("APPROVE_FLG", "N");
			insertBillmNegativeParm.setData("REDUCE_REASON", selBillm.getData(
					"REDUCE_REASON", i) == null ? new TNull(String.class)
					: selBillm.getData("REDUCE_REASON", i));
			insertBillmNegativeParm.setData("REDUCE_AMT",
					selBillm.getData("REDUCE_AMT", i));
			insertBillmNegativeParm.setData("REDUCE_DATE", selBillm.getData(
					"REDUCE_DATE", i) == null ? new TNull(Timestamp.class)
					: selBillm.getData("REDUCE_DATE", i));
			insertBillmNegativeParm.setData("REDUCE_DEPT_CODE", selBillm
					.getData("REDUCE_DEPT_CODE", i) == null ? new TNull(
					String.class) : selBillm.getData("REDUCE_DEPT_CODE", i));
			insertBillmNegativeParm.setData("REDUCE_RESPOND", selBillm.getData(
					"REDUCE_RESPOND", i) == null ? new TNull(String.class)
					: selBillm.getData("REDUCE_RESPOND", i));
			insertBillmNegativeParm.setData("AR_AMT",
					-selBillm.getDouble("AR_AMT", i));
			insertBillmNegativeParm.setData("PAY_AR_AMT",
					selBillm.getDouble("PAY_AR_AMT", i));
			insertBillmNegativeParm.setData("CANDEBT_CODE", selBillm.getData(
					"CANDEBT_CODE", i) == null ? new TNull(String.class)
					: selBillm.getData("CANDEBT_CODE", i));
			insertBillmNegativeParm.setData("CANDEBT_PERSON", selBillm.getData(
					"CANDEBT_PERSON", i) == null ? new TNull(String.class)
					: selBillm.getData("CANDEBT_PERSON", i));
			insertBillmNegativeParm.setData("REFUND_CODE", selBillm.getData(
					"REFUND_CODE", i) == null ? new TNull(String.class)
					: selBillm.getData("REFUND_CODE", i));
			insertBillmNegativeParm.setData("REFUND_DATE", selBillm.getData(
					"REFUND_DATE", i) == null ? new TNull(Timestamp.class)
					: selBillm.getData("REFUND_DATE", i));
			insertBillmNegativeParm.setData("OPT_USER", optUser);
			insertBillmNegativeParm.setData("OPT_TERM", optTerm);

			result = IBSBillmTool.getInstance().insertdata(
					insertBillmNegativeParm, connection);
//			System.out.println("主账单插入负数据");
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}
			
			String sqlIbsOrdm = "SELECT * FROM IBS_ORDM WHERE BILL_NO = '"+selBillm.getData("BILL_NO", i)+"' AND CASE_NO = '"+selBillm.getData("CASE_NO", i)+"'";
			TParm parmIbsOrdm = new TParm(TJDODBTool.getInstance().select(sqlIbsOrdm));
			//循环费用主档
			if(parmIbsOrdm.getCount()>0){
				TParm insertIbsOrdMNegativeParm=null;
				for (int n = 0; n < parmIbsOrdm.getCount(); n++) {
					//step6:费用主档插入负数据
					//ibs_ordm插入付负数据    case_no_seq_new1，optUser，optTerm
//					System.out.println("循环第"+n+"次费用主档");
					insertIbsOrdMNegativeParm = new TParm();					
					insertIbsOrdMNegativeParm.setData("CASE_NO",parmIbsOrdm.getData("CASE_NO",n));
					if (maxCaseNoSeqParm.getCount("CASE_NO_SEQ") == 0) {
						insertIbsOrdMNegativeParm.setData("CASE_NO_SEQ",1);
					} else {
						insertIbsOrdMNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
					}
					
					insertIbsOrdMNegativeParm.setData("BILL_DATE",sysDate);
					insertIbsOrdMNegativeParm.setData("IPD_NO",parmIbsOrdm.getData("IPD_NO", n) == null ? new TNull(String.class) : parmIbsOrdm.getData("IPD_NO", n));
					insertIbsOrdMNegativeParm.setData("MR_NO",parmIbsOrdm.getData("MR_NO", n) == null ? new TNull(String.class) : parmIbsOrdm.getData("MR_NO", n));
					insertIbsOrdMNegativeParm.setData("DEPT_CODE",parmIbsOrdm.getData("DEPT_CODE", n) == null ? new TNull(String.class) : parmIbsOrdm.getData("DEPT_CODE", n));
					insertIbsOrdMNegativeParm.setData("STATION_CODE",parmIbsOrdm.getData("STATION_CODE", n) == null ? new TNull(String.class) : parmIbsOrdm.getData("STATION_CODE", n));
					insertIbsOrdMNegativeParm.setData("BED_NO",parmIbsOrdm.getData("BED_NO", n) == null ? new TNull(String.class) : parmIbsOrdm.getData("BED_NO", n));
					insertIbsOrdMNegativeParm.setData("DATA_TYPE",parmIbsOrdm.getData("DATA_TYPE", n) == null ? new TNull(String.class) : parmIbsOrdm.getData("DATA_TYPE", n));
					insertIbsOrdMNegativeParm.setData("BILL_NO",parmIbsOrdm.getData("BILL_NO", n) == null ? new TNull(String.class) : parmIbsOrdm.getData("BILL_NO", n));
					insertIbsOrdMNegativeParm.setData("OPT_USER",optUser);
					insertIbsOrdMNegativeParm.setData("OPT_TERM",optTerm);
					insertIbsOrdMNegativeParm.setData("REGION_CODE",parmIbsOrdm.getData("REGION_CODE", n) == null ? new TNull(String.class) : parmIbsOrdm.getData("REGION_CODE", n));
					insertIbsOrdMNegativeParm.setData("COST_CENTER_CODE",parmIbsOrdm.getData("COST_CENTER_CODE", n) == null ? new TNull(String.class) : parmIbsOrdm.getData("COST_CENTER_CODE", n));
					
					result =IBSOrdmTool.getInstance().insertdataM(insertIbsOrdMNegativeParm,connection);
			        if (result.getErrCode() < 0) {
			                err(result.getErrName() + " " + result.getErrText());
			                return result;
			        }
//			        System.out.println("费用主档负数据插入成功！");
					//根据bill_no查询
					String sqlIbsOrdd = "SELECT * FROM IBS_ORDD WHERE BILL_NO = '"+selBillm.getData("BILL_NO", i)+"' AND CASE_NO = '"+selBillm.getData("CASE_NO", i)
					+"' AND CASE_NO_SEQ ="+parmIbsOrdm.getDouble("CASE_NO_SEQ",n)+" ORDER BY CASE_NO_SEQ,ORDERSET_GROUP_NO,SEQ_NO";
					TParm parmIbsOrdd = new TParm(TJDODBTool.getInstance().select(sqlIbsOrdd));
					
					int seqNo = 1;
					//循环费用明细档
					if(parmIbsOrdd.getCount()>0){
						TParm insertIbsOrddNegativeParm =null;
						maxGroupNo=maxGroupNo+1;
						String ordersetCode=parmIbsOrdd.getValue("ORDERSET_CODE", 0);
						String groupNoSeq=parmIbsOrdd.getValue("ORDERSET_GROUP_NO", 0);
						String orderNoSeq=parmIbsOrdd.getValue("CASE_NO_SEQ", 0);
						for(int m = 0;m<parmIbsOrdd.getCount();m++){
							//step7:费用明细档插入负数据
//							System.out.println("循环第"+m+"次费用明细");
							//ibs_ordd插入负数据  case_no_seq_new1,bill_date-->sysdate,金额为负
							insertIbsOrddNegativeParm = new TParm();
							insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", m));					
							if (maxCaseNoSeqParm.getCount("CASE_NO_SEQ") == 0) {
								insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",1);
							} else {
								insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
							}							
							insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
							insertIbsOrddNegativeParm.setData("BILL_DATE",sysDate);
							insertIbsOrddNegativeParm.setData("EXEC_DATE",sysDate);
							insertIbsOrddNegativeParm.setData("ORDER_NO",parmIbsOrdd.getData("ORDER_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_NO", m));
							insertIbsOrddNegativeParm.setData("ORDER_SEQ",parmIbsOrdd.getData("ORDER_SEQ", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_SEQ", m));
							insertIbsOrddNegativeParm.setData("ORDER_CODE",parmIbsOrdd.getData("ORDER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CODE", m));
							insertIbsOrddNegativeParm.setData("ORDER_CAT1_CODE",parmIbsOrdd.getData("ORDER_CAT1_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CAT1_CODE", m));
							insertIbsOrddNegativeParm.setData("CAT1_TYPE",parmIbsOrdd.getData("CAT1_TYPE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CAT1_TYPE", m));
							//添加集合医嘱数据组号重新获得======pangben 2016-2-22
							if (null!=parmIbsOrdd.getData("ORDERSET_CODE", m)&&parmIbsOrdd.getValue("ORDERSET_CODE", m).length()>0) {//集合医嘱
								if (ordersetCode.equals(parmIbsOrdd.getValue("ORDERSET_CODE", m))&&
										groupNoSeq.equals(parmIbsOrdd.getValue("ORDERSET_GROUP_NO", m))&&
										orderNoSeq.equals(parmIbsOrdd.getValue("CASE_NO_SEQ", m))) {
									insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",maxGroupNo);
								}else{
									maxGroupNo=maxGroupNo+1;
									insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",maxGroupNo);
									ordersetCode=parmIbsOrdd.getValue("ORDERSET_CODE", m);
									groupNoSeq=parmIbsOrdd.getValue("ORDERSET_GROUP_NO", m);
									orderNoSeq=parmIbsOrdd.getValue("CASE_NO_SEQ", m);
								}
							}else{
								insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",parmIbsOrdd.getData("ORDERSET_GROUP_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_GROUP_NO", m));
							}
							//insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",parmIbsOrdd.getData("ORDERSET_GROUP_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_GROUP_NO", m));
							insertIbsOrddNegativeParm.setData("ORDERSET_CODE",parmIbsOrdd.getData("ORDERSET_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_CODE", m));
							insertIbsOrddNegativeParm.setData("INDV_FLG",parmIbsOrdd.getData("INDV_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INDV_FLG", m));
							insertIbsOrddNegativeParm.setData("DEPT_CODE",parmIbsOrdd.getData("DEPT_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DEPT_CODE", m));
							insertIbsOrddNegativeParm.setData("STATION_CODE",parmIbsOrdd.getData("STATION_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("STATION_CODE", m));
							insertIbsOrddNegativeParm.setData("DR_CODE",parmIbsOrdd.getData("DR_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DR_CODE", m));
							insertIbsOrddNegativeParm.setData("EXE_DEPT_CODE",parmIbsOrdd.getData("EXE_DEPT_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DEPT_CODE", m));
							insertIbsOrddNegativeParm.setData("EXE_STATION_CODE",parmIbsOrdd.getData("EXE_STATION_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_STATION_CODE", m));
							insertIbsOrddNegativeParm.setData("EXE_DR_CODE",parmIbsOrdd.getData("EXE_DR_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DR_CODE", m));
							insertIbsOrddNegativeParm.setData("MEDI_QTY",-parmIbsOrdd.getDouble("MEDI_QTY",m));
							insertIbsOrddNegativeParm.setData("MEDI_UNIT",parmIbsOrdd.getData("MEDI_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("MEDI_UNIT", m));
							insertIbsOrddNegativeParm.setData("DOSE_CODE",parmIbsOrdd.getData("DOSE_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSE_CODE", m));
							insertIbsOrddNegativeParm.setData("FREQ_CODE",parmIbsOrdd.getData("FREQ_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("FREQ_CODE", m));
							insertIbsOrddNegativeParm.setData("TAKE_DAYS",parmIbsOrdd.getData("TAKE_DAYS", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("TAKE_DAYS", m));
							insertIbsOrddNegativeParm.setData("DOSAGE_QTY",-parmIbsOrdd.getDouble("DOSAGE_QTY",m));
							insertIbsOrddNegativeParm.setData("DOSAGE_UNIT",parmIbsOrdd.getData("DOSAGE_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSAGE_UNIT", m));
							insertIbsOrddNegativeParm.setData("OWN_PRICE",parmIbsOrdd.getDouble("OWN_PRICE",m));
							insertIbsOrddNegativeParm.setData("NHI_PRICE",parmIbsOrdd.getDouble("NHI_PRICE",m));
							insertIbsOrddNegativeParm.setData("TOT_AMT",-parmIbsOrdd.getDouble("TOT_AMT",m));
							insertIbsOrddNegativeParm.setData("OWN_FLG",parmIbsOrdd.getData("OWN_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("OWN_FLG", m));
							insertIbsOrddNegativeParm.setData("BILL_FLG",parmIbsOrdd.getData("BILL_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("BILL_FLG", m));
							insertIbsOrddNegativeParm.setData("REXP_CODE",parmIbsOrdd.getData("REXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REXP_CODE", m));
							insertIbsOrddNegativeParm.setData("BILL_NO",parmIbsOrdd.getData("BILL_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("BILL_NO", m));
							insertIbsOrddNegativeParm.setData("HEXP_CODE",parmIbsOrdd.getData("HEXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("HEXP_CODE", m));
							insertIbsOrddNegativeParm.setData("BEGIN_DATE",parmIbsOrdd.getData("BEGIN_DATE", m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("BEGIN_DATE", m));
							insertIbsOrddNegativeParm.setData("END_DATE",parmIbsOrdd.getData("END_DATE", m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("END_DATE", m));
							insertIbsOrddNegativeParm.setData("OWN_AMT",-parmIbsOrdd.getDouble("OWN_AMT",m));
							insertIbsOrddNegativeParm.setData("OWN_RATE",parmIbsOrdd.getDouble("OWN_RATE",m));
							insertIbsOrddNegativeParm.setData("REQUEST_FLG",parmIbsOrdd.getData("REQUEST_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_FLG", m));
							insertIbsOrddNegativeParm.setData("REQUEST_NO",parmIbsOrdd.getData("REQUEST_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_NO", m));
							insertIbsOrddNegativeParm.setData("INV_CODE",parmIbsOrdd.getData("INV_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INV_CODE", m));
							insertIbsOrddNegativeParm.setData("OPT_USER",optUser);
							insertIbsOrddNegativeParm.setData("OPT_TERM",optTerm);
							insertIbsOrddNegativeParm.setData("COST_AMT",-parmIbsOrdd.getDouble("COST_AMT",m));
							insertIbsOrddNegativeParm.setData("ORDER_CHN_DESC",parmIbsOrdd.getData("ORDER_CHN_DESC", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CHN_DESC", m));
							insertIbsOrddNegativeParm.setData("COST_CENTER_CODE",parmIbsOrdd.getData("COST_CENTER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("COST_CENTER_CODE", m));
							insertIbsOrddNegativeParm.setData("SCHD_CODE",parmIbsOrdd.getData("SCHD_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("SCHD_CODE", m));
							insertIbsOrddNegativeParm.setData("CLNCPATH_CODE",parmIbsOrdd.getData("CLNCPATH_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CLNCPATH_CODE", m));
							insertIbsOrddNegativeParm.setData("DS_FLG",parmIbsOrdd.getData("DS_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DS_FLG", m));
							insertIbsOrddNegativeParm.setData("KN_FLG",parmIbsOrdd.getData("KN_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("KN_FLG", m));
							insertIbsOrddNegativeParm.setData("INCLUDE_FLG",parmIbsOrdd.getData("INCLUDE_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INCLUDE_FLG", m));
							insertIbsOrddNegativeParm.setData("INCLUDE_EXEC_FLG","Y");//作废数据处理状态
							seqNo++;
							result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
					        if (result.getErrCode() < 0) {
					                err(result.getErrName() + " " + result.getErrText());
					                return result;
					        }
//					        System.out.println("费用明细负数据插入成功！");
						}						
					}
					maxCaseNoSeq++;
					//step8:费用主档插入正数据（修改身份后的费用）
					//插入ibs_ordm正数据
					TParm insertIbsOrdMPositiveParm = new TParm();					
					insertIbsOrdMPositiveParm.setData("CASE_NO",parmIbsOrdm.getData("CASE_NO",n));					
					insertIbsOrdMPositiveParm.setData("CASE_NO_SEQ",maxCaseNoSeq);										
					insertIbsOrdMPositiveParm.setData("BILL_DATE",sysDate);
					insertIbsOrdMPositiveParm.setData("IPD_NO",parmIbsOrdm.getData("IPD_NO", n) == null ? new TNull(String.class) : parmIbsOrdm.getData("IPD_NO", n));
					insertIbsOrdMPositiveParm.setData("MR_NO",parmIbsOrdm.getData("MR_NO", n) == null ? new TNull(String.class) : parmIbsOrdm.getData("MR_NO", n));
					insertIbsOrdMPositiveParm.setData("DEPT_CODE",parmIbsOrdm.getData("DEPT_CODE", n) == null ? new TNull(String.class) : parmIbsOrdm.getData("DEPT_CODE", n));
					insertIbsOrdMPositiveParm.setData("STATION_CODE",parmIbsOrdm.getData("STATION_CODE", n) == null ? new TNull(String.class) : parmIbsOrdm.getData("STATION_CODE", n));
					insertIbsOrdMPositiveParm.setData("BED_NO",parmIbsOrdm.getData("BED_NO", n) == null ? new TNull(String.class) : parmIbsOrdm.getData("BED_NO", n));
					insertIbsOrdMPositiveParm.setData("DATA_TYPE",parmIbsOrdm.getData("DATA_TYPE", n) == null ? new TNull(String.class) : parmIbsOrdm.getData("DATA_TYPE", n));
					insertIbsOrdMPositiveParm.setData("BILL_NO","");
					insertIbsOrdMPositiveParm.setData("OPT_USER",optUser);
					insertIbsOrdMPositiveParm.setData("OPT_TERM",optTerm);
					insertIbsOrdMPositiveParm.setData("REGION_CODE",parmIbsOrdm.getData("REGION_CODE", n) == null ? new TNull(String.class) : parmIbsOrdm.getData("REGION_CODE", n));
					insertIbsOrdMPositiveParm.setData("COST_CENTER_CODE",parmIbsOrdm.getData("COST_CENTER_CODE", n) == null ? new TNull(String.class) : parmIbsOrdm.getData("COST_CENTER_CODE", n));
					result =IBSOrdmTool.getInstance().insertdataM(insertIbsOrdMPositiveParm,connection);
			        if (result.getErrCode() < 0) {
			                err(result.getErrName() + " " + result.getErrText());
			                return result;
			        }
//			        System.out.println("费用主档正数据插入成功！");
					//插入ibs_ordd正数据
					seqNo = 1;
					
					if(parmIbsOrdd.getCount()>0){
						maxGroupNo=maxGroupNo+1;
						String ordersetCode=parmIbsOrdd.getValue("ORDERSET_CODE", 0);
						String groupNoSeq=parmIbsOrdd.getValue("ORDERSET_GROUP_NO", 0);
						String orderNoSeq=parmIbsOrdd.getValue("CASE_NO_SEQ", 0);
						TParm insertIbsOrddPositiveParm =null;
						for(int m = 0;m<parmIbsOrdd.getCount();m++){
							//step9:费用明细档插入正数据（修改身份后的费用）
//							System.out.println("循环第"+m+"次费用明细档");
							//ibs_ordd插入负数据  case_no_seq_new1,bill_date-->sysdate,金额为负
							insertIbsOrddPositiveParm = new TParm();
							insertIbsOrddPositiveParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", m));					
							insertIbsOrddPositiveParm.setData("CASE_NO_SEQ",maxCaseNoSeq);													
							insertIbsOrddPositiveParm.setData("SEQ_NO",seqNo);
							insertIbsOrddPositiveParm.setData("BILL_DATE",sysDate);
							insertIbsOrddPositiveParm.setData("EXEC_DATE",sysDate);
							insertIbsOrddPositiveParm.setData("ORDER_NO",parmIbsOrdd.getData("ORDER_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_NO", m));
							insertIbsOrddPositiveParm.setData("ORDER_SEQ",parmIbsOrdd.getData("ORDER_SEQ", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_SEQ", m));
							insertIbsOrddPositiveParm.setData("ORDER_CODE",parmIbsOrdd.getData("ORDER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CODE", m));
							insertIbsOrddPositiveParm.setData("ORDER_CAT1_CODE",parmIbsOrdd.getData("ORDER_CAT1_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CAT1_CODE", m));
							insertIbsOrddPositiveParm.setData("CAT1_TYPE",parmIbsOrdd.getData("CAT1_TYPE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CAT1_TYPE", m));
							//添加集合医嘱数据组号重新获得======pangben 2016-2-22
							if (null!=parmIbsOrdd.getData("ORDERSET_CODE", m)&&parmIbsOrdd.getValue("ORDERSET_CODE", m).length()>0) {//集合医嘱
								if (ordersetCode.equals(parmIbsOrdd.getValue("ORDERSET_CODE", m))&&
										groupNoSeq.equals(parmIbsOrdd.getValue("ORDERSET_GROUP_NO", m))&&
										orderNoSeq.equals(parmIbsOrdd.getValue("CASE_NO_SEQ", m))) {
									insertIbsOrddPositiveParm.setData("ORDERSET_GROUP_NO",maxGroupNo);
								}else{
									maxGroupNo=maxGroupNo+1;
									insertIbsOrddPositiveParm.setData("ORDERSET_GROUP_NO",maxGroupNo);
									ordersetCode=parmIbsOrdd.getValue("ORDERSET_CODE", m);
									groupNoSeq=parmIbsOrdd.getValue("ORDERSET_GROUP_NO", m);
									orderNoSeq=parmIbsOrdd.getValue("CASE_NO_SEQ", m);
								}
							}else{
								insertIbsOrddPositiveParm.setData("ORDERSET_GROUP_NO",parmIbsOrdd.getData("ORDERSET_GROUP_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_GROUP_NO", m));
							}
							//insertIbsOrddPositiveParm.setData("ORDERSET_GROUP_NO",parmIbsOrdd.getData("ORDERSET_GROUP_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_GROUP_NO", m));
							insertIbsOrddPositiveParm.setData("ORDERSET_CODE",parmIbsOrdd.getData("ORDERSET_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_CODE", m));
							insertIbsOrddPositiveParm.setData("INDV_FLG",parmIbsOrdd.getData("INDV_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INDV_FLG", m));
							insertIbsOrddPositiveParm.setData("DEPT_CODE",parmIbsOrdd.getData("DEPT_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DEPT_CODE", m));
							insertIbsOrddPositiveParm.setData("STATION_CODE",parmIbsOrdd.getData("STATION_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("STATION_CODE", m));
							insertIbsOrddPositiveParm.setData("DR_CODE",parmIbsOrdd.getData("DR_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DR_CODE", m));
							insertIbsOrddPositiveParm.setData("EXE_DEPT_CODE",parmIbsOrdd.getData("EXE_DEPT_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DEPT_CODE", m));
							insertIbsOrddPositiveParm.setData("EXE_STATION_CODE",parmIbsOrdd.getData("EXE_STATION_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_STATION_CODE", m));
							insertIbsOrddPositiveParm.setData("EXE_DR_CODE",parmIbsOrdd.getData("EXE_DR_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DR_CODE", m));
							insertIbsOrddPositiveParm.setData("MEDI_QTY",parmIbsOrdd.getDouble("MEDI_QTY",m));
							insertIbsOrddPositiveParm.setData("MEDI_UNIT",parmIbsOrdd.getData("MEDI_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("MEDI_UNIT", m));
							insertIbsOrddPositiveParm.setData("DOSE_CODE",parmIbsOrdd.getData("DOSE_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSE_CODE", m));
							insertIbsOrddPositiveParm.setData("FREQ_CODE",parmIbsOrdd.getData("FREQ_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("FREQ_CODE", m));
							insertIbsOrddPositiveParm.setData("TAKE_DAYS",parmIbsOrdd.getData("TAKE_DAYS", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("TAKE_DAYS", m));
							insertIbsOrddPositiveParm.setData("DOSAGE_QTY",parmIbsOrdd.getDouble("DOSAGE_QTY",m));
							insertIbsOrddPositiveParm.setData("DOSAGE_UNIT",parmIbsOrdd.getData("DOSAGE_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSAGE_UNIT", m));
							insertIbsOrddPositiveParm.setData("OWN_PRICE",parmIbsOrdd.getDouble("OWN_PRICE",m));
							insertIbsOrddPositiveParm.setData("NHI_PRICE",parmIbsOrdd.getDouble("NHI_PRICE",m));							
							insertIbsOrddPositiveParm.setData("OWN_FLG",parmIbsOrdd.getData("OWN_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("OWN_FLG", m));
							insertIbsOrddPositiveParm.setData("BILL_FLG",parmIbsOrdd.getData("BILL_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("BILL_FLG", m));
							insertIbsOrddPositiveParm.setData("REXP_CODE",parmIbsOrdd.getData("REXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REXP_CODE", m));
							insertIbsOrddPositiveParm.setData("BILL_NO", "");
							insertIbsOrddPositiveParm.setData("HEXP_CODE",parmIbsOrdd.getData("HEXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("HEXP_CODE", m));
							insertIbsOrddPositiveParm.setData("BEGIN_DATE",parmIbsOrdd.getData("BEGIN_DATE", m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("BEGIN_DATE", m));
							insertIbsOrddPositiveParm.setData("END_DATE",parmIbsOrdd.getData("END_DATE", m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("END_DATE", m));							
							insertIbsOrddPositiveParm.setData("REQUEST_FLG",parmIbsOrdd.getData("REQUEST_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_FLG", m));
							insertIbsOrddPositiveParm.setData("REQUEST_NO",parmIbsOrdd.getData("REQUEST_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_NO", m));
							insertIbsOrddPositiveParm.setData("INV_CODE",parmIbsOrdd.getData("INV_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INV_CODE", m));
							insertIbsOrddPositiveParm.setData("OPT_USER",optUser);
							insertIbsOrddPositiveParm.setData("OPT_TERM",optTerm);							
							insertIbsOrddPositiveParm.setData("ORDER_CHN_DESC",parmIbsOrdd.getData("ORDER_CHN_DESC", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CHN_DESC", m));
							insertIbsOrddPositiveParm.setData("COST_CENTER_CODE",parmIbsOrdd.getData("COST_CENTER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("COST_CENTER_CODE", m));
							insertIbsOrddPositiveParm.setData("SCHD_CODE",parmIbsOrdd.getData("SCHD_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("SCHD_CODE", m));
							insertIbsOrddPositiveParm.setData("CLNCPATH_CODE",parmIbsOrdd.getData("CLNCPATH_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CLNCPATH_CODE", m));
							insertIbsOrddPositiveParm.setData("DS_FLG",parmIbsOrdd.getData("DS_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DS_FLG", m));
							insertIbsOrddPositiveParm.setData("KN_FLG",parmIbsOrdd.getData("KN_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("KN_FLG", m));
							insertIbsOrddPositiveParm.setData("INCLUDE_FLG","N");
				    		BIL bil = new BIL();
				    		String chargeHospCode = parmIbsOrdd.getData("HEXP_CODE", m)+"";
				    		String orderCode = parmIbsOrdd.getData("ORDER_CODE", m)+"";
				    		double tot_amt_new = 0.00;
				    		double cost_amt_new = 0.00;
				    		double own_amt = parmIbsOrdd.getDouble("OWN_AMT",m);
				    		double ownRate =1;
				    		//====pangben 2015-7-28 添加套餐病人修改身份，差异医嘱不打折操作
				    		if (!newBodyflg) {//婴儿不需要操作
								if (checkLumpWorkFlg&&null!=odiParm) {//套餐病人操作
									if (parmIbsOrdd.getValue("ORDER_CODE", m)
											.equals(odiParm.getValue("LUMPWORK_ORDER_CODE",0)) ) {//套餐病人 套餐差异医嘱不可以执行折扣
										tot_amt_new=own_amt;
										cost_amt_new = own_amt;
									}else{
										ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,chargeHospCode,orderCode) ;
										tot_amt_new =  own_amt*ownRate;
										cost_amt_new = own_amt*ownRate;
									}
								}else{
									ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,chargeHospCode,orderCode) ;
									tot_amt_new =  own_amt*ownRate;
									cost_amt_new = own_amt*ownRate;
								}
							}else{
								ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,chargeHospCode,orderCode) ;
								tot_amt_new =  own_amt*ownRate;
								cost_amt_new = own_amt*ownRate;
							}
				    		//double tot_amt = parmIbsOrdd.getDouble("TOT_AMT",m);				    		
				    		//===========pangben 20150212  修改身份折扣金额， 入院身份为0.7折 ，改成0.5折 要用自费金额* 当前折扣
				    		insertIbsOrddPositiveParm.setData("TOT_AMT",tot_amt_new);
				    		insertIbsOrddPositiveParm.setData("OWN_AMT",own_amt);
							insertIbsOrddPositiveParm.setData("OWN_RATE",ownRate);
				    		insertIbsOrddPositiveParm.setData("COST_AMT",cost_amt_new);
				    		seqNo++;
				    		//System.out.println("明细档正数据参数："+insertIbsOrdMPositiveParm);
				    		result =IBSOrdmTool.getInstance().insertdataD(insertIbsOrddPositiveParm,connection);
					        if (result.getErrCode() < 0) {
					                err(result.getErrName() + " " + result.getErrText());
					                return result;
					        }
//					        System.out.println("费用明细档正数据插入成功！");
						}						
					}
					maxCaseNoSeq++;
				}
			
			}
			
			
			//step10:查询账单明细档数据			
			TParm selBilldParm = new TParm();
			selBilldParm.setData("BILL_NO", selBillm.getData("BILL_NO", i));
			selBilldParm.setData("BILL_SEQ", selBillm.getData("BILL_SEQ", i));
			TParm selBliid = IBSBilldTool.getInstance().selectAllData(
					selBilldParm);
			int billDCount = selBliid.getCount("BILL_NO");
			TParm insertBilldNegativeParm = new TParm();
			//TParm insertBilldPositiveParm = new TParm();
			TParm updateBliidParm=null;
			for (int j = 0; j < billDCount; j++) {
//				System.out.println("循环第"+j+"张明细账单");
				//step11:更新账单明细档数据
				updateBliidParm = new TParm();
				updateBliidParm.setData("BILL_NO", selBillm.getData("BILL_NO", i));					
				updateBliidParm.setData("REFUND_CODE",optUser);
				updateBliidParm.setData("REFUND_BILL_NO",newBillNo);
				result = IBSBilldTool.getInstance().updataDateforCTZ(
						updateBliidParm, connection);
               
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					return result;
				}
				// step12：插入账单明细档负数据
				insertBilldNegativeParm.setData("BILL_NO", newBillNo);
				insertBilldNegativeParm.setData("BILL_SEQ", 1);
				insertBilldNegativeParm.setData("REXP_CODE",
						selBliid.getData("REXP_CODE", j));
				insertBilldNegativeParm.setData("OWN_AMT",
						-selBliid.getDouble("OWN_AMT", j));
				insertBilldNegativeParm.setData("AR_AMT",
						-selBliid.getDouble("AR_AMT", j));
				insertBilldNegativeParm.setData("PAY_AR_AMT",
						selBliid.getDouble("PAY_AR_AMT", j));
				insertBilldNegativeParm.setData("REFUND_BILL_NO", "");
				insertBilldNegativeParm.setData("REFUND_FLG", "Y");
				insertBilldNegativeParm.setData("REFUND_CODE", "");
				insertBilldNegativeParm.setData("REFUND_DATE", "");
				insertBilldNegativeParm.setData("OPT_USER", optUser);
				insertBilldNegativeParm.setData("OPT_TERM", optTerm);
				result = IBSBilldTool.getInstance().insertdata(
						insertBilldNegativeParm, connection);

				
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					return result;
				}
			}

		}
		}
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		//未结账的费用的处理=====================开始
		
		String sqlIbsOrdmForUnBil = "SELECT * FROM IBS_ORDM WHERE BILL_NO IS NULL AND CASE_NO = '"+caseNo+"'";
		TParm parmIbsOrdmForUnBil = new TParm(TJDODBTool.getInstance().select(sqlIbsOrdmForUnBil));
		if(parmIbsOrdmForUnBil.getCount()>0){
			TParm insertIbsOrdMNegativeParm =null;
			for(int n=0;n<parmIbsOrdmForUnBil.getCount();n++){
			//step6:费用主档插入负数据
			//ibs_ordm插入付负数据    case_no_seq_new1，optUser，optTerm
//			System.out.println("循环第"+n+"次费用主档");
			insertIbsOrdMNegativeParm = new TParm();					
			insertIbsOrdMNegativeParm.setData("CASE_NO",parmIbsOrdmForUnBil.getData("CASE_NO",n));
			if (maxCaseNoSeqParm.getCount("CASE_NO_SEQ") == 0) {
				insertIbsOrdMNegativeParm.setData("CASE_NO_SEQ",1);
			} else {
				insertIbsOrdMNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
			}
			
			insertIbsOrdMNegativeParm.setData("BILL_DATE",sysDate);
			insertIbsOrdMNegativeParm.setData("IPD_NO",parmIbsOrdmForUnBil.getData("IPD_NO", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("IPD_NO", n));
			insertIbsOrdMNegativeParm.setData("MR_NO",parmIbsOrdmForUnBil.getData("MR_NO", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("MR_NO", n));
			insertIbsOrdMNegativeParm.setData("DEPT_CODE",parmIbsOrdmForUnBil.getData("DEPT_CODE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("DEPT_CODE", n));
			insertIbsOrdMNegativeParm.setData("STATION_CODE",parmIbsOrdmForUnBil.getData("STATION_CODE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("STATION_CODE", n));
			insertIbsOrdMNegativeParm.setData("BED_NO",parmIbsOrdmForUnBil.getData("BED_NO", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("BED_NO", n));
			insertIbsOrdMNegativeParm.setData("DATA_TYPE",parmIbsOrdmForUnBil.getData("DATA_TYPE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("DATA_TYPE", n));
			insertIbsOrdMNegativeParm.setData("BILL_NO",parmIbsOrdmForUnBil.getData("BILL_NO", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("BILL_NO", n));
			insertIbsOrdMNegativeParm.setData("OPT_USER",optUser);
			insertIbsOrdMNegativeParm.setData("OPT_TERM",optTerm);
			insertIbsOrdMNegativeParm.setData("REGION_CODE",parmIbsOrdmForUnBil.getData("REGION_CODE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("REGION_CODE", n));
			insertIbsOrdMNegativeParm.setData("COST_CENTER_CODE",parmIbsOrdmForUnBil.getData("COST_CENTER_CODE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("COST_CENTER_CODE", n));
			
			result =IBSOrdmTool.getInstance().insertdataM(insertIbsOrdMNegativeParm,connection);
	        if (result.getErrCode() < 0) {
	                err(result.getErrName() + " " + result.getErrText());
	                return result;
	        }
//	        System.out.println("费用主档负数据插入成功！");
			//根据bill_no查询
			String sqlIbsOrdd = "SELECT * FROM IBS_ORDD WHERE BILL_NO IS NULL AND CASE_NO = '"
				+caseNo+"' AND CASE_NO_SEQ ="+parmIbsOrdmForUnBil.getDouble("CASE_NO_SEQ",n)+" ORDER BY CASE_NO_SEQ,ORDERSET_GROUP_NO,SEQ_NO ";
			TParm parmIbsOrdd = new TParm(TJDODBTool.getInstance().select(sqlIbsOrdd));
		
			int seqNo = 1;
			TParm insertIbsOrdMPositiveParm =null;
			//循环费用明细档
			if(parmIbsOrdd.getCount()>0){
				TParm insertIbsOrddNegativeParm=null;
				maxGroupNo=maxGroupNo+1;
				String ordersetCode=parmIbsOrdd.getValue("ORDERSET_CODE", 0);
				String groupNoSeq=parmIbsOrdd.getValue("ORDERSET_GROUP_NO", 0);
				String orderNoSeq=parmIbsOrdd.getValue("CASE_NO_SEQ", 0);
				for(int m = 0;m<parmIbsOrdd.getCount();m++){
					//step7:费用明细档插入负数据
//					System.out.println("循环第"+m+"次费用明细");
					//ibs_ordd插入负数据  case_no_seq_new1,bill_date-->sysdate,金额为负
					insertIbsOrddNegativeParm = new TParm();
					insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", m));					
					if (maxCaseNoSeqParm.getCount("CASE_NO_SEQ") == 0) {
						insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",1);
					} else {
						insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
					}							
					insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
					insertIbsOrddNegativeParm.setData("BILL_DATE",sysDate);
					insertIbsOrddNegativeParm.setData("EXEC_DATE",sysDate);
					insertIbsOrddNegativeParm.setData("ORDER_NO",parmIbsOrdd.getData("ORDER_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_NO", m));
					insertIbsOrddNegativeParm.setData("ORDER_SEQ",parmIbsOrdd.getData("ORDER_SEQ", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_SEQ", m));
					insertIbsOrddNegativeParm.setData("ORDER_CODE",parmIbsOrdd.getData("ORDER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CODE", m));
					insertIbsOrddNegativeParm.setData("ORDER_CAT1_CODE",parmIbsOrdd.getData("ORDER_CAT1_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CAT1_CODE", m));
					insertIbsOrddNegativeParm.setData("CAT1_TYPE",parmIbsOrdd.getData("CAT1_TYPE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CAT1_TYPE", m));
					//添加集合医嘱数据组号重新获得======pangben 2016-2-22
					if (null!=parmIbsOrdd.getData("ORDERSET_CODE", m)&&parmIbsOrdd.getValue("ORDERSET_CODE", m).length()>0) {//集合医嘱
						if (ordersetCode.equals(parmIbsOrdd.getValue("ORDERSET_CODE", m))&&
								groupNoSeq.equals(parmIbsOrdd.getValue("ORDERSET_GROUP_NO", m))&&
								orderNoSeq.equals(parmIbsOrdd.getValue("CASE_NO_SEQ", m))) {
							insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",maxGroupNo);
						}else{
							maxGroupNo=maxGroupNo+1;
							insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",maxGroupNo);
							ordersetCode=parmIbsOrdd.getValue("ORDERSET_CODE", m);
							groupNoSeq=parmIbsOrdd.getValue("ORDERSET_GROUP_NO", m);
							orderNoSeq=parmIbsOrdd.getValue("CASE_NO_SEQ", m);
						}
					}else{
						insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",parmIbsOrdd.getData("ORDERSET_GROUP_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_GROUP_NO", m));
					}
					//insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",parmIbsOrdd.getData("ORDERSET_GROUP_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_GROUP_NO", m));
					insertIbsOrddNegativeParm.setData("ORDERSET_CODE",parmIbsOrdd.getData("ORDERSET_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_CODE", m));
					insertIbsOrddNegativeParm.setData("INDV_FLG",parmIbsOrdd.getData("INDV_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INDV_FLG", m));
					insertIbsOrddNegativeParm.setData("DEPT_CODE",parmIbsOrdd.getData("DEPT_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DEPT_CODE", m));
					insertIbsOrddNegativeParm.setData("STATION_CODE",parmIbsOrdd.getData("STATION_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("STATION_CODE", m));
					insertIbsOrddNegativeParm.setData("DR_CODE",parmIbsOrdd.getData("DR_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DR_CODE", m));
					insertIbsOrddNegativeParm.setData("EXE_DEPT_CODE",parmIbsOrdd.getData("EXE_DEPT_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DEPT_CODE", m));
					insertIbsOrddNegativeParm.setData("EXE_STATION_CODE",parmIbsOrdd.getData("EXE_STATION_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_STATION_CODE", m));
					insertIbsOrddNegativeParm.setData("EXE_DR_CODE",parmIbsOrdd.getData("EXE_DR_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DR_CODE", m));
					insertIbsOrddNegativeParm.setData("MEDI_QTY",-parmIbsOrdd.getDouble("MEDI_QTY",m));
					insertIbsOrddNegativeParm.setData("MEDI_UNIT",parmIbsOrdd.getData("MEDI_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("MEDI_UNIT", m));
					insertIbsOrddNegativeParm.setData("DOSE_CODE",parmIbsOrdd.getData("DOSE_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSE_CODE", m));
					insertIbsOrddNegativeParm.setData("FREQ_CODE",parmIbsOrdd.getData("FREQ_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("FREQ_CODE", m));
					insertIbsOrddNegativeParm.setData("TAKE_DAYS",parmIbsOrdd.getData("TAKE_DAYS", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("TAKE_DAYS", m));
					insertIbsOrddNegativeParm.setData("DOSAGE_QTY",-parmIbsOrdd.getDouble("DOSAGE_QTY",m));
					insertIbsOrddNegativeParm.setData("DOSAGE_UNIT",parmIbsOrdd.getData("DOSAGE_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSAGE_UNIT", m));
					insertIbsOrddNegativeParm.setData("OWN_PRICE",parmIbsOrdd.getDouble("OWN_PRICE",m));
					insertIbsOrddNegativeParm.setData("NHI_PRICE",parmIbsOrdd.getDouble("NHI_PRICE",m));
					insertIbsOrddNegativeParm.setData("TOT_AMT",-parmIbsOrdd.getDouble("TOT_AMT",m));
					insertIbsOrddNegativeParm.setData("OWN_FLG",parmIbsOrdd.getData("OWN_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("OWN_FLG", m));
					insertIbsOrddNegativeParm.setData("BILL_FLG",parmIbsOrdd.getData("BILL_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("BILL_FLG", m));
					insertIbsOrddNegativeParm.setData("REXP_CODE",parmIbsOrdd.getData("REXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REXP_CODE", m));
					insertIbsOrddNegativeParm.setData("BILL_NO",parmIbsOrdd.getData("BILL_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("BILL_NO", m));
					insertIbsOrddNegativeParm.setData("HEXP_CODE",parmIbsOrdd.getData("HEXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("HEXP_CODE", m));
					insertIbsOrddNegativeParm.setData("BEGIN_DATE",parmIbsOrdd.getData("BEGIN_DATE", m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("BEGIN_DATE", m));
					insertIbsOrddNegativeParm.setData("END_DATE",parmIbsOrdd.getData("END_DATE", m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("END_DATE", m));
					insertIbsOrddNegativeParm.setData("OWN_AMT",-parmIbsOrdd.getDouble("OWN_AMT",m));
					insertIbsOrddNegativeParm.setData("OWN_RATE",parmIbsOrdd.getDouble("OWN_RATE",m));
					insertIbsOrddNegativeParm.setData("REQUEST_FLG",parmIbsOrdd.getData("REQUEST_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_FLG", m));
					insertIbsOrddNegativeParm.setData("REQUEST_NO",parmIbsOrdd.getData("REQUEST_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_NO", m));
					insertIbsOrddNegativeParm.setData("INV_CODE",parmIbsOrdd.getData("INV_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INV_CODE", m));
					insertIbsOrddNegativeParm.setData("OPT_USER",optUser);
					insertIbsOrddNegativeParm.setData("OPT_TERM",optTerm);
					insertIbsOrddNegativeParm.setData("COST_AMT",-parmIbsOrdd.getDouble("COST_AMT",m));
					insertIbsOrddNegativeParm.setData("ORDER_CHN_DESC",parmIbsOrdd.getData("ORDER_CHN_DESC", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CHN_DESC", m));
					insertIbsOrddNegativeParm.setData("COST_CENTER_CODE",parmIbsOrdd.getData("COST_CENTER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("COST_CENTER_CODE", m));
					insertIbsOrddNegativeParm.setData("SCHD_CODE",parmIbsOrdd.getData("SCHD_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("SCHD_CODE", m));
					insertIbsOrddNegativeParm.setData("CLNCPATH_CODE",parmIbsOrdd.getData("CLNCPATH_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CLNCPATH_CODE", m));
					insertIbsOrddNegativeParm.setData("DS_FLG",parmIbsOrdd.getData("DS_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DS_FLG", m));
					insertIbsOrddNegativeParm.setData("KN_FLG",parmIbsOrdd.getData("KN_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("KN_FLG", m));
					insertIbsOrddNegativeParm.setData("INCLUDE_FLG",parmIbsOrdd.getData("INCLUDE_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INCLUDE_FLG", m));
					insertIbsOrddNegativeParm.setData("INCLUDE_EXEC_FLG","Y");//数据处理状态
					seqNo++;
					result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
			        if (result.getErrCode() < 0) {
			                err(result.getErrName() + " " + result.getErrText());
			                return result;
			        }
//			        System.out.println("费用明细负数据插入成功！");
				}						
			}
			maxCaseNoSeq++;
			//step8:费用主档插入正数据（修改身份后的费用）
			//插入ibs_ordm正数据
			insertIbsOrdMPositiveParm = new TParm();					
			insertIbsOrdMPositiveParm.setData("CASE_NO",parmIbsOrdmForUnBil.getData("CASE_NO",n));					
			insertIbsOrdMPositiveParm.setData("CASE_NO_SEQ",maxCaseNoSeq);										
			insertIbsOrdMPositiveParm.setData("BILL_DATE",sysDate);
			insertIbsOrdMPositiveParm.setData("IPD_NO",parmIbsOrdmForUnBil.getData("IPD_NO", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("IPD_NO", n));
			insertIbsOrdMPositiveParm.setData("MR_NO",parmIbsOrdmForUnBil.getData("MR_NO", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("MR_NO", n));
			insertIbsOrdMPositiveParm.setData("DEPT_CODE",parmIbsOrdmForUnBil.getData("DEPT_CODE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("DEPT_CODE", n));
			insertIbsOrdMPositiveParm.setData("STATION_CODE",parmIbsOrdmForUnBil.getData("STATION_CODE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("STATION_CODE", n));
			insertIbsOrdMPositiveParm.setData("BED_NO",parmIbsOrdmForUnBil.getData("BED_NO", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("BED_NO", n));
			insertIbsOrdMPositiveParm.setData("DATA_TYPE",parmIbsOrdmForUnBil.getData("DATA_TYPE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("DATA_TYPE", n));
			insertIbsOrdMPositiveParm.setData("BILL_NO","");
			insertIbsOrdMPositiveParm.setData("OPT_USER",optUser);
			insertIbsOrdMPositiveParm.setData("OPT_TERM",optTerm);
			insertIbsOrdMPositiveParm.setData("REGION_CODE",parmIbsOrdmForUnBil.getData("REGION_CODE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("REGION_CODE", n));
			insertIbsOrdMPositiveParm.setData("COST_CENTER_CODE",parmIbsOrdmForUnBil.getData("COST_CENTER_CODE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("COST_CENTER_CODE", n));
			result =IBSOrdmTool.getInstance().insertdataM(insertIbsOrdMPositiveParm,connection);
	        if (result.getErrCode() < 0) {
	                err(result.getErrName() + " " + result.getErrText());
	                return result;
	        }
//	        System.out.println("费用主档正数据插入成功！");
			//插入ibs_ordd正数据
			seqNo = 1;
			
			if(parmIbsOrdd.getCount()>0){
				TParm insertIbsOrddPositiveParm=null;
				maxGroupNo=maxGroupNo+1;
				String ordersetCode=parmIbsOrdd.getValue("ORDERSET_CODE", 0);
				String groupNoSeq=parmIbsOrdd.getValue("ORDERSET_GROUP_NO", 0);
				String orderNoSeq=parmIbsOrdd.getValue("CASE_NO_SEQ", 0);
				for(int m = 0;m<parmIbsOrdd.getCount();m++){
					//step9:费用明细档插入正数据（修改身份后的费用）
//					System.out.println("循环第"+m+"次费用明细档");
					//ibs_ordd插入负数据  case_no_seq_new1,bill_date-->sysdate,金额为负
					insertIbsOrddPositiveParm = new TParm();
					insertIbsOrddPositiveParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", m));					
					insertIbsOrddPositiveParm.setData("CASE_NO_SEQ",maxCaseNoSeq);													
					insertIbsOrddPositiveParm.setData("SEQ_NO",seqNo);
					insertIbsOrddPositiveParm.setData("BILL_DATE",sysDate);
					insertIbsOrddPositiveParm.setData("EXEC_DATE",sysDate);
					insertIbsOrddPositiveParm.setData("ORDER_NO",parmIbsOrdd.getData("ORDER_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_NO", m));
					insertIbsOrddPositiveParm.setData("ORDER_SEQ",parmIbsOrdd.getData("ORDER_SEQ", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_SEQ", m));
					insertIbsOrddPositiveParm.setData("ORDER_CODE",parmIbsOrdd.getData("ORDER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CODE", m));
					insertIbsOrddPositiveParm.setData("ORDER_CAT1_CODE",parmIbsOrdd.getData("ORDER_CAT1_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CAT1_CODE", m));
					insertIbsOrddPositiveParm.setData("CAT1_TYPE",parmIbsOrdd.getData("CAT1_TYPE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CAT1_TYPE", m));
					//添加集合医嘱数据组号重新获得======pangben 2016-2-22
					if (null!=parmIbsOrdd.getData("ORDERSET_CODE", m)&&parmIbsOrdd.getValue("ORDERSET_CODE", m).length()>0) {//集合医嘱
						if (ordersetCode.equals(parmIbsOrdd.getValue("ORDERSET_CODE", m))&&
								groupNoSeq.equals(parmIbsOrdd.getValue("ORDERSET_GROUP_NO", m))&&
								orderNoSeq.equals(parmIbsOrdd.getValue("CASE_NO_SEQ", m))) {
							insertIbsOrddPositiveParm.setData("ORDERSET_GROUP_NO",maxGroupNo);
						}else{
							maxGroupNo=maxGroupNo+1;
							insertIbsOrddPositiveParm.setData("ORDERSET_GROUP_NO",maxGroupNo);
							ordersetCode=parmIbsOrdd.getValue("ORDERSET_CODE", m);
							groupNoSeq=parmIbsOrdd.getValue("ORDERSET_GROUP_NO", m);
							orderNoSeq=parmIbsOrdd.getValue("CASE_NO_SEQ", m);
						}
					}else{
						insertIbsOrddPositiveParm.setData("ORDERSET_GROUP_NO",parmIbsOrdd.getData("ORDERSET_GROUP_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_GROUP_NO", m));
					}
					//insertIbsOrddPositiveParm.setData("ORDERSET_GROUP_NO",parmIbsOrdd.getData("ORDERSET_GROUP_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_GROUP_NO", m));
					insertIbsOrddPositiveParm.setData("ORDERSET_CODE",parmIbsOrdd.getData("ORDERSET_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_CODE", m));
					insertIbsOrddPositiveParm.setData("INDV_FLG",parmIbsOrdd.getData("INDV_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INDV_FLG", m));
					insertIbsOrddPositiveParm.setData("DEPT_CODE",parmIbsOrdd.getData("DEPT_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DEPT_CODE", m));
					insertIbsOrddPositiveParm.setData("STATION_CODE",parmIbsOrdd.getData("STATION_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("STATION_CODE", m));
					insertIbsOrddPositiveParm.setData("DR_CODE",parmIbsOrdd.getData("DR_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DR_CODE", m));
					insertIbsOrddPositiveParm.setData("EXE_DEPT_CODE",parmIbsOrdd.getData("EXE_DEPT_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DEPT_CODE", m));
					insertIbsOrddPositiveParm.setData("EXE_STATION_CODE",parmIbsOrdd.getData("EXE_STATION_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_STATION_CODE", m));
					insertIbsOrddPositiveParm.setData("EXE_DR_CODE",parmIbsOrdd.getData("EXE_DR_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DR_CODE", m));
					insertIbsOrddPositiveParm.setData("MEDI_QTY",parmIbsOrdd.getDouble("MEDI_QTY",m));
					insertIbsOrddPositiveParm.setData("MEDI_UNIT",parmIbsOrdd.getData("MEDI_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("MEDI_UNIT", m));
					insertIbsOrddPositiveParm.setData("DOSE_CODE",parmIbsOrdd.getData("DOSE_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSE_CODE", m));
					insertIbsOrddPositiveParm.setData("FREQ_CODE",parmIbsOrdd.getData("FREQ_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("FREQ_CODE", m));
					insertIbsOrddPositiveParm.setData("TAKE_DAYS",parmIbsOrdd.getData("TAKE_DAYS", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("TAKE_DAYS", m));
					insertIbsOrddPositiveParm.setData("DOSAGE_QTY",parmIbsOrdd.getDouble("DOSAGE_QTY",m));
					insertIbsOrddPositiveParm.setData("DOSAGE_UNIT",parmIbsOrdd.getData("DOSAGE_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSAGE_UNIT", m));
					insertIbsOrddPositiveParm.setData("OWN_PRICE",parmIbsOrdd.getDouble("OWN_PRICE",m));
					insertIbsOrddPositiveParm.setData("NHI_PRICE",parmIbsOrdd.getDouble("NHI_PRICE",m));							
					insertIbsOrddPositiveParm.setData("OWN_FLG",parmIbsOrdd.getData("OWN_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("OWN_FLG", m));
					insertIbsOrddPositiveParm.setData("BILL_FLG",parmIbsOrdd.getData("BILL_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("BILL_FLG", m));
					insertIbsOrddPositiveParm.setData("REXP_CODE",parmIbsOrdd.getData("REXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REXP_CODE", m));
					insertIbsOrddPositiveParm.setData("BILL_NO", "");
					insertIbsOrddPositiveParm.setData("HEXP_CODE",parmIbsOrdd.getData("HEXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("HEXP_CODE", m));
					insertIbsOrddPositiveParm.setData("BEGIN_DATE",parmIbsOrdd.getData("BEGIN_DATE", m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("BEGIN_DATE", m));
					insertIbsOrddPositiveParm.setData("END_DATE",parmIbsOrdd.getData("END_DATE", m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("END_DATE", m));							
					insertIbsOrddPositiveParm.setData("REQUEST_FLG",parmIbsOrdd.getData("REQUEST_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_FLG", m));
					insertIbsOrddPositiveParm.setData("REQUEST_NO",parmIbsOrdd.getData("REQUEST_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_NO", m));
					insertIbsOrddPositiveParm.setData("INV_CODE",parmIbsOrdd.getData("INV_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INV_CODE", m));
					insertIbsOrddPositiveParm.setData("OPT_USER",optUser);
					insertIbsOrddPositiveParm.setData("OPT_TERM",optTerm);							
					insertIbsOrddPositiveParm.setData("ORDER_CHN_DESC",parmIbsOrdd.getData("ORDER_CHN_DESC", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CHN_DESC", m));
					insertIbsOrddPositiveParm.setData("COST_CENTER_CODE",parmIbsOrdd.getData("COST_CENTER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("COST_CENTER_CODE", m));
					insertIbsOrddPositiveParm.setData("SCHD_CODE",parmIbsOrdd.getData("SCHD_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("SCHD_CODE", m));
					insertIbsOrddPositiveParm.setData("CLNCPATH_CODE",parmIbsOrdd.getData("CLNCPATH_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CLNCPATH_CODE", m));
					insertIbsOrddPositiveParm.setData("DS_FLG",parmIbsOrdd.getData("DS_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DS_FLG", m));
					insertIbsOrddPositiveParm.setData("KN_FLG",parmIbsOrdd.getData("KN_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("KN_FLG", m));
					insertIbsOrddPositiveParm.setData("INCLUDE_FLG","N");
		    		BIL bil = new BIL();
		    		String chargeHospCode = parmIbsOrdd.getData("HEXP_CODE", m)+"";
		    		String orderCode = parmIbsOrdd.getData("ORDER_CODE", m)+"";
		    		double tot_amt_new = 0.00;
		    		double cost_amt_new = 0.00;
		    		double own_amt = parmIbsOrdd.getDouble("OWN_AMT",m);
		    		double ownRate =1;
		    		//====pangben 2015-7-28 添加套餐病人修改身份，差异医嘱不打折操作
		    		if (!newBodyflg) {//婴儿不需要操作
						if (checkLumpWorkFlg&&null!=odiParm) {//套餐病人操作
							if (parmIbsOrdd.getValue("ORDER_CODE", m)
									.equals(odiParm.getValue("LUMPWORK_ORDER_CODE",0)) ) {//套餐病人 套餐差异医嘱不可以执行折扣
								tot_amt_new=own_amt;
								cost_amt_new = own_amt;
							}else{
								ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,chargeHospCode,orderCode) ;
								tot_amt_new =  own_amt*ownRate;
								cost_amt_new = own_amt*ownRate;
							}
						}else{
							ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,chargeHospCode,orderCode) ;
							tot_amt_new =  own_amt*ownRate;
							cost_amt_new = own_amt*ownRate;
						}
					}else{
						ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,chargeHospCode,orderCode) ;
						tot_amt_new =  own_amt*ownRate;
						cost_amt_new = own_amt*ownRate;
					}
		    		//===========pangben 29150212  修改身份折扣金额， 入院身份为0.7折 ，改成0.5折 要用自费金额* 当前折扣
		    		insertIbsOrddPositiveParm.setData("TOT_AMT",tot_amt_new);
		    		insertIbsOrddPositiveParm.setData("OWN_AMT",own_amt);
					insertIbsOrddPositiveParm.setData("OWN_RATE",ownRate);
		    		insertIbsOrddPositiveParm.setData("COST_AMT",cost_amt_new);
		    		
		    		seqNo++;
		    		//System.out.println("明细档正数据参数："+insertIbsOrdMPositiveParm);
		    		result =IBSOrdmTool.getInstance().insertdataD(insertIbsOrddPositiveParm,connection);
			        if (result.getErrCode() < 0) {
			                err(result.getErrName() + " " + result.getErrText());
			                return result;
			        }
//			        System.out.println("费用明细档正数据插入成功！");
				}						
			}
			maxCaseNoSeq++;
		  }
		}
		//未结账的费用的处理=======================结束
		//step13:更改病案首页身份
		String sqlMro = "SELECT MRO_CTZ,CTZ1_CODE FROM  MRO_RECORD WHERE CASE_NO = '"+caseNo+"' AND MR_NO = '"+selPatientParm.getValue("MR_NO",0).toString()+"'";
		TParm parmMro = new TParm(TJDODBTool.getInstance().select(sqlMro));
		//.out.println("首页-------》"+sqlMro+parmMro.getCount());
		if(parmMro.getCount()>0){
			//System.out.println("更改病案首页身份");
			String sqlCTZ = "SELECT MRO_CTZ FROM SYS_CTZ WHERE CTZ_CODE = '"+CTZ1+"'";
			TParm parmCTZ = new TParm(TJDODBTool.getInstance().select(sqlCTZ));
			String mroCTZ = "";
			//System.out.println("sqlCTZ-->"+sqlCTZ);
			if(parmCTZ.getCount()>0){
				mroCTZ = parmCTZ.getData("MRO_CTZ",0)+"";
			}
			String updMROSql = "UPDATE  MRO_RECORD SET CTZ1_CODE = '"+CTZ1+"' , MRO_CTZ = '"+mroCTZ+"' WHERE CASE_NO = '"+caseNo+"' AND MR_NO = '"+selPatientParm.getValue("MR_NO",0).toString()+"'";
			//System.out.println("病案首页身份修改sql"+updMROSql);
			result = new TParm(TJDODBTool.getInstance().update(updMROSql));
			//System.out.println("病案首页身份修改成功！");
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}
		}
		// step14：插入日志信息
		String mr_no = selPatientParm.getValue("MR_NO").toString();
		if (mr_no != null && mr_no.length() != 0) {
			mr_no = mr_no.substring(1, mr_no.length() - 1);
		} else {
			mr_no = "";
		}

		String ipd_no = selPatientParm.getData("IPD_NO").toString();
		if (ipd_no != null && ipd_no.length() != 0) {
			ipd_no = ipd_no.substring(1, ipd_no.length() - 1);
		} else {
			ipd_no = "";
		}

		String bed_no = selPatientParm.getData("BED_NO").toString();
		if (bed_no != null && bed_no.length() != 0) {
			bed_no = bed_no.substring(1, bed_no.length() - 1);
		} else {
			bed_no = "";
		}

		String region_code = selPatientParm.getData("REGION_CODE").toString();
		if (region_code != null && region_code.length() != 0) {
			region_code = region_code.substring(1, region_code.length() - 1);
		} else {
			region_code = "";
		}

		String ctz1_code = selPatientParm.getData("CTZ1_CODE").toString();
		if (ctz1_code != null && ctz1_code.length() != 0) {
			ctz1_code = ctz1_code.substring(1, ctz1_code.length() - 1);
		} else {
			ctz1_code = "";
		}

		String ctz2_code = selPatientParm.getData("CTZ2_CODE").toString();
		if (ctz2_code != null && ctz2_code.length() != 0) {
			ctz2_code = ctz2_code.substring(1, ctz2_code.length() - 1);
		} else {
			ctz2_code = "";
		}

		String ctz3_code = selPatientParm.getData("CTZ3_CODE").toString();
		if (ctz3_code != null && ctz3_code.length() != 0) {
			ctz3_code = ctz3_code.substring(1, ctz3_code.length() - 1);
		} else {
			ctz3_code = "";
		}

		int seq_no = 1;
		int count = selCountOfCaseno(caseNo);
		seq_no += count;
		String logSql = "INSERT INTO "
				+ " ADM_CTZ_LOG(CASE_NO,SEQ_NO,MR_NO,IPD_NO,BED_NO,REGION_CODE,CTZ_CODE1_O,CTZ_CODE2_O,CTZ_CODE3_O,CTZ_CODE1_N,CTZ_CODE2_N,CTZ_CODE3_N,OPT_USER,OPT_DATE,OPT_TERM) "
				+ " VALUES ('"
				+ caseNo
				+ "','"
				+ seq_no
				+ "','"
				+ mr_no
				+ "','"
				+ ipd_no
				+ "','"
				+ bed_no
				+ "','"
				+ region_code
				+ "','"
				+ ctz1_code
				+ "','"
				+ ctz2_code
				+ "','"
				+ ctz3_code
				+ "','"
				+ CTZ1
				+ "','"
				+ CTZ2
				+ "','"
				+ CTZ3
				+ "','"
				+ optUser
				+ "',SYSDATE,'"
				+ optTerm + "')";
		result = new TParm(TJDODBTool.getInstance().update(logSql));
		
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	/**
	 * 计算单价，套餐使用 ，套餐外金额计算
	 * @param orderCode
	 * @param level
	 * @return
	 */
	public double[] getOrderOwnPrice(String orderCode,String level){
		 double []sum={0.00,0.00};
		 double ownPrice=0.00;
		 TParm feeParm = SYSFeeTool.getInstance().getFeeData(orderCode);
		 if ("2".equals(level)) {
             ownPrice = feeParm.getDouble("OWN_PRICE2", 0);
         } else if ("3".equals(level)) {
             ownPrice = feeParm.getDouble("OWN_PRICE3", 0);
         } else
             ownPrice = feeParm.getDouble("OWN_PRICE", 0);
		 sum[0]=ownPrice;
		 sum[1]= feeParm.getDouble("NHI_PRICE", 0);
		 return sum;
	}
	private double getLumpOrderOwnPriceOne(String caseNoNew,String lumpWorkCode ,String orderCode,double ownPriceNew){
		TParm lumpParm=new TParm();
		lumpParm.setData("CASE_NO",caseNoNew);
		lumpParm.setData("PACKAGE_CODE",lumpWorkCode);
		lumpParm.setData("ORDER_CODE",orderCode);
		TParm feeParm= MEMTool.getInstance().selectMemPackageSectionDByCaseNo(lumpParm); 
		double ownPrice=0.00;
		if(feeParm.getErrCode()<0||feeParm.getCount()<=0){//套餐外医嘱，正常计费
			ownPrice = ownPriceNew;
		}else{
    		ownPrice = feeParm.getDouble("OWN_PRICE", 0);
		}
		return ownPrice;
	}
	/**
	 * 计算单价，套餐使用 ，套内金额计算
	 * @param caseNoNew
	 * @param lumpWorkCode
	 * @param orderCode
	 * @param level
	 * @return
	 */
	public double getLumpOrderOwnPrice(String caseNoNew,String lumpWorkCode ,String orderCode,String level){
		TParm lumpParm=new TParm();
		lumpParm.setData("CASE_NO",caseNoNew);
		lumpParm.setData("PACKAGE_CODE",lumpWorkCode);
		lumpParm.setData("ORDER_CODE",orderCode);
		TParm feeParm= MEMTool.getInstance().selectMemPackageSectionDByCaseNo(lumpParm); 
		double ownPrice=0.00;
		double [] sumPrice=new double[2];
		if(feeParm.getErrCode()<0||feeParm.getCount()<=0){//套餐外医嘱，正常计费
			sumPrice = getOrderOwnPrice(orderCode,level);
			ownPrice = sumPrice[0];
		}else{
    		ownPrice = feeParm.getDouble("OWN_PRICE", 0);
		}
		return ownPrice;
	}
	/**
	 * 校验是否存在多个临床路径
	 * @return
	 */
	public TParm onCheckClpDiff(String caseNo){
		TParm clpCParm=new TParm();
		clpCParm.setData("CASE_NO",caseNo);
		TParm clpCheckParm=IBSOrderdTool.getInstance().selClpClncpathCode(clpCParm);
		TParm result=new TParm();
		//存在临床路径
		if (null!=clpCheckParm.getValue("CLNCPATH_CODE_SUM",0)&&clpCheckParm.getValue("CLNCPATH_CODE_SUM",0).length()>0) {
			TParm clpSchdCodeParm=IBSOrderdTool.getInstance().selClpSchdCode(clpCParm);
			String sql="";
			if (clpSchdCodeParm.getCount()>0) {
				if(null!=clpCheckParm.getValue("SCHD_CODE",0)&&
						clpCheckParm.getValue("SCHD_CODE",0).length()>0){
					sql="UPDATE IBS_ORDD SET SCHD_CODE='"+clpCheckParm.getValue("SCHD_CODE",0)+
					"' WHERE CASE_NO='"+caseNo+"' AND SCHD_CODE IS NULL ";
				}else{
					String sqlClp="SELECT SCHD_CODE FROM CLP_THRPYSCHDM WHERE CLNCPATH_CODE='"+clpCheckParm.getValue("CLNCPATH_CODE_SUM",0)+"' ORDER BY SEQ";
					TParm clpParm = new TParm(TJDODBTool.getInstance().select(sqlClp));
					sql="UPDATE IBS_ORDD SET SCHD_CODE='"+clpParm.getValue("SCHD_CODE",0)+
					"' WHERE CASE_NO='"+caseNo+"' AND SCHD_CODE IS NULL ";
				}
				result = new TParm(TJDODBTool.getInstance().update(sql));
			}
			if (clpCheckParm.getCount()>1) {//存在多个路径情况
				sql="UPDATE IBS_ORDD SET CLNCPATH_CODE='"+clpCheckParm.getValue("CLNCPATH_CODE_SUM",0)+
				"' WHERE CASE_NO='"+caseNo+"' AND (CLNCPATH_CODE IS NULL OR CLNCPATH_CODE<>'"+
				clpCheckParm.getValue("CLNCPATH_CODE_SUM",0)+"')";
				result = new TParm(TJDODBTool.getInstance().update(sql));
			}
		}
		return result;
	}
}
