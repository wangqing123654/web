package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import jdo.clp.CLPEVLStandmTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTable;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import java.sql.Timestamp;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.dongyang.ui.TButton;
import jdo.clp.CLPEVLCat3Tool;
import java.util.Vector;
import java.util.Iterator;
import jdo.clp.CLPEVLStanddTool;

/**
 * <p>Title: �ٴ�·��������Ŀά��</p>
 *
 * <p>Description:�ٴ�·��������Ŀά�� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author luhai
 * @version 1.0
 */
public class CLPEVLStandmControl extends TControl {
    //��¼ѡ������
    private int selectRow = -1;
    private int selectTypeLeft = -1;
    private int selectTypeRight = -1;
    public CLPEVLStandmControl() {
    }

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        initPage();
    }

    /**
     * ҳ���ʼ��
     */
    public void initPage() {
        callFunction("UI|CLP_EVL_STANDM_TABLE|addEventListener",
                     "CLP_EVL_STANDM_TABLE->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        callFunction("UI|CLP_EVL_STANDD_TABLE_LEFT|addEventListener",
                     "CLP_EVL_STANDD_TABLE_LEFT->" + TTableEvent.CLICKED, this,
                     "onTableLeftClicked");
        callFunction("UI|CLP_EVL_STANDD_TABLE_RIGHT|addEventListener",
                     "CLP_EVL_STANDD_TABLE_RIGHT->" + TTableEvent.CLICKED, this,
                     "onTableRightClicked");
        //������ϸ���ఴť����
        TButton TYPE_BTN = (TButton)this.getComponent("TYPE_BTN");

        callFunction("UI|CLP_EVL_STANDM_TABLE|addEventListener",
                     "CLP_EVL_STANDM_TABLE->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        onQuery();
    }

    /**
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {
        if (row < 0) {
            return;
        }
        TParm data = (TParm) callFunction(
                "UI|CLP_EVL_STANDM_TABLE|getParmValue");
        setValueForParm(
                "CLNCPATH_CODE;EVL_CODE;EVL_CHN_DESC;DESCRIPTION;EVL_ENG_DESC;SEQ;PY1",
                data, row);
        String str = data.getValue("SEQ", row);
        this.setValue("SEQ", str);
        setPrimaryKeyEnabled(false);
        //��ѯ��Ӧ��������Ϣ
        TParm typeListTParm = CLPEVLStanddTool.getInstance().selectData(data.
                getRow(row));
        TParm rightmoveParm = new TParm();
        int totalrowcount = typeListTParm.getCount();
        for (int i = 0; i < totalrowcount; i++) {
            rightmoveParm.setData("CAT3_CODE", i,
                                  typeListTParm.getData("CAT3_CODE", i));
            rightmoveParm.setData("CAT3_CHN_DESC", i,
                                  typeListTParm.getData("CAT3_CHN_DESC", i));
            rightmoveParm.setData("SCORE", i, typeListTParm.getData("SCORE", i));
            rightmoveParm.setData("CAT1_CODE", i,
                                  typeListTParm.getData("CAT1_CODE", i));
            rightmoveParm.setData("CAT2_CODE", i,
                                  typeListTParm.getData("CAT2_CODE", i));
        }

        //SYSTEM.OUT.PRINTln("���Ҳำ��parm:" + typeListTParm);
        this.callFunction("UI|CLP_EVL_STANDD_TABLE_RIGHT|setParmValue",
                          rightmoveParm);
        //��ʼ�����ͻ�����Ϣ
        typeListInfoInit();
        selectRow = row;
    }

    /**
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableLeftClicked(int row) {
        if (row < 0) {
            return;
        }
//        TParm data = (TParm) callFunction(
//                "UI|CLP_EVL_STANDD_TABLE_LEFT|getParmValue");
        selectTypeLeft = row;
    }

    /**
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableRightClicked(int row) {
        if (row < 0) {
            return;
        }
//        TParm data = (TParm) callFunction(
//                "UI|CLP_EVL_STANDD_TABLE_RIGHT|getParmValue");
        selectTypeRight = row;
    }

    /**
     * ��ʼ������ϸ���෽��
     */
    public void showTypeList() {
        //SYSTEM.OUT.PRINTln("��������ϸ����");
        TParm parm = new TParm();
        this.putBasicSysInfoIntoParm(parm);
        TParm result = CLPEVLCat3Tool.getInstance().selectData(parm);
        this.callFunction("UI|CLP_EVL_STANDD_TABLE_LEFT|setParmValue", result);
    }

    /**
     * ҳ���ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        putParamWithObjNameForQuery("CLNCPATH_CODE", parm);
        putParamLikeWithObjName("EVL_CODE", parm);
        putParamLikeWithObjName("EVL_CHN_DESC", parm);
//        putParamLikeWithObjName("EVL_ENG_DESC", parm);
//        putParamLikeWithObjName("DESCRIPTION", parm);
//        putParamLikeWithObjName("PY1", parm);
//        putParamWithObjNameForQuery("SEQ", parm);
        this.putBasicSysInfoIntoParm(parm);
        TParm result = CLPEVLStandmTool.getInstance().selectData(parm);
        this.callFunction("UI|CLP_EVL_STANDM_TABLE|setParmValue", result);
        setPrimaryKeyEnabled(true);
    }

    /**
     * ��շ���
     */
    public void onClear() {
        this.setValue("CLNCPATH_CODE", "");
        this.setValue("EVL_CODE", "");
        this.setValue("EVL_CHN_DESC", "");
        this.setValue("EVL_ENG_DESC", "");
        this.setValue("DESCRIPTION", "");
        this.setValue("SEQ", "");
        this.setValue("PY1", "");
        this.callFunction("UI|CLP_EVL_STANDD_TABLE_RIGHT|setParmValue",
                          new TParm());
        setPrimaryKeyEnabled(true);
        //��ʼ��������Ϣ
        typeListInfoInit();
        onQuery();
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        selectRow = this.getSelectedRow("CLP_EVL_STANDM_TABLE");
        if (selectRow == -1) {
            this.messageBox("��ѡ����Ҫɾ��������");
            return;
        }
        if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 2) == 0) {
            TTable table = (TTable)this.getComponent("CLP_EVL_STANDM_TABLE");
            int selRow = table.getSelectedRow();
            TParm tableParm = table.getParmValue();
            String CLNCPATH_CODE = tableParm.getValue("CLNCPATH_CODE", selRow);
            String EVL_CODE = tableParm.getValue("EVL_CODE", selRow);
            TParm parm = new TParm();
            parm.setData("CLNCPATH_CODE", CLNCPATH_CODE);
            parm.setData("EVL_CODE", EVL_CODE);
            //SYSTEM.OUT.PRINTln("delete operation ----------------");

            TParm result = TIOM_AppServer.executeAction(
                    "action.clp.CLPEVLStandmAction",
                    "deleteCLPEVLStandm", parm);
            // TParm result = CLPEVLStandmTool.getInstance().deleteData(parm);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //ɾ��������ʾ
            int row = (Integer) callFunction(
                    "UI|CLP_EVL_STANDM_TABLE|getSelectedRow");
            if (row < 0) {
                return;
            }
            this.callFunction("UI|CLP_EVL_STANDM_TABLE|removeRow", row);
            this.callFunction("UI|CLP_EVL_STANDM_TABLE|setSelectRow", row);
            this.messageBox("P0003");
        } else {
            return;
        }
    }

    /**
     * ���淽��
     */
    public void onSave() {
        //SYSTEM.OUT.PRINTln("���淽��ִ��");
        if (!validData()) {
            return;
        }
        TParm param = getBasicDataTparm();
        TParm rightTableParm = getTypeListTableData(param);
        TParm allParm = new TParm();
        allParm.setData("basicInfoParm", param.getData());
        allParm.setData("typeList", rightTableParm.getData());
        TParm returnParam = CLPEVLStandmTool.getInstance().checkDataExist(param);
        int dataCount = returnParam.getInt("DATACOUNT", 0);
        //SYSTEM.OUT.PRINTln("--------��ѯ��������" + dataCount + "------------");
        if (dataCount > 0) {
            //SYSTEM.OUT.PRINTln("data exist sys will update");
            //SYSTEM.OUT.PRINTln("ִ�и������ݷ�����ʼ����");
            TParm result = TIOM_AppServer.executeAction(
                    "action.clp.CLPEVLStandmAction",
                    "updateCLPEVLStandm", allParm);
            boolean flag = false;
            if (result.getErrCode() >= 0) {
                flag = true;
            }
            String msg = flag ? "�༭�ɹ�" : "�༭ʧ��";
            this.messageBox("��ʾ", msg, -1);
        } else {
            //SYSTEM.OUT.PRINTln("data not exist sys will insert data ");
            //���ʱ�����ݽ��д���
            param.setData("REGION_CODE", Operator.getRegion());
            param.setData("OPT_USER", Operator.getID());
            Timestamp today = SystemTool.getInstance().getDate();
            String datestr = StringTool.getString(today, "yyyyMMdd");
            param.setData("OPT_DATE", datestr);
            param.setData("OPT_TERM", Operator.getIP());
            TParm result = TIOM_AppServer.executeAction(
                    "action.clp.CLPEVLStandmAction",
                    "insertCLPEVLStandm", allParm);
//            TParm resultParam = CLPCHKItemTool.getInstance().insertData(param);
            TParm resultParam = TIOM_AppServer.executeAction(
                    "action.clp.CLPEVLStandmAction",
                    "insertCLPEVLStandm", allParm);
            boolean flag = false;
            if (result.getErrCode() >= 0) {
                flag = true;
            }
            String msg = flag ? "��ӳɹ�" : "���ʧ��";
            this.messageBox("��ʾ", msg, -1);
        }
        //ˢ������
        onQuery();
    }

    /**
     * �õ����ͼ���TParm
     * @param basicDataTParm TParm
     * @return TParm
     */
    private TParm getTypeListTableData(TParm basicDataTParm) {
        TTable rightTable = (TTable)this.getComponent(
                "CLP_EVL_STANDD_TABLE_RIGHT");
        TParm rightTableParm = rightTable.getParmValue();
        if (rightTableParm == null) {
            rightTableParm = new TParm();
        }
        //�����Ҳ�������
        int rowcount = rightTableParm.getCount();
        for (int i = 0; i < rowcount; i++) {
            //���ʱ�����ݽ��д���
            rightTableParm.setData("OPT_USER", i, Operator.getID());
            rightTableParm.setData("OPT_TERM", i, Operator.getIP());
            Timestamp today = SystemTool.getInstance().getDate();
            String currentTime = StringTool.getString(today, "yyyyMMdd");
            rightTableParm.setData("OPT_DATE", i, currentTime);
            //���������������
            rightTableParm.setData("CLNCPATH_CODE", i,
                                   basicDataTParm.getData("CLNCPATH_CODE"));
            rightTableParm.setData("EVL_CODE", i,
                                   basicDataTParm.getData("EVL_CODE"));
        }
        //SYSTEM.OUT.PRINTln("�����ı������-----------:" + rightTableParm);
        //�����Ҳ������ݽ���
        return rightTableParm;
    }

    /**
     * �õ�����������Ϣ
     * @return TParm
     */
    private TParm getBasicDataTparm() {
        TParm param = new TParm();
        putParamWithObjName("CLNCPATH_CODE", param);
        putParamWithObjName("EVL_CODE", param);
        putParamWithObjName("EVL_CHN_DESC", param);
        putParamWithObjName("EVL_ENG_DESC", param);
        putParamWithObjName("DESCRIPTION", param);
        putParamWithObjName("PY1", param);
        putParamWithObjName("SEQ", param);
        //�ж�˳����Ƿ�Ϊ�գ����Ϊ����Ҫ���д����������ݿ�SEQ���ֵ��1
        //SYSTEM.OUT.PRINTln("form SEQ " + this.getValueString("SEQ"));
        if (this.getValueString("SEQ") == null ||
            this.getValueString("SEQ").length() <= 0) {
            TParm maxSEQResult = CLPEVLStandmTool.getInstance().getMaxSEQ();
            int SEQNumber = Integer.parseInt(maxSEQResult.getData("MAXSEQ",
                    0) +
                                             "") + 1;
            param.setData("SEQ", SEQNumber);
        }
        return param;

    }

    /**
     * ����ϸ��������
     */
    public void TypeLeftMove() {
        selectTypeRight = getSelectedRow("CLP_EVL_STANDD_TABLE_RIGHT");
        if (selectTypeRight < 0) {
            this.messageBox("��ʾ", "��ѡ���ƶ�����", -1);
            return;
        }
        TParm rightParm = (TParm) callFunction(
                "UI|CLP_EVL_STANDD_TABLE_RIGHT|getParmValue");
        TTable leftTable = (TTable)this.getComponent(
                "CLP_EVL_STANDD_TABLE_LEFT");
        TParm paramLeft = leftTable.getParmValue();
        if (paramLeft == null) {
            paramLeft = new TParm();
        }
        boolean flag = false;
        Vector leftNameVector = paramLeft.getVector("CAT3_CODE");
        Iterator itr = leftNameVector.iterator();
        while (itr.hasNext()) {
            Vector rowVector = (Vector) itr.next();
            boolean flag2 = rowVector.contains(rightParm.getValue("CAT3_CODE",
                    selectTypeRight));
            if (flag2)
                flag = true;
        }
        //SYSTEM.OUT.PRINTln("���:" + flag);
        //����б��������޸�begin
//        if (!flag) {
//            paramLeft.addRowData(rightParm, selectTypeRight);
//            leftTable.setParmValue(paramLeft);
//        }
        //����б��������޸�end
        //�Ҳ�tableɾ������
        TTable tTableRight = (TTable)this.getComponent(
                "CLP_EVL_STANDD_TABLE_RIGHT");
        //tTableRight.removeRow(selectTypeRight);
        TParm parm = tTableRight.getParmValue();
        selectTypeRight = getSelectedRow("CLP_EVL_STANDD_TABLE_RIGHT");
        //SYSTEM.OUT.PRINTln("�Ҳ�parm" + parm + "�кţ�" + selectTypeRight);
        parm.removeRow(selectTypeRight);
        //SYSTEM.OUT.PRINTln("---------------����  1");
        tTableRight.setParmValue(parm);
        //SYSTEM.OUT.PRINTln("�Ҳ�ɾ�����Tparam:" + tTableRight.getParmValue());
        //ѡ�����-1
        clearTypeTableSelectedRow();
        //��ʼ��������Ϣ
        typeListInfoInit();
    }

    /**
     * ��������ʾѡ������Ϣ
     */
    private void clearTypeTableSelectedRow() {
        selectTypeLeft = -1;
        selectTypeRight = -1;
        TTable rightTableview = (TTable)this.getComponent(
                "CLP_EVL_STANDD_TABLE_RIGHT");
//        rightTableview.setSelectedRow(-1);
        TTable leftTableview = (TTable)this.getComponent(
                "CLP_EVL_STANDD_TABLE_LEFT");
//        leftTableview.setSelectedRow(-1);
        //���ѡ�з���
        leftTableview.clearSelection();
        rightTableview.clearSelection();

    }

    /**
     * ����ϸ��������
     */
    public void TypeRightMove() {
        selectTypeLeft = getSelectedRow("CLP_EVL_STANDD_TABLE_LEFT");
        if (selectTypeLeft < 0) {
            this.messageBox("��ʾ", "��ѡ���ƶ�����", -1);
            return;
        }
        TParm leftParm = (TParm) callFunction(
                "UI|CLP_EVL_STANDD_TABLE_LEFT|getParmValue");
        TTable rightTable = (TTable)this.getComponent(
                "CLP_EVL_STANDD_TABLE_RIGHT");
        TParm paramRight = rightTable.getParmValue();
        if (paramRight == null) {
            paramRight = new TParm();
        }
        boolean flag = false;
        Vector rightNameVector = paramRight.getVector("CAT3_CODE");
        Iterator itr = rightNameVector.iterator();
        while (itr.hasNext()) {
            Vector rowVector = (Vector) itr.next();
            boolean flag2 = rowVector.contains(leftParm.getValue("CAT3_CODE",
                    selectTypeLeft));
            if (flag2)
                flag = true;
        }
        //SYSTEM.OUT.PRINTln("���:" + flag);
        if (!flag) {
            TParm moveData = new TParm();
            moveData.setData("CAT3_CODE", 0,
                             leftParm.getValue("CAT3_CODE", selectTypeLeft));
            moveData.setData("CAT3_CHN_DESC", 0,
                             leftParm.getValue("CAT3_CHN_DESC", selectTypeLeft));
            moveData.setData("SCORE", 0,
                             leftParm.getValue("SCORE", selectTypeLeft));
            moveData.setData("CAT1_CODE", 0,
                             leftParm.getValue("CAT1_CODE", selectTypeLeft));
            moveData.setData("CAT2_CODE", 0,
                             leftParm.getValue("CAT2_CODE", selectTypeLeft));
            paramRight.addRowData(moveData, 0);
            rightTable.setParmValue(paramRight);
//            //���tableɾ������
//            TTable tTableLeft = (TTable)this.getComponent(
//                    "CLP_EVL_STANDD_TABLE_LEFT");
//            TParm parm = tTableLeft.getParmValue();
//            parm.removeRow(selectTypeLeft);
//            tTableLeft.setParmValue(parm);
//           // tTableLeft.removeRow(selectTypeLeft);
//            //SYSTEM.OUT.PRINTln("���ɾ�����Tparam:" + tTableLeft.getParmValue());
        }
        clearTypeTableSelectedRow();
        //��ʼ��������Ϣ
        typeListInfoInit();
    }

    /**
     * ��������Ϣ��ʼ��
     */
    public void typeListInfoInit() {

        TTable table = (TTable)this.getComponent("CLP_EVL_STANDD_TABLE_RIGHT");
        //SYSTEM.OUT.PRINTln("��������Ϣ��ʼ��");
        int rowcount = table.getParmValue().getCount();
        rowcount = rowcount < 0 ? 0 : rowcount;
        this.setValue("TOTAL_INPUT", rowcount + "");
        //�����ܷ�ֵ
        double total = 0;
        TParm rightParm = table.getParmValue() == null ? new TParm() :
                          table.getParmValue();
        Vector scoreVector = rightParm.getVector("SCORE");
        Iterator itr = scoreVector.iterator();
        while (itr.hasNext()) {
            Vector scoreVectortmp = (Vector) itr.next();
            //SYSTEM.OUT.PRINTln("doubless" +
                         //      Double.parseDouble(scoreVectortmp.get(0) + ""));
            total += Double.parseDouble(scoreVectortmp.get(0) + "");
        }
        this.setValue("TOTAL_SCORE_INPUT", total + "");
    }

    /**
     * ������֤����
     * @return boolean
     */
    private boolean validData() {
        if (!checkComponentNullOrEmpty("CLNCPATH_CODE")) {
            this.messageBox("��ѡ�������ٴ�·��!");
            return false;
        }
        if (!this.emptyTextCheck("EVL_CODE,EVL_CHN_DESC")) {
            return false;
        }
        String seq = this.getValueString("SEQ");
        if (seq != null && seq.length() > 0) {
            Pattern p = Pattern.compile("[0-9]{1,}");
            Matcher match = p.matcher(seq);
            if (!match.matches()) {
                this.messageBox("˳�������������!");
                return false;
            }
        }
        return true;
    }

    /**
     * ���������Ƿ�����
     * @param flag boolean
     */
    private void setPrimaryKeyEnabled(boolean flag) {
        TTextFormat tTextFormat = (TTextFormat)this.getComponent(
                "CLNCPATH_CODE");
        tTextFormat.setEnabled(flag);
        TTextField EVL_CODE_TTextField = (TTextField)this.getComponent(
                "EVL_CODE");
        EVL_CODE_TTextField.setEnabled(flag);
    }

    /**
     * ���ؼ�ֵ����TParam����(���Դ�����ò���ֵ)
     * @param objName String
     */
    private void putParamWithObjName(String objName, TParm parm,
                                     String paramName) {
        String objstr = this.getValueString(objName);
        objstr = objstr;
        parm.setData(paramName, objstr);
    }

    /**
     * ���ؼ�ֵ����TParam����(���ò���ֵ��ؼ�����ͬ)
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        //SYSTEM.OUT.PRINTln(objstr);
        objstr = objstr;
        //����ֵ��ؼ�����ͬ
        parm.setData(objName, objstr);
    }

    /**
     * ���ؼ�ֵ����TParam����(���Դ�����ò���ֵ)
     * @param objName String
     */
    private void putParamLikeWithObjName(String objName, TParm parm,
                                         String paramName) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr + "%";
            parm.setData(paramName, objstr);
        }

    }

    /**
     * ���ؼ�ֵ����TParam����(���ò���ֵ��ؼ�����ͬ)
     * @param objName String
     * @param parm TParm
     */
    private void putParamLikeWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr + "%";
            //����ֵ��ؼ�����ͬ
            parm.setData(objName, objstr);
        }
    }

    /**
     * ���ڷ���������ȫƥ����в�ѯ�Ŀؼ�
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjNameForQuery(String objName, TParm parm) {
        putParamWithObjNameForQuery(objName, parm, objName);
    }

    /**
     * ���ڷ���������ȫƥ����в�ѯ�Ŀؼ�
     * @param objName String
     * @param parm TParm
     * @param paramName String
     */
    private void putParamWithObjNameForQuery(String objName, TParm parm,
                                             String paramName) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            //����ֵ��ؼ�����ͬ
            parm.setData(paramName, objstr);
        }
    }

    /**
     * ���ؼ��Ƿ�Ϊ��
     * @param componentName String
     * @return boolean
     */
    private boolean checkComponentNullOrEmpty(String componentName) {
        if (componentName == null || "".equals(componentName)) {
            return false;
        }
        String valueStr = this.getValueString(componentName);
        if (valueStr == null || "".equals(valueStr)) {
            return false;
        }
        return true;
    }

    /**
     * �õ�ָ��table��ѡ����
     * @param tableName String
     * @return int
     */
    private int getSelectedRow(String tableName) {
        int selectedIndex = -1;
        if (tableName == null || tableName.length() <= 0) {
            return -1;
        }
        Object componentObj = this.getComponent(tableName);
        if (!(componentObj instanceof TTable)) {
            return -1;
        }
        TTable table = (TTable) componentObj;
        selectedIndex = table.getSelectedRow();
        return selectedIndex;
    }

    /**
     * ������֤����
     * @param validData String
     * @return boolean
     */
    private boolean validNumber(String validData) {
        Pattern p = Pattern.compile("[0-9]{1,}");
        Matcher match = p.matcher(validData);
        if (!match.matches()) {
            return false;
        }
        return true;
    }

    /**
     * ��TParm�м���ϵͳĬ����Ϣ
     * @param parm TParm
     */
    private void putBasicSysInfoIntoParm(TParm parm) {
        int total = parm.getCount();
        //SYSTEM.OUT.PRINTln("total" + total);
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM",  Operator.getIP());
    }


}


