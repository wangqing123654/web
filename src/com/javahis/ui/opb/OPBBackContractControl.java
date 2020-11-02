package com.javahis.ui.opb;

import com.dongyang.control.TControl;
import jdo.opb.OPBReceiptTool;
import com.dongyang.ui.TTable;
import com.javahis.util.StringUtil;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.IReportTool;
import jdo.sys.Pat;
import com.dongyang.data.TParm;
import jdo.bil.BILContractRecordTool;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import jdo.opb.OPB;
import jdo.reg.PatAdmTool;
import jdo.reg.Reg;
import jdo.opb.OPBReceiptList;
import jdo.sys.SystemTool;
import jdo.opd.OrderTool;
import com.dongyang.util.StringTool;
import jdo.util.Manager;
import com.dongyang.util.TypeTool;
import java.sql.Timestamp;

/**
 * <p>Title: ���������ϸ��ѯ�������˷ѽ���</p>
 *
 * <p>Description:���������ϸ��ѯ�� �����˷ѽ���</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110823
 * @version 1.0
 */

public class OPBBackContractControl extends TControl {
    public OPBBackContractControl() {
    }

    /**
     * Ʊ�ݵ�
     */
    TTable tableM;
    /**
     * ҽ����
     */
    TTable tableD;
    /**
     * �Ƽ۶���
     */
    OPB opb;
    /**
     * table����
     */
    TParm tableMParm;
    /**
     * ӡˢ��
     */
    String printNoOnly;
    /**
     * �˷�Ȩ��
     */
    boolean backBill = false;
    /**
     * �Ƿ���˷�
     */
    boolean returnFeeFlg = false;
    //����
    private TParm batchNoParm;
    private String printNo;//Ʊ��
    private Timestamp bill_date;//�շ�����

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        tableM = (TTable)this.getComponent("TABLEM");
        tableD = (TTable)this.getComponent("TABLED");
        //��������
        dealData();
        batchNoParm=new TParm(TJDODBTool.getInstance().select("SELECT ORDER_CODE, BATCH_NO FROM IND_STOCK"));
    }

    /**
     * ��������
     */
    public void dealData() {
        //��ʼ��Ȩ��
        if (!initPopedem())
            return;
//        //�����ݷ������
//        if (!getReceiptList())
//            return;
        //�õ��շ��б�
        getReceiptParm();
    }

    /**
     * ����ǰ̨�������ݺͳ�ʼ��Ȩ��
     * @return boolean
     */
    public boolean initPopedem() {
        TParm parm;
        //ǰ̨�����ļƼ۶���
        if (this.getParameter() != null) {
            parm = (TParm)this.getParameter();
            //����opb
            if (!initOpb(parm))
                return false;
        }
        //�˷�Ȩ��
        if (!getPopedem("BACKBILL")) {
            backBill = false;
        }
        return true;
    }

    /**
     * ����opb
     * @param parm TParm
     * @return boolean
     */
    public boolean initOpb(TParm parm) {
        String caseNo = parm.getValue("CASE_NO");
        String mrNo = parm.getValue("MR_NO");
        Pat pat = Pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            this.messageBox("���޴˲�����");
            return false;
        }
        //���渳ֵ
        setValueForParm(
                "MR_NO;PAT_NAME;IDNO;SEX_CODE;COMPANY_DESC",
                pat.getParm());
        Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
        //�жϹҺ���Ϣ
        if (reg == null)
            return false;
        //�����
        callFunction("UI|CTZ1_CODE|setValue", reg.getCtz1Code());
        callFunction("UI|CTZ2_CODE|setValue", reg.getCtz2Code());
        callFunction("UI|CTZ3_CODE|setValue", reg.getCtz3Code());
        //ͨ��reg��caseNo�õ�pat
        opb = OPB.onQueryByCaseNo(reg);
        //�������ϲ��ֵط���ֵ
        if (opb == null) {
            this.messageBox("�˲�����δ����!");
            return false;
        }
        return true;
    }
    /**
     * �õ��շѽ������շ��б�
     */
    public void getReceiptParm() {
        TParm parm = OPBReceiptTool.getInstance().getContractReceipt(opb.getReg().
                caseNo());
//            opb.getReceiptList().getParm(opb.getReceiptList().PRIMARY);
        tableM.setParmValue(parm);
//        opb.getReceiptList().initOrder(opb.getPrescriptionList());
    }

    /**
     * �������¼�
     */
    public void onClickTableM() {
        tableMParm = new TParm();
        returnFeeFlg = false;
        int row = tableM.getSelectedRow();
        TParm tableParm = tableM.getParmValue();
        printNo= tableParm.getValue("PRINT_NO",row);//Ʊ��
        bill_date=  tableParm.getTimestamp("BILL_DATE",row);//�շ�����
//        System.out.println("��������"+tableParm);
//        //�õ�һ��Ʊ��
//        OPBReceipt opbReceipt = (OPBReceipt) opb.getReceiptList().get(row);
//        //�õ����е�parm
//        TParm parm = opbReceipt.getOrderList().getParm(OrderList.PRIMARY);
        TParm orderParm = new TParm();
        orderParm.setData("RECEIPT_NO", tableParm.getData("RECEIPT_NO", row));
        orderParm.setData("CASE_NO", tableParm.getData("CASE_NO", row));
        TParm parm = OrderTool.getInstance().queryFill(orderParm);
//        System.out.println("��ϸ������"+parm);
        tableMParm = parm;
        tableD.setParmValue(parm);
        //У���Ƿ��ѵ�����ѷ�ҩ
//        int count = parm.getCount("EXEC_FLG");
//        String exeFlg = "";
//        for (int i = 0; i < count; i++) {
//            exeFlg = parm.getValue("EXEC_FLG", i);
//            if ("Y".equals(exeFlg)) {
//                returnFeeFlg = true;
//                return;
//            }
//        }
    }

    /**
     * �˷����
     * @return boolean
     */
    public boolean onSave() {
        int row = tableM.getSelectedRow();
        if (row < 0) {
            messageBox("��ѡ��Ҫ�˷ѵ�Ʊ��!");
        }
        switch (this.messageBox("��ʾ��Ϣ",
                                "�Ƿ��˷�", this.YES_NO_OPTION)) {
        case 0:
            if (!backReceipt(row))
                return false;
            break;
        case 1:
            return true;
        }
        return true;
    }

    /**
     * �˷ѷ���
     * @param row int
     * @return boolean
     */
    public boolean backReceipt(int row) {
        if (returnFeeFlg) {
            this.messageBox("�ѷ�ҩ���ѵ��죬�����˷�!");
            return false;
        }
        TParm parm = new TParm();
        parm.setData("RECEIPT_NO",
                     tableM.getParmValue().getValue("RECEIPT_NO", row));
        parm.setData("RECEIPT_TYPE", "OPB");
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            parm.setData("REGION_CODE", Operator.getRegion());
        //��ѯ��ǰ���ﲡ���Ƿ����
        TParm result = BILContractRecordTool.getInstance().regRecodeQuery(parm);
        String bilStauts = result.getValue("BIL_STATUS", 0);
        //���˲�����û��ִ���շѴ�Ʊ,��ȥ����BIL_INVRCP�������
        if (null != bilStauts && "1".equals(bilStauts)) {
            //this.messageBox("û��ִ�н���,�����˷�");
             //�����շѣ��˹�ʱ���Ѿ����˵���û�н��н��˲���ʱ,�˷Ѳ���֮�󽫲���ʾ��������
            if (!onSaveBackReceiptStatus(row)) {
                //����ʧ��
                messageBox("E0001");
                return false;
            }
        } else {
            //        if (!opb.onSaveBackReceipt(row)) {
            if (!onSaveBackReceipt(row)) {
                //����ʧ��
                messageBox("E0001");
                return false;
            }
            //����ɹ�
            messageBox("P0001");
        }
        //��������³�ʼ��Ʊ��
        afterSave();
        return true;
    }

    /**
     * ��������³�ʼ��Ʊ��
     */
    public void afterSave() {
//        //�����ݷ������
//        if (!getReceiptList())
//            return;
        //�õ��շ��б�
        dealData();
        getReceiptParm();
        tableD.removeRowAll();
    }

    /**
     * ��ӡ�����嵥
     */
