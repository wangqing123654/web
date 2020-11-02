package com.javahis.ui.hrm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdo.hl7.Hl7Communications;
import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMContractD;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMPatAdm;
import jdo.hrm.HRMPatInfo;
import jdo.sys.PatTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p> Title: ����ɨ��ǹ���� </p>
 * 
 * <p> Description: ����ɨ��ǹ����  </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company:BlueCore </p>
 * 
 * @author wanglong
 * @version 1.0
 */
public class HRMScanGunReportControl extends TControl {

    private TTable table;// ����TABLE
    private HRMPatInfo pat;// ��������
    private HRMPatAdm adm;// ��������
    private HRMOrder order;// ҽ������
    private HRMContractD contractD;// ��ͬ����

    /**
     * ��ʼ���¼�
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        onClear();
    }

    /**
     * ����¼�
     */
    public void onClear() {
        initData();
        table.setParmValue(new TParm());
        this.setValue("MR_NO", "");
        ((TTextField) this.getComponent("MR_NO")).requestFocus();
    }

    /**
     * ��ʼ������
     */
    private void initData() {
        pat = new HRMPatInfo();
        adm = new HRMPatAdm();
        adm.onQuery();
        order = new HRMOrder();
        order.onQuery("", "");
        contractD = new HRMContractD();
        contractD.onQuery();
    }

    /**
     * �����Ų�ѯ
     */
    public void onQueryByMr() {
        String mrNo = this.getValueString("MR_NO").trim();
        if (mrNo.equals("")) {
            onClear();
            return;
        }
        mrNo = PatTool.getInstance().checkMrno(mrNo);// �����Ų��볤��
        this.setValue("MR_NO", mrNo);
        TParm result = HRMCompanyTool.getInstance().getContractDByMr(mrNo);
        if (result.getErrCode() != 0) {
            this.messageBox_("��ѯʧ��");
            onClear();
            return;
        }
        if (result.getCount() < 1) {
            return;
        } else if (result.getCount() == 1) {// ��������
            table.setParmValue(result);
            table.setSelectedRow(0);
            result = result.getRow(0);
            if (!result.getValue("COVER_FLG").equals("Y")) {
                if (this.messageBox("��ʾ", "�Ƿ���б���", 2) == 0) {
                    onSave();
                }
            } else {
                this.messageBox("�ѱ���");
                onClear();
                return;
            }
        } else {// ��������(����ѡ�񴰿�)
            for (int i = result.getCount(); i >= 0; i--) {
                if (result.getValue("COVER_FLG", i).equals("Y")) {
                    result.removeRow(i);
                }
            }
            if (result.getCount("MR_NO") < 1) {
                this.messageBox("�ѱ���");
                onClear();
                return;
            }
            result.setCount(result.getCount("MR_NO"));
            if (result.getCount("MR_NO") == 1) {
                table.setParmValue(result);
                table.setSelectedRow(0);
                result = result.getRow(0);
                if (!result.getValue("COVER_FLG").equals("Y")) {
                    if (this.messageBox("��ʾ", "�Ƿ���б���", 2) == 0) {
                        onSave();
                    }
                } else {
                    this.messageBox("�ѱ���");
                    onClear();
                    return;
                }
            } else {
                Object obj = this.openDialog("%ROOT%\\config\\hrm\\HRMPatRecord.x", result);
                if (obj != null) {
                    TParm rowParm = (TParm) obj;
                    table.setParmValue(rowParm);
                    table.setSelectedRow(0);
                    rowParm = rowParm.getRow(0);
                    if (!rowParm.getValue("COVER_FLG").equals("Y")) {
                        if (this.messageBox("��ʾ", "�Ƿ���б���", 2) == 0) {
                            onSave();
                        }
                    } else {
                        this.messageBox("�ѱ���");
                        onClear();
                        return;
                    }
                } else {
                    onClear();
                }
            }
        }
    }

