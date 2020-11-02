package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.javahis.util.StringUtil;
import com.javahis.system.textFormat.TextFormatINVOrg;
import jdo.inv.INVSQL;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.system.textFormat.TextFormatINVMaterialloc;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.inv.InvStockMTool;

/**
 * <p>Title: ���ʿ�������趨</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy
 * @version 1.0
 */
public class INVStockMControl
    extends TControl {

    private String action = "insertM";

    // ������
    private TTable table_m;

    // ϸ����
    private TTable table_d;

    public INVStockMControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        TParm parm = new TParm();
        parm.setData("SUP_CODE", "");
        parm.setData("REGION_CODE",Operator.getRegion()) ;
        // ���õ����˵�
        getTextField("INV_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parm);
        // ������ܷ���ֵ����
        getTextField("INV_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

        // ��ʼ��TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        callFunction("UI|DISPENSE_ORG_CODE|setEnabled", false);
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        table_m.removeRowAll();
        table_d.removeRowAll();
        String org_code = getValueString("ORG_CODE");
        if ("".equals(org_code)) {
            this.messageBox("���Ŵ��벻��Ϊ��");
            return;
        }
        String sql = INVSQL.getInvStockM(org_code,
                                         getValueString("INV_CODE"),
                                         getValueString("MATERIAL_LOC_CODE"));
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        table_m.setParmValue(parm);
    }

    /**
     * ��շ���
     */
    public void onClear() {
        // ���VALUE
        String clear =
            "INV_CODE;INV_CHN_DESC;MATERIAL_LOC_CODE;DISPENSE_FLG;STOCK_UNIT;"
            + "SAFE_QTY;MAX_QTY;MIN_QTY;ECONOMICBUY_QTY;REGION_CODE;"
            + "AVERAGE_DAYUSE_QTY;MM_USE_QTY;DISPENSE_ORG_CODE;ORG_CODE;BASE_QTY";
        this.clearValue(clear);
        table_m.removeRowAll();
        table_m.setSelectionMode(0);
        table_d.removeRowAll();
        table_d.setSelectionMode(0);
  callFunction("UI|DISPENSE_ORG_CODE|setEnabled", false);
        action = "insertM";
    }

    /**
     * ���淽��
     */
    public void onSave() {
        // ���������
        TParm parm = new TParm();
        // ���ؽ����
        TParm result = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        parm.setData("INV_CODE", this.getValueString("INV_CODE"));
        parm.setData("MATERIAL_LOC_CODE",
                     this.getValueString("MATERIAL_LOC_CODE"));
        parm.setData("DISPENSE_FLG", this.getValueString("DISPENSE_FLG"));
        parm.setData("DISPENSE_ORG_CODE",
                     this.getValueString("DISPENSE_ORG_CODE"));
        parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        parm.setData("MM_USE_QTY", this.getValueDouble("MM_USE_QTY"));
        parm.setData("AVERAGE_DAYUSE_QTY", this.getValueDouble("AVERAGE_DAYUSE_QTY"));
        parm.setData("MAX_QTY", this.getValueDouble("MAX_QTY"));
        parm.setData("SAFE_QTY", this.getValueDouble("SAFE_QTY"));
        parm.setData("MIN_QTY", this.getValueDouble("MIN_QTY"));
        parm.setData("ECONOMICBUY_QTY", this.getValueDouble("ECONOMICBUY_QTY"));
        parm.setData("STOCK_UNIT", this.getValueString("STOCK_UNIT"));
        parm.setData("STOCK_FLG", "N");
        parm.setData("STOCK_QTY", 0);
        parm.setData("BASE_QTY", this.getValueDouble("BASE_QTY")) ; //����
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());

        if ("insertM".equals(action)) {
            if (!CheckDataM()) {
                return;
            }
            result = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvStockM(this.getValueString("ORG_CODE"),
                             this.getValueString("INV_CODE"))));
            if (result.getCount() > 0) {
                this.messageBox("�������Ѵ���");
                return;
            }
            result = InvStockMTool.getInstance().onInsert(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
        }
        else {
            if (!CheckDataM()) {
                return;
            }
            result = InvStockMTool.getInstance().onUpdate(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
        }
        onQuery();
    }

    /**
     * TABLE_M�����¼�
     */
    public void onTableMClicked() {
        int row = table_m.getSelectedRow();
        if (row != -1) {
            this.setValue("INV_CODE", table_m.getItemData(row, "INV_CODE"));
            this.setValue("INV_CHN_DESC",
                          table_m.getItemData(row, "INV_CHN_DESC"));
            this.setValue("MATERIAL_LOC_CODE",
                          table_m.getItemData(row, "MATERIAL_LOC_CODE"));
            this.setValue("DISPENSE_FLG",
                          table_m.getItemData(row, "DISPENSE_FLG"));
            this.setValue("DISPENSE_ORG_CODE",
                          table_m.getItemData(row, "DISPENSE_ORG_CODE"));
            this.setValue("MM_USE_QTY", table_m.getItemData(row, "MM_USE_QTY"));
            this.setValue("AVERAGE_DAYUSE_QTY",
                          table_m.getItemData(row, "AVERAGE_DAYUSE_QTY"));
            this.setValue("MAX_QTY", table_m.getItemData(row, "MAX_QTY"));
            this.setValue("SAFE_QTY", table_m.getItemData(row, "SAFE_QTY"));
            this.setValue("MIN_QTY", table_m.getItemData(row, "MIN_QTY"));
            this.setValue("ECONOMICBUY_QTY",
                          table_m.getItemData(row, "ECONOMICBUY_QTY"));
            this.setValue("REGION_CODE",
                          table_m.getParmValue().getValue("REGION_CODE", row));
            this.setValue("BASE_QTY",table_m.getItemData(row, "BASE_QTY")); //����
            this.setValue("STOCK_UNIT",
                          table_m.getParmValue().getValue("STOCK_UNIT", row));
            if ("Y".equals(table_m.getItemData(row, "DISPENSE_FLG"))) {
            	callFunction("UI|DISPENSE_ORG_CODE|setEnabled", true);
            }
            else {
            	callFunction("UI|DISPENSE_ORG_CODE|setEnabled", false);
            }
            action = "updateM";

            // ��ϸ��Ϣ
            table_d.removeRowAll();
            table_d.setSelectionMode(0);
            String sql = INVSQL.getInvStockD(this.getValueString("ORG_CODE"),
                                             this.getValueString("INV_CODE"));
            TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
            //System.out.println("sql---" + sql);
            if (parm == null || parm.getCount() <= 0) {
                return;
            }
            table_d.setParmValue(parm);
        }
    }

    /**
     * ���Ÿı��¼�
     */
    public void onChangeOrgCode() {
        this.setValue("MATERIAL_LOC_CODE", "");
        String org_code = this.getValueString("ORG_CODE");
        if (!"".equals(org_code)) {
            // �趨��λ
            ( (TextFormatINVMaterialloc) getComponent("MATERIAL_LOC_CODE")).
                setOrgCode(org_code);
            ( (TextFormatINVMaterialloc) getComponent("MATERIAL_LOC_CODE")).
                onQuery();
        }

        TParm parm = new TParm(TJDODBTool.getInstance().select(INVSQL.getINVOrg(
            org_code)));
        String org_type = "";
        this.setValue("DISPENSE_ORG_CODE", "");
        if ("C".equals(parm.getValue("ORG_TYPE", 0))) {
            org_type = "B";
        }
        else if ("B".equals(parm.getValue("ORG_TYPE", 0))) {
            org_type = "A";
        }
        else {
            org_type = "-";
        }
//        ( (TextFormatINVOrg)this.getComponent("DISPENSE_ORG_CODE")).setOrgType(
//            org_type);
//        ( (TextFormatINVOrg)this.getComponent("DISPENSE_ORG_CODE")).onQuery();
    }


    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String inv_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(inv_code))
            getTextField("INV_CODE").setValue(inv_code);
        String inv_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(inv_desc))
            getTextField("INV_CHN_DESC").setValue(inv_desc);
        if (!StringUtil.isNullString("STOCK_UNIT"))
            setValue("STOCK_UNIT", parm.getValue("STOCK_UNIT"));
    }

    /**
     * �����ı��¼�
     */
    public void onDispenseFlgAction() {
        if ("Y".equals(this.getValueString("DISPENSE_FLG"))) {
//            ( (TextFormatINVOrg)this.getComponent("DISPENSE_ORG_CODE")).
//                setEnabled(true);
        	callFunction("UI|DISPENSE_ORG_CODE|setEnabled", true);

        }
        else {
        	callFunction("UI|DISPENSE_ORG_CODE|setEnabled", false);
//            ( (TextFormatINVOrg)this.getComponent("DISPENSE_ORG_CODE")).
//                setEnabled(false);
        }
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("��沿�Ų���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("INV_CODE"))) {
            this.messageBox("���ʴ��벻��Ϊ��");
            return false;
        }
        return true;
    }


    /**
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
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


}
