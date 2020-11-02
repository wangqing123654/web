package jdo.util;

import java.sql.Timestamp;
import java.util.Map;

import jdo.sys.SYSEmrIndexTool;
import jdo.sys.SYSSupplierTool;
import jdo.sys.SYSTerminalTool;

import com.dongyang.db.TConnection;
import com.dongyang.jdo.TStrike;

/**
 *
 * <p>
 * Title: ��������
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.6.17
 * @version JavaHis 1.0
 */
public class Other extends TStrike {
	/**
	 * �������Һ�д�벡��������(SYS_EMR_INDEX)
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
	public boolean onInsertSYSEMRIndexOpdEmg(String case_no, String adm_type,
			String region_code, String mr_no, Timestamp adm_date,
			String dept_code, String dr_code, String opt_user, String opt_date,
			String opt_term, TConnection conn) {
		if (isClientlink()) {
			System.out.println("�ͷ����޷�ȡ������");
			return false;
		}
		return SYSEmrIndexTool.getInstance().onInsertOpdEmg(case_no, adm_type,
				region_code, mr_no, adm_date, dept_code, dr_code, opt_user,
				 opt_term, conn);
	}

	/**
	 * סԺ�Ǽ�д�벡��������(SYS_EMR_INDEX)
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
	public boolean onInsertSYSEMRIndexIpd(String case_no, String adm_type,
			String region_code, String ipd_no, String mr_no,
			Timestamp adm_date, String dept_code, String dr_code,
			String opt_user, String opt_date, String opt_term, TConnection conn) {
		if (isClientlink()) {
			System.out.println("�ͷ����޷�ȡ������");
			return false;
		}
		return SYSEmrIndexTool.getInstance().onInsertIpd(case_no, adm_type,
				region_code, ipd_no, mr_no, adm_date, dept_code, dr_code,
				opt_user, opt_term, conn);
	}

	/**
	 * סԺ��Ժд�벡��������(SYS_EMR_INDEX)
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
	public boolean onUpdateSYSEMRIndexIpd(String case_no, Timestamp ds_date,
			String opt_user, String opt_date, String opt_term, TConnection conn) {
		if (isClientlink()) {
			System.out.println("�ͷ����޷�ȡ������");
			return false;
		}
		return SYSEmrIndexTool.getInstance().onUpdateIpd(case_no, ds_date,
				opt_user, opt_term, conn);
	}

	/**
	 * ȡ�ö�ĩIP
	 *
	 * @param term_no
	 *            ��ĩ����
	 * @return
	 */
	public String getSYSTerminal(String term_no) {
		if (isClientlink())
			return (String) callServerMethod(term_no);
		return SYSTerminalTool.getInstance().selectdata(term_no).getValue(
				"TERM_IP", 0);
	}

	/**
	 * ȡ�ù�����״̬��Ϣ
	 *
	 * @param sup_code
	 *            �����̴���
	 * @return Map TParm{PHA_FLG,MAT_FLG,DEV_FLG,OTHER_FLG,SUP_STOP_FLG,
	 *         SUP_STOP_DATE,SUP_END_DATE}
	 */
	public Map getSYSSupplierFlg(String sup_code) {
		if (isClientlink())
			return (Map) callServerMethod(sup_code);
		return SYSSupplierTool.getInstance().selectdata(sup_code).getData();
	}
}
