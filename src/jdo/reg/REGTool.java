package jdo.reg;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.bil.BILREGRecpTool;
import jdo.bil.BILInvrcptTool;
import com.dongyang.data.TNull;

import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.sys.SYSEmrIndexTool;
import jdo.bil.BILPrintTool;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TCM_Transform;

import jdo.bil.BILAccountTool;
import com.dongyang.util.TypeTool;

import jdo.sys.PatTool;
import jdo.bil.BILInvoiceTool;
import jdo.ins.INSOpdOrderTJTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJFlow;
import jdo.bil.BILContractRecordTool;

/**
 * 
 * <p>
 * Title: 挂号共用工具类
 * </p>
 * 
 * <p>
 * Description: 挂号共用工具类
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
 * @author wangl 2009.07.09
 * @version 1.0
 */
public class REGTool extends TJDOTool {
	/**
	 * 实例
	 */
	private static REGTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return REGTool
	 */
	public static REGTool getInstance() {
		if (instanceObject == null)
			instanceObject = new REGTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public REGTool() {
		onInit();
	}

	/**
	 * 挂号保存
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onSaveREGPatAdm(TParm parm, TConnection connection) {
		TParm result = new TParm();
		TParm regParm = parm.getParm("REG");
		regParm.setData("REASSURE_FLG", parm.getData("REASSURE_FLG"));
//		System.out.println("挂号信息"+regParm);
		// 插入挂号主档
		// 特批款预约挂号使用
		if (null != parm.getValue("EXE_FLG")
				&& parm.getValue("EXE_FLG").equals("Y")) {
			result = PatAdmTool.getInstance().insertInfoGreen(regParm.getRow(),
					connection);
			// 修改VIP预约信息
			regParm.setData("ADM_DATE", regParm.getData("REG_DATE"));
		} else {
			result = PatAdmTool.getInstance().insertInfo(regParm.getRow(),
					connection);
		}
		if (result.getErrCode() < 0) {
			return result;
		}
		// 处理号表
		// ====================pangben 2012-6-18 注释 重号问题解决方法,
		// 将此就诊号码放到保存时就修改相关表,两个事物里处理
		// if (regParm.getBoolean("VIP_FLG")) {
		// result = SchDayTool.getInstance().updatequeno(regParm.getRow(),
		// connection);
		// if (result.getErrCode() < 0) {
		// return result;
		// }
		// TParm regParm1 = regParm.getRow();
		// TCM_Transform.getTimestamp(regParm1.getData("ADM_DATE"));
		// regParm1.setData("ADM_DATE", StringTool.getString(TCM_Transform
		// .getTimestamp(regParm1.getData("ADM_DATE")), "yyyyMMdd"));
		// // VIP诊
		// result = REGClinicQueTool.getInstance().updatequeno(regParm1,
		// connection);
		// if (result.getErrCode() < 0) {
		// return result;
		// }
		//
		// } else {
		// // 普通诊
		// result = SchDayTool.getInstance().updatequeno(regParm, connection);
		// }

		// 判断是否为预约挂号
		// System.out.println("APPT_CODE："+regParm.getValue("APPT_CODE"));
		if ("Y".equals(regParm.getValue("APPT_CODE")))
			return result;
//		// 写医疗卡交易挡
//		if (!EKTTool.getInstance().consumeConfirmation(
//				parm.getValue("TREDE_NO"), connection)) {
//			result.setErr(-1, "医疗卡存档失败");
//			return result;
//		}
		// 更新最近就诊信息
		result = PatTool.getInstance().upLatestDeptDate(regParm, connection);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 插入就诊主索引
		String case_no = regParm.getValue("CASE_NO");
		String adm_type = regParm.getValue("ADM_TYPE");
		String region_code = regParm.getValue("REGION_CODE");
		String mr_no = regParm.getValue("MR_NO");
		//System.out.println("------ADM_DATE---------"+regParm.getValue("ADM_DATE"));
			Timestamp adm_date = Timestamp.valueOf(regParm.getValue("ADM_DATE"));  
		//System.out.println("---Timestamp adm_date1111----"+adm_date);  
		/*Timestamp adm_date = StringTool.getTimestamp(regParm
				.getValue("ADM_DATE"), "yyyyMMdd");*/
		String dept_code = regParm.getValue("DEPT_CODE");
		String dr_code = regParm.getValue("DR_CODE");
		String opt_user = regParm.getValue("OPT_USER");
		String opt_term = regParm.getValue("OPT_TERM");
		boolean insertSysEmrIndex = SYSEmrIndexTool.getInstance()
				.onInsertOpdEmg(case_no, adm_type, region_code, mr_no,
						adm_date, dept_code, dr_code, opt_user, opt_term,
						connection);
		if (!insertSysEmrIndex) {
			return result;
		}
		// 写票据
		TParm receipt = new TParm();
		receipt.setData("REG_RECEIPT", parm.getData("REG_RECEIPT"));
		receipt.setData("BIL_INVOICE", parm.getData("BIL_INVOICE"));
		receipt.setData("BIL_INVRCP", parm.getData("BIL_INVRCP"));
		receipt.setData("ADM_TYPE", parm.getData("ADM_TYPE"));
		// ===========pangben modify 20110819 判断是否记账,数据为空表示记账
		result = BILPrintTool.getInstance().saveReg(receipt, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + ":" + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 报道保存
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onSaveRegister(TParm parm, TConnection connection) {
		TParm result = new TParm();
		TParm regParm = parm.getParm("REG");
		// System.out.println("报道保存regParm:::::"+regParm);
		// System.out.println("挂号信息" + regParm);
		// if (!EKTTool.getInstance().consumeConfirmation(
		// parm.getValue("TREDE_NO"), connection)) {
		// result.setErr(-1, "医疗卡存档失败");
		// return result;
		// }
		TParm arriveParm = new TParm();
		arriveParm.setData("CASE_NO", regParm.getValue("CASE_NO"));
		arriveParm.setData("CTZ1_CODE", regParm.getValue("CTZ1_CODE"));
		arriveParm.setData("CTZ2_CODE", regParm.getValue("CTZ2_CODE"));
		arriveParm.setData("CTZ3_CODE", regParm.getValue("CTZ3_CODE"));
		result = PatAdmTool.getInstance().updateForArrive(arriveParm,
				connection);
		if (result.getErrCode() < 0) {
			return result;
		}
		// System.out.println("挂号信息");
		// 更新最近就诊信息
		result = PatTool.getInstance().upLatestDeptDate(regParm, connection);
		if (result.getErrCode() < 0) {
			return result;
		}
		// System.out.println("更新最近就诊信息");
		String case_no = regParm.getValue("CASE_NO");
		String adm_type = regParm.getValue("ADM_TYPE");
		String region_code = regParm.getValue("REGION_CODE");
		String mr_no = regParm.getValue("MR_NO");
		
		Timestamp adm_date = Timestamp.valueOf(regParm.getValue("ADM_DATE"));
		
		/*Timestamp adm_date = StringTool.getTimestamp(regParm
				.getValue("ADM_DATE"), "yyyyMMdd");*/
		String dept_code = regParm.getValue("DEPT_CODE");
		String dr_code = regParm.getValue("DR_CODE");
		String opt_user = regParm.getValue("OPT_USER");
		String opt_term = regParm.getValue("OPT_TERM");
		// 插入就诊主索引
		boolean insertSysEmrIndex = SYSEmrIndexTool.getInstance()
				.onInsertOpdEmg(case_no, adm_type, region_code, mr_no,
						adm_date, dept_code, dr_code, opt_user, opt_term,
						connection);
		if (!insertSysEmrIndex) {
			return result;
		}

		// 写票据
		TParm receipt = new TParm();
		receipt.setData("REG_RECEIPT", parm.getData("REG_RECEIPT"));
		receipt.setData("BIL_INVOICE", parm.getData("BIL_INVOICE"));
		receipt.setData("BIL_INVRCP", parm.getData("BIL_INVRCP"));
		//===zhangp 20130109 start
		if(parm.getParm("REG_RECEIPT").getValue("ADM_TYPE").length()>0){
			receipt.setData("ADM_TYPE", parm.getParm("REG_RECEIPT").getValue("ADM_TYPE"));
		}
		//===zhangp 20130109 end
		result = BILPrintTool.getInstance().saveReg(receipt, connection);
		if (result.getErrCode() < 0) {
			return result;
		}
		TParm insParm = parm.getParm("insParm");// 医保报到操作
		// ==============pangben 2012-3-22
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
		if (null != opbReadCardParm
				&& null != opbReadCardParm.getValue("CONFIRM_NO")
				&& opbReadCardParm.getValue("CONFIRM_NO").length() > 0) {
			String sql = "UPDATE REG_PATADM SET CONFIRM_NO ='"
					+ opbReadCardParm.getValue("CONFIRM_NO")
					+ "', INS_PAT_TYPE='" + insParm.getValue("INS_TYPE")
					+ "' WHERE CASE_NO='" + case_no + "'";
			TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql,
					connection));
			if (updateParm.getErrCode() < 0) {
				return updateParm;
			}
			TParm temp = new TParm();
			temp.setData("EXE_TERM", opbReadCardParm.getValue("OPT_TERM"));
			temp.setData("EXE_TYPE", insParm.getValue("RECP_TYPE"));
			temp.setData("EXE_USER", opbReadCardParm.getValue("OPT_USER"));
			temp.setData("CASE_NO", case_no);
			updateParm = INSRunTool.getInstance()
					.deleteInsRun(temp, connection);
			if (updateParm.getErrCode() < 0) {
				err(updateParm.getErrCode() + " " + updateParm.getErrText());
				connection.close();
			}
			updateParm = INSTJFlow.getInstance().updateInsAmtFlgPrint(insParm,
					insParm.getValue("RECP_TYPE"), connection);
			if (updateParm.getErrCode() < 0) {
				err(updateParm.getErrCode() + " " + result.getErrText());
				connection.close();
			}
		}
		return result;
	}

