package com.javahis.ui.bil;

import jdo.bil.BILInvoiceTool;

import com.bluecore.webx.core.utils.commons.StringUtils;
import com.dongyang.control.*;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title:����Ʊ�� </p>
 *
 * <p>Description:����Ʊ�� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author TParm
 * @version 1.0
 */
public class BILAdjustmentRecipientsControl
    extends TControl {
    public void onInit() {
        super.onInit();
        TParm recptype = (TParm)this.getParameter();
        this.callFunction("UI|START_INVNO|setValue",
                          recptype.getValue("START_INVNO"));
        this.callFunction("UI|END_INVNO|setValue",
                          recptype.getValue("END_INVNO"));
        this.callFunction("UI|UPDATE_NO|setValue",
                          recptype.getValue("UPDATE_NO"));
        this.callFunction("UI|NOWNUMBER|setValue",
                          recptype.getValue("UPDATE_NO"));
        this.callFunction("UI|START_INVNO|setEnabled", false);
        this.callFunction("UI|END_INVNO|setEnabled", false);
        this.callFunction("UI|UPDATE_NO|setEnabled", false);
    }

    /**
     * ����
     */
    public void onSave() {
        if (!this.emptyTextCheck("NOWNUMBER"))
            return;
        if (!this.checkouts()) {
            return;
        }
        TParm recptype = (TParm)this.getParameter();
        //����Ʊ�ݵ�Ʊ��
        recptype.setData("NUMBER",
                         StringTool.bitDifferOfString(recptype.
            getValue("UPDATE_NO"), getValueString("NOWNUMBER"))+1);
        recptype.setData("NOWNUMBER", StringTool.addString(this.getValueString("NOWNUMBER")));
        //System.out.println("recptype::::::  " + recptype);
        //����ACTION��
        //System.out.println("recptype::::::"+recptype);
        TParm result = TIOM_AppServer.executeAction(
            "action.bil.InvoicePersionAction", "Adjustment", recptype);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            this.messageBox("E0005"); //ִ��ʧ��
            return;
        }
        else {
            this.messageBox("P0005"); //ִ�гɹ�
        }
        this.callFunction("UI|onClose");
    }

    /**
     * ��˽����ϵ�������ȷ��
     * @return boolean
     */
    public boolean checkouts() {
        if (getValueString("NOWNUMBER").length() !=
            getValueString("UPDATE_NO").length()) {
            this.messageBox("E0016");
            return false;
        }else if (getValueString("NOWNUMBER").compareTo(getValueString("END_INVNO")) >
            0) {
            this.messageBox("E0015");
            return false;
        }else if(!getValueString("NOWNUMBER").substring(0,1).equals(getValueString("UPDATE_NO").substring(0,1))){
        	this.messageBox("����ĸ�뵱ǰƱ�Ų����");
            return false;
        }else if(getValueString("NOWNUMBER").compareTo(getValueString("UPDATE_NO")) <0){
        	this.messageBox("Ʊ��ֻ��������");
            return false;
        }
        
        //�жϷ�����Ʊ�����Ƿ�����Ѵ���Ʊ�� 2016/11/15 yanmm
        String startStr = this.getValueString("UPDATE_NO");
        String endStr = this.getValueString("NOWNUMBER");
        TParm parm = (TParm)this.getParameter();
        parm.setData("UPDATE_NO", startStr);
        parm.setData("NOWNUMBER", endStr);
        TParm parmQ = BILInvoiceTool.getInstance().getTearPrintRepNo(parm);
        if(parmQ.getErrCode()<0){
        	this.messageBox("��ѯ��������:"+parmQ.getErrText());
        	return false;
        }
        //����Ʊ�а����Ѵ���Ʊ��ʱ����ʾ�ڼ����Ƿ�Ʊ
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < parmQ.getCount(); i++){
        	//int number = StringTool.bitDifferOfString(startStr, String.valueOf(parmQ.getData("INV_NO"))) + 1;
        	sb.append((i+1)+ ",");
        }
        if(sb.toString().length()>0) {
        	this.messageBox(startStr+"��"+endStr+"�а����Ѵ���Ʊ��:��" + sb.substring(0,sb.lastIndexOf(","))+"��");
        	return false;
        }
        return true;
    }

    /**
     * �������� enter�¼�
     */
    public void onAmt() {
        TParm recptype = (TParm)this.getParameter();
        //��Ʊ����100����ʾ�Ƿ����  16/11/15 yanmm
        int number = StringTool.bitDifferOfString(recptype.
                getValue("UPDATE_NO"), getValueString("NOWNUMBER"))+1;
        if(number >= 100) {
        	int numberDe = this.messageBox("������������100�ţ��Ƿ������", "ȷ�ϼ�����",
					TControl.OK_CANCEL_OPTION);
			if (numberDe == 0) {
				
			}else{
				return;
			}
        }
        this.callFunction("UI|number|setValue",
                "NUMBER",
                StringTool.bitDifferOfString(recptype.
  getValue("UPDATE_NO"), getValueString("NOWNUMBER"))+1);
    }
    
    
}










