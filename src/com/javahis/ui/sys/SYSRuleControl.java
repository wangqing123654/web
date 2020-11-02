package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.ui.TTable;
/**
 *
 * <p>Title: ������������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2008.12.18
 * @version 1.0
 */
public class SYSRuleControl extends TControl{
    /**
     * �����¼�
     * @return boolean
     */
    public boolean onUpdate()
    {
        Timestamp date = SystemTool.getInstance().getDate();
        TTable table = (TTable)callFunction("UI|TABLE|getThis");
        //�����ı�
        table.acceptText();
        //���ȫ���Ķ����к�
        int rows[] = table.getModifiedRows();
        for(int i = 0;i < rows.length;i++)
        {
            table.setItem(rows[i],"OPT_USER",Operator.getID());
            table.setItem(rows[i],"OPT_DATE",date);
            table.setItem(rows[i],"OPT_TERM",Operator.getIP());
            int tot = 0;
            tot += table.getItemInt(rows[i],"CLASSIFY1");
            tot += table.getItemInt(rows[i],"CLASSIFY2");
            tot += table.getItemInt(rows[i],"CLASSIFY3");
            tot += table.getItemInt(rows[i],"CLASSIFY4");
            tot += table.getItemInt(rows[i],"CLASSIFY5");
            tot += table.getItemInt(rows[i],"SERIAL_NUMBER");
            table.setItem(rows[i],"TOT_NUMBER",tot);
            /*int sys = table.getItemInt(rows[i],"SYS_NUMBER");

            if(tot > sys)
            {
                messageBox_("���������ܳ���ϵͳ�涨��" + sys);
                return;
            }*/
        }
        if(!table.update())
        {
            messageBox("����ʧ��");
            return false;
        }
        messageBox("����ɹ�");
        return true;
    }
    /**
     * �Ƿ�رմ���
     * @return boolean true �ر� false ���ر�
     */
    public boolean onClosing()
    {
        TTable table = (TTable)callFunction("UI|TABLE|getThis");
        table.acceptText();
        if(table.getModifiedRows().length == 0)
            return true;
        int value = messageBox("��ʾ��Ϣ","�Ƿ񱣴�?",YES_NO_CANCEL_OPTION);
        if(value == 0)
        {
            if(!onUpdate())
                return false;
            return true;
        }
        if(value == 1)
            return true;
        return false;
    }

}
