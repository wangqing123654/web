/**
 * @className UDDGatherTool.java 
 * @author litong
 * @Date 2013-3-24 
 * @version V 1.0 
 */
package jdo.udd;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * @author litong
 * @Date 2013-3-24 
 */
public class UDDGatherTool extends TJDOTool {
	/**
	 * ������
	 */
private UDDGatherTool () {
	//����module�ļ�
	this.setModuleName("UDD\\UDDGatherModule.x");
	onInit();
}
/**
 * ʵ��
 */
private static UDDGatherTool insenceObject;
/**
 * �õ�ʵ��
 */
public static UDDGatherTool getInstence() {
	if(insenceObject == null){
		insenceObject = new UDDGatherTool();
	}
	return insenceObject;
}
/**
 * ��ѯ��������������
 * 
 * @return TParm
 */
public TParm selectInformation1(TParm parm) {
	TParm result = query("selectInformation1", parm);
	if (result.getErrCode() < 0) {
		err("ERR:M " + result.getErrCode() + result.getErrText()
				+ result.getErrName());
		return result;
	}
	return result;
}

public TParm selectInformation3(TParm parm){
	TParm result = query("selectInformation3",parm);
	if(result.getErrCode() < 0){
		err("ERR:M "+result.getErrCode() + result.getErrText()
		 + result.getErrName());
		return result;
	}
	return result;	
}
}
