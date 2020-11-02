package com.javahis.ui.customquery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import jdo.sys.BuildSqlTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title:����SQL������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author ehui 2009.08.14
 * @version 1.0
 */
public class BuildSqlControl
    extends TControl {
    String tableId, tempCode, sqlString;
    TTextFormat tableName;
    TTable column, select, groupby, orderby, where;
    TTextArea sql;
    /**
     * ��ʼ������
     */
    public void onInitParameter() {
//        Object obj = getParameter();
//        if (obj == null || ! (obj instanceof TParm)) {
//            return;
//        }
//
//        TParm parm = (TParm) obj;
//        tableId = parm.getValue("TABLE_ID");
//        tempCode = parm.getValue("TEMP_CODE");
        
        if (tempCode == null || tempCode.trim().length() < 1) {

            tempCode = StringTool.getString(TJDODBTool.getInstance().getDBTime(),
                                            "yyyyMMddHHmmss") + Operator.getID();
            System.out.println("tempCode::::"+tempCode);
        }
    	
        this.setValue("TEMP_CODE", tempCode);
    }

    /**
     * ���������TABLE_ID,�Ͳ�ѯ���б��ʼ����ЩTABLE
     */
    private void onInitData() {

        tableName.setValue(tableId);
        TParm templateParm = BuildSqlTool.getInstance().getTemplate(tempCode);

        TComboBox columnCombo = new TComboBox();

        columnCombo = new TComboBox();
        columnCombo.setParmValue(templateParm);
        columnCombo.setParmMap("ID:ID;NAME:NAME");
        columnCombo.setShowID(false);
        columnCombo.setShowText(false);
        columnCombo.setShowName(true);
        columnCombo.setTableShowList("NAME");
        orderby.addItem("COLUMN_COMBO", columnCombo);
        groupby.addItem("COLUMN_COMBO", columnCombo);

        columnCombo = new TComboBox();
        columnCombo.setParmMap("ID:ID;NAME:NAME");
        columnCombo.setShowID(false);
        columnCombo.setShowText(false);
        columnCombo.setShowName(true);
        columnCombo.setTableShowList("NAME");
        columnCombo.setStringData("[[ID,NAME],[AND,����],[OR,����]]");
        where.addItem("LINK_COMBO", columnCombo);

        columnCombo = new TComboBox();
        columnCombo.setParmMap("ID:ID;NAME:NAME");
        columnCombo.setShowID(false);
        columnCombo.setShowText(false);
        columnCombo.setShowName(true);
        columnCombo.setTableShowList("NAME");
        columnCombo.setStringData("[[ID,NAME],[=,����],[<,С��],[>,����],[<=,С�ڵ���],[>=,���ڵ���],[<>,������],[LIKE,ƥ��],[IS,����Чֵ],[IS NOT,����Чֵ]]");
        where.addItem("OPERATOR_COMBO", columnCombo);

        columnCombo = new TComboBox();
        columnCombo.setParmMap("ID:ID;NAME:NAME");
        columnCombo.setShowID(false);
        columnCombo.setShowText(false);
        columnCombo.setShowName(true);
        columnCombo.setTableShowList("NAME");
        columnCombo.setStringData("[[id,name],[ASC,˳��],[DESC,����]]");
        orderby.addItem("ORDERBY_DIRECTION", columnCombo);

        if (tableId == null || tableId.trim().length() < 1) {
            this.messageBox_("û������");
            return;
        }
        this.setValue("TEMP_CODE", tempCode);
        this.setValue("TEMP_DESC", templateParm.getValue("TEMP_DESC", 0));
        sql.setText(templateParm.getValue("SQL", 0));

        TParm columnParm = BuildSqlTool.getInstance().getColumn(tableId);
        if (columnParm == null || columnParm.getErrCode() != 0) {
            this.messageBox_("û������");
            return;
        }
        column.setParmValue(columnParm);
        TParm selectParm = BuildSqlTool.getInstance().getSelect(tempCode);
        if (selectParm == null || selectParm.getErrCode() < 0) {
            this.messageBox_("û������");
            return;
        }
        select.setParmValue(selectParm);
        TParm groupbyParm = BuildSqlTool.getInstance().getGroupBy(tempCode);
        if (groupbyParm == null || groupbyParm.getErrCode() < 0) {
            this.messageBox_("û������");
            return;
        }
        groupby.setParmValue(groupbyParm);
        TParm orderbyParm = BuildSqlTool.getInstance().getOrderBy(tempCode);
        if (orderbyParm == null || orderbyParm.getErrCode() < 0) {
            this.messageBox_("û������");
            return;
        }
        orderby.setParmValue(orderbyParm);
        TParm whereParm = BuildSqlTool.getInstance().getWhere(tempCode);
        if (whereParm == null || whereParm.getErrCode() < 0) {
            this.messageBox_("û������");
            return;
        }
        where.setParmValue(whereParm);
    }

    /**
     * ��ʼ��
     */
    public void onInit() {
        initComponent();
        if (tableId != null && tableId.trim().length() > 0) {
            onInitData();
        }
    }

    /**
     * ��ʼ���ؼ�
     */
    private void initComponent() {
        tableName = (TTextFormat) getComponent("TABLE_NAME");

        column = (TTable) getComponent("COLUMN");
        select = (TTable) getComponent("SELECT_TABLE");
        select.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onSum");
        groupby = (TTable) getComponent("GROUPBY_TABLE");
        orderby = (TTable) getComponent("ORDERBY_TABLE");
        where = (TTable) getComponent("WHERE_TABLE");

        addEventListener("WHERE_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                         "onWhereValueChange");

        where.addEventListener("WHERE_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                               "onWhereValueChange");
        where.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                               "onExternalClick");
        sql = (TTextArea) getComponent("SQL");

    }

    /**
     * ���
     */
    public void onClear() {
        select.removeRowAll();
        groupby.removeRowAll();
        orderby.removeRowAll();
        where.removeRowAll();
        TCheckBox isGroupby = (TCheckBox) getComponent("ISGROUPBY");
        isGroupby.setSelected(false);
        TButton button = (TButton) getComponent("GROUPBY_ADD");
        button.setEnabled(false);
        button = (TButton) getComponent("GROUPBY_REMOVE");
        button.setEnabled(false);
        groupby.setEnabled(false);
        sql.setText("");
    }

    /**
     * ���������¼�
     */
    public void onTable() {
        onClear();
        tableId = tableName.getValue() + "";
        TParm parm = BuildSqlTool.getInstance().getColumn(tableName.getValue() +
            "");
        if (parm.getErrCode() < 0) {
            messageBox_(parm.getErrText());
            return;
        }
        column.setParmValue(parm);

        TComboBox columnCombo = new TComboBox();

        columnCombo = new TComboBox();
        columnCombo.setParmValue(parm);
        columnCombo.setParmMap("ID:ID;NAME:NAME");
        columnCombo.setShowID(false);
        columnCombo.setShowText(false);
        columnCombo.setShowName(true);
        columnCombo.setTableShowList("NAME");
        orderby.addItem("COLUMN_COMBO", columnCombo);
        groupby.addItem("COLUMN_COMBO", columnCombo);

        columnCombo = new TComboBox();
        columnCombo.setParmMap("ID:ID;NAME:NAME");
        columnCombo.setShowID(false);
        columnCombo.setShowText(false);
        columnCombo.setShowName(true);
        columnCombo.setTableShowList("NAME");
        columnCombo.setStringData("[[ID,NAME],[AND,����],[OR,����]]");
        where.addItem("LINK_COMBO", columnCombo);

        columnCombo = new TComboBox();
        columnCombo.setParmMap("ID:ID;NAME:NAME");
        columnCombo.setShowID(false);
        columnCombo.setShowText(false);
        columnCombo.setShowName(true);
        columnCombo.setTableShowList("NAME");
        columnCombo.setStringData("[[ID,NAME],[=,����],[<,С��],[>,����],[<=,С�ڵ���],[>=,���ڵ���],[<>,������],[LIKE,ƥ��],[IS,����Чֵ],[IS NOT,����Чֵ]]");
        where.addItem("OPERATOR_COMBO", columnCombo);

        columnCombo = new TComboBox();
        columnCombo.setParmMap("ID:ID;NAME:NAME");
        columnCombo.setShowID(false);
        columnCombo.setShowText(false);
        columnCombo.setShowName(true);
        columnCombo.setTableShowList("NAME");
        columnCombo.setStringData("[[id,name],[ASC,˳��],[DESC,����]]");
        orderby.addItem("ORDERBY_DIRECTION", columnCombo);
    }

    /**
     * ��ѡ��Ҫ�ϼƵ��ֶΣ���û�б�ѡ�е��ֶ�д��GROUPBY_TABLE,ORDERBY_TABLE
     * @param obj
     */
    public boolean onSum(Object obj) {
        TTable table = (TTable) obj;

        TParm parm = table.getParmValue();
        int row = table.getSelectedRow();
        if (row < 0) {
            return true;
        }
        parm.setData("SUM", row, "Y");
        TParm sumParm = new TParm();
        TParm orderParm = new TParm();
        int count = parm.getCount();

        if (parm == null || count < 1) {
            this.messageBox_("null");
            return true;
        }
        int sumParmCount = count;
        int orderParmCount = count;
        for (int i = 0; i < count; i++) {
            if (!parm.getBoolean("SUM", i)) {
                String code = parm.getValue("COLUMN_ID", i);
                String columnDesc = parm.getValue("COLUMN_NAME", i);
                sumParm.addData("COLUMN_ID", code);
                orderParm.addData("COLUMN_ID", code);
                sumParm.addData("COLUMN_NAME", columnDesc);
                orderParm.addData("COLUMN_NAME", columnDesc);
                orderParm.addData("DIRECTION", "ASC");
            }
            else {
                sumParmCount--;
                orderParmCount--;
            }

        }
        sumParm.setCount(sumParmCount);
        orderParm.setCount(orderParmCount);
        groupby.setParmValue(sumParm);
        orderby.setParmValue(orderParm);
        return false;
    }

    /**
     * ����SELECT_TABLE
     */
    public void onSelectAdd() {
        int row = column.getSelectedRow();

        TParm columnParm = column.getParmValue();
        if (columnParm == null || columnParm.getCount() < 1) {
            this.messageBox_("û������");
            return;
        }
        String code = columnParm.getValue("COLUMN_ID", row);

        String name = columnParm.getValue("COLUMN_NAME", row);

        String dataType = columnParm.getValue("DATA_TYPE", row);

        TParm selectParm = select.getParmValue();
        if (selectParm == null)
            selectParm = new TParm();
        selectParm.addData("COLUMN_ID", code);
        selectParm.addData("COLUMN_NAME", name);
        selectParm.addData("DATA_TYPE", dataType);
        selectParm.addData("SUM", "N");
        selectParm.setCount(selectParm.getCount("COLUMN_ID"));
        select.setParmValue(selectParm);

    }

    /**
     * ɾ�� SELECT_TABLE��ֵ��
     * ���ж������TABLE��ĳ��Щ�����Ǻϼ��У����ж�ɾ���ϼ���Ҫɾ��GROUPBY_TABLE��ORDERBY_TABLE�������У����ɾ���Ǻϼ��У���ֻɾ��SELECT_TABLE,GROUPBY_TABLE,ORDERBY_TABLE����
     * ���SELECT_TABLEû�кϼ��У���ֻɾ��SELECT_TABLE��
     *
     */
    public void onSelectRemove() {
        TParm parm = select.getParmValue();
        int count = parm.getCount();
        int row = select.getSelectedRow();
        if (parm == null || count < 1 || row < 0 || row >= count) {
            return;
        }
        int sumRow = isExistSum();
        //û�кϼ���
        if (sumRow < 0) {
            select.removeRow(row);
            if (row >= select.getRowCount() - 1 || select.getRowCount() < 1) {
                return;
            }
            else {
                select.setSelectedRow(row);
            }
            return;
        }
        //�кϼ���
        //Ҫɾ�����Ǻϼ���
        if (row == sumRow) {
            //ɾ��SELECT_TABLE
            select.removeRow(row);
            if (select.getRowCount() > 0) {
                select.setSelectedRow(0);
            }

            groupby.removeRowAll();
            orderby.removeRowAll();
            return;
        }
        //�кϼ���
        //Ҫɾ���Ĳ��Ǻϼ���
        //ɾ��SELECT_TABLE
        int col_column_id = select.getColumnIndex("COLUMN_ID");
        String code = select.getValueAt(row, col_column_id) + "";
        select.removeRow(row);

        if (row >= select.getRowCount() - 1) {
            select.setSelectedRow(select.getRowCount() - 1);
        }
        else {
            select.setSelectedRow(row);
        }

        int groupbyRow = isExist(groupby, "COLUMN_ID", code);
        groupby.removeRow(groupbyRow);
        int orderbyRow = isExist(orderby, "COLUMN_ID", code);
        orderby.removeRow(orderbyRow);
    }

    /**
     * ȫ��ѡ���¼�
     */
    public void onSelectAddAll() {
        if (column.getRowCount() < 1) {
            this.messageBox_("û������");
            return;
        }
        TParm selectParm = new TParm();
        TParm columnParm = column.getParmValue();
        if (columnParm == null || columnParm.getCount() < 1) {
            this.messageBox_("û������");
            return;
        }
        //ȫѡ�¼�ʵ��
        int count = columnParm.getCount();
        for (int i = 0; i < count; i++) {
            selectParm.addData("COLUMN_ID", columnParm.getValue("COLUMN_ID", i));
            selectParm.addData("COLUMN_NAME",
                               columnParm.getValue("COLUMN_NAME", i));
            selectParm.addData("DATA_TYPE", columnParm.getValue("DATA_TYPE", i));
        }
        selectParm.setCount(selectParm.getCount("COLUMN_ID"));
        onClear();
        select.setParmValue(selectParm);
    }

    /**
     * ȫ��ɾ���¼�
     */
    public void onSelectRemoveAll() {
        if (select.getRowCount() < 1) {
            this.messageBox_("û������");
            return;
        }
        onClear();
        select.removeRowAll();
    }

    /**
     * ��Ҫ����checkBox��ѡ�¼�
     */
    public void isGroupby() {
        TCheckBox isGroupby = (TCheckBox)this.getComponent("ISGROUPBY");
        TButton groupbyAdd = (TButton)this.getComponent("GROUPBY_ADD");
        TButton groupbyRemove = (TButton)this.getComponent("GROUPBY_REMOVE");
        boolean isSelected = isGroupby.isSelected();
        TParm groupbyParm = groupby.getParmValue();
        TParm orderbyParm = orderby.getParmValue();
        TParm selectParm = select.getParmValue();
        //fux modify 20120528 start
//      this.messageBox_(isGroupby.isSelected());
        //fux modify 20120528 end
        if (selectParm == null || select.getRowCount() < 1) {
            this.messageBox_("����ѡ��Ҫ��ѯ����Ŀ");
            isGroupby.setSelected(false);
            return;
        }   
        if (isSelected) {
            groupbyAdd.setEnabled(true);
            groupbyRemove.setEnabled(true);
            groupby.setEnabled(true);
            groupbyParm = new TParm();
            orderbyParm = new TParm();
            int count = selectParm.getCount();
            String code = "", name = "";
            for (int i = 0; i < count; i++) {
                code = selectParm.getValue("COLUMN_ID", i);
                name = selectParm.getValue("COLUMN_NAME", i);
                groupbyParm.addData("COLUMN_ID", code);
                groupbyParm.addData("COLUMN_NAME", name);
                orderbyParm.addData("COLUMN_ID", code);
                orderbyParm.addData("COLUMN_NAME", name);
                orderbyParm.addData("DIRECTION", "ASC");
            }
            groupbyParm.setCount(groupbyParm.getCount("COLUMN_ID"));
            orderbyParm.setCount(orderbyParm.getCount("COLUMN_ID"));
            groupby.setParmValue(groupbyParm);
            orderby.setParmValue(orderbyParm);
            return;
        }
        groupby.setParmValue(new TParm());
        groupbyAdd.setEnabled(false);
        groupbyRemove.setEnabled(false);
        groupby.setEnabled(false);
        orderby.setParmValue(new TParm());
    }

    /**
     * ���������¼�,ͬʱ��������
     */
    public void onGroupAdd() {
        TParm groupbyParm = groupby.getParmValue();
        TParm orderbyParm = orderby.getParmValue();
        TParm columnParm = column.getParmValue();
        TParm selectParm = select.getParmValue();
        if (groupbyParm == null)
            groupbyParm = new TParm();
        if (orderbyParm == null)
            orderbyParm = new TParm();
        if (selectParm == null) {
            selectParm = new TParm();
        }
        int row = column.getSelectedRow();
        if (column == null || columnParm.getCount("COLUMN_ID") < 1 ||
            row >= column.getRowCount()) {
            this.messageBox_("�޿�ѡ�ֶ�");
            return;
        }
        if (selectParm.getCount("COLUMN_ID") < 1) {
            this.messageBox_("����ѡ��Ҫ��ѯ����Ŀ");
            return;
        }
        String code = columnParm.getValue("COLUMN_ID", row);
        int existRow = isExist(groupby, "COLUMN_ID", code);
        if (existRow > -1) {
            this.messageBox_("�Ѿ������з��飬�����ظ�����");
            return;
        }

        String name = columnParm.getValue("COLUMN_NAME", row);
        groupbyParm.addData("COLUMN_ID", code);
        groupbyParm.addData("COLUMN_NAME", name);
        groupbyParm.setCount(groupbyParm.getCount("COLUMN_ID"));
        groupby.setParmValue(groupbyParm);

        orderbyParm.addData("COLUMN_ID", code);
        orderbyParm.addData("COLUMN_NAME", name);
        orderbyParm.addData("DIRECTION", "ASC");
        orderbyParm.setCount(orderbyParm.getCount("COLUMN_ID"));
        orderby.setParmValue(orderbyParm);

        if (row == column.getRowCount() - 1) {
            column.setSelectedRow(0);
        }
        else {
            column.setSelectedRow(++row);
        }
    }

    /**
     * ����ɾ���¼������Ҫɾ�����ֶ��Ƿ��ڲ�ѯ�ֶ����У�����У��Ͳ�����ɾ�������ɾ����һ��ɾ�������ֶ�
     */
    public void onGroupbyRemove() {
        TParm groupbyParm = groupby.getParmValue();
//		TParm orderbyParm=orderby.getParmValue();
//		TParm columnParm=column.getParmValue();
//		TParm selectParm=select.getParmValue();
        if (groupbyParm == null || groupbyParm.getCount("COLUMN_ID") < 1) {
            this.messageBox_("û������");
            return;
        }
        int row = groupby.getSelectedRow();
        String code = groupbyParm.getValue("COLUMN_ID", row);
        if (isExist(select, "COLUMN_ID", code) > -1) {
            this.messageBox_("���ֶ���Ҫ��ѯ���ֶ��д��ڣ�����ɾ��");
            return;
        }

        int orderbyRow = isExist(orderby, "COLUMN_ID", code);
        if (orderbyRow < 0) {
            this.messageBox_("ɾ�����������󣬿��ܻᵼ�����ɵ�sql����");
            return;
        }
        groupby.removeRow(row);
        orderby.removeRow(orderbyRow);
    }

    /**
     * ���������¼�
     */
    public void onOrderbyAdd() {
        //���û�кϼ��У����������ӻ�ɾ��������кϼ��У������ӣ�����Ҫ�ڷ�����ϸ�����ӣ���û�кϼ��У�����������
        TParm groupbyParm = groupby.getParmValue();
        TParm orderbyParm = orderby.getParmValue();
        TParm columnParm = column.getParmValue();
        if (orderbyParm == null) {
            orderbyParm = new TParm();
        }
        int row = column.getSelectedRow();
        if (row < 0 || columnParm == null || columnParm.getCount() < 1) {
            return;
        }
        String code = columnParm.getValue("COLUMN_ID", row);
        String name = columnParm.getValue("COLUMN_NAME", row);

        //û�з�����
        if (groupbyParm == null || groupbyParm.getCount() < 1) {
            orderbyParm.addData("COLUMN_ID", code);
            orderbyParm.addData("COLUMN_NAME", name);
            orderbyParm.addData("DIRECTION", "ASC");
            orderbyParm.setCount(orderbyParm.getCount("COLUMN_ID"));
            orderby.setParmValue(orderbyParm);
            return;
        }

        int isExistRow = isExist(select, "COLUMN_ID", code);
        if (isExistRow > -1) {
            orderbyParm.addData("COLUMN_ID", code);
            orderbyParm.addData("COLUMN_NAME", name);
            orderbyParm.addData("DIRECTION", "ASC");
            orderby.setParmValue(orderbyParm);
            return;
        }
        else {
            this.messageBox_("�����Ƿ������ݣ���������");
        }
    }

    /**
     * ����ɾ���¼�
     */
    public void onOrderbyRemove() {
        TParm orderbyParm = orderby.getParmValue();
        if (orderbyParm == null || orderbyParm.getCount() < 0) {
            this.messageBox_("û������");
            return;
        }
        int row = orderby.getSelectedRow();
        orderby.removeRow(row);
        if (orderbyParm.getCount() > 0) {
            orderby.setSelectedRow(0);
        }
    }

    /**
     * ��ѯ����TABLEֵ�ı��¼�
     * @param tNode TTableNode
     * @return
     */
    public boolean onWhereValueChange(Object obj) {
        TTableNode tNode = (TTableNode) obj;
        String columnName = where.getParmMap(tNode.getColumn());
        TParm whereParm = where.getParmValue();
        int row = where.getSelectedRow();
        String value = tNode.getValue() + "";
        //BRACKET_START;COLUMN_ID;MARK;VALUE;BRACKET_END;LINK;EXTERNAL_FLG
        if ("BRACKET_START".equalsIgnoreCase(columnName)) {

            if (!"(".equalsIgnoreCase(value)) {
                this.messageBox_("��������ȷ��ʽ������");
                return true;
            }
            else {
//				whereParm.setData("BRACKET_START",row,value);
//				where.setParmValue(whereParm);
                return false;
            }
        }
        else if ("BRACKET_END".equalsIgnoreCase(columnName)) {
            if (!")".equalsIgnoreCase(value)) {
                this.messageBox_("��������ȷ��ʽ������");
                return true;
            }
            else {
//				whereParm.setData("BRACKET_END",row,value);
//				where.setParmValue(whereParm);
                return false;
            }
        }
        else if ("VALUE".equalsIgnoreCase(columnName)) {
            String dataType = whereParm.getValue("DATA_TYPE", row);
            if ( ("DATE".equalsIgnoreCase(dataType) ||
                  "TIMESTAMP".equalsIgnoreCase(dataType))) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                try {
                    Date dValue = sdf.parse(value);
                    String sValue = StringTool.getString(dValue,
                        "yyyyMMddHHmmss");
                    if (!value.equalsIgnoreCase(sValue)) {
                        this.messageBox_("���ڸ�ʽ��ֵ������������\"20090804115959\"��ֵ");
                        return true;
                    }
                }
                catch (ParseException e) {
                    this.messageBox_("���ڸ�ʽ��ֵ������������\"20090804115959\"��ֵ");
                    e.printStackTrace();
                    return true;
                }

                return false;
            }
        }
//
//		whereParm.setData(columnName,row,value);
//		where.setParmValue(whereParm);

        return false;
    }

    /**
     * �ⲿ����CHECK_BOX��ѡ�¼�
     * @param obj
     * @return
     */
    public boolean onExternalClick(Object obj) {
        TTable table = (TTable) obj;
        TParm parm = table.getParmValue();
        if (parm == null || parm.getCount() < 1) {
            this.messageBox_("û������");
            return true;
        }
        table.acceptText();
        return false;
    }

    /**
     * ��ѯ���������¼�
     */
    public void onWhereAdd() {
        TParm columnParm = column.getParmValue();
        if (columnParm == null || column.getRowCount() < 1) {
            this.messageBox_("û������");
            return;
        }
        if (select.getRowCount() < 1) {
            this.messageBox_("����ѡ��Ҫ��ѯ����Ŀ");
            return;
        }
        TParm whereParm = where.getParmValue();
        if (whereParm == null) {
            whereParm = new TParm();
        }
        int row = column.getSelectedRow();
        String code = columnParm.getValue("COLUMN_ID", row);
        String name = columnParm.getValue("COLUMN_NAME", row);
        String dataType = columnParm.getValue("DATA_TYPE", row);
        whereParm.addData("COLUMN_ID", code);
        whereParm.addData("COLUMN_NAME", name);
        whereParm.addData("DATA_TYPE", dataType);
        //BRACKET_START;COLUMN_ID;MARK;VALUE;BRACKET_END;LINK;EXTERNAL_FLG
        whereParm.addData("BRACKET_START", "");
        whereParm.addData("MARK", "=");
        whereParm.addData("VALUE", "*");
        whereParm.addData("BRACKET_END", "");
        whereParm.addData("LINK", "AND");
        whereParm.addData("EXTERNAL_FLG", "N");
        whereParm.addData("NULL_FLG", "N");
        whereParm.setCount(whereParm.getCount("COLUMN_ID"));
        where.setParmValue(whereParm);
        //fux modify  
        if (row == column.getRowCount() - 1) 
        {
            column.setSelectedRow(0);
        }
        else {
        //fux modify 
            column.setSelectedRow(++row);
        }
    }

    /**
     * ��ѯ����ɾ���¼�
     */
    public void onWhereRemove() {
        int row = where.getSelectedRow();
        if (row < 0 || where.getRowCount() < 0) {
            this.messageBox_("û������");
            return;
        }
        where.removeRow(row);
        if (where.getRowCount() >= 1)
            where.setSelectedRow(0);
    }

    /**
     * �ж�SELECT_TABLE����û�кϼ���,�����򷵻�true,�����򷵻�false
     * @return boolean
     */
    private int isExistSum() {
        TParm parm = select.getParmValue();
        int count = parm.getCount();
        if (parm == null || count < 1) {
            return -1;
        }
        for (int i = 0; i < count; i++) {
            if (parm.getBoolean("SUM", i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * ���ݴ����TABLE��Ҫ��˵�����������˵�ֵ�����ظ���TABLE���Ƿ��и���ֵ����
     * @param table
     * @param columnName
     * @param code
     * @return int �������TABLE���и����е�ֵ���򷵻ظ�ֵ���кţ����û�У��򷵻�-1
     */
    private int isExist(TTable table, String columnName, String code) {

        TParm parm = table.getParmValue();

        if (parm == null || parm.getCount() < 0) {
            return -1;
        }
        if (columnName == null || code == null ||
            columnName.trim().length() < 1 || code.trim().length() < 1) {
            return -1;
        }
        Vector vct = parm.getVectorValue(columnName);
        if (vct == null || vct.size() < 1) {
            return -1;
        }

        return vct.indexOf(code);
    }

    /**
     * ���ɲ���SQL
     */
    public void onBuildSql() {
        boolean isTop = StringTool.getBoolean(this.getValueString("INDEX"));
        String topNo = this.getValueString("INDEX_NO");
        sqlString = BuildSqlTool.getInstance().buildSql(select.getParmValue(),
            groupby.getParmValue(), orderby.getParmValue(), where.getParmValue(),
            tableId, isTop, topNo);
        sql.setText(sqlString);
        

    }

    /**
     * ����SQL�¼�
     */
    public void onTestSql() {
        String sqlString = sql.getText();
        if (sqlString == null || sqlString.trim().length() < 1) {
            sql.setText("�޲���sql");
            return;
        }

        TParm result = new TParm(TJDODBTool.getInstance().select(sqlString));
        if (result.getErrCode() != 0) {
            sql.setText(result.getErrText());
            return;
        }
    }

    /**
     * ����
     */
    public void onSave() {
//		tempCode=StringTool.getString(TJDODBTool.getInstance().getDBTime(), "yyyyMMddHHmmss")+Operator.getID();
        System.out.println("buildSql.tempCode=" + tempCode);
        /**
         * TEMP_CODE    VARCHAR2(20 BYTE),
           USER_ID      VARCHAR2(20 BYTE),
           TABLE_ID     VARCHAR2(20 BYTE),
           TEMP_DESC    VARCHAR2(200 BYTE),
           SQL          NUMBER(3),
           DESCRIPTION  VARCHAR2(200 BYTE),
           OPT_USER     VARCHAR2(20 BYTE)                NOT NULL,
           OPT_DATE     TIMESTAMP(6)                     NOT NULL,
           OPT_TERM     VARCHAR2(20 BYTE)                NOT NULL
         */
        String now = StringTool.getString(TJDODBTool.getInstance().getDBTime(),
                                          "yyyyMMddHHmmss");
//        this.messageBox_(now);
        //SYS_VIEW_TEMPLATE
        TParm template = new TParm();
//		String tempCode=this.getValueString("TEMP_CODE");
//        this.messageBox_(tempCode);
        template.setData("TEMP_CODE", tempCode);
        template.setData("TEMP_DESC", getValue("TEMP_DESC"));
        template.setData("USER_ID", Operator.getID());
        if (sqlString == null || sqlString.trim().length() < 1) {
            boolean isTop = StringTool.getBoolean(this.getValueString("INDEX"));
            String topNo = this.getValueString("INDEX_NO");
            sqlString = BuildSqlTool.getInstance().buildSql(select.getParmValue(),
                groupby.getParmValue(), orderby.getParmValue(),
                where.getParmValue(), tableId, isTop, topNo);
        }
        template.setData("SQL", sqlString);
        template.setData("TABLE_ID", tableId);
        template.setData("OPT_USER", Operator.getID());
        template.setData("OPT_DATE", now);
        template.setData("OPT_TERM", Operator.getIP());
        boolean isTop = StringTool.getBoolean(this.getValue("INDEX") + "");
        if (isTop) {
            template.setData("IS_TOP", "Y");
            template.setData("TOP_NO", this.getValue("INDEX_NO"));
           
        }
        else {
            template.setData("IS_TOP", "N");
            template.setData("TOP_NO", "");
        }

        //SYS_VIEW_COLUMN
        TParm selectParm = select.getParmValue();
        System.out.println("selectParm========" + selectParm);
        if (selectParm == null) {
            selectParm = new TParm();
        }
        else {
            int count = selectParm.getCount("COLUMN_ID");
            for (int i = 0; i < count; i++) {
                selectParm.addData("TEMP_CODE", tempCode);
                selectParm.addData("TABLE_ID", tableId);
                selectParm.addData("SEQ", i);
                selectParm.addData("OPT_USER", Operator.getID());
                selectParm.addData("OPT_DATE", now);
                selectParm.addData("OPT_TERM", Operator.getIP());
            }
            selectParm.setCount(selectParm.getCount("COLUMN_ID"));
        }
        //SYS_VIEW_GROUPBY
        TParm groupbyParm = groupby.getParmValue();
        if (groupbyParm == null) {
            groupbyParm = new TParm();
        }
        else {
            int count = groupbyParm.getCount("COLUMN_ID");
            for (int i = 0; i < count; i++) {
                groupbyParm.addData("TEMP_CODE", tempCode);
                groupbyParm.addData("TABLE_ID", tableId);
                groupbyParm.addData("SEQ", i);
                groupbyParm.addData("OPT_USER", Operator.getID());
                groupbyParm.addData("OPT_DATE", now);
                groupbyParm.addData("OPT_TERM", Operator.getIP());
            }
            groupbyParm.setCount(groupbyParm.getCount("COLUMN_ID"));
        }

        //SYS_VIEW_ORDERBY
        TParm orderbyParm = orderby.getParmValue();
        if (orderbyParm == null) {
            orderbyParm = new TParm();
        }
        else {
            int count = orderbyParm.getCount("COLUMN_ID");
            System.out.println("orderbyParm.count==========" + count);
            for (int i = 0; i < count; i++) {
                orderbyParm.addData("TEMP_CODE", tempCode);
                orderbyParm.addData("TABLE_ID", tableId);
                orderbyParm.addData("SEQ", i);
                orderbyParm.addData("OPT_USER", Operator.getID());
                orderbyParm.addData("OPT_DATE", now);
                orderbyParm.addData("OPT_TERM", Operator.getIP());
            }
            orderbyParm.setCount(orderbyParm.getCount("COLUMN_ID"));
        }
        System.out.println("orderParm===============" + orderbyParm);
        //SYS_VIEW_WHERE_DETAIL
        TParm whereParm = where.getParmValue();
        if (whereParm == null) {
            whereParm = new TParm();
        }
        else {
            int count = whereParm.getCount("COLUMN_ID");
            for (int i = 0; i < count; i++) {
                whereParm.addData("TEMP_CODE", tempCode);
                whereParm.addData("TABLE_ID", tableId);
                whereParm.addData("SEQ", i);
                whereParm.addData("OPT_USER", Operator.getID());
                whereParm.addData("OPT_DATE", now);
                whereParm.addData("OPT_TERM", Operator.getIP());
            } 
            whereParm.setCount(whereParm.getCount("COLUMN_ID"));
        }
        TParm parm = new TParm();
        parm.setData("TEMPLATE", template.getData());
        parm.setData("SELECT", selectParm.getData());
        parm.setData("WHERE", whereParm.getData());
        parm.setData("ORDERBY", orderbyParm.getData());
        parm.setData("GROUPBY", groupbyParm.getData());
        
        TParm result = TIOM_AppServer.executeAction(
            "action.sys.BuildSqlAction", "onSave", parm);
        if (result.getErrCode() != 0) {
            System.out.println(result.getErrText());
            this.messageBox_("����ʧ��");
            return;
        }
        this.messageBox_("����ɹ�");
//        this.closeWindow();
    }

    /**
     * ������ѡ�¼�
     */
    public void onIndex() {
        TCheckBox index = (TCheckBox) getComponent("INDEX");

        TNumberTextField indexNo = (TNumberTextField) getComponent("INDEX_NO");
        if (index.isSelected()) {
            indexNo.setEnabled(true);
            indexNo.setValue(10);
        }
        else {
            indexNo.setEnabled(false);
            indexNo.setValue(0);
        }
    }
}
