package jdo.erd;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title:�����������µ������� </p>
 *
 * <p>Description: �����������µ�������</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company:javahis </p>
 *
 * @author sundx
 * @version 1.0
 */

public class ERDForSUMTool
    extends TJDOTool {

    /**
     * ʵ��
     */
    private static ERDForSUMTool instanceObject;

    /**
     * �õ�ʵ��
     * @return PatTool
     */
    public static ERDForSUMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ERDForSUMTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public ERDForSUMTool() {
        //����Module�ļ�
        setModuleName("erd\\ERDMainModule.x");
        onInit();
    }

    /**
     * ȡ�ü������۲���������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selERDPatInfo(TParm parm){
        return query("selERDPatInfo",parm);
    }

}
