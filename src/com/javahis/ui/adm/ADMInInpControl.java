package com.javahis.ui.adm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.Pat;
import jdo.sys.SYSBedTool;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TTextFormat;
import jdo.sys.Operator;
import jdo.udd.UddDispatchTool;
import jdo.udd.UddRtnRgsTool;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import jdo.hl7.Hl7Communications;
import jdo.inw.InwForOutSideTool;
import jdo.odi.OdiOrderTool;
import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;

/**
 * <p>
 * Title:ת�ƹ���
 * </p>
 *
 * <p>
 * Description:ת�ƹ���
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author zhangk
 * @version 1.0
 */
public class ADMInInpControl extends TControl {
    TParm acceptData = new TParm(); // �Ӳ�
    Pat pat = new Pat();

    public void onInit() {
        super.onInit();
        acceptData = (TParm)this.getParameter();
        this.setValueForParm("BED_NO;MR_NO;IPD_NO;PAT_NAME;SEX_CODE;",
                             acceptData);
        TParm parm = new TParm();
        parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
        TParm admInp = ADMTool.getInstance().getADM_INFO(parm);
        this.setValue("OUT_DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
        this.setValue("OUT_STATION_CODE", admInp.getValue("STATION_CODE", 0));
        // ��Ժ����
        ((TTextFormat)this.getComponent("IN_DATE")).setValue(acceptData
                .getData("IN_DATE"));
        // Ԥ������
        setValue("OUT_DATE", SystemTool.getInstance().getDate());
    }

    /**
     * �����¼�
     */
    public void onSave() {
        String out = getValue("OUT_INP").toString();
        if ("Y".equals(out))
            this.outAdm();
        else
            this.InOutDept();
    }

    /**
     * ת�Ʊ���
     */
    public void InOutDept() {
        if (!checkDate(true))
            return;
        TParm parm = new TParm();
        parm.setData("CASE_NO", acceptData.getData("CASE_NO"));
        parm.setData("MR_NO", this.getValue("MR_NO"));
        parm.setData("IPD_NO", this.getValue("IPD_NO"));
        parm.setData("BED_NO", this.getValue("BED_NO"));
        parm.setData("IN_DEPT_CODE", this.getValue("IN_DEPT_CODE"));
        parm.setData("IN_STATION_CODE", this.getValue("IN_STATION_CODE"));
        parm.setData("OUT_DEPT_CODE", this.getValue("OUT_DEPT_CODE"));
        parm.setData("OUT_STATION_CODE", this.getValue("OUT_STATION_CODE"));
        parm.setData("OUT_DATE", this.getValue("OUT_DATE"));
        parm.setData("PSF_KIND", "INDP");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        // ==========pangben modify 20110617 start
        parm.setData("REGION_CODE", Operator.getRegion());
        
        /***********************liuf HL7 *******************************/
//        TParm parmCIS = new TParm();
//        parmCIS.setData("CASE_NO", acceptData.getData("CASE_NO"));
//        parmCIS.setData("MR_NO", this.getValue("MR_NO"));
//        parmCIS.setData("IPD_NO", this.getValue("IPD_NO"));
//        parmCIS.setData("BED_NO", this.getValue("BED_NO"));
//        parmCIS.setData("IN_DEPT_CODE", this.getValue("IN_DEPT_CODE"));
//        parmCIS.setData("IN_STATION_CODE", this.getValue("IN_STATION_CODE"));
//        parmCIS.setData("OUT_DEPT_CODE", this.getValue("OUT_DEPT_CODE"));
//        parmCIS.setData("OUT_STATION_CODE", this.getValue("OUT_STATION_CODE"));
//        parmCIS.setData("OUT_DATE", this.getValue("OUT_DATE"));
//        parmCIS.setData("PSF_KIND", "INDP");
//        parmCIS.setData("OPT_USER", Operator.getID());
//        parmCIS.setData("OPT_TERM", Operator.getIP());
//        parmCIS.setData("REGION_CODE", Operator.getRegion());
//        sendHL7Mes(parmCIS, "ADM_TRAN");             
        /***********************liuf HL7 *******************************/
 		boolean IsICU = SYSBedTool.getInstance().checkIsICU(""+acceptData.getData("CASE_NO"));
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(""+acceptData.getData("CASE_NO"));
		parm.setData("ICU_FLG", IsICU);
		parm.setData("CCU_FLG", IsCCU);
		
        TParm result = TIOM_AppServer.executeAction(
                "action.adm.ADMWaitTransAction", "onInOutSave", parm);
        if ("F".equals(result.getData("CHECK"))) {
            this.messageBox("�β����ݲ���ת�ƣ�");
        }
        if (result.getErrCode() < 0) {
            this.messageBox("E0005");
        } else {
            this.messageBox("P0005");
            /*********************** HL7 *******************************/
            sendHL7Mes(parm, "ADM_TRAN");
            this.closeWindow();
        }
    }

    /**
     * ��Ժ����
     */
    public void outAdm() {
        if (!checkDate(false))
            return;
        // �жϲ����Ƿ����ٻصĲ��� �������ô��Ҫѡ���Ժ����
        TParm adm_case = new TParm();
        adm_case.setData("CASE_NO", acceptData.getData("CASE_NO"));
        TParm check = ADMTool.getInstance().getADM_INFO(adm_case);
        Timestamp ds_date;
        String outDept=this.getValueString("OUT_DEPT_CODE");
        String outStation=this.getValueString("OUT_STATION_CODE");
        //===zhangp 20130105 start �ٻ������봲�Ĳ��˻���Ϊ���ű����ѯ�������õ���״̬Ϊ��Ժ���˵�
        TParm parm = new TParm();
        // Ŀǰ��û���ٻع��� ����ע�͵�
        if (check.getValue("LAST_DS_DATE", 0).length() > 0) { // ҽ�Ƴ�Ժ���ڲ�Ϊ��
            // ��ô���ٻصĲ���
            Object obj = this.openDialog("%ROOT%/config/adm/ADMChangeDsDate.x",
                                         check.getTimestamp("LAST_DS_DATE", 0));
            if (obj == null) {
                return;
            }
            ds_date = (Timestamp) obj;
            if(ds_date.equals(check.getTimestamp("LAST_DS_DATE", 0))){// SHIBL ADD ��Ժ���Ҳ���ȡ��һ�εļ�¼
            	outDept=check.getValue("DS_DEPT_CODE", 0).equals("")?outDept:check.getValue("DS_DEPT_CODE", 0);
            	outStation=check.getValue("DS_STATION_CODE", 0).equals("")?outStation:check.getValue("DS_STATION_CODE", 0);
            }
            parm.setData("ADMS", "1");
            //===zhangp 20130105 end

        } else {
            //�ٴγ�Ժ���ı��Ժ����
            if (check.getValue("DS_DATE", 0).length() > 0)
                ds_date = check.getTimestamp("DS_DATE", 0);
            else
                ds_date = SystemTool.getInstance().getDate();
        }
        // ����סԺ����
        int in_days = StringTool.getDateDiffer(
                (Timestamp)this.getValue("OUT_DATE"),
                acceptData.getTimestamp("IN_DATE"));
        parm.setData("CASE_NO", acceptData.getData("CASE_NO"));
        parm.setData("MR_NO", this.getValue("MR_NO"));
        parm.setData("IPD_NO", this.getValue("IPD_NO"));
        parm.setData("BED_NO", this.getValue("BED_NO"));
        parm.setData("OUT_DEPT_CODE", outDept);
        parm.setData("OUT_STATION_CODE", outStation);
        parm.setData("IN_DAYS", in_days);
        parm.setData("OUT_DATE", ds_date);
        parm.setData("VS_NURSE_CODE", check.getValue("VS_NURSE_CODE", 0));
        parm.setData("DIRECTOR_DR_CODE", check.getValue("DIRECTOR_DR_CODE", 0));
        parm.setData("ATTEND_DR_CODE", check.getValue("ATTEND_DR_CODE", 0));
        parm.setData("VS_DR_CODE", check.getValue("VS_DR_CODE", 0));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        // =========pangben modify 20110617
        parm.setData("REGION_CODE", Operator.getRegion());
      // System.out.println("�ٴγ�Ժ���" + parm);
        /***********************liuf HL7 *******************************/
//        TParm parmCIS = new TParm();
//        parmCIS.setData("CASE_NO", acceptData.getData("CASE_NO"));
//        parmCIS.setData("MR_NO", this.getValue("MR_NO"));
//        parmCIS.setData("IPD_NO", this.getValue("IPD_NO"));
//        parmCIS.setData("BED_NO", this.getValue("BED_NO"));
//        parmCIS.setData("OUT_DEPT_CODE", this.getValue("OUT_DEPT_CODE"));
//        parmCIS.setData("OUT_STATION_CODE", this.getValue("OUT_STATION_CODE"));
//        parmCIS.setData("IN_DAYS", in_days);
//        parmCIS.setData("OUT_DATE", ds_date);
//        parmCIS.setData("VS_NURSE_CODE", check.getValue("VS_NURSE_CODE", 0));
//        parmCIS.setData("DIRECTOR_DR_CODE", check.getValue("DIRECTOR_DR_CODE", 0));
//        parmCIS.setData("ATTEND_DR_CODE", check.getValue("ATTEND_DR_CODE", 0));
//        parmCIS.setData("VS_DR_CODE", check.getValue("VS_DR_CODE", 0));
//        parmCIS.setData("OPT_USER", Operator.getID());
//        parmCIS.setData("OPT_TERM", Operator.getIP());
//        parmCIS.setData("REGION_CODE", Operator.getRegion());
 		boolean IsICU = SYSBedTool.getInstance().checkIsICU(""+acceptData.getData("CASE_NO"));
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(""+acceptData.getData("CASE_NO"));
		parm.setData("ICU_FLG", IsICU);
		parm.setData("CCU_FLG", IsCCU);
		parm.setData("EXE_DEPT_CODE", Operator.getCostCenter());
		
//        sendHL7Mes(parmCIS, "ADM_OUT");
        /***********************liuf HL7 *******************************/
        
        TParm result = TIOM_AppServer.executeAction(
                "action.adm.ADMWaitTransAction", "outAdmSave", parm);
        if (result.getErrCode() < 0){
        	System.out.println("result.ERR:"+result.getErrText());
            this.messageBox("E0005");
        }else {
            this.messageBox("P0005");
            /*********************** HL7 *******************************/
            sendHL7Mes(parm, "ADM_OUT");
            this.closeWindow();
        }
    }

    /**
     * ��Ժ��ѡ�¼�
     */
    public void onOUT_INP() {
        String check = getValue("OUT_INP").toString();
        if ("Y".equals(check)) {
            callFunction("UI|IN_DEPT_CODE|setEnabled", false);
            callFunction("UI|IN_STATION_CODE|setEnabled", false);

            TParm parm = new TParm();
            // ��鳤��ҽ��
            if (OdiOrderTool.getInstance().getUDOrder(
                    acceptData.getValue("CASE_NO"))) {
                this.messageBox( "�ò�����δͣ�õĳ���ҽ������������ơ�");
                callFunction("UI|save|setEnabled", false);
                return   ;
            }
            // ��黤ʿ���
            parm = new TParm();
            parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
            if (InwForOutSideTool.getInstance().checkOrderisCHECKTool(parm)) {
            	this.messageBox( "�ò�����δ��˵�ҽ������������ơ�");
            	  callFunction("UI|save|setEnabled", false);
            	  return   ;
            }
            //=============================  chenxi  add  20130321 ��Ժ��������ҩ��δ��ˣ�ҩ��δ��ҩ���
            // ���סԺҩ�����ִ�� true����δ���,false:û��δ���ҩ
            parm = new TParm();
            parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
             if(InwForOutSideTool.getInstance().checkDrug(parm)){
            	 this.messageBox( "סԺҩ���иò���δ��˵�ҽ������������ơ�");
            	 callFunction("UI|save|setEnabled", false);
            	 return   ;
             }
          // �����ҩִ�� true����δ���ҩ,false:û��δ���ҩ
             parm = new TParm();
             parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
              if(InwForOutSideTool.getInstance().exeDrug(parm)){
             	 this.messageBox( "סԺҩ���иò���δ��ɵ���ҩ����������ơ�");
             	 callFunction("UI|save|setEnabled", false);
             	 return   ;
              }
              //=============================  chenxi  add  20130321 ��Ժ��������ҩ��δ��ˣ�ҩ��δ��ҩ���
            // ��黤ʿִ��
            parm = new TParm();
            parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
            if (InwForOutSideTool.getInstance().checkOrderisEXETool(parm)) {
            	this.messageBox( "�ò�����δִ�е�ҽ������������ơ�");
            	  callFunction("UI|save|setEnabled", false);
            	  return   ;
            }
            parm = new TParm();
            parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
            TParm result=InwForOutSideTool.getInstance().checkExmOrderFee(parm);
            if (result.getErrCode()<0) {
            	this.messageBox( "�ò���ҽ��ִ�к˲��쳣����������ơ�");
            	  callFunction("UI|save|setEnabled", false);
            	  return   ;
            }else if (result.getCount()>0) {
              for(int i=0;i<result.getCount();i++){
            	  this.messageBox("�ò���,'"+result.getValue("ORDER_DESC",i)+"'ҽ��δִ�У���������ơ�");  
              }
          	  callFunction("UI|save|setEnabled", false);
          	  return   ;
          }
            //=============================  duzhw  add  20130917 ��Ժ����������ҩ�����Ƿ���ɣ�δ��ɲ����Ժ
            // 
            if (OdiOrderTool.getInstance().getRtnCfmM(
                    acceptData.getValue("CASE_NO"))) {
                this.messageBox( "�ò�������ҩδȷ�ϵ���Ϣ���������Ժ��");
                callFunction("UI|save|setEnabled", false);
                return   ;
            }
        } else {
            callFunction("UI|IN_DEPT_CODE|setEnabled", true);
            callFunction("UI|IN_STATION_CODE|setEnabled", true);
            callFunction("UI|save|setEnabled", true);
        }
        // this.messageBox_(getValue("OUT_INP"));

    }

    /**
     * ѡ��ת������¼�
     */
    public void onIN_DEPT_CODE() {
        this.setValue("IN_STATION_CODE", "");
    }

    /**
     * ���ת�벡��
     */
    public void onIN_STATION_CODE() {
        String inStation = this.getValue("IN_STATION_CODE").toString();
        String outStation = this.getValue("OUT_STATION_CODE").toString();
        if (inStation.equals(outStation)) {
            this.messageBox("ת��ת��������ͬ��");
            this.setValue("IN_STATION_CODE", "");
            return;
        }
    }

    /**
     * �������
     *
     * @return boolean
     */
    public boolean checkDate(boolean flg) {
        if (this.getValue("OUT_DEPT_CODE") == null
            || this.getValue("OUT_DEPT_CODE").equals("")) {
            this.messageBox_("��ѡ��ת�����ң�");
            return false;
        }
        if (this.getValue("OUT_STATION_CODE") == null
            || this.getValue("OUT_STATION_CODE").equals("")) {
            this.messageBox_("��ѡ��ת��������");
            return false;
        }
        String out = getValue("OUT_INP").toString();
        if ("N".equals(out)) {
            if (this.getValue("IN_DEPT_CODE") == null
                || this.getValue("IN_DEPT_CODE").equals("")) {
                this.grabFocus("IN_DEPT_CODE");
                this.messageBox_("��ѡ��ת����ң�");
                return false;
            }
            //shibl 20120524 add
//            if(this.getValue("IN_DEPT_CODE").equals(this.getValue("OUT_DEPT_CODE"))){
//            	this.grabFocus("IN_DEPT_CODE");
//                this.messageBox_("ת�������ת��������ͬ,������ѡ��");
//                return false;
//            }
            if (this.getValue("IN_STATION_CODE") == null
                || this.getValue("IN_STATION_CODE").equals("")) {
                this.grabFocus("IN_STATION_CODE");
                this.messageBox_("��ѡ��ת�벡����");
                return false;
            }
        	if(flg){
            	//�ײͻ���ת��/��Ժ�Զ�ִ���ײ�����
        		String sql = "SELECT MR_NO,LUMPWORK_RATE FROM ADM_INP WHERE CASE_NO='"
    				+ acceptData.getValue("CASE_NO")
    				+ "' AND IN_DATE IS NOT NULL AND CANCEL_FLG <> 'Y' AND LUMPWORK_CODE IS NOT NULL ";
        		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
        		if(parm.getCount()>0){
        			String sql1="SELECT CASE_NO FROM IBS_ORDD WHERE CASE_NO ='"+acceptData.getValue("CASE_NO")+"' AND INCLUDE_EXEC_FLG='N'";
        	    	TParm result = new TParm(TJDODBTool.getInstance().select(sql1));
        	    	 parm= new TParm();
        	    	if (result.getCount()>0) {
        	    		onExeIncludeBatch(acceptData.getValue("CASE_NO"));
        			}
        	    	sql="SELECT DISTINCT A.CASE_NO FROM IBS_ORDD A,ADM_INP B WHERE A.CASE_NO=B.CASE_NO AND B.NEW_BORN_FLG='Y'  AND M_CASE_NO ='"+acceptData.getValue("CASE_NO")+"' AND INCLUDE_EXEC_FLG='N'";
        	    	result = new TParm(TJDODBTool.getInstance().select(sql));
        	    	if (result.getCount()>0) {
        	    		for(int i=0;i<result.getCount();i++){
        	    		onExeIncludeBatch(result.getValue("CASE_NO",i));
        	    		}     	    		
        			}
           /* TParm result=ADMInpTool.getInstance().onCheckLumpworkExeBatch(acceptData.getValue("CASE_NO"));
            	if (result.getErrCode()<0) {
					this.messageBox(result.getErrText());
					return false;
				}*/
            	}
        	}
        }
        TParm parm = new TParm();
        int re = 0;
        // ��鳤��ҽ��
        if (OdiOrderTool.getInstance().getUDOrder(
                acceptData.getValue("CASE_NO"))) {
            re = this.messageBox("��ʾ", "�ò�����δͣ�õĳ���ҽ����ȷ�ϳ�����", 0);
            if (re == 1) {
                return false;
            }
        }
        // ��黤ʿ���
        parm = new TParm();
        parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
        if (InwForOutSideTool.getInstance().checkOrderisCHECKTool(parm)) {
            re = this.messageBox("��ʾ", "�ò�����δ��˵�ҽ����ȷ�ϳ�����?", 0);
            if (re == 1) {
                return false;
            }
        }
        // ��黤ʿִ��
        parm = new TParm();
        parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
        if (InwForOutSideTool.getInstance().checkOrderisEXETool(parm)) {
            re = this.messageBox("��ʾ", "�ò�����δִ�е�ҽ����ȷ�ϳ�����?", 0);
            if (re == 1) {
                return false;
            }
        }
        //fux modify 20180111 id:5746 ���߻�����������ҩƷ��Ϣ��������
        parm = new TParm();
		parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
		if(InwForOutSideTool.getInstance().checkDrug(parm)){
			this.messageBox( "ҩ���иò���δ��˵�ҽ������������ơ�");
			return  false;
		}
		// �����ҩִ�� true����δ���ҩ,false:û��δ���ҩ
		parm = new TParm();
		parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
		if(InwForOutSideTool.getInstance().exeDrug(parm)){
			this.messageBox( "ҩ���иò���δ��ɵ���ҩ����������ơ�");
			return  false ;
		}
		if (OdiOrderTool.getInstance().getRtnCfmM(acceptData.getValue("CASE_NO"))) {
			this.messageBox( "�ò�����ҩ����ҩδȷ�ϵ���Ϣ����������ơ�");
			return   false;
		}

        
        //=====pangben 2015-6-18 ��ӳ�Ժ����У��
        if ("Y".equals(out)) {
        	//У��˲����Ƿ��ײͻ���
        	TParm result=ADMInpTool.getInstance().onCheckLumpwork(acceptData.getValue("CASE_NO"));
        	if (result.getCount()>0) {
        		//�����������û�а����Ժ����ʾ��Ϣ
            	result=ADMInpTool.getInstance().onCheckNewBodyDsDate(acceptData.getValue("CASE_NO"));
            	if (result.getCount()>0) {
    				this.messageBox("�˲�������������δ�����Ժ");
    				return false;
    			}
            	//����ײͲ��ҽ������
            	result=ADMInpTool.getInstance().getLumpworkOrderCode();
            	if (result.getCount("LUMPWORK_ORDER_CODE")>0&&
            			result.getValue("LUMPWORK_ORDER_CODE",0).length()>0) {
				}else{
					this.messageBox("δ�����ײͲ��ҽ������");
					return false;
				}
            	//��Ժ�ײͻ����Զ�ִ������ add by kangy20171215
            	String sql1="SELECT CASE_NO FROM IBS_ORDD WHERE CASE_NO ='"+acceptData.getValue("CASE_NO")+"' AND INCLUDE_EXEC_FLG='N'";
    	    	 result = new TParm(TJDODBTool.getInstance().select(sql1));
    	    	 parm= new TParm();
    	    	if (result.getCount()>0) {
    	    		onExeIncludeBatch(acceptData.getValue("CASE_NO"));
    			}
            	//pangben 2017-8-9
            /*	if(flg){
	            	//ת������ײͻ���У���Ƿ��Ѿ�ִ�����Σ�δִ�в�����ת��
	            	result=ADMInpTool.getInstance().onCheckLumpworkExeBatch(acceptData.getValue("CASE_NO"));
	            	if (result.getErrCode()<0) {
						this.messageBox(result.getErrText());
						return false;
					}
            	}*/
			}
        }
        return true;
    }

    //$$==========liuf =============$$//
    /**
     * hl7�ӿ�
     * @param parm TParm
     * @param type String
     */
    private void sendHL7Mes(TParm parm, String type) {
    	System.out.println("parm:"+parm);
        String caseNo = parm.getValue("CASE_NO");
        //ת��
        if (type.equals("ADM_TRAN")) 
        {
            String InDeptCode = parm.getValue("IN_DEPT_CODE");
     		boolean IsICU = parm.getBoolean("ICU_FLG");
    		boolean IsCCU = parm.getBoolean("CCU_FLG");
    		//CIS
            if (InDeptCode.equals("0303")||InDeptCode.equals("0304")||IsICU||IsCCU) 
            {
                List list = new ArrayList();
                parm.setData("ADM_TYPE", "I");
                parm.setData("SEND_COMP", "CIS");
                list.add(parm);
                TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
                if (resultParm.getErrCode() < 0)
                    this.messageBox(resultParm.getErrText());
            }
            //Ѫ��
            List list = new ArrayList();
            parm.setData("ADM_TYPE", "I");
            parm.setData("SEND_COMP", "NOVA");
            list.add(parm);
            TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
            if (resultParm.getErrCode() < 0)
                this.messageBox(resultParm.getErrText());            
        }
        //��Ժ
        if (type.equals("ADM_OUT")) 
        {
     		boolean IsICU = parm.getBoolean("ICU_FLG");
    		boolean IsCCU = parm.getBoolean("CCU_FLG");
    		//CIS
    		if (IsICU||IsCCU) 
    		{
              List list = new ArrayList();
              parm.setData("ADM_TYPE", "I");
              parm.setData("SEND_COMP", "CIS");
              list.add(parm);
              TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
              if (resultParm.getErrCode() < 0)
                messageBox(resultParm.getErrText());
    		}
            //Ѫ��
            List list = new ArrayList();
            parm.setData("ADM_TYPE", "I");
            parm.setData("SEND_COMP", "NOVA");
            list.add(parm);
            TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
            if (resultParm.getErrCode() < 0)
                this.messageBox(resultParm.getErrText());        		
        }
    }
  //$$==========liuf =============$$//
    
    public void onExeIncludeBatch(String caseNo) {
		TParm selParm = new TParm();
		String sql = "SELECT MR_NO,LUMPWORK_RATE FROM ADM_INP WHERE CASE_NO='"
				+ caseNo
				+ "' AND IN_DATE IS NOT NULL AND CANCEL_FLG <> 'Y' AND LUMPWORK_CODE IS NOT NULL ";
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selParm.getCount() <= 0) {
			this.messageBox("�˲��������ײͻ���,�����Բ���");
			return;
		}
		if(null==selParm.getValue("LUMPWORK_RATE",0)||
				selParm.getValue("LUMPWORK_RATE",0).length()<=0||selParm.getDouble("LUMPWORK_RATE",0)==0.00){
			this.messageBox("�˲���δ�����ײ��ۿ�,����ִ���ײ�����");
    		return ;
    	}
		if (null != caseNo && caseNo.length() > 0) {
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			TParm result = TIOM_AppServer.executeAction("action.ibs.IBSAction",
					"onExeIbsLumpWorkBatch", parm);
			if (result.getErrCode() < 0) {
				this.messageBox("����ʧ��," + result.getErrText());
				return;
			}
			if (null != result.getValue("MESSAGE")
					&& result.getValue("MESSAGE").length() > 0) {
				this.messageBox(result.getValue("MESSAGE"));
				return;
			}
		}
	}
}
