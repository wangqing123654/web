package com.javahis.ui.sta;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import jdo.sta.STADeptListTool;
import jdo.sta.STAIn_03Tool;
import jdo.sta.STASQLTool;
import jdo.sta.STATool;
import jdo.sta.STAWorkLogTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: ��������
 * </p>
 * 
 * <p>
 * Description: ��������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangk 2009-5-21
 * @version 1.0
 */
public class STAWorkLogControl extends TControl {
    int MDays = 0; // ͳ���·ݵ�ʵ������
    String STA_DEPT_CODE = ""; // ��¼�м䲿�ű� code
    TParm result = null; // ��������
    String resultType = ""; // ��¼�������� day����ʶ�ձ� month����ʾ�±�
    boolean isSubmitD = false; // ��¼��ȡ���ձ������Ƿ��Ѿ�ȷ���ύ true��ȷ���ύ
    boolean isSubmitM = false; // ��¼��ȡ���±������Ƿ��Ѿ�ȷ���ύ true��ȷ���ύ
    private String LEADER = "";// ��¼�Ƿ����鳤Ȩ�� ���LEADER=2��ô�����鳤Ȩ��

    /**
     * ��ʼ��
     */
    public void onInit() {
        Timestamp time = SystemTool.getInstance().getDate();
        this.setValue("STA_DATE1", StringTool.rollDate(time, -1));// ǰһ��
        this.setValue("STA_DATE2", STATool.getInstance().getLastMonth());// ���ó�ʼʱ�䣨�ϸ��£�
        this.setValue("DAY_START_DATE", StringTool.rollDate(time, -8));//wanglong add 20140710
        this.setValue("DAY_END_DATE", StringTool.rollDate(time, -1));
        this.setValue("MONTH_START_DATE",
                      StringTool.getTimestamp(STATool.getInstance()
                                                      .rollMonth(StringTool.getString(time,
                                                                                      "yyyyMM"), -2),
                                              "yyyyMM"));
        this.setValue("MONTH_END_DATE", STATool.getInstance().getLastMonth());
        // ��ʼ��Ȩ��
        if (this.getPopedem("LEADER")) {
            LEADER = "2";
        }
    }


    /**
     * ����
     */
    public void onSave() {
        TTabbedPane t = (TTabbedPane) this.getComponent("tTabbedPane_0");
        TTable table = null;
        boolean submit = false;
        if (t.getSelectedIndex() == 0) { // �ձ�
            if (!checkSubmit(this.getText("STA_DATE1").replace("/", "")))// ��ѯ����סԺ�����Ƿ��ύ��������־������û���ύ�򷵻�
                return;
            // ��������鳤Ȩ�� ��ô�Ѿ��ύ�����ݲ������޸�
            if (!LEADER.equals("2")) {
                TParm check = STAWorkLogTool.getInstance().checkNum(this.getText("STA_DATE1").replace("/", ""), Operator.getRegion());
                if (check.getErrCode() < 0) {
                    this.messageBox(check.getErrText());
                    return;
                }
                if (check.getCount("STA_DATE") > 0) { // �Ѵ��ڸ��������ɵ�����
                    if (check.getValue("CONFIRM_FLG", 0).equals("Y")) {
                        if (this.getValue("Submit1").equals("Y")) {
                            this.messageBox_("�Ѿ��ύ�������޸ģ�");
                            return;
                        }
                    }
                }
            }   
            table = (TTable) this.getComponent("TableDay");
            // �ж� �ձ� �Ƿ��ύ
            if (((TCheckBox) this.getComponent("Submit1")).isSelected())
                submit = true;
        } else if (t.getSelectedIndex() == 1) { // �±�
            // ��������鳤Ȩ�� ��ô�Ѿ��ύ�����ݲ������޸�
            if (!LEADER.equals("2")) {
                TParm check = STAWorkLogTool.getInstance().checkNum(this.getText("STA_DATE1").replace("/", ""), Operator.getRegion());
                if (check.getErrCode() < 0) {
                    this.messageBox(check.getErrText());
                    return;
                }
                if (check.getCount("STA_DATE") > 0) { // �Ѵ��ڸ��������ɵ�����
                    if (check.getValue("CONFIRM_FLG", 0).equals("Y")) {
                        if (this.getValue("Submit2").equals("Y")) {
                            this.messageBox_("�Ѿ��ύ�������޸ģ�");
                            return;
                        }
                    }
                }
            }
            table = (TTable) this.getComponent("TableMouth");
            // �ж� �±� �Ƿ��ύ
            if (((TCheckBox) this.getComponent("Submit2")).isSelected())
                submit = true;
        }
        // û�����ݲ����в���
        if (table.getRowCount() <= 0) {
            return;
        }
        table.acceptText();
        TParm parmValue = table.getParmValue();
        // ��ȡ���ݿ�ʱ��
        Timestamp DBTime = SystemTool.getInstance().getDate();
        for (int i = 0; i < parmValue.getCount(); i++) {
            parmValue.setData("OPT_USER", i, Operator.getID()); // �����޸���Ա��Ϣ
            parmValue.setData("OPT_TERM", i, Operator.getIP()); // �����޸�IP
            parmValue.setData("OPT_DATE", i, DBTime); // �����޸�ʱ��
        }
        if (submit) { // �ύ ��Ҫ������״̬λ �޸�Ϊ 'Y'
            // ���̶�����������
            for (int i = 0; i < parmValue.getCount(); i++) {
                parmValue.setData("CONFIRM_FLG", i, "Y"); // ȷ�ϱ��
                parmValue.setData("CONFIRM_USER", i, Operator.getID()); // ȷ����ID
                parmValue.setData("CONFIRM_DATE", i, DBTime); // ȷ��ʱ��
                parmValue.setData("REGION_CODE", i, Operator.getRegion()); // ����
            }
            if (onUpdate(parmValue)) {
                this.messageBox_("�ύ�ɹ���");
                if (t.getSelectedIndex() == 0) {
                    isSubmitD = true;
                } else if (t.getSelectedIndex() == 1) { // �±�
                    isSubmitM = true;
                }
            } else {
                this.messageBox_("�ύʧ�ܣ�" + parmValue.getErrText());
            }
        } else { // ���ύ
            // ���̶�����������
            for (int i = 0; i < parmValue.getCount(); i++) {
                parmValue.setData("CONFIRM_FLG", i, "N"); // ȷ�ϱ��
                parmValue.setData("CONFIRM_USER", i, Operator.getID()); // ȷ����ID
                parmValue.setData("CONFIRM_DATE", i, DBTime); // ȷ��ʱ��
                parmValue.setData("REGION_CODE", i, Operator.getRegion()); // ����
            }
            if (onUpdate(parmValue))
                this.messageBox_("�޸ĳɹ���");
            else
                this.messageBox_("�޸�ʧ�ܣ�" + parmValue.getErrText());
        }
    }

    /**
     * �޸ķ���
     * 
     * @param parm
     * @return
     */
    public boolean onUpdate(TParm parm) {
        int count = parm.getCount();
        if (count <= 0) {
            return true;
        }
        for (int i = 0; i < count - 1; i++) {
            TParm result = STAWorkLogTool.getInstance().updateSTA_DAILY_02(parm.getRow(i));
            if (result.getErrCode() < 0) return false;
        }
        return true;
    }
    
    /**
     * ���ɱ�������
     */
    public void onGenerate() {
        isSubmitD = false;
        isSubmitM = false;
        this.clearValue("DEPT_CODE1;DEPT_CODE2;SubmitFlg;Submit1;Submit2");// ��տؼ�״̬
        String STA_DATE = "";
        String deptCode="";
        TTable table = new TTable();
        TTabbedPane t = (TTabbedPane) this.getComponent("tTabbedPane_0");
        if (t.getSelectedIndex() == 0) { // �ձ���
            if (this.getText("STA_DATE1").trim().length() <= 0) {
                this.messageBox_("��ѡ������!");
                return;
            }
            deptCode = this.getValueString("DEPT_CODE1");
            STA_DATE = this.getText("STA_DATE1").replace("/", ""); // ��ȡ����
            table = (TTable) this.getComponent("TableDay"); // ��ȡTable
            // if(!checkSubmit(STA_DATE))//��ѯ����סԺ�����Ƿ��ύ��������־������û���ύ�򷵻�
            // return;
            // �ж��Ƿ��Ѿ����ɸ����ڵ�����
            TParm check = STAWorkLogTool.getInstance().checkNum(STA_DATE, Operator.getRegion());
            if (check.getErrCode() < 0) {
                this.messageBox("û�и����ڵ�����");
                return;
            }
            if (check.getCount("STA_DATE") > 0) { // �Ѵ��ڸ��������ɵ�����
                if (check.getValue("CONFIRM_FLG", 0).equals("Y")) {
                    isSubmitD = true; // ��ʶ�ձ��Ѿ��ύ
                    this.setValue("Submit1", true);
                    this.messageBox_("�����Ѿ��ύ�������������ɣ�");
                    TableBind(table, STA_DATE, Operator.getRegion(),deptCode); // ������
                    return;
                } else {
                    switch (this.messageBox("��ʾ��Ϣ", "�����Ѵ��ڣ��Ƿ��������ɣ�", TControl.YES_NO_OPTION)) {
                    // ����
                    case 0:
                        break;
                    // ������
                    case 1:
                        TableBind(table, STA_DATE, Operator.getRegion(),deptCode); // ������
                        return;
                    }
                }
            }
            DayReport(); // �����ձ�����
        } else if (t.getSelectedIndex() == 1) { // �±���
            if (this.getText("STA_DATE2").trim().length() <= 0) {
                this.messageBox_("��ѡ������!");
                return;
            }
            deptCode = this.getValueString("DEPT_CODE2");
            STA_DATE = this.getText("STA_DATE2").replace("/", "");
            table = (TTable) this.getComponent("TableMouth");
            // �ж��Ƿ��Ѿ����ɸ��·ݵ�����
            TParm check = STAWorkLogTool.getInstance().checkNum(STA_DATE, Operator.getRegion());
            if (check.getErrCode() < 0) {
                this.messageBox("û�и����ڵ�����");
                return;
            }
            if (check.getCount("STA_DATE") > 0) { // �Ѵ��ڸ��������ɵ�����
                if (check.getValue("CONFIRM_FLG", 0).equals("Y")) {
                    isSubmitM = true; // ��ʶ �±��Ѿ��ύ
                    this.setValue("Submit2", true);
                    this.messageBox_("�����Ѿ��ύ�������������ɣ�");
                    TableBind(table, STA_DATE, Operator.getRegion(),deptCode); // ������
                    return;
                } else {
                    switch (this.messageBox("��ʾ��Ϣ", "�����Ѵ��ڣ��Ƿ��������ɣ�", TControl.YES_NO_OPTION)) {
                    // ����
                    case 0:
                        break;
                    // ������
                    case 1:
                        TableBind(table, STA_DATE, Operator.getRegion(),deptCode); // ������
                        return;
                    }
                }
            }
            MonthReport(); // �����±�����
        }
        if (result == null) {
            this.messageBox_("����ʧ�ܣ�");
            return;
        }
        TParm parm = new TParm();
        parm.setData("STA_DATE", STA_DATE);
        parm.setData("Daily", result.getData());
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            parm.setData("REGION_CODE", Operator.getRegion());
        TParm re = TIOM_AppServer.executeAction("action.sta.STAWorkLogAction", "insertData", parm);
        if (re.getErrCode() < 0) {
            this.messageBox_("����ʧ�ܣ�");
            return;
        }
        this.messageBox_("���ɳɹ���");
        TableBind(table, STA_DATE, Operator.getRegion(),deptCode); // ������
    }
    

    /**
     * �ձ�������
     */
    private void DayReport() {
        TParm parm = new TParm();
        parm.setData("STA_DATE", ((TTextFormat) this.getComponent("STA_DATE1")).getText().replace("/", ""));
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            parm.setData("REGION_CODE", Operator.getRegion());
//        if (this.getValue("DEPT_CODE1") != null
//                && this.getValue("DEPT_CODE1").toString().trim().length() > 0) {
//            parm.setData("DEPT_CODE", this.getValue("DEPT_CODE1"));// ����CODE
//        }
        TParm re = STAWorkLogTool.getInstance().selectDataDay(parm);
        if (re.getErrCode() < 0) {
            result = null;
            return;
        }
        reportParm(re, "D");
        resultType = "day";
    }

    /**
     * �±�������
     */
    private void MonthReport() {
        TParm parm = new TParm();
        parm.setData("STA_DATE", ((TTextFormat) this.getComponent("STA_DATE2")).getText().replace("/", ""));
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            parm.setData("REGION_CODE", Operator.getRegion());
        TParm re = STAWorkLogTool.getInstance().selectDataMonth(parm);
        if (re.getErrCode() < 0) {
            result = null;
            return;
        }
        // System.out.println("�±�:"+re);
        reportParm(re, "M"); // �����±�������
        resultType = "month";
    }

