package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import jdo.clp.CLPCHKItemTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.ui.TTextFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.data.TNull;


/**
 * <p>Title: ��ҽ�����ֵ�</p>
 *
 * <p>Description:��ҽ�����ֵ� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author  luhai
 * @version 1.0
 */
public class CLPCHKItemControl extends TControl {
    //��¼ѡ������
    int selectRow = -1;
    //Ĭ�ϵ�λ���
    private String defaultUnit="584";
    public CLPCHKItemControl() {

    }

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        initPage();
//        System.out.println("��ҽ�����ֵ�ҳ�����");
    }

    /**
     * ҳ���ʼ��
     */
    public void initPage() {
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
        onQuery();
        //���õ�λ��ֵ
        this.setValue("CLP_UNIT",defaultUnit);
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
                "CHKTYPE_CODE;CHKITEM_CODE;CHKITEM_CHN_DESC;PY1;CHKUSER_CODE;ORDTYPE_CODE;INDV_FLG;CLNCPATH_CODE;REGION_CODE;CHKITEM_ENG_DESC;DESCRIPTION;SEQ;ORDTYPE_CODE;CLP_UNIT;CLP_RATE;",
                data, row);
        String str = data.getValue("SEQ", row);
        this.setValue("SEQ", str);
        this.setValue("CLP_RATE", data.getValue("CLP_RATE", row));
        this.setValue("CLP_QTY",data.getValue("CLP_QTY", row));
        setPrimaryKeyEnabled(false);
        selectRow = row;
    }

    /**
     * ��ʼ������
     */
    public void onQuery() {
        setPrimaryKeyEnabled(true);
        TParm param = getFormTParmWithLikeCondition();
        CLPCHKItemTool CLPCHKITemTool = CLPCHKItemTool.getInstance();
        this.putBasicSysInfoIntoParm(param);
        TParm result = CLPCHKITemTool.selectData(param);
        this.callFunction("UI|Table|setParmValue", result);
    }

    public void onClear() {
        this.setValue("CHKTYPE_CODE", "");
        this.setValue("CHKUSER_CODE", "");
        this.setValue("INDV_FLG", "");
        this.setValue("CHKITEM_CODE", "");
//        this.setValue("ORDTYPE_CODE", "");
        this.setValue("CHKITEM_CHN_DESC", "");
        this.setValue("PY1", "");
        this.setValue("CLNCPATH_CODE", "");
        this.setValue("CHKITEM_ENG_DESC", "");
        this.setValue("DESCRIPTION", "");
        this.setValue("SEQ", "");
        this.setValue("ORDTYPE_CODE", "");
        this.setValue("CLP_UNIT", defaultUnit);
        this.setValue("CLP_RATE", "1");
        this.setValue("CLP_QTY", "1");
        setPrimaryKeyEnabled(true);
        onQuery();
    }

    /**
     * �õ�ҳ���еķǿ�ҳ�����
     * @return TParm
     */
    private TParm getFormTParmWithLikeCondition() {
        TParm parm = new TParm();
        putParamLikeWithObjName("CHKTYPE_CODE", parm);
//        putParamLikeWithObjName("CHKUSER_CODE", parm);
        //putParamWithObjName("INDV_FLG", parm);
        putParamLikeWithObjName("CHKITEM_CODE", parm);
//        putParamLikeWithObjName("ORDTYPE_CODE", parm);
        putParamLikeWithObjName("CHKITEM_CHN_DESC", parm);
//        putParamLikeWithObjName("PY1", parm);
//        putParamLikeWithObjName("CLNCPATH_CODE", parm);
//        putParamLikeWithObjName("CHKITEM_ENG_DESC", parm);
//        putParamLikeWithObjName("DESCRIPTION", parm);
//        putParamWithObjNameForQuery("ORDTYPE_CODE", parm);
//        putParamWithObjNameForQuery("CLP_UNIT", parm);
//        putParamWithObjNameForQuery("CLP_RATE", parm);
//        //�������������ж��Ƿ�Ϊ��
//        if (this.getValueString("SEQ") != null &&
//            this.getValueString("SEQ").length() > 0) {
//            putParamWithObjName("SEQ", parm);
//        }
        return parm;
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
            TParm result = TIOM_AppServer.executeAction(
                    "action.clp.CLPChkItemAction",
                    "delCheckItem", tableParm.getRow(selRow));
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
     * ���淽��
     */
    public void onSave() {
//        System.out.println("���淽��ִ��");
        if (!validData()) {
            return;
        }
        TParm param = new TParm();
        putParamWithObjName("CHKTYPE_CODE", param);
        putParamWithObjName("CHKUSER_CODE", param);
        putParamWithObjName("INDV_FLG", param);
        putParamWithObjName("CHKITEM_CODE", param);
        //¬������20110719  CHKITEM_CODE ��ȡ��ԭ����ȡֵ begin
        if(!this.checkNullAndEmpty(param.getValue("CHKITEM_CODE"))){
            param.setData("CHKITEM_CODE",SystemTool.getInstance().getNo("ALL", "CLP", "CLP",
                    "CHKITEM_CODE"));
        }
        //¬������20110719  CHKITEM_CODE ��ȡ��ԭ����ȡֵ begin
        putParamWithObjName("ORDTYPE_CODE", param);
        putParamWithObjName("CHKITEM_CHN_DESC", param);
        putParamWithObjName("PY1", param);
        putParamWithObjName("CLNCPATH_CODE", param);
        putParamWithObjName("REGION_CODE", param);
        putParamWithObjName("CHKITEM_ENG_DESC", param);
        putParamWithObjName("DESCRIPTION", param);
        putParamWithObjName("SEQ", param);
        putParamWithObjName("ORDTYPE_CODE", param);
        putParamWithObjName("CLP_QTY", param);
        putParamWithObjName("CLP_UNIT", param);
        putParamWithObjName("CLP_RATE", param);
        //����λ��ת���ʣ�Ϊ��ʱ����TNULL
        if(!this.checkNullAndEmpty(param.getValue("CLP_UNIT"))){
            param.setData("CLP_UNIT",new TNull(String.class));
        }
        if(!this.checkNullAndEmpty(param.getValue("CLP_RATE"))){
            param.setData("CLP_RATE",new TNull(String.class));
        }
        //�ж�˳����Ƿ�Ϊ�գ����Ϊ����Ҫ���д����������ݿ�SEQ���ֵ��1
//        System.out.println("form SEQ " + this.getValueString("SEQ"));
        if (this.getValueString("SEQ") == null ||
            this.getValueString("SEQ").length() <= 0) {
            TParm maxSEQResult = CLPCHKItemTool.getInstance().getMaxSEQ();
            int SEQNumber = Integer.parseInt(maxSEQResult.getData("MAXSEQ",
                    0) +
                                             "") + 1;
            param.setData("SEQ", SEQNumber);
        }
        TParm returnParam = CLPCHKItemTool.getInstance().checkDataExist(param);
        int dataCount = returnParam.getInt("DATACOUNT", 0);
        String strtmp = returnParam.getRow(0).getValue("DATACOUNT");
        //�����ݽ��д���
        param.setData("REGION_CODE", Operator.getRegion());
        param.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        param.setData("OPT_DATE", datestr);
        param.setData("OPT_TERM", Operator.getIP());
        param.setData("REGION_CODE", Operator.getRegion());
        if (dataCount > 0) {
//            TParm resultParam = CLPCHKItemTool.getInstance().updateData(param);
            TParm result = TIOM_AppServer.executeAction(
                    "action.clp.CLPChkItemAction",
                    "editCheckItem", param);
            boolean flag = false;
            if (result.getErrCode() >= 0) {
                flag = true;
            }
            String msg = flag ? "�༭�ɹ�" : "�༭ʧ��";
            this.messageBox("��ʾ", msg, -1);
        } else {
//            System.out.println("data not exist sys will insert data ");
//            TParm resultParam = CLPCHKItemTool.getInstance().insertData(param);
            TParm result = TIOM_AppServer.executeAction(
                    "action.clp.CLPChkItemAction",
                    "addCheckItem", param);
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
     * ������֤����
     */
    private boolean validData() {
        boolean flag = true;
        if (this.getValueString("CHKTYPE_CODE").length() <= 0) {
            this.messageBox("��ѡ�������!");
            return false;
        }
        if (this.getValueString("CHKUSER_CODE").length() <= 0) {
            this.messageBox("��ѡ�񱻲����Ա!");
            return false;
        }
        //¬�� 201107719 ɾ�� CHECKITEM_CODE����ȡ��ԭ��ʵ�֣��û���������
//        if (!this.emptyTextCheck(
//                "CHKITEM_CODE")) {
//            return false;
//        }
//        if (this.getValueString("ORDTYPE_CODE").length() <= 0) {
//            this.messageBox("��ѡ���ٴ�·����Ŀ!");
//            return false;
//        }
        //,CHKITEM_ENNAME,PY1,CLNCPATH_CODE
        if (!this.emptyTextCheck(
                "CHKITEM_CHN_DESC")) {
            return false;
        }
        String seq = this.getValueString("SEQ");
        if (seq != null && seq.length() > 0) {
            if (!validNumber(seq)) {
                this.messageBox("˳�������������!");
                return false;
            }
        }
        if (!this.checkNullAndEmpty(this.getValueString("ORDTYPE_CODE"))) {
            this.messageBox("��ѡ���ٴ�·��");
            return false;
        }
//        if (!this.checkNullAndEmpty(this.getValueString("CLP_UNIT"))) {
//            this.messageBox("��ѡ��λ");
//            return false;
//        }
//        if (!this.checkNullAndEmpty(this.getValueString("CLP_RATE"))) {
//            this.messageBox("������ת����");
//            return false;
//        }
        String clpRate=this.getValueString("CLP_RATE");
        if (!this.validDouble(clpRate)&&this.checkNullAndEmpty(clpRate)) {
            this.messageBox("ת����������Ϸ���ֵ");
            return false;
        }
        return flag;
    }

    /**
     * ���������Ƿ�����
     * @param flag boolean
     */
    private void setPrimaryKeyEnabled(boolean flag) {
        TTextFormat tTextFormat = (TTextFormat)this.getComponent("CHKTYPE_CODE");
        tTextFormat.setEnabled(flag);
        TTextField tTextField = (TTextField)this.getComponent("CHKITEM_CODE");
        tTextField.setEnabled(flag);
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


}
