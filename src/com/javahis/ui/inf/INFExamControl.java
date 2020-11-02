package com.javahis.ui.inf;

import com.dongyang.data.TParm;
import com.dongyang.control.TControl;
import jdo.inf.INFExamTool;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TIOM_AppServer;
import java.sql.Timestamp;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title: �����¼��</p>
 *
 * <p>Description:�����¼��</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFExamControl extends TControl {

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        //�豸��ϸ���༭�¼�
        addEventListener("INF_EXAM_RECORDD->" + TTableEvent.CHANGE_VALUE,"onTableValueChange");
        //�豸¼�¼�
        getTable("INF_EXAM_RECORDD").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,"onTableComponent");
        onInitUI();
    }

    /**
     * ��ʼ���������
     */
    public void onInitUI(){
        Timestamp timestamp = SystemTool.getInstance().getDate();
        setValue("EXAM_PERIOD",timestamp);
        setValue("DEPT_CODE",Operator.getDept());
        setValue("EXAM_DATE",timestamp);
        //========pangben modify 20110624 start Ȩ�����
        this.setValue("REGION_CODE", Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110624 stop
    }

    /**
     * ȡ�ü���¼
     */
    public void onExamStandM(){
        if(getValueString("EXAM_STANDM").length() == 0)
            return;
        TParm parm = INFExamTool.getInstance().selectINFExamStand(getValueString("EXAM_STANDM"));
        if(parm.getErrCode() < 0)
            return;
        getTable("INF_EXAM_RECORDD").setParmValue(parm);
    }

    /**
     * ȡ��Table�ؼ�
     * @param tableTag String
     * @return TTable
     */
    private TTable getTable(String tableTag){
        return ((TTable)getComponent(tableTag));
    }

    /**
     * ���淽��
     */
    public void onSave(){
        getTable("INF_EXAM_RECORDM").acceptText();
        getTable("INF_EXAM_RECORDD").acceptText();
        if(getTable("INF_EXAM_RECORDM").getSelectedRow() < 0)
            onNew();
        else
            onUpdate();
    }


    /**
     * ��������ǰ������ݺϷ���
     * @return boolean
     */
    public boolean checkSaveNew(){
        if(getTable("INF_EXAM_RECORDD").getRowCount() <= 0){
            messageBox("�޼����Ŀ��Ҫ����");
            return true;
        }
        if(getValueString("EXAM_PERIOD").length() == 0){
            messageBox("����ڼ䲻��Ϊ��");
            return true;
        }
        if(getValueString("DEPT_CODE").length() == 0){
            messageBox("�����Ҳ���Ϊ��");
            return true;
        }
        if(getValueString("EXAM_STANDM").length() == 0){
            messageBox("����׼����Ϊ��");
            return true;
        }
        if(getValueString("EXAM_DATE").length() == 0){
            messageBox("������ڿ�Ϊ��");
            return true;
        }
        return false;
    }

    /**
     * ������������
     */
    public void onNew(){
        if(checkSaveNew())
            return;
        TParm parm = new TParm();
        String examNo = INFExamTool.getInstance().getExamNo();
        String examPeriod = getValueString("EXAM_PERIOD").substring(0,7).replace("-","");
        String seq = INFExamTool.getInstance().getDeptExamPeriodMaxSeq(getValueString("DEPT_CODE"),examPeriod);
        if(seq.length() == 0){
            messageBox("ȡ���ʧ��ʧ��,������");
            return;
        }
        Timestamp timestamp = SystemTool.getInstance().getDate();
        TParm tableParm = getTable ("INF_EXAM_RECORDD").getParmValue();
        for(int i = 0;i < getTable("INF_EXAM_RECORDD").getRowCount();i++ ){
            parm.addData("EXAM_NO",examNo);
            parm.addData("EXAM_PERIOD",examPeriod + seq);
            parm.addData("DEPT_CODE",getValue("DEPT_CODE"));
            parm.addData("EXAM_STANDM",tableParm.getData("EXAM_STANDM",i));
            parm.addData("INFITEM_CODE",tableParm.getData("INFITEM_CODE",i));
            parm.addData("PASS_FLG",tableParm.getData("PASS_FLG",i));
            parm.addData("REMARK",tableParm.getData("REMARK",i) == null?"":tableParm.getData("REMARK",i));
            parm.addData("EXAM_DATE",getValue("EXAM_DATE"));
            parm.addData("OPT_USER",Operator.getID());
            parm.addData("OPT_DATE",timestamp);
            parm.addData("OPT_TERM",Operator.getIP());
            parm.addData("REGION_CODE",Operator.getRegion());//=======pangben modify 20110624
        }
        parm = TIOM_AppServer.executeAction(
            "action.inf.InfAction","insertINFExamRecord", parm);
        if(parm.getErrCode() < 0){
            messageBox("����ʧ��");
            return;
        }
        messageBox("����ɹ�");
        onQuery();
    }

    /**
     * �����޸ķ���
     */
    public void onUpdate(){
        TParm parm = new TParm();
        TParm tableParmM = getTable("INF_EXAM_RECORDM").getParmValue();
        TParm tableParmD = getTable("INF_EXAM_RECORDD").getParmValue();
        Timestamp timestamp = SystemTool.getInstance().getDate();
        for(int i = 0;i < getTable("INF_EXAM_RECORDD").getRowCount();i++ ){
            parm.addData("EXAM_NO",tableParmM.getData("EXAM_NO",getTable("INF_EXAM_RECORDM").getSelectedRow()));
            parm.addData("EXAM_PERIOD",tableParmM.getData("EXAM_PERIOD",getTable("INF_EXAM_RECORDM").getSelectedRow()));
            parm.addData("DEPT_CODE",tableParmM.getData("DEPT_CODE",getTable("INF_EXAM_RECORDM").getSelectedRow()));
            parm.addData("EXAM_STANDM",tableParmM.getData("EXAM_STANDM",getTable("INF_EXAM_RECORDM").getSelectedRow()));
            parm.addData("INFITEM_CODE",tableParmD.getData("INFITEM_CODE",i));
            parm.addData("PASS_FLG",tableParmD.getData("PASS_FLG",i));
            parm.addData("REMARK",tableParmD.getData("REMARK",i) == null ? "" :tableParmD.getData("REMARK",i));
            parm.addData("EXAM_DATE",tableParmD.getData("EXAM_DATE",i));
            parm.addData("OPT_USER",Operator.getID());
            parm.addData("OPT_DATE",timestamp);
            parm.addData("OPT_TERM",Operator.getIP());
        }
        parm = TIOM_AppServer.executeAction(
            "action.inf.InfAction","updateINFExamRecord", parm);
        if(parm.getErrCode() < 0){
            messageBox("����ʧ��");
            return;
        }
        messageBox("����ɹ�");
        onQuery();
    }

    /**
     * ��ѯ����
     */
    public void onQuery(){
        TParm parm = new TParm();
        if(getValueString("EXAM_NO").length() != 0)
            parm.setData("EXAM_NO",getValueString("EXAM_NO"));
        if(getValueString("EXAM_PERIOD").length() >= 7)
            parm.setData("EXAM_PERIOD",getValueString("EXAM_PERIOD").substring(0,7).replace("-",""));
        if(getValueString("DEPT_CODE").length() != 0)
            parm.setData("DEPT_CODE",getValueString("DEPT_CODE"));
        if(getValueString("EXAM_STANDM").length() != 0)
            parm.setData("EXAM_STANDM",getValueString("EXAM_STANDM"));
        if(getValueString("EXAM_DATE").length() != 0)
            parm.setData("EXAM_DATE",getValue("EXAM_DATE"));
        //==========pangben modify 20110624 start
        if(null !=this.getValueString("REGION_CODE") && this.getValueString("REGION_CODE").length() != 0 )
            parm.setData("REGION_CODE",this.getValueString("REGION_CODE"));
        //==========pangben modify 20110624 stop
        if(parm.getNames().length == 0)
            return;
        TParm parmM = INFExamTool.getInstance().selectINFExamRecordM(parm);
        if(parmM.getErrCode() < 0)
            return;
        getTable("INF_EXAM_RECORDM").setParmValue(parmM);
        getTable("INF_EXAM_RECORDD").removeRowAll();
     }

     /**
      * ����������Ϣ��ѯ��ϸ��
      */
     public void onTableM(){
         TParm parm = new TParm();
         TParm tableMParm = getTable("INF_EXAM_RECORDM").getParmValue();
         parm.setData("EXAM_NO",tableMParm.getData("EXAM_NO",getTable("INF_EXAM_RECORDM").getSelectedRow()));
         parm = INFExamTool.getInstance().selectINFExamRecordD(parm);
         if(parm.getErrCode() < 0){
             getTable("INF_EXAM_RECORDM").setParmValue(getTable("INF_EXAM_RECORDM").getParmValue());
             return;
         }
         setValue("EXAM_NO",tableMParm.getData("EXAM_NO",getTable("INF_EXAM_RECORDM").getSelectedRow()));
         String examPeriod = tableMParm.getValue("EXAM_PERIOD",getTable("INF_EXAM_RECORDM").getSelectedRow());
         setValue("EXAM_PERIOD",StringTool.getTimestamp(examPeriod.substring(0,4) + "-" + examPeriod.substring(4,6),"yyyy-MM"));
         setValue("DEPT_CODE",tableMParm.getData("DEPT_CODE",getTable("INF_EXAM_RECORDM").getSelectedRow()));
         setValue("EXAM_STANDM",tableMParm.getData("EXAM_STANDM",getTable("INF_EXAM_RECORDM").getSelectedRow()));
         setValue("EXAM_DATE",tableMParm.getData("EXAM_DATE",getTable("INF_EXAM_RECORDM").getSelectedRow()));
         setValue("TOT_GAINPOINT",tableMParm.getData("TOT_GAINPOINT",getTable("INF_EXAM_RECORDM").getSelectedRow()));
         ((TTextField)getComponent("EXAM_NO")).setEnabled(false);
         ((TTextFormat)getComponent("EXAM_PERIOD")).setEnabled(false);
         ((TTextFormat)getComponent("DEPT_CODE")).setEnabled(false);
         ((TTextFormat)getComponent("EXAM_STANDM")).setEnabled(false);
         ((TTextFormat)getComponent("EXAM_DATE")).setEnabled(false);
         getTable("INF_EXAM_RECORDD").setParmValue(parm);
     }

     /**
      * ��շ���
      */
     public void onClear(){
         setValue("EXAM_NO","");
         setValue("EXAM_STANDM","");
         setValue("TOT_GAINPOINT","");
         onInitUI();
         ((TTextField)getComponent("EXAM_NO")).setEnabled(true);
         ((TTextFormat)getComponent("EXAM_PERIOD")).setEnabled(true);
         ((TTextFormat)getComponent("DEPT_CODE")).setEnabled(true);
         ((TTextFormat)getComponent("EXAM_STANDM")).setEnabled(true);
         ((TTextFormat)getComponent("EXAM_DATE")).setEnabled(true);
          getTable("INF_EXAM_RECORDM").removeRowAll();
          getTable("INF_EXAM_RECORDD").removeRowAll();
     }

     /**
      * ɾ������
      */
     public void onDelete(){
         if(getTable("INF_EXAM_RECORDM").getSelectedRow() < 0){
             messageBox("��ɾ������");
             return;
         }
         TParm parm = new TParm();
         TParm tableParmM = getTable("INF_EXAM_RECORDM").getParmValue();
         TParm tableParmD = getTable("INF_EXAM_RECORDD").getParmValue();
         Timestamp timestamp = SystemTool.getInstance().getDate();
         for(int i = 0;i < getTable("INF_EXAM_RECORDD").getRowCount();i++ ){
             parm.addData("EXAM_NO",tableParmM.getData("EXAM_NO",getTable("INF_EXAM_RECORDM").getSelectedRow()));
             parm.addData("EXAM_PERIOD",tableParmM.getData("EXAM_PERIOD",getTable("INF_EXAM_RECORDM").getSelectedRow()));
             parm.addData("DEPT_CODE",tableParmM.getData("DEPT_CODE",getTable("INF_EXAM_RECORDM").getSelectedRow()));
             parm.addData("EXAM_STANDM",tableParmM.getData("EXAM_STANDM",getTable("INF_EXAM_RECORDM").getSelectedRow()));
             parm.addData("INFITEM_CODE",tableParmD.getData("INFITEM_CODE",i));
             parm.addData("PASS_FLG",tableParmD.getData("PASS_FLG",i));
             parm.addData("REMARK",tableParmD.getData("REMARK",i));
             parm.addData("EXAM_DATE",tableParmD.getData("EXAM_DATE",i));
             parm.addData("OPT_USER",Operator.getID());
             parm.addData("OPT_DATE",timestamp);
             parm.addData("OPT_TERM",Operator.getIP());
         }
         parm = TIOM_AppServer.executeAction(
             "action.inf.InfAction","deleteINFExamRecord", parm);
         if(parm.getErrCode() < 0){
             messageBox("ɾ��ʧ��");
             return;
         }
        messageBox("ɾ���ɹ�");
        deleteQuery();
     }

     /**
      * ɾ�������
      */
     private void deleteQuery(){
         setValue("EXAM_NO","");
         setValue("EXAM_STANDM","");
         setValue("TOT_GAINPOINT","");
         onInitUI();
         ((TTextField)getComponent("EXAM_NO")).setEnabled(true);
         ((TTextFormat)getComponent("EXAM_PERIOD")).setEnabled(true);
         ((TTextFormat)getComponent("DEPT_CODE")).setEnabled(true);
         ((TTextFormat)getComponent("EXAM_STANDM")).setEnabled(true);
         ((TTextFormat)getComponent("EXAM_DATE")).setEnabled(true);
         getTable("INF_EXAM_RECORDM").removeRow(getTable("INF_EXAM_RECORDM").getSelectedRow());
         getTable("INF_EXAM_RECORDD").removeRowAll();
     }

     /**
      * �����Ǵ����¼�
      * @param obj Object
      * @return boolean
      */
     public boolean onTableValueChange(Object obj) {
         TTableNode node = (TTableNode) obj;
         if(node.getColumn() != 2)
             return false;
         TParm tableParm = getTable("INF_EXAM_RECORDD").getParmValue();
         int tot = 0;
         for(int i = 0;i < getTable("INF_EXAM_RECORDD").getRowCount();i++){
             if(i != node.getRow() && tableParm.getData("PASS_FLG",i).equals("N") ||
                i == node.getRow() && node.getValue().equals("N"))
                 continue;
             tot += tableParm.getInt("ITEM_GAINPOINT",i);
         }
         setValue("TOT_GAINPOINT",tot);
         return false;
    }

    /**
     * ��ϸ�������¼�
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        TTable chargeTable = (TTable) obj;
        chargeTable.acceptText();
        return true;
    }
}
