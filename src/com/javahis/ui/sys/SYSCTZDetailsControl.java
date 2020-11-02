package com.javahis.ui.sys;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import jdo.sys.StatusDetailsTool;
import com.dongyang.ui.TTreeNode;
import jdo.sys.CTZTool;
import com.dongyang.ui.event.TTreeEvent;
import jdo.sys.Operator;
import jdo.sys.FeeCodeOperateTool;
import com.dongyang.ui.TTableNode;
import jdo.sys.ChargeDetailList;

/**
 * <p>Title:�����ϸ </p>
 *
 * <p>Description:�����ϸ </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author TParm
 * @version 1.0
 */
public class SYSCTZDetailsControl
    extends TControl {
    int selectrow = -1; //table��ѡ��
    private static final String TREE = "Tree";

    /**
     * ��ʼ������
     *  @return TParm
     */
    public void onInit() {
        super.onInit();
        //table�������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table��ֵ�ı��¼�
        this.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                              "onTableChangeValue");
        callFunction("UI|save|setEnabled", false);
        callFunction("UI|CTZ_CODE|setEnabled", false);
    }

    /**
     * ��ѯ����
     * @return TParm
     */
    public void onQuery() {
        this.callFunction("UI|Table|removeRowAll"); //���table
       TParm data = StatusDetailsTool.getInstance().selectalldata();
        this.callFunction("UI|TABLE|setParmValue", data); //��table���
        this.onClear(); //������շ���
    }
    /**
     *���
     */
    public void onClear() {
        //�����������
        clearValue("CHARGE_HOSP_CODE;OWN_RATE;CTZ_CODE;SEQ;DESCRITPION");
        callFunction("UI|TABLE|learSelectc", true);
        selectrow = -1;
        //ɾ�����ܱ��༭
        callFunction("UI|save|setEnabled", false);
        callFunction("UI|TABLE|clearSelection");
    }

    /**
     *���Ӷ�Table��ѡ�м���
     * @param row int
     */
    public void onTableClicked(int row) {
        //table���������¼�
        this.callFunction("UI|TABLE|acceptText");
        setValueForParm("CHARGE_HOSP_CODE;CTZ_CODE;SEQ;DESCRITPION;OWN_RATE",
                        (TParm) callFunction("UI|TABLE|getParmValue"), row); //�����Ϸ�
        selectrow = row;
        callFunction("UI|save|setEnabled", true);
    }

    /**
     *���Ӷ�Tableֵ�ı�ļ���
     * @param row int
     */
    public void onTableChangeValue(Object obj) {
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return;
        if (node.getColumn() != 0)
            return;
        TParm data=(TParm) callFunction("UI|TABLE|getParmValue");
//        System.out.println(node.getTable().getParmValue());
        //��������������������������������������������������������������������������
        ChargeDetailList list = new ChargeDetailList();
        list.initParm(data);
//        for (int i = 0; i < list.size(); i++)
//            System.out.println(list.getChargeDetail(i));
//        //list.removeData(1);
//        list.getChargeDetail(0).modifySeq(1000);
//        list.getChargeDetail(0).modifyChargeHospCode("88");
//        //list.getChargeDetail(0).modifySeq(100);
//        list.newChargeDetail().setCtzCode("1");
//        list.newChargeDetail().setCtzCode("2");
//        list.newChargeDetail().setCtzCode("3");
//        System.out.println("--------------------");
//        for (int i = 0; i < list.size(); i++)
//            System.out.println(list.getChargeDetail(i));
//        System.out.println(list.getParm());

    }
    /**
     * ��������
     */
    public void onSave() {
        this.callFunction("UI|TABLE|acceptText");
        //����ϸ��
        if (selectrow >0) {
            //����ϸ�����
            TParm parm = getParmForTag(
                "CTZ_CODE;CHARGE_HOSP_CODE;OWN_RATE;CTZ_CODE;SEQ;DESCRITPION");
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_TERM", Operator.getIP());
           TParm result= StatusDetailsTool.getInstance().updateData(parm);
           if (result.getErrCode() < 0) {
               this.messageBox("E0005"); //ִ��ʧ��
               return;
           }
           else {
               this.messageBox("P0005"); //ִ�гɹ�
           }
           TParm data=(TParm) callFunction("UI|TABLE|getParmValue");
            data.setRowData(selectrow, parm);
            callFunction("UI|TABLE|setRowParmValue", selectrow, data);
        }
        selectrow = -1;
    }

}