    /**
     * �������ɱ��������
     * @param parm TParm
     * @param type String   ��M��:�±���    ��D��:�ձ���
     */
    private void reportParm(TParm parm, String type) {
        DecimalFormat df = new DecimalFormat("0.00"); // �趨Double���͸�ʽ
        if (type.equals("M"))
            MDays = this.getDaysOfMonth(((TTextFormat) this.getComponent("STA_DATE2")).getText());
        result = new TParm();
        for (int i = 0; i < parm.getCount("OUTP_NUM"); i++) {
            // ��� OUTP_NUM��ERD_NUM��һ��Ϊ�� ��ʾ �ò��Ų����ż��ﲿ�� �����ż����ͳ����Ŀ
            if (parm.getData("OUTP_NUM", i) == null || parm.getData("ERD_NUM", i) == null) {
                result.addData("DATA_01", "");
            } else {
                // ���ݿ� ERD_NUM�ֶ����ʹ��� ע��Ҫ�޸�
                int DATA_01 = parm.getInt("OUTP_NUM", i) + Integer.valueOf(parm.getValue("ERD_NUM", i).trim());
                result.addData("DATA_01", DATA_01); // ���������ܼ�<1>
            }
            result.addData("DATA_02", parm.getValue("OUTP_NUM", i)); // �����˴���<2>
            result.addData("DATA_03", parm.getValue("ERD_NUM", i)); // �����˴μ� <3>
            result.addData("DATA_04", parm.getValue("ERD_DIED_NUM", i)); // ���������˴�<4>
            result.addData("DATA_05", parm.getValue("OBS_NUM", i)); // ���۲�����<5>
            result.addData("DATA_06", parm.getValue("OBS_DIED_NUM", i)); // ������������<6>
            result.addData("DATA_07", parm.getValue("DATA_07", i)); // �ڳ�ʵ�в���<7>
            result.addData("DATA_08", parm.getValue("DATA_08", i)); // ��Ժ����<8>
            result.addData("DATA_08_1", parm.getValue("DATA_08_1", i)); // ����ת��<8-1>
            result.addData("DATA_09", parm.getValue("DATA_09", i)); // ��Ժ�����ܼ�<9>
            result.addData("DATA_10", parm.getValue("DATA_10", i)); // ��������<10>
            result.addData("DATA_11", parm.getValue("DATA_11", i)); // ����<11>
            result.addData("DATA_12", parm.getValue("DATA_12", i)); // ��ת<12>
            result.addData("DATA_13", parm.getValue("DATA_13", i)); // δ��<13>
            result.addData("DATA_14", parm.getValue("DATA_14", i)); // ����<14>
            result.addData("DATA_15", parm.getValue("DATA_15", i)); // ����<15>
            result.addData("DATA_15_1", parm.getValue("DATA_15_1", i)); // ת����������<15_1>
            result.addData("DATA_16", parm.getValue("DATA_16", i)); // ʵ�в�����<16>
            result.addData("DATA_17", parm.getValue("DATA_17", i)); // ��ĩʵ�в���<17>(һ��ʱ���ڣ�DATA17==DATA18)
            result.addData("DATA_18", parm.getValue("DATA_18", i)); // ʵ�ʿ����ܴ���<18> = DATA_17 * MDays
            if (parm.getData("DATA_18", i) != null) { // �жϸ������Ƿ�Ϊ null
                if (type.equals("M")) {// �±�        ȡƽ����
                    result.addData("DATA_19", parm.getInt("DATA_18", i) / MDays); // ƽ�����Ų�����<19>
                } else { // �ձ�         ȡ����ʵ�в�����
                    result.addData("DATA_19", parm.getValue("DATA_17", i)); // ƽ�����Ų�����<19>
                }
            } else
                result.addData("DATA_19", ""); // ƽ�����Ų�����<19>
            result.addData("DATA_20", parm.getValue("DATA_16", i)); // ʵ��ռ���ܴ���<20>
            result.addData("DATA_21", parm.getValue("DATA_19", i)); // ��Ժ��סԺ����<21>
            result.addData("DATA_22", parm.getValue("OUYCHK_OI_NUM", i)); // ������Ϸ�����<22>
            result.addData("DATA_23", parm.getValue("OUYCHK_RAPA_NUM", i)); // ������Ϸ�����<23>
            result.addData("DATA_24", parm.getValue("OUYCHK_INOUT", i)); // ��Ժ��Ϸ�����<24>
            result.addData("DATA_25", parm.getValue("OUYCHK_OPBFAF", i)); // ��ǰ���������<25>
            result.addData("DATA_26", parm.getValue("HEAL_LV_I_CASE", i)); // �޾��п�������<26>
            result.addData("DATA_27", parm.getValue("HEAL_LV_BAD", i)); // �޾��пڻ�ŧ��<27>
            result.addData("DATA_28", parm.getValue("GET_TIMES", i)); // Σ�ز���������<28>
            result.addData("DATA_29", parm.getValue("SUCCESS_TIMES", i)); // Σ�ز������ȳɹ�
            result.addData("DATA_30", parm.getValue("DATA_22", i)); // ������<30>
            double O_num = parm.getDouble("DATA_10", i) + parm.getDouble("DATA_15", i); // ���ڼ���������
            if (O_num == 0) {
                result.addData("DATA_31", ""); // ������<31>
                result.addData("DATA_32", ""); // ��ת��<32>
                result.addData("DATA_33", ""); // ������<33>
            } else {
                result.addData("DATA_31", df.format(parm.getDouble("DATA_11", i) / O_num * 100)); // ������<31>
                result.addData("DATA_32", df.format(parm.getDouble("DATA_12", i) / O_num * 100)); // ��ת��<32>
                result.addData("DATA_33", df.format(parm.getDouble("DATA_14", i) / O_num * 100)); // ������<33>
            }
            // ������ת(��)
            if (type.equals("M")) { // �±� ������ת(��)=���ڳ�Ժ����/ ͬ��ƽ�����Ų�����
                if (parm.getInt("DATA_18", i) > 0 && parm.getDouble("DATA_09", i) > 0) {
                    result.addData("DATA_34", parm.getDouble("DATA_09", i) / (parm.getDouble("DATA_18", i) / MDays));
                } else
                    result.addData("DATA_34", "");
            } else { // �ձ�
                if (parm.getDouble("BED_RETUEN", i) != 0)
                    result.addData("DATA_34", parm.getDouble("BED_RETUEN", i));
                else
                    result.addData("DATA_34", "");
            }
            result.addData("DATA_35", parm.getValue("BED_WORK_DAY", i)); // ����������<35>
            // ����ʹ����<36> �ձ���ͳ��
            if (type.equals("M")) { // �±�
                double DATA_36 = 0;
                DATA_36 = STAWorkLogTool.getInstance().countBED_USE_RATE(parm.getInt("DATA_16", i), MDays, parm.getInt("DATA_18", i) / MDays);
                result.addData("DATA_36", DATA_36);
            } else { // �ձ� �ձ���ͳ��
                result.addData("DATA_36", "");
            }
            // ����ƽ��סԺ��<37>
            if (parm.getData("DATA_09", i) == null) // �ж������Ƿ�ΪNull
                result.addData("DATA_37", "");
            else if (parm.getInt("DATA_09", i) == 0)
                result.addData("DATA_37", "0");
            else
                result.addData("DATA_37", parm.getInt("DATA_21", i) / parm.getInt("DATA_09", i));
            result.addData("DATA_38", ""); // ��Ϸ�����<38>
            // �޾��пڻ�ŧ��<39>
            if (parm.getData("HEAL_LV_I_CASE", i) == null) // �ж������Ƿ�ΪNull
                result.addData("DATA_39", "");
            else if (parm.getDouble("HEAL_LV_I_CASE", i) == 0)
                result.addData("DATA_39", "0");
            else
                result.addData("DATA_39", df.format(parm.getDouble("HEAL_LV_BAD", i)
                                                  / parm.getDouble("HEAL_LV_I_CASE", i) * 100));
            // Σ�ز������ȳɹ�%<40>
            if (parm.getData("GET_TIMES", i) == null)
                result.addData("DATA_40", "");
            else if (parm.getDouble("GET_TIMES", i) == 0)
                result.addData("DATA_40", "100");
            else
                result.addData("DATA_40", df.format(parm.getDouble("SUCCESS_TIMES", i)
                                                  / parm.getDouble("GET_TIMES", i) * 100));
            // ������%<41> = ������/ʵ�в�����
            if (parm.getData("DATA_16", i) == null)
                result.addData("DATA_41", "");
            else if (parm.getDouble("DATA_16", i) == 0)
                result.addData("DATA_41", "0");
            else
                result.addData("DATA_41", df.format(parm.getDouble("DATA_22", i)
                                                  / parm.getDouble("DATA_16", i) * 100));
            // //������Ϸ�����%<41_1>
            if (parm.getData("DATA_10", i) == null
                    && parm.getData("DATA_15", i) == null)
                result.addData("DATA_41_1", "");
            else if ((parm.getDouble("DATA_10", i) + parm.getDouble("DATA_15",
                    i)) == 0)
                result.addData("DATA_41_1", "100");
            else
                result.addData("DATA_41_1", df.format(parm.getDouble("OUYCHK_RAPA_NUM", i)
                                                     / (parm.getDouble("DATA_10", i) + parm.getDouble("DATA_15", i)) * 100));
            // ��ǰ������Ϸ�����%<41_2>
            if (parm.getData("DATA_10", i) == null && parm.getData("DATA_15", i) == null)
                result.addData("DATA_41_2", "");
            else if ((parm.getDouble("DATA_10", i) + parm.getDouble("DATA_15", i)) == 0)
                result.addData("DATA_41_2", "0");
            else
                result.addData("DATA_41_2", df.format(parm.getDouble("OUYCHK_OPBFAF", i)
                                                      / (parm.getDouble("DATA_10", i) + parm.getDouble("DATA_15", i)) * 100));

            // �������
            result.addData("DEPT_CODE", parm.getValue("DEPT_CODE", i)); // ����
            result.addData("STATION_CODE", parm.getValue("STATION_CODE", i)); // ����
            result.addData("CONFIRM_FLG", "N"); // ȷ��ע�� ��������Ĭ��Ϊ ��N��
            result.addData("CONFIRM_USER", Operator.getID()); // ȷ�ϻ�ʿ
            result.addData("OPT_USER", Operator.getID());
            result.addData("OPT_TERM", Operator.getIP());
            // ����
            if (type.equals("M")) // �±�
            result.addData("STA_DATE", ((TTextFormat) this.getComponent("STA_DATE2")).getText().replace("/", ""));
            else
            // �ձ�
            result.addData("STA_DATE", ((TTextFormat) this.getComponent("STA_DATE1")).getText().replace("/", ""));
            // ������־�Ƿ��ύ���
            if (type.equals("D")) // �ձ�
            result.addData("SUBMIT_FLG", parm.getValue("SUBMIT_FLG", i));
        }
    }

    
    /**
     * Table���ݰ�
     */
    private void TableBind(TTable table, String STA_DATE, String regionCode,String deptcode) {
        DecimalFormat df = new DecimalFormat("0.00"); // �趨Double���͸�ʽ
        String sql = STASQLTool.getInstance().getSelectSTA_DAILY_02(STA_DATE, regionCode, deptcode);
        TTabbedPane t = (TTabbedPane) this.getComponent("tTabbedPane_0");
        // �ϼ� shibl 20210910 add
        int data01 = 0;// ���������ܼƺϼ�<1>
        int data02 = 0;// �����˴����ϼ�<2>
        int data03 = 0;// �����˴κϼ�<3>
        int data04 = 0;// ���������˴κϼ�<4>
        int data05 = 0;// ���۲������ϼ�<5>
        int data06 = 0;// �������������ϼ�<6>
        int data07 = 0;// �ڳ�ʵ�в��˺ϼ�<7>
        int data08 = 0;// ��Ժ�����ϼ�<8>
        int data0801 = 0;// ����ת��ϼ�<8-1>
        int data09 = 0;// ��Ժ�����ܼƺϼ�<9>
        int data10 = 0;// �������ƺϼ�<10>
        int data11 = 0;// �����ϼ�<11>
        int data12 = 0;// ��ת�ϼ�<12>
        int data13 = 0;// δ���ϼ�<13>
        int data14 = 0;// �����ϼ�<14>
        int data15 = 0;// �����ϼ�<15>
        int data1501 = 0;// ת�����������ϼ�<15_01>
        int data16 = 0;// ʵ�в������ϼ�<16>
        int data17 = 0;// ��ĩʵ�в�����<17>
        int data18 = 0;// ʵ�ʿ����ܴ������ϼ�<18>
        int data19 = 0;// ƽ�����Ŵ����ϼ�<19>
        int data20 = 0;// ʵ��ռ�������ϼ�
        int data21 = 0;// ��Ժ����סԺ�����ϼ�
        int data22 = 0;// ������Ϸ����ϼ�
        int data23 = 0;// ������Ϸ������ϼ�
        int data24 = 0;// ��Ժ��Ϸ������ϼ�
        int data25 = 0;// ��ǰ����������ϼ�
        int data26 = 0;// �޾��п��������ϼ�
        int data27 = 0;// �޾��пڻ�ŧ���ϼ�
        int data28 = 0;// Σ�ز����������ϼ�
        int data29 = 0;// Σ�ز������ȳɹ��ϼ�
        int data30 = 0;// �� �����ϼ�
        double data31 = 0;// ������
        double data32 = 0;// ��ת��
        double data33 = 0;// ������
        double data34 = 0;// ������ת
        int data35 = 0;// ����������
        double data36 = 0;// ����ʹ����
        int data37 = 0;// ����ƽ��סԺ��<37>
        double data38 = 0;// ��Ϸ�����
        double data39 = 0;// �޾��пڻ�ŧ��<39>
        double data40 = 0;// Σ�ز������ȳɹ�%<40>
        double data41 = 0;// ������%<41>
        double data4101 = 0;// ������Ϸ�����%<41_1>
        double data4102 = 0;// ��ǰ������Ϸ�����%<41_2>
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm.getCount() <= 0) {
            this.messageBox("û������");
            return;
        }
        for (int i = 0; i < parm.getCount(); i++) {
            data01 += parm.getInt("DATA_01", i);
            data02 += parm.getInt("DATA_02", i);
            data03 += parm.getInt("DATA_03", i);
            data04 += parm.getInt("DATA_04", i);
            data05 += parm.getInt("DATA_05", i);
            data06 += parm.getInt("DATA_06", i);
            data07 += parm.getInt("DATA_07", i);
            data08 += parm.getInt("DATA_08", i);
            data0801 += parm.getInt("DATA_08_1", i);
            data09 += parm.getInt("DATA_09", i);
            data10 += parm.getInt("DATA_10", i);
            data11 += parm.getInt("DATA_11", i);
            data12 += parm.getInt("DATA_12", i);
            data13 += parm.getInt("DATA_13", i);
            data14 += parm.getInt("DATA_14", i);
            data15 += parm.getInt("DATA_15", i);
            data1501 += parm.getInt("DATA_15_1", i);
            data16 += parm.getInt("DATA_16", i);
            data17 += parm.getInt("DATA_17", i);
            data18 += parm.getInt("DATA_18", i);
            data19 += parm.getInt("DATA_19", i);
            data20 += parm.getInt("DATA_20", i);
            data21 += parm.getInt("DATA_21", i);
            data22 += parm.getInt("DATA_22", i);
            data23 += parm.getInt("DATA_23", i);
            data24 += parm.getInt("DATA_24", i);
            data25 += parm.getInt("DATA_25", i);
            data26 += parm.getInt("DATA_26", i);
            data27 += parm.getInt("DATA_27", i);
            data28 += parm.getInt("DATA_28", i);
            data29 += parm.getInt("DATA_29", i);
            data30 += parm.getInt("DATA_30", i);
        }
        parm.addData("SUBMIT_FLG", "N");
        parm.addData("DEPT_CODE", "�ϼ�:");
        parm.addData("DATA_01", data01);
        parm.addData("DATA_02", data02);
        parm.addData("DATA_03", data03);
        parm.addData("DATA_04", data04);
        parm.addData("DATA_05", data05);
        parm.addData("DATA_06", data06);
        parm.addData("DATA_07", data07);
        parm.addData("DATA_08", data08);
        parm.addData("DATA_08_1", data0801);
        parm.addData("DATA_09", data09);
        parm.addData("DATA_10", data10);
        parm.addData("DATA_11", data11);
        parm.addData("DATA_12", data12);
        parm.addData("DATA_13", data13);
        parm.addData("DATA_14", data14);
        parm.addData("DATA_15", data15);
        parm.addData("DATA_16", data16);
        parm.addData("DATA_17", data17);
        parm.addData("DATA_18", data18);
        parm.addData("DATA_15_1", data1501);
        parm.addData("DATA_19", data19);
        parm.addData("DATA_20", data20);
        parm.addData("DATA_21", data21);
        parm.addData("DATA_22", data22);
        parm.addData("DATA_23", data23);
        parm.addData("DATA_24", data24);
        parm.addData("DATA_25", data25);
        parm.addData("DATA_26", data26);
        parm.addData("DATA_27", data27);
        parm.addData("DATA_28", data28);
        parm.addData("DATA_29", data29);
        parm.addData("DATA_30", data30);
        parm.addData("DATA_30", data30);
        double O_num = data10 + data15; // ���ڼ���������
        if (O_num == 0) {
            parm.addData("DATA_31", ""); // ������<31>
            parm.addData("DATA_32", ""); // ��ת��<32>
            parm.addData("DATA_33", ""); // ������<33>
        } else {
            parm.addData("DATA_31", df.format((double) data11 / O_num * 100)); // ������<31>
            parm.addData("DATA_32", df.format((double) data12 / O_num * 100)); // ��ת��<32>
            parm.addData("DATA_33", df.format((double) data14 / O_num * 100)); // ������<33>
        }
        // ������ת(��)
        if (t.getSelectedIndex() == 1) { // �±�
            if (data18 > 0 && data09 > 0) {
                parm.addData("DATA_34", df.format((double) data09 / ((double) data18 / MDays)));// ������ת(��) = ���ڳ�Ժ����/ ͬ��ƽ�����Ų�����
            } else parm.addData("DATA_34", "");
        } else { // �ձ�
            parm.addData("DATA_34", "");// �㷨����
        }
        parm.addData("DATA_35", ""); // ����������<35> �㷨����
        if (t.getSelectedIndex() == 1) { // �±�
            //add by wanglong 20131126
            MDays = this.getDaysOfMonth(StringTool.getString(StringTool.getTimestamp(STA_DATE, "yyyyMM"), "yyyy/MM"));
            //add end
            double DATA_36 = 0;
            DATA_36 = STAWorkLogTool.getInstance().countBED_USE_RATE(data16, MDays, data18 / MDays);  // ����ʹ����<36>
            parm.addData("DATA_36", df.format(DATA_36));
        } else { // �ձ�
            parm.addData("DATA_36", "");// �ձ���ͳ��
        }
        if (data09 == 0)
            parm.addData("DATA_37", "0");
        else 
            parm.addData("DATA_37", df.format((double) data21 / (double) data09)); // ����ƽ��סԺ��<37>
        parm.addData("DATA_38", ""); // ��Ϸ�����<38>
        if (data26 == 0)
            parm.addData("DATA_39", "0");
        else
            parm.addData("DATA_39", df.format((double) data27 / (double) data26 * 100)); // �޾��пڻ�ŧ��<39>
        if (data28 == 0)
            parm.addData("DATA_40", "100");
        else
            parm.addData("DATA_40", df.format((double) data29 / (double) data28 * 100)); // Σ�ز������ȳɹ�%<40>
        if (data16 == 0)
            parm.addData("DATA_41", "100");
        else
            parm.addData("DATA_41", df.format((double) data30 / (double) data16 * 100));// ������%<41> = ������/ʵ�в�����
        if (data10 == 0 && data15 == 0)
            parm.addData("DATA_41_1", "");
        else if (data10 + data15 == 0)
            parm.addData("DATA_41_1", "100");
        else
            parm.addData("DATA_41_1", df.format((double) data23 / (double) (data10 + data15) * 100)); // ������Ϸ�����%<41_1>
        if (data10 == 0 && data15 == 0)
            parm.addData("DATA_41_2", "");
        else if (data10 + data15 == 0)
            parm.addData("DATA_41_2", "0");
        else
            parm.addData("DATA_41_2", df.format((double) data25 / (double) (data10 + data15) * 100)); // ��ǰ������Ϸ�����%<41_2>
        parm.setCount(parm.getCount("DATA_01"));
        table.setParmValue(parm);
        
