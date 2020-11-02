package com.javahis.ui.clp;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.system.textFormat.TextFormatCLPDuration;
import com.javahis.system.textFormat.TextFormatCLPEvlStandm;
/**
 * <p>Title: �����ٴ�·����� </p>
 *
 * <p>Description: ��ת�Ʋ�������ٴ�·����ͬʱ�޸Ľ��ױ�·�������ʱ��</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: bluecore </p>
 *
 * @author pangben 20150810
 * �������ϣ�pangben 20160922 
 * @version 1.0
 */
public class CLPManagemChangeNewControl extends TControl {
    public CLPManagemChangeNewControl() {

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
    /**
     * ��ʼ��ҳ��
     */
    private void initPage() {
        sendParm = (TParm)this.getParameter();
        String oldCLNPathCode = (String) sendParm.getData("OLD_CLNCPATH_CODE");
        String sql="SELECT CLNCPATH_CODE,EVL_CODE FROM CLP_MANAGEM WHERE CASE_NO='"+sendParm.getValue("CASE_NO")+"' AND  CLNCPATH_CODE='"+oldCLNPathCode+"'";
        TParm result=new TParm(TJDODBTool.getInstance().select(
                sql.toString()));

        this.setValue("OLD_CLNCPATH_CODE", oldCLNPathCode);
        this.setValue("OLD_EVL_CODE", result.getValue("EVL_CODE",0));
        this.setValue("CLNCPATH_CODE", "");
        this.setValue("EVL_CODE", "");
        
       	//TParm deptParm=sendParm.getParm("deptParm");//ת��������ݣ���ѯ·������
       	//���ݿ��ҡ���ݡ���ϲ�ѯ·������
       	//
        table= (TTable) this.getComponent("TABLE");
        onClncpathCode();
    }

    /**
     * ��շ���
     */
    public void onClear() {
//        CLNCPATH_CODE
        this.setValue("CLNCPATH_CODE", "");
        this.setValue("CHANGE_REASON", "");
        TParm parm = table.getParmValue();
		if (parm.getCount() > 0) {
			for (int i = 0; i < parm.getCount(); i++) {
				parm.setData("NEW_CLNCPATH_CODE", i, "");
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
        //�¼���·�����Զ������·��
        parmForInsert.setData("START_DTTM",getCurrentDateStr("yyyyMMddHHmmss"));
        parmForInsert.setData("EVL_CODE",this.getValue("EVL_CODE"));//��������
        parmForInsert.setData("SCHD_CODE",this.getValue("SCHD_CODE_SUM"));//��ǰʱ��
        //���������Ϣ
        putBasicSysInfoIntoParm(parmForInsert);
        TParm parmForUpadate = new TParm();
        parmForUpadate.setData("CASE_NO",parmForInsert.getValue("CASE_NO"));
        parmForUpadate.setData("CLNCPATH_CODE",parmForInsert.getValue("OLD_CLNCPATH_CODE"));
        parmForUpadate.setData("CHANGE_REASON",parmForInsert.getValue("CHANGE_REASON"));
        TParm actionParm = new TParm();
        actionParm.setData("parmForInsert",parmForInsert.getData());
        actionParm.setData("parmForUpadate",parmForUpadate.getData());
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
        if (!checkNullAndEmpty(this.getValueString("EVL_CODE"))) {
            this.messageBox("��ѡ����������");
            return false;
        }
        //�����·����ֻתʱ�̲���ҪУ����ԭ��
        if (!this.getValue("OLD_CLNCPATH_CODE").equals(this.getValue("CLNCPATH_CODE"))) {
        	 if (!checkNullAndEmpty(this.getValueString("CHANGE_REASON"))) {
                 this.messageBox("��������ԭ��");
                 return false;
             }
		}
        if (null!=table && null!=table.getParmValue() && table.getParmValue().getCount()>0) {
        	for (int i = 0; i < table.getParmValue().getCount(); i++) {
    			if (null==table.getParmValue().getValue("NEW_SCHD_CODE",i)||table.getParmValue().getValue("NEW_SCHD_CODE",i).length()<=0) {
    				this.messageBox("��·��ʱ�̲�����Ϊ��ֵ");
    				return false;
    			}
    			if (null==table.getParmValue().getValue("NEW_CLNCPATH_CODE",i)||table.getParmValue().getValue("NEW_CLNCPATH_CODE",i).length()<=0) {
    				this.messageBox("��·��������Ϊ��ֵ");
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
		String cln = this.getValueString("CLNCPATH_CODE");
		if (row<0) {
			return;
		}
		String schdCode=this.getValueString("SCHD_CODE_SUM");
		if (null != cln && cln.length() > 0) {
			parm.setData("NEW_CLNCPATH_CODE", row, cln);
			
		}else{
			parm.setData("NEW_CLNCPATH_CODE", row, "");
		}
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
        String sql="SELECT SCHD_CODE FROM CLP_THRPYSCHDM WHERE CLNCPATH_CODE='"+
        this.getValueString("CLNCPATH_CODE")+"' ORDER BY SEQ";
        TParm result=new TParm(TJDODBTool.getInstance().select(sql));
        TextFormatCLPEvlStandm combo_evl=  (TextFormatCLPEvlStandm)this.getComponent("EVL_CODE");
        combo_evl.setClncpathCode(this.getValueString("CLNCPATH_CODE"));
        combo_evl.onQuery();
        if (result.getCount()>0) {
        	this.setValue("SCHD_CODE_SUM", result.getValue("SCHD_CODE",0));
		}
        sql="SELECT EVL_CODE FROM CLP_EVL_STANDM WHERE CLNCPATH_CODE = '" + 
        this.getValueString("CLNCPATH_CODE") + "' ORDER BY EVL_CODE,SEQ ";
        result=new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getCount()>0) {
       	    this.setValue("EVL_CODE", result.getValue("EVL_CODE",0));
		}
        if (null!=this.getValueString("CLNCPATH_CODE")
        		&&!this.getValueString("OLD_CLNCPATH_CODE").equals(this.getValueString("CLNCPATH_CODE"))) {
        	 //��ѯ�˲����Ƿ��Ѿ�����ʱ�̵�ҽ�����ã����Ѿ������ҽ������·����ʱ�̴���
            sql="SELECT CLNCPATH_CODE,SCHD_CODE,'' NEW_CLNCPATH_CODE,'' NEW_SCHD_CODE,'Y' FLG  FROM IBS_ORDD WHERE CASE_NO='"
            	+sendParm.getValue("CASE_NO")+"' AND CLNCPATH_CODE IS NOT NULL GROUP BY CLNCPATH_CODE,SCHD_CODE";
            TParm oldClnParm = new TParm(TJDODBTool.getInstance().select(
                    sql.toString()));
            if (oldClnParm.getCount()>0) {
            	table.setParmValue(oldClnParm);
    		}
		}else{
			table.setParmValue(new TParm());
		}
	}
}
