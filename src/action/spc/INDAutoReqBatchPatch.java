package action.spc;

import com.dongyang.patch.Patch;
import com.dongyang.data.TParm;

import java.util.Calendar;


import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;

import jdo.spc.INDTool;
import jdo.spc.INDSQL;
import jdo.sys.SystemTool;

import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: ҩ���Զ�������ҵ
 * </p>
 * 
 * <p>
 * Description: ҩ���Զ�������ҵ
 * </p>
 * 
 * <p>
 * Copyright: bluecore (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author liyh 2012.10.13
 * @version 1.0
 */

public class INDAutoReqBatchPatch extends Patch {

	private static final String Flag_Y = "Y";
	private static final String Flag_N = "N";
	private static final String Flag_ONE = "1";
	private static final String Flag_ZERO = "0";
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	public boolean run() {
		TConnection connection = TDBPoolManager.getInstance().getConnection();
		// �õ�ҩ�Ⲧ�����ڲ���
		TParm assignParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getAssignorg()));
		// �õ�ҩ�Ⲧ������
		TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.getINDSysParm()));
		// �Զ�������ʽ��0:ÿ��(��)��������, 1:���������(���ڰ�ȫ���ʱ) 2:����С�����(���ڰ�ȫ���ʱ)
		String fixedAmountFlagMain = parm.getValue("FIXEDAMOUNT_FLG", 0);
		if (null == fixedAmountFlagMain || "".equals(fixedAmountFlagMain)) {
			fixedAmountFlagMain = Flag_ZERO;
		}
		// �Զ������� :2�����������������1����������
		String autoFillTypeMain = parm.getValue("AUTO_FILL_TYPE", 0);
		if (null == autoFillTypeMain || "".equals(autoFillTypeMain)) {
			autoFillTypeMain = Flag_ONE;
		}

		// ��ѯ���п����Զ�������ҩ��
//		System.out.println("-------------INDSQL.queryOrgCodeAuto(): " + INDSQL.queryOrgCodeAuto());
		TParm orgParm = new TParm(TJDODBTool.getInstance().select(INDSQL.queryOrgCodeAuto()));
		// ���ݼ��
		if (orgParm == null || orgParm.getCount() < 1)
			return false;
		TParm result = new TParm();
		for (int i = 0; i < orgParm.getCount("ORG_CODE"); i++) {
			String orgCode = orgParm.getValue("ORG_CODE", i);
			//��ѯҩ�ⲿ���Ƿ���������Զ���������������
			TParm orgAutoParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getINDORG(orgCode, "H01")));
			//�Զ�������ʽ
			String fixedAmountFlag = fixedAmountFlagMain;
			//�Զ���������
			String autoFillType = autoFillTypeMain;
			if (null != orgAutoParm && orgAutoParm.getCount() > 0) {
				//ҩ�������Դ��� �Զ�������ʽ��0:ÿ��(��)��������, 1:���������(���ڰ�ȫ���ʱ) 2:����С�����(���ڰ�ȫ���ʱ)
				String fixedAmountFlagNew = orgAutoParm.getValue("FIXEDAMOUNT_FLG", i);
				if (null != fixedAmountFlagNew || !"".equals(fixedAmountFlagNew)) {//���ҩ�ⲿ��û���趨�Զ�������ʽ ��ο� ҩ�����ͳһ�涨���Զ�������ʽ
					fixedAmountFlag = fixedAmountFlagNew;
				}
				//ҩ�������Դ��� �Զ������� :2�����������������1����������
				String autoFillTypeNew = orgAutoParm.getValue("AUTO_FILL_TYPE", i);
				if (null != autoFillTypeNew || !"".equals(autoFillTypeNew)) {////���ҩ�ⲿ��û���趨�Զ��������� ��ο� ҩ�����ͳһ�涨���Զ���������
					autoFillType = autoFillTypeNew;
				}
			}
			// ��������
			System.out.println(""+orgParm.getValue("DISPENSE_ORG_CODE", i));
			String toOrgCode =orgParm.getValue("DISPENSE_ORG_CODE", i);
			//�ӿ�ĸ���Ĳ���
//			String childOrgSubOrg = "";
			//���ű��������ж��Ƿ��в���
			String orgCodeAssign = orgCode;
			// �Ƿ����ӿ� N����Y����
//			String isSubOrg = orgParm.getValue("IS_SUBORG", i);

			/***********ȥ���ӿ�start**********/
