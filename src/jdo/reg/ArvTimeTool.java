package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:��Ժʱ�乤���� </p>
 *
 * <p>Description:��Ժʱ�乤���� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.12
 * @version 1.0
 */
public class ArvTimeTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static ArvTimeTool instanceObject;
    /**
     * �õ�ʵ��
     * @return REGArvTimeTool
     */
    public static ArvTimeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ArvTimeTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public ArvTimeTool() {
        setModuleName("reg\\REGArvTimeModule.x");
        onInit();
    }
    /**
     * ������Ժʱ��
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String queGrop = parm.getValue("QUE_GROUP");
        String startTime = parm.getValue("START_TIME");
        if(existsArvTime(queGrop,startTime)){
            result.setErr(-1,"��Ժʱ��"+" �Ѿ�����!");
            return result ;
        }

        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���µ�Ժʱ��
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ݸ�����𣬿�ʼʱ���ѯ��Ժʱ��
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        TParm result = new TParm();
        result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ����Ժʱ��
     * @param queGrop String
     * @param startTime String
     * @return TParm
     */
    public TParm deletedata(String queGrop,String startTime) {
        TParm parm = new TParm();
        parm.setData("QUE_GROUP", queGrop);
        parm.setData("START_TIME", startTime);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �ж��Ƿ���ڵ�Ժʱ��
     * @param queGrop String
     * @param startTime String
     * @return boolean
     */
    public boolean existsArvTime(String queGrop,String startTime) {
        TParm parm = new TParm();
        parm.setData("QUE_GROUP", queGrop);
        parm.setData("START_TIME", startTime);
        return getResultInt(query("existsArvTime", parm), "COUNT") > 0;
    }

}
