package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import java.util.regex.Pattern;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import java.util.Map;
import com.dongyang.data.TNull;
import java.util.HashMap;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.util.regex.Matcher;
import java.sql.Timestamp;
import com.dongyang.ui.TCheckBox;
import jdo.clp.CLPNursOrderTool;
import java.util.List;
import java.util.ArrayList;
import com.dongyang.util.TMessage;

/**
 * <p>Title: �����ֵ�</p>
 *
 * <p>Description:�����ֵ� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPNursOrderControl extends TControl {
    //��¼ѡ������
    int selectRow = -1;
    public CLPNursOrderControl() {
    }

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        initPage();
        //SYSTEM.OUT.PRINTln("�������ҳ�����");
        this.onQuery();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
    }

    /**
     * ��ʼ��ҳ��
     */
    private void initPage() {

    }

    /**
     * ҳ���ѯ����
     */
    public void onQuery() {
        setPrimaryKeyEnabled(true);
        if(!validQuery()){
            return;
        }
        TParm selectParm = getSelectedCondition();
        TParm result = CLPNursOrderTool.getInstance().selectData(selectParm);
        this.callFunction("UI|Table|setParmValue", result);
    }

    /**
     * ��շ���
     */
    public void onClear() {
        List<String> list = new ArrayList();
        list.add("ORDER_CODE");
        list.add("PY2");
        list.add("ORDER_CHN_DESC");
        list.add("PY1");
        list.add("ORDER_ENG_DESC");
        list.add("UNIT");
        list.add("FREQ");
        list.add("AMOUNT");
        list.add("TYPE_CODE");
        list.add("CHKTYPE_CODE");
        list.add("SEQ");
        list.add("DESCRIPTION");
        clearInput(list);
        this.callFunction("UI|DEL_FLG|setSelected",false);
        setPrimaryKeyEnabled(true);
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        selectRow = this.getSelectedRow("Table");
        if (selectRow == -1) {
            this.messageBox("��ѡ����Ҫɾ��������");
            return;
        }
        if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 2) == 0) {
            TTable table = (TTable)this.getComponent("Table");
            int selRow = table.getSelectedRow();
            TParm tableParm = table.getParmValue();
            String order_code = tableParm.getValue("ORDER_CODE", selRow);
            TParm parm = new TParm();
            parm.setData("ORDER_CODE", order_code);
            TParm result = CLPNursOrderTool.getInstance().deleteData(parm);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //ɾ��������ʾ
            int row = (Integer) callFunction("UI|Table|getSelectedRow");
            if (row < 0) {
                return;
            }
            this.callFunction("UI|Table|removeRow", row);
            this.callFunction("UI|Table|setSelectRow", row);
            this.messageBox("P0003");
        } else {
            return;
        }
    }

    /**
     * ���淽��
     */
    public void onSave() {
        //SYSTEM.OUT.PRINTln("���水ťִ��");
        //��֤����
        if (!validBasicData()) {
            return;
        }
        //�õ���������
        //��������ƴ������
        //����ƴ�������Զ���������
        //����ƴ��1
        setValue("PY1", TMessage.getPy(getValueString("ORDER_CHN_DESC")));
        TParm parm = new TParm();
        this.putParamWithObjName("ORDER_CODE", parm);
        this.putParamWithObjName("PY2", parm);
        this.putParamWithObjName("ORDER_CHN_DESC", parm);
        this.putParamWithObjName("PY1", parm);
        this.putParamWithObjName("ORDER_ENG_DESC", parm);
        this.putParamWithObjName("UNIT", parm);
        this.putParamWithObjName("FREQ", parm);
        this.putParamWithObjName("AMOUNT", parm);
        this.putParamWithObjName("TYPE_CODE", parm);
        this.putParamWithObjName("CHKTYPE_CODE", parm);
        this.putParamWithObjName("SEQ", parm);
        this.putParamWithObjName("DESCRIPTION", parm);
        boolean flag=(Boolean)this.callFunction("UI|DEL_FLG|isSelected");
        if(flag){
            parm.setData("DEL_FLG","Y");
        }else{
            parm.setData("DEL_FLG","N");
        }
        this.putBasicSysInfoIntoParm(parm);
        //�ж������Ƿ����
        TParm result = CLPNursOrderTool.getInstance().checkDataExist(parm);
        boolean isdataExist = Integer.parseInt(result.getValue("DATACOUNT", 0)) >
                              0 ? true : false;
        TParm resultopt = null;
        //����seq
        if ("".equals(parm.getValue("SEQ"))) {
            TNull tnull = new TNull(String.class);
            parm.setData("SEQ", tnull);
        }
        if (isdataExist) {
            //SYSTEM.OUT.PRINTln("���ݴ��ڣ�����----------");
            resultopt = CLPNursOrderTool.getInstance().updateData(parm);
            if (resultopt.getErrCode() >= 0) {
                this.messageBox("P0001");
            } else {
                this.messageBox("E0001");
            }
        } else {
            //SYSTEM.OUT.PRINTln("���ݲ����ڣ�����--------");
            resultopt = CLPNursOrderTool.getInstance().insertData(parm);
            if (resultopt.getErrCode() >= 0) {
                this.messageBox("P0002");
            } else {
                this.messageBox("E0002");
            }
        }
        this.onQuery();
    }
    /**
     * ��֤����
     * @return boolean
     */
    private boolean validBasicData(){
        if (!this.emptyTextCheck("ORDER_CODE,ORDER_CHN_DESC,AMOUNT")) {
            return false;
        }
        String amount = this.getValueString("AMOUNT");
        if (!this.validDouble(amount)) {
            this.messageBox("������Ϸ�����!");
            return false;
        }
        if("".equals(getValue("UNIT"))){
            this.messageBox("��ѡ��λ!");
            return false;
        }
        if ("".equals(getValue("FREQ"))) {
            this.messageBox("��ѡ��Ƶ��!");
            return false;
        }
        if ("".equals(getValue("TYPE_CODE"))) {
            this.messageBox("��ѡ�������!");
            return false;
        }
        if ("".equals(getValue("CHKTYPE_CODE"))) {
            this.messageBox("��ѡ�������!");
            return false;
        }

        //��˳��Ŵ���ʱ��֤����
        if (this.checkNullAndEmpty(this.getValueString("SEQ")) &&
            !this.validNumber(this.getValueString("SEQ"))) {
            this.messageBox("˳�������������");
            return false;
        }
        return true;

    }
    /**
     * ��ѯʱ��֤
     * @return boolean
     */
    private boolean validQuery(){
//        //��˳��Ŵ���ʱ��֤����
//        if (this.checkNullAndEmpty(this.getValueString("SEQ")) &&
//            !this.validNumber(this.getValueString("SEQ"))) {
//            this.messageBox("˳�������������");
//            return false;
//        }
//        String amount =this.getValueString("AMOUNT");
//        if(this.checkNullAndEmpty(amount)&&!this.validDouble(amount)){
//            this.messageBox("������Ϸ�����");
//            return false;
//        }
        return true;
    }
    /**
     * ��ҳ��õ���ѯ��������
     */
    private TParm getSelectedCondition() {
        TParm selectedCondition = new TParm();
        putParamLikeWithObjName("ORDER_CODE", selectedCondition);
//        putParamLikeWithObjName("PY2", selectedCondition);
        putParamLikeWithObjName("ORDER_CHN_DESC", selectedCondition);
//        putParamLikeWithObjName("ORDER_ENG_DESC", selectedCondition);
//        putParamWithObjNameForQuery("UNIT", selectedCondition);
//        putParamWithObjNameForQuery("FREQ", selectedCondition);
//        putParamWithObjNameForQuery("AMOUNT", selectedCondition);
//        putParamWithObjNameForQuery("TYPE_CODE", selectedCondition);
//        TCheckBox b = new TCheckBox();
//        boolean delFlg=(Boolean) this.callFunction("UI|DEL_FLG|isSelected");
//        //SYSTEM.OUT.PRINTln("ɾ�����-----------"+delFlg);
//        if(delFlg){
//            selectedCondition.setData("DEL_FLG","Y");
//        }
//        putParamWithObjNameForQuery("CHKTYPE_CODE", selectedCondition);
//        putParamWithObjNameForQuery("ORDTYPE_CODE", selectedCondition);
//        putParamWithObjNameForQuery("CLP_RATE", selectedCondition);
//        putParamWithObjNameForQuery("CLP_UNIT", selectedCondition);
//        putParamWithObjNameForQuery("DESCRIPTION", selectedCondition);
//        putParamLikeWithObjName("SEQ", selectedCondition);
        selectedCondition.setData("REGION_CODE", Operator.getRegion());
        return selectedCondition;
    }

    /**
     * ���ÿؼ��Ƿ���÷���
     * @param flag boolean
     */
    private void setPrimaryKeyEnabled(boolean flag) {
        TTextField tTextField = (TTextField)this.getComponent("ORDER_CODE");
        tTextField.setEnabled(flag);
    }

    /**
     * �����Ķ�Ӧ��Ԫ�����óɿ�д�����������óɲ���д
     * @param tableName String
     * @param rowNum int
     * @param columnNum int
     */
    private void setTableEnabled(String tableName, int rowNum, int columnNum) {
        TTable table = (TTable)this.getComponent(tableName);
        int totalColumnMaxLength = table.getColumnCount();
        int totalRowMaxLength = table.getRowCount();
        //SYSTEM.OUT.PRINTln("��������" + totalColumnMaxLength + "������:" +
         //                  totalRowMaxLength);
        //����
        String lockColumnStr = "";
        for (int i = 0; i < totalColumnMaxLength; i++) {
            if (!(i + "").equals(columnNum + "")) {
                lockColumnStr += i + ",";
            }
        }
        lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
        //SYSTEM.OUT.PRINTln("���д���" + lockColumnStr);
        table.setLockColumns(lockColumnStr);
        //����
        String lockRowStr = "";
        for (int i = 0; i < totalRowMaxLength; i++) {
            if (!(i + "").equals(rowNum + "")) {
                lockRowStr += i + ",";
            }
        }
        //SYSTEM.OUT.PRINTln("���д�ǰ��" + lockRowStr + "����" + totalRowMaxLength);
        lockRowStr = lockRowStr.substring(0,
                                          ((lockRowStr.length() - 1) < 0 ? 0 :
                                           (lockRowStr.length() - 1)));
        //SYSTEM.OUT.PRINTln("���д���" + lockRowStr);
        if (lockRowStr.length() > 0) {
            table.setLockRows(lockRowStr);
        }

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
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {
        if (row < 0) {
            return;
        }
        TParm data = (TParm) callFunction("UI|Table|getParmValue");
        setValueForParm(
                "ORDER_CODE;ORDER_CHN_DESC;PY1;PY2;ORDER_ENG_DESC;UNIT;FREQ;AMOUNT;TYPE_CODE;CHKTYPE_CODE;DESCRIPTION;SEQ;DEL_FLG",
                data, row);
        String str = data.getValue("SEQ", row);
        this.setValue("SEQ", str);
        String AMOUNT = data.getValue("AMOUNT", row);
        this.setValue("AMOUNT", AMOUNT);
        setPrimaryKeyEnabled(false);
        selectRow = row;
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
            objstr = "%" + objstr.trim() + "%";
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
            parm.setData(paramName, objstr.trim());
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
     * ������֤����
     * @param validData String
     * @return boolean
     */
    private boolean validDouble(String validData) {
        Pattern p = Pattern.compile("[0-9]{1,}([.][0-9]{1,}){0,1}");
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
        parm.setData("OPT_TERM", Operator.getIP());
    }

    /**
     * ����Operator�õ�map
     * @return Map
     */
    private Map getBasicOperatorMap() {
        Map map = new HashMap();
        map.put("REGION_CODE", Operator.getRegion());
        map.put("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        map.put("OPT_DATE", datestr);
        map.put("OPT_TERM", Operator.getIP());
        return map;
    }

    /**
     * �õ���ǰʱ���ַ�������
     * @param dataFormatStr String
     * @return String
     */
    private String getCurrentDateStr(String dataFormatStr) {
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, dataFormatStr);
        return datestr;
    }

    /**
     * �õ���ǰʱ���ַ�������
     * @return String
     */
    private String getCurrentDateStr() {
        return getCurrentDateStr("yyyyMMdd");
    }

    /**
     * ����Ƿ�Ϊ�ջ�մ�
     * @return boolean
     */
    private boolean checkNullAndEmpty(String checkstr) {
        if (checkstr == null) {
            return false;
        }
        if ("".equals(checkstr)) {
            return false;
        }
        return true;
    }

    /**
     * ����TParm
     * @param from TParm
     * @param to TParm
     * @param row int
     */
    private void cloneTParm(TParm from, TParm to, int row) {
        for (int i = 0; i < from.getNames().length; i++) {
            to.addData(from.getNames()[i],
                       from.getValue(from.getNames()[i], row));
        }
    }

    /**
     * ��¡����
     * @param parm TParm
     * @return TParm
     */
    private TParm cloneTParm(TParm from) {
        TParm returnTParm = new TParm();
        for (int i = 0; i < from.getNames().length; i++) {
            returnTParm.setData(from.getNames()[i],
                                from.getValue(from.getNames()[i]));
        }
        return returnTParm;
    }

    /**
     * ����TParm ���null�ķ���
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNullVector(TParm parm, String keyStr, Class type) {
        for (int i = 0; i < parm.getCount(); i++) {
            if (parm.getData(keyStr, i) == null) {
                //SYSTEM.OUT.PRINTln("����Ϊ�����");
                TNull tnull = new TNull(type);
                parm.setData(keyStr, i, tnull);
            }
        }
    }

    /**
     * ����TParm ��nullֵ����
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNull(TParm parm, String keyStr, Class type) {
        if (parm.getData(keyStr) == null) {
            //SYSTEM.OUT.PRINTln("����Ϊ�����");
            TNull tnull = new TNull(type);
            parm.setData(keyStr, tnull);
        }
    }

    /**
     * ��ռ����ж�Ӧ��������ֵ
     * @param inputNames List
     */
    private void clearInput(List<String> inputNames){
        for(String inputstr : inputNames){
            this.setValue(inputstr,"");
        }
    }

}
