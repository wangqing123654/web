package com.javahis.ui.mro;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.sys.PatTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.mro.MROInfectTool;
import com.dongyang.ui.TTable;
import java.util.Vector;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TLabel;
import com.dongyang.jdo.TDataStore;
import jdo.sys.SystemTool;
import com.dongyang.ui.event.TPopupMenuEvent;

/**
 * <p>Title: ��Ⱦ�����濨��ѯ</p>
 *
 * <p>Description: ��Ⱦ�����濨��ѯ</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-10-16
 * @version 1.0
 */
public class MROInfectQueryControl
    extends TControl {
    private TTable Table;
    private String TYPE = "H";//��¼����״̬ H:����ʷ��ѯ����ť����  M:��ָ���������ڶ�����Ⱦ����¼ʱ�Զ�����
    private boolean returnType = false;//��¼�Ƿ�ѡ���˻ش�ֵ���лش�
    public void onInit(){
        Table = (TTable)this.getComponent("Table");
        OrderList orderList = new OrderList();
        Table.addItem("OrderList",orderList);
        PageInit();
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("DATE_S",date);
        this.setValue("DATE_E",date);
        //ֻ��text���������������ICD10������
        callFunction("UI|ICD_CODE|setPopupMenuParameter", "aaa",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        //textfield���ܻش�ֵ
        callFunction("UI|ICD_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
    }
    /**
     * ���ݲ��� ҳ���ʼ��
     */
    private void PageInit(){
        //��ȡ���ò���
        /**
         * �������ã�
         * MR_NO��������
         * TYPE������״̬ H:����ʷ��ѯ����ť����  M:��ָ���������ڶ�����Ⱦ����¼ʱ�Զ�����
         */
//        TParm a = new TParm();
//        a.setData("MR_NO","2211111");
//        a.setData("TYPE","H");
//        Object obj = a;
        Object obj = this.getParameter();
        TParm parm = new TParm();
        if(obj instanceof TParm){
            parm = (TParm)obj;
            this.setValue("MR_NO",parm.getValue("MR_NO"));
            if(parm.getValue("CASE_NO").length()>0)
                this.setValue("CASE_NO",parm.getValue("CASE_NO"));
            TYPE = parm.getValue("TYPE");
        }
        //����� ָ���������ڶ�����Ⱦ����¼ʱ�Զ����õ� ��ô�����Բ�ѯ��������
        if("M".equals(TYPE)){
            this.callFunction("UI|MR_NO|setEnabled",false);
            this.callFunction("UI|close|setVisible",false);
            //���CASE_NO��ֵ ��ʾҽ��վ���ô�Ⱦ�����濨 CASE_NO�����޸�
            if(this.getValueString("CASE_NO").trim().length()>0){
                this.callFunction("UI|CASE_NO|setEnabled",false);
                this.callFunction("UI|IPD_NO|setEnabled",false);
            }
        }
        //����� ����ʷ��ѯ����ť���� ��ô�½���ť���� ʹ��
        if("H".equals(TYPE)){
            this.callFunction("UI|new|setVisible",false);
        }
        queryData();
    }
    /**
     * ��ѯ
     */
    public void onQuery(){
        queryData();
    }
    /**
     * ����������ѯ��Ⱦ�����濨��Ϣ
     */
    public void queryData(){
        TParm parm = new TParm();
        if(this.getValueString("MR_NO").trim().length()>0){
            String MR_NO = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
            parm.setData("MR_NO",MR_NO);
            this.setValue("MR_NO",MR_NO);
        }
        if(this.getValueString("CASE_NO").trim().length()>0){
            parm.setData("CASE_NO",this.getValue("CASE_NO"));
        }
        if(this.getValueString("IPD_NO").trim().length()>0){
            parm.setData("IPD_NO",this.getValue("IPD_NO"));
        }
        if(this.getValue("FIRST_FLG")!=null&&!"".equals(this.getValue("FIRST_FLG"))){
            parm.setData("FIRST_FLG",this.getValue("FIRST_FLG"));
        }
        if(this.getValueString("PAT_NAME").trim().length()>0){
            parm.setData("PAT_NAME",this.getValue("PAT_NAME"));
        }
        if(!(this.getValue("DATE_S")==null)&&!(this.getValue("DATE_E")==null)){
            parm.setData("DATE_S",this.getText("DATE_S").replace("/","")+"000000");
            parm.setData("DATE_E",this.getText("DATE_E").replace("/","")+"235959");
        }
        if(this.getValueString("ICD_CODE").length()>0){
            parm.setData("ICD_CODE",this.getValue("ICD_CODE"));
        }
        if(this.getValueString("DISEASETYPE_CODE").length()>0){
            parm.setData("DISEASETYPE_CODE",this.getValueString("DISEASETYPE_CODE"));
        }
        if(this.getValueString("ADM_TYPE").length()>0){
            parm.setData("ADM_TYPE",this.getValue("ADM_TYPE"));
        }
        TParm result = MROInfectTool.getInstance().selectInfect(parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        Table.setParmValue(result);
    }
    /**
     * ���
     */
    public void onClear(){
        if("H".equals(TYPE)){
            this.clearValue("MR_NO;CASE_NO");
        }
        this.clearValue("FIRST_FLG;PAT_NAME;DATE_S;DATE_E;ICD_CODE;ICD_DESC;DISEASETYPE_CODE;ADM_TYPE");
        Table.removeRowAll();
    }
    /**
     * ģ����ѯ���ڲ��ࣩ
     */
    public class OrderList extends TLabel {
        TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("ICD_CODE");
            Vector d = (Vector) parm.getData("ICD_CHN_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i)))
                    return "" + d.get(i);
            }
            return s;
        }
    }
    /**
     * �ش�ѡ��ֵ�¼�
     */
    public void onBack(){
        int rowIndex = Table.getSelectedRow();
        if(rowIndex<0){
            this.messageBox_("��ѡ��һ�����濨��Ϣ!");
            return;
        }
        TParm result = Table.getParmValue();
        TParm parm = new TParm();
        parm.setData("MR_NO",result.getValue("MR_NO",rowIndex));
        parm.setData("CASE_NO",result.getValue("CASE_NO",rowIndex));
        parm.setData("CARD_SEQ_NO",result.getValue("CARD_SEQ_NO",rowIndex));
        parm.setData("SAVE_FLG","UPDATE");//���ر���״̬ Ϊ �޸�
        this.setReturnValue(parm);
        returnType = true;//��ʾѡ���˻ش�ֵ
        this.closeWindow();
    }
    /**
     * �½���ť�¼�
     */
    public void onNew(){
        TParm parm = new TParm();
        parm.setData("SAVE_FLG","NEW");//���ر���״̬ Ϊ  �½�
        this.setReturnValue(parm);
        this.closeWindow();
    }
    /**
     * ���ڹر�ʱ����
     * @return boolean
     */
    public boolean onClosing(){
        if(!returnType){//���û��ѡ�лش�ֵ
            //������ҽ��վ���� ��ѯָ��������ʱ�� ���ر���״̬Ϊ ����
            if ("M".equals(TYPE)) {
                TParm parm = new TParm();
                parm.setData("SAVE_FLG", "NEW"); //���ر���״̬ Ϊ  �½�
                this.setReturnValue(parm);
            }
        }
        return true;
    }
    /**
     * ����¼�
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        this.setValue("ICD_CODE", parm.getValue("ICD_CODE"));
        this.setValue("ICD_DESC", parm.getValue("ICD_CHN_DESC"));
    }
}
