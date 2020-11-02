package com.javahis.ui.emr;

import com.dongyang.control.*;
import com.dongyang.ui.TMovePane;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TMouseListener;
import com.dongyang.util.StringTool;
import java.util.Date;
import jdo.sys.Operator;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_FileServer;
import java.awt.event.MouseEvent;
import javax.swing.tree.TreePath;
import com.dongyang.data.TSocket;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TFontCombo;
import com.dongyang.ui.TFontSizeCombo;
import com.dongyang.ui.TToolButton;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.EMacroroutine;
import com.dongyang.tui.text.IBlock;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.ETable;
import com.dongyang.tui.text.EImage;
import jdo.emr.EMRCreateXMLTool;

import com.dongyang.tui.text.ETextFormat;
import com.dongyang.tui.text.EInputText;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.tui.text.EMultiChoose;
import com.dongyang.tui.text.EHasChoose;
import com.dongyang.tui.text.EMicroField;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.ENumberChoose;
import com.dongyang.util.ImageTool;
import com.dongyang.ui.TWindow;
import com.dongyang.tui.text.CopyOperator;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author lix
 * @author whao
 *
 * @version 1.0
 */
public class EMREditTempletUIControl extends TControl {

    /**
     * ������
     */
    private static final String TREE = "TREE";
    /**
     * �õ�TWORD����
     */
    private static final String WORD = "WORD";
    /**
     * ���������
     */
    private static final String TABLEPANEL = "PANEL2";
    /**
     * ����
     */
    private TTreeNode treeRoot;
    /**
     * WORD����
     */
    private TWord word;
    /**
     * ��ǰģ��״̬
     */
    private String onlyType;
    /**
     * Ҫ�����ģ����Ϣ
     */
    private TParm saveParm;
    /**
     * ��ǰ�༭�ļ�
     */
    private String onlyEditfileName = "";
    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        //��ʼ����
        this.loadTree();
        //��ʼ��WORD
        initWord();
        //ע�����¼�
        initEven();

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
            this.getTMenuItem("insertElementData").setEnabled(false);
            this.getTMenuItem("insertHideElement").setEnabled(false);

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
            this.getTMenuItem("insertElementData").setEnabled(true);
            this.getTMenuItem("insertHideElement").setEnabled(true);
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
     * �õ�TToolButton
     * @param tag String
     * @return TToolButton
     */
    public TToolButton getTToolButton(String tag) {
        return (TToolButton)this.getComponent(tag);
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
     * �õ�WORD����
     * @param tag String
     * @return TWord
     */
    public TWord getTWord(String tag) {
        return (TWord)this.getComponent(tag);
    }

    /**
     * ע���¼�
     */
    public void initEven() {
        //����ѡ������Ŀ
        addEventListener(TREE + "->" + TTreeEvent.DOUBLE_CLICKED,
                         "onTreeDoubleClicked");
        //����Ҽ�
        this.callFunction("UI|" + TREE + "|addEventListener",
                          TREE + "->" + TMouseListener.MOUSE_RIGHT_CLICKED, this,
                          "mouseRightPressed");
    }

    /**
     * ����ѡ�����¼�
     * @param obj Object
     */
    public void onTreeDoubleClicked(Object obj) {
        if (obj == null) {
            return;
        }
        TTreeNode node = this.getTTree(TREE).getSelectNode();
        //�����ģ��ſɴ�;
        if (node.getType().equals("4")) {
            onOpenTemplet();
        }
    }

    /**
     * ���Ҽ��¼�
     * @param e MouseEvent
     */
    public void mouseRightPressed(MouseEvent e) {
        //�õ���·��
        TreePath path = this.getTTree(TREE).getTree().getPathForLocation(e.getX(),
            e.getY());
        //�õ����ڵ�
        TTreeNode node = (TTreeNode) path.getLastPathComponent();
    }

    /**
     * ��ʼ����
     */
    public void loadTree() {
        treeRoot = this.getTTree(TREE).getRoot();
        //ȡ���ڵ�;
        TParm rootParm = this.getTreeRoot();

        treeRoot.setText(rootParm.getValue("CLASS_DESC", 0));
        treeRoot.setType("Root");
        treeRoot.setID(rootParm.getValue("CLASS_CODE", 0));
        //��ʼ������ǰ���������Ҷ�ӽڵ�
        treeRoot.removeAllChildren();
        //�õ�EMR�ּ�������ݼ���
        TParm mainRootParm = this.getMainRootTree();

        if (mainRootParm == null) {
            return;
        }
        int rowCount = mainRootParm.getCount();
        //this.messageBox("dd");
        for (int i = 0; i < rowCount; i++) {
            // add by lx start
            TParm temp = mainRootParm.getRow(i);
            //���ݽڵ����������Ƿ�ΪĿ¼�ڵ�
            String noteType = "Root";
            //Ҷ�ڵ�
            if (mainRootParm.getValue("LEAF_FLG", i).equals("Y")) {
                noteType = "1";
            }

            //�����½ڵ�
            TTreeNode EMRClass = new TTreeNode("EMRCLASS" + i, noteType);
            //��ERM������Ϣ���õ��ڵ㵱��
            EMRClass.setText(mainRootParm.getValue("CLASS_DESC", i));
            EMRClass.setID(mainRootParm.getValue("CLASS_CODE", i));
            EMRClass.setGroup(mainRootParm.getValue("CLASS_STYLE", i));
            // TParm leafParm=new TParm();
            //EMRClass.setUserObject(new VerifyError());


            //��һ���Ľڵ��������
            if (mainRootParm.getValue("PARENT_CLASS_CODE", i).equals("ROOT")) {
                //this.messageBox("EMRClass name"+EMRClass.getText());
                treeRoot.addSeq(EMRClass);
            }
            //��������Ľڵ������Ӧ�ĸ��ڵ�����
            else {
                if (mainRootParm.getValue("PARENT_CLASS_CODE", i).length() != 0) {
                   System.out.println("EMRClass name11111============"+EMRClass.getText());
                   //
                    treeRoot.findNodeForID(mainRootParm.getValue(
                        "PARENT_CLASS_CODE", i)).add(EMRClass);
                }
            }
            //�ж��Ƿ�ΪҶ�ڵ㣬��������Ӧ��ģ��
            if (mainRootParm.getValue("LEAF_FLG", i).equals("Y")) {

                //������������ڵ�
                TParm childrenTreeParm = this.getChildTreeType(temp.getValue(
                    "CLASS_CODE"));
                int childRowCount = childrenTreeParm.getCount();
                for (int j = 0; j < childRowCount; j++) {
                    TParm childrenTemp = childrenTreeParm.getRow(j);
                    TTreeNode childrenNode = new TTreeNode();
                    childrenNode.setID(childrenTemp.getValue("SUBCLASS_CODE"));
                    childrenNode.setText(childrenTemp.getValue("SUBCLASS_DESC"));
                    childrenNode.setValue(childrenTemp.getValue("CLASS_CODE"));
                    childrenNode.setGroup(temp.getValue("CLASS_STYLE"));
                    childrenNode.setType("4");
                    childrenNode.setData(childrenTemp);
                    //ģ�����Ҷ�ڵ�
                    EMRClass.add(childrenNode);
                }

            }
            this.getTTree(TREE).update();
        }

        /**treeRoot = this.getTTree(TREE).getRoot();
                 treeRoot.setText("ģ�������");
                 treeRoot.setType("Root");
                 treeRoot.removeAllChildren();
                 TParm mainRootParm =this.getMainRootTree();
                 int rowCount = mainRootParm.getCount();
                 for(int i=0;i<rowCount;i++){
            TParm temp = mainRootParm.getRow(i);
            TTreeNode node = new TTreeNode();
            node.setID(temp.getValue("CLASS_CODE"));
            node.setText(temp.getValue("CLASS_DESC"));
            node.setGroup(temp.getValue("CLASS_STYLE"));
            node.setType("1");
            node.setData(temp);
            //������������ڵ�
         TParm childrenTreeParm = this.getChildTreeType(temp.getValue("CLASS_CODE"));
            int childRowCount = childrenTreeParm.getCount();
            for(int j=0;j<childRowCount;j++){
                TParm childrenTemp = childrenTreeParm.getRow(j);
                TTreeNode childrenNode = new TTreeNode();
                childrenNode.setID(childrenTemp.getValue("SUBCLASS_CODE"));
                childrenNode.setText(childrenTemp.getValue("SUBCLASS_DESC"));
                childrenNode.setValue(childrenTemp.getValue("CLASS_CODE"));
                childrenNode.setGroup(temp.getValue("CLASS_STYLE"));
                childrenNode.setType("4");
                childrenNode.setData(childrenTemp);
                node.add(childrenNode);
            }
            //��������
            treeRoot.add(node);
                 }
                 this.getTTree(TREE).update();**/
    }

    /**
     * �½��������
     */
    public void onCreatEmrType() {
        //this.messageBox("come in.");
        Object obj = this.openDialog("%ROOT%\\config\\emr\\EMRClassUI.x");
        this.loadTree();
    }

    /**
     * �½�������
     */
    public void onCreatEmrTreeType() {
        Object obj = this.openDialog("%ROOT%\\config\\emr\\EMRTreeUI.x");
        this.loadTree();
    }

    /**
     * ����
     */
    public boolean onSave() {
        //����
        if (onlyEditfileName.length() != 0) {
        	word.setMarkNone();
        	//
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
            word.setFileTitle(this.saveParm.getValue("EMT_FILENAME"));
            //���ñ�ע
            word.setFileRemark(this.saveParm.getValue("CLASS_CODE") + "|ģ���ļ�|" +
                               this.saveParm.getValue("TEMPLET_PATH") + "|" +
                               this.saveParm.getValue("EMT_FILENAME"));
            if (word.onSaveAs(this.saveParm.getValue("TEMPLET_PATH"),
                              this.saveParm.getValue("EMT_FILENAME"), 2)) {
                EMRCreateXMLTool.getInstance().createXML(saveParm.getValue(
                    "TEMPLET_PATH"),
                    saveParm.getValue("EMT_FILENAME"),
                    "EmrTemplet",
                    word);
                //this.messageBox("=========start exc new========");
                if (this.getOnlyType().equals("NEW")) {
                    TDataStore data = new TDataStore();
                    data.setSQL("SELECT * FROM EMR_TEMPLET");
                    data.retrieve();
                    int rowNumber = data.insertRow();
                    data.setItem(rowNumber, "CLASS_CODE",
                                 this.saveParm.getValue("CLASS_CODE"));
                    data.setItem(rowNumber, "SUBCLASS_CODE",
                                 this.saveParm.getValue("SUBCLASS_CODE"));
                    data.setItem(rowNumber, "SEQ", this.saveParm.getValue("SEQ"));
                    data.setItem(rowNumber, "OPT_USER", Operator.getID());
                    data.setItem(rowNumber, "OPT_DATE",
                                 StringTool.getTimestamp(new Date()));
                    data.setItem(rowNumber, "OPT_TERM", Operator.getIP());
                    data.setItem(rowNumber, "DEPT_CODE",
                                 this.saveParm.getValue("DEPT_CODE"));
                    data.setItem(rowNumber, "EMT_FILENAME",
                                 this.saveParm.getValue("EMT_FILENAME"));
                    data.setItem(rowNumber, "SUBCLASS_DESC",
                                 this.saveParm.getValue("SUBCLASS_DESC"));
                    data.setItem(rowNumber, "RUN_PROGARM",
                                 this.saveParm.getValue("RUN_PROGARM"));
                    data.setItem(rowNumber, "SUBTEMPLET_CODE",
                                 this.saveParm.getValue("SUBTEMPLET_CODE"));
                    data.setItem(rowNumber, "CLASS_STYLE",
                                 this.saveParm.getValue("CLASS_STYLE"));
                    data.setItem(rowNumber, "IPD_FLG",
                                 this.saveParm.getValue("IPD_FLG"));
                    data.setItem(rowNumber, "HRM_FLG",
                                 this.saveParm.getValue("HRM_FLG"));
                    data.setItem(rowNumber, "OPD_FLG",
                                 this.saveParm.getValue("OPD_FLG"));
                    data.setItem(rowNumber, "EMG_FLG",
                                 this.saveParm.getValue("EMG_FLG"));
                    data.setItem(rowNumber, "OIDR_FLG",
                                 this.saveParm.getValue("OIDR_FLG"));
                    data.setItem(rowNumber, "NSS_FLG",
                                 this.saveParm.getValue("NSS_FLG"));
                    data.setItem(rowNumber, "SYSTEM_CODE",
                                 this.saveParm.getValue("SYSTEM_CODE"));
                    data.setItem(rowNumber, "TEMPLET_PATH",
                                 this.saveParm.getValue("TEMPLET_PATH"));
                    data.setItem(rowNumber, "PY1", this.saveParm.getValue("PY1"));

                    data.setItem(rowNumber, "DOCUMENT_TYPE",
                                 this.saveParm.getValue("DOCUMENT_TYPE"));

                    data.setItem(rowNumber, "MBABY_FLG",
                            this.saveParm.getValue("MBABY_FLG"));
                    //
                    data.setItem(rowNumber, "PUBLIC_FLG",
                            this.saveParm.getValue("PUBLIC_FLG"));
                    //
                    data.setItem(rowNumber, "STOP_FLG",
                            this.saveParm.getValue("STOP_FLG"));
                    
                    /*data.setItem(rowNumber, "REF_FLG",
                            this.saveParm.getValue("REF_FLG"));*/

                    if (!data.update()) {
                        this.messageBox("����ʧ�ܣ�");
                        //������ʾ��WORD
                        word.setMessageBoxSwitch(true);
                        //ɾ���ļ�
                        delFileTempletFile(word.getFileOpenPath(),
                                           this.
                                           saveParm.getValue("EMT_FILENAME"));
                        return false;
                    }
                }
                //this.messageBox("=========level exc new========");
                word.setMessageBoxSwitch(true);
                this.messageBox("����ɹ���");
                this.loadTree();
                this.setOnlyType("EDIT");
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
     * �ݹ�·��
     * @param node TTreeNode
     * @param fullPath String
     * @return String
     */
    private String getPathFullName(TTreeNode node, String fullPath) {
        //String pathName="";
        if (node.getParent() != null) {
            //this.messageBox("node text==="+((TTreeNode)node.getParent()).getText());
            fullPath += ( (TTreeNode) node.getParent()).getText() + ";";

            return this.getPathFullName( (TTreeNode) node.getParent(), fullPath);
        }
        else {
            //this.messageBox("fullPath result==="+fullPath);
            return fullPath;
        }

    }

    /**
     * �½�ģ��
     */
    public void onNewTemplet() {
        TTreeNode node = this.getTTree(TREE).getSelectNode();
        String pathName = node.getText();
        String fullPath = "";
        fullPath = this.getPathFullName(node, fullPath);
        String dir[] = fullPath.split(";");
        //this.messageBox("fullPath===="+fullPath);
        String fullpathName = "";
        //this.messageBox("dir.length"+dir.length);
        //�������ڵ�
        for (int i = dir.length - 2; i >= 0; i--) {
            fullpathName += dir[i] + "\\";
        }
        pathName = "JHW\\" + fullpathName + "\\" + pathName;

        //this.messageBox("===fullpathName==="+pathName);

        String classCode = node.getID();
        TParm subParm = creatSubClassCode(classCode);

        //this.messageBox("classCode===="+classCode);
        //this.messageBox("className"+node.getText());
        //this.messageBox("SEQ===="+subParm.getValue("SEQ"));
        TParm inParm = new TParm();
        inParm.setData("OP", "ADD");
        //SUBCLASS_CODE
        inParm.setData("SUBCLASS_CODE", subParm.getData("SUBCLASS_CODE"));
        Object obj = this.openDialog("%ROOT%\\config\\emr\\EMRTempInfoUI.x",
                                     inParm);
        if (obj == null) {
            return;
        }
        TParm temp = (TParm) obj;
        String templetMName = "��ģ��";
        //�����ļ�����·��
        String emrLocal = TIOM_FileServer.getPath("EmrLocalTempFileName");
        if (temp.getValue("TITLE_TYPE").equals("1")) {
            templetMName = "�հ�ģ��";
        }
        if (temp.getValue("TITLE_TYPE").equals("2")) {
            templetMName = "����ģ��";
        }
        if (temp.getValue("TITLE_TYPE").equals("3")) {
            templetMName = "ͼƬģ��";
        }
        if (temp.getValue("TITLE_TYPE").equals("4")) {
            templetMName = "A5��ģ��";
        }
        if (temp.getValue("TITLE_TYPE").equals("5")) {
            templetMName = "A5����ģ��";
        }
        if (temp.getValue("TITLE_TYPE").equals("6")) {
            templetMName = "A5ͼƬģ��";
        }
        if (word.onOpen(emrLocal, templetMName, 2, false)) {
            //Ҫ���������
            TParm saveEmrData = new TParm();
            saveEmrData.setData("CLASS_CODE", classCode);
            saveEmrData.setData("SUBCLASS_CODE",
                                temp.getValue("SUBCLASS_CODE"));
            saveEmrData.setData("SEQ", subParm.getValue("SEQ"));
            saveEmrData.setData("DEPT_CODE", temp.getData("DEPT_CODE"));
            saveEmrData.setData("EMT_FILENAME", temp.getData("EMT_FILENAME"));
            saveEmrData.setData("SUBCLASS_DESC", temp.getData("SUBCLASS_DESC"));
            saveEmrData.setData("RUN_PROGARM", temp.getData("RUN_PROGARM"));
            saveEmrData.setData("SUBTEMPLET_CODE",
                                temp.getData("SUBTEMPLET_CODE"));
            saveEmrData.setData("CLASS_STYLE", temp.getData("CLASS_STYLE"));
            saveEmrData.setData("IPD_FLG", temp.getData("IPD_FLG"));
            saveEmrData.setData("HRM_FLG", temp.getData("HRM_FLG"));
            saveEmrData.setData("OPD_FLG", temp.getData("OPD_FLG"));
            saveEmrData.setData("EMG_FLG", temp.getData("EMG_FLG"));
            saveEmrData.setData("OIDR_FLG", temp.getData("OIDR_FLG"));
            saveEmrData.setData("NSS_FLG", temp.getData("NSS_FLG"));
            saveEmrData.setData("SYSTEM_CODE", temp.getData("SYSTEM_CODE"));
            saveEmrData.setData("PY1", temp.getData("PY1"));
            saveEmrData.setData("TEMPLET_PATH", pathName);
            //this.messageBox("DOCUMENT_TYPE============="+temp.getData("DOCUMENT_TYPE"));
            saveEmrData.setData("DOCUMENT_TYPE", temp.getData("DOCUMENT_TYPE"));
            saveEmrData.setData("MBABY_FLG", temp.getData("MBABY_FLG"));
            saveEmrData.setData("PUBLIC_FLG", temp.getData("PUBLIC_FLG"));
            saveEmrData.setData("STOP_FLG", temp.getData("STOP_FLG"));
            
            //saveEmrData.setData("REF_FLG", temp.getData("REF_FLG"));


            this.word.setCanEdit(true);
            this.setOnlyType("NEW");
            this.saveParm = saveEmrData;
            onlyEditfileName = templetMName;
        }
        else {
            this.messageBox("����ģ��ʧ�ܣ�");
            return;
        }
        //��ʼ���˵�
        initMenu();
    }

    /**
     * ��ģ��
     */
    public void onOpenTemplet() {

        TTreeNode node = this.getTTree(TREE).getSelectNode();
        //this.messageBox("node  type======"+node.getType());

        String templetPath = ( (TParm) node.getData()).getValue("TEMPLET_PATH");
        String templetName = ( (TParm) node.getData()).getValue("EMT_FILENAME");
        word.onOpen(templetPath, templetName, 2, false);
        word.setMarkNone();
        this.word.setCanEdit(false);
        onlyEditfileName = templetName;
        this.saveParm = (TParm) node.getData();
        this.setOnlyType("OPEN");
        //��ʼ���˵�
        initMenu();

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
     * ɾ��ģ��
     */
    public void onDelTemplet() {
        //����ѯ��ɾ����
        if (this.messageBox("ѯ��", "�Ƿ�ɾ����", 2) == 0) {

            TTreeNode node = this.getTTree(TREE).getSelectNode();
            String subclassCode = node.getID();
            TParm delParm = (TParm) node.getData();
            TDataStore data = new TDataStore();
            String templetPath = delParm.getValue("TEMPLET_PATH");
            String seq = delParm.getValue("SEQ");
            data.setSQL("SELECT * FROM EMR_TEMPLET");
            data.retrieve();
            int rowNumber = data.rowCount();
            for (int i = 0; i < rowNumber; i++) {
                if (data.getRowParm(i).getValue("SUBCLASS_CODE").equals(
                    subclassCode) &&
                    data.getRowParm(i).getValue("SEQ").equals(seq)) {
                    data.deleteRow(i);
                }
            }
            if (data.update() &&
                delFileTempletFile(templetPath, delParm.getValue("EMT_FILENAME"))) {
                this.messageBox("ɾ���ɹ���");
                if (this.word.getFileOpenName() != null &&
                    this.word.
                    getFileOpenName().equals(delParm.getValue("EMT_FILENAME"))) {
                    //�����ļ�����·��
                    String emrLocal = TIOM_FileServer.getPath(
                        "EmrLocalTempFileName");
                    this.word.onOpen(emrLocal, "��ʼ��", 2, false);
                    //ȡ���༭
                    this.word.setCanEdit(false);
                }
                this.loadTree();
                this.setOnlyType("");
                onlyEditfileName = "";
            }
            else {
                this.messageBox("ɾ��ʧ�ܣ�");
                return;
            }
            //��ʼ���˵�
            initMenu();
        }
        else {
            return;
        }
    }

    /**
     * �Ƚ�����ֵ�Ƿ����޸�
     * @param oldParm TParm
     * @param newParm TParm
     * @return boolean
     */
    public boolean isOldNewEmrEdit(TParm oldParm, TParm newParm) {
        boolean falg = false;
        String coumn[] = oldParm.getNames();
        for (String temp : coumn) {
            if (!oldParm.getValue(temp).equals(newParm.getValue(temp))) {
                falg = true;
                break;
            }
        }
        return falg;
    }

    /**
     * ģ������
     */
    public void onPropertyTemplet() {
        TTreeNode node = this.getTTree(TREE).getSelectNode();
        Object obj = this.openDialog("%ROOT%\\config\\emr\\EMRTempInfoUI.x",
                                     node.getData());
//        this.messageBox_(obj);
        if (obj == null) {
            return;
        }
        TParm temp = (TParm) obj;
//        this.messageBox_(temp);
        String subclassCode = node.getID();
//        String subclassCode = temp.getValue("SUBCLASS_CODE");
        TParm oldParm = (TParm) node.getData();
        String seq = oldParm.getValue("SEQ");
        if (oldParm.getValue("EMT_FILENAME").equals(temp.getValue(
            "EMT_FILENAME"))) {
            if (this.word.onOpen(oldParm.getValue("TEMPLET_PATH"),
                                 oldParm.getValue("EMT_FILENAME"), 2, false)) {
            }
            else {
                this.messageBox("�ļ�����ʧ�ܣ�");
                return;
            }
            this.word.setCanEdit(false);
        }
        else {
            if (this.word.onOpen(oldParm.getValue("TEMPLET_PATH"),
                                 oldParm.getValue("EMT_FILENAME"), 2, false)) {
                this.word.setMessageBoxSwitch(false);
                if (!delFileTempletFile(oldParm.getValue("TEMPLET_PATH"),
                                        oldParm.getValue("EMT_FILENAME"))) {
                    this.messageBox("�ļ�����ʧ�ܣ�");
                    this.word.setMessageBoxSwitch(false);
                    return;
                }
            }
            else {
                this.messageBox("�ļ�����ʧ�ܣ�");
                this.word.setMessageBoxSwitch(false);
                return;
            }
            this.word.setMessageBoxSwitch(true);
            this.word.setCanEdit(false);
        }
        TDataStore data = new TDataStore();
        data.setSQL("SELECT * FROM EMR_TEMPLET");
        data.retrieve();
        int rowNumber = data.rowCount();
        TParm onlyParm = new TParm();
        for (int i = 0; i < rowNumber; i++) {
            if (data.getRowParm(i).getValue("SUBCLASS_CODE").equals(
                subclassCode) && data.getRowParm(i).getValue("SEQ").equals(seq)) {
                onlyParm = data.getRowParm(i);
                break;
            }
        }
//        this.messageBox_(onlyParm);
        for (int i = 0; i < rowNumber; i++) {
            if (data.getRowParm(i).getValue("SUBCLASS_CODE").equals(
                subclassCode) && data.getRowParm(i).getValue("SEQ").equals(seq)) {
                data.setItem(i, "OPT_USER", Operator.getID());
                data.setItem(i, "OPT_DATE", StringTool.getTimestamp(new Date()));
                data.setItem(i, "OPT_TERM", Operator.getIP());
                data.setItem(i, "DEPT_CODE", temp.getData("DEPT_CODE"));
                data.setItem(i, "EMT_FILENAME", temp.getData("EMT_FILENAME"));
                data.setItem(i, "SUBCLASS_DESC", temp.getData("SUBCLASS_DESC"));
                data.setItem(i, "RUN_PROGARM", temp.getData("RUN_PROGARM"));
                data.setItem(i, "SUBTEMPLET_CODE",
                             temp.getData("SUBTEMPLET_CODE"));
                data.setItem(i, "CLASS_STYLE", temp.getData("CLASS_STYLE"));
                data.setItem(i, "PY1", temp.getData("PY1"));
                data.setItem(i, "IPD_FLG", temp.getData("IPD_FLG"));
                data.setItem(i, "HRM_FLG", temp.getData("HRM_FLG"));
                data.setItem(i, "OPD_FLG", temp.getData("OPD_FLG"));
                data.setItem(i, "EMG_FLG", temp.getData("EMG_FLG"));
                data.setItem(i, "OIDR_FLG", temp.getData("OIDR_FLG"));
                data.setItem(i, "NSS_FLG", temp.getData("NSS_FLG"));
                data.setItem(i, "SUBCLASS_CODE", temp.getData("SUBCLASS_CODE"));
                data.setItem(i, "SYSTEM_CODE", temp.getData("SYSTEM_CODE"));
                data.setItem(i, "DOCUMENT_TYPE", temp.getData("DOCUMENT_TYPE"));
                data.setItem(i, "MBABY_FLG", temp.getData("MBABY_FLG"));
                data.setItem(i, "PUBLIC_FLG", temp.getData("PUBLIC_FLG"));
                data.setItem(i, "STOP_FLG", temp.getData("STOP_FLG"));
                
                //data.setItem(i, "REF_FLG", temp.getData("REF_FLG"));
//                this.messageBox_(temp.getData("SYSTEM_CODE"));
            }
        }
//        String[] str = data.getUpdateSQL();
//        for(String temps:str){
//            System.out.println(""+temps);
//        }
        if (data.update()) {
            if (this.word.onSaveAs(oldParm.getValue("TEMPLET_PATH"),
                                   temp.getValue("EMT_FILENAME"), 2)) {
                this.word.setCanEdit(false);
                onlyEditfileName = temp.getValue("EMT_FILENAME");
                this.saveParm = onlyParm;
                this.setOnlyType("OPEN");
            }
            else {
                return;
            }
        }
        else {
//            System.out.println("DATA"+data.getErrText());
            this.messageBox("����ʧ�ܣ�");
            return;
        }
//        System.out.println(""+data.getErrText());
        this.loadTree();
    }

    /**
     * �õ�ģ����
     * @param classCode String
     * @return String
     */
    public TParm creatSubClassCode(String classCode) {
        TParm result = new TParm();
        TParm parm = new TParm(this.getDBTool().select(
            "SELECT NVL(MAX(SEQ)+1,1) AS SEQ FROM EMR_TEMPLET WHERE CLASS_CODE='" +
            classCode + "'"));
        String temp = parm.getValue("SEQ", 0);
        result.setData("SEQ", parm.getData("SEQ", 0));
        if (temp.length() == 1) {
            temp = "0" + temp;
        }
        temp = classCode + temp;
        result.setData("SUBCLASS_CODE", temp);
        return result;
    }

    /**
     * �ҵ�����
     * @return TParm
     */
    /**public TParm getMainRootTree() {
        TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE FROM EMR_CLASS ORDER BY CLASS_CODE,SEQ"));
        return result;
         }**/

	/**
	 * �����ӽڵ�
	 *
	 * @param systemCode
	 *            String
	 * @return TParm
	 */
	public TParm getChildTreeType(String classCode) {
		// ,REF_FLG
		TParm result = new TParm(
				this
						.getDBTool()
						.select(
								"SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE,IPD_FLG,HRM_FLG,OPD_FLG,EMG_FLG,OIDR_FLG,NSS_FLG,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE,PY1,SEQ,SYSTEM_CODE,DOCUMENT_TYPE,MBABY_FLG,PUBLIC_FLG,STOP_FLG FROM EMR_TEMPLET WHERE CLASS_CODE='"
										+ classCode
										+ "' ORDER BY SUBCLASS_CODE,CLASS_CODE,SEQ"));
		return result;
	}

    /**
     * �õ���
     * @param tag String
     * @return TTree
     */
    public TTree getTTree(String tag) {
        return (TTree)this.getComponent(tag);
    }

    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * ����ѡ��ر��¼�
     */
    public void onCloseChickedCY() {
        TMovePane mp = (TMovePane)this.getComponent("EMRMOVEPANE");
        mp.onDoubleClicked(true);
    }

    /**
     * �ر��¼�
     * @return boolean
     */
    public boolean onClosing() {
        if (onlyEditfileName.length() == 0) {
            return true;
        }
        else {
            switch (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�?", this.YES_NO_CANCEL_OPTION)) {
                case 0:
                    if (word.isFileEditLock()) {
                        //ģ���ļ�����
                        word.setFileEditLock(false);
                        //��������IP
                        word.setFileLockIP("");
                        //������������
                        word.setFileLockDate("");
                        //���������û�
                        word.setFileLockUser("");
                    }
                    if (!onSave()) {
                        return false;
                    }
                    break;
                case 1:
                    break;
                case 2:
                    return false;
            }
        }
        return true;

    }

    public String getOnlyType() {
        return onlyType;
    }

    public void setOnlyType(String onlyType) {
        this.onlyType = onlyType;
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
     * �̶��ı�����
     */
    public void onFixTextProperty() {
        if (onlyEditfileName.length() != 0) {
            word.onOpenFixedProperty();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
        //̩������
        //setCapName();
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

    public void onDeleteCap() {
        if (onlyEditfileName.length() == 0) {
            messageBox("��ѡ��ģ�棡");
            return;
        }
        EComponent com = word.getFocusManager().getFocus();
        if (! (com instanceof ECapture)) {
            messageBox("ѡȡ�����ץȡ��");
            return;
        }
        if ( ( (ECapture) com).getCaptureType() == 1) {
            messageBox("��ѡ��ʼץȡ��");
            return;
        }
        deleteCap(word.findCapture( ( (ECapture) com).getName()));
        deleteCapCom(com);
    }


    public void deleteCap(ECapture capture) {
        IBlock block = capture.getNextTrueBlock();
        while (block != null) {
            if (isEndCapture(block, capture)) {
                deleteCapCom( (EComponent) block);
                return;
            }
            if (block instanceof ETable) {
                deleteCapCom( (EComponent) block);
                block = capture.getNextTrueBlock();
                continue;
            }
            IBlock nextBlock = block.getNextTrueBlock();
            if (nextBlock != null) {
                deleteCapCom( (EComponent) block);
                block = nextBlock;
                continue;
            }
            EPanel panel = block.getPanel().getNextTruePanel();
            deleteCapCom( (EComponent) block);
            if (panel == null || panel.size() == 0) {
                return;
            }
            block = panel.get(0);
        }
    }

    private boolean isEndCapture(IBlock block, ECapture captureStart) {
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
        if (capture.getName() == null || capture.getName().length() == 0) {
            return false;
        }
        if (capture.getName().equals(captureStart.getName())) {
            return true;
        }
        return false;
    }

    private void deleteCapCom(EComponent com) {
        if (com == null) {
            return;
        }
        if (com instanceof EFixed) {
            ( (EFixed) com).deleteFixed();
        }
        else if (com instanceof EMacroroutine) {
            ( (EMacroroutine) com).delete();
        }
        else if (com instanceof ETable) {
            ( (ETable) com).removeThisAll();
        }
        else if (com instanceof EImage) {
            ( (EImage) com).removeThis();
        }
        else {
            return;
        }
        word.update();
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
     * ��ӡ����
     */
    public void onPrintSetup() {
        if (onlyEditfileName.length() != 0) {
            word.printSetup();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ҳ������
     */
    public void onPrintPageSet() {
        if (onlyEditfileName.length() != 0) {
            this.openDialog("%ROOT%\\config\\database\\PageSetupDialog.x",
                            word.getPageManager());
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ��ӡ
     */
    public void onPrint() {
        if (onlyEditfileName.length() != 0) {
            word.print();
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
     * ����Ԥ��
     */
    public void onPrintClear() {
        if (onlyEditfileName.length() != 0) {
            word.onPreviewWord();
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
     * ����ͼƬ�༭��
     */
    public void onInsertImageEdit() {
        if (onlyEditfileName.length() != 0) {
            word.insertImage();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ɾ��ͼƬ�༭��
     */
    public void onDeleteImageEdit() {
        if (onlyEditfileName.length() != 0) {
            word.deleteImage();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * �����
     */
    public void onInsertGBlock() {
        if (onlyEditfileName.length() != 0) {
            word.insertGBlock();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ������
     */
    public void onInsertGLine() {
        if (onlyEditfileName.length() != 0) {
            word.insertGLine();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ������ߴ�
     * @param index int
     */
    public void onSizeBlockMenu(String index) {
        if (onlyEditfileName.length() != 0) {
            word.onSizeBlockMenu(StringTool.getInt(index));
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
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
     * ��ӡ
     */
    public void onPrintXDDialog() {
        if (onlyEditfileName.length() != 0) {
            word.onPreviewWord();
            word.printXDDialog();
        }
        else {
            //��ѡ����
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ��ʾ�кſ���
     */
    public void onShowRowIDSwitch() {
        if (onlyEditfileName.length() != 0) {
            word.setShowRowID(!word.isShowRowID());
            word.update();
        }
        else {
            //��ѡ����
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
     * add by lx
     * @return TParm
     */
    public TParm getTreeRoot() {
        TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE FROM EMR_CLASS WHERE PARENT_CLASS_CODE IS NULL"));
        return result;

    }


    /**add by lx
     * �ҵ�����
     * @return TParm
     */
    public TParm getMainRootTree() {
        //m by lx
        TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE FROM EMR_CLASS ORDER BY CLASS_CODE,SEQ"));
        return result;
    }

    /**
     * ���벡��Ԫ��;
     */
    public void onInserElement() {
        //�����ĵ����Ͳ�����
        TTreeNode node = this.getTTree(TREE).getSelectNode();
        if (node == null) {
            this.messageBox("����ѡ��ģ�棡");
            return;
        }
        String documentType = ( (TParm) node.getData()).getValue(
            "DOCUMENT_TYPE");
        //this.messageBox("documentType==="+documentType);
        TParm inParm = new TParm();
        inParm.setData("DOCUMENT_TYPE", documentType);

        //�򿪲��벡��Ԫ�ش��壻
        TParm reParm = (TParm)this.openDialog(
            "%ROOT%\\config\\database\\InsertElementDialog.x", inParm);

        //System.out.println("reParm-============"+reParm);
        //�Ƿ����
        //this.setValue("REPEAT_COUNT", reParm.getData("REPEAT_COUNT", 0));
        //��ʲô�ؼ�;
        // this.setValue("COMPONENT_TYPE", reParm.getData("COMPONENT_TYPE", 0));
        //���ݱ�� ����
        //this.setValue("DATA_CODE", reParm.getData("DATA_CODE", 0));
        //����ֵ
        //this.setValue("DATA_DESC", reParm.getData("DATA_DESC", 0));

        //���ز����Ƿ�  �����|����
        insertElement(reParm);

    }

    /**
     * ��������Ԫ��
     */
    public void onInserHideElement() {
        //
        TParm inParam = new TParm();
        inParam.addData("theWord", word);

        //������Ԫ�ش���
        TParm reParm = (TParm)this.openDialog(
            "%ROOT%\\config\\database\\InsertHideElementDialog.x", inParam);
        //����word
        //word=reParm.getData("REPEAT_COUNT", 0)

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
     * ����ģ��Ƭ��
     */
    public void onInsertTemplatePY() {
        TParm inParm = new TParm();
        inParm.setData("TYPE", "2");
        //this.messageBox("this.getDeptCode()"+this.getDeptCode());
        inParm.setData("DEPT_CODE", "");
        inParm.setData("TWORD", this.word);
        inParm.addListener("onReturnTemplateContent", this,
                           "onReturnTemplateContent");
        TWindow window = (TWindow)this.openWindow(
            "%ROOT%\\config\\emr\\EMRTemplateComPhraseQuote.x", inParm, true);
        window.setX(ImageTool.getScreenWidth() - window.getWidth());
        window.setY(0);
        window.setVisible(true);

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
     * ���ϵͳ������
     */
    public void onClearMenu() {
        CopyOperator.clearComList();
    }

    /**
     * �ϲ���Ԫ��
     */
    public void onMergerCell(){
		if (word.getFileOpenName() != null) {
			word.mergerCell();
		} else {
			this.messageBox("��ѡ��ģ�棡");
		}
    }


    /**
     * ���ø�ʽˢ
     */
    public void onFormatSet(){
		if (this.word.getFileOpenName() != null) {
			this.word.onFormatSet();
		} else {
			this.messageBox("��ѡ��ģ�棡");
		}
    }

    /**
     * ʹ�ø�ʽˢ
     */
    public void onFormatUse(){
		if (this.word.getFileOpenName() != null) {
			this.word.onFormatUse();
		} else {
			this.messageBox("��ѡ��ģ�棡");
		}
    }

    /**
     * ��ӱ��ʽ
     */
    public void onInsertExpression(){

        if (onlyEditfileName.length() != 0) {

        	EFixed ef = word.insertFixed("�������ʽ","���ʽ");
        	ef.setExpression(true);
        	word.getExpressionList().add(ef);

		} else {
			this.messageBox("��ѡ��ģ�棡");
		}
    }

    /**
     * ���ñ��ʽ
     */
    public void onSettingExpression(){

    	if (onlyEditfileName.length() != 0) {

	       	 EComponent ec = word.getFocusManager().getFocus();

	       	 if( this.onSettingExpression(ec) ) return;

	    	 //
	    	 this.messageBox("��ѡ����ʽ�����");
    	}else {
			this.messageBox("��ѡ��ģ�棡");
		}
    }

    /**
     *
     * @param ec
     * @return
     */
    private boolean onSettingExpression(EComponent ec) {

		if (ec instanceof EFixed) {
			EFixed ef = (EFixed) ec;
			if (ef.isExpression()) {
				openDialog( "%ROOT%\\config\\emr\\EMRExpressionDialog.x", new Object[]{this.word,ef} );
				return true;
			}
		}

		//
		return false;
	}

    /**
	 * ������ʽ
	 */
    public void onCalculateExpression(){

        if (onlyEditfileName.length() != 0) {
             word.onCalculateExpression();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     *�����ץȡ
     */
    public void onInsertExpressionCapture(){

        if (onlyEditfileName.length() != 0) {
        	openDialog( "%ROOT%\\config\\emr\\EMRECaptureDialog.x", this.word );
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

}
