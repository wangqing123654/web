/**
 * @className UDDKJGather.java 
 * @author litong
 * @Date 2013-3-25 
 * @version V 1.0 
 */
package jdo.udd;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * @author litong
 * @Date 2013-3-25 
 */
public class UDDKJGatherTool extends TJDOTool{
	
	/**
	 * ������
	 */
 private UDDKJGatherTool(){
	 //����module�ļ�
	 this.setModuleName("UDD\\UDDKJGather.x");
	 onInit();	 
 }	
 /**
  * ʵ��
  */
 public static UDDKJGatherTool instenceObject;
 /**
  * �õ�ʵ��
  */
 public static UDDKJGatherTool getInstance() {
	 if (instenceObject == null) {
		instenceObject = new UDDKJGatherTool();
	}
	return instenceObject;
}
 public TParm selectInformation2(TParm parm){
		TParm result = query("selectInformation2",parm);
		if(result.getErrCode() < 0){
			err("ERR:M "+result.getErrCode() + result.getErrText()
			 + result.getErrName());
			return result;
		}
		return result;	
	}
}
