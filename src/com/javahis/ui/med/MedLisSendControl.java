package com.javahis.ui.med;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.hl7.Hl7Communications;
import jdo.med.MedLisSendTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p> Title:�����ͼ��嵥 </p>
 * 
 * <p> Description: �����ͼ��嵥 </p>
 * 
 * <p> Copyright: Copyright (c) 2012 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 * @author shibl
 * @version 1.0
 */
public class MedLisSendControl extends TControl {//wanglong refactor 20140422
    private TTable table;
    private BILComparator compare = new BILComparator();
    private boolean ascending = false;
    private int sortColumn = -1;
	private TParm sendHL7Parm = new TParm();// �����HL7�ӿڵ�����
	private String admType;
	
	/**
	 * ��ʼ��
	 */
    public void onInit() {
        super.onInit();
        table=(TTable)this.getComponent("TABLE");
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                                "onCheckBoxValue");
        addListener(table);// �������
        this.onClear();
        this.setValue("LIS_RE_USER", Operator.getID());
        Object obj = this.getParameter();// ���
        if (obj != null) {
            if (obj instanceof String) {
                admType = this.getParameter().toString();
                callFunction("UI|" + admType + "|setSelected", true);
                onChooseAdmType(admType);// ����onClear()
                if (admType.equalsIgnoreCase("OE")) {
                    callFunction("UI|I|setEnabled", false);
                } else if (admType.equalsIgnoreCase("I")) {
                    callFunction("UI|OE|setEnabled", false);
                }
            }
        }
        this.setValue("LIS_RE_USER", Operator.getID());
    }

	/**
	 * ����Żس��¼�
	 */
    public void onMedApplyNo() {
        TParm parm = new TParm();
        String medApplyNo = this.getValueString("MED_APPLY_NO");// �����
        String lis_re_user=this.getValueString("LIS_RE_USER");
        String send_user=this.getValueString("SEND_USER");
        parm.setData("MED_APPLY_NO", medApplyNo);
        boolean reFlag = (Boolean) this.callFunction("UI|RE_YES|isSelected"); // δ����
        parm.setData("RE_FLG", reFlag);
        TParm result = execQuery(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount() <= 0) {
            this.messageBox("û������");
            return;
        }
        if (table.getRowCount() > 0) {
            boolean flg = true;
            TParm tableParm = table.getParmValue();
            if(result.getCount() == 1){//���뵥������Ž����ж�
	            for (int i = 0; i < tableParm.getCount("MED_APPLY_NO"); i++) {
	                if (tableParm.getValue("MED_APPLY_NO", i).equals(result.getValue("MED_APPLY_NO", 0))) {
	                    this.messageBox("��ɨ������룡");
	                    flg = false;
	                    break;
	                }
	            }
            }else{//�����Ϊ��ʱ�س��������ʾtable��ʣ��Ĳ���
            	TParm newParm=new TParm();
            	boolean newflg=true;
            	//int k=0;
            	for(int j = 0;j<result.getCount();j++){
            		for(int i = 0;i<tableParm.getCount("MED_APPLY_NO");i++){
            			if(tableParm.getValue("MED_APPLY_NO", i).equals(result.getValue("MED_APPLY_NO", j))){
            				newflg=false;
            				break;
            			}
            		}
            		if(newflg){
            			newParm.addRowData(result,j);
            			//System.out.println("tt-----------------------------tttt---------------------------tt"+newParm);
            			//k++;
            		}
            		newflg=true;
            	}
            	if(newParm.getCount()<0){
            		this.messageBox("�Ѿ���ʾȫ����Ϣ");
            		flg=false;
            	}else
            		result=newParm;
            }
            if (flg) {
            	if(getRadioButton("RE_NO").isSelected()){
            		for(int i=0;i<result.getCount();i++){
		            	result.setData("LIS_RE_USER",i, lis_re_user);
		            	result.setData("SEND_USER", i,send_user);
            		}
            	}
                tableParm.addParm(result);
                table.setParmValue(tableParm);
            	
            }
        } else {
        	if(getRadioButton("RE_NO").isSelected()){
        		for(int i=0;i<result.getCount();i++){
	            	result.setData("LIS_RE_USER",i, lis_re_user);
	            	result.setData("SEND_USER",i, send_user);
        		}
        	}
            table.setParmValue(result);
        }
        this.setValue("MED_APPLY_NO", "");
    }
	
	/**
     * ��ѯ
     */
    public void onQuery() {
        if(this.getValue("START_DATE")==null){
            this.messageBox("��ѡ��ʼ����");
            return;
        }
        if(this.getValue("END_DATE")==null){
            this.messageBox("��ѡ���������");
            return;
        }
        TParm parm = new TParm();
        String clinicAreaCode = this.getValueString("CLINICAREA_CODE"); // ����
        String stationCode = this.getValueString("STATION_CODE"); // ����
        boolean reFlag = (Boolean) this.callFunction("UI|RE_YES|isSelected"); // �ѽ���
        String startDate =
                StringTool.getString((Timestamp) this.getValue("START_DATE"), "yyyyMMddHHmmss");// ��ʼʱ��
        String endDate =
                StringTool.getString((Timestamp) this.getValue("END_DATE"), "yyyyMMddHHmmss"); // ����ʱ��
      
        String reStartDate =
                StringTool.getString((Timestamp) this.getValue("RE_START_DATE"), "yyyyMMddHHmmss");// ��������
        String reEndDate =
                StringTool.getString((Timestamp) this.getValue("RE_END_DATE"), "yyyyMMddHHmmss"); // ��������
        String lisReUser = this.getValueString("LIS_RE_USER"); // �ͼ���Ա
        String medApplyNo = this.getValueString("MED_APPLY_NO");// �����
        String send_user = this.getValueString("SEND_USER");//������Ա
        parm.setData("CLINICAREA_CODE", clinicAreaCode);
        parm.setData("STATION_CODE", stationCode);
        parm.setData("RE_FLG", reFlag);
        if (!reFlag) {
            parm.setData("START_DATE", startDate);
            parm.setData("END_DATE", endDate);
        } else {
            parm.setData("RE_START_DATE", reStartDate);
            parm.setData("RE_END_DATE", reEndDate);
        }
        parm.setData("LIS_RE_USER", lisReUser);
        parm.setData("SEND_USER",send_user);
        parm.setData("MED_APPLY_NO", medApplyNo);
        TParm result = execQuery(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount() <= 0) {
            this.messageBox("û������");
            table.setParmValue(new TParm());
            return;
        }
        if(getRadioButton("RE_NO").isSelected()){
	        for(int i=0;i<result.getCount();i++){
		        //if("".equals(result.getValue("LIS_RE_USER",i)))
		        	//result.setData("LIS_RE_USER",i,Operator.getID());
	        }
        }
        table.setParmValue(result);
        this.setValue("ALL", "Y");
    }
    
	/**
     * ִ�в�ѯ
     */
    public TParm execQuery(TParm parm) {
        TParm result = new TParm();
        if ((Boolean) callFunction("UI|OE|isSelected") == true) {// �ż���
            result = MedLisSendTool.getInstance().selectLisDetailByOE(parm);
        } else {// סԺ
            result = MedLisSendTool.getInstance().selectLisDetailByI(parm);
        }
        return result;
    }

	/**
	 * ���涯��
	 */
	public void onSave() {
		table.acceptText();
		TParm parmValue = table.getParmValue();
        if (parmValue == null) {
            this.messageBox("�ޱ������ݣ�");
            return;
        }
        TParm parm = new TParm();
        Timestamp now = SystemTool.getInstance().getDate();
        for (int i = 0; i < parmValue.getCount(); i++) {
            if (!parmValue.getBoolean("FLG", i)) {
                continue;
            }
//            if (StringUtil.isNullString(parmValue.getValue("LIS_RE_USER", i))) {
//                continue;
//            }
//            if (StringUtil.isNullString(parmValue.getValue("SEND_USER", i))) {
//                continue;
//            }
            parm.addData("CASE_NO", parmValue.getValue("CASE_NO", i));
            parm.addData("MED_APPLY_NO", parmValue.getValue("MED_APPLY_NO", i));
            parm.addData("LIS_RE_USER",parmValue.getValue("LIS_RE_USER",i));
            parm.addData("SEND_USER", parmValue.getValue("SEND_USER",i));//add by huangjw 20140718
            parm.addData("LIS_RE_DATE", now);
            parm.addData("OPT_USER", Operator.getID());
            parm.addData("OPT_TERM", Operator.getIP());
            sendHL7Parm.addData("CASE_NO", parmValue.getValue("CASE_NO", i));
            sendHL7Parm.addData("PAT_NAME", parmValue.getValue("PAT_NAME", i));
            sendHL7Parm.addData("LAB_NO", parmValue.getValue("MED_APPLY_NO", i));
            sendHL7Parm.addData("CAT1_TYPE", parmValue.getValue("CAT1_TYPE", i));
            if ((Boolean) callFunction("UI|OE|isSelected") == true) {// �ż���
                parm.addData("RX_NO", parmValue.getValue("RX_NO", i));
                parm.addData("SEQ_NO", parmValue.getInt("SEQ_NO", i));
                sendHL7Parm.addData("ORDER_NO", parmValue.getValue("RX_NO", i));
                sendHL7Parm.addData("SEQ_NO", parmValue.getInt("SEQ_NO", i));
            } else {// סԺ
                parm.addData("ORDER_NO", parmValue.getValue("ORDER_NO", i));
                parm.addData("ORDER_SEQ", parmValue.getInt("ORDER_SEQ", i));
                parm.addData("START_DTTM", parmValue.getValue("START_DTTM", i));
                sendHL7Parm.addData("ORDER_NO", parmValue.getValue("ORDER_NO", i));
                sendHL7Parm.addData("SEQ_NO", parmValue.getInt("ORDER_SEQ", i));
            }
        }
        parm.setCount(parm.getCount("CASE_NO"));
        sendHL7Parm.setCount(sendHL7Parm.getCount("CASE_NO"));
        TParm result = new TParm();
        result = MedLisSendTool.getInstance().updateMedApplyLisData(parm);//����MED_APPLY�Ľ�����Ա��Ϣ
        if (result.getErrCode() < 0) {
            this.messageBox("����ʧ��1��" + result.getErrText());
            return;
        }
        if ((Boolean) callFunction("UI|I|isSelected") == true) {// סԺ
            result = MedLisSendTool.getInstance().updateOdidspnmLisData(parm);//����ODI_DSPNM�Ľ�����Ա��Ϣ
            if (result.getErrCode() < 0) {
                this.messageBox("����ʧ��2��" + result.getErrText());
                return;
            }
        }
        this.messageBox("����ɹ���");
		sendHL7Msg(sendHL7Parm);//����HL7��Ϣ
		savePrint();
		table.removeRowAll();
	}

	/**
	 * ��Ϣ����
	 */
    public void onReSendHL7() {
        TParm parmValue = table.getParmValue();
        for (int i = 0; i < parmValue.getCount(); i++) {
            if (!parmValue.getBoolean("FLG", i)) continue;
            sendHL7Parm.addData("CASE_NO", parmValue.getValue("CASE_NO", i));
            sendHL7Parm.addData("PAT_NAME", parmValue.getValue("PAT_NAME", i));
            sendHL7Parm.addData("LAB_NO", parmValue.getValue("MED_APPLY_NO", i));
            sendHL7Parm.addData("CAT1_TYPE", parmValue.getValue("CAT1_TYPE", i));
            if ((Boolean) callFunction("UI|OE|isSelected") == true) {// �ż���
                sendHL7Parm.addData("ORDER_NO", parmValue.getValue("RX_NO", i));
                sendHL7Parm.addData("SEQ_NO", parmValue.getInt("SEQ_NO", i));
            } else {// סԺ
                sendHL7Parm.addData("ORDER_NO", parmValue.getValue("ORDER_NO", i));
                sendHL7Parm.addData("SEQ_NO", parmValue.getInt("ORDER_SEQ", i));
            }
        }
        sendHL7Msg(sendHL7Parm);
    }

    /**
     * ����HL7��Ϣ
     * @param hl7Parm
     */
    public void sendHL7Msg(TParm hl7Parm) {
        if (hl7Parm.getCount() <= 0) {
            return;
        }
        List list = new ArrayList();
        for (int i = 0; i < hl7Parm.getCount(); i++) {
            if (hl7Parm.getValue("MED_APPLY_NO", i).equals("")) {
                continue;
            }
            list.add(hl7Parm.getRow(i));
        }
        TParm resultParm = Hl7Communications.getInstance().Hl7SendLis(list); // ���ýӿ�
        if (resultParm.getErrCode() < 0) {
            this.messageBox(resultParm.getErrText());
            return;
        }
        this.messageBox("���ͳɹ�");
    }

    /**
     * ѡ��������ѡ��ť�¼�
     */
    public void onChooseAdmType(String tag) {//wanglong add 20140422
        this.onClear();
        if (tag.equals("OE")) {// �ż���
            callFunction("UI|CLINICAREA_CODE|setEnabled", true);
            callFunction("UI|STATION_CODE|setEnabled", false);
            table.setHeader("ѡ,35,boolean;������,120;����,120;�����,120;��Ŀ����,200;��Ѫʱ��,150,timestamp,yyyy/MM/dd HH:mm:ss;ҽ����ע,150;����ʱ��,150,timestamp,yyyy/MM/dd HH:mm:ss;�ͼ���Ա,100,LIS_RE_USER;������Ա,100");
            table.setParmMap("FLG;MR_NO;PAT_NAME;MED_APPLY_NO;ORDER_DESC;BLOOD_DATE;DR_NOTE;LIS_RE_DATE;LIS_RE_USER;SEND_USER;CASE_NO;RX_NO;SEQ_NO");
            table.setColumnHorizontalAlignmentData("1,left;3,left;5,left;7,left;9,left");
            table.setLockColumns("1,2,3,4,5,6,7");
        } else if (tag.equals("I")) {// סԺ
            callFunction("UI|CLINICAREA_CODE|setEnabled", false);
            callFunction("UI|STATION_CODE|setEnabled", true);
            table.setHeader("ѡ,35,boolean;����,70;������,120;����,120;�����,120;��Ŀ����,200;��ʿִ��ʱ��,150,timestamp,yyyy/MM/dd HH:mm:ss;ҽ����ע,150;����ʱ��,150,timestamp,yyyy/MM/dd HH:mm:ss;�ͼ���Ա,100,LIS_RE_USER;������Ա,100");
            table.setParmMap("FLG;BED_NO;MR_NO;PAT_NAME;MED_APPLY_NO;ORDER_DESC;NS_EXEC_DATE;DR_NOTE;LIS_RE_DATE;LIS_RE_USER;SEND_USER;CASE_NO;RX_NO;SEQ_NO");
            table.setColumnHorizontalAlignmentData("1,left;3,left;5,left;7,left;9,left");
            table.setLockColumns("1,2,3,4,5,6,7,8");
        }
    }
    
	/**
	 * �ı��¼�
	 */
	public void onChooseREState() {
        if ((Boolean) this.callFunction("UI|RE_YES|isSelected")==true) {// �ѽ���
            callFunction("UI|save|setEnabled", false);
            callFunction("UI|print|setEnabled", true);
            callFunction("UI|RE_START_DATE|setEnabled", true);
            callFunction("UI|RE_END_DATE|setEnabled", true);
            Timestamp sysDate = SystemTool.getInstance().getDate();
            String tDate = StringTool.getString(sysDate, "yyyyMMdd");
            this.setValue("RE_START_DATE",
                          StringTool.getTimestamp(tDate + "000000", "yyyyMMddHHmmss")); // Ĭ��������ʼ����
            this.setValue("RE_END_DATE", StringTool.getTimestamp(("" + sysDate).substring(0, 19)
                    .replaceAll("-", "").replaceAll(":", ""), "yyyyMMdd HHmmss")); // Ĭ��������ֹ����
            table.removeRowAll();
        } else {
            callFunction("UI|save|setEnabled", true);
            callFunction("UI|print|setEnabled", false);
            callFunction("UI|RE_START_DATE|setEnabled", false);
            callFunction("UI|RE_END_DATE|setEnabled", false);
            this.setValue("RE_START_DATE", "");
            this.setValue("RE_END_DATE", "");
            table.removeRowAll(); 
        }
    }
	
	/**
	 * ������ӡ
	 */
	public void savePrint(){
		if (table.getRowCount() <= 0) {
            this.messageBox("�޴�ӡ����");
            return;
        }
		TParm parm = table.getParmValue();
        TParm printData = new TParm();
        int count = 0;
        for (int i = 0; i < parm.getCount("MR_NO"); i++) {
            if (!parm.getBoolean("FLG", i)) continue;
            printData.addData("MR_NO", parm.getValue("MR_NO", i));
            printData.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
            printData.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
            printData.addData("MED_APPLY_NO", parm.getValue("MED_APPLY_NO", i));
//            printData
//                    .addData("LIS_RE_DATE",
//                             StringTool.getString(parm.getTimestamp("LIS_RE_DATE", i), "HH:mm:ss"));
            printData.addData("DR_NOTE", parm.getValue("DR_NOTE", i));
            String lis_re_user="";
            if ((Boolean) callFunction("UI|OE|isSelected") == true) {// �ż���
            	String sql="SELECT A.LIS_RE_DATE ,B.USER_NAME FROM MED_APPLY A,SYS_OPERATOR B" +
            			" WHERE A.APPLICATION_NO='"+parm.getValue("MED_APPLY_NO",i)+"' AND A.CAT1_TYPE='LIS' AND A.LIS_RE_USER=B.USER_ID ";
            	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
            	lis_re_user=result.getValue("USER_NAME",0);
            	printData.addData("LIS_RE_DATE", StringTool.getString(result.getTimestamp("LIS_RE_DATE",0), "HH:mm:ss"));
                printData.addData("BLOOD_DATE", StringTool.getString(parm
                        .getTimestamp("BLOOD_DATE", i), "HH:mm:ss"));
            } else {// סԺ
            	String sql="SELECT A.LIS_RE_DATE,C.USER_NAME FROM ODI_DSPNM A, ODI_ORDER B,SYS_OPERATOR C " +
            		   " WHERE  B.MED_APPLY_NO = '"+parm.getValue("MED_APPLY_NO",i)+"' " +
            		   " AND A.CASE_NO = B.CASE_NO" +
                       " AND A.ORDER_NO = B.ORDER_NO" +
                       " AND A.ORDER_SEQ = B.ORDER_SEQ"+
                       " AND A.LIS_RE_USER=C.USER_ID"; 
            	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
            	lis_re_user=result.getValue("USER_NAME",0);
            	
            	//System.out.println("........................result......................"+result);
            	//System.out.println("result===="+StringTool.getString(result.getTimestamp("LIS_RE_DATE",0), "HH:mm:ss"));
            	printData.addData("LIS_RE_DATE", StringTool.getString(result.getTimestamp("LIS_RE_DATE",0), "HH:mm:ss"));
                printData.addData("BED_NO", parm.getValue("BED_NO", i));
                printData.addData("NS_EXEC_DATE", StringTool.getString(parm
                        .getTimestamp("NS_EXEC_DATE", i), "HH:mm:ss"));
                
            }
            printData.addData("LIS_RE_USER",lis_re_user);
            printData.addData("SEND_USER",parm.getValue("SEND_USER",i));
            count++;
        }
        printData.setCount(count);
        if ((Boolean) callFunction("UI|OE|isSelected") == true) {// �ż���
            printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
            printData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printData.addData("SYSTEM", "COLUMNS", "MED_APPLY_NO");
            printData.addData("SYSTEM", "COLUMNS", "BLOOD_DATE");
            printData.addData("SYSTEM", "COLUMNS", "LIS_RE_DATE");
            printData.addData("SYSTEM", "COLUMNS", "DR_NOTE");
            printData.addData("SYSTEM", "COLUMNS", "MR_NO");
            printData.addData("SYSTEM", "COLUMNS", "LIS_RE_USER");
            printData.addData("SYSTEM", "COLUMNS", "SEND_USER");
        } else {// סԺ
            printData.addData("SYSTEM", "COLUMNS", "BED_NO");
            printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
            printData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printData.addData("SYSTEM", "COLUMNS", "MED_APPLY_NO");
            printData.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
            printData.addData("SYSTEM", "COLUMNS", "LIS_RE_DATE");
            printData.addData("SYSTEM", "COLUMNS", "DR_NOTE");
            printData.addData("SYSTEM", "COLUMNS", "MR_NO");
            printData.addData("SYSTEM", "COLUMNS", "LIS_RE_USER");
            printData.addData("SYSTEM", "COLUMNS", "SEND_USER");
        }
        TParm printParm = new TParm();
        printParm.setData("TITLE", "TEXT", "����걾�ͼ��嵥");
        if ((Boolean) callFunction("UI|OE|isSelected") == true) {// �ż���
            printParm.setData("CLINICAREA_CODE", "TEXT", "������" +((TComboBox)this.getComponent("CLINICAREA_CODE")).getSelectedName() );
        } else {// סԺ
            printParm.setData("STATION_CODE", "TEXT", "������" + this.getText("STATION_CODE"));
        }
        Timestamp reStartDate = (Timestamp) this.getValue("START_DATE"); // ��ʼʱ��
        Timestamp reEndDate = (Timestamp) this.getValue("END_DATE"); // ����ʱ��
        printParm
                .setData("DATE", "TEXT",
                         "�������գ�" + StringTool.getString(reStartDate, "yyy/MM/dd HH:mm:ss") + " "
                                 + "�������գ�" + StringTool.getString(reEndDate, "yyy/MM/dd HH:mm:ss"));
        printParm.setData("TABLE", printData.getData());
        
        if ((Boolean) callFunction("UI|OE|isSelected") == true) {// �ż���
        	String prtSwitch=IReportTool.getInstance().getPrintSwitch("MedOpdLisSend_V45.prtSwitch");
            String previewSwitch=IReportTool.getInstance().getPrintSwitch("MedOpdLisSend_V45.previewSwitch");
        	if(prtSwitch.equals(IReportTool.ON)){
				if (previewSwitch.equals(IReportTool.ON)) {
					this.openPrintWindow(IReportTool.getInstance()
							.getReportPath("MedOpdLisSend_V45.jhw"),
							IReportTool.getInstance().getReportParm("MedOpdLisSend_V45.class", printParm));
				}else{
					this.openPrintWindow(IReportTool.getInstance()
							.getReportPath("MedOpdLisSend_V45.jhw"),
							IReportTool.getInstance().getReportParm("MedOpdLisSend_V45.class", printParm),true);
				}
        	}
        } else {// סԺ
        	String prtSwitch=IReportTool.getInstance().getPrintSwitch("MedLisSend_V45.prtSwitch");
        	String previewSwitch=IReportTool.getInstance().getPrintSwitch("MedLisSend_V45.previewSwitch");
        	if(prtSwitch.equals(IReportTool.ON)){
        		if (previewSwitch.equals(IReportTool.ON)) {
	        		this.openPrintWindow(IReportTool.getInstance().getReportPath("MedLisSend_V45.jhw"),
	        				IReportTool.getInstance().getReportParm("MedLisSend_V45.class",printParm));
        		}else{
        			this.openPrintWindow(IReportTool.getInstance()
							.getReportPath("MedLisSend_V45.jhw"),
							IReportTool.getInstance().getReportParm("MedLisSend_V45.class", printParm),true);
        		}
        	}
        }
	}
	/**
	 * ��ӡ����
	 */
    public void onPrint() {
        if (table.getRowCount() <= 0) {
            this.messageBox("�޴�ӡ����");
            return;
        }
        TParm parm = table.getParmValue();
        TParm printData = new TParm();
        int count = 0;
        for (int i = 0; i < parm.getCount("MR_NO"); i++) {
        	String lis_re_user="";
            if (!parm.getBoolean("FLG", i)) continue;
            printData.addData("MR_NO", parm.getValue("MR_NO", i));
            printData.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
            printData.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
            printData.addData("MED_APPLY_NO", parm.getValue("MED_APPLY_NO", i));
            printData
                    .addData("LIS_RE_DATE",
                             StringTool.getString(parm.getTimestamp("LIS_RE_DATE", i), "HH:mm:ss"));
            printData.addData("DR_NOTE", parm.getValue("DR_NOTE", i));
            if ((Boolean) callFunction("UI|OE|isSelected") == true) {// �ż���
                printData.addData("BLOOD_DATE", StringTool.getString(parm
                        .getTimestamp("BLOOD_DATE", i), "HH:mm:ss"));
            } else {// סԺ
                printData.addData("BED_NO", parm.getValue("BED_NO", i));
                printData.addData("NS_EXEC_DATE", StringTool.getString(parm
                        .getTimestamp("NS_EXEC_DATE", i), "HH:mm:ss"));
            }
            if ((Boolean) callFunction("UI|OE|isSelected") == true) {
            	String sql="SELECT B.USER_NAME FROM MED_APPLY A,SYS_OPERATOR B " +
            			" WHERE A.APPLICATION_NO='"+parm.getValue("MED_APPLY_NO",i)+"' AND A.CAT1_TYPE='LIS' AND A.LIS_RE_USER=B.USER_ID ";
            	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
            	lis_re_user=result.getValue("USER_NAME",0);
            }else{
            	String sql="SELECT A.LIS_RE_DATE,C.USER_NAME FROM ODI_DSPNM A, ODI_ORDER B,SYS_OPERATOR C " +
     		    " WHERE  B.MED_APPLY_NO = '"+parm.getValue("MED_APPLY_NO",i)+"' " +
     		    " AND A.CASE_NO = B.CASE_NO" +
                " AND A.ORDER_NO = B.ORDER_NO" +
                " AND A.ORDER_SEQ = B.ORDER_SEQ"+
                " AND A.LIS_RE_USER=C.USER_ID"; 
            	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
            	lis_re_user=result.getValue("USER_NAME",0);
            }
            printData.addData("LIS_RE_USER",lis_re_user);
            printData.addData("SEND_USER",parm.getValue("SEND_USER",i));
            //System.out.println("-----printData-----"+printData);
            count++;
        }
        printData.setCount(count);
        if ((Boolean) callFunction("UI|OE|isSelected") == true) {// �ż���
            printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
            printData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printData.addData("SYSTEM", "COLUMNS", "MED_APPLY_NO");
            printData.addData("SYSTEM", "COLUMNS", "BLOOD_DATE");
            printData.addData("SYSTEM", "COLUMNS", "LIS_RE_DATE");
            printData.addData("SYSTEM", "COLUMNS", "DR_NOTE");
            printData.addData("SYSTEM", "COLUMNS", "MR_NO");
            printData.addData("SYSTEM", "COLUMNS", "LIS_RE_USER");
            printData.addData("SYSTEM", "COLUMNS", "SEND_USER");
        } else {// סԺ
            printData.addData("SYSTEM", "COLUMNS", "BED_NO");
            printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
            printData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printData.addData("SYSTEM", "COLUMNS", "MED_APPLY_NO");
            printData.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
            
            //modify by yangjj 20150529 ȥ������ʱ��
            //printData.addData("SYSTEM", "COLUMNS", "LIS_RE_DATE");
            
            printData.addData("SYSTEM", "COLUMNS", "DR_NOTE");
            printData.addData("SYSTEM", "COLUMNS", "MR_NO");
            printData.addData("SYSTEM", "COLUMNS", "LIS_RE_USER");
            
            //modify by yangjj 20150529 ȥ��������Ա
            //printData.addData("SYSTEM", "COLUMNS", "SEND_USER");
        }
        TParm printParm = new TParm();
        printParm.setData("TITLE", "TEXT", "����걾�ͼ��嵥");
        if ((Boolean) callFunction("UI|OE|isSelected") == true) {// �ż���
            printParm.setData("CLINICAREA_CODE", "TEXT", "������" +((TComboBox)this.getComponent("CLINICAREA_CODE")).getSelectedName() );
        } else {// סԺ
            printParm.setData("STATION_CODE", "TEXT", "������" + this.getText("STATION_CODE"));
        }
        Timestamp reStartDate = (Timestamp) this.getValue("RE_START_DATE"); // ��ʼʱ��
        Timestamp reEndDate = (Timestamp) this.getValue("RE_END_DATE"); // ����ʱ��
        printParm
                .setData("DATE", "TEXT",
                         "�������գ�" + StringTool.getString(reStartDate, "yyy/MM/dd HH:mm:ss") + " "
                                 + "�������գ�" + StringTool.getString(reEndDate, "yyy/MM/dd HH:mm:ss"));
        printParm.setData("TABLE", printData.getData());
        
        
        //add by yangjj 20150529 ���Ӵ�ӡʱ��
        printParm
        .setData("printTime", "TEXT",SystemTool.getInstance().getDate().toString().replace("-", "/").substring(0, 19));
        
        if ((Boolean) callFunction("UI|OE|isSelected") == true) {// �ż���
        	//===========modify by huangjw 20140718 start
        	String prtSwitch=IReportTool.getInstance().getPrintSwitch("MedOpdLisSend_new_V45.prtSwitch");
        	String previewSwitch=IReportTool.getInstance().getPrintSwitch("MedOpdLisSend_new_V45.previewSwitch");
        	if(prtSwitch.equals(IReportTool.ON)){
	        	if(previewSwitch.equals(IReportTool.ON)){
		            this.openPrintWindow(IReportTool.getInstance().getReportPath("MedOpdLisSend_new_V45.jhw"),
		            		IReportTool.getInstance().getReportParm("MedOpdLisSend_new_V45.class",printParm));
	        	}else{
	        		this.openPrintWindow(IReportTool.getInstance().getReportPath("MedOpdLisSend_new_V45.jhw"),
		            		IReportTool.getInstance().getReportParm("MedOpdLisSend_new_V45.class",printParm),true);
	        	}
        	}
        } else {// סԺ
        	String prtSwitch=IReportTool.getInstance().getPrintSwitch("MedLisSend_new_V45.prtSwitch");
        	String previewSwitch=IReportTool.getInstance().getPrintSwitch("MedLisSend_new_V45.previewSwitch");
        	if(prtSwitch.equals(IReportTool.ON)){
	        	if(previewSwitch.equals(IReportTool.ON)){
	        		this.openPrintWindow(IReportTool.getInstance().getReportPath("MedLisSend_new_V45.jhw"),
	        				IReportTool.getInstance().getReportParm("MedLisSend_new_V45.class",printParm));
	        	}else{
	        		this.openPrintWindow(IReportTool.getInstance().getReportPath("MedLisSend_new_V45.jhw"),
	        				IReportTool.getInstance().getReportParm("MedLisSend_new_V45.class",printParm),true);
	        	}
        	}
        	//===========modify by huangjw 20140718 end
        }
    }

	/**
	 * ��շ���
	 */
	public void onClear() {
        this.clearValue("CLINICAREA_CODE;STATION_CODE;LIS_RE_USER;MED_APPLY_NO;SEND_USER");
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String today = StringTool.getString(sysDate, "yyyyMMdd");
        this.setValue("START_DATE", StringTool.getTimestamp(today + "000000", "yyyyMMddHHmmss"));// Ĭ��������ʼ����
        this.setValue("END_DATE", StringTool.getTimestamp(today + "235959", "yyyyMMddHHmmss")); // Ĭ��������ֹ����
        this.setValue("CLINICAREA_CODE", Operator.getStation());
        this.setValue("STATION_CODE", Operator.getStation());
        callFunction("UI|RE_NO|setSelected", true);
        onChooseREState();
        callFunction("UI|MED_APPLY_NO|grabFocus");
        table.removeRowAll();
    }

	/**
     * TABLE��ѡ��ѡ�¼�
     * @param obj
     */
    public void onCheckBoxValue(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        int col = table.getSelectedColumn();
        String columnName = table.getDataStoreColumnName(col);
        int row = table.getSelectedRow();
        TParm parm = table.getParmValue();
        TParm tableParm = parm.getRow(row);
        String applicationNo = tableParm.getValue("MED_APPLY_NO");
        if ("FLG".equals(columnName)) {
            int rowCount = parm.getCount("ORDER_DESC");
            for (int i = 0; i < rowCount; i++) {
                if (i == row)
                    continue;
                if (applicationNo.equals(parm.getValue("MED_APPLY_NO", i))) {
                    parm.setData("FLG", i, parm.getBoolean("FLG", i) ? "N"
                            : "Y");
                }
            }
            table.setParmValue(parm);
        }
    }
    
	/**
	 * ȫѡ
	 */
	public void onAll() {
		boolean Flag = (Boolean) this.callFunction("UI|ALL|isSelected");
		TParm parm = table.getParmValue();
	
		for (int i = 0; i < parm.getCount(); i++) {
			table.setItem(i, "FLG", Flag);
		}
	}
	
	/**
	 * ���TTextFormat
	 */
	public TTextFormat getTextFormat(String tagName){
		return (TTextFormat)getTextFormat(tagName);
	}
	
	/**
	 * ���getRadioButton
	 */
	public TRadioButton getRadioButton(String tagName){
		return (TRadioButton)getComponent(tagName);
	}

    // ====================������begin======================
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = table.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.���ݵ������,��vector����
				// ������������;
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);
			}
		});
	}

    /**
     * ����ת������ֵ
     * 
     * @param columnName
     * @param tblColumnName
     * @return
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * �õ� Vector ֵ
     * 
     * @param group
     *            String ����
     * @param names
     *            String "ID;NAME"
     * @param size
     *            int �������
     * @return Vector
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size) count = size;
        for (int i = 0; i < count; i++) {
            Vector row = new Vector();
            for (int j = 0; j < nameArray.length; j++) {
                row.add(parm.getData(group, nameArray[j], i));
            }
            data.add(row);
        }
        return data;
    }

    /**
     * vectoryת��param
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames) {
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        // ������;
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        table.setParmValue(parmTable);
    }
    // ====================������end======================
}
