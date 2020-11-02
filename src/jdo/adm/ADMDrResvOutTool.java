package jdo.adm;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: ��Ժ֪ͨ</p>
 *
 * <p>Description: ��Ժ֪ͨ</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-2-25
 * @version 4.0
 */
public class ADMDrResvOutTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static ADMDrResvOutTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SchWeekTool
     */
    public static ADMDrResvOutTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMDrResvOutTool();
        return instanceObject;
    }

    public ADMDrResvOutTool() {
        setModuleName("adm\\ADMDrResvOutModule.x");
        onInit();
    }
    /**
     * ��ѯ��Ժ֪ͨ���ӡ��Ϣ
     * @param CASE_NO String
     * @return TParm
     */
    public TParm selectPrintInfo(String CASE_NO){
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        TParm result = this.query("selectPrintInfo",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ���������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDiag(TParm parm){
        TParm result = this.query("selectDiag",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
