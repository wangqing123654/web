package com.javahis.ui.clp;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
/**
 * <p>Title: �����ٴ�·����� </p>
 *
 * <p>Description: ����·������ֱ�ӽ���ʱ�̣���ѯ��ͬʱ�̴���ֱ�ӽ���</p>
 *
 * <p>Copyright: Copyright (c) 2016</p>
 *
 * <p>Company: bluecore </p>
 *
 * @author pangben 20160920
 * @version 1.0
 */
public class CLPManageChangeExeControl extends TControl{
	 /**
     * ҳ���ʼ������
     */
    public void onInit() {
        super.onInit();
        initPage();
    }
    TParm sendParm;
    TParm oldClnParm; 
    String caseNo;
    String mrNo;
    String evlCode;
    /**
     * ��ʼ��ҳ��
     */
    private void initPage() {
    	 sendParm = (TParm)this.getParameter();
         String oldCLNPathCode = (String) sendParm.getData("OLD_CLNCPATH_CODE");
         caseNo=sendParm.getValue("CASE_NO");
         mrNo=sendParm.getValue("MR_NO");
         if(oldCLNPathCode.length()<=0){
        	 callFunction("UI|save|setEnabled", false);
        	 this.messageBox("�����Բ���");
        	 return;
         }
         evlCode=sendParm.getValue("EVL_CODE");
//         String sql=" SELECT EVL_CODE FROM CLP_EVL_STANDM WHERE CLNCPATH_CODE='"+oldCLNPathCode+"'";
//         TParm newClnParm = new TParm(TJDODBTool.getInstance().select(
//                 sql.toString()));
//         if (newClnParm.getCount()>0) {
//        	
// 		 }
         this.setValue("OLD_CLNCPATH_CODE", oldCLNPathCode);//ԭ·��
         String sql="SELECT CLNCPATH_CODE AS OLD_CLNCPATH_CODE,SCHD_CODE AS OLD_SCHD_CODE," +
         		"'' AS NEW_CLNCPATH_CODE,'' NEW_SCHD_CODE,'N' FLG  FROM IBS_ORDD WHERE CASE_NO='"
         	+sendParm.getValue("CASE_NO")+"' GROUP BY CLNCPATH_CODE,SCHD_CODE";
         oldClnParm = new TParm(TJDODBTool.getInstance().select(
                 sql.toString()));
    }
    /**
     * ���
     */
    public void onSave(){
    	if(null==this.getValue("NEW_CLNCPATH_CODE")||this.getValue("NEW_CLNCPATH_CODE").toString().length()<=0){
    		this.messageBox("��ѡ����Ҫ�����·��");
    		return;
    	}
    	 if (!checkNullAndEmpty(this.getValueString("CHANGE_REASON"))) {
             this.messageBox("��������ԭ��");
             return ;
         }
    	String sqlClp="SELECT SCHD_CODE FROM CLP_THRPYSCHDM WHERE CLNCPATH_CODE='"+this.getValue("NEW_CLNCPATH_CODE")+"' ORDER BY SEQ";
		TParm clpParm = new TParm(TJDODBTool.getInstance().select(sqlClp));
    	for (int i = 0; i < oldClnParm.getCount(); i++) {
    		if(null==oldClnParm.getValue("OLD_SCHD_CODE",i)||oldClnParm.getValue("OLD_SCHD_CODE",i).length()<=0){
				continue;
			}
			for (int j = 0; j < clpParm.getCount(); j++) {
				if (oldClnParm.getValue("OLD_SCHD_CODE",i).
						equals(clpParm.getValue("SCHD_CODE",j))) {//ʱ�̴�����ͬ���Զ��ȶ�
					oldClnParm.setData("FLG",i,"Y");
					break;
				}
			}
		}
    	boolean flg=false;
    	for (int i = 0; i < oldClnParm.getCount(); i++) {
    		if (oldClnParm.getValue("FLG",i).equals("N")){
    			flg=true;
    			break;
    		}
		}
    	 String sql=" SELECT EVL_CODE FROM CLP_EVL_STANDM WHERE CLNCPATH_CODE='"+this.getValue("NEW_CLNCPATH_CODE")+"'";
         TParm newClnParm = new TParm(TJDODBTool.getInstance().select(
                 sql.toString()));
         String evlCodeNew="";
         if (newClnParm.getCount()>0) {
        	 evlCodeNew=newClnParm.getValue("EVL_CODE",0);
 		 }
         if (flg) {
    		this.messageBox("����δƥ���ʱ��,��Ҫ�ֶ�����");
    		TParm sendParm = new TParm();
    		sendParm.setData("OLD_CLNCPATH_CODE", this
    				.getValueString("OLD_CLNCPATH_CODE"));
    		sendParm.setData("MR_NO", mrNo);
    		sendParm.setData("EVL_CODE", evlCode);
    		sendParm.setData("CASE_NO", caseNo);
    		sendParm.setData("CHANGE_REASON", this.getValue("CHANGE_REASON"));
    		sendParm.setData("EVL_CODE_NEW",evlCodeNew);
    		sendParm.setData("oldClnParm",oldClnParm.getData());
    		sendParm.setData("NEW_CLNCPATH_CODE",this.getValue("NEW_CLNCPATH_CODE"));
    		String resultstr = (String) this.openDialog(
    				"%ROOT%\\config\\clp\\CLPManagemChange.x", sendParm);
    		if (null!=resultstr) {
    			if (resultstr.toLowerCase().indexOf("success") >= 0) {
    			    this.setReturnValue("SUCCESS");
		            this.closeWindow();
    			}
    		}
		}else{//ȫ��ƥ��ֱ�Ӹ��²���
				TParm parmForInsert = new TParm(sendParm.getData());
		        parmForInsert.setData("CLNCPATH_CODE", this.getValueString("NEW_CLNCPATH_CODE"));
		        parmForInsert.setData("CHANGE_REASON", this.getValueString("CHANGE_REASON"));
		        Timestamp today = SystemTool.getInstance().getDate();
		        String datestr = StringTool.getString(today, "yyyyMMdd");
		        parmForInsert.setData("IN_DATE",datestr);
		        parmForInsert.setData("OPT_DATE", datestr);
		        parmForInsert.setData("SCHD_CODE",this.getValue("SCHD_CODE_SUM"));//��ǰʱ��
		        //�¼���·�����Զ������·��
		        datestr = StringTool.getString(today, "yyyyMMddHHmmss");
		        parmForInsert.setData("START_DTTM",datestr);
		        sql=" SELECT EVL_CODE FROM CLP_EVL_STANDM WHERE CLNCPATH_CODE='"+this.getValueString("NEW_CLNCPATH_CODE")+"'";
		        newClnParm = new TParm(TJDODBTool.getInstance().select(
		                sql.toString()));
		        if (newClnParm.getCount()>0) {
		        	parmForInsert.setData("EVL_CODE",newClnParm.getValue("EVL_CODE",0));
				}
		        //���������Ϣ
		        parmForInsert.setData("REGION_CODE", Operator.getRegion());
		        parmForInsert.setData("OPT_USER", Operator.getID());
		        parmForInsert.setData("OPT_TERM",  Operator.getIP());
		        TParm parmForUpadate = new TParm();
		        parmForUpadate.setData("CASE_NO",parmForInsert.getValue("CASE_NO"));
		        parmForUpadate.setData("CLNCPATH_CODE",this.getValue("OLD_CLNCPATH_CODE"));
		        parmForUpadate.setData("CHANGE_REASON",parmForInsert.getValue("CHANGE_REASON"));
		        TParm actionParm = new TParm();
		        actionParm.setData("parmForInsert",parmForInsert.getData());
		        actionParm.setData("parmForUpadate",parmForUpadate.getData());
		        actionParm.setData("CLP_BILL_FLG","Y");//��ȫƥ��ֻ�����޸�·�����Ƽ���
		        actionParm.setData("billParm",oldClnParm.getData());
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
}
