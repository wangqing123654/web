package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: ��������ͳ�Ʊ�Tool</p>
 *
 * <p>Description: ��������ͳ�Ʊ�Tool</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-13
 * @version 4.0
 */
public class MRORT001TypeTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static MRORT001TypeTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static MRORT001TypeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MRORT001TypeTool();
        return instanceObject;
    }
    public MRORT001TypeTool() {
        this.setModuleName("mro\\MRORT001_TypeModule.x");
        this.onInit();
    }
    /**
     * ��ѯ����
     * @param parm TParm
     * @return TParm
     */
    public TParm selectParm(TParm parm){
        TParm result = this.query("select",parm);
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
