package com.javahis.ui.bil;

import com.dongyang.control.*;
import jdo.bil.BILInvoiceTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TCM_Transform;

/**
 * <p>Title:Ʊ������ </p>
 *
 * <p>Description:Ʊ������ </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author TParm
 * @version 1.0
 */
public class BILRecipientsControl
    extends TControl {
    public void onInit() {
        super.onInit();
        String recptype = (String)this.getParameter();
        this.callFunction("UI|RECP_TYPE|setValue", recptype);
        this.callFunction("UI|RECP_TYPE|setEnabled", false);
        this.callFunction("UI|START_VALID_DATE|setValue",
                          SystemTool.getInstance().getDate());
    }

    /**
     * ���ñ��淽��
     */
    public void onSave() {
        if (!this.emptyTextCheck("START_INVNO,END_INVNO"))
            return;
        if (!this.checkouts())
            return;
        TParm parm = getParmForTag(
            "START_INVNO;END_INVNO;START_VALID_DATE;RECP_TYPE");
//      String value=TCM_Transform.getTimestamp(getValue(
//            "START_VALID_DATE"));
        parm.setData("START_VALID_DATE", TCM_Transform.getTimestamp(getValue(
            "START_VALID_DATE")));
        parm.setData("UPDATE_NO", getValueString("START_INVNO"));
        parm.setData("CASHIER_CODE", this.getValue("CASHIER_CODE"));
        parm.setData("STATUS", "1");
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("TERM_IP", Operator.getIP());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("REGION_CODE", Operator.getRegion().length()<=0?"":Operator.getRegion());
//      System.out.println("parm"+parm); 
        TParm result = BILInvoiceTool.getInstance().insertData(parm);
        if (result.getErrCode() < 0) {
            this.messageBox("E0005"); //ִ��ʧ��
            return;
        }
        this.messageBox("P0005"); //ִ�гɹ�
        this.callFunction("UI|onClose");
    }

    /**
     * ���Ʊ���Ƿ��ù�
     * @return String
     */
    public String checkout() {
        TParm parm = getParmForTag("START_INVNO;END_INVNO;RECP_TYPE");
//      TParm data=InvoiceTool.getInstance().checkData(this.getValueString("RECP_TYPE"));
//      String result = data.getValue("START_INVNO");
        return "";
    }

    /**
     * ��˽����ϵ�������ȷ��
     * @return boolean
     */
    public boolean checkouts() {
        if (getValueString("START_INVNO").length() !=
            getValueString("END_INVNO").length()) {
            this.messageBox("����Ʊ�ų��Ȳ����");
            return false;
        }
        if (getValueString("START_INVNO").compareTo(getValueString("END_INVNO")) >
            0) {
            this.messageBox("��ʼƱ�Ų��ܴ��ڽ���Ʊ��");
            return false;
        }
        if(this.getValueString("CASHIER_CODE").length()<=0){
            this.messageBox_("��ѡ���շ���Ա");
            return false;
        }
        return true;
    }

    public static String addString(String s) {
        String value = "";
        boolean flt = true;
        for (int i = s.length() - 1; i >= 0; i--) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') {
                value = c + value;
                continue;
            }
            if (flt) {
                c++;
                if (c > '9')
                    c = '0';
                else
                    flt = false;
            }
            value = c + value;
        }
        return value;
    }

    public static void main(String args[]) {
//      System.out.println(addString("A001-ABC"));
    }

}
