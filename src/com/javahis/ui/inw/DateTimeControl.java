package com.javahis.ui.inw;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;

/**
 * <p>Title: ��ʼ����ǰʱ��ʹ�ã�Ӧ����ʱ��ؼ����ڵ�pannel��Contorl</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author ZangJH 2009-10-30
 *
 * @version 1.0
 */

public class DateTimeControl
    extends TControl {
    public DateTimeControl() {
    }

    public void onInit() {
        super.onInit();

        Timestamp date = TJDODBTool.getInstance().getDBTime();
        //�õ�ǰʱ���ʼ��ʱ��ؼ�
        TTextFormat stratDate = (TTextFormat)this.getComponent("START_DATE");
        TTextFormat endDate = (TTextFormat)this.getComponent("END_DATE");
        stratDate.setValue(date);
        endDate.setValue(date);
    }

}
