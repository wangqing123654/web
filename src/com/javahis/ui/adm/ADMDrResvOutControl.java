package com.javahis.ui.adm;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.adm.ADMTool;
import jdo.sys.SystemTool;
import jdo.adm.ADMInpTool;
import javax.swing.SwingUtilities;
import jdo.adm.ADMDrResvOutTool;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;

/**
 * <p>Title: ��Ժ֪ͨ</p>
 *
 * <p>Description: ��Ժ֪ͨ</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk  2010-2-25
 * @version 4.0
 */
public class ADMDrResvOutControl
    extends TControl {
    String CASE_NO = "";//��¼�������
    TParm admInfo;//������Ժ��Ϣ
    boolean IS_MEDDISCH = false;//��¼�Ƿ��Ѿ��´�סԺ֪ͨ
    public void onInit(){
        super.onInit();
        onPageInit();
    }
    /**
     * ҳ���ʼ��
     */
    private void onPageInit(){
        Object obj = this.getParameter();
        if(obj == null){
            this.messageBox_("��������Ϊ��");
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        closeW();//�رմ���
                    }
                    catch (Exception e) {
                    }
                }
            });
            return;
        }
        if(obj instanceof String){
            CASE_NO = (String)obj;
        }
        //��ѯ������Ժ��Ϣ
        TParm admParm = new TParm();
        admParm.setData("CASE_NO",CASE_NO);
        admInfo = ADMTool.getInstance().getADM_INFO(admParm);
        if(admInfo.getData("MEDDISCH_DATE",0)!=null){
            IS_MEDDISCH = true;
        }
        //��ѯ���� ҽ�Ƴ�Ժ���� ���������������ֵ ���򸳵�ǰʱ��
        if(admInfo.getValue("MEDDISCH_DATE",0).length()>0){
            this.setValue("MEDDISCH_DATE",admInfo.getData("MEDDISCH_DATE",0));
        }else{
            this.setValue("MEDDISCH_DATE",SystemTool.getInstance().getDate());
        }
        if(admInfo.getValue("DISCH_CODE",0).length()>0){
            this.setValue("DISCH_CODE",admInfo.getValue("DISCH_CODE",0));
        }
        //��ѯ���������Ϣ
        TParm diagParm = new TParm();
        diagParm.setData("IO_TYPE","O");//��Ժ���
        diagParm.setData("CASE_NO",CASE_NO);
        diagParm.setData("MAINDIAG_FLG","Y");//�����
        TParm diagInfo = ADMDrResvOutTool.getInstance().selectDiag(diagParm);
        if(diagInfo.getCount()>0){
            this.setValue("OUT_ICD",diagInfo.getValue("ICD_CODE",0));
            this.setValue("OUT_DESC",diagInfo.getValue("ICD_CHN_DESC",0));
        }
    }
    /**
     * ����
     */
    public void onSave(){
        if(this.getValueString("OUT_ICD").length()<=0){
            this.messageBox_("������д�ò����ĳ�Ժ���");
            return;
        }
        if(this.getValue("MEDDISCH_DATE")==null){
            this.messageBox_("��ѡ��Ԥ����Ժ����");
            this.grabFocus("MEDDISCH_DATE");
            return;
        }
        if(this.getValueString("DISCH_CODE").length()<=0){
            this.messageBox_("��ѡ���Ժ���");
            this.grabFocus("DISCH_CODE");
            return;
        }
        TParm parm = new TParm();
        parm.setData("MEDDISCH_DATE",this.getValue("MEDDISCH_DATE"));
        parm.setData("DISCH_CODE",this.getValue("DISCH_CODE"));
        parm.setData("CASE_NO",CASE_NO);
        TParm re = ADMInpTool.getInstance().updateMEDDISCH_DATE(parm);
        if(re.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        IS_MEDDISCH = true;
        this.messageBox("P0005");
    }
    /**
     * ���
     */
    public void onClear(){
        this.clearValue("DISCH_CODE");
        this.setValue("MEDDISCH_DATE",SystemTool.getInstance().getDate());
    }
    /**
     * ��ӡ
     */
    public void onPrint(){
        if(!IS_MEDDISCH){
            this.messageBox_("��δ�´��Ժ֪ͨ");
            return;
        }
        //��ѯ��ӡ��Ϣ
        TParm print = ADMDrResvOutTool.getInstance().selectPrintInfo(CASE_NO);
        //��ѯ��Ժ�����
        TParm inDiagParm = new TParm();
        inDiagParm.setData("IO_TYPE","M");//��Ժ���
        inDiagParm.setData("CASE_NO",CASE_NO);
        inDiagParm.setData("MAINDIAG_FLG","Y");//�����
        TParm inDiag = ADMDrResvOutTool.getInstance().selectDiag(inDiagParm);
        //��ѯ��Ժ�����
        TParm outDiagParm = new TParm();
        outDiagParm.setData("IO_TYPE","O");//��Ժ���
        outDiagParm.setData("CASE_NO",CASE_NO);
        outDiagParm.setData("MAINDIAG_FLG","Y");//�����
        TParm outDiag = ADMDrResvOutTool.getInstance().selectDiag(outDiagParm);
        TParm printData = new TParm();
        printData.setData("MR_NO","TEXT",admInfo.getValue("MR_NO",0));
        printData.setData("IPD_NO","TEXT",admInfo.getValue("IPD_NO",0));
        printData.setData("CTZ1","TEXT",print.getValue("CTZ_DESC",0));//���ʽ
        printData.setData("PAT_NAME","TEXT",print.getValue("PAT_NAME",0));//��������
        printData.setData("SEX","TEXT",print.getValue("CHN_DESC",0));//�Ա�
        printData.setData("BIRTHDAY","TEXT",StringTool.getString(print.getTimestamp("BIRTH_DATE",0),"yyyy��MM��dd��"));//����
        printData.setData("DEPT","TEXT",print.getValue("DEPT_CHN_DESC",0));//����
        printData.setData("IN_DATE","TEXT",StringTool.getString(print.getTimestamp("IN_DATE",0),"yyyy��MM��dd��HHʱ"));//��Ժʱ��
        System.out.println("print"+print.getTimestamp("MEDDISCH_DATE",0));
        printData.setData("MEDDISCH_DATE","TEXT",StringTool.getString(
        		print.getTimestamp("MEDDISCH_DATE",0),"yyyy��MM��dd��"));//Ԥ����Ժ����xiongwg20150130
        //����סԺ����
        //int inDays = StringTool.getDateDiffer(SystemTool.getInstance().getDate(),print.getTimestamp("IN_DATE",0));
        //$$ add by lx  this.getValue("MEDDISCH_DATE")
        //System.out.println("==MEDDISCH_DATE=="+this.getValue("MEDDISCH_DATE"));
       // Timestamp outdate=StringTool.getTimestamp((String)this.getValue("MEDDISCH_DATE"), "yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//
        String strOut = df.format((Timestamp)this.getValue("MEDDISCH_DATE"));
        //System.out.println("==strOut=="+strOut);
        strOut=strOut.substring(0, strOut.indexOf(" "))+" "+"23:59:59";
        //this.messageBox("==strOut=="+strOut);
        Timestamp tOut = Timestamp.valueOf(strOut.replaceAll("/", "-"));

        int inDays = StringTool.getDateDiffer(tOut,print.getTimestamp("IN_DATE",0));
       //this.messageBox("inDays"+inDays);
        
        printData.setData("IN_DAYS","TEXT",inDays==0?"1":inDays +" ��");
        printData.setData("PAT_HEAD","TEXT",print.getValue("PAT_NAME",0) + " ����/����");
        //��Ժ��Ϻͳ�Ժ���Ϊ�Զ������ʱ����ע��Ϊ������� modify by huangjw 20150126 start
        if(!" ".equals(inDiag.getValue("ICD_CHN_DESC",0)) && !"".equals(inDiag.getValue("ICD_CHN_DESC",0)) && inDiag.getValue("ICD_CHN_DESC",0)!=null){
        	printData.setData("IN_DESC","TEXT",inDiag.getValue("ICD_CHN_DESC",0));
    	}else{
    		printData.setData("IN_DESC","TEXT",inDiag.getValue("DESCRIPTION",0));
    	}
        if(!" ".equals(outDiag.getValue("ICD_CHN_DESC",0)) && !"".equals(outDiag.getValue("ICD_CHN_DESC",0)) && outDiag.getValue("ICD_CHN_DESC",0)!=null){
        	printData.setData("OUT_DESC","TEXT",outDiag.getValue("ICD_CHN_DESC",0));
        }else{
        	printData.setData("OUT_DESC","TEXT",outDiag.getValue("DESCRIPTION",0));
        }
        //��Ժ��Ϻͳ�Ժ���Ϊ�Զ������ʱ����ע��Ϊ������� modify by huangjw 20150126 end
        printData.setData("HOSP","TEXT",Operator.getHospitalCHNFullName());
        this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMDrResvOut.jhw",printData);
    }
    /**
     * �رմ���
     */
    private void closeW(){
        this.closeWindow();
    }
}
