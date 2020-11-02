package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTreeNode;
import jdo.sys.SYSRuleTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TMessage;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TCM_Transform;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSPublic;
import jdo.sys.SYSSQL;

/**
 *
 * <p>Title: ��ʿվ</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author fudw 2008-12-24
 * @version 1.0
 */
public class SYSStationControl
    extends TControl {
    /**
     * ��ʼ���õ���ǰ��ʾ�Ļ�ʿվ����
     */
    TTable tableNow;
    /**
     * ����
     */
    private TTreeNode treeRoot;
    /**
     * ѡ�е����ڵ�
     */
    private TTreeNode selectTreeNode;
    /**
     * ��Ź�����𹤾�
     */
    SYSRuleTool ruleTool = new SYSRuleTool("SYS_STATION");
    /**
     * �������ݷ���datastore���ڶ��������ݹ���
     */
    TDataStore treeDataStore = new TDataStore();
    /**
     * ��ʿվ
     */
    TTable tableStation;
    /**
     * ����
     */
    TTable tableRoom;
    /**
     * ��
     */
    TTable tableBed;
    /**
     * ��
     */
    TTree tree;
    /**
     * ҳǩ
     */
    TTabbedPane tablePane;
    /**
     * ��ʼ��
     */
    public void onInit() { //��ʼ������
        super.onInit();
        //��tree��Ӽ����¼�
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        //��Table��������¼�
        addEventListener("TABLESTATION->" + TTableEvent.CHANGE_VALUE,
                         "onTableStationChangeValue");
        addEventListener("TABLEROOM->" + TTableEvent.CHANGE_VALUE,
                         "onTableRoomChangeValue");
        addEventListener("TABLEBED->" + TTableEvent.CHANGE_VALUE,
                         "onTableBedChangeValue");
        tableStation = (TTable)this.getComponent("TABLESTATION");
        //===========pangben modify 20110422 start ����������
        tableStation.getDataStore().setSQL(SYSSQL.getSYSStation(Operator.getRegion()));
        //===========pangben modify 20110422 stop
        tableStation.getDataStore().retrieve();
        tableStation.setDataStore(tableStation.getDataStore());
        tableStation.setDSValue();

        tableRoom = (TTable)this.getComponent("TABLEROOM");
        //===========pangben modify 20110422 start ����������
        tableRoom.getDataStore().setSQL(SYSSQL.getSYSRoom(Operator.getRegion()));
        //===========pangben modify 20110422 stop
        tableRoom.getDataStore().retrieve();
        tableRoom.setDataStore(tableRoom.getDataStore());
        tableRoom.setDSValue();

        tableBed = (TTable)this.getComponent("TABLEBED");
         //===========pangben modify 20110422 start ����������
        tableBed.getDataStore().setSQL(SYSSQL.getSYSBed(Operator.getRegion()));
        //===========pangben modify 20110422 stop
        tableBed.getDataStore().retrieve();
        tableBed.setDataStore(tableBed.getDataStore());
        tableBed.setDSValue();

        tree = (TTree)this.getComponent("TREE");
        treeRoot = tree.getRoot();
        tablePane = (TTabbedPane)this.getComponent("TABBEDPANLE");
        //��ʼ����
        onInitSelectTree();
        //����
        onCreatTree();
        tableNow = tableStation;
        //
        getTreeLoyar();
        iniComBox();
    }

    /**
     * ��չ����combox��ֵ
     */
    public void getTreeLoyar() {
        TComboBox comBox = (TComboBox)this.callFunction("UI|TREELOYAR|getThis");
        //�����ݸ���combo
        comBox.setParmValue(SYSPublic.getTreeLoyar(treeRoot));
        //ˢ�ֽ����ϵ�combo
        comBox.updateUI();
    }

    /**
     * չ������Ӧ�Ĳ�
     */
    public void onExtendToLoyar() {
        tree.expandLayer(getValueInt("TREELOYAR") - 1);
        tree.collapseLayer(getValueInt("TREELOYAR"));
    }

    /**
     * ��ʼ����
     */
    public void onInitSelectTree() {
        //�õ�����
        treeRoot = tree.getRoot();
        if (treeRoot == null)
            return;
        //�����ڵ����������ʾ
        treeRoot.setText("��ʿվ");
        //�����ڵ㸳tag
        treeRoot.setType("Station");
        //���ø��ڵ��id
        treeRoot.setID("");
        //������нڵ������
        treeRoot.removeAllChildren();
        //���������ʼ������
        callMessage("UI|TREE|update");
    }

    /**
     * ����combox
     */
    public void iniComBox() {
        TParm parm = new TParm();
        //��ʿվparm
        tableStation.acceptText();
        TDataStore dateStore = tableStation.getDataStore();
        String name = dateStore.isFilter() ? dateStore.FILTER :
            dateStore.PRIMARY;
        parm = dateStore.getBuffer(name);
        TComboBox comboBox = new TComboBox();
        comboBox.setParmMap("id:STATION_CODE;name:STATION_DESC");
        comboBox.setParmValue(parm);
        comboBox.setShowID(true);
        comboBox.setShowName(true);
        comboBox.setExpandWidth(30);
        comboBox.setTableShowList("name");
        tableBed.addItem("STATION", comboBox);
        tableRoom.addItem("STATION", comboBox);

        //����
        dateStore = tableRoom.getDataStore();
        name = dateStore.isFilter() ? dateStore.FILTER :
            dateStore.PRIMARY;
        parm = dateStore.getBuffer(name);
        comboBox = new TComboBox();
        comboBox.setParmMap("id:ROOM_CODE;name:ROOM_DESC");
        comboBox.setParmValue(parm);
        comboBox.setShowID(true);
        comboBox.setShowName(true);
        comboBox.setExpandWidth(30);
        comboBox.setTableShowList("name");
        tableBed.addItem("ROOM", comboBox);

    }

    /**
     * ����������
     * @param parentNode TTreeNode
     */
    public void onCreatTree() {
        //�õ�table����
        tableNow = tableStation;
        //table�������иı�ֵ
        tableNow.acceptText();
        //�õ���ʿվ��datastore
        TDataStore dataStoreStation = tableNow.getDataStore();
        //�õ���������
        int stationCount = dataStoreStation.rowCount();
        //ѭ��������ڵ�
        for (int i = 0; i < stationCount; i++) {
            String id = dataStoreStation.getItemString(i, "STATION_CODE");
            String name = dataStoreStation.getItemString(i, "STATION_DESC");
            String type = "Room";
            TTreeNode node = new TTreeNode(name, type);
            node.setID(id);
            treeRoot.add(node);
            //���÷���ڵ�
            CcratRoomTree(node);
        }
        //�õ������ϵ�������
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        //������
        tree.update();
        //��������Ĭ��ѡ�нڵ�
        tree.setSelectNode(treeRoot);
        selectTreeNode = treeRoot;

        tableStation.setFilter("");
        tableStation.filter();
        tableStation.setDSValue();

        tableRoom.setFilter("");
        tableRoom.filter();
        tableRoom.setDSValue();

    }

    /**
     * ��ʼ�����Ϸ���ڵ�
     * @param parentNode TTreeNode
     */
    public void CcratRoomTree(TTreeNode parentNode) {
        //�鿴����Ľڵ�
        if (parentNode == null)
            return;
        //table�������иı�ֵ
        tableRoom.acceptText();
        //�õ���datastore
        TDataStore dataStoreRoom = tableRoom.getDataStore();
        String value = "STATION_CODE='" + parentNode.getID() + "'";
        dataStoreRoom.setFilter(value);
        dataStoreRoom.filter();
        //�õ���������
        int stationCount = dataStoreRoom.rowCount();
        //ѭ��������ڵ�
        for (int i = 0; i < stationCount; i++) {
            String id = dataStoreRoom.getItemString(i, "ROOM_CODE");
            String name = dataStoreRoom.getItemString(i, "ROOM_DESC");
            String type = "Bed";
            TTreeNode node = new TTreeNode(name, type);
            node.setID(id);
            parentNode.add(node);
        }
    }

    /**
     * ���ĵ���¼�
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
        //�õ�������Ľڵ����
        TTreeNode node = (TTreeNode) parm;
        if (node == null)
            return;
        if (tableNow != null)
            tableNow.acceptText();
        selectTreeNode = node;
        //����ѡ�нڵ������
        String type = node.getType();
        //�洢��������
        String value = "";
        //ѡ�е����ڵ��ID
        String id = node.getID();
        //���ѡ�и��ڵ�
        if (type.equals("Station")) {
            //�õ���ʿվ��table
            tableNow = tableStation;
            tableFilter(tableRoom, "");
            tableFilter(tableBed, "");
        }
        //���ѡ�л�ʿվ�ڵ�
        if (type.equals("Room")) {
            //�õ������table
            tableNow = tableRoom;
            value = "STATION_CODE='" + id + "'";
            tableFilter(tableRoom, value);
            tableFilter(tableBed, value);

        }
        //����õ����Ƿ���ڵ�
        if (type.equals("Bed")) {
            //�õ���λ��table
            tableNow = tableBed;
            //�õ����ڵ����
            TTreeNode parern = (TTreeNode) node.getParent();
            value = "ROOM_CODE='" + id + "' AND STATION_CODE='" + parern.getID() +
                "'";
            tableFilter(tableBed, value);

        }
    }

    /**
     * ����table
     * @param table TTable
     * @param filterSql String
     */
    public void tableFilter(TTable table, String filterSql) {
        table.setFilter(filterSql);
        table.filter();
    }

    /**
     * ��ʿվ˫���¼�
     */
    public void onTableStationDoubleClicked() {
        //��������ѡ�нڵ�
        tree.setSelectNode(treeRoot);
        tree.update();

    }

    /**
     * ����˫���¼�
     */
    public void onTableRoomDoubleClicked() {
        int row = tableRoom.getSelectedRow();
        String stationCode = tableRoom.getItemString(row, "STATION_CODE");
        TTreeNode node = treeRoot.findNodeForID(stationCode);
        if (node == null)
            return;
        //��������ѡ�нڵ�
        tree.setSelectNode(node);
        tree.update();

    }

    /**
     * ��˫���¼�
     */
    public void onTableBedDoubleClicked() {
        int row = tableBed.getSelectedRow();
        String roomCode = tableBed.getItemString(row, "ROOM_CODE");
        TTreeNode node = treeRoot.findNodeForID(roomCode);
        if (node == null)
            return;
        //��������ѡ�нڵ�
        tree.setSelectNode(node);
        tree.update();

    }

    /**
     * ���Ի�ʿվtable�ı�ֵ
     * @param obj Object
     */
    public boolean onTableStationChangeValue(Object obj) {
        //�õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        //����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
        if (node.getValue().equals(node.getOldValue()))
            return false;
        //�õ�table�ϵ�parmmap������
        String columnName = node.getTable().getDataStoreColumnName(node.
            getColumn());
        //�õ���ǰ�ı�������
        String value = "" + node.getValue();
        int row = node.getRow();
        //�õ���ǰѡ�е����Ͻڵ�
        String id = node.getTable().getItemString(row, 0);
        TTreeNode newTree = selectTreeNode.findChildNodeForID(id);
        //�����ʿվ���Ƹı��ƴ��
        if ("STATION_DESC".equals(columnName)) {
            //�����ƾ͸����ڵ�
            if (newTree != null) {
                newTree.setText(value);
                tree.update();
            }
            //��ƴ��
            String py = TMessage.getPy(value);
            node.getTable().setItem(row, "PY1", py);
            return false;
        }
        //����ǻ�ʿվ�������
        if ("STATION_CODE".equals(columnName)) {

            //����Ϊ��
            if (value.equals("") || value == null) {
                messageBox_("���벻��Ϊ��!");
                return true;
            }
            //�����ظ�
            if (node.getTable().getDataStore().exist("STATION_CODE='" +
                value +
                "'")) {
                messageBox_("���" + node.getValue() + "�ظ�!");
                return true;
            }
            //�õ���ǰ����볤��
            int lengthStation = ruleTool.getClassNumber(1);
            if (lengthStation != value.length()) {
                messageBox_("���" + node.getValue() + "���Ȳ�����" + lengthStation +
                            "!");
                return true;
            }
            //�Ĵ���Ҳ����
            if (newTree != null)
                newTree.setID(value);
            String oldValue = node.getOldValue().toString();
            //�Է����еĻ�ʿվ����ͷ���Ž��и���
            TTable tableRoom = (TTable) callFunction("UI|TABLEROOM|getThis");
            TDataStore dataStoreRoom = tableRoom.getDataStore();
            if (dataStoreRoom.isFilter()) {
                tableRoom.setFilter("");
                tableRoom.filter();
            }
            //�Է����еĻ�ʿվ��������޸�
            int countRoom = dataStoreRoom.rowCount();
            for (int i = 0; i < countRoom; i++) {
                //�õ���ʿվ����
                String codeStation = TCM_Transform.getString(dataStoreRoom.
                    getItemData(
                        i, "STATION_CODE"));
                //�õ��ϵķ����
                String codeRoom = TCM_Transform.getString(dataStoreRoom.
                    getItemData(
                        i, "ROOM_CODE"));
                if (codeStation.equals(oldValue)) {
                    //����ʿվ����ֵ
                    dataStoreRoom.setItem(i, "STATION_CODE", value);
                    //�����丳��ֵ
                    String room = codeRoom.substring(value.length()); //�з����
                    //���·����
                    dataStoreRoom.setItem(i, "ROOM_CODE", value + room);
                    //�����Ϸ���Ų������ϵĽڵ�
                    TTreeNode treenode1 = newTree.findNodeForID(codeRoom);
                    if (treenode1 != null)
                        treenode1.setID(value + room);
                }
            }
            //�Դ����Ļ�ʿվ������и���
            TTable tableBed = (TTable) callFunction("UI|TABLEBED|getThis");
            TDataStore dataStoreBed = tableBed.getDataStore();
            if (dataStoreBed.isFilter()) {
                tableBed.setFilter("");
                tableBed.filter();
            }
            int countBed = dataStoreBed.rowCount();
            for (int i = 0; i < countBed; i++) {
                //�õ��ϵĻ�ʿվ��
                String codeStation = TCM_Transform.getString(dataStoreBed.
                    getItemData(
                        i, "STATION_CODE"));
                //�õ��ϵķ����
                String codeRoom = TCM_Transform.getString(dataStoreBed.
                    getItemData(
                        i, "ROOM_CODE"));
                //�õ��ϵĴ���
                String codeBed = TCM_Transform.getString(dataStoreBed.
                    getItemData(
                        i, "BED_NO"));
                if (codeStation.equals(oldValue)) {
                    //���»�ʿվ
                    dataStoreBed.setItem(i, "STATION_CODE", value);
                    //�����丳��ֵ
                    String room = codeRoom.substring(value.length()); //�з����
                    //���·����
                    dataStoreBed.setItem(i, "ROOM_CODE", value + room);
                    //�����丳��ֵ
                    String Bed = codeBed.substring(value.length()); //�д���
                    //���·����
                    dataStoreBed.setItem(i, "BED_NO", value + Bed);
                }
            }
            return false;
        }
        tree.update();
        return false;
    }

    /**
     * ���Է���table�ı�ֵ
     * @param obj Object
     */
    public boolean onTableRoomChangeValue(Object obj) {
        //�õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        //����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
        if (node.getValue().equals(node.getOldValue()))
            return false;
        //�õ�table�ϵ�parmmap������
        String columnName = node.getTable().getDataStoreColumnName(node.
            getColumn());
        //�õ���ǰ�ı�������
        String value = "" + node.getValue();
        int row = node.getRow();
        //�õ���ǰѡ�е����Ͻڵ�
        String id = node.getTable().getItemString(row, 0);
        TTreeNode newTree = selectTreeNode.findChildNodeForID(id);
        //����������Ƹı��ƴ��
        if ("ROOM_DESC".equals(columnName)) {
            //�����ƾ͸����ڵ�
            if (newTree != null) {
                newTree.setText(value);
                tree.update();
            }
            //��ƴ��
            String py = TMessage.getPy(value);
            node.getTable().setItem(node.getRow(), "PY1", py);
            return false;
        }
        //����ű仯Ҫ����ظ��Ϳ�
        if ("ROOM_CODE".equals(columnName)) {
            //����Ϊ��
            if (value.equals("") || value == null) {
                messageBox_("���벻��Ϊ��!");
                return true;
            }
            if (node.getTable().getDataStore().exist("ROOM_CODE='" + value +
                "'")) {
                messageBox_("���" + node.getValue() + "�ظ�!");
                return true;
            }
            //�õ���ǰ����볤��
            int length = ruleTool.getClassNumber(2);
            if (length != value.length()) {
                messageBox_("���" + node.getValue() + "���Ȳ�����" + length + "!");
                return true;
            }
            //�Ĵ���Ҳ����
            if (newTree != null)
                newTree.setID(value);
            String oldValue = node.getOldValue().toString();
            //�Դ����Ļ�ʿվ������и���
            TTable tableBed = (TTable) callFunction("UI|TABLEBED|getThis");
            TDataStore dataStoreBed = tableBed.getDataStore();
            if (dataStoreBed.isFilter()) {
                tableBed.setFilter("");
                tableBed.filter();
            }
            int countBed = dataStoreBed.rowCount();
            for (int i = 0; i < countBed; i++) {
                //���Ϸ����
                String codeRoom = TCM_Transform.getString(dataStoreBed.
                    getItemData(
                        i, "ROOM_CODE"));
                //���ϴ���
                String codeBed = TCM_Transform.getString(dataStoreBed.
                    getItemData(
                        i, "BED_NO"));
                if (codeRoom.equals(oldValue)) {
                    //���·���
                    dataStoreBed.setItem(i, "ROOM_CODE", value);
                    //���´�
                    String Bed = codeBed.substring(value.length()); //�д���
                    //���·����
                    dataStoreBed.setItem(i, "BED_NO", value + Bed);
                }
            }
            return false;
        }
        tree.update();
        return false;
    }

    /**
     * ���Դ�λtable�ı�ֵ
     * @param obj Object
     */
    public boolean onTableBedChangeValue(Object obj) {
        //�õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        //����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
        if (node.getValue().equals(node.getOldValue()))
            return false;
        //�õ�table�ϵ�parmmap������
        String columnName = node.getTable().getDataStoreColumnName(node.
            getColumn());
        //�õ���ǰ�ı�������
        String value = "" + node.getValue();
        if ("BED_NO_DESC".equals(columnName)) {
            //��ƴ��
            String py = TMessage.getPy(value);
            node.getTable().setItem(node.getRow(), "PY1", py);
            return false;
        }

        //���ű仯
        if ("BED_NO".equals(columnName)) {
            //����Ϊ��
            if (value.equals("") || value == null) {
                messageBox_("���벻��Ϊ��!");
                return true;
            }
            if (node.getTable().getDataStore().exist("BED_NO='" + value +
                "'")) {
                messageBox_("���" + node.getValue() + "�ظ�!");
                return true;
            }
            //�õ���ǰ����볤��
            int length = ruleTool.getTot() + ruleTool.getSerial();
            if (length != value.length()) {
                messageBox_("���" + node.getValue() + "���Ȳ�����" + length + "!");
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * ��������
     */
    public void onNew() {
        //�����ı�
        if (tableNow != null)
            tableNow.acceptText();
        //�ҵ�ѡ�е����ڵ�
        TTreeNode node = tree.getSelectionNode();
        //���û�ҵ���ǰѡ�еĽڵ�
        if (node == null)
            return;
        //��˵�ǰѡ�е�Χ��
        String type = node.getType();
        //���ѡ�и��ڵ�
        if (type.equals("Station")) {
            //����������ʿվ�ķ���
            tablePane.setSelectedIndex(0);
            stationNew(node);
            iniComBox();
        }
        //���ѡ�л�ʿվ�ڵ�
        if (type.equals("Room")) {
            //������������ķ���
            tablePane.setSelectedIndex(1);
            roomNew(node.getID(), node);
            iniComBox();
        }
        //����õ����Ƿ���ڵ�
        if (type.equals("Bed")) {
            tablePane.setSelectedIndex(2);
            TTreeNode parentNode = (TTreeNode) node.getParent();
            //����������λ�ķ���
            bedNew(node.getID(), parentNode.getID());
        }
    }

    /**
     * ������ʿվ
     */
    public void stationNew(TTreeNode node) {

        String stationMaxCode = getMaxCode(tableStation.
                                           getDataStore(), "STATION_CODE");
        // getNewCodeByString
        String stationCode = ruleTool.getNewCodeByString(stationMaxCode, 1);
        int seq = getMaxSeq(tableStation.
                            getDataStore(), "SEQ");
        //�õ�����ӵ�table�����к�
        int row = tableStation.addRow();
        //���õ�ǰѡ����Ϊ��ӵ���
        tableStation.setSelectedRow(row);
        //Ĭ�ϻ�ʿվ����
        tableStation.setItem(row, "STATION_CODE", stationCode);
        //Ĭ�ϻ�ʿվ����
        tableStation.setItem(row, "STATION_DESC", "");
        tableStation.setItem(row, "SEQ", seq);
        //�������ֽڵ�
        String name = "��ʿվ";
        String type = "Room";
        TTreeNode nodeNew = new TTreeNode(name, type);
        nodeNew.setText("��ʿվ");
        nodeNew.setID(stationCode);
        node.add(nodeNew);
        tree.update();
    }

    /**
     * ��������
     */
    public void roomNew(String stationCode, TTreeNode node) {

        String roomMaxCode = getMaxCode(tableRoom.
                                        getDataStore(), "ROOM_CODE");
        String roomCode = stationCode + ruleTool.getNewCodeByString(roomMaxCode, 2);
        //�õ�����ӵ�table�����к�
        int row = tableRoom.addRow();
        //���õ�ǰѡ����Ϊ��ӵ���
        tableRoom.setSelectedRow(row);
        //Ĭ�Ϸ����
        tableRoom.setItem(row, "ROOM_CODE", roomCode);
        //Ĭ�Ϸ�������
        tableRoom.setItem(row, "ROOM_DESC", "");
        //Ĭ�ϻ�ʿվ��
        tableRoom.setItem(row, "STATION_CODE", stationCode);
        //���ַ���
        String name = "����";
        String type = "Bed";
        TTreeNode nodeNew = new TTreeNode(name, type);
        nodeNew.setText("����");
        nodeNew.setID(roomCode);
        node.add(nodeNew);
        tree.update();
    }

    /**
     * ������λ
     */
    public void bedNew(String roomCode, String stationCode) {

        String bedMaxCode = getMaxCode(tableBed.getDataStore(),
                                       "BED_NO");
        String bedCode = roomCode + ruleTool.getNewCodeByString(bedMaxCode, 3);
        int seq = getMaxSeq(tableBed.getDataStore(), "SEQ");
        //�õ�����ӵ�table�����к�
        int row = tableBed.addRow();
        //���õ�ǰѡ����Ϊ��ӵ���
        tableBed.setSelectedRow(row);
        //Ĭ�Ϸ����
        tableBed.setItem(row, "BED_NO", bedCode);
        //Ĭ�ϻ�ʿվ��
        tableBed.setItem(row, "STATION_CODE", stationCode);
        //Ĭ�Ϸ����
        tableBed.setItem(row, "ROOM_CODE", roomCode);
        tableBed.setItem(row, "SEQ", seq);
//        //Ĭ�Ͽ���
//        tableBed.setItem(row, "DEPT_CODE", deptCode);
    }

    /**
     * �õ����ı��
     * @param dataStore TDataStore
     * @param columnName String
     * @return String
     */
    public String getMaxCode(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return "";
        int count = dataStore.rowCount();
        String s = "";
        for (int i = 0; i < count; i++) {
            String value = dataStore.getItemString(i, columnName);
            if (StringTool.compareTo(s, value) < 0)
                s = value;
        }
        return s;
    }

    /**
     * �õ��������
     * @param dataStore TDataStore
     * @param columnName String
     * @return String
     */
    public int getMaxSeq(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return 0;
        //����������
        int count = dataStore.rowCount();
        //��������
        int s = 0;
        for (int i = 0; i < count; i++) {
            int value = dataStore.getItemInt(i, columnName);
            //�������ֵ
            if (s < value) {
                s = value;
                continue;
            }
        }
        //���ż�1
        s++;
        return s;
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        //�����ı�
        if (tableNow != null)
            tableNow.acceptText();
        //�õ�Ҫɾ����table�к�
        int row = tableNow.getSelectedRow();
        //���û��ѡ����
        if (row == -1)
            return;
        //�ҵ�ѡ�е����ڵ�
        TTreeNode node = tree.getSelectionNode();
        //���û�ҵ���ǰѡ�еĽڵ�
        if (node == null)
            return;
        //��˵�ǰѡ�е�Χ��
        String type = node.getType();
        //���ѡ�и��ڵ�
        if (type.equals("Station")) {
            tablePane.setSelectedIndex(0);
            switch (messageBox("��ʾ��Ϣ", "����ͬ����Ͳ���һ��ɾ��!", this.YES_NO_OPTION)) {
                //����
                case 0:

                    //ɾ����ʿվ�ķ���
                    stationDeleteRow(row, node);
                    return;
                    //������
                case 1:
                    return;
            }
        }
        //���ѡ�л�ʿվ�ڵ�
        if (type.equals("Room")) {
            tablePane.setSelectedIndex(1);
            switch (messageBox("��ʾ��Ϣ", "����ͬ����һ��ɾ��!", this.YES_NO_OPTION)) {
                //����
                case 0:

                    //ɾ�������ķ���
                    roomDeleteRow(row, node);
                    return;
                    //������
                case 1:
                    return;
            }
        }
        //����õ����Ƿ���ڵ�
        if (type.equals("Bed")) {
            tablePane.setSelectedIndex(2);
            switch (messageBox("��ʾ��Ϣ", "ȷ��ɾ��", this.YES_NO_OPTION)) {
                //����
                case 0:

                    //ɾ����λ�ķ���
                    bedDeleteRow(row);
                    return;
                    //������
                case 1:
                    return;
            }
        }
    }

    /**
     * ɾ����ʿվ
     */
    public void stationDeleteRow(int row, TTreeNode node) {
        //�õ�Ҫɾ���Ļ�ʿվ����
        String code = tableStation.getItemString(row, 0);

        //ɾ����
        TDataStore dataStoreRoom = tableRoom.getDataStore();
        if (dataStoreRoom.isFilter()) {
            tableRoom.setFilter("");
            tableRoom.filter();
        }
        int countRoom = dataStoreRoom.rowCount();
        for (int i = countRoom - 1; i >= 0; i--) {
            //�õ���ʿվ��
            String stationCode = dataStoreRoom.getItemString(i, "STATION_CODE");
            //����Ǳ���ʿվ��
            if (stationCode.equals(code)) {
                //ɾ������
                dataStoreRoom.deleteRow(i);
            }
        }
        //ɾ��
        TDataStore dataStoreBed = tableBed.getDataStore();
        if (dataStoreBed.isFilter()) {
            tableBed.setFilter("");
            tableBed.filter();
        }
        int countBed = dataStoreBed.rowCount();
        for (int i = countBed - 1; i >= 0; i--) {
            //�õ���ʿվ����
            String stationCode = dataStoreBed.getItemString(i, "STATION_CODE");
            //����Ǳ���ʿվ��
            if (stationCode.equals(code)) {
                dataStoreBed.deleteRow(i);
            }
        }
        //ɾ����ʿվ����
        tableStation.removeRow(row);
        //ɾ�����ϵĽڵ�
        TTreeNode treenodeChild = node.findNodeForID(code);
        if (treenodeChild != null)
            node.remove(treenodeChild);

        tree.update();
        tableStation.setDSValue();
        tableBed.setDSValue();
        tableRoom.setDSValue();
    }

    /**
     * ɾ������
     */
    public void roomDeleteRow(int row, TTreeNode node) {
        //�õ���ǰɾ���ķ���
        String code = tableNow.getItemString(row, 0);
        //ɾ��table�ϵĵ�ǰ��
        tableNow.removeRow(row);
        //ɾ����ǰ����ڵ�
        TTreeNode treeNodeChild = node.findNodeForID(code);
        if (treeNodeChild != null)
            node.remove(treeNodeChild);
        //ͬʱɾ������ʿվ�����д�λ
        TTable tableBed = (TTable) callFunction("UI|TABLEBED|getThis");
        TDataStore dataStoreBed = tableBed.getDataStore();
        if (dataStoreBed.isFilter()) {
            tableBed.setFilter("");
            tableBed.filter();
        }
        int countBed = dataStoreBed.rowCount();
        for (int i = countBed - 1; i >= 0; i--) {
            //�õ���ʿվ����
            String roomCode = TCM_Transform.getString(dataStoreBed.getItemData(
                i, "ROOM_CODE"));
            //����Ǳ���ʿվ��
            if (roomCode.equals(code)) {
                dataStoreBed.deleteRow(i);
            }
        }
        tree.update();
    }

    /**
     * ɾ������
     */
    public void bedDeleteRow(int row) {
        tableNow.removeRow(row);
    }

    /**
     * ��������
     */
    public boolean onUpdate() {
        Timestamp date = SystemTool.getInstance().getDate();
        //�����ı�
        tableStation.acceptText();
        TDataStore dateStoreStation = tableStation.getDataStore();
        String name = dateStoreStation.isFilter() ? dateStoreStation.FILTER :
            dateStoreStation.PRIMARY;
        int rowsStation[] = dateStoreStation.getModifiedRows(name);
        //���̶�����������
        for (int i = 0; i < rowsStation.length; i++) {
            if (dateStoreStation.getItemString(rowsStation[i], "REGION_CODE").equals("")) {// wanglong add 20141222
                this.messageBox("������Ϊ��");
                return false;
            }
            tableStation.setItem(rowsStation[i], "OPT_USER",
                                 Operator.getID());
            tableStation.setItem(rowsStation[i], "OPT_DATE", date);
            tableStation.setItem(rowsStation[i], "OPT_TERM",
                                 Operator.getIP());
        }

        for (int i = 0; i < dateStoreStation.rowCount(); i++) {
            if ("".equals(dateStoreStation.getItemString(i, "DEPT_CODE"))) {
                this.messageBox("���Ҵ��벻��Ϊ��");
                return false;
            }
        }
        //ȡ�������Ʒsql
        String[] station = tableStation.getUpdateSQL();
        //�����ı�
        tableRoom.acceptText();

        TDataStore dataStoreRoom = tableRoom.getDataStore();
        name = dataStoreRoom.isFilter() ? dataStoreRoom.FILTER :
            dataStoreRoom.PRIMARY;
        //���ȫ���Ķ����к�
        int rowsRoom[] = dataStoreRoom.getModifiedRows(name);
        //���̶�����������
        for (int i = 0; i < rowsRoom.length; i++) {
            if (TCM_Transform.isNull(dataStoreRoom.getItemData(rowsRoom[i], "REGION_CODE", name))) {// wanglong add 20141222
                this.messageBox("������Ϊ��");
                return false;
            }
            tableRoom.setItemFilter(rowsRoom[i], "OPT_USER", Operator.getID());
            tableRoom.setItemFilter(rowsRoom[i], "OPT_DATE", date);
            tableRoom.setItemFilter(rowsRoom[i], "OPT_TERM", Operator.getIP());
        }

        for (int i = 0; i < dataStoreRoom.rowCount(); i++) {
            if ("".equals(dataStoreRoom.getItemString(i, "STATION_CODE"))) {
                this.messageBox("�������벻��Ϊ��");
                return false;
            }
        }

        String[] room = dataStoreRoom.getUpdateSQL();

        //�����ı�
        tableBed.acceptText();
        TDataStore dataStoreBed = tableBed.getDataStore();
        name = dataStoreBed.isFilter() ? dataStoreBed.FILTER :
            dataStoreBed.PRIMARY;
        //���ȫ���Ķ����к�
        int rowsBed[] = dataStoreBed.getModifiedRows(name);
        //���̶�����������
        for (int i = 0; i < rowsBed.length; i++) {
            if (TCM_Transform.isNull(dataStoreBed.getItemData(rowsBed[i], "REGION_CODE", name))) {// wanglong add 20141222
                this.messageBox("������Ϊ��");
                return false;
            }
            tableBed.setItemFilter(rowsBed[i], "OPT_USER", Operator.getID());
            tableBed.setItemFilter(rowsBed[i], "OPT_DATE", date);
            tableBed.setItemFilter(rowsBed[i], "OPT_TERM", Operator.getIP());
        }

        String[] bed = dataStoreBed.getUpdateSQL();
        //������sql�ϲ�
        bed = StringTool.copyArray(bed, room);
        bed = StringTool.copyArray(bed, station);
        //�õ�����sql�Ķ���
        TJDODBTool dbTool = new TJDODBTool();
        //��������
        TParm result = new TParm(dbTool.update(bed));
        if (result.getErrCode() < 0) {
            out("update err " + result.getErrText());
            this.messageBox("E0001");
            return false;
        }
        messageBox("P0001");
        //��ձ����¼
        dateStoreStation.resetModify();
        dataStoreRoom.resetModify();
        dataStoreBed.resetModify();
        return true;
    }

    /**
     * �Ƿ�رմ���
     * @return boolean true �ر� false ���ر�
     */
    public boolean onClosing() {
        // ��������ݱ��
        if (CheckChange())
            switch (this.messageBox("��ʾ��Ϣ",
                                    "�Ƿ񱣴�", this.YES_NO_CANCEL_OPTION)) {
                //����
                case 0:
                    if (!onUpdate())
                        return false;
                    break;
                    //������
                case 1:
                    return true;
                    //����
                case 2:
                    return false;
            }
        //û�б��������
        return true;

    }

    /**
     * ����Ƿ������ݱ��
     * @return boolean
     */
    public boolean CheckChange() {
        //�������
        TTable tableBed = (TTable) callFunction("UI|TABLEBED|getThis");
        if (tableBed.isModified())
            return true;
        //�����ʿվ
        TTable tableStation = (TTable) callFunction("UI|TABLESTATION|getThis");
        if (tableStation.isModified())
            return true;
        //�������
        TTable tableRoom = (TTable) callFunction("UI|TABLEROOM|getThis");
        if (tableRoom.isModified())
            return true;
        return false;
    }

    /* *
     * @param args String[]
     */
    public static void main(String args[]) {
        com.javahis.util.JavaHisDebug.TBuilder();
//        Operator.setData("admin", "HIS", "127.0.0.1", "C00101");
    }
}
