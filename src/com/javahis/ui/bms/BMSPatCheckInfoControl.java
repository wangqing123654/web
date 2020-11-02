package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.bms.BMSApplyMTool;
import jdo.sys.Pat;
import com.javahis.util.StringUtil;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.dongyang.ui.TRadioButton;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.data.TNull;

/**
 * <p>
 * Title: ��������¼
 * </p>
 *
 * <p>
 * Description: ��������¼
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
 * @author zhangy 2009.09.24
 * @version 1.0
 */
public class BMSPatCheckInfoControl
    extends TControl {

    public BMSPatCheckInfoControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            TParm parm = (TParm) obj;
            this.setValue("APPLY_NO", parm.getValue("APPLY_NO"));
            this.onApplyNoAction();
        }
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if ("".equals(this.getValueString("APPLY_NO"))) {
            this.messageBox("���������뵥��");
            return;
        }
        TParm parm = new TParm();
        // �ж��Ƿ���²���Ѫ��
        Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
        String blood_type = pat.getBloodType();
        if (!blood_type.equals(this.getValueString("TEST_BLD_B"))) {
            if (this.messageBox("��ʾ", "Ѫ�ʹ����벡��Ѫ�Ͳ��ϣ��Ƿ񱣴�", 2) == 0) {
//                parm.setData("BLOOD_TYPE", this.getValueString("TEST_BLD_B"));
//                parm.setData("BLOOD_TYPE_OLD", pat.getBloodType());
//                parm.setData("RH_FLG",
//                             this.getRadioButton("RH_A").isSelected() ? "+" :
//                             "-");
//                parm.setData("MR_NO", this.getValueString("MR_NO"));
            }
            else {
                return;
            }
        }

        parm.setData("BLOOD_TYPE", this.getValueString("TEST_BLD_B"));
        parm.setData("BLOOD_TYPE_OLD", pat.getBloodType());
        parm.setData("RH_FLG",
                     this.getRadioButton("RH_A").isSelected() ? "+" :
                     "-");
        parm.setData("MR_NO", this.getValueString("MR_NO"));
        
        // ���±�Ѫ����Ϣ
        Timestamp date = SystemTool.getInstance().getDate();
        parm.setData("APPLY_NO", this.getValue("APPLY_NO"));
        parm.setData("ANTI_A", this.getValue("ANTI_A"));
        parm.setData("ANTI_A_DATE", date);
        parm.setData("ANTI_B", this.getValue("ANTI_B"));
        parm.setData("ANTI_B_DATE", date);
        parm.setData("ANTI_D", this.getValue("ANTI_D"));
        parm.setData("ANTI_D_DATE", date);
        parm.setData("A1_CELL", this.getValue("A1_CELL"));
        parm.setData("A1_CELL_DATE", date);
        parm.setData("B_CELL", this.getValue("B_CELL"));
        parm.setData("B_CELL_DATE", date);
        parm.setData("SI", this.getValue("SI"));
        parm.setData("SII", this.getValue("SII"));
        parm.setData("SIII", this.getValue("SIII"));
        parm.setData("SI_III_DATE", date);
        parm.setData("TEST_BLD", this.getValue("TEST_BLD_A"));
        parm.setData("SIFT_RESU", this.getValue("SIFT_RESU"));
        parm.setData("SIFT_FLG", "Y");
        parm.setData("SIFT_DATE", date);
        parm.setData("HBSAG", this.getValue("HBSAG"));
        parm.setData("HBSAG_DATE", date);
        parm.setData("HCVAB", this.getValue("HCVAB"));
        parm.setData("HCVAB_DATE", date);
        parm.setData("HIV", this.getValue("HIV"));
        parm.setData("HIV_DATE", date);
        parm.setData("HTIV", this.getValue("HTIV"));
        parm.setData("HTIV_DATE", date);
        parm.setData("TEST_USER", Operator.getID());
        parm.setData("TEST_DATE", date);
        parm.setData("PRT_FLG", "Y");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());

        TParm result = TIOM_AppServer.executeAction(
            "action.bms.BMSBloodAction", "onUpdatePatCheckInfo", parm);
        // ������ж�
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        if ("".equals(this.getValueString("APPLY_NO"))) {
            this.messageBox("���������뵥��");
            return;
        }
        TParm parm = new TParm();
        // ���±�Ѫ����Ϣ
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        parm.setData("APPLY_NO", this.getValue("APPLY_NO"));
        parm.setData("ANTI_A", "");
        parm.setData("ANTI_A_DATE", tnull);
        parm.setData("ANTI_B", "");
        parm.setData("ANTI_B_DATE", tnull);
        parm.setData("ANTI_D", "");
        parm.setData("ANTI_D_DATE", tnull);
        parm.setData("A1_CELL", "");
        parm.setData("A1_CELL_DATE", tnull);
        parm.setData("B_CELL", "");
        parm.setData("B_CELL_DATE", tnull);
        parm.setData("SI", "");
        parm.setData("SII", "");
        parm.setData("SIII", "");
        parm.setData("SI_III_DATE", tnull);
        parm.setData("TEST_BLD", "");
        parm.setData("SIFT_RESU", "");
        parm.setData("SIFT_FLG", "");
        parm.setData("SIFT_DATE", tnull);
        parm.setData("HBSAG", "");
        parm.setData("HBSAG_DATE", tnull);
        parm.setData("HCVAB", "");
        parm.setData("HCVAB_DATE", tnull);
        parm.setData("HIV", "");
        parm.setData("HIV_DATE", tnull);
        parm.setData("HTIV", "");
        parm.setData("HTIV_DATE", tnull);
        parm.setData("TEST_USER", "");
        parm.setData("TEST_DATE", tnull);
        parm.setData("PRT_FLG", "N");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());

        TParm result = TIOM_AppServer.executeAction(
            "action.bms.BMSBloodAction", "onUpdatePatCheckInfo", parm);
        // ������ж�
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("ɾ��ʧ��");
            return;
        }
        this.messageBox("ɾ���ɹ�");
        onClear();

    }

    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "APPLY_NO;PRE_DATE;USE_DATE;END_DAYS;MR_NO;IPD_NO;"
            + "PAT_NAME;SEX;AGE;ID_NO;TEST_BLD;BLOOD_TEXT;ANTI_A;ANTI_B;"
            + "ANTI_D;A1_CELL;B_CELL;SI;SII;SIII;TEST_BLD_A;TEST_BLD_B;"
            + "SIFT_RESU;HBSAG;HCVAB;HIV;HTIV";
        this.clearValue(clearStr);
        this.getRadioButton("RH_A").setSelected(true);
    }

    /**
     * ��ʷ��¼��ѯ
     */
    public void onHistoryQuery() {
        if ("".equals(this.getValueString("APPLY_NO"))) {
            this.messageBox("�����뱸Ѫ����");
            return;
        }
        if ("".equals(this.getValueString("MR_NO"))) {
            this.messageBox("�����벡����");
            return;
        }

        TParm parm = new TParm();
        parm.setData("MR_NO", getValueString("MR_NO"));
        TParm result = BMSApplyMTool.getInstance().onApplyQuery(parm);
        if (result.getCount() <= 0) {
            this.messageBox("�������ϴμ�������");
            return;
        }
        int row = 0;
        for (int i = 0; i < result.getCount(); i++) {
            if (getValueString("APPLY_NO").equals(result.getValue("APPLY_NO", i))) {
                row = i - 1;
                break;
            }
        }
        if (row < 0) {
            this.messageBox("�������ϴμ�������");
            return;
        }
        TParm resultParm = result.getRow(row);
        this.setValue("APPLY_NO",resultParm.getValue("APPLY_NO"));
        this.onApplyNoAction();
    }

    /**
     * ��Ѫ���Ų�ѯ
     */
    public void onBloodNoQuery() {
        Object result = openDialog("%ROOT%\\config\\bms\\BMSPatApplyNo.x");
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                this.messageBox("û�д�������");
                return;
            }
            String apply_no = addParm.getValue("APPLY_NO");
            this.setValue("APPLY_NO", apply_no);
            this.onApplyNoAction();
        }
    }

    /**
     * ��Ѫ���Ų�ѯ(�س��¼�)
     */
    public void onApplyNoAction() {
        String apply_no = this.getValueString("APPLY_NO");
        if (!"".equals(apply_no)) {
            TParm parm = new TParm();
            parm.setData("APPLY_NO", apply_no);
            TParm result = BMSApplyMTool.getInstance().onApplyQuery(parm);
            if (result.getCount("APPLY_NO") == 0 || result.getCount() <= 0) {
                this.messageBox("��Ѫ��������");
                this.setValue("APPLY_NO", "");
                return;
            }
            this.setValue("PRE_DATE", result.getData("PRE_DATE", 0));
            this.setValue("USE_DATE", result.getData("USE_DATE", 0));
            this.setValue("END_DAYS", result.getData("END_DAYS", 0));
            this.setValue("MR_NO", result.getData("MR_NO", 0));
            this.setValue("IPD_NO", result.getData("IPD_NO", 0));
            // ��ѯ������Ϣ
            Pat pat = Pat.onQueryByMrNo(result.getValue("MR_NO", 0));
            this.setValue("PAT_NAME", pat.getName());
            this.setValue("SEX", pat.getSexString());
            Timestamp date = SystemTool.getInstance().getDate();
            this.setValue("AGE",
                          StringUtil.getInstance().showAge(pat.getBirthday(),
                date));
            this.setValue("ID_NO", pat.getIdNo());
            this.setValue("TEST_BLD", result.getData("TEST_BLD", 0));
            this.setValue("ANTI_A", result.getData("ANTI_A", 0));
            this.setValue("ANTI_B", result.getData("ANTI_B", 0));
            this.setValue("ANTI_D", result.getData("ANTI_D", 0));
            this.setValue("A1_CELL", result.getData("A1_CELL", 0));
            this.setValue("B_CELL", result.getData("B_CELL", 0));
            this.setValue("SI", result.getData("SI", 0));
            this.setValue("SII", result.getData("SII", 0));
            this.setValue("SIII", result.getData("SIII", 0));
            this.setValue("TEST_BLD_A", result.getData("TEST_BLD", 0));
            this.setValue("TEST_BLD_B", pat.getBloodType());
            this.setValue("SIFT_RESU", result.getData("SIFT_RESU", 0));
            this.setValue("HBSAG", result.getData("HBSAG", 0));
            this.setValue("HCVAB", result.getData("HCVAB", 0));
            this.setValue("HIV", result.getData("HIV", 0));
            this.setValue("HTIV", result.getData("HTIV", 0));
            if ("-".equals(pat.getBloodRHType())) {
                getRadioButton("RH_B").setSelected(true);
            }
            else {
                getRadioButton("RH_A").setSelected(true);
            }
            this.setValue("BLOOD_TEXT", pat.getBloodType());
        }
    }


    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

}
