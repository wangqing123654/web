package jdo.bil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

import org.apache.bcel.generic.IF_ICMPGE;

import jdo.ekt.EKTNewIO;
import jdo.ekt.EKTNewTool;
import jdo.ekt.EKTTool;
import jdo.ekt.EKTpreDebtTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 费用减免
 * </p>
 * 
 * <p>
 * Description: 费用减免
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author caowl 20140504
 * @version 1.0
 */
public class BILReduceTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static BILReduceTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return OPBReceiptTool
	 */
	public static BILReduceTool getInstance() {
		if (instanceObject == null)
			instanceObject = new BILReduceTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public BILReduceTool() {
		setModuleName("bil\\BILReduceModule.x");
		onInit();
	}

	/**
	 * 住院
	 * 
	 * @return
	 */
	public TParm onInsertIBSBilReduce(TParm parm, TConnection connection) {
		// 流水
		String receiptNo = parm.getValue("RECEIPT_NO");
		TParm ReduceMParm = parm.getParm("ReduceMParm");
		ReduceMParm.setData("BUSINESS_NO", receiptNo);
		//主表
		TParm result = update("insertBilReduceM", ReduceMParm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		//细表
		TParm ReduceDParm = parm.getParm("ReduceDParm");
		for (int i = 0; i < ReduceDParm.getCount("REDUCE_NO"); i++) {
			TParm parmRow=ReduceDParm.getRow(i);
			result = update("insertBilReduceD", parmRow, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
		}
		return result;
	}
	/**
	 * 门诊减免打票---现金打票
	 * 正流程
	 * @return
	 * =====pangben 2014-8-21
	 */
	public TParm onInsertOPDBilReduceCash(TParm parm, TConnection connection){
		// 流水
		String receiptNo = parm.getValue("RECEIPT_NO");
		TParm ReduceMParm = parm.getParm("parmForReduceM");
		ReduceMParm.setData("BUSINESS_NO", receiptNo);
//		double reduceLastAmt=parm.getDouble("REDUCELAST_AMT");//剩余减免金额=减免金额-医疗卡金额
//		String mrNo=parm.getValue("MR_NO");
//		TParm opbParm=parm.getParm("OPB_RECEIPT_PARM");//票据数据
		//主表
		TParm result = update("insertBilReduceM", ReduceMParm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.rollback();
			connection.close();
			return result;
		}
		//细表
		TParm ReduceDParm = parm.getParm("parmForReduceD");
		for (int i = 0; i < ReduceDParm.getCount("REDUCE_NO"); i++) {
			TParm parmRow=ReduceDParm.getRow(i);
			result = update("insertBilReduceD", parmRow, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}
		}
		//添加礼品卡、现金折扣券减免功能=====pangben 2014-7-17
		//礼品卡、现金折扣券减免操作===pangben 2014-7-17
//		if (reduceLastAmt>0) {
//			TParm inparm=new TParm();
//			inparm.setData("OPT_USER_T",parm.getValue("OPT_USER"));
//			inparm.setData("OPT_TERM_T",parm.getValue("OPT_TERM"));
//			result=newBillTypeREduce(mrNo, reduceLastAmt, opbParm, inparm, connection);
//		}
//		if (result.getErrCode() < 0) {
//			err("ERR:" + result.getErrCode() + result.getErrText()
//					+ result.getErrName());
//			return result;
//		}	
		connection.commit();
//		connection.close();
		return result;
	}
	/**
	 * 门诊减免打票
	 * 正流程
	 * @return
	 */
	public TParm onInsertOPDBilReduce(TParm parm, TConnection connection) {
		// 流水
		String receiptNo = parm.getValue("RECEIPT_NO");
		TParm ReduceMParm = parm.getParm("parmForReduceM");
		ReduceMParm.setData("BUSINESS_NO", receiptNo);
		double reduceLastAmt=parm.getDouble("REDUCELAST_AMT");//剩余减免金额=减免金额-医疗卡金额
		String mrNo=parm.getValue("MR_NO");
		TParm opbParm=parm.getParm("OPB_RECEIPT_PARM");//票据数据
//		String opbExeBilPrint=null!=parm.getValue("OPB_EXE_BILPRINT")&&parm.getValue("OPB_EXE_BILPRINT").equals("Y")?
//				parm.getValue("OPB_EXE_BILPRINT"):"";//判断流程操作，OPB_EXE_BILPRINT:Y：正流程  其他：退票逆流成
		//主表
		TParm result = update("insertBilReduceM", ReduceMParm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.rollback();
			connection.close();
			return result;
		}
		//细表
		TParm ReduceDParm = parm.getParm("parmForReduceD");
		for (int i = 0; i < ReduceDParm.getCount("REDUCE_NO"); i++) {
			TParm parmRow=ReduceDParm.getRow(i);
			result = update("insertBilReduceD", parmRow, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}
		}
		
		//==========20141204 yanjing 注=========================
//		// EKT_TRADE表添加减免的金额STATE =1
//		TParm parmForEktTrade = new TParm();
//		parmForEktTrade = parm.getParm("parmForEktTrade");
//		result =updateForEktTrade(
//				parmForEktTrade, connection);
//		if (result.getErrCode() < 0) {
//			err("ERR:" + result.getErrCode() + result.getErrText()
//					+ result.getErrName());
//			return result;
//		}
//		// 更新EKT_MASTER表金额
//		TParm parmForEktMaster = new TParm();
//		parmForEktMaster = parm.getParm("parmForEktMaster");
//		result =updateForEktMaster(
//				parmForEktMaster, connection);
//		if (result.getErrCode() < 0) {
//			err("ERR:" + result.getErrCode() + result.getErrText()
//					+ result.getErrName());
//			return result;
//		}
//		//添加礼品卡、现金折扣券减免功能=====pangben 2014-7-17
//		//礼品卡、现金折扣券减免操作===pangben 2014-7-17
//		if (reduceLastAmt>0) {
//			TParm inparm=new TParm();
//			inparm.setData("OPT_USER_T",parm.getValue("OPT_USER"));
//			inparm.setData("OPT_TERM_T",parm.getValue("OPT_TERM"));
//			result=newBillTypeREduce(mrNo, reduceLastAmt, opbParm, inparm, connection);
//		}
//		if (result.getErrCode() < 0) {
//			err("ERR:" + result.getErrCode() + result.getErrText()
//					+ result.getErrName());
//			return result;
//		}	
		//==========20141204 yanjing 注=========================
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * 门诊/住院减免退票
	 * 逆流成/现金
	 * @return
	 */
	public TParm onBackOpdBilReduceCash(TParm opbParm,TParm inparm,TConnection connection){
       TParm result=this.onRefundOPDBilReduce(inparm,opbParm,connection);
       if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
       return result;
	}
	/**
	 * 门诊减免退票
	 * 逆流成/医疗卡
	 * @return
	 */
	public TParm onBackOPDBilReduce(TParm opbParm,TParm inparm,TConnection connection) {
		TParm  result=new TParm();
		String mrNo=opbParm.getValue("MR_NO");
		double reduceAmt=opbParm.getDouble("REDUCE_AMT");
		double reduceLastAmt=opbParm.getDouble("REDUCELAST_AMT");//剩余减免金额=减免金额-医疗卡金额
		double totAmt=EKTpreDebtTool.getInstance().getEkeMaster(mrNo);//医疗卡余额
		double payOther3 = EKTpreDebtTool.getInstance().getPayOther(mrNo, EKTpreDebtTool.PAY_TOHER3);
		double payOther4 = EKTpreDebtTool.getInstance().getPayOther(mrNo, EKTpreDebtTool.PAY_TOHER4);
		BigDecimal totAmtbd = new BigDecimal(totAmt).setScale(2, RoundingMode.HALF_UP);
		BigDecimal payOther3bd = new BigDecimal(payOther3).setScale(2, RoundingMode.HALF_UP);
		BigDecimal payOther4bd = new BigDecimal(payOther4).setScale(2, RoundingMode.HALF_UP);
		BigDecimal reduceAmtbd = new BigDecimal(reduceAmt).setScale(2, RoundingMode.HALF_UP);
        //查询医疗卡金额
		TParm ektParm=EKTpreDebtTool.getInstance().getEktCardAndAmt(inparm.getValue("MR_NO"));
		boolean isCashFull = true;//====yanjing 20141224
        if(reduceAmt>(totAmt-payOther3-payOther4)){//当前
        	isCashFull = false;
        	result.setErrCode(-1);
        	result.setErrName("onBackOPDBilReduce->onCheck");
        	result.setErrText("减免金额大于医疗卡现金余额,请现金充值:"+(reduceAmtbd.subtract((totAmtbd.subtract(payOther3bd.add(payOther4bd))))));
        	//TODO bil_opb_recp
        	//TODO ekt_trade greenPath payOther3 payOther4 ekt_master=amt-greenPath 3 opbt
        	// ekt_master=amt-greenPath-payOther3-payOther4
        	//TODO opd_order bill_flg bill_user bill_date 
        	//TODO onRefundOPDBilReduce
        	//========yanjing modify 20141208 start
        	result=this.onRefundOPDBilReduce(inparm,opbParm,connection);//减免作废
            if (result.getErrCode() < 0) {
    			err("ERR:" + result.getErrCode() + result.getErrText()
    					+ result.getErrName());
    			return result;
    		}
            //入参的设置
        	
            TParm reduceInParm = new TParm();
            reduceInParm.setData("CASE_NO", opbParm.getValue("CASE_NO"));//就诊号
            reduceInParm.setData("MR_NO", mrNo);//病案号
            
            reduceInParm.setData("RECEIPT_NO", opbParm.getValue("RECEIPT_NO"));//票据号
            reduceInParm.setData("TRADE_NO", this.onSelectBusinessNo(opbParm.getValue("RECEIPT_NO")));//票据号
            reduceInParm.setData("GREEN_BALANCE",
            		this.setGreenBalanceForReg(opbParm.getValue("CASE_NO"), opbParm.getValue("RECEIPT_NO")));//绿色通道金额
            reduceInParm.setData("CARD_NO", ektParm.getValue("CARD_NO"));//医疗卡的卡号
            reduceInParm.setData("TRADE_SUM_NO", this.onSelectBusinessNo(opbParm.getValue("RECEIPT_NO")));//用于查询payother3和payother4
            //计算PAY_OTHER3和PAY_OTHER4的值
            TParm tPayOther = EKTNewTool.getInstance().selectTradeNo(reduceInParm);
            double tPayOther3 = tPayOther.getDouble("PAY_OTHER3", 0);
            double tPayOther4 = tPayOther.getDouble("PAY_OTHER4", 0);
            reduceInParm.setData("PAY_OTHER3", tPayOther3);//礼品卡
            reduceInParm.setData("PAY_OTHER4", tPayOther4);//现金折扣券
//   		 更新EKT_MASTER表金额
            TParm parm=this.onResetEktParm(inparm,ektParm);
    		TParm parmForEktMaster = new TParm();
    		parmForEktMaster = parm.getParm("parmForEktMaster");
    		parmForEktMaster.setData("CURRENT_BALANCE", StringTool.round(opbParm.getDouble("AR_AMT")+totAmt-
    				this.onSelectGreen(opbParm.getValue("RECEIPT_NO")), 2));//设定医疗卡的余额，医疗卡余额+回退的金额
    		result =updateForEktMaster(parmForEktMaster, connection);
    		if (result.getErrCode() < 0) {
    			err("ERR:" + result.getErrCode() + result.getErrText()
    					+ result.getErrName());
    			return result;
    		}
        	result= this.onReSetEktForReduce(reduceInParm,connection);
        	result.setData("CASHfULL_FLG", isCashFull);
        	return result;
        }
        result=this.onRefundOPDBilReduce(inparm,opbParm,connection);
        if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
      //========yanjing modify 20141208 end
//        // EKT_TRADE表添加减免的金额STATE =1
        TParm parm=this.onResetEktParm(inparm,ektParm);
		TParm parmForEktTrade = new TParm();
		parmForEktTrade = parm.getParm("parmForEktTrade");
		result =updateForEktTrade(parmForEktTrade, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
//		 更新EKT_MASTER表金额
		TParm parmForEktMaster = new TParm();
		parmForEktMaster = parm.getParm("parmForEktMaster");
		result =updateForEktMaster(parmForEktMaster, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		//礼品卡、现金折扣券减免操作===pangben 2014-7-17
//		if (reduceLastAmt>0) {
//			result=reNewBillTypeREduce(mrNo, reduceLastAmt, opbParm, inparm, connection);
//			if (result.getErrCode() < 0) {
//				err("ERR:" + result.getErrCode() + result.getErrText()
//						+ result.getErrName());
//				return result;
//			}	
//		}
		result.addData("CASHfULL_FLG", isCashFull);
		return result;
	}
	/**
	 * 根据RECEIPT_NO查询BUSINESS_NO
	 * yanjing 20141209 
	 */
	private String onSelectBusinessNo(String receiptNo){
		String businessNo = "";
		String sql = "SELECT DISTINCT BUSINESS_NO FROM OPD_ORDER_HISTORY WHERE RECEIPT_NO = '"+receiptNo+"'";
		TParm result = new TParm (TJDODBTool.getInstance().select(sql));
		businessNo = result.getValue("BUSINESS_NO", 0);
		return businessNo;
	}
	/**
	 * 恢复reg_patadm表中的绿色通道的金额
	 * yanjing 20141208 
	 */
	private double setGreenBalanceForReg(String caseNo,String tradeNo){
		TParm result = new TParm();
		double greenBalance = 0.00;
		//查询挂号表中绿色通道的余额
		String regSql = "SELECT GREEN_BALANCE FROM REG_PATADM WHERE CASE_NO = '"+caseNo+"'";
		result = new TParm (TJDODBTool.getInstance().select(regSql));
		double regGreenBalance = result.getDouble("GREEN_BALANCE",0);
		double ektGreenBalance = this.onSelectGreen(tradeNo);
		greenBalance = StringTool.round(regGreenBalance +ektGreenBalance, 2);
		return greenBalance;
	}
	/**
	 * 查询EKT_TRADE表中的绿色通道的使用金额
	 * yanjing 20141209
	 */
	private double onSelectGreen(String tradeNo){
		//查询医疗卡交易表中绿色通道的交易金额
		String ektSql = "SELECT GREEN_BUSINESS_AMT FROM EKT_TRADE WHERE TRADE_NO = '"+this.onSelectBusinessNo(tradeNo)+"'";//trade_no 即为opd_order中的BUSINESS_NO
		TParm result = new TParm (TJDODBTool.getInstance().select(ektSql));
		double ektGreenBalance = result.getDouble("GREEN_BUSINESS_AMT",0);
		return ektGreenBalance;
	}
	/**
	 * 礼品卡、现金折扣券减免操作
	 * 退票方法
	 * @param mrNo
	 * @param reduceAmt 减免金额=当前票据中医疗卡的金额-减免金额部分
	 * @param opbParm 
	 * @param inparm
	 * @param connection
	 * @return
	 * pangben 2014-7-16 
	 */
	private TParm reNewBillTypeREduce(String mrNo,double reduceAmt,TParm opbParm,
			TParm inparm,TConnection connection){
		double usedLpk=inparm.getDouble("USED_LPK");//此次打票使用的礼品卡金额
		double usedXjzkq=inparm.getDouble("USED_XJZKQ");//此次打票使用的代金券金额
		TParm bilPayParm=EKTpreDebtTool.getInstance().getPayOtherSum(mrNo, EKTpreDebtTool.PAY_TOHER3, EKTpreDebtTool.PAY_TOHER4);
		double usedAmt=reduceAmt-usedLpk;
		TParm result=new TParm();
		if (usedAmt>0) {//剩余的减免金额（减去医疗卡使用的金额）大于礼品卡使用的金额
			result=reBilRecp(opbParm, bilPayParm, inparm, usedLpk, connection,EKTpreDebtTool.PAY_TOHER3);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode()
						+ result.getErrText() + result.getErrName());
				return result;
			}
			if (usedXjzkq<usedAmt) {
				result.setErr(-1,"减免金额："+usedAmt+"大于代金券使用金额："+usedXjzkq);
				err("ERR:减免金额："+usedAmt+"大于代金券使用金额："+usedXjzkq);
				return result;
			}
			result=reBilRecp(opbParm, bilPayParm, inparm, usedAmt, connection,EKTpreDebtTool.PAY_TOHER4);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode()
						+ result.getErrText() + result.getErrName());
				return result;
			}
		}else{
			result=reBilRecp(opbParm, bilPayParm, inparm, reduceAmt, connection, EKTpreDebtTool.PAY_TOHER3);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode()
						+ result.getErrText() + result.getErrName());
				return result;
			}
		}
		return result;
	}
	/**
	 * 礼品卡、现金折扣券减免操作
	 * 打票方法
	 * @param mrNo
	 * @param reduceAmt 减免金额=当前票据中医疗卡的金额-减免金额部分
	 * @param opbParm 
	 * @param inparm
	 * @param connection
	 * @return
	 * pangben 2014-7-16 
	 */
	private TParm newBillTypeREduce(String mrNo,double reduceAmt,TParm opbParm,
			TParm inparm,TConnection connection){
		//=========pangben 2014-7-16 添加礼品卡和现金折扣券功能，如果此票据是操作这两种支付方式，
		//在打票或者退票时需要将减免金额回冲或者扣除这个金额
		TParm result=new TParm();
		if (opbParm.getDouble("PAY_OTHER3_TEMP")>0
				||opbParm.getDouble("PAY_OTHER4_TEMP")>0) {
			TParm bilPayParm=EKTpreDebtTool.getInstance().getPayOtherSum(mrNo, EKTpreDebtTool.PAY_TOHER3, EKTpreDebtTool.PAY_TOHER4);
			if (bilPayParm.getCount()>0) {
				result=bilRecp(opbParm, bilPayParm, inparm, reduceAmt, 
						connection,"PAY_OTHER3_TEMP",EKTpreDebtTool.PAY_TOHER3);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode()
							+ result.getErrText()
							+ result.getErrName());
					return result;
				}
				if (result.getDouble("REDUCE_AMT")>0) {
					result=bilRecp(opbParm, bilPayParm, inparm, reduceAmt, 
							connection,"PAY_OTHER4_TEMP",EKTpreDebtTool.PAY_TOHER4);
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode()
								+ result.getErrText()
								+ result.getErrName());
						return result;
					}
				}
			}
		}
		return result;
	}
	/**
	 * 减免逆流成退票操作
	 * @param opbParm
	 * @param bilPayParm
	 * @param inparm
	 * @param reduceAmt
	 * @param connection
	 * @param type
	 * @param payOther
	 * @return
	 * ============pangben 2014-7-18
	 */
	private TParm reBilRecp(TParm opbParm, TParm bilPayParm, TParm inparm,
			double reduceAmt, TConnection connection, String payOther) {
		// System.out.println("SADFASDFASDFASDF:::::" + bilPaytemp);
		TParm result = new TParm();
		TParm bilPaytemp = new TParm();
		if (reduceAmt > 0) {
			for (int i = 0; i < bilPayParm.getCount(); i++) {
				bilPaytemp = bilPayParm.getRow(i);
				bilPaytemp.setData("OPT_USER", inparm.getValue("OPT_USER_T"));
				bilPaytemp.setData("OPT_TERM", inparm.getValue("OPT_TERM_T"));
				if (bilPaytemp.getValue("GATHER_TYPE").equals(payOther)) {
					if (reduceAmt - bilPaytemp.getDouble("AMT") > 0) {// 减免金额-礼品卡余额====退票操作
						// 需要判断金额是否充足，AMT为可以使用金额
						result = EKTpreDebtTool.getInstance()
								.updateEktBilPayUsedAmt(bilPaytemp,bilPaytemp.getDouble("SUM_AMT"),// 当前使用金额=总金额
										connection);
						if (result.getErrCode() < 0) {
							err("ERR:" + result.getErrCode()
									+ result.getErrText() + result.getErrName());
							return result;
						}
						// 礼品卡金额不足
						reduceAmt -= bilPayParm.getDouble("AMT", i);
					} else {// 可以使用金额充足直接将减免金额添加到使用的金额中
						result = EKTpreDebtTool.getInstance()
								.updateEktBilPayUsedAmt(
										bilPaytemp,bilPaytemp.getDouble("USED_AMT")
												+ reduceAmt,// 当前使用金额+=减免金额
										connection);
						if (result.getErrCode() < 0) {
							err("ERR:" + result.getErrCode()
									+ result.getErrText() + result.getErrName());
							return result;
						}
						reduceAmt = 0;
						break;
					}
				}

			}
		}
		return result;
	}
	/**
	 * 减免正流程打票操作
	 * @param opbParm
	 * @param bilPayParm
	 * @param inparm
	 * @param reduceAmt
	 * @param connection
	 * @return
	 * =============pangben 2014-7-16
	 */
	private TParm bilRecp(TParm opbParm,TParm bilPayParm,TParm inparm,
			double reduceAmt,TConnection connection,String type,String payOther){
		TParm result=new TParm();
		TParm bilPaytemp=new TParm();
		if (opbParm.getDouble(type) > 0) {// 礼品卡
			for (int i = 0; i < bilPayParm.getCount(); i++) {
				bilPaytemp = bilPayParm.getRow(i);
				// 更新当前操作的礼品卡数据
				bilPaytemp.setData("OPT_USER", inparm.getValue("OPT_USER_T"));
				bilPaytemp.setData("OPT_TERM", inparm.getValue("OPT_TERM_T"));
				if (bilPaytemp.getValue("GATHER_TYPE").equals(payOther)) {// 礼品卡、现金折扣券
					if (reduceAmt + bilPaytemp.getDouble("AMT") > bilPaytemp
							.getDouble("SUM_AMT")) {// 校验减免金额+当前剩余金额>总金额
						result = EKTpreDebtTool.getInstance()
								.updateEktBilPayUsedAmt(bilPaytemp, 0,// 剩余金额很多的情况，减免金额+剩余>总金额说明这次操作使用金额应该为0
										connection);
						if (result.getErrCode() < 0) {
							err("ERR:" + result.getErrCode()
									+ result.getErrText() + result.getErrName());
							return result;
						}
						// 礼品卡使用金额不足
						reduceAmt -= bilPaytemp.getDouble("USED_AMT");
					} else {
						result = EKTpreDebtTool.getInstance()
								.updateEktBilPayUsedAmt(
										bilPaytemp,
										bilPaytemp.getDouble("USED_AMT")
												- reduceAmt,// 当前使用金额+=减免金额
										connection);
						if (result.getErrCode() < 0) {
							err("ERR:" + result.getErrCode()
									+ result.getErrText() + result.getErrName());
							return result;
						}
						// 礼品卡金额不足
						reduceAmt = 0;
						break;
					}
				}
			}
		}
		result.setData("REDUCE_AMT",reduceAmt);
		return result;
	}
	/**
	 * 作废减免数据
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm onRefundOPDBilReduce(TParm parm,TParm opbParm,TConnection connection){
		String reduce_no = SystemTool.getInstance().getNo("ALL", "OPB",
				"REDUCE_NO", "REDUCE_NO");
		String optUser=parm.getValue("OPT_USER_T");
		String optTerm=parm.getValue("OPT_TERM_T");
		Timestamp optdate=parm.getTimestamp("OPT_DATE_T");
		String oldreduceNo=opbParm.getValue("REDUCE_NO");
		TParm inParm=new TParm();
		inParm.setData("REDUCE_NO", oldreduceNo);
		inParm.setData("OPT_USER", optUser);
		inParm.setData("OPT_TERM", optTerm);
		inParm.setData("OPT_DATE", optdate);
		inParm.setData("RESET_REDUCE_NO", reduce_no);
		TParm reduceMParm=this.selectReduceM(inParm);
		reduceMParm.setData("REDUCE_NO", reduce_no);
		reduceMParm.setData("AR_AMT", -reduceMParm.getDouble("AR_AMT"));
		reduceMParm.setData("REDUCE_AMT", -reduceMParm.getDouble("REDUCE_AMT"));
		reduceMParm.setData("BUSINESS_NO",opbParm.getValue("NEWRECEIPT_NO"));
		reduceMParm.setData("REDUCE_NOTE", new TNull(String.class));
		reduceMParm.setData("REDUCE_FLG", new TNull(String.class));
		reduceMParm.setData("RESET_REDUCE_NO", new TNull(String.class));
		reduceMParm.setData("ACCOUNT_DATE", new TNull(Timestamp.class));
		reduceMParm.setData("RESET_USER", new TNull(String.class));
		reduceMParm.setData("RESET_DATE", new TNull(Timestamp.class));
		reduceMParm.setData("ACCOUNT_USER", new TNull(String.class));
		reduceMParm.setData("ACCOUNT_SEQ", new TNull(String.class));
		reduceMParm.setData("ACCOUNT_FLG", new TNull(String.class));
		//主表
		TParm result = update("insertBilReduceM",reduceMParm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result=this.updateForReduceMRefund(inParm, connection);
		if(result.getErrCode()<0){
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result=this.updateForReduceDRefund(inParm, connection);
		if(result.getErrCode()<0){
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 回冲医疗卡金额和医嘱收费状态
	 * yanjing 20141208
	 */
	private TParm onReSetEktForReduce(TParm inParm,TConnection connection){
		TParm result = new TParm();
		//更新ekt_trade表，将state=1改为state=3
		
		result = this.updateStateForEktTrade(inParm, connection);
		if(result.getErrCode()<0){
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.rollback();
			connection.close();
			return result;
		}
//		//更新ekt_master表
//		result = this.updateForEktMaster(inParm, connection);
//		if(result.getErrCode()<0){
//			err("ERR:" + result.getErrCode() + result.getErrText()
//					+ result.getErrName());
//			connection.rollback();
//			connection.close();
//			return result;
//		}
//		//更新opd_order表
//		result = this.updateOpdOrderForReduce(inParm, connection);
//		if(result.getErrCode()<0){
//			err("ERR:" + result.getErrCode() + result.getErrText()
//					+ result.getErrName());
//			connection.rollback();
//			connection.close();
//			return result;
//		}
		//更新ekt_bil_pay表
		result = this.updateEktBilPayForReduce(inParm, connection);
		if(result.getErrCode()<0){
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.rollback();
			connection.close();
			return result;
		}
		//更新reg_patadm表
		result = this.updateRegPatadmForReduce(inParm, connection);
		if(result.getErrCode()<0){
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * 组装EKT  参数
	 * @param parm
	 * @return
	 */
	public TParm onResetEktParm(TParm parm,TParm ektParm){
		TParm result=new TParm();
		TParm parmForEktTrade = new TParm();
		String TradeNo = EKTTool.getInstance().getTradeNo();// 得到医疗卡外部交易号
		parmForEktTrade.setData("TRADE_NO", TradeNo);
		double totAmt=ektParm.getDouble("CURRENT_BALANCE",0);
		parmForEktTrade.setData("CARD_NO",ektParm.getValue("CARD_NO",0));
		parmForEktTrade.setData("MR_NO", parm.getValue("MR_NO"));
		parmForEktTrade.setData("CASE_NO", parm.getValue("CASE_NO"));
		parmForEktTrade.setData("PAT_NAME", Pat.onQueryByMrNo(parm.getValue("MR_NO")).getName());
		parmForEktTrade.setData("OLD_AMT",  totAmt);
		parmForEktTrade.setData("AMT", parm.getDouble("REDUCE_AMT"));
		parmForEktTrade.setData("STATE", "1");
		parmForEktTrade.setData("BUSINESS_TYPE", "OPBR");
		parmForEktTrade.setData("OPT_USER", parm.getValue("OPT_USER_T"));
		parmForEktTrade.setData("OPT_TERM", parm.getValue("OPT_TERM_T"));
		parmForEktTrade.setData("GREEN_BALANCE", "");
		parmForEktTrade.setData("GREEN_BUSINESS_AMT", "");
		parmForEktTrade.setData("RESET_TRADE_NO", "");
		parmForEktTrade.setData("PAY_OTHER3", "");
		parmForEktTrade.setData("PAY_OTHER4", "");
		result.setData("parmForEktTrade", parmForEktTrade.getData());
		TParm parmForEktMaster = new TParm();
		parmForEktMaster.setData("CARD_NO", ektParm.getValue("CARD_NO",0));
		parmForEktMaster.setData("CURRENT_BALANCE", totAmt-parm.getDouble("REDUCE_AMT"));// 现在医疗卡中剩余的金额
		parmForEktMaster.setData("MR_NO", parm.getValue("MR_NO"));// 病案号
		parmForEktMaster.setData("ID_NO", "");
		parmForEktMaster.setData("NAME", Pat.onQueryByMrNo(parm.getValue("MR_NO")).getName());
		parmForEktMaster.setData("CREAT_USER", parm.getValue("OPT_USER_T"));
		parmForEktMaster.setData("OPT_USER", parm.getValue("OPT_USER_T"));
		parmForEktMaster.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parmForEktMaster.setData("OPT_TERM", parm.getValue("OPT_TERM_T"));
		result.setData("parmForEktMaster", parmForEktMaster.getData());
		return result;
	}
	/**
	 * 查询减免M表
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm selectReduceM(TParm parm){
		TParm result = new TParm();
		result = this.query("selectReduceM",parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result.getRow(0);
	}
	/**
	 * 作废减免D表
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm updateForReduceDRefund(TParm parm, TConnection connection){
		TParm result = new TParm();
		result = update("updateForReduceDRefund", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 作废减免M表
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm updateForReduceMRefund(TParm parm, TConnection connection){
		TParm result = new TParm();
		result = update("updateForReduceMRefund", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * EKT_TRADE表添加减免的金额STATE =1
	 * */
	public TParm updateForEktTrade(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateForEktTrade", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * EKT_TRADE表添加减免的金额STATE =3
	 * */
	public TParm updateStateForEktTrade(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateStateForEktTrade", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}


	/**
	 * 更新EKT_MASTER表金额
	 * */
	public TParm updateForEktMaster(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = EKTTool.getInstance().deleteEKTMaster(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.createCard " + result.getErrCode()
					+ result.getErrText());
			return result;
		}
		result = EKTTool.getInstance().insertEKTMaster(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.createCard " + result.getErrCode()
					+ result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询当前的卡号
	 * */
	public TParm selectCardForEkt(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("selectCardForEkt", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
//	/**
////	 * 更新医嘱表
////	 * yanjing 20141208
////	 */
//	private TParm updateOpdOrderForReduce(TParm inParm, TConnection connection){
//		TParm result = new TParm();
//		String receptNo = inParm.getValue("RECEIPT_NO");
//		String caseNo = inParm.getValue("CASE_NO");
//		String opdSql = "UPDATE OPD_ORDER SET BILL_FLG = 'N',BILL_DATE = '',BILL_USER = '' "
//				+" WHERE CASE_NO = '"+caseNo+"' AND BUSINESS_NO = '"+this.onSelectBusinessNo(receptNo)+"' ";
//		System.out.println("输出更新opd_order的语句====opdSql opdSql is：："+opdSql);
//		result = new TParm(TJDODBTool.getInstance().update(opdSql, connection));
//		if(result.getErrCode()<0){
//			err("ERR:" + result.getErrCode() + result.getErrText()
//					+ result.getErrName());
//			connection.rollback();
//			connection.close();
//			return result;
//		}
//		connection.commit();
//		connection.close();
//		return result;
//		
//	}
	/**
	 * 更新医疗卡账务表
	 * yanjing 20141208
	 */
	private TParm updateEktBilPayForReduce(TParm inParm, TConnection connection){
		TParm result = new TParm();
		double restPayOther3 = inParm.getDouble("PAY_OTHER3");
		double restPayOther4 = inParm.getDouble("PAY_OTHER4");
		
		if(restPayOther3 > 0){
			result = EKTNewIO.getInstance().resetPayOther(inParm.getValue("MR_NO"),
					EKTpreDebtTool.PAY_TOHER3, restPayOther3, connection);
		}
		if(restPayOther4 > 0){
			result = EKTNewIO.getInstance().resetPayOther(inParm.getValue("MR_NO"),
					EKTpreDebtTool.PAY_TOHER4, restPayOther4, connection);
		}
		if(result.getErrCode()<0){
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
		
	}
	/**
	 * 更新挂号表
	 * yanjing 20141208
	 */
	private TParm updateRegPatadmForReduce(TParm inParm, TConnection connection){
		TParm result = new TParm();
		double greenBalance = inParm.getDouble("GREEN_BALANCE");
		String caseNo = inParm.getValue("CASE_NO");
		String regPatadmSql = "UPDATE REG_PATADM SET GREEN_BALANCE = '"+greenBalance+"'  " +
				"WHERE CASE_NO = '"+caseNo+"'" ;
		result = new TParm(TJDODBTool.getInstance().update(regPatadmSql, connection));
		if(result.getErrCode()<0){
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
		
	}
}