        TParm check = STAWorkLogTool.getInstance().checkNum(STA_DATE, Operator.getRegion());
        if (check.getErrCode() < 0) {
            this.messageBox(check.getErrText());
            return;
        }
        if (check.getCount("STA_DATE") > 0) { // �Ѵ��ڸ��������ɵ�����
            if (check.getValue("CONFIRM_FLG", 0).equals("Y")) {
                if (t.getSelectedIndex() == 0) { // �ձ�
                    isSubmitD = true; // ��ʶ�ձ��Ѿ��ύ
                    this.setValue("Submit1", true);
                } else { // �±�
                    isSubmitM = true; // ��ʶ �±��Ѿ��ύ
                    this.setValue("Submit2", true);
                }
            } else {
                if (t.getSelectedIndex() == 0) { // �ձ�
                    isSubmitD = false; // ��ʶ�ձ��Ѿ��ύ
                    this.setValue("Submit1", false);
                } else { // �±�
                    isSubmitM = false; // ��ʶ �±��Ѿ��ύ
                    this.setValue("Submit2", false);
                }
            }
        }
    }
    
    
    /**
     * ��ѯ
     */
    public void onQuery() {
        TTabbedPane t = (TTabbedPane) this.getComponent("tTabbedPane_0");
        TTable table;
        String STA_DATE = "";
        if (t.getSelectedIndex() == 0) { // �ձ�
            if ((Boolean) this.callFunction("UI|DAY_PERIOD|isSelected")
                  ) {// wanglong add 20140710
                onQueryDayByPeriod();
            }else{
                table = (TTable) this.getComponent("TableDay");
                STA_DATE = this.getText("STA_DATE1").replace("/", ""); // ��ȡ����
                String dept = this.getValueString("DEPT_CODE1");
                this.TableBind(table, STA_DATE, Operator.getRegion(), dept);
            }
            
        } else if (t.getSelectedIndex() == 1) { // �±�
            if ((Boolean) this.callFunction("UI|MONTH_PERIOD|isSelected")) {// wanglong add 20140710
                onQueryMonthByPeriod();
            }else{
                table = (TTable) this.getComponent("TableMouth");
                STA_DATE = this.getText("STA_DATE2").replace("/", "");
                String dept = this.getValueString("DEPT_CODE2");
                this.TableBind(table, STA_DATE, Operator.getRegion(), dept);
            }
        
        }
    }

 
    /**
     * ��ӡ
     */
    public void onPrint() {
        if ((Boolean) this.callFunction("UI|DAY_PERIOD|isSelected")
                || (Boolean) this.callFunction("UI|MONTH_PERIOD|isSelected")) {// wanglong add 20140710
            int index = ((TTabbedPane) getComponent("tTabbedPane_0")).getSelectedIndex();
            if (index == 0) {// �ձ�
                TTable table = (TTable) this.getComponent("TableDay");
                TParm parmValue = table.getParmValue();
                String DATE_S =
                        StringTool.getString((Timestamp) this.getValue("DAY_START_DATE"),
                                             "yyyyMMdd");
                String DATE_E =
                        StringTool.getString((Timestamp) this.getValue("DAY_END_DATE"), "yyyyMMdd");
                TParm printData = getPrintData(parmValue, DATE_S, DATE_E);
                // System.out.println("��ӡ����==>:"+printData);
                TParm parm = new TParm();
                parm.setData("Title", Operator.getHospitalCHNFullName());
                parm.setData("DATE",
                             this.getText("DAY_START_DATE") + " �� "
                                     + this.getText("DAY_END_DATE"));
                parm.setData("Table1", printData.getData());
                this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_02.jhw", parm);
            } else {// �±�
                TTable table = (TTable) this.getComponent("TableMouth");
                TParm parmValue = table.getParmValue();
                String MONTH_DATE_S =
                        StringTool.getString((Timestamp) this.getValue("MONTH_START_DATE"),
                                             "yyyyMM");
                String MONTH_DATE_E =
                        StringTool.getString((Timestamp) this.getValue("MONTH_END_DATE"), "yyyyMM");
                String DATE_S =
                        StringTool.getString(StringTool.getTimestamp(MONTH_DATE_S, "yyyyMM"),
                                             "yyyyMMdd");
                String DATE_E =
                        StringTool
                                .getString(StringTool.rollDate(StringTool
                                                                       .getTimestamp(STATool.getInstance()
                                                                                             .rollMonth(MONTH_DATE_E,
                                                                                                        1),// ��һ����
                                                                                     "yyyyMM"),
                                                               -1), "yyyyMMdd");
                TParm printData = getPrintData(parmValue, DATE_S, DATE_E);
                // System.out.println("��ӡ����==>:"+printData);
                TParm parm = new TParm();
                parm.setData("Title", Operator.getHospitalCHNFullName());
                parm.setData("DATE",
                             this.getText("MONTH_START_DATE") + " �� "
                                     + this.getText("MONTH_END_DATE"));
                parm.setData("Table1", printData.getData());
                this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_02.jhw", parm);
            }
            return;
        }
        String STADATE = "";
        TTabbedPane t = (TTabbedPane) this.getComponent("tTabbedPane_0");
        if (t.getSelectedIndex() == 0) { // �ձ�ҳǩ
            STADATE = this.getText("STA_DATE1").replace("/", "");
            if (STADATE.trim().length() <= 0) {
                this.messageBox_("��ѡ������!");
                return;
            }
            // if(!checkSubmit(STADATE))//��ѯ����סԺ�����Ƿ��ύ��������־������û���ύ�򷵻�
            // return;

            // //�ж��Ƿ��Ѿ����ɸ����ڵ�����
            // TParm check = STAWorkLogTool.getInstance().checkNum(STADATE);
            // if (check.getCount("STA_DATE") > 0) { //�Ѵ��ڸ��������ɵ�����
            // if (!check.getValue("CONFIRM_FLG", 0).equals("Y")) {
            // this.messageBox_("����û���ύ���������ɱ���");
            // return;
            // }
            // }
        } else if (t.getSelectedIndex() == 1) { // �±�ҳǩ
            STADATE = this.getText("STA_DATE2").replace("/", "");
            if (STADATE.trim().length() <= 0) {
                this.messageBox_("��ѡ������!");
                return;
            }
            // �ж��Ƿ��Ѿ����ɸ��·ݵ�����
            TParm check = STAWorkLogTool.getInstance().checkNum(STADATE, Operator.getRegion());
            if (check.getCount("STA_DATE") > 0) { // �Ѵ��ڸ��������ɵ�����
                if (!check.getValue("CONFIRM_FLG", 0).equals("Y")) {
                    this.messageBox_("����û���ύ���������ɱ���");
                    return;
                }
            }
        }
        TParm printData = this.getPrintDate(STADATE, Operator.getRegion());
        // System.out.println("��ӡ����==>:"+printData);
        TParm parm = new TParm();
        parm.setData("Title", Operator.getHospitalCHNFullName());
        parm.setData("Date", STADATE);
        parm.setData("Table1", printData.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_02.jhw", parm);
    }
    


    private TParm getPrintDate(String STADATE, String regionCode) {
        DecimalFormat df = new DecimalFormat("0.00"); // �趨Double���͸�ʽ
        // ��ȡ����1��2��3������ ��Ϊ�������߲�����
        TParm DeptList = STAWorkLogTool.getInstance().selectDeptList();
        // ��ȡ����STA_DAILY_02�����ݣ����ļ�����Ϊ���������ݣ�
        TParm reData = STAWorkLogTool.getInstance().selectSTA_DAILY_02(STADATE, regionCode);
        String DATE_S = "";
        String DATE_E = "";
        if (STADATE.length() == 6) {
            DATE_S = STADATE + "01"; // ÿ�µ�һ��
            // ��ȡ���·ݵ����һ��
            DATE_E = StringTool.getString(STATool.getInstance().getLastDayOfMonth(STADATE), "yyyyMMdd");
        } else if (STADATE.length() > 6) {
            DATE_S = STADATE;
            DATE_E = STADATE;
        }
        Map deptIPD = STADeptListTool.getInstance().getIPDDeptMap(Operator.getRegion());
        // System.out.println("-=--------deptIPD-----------"+deptIPD);
        TParm printData = new TParm();// ��ӡ����
        for (int i = 0; i < DeptList.getCount(); i++) {
            String d_LEVEL = DeptList.getValue("DEPT_LEVEL", i);// ���ŵȼ�
            String d_CODE = DeptList.getValue("DEPT_CODE", i);// �м䲿��CODE
            String d_DESC = DeptList.getValue("DEPT_DESC", i);// �м䲿������
            int subIndex = 0;// ��¼���ݿ��Ҽ���Ҫ��ȡCODE�ĳ���
            if (d_LEVEL.equals("1")) { // �����һ������ code����Ϊ1
                subIndex = 1;
            } else if (d_LEVEL.equals("2")) { // ����Ƕ������� code����Ϊ3
                subIndex = 3;
                d_DESC = " " + d_DESC;// ����ǰ���ո�
            } else if (d_LEVEL.equals("3")) { // ������������� code����Ϊ5
                subIndex = 5;
                d_DESC = "  " + d_DESC;// ����ǰ���ո�
            }
            /*
             * ������� �����ۼ��Ӳ��ŵ���ֵ ��ʼֵΪ-1����������ۼӵ�����Ϊnull��ô����ʼ��Ϊ-1��
             * �Ǹ�����ñ��������ֶε�ʱ��Ͳ���null
             */
            int DATA_01 = -1;
            int DATA_02 = -1;
            int DATA_03 = -1;
            int DATA_04 = -1;
            int DATA_05 = -1;
            int DATA_06 = -1;
            int DATA_07 = -1;
            int DATA_08 = -1;
            int DATA_08_1 = -1;
            int DATA_09 = -1;
            int DATA_10 = -1;
            int DATA_11 = -1;
            int DATA_12 = -1;
            int DATA_13 = -1;
            int DATA_14 = -1;
            int DATA_15 = -1;
            int DATA_15_1 = -1;
            int DATA_16 = -1;
            int DATA_17 = -1;
            int DATA_18 = -1;
            int DATA_19 = -1;
            int DATA_20 = -1;
            int DATA_21 = -1;
            int DATA_22 = -1;
            int DATA_23 = -1;
            int DATA_24 = -1;
            int DATA_25 = -1;
            int DATA_26 = -1;
            int DATA_27 = -1;
            double DATA_28 = -1;
            int DATA_29 = -1;
            int DATA_30 = -1;
            double DATA_31 = -1;
            double DATA_32 = -1;
            double DATA_33 = -1;
            double DATA_34 = -1;
            int DATA_35 = -1;
            double DATA_36 = -1;
            int DATA_37 = -1;
            double DATA_38 = -1;
            double DATA_39 = -1;
            double DATA_40 = -1;
            double DATA_41 = -1;
            double DATA_41_1 = -1;
            double DATA_41_2 = -1;
            String dept4 = "";// ��¼���������µ��ļ�����
            int deptCount = 0;// ��¼ÿ�������µ��Ӳ��ţ����ʷ����������Ҫȡƽ��ֵ
            int deptInpCount = 0;// ��¼ÿ�������µ�סԺ��������
            // ѭ���������� ȡ�����������Ĳ��ŵ����ݽ����ۼ�
            for (int j = 0; j < reData.getCount(); j++) {
                // �������id��ȡ��ָ�����Ⱥ� �������ѭ���еĲ���CODE��ô�������ѭ�����Ӳ��ţ��ͽ����ۼ�
                if (reData.getValue("DEPT_CODE", j).substring(0, subIndex).equals(d_CODE)) {
                    if (deptIPD.get(reData.getValue("DEPT_CODE", j)) != null) {
                        dept4 += deptIPD.get(reData.getValue("DEPT_CODE", j)).toString() + ",";
                    }
                    DATA_01 = checkNull_i(DATA_01, reData.getValue("DATA_01", j));
                    DATA_02 = checkNull_i(DATA_02, reData.getValue("DATA_02", j));
                    DATA_03 = checkNull_i(DATA_03, reData.getValue("DATA_03", j));
                    DATA_04 = checkNull_i(DATA_04, reData.getValue("DATA_04", j));
                    DATA_05 = checkNull_i(DATA_05, reData.getValue("DATA_05", j));
                    DATA_06 = checkNull_i(DATA_06, reData.getValue("DATA_06", j));
                    DATA_07 = checkNull_i(DATA_07, reData.getValue("DATA_07", j));
                    DATA_08 = checkNull_i(DATA_08, reData.getValue("DATA_08", j));
                    DATA_08_1 = checkNull_i(DATA_08_1, reData.getValue("DATA_08_1", j));
                    DATA_09 = checkNull_i(DATA_09, reData.getValue("DATA_09", j));
                    DATA_10 = checkNull_i(DATA_10, reData.getValue("DATA_10", j));
                    DATA_11 = checkNull_i(DATA_11, reData.getValue("DATA_11", j));
                    DATA_12 = checkNull_i(DATA_12, reData.getValue("DATA_12", j));
                    DATA_13 = checkNull_i(DATA_13, reData.getValue("DATA_13", j));
                    DATA_14 = checkNull_i(DATA_14, reData.getValue("DATA_14", j));
                    DATA_15 = checkNull_i(DATA_15, reData.getValue("DATA_15", j));
                    DATA_15_1 = checkNull_i(DATA_15_1, reData.getValue("DATA_15_1", j));
                    DATA_16 = checkNull_i(DATA_16, reData.getValue("DATA_16", j));
                    DATA_17 = checkNull_i(DATA_17, reData.getValue("DATA_17", j));
                    DATA_18 = checkNull_i(DATA_18, reData.getValue("DATA_18", j));
                    DATA_19 = checkNull_i(DATA_19, reData.getValue("DATA_19", j));
                    DATA_20 = checkNull_i(DATA_20, reData.getValue("DATA_20", j));
                    DATA_21 = checkNull_i(DATA_21, reData.getValue("DATA_21", j));
                    DATA_22 = checkNull_i(DATA_22, reData.getValue("DATA_22", j));
                    DATA_23 = checkNull_i(DATA_23, reData.getValue("DATA_23", j));
                    DATA_24 = checkNull_i(DATA_24, reData.getValue("DATA_24", j));
                    DATA_25 = checkNull_i(DATA_25, reData.getValue("DATA_25", j));
                    DATA_26 = checkNull_i(DATA_26, reData.getValue("DATA_26", j));
                    DATA_27 = checkNull_i(DATA_27, reData.getValue("DATA_27", j));
                    DATA_28 = checkNull_d(DATA_28, reData.getValue("DATA_28", j));
                    DATA_29 = checkNull_i(DATA_29, reData.getValue("DATA_29", j));
                    DATA_30 = checkNull_i(DATA_30, reData.getValue("DATA_30", j));
                    DATA_35 = checkNull_i(DATA_35, reData.getValue("DATA_35", j));
                    DATA_38 = checkNull_d(DATA_38, reData.getValue("DATA_38", j));
                    deptCount++;// �ۼƹ����ܵĶ��ٸ�4�����ŵ�����
                    if (STAWorkLogTool.getInstance().checkIPDDept(reData.getValue("DEPT_CODE", j))) {
                        deptInpCount++;
                    }
                }
            }
            if (DATA_10 + DATA_15 > 0) {
                DATA_31 = (double) DATA_11 / ((double) DATA_10 + (double) DATA_15) * 100; // ������
                DATA_32 = (double) DATA_12 / ((double) DATA_10 + (double) DATA_15) * 100; // ��ת��
                DATA_33 = (double) DATA_13 / ((double) DATA_10 + (double) DATA_15) * 100; // ������
            }
            if (DATA_19 > 0) {
                if ((DATA_10 + DATA_15) > 0)
                    DATA_34 = (double) (DATA_10 + DATA_15) / (double) DATA_19; // ������ת�� = ��Ժ����(1~5)/ƽ�����Ų�����
            }
            
            if ("month".equals(resultType)) {// �±�
                DATA_36 = STAWorkLogTool.getInstance().countBED_USE_RATE(DATA_20, MDays, DATA_19);// ����ʹ���� ��ͳ����Ч
            }
            if (DATA_09 > 0) DATA_37 = DATA_21 / DATA_09; // ����ƽ��סԺ����
            if (DATA_26 > 0) DATA_39 = (double) DATA_27 / (double) DATA_26 * 100; // �޾��пڻ�ŧ��
            if (DATA_28 > 0) DATA_40 = (double) DATA_29 / (double) DATA_28 * 100; // Σ�ز������ȳɹ�%
            if (DATA_16 > 0) DATA_41 = (double) DATA_30 / (double) DATA_16 * 100; // ������
            if ((DATA_10 + DATA_15) > 0) {
                DATA_41_1 = (double) DATA_23 / ((double) DATA_10 + (double) DATA_15) * 100; // ������Ϸ�����%
                DATA_41_2 = (double) DATA_25 / ((double) DATA_10 + (double) DATA_15) * 100;
            }
            // ������������������� ��ôת��������Ҫ������ȡ
            // ��Ϊͬһ���������µ��ļ�����֮���ת�Ʋ������������ҵ�������(�����Ժ����)
            if (d_LEVEL.equals("3")) {
                if (dept4.length() > 0) {
                    String[] d = dept4.substring(0, dept4.length() - 1).split(",");
                    DATA_08_1 = STAIn_03Tool.getInstance().getDept_TranInNum(d, DATE_S, DATE_E);
                    DATA_15_1 = STAIn_03Tool.getInstance().getDept_TranOutNum(d, DATE_S, DATE_E);
                }
            }
            printData.addData("DEPT_DESC", d_DESC);
            printData.addData("DATA_01", DATA_01 == -1 ? "" : DATA_01);
            printData.addData("DATA_02", DATA_02 == -1 ? "" : DATA_02);
            printData.addData("DATA_03", DATA_03 == -1 ? "" : DATA_03);
            printData.addData("DATA_04", DATA_04 == -1 ? "" : DATA_04);
            printData.addData("DATA_05", DATA_05 == -1 ? "" : DATA_05);
            printData.addData("DATA_06", DATA_06 == -1 ? "" : DATA_06);
            printData.addData("DATA_07", DATA_07 == -1 ? "" : DATA_07);
            printData.addData("DATA_08", DATA_08 == -1 ? "" : DATA_08);
            printData.addData("DATA_08_1", DATA_08_1 == -1 ? "" : DATA_08_1);
            printData.addData("DATA_09", DATA_09 == -1 ? "" : DATA_09);
            printData.addData("DATA_10", DATA_10 == -1 ? "" : DATA_10);
            printData.addData("DATA_11", DATA_11 == -1 ? "" : DATA_11);
            printData.addData("DATA_12", DATA_12 == -1 ? "" : DATA_12);
            printData.addData("DATA_13", DATA_13 == -1 ? "" : DATA_13);
            printData.addData("DATA_14", DATA_14 == -1 ? "" : DATA_14);
            printData.addData("DATA_15", DATA_15 == -1 ? "" : DATA_15);
            printData.addData("DATA_15_1", DATA_15_1 == -1 ? "" : DATA_15_1);
            printData.addData("DATA_16", DATA_16 == -1 ? "" : DATA_16);
            printData.addData("DATA_17", DATA_17 == -1 ? "" : DATA_17);
            printData.addData("DATA_18", DATA_18 == -1 ? "" : DATA_18);
            printData.addData("DATA_19", DATA_19 == -1 ? "" : DATA_19);
            printData.addData("DATA_20", DATA_20 == -1 ? "" : DATA_20);
            printData.addData("DATA_21", DATA_21 == -1 ? "" : DATA_21);
            printData.addData("DATA_22", DATA_22 == -1 ? "" : DATA_22);
            printData.addData("DATA_23", DATA_23 == -1 ? "" : DATA_23);
            printData.addData("DATA_24", DATA_24 == -1 ? "" : DATA_24);
            printData.addData("DATA_25", DATA_25 == -1 ? "" : DATA_25);
            printData.addData("DATA_26", DATA_26 == -1 ? "" : DATA_26);
            printData.addData("DATA_27", DATA_27 == -1 ? "" : DATA_27);
            printData.addData("DATA_28", DATA_28 == -1 ? "" : DATA_28);
            printData.addData("DATA_29", DATA_29 == -1 ? "" : DATA_29);
            printData.addData("DATA_30", DATA_30 == -1 ? "" : DATA_30);
            printData.addData("DATA_31", DATA_31 == -1 ? "" : df.format(DATA_31));
            printData.addData("DATA_32", DATA_32 == -1 ? "" : df.format(DATA_32));
            printData.addData("DATA_33", DATA_33 == -1 ? "" : df.format(DATA_33));
            printData.addData("DATA_34", DATA_34 == -1 ? "" : df.format(DATA_34));
            printData.addData("DATA_35", DATA_35 == -1 ? "" : DATA_35);
            printData.addData("DATA_36", DATA_36 == -1 ? "" : df.format(DATA_36));
            printData.addData("DATA_37", DATA_37 == -1 ? "" : DATA_37);
            printData.addData("DATA_38", DATA_38 == -1 ? "" : df.format(DATA_38));
            printData.addData("DATA_39", DATA_39 == -1 ? "" : df.format(DATA_39));
            printData.addData("DATA_40", DATA_40 == -1 ? "" : df.format(DATA_40));
            printData.addData("DATA_41", DATA_41 == -1 ? "" : df.format(DATA_41));
            printData.addData("DATA_41_1", DATA_41_1 == -1 ? "" : df.format(DATA_41_1));
            printData.addData("DATA_41_2", DATA_41_2 == -1 ? "" : df.format(DATA_41_2));
        }
        // System.out.println(""+printData);
        printData.setCount(DeptList.getCount());
        printData.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
        printData.addData("SYSTEM", "COLUMNS", "DATA_01");
        printData.addData("SYSTEM", "COLUMNS", "DATA_02");
        printData.addData("SYSTEM", "COLUMNS", "DATA_03");
        printData.addData("SYSTEM", "COLUMNS", "DATA_04");
        printData.addData("SYSTEM", "COLUMNS", "DATA_05");
        printData.addData("SYSTEM", "COLUMNS", "DATA_06");
        printData.addData("SYSTEM", "COLUMNS", "DATA_07");
        printData.addData("SYSTEM", "COLUMNS", "DATA_08");
        printData.addData("SYSTEM", "COLUMNS", "DATA_08_1");
        printData.addData("SYSTEM", "COLUMNS", "DATA_09");
        printData.addData("SYSTEM", "COLUMNS", "DATA_10");
        printData.addData("SYSTEM", "COLUMNS", "DATA_11");
        printData.addData("SYSTEM", "COLUMNS", "DATA_12");
        printData.addData("SYSTEM", "COLUMNS", "DATA_13");
        printData.addData("SYSTEM", "COLUMNS", "DATA_14");
        printData.addData("SYSTEM", "COLUMNS", "DATA_15");
        printData.addData("SYSTEM", "COLUMNS", "DATA_15_1");
        printData.addData("SYSTEM", "COLUMNS", "DATA_16");
        printData.addData("SYSTEM", "COLUMNS", "DATA_17");
        printData.addData("SYSTEM", "COLUMNS", "DATA_18");
        printData.addData("SYSTEM", "COLUMNS", "DATA_19");
        printData.addData("SYSTEM", "COLUMNS", "DATA_20");
        printData.addData("SYSTEM", "COLUMNS", "DATA_21");
        printData.addData("SYSTEM", "COLUMNS", "DATA_22");
        printData.addData("SYSTEM", "COLUMNS", "DATA_23");
        printData.addData("SYSTEM", "COLUMNS", "DATA_24");
        printData.addData("SYSTEM", "COLUMNS", "DATA_25");
        printData.addData("SYSTEM", "COLUMNS", "DATA_26");
        printData.addData("SYSTEM", "COLUMNS", "DATA_27");
        printData.addData("SYSTEM", "COLUMNS", "DATA_28");
        printData.addData("SYSTEM", "COLUMNS", "DATA_29");
        printData.addData("SYSTEM", "COLUMNS", "DATA_30");
        printData.addData("SYSTEM", "COLUMNS", "DATA_31");
        printData.addData("SYSTEM", "COLUMNS", "DATA_32");
        printData.addData("SYSTEM", "COLUMNS", "DATA_33");
        printData.addData("SYSTEM", "COLUMNS", "DATA_34");
        printData.addData("SYSTEM", "COLUMNS", "DATA_35");
        printData.addData("SYSTEM", "COLUMNS", "DATA_36");
        printData.addData("SYSTEM", "COLUMNS", "DATA_37");
        printData.addData("SYSTEM", "COLUMNS", "DATA_38");
        printData.addData("SYSTEM", "COLUMNS", "DATA_39");
        printData.addData("SYSTEM", "COLUMNS", "DATA_40");
        printData.addData("SYSTEM", "COLUMNS", "DATA_41");
        printData.addData("SYSTEM", "COLUMNS", "DATA_41_1");
        printData.addData("SYSTEM", "COLUMNS", "DATA_41_2");
        // System.out.println("----------printData--------------------"+printData);
        return printData;
    }

    /**
     * ���
     */
    public void onClear() {
        this.clearValue("DEPT_CODE1;DEPT_CODE2;SubmitFlg;Submit1;Submit2");
        Timestamp time = SystemTool.getInstance().getDate();
        this.setValue("STA_DATE1", StringTool.rollDate(time, -1)); // ǰһ��
        this.setValue("STA_DATE2", STATool.getInstance().getLastMonth());  // ���ó�ʼʱ�䣨�ϸ��£�
        this.setValue("DAY_START_DATE", StringTool.rollDate(time, -8));//wanglong add 20140710
        this.setValue("DAY_END_DATE", StringTool.rollDate(time, -1));
        this.setValue("MONTH_START_DATE",
                      StringTool.getTimestamp(STATool.getInstance()
                                                      .rollMonth(StringTool.getString(time,
                                                                                      "yyyyMM"), -2),
                                              "yyyyMM"));
        this.setValue("MONTH_END_DATE", STATool.getInstance().getLastMonth());
        this.callFunction("UI|TableDay|setParmValue", new TParm());//add by wanglong 20131126
        this.callFunction("UI|TableMouth|setParmValue", new TParm());
        this.callFunction("UI|DAY_DATE|setSelected",true);//wanglong add 20140710
        this.callFunction("UI|MONTH_DATE|setSelected",true);
        this.callFunction("UI|save|setEnabled", true);
        this.callFunction("UI|generate|setEnabled", true);
        
    }

    /**
     * �ж������Ƿ�Ϊ�� �����ض�Ӧ�ı���ֵ��������
     * @param c_num int ����
     * @param data Object ����ֵ
     * @return int
     */
    private int checkNull_i(int c_num, String data) {
        // ������ݲ�Ϊnull
        if (data.trim().length() > 0) {
            // ����ۼӱ����Ѿ����ǳ�ʼֵ-1
            if (c_num != -1) {
                c_num += Integer.valueOf(data);
                return c_num;
            }
            // ��������� -1 ��ô�������ݵ�ֵ
            if (c_num == -1) {
                return Integer.valueOf(data);
            }
        }
        return c_num;
    }

    /**
     * �ж������Ƿ�Ϊ�� �����ض�Ӧ�ı���ֵ����С����
     * @param c_num double
     * @param data Object
     * @return double
     */
    private double checkNull_d(double c_num, String data) {
        // ������ݲ�Ϊnull
        if (data.trim().length() > 0) {
            // ����ۼӱ����Ѿ����ǳ�ʼֵ-1
            if (c_num != -1) {
                return c_num + Double.valueOf(data);
            }
            // ��������� -1 ��ô�������ݵ�ֵ
            if (c_num == -1) {
                return Double.valueOf(data);
            }
        }
        return c_num;
    }

    /**
     * ��ѯ�����ŵ� ��������־���Ƿ��ύ
     * @param STADATE String
     * @return boolean
     */
    public boolean checkSubmit(String STADATE) {
        // �������״̬
        int reFlg =
                STATool.getInstance().checkCONFIRM_FLG("STA_OPD_DAILY", STADATE, Operator.getRegion());
        if (reFlg != 2) {// û���ύ�ż�����־
            this.messageBox_("û���ύ�ż�����־������ִ�в���");
            return false;
        }
        TParm re = STAWorkLogTool.getInstance().checkSubmit(STADATE, Operator.getRegion());
        if (re.getCount() > 0) {
            String message = "���²��Ż�û���ύ��������־������ִ�в���";
            for (int i = 0; i < re.getCount(); i++) {
                message += "\n" + re.getValue("DEPT_DESC", i);
            }
            this.messageBox_(message);
            return false;
        }
        return true;
    }

    /**
     * ����ĳһ���ж�����
     * @param month String ��ʽ yyyy/MM
     * @return int
     */
    private int getDaysOfMonth(String month) {
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM");
        try {
            rightNow.setTime(simpleDate.parse(month)); // Ҫ��������Ҫ���·�
        } catch (Exception e) {
            e.printStackTrace();
        }
        int days = rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);
        return days;
    }

    /**
     * �޸��¼�����
     * @param obj Object
     */
    public void onChangeTableValue() {
        DecimalFormat df = new DecimalFormat("0.00"); // �趨Double���͸�ʽ
        TTabbedPane t = (TTabbedPane) this.getComponent("tTabbedPane_0");
        TTable table = null;
        if (t.getSelectedIndex() == 0)
            table = (TTable) this.getComponent("TableDay");
        else if (t.getSelectedIndex() == 1)
            table = (TTable) this.getComponent("TableMouth");
        table.acceptText();
        // �ϼ� shibl 20210910 add
        int data01 = 0;// ���������ܼƺϼ�<1>
        int data02 = 0;// �����˴����ϼ�<2>
        int data03 = 0;// �����˴κϼ�<3>
        int data04 = 0;// ���������˴κϼ�<4>
        int data05 = 0;// ���۲������ϼ�<5>
        int data06 = 0;// �������������ϼ�<6>
        int data07 = 0;// �ڳ�ʵ�в��˺ϼ�<7>
        int data08 = 0;// ��Ժ�����ϼ�<8>
        int data0801 = 0;// ����ת��ϼ�<8-1>
        int data09 = 0;// ��Ժ�����ܼƺϼ�<9>
        int data10 = 0;// �������ƺϼ�<10>
        int data11 = 0;// �����ϼ�<11>
        int data12 = 0;// ��ת�ϼ�<12>
        int data13 = 0;// δ���ϼ�<13>
        int data14 = 0;// �����ϼ�<14>
        int data15 = 0;// �����ϼ�<15>
        int data1501 = 0;// ת�����������ϼ�<15_01>
        int data16 = 0;// ʵ�в������ϼ�<16>
        int data17 = 0;// ��ĩʵ�в�����<17>
        int data18 = 0;// ʵ�ʿ����ܴ������ϼ�<18>
        int data19 = 0;// ƽ�����Ŵ����ϼ�<19>
        int data20 = 0;// ʵ��ռ�������ϼ�
        int data21 = 0;// ��Ժ����סԺ�����ϼ�
        int data22 = 0;// ������Ϸ����ϼ�
        int data23 = 0;// ������Ϸ������ϼ�
        int data24 = 0;// ��Ժ��Ϸ������ϼ�
        int data25 = 0;// ��ǰ����������ϼ�
        int data26 = 0;// �޾��п��������ϼ�
        int data27 = 0;// �޾��пڻ�ŧ���ϼ�
        int data28 = 0;// Σ�ز����������ϼ�
        int data29 = 0;// Σ�ز������ȳɹ��ϼ�
        int data30 = 0;// �� �����ϼ�
        double data31 = 0;// ������
        double data32 = 0;// ��ת��
        double data33 = 0;// ������
        double data34 = 0;// ������ת
        int data35 = 0;// ����������
        double data36 = 0;// ����ʹ����
        int data37 = 0;// ����ƽ��סԺ��<37>
        double data38 = 0;// ��Ϸ�����
        double data39 = 0;// �޾��пڻ�ŧ��<39>
        double data40 = 0;// Σ�ز������ȳɹ�%<40>
        double data41 = 0;// ������%<41>
        double data4101 = 0;// ������Ϸ�����%<41_1>
        double data4102 = 0;// ��ǰ������Ϸ�����%<41_2>
        TParm parm = table.getParmValue();
        for (int i = 0; i < parm.getCount("DATA_01")-1; i++) {
            data01 += parm.getInt("DATA_01", i);
            data02 += parm.getInt("DATA_02", i);
            data03 += parm.getInt("DATA_03", i);
            data04 += parm.getInt("DATA_04", i);
            data05 += parm.getInt("DATA_05", i);
            data06 += parm.getInt("DATA_06", i);
            data07 += parm.getInt("DATA_07", i);
            data08 += parm.getInt("DATA_08", i);
            data0801 += parm.getInt("DATA_08_1", i);
            data09 += parm.getInt("DATA_09", i);
            data10 += parm.getInt("DATA_10", i);
            data11 += parm.getInt("DATA_11", i);
            data12 += parm.getInt("DATA_12", i);
            data13 += parm.getInt("DATA_13", i);
            data14 += parm.getInt("DATA_14", i);
            data15 += parm.getInt("DATA_15", i);
            data1501 += parm.getInt("DATA_15_1", i);
            data16 += parm.getInt("DATA_16", i);
            data17 += parm.getInt("DATA_17", i);
            data18 += parm.getInt("DATA_18", i);
            data19 += parm.getInt("DATA_19", i);
            data20 += parm.getInt("DATA_20", i);
            data21 += parm.getInt("DATA_21", i);
            data22 += parm.getInt("DATA_22", i);
            data23 += parm.getInt("DATA_23", i);
            data24 += parm.getInt("DATA_24", i);
            data25 += parm.getInt("DATA_25", i);
            data26 += parm.getInt("DATA_26", i);
            data27 += parm.getInt("DATA_27", i);
            data28 += parm.getInt("DATA_28", i);
            data29 += parm.getInt("DATA_29", i);
            data30 += parm.getInt("DATA_30", i);
        }
        parm.setData("DATA_01",parm.getCount()-1, data01);
        parm.setData("DATA_02",parm.getCount()-1, data02);
        parm.setData("DATA_03",parm.getCount()-1, data03);
        parm.setData("DATA_04",parm.getCount()-1, data04);
        parm.setData("DATA_05",parm.getCount()-1, data05);
        parm.setData("DATA_06",parm.getCount()-1, data06);
        parm.setData("DATA_07",parm.getCount()-1, data07);
        parm.setData("DATA_08",parm.getCount()-1, data08);
        parm.setData("DATA_08_1",parm.getCount()-1, data0801);
        parm.setData("DATA_09",parm.getCount()-1, data09);
        parm.setData("DATA_10",parm.getCount()-1, data10);
        parm.setData("DATA_11",parm.getCount()-1, data11);
        parm.setData("DATA_12",parm.getCount()-1, data12);
        parm.setData("DATA_13",parm.getCount()-1, data13);
        parm.setData("DATA_14",parm.getCount()-1, data14);
        parm.setData("DATA_15",parm.getCount()-1, data15);
        parm.setData("DATA_16",parm.getCount()-1, data16);
        parm.setData("DATA_17",parm.getCount()-1, data17);
        parm.setData("DATA_18",parm.getCount()-1, data18);
        parm.setData("DATA_15_1",parm.getCount()-1, data1501);
        parm.setData("DATA_19",parm.getCount()-1, data19);
        parm.setData("DATA_20",parm.getCount()-1, data20);
        parm.setData("DATA_21",parm.getCount()-1, data21);
        parm.setData("DATA_22",parm.getCount()-1, data22);
        parm.setData("DATA_23",parm.getCount()-1, data23);
        parm.setData("DATA_24",parm.getCount()-1, data24);
        parm.setData("DATA_25",parm.getCount()-1, data25);
        parm.setData("DATA_26",parm.getCount()-1, data26);
        parm.setData("DATA_27",parm.getCount()-1, data27);
        parm.setData("DATA_28",parm.getCount()-1, data28);
        parm.setData("DATA_29",parm.getCount()-1, data29);
        parm.setData("DATA_30",parm.getCount()-1, data30);
        parm.setData("DATA_30",parm.getCount()-1, data30);
        double O_num = data10 + data15; // ���ڼ���������
        if (O_num == 0) {
            parm.setData("DATA_31",parm.getCount()-1, ""); // ������<31>
            parm.setData("DATA_32",parm.getCount()-1, ""); // ��ת��<32>
            parm.setData("DATA_33",parm.getCount()-1, ""); // ������<33>
        } else {
            parm.setData("DATA_31",parm.getCount()-1, df.format((double) data11 / O_num * 100)); // ������<31>
            parm.setData("DATA_32",parm.getCount()-1, df.format((double) data12 / O_num * 100)); // ��ת��<32>
            parm.setData("DATA_33",parm.getCount()-1, df.format((double) data14 / O_num * 100)); // ������<33>
        }
        // ������ת(��)
        if (t.getSelectedIndex() == 1) { // �±� ������ת(��)=���ڳ�Ժ����/ ͬ��ƽ�����Ų�����
            if (data18 > 0 && data09 > 0) {
                parm.setData("DATA_34",parm.getCount()-1, df.format((double) data09 / ((double) data18 / MDays)));
            } else
                parm.setData("DATA_34",parm.getCount()-1, "");
        } else { // �ձ�
            parm.setData("DATA_34",parm.getCount()-1, "");// �㷨����
        }
        parm.setData("DATA_35",parm.getCount()-1, ""); // ����������<35> �㷨����
        if (t.getSelectedIndex() == 1) { // �±�
            double DATA_36 = 0;
            DATA_36 = STAWorkLogTool.getInstance().countBED_USE_RATE(data16, MDays, data18 / MDays); // ����ʹ����<36>
            parm.setData("DATA_36",parm.getCount()-1, df.format(DATA_36));
        } else { // �ձ�
            parm.setData("DATA_36",parm.getCount()-1, "");// �ձ���ͳ��
        }
        if (data09 == 0)
            parm.setData("DATA_37",parm.getCount()-1, "0");
        else
            parm.setData("DATA_37",parm.getCount()-1, df.format((double) data21 / (double) data09)); // ����ƽ��סԺ��<37>
        parm.setData("DATA_38",parm.getCount()-1, ""); // ��Ϸ�����<38>
        if (data26 == 0)
            parm.setData("DATA_39",parm.getCount()-1, "0");
        else
            parm.setData("DATA_39",parm.getCount()-1, df.format((double) data27 / (double) data26 * 100)); // �޾��пڻ�ŧ��<39>
        if (data28 == 0)
            parm.setData("DATA_40",parm.getCount()-1, "100");
        else
            parm.setData("DATA_40",parm.getCount()-1, df.format((double) data29 / (double) data28 * 100));// Σ�ز������ȳɹ�%<40>
        if (data16 == 0)
            parm.setData("DATA_41",parm.getCount()-1, "100");
        else
            parm.setData("DATA_41",parm.getCount()-1, df.format((double) data30 / (double) data16 * 100)); // ������%<41> = ������/ʵ�в�����
        if (data10 == 0 && data15 == 0)
            parm.setData("DATA_41_1",parm.getCount()-1, "");
        else if (data10 + data15 == 0)
            parm.setData("DATA_41_1",parm.getCount()-1, "100");
        else
            parm.setData("DATA_41_1",parm.getCount()-1, df.format((double) data23 / (double) (data10 + data15) * 100)); // ������Ϸ�����%<41_1>
        if (data10 == 0 && data15 == 0)
            parm.setData("DATA_41_2",parm.getCount()-1, "");
        else if (data10 + data15 == 0)
            parm.setData("DATA_41_2",parm.getCount()-1, "0");
        else
            parm.setData("DATA_41_2",parm.getCount()-1, df.format((double) data25 / (double) (data10 + data15) * 100)); // ��ǰ������Ϸ�����%<41_2>
        table.setParmValue(parm);
    }
    
    /**
     * ���Excel
     */
    public void onExport() {//add by wangbin 20140707
        TTabbedPane t = (TTabbedPane) this.getComponent("tTabbedPane_0");
        TTable table = new TTable();
        String title = "";
        
        if (t.getSelectedIndex() == 0) { // �ձ�
            table = (TTable) this.getComponent("TableDay");
            title = "�����ձ�";
        } else if (t.getSelectedIndex() == 1) { // �±�
            table = (TTable) this.getComponent("TableMouth");
            title = "�����±�";
        }
        
        if (table.getRowCount() > 0) {
            ExportExcelUtil.getInstance().exportExcel(table, title);
        } else {
            this.messageBox("û����Ҫ����������");
            return;
        }
    }

    /**
     * ��ѡ��ť�¼�
     */
    public void onChooseRBT() {// wanglong add 20140710
        int index = ((TTabbedPane) getComponent("tTabbedPane_0")).getSelectedIndex();
        if (index == 0) {// �ձ�
            if ((Boolean) this.callFunction("UI|DAY_PERIOD|isSelected")) {
                this.callFunction("UI|save|setEnabled", false);
                this.callFunction("UI|generate|setEnabled", false);
                return;
            }
        } else {// �±�
            if ((Boolean) this.callFunction("UI|MONTH_PERIOD|isSelected")) {
                this.callFunction("UI|save|setEnabled", false);
                this.callFunction("UI|generate|setEnabled", false);
                return;
            }
        }
        this.callFunction("UI|save|setEnabled", true);
        this.callFunction("UI|generate|setEnabled", true);
    }
    
    /**
     * �����������ѯ�ձ�����
     */
    public void onQueryDayByPeriod() {// wanglong add 20140710
        TParm parm = new TParm();
        parm.setData("REGION_CODE", Operator.getRegion());
        if (this.getValue("DAY_START_DATE") == null || this.getValue("DAY_END_DATE") == null) {
            this.messageBox("ʱ�䲻��Ϊ��");
            return;
        }
        parm.setData("STA_START_DATE",
                     StringTool.getString((Timestamp) this.getValue("DAY_START_DATE"), "yyyyMMdd"));
        parm.setData("STA_END_DATE",
                     StringTool.getString((Timestamp) this.getValue("DAY_END_DATE"), "yyyyMMdd"));
        if (!this.getValueString("DEPT_CODE1").equals("")) {
            parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE1"));
        }
        if (!this.getValueString("SubmitFlg").equals("")) {
            parm.setData("CONFIRM_FLG", this.getValueString("SubmitFlg"));
        }
        TParm re = STAWorkLogTool.getInstance().selectDataByPeriod(parm, "D");
        if (re.getErrCode() < 0) {
            result = null;
            return;
        }
        if (re.getCount() < 1) {
            this.messageBox("E0116");// û������
            return;
        }
        re = addSumRow(re, parm, "D");
        TTable table = (TTable) this.getComponent("TableDay");
        table.setParmValue(re);
        resultType = "day";
    }

    /**
     * ���·������ѯ�±�����
     */
    public void onQueryMonthByPeriod() {// wanglong add 20140710
        TParm parm = new TParm();
        parm.setData("REGION_CODE", Operator.getRegion());
        if (this.getValue("MONTH_START_DATE") == null || this.getValue("MONTH_END_DATE") == null) {
            this.messageBox("ʱ�䲻��Ϊ��");
            return;
        }
        parm.setData("STA_START_DATE",
                     StringTool.getString((Timestamp) this.getValue("MONTH_START_DATE"), "yyyyMM"));
        parm.setData("STA_END_DATE",
                     StringTool.getString((Timestamp) this.getValue("MONTH_END_DATE"), "yyyyMM"));
        if (!this.getValueString("DEPT_CODE2").equals("")) {
            parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE2"));
        }
        TParm re = STAWorkLogTool.getInstance().selectDataByPeriod(parm, "M");
        if (re.getErrCode() < 0) {
            result = null;
            return;
        }
        if (re.getCount() < 1) {
            this.messageBox("E0116");// û������
            return;
        }
        re = addSumRow(re, parm, "M");
        TTable table = (TTable) this.getComponent("TableMouth");
        table.setParmValue(re);
        resultType = "month";
    }
    
    /**
     * �����ܼ���
     * @param re Ԥ������
     * @param parm ��ѯ����
     * @param type �ձ� or �±�
     * @return
     */
    public TParm addSumRow(TParm re, TParm parm, String type) {// wanglong add 20140710
        MDays =
                STAWorkLogTool.getInstance().countDays(parm.getValue("STA_START_DATE"),
                                                       parm.getValue("STA_END_DATE"), type);
        DecimalFormat df = new DecimalFormat("0.00"); // �趨Double���͸�ʽ
        int data01 = 0;// ���������ܼƺϼ�<1>
        int data02 = 0;// �����˴����ϼ�<2>
        int data03 = 0;// �����˴κϼ�<3>
        int data04 = 0;// ���������˴κϼ�<4>
        int data05 = 0;// ���۲������ϼ�<5>
        int data06 = 0;// �������������ϼ�<6>
        int data07 = 0;// �ڳ�ʵ�в��˺ϼ�<7>
        int data08 = 0;// ��Ժ�����ϼ�<8>
        int data0801 = 0;// ����ת��ϼ�<8-1>
        int data09 = 0;// ��Ժ�����ܼƺϼ�<9>
        int data10 = 0;// �������ƺϼ�<10>
        int data11 = 0;// �����ϼ�<11>
        int data12 = 0;// ��ת�ϼ�<12>
        int data13 = 0;// δ���ϼ�<13>
        int data14 = 0;// �����ϼ�<14>
        int data15 = 0;// �����ϼ�<15>
        int data1501 = 0;// ת�����������ϼ�<15_01>
        int data16 = 0;// ʵ�в������ϼ�<16>
        int data17 = 0;// ��ĩʵ�в�����<17>
        int data18 = 0;// ʵ�ʿ����ܴ������ϼ�<18>
        int data19 = 0;// ƽ�����Ŵ����ϼ�<19>
        int data20 = 0;// ʵ��ռ�������ϼ�
        int data21 = 0;// ��Ժ����סԺ�����ϼ�
        int data22 = 0;// ������Ϸ����ϼ�
        int data23 = 0;// ������Ϸ������ϼ�
        int data24 = 0;// ��Ժ��Ϸ������ϼ�
        int data25 = 0;// ��ǰ����������ϼ�
        int data26 = 0;// �޾��п��������ϼ�
        int data27 = 0;// �޾��пڻ�ŧ���ϼ�
        int data28 = 0;// Σ�ز����������ϼ�
        int data29 = 0;// Σ�ز������ȳɹ��ϼ�
        int data30 = 0;// �� �����ϼ�
        double data31 = 0;// ������
        double data32 = 0;// ��ת��
        double data33 = 0;// ������
        double data34 = 0;// ������ת
        int data35 = 0;// ����������
        double data36 = 0;// ����ʹ����
        int data37 = 0;// ����ƽ��סԺ��<37>
        double data38 = 0;// ��Ϸ�����
        double data39 = 0;// �޾��пڻ�ŧ��<39>
        double data40 = 0;// Σ�ز������ȳɹ�%<40>
        double data41 = 0;// ������%<41>
        double data4101 = 0;// ������Ϸ�����%<41_1>
        double data4102 = 0;// ��ǰ������Ϸ�����%<41_2>
        for (int i = 0; i < re.getCount(); i++) {
            data01 += re.getInt("DATA_01", i);
            data02 += re.getInt("DATA_02", i);
            data03 += re.getInt("DATA_03", i);
            data04 += re.getInt("DATA_04", i);
            data05 += re.getInt("DATA_05", i);
            data06 += re.getInt("DATA_06", i);
            data07 += re.getInt("DATA_07", i);
            data08 += re.getInt("DATA_08", i);
            data0801 += re.getInt("DATA_08_1", i);
            data09 += re.getInt("DATA_09", i);
            data10 += re.getInt("DATA_10", i);
            data11 += re.getInt("DATA_11", i);
            data12 += re.getInt("DATA_12", i);
            data13 += re.getInt("DATA_13", i);
            data14 += re.getInt("DATA_14", i);
            data15 += re.getInt("DATA_15", i);
            data1501 += re.getInt("DATA_15_1", i);
            data16 += re.getInt("DATA_16", i);
            data17 += re.getInt("DATA_17", i);
            data18 += re.getInt("DATA_18", i);
            data19 += re.getInt("DATA_19", i);
            data20 += re.getInt("DATA_20", i);
            data21 += re.getInt("DATA_21", i);
            data22 += re.getInt("DATA_22", i);
            data23 += re.getInt("DATA_23", i);
            data24 += re.getInt("DATA_24", i);
            data25 += re.getInt("DATA_25", i);
            data26 += re.getInt("DATA_26", i);
            data27 += re.getInt("DATA_27", i);
            data28 += re.getInt("DATA_28", i);
            data29 += re.getInt("DATA_29", i);
            data30 += re.getInt("DATA_30", i);
        }
        re.addData("SUBMIT_FLG", "N");
        re.addData("DEPT_CODE", "�ϼ�:");
        re.addData("DATA_01", data01);
        re.addData("DATA_02", data02);
        re.addData("DATA_03", data03);
        re.addData("DATA_04", data04);
        re.addData("DATA_05", data05);
        re.addData("DATA_06", data06);
        re.addData("DATA_07", data07);
        re.addData("DATA_08", data08);
        re.addData("DATA_08_1", data0801);
        re.addData("DATA_09", data09);
        re.addData("DATA_10", data10);
        re.addData("DATA_11", data11);
        re.addData("DATA_12", data12);
        re.addData("DATA_13", data13);
        re.addData("DATA_14", data14);
        re.addData("DATA_15", data15);
        re.addData("DATA_16", data16);
        re.addData("DATA_17", data17);
        re.addData("DATA_18", data18);
        re.addData("DATA_15_1", data1501);
        re.addData("DATA_19", data19);
        re.addData("DATA_20", data20);
        re.addData("DATA_21", data21);
        re.addData("DATA_22", data22);
        re.addData("DATA_23", data23);
        re.addData("DATA_24", data24);
        re.addData("DATA_25", data25);
        re.addData("DATA_26", data26);
        re.addData("DATA_27", data27);
        re.addData("DATA_28", data28);
        re.addData("DATA_29", data29);
        re.addData("DATA_30", data30);
        re.addData("DATA_30", data30);
        double O_num = data10 + data15; // ���ڼ���������
        if (O_num == 0) {
            re.addData("DATA_31", ""); // ������<31>
            re.addData("DATA_32", ""); // ��ת��<32>
            re.addData("DATA_33", ""); // ������<33>
        } else {
            re.addData("DATA_31", df.format((double) data11 / O_num * 100));
            re.addData("DATA_32", df.format((double) data12 / O_num * 100));
            re.addData("DATA_33", df.format((double) data14 / O_num * 100));
        }
        // ������ת(��)
        if (data18 > 0 && data09 > 0) {// ������ת(��) = ���ڳ�Ժ����/ ͬ��ƽ�����Ų�����
            re.addData("DATA_34", df.format((double) data09 / ((double) data18 / MDays)));
        } else re.addData("DATA_34", "");
        re.addData("DATA_35", ""); // ����������<35> �㷨����
        double DATA_36 = 0;// ����ʹ����<36>
        DATA_36 = STAWorkLogTool.getInstance().countBED_USE_RATE(data16, MDays, data18 / MDays);
        re.addData("DATA_36", df.format(DATA_36));
        if (data09 == 0) re.addData("DATA_37", "0");// ����ƽ��סԺ��<37>
        else re.addData("DATA_37", df.format((double) data21 / (double) data09));
        re.addData("DATA_38", ""); // ��Ϸ�����<38>
        if (data26 == 0) re.addData("DATA_39", "0");// �޾��пڻ�ŧ��<39>
        else re.addData("DATA_39", df.format((double) data27 / (double) data26 * 100));
        if (data28 == 0) re.addData("DATA_40", "100");// Σ�ز������ȳɹ�%<40>
        else re.addData("DATA_40", df.format((double) data29 / (double) data28 * 100));
        if (data16 == 0) re.addData("DATA_41", "100");// ������%<41> = ������/ʵ�в�����
        else re.addData("DATA_41", df.format((double) data30 / (double) data16 * 100));
        if (data10 == 0 && data15 == 0) re.addData("DATA_41_1", "");// ������Ϸ�����%<41_1>
        else if (data10 + data15 == 0) re.addData("DATA_41_1", "100");
        else re.addData("DATA_41_1",
                            df.format((double) data23 / (double) (data10 + data15) * 100));
        if (data10 == 0 && data15 == 0) re.addData("DATA_41_2", ""); // ��ǰ������Ϸ�����%<41_2>
        else if (data10 + data15 == 0) re.addData("DATA_41_2", "0");
        else re.addData("DATA_41_2",
                            df.format((double) data25 / (double) (data10 + data15) * 100));
        re.setCount(re.getCount("DATA_01"));
        return re;
    }
    
    /**
     * ���ɴ�ӡ����
     * @param reData Ԥ������
     * @param DATE_S ��ʼ����
     * @param DATE_E ��������
     * @return
     */
    public TParm getPrintData(TParm reData, String DATE_S, String DATE_E) {
        DecimalFormat df = new DecimalFormat("0.00"); // �趨Double���͸�ʽ
        // ��ȡ����1��2��3������ ��Ϊ�������߲�����
        TParm DeptList = STAWorkLogTool.getInstance().selectDeptList();
        // ��ȡ����STA_DAILY_02�����ݣ����ļ�����Ϊ���������ݣ�
        Map deptIPD = STADeptListTool.getInstance().getIPDDeptMap(Operator.getRegion());
        // System.out.println("-=--------deptIPD-----------"+deptIPD);
        TParm printData = new TParm();// ��ӡ����
        for (int i = 0; i < DeptList.getCount(); i++) {
            String d_LEVEL = DeptList.getValue("DEPT_LEVEL", i);// ���ŵȼ�
            String d_CODE = DeptList.getValue("DEPT_CODE", i);// �м䲿��CODE
            String d_DESC = DeptList.getValue("DEPT_DESC", i);// �м䲿������
            int subIndex = 0;// ��¼���ݿ��Ҽ���Ҫ��ȡCODE�ĳ���
            if (d_LEVEL.equals("1")) { // �����һ������ code����Ϊ1
                subIndex = 1;
            } else if (d_LEVEL.equals("2")) { // ����Ƕ������� code����Ϊ3
                subIndex = 3;
                d_DESC = " " + d_DESC;// ����ǰ���ո�
            } else if (d_LEVEL.equals("3")) { // ������������� code����Ϊ5
                subIndex = 5;
                d_DESC = "  " + d_DESC;// ����ǰ���ո�
            }
            /*
             * ������� �����ۼ��Ӳ��ŵ���ֵ ��ʼֵΪ-1����������ۼӵ�����Ϊnull��ô����ʼ��Ϊ-1��
             * �Ǹ�����ñ��������ֶε�ʱ��Ͳ���null
             */
            int DATA_01 = -1;
            int DATA_02 = -1;
            int DATA_03 = -1;
            int DATA_04 = -1;
            int DATA_05 = -1;
            int DATA_06 = -1;
            int DATA_07 = -1;
            int DATA_08 = -1;
            int DATA_08_1 = -1;
            int DATA_09 = -1;
            int DATA_10 = -1;
            int DATA_11 = -1;
            int DATA_12 = -1;
            int DATA_13 = -1;
            int DATA_14 = -1;
            int DATA_15 = -1;
            int DATA_15_1 = -1;
            int DATA_16 = -1;
            int DATA_17 = -1;
            int DATA_18 = -1;
            int DATA_19 = -1;
            int DATA_20 = -1;
            int DATA_21 = -1;
            int DATA_22 = -1;
            int DATA_23 = -1;
            int DATA_24 = -1;
            int DATA_25 = -1;
            int DATA_26 = -1;
            int DATA_27 = -1;
            double DATA_28 = -1;
            int DATA_29 = -1;
            int DATA_30 = -1;
            double DATA_31 = -1;
            double DATA_32 = -1;
            double DATA_33 = -1;
            double DATA_34 = -1;
            int DATA_35 = -1;
            double DATA_36 = -1;
            int DATA_37 = -1;
            double DATA_38 = -1;
            double DATA_39 = -1;
            double DATA_40 = -1;
            double DATA_41 = -1;
            double DATA_41_1 = -1;
            double DATA_41_2 = -1;
            String dept4 = "";// ��¼���������µ��ļ�����
            int deptCount = 0;// ��¼ÿ�������µ��Ӳ��ţ����ʷ����������Ҫȡƽ��ֵ
            int deptInpCount = 0;// ��¼ÿ�������µ�סԺ��������
            // ѭ���������� ȡ�����������Ĳ��ŵ����ݽ����ۼ�
            for (int j = 0; j < reData.getCount(); j++) {
                // �������id��ȡ��ָ�����Ⱥ� �������ѭ���еĲ���CODE��ô�������ѭ�����Ӳ��ţ��ͽ����ۼ�
                if (reData.getValue("DEPT_CODE", j).substring(0, subIndex).equals(d_CODE)) {
                    if (deptIPD.get(reData.getValue("DEPT_CODE", j)) != null) {
                        dept4 += deptIPD.get(reData.getValue("DEPT_CODE", j)).toString() + ",";
                    }
                    DATA_01 = checkNull_i(DATA_01, reData.getValue("DATA_01", j));
                    DATA_02 = checkNull_i(DATA_02, reData.getValue("DATA_02", j));
                    DATA_03 = checkNull_i(DATA_03, reData.getValue("DATA_03", j));
                    DATA_04 = checkNull_i(DATA_04, reData.getValue("DATA_04", j));
                    DATA_05 = checkNull_i(DATA_05, reData.getValue("DATA_05", j));
                    DATA_06 = checkNull_i(DATA_06, reData.getValue("DATA_06", j));
                    DATA_07 = checkNull_i(DATA_07, reData.getValue("DATA_07", j));
                    DATA_08 = checkNull_i(DATA_08, reData.getValue("DATA_08", j));
                    DATA_08_1 = checkNull_i(DATA_08_1, reData.getValue("DATA_08_1", j));
                    DATA_09 = checkNull_i(DATA_09, reData.getValue("DATA_09", j));
                    DATA_10 = checkNull_i(DATA_10, reData.getValue("DATA_10", j));
                    DATA_11 = checkNull_i(DATA_11, reData.getValue("DATA_11", j));
                    DATA_12 = checkNull_i(DATA_12, reData.getValue("DATA_12", j));
                    DATA_13 = checkNull_i(DATA_13, reData.getValue("DATA_13", j));
                    DATA_14 = checkNull_i(DATA_14, reData.getValue("DATA_14", j));
                    DATA_15 = checkNull_i(DATA_15, reData.getValue("DATA_15", j));
                    DATA_15_1 = checkNull_i(DATA_15_1, reData.getValue("DATA_15_1", j));
                    DATA_16 = checkNull_i(DATA_16, reData.getValue("DATA_16", j));
                    DATA_17 = checkNull_i(DATA_17, reData.getValue("DATA_17", j));
                    DATA_18 = checkNull_i(DATA_18, reData.getValue("DATA_18", j));
                    DATA_19 = checkNull_i(DATA_19, reData.getValue("DATA_19", j));
                    DATA_20 = checkNull_i(DATA_20, reData.getValue("DATA_20", j));
                    DATA_21 = checkNull_i(DATA_21, reData.getValue("DATA_21", j));
                    DATA_22 = checkNull_i(DATA_22, reData.getValue("DATA_22", j));
                    DATA_23 = checkNull_i(DATA_23, reData.getValue("DATA_23", j));
                    DATA_24 = checkNull_i(DATA_24, reData.getValue("DATA_24", j));
                    DATA_25 = checkNull_i(DATA_25, reData.getValue("DATA_25", j));
                    DATA_26 = checkNull_i(DATA_26, reData.getValue("DATA_26", j));
                    DATA_27 = checkNull_i(DATA_27, reData.getValue("DATA_27", j));
                    DATA_28 = checkNull_d(DATA_28, reData.getValue("DATA_28", j));
                    DATA_29 = checkNull_i(DATA_29, reData.getValue("DATA_29", j));
                    DATA_30 = checkNull_i(DATA_30, reData.getValue("DATA_30", j));
                    DATA_35 = checkNull_i(DATA_35, reData.getValue("DATA_35", j));
                    DATA_38 = checkNull_d(DATA_38, reData.getValue("DATA_38", j));
                    deptCount++;// �ۼƹ����ܵĶ��ٸ�4�����ŵ�����
                    if (STAWorkLogTool.getInstance().checkIPDDept(reData.getValue("DEPT_CODE", j))) {
                        deptInpCount++;
                    }
                }
            }
            if (DATA_10 + DATA_15 > 0) {
                DATA_31 = (double) DATA_11 / ((double) DATA_10 + (double) DATA_15) * 100; // ������
                DATA_32 = (double) DATA_12 / ((double) DATA_10 + (double) DATA_15) * 100; // ��ת��
                DATA_33 = (double) DATA_13 / ((double) DATA_10 + (double) DATA_15) * 100; // ������
            }
            if (DATA_19 > 0) {
                if ((DATA_10 + DATA_15) > 0) DATA_34 =
                        (double) (DATA_10 + DATA_15) / (double) DATA_19; // ������ת�� = ��Ժ����(1~5)/ƽ�����Ų�����
            }
            if ("month".equals(resultType)) {// �±�
                DATA_36 = STAWorkLogTool.getInstance().countBED_USE_RATE(DATA_20, MDays, DATA_19);// ����ʹ����
                                                                                                  // ��ͳ����Ч
            }
            if (DATA_09 > 0) DATA_37 = DATA_21 / DATA_09; // ����ƽ��סԺ����
            if (DATA_26 > 0) DATA_39 = (double) DATA_27 / (double) DATA_26 * 100; // �޾��пڻ�ŧ��
            if (DATA_28 > 0) DATA_40 = (double) DATA_29 / (double) DATA_28 * 100; // Σ�ز������ȳɹ�%
            if (DATA_16 > 0) DATA_41 = (double) DATA_30 / (double) DATA_16 * 100; // ������
            if ((DATA_10 + DATA_15) > 0) {
                DATA_41_1 = (double) DATA_23 / ((double) DATA_10 + (double) DATA_15) * 100; // ������Ϸ�����%
                DATA_41_2 = (double) DATA_25 / ((double) DATA_10 + (double) DATA_15) * 100;
            }
            // ������������������� ��ôת��������Ҫ������ȡ
            // ��Ϊͬһ���������µ��ļ�����֮���ת�Ʋ������������ҵ�������(�����Ժ����)
            if (d_LEVEL.equals("3")) {
                if (dept4.length() > 0) {
                    String[] d = dept4.substring(0, dept4.length() - 1).split(",");
                    DATA_08_1 = STAIn_03Tool.getInstance().getDept_TranInNum(d, DATE_S, DATE_E);
                    DATA_15_1 = STAIn_03Tool.getInstance().getDept_TranOutNum(d, DATE_S, DATE_E);
                }
            }
            printData.addData("DEPT_DESC", d_DESC);
            printData.addData("DATA_01", DATA_01 == -1 ? "" : DATA_01);
            printData.addData("DATA_02", DATA_02 == -1 ? "" : DATA_02);
            printData.addData("DATA_03", DATA_03 == -1 ? "" : DATA_03);
            printData.addData("DATA_04", DATA_04 == -1 ? "" : DATA_04);
            printData.addData("DATA_05", DATA_05 == -1 ? "" : DATA_05);
            printData.addData("DATA_06", DATA_06 == -1 ? "" : DATA_06);
            printData.addData("DATA_07", DATA_07 == -1 ? "" : DATA_07);
            printData.addData("DATA_08", DATA_08 == -1 ? "" : DATA_08);
            printData.addData("DATA_08_1", DATA_08_1 == -1 ? "" : DATA_08_1);
            printData.addData("DATA_09", DATA_09 == -1 ? "" : DATA_09);
            printData.addData("DATA_10", DATA_10 == -1 ? "" : DATA_10);
            printData.addData("DATA_11", DATA_11 == -1 ? "" : DATA_11);
            printData.addData("DATA_12", DATA_12 == -1 ? "" : DATA_12);
            printData.addData("DATA_13", DATA_13 == -1 ? "" : DATA_13);
            printData.addData("DATA_14", DATA_14 == -1 ? "" : DATA_14);
            printData.addData("DATA_15", DATA_15 == -1 ? "" : DATA_15);
            printData.addData("DATA_15_1", DATA_15_1 == -1 ? "" : DATA_15_1);
            printData.addData("DATA_16", DATA_16 == -1 ? "" : DATA_16);
            printData.addData("DATA_17", DATA_17 == -1 ? "" : DATA_17);
            printData.addData("DATA_18", DATA_18 == -1 ? "" : DATA_18);
            printData.addData("DATA_19", DATA_19 == -1 ? "" : DATA_19);
            printData.addData("DATA_20", DATA_20 == -1 ? "" : DATA_20);
            printData.addData("DATA_21", DATA_21 == -1 ? "" : DATA_21);
            printData.addData("DATA_22", DATA_22 == -1 ? "" : DATA_22);
            printData.addData("DATA_23", DATA_23 == -1 ? "" : DATA_23);
            printData.addData("DATA_24", DATA_24 == -1 ? "" : DATA_24);
            printData.addData("DATA_25", DATA_25 == -1 ? "" : DATA_25);
            printData.addData("DATA_26", DATA_26 == -1 ? "" : DATA_26);
            printData.addData("DATA_27", DATA_27 == -1 ? "" : DATA_27);
            printData.addData("DATA_28", DATA_28 == -1 ? "" : DATA_28);
            printData.addData("DATA_29", DATA_29 == -1 ? "" : DATA_29);
            printData.addData("DATA_30", DATA_30 == -1 ? "" : DATA_30);
            printData.addData("DATA_31", DATA_31 == -1 ? "" : df.format(DATA_31));
            printData.addData("DATA_32", DATA_32 == -1 ? "" : df.format(DATA_32));
            printData.addData("DATA_33", DATA_33 == -1 ? "" : df.format(DATA_33));
            printData.addData("DATA_34", DATA_34 == -1 ? "" : df.format(DATA_34));
            printData.addData("DATA_35", DATA_35 == -1 ? "" : DATA_35);
            printData.addData("DATA_36", DATA_36 == -1 ? "" : df.format(DATA_36));
            printData.addData("DATA_37", DATA_37 == -1 ? "" : DATA_37);
            printData.addData("DATA_38", DATA_38 == -1 ? "" : df.format(DATA_38));
            printData.addData("DATA_39", DATA_39 == -1 ? "" : df.format(DATA_39));
            printData.addData("DATA_40", DATA_40 == -1 ? "" : df.format(DATA_40));
            printData.addData("DATA_41", DATA_41 == -1 ? "" : df.format(DATA_41));
            printData.addData("DATA_41_1", DATA_41_1 == -1 ? "" : df.format(DATA_41_1));
            printData.addData("DATA_41_2", DATA_41_2 == -1 ? "" : df.format(DATA_41_2));
        }
        // System.out.println(""+printData);
        printData.setCount(DeptList.getCount());
        printData.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
        printData.addData("SYSTEM", "COLUMNS", "DATA_01");
        printData.addData("SYSTEM", "COLUMNS", "DATA_02");
        printData.addData("SYSTEM", "COLUMNS", "DATA_03");
        printData.addData("SYSTEM", "COLUMNS", "DATA_04");
        printData.addData("SYSTEM", "COLUMNS", "DATA_05");
        printData.addData("SYSTEM", "COLUMNS", "DATA_06");
        printData.addData("SYSTEM", "COLUMNS", "DATA_07");
        printData.addData("SYSTEM", "COLUMNS", "DATA_08");
        printData.addData("SYSTEM", "COLUMNS", "DATA_08_1");
        printData.addData("SYSTEM", "COLUMNS", "DATA_09");
        printData.addData("SYSTEM", "COLUMNS", "DATA_10");
        printData.addData("SYSTEM", "COLUMNS", "DATA_11");
        printData.addData("SYSTEM", "COLUMNS", "DATA_12");
        printData.addData("SYSTEM", "COLUMNS", "DATA_13");
        printData.addData("SYSTEM", "COLUMNS", "DATA_14");
        printData.addData("SYSTEM", "COLUMNS", "DATA_15");
        printData.addData("SYSTEM", "COLUMNS", "DATA_15_1");
        printData.addData("SYSTEM", "COLUMNS", "DATA_16");
        printData.addData("SYSTEM", "COLUMNS", "DATA_17");
        printData.addData("SYSTEM", "COLUMNS", "DATA_18");
        printData.addData("SYSTEM", "COLUMNS", "DATA_19");
        printData.addData("SYSTEM", "COLUMNS", "DATA_20");
        printData.addData("SYSTEM", "COLUMNS", "DATA_21");
        printData.addData("SYSTEM", "COLUMNS", "DATA_22");
        printData.addData("SYSTEM", "COLUMNS", "DATA_23");
        printData.addData("SYSTEM", "COLUMNS", "DATA_24");
        printData.addData("SYSTEM", "COLUMNS", "DATA_25");
        printData.addData("SYSTEM", "COLUMNS", "DATA_26");
        printData.addData("SYSTEM", "COLUMNS", "DATA_27");
        printData.addData("SYSTEM", "COLUMNS", "DATA_28");
        printData.addData("SYSTEM", "COLUMNS", "DATA_29");
        printData.addData("SYSTEM", "COLUMNS", "DATA_30");
        printData.addData("SYSTEM", "COLUMNS", "DATA_31");
        printData.addData("SYSTEM", "COLUMNS", "DATA_32");
        printData.addData("SYSTEM", "COLUMNS", "DATA_33");
        printData.addData("SYSTEM", "COLUMNS", "DATA_34");
        printData.addData("SYSTEM", "COLUMNS", "DATA_35");
        printData.addData("SYSTEM", "COLUMNS", "DATA_36");
        printData.addData("SYSTEM", "COLUMNS", "DATA_37");
        printData.addData("SYSTEM", "COLUMNS", "DATA_38");
        printData.addData("SYSTEM", "COLUMNS", "DATA_39");
        printData.addData("SYSTEM", "COLUMNS", "DATA_40");
        printData.addData("SYSTEM", "COLUMNS", "DATA_41");
        printData.addData("SYSTEM", "COLUMNS", "DATA_41_1");
        printData.addData("SYSTEM", "COLUMNS", "DATA_41_2");
        // System.out.println("----------printData-----------"+printData);
        return printData;
    }
}
