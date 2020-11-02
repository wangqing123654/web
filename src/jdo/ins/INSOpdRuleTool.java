package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:����ҽ��֧����׼������ </p>
 *
 * <p>Description:����ҽ��֧����׼������ </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2008.11.03
 * @version JavaHis 1.0
 */
public class INSOpdRuleTool extends TJDOTool{
    /**
     * ʵ��
     */
    public static INSOpdRuleTool instanceObject;
    /**
     * �õ�ʵ��
     * @return INSOpdRuleTool
     */
    public static INSOpdRuleTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INSOpdRuleTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public INSOpdRuleTool() {
        setModuleName("ins\\INSOpdRuleModule.x");
        onInit();
    }
    /**
     * ����֧����׼
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String ctz1Code = parm.getValue("CTZ1_CODE");
        String ctz2Code = parm.getValue("CTZ2_CODE");
        String orderCode = parm.getValue("ORDER_CODE");
        TParm parm1 = new TParm();
        parm1.setData("CTZ1_CODE",ctz1Code);
        if (ctz2Code == null || ctz2Code.length() < 1) {
            parm1.setData("CZT2_CODENULL", "00");
        } else {
            parm1.setData("CTZ2_CODE", ctz2Code);
            }
        parm1.setData("ORDER_CODE",orderCode);
        if(existsInsOpdRule(parm1)){
            result.setErr(-1,"֧����׼"+" �Ѿ�����!");
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
     * ����֧����׼
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
     * ��ѯ֧����׼
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
     * ɾ��֧����׼
     * @param ctz1Code String
     * @param ctz2Code String
     * @param orderCode String
     * @return TParm
     */
    public TParm deletedata(String ctz1Code,String ctz2Code,String orderCode) {
        TParm parm = new TParm();
        parm.setData("CTZ1_CODE", ctz1Code);
        if (ctz2Code == null || ctz2Code.length() < 1) {
            parm.setData("CZT2_CODENULL", "00");
        } else {
            parm.setData("CTZ2_CODE", ctz2Code);
            }
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
     * �ж��Ƿ����֧����׼
     * @param ctz1Code String �����
     * @param ctz2Code String �����
     * @param orderCode String ҽ������
     * @return boolean
     */
    public boolean existsInsOpdRule(TParm parm) {
        return getResultInt(query("existsInsOpdRule", parm), "COUNT") > 0;
    }

}
