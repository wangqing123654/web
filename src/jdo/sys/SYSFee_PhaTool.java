package jdo.sys;

import java.util.Map;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SYSFee_PhaTool {

    /**
     * ʵ��
     */
    private static SYSFee_PhaTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PatTool
     */
    public static SYSFee_PhaTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSFee_PhaTool();
        return instanceObject;
    }

    public SYSFee_PhaTool() {
    }

    public TParm exeSave(TParm parm,TConnection conn){
        //�õ�Ҫִ�е�SQL
     String[] InwSql = parm.getStringArray("sysphasql");
   //  System.out.println("�����ִ��SQL������---------��" + InwSql);
     //ִ�д�����SQL���
     Map rtn=TJDODBTool.getInstance().update(InwSql, conn);
     //��װ��TParm��֤
     TParm result=new TParm(rtn);
     if (result.getErrCode() < 0) {
         return result;
     }
     //����IND�ṩ��Tool����ִ��


     if (result.getErrCode() < 0) {
         return result;
     }
        return result;

    }
}
