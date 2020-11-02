package jdo.opb;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: �����շ�Ա������ͳ��</p>
 *
 * <p>Description: �����շ�Ա������ͳ��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-3-24
 * @version 1.0
 */
public class OPBCashierStaTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static OPBCashierStaTool instanceObject;
    /**
     * �õ�ʵ��
     * @return BILCounteTool
     */
    public static OPBCashierStaTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPBCashierStaTool();
        return instanceObject;
    }

    public OPBCashierStaTool() {
        setModuleName("opb\\OPBCashierStaModule.x");
        onInit();
    }
    /**
     * ��ѯ�շ��������շ��ܽ��
     * @param parm TParm
     * @return TParm
     */
    public TParm selectIn(TParm parm){
        TParm result = this.query("selectIn",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ�˷��������˷��ܽ��
     * @return TParm
     */
    public TParm selectOut(TParm parm){
        TParm result = this.query("selectOut",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
    * �����շ�Աͳ�Ʊ����л��ܲ��ҵ�������region�����༰����
    * @param parm TParm
    * @return TParm
    * ==============pangben modify 20110415
    */
   public TParm selectRegionCode(TParm parm){
       TParm result = this.query("selectRegionCode",parm);
          if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText() +
                  result.getErrName());
              return result;
          }
          return result;


   }

}
