package com.javahis.ui.opb;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.IReportTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import jdo.bil.BILSQL;
import jdo.opd.OrderTool;
import jdo.opb.OPBReceiptTool;
import jdo.opb.OPBTool;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import jdo.bil.BILInvoiceTool;
import com.dongyang.ui.event.TTableEvent;
import jdo.util.Manager;
import com.javahis.util.StringUtil;
import jdo.reg.Reg;
import java.awt.Color;

/**
 * <p>Title: �����շ�������Ʊ</p>
 *
 * <p>Description: �����շ�������Ʊ</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2010.09.14
 * @version 1.0
 */
public class OPBPatchPrintControl extends TControl {
    TTable table;
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        setValue("ADM_TYPE", "");
        table = (TTable)this.getComponent("TABLE");
        //�˵�tableר�õļ���
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                               "onTableComponent");
        initPage();
    }

    /**
     * �˵�table�����¼�
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        TTable printTable = (TTable) obj;
        printTable.acceptText();
        return true;
    }


    /**
     * ��ʼ����������
     */
    public void initPage() {
        //================pangben modify 20110407 start ��������
        //this.setValue("REGION_CODE", "HIS");
        setValue("REGION_CODE", Operator.getRegion());
        //================pangben modify 20110407 stop
        TParm selInvoice = new TParm();
        selInvoice.setData("STATUS", "0");
        selInvoice.setData("RECP_TYPE", "OPB");
        selInvoice.setData("CASHIER_CODE", Operator.getID());
        selInvoice.setData("TERM_IP", Operator.getIP());
//        System.out.println("Ʊ����������" + selInvoice);
        TParm invoice = BILInvoiceTool.getInstance().selectNowReceipt(
                selInvoice);
        String invNo = invoice.getValue("UPDATE_NO", 0);
        if (invNo == null || invNo.length() == 0) {
            this.messageBox("���ȿ���");
            return;
        }
//        System.out.println("ʱ��" + getApTime());
        this.setValue("UPDATE_NO", invNo);
        //==liling modify 20140424 start ��Ӳ�ѯ���� ʱ���==
//        Timestamp today = SystemTool.getInstance().getDate();
//    	String startDate = today.toString();
//      startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
//        setValue("START_DATE", startDate);
//    	setValue("END_DATE", today);//����ʱ��
        String now = StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd");
        setValue("START_DATE", StringTool.getTimestamp(now + "000000","yyyyMMddHHmmss"));// ��ʼʱ��
        setValue("END_DATE", StringTool.getTimestamp(now + "235959","yyyyMMddHHmmss"));// ����ʱ��
    
    	   //==liling   modify  20140424  end==
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = new TParm();
        if (getValue("ADM_TYPE") != null ||
            getValueString("ADM_TYPE").length() != 0) {
            parm.setData("ADM_TYPE", getValueString("ADM_TYPE"));
        }
        if (getValue("CLINIC_AREA") != null ||
            getValueString("CLINIC_AREA").length() != 0) {
            parm.setData("CLINIC_AREA", getValueString("CLINIC_AREA"));
        }
        if (getValue("SESSION_CODE") != null ||
            getValueString("SESSION_CODE").length() != 0) {
            parm.setData("SESSION_CODE", getValueString("SESSION_CODE"));
        }
        if (getValue("REALDEPT_CODE") != null ||
            getValueString("REALDEPT_CODE").length() != 0) {
            parm.setData("REALDEPT_CODE", getValueString("REALDEPT_CODE"));
        }
        if (getValue("REALDR_CODE") != null ||
            getValueString("REALDR_CODE").length() != 0) {
            parm.setData("REALDR_CODE", getValueString("REALDR_CODE"));
        }
        //=======================pangben modify 20110407 start  ��Ӳ�ѯ����
        //����
        if (getValueString("REGION_CODE").length() > 0)
            parm.setData("REGION_CODE", getValueString("REGION_CODE"));
        //=======================pangben modify 20110407 stop
      //=======================liling modify 20140424 start  ��Ӳ�ѯ����
        //ʱ���
        if (getValueString("START_DATE").length() > 0)
            parm.setData("START_DATE", getValueString("START_DATE"));
        if (getValueString("END_DATE").length() > 0)
            parm.setData("END_DATE", getValueString("END_DATE"));
        //========================liling modify 20140424 end
        String sql = this.getOPBPatchPrintSql(parm);
        System.out.println("������Ʊ��ѯ" + sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            System.out.println("�ս����ݲ�ѯ����  " + result.getErrText());
            return;
        }
        table.setParmValue(result);
        TParm tableParm = table.getParmValue();
        String confirm_no = "";
        /**
         * ҽ�����˱�����ɫ
         */
        Color nhiColor = new Color(128, 0, 128);
        /**
         * ��ͨ������ɫ
         */
        Color normalColorBJ = new Color(255, 255, 255);
        for (int i = 0; i < tableParm.getCount(); i++) {
//            ctz1Code = tableParm.getValue("CTZ1_CODE", i);
//            String ctzSql =
//                    " SELECT NHI_CTZ_FLG " + "   FROM SYS_CTZ " +
//                    "  WHERE CTZ_CODE = '" + ctz1Code + "' ";
//            TParm ctzParm = new TParm(TJDODBTool.getInstance().select(ctzSql));
        	confirm_no = tableParm.getValue("CONFIRM_NO",i);
            if (confirm_no.length()>0) {
                table.setRowColor(i, nhiColor);
            } else {
                table.setRowColor(i, normalColorBJ);
            }
        }

    }

    /**
     * ȫѡ�¼�
     */
    public void onSelectAll() {
        String select = getValueString("SELECT");
        TParm parm = table.getParmValue();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            parm.setData("FLG", i, select);
        }
        table.setParmValue(parm);
    }

    /**
     * ��ӡ
     */
    public void onPrint() {
        //�õ�table�ϵ�����
        table.acceptText();
        TParm tableParm = table.getParmValue();
//        System.out.println("table����====" + tableParm);
        //��˴�ӡ����
        if (!checkPrintData(tableParm))
            return;
        int rowCount = tableParm.getCount("CASE_NO");
//        System.out.println("table����" + rowCount);
        Pat pat = null;
        Reg reg = null;
        for (int i = 0; i < rowCount; i++) {
//            System.out.println("������"+rowCount);
//            System.out.println("table������պ�����"+tableParm);
            String onlyCaseNo = tableParm.getValue("CASE_NO", i);
//            System.out.println("��" + i + "��case_no����������" + onlyCaseNo);
            String flg = tableParm.getValue("FLG", i);
            if ("Y".equals(flg)) {
                TParm selInvoice = new TParm();
                selInvoice.setData("STATUS", "0");
                selInvoice.setData("RECP_TYPE", "OPB");
                selInvoice.setData("CASHIER_CODE", Operator.getID());
                selInvoice.setData("TERM_IP", Operator.getIP());
//                System.out.println("Ʊ�������������" + selInvoice);
                TParm invoice = BILInvoiceTool.getInstance().selectNowReceipt(
                        selInvoice);
//                System.out.println("Ʊ������������Ϣ======" + invoice);
                String invNo = invoice.getValue("UPDATE_NO", 0);
                String endInvNo = invoice.getValue("END_INVNO", 0);
                if (invNo.compareTo(endInvNo) > 0) {
                    this.messageBox("Ʊ��������!");
                    return;
                }
                pat = Pat.onQueryByMrNo(tableParm.getValue("MR_NO", i));
                reg = Reg.onQueryByCaseNo(pat, onlyCaseNo);
                TParm parm = new TParm();
                parm.setData("CASE_NO", onlyCaseNo);
                parm.setData("MR_NO", tableParm.getValue("MR_NO", i));
                parm.setData("INV_NO", invNo);
                parm.setData("OPT_USER", Operator.getID());
                parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
                parm.setData("OPT_TERM", Operator.getIP());
                parm.setData("REGION_CODE", Operator.getRegion());
                parm.setData("START_INVNO", invoice.getData("START_INVNO", 0));
                // =============pangben modify 201110817 start
                parm.setData("feeShow", ""); // ���ı���ʹ�ùܿ�
                parm.setData("TOT_AMT", this.getValueDouble("TOT_AMT"));
                parm.setData("billFlg", "Y"); // ����: N ������:Y
                parm.setData("CONTRACT_CODE", ""); // ���˵�λ

                TParm selOpdParm = new TParm();
                selOpdParm.setData("CASE_NO", onlyCaseNo);
                selOpdParm.setData("REGION_CODE", Operator.getRegion());
                OrderTool.getInstance().selDataForOPBEKTC(selOpdParm);
                selOpdParm.setData("REGION_CODE", Operator.getRegion());
                TParm opdParm = OrderTool.getInstance().selDataForOPBEKTC(
                        selOpdParm);
                TParm result = ektSavePrint(opdParm, parm);
                if (null == result || result.getErrCode() < 0) {
                    this.messageBox("E0005");
                    return;
                }
                String receiptNo = result.getValue("RECEIPT_NO", 0);
                int opdCount = opdParm.getCount("CASE_NO");
                if (opdCount <= 0) {
//                    this.messageBox("�޿��շ�ҽ��");
//                    return;
                	continue;
                }
                TParm recpParm = null;
                // �����վݵ�����:ҽ�ƿ��շѴ�Ʊ|�ֽ��շѴ�Ʊ||ҽ����Ʊ
                recpParm = OPBReceiptTool.getInstance().getOneReceipt(receiptNo);
                if(recpParm.getErrCode()<0){
                    System.out.println("Ʊ�ݲ�ѯ����  " + result.getErrText());
                        return;
                }
//                System.out.println("Ʊ����Ϣ" + recpParm);
                onPrintRPT(recpParm, onlyCaseNo,
                           tableParm.getValue("PAT_NAME", i),
                           tableParm.getValue("IDNO", i), reg.getAdmType());
            }

        }
        this.onClear();
    }

    /**
     * �ֽ��Ʊ��ϸ���
     *
     * @param oneReceiptParm
     *            TParm
     * @param recpParm
     *            TParm
     * @param dparm
     *            TParm
     */
    private void onPrintCashParm(TParm oneReceiptParm, TParm recpParm,
                                 TParm dparm) {
        String receptNo = recpParm.getData("RECEIPT_NO", 0).toString();
        dparm.setData("NO", receptNo);
        TParm tableresultparm = OPBTool.getInstance().getReceiptDetail(dparm);
        // if(orderParm.getCount()>10){
        // oneReceiptParm.setData("DETAIL", "TEXT", "(���������ϸ��)");
        // }
        oneReceiptParm.setData("TABLE", tableresultparm.getData());
    }

    /**
     * ��ӡƱ�ݷ�װ===================pangben 20111014
     * @param recpParm TParm
     * @param onlyCaseNo String
     * @param patName String
     * @param IDNO String
     * @param admType String
     */
    private void onPrintRPT(TParm recpParm, String onlyCaseNo, String patName,
                            String IDNO, String admType) {
//        System.out.println("recpParm=====" + recpParm);
        DecimalFormat df = new DecimalFormat("0.00");
        TParm oneReceiptParm = new TParm();
        TParm insOpdInParm = new TParm();
        String confirmNo = "";
        String cardNo = "";
        String insCrowdType = "";
        String insPatType = "";
        // ������Ա������
        String spPatType = "";
        // ������Ա���
        String spcPerson = "";
        String startStandard = ""; // �𸶱�׼
        String accountPay = ""; // ����ʵ���ʻ�֧��
        String gbNhiPay = ""; // ҽ��֧��
        String reimType = ""; // �������
        String gbCashPay = ""; // �ֽ�֧��
        String agentAmt = ""; // �������

        insOpdInParm.setData("REGION_CODE", Operator.getRegion());
        insOpdInParm.setData("CONFIRM_NO", confirmNo);
        insOpdInParm.setData("CASE_NO", onlyCaseNo);
        // INS_CROWD_TYPE, INS_PAT_TYPE
        // Ʊ����Ϣ
        // ����
        oneReceiptParm.setData("PAT_NAME", "TEXT", patName);
        // ������Ա���
        oneReceiptParm.setData("SPC_PERSON", "TEXT",
                               spcPerson.length() == 0 ? "" : spcPerson);
        // ��ᱣ�Ϻ�
        oneReceiptParm.setData("Social_NO", "TEXT", cardNo);
        // ��Ա���
        oneReceiptParm.setData("CTZ_DESC", "TEXT", "ְ��ҽ��");
        // �������
        // ======zhangp 20120228 modify start
        if ("1".equals(insPatType)) {
            oneReceiptParm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
            oneReceiptParm.setData("Cost_class", "TEXT", "��ͳ");
        } else if ("2".equals(insPatType) || "3".equals(insPatType)) {
            oneReceiptParm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
            oneReceiptParm.setData("Cost_class", "TEXT", "����");
        }
        // =====zhangp 20120228 modify end
        // ҽ�ƻ�������
        oneReceiptParm.setData("HOSP_DESC", "TEXT", Manager.getOrganization()
                               .getHospitalCHNFullName(Operator.getRegion()));
        // �𸶽��
        oneReceiptParm
                .setData("START_AMT", "TEXT",
                         startStandard.length() == 0 ? "0.00" : df
                         .format(startStandard));
        // ����޶����
        oneReceiptParm.setData("MAX_AMT", "TEXT", "--");
        // �˻�֧��
        oneReceiptParm.setData("DA_AMT", "TEXT",
                               accountPay.length() == 0 ? "0.00" :
                               df.format(accountPay));

        // ���úϼ�
        oneReceiptParm.setData("TOT_AMT", "TEXT", df.format(recpParm.getDouble(
                "TOT_AMT", 0)));
        // ������ʾ��д���
        oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance()
                               .numberToWord(recpParm.getDouble("TOT_AMT", 0)));

        // ͳ��֧��
        oneReceiptParm.setData("Overall_pay", "TEXT", StringTool.round(recpParm
                .getDouble("Overall_pay", 0), 2));
        // ����֧��
        oneReceiptParm.setData("Individual_pay", "TEXT", df.format(recpParm
                .getDouble("TOT_AMT", 0)));
        // �ֽ�֧��= ҽ�ƿ����+�ֽ�+��ɫͨ��
        double payCash = StringTool.round(recpParm.getDouble("PAY_CASH", 0), 2)
                         + StringTool
                         .round(recpParm.getDouble("PAY_MEDICAL_CARD", 0), 2)
                         +
                         StringTool.round(recpParm.getDouble("PAY_OTHER1", 0),
                                          2);
        // �ֽ�֧��
        oneReceiptParm.setData("Cash", "TEXT",
                               gbCashPay.length() == 0 ? payCash :
                               df.format(gbCashPay));

        // �˻�֧��---ҽ�ƿ�֧��
        oneReceiptParm.setData("Recharge", "TEXT", 0.00);
        // ҽ�ƾ������
        oneReceiptParm.setData("AGENT_AMT", "TEXT",
                               agentAmt.length() == 0 ? "0.00" :
                               df.format(agentAmt));
        // =====zhangp 20120229 modify start
        if (agentAmt.length() != 0) {
            oneReceiptParm.setData("AGENT_NAME", "TEXT", "ҽ�ƾ���֧��");
        }
        oneReceiptParm.setData("MR_NO", "TEXT", onlyCaseNo);
        // =====zhangp 20120229 modify end
        // ��ӡ����
        oneReceiptParm.setData("OPT_DATE", "TEXT", StringTool.getString(
                SystemTool.getInstance().getDate(), "yyyy/MM/dd"));
        // ҽ�����
        oneReceiptParm.setData("PAY_DEBIT", "TEXT",
                               gbNhiPay.length() == 0 ?
                               StringTool.round(recpParm.getDouble(
                                       "PAY_INS_CARD", 0), 2) :
                               df.format(gbNhiPay));
        if (recpParm.getDouble("PAY_OTHER1", 0) > 0) {
            // ��ɫͨ�����
            oneReceiptParm.setData("GREEN_PATH", "TEXT", "��ɫͨ��֧��");
            // ��ɫͨ�����
            oneReceiptParm.setData("GREEN_AMT", "TEXT", StringTool.round(
                    recpParm.getDouble("PAY_OTHER1", 0), 2));

        }
        // ҽ������
        oneReceiptParm.setData("DR_NAME", "TEXT", recpParm.getValue(
                "CASHIER_CODE", 0));

        // ��ӡ��
        oneReceiptParm.setData("OPT_USER", "TEXT", Operator.getName());
        oneReceiptParm.setData("USER_NAME", "TEXT", Operator.getID());
        oneReceiptParm.setData("TEXT_TITLE1", "TEXT", "(��������嵥)");
        // =====20120229 zhangp modify start
        oneReceiptParm.setData("CARD_CODE", "TEXT", IDNO); // �������ҽ��
        // =====20120229 zhangp modify end
        for (int i = 1; i <= 30; i++) {
            if (i < 10) {
                oneReceiptParm.setData("CHARGE0" + i, "TEXT", recpParm
                                       .getDouble("CHARGE0" + i, 0) == 0 ? "" :
                                       recpParm
                                       .getData("CHARGE0" + i, 0));
            } else {
                oneReceiptParm.setData("CHARGE" + i, "TEXT", recpParm
                                       .getDouble("CHARGE" + i, 0) == 0 ? "" :
                                       recpParm
                                       .getData("CHARGE" + i, 0));
            }
        }
        // =================20120219 zhangp modify start
        oneReceiptParm.setData("CHARGE01", "TEXT", df.format(recpParm
                .getDouble("CHARGE01", 0)
                + recpParm.getDouble("CHARGE02", 0)));

        TParm dparm = new TParm();
        dparm.setData("CASE_NO", onlyCaseNo);
        dparm.setData("ADM_TYPE", admType);
        onPrintCashParm(oneReceiptParm, recpParm, dparm);
        oneReceiptParm.setData("RECEIPT_NO", "TEXT", recpParm.getValue("RECEIPT_NO", 0));//add by wanglong 20121217
