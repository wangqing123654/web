package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;

/**
 * <p>Title: ���˵�</p>
 *
 * <p>Description: ���˵�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.5.12
 * @version 1.0
 */
public class BILReminderQueryControl
    extends TControl {

    private TTable table;

    public BILReminderQueryControl() {
    }

    /*
     * ��ʼ��
     */
    public void onInit() {
        table = (TTable)this.getComponent("TABLE");
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {

    }

    /**
     * ��շ���
     */
    public void onClear() {
        table.removeRowAll();
    }

    /**
     * ��ӡ����
     */
    public void onPrint(){

    }


}
