package action.mem;

import jdo.mem.MEMMainpackageTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class MEMMainPackageAction extends TAction{
	public TParm onSavePackageData(TParm inParam){
		
		
		TConnection conn = this.getConnection();
		TParm result = new TParm();
		try{
			for(int i = 0; i < inParam.getCount("OPER"); i++){
				String oper = inParam.getValue("OPER",i);
				
					if("ADD".equals(oper)){
						result = MEMMainpackageTool.getInstance().onSavePackageData(inParam.getRow(i), conn);
					}else{
						result = MEMMainpackageTool.getInstance().onUpdatePackageData(inParam.getRow(i), conn);
					}
					if(result.getErrCode() < 0){
						conn.rollback();
						conn.close();
						return result;
					}
			}
		}catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, e.getMessage());
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	
	public TParm updateActiveFlg(TParm inParam){
		String[] packageCode = inParam.getValue("packageCodes").split(",");
		String activeFlg = inParam.getValue("ACTIVE_FLG");
		TConnection conn = this.getConnection();
		TParm result = new TParm();
		try{
			for(int i = 0; i < packageCode.length; i++){
				TParm parm = new TParm();
				parm.setData("ACTIVE_FLG",activeFlg);
				parm.setData("PACKAGE_CODE",packageCode[i]);
				parm.setData("PARENT_PACKAGE_CODE",packageCode[i]);
				result = MEMMainpackageTool.getInstance().updateActiveFlg(parm, conn);
				if(result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, e.getMessage());
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
}
