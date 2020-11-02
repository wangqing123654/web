package jdo.fol;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:随访参数设定Tool类
 * </p>
 * 
 * <p>
 * Description:随访参数设定Tool类
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

	/** 实例对象 */
	private static FOLParmaSettingTool instanceObject;

	/**
	 * 获取实例对象
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
	 * 查询
	 * 
	 * @return
	 */
	public TParm queryFolParm() {
		String sql = "SELECT AUTO_REMIND_FLG, BEFORE_REMIND_DAY, FOLLOWUP_DELINE FROM FOL_PARM";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 更新
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