//    public void onFill() {
//        if (opb == null)
//            return;
//        System.out.println("tableMParm" + tableMParm);
//        int count = tableMParm.getCount("SETMAIN_FLG");
//        String setmainFlg = "";
//        for (int i = count - 1; i >= 0; i--) {
//            setmainFlg = tableMParm.getValue("SETMAIN_FLG", i);
//            if ("Y".equals(setmainFlg)) {
//                tableMParm.removeRow(i);
//            }
//        }
//        TParm parm = opb.getReceiptList().dealTParm(tableMParm,batchNoParm);
//        if (parm == null)
//            return;
//        TParm pringListParm = new TParm();
//        pringListParm.setData("TABLEORDER", parm.getData());
//        //������
//        pringListParm.setData("MR_NO", opb.getPat().getMrNo());
//        //��������
//        pringListParm.setData("PAT_NAME", opb.getPat().getName());
//        //�������
//        pringListParm.setData("CASE_NO", opb.getReg().caseNo());
//        String sql =
//                " SELECT CHN_DESC FROM SYS_DICTIONARY " +
//                "  WHERE GROUP_ID ='SYS_SEX' " +
//                "    AND ID = '" + opb.getPat().getSexCode() + "'";
//        TParm sexParm = new TParm(TJDODBTool.getInstance().select(sql));
//        //�Ա�
//        pringListParm.setData("SEX_CODE", sexParm.getValue("CHN_DESC", 0));
//        //��������
//        pringListParm.setData("ADM_DATE", opb.getReg().getAdmDate());
//        //ҽԺ����
//        pringListParm.setData("HOSP",
//                              Operator.getHospitalCHNFullName() + "��������嵥");
//        this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBOrderList.jhw",
//                             pringListParm);
//    }
    /**
        * ��ӡ�����嵥
        * ============̩��ҽԺ�����嵥��ӡ
        */
       public void onFill() {
           if (opb == null)
               return;
           //System.out.println("tableMParm" + tableMParm);
           //ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;
           //DOSAGE_QTY;DOSAGE_UNIT;OWN_PRICE;AR_AMT
           int count = tableMParm.getCount("SETMAIN_FLG");
           String setmainFlg = "";
           for (int i = count - 1; i >= 0; i--) {
               setmainFlg = tableMParm.getValue("SETMAIN_FLG", i);//����ҽ��ע��
               if ("Y".equals(setmainFlg)) {
                   tableMParm.removeRow(i);
               }
           }
           TParm parm = opb.getReceiptList().dealTParm(tableMParm,batchNoParm);
           if (parm == null)
               return;
           double sum=parm.getDouble("SUM");//�ϼƽ��
           TParm pringListParm = new TParm();
           pringListParm.setData("TABLEORDER", parm.getData());
           //��������
           pringListParm.setData("PAT_NAME","TEXT", opb.getPat().getName());//��������
           pringListParm.setData("HOSP","TEXT",Operator.getHospitalCHNFullName());//ҽԺ����
           pringListParm.setData("TITLE","TEXT",Operator.getHospitalCHNFullName());//ҽԺ����
           pringListParm.setData("BILL_DATE","TEXT",StringTool.getString(TypeTool.getTimestamp(bill_date),"yyyyMMddHHmmss"));//�շ�����

           pringListParm.setData("PRINT_NO","TEXT",printNo);//Ʊ��
           String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
                  getInstance().getDBTime()), "yyyy/MM/dd"); //������
           pringListParm.setData("DATE","TEXT",yMd);//����
           pringListParm.setData("TOTAL","TEXT",sum);//����
