package com.javahis.ui.med;

import java.sql.Timestamp;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTpreDebtTool;
import jdo.hl7.BILJdo;
import jdo.med.MedToLedTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p> Title: ҽ�������ų���Ϣ���� </p>
 * 
 * <p> Description: ҽ�������ų���Ϣ���� </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company:BlueCore </p>
 * 
 * @author wanglong 2013.11.12
 * @version 1.0
 */
public class MedToLedUIControl
        extends TControl {

    private TTable tableM;// ��Աtable
    private TTable tableD;// ҽ��table
    private TParm param;// ��ѯ����
    TTextFormat execDeptCode;// �ϱ����� add by wanglong 20131127
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        tableM = (TTable) this.getComponent("TABLE_M");
        tableD = (TTable) this.getComponent("TABLE_D");
        execDeptCode  = (TTextFormat) this.getComponent("EXEC_DEPT_CODE");// ִ�п���
        // this.setValue("ADM_TYPE", "O");
        Timestamp now = SystemTool.getInstance().getDate();
        String dateStr = StringTool.getString(now, "yyyy/MM/dd");
        this.setValue("START_DATE", dateStr + " 00:00:00");
        this.setValue("END_DATE", now);
        tableD.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onCheckBoxClicked");
        if (this.getPopedem("ADMIN")) {
        	String deptSql = // add by wanglong 20131127
                "SELECT A.DEPT_CODE AS ID, A.DEPT_ABS_DESC AS NAME, A.PY1 FROM SYS_DEPT A, SYS_OPERATOR_DEPT B "
                        + " WHERE A.DEPT_CODE = B.DEPT_CODE AND B.USER_ID = '#' AND A.ACTIVE_FLG = 'Y' ORDER BY A.DEPT_CODE, A.SEQ";
        deptSql = deptSql.replaceFirst("#", Operator.getID());
        execDeptCode.setPopupMenuSQL(deptSql);
        }else if(this.getPopedem("ORDINARY")){
        	execDeptCode.setValue(Operator.getDept());
        	execDeptCode.setEnabled(false);
        }
        if (Operator.getRole().equals("ADMIN")) {
            execDeptCode.setHisOneNullRow(true);
        }
        execDeptCode.onQuery();
        execDeptCode.setValue(Operator.getDept());
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
    
        param = new TParm();
        Timestamp startDate = (Timestamp) this.getValue("START_DATE");
        Timestamp endDate = (Timestamp) this.getValue("END_DATE");
//        endDate = new Timestamp(endDate.getTime() + 24 * 60 * 60 * 1000 - 1);
        param.setData("START_DATE", StringTool.getString(startDate, "yyyyMMddHHmmss"));
        param.setData("END_DATE", StringTool.getString(endDate, "yyyyMMddHHmmss"));
        

        //�Ա�����δ��������ȫ����ѡ��
        if (this.getValueString("ALL").equals("Y")) {
            param.setData("REPORT_STATE", "ALL");
        } else if (this.getValueString("UNREPORT").equals("Y")) {
            param.setData("REPORT_STATE", "UNREPORT");
        } else if (this.getValueString("REPORTED").equals("Y")) {
            param.setData("REPORT_STATE", "REPORTED");
        }
        //���ݲ����Ų�ѯ
        if (!"".equals(this.getValueString("MR_NO"))) {
            param.setData("MR_NO", this.getValueString("MR_NO"));
        }
        //���������ѯ
        if (!"".equals(this.getValueString("ADM_TYPE"))) {
            param.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
        }
        //���ݿ���ҽ����ѯ
        //============donglt MedToled 20160304 ��ӿ���ҽ����ѯ
//        this.messageBox(this.getValueString("C_DR_CODE"));
//        System.out.println("****"+param);
        if (!"".equals(this.getValueString("C_DR_CODE"))) {
            param.setData("C_DR_CODE", this.getValueString("C_DR_CODE"));
        }
        //����ִ�п��Ҳ�ѯx
        if (!"".equals(this.getValueString("EXEC_DEPT_CODE"))) {// add by wanglong 20131127
            param.setData("EXEC_DEPT_CODE", this.getValueString("EXEC_DEPT_CODE"));
        } else {
            this.messageBox("��ѡ��ִ�п���");
            return;
        }
        
        // ��ѯ��Ա��Ϣ
//        System.out.println(param);
//        this.messageBox_(param);
        TParm patParm = MedToLedTool.getInstance().queryPatInfo(param);
        if (patParm.getErrCode() < 0) {
            this.messageBox("����ʧ��");// ����ʧ��
            return;
        }
//        this.messageBox("nihao12222222");
        this.callFunction("UI|regist|setEnabled", true);
        this.callFunction("UI|unRegist|setEnabled", true);
        this.setValue("ALL_SELECT", "N");
        if (patParm.getCount() <= 0) {
            tableD.setDSValue();
            tableM.setDSValue();
            this.setValue("COUNT", "");
            return;
        }else{
            if (this.getValueString("ADM_TYPE").equals("O")
                    || this.getValueString("ADM_TYPE").equals("E")) {
                tableM.setHeader("��Դ,80,ADM_TYPE;������,100;����,120;�Ա�,70,SEX_CODE;����,50;��������,120,timestamp;����,120,COMPANY_CODE;ҽʦ,100,CONTRACT_CODE;���");
                tableM.setColumnHorizontalAlignmentData("2,left;4,right;6,left;7,left;8,right");
            } else if (this.getValueString("ADM_TYPE").equals("I")) {
                tableM.setHeader("��Դ,80,ADM_TYPE;������,100;����,120;�Ա�,70,SEX_CODE;����,50;��������,120,timestamp;����,120,COMPANY_CODE;ҽʦ,100,CONTRACT_CODE;����");
                tableM.setColumnHorizontalAlignmentData("2,left;4,right;6,left;7,left;8,left");
            } else if (this.getValueString("ADM_TYPE").equals("H")) {
                tableM.setHeader("��Դ,80,ADM_TYPE;������,100;����,120;�Ա�,70,SEX_CODE;����,50;��������,120,timestamp;��������,120,COMPANY_CODE;��ͬ����,120,CONTRACT_CODE;���");
                tableM.setColumnHorizontalAlignmentData("2,left;4,right;6,left;7,left;8,right");
            } else {
                tableM.setHeader("��Դ,80,ADM_TYPE;������,100;����,120;�Ա�,70,SEX_CODE;����,50;��������,120,timestamp;����/��������,120,COMPANY_CODE;ҽʦ/��ͬ����,120,CONTRACT_CODE;���/����/���");
                tableM.setColumnHorizontalAlignmentData("2,left;4,right;6,left;7,left;8,right");
            }
        }
        //System.out.println("----------------patParm-------------"+patParm);
        tableM.setParmValue(patParm);
        this.setValue("COUNT", patParm.getCount() + "");
        TParm resultParm = new TParm();
        tableD.setParmValue(resultParm);
//        tableD.removeAll();//yanjing 20140411
        
        if (patParm.getCount() == 1) {
            tableM.setSelectedRow(0);
            onTableClicked();
        }
    }

    /**
     * ����
     */
    public void onReadCard() {
        TParm readEkt = new TParm();
        try {
            readEkt = EKTIO.getInstance().readEkt();// ҽ�ƿ���ֻ��MR_NO��Ϣ
        }
        catch (Exception e) {
          
            this.messageBox("����ʧ��");
            e.printStackTrace();
        }
        String mrNo = readEkt.getValue("MR_NO");
        this.setValue("MR_NO", mrNo);
        onMrNo();
    }

    /**
     * ����
     */
    public void onRegist() {
        TParm parmShowValue = tableD.getShowParmValue();
        TParm parmValue = tableD.getParmValue();
        TParm parm = new TParm();
        for (int i = 0; i < parmShowValue.getCount("ORDER_DESC"); i++) {
            if ("Y".equals(parmShowValue.getValue("FLG", i))) {
                if ("UNREPORT".equals(parmValue.getValue("REPORT_STATE", i))) {
                    if(parmValue.getData("ADM_TYPE", i).equals("I")){
                        String sql =
                                "SELECT A.* FROM ODI_DSPNM A, ODI_ORDER B "
                                        + " WHERE A.CASE_NO = B.CASE_NO   "
                                        + "   AND A.ORDER_NO = B.ORDER_NO "
                                        + "   AND A.ORDER_SEQ = B.ORDER_SEQ "
                                        + "   AND B.CASE_NO = '#'       "
                                        + "   AND B.ORDER_NO = '#'      "
                                        + "   AND B.ORDER_SEQ = #       "
                                        + "   AND B.ORDER_CODE = '#' ";
                        sql = sql.replaceFirst("#", parmValue.getValue("CASE_NO", i));
                        sql = sql.replaceFirst("#", parmValue.getValue("ORDER_NO", i));
                        sql = sql.replaceFirst("#", parmValue.getValue("SEQ_NO", i));
                        sql = sql.replaceFirst("#", parmValue.getValue("ORDER_CODE", i));
                        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
                        if (result.getErrCode() < 0) {
                            this.messageBox(result.getErrText());
                            continue;
                        }
                        if (result.getCount() < 1||result.getValue("NS_EXEC_CODE", 0).equals("")) {
                            this.messageBox(parmValue.getData("ORDER_DESC", i) + " ��ʿδִ�У��޷�����");
                            continue;
                        }
                    }
                    parm.addData("ADM_TYPE", parmValue.getData("ADM_TYPE", i));
                    parm.addData("CASE_NO", parmValue.getData("CASE_NO", i));
                    parm.addData("MR_NO", parmValue.getData("MR_NO", i));
                    parm.addData("ORDER_NO", parmValue.getData("ORDER_NO", i));
                    parm.addData("ORDER_CODE", parmValue.getData("ORDER_CODE", i));
                    parm.addData("CAT1_TYPE", parmValue.getData("CAT1_TYPE", i));// סԺר��
                    parm.addData("IPD_NO", parmValue.getData("IPD_NO", i));// סԺר��
                    parm.addData("DEPT_CODE", parmValue.getData("DEPT_CODE", i));// סԺר��
                    parm.addData("STATION_CODE", parmValue.getData("STATION_CODE", i));// סԺר��
                    parm.addData("BED_NO", parmValue.getData("BED_NO", i));// סԺר��
                    parm.addData("DOSAGE_QTY", 1);// סԺר��
                    parm.addData("OPT_USER", Operator.getID());// סԺר��
                    parm.addData("OPT_TERM", Operator.getIP());// סԺר��
                    parm.addData("SEQ_NO", parmValue.getData("SEQ_NO", i));
                    parm.addData("APPLICATION_NO", parmValue.getData("APPLICATION_NO", i));
                }
            }
        }
        parm.setData("EXEC_DR_CODE", Operator.getID());//wanglong add 20140514���Ӹ���ִ�����ֶ�
        parm.setData("EXEC_DR_DESC", Operator.getName());
        parm.setCount(parm.getCount("CASE_NO"));
        if((parmValue.getValue("ADM_TYPE", 0).equals("O")
                || parmValue.getValue("ADM_TYPE", 0).equals("E")) && EKTIO.getInstance().ektAyhSwitch()){//�ż�������� add by wanglong 20131209
            TParm orderPreParm = EKTpreDebtTool.getInstance().getMedOpdOrder(parm);
            orderPreParm.setData("CASE_NO", parmValue.getData("CASE_NO", 0));
            orderPreParm.setData("MR_NO", parmValue.getData("MR_NO", 0));
            TParm preParm = EKTpreDebtTool.getInstance().checkMasterForExe(orderPreParm);
            if(preParm.getErrCode()<0){
                messageBox(preParm.getErrText());
                return;
            }
        }
        if (parm.getCount() > 0) {
            TParm result = MedToLedTool.getInstance().registOrder(parm);//ִ�б�������
            if (result.getErrCode() < 0) {
                this.messageBox("����ʧ�ܣ�" + result.getErrText());
                return;
            }
            this.messageBox("�����ɹ���");
            for (int i = 0; i < parmValue.getCount("ORDER_CODE"); i++) {
                for (int j = 0; j < parm.getCount("CASE_NO"); j++) {
                    if (parmValue.getValue("ORDER_NO", i).equals(parm.getValue("ORDER_NO", j))
                            && parmValue.getValue("ORDER_CODE", i)
                                    .equals(parm.getValue("ORDER_CODE", j))
                            && parmValue.getValue("SEQ_NO", i).equals(parm.getValue("SEQ_NO", j))
                            && parmValue.getValue("APPLICATION_NO", i)
                                    .equals(parm.getValue("APPLICATION_NO", j))) {
                        parmValue.setData("REPORT_STATE", i, "REPORTED");
                    }
                }
            }
            for (int i = parmValue.getCount("ORDER_CODE") - 1; i >= 0; i--) {
                if (parmValue.getValue("REPORT_STATE", i).equals("REPORTED")) {
                    parmValue.removeRow(i);
                }
            }
            tableD.setParmValue(parmValue);
        } else {
            this.messageBox("û����Ҫ����������");
        }
        this.onQuery();
    }

    /**
     * ȡ������
     */
    public void onUnRegist() {
        TParm parmShowValue = tableD.getShowParmValue();
        TParm parmValue = tableD.getParmValue();
        if (parmValue.getCount() <= 0) {
            return;
        }
        TParm parm = new TParm();
        for (int i = 0; i < parmValue.getCount("ORDER_CODE"); i++) {
            if ("Y".equals(parmShowValue.getValue("FLG", i))) {
                if ("REPORTED".equals(parmValue.getValue("REPORT_STATE", i))) {
                    parm.addData("ADM_TYPE", parmValue.getData("ADM_TYPE", i));
                    parm.addData("CASE_NO", parmValue.getData("CASE_NO", i));
                    parm.addData("MR_NO", parmValue.getData("MR_NO", i));
                    parm.addData("ORDER_NO", parmValue.getData("ORDER_NO", i));
                    parm.addData("ORDER_CODE", parmValue.getData("ORDER_CODE", i));
                    parm.addData("IPD_NO", parmValue.getData("IPD_NO", i));// סԺר��
                    parm.addData("DEPT_CODE", parmValue.getData("DEPT_CODE", i));// סԺר��
                    parm.addData("STATION_CODE", parmValue.getData("STATION_CODE", i));// סԺר��
                    parm.addData("BED_NO", parmValue.getData("BED_NO", i));// סԺר��
                    parm.addData("DOSAGE_QTY", 1);// סԺר��
                    parm.addData("OPT_USER", Operator.getID());// סԺר��
                    parm.addData("OPT_TERM", Operator.getIP());// סԺר��
                    parm.addData("SEQ_NO", parmValue.getData("SEQ_NO", i));
                    parm.addData("APPLICATION_NO", parmValue.getData("APPLICATION_NO", i));
                }
            }
        }
        parm.setCount(parm.getCount("CASE_NO"));
        if (parm.getCount() > 0) {
            TParm result = MedToLedTool.getInstance().unRegistOrder(parm);//ִ��ȡ����������
            if (result.getErrCode() < 0) {
                this.messageBox("ȡ������ʧ�ܣ�" + result.getErrText());
                return;
            }
            this.messageBox("ȡ�������ɹ���");
            TParm lumpParm=new TParm();
            lumpParm.setData("CASE_NO",parmValue.getData("CASE_NO", 0));
            result=ADMInpTool.getInstance().selectall(lumpParm);//�ײͲ���ҽ�������������ҽ���˷���Ҫ�ֶ��˷�
            if (null!=result.getValue("LUMPWORK_CODE",0)&&result.getValue("LUMPWORK_CODE",0).length()>0) {
            	this.messageBox("�ײͲ���,ȡ��������Ҫ�ֶ������˷�");
            }
            for (int i = 0; i < parmValue.getCount("ORDER_CODE"); i++) {
                for (int j = 0; j < parm.getCount("CASE_NO"); j++) {
                    if (parmValue.getValue("ORDER_NO", i).equals(parm.getValue("ORDER_NO", j))
                            && parmValue.getValue("ORDER_CODE", i)
                                    .equals(parm.getValue("ORDER_CODE", j))
                            && parmValue.getValue("SEQ_NO", i).equals(parm.getValue("SEQ_NO", j))
                            && parmValue.getValue("APPLICATION_NO", i)
                                    .equals(parm.getValue("APPLICATION_NO", j))) {
                        parmValue.setData("REPORT_STATE", i, "UNREPORT");
                    }
                }
            }
            for (int i = parmValue.getCount("ORDER_CODE") - 1; i >= 0; i--) {
                if (parmValue.getValue("REPORT_STATE", i).equals("UNREPORT")) {
                    parmValue.removeRow(i);
                }
            }
            tableD.setParmValue(parmValue);
        } else {
            this.messageBox("û����Ҫȡ��������");
        }
    }

    /**
     * �һ�MENU�����¼�
     */
    public void showPopMenu() {
        tableD.setPopupMenuSyntax("��ʾ����ҽ��ϸ��,onShowOrderSet");
        // tableD.setPopupMenuSyntax("��ʾ����ҽ��ϸ��,onShowOrderSet;�鿴����,showReportResult");
    }

    /**
     * ��ʾ����ҽ��ϸ��
     */
    public void onShowOrderSet() {
        int row = tableD.getSelectedRow();
        if (row < 0) {
            return;
        }
        TParm parmValue = tableD.getParmValue();
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", parmValue.getData("ADM_TYPE", row));
        parm.setData("CASE_NO", parmValue.getData("CASE_NO", row));
        parm.setData("ORDER_NO", parmValue.getData("ORDER_NO", row));
        parm.setData("ORDER_CODE", parmValue.getData("ORDER_CODE", row));
        parm.setData("SEQ_NO", parmValue.getData("SEQ_NO", row));
        parm.setData("APPLICATION_NO", parmValue.getData("APPLICATION_NO", row));
        TParm result = MedToLedTool.getInstance().getOrderSet(parm);
        if (result.getCount() > 0) {
            this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", result);
        }
    }

