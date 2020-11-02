package jdo.hl7;

import com.dongyang.jdo.TJDOTool;
import java.sql.Timestamp;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.exception.HL7Exception;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import jdo.odi.OdiMainTool;
import jdo.sys.SystemTool;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class RISJdo extends TJDOTool {
	private String bilpoint;// 计费点 1护士执行计费 2医技科室执行
	/**
	 * 实例
	 */
	public static RISJdo instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return TestJDO
	 */
	public static synchronized RISJdo getInstance() {
		if (instanceObject == null)
			instanceObject = new RISJdo();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public RISJdo() {
		setModuleName("ris\\risJavaHisTQL.x");
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
	 * 更新RIS新医嘱发送状态
	 * 
	 * @param labnumberNo
	 *            String 条码号
	 * @return boolean
	 */
	public boolean getUpDateNewRisStust(String labnumberNo) {
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		// Log.DEBUG = true;
		TParm action = this.update("getUpDateNewRisStust", parm);
		if (action.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 预约
	 * 
	 * @param labnumberNo
	 *            String
	 * @param status
	 *            String
	 * @return boolean
	 */
	public TParm upDateRisYY(String labnumberNo, String status,
			String dateTime) throws HL7Exception {
		TParm medData = Hl7Tool.getInstance().getOrder(labnumberNo, "RIS", "");
		if (medData.getCount() <= 0) {
			medData.setErr(-1, "未查询到医嘱");
			return medData;
		}
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		parm.setData("STATUS", status);
		parm.setData("YYDATE", dateTime);
		TParm action = this.update("upDateRisYY", parm);
		if (action.getErrCode() < 0) {
			return action;
		}
		return action;
	}

	/**
	 * RIS到检检体核收
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public  TParm upDateRisDJ(String labnumberNo, String status,
			String dateTime, String admType, String user) throws HL7Exception {
		TParm medData = Hl7Tool.getInstance().getOrder(labnumberNo, "RIS", "");
		if (medData.getCount() <= 0) {
			medData.setErr(-1, "未查询到医嘱");
			return medData;
		}
		TConnection cn = this.getConnection();
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		parm.setData("STATUS", status);
		parm.setData("DJDATE", dateTime);
		parm.setData("EXEC_DR_CODE", user);
		TParm action = this.update("upDateRisDJ", parm, cn);
		if (action.getErrCode() < 0) {
			action.setErr(-1, "到检更新失败");
			cn.rollback();
			cn.close();
			return action;
		}
		action = this.upDateHisDJOrder(admType, status, labnumberNo, cn);
		return action;
	}

	/**
	 * 更新HIS医嘱表退费注记状态
	 * 
	 * @param admType
	 *            String
	 * @param status
	 *            String
	 * @return boolean
	 */
	public TParm upDateHisDJOrder(String admType, String status,
			String labnumberNo, TConnection con) throws HL7Exception {
		TParm exeSql = new TParm();
		if ("O".equals(admType)) {
			TParm action = new TParm(getDBTool().select(
					"SELECT ORDERSET_CODE,ORDERSET_GROUP_NO FROM OPD_ORDER WHERE MED_APPLY_NO='"
							+ labnumberNo
							+ "' AND ADM_TYPE='O' AND CAT1_TYPE='RIS'"));
			String orderSetCode = action.getValue("ORDERSET_CODE", 0);
			// System.out.println("集合医嘱主项号:" + orderSetCode);
			String groupNo = action.getValue("ORDERSET_GROUP_NO", 0);
			// System.out.println("集合医嘱组号:" + groupNo);
			TParm parm = new TParm();
			parm.setData("MED_APPLY_NO", labnumberNo);
			parm.setData("ORDERSET_CODE", orderSetCode);
			parm.setData("ORDERSET_GROUP_NO", groupNo);
			// 到检
			if ("4".equals(status)) {
				parm.setData("EXEC_FLG", "Y");
				parm.setData("EXEC_DR_CODE", "MEDWEBSERVICE");
				parm.setData("EXEC_DATE", SystemTool.getInstance().getDate());
				// 判断余额
				exeSql = BILJdo.getInstance().onOEBilCheck(parm, "RIS");
				if (exeSql.getErrCode() < 0) {
					con.rollback();
					con.close();
					return exeSql;
				}
				exeSql = this.update("updateOpdOrderTable", parm, con);
				if (exeSql.getErrCode() < 0) {
					exeSql.setErr(-1, "门诊执行状态更新失败");
					con.rollback();
					con.close();
					return exeSql;
				}
			}
			// 取消到检
			if ("5".equals(status)) {
				parm.setData("EXEC_FLG", "N");
				parm.setData("EXEC_DR_CODE", "");
				parm.setData("EXEC_DATE", new TNull(Timestamp.class));
				exeSql = this.update("updateOpdOrderTable", parm, con);
				if (exeSql.getErrCode() < 0) {
					exeSql.setErr(-1, "门诊执行状态更新失败");
					con.rollback();
					con.close();
					return exeSql;
				}
			}
		}
		if ("E".equals(admType)) {
			TParm action = new TParm(getDBTool().select(
					"SELECT ORDERSET_CODE,ORDERSET_GROUP_NO FROM OPD_ORDER WHERE MED_APPLY_NO='"
							+ labnumberNo
							+ "' AND ADM_TYPE='E' AND CAT1_TYPE='RIS'"));
			String orderSetCode = action.getValue("ORDERSET_CODE", 0);
			String groupNo = action.getValue("ORDERSET_GROUP_NO", 0);
			TParm parm = new TParm();
			parm.setData("MED_APPLY_NO", labnumberNo);
			parm.setData("ORDERSET_CODE", orderSetCode);
			parm.setData("ORDERSET_GROUP_NO", groupNo);
			// 到检
			if ("4".equals(status)) {
				parm.setData("EXEC_FLG", "Y");
				parm.setData("EXEC_DR_CODE", "MEDWEBSERVICE");
				parm.setData("EXEC_DATE", SystemTool.getInstance().getDate());
				// 判断余额
				exeSql = BILJdo.getInstance().onOEBilCheck(parm, "RIS");
				if (exeSql.getErrCode() < 0) {
					con.rollback();
					con.close();
					return exeSql;
				}
				exeSql = this.update("updateOpdOrderTable", parm, con);
				if (exeSql.getErrCode() < 0) {
					exeSql.setErr(-1, "急诊执行状态更新失败");
					con.rollback();
					con.close();
					return exeSql;
				}
			}
			// 取消到检
			if ("5".equals(status)) {
				parm.setData("EXEC_FLG", "N");
				parm.setData("EXEC_DR_CODE", "");
				parm.setData("EXEC_DATE", new TNull(Timestamp.class));
				exeSql = this.update("updateOpdOrderTable", parm, con);
				if (exeSql.getErrCode() < 0) {
					exeSql.setErr(-1, "急诊执行状态更新失败");
					con.rollback();
					con.close();
					return exeSql;
				}
			}
		}
		if ("I".equals(admType)) {
			bilpoint = (String) OdiMainTool.getInstance().getOdiSysParmData(
					"BIL_POINT");
			TParm action = new TParm(getDBTool().select(
					"SELECT ORDERSET_CODE,ORDERSET_GROUP_NO FROM ODI_ORDER WHERE MED_APPLY_NO='"
							+ labnumberNo + "'  AND CAT1_TYPE='RIS'"));
			String orderSetCode = action.getValue("ORDERSET_CODE", 0);
			// System.out.println("集合医嘱主项号:" + orderSetCode);
			String groupNo = action.getValue("ORDERSET_GROUP_NO", 0);
			// System.out.println("集合医嘱组号:" + groupNo);
			TParm parm = new TParm();
			parm.setData("MED_APPLY_NO", labnumberNo);
			parm.setData("ORDERSET_CODE", orderSetCode);
			parm.setData("ORDERSET_GROUP_NO", groupNo);
			// 到检
			if ("4".equals(status)) {
				parm.setData("EXEC_FLG", "Y");
				exeSql = this.update("updateOdiOrderTable", parm, con);
				if (exeSql.getErrCode() < 0) {
					exeSql.setErr(-1, "住院执行状态更新失败");
					con.rollback();
					con.close();
					return exeSql;
				}
				if (bilpoint.equals("2")) {
					// 住院计费
					exeSql = BILJdo.getInstance().onIBilOperate(con, parm,
							"RIS", "ADD");
					if (exeSql.getErrCode() < 0) {
						con.rollback();
						con.close();
						return exeSql;
					}
				}
			}
			// 取消到检
			if ("5".equals(status)) {
				parm.setData("EXEC_FLG", "N");
				exeSql = this.update("updateOdiOrderTable", parm, con);
				if (exeSql.getErrCode() < 0) {
					exeSql.setErr(-1, "住院执行状态更新失败");
					con.rollback();
					con.close();
					return exeSql;
				}
				if (bilpoint.equals("2")) {
					exeSql = BILJdo.getInstance().onIBilOperate(con, parm,
							"RIS", "UNADD");
					if (exeSql.getErrCode() < 0) {
						con.rollback();
						con.close();
						return exeSql;
					}
				}
			}
		}
		if ("H".equals(admType)) {
			TParm action = new TParm(
					getDBTool()
							.select("SELECT ORDERSET_CODE,ORDERSET_GROUP_NO,CASE_NO FROM HRM_ORDER WHERE MED_APPLY_NO='"
									+ labnumberNo + "'"));
			String orderSetCode = action.getValue("ORDERSET_CODE", 0);
			String groupNo = action.getValue("ORDERSET_GROUP_NO", 0);
			TParm parm = new TParm();
			parm.setData("MED_APPLY_NO", labnumberNo);
			parm.setData("ORDERSET_CODE", orderSetCode);
			parm.setData("ORDERSET_GROUP_NO", groupNo);

			// 到检
			if ("4".equals(status)) {
				parm.setData("EXEC_FLG", "Y");
				exeSql = this.update("updateHrmOrderTable", parm, con);
				if (exeSql.getErrCode() < 0) {
					exeSql.setErr(-1, "健检执行状态更新失败");
					con.rollback();
					con.close();
					return exeSql;
				}
			}
			// 取消到检
			if ("5".equals(status)) {
				parm.setData("EXEC_FLG", "N");
				exeSql = this.update("updateHrmOrderTable", parm, con);
				if (exeSql.getErrCode() < 0) {
					exeSql.setErr(-1, "健检执行状态更新失败");
					con.rollback();
					con.close();
					return exeSql;
				}
			}
		}
		con.commit();
		con.close();
		return exeSql;
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
	 * RIS报告完成
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public  TParm upDateRisWCJC(String labnumberNo, String status,
			String dateTime, String user, String devCode, String devDesc)
			throws HL7Exception {
		TParm medData = Hl7Tool.getInstance().getOrder(labnumberNo, "RIS", "");
		if (medData.getCount() <= 0) {
			medData.setErr(-1, "未查询到医嘱");
			return medData;
		}
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		parm.setData("STATUS", status);
		parm.setData("DJDATE", dateTime);
		parm.setData("EXEC_DR_CODE", user);
		parm.setData("EXEC_DEV_CODE", devCode);
		parm.setData("EXEC_DEV_DESC", devDesc);
		TParm action = this.update("upDateRisWCJC", parm);
		if (action.getErrCode() < 0) {
			return action;
		}
		return action;
	}

	/**
	 * 报告完成
	 * 
	 * @param labnumberNo
	 *            String 条码号
	 * @param status
	 *            String 状态
	 * @param dateTime
	 *            String 报告时间
	 * @param shUser
	 *            String 审核医师
	 * @param bgUser
	 *            String 报告医师
	 * @param typeStutas
	 *            String 报告状态
	 * @param zdyx
	 *            String 诊断印象
	 * @param zdsj
	 *            String 诊断所见
	 * @param type
	 *            String 阴阳性
	 * @param inMuf
	 *            String 厂商
	 * @param chNo
	 *            String 仪器代码(检查号)
	 * @param admType
	 *            String 门 急 住 健
	 * @return boolean
	 */
	public  TParm upDateRisSHBG(String labnumberNo, String status,
			String dateTime, String shDate, String shUser, String bgUser,
			String typeStutas, List<String> zdyx, List<String> zdsj,
			List<String> type, String inMuf, String chNo, List<String> bgNo,
			List<String> imageUrl, List<String> pdfPath, String admType)
			throws HL7Exception {
		TParm parm = new TParm();
		TConnection conn = this.getConnection();
		parm.setData("LAB_NUMBER", labnumberNo);
		parm.setData("STATUS", status);
		parm.setData("DJDATE", dateTime);
		parm.setData("SHDATE", shDate);
		parm.setData("SHUSER", shUser);
		parm.setData("BGUSER", bgUser);
		TParm action = new TParm();
		// 删除RIS检查
		action = this.update("deleteRisSHBG", parm, conn);
		if ("F".equals(typeStutas)) {
			action = this.update("upDateRisSHBG", parm, conn);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "报告更新失败");
				return action;
			}
			if ("H".equals(admType)) {
				action = this.update("updateHrmOrderWCStstus", parm, conn);
				action.setErr(-1, "健检报告更新失败");
			}
			if (action.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return action;
			}
			String cat1Type = "";
			if (inMuf.equals("ULT") || inMuf.equals("RIS"))
				cat1Type = "RIS";
			int count = 1;
			for (int i = 0; i < bgNo.size(); i++) {
				parm.setData("ORDER_CAT1_CODE", cat1Type);
				parm.setData("RPDTL_SEQ", count);
				parm.setData("EXEC_NO", chNo);
				parm.setData("OUTCOME_DESCRIBE",
						zdyx.size() >= (i + 1) ? zdyx.get(i) : "");
				parm.setData("OUTCOME_CONCLUSION",
						zdsj.size() >= (i + 1) ? zdsj.get(i) : "");
				parm.setData("OUTCOME_TYPE",
						type.size() >= (i + 1) ? type.get(i) : "");
				parm.setData("IMAGE_URL",
						imageUrl.size() >= (i + 1) ? imageUrl.get(i) : "");
				parm.setData("REPORT_ID", bgNo.get(i));
				parm.setData("PDF_PATH",
						pdfPath.size() >= (i + 1) ? pdfPath.get(i) : "");
				count++;
				action = this.update("insertRisSHBG", parm, conn);
				if (action.getErrCode() < 0) {
					action.setErr(-1, "报告更新失败");
					conn.rollback();
					conn.close();
					return action;
				}
			}
		}
		// 取消报告
		if ("D".equals(typeStutas)) {
			action = this.update("upDateRisSHBG", parm, conn);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "报告更新失败");
				conn.rollback();
				conn.close();
				return action;
			}
			action = this.update("deleteRisSHBG", parm, conn);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "报告更新失败");
				conn.rollback();
				conn.close();
				return action;
			}
		}
		conn.commit();
		conn.close();
		TParm regParm = new TParm(this.getDBTool().select(
				"SELECT CASE_NO,MR_NO FROM MED_APPLY WHERE APPLICATION_NO='"
						+ labnumberNo + "' AND  CAT1_TYPE='RIS'"));
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
				if ("O".equals(admType) || "E".equals(admType)) {
					action = this.update("updateRegPatAdmStstus", p);
					if (action.getErrCode() < 0) {
						action.setErr(-1, "门急诊报告更新失败");
					}
				}
				if ("H".equals(admType)) {
					// 查看其他项目
					TParm hrmParm = new TParm(
							this.getDBTool()
									.select("SELECT CASE WHEN (A.EXEC_DR_CODE IS NULL OR A.EXEC_DR_CODE = '') THEN 'N' ELSE 'Y' END DONE FROM HRM_ORDER A    WHERE A.CASE_NO='"
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
						action.setErr(-1, "健检报告更新失败");
						return action;
					}
				}
			} else {
				// 部分完成
				TParm p = new TParm();
				p.setData("REPORT_STATUS", "2");
				p.setData("CASE_NO", caseNo);
				if ("O".equals(admType) || "E".equals(admType))
					action = this.update("updateRegPatAdmStstus", p);
				if (action.getErrCode() < 0) {
					action.setErr(-1, "门急诊报告更新失败");
					return action;
				}
				if ("H".equals(admType)) {
					action = this.update("updateHrmPatAdmStstus", p);
					if (action.getErrCode() < 0) {
						action.setErr(-1, "健检报告更新失败");
						return action;
					}
				}
			}
		}
		TParm medLog = Hl7Tool.getInstance().onInsertMedNodify(labnumberNo,
				"RIS");
		if (medLog.getErrCode() < 0) {
			System.out.println("插入MedNodify表出现错误" + medLog);
		}
		return action;
	}

	/**
	 * LIS取消检体核收
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public  boolean upDateLisQXDJ(String labnumberNo)
			throws HL7Exception {
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.update("upDateLisQXDJ", parm);
		if (action.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * LIS检体拒收
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public  boolean upDateLisJSDJ(String labnumberNo)
			throws HL7Exception {
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.update("upDateLisJSDJ", parm);
		if (action.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 更新护士工作站状态
	 * 
	 * @param labnumberNo
	 *            String
	 * @return TParm
	 */
	public  TParm selectNBWStat(String labnumberNo) {
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
	public  boolean upDateSHWC(String labnumberNo)
			throws HL7Exception {
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.update("upDateSHWC", parm);
		if (action.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 删除临检表数据
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public  boolean delLAB_GENRPTDTLData(String labnumberNo) {
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.update("delLAB_GENRPTDTLData", parm);
		if (action.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 删除微免表的LIS报告数据
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public  boolean delLAB_ANTISENSTESTData(String labnumberNo) {
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.update("delLAB_ANTISENSTESTData", parm);
		if (action.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 删除细菌表的LIS报告数据
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public  boolean delLAB_CULRPTDTLData(String labnumberNo) {
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.update("delLAB_CULRPTDTLData", parm);
		if (action.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 插入临检表数据
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public  boolean insertLAB_GENRPTDTLData(TParm parm) {
		TParm action = this.update("insertLAB_GENRPTDTLData", parm);
		if (action.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 报告完成
	 * 
	 * @return boolean
	 */
	public  boolean upDateBGEND(String labNumberNo, String rptDttm,
			String drUser, String testSeq) throws HL7Exception {
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labNumberNo);
		parm.setData("RPT_DTTM", rptDttm);
		parm.setData("DLVRYRPT_USER", drUser);
		parm.setData("TESTSET_SEQ", testSeq);
		TParm action = this.update("upDateBGEND", parm);
		if (action.getErrCode() < 0) {
			return false;
		}
		return true;
	}
}
