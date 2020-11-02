package jdo.sys.ws;

import javax.jws.WebService;

import jdo.sys.SYSDictionaryServiceTool;

@WebService
public class DictionaryServiceImpl implements IDictionaryService {

	/**
	 * µÃµ½°æ±¾
	 * 
	 * @return String
	 */
	public String getVersion() {
		return "BlueCore DictionaryService version is 1.0.1";
	}

	@Override
	public String[] getDeptInf(String code, String password) {
		return SYSDictionaryServiceTool.getInstance()
				.getDeptInf(code, password);
	}

	@Override
	public String confirmedInf(String code, String password, String tableName,
			String index) {
		return SYSDictionaryServiceTool.getInstance().confirmedInf(code,
				password, tableName, index);
	}

	@Override
	public String deleteInf(String code, String password, String tableName,
			String index) {
		return SYSDictionaryServiceTool.getInstance().deleteInf(code, password,
				tableName, index);
	}

	@Override
	public String fetchInf(String code, String password, String tableName,
			String index) {
		return SYSDictionaryServiceTool.getInstance().fetchInf(code, password,
				tableName, index);
	}

	@Override
	public String[] getAdmType(String code, String password) {
		return SYSDictionaryServiceTool.getInstance()
				.getAdmType(code, password);
	}

	@Override
	public String[] getClinicArea(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getClinicArea(code,
				password);
	}

	@Override
	public String[] getClinicRoom(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getClinicRoom(code,
				password);
	}

	@Override
	public String[] getCtzInf(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getCtzInf(code, password);
	}

	@Override
	public String[] getDeptClassRule(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getDeptClassRule(code,
				password);
	}

	@Override
	public String[] getDeptInfCode(String code, String password, String deptcode) {
		return SYSDictionaryServiceTool.getInstance().getDeptInfCode(code,
				password, deptcode);
	}

	@Override
	public String[] getDeptInfSY(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getDeptInfSY(code,
				password);
	}

	@Override
	public String[] getDiagnosisInf(String code, String password,
			String classify, String type) {
		return SYSDictionaryServiceTool.getInstance().getDiagnosisInf(code,
				password, classify, type);
	}

	@Override
	public String getDictionary(String groupId, String id) {
		return SYSDictionaryServiceTool.getInstance()
				.getDictionary(groupId, id);
	}

	@Override
	public String[] getHisCancelOrder(String code, String password,
			String caseno, String rxno, String seqno) {
		return SYSDictionaryServiceTool.getInstance().getHisCancelOrder(code,
				password, caseno, rxno, seqno);
	}

	@Override
	public String[] getIndMaterialloc(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getIndMaterialloc(code,
				password);
	}

	@Override
	public String[] getIndMateriallocOrder(String code, String password,
			String orgcode, String ordercode) {
		return SYSDictionaryServiceTool.getInstance().getIndMateriallocOrder(
				code, password, orgcode, ordercode);
	}

	@Override
	public String[] getLisOrder(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getLisOrder(code,
				password);
	}

	@Override
	public String[] getModifyInf(String code, String password, String status,
			String tableName) {
		return SYSDictionaryServiceTool.getInstance().getModifyInf(code,
				password, status, tableName);
	}

	@Override
	public String[] getModifyTable(String code, String password, String status) {
		return SYSDictionaryServiceTool.getInstance().getModifyTable(code,
				password, status);
	}

	@Override
	public String[] getODIPhaOrderInfo(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getODIPhaOrderInfo(code,
				password);
	}

	@Override
	public String[] getODIPhaOrderInfoItem(String code, String password,
			String ordercode) {
		return SYSDictionaryServiceTool.getInstance().getODIPhaOrderInfoItem(
				code, password, ordercode);
	}

	@Override
	public String[] getOperatorInf(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getOperatorInf(code,
				password);
	}

	@Override
	public String[] getOperatorInfSY(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getOperatorInfSY(code,
				password);
	}

	@Override
	public String[] getOrderSY(String code, String password, String rxno) {
		return SYSDictionaryServiceTool.getInstance().getOrderSY(code,
				password, rxno);
	}

	@Override
	public String[] getPatAppRegInfo(String code, String password, String mrNo) {
		return SYSDictionaryServiceTool.getInstance().getPatAppRegInfo(code,
				password, mrNo);
	}

	@Override
	public String[] getPatForSID(String code, String password, String SID,
			String name) {
		return SYSDictionaryServiceTool.getInstance().getPatForSID(code,
				password, SID, name);
	}

	@Override
	public String[] getPatInfAndOrder(String code, String password, String rxno) {
		return SYSDictionaryServiceTool.getInstance().getPatInfAndOrder(code,
				password, rxno);
	}

	@Override
	public String[] getPhaClassify(String code, String password, String name) {
		return SYSDictionaryServiceTool.getInstance().getPhaClassify(code,
				password, name);
	}

	@Override
	public String[] getPhaEsyOrder(String code, String password,
			String ordercode) {
		return SYSDictionaryServiceTool.getInstance().getPhaEsyOrder(code,
				password, ordercode);
	}

	@Override
	public String[] getPhaFreqInf(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getPhaFreqInf(code,
				password);
	}

	@Override
	public String[] getPhaFreqInfSY(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getPhaFreqInfSY(code,
				password);
	}

	@Override
	public String[] getPhaInf(String code, String password, String classify) {
		return SYSDictionaryServiceTool.getInstance().getPhaInf(code, password,
				classify);
	}

	@Override
	public String[] getPhaOrder(String code, String password, String ordercode) {
		return SYSDictionaryServiceTool.getInstance().getPhaOrder(code,
				password, ordercode);
	}

