package jdo.spc;

import java.util.List;
import java.util.TimerTask;

import jdo.sys.client.IDictionaryService;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>
 * Title: ��������hisͬ��
 * </p>
 * 
 * <p>
 * Description: ��������hisͬ��
 * </p>
 * 
 * @author zhangyiwu 2013.7.10
 * @version 1.0
 */

public class SYSDictionaryTask extends TimerTask {

	// ��ʱɨ��˷������õ���Ҫͬ�������ݲ�������
	public void run() {

		// �����־��Ϣ
		TParm logparm = new TParm();
		logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
		// ��ϵͳ����
		String arg0 = SYSDictionaryConfig.getUserName();
		// ��ϵͳ����
		String arg1 = SYSDictionaryConfig.getpassword();
		// ״̬
		String arg2 = "NEW";
		// ���շ����仯�ı�
		List<String> result = null;
		// ����webservice�ӿ���������쳣�����񲢼�¼��־
		try {
			// �õ������仯�ı���
			result = SYSDictionaryConfig.ModifyTable(arg0, arg1, arg2);

		} catch (Exception e) {

			logparm.setData("RESULT_DESC", "WEBSERVICESû������");
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);

		}
		// �ж���û������ͬ���ı�
		if (null != result && result.size() > 0) {
			// �з������ݱ仯�ı�������������
			for (int i = 0; i < result.size(); i++) {
				// �жϳ���ע������Ƿ�Ϊ��
				if (result.get(i).equals("ERR:code is null")) {
					logparm.setData("RESULT_DESC", "���̴���Ϊ�գ�");
					SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(
							logparm);
				}
				// �жϳ���ע�������Ƿ�Ϊ��
				if (result.get(i).equals("ERR:password is null")) {
					logparm.setData("RESULT_DESC", "��������Ϊ�գ�");
					SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(
							logparm);
				}
				// �жϳ���ע������Ƿ���ȷ
				if (result.get(i).equals("Err:No Find " + arg0)) {

					logparm.setData("RESULT_DESC", "���̴������");
					SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(
							logparm);

				}
				// �жϳ���ע�������Ƿ���ȷ
				if (result.get(i).equals("Err:password err")) {

					logparm.setData("RESULT_DESC", "�����������");
					SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(
							logparm);
				}
				/** �жϾ��巢���仯�ı����õ�����仯����Ȼ�������� */
				if (result.get(i).equals("PHADOSE_INF")) {
					// ����webservice�ӿڣ��õ����巢���仯������
					List<String> _getModifyInf__return = SYSDictionaryConfig
							.ModifyInf(arg0, arg1, arg2, "PHADOSE_INF");

					PhaDoseService(_getModifyInf__return, arg0, arg1);

				}
				if (result.get(i).equals("BED_INF")) {
					List<String> _getModifyInf__return = SYSDictionaryConfig
							.ModifyInf(arg0, arg1, arg2, "BED_INF");
					SysBedService(_getModifyInf__return, arg0, arg1);
				}
				if (result.get(i).equals("BEDTYPE_INF")) {
					List<String> _getModifyInf__return = SYSDictionaryConfig
							.ModifyInf(arg0, arg1, arg2, "BEDTYPE_INF");
					SysBedTypeService(_getModifyInf__return, arg0, arg1);

				}
				if (result.get(i).equals("CATEGORY_INF")) {
					List<String> _getModifyInf__return = SYSDictionaryConfig
							.ModifyInf(arg0, arg1, arg2, "CATEGORY_INF");
					SysCategoryService(_getModifyInf__return, arg0, arg1);
				}
				if (result.get(i).equals("COST_CENTER_INF")) {
					List<String> _getModifyInf__return = SYSDictionaryConfig
							.ModifyInf(arg0, arg1, arg2, "COST_CENTER_INF");
					SysCostCenterService(_getModifyInf__return, arg0, arg1);

				}
				if (result.get(i).equals("CTRLDRUGCLASS_INF")) {
					List<String> _getModifyInf__return = SYSDictionaryConfig
							.ModifyInf(arg0, arg1, arg2, "CTRLDRUGCLASS_INF");
					SysCtrldrugclassService(_getModifyInf__return, arg0, arg1);

				}
				 if (result.get(i).equals("CTZ_INF")) {
				 List<String> _getModifyInf__return = SYSDictionaryConfig
				 .ModifyInf(arg0, arg1, arg2, "CTZ_INF");
				 SysCtzService(_getModifyInf__return, arg0, arg1);
				
				 }
				if (result.get(i).equals("DIAGNOSIS_INF")) {
					List<String> _getModifyInf__return = SYSDictionaryConfig
							.ModifyInf(arg0, arg1, arg2, "DIAGNOSIS_INF");
					SysDiagnosisService(_getModifyInf__return, arg0, arg1);

				}
//				if (result.get(i).equals("MANUFACTURER_INF")) {
//					List<String> _getModifyInf__return = SYSDictionaryConfig
//							.ModifyInf(arg0, arg1, arg2, "MANUFACTURER_INF");
//					SysManufacturerService(_getModifyInf__return, arg0, arg1);
//
//				}
				if (result.get(i).equals("POSITION_INF")) {
					List<String> _getModifyInf__return = SYSDictionaryConfig
							.ModifyInf(arg0, arg1, arg2, "POSITION_INF");
					SysPositionService(_getModifyInf__return, arg0, arg1);

				}
				if (result.get(i).equals("ROOM_INF")) {
					List<String> _getModifyInf__return = SYSDictionaryConfig
							.ModifyInf(arg0, arg1, arg2, "ROOM_INF");
					SysRoomService(_getModifyInf__return, arg0, arg1);

				}
				if (result.get(i).equals("STATION_INF")) {
					List<String> _getModifyInf__return = SYSDictionaryConfig
							.ModifyInf(arg0, arg1, arg2, "STATION_INF");
					SysStationService(_getModifyInf__return, arg0, arg1);

				}
				if (result.get(i).equals("ANTIBIOTIC_INF")) {
					List<String> _getModifyInf__return = SYSDictionaryConfig
							.ModifyInf(arg0, arg1, arg2, "ANTIBIOTIC_INF");
					SysAntibioticService(_getModifyInf__return, arg0, arg1);

				}
				if (result.get(i).equals("OPERATOR_INF")) {
					List<String> _getModifyInf__return = SYSDictionaryConfig
							.ModifyInf(arg0, arg1, arg2, "OPERATOR_INF");
					SysOperatorService(_getModifyInf__return, arg0, arg1);

				}
				if (result.get(i).equals("FEE_INF")) {
					List<String> _getModifyInf__return = SYSDictionaryConfig
							.ModifyInf(arg0, arg1, arg2, "FEE_INF");
					SysFeeService(_getModifyInf__return, arg0, arg1);

				}
				if (result.get(i).equals("BASE_INF")) {
					List<String> _getModifyInf__return = SYSDictionaryConfig
							.ModifyInf(arg0, arg1, arg2, "BASE_INF");
					PhaBaseService(_getModifyInf__return, arg0, arg1);

				}
				if (result.get(i).equals("AGENT_INF")) {
					List<String> _getModifyInf__return = SYSDictionaryConfig
							.ModifyInf(arg0, arg1, arg2, "AGENT_INF");
					IndAgentService(_getModifyInf__return, arg0, arg1);

				}
				if (result.get(i).equals("PATINFO_INF")) {
					List<String> _getModifyInf__return = SYSDictionaryConfig
							.ModifyInf(arg0, arg1, arg2, "PATINFO_INF");
					SysPatInfoService(_getModifyInf__return, arg0, arg1);

				}
				if (result.get(i).equals("TRANSUNIT_INF")) {
					List<String> _getModifyInf__return = SYSDictionaryConfig
							.ModifyInf(arg0, arg1, arg2, "TRANSUNIT_INF");
					PhaTransunitService(_getModifyInf__return, arg0, arg1);

				}
			}
		} else {
			// ����Ϣͬ����¼��־

			logparm.setData("RESULT_DESC", "û������ͬ��");

			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);
		}

	}

	/*
	 * PHA_DOSE��ͬ��
	 */
	private void PhaDoseService(List<String> _getModifyInf__return,
			String arg0, String arg1) {
		// �õ�����仯������

		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼���ݿ���в����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;

			// �ж��Ƿ�Ϊ����
			if (Endresult[2].trim().equals("INSERT")) {
				// ���ܳ����쳣��������������¼��־��Ϣ
				try {
					tparm.setData("DOSE_CODE", Endresult[3].trim());
					tparm.setData("DOSE_CHN_DESC", Endresult[4].trim());
					tparm.setData("ENG_DESC", Endresult[5].trim());
					tparm.setData("PY1", Endresult[6].trim());
					tparm.setData("PY2", Endresult[7].trim());
					tparm.setData("DESCRIPTION", Endresult[8].trim());
					tparm.setData("DOSE_TYPE", Endresult[9].trim());
					tparm.setData("SEQ", Endresult[10].trim());

				} catch (Exception e) {

					operation = false;
					result_desc = "FAILED prase String I/PHA_DOSE" + "/"
							+ Endresult[3].trim();
					// ��ӡ��������Ϣ����׼ȷ�Ķ�λ��������λ��
					System.out.println("---FAILED  I/PHA_DOSE" + "/"
							+ Endresult[3].trim() + result);
				}
				// �ж�ǰ���Ƿ�����쳣�����ж��Ƿ���ִ�����ݿ�����ı�Ҫ
				if (operation) {
					// ִ����������
					returnresult = SYSDictionaryPatchTool.getInstance()
							.onSavePhaDose(tparm);
					// �ж��Ƿ������ɹ�
					if (null == returnresult || returnresult.getErrCode() < 0) {
						// ʧ�ܼ�¼������Ϣ
						result_desc = "FAILED save date I/PHA_DOSE" + "/"
								+ Endresult[3].trim();
					} else {
						result_desc = "SUCCESS I/PHA_DOSE" + "/"
								+ Endresult[3].trim();
						try {
							// ����webservice�ӿڿ��ܳ����쳣�����񲢼�¼��־
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"PHADOSE_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}

				}
			}
			// �ж��Ƿ�Ϊ�޸�
			if (Endresult[2].trim().equals("UPDATE")) {
				// ���ܳ����쳣��������������¼��־��Ϣ
				try {
					tparm.setData("DOSE_CODE", Endresult[3].trim());
					tparm.setData("DOSE_CHN_DESC", Endresult[4].trim());
					tparm.setData("ENG_DESC", Endresult[5].trim());
					tparm.setData("PY1", Endresult[6].trim());
					tparm.setData("PY2", Endresult[7].trim());
					tparm.setData("DESCRIPTION", Endresult[8].trim());
					tparm.setData("DOSE_TYPE", Endresult[9].trim());
					tparm.setData("SEQ", Endresult[10].trim());

				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String U/PHA_DOSE" + "/"
							+ Endresult[3].trim();

					System.out.println("---FAILED  U/PHA_DOSE" + "/"
							+ Endresult[3].trim() + result);

				}
				// �ж�ǰ���Ƿ�����쳣�����ж��Ƿ���ִ�����ݿ�����ı�Ҫ
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnUpdatePhaDose(tparm);
					// �ж��Ƿ���³ɹ�
					if (null == returnresult || returnresult.getErrCode() < 0) {

						result_desc = "FAILED update date U/PHA_DOSE" + "/"
								+ Endresult[3].trim();

					} else {
						// �ɹ����¼��־
						result_desc = "SUCCESS U/PHA_DOSE" + "/"
								+ Endresult[3].trim();

						// ����webservice�ӿڿ��ܳ����쳣�����񲢼�¼��־
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"PHADOSE_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}

					}
				}
			}
			// �ж��Ƿ�Ϊɾ��
			if (Endresult[2].trim().equals("DELETE")) {
				// ���ܳ����쳣��������������¼��־��Ϣ
				try {
					tparm.setData("DOSE_CODE", Endresult[3].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/PHA_DOSE" + "/"
							+ Endresult[3].trim();

					System.out.println("---FAILED  D/PHA_DOSE" + "/"
							+ Endresult[3].trim() + result);

				}
				// �ж�ǰ���Ƿ�����쳣�����ж��Ƿ���ִ�����ݿ�����ı�Ҫ
				if (operation) {

					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnDeletePhaDose(tparm);
					// �ж��Ƿ�ɾ���ɹ�
					if (null == returnresult || returnresult.getErrCode() < 0) {

						result_desc = "FAILED delete date D/PHA_DOSE" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS D/PHA_DOSE" + "/"
								+ Endresult[3].trim();
						// ����webservice�ӿ���������쳣�����񲢼�¼��־
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"PHADOSE_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}
			}

			// ��¼log
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);
		}

	}

	/*
	 * ͬ��SYS_BED��
	 */
	private void SysBedService(List<String> _getModifyInf__return, String arg0,
			String arg1) {

		for (int j = 0; j < _getModifyInf__return.size(); j++) {

			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			// �ж��Ƿ�Ϊ����
			if (Endresult[2].trim().equals("INSERT")) {
				// ���ܳ����쳣��������������¼��־��Ϣ
				try {
					tparm.setData("BED_NO", Endresult[3].trim());
					tparm.setData("BED_NO_DESC", Endresult[4].trim());
					tparm.setData("PY1", Endresult[5].trim());
					tparm.setData("PY2", Endresult[6].trim());
					tparm.setData("DESCRIPTION", Endresult[7].trim());
					tparm.setData("ROOM_CODE", Endresult[8].trim());
					tparm.setData("STATION_CODE", Endresult[9].trim());
					tparm.setData("SEQ", Endresult[10].trim());
					tparm.setData("REGION_CODE", Endresult[11].trim());
					tparm.setData("BED_CLASS_CODE", Endresult[12].trim());
					tparm.setData("BED_TYPE_CODE", Endresult[13].trim());
					tparm.setData("ACTIVE_FLG", Endresult[14].trim());
					tparm.setData("APPT_FLG", Endresult[15].trim());
					tparm.setData("ALLO_FLG", Endresult[16].trim());
					tparm.setData("BED_OCCU_FLG", Endresult[17].trim());
					tparm.setData("RESERVE_BED_FLG", Endresult[18].trim());
					tparm.setData("SEX_CODE", Endresult[19].trim());
					tparm.setData("OCCU_RATE_FLG", Endresult[20].trim());
					tparm.setData("DR_APPROVE_FLG", Endresult[21].trim());
					tparm.setData("BABY_BED_FLG", Endresult[22].trim());
					tparm.setData("HTL_FLG", Endresult[23].trim());
					tparm.setData("ADM_TYPE", Endresult[24].trim());
					tparm.setData("MR_NO", Endresult[25].trim());
					tparm.setData("CASE_NO", Endresult[26].trim());
					tparm.setData("IPD_NO", Endresult[27].trim());
					tparm.setData("DEPT_CODE", Endresult[28].trim());
					tparm.setData("BED_STATUS", Endresult[29].trim());
					tparm.setData("ENG_DESC", Endresult[30].trim());

					tparm.setData("STATION_CODE", Endresult[9].trim());

				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/SYS_BED" + "/"
							+ Endresult[3].trim();

					System.out.println("---FAILED  I/SYS_BED" + "/"
							+ Endresult[3].trim() + result);

				}
				// �ж�ǰ���Ƿ�����쳣�����ж��Ƿ���ִ�����ݿ�����ı�Ҫ
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnSaveSysBed(tparm);

					if (null == returnresult || returnresult.getErrCode() < 0) {

						result_desc = "FAILED save date I/SYS_BED" + "/"
								+ Endresult[3].trim();
					} else {
						// �ɹ����¼��־
						result_desc = "SUCCESS I/SYS_BED" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"BED_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + "confirm faield";
						}
					}
				}
			}
			// �ж��Ƿ�Ϊ����
			if (Endresult[2].trim().equals("UPDATE")) {
				// ���ܳ����쳣��������������¼��־��Ϣ
				try {
					tparm.setData("BED_NO", Endresult[3].trim());
					tparm.setData("BED_NO_DESC", Endresult[4].trim());
					tparm.setData("PY1", Endresult[5].trim());
					tparm.setData("PY2", Endresult[6].trim());
					tparm.setData("DESCRIPTION", Endresult[7].trim());
					tparm.setData("ROOM_CODE", Endresult[8].trim());
					tparm.setData("STATION_CODE", Endresult[9].trim());
					tparm.setData("SEQ", Endresult[10].trim());
					tparm.setData("REGION_CODE", Endresult[11].trim());
					tparm.setData("BED_CLASS_CODE", Endresult[12].trim());
					tparm.setData("BED_TYPE_CODE", Endresult[13].trim());
					tparm.setData("ACTIVE_FLG", Endresult[14].trim());
					tparm.setData("APPT_FLG", Endresult[15].trim());
					tparm.setData("ALLO_FLG", Endresult[16].trim());
					tparm.setData("BED_OCCU_FLG", Endresult[17].trim());
					tparm.setData("RESERVE_BED_FLG", Endresult[18].trim());
					tparm.setData("SEX_CODE", Endresult[19].trim());
					tparm.setData("OCCU_RATE_FLG", Endresult[20].trim());
					tparm.setData("DR_APPROVE_FLG", Endresult[21].trim());
					tparm.setData("BABY_BED_FLG", Endresult[22].trim());
					tparm.setData("HTL_FLG", Endresult[23].trim());
					tparm.setData("ADM_TYPE", Endresult[24].trim());
					tparm.setData("MR_NO", Endresult[25].trim());
					tparm.setData("CASE_NO", Endresult[26].trim());
					tparm.setData("IPD_NO", Endresult[27].trim());
					tparm.setData("DEPT_CODE", Endresult[28].trim());
					tparm.setData("BED_STATUS", Endresult[29].trim());
					tparm.setData("ENG_DESC", Endresult[30].trim());

				} catch (Exception e) {

					operation = false;
					result_desc = "FAILED prase String U/SYS_BED" + "/"
							+ Endresult[3].trim();

					System.out.println("---FAILED  U/SYS_BED" + "/"
							+ Endresult[3].trim() + result);

				}
				// �ж�ǰ���Ƿ�����쳣�����ж��Ƿ���ִ�����ݿ�����ı�Ҫ
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnUpdateSysBed(tparm);
					// �ж��Ƿ���гɹ�
					if (null == returnresult || returnresult.getErrCode() < 0) {

						result_desc = "FAILED update date U/SYS_BED" + "/"
								+ Endresult[3].trim();

					} else {
						// �ɹ����¼��־
						result_desc = "SUCCESS U/SYS_BED" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"BED_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + "confirm faield";
						}
					}

				}
			}
			// �ж��Ƿ�Ϊɾ��
			if (Endresult[2].trim().equals("DELETE")) {
				// ���ܳ����쳣��������������¼��־��Ϣ
				try {
					tparm.setData("BED_NO", Endresult[3].trim());

				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/SYS_BED" + "/"
							+ Endresult[3].trim();

					System.out.println("---FAILED  D/SYS_BED" + "/"
							+ Endresult[3].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnDeleteSysBed(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {

						result_desc = "FAILED delete date D/SYS_BED" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS D/SYS_BED" + "/"
								+ Endresult[3].trim();

						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"BED_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + "confirm faield";
						}
					}
				}

			}

			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);

		}
	}

	/*
	 * ͬ��SYS_BED_TYPE��
	 */

	private void SysBedTypeService(List<String> _getModifyInf__return,
			String arg0, String arg1) {

		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			// �ж��Ƿ�Ϊ����
			if (Endresult[2].trim().equals("INSERT")) {
				// ���ܳ����쳣��������������¼��־��Ϣ
				try {
					tparm.setData("BED_TYPE_CODE", Endresult[3].trim());
					tparm.setData("BEDTYPE_DESC", Endresult[4].trim());
					tparm.setData("ENNAME", Endresult[5].trim());
					tparm.setData("PY1", Endresult[6].trim());
					tparm.setData("PY2", Endresult[7].trim());
					tparm.setData("SEQ", Endresult[8].trim());
					tparm.setData("DESCRIPTION", Endresult[9].trim());
					tparm.setData("LAB_DISCNT_FLG", Endresult[10].trim());
					tparm.setData("ISOLATION_FLG", Endresult[11].trim());
					tparm.setData("BURN_FLG", Endresult[12].trim());
					tparm.setData("PEDIATRIC_FLG", Endresult[13].trim());
					tparm.setData("OBSERVATION_FLG", Endresult[14].trim());
					tparm.setData("TRANSPLANT_FLG", Endresult[15].trim());
					tparm.setData("ICU_FLG", Endresult[16].trim());
					tparm.setData("CCU_FLG", Endresult[17].trim());
					tparm.setData("BC_FLG", Endresult[18].trim());

				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/SYS_BED_TYPE" + "/"
							+ Endresult[3].trim();

					System.out.println("---FAILED  I/SYS_BED_TYPE" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {

					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnSaveSysBedType(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {

						result_desc = "FAILED save date I/SYS_BED_TYPE" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS I/SYS_BED_TYPE" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"BEDTYPE_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}

				}
			}
			// �ж��Ƿ�Ϊ����
			if (Endresult[2].trim().equals("UPDATE")) {
				// ���ܳ����쳣��������������¼��־��Ϣ
				try {
					tparm.setData("BED_TYPE_CODE", Endresult[3].trim());
					tparm.setData("BEDTYPE_DESC", Endresult[4].trim());
					tparm.setData("ENNAME", Endresult[5].trim());
					tparm.setData("PY1", Endresult[6].trim());
					tparm.setData("PY2", Endresult[7].trim());
					tparm.setData("SEQ", Endresult[8].trim());
					tparm.setData("DESCRIPTION", Endresult[9].trim());
					tparm.setData("LAB_DISCNT_FLG", Endresult[10].trim());
					tparm.setData("ISOLATION_FLG", Endresult[11].trim());
					tparm.setData("BURN_FLG", Endresult[12].trim());
					tparm.setData("PEDIATRIC_FLG", Endresult[13].trim());
					tparm.setData("OBSERVATION_FLG", Endresult[14].trim());
					tparm.setData("TRANSPLANT_FLG", Endresult[15].trim());
					tparm.setData("ICU_FLG", Endresult[16].trim());
					tparm.setData("CCU_FLG", Endresult[17].trim());
					tparm.setData("BC_FLG", Endresult[18].trim());

				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String U/SYS_BED_TYPE" + "/"
							+ Endresult[3].trim();

					System.out.println("---FAILED  U/SYS_BED_TYPE" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnUpdateSysBedType(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {

						result_desc = "FAILED update date U/SYS_BED_TYPE" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS U/SYS_BED_TYPE" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"BEDTYPE_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}

			}
			// �ж��Ƿ�Ϊɾ��
			if (Endresult[2].trim().equals("DELETE")) {
				// ���ܳ����쳣��������������¼��־��Ϣ
				try {
					tparm.setData("BED_TYPE_CODE", Endresult[3].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/SYS_BED_TYPE" + "/"
							+ Endresult[3].trim();

					System.out.println("---FAILED  D/SYS_BED_TYPE" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnDeleteSysBedType(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {

						result_desc = "FAILED delete date D/SYS_BED_TYPE" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS D/SYS_BED_TYPE" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"BEDTYPE_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}
			}
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);
		}
	}

	/*
	 * ͬ��SYS_CATEGORY��
	 */
	private void SysCategoryService(List<String> _getModifyInf__return,
			String arg0, String arg1) {

		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			if (Endresult[2].trim().equals("INSERT")) {
				try {
					tparm.setData("RULE_TYPE", Endresult[3].trim());
					tparm.setData("CATEGORY_CODE", Endresult[4].trim());
					tparm.setData("CATEGORY_CHN_DESC", Endresult[5].trim());
					tparm.setData("CATEGORY_ENG_DESC", Endresult[6].trim());
					tparm.setData("DETAIL_FLG", Endresult[7].trim());
					tparm.setData("PY1", Endresult[8].trim());
					tparm.setData("PY2", Endresult[9].trim());
					tparm.setData("SEQ", Endresult[10].trim());
					tparm.setData("DESCRIPTION", Endresult[11].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/SYS_CATEGORY" + "/"
							+ Endresult[4].trim();

					System.out.println("---FAILED  I/SYS_CATEGORY" + "/"
							+ Endresult[4].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnSaveSysCategory(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {

						result_desc = "FAILED save date I/SYS_CATEGORY" + "/"
								+ Endresult[4].trim();

					} else {
						result_desc = "SUCCESS I/SYS_CATEGORY" + "/"
								+ Endresult[4].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"CATEGORY_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}

					}

				}
			}
			if (Endresult[2].trim().equals("UPDATE")) {
				try {
					tparm.setData("RULE_TYPE", Endresult[3].trim());
					tparm.setData("RULE_TYPE1", Endresult[4].trim());
					tparm.setData("CATEGORY_CODE", Endresult[5].trim());
					tparm.setData("CATEGORY_CODE1", Endresult[6].trim());

					tparm.setData("CATEGORY_CHN_DESC", Endresult[7].trim());
					tparm.setData("CATEGORY_ENG_DESC", Endresult[8].trim());
					tparm.setData("DETAIL_FLG", Endresult[9].trim());
					tparm.setData("PY1", Endresult[10].trim());
					tparm.setData("PY2", Endresult[11].trim());
					tparm.setData("SEQ", Endresult[12].trim());
					tparm.setData("DESCRIPTION", Endresult[13].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String U/SYS_CATEGORY" + "/"
							+ Endresult[6].trim();

					System.out.println("---FAILED  U/SYS_CATEGORY" + "/"
							+ Endresult[6].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnUpdateSysCategory(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {

						result_desc = "FAILED update date U/SYS_CATEGORY" + "/"
								+ Endresult[6].trim();

					} else {
						result_desc = "SUCCESS U/SYS_CATEGORY" + "/"
								+ Endresult[6].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"CATEGORY_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}

					}

				}
			}
			if (Endresult[2].trim().equals("DELETE")) {
				try {
					tparm.setData("CATEGORY_CODE", Endresult[4].trim());
					tparm.setData("RULE_TYPE", Endresult[3].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/SYS_CATEGORY" + "/"
							+ Endresult[4].trim();

					System.out.println("---FAILED  D/SYS_CATEGORY" + "/"
							+ Endresult[4].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnDeleteSysCategory(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED delete date D/SYS_CATEGORY" + "/"
								+ Endresult[4].trim();

					} else {
						result_desc = "SUCCESS D/SYS_CATEGORY" + "/"
								+ Endresult[4].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"CATEGORY_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}
			}
			// ��¼log
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);
		}

	}

	/*
	 * ͬ��SYS_COST_CENTER��
	 */
	private void SysCostCenterService(List<String> _getModifyInf__return,
			String arg0, String arg1) {

		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			if (Endresult[2].trim().equals("INSERT")) {
				try {
					tparm.setData("COST_CENTER_CODE", Endresult[3].trim());
					tparm.setData("COST_CENTER_CHN_DESC", Endresult[4].trim());
					tparm.setData("COST_CENTER_ENG_DESC", Endresult[5].trim());
					tparm.setData("COST_CENTER_ABS_DESC", Endresult[6].trim());

					tparm.setData("PY1", Endresult[7].trim());
					tparm.setData("PY2", Endresult[8].trim());
					tparm.setData("SEQ", Endresult[9].trim());
					tparm.setData("DESCRIPTION", Endresult[10].trim());
					tparm.setData("FINAL_FLG", Endresult[11].trim());
					tparm.setData("REGION_CODE", Endresult[12].trim());
					tparm.setData("DEPT_GRADE", Endresult[13].trim());
					tparm.setData("CLASSIFY", Endresult[14].trim());
					tparm.setData("DEPT_CAT1", Endresult[15].trim());
					tparm.setData("OPD_FIT_FLG", Endresult[16].trim());
					tparm.setData("EMG_FIT_FLG", Endresult[17].trim());
					tparm.setData("IPD_FIT_FLG", Endresult[18].trim());
					tparm.setData("HRM_FIT_FLG", Endresult[19].trim());
					tparm.setData("DEFAULT_TERM_NO", Endresult[20].trim());
					tparm.setData("DEFAULT_PRINTER_NO", Endresult[21].trim());
					tparm.setData("STATISTICS_FLG", Endresult[22].trim());
					tparm.setData("ACTIVE_FLG", Endresult[23].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/SYS_COST_CENTER" + "/"
							+ Endresult[3].trim();

					System.out.println("---FAILED  I/SYS_COST_CENTER" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {

					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnSaveSysCostCenter(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED save date I/SYS_COST_CENTER"
								+ "/" + Endresult[3].trim();

					} else {
						result_desc = "SUCCESS I/SYS_COST_CENTER" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"COST_CENTER_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";

						}
					}

				}
			}
			if (Endresult[2].trim().equals("UPDATE")) {
				try {
					tparm.setData("COST_CENTER_CODE", Endresult[3].trim());
					tparm.setData("COST_CENTER_CODE1", Endresult[4].trim());
					tparm.setData("COST_CENTER_CHN_DESC", Endresult[5].trim());
					tparm.setData("COST_CENTER_ENG_DESC", Endresult[6].trim());
					tparm.setData("COST_CENTER_ABS_DESC", Endresult[7].trim());
					tparm.setData("PY1", Endresult[8].trim());
					tparm.setData("PY2", Endresult[9].trim());
					tparm.setData("SEQ", Endresult[10].trim());
					tparm.setData("DESCRIPTION", Endresult[11].trim());
					tparm.setData("FINAL_FLG", Endresult[12].trim());
					tparm.setData("REGION_CODE", Endresult[13].trim());
					tparm.setData("DEPT_GRADE", Endresult[14].trim());
					tparm.setData("CLASSIFY", Endresult[15].trim());
					tparm.setData("DEPT_CAT1", Endresult[16].trim());
					tparm.setData("OPD_FIT_FLG", Endresult[17].trim());
					tparm.setData("EMG_FIT_FLG", Endresult[18].trim());
					tparm.setData("IPD_FIT_FLG", Endresult[19].trim());
					tparm.setData("HRM_FIT_FLG", Endresult[20].trim());
					tparm.setData("DEFAULT_TERM_NO", Endresult[21].trim());
					tparm.setData("DEFAULT_PRINTER_NO", Endresult[22].trim());
					tparm.setData("STATISTICS_FLG", Endresult[23].trim());
					tparm.setData("ACTIVE_FLG", Endresult[24].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String U/SYS_COST_CENTER" + "/"
							+ Endresult[4].trim();

					System.out.println("---FAILED  U/SYS_COST_CENTER" + "/"
							+ Endresult[4].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnUpdateSysCostCenter(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {

						result_desc = "FAILED update date U/SYS_COST_CENTER"
								+ "/" + Endresult[4].trim();
					} else {
						result_desc = "SUCCESS U/SYS_COST_CENTER" + "/"
								+ Endresult[4].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"COST_CENTER_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}

					}
				}
			}
			if (Endresult[2].trim().equals("DELETE")) {
				try {
					tparm.setData("COST_CENTER_CODE", Endresult[3].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/SYS_COST_CENTER" + "/"
							+ Endresult[3].trim();

					System.out.println("---FAILED  D/SYS_COST_CENTER" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnDeleteSysCostCenter(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED delete date D/SYS_COST_CENTER"
								+ "/" + Endresult[3].trim();

					} else {
						result_desc = "SUCCESS D/SYS_COST_CENTER" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"COST_CENTER_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}

				}
			}
			// ��¼log
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);
		}
	}

	/*
	 * ͬ��SYS_CTRLDRUGCLASS��
	 */
	private void SysCtrldrugclassService(List<String> _getModifyInf__return,
			String arg0, String arg1) {

		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			if (Endresult[2].trim().equals("INSERT")) {
				try {
					tparm.setData("CTRLDRUGCLASS_CODE", Endresult[3].trim());
					tparm
							.setData("CTRLDRUGCLASS_CHN_DESC", Endresult[4]
									.trim());
					tparm
							.setData("CTRLDRUGCLASS_ENG_DESC", Endresult[5]
									.trim());
					tparm.setData("TAKE_DAYS", Endresult[6].trim());
					tparm.setData("PY1", Endresult[7].trim());
					tparm.setData("PY2", Endresult[8].trim());
					tparm.setData("SEQ", Endresult[9].trim());
					tparm.setData("DESCRIPTION", Endresult[10].trim());
					tparm.setData("PRN_TYPE_CODE", Endresult[11].trim());
					tparm.setData("PRN_TYPE_DESC", Endresult[12].trim());
					tparm.setData("PRNSPCFORM_FLG", Endresult[13].trim());
					tparm.setData("CTRL_FLG", Endresult[14].trim());
					tparm.setData("NARCOTIC_FLG", Endresult[15].trim());
					tparm.setData("TOXICANT_FLG", Endresult[16].trim());
					tparm.setData("PSYCHOPHA1_FLG", Endresult[17].trim());
					tparm.setData("RADIA_FLG", Endresult[18].trim());
					tparm.setData("TEST_DRUG_FLG", Endresult[19].trim());
					tparm.setData("ANTISEPTIC_FLG", Endresult[20].trim());
					tparm.setData("ANTIBIOTIC_FLG", Endresult[21].trim());
					tparm.setData("TPN_FLG", Endresult[22].trim());
					tparm.setData("PSYCHOPHA2_FLG", Endresult[23].trim());

				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/SYS_CTRLDRUGCLASS"
							+ "/" + Endresult[3].trim();

					System.out.println("---FAILED I/SYS_CTRLDRUGCLASS" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnSaveSysCtrldrugclass(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {

						result_desc = "FAILED save date I/SYS_CTRLDRUGCLASS"
								+ "/" + Endresult[3].trim();

					} else {
						result_desc = "SUCCESS I/SYS_CTRLDRUGCLASS" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"CTRLDRUGCLASS_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}
			}
			if (Endresult[2].trim().equals("UPDATE")) {
				try {
					tparm.setData("CTRLDRUGCLASS_CODE", Endresult[3].trim());
					tparm.setData("CTRLDRUGCLASS_CODE1", Endresult[4].trim());
					tparm
							.setData("CTRLDRUGCLASS_CHN_DESC", Endresult[5]
									.trim());
					tparm
							.setData("CTRLDRUGCLASS_ENG_DESC", Endresult[6]
									.trim());
					tparm.setData("TAKE_DAYS", Endresult[7].trim());
					tparm.setData("PY1", Endresult[8].trim());
					tparm.setData("PY2", Endresult[9].trim());
					tparm.setData("SEQ", Endresult[10].trim());
					tparm.setData("DESCRIPTION", Endresult[11].trim());
					tparm.setData("PRN_TYPE_CODE", Endresult[12].trim());
					tparm.setData("PRN_TYPE_DESC", Endresult[13].trim());
					tparm.setData("PRNSPCFORM_FLG", Endresult[14].trim());
					tparm.setData("CTRL_FLG", Endresult[15].trim());
					tparm.setData("NARCOTIC_FLG", Endresult[16].trim());
					tparm.setData("TOXICANT_FLG", Endresult[17].trim());
					tparm.setData("PSYCHOPHA1_FLG", Endresult[18].trim());
					tparm.setData("RADIA_FLG", Endresult[19].trim());
					tparm.setData("TEST_DRUG_FLG", Endresult[20].trim());
					tparm.setData("ANTISEPTIC_FLG", Endresult[21].trim());
					tparm.setData("ANTIBIOTIC_FLG", Endresult[22].trim());
					tparm.setData("TPN_FLG", Endresult[23].trim());
					tparm.setData("PSYCHOPHA2_FLG", Endresult[24].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String U/SYS_CTRLDRUGCLASS"
							+ "/" + Endresult[4].trim();

					System.out.println("---FAILED U/SYS_CTRLDRUGCLASS" + "/"
							+ Endresult[4].trim() + result);

				}

				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnUpdateSysCtrldrugclass(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {

						result_desc = "FAILED update date U/SYS_CTRLDRUGCLASS"
								+ "/" + Endresult[4].trim();

					} else {
						result_desc = "SUCCESS U/SYS_CTRLDRUGCLASS" + "/"
								+ Endresult[4].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"CTRLDRUGCLASS_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}
			}
			if (Endresult[2].trim().equals("DELETE")) {
				try {
					tparm.setData("CTRLDRUGCLASS_CODE", Endresult[3].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/SYS_CTRLDRUGCLASS"
							+ "/" + Endresult[3].trim();

					System.out.println("---FAILED D/SYS_CTRLDRUGCLASS" + "/"
							+ Endresult[3].trim() + result);
				}

				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnDeleteSysCtrldrugclass(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED delete date D/SYS_CTRLDRUGCLASS"
								+ "/" + Endresult[3].trim();

					} else {
						result_desc = "SUCCESS D/SYS_CTRLDRUGCLASS" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"CTRLDRUGCLASS_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}
			}
			// ��¼log
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);
		}

	}

	/*
	 * ͬ��SYS_CTZ��
	 */
	private void SysCtzService(List<String> _getModifyInf__return, String arg0,
			String arg1) {

		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			if (Endresult[2].trim().equals("INSERT")) {
				try {
					tparm.setData("CTZ_CODE", Endresult[3].trim());
					tparm.setData("CTZ_DESC", Endresult[4].trim());
					tparm.setData("ENG_DESC", Endresult[5].trim());
					tparm.setData("DESCRIPT", Endresult[6].trim());
					tparm.setData("PY1", Endresult[7].trim());
					tparm.setData("PY2", Endresult[8].trim());
					tparm.setData("SEQ", Endresult[9].trim());
					tparm.setData("NHI_COMPANY_CODE", Endresult[10].trim());
					tparm.setData("MAIN_CTZ_FLG", Endresult[11].trim());
					tparm.setData("NHI_CTZ_FLG", Endresult[12].trim());
					tparm.setData("MRCTZ_UPD_FLG", Endresult[13].trim());
					tparm.setData("DEF_CTZ_FLG", Endresult[14].trim());
					tparm.setData("MRO_CTZ", Endresult[15].trim());
					tparm.setData("STA2_CODE", Endresult[16].trim());
					tparm.setData("STA1_CODE", Endresult[17].trim());
					tparm.setData("NHI_NO", Endresult[18].trim());

				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/SYS_CTZ" + "/"
							+ Endresult[3].trim();

					System.out.println("---FAILED I/SYS_CTZ" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnSaveSysCtz(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED save date I/SYS_CTZ" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS I/SYS_CTZ" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"CTZ_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}

					}

				}
			}
			if (Endresult[2].trim().equals("UPDATE")) {
				try {
					tparm.setData("CTZ_CODE", Endresult[3].trim());
					tparm.setData("CTZ_CODE1", Endresult[4].trim());
					tparm.setData("CTZ_DESC", Endresult[5].trim());
					tparm.setData("ENG_DESC", Endresult[6].trim());
					tparm.setData("DESCRIPT", Endresult[7].trim());
					tparm.setData("PY1", Endresult[8].trim());
					tparm.setData("PY2", Endresult[9].trim());
					tparm.setData("SEQ", Endresult[10].trim());
					tparm.setData("NHI_COMPANY_CODE", Endresult[11].trim());
					tparm.setData("MAIN_CTZ_FLG", Endresult[12].trim());
					tparm.setData("NHI_CTZ_FLG", Endresult[13].trim());
					tparm.setData("MRCTZ_UPD_FLG", Endresult[14].trim());
					tparm.setData("DEF_CTZ_FLG", Endresult[15].trim());
					tparm.setData("MRO_CTZ", Endresult[16].trim());
					tparm.setData("STA2_CODE", Endresult[17].trim());
					tparm.setData("STA1_CODE", Endresult[18].trim());
					tparm.setData("NHI_NO", Endresult[19].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String U/SYS_CTZ" + "/"
							+ Endresult[4].trim();

					System.out.println("---FAILED U/SYS_CTZ" + "/"
							+ Endresult[4].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnUpdateSysCtz(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date U/SYS_CTZ" + "/"
								+ Endresult[4].trim();
					} else {

						result_desc = "SUCCESS U/SYS_CTZ" + "/"
								+ Endresult[4].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"CTZ_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}

					}

				}
			}
			if (Endresult[2].trim().equals("DELETE")) {
				try {
					tparm.setData("CTZ_CODE", Endresult[3].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/SYS_CTZ" + "/"
							+ Endresult[3].trim();

					System.out.println("---FAILED D/SYS_CTZ" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnDeleteSysCtz(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED delete date D/SYS_CTZ" + "/"
								+ Endresult[3].trim();
					} else {

						result_desc = "SUCCESS D/SYS_CTZ" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"CTZ_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}

					}
				}

			}
			// ��¼log
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);
		}

	}

	/*
	 * ͬ��SYS_DIAGNOSIS��
	 */
	private void SysDiagnosisService(List<String> _getModifyInf__return,
			String arg0, String arg1) {

		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			if (Endresult[2].trim().equals("INSERT")) {
				try {
					tparm.setData("ICD_TYPE", Endresult[3].trim());
					tparm.setData("ICD_CODE", Endresult[4].trim());
					tparm.setData("ICD_CHN_DESC", Endresult[5].trim());
					tparm.setData("ICD_ENG_DESC", Endresult[6].trim());
					tparm.setData("MAIN_DIAG_FLG", Endresult[7].trim());
					tparm.setData("PY1", Endresult[8].trim());
					tparm.setData("PY2", Endresult[9].trim());
					tparm.setData("SEQ", Endresult[10].trim());
					tparm.setData("DESCRIPTION", Endresult[11].trim());
					tparm.setData("SYNDROME_FLG", Endresult[12].trim());
					tparm.setData("MDC_CODE", Endresult[13].trim());
					tparm.setData("CCMD_CODE", Endresult[14].trim());
					tparm.setData("CAT_FLG", Endresult[15].trim());
					tparm.setData("STANDARD_DAYS", Endresult[16].trim());
					tparm.setData("CHLR_FLG", Endresult[17].trim());
					tparm.setData("DISEASETYPE_CODE", Endresult[18].trim());
					tparm.setData("MR_CODE", Endresult[19].trim());
					tparm.setData("CHRONIC_FLG", Endresult[20].trim());
					tparm.setData("START_AGE", Endresult[21].trim());
					tparm.setData("LIMIT_DEPT_CODE", Endresult[22].trim());
					tparm.setData("LIMIT_SEX_CODE", Endresult[23].trim());
					tparm.setData("END_AGE", Endresult[24].trim());
					tparm.setData("AVERAGE_FEE", Endresult[25].trim());
					tparm.setData("STA2_CODE", Endresult[26].trim());
					tparm.setData("STA1_CODE", Endresult[27].trim());

				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/SYS_DIAGNOSIS" + "/"
							+ Endresult[4].trim();
					System.out.println("---FAILED I/SYS_DIAGNOSIS" + "/"
							+ Endresult[4].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnSaveSysDiagnosis(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED save date I/SYS_DIAGNOSIS" + "/"
								+ Endresult[4].trim();

					} else {
						result_desc = "SUCCESS I/SYS_DIAGNOSIS" + "/"
								+ Endresult[4].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"DIAGNOSIS_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}

					}

				}
			}
			if (Endresult[2].trim().equals("UPDATE")) {
				try {
					tparm.setData("ICD_TYPE", Endresult[3].trim());
					tparm.setData("ICD_TYPE1", Endresult[4].trim());
					tparm.setData("ICD_CODE", Endresult[5].trim());
					tparm.setData("ICD_CODE1", Endresult[6].trim());
					tparm.setData("ICD_CHN_DESC", Endresult[7].trim());
					tparm.setData("ICD_ENG_DESC", Endresult[8].trim());
					tparm.setData("MAIN_DIAG_FLG", Endresult[9].trim());
					tparm.setData("PY1", Endresult[10].trim());
					tparm.setData("PY2", Endresult[11].trim());
					tparm.setData("SEQ", Endresult[12].trim());
					tparm.setData("DESCRIPTION", Endresult[13].trim());
					tparm.setData("SYNDROME_FLG", Endresult[14].trim());
					tparm.setData("MDC_CODE", Endresult[15].trim());
					tparm.setData("CCMD_CODE", Endresult[16].trim());
					tparm.setData("CAT_FLG", Endresult[17].trim());
					tparm.setData("STANDARD_DAYS", Endresult[18].trim());
					tparm.setData("CHLR_FLG", Endresult[19].trim());
					tparm.setData("DISEASETYPE_CODE", Endresult[20].trim());
					tparm.setData("MR_CODE", Endresult[21].trim());
					tparm.setData("CHRONIC_FLG", Endresult[22].trim());
					tparm.setData("START_AGE", Endresult[23].trim());
					tparm.setData("LIMIT_DEPT_CODE", Endresult[24].trim());
					tparm.setData("LIMIT_SEX_CODE", Endresult[25].trim());
					tparm.setData("END_AGE", Endresult[26].trim());
					tparm.setData("AVERAGE_FEE", Endresult[27].trim());
					tparm.setData("STA2_CODE", Endresult[28].trim());
					tparm.setData("STA1_CODE", Endresult[29].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String U/SYS_DIAGNOSIS" + "/"
							+ Endresult[6].trim();
					System.out.println("---FAILED U/SYS_DIAGNOSIS" + "/"
							+ Endresult[6].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnUpdateSysDiagnosis(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {

						result_desc = "FAILED update date U/SYS_DIAGNOSIS"
								+ "/" + Endresult[6].trim();

					} else {
						result_desc = "SUCCESS U/SYS_DIAGNOSIS" + "/"
								+ Endresult[6].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"DIAGNOSIS_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}

					}

				}
			}
			if (Endresult[2].trim().equals("DELETE")) {
				try {
					tparm.setData("ICD_TYPE", Endresult[3].trim());
					tparm.setData("ICD_CODE", Endresult[4].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/SYS_DIAGNOSIS" + "/"
							+ Endresult[4].trim();
					System.out.println("---FAILED D/SYS_DIAGNOSIS" + "/"
							+ Endresult[4].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnDeleteSysDiagnosis(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED delete date D/SYS_DIAGNOSIS"
								+ "/" + Endresult[3].trim() + "/"
								+ Endresult[4].trim();

					} else {
						result_desc = "SUCCESS D/SYS_DIAGNOSIS" + "/"
								+ Endresult[4].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"DIAGNOSIS_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}

					}
				}

			}
			// ��¼log
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);

		}
	}

	/*
	 * ͬ��SYS_MANUFACTURER��
	 */
	private void SysManufacturerService(List<String> _getModifyInf__return,
			String arg0, String arg1) {

		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			if (Endresult[2].trim().equals("INSERT")) {
				try {
					tparm.setData("MAN_CODE", Endresult[3].trim());
					tparm.setData("MAN_CHN_DESC", Endresult[4].trim());
					tparm.setData("MAN_ENG_DESC", Endresult[5].trim());
					tparm.setData("MAN_ABS_DESC", Endresult[6].trim());

					tparm.setData("PY1", Endresult[7].trim());
					tparm.setData("PY2", Endresult[8].trim());
					tparm.setData("SEQ", Endresult[9].trim());
					tparm.setData("DESCRIPTION", Endresult[10].trim());
					tparm.setData("NATIONAL_CODE", Endresult[11].trim());
					tparm.setData("POST_CODE", Endresult[12].trim());
					tparm.setData("ADDRESS", Endresult[13].trim());
					tparm.setData("TEL", Endresult[14].trim());
					tparm.setData("FAX", Endresult[15].trim());
					tparm.setData("WEBSITE", Endresult[16].trim());
					tparm.setData("E_MAIL", Endresult[17].trim());
					tparm.setData("PHA_FLG", Endresult[18].trim());
					tparm.setData("MAT_FLG", Endresult[19].trim());
					tparm.setData("DEV_FLG", Endresult[20].trim());
					tparm.setData("OTHER_FLG", Endresult[21].trim());

				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/SYS_MANUFACTURER"
							+ "/" + Endresult[3].trim();
					System.out.println("---FAILED I/SYS_MANUFACTURER" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnSaveSysManufacturer(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED save date I/SYS_MANUFACTURER"
								+ "/" + Endresult[3].trim();

					} else {
						result_desc = "SUCCESS I/SYS_MANUFACTURER" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"MANUFACTURER_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}
			}
			if (Endresult[2].trim().equals("UPDATE")) {
				try {
					tparm.setData("MAN_CODE", Endresult[3].trim());
					tparm.setData("MAN_CODE1", Endresult[4].trim());
					tparm.setData("MAN_CHN_DESC", Endresult[5].trim());
					tparm.setData("MAN_ENG_DESC", Endresult[6].trim());
					tparm.setData("MAN_ABS_DESC", Endresult[7].trim());
					tparm.setData("PY1", Endresult[8].trim());
					tparm.setData("PY2", Endresult[9].trim());
					tparm.setData("SEQ", Endresult[10].trim());
					tparm.setData("DESCRIPTION", Endresult[11].trim());
					tparm.setData("NATIONAL_CODE", Endresult[12].trim());
					tparm.setData("POST_CODE", Endresult[13].trim());
					tparm.setData("ADDRESS", Endresult[14].trim());
					tparm.setData("TEL", Endresult[15].trim());
					tparm.setData("FAX", Endresult[16].trim());
					tparm.setData("WEBSITE", Endresult[17].trim());
					tparm.setData("E_MAIL", Endresult[18].trim());
					tparm.setData("PHA_FLG", Endresult[19].trim());
					tparm.setData("MAT_FLG", Endresult[20].trim());
					tparm.setData("DEV_FLG", Endresult[21].trim());
					tparm.setData("OTHER_FLG", Endresult[22].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String U/SYS_MANUFACTURER"
							+ "/" + Endresult[4].trim();
					System.out.println("---FAILED U/SYS_MANUFACTURER" + "/"
							+ Endresult[4].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnUpdateSysManufacturer(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date U/SYS_MANUFACTURER"
								+ "/" + Endresult[4].trim();

					} else {
						result_desc = "SUCCESS U/SYS_MANUFACTURER" + "/"
								+ Endresult[4].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"MANUFACTURER_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}

				}
			}
			if (Endresult[2].trim().equals("DELETE")) {
				try {
					tparm.setData("MAN_CODE", Endresult[3].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/SYS_MANUFACTURER"
							+ "/" + Endresult[3].trim();
					System.out.println("---FAILED D/SYS_MANUFACTURER" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnDeleteSysManufacturer(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED delete date D/SYS_MANUFACTURER"
								+ "/" + Endresult[3].trim();

					} else {
						result_desc = "SUCCESS D/SYS_MANUFACTURER" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"MANUFACTURER_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}

			}
			// ��¼log
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);

		}

	}

	/*
	 * ͬ��SYS_POSITION��
	 */
	private void SysPositionService(List<String> _getModifyInf__return,
			String arg0, String arg1) {

		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			if (Endresult[2].trim().equals("INSERT")) {
				try {
					tparm.setData("POS_CODE", Endresult[3].trim());
					tparm.setData("POS_CHN_DESC", Endresult[4].trim());
					tparm.setData("POS_ENG_DESC", Endresult[5].trim());
					tparm.setData("DESCRIPTION", Endresult[6].trim());

					tparm.setData("PY1", Endresult[7].trim());
					tparm.setData("PY2", Endresult[8].trim());
					tparm.setData("SEQ", Endresult[9].trim());
					tparm.setData("POS_TYPE", Endresult[10].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/SYS_POSITION" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED I/SYS_POSITION" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnSaveSysPosition(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED save date I/SYS_POSITION" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS I/SYS_POSITION" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"POSITION_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}
			}
			if (Endresult[2].trim().equals("UPDATE")) {
				try {

					tparm.setData("POS_CODE", Endresult[3].trim());
					tparm.setData("POS_CODE1", Endresult[4].trim());
					tparm.setData("POS_CHN_DESC", Endresult[5].trim());
					tparm.setData("POS_ENG_DESC", Endresult[6].trim());
					tparm.setData("DESCRIPTION", Endresult[7].trim());

					tparm.setData("PY1", Endresult[8].trim());
					tparm.setData("PY2", Endresult[9].trim());
					tparm.setData("SEQ", Endresult[10].trim());
					tparm.setData("POS_TYPE", Endresult[11].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String U/SYS_POSITION" + "/"
							+ Endresult[4].trim();
					System.out.println("---FAILED U/SYS_POSITION" + "/"
							+ Endresult[4].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnUpdateSysPosition(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date U/SYS_POSITION" + "/"
								+ Endresult[4].trim();

					} else {
						result_desc = "SUCCESS I/SYS_POSITION" + "/"
								+ Endresult[4].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"POSITION_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}

					}

				}
			}
			if (Endresult[2].trim().equals("DELETE")) {
				try {
					tparm.setData("POS_CODE", Endresult[3].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/SYS_POSITION" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED D/SYS_POSITION" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnDeleteSysPosition(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED delete date D/SYS_POSITION" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS D/SYS_POSITION" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"POSITION_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}
			}
			// ��¼log
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);

		}
	}

	/*
	 * ͬ��SYS_ROOM��
	 */
	private void SysRoomService(List<String> _getModifyInf__return,
			String arg0, String arg1) {

		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			if (Endresult[2].trim().equals("INSERT")) {
				try {
					tparm.setData("ROOM_CODE", Endresult[3].trim());
					tparm.setData("ROOM_DESC", Endresult[4].trim());
					tparm.setData("ENG_DESC", Endresult[5].trim());

					tparm.setData("STATION_CODE", Endresult[6].trim());
					tparm.setData("REGION_CODE", Endresult[7].trim());
					tparm.setData("SEX_LIMIT_FLG", Endresult[8].trim());

					tparm.setData("PY1", Endresult[9].trim());
					tparm.setData("PY2", Endresult[10].trim());
					tparm.setData("SEQ", Endresult[11].trim());
					tparm.setData("DESCRIPT", Endresult[12].trim());
					tparm.setData("RED_SIGN", Endresult[13].trim());
					tparm.setData("YELLOW_SIGN", Endresult[14].trim());

				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/SYS_ROOM" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED I/SYS_ROOM" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnSaveSysRoom(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED save date I/SYS_ROOM" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS I/SYS_ROOM" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"ROOM_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}

			}
			if (Endresult[2].trim().equals("UPDATE")) {
				try {
					tparm.setData("ROOM_CODE", Endresult[3].trim());
					tparm.setData("ROOM_CODE1", Endresult[4].trim());

					tparm.setData("ENG_DESC", Endresult[5].trim());
					tparm.setData("STATION_CODE", Endresult[6].trim());
					tparm.setData("REGION_CODE", Endresult[7].trim());
					tparm.setData("SEX_LIMIT_FLG", Endresult[8].trim());

					tparm.setData("PY1", Endresult[9].trim());
					tparm.setData("PY2", Endresult[10].trim());
					tparm.setData("SEQ", Endresult[11].trim());
					tparm.setData("DESCRIPT", Endresult[12].trim());
					tparm.setData("RED_SIGN", Endresult[13].trim());
					tparm.setData("YELLOW_SIGN", Endresult[14].trim());
					tparm.setData("ROOM_DESC", Endresult[15].trim());

				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String U/SYS_ROOM" + "/"
							+ Endresult[4].trim();
					System.out.println("---FAILED U/SYS_ROOM" + "/"
							+ Endresult[4].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnUpdateSysRoom(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date U/SYS_ROOM" + "/"
								+ Endresult[4].trim();

					} else {
						result_desc = "SUCCESS U/SYS_ROOM" + "/"
								+ Endresult[4].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"ROOM_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}

					}
				}
			}
			if (Endresult[2].trim().equals("DELETE")) {
				try {
					tparm.setData("ROOM_CODE", Endresult[3].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/SYS_ROOM" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED D/SYS_ROOM" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnDeleteSysRoom(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED delete date D/SYS_ROOM" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS D/SYS_ROOM" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"ROOM_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}

					}
				}
			}
			// ��¼log
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);

		}

	}

	/*
	 * ͬ��SYS_STATION��
	 */
	private void SysStationService(List<String> _getModifyInf__return,
			String arg0, String arg1) {

		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			if (Endresult[2].trim().equals("INSERT")) {
				try {
					tparm.setData("STATION_CODE", Endresult[3].trim());
					tparm.setData("STATION_DESC", Endresult[4].trim());
					tparm.setData("ENG_DESC", Endresult[5].trim());
					tparm.setData("DEPT_CODE", Endresult[6].trim());
					tparm.setData("REGION_CODE", Endresult[7].trim());
					tparm.setData("ORG_CODE", Endresult[8].trim());

					tparm.setData("PY1", Endresult[9].trim());
					tparm.setData("PY2", Endresult[10].trim());
					tparm.setData("SEQ", Endresult[11].trim());
					tparm.setData("DESCRIPTION", Endresult[12].trim());
					tparm.setData("LOC_CODE", Endresult[13].trim());
					tparm.setData("PRINTER_NO", Endresult[14].trim());
					tparm.setData("TEL_EXT", Endresult[15].trim());
					tparm.setData("COST_CENTER_CODE", Endresult[16].trim());
					tparm.setData("STA2_CODE", Endresult[17].trim());
					tparm.setData("STA1_CODE", Endresult[18].trim());
					tparm.setData("MACHINENO", Endresult[19].trim());
					tparm.setData("ATC_TYPE", Endresult[20].trim());

				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/SYS_STATION" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED I/SYS_STATION" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnSaveSysStation(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED save date I/SYS_STATION" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS I/SYS_STATION" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"STATION_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}

					}
				}
			}
			if (Endresult[2].trim().equals("UPDATE")) {
				try {
					tparm.setData("STATION_CODE", Endresult[3].trim());
					tparm.setData("STATION_DESC", Endresult[4].trim());
					tparm.setData("ENG_DESC", Endresult[5].trim());
					tparm.setData("DEPT_CODE", Endresult[6].trim());
					tparm.setData("REGION_CODE", Endresult[7].trim());
					tparm.setData("ORG_CODE", Endresult[8].trim());

					tparm.setData("PY1", Endresult[9].trim());
					tparm.setData("PY2", Endresult[10].trim());
					tparm.setData("SEQ", Endresult[11].trim());
					tparm.setData("DESCRIPTION", Endresult[12].trim());
					tparm.setData("LOC_CODE", Endresult[13].trim());
					tparm.setData("PRINTER_NO", Endresult[14].trim());
					tparm.setData("TEL_EXT", Endresult[15].trim());
					tparm.setData("COST_CENTER_CODE", Endresult[16].trim());
					tparm.setData("STA2_CODE", Endresult[17].trim());
					tparm.setData("STA1_CODE", Endresult[18].trim());
					tparm.setData("MACHINENO", Endresult[19].trim());
					tparm.setData("ATC_TYPE", Endresult[20].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String U/SYS_STATION" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED U/SYS_STATION" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnUpdateSysStation(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date U/SYS_STATION" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS U/SYS_STATION" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"STATION_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}

					}

				}
			}
			if (Endresult[2].trim().equals("DELETE")) {
				try {
					tparm.setData("STATION_CODE", Endresult[3].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/SYS_STATION" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED D/SYS_STATION" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnDeleteSysStation(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED delete date D/SYS_STATION" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS D/SYS_STATION" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"STATION_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}

					}

				}

			}
			// ��¼log
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);

		}

	}

	/*
	 * ͬ��SYS_ANTIBIOTIC��
	 */
	private void SysAntibioticService(List<String> _getModifyInf__return,
			String arg0, String arg1) {
		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			if (Endresult[2].trim().equals("INSERT")) {
				try {
					tparm.setData("ANTIBIOTIC_CODE", Endresult[3].trim());
					tparm.setData("ANTIBIOTIC_DESC", Endresult[4].trim());
					tparm.setData("ENG_DESC", Endresult[5].trim());
					tparm.setData("TAKE_DAYS", Endresult[6].trim());
					tparm.setData("PY1", Endresult[7].trim());
					tparm.setData("PY2", Endresult[8].trim());
					tparm.setData("DESCRIPTION", Endresult[9].trim());
					tparm.setData("MR_CODE", Endresult[10].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/SYS_ANTIBIOTIC" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED I/SYS_ANTIBIOTIC" + "/"
							+ Endresult[3].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnSaveSysAntibiotic(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED save date I/SYS_ANTIBIOTIC" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS I/SYS_ANTIBIOTIC" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"ANTIBIOTIC_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}

			}
			if (Endresult[2].trim().equals("UPDATE")) {
				try {
					tparm.setData("ANTIBIOTIC_CODE", Endresult[3].trim());
					tparm.setData("ANTIBIOTIC_DESC", Endresult[4].trim());
					tparm.setData("ENG_DESC", Endresult[5].trim());
					tparm.setData("TAKE_DAYS", Endresult[6].trim());
					tparm.setData("PY1", Endresult[7].trim());
					tparm.setData("PY2", Endresult[8].trim());
					tparm.setData("DESCRIPTION", Endresult[9].trim());
					tparm.setData("MR_CODE", Endresult[10].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String U/SYS_ANTIBIOTIC" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED U/SYS_ANTIBIOTIC" + "/"
							+ Endresult[3].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnUpdateSysAntibiotic(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date U/SYS_ANTIBIOTIC"
								+ "/" + Endresult[3].trim();

					} else {
						result_desc = "SUCCESS U/SYS_ANTIBIOTIC" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"ANTIBIOTIC_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}

			}
			if (Endresult[2].trim().equals("DELETE")) {
				try {
					tparm.setData("ANTIBIOTIC_CODE", Endresult[3].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/SYS_ANTIBIOTIC" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED D/SYS_ANTIBIOTIC" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnDeleteSysAntibiotic(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date D/SYS_ANTIBIOTIC"
								+ "/" + Endresult[3].trim();

					} else {
						result_desc = "SUCCESS D/SYS_ANTIBIOTIC" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"ANTIBIOTIC_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}
			}
			// ��¼log
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);
		}

	}

	/*
	 * ͬ��SYS_OPERATOR��
	 * 
	 * @author shendr
	 */
	private void SysOperatorService(List<String> _getModifyInf__return,
			String arg0, String arg1) {
		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			if (Endresult[2].trim().equals("INSERT")) {
				try {
					tparm.setData("USER_ID", Endresult[3].trim());
					tparm.setData("USER_NAME", Endresult[4].trim());
					tparm.setData("PY1", Endresult[5].trim());
					tparm.setData("PY2", Endresult[6].trim());
					tparm.setData("SEQ", Endresult[7].trim());
					tparm.setData("DESCRIPTION", Endresult[8].trim());
					tparm.setData("ID_NO", Endresult[9].trim());
					tparm.setData("SEX_CODE", Endresult[10].trim());
					tparm.setData("USER_PASSWORD", Endresult[11].trim());
					tparm.setData("POS_CODE", Endresult[12].trim());
					tparm.setData("ROLE_ID", Endresult[13].trim());
					tparm.setData("ACTIVE_DATE", Endresult[14].trim());
					tparm.setData("END_DATE", Endresult[15].trim());
					tparm.setData("PUB_FUNCTION", Endresult[16].trim());
					tparm.setData("E_MAIL", Endresult[17].trim());
					tparm.setData("LCS_NO", Endresult[18].trim());
					tparm.setData("EFF_LCS_DATE", Endresult[19].trim());
					tparm.setData("END_LCS_DATE", Endresult[20].trim());
					tparm.setData("FULLTIME_FLG", Endresult[21].trim());
					tparm.setData("CTRL_FLG", Endresult[22].trim());
					tparm.setData("REGION_CODE", Endresult[23].trim());
					tparm.setData("RCNT_LOGIN_DATE", Endresult[24].trim());
					tparm.setData("RCNT_LOGOUT_DATE", Endresult[25].trim());
					tparm.setData("RCNT_IP", Endresult[26].trim());
					tparm.setData("OPT_USER", Endresult[27].trim());
					tparm.setData("OPT_DATE", Endresult[28].trim());
					tparm.setData("OPT_TERM", Endresult[29].trim());
					tparm.setData("FOREIGNER_FLG", Endresult[30].trim());
					tparm.setData("ABNORMAL_TIMES", Endresult[31].trim());
					tparm.setData("POS_DESC", Endresult[32].trim());
					tparm.setData("USER_ENG_NAME", Endresult[33].trim());
					tparm.setData("PWD_ENDDATE", Endresult[34].trim());
					tparm.setData("PWD_STARTDATE", Endresult[35].trim());
					tparm.setData("UKEY_FLG", Endresult[36].trim());
					tparm.setData("DR_QUALIFY_CODE", Endresult[37].trim());
					tparm.setData("COST_CENTER_CODE", Endresult[38].trim());
					tparm.setData("TEL1", Endresult[39].trim());
					tparm.setData("TEL2", Endresult[40].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/SYS_OPERATOR" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED I/SYS_OPERATOR" + "/"
							+ Endresult[3].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.onSaveSysOperator(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED save date I/SYS_OPERATOR" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS I/SYS_OPERATOR" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"OPERATOR_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}

			}
			if (Endresult[2].trim().equals("UPDATE")) {
				try {
					tparm.setData("USER_ID", Endresult[3].trim());
					tparm.setData("USER_NAME", Endresult[4].trim());
					tparm.setData("PY1", Endresult[5].trim());
					tparm.setData("PY2", Endresult[6].trim());
					tparm.setData("SEQ", Endresult[7].trim());
					tparm.setData("DESCRIPTION", Endresult[8].trim());
					tparm.setData("ID_NO", Endresult[9].trim());
					tparm.setData("SEX_CODE", Endresult[10].trim());
					tparm.setData("USER_PASSWORD", Endresult[11].trim());
					tparm.setData("POS_CODE", Endresult[12].trim());
					tparm.setData("ROLE_ID", Endresult[13].trim());
					tparm.setData("ACTIVE_DATE", Endresult[14].trim());
					tparm.setData("END_DATE", Endresult[15].trim());
					tparm.setData("PUB_FUNCTION", Endresult[16].trim());
					tparm.setData("E_MAIL", Endresult[17].trim());
					tparm.setData("LCS_NO", Endresult[18].trim());
					tparm.setData("EFF_LCS_DATE", Endresult[19].trim());
					tparm.setData("END_LCS_DATE", Endresult[20].trim());
					tparm.setData("FULLTIME_FLG", Endresult[21].trim());
					tparm.setData("CTRL_FLG", Endresult[22].trim());
					tparm.setData("REGION_CODE", Endresult[23].trim());
					tparm.setData("RCNT_LOGIN_DATE", Endresult[24].trim());
					tparm.setData("RCNT_LOGOUT_DATE", Endresult[25].trim());
					tparm.setData("RCNT_IP", Endresult[26].trim());
					tparm.setData("OPT_USER", Endresult[27].trim());
					tparm.setData("OPT_DATE", Endresult[28].trim());
					tparm.setData("OPT_TERM", Endresult[29].trim());
					tparm.setData("FOREIGNER_FLG", Endresult[30].trim());
					tparm.setData("ABNORMAL_TIMES", Endresult[31].trim());
					tparm.setData("POS_DESC", Endresult[32].trim());
					tparm.setData("USER_ENG_NAME", Endresult[33].trim());
					tparm.setData("PWD_ENDDATE", Endresult[34].trim());
					tparm.setData("PWD_STARTDATE", Endresult[35].trim());
					tparm.setData("UKEY_FLG", Endresult[36].trim());
					tparm.setData("DR_QUALIFY_CODE", Endresult[37].trim());
					tparm.setData("COST_CENTER_CODE", Endresult[38].trim());
					tparm.setData("TEL1", Endresult[39].trim());
					tparm.setData("TEL2", Endresult[40].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String U/SYS_OPERATOR" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED U/SYS_OPERATOR" + "/"
							+ Endresult[3].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.onUpdateSysOperator(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date U/SYS_OPERATOR" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS U/SYS_OPERATOR" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"OPERATOR_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}

			}
			if (Endresult[2].trim().equals("DELETE")) {
				try {
					tparm.setData("USER_ID", Endresult[3].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/SYS_OPERATOR" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED D/SYS_OPERATOR" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.onDeleteSysOperator(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date D/SYS_OPERATOR" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS D/SYS_OPERATOR" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"OPERATOR_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}
			}
			// ��¼log
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);
		}
	}

	/*
	 * ͬ��SYS_FEE��
	 * 
	 * @author shendr
	 */
	private void SysFeeService(List<String> _getModifyInf__return, String arg0,
			String arg1) {
		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			if (Endresult[2].trim().equals("INSERT")) {
				try {
					tparm.setData("ORDER_CODE", Endresult[3].trim());
					tparm.setData("ORDER_DESC", Endresult[4].trim());
					tparm.setData("PY1", Endresult[5].trim());
					tparm.setData("PY2", Endresult[6].trim());
					tparm.setData("SEQ", Endresult[7].trim());
					tparm.setData("TRADE_ENG_DESC", Endresult[8].trim());
					tparm.setData("GOODS_DESC", Endresult[9].trim());
					tparm.setData("GOODS_PYCODE", Endresult[10].trim());
					tparm.setData("ALIAS_DESC", Endresult[11].trim());
					tparm.setData("ALIAS_PYCODE", Endresult[12].trim());
					tparm.setData("SPECIFICATION", Endresult[13].trim());
					tparm.setData("MAN_CODE", Endresult[14].trim());
					tparm.setData("ORDER_CAT1_CODE", Endresult[15].trim());
					tparm.setData("OWN_PRICE", Endresult[16].trim());
					tparm.setData("CTRL_FLG", Endresult[17].trim());
					tparm.setData("CAT1_TYPE", Endresult[18].trim());
					tparm.setData("ACTIVE_FLG", Endresult[19].trim());
					tparm.setData("OPT_USER", Endresult[20].trim());
					tparm.setData("OPT_DATE", Endresult[21].trim());
					tparm.setData("OPT_TERM", Endresult[22].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/SYS_FEE"
							+ "/" + Endresult[3].trim();
					System.out.println("---FAILED I/SYS_FEE" + "/"
							+ Endresult[3].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool
							.getInstance().OnInsertFee(tparm);
					if (null == returnresult
							|| returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date I/SYS_FEE"
								+ "/" + Endresult[3].trim();

					} else {
						result_desc = "SUCCESS I/SYS_FEE" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0,
									arg1, "FEE_INF", Endresult[1]
											.trim());
						} catch (Exception e) {
							result_desc = result_desc
									+ " confirm faield";
						}
					}
				}
			}
			if (Endresult[2].trim().equals("UPDATE")) {
				try {
					tparm.setData("ORDER_CODE", Endresult[3].trim());
					tparm.setData("ORDER_DESC", Endresult[4].trim());
					tparm.setData("PY1", Endresult[5].trim());
					tparm.setData("PY2", Endresult[6].trim());
					tparm.setData("SEQ", Endresult[7].trim());
					tparm.setData("TRADE_ENG_DESC", Endresult[8].trim());
					tparm.setData("GOODS_DESC", Endresult[9].trim());
					tparm.setData("GOODS_PYCODE", Endresult[10].trim());
					tparm.setData("ALIAS_DESC", Endresult[11].trim());
					tparm.setData("ALIAS_PYCODE", Endresult[12].trim());
					tparm.setData("SPECIFICATION", Endresult[13].trim());
					tparm.setData("MAN_CODE", Endresult[14].trim());
					tparm.setData("ORDER_CAT1_CODE", Endresult[15].trim());
					tparm.setData("OWN_PRICE", Endresult[16].trim());
					tparm.setData("CTRL_FLG", Endresult[17].trim());
					tparm.setData("CAT1_TYPE", Endresult[18].trim());
					tparm.setData("ACTIVE_FLG", Endresult[19].trim());
					tparm.setData("OPT_USER", Endresult[20].trim());
					tparm.setData("OPT_DATE", Endresult[21].trim());
					tparm.setData("OPT_TERM", Endresult[22].trim());
				} catch (Exception e) {
					operation = false;
					result_desc += "FAILED prase String U/SYS_FEE"
							+ "/" + Endresult[3].trim();
					System.out.println("---FAILED U/SYS_FEE" + "/"
							+ Endresult[3].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool
							.getInstance().OnUpdateFee(tparm);
					if (null == returnresult
							|| returnresult.getErrCode() < 0) {
						result_desc += "FAILED update date U/SYS_FEE"
								+ "/" + Endresult[3].trim();

					} else {
						result_desc += "SUCCESS U/SYS_FEE" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0,
									arg1, "FEE_INF", Endresult[1]
											.trim());
						} catch (Exception e) {
							result_desc = result_desc
									+ " confirm faield";
						}
					}
				}
			}
			if (Endresult[2].trim().equals("DELETE")) {
				try {
					tparm.setData("ORDER_CODE", Endresult[3].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/SYS_FEE"
							+ "/" + Endresult[3].trim();
					System.out.println("---FAILED D/SYS_FEE" + "/"
							+ Endresult[3].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool
							.getInstance().OnDeleteFee(tparm);
					if (null == returnresult
							|| returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date D/SYS_FEE"
								+ "/" + Endresult[3].trim();

					} else {
						result_desc = "SUCCESS D/SYS_FEE" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0,
									arg1, "FEE_INF", Endresult[1]
											.trim());
						} catch (Exception e) {
							result_desc = result_desc
									+ " confirm faield";
						}
					}
				}
			}
			// ��¼log
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);
		}
	}

	/*
	 * ͬ��PHA_BASE��
	 * 
	 * @author shendr
	 */
	private void PhaBaseService(List<String> _getModifyInf__return,
			String arg0, String arg1) {
		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			if (Endresult[2].trim().equals("INSERT")) {
				try {
					tparm.setData("ORDER_CODE", Endresult[3]
					                                      	.trim());
					tparm.setData("ORDER_DESC", Endresult[4]
					                                      	.trim());
					tparm.setData("GOODS_DESC", Endresult[5]
															.trim());
					tparm.setData("ALIAS_DESC", Endresult[6]
															.trim());
					tparm.setData("SPECIFICATION", Endresult[7]
															.trim());
					tparm.setData("MAN_CHN_DESC", Endresult[8]
															.trim());
					tparm.setData("PHA_TYPE", Endresult[9]
															.trim());
					tparm.setData("TYPE_CODE", Endresult[10]
															.trim());
					tparm.setData("DOSE_CODE", Endresult[11]
															.trim());
					tparm.setData("FREQ_CODE", Endresult[12]
															.trim());
					tparm.setData("ROUTE_CODE", Endresult[13]
															.trim());
					tparm.setData("TAKE_DAYS", Endresult[14]
															.trim());
					tparm.setData("MEDI_QTY", Endresult[15]
															.trim());
					tparm.setData("MEDI_UNIT", Endresult[16]
															.trim());
					tparm.setData("DOSAGE_UNIT", Endresult[17]
															.trim());
					tparm.setData("STOCK_UNIT", Endresult[18]
															.trim());
					tparm.setData("PURCH_UNIT", Endresult[19]
															.trim());
					tparm.setData("RETAIL_PRICE", Endresult[20]
															.trim());
					tparm.setData("CTRLDRUGCLASS_CODE", Endresult[21]
															.trim());
					tparm.setData("ANTIBIOTIC_CODE", Endresult[22]
															.trim());
					tparm.setData("SUP_CODE", Endresult[23]
															.trim());
					tparm.setData("OPT_USER", Endresult[24]
															.trim());
					tparm.setData("OPT_DATE", Endresult[25]
															.trim());
					tparm.setData("OPT_TERM", Endresult[26]
															.trim());
					tparm.setData("PACK_UNIT", Endresult[27]
															.trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/PHA_BASE"
							+ "/" + Endresult[3].trim();
					System.out.println("---FAILED I/PHA_BASE" + "/"
							+ Endresult[3].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool
							.getInstance().OnInsertBase(tparm);
					if (null == returnresult
							|| returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date I/PHA_BASE"
								+ "/" + Endresult[3].trim();

					} else {
						result_desc = "SUCCESS I/PHA_BASE" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0,
									arg1, "BASE_INF", Endresult[1]
											.trim());
						} catch (Exception e) {
							result_desc = result_desc
									+ " confirm faield";
						}
					}
				}
			}
			if (Endresult[2].trim().equals("UPDATE")) {
				try {
					tparm.setData("ORDER_CODE", Endresult[3]
					                                      	.trim());
					tparm.setData("ORDER_DESC", Endresult[4]
				                                      		.trim());
					tparm.setData("GOODS_DESC", Endresult[5]
															.trim());
					tparm.setData("ALIAS_DESC", Endresult[6]
															.trim());
					tparm.setData("SPECIFICATION", Endresult[7]
															.trim());
					tparm.setData("MAN_CHN_DESC", Endresult[8]
															.trim());
					tparm.setData("PHA_TYPE", Endresult[9]
															.trim());
					tparm.setData("TYPE_CODE", Endresult[10]
															.trim());
					tparm.setData("DOSE_CODE", Endresult[11]
															.trim());
					tparm.setData("FREQ_CODE", Endresult[12]
															.trim());
					tparm.setData("ROUTE_CODE", Endresult[13]
															.trim());
					tparm.setData("TAKE_DAYS", Endresult[14]
															.trim());
					tparm.setData("MEDI_QTY", Endresult[15]
															.trim());
					tparm.setData("MEDI_UNIT", Endresult[16]
															.trim());
					tparm.setData("DOSAGE_UNIT", Endresult[17]
															.trim());
					tparm.setData("STOCK_UNIT", Endresult[18]
															.trim());
					tparm.setData("PURCH_UNIT", Endresult[19]
															.trim());
					tparm.setData("RETAIL_PRICE", Endresult[20]
															.trim());
					tparm.setData("CTRLDRUGCLASS_CODE", Endresult[21]
															.trim());
					tparm.setData("ANTIBIOTIC_CODE", Endresult[22]
															.trim());
					tparm.setData("SUP_CODE", Endresult[23]
															.trim());
					tparm.setData("OPT_USER", Endresult[24]
															.trim());
					tparm.setData("OPT_DATE", Endresult[25]
															.trim());
					tparm.setData("OPT_TERM", Endresult[26]
															.trim());
					tparm.setData("PACK_UNIT", Endresult[27]
															.trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String U/PHA_BASE"
							+ "/" + Endresult[3].trim();
					System.out.println("---FAILED U/PHA_BASE" + "/"
							+ Endresult[3].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool
							.getInstance().OnUpdateBase(tparm);
					if (null == returnresult
							|| returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date U/PHA_BASE"
								+ "/" + Endresult[3].trim();

					} else {
						result_desc = "SUCCESS U/PHA_BASE" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0,
									arg1, "BASE_INF", Endresult[1]
											.trim());
						} catch (Exception e) {
							result_desc = result_desc
									+ " confirm faield";
						}
					}
				}
			}
			if (Endresult[2].trim().equals("DELETE")) {
				try {
					tparm.setData("ORDER_CODE", Endresult[3].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/PHA_BASE"
							+ "/" + Endresult[3].trim();
					System.out.println("---FAILED D/PHA_BASE" + "/"
							+ Endresult[3].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool
							.getInstance().OnDeleteBase(tparm);
					if (null == returnresult
							|| returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date D/PHA_BASE"
								+ "/" + Endresult[3].trim();

					} else {
						result_desc = "SUCCESS D/PHA_BASE" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0,
									arg1, "BASE_INF", Endresult[1]
											.trim());
						} catch (Exception e) {
							result_desc = result_desc
									+ " confirm faield";
						}
					}
				}
			}
			// ��¼log
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);
		}
	}

	/*
	 * ͬ��IND_AGENT��
	 * 
	 * @author shendr
	 */
	private void IndAgentService(List<String> _getModifyInf__return,
			String arg0, String arg1) {
		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			if (Endresult[2].trim().equals("INSERT")) {
				try {
					tparm.setData("SUP_CODE", Endresult[3].trim());
					tparm.setData("ORDER_CODE", Endresult[4].trim());
					tparm.setData("MAIN_FLG", Endresult[5].trim());
					tparm.setData("CONTRACT_PRICE", Endresult[6].trim());
					tparm.setData("LAST_VERIFY_DATE", Endresult[7].trim());
					tparm.setData("LAST_VERIFY_PRICE", Endresult[8].trim());
					tparm.setData("OPT_USER", Endresult[9].trim());
					tparm.setData("OPT_DATE", Endresult[10].trim());
					tparm.setData("OPT_TERM", Endresult[11].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/IND_AGENT" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED I/IND_AGENT" + "/"
							+ Endresult[3].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnInsertInd(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED save date I/IND_AGENT" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS I/IND_AGENT" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"AGENT_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}
			}
			if (Endresult[2].trim().equals("UPDATE")) {
				try {
					tparm.setData("SUP_CODE", Endresult[3].trim());
					tparm.setData("ORDER_CODE", Endresult[4].trim());
					tparm.setData("MAIN_FLG", Endresult[5].trim());
					tparm.setData("CONTRACT_PRICE", Endresult[6].trim());
					tparm.setData("LAST_VERIFY_DATE", Endresult[7].trim());
					tparm.setData("LAST_VERIFY_PRICE", Endresult[8].trim());
					tparm.setData("OPT_USER", Endresult[9].trim());
					tparm.setData("OPT_DATE", Endresult[10].trim());
					tparm.setData("OPT_TERM", Endresult[11].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String U/IND_AGENT" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED U/IND_AGENT" + "/"
							+ Endresult[3].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnUpdateInd(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date U/IND_AGENT" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS U/IND_AGENT" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"AGENT_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}
			}
			if (Endresult[2].trim().equals("DELETE")) {
				try {
					tparm.setData("SUP_CODE", Endresult[3].trim());
					tparm.setData("ORDER_CODE", Endresult[4].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/IND_AGENT" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED D/IND_AGENT" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.OnDeleteInd(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED delete date D/IND_AGENT" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS D/IND_AGENT" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"AGENT_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}
			}
			// ��¼log
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);
		}
	}

	/*
	 * ͬ��SYS_PATINFO��
	 * 
	 * @author shendr
	 */
	private void SysPatInfoService(List<String> _getModifyInf__return,
			String arg0, String arg1) {
		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			if (Endresult[2].trim().equals("INSERT")) {
				try {
					tparm.setData("MR_NO", Endresult[3].trim());
					tparm.setData("IPD_NO", Endresult[4].trim());
					tparm.setData("PAT_NAME", Endresult[5].trim());
					tparm.setData("IDNO", Endresult[6].trim());
					tparm.setData("BIRTH_DATE", Endresult[7].trim());
					tparm.setData("TEL_HOME", Endresult[8].trim());
					tparm.setData("CELL_PHONE", Endresult[9].trim());
					tparm.setData("BLOOD_TYPE", Endresult[10].trim());
					tparm.setData("SEX_CODE", Endresult[11].trim());
					tparm.setData("MARRIAGE_CODE", Endresult[12].trim());
					tparm.setData("POST_CODE", Endresult[13].trim());
					tparm.setData("ADDRESS", Endresult[14].trim());
					tparm.setData("RESID_POST_CODE", Endresult[15].trim());
					tparm.setData("RESID_ADDRESS", Endresult[16].trim());
					tparm.setData("NATION_CODE", Endresult[17].trim());
					tparm.setData("DEAD_DATE", Endresult[18].trim());
					tparm.setData("HEIGHT", Endresult[19].trim());
					tparm.setData("WEIGHT", Endresult[20].trim());
					tparm.setData("OPT_USER", Endresult[21].trim());
					tparm.setData("OPT_DATE", Endresult[22].trim());
					tparm.setData("OPT_TERM", Endresult[23].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/SYS_PATINFO" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED I/SYS_PATINFO" + "/"
							+ Endresult[3].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.onSaveSysPatInfo(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED save date I/SYS_PATINFO" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS I/SYS_PATINFO" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"PATINFO_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}

			}
			if (Endresult[2].trim().equals("UPDATE")) {
				try {
					tparm.setData("MR_NO", Endresult[3].trim());
					tparm.setData("IPD_NO", Endresult[4].trim());
					tparm.setData("PAT_NAME", Endresult[5].trim());
					tparm.setData("IDNO", Endresult[6].trim());
					tparm.setData("BIRTH_DATE", Endresult[7].trim());
					tparm.setData("TEL_HOME", Endresult[8].trim());
					tparm.setData("CELL_PHONE", Endresult[9].trim());
					tparm.setData("BLOOD_TYPE", Endresult[10].trim());
					tparm.setData("SEX_CODE", Endresult[11].trim());
					tparm.setData("MARRIAGE_CODE", Endresult[12].trim());
					tparm.setData("POST_CODE", Endresult[13].trim());
					tparm.setData("ADDRESS", Endresult[14].trim());
					tparm.setData("RESID_POST_CODE", Endresult[15].trim());
					tparm.setData("RESID_ADDRESS", Endresult[16].trim());
					tparm.setData("NATION_CODE", Endresult[17].trim());
					tparm.setData("DEAD_DATE", Endresult[18].trim());
					tparm.setData("HEIGHT", Endresult[19].trim());
					tparm.setData("WEIGHT", Endresult[20].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String U/SYS_PATINFO" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED U/SYS_PATINFO" + "/"
							+ Endresult[3].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.onUpdateSysPatInfo(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date U/SYS_PATINFO" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS U/SYS_PATINFO" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"PATINFO_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}

			}
			if (Endresult[2].trim().equals("DELETE")) {
				try {
					tparm.setData("MR_NO", Endresult[3].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/SYS_PATINFO" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED D/SYS_PATINFO" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.onDeleteSysPatInfo(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date D/SYS_PATINFO" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS D/SYS_PATINFO" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"PATINFO_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}
			}
			// ��¼log
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);
		}
	}
	
	/*
	 * ͬ��PHA_TRANSUNIT��
	 * 
	 * @author shendr
	 */
	private void PhaTransunitService(List<String> _getModifyInf__return,
			String arg0, String arg1) {
		for (int j = 0; j < _getModifyInf__return.size(); j++) {
			// �����������
			TParm tparm = new TParm();
			// ��¼LOG
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "SYS_DIC_BATCH");
			// ����һ�����ݣ�ȡһ��
			String result = _getModifyInf__return.get(j);
			result = result.replace(";", " ; ");
			// ����
			String[] Endresult = result.split(";");
			// ��¼����󷵻ؽ��
			TParm returnresult = null;
			// ������Ϣ
			String result_desc = null;
			// �ж��Ƿ��ж����ݿ���в�������Ҫ
			boolean operation = true;
			if (Endresult[2].trim().equals("INSERT")) {
				try {
					tparm.setData("ORDER_CODE", Endresult[3].trim());
					tparm.setData("PURCH_UNIT", Endresult[4].trim());
					tparm.setData("PURCH_QTY", Endresult[5].trim());
					tparm.setData("STOCK_UNIT", Endresult[6].trim());
					tparm.setData("STOCK_QTY", Endresult[7].trim());
					tparm.setData("DOSAGE_UNIT", Endresult[8].trim());
					tparm.setData("DOSAGE_QTY", Endresult[9].trim());
					tparm.setData("MEDI_UNIT", Endresult[10].trim());
					tparm.setData("MEDI_QTY", Endresult[11].trim());
					tparm.setData("OPT_USER", Endresult[12].trim());
					tparm.setData("OPT_DATE", Endresult[13].trim());
					tparm.setData("OPT_TERM", Endresult[14].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String I/PHA_TRANSUNIT" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED I/PHA_TRANSUNIT" + "/"
							+ Endresult[3].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.onInsertTransunit(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED save date I/PHA_TRANSUNIT" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS I/PHA_TRANSUNIT" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"TRANSUNIT_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}

			}
			if (Endresult[2].trim().equals("UPDATE")) {
				try {
					tparm.setData("ORDER_CODE", Endresult[3].trim());
					tparm.setData("PURCH_UNIT", Endresult[4].trim());
					tparm.setData("PURCH_QTY", Endresult[5].trim());
					tparm.setData("STOCK_UNIT", Endresult[6].trim());
					tparm.setData("STOCK_QTY", Endresult[7].trim());
					tparm.setData("DOSAGE_UNIT", Endresult[8].trim());
					tparm.setData("DOSAGE_QTY", Endresult[9].trim());
					tparm.setData("MEDI_UNIT", Endresult[10].trim());
					tparm.setData("MEDI_QTY", Endresult[11].trim());
					tparm.setData("OPT_USER", Endresult[12].trim());
					tparm.setData("OPT_DATE", Endresult[13].trim());
					tparm.setData("OPT_TERM", Endresult[14].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String U/PHA_TRANSUNIT" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED U/PHA_TRANSUNIT" + "/"
							+ Endresult[3].trim() + result);

				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.onUpdateTransunit(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date U/PHA_TRANSUNIT" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS U/PHA_TRANSUNIT" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"TRANSUNIT_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}

			}
			if (Endresult[2].trim().equals("DELETE")) {
				try {
					tparm.setData("ORDER_CODE", Endresult[3].trim());
				} catch (Exception e) {
					operation = false;
					result_desc = "FAILED prase String D/PHA_TRANSUNIT" + "/"
							+ Endresult[3].trim();
					System.out.println("---FAILED D/PHA_TRANSUNIT" + "/"
							+ Endresult[3].trim() + result);
				}
				if (operation) {
					returnresult = SYSDictionaryPatchTool.getInstance()
							.onDeleteTransunit(tparm);
					if (null == returnresult || returnresult.getErrCode() < 0) {
						result_desc = "FAILED update date D/PHA_TRANSUNIT" + "/"
								+ Endresult[3].trim();

					} else {
						result_desc = "SUCCESS D/PHA_TRANSUNIT" + "/"
								+ Endresult[3].trim();
						try {
							SYSDictionaryConfig.ConfirmedInf(arg0, arg1,
									"TRANSUNIT_INF", Endresult[1].trim());
						} catch (Exception e) {
							result_desc = result_desc + " confirm faield";
						}
					}
				}
			}
			// ��¼log
			logparm.setData("RESULT_DESC", result_desc);
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);
		}
	}

}
