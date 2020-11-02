package jdo.sys;

import com.dongyang.jdo.TStrike;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;

/**
 *
 * <p>Title: �û�������¼</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PATLockTool extends TStrike{
    /**
     * Ψһʵ��
     */
    private static PATLockTool instance;
    /**
     * ����ʱ���ʽ
     */
    public static DateFormat dateFormat = new SimpleDateFormat(
            "yyyy/MM/dd hh:mm:ss.SS");
    /**
     * �õ�ʵ��
     * @return TJDODBTool
     */
    public static PATLockTool getInstance()
    {
        if(instance == null)
            instance = new PATLockTool();
        return instance;
    }
    /**
     * �õ���ǰ��ʱ��
     * @return String
     */
    public static String getTime() {
        return dateFormat.format(new Date());
    }
    /**
     * ������¼
     * @param text String
     * @return boolean
     */
    public boolean log(String text) {
        if(isClientlink())
            return (Boolean)callServerMethod(text);
//        System.out.println("-----------------------");
//        System.out.println("PatLockLog " + getTime());
//        System.out.println(text);
//        System.out.println("-----------------------");
        return true;
    }
}
