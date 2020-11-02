package jdo.sys;

import java.sql.Timestamp;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:����������Tool
 * </p>
 *
 * <p>
 * Description:����������Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.06.14
 * @version 1.0
 */
public class SYSEmrIndexTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static SYSEmrIndexTool instanceObject;

	/**
	 * �õ�ʵ��
	 *
	 * @return PositionTool
	 */
	public static SYSEmrIndexTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSEmrIndexTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SYSEmrIndexTool() {
		setModuleName("sys\\SYSEmrIndexModule.x");
		onInit();
	}

	/**
	 * �������Һ�д��
	 *
	 * @param case_no
	 *            �����
	 * @param adm_type
	 *            �ż�ס��
	 * @param region_code
	 *            ����
	 * @param mr_no
	 *            ������
	 * @param adm_date
	 *            ������
	 * @param dept_code
	 *            ����
	 * @param dr_code
	 *            ҽ��
	 * @param opt_user
	 *            ������
	 * @param opt_date
	 *            ����ʱ��
	 * @param opt_term
	 *            ����IP
	 * @param conn
	 *            DB����
	 * @return
	 */
	public boolean onInsertOpdEmg(String case_no, String adm_type,
			String region_code, String mr_no, Timestamp adm_date,
			String dept_code, String dr_code, String opt_user,
			String opt_term, TConnection conn) {
		TParm result = new TParm();
		// �ж��Ƿ��Ѵ��ڸ�����
		if (existsData(case_no)) {
			return false;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", case_no);
		parm.setData("ADM_TYPE", adm_type);
		parm.setData("REGION_CODE", region_code);
		parm.setData("MR_NO", mr_no);
		parm.setData("ADM_DATE", adm_date);
		parm.setData("DEPT_CODE", dept_code);
		parm.setData("DR_CODE", dr_code);
		parm.setData("OPT_USER", opt_user);
		parm.setData("OPT_TERM", opt_term);
		result = this.update("insertOpdEmg", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		return true;
	}

	/**
	 * סԺ�Ǽ�д��
	 *
	 * @param case_no
	 *            �����
	 * @param adm_type
	 *            �ż�ס��
	 * @param region_code
	 *            ����
	 * @param ipd_no
	 *            סԺ��
	 * @param mr_no
	 *            ������
	 * @param adm_date
	 *            ������
	 * @param dept_code
	 *            ����
	 * @param dr_code
	 *            ҽ��
	 * @param opt_user
	 *            ������
	 * @param opt_date
	 *            ����ʱ��
	 * @param opt_term
	 *            ����IP
	 * @param conn
	 *            DB����
	 * @return
	 */
	public boolean onInsertIpd(String case_no, String adm_type,
			String region_code, String ipd_no, String mr_no,
			Timestamp adm_date, String dept_code, String dr_code,
			String opt_user, String opt_term, TConnection conn) {
		TParm result = new TParm();
		// �ж��Ƿ��Ѵ��ڸ�����
		if (existsData(case_no)) {
			return false;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", case_no);
		parm.setData("ADM_TYPE", adm_type);
		parm.setData("REGION_CODE", region_code);
		parm.setData("IPD_NO", ipd_no);
		parm.setData("MR_NO", mr_no);
		parm.setData("ADM_DATE", adm_date);
		parm.setData("DEPT_CODE", dept_code);
		parm.setData("DR_CODE", dr_code);
		parm.setData("OPT_USER", opt_user);
		parm.setData("OPT_TERM", opt_term);
		result = this.update("insertIpd", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		return true;
	}

	/**
	 * ��Ժд��
	 *
	 * @param case_no
	 *            �����
	 * @param ds_date
	 *            ��Ժʱ��
	 * @param opt_user
	 *            ������
	 * @param opt_date
	 *            ����ʱ��
	 * @param opt_term
	 *            ����IP
	 * @param conn
	 *            DB����
	 * @return
	 */
	public boolean onUpdateIpd(String case_no, Timestamp ds_date,
			String opt_user, String opt_term, TConnection conn) {
		TParm result = new TParm();
		TParm parm = new TParm();
		parm.setData("CASE_NO", case_no);
		parm.setData("DR_DATE", ds_date);
		parm.setData("OPT_USER", opt_user);
		parm.setData("OPT_TERM", opt_term);
		result = this.update("updateIpd", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		return true;
	}

	/**
	 * ����֮ǰ�ж��Ƿ��Դ��ڸ�����
	 *
	 * @param case_no
	 *            �����
	 * @return
	 */
	public boolean existsData(String case_no) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", case_no);
		return getResultInt(query("existdata", parm), "COUNT") > 0;
	}

}
