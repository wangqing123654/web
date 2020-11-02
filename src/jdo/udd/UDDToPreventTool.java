package jdo.udd;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title: ����Ԥ��ʹ����ͳ��</p>
 *
 * <p>Description:����Ԥ��ʹ����ͳ��</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2013</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author 2013.10.28
 * @version 1.0
 */
public class UDDToPreventTool extends TJDOTool {
	public static UDDToPreventTool instanceObject;
	
	public static UDDToPreventTool getInstance(){
	if(instanceObject==null){
		instanceObject=new UDDToPreventTool();
	}
	   return instanceObject;
	}
	public UDDToPreventTool(){
		setModuleName("udd\\UDDToPreventModule.x");
        onInit();
	}
	
	/**
	 * ��ѯסԺ����
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
	 *   ����ҩƷʹ���˴�  
	 * @param parm
	 * @return
	 */
	public TParm selectdataM(TParm parm) {
		TParm result = new TParm();
		result = query("selectdataM", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
