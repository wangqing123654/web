package jdo.nss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: ÿ���ײ�</p>
 *
 * <p>Description: ÿ���ײ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSWeeklyMenuTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static NSSWeeklyMenuTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return NSSWeeklyMenuTool
     */
    public static NSSWeeklyMenuTool getInstance() {
        if (instanceObject == null)
            instanceObject = new NSSWeeklyMenuTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public NSSWeeklyMenuTool() {
        setModuleName("nss\\NSSWeeklyMenuModule.x");
        onInit();
    }

    /**
     * ��ѯÿ���ײ�
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryNSSWeeklyMenu(TParm parm) {
        TParm result = this.query("queryNSSWeeklyMenu", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����ÿ���ײ�
     *
     * @param parm
     * @return
     */
    public TParm onInsertNSSWeeklyMenu(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("insertNSSWeeklyMenu", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����ÿ���ײ�
     *
     * @param parm
     * @return
     */
    public TParm onUpdateNSSWeeklyMenu(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("updateNSSWeeklyMenu", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ��ÿ���ײ�
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSWeeklyMenu(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("deleteNSSWeeklyMenu", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
