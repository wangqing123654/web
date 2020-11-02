package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.data.TParm;
import java.awt.event.KeyEvent;
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
import jdo.sys.PatTool;
import jdo.clp.CLPEvaluateTool;

/**
 * <p>Title: �ٴ�·������ִ��</p>
 *
 * <p>Description: �ٴ�·������ִ��</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author luhai
 * @version 1.0
 */
public class CLPEvaluateSelectPatientControl extends TControl {
    public CLPEvaluateSelectPatientControl() {

    }

    /**
     * ҳ���ʼ������
     */
    public void onInit() {
        super.onInit();
        initPage();
    }
    /**
     * ��ʼ��ҳ��
     */
    private void initPage(){
        //�󶨿ؼ��¼�
        callFunction("UI|MR_NO|addEventListener",
             "MR_NO->" + TKeyListener.KEY_RELEASED, this,
             "onKeyReleased");

    }
    //�ؼ�����¼�
    public void onKeyReleased(KeyEvent e){
        if (e.getKeyCode() != 10) {
            return;
        }
        //���Ҳ�����Ϣ
        this.selectPatientBasicInfo();
        //��ѯ���˵�����ִ����Ϣ
        this.onQuery();
    }

    /**
     * ��ѯ���˻�����Ϣ
     */
    private void selectPatientBasicInfo(){
        String mr_no = this.getValueString("MR_NO");
        //�Զ�����mr_no
        mr_no = PatTool.getInstance().checkMrno(mr_no);
        TParm selectParm = new TParm();
        selectParm.setData("MR_NO",mr_no);
        selectParm.setData("REGION_CODE",this.getBasicOperatorMap().get("REGION_CODE"));
        TParm patientTParm=CLPEvaluateTool.getInstance().selectPatientInfo(selectParm);
        if(patientTParm.getCount()>0){
            this.setValue("MR_NO",patientTParm.getValue("MR_NO",0));
            this.setValue("CLNCPATH_CODE",patientTParm.getValue("CLNCPATH_CODE",0));
            this.setValue("DEPT_CODE",patientTParm.getValue("DEPT_CODE",0));
            this.setValue("STATION_CODE",patientTParm.getValue("STATION_CODE",0));
            this.setValue("BED_NO",patientTParm.getValue("BED_NO",0));
            this.setValue("VS_DR_CODE",patientTParm.getValue("VS_DR_CODE",0));
            this.setValue("PAT_NAME",patientTParm.getValue("PAT_NAME",0));
        }
    }

    public void onClear(){
        this.clearText("MR_NO;CLNCPATH_CODE;DEPT_CODE;STATION_CODE;BED_NO;VS_DR_CODE;PAT_NAME");
        //��պ��ѯ
        this.onQuery();
    }
    /**
     * ��ѯ
     */
    public void onQuery(){
        TParm selectParm = getSelectPatientParm();
        TParm result=CLPEvaluateTool.getInstance().selectManagemWithPatientInfo(selectParm);
        //��ѯ����������ִ����Ϣ
        this.callFunction("UI|TABLE|setParmValue", result);
    }

    /**
     * ����
     */
    public void onEvaluation(){
        TParm sendParm = new TParm();
        //�õ�case_no
        int selectedRow= this.getSelectedRow("TABLE");
        if(selectedRow<0){
            this.messageBox("��ѡ������ִ������");
            return ;
        }
        TTable table = (TTable) this.getComponent("TABLE");
        String case_no= table.getParmValue().getRow(selectedRow).getValue("CASE_NO");
        sendParm.setData("CASE_NO",case_no);
        String mr_no= table.getParmValue().getRow(selectedRow).getValue("MR_NO");
        sendParm.setData("MR_NO",mr_no);
        String pat_name= table.getParmValue().getRow(selectedRow).getValue("PAT_NAME");
        sendParm.setData("PAT_NAME",pat_name);
        String sex_desc= table.getParmValue().getRow(selectedRow).getValue("SEX_DESC");
        sendParm.setData("SEX_DESC",sex_desc);
        String bed_no= table.getParmValue().getRow(selectedRow).getValue("BED_NO");
        sendParm.setData("BED_NO",bed_no);
        String VSDRCode= table.getParmValue().getRow(selectedRow).getValue("VS_DR_CODE");
        sendParm.setData("VS_DR_CODE",VSDRCode);
        String clncPathCode= table.getParmValue().getRow(selectedRow).getValue("CLNCPATH_CODE");
        sendParm.setData("CLNCPATH_CODE",clncPathCode);
        String inDate= table.getParmValue().getRow(selectedRow).getValue("IN_DATE");
        sendParm.setData("IN_DATE",inDate);
        String dsDate= table.getParmValue().getRow(selectedRow).getValue("DS_DATE");
        sendParm.setData("DS_DATE",dsDate);
        String resultstr = (String)this.openDialog(
                "%ROOT%\\config\\clp\\CLPEvaluation.x", sendParm);
    }
    /**
     * ��ѯ���˻�����Ϣ
     * @return TParm
     */
    private TParm getSelectPatientParm(){
        TParm selectParm = new TParm();
        this.putParamWithObjNameForQuery("MR_NO",selectParm);
        this.putParamWithObjNameForQuery("DEPT_CODE",selectParm);
        this.putParamWithObjNameForQuery("STATION_CODE",selectParm);
        this.putParamLikeWithObjName("BED_NO",selectParm);
        this.putParamWithObjNameForQuery("CLNCPATH_CODE",selectParm);
        this.putParamWithObjNameForQuery("VS_DR_CODE",selectParm);
        return selectParm;
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
        //����
        String lockColumnStr = "";
        for (int i = 0; i < totalColumnMaxLength; i++) {
            if (!(i + "").equals(columnNum + "")) {
                lockColumnStr += i + ",";
            }
        }
        lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
        table.setLockColumns(lockColumnStr);
        //����
        String lockRowStr = "";
        for (int i = 0; i < totalRowMaxLength; i++) {
            if (!(i + "").equals(rowNum + "")) {
                lockRowStr += i + ",";
            }
        }
        lockRowStr = lockRowStr.substring(0,
                                          ((lockRowStr.length() - 1) < 0 ? 0 :
                                           (lockRowStr.length() - 1)));
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
     * ���ؼ�ֵ����TParam����(���ò���ֵ��ؼ�����ͬ)
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
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
        String objstr = this.getValueString(objName).trim();
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
        Pattern p = Pattern.compile("[0-9]{1,2}([.][0-9]{1,2}){0,1}");
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
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM",  Operator.getIP());
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
            TNull tnull = new TNull(type);
            parm.setData(keyStr, tnull);
        }
    }

}
