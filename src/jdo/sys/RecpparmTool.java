package jdo.sys;

import com.dongyang.jdo.*;
import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import jdo.reg.ArvTimeTool;
import com.dongyang.ui.TComponent;
import jdo.sys.Operator;
import java.util.Vector;

/**
 * <p>Title:�������� </p>
 *
 * <p>Description:�������� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author TParm
 * @version 1.0
 */

public class RecpparmTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static RecpparmTool instanceObject;
    /**
     * �õ�ʵ��
     * @return REGArvTimeTool
     */
    public static RecpparmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new RecpparmTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public RecpparmTool() {
        setModuleName("sys\\SYSRecpparmModule.x");
        onInit();
    }

    /**
     * ��ѯ����
     * @return TParm
     */
    public TParm selectalldata() {
        return query("selectall");
    }
    /**
     * �������ݿ�
     * @return TParm
     */
    public TParm updata(TParm parm) {
        TParm result = update("updatadata",parm);
        return result;
    }
    /**
     * ��ѯ�շѴ���
     * @param parm TParm
     * @return TParm
     */
    public TParm selectChargeCode(TParm parm){
      TParm result=query("selectChargeCode",parm);
      if(result.getErrCode() < 0)
         {
             err(result.getErrCode() + " " + result.getErrText());
             return result;
         }
      return result;
    }
}