//           this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBOrderPrint.jhw",pringListParm);
           this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBOrderPrint.jhw"),
                                IReportTool.getInstance().getReportParm("OPBOrderPrint.class", pringListParm));//����ϲ�modify by wanglong 20130730
    }
    /**
     * �ֽ�����Ʊ��
     * @param row int
     * @return boolean
     */
    public boolean onSaveBackReceipt(int row) {
        TParm saveParm = tableM.getParmValue();
        TParm actionParm = saveParm.getRow(row);
        actionParm.setData("OPT_USER_T", Operator.getID());
        actionParm.setData("OPT_TERM_T", Operator.getIP());
        actionParm.setData("OPT_DATE_T", SystemTool.getInstance().getDate());
        if (saveParm == null)
            return false;
        //����opbaction
        TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
                "backOPBRecp", actionParm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return false;
        }
        return true;
    }

    /**
     * �ֽ�����Ʊ��:BIL_STATUS =1 ���˲���ӡ�˵�
     * @param row int
     * @return boolean
     * ==================pangben modify 20110822
     */
    public boolean onSaveBackReceiptStatus(int row) {
        TParm saveParm = tableM.getParmValue();
        TParm actionParm = saveParm.getRow(row);
        actionParm.setData("OPT_USER_T", Operator.getID());
        actionParm.setData("OPT_TERM_T", Operator.getIP());
        actionParm.setData("OPT_DATE_T", SystemTool.getInstance().getDate());
        if (saveParm == null)
            return false;
        //����opbaction
        TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
                "backOPBRecpStatus", actionParm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return false;
        }
        return true;
    }

    /**
     * Ʊ�ݲ���
     * @param row int
     * @return boolean
     */
    public boolean onSaveRePrint(int row) {
        TParm saveRePrintParm = getRePrintData(row);
        if (saveRePrintParm == null)
            return false;
        //����opbaction
        TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
                "saveOPBRePrint", saveRePrintParm);
        printNoOnly = result.getValue("PRINT_NO");
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return false;
        }

        return true;
    }

    /**
     * �õ���ӡ����
     * @param row int
     * @return TParm
     */
    public TParm getRePrintData(int row) {
        TParm saveParm = tableM.getParmValue();
        TParm actionParm = saveParm.getRow(row);
        actionParm.setData("OPT_USER", Operator.getID());
        actionParm.setData("OPT_TERM", Operator.getIP());
        actionParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
//        System.out.println("actionParm"+actionParm);
        return actionParm;

    }

}
