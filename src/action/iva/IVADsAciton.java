package action.iva;

import jdo.iva.IVAAllocatecheckTool;
import jdo.iva.IVADeploymentTool;
import jdo.iva.IVADispensingTool;
import jdo.iva.IVAPutMedicineWorkCheckTool;
import jdo.iva.IVAPutMedicineWorkTool;
import jdo.iva.IVARefundMedicineTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class IVADsAciton extends TAction {

	// ≈≈“©±£¥Ê
	public TParm onUpdatePut(TParm parm) {
		TConnection conn = getConnection();
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		resultM = IVAPutMedicineWorkTool.getInstance().updateInfoM(parm, conn);

		if (resultM.getErrCode() < 0) {
			err("ERR:" + resultM.getErrCode() + resultM.getErrText()
					+ resultM.getErrName());
			conn.rollback();
			return resultM;
		}
		resultD = IVAPutMedicineWorkTool.getInstance().updateInfoD(parm, conn);

		if (resultD.getErrCode() < 0) {
			err("ERR:" + resultD.getErrCode() + resultD.getErrText()
					+ resultD.getErrName());
			conn.rollback();
			return resultD;
		}
		conn.commit();
		conn.close();
		return resultD;
	}
	
	// ≈≈“©∫À∂‘±£¥Ê
	public TParm onUpdatePutCheck(TParm parm) {
		TConnection conn = getConnection();
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		resultM = IVAPutMedicineWorkCheckTool.getInstance().updateInfoM(parm, conn);

		if (resultM.getErrCode() < 0) {
			err("ERR:" + resultM.getErrCode() + resultM.getErrText()
					+ resultM.getErrName());
			conn.rollback();
			return resultM;
		}
		resultD = IVAPutMedicineWorkCheckTool.getInstance().updateInfoD(parm, conn);

		if (resultD.getErrCode() < 0) {
			err("ERR:" + resultD.getErrCode() + resultD.getErrText()
					+ resultD.getErrName());
			conn.rollback();
			return resultD;
		}
		conn.commit();
		conn.close();
		return resultD;
	}

	// µ˜≈‰±£¥Ê
	public TParm onUpdateDep(TParm parm) {
		TConnection conn = getConnection();
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		resultM = IVADeploymentTool.getInstance().updateInfoM(parm, conn);

		if (resultM.getErrCode() < 0) {
			err("ERR:" + resultM.getErrCode() + resultM.getErrText()
					+ resultM.getErrName());
			conn.rollback();
			return resultM;
		}
		resultD = IVADeploymentTool.getInstance().updateInfoD(parm, conn);

		if (resultD.getErrCode() < 0) {
			err("ERR:" + resultD.getErrCode() + resultD.getErrText()
					+ resultD.getErrName());
			conn.rollback();
			return resultD;
		}
		conn.commit();
		conn.close();
		return resultD;
	}

	// µ˜≈‰…Û∫À±£¥Ê
	public TParm onUpdateDepCheck(TParm parm) {
		TConnection conn = getConnection();
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		resultM = IVAAllocatecheckTool.getInstance().updateInfoM(parm, conn);

		if (resultM.getErrCode() < 0) {
			err("ERR:" + resultM.getErrCode() + resultM.getErrText()
					+ resultM.getErrName());
			conn.rollback();
			return resultM;
		}
		resultD = IVAAllocatecheckTool.getInstance().updateInfoD(parm, conn);

		if (resultD.getErrCode() < 0) {
			err("ERR:" + resultD.getErrCode() + resultD.getErrText()
					+ resultD.getErrName());
			conn.rollback();
			return resultD;
		}
		conn.commit();
		conn.close();
		return resultD;
	}

	// ∑¢“©±£¥Ê
	public TParm onUpdateDispensing(TParm parm) {
		TConnection conn = getConnection();
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		resultM = IVADispensingTool.getInstance().updateInfoM(parm, conn);

		if (resultM.getErrCode() < 0) {
			err("ERR:" + resultM.getErrCode() + resultM.getErrText()
					+ resultM.getErrName());
			conn.rollback();
			return resultM;
		}
		resultD = IVADispensingTool.getInstance().updateInfoD(parm, conn);

		if (resultD.getErrCode() < 0) {
			err("ERR:" + resultD.getErrCode() + resultD.getErrText()
					+ resultD.getErrName());
			conn.rollback();
			return resultD;
		}
		conn.commit();
		conn.close();
		return resultD;
	}

	// ÕÀ“©±£¥Ê
	public TParm onUpdateReturn(TParm parm) {
		TConnection conn = getConnection();
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		
		resultM = IVARefundMedicineTool.getInstance().updateInfoM(parm, conn);

		if (resultM.getErrCode() < 0) {
			err("ERR:" + resultM.getErrCode() + resultM.getErrText()
					+ resultM.getErrName());
			conn.rollback();
			return resultM;
		}
		
		resultD = IVARefundMedicineTool.getInstance().updateInfoD(parm, conn);

		if (resultD.getErrCode() < 0) {
			err("ERR:" + resultD.getErrCode() + resultD.getErrText()
					+ resultD.getErrName());
			conn.rollback();
			return resultD;
		}
		conn.commit();
		conn.close();
		return resultD;
	}
	
	public TParm onInsertMReturn(TParm parm) {
		TConnection conn = getConnection();
		TParm insertM = new TParm();
		insertM = IVARefundMedicineTool.getInstance().insertInfoM(parm, conn);
		if (insertM.getErrCode() < 0) {
			err("ERR:" + insertM.getErrCode() + insertM.getErrText()
					+ insertM.getErrName());
			conn.rollback();
			return insertM;
		}
		conn.commit();
		conn.close();
		return insertM;
	}

}
