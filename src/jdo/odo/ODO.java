package jdo.odo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jdo.device.CallNo;
import jdo.opd.OrderTool;
import jdo.reg.REGTool;
import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;

/**
 * 
 * <p>
 * Title: 门诊医生站对象
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
 * Company: JavaHis
 * </p>
 * 
 * @author lzk 2009.2.11
 * @version 1.0
 */
public class ODO {
	/**
	 * 病案号
	 */
	private String mrNo;
	/**
	 * 就诊号
	 */
	private String caseNo;
	/**
	 * 门急住别
	 */
	private String admType;
	/**
	 * 医师
	 */
	private String drCode;
	/**
	 * 诊断类型
	 */
	private String icdType;
	/**
	 * 部门
	 */
	private String deptCode;
	/**
	 * 主诉客述
	 */
	private Subjrec subjrec;
	/**
	 * 诊断
	 */
	private Diagrec diagrec;
	/**
	 * 过敏史
	 */
	private DrugAllergy drugAllergy;
	/**
	 * 医嘱
	 */
	private OpdOrder opdOrder;
	/**
	 * 既往史
	 */
	private MedHistory medHistory;
	/**
	 * 挂号
	 */
	private RegPatAdm regPatAdm;
	/**
	 * 病患
	 */
	private PatInfo patInfo;
	/**
	 * 医嘱历史
	 */
	private OpdOrderHistory opdOrderHistory;

	/**
	 * 错误信息
	 */
	private String errText = "";
	/**
	 * 数据库工具
	 */
	private TJDODBTool dbTool = new TJDODBTool();
	/**
	 * 医疗卡交易号
	 */
	private String TREDE_NO = "";
	/**
	 * 存放预开检查处方签号急就诊号 yanjing 20131217
	 */
	private Map<String, String> preCaseNos;

	/**
	 * 构造器
	 */
	public ODO() {
		setOdo();
	}

	/**
	 * 
	 * @param caseNo
	 * @param mrNo
	 * @param deptCode
	 * @param drCode
	 * @param admType
	 */
	public ODO(String caseNo, String mrNo, String deptCode, String drCode,
			String admType) {
		setOdo();
		setCaseNo(caseNo);
		setMrNo(mrNo);
		setDeptCode(deptCode);
		setDrCode(drCode);
		setAdmType(admType);
	}

	public void setOdo() {
		// 初始化主诉客述
		setSubjrec(new Subjrec());
		// 初始化诊断
		setDiagrec(new Diagrec());
		// 过敏史
		setDrugAllergy(new DrugAllergy());
		// 医嘱
		setOpdOrder(new OpdOrder());
		// 既往史
		setMedHistory(new MedHistory());
		// 挂号
		setRegPatAdm(new RegPatAdm());
		// 病患
		setPatInfo(new PatInfo());
		// 医嘱历史
		setOpdOrderHistory(new OpdOrderHistory());
	}

	/**
	 * 设置门急住别
	 * 
	 * @param admType
	 *            String
	 */
	public void setAdmType(String admType) {
		this.admType = admType;
		getSubjrec().setAdmType(admType);
		getDiagrec().setAdmType(admType);
		getDrugAllergy().setAdmType(admType);
		getOpdOrder().setAdmType(admType);
		// getOpdOrderHistory().setAdmType(admType);
		getMedHistory().setAdmType(admType);
	}

	/**
	 * 得到门急住别
	 * 
	 * @return String
	 */
	public String getAdmType() {
		return admType;
	}

	/**
	 * 得到诊断类型
	 * 
	 * @return String
	 */
	public String getIcdType() {
		return icdType;
	}

	public void setIcdType(String icdType) {
		this.icdType = icdType;
		getDiagrec().setIcdType(icdType);
	}

	/**
	 * 设置医师
	 * 
	 * @param drCode
	 *            String
	 */
	public void setDrCode(String drCode) {
		this.drCode = drCode;
		getSubjrec().setDrCode(drCode);
		getDiagrec().setDrCode(drCode);
		getDrugAllergy().setDrCode(drCode);
		getOpdOrder().setDrCode(drCode);
		// getOpdOrderHistory().setDrCode(drCode);
		getMedHistory().setDrCode(drCode);
	}

	/**
	 * 得到医师
	 * 
	 * @return String
	 */
	public String getDrCode() {
		return drCode;
	}

	/**
	 * 设置病案号
	 * 
	 * @return String
	 */
	public String getMrNo() {
		return this.mrNo;
	}

