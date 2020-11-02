package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import jdo.inv.InvPackStockMTool;
import jdo.inv.INVSQL;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.util.Map;
import java.util.HashMap;
import com.dongyang.util.TypeTool;
import java.util.Set;
import java.util.Iterator;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;

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
public class INVTearPackMControl extends TControl{

    // ����
    private TTable tableM;
    // ϸ��
    private TTable tableD;

    public INVTearPackMControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        tableM = getTable("TABLEM");
        tableD = getTable("TABLED");
        // ��������¼�
        addEventListener("TABLEM->" + TTableEvent.CHANGE_VALUE,
                         "onTableMChangeValue");
    }

    /**
     *
     */
    public void onOrgCodeAction() {
        TParm parm = new TParm();
        parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        //parm.setData("STATUS", "1");
//        // ���õ����˵� (��ÿ������������)
//        getTextField("PACK_CODE").setPopupMenuParameter("UD",
//            getConfigParm().newConfig(
//                "%ROOT%\\config\\inv\\INVPackStockMPopup.x"), parm);
//        // ������ܷ���ֵ����
//        getTextField("PACK_CODE").addEventListener(TPopupMenuEvent.
//            RETURN_VALUE, this, "popReturn");
      //��������������ѡ�񴰿�
		((TTextField)getComponent("PACK_CODE")).setPopupMenuParameter("UD",
	            getConfigParm().newConfig(
	                "%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
	    // ������ܷ���ֵ����
		((TTextField)getComponent("PACK_CODE")).addEventListener(TPopupMenuEvent.
	            RETURN_VALUE, this, "popReturn");
    }

    /**
     *
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("��ѡ��Ӧ�Ҳ���");
            getTextField("PACK_CODE").setValue("");
            getTextField("PACK_DESC").setValue("");		//wm2013���
            return;
        }

        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        this.setValue("PACK_CODE", parm.getValue("PACK_CODE"));
        this.setValue("PACK_DESC", parm.getValue("PACK_DESC"));
//        this.setValue("PACK_SEQ_NO", parm.getValue("PACK_SEQ_NO"));
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        if ("".equals(this.getValueString("ORG_CODE")) ||
            "".equals(this.getValueString("PACK_CODE"))) {
            this.messageBox("������Һ����������벻��Ϊ��");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        parm.setData("PACK_CODE", this.getValueString("PACK_CODE"));
 //       parm.setData("PACK_SEQ_NO", this.getValueInt("PACK_SEQ_NO"));
        if (!"".equals(this.getValueString("STATUS"))) {
            parm.setData("STATUS", this.getValueString("STATUS"));
        }

        TParm result = InvPackStockMTool.getInstance().onQueryStockM(parm);
        if (result == null || result.getCount("PACK_CODE") <= 0) {
            this.messageBox("û�в�ѯ����");
            tableM.removeRowAll();
            return;
        }
        tableM.removeRowAll();
        tableM.setParmValue(result);
    }

    /**
     * ���淽��
     */
    public void onSave() {
        tableM.acceptText();
        int tableM_row = tableM.getSelectedRow();
        if (tableM_row < 0) {
            this.messageBox("û�в����Ϣ");
            return;
        }
//        if (!checkData()) {
//            return;
//        }
        TParm parm = new TParm();
        TParm tableMParm = tableM.getParmValue();
        TParm result = new TParm();
        if (tableMParm.getInt("PACK_SEQ_NO", tableM_row) != 0) {
            // �������������Ź���
            getINVPackStockMData(parm);
            getINVPackStockDData(parm);
            getINVStockMData(parm);
            getINVStockDDData(parm);
            result = TIOM_AppServer.executeAction(
                "action.inv.INVTearPackAction", "onINVTearPackA", parm);
        }
        else {
            // ���ư����������Ź���
            getINVPackStockMData(parm);
            getINVPackStockDData(parm);
            getINVStockMData(parm);
            result = TIOM_AppServer.executeAction(
                "action.inv.INVTearPackAction", "onINVTearPackB", parm);
        }
        // �����ж�
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
//        this.onClear();
        this.onQuery();
        tableD.removeRowAll();
    }

    /**
     * �������������
     * @param parm TParm
     * @return TParm
     */
    private TParm getINVPackStockMData(TParm parm) {
        TParm packStockM = new TParm();
        packStockM.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        packStockM.setData("PACK_CODE", this.getValueString("PACK_CODE"));
        
//        packStockM.setData("PACK_SEQ_NO", this.getValueInt("PACK_SEQ_NO"));
        packStockM.setData("PACK_SEQ_NO", tableM.getItemString(tableM.getSelectedRow(), "PACK_SEQ_NO"));
        packStockM.setData("QTY",
                           tableM.getItemDouble(tableM.getSelectedRow(), "QTY"));
        packStockM.setData("OPT_USER", Operator.getID());
        packStockM.setData("OPT_DATE", SystemTool.getInstance().getDate());
        packStockM.setData("OPT_TERM", Operator.getIP());
        
        packStockM.setData("PACK_BATCH_NO", Integer.parseInt(tableM.getItemString(tableM.getSelectedRow(), "PACK_BATCH_NO")));
        
        parm.setData("INV_PACKSTOCKM", packStockM.getData());
        return parm;
    }

    /**
     * �����������ϸ
     * @param parm TParm
     * @return TParm
     */
    private TParm getINVPackStockDData(TParm parm) {
        TParm packStockD = new TParm();
        TParm tableDParm = tableD.getParmValue();
        for (int i = 0; i < tableDParm.getCount("INV_CODE"); i++) {
            packStockD.addData("ORG_CODE", this.getValueString("ORG_CODE"));
            packStockD.addData("PACK_CODE", this.getValueString("PACK_CODE"));
            
 //           packStockD.addData("PACK_SEQ_NO", this.getValueInt("PACK_SEQ_NO"));
            
            packStockD.addData("PACK_SEQ_NO", tableM.getItemString(tableM.getSelectedRow(), "PACK_SEQ_NO"));
            
            packStockD.addData("INV_CODE", tableDParm.getValue("INV_CODE", i));
            packStockD.addData("INVSEQ_NO", tableDParm.getInt("INVSEQ_NO", i));
            packStockD.addData("QTY", tableDParm.getDouble("QTY", i));
            packStockD.addData("OPT_USER", Operator.getID());
            packStockD.addData("OPT_DATE", SystemTool.getInstance().getDate());
            packStockD.addData("OPT_TERM", Operator.getIP());
            
            packStockD.addData("PACK_BATCH_NO", Integer.parseInt(tableM.getItemString(tableM.getSelectedRow(), "PACK_BATCH_NO")));
            
        }
        parm.setData("INV_PACKSTOCKD", packStockD.getData());
        return parm;
    }

    /**
     * ���ʿ����������ϸ
     * @param parm TParm
     * @return TParm
     */
    private TParm getINVStockMData(TParm parm) {
        String status = tableM.getItemString(tableM.getSelectedRow(), "STATUS");
        TParm stockM = new TParm();
        TParm tableDParm = tableD.getParmValue();
        Map map = new HashMap();
        String key = "";
        for (int i = 0; i < tableDParm.getCount("INV_CODE"); i++) {
            if (!("0".equals(status)) &&	//	"1".equals(status)
                "Y".equals(tableDParm.getValue("ONCE_USE_FLG", i))) {
                continue;
            }
            key =  this.getValueString("ORG_CODE") + ":" +
                tableDParm.getValue("INV_CODE", i);
            if (map.containsKey(key)) {
                String[] value = map.get(key).toString().split(":");
                double qty = tableDParm.getDouble("QTY", i) +
                    TypeTool.getDouble(value[2]);
                map.remove(key);
                map.put(key, key + ":" + qty);
            }
            else {
                map.put(key, key + ":" + tableDParm.getDouble("QTY", i));
            }
        }
        Set set = map.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            String[] value = map.get(TypeTool.getString(iterator.next())).toString().
                split(":");
            stockM.addData("ORG_CODE", value[0]);
            stockM.addData("INV_CODE", value[1]);
            stockM.addData("STOCK_QTY", TypeTool.getDouble(value[2]));
            stockM.addData("OPT_USER", Operator.getID());
            stockM.addData("OPT_DATE", SystemTool.getInstance().getDate());
            stockM.addData("OPT_TERM", Operator.getIP());
        }
        parm.setData("INV_STOCKM", stockM.getData());
        return parm;
    }

    /**
     * ������ʿ���嵥
     * @param parm TParm
     * @return TParm
     */
    private TParm getINVStockDDData(TParm parm) {
        TParm stockDD = new TParm();
        TParm tableDParm = tableD.getParmValue();
        for (int i = 0; i < tableDParm.getCount("INV_CODE"); i++) {
            if (tableDParm.getInt("INVSEQ_NO", i) == 0) {
                continue;
            }
            stockDD.addData("INV_CODE", tableDParm.getValue("INV_CODE", i));
            stockDD.addData("INVSEQ_NO", tableDParm.getInt("INVSEQ_NO", i));
            stockDD.addData("PACK_CODE", "");
            stockDD.addData("PACK_SEQ_NO", 0);
            stockDD.addData("PACK_FLG", "N");
            stockDD.addData("OPT_USER", Operator.getID());
            stockDD.addData("OPT_DATE", SystemTool.getInstance().getDate());
            stockDD.addData("OPT_TERM", Operator.getIP());
        }
        parm.setData("INV_STOCKDD", stockDD.getData());
        return parm;
    }

//    /**
//     * ��˿��
//     * @return boolean
//     */
//    private boolean checkData() {
//        String org_code = this.getValueString("ORG_CODE");
//        String pack_code = this.getValueString("PACK_CODE");
//        int pack_seq_no = this.getValueInt("PACK_SEQ_NO");
//        TParm parm = new TParm(TJDODBTool.getInstance().select(INVSQL.
//            getINVPackStockMQty(org_code, pack_code, pack_seq_no)));
//        if (parm.getDouble("STOCK_QTY", 0) <
//            tableM.getItemDouble(tableM.getSelectedRow(), "QTY")) {
//            this.messageBox("��������治��");
//            return false;
//        }
//        return true;
//    }

    /**
     * ���ֵ�ı��¼�
     *
     * @param obj
     *            Object
     */
    public boolean onTableMChangeValue(Object obj) {
        tableM.acceptText();
        // ֵ�ı�ĵ�Ԫ��
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // �ж����ݸı�
        if (node.getValue().equals(node.getOldValue()))
            return true;
        int column = node.getColumn();
        if (column == 2) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("�����������С�ڻ����0");
                return true;
            }
            double invseq_no = tableM.getItemDouble(tableM.getSelectedRow(),
                                                    "PACK_SEQ_NO");
            if (invseq_no != 0 && qty != 1) {
                this.messageBox("������������������ܴ���1");
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * ��շ���
     */
    public void onClear() {
        onOrgCodeAction();
        this.clearValue("ORG_CODE;PACK_CODE;PACK_SEQ_NO;PACK_DESC;STATUS");
        tableM.removeRowAll();
        tableD.removeRowAll();
    }

    /**
     * �������¼�
     */
    public void onTableMClicked() {
        TParm parm = tableM.getParmValue().getRow(tableM.getSelectedRow());
        String sql = INVSQL.getINVPackStockDInfo(
            getValueString("ORG_CODE"), parm.getValue("PACK_CODE"),
            parm.getInt("PACK_SEQ_NO"), parm.getDouble("QTY"), parm.getInt("PACK_BATCH_NO"));
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        tableD.setParmValue(result);
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
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

}
