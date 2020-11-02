package jdo.bms.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * @author shibl
 * 
 */
public class BmsTool extends TJDOTool {
	/**
	 * RH对应编码
	 */
	private static String rhMapType = "01";
	/**
	 * 血型对应编码
	 */
	private static String AOBMapType = "02";
	/**
	 * 实例
	 */
	public static BmsTool instanceObject;
	/**
	 * 新增sql
	 */
	private static final String sqL = " INSERT INTO BMS_BLOOD(BLOOD_NO,APPLY_NO,BLD_CODE,SUBCAT_CODE,BLOOD_VOL,"
			+ " OUT_NO,OUT_DATE,OUT_USER,MR_NO,CASE_NO,IPD_NO,STATE_CODE,"
			+ " DEPT_CODE,STATION_CODE,OPT_USER,OPT_DATE,OPT_TERM,BLD_TYPE,RH_FLG)"
			+ " VALUES('<BLOOD_NO>','<APPLY_NO>','<BLD_CODE>','<SUBCAT_CODE>','<BLOOD_VOL>',"
			+ " '<OUT_NO>',TO_DATE('<OUT_DATE>','YYYYMMDDHH24MISS'),'<OUT_USER>','<MR_NO>','<CASE_NO>','<IPD_NO>','2',"
			+ " '<DEPT_CODE>','<STATION_CODE>','<OPT_USER>',SYSDATE,'127.0.0.1','<BLD_TYPE>','<RH_FLG>')";
	/**
	 * 删除sql
	 */
	private static final String delsqL = " DELETE FROM BMS_BLOOD WHERE BLOOD_NO='<BLOOD_NO>'";


	/**
	 * 审核申请sql
	 */
	private static final String CheckApplysqL = " UPDATE  BMS_APPLYM SET CHECK_FLG='<CHECK_FLG>'  WHERE APPLY_NO='<APPLY_NO>'";

	/**
	 * 申请不良反应sql
	 */
	private static final String CheckReacsqL = " UPDATE  BMS_SPLREACT SET CHECK_FLG='<CHECK_FLG>'  WHERE REACT_NO='<REACT_NO>'";

