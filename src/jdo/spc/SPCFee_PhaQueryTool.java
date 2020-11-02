package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class SPCFee_PhaQueryTool extends TJDOTool {

	private static SPCFee_PhaQueryTool instanceObject;

	/**
	 * ������
	 */
	public SPCFee_PhaQueryTool() {
		super.onInit();
	}

	/**
	 * ��ȡʵ������
	 * 
	 * @return
	 */
	public static SPCFee_PhaQueryTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new SPCFee_PhaQueryTool();
		}
		return instanceObject;
	}

	/**
	 * �޸��а�װPACK_UNIT
	 * 
	 * @return
	 */
	public TParm updatePhaBase(TParm parm) {
		String sql = "UPDATE PHA_BASE SET PACK_UNIT = '"
				+ parm.getValue("PACK_UNIT") + "' " 
				+ "WHERE ORDER_CODE = '"
				+ parm.getValue("ORDER_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

}
