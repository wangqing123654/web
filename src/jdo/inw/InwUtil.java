package jdo.inw;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title:��ʿվ������
 *
 * <p>Description: ����ִ��SQL���õ�һЩ��ǳ�����Ҳ���Դ�ǰֱ̨��ִ��SQL��䣩</p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-10-10
 * @version 1.0
 */
public class InwUtil
    extends TJDOTool {
    /**
     * ���캯��˽�л�
     */
    private InwUtil() {
        //���ù�����SQL�ļ�
        setModuleName("inw\\INWUtilModule.x");
        onInit();
    }

    /**
     * ʵ��
     */
    private static InwUtil instanceObject;
    /**
     * �õ�ʵ��
     * @return RuleTool
     */
    public static InwUtil getInstance() {
        if (instanceObject == null)
            instanceObject = new InwUtil();
        return instanceObject;
    }

    /**
     * �õ���ʿ���ִ��ע��
     * @return boolean
     */
    public boolean getNsCheckEXEFlg() {
        TParm result = query("getNsCheckEXEFlg");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;
        }
        //ΪN���ؼ�
        if (result.getData("NS_CHECK_FLG", 0).equals("N")) {
            return false;
        }
        //��֮Ϊ��
        return true;
    }


}
