package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TRadioButton;
import jdo.clp.PackTool;

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
public class CLPChkItemMainPopupControl
    extends TControl {

    public CLPChkItemMainPopupControl() {
        super();
    }

    private TTextFormat CLNCPATH_CODE;
    private TTextFormat SCHD_CODE;
    private TTextField DOSE_DAYS;
    private TCheckBox SEL_ALL;
    private TRadioButton ORDER_TYPE1;
    private TRadioButton ORDER_TYPE2;
    private TRadioButton ORDER_TYPE3;
    private TTextFormat DEPT_CODE;
    private TTextFormat CHKUSER_CODE;

    private TTable CLP_PACK;
    /**
     * ��ʼ������
     */
    public void onInit() {
        Object obj = getParameter();
        if (obj == null) {
            return;
        }
        if (! (obj instanceof TParm)) {
            return;
        }
        TParm parm = (TParm) obj;
        this.setValueForParm("CLNCPATH_CODE;SCHD_CODE;SCHD_DAY", parm);

        // ��ȡȫ�������ؼ�
        getAllComponent();
        // ע������¼�
        initControler();

        // �ؼ��ܿ�
        objectManage();

        // �б�����
        onQuery();
    }

    public void getAllComponent() {
        CLNCPATH_CODE = (TTextFormat)this.getComponent("CLNCPATH_CODE");
        SCHD_CODE = (TTextFormat)this.getComponent("SCHD_CODE");
        DOSE_DAYS = (TTextField)this.getComponent("DOSE_DAYS");
        SEL_ALL = (TCheckBox)this.getComponent("SEL_ALL");
        ORDER_TYPE1 = (TRadioButton)this.getComponent("ORDER_TYPE1");
        ORDER_TYPE2 = (TRadioButton)this.getComponent("ORDER_TYPE2");
        ORDER_TYPE3 = (TRadioButton)this.getComponent("ORDER_TYPE3");
        DEPT_CODE = (TTextFormat)this.getComponent("DEPT_CODE");
        CHKUSER_CODE = (TTextFormat)this.getComponent("CHKUSER_CODE");
        CLP_PACK = (TTable)this.getComponent("CLP_PACK");
    }

    public void initControler() {

    }

    public void objectManage() {
        CLNCPATH_CODE.setEnabled(false);
        SCHD_CODE.setEnabled(false);
    }

    public TParm dataFormat() {
        TParm parm = new TParm();
        parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
        parm.setData("SCHD_CODE", SCHD_CODE.getValue());
        parm.setData("DOSE_DAYS", DOSE_DAYS.getValue());
        parm.setData("ORDER_FLG", "N");
        if (ORDER_TYPE1.isSelected()) {
            parm.setData("ORDER_TYPE", "");
        }
        if (ORDER_TYPE2.isSelected()) {
            parm.setData("ORDER_TYPE", "UD");
        }
        if (ORDER_TYPE3.isSelected()) {
            parm.setData("ORDER_TYPE", "ST");
        }
        parm.setData("DEPT_CODE", DEPT_CODE.getValue());
        parm.setData("CHKUSER_CODE", CHKUSER_CODE.getValue());
        return parm;
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = dataFormat();
        TParm result = PackTool.getInstance().getPack02ListForManagerD(parm);
        CLP_PACK.setParmValue(result, CLP_PACK.getParmMap());
    }

    /**
     * ��շ���
     */
    public void onClear() {

    }

    public void onSelectAll() {
        int count = CLP_PACK.getRowCount();
        if ("Y".equals(SEL_ALL.getValue())) {
            for (int i = 0; i < count; i++) {
                CLP_PACK.setItem(i, "SEL_FLG", "Y");
            }
        }
        if ("N".equals(SEL_ALL.getValue())) {
            for (int i = 0; i < count; i++) {
                CLP_PACK.setItem(i, "SEL_FLG", "N");
            }
        }
    }

    /**
     * ���ط���
     */
    public void onReturn() {
        CLP_PACK.acceptText();
        int count = CLP_PACK.getRowCount();
        TParm data = CLP_PACK.getParmValue();
        TParm parm = new TParm();
        int j = 0;
        for (int i = 0; i < count; i++) {
            String selFlg = data.getValue("SEL_FLG", i);
            if ("Y".equals(selFlg)) {
                parm.setRowData(j, data.getRow(i));
                j++;
            }
        }
        if (parm.getCount("SEL_FLG") <= 0) {
            this.messageBox("��ѡ��Ҫ���ص����ݣ�");
            return;
        }
        setReturnValue(parm);
        this.closeWindow();
    }
}
