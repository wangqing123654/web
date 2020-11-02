package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import jdo.inv.INVSQL;
import com.dongyang.ui.TComboBox;
import jdo.inv.INVPublicTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTextFormat;
import java.sql.Timestamp;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TDS;



/**
 * <p>Title: ���������ϸ</p>
 *
 * <p>Description:���������ϸ </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw 090729
 * @version 1.0
 */
public class INVSuptitemDetallControl
    extends TControl {

    /**
     * ��������
     */
    private TTable table;


    public void onInit() {
        super.onInit();
        //��Ŀ����
        table = (TTable) getComponent("TABLE");
        //��ʼ��table
        iniTable();
        //��ʼ��combo
        iniCombo();
        //���벿�ֲ���
        initValue();
        observer();
    }

    /**
     * ��ӹ۲���
     */
    public void observer() {
        //��������ӹ۲���
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL("SELECT SUPITEM_CODE,SUPITEM_DESC FROM INV_SUPTITEM");
        dataStore.retrieve();
        //��ϸ����ӹ۲���
        TDS tds = (TDS) table.getDataStore();
        tds.addObserver(new INVSuptitemTob(dataStore));
    }

    /**
     * ��ʼ����������
     */
    public void iniCombo() {
        String sql =
            "SELECT DEPT_CODE,DEPT_CHN_DESC,DEPT_ABS_DESC,PY1 FROM SYS_DEPT " +
            "WHERE ACTIVE_FLG='Y' AND FINAL_FLG='Y' ORDER BY SEQ,DEPT_CODE";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));

        TComboBox comboBox = new TComboBox();
        comboBox.setParmMap("id:DEPT_CODE;name:DEPT_CHN_DESC");
        comboBox.setParmValue(parm);
        comboBox.setShowID(true);
        comboBox.setShowName(true);
        comboBox.setExpandWidth(30);
        comboBox.setTableShowList("name");
        table.addItem("DEPT", comboBox);

        TParm parmUser = INVPublicTool.getInstance().getInvOperator();
        comboBox = (TComboBox)this.getComponent("CASHIER_CODE");
        comboBox.setParmValue(parmUser);
        comboBox.updateUI();
        comboBox.setValue(Operator.getID());

        comboBox = new TComboBox();
        comboBox.setParmMap("id:USER_ID;name:USER_NAME;py1:PY1");
        comboBox.setParmValue(parmUser);
        comboBox.setShowID(true);
        comboBox.setShowName(true);
        comboBox.setExpandWidth(30);
        comboBox.setTableShowList("name");
        table.addItem("OPERATOR", comboBox);
    }

    /**
     * ��ʼ��tale
     */
    public void iniTable() {
        table.setSQL(
            "SELECT * FROM INV_SUPTITEMDETAIL WHERE SUP_DETAIL_NO IS NULL");
        table.retrieve();
    }

    /**
     * ���벿�ֳ�ʼ����
     */
    public void initValue() {
        //�����Ա
        TComboBox conBox = (TComboBox)this.getComponent("CASHIER_CODE");
        conBox.setValue(Operator.getID());
        String orgCode = "";
//       //���ҿ���
//      TParm toOrgParm = INVPublicTool.getInstance().getOrgCode("B");
//      if (toOrgParm.getErrCode() >= 0) {
//          orgCode = toOrgParm.getValue("ORG_CODE", 0);
//      }
//      //Ĭ�ϼ��˿���
//      this.setValue("USE_DEPT",orgCode);

        //���ʱ��
        Timestamp date = SystemTool.getInstance().getDate();
        TTextFormat format = (TTextFormat)this.getComponent("SUP_DATE");
        this.setValue("SUP_DATEE", date);
        format.setValue(date);

    }

    public void onSupitemCodeSelected() {
        TTextFormat format = (TTextFormat)this.getComponent("SUPITEM_CODE");
        this.setValue("COST_PRICE", format.getComboValue("COST_PRICE"));
        this.setValue("ADD_PRICE", format.getComboValue("ADD_PRICE"));
    }

    //zhangyong20091204 onChangeQty
    public void onChangeQty() {
        //this.messageBox("");
        double qty = this.getValueDouble("QTY");
        TTextFormat format = (TTextFormat)this.getComponent("SUPITEM_CODE");
        this.setValue("COST_PRICE",
                      Double.parseDouble(format.getComboValue("COST_PRICE").
                                         toString()) * qty);
        this.setValue("ADD_PRICE",
                      Double.parseDouble(format.getComboValue("ADD_PRICE").
                                         toString()) * qty);
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = getQueryData();
        String sql = INVSQL.getInvSuptitemDetallQuerySql(parm);
        table.setSQL(sql);
        table.retrieve();
        table.setDSValue();
    }

    /**
     * ɾ��
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("û��ѡ��ɾ������");
            return;
        }
        table.removeRow(row);
        if (!table.update()) {
            messageBox("ɾ��ʧ��!");
            return;
        }
        messageBox("ɾ���ɹ�!");
        this.onClear();

    }

    /**
     * �õ���ѯsql����
     * @return String
     */
    public TParm getQueryData() {
        TParm parm = new TParm();
        parm.setData("SUP_DETAIL_NO", getValueString("SUP_DETAIL_NO"));
        parm.setData("SUP_DETAIL_SEQ", getValueInt("SUP_DETAIL_SEQ"));
        parm.setData("SUP_DATE", getValue("SUP_DATE"));
        parm.setData("SUP_DATEE", getValue("SUP_DATEE"));
        parm.setData("USE_DEPT", getValueString("USE_DEPT"));
        parm.setData("SUPITEM_CODE", getValueString("SUPITEM_CODE"));
        return parm;
    }

    /**
     * table�ĵ���¼�
     */
    public void onClickedTable() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        //table���������
        TParm tableValue = table.getDataStore().getBuffer(table.getDataStore().
            PRIMARY);
        setTextValue(tableValue.getRow(row));
        setEnableD(false);
    }

    public void setEnableD(boolean boo) {
        this.callFunction("UI|SUP_DETAIL_NO|setEnabled", boo);
    }

    /**
     * �����Ϸ�
     * @param parm TParm
     */
    public void setTextValue(TParm parm) {
        this.setValueForParm("SUP_DETAIL_NO;SUP_DETAIL_SEQ;SUP_DATE;" +
                             "USE_DEPT;SUPITEM_CODE;QTY;COST_PRICE;" +
                             "ADD_PRICE;CASHIER_CODE;DESCRIPTION", parm);
    }

    /**
     * ���
     */
    public void onClear() {
        setEnableD(true);
        this.clearValue("SUP_DETAIL_NO;SUP_DETAIL_SEQ;SUP_DATE;SUP_DATEE" +
                        "USE_DEPT;SUPITEM_CODE;QTY;COST_PRICE;" +
                        "ADD_PRICE;CASHIER_CODE;DESCRIPTION");
        clearTextFormat();
        table.clearSelection();
        initValue();
         table.removeRowAll();
         table.resetModify();
    }

    /**
     * ���textFormat������
     */
    public void clearTextFormat() {
        //����
        this.setValue("USE_DEPT", "");
        this.setText("USE_DEPT", "");
        //��Ŀ
        this.setValue("SUPITEM_CODE", "");
        this.setText("SUPITEM_CODE", "");
    }

    /**
     * ����
     * @return boolean
     */
    public boolean onUpdate() {
    	if(table.getRowCount()==0 ||table ==null){
    		messageBox("û�б�������") ;
    		return false ;
    	}
        int row = table.getSelectedRow();
        boolean saveFlg = false;
        //�����ѡ����Ϊ����
        if (row >= 0) {
            //�����������
            if (!dealUpdate(row, saveFlg))
                return false;
        }
        else {
            //������������
        	for(int i=0;i<table.getRowCount();i++){
        	        //����
        	        if(table.getItemString(i, "SUP_DETAIL_NO").equals("") || 
        	        		table.getItemString(i, "SUP_DETAIL_NO").length()<=0) {
        	        	 String supdetailNo = getSupDetailNo();
             	        if (supdetailNo == null || supdetailNo.length() == 0)
             	            return false;
             	       table.setItem(i, "SUP_DETAIL_NO", supdetailNo);
        	        }
        	  
        	}
//            if (!dealNewData())
//                return false;
        }
        //�������ݷ���
        return saveData();
    }

    /**
     * ���ñ��淽��
     * @return boolean
     */
    public boolean saveData() {
    	
        if (!table.update()) {
            messageBox("����ʧ��!");
            return false;
        }
        messageBox("����ɹ�!");
        table.resetModify();
        table.setDSValue();
        onClear();
        return true;
    }

    /**
     * ��������
     * @return boolean
     */
    public boolean dealNewData() {
        //����
        String useDept = getValueString("USE_DEPT");
        //�������
        if (!checkItemDesc(useDept))
            return false;

        //������Ŀ
        String supItemCode = getValueString("SUPITEM_CODE");
        if (supItemCode == null || supItemCode.length() == 0) {
            messageBox("������Ŀ����Ϊ��!");
            return false;
        }

        //�õ���ɱ��۸�
        double costPrice = getValueDouble("COST_PRICE");
        if (costPrice < 0) {
            messageBox("�ɱ�����С����!");
            return false;
        }

        //����
        int qty = getValueInt("QTY");
        if (qty <= 0) {
            messageBox("�������Ϸ�!");
            return false;
        }

        //�õ����ӽ��
        double addPrice = getValueDouble("ADD_PRICE");
        if (addPrice < 0) {
            messageBox("���ӽ���С����!");
            return false;
        }
        //�շ�Ա
        String casherCode = getValueString("CASHIER_CODE");
        if (casherCode == null || casherCode.length() == 0) {
            messageBox("�շ�Ա����Ϊ��!");
            return false;
        }

        //������������
        return setNewData();
    }

    /**
     * ��˴���
     * @param supItemCode String
     * @return boolean
     */
    public boolean checkSupItemCode(String supItemCode) {
        if (supItemCode == null || supItemCode.length() == 0) {
            messageBox("���벻��Ϊ��");
            return false;
        }
        if (table.getDataStore().exist("SUPITEM_CODE='" + supItemCode + "'")) {
            messageBox("����" + supItemCode + "�ظ�");
            return false;
        }
        return true;    
    }

    /**
     * ������������ 
     * @return boolean
     */
    public boolean setNewData() {
//        String supdetailNo = getSupDetailNo();
//        if (supdetailNo == null || supdetailNo.length() == 0)
//            return false;
        int row = table.addRow();
        //����
//        table.setItem(row, "SUP_DETAIL_NO", supdetailNo);
        //���
        table.setItem(row, "SUP_DETAIL_SEQ", table.getRowCount());
        //ʱ��
        table.setItem(row, "SUP_DATE", SystemTool.getInstance().getDate());
        //�շ�Ա
        table.setItem(row, "CASHIER_CODE", getValueString("CASHIER_CODE"));
        //��Ŀ����
        table.setItem(row, "SUPITEM_CODE", getValueString("SUPITEM_CODE"));
        //����
        table.setItem(row, "USE_DEPT", getValueString("USE_DEPT"));
        //�ɱ�
        table.setItem(row, "COST_PRICE", getValue("COST_PRICE"));
        //���ӷ���
        table.setItem(row, "ADD_PRICE", getValue("ADD_PRICE"));
        //��ע
        table.setItem(row, "DESCRIPTION", getValue("DESCRIPTION"));
        //zhangyong20091028
        table.setItem(row, "QTY", this.getValueInt("QTY"));

        //��ӹ̶�����
        setTableData(row); 
        this.clearValue("SUP_DETAIL_NO;SUP_DETAIL_SEQ;SUP_DATE;SUP_DATEE" +
                "USE_DEPT;SUPITEM_CODE;QTY;COST_PRICE;" +
                "ADD_PRICE;CASHIER_CODE;DESCRIPTION");
        clearTextFormat();
        return true;
    }

    /**
     * ȡ���˱��
     * @return String
     */
    public String getSupDetailNo() {
        //��ⵥ��
        String dispenseNo = SystemTool.getInstance().getSUDispense();
        if (dispenseNo == null || dispenseNo.length() <= 0) {
            messageBox_("ȡ���˱�Ŵ���!");
            return null;
        }
        return dispenseNo;
    }

    /**
     * �����������
     * @param row int
     * @param saveFlg boolean
     * @return boolean
     */
    public boolean dealUpdate(int row, boolean saveFlg) {
        //����
        String useDept = getValueString("USE_DEPT");
        //�������
        if (!checkItemDesc(useDept))
            return false;
        String desc = table.getItemString(row, "USE_DEPT");
        //������޸������
        if (!useDept.equals(desc)) {
            table.setItem(row, "USE_DEPT", useDept);
            //��¼�Ƿ����޸�
            saveFlg = true;
        }
        //������Ŀ
        String supItemCode = getValueString("SUPITEM_CODE");
        if (supItemCode == null || supItemCode.length() == 0) {
            messageBox("������Ŀ����Ϊ��!");
            return false;
        }
        String itemCode = table.getItemString(row, "SUPITEM_CODE");
        if (!itemCode.equals(supItemCode)) {
            table.setItem(row, "SUPITEM_CODE", supItemCode);
            //��¼�Ƿ����޸�
            saveFlg = true;
        }
        //�շ�Ա
        String casherCode = getValueString("CASHIER_CODE");
        if (casherCode == null || casherCode.length() == 0) {
            messageBox("�շ�Ա����Ϊ��!");
            return false;
        }
        if (!casherCode.equals(table.getItemString(row, "CASHIER_CODE"))) {
            table.setItem(row, "CASHIER_CODE", casherCode);
            //��¼�Ƿ����޸�
            saveFlg = true;

        }
        //�õ���ɱ��۸�
        double costPrice = getValueDouble("COST_PRICE");
        if (costPrice < 0) {
            messageBox("�ɱ�����С����!");
            return false;
        }
        double price = table.getItemDouble(row, "COST_PRICE");
        if (costPrice != price) {
            table.setItem(row, "COST_PRICE", costPrice);
            //��¼�Ƿ����޸�
            saveFlg = true;
        }
        //����
        int qty = getValueInt("QTY");
        if (qty <= 0) {
            messageBox("�������Ϸ�!");
            return false;
        }
        if (qty != table.getItemInt(row, "QTY")) {
            table.setItem(row, "QTY", qty);
            //��¼�Ƿ����޸�
            saveFlg = true;
        }
        //�õ����ӽ��
        double addPrice = getValueDouble("ADD_PRICE");
        if (addPrice < 0) {
            messageBox("���ӽ���С����!");
            return false;
        }
        price = table.getItemDouble(row, "ADD_PRICE");
        if (addPrice != price) {
            table.setItem(row, "ADD_PRICE", costPrice);
            //��¼�Ƿ����޸�
            saveFlg = true;
        }
        //����
        String decription = getValueString("DESCRIPTION");
        String decript = table.getItemString(row, "DESCRIPTION");
        //������޸������
        if (!decription.equals(decript)) {
            table.setItem(row, "DESCRIPTION", decription);
            //��¼�Ƿ����޸�
            saveFlg = true;
        }
        //������޸����޸Ĺ̶�����
        if (saveFlg)
            setTableData(row);
        return true;
    }

    /**
     * �޹̶�����
     * @param row int
     */
    public void setTableData(int row) {
        table.setItem(row, "OPT_USER", Operator.getID());
        table.setItem(row, "OPT_DATE", SystemTool.getInstance().getDate());
        table.setItem(row, "OPT_TERM", Operator.getIP());
    }

    /**
     *  �������
     * @param useDept String
     * @return boolean
     */
    public boolean checkItemDesc(String useDept) {
        if (useDept == null || useDept.length() == 0) {
            messageBox("���˿��Ҳ���Ϊ��");
            return false;
        }
        return true;
    }

    //zhangyong20100129 ��Ӵ�ӡ����
    public void onPrint() {
        //zhangyong20110217 �޸Ĵ�ӡ��Ϣ
        int selectRow = table.getSelectedRow();
        if (selectRow < 0) {
            this.messageBox("��ѡ���ӡ��Ϣ");
            return;
        }
        TParm parm = new TParm();
        parm.setData("HOSP_AREA", "TEXT", "������Ӧ�����������˵�");
        parm.setData("SUP_DETAIL_NO", "TEXT", "���˵��ţ� " + getValueString("SUP_DETAIL_NO"));
        TTextFormat use_dept = (TTextFormat)this.getComponent("USE_DEPT");
        parm.setData("USE_DEPT", "TEXT", "�������ң� "+ use_dept.getText());
        parm.setData("SUP_DATE", "TEXT", "����ʱ�䣺 " +
                     table.getDataStore().getItemData(selectRow, "SUP_DATE").
                     toString().substring(0, 19).replace("-", "/"));
        TComboBox operator = (TComboBox)this.getComponent("CASHIER_CODE");
        parm.setData("USER", "TEXT", "�����ˣ� " + operator.getSelectedName());

        String sql =
                " SELECT A.SUP_DETAIL_SEQ, B.SUPITEM_DESC, B.DESCRIPTION AS SPECIFICATION, "
                + " A.COST_PRICE / A.QTY AS OWN_PRICE, A.QTY, '��' AS UNIT_DESC, "
                +
                " A.ADD_PRICE/ A.QTY AS ADD_PRICE , A.COST_PRICE  AS AR_AMT, A.DESCRIPTION "
                + " FROM INV_SUPTITEMDETAIL A, INV_SUPTITEM B "
                + " WHERE A.SUPITEM_CODE = B.SUPITEM_CODE AND A.SUP_DETAIL_NO = '" +
                getValueString("SUP_DETAIL_NO") + "' ";

        TParm parmData = new TParm(TJDODBTool.getInstance().select(sql));
        //System.out.println("sql---"+sql);
        double sum_ar_amt = 0;
        for (int i = 0; i < parmData.getCount("SUP_DETAIL_SEQ"); i++) {
            sum_ar_amt += parmData.getDouble("AR_AMT", i);
        }
        parmData.addData("SUP_DETAIL_SEQ", "�ϼ�:");
        parmData.addData("SUPITEM_DESC", "");
        parmData.addData("SPECIFICATION", "");
        parmData.addData("OWN_PRICE", "");
        parmData.addData("QTY", "");
        parmData.addData("UNIT_DESC", "");
        parmData.addData("ADD_PRICE", "");
        parmData.addData("AR_AMT", sum_ar_amt);
        parmData.addData("DESCRIPTION", "");

        parmData.setCount(parmData.getCount("SUP_DETAIL_SEQ"));
//        parmData.addData("SYSTEM", "COLUMNS", "SUP_DETAIL_SEQ");
//        parmData.addData("SYSTEM", "COLUMNS", "SUPITEM_DESC");
//        parmData.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//        parmData.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
//        parmData.addData("SYSTEM", "COLUMNS", "QTY");
//        parmData.addData("SYSTEM", "COLUMNS", "UNIT_DESC");
//        parmData.addData("SYSTEM", "COLUMNS", "ADD_PRICE");
//        parmData.addData("SYSTEM", "COLUMNS", "AR_AMT");
//        parmData.addData("SYSTEM", "COLUMNS", "DESCRIPTION");

        parm.setData("TABLE", parmData.getData());
        //parm.setData("T1", "SQL", sql);
        //System.out.println("parm===" + parm);
        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVSuptitemDetall.jhw", parm);
    }

}