	/**
	 * 退挂保存
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUnREGPatAdm(TParm parm, TConnection connection) {
		TParm result = new TParm();
		if (parm.getDouble("AR_AMT", 0) == 0) {
			// 退挂,挂号主档写入退挂人员,日期
			result = PatAdmTool.getInstance().updateForUnReg(parm, connection);
			if (result.getErrCode() < 0) {
				return result;
			}
			boolean quereuseFlg = REGSysParmTool.getInstance().selectdata().getBoolean("QUEREUSE_FLG", 0);
			if(quereuseFlg){
				result = unRegQue(parm.getValue("CASE_NO"), connection);
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					return result;
				}
			}
		}
		TParm oldDataRecpParm = new TParm();
		oldDataRecpParm = parm.getParm("RECP_PARM");
		// ======================================pangben 20110823 记账操作退费修改退费状态
		if (parm.getBoolean("FLG")) {
			TParm parmOne = new TParm();
			parmOne.setData("BIL_STATUS", "3"); // 退费
			parmOne.setData("RECEIPT_FLG", "2"); // 退挂记录
			parmOne.setData("RECEIPT_TYPE", "REG");
			parmOne.setData("OPT_USER", parm.getValue("OPT_USER"));
			parmOne.setData("OPT_TERM", parm.getValue("OPT_TERM"));
			parmOne.setData("RECEIPT_NO", oldDataRecpParm.getData("RECEIPT_NO",
					0));
			result = BILContractRecordTool.getInstance().updateRecode(parmOne,
					connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				connection.close();
				return result;
			}
		}
		// ======================================pangben 20110823 STOP
		TParm caseCountParm = new TParm();
		caseCountParm.setData("CASE_NO", parm.getData("CASE_NO"));

		TParm newDataRecpParm = new TParm();
		newDataRecpParm.setData("CASE_NO", oldDataRecpParm
				.getData("CASE_NO", 0));
		String receiptNo = SystemTool.getInstance().getNo("ALL", "REG",
				"RECEIPT_NO", "RECEIPT_NO");
		newDataRecpParm.setData("RESET_RECEIPT_NO", receiptNo);
		newDataRecpParm.setData("RECEIPT_NO", oldDataRecpParm.getData(
				"RECEIPT_NO", 0));
		// 退挂,更新收据档档收据状态(FOR REG)
		result = BILREGRecpTool.getInstance().updateRecpForUnReg(
				newDataRecpParm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		TParm newUnRecpParm = new TParm();
		newUnRecpParm.setData("CASE_NO",
				oldDataRecpParm.getData("CASE_NO", 0) == null ? new TNull(
						String.class) : oldDataRecpParm.getData("CASE_NO", 0));
		newUnRecpParm.setData("RECEIPT_NO", receiptNo);
		newUnRecpParm.setData("ADM_TYPE", oldDataRecpParm
				.getData("ADM_TYPE", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("ADM_TYPE", 0));
		newUnRecpParm.setData("REGION_CODE", oldDataRecpParm.getData(
				"REGION_CODE", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("REGION_CODE", 0));
		newUnRecpParm.setData("MR_NO",
				oldDataRecpParm.getData("MR_NO", 0) == null ? new TNull(
						String.class) : oldDataRecpParm.getData("MR_NO", 0));
		newUnRecpParm.setData("RESET_RECEIPT_NO", oldDataRecpParm.getData(
				"RESET_RECEIPT_NO", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("RESET_RECEIPT_NO", 0));
		newUnRecpParm.setData("PRINT_NO", oldDataRecpParm
				.getData("PRINT_NO", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("PRINT_NO", 0));
		newUnRecpParm.setData("BILL_DATE", oldDataRecpParm.getData("BILL_DATE",
				0) == null ? new TNull(Timestamp.class) : oldDataRecpParm
				.getData("BILL_DATE", 0));
		newUnRecpParm.setData("CHARGE_DATE", oldDataRecpParm.getData(
				"CHARGE_DATE", 0) == null ? new TNull(Timestamp.class)
				: oldDataRecpParm.getData("CHARGE_DATE", 0));
		newUnRecpParm.setData("PRINT_DATE", oldDataRecpParm.getData(
				"PRINT_DATE", 0) == null ? new TNull(Timestamp.class)
				: oldDataRecpParm.getData("PRINT_DATE", 0));
		newUnRecpParm.setData("REG_FEE", -oldDataRecpParm.getDouble("REG_FEE",
				0));
		newUnRecpParm.setData("REG_FEE_REAL", -oldDataRecpParm.getDouble(
				"REG_FEE_REAL", 0));
		newUnRecpParm.setData("CLINIC_FEE", -oldDataRecpParm.getDouble(
				"CLINIC_FEE", 0));
		newUnRecpParm.setData("CLINIC_FEE_REAL", -oldDataRecpParm.getDouble(
				"CLINIC_FEE_REAL", 0));
		newUnRecpParm.setData("SPC_FEE", -oldDataRecpParm.getDouble("SPC_FEE",
				0));
		newUnRecpParm.setData("OTHER_FEE1", -oldDataRecpParm.getDouble(
				"OTHER_FEE1", 0));
		newUnRecpParm.setData("OTHER_FEE2", -oldDataRecpParm.getDouble(
				"OTHER_FEE2", 0));
		newUnRecpParm.setData("OTHER_FEE3", -oldDataRecpParm.getDouble(
				"OTHER_FEE3", 0));
		newUnRecpParm
				.setData("AR_AMT", -oldDataRecpParm.getDouble("AR_AMT", 0));
		newUnRecpParm.setData("PAY_CASH", -oldDataRecpParm.getDouble(
				"PAY_CASH", 0));
		newUnRecpParm.setData("PAY_BANK_CARD", -oldDataRecpParm.getDouble(
				"PAY_BANK_CARD", 0));
		newUnRecpParm.setData("PAY_CHECK", -oldDataRecpParm.getDouble(
				"PAY_CHECK", 0));
		newUnRecpParm.setData("PAY_MEDICAL_CARD", -oldDataRecpParm.getDouble(
				"PAY_MEDICAL_CARD", 0));
		newUnRecpParm.setData("PAY_INS_CARD", -oldDataRecpParm.getDouble(
				"PAY_INS_CARD", 0));
		newUnRecpParm.setData("PAY_DEBIT", -oldDataRecpParm.getDouble(
				"PAY_DEBIT", 0));
		newUnRecpParm.setData("PAY_INS", -oldDataRecpParm.getDouble("PAY_INS",
				0));
		newUnRecpParm.setData("REMARK",
				oldDataRecpParm.getData("REMARK", 0) == null ? new TNull(
						String.class) : oldDataRecpParm.getData("REMARK", 0));
		newUnRecpParm.setData("CASH_CODE",
				oldDataRecpParm.getData("CASH_CODE", 0) == null ? new TNull(String.class)
						: oldDataRecpParm.getData("CASH_CODE", 0)); 
		// ===ZHANGP 20120319 START
		newUnRecpParm.setData("ACCOUNT_FLG", "");
		newUnRecpParm.setData("ACCOUNT_SEQ", "");
		newUnRecpParm.setData("ACCOUNT_USER", "");
		newUnRecpParm.setData("ACCOUNT_DATE", "");
		// ===zhangp 20120319 end
		newUnRecpParm.setData("BANK_SEQ", oldDataRecpParm
				.getData("BANK_SEQ", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("BANK_SEQ", 0));
		newUnRecpParm.setData("BANK_DATE", oldDataRecpParm.getData("BANK_DATE",
				0) == null ? new TNull(Timestamp.class) : oldDataRecpParm
				.getData("BANK_DATE", 0));
		newUnRecpParm.setData("BANK_USER", oldDataRecpParm.getData("BANK_USER",
				0) == null ? new TNull(String.class) : oldDataRecpParm.getData(
				"BANK_USER", 0));
		newUnRecpParm.setData("OPT_USER",
				parm.getData("OPT_USER") == null ? new TNull(String.class)
						: parm.getData("OPT_USER"));
		newUnRecpParm.setData("OPT_TERM",
				parm.getData("OPT_TERM") == null ? new TNull(String.class)
						: parm.getData("OPT_TERM"));
		// 退挂,收据档写入一笔负数资料
		result = BILREGRecpTool.getInstance().insertDataForUnReg(newUnRecpParm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		TParm invRcpParm = new TParm();
		invRcpParm.setData("CANCEL_FLG", "1");
		invRcpParm.setData("CANCEL_USER", parm.getData("OPT_USER"));
		invRcpParm.setData("OPT_USER", parm.getData("OPT_USER"));
		invRcpParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
		invRcpParm.setData("RECP_TYPE", "REG");
		invRcpParm.setData("INV_NO", parm.getData("INV_NO"));
		// 退挂,更新票据明细档票据状态
//		result = BILInvrcptTool.getInstance()
//				.updataData(invRcpParm, connection);
//		if (result.getErrCode() < 0) {
//			err(result.getErrName() + " " + result.getErrText());
//			return result;
//		}
		return result;
	}

	/**
	 * 挂号日结保存
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm executeREGAccount(TParm parm, TConnection connection) {
		// System.out.println("parm"+parm);
		TParm result = new TParm();
		double amt = 0.00;
		TParm selDateAccountInParm = new TParm();
		selDateAccountInParm.setData("BILL_DATE", parm.getData("BILL_DATE"));
		selDateAccountInParm.setData("CASH_CODE", parm.getData("CASH_CODE"));
		selDateAccountInParm.setData("ADM_TYPE", parm.getData("ADM_TYPE"));
		// =========pangben modify 20110407 start 添加区域查询条件
		if (parm.getData("REGION_CODE") != null
				&& !parm.getData("REGION_CODE").equals(""))
			selDateAccountInParm.setData("REGION_CODE", parm
					.getData("REGION_CODE"));
		// =========pangben modify 20110407 stop

		// 查询准备日结的数据
		result = BILREGRecpTool.getInstance().selDateForAccount(
				selDateAccountInParm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		if (result.getCount() < 1) {
			result.setErr(-1, "查无信息");
			return result;
		}
		// ==zhangp 20120319 start
		// if (result.getDouble("AR_AMT", 0) >= 0) {
		if (result.getDouble("AR_AMT", 0) != 0) {
			// ===zhangp 20120319 end
			double reg_fee = result.getDouble("REG_FEE", 0); // 挂号费（实际）
			double clinic_fee = result.getDouble("CLINIC_FEE", 0); // 诊察费（实际）
			double All_Fee = reg_fee + clinic_fee; // 合计（费用）
			amt = TypeTool.getDouble(All_Fee);
		}
		TParm upREGRecp = new TParm();
		upREGRecp.setData("ACCOUNT_SEQ", parm.getData("ACCOUNT_SEQ"));
		upREGRecp.setData("ACCOUNT_USER", parm.getData("ACCOUNT_USER"));
		upREGRecp.setData("CASH_CODE", parm.getData("CASH_CODE"));
		upREGRecp.setData("BILL_DATE", parm.getData("BILL_DATE"));
		upREGRecp.setData("ACCOUNT_DATE", parm.getData("ACCOUNT_DATE"));
		upREGRecp.setData("ADM_TYPE", parm.getData("ADM_TYPE"));
		// 更新收据档
		result = BILREGRecpTool.getInstance().updateAccount(upREGRecp,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		TParm upBILRegInvRcp = new TParm();
		upBILRegInvRcp.setData("ACCOUNT_SEQ", parm.getData("ACCOUNT_SEQ"));
		upBILRegInvRcp.setData("ACCOUNT_USER", parm.getData("ACCOUNT_USER"));
		upBILRegInvRcp.setData("RECP_TYPE", "REG");
		upBILRegInvRcp.setData("ADM_TYPE", parm.getData("ADM_TYPE"));
		upBILRegInvRcp.setData("CASHIER_CODE", parm.getData("CASH_CODE"));
		// ===zhangp 20120320 start
		// upBILRegInvRcp.setData("PRINT_DATE", parm.getData("BILL_DATE"));
		String accntDate = parm.getData("ACCOUNT_DATE").toString();
		accntDate = accntDate.substring(0, 4) + accntDate.substring(5, 7)
				+ accntDate.substring(8, 10) + accntDate.substring(11, 13)
				+ accntDate.substring(14, 16) + accntDate.substring(17, 19);
		upBILRegInvRcp.setData("PRINT_DATE", accntDate);
		// ===zhangp 20120320 end
		// 更新票据状态,票据明细档写入日结号,人员,时间
		result = BILInvrcptTool.getInstance().account(upBILRegInvRcp,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		// ===zhangp 20120320 start
		result = updateRestInvrcp(parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		// ===zhangp 20120320 end
		TParm selCancelRecpNoParm = new TParm();
		selCancelRecpNoParm.setData("RECP_TYPE", "REG");
		selCancelRecpNoParm.setData("CASH_CODE", parm.getData("CASH_CODE"));
		selCancelRecpNoParm.setData("ADM_TYPE", parm.getData("ADM_TYPE"));
		selCancelRecpNoParm.setData("PRINT_DATE", parm.getData("BILL_DATE"));
		// 查询作废张数
		// ===zhangp 20120319 start
		// TParm selCancelRecpNo = BILInvrcptTool.getInstance().getInvalidCount(
		// selCancelRecpNoParm);
		TParm selCancelRecpNo = BILREGRecpTool.getInstance().getRegResetCount(
				selCancelRecpNoParm);
		// ===zhangp 20120319 end
		int canselCount = selCancelRecpNo.getInt("COUNT", 0);
		TParm accountParm = new TParm();
		accountParm.setData("ACCOUNT_TYPE", "REG");
		accountParm.setData("ACCOUNT_SEQ", parm.getData("ACCOUNT_SEQ"));
		accountParm.setData("ACCOUNT_USER", parm.getData("ACCOUNT_USER"));
		accountParm.setData("ACCOUNT_DATE", parm.getData("ACCOUNT_DATE"));
		accountParm.setData("AR_AMT", amt);
		accountParm.setData("STATUS", "0");
		accountParm.setData("INVALID_COUNT", canselCount);
		accountParm.setData("ADM_TYPE", parm.getData("ADM_TYPE"));
		accountParm.setData("REGION_CODE", parm.getData("REGION_CODE"));
		accountParm.setData("OPT_USER", parm.getData("OPT_USER"));
		accountParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
		result = BILAccountTool.getInstance().insertAccount(accountParm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 挂号保存（医疗卡付费使用）
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onSaveREGPatAdmForEKT(TParm parm, TConnection connection) {
		TParm result = new TParm();
		TParm regParm = parm.getParm("REG");
		// System.out.println("挂号信息"+regParm);
		// 插入挂号主档
		result = PatAdmTool.getInstance().insertInfo(regParm.getRow(),
				connection);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 更新最近就诊信息
		result = PatTool.getInstance().upLatestDeptDate(regParm, connection);
		if (result.getErrCode() < 0) {
			return result;
		}

		// 处理号表
		if (regParm.getBoolean("VIP_FLG")) {
			result = SchDayTool.getInstance().updatequeno(regParm.getRow(),
					connection);
			if (result.getErrCode() < 0) {
				return result;
			}
			TParm regParm1 = regParm.getRow();
			TCM_Transform.getTimestamp(regParm1.getData("ADM_DATE"));
			regParm1.setData("ADM_DATE", StringTool.getString(TCM_Transform
					.getTimestamp(regParm1.getData("ADM_DATE")), "yyyyMMdd"));
			// VIP诊
			result = REGClinicQueTool.getInstance().updatequeno(regParm1,
					connection);
			if (result.getErrCode() < 0) {
				return result;
			}

		} else {
			// 普通诊
			result = SchDayTool.getInstance().updatequeno(regParm, connection);
		}
		String case_no = regParm.getValue("CASE_NO");
		String adm_type = regParm.getValue("ADM_TYPE");
		String region_code = regParm.getValue("REGION_CODE");
		String mr_no = regParm.getValue("MR_NO");
		Timestamp adm_date = Timestamp.valueOf(regParm.getValue("ADM_DATE"));
		/*Timestamp adm_date = StringTool.getTimestamp(regParm
				.getValue("ADM_DATE"), "yyyyMMdd");*/
		String dept_code = regParm.getValue("DEPT_CODE");
		String dr_code = regParm.getValue("DR_CODE");
		String opt_user = regParm.getValue("OPT_USER");
		String opt_term = regParm.getValue("OPT_TERM");
		// 插入就诊主索引
		boolean insertSysEmrIndex = SYSEmrIndexTool.getInstance()
				.onInsertOpdEmg(case_no, adm_type, region_code, mr_no,
						adm_date, dept_code, dr_code, opt_user, opt_term,
						connection);
		if (!insertSysEmrIndex) {
			return result;
		}
		return result;
	}

	/**
	 * 医疗卡记账退费，没有产生票据只添加、修改BIL_REG_RECP表
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm ===================pangben modify 20110820
	 */
	public TParm onUnREGStatusForEKT(TParm parm, TConnection connection) {
		TParm result = new TParm();
		if (parm.getDouble("AR_AMT", 0) == 0) {
			// 退挂,挂号主档写入退挂人员,日期
			result = PatAdmTool.getInstance().updateForUnReg(parm, connection);
			if (result.getErrCode() < 0) {
				return result;
			}
		}
//		EKTTool.getInstance().consumeConfirmation(parm.getValue("TREDE_NO"),
//				connection);
		TParm caseCountParm = new TParm();
		caseCountParm.setData("CASE_NO", parm.getData("CASE_NO"));
		TParm oldDataRecpParm = new TParm();
		oldDataRecpParm = parm.getParm("RECP_PARM");
		TParm newDataRecpParm = new TParm();
		newDataRecpParm.setData("CASE_NO", oldDataRecpParm
				.getData("CASE_NO", 0));
		String receiptNo = SystemTool.getInstance().getNo("ALL", "REG",
				"RECEIPT_NO", "RECEIPT_NO");
		newDataRecpParm.setData("RESET_RECEIPT_NO", receiptNo);
		newDataRecpParm.setData("RECEIPT_NO", oldDataRecpParm.getData(
				"RECEIPT_NO", 0));
		// 退挂,更新收据档档收据状态(FOR REG)
		result = BILREGRecpTool.getInstance().updateRecpForUnReg(
				newDataRecpParm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		TParm newUnRecpParm = new TParm();
		newUnRecpParm.setData("CASE_NO",
				oldDataRecpParm.getData("CASE_NO", 0) == null ? new TNull(
						String.class) : oldDataRecpParm.getData("CASE_NO", 0));
		newUnRecpParm.setData("RECEIPT_NO", receiptNo);
		newUnRecpParm.setData("ADM_TYPE", oldDataRecpParm
				.getData("ADM_TYPE", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("ADM_TYPE", 0));
		newUnRecpParm.setData("REGION_CODE", oldDataRecpParm.getData(
				"REGION_CODE", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("REGION_CODE", 0));
		newUnRecpParm.setData("MR_NO",
				oldDataRecpParm.getData("MR_NO", 0) == null ? new TNull(
						String.class) : oldDataRecpParm.getData("MR_NO", 0));
		newUnRecpParm.setData("RESET_RECEIPT_NO", oldDataRecpParm.getData(
				"RESET_RECEIPT_NO", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("RESET_RECEIPT_NO", 0));
		newUnRecpParm.setData("PRINT_NO", oldDataRecpParm
				.getData("PRINT_NO", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("PRINT_NO", 0));
		newUnRecpParm.setData("BILL_DATE", oldDataRecpParm.getData("BILL_DATE",
				0) == null ? new TNull(Timestamp.class) : oldDataRecpParm
				.getData("BILL_DATE", 0));
		newUnRecpParm.setData("CHARGE_DATE", oldDataRecpParm.getData(
				"CHARGE_DATE", 0) == null ? new TNull(Timestamp.class)
				: oldDataRecpParm.getData("CHARGE_DATE", 0));
		newUnRecpParm.setData("PRINT_DATE", oldDataRecpParm.getData(
				"PRINT_DATE", 0) == null ? new TNull(Timestamp.class)
				: oldDataRecpParm.getData("PRINT_DATE", 0));
		newUnRecpParm.setData("REG_FEE", -oldDataRecpParm.getDouble("REG_FEE",
				0));
		newUnRecpParm.setData("REG_FEE_REAL", -oldDataRecpParm.getDouble(
				"REG_FEE_REAL", 0));
		newUnRecpParm.setData("CLINIC_FEE", -oldDataRecpParm.getDouble(
				"CLINIC_FEE", 0));
		newUnRecpParm.setData("CLINIC_FEE_REAL", -oldDataRecpParm.getDouble(
				"CLINIC_FEE_REAL", 0));
		newUnRecpParm.setData("SPC_FEE", -oldDataRecpParm.getDouble("SPC_FEE",
				0));
		newUnRecpParm.setData("OTHER_FEE1", -oldDataRecpParm.getDouble(
				"OTHER_FEE1", 0));
		newUnRecpParm.setData("OTHER_FEE2", -oldDataRecpParm.getDouble(
				"OTHER_FEE2", 0));
		newUnRecpParm.setData("OTHER_FEE3", -oldDataRecpParm.getDouble(
				"OTHER_FEE3", 0));
		newUnRecpParm
				.setData("AR_AMT", -oldDataRecpParm.getDouble("AR_AMT", 0));
		newUnRecpParm.setData("PAY_CASH", -oldDataRecpParm.getDouble(
				"PAY_CASH", 0));
		newUnRecpParm.setData("PAY_BANK_CARD", -oldDataRecpParm.getDouble(
				"PAY_BANK_CARD", 0));
		newUnRecpParm.setData("PAY_CHECK", -oldDataRecpParm.getDouble(
				"PAY_CHECK", 0));
		newUnRecpParm.setData("PAY_MEDICAL_CARD", -oldDataRecpParm.getDouble(
				"PAY_MEDICAL_CARD", 0));
		newUnRecpParm.setData("PAY_INS_CARD", -oldDataRecpParm.getDouble(
				"PAY_INS_CARD", 0));
		newUnRecpParm.setData("PAY_DEBIT", -oldDataRecpParm.getDouble(
				"PAY_DEBIT", 0));
		newUnRecpParm.setData("PAY_INS", -oldDataRecpParm.getDouble("PAY_INS",
				0));
		newUnRecpParm.setData("REMARK",
				oldDataRecpParm.getData("REMARK", 0) == null ? new TNull(
						String.class) : oldDataRecpParm.getData("REMARK", 0));
		newUnRecpParm.setData("CASH_CODE",
				parm.getData("OPT_USER") == null ? new TNull(String.class)
						: parm.getData("OPT_USER"));
		newUnRecpParm.setData("ACCOUNT_FLG", oldDataRecpParm.getData(
				"ACCOUNT_FLG", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("ACCOUNT_FLG", 0));
		newUnRecpParm.setData("ACCOUNT_SEQ", oldDataRecpParm.getData(
				"ACCOUNT_SEQ", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("ACCOUNT_SEQ", 0));
		newUnRecpParm.setData("ACCOUNT_USER", oldDataRecpParm.getData(
				"ACCOUNT_USER", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("ACCOUNT_USER", 0));
		newUnRecpParm.setData("ACCOUNT_DATE", oldDataRecpParm.getData(
				"ACCOUNT_DATE", 0) == null ? new TNull(Timestamp.class)
				: oldDataRecpParm.getData("ACCOUNT_DATE", 0));
		newUnRecpParm.setData("BANK_SEQ", oldDataRecpParm
				.getData("BANK_SEQ", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("BANK_SEQ", 0));
		newUnRecpParm.setData("BANK_DATE", oldDataRecpParm.getData("BANK_DATE",
				0) == null ? new TNull(Timestamp.class) : oldDataRecpParm
				.getData("BANK_DATE", 0));
		newUnRecpParm.setData("BANK_USER", oldDataRecpParm.getData("BANK_USER",
				0) == null ? new TNull(String.class) : oldDataRecpParm.getData(
				"BANK_USER", 0));
		newUnRecpParm.setData("OPT_USER",
				parm.getData("OPT_USER") == null ? new TNull(String.class)
						: parm.getData("OPT_USER"));
		newUnRecpParm.setData("OPT_TERM",
				parm.getData("OPT_TERM") == null ? new TNull(String.class)
						: parm.getData("OPT_TERM"));
		// 退挂,收据档写入一笔负数资料
		result = BILREGRecpTool.getInstance().insertDataForUnReg(newUnRecpParm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 退挂保存(医疗卡使用)
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUnREGPatAdmForEKT(TParm parm, TConnection connection) {
		TParm result = new TParm();
		if (parm.getDouble("AR_AMT", 0) == 0) {
			// 退挂,挂号主档写入退挂人员,日期
			result = PatAdmTool.getInstance().updateForUnReg(parm, connection);
			if (result.getErrCode() < 0) {
				return result;
			}
			boolean quereuseFlg = REGSysParmTool.getInstance().selectdata().getBoolean("QUEREUSE_FLG", 0);
			if(quereuseFlg){
				result = unRegQue(parm.getValue("CASE_NO"), connection);
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					return result;
				}
			}
		}
		// EKTTool.getInstance().consumeConfirmation(parm.getValue("TREDE_NO"),
		// connection);
		TParm caseCountParm = new TParm();
		caseCountParm.setData("CASE_NO", parm.getData("CASE_NO"));
		TParm oldDataRecpParm = new TParm();
		oldDataRecpParm = parm.getParm("RECP_PARM");
		TParm newDataRecpParm = new TParm();
		newDataRecpParm.setData("CASE_NO", oldDataRecpParm
				.getData("CASE_NO", 0));
		String receiptNo = SystemTool.getInstance().getNo("ALL", "REG",
				"RECEIPT_NO", "RECEIPT_NO");
		newDataRecpParm.setData("RESET_RECEIPT_NO", receiptNo);
		newDataRecpParm.setData("RECEIPT_NO", oldDataRecpParm.getData(
				"RECEIPT_NO", 0));
		// 退挂,更新收据档档收据状态(FOR REG)
		result = BILREGRecpTool.getInstance().updateRecpForUnReg(
				newDataRecpParm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		TParm newUnRecpParm = new TParm();
		newUnRecpParm.setData("CASE_NO",
				oldDataRecpParm.getData("CASE_NO", 0) == null ? new TNull(
						String.class) : oldDataRecpParm.getData("CASE_NO", 0));
		newUnRecpParm.setData("RECEIPT_NO", receiptNo);
		newUnRecpParm.setData("ADM_TYPE", oldDataRecpParm
				.getData("ADM_TYPE", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("ADM_TYPE", 0));
		newUnRecpParm.setData("REGION_CODE", oldDataRecpParm.getData(
				"REGION_CODE", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("REGION_CODE", 0));
		newUnRecpParm.setData("MR_NO",
				oldDataRecpParm.getData("MR_NO", 0) == null ? new TNull(
						String.class) : oldDataRecpParm.getData("MR_NO", 0));
		newUnRecpParm.setData("RESET_RECEIPT_NO", oldDataRecpParm.getData(
				"RESET_RECEIPT_NO", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("RESET_RECEIPT_NO", 0));
		newUnRecpParm.setData("PRINT_NO", oldDataRecpParm
				.getData("PRINT_NO", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("PRINT_NO", 0));
		newUnRecpParm.setData("BILL_DATE", oldDataRecpParm.getData("BILL_DATE",
				0) == null ? new TNull(Timestamp.class) : oldDataRecpParm
				.getData("BILL_DATE", 0));
		newUnRecpParm.setData("CHARGE_DATE", oldDataRecpParm.getData(
				"CHARGE_DATE", 0) == null ? new TNull(Timestamp.class)
				: oldDataRecpParm.getData("CHARGE_DATE", 0));
		newUnRecpParm.setData("PRINT_DATE", oldDataRecpParm.getData(
				"PRINT_DATE", 0) == null ? new TNull(Timestamp.class)
				: oldDataRecpParm.getData("PRINT_DATE", 0));
		newUnRecpParm.setData("REG_FEE", -oldDataRecpParm.getDouble("REG_FEE",
				0));
		newUnRecpParm.setData("REG_FEE_REAL", -oldDataRecpParm.getDouble(
				"REG_FEE_REAL", 0));
		newUnRecpParm.setData("CLINIC_FEE", -oldDataRecpParm.getDouble(
				"CLINIC_FEE", 0));
		newUnRecpParm.setData("CLINIC_FEE_REAL", -oldDataRecpParm.getDouble(
				"CLINIC_FEE_REAL", 0));
		newUnRecpParm.setData("SPC_FEE", -oldDataRecpParm.getDouble("SPC_FEE",
				0));
		newUnRecpParm.setData("OTHER_FEE1", -oldDataRecpParm.getDouble(
				"OTHER_FEE1", 0));
		newUnRecpParm.setData("OTHER_FEE2", -oldDataRecpParm.getDouble(
				"OTHER_FEE2", 0));
		newUnRecpParm.setData("OTHER_FEE3", -oldDataRecpParm.getDouble(
				"OTHER_FEE3", 0));
		newUnRecpParm
				.setData("AR_AMT", -oldDataRecpParm.getDouble("AR_AMT", 0));
		newUnRecpParm.setData("PAY_CASH", -oldDataRecpParm.getDouble(
				"PAY_CASH", 0));
		newUnRecpParm.setData("PAY_BANK_CARD", -oldDataRecpParm.getDouble(
				"PAY_BANK_CARD", 0));
		newUnRecpParm.setData("PAY_CHECK", -oldDataRecpParm.getDouble(
				"PAY_CHECK", 0));
		newUnRecpParm.setData("PAY_MEDICAL_CARD", -oldDataRecpParm.getDouble(
				"PAY_MEDICAL_CARD", 0));
		newUnRecpParm.setData("PAY_INS_CARD", -oldDataRecpParm.getDouble(
				"PAY_INS_CARD", 0));
		newUnRecpParm.setData("PAY_DEBIT", -oldDataRecpParm.getDouble(
				"PAY_DEBIT", 0));
		newUnRecpParm.setData("PAY_INS", -oldDataRecpParm.getDouble("PAY_INS",
				0));
		newUnRecpParm.setData("REMARK",
				oldDataRecpParm.getData("REMARK", 0) == null ? new TNull(
						String.class) : oldDataRecpParm.getData("REMARK", 0));
		newUnRecpParm.setData("CASH_CODE",
				parm.getData("OPT_USER") == null ? new TNull(String.class)
						: parm.getData("OPT_USER"));
		// ===zhangp 20120319 start
		// newUnRecpParm.setData("ACCOUNT_FLG", oldDataRecpParm.getData(
		// "ACCOUNT_FLG", 0) == null ? new TNull(String.class)
		// : oldDataRecpParm.getData("ACCOUNT_FLG", 0));
		newUnRecpParm.setData("ACCOUNT_FLG", "");
		// newUnRecpParm.setData("ACCOUNT_SEQ", oldDataRecpParm.getData(
		// "ACCOUNT_SEQ", 0) == null ? new TNull(String.class)
		// : oldDataRecpParm.getData("ACCOUNT_SEQ", 0));
		newUnRecpParm.setData("ACCOUNT_SEQ", "");
		// newUnRecpParm.setData("ACCOUNT_USER", oldDataRecpParm.getData(
		// "ACCOUNT_USER", 0) == null ? new TNull(String.class)
		// : oldDataRecpParm.getData("ACCOUNT_USER", 0));
		newUnRecpParm.setData("ACCOUNT_USER", "");
		// newUnRecpParm.setData("ACCOUNT_DATE", oldDataRecpParm.getData(
		// "ACCOUNT_DATE", 0) == null ? new TNull(Timestamp.class)
		// : oldDataRecpParm.getData("ACCOUNT_DATE", 0));
		newUnRecpParm.setData("ACCOUNT_DATE", "");
		// ===zhangp 20120319 end
		newUnRecpParm.setData("BANK_SEQ", oldDataRecpParm
				.getData("BANK_SEQ", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("BANK_SEQ", 0));
		newUnRecpParm.setData("BANK_DATE", oldDataRecpParm.getData("BANK_DATE",
				0) == null ? new TNull(Timestamp.class) : oldDataRecpParm
				.getData("BANK_DATE", 0));
		newUnRecpParm.setData("BANK_USER", oldDataRecpParm.getData("BANK_USER",
				0) == null ? new TNull(String.class) : oldDataRecpParm.getData(
				"BANK_USER", 0));
		newUnRecpParm.setData("OPT_USER",
				parm.getData("OPT_USER") == null ? new TNull(String.class)
						: parm.getData("OPT_USER"));
		newUnRecpParm.setData("OPT_TERM",
				parm.getData("OPT_TERM") == null ? new TNull(String.class)
						: parm.getData("OPT_TERM"));
		// 退挂,收据档写入一笔负数资料
		result = BILREGRecpTool.getInstance().insertDataForUnReg(newUnRecpParm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		TParm invRcpParm = new TParm();
		invRcpParm.setData("CANCEL_FLG", "1");
		invRcpParm.setData("CANCEL_USER", parm.getData("OPT_USER"));
		invRcpParm.setData("OPT_USER", parm.getData("OPT_USER"));
		invRcpParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
		invRcpParm.setData("RECP_TYPE", "REG");
		invRcpParm.setData("INV_NO", parm.getData("INV_NO"));
//		// 退挂,更新票据明细档票据状态
//		result = BILInvrcptTool.getInstance()
//				.updataData(invRcpParm, connection);
//		if (result.getErrCode() < 0) {
//			err(result.getErrName() + " " + result.getErrText());
//			return result;
//		}
		// TParm tredeParm = new TParm();
		// tredeParm.setData("CASE_NO", parm.getData("CASE_NO"));
		// tredeParm.setData("BUSINESS_TYPE", "REG");
		// result = EKTTool.getInstance().selectTredeNo(tredeParm);
		// if (result.getErrCode() < 0) {
		// err(result.getErrName() + " " + result.getErrText());
		// return result;
		// }
		// if (result.getCount("TREDE_NO") <= 0) {
		// return result;
		// }
		// result = EKTTool.getInstance().consumeCancel(
		// result.getValue("TREDE_NO", 0), connection);
		// if (result.getErrCode() < 0) {
		// err(result.getErrName() + " " + result.getErrText());
		// return result;
		// }
		return result;
	}

	/**
	 * 挂号条补印
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onREGReprint(TParm parm, TConnection connection) {
		TParm result = new TParm();
		String caseNo = parm.getValue("CASE_NO");
		TParm selInvoice = new TParm();
		selInvoice.setData("STATUS", "0");
		selInvoice.setData("RECP_TYPE", "REG");
		selInvoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
		selInvoice.setData("TERM_IP", parm.getData("OPT_TERM"));
		TParm invoice = BILInvoiceTool.getInstance().selectNowReceipt(
				selInvoice);
		String invNo = invoice.getValue("UPDATE_NO", 0);

		invoice.setData("UPDATE_NO", StringTool.addString(invNo));
		invoice.setData("RECP_TYPE", "REG");
		invoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
		invoice.setData("STATUS", "0");
		invoice.setData("START_INVNO", invoice.getData("START_INVNO", 0));
		// System.out.println("票据主档数据"+invoice);
		// 更新票据主档
		result = BILInvoiceTool.getInstance().updateDatePrint(invoice,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		TParm oldDataRecpParm = new TParm();
		oldDataRecpParm = BILREGRecpTool.getInstance().selForRePrint(caseNo);
		TParm newDataRecpParm = new TParm();
		newDataRecpParm.setData("CASE_NO", caseNo);
		newDataRecpParm.setData("PRINT_NO", invNo);
		newDataRecpParm.setData("RECEIPT_NO", oldDataRecpParm.getData(
				"RECEIPT_NO", 0));
		newDataRecpParm.setData("OPT_USER", parm.getData("OPT_USER"));
		newDataRecpParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
		// System.out.println("更新收据档数据"+newDataRecpParm);
		// 补印,更新收据档档收据状态(FOR REG)
		result = BILREGRecpTool.getInstance().upRecpForRePrint(newDataRecpParm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		// 医保修改票据号码======pangben 2012-3-19
		TParm insParm = new TParm();
		insParm.setData("CASE_NO", caseNo);
		insParm.setData("PRINT_NO", oldDataRecpParm.getData("PRINT_NO", 0));
		insParm.setData("NEW_PRINT_NO", invNo);
		result = updateInsInvNo(insParm, connection);// 修改医保票据号码
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// =========== pangben 2012-3-19 stop
		TParm selInvrcp = new TParm();
		selInvrcp.setData("RECP_TYPE", "REG");
		selInvrcp.setData("INV_NO", oldDataRecpParm.getData("PRINT_NO", 0));
		// 查询原票据明细档
		TParm oneInvParm = BILInvrcptTool.getInstance().getOneInv(selInvrcp);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		TParm invRcpParm = new TParm();
		invRcpParm.setData("CANCEL_FLG", "3");
		invRcpParm.setData("CANCEL_USER", parm.getData("OPT_USER"));
		invRcpParm.setData("OPT_USER", parm.getData("OPT_USER"));
		invRcpParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
		invRcpParm.setData("RECP_TYPE", "REG");
		invRcpParm.setData("INV_NO", oneInvParm.getData("INV_NO", 0));
		// 补印,更新票据明细档票据状态
		result = BILInvrcptTool.getInstance()
				.updataData(invRcpParm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// 票据明细档新增一条新数据
		TParm insertInvrcp = new TParm();
		insertInvrcp.setData("INV_NO", invNo);
		insertInvrcp.setData("RECP_TYPE", "REG");
		insertInvrcp.setData("RECEIPT_NO", oldDataRecpParm.getData(
				"RECEIPT_NO", 0));
		insertInvrcp.setData("CASHIER_CODE", parm.getData("OPT_USER"));
		insertInvrcp.setData("AR_AMT", oneInvParm.getValue("AR_AMT", 0));
		insertInvrcp.setData("CANCEL_FLG", "0");
		insertInvrcp.setData("STATUS", "0");
		insertInvrcp.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));
		insertInvrcp.setData("OPT_TERM", parm.getData("OPT_TERM"));
		insertInvrcp.setData("OPT_USER", parm.getData("OPT_USER"));
		result = BILInvrcptTool.getInstance().insertData(insertInvrcp,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}

	/**
	 * 修改医保操作票据号码
	 * 
	 * @param parm
	 * @param connection
	 * @return pangben 2012-3-19
	 */
	private TParm updateInsInvNo(TParm parm, TConnection connection) {
		TParm tempParm = new TParm();
		// System.out.println("---------------医保退费-------------:" + opbParm);
		tempParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		tempParm.setData("INV_NO", parm.getValue("PRINT_NO"));
		tempParm.setData("RECP_TYPE", "REG");// 收费类型

		// 查询是否医保 退费
		TParm result = INSOpdTJTool.getInstance().selectInsInvNo(tempParm);
		if (result.getErrCode() < 0) {
			return result;
		}
		if (null != result && null != result.getValue("CONFIRM_NO", 0)
				&& result.getValue("CONFIRM_NO", 0).length() > 0) {
			tempParm.setData("CONFIRM_NO", result.getValue("CONFIRM_NO", 0));
			tempParm.setData("INV_NO", parm.getValue("NEW_PRINT_NO"));// 新的票据号码
			// 修改医保主档票据号码
			result = INSOpdTJTool.getInstance().updateInsOpdInvNo(tempParm,
					connection);
			if (result.getErrCode() < 0) {
				return result;
			}
			// 修改医保票据号码
			result = INSOpdOrderTJTool.getInstance().updateInsOpdOrderInvNo(
					tempParm, connection);
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		return result;
	}

	/**
	 * 更新诊号(护士站分诊)
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onSaveTriage(TParm parm, TConnection connection) {
		TParm result = new TParm();
		TParm regParm = new TParm();
		regParm.setData("ADM_TYPE", parm.getData("ADM_TYPE"));
		regParm.setData("ADM_DATE", parm.getData("ADM_DATE"));
		regParm.setData("SESSION_CODE", parm.getData("SESSION_CODE"));
		regParm.setData("CLINICROOM_NO", parm.getData("CLINICROOM_NO"));
		regParm.setData("QUE_NO", parm.getData("QUE_NO"));
		regParm.setData("REGION_CODE", parm.getData("REGION_CODE"));
		// 处理号表
		if (regParm.getBoolean("VIP_FLG")) {
			regParm.setData("ADM_DATE", StringTool.getString(TCM_Transform
					.getTimestamp(regParm.getData("ADM_DATE")), "yyyyMMdd"));
			// VIP诊
			result = REGClinicQueTool.getInstance().updatequeno(regParm,
					connection);
			if (result.getErrCode() < 0) {
				return result;
			}

		} else {
			// 普通诊
			result = SchDayTool.getInstance().updatequeno(regParm, connection);
		}
		return result;
	}

	/**
	 * 退挂记账表没有完成将执行添加、修改BIL_REG_RECP 表 ，不执行票据修改操作
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUnRegStatus(TParm parm, TConnection connection) {
		TParm caseCountParm = new TParm();
		TParm result = new TParm();
		if (parm.getDouble("AR_AMT", 0) == 0) {
			// 退挂,挂号主档写入退挂人员,日期
			result = PatAdmTool.getInstance().updateForUnReg(parm, connection);
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		TParm oldDataRecpParm = new TParm();
		oldDataRecpParm = parm.getParm("RECP_PARM");
		// ======================================pangben 20110823 记账操作退费修改退费状态
		TParm parmOne = new TParm();
		parmOne.setData("BIL_STATUS", "3"); // 退费
		parmOne.setData("RECEIPT_FLG", "2"); // 退挂记录
		parmOne.setData("OPT_USER", parm.getValue("OPT_USER"));
		parmOne.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		parmOne.setData("RECEIPT_NO", oldDataRecpParm.getData("RECEIPT_NO", 0));
		parmOne.setData("RECEIPT_TYPE", "REG");
		result = BILContractRecordTool.getInstance().updateRecode(parmOne,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.close();
			return result;
		}
		// ======================================pangben 20110823 STOP

		caseCountParm.setData("CASE_NO", parm.getData("CASE_NO"));

		TParm newDataRecpParm = new TParm();
		newDataRecpParm.setData("CASE_NO", oldDataRecpParm
				.getData("CASE_NO", 0));
		String receiptNo = SystemTool.getInstance().getNo("ALL", "REG",
				"RECEIPT_NO", "RECEIPT_NO");
		newDataRecpParm.setData("RESET_RECEIPT_NO", receiptNo);
		newDataRecpParm.setData("RECEIPT_NO", oldDataRecpParm.getData(
				"RECEIPT_NO", 0));
		// 退挂,更新收据档档收据状态(FOR REG)
		result = BILREGRecpTool.getInstance().updateRecpForUnReg(
				newDataRecpParm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		TParm newUnRecpParm = new TParm();
		newUnRecpParm.setData("CASE_NO",
				oldDataRecpParm.getData("CASE_NO", 0) == null ? new TNull(
						String.class) : oldDataRecpParm.getData("CASE_NO", 0));
		newUnRecpParm.setData("RECEIPT_NO", receiptNo);
		newUnRecpParm.setData("ADM_TYPE", oldDataRecpParm
				.getData("ADM_TYPE", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("ADM_TYPE", 0));
		newUnRecpParm.setData("REGION_CODE", oldDataRecpParm.getData(
				"REGION_CODE", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("REGION_CODE", 0));
		newUnRecpParm.setData("MR_NO",
				oldDataRecpParm.getData("MR_NO", 0) == null ? new TNull(
						String.class) : oldDataRecpParm.getData("MR_NO", 0));
		newUnRecpParm.setData("RESET_RECEIPT_NO", oldDataRecpParm.getData(
				"RESET_RECEIPT_NO", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("RESET_RECEIPT_NO", 0));
		newUnRecpParm.setData("PRINT_NO", oldDataRecpParm
				.getData("PRINT_NO", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("PRINT_NO", 0));
		newUnRecpParm.setData("BILL_DATE", oldDataRecpParm.getData("BILL_DATE",
				0) == null ? new TNull(Timestamp.class) : oldDataRecpParm
				.getData("BILL_DATE", 0));
		newUnRecpParm.setData("CHARGE_DATE", oldDataRecpParm.getData(
				"CHARGE_DATE", 0) == null ? new TNull(Timestamp.class)
				: oldDataRecpParm.getData("CHARGE_DATE", 0));
		newUnRecpParm.setData("PRINT_DATE", oldDataRecpParm.getData(
				"PRINT_DATE", 0) == null ? new TNull(Timestamp.class)
				: oldDataRecpParm.getData("PRINT_DATE", 0));
		newUnRecpParm.setData("REG_FEE", -oldDataRecpParm.getDouble("REG_FEE",
				0));
		newUnRecpParm.setData("REG_FEE_REAL", -oldDataRecpParm.getDouble(
				"REG_FEE_REAL", 0));
		newUnRecpParm.setData("CLINIC_FEE", -oldDataRecpParm.getDouble(
				"CLINIC_FEE", 0));
		newUnRecpParm.setData("CLINIC_FEE_REAL", -oldDataRecpParm.getDouble(
				"CLINIC_FEE_REAL", 0));
		newUnRecpParm.setData("SPC_FEE", -oldDataRecpParm.getDouble("SPC_FEE",
				0));
		newUnRecpParm.setData("OTHER_FEE1", -oldDataRecpParm.getDouble(
				"OTHER_FEE1", 0));
		newUnRecpParm.setData("OTHER_FEE2", -oldDataRecpParm.getDouble(
				"OTHER_FEE2", 0));
		newUnRecpParm.setData("OTHER_FEE3", -oldDataRecpParm.getDouble(
				"OTHER_FEE3", 0));
		newUnRecpParm
				.setData("AR_AMT", -oldDataRecpParm.getDouble("AR_AMT", 0));
		newUnRecpParm.setData("PAY_CASH", -oldDataRecpParm.getDouble(
				"PAY_CASH", 0));
		newUnRecpParm.setData("PAY_BANK_CARD", -oldDataRecpParm.getDouble(
				"PAY_BANK_CARD", 0));
		newUnRecpParm.setData("PAY_CHECK", -oldDataRecpParm.getDouble(
				"PAY_CHECK", 0));
		newUnRecpParm.setData("PAY_MEDICAL_CARD", -oldDataRecpParm.getDouble(
				"PAY_MEDICAL_CARD", 0));
		newUnRecpParm.setData("PAY_INS_CARD", -oldDataRecpParm.getDouble(
				"PAY_INS_CARD", 0));
		newUnRecpParm.setData("PAY_DEBIT", -oldDataRecpParm.getDouble(
				"PAY_DEBIT", 0));
		newUnRecpParm.setData("PAY_INS", -oldDataRecpParm.getDouble("PAY_INS",
				0));
		newUnRecpParm.setData("REMARK",
				oldDataRecpParm.getData("REMARK", 0) == null ? new TNull(
						String.class) : oldDataRecpParm.getData("REMARK", 0));
		newUnRecpParm.setData("CASH_CODE",
				parm.getData("OPT_USER") == null ? new TNull(String.class)
						: parm.getData("OPT_USER"));
		newUnRecpParm.setData("ACCOUNT_FLG", oldDataRecpParm.getData(
				"ACCOUNT_FLG", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("ACCOUNT_FLG", 0));
		newUnRecpParm.setData("ACCOUNT_SEQ", oldDataRecpParm.getData(
				"ACCOUNT_SEQ", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("ACCOUNT_SEQ", 0));
		newUnRecpParm.setData("ACCOUNT_USER", oldDataRecpParm.getData(
				"ACCOUNT_USER", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("ACCOUNT_USER", 0));
		newUnRecpParm.setData("ACCOUNT_DATE", oldDataRecpParm.getData(
				"ACCOUNT_DATE", 0) == null ? new TNull(Timestamp.class)
				: oldDataRecpParm.getData("ACCOUNT_DATE", 0));
		newUnRecpParm.setData("BANK_SEQ", oldDataRecpParm
				.getData("BANK_SEQ", 0) == null ? new TNull(String.class)
				: oldDataRecpParm.getData("BANK_SEQ", 0));
		newUnRecpParm.setData("BANK_DATE", oldDataRecpParm.getData("BANK_DATE",
				0) == null ? new TNull(Timestamp.class) : oldDataRecpParm
				.getData("BANK_DATE", 0));
		newUnRecpParm.setData("BANK_USER", oldDataRecpParm.getData("BANK_USER",
				0) == null ? new TNull(String.class) : oldDataRecpParm.getData(
				"BANK_USER", 0));
		newUnRecpParm.setData("OPT_USER",
				parm.getData("OPT_USER") == null ? new TNull(String.class)
						: parm.getData("OPT_USER"));
		newUnRecpParm.setData("OPT_TERM",
				parm.getData("OPT_TERM") == null ? new TNull(String.class)
						: parm.getData("OPT_TERM"));
		// 退挂,收据档写入一笔负数资料
		result = BILREGRecpTool.getInstance().insertDataForUnReg(newUnRecpParm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;

	}

	/**
	 * 更新已作废票据的accountSeq ===zhangp 20120320
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm updateRestInvrcp(TParm parm, TConnection connection) {
		String billDate = parm.getData("BILL_DATE").toString();
		String sql = "SELECT PRINT_NO FROM BIL_REG_RECP WHERE AR_AMT < 0 AND (ACCOUNT_FLG IS NULL OR ACCOUNT_FLG = 'N') "
				+ " AND ADM_TYPE = '"
				+ parm.getData("ADM_TYPE")
				+ "' AND CASH_CODE = '"
				+ parm.getData("CASH_CODE")
				+ "' "
				+ " AND BILL_DATE < TO_DATE('"
				+ billDate
				+ "','yyyyMMddHH24miss') " + " AND RESET_RECEIPT_NO IS NULL";
		TParm temp = new TParm(TJDODBTool.getInstance().select(sql));
		if (temp.getErrCode() < 0) {
			return temp;
		}
		if (temp.getCount() < 0) {
			return temp;
		}
		String printNos = "";
		for (int i = 0; i < temp.getCount(); i++) {
			printNos += ",'" + temp.getData("PRINT_NO", i) + "'";
		}
		printNos = printNos.substring(1, printNos.length());
		String accntDate = parm.getData("ACCOUNT_DATE").toString();
		accntDate = accntDate.substring(0, 4) + accntDate.substring(5, 7)
				+ accntDate.substring(8, 10) + accntDate.substring(11, 13)
				+ accntDate.substring(14, 16) + accntDate.substring(17, 19);
		sql = "UPDATE BIL_INVRCP SET ACCOUNT_SEQ = '"
				+ parm.getData("ACCOUNT_SEQ") + "',ACCOUNT_USER = '"
				+ parm.getData("ACCOUNT_USER") + "',ACCOUNT_DATE = TO_DATE('"
				+ accntDate + "','yyyyMMddHH24miss') WHERE INV_NO IN ("
				+ printNos + ") AND RECP_TYPE = 'REG'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,
				connection));
		return result;
	}

	/**
	 * 挂VIP号失败 将此VIP就诊号设置 未占号状态
	 * 
	 * @return ============pangben 2012-7-1
	 */
	public boolean concelVIPQueNo(TParm parm) {
		if (parm.getBoolean("VIP_FLG")) {
			String admDate=StringTool.getString(TCM_Transform.getTimestamp(parm.getData("ADM_DATE")), "yyyyMMdd");
			String vipSql = "UPDATE REG_CLINICQUE SET QUE_STATUS='N' "
					+ "WHERE ADM_TYPE='" + parm.getValue("ADM_TYPE")
					+ "' AND ADM_DATE='" + admDate
					+ "' AND SESSION_CODE='" + parm.getValue("SESSION_CODE")
					+ "' AND CLINICROOM_NO='" + parm.getValue("CLINICROOM_NO")
					+ "' AND QUE_NO= '" + parm.getValue("QUE_NO")
					+ "' AND QUE_STATUS='Y' ";
			//System.out.println("vipSql：：：：："+vipSql);
			TParm result = new TParm(TJDODBTool.getInstance().update(vipSql));
			if (result.getErrCode() < 0) {
				return false;
			}
		}
		return true;
	}
	/**
	 * 挂号获得有效天数
	 * ======pangben 2013-4-28
	 * @return
	 */
	public TParm getRegParm() {
		String SQL = "SELECT EFFECT_DAYS FROM REG_SYSPARM";
		return new TParm(TJDODBTool.getInstance().select(SQL));
	}
	
	/**
	 * 退号不占号
	 * @return
	 */
	private TParm unRegQue(String caseNo, TConnection tConnection){
		String sql =
			" SELECT ADM_TYPE," +
			" TO_CHAR (ADM_DATE, 'YYYYMMDD') ADM_DATE," +
			" SESSION_CODE," +
			" CLINICROOM_NO," +
			" QUE_NO" +
			" FROM REG_PATADM" +
			" WHERE CASE_NO = '" + caseNo + "'";
		TParm reg = new TParm(TJDODBTool.getInstance().select(sql));
		sql = "UPDATE REG_CLINICQUE SET QUE_STATUS = 'N' WHERE ADM_TYPE = '"
			+ reg.getValue("ADM_TYPE", 0)
			+ "'AND ADM_DATE = '"
			+ reg.getValue("ADM_DATE", 0)
			+ "' AND "
			+ "SESSION_CODE = '"
			+ reg.getValue("SESSION_CODE", 0)
			+ "' AND "
			+ "CLINICROOM_NO = '"
			+ reg.getValue("CLINICROOM_NO", 0)
			+ "' AND "
			+ "QUE_NO = '"
			+ reg.getValue("QUE_NO", 0) + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, tConnection));
		return result;
	}

	/**
	 * 查询该病患是否退挂   add by huangtt 20140926
	 * @param caseNo
	 * @return
	 */
	public boolean queryUnReg(String caseNo){
		String sql = "SELECT CASE_NO COUNT FROM REG_PATADM WHERE CASE_NO='"+caseNo+"' AND REGCAN_USER IS NOT NULL AND REGCAN_DATE IS NOT NULL";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		boolean flg= false;
		if(result.getCount()>0){
			flg=true;
		}
		return flg;
	}
	
	/**
	 * 更新挂号档中的的报告状态  add by huangtt 20150720
	 * @param parm
	 * @return
	 */
	public TParm updateReportStatus(TParm parm){
		String sql = "UPDATE REG_PATADM SET REPORT_STATUS='"+parm.getValue("REPORT_STATUS")+"' WHERE" +
				" CASE_NO ='"+parm.getValue("CASE_NO")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		return result;
	}
	
}
