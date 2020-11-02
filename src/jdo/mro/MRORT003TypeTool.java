package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: ҽʦ��������ͳ�Ʊ�</p>
 *
 * <p>Description: ҽʦ��������ͳ�Ʊ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-13
 * @version 4.0
 */
public class MRORT003TypeTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static MRORT003TypeTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static MRORT003TypeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MRORT003TypeTool();
        return instanceObject;
    }

    public MRORT003TypeTool() {
        this.setModuleName("mro\\MRORT003_TypeModule.x");
        this.onInit();
    }
    /**
     * ��ѯ��Ժ����ͳ������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOUT(TParm parm){
        TParm result = this.query("selectOUT",parm);
        // �жϴ���ֵ
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ��Ժ����ͳ������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectIN(TParm parm){
        TParm result = this.query("selectIN",parm);
        // �жϴ���ֵ
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ��ѯ��Ժ����ͳ��������ϸ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOUTDetail(TParm parm){//add by wanglong 20130909
        TParm result = this.query("selectOUTDetail",parm);
        // �жϴ���ֵ
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ��ѯ��Ժ����ͳ��������ϸ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectINDetail(TParm parm){//add by wanglong 20130909
        TParm result = this.query("selectINDetail",parm);
        // �жϴ���ֵ
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
}