	/**
	 * 得到病案号
	 * 
	 * @param mrNo
	 *            String
	 */
	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
		getSubjrec().setMrNo(mrNo);
		getDrugAllergy().setMrNo(mrNo);
		getOpdOrder().setMrNo(mrNo);
		// getOpdOrderHistory().setMrNo(mrNo);
		getMedHistory().setMrNo(mrNo);
		getPatInfo().setMrNo(mrNo);
	}

	/**
	 * 得到就诊号
	 * 
	 * @return String
	 */
	public String getCaseNo() {
		return this.caseNo;
	}

	/**
	 * 设置就诊号
	 * 
	 * @param caseNo
	 *            String
	 */
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
		getSubjrec().setCaseNo(caseNo);
		getDiagrec().setCaseNo(caseNo);
		getDrugAllergy().setCaseNo(caseNo);
		getOpdOrder().setCaseNo(caseNo);
		// getOpdOrderHistory().setCaseNo(caseNo);
		getMedHistory().setCaseNo(caseNo);
		getRegPatAdm().setCaseNo(caseNo);
	}

	/**
	 * 设置部门
	 * 
	 * @param deptCode
	 *            String
	 */
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
		getDrugAllergy().setDeptCode(deptCode);
		getOpdOrder().setDeptCode(deptCode);
		// getOpdOrderHistory().setDeptCode(deptCode);
		getMedHistory().setDeptCode(deptCode);
	}

	/**
	 * 得到部门
	 * 
	 * @return String
	 */
	public String getDeptCode() {
		return deptCode;
	}

	/**
	 * 设置主诉客述
	 * 
	 * @param subjrec
	 *            Subjrec
	 */
	public void setSubjrec(Subjrec subjrec) {
		this.subjrec = subjrec;
	}

	/**
	 * 得到主诉客述
	 * 
	 * @return Subjrec
	 */
	public Subjrec getSubjrec() {
		return subjrec;
	}

	/**
	 * 设置诊断对象
	 * 
	 * @param diagrec
	 *            Diagrec
	 */
	public void setDiagrec(Diagrec diagrec) {
		this.diagrec = diagrec;
	}

	/**
	 * 得到诊断对象
	 * 
	 * @return Diagrec
	 */
	public Diagrec getDiagrec() {
		return diagrec;
	}

	/**
	 * 设置过敏史对象
	 * 
	 * @param drugAllergy
	 *            DrugAllergy
	 */
	public void setDrugAllergy(DrugAllergy drugAllergy) {
		this.drugAllergy = drugAllergy;
	}

	/**
	 * 得到过敏史对象
	 * 
	 * @return DrugAllergy
	 */
	public DrugAllergy getDrugAllergy() {
		return drugAllergy;
	}

	/**
	 * 设置医嘱对象
	 * 
	 * @param opdOrder
	 *            OpdOrder
	 */
	public void setOpdOrder(OpdOrder opdOrder) {
		this.opdOrder = opdOrder;
	}

	/**
	 * 得到医嘱对象
	 * 
	 * @return OpdOrder
	 */
	public OpdOrder getOpdOrder() {
		return opdOrder;
	}

	/**
	 * 设置既往史对象
	 * 
	 * @param medHistory
	 *            MedHistory
	 */
	public void setMedHistory(MedHistory medHistory) {
		this.medHistory = medHistory;
	}

	/**
	 * 得到既往史对象
	 * 
	 * @return MedHistory
	 */
	public MedHistory getMedHistory() {
		return medHistory;
	}

	/**
	 * 设置挂号对象
	 * 
	 * @param regPatAdm
	 *            RegPatAdm
	 */
	public void setRegPatAdm(RegPatAdm regPatAdm) {
		this.regPatAdm = regPatAdm;
	}

	/**
	 * 得到挂号对象
	 * 
	 * @return RegPatAdm
	 */
	public RegPatAdm getRegPatAdm() {
		return this.regPatAdm;
	}

	/**
	 * 设置病患对象
	 * 
	 * @param patInfo
	 *            PatInfo
	 */
	public void setPatInfo(PatInfo patInfo) {
		this.patInfo = patInfo;
	}

	/**
	 * 得到病患对象
	 * 
	 * @return PatInfo
	 */
	public PatInfo getPatInfo() {
		return this.patInfo;
	}

	/**
	 * 设置医嘱历史对象
	 * 
	 * @param opdOrderHistory
	 *            OpdOrderHistory
	 */
	public void setOpdOrderHistory(OpdOrderHistory opdOrderHistory) {
		this.opdOrderHistory = opdOrderHistory;
	}

	/**
	 * 得到医嘱历史对象
	 * 
	 * @return OpdOrderHistory
	 */
	public OpdOrderHistory getOpdOrderHistory() {
		return this.opdOrderHistory;
	}

	/**
	 * 得到数据库工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return dbTool;
	}

	/**
	 * 设置错误信息
	 * 
	 * @param errText
	 *            String
	 */
	public void setErrText(String errText) {
		this.errText = errText;
	}

	/**
	 * 得到错误信息
	 * 
	 * @return String
	 */
	public String getErrText() {
		return errText;
	}

	/**
	 * 设置医疗卡交易号
	 * 
	 * @param TredeNo
	 *            String
	 */
	public void setTredeNo(String TredeNo) {
		this.TREDE_NO = TredeNo;
	}

	/**
	 * 得到医疗卡交易号
	 */
	public String getTredeNo() {
		return TREDE_NO;
	}

	/**
	 * 查询
	 * 
	 * @return boolean
	 */
	public boolean onQuery() {
		// 重新设置错误信息
		setErrText("");

		// 检核CASE_NO
		if (getCaseNo() == null || getCaseNo().length() <= 0) {
			setErrText("CaseNo为空！");
			return false;
		}
		// 查询主诉客诉数据
		if (!getSubjrec().onQuery()) {
			setErrText(getSubjrec().getErrText());
			return false;
		}
		if (getSubjrec().rowCount() < 1)
			getSubjrec().insertRow();
		// 查询诊断数据
		if (!getDiagrec().onQuery()) {
			setErrText(getDiagrec().getErrText());
			return false;
		}
		getDiagrec().insertRow();
		// 查询过敏史数据
		if (!getDrugAllergy().onQuery()) {
			setErrText(getDrugAllergy().getErrText());
			return false;
		}
		int row = getDrugAllergy().insertRow();
		getDrugAllergy().setItem(row, "DRUG_TYPE", "A");
		row = getDrugAllergy().insertRow();
		getDrugAllergy().setItem(row, "DRUG_TYPE", "B");
		row = getDrugAllergy().insertRow();
		getDrugAllergy().setItem(row, "DRUG_TYPE", "C");
		// 查询医嘱数据
		if (!getOpdOrder().onQuery()) {
			setErrText(getOpdOrder().getErrText());
			return false;
		}
		// 查询既往史数据
		if (!getMedHistory().onQuery()) {
			setErrText(getMedHistory().getErrText());
			return false;
		}
		getMedHistory().insertRow();
		// 查询挂号数据
		if (!getRegPatAdm().onQuery()) {
			setErrText(getRegPatAdm().getErrText());
			return false;
		}

		// 查询病患数据
		if (!getPatInfo().onQuery()) {
			setErrText(getPatInfo().getErrText());
			return false;
		}
		getOpdOrder().getMedApply();// 初始化med_apply
		getOpdOrder().getLabMap();
		// //查询病患数据
		// if(!getPatInfo().onQuery()){
		// setErrText(getPatInfo().getErrText());
		// return false;
		// }
		return true;
	}

	/**
	 * 保存检测
	 * 
	 * @return boolean
	 */
	public boolean checkSave() {
		// 检测主诉客诉
		if (!getSubjrec().checkSave()) {
			setErrText(getSubjrec().getErrText());
			return false;
		}
		// 检测诊断
		if (!getDiagrec().checkSave()) {
			setErrText(getDiagrec().getErrText());
			return false;
		}
		// 检测过敏史
		if (!getDrugAllergy().checkSave()) {
			setErrText(getDrugAllergy().getErrText());
			return false;
		}

		// 检测医嘱
		if (!getOpdOrder().checkSave()) {
			setErrText(getOpdOrder().getErrText());
			return false;
		}

		// 检测既往史
		if (!getMedHistory().checkSave()) {
			setErrText(getMedHistory().getErrText());
			return false;
		}
		// 检测挂号
		if (!getRegPatAdm().checkSave()) {
			setErrText(getRegPatAdm().getErrText());
			return false;
		}
		// 检测病患
		if (!getPatInfo().checkSave()) {
			setErrText(getPatInfo().getErrText());
			return false;
		}
		Timestamp optDate = getDBTool().getDBTime();
		// 设置主诉客诉修改行的用户信息
		getSubjrec().setOperator(Operator.getID(), optDate, Operator.getIP());
		// 设置诊断修改行的用户信息
		getDiagrec().setOperator(Operator.getID(), optDate, Operator.getIP());
		// 设置过敏史修改行的用户信息
		getDrugAllergy().setOperator(Operator.getID(), optDate,
				Operator.getIP());
		// 设置医嘱修改行的用户信息
		getOpdOrder().setOperator(Operator.getID(), optDate, Operator.getIP());
		// 设置既往史修改行的用户信息
		getMedHistory()
				.setOperator(Operator.getID(), optDate, Operator.getIP());
		return true;
	}

	/**
	 * 保存医嘱
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public boolean onSave() throws Exception {
		//判断该病人是否为退挂人员  true 退挂 add by huangtt 20150215
		if(REGTool.getInstance().queryUnReg(caseNo)){
			return false;
		}
		
		
		// 保存检测
		if (!checkSave())
			return false;

		// 保存主诉客诉
		String sql[] = getSubjrec().getUpdateSQL();
		// 检核SQL是否为空
		if (sql == null) {
			// System.out.println("subjrec is wrong");
			setErrText(getSubjrec().getErrText());
			return false;
		}

		// 保存诊断
		String sqlTemp[] = getDiagrec().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("diag is wrong");
			setErrText(getDiagrec().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);
		// getDiagrec().showDebug();
		// 保存过敏史
		sqlTemp = getDrugAllergy().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("allergy is wrong");
			setErrText(getDrugAllergy().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);

		boolean isChanged = false;

		String lastFilter = opdOrder.getFilter();
		opdOrder.setFilter("");
		opdOrder.filter();

		TParm modifyParm = opdOrder.getBuffer(OpdOrder.MODIFY);
		TParm deleteParm = opdOrder.getBuffer(OpdOrder.DELETE);

		TParm mParm = this.makeParmClean(modifyParm);
		TParm dParm = this.makeParmClean(deleteParm);

		String bSql = " SELECT RX_NO,SEQ_NO,ORDER_CODE,MEDI_QTY,MEDI_UNIT,DOSAGE_QTY,DOSAGE_UNIT,DISPENSE_QTY,AR_AMT,BILL_FLG,PRINT_FLG,BILL_TYPE,EXEC_FLG,PHA_CHECK_CODE,PHA_DOSAGE_CODE,PHA_DISPENSE_CODE,REQUEST_FLG,REQUEST_NO "
				+ " FROM OPD_ORDER "
				+ " WHERE "
				+ " CASE_NO ='"
				+ caseNo
				+ "' ";
		TParm bParm = new TParm(TJDODBTool.getInstance().select(bSql));

		List<String> mList = getKeyWordListHasChanged(mParm, bParm);
		List<String> dList = getKeyWordListHasChanged(dParm, bParm);

		// 保存医嘱
		sqlTemp = getOpdOrder().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("order is wrong");
			// getOpdOrder().showDebug();
			setErrText(getOpdOrder().getErrText());
			return false;
		}

		List<String> sqlList = new ArrayList<String>();
		List<String> mTempList = new ArrayList<String>();
		List<String> dTempList = new ArrayList<String>();
		String update = "UPDATE OPD_ORDER SET";
		String delete = "DELETE FROM OPD_ORDER";
		String insert = "INSERT INTO OPD_ORDER";

		for (String ts : sqlTemp) {
			if (ts.contains(update)) {
				mTempList.add(ts);
			}
			if (ts.contains(delete)) {
				dTempList.add(ts);
			}
			if (ts.contains(insert)) {
				sqlList.add(ts);
			}
		}
		
		this.sqlFilter(mTempList, mList, sqlList);
		this.sqlFilter(dTempList, dList, sqlList);

		String[] sqls = new String[sqlList.size()];

		List<String> delList = new ArrayList<String>();
		for (int i = 0; i < sqlList.size(); i++) {
			sqls[i] = sqlList.get(i);
			
			if(sqlList.get(i).contains(delete)){
				delList.add(sqlList.get(i));
			}
		}
		
		if(delList.size() > 0){
			List<String> delDList = this.dealDel(delList);
			if(delDList.size() > 0){
				String [] delSql = new String[delDList.size()];
				for (int i = 0; i < delDList.size(); i++) {
					delSql[i] = delDList.get(i);
					System.out.println("delDList===="+delDList.get(i));
				}
				sql = StringTool.copyArray(sql, delSql);
			}
			
		}
		

		if (sqls.length != sqlTemp.length) {
			isChanged = true;
		}

	
		opdOrder.setFilter(lastFilter);
		opdOrder.filter();

		sql = StringTool.copyArray(sql, sqls);


		// ----判断预开检查 add caoyong 20140103 start-----
		String medFlg = "";
		String sqlQ = "SELECT MED_FLG FROM OPD_SYSPARM";
		TParm sresult = new TParm(TJDODBTool.getInstance().select(sqlQ));
		if (sresult.getCount() > 0) {
			medFlg = sresult.getValue("MED_FLG", 0);
		}
		
		// ----判断预开检查 add caoyong 20140103 end-----
		if (getOpdOrder().getMedApply().isModified()) {
			// 保存MED_APPLY
			// ============pangben 2013-01-31 删除Med_Apply表操作更改成修改BILL_FLG =N字段
			// 去掉删除的sql 只保存 添加的sql语句
			List list = new ArrayList();
			sqlTemp = getOpdOrder().getMedApply().getUpdateSQL();
			for (int i = 0; i < sqlTemp.length; i++) {
				// ----判断预开检查 add caoyong 20140103 start-----
				String line = sqlTemp[i];
				if ("Y".equalsIgnoreCase(medFlg)) {
					if (sqlTemp[i].contains("DELETE"))
						continue;
				}
				// ----判断预开检查 add caoyong 20140103 end-----
				list.add(sqlTemp[i]);
			}
			String[] insertSqlTemp = null;
			if (list.toArray().length > 0) {
				insertSqlTemp = new String[list.toArray().length];
				Iterator iterator = list.iterator();
				int i = 0;
				while (iterator.hasNext()) {
					insertSqlTemp[i] = iterator.next().toString();
					i++;
				}
			}
			if (insertSqlTemp == null) {
			} else {
				sql = StringTool.copyArray(sql, insertSqlTemp);
			}
		}
		
		// 如果有删除的医嘱，则需要插入历史档
		int count = getOpdOrder().getDeleteCount();
		//如果医嘱没有发生变化，删除医嘱将插入历史档 
		if(!isChanged){
			if (count > -1) {
				getOpdOrderHistory().onQuery();
				getOpdOrderHistory().insert(
						getOpdOrder().getBuffer(getOpdOrder().DELETE));
				sqlTemp = getOpdOrderHistory().getUpdateSQL();
				if (sqlTemp == null) {
					// System.out.println("orderHistory is wrong");
					// getOpdOrderHistory().showDebug();
					setErrText(getOpdOrderHistory().getErrText());
					return false;
				}
				sql = StringTool.copyArray(sql, sqlTemp);
			}
		}
		

		// 保存既往史
		sqlTemp = getMedHistory().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("medhis is wrong");
			setErrText(getMedHistory().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);

		// 保存挂号
		sqlTemp = getRegPatAdm().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("regpatadm is wrong");
			setErrText(getRegPatAdm().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);
		// 保存病患
		sqlTemp = getPatInfo().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("patinfo is wrong");
			setErrText(getPatInfo().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);
		// ======pangben 2013-11-7爱育华锁库存查询医嘱功能
		TParm oldOrderParm = new TParm();
		TParm newOrderParm = new TParm();
		if (Operator.getLockFlg().equals("Y")) {// 操作之前数据
			TParm parm = new TParm();
			parm.setData("CASE_NO", this.getCaseNo());
			parm.setData("CAT1_TYPE", "PHA");
			oldOrderParm = OrderTool.getInstance().selectLockQtyCheckSumQty(
					parm);
			if (oldOrderParm.getErrCode() < 0) {
				setErrText(oldOrderParm.getErrText());
				return false;
			}
		}

		TParm opdOrderParm = new TParm();
		if (Operator.getLockFlg().equals("Y")) {
			TParm orderParm = getOpdOrder().getBuffer(getOpdOrder().DELETE);
			String rxNo = orderParm.getValue("RX_NO", 0);
			TParm searchParm = new TParm();
			searchParm.setData("RX_NO", rxNo);
			searchParm.setData("CASE_NO", this.getCaseNo());
			searchParm.setData("CAT1_TYPE", "PHA");

			// 查询数据
			opdOrderParm = OrderTool.getInstance().selectOpdOrder(searchParm);

		}

		TParm inParm = new TParm();
		inParm.setData("SQL", sql);
		inParm.setData("TREDE_NO", this.TREDE_NO);
		inParm.setData("CASE_NO", this.getCaseNo());
		TParm saveResult = TIOM_AppServer.executeAction("action.opd.ODOAction",
				"onSave", inParm);
		if (saveResult.getErrCode() != 0) {
			setErrText(saveResult.getErrText());
			return false;
		}
		if (Operator.getLockFlg().equals("Y")) {// ======pangben 2013-11-7 锁库存管理
												// 操作之后数据查询

			TParm parm = new TParm();
			parm.setData("CASE_NO", this.getCaseNo());
			parm.setData("CAT1_TYPE", "PHA");
			// 删除，判断删除的
			if (count > -1) {

				if (opdOrderParm.getCount("ORDER_CODE") <= 0) {

					newOrderParm = OrderTool.getInstance()
							.selectLockQtyCheckSumQty(parm);
					parm.setData("oldOrderParm", oldOrderParm.getData());
					parm.setData("newOrderParm", newOrderParm.getData());
					TParm result = TIOM_AppServer.executeAction(
							"action.opd.ODOAction", "onSaveLockQty", parm);
				}

			} else {

				newOrderParm = OrderTool.getInstance()
						.selectLockQtyCheckSumQty(parm);
				parm.setData("oldOrderParm", oldOrderParm.getData());
				parm.setData("newOrderParm", newOrderParm.getData());
				TParm result = TIOM_AppServer.executeAction(
						"action.opd.ODOAction", "onSaveLockQty", parm);
			}
		}
		// 清除修改存储
		getSubjrec().resetModify();
		getDiagrec().resetModify();
		getDrugAllergy().resetModify();
		getOpdOrder().resetModify();
		getOpdOrder().resetMap();
		getMedHistory().resetModify();
		getRegPatAdm().resetModify();
		getPatInfo().resetModify();
		getOpdOrder().resetMedApply();
		// System.out.println();
		CallNo callNoUtil = new CallNo();
		if (callNoUtil.init()) {
			/**
			 * /** 挂号同步
			 * 
			 * @param VISIT_DATE
			 *            String 挂号日期 NOT NULL
			 * @param VISIT_NO
			 *            String 挂号号 NOT NULL CASENO
			 * @param CLINIC_LABEL
			 *            String 专家 普通等
			 * @param SERIAL_NO
			 *            String 小号
			 * @param PATIENT_ID
			 *            String 病人卡号
			 * @param PNAME
			 *            String 病人姓名
			 * @param PSEX
			 *            String 性别
			 * @param PAGE
			 *            String 年龄
			 * @param IDENTITY
			 *            String 身份
			 * @param DEPTID
			 *            String 科室ID
			 * @param REGISTERING_DATE
			 *            String 挂号时间（2010-2-8 11:11:11）
			 * @param DOCTOR
			 *            String 医生编号 普通不分为空
			 * @param CTYPE
			 *            String 0 挂号 1 退号 2就诊完成
			 * @param OPTYPE
			 *            String 操作类型 0 清空 1 删除 2 插入或者更新 3 退号
			 * 
			 */
			callNoUtil.SyncClinicMaster("", getCaseNo(), "", "", "", "", "",
					"", "", "", "", "", "2", "", "");
		}
		if (isChanged) {
			throw new Exception("您修改或删除的医嘱已被他人修改\r\n您新增的医嘱已保存\r\n请重新修改医嘱");
		}
		return true;
	}

	/**
	 * 判断odo对象是否修改过
	 * 
	 * @return boolean true:修改过,false:未修改
	 */
	public boolean isModified() {
		return getSubjrec().isModified() || getDiagrec().isModified()
				|| getDrugAllergy().isModified() || getOpdOrder().isModified()
				|| getMedHistory().isModified() || getRegPatAdm().isModified()
				|| getPatInfo().isModified();
	}

	/**
	 * 初始化打印信息
	 * 
	 * @return boolean true:成功,false:错误
	 */
	/*
	 * /* 处方签使用信息 病患名称、支付方式、部门名称、性别、诊断、诊间名称、时段名称、年龄
	 */
	/*
	 * private String patName; private String payTypeName; private String
	 * deptName; private String sexName; private String icdName; private String
	 * clinicName; private String sessionName; private String ageName;
	 */

	public Map<String, String> getPreCaseNos() {
		return preCaseNos;
	}

	public void setPreCaseNos(Map<String, String> preCaseNos) {
		this.preCaseNos = preCaseNos;
	}

	/**
	 * 预开检查case_no和细项的标记位的修改 yanjing
	 */
	public void preChangeCaceNo() {
		String lastFilter = opdOrder.getFilter();
		opdOrder.setFilter("");
		opdOrder.filter();
		String med_apply_no = "";
		for (int i = 0; i < opdOrder.rowCount(); i++) {
			// String oldStringFilter = opdOrder.getFilter();
			// opdOrder.setFilter("");
			// opdOrder.filter();
			med_apply_no = opdOrder.getItemString(i, "MED_APPLY_NO");
			String isPre = opdOrder.getItemString(i, "IS_PRE_ORDER");
			if (isPre.equals("Y")) {
				// String ss = opdOrder.getItemString(i, "ORDER_CODE");
				// String sen = opdOrder.getItemString(i, "ORDER_DESC");
				String orderCode = opdOrder.getItemString(i, "ORDERSET_CODE");
				String rxNo = opdOrder.getItemString(i, "RX_NO");
				String groupNo = opdOrder.getItemString(i, "ORDERSET_GROUP_NO");
				for (int j = 0; j < opdOrder.rowCount(); j++) {
					String orderCode1 = opdOrder.getItemString(j,
							"ORDERSET_CODE");
					String rxNo1 = opdOrder.getItemString(j, "RX_NO");
					String groupNo1 = opdOrder.getItemString(j,
							"ORDERSET_GROUP_NO");
					if (orderCode.equals(orderCode1) && rxNo.equals(rxNo1)
							&& groupNo.equals(groupNo1)) {
						opdOrder.setItem(j, "IS_PRE_ORDER", "Y");
						opdOrder.setItem(j, "PRE_DATE", opdOrder.getItemData(i,
								"PRE_DATE"));
					}
				}
				if (preCaseNos.containsKey(opdOrder.getItemString(i, "RX_NO"))) {
					String preCaseNo = preCaseNos.get(opdOrder.getItemString(i,
							"RX_NO"));
					opdOrder.setItem(i, "CASE_NO", preCaseNo);
				}
			}
			// opdOrder.setFilter(oldStringFilter);
			// opdOrder.filter();
			String lastMedFilter = opdOrder.getMedApply().getFilter();
			opdOrder.getMedApply().setFilter("");
			opdOrder.getMedApply().filter();
			for (int j = 0; j < opdOrder.getMedApply().rowCount(); j++) {
				String medApplyNo = opdOrder.getMedApply().getItemString(j,
						"APPLICATION_NO");
				if (medApplyNo.equals(med_apply_no)) {
					if (preCaseNos.containsKey(opdOrder.getItemString(i,
							"RX_NO"))) {
						String preCaseNo = preCaseNos.get(opdOrder
								.getItemString(i, "RX_NO"));
						opdOrder.getMedApply().setItem(j, "CASE_NO", preCaseNo);
					}
				}
			}
			opdOrder.getMedApply().setFilter(lastMedFilter);
			opdOrder.getMedApply().filter();
		}
		opdOrder.setFilter(lastFilter);
		opdOrder.filter();
	}

	/**
	 * 比较a,b中的同一字段是否不同
	 * 
	 * @author zhangp
	 * @param aParm
	 * @param bParm
	 * @param column
	 * @return
	 */
	private boolean comPareColumn(TParm aParm, TParm bParm, String column) {
		String a = aParm.getValue(column);
		String b = bParm.getValue(column);
		if (!a.equals(b)) {
			return true;
		}
		return false;
	}

	/**
	 * 整理parm并去掉空行
	 * 
	 * @author zhangp
	 * @param aParm
	 * @return
	 */
	private TParm makeParmClean(TParm aParm) {
		TParm bParm = new TParm();
		String[] aNames = aParm.getNames();
		for (int i = 0; i < aParm.getCount(); i++) {
			if (aParm.getValue("ORDER_CODE", i).length() != 0) {
				for (int j = 0; j < aNames.length; j++) {
					bParm.addData(aNames[j], aParm.getData(aNames[j], i));
				}
			}
		}
		return bParm;
	}

	/**
	 * 获取改变了的parm中的sql中的关键字
	 * 
	 * @param aParm
	 * @param bParm
	 * @author zhangp
	 * @return
	 */
	private List<String> getKeyWordListHasChanged(TParm aParm, TParm bParm) {
		String aRxNo;
		String bRxNo;
		int aSeqNo;
		int bSeqNo;
		TParm aRowParm;
		TParm bRowParm;
		String[] bNames = bParm.getNames();
		List<String> list = new ArrayList<String>();

		for (int i = 0; i < aParm.getCount("ORDER_CODE"); i++) {
			aRxNo = aParm.getValue("RX_NO", i);
			aSeqNo = aParm.getInt("SEQ_NO", i);
			aRowParm = aParm.getRow(i);
			for (int j = 0; j < bParm.getCount("ORDER_CODE"); j++) {
				bRxNo = bParm.getValue("RX_NO", j);
				bSeqNo = bParm.getInt("SEQ_NO", j);
				bRowParm = bParm.getRow(j);
				if (aRxNo.equals(bRxNo) && aSeqNo == bSeqNo) {
					for (int j2 = 0; j2 < bNames.length; j2++) {
						if (comPareColumn(aRowParm, bRowParm, bNames[j2])) {
							list.add("CASE_NO='" + aParm.getValue("CASE_NO", i)
									+ "' AND RX_NO='"
									+ aParm.getValue("RX_NO", i)
									+ "' AND SEQ_NO="
									+ aParm.getValue("SEQ_NO", i));
							break;
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * 过滤不需要执行的sql
	 * 
	 * @author zhangp
	 * @param tempList
	 * @param list
	 * @param sqlList
	 */
	private void sqlFilter(List<String> tempList, List<String> list,
			List<String> sqlList) {
		List<Integer> temp = new ArrayList<Integer>();
		for (int i = 0; i < tempList.size(); i++) {
			String bs = tempList.get(i);
			for (String as : list) {
				if (bs.contains(as) && !temp.contains(i)) {
					temp.add(i);
				}
			}
		}

		for (int i = 0; i < tempList.size(); i++) {
			if (!temp.contains(i)) {
				sqlList.add(tempList.get(i));
			}
		}
	}
	
	/**
	 * 对删除的医嘱进行判断，如果为集合医嘱就去数据库中查询他的细项，然后跟要删除的SQL进行比较查看是否存在
	 * （为了解决集合医嘱主项删除，细项没有删除问题）
	 * @param dList
	 * @return
	 */
	private List<String> dealDel(List<String> dList){
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < dList.size(); i++) {
			String sql = dList.get(i).replace("DELETE FROM OPD_ORDER", "SELECT CASE_NO,RX_NO, SETMAIN_FLG,ORDERSET_GROUP_NO,ORDERSET_CODE FROM OPD_ORDER");
			TParm dParm = new TParm(TJDODBTool.getInstance().select(sql));
			if(dParm.getCount() > 0){
				if(dParm.getBoolean("SETMAIN_FLG",0)){
					String sqlAll = "SELECT CASE_NO,RX_NO,SEQ_NO FROM OPD_ORDER " +
							"WHERE CASE_NO='"+dParm.getValue("CASE_NO", 0)+"' " +
							"AND RX_NO='"+dParm.getValue("RX_NO", 0)+"' " +
							"AND ORDERSET_CODE='"+dParm.getValue("ORDERSET_CODE", 0)+"' " +
							"AND ORDERSET_GROUP_NO='"+dParm.getValue("ORDERSET_GROUP_NO", 0)+"' " +
							"AND SETMAIN_FLG='N'";
					TParm delDParm = new TParm(TJDODBTool.getInstance().select(sqlAll));
					for (int j = 0; j < delDParm.getCount(); j++) {
//						System.out.println("delDParm==="+j+"=="+delDParm.getRow(j));
						list.add("CASE_NO='" + delDParm.getValue("CASE_NO", j)
								+ "' AND RX_NO='"
								+ delDParm.getValue("RX_NO", j)
								+ "' AND SEQ_NO="
								+ delDParm.getValue("SEQ_NO", j));
					}
				}
			}
		}
		
		List<String> listD = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
//			System.out.println(list.get(i));
			boolean flg = true;
			for (int j = 0; j < dList.size(); j++) {
				
				if(dList.get(j).contains(list.get(i))){				
					flg = false;
					break;
				}
				
			}
			if(flg){
				listD.add("DELETE FROM OPD_ORDER WHERE "+list.get(i));
			}
		}
		
		return listD;
		
	}

}
