package com.javahis.ui.opb;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.data.TParm;

public class OPBChargerWorkListControl
    extends TControl {
    int selectrow = -1;
    TParm data = new TParm();
    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        //table1�������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table1ֵ�ı��¼�
        this.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                              "onTableChangeValue");
        callFunction("UI|TABLE|addRow");
    }

    /**
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {
        //���������¼�
        this.callFunction("UI|TABLE|acceptText");
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        setValueForParm("ACCOUNT_TYPE;ACCOUNT_USER;TOT_AMT",
                        data, row); //�����Ϸ�
        selectrow = row;
        callFunction("UI|delete|setEnabled", true);
    }
    /**
     * ���Ӷ�Tableֵ�ı�ļ���
     * @param obj Object
     */
    public void onTableChangeValue(Object obj) {
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return;
        if (node.getColumn() != 0)
            return;
        node.getTable().getParmValue().setData("FLG", node.getRow(),
                                               node.getValue());
    }
    /**
     * �Ƽ�
     */
//     public void onCharges(){
//
//}
    /**
     * ��ӡ
     */
//     public void onFill(){
//
//     }
    /**
     *Ԥ��
     */
//     public void onFill(){
//
//     }
    /**
     * ���
     */
//     public void onFill(){
//
//     }
    /**
     * ��ϸ
     */
//   public void onDetial(){
//       this.openDialog("%ROOT%\\config\\opb\\OPBFillDetial.x");
//   }


}
