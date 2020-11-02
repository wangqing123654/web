/**
 * @className INDNewTool.java 
 * @author litong
 * @Date 2013-3-23 
 * @version V 1.0 
 */
package jdo.udd;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * @author litong
 * @Date 2013-3-23 
 */
public class UDDNewTool extends TJDOTool {

	/**
	 * ������
	 */
	  private UDDNewTool() {
	        //����Module�ļ�
	        this.setModuleName("UDD\\UDDAntbacterPatientedDetial.x");
	        onInit();
	    }
	  /**
		 * ʵ��
		 */
		private static UDDNewTool instanceObject;

	 /**
	  * �õ�ʵ��
	  * @return
	  */
	  public static UDDNewTool getInstance(){
	        if(instanceObject == null){
	            instanceObject = new UDDNewTool();
	        }
	        return instanceObject;
	}
	  
	  /**
		 * ��ѯʹ�ÿ���ҩ���Ժ���˵���Ϣ
		 */
		
	public TParm getPatientDetail(TParm parm) {
		TParm result = query("getPatientDetail",parm);
		if(result.getErrCode() < 0){
			err("ERR:M "+result.getErrCode() + result.getErrText()
			 + result.getErrName());
			return result;
		}
		return result;	
}
}










