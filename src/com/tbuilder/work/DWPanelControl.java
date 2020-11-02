package com.tbuilder.work;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TDWToolEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
import com.dongyang.ui.datawindow.DText;
import javax.swing.JLabel;
import com.dongyang.ui.TComponent;
import com.dongyang.config.INode;
import com.dongyang.manager.TCM_Transform;

public class DWPanelControl extends TControl{
    public static final String DWT = "DWT";
    public static final String DW = "DW";
    /**
     * �ļ���
     */
    private String filename;
    /**
     * ����
     */
    private String name;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        super.onInit();
        TParm parm = (TParm) getParameter();
        if(parm != null)
        {
            //�õ�����
            name = parm.getValue("NAME");
            //�õ��ļ���
            filename = parm.getValue("FILE_NAME");
            //��������
            callMessage("UI|setTitle|" + name);
            callFunction("UI|" + DWT + "|setFileName", filename);
        }
        callFunction("UI|" + DWT + "|addEventListener",TDWToolEvent.TEXT_EDIT_START,this,"onTextEditStart");
        callFunction("UI|" + DWT + "|addEventListener",TDWToolEvent.TEXT_EDIT_END,this,"onTextEditEnd");
        callFunction("UI|" + DWT + "|addEventListener",TDWToolEvent.SELECT_ONE_TEXT,this,"onSelectOneText");
        callFunction("UI|ModifyText|addEventListener",TTextFieldEvent.KEY_RELEASED,this,"onModifyText");
    }
    /**
     * ����
     */
    public void onSave()
    {
        callFunction("UI|" + DWT + "|saveFile");
    }
    /**
     * ȡ������
     */
    public void onSOTBArrow()
    {
        callFunction("UI|" + DWT + "|setState","");
    }
    /**
     * �����ı�
     */
    public void onSOTBText()
    {
        callFunction("UI|" + DWT + "|setState","Create Text");
    }
    /**
     * ������
     */
    public void onSOTBColumn()
    {
        callFunction("UI|" + DWT + "|setState","Create Column");
    }
    /**
     * ������
     */
    public void onSOTBLine()
    {
        callFunction("UI|" + DWT + "|setState","Create Line");
    }
    /**
     * ����ͼ
     */
    public void onSOTBPicture()
    {
        callFunction("UI|" + DWT + "|setState","Create Picture");
    }
    /**
     * ������ť
     */
    public void onSOTBButton()
    {
        callFunction("UI|" + DWT + "|setState","Create Button");
    }
    /**
     * ������
     */
    public void onSOTBGroup()
    {
        callFunction("UI|" + DWT + "|setState","Create Group");
    }
    /**
     * ����
     */
    public void onUndo()
    {
        callFunction("UI|" + DWT + "|onUndo");
    }
    /**
     * �ָ�
     */
    public void onRedo()
    {
        callFunction("UI|" + DWT + "|onRedo");
    }
    /**
     * ɾ��
     */
    public void onDelete()
    {
        callFunction("UI|" + DWT + "|onDelete");
    }
    /**
     * �ı��༭��ʼ
     * @param text String
     */
    public void onTextEditStart(String text)
    {
        callFunction("UI|ModifyText|setEnabled",true);
        callFunction("UI|ModifyText|setText",text);
    }
    /**
     * �ı��༭ֹͣ
     */
    public void onTextEditEnd()
    {
        callFunction("UI|ModifyText|setEnabled",false);
        callFunction("UI|ModifyText|setText","");
    }
    /**
     * �༭�ı�
     * @param text String
     */
    public void onModifyText(String text)
    {
        callFunction("UI|" + DWT + "|onEditText",text);
    }
    /**
     * �༭����
     */
    public void onModifyFont()
    {
        String font = (String)callFunction("UI|ModifyFont|getSelectedID");
        callFunction("UI|" + DWT + "|onEditFont",font);
    }
    /**
     * �༭����ߴ�
     */
    public void onModifyFontSize()
    {
        String fontSize = (String)callFunction("UI|ModifyFontSize|getSelectedID");
        callFunction("UI|" + DWT + "|onEditFontSize",StringTool.getInt(fontSize));
    }
    /**
     * ѡ��һ���ı�
     * @param text DText
     */
    public void onSelectOneText(DText text)
    {
        callFunction("UI|ModifyFont|setText",text.getFont().getFontName());
        callFunction("UI|ModifyFontSize|setText","" + text.getFont().getSize());
        callFunction("UI|FontTypeB|setSelected",(text.getFont().getStyle()&1) == 1);
        callFunction("UI|FontTypeI|setSelected",(text.getFont().getStyle()&2) == 2);
    }
    /**
     * ����
     */
    public void onAlignmentLeft()
    {
        callFunction("UI|" + DWT + "|editAlignment",JLabel.LEFT);
    }
    /**
     * ����
     */
    public void onAlignmentCenter()
    {
        callFunction("UI|" + DWT + "|editAlignment",JLabel.CENTER);
    }
    /**
     * ����
     */
    public void onAlignmentRight()
    {
        callFunction("UI|" + DWT + "|editAlignment",JLabel.RIGHT);
    }
    /**
     * �Ӵ�
     */
    public void onFontTypeB()
    {
        boolean selected = (Boolean)callFunction("UI|FontTypeB|isSelected");
        callFunction("UI|" + DWT + "|onEditB",selected);
    }
    /**
     * ��б
     */
    public void onFontTypeI()
    {
        boolean selected = (Boolean)callFunction("UI|FontTypeI|isSelected");
        callFunction("UI|" + DWT + "|onEditI",selected);
    }
    /**
     * ��ҳ����
     */
    public void onTABChangedAction()
    {
        TComponent com = (TComponent)callFunction("UI|TAB|getSelectedTComponent");
        if(com == null)
            return;
        if(DW.equals(com.getTag()))
        {
            INode node = (INode)callFunction("UI|" + DWT + "|getConfig");
            if(node == null)
                return;
            callFunction("UI|" + DW + "|setNodeData",node);
        }
        if("PSQL".equals(com.getTag()))
            initSQL();
        callFunction("UI|setMenuID",com.getTag());
        if(DW.equals(com.getTag()))
        {
            //��ʼ�������ߴ�
            initPriviewZoomChanged();
            //��ʼ�����ųߴ�
            initPrintZoomChanged();
        }
    }
    /**
     * ��ӡ����
     */
    public void onPrintSetup()
    {
        callFunction("UI|" + DW + "|openPageDialog");
    }
    /**
     * ��ӡ
     */
    public void onPrint()
    {
        callFunction("UI|" + DW + "|print");
    }
    /**
     * �����ߴ�
     */
    public void onPriviewZoomChanged()
    {
        double zoom = (Integer)getValue("PriviewZoom");
        callFunction("UI|" + DW + "|setPriviewZoom",zoom);
    }
    /**
     * ��ʼ�������ߴ�
     */
    public void initPriviewZoomChanged()
    {
        int zoom = TCM_Transform.getInt(callFunction("UI|" + DW + "|getPriviewZoom"));
        setValue("PriviewZoom",zoom);
    }
    /**
     * ��ӡ���ųߴ�
     */
    public void onPrintZoomChanged()
    {
        double zoom = (Integer)getValue("PrintZoom");
        callFunction("UI|" + DW + "|setPrintZoom",zoom);
    }
    /**
     * ��ʼ�����ųߴ�
     */
    public void initPrintZoomChanged()
    {
        int zoom = TCM_Transform.getInt(callFunction("UI|" + DW + "|getPrintZoom"));
        setValue("PrintZoom",zoom);
    }
    /**
     * �ύ
     */
    public void onSQLCommit()
    {
        String sql = getText("TSQL");
        callFunction("UI|ViewDataWindow|loadSQL",sql);
    }
    /**
     * ��ѯ����
     */
    public void onSQLRetrieve()
    {
        int count = (Integer)callFunction("UI|ViewDataWindow|retrieve");
        if(count < 0)
        {
            String err = (String)callFunction("UI|ViewDataWindow|getErrText");
            messageBox_(err);
        }
    }
    /**
     * ����
     */
    public void onSQLCreate()
    {
        String sql = getText("TSQL");
        callFunction("UI|" + DWT + "|load",sql);
    }
    /**
     * �༭SQL���
     */
    public void onSQLEdit()
    {
        String sql = getText("TSQL");
        callFunction("UI|" + DWT + "|setSql",sql);
    }
    /**
     * ����SQL���
     */
    public void initSQL()
    {
        String sql = (String)callFunction("UI|" + DWT + "|getSql");
        setText("TSQL",sql);
    }
}
