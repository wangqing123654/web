package com.javahis.ui.dss;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TComboBox;
import jdo.dss.DSSKPISQLTool;
import com.dongyang.util.TMessage;
import jdo.sys.Operator;
import com.dongyang.jdo.TJDODBTool;

/**
 * �ȼ�ָ�굥�����������
 * <p>Title: �ȼ�ָ�굥�����������</p>
 *
 * <p>Description:�ȼ�ָ�굥����������� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class DSSKPIControl extends TControl {

    /**
     * ��ʼ������
     */
    public void onInit() {
        //���ø����ʼ������
        super.onInit();
        //��ʼ����
        onInitTree();
        //��TREE��Ӽ����¼�
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
    }

    /**
     * ��ʼ����
     */
    public void onInitTree(){
        //�õ�������
        TTree tree = (TTree)getThisItem("TREE");
        //�õ��������ĸ����
        TTreeNode root = (TTreeNode) callMessage("UI|TREE|getRoot");
        //���ø���������
        root.setText("ָ��ȼ�");
        root.setType("Root");
        //��ʼ������ǰ���������Ҷ�ӽڵ�
        root.removeAllChildren();
        //�õ��ȼ�ָ��ּ�������ݼ���
        TParm treeKPIParm = DSSKPISQLTool.getInstance().queryDSSKPI();
        if(treeKPIParm == null)
            return;
        //ѭ��ȡ��ÿ����������ݽ������Ľ���
        for(int i =0;i<treeKPIParm.getCount("KPI_CODE");i++){
            String noteType = "Path";
            //���ݽڵ����������Ƿ�ΪҶ�ӽڵ�
            if(treeKPIParm.getValue("LEAF",i).equals("Y"))
                noteType = "UI";
            else
                noteType = "Path";
            //�����½ڵ�
            TTreeNode KPILeave = new TTreeNode("KPILEAVE" + i, noteType);
            //���ȼ�ָ����Ϣ���õ��ڵ㵱��
            KPILeave.setText(treeKPIParm.getValue("KPI_DESC", i));
            KPILeave.setValue(treeKPIParm.getValue("KPI_CODE", i));
            //��һ���Ľڵ��������
            if (treeKPIParm.getValue("PARENT_CODE", i).length() == 0)
                root.addSeq(KPILeave);
            //��������Ľڵ������Ӧ�ĸ��ڵ�����
            else
                root.findNodeForValue(treeKPIParm.getValue("PARENT_CODE", i)).add(KPILeave);
        }
        //�ػ���
        tree.update();
    }

    /**
     * ����˫���¼�
     */
    public void onTreeClicked(){
       //����Ƿ�õ����Ľڵ�
       if(((TTree)getThisItem("TREE")).getSelectNode() == null)
           return;
       //������е���Ϣ���
       ((TTable)getThisItem("TABLE")).removeRowAll();
       //ȡ�ø��ڵ���Ϣ
       onQueryKPICode(((TTree)getThisItem("TREE")).getSelectNode().getValue());
       //ȡ����Ӧ���ӽڵ���Ϣ
       onQueryChildren(((TTree)getThisItem("TREE")).getSelectNode().getValue());
    }


    /**
     * ��ѯ����
     */
    public void onQuery(){
        //������е���Ϣ�����
        ((TTable)getThisItem("TABLE")).removeRowAll();
        //��ѯ���ڵ���Ϣ
        onQueryKPICode(getValueString("KPI_CODE"));
        //��ѯ���ӽڵ���Ϣ
        onQueryChildren(getValueString("KPI_CODE"));
        //������һ�������λ��
        ((TTextField)getThisItem("KPI_DESC")).grabFocus();
    }


    /**
     * ��ѯ���ڵ㷽��
     * @param KPICode String
     */
    private void onQueryKPICode(String KPICode){
        //��˵ȼ�ָ������Ƿ�Ϸ�
        if(KPICode == null || KPICode.length() == 0)
            return;
        //ͨ���ȼ�ָ������ѯ�ȼ�ָ�������Ϣ
        TParm parm = DSSKPISQLTool.getInstance().queryDSSKPIByCode(KPICode);
        //���ݽ���ȼ�ָ����Ϣ
        String code =  getValueString("KPI_CODE");
        //��ս���
        onClear();
        //��ԭ�ȼ�ָ�������Ϣ
        setValue("KPI_CODE",code);
        if(parm == null||
           parm.getCount("KPI_CODE") <= 0)
            return;
        //���ȼ�ָ����Ϣ���õ�������
        setValue("KPI_CODE",parm.getValue("KPI_CODE",0));
        setValue("KPI_DESC",parm.getValue("KPI_DESC",0));
        setValue("PARENT_CODE",parm.getValue("PARENT_CODE",0));
        setValue("LEAF",parm.getValue("LEAF",0));
        setValue("PY1",parm.getValue("PY1",0));
        setValue("PY2",parm.getValue("PY2",0));
        setValue("SEQ",parm.getValue("SEQ",0));
        setValue("DESCRIPTION",parm.getValue("DESCRIPTION",0));
        setValue("KPI_VALUE",parm.getValue("KPI_VALUE",0));
        setValue("KPI_GOAL",parm.getValue("KPI_GOAL",0));
        setValue("KPI_STATUS",parm.getValue("KPI_STATUS",0));
        setValue("KPI_ATTRIBUTE",parm.getValue("KPI_ATTRIBUTE",0));
        setValue("KPI_KIND",parm.getValue("KPI_KIND",0));
    }

    /**
     * ��ѯ���ӽڵ㷽��
     * @param parentCode String
     */
    private void onQueryChildren(String parentCode){
        //��˸��ڵ�����Ƿ�Ϸ�
        if(parentCode == null ||
           parentCode.length() == 0)
            return;
        //��ѯ���ڵ������ĺ��ӽڵ�
        TParm parm = DSSKPISQLTool.getInstance().queryDSSKPIByParentCode(parentCode);
        if(parm == null||
           parm.getCount("KPI_CODE") == 0)
            return;
        //�����ӽ������ݷ��뻭��ı����
        ((TTable)getThisItem("TABLE")).setParmValue(parm);
    }

    /**
     * ��ȡ����ؼ�
     * @param tag String
     * @return Object
     */
    private Object getThisItem(String tag){
        return  callMessage("UI|"+tag+"|getThis");
    }

    /**
     * KPI���ƻس��¼�
     */
    public void onKPIDesc(){
        //�õ���ĸ����
        TTreeNode root = (TTreeNode) callMessage("UI|TREE|getRoot");
        //��˴˽ڵ�����Ƿ����±���
        if(root.findNodeForValue(getValueString("KPI_CODE")) == null){
            //�õ����±���
            setValue("SEQ", DSSKPISQLTool.getInstance().getMaxSeq());
            //����ƴ��1
            setValue("PY1",TMessage.getPy(getValueString("KPI_DESC")));
            //����ƴ��2
            setValue("PY2",TMessage.getPy(getValueString("KPI_DESC")));
        }
        //������һ����λ��
        ((TComboBox)getThisItem("KPI_ATTRIBUTE")).grabFocus();
    }

    /**
     * KPI���ʽ�س��¼�
     */
    public void onKPIValue(){
        ((TTextField)getThisItem("KPI_GOAL")).grabFocus();
    }

    /**
     * KPIĿ����ʽ�س��¼�
     */
    public void onKPIGoal(){
        ((TTextField)getThisItem("KPI_STATUS")).grabFocus();
    }

    /**
     * KPI״̬���ʽ�س��¼�
     */
    public void onKPIStatus(){
        ((TTextField)getThisItem("DESCRIPTION")).grabFocus();
    }

    /**
     * KPI���Իس��¼�
     */
    public void onKPIAttribute(){
        ((TComboBox)getThisItem("KPI_KIND")).grabFocus();
    }

    /**
     * KPI����س��¼�
     */
    public void onKPIKind(){
        ((TTextField)getThisItem("PARENT_CODE")).grabFocus();
    }

    /**
     * ����һس��¼�
     */
    public void onParentCode(){
        ((TComboBox)getThisItem("LEAF")).grabFocus();
    }

    /**
     * Ҷ�ӻس��¼�
     */
    public void onLeaf(){
        ((TTextField)getThisItem("KPI_VALUE")).grabFocus();
    }

    /**
     * �����񵥻��¼�
     */
    public void onTable(){
        //�õ�ѡ�����к�
        int row = ((TTable)getThisItem("TABLE")).getSelectedRow();
        //�õ�ѡ��������
        TParm parm = ((TTable)getThisItem("TABLE")).getParmValue();
        //��ѡ�������ݷŵ������Ϸ�
        setValue("KPI_CODE",parm.getValue("KPI_CODE",row));
        setValue("KPI_DESC",parm.getValue("KPI_DESC",row));
        setValue("PARENT_CODE",parm.getValue("PARENT_CODE",row));
        setValue("LEAF",parm.getValue("LEAF",row));
        setValue("PY1",parm.getValue("PY1",row));
        setValue("PY2",parm.getValue("PY2",row));
        setValue("SEQ",parm.getValue("SEQ",row));
        setValue("DESCRIPTION",parm.getValue("DESCRIPTION",row));
        setValue("KPI_VALUE",parm.getValue("KPI_VALUE",row));
        setValue("KPI_GOAL",parm.getValue("KPI_GOAL",row));
        setValue("KPI_STATUS",parm.getValue("KPI_STATUS",row));
        setValue("KPI_ATTRIBUTE",parm.getValue("KPI_ATTRIBUTE",row));
        setValue("KPI_KIND",parm.getValue("KPI_KIND",row));
    }

    /**
     * ���水ť�¼�
     */
    public void onSave(){
        //��˵ȼ�ָ������Ƿ�Ϊ��
        if(getValueString("KPI_CODE").length() == 0){
            messageBox("KPI���岻��Ϊ��");
            return;
        }
        //�õ���ĸ����
        TTreeNode root = (TTreeNode) callMessage("UI|TREE|getRoot");
        //������ڵ㲻Ϊ�ռ�˸��ڵ��Ƿ����
        if(getValueString("PARENT_CODE").length() != 0&&
           root.findNodeForValue(getValueString("PARENT_CODE")) == null){
            messageBox("���ڵ㲻����");
            ((TTextField)getThisItem("PARENT_CODE")).grabFocus();
            return;
        }
        //��˵ȼ�ָ������Ƿ����
        TParm tParm = DSSKPISQLTool.getInstance().queryDSSKPIByCode(getValueString("KPI_CODE"));
        if(tParm == null)
            return;
        //�õ���������
        TParm parm = getParmForTag("KPI_CODE;KPI_DESC;PARENT_CODE;LEAF;PY1;" +
                                   "PY2;SEQ;DESCRIPTION;KPI_VALUE;KPI_GOAL;"+
                                   "KPI_STATUS;KPI_ATTRIBUTE;KPI_KIND");
        //�����������
        parm.setData("SEQ",new Integer("" + parm.getData("SEQ")));
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_DATE",TJDODBTool.getInstance().getDBTime());
        parm.setData("OPT_TERM",Operator.getIP());
        TParm result = new TParm();
        //����ȼ�ָ����벻�����������������,�������������޸ķ���
        if(tParm.getCount() <= 0)
            result = DSSKPISQLTool.getInstance().insertDSSKPI(parm);
        else
            result = DSSKPISQLTool.getInstance().updateDSSKPIByCode(parm);
        if (result == null){
            messageBox("����ʧ��");
            return;
        }
        messageBox("����ɹ�");
        //���³�ʼ����
        onInitTree();
        //���µ��ò�ѯ����
        onQuery();
    }

    /**
     * ɾ����ť�����¼�
     */
    public void onDelete() {
        //����Ƿ�ѡ��Ҫɾ�������ڵ�
        if (((TTree) getThisItem("TREE")).getSelectNode() == null)
            return;
        //����ɾ������
        TParm result = DSSKPISQLTool.getInstance().deleteKPIAndChlidren(((TTree)getThisItem("TREE")).getSelectNode().getValue());
        if (result == null) {
            messageBox("ɾ��ʧ��");
            return;
        }
        messageBox("ɾ���ɹ�");
        //���³�ʼ����
        onInitTree();
        //���µ��ò�ѯ����
        onQuery();
    }

    /**
     * ��շ���
     */
    public void onClear(){
        setValue("KPI_CODE","");
        setValue("KPI_DESC","");
        setValue("PARENT_CODE","");
        setValue("LEAF","");
        setValue("PY1","");
        setValue("PY2","");
        setValue("SEQ","");
        setValue("DESCRIPTION","");
        setValue("KPI_VALUE","");
        setValue("KPI_GOAL","");
        setValue("KPI_STATUS","");
        setValue("KPI_ATTRIBUTE","");
        setValue("KPI_KIND","");
        ((TTable)getThisItem("TABLE")).removeRowAll();
    }

}
