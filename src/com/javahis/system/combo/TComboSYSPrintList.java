package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import javax.print.PrintService;
import java.awt.print.PrinterJob;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
/**
 *
 * <p>Title: ��ӡ�������б�</p>
 *
 * <p>Description: ��ӡ�������б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.06.29
 * @version 1.0
 */
public class TComboSYSPrintList
    extends TComboBox {
    /**
     * ������
     */
    public TComboSYSPrintList() {
        PrintService[] service = PrinterJob.lookupPrintServices();
        String[] names = new String[service.length];
        for (int i = 0; i < service.length; i++) {
            names[i] = service[i].getName();
        }
        if(names==null||names.length==0)
            return;
        setData(names);
    }
    /**
     * �½�����ĳ�ʼֵ
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null)
            return;
        object.setValue("Width", "81");
        object.setValue("Height", "23");
        object.setValue("Text", "TButton");
        object.setValue("Tip", "��ӡ���б�");
        object.setValue("showText","N");
        object.setValue("TableShowList", "id");
    }

    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
    }

}
