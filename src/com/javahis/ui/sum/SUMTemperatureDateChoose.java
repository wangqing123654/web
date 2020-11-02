package com.javahis.ui.sum;

import com.dongyang.control.TControl;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

/**
 * <p>Title: ���µ�ѡ����������</p>
 *
 * <p>Description: ���µ�ѡ����������</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */

public class SUMTemperatureDateChoose extends TControl {
    /**
     * ���캯��
     */
    public SUMTemperatureDateChoose() {
    }
    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        setDefultValue();
    }
    /**
     * ����Ĭ��ֵ
     */
    public void setDefultValue(){
        setValue("DATE",TJDODBTool.getInstance().getDBTime());
    }
    /**
     * ȷ��
     */
    public void onConfirm(){
        if(getValue("DATE") == null ||
           ("" + getValue("DATE")).length() == 0||
           ("" + getValue("DATE")).equalsIgnoreCase("null"))
            return;
        setReturnValue(StringTool.getString((Timestamp)getValue("DATE"),"yyyyMMdd"));
        closeWindow();
    }
    /**
     * ȡ��
     */
    public void onCancel(){
       setReturnValue("");
       closeWindow();
    }
}
