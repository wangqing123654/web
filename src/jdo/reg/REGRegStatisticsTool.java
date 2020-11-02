package jdo.reg;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: �Һ���Ϣͳ��</p>
 *
 * <p>Description: �Һ���Ϣͳ��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-3-18
 * @version 4.0
 */
public class REGRegStatisticsTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static REGRegStatisticsTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PanelTypeTool
     */
    public static REGRegStatisticsTool getInstance() {
        if (instanceObject == null)
            instanceObject = new REGRegStatisticsTool();
        return instanceObject;
    }
    public REGRegStatisticsTool() {
        setModuleName("reg\\REGRegStatisticsModule.x");
        onInit();
    }
    /**
     * ��ѯ�������ҵĹҺ�������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectNumForDept3(TParm parm){
        TParm result = this.query("selectNumForDept3",parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }
    /**
     * ��ѯ�������ҵĹҺ�������Ϣ
     * @return TParm
     */
    public TParm selectNumForDept2(TParm parm){
        TParm result = this.query("selectNumForDept2",parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }
    /**
     * ��ѯ�������ҵĹҺŷ�����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectNumForDept3_M(TParm parm){
        TParm result = this.query("selectNumForDept3_M",parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }
    /**
     * ��ѯ�������ҵĹҺŷ�����Ϣ
     * @return TParm
     */
    public TParm selectNumForDept2_M(TParm parm){
        TParm result = this.query("selectNumForDept2_M",parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * ��ѯ�ż��ﲿ��(��������)
     * @return TParm
     */
    //=============pangben modify 20110325 ��Ӳ���
    public TParm selectOEDpet(TParm parm){
        TParm result = this.query("selectOEDpet",parm);  //=============pangben modify 20110325 ��Ӳ��� parm
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }
    /**
     * ��ѯ��������
     * @return TParm
     */
    public TParm selectDept2(){
        TParm result = this.query("selectDept2");
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }
}
