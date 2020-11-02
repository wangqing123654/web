package jdo.ope;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ��������Tool</p>
 *
 * <p>Description: ��������Tool</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-24
 * @version 1.0
 */
public class OPEOpBookTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static OPEOpBookTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static OPEOpBookTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPEOpBookTool();
        return instanceObject;
    }

    public OPEOpBookTool() {
        this.setModuleName("ope\\OPEOpBookModule.x");
        this.onInit();
    }
    /**
     * ��������������Ϣ
     * @return TParm
     */
    public TParm insertOpBook(TParm parm){
        TParm result = this.update("insertOpBook",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ����������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOpBook(TParm parm){
        TParm result = this.query("selectOpBook",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �޸�����������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm updateOpBook(TParm parm){
        TParm result = this.update("updateOpBook",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �޸�����������ų̲�����Ϣ(�����ų�)
     * @param parm TParm
     * @return TParm
     */
    public TParm updateOpBookForPersonnel(TParm parm){
        TParm result = this.update("updateOpBookForPersonnel",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ȡ������
     * @return TParm
     */
    public TParm cancelOpBook(String OPBOOK_SEQ){
        TParm parm = new TParm();
        parm.setData("OPBOOK_SEQ",OPBOOK_SEQ);
        TParm result = this.update("cancelOpBook",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �޸�����ԤԼ״̬  0 ���룬 1 �ų���� ��2�������
     * @param parm TParm
     * @return TParm
     */
    public TParm updateOPEState(TParm parm,TConnection conn){
        TParm result = this.update("updateOPEState",parm,conn);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
