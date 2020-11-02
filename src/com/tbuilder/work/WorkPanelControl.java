package com.tbuilder.work;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.config.TConfigParse;
import com.dongyang.ui.TTreeNode;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.base.JTreeBase;
import javax.swing.tree.TreePath;
import com.dongyang.ui.edit.TUIEditView;
import java.util.ArrayList;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import java.util.Vector;
import com.dongyang.util.MemoryMonitor;
import com.dongyang.util.TSystem;

public class WorkPanelControl extends TControl{
    public static final String INPUT_PARM_DIALOG_CONFIG="InputParmDialogConfig";
    /**
     * �ṹ��Tag
     */
    public static final String STRUCTURE_TREE = "StructureTree";
    /**
     * ѡ����Tag
     */
    public static final String SELECT_TREE = "TComponentSelection";
    /**
     * ����Table
     */
    public static final String UI_PROPERTY = "UIProperty";
    /**
     * �ļ���
     */
    private String filename;
    /**
     * ����
     */
    private String name;
    /**
     * ����
     */
    private String data;
    /**
     * ���ݽ�������
     */
    private TConfigParse configParse;
    /**
     * �ṹ������
     */
    private TTreeNode structureNodeRoot;
    /**
     * ѡ��������
     */
    private TTreeNode selectNodeRoot;
    /**
     * ��������
     */
    private String previewParm;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        super.onInit();
        TParm parm = (TParm)getParameter();
        //�õ�����
        name = parm.getValue("NAME");
        //�õ��ļ���
        filename = parm.getValue("FILE_NAME");
        //��������
        callMessage("UI|setTitle|" + name);
        //��ȡ�ļ�����
        data = new String(TIOM_AppServer.readFile(filename));
        //�����ļ�
        setConfigParse(new TConfigParse(data));
        //����ѡ�м���
        getView().addEventListener("Selected",this,"onSelected");
        //����ȡ��ѡ�м���
        getView().addEventListener("undoSelected",this,"onUndoSelected");
        //������������ļ���
        getView().addEventListener("createTComponent",this,"onCreateTComponent");
        //����ɾ������ļ���
        getView().addEventListener("deleteTComponent",this,"onDeleteTComponent");
        //����޸�Tag
        getView().addEventListener("modifiedTag",this,"onModifiedTag");
        //��ʼ���ṹ��
        onInitStructureTree();
        //��ʼ��ѡ����
        onInitSelectTree();
        //����ѡ������Ŀ
        addEventListener(SELECT_TREE + "->" + TTreeEvent.CLICKED,"onSelectTreeClicked");
        //����Table�ı�ֵ
        addEventListener(UI_PROPERTY + "->" + TTableEvent.CHANGE_VALUE,"onPropertyTableChangeValue");
        //����Table˫��
        callFunction("UI|" + UI_PROPERTY + "|addEventListener",UI_PROPERTY + "->" + TTableEvent.DOUBLE_CLICKED,this,"onPropertyTableDoubleClicked");
        SwingUtilities.invokeLater(new Runnable(){
            public void run()
            {
                callFunction("UI|UIDesign|updateUI",getConfigParse().getRoot());
            }
        });
    }
    /**
     * �õ��༭��ͼ
     * @return TUIEditView
     */
    public TUIEditView getView()
    {
        if(getConfigParse() == null)
            return null;
        return getConfigParse().getView();
    }
    /**
     * ���ý�������
     * @param configParse TConfigParse
     */
    public void setConfigParse(TConfigParse configParse)
    {
        this.configParse = configParse;
    }
    /**
     * �õ���������
     * @return TConfigParse
     */
    public TConfigParse getConfigParse()
    {
        return configParse;
    }
    /**
     * ��ʼ��ѡ����
     */
    public void onInitSelectTree()
    {
        out("begin");
        selectNodeRoot = (TTreeNode)callMessage("UI|" + SELECT_TREE + "|getRoot");
        if(selectNodeRoot == null)
            return;
        selectNodeRoot.setText("TComponent List");
        selectNodeRoot.setType("Path");
        selectNodeRoot.removeAllChildren();
        TTreeNode noneNode = new TTreeNode("<none>","TComponent");
        selectNodeRoot.add(noneNode);
        TTreeNode baseNode = new TTreeNode("TComponent","Path");
        selectNodeRoot.add(baseNode);
        String items[] = StringTool.parseLine(getConfigString("TComponentList"), ';', "[]{}()");
        for(int i = 0;i < items.length;i++)
            baseNode.add(new TTreeNode(items[i], "TComponent"));
        TTreeNode comboNode = new TTreeNode("TCombo","Path");
        TTreeNode textFormatNode = new TTreeNode("TTextFormat","Path");
        selectNodeRoot.add(comboNode);
        selectNodeRoot.add(textFormatNode);
        loadComboNode(comboNode);
        loadTextFormatNode(textFormatNode);
        callMessage("UI|" + SELECT_TREE + "|update");
        out("end");
    }
    public void loadComboNode(TTreeNode parent)
    {
        String item = getConfigString("TComboList");
        loadComboNode(parent,"TComboList",item);
    }
    public void loadComboNode(TTreeNode parent,String parentItem,String item)
    {
        String items[] = StringTool.parseLine(item, ';', "[]{}()");
        for(int i = 0;i < items.length;i++)
        {
            TTreeNode node = new TTreeNode(items[i], "TComponent");
            String ls = getConfigString(parentItem + "." + items[i]);
            if(ls.length() > 0)
                loadComboNode(node,parentItem + "." + items[i],ls);
            parent.add(node);
        }
    }
    public void loadTextFormatNode(TTreeNode parent)
    {
        String item = getConfigString("TTextFormatList");
        loadComboNode(parent,"TTextFormatList",item);
    }
    /**
     * ��ʼ���ṹ��
     */
    public void onInitStructureTree()
    {
        out("begin");
        structureNodeRoot = (TTreeNode)callMessage("UI|" + STRUCTURE_TREE + "|getRoot");
        if(structureNodeRoot == null)
            return;
        if(getConfigParse() == null)
            return;
        structureNodeRoot.setText(name);
        structureNodeRoot.setType("<Root>");
        structureNodeRoot.removeAllChildren();
        downloadStructureTree(structureNodeRoot,getConfigParse().getRoot());
        callMessage("UI|" + STRUCTURE_TREE + "|update");
        out("end");
    }
    /**
     * װ�ؽṹ��
     * @param root TTreeNode
     * @param object TObject
     */
    private void downloadStructureTree(TTreeNode root,TObject object)
    {
        if(root == null)
            return;
        if(object == null)
            return;
        String text = object.getType() + " [" + object.getTag() + "]";
        if(object.getPosition() != null)
            text += " " + object.getPosition();
        //�����ڵ�
        TTreeNode node = new TTreeNode(text,object.getType());
        //�������ݶ���
        node.setData(object);
        //�����ڵ���󱣴浽���ݶ���
        object.setData(node);
        int count = object.getItemCount();
        for(int i = 0;i < count;i++)
            downloadStructureTree(node,object.getItem(i));
        root.add(node);
    }
    /**
     * ������
     */
    public void onAlignUP()
    {
        getView().onAlignUP();
    }
    /**
     * �׶���
     */
    public void onAlignDown()
    {
        getView().onAlignDown();
    }
    /**
     * �����
     */
    public void onAlignLeft()
    {
        getView().onAlignLeft();
    }
    /**
     * �Ҷ���
     */
    public void onAlignRight()
    {
        getView().onAlignRight();
    }
    /**
     * ˮƽ����
     */
    public void onAlignWCenter()
    {
        getView().onAlignWCenter();
    }
    /**
     * ��ֱ����
     */
    public void onAlignHCenter()
    {
        getView().onAlignHCenter();
    }
    /**
     * ͬ��
     */
    public void onAlignWidth()
    {
        getView().onAlignWidth();
    }
    /**
     * ͬ��
     */
    public void onAlignHeight()
    {
        getView().onAlignHeight();
    }
    /**
     * ����ͬ���
     */
    public void onAlignWSpace()
    {
        getView().onAlignWSpace();
    }
    /**
     * ����ͬ���
     */
    public void onAlignHSpace()
    {
        getView().onAlignHSpace();
    }
    /**
     * �����ṹ����Ŀ
     */
    public void onStructureTreeClicked()
    {
        JTreeBase tree = (JTreeBase)callMessage("UI|" + STRUCTURE_TREE + "|getTree");
        if(tree == null)
            return;
        TreePath treePath[] = tree.getSelectionPaths();
        if(treePath == null)
            return;
        ArrayList list = new ArrayList();
        for(int i = 0;i < treePath.length;i ++)
        {
            TTreeNode node = (TTreeNode) treePath[i].getLastPathComponent();
            if (node == null)
                continue;
            if ("<Root>".equals(node.getType()))
                continue;
            TObject object = (TObject) node.getData();
            if (object == null)
                return;
            list.add(object);
        }
        TObject selectObject[] = (TObject[])list.toArray(new TObject[]{});
        getView().setSelected(selectObject);
    }
    /**
     * ѡ�����
     * @param tobject TObject
     */
    public void onSelected(TObject tobject)
    {
        //ͬ���ṹ��ѡ��
        synchronizationStructureNodeSelected();
        //ͬ��������
        synchronizationProperty(tobject);
    }
    /**
     * ȡ��ѡ�����
     * @param tobject TObject
     */
    public void onUndoSelected(TObject tobject)
    {
        //ͬ���ṹ��ѡ��
        synchronizationStructureNodeSelected();
    }
    /**
     * ͬ��������
     * @param tobject TObject
     */
    public void synchronizationProperty(TObject tobject)
    {
        if(tobject == null)
            return;
        TAttributeList attributeList = tobject.getAttributeList();
        if(attributeList == null)
            return;
        callFunction("UI|" + UI_PROPERTY + "|acceptText");
        String value = attributeList.getValue();
        String editType = attributeList.getEditType();
        String alignment = attributeList.getEditAlignment();
        callFunction("UI|" + UI_PROPERTY + "|value=" + value);
        callFunction("UI|" + UI_PROPERTY + "|RowColumnTypeData=" + editType);
        callFunction("UI|" + UI_PROPERTY + "|HorizontalAlignmentData=" + alignment);
        callFunction("UI|" + UI_PROPERTY + "|setData",tobject);
        //����ת�Ƶ����̼����ؼ���
        callFunction("UI|UIDesign|onKeyGrabFocus");
    }
    /**
     * ͬ���ṹ��ѡ��
     */
    public void synchronizationStructureNodeSelected()
    {
        if(structureNodeRoot == null)
            return;
        int count = getView().getSelectedCount();
        TTreeNode node[] = new TTreeNode[count];
        for(int i = 0;i < count;i++)
            node[i] = (TTreeNode)getView().getSelected(i).getData();
        JTreeBase tree = (JTreeBase)callMessage("UI|" + STRUCTURE_TREE + "|getTree");
        if(tree == null)
            return;
        tree.setSelectionNode(node);
    }
    /**
     * ����ѡ����
     * @param parm Object
     */
    public void onSelectTreeClicked(Object parm)
    {
        TTreeNode node = (TTreeNode)parm;
        if(node == null)
            return;
        if("Path".equals(node.getType()))
            return;
        String name = node.getText();
        if("<none>".equals(name))
            name = null;
        getView().setCreateTComponentName(name);
    }
    /**
     * ��������¼�
     * @param parent TObject
     * @param newObj TObject
     */
    public void onCreateTComponent(TObject parent,TObject newObj)
    {
        out("begin");
        if(parent == null||newObj == null)
            return;
        if (structureNodeRoot == null)
            return;
        TTreeNode node = (TTreeNode)parent.getData();
        if(node == null)
            return;
        String text = newObj.getType() + " [" + newObj.getTag() + "]";
        if(newObj.getPosition() != null)
            text += " " + newObj.getPosition();
        //�����ڵ�
        TTreeNode newNode = new TTreeNode(text,newObj.getTag());
        //�������ݶ���
        newNode.setData(newObj);
        //�����ڵ���󱣴浽���ݶ���
        newObj.setData(newNode);
        //����½ڵ�
        node.add(newNode);
        //ˢ�½ṹ��
        callMessage("UI|" + STRUCTURE_TREE + "|update");

        //ȡ���½�״̬
        node = selectNodeRoot.findNodeForText("<none>");
        if(node == null)
            return;
        JTreeBase tree = (JTreeBase)callMessage("UI|" + SELECT_TREE + "|getTree");
        if(tree == null)
            return;
        tree.setSelectionNode(new TTreeNode[]{node});
        out("end");
    }
    /**
     * ɾ������¼�
     * @param obj TObject
     */
    public void onDeleteTComponent(TObject obj)
    {
        out("begin");
        if(obj == null || structureNodeRoot == null)
            return;
        TTreeNode node = (TTreeNode)obj.getData();
        if(node == null)
            return;
        TTreeNode parent = (TTreeNode)node.getParent();
        parent.remove(node);
        callMessage("UI|" + STRUCTURE_TREE + "|update");
        callFunction("UI|" + UI_PROPERTY + "|setValue",new Vector());
        //����ת�Ƶ����̼����ؼ���
        callFunction("UI|UIDesign|onKeyGrabFocus");

        out("end");
    }
    /**
     * ����޸�Tag�¼�
     * @param obj TObject
     */
    public void onModifiedTag(TObject obj)
    {
        out("begin");
        if(obj == null || structureNodeRoot == null)
            return;
        TTreeNode node = (TTreeNode)obj.getData();
        String text = obj.getType() + " [" + obj.getTag() + "]";
        node.setText(text);
        callMessage("UI|" + STRUCTURE_TREE + "|update");
    }
    /**
     * ����Table�ı�ֵ
     * @param obj Object
     */
    public void onPropertyTableChangeValue(Object obj)
    {
        TTableNode node = (TTableNode)obj;
        if(node == null)
            return;
        TObject object = (TObject)node.getTable().getData();
        if(object == null)
            return;
        String name = (String)node.getTable().getValueAt(node.getRow(),0);
        String value = (String)node.getValue();
        TComponent component = object.getComponent();
        if(component == null)
            return;
        component.callFunction("setAttribute",name,value);
    }
    /**
     * ����Table˫��
     * @param row int
     */
    public void onPropertyTableDoubleClicked(int row)
    {
        if(row < 0)
            return;
        TObject tobject = (TObject)callFunction("UI|" + UI_PROPERTY + "|getData");
        if(tobject == null)
            return;
        String name = (String)callFunction("UI|" + UI_PROPERTY + "|getValueAt",row,0);
        String value = (String)callFunction("UI|" + UI_PROPERTY + "|getValueAt",row,1);
        //�õ������б�
        TAttributeList attributeList = tobject.getAttributeList();
        if(attributeList == null)
            return;
        //�õ����Զ���
        TAttributeList.TAttribute attribute = attributeList.get(name);
        if(attribute == null)
            return;
        if(attribute.getEditDialog() == null || attribute.getEditDialog().length() == 0)
            return;
        TParm parm = StringTool.getParm(attribute.getEditDialog());
        parm.setData("TITLE",tobject.getTag() + "." +  parm.getValue("TITLE"));
        parm.setData("VALUE",value);
        String fileName = getConfigString("editDialog." + parm.getData("TYPE"));
        if(fileName == null || fileName.length() == 0)
            return;
        String outValue = (String)openDialog(fileName,parm);
        if(outValue == null)
            return;
        outValue = StringTool.replaceAll(outValue,"\n"," ");
        callFunction("UI|" + UI_PROPERTY + "|setValueAt",outValue,row,1);
        TComponent component = tobject.getComponent();
        if(component == null)
            return;
        component.callFunction("setAttribute",name,outValue);
    }
    /**
     * ɾ��
     */
    public void onDelete()
    {
        getView().onDelete();
    }
    /**
     * ����
     */
    public void onSave()
    {
        callFunction("UI|" + UI_PROPERTY + "|acceptText");
        String newData = getConfigParse().save();
        if (data.equals(newData))
            return;
        TIOM_AppServer.writeFile(filename,newData.getBytes());
        data = newData;
    }
    /**
     * ����
     */
    public void onPreview()
    {
        String type = getConfigParse().getRoot().getType();
        if("TFrame".equalsIgnoreCase(type))
        {
            openWindow(filename,previewParm);
            return;
        }
    }
    public void onMEMAction()
    {
        MemoryMonitor.run();
    }
    public static void p()
    {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long userMemory = totalMemory - freeMemory;
        //System.out.println("availableProcessors=" + Runtime.getRuntime().availableProcessors());
        //System.out.println("totalMemory=" + totalMemory);
        //System.out.println("maxMemory=" + Runtime.getRuntime().maxMemory());
        //System.out.println("freeMemory=" + freeMemory);
        System.out.println("userMemory=" + userMemory);
    }
    /**
     * ��������Ի���
     */
    public void onInputParm()
    {
        String value = (String)openDialog(getConfigString(INPUT_PARM_DIALOG_CONFIG),previewParm);
        if(value == null)
            return;
        if(value.length() == 0)
            previewParm = null;
        else
            previewParm = value;
    }
    /**
     * ѡ������
     */
    public void onChangeLanguage()
    {
        String language = (String)getValue("LanguageComboTool");
        TSystem.setObject("Language",language);
        callFunction("UI|UIDesign|onChangeLanguage",language);
    }
}
