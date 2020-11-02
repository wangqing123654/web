package com.javahis.ui.sys;

import jdo.sys.SYSRuleTool;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.TMessage;
import com.dongyang.ui.event.TTreeEvent;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.ui.TTree;
import com.dongyang.util.StringTool;
import jdo.sys.SYSPublic;
import com.dongyang.ui.TTreeNode;
import jdo.bil.BILSQL;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title:��ʿվ���䴲 </p>
 *
 * <p>Description:��ʿվ���䴲 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis </p>
 *
 * @author fudw 2009-9-18
 * @version 1.0
 */
public class SYSSRBControl
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
     * ��Ź�����𹤾�
     */
    SYSRuleTool ruleTool = new SYSRuleTool("SYS_STATION");
    /**
     * �������ݷ���datastore���ڶ��������ݹ���
     */
    TDataStore treeDataStore = new TDataStore();
    /**
     * ��
     */
    TTree tree;

    /**
     * ��ʼ��
     */
    public void onInit() { //��ʼ������
        super.onInit();
        //��tree��Ӽ����¼�
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");

        tree = (TTree)this.getComponent("TREE");
        treeRoot = tree.getRoot();
        //���¼�����
        retriveTree();
    }

    /**
     * ���¼�����
     */
    public void retriveTree() {
        //��ʼ����
        onInitSelectTree();
        //����
        onCreatTree();
        //
        getTreeLoyar();
        //��ʼ�������ϵĿ�����
        cleckRoot("");

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
     * ����������
     */
    public void onCreatTree() {
        //�õ�table����
        tableNow = new TTable();
        tableNow.setSQL("select * from sys_station");
        tableNow.retrieve();

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
        TTable tableRoom = new TTable();
        tableRoom.setSQL("select * from sys_room");
        tableRoom.retrieve();
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
            creatBedTree(node);
        }
    }

    /**
     * ������λ
     * @param parentNode TTreeNode
     */
    public void creatBedTree(TTreeNode parentNode) {
        //�鿴����Ľڵ�
        if (parentNode == null)
            return;

        //�õ���datastore
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL("select * from sys_bed");
        dataStore.retrieve();

        String value = "ROOM_CODE='" + parentNode.getID() + "'";
        dataStore.setFilter(value);
        dataStore.filter();
        //�õ���������
        int stationCount = dataStore.rowCount();
        //ѭ��������ڵ�
        for (int i = 0; i < stationCount; i++) {
            String id = dataStore.getItemString(i, "BED_NO");
            String name = dataStore.getItemString(i, "BED_NO_DESC");
            String type = "BEDSEQ";
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
        //����ѡ�нڵ������
        String type = node.getType();
        //ѡ�е����ڵ��ID
        String id = node.getID();
        //���ѡ�и��ڵ�
        if (type.equals("Station")) {
            //���ڵ�ѡ����
            cleckRoot(id);
        }
        //���ѡ�л�ʿվ�ڵ�
        if (type.equals("Room")) {
            //�����ʿվ
            cleckStation(id);
        }
        //����õ����Ƿ���ڵ�
        if (type.equals("Bed")) {
            //�������
            cleckRoom(id);
        }
        //�����ڵ�
        if (type.equals("BEDSEQ")) {
            //�������
            cleckBed(id);
        }
    }

    /**
     * ���ڵ�ѡ����
     * @param id String
     */
    public void cleckRoot(String id) {
        //��ʿվ���Կɱ༭
        setStationEnabled(true);
        clearStation();
        //�������Կɱ༭
        setRoomEnabled(false);
        clearRoom();
        //��λ���Կ˱༭
        setBedEnabled(false);
        clearBed();
        this.callFunction("UI|STATIONSAVE|setEnabled", true);
        this.callFunction("UI|STATIONCLEAR|setEnabled", true);
        this.callFunction("UI|STATIONDELETE|setEnabled", false);

        this.callFunction("UI|ROOMSAVE|setEnabled", false);
        this.callFunction("UI|ROOMCLEAR|setEnabled", false);
        this.callFunction("UI|ROOMDELETE|setEnabled", false);

        this.callFunction("UI|BEDSAVE|setEnabled", false);
        this.callFunction("UI|BEDCLEAR|setEnabled", false);
        this.callFunction("UI|BEDDELETE|setEnabled", false);
    }

    /**
     * �����ʿվ
     * @param id String
     */
    public void cleckStation(String id) {
        //����ʿվ����
        TParm stationParm = setStationData(id);
        //��ʿվ���Կɱ༭
        setStationEnabled(true);
        this.callFunction("UI|STATION_CODE|setEnabled", false);
        //�������Կɱ༭
        setRoomEnabled(true);
        clearRoom();
        //��λ���Կ˱༭
        setBedEnabled(false);
        clearBed();

        this.callFunction("UI|STATIONSAVE|setEnabled", true);
        this.callFunction("UI|STATIONCLEAR|setEnabled", false);
        this.callFunction("UI|STATIONDELETE|setEnabled", true);

        this.callFunction("UI|ROOMSAVE|setEnabled", true);
        this.callFunction("UI|ROOMCLEAR|setEnabled", true);
        this.callFunction("UI|ROOMDELETE|setEnabled", false);

        this.callFunction("UI|BEDSAVE|setEnabled", false);
        this.callFunction("UI|BEDCLEAR|setEnabled", false);
        this.callFunction("UI|BEDDELETE|setEnabled", false);
    }

    /**
     * �������
     * @param id String
     */
    public void cleckRoom(String id) {
        //����������
        TParm parmRoom = setRoomData(id);
        //����ʿվ����
        setStationData(parmRoom.getValue("STATION_CODE", 0));
        //��ʿվ���Կɱ༭
        setStationEnabled(false);
        //�������Կɱ༭
        setRoomEnabled(true);
        this.callFunction("UI|ROOM_CODE|setEnabled", false);
        //��λ���Կ˱༭
        setBedEnabled(true);
        clearBed();

        this.callFunction("UI|STATIONSAVE|setEnabled", false);
        this.callFunction("UI|STATIONCLEAR|setEnabled", false);
        this.callFunction("UI|STATIONDELETE|setEnabled", false);

        this.callFunction("UI|ROOMSAVE|setEnabled", true);
        this.callFunction("UI|ROOMCLEAR|setEnabled", false);
        this.callFunction("UI|ROOMDELETE|setEnabled", true);

        this.callFunction("UI|BEDSAVE|setEnabled", true);
        this.callFunction("UI|BEDCLEAR|setEnabled", true);
        this.callFunction("UI|BEDDELETE|setEnabled", false);

    }

    /**
     * ��λ��������¼�
     * @param id String
     */
    public void cleckBed(String id) {
        TParm bedData = setBedData(id);
        //����ʿվ����
        setStationData(bedData.getValue("STATION_CODE", 0));
        //��λ����
        setRoomData(bedData.getValue("ROOM_CODE", 0));
        //��ʿվ���Կɱ༭
        setStationEnabled(false);
        //�������Կɱ༭
        setRoomEnabled(false);
        //��λ���Կ˱༭
        setBedEnabled(true);
        this.callFunction("UI|BED_NO|setEnabled", false);

        this.callFunction("UI|STATIONSAVE|setEnabled", false);
        this.callFunction("UI|STATIONCLEAR|setEnabled", false);
        this.callFunction("UI|STATIONDELETE|setEnabled", false);

        this.callFunction("UI|ROOMSAVE|setEnabled", false);
        this.callFunction("UI|ROOMCLEAR|setEnabled", false);
        this.callFunction("UI|ROOMDELETE|setEnabled", false);

        this.callFunction("UI|BEDSAVE|setEnabled", true);
        this.callFunction("UI|BEDCLEAR|setEnabled", false);
        this.callFunction("UI|BEDDELETE|setEnabled", true);

    }

    /**
     * ����ʿվ����
     * @param stationCode String
     * @return TParm
     */
    public TParm setStationData(String stationCode) {
        TParm parmStation = getStationData(stationCode);
        if (parmStation.getErrCode() < 0)
            return parmStation;
        setValueForParm(
            "STATION_CODE;STATION_DESC;PY1;DEPT_CODE;REGION_CODE;ORG_CODE;" +
            "LOC_CODE;PRINTER_NO;TEL_EXT;SEQ", parmStation, 0);
        return parmStation;
    }

    /**
     * ����������
     * @param roomCode String
     * @return TParm
     */
    public TParm setRoomData(String roomCode) {
        TParm parmRoom = getRoomData(roomCode);
        if (parmRoom.getErrCode() < 0)
            return parmRoom;
        setValueForParm(
            "ROOM_CODE;ROOM_DESC;ROOMPY1;SEX_LIMIT_FLG;RED_SIGN;YELLOW_SIGN;DESCRIPT",
            parmRoom, 0);
        setValue("ROOMPY1", parmRoom.getValue("PY1", 0));
        return parmRoom;
    }

    /**
     * ����λ����
     * @param bedCode String
     * @return TParm
     */
    public TParm setBedData(String bedCode) {
        TParm parmBed = getBedData(bedCode);
        if (parmBed.getErrCode() < 0)
            return parmBed;
        setValueForParm(
            "BED_NO;BED_NO_DESC;BEDPY1;ROOM_CLASS_CODE;BED_TYPE_CODE;ACTIVE_FLG;" +
            "APPT_FLG;ALLO_FLG;BED_OCCU_FLG;RESERVE_BED_FLG;BED_ATTR_CODE;SEX_CODE;" +
            "OCCU_RATE_FLG;DR_APPROVE_FLG;HTL_FLG;BEDSEQ;DESCRIPTION", parmBed,
            0);
        setValue("BEDPY1", parmBed.getValue("PY1", 0));
        setValue("BEDSEQ", parmBed.getInt("SEQ", 0));
        return parmBed;
    }

    /**
     * �õ���ʿվ����
     * @param stationCode String
     * @return TParm
     */
    public TParm getStationData(String stationCode) {
        TParm result = new TParm();
        String sql = BILSQL.getStationSql(stationCode);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * ���ݷ���ŵõ���������
     * @param roomCode String
     * @return TParm
     */
    public TParm getRoomData(String roomCode) {
        TParm result = new TParm();
        String sql = BILSQL.getRoomSql(roomCode);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * ����������
     * @param bedCode String
     * @return TParm
     */
    public TParm getBedData(String bedCode) {
        TParm result = new TParm();
        String sql = BILSQL.getBedSql(bedCode);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * ���
     */
    public void onClear() {
        //��ʿվ���Կɱ༭
        setStationEnabled(true);
        clearStation();
        //�������Կɱ༭
        setRoomEnabled(true);
        clearRoom();
        //��λ���Կ˱༭
        setBedEnabled(true);
        clearBed();
    }

    /**
     * ��ջ�ʿվ����
     */
    public void clearStation() {
        //��ջ�ʿվ����
        clearValue(
            "STATION_CODE;STATION_DESC;PY1;DEPT_CODE;REGION_CODE;ORG_CODE;" +
            "LOC_CODE;PRINTER_NO;TEL_EXT;SEQ");
    }

    /**
     * ��շ�������
     */
    public void clearRoom() {
        //��չ�������
        clearValue(
            "ROOM_CODE;ROOM_DESC;ROOMPY1;SEX_LIMIT_FLG;RED_SIGN;YELLOW_SIGN;DESCRIPT");
    }

    /**
     * ��մ�λ����
     */
    public void clearBed() {
        //��չ�������
        clearValue(
            "BED_NO;BED_NO_DESC;BEDPY1;BED_CLASS_CODE;BED_TYPE_CODE;ACTIVE_FLG;" +
            "APPT_FLG;ALLO_FLG;BED_OCCU_FLG;RESERVE_BED_FLG;BED_ATTR_CODE;SEX_CODE;" +
            "OCCU_RATE_FLG;DR_APPROVE_FLG;HTL_FLG;BEDSEQ;DESCRIPTION");
    }

    /**
     * ��λ���Կɱ༭
     * @param enable boolean
     */
    public void setStationEnabled(boolean enable) {
        this.callFunction("UI|STATION_CODE|setEnabled", enable);
        this.callFunction("UI|STATION_DESC|setEnabled", enable);
        this.callFunction("UI|PY1|setEnabled", enable);
        this.callFunction("UI|DEPT_CODE|setEnabled", enable);
        this.callFunction("UI|REGION_CODE|setEnabled", enable);
        this.callFunction("UI|ORG_CODE|setEnabled", enable);
        this.callFunction("UI|LOC_CODE|setEnabled", enable);
        this.callFunction("UI|PRINTER_NO|setEnabled", enable);
        this.callFunction("UI|TEL_EXT|setEnabled", enable);
        this.callFunction("UI|SEQ|setEnabled", enable);
    }

    /**
     * �������Ա༭
     * @param enable boolean
     */
    public void setRoomEnabled(boolean enable) {
        this.callFunction("UI|ROOM_CODE|setEnabled", enable);
        this.callFunction("UI|ROOM_DESC|setEnabled", enable);
        this.callFunction("UI|ROOMPY1|setEnabled", enable);
        this.callFunction("UI|SEX_LIMIT_FLG|setEnabled", enable);
        this.callFunction("UI|RED_SIGN|setEnabled", enable);
        this.callFunction("UI|YELLOW_SIGN|setEnabled", enable);
        this.callFunction("UI|DESCRIPT|setEnabled", enable);
    }

    /**
     * ��λ���Ա༭
     * @param enable boolean
     */
    public void setBedEnabled(boolean enable) {

        this.callFunction("UI|BED_NO|setEnabled", enable);
        this.callFunction("UI|BED_NO_DESC|setEnabled", enable);
        this.callFunction("UI|BEDPY1|setEnabled", enable);
        this.callFunction("UI|BED_CLASS_CODE|setEnabled", enable);
        this.callFunction("UI|BED_TYPE_CODE|setEnabled", enable);
        this.callFunction("UI|ACTIVE_FLG|setEnabled", enable);
        this.callFunction("UI|APPT_FLG|setEnabled", enable);
        this.callFunction("UI|ALLO_FLG|setEnabled", enable);
        this.callFunction("UI|BED_OCCU_FLG|setEnabled", enable);
        this.callFunction("UI|RESERVE_BED_FLG|setEnabled", enable);
        this.callFunction("UI|SEX_CODE|setEnabled", enable);
        this.callFunction("UI|OCCU_RATE_FLG|setEnabled", enable);
        this.callFunction("UI|DR_APPROVE_FLG|setEnabled", enable);
        this.callFunction("UI|HTL_FLG|setEnabled", enable);
        this.callFunction("UI|BEDSEQ|setEnabled", enable);
        this.callFunction("UI|DESCRIPTION|setEnabled", enable);
    }

    /**
     * ��ʿվ���Ƽ��
     */
    public void onStationDescAction() {
        String stationDesc = getValueString("STATION_DESC");
        if (stationDesc == null || stationDesc.length() == 0) {
            if (!message("��ʿվ����Ϊ��,�Ƿ����!"))
                return;
        }
        else {
            String sql = BILSQL.getStationDescSql(stationDesc);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0 || result.getCount() > 0)
                if (!message("��ʿվ�����ظ�,�Ƿ����!"))
                    return;
        }
        String py = TMessage.getPy(stationDesc);
        this.setValue("PY1", py);
    }

    /**
     * ��ʿվ������
     * @return boolean
     */
    public boolean onStationCodeAction() {
        String stationCode = getValueString("STATION_CODE");
        if (stationCode == null || stationCode.length() == 0) {
            messageBox("��ʿվ���벻��Ϊ��!");
            return false;
        }
        else {
            String sql = BILSQL.getStationSql(stationCode);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0 || result.getCount() > 0) {
                messageBox("��ʿվ�����ظ�!");
                return false;
            }
        }
        //�õ���ǰ����볤��
        int lengthStation = ruleTool.getClassNumber(1);
        if (lengthStation != stationCode.length()) {
            messageBox("���" + stationCode + "���Ȳ�����" + lengthStation + "!");
            return false;
        }
        return true;
    }

    /**
     * �������Ƽ��
     */
    public void onRoomDescAction() {
        String roomDesc = getValueString("ROOM_DESC");
        if (roomDesc == null || roomDesc.length() == 0) {
            if (!message("��ʿվ����Ϊ��,�Ƿ����!"))
                return;
        }
        else {
            String sql = BILSQL.getRoomDescSql(roomDesc);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0 || result.getCount() > 0)
                if (!message("��ʿվ�����ظ�,�Ƿ����!"))
                    return;
        }
        String py = TMessage.getPy(roomDesc);
        this.setValue("ROOMPY1", py);
    }

    /**
     * ���������
     * @return boolean
     */
    public boolean onRoomCodeAction() {
        String roomCode = getValueString("ROOM_CODE");
        if (roomCode == null || roomCode.length() == 0) {
            messageBox("����Ų���Ϊ��!");
            return false;
        }
        else {
            String sql = BILSQL.getRoomSql(roomCode);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0 || result.getCount() > 0) {
                messageBox("������ظ��ظ�!");
                return false;
            }
        }
        //�õ���ǰ����볤��
        int lengthStation = ruleTool.getClassNumber(2);
        if (lengthStation != roomCode.length()) {
            messageBox("���" + roomCode + "���Ȳ�����" + lengthStation + "!");
            return false;
        }
        return true;
    }

    /**
     * ��λ���Ƽ��
     */

    public void onBedDescAction() {
        String bedNoDesc = getValueString("BED_NO_DESC");
        if (bedNoDesc == null || bedNoDesc.length() == 0) {
            if (!message("��λ����Ϊ��,�Ƿ����!"))
                return;
        }
        else {
            String sql = BILSQL.getBedNoDescSql(bedNoDesc);
            //System.out.println("bedCheckSql=" + sql);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            //System.out.println("bedSescCheckResult======" + result);
            if (result.getErrCode() < 0 || result.getCount() > 0)
                if (!message("��λ�����ظ�,�Ƿ����!"))
                    return;
        }
        String py = TMessage.getPy(bedNoDesc);
        this.setValue("BEDPY1", py);

    }

    /**
     * ��λ�ż��
     * @return boolean
     */
    public boolean onbedNoAction() {
        String bedNo = getValueString("BED_NO");
        if (bedNo == null || bedNo.length() == 0) {
            messageBox("��λ�Ų���Ϊ��!");
            return false;
        }
        else {
            String sql = BILSQL.getBedSql(bedNo);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0 || result.getCount() > 0) {
                messageBox("��λ���ظ��ظ�!");
                return false;
            }
        }
        //�õ���ǰ����볤��
        int lengthStation = ruleTool.getTot() + ruleTool.getSerial();
        if (lengthStation != bedNo.length()) {
            messageBox("���" + bedNo + "���Ȳ�����" + lengthStation + "!");
            return false;
        }
        return true;
    }


    /**
     * �õ���ǰѡ��Ľڵ�����
     * @return String
     */
    public String getTreeSelectNodeType() {
        //�ҵ�ѡ�е����ڵ�
        TTreeNode node = tree.getSelectionNode();
        //���û�ҵ���ǰѡ�еĽڵ�
        if (node == null)
            return "";
        //��˵�ǰѡ�е�Χ��
        String type = node.getType();
        return type;
    }

    /**
     * ���滤ʿվ
     */
    public void onSaveStation() {
        String type = getTreeSelectNodeType();
        String sql = "";
        if (type.equals("Station"))
            sql = insertStation();
        if (type.equals("Room"))
            sql = updateStation();
        if (sql == null || sql.length() == 0)
            return;
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (saveMessage(result)) {
            if (type.equals("Station"))
                insertTreeNode("", getValueString("STATION_DESC"),
                               "Room", getValueString("STATION_CODE"));
            if (type.equals("Room"))
                changeDesc(getValueString("STATION_CODE"),
                           getValueString("STATION_DESC"));
        }

        return;
    }

    /**
     * ���������ʾ��Ϣ
     * @param result TParm
     * @return boolean
     */
    public boolean saveMessage(TParm result) {
        if (result.getErrCode() < 0) {
            out("update err " + result.getErrText());
            this.messageBox("E0001");
            return false;
        }
        messageBox("P0001");
        return true;
    }

    /**
     * ������ʿվ
     * @return String
     */
    public String insertStation() {
        //��˻�ʿվ����
        if (!onStationCodeAction())
            return "";
        return StationValue();
    }

    /**
     * ��ʿվ����sql
     * @return String
     */
    public String StationValue() {
        String value =
            "INSERT INTO SYS_STATION (STATION_CODE,STATION_DESC,PY1," +
            "DEPT_CODE,REGION_CODE,ORG_CODE,LOC_CODE,PRINTER_NO,TEL_EXT,SEQ," +
            "OPT_USER,OPT_DATE,OPT_TERM) VALUES (";
        //��ʿվ����
        value += "'" + this.getValue("STATION_CODE") + "'";
        //��ʿվ����
        value += ",'" + this.getValue("STATION_DESC") + "'";
        //ƴ��
        value += ",'" + this.getValue("PY1") + "'";
        //����
        value += ",'" + this.getValue("DEPT_CODE") + "'";
        value += ",'" + this.getValue("REGION_CODE") + "'";
        value += ",'" + this.getValue("ORG_CODE") + "'";
        value += ",'" + this.getValue("LOC_CODE") + "'";
        value += ",'" + this.getValue("PRINTER_NO") + "'";
        value += ",'" + this.getValue("TEL_EXT") + "'";
        value += ",'" + this.getValueInt("SEQ") + "'";
        value += ",'" + Operator.getID() + "'";
        value += ",TO_DATE('" +
            StringTool.getString(SystemTool.getInstance().getDate(),
                                 "yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
        value += ",'" + Operator.getIP() + "')";
        return value;
    }

    /**
     * ���»�ʿվ
     * @return String
     */
    public String updateStation() {
        String value = "UPDATE SYS_STATION SET ";
        //��ʿվ����
        value += "STATION_DESC='" + this.getValue("STATION_DESC") + "'";
        //ƴ��
        value += ",PY1='" + this.getValue("PY1") + "'";
        //����
        value += ",DEPT_CODE='" + this.getValue("DEPT_CODE") + "'";
        value += ",REGION_CODE='" + this.getValue("REGION_CODE") + "'";
        value += ",ORG_CODE='" + this.getValue("ORG_CODE") + "'";
        value += ",LOC_CODE='" + this.getValue("LOC_CODE") + "'";
        value += ",PRINTER_NO='" + this.getValue("PRINTER_NO") + "'";
        value += ",TEL_EXT='" + this.getValue("TEL_EXT") + "'";
        value += ",SEQ='" + this.getValue("SEQ") + "'";
        value += ",OPT_USER='" + Operator.getID() + "'";
        value += ",OPT_DATE=TO_DATE('" +
            StringTool.getString(SystemTool.getInstance().getDate(),
                                 "yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
        value += ",OPT_TERM='" + Operator.getIP() + "' WHERE STATION_CODE='" +
            this.getValue("STATION_CODE") + "'";
        return value;

    }

    /**
     * ���淿��
     */
    public void onSaveRoom() {
        String type = getTreeSelectNodeType();
        String sql = "";
        if (type.equals("Room"))
            sql = insertRoom();
        if (type.equals("Bed"))
            sql = updateRoom();
        if (sql == null || sql.length() == 0)
            return;
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));

        if (saveMessage(result)) {
            if (type.equals("Room"))
                insertTreeNode(getValueString("STATION_CODE"),
                               getValueString("ROOM_DESC"),
                               "Bed", getValueString("ROOM_CODE"));
            if (type.equals("Bed"))
                changeDesc(getValueString("ROOM_CODE"),
                           getValueString("ROOM_DESC"));
        }
        return;
    }

    /**
     * ��������
     * @return String
     */
    public String insertRoom() {
        if (!onRoomCodeAction())
            return "";
        //��˻�ʿվ����
        String stationCode = getValueString("STATION_CODE");
        if (stationCode == null || stationCode.length() == 0) {
            messageBox("��ʿվ���벻��Ϊ��!");
            return "";
        }
        //�õ���ʿվ���볤��
        int lengthStation = ruleTool.getClassNumber(1);
        if (lengthStation != stationCode.length()) {
            messageBox("���" + stationCode + "���Ȳ�����" + lengthStation + "!");
            return "";
        }
        return insertRoomSql();
    }

    /**
     * ���뷿���sql
     * @return String
     */
    public String insertRoomSql() {
        String value = "INSERT INTO SYS_ROOM (ROOM_CODE,ROOM_DESC,PY1," +
            "SEX_LIMIT_FLG,RED_SIGN,YELLOW_SIGN,DESCRIPT,STATION_CODE," +
            "OPT_USER,OPT_DATE,OPT_TERM) VALUES(";
        value += "'" + this.getValue("ROOM_CODE") + "'";
        value += ",'" + this.getValue("ROOM_DESC") + "'";
        value += ",'" + this.getValue("PY1") + "'";
        value += ",'" + this.getValue("SEX_LIMIT_FLG") + "'";
        value += ",'" + this.getValue("RED_SIGN") + "'";
        value += ",'" + this.getValue("YELLOW_SIGN") + "'";
        value += ",'" + this.getValue("DESCRIPT") + "'";
        value += ",'" + this.getValue("STATION_CODE") + "'";
        value += ",'" + Operator.getID() + "'";
        value += ",TO_DATE('" +
            StringTool.getString(SystemTool.getInstance().getDate(),
                                 "yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
        value += ",'" + Operator.getIP() + "')";
        return value;
    }

    /**
     * ���·���
     * @return String
     */
    public String updateRoom() {
        String value = "UPDATE SYS_ROOM SET ";
        value += " ROOM_DESC='" + this.getValue("ROOM_DESC") + "'";
        value += ",PY1='" + this.getValue("ROOMPY1") + "'";
        value += ",SEX_LIMIT_FLG='" + this.getValue("SEX_LIMIT_FLG") + "'";
        value += ",RED_SIGN='" + this.getValue("RED_SIGN") + "'";
        value += ",YELLOW_SIGN='" + this.getValue("YELLOW_SIGN") + "'";
        value += ",DESCRIPT='" + this.getValue("DESCRIPT") + "'";
        value += ",OPT_USER='" + Operator.getID() + "'";
        value += ",OPT_DATE=TO_DATE('" +
            StringTool.getString(SystemTool.getInstance().getDate(),
                                 "yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
        value += ",OPT_TERM='" + Operator.getIP() + "'" +
            " WHERE ROOM_CODE ='" + this.getValue("ROOM_CODE") + "'";
        return value;

    }

    /**
     * ���洲λ
     */
    public void onSaveBed() {
        String type = getTreeSelectNodeType();
        String sql = "";
        if (type.equals("Bed"))
            sql = insertBed();
        if (type.equals("BEDSEQ"))
            sql = updateBedSql();
        if (sql == null || sql.length() == 0)
            return;
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (saveMessage(result)) {
            if (type.equals("Bed"))
                insertTreeNode(getValueString("ROOM_CODE"),
                               getValueString("BED_NO_DESC"),
                               "BEDSEQ", getValueString("BED_NO"));
            if (type.equals("BEDSEQ"))
                changeDesc(getValueString("BED_NO"),
                           getValueString("BED_NO_DESC"));
        }
        return;
    }
    /**
     * �жϷ����Ա����
     */
    public void onSexCodeChoose() {
        String sexLimitFlg = getValueString("SEX_LIMIT_FLG");
        String sexCode=getValueString("SEX_CODE");
        if(sexCode==null||sexCode.equals("")||sexCode.length()==0)
            return;
        if (sexLimitFlg == null || sexLimitFlg.length() == 0 ||
            sexLimitFlg.equals("N")) {
            messageBox("�˷��䲻���Ա����!");
            setValue("SEX_CODE", "");
        }
    }
    /**
     * ������λ
     * @return String
     */
    public String insertBed() {
        //��λ���
        if (!onbedNoAction())
            return "";
        String stationCode = getValueString("STATION_CODE");
        if (stationCode == null || stationCode.length() == 0) {
            messageBox("��ʿվ���벻��Ϊ��!");
            return "";
        }
        //�õ���ʿվ���볤��
        int lengthStation = ruleTool.getClassNumber(1);
        if (lengthStation != stationCode.length()) {
            messageBox("���" + stationCode + "���Ȳ�����" + lengthStation + "!");
            return "";
        }
        //���������
        String roomCode = getValueString("ROOM_CODE");
        if (roomCode == null || roomCode.length() == 0) {
            messageBox("����Ų���Ϊ��!");
            return "";
        }
        //�õ�������볤��
        int lengthRoom = ruleTool.getClassNumber(2);
        if (lengthRoom != roomCode.length()) {
            messageBox("���" + roomCode + "���Ȳ�����" + lengthRoom + "!");
            return "";
        }
        return insertBedSql();
    }

    /**
     * ���봲λ��sql
     * @return String
     */
    public String insertBedSql() {
        String value =
            "INSERT INTO SYS_BED (ROOM_CODE,STATION_CODE,BED_NO,BED_NO_DESC,PY1," +
            "BED_CLASS_CODE,BED_TYPE_CODE,ACTIVE_FLG," +
            "APPT_FLG,ALLO_FLG,BED_OCCU_FLG,RESERVE_BED_FLG,SEX_CODE," +
            "OCCU_RATE_FLG,DR_APPROVE_FLG,HTL_FLG,SEQ,DESCRIPTION,OPT_USER,OPT_DATE,OPT_TERM) VALUES(";
        value += "'" + this.getValue("ROOM_CODE") + "'";
        value += ",'" + this.getValue("STATION_CODE") + "'";
        value += ",'" + this.getValue("BED_NO") + "'";
        value += ",'" + this.getValue("BED_NO_DESC") + "'";
        value += ",'" + this.getValue("BEDPY1") + "'";
        value += ",'" + this.getValue("BED_CLASS_CODE") + "'";
        value += ",'" + this.getValue("BED_TYPE_CODE") + "'";
        value += ",'" + this.getValue("ACTIVE_FLG") + "'";
        value += ",'" + this.getValue("APPT_FLG") + "'";
        value += ",'" + this.getValue("ALLO_FLG") + "'";
        value += ",'" + this.getValue("BED_OCCU_FLG") + "'";
        value += ",'" + this.getValue("RESERVE_BED_FLG") + "'";
        value += ",'" + this.getValue("SEX_CODE") + "'";
        value += ",'" + this.getValue("OCCU_RATE_FLG") + "'";
        value += ",'" + this.getValue("DR_APPROVE_FLG") + "'";
        value += ",'" + this.getValue("HTL_FLG") + "'";
        value += ",'" + this.getValue("SEQ") + "'";
        value += ",'" + this.getValue("DESCRIPTION") + "'";
        value += ",'" + Operator.getID() + "'";
        value += ",TO_DATE('" +
            StringTool.getString(SystemTool.getInstance().getDate(),
                                 "yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
        value += ",'" + Operator.getIP() + "')";
        return value;
    }

    /**
     * ���´�λ����
     * @return String
     */
    public String updateBedSql() {
        String value =
            "UPDATE SYS_BED SET ";
        value += "BED_NO_DESC='" + this.getValue("BED_NO_DESC") + "'";
        value += ",PY1='" + this.getValue("BEDPY1") + "'";
        value += ",BED_CLASS_CODE='" + this.getValue("BED_CLASS_CODE") + "'";
        value += ",BED_TYPE_CODE='" + this.getValue("BED_TYPE_CODE") + "'";
        value += ",ACTIVE_FLG='" + this.getValue("ACTIVE_FLG") + "'";
        value += ",APPT_FLG='" + this.getValue("APPT_FLG") + "'";
        value += ",ALLO_FLG='" + this.getValue("ALLO_FLG") + "'";
        value += ",BED_OCCU_FLG='" + this.getValue("BED_OCCU_FLG") + "'";
        value += ",RESERVE_BED_FLG='" + this.getValue("RESERVE_BED_FLG") + "'";
        value += ",OCCU_RATE_FLG='" + this.getValue("OCCU_RATE_FLG") + "'";
        value += ",DR_APPROVE_FLG='" + this.getValue("DR_APPROVE_FLG") + "'";
        value += ",HTL_FLG='" + this.getValue("HTL_FLG") + "'";
        value += ",SEQ='" + this.getValueInt("BEDSEQ") + "'";
        value += ",DESCRIPTION='" + this.getValue("DESCRIPTION") + "'";
        value += ",OPT_USER='" + Operator.getID() + "'";
        value += ",OPT_DATE=TO_DATE('" +
            StringTool.getString(SystemTool.getInstance().getDate(),
                                 "yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
        value += ",OPT_TERM='" + Operator.getIP() + "'" +
            " WHERE BED_NO='" + this.getValue("BED_NO") + "'"; ;
        return value;

    }

    /**
     * ɾ����ʿվ
     */
    public void deleteStation() {
        if (!message("����ɾ���䷿��,�������ڵĴ�λ,�Ƿ����!"))
            return;
        String type = getTreeSelectNodeType();
        if (!type.equals("Room")) {
            messageBox_("��ѡ��ʿվ!");
            return;
        }
        String stationCode = getValueString("STATION_CODE");
        if (stationCode == null || stationCode.length() == 0) {
            messageBox("��ʿվ���벻��Ϊ��!");
            return;
        }
        else {
            String sql = BILSQL.getStationSql(stationCode);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0 || result.getCount() == 0) {
                messageBox("��ʿվ������!");
                return;
            }
        }
        //����ɾ��sql
        TParm parm = new TParm();
        //ɾ����ʿվ��sql
        String deleteStationSql = "DELETE SYS_STATION WHERE STATION_CODE='" +
            stationCode + "'";
        parm.setData("DELETESTATION", deleteStationSql);
        //ɾ����ʿվ����ķ���sql
        String deleteRoomSql = "DELETE SYS_ROOM WHERE STATION_CODE='" +
            stationCode + "'";
        parm.setData("DELETEROOM", deleteRoomSql);
        //ɾ����ʿվ����Ĵ�λsql
        String deleteBedSql = "DELETE SYS_BED WHERE STATION_CODE='" +
            stationCode + "'";
        parm.setData("DELETEBED", deleteBedSql);
        //���ú�̨action
        TParm saveParm = new TParm();
        saveParm.setData("DELETE", parm.getData());
        TParm result = TIOM_AppServer.executeAction(
            "action.sys.SYSStationAction", "deleteStation", saveParm);
        if (saveMessage(result)) {
            updateTree("", stationCode);
            this.clearStation();
        }
    }

    /**
     * ɾ������
     */
    public void deleteRoom() {
        if (!message("����ɾ�������еĴ�λ,�Ƿ����!"))
            return;
        String type = getTreeSelectNodeType();
        if (!type.equals("Bed")) {
            messageBox_("��ѡ�񷿼�!");
            return;
        }
        String roomCode = getValueString("ROOM_CODE");
        if (roomCode == null || roomCode.length() == 0) {
            messageBox("����Ų���Ϊ��!");
            return;
        }
        else {
            String sql = BILSQL.getRoomSql(roomCode);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0 || result.getCount() == 0) {
                messageBox("���䲻����!");
                return;
            }
        }

        //����ɾ��sql
        TParm parm = new TParm();
        //ɾ������
        String deleteRoomSql = "DELETE SYS_ROOM WHERE ROOM_CODE='" + roomCode +
            "'";
        parm.setData("DELETEROOM", deleteRoomSql);
        //ɾ������
        String deleteBedSql = "DELETE SYS_BED WHERE ROOM_CODE='" + roomCode +
            "'";
        parm.setData("DELETEBED", deleteBedSql);
        //���ú�̨action
        TParm saveParm = new TParm();
        saveParm.setData("DELETE", parm.getData());

        TParm result = TIOM_AppServer.executeAction(
            "action.sys.SYSStationAction", "deleteRoom", saveParm);
        if (saveMessage(result)) {
            updateTree(getValueString("STATION_CODE"), roomCode);
            this.clearRoom();
        }
    }

    /**
     * ɾ��ˢ����
     * @param parentId String
     * @param id String
     */
    public void updateTree(String parentId, String id) {
        TTreeNode deleteNode = treeRoot.findNodeForID(parentId);
        if (deleteNode != null)
            deleteNode.remove(deleteNode.findChildNodeForID(id));
        tree.update();
    }

    /**
     * �����ڵ�������Ͻڵ�
     * @param parentId String
     * @param name String
     * @param type String
     * @param id String
     */
    public void insertTreeNode(String parentId, String name, String type,
                               String id) {
        TTreeNode node = treeRoot.findNodeForID(parentId);
        TTreeNode nodeNew = new TTreeNode(name, type);
        nodeNew.setText(name);
        nodeNew.setID(id);
        node.add(nodeNew);
        tree.update();
    }

    /**
     * �������ϵ�����
     * @param id String
     * @param desc String
     */
    public void changeDesc(String id, String desc) {
        TTreeNode node = treeRoot.findNodeForID(id);
        node.setText(desc);
        tree.update();
    }

    /**
     * ɾ����λ
     */
    public void onDeleteBed() {
        if (!message("����ͣ��,ȷ��ɾ��!"))
            return;
        String type = getTreeSelectNodeType();
        if (!type.equals("BEDSEQ")) {
            messageBox_("��ѡ��λ!");
            return;
        }
        String bedNo = getValueString("BED_NO");
        if (bedNo == null || bedNo.length() == 0) {
            messageBox("��λ�Ų���Ϊ��!");
            return;
        }
        else {
            String sql = BILSQL.getBedSql(bedNo);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0 || result.getCount() == 0) {
                messageBox("��λ������!");
                return;
            }
        }
        String deleteBedSql = "DELETE SYS_BED WHERE BED_NO='" + bedNo + "'";
        TParm result = new TParm(TJDODBTool.getInstance().update(deleteBedSql));
        if (saveMessage(result)) {
            updateTree(getValueString("ROOM_CODE"), bedNo);
            this.clearBed();
        }
        return;
    }

    /**
     * ��ʾ��Ϣ
     * @param inMessage String
     * @return boolean
     */
    public boolean message(String inMessage) {
        switch (this.messageBox("��ʾ��Ϣ",
                                inMessage, this.YES_NO_OPTION)) {
            //����
            case 0:
                return true;
                //������
            case 1:
                return false;
        }
        //û�б��������
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
