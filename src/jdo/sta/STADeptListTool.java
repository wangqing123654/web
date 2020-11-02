package jdo.sta;

import java.util.HashMap;
import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ҽ������
 * </p>
 * 
 * <p>
 * Description: ��Ƕʽҽ������
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
 * @author Miracle
 * @version JavaHis 1.0
 */
public class STADeptListTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static STADeptListTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return RegMethodTool
	 */
	public static STADeptListTool getInstance() {
		if (instanceObject == null)
			instanceObject = new STADeptListTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public STADeptListTool() {
		setModuleName("sta\\STADeptListModule.x");
		onInit();
	}

	/**
	 * ��������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertdata(TParm parm) {
		TParm result = new TParm();
		result = update("insertdata", parm);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸Ŀ�����Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatedata(TParm parm) {
		TParm result = new TParm();
		result = update("updatedata", parm);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ɾ������
	 * 
	 * @param regMethod
	 *            String
	 * @return boolean ============pangben modify 20110525 ����������
	 */
	public TParm deletedata(String staSeq, String reginCode) {
		TParm parm = new TParm();
		parm.setData("STA_SEQ", staSeq);
		parm.setData("REGION_CODE", reginCode);
		TParm result = update("deletedata", parm);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���ݲ���CODE��ѯ��Ϣ(�Һ���)
	 * 
	 * @param dept_code
	 *            String ����CODE
	 * @param regionCode
	 *            String ����CODE
	 * @return TParm //============pangben modify 20110519 ����������
	 */
	public TParm selectdata(String dept_code, String regionCode) {
		TParm parm = new TParm();
		dept_code += "%";
		parm.setData("DEPT_CODE", dept_code);
		// ============pangben modify 20110519 start
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ============pangben modify 20110519 stop
		TParm result = query("selectdata", parm);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ��������
	 * 
	 * @return String
	 */
	public String getMaxSEQ() {
		TParm result = query("selectMaxSEQ");
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return "";
		}
		return result.getValue("SEQ", 0);
	}

	/**
	 * ��ѯ�����м���е����ﲿ��
	 * 
	 * @return TParm =========pangben modify 20110519 ����������
	 */
	public TParm selectOE_DEPT(String regionCode) {
		TParm parm = new TParm();
		// =========pangben modify 20110519 start ����������
		if (null != regionCode && !"".equals(regionCode))
			parm.setData("REGION_CODE", regionCode);
		// =========pangben modify 20110519 stop
		TParm result = query("selectOE_DEPT", parm);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ�����м���סԺ����
	 * 
	 * @return TParm ===============pangben modify 20110520 ����������
	 */
	public TParm selectIPD_DEPT(TParm parm) {
		TParm result = query("selectIPD_DEPT", parm);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����CODE��ѯ��Ӧ�Ĳ�����Ϣ
	 * 
	 * @param Code
	 *            String
	 * @return TParm
	 */
	public TParm selectDeptByCode(String Code, String StationCode,
			String regionCode) {
		TParm parm = new TParm();
		parm.setData("DEPT_CODE", Code);
		if (null != StationCode && StationCode.length() > 0)
			parm.setData("STATION_CODE", StationCode);
		// ==========pangben modify 20110523 start
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ==========pangben modify 20110523 stop
		TParm result = this.query("selectDeptByCode", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����CODE��ѯ��Ӧ�Ĳ�����Ϣ
	 * 
	 * @param Code
	 *            String
	 * @return TParm
	 */
	public TParm selectNewDeptByCode(String Code, String regionCode) {
		String sql = " SELECT DISTINCT DEPT_CODE,DEPT_DESC,DEPT_LEVEL,OE_DEPT_CODE,IPD_DEPT_CODE"
				+ " FROM STA_OEI_DEPT_LIST "
				+ " WHERE DEPT_CODE ='"
				+ Code
				+ "'";
		if (null != regionCode && regionCode.length() > 0)
			sql += sql + " AND REGION_CODE ='" + regionCode + "'";
		// ==========pangben modify 20110523 stop
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����CODE��ѯ��Ӧ�Ĳ�����Ϣ
	 * 
	 * @param Code
	 *            String
	 * @return TParm
	 */
	public TParm selectNewIPDDeptCode(String Code, String regionCode) {
		//modify by lich 20150210 start
		StringBuffer sql = new StringBuffer( "SELECT DISTINCT DEPT_CODE,DEPT_DESC,DEPT_LEVEL,OE_DEPT_CODE,IPD_DEPT_CODE,TO_NUMBER(SEQ) AS SEQ "
				+ " FROM STA_OEI_DEPT_LIST "
				+ " WHERE IPD_DEPT_CODE IS NULL  ");		
		if (null != regionCode && regionCode.length() > 0)
			sql.append( " AND REGION_CODE ='" + regionCode + "'");
		if (null != Code && Code.length() > 0)
			sql .append( " AND DEPT_CODE ='" + Code + "'");
		if(sql.length()>0)
			sql.append( " ORDER BY SEQ ");
		//modify by lich 20150210 end
		// ==========pangben modify 20110523 stop
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����סԺ���ҿ���CODE��ѯ������Ϣ
	 * 
	 * @param Code
	 *            String
	 * @return TParm ==============pangben modify 20110525 ����������
	 */
	public TParm selectDeptByIPDCODE(String Code, String regionCode) {
		TParm parm = new TParm();
		parm.setData("IPD_DEPT_CODE", Code);
		// ===================pangben modify 20110525 start
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ===================pangben modify 20110525 stop
		TParm result = this.query("selectDeptByIPDCode", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����������ҿ���CODE��ѯ������Ϣ
	 * 
	 * @param Code
	 *            String
	 * @return TParm
	 */
	public TParm selectDeptByOECODE(String Code, String regionCode) {
		TParm parm = new TParm();
		parm.setData("OE_DEPT_CODE", Code);
		// ===================pangben modify 20110525 start
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ===================pangben modify 20110525 stop
		TParm result = this.query("selectDeptByOECode", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ�����м䵵����
	 * 
	 * @return TParm ===========pangben modify 20110524 ����������
	 */
	public TParm selectAll(String regionCode) {
		// ==========pangben modify 20110524 start
		TParm parm = new TParm();
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		TParm result = this.query("selectAll", parm);
		// ==========pangben modify 20110524 stop
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ȡ�ż�������ֶε�Map������ǰ̨�ж�ĳһ�����Ƿ�Ϊ�ż������
	 * 
	 * @return TParm ==============pangben modify 20110524 ����������
	 */
	public Map getOEDeptMap(String regionCode) {
		TParm data = this.selectAll(regionCode);// ==============pangben modify
		// 20110524
		Map result = new HashMap();
		for (int i = 0; i < data.getCount(); i++) {
			result.put(data.getValue("DEPT_CODE", i), data.getValue(
					"OE_DEPT_CODE", i));
		}
		return result;
	}

	/**
	 * ��ȡסԺ�����ֶε�Map������ǰ̨�ж�ĳһ�����Ƿ�ΪסԺ����
	 * 
	 * @return TParm ==============pangben modify 20110524 ����������
	 */
	public Map getIPDDeptMap(String regionCode) {
		TParm data = this.selectAll(regionCode);// ==============pangben modify
		// 20110524
		Map result = new HashMap();
		for (int i = 0; i < data.getCount(); i++) {
			result.put(data.getValue("DEPT_CODE", i), new String[] {
					data.getValue("IPD_DEPT_CODE", i),
					data.getValue("STATION_CODE", i) });
		}
		return result;
	}

}
