package jdo.sys;

import com.dongyang.data.TModifiedList;
import com.dongyang.data.TParm;

/**
 * 
 * <p>
 * Title: ֤��jdoList
 * </p>
 * 
 * <p>
 * Description:֤��jdoList
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author ehui 20081111
 * @version 1.0
 */
public class JDOLicenceList extends TModifiedList  {
	
	public JDOLicenceList(){
		StringBuffer sb=new StringBuffer();
		sb.append("userId:USER_ID;");
		sb.append("lcsClassCode:LCS_CLASS_CODE");
		sb.append("lcsNo:LCS_NO;");
		sb.append("effLcsDate:EFF_LCS_DATE;");
		sb.append("endLcsDate:END_LCS_DATE;");
		sb.append("optUser:OPT_USER;");
		sb.append("optTerm:OPT_TERM");
		setMapString(sb.toString());
	}
	/**
	 * ��������
	 * @return JDOLicense
	 */
	public JDOLicense newJDOLicense() {
		JDOLicense jdoLicense = new JDOLicense();
		this.newData(jdoLicense);
		return jdoLicense;
	}

	/**
	 * �õ�����
	 * @param index int
	 * @return JDOLicense
	 */
	public JDOLicense getJDOLicense(int index) {
		return (JDOLicense) get(index);
	}

	/**
	 * ��ʼ��TPARM
	 * @param parm
	 * @return �棺�ɹ����٣�ʧ��
	 */
	public boolean initParm(TParm parm) {
		if (parm == null)
			return false;
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			JDOLicense jdoLicense = new JDOLicense();
			jdoLicense.setMapString(getMapString());
			if (!jdoLicense.initParm(parm, i))
				return false;
			add(jdoLicense);
		}
		return true;
	}
}
