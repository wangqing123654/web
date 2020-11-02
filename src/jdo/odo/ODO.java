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
 * Title: ����ҽ��վ����
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
	 * ������
	 */
	private String mrNo;
	/**
	 * �����
	 */
	private String caseNo;
	/**
	 * �ż�ס��
	 */
	private String admType;
	/**
	 * ҽʦ
	 */
	private String drCode;
	/**
	 * �������
	 */
	private String icdType;
	/**
	 * ����
	 */
	private String deptCode;
	/**
	 * ���߿���
	 */
	private Subjrec subjrec;
	/**
	 * ���
	 */
	private Diagrec diagrec;
	/**
	 * ����ʷ
	 */
	private DrugAllergy drugAllergy;
	/**
	 * ҽ��
	 */
	private OpdOrder opdOrder;
	/**
	 * ����ʷ
	 */
	private MedHistory medHistory;
	/**
	 * �Һ�
	 */
	private RegPatAdm regPatAdm;
	/**
	 * ����
	 */
	private PatInfo patInfo;
	/**
	 * ҽ����ʷ
	 */
	private OpdOrderHistory opdOrderHistory;

	/**
	 * ������Ϣ
	 */
	private String errText = "";
	/**
	 * ���ݿ⹤��
	 */
	private TJDODBTool dbTool = new TJDODBTool();
	/**
	 * ҽ�ƿ����׺�
	 */
	private String TREDE_NO = "";
	/**
	 * ���Ԥ����鴦��ǩ�ż������ yanjing 20131217
	 */
	private Map<String, String> preCaseNos;

	/**
	 * ������
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
		// ��ʼ�����߿���
		setSubjrec(new Subjrec());
		// ��ʼ�����
		setDiagrec(new Diagrec());
		// ����ʷ
		setDrugAllergy(new DrugAllergy());
		// ҽ��
		setOpdOrder(new OpdOrder());
		// ����ʷ
		setMedHistory(new MedHistory());
		// �Һ�
		setRegPatAdm(new RegPatAdm());
		// ����
		setPatInfo(new PatInfo());
		// ҽ����ʷ
		setOpdOrderHistory(new OpdOrderHistory());
	}

	/**
	 * �����ż�ס��
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
	 * �õ��ż�ס��
	 * 
	 * @return String
	 */
	public String getAdmType() {
		return admType;
	}

	/**
	 * �õ��������
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
	 * ����ҽʦ
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
	 * �õ�ҽʦ
	 * 
	 * @return String
	 */
	public String getDrCode() {
		return drCode;
	}

	/**
	 * ���ò�����
	 * 
	 * @return String
	 */
	public String getMrNo() {
		return this.mrNo;
	}

	/**
	 * �õ�������
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
	 * �õ������
	 * 
	 * @return String
	 */
	public String getCaseNo() {
		return this.caseNo;
	}

	/**
	 * ���þ����
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
	 * ���ò���
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
	 * �õ�����
	 * 
	 * @return String
	 */
	public String getDeptCode() {
		return deptCode;
	}

	/**
	 * �������߿���
	 * 
	 * @param subjrec
	 *            Subjrec
	 */
	public void setSubjrec(Subjrec subjrec) {
		this.subjrec = subjrec;
	}

	/**
	 * �õ����߿���
	 * 
	 * @return Subjrec
	 */
	public Subjrec getSubjrec() {
		return subjrec;
	}

	/**
	 * ������϶���
	 * 
	 * @param diagrec
	 *            Diagrec
	 */
	public void setDiagrec(Diagrec diagrec) {
		this.diagrec = diagrec;
	}

	/**
	 * �õ���϶���
	 * 
	 * @return Diagrec
	 */
	public Diagrec getDiagrec() {
		return diagrec;
	}

	/**
	 * ���ù���ʷ����
	 * 
	 * @param drugAllergy
	 *            DrugAllergy
	 */
	public void setDrugAllergy(DrugAllergy drugAllergy) {
		this.drugAllergy = drugAllergy;
	}

	/**
	 * �õ�����ʷ����
	 * 
	 * @return DrugAllergy
	 */
	public DrugAllergy getDrugAllergy() {
		return drugAllergy;
	}

	/**
	 * ����ҽ������
	 * 
	 * @param opdOrder
	 *            OpdOrder
	 */
	public void setOpdOrder(OpdOrder opdOrder) {
		this.opdOrder = opdOrder;
	}

	/**
	 * �õ�ҽ������
	 * 
	 * @return OpdOrder
	 */
	public OpdOrder getOpdOrder() {
		return opdOrder;
	}

	/**
	 * ���ü���ʷ����
	 * 
	 * @param medHistory
	 *            MedHistory
	 */
	public void setMedHistory(MedHistory medHistory) {
		this.medHistory = medHistory;
	}

	/**
	 * �õ�����ʷ����
	 * 
	 * @return MedHistory
	 */
	public MedHistory getMedHistory() {
		return medHistory;
	}

	/**
	 * ���ùҺŶ���
	 * 
	 * @param regPatAdm
	 *            RegPatAdm
	 */
	public void setRegPatAdm(RegPatAdm regPatAdm) {
		this.regPatAdm = regPatAdm;
	}

	/**
	 * �õ��ҺŶ���
	 * 
	 * @return RegPatAdm
	 */
	public RegPatAdm getRegPatAdm() {
		return this.regPatAdm;
	}

	/**
	 * ���ò�������
	 * 
	 * @param patInfo
	 *            PatInfo
	 */
	public void setPatInfo(PatInfo patInfo) {
		this.patInfo = patInfo;
	}

	/**
	 * �õ���������
	 * 
	 * @return PatInfo
	 */
	public PatInfo getPatInfo() {
		return this.patInfo;
	}

	/**
	 * ����ҽ����ʷ����
	 * 
	 * @param opdOrderHistory
	 *            OpdOrderHistory
	 */
	public void setOpdOrderHistory(OpdOrderHistory opdOrderHistory) {
		this.opdOrderHistory = opdOrderHistory;
	}

	/**
	 * �õ�ҽ����ʷ����
	 * 
	 * @return OpdOrderHistory
	 */
	public OpdOrderHistory getOpdOrderHistory() {
		return this.opdOrderHistory;
	}

	/**
	 * �õ����ݿ⹤��
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return dbTool;
	}

	/**
	 * ���ô�����Ϣ
	 * 
	 * @param errText
	 *            String
	 */
	public void setErrText(String errText) {
		this.errText = errText;
	}

	/**
	 * �õ�������Ϣ
	 * 
	 * @return String
	 */
	public String getErrText() {
		return errText;
	}

	/**
	 * ����ҽ�ƿ����׺�
	 * 
	 * @param TredeNo
	 *            String
	 */
	public void setTredeNo(String TredeNo) {
		this.TREDE_NO = TredeNo;
	}

	/**
	 * �õ�ҽ�ƿ����׺�
	 */
	public String getTredeNo() {
		return TREDE_NO;
	}

	/**
	 * ��ѯ
	 * 
	 * @return boolean
	 */
	public boolean onQuery() {
		// �������ô�����Ϣ
		setErrText("");

		// ���CASE_NO
		if (getCaseNo() == null || getCaseNo().length() <= 0) {
			setErrText("CaseNoΪ�գ�");
			return false;
		}
		// ��ѯ���߿�������
		if (!getSubjrec().onQuery()) {
			setErrText(getSubjrec().getErrText());
			return false;
		}
		if (getSubjrec().rowCount() < 1)
			getSubjrec().insertRow();
		// ��ѯ�������
		if (!getDiagrec().onQuery()) {
			setErrText(getDiagrec().getErrText());
			return false;
		}
		getDiagrec().insertRow();
		// ��ѯ����ʷ����
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
		// ��ѯҽ������
		if (!getOpdOrder().onQuery()) {
			setErrText(getOpdOrder().getErrText());
			return false;
		}
		// ��ѯ����ʷ����
		if (!getMedHistory().onQuery()) {
			setErrText(getMedHistory().getErrText());
			return false;
		}
		getMedHistory().insertRow();
		// ��ѯ�Һ�����
		if (!getRegPatAdm().onQuery()) {
			setErrText(getRegPatAdm().getErrText());
			return false;
		}

		// ��ѯ��������
		if (!getPatInfo().onQuery()) {
			setErrText(getPatInfo().getErrText());
			return false;
		}
		getOpdOrder().getMedApply();// ��ʼ��med_apply
		getOpdOrder().getLabMap();
		// //��ѯ��������
		// if(!getPatInfo().onQuery()){
		// setErrText(getPatInfo().getErrText());
		// return false;
		// }
		return true;
	}

	/**
	 * ������
	 * 
	 * @return boolean
	 */
	public boolean checkSave() {
		// ������߿���
		if (!getSubjrec().checkSave()) {
			setErrText(getSubjrec().getErrText());
			return false;
		}
		// ������
		if (!getDiagrec().checkSave()) {
			setErrText(getDiagrec().getErrText());
			return false;
		}
		// ������ʷ
		if (!getDrugAllergy().checkSave()) {
			setErrText(getDrugAllergy().getErrText());
			return false;
		}

		// ���ҽ��
		if (!getOpdOrder().checkSave()) {
			setErrText(getOpdOrder().getErrText());
			return false;
		}

		// ������ʷ
		if (!getMedHistory().checkSave()) {
			setErrText(getMedHistory().getErrText());
			return false;
		}
		// ���Һ�
		if (!getRegPatAdm().checkSave()) {
			setErrText(getRegPatAdm().getErrText());
			return false;
		}
		// ��ⲡ��
		if (!getPatInfo().checkSave()) {
			setErrText(getPatInfo().getErrText());
			return false;
		}
		Timestamp optDate = getDBTool().getDBTime();
		// �������߿����޸��е��û���Ϣ
		getSubjrec().setOperator(Operator.getID(), optDate, Operator.getIP());
		// ��������޸��е��û���Ϣ
		getDiagrec().setOperator(Operator.getID(), optDate, Operator.getIP());
		// ���ù���ʷ�޸��е��û���Ϣ
		getDrugAllergy().setOperator(Operator.getID(), optDate,
				Operator.getIP());
		// ����ҽ���޸��е��û���Ϣ
		getOpdOrder().setOperator(Operator.getID(), optDate, Operator.getIP());
		// ���ü���ʷ�޸��е��û���Ϣ
		getMedHistory()
				.setOperator(Operator.getID(), optDate, Operator.getIP());
		return true;
	}

	/**
	 * ����ҽ��
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public boolean onSave() throws Exception {
		//�жϸò����Ƿ�Ϊ�˹���Ա  true �˹� add by huangtt 20150215
		if(REGTool.getInstance().queryUnReg(caseNo)){
			return false;
		}
		
		
		// ������
		if (!checkSave())
			return false;

		// �������߿���
		String sql[] = getSubjrec().getUpdateSQL();
		// ���SQL�Ƿ�Ϊ��
		if (sql == null) {
			// System.out.println("subjrec is wrong");
			setErrText(getSubjrec().getErrText());
			return false;
		}

		// �������
		String sqlTemp[] = getDiagrec().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("diag is wrong");
			setErrText(getDiagrec().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);
		// getDiagrec().showDebug();
		// �������ʷ
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

		// ����ҽ��
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


		// ----�ж�Ԥ����� add caoyong 20140103 start-----
		String medFlg = "";
		String sqlQ = "SELECT MED_FLG FROM OPD_SYSPARM";
		TParm sresult = new TParm(TJDODBTool.getInstance().select(sqlQ));
		if (sresult.getCount() > 0) {
			medFlg = sresult.getValue("MED_FLG", 0);
		}
		
		// ----�ж�Ԥ����� add caoyong 20140103 end-----
		if (getOpdOrder().getMedApply().isModified()) {
			// ����MED_APPLY
			// ============pangben 2013-01-31 ɾ��Med_Apply��������ĳ��޸�BILL_FLG =N�ֶ�
			// ȥ��ɾ����sql ֻ���� ��ӵ�sql���
			List list = new ArrayList();
			sqlTemp = getOpdOrder().getMedApply().getUpdateSQL();
			for (int i = 0; i < sqlTemp.length; i++) {
				// ----�ж�Ԥ����� add caoyong 20140103 start-----
				String line = sqlTemp[i];
				if ("Y".equalsIgnoreCase(medFlg)) {
					if (sqlTemp[i].contains("DELETE"))
						continue;
				}
				// ----�ж�Ԥ����� add caoyong 20140103 end-----
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
		
		// �����ɾ����ҽ��������Ҫ������ʷ��
		int count = getOpdOrder().getDeleteCount();
		//���ҽ��û�з����仯��ɾ��ҽ����������ʷ�� 
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
		

		// �������ʷ
		sqlTemp = getMedHistory().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("medhis is wrong");
			setErrText(getMedHistory().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);

		// ����Һ�
		sqlTemp = getRegPatAdm().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("regpatadm is wrong");
			setErrText(getRegPatAdm().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);
		// ���没��
		sqlTemp = getPatInfo().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("patinfo is wrong");
			setErrText(getPatInfo().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);
		// ======pangben 2013-11-7������������ѯҽ������
		TParm oldOrderParm = new TParm();
		TParm newOrderParm = new TParm();
		if (Operator.getLockFlg().equals("Y")) {// ����֮ǰ����
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

			// ��ѯ����
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
		if (Operator.getLockFlg().equals("Y")) {// ======pangben 2013-11-7 ��������
												// ����֮�����ݲ�ѯ

			TParm parm = new TParm();
			parm.setData("CASE_NO", this.getCaseNo());
			parm.setData("CAT1_TYPE", "PHA");
			// ɾ�����ж�ɾ����
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
		// ����޸Ĵ洢
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
			 * /** �Һ�ͬ��
			 * 
			 * @param VISIT_DATE
			 *            String �Һ����� NOT NULL
			 * @param VISIT_NO
			 *            String �Һź� NOT NULL CASENO
			 * @param CLINIC_LABEL
			 *            String ר�� ��ͨ��
			 * @param SERIAL_NO
			 *            String С��
			 * @param PATIENT_ID
			 *            String ���˿���
			 * @param PNAME
			 *            String ��������
			 * @param PSEX
			 *            String �Ա�
			 * @param PAGE
			 *            String ����
			 * @param IDENTITY
			 *            String ���
			 * @param DEPTID
			 *            String ����ID
			 * @param REGISTERING_DATE
			 *            String �Һ�ʱ�䣨2010-2-8 11:11:11��
			 * @param DOCTOR
			 *            String ҽ����� ��ͨ����Ϊ��
			 * @param CTYPE
			 *            String 0 �Һ� 1 �˺� 2�������
			 * @param OPTYPE
			 *            String �������� 0 ��� 1 ɾ�� 2 ������߸��� 3 �˺�
			 * 
			 */
			callNoUtil.SyncClinicMaster("", getCaseNo(), "", "", "", "", "",
					"", "", "", "", "", "2", "", "");
		}
		if (isChanged) {
			throw new Exception("���޸Ļ�ɾ����ҽ���ѱ������޸�\r\n��������ҽ���ѱ���\r\n�������޸�ҽ��");
		}
		return true;
	}

	/**
	 * �ж�odo�����Ƿ��޸Ĺ�
	 * 
	 * @return boolean true:�޸Ĺ�,false:δ�޸�
	 */
	public boolean isModified() {
		return getSubjrec().isModified() || getDiagrec().isModified()
				|| getDrugAllergy().isModified() || getOpdOrder().isModified()
				|| getMedHistory().isModified() || getRegPatAdm().isModified()
				|| getPatInfo().isModified();
	}

	/**
	 * ��ʼ����ӡ��Ϣ
	 * 
	 * @return boolean true:�ɹ�,false:����
	 */
	/*
	 * /* ����ǩʹ����Ϣ �������ơ�֧����ʽ���������ơ��Ա���ϡ�������ơ�ʱ�����ơ�����
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
	 * Ԥ�����case_no��ϸ��ı��λ���޸� yanjing
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
	 * �Ƚ�a,b�е�ͬһ�ֶ��Ƿ�ͬ
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
	 * ����parm��ȥ������
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
	 * ��ȡ�ı��˵�parm�е�sql�еĹؼ���
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
	 * ���˲���Ҫִ�е�sql
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
	 * ��ɾ����ҽ�������жϣ����Ϊ����ҽ����ȥ���ݿ��в�ѯ����ϸ�Ȼ���Ҫɾ����SQL���бȽϲ鿴�Ƿ����
	 * ��Ϊ�˽������ҽ������ɾ����ϸ��û��ɾ�����⣩
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
