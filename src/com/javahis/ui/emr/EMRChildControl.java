package com.javahis.ui.emr;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TTree;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EMRChildControl extends TControl {
    /**
     * ��������
     */
    private static final String TREE_NAME = "TREECHILD";
    /**
     * ��ģ������
     */
    private TParm emrChildData;
    /**
     * ����
     */
    private TTreeNode treeRoot;

    /**
     * ��ʼ������
     */
    public void onInit(){
        super.onInit();
        Object obj = this.getParameter();
        if(obj!=null&&obj instanceof TParm){
            emrChildData = (TParm)obj;
        }
        //��ʼ��ҳ��
        initPage();
        //ע���¼�
        initEven();

    }
    /**
    * ע���¼�
    */
   public void initEven(){
       //����ѡ������Ŀ
       addEventListener(TREE_NAME+"->" + TTreeEvent.DOUBLE_CLICKED, "onTreeClicked");
   }
   /**
     * �õ�����
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode){
        TParm parm = new TParm(this.getDBTool().select("SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"'"));
        return parm.getValue("DEPT_CHN_DESC",0);
    }
    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool(){
        return TJDODBTool.getInstance();
    }
    /**
     * ��ʼ��ҳ��
     */
    public void initPage(){
        treeRoot = this.getTTree(TREE_NAME).getRoot();
        //�õ�����
        treeRoot.setText(emrChildData.getValue("NODE_NAME"));
        treeRoot.setType("Root");
        treeRoot.removeAllChildren();
        int rowCount = emrChildData.getInt("ACTION","COUNT");
        for(int i=0;i<rowCount;i++){
            //�ӽڵ�EMR_TEMPLET���е�NAME,PATH�ֶ�
            String deptCode = this.getDeptDesc(emrChildData.getValue("DEPT_CODE", i)).length()==0?"":"("+emrChildData.getValue("DEPT_CODE", i)+")";
            TTreeNode node = new TTreeNode(emrChildData.getValue("SUBCLASS_DESC", i)+deptCode,"1");
            //�洢EMR_TEMPLET���CODE�ֶ�
            node.setID(emrChildData.getValue("EMT_FILENAME", i));
            //�����Ƿ��EMR,EMR_TEMPLET���EMR_CLASS�ֶ�
            node.setValue(emrChildData.getValue("TEMPLET_PATH", i));
            //���ÿ���Ȩ��
            node.setGroup(emrChildData.getValue("DEPT_CODE", i));
            //�洢EMR_TEMPLET��� һ������
            //this.messageBox("== emr data=="+emrChildData.getRow(i));
            node.setData(emrChildData.getRow(i));
            //��������
            treeRoot.add(node);
        }
        this.getTTree(TREE_NAME).update();
    }
    /**
     * ˫���¼�
     * @param parm Object
     */
    public void onTreeClicked(Object parm){
        if(parm==null)
            return;
        TTreeNode node = (TTreeNode)parm;
        if(!emrChildData.getValue("DEPT_CODE").equals(node.getGroup())&&node.getGroup().length()!=0){
            this.messageBox("�����Ǵ�ģ��༭�Ŀ��ң�");
            return;
        }
        this.setReturnValue(node.getData());
        this.closeWindow();
    }
    /**
    *  �õ���
    * @param tag String
    * @return TTree
    */
   public TTree getTTree(String tag){
       return (TTree)this.getComponent(tag);
   }
   /**
    * ȷ��
    */
   public void onOK(){
       TTreeNode node = this.getTTree(TREE_NAME).getSelectNode();
       if(node==null){
           this.messageBox("��ѡ��Ҫ�򿪵�ģ�棡");
           return;
       }
       if(!emrChildData.getValue("DEPT_CODE").equals(node.getGroup())&&node.getGroup().length()!=0){
           this.messageBox("�����Ǵ�ģ��༭�Ŀ��ң�");
           return;
       }
       this.setReturnValue(node.getData());
       this.closeWindow();
   }
   /**
    * ȡ��
    */
   public void onNo(){
       this.closeWindow();
   }
}
