package com.javahis.ui.mro;

import java.sql.Timestamp;

import jdo.mro.MROQueueTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * <p>Title: ������������</p>
 *
 * <p>Description: ������������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk  2009-5-6
 * @version 1.0
 */
public class MROQueueControl
    extends TControl {
    private TParm data;
    private TParm resultPage1;
    private TParm resultPage2;
    private TParm resultPage3;
    private String IN_FLG;
    public void onInit() {
        super.onInit();
        this.setValue("t3_STATE","0");
    }
    /**
     * ���
     */
    public void onClear(){
    	int selPage = ((TTabbedPane)this.getComponent("tTabbedPane_0")).getSelectedIndex();
    	switch (selPage) {
		case 0:
			//��յ�һҳǩ
	        this.clearValue("t1_IPD_NO;t1_MR_NO;t1_QUE_SEQ;t1_QUE_DATE;t1_RTN_DATE;t1_DUE_DATE;t1_LEND_CODE");
	        ((TTable)this.getComponent("Table1")).clearSelection();
	        ((TTable)this.getComponent("Table1")).removeRowAll();
			break;
		case 1:
			//��յڶ�ҳǩ
	        this.clearValue("t2_IPD_NO;t2_MR_NO;isOutHp");
	        ((TTable)this.getComponent("Table2")).clearSelection();	
	        ((TTable)this.getComponent("Table2")).removeRowAll();
			break;
		case 2:
			//��յ���ҳǩ
	        this.clearValue("t3_LEND_CODE;t3_RTN_DATE;t3_REQ_DEPT;t3_MR_PERSON;t3_isOutHp;t3_OUT_DATE");
	        ((TTable)this.getComponent("Table3")).clearSelection();
	        ((TTable)this.getComponent("Table3")).removeRowAll();
	        ont3_isOutHp();
	        this.setValue("t3_STATE", "0");
			break;
		case 3:
			//��յ���ҳǩ
	        this.clearValue("t4_IPD_NO;t4_MR_NO;QUE_DATE_START;QUE_DATE_END;IN_DATE_START;IN_DATE_END");
	        ((TTable)this.getComponent("Table4")).clearSelection();
	        ((TTable)this.getComponent("Table4")).removeRowAll();
			break;
		default:
			break;
		}
    }
    /**
     * ��ѯ
     */
    public void onQuery(){
        //��ȡ��ǰѡ���ҳǩ ����
        int selectedPage = ((TTabbedPane)this.getComponent("tTabbedPane_0")).getSelectedIndex();
        //��һҳǩ
        if(selectedPage==0){
            query1();
        }else if(selectedPage==1){
            query2();
        }else if(selectedPage==2){
            query3();
        }else if(selectedPage==3){
            query4();
        }
    }
    /**
     * ��һҳǩ��ѯ
     */
    private void query1() {
        TParm parm = new TParm();
        resultPage1 = new TParm();
        //IPD_NO
        if (getValueString("t1_IPD_NO").trim().length() > 0) {
            String ipd_no = PatTool.getInstance().checkIpdno(getValueString(
                "t1_IPD_NO"));
            this.setValue("t1_IPD_NO", ipd_no);
            parm.setData("IPD_NO", ipd_no);
        }
        //MR_NO
        if (getValueString("t1_MR_NO").trim().length() > 0) {
            String mr_no = PatTool.getInstance().checkMrno(getValueString(
                "t1_MR_NO"));
            this.setValue("t1_MR_NO", mr_no);
            parm.setData("MR_NO", mr_no);
        }
        //���ĺ�
        if (getValueString("t1_QUE_SEQ").trim().length() > 0) {
            parm.setData("QUE_SEQ", getValueString("t1_QUE_SEQ").trim());
        }
        parm.setData("ISSUE_CODE", "0"); //��ѯ������״̬�Ĳ���
        //��������
        if(this.getValueString("t1_QUE_DATE").trim().length() > 0){
        	parm.setData("QUE_DATE", this.getValueString("t1_QUE_DATE").replace("-", "").substring(0, 8));
        }
        resultPage1 = MROQueueTool.getInstance().queryQueue(parm);
        if (resultPage1.getErrCode() < 0) {
            this.messageBox("��ѯʧ�ܣ�");
            return;
        }
        if(resultPage1.getCount() <= 0){
        	return;
        }
        TTable table = (TTable)this.getComponent("Table1");
        table.setParmValue(resultPage1);
    }
    /**
     * �ڶ�ҳǩ��ѯ
     */
    private void query2(){ 
        TParm parm = new TParm();
        resultPage2 = new TParm();
        //IPD_NO
        if (getValueString("t2_IPD_NO").trim().length() > 0) {
            String ipd_no = PatTool.getInstance().checkIpdno(getValueString(
                "t2_IPD_NO"));
            this.setValue("t2_IPD_NO", ipd_no);
            parm.setData("IPD_NO", ipd_no);
        }
        //MR_NO
        if (getValueString("t2_MR_NO").trim().length() > 0) {
            String mr_no = PatTool.getInstance().checkMrno(getValueString(
                "t2_MR_NO"));
            this.setValue("t2_MR_NO", mr_no);
            parm.setData("MR_NO", mr_no);
        }
        resultPage2 = MROQueueTool.getInstance().selectIn(parm);
        if (resultPage2.getErrCode() < 0) {
            this.messageBox("��ѯʧ�ܣ�" + resultPage2.getErrCode() + resultPage2.getErrName() + resultPage2.getErrText());
            ((TTable)this.getComponent("Table2")).removeRowAll();
            return;
        }
        if(resultPage2.getCount() <= 0){
        	return;
        }
        TTable table = (TTable)this.getComponent("Table2");
        table.setParmValue(resultPage2);
    }
    /**
     * ����ҳǩ��ѯ
     */
    private void query3(){
        TParm parm = new TParm();
        resultPage3 = new TParm();
        String flg = this.getValueString("t3_isOutHp");
        //===========pangben modify 20110518 start ����������
        if(null!=Operator.getRegion()&&Operator.getRegion().length()>0)
            parm.setData("REGION_CODE",Operator.getRegion());
        //===========pangben modify 20110518 stop

        //����� ��Ժ�����鵵��ѯ
        if("Y".equals(flg)){
            if(this.getValue("t3_OUT_DATE")!=null){
                parm.setData("DS_DATE",this.getValue("t3_OUT_DATE").toString().replace("-", "").substring(0, 8));
            }
            resultPage3 = MROQueueTool.getInstance().selectOutHp(parm);
        }else{
            if(this.getValueString("t3_STATE").length()>0){
                parm.setData("ISSUE_CODE",this.getValueString("t3_STATE"));
            }
            if(this.getValueString("t3_LEND_CODE").length()>0){
                parm.setData("LEND_CODE",this.getValueString("t3_LEND_CODE"));
            }
            if(this.getValueString("t3_REQ_DEPT").length()>0){
                parm.setData("REQ_DEPT",this.getValueString("t3_REQ_DEPT"));
            }
            if(this.getValueString("t3_MR_PERSON").length()>0){
                parm.setData("MR_PERSON",this.getValueString("t3_MR_PERSON"));
            }
            if(this.getValue("t3_RTN_DATE")!=null){
                parm.setData("RTN_DATE",this.getValueString("t3_RTN_DATE").replace("-","").substring(0, 8));
            }
            resultPage3 = MROQueueTool.getInstance().selectOutQueue(parm);
            IN_FLG = this.getValueString("t3_STATE");
        }
        if (resultPage3.getErrCode() < 0) {
            this.messageBox("E0005");
            ((TTable)this.getComponent("Table3")).removeRowAll();
            return;
        }
        if (resultPage3.getCount() <= 0) {
            ((TTable)this.getComponent("Table3")).removeRowAll();
            return;
        }
        ((TTable)this.getComponent("Table3")).setParmValue(resultPage3);
    }
    /**
     * ����ҳǩ��ѯ
     */
    private void query4(){
        TParm parm = new TParm();
        if(this.getValueString("t4_IPD_NO").trim().length()>0){
            String ipd_no = PatTool.getInstance().checkIpdno(getValueString("t4_IPD_NO"));
            this.setValue("t4_IPD_NO", ipd_no);
            parm.setData("IPD_NO", ipd_no);
        }
        //===========pangben modify 20110518 start ����������
        if(null!=Operator.getRegion()&&Operator.getRegion().length()>0)
            parm.setData("REGION_CODE",Operator.getRegion());
        //===========pangben modify 20110518 stop
        //MR_NO
        if (getValueString("t4_MR_NO").trim().length() > 0) {
            String mr_no = PatTool.getInstance().checkMrno(getValueString("t4_MR_NO"));
            this.setValue("t4_MR_NO", mr_no);
            parm.setData("MR_NO", mr_no);
        }
        //��������
        if(this.getValue("QUE_DATE_START") != null){
            parm.setData("QUE_DATE_START",this.getValue("QUE_DATE_START").toString().replace("-", "").substring(0, 8));
        }
        if(this.getValue("QUE_DATE_END") != null){
        	parm.setData("QUE_DATE_END",this.getValue("QUE_DATE_END").toString().replace("-", "").substring(0, 8));
        }
        //�������  ��Ҫת�����ַ��� ��ʽ������ YYYYMMDD
        if(this.getValue("IN_DATE_START") != null){
            parm.setData("IN_DATE_START",this.getValue("IN_DATE_START").toString().replace("-", "").substring(0, 8));
        }
        if(this.getValue("IN_DATE_END") != null){
        	parm.setData("IN_DATE_END",this.getValue("IN_DATE_END").toString().replace("-", "").substring(0, 8));
        }
        data = MROQueueTool.getInstance().selectTRANHIS(parm);
        if (data.getErrCode() < 0) {
            this.messageBox("��ѯʧ�ܣ�");
            ((TTable)this.getComponent("Table4")).removeRowAll();
            return;
        }
        if(data.getCount() <= 0){
        	((TTable)this.getComponent("Table4")).removeRowAll();
        	return;
        }
        TTable table = (TTable)this.getComponent("Table4");
        table.setParmValue(data);
    }
    /**
     * ����
     */
    public void onSave(){
        //��ȡ��ǰѡ���ҳǩ ����
        int selectedPage = ((TTabbedPane)this.getComponent("tTabbedPane_0")).getSelectedIndex();
        //��һҳǩ ��������
        if(selectedPage==0){
            save1();
        }else if(selectedPage==1){
            save2();
        }else if(selectedPage==2){
            save3();
        }
    }
    /**
     * ��һҳǩ���淽��
     */
    public void save1(){
        int row = ( (TTable)this.getComponent("Table1")).getSelectedRow();
        if(row<0){
            this.messageBox("��ѡ�񲡰�!");
            return;
        }
        TParm submit = new TParm();
        TParm parm = new TParm();
        
        //���������޸Ĳ���
        parm.setData("MR_NO", resultPage1.getValue("MR_NO", row));
        parm.setData("IN_FLG", "1"); //�������� �������״̬
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        submit.setData("MRV",parm.getData());

        //���������޸Ĳ���
        parm = new TParm();
        parm.setData("QUE_SEQ", resultPage1.getValue("QUE_SEQ", row));
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        parm.setData("ISSUE_CODE", "1"); //���������⵵ �������״̬
        submit.setData("Queue",parm.getData());

        //��ʷ���޸Ĳ���
        parm = new TParm();
        //������ʷ��¼����Ҫ�Ĳ���
        parm.setData("IPD_NO",resultPage1.getValue("IPD_NO",row));
        parm.setData("MR_NO", resultPage1.getValue("MR_NO", row));
        parm.setData("QUE_DATE",StringTool.getString((Timestamp)resultPage1.getData("QUE_DATE",row), "yyyyMMdd") + 
        		SystemTool.getInstance().getDate().toString().substring(0, SystemTool.getInstance().getDate().toString().lastIndexOf("."))
    			.replace("-", "").replace(" ", "").replace(":", "").substring(8, 14));
        parm.setData("TRAN_KIND","0");//������0 ���� 1���
        parm.setData("QUE_SEQ",resultPage1.getValue("QUE_SEQ",row));
        parm.setData("LEND_CODE",resultPage1.getValue("LEND_CODE",row));
        parm.setData("CURT_LOCATION","");//Ŀǰλ��
        parm.setData("REGION_CODE","");//�Һ�����
        parm.setData("MR_PERSON",resultPage1.getValue("MR_PERSON",row));//������
        parm.setData("TRAN_HOSP",resultPage1.getValue("QUE_HOSP",row));//�����Ժ��
        TNull t = new TNull(Timestamp.class);
        parm.setData("IN_DATE",t);//�黹�������
        parm.setData("IN_PERSON","");//�����Ա
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        parm.setData("REGION_CODE", Operator.getRegion());
        submit.setData("Tranhis",parm.getData());
        TParm result = TIOM_AppServer.executeAction(
            "action.mro.MROQueueAction",
            "updateOUT",submit);
        if (result.getErrCode() < 0) {
            this.messageBox("�������ʧ�ܣ�" + result.getErrName() + result.getErrCode() + result.getErrText());
            return;
        }
        this.messageBox("��������ɹ���");
        onClear();
    }
    /**
     * �ڶ�ҳǩ���淽��
     */
    public void save2(){
        TParm submit = new TParm();
        TParm parm = new TParm();
        TParm result = new TParm();
        //����� ����Ժ�����鵵�� ֻ�޸� �����������״̬λ IN_FLG ����Ϊ�ڿ�
        if(((TCheckBox)this.getComponent("isOutHp")).isSelected()){
            if(this.getValueString("t2_MR_NO").trim().length()<=0){
                this.messageBox_("�����벡����!");
                return;
            }
            //����Ժ�����鵵�� �Ĳ��� ��MRO_Queue���в������� Ҫ��ȡ�����MR_NO
            String mr_no =PatTool.getInstance().checkMrno(this.getValueString("t2_MR_NO"));
            parm.setData("MR_NO",mr_no);
            parm.setData("IN_FLG", "2"); //�������� �������״̬ (�ڿ�)
            parm.setData("OPT_USER",Operator.getID());
            parm.setData("OPT_TERM",Operator.getIP());
            this.setValue("t2_MR_NO",mr_no);
            result = TIOM_AppServer.executeAction(
                "action.mro.MROQueueAction",
                "updateIN_FLG", parm);
        }
        else{
            int row = ( (TTable)this.getComponent("Table2")).getSelectedRow(); //��ȡѡ����
            if (row < 0) {
                this.messageBox_("��ѡ�񲡰�!");
                return;
            }
            if(resultPage2.getData("QUE_SEQ", row)==null){
                this.messageBox_("�˲���Ϊ��Ժ��������ѡ���Ժ�����鵵��");
                return;
            }
            //���������޸Ĳ���
            parm.setData("QUE_SEQ", resultPage2.getValue("QUE_SEQ", row));
            parm.setData("ISSUE_CODE", "2"); //���������⵵ �������״̬ (�黹)
            parm.setData("OPT_USER",Operator.getID());
            parm.setData("OPT_TERM",Operator.getIP());
            submit.setData("Queue",parm.getData());

            //���������޸Ĳ���
            parm = new TParm();
            parm.setData("MR_NO",resultPage2.getValue("MR_NO",row));
            parm.setData("IN_FLG", "2"); //�������� �������״̬ (�ڿ�)
            parm.setData("OPT_USER",Operator.getID());
            parm.setData("OPT_TERM",Operator.getIP());
            submit.setData("MRV",parm.getData());

            //������ʷ��¼����Ҫ�Ĳ���
            parm = new TParm();
            parm.setData("IPD_NO", resultPage2.getValue("IPD_NO", row));
            parm.setData("MR_NO", resultPage2.getValue("MR_NO", row));
            parm.setData("QUE_DATE",
                         StringTool.getString( (Timestamp) resultPage2.
                                              getData("QUE_DATE", row),
                                              "yyyyMMddHHmmss"));
            parm.setData("TRAN_KIND", "1"); //������0 ���� 1���
            parm.setData("QUE_SEQ",resultPage2.getValue("QUE_SEQ",row));
            parm.setData("LEND_CODE", resultPage2.getValue("LEND_CODE", row));
            parm.setData("CURT_LOCATION", ""); //Ŀǰλ��
            parm.setData("REGION_CODE", Operator.getRegion()); //�Һ�����
            parm.setData("MR_PERSON", resultPage2.getValue("MR_PERSON", row)); //������
            parm.setData("TRAN_HOSP", resultPage2.getValue("QUE_HOSP", row)); //�����Ժ��
            parm.setData("IN_DATE", SystemTool.getInstance().getDate()); //�黹�������
            parm.setData("IN_PERSON", Operator.getID()); //�����Ա
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", SystemTool.getInstance().getDate()); //��������
            parm.setData("OPT_TERM", Operator.getIP());
            submit.setData("Tranhis", parm.getData());

            result = TIOM_AppServer.executeAction(
                "action.mro.MROQueueAction",
                "updateIN", submit);
        }
        if (result.getErrCode() < 0) {
            this.messageBox_("������ʧ�ܣ�" + result.getErrName());
            return;
        }
        this.messageBox_("�������ɹ���");
        onClear();
    }
    //����ҳǩ�������
    private void save3(){
        TParm parm = new TParm();
        TParm result = new TParm();
        int row = ( (TTable)this.getComponent("Table3")).getSelectedRow(); //��ȡѡ����
        if(row<0){
            this.messageBox_("��ѡ��һ����Ϣ��");
            return;
        }
        //����� ����Ժ�����鵵�� ֻ�޸� �����������״̬λ IN_FLG ����Ϊ�ڿ�
        if(((TCheckBox)this.getComponent("t3_isOutHp")).isSelected()){
            parm.setData("MR_NO",resultPage3.getValue("MR_NO",row));
            parm.setData("IN_FLG", "2"); //�������� �������״̬ (�ڿ�)
            parm.setData("OPT_USER",Operator.getID());
            parm.setData("OPT_TERM",Operator.getIP());
            result = TIOM_AppServer.executeAction("action.mro.MROQueueAction","updateIN_FLG", parm);
            if(result.getErrCode()<0){
                this.messageBox("E0005");
                return;
            }
        }else{
            if ("0".equals(IN_FLG)) {//�������
                TParm submit = new TParm();
                TParm parm1 = new TParm();
                //���������޸Ĳ���
                parm1.setData("MR_NO", resultPage3.getValue("MR_NO", row));
                parm1.setData("IN_FLG", "1"); //�������� �������״̬
                parm1.setData("OPT_USER", Operator.getID());
                parm1.setData("OPT_TERM", Operator.getIP());
                submit.setData("MRV", parm1.getData());

                //���������޸Ĳ���
                parm1 = new TParm();
                parm1.setData("QUE_SEQ", resultPage3.getValue("QUE_SEQ", row));
                parm1.setData("OPT_USER", Operator.getID());
                parm1.setData("OPT_TERM", Operator.getIP());
                parm1.setData("ISSUE_CODE", "1"); //���������⵵ �������״̬
                submit.setData("Queue", parm1.getData());

                //��ʷ���޸Ĳ���
                parm1 = new TParm();
                //������ʷ��¼����Ҫ�Ĳ���
                parm1.setData("IPD_NO", resultPage3.getValue("IPD_NO", row));
                parm1.setData("MR_NO", resultPage3.getValue("MR_NO", row));
                parm1.setData("QUE_DATE",
                             StringTool.getString( (Timestamp) resultPage3.
                                                  getData("QUE_DATE", row),
                                                  "yyyyMMddHHmmss"));
                parm1.setData("TRAN_KIND", "0"); //������0 ���� 1���
                parm1.setData("QUE_SEQ", resultPage3.getValue("QUE_SEQ", row));
                parm1.setData("LEND_CODE", resultPage3.getValue("LEND_CODE", row));
                parm1.setData("CURT_LOCATION", ""); //Ŀǰλ��
                parm1.setData("REGION_CODE", ""); //�Һ�����
                parm1.setData("MR_PERSON", resultPage3.getValue("MR_PERSON", row)); //������
                parm1.setData("TRAN_HOSP", resultPage3.getValue("QUE_HOSP", row)); //�����Ժ��
                TNull t = new TNull(Timestamp.class);
                parm1.setData("IN_DATE", t); //�黹�������
                parm1.setData("IN_PERSON", ""); //�����Ա
                parm1.setData("OPT_USER", Operator.getID());
                parm1.setData("OPT_TERM", Operator.getIP());
                submit.setData("Tranhis", parm1.getData());
                TParm re = TIOM_AppServer.executeAction("action.mro.MROQueueAction","updateOUT", submit);
                if (re.getErrCode() < 0) {
                    this.messageBox_("�������ʧ�ܣ�" + re.getErrName() + re.getErrCode() + re.getErrText());
                    return;
                }
                this.messageBox_("��������ɹ���");

            }else if("1".equals(IN_FLG)){//������
                TParm submit = new TParm();
                TParm parm1 = new TParm();
                //���������޸Ĳ���
                parm1.setData("QUE_SEQ", resultPage3.getValue("QUE_SEQ", row));
                parm1.setData("ISSUE_CODE", "2"); //���������⵵ �������״̬ (�黹)
                parm1.setData("OPT_USER", Operator.getID());
                parm1.setData("OPT_TERM", Operator.getIP());
                submit.setData("Queue", parm1.getData());

                //���������޸Ĳ���
                parm1 = new TParm();
                parm1.setData("MR_NO", resultPage3.getValue("MR_NO", row));
                parm1.setData("IN_FLG", "2"); //�������� �������״̬ (�ڿ�)
                parm1.setData("OPT_USER", Operator.getID());
                parm1.setData("OPT_TERM", Operator.getIP());
                submit.setData("MRV", parm1.getData());

                //������ʷ��¼����Ҫ�Ĳ���
                parm1 = new TParm();
                parm1.setData("IPD_NO", resultPage3.getValue("IPD_NO", row));
                parm1.setData("MR_NO", resultPage3.getValue("MR_NO", row));
                parm1.setData("QUE_DATE",
                             StringTool.getString( (Timestamp) resultPage3.
                                                  getData("QUE_DATE", row),
                                                  "yyyyMMddHHmmss"));
                parm1.setData("TRAN_KIND", "1"); //������0 ���� 1���
                parm1.setData("QUE_SEQ", resultPage3.getValue("QUE_SEQ", row));
                parm1.setData("LEND_CODE", resultPage3.getValue("LEND_CODE", row));
                parm1.setData("CURT_LOCATION", ""); //Ŀǰλ��
                parm1.setData("REGION_CODE", ""); //�Һ�����
                parm1.setData("MR_PERSON", resultPage3.getValue("MR_PERSON", row)); //������
                parm1.setData("TRAN_HOSP", resultPage3.getValue("QUE_HOSP", row)); //�����Ժ��
                parm1.setData("IN_DATE", SystemTool.getInstance().getDate()); //�黹�������
                parm1.setData("IN_PERSON", Operator.getID()); //�����Ա
                parm1.setData("OPT_USER", Operator.getID());
                parm1.setData("OPT_TERM", Operator.getIP());
                submit.setData("Tranhis", parm1.getData());
                TParm re = TIOM_AppServer.executeAction(
                    "action.mro.MROQueueAction",
                    "updateIN", submit);
                if (re.getErrCode() < 0) {
                    this.messageBox_("������ʧ�ܣ�" + re.getErrName() + re.getErrCode() + re.getErrText());
                    return;
                }
                this.messageBox_("�������ɹ���");
            }
        }
        onClear();
    }
    /**
     * ҳǩ�л�
     */
    public void pageChange(){
        TTabbedPane tp = (TTabbedPane)this.getComponent("tTabbedPane_0");
        int index = tp.getSelectedIndex();
        if(index==3){
            //���ذ�ť
            ((TMenuItem) getComponent("save")).setVisible(false);
        }
        else{
            //��ʾ��ť
            ((TMenuItem) getComponent("save")).setVisible(true);
        }
    }

    /**
     * Table1����¼�
     */
    public void selectRow1() {
        TTable table = (TTable)this.getComponent("Table1");
        int row = table.getSelectedRow();
        this.setValue("t1_IPD_NO",resultPage1.getData("IPD_NO",row));//סԺ��
        this.setValue("t1_MR_NO",resultPage1.getData("MR_NO",row));//������
        this.setValue("t1_QUE_SEQ",resultPage1.getData("QUE_SEQ",row));//���ĺ�
        this.setValue("t1_QUE_DATE",resultPage1.getData("QUE_DATE",row));//��������
        this.setValue("t1_RTN_DATE",resultPage1.getData("RTN_DATE",row));//Ӧ�黹����
        this.setValue("t1_DUE_DATE",resultPage1.getData("DUE_DATE",row));//Ӧ�����
        this.setValue("t1_LEND_CODE",resultPage1.getData("LEND_CODE",row));//����ԭ��
    }
    /**
     * Table2����¼�
     */
    public void selectRow2() {
        TTable table = (TTable)this.getComponent("Table2");
        int row = table.getSelectedRow();
        this.setValue("t2_IPD_NO",resultPage2.getData("IPD_NO",row));//סԺ��
        this.setValue("t2_MR_NO",resultPage2.getData("MR_NO",row));//������
    }
    /**
     * ҳǩ�� ��Ժ�����鵵 ����¼�
     */
    public void ont3_isOutHp(){
        if("Y".equals(this.getValueString("t3_isOutHp"))){
            this.callFunction("UI|t3_STATE|setEnabled", false);
            this.callFunction("UI|t3_LEND_CODE|setEnabled", false);
            this.callFunction("UI|t3_RTN_DATE|setEnabled", false);
            this.callFunction("UI|t3_REQ_DEPT|setEnabled", false);
            this.callFunction("UI|t3_MR_PERSON|setEnabled", false);
            this.callFunction("UI|t3_OUT_DATE|setEnabled", true);
        }else {
            this.callFunction("UI|t3_STATE|setEnabled", true);
            this.callFunction("UI|t3_LEND_CODE|setEnabled", true);
            this.callFunction("UI|t3_RTN_DATE|setEnabled", true);
            this.callFunction("UI|t3_REQ_DEPT|setEnabled", true);
            this.callFunction("UI|t3_MR_PERSON|setEnabled", true);
            this.callFunction("UI|t3_OUT_DATE|setEnabled", false);
        }
        this.clearValue("t3_STATE;t3_LEND_CODE;t3_RTN_DATE;t3_REQ_DEPT;t3_MR_PERSON;t3_OUT_DATE;");
        TTable table3 = (TTable)this.getComponent("Table3");
        table3.removeRowAll();
    }
}
