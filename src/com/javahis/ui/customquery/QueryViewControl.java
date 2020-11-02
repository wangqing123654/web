package com.javahis.ui.customquery;

import java.util.Vector;

import jdo.sys.BuildSqlTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 *
 * <p>Title: ��ѯ��ʾ���ڿ�����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author ehui 20090818
 * @version 1.0
 */
public class QueryViewControl
    extends TControl {
    TComboBox tempCode;
    TTable tempTable, selectTable, table;
    String author;
    TButton set;
    /**
     * ��ʼ��
     */
    public void onInit() {
//    	Object o=this.getParameter();
//    	System.out.println(o.getClass().getName());
//    	Vector vct=(Vector)o;
//    	System.out.println("vct============"+vct);
//    	if(vct!=null&&vct.size()>0){
//    		author=vct.get(1)+"";
//    	}
//
        initComponent();
        initData();
    }

    /**
     * ��ʼ���ؼ�
     */
    private void initComponent() {
        table = (TTable) getComponent("TABLE");
        selectTable = (TTable) getComponent("SELECT_TABLE");
        tempTable = (TTable) getComponent("TEMP_TABLE");

        tempCode = (TComboBox) getComponent("TEMP_COMBO");
        set = (TButton) getComponent("SETBUTTON");
//        if (author == null || author.trim().length() < 1 ||
//            "1".equalsIgnoreCase(author)) {
//            set.setVisible(false);
//        }
    }

    /**
     * ��ʼ������
     */
    private void initData() {
        TParm comboData = BuildSqlTool.getInstance().getTempCodeComboData(
            Operator.getID());
        if (comboData == null || comboData.getErrCode() != 0) {
            this.messageBox_("û������");
            return;
        }
        tempCode.setParmValue(comboData);

        TParm tableParm = BuildSqlTool.getInstance().getTempCodeTableData(
            Operator.getID());
        if (tableParm == null || tableParm.getErrCode() != 0) {
            this.messageBox_("û������");
            return;
        }
        //ģ������,200;�������,100;������,100;������Ա,100;����ʱ��,200;������ĩ,100
        //TEMP_DESC;TABLE_ID;OWNER;USER_NAME;BUILDDATE;OPT_TERM
        tempTable.setParmValue(tableParm);
    }

    /**
     * ģ��TABLE�е���¼�
     */
    public void onClickRow() {
        int row = tempTable.getSelectedRow();
        if (row < 0) {
            this.messageBox_("û������");
            return;
        }
        TParm tempParm = tempTable.getParmValue();
        String tempCodeString = tempParm.getValue("TEMP_CODE", row);
        tempCode.setValue(tempCodeString);
        onTempChange();
    }

    /**
     * COMBO��ѡ�¼�����ʼ��3��TABLE
     */
    public void onTempChange() {
        String tempCodeString = tempCode.getSelectedID();
        if (tempCodeString == null || tempCodeString.trim().length() < 1) {
            onClear();
            return;
        }
        TParm tempTableParm = BuildSqlTool.getInstance().
            getTempCodeTableDataByTempCode(Operator.getID(), tempCodeString);
        if (tempTableParm == null || tempTableParm.getCount() < 1) {
            this.messageBox_("û������");
            return;
        }
        tempTable.setParmValue(tempTableParm);

        String tableId = tempTableParm.getValue("TABLE_ID", 0);
        if (tableId == null || tableId.trim().length() < 1) {
            this.messageBox_("û������");
//            this.messageBox_("2");
            return;
        }
        TParm whereParm = BuildSqlTool.getInstance().getWhereTable(
            tempCodeString, tableId);
        if (whereParm == null || whereParm.getErrCode() < 0) {
            this.messageBox_("û������");
//            this.messageBox_("3");
            return;
        }
        for (int i = 0; i < whereParm.getCount("VALUE"); i++) {
            whereParm.setData("VALUE", i, "");
        }
        selectTable.setParmValue(whereParm);
    }

    /**
     * ����¼�
     */
    public void onClear() {
        initData();
        selectTable.removeRowAll();
        table.removeRowAll();
    }

    public void onQuery() {
    	//selectTable����ʾ
        selectTable.acceptText();
        String tempCodeString = tempCode.getSelectedID();
        if (tempCodeString == null || tempCodeString.trim().length() < 1) {
//            this.messageBox_("1");
            this.messageBox_("û������");
            return;
        }
        TParm column = BuildSqlTool.getInstance().getColumnInfo(tempCodeString);
        if (column == null || column.getErrCode() < 0) {
//            this.messageBox_("2");
            this.messageBox_("û������");
            return;
        }
        String header = BuildSqlTool.getInstance().getColumnHeader(column);
        String parmMap = BuildSqlTool.getInstance().getColumnParmMap(column);
        System.out.println("parmMap===============" + parmMap);
        System.out.println("header===============" + header);
        table.setHeader(header);
        table.setParmMap(parmMap);
        //????
        TParm whereValue = selectTable.getParmValue();
        int row = 0;
        int columnIndex = tempTable.getColumnIndex("TABLE_ID");
        String tableName = tempTable.getValueAt(row, columnIndex) + "";
        System.out.println("WHERE______VALUE=" + whereValue);
        String sql = BuildSqlTool.getInstance().buildSql(tempCodeString,
            whereValue, tableName);
        if (sql == null || sql.trim().length() < 1) {
//            this.messageBox_("4");
            this.messageBox_("û������");
            return;
        }
        //!!!��
        System.out.println("sql===================" + sql);
        TParm data = BuildSqlTool.getInstance().queryBySql(sql);
        if (data == null || data.getErrCode() != 0) {
            this.messageBox_("û������");
            return;
        }
        table.setParmValue(data);
        String tempDesc = tempCode.getSelectedName();
        if ("���ƿ���������ͳ�Ʊ�".equalsIgnoreCase(tempDesc)) {
            header = "����,100;�����ؽ��,100;ҩƷ�ܽ��,100;������/�ܽ��,100";
            parmMap = "DEPTDESC;ANTISUM;TOT_AMT;RATE";
            int count = data.getCount();
            String startDate = selectTable.getValueAt(0, 1) + "";
            String endDate = selectTable.getValueAt(1, 1) + "";
            for (int i = 0; i < count; i++) {
                String deptDesc = data.getValue("DEPTDESC", i);

                TParm antiSum = BuildSqlTool.getInstance().getAntiByDept(
                    deptDesc, startDate, endDate);
                System.out.println("antiSum==" + antiSum);
                long anti = antiSum.getLong("ANTISUM", 0);
                long totAmt = data.getLong("TOT_AMT", i);
                long rate = anti / totAmt;
                double rateD = StringTool.round(Double.longBitsToDouble(rate),
                                                2);
                data.addData("ANTISUM", anti);
                data.addData("RATE", rateD);
            }
            data.setCount(data.getCount("TOT_AMT"));
            table.setHeader(header);
            table.setParmMap(parmMap);
            table.setParmValue(data);
        }
        else if ("����ҩƷ���۶�/�ܽ�������".equalsIgnoreCase(tempDesc)) {
            header = "ҩƷ����,100;�����۶�,100;�����۶�,100;�����۶�/�����۶�,100";
            parmMap = "CATDESC;TOTAMT;ALLAMT;RATE";
            String startDate = selectTable.getValueAt(0, 1) + "";
            String endDate = selectTable.getValueAt(1, 1) + "";
            TParm allAmt = BuildSqlTool.getInstance().getTotAmtByDate(startDate,
                endDate);
            if (allAmt.getErrCode() != 0) {
                this.messageBox_("û������");
                return;
            }
            long all = allAmt.getLong("ALLAMT", 0);
            double allD = Double.longBitsToDouble(all);
            int count = data.getCount();
            System.out.println("parm===========" + data);
            for (int i = 0; i < count; i++) {
                long totAmt = data.getLong("TOTAMT", i);
                double totAmtD = Double.longBitsToDouble(totAmt);
                System.out.println("totAmt==============" + totAmt);
                double rate = totAmtD / allD;
                System.out.println("rate=" + rate);
                rate = StringTool.round(rate, 2);
                data.addData("ALLAMT", all);
                data.addData("RATE", rate);
            }
            data.setCount(data.getCount("TOT_AMT"));
            table.setHeader(header);
            table.setParmMap(parmMap);
            table.setParmValue(data);
        }
        else if ("����ҩƷ���۶�/ͬ�����۶������".equalsIgnoreCase(tempDesc)) {
            header = "ҩƷ����,100;���۶�,100;�����۶�,100;���۶�/�����۶�,100";
            parmMap = "ORDERDESC;TOT_AMT;ALLAMT;RATE";
            String startDate = selectTable.getValueAt(0, 1) + "";
            String endDate = selectTable.getValueAt(1, 1) + "";
            TParm classifyTot = BuildSqlTool.getInstance().getPhaClassifyTotAmt(
                startDate, endDate);
            Vector catCodeVct = classifyTot.getVectorValue("CATCODE");
            System.out.println("classifyTot+" + classifyTot);
            System.out.println("catCodeVct=" + catCodeVct);
            int count = data.getCount();
            for (int i = 0; i < count; i++) {
                String orderCode = data.getValue("ORDERCODE", i);
                orderCode = orderCode.substring(0, 4);
                int rowOfOrderCode = catCodeVct.indexOf(orderCode);
                double allamt = 0.0;
                double totAmt = data.getDouble("TOT_AMT", i);
                double rate = 0.0;
                if (rowOfOrderCode > -1) {
                    allamt = classifyTot.getDouble("TOTAMT", rowOfOrderCode);
                    rate = totAmt / allamt;
                    rate = StringTool.round(rate, 2);
                }
                data.addData("ALLAMT", allamt);
                data.addData("RATE", rate);
            }
            table.setHeader(header);
            table.setParmMap(parmMap);
            table.setParmValue(data);
        }
    }

    /**
     * ������EXCEL
     */
    public void onExportExcel() {
        int count = table.getRowCount();
        if (count < 1) {

            this.messageBox_("û������");
            return;
        }
        int column = tempTable.getColumnIndex("TEMP_DESC");
        String tableName = tempTable.getValueAt(0, column) + "";
        ExportExcelUtil.getInstance().exportExcel(table,
                                                  "C:\\CustomeQuery\\" + tableName);
    }

    /**
     * ɾ��ģ��
     */
    public void onDelete() {
        if (tempTable == null) {
            this.messageBox_("û������");
            return;
        }
        int row = tempTable.getSelectedRow();
        if (row < 0) {
            this.messageBox_("û������");
            return;
        }
        TParm tempParm = tempTable.getParmValue();
        String tempCode = tempParm.getValue("TEMP_CODE", row);
        TParm parm = new TParm();
        parm.setData("TEMP_CODE", tempCode);
        TParm result = TIOM_AppServer.executeAction(
            "action.sys.BuildSqlAction", "onDelete", parm);
        if (result.getErrCode() != 0) {
            System.out.println(result.getErrText());
            this.messageBox_("����ʧ��");
            return;
        }
        this.messageBox_("����ɹ�");
        initData();
    }

    /**
     * �½���ѯģ��
     */
    public void onInsert() {
        TParm inParm = new TParm();
        inParm.setData("TEMP_CODE", "");
        this.openDialog("%ROOT%\\config\\customquery\\BuildSql.x", inParm, false);
        initData();
    }

    /**
     * ����ģ��
     */
    public void onUpdate() {
        if (tempTable == null) {
            this.messageBox_("û������");
            return;
        }
        TParm tempParm = tempTable.getParmValue();
        if (tempParm == null || tempParm.getCount() < 1) {
            this.messageBox_("û������");
            return;
        }

        String tempCodeString = tempCode.getSelectedID();
        int column = tempTable.getColumnIndex("TABLE_ID");
        String tableId = tempTable.getValueAt(0, column) + "";
        TParm inParm = new TParm();
        System.out.println("queryViewControl.tempCode===" + tempCodeString);
        inParm.setData("TEMP_CODE", tempCodeString);
        inParm.setData("TABLE_ID", tableId);
        this.openDialog("%ROOT%\\config\\customquery\\BuildSql.x", inParm, false);
        initData();
    }
    //���˫���¼�
    public void onOK(){
        int rowIndex = table.getSelectedRow();
        TParm data = table.getParmValue();
        TParm re = data.getRow(rowIndex);
        this.setReturnValue(re);
        this.closeWindow();
    }

    public static void main(String args[]) {

        com.javahis.util.JavaHisDebug.TBuilder();
        Operator.setData("tiis", "HIS", "127.0.0.1", "20803", "001");

//        Object parm = com.javahis.util.JavaHisDebug.runDialog("customquery\\QueryView.x",new Object[]{"UDD_SHEET"});
//        System.out.println("parm=" + parm);
    }

}
