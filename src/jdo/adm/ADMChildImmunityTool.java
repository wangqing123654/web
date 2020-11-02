package jdo.adm;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: ������������Ϣ</p>
 *
 * <p>Description: ������������Ϣ</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-12-10
 * @version 1.0
 */
public class ADMChildImmunityTool
    extends TJDOTool {
    /**
    * ʵ��
    */
   public static ADMChildImmunityTool instanceObject;

   /**
    * �õ�ʵ��
    * @return SYSRegionTool
    */
   public static ADMChildImmunityTool getInstance() {
       if (instanceObject == null)
           instanceObject = new ADMChildImmunityTool();
       return instanceObject;
   }

    public ADMChildImmunityTool() {
        setModuleName("adm\\ADMChildImmunityModule.x");
        onInit();
    }
    /**
     * ��ѯ��Ϣ
     * @param parm TParm
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
     * ��������
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
     * ��鲡���Ƿ���������
     * @param CASE_NO String
     * @return boolean
     */
    public boolean checkNEW_BORN_FLG(String CASE_NO){
        boolean flg = false;
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        TParm result = this.query("checkNEW_BORN_FLG",parm);
        if(result.getValue("NEW_BORN_FLG",0).equals("Y")){
            flg = true;
        }
        return flg;
    }
    /**
     * ��鲡���Ƿ��ǲ��� �������������ľ������CASE_NO
     * @return TParm
     */
    public TParm checkM_CASE_NO(String M_CASE_NO){
        TParm parm = new TParm();
        parm.setData("M_CASE_NO",M_CASE_NO);
        TParm result = this.query("checkM_CASE_NO",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
