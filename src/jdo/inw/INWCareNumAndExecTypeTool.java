package jdo.inw;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

public class INWCareNumAndExecTypeTool extends TJDOTool {
	public static INWCareNumAndExecTypeTool instanceObject;

	public INWCareNumAndExecTypeTool() {
		setModuleName("inw\\INWCareNumAndExecTypeModule.x");
		onInit();
	}

	public static INWCareNumAndExecTypeTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INWCareNumAndExecTypeTool();
		return instanceObject;
	}

	public TParm onQueryCareNum(TParm parm) {
		TParm result = query("queryCareNum", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public TParm onQueryExecType(TParm parm) {
		TParm result = query("queryExecType", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}