//        this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBRECTPrint.jhw",
//                             oneReceiptParm,true);
        this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
                             IReportTool.getInstance().getReportParm("OPBRECTPrint.class", oneReceiptParm), true);//����ϲ�modify by wanglong 20130730
        return;

    }

    /**
     *
     * @param opdParm TParm
     * @param parm TParm
     * @return TParm
     */
    private TParm ektSavePrint(TParm opdParm, TParm parm) {
        TParm result = new TParm();
        int opdCount = opdParm.getCount("CASE_NO");
        if (opdCount <= 0) {
            this.messageBox("�޿��շ�ҽ��");
            return null;
        }
        parm.setData("opdParm", opdParm.getData()); // ���һ�����ܽ��
        parm.setData("REGION_CODE", Operator.getRegion()); // ����
        //===zhangp 20120328 start
        parm.setData("ADM_TYPE", opdParm.getData("ADM_TYPE", 0));
        //===zhangp 20120328 end
        result = TIOM_AppServer.executeAction("action.opb.OPBAction",
                                              "onOPBEktprint", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ��˴�ӡ����
     * @param tableParm TParm
     * @return boolean
     */
    public boolean checkPrintData(TParm tableParm) {
        //δ��Ʊ����
        int count = table.getRowCount();
        if (count <= 0) {
            messageBox("�޴�ӡ����!");
            return false;
        }
        for (int i = 0; i < count; i++) {
            if (tableParm.getValue("FLG", i).equals("Y")) {
                return true;
            }
        }
        messageBox("�޴�ӡ����!");
        return false;
    }

    /**
     * ���
     */
    public void onClear() {
        //��ʼ��ҳ����Ϣ
        initPage();
        this.callFunction("UI|TABLE|removeRowAll");

    }
    
    /**
     * �����շ�������ӡ
     * @param parm TParm
     * @return String
     */
    public static String getOPBPatchPrintSql(TParm parm) {
        String sql = "";
        if (parm == null)
            return sql;
        sql =
                "  SELECT '' AS FLG, TO_CHAR (F.ADM_DATE, 'YYYY/MM/DD') ADM_DATE," +
                "         F.SESSION_CODE, F.REALDEPT_CODE, F.REALDR_CODE, F.QUE_NO, F.MR_NO," +
                "         SUM (A.AR_AMT) AS AR_AMT, F.CASE_NO, G.PAT_NAME,G.IDNO,F.CTZ1_CODE,F.CONFIRM_NO " +
                "    FROM OPD_ORDER A, EKT_TRADE D, REG_PATADM F, SYS_PATINFO G" +
                "   WHERE A.BUSINESS_NO = D.TRADE_NO" +
                "     AND A.BILL_FLG = 'Y'" +
                "     AND A.BILL_TYPE IN ('C','E')" +
                "     AND A.RELEASE_FLG <> 'Y'" +
                "     AND A.RECEIPT_NO IS NULL" +
                "     AND (A.BUSINESS_NO IS NOT NULL OR A.BUSINESS_NO <> '')" +
                "     AND (A.PRINT_FLG IS NULL OR A.PRINT_FLG = 'N' OR A.PRINT_FLG = '')" +
                "     AND D.STATE = '1'" +
                "     AND F.MR_NO = G.MR_NO" +
                "     AND A.MR_NO = G.MR_NO" +
                "     AND F.CASE_NO = A.CASE_NO" ;
    	//===zhangp �˴�д�� ��������ֻ�ܲ鵽����������� start
//    	String opDept = Operator.getDept();
//    	if(opDept.equals("020103")){
//    		sql += " AND F.REALDEPT_CODE='020103' ";
//    	}else{
//    		sql += " AND F.REALDEPT_CODE<>'020103' ";
//    	}
//    	//===zhangp �˴�д�� ��������ֻ�ܲ鵽����������� end
        String admType = parm.getValue("ADM_TYPE");
        if (admType != null && !admType.equals(""))
            sql += " AND F.ADM_TYPE='" + admType + "' ";
        String clinicArea = parm.getValue("CLINIC_AREA");
        if (clinicArea != null && !clinicArea.equals(""))
            sql += " AND F.CLINICAREA_CODE='" + clinicArea + "' ";
        String sessionCode = parm.getValue("SESSION_CODE");
        if (sessionCode != null && !sessionCode.equals(""))
            sql += " AND F.SESSION_CODE='" + sessionCode + "' ";
        String realDeptCode = parm.getValue("REALDEPT_CODE");
        if (realDeptCode != null && !realDeptCode.equals(""))
            sql += " AND F.REALDEPT_CODE='" + realDeptCode + "' ";
        String realDrCode = parm.getValue("REALDR_CODE");
        if (realDrCode != null && !realDrCode.equals(""))
            sql += " AND F.REALDR_CODE='" + realDrCode + "' ";
        //=================pangben modify 20110407 start ��������ѯ����
        String region = parm.getValue("REGION_CODE");
        if (region != null && !region.trim().equals(""))
            sql += " AND F.REGION_CODE='" + region + "' ";
        //=================pangben modify 20110407 stop
        //=================liling  modify 20140424 start ��Ӳ�ѯ����ʱ���
        String startDate = parm.getValue("START_DATE");
		String endDate = parm.getValue("END_DATE") ;
		if(startDate != null && !"".equals(startDate) && endDate != null && !"".equals(endDate))
			sql +=" AND F.ADM_DATE BETWEEN TO_DATE ('" + SystemTool.getInstance().getDateReplace(startDate, true)+ "'," +
				" 'YYYYMMDDHH24MISS' )" +
				" AND TO_DATE ('" +SystemTool.getInstance().getDateReplace(endDate.substring(0,10), false) + "'," +
				" 'YYYYMMDDHH24MISS' ) ";				
        //=================liling  modify 20140424 stop
        sql +=
                "GROUP BY F.ADM_DATE," +
                "         F.SESSION_CODE," +
                "         F.REALDEPT_CODE," +
                "         F.REALDR_CODE," +
                "         F.QUE_NO," +
                "         F.MR_NO," +
                "         G.PAT_NAME," +
                "         F.CASE_NO," +
                "         G.IDNO," +
                "         F.CTZ1_CODE," +
                "		  F.CONFIRM_NO"+
                " ORDER BY F.CASE_NO";
        return sql;
    }
}
