package jdo.util;

import jdo.sys.SYSNewRegionTool;

import com.dongyang.jdo.TStrike;

/**
 *
 * <p>
 * Title: ��֯����
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
 * @author lzk 2009.6.2
 * @version JavaHis 1.0
 */
public class Organization extends TStrike {

	/**
	 * �õ�ҽԺ����ȫ��
	 *
	 * @param region_code
	 *            �������
	 * @return
	 */
	public String getHospitalCHNFullName(String region_code) {
		if (isClientlink())
			return (String) callServerMethod(region_code);
		return SYSNewRegionTool.getInstance().getHospitalName(region_code)
				.getValue("REGION_CHN_DESC", 0);
	}

        /**
         * �õ�ҽԺӢ��ȫ��
         *
         * @param region_code
         *            �������
         * @return
         */
        public String getHospitalENGFullName(String region_code) {
                if (isClientlink())
                        return (String) callServerMethod(region_code);
                return SYSNewRegionTool.getInstance().getHospitalName(region_code)
                                .getValue("REGION_ENG_DESC", 0);
        }

	/**
	 * �õ�ҽԺ���ļ��
	 *
	 * @param region_code
	 *            �������
	 * @return
	 */
	public String getHospitalCHNShortName(String region_code) {
		if (isClientlink())
			return (String) callServerMethod(region_code);
		return SYSNewRegionTool.getInstance().getHospitalName(region_code)
				.getValue("REGION_CHN_ABN", 0);
	}

	/**
	 * �Ƿ����ýṹ������
	 *
	 * @param region_code
	 *            �������
	 * @return
	 */
	public boolean getHospitalEMR(String region_code) {
		if (isClientlink())
			return (Boolean) callServerMethod(region_code);
		return SYSNewRegionTool.getInstance().isEMR(region_code);
	}

	/**
	 * �Ƿ�����LDAP
	 *
	 * @param region_code
	 *            �������
	 * @return
	 */
	public boolean getHospitalLDAP(String region_code) {
		if (isClientlink())
			return (Boolean) callServerMethod(region_code);
		return SYSNewRegionTool.getInstance().isLDAP(region_code);
	}
}
