package com.javahis.ui.adm;

import com.dongyang.control.TControl;
import jdo.sys.Pat;
import jdo.sys.SYSBedTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import jdo.adm.ADMInpTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_FileServer;
import java.awt.Image;

import com.dongyang.util.FileTool;
import com.dongyang.util.ImageTool;
import com.dongyang.ui.TPanel;
import javax.swing.JLabel;
import com.dongyang.ui.TComboBox;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import com.javahis.device.JMStudio;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;
import jdo.adm.ADMXMLTool;
import jdo.hl7.Hl7Communications;
import jdo.mro.MROBorrowTool;

/**
 * <p>Title: 三级检诊信息（ADM入出转调用）</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:JavaHis </p>
 *
 * @author JiaoY 2009.01.14
 * @version 1.0
 */
public class ADMPatInfoControl
    extends TControl {
	
	private boolean flag;
    public ADMPatInfoControl() {
    }

    TParm acceptData = new TParm(); //接参
    Pat pat = new Pat();
    TParm initParm = new TParm(); //初始数据
    public void onInit() {
        callFunction("UI|TRAN_BED|setEnabled", false);
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            acceptData = (TParm) obj;
            this.initUI(acceptData);
        }
        
        
    }
    /**
     * 界面初始化
     * @param parm TParm
     */
    public void initUI(TParm parm) {
        Pat pat = new Pat();
        String mrNo = acceptData.getData("MR_NO").toString();
        pat = pat.onQueryByMrNo(mrNo);
        this.setValue("MR_NO", pat.getMrNo());
        this.setValue("PAT_NAME", pat.getName());
        this.setValue("SEX_CODE", pat.getSexCode());
        this.setValue("IPD_NO", acceptData.getData("IPD_NO"));
        this.setValue("SPECIAL_DIET", acceptData.getData("SPECIAL_DIET"));//add by sunqy 20140527
        //设定可保存权限 可否转床转医师
        if(acceptData.getBoolean("SAVE_FLG")){
            callFunction("UI|save|setVisible", true);
        }else{
            callFunction("UI|save|setVisible", false);
        }
        this.initQuery();
        PtotoCheck();
        viewPhoto(pat.getMrNo());// add caoyong 显示照片
    }
  

    
    /**
     * 初始化查询
     */
    public void initQuery() {
        TParm parm = new TParm();
        parm.setData("CASE_NO", acceptData.getData("CASE_NO"));
        parm.setData("MR_NO", acceptData.getData("MR_NO"));
        parm.setData("IPD_NO", acceptData.getData("IPD_NO"));
        //查询病患住院信息
        TParm result = ADMInpTool.getInstance().selectall(parm);
        initParm.setRowData(result);
        //获取病患基本信息
        Pat pat = Pat.onQueryByMrNo(acceptData.getValue("MR_NO"));
        
        //modify by yangjj 20150710
        //setValue("AGE", StringUtil.showAge(pat.getBirthday(),result.getTimestamp("IN_DATE",0)));
        
        setValue("AGE", DateUtil.showAge(pat.getBirthday(), SystemTool.getInstance().getDate()));
        
        setValue("DEPT_CODE", result.getData("DEPT_CODE", 0));
        setValue("STATION_CODE", result.getData("STATION_CODE", 0));
        setValue("BED_NO", result.getData("BED_NO", 0));
        setValue("VS_DR_CODE", result.getData("VS_DR_CODE", 0));
        setValue("ATTEND_DR_CODE", result.getData("ATTEND_DR_CODE", 0));
        setValue("DIRECTOR_DR_CODE", result.getData("DIRECTOR_DR_CODE", 0));
        setValue("VS_NURSE_CODE", result.getData("VS_NURSE_CODE", 0));
        setValue("PATIENT_CONDITION", result.getData("PATIENT_CONDITION", 0));
        setValue("NURSING_CLASS", result.getData("NURSING_CLASS", 0));
        setValue("PATIENT_STATUS", result.getData("PATIENT_STATUS", 0));
        setValue("DIE_CONDITION", result.getData("DIE_CONDITION", 0));
        setValue("CARE_NUM", result.getData("CARE_NUM", 0));
        setValue("IO_MEASURE",result.getValue("IO_MEASURE",0));
        setValue("ISOLATION",result.getValue("ISOLATION",0));
        setValue("TOILET",result.getValue("TOILET",0));
        if("Y".equals(result.getValue("ALLERGY",0)))
            setValue("ALLERGY_Y","Y");
    }

    /**
     * 保存事件
     */
    public void onSave() 
    {
        if ("Y".equals(getValue("TRANBED_CHECK"))) {
            if ("Y".equals(acceptData.getData("BED_OCCU_FLG"))) {
                this.messageBox_("此病患已包床");
                return;
            }
        }
        TParm newParm = this.getParmForTag("MR_NO;IPD_NO;PAT_NAME;SEX_CODE;AGE;DEPT_CODE;STATION_CODE;VS_DR_CODE;ATTEND_DR_CODE;DIRECTOR_DR_CODE;VS_NURSE_CODE;PATIENT_CONDITION;NURSING_CLASS;PATIENT_STATUS;DIE_CONDITION;CARE_NUM;IO_MEASURE;ISOLATION;TOILET");
        if (newParm.getValue("VS_DR_CODE").equals("")) {//add by wanglong 20140331
            this.messageBox("经治医生不能为空");
            return;
        }
        newParm.setData("CASE_NO", acceptData.getData("CASE_NO"));
        newParm.setData("BED", getValue("TRANBED_CHECK"));
        newParm.setData("BED_NO", getValue("BED_NO"));
        newParm.setData("TRAN_BED", getValue("TRAN_BED"));
        //=====liuf=====//
        newParm.setData("BED_NO_DESC",((TComboBox)getComponent("BED_NO")).getComboEditor().getText());
        //=====liuf========//
        if(this.getValueBoolean("ALLERGY_Y")){
            newParm.setData("ALLERGY","Y");
        }else{
            newParm.setData("ALLERGY","N");
        }
        newParm.setData("OPT_USER", Operator.getID());
        newParm.setData("OPT_TERM", Operator.getIP());
        TParm DATA = new TParm();
        DATA.setData("OLD_DATA", initParm.getData());
        DATA.setData("NEW_DATA", newParm.getData());
//        System.out.println("data=====转床保存的数据==========="+DATA);
        //===========pangben modify 20110617 start
        if (null != Operator.getRegion() &&
                Operator.getRegion().length() > 0){
            DATA.setData("REGION_CODE", Operator.getRegion());
        }
        TParm result = TIOM_AppServer.executeAction(
            "action.adm.ADMWaitTransAction",
            "changeDcBed", DATA); // 保存
        if (result.getErrCode() < 0){
            this.messageBox("执行失败！！"+result.getErrName()); //抢床错误提示   chenxi
            this.clearValue("TRANBED_CHECK;TRAN_BED");
        }
        else
        {
            this.messageBox("P0005");
            //===liuf 传送给CIS转床的消息===
            if ("Y".equals(getValue("TRANBED_CHECK"))) 
            {
              sendMessage(newParm);
            }
            // add by wangb 2016/6/6 更新经治医师时同步更新MRO_REG表未出库确认的住院医师 START
            TParm updateParm = new TParm();
            updateParm.setData("CASE_NO", acceptData.getData("CASE_NO"));
            updateParm.setData("DR_CODE", newParm.getValue("VS_DR_CODE"));
            TParm mroRegResult = MROBorrowTool.getInstance().updateMroRegDrCode(updateParm);
            if (mroRegResult.getErrCode() < 0) {
            	 err("ERR:" + mroRegResult.getErrCode() + mroRegResult.getErrText() +
            			 mroRegResult.getErrName());
            }
            // add by wangb 2016/6/6 更新经治医师时同步更新MRO_REG表未出库确认的住院医师 END
            this.initQuery();
            this.clearValue("TRANBED_CHECK;TRAN_BED");
            ADMXMLTool.getInstance().creatXMLFile(acceptData.getValue("CASE_NO"));
       
        }
    }
    /**
     * 点选转床
     */
    public void onTRANBED_CHECK() {
        if ("Y".equals(getValue("TRANBED_CHECK"))) {
            TParm sendParm = new TParm();
            if (getValue("DEPT_CODE") == null || "".equals(getValue("DEPT_CODE"))) {
                return;
            }
            if (getValue("STATION_CODE") == null ||
                "".equals(getValue("STATION_CODE"))) {
                return;
            }
            sendParm.setData("DEPT_CODE", getValue("DEPT_CODE"));
            sendParm.setData("STATION_CODE", getValue("STATION_CODE"));
            TParm reParm = (TParm)this.openDialog(
                "%ROOT%\\config\\adm\\ADMQueryBed.x", sendParm);
            if (reParm != null) {
                this.setValue("TRAN_BED", reParm.getValue("BED_NO", 0));
            }else{
                this.clearValue("TRANBED_CHECK;TRAN_BED");
            }
        }else{
            this.clearValue("TRANBED_CHECK;TRAN_BED");
        }
    }
    
    /**
     * 传送CIS和血糖转床消息
     * @param parm
     */
    public void sendMessage(TParm parm)
    {
//		System.out.println("sendCISMessage()");
		// ICU、CCU注记
		String caseNO = parm.getValue("CASE_NO");		
		boolean IsICU = SYSBedTool.getInstance().checkIsICU(caseNO);
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(caseNO);
		//转床
		String type="ADM_TRAN_BED";
		parm.setData("ADM_TYPE", "I");
		//CIS
		if (IsICU||IsCCU)
		{ 
		  List list = new ArrayList();
		  parm.setData("SEND_COMP", "CIS");
		  list.add(parm);
		  TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,type);
		  if (resultParm.getErrCode() < 0)
				messageBox(resultParm.getErrText());
		}
		//血糖
		List list = new ArrayList();
		parm.setData("SEND_COMP", "NOVA");	
		list.add(parm);
		TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,type);
		if (resultParm.getErrCode() < 0)
		  messageBox(resultParm.getErrText());
    }
    
    /**
     * 拍照
     * add caoyong 20140313
     * @throws IOException
     */
    public void onPhoto() throws IOException {

        String mrNo = getValue("MR_NO").toString();
        String photoName = mrNo + ".jpg";
        String dir = TIOM_FileServer.getPath("PatInfPIC.LocalPath");
       
        new File(dir).mkdirs();
        JMStudio jms = JMStudio.openCamera(dir + photoName);
        jms.addListener("onCameraed", this, "sendpic");
    }
    
    
    /**
     * 显示photo
     *
     * @param mrNo
     *            String 病案号
     */
    public void viewPhoto(String mrNo) {
        String photoName = mrNo + ".jpg";
        String fileName = photoName;
        try {
            TPanel viewPanel = (TPanel) getComponent("VIEW_PANEL");
            String root = TIOM_FileServer.getRoot();
            
            String dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
          //病案号大于等于10位处理
            if(mrNo.length()>=10){
            	dir = root + dir + mrNo.substring(0, 3) + "\\"
                + mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";
            //病案号小于10位处理
            }else{
            	dir = root + dir + mrNo.substring(0, 2) + "\\"
                + mrNo.substring(2, 4) + "\\" + mrNo.substring(4, 7) + "\\";
            }
            //
            byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
                    dir + fileName);
            if (data == null) {
            	
                //viewPanel.removeAll();
                flag=false;
                return;
            }
                flag=true;
            double scale = 0.5;
            boolean flag = true;
            Image image = ImageTool.scale(data, scale, flag);
            // Image image = ImageTool.getImage(data);
            Pic pic = new Pic(image);
            pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
            pic.setLocation(0, 0);
            viewPanel.removeAll();
            viewPanel.add(pic);
            pic.repaint();
            PtotoCheck();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    class Pic extends JLabel {
        Image image;

        public Pic(Image image) {
            this.image = image;
        }

        public void paint(Graphics g) {
            g.setColor(new Color(161, 220, 230));
            g.fillRect(4, 22, 70, 70);
            if (image != null) {
                g.drawImage(image, 10, 0, null);

            }
        }
    }
    
    /**
     * 传送照片
     *
     * @param image
     *            Image
     */
    public void sendpic(Image image) {
        String mrNo = getValue("MR_NO").toString();
        String photoName = mrNo + ".jpg";
        String dir = TIOM_FileServer.getPath("PatInfPIC.LocalPath");
        String localFileName = dir + photoName;
        try {
        	File file =new File(localFileName);
        	byte[] data = FileTool.getByte(localFileName);
        	if (file.exists()) {	 
                 new File(localFileName).delete();
			}
            String root = TIOM_FileServer.getRoot();
            dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
            //病案号大于等于10位处理
            if(mrNo.length()>=10){
            	dir = root + dir + mrNo.substring(0, 3) + "\\"
                + mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";
            //病案号小于10位处理
            }else{
            	dir = root + dir + mrNo.substring(0, 2) + "\\"
                + mrNo.substring(2, 4) + "\\" + mrNo.substring(4, 7) + "\\";
            }
            //
            TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(), dir
                    + photoName, data);
        } catch (Exception e) {
        	System.out.println("e::::"+e.getMessage());
        }
        this.viewPhoto(mrNo );//modify by huangjw 20140730
        
    }
    
    public void PtotoCheck(){
    	if(flag){
    		 callFunction("UI|PHOTO_BOTTON|setEnabled", false);
    	}else{
    		 callFunction("UI|PHOTO_BOTTON|setEnabled", true);
    	}
    }

}
