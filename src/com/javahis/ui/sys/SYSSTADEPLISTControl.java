package com.javahis.ui.sys;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;

public class SYSSTADEPLISTControl
    extends TControl {

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��TABLE�������
        showTable();
        // ���ð�ť״̬
        callFunction("UI|save|setEnabled", true);
        callFunction("UI|query|setEnabled", true);
        callFunction("UI|delete|setEnabled", false);
        callFunction("UI|clear|setEnabled", true);
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        onFilter();
    }

    /**
     * ���淽��
     */
    public void onSave() {
        String station = getValueString("STATION_CODE");
        String dept = getValueString("DEPT_CODE");
        String costCenter = getValueString("COST_CENTER_CODE");
//        TTable table = getTable("TABLE");
        TComboBox combo = (TComboBox) getComponent("STATION_CODE");
        boolean flg = combo.isEnabled();
//        Timestamp date = StringTool.getTimestamp(new Date());
        if (flg) {
            // �ǿ��ж�
            if ("".equals(station)) {
                this.messageBox("��������Ϊ��");
                return;
            }
            if ("".equals(dept)) {
                this.messageBox("���Ҳ���Ϊ��");
                return;
            }
            // �жϺϷ���
            if (!onCheck(station, dept)) {
                this.messageBox("��������ҵĶ�Ӧ�Ѵ��ڣ��޷�����");
                return;
            }
            //=======pangben modify 20110513 start
//			int row = table.addRow();
//
//			table.setItem(row, "STATION_CODE", station);
//			table.setItem(row, "DEPT_CODE", dept);
//			table.setItem(row, "OPT_USER", Operator.getID());
//			table.setItem(row, "OPT_DATE", date);
//			table.setItem(row, "OPT_TERM", Operator.getIP());
            //=======pangben modify 20110513 stop

            //=======pangben modify 20110513 start
            String insertSQL = "INSERT INTO SYS_STADEP_LIST(STATION_CODE,DEPT_CODE,COST_CENTER_CODE,OPT_USER,OPT_DATE,OPT_TERM) VALUES" +
                "('" + station + "','" + dept + "','" + costCenter + "','" +
                Operator.getID() + "',SYSDATE,'" +
                Operator.getIP() + "')";
            TParm parm = new TParm(TJDODBTool.getInstance().update(
                insertSQL));
//		TDataStore dataStore = table.getDataStore();
//		if (dataStore.isModified()) {
//			table.acceptText();
//			if (!table.update()) {
//				messageBox("E0001");
//				return;
//			}
//			table.setDSValue();
//		}
            if (parm.getErrCode() != 0) {
                messageBox("E0001");
                return;
            }
            //=======pangben modify 20110513 stop
            this.messageBox("P0001");
            onFilter();
        }
        else
            messageBox("�����ݲ������޸�");
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        TTable table = getTable("TABLE");
        int row = table.getTable().getSelectedRow();
        if (row < 0)
            return;
        //========pangben modify 20110513 start
        String deleteSQL = "DELETE FROM SYS_STADEP_LIST WHERE STATION_CODE='" +
            this.getValueString("STATION_CODE") +
            "' AND DEPT_CODE='" + this.getValueString("DEPT_CODE") +
            "'";
        TParm parm = new TParm(TJDODBTool.getInstance().update(deleteSQL));

        if (parm.getErrCode() != 0) {
            this.messageBox("ɾ��ʧ��");
        }
        else {
            this.messageBox("ɾ���ɹ�");
        }
        callFunction("UI|delete|setEnabled", false);
        onFilter();
        //========pangben modify 20110513 stop
    }

    /**
     * ��շ���
     */
    public void onClear() {
        callFunction("UI|STATION_CODE|setEnabled", true);
        callFunction("UI|DEPT_CODE|setEnabled", true);
        clearValue("STATION_CODE;DEPT_CODE;COST_CENTER_CODE");
        onInit();
    }

    /**
     * �������Ҷ��ձ��(TABLE)�����¼�
     */
    public void onTableClicked() {
        // ����ɾ����ť״̬
        if (getTable("TABLE").getSelectedRow() != -1) {
            callFunction("UI|delete|setEnabled", true);
        }
        // �õ���ѡ���е�����
        TParm parm = getTable("TABLE").getParmValue().getRow(getTable("TABLE").
            getSelectedRow());
        // ��UI����ʾѡ���е�����
        setValue("STATION_CODE", parm.getData("STATION_CODE"));
        setValue("DEPT_CODE", parm.getData("DEPT_CODE"));
        setValue("COST_CENTER_CODE", parm.getData("COST_CENTER_CODE"));
        // ����UI�пؼ���״̬
        callFunction("UI|STATION_CODE|setEnabled", false);
        callFunction("UI|DEPT_CODE|setEnabled", false);
    }

    /**
     * ��鲡����Ӧ�����Ƿ��ظ�
     * @param station String
     * @param dept String
     * @return boolean
     */
    public boolean onCheck(String station, String dept) {
        String sql =
            "SELECT STATION_CODE,DEPT_CODE,COST_CENTER_CODE,OPT_USER,OPT_DATE,OPT_TERM FROM SYS_STADEP_LIST "
            + "WHERE STATION_CODE = '"
            + station
            + "' AND DEPT_CODE = '"
            + dept + "'";
        //========pangben modify 20110513 start
        //		TDataStore dataStore = new TDataStore();
        //		dataStore.setSQL(sql);
        //		dataStore.retrieve();

        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        //========pangben modify 20110513 start
        if (parm.getCount("STATION_CODE") > 0)
            return false;

        return true;
    }

    /**
     * ���˲���
     */
    public void onFilter() {
        String station = getValueString("STATION_CODE");
        String dept = getValueString("DEPT_CODE");
        TTable table1 = getTable("TABLE");
        String value = "";
        //=======pangben modify 20110513 start ����������
        if (station.length() != 0)
            value = " AND A.STATION_CODE='" + station + "'";
        if (dept.length() != 0) {
            value = " AND A.DEPT_CODE='" + dept + "'";
        }
        if (station.length() != 0 && dept.length() != 0)
            value = " AND A.STATION_CODE='" + station + "' AND A.DEPT_CODE='" +
                dept
                + "'";

        String region = "";
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            region = " AND B.REGION_CODE ='" + Operator.getRegion() + "'";

        String sql = "SELECT A.STATION_CODE,A.DEPT_CODE,A.COST_CENTER_CODE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM FROM SYS_STADEP_LIST A,SYS_DEPT B WHERE A.DEPT_CODE=B.DEPT_CODE" +
            region + value;
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        table1.setParmValue(parm);
        //=======pangben modify 20110513 stop
        //		table1.setFilter(value);
//		table1.filter();
    }

    /**
     * ���˿���
     */
    public void onFilterDept() {
        String dept = getValueString("DEPT_CODE");
        TTable table1 = getTable("TABLE");
        if (dept.length() != 0) {
            table1.setFilter("DEPT_CODE='" + dept + "'");
            table1.filter();
        }
    }

    /**
     * ��ʼ��TABLE�������
     */
    public void showTable() {
        //=======pangben modify 20110513 start ����������
        String region = "";
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            region = " AND B.REGION_CODE ='" + Operator.getRegion() + "'";
        //=======pangben modify 20110513 stop
        String sql = "SELECT A.STATION_CODE,A.DEPT_CODE,A.COST_CENTER_CODE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM FROM SYS_STADEP_LIST A,SYS_DEPT B WHERE A.DEPT_CODE=B.DEPT_CODE" +
            region;
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//            TDataStore dataStore = new TDataStore();
//            dataStore.setSQL(sql);
//            dataStore.retrieve();
        TTable table = getTable("TABLE");
//            table.setDataStore(dataStore);
//            table.setDSValue();
        table.setParmValue(parm);
    }

    /**
     * �õ�ҳ����Table����
     * @param tag String
     * @return TTable
     */
    private TTable getTable(String tag) {
        return (TTable) callFunction("UI|" + tag + "|getThis");
    }
}
