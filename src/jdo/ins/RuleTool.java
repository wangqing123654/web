package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import jdo.sys.PositionTool;
import com.dongyang.data.TParm;

/**
 *
 * <p>Title: ֧����׼������
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis
 *
 * @author wangl 2008.08.18
 * @version 1.0
 */
public class RuleTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static RuleTool instanceObject;
    /**
     * �õ�ʵ��
     * @return RuleTool
     */
    public static RuleTool getInstance() {
        if (instanceObject == null)
            instanceObject = new RuleTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public RuleTool() {
        setModuleName("ins\\INSRuleModule.x");

        onInit();
    }

    /**
     * ����֧����׼
     * @param nhiCompany String
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
//        String nhiCompany = parm.getValue("NHI_COMPANY");
//        if(existsRule(nhiCompany)){
//            result.setErr(-1,"������ "+nhiCompany+" �Ѿ�����!");
//            return result ;
//        }
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
     * @param nhiCompany String
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
     * ������λ�����ѯ֧����׼��Ϣ(�Һ���)
     * @param nhiCompany String ��λ����
     * @return TParm
     */
    public TParm selectdata(String nhiCompany,String feeType,String ctzCode,double startRange) {
        TParm parm = new TParm();
        nhiCompany += "%";
        parm.setData("NHI_COMPANY", nhiCompany);
        feeType += "%";
        parm.setData("FEE_TYPE", feeType);
        ctzCode += "%";
        parm.setData("CTZ_CODE", ctzCode);
        parm.setData("START_RANGE", startRange);
        String name = "selectdata";
        if(startRange==0)
            name = "selectdata1";
        TParm result = query(name, parm);

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
   /**
     * ɾ��ѡ��֧����׼
     * @param nhiCompany String
     * @return boolean
     */
    public TParm deletedata(String nhiCompany,String feeType,String ctzCode) {
        TParm parm = new TParm();
        parm.setData("NHI_COMPANY", nhiCompany);
        parm.setData("FEE_TYPE", feeType);
        parm.setData("CTZ_CODE", ctzCode);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * ȡ��������combo��Ϣ
     * @return TParm
     */
    public TParm getComanyCombo() {
        TParm parm = new TParm();
        return query("getComanyCombo", parm);
    }

    /**
     * ȡ���շѵȼ�combo��Ϣ
     * @return TParm
     */
    public TParm getTypeCombo() {
        TParm parm = new TParm();
        return query("getTypeCombo", parm);
    }

    /**
     * ȡ�����combo��Ϣ
     * @return TParm
     */

    public TParm getCtzCombo() {
        TParm parm = new TParm();
        return query("getCtzCombo", parm);

    }
    /**
     * �ж��Ƿ����֧����ʽ
     * @param nhiCompany String ֧����ʽ
     * @return boolean TRUE ���� FALSE ������
     */
    public boolean existsRule(String nhiCompany){
        TParm parm = new TParm();
        parm.setData("NHI_COMPANY",nhiCompany);
        return getResultInt(query("existsRule",parm),"COUNT") > 0;
    }

}
