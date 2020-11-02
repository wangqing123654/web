package com.javahis.ui.reg;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.reg.PatAdmTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TCheckBox;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TTableEvent;
import jdo.opd.OrderTool;
import jdo.bil.BILInvrcptTool;
import jdo.ekt.EKTIO;
import jdo.bil.BILREGRecpTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TButton;
import jdo.sys.SystemTool;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: �Һ��հ���ѯ�Һ���Ϣ����</p>
 *
 * <p>Description: �Һ��հ���ѯ�Һ���Ϣ����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-04-14
 * =====================pangben modify 20110602 �޸�
 * @version 1.0
 */
public class REGSchDayRegListControl extends TControl {
    public void onInit() {
        super.onInit();
        //�˵�tableר�õļ���
        getTable("Table").addEventListener(TTableEvent.
                                           CHECK_BOX_CLICKED, this,
                                           "onTableComponent");
        pageInit();
        tableAllParm = new TParm();
    }

    //======pangben modify 20110602 ѡ�е���
    private TParm tableAllParm;
    private boolean LEADER = true; //Ȩ��
    private boolean UnRegMessage=true;//�˹Ҳ�����Ϣ����ʾ
    private String allMrNo="";//�ۼ��˹�ʧ�ܲ���NO
    private TParm obj=null;//����
    public void pageInit() {
        obj = (TParm)this.getParameter();
        this.setValue("ADM_DATE",
                      StringTool.getTimestamp(obj.getValue("ADM_DATE"),
                                              "yyyyMMdd"));
        this.setValue("REGION_CODE", obj.getValue("REGION_CODE"));
        this.setValue("ADM_TYPE", obj.getValue("ADM_TYPE"));
        this.setValue("SESSION_CODE", obj.getValue("SESSION_CODE"));
        this.setValue("CLINICROOM_NO", obj.getValue("CLINICROOM_NO"));
        this.setValue("DR_CODE", obj.getValue("DR_CODE"));
        LEADER = obj.getBoolean("LEADER");
        onQuery();
    }
    /**
     * ��ѯ
     */
    public void onQuery() {

        TParm result = PatAdmTool.getInstance().getSchDayRegInfo(obj);
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
        table.setParmValue(result);

    }
    /**
     * �˹�ȫѡ��ѡ��ִ�з���
     * ================pangben modify 20110602
     */
    public void onCheckSelectAll() {
        TTable table = this.getTable("Table");
        table.acceptText();
        if (table.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }

        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.getParmValue().setData("REGCAN_FLG", i, "Y");
            }
            tableAllParm = table.getParmValue();
        } else {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.getParmValue().setData("REGCAN_FLG", i, "N");
            }
            tableAllParm = new TParm();
        }
        table.setParmValue(table.getParmValue());
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�CheckBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * table����checkBox�ı�
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        TParm tableParm = table.getParmValue();
        tableAllParm = new TParm();
        int count = tableParm.getCount("CASE_NO");
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("REGCAN_FLG", i)) {
                tableAllParm.addData("REGION_CODE",
                                     tableParm.getValue("REGION_CODE", i));
                tableAllParm.addData("QUE_NO",
                                     tableParm.getValue("QUE_NO", i));
                tableAllParm.addData("CASE_NO",
                                     tableParm.getValue("CASE_NO", i));
                tableAllParm.addData("MR_NO",tableParm.getValue("MR_NO", i));
            }
        }
        return true;
    }


    /**
     * ��������˹Ҳ���
     * ���ڵ��߼��������˹�ʱ��û�зŵ�һ�������У�����ÿһ���˹���Աһ����������ִ�����˹���Ա�ۼ���ʾ��Ϣ���˹ҳɹ�����Ա����ʾ�����
     */
    public void onUnReg() {
        if (!LEADER) {
            this.messageBox("���鳤�����˹�!");
            return;
        }
        if(tableAllParm.getCount("CASE_NO")<0){
            this.messageBox("��ѡ��Ҫ�˹ҵĲ���");
            return;
        }
        for (int i = 0; i < tableAllParm.getCount("CASE_NO"); i++) {
            TParm parm = tableAllParm.getRow(i);
            oneUnReg(parm);
        }
        if (UnRegMessage) {
            this.messageBox("�˹ҳɹ�!");
        } else{//��ʾ�˹�ʧ�ܲ�������
            this.messageBox(allMrNo.substring(0, allMrNo.length()-1) +
                            " ����,�˹�ʧ��!");
        }
         onQuery();
    }

    /**
     * ���������˹Ҳ���
     * @param parm TParm
     * ======pangben modify 20110603
     */
    public void oneUnReg(TParm parm) {

        String optUser = Operator.getID();
        String optTerm = Operator.getIP();
        TParm unRegParm = new TParm();
        TParm patFeeParm = new TParm();
        String case_no = parm.getValue("CASE_NO");
        patFeeParm.setData("CASE_NO", case_no);
        patFeeParm.setData("REGCAN_USER", optUser);
        //��ѯ��ǰ�����Ƿ��������
        TParm selPatFeeForREG = OrderTool.getInstance().selPatFeeForREG(
                patFeeParm);
        TParm unRegRecpParm = BILREGRecpTool.getInstance().selDataForUnReg(
                case_no);
        String recpNo = unRegRecpParm.getValue("RECEIPT_NO", 0);
        TParm inInvRcpParm = new TParm();
        inInvRcpParm.setData("RECEIPT_NO", recpNo);
        inInvRcpParm.setData("CANCEL_FLG", 0);//======pangben 2012-3-23
        inInvRcpParm.setData("RECP_TYPE", "REG");//======pangben 2012-3-23
        TParm unInvRcpParm = BILInvrcptTool.getInstance().selectAllData(
                inInvRcpParm);
        unRegParm.setData("CASE_NO", case_no);
        unRegParm.setData("REGCAN_USER", optUser);
        unRegParm.setData("OPT_USER", optUser);
        unRegParm.setData("OPT_TERM", optTerm);
        unRegParm.setData("RECP_PARM", unRegRecpParm.getData());
        unRegParm.setData("INV_NO", unInvRcpParm.getData("INV_NO", 0));
        if (selPatFeeForREG.getDouble("AR_AMT", 0) == 0) {
            TParm result = new TParm();
            //�ֽ��˹Ҷ���
            result = TIOM_AppServer.executeAction("action.reg.REGAction",
                                                  "onUnReg", unRegParm);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                allMrNo+=parm.getValue("MR_NO")+"��";;
                UnRegMessage=false;
                return;
            }
        } else {
            this.messageBox(parm.getValue("MR_NO") + "�����Ѳ�������,�����˹�!");
            allMrNo+=parm.getValue("MR_NO")+"��";;
            return;
        }
    }
    /**
     * ��ӡ����
     * ===========pangben modify 20110607
     */
    public void onPrint() {
        TTable table = this.getTable("Table");
        TParm parm = table.getParmValue();
        if (parm.getCount() < 0) {
            this.messageBox("û����Ҫ��ӡ������");
            return;
        }
         TParm result=new TParm();
         TParm tableParm=new TParm();
         for(int i=0;i<parm.getCount();i++){
             tableParm.addData("QUE_NO", parm.getValue("QUE_NO", i));
             tableParm.addData("MR_NO", parm.getValue("MR_NO", i));
             tableParm.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
             tableParm.addData("SEX_CODE",
                               parm.getValue("SEX_CODE", i).equals("1") ? "��" :
                               "Ů");
             tableParm.addData("TEL_HOME", parm.getValue("TEL_HOME", i));
         }
         tableParm.setCount(parm.getCount());
        tableParm.addData("SYSTEM", "COLUMNS", "QUE_NO");
        tableParm.addData("SYSTEM", "COLUMNS", "MR_NO");
        tableParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        tableParm.addData("SYSTEM", "COLUMNS", "SEX_CODE");
        tableParm.addData("SYSTEM", "COLUMNS", "TEL_HOME");
        result.setData("T1",tableParm.getData());

        //�Һ�ʱ��
        result.setData("DATE", "TEXT",
                       this.getValueString("ADM_DATE").
                       substring(0, this.getValueString("ADM_DATE").indexOf(" ")).replace("-", "/"));
        result.setData("ADM_TYPE", "TEXT",
                       this.getValue("ADM_TYPE").equals("O") ? "����" : "����");
        result.setData("REG_SESSION", "TEXT", parm.getValue("SESSION_DESC", 0));
        result.setData("CLINICROOM_NO", "TEXT",
                       ((TTextFormat)this.getComponent("CLINICROOM_NO")).
                       getText());
        result.setData("DR_CODE", "TEXT",
                       ((TTextFormat)this.getComponent("DR_CODE")).getText());
        result.setData("userName", "TEXT", Operator.getName());
        result.setData("userDate", "TEXT",
                       StringTool.getString(SystemTool.getInstance().getDate(),
                                            "yyyy/MM/dd"));
        result.setData("TITLE1", "TEXT",
                       Operator.getHospitalCHNFullName());
        result.setData("TITLE2", "TEXT","����ҽ���Һ���Ա��Ϣ");
        this.openPrintWindow("%ROOT%\\config\\prt\\REG\\REGSchDayRegList.jhw",
                             result);
    }
   /**
    * ����EXECL
    */
   public void onExecl() {
       TTable table = (TTable) callFunction("UI|Table|getThis");
       ExportExcelUtil.getInstance().exportExcel(table,
                                                 "����ҽ���Һ���Ա��Ϣ");
   }
}