	/**
	 * 得到实例
	 * 
	 * @return JDO
	 */
	public static BmsTool getInstance() {
		if (instanceObject == null)
			instanceObject = new BmsTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public BmsTool() {
		setModuleName("bms\\BMSApplyMModule.x");
		onInit();
	}

	/**
	 * 获取医师姓名
	 * 
	 * @return Map
	 */
	private String getDrDesc(String userId) throws Exception {
		String sql = "SELECT USER_ID,USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"
				+ userId + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		String userName = "";
		if (result.getCount() > 0) {
			userName = result.getValue("USER_NAME", 0);
		} else {
			userName = userId;
		}
		return userName;
	}

	/**
	 * 获取科室名称
	 * 
	 * @return Map
	 */
	private String getDeptDesc(String deptCode) throws Exception {
		String sql = "SELECT DEPT_CODE,DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
				+ deptCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		String deptDesc = "";
		if (result.getCount() > 0) {
			deptDesc = result.getValue("DEPT_CHN_DESC", 0);
		} else {
			deptDesc = deptCode;
		}
		return deptDesc;
	}

	/**
	 * 获取床位名称
	 * 
	 * @return String
	 */
	private String getBedDesc(String bedNo) throws Exception {
		String sql = "SELECT BED_NO,BED_NO_DESC FROM SYS_BED WHERE BED_NO='"
				+ bedNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		String bedDesc = "";
		if (result.getCount() > 0) {
			bedDesc = result.getValue("BED_NO_DESC", 0);
		} else {
			bedDesc = bedNo;
		}
		return bedDesc;
	}

	/**
	 * 获取诊断名称
	 * 
	 * @param diagCode
	 * @return
	 */
	private String getDiagDesc(String diagCode) throws Exception {
		String sql = "SELECT ICD_CODE,ICD_CHN_DESC FROM SYS_DIAGNOSIS WHERE ICD_CODE='"
				+ diagCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		String diagDesc = "";
		if (result.getCount() > 0) {
			diagDesc = result.getValue("ICD_CHN_DESC", 0);
		} else {
			diagDesc = diagCode;
		}
		return diagDesc;
	}

	/**
	 * 血品出库
	 * 
	 * @param parm
	 * @return
	 */
	@SuppressWarnings("finally")
	public TParm onBmsOut(TParm parm, String admType) throws Exception {
		TConnection conn = this.getConnection();
		TParm reParm = new TParm();
		try {
			TParm bloodParm = parm.getParm("bloodParm");// 血品
			if (bloodParm.getCount() <= 0) {
				return reParm;
			}
			String applyNo = parm.getValue("ApplyNo");
			TParm applyM = this.getBmsApplyM(applyNo, admType);
			if (applyM.getCount() <= 0) {
				applyM.setErr(-1, "未查询到申请单信息");
				return reParm;
			}
			for (int i = 0; i < bloodParm.getCount(); i++) {
				TParm parmRow = bloodParm.getRow(i);
				TParm inparm = new TParm();
				inparm.setData("APPLY_NO", applyNo);
				inparm.setData("BLOOD_NO", parmRow.getValue("BloodNo"));
				inparm.setData("BLD_CODE", parmRow.getValue("BloodCode"));
				inparm.setData("SUBCAT_CODE", parmRow.getValue("BloodSpe"));
				inparm.setData("BLOOD_VOL", parmRow.getValue("BloodQty"));
				inparm.setData("OUT_NO", parm.getValue("OutNo"));
				inparm.setData("OUT_DATE", parm.getValue("OutDate"));
				inparm.setData("OUT_USER", parm.getValue("UserId"));
				inparm.setData("MR_NO", parm.getValue("MrNo"));
				inparm.setData("CASE_NO", applyM.getValue("CASE_NO", 0));
				inparm.setData("IPD_NO", applyM.getValue("IPD_NO", 0));
				inparm.setData("DEPT_CODE", applyM.getValue("DEPT_CODE", 0));
				inparm.setData("STATION_CODE", applyM.getValue("STATION_CODE",
						0));
				inparm.setData("OPT_USER", parm.getValue("UserId"));
				inparm.setData("BLD_TYPE", parmRow.getValue("BloodType"));
				inparm.setData("RH_FLG", parmRow.getValue("RhType"));
				reParm = new TParm(TJDODBTool.getInstance().update(
						this.DelOutBloodSql(inparm), conn));
				if (reParm.getErrCode() < 0) {
					reParm.setErr(-1, "存储出库明细错误");
					conn.rollback();
					conn.close();
					return reParm;
				}
				reParm = new TParm(TJDODBTool.getInstance().update(
						this.getOutBloodSql(inparm), conn));
				if (reParm.getErrCode() < 0) {
					reParm.setErr(-1, "存储出库明细错误");
					conn.rollback();
					conn.close();
					return reParm;
				}
			}
			conn.commit();
			conn.close();
		} catch (Exception e) {
			System.out.println(e);
			reParm.setErr(-1, "出库操作异常");
		} finally {
			if (conn != null)
				conn.close();
			return reParm;
		}
	}

	/**
	 * 得到申请单信息
	 * 
	 * @return
	 */
	public TParm getBmsApplyData(String applyNo, String admType)
			throws Exception {
		TParm reParm = new TParm();
		TParm applyMParm = this.getBmsApplyM(applyNo, admType);
		if (applyMParm.getCount() <= 0) {
			reParm.setErr(-1, "未查询到申请单信息");
			return reParm;
		}
		reParm = applyMParm.getRow(0);
		// 医生名称
		String drDesc = "";
		if (!reParm.getValue("DR_CODE").equals("")) {
			drDesc = this.getDrDesc(reParm.getValue("DR_CODE"));
		}
		String deptDesc = "";
		if (!reParm.getValue("DEPT_CODE").equals("")) {
			deptDesc = this.getDeptDesc(reParm.getValue("DEPT_CODE"));
		}
		String diagDesc = "";
		if (!reParm.getValue("DIAG_CODE1").equals("")) {
			diagDesc = this.getDiagDesc(reParm.getValue("DIAG_CODE1"));
		}
		String bedDesc = "";
		if (!reParm.getValue("BED_NO").equals("")) {
			bedDesc = this.getBedDesc(reParm.getValue("BED_NO"));
		}
		reParm.setData("DR_DESC", drDesc);
		reParm.setData("DEPT_DESC", deptDesc);
		reParm.setData("DIAG_DESC1", diagDesc);
		reParm.setData("BED_DESC", bedDesc);
		TParm applyDParm = this.getBmsApplyD(applyNo);
		if (applyDParm.getCount() <= 0) {
			reParm.setErr(-1, "未查询到申请单明细");
			return reParm;
		}
		reParm.setData("DATA", applyDParm.getData());
		return reParm;
	}

	/**
	 * 申请单主表
	 * 
	 * @param applyNo
	 * @return
	 */
	public TParm getBmsApplyM(String applyNo, String admType) throws Exception {
		TParm applyMParm = new TParm();
		if (admType.equals("I")) {
			String Msql = " SELECT A.APPLY_NO,A.PRE_DATE,A.CASE_NO,A.MR_NO,B.PAT_NAME,C.CHN_DESC SEX, "
					+ " B.BIRTH_DATE,A.DEPT_CODE,A.DR_CODE,A.DIAG_CODE1,A.USE_DATE,A.TEST_BLD,D.IN_DATE,"
					+ " B.IPD_NO,D.STATION_CODE,A.BED_NO,B.BLOOD_RH_TYPE"
					+ " FROM BMS_APPLYM A,SYS_PATINFO B,SYS_DICTIONARY C,ADM_INP D "
					+ " WHERE A.MR_NO=B.MR_NO AND C.GROUP_ID='SYS_SEX' AND B.SEX_CODE=C.ID(+) AND "
					+ " A.CASE_NO=D.CASE_NO  AND A.APPLY_NO='" + applyNo + "'";
			applyMParm = new TParm(TJDODBTool.getInstance().select(Msql));
			if (applyMParm.getCount() <= 0) {
				return applyMParm;
			}
		} else {
			String Msql = " SELECT A.APPLY_NO,A.PRE_DATE,A.CASE_NO,A.MR_NO,B.PAT_NAME,C.CHN_DESC SEX, "
					+ " B.BIRTH_DATE,A.DEPT_CODE,A.DR_CODE,A.DIAG_CODE1,A.USE_DATE,A.TEST_BLD,D.ADM_DATE IN_DATE,"
					+ " B.IPD_NO,'' STATION_CODE,'' BED_NO,B.BLOOD_RH_TYPE"
					+ " FROM BMS_APPLYM A,SYS_PATINFO B,SYS_DICTIONARY C,REG_PATADM D "
					+ " WHERE A.MR_NO=B.MR_NO AND C.GROUP_ID='SYS_SEX' AND B.SEX_CODE=C.ID(+) AND "
					+ " A.CASE_NO=D.CASE_NO AND A.APPLY_NO='" + applyNo + "'";
			applyMParm = new TParm(TJDODBTool.getInstance().select(Msql));
			if (applyMParm.getCount() <= 0) {
				return applyMParm;
			}
		}
		return applyMParm;
	}

	/**
	 * 申请单明细
	 * 
	 * @param applyNo
	 * @return
	 */
	public TParm getBmsApplyD(String applyNo) throws Exception {
		String Dsql = " SELECT A.APPLY_NO,A.BLD_CODE,B.BLDCODE_DESC,A.UNIT_CODE,C.UNIT_CHN_DESC,A.APPLY_QTY," +
				" A.APPLY_BLD,A.APPLY_RH_TYPE "
				+ " FROM BMS_APPLYD A,BMS_BLDCODE B,SYS_UNIT C"
				+ " WHERE A.BLD_CODE=B.BLD_CODE(+) AND A.UNIT_CODE=C.UNIT_CODE(+) AND "
				+ " A.APPLY_NO='" + applyNo + "'";
		TParm applyDParm = new TParm(TJDODBTool.getInstance().select(Dsql));
		if (applyDParm.getCount() <= 0) {
			return applyDParm;
		}
		return applyDParm;
	}

	/**
	 * 输血反应信息
	 * 
	 * @param applyNo
	 * @return
	 */
	public TParm getBmsReacData(String reactNo) throws Exception {
		TParm reParm = new TParm();
		String sql = " SELECT A.REACT_NO,A.DEPT_CODE,A.STATION_CODE,A.MR_NO,A.CASE_NO,B.PAT_NAME,"
				+ " C.CHN_DESC SEX,B.BIRTH_DATE,B.IDNO,B.BLOOD_TYPE,B.BLOOD_RH_TYPE,A.START_DATE,"
				+ " A.END_DATE,A.BLD_CODE,DECODE(A.REACTION_CODE,'01','发热','02','过敏','03','溶血','04','其他','') REACTION_CODE,"
				+ "  DECODE(A.REACT_CLASS,'0','无','1','轻','2','重','') REACT_CLASS,A.REACT_OTH,"
				+ " DECODE(A.REACT_HIS,'0','无','1','有','') REACT_HIS,A.TREAT,A.OPT_USER,A.RECAT_SYMPTOM,A.IPD_NO,A.START_DATE IN_DATE "
				+ " FROM BMS_SPLREACT A,SYS_PATINFO B,SYS_DICTIONARY C"
				+ " WHERE A.MR_NO=B.MR_NO AND C.GROUP_ID='SYS_SEX' AND B.SEX_CODE=C.ID(+) AND "
				+ " A.REACT_NO='" + reactNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() <= 0) {
			reParm.setErr(-1, "未查询到反应单信息");
			return reParm;
		}
		reParm = parm.getRow(0);
		return reParm;
	}

