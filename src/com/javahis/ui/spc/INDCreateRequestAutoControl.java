package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p>Title: �����������쵥</p>
 *
 * <p>Description: �����������쵥</p>
 *
 * <p>Copyright: (c) 2013</p>
 *
 * <p>Company: </p>
 *
 * @author liyh
 * @version 1.0
 */
public class INDCreateRequestAutoControl
    extends TControl {
    public INDCreateRequestAutoControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ����ѯ����
        Timestamp date = SystemTool.getInstance().getDate();
        date = StringTool.rollDate(date, -1);
        this.setValue("CLOSE_DATE", date.toString().substring(0, 7).replace('-', '/'));
    }

    /**
     * �����������쵥
     */
    public void onSave() {
      /*  String org_code = this.getValueString("ORG_CODE");
        if ("".equals(org_code)) {
            this.messageBox("��ѡ�����ҩ��");
            return;
        }*/  
        String date = this.getValueString("DATE");
        //��ѯ����ս���� ����
        Timestamp datetime = StringTool.getTimestamp(date, "yyyyMMdd");
        String optUser = Operator.getID();
        String optTerm = Operator.getIP();
        String regionCode = Operator.getRegion();
        TParm parm = new TParm();  
        parm.setData("OPT_USER",optUser);
        parm.setData("OPT_TERM",optTerm);
        parm.setData("REGION_CODE",regionCode);
        TParm result = TIOM_AppServer.executeAction(
				"action.spc.INDCreateRequestAutoAction", "createIndRequsestAuto", parm);
        if (result.getErrCode() < 0) {
        	this.messageBox("ִ��ʧ�ܣ�");
        }
        else {
        	this.messageBox("ִ�гɹ���");
        }
   /*     TParm result = INDTool.getInstance().createIndRequsestAuto("", optUser, optTerm, regionCode);
        if (result.getErrCode() < 0) {
        	this.messageBox("ִ��ʧ�ܣ�");
        }
        else {
        	this.messageBox("ִ�гɹ���");
        }*/


    }
    
    /**
     * ���Է�ʽ
     * @param args String[]
     */
    public static void main(String[] args) {
        INDCreateRequestAutoControl ind = new INDCreateRequestAutoControl();
    }
}
