package jdo.reg;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * <p>
 * Title:��/����Һž�������ͳ�Ʊ���
 * </p>
 * 
 * <p>
 * Description:��/����Һž�������ͳ�Ʊ���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) bluecore 2008
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author caoyong 2013.07.01
 * @version 4.0
 */
public class REGMedicalTool extends TJDOTool {
	public static REGMedicalTool instanceObject;

	public static REGMedicalTool getInstance() {
		if (instanceObject == null)
			instanceObject = new REGMedicalTool();
		return instanceObject;
	}

	public REGMedicalTool() {
		setModuleName("reg\\REGMedicalModule.x");
		onInit();
	}

	/**
	 * ��ѯ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectdata(TParm parm) {
		TParm result = new TParm();
		result = query("selectdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ��ѯ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectdataO(TParm parm) {
		TParm result = new TParm();
		result = query("selectdataO", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
