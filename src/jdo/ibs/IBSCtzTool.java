package jdo.ibs;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
*
* <p>Title: סԺ��ݼ�¼��
*
* <p>Description: סԺ��ݼ�¼��</p>
*
* <p>Copyright: Copyright (c) 
*
* <p>Company: BlueCore</p>
*
* @author pangb 2012-2-4
* @version 4.0
*/
public class IBSCtzTool  extends TJDOTool{
	 /**
     * ʵ��
     */
    public static IBSCtzTool instanceObject;
    /**
     * �õ�ʵ��
     * @return IBSBilldTool
     */
    public static IBSCtzTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IBSCtzTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public IBSCtzTool() {
        setModuleName("ibs\\IBSCtzModule.x");
        onInit();
    }
    public TParm insertIBSCtz(TParm parm,TConnection connection){
    	 TParm result = new TParm();
         result = update("insertIBSCtz",parm, connection);
         if(result.getErrCode()<0){
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
    }
}
