package jdo.spc;

import com.dongyang.jdo.TJDOTool;

public class SPCPrepareDrugsTool extends TJDOTool {
	
	/**
	 * ʵ��
	 */
	private static SPCPrepareDrugsTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PatAdmTool
	 */
	public static SPCPrepareDrugsTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCPrepareDrugsTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SPCPrepareDrugsTool() {
		setModuleName("spc\\SPCPrepareDrugsModule.x");
		onInit();
	}

}
