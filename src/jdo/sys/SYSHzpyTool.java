package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.TypeTool;

/**
 *
 * <p>Title: ����ƴ��</p>
 *
 * <p>Description:����ƴ��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author ehui 200800901
 * @version 1.0
 */
public class SYSHzpyTool
    extends TJDOTool {

    /**
     * ʵ��
     */
    public static SYSHzpyTool instanceObject;

    /**
     * �õ�ʵ��
     * @return SYSHzpyTool
     */
    public static SYSHzpyTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSHzpyTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public SYSHzpyTool() {
        setModuleName("sys\\SYSHzpyModule.x");
        onInit();
    }

    /**
     * ���ݴ���ĺ��ֲ�ѯ��ƴ������ĸ
     * @param CNS_STR String
     * @return String
     */
    public String charToCode(String CNS_STR) {
        String regex = "[A-Za-z0-9]";
        char[] temp = CNS_STR.trim().toCharArray();
        //�ǿ��ж�
        if (CNS_STR == null || CNS_STR.trim().getBytes().length < 1) {
            return null;
        }
        TParm parm;
        TParm result = new TParm();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < temp.length; i++) {
            parm = new TParm();
            //����ǷǺ����ַ���ֱ�ӷ���
            if (String.valueOf(temp[i]).matches(regex)) {
                sb.append(String.valueOf(temp[i]).toUpperCase());
                continue;
            }
            parm.setData("CNS_STR", String.valueOf(temp[i]));
            result = this.query("selectPYbyCHAR", parm);
            if (result.getData("HZPY_CODE", 0) == null) {
                sb.append(String.valueOf(temp[i]));
                continue;
            }
            sb.append(String.valueOf(result.getData("HZPY_CODE", 0)));
        }
        if (sb.toString().contains("null")) {
            return null;
        }
        return sb.toString();
    }

    /**
     * ���ݴ���ĺ��ֲ�ѯȫƴ
     * @param CNS_STR String
     * @return String
     */
    public String charToAllPy(String CNS_STR) {
        String regex = "[A-Za-z0-9]";
        char[] temp = CNS_STR.trim().toCharArray();
        //�ǿ��ж�
        if (CNS_STR == null || CNS_STR.trim().getBytes().length < 1) {
            return null;
        }
        TParm parm;
        TParm result = new TParm();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < temp.length; i++) {
            parm = new TParm();
            //����ǷǺ����ַ���ֱ�ӷ���
            if (String.valueOf(temp[i]).matches(regex)) {
                sb.append(String.valueOf(temp[i]).toUpperCase());
                continue;
            }
            parm.setData("CNS_STR", String.valueOf(temp[i]));
            result = this.query("selectPYbyCHAR", parm);
            if (result.getData("HZPY_CODE", 0) == null) {
                sb.append(String.valueOf(temp[i]));
                continue;
            }
            sb.append(TypeTool.getString(result.getData("HZPY_CODE", 0)) +
                      TypeTool.getString(result.getData("HZPY_CODE2", 0)));
        }
        if (sb.toString().contains("null")) {
            return null;
        }
        return sb.toString();
    }
}
