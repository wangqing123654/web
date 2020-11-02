package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title:  ���������Ա���� </p>
 *
 * <p>Description:  ���������Ա���� </p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110708
 * @version 1.0
 */
public class CLPOverPersonManagerTool extends TJDOTool{
    public CLPOverPersonManagerTool() {
         setModuleName("clp\\CLPOverPersonManagerModule.x");
         onInit();
     }
     /**
     * ʵ��
     */
    public static CLPOverPersonManagerTool instanceObject;

     /**
      * �õ�ʵ��
      * @return IBSTool
      */
     public static CLPOverPersonManagerTool getInstance() {
         if (instanceObject == null)
             instanceObject = new CLPOverPersonManagerTool();
         return instanceObject;
     }
     /**
      * ��ʾ����
      * @param parm TParm
      */
     public TParm selectData(String sqlName,TParm parm){
         TParm result = query(sqlName, parm);
         if (result.getErrCode() < 0) {
             err(result.getErrCode() + " " + result.getErrText());
             return result;
         }
         return result;

     }

}
