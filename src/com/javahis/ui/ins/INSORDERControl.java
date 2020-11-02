package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.config.TConfigParm;
import com.dongyang.data.TParm;
import jdo.ins.InsOrderTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.Operator;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.text.DecimalFormat;
import java.util.Date;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: ҽ������</p>
 *
 * <p>Description: ��Ƕʽҽ������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class INSORDERControl extends TControl {
    private static String TABLE1 = "Table1";
    private static String TABLE2 = "Table2";
    private TConfigParm configParm;
    private int mrInt;
    private int ipdInt;
    private static final String actionName = "action.ins.InsAction";
    private String action = "INSERT";
    public void onInit() {
        super.onInit();
        configParm = getConfigParm().newConfig(
               "%ROOT%\\config\\sys\\medicareInfo.x");
       this.mrInt = configParm.getConfig().getInt("", "MRNO");
       this.ipdInt = configParm.getConfig().getInt("", "IPDNO");
        callFunction("UI|" + TABLE1 + "|addEventListener",
                     TABLE1 + "->" + TTableEvent.CLICKED, this, "onTableClicked1");
        callFunction("UI|" + TABLE2 + "|addEventListener",
                     TABLE1 + "->" + TTableEvent.CLICKED, this, "onTableClicked2");
        Object parm = getParameter();
        TParm result = new TParm();
        if (parm != null && parm instanceof TParm) {
            //���ղ���
            result = (TParm) parm;
        }
        this.initPage(result);
        onClear();
    }
    /**
     * ��ʼ��UI
     */
    public void initPage(TParm parm){
        //this.setValue("START_DATE",this.result.getTimestamp("ADM_DATE"));
    }
    /**
     *���Ӷ�Table1�ļ���
     * @param row int
     */
    public void onTableClicked1(int row) {
        if(row < 0)
              return;
          callFunction("UI|"+TABLE2+"|removeRowAll");
          callFunction("UI|save|setEnabled", true);
          callFunction("UI|renewedly|setEnabled", true);
          callFunction("UI|NHI_COMPANY|setEnabled", false);
          callFunction("UI|CTZ_CODE|setEnabled", false);
          callFunction("UI|PAY_KIND_CODE|setEnabled", false);
          callFunction("UI|IN_DEPT|setEnabled", false);
          callFunction("UI|BED_NO|setEnabled", false);
          callFunction("UI|MR_NO|setEnabled", false);
          callFunction("UI|IPD_NO|setEnabled", false);
          callFunction("UI|PAT_NAME|setEnabled", false);
          callFunction("UI|PAT_AGE|setEnabled", false);
          callFunction("UI|IDNO|setEnabled", false);
          callFunction("UI|ADM_DATE|setEnabled", false);
          callFunction("UI|DS_DATE|setEnabled", false);
          callFunction("UI|ALL_PRICE|setEnabled", false);
          callFunction("UI|RESV_NO|setEnabled", false);
          callFunction("UI|RESV_NO|setEnabled", false);
          callFunction("UI|COMPANY_DESC|setEnabled", false);
          //�ж��Ƿ��Ѿ�����
          TParm parmRow = this.getSelectRowData(TABLE1);
          TParm parm = InsOrderTool.getInstance().checkInsStatus(parmRow);
          if (parm.getInt("INS_STATUS", 0) == 1) {
              callFunction("UI|cancelinfo|setEnabled", false);
              callFunction("UI|onsaveinfo|setEnabled", true);
          }else if(parm.getInt("INS_STATUS", 0) == 2){
              callFunction("UI|cancelinfo|setEnabled", true);
              callFunction("UI|onsaveinfo|setEnabled", false);
          }else if(parm.getInt("INS_STATUS", 0) == 3){
              this.messageBox("�Ѿ����������Ҫ�����������ϵ������Աȡ�����㣡");
          }
          parm = InsOrderTool.getInstance().checkIbsStatus(parmRow);
          if(parm.getCount("DISCHARGE_FLG")<=0){
              this.messageBox("�˲���û�в�����Ժ�˵���");
              return;
          }
          this.clearValue("ALL_PRICE;RESV_NO");
          this.selTable2Data();
          this.action = "EDIT";
    }
    /**
     * ��ѯ�ڶ���GIRD
     */
    public void selTable2Data(){
        out("��ѯ�ڶ���GIRDbegin");
        TParm  parmRow = this.getSelectRowData(TABLE1);
        TParm action = InsOrderTool.getInstance().queryOrderTable(parmRow);
        out("ѡ��������"+parmRow);
        out("��ѯ�������"+action);
        if(action.getErrCode()<0){
          messageBox_("��ѯ����");
          return;
        }
        if(action.getInt("ACTION","COUNT")>0){
            out("��ѯ�������"+action);
            this.jsAllPrice(action);
            callFunction("UI|"+TABLE2+"|setParmValue",action);
        }
        TParm actionParm = InsOrderTool.getInstance().queryAdmInpTOTAL_DEPOSIT(parmRow);
        if(actionParm.getInt("ACTION","COUNT")>0){
            double totalDeposit = actionParm.getDouble("TOTAL_DEPOSIT",0) - this.getValueDouble("ALL_PRICE");
            this.setValue("RESV_NO",totalDeposit);
        }
        out("��ѯ�ڶ���GIRDend");
    }
    /**
     * �����ܷ���
     * @param parm TParm
     */
    public void jsAllPrice(TParm parm){
        DecimalFormat df = new DecimalFormat("#############0.00");
        int count = parm.getCount("SEQ_NO");
        double totAmt = 0.0;
        for(int i=0;i<count;i++){
           totAmt+=parm.getDouble("TOTAL_AMT",i);
        }

        this.setValue("ALL_PRICE",df.format(StringTool.round(totAmt,2)));
    }
    /**
     * ��ѯ������Ϣ
     */
    public void seleInfoBase(TParm parm){
        //System.out.println("��ѯ������Ϣ"+parm);
        //��ʼʱ��
        Timestamp startDate = (Timestamp)this.getValue("ADM_DATE");
        //����ʱ��
        Timestamp endDate = SystemTool.getInstance().getDate();
        //�����
        String caseNo = parm.getValue("CASE_NO");
        TParm action = new TParm();
        action.setData("START_DATE",startDate);
        action.setData("END_DATE",endDate);
        action.setData("CASE_NO",caseNo);
        action.setData("CTZ_CODE",parm.getValue("CTZ_CODE"));
        action.setData("INS_COMPANY",parm.getValue("INS_COMPANY"));
        action.setData("OPT_USER",parm.getValue("OPT_USER"));
        action.setData("OPT_TERM",parm.getValue("OPT_TERM"));
        action.setData("BKC023",parm.getValue("BKC023"));
//        this.messageBox_(action);
        if(this.excuteSave(action)){
            messageBox_("����ɹ���");
            this.selTable2Data();
        }else{
            messageBox_("����ʧ�ܣ�");
        }
    }
    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool(){
        return TJDODBTool.getInstance();
    }
    /**
      * ִ�б���
      * @param parm TParm
      * @return boolean
      */
     public boolean excuteSave(TParm parm){
         TParm actionParm = TIOM_AppServer.executeAction(actionName,"excuteSave",parm);
         if(actionParm.getErrCode()<0){
             return false;
         }
         return true;
     }
    /**
     * �õ�ѡ��������
     * @param tableTag String
     * @return TParm
     */
    public TParm getSelectRowData(String tableTag){
        int selectRow = (Integer) callFunction("UI|" + tableTag +"|getSelectedRow");
        out("�к�" + selectRow);
        TParm parm = (TParm) callFunction("UI|" + tableTag + "|getParmValue");
        out("GRID����" + parm);
        TParm parmRow = parm.getRow(selectRow);
        parmRow.setData("OPT_USER",Operator.getID());
        parmRow.setData("OPT_TERM",Operator.getIP());
        return parmRow;
    }
    /**
     *���Ӷ�Table2�ļ���
     * @param row int
     */
    public void onTableClicked2(int row) {
        if(row < 0)
              return;

          action = "EDIT";
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        out("��ѯbegin");
        if (this.getValue("MR_NO").toString().length() != 0) {
            this.setValue("MR_NO",
                          this.getSelectParm("MR_NO",
                                             this.getValue("MR_NO").toString()));
        }
        if (this.getValue("IPD_NO").toString().length() != 0) {
            this.setValue("IPD_NO",
                          this.getSelectParm("IPD_NO",
                                             this.getValue("IPD_NO").toString()));
        }
        callFunction("UI|" + TABLE1 + "|onQuery");
        out("��ѯend");
    }
    /**
     * ����
     */
    public void onSave() {
        out("����begin");
        //�ж��Ƿ��Ѿ�����
        TParm  parmRow = this.getSelectRowData(TABLE1);
        TParm parm = InsOrderTool.getInstance().checkInsStatus(parmRow);
        if(parm.getInt("INS_STATUS",0)==3){
            this.messageBox_("�Ѿ����㲻��������ˣ�");
            return;
        }
        parm = InsOrderTool.getInstance().checkIbsStatus(parmRow);
        if(parm.getCount("DISCHARGE_FLG")<=0){
            this.messageBox("�˲���û�в�����Ժ�˵���");
            return;
        }
        TParm result = (TParm)this.openDialog("%ROOT%\\config\\ins\\INSORDERIBSDATE.x",this.getSelectRowData(TABLE1));
        String start = result.getValue("START");
        if(start.equals("Y")){
            this.selTable2Data();
        }
        out("����end");
    }
    /**
     * ȫ����ת
     */
    public void onRenewedly(){
        out("ȫ����תbegin");
        //�ж��Ƿ��Ѿ�����
        TParm  parmRow = this.getSelectRowData(TABLE1);
        TParm parm = InsOrderTool.getInstance().checkInsStatus(parmRow);
        if(parm.getInt("INS_STATUS",0)==3){
            this.messageBox_("�Ѿ����㲻��������ˣ�");
            return;
        }
        parm = InsOrderTool.getInstance().checkIbsStatus(parmRow);
        if (parm.getCount("DISCHARGE_FLG") <= 0) {
            this.messageBox("�˲���û�в�����Ժ�˵���");
            return;
        }
        if (messageBox("��ʾ��Ϣ", "�Ƿ񽫴˲��˵�ȫ��ҽ��������Ϣ���·ָ�?", this.YES_NO_OPTION) != 0)
                return;
        this.seleInfoBase(this.getSelectRowData(TABLE1));
        out("ȫ����תend");
    }
    /**
     *���
     */
    public void onClear() {
        out("���begin");
        callFunction("UI|" + TABLE1 + "|removeRowAll");
        callFunction("UI|" + TABLE2 + "|removeRowAll");
        this.clearValue("NHI_COMPANY;CTZ_CODE;PAY_KIND_CODE;IN_DEPT;BED_NO;MR_NO;IPD_NO;PAT_NAME;PAT_AGE;IDNO;ADM_DATE;DS_DATE;ALL_PRICE;RESV_NO;COMPANY_DESC");
        callFunction("UI|" + TABLE1 + "|clearSelection");
        callFunction("UI|" + TABLE2 + "|clearSelection");
        callFunction("UI|save|setEnabled", false);
        callFunction("UI|renewedly|setEnabled", false);
        callFunction("UI|cancelinfo|setEnabled", false);
        callFunction("UI|onsaveinfo|setEnabled", true);
        callFunction("UI|NHI_COMPANY|setEnabled", true);
        callFunction("UI|CTZ_CODE|setEnabled", true);
        callFunction("UI|PAY_KIND_CODE|setEnabled", true);
        callFunction("UI|IN_DEPT|setEnabled", true);
        callFunction("UI|BED_NO|setEnabled", true);
        callFunction("UI|MR_NO|setEnabled", true);
        callFunction("UI|IPD_NO|setEnabled", true);
        callFunction("UI|PAT_NAME|setEnabled", true);
        callFunction("UI|PAT_AGE|setEnabled", true);
        callFunction("UI|IDNO|setEnabled", true);
        callFunction("UI|ADM_DATE|setEnabled", true);
        callFunction("UI|DS_DATE|setEnabled", true);
        callFunction("UI|ALL_PRICE|setEnabled", true);
        callFunction("UI|RESV_NO|setEnabled", true);
        callFunction("UI|RESV_NO|setEnabled", true);
        callFunction("UI|COMPANY_DESC|setEnabled", true);
        this.action="INSERT";
        out("���end");
    }
    /**
     * ��ò�ѯ����
     * @param type String
     * @param value String
     * @return String
     */
    public String getSelectParm(String type, String value) {
        String result = "";
        if (type.equals("IPD_NO")) {
            String zro = "";
            int count = this.ipdInt - value.length();
            for (int i = 0; i < count; i++) {
                zro += "0";
            }
            result = zro + value;
        } else if (type.equals("MR_NO")) {
            String zro = "";
            int count = this.mrInt - value.length();
            for (int i = 0; i < count; i++) {
                zro += "0";
            }
            result = zro + value;
        }
        return result;
    }
    /**
     * ���ͨ��
     */
    public void onSaveInfo(){
        TParm parm = this.getSelectRowData(TABLE1);
        TParm ibsParm = InsOrderTool.getInstance().checkIbsStatus(parm);
        if (ibsParm.getCount("DISCHARGE_FLG") <= 0) {
            this.messageBox("�˲���û�в�����Ժ�˵���");
            return;
        }
        parm.setData("INS_STATUS","2");
        parm.setData("SH_USER",Operator.getID());
        TParm result = InsOrderTool.getInstance().onSaveInfo(parm);
        if(result.getErrCode()<0){
            this.messageBox_("����ʧ��!");
        }else{
            this.messageBox_("����ɹ�!");
            callFunction("UI|cancelinfo|setEnabled", true);
            callFunction("UI|onsaveinfo|setEnabled", false);
        }
        //System.out.println("�������"+parm);
    }
    /**
     * ȡ�����
     */
    public void onCancelInfo(){
        TParm parm = this.getSelectRowData(TABLE1);
        parm.setData("INS_STATUS","1");
        parm.setData("SH_USER",Operator.getID());
        TParm result = InsOrderTool.getInstance().onSaveInfo(parm);
        if(result.getErrCode()<0){
            this.messageBox_("����ʧ��!");
        }else{
            this.messageBox_("����ɹ�!");
            callFunction("UI|cancelinfo|setEnabled", false);
            callFunction("UI|onsaveinfo|setEnabled", true);
        }
        //System.out.println("�������"+parm);
    }
    /**
     * ���
     */
    public void onReSaveInfo(){
        TParm parm = this.getSelectRowData(TABLE1);
        //System.out.println("������˵�����"+parm);
        TParm ibsParm = InsOrderTool.getInstance().checkIbsStatus(parm);
        if (ibsParm.getCount("DISCHARGE_FLG") <= 0) {
            this.messageBox("�˲���û�в�����Ժ�˵���");
            return;
        }
        TParm result = (TParm)this.openDialog("%ROOT%\\config\\ins\\INSORDERCheckUI.x",parm);
        String start = result.getValue("START");
        //System.out.println("����״̬"+start);
        if(start.equals("Y")){
            this.selTable2Data();
        }
    }
    /**
     * ��ӡ
     */
    public void onPrint(){
        //System.out.println("��ӡ");
        TParm parm = this.getSelectRowData(TABLE1);
        //System.out.println("TABLE����"+parm);
        TParm result = InsOrderTool.getInstance().queryPrintData(parm);
        if(result.getErrCode()<0){
            return;
        }
        if(result.getCount()<=0){
            return;
        }
        //System.out.println("��ӡ��������"+result);
        result.setData("MR_NO",parm.getData("MR_NO"));
        result.setData("PAT_NAME",parm.getData("PAT_NAME"));
        result.setData("ADM_DATE",parm.getData("ADM_DATE"));
        TParm printData = this.printData(result);
        //System.out.println("��ӡ����"+printData);
        TParm resultPrint = (TParm)this.openDialog("%ROOT%\\config\\print\\toolui\\toolPrintUI.x",printData);
    }
    /**
     * ��ӡ����
     * @param parm TParm
     */
    public TParm printData(TParm parm){
        int rowCount = parm.getCount("FEE_TYPE");
        TParm printData = new TParm();
        //System.out.println("����"+rowCount);
        printData.setData("FILENAME","%ROOT%\\config\\datawindow\\INS_LISTRPT.xml");
        DecimalFormat df = new DecimalFormat("#############0.00");
        //�ܽ��
        double totalAmt = 0.0;
        //����׼��λ��
        double bedPrice=0.0;
        String nhiTitleName = "";
        //��ͷ��(�����ţ���������Ժ����)
        printData.setData("MR_NO", parm.getData("MR_NO"));
        printData.setData("PAT_NAME", parm.getData("PAT_NAME"));
        printData.setData("BILL_DATE", parm.getData("ADM_DATE").toString().substring(0,10));
        for(int i=0;i<rowCount;i++){
            //System.out.println("��ǰ������"+parm.getRow(i));
            if("Y".equals(parm.getData("MEDICAREBEDPRICE_FLG",i).toString())){
                //System.out.println("1111");
                double onlyBedPrice = parm.getDouble("PRICE",i)>=parm.getDouble("LIMIT_PRICE",i)?(parm.getDouble("PRICE",i)-parm.getDouble("LIMIT_PRICE",i))*parm.getDouble("QTY",i):0;
                //System.out.println("����׼��λ��"+onlyBedPrice+"����"+parm.getDouble("PRICE",i)+"����޼�"+parm.getDouble("LIMIT_PRICE",i));
                bedPrice+=onlyBedPrice;
                nhiTitleName=parm.getData("TYPE_NAME",i).toString();
                //System.out.println("����׼��λ��"+bedPrice);
                continue;
            }
            if(i==0){
               //������(������---1:����0:��ʾ)
               printData.addData("FALG1",1);
               printData.addData("FALG2", 1);
               printData.addData("FALG3", 1);
               printData.addData("FALG4", 1);
               printData.addData("FALG5", 1);
               printData.addData("FALG6", 1);
               printData.addData("ORDER_CAT1",parm.getData("TYPE_NAME",i));
               printData.addData("ORDER_DESC","");
               printData.addData("UNIT_DESC","");
               printData.addData("PRICE","");
               printData.addData("TOT","");
               printData.addData("TOT_AMT","");
               printData.addData("TYPE","");
               //�ڶ���
               printData.addData("FALG1",0);
               printData.addData("FALG2", 0);
               printData.addData("FALG3", 0);
               printData.addData("FALG4", 0);
               printData.addData("FALG5", 0);
               printData.addData("FALG6", 0);
               printData.addData("ORDER_CAT1",parm.getData("CHARGE_HOSP_DESC",i));
               printData.addData("ORDER_DESC",parm.getData("ORDER_DESC",i));
               printData.addData("UNIT_DESC",parm.getData("UNIT_DESC",i));
               printData.addData("PRICE",parm.getData("PRICE",i));
               printData.addData("TOT",parm.getData("QTY",i));
               printData.addData("TOT_AMT",parm.getData("TOTAL_AMT",i));
               printData.addData("TYPE",parm.getData("PRINT_NAME",i));
               totalAmt+=parm.getDouble("TOTAL_AMT",i);
            }else{
                if (!parm.getData("TYPE_CODE", i).toString().equals(parm.getData("TYPE_CODE", i - 1).toString())){
                    //�ϼ�
                    printData.addData("FALG1", 1);
                    printData.addData("FALG2", 1);
                    printData.addData("FALG3", 1);
                    printData.addData("FALG4", 1);
                    printData.addData("FALG5", 1);
                    printData.addData("FALG6", 1);
                    printData.addData("ORDER_CAT1","");
                    printData.addData("ORDER_DESC","");
                    printData.addData("UNIT_DESC","�ϼ�:");
                    printData.addData("PRICE", "");
                    printData.addData("TOT","");
                    printData.addData("TOT_AMT",df.format(StringTool.round(totalAmt,2)));
                    printData.addData("TYPE","");
                    totalAmt=0.0;
                    //������ǩ��
                    printData.addData("FALG1", 1);
                    printData.addData("FALG2", 1);
                    printData.addData("FALG3", 1);
                    printData.addData("FALG4", 1);
                    printData.addData("FALG5", 1);
                    printData.addData("FALG6", 1);
                    printData.addData("ORDER_CAT1","������ǩ��");
                    printData.addData("ORDER_DESC","");
                    printData.addData("UNIT_DESC","");
                    printData.addData("PRICE", "");
                    printData.addData("TOT","");
                    printData.addData("TOT_AMT","");
                    printData.addData("TYPE","");
                    //ͷ����
                    printData.addData("FALG1", 1);
                    printData.addData("FALG2", 1);
                    printData.addData("FALG3", 1);
                    printData.addData("FALG4", 1);
                    printData.addData("FALG5", 1);
                    printData.addData("FALG6", 1);
                    printData.addData("ORDER_CAT1", parm.getData("TYPE_NAME", i));
                    printData.addData("ORDER_DESC", "");
                    printData.addData("UNIT_DESC", "");
                    printData.addData("PRICE", "");
                    printData.addData("TOT", "");
                    printData.addData("TOT_AMT", "");
                    printData.addData("TYPE", "");
                    //��һ��
                    printData.addData("FALG1", 0);
                    printData.addData("FALG2", 0);
                    printData.addData("FALG3", 0);
                    printData.addData("FALG4", 0);
                    printData.addData("FALG5", 0);
                    printData.addData("FALG6", 0);
                    printData.addData("ORDER_CAT1",parm.getData("CHARGE_HOSP_DESC", i));
                    printData.addData("ORDER_DESC",parm.getData("ORDER_DESC", i));
                    printData.addData("UNIT_DESC", parm.getData("UNIT_DESC", i));
                    printData.addData("PRICE", parm.getData("PRICE", i));
                    printData.addData("TOT", parm.getData("QTY", i));
                    printData.addData("TOT_AMT", parm.getData("TOTAL_AMT", i));
                    printData.addData("TYPE", parm.getData("PRINT_NAME", i));
                    totalAmt += parm.getDouble("TOTAL_AMT", i);
                    continue;
                }
                //����
                printData.addData("FALG1", 0);
                printData.addData("FALG2", 0);
                printData.addData("FALG3", 0);
                printData.addData("FALG4", 0);
                printData.addData("FALG5", 0);
                printData.addData("FALG6", 0);
                printData.addData("ORDER_CAT1",parm.getData("CHARGE_HOSP_DESC", i));
                printData.addData("ORDER_DESC", parm.getData("ORDER_DESC", i));
                printData.addData("UNIT_DESC", parm.getData("UNIT_DESC", i));
                printData.addData("PRICE", parm.getData("PRICE", i));
                printData.addData("TOT", parm.getData("QTY", i));
                printData.addData("TOT_AMT", parm.getData("TOTAL_AMT", i));
                printData.addData("TYPE", parm.getData("PRINT_NAME", i));
                totalAmt += parm.getDouble("TOTAL_AMT", i);
            }
        }
        //�ϼ�
        printData.addData("FALG1", 1);
        printData.addData("FALG2", 1);
        printData.addData("FALG3", 1);
        printData.addData("FALG4", 1);
        printData.addData("FALG5", 1);
        printData.addData("FALG6", 1);
        printData.addData("ORDER_CAT1", "");
        printData.addData("ORDER_DESC", "");
        printData.addData("UNIT_DESC", "�ϼ�:");
        printData.addData("PRICE", "");
        printData.addData("TOT", "");
        printData.addData("TOT_AMT", df.format(StringTool.round(totalAmt, 2)));
        printData.addData("TYPE", "");
        totalAmt = 0.0;
        //������ǩ��
        printData.addData("FALG1", 1);
        printData.addData("FALG2", 1);
        printData.addData("FALG3", 1);
        printData.addData("FALG4", 1);
        printData.addData("FALG5", 1);
        printData.addData("FALG6", 1);
        printData.addData("ORDER_CAT1", "������ǩ��");
        printData.addData("ORDER_DESC", "");
        printData.addData("UNIT_DESC", "");
        printData.addData("PRICE", "");
        printData.addData("TOT", "");
        printData.addData("TOT_AMT", "");
        printData.addData("TYPE", "");
        //����׼��λ�ѱ���
        printData.addData("FALG1",1);
        printData.addData("FALG2", 1);
        printData.addData("FALG3", 1);
        printData.addData("FALG4", 1);
        printData.addData("FALG5", 1);
        printData.addData("FALG6", 1);
        printData.addData("ORDER_CAT1",nhiTitleName);
        printData.addData("ORDER_DESC", "");
        printData.addData("UNIT_DESC", "");
        printData.addData("PRICE", "");
        printData.addData("TOT", "");
        printData.addData("TOT_AMT", "��"+df.format(StringTool.round(bedPrice,2)));
        printData.addData("TYPE", "");
        printData.setData("ACTION","COUNT",printData.getCount("ORDER_CAT1"));
        //System.out.println("��ӡ����"+printData);
        //������ǩ��
        printData.addData("FALG1", 1);
        printData.addData("FALG2", 1);
        printData.addData("FALG3", 1);
        printData.addData("FALG4", 1);
        printData.addData("FALG5", 1);
        printData.addData("FALG6", 1);
        printData.addData("ORDER_CAT1", "������ǩ��");
        printData.addData("ORDER_DESC", "");
        printData.addData("UNIT_DESC", "");
        printData.addData("PRICE", "");
        printData.addData("TOT", "");
        printData.addData("TOT_AMT", "");
        printData.addData("TYPE", "");
        return printData;
    }
}