    /**
     * �����¼�
     */
    public void onSave() {
        table.acceptText();
        TParm result = table.getParmValue();
        if (result == null) {
            this.messageBox("��ñ�����ݳ���");
            onClear();
            return;
        }
        int count = result.getCount();
        if (count < 1) {
            this.messageBox_("�ޱ�������");
            onClear();
            return;
        }
        TParm parmRow = result.getRow(0);
        String mrNo = parmRow.getValue("MR_NO");
        if (StringUtil.isNullString(mrNo)) {
            this.messageBox_("������Ϊ��");
            onClear();
            return;
        }
        if (StringUtil.isNullString(parmRow.getValue("DEPT_CODE"))) {
            this.messageBox("��������Ϊ��");
            onClear();
            return;
        }
        pat.onQuery(mrNo);
        TParm patParm = pat.getRowParm(0);
        List<TParm> listHl7 = new ArrayList<TParm>();
        // �ж�HRM_ADM���Ƿ�������ݣ��������ݵĻ��� ֻ����һ�� HRM_ADM D����ѱ�����־
        Timestamp now = adm.getDBTime();
        String companyCode = parmRow.getValue("COMPANY_CODE");// �������
        String contractCode = parmRow.getValue("CONTRACT_CODE");// ��ͬ����
        String caseNo = "";
        if (parmRow.getValue("CASE_NO").length() == 0) {
            caseNo = HRMPatAdm.getLatestCaseNoBy(mrNo, contractCode);
        } else {
            caseNo = parmRow.getValue("CASE_NO");
        }
        // �ж���δ�������򱣴����ݣ������ó�δ����
        String tel = parmRow.getValue("TEL");
        if (!StringUtil.isNullString(caseNo)) {// ��չ��ҽ��(���±���״̬)
            TParm resultParm = new TParm();
            resultParm = adm.updateCoverFlg(caseNo, now, tel);// add by wanglong 20130408
            if (resultParm.getErrCode() < 0) {
                this.messageBox("���±���״̬ʧ��");
                onClear();
                return;
            }
            resultParm = contractD.updateCoverFlg(companyCode, contractCode, mrNo, now, tel);
            if (resultParm.getErrCode() < 0) {
                this.messageBox("���±���״̬ʧ��");
                onClear();
                return;
            }
            this.getHl7List(listHl7, caseNo);
            // ȷʵû��caseno˵�����ֳ�����,��¼�յ�CASE_NO
        } else {
            patParm.setData("PAT_NAME", parmRow.getData("PAT_NAME"));
            patParm.setData("COMPANY_PAY_FLG", parmRow.getData("COMPANY_PAY_FLG"));
            patParm.setData("COMPANY_CODE", companyCode);
            patParm.setData("CONTRACT_CODE", contractCode);
            String packagecode = parmRow.getValue("PACKAGE_CODE");
            patParm.setData("PACKAGE_CODE", packagecode);
            patParm.setData("REPORTLIST", parmRow.getData("REPORTLIST"));
            patParm.setData("INTRO_USER", parmRow.getData("INTRO_USER"));
            patParm.setData("DISCNT", parmRow.getData("DISCNT"));
            patParm.setData("TEL", parmRow.getValue("TEL"));
            patParm.setData("MARRIAGE_CODE", parmRow.getValue("MARRIAGE_CODE"));
            patParm.setData("PAT_DEPT", parmRow.getValue("PAT_DEPT"));
            if (!adm.onNewAdm(patParm, now)) {
                this.messageBox("����HRM_PATADM����ʧ��");
                onClear();
                return;
            }
            String caseNo1 = adm.getItemString(adm.rowCount() - 1, "CASE_NO");
            if (StringUtil.isNullString(caseNo1)) {
                this.messageBox("ȡ������ʧ��");
                onClear();
                return;
            }
            caseNo=caseNo1;
            order.filt(caseNo);
            order.initOrderByTParm(packagecode, caseNo, mrNo, contractCode, patParm);
            if (!StringUtil.isNullString(tel)) {
                contractD.updateTel(companyCode, contractCode, mrNo, tel);
            }
            contractD.updateCoverFlg(companyCode, contractCode, mrNo, tel);
            String[] sql = adm.getUpdateSQL();
            sql = StringTool.copyArray(sql, contractD.getUpdateSQL());
            sql = StringTool.copyArray(sql, order.getUpdateSQL());
            sql = StringTool.copyArray(sql, order.getMedApply().getUpdateSQL());
            // ���������̨���淽���Ĳ���������֤��̨���淽���ķ���ֵ�Ƿ�ɹ�
            TParm inParm = new TParm();
            Map<String,String[]> inMap = new HashMap<String,String[]> ();
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm saveResult =
                    TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave",
                                                 inParm);
            if (saveResult.getErrCode() != 0) {
                this.messageBox("����ʧ��");
                onClear();
                return;
            } else {
                // this.messageBox_("�����ɹ�");
                this.getHl7List(listHl7, caseNo);
            }
        }
        // ����HL7��Ϣ
        // sendHl7();
        TParm hl7Parm = Hl7Communications.getInstance().Hl7Message(listHl7);
        if (hl7Parm.getErrCode() < 0) {
            this.messageBox("����HL7��Ϣʧ�� " + hl7Parm.getErrText());
            onClear();
            return;
        }
        this.messageBox("�����ɹ�");
        onClear();
    }

    /**
     * �õ�HL7����
     * 
     * @param listHl7
     *            List
     * @param caseNo
     *            String
     * @return List
     */
    public List<TParm> getHl7List(List<TParm> listHl7, String caseNo) {
        String sql =
                "SELECT CAT1_TYPE,PAT_NAME,CASE_NO,APPLICATION_NO AS LAB_NO,ORDER_NO,SEQ_NO FROM MED_APPLY WHERE ADM_TYPE='H' AND CASE_NO='"
                        + caseNo + "' AND SEND_FLG < 2";
        // System.out.println("SQLMED=="+sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        int rowCount = parm.getCount();
        String preLabNo = "";
        for (int i = 0; i < rowCount; i++) {
            TParm temp = parm.getRow(i);
            String labNo = temp.getValue("LAB_NO");
//            if (!preLabNo.equals(labNo)) {
                temp.setData("ADM_TYPE", "H");
                temp.setData("FLG", "0");
                // System.out.println("PAT_NAME----:"+temp.getValue("PAT_NAME")+":LAB_NO----:"+temp.getValue("LAB_NO"));
                listHl7.add(temp);
                preLabNo = labNo;
//            }
        }
        // System.out.println("listHl7.size----:"+listHl7.size());
        return listHl7;
    }
}
