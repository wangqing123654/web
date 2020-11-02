package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TDPanel;
import com.dongyang.tui.DText;
import com.dongyang.tui.text.MFocus;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EText;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.ETable;
import com.dongyang.tui.text.ETR;
import com.dongyang.tui.text.ETD;
import com.dongyang.tui.text.DString;
import com.dongyang.util.TypeTool;
import com.dongyang.tui.text.ui.CTable;
import com.dongyang.data.TParm;
import com.dongyang.ui.TFrame;
import com.dongyang.tui.text.MFile;
import com.dongyang.tui.text.MSyntax;
import com.dongyang.tui.text.MPage;
import com.dongyang.ui.TWindow;
import com.dongyang.tui.text.EPic;
import com.dongyang.tui.text.EMacroroutine;
import com.dongyang.tui.text.EFixed;
import com.dongyang.ui.TShowZoomCombo;
import com.dongyang.ui.TWord;
import com.dongyang.ui.TToolButton;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TPanel;
import com.dongyang.tui.text.EPage;


/**
 *
 * <p>Title: �����߿�����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.3.30
 * @version 1.0
 */
public class WordControl extends TControl{
    //��������
    private TWord word;
    private DText text;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        word = (TWord)getComponent("WORD");
        text = word.getWordText();
        word.setShowZoomComboTag("ShowZoom");
        word.setFontComboTag("ModifyFontCombo");
        word.setFontSizeComboTag("ModifyFontSizeCombo");
        word.setFontBoldButtonTag("FontBMenu");
        word.setFontItalicButtonTag("FontIMenu");
        word.setAlignmentLeftButtonTag("AlignmentLeft");
        word.setAlignmentCenterButtonTag("AlignmentCenter");
        word.setAlignmentRightButtonTag("AlignmentRight");
    }
    /**
     * ��ʼ�����
     */
    /*public void initPanel()
    {
        text = new DText();
        text.setTag("text");
        text.setBorder("��");
        text.setAutoBaseSize(true);
        panel.addDComponent(text);
    }*/
    /**
     * ����Table
     */
    public void onInsertTableBase()
    {
        word.insertBaseTableDialog();
    }
    /**
     * ������
     */
    public void onInsertTable()
    {
        Object obj[] = (Object[])openDialog("%ROOT%\\config\\database\\InsertTableDialog.x");
        if(obj == null)
            return;
        int columnCount = TypeTool.getInt(obj[0]);
        int rowCount = TypeTool.getInt(obj[1]);
        String sql = TypeTool.getString(obj[2]);
        boolean isRetrieve = TypeTool.getBoolean(obj[3]);
        boolean isSum = TypeTool.getBoolean(obj[4]);
        int insertDataRow = TypeTool.getInt(obj[5]);
        TParm group = (TParm)obj[6];
        boolean groupShowData = (Boolean)obj[7];
        boolean inputData = TypeTool.getBoolean(obj[8]);
        String tableID = TypeTool.getString(obj[9]);
        MFocus focusManager = text.getFocusManager();
        if(focusManager == null)
            return;
        ETable table = focusManager.insertTable(rowCount, columnCount);
        if(inputData || sql.length() > 0)
        {
            CTable cTable = table.createControl();
            //����TableID
            cTable.setTableID(tableID);
            //����SQL
            cTable.setSQL(sql);
            //���ô�������
            cTable.setInputData(inputData);
            //�����Ƿ���ʾ�ܼ�
            cTable.setHasSum(isSum);
            //���ò������ݵ��к�
            cTable.setInsertDataRow(insertDataRow);
            //���÷���
            cTable.setGroup(group);
            //������ʾ��������
            cTable.setGroupShowData(groupShowData);
            //��ʼ��
            cTable.init();
        }
        focusManager.update();
    }
    /**
     * �����
     */
    public void onInsertMacroroutine()
    {
        Object value[] = (Object[])openDialog("%ROOT%\\config\\database\\MacroroutineEditDialog.x",
                new Object[]{"<none>","",1,false,0,0,"",""});
        if(value == null)
            return;
        MFocus focusManager = text.getFocusManager();
        if(focusManager == null)
            return;
        //�����
        EMacroroutine macroroutine = focusManager.insertMacroroutine(TypeTool.getString(value[0]),TypeTool.getString(value[1]),TypeTool.getInt(value[2]));
        macroroutine.getModel().setLockSize(TypeTool.getBoolean(value[3]));
        macroroutine.getModel().setWidth(TypeTool.getInt(value[4]));
        macroroutine.getModel().setHeight(TypeTool.getInt(value[5]));

        macroroutine.getModel().setGroupName(TypeTool.getString(value[6]));
        macroroutine.getModel().setCdaName(TypeTool.getString(value[7]));

        //System.out.println("group name===="+TypeTool.getString(value[6]));
        //System.out.println("cda name===="+TypeTool.getString(value[7]));

        //����
        focusManager.update();
    }
    /**
     * �쿴��ű�
     */
    public void onLookScript()
    {
        String script = text.getPM().getSyntaxManager().createScript();
        openDialog("%ROOT%\\config\\database\\LookScriptDialog.x",script);
    }
    /**
     * �쿴�����
     */
    public void onLookScriptErr()
    {
        String err = text.getPM().getSyntaxManager().getMakeErrText();
        openDialog("%ROOT%\\config\\database\\LookScriptErrDialog.x",err);
    }
    /**
     * ���к�
     */
    public void onRunMacroroutine()
    {
        text.getPM().getMacroroutineManager().run();
        text.getPM().getFocusManager().update();
    }
    /**
     * �༭��
     */
    public void onEditMacroroutine()
    {
        text.getPM().getMacroroutineManager().edit();
        text.getPM().getFocusManager().update();
    }
    /**
     * ɾ����
     */
    public void onDeleteMacroroutine()
    {
        EComponent com = text.getFocusManager().getFocus();
        if(!(com instanceof EMacroroutine))
            return;
        ((EMacroroutine)com).delete();
        text.getPM().getFocusManager().update();
    }
    /**
     * ����
     */
    public void onSave()
    {
        text.getPM().getFileManager().onSave();
    }
    /**
     * ���Ϊ
     */
    public void onSaveAs()
    {
        text.getPM().getFileManager().onSaveAsDialog();
    }
    /**
     * ���ļ�
     */
    public void onOpenFile()
    {
        text.getPM().getFileManager().onOpenDialog();
    }
    /**
     * ҳ������
     */
    public void onPageSetup()
    {
        openDialog("%ROOT%\\config\\database\\PageSetupDialog.x",word.getPageManager());
    }
    /**
     * ɾ�����
     */
    public void onDeleteTable()
    {
        word.deleteTable();
    }
    /**
     * ��������
     */
    public void onInsertTR()
    {
        word.insertTR();
    }
    /**
     * ׷�ӱ����
     */
    public void onAppendTR()
    {
        word.appendTR();
    }
    /**
     * ɾ�������
     */
    public void onDeleteTR()
    {
        word.deleteTR();
    }
    /**
     * �Զ���ű��Ի���
     */
    public void onCustomScript()
    {
        word.onCustomScriptDialog();
    }
    /**
     * ��ѯ���
     */
    public void onRetrieveTable()
    {
        text.getPM().getMacroroutineManager().setIsRun(true);
        ETable table = text.getPM().getFocusManager().getFocusTable();
        if(table == null)
            return;
        CTable ctable = table.getCTable();
        if(ctable == null)
            return;
        ctable.retrieve();
        ctable.showData();
        text.getPM().getFocusManager().update();
    }
    /**
     * �༭���
     */
    public void onEditTable()
    {
        text.getPM().getMacroroutineManager().setIsRun(false);
        ETable table = text.getPM().getFocusManager().getFocusTable();
        if(table == null)
            return;
        CTable ctable = table.getCTable();
        if(ctable == null)
            return;
        //��ʾ�༭״̬
        ctable.showEdit();
        text.getPM().getFocusManager().update();
    }
    /**
     * ��ӡ���öԻ���
     */
    public void onPrintSetup()
    {
        word.printSetup();
    }
    /**
     * ��ӡ
     */
    public void onPrintXDDialog()
    {
        word.printXDDialog();
    }
    /**
     * ��ʾ�кſ���
     */
    public void onShowRowIDSwitch()
    {
        word.setShowRowID(!word.isShowRowID());
        word.update();
    }
    /**
     * ��ӡ
     */
    public void onPrint()
    {
        word.print();
    }
    /**
     * ҳ�����öԻ���
     */
    public void onPrintDialog()
    {
        word.printDialog();
    }
    /**
     * ����
     */
    public void onParagraphEdit()
    {
        word.onParagraphDialog();
    }
    /**
     * ����ͼ��
     */
    public void onInsertPic()
    {
        MFocus focusManager = text.getFocusManager();
        if(focusManager == null)
            return;
        //����ͼ��
        focusManager.insertPic();
        //����
        focusManager.update();
    }
    /**
     * Table����
     */
    public void onTableProperty()
    {
        ETable table = text.getPM().getFocusManager().getFocusTable();
        if(table == null)
            return;
        openDialog("%ROOT%\\config\\database\\TablePropertyDialog.x",table);
    }
    /**
     * TR����
     */
    public void onTRProperty()
    {
        ETR tr = text.getPM().getFocusManager().getFocusTR();
        if(tr == null)
            return;
        openDialog("%ROOT%\\config\\database\\TRPropertyDialog.x",tr);
    }
    /**
     * �༭
     */
    public void onEditWord()
    {
        word.onEditWord();
    }
    /**
     * ����
     */
    public void onPreviewWord()
    {
        word.onPreviewWord();
    }
    /**
     * �½�
     */
    public void onNewFile()
    {
        text.getFileManager().onNewFile();
    }
    /**
     * �ļ�����
     */
    public void onFileProperty()
    {
        MFile file = text.getPM().getFileManager();
        if(file == null)
            return;
        openDialog("%ROOT%\\config\\database\\FileProperty.x",file);
    }
    /**
     * ��ӡԤ������
     */
    public void onPreviewWindow()
    {
        MFile file = text.getPM().getFileManager();
        if(file == null)
            return;
        if(!file.isOpen())
        {
            messageBox("�ļ�û�б���!");
            return;
        }
        openPrintWindow(file.getPath() + "\\" + file.getFileName(),"DEFAULT");
    }
    /**
     * �����﷨
     */
    public void onDebugSyntax()
    {
        MSyntax msyntax = text.getPM().getSyntaxManager();
        openDialog("%ROOT%\\config\\database\\DebugSyntax.x",msyntax);
    }
    /**
     * ͼ�����
     */
    public void onDIVProperty()
    {
        text.getPM().getPageManager().getMVList().openProperty();
    }
    /**
     * ɾ��ͼ��
     */
    public void onDeletePic()
    {
        EComponent com = text.getFocusManager().getFocus();
        if(!(com instanceof EPic))
            return;
        ((EPic)com).removeThis();
        text.getPM().getFocusManager().update();
    }
    /**
     * ͼ��ͼ�����
     */
    public void onPicDIVProperty()
    {
        EComponent com = text.getFocusManager().getFocus();
        if(!(com instanceof EPic))
            return;
        ((EPic)com).getMVList().openProperty();
    }
    /**
     * ͼ��ͼ�����
     */
    public void onPicDataProperty()
    {
        EComponent com = text.getFocusManager().getFocus();
        if(!(com instanceof EPic))
            return;
        ((EPic)com).openDataProperty();
    }
    /**
     * ��ͼ�����
     */
    public void onMacroroutineDIVProperty()
    {
        EComponent com = text.getFocusManager().getFocus();
        if(!(com instanceof EMacroroutine))
            return;
        EMacroroutine m = (EMacroroutine)com;
        if(m.getModel() == null)
            return;
        m.getModel().getMVList().openProperty();
    }
    /**
     * ����Table
     */
    public void onDebugTable()
    {
        ETable table = text.getPM().getFocusManager().getFocusTable();
        if(table == null)
            return;
        table.debugModify(2);
    }
    /**
     * ��ʾȫ������
     */
    public void onDebugShowObject()
    {
        text.getPM().getPageManager().debugShow();
    }
    /**
     * ��ʾ�޸Ķ���
     */
    public void onDebugShowModify()
    {
        text.getPM().getPageManager().debugModify();
    }
    /**
     * ����ѡ������
     */
    public void onDebugShowSelected()
    {
        text.getPM().getFocusManager().getSelectedModel().debugShow();
    }
    /**
     * ����̶��ı�
     */
    public void onInsertFixedObject()
    {
        word.insertFixed();
    }
    /**
     * ���뵥ѡ
     */
    public void onInsertSingleChooseObject()
    {
        word.insertSingleChoose();
    }
    /**
     * �����ѡ
     */
    public void onInsertMultiChooseObject()
    {
        word.insertMultiChoose();
    }
    /**
     * ��������ѡ��
     */
    public void onInsertHasChooseObject()
    {
        word.insertHasChoose();
    }
    /**
     * ����������ʾ���
     */
    public void onInsertInputTextObject()
    {
        word.insertInputText();
    }
    /**
     * �����
     */
    public void onInsertMicroFieldObject()
    {
        word.insertMicroField();
    }
    /**
     * ����ץȡ
     */
    public void onInsertCaptureObject()
    {
        word.insertCaptureObject();
    }
    /**
     * ����ѡ���
     */
    public void onInsertCheckBoxChooseObject()
    {
        word.insertCheckBoxChoose();
    }
    /**
     * ����ͼƬ
     */
    public void onInsertPictureObject()
    {
        word.insertPicture();
    }
    /**
     * �̶��ı�����
     */
    public void onOpenFixedProperty()
    {
        word.onOpenFixedProperty();
    }
    /**
     * ɾ���̶��ı�
     */
    public void onRemoveFixed()
    {
        word.deleteFixed();
    }
    /**
     * ����������
     */
    public void OpenMicroFieldProperty()
    {
        word.onOpenMicroFieldProperty();
    }
    /**
     * ����
     */
    public void onCutMenu()
    {
        word.onCut();
    }
    /**
     * ����
     */
    public void onCopyMenu()
    {
        word.onCopy();
    }
    /**
     * ճ��
     */
    public void onPasteMenu()
    {
        word.onPaste();
    }
    /**
     * ɾ��
     */
    public void onDeleteMenu()
    {
        word.onDelete();
    }
    /**
     * ץȡ���ԶԻ���
     */
    public void onCaptureDataProperty()
    {
        openDialog("%ROOT%\\config\\database\\CaptureDataDialog.x",word);
    }
    /**
     * ���Դ���
     */
    public void onDebugDialog()
    {
        openDialog("%ROOT%\\config\\database\\DebugDialog.x",word);
    }
    /**
     * �ϲ���Ԫ��
     */
    public void onUniteTD()
    {
        word.onUniteTD();
    }
    /**
     * ����ǩ��
     */
    public void onKeyWordMenu()
    {
        openDialog("%ROOT%\\config\\database\\KeyWordDialog.x",word);
    }
    /**
     * ����ѡ��
     */
    public void onInsertNumberChooseObject()
    {
        word.insertNumberChoose();
    }
    /**
     * �����ļ�
     */
    public void onInsertFile()
    {
        //word.onInsertFileFrontFixed("AAA","%ROOT%\\config\\prt","a1.jhw",1,false);
        word.onInsertFileDialog();
    }
    /*public void onFontBMenu()
    {
        TToolButton button = (TToolButton)getComponent("FontBMenu");
        System.out.println(button.isSelected());
        button.setSelected(!button.isSelected());
        word.modifyBold(button.isSelected());
        System.out.println(getComponent("FontBMenu").getClass().getName());

        System.out.println("xxxxxxxxxxxxx");
    }*/
    /**
     * ����ͼƬ�༭��
     */
    public void onInsertImageEdit()
    {
        word.insertImage();
    }
    /**
     * ɾ��ͼƬ�༭��
     */
    public void onDeleteImageEdit()
    {
        word.deleteImage();
    }
    /**
     * �����
     */
    public void onInsertGBlock()
    {
        word.insertGBlock();
    }
    /**
     * ������
     */
    public void onInsertGLine()
    {
        word.insertGLine();
    }
    /**
     * ������ߴ�
     * @param index int
     */
    public void onSizeBlockMenu(String index)
    {
        word.onSizeBlockMenu(StringTool.getInt(index));
    }
    /**
     * ���ǰ�������
     */
    public void onTableInsertPanel()
    {
        ETable table = word.getFocusManager().getFocusTable();
        if(table == null)
            return;
        EPanel panel = table.getPanel();
        int index = panel.findIndex();
        EPage page = panel.getPage();
        EPanel p = page.newPanel(index);
        p.newText();
        word.update();
    }
    public static void main(String args[])
    {
        TFrame frame = com.javahis.util.JavaHisDebug.runFrame("database\\word.x");
        //TFrame frame = com.javahis.util.JavaHisDebug.runFrame("ekt\\aaa.x");
        //com.javahis.util.JavaHisDebug.runWindow("database\\OpenDialog.x",frame);
        //com.javahis.util.JavaHisDebug.runWindow("aaa\\aaa.x",frame);
        //com.javahis.util.JavaHisDebug.runDialog("database\\word.x");
    }
}
