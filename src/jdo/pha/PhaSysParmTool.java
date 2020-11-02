package jdo.pha;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: ҽԺ�ҡ����䷢��/ҩƷȫ����ʾ��</p>
 *
 * <p>Description: ��ǹ�����</p>
 *
 * <p>Copyright: JAVAHIS</p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH
 *
 * @version 1.0
 */

public class PhaSysParmTool
    extends TJDOTool {

    /**
     * ʵ��
     */
    public static PhaSysParmTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PositionTool
     */
    public static PhaSysParmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhaSysParmTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public PhaSysParmTool() {
        setModuleName("pha\\PHASysParmModule.x");
        onInit();
    }

    /**
     * ��ѯ
     * ����EXAMINE_FLG DISPENSE_FLG SEND_FLG�ı��Y/N
     */

    public boolean needExamine() {
        //��á���ҩ�Ƿ���Ҫ��˺���ҩ��ǡ�--N(������ˣ�ֱ����ҩ)Y(����ˣ�����ҩ)
        return getResultBoolean(query("needExamine"),"EXAMINEOE_FLG");
    }

    public boolean dispEqualSend() {
        //��á��Ƿ�����ҩ����ҩģʽ��--N(����)/Y(��)
        return getResultBoolean(query("dispEqualSend"),"DGTSENDOE_FLG");
    }

    /**
     * ��ѯ
     * ����EXAMINEOED_FLG�ı��Y/N
     */
    public boolean needExamineD() {
        //��á���ҩ�Ƿ���Ҫ��˺���ҩ��ǡ�--N(������ˣ�ֱ����ҩ)Y(����ˣ�����ҩ)
        return getResultBoolean(query("needExamineD"),"EXAMINEOED_FLG");
    }
}
