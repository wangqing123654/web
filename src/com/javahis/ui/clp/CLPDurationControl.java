package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import java.util.regex.Pattern;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.util.regex.Matcher;
import java.sql.Timestamp;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import jdo.clp.CLPDurationTool;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.util.TMessage;
import com.dongyang.data.TNull;
import com.dongyang.ui.TCheckBox;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title:�ٴ�·��ʱ�� </p>
 *
 * <p>Description:�ٴ�·��ʱ�� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPDurationControl extends TControl {
    //��¼ѡ������
    int selectRow = -1;

    public CLPDurationControl() {

    }

    /**
     * ҳ���ʼ������
     */
    public void onInit() {
        super.onInit();
        initPage();
    }

    /**
     * ҳ���ʼ������
     */
    private void initPage() {
//        System.out.println("ҳ���ʼ����ʼ");
        //�󶨱���¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //��ʼ���ٴ�·��ʱ�����β˵�
        initTree();
        //��ʼ������
        onQuery();
    }

    /**
     * ��ʼ�����β˵�
     */
    private void initTree() {
        TTree tree = (TTree) callMessage("UI|TTree|getThis");
        //��ʼ��Tree�Ļ�����Ϣ
        //�õ�Tree�Ļ�������
        TParm selectTParm = new TParm();
        this.putBasicSysInfoIntoParm(selectTParm);
        TParm result = CLPDurationTool.getInstance().selectData(selectTParm);
//        System.out.println("��ѯ������������:" + result);
        TTreeNode root = (TTreeNode) callMessage("UI|TTree|getRoot");
        root.setText("�ٴ�·��ʱ��");
        root.setType("Root");
        root.setID("");
        root.removeAllChildren();
        //��ʼ��ʼ����������
        int count = result.getCount();
        for (int i = 0; i < count; i++) {
            TParm dataParm = result.getRow(i);
            putNodeInTree(dataParm,root);
        }
        tree.update();
    }

    private void putNodeInTree(TParm dataParm,TTreeNode root){
        String noteType = "Path"; //UI
        TTreeNode treeNode = new TTreeNode("KPILEAVE", noteType);
        treeNode.setText(dataParm.getValue("DURATION_CHN_DESC"));
        treeNode.setID(dataParm.getValue("DURATION_CODE"));
    //           treeNode.setName("name");
    //           treeNode.setValue("valuesss");
    String parentID = dataParm.getValue("PARENT_CODE");
//    System.out.println("parentID-------------:"+parentID);
    if (root.findNodeForID(dataParm.getValue("DURATION_CODE"))!=null){
        System.out.println("�Ѿ����д˽ڵ㲻��ִ����Ӳ���");
    }else if (root.findNodeForID(parentID) != null) {
        root.findNodeForID(parentID).add(treeNode);
    } else {
        //root.add(treeNode);
        TParm parentTparmselect=new TParm();
        parentTparmselect.setData("DURATION_CODE",parentID);
        this.putBasicSysInfoIntoParm(parentTparmselect);
        TParm resultParm=CLPDurationTool.getInstance().selectData(parentTparmselect);
//        System.out.println("��ѯ���ĸ��ڵ�:"+resultParm);
        if(resultParm.getCount()<=0){
            root.add(treeNode);
        }else{
            putNodeInTree(resultParm.getRow(0),root);
            root.findNodeForID(parentID).add(treeNode);
        }

    }
    }
    /**
     * ���ݲ�ѯ����
     */
    public void onQuery() {
        TParm parmselect = new TParm();
        this.putBasicSysInfoIntoParm(parmselect);
        TParm parmresult = CLPDurationTool.getInstance().selectData(parmselect);
        this.callFunction("UI|TABLE|setParmValue", parmresult);
        initTree();
        setPrimaryKeyEnabled(true);
        TTextFormat parentText = (TTextFormat)this.getComponent("PARENT_CODE");
//        System.out.println("----����������ֵ----");
        TTextFormat textFormatParentIds=(TTextFormat) this.getComponent("PARENT_CODE");
        textFormatParentIds.onQuery();

    }

    /**
     * �������
     */
    public void onClear() {
        this.setValue("DURATION_CODE", "");
        this.setValue("PY2", "");
        this.setValue("LEAF_FLG", "0");
        this.setValue("DURATION_CHN_DESC", "");
        this.setValue("DURATION_ENG_DESC", "");
        this.setValue("PARENT_CODE", "");
        this.setValue("PY1", "");
        this.setValue("SEQ", "");
        this.setValue("DESCRIPTION","");
        initHasLeafVisiable();
        onQuery();
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
            TTable table = (TTable)this.getComponent("TABLE");
            int selRow = table.getSelectedRow();
            TParm tableParm = table.getParmValue();
            String durationCode = tableParm.getValue("DURATION_CODE", selRow);
            TParm parm = new TParm();
            parm.setData("DURATION_CODE", durationCode);
//            System.out.println("ɾ����ID"+durationCode);
            TParm result = TIOM_AppServer.executeAction(
                    "action.clp.CLPDuringAction",
                    "deleteCLPDuring", parm);
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
            //������ɾ�������������͵�������ʱ���ˢ��ҳ��
            this.onQuery();
            this.messageBox("P0003");

        } else {
            return;
        }
    }

    /**
     * ���������
     */
    public void onTableClicked(int row) {
        if (row < 0) {
            return;
        }
        TParm data = (TParm) callFunction("UI|Table|getParmValue");
        setValueForParm(
                "DURATION_CODE;DURATION_CHN_DESC;PY1;PY2;DURATION_ENG_DESC;LEAF_FLG;PARENT_CODE;SEQ;DESCRIPTION",
                data, row);
        String str = data.getValue("SEQ", row);
        this.setValue("SEQ", str);
        setPrimaryKeyEnabled(false);
        initHasLeafVisiable();
        selectRow = row;
    }

    /**
     * ��ʼ���Ƿ������Ҷ�ӽڵ��ѡ���ѡ
     */
    private void initHasLeafVisiable(){
        TCheckBox checkbox=(TCheckBox) this.getComponent("LEAF_FLG");
        checkbox.setEnabled(true);
        String leafId= this.getValueString("DURATION_CODE");
        if(checkNodeIsHasLeaf(leafId)){
           checkbox.setEnabled(false);
        }
    }
    /**
     * ���ýڵ��Ƿ���Ҷ�ڵ�
     * @return boolean
     */
    private boolean checkNodeIsHasLeaf(String nodeId){
        boolean flag = true;
        TParm checkParm = new TParm();
        checkParm.setData("PARENT_CODE",nodeId);
        this.putBasicSysInfoIntoParm(checkParm);
        checkParm=CLPDurationTool.getInstance().selectData(checkParm);
        if(checkParm.getCount()<=0){
            flag=false;
        }
        return flag;
    }
    private boolean checkParentIdIsValid(String durationCode,String parentId){
        boolean flag = true;
        TParm checkParm = new TParm();
        checkParm.setData("DURATION_CODE",durationCode);
        checkParm.setData("PARENT_CODE",parentId);
        TParm checkResult=CLPDurationTool.getInstance().checkParentId(checkParm);
        String totalCountstr= checkResult.getValue("TOTALCOUNT",0);
        int totalCount = Integer.parseInt(totalCountstr);
        if(totalCount>0){
            flag=false;
            return flag;
        }
        TParm parentTparmselect = new TParm();
        parentTparmselect.setData("PARENT_CODE", durationCode);
        this.putBasicSysInfoIntoParm(parentTparmselect);
        TParm resultParm = CLPDurationTool.getInstance().selectData(
                parentTparmselect);
//        System.out.println("�����ͣ�"+resultParm);
        for(int i=0;i<resultParm.getCount();i++){
            String durationCodevalid= resultParm.getValue("DURATION_CODE",i);
            if(!checkParentIdIsValid(durationCodevalid,parentId)){
                return false;
            }
        }
        return true;
    }
    /**
     * ���淽��
     */
    public void onSave() {
//        System.out.println("���淽��ִ��");
        if (!validData()) {
            return;
        }
        if(this.getValueString("DURATION_CODE").equals(this.getValueString("PARENT_CODE"))){
            this.messageBox("���ڵ���벻�ܺ�ʱ�̴�����ͬ");
            return;
        }
        boolean checkParentIdIsValid= this.checkParentIdIsValid(this.getValueString("DURATION_CODE"),this.getValueString("PARENT_CODE"));
        if(!checkParentIdIsValid){
            this.messageBox("���ٴ�·��ʱ�̵ĸ����Ͳ����Ǹ��ٴ�·����������");
            return;
        }
        TParm param = new TParm();
        putParamWithObjName("DURATION_CODE", param);
        putParamWithObjName("REGION_CODE", param);
        putParamWithObjName("DURATION_CHN_DESC", param);
        putParamWithObjName("DURATION_ENG_DESC", param);
        putParamWithObjName("PY1", param);
        putParamWithObjName("PY2", param);
        putParamWithObjName("SEQ", param);
        putParamWithObjName("PARENT_CODE", param);
        putParamWithObjName("LEAF_FLG",param);
        putParamWithObjName("DESCRIPTION",param);
        //����ƴ�������Զ���������
        //����ƴ��1
        setValue("PY1",TMessage.getPy(getValueString("DURATION_CHN_DESC")));
        putParamWithObjName("PY1", param);
        //����seq
        if("".equals(param.getValue("SEQ"))){
            TNull tnull = new TNull(String.class);
//            System.out.println("����----------------");
            param.setData("SEQ",tnull);
        }
//        //�ж�˳����Ƿ�Ϊ�գ����Ϊ����Ҫ���д����������ݿ�SEQ���ֵ��1
//        if (this.getValueString("SEQ") == null ||
//            this.getValueString("SEQ").length() <= 0) {
//            TParm maxSEQResult = CLPDurationTool.getInstance().getMaxSEQ();
//            int SEQNumber = Integer.parseInt(maxSEQResult.getData("MAXSEQ",
//                    0) +
//                                             "") + 1;
//            param.setData("SEQ", SEQNumber);
//        }
        TParm returnParam = CLPDurationTool.getInstance().checkDataExist(param);
        int dataCount = returnParam.getInt("DATACOUNT", 0);
        if (dataCount > 0) {
            TParm resultParam = CLPDurationTool.getInstance().updateData(param);
            boolean flag = false;
            if (resultParam.getErrCode() >= 0) {
                flag = true;
            }
            String msg = flag ? "P0001" : "E0001";
            this.messageBox("��ʾ", msg, -1);
        } else {
            //���ʱ�����ݽ��д���
            this.putBasicSysInfoIntoParm(param);
            TParm resultParam = CLPDurationTool.getInstance().insertData(param);
            boolean flag = false;
            if (resultParam.getErrCode() >= 0) {
                flag = true;
            }
            String msg = flag ? "P0002" : "E0002";
            this.messageBox("��ʾ", msg, -1);
        }

        //ˢ������
        onQuery();
    }

    private boolean validData() {
        //��֤���Բ��÷�װ�ķ���
        if (!this.emptyTextCheck(
                "DURATION_CODE,DURATION_CHN_DESC,OPT_DATE,OPT_TERM,PRG_ID")) {
            return false;
        }
        String seqstr = this.getValueString("SEQ");
        if (this.checkNullAndEmpty(seqstr)) {
            if (!this.validNumber(seqstr)) {
                this.messageBox("˳�������������");
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
        TTextField tTextField = (TTextField)this.getComponent("DURATION_CODE");
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
//        System.out.println("��������" + totalColumnMaxLength + "������:" +
//                           totalRowMaxLength);
        //����
        String lockColumnStr = "";
        for (int i = 0; i < totalColumnMaxLength; i++) {
            if (!(i + "").equals(columnNum + "")) {
                lockColumnStr += i + ",";
            }
        }
        lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
//        System.out.println("���д���" + lockColumnStr);
        table.setLockColumns(lockColumnStr);
        //����
        String lockRowStr = "";
        for (int i = 0; i < totalRowMaxLength; i++) {
            if (!(i + "").equals(rowNum + "")) {
                lockRowStr += i + ",";
            }
        }
//        System.out.println("���д�ǰ��" + lockRowStr + "����" + totalRowMaxLength);
        lockRowStr = lockRowStr.substring(0,
                                          ((lockRowStr.length() - 1) < 0 ? 0 :
                                           (lockRowStr.length() - 1)));
//        System.out.println("���д���" + lockRowStr);
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
//        System.out.println(objstr);
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
//        System.out.println("total" + total);
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM", Operator.getIP());
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


}