	/**
	 * 审核申请
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onCheckApply(TParm parm) throws Exception {
		String sql = this.onUpdateCheckApplySql(parm);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() <= 0) {
			err("BmsTool->onCheckApply" + result.getErrName()
					+ result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 审核不良反应
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onCheckReac(TParm parm) throws Exception {
		String sql = this.onUpdateCheckReacSql(parm);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() <= 0) {
			err("BmsTool->onCheckReac" + result.getErrName()
					+ result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 出库明细
	 * 
	 * @return
	 */
	public String getOutBloodSql(TParm parm) throws Exception {
		String sql = this.buildSQL(sqL, parm);
		return sql;
	}

	/**
	 * 删除出库明细sql
	 * 
	 * @return
	 */
	public String DelOutBloodSql(TParm parm) throws Exception {
		String sql = this.buildSQL(delsqL, parm);
		return sql;
	}

	/**
	 * 更新病患血型
	 * 
	 * @param parm
	 * @return
	 */
	public String onUpdatePatSql(TParm parm) throws Exception {
		StringBuffer sql=new StringBuffer();
		sql.append("UPDATE SYS_PATINFO SET ");
		if(parm.existData("BLOOD_TYPE"))
			sql.append(" BLOOD_TYPE='<BLOOD_TYPE>',");
		if(parm.existData("BLOOD_RH_TYPE"))
			sql.append(" BLOOD_RH_TYPE='<BLOOD_RH_TYPE>',");
		sql.append(" OPT_USER='BMS', OPT_DATE=SYSDATE,OPT_TERM='127.0.0.1',TESTBLD_DR='<TESTBLD_DR>'");
		sql.append(" WHERE MR_NO='<MR_NO>'");
		String sqlline = this.buildSQL(sql.toString(), parm);
		return sqlline;
	}

