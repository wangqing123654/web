package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: ��Ժҽʦ��ֵͳ�Ʊ�Tool</p>
 *
 * <p>Description: ��Ժҽʦ��ֵͳ�Ʊ�Tool</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-13
 * @version 4.0
 */
public class MRORT004DsScoreTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static MRORT004DsScoreTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static MRORT004DsScoreTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MRORT004DsScoreTool();
        return instanceObject;
    }

    public MRORT004DsScoreTool() {
        this.setModuleName("mro\\MRORT004_DsScoreModule.x");
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