//    /**
//     * �鿴����
//     */
//    public void showRept() {
//        String mrNo = tableM.getParmValue().getValue("MR_NO", tableM.getSelectedRow());
//        SystemTool.getInstance().OpenRisWeb(mrNo);
//    }
    
    /**
     * TABLE�����¼�
     */
    public void onTableClicked() {
        int row = tableM.getSelectedRow();
        if (row < 0) {
            return;
        }
        TParm parm = tableM.getParmValue().getRow(row);
//        this.setValueForParm("MR_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;ADM_TYPE", parm);
//        Timestamp now = SystemTool.getInstance().getDate();
//        this.setValue("AGE", StringUtil.showAge(parm.getTimestamp("BIRTH_DATE"), now));
        // ��ѯҽ����Ϣ
        param.setData("MR_NO", parm.getValue("MR_NO"));
        param.setData("CASE_NO", parm.getValue("CASE_NO"));
        TParm orderParm = MedToLedTool.getInstance().queryOrderInfo(param);
        if (orderParm.getErrCode() < 0) {
            this.messageBox("E0035");// ����ʧ��
            return;
        }
        if (orderParm.getCount() <= 0) {
            tableD.setDSValue();
            return;
        }
        tableD.setParmValue(orderParm);
        this.setValue("ALL_SELECT", "N");
    }

    /**
     * �����Żس��¼�
     */
    public void onMrNo() {
        String mrNo = this.getValueString("MR_NO");
        if(mrNo.equals("")){
            return;
        }
        Pat pat = Pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            this.messageBox("û�в�ѯ����");
            this.setValue("MR_NO", "");
            this.setValue("PAT_NAME", "");
            this.setValue("SEX_CODE", "");
            this.setValue("AGE", "");
            this.setValue("BIRTH_DATE", "");
            return;
        }
        Timestamp now = SystemTool.getInstance().getDate();
        this.setValue("MR_NO", pat.getMrNo());
        this.setValue("PAT_NAME", pat.getName());
        this.setValue("SEX_CODE", pat.getSexCode());
        this.setValue("AGE", StringUtil.showAge(pat.getBirthday(), now));
        this.setValue("BIRTH_DATE", pat.getBirthday());
        onQuery();
    }

    /**
     * ȫѡ��ť�¼�
     */
    public void onSelectAll() {
        String select = this.getValueString("ALL_SELECT");
        TParm parm = tableD.getParmValue();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            parm.setData("FLG", i, select);
        }
        tableD.setParmValue(parm);
        tableD.setSelectedRow(0);
    }
    
    /**
     * ϸ���CHECK_BOX�¼�
     * @param obj
     * @return
     */
    public boolean onCheckBoxClicked(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        int row = table.getSelectedRow();
        TParm parmValue = table.getParmValue();
        String medApplyNo = parmValue.getValue("APPLICATION_NO", row);
        for (int i = 0; i < parmValue.getCount(); i++) {
            if (parmValue.getValue("APPLICATION_NO", i).equals(medApplyNo)) {
                table.setValueAt(table.getItemString(row, "FLG"), i, 0);
            }
        }
        if (table.getParmValue().getValue("REPORT_STATE", row).equals("UNREPORT")) {
            this.callFunction("UI|regist|setEnabled", true);
            this.callFunction("UI|unRegist|setEnabled", false);
        } else if (table.getParmValue().getValue("REPORT_STATE", row).equals("REPORTED")) {
            this.callFunction("UI|regist|setEnabled", false);
            this.callFunction("UI|unRegist|setEnabled", true);
        }
        return true;
    }

    /**
     * ����Ƽ�
     */
    public void onCharge() {// add by wanglong 20131127
        int selRow = tableM.getSelectedRow();// ��ȡѡ����
        if (selRow < 0) {
            this.messageBox("��ѡ�񲡻�");
            return;
        }
        TParm parmRow = tableM.getParmValue().getRow(selRow);
        if (parmRow.getValue("ADM_TYPE").equals("O") || parmRow.getValue("ADM_TYPE").equals("E")) {
            TParm parm = new TParm();
            parm.setData("MR_NO", parmRow.getValue("MR_NO"));
            parm.setData("CASE_NO", parmRow.getValue("CASE_NO"));
            parm.setData("SYSTEM", "ONW");
            parm.setData("ONW_TYPE", parm.getValue("ADM_TYPE"));
            parm.setData("count", "1");
//            this.openDialog("%ROOT%\\config\\opb\\OPBChargesM.x", parm);//�ɲ���Ʒѽ���
            this.openDialog("%ROOT%\\config\\opbTest\\OPBCharge.x",parm);//�²���Ʒѽ���
        } else if (parmRow.getValue("ADM_TYPE").equals("I")) {
            int selRowD = tableD.getSelectedRow();// ��ȡѡ����
            if (selRowD < 0) {
                this.messageBox("��ѡ��ҽ��");
                return;
            }
            TParm parm =tableD.getParmValue().getRow(selRowD);
            onIBSCharge(parmRow,parm);
        } else if (parmRow.getValue("ADM_TYPE").equals("H")) {
            int choose = this.messageBox("��ʾ", "��ѡ��������ͣ��ǣ�����Ʒѣ���ɾ���Ʒѣ�������ȡ��", YES_NO_CANCEL_OPTION);
            if (choose == YES_OPTION) {
                addHRMCharge(parmRow);
            } else if (choose == NO_OPTION) {
                TParm parm = new TParm();
                deleteHRMCharge(parmRow);
            } else {
                return;
            }
        }
    }
    
    /**
     * סԺ����Ƽ�
     */
    public void onIBSCharge(TParm parmM,TParm parmD) {// add by wanglong 20131127
        TParm ibsParm = new TParm();
        TParm parm = ADMTool.getInstance().getADM_INFO(parmM).getRow(0);
        ibsParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        ibsParm.setData("MR_NO", parm.getValue("MR_NO"));
        ibsParm.setData("IPD_NO", parm.getValue("IPD_NO"));
        ibsParm.setData("BED_NO", parm.getValue("BED_NO"));
        ibsParm.setData("DEPT_CODE", parm.getValue("DEPT_CODE"));
        ibsParm.setData("STATION_CODE", parm.getValue("STATION_CODE"));
        ibsParm.setData("SERVICE_LEVEL", parm.getValue("SERVICE_LEVEL"));
        ibsParm.setData("VS_DR_CODE", parm.getValue("VS_DR_CODE"));
        ibsParm.setData("CLNCPATH_CODE", parm.getValue("CLNCPATH_CODE"));
        ibsParm.setData("ORDER_CODE", parmD.getValue("ORDER_CODE"));
        ibsParm.setData("ORDER_NO", parmD.getValue("ORDER_NO"));
        ibsParm.setData("ORDER_SEQ", parmD.getInt("SEQ_NO"));
        this.openDialog("%ROOT%\\config\\ibs\\IBSUnderOrderCharge.x", ibsParm);   
//        ibsParm.setData("IBS", "CASE_NO", parm.getValue("CASE_NO"));
//        ibsParm.setData("IBS", "IPD_NO", parm.getValue("IPD_NO"));
//        ibsParm.setData("IBS", "MR_NO", parm.getValue("MR_NO"));
//        ibsParm.setData("IBS", "BED_NO", parm.getValue("BED_NO"));
//        ibsParm.setData("IBS", "DEPT_CODE", parm.getValue("DEPT_CODE"));
//        ibsParm.setData("IBS", "STATION_CODE", parm.getValue("STATION_CODE"));
//        ibsParm.setData("IBS", "VS_DR_CODE", parm.getValue("VS_DR_CODE"));
//        ibsParm.setData("IBS", "TYPE", "INW");
//        ibsParm.setData("IBS", "CLNCPATH_CODE", parm.getValue("CLNCPATH_CODE"));
//        this.openDialog("%ROOT%\\config\\ibs\\IBSOrderm.x", ibsParm);
    }
    
    /**
     * �������ӼƷ�
     */
    public void addHRMCharge(TParm parm) {// add by wanglong 20131127
        TParm result = new TParm();
        String[] names = parm.getNames();
        for (int i = 0; i < names.length; i++) {
            result.addData(names[i], parm.getData(names[i]));
        }
        result.setCount(1);
        result.addData("METHOD", "ADD");
        String billSql =
                "SELECT DISTINCT A.BILL_NO, B.CASE_NO FROM HRM_ORDER A, HRM_PATADM B "
                        + " WHERE A.CASE_NO = B.CASE_NO AND A.CASE_NO = '#'";
        billSql = billSql.replaceFirst("#", result.getValue("CASE_NO", 0));
        TParm billParm = new TParm(TJDODBTool.getInstance().select(billSql));
        if (billParm.getErrCode() != 0) {
            this.messageBox("��������Ϣʧ�� " + billParm.getErrText());
            return;
        }
        if (billParm.getCount() > 1
                || (billParm.getCount() > 0 && !StringUtil.isNullString(billParm
                        .getValue("BILL_NO", 0).trim()))) {
            this.messageBox("������" + result.getValue("PAT_NAME", 0) + " �ѽ��㣬����������");
            return;
        } else if (billParm.getCount() < 1) {
            String patSql =
                    "SELECT * FROM HRM_PATADM A WHERE A.CASE_NO = '#'";
            patSql = patSql.replaceFirst("#", result.getValue("CASE_NO", 0));
            TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));
            if (patParm.getErrCode() != 0) {
                this.messageBox("���ҽ��չ��״̬ʧ�� " + patParm.getErrText());
                return;
            }
            if (patParm.getCount() < 1) {
                this.messageBox("������" + result.getValue("PAT_NAME", 0) + " ҽ��δչ��������������");
                return;
            }
        }
        this.openDialog("%ROOT%\\config\\hrm\\HRMBatchAdd.x", result);
    }

    /**
     * ����ɾ���Ʒ�
     */
    public void deleteHRMCharge(TParm parm) {// add by wanglong 20131127
        TParm result = new TParm();
        String[] names = parm.getNames();
        for (int i = 0; i < names.length; i++) {
            result.addData(names[i], parm.getData(names[i]));
        }
        result.setCount(1);
        result.addData("METHOD", "DELETE");
        String billSql =
                "SELECT DISTINCT A.BILL_NO, B.CASE_NO FROM HRM_ORDER A, HRM_PATADM B "
                        + " WHERE A.CASE_NO = B.CASE_NO AND A.CASE_NO = '#' ";
        billSql = billSql.replaceFirst("#", result.getValue("CASE_NO", 0));
        TParm billParm = new TParm(TJDODBTool.getInstance().select(billSql));
        if (billParm.getErrCode() != 0) {
            this.messageBox("��������Ϣʧ�� " + billParm.getErrText());
            return;
        }
        if (billParm.getCount() > 1
                || (billParm.getCount() > 0 && !StringUtil.isNullString(billParm
                        .getValue("BILL_NO", 0).trim()))) {
            this.messageBox("������" + result.getValue("PAT_NAME", 0) + " �ѽ��㣬���������");
            return;
        } else if (billParm.getCount() < 1) {
            String patSql =
                    "SELECT * FROM HRM_PATADM A WHERE A.CASE_NO = '#'";
            patSql = patSql.replaceFirst("#", result.getValue("CASE_NO", 0));
            TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));
            if (patParm.getErrCode() != 0) {
                this.messageBox("���ҽ��չ��״̬ʧ�� " + patParm.getErrText());
                return;
            }
            if (patParm.getCount() < 1) {
                this.messageBox("������" + result.getValue("PAT_NAME", 0) + " ҽ��δչ�������������");
                return;
            }
        }
        this.openDialog("%ROOT%\\config\\hrm\\HRMBatchAdd.x", result);
    }

    /**
     * ���
     */
    public void onClear() {
        this.clearValue("MR_NO;PAT_NAME;SEX_CODE;AGE;BIRTH_DATE;ADM_TYPE;COUNT");
        tableM.setParmValue(new TParm());
        tableD.setParmValue(new TParm());
        this.callFunction("UI|regist|setEnabled", true);
        this.callFunction("UI|unRegist|setEnabled", true);
        this.setValue("UNREPORT", "Y");
        this.setValue("ALL_SELECT", "N");
        param = new TParm();
    }
    
}
