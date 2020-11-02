package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import action.clp.CLPManagemAction;
import jdo.sys.SystemTool;

import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.system.textFormat.TextFormatCLPDuration;

import jdo.sys.Operator;

import java.awt.Component;
import java.sql.Timestamp;

/**
 * <p>Title: �����ٴ�·����� </p>
 *
 * <p>Description: �����ٴ�·�����</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPManagemChangeControl extends TControl {
    public CLPManagemChangeControl() {

    }
    TTable table;
    TextFormatCLPDuration combo_schd;
    /**
     * ҳ���ʼ������
     */
    public void onInit() {
        super.onInit();
        initPage();
    }

    TParm sendParm;
    TParm oldClnParm;
    String sameSchdCode="";
    /**
     * ��ʼ��ҳ��
     */
    private void initPage() {
        sendParm = (TParm)this.getParameter();
        String oldCLNPathCode = (String) sendParm.getData("OLD_CLNCPATH_CODE");
        //SYSTEM.OUT.PRINTln("ԭ�ٴ�·������:" + oldCLNPathCode);
        String newCLNPathCode = (String) sendParm.getData("NEW_CLNCPATH_CODE");
        String changeReason = (String) sendParm.getData("CHANGE_REASON");
        this.setValue("OLD_CLNCPATH_CODE", oldCLNPathCode);
        table= (TTable) this.getComponent("TABLE");
        oldClnParm=sendParm.getParm("oldClnParm");
        this.setValue("CHANGE_REASON", changeReason);
        this.setValue("CLNCPATH_CODE", newCLNPathCode);
//        addEventListener(table + "->" + TTableEvent.CHANGE_VALUE, this,
//        "onChangeTableValue");
        onClncpathCode();
      //��ѯ�˲����Ƿ��Ѿ�����ʱ�̵�ҽ�����ã����Ѿ������ҽ������·����ʱ�̴���
        TParm tableParm=new TParm();
        for (int i = 0; i < oldClnParm.getCount(); i++) {
        	if(null!=oldClnParm.getValue("FLG",i) && 
        			oldClnParm.getValue("FLG",i).equals("N")){
        		tableParm.addData("CLNCPATH_CODE", oldClnParm.getValue("OLD_CLNCPATH_CODE",i));
        		tableParm.addData("SCHD_CODE", oldClnParm.getValue("OLD_SCHD_CODE",i));
        		tableParm.addData("NEW_CLNCPATH_CODE", newCLNPathCode);
        		tableParm.addData("NEW_SCHD_CODE", "");
        	}else{
        		if(oldClnParm.getValue("OLD_SCHD_CODE",i).toString().length()>0){
        			sameSchdCode+="'"+oldClnParm.getValue("OLD_SCHD_CODE",i).toString()+"',";
        		}
        	}
        }
        tableParm.setCount(tableParm.getCount("SCHD_CODE"));
        if (tableParm.getCount()>0) {
        	table.setParmValue(tableParm);
		}
    }

    /**
     * ��շ���
     */
    public void onClear() {
//        CLNCPATH_CODE
        //this.setValue("CLNCPATH_CODE", "");
        //this.setValue("CHANGE_REASON", "");
        TParm parm = table.getParmValue();
		if (parm.getCount() > 0) {
			for (int i = 0; i < parm.getCount(); i++) {
				//parm.setData("NEW_CLNCPATH_CODE", i, "");
				parm.setData("NEW_SCHD_CODE", i, "");
			}
			table.setParmValue(parm);
		}
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (!validData()) {
            return;
        }
        //SYSTEM.OUT.PRINTln("���յĲ���:"+sendParm);
        TParm parmForInsert = new TParm(sendParm.getData());
        parmForInsert.setData("CLNCPATH_CODE", this.getValueString("CLNCPATH_CODE"));
        parmForInsert.setData("CHANGE_REASON", this.getValueString("CHANGE_REASON"));
        parmForInsert.setData("IN_DATE",this.getCurrentDateStr());
        parmForInsert.setData("SCHD_CODE",this.getValue("SCHD_CODE_SUM"));//��ǰʱ��
        //�¼���·�����Զ������·��
        parmForInsert.setData("START_DTTM",getCurrentDateStr("yyyyMMddHHmmss"));
        String sql=" SELECT EVL_CODE FROM CLP_EVL_STANDM WHERE CLNCPATH_CODE='"+this.getValueString("CLNCPATH_CODE")+"'";
        TParm newClnParm = new TParm(TJDODBTool.getInstance().select(
                sql.toString()));
        if (newClnParm.getCount()>0) {
        	parmForInsert.setData("EVL_CODE",newClnParm.getValue("EVL_CODE",0));
		}
        //���������Ϣ
        putBasicSysInfoIntoParm(parmForInsert);
        TParm parmForUpadate = new TParm();
        parmForUpadate.setData("CASE_NO",parmForInsert.getValue("CASE_NO"));
        parmForUpadate.setData("CLNCPATH_CODE",parmForInsert.getValue("OLD_CLNCPATH_CODE"));
        parmForUpadate.setData("CHANGE_REASON",parmForInsert.getValue("CHANGE_REASON"));
        TParm actionParm = new TParm();
        actionParm.setData("parmForInsert",parmForInsert.getData());
        actionParm.setData("parmForUpadate",parmForUpadate.getData());
        if (null!=sameSchdCode && sameSchdCode.length()>0) {
        	sameSchdCode=sameSchdCode.substring(0,sameSchdCode.lastIndexOf(","));
        	actionParm.setData("sameSchdCode",sameSchdCode);
		}
        if (null!=table && null!=table.getParmValue()) {
        	 TParm billParm =table.getParmValue();
        	 if (billParm.getCount()>0) {
             	actionParm.setData("billParm",billParm.getData());
     		}
		}
        //SYSTEM.OUT.PRINTln("��ʼִ���ٴ�·��������淽��");
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPManagemAction",
                "changeCLPManagem", actionParm);
        if(result.getErrCode()<0){
            this.messageBox("���ʧ��");
             this.setReturnValue("FAILURE");
        }else{
            this.messageBox("����ɹ�");
            this.setReturnValue("SUCCESS");
            this.closeWindow();
        }

    }

    /**
     * ������֤����
     * @return boolean
     */
    private boolean validData() {
        if (!checkNullAndEmpty(this.getValueString("CLNCPATH_CODE"))) {
            this.messageBox("��ѡ����·��");
            return false;
        }
        if (!checkNullAndEmpty(this.getValueString("CHANGE_REASON"))) {
            this.messageBox("��������ԭ��");
            return false;
        }
        if (null!=table && null!=table.getParmValue() && table.getParmValue().getCount()>0) {
        	for (int i = 0; i < table.getParmValue().getCount(); i++) {
    			if (null==table.getParmValue().getValue("NEW_SCHD_CODE",i)||table.getParmValue().getValue("NEW_SCHD_CODE",i).length()<=0) {
    				this.messageBox("��·��ʱ�̲�����Ϊ��ֵ");
    				return false;
    			}
    		}
		}
        return true;
    }

    /**
     * ����Ƿ�Ϊ�ջ�մ�
     * @return boolean
     */
    private boolean checkNullAndEmpty(String checkstr) {
        if (checkstr == null) {
            return false;
        }
        if ("".equals(checkstr)) {
            return false;
        }
        return true;
    }

    /**
     * ��TParm�м���ϵͳĬ����Ϣ
     * @param parm TParm
     */
    private void putBasicSysInfoIntoParm(TParm parm) {
        int total = parm.getCount();
        //SYSTEM.OUT.PRINTln("total" + total);
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM",  Operator.getIP());
    }

    /**
     * �õ���ǰʱ���ַ�������
     * @param dataFormatStr String
     * @return String
     */
    private String getCurrentDateStr(String dataFormatStr) {
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, dataFormatStr);
        return datestr;
    }

    /**
     * �õ���ǰʱ���ַ�������
     * @return String
     */
    private String getCurrentDateStr() {
        return getCurrentDateStr("yyyyMMdd");
    }

	public void onSelect() {
		int row=table.getSelectedRow();
		TParm parm=table.getParmValue();
		if (row<0) {
			return;
		}
		String schdCode=this.getValueString("SCHD_CODE_SUM");
		if (null != schdCode && schdCode.length() > 0) {
			parm.setData("NEW_SCHD_CODE", row,schdCode);
		}else{
			parm.setData("NEW_SCHD_CODE", row, "");
		}
		table.setParmValue(parm);
	}
	public void onClncpathCode(){
		TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this.getComponent("SCHD_CODE_SUM");
		combo_schd.setClncpathCode(this.getValueString("CLNCPATH_CODE"));
	    combo_schd.onQuery();
	}
}
