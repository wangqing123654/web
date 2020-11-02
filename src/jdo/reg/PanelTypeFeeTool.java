package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:�ű���ù����� </p>
 *
 * <p>Description:�ű���ù����� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.16
 * @version 1.0
 */
public class PanelTypeFeeTool extends TJDOTool{
    /**
     * ʵ��
     */
    public static PanelTypeFeeTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PanelTypeFeeTool
     */
    public static PanelTypeFeeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PanelTypeFeeTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public PanelTypeFeeTool() {
        setModuleName("reg\\REGPanelTypeFeeModule.x");
        onInit();
    }
    /**
     * ��ѯ�ű����(Tree)
     * @param clinicTypeCode String �ű�
     * @return TParm
     */
    public TParm queryTree(String clinicTypeCode) {
        TParm parm = new TParm();
        parm.setData("CLINICTYPE_CODE", clinicTypeCode);
        TParm result = query("queryTree", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;

    }
    /**
     * �����ű����
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String admType = parm.getValue("ADM_TYPE");
        String clinicTypeCode = parm.getValue("CLINICTYPE_CODE");
        String orderCode = parm.getValue("ORDER_CODE");
        if(existsClinictypefee(admType,clinicTypeCode,orderCode)){
            result.setErr(-1,"�ű����"+" �Ѿ�����!");
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
     * ���ºű����
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
     * ���ݺű��ѯ�ű����table��Ϣ
     * @param clinicTypeCode String
     * @return TParm
     */
    public TParm selectdata(String clinicTypeCode) {
        TParm parm = new TParm();
        parm.setData("CLINICTYPE_CODE",clinicTypeCode);
        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �����ż���,�ű����,���ô����ѯ�ű����
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
     * ɾ���ű����
     * @param admType String
     * @param clinicTypeCode String
     * @param orderCode String
     * @return TParm
     */
    public TParm deletedata(String admType,String clinicTypeCode,String orderCode) {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("CLINICTYPE_CODE", clinicTypeCode);
        parm.setData("ORDER_CODE", orderCode);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �ж��Ƿ���ںű����
     * @param admType String
     * @param clinicTypeCode String
     * @param orderCode String
     * @return boolean
     */
    public boolean existsClinictypefee(String admType,String clinicTypeCode,String orderCode) {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("CLINICTYPE_CODE", clinicTypeCode);
        parm.setData("ORDER_CODE", orderCode);
        return getResultInt(query("existsClinictypefee", parm), "COUNT") > 0;
    }
    /**
     * �õ�ORDE_RCODE
     * @param admType String �ż���
     * @param clinicType String �ű�
     * @return TParm
     */
    public TParm getOrderCode(String admType,String clinicType){
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("CLINICTYPE_CODE", clinicType);
        TParm result = query("getOrderCode", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
}
    /**
     * ����admtype,CLINICTYPE_CODE����ORDE_RCODE
     * @param admType String
     * @param clinicType String
     * @param receiptType String
     * @return TParm
     */
    public TParm getOrderCodeDetial(String admType,String clinicType,String receiptType){
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("CLINICTYPE_CODE", clinicType);
        parm.setData("RECEIPT_TYPE",receiptType);
        TParm result = query("getOrderCodeDetial", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
}

}