	/**
	 * 审核申请sql
	 * 
	 * @param parm
	 * @return
	 */
	public String onUpdateCheckApplySql(TParm parm) throws Exception {
		String sql = this.buildSQL(CheckApplysqL, parm);
		return sql;
	}

	/**
	 * 审核反应
	 * 
	 * @param parm
	 * @return
	 */
	public String onUpdateCheckReacSql(TParm parm) throws Exception {
		String sql = this.buildSQL(CheckReacsqL, parm);
		return sql;
	}

	/**
	 * 删除出库明细 TParm parm
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onDelOutBlood(TParm parm) throws Exception {
		TParm reParm = new TParm();
		TConnection conn = this.getConnection();
		TParm bloodParm = parm.getParm("bloodParm");// 血品
		if (bloodParm.getCount() <= 0) {
			return reParm;
		}
		for (int i = 0; i < bloodParm.getCount(); i++) {
			TParm parmRow = bloodParm.getRow(i);
			TParm inparm = new TParm();
			inparm.setData("BLOOD_NO", parmRow.getValue("BloodNo"));
			reParm = new TParm(TJDODBTool.getInstance().update(
					this.DelOutBloodSql(inparm), conn));
			if (reParm.getErrCode() < 0) {
				reParm.setErr(-1, "存储出库明细错误");
				conn.rollback();
				conn.close();
				return reParm;
			}

		}
		conn.commit();
		conn.close();
		return reParm;

	}

	/**
	 * 初始化编码对照参数
	 * 
	 * @return
	 */
	public Map<String, List<String>> initBmsMap() throws Exception {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> rhList = new ArrayList<String>();
		List<String> aobList = new ArrayList<String>();

		String sql = "SELECT MAP_TYPE,LIS_ID FROM MED_LIS_MAP "
				+ " WHERE MAP_TYPE='" + rhMapType + "' OR MAP_TYPE='"
				+ AOBMapType + "'";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() <= 0) {
			return map;
		}
		for (int i = 0; i < result.getCount(); i++) {
			TParm pamrRow = result.getRow(i);
			// 对照 RH
			if (pamrRow.getValue("MAP_TYPE").equals(rhMapType)) {
				rhList.add(pamrRow.getValue("LIS_ID"));
			}
			// 对照 AOB
			if (pamrRow.getValue("MAP_TYPE").equals(AOBMapType)) {
				aobList.add(pamrRow.getValue("LIS_ID"));
			}
		}
		map.put("rhList", rhList);
		map.put("aobList", aobList);
		return map;
	}

	/**
	 * 初始化AOB血型名称与值对照参数
	 * 
	 * @return
	 */
	public Map<String, String> initBldMap() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String sql = "SELECT ID,STA3_CODE FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_BLOOD'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() <= 0) {
			return map;
		}
		for (int i = 0; i < result.getCount(); i++) {
			TParm pamrRow = result.getRow(i);
			if (!pamrRow.getValue("STA3_CODE").equals(""))
				map.put(pamrRow.getValue("STA3_CODE"), pamrRow.getValue("ID"));
		}
		return map;
	}

	/**
	 * 初始化RH血型名称与值对照参数
	 * 
	 * @return
	 */
	public Map<String, String> initRhMap() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String sql = "SELECT ID,STA3_CODE FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RH'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() <= 0) {
			return map;
		}
		for (int i = 0; i < result.getCount(); i++) {
			TParm pamrRow = result.getRow(i);
			if (!pamrRow.getValue("STA3_CODE").equals(""))
				map.put(pamrRow.getValue("STA3_CODE"), pamrRow.getValue("ID"));
		}
		return map;
	}

	/**
	 * 根据LIS回传结果更新病患血型信息
	 * 
	 * @param labNumber
	 *            条码号
	 * @return
	 */
	public TParm onMedExeBld(String labNumber, String mrNo,String TestUser) throws Exception {
		TParm result = new TParm();
		Map<String, List<String>> Bmsmap = this.initBmsMap();

		List<String> rhKeyList = Bmsmap.get("rhList");

		List<String> aobKeyList = Bmsmap.get("aobList");

		Map<String, String> Bldmap = this.initBldMap();
		if (Bldmap.size() <= 0) {
			return result;
		}
		Map<String, String> Rhmap = this.initRhMap();
		if (Rhmap.size() <= 0) {
			return result;
		}
		String medrptsql = " SELECT TESTITEM_CODE,TESTITEM_CHN_DESC,TEST_VALUE,TEST_UNIT "
				+ " FROM MED_LIS_RPT WHERE CAT1_TYPE='LIS' AND APPLICATION_NO='"
				+ labNumber + "'";
		TParm medrptParm = new TParm(TJDODBTool.getInstance().select(medrptsql));
		if (medrptParm.getCount() <= 0) {
			return result;
		}
		for (int i = 0; i < medrptParm.getCount(); i++) {
			TParm parmRow = medrptParm.getRow(i);
			if (aobKeyList.size() > 0
					&& aobKeyList.contains(parmRow.getValue("TESTITEM_CODE"))) {
				result.setData("BLOOD_TYPE", Bldmap.get(parmRow
						.getValue("TEST_VALUE")));
			}
			if (rhKeyList.size() > 0
					&& rhKeyList.contains(parmRow.getValue("TESTITEM_CODE"))) {
				result.setData("BLOOD_RH_TYPE", Rhmap.get(parmRow
						.getValue("TEST_VALUE")));
			}
		}
		//增加逻辑判断存在血型和阴阳性属性都为空
		if(!result.existData("BLOOD_TYPE")&&!result.existData("BLOOD_RH_TYPE")){
			return result;
		}
		result.setData("MR_NO", mrNo);
		result.setData("TESTBLD_DR", TestUser);
		result = new TParm(TJDODBTool.getInstance().update(
				onUpdatePatSql(result)));
		return result;
	}

	/**
	 * 建造SQL语句
	 * 
	 * @param SQL
	 *            原始语句
	 * @param obj
	 *            替换语句中?的数值数组
	 * @return SQL语句
	 */
	public String buildSQL(String SQL, TParm parm) {
		SQL = SQL.trim();
		Object[] names = parm.getNames();
		for (int i = 0; i < names.length; i++) {
			String name = (String) names[i];
			if (SQL.indexOf("<" + name.trim() + ">") == -1)
				continue;
			SQL = replace(SQL, "<" + name.trim() + ">", parm
					.getValue((String) names[i]));
		}
		return SQL;
	}

	public String replace(String s, String name, String value) {
		int index = s.indexOf(name);
		while (index >= 0) {
			s = s.substring(0, index) + value
					+ s.substring(index + name.length(), s.length());
			index = s.indexOf(name);
		}
		return s;
	}
	
	public static void main(String[] args) {
			
	}
}
