package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import jdo.bms.BMSTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TComboBox;
import com.javahis.system.combo.TComboDept;

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
public class BMSQueryPatControl
    extends TControl {
    // �ⲿ���ô���
    private TParm parm;

    private String mr_no;

    private String adm_type;

    public BMSQueryPatControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        Object obj = this.getParameter();
        if (obj != null) {
            parm = (TParm) obj;
            mr_no = parm.getValue("MR_NO");
            if (parm.existData("IO_TYPE")) {
                adm_type = parm.getValue("IO_TYPE");
            }
        }

        // ��ʼ����������
        initPage();
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        Timestamp date = StringTool.getTimestamp(new Date());
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        this.setValue("MR_NO", mr_no);
        this.setValue("IO_TYPE", adm_type);
        this.onChangeIOType();
        this.onQuery();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        if (!"".equals(this.getValueString("START_DATE"))) {
            parm.setData("START_DATE", this.getValue("START_DATE"));
        }
        if (!"".equals(this.getValueString("END_DATE"))) {
            parm.setData("END_DATE", this.getValue("END_DATE"));
        }
        if (!"".equals(this.getValueString("IO_TYPE"))) {
            parm.setData("IO_TYPE", this.getValue("IO_TYPE"));
        }
        if (!"".equals(this.getValueString("DEPT_CODE"))) {
            parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
        }
        if (!"".equals(this.getValueString("MR_NO"))) {
            parm.setData("MR_NO", this.getValue("MR_NO"));
        }
        if (!"".equals(this.getValueString("PAT_NAME"))) {
            parm.setData("PAT_NAME", this.getValue("PAT_NAME"));
        }
        if (!"".equals(this.getValueString("ID_NO"))) {
            parm.setData("ID_NO", this.getValue("ID_NO"));
        }
        if (!"".equals(this.getValueString("BIRTH_DATE"))) {
            parm.setData("BIRTH_DATE", this.getValue("BIRTH_DATE"));
        }
        TParm result = BMSTool.getInstance().onQueryPat(parm);
        //System.out.println("result  " + result);
        if (result == null || result.getCount("ADM_TYPE") == 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        this.getTable("TABLE").setParmValue(result);
    }

    /**
     * TABLE˫���¼�
     */
    public void onTableDoubleClickAction() {
        //this.messageBox("");
        TTable table = this.getTable("TABLE");
        TParm result = table.getParmValue().getRow(table.getSelectedRow());
        setReturnValue(result);
        this.closeWindow();
    }

    /**
     * �ż�ס����ʱ��
     */
    public void onChangeIOType() {
        this.getComboDept("DEPT_CODE").revalidate();
        if (!"".equals(this.getValue("IO_TYPE"))) {
            if ("O".equals(this.getValue("IO_TYPE"))) {
                this.getComboDept("DEPT_CODE").setOpdFitFlg("Y");
            }
            else if ("E".equals(this.getValue("IO_TYPE"))) {
                this.getComboDept("DEPT_CODE").setEmgFitFlg("Y");
            }
            else if ("I".equals(this.getValue("IO_TYPE"))) {
                this.getComboDept("DEPT_CODE").setIpdFitFlg("Y");
            }
            this.getComboDept("DEPT_CODE").onQuery();
        }
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
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
     * �õ�ComboDept����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboDept getComboDept(String tagName) {
        return (TComboDept) getComponent(tagName);
    }

}
