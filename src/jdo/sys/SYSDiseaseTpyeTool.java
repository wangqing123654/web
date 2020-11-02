package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:��Ⱦ�����Tool
 * </p>
 * 
 * <p>
 * Description:��Ⱦ�����Tool
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
public class SYSDiseaseTpyeTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static SYSDiseaseTpyeTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PositionTool
	 */
	public static SYSDiseaseTpyeTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSDiseaseTpyeTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SYSDiseaseTpyeTool() {
		setModuleName("sys\\SYSDiseaseTypeModule.x");
		onInit();
	}

	/**
	 * �õ���Ⱦ����Ϣ
	 * 
	 * @param diseasetpye_code
	 *            ��Ⱦ��������
	 * @return
	 */
	public TParm getDiseaseTpye(String diseasetpye_code) {
		TParm parm = new TParm();
		parm.setData("DISEASETYPE_CODE", diseasetpye_code);
		TParm result = this.query("queryDiseaseTpye", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
