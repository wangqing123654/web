package jdo.sys;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;

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
public class SYSNewRegionTool extends TJDOTool {
	public SYSNewRegionTool() {
		setModuleName("sys\\SYSNewRegionModule.x");
		onInit();
	}

	/**
	 * ʵ��
	 */
	public static SYSNewRegionTool instanceObject;

	/**
	 * �õ�ʵ��
	 *
	 * @return SYSRegionTool
	 */
	public static SYSNewRegionTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSNewRegionTool();
		return instanceObject;
	}

	/**
	 * ��ѯҳ��flag��˳��,һ��ƴ��20λ�����硰yyyyynnnnn����������Ϊstate_list���ֶ�ֵ
	 *
	 * @param GROUP_ID
	 * @return
	 */
	public TParm getStateList(String GROUP_ID) {
		TParm parm = new TParm();
		parm.setData("GROUP_ID", GROUP_ID);
		TParm result = query("selectStateList", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����������ѯ
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQuery(TParm parm) {
		TParm result = this.query("selectByConditions", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���������
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsert(TParm parm) {
		TParm result = this.update("insertdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸�
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onUpdate(TParm parm) {
		TParm result = this.update("update", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * ɾ��
	 *
	 * @param <any>
	 *            TParm
	 * @return TParm
	 */
	public TParm onDelete(TParm parm) {
		TParm result = this.update("delete", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/*-------------------2009-3-24 zhangk start--------------------*/
	/**
	 * �Ƿ����ýṹ������
	 *
	 * @return boolean
	 */
	public boolean isEMR(String REGION_CODE) {

		boolean result = getState("EMR", REGION_CODE);
		return result;
	}

	/**
	 * �Ƿ�����LDAP
	 *
	 * @return boolean
	 */
	public boolean isLDAP(String REGION_CODE) {
		boolean result = getState("LDAP", REGION_CODE);
		return result;
	}

	/**
	 * �Ƿ���������״̬
	 *
	 * @return boolean
	 */
	public boolean isONLINE(String REGION_CODE) {
		boolean result = getState("ONLINE", REGION_CODE);
		return result;
	}

	/**
	 * �Ƿ��������������ҩ
	 *
	 * @return boolean
	 */
	public boolean isOREASONABLEMED(String REGION_CODE) {
		boolean result = getState("REASONABLEMED_O", REGION_CODE);
		return result;
	}

	/**
	 * �Ƿ�����סԺ������ҩ
	 *
	 * @return boolean
	 */
	public boolean isIREASONABLEMED(String REGION_CODE) {
		boolean result = getState("REASONABLEMED_I", REGION_CODE);
		return result;
	}

	/**
	 * �Ƿ�����ҽ�ƿ�
	 *
	 * @return boolean
	 */
	public boolean isEKT(String REGION_CODE) {
		boolean result = getState("EKT", REGION_CODE);
		return result;
	}

	/**
	 * �Ƿ�����ҽ����
	 *
	 * @return boolean
	 */
	public boolean isINS(String REGION_CODE) {
		boolean result = getState("INS", REGION_CODE);
		return result;
	}

	/**
	 * �Ƿ���������ҽ������
	 *
	 * @return boolean
	 */
	public boolean isODO(String REGION_CODE) {
		boolean result = getState("ODO", REGION_CODE);
		return result;
	}

	/**
	 * �Ƿ���������Һ���ҽ������
	 *
	 * @return boolean
	 */
	public boolean isREG(String REGION_CODE) {
		boolean result = getState("REG", REGION_CODE);
		return result;
	}

	/**
	 * �Ƿ����������շѼ�ҽ������
	 *
	 * @return boolean
	 */
	public boolean isCHARGE(String REGION_CODE) {
		boolean result = getState("CHARGE", REGION_CODE);
		return result;
	}

	/**
	 * ������Ų�ѯSTATE_LIST�ַ����е�ֵ
	 *
	 * @param String
	 *            name Ҫ��ѯ��ģ����
	 * @param String
	 *            REGION_CODE Ժ����
	 * @return boolean
	 */
	private boolean getState(String name, String REGION_CODE) {
		TParm start = this.getStateList("SYS_REGION_FLG");
		int index = -1;
		// ѭ���Ƚ�ģ�����ƣ��ж�ѹ�Ȳ�ѯ��ģ���ڵڼ���
		for (int i = 0; i < start.getCount("ID"); i++) {
			if (start.getValue("ID", i).equals(name)) {
				index = i;
			}
		}
		TParm parm = new TParm();
		parm.setData("REGION_CODE", REGION_CODE);
		TParm result = this.query("selectStateList2", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		String state = result.getValue("STATE_LIST");
//		System.out.println("result-===" + result);
//		System.out.println("state" + state);
		if(state==null||state.equals(""))
		{
			return false;
		}
		
		if (index < 1)
			return false;
		state = state.substring(index + 1, index + 2);
		return TCM_Transform.getBoolean(state);
	}

	/*-------------------2009-3-24 zhangk end--------------------*/

	/**
	 * �õ���������Ժ������Ժ��ע��
	 */
	public TParm getAllRegion() {
		String sql = "SELECT MAIN_FLG,REGION_CODE FROM SYS_REGION WHERE ACTIVE_FLG = 'Y'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * �õ�ҽԺ����(����ȫ�ƣ����ļ�ƣ�Ӣ��ȫ��,Ӣ�ļ��)
	 *
	 * @param region_code
	 * @return TParm
	 */
	public TParm getHospitalName(String region_code) {
		String sql = "SELECT REGION_CHN_DESC, REGION_CHN_ABN, REGION_ENG_DESC, REGION_ENG_ABN "
				+ "FROM SYS_REGION "
				+ "WHERE REGION_CODE='"
				+ region_code
				+ "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
}
