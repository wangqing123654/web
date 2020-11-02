package com.javahis.ui.emr;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TWord;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TTree;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.tui.text.MEvent;
import com.dongyang.tui.text.EText;
import com.dongyang.tui.text.ETR;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EMacroroutine;
import com.dongyang.tui.text.ECapture;

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
public class EMRTemplateComPhraseQuoteControl
    extends TControl {
    private String deptCode;
    private TParm evenParm;
    //COMBOBOX ����
    private TComboBox combo;
    /**
     * ����
     */
    private TTreeNode treeRoot;

    /**
     * WORD����
     */
    private TWord parmWord;

    private TWord word;
    private TTree tree;

    /**
     * WORD����
     */
    private static final String TWORD_NAME = "WORD";
    /**
     * ��������
     */
    private static final String TREE_NAME = "TREE";

    private String phraseFilePath = "";
    private String phraseFileName = "";


    public EMRTemplateComPhraseQuoteControl() {
    }

    public void onInit() {
        super.onInit();
        Object obj = this.getParameter();
        combo = (TComboBox)this.getComponent("DEPT_CODE");
        word = (TWord)this.getComponent("WORD");
        tree = (TTree)this.getComponent(TREE_NAME);
        if (obj != null) {
            evenParm = (TParm) obj;
            //this.messageBox("evenParm++++"+evenParm);
            //this.messageBox("++DEPT_CODE++"+evenParm.getValue("DEPT_CODE"));
            parmWord = (TWord) evenParm.getData("TWORD");
            this.setDeptCode(evenParm.getValue("DEPT_CODE"));
            //this.messageBox("==DEPT_CODE=="+this.getDeptCode());
            combo.setSelectedID(evenParm.getValue("DEPT_CODE"));
            combo.setVisible(true);
            combo.setEnabled(false);

            loadTree();

            initEven();
        }

    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptCode() {
        return this.deptCode;
    }

    public void onClear() {
        onInit();
    }

    /**
     * ������
     */
    public void loadTree() {
        treeRoot = tree.getRoot();
        //�õ�����
        treeRoot.setText("Ƭ�����");
        treeRoot.setType("Root");
        treeRoot.removeAllChildren();

        //�õ��ڵ�����
        TParm rootParam = this.getRootNodeData();
        if (rootParam.getInt("ACTION", "COUNT") == 0) {
            //û������Ƭ����࣡�������ݿ�����
            this.messageBox("��������Ƭ����࣡");
            return;
        }

        treeRoot.setText(rootParam.getValue("PHRASE_CODE", 0));
        treeRoot.setType("Root");
        treeRoot.setID(rootParam.getValue("CLASS_CODE", 0));

        //�õ����ݼ���
        TParm nodesParam = this.getNodes();

        if (nodesParam == null) {
            return;
        }
        int nodesCount = nodesParam.getCount();
        for (int i = 0; i < nodesCount; i++) {
            TParm temp = nodesParam.getRow(i);
            //���ݽڵ����������Ƿ�ΪĿ¼�ڵ�
            String noteType = "1";
            //Ҷ�ڵ�(�ǽṹ��Ƭ��)
            if (nodesParam.getValue("LEAF_FLG", i).equals
                ("Y")) {
                noteType = "4";
            }
            //�����½ڵ�
            TTreeNode PhraseClass = new TTreeNode("PHRASECLASS"
                                                  + i, noteType);
            //��ERM������Ϣ���õ��ڵ㵱��
            PhraseClass.setText(nodesParam.getValue
                                ("PHRASE_CODE", i));
            PhraseClass.setID(nodesParam.getValue
                              ("CLASS_CODE", i));
            //������������
            PhraseClass.setData(temp);

            //��һ���Ľڵ��������
            if (nodesParam.getValue("PARENT_CLASS_CODE",
                                    i).equals("ROOT")) {
                treeRoot.addSeq(PhraseClass);
            }
            //��������Ľڵ������Ӧ�ĸ��ڵ�����
            else {
                if (nodesParam.getValue
                    ("PARENT_CLASS_CODE", i).length() != 0) {
                    //����Ҷ�ڵ㣨Ƭ��ڵ㣩,�ǵ�ǰ���ҵļ��룻

                    //��Ҷ�ڵ�
                    if(nodesParam.getValue("LEAF_FLG", i).equals("Y")){
                        //����Ƭ�����ڵ㣬�����Ƭ���ڴ˲�����
                         if(nodesParam.getValue("MAIN_FLG", i).equals("Y")){
                             treeRoot.findNodeForID(nodesParam.getValue(
                            "PARENT_CLASS_CODE", i)).add(PhraseClass);
                         }
                        //
                    }else{
                        treeRoot.findNodeForID(nodesParam.getValue(
                            "PARENT_CLASS_CODE", i)).add(PhraseClass);
                    }
                }
            }

        }
        tree.update();

        /**
                 int rowCount = parm.getInt("ACTION", "COUNT");
                 for (int i = 0; i < rowCount; i++) {
            TParm mainParm = parm.getRow(i);
            //�ӽڵ�   EMR_CLASS���е�STYLE�ֶ�
            TTreeNode node = new TTreeNode();
            node.setID(mainParm.getValue("ID"));
            node.setText(mainParm.getValue("CHN_DESC"));
            //���ò˵���ʾ��ʽ
            node.setType("2");
            node.setGroup(mainParm.getValue("ID"));
            node.setData(mainParm);
            //��������
            treeRoot.add(node);
            //node.setInitExpanded(true);
            //�ӽڵ�
            TParm childParms = this.getChildtNodeData(node.getID());
            //System.out.println("=======childParms========="+childParms);
            for (int j = 0; j < childParms.getCount(); j++) {
                TParm theChildParm = childParms.getRow(j);
                TTreeNode childNode = new TTreeNode();
                childNode.setID(theChildParm.getValue("PHRASE_FILE_NAME"));
                childNode.setText(theChildParm.getValue("PHRASE_CODE"));
                //���ò˵���ʾ��ʽ
                childNode.setType("4");
                childNode.setGroup(theChildParm.getValue("PHRASE_FILE_NAME"));
                childNode.setData(theChildParm);
                node.add(childNode);
            }

                 }**/

    }

    /**
     * ע���¼�
     */
    public void initEven() {
        //Ҷ�ڵ�˫���¼����򿪶�Ӧ�Ľṹ��Ƭ�
        //����ѡ������Ŀ
        addEventListener(TREE_NAME + "->" + TTreeEvent.DOUBLE_CLICKED,
                         "onTreeDoubled");

    }

    /**
     * ����
     */
    public void onFetchBack() {
        //this.messageBox("come in");
        EComponent com=parmWord.getFocusManager().getFocus();
        ECapture firstCapture=null;
        if(com!=null){
           if(com instanceof ECapture)
           {
               //System.out.println(((ECapture)com).getName());
               //System.out.println(((ECapture)com).getCaptureType());
               firstCapture=(ECapture)com;
           }
        }

        parmWord.getFocusManager().onInsertFile(phraseFilePath, phraseFileName,
                                             2, false);
        //ɾ���س�
        if(firstCapture!=null){
            firstCapture.setFocusLast();
            boolean b=firstCapture.deleteChar();
            //System.out.println("ɾ������++++++"+b);
        }
        //System.out.println("EMRTemplateComPhrase test===="+parmWord.getFocus());
        parmWord.update();

    }

    /**
     * �õ��¼�������
     * @return MEvent
     */
    /** public MEvent getEventManager() {
         return word.getEventManager();
     }

     public void doRightMove() {
         this.messageBox("isSelected"+word.getFocusManager().isSelected());
         //����ѡ�п�ʼ
         if (!word.getFocusManager().isSelected()) {
             word.getFocusManager().setStartSelected();
         }
         onFocusToRight();
         //����ѡ�н���
         word.getFocusManager().setEndSelected();
         //setHasFocusPointX(false);

     }


     public void onFocusToRight() {
         ETR tr = word.getFocusManager().getFocusTRE();
         if (tr != null) {
             tr.onFocusToRight();
             return;
         }
         EText text = word.getFocusManager().getFocusText();
         if (text == null) {
             return;
         }
         text.onFocusToRight();
     }**/


    public void onTreeDoubled(Object parm) {

        TTreeNode node = (TTreeNode) parm;
        TParm dataParm = (TParm) node.getData();
        //this.messageBox("==dataParm==="+dataParm);
        //��ģ���ļ�
        String isPhrase=dataParm.getValue("LEAF_FLG");
        //this.messageBox("==isPhrase==="+isPhrase);

        if(isPhrase.equals("Y")){
            phraseFilePath = dataParm.getValue("FILE_PATH");
            phraseFileName = dataParm.getValue("FILE_NAME");
            if (!this.word.onOpen(dataParm.getValue("FILE_PATH"),
                                  dataParm.getValue("FILE_NAME"), 2, false)) {
                return;
            }
            this.word.onEditWord();
            //���ò��ɱ༭
            this.word.setCanEdit(true);
            //word.getFocusManager().onPreviewWord();
        }

    }

    public TParm getChildtNodeData(String parentID) {
        String sql =
            "SELECT * FROM OPD_COMTEMPLATEPHRASE WHERE PHRASE_TYPE='" +
            parentID + "'";
        sql += " AND DEPTORDR_CODE='" + this.getDeptCode() + "'";
        sql += " ORDER BY OPT_DATE";
        //this.messageBox("sql=="+sql);

        //this.messageBox("dept code"+this.getDeptCode());
        TParm result = new TParm(this.getDBTool().select(sql));
        return result;

    }

    /**
     * �õ����ڵ�����
     * @return TParm
     */
    public TParm getRootNodeData() {
        /**String sql =
         "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='PHRASE_TYPE'";
                 sql += " ORDER BY SEQ";
                 TParm result = new TParm(this.getDBTool().select(sql));
                 return result;**/
        TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,PHRASE_CODE,LEAF_FLG,MAIN_FLG,PARENT_CLASS_CODE,FILE_PATH,FILE_NAME FROM OPD_COMTEMPLATE_PHRASE WHERE PARENT_CLASS_CODE IS NULL"));
        return result;
    }

    private TParm getNodes() {
        TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,PHRASE_CODE,LEAF_FLG,MAIN_FLG,PARENT_CLASS_CODE,FILE_PATH,FILE_NAME FROM OPD_COMTEMPLATE_PHRASE ORDER BY CLASS_CODE,SEQ"));
        return result;
    }


    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }


}
