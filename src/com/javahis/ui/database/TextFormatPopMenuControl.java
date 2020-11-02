package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.tui.text.ETextFormat;
import com.dongyang.ui.event.TTextFormatEvent;

/**
 *
 * <p>Title:TWord������ </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TextFormatPopMenuControl extends TControl{

    /**
     * ��ѡ�ı�����
     */
    private ETextFormat eTextFormat;
    private TTextFormat value;
    private String SQLID = "";
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        eTextFormat = (ETextFormat)getParameter();
        if (eTextFormat == null)
            return;

        String text = eTextFormat.getText();
        SQLID = eTextFormat.getData();
        value = (TTextFormat)getComponent("VALUE");
        value.setModifySQL(true);
        value.setPopupMenuSQL(getSQL());
        value.popupMenuRetrieve();
        value.setText(text);

        callFunction("UI|VALUE|addEventListener","VALUE->" + TTextFormatEvent.EDIT_ENTER,this,"onEnter");
        callFunction("UI|VALUE|addEventListener","VALUE->" + TTextFormatEvent.EDIT_ESC,this,"onEsc");
    }
    /**
     * �˳�
     */
    public void onEsc(){
        closeWindow();
    }

    /**
     * �رմ���
     */
    public void closeWindow(){
        if(getValueString("VALUE").length() == 0 ||
           !checkTextHas((TParm)value.getPopupMenuData(),eTextFormat.getText())){
            eTextFormat.setText("");
            eTextFormat.setCode("");
            eTextFormat.getPM().getFocusManager().update();
        }
        super.closeWindow();
    }

    /**
     * ���������¼���ֵ֪�����
     * @param parm TParm
     * @param text String
     * @return boolean
     */
    private boolean checkTextHas(TParm parm, String text){
        for(int i = 0;i < parm.getCount();i++){
            if(text.equals(parm.getValue("NAME",i)))
                return true;
        }
        return false;
    }
    /**
     * ѡ�д���
     */
    public void onEnter()
    {
        eTextFormat.setText(value.getText());
        eTextFormat.setCode(getValueString("VALUE"));
        eTextFormat.getPM().getFocusManager().update();
        closeWindow();
    }

    /**
     * �õ�Ҫִ�е�SQL
     * @return String
     */
    private String getSQL(){
        String exeSQL = " SELECT SQL_VALUE "+
                        " FROM EMR_TEXTFORMAT "+
                        " WHERE SQL_CODE = '"+SQLID+"'";
        return (new TParm(TJDODBTool.getInstance().select(exeSQL)).getValue("SQL_VALUE",0));
    }
}
