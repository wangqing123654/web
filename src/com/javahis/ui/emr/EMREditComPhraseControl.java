package com.javahis.ui.emr;

import com.dongyang.control.TControl;
import com.dongyang.ui.TWord;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TFontCombo;
import com.dongyang.ui.TFontSizeCombo;
import com.dongyang.ui.TToolButton;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.util.Date;
import jdo.sys.Operator;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.IBlock;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.EMicroField;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EInputText;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.ENumberChoose;
import com.dongyang.tui.text.EMultiChoose;
import com.dongyang.tui.text.ETextFormat;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.tui.text.EHasChoose;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.text.ETable;
import com.dongyang.tui.text.EPage;
import com.dongyang.tui.text.EText;
import org.w3c.dom.Node;
import com.dongyang.tui.text.EMacroroutine;
import com.dongyang.tui.text.EImage;
import com.dongyang.tui.text.div.VPic;

/**
 * <p>Title: </p>
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
public class EMREditComPhraseControl
    extends TControl {
    /**
     * �õ�TWORD����
     */
    private static final String WORD = "WORD";
    /**
     * �������ͣ����
     */
    private static final String REQUEST_PARAM_OPTYPE = "opType";
    /**
     * Ƭ���ļ�·��
     */
    private static final String REQUEST_PARAM_PHRASE_FILE_PATH =
        "phraseFilePath";
    /**
     * Ƭ���ļ���
     */
    private static final String REQUEST_PARAM_PHRASE_FILE_NAME =
        "phraseFileName";
    /**
     *
     */
    private static final String REQUEST_PARAM_PHRASE_ID =
        "classCode";


    /**
     * ����
     */
    private static final String NEW_OP = "NEW";
    /**
     * �༭
     */
    private static final String EDIT_OP = "EDIT";


    /**
     * ��������
     */
    private String strOpType = "";
    /**
     * Ƭ���ļ�·��;
     */
    private String strPhraseFilePath = "";
    /**
     * Ƭ���ļ�����;
     */
    private String strPhraseFileName = "";

    private String strPhraseID = "";


    /**
     * ��ǰģ��״̬
     */
    private String onlyType;


    /**
     * ��ǰ�༭�ļ�
     */
    private String onlyEditfileName = "";


    /**
     * WORD����
     */
    private TWord word;

    public EMREditComPhraseControl() {
    }

    public void onInit() {
        //
        super.onInit();
        //��ʼ��WORD
        initWord();
        //ȡ�ò���;
        TParm inParm = (TParm)this.getParameter();
        //
        strOpType = inParm.getValue(REQUEST_PARAM_OPTYPE);
        strPhraseFilePath = inParm.getValue(REQUEST_PARAM_PHRASE_FILE_PATH);
        strPhraseFileName = inParm.getValue(REQUEST_PARAM_PHRASE_FILE_NAME);
        strPhraseID = inParm.getValue(REQUEST_PARAM_PHRASE_ID);

        /**System.out.println("===strOpType====" + strOpType);
         System.out.println("===strPhraseFilePath====" + strPhraseFilePath);
         System.out.println("===strPhraseFileName====" + strPhraseFileName);
                 System.out.println("===strPhraseID====" + strPhraseID);**/
        //
        if (strOpType.equals(NEW_OP)) {
            //�����ļ���
            onlyEditfileName = strPhraseFileName;
            this.setOnlyType(NEW_OP);
            if (!onSave()) {
                return;
            }

        }
        //��EMR�ṹ��Ƭ��
        onEditTemplet();

    }

    /**
     * ����
     */
    public boolean onSave() {
        //����
        if (onlyEditfileName.length() != 0) {
            word.setMessageBoxSwitch(false);
            //�����ļ�����
            word.setFileAuthor(Operator.getID());
            //����
            String dateStr = StringTool.getString(StringTool.getTimestamp(new
                Date()), "yyyy��MM��dd�� HHʱmm��ss��");
            //���ô�������
            word.setFileCreateDate(dateStr);
            //���ù�˾��Ϣ
            word.setFileCo("JAVAHIS");
            //�����ļ�����
            word.setFileTitle(strPhraseFileName);
            //����
            if (word.onSaveAs(strPhraseFilePath,
                              strPhraseFileName, 2)) {

                //ȡword���ı�������;

                String strWord=getWordString();
                //this.messageBox("str==" + strWord);

                String sql = "UPDATE OPD_COMTEMPLATE_PHRASE SET FILE_PATH='" +
                    strPhraseFilePath + "',";
                    sql+="FILE_NAME='" +strPhraseFileName + "',";
                    sql+="TEMPLATE_DESC='" +strWord + "'";
                    sql+=" WHERE CLASS_CODE='" + strPhraseID + "'";

                //System.out.println("==========sql=========="+sql);
                TParm parm = new TParm(this.getDBTool().update(sql));

                if (parm.getErrCode() < 0) {
                    this.messageBox("����ʧ�ܣ�");
                    word.setMessageBoxSwitch(true);
                    //ɾ���ļ�
                    delFileTempletFile(word.getFileOpenPath(),
                                       strPhraseFileName);

                    return false;
                }
                else {
                    //��������
                    if (!this.getOnlyType().equals(NEW_OP)) {
                        word.setMessageBoxSwitch(true);
                        this.messageBox("����ɹ���");
                    }

                }

                this.setOnlyType(EDIT_OP);
                return true;
            }
            else {
                this.messageBox("����ʧ�ܣ�");
                word.setMessageBoxSwitch(true);
                return false;
            }

        }
        else {
            this.messageBox("û����Ҫ�����ģ�棡");
            word.setMessageBoxSwitch(true);
            return true;
        }

    }

    /**
     * ɾ��ģ���ļ�
     * @param templetPath String
     * @param templetName String
     * @return boolean
     */
    public boolean delFileTempletFile(String templetPath, String templetName) {
        //Ŀ¼���һ����Ŀ¼FILESERVER
        String rootName = TIOM_FileServer.getRoot();
        //ģ��·��������
        String templetPathSer = TIOM_FileServer.getPath("EmrTemplet");
        //�õ�SocketͨѶ����
        TSocket socket = TIOM_FileServer.getSocket();
        //ɾ���ļ�
        return TIOM_FileServer.deleteFile(socket,
                                          rootName + templetPathSer +
                                          templetPath +
                                          "\\" + templetName + ".jhw");
    }


    /**
     * ��ʼ��WORD
     */
    public void initWord() {
        word = this.getTWord(WORD);
        //����
        word.setShowZoomComboTag("ShowZoom");
        //����
        word.setFontComboTag("ModifyFontCombo");
        //����
        word.setFontSizeComboTag("ModifyFontSizeCombo");
        //���
        word.setFontBoldButtonTag("FontBMenu");
        //б��
        word.setFontItalicButtonTag("FontIMenu");
        //ȡ���༭
        this.word.setCanEdit(false);
        //��ʼ���˵�
        initMenu();
    }

    /**
     * ��ʼ���˵�
     */
    public void initMenu() {
        if (!this.word.canEdit()) {
            this.getTFontCombo("ModifyFontCombo").setEnabled(false);
            this.getTFontSizeCombo("ModifyFontSizeCombo").setEnabled(false);
            this.getTToolButton("AlignmentLeft").setEnabled(false);
            this.getTToolButton("AlignmentCenter").setEnabled(false);
            this.getTToolButton("AlignmentRight").setEnabled(false);
            this.getTMenuItem("insertFixText").setEnabled(false);
            this.getTMenuItem("FixTextProperty").setEnabled(false);
            this.getTMenuItem("InsertSingleChoose").setEnabled(false);
            this.getTMenuItem("InsertRelateChoose").setEnabled(false);
            this.getTMenuItem("InsertMultiChoose").setEnabled(false);
            this.getTMenuItem("InsertHasChoose").setEnabled(false);
            this.getTMenuItem("InsertMicroField").setEnabled(false);
            this.getTMenuItem("MicroFieldProperty").setEnabled(false);
            this.getTMenuItem("InsertTable").setEnabled(false);
            this.getTMenuItem("DelTable").setEnabled(false);
            this.getTMenuItem("InsertTableRow").setEnabled(false);
            this.getTMenuItem("AddTableRow").setEnabled(false);
            this.getTMenuItem("DelTableRow").setEnabled(false);
            this.getTMenuItem("delFixText").setEnabled(false);
            this.getTMenuItem("addDLText").setEnabled(false);
            this.getTMenuItem("TableProperty").setEnabled(false);
            this.getTMenuItem("addInputMessage").setEnabled(false);
            this.getTMenuItem("InsertCaptureObject").setEnabled(false);
            this.getTMenuItem("CaptureDataProperty").setEnabled(false);
            this.getTMenuItem("InsertCheckBoxChooseObject").setEnabled(false);
            this.getTMenuItem("CustomScriptDialog").setEnabled(false);
            this.getTMenuItem("InsertPictureObject").setEnabled(false);
            this.getTMenuItem("InsertNumberChooseObject").setEnabled(false);
            this.getTMenuItem("CutMenu").setEnabled(false);
            this.getTMenuItem("CopyMenu").setEnabled(false);
            this.getTMenuItem("PasteMenu").setEnabled(false);
            this.getTMenuItem("DeleteMenu").setEnabled(false);
            this.getTMenuItem("DIVProperty").setEnabled(false);
        }
        else {
            this.getTFontCombo("ModifyFontCombo").setEnabled(true);
            this.getTFontSizeCombo("ModifyFontSizeCombo").setEnabled(true);
            this.getTToolButton("AlignmentLeft").setEnabled(true);
            this.getTToolButton("AlignmentCenter").setEnabled(true);
            this.getTToolButton("AlignmentRight").setEnabled(true);
            this.getTMenuItem("insertFixText").setEnabled(true);
            this.getTMenuItem("FixTextProperty").setEnabled(true);
            this.getTMenuItem("InsertSingleChoose").setEnabled(true);
            this.getTMenuItem("InsertRelateChoose").setEnabled(true);
            this.getTMenuItem("InsertMultiChoose").setEnabled(true);
            this.getTMenuItem("InsertHasChoose").setEnabled(true);
            this.getTMenuItem("InsertMicroField").setEnabled(true);
            this.getTMenuItem("MicroFieldProperty").setEnabled(true);
            this.getTMenuItem("InsertTable").setEnabled(true);
            this.getTMenuItem("DelTable").setEnabled(true);
            this.getTMenuItem("InsertTableRow").setEnabled(true);
            this.getTMenuItem("AddTableRow").setEnabled(true);
            this.getTMenuItem("DelTableRow").setEnabled(true);
            this.getTMenuItem("delFixText").setEnabled(true);
            this.getTMenuItem("addDLText").setEnabled(true);
            this.getTMenuItem("TableProperty").setEnabled(true);
            this.getTMenuItem("addInputMessage").setEnabled(true);
            this.getTMenuItem("InsertCaptureObject").setEnabled(true);
            this.getTMenuItem("CaptureDataProperty").setEnabled(true);
            this.getTMenuItem("InsertCheckBoxChooseObject").setEnabled(true);
            this.getTMenuItem("CustomScriptDialog").setEnabled(true);
            this.getTMenuItem("InsertPictureObject").setEnabled(true);
            this.getTMenuItem("InsertNumberChooseObject").setEnabled(true);
            this.getTMenuItem("CutMenu").setEnabled(true);
            this.getTMenuItem("CopyMenu").setEnabled(true);
            this.getTMenuItem("PasteMenu").setEnabled(true);
            this.getTMenuItem("DeleteMenu").setEnabled(true);
            this.getTMenuItem("DIVProperty").setEnabled(true);
        }
    }

    /**
     * �õ��˵�
     * @param tag String
     * @return TMenuItem
     */
    public TMenuItem getTMenuItem(String tag) {
        return (TMenuItem)this.getComponent(tag);
    }

    /**
     * �õ�����Combo
     * @param tag String
     * @return TFontCombo
     */
    public TFontCombo getTFontCombo(String tag) {
        return (TFontCombo)this.getComponent(tag);
    }

    /**
     * �õ������СCombo
     * @param tag String
     * @return TFontSizeCombo
     */
    public TFontSizeCombo getTFontSizeCombo(String tag) {
        return (TFontSizeCombo)this.getComponent(tag);
    }

    /**
     * �õ�TToolButton
     * @param tag String
     * @return TToolButton
     */
    public TToolButton getTToolButton(String tag) {
        return (TToolButton)this.getComponent(tag);
    }


    /**
     * �õ�WORD����
     * @param tag String
     * @return TWord
     */
    public TWord getTWord(String tag) {
        return (TWord)this.getComponent(tag);
    }

    /**
     * �༭ģ��
     */
    public void onEditTemplet() {
        //��ģ��
        this.onOpenTemplet();
        //�༭״̬(������)
        this.word.onEditWord();
        //���ÿɱ༭
        this.word.setCanEdit(true);
        //�༭״̬
        this.setOnlyType("EDIT");
        //��ʼ���˵�
        initMenu();

    }

    /**
     * ��ģ��
     */
    public void onOpenTemplet() {

        word.onOpen(strPhraseFilePath, strPhraseFileName, 2, false);
        this.word.setCanEdit(false);
        onlyEditfileName = strPhraseFileName;
        this.setOnlyType("OPEN");
        //��ʼ���˵�
        initMenu();

    }

    public String getOnlyType() {
        return onlyType;
    }

    public void setOnlyType(String onlyType) {
        this.onlyType = onlyType;
    }

    /**
     * ɾ���̶��ı�
     */
    public void onDelFixText() {
        if (onlyEditfileName.length() != 0) {
            word.deleteFixed();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * �̶��ı�����
     */
    public void onFixTextProperty() {
        if (onlyEditfileName.length() != 0) {
            word.onOpenFixedProperty();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
        setCapName();
    }

    private void setCapName() {
        EComponent com = word.getFocusManager().getFocus();
        if (! (com instanceof ECapture)) {
            return;
        }
        ECapture capture = (ECapture) com;
        if (capture.getCaptureType() == 0) {
            setNCapName(capture);
        }
        else {
            setPCapName(capture);
        }
        word.getFileManager().update();
    }

    private void setPCapName(ECapture capture) {
        IBlock block = capture.getPreviousTrueBlock();
        while (block != null) {
            if (isSetStartCapture(block)) {
                ( (ECapture) block).setName(capture.getName());
                return;
            }
            IBlock preBlock = block.getPreviousTrueBlock();
            if (preBlock != null) {
                block = preBlock;
                continue;
            }
            EPanel panel = block.getPanel().getPreviousTruePanel();
            if (panel == null || panel.size() == 0) {
                return;
            }
            block = panel.get(0);
        }
    }

    private boolean isSetStartCapture(IBlock block) {
        if (block == null) {
            return false;
        }
        if (block.getObjectType() != EComponent.CAPTURE_TYPE) {
            return false;
        }
        ECapture capture = (ECapture) block;
        if (capture.getCaptureType() != 0) {
            return false;
        }
        return true;
    }

    private void setNCapName(ECapture capture) {
        IBlock block = capture.getNextTrueBlock();
        while (block != null) {
            if (isSetEndCapture(block)) {
                ( (ECapture) block).setName(capture.getName());
                return;
            }
            IBlock nextBlock = block.getNextTrueBlock();
            if (nextBlock != null) {
                block = nextBlock;
                continue;
            }
            EPanel panel = block.getPanel().getNextTruePanel();
            if (panel == null || panel.size() == 0) {
                return;
            }
            block = panel.get(0);
        }
    }

    private boolean isSetEndCapture(IBlock block) {
        if (block == null) {
            return false;
        }
        if (block.getObjectType() != EComponent.CAPTURE_TYPE) {
            return false;
        }
        ECapture capture = (ECapture) block;
        if (capture.getCaptureType() != 1) {
            return false;
        }
        return true;
    }

    /**
     * ͼ�����
     */
    public void onDIVProperty() {
        if (onlyEditfileName.length() != 0) {
            word.onDIVProperty();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ������
     */
    public void onMicroFieldProperty() {
        if (onlyEditfileName.length() != 0) {
            word.onOpenMicroFieldProperty();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ����
     */
    public void onAlignmentLeft() {
        if (onlyEditfileName.length() != 0) {
            word.setAlignment(0);
        }
        else {
            return;
        }

    }

    /**
     * ����
     */
    public void onAlignmentCenter() {
        if (onlyEditfileName.length() != 0) {
            word.setAlignment(1);
        }
        else {
            return;
        }

    }

    /**
     * ����
     */
    public void onAlignmentRight() {
        if (onlyEditfileName.length() != 0) {
            word.setAlignment(2);
        }
        else {
            return;
        }
    }

    /**
     * �������
     */
    public void onAddDLText() {
        if (onlyEditfileName.length() != 0) {
            word.onParagraphDialog();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ����
     */
    public void onCutMenu() {
        if (onlyEditfileName.length() != 0) {
            word.onCut();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ����
     */
    public void onCopyMenu() {
        if (onlyEditfileName.length() != 0) {
            word.onCopy();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ճ��
     */
    public void onPasteMenu() {
        if (onlyEditfileName.length() != 0) {
            word.onPaste();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ɾ��
     */
    public void onDeleteMenu() {
        if (onlyEditfileName.length() != 0) {
            word.onDelete();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ����̶��ı�
     */
    public void onInsertFixText() {
        if (onlyEditfileName.length() != 0) {
            word.insertFixed();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ���뵥ѡ
     */
    public void onInsertSingleChoose() {
        if (onlyEditfileName.length() != 0) {
            word.insertSingleChoose();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * �����ѡ
     */
    public void onInsertMultiChoose() {
        if (onlyEditfileName.length() != 0) {
            word.insertMultiChoose();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ����ѡ��
     */
    public void onInsertHasChoose() {
        if (onlyEditfileName.length() != 0) {
            word.insertHasChoose();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * �����
     */
    public void onInsertMicroField() {
        if (onlyEditfileName.length() != 0) {
            word.insertMicroField();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ������
     */
    public void onInsertTable() {
        if (onlyEditfileName.length() != 0) {
            ETable etable = word.insertBaseTableDialog();
            //System.out.println("===etable ==="+etable.isShowBorder());
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ɾ�����
     */
    public void onDelTable() {
        if (onlyEditfileName.length() != 0) {
            word.deleteTable();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ��������
     */
    public void onInsertTableRow() {
        if (onlyEditfileName.length() != 0) {
            word.insertTR();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ׷�ӱ����
     */
    public void onAddTableRow() {
        if (onlyEditfileName.length() != 0) {
            word.appendTR();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ɾ�������
     */
    public void onDelTableRow() {
        if (onlyEditfileName.length() != 0) {
            word.deleteTR();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }


    /**
     * ����������ʾ���
     */
    public void onAddInputMessage() {
        if (onlyEditfileName.length() != 0) {
            word.insertInputText();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * �������
     */
    public void onTableProperty() {
        if (onlyEditfileName.length() != 0) {
            word.onTablePropertyDialog();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }


    /**
     * ����ץȡ
     */
    public void onInsertCaptureObject() {
        if (onlyEditfileName.length() != 0) {
            ECapture obj = word.insertCaptureObject();
            word.insertCaptureObject();
            obj.setCaptureType(1);
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ץȡ���ԶԻ���//String word.getCaptureValue(String name)ץȡ����
     */
    public void onCaptureDataProperty() {
        openDialog("%ROOT%\\config\\database\\CaptureDataDialog.x", word);
    }

    /**
     * ѡ���
     */
    public void onInsertCheckBoxChooseObject() {
        if (onlyEditfileName.length() != 0) {
            word.insertCheckBoxChoose();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * �Զ���ű�
     */
    public void onCustomScriptDialog() {
        if (onlyEditfileName.length() != 0) {
            word.onCustomScriptDialog();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ����ͼƬ
     */
    public void onInsertPictureObject() {
        if (onlyEditfileName.length() != 0) {
            word.insertPicture();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ����ѡ��
     */
    public void onInsertNumberChooseObject() {
        if (onlyEditfileName.length() != 0) {
            word.insertNumberChoose();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ������
     */
    public void onInsertETextFormatObject() {
        if (onlyEditfileName.length() != 0) {
            word.insertTextFormat();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ���벡��Ԫ��;
     */
    public void onInserElement() {
        //�����ĵ����Ͳ�����
        String documentType = null;
        //this.messageBox("documentType==="+documentType);
        TParm inParm = new TParm();
        inParm.setData("DOCUMENT_TYPE", documentType);

        //�򿪲��벡��Ԫ�ش��壻
        TParm reParm = (TParm)this.openDialog(
            "%ROOT%\\config\\database\\InsertElementDialog.x", inParm);
        //���ز����Ƿ�  �����|����
        insertElement(reParm);

    }

    /**
     * ͨ�����Ͳ����ӦԪ�أ�
     * �ı� 0
        ͼ�� 2
        �̶��ı� 3
        ��ѡ 4
        ��ѡ 5
        ����ѡ�� 6
        ������ʾ 7
        ����� 8
        ץȡ���� 9
        ѡ��� 10
        ͼƬ�༭ 11
        ͼƬ 13
        ���� 14
        ������ 15
     */
    private void insertElement(TParm reParm) {
        //System.out.println("====reParm====="+reParm);
        //�ؼ�����;
        String componentType = (String) reParm.getData(
            "SELECTED_COMPONENT_TYPE", 0);
        //��������
        String groupCode = (String) reParm.getData("GROUP_CODE", 0);

        //���ݱ�� ����
        String dataCode = (String) reParm.getData("DATA_CODE", 0);
        //����ֵ
        String dataDesc = (String) reParm.getData("DATA_DESC", 0);
        //����
        String macroName = (String) reParm.getData("MACRO_NAME", 0);

        //�Ƿ�Ϊ��Ҫ
        boolean isAllowNull = isAllowNull(reParm);

        //this.messageBox("==groupCode=="+groupCode);
        //this.messageBox("==macroName=="+macroName);

        //codesystem
        String codesystem = (String) reParm.getData("CODE_SYSTEM", 0);

        //ȡ��textFormat����
        if (componentType.equals("0")) {

        }
        else if (componentType.equals("2")) {

        }
        else if (componentType.equals("3")) {
            if (onlyEditfileName.length() != 0) {
                EFixed eword = word.insertFixed();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("��ѡ��ģ�棡");
            }

        }
        else if (componentType.equals("4")) {
            if (onlyEditfileName.length() != 0) {
                ESingleChoose eword = word.insertSingleChoose();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("��ѡ��ģ�棡");
            }
        }
        else if (componentType.equals("5")) {
            if (onlyEditfileName.length() != 0) {
                EMultiChoose eword = word.insertMultiChoose();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("��ѡ��ģ�棡");
            }

        }
        else if (componentType.equals("6")) {
            if (onlyEditfileName.length() != 0) {
                EHasChoose eword = word.insertHasChoose();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("��ѡ��ģ�棡");
            }

        }
        else if (componentType.equals("7")) {
            if (onlyEditfileName.length() != 0) {
                EInputText eword = word.insertInputText();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("��ѡ��ģ�棡");
            }

        }
        else if (componentType.equals("8")) {
            if (onlyEditfileName.length() != 0) {
                EMicroField eword = word.insertMicroField();
                eword.setName(dataCode);
                eword.setText(dataDesc);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

            }
            else {
                this.messageBox("��ѡ��ģ�棡");
            }

        }
        //ץȡ
        else if (componentType.equals("9")) {
            if (onlyEditfileName.length() != 0) {
                ECapture obj = word.insertCaptureObject();
                obj.setName(dataCode);
                //obj.setText(dataDesc);

                ECapture obj1 = word.insertCaptureObject();
                obj1.setName(dataCode);
                //obj1.setText(dataDesc);
                obj1.setAllowNull(isAllowNull);
                obj1.setDataElements(true);
                obj1.setGroupName(groupCode);
                //this.messageBox("macroName=========="+macroName);
                obj1.setMicroName(macroName);

                obj.setCaptureType(1);
                obj.setAllowNull(isAllowNull);
                obj.setDataElements(true);
                obj.setGroupName(groupCode);
                obj.setMicroName(macroName);

            }
            else {
                this.messageBox("��ѡ��ģ�棡");
            }

        }
        else if (componentType.equals("10")) {
            if (onlyEditfileName.length() != 0) {
                // word.inserc.insertCheckBoxChoose();
                ECheckBoxChoose eword = word.insertCheckBoxChoose();
                eword.setName(dataCode);
                eword.setText(dataDesc);

                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("��ѡ��ģ�棡");
            }

        }
        else if (componentType.equals("11")) {

        }
        else if (componentType.equals("13")) {

        }
        //����ѡ��
        else if (componentType.equals("14")) {
            if (onlyEditfileName.length() != 0) {
                ENumberChoose eword = word.insertNumberChoose();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);
            }
            else {
                this.messageBox("��ѡ��ģ�棡");
            }

        }
        //������
        else if (componentType.equals("15")) {
            if (onlyEditfileName.length() != 0) {
                ETextFormat eword = word.insertTextFormat();
                eword.setName(dataCode);
                //this.messageBox("dataDesc"+dataDesc);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);
                //this.messageBox(codesystem);
                eword.setData(codesystem);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("��ѡ��ģ�棡");
            }

        }

    }

    /**
     * �������
     * @param reParm TParm
     * @return boolean
     */
    private boolean isAllowNull(TParm reParm) {
        String strRequire = (String) reParm.getData("REPEAT_COUNT", 0);
        String flag = strRequire.substring(0, 1);
        //this.messageBox("==flag==" + flag);
        //������  ����գ�
        if (flag.equals("0")) {
            return true;
            //����   ������գ�
        }
        else {
            return false;
        }

    }

    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    private TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * �������ѡ��
     */
    public void onInsertRelateChoose() {
        if (onlyEditfileName.length() != 0) {
            word.insertAssociateChoose();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ��ȡword���ĵ�;
     * @param word TWord
     * @return String
     */
    public String getWordString() {
        String wordText = "";
        //ȡҳ
        for (int pageIndex = 0; pageIndex < getPageCount(); pageIndex++) {
            //ȡpanels
            for (int panelIndex = 0; panelIndex < getPagePanelCount(pageIndex);
                 panelIndex++) {
                //ȡpanel�µ�ֵ;
                for (int blockIndex = 0;
                     blockIndex < getPagePanelBlockCount(pageIndex,
                    panelIndex); blockIndex++) {
                    if (!isTable(pageIndex, panelIndex, blockIndex)) {
                        IBlock block = getIBlock(pageIndex, panelIndex,
                                                 blockIndex);
                        if (block.getNextTrueBlock() != null &&
                            (block.getNextTrueBlock().findIndex()) - blockIndex >
                            1) {
                            continue;
                        }
                        wordText += setElement(block);
                        continue;
                    }

                }
            }

        }
        return wordText;
    }

    /**
     * ȡ��ҳ��
     * @return int
     */
    private int getPageCount() {
        return word.getPageManager().getComponentList().size();
    }

    /**
     * ȡ��ҳ������
     * @param pageIndex int
     * @return int
     */
    private int getPagePanelCount(int pageIndex) {
        return getEPage(pageIndex).getComponentList().size();
    }

    /**
     * �ĵ�ҳ
     * @param pageIndex int
     * @return EPage
     */
    private EPage getEPage(int pageIndex) {
        return word.getPageManager().get(pageIndex);
    }

    /**
     * ȡ��ҳ���Ԫ�ظ���
     * @param pageIndex int
     * @param panelIndex int
     * @return int
     */
    private int getPagePanelBlockCount(int pageIndex, int panelIndex) {
        return getEPanel(pageIndex, panelIndex).getComponentList().size();
    }

    /**
     * �õ����
     * @param pageIndex int
     * @param panelIndex int
     * @return EPanel
     */
    private EPanel getEPanel(int pageIndex, int panelIndex) {
        return getEPage(pageIndex).get(panelIndex);
    }


    /**
     * �ж��Ƿ��Ǳ��
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @return boolean
     */
    private boolean isTable(int pageIndex, int panelIndex, int blockIndex) {
        IBlock block = getIBlock(pageIndex, panelIndex, blockIndex);
        if (block == null) {
            return false;
        }
        return (block.getObjectType() == EComponent.TABLE_TYPE);
    }

    /**
     * �õ�����Ԫ��
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @return IBlock
     */
    private IBlock getIBlock(int pageIndex, int panelIndex, int blockIndex) {
        return getEPanel(pageIndex, panelIndex).get(blockIndex);
    }

    /**
     * ����Ԫ�������Ϣ
     * @param block IBlock
     * @param parent Node
     */
    private String setElement(IBlock block) {
        if (block instanceof EMacroroutine && ( (EMacroroutine) block).isPic()) {
            return ( (VPic) ( (EMacroroutine) block).getModel().getMVList().get(
                0).get(0)).getPictureName();
        }
        if (block instanceof EImage) {
            return ( (EImage) block).getBlockValue();
        }
        if (block.getObjectType() == EComponent.TEXT_TYPE) {
            return ( (EText) block).getString();
        }
        if (! (block instanceof EFixed)) {
            return "";
        }
        //this.messageBox("object type"+block.getObjectType());
        switch (block.getObjectType()) {
            case EComponent.CAPTURE_TYPE:
                //this.messageBox("come in1");
                if ( ( (ECapture) block).getCaptureType() == 1) {
                    return ( (ECapture) block).getValue();
                }
                // this.messageBox("come in2");
                 return "";
            case EComponent.CHECK_BOX_CHOOSE_TYPE:
                return ( (ECheckBoxChoose) block).isChecked() ? "��" : "��";
            case EComponent.TEXTFORMAT_TYPE:
                return block.getBlockValue();

            default:
                return block.getBlockValue();
        }
    }


}
