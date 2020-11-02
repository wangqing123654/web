package jdo.clp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title:�ٴ�������ѯTool </p>
 * 
 * <p>Description:�ٴ�������ѯTool </p>
 * 
 * <p>Company:bluecore Copyright C </p>
 * @author wukai on 20160524
 *
 */
public class CLPCliCaseQueryTool extends TJDOTool{
	
	private static CLPCliCaseQueryTool mInstance;
	
	public static CLPCliCaseQueryTool getNewInstance() {
		if(mInstance == null) {
			mInstance = new CLPCliCaseQueryTool();
		}
		return mInstance;
	} 
	
	public CLPCliCaseQueryTool () {
		this.setModuleName("clp\\CLPCliCaseQueryModule.x");
		super.onInit();
	}
		
	public TParm onQuery(TParm parm) {
		parm = this.query("query", parm);
		return parm;
	}
	
	/**
	 * ɾ������
	 * @param parm
	 * @return
	 */
	public boolean deleteCase(TParm parm) {
		if(this.update("delete",parm).getErrCode() < 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * ��ѯ�����еĲ���
	 * @return
	 */
	public TParm queryAll(){
		return query("queryAll");
	}
}
