package jdo.fol;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:��ò����趨Tool��
 * </p>
 * 
 * <p>
 * Description:��ò����趨Tool��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author shendr 2014-03-19
 * @version 1.0
 */
public class FOLParmaSettingTool extends TJDOTool {

	/** ʵ������ */
	private static FOLParmaSettingTool instanceObject;

	/**
	 * ��ȡʵ������
	 * 
	 * @return
	 */
	public static FOLParmaSettingTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new FOLParmaSettingTool();
		}
		return instanceObject;
	}

	/**
	 * ��ѯ
	 * 
	 * @return
	 */
	public TParm queryFolParm() {
		String sql = "SELECT AUTO_REMIND_FLG, BEFORE_REMIND_DAY, FOLLOWUP_DELINE FROM FOL_PARM";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * ����
	 * 
	 * @return
	 */
	public TParm updateFolParm(TParm parm) {
		String sql = "UPDATE FOL_PARM SET AUTO_REMIND_FLG = '"
				+ parm.getValue("AUTO_REMIND_FLG") + "',BEFORE_REMIND_DAY = "
				+ parm.getInt("BEFORE_REMIND_DAY") + ",FOLLOWUP_DELINE = "
				+ parm.getInt("FOLLOWUP_DELINE") + ",OPT_USER='"
				+ parm.getValue("OPT_USER") + "',OPT_DATE = SYSDATE,OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

}
