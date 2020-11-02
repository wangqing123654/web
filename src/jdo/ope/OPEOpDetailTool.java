package jdo.ope;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ������¼</p>
 *
 * <p>Description: ������¼</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-28
 * @version 1.0
 */
public class OPEOpDetailTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static OPEOpDetailTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static OPEOpDetailTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPEOpDetailTool();
        return instanceObject;
    }

    public OPEOpDetailTool() {
        this.setModuleName("ope\\OPEOpDetailModule.x");
        this.onInit();
    }
    /**
     * ��ѯ������¼��Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm){
        TParm result = this.query("selectData",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����������¼
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm){
        TParm result = this.update("insertData",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����������¼
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm,TConnection conn){
        //�����޸��������뵥״̬�Ĳ���
        TParm stateParm = new TParm();
        stateParm.setData("OPBOOK_SEQ",parm.getValue("OPBOOK_NO"));//ԤԼ����
        //�µ�����״̬  0,���룻1,�ų���ϣ�2,�ӻ��ߣ�3,�����ҽ��ӣ�4,�����ȴ���5,������ʼ��6,���أ�7,����������8,���ز���
        stateParm.setData("STATE","7");//2�������   7Ϊ�������� wanglong 20150330
        TParm result = new TParm();
        result = this.update("insertData",parm,conn);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = OPEOpBookTool.getInstance().updateOPEState(stateParm,conn);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �޸�������¼
     * @param parm TParm
     * @return TParm
     */
    public TParm updateData(TParm parm){
        TParm result = this.update("updateData",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����������Ϣת�루������ҳʹ�ã�
     * @return TParm
     */
    public TParm intoOPEDataForMRO(String CASE_NO){
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        TParm result = this.query("intoOPEDataForMRO",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �ṹ��������¼Ҫ���������
     * @param OP_RECORD_NO String
     * @return TParm
     */
    public TParm selectForEmr(String OP_RECORD_NO){
        TParm parm = new TParm();
        parm.setData("OP_RECORD_NO",OP_RECORD_NO);
        TParm result = this.query("selectForEmr",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ������������Ų�ѯ������¼��
     * @param OP_BOOK_NO
     * @return
     */
    public String selectForEmrByBookNo(String OP_BOOK_NO) {// add by wanglong 20130608
        TParm parm = new TParm();
        parm.setData("OP_BOOK_NO", OP_BOOK_NO);
        TParm result = this.query("selectForEmrByBookNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return "";
        }
        return result.getValue("OP_RECORD_NO", 0);
    }
}
