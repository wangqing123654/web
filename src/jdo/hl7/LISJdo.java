package jdo.hl7;

import com.dongyang.jdo.TJDOTool;
import java.sql.Timestamp;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import java.util.Map;
import java.util.HashMap;

import jdo.bms.ws.BmsTool;
import jdo.odi.OdiMainTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.exception.HL7Exception;

public class LISJdo extends TJDOTool {
	private String bilpoint;// 计费点 1护士执行计费 2医技科室执行
	/**
	 * 实例
	 */
	public static LISJdo instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return TestJDO
	 */
	public static synchronized LISJdo getInstance() {
		if (instanceObject == null)
			instanceObject = new LISJdo();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public LISJdo() {
		setModuleName("lis\\LisJavaHisTQL.x");
		onInit();
	}

	/**
	 * 得到系统时间
	 * 
	 * @return Timestamp 生效日期
	 */
	public Timestamp getDate() {
		TParm parm = new TParm();
		return getResultTimestamp(query("getDate", parm), "SYSDATE");
	}

	/**
	 * 更新LIS新医嘱发送状态
	 * 
	 * @param labnumberNo
	 *            String 条码号
	 * @return boolean
	 */
	public boolean getUpDateNewLabStust(String labnumberNo) {
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		// Log.DEBUG = true;
		TParm action = this.update("getUpDateNewLabStust", parm);
		if (action.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 更新LIS新医嘱签收状态
	 * 
	 * @param labnumberNo
	 *            String 条码号
	 * @return boolean
	 */
	public TParm upDateLisQS(String labnumberNo, String admType) throws HL7Exception{
		TParm parm = new TParm();
		TParm medData = Hl7Tool.getInstance().getOrder(labnumberNo, "LIS","");
		if (medData.getCount() <= 0) {
			medData.setErr(-1, "未查询到医嘱");
			return medData;
		}
		TConnection con = this.getConnection();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.update("upDateLisQS", parm, con);
		if (action.getErrCode() < 0) {
			action.setErr(-1, "更新签收状态失败");
			con.rollback();
			con.close();
			return action;
		}
		if (admType.equals("I")) {
			parm.setData("EXEC_FLG", "Y");
			action = this.update("updateOdiOrderTable", parm, con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "更新住院执行状态失败");
				con.rollback();
				con.close();
				return action;
			}
		}
		con.commit();
		con.close();
		return action;
	}

	/**
	 * 更新LIS新医嘱取消签收状态
	 * 
	 * @param labnumberNo
	 *            String 条码号
	 * @return boolean
	 */
	public TParm upDateQXLisQS(String labnumberNo, String admType)throws HL7Exception {
		TParm parm = new TParm();
		TParm medData = Hl7Tool.getInstance().getOrder(labnumberNo, "LIS","");
		if (medData.getCount() <= 0) {
			medData.setErr(-1, "未查询到医嘱");
			return medData;
		}
		TConnection con = this.getConnection();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.update("upDateQXLisQS", parm,con);
		if (action.getErrCode() < 0) {
			action.setErr(-1, "更新签收状态失败");
			con.rollback();
			con.close();
			return action;
		}
		if (admType.equals("I")) {
			parm.setData("EXEC_FLG", "N");
			action = this.update("updateOdiOrderTable", parm, con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "更新住院执行状态失败");
				con.rollback();
				con.close();
				return action;
			}
		}
		con.commit();
		con.close();
		return action;
	}

	/**
	 * LIS到检检体核收
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public TParm upDateLisDJ(String labnumberNo, String admType) throws HL7Exception{
		TParm medData = Hl7Tool.getInstance().getOrder(labnumberNo, "LIS","");
		if (medData.getCount() <= 0) {
			medData.setErr(-1, "未查询到医嘱");
			return medData;
		}
		TConnection con = this.getConnection();
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		parm.setData("EXEC_FLG", "Y");
		TParm action = this.update("upDateLisDJ", parm, con);
		if (action.getErrCode() < 0) {
			action.setErr(-1, "更新到检状态失败");
			con.rollback();
			con.close();
			return action;
		}
		if ("O".equals(admType)) {
			// action = BILJdo.getInstance().onOEBilCheck(parm, "LIS");
			// if (action.getErrCode() < 0) {
			// con.rollback();
			// con.close();
			// return action;
			// }
			// action = this.update("updateOpdOrderTable", parm, con);
			// if (action.getErrCode() < 0) {
			// action.setErr(-1, "更新门诊执行状态失败");
			// con.rollback();
			// con.close();
			// return action;
			// }
		}
		if ("E".equals(admType)) {
			// action = BILJdo.getInstance().onOEBilCheck(parm, "LIS");
			// if (action.getErrCode() < 0) {
			// con.rollback();
			// con.close();
			// return action;
			// }
			// action = this.update("updateOpdOrderTable", parm, con);
			// if (action.getErrCode() < 0) {
			// action.setErr(-1, "更新急诊执行状态失败");
			// con.rollback();
			// con.close();
			// return action;
			// }
		}
		if ("I".equals(admType)) {
			bilpoint = (String) OdiMainTool.getInstance().getOdiSysParmData(
					"BIL_POINT");
			if (bilpoint.equals("2")) {
				action = BILJdo.getInstance().onIBilOperate(con, parm, "LIS",
						"ADD");
				if (action.getErrCode() < 0) {
					con.rollback();
					con.close();
					return action;
				}
			}
		}
		if ("H".equals(admType)) {
			action = this.update("updateHrmOrderTable", parm, con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "更新健检执行状态失败");
				con.rollback();
				con.close();
				return action;
			}
		}
		con.commit();
		con.close();
		return action;
	}

	/**
	 * LIS取消检体核收
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public TParm upDateLisQXDJ(String labnumberNo, String admType) throws HL7Exception{
		TParm medData = Hl7Tool.getInstance().getOrder(labnumberNo, "LIS","");
		if (medData.getCount() <= 0) {
			medData.setErr(-1, "未查询到医嘱");
			return medData;
		}
		TConnection con = this.getConnection();
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		parm.setData("EXEC_FLG", "N");
		parm.setData("EXEC_DR_CODE", "");
		parm.setData("EXEC_DATE", new TNull(Timestamp.class));
		TParm action = this.update("upDateLisQXDJ", parm);
		if (action.getErrCode() < 0) {
			action.setErr(-1, "取消到检状态更新失败");
			return action;
		}
		if ("O".equals(admType)) {
			action = this.update("updateOpdOrderTable", parm, con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "门诊执行状态更新失败");
				con.rollback();
				con.close();
				return action;
			}
			action = upDateLisPrint(labnumberNo, "N", con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "门诊打印状态更新失败");
				con.rollback();
				con.close();
				return action;
			}
		}
		if ("E".equals(admType)) {
			action = this.update("updateOpdOrderTable", parm, con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "急诊执行状态更新失败");
				con.rollback();
				con.close();
				return action;
			}
			action = upDateLisPrint(labnumberNo, "N", con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "急诊打印状态更新失败");
				con.rollback();
				con.close();
				return action;
			}
		}
		if ("I".equals(admType)) {
			bilpoint = (String) OdiMainTool.getInstance().getOdiSysParmData(
					"BIL_POINT");
			action = this.update("updateOdiOrderTable", parm, con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "住院执行状态更新失败");
				con.rollback();
				con.close();
				return action;
			}
			if (bilpoint.equals("2")) {
				action = BILJdo.getInstance().onIBilOperate(con, parm, "LIS",
						"UNADD");
				if (action.getErrCode() < 0) {
					con.rollback();
					con.close();
					return action;
				}
			}
		}
		if ("H".equals(admType)) {
			action = this.update("updateHrmOrderTable", parm, con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "健检执行状态更新失败");
				con.rollback();
				con.close();
				return action;
			}
		}
		con.commit();
		con.close();
		return action;
	}

	/**
	 * LIS检体拒收
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public TParm upDateLisJSDJ(String labnumberNo,String admType) throws HL7Exception{
		TParm medData = Hl7Tool.getInstance().getOrder(labnumberNo, "LIS","");
		if (medData.getCount() <= 0) {
			medData.setErr(-1, "未查询到医嘱");
			return medData;
		}
		TConnection con = this.getConnection();
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.update("upDateLisJSDJ", parm,con);
		if (action.getErrCode() < 0) {
			action.setErr(-1, "检验拒收状态更新失败");
			con.rollback();
			con.close();
			return action;
		}
		if (admType.equals("I")) {
			parm.setData("EXEC_FLG", "N");
			action = this.update("updateOdiOrderTable", parm, con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "更新住院执行状态失败");
				con.rollback();
				con.close();
				return action;
			}
		}
		con.commit();
		con.close();
		return action;
	}
	/**
	 * LIS检体打印
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public TParm upDateLisPrint(String labnumberNo,String printFlg,TConnection connection) throws HL7Exception{
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		parm.setData("PRINT_FLG", printFlg);
		TParm action = this.update("upDateLisPrint", parm,connection);
		if (action.getErrCode() < 0) {
			action.setErr(-1, "检验打印更新失败");
			return action;
		}
		return action;
	}

	/**
	 * 更新护士工作站状态
	 * 
	 * @param labnumberNo
	 *            String
	 * @return TParm
	 */
	public TParm selectNBWStat(String labnumberNo) {
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.query("selectNBWStat", parm);
		return action;
	}

	/**
	 * 审核完成
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public TParm upDateSHWC(String labnumberNo) throws HL7Exception{
		TParm medData = Hl7Tool.getInstance().getOrder(labnumberNo, "LIS","");
		if (medData.getCount() <= 0) {
			medData.setErr(-1, "未查询到医嘱");
			return medData;
		}
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.update("upDateSHWC", parm);
		if (action.getErrCode() < 0) {
			action.setErr(-1, "审核失败");
			return action;
		}
		return action;
	}

	/**
	 * 删除临检表数据
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public TParm delLAB_GENRPTDTLData(String labnumberNo) throws HL7Exception{
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.update("delLAB_GENRPTDTLData", parm);
		if (action.getErrCode() < 0) {
			return action;
		}
		return action;
	}

	/**
	 * 删除微免表的LIS报告数据
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public TParm delLAB_ANTISENSTESTData(String labnumberNo,
			String SampleNo) throws HL7Exception{
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		parm.setData("SAMPLE_NO", SampleNo);
		TParm action = this.update("delLAB_ANTISENSTESTData", parm);
		if (action.getErrCode() < 0) {
			return action;
		}
		return action;
	}

	/**
	 * 删除细菌表的LIS报告数据
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public TParm delLAB_CULRPTDTLData(String labnumberNo,
			String SampleNo) throws HL7Exception{
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		parm.setData("SAMPLE_NO", SampleNo);
		TParm action = this.update("delLAB_CULRPTDTLData", parm);
		if (action.getErrCode() < 0) {
			return action;
		}
		return action;
	}

	/**
	 * 插入临检表数据
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public TParm insertLAB_GENRPTDTLData(TParm parm) throws HL7Exception{
		TParm action = this.update("insertLAB_GENRPTDTLData", parm);
		if (action.getErrCode() < 0) {
			return action;
		}
		return action;
	}

	/**
	 * 得到orderNo长度
	 * 
	 * @param admType
	 * @return
	 */
	public int getorderNolen(String admType) throws HL7Exception{
		int length = 0;
		TParm parm = new TParm();
		if ("O".equals(admType) || "E".equals(admType)) {
			TParm action = this.query("getOorderNO", parm);
			length = action.getInt("NO_LENGTH", 0);
		} else if (admType.equals("I")) {
			TParm action = this.query("getIorderNO", parm);
			length = action.getInt("NO_LENGTH", 0);
		} else if ("H".equals(admType)) {
			TParm action = this.query("getHorderNO", parm);
			length = action.getInt("NO_LENGTH", 0);
		}
		return length;
	}

	/**
	 * 插入微免表数据
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public TParm insertLAB_ANTISENSTESTData(TParm parm) throws HL7Exception{
		TParm action = this.update("insertLAB_ANTISENSTESTData", parm);
		if (action.getErrCode() < 0) {
			return action;
		}
		return action;
	}

	/**
	 * 插入细菌表数据
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public TParm insertLAB_CULRPTDTLData(TParm parm) throws HL7Exception{
		TParm action = this.update("insertLAB_CULRPTDTLData", parm);
		if (action.getErrCode() < 0) {
			return action;
		}
		return action;
	}

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 报告完成
	 * 
	 * @return boolean
	 */
	public TParm upDateBGEND(String labNumberNo, String rptDttm,
			String drUser, String shUser, String admType) throws HL7Exception{
		TParm parm = new TParm();
		String userdr[] = StringTool.parseLine(drUser, "^");
		String usersh[] = StringTool.parseLine(shUser, "^");
		parm.setData("LAB_NUMBER", labNumberNo);
		parm.setData("RPT_DTTM", rptDttm);
		String testUser="";
		if (userdr.length > 0) {
			parm.setData("DLVRYRPT_USER", userdr[0]);
			testUser=userdr[0];
		} else {
			parm.setData("DLVRYRPT_USER", "");
		}
		if (usersh.length > 0) {
			parm.setData("SH_USER", usersh[0]);
		} else {
			parm.setData("SH_USER", "");
		}
		TParm action = this.update("upDateBGEND", parm);
		if (action.getErrCode() < 0) {
			return action;
		}
		//===================================shibl add start=============================================================
		TParm medLog=Hl7Tool.getInstance().onInsertMedNodify(labNumberNo, "LIS");
		if(medLog.getErrCode()<0){
			System.out.println("插入MedNodify表出现错误"+medLog);
		}
		TParm regParm = new TParm(this.getDBTool().select(
				"SELECT CASE_NO,MR_NO FROM MED_APPLY WHERE APPLICATION_NO='"
						+ labNumberNo + "' AND CAT1_TYPE='LIS'"));
		if (regParm.getCount() > 0) {
			String mrNo = regParm.getValue("MR_NO", 0);
			TParm patParm=new TParm();
			try {
				patParm = BmsTool.getInstance().onMedExeBld(labNumberNo, mrNo,testUser);
			} catch (Exception e) {
				e.printStackTrace();
				throw new HL7Exception("更新病患血型操作异常");
			}
			if(patParm.getErrCode()<0){
				System.out.println("jdo.hl7.LISJdo->upDateBGEND 更新sys_patinfo表血型信息失败"+patParm);
			}
		}
		//=====================================shibl add end=============================================================
		if (regParm.getCount() > 0) {
			String caseNo = regParm.getValue("CASE_NO", 0);
			String mrNo = regParm.getValue("MR_NO", 0);
			regParm = new TParm(this.getDBTool().select(
					"SELECT STATUS FROM MED_APPLY WHERE CASE_NO='" + caseNo
							+ "' AND MR_NO='" + mrNo + "'"));
			Map map = new HashMap();
			map.put(7, "Y");
			map.put(8, "Y");
			int rowCount = regParm.getCount();
			int statCount = 0;
			if (rowCount > 0) {
				for (int i = 0; i < rowCount; i++) {
					TParm temp = regParm.getRow(i);
					if (map.get(temp.getInt("STATUS")) != null) {
						statCount++;
					}
				}
			}
			// 赋值REG状态
			if (statCount == (rowCount)) {
				// 全部完成
				TParm p = new TParm();
				p.setData("REPORT_STATUS", "3");
				p.setData("CASE_NO", caseNo);
				// 门急
				if ("O".equals(admType) || "E".equals(admType)){
					action = this.update("updateRegPatAdmStstus", p);
					if (action.getErrCode() < 0) {
						return action;
					}
				}
				// 健康检查
				if ("H".equals(admType)) {
					// 查看其他项目
					TParm hrmParm = new TParm(
							this
									.getDBTool()
									.select(
											"SELECT CASE WHEN (A.EXEC_DR_CODE IS NULL OR A.EXEC_DR_CODE = '') THEN 'N' ELSE 'Y' END DONE FROM HRM_ORDER A    WHERE A.CASE_NO='"
													+ caseNo
													+ "' AND A.SETMAIN_FLG='Y' AND DEPT_ATTRIBUTE IS NOT NULL"));
					int hrmParmCount = hrmParm.getCount();
					for (int i = 0; i < hrmParmCount; i++) {
						TParm temp = hrmParm.getRow(i);
						if ("N".equals(temp.getValue("DONE"))) {
							p.setData("REPORT_STATUS", "2");
							break;
						}
					}
					action = this.update("updateHrmPatAdmStstus", p);
					if (action.getErrCode() < 0) {
						return action;
					}
				}
			} else {
				// 部分完成
				TParm p = new TParm();
				p.setData("REPORT_STATUS", "2");
				p.setData("CASE_NO", caseNo);
				// 门急
				if ("O".equals(admType) || "E".equals(admType)){
					action = this.update("updateRegPatAdmStstus", p);
					if (action.getErrCode() < 0) {
						return action;
					}
				}
				// 健康检查
				if ("H".equals(admType)){
					action = this.update("updateHrmPatAdmStstus", p);
					if (action.getErrCode() < 0) {
						return action;
					}
				}
			}
		}
		return action;
	}

	/**
	 * 查询通知消息内容；
	 * 
	 * @param cat1Type
	 *            String
	 * @param applicationNo
	 *            String
	 * @param orderNo
	 *            String
	 * @param seqNo
	 *            String
	 * @return TParm
	 */
	public TParm selectMedApply(String cat1Type,
			String applicationNo, String orderNo, String seqNo) throws HL7Exception{
		StringBuffer sb = new StringBuffer();
		sb
				.append("SELECT A.MR_NO,A.CASE_NO,A.PAT_NAME,A.ORDER_DR_CODE,B.USER_NAME,B.E_MAIL ");
		sb
				.append("FROM MED_APPLY A LEFT JOIN SYS_OPERATOR B ON A.ORDER_DR_CODE=B.USER_ID ");
		sb.append("WHERE A.CAT1_TYPE='" + cat1Type + "' ");
		sb.append("AND A.APPLICATION_NO='" + applicationNo + "' ");
		sb.append("AND A.ORDER_NO='" + orderNo + "' ");
		sb.append("AND A.SEQ_NO='" + seqNo + "'");
		// MED_APPLY
		TParm parm = new TParm(this.getDBTool().select(sb.toString()));
		return parm;
	}

}
