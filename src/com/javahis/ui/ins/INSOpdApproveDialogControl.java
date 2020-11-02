package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import jdo.ins.INSOpdApproveTool;
/**
 *
 * <p>Title:����ҽ����˾���ſ����� </p>
 *
 * <p>Description:����ҽ����˾���ſ����� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2008.11.05
 * @version JavaHis 1.0
 */
public class INSOpdApproveDialogControl extends TControl {
    private TParm approve;
    int selectrow = -1;
    /**
     * ��ʼ������
     *  @return TParm
     */
    public void onInit() {
        super.onInit();
        //�õ�ǰ̨���������ݲ���ʾ�ڽ�����
        TParm approve = (TParm) getParameter();
        if (!approve.getData("count").equals("0")) {
            setValueForParm("MR_NO;PAT_NAME;ADM_DATE;SEX_CODE", approve.getParm("PATINFO"),
                            0);
            callFunction("UI|TABLE|setParmValue", approve.getParm("PATINFO"));
        }
        if (approve.getData("count").equals("0")) {
            setValueForParm("MR_NO;PAT_NAME;ADM_DATE;SEX_CODE", approve, 0);
        }
        //table1�ĵ��������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table1��˫�������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
        //Ĭ��Table����ʾ����Һż�¼
        //    onQuery();
    }

    /**
     *����Table�ĵ����¼�
     * @param row int
     */
    public void onTableClicked(int row) {
        //���������¼�
        this.callFunction("UI|TABLE|acceptText");
        selectrow = row;
    }
    /**
     * ����Table��˫�����¼�
     * @param row int
     */
    public void onTableDoubleClicked(int row) {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        this.setReturnValue((String) data.getData("CASE_NO", row));
        this.callFunction("UI|onClose");
    }

    /**
     * ��ѯ
     */
    public void onQuery() {

        TParm result = INSOpdApproveTool.getInstance().selPatInfo(approve.getParm("PATINFO"));
        if (result.getErrCode() < 0)
            return;
        if (result.getCount() == 0)
            this.messageBox("�޹Һ���Ϣ");
        this.callFunction("UI|TABLE|setParmValue", result);
    }
    /**
     * ȷ�ϰ�ť�¼�
     */
    public void onOK() {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        this.setReturnValue((String) data.getData("CASE_NO", selectrow));
        this.callFunction("UI|onClose");
    }
}
