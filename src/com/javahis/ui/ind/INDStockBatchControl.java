package com.javahis.ui.ind;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.ind.INDSQL;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TCheckBox;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>
 * Title: �ս���ʹ���Control
 * </p>
 *
 * <p>
 * Description: �ս���ʹ���Control
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.02
 * @version 1.0
 */
public class INDStockBatchControl extends TControl{

    // ȫ������Ȩ��
    //private boolean dept_flg = true;

    public INDStockBatchControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        initPage();
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        /**
         * Ȩ�޿���
         * Ȩ��1:һ�����ֻ��ʾ������������
         * Ȩ��9:��ʾȫԺҩ�ⲿ��
         */
        // ��ʾȫԺҩ�ⲿ��
        if (!this.getPopedem("deptAll")) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion())));
            getComboBox("ORG_CODE").setParmValue(parm);
            //dept_flg = false;
        }

        Timestamp date = StringTool.getTimestamp(new Date());
        date = StringTool.rollDate(date, -1);
        this.setValue("TRAN_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("δ���˿��Ҳ���Ϊ��");
            return;
        }
        Timestamp date = StringTool.getTimestamp(new Date());
        //1.ҩƷ����ս�������ҵ
        if (this.getCheckBox("STOCK_BATCH_FLG").isSelected()) {
            //1.1 ҩ��������趨
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getINDSysParm()));
            if (parm.getValue("MANUAL_TYPE", 0) == null) {
                this.messageBox("ҩ���������δ�趨");
                return;
            }
            //1.2 ��ѯ�����Ƿ�Ϊ�Զ�������
            String manual_type = parm.getValue("MANUAL_TYPE", 0);
            if ("0".equals(manual_type)) {
                this.messageBox("ҩ����������趨Ϊ�Զ�������,�����ֹ���");
                return;
            }
            //1.3 �ж��Ƿ��Ѿ�������
            parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getStockBatchByOrgCode(this.getValueString("ORG_CODE"),
                                       this.getValueString("TRAN_DATE").
                                       replaceAll("-", ""))));
            if (parm.getInt("NUM", 0) > 0) {
                this.messageBox("�ò���" +
                                this.getValueString("TRAN_DATE").substring(0, 10) +
                                "�����ս�!");
                return;
            }
            //1.4 ҩƷ����ս�������ҵ
            TParm inparm = new TParm();
            inparm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
            inparm.setData("BATCH_FLG", "Y");
            inparm.setData("OPT_USER", Operator.getID());
            inparm.setData("OPT_DATE", date);
            inparm.setData("OPT_TERM", Operator.getIP());
            inparm.setData("TRANDATE",
                           this.getValue("TRAN_DATE"));
            TParm result = new TParm();
            result = TIOM_AppServer.executeAction(
                "action.ind.INDStockBatchAction", "onIndStockBatch", inparm);
            // �����ж�
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
        }

        //2.����ҩƷ�Զ����䵵��ҵ
        if (this.getCheckBox("GIF_FLG").isSelected()) {
            //this.messageBox("");
        }
    }

    /**
     * ���δ���˿���
     */
    public void onChangeOrgCode() {
        if (!"".equals(getValueString("ORG_CODE"))) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getINDOrgType(this.getValueString("ORG_CODE"))));
            if ("A".equals(parm.getValue("ORG_TYPE", 0))) {
                this.getCheckBox("GIF_FLG").setSelected(false);
            }
            else {
                this.getCheckBox("GIF_FLG").setSelected(true);
            }
        }
    }

    /**
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * �õ�CheckBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

}
