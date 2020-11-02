package jdo.sta;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title:������ά�� </p>
 *
 * <p>Description:������ά�� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-6
 * @version 4.0
 */
public class STASDListTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static STASDListTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static STASDListTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new STASDListTool();
        return instanceObject;
    }

    public STASDListTool() {
        setModuleName("sta\\STASDListModule.x");
        onInit();
    }
    /**
     * ��ѯ��Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm){
        TParm result = this.query("selectData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm){
        TParm result = this.update("insertData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �޸�����
     * @param parm TParm
     * @return TParm
     */
    public TParm updateData(TParm parm){
        TParm result = this.update("updateData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ������
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteData(TParm parm){
        TParm result = this.update("deleteData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ�������б�
     * @return TParm
     */
    public TParm selectSDList(){
        TParm result = this.query("selectDataList");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