/*			if(Flag_Y.equals(isSubOrg)){//������ӿ⣬�Ȳ�ѯ�丸��Ĳ�������
				//������ӿ� ���� �丸���ѯ�丸��Ĳ�������
				TParm subParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getSubOrgInfo(toOrgCode)));
				if(null == subParm || subParm.getCount() < 1){
					return false;
				}
				childOrgSubOrg = subParm.getValue("SUP_ORG_CODE", 0);
				orgCodeAssign =toOrgCode;// 
			}*/
			/***********ȥ���ӿ�end**********/
			// �����ҩƷ���� ���� �����ж� ��������,������ �Ƿ񲦲���false����������true:����
			boolean flag = isAssignAuto(assignParm, orgCodeAssign);
//			System.out.println("---------flag: "+flag);
			if (!flag) {// ����Ƿ� ������ѭ��
//				System.out.println(orgCode+"------orgCode-nononoono-----");
				continue;
			}

			// ��ѯ�����Զ�������ҩƷ
//			System.out.println("------------INDSQL.queryStockM(orgCode, fixedAmountFlag): " + INDSQL.queryStockM(orgCode, fixedAmountFlag));
			//���龫
			TParm parmD = new TParm(TJDODBTool.getInstance().select(INDSQL.queryStockM(orgCode, fixedAmountFlag)));
			//�龫
			TParm parmDrugD = new TParm(TJDODBTool.getInstance().select(INDSQL.queryStockMDrug(orgCode, fixedAmountFlag)));
			if ((null == parmD || parmD.getCount() < 1)  && (null == parmDrugD || parmDrugD.getCount() < 1)) {// ���Ϊ���� ������һ�������ж�
				continue;
			}

			if ("-1".equals(toOrgCode)) {// ������
				// �õ��ƻ�����
				if(null != parmD && parmD.getCount()>0){//���龫�Զ����ɶ���
					String purorderNo = SystemTool.getInstance().getNo("ALL", "IND", "IND_PURORDER", "No");
					TParm purOrderM = getPurOrderMParm(purorderNo,orgCode,"18", fixedAmountFlag, autoFillType,"1");
					// �����Զ�������/ϸ 
					result = INDTool.getInstance().onSavePurOrderMAuto(purOrderM, parmD, connection);
					if (result.getErrCode() < 0) {
						System.out.println("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
						connection.close();
						return false;
					}
				}
				if(null != parmDrugD && parmDrugD.getCount()>0){//���龫�Զ����ɶ���
					String purorderNo = SystemTool.getInstance().getNo("ALL", "IND", "IND_PURORDER", "No");
					TParm purOrderM = getPurOrderMParm(purorderNo,orgCode,"18", fixedAmountFlag, autoFillType,"2");
					// �����Զ�������/ϸ
					result = INDTool.getInstance().onSavePurOrderMAuto(purOrderM, parmDrugD, connection);
					if (result.getErrCode() < 0) {
						System.out.println("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
						connection.close();
						return false;
					}
				}				
			
			} 
			else {
/*				if(Flag_Y.equals(isSubOrg)){//������ӿ⣬�Ȳ�ѯ�丸��Ĳ�������
					toOrgCode = childOrgSubOrg;
				}*/
				if(null != parmD && parmD.getCount()>0){//���龫�Զ����ɶ���
					// �õ����쵥��
					String requestNo = SystemTool.getInstance().getNo("ALL", "IND", "IND_REQUEST", "No");
					// ��װ����������Ϣ
					TParm parmM = getRequestMParm(requestNo, orgCode, toOrgCode, fixedAmountFlag, autoFillType,"1");
					// �����Զ�������/ϸ
					result = INDTool.getInstance().onSaveRequestMAuto(parmM, parmD, connection);
					if (result.getErrCode() < 0) {
						System.out.println("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
						connection.close();
						return false;
					}
				}
				if(null != parmDrugD && parmDrugD.getCount()>0){//�龫�Զ����ɶ���
					// �õ����쵥��
					String requestNo = SystemTool.getInstance().getNo("ALL", "IND", "IND_REQUEST", "No");
					// ��װ����������Ϣ
					TParm parmM = getRequestMParm(requestNo, orgCode, toOrgCode, fixedAmountFlag, autoFillType,"2");
					// �����Զ�������/ϸ
					result = INDTool.getInstance().onSaveRequestMAuto(parmM, parmDrugD, connection);
					if (result.getErrCode() < 0) {
						System.out.println("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
						connection.close();
						return false;
					}
				}

			}

		}

		connection.commit();
		connection.close();
		return true;
	}

	/**
	 * �жϲ��Ž����Ƿ��Զ�����
	 * 
	 * @param assignParm
	 * @param orgCode
	 * @return
	 */
	public boolean isAssignAuto(TParm assignParm, String orgCode) {
		// ���ر�־
		boolean flag = false;
		// �������� 1�����ڣ�0����
		String cycleType = Flag_ZERO;
		String assignDay = "";
		if (null == assignParm || assignParm.getCount() < 0) {// ���û�в������ڷ���false;
			return false;
		}
		// ѭ�� �õ��Ĳ��� ������ ��ʽ�������ڻ����·�
		for (int i = 0; i < assignParm.getCount(); i++) {
			String orgCode1 = assignParm.getValue("ORG_CODE", i);
			if (orgCode.equals(orgCode1)) {
				cycleType = assignParm.getValue("CYCLE_TYPE", i);
				assignDay = assignParm.getValue("ASSIGNED_DAY", i);
				// �жϵ��� �Ƿ񲦲�
				flag = isAtuoToday(cycleType, assignDay);
				break;
			}
		}
		return flag;
	}

	/**
	 * �жϽ����Ƿ��Զ�����
	 * 
	 * @return
	 */
	public boolean isAtuoToday(String cycleType, String assignDay) {
		boolean flag = false;
		if (Flag_ONE.equals(cycleType)) {// �����ڲ���
			// �õ����������ڼ����գ�һ�����������ģ��壬��
			int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
			// ���¸�ʽ�����ڰ��ţ��գ�һ�����������ģ� �壬��
			String assday = getFromatAssingDay(assignDay);
			// ��ȡ��ǰ����ֵ��Ӧ������
			String subString = assday.substring(day, day + 1);
			if (Flag_Y.equals(subString)) {
				flag = true;
			} else {
				flag = false;
			}
		}
		if (Flag_ZERO.equals(cycleType)) {// ���·ݲ���
			// �õ������Ǽ���
			int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			// ��ȡ������ֵ��Ӧ������
			String subString = assignDay.substring(day-1, day);
			if (Flag_Y.equals(subString)) {
				flag = true;
			} else {
				flag = false;
			}
		}
		return flag;
	}

	/**
	 * ���������������
	 * 
	 * @des ԭ�ȸ�ʽ ����һ�����������ģ��壬�����գ���Ϊ�����գ�һ�����������ģ��壬��
	 * @param assignDay
	 * @return
	 */
	public String getFromatAssingDay(String assignDay) {
		int len = assignDay.length();
		String newStr = assignDay.substring(0, len - 1);
		String lastStr = assignDay.substring(len - 1);
		return lastStr + newStr;
	}

	/**
	 * ����Զ��������������PARM
	 * @param isDrug �Ƿ�Ϊ�龫2�ǣ�1����
	 * @param parm
	 * @return
	 */
	private TParm getRequestMParm(String requestNo, String orgCode, String toOrgCode, String fixedType, String autoFillType,String isDrug){
		TParm parm = new TParm();
		parm.setData("REQUEST_NO", requestNo);
		parm.setData("REQTYPE_CODE", "DEP");
		parm.setData("APP_ORG_CODE", orgCode);
		parm.setData("TO_ORG_CODE", toOrgCode);
		// �Զ�������ʽ
		parm.setData("FIXEDAMOUNT_FLG", fixedType);
		parm.setData("AUTO_FILL_TYPE", autoFillType);
		parm.setData("DRUG_CATEGORY", isDrug);
		return parm;
	}

	/**
	 * ��ö���/�ƻ����������PARM
	 * @param purOrderNo
	 * @param orgCode
	 * @param SUP_CODE
	 * @param isDrug �Ƿ�Ϊ�龫 Y�ǣ�N����
	 * @return
	 */
	private TParm getPurOrderMParm(String purOrderNo,String orgCode, String SUP_CODE, String fixedType, String autoFillType,String isDrug) {
		TParm parm = new TParm();
		parm.setData("PURORDER_NO",purOrderNo);
		parm.setData("ORG_CODE", orgCode);
		parm.setData("SUP_CODE", SUP_CODE);
		parm.setData("REASON_CHN_DESC", "");
		parm.setData("REGION_CODE", "H01");
		// �Զ�������ʽ
		parm.setData("FIXEDAMOUNT_FLG", fixedType);
		//�Զ���������
		parm.setData("AUTO_FILL_TYPE", autoFillType);
		parm.setData("DRUG_CATEGORY", isDrug);
		return parm;
	}
}