	@Override
	public String[] getPhaOrderPY1(String code, String password, String py1,
			int startrow, int endrow) {
		return SYSDictionaryServiceTool.getInstance().getPhaOrderPY1(code,
				password, py1, startrow, endrow);
	}

	@Override
	public String[] getRegSchDay(String code, String password, String admDate) {
		return SYSDictionaryServiceTool.getInstance().getRegSchDay(code,
				password, admDate);
	}

	@Override
	public String[] getRegWorkList(String code, String password,
			String admtype, String startdate, String enddate) {
		return SYSDictionaryServiceTool.getInstance().getRegWorkList(code,
				password, admtype, startdate, enddate);
	}

	@Override
	public String[] getRegionJD(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getRegionJD(code,
				password);
	}

	@Override
	public String getRegistStatus(String code) {
		return SYSDictionaryServiceTool.getInstance().getRegistStatus(code);
	}

	@Override
	public String[] getRegistTableInf(String code, String password,
			String tableName) {
		return SYSDictionaryServiceTool.getInstance().getRegistTableInf(code,
				password, tableName);
	}

	@Override
	public String[] getReulInf(String code, String password) {
		return SYSDictionaryServiceTool.getInstance()
				.getReulInf(code, password);
	}

	@Override
	public String[] getRouteInf(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getRouteInf(code,
				password);
	}

	@Override
	public String[] getSessionInf(String code, String password, String admType) {
		return SYSDictionaryServiceTool.getInstance().getSessionInf(code,
				password, admType);
	}

	@Override
	public String[] getSexInf(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getSexInf(code, password);
	}

	@Override
	public String[] getShareTable() {
		return SYSDictionaryServiceTool.getInstance().getShareTable();
	}

	@Override
	public String[] getStation(String code, String password) {
		return SYSDictionaryServiceTool.getInstance()
				.getStation(code, password);
	}

	@Override
	public String[] getSysOperationICD(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getSysOperationICD(code,
				password);
	}

	@Override
	public String modifyPassword(String code, String oldPassword,
			String newPassword) {
		return SYSDictionaryServiceTool.getInstance().modifyPassword(code,
				oldPassword, newPassword);
	}

	@Override
	public String[] readOpdOrderPS(String code, String password, String caseno,
			String rxno, String seqno, String value) {
		return SYSDictionaryServiceTool.getInstance().readOpdOrderPS(code,
				password, caseno, rxno, seqno, value);
	}

	@Override
	public String regAppt(String code, String password, String mrNo,
			String date, String sessionCode, String admType, String deptCode,
			String clinicRoomNo, String drCode, String regionCode,
			String ctz1Code, String serviceLevel) {
		return SYSDictionaryServiceTool.getInstance().regAppt(code, password,
				mrNo, date, sessionCode, admType, deptCode, clinicRoomNo,
				drCode, regionCode, ctz1Code, serviceLevel);
	}

	@Override
	public String regUnAppt(String code, String password, String caseNo) {
		return SYSDictionaryServiceTool.getInstance().regUnAppt(code, password,
				caseNo);
	}

	@Override
	public String regist(String code, String chnDesc, String engDesc,
			String contactsName, String tel, String email, String password) {
		return SYSDictionaryServiceTool.getInstance().regist(code, chnDesc,
				engDesc, contactsName, tel, email, password);
	}

	@Override
	public String registTable(String code, String password, String tableName,
			String action) {
		return SYSDictionaryServiceTool.getInstance().registTable(code,
				password, tableName, action);
	}

	@Override
	public String savePat(String code, String password, String name,
			String birthday, String sex, String SID, String tel, String address) {
		return SYSDictionaryServiceTool.getInstance().savePat(code, password,
				name, birthday, sex, SID, tel, address);
	}

	@Override
	public String[] getSysFee(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getSysFee(code, password);
	}

	@Override
	public String[] getSysFeeHistory(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getSysFeeHistory(code, password);
	}

	@Override
	public String[] getData(String code, String password, String tableName) {
		return SYSDictionaryServiceTool.getInstance().getData(code, password, tableName);
	}

	@Override
	public String[] getPatByMrNo(String mrNo) {
		return SYSDictionaryServiceTool.getInstance().getPatByMrNo(mrNo);

	}

	@Override
	public String getSchDay(String beginDate, String endDate, String deptCode,
			String drCode) {		
		return SYSDictionaryServiceTool.getInstance().getSchDay(beginDate, endDate, deptCode, drCode);
	}

	@Override
	public String getUpdatedScyDay(String beginDate, String endDate) {
		return SYSDictionaryServiceTool.getInstance().getUpdatedScyDay(beginDate, endDate);
	}

	@Override
	public String getVipSchDay(String beginDate, String endDate) {
		
		return SYSDictionaryServiceTool.getInstance().getVipSchDay(beginDate, endDate);
	}

	@Override
	public String[] getClinictype(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getClinictype(code, password);
	}

	@Override
	public String[] getClinictypeFee(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getClinictypeFee(code, password);
	}

	@Override
	public String[] getQuegroup(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getQuegroup(code, password);
	}

	@Override
	public String[] getRegmethod(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getRegmethod(code, password);
	}

	@Override
	public String[] getOperatorDeptCode(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getOperatorDeptCode(code, password);
	}

	@Override
	public boolean updateSchdayCrmSyncFlg(String beginDate, String endDate) {
		return SYSDictionaryServiceTool.getInstance().updateSchdayCrmSyncFlg(beginDate, endDate);
	}




}
