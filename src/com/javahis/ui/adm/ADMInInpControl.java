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
 * Title:转科管理
 * </p>
 *
 * <p>
 * Description:转科管理
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
    TParm acceptData = new TParm(); // 接参
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
        // 入院日期
        ((TTextFormat)this.getComponent("IN_DATE")).setValue(acceptData
                .getData("IN_DATE"));
        // 预定日期
        setValue("OUT_DATE", SystemTool.getInstance().getDate());
    }

    /**
     * 保存事件
     */
    public void onSave() {
        String out = getValue("OUT_INP").toString();
        if ("Y".equals(out))
            this.outAdm();
        else
            this.InOutDept();
    }

    /**
     * 转科保存
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
            this.messageBox("次病患暂不能转科！");
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
     * 出院保存
     */
    public void outAdm() {
        if (!checkDate(false))
            return;
        // 判断病患是否是召回的病人 如果是那么需要选择出院日期
        TParm adm_case = new TParm();
        adm_case.setData("CASE_NO", acceptData.getData("CASE_NO"));
        TParm check = ADMTool.getInstance().getADM_INFO(adm_case);
        Timestamp ds_date;
        String outDept=this.getValueString("OUT_DEPT_CODE");
        String outStation=this.getValueString("OUT_STATION_CODE");
        //===zhangp 20130105 start 召回重新入床的病人会因为床号变更查询不到费用导致状态为出院无账单
        TParm parm = new TParm();
        // 目前还没有召回功能 暂且注释掉
        if (check.getValue("LAST_DS_DATE", 0).length() > 0) { // 医疗出院日期不为空
            // 那么是召回的病人
            Object obj = this.openDialog("%ROOT%/config/adm/ADMChangeDsDate.x",
                                         check.getTimestamp("LAST_DS_DATE", 0));
            if (obj == null) {
                return;
            }
            ds_date = (Timestamp) obj;
            if(ds_date.equals(check.getTimestamp("LAST_DS_DATE", 0))){// SHIBL ADD 出院科室病区取上一次的记录
            	outDept=check.getValue("DS_DEPT_CODE", 0).equals("")?outDept:check.getValue("DS_DEPT_CODE", 0);
            	outStation=check.getValue("DS_STATION_CODE", 0).equals("")?outStation:check.getValue("DS_STATION_CODE", 0);
            }
            parm.setData("ADMS", "1");
            //===zhangp 20130105 end

        } else {
            //再次出院不改变出院日期
            if (check.getValue("DS_DATE", 0).length() > 0)
                ds_date = check.getTimestamp("DS_DATE", 0);
            else
                ds_date = SystemTool.getInstance().getDate();
        }
        // 计算住院天数
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
      // System.out.println("再次出院入参" + parm);
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
     * 出院点选事件
     */
    public void onOUT_INP() {
        String check = getValue("OUT_INP").toString();
        if ("Y".equals(check)) {
            callFunction("UI|IN_DEPT_CODE|setEnabled", false);
            callFunction("UI|IN_STATION_CODE|setEnabled", false);

            TParm parm = new TParm();
            // 检查长期医嘱
            if (OdiOrderTool.getInstance().getUDOrder(
                    acceptData.getValue("CASE_NO"))) {
                this.messageBox( "该病患有未停用的长期医嘱，不允许出科。");
                callFunction("UI|save|setEnabled", false);
                return   ;
            }
            // 检查护士审核
            parm = new TParm();
            parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
            if (InwForOutSideTool.getInstance().checkOrderisCHECKTool(parm)) {
            	this.messageBox( "该病患有未审核的医嘱，不允许出科。");
            	  callFunction("UI|save|setEnabled", false);
            	  return   ;
            }
            //=============================  chenxi  add  20130321 出院病患检索药房未审核，药房未配药情况
            // 检查住院药房审核执行 true：有未审核,false:没有未配的药
            parm = new TParm();
            parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
             if(InwForOutSideTool.getInstance().checkDrug(parm)){
            	 this.messageBox( "住院药房有该病患未审核的医嘱，不允许出科。");
            	 callFunction("UI|save|setEnabled", false);
            	 return   ;
             }
          // 检查配药执行 true：有未配的药,false:没有未配的药
             parm = new TParm();
             parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
              if(InwForOutSideTool.getInstance().exeDrug(parm)){
             	 this.messageBox( "住院药房有该病患未完成的配药，不允许出科。");
             	 callFunction("UI|save|setEnabled", false);
             	 return   ;
              }
              //=============================  chenxi  add  20130321 出院病患检索药房未审核，药房未配药情况
            // 检查护士执行
            parm = new TParm();
            parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
            if (InwForOutSideTool.getInstance().checkOrderisEXETool(parm)) {
            	this.messageBox( "该病患有未执行的医嘱，不允许出科。");
            	  callFunction("UI|save|setEnabled", false);
            	  return   ;
            }
            parm = new TParm();
            parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
            TParm result=InwForOutSideTool.getInstance().checkExmOrderFee(parm);
            if (result.getErrCode()<0) {
            	this.messageBox( "该病患医技执行核查异常，不允许出科。");
            	  callFunction("UI|save|setEnabled", false);
            	  return   ;
            }else if (result.getCount()>0) {
              for(int i=0;i<result.getCount();i++){
            	  this.messageBox("该病患,'"+result.getValue("ORDER_DESC",i)+"'医技未执行，不允许出科。");  
              }
          	  callFunction("UI|save|setEnabled", false);
          	  return   ;
          }
            //=============================  duzhw  add  20130917 出院病患检索退药流程是否完成，未完成不许出院
            // 
            if (OdiOrderTool.getInstance().getRtnCfmM(
                    acceptData.getValue("CASE_NO"))) {
                this.messageBox( "该病患有退药未确认的信息，不允许出院。");
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
     * 选择转入科室事件
     */
    public void onIN_DEPT_CODE() {
        this.setValue("IN_STATION_CODE", "");
    }

    /**
     * 检核转入病区
     */
    public void onIN_STATION_CODE() {
        String inStation = this.getValue("IN_STATION_CODE").toString();
        String outStation = this.getValue("OUT_STATION_CODE").toString();
        if (inStation.equals(outStation)) {
            this.messageBox("转入转出病区相同！");
            this.setValue("IN_STATION_CODE", "");
            return;
        }
    }

    /**
     * 检核数据
     *
     * @return boolean
     */
    public boolean checkDate(boolean flg) {
        if (this.getValue("OUT_DEPT_CODE") == null
            || this.getValue("OUT_DEPT_CODE").equals("")) {
            this.messageBox_("请选择转出科室！");
            return false;
        }
        if (this.getValue("OUT_STATION_CODE") == null
            || this.getValue("OUT_STATION_CODE").equals("")) {
            this.messageBox_("请选择转出病区！");
            return false;
        }
        String out = getValue("OUT_INP").toString();
        if ("N".equals(out)) {
            if (this.getValue("IN_DEPT_CODE") == null
                || this.getValue("IN_DEPT_CODE").equals("")) {
                this.grabFocus("IN_DEPT_CODE");
                this.messageBox_("请选择转入科室！");
                return false;
            }
            //shibl 20120524 add
//            if(this.getValue("IN_DEPT_CODE").equals(this.getValue("OUT_DEPT_CODE"))){
//            	this.grabFocus("IN_DEPT_CODE");
//                this.messageBox_("转入科室与转出科室相同,请重新选择！");
//                return false;
//            }
            if (this.getValue("IN_STATION_CODE") == null
                || this.getValue("IN_STATION_CODE").equals("")) {
                this.grabFocus("IN_STATION_CODE");
                this.messageBox_("请选择转入病区！");
                return false;
            }
        	if(flg){
            	//套餐患者转科/出院自动执行套餐批次
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
        // 检查长期医嘱
        if (OdiOrderTool.getInstance().getUDOrder(
                acceptData.getValue("CASE_NO"))) {
            re = this.messageBox("提示", "该病患有未停用的长期医嘱，确认出科吗？", 0);
            if (re == 1) {
                return false;
            }
        }
        // 检查护士审核
        parm = new TParm();
        parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
        if (InwForOutSideTool.getInstance().checkOrderisCHECKTool(parm)) {
            re = this.messageBox("提示", "该病患有未审核的医嘱，确认出科吗?", 0);
            if (re == 1) {
                return false;
            }
        }
        // 检查护士执行
        parm = new TParm();
        parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
        if (InwForOutSideTool.getInstance().checkOrderisEXETool(parm)) {
            re = this.messageBox("提示", "该病患有未执行的医嘱，确认出科吗?", 0);
            if (re == 1) {
                return false;
            }
        }
        //fux modify 20180111 id:5746 患者换床后长嘱调配药品信息出现两条
        parm = new TParm();
		parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
		if(InwForOutSideTool.getInstance().checkDrug(parm)){
			this.messageBox( "药房有该病患未审核的医嘱，不允许出科。");
			return  false;
		}
		// 检查配药执行 true：有未配的药,false:没有未配的药
		parm = new TParm();
		parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
		if(InwForOutSideTool.getInstance().exeDrug(parm)){
			this.messageBox( "药房有该病患未完成的配药，不允许出科。");
			return  false ;
		}
		if (OdiOrderTool.getInstance().getRtnCfmM(acceptData.getValue("CASE_NO"))) {
			this.messageBox( "该病患有药房退药未确认的信息，不允许出科。");
			return   false;
		}

        
        //=====pangben 2015-6-18 添加出院办理校验
        if ("Y".equals(out)) {
        	//校验此病患是否套餐患者
        	TParm result=ADMInpTool.getInstance().onCheckLumpwork(acceptData.getValue("CASE_NO"));
        	if (result.getCount()>0) {
        		//如果有新生儿没有办理出院，提示信息
            	result=ADMInpTool.getInstance().onCheckNewBodyDsDate(acceptData.getValue("CASE_NO"));
            	if (result.getCount()>0) {
    				this.messageBox("此病患存在新生儿未办理出院");
    				return false;
    			}
            	//获得套餐差额医嘱代码
            	result=ADMInpTool.getInstance().getLumpworkOrderCode();
            	if (result.getCount("LUMPWORK_ORDER_CODE")>0&&
            			result.getValue("LUMPWORK_ORDER_CODE",0).length()>0) {
				}else{
					this.messageBox("未设置套餐差额医嘱代码");
					return false;
				}
            	//出院套餐患者自动执行批次 add by kangy20171215
            	String sql1="SELECT CASE_NO FROM IBS_ORDD WHERE CASE_NO ='"+acceptData.getValue("CASE_NO")+"' AND INCLUDE_EXEC_FLG='N'";
    	    	 result = new TParm(TJDODBTool.getInstance().select(sql1));
    	    	 parm= new TParm();
    	    	if (result.getCount()>0) {
    	    		onExeIncludeBatch(acceptData.getValue("CASE_NO"));
    			}
            	//pangben 2017-8-9
            /*	if(flg){
	            	//转科添加套餐患者校验是否已经执行批次，未执行不可以转科
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
     * hl7接口
     * @param parm TParm
     * @param type String
     */
    private void sendHL7Mes(TParm parm, String type) {
    	System.out.println("parm:"+parm);
        String caseNo = parm.getValue("CASE_NO");
        //转科
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
            //血糖
            List list = new ArrayList();
            parm.setData("ADM_TYPE", "I");
            parm.setData("SEND_COMP", "NOVA");
            list.add(parm);
            TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
            if (resultParm.getErrCode() < 0)
                this.messageBox(resultParm.getErrText());            
        }
        //出院
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
            //血糖
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
			this.messageBox("此病患不是套餐患者,不可以操作");
			return;
		}
		if(null==selParm.getValue("LUMPWORK_RATE",0)||
				selParm.getValue("LUMPWORK_RATE",0).length()<=0||selParm.getDouble("LUMPWORK_RATE",0)==0.00){
			this.messageBox("此病患未设置套餐折扣,不能执行套餐批次");
    		return ;
    	}
		if (null != caseNo && caseNo.length() > 0) {
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			TParm result = TIOM_AppServer.executeAction("action.ibs.IBSAction",
					"onExeIbsLumpWorkBatch", parm);
			if (result.getErrCode() < 0) {
				this.messageBox("操作失败," + result.getErrText());
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
