package com.javahis.ui.hrm;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import jdo.hrm.HRMContractD;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMPatAdm;
import com.javahis.util.StringUtil;
import com.javahis.system.textFormat.TextFormatHRMPatPackage;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;


/**
 * <p> Title: ���츴������ҽ������ </p>
 * 
 * <p> Description: ���츴������ҽ������  </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 * @author wanglong 2013.05.07
 * @version 1.0
 */
public class HRMCopyOrderControl extends TControl {
    

    TTable table;
    TTextFormat  contract;
    TextFormatHRMPatPackage patPackage;
    private TParm inParm;
    private String companyCode, contractCode;// ������롢��ͬ����
    private HRMContractD contractD;// ��ͬ����
    private HRMPatAdm adm;// ��������
    private HRMOrder order;
    
    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
        patPackage = (TextFormatHRMPatPackage) this.getComponent("PAT_PACKAGE");
        contractD = new HRMContractD();
        contractD.onQuery("", "", "");
        adm = new HRMPatAdm();
        adm.onQuery();
        order = new HRMOrder();
        order.onQuery();
        if (this.getParameter() != null && !StringUtil.isNullString(this.getParameter() + "")
                && (this.getParameter() instanceof TParm)) {
            inParm = (TParm) this.getParameter();
            int patCount = inParm.getCount("MR_NO");
            if (patCount >= 1000) {
                this.messageBox("ͬʱ��������Ա����Ҫ����1000��");
                this.closeWindow();
            }
            this.setValue("COMPANY_CODE", inParm.getValue("COMPANY_CODE", 0));
            contract.setValue(inParm.getValue("CONTRACT_CODE", 0));
            onContractChoose();
    
        }
    }
    
	/**
	 * ����ҽ��
	 */
    public void onCopy() {
        String copyMrNo = this.getValueString("PAT_PACKAGE");
        int patCount = inParm.getCount("MR_NO");
        String mrList = "";
        String patSql = "SELECT * FROM SYS_PATINFO WHERE MR_NO IN (#)";// add by wanglong 20130304
        for (int i = 0; i < patCount; i++) {
            mrList += "'" + inParm.getValue("MR_NO", i) + "',";
        }
        mrList = mrList.substring(0, mrList.length() - 1);
        patSql = patSql.replaceFirst("#", mrList);
        TParm patList = new TParm(TJDODBTool.getInstance().select(patSql));
        if (patList.getErrCode() != 0 || patList.getCount() < 1) {
            this.messageBox("��ѯ������Ϣ����");
            return;
        }
        boolean flag = true;
        for (int i = 0; i < patCount; i++) {
            TParm parmRow = inParm.getRow(i);
            String mrNo = parmRow.getValue("MR_NO");
            if (StringUtil.isNullString(mrNo)) {
                this.messageBox_("������Ϊ��");
                continue;
            }
            String caseNo = "";
            String contractCode = parmRow.getValue("CONTRACT_CODE");
            if (parmRow.getValue("CASE_NO").length() == 0) {
                caseNo = adm.getLatestCaseNoBy(mrNo, contractCode);
            } else {
                caseNo = parmRow.getValue("CASE_NO");
            }
            // �ж���δ�������򱣴����ݣ������ó�δ����
            // 1.����HRM_PATADM
            if (StringUtil.isNullString(caseNo)) {
                TParm patParm = new TParm();
                for (int j = 0; j < patList.getCount(); j++) {// add by wanglong 20130304
                    if (patList.getValue("MR_NO", j).equals(mrNo)) {
                        patParm = patList.getRow(j);
                    }
                }
                patParm.setData("PAT_NAME", inParm.getData("PAT_NAME", i));
                patParm.setData("COMPANY_PAY_FLG", inParm.getData("COMPANY_PAY_FLG", i));
                patParm.setData("COMPANY_CODE", inParm.getData("COMPANY_CODE", i));
                patParm.setData("CONTRACT_CODE", inParm.getData("CONTRACT_CODE", i));
                String packCode = inParm.getValue("PACKAGE_CODE", i);
                patParm.setData("PACKAGE_CODE", packCode);
                patParm.setData("REPORTLIST", inParm.getData("REPORTLIST", i));
                patParm.setData("INTRO_USER", inParm.getData("INTRO_USER", i));
                patParm.setData("DISCNT", inParm.getData("DISCNT", i));
                patParm.setData("TEL", inParm.getData("TEL", i));
                patParm.setData("MARRIAGE_CODE", inParm.getData("MARRIAGE_CODE", i));// add by wanglong 20130117
                patParm.setData("PAT_DEPT", inParm.getData("PAT_DEPT", i));// add by wanglong 20130225
                // 1.Ԥ����
                Timestamp now = order.getDBTime();
                if (!adm.onPreAdm(patParm, now)) {
                    this.messageBox_("���:" + inParm.getData("SEQ_NO", i) + "  ������"
                            + inParm.getData("PAT_NAME", i) + ",Ԥ��������HRM_PATADM����ʧ��");
                    adm = new HRMPatAdm();
                    adm.onQuery();
                    order = new HRMOrder();
                    order.onQuery();
                    flag = false;
                    continue;
                }
                // 2.HRM_ORDER
                String admCaseNo1 = adm.getItemString(adm.rowCount() - 1, "CASE_NO");
                // caseNo = admCaseNo1;
                if (StringUtil.isNullString(admCaseNo1)) {
                    this.messageBox_("���:" + inParm.getData("SEQ_NO", i) + "  ������"
                            + inParm.getData("PAT_NAME", i) + ",ȡ������ʧ��");
                    adm = new HRMPatAdm();
                    adm.onQuery();
                    order = new HRMOrder();
                    order.onQuery();
                    flag = false;
                    continue;
                }
                patParm.setData("CASE_NO", admCaseNo1);
                order.filt(admCaseNo1);
                order.initOrderByCaseNo(contractCode, copyMrNo, patParm);
                String[] sql = adm.getUpdateSQL();
                sql = StringTool.copyArray(sql, order.getUpdateSQL());
                sql = StringTool.copyArray(sql, order.getMedApply().getUpdateSQL());
                // ���������̨���淽���Ĳ���������֤��̨���淽���ķ���ֵ�Ƿ�ɹ�
                TParm inParm = new TParm();
                Map inMap = new HashMap();
                inMap.put("SQL", sql);
                inParm.setData("IN_MAP", inMap);
                TParm saveResult =
                        TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave",
                                                     inParm);
                if (saveResult.getErrCode() != 0) {
                    this.messageBox("���:" + inParm.getData("SEQ_NO", i) + "  ����:"
                            + inParm.getData("PAT_NAME", i) + "\nҽ��չ��ʧ��");
                }
            } else {
                this.messageBox("���:" + inParm.getData("SEQ_NO", i) + "  ����:"
                        + inParm.getData("PAT_NAME", i) + "\nҽ����չ������������");
            }
            adm = new HRMPatAdm();
            adm.onQuery();
            order = new HRMOrder();
            order.onQuery("", "");
        }
        if (flag == true) {
            this.messageBox("ҽ��չ���ɹ�");
        }
        this.closeWindow();
    }

	/**
	 * ȡ��
	 */
	public void onCancel() {
		this.closeWindow();
	}
	
	/**
     * ��������ѡ�¼�
     */
    public void onCompanyChoose() {
        // ����ѡ���������룬���첢��ʼ��������ĺ�ͬ��ϢTTextFormat
        companyCode = this.getValueString("COMPANY_CODE");
        TParm contractParm = contractD.onQueryByCompany(companyCode);
        if (contractParm == null || contractParm.getCount() <= 0
                || contractParm.getErrCode() != 0) {
            this.messageBox_("û������");
            return;
        }
        // System.out.println("contractParm="+contractParm);
        contract.setPopupMenuData(contractParm);
        contract.setComboSelectRow();
        contract.popupMenuShowData();
        contractCode = contractParm.getValue("ID", 0);
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox_("��ѯ��ͬʧ��");
            return;
        }
        contract.setValue(contractCode);
        onContractChoose();
    }

    /**
     * ��ͬ�����ѡ�¼�
     */
    public void onContractChoose() {
        contractCode = this.getValueString("CONTRACT_CODE");
        if (StringUtil.isNullString(contractCode)) {
            return;
        }
        patPackage.setContractCode(contractCode);
        patPackage.setFilterFlg("Y");
        patPackage.onQuery();
    }
    
    /**
     * �鿴ҽ����ϸ
     */
    public void onShowDetail() {
        String mrNo = this.getValueString("PAT_PACKAGE");
        String sql =
                "SELECT DISTINCT A.MR_NO,A.PAT_NAME,A.ORDERSET_CODE ORDER_CODE,B.ORDER_DESC||' '||B.SPECIFICATION ORDER_DESC,A.DISPENSE_QTY,"
                        + "A.OWN_PRICE/A.DISPENSE_QTY OWN_PRICE,A.OWN_PRICE OWN_AMT,A.DISCOUNT_RATE,A.OWN_AMT AR_AMT "
                        + " FROM (SELECT MR_NO,CASE_NO,PAT_NAME,ORDERSET_CODE,DISCOUNT_RATE,SUM(DISPENSE_QTY) "
                        + "              DISPENSE_QTY,SUM(OWN_PRICE) OWN_PRICE,SUM(OWN_AMT) OWN_AMT "
                        + "        FROM (SELECT A.MR_NO,B.CASE_NO,A.PAT_NAME,B.ORDERSET_CODE,"
                        + "                     SUM(CASE WHEN B.ORDER_CODE = B.ORDERSET_CODE THEN B.DISCOUNT_RATE ELSE 0 END) DISCOUNT_RATE,"
                        + "                     SUM(CASE WHEN B.ORDER_CODE = B.ORDERSET_CODE THEN B.DISPENSE_QTY ELSE 0 END) DISPENSE_QTY,"
                        + "                     SUM(B.OWN_AMT) OWN_PRICE,SUM(B.AR_AMT) OWN_AMT,B.ORDERSET_GROUP_NO "
                        + "               FROM HRM_CONTRACTD A, HRM_ORDER B "
                        + "              WHERE A.MR_NO = B.MR_NO "
                        + "                AND A.CONTRACT_CODE = B.CONTRACT_CODE "
                        + "                AND B.CAT1_TYPE <> 'PHA' "
                        + "                AND B.CONTRACT_CODE = '#' "
                        + "                AND B.MR_NO = '#' "
                        + "           GROUP BY A.MR_NO,B.CASE_NO,A.PAT_NAME,B.ORDERSET_CODE,B.ORDERSET_GROUP_NO) "
                        + "     GROUP BY MR_NO,CASE_NO,PAT_NAME,ORDERSET_CODE,DISCOUNT_RATE,OWN_PRICE) A, HRM_ORDER B "
                        + " WHERE A.ORDERSET_CODE = B.ORDER_CODE" + " AND A.CASE_NO = B.CASE_NO "
                        + "ORDER BY A.PAT_NAME, ORDER_DESC";
        sql = sql.replaceFirst("#", contractCode);
        sql = sql.replaceFirst("#", mrNo);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            this.messageBox("��ѯҽ����ϸʧ�� " + result.getErrText());
            return;
        }
        if (result.getCount() < 1) {
            this.setValue("OWN_AMT", "");
            table.setParmValue(new TParm());
        } else {
            double ownAmt = 0;
            for (int i = 0; i < result.getCount(); i++) {
                ownAmt += result.getDouble("OWN_AMT", i);
            }
            ownAmt = StringTool.round(ownAmt, 2);
            this.setValue("OWN_AMT", ownAmt + "");
            table.setParmValue(result);
        }
    }
}
