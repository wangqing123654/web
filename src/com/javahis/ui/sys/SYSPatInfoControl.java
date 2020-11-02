package com.javahis.ui.sys;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JLabel;

import jdo.mem.MEMSQL;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextField;
import com.dongyang.util.FileTool;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.util.TypeTool;
import com.javahis.device.JMFRegistry;
import com.javahis.device.JMStudio;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: ����������Ϣ
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author JiaoY 2008-10-16
 * 
 * @version 1.0
 */

public class SYSPatInfoControl extends TControl {
	TParm data, comboldata;
	Pat pat;
	String action = "NEW";
	String recpt = ""; // ������Դ
	boolean nameSexFlg=false; //���������Ա��ѯ�����в�ѯ���Ϊtrue
	boolean idNoFlg = false; //�����֤�Ų���ʱ�����в�ѯ���Ϊtrue
	private boolean crmFlg = true; //crm�ӿڿ���
	 private TParm mem=new TParm(); //��Ա����    huangtt
	private TParm memTrade=new TParm(); //��Ա���ױ����    huangtt
	private String oldName = "";
	private boolean modifyFlg=false;
	public void onInit() {
		callFunction("UI|new|setEnabled", true);
		callFunction("UI|save|setEnabled", false);
		callFunction("UI|delete|setEnabled", false);
		callFunction("UI|PHOTO_BOTTON|setEnabled", false);
		
		crmFlg = StringTool.getBoolean(TConfig.getSystemValue("crm.switch"));

		onSavePopedem() ;
		 //�޸�Ȩ�� add  by huangjw 20150720
        if(this.getPopedem("MODIFY")){
        	modifyFlg=true;
        }
		Object obj = this.getParameter();
		TParm recptParm = new TParm();
		if (obj instanceof TParm) {
			recptParm = (TParm) obj;
			this.initUI(recptParm);
		}
	}
	
	private void onSavePopedem(){
		//modify lim 2012/04/05 begin
        if (this.getPopedem("readOnlyEnabled")) {
            this.callFunction("UI|new|setEnabled", false);
            this.callFunction("UI|save|setEnabled", false);
            this.callFunction("UI|delete|setEnabled", false); 
        }		
		//modify lim 2012/04/05 end		
        
       
	}
	/**
	 * ��ʾphoto
	 */
	public void viewPhoto(String mrNo) {
		String photoName = mrNo + ".jpg";
		String fileName = photoName;
		try {
			TPanel viewPanel = (TPanel) getComponent("VIEW_PANEL");
			String root = TIOM_FileServer.getRoot();
			String dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
			//�����Ŵ��ڵ���10λ����
            if(mrNo.length()>=10){
            	dir = root + dir + mrNo.substring(0, 3) + "\\"
                + mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";
            //������С��10λ����
            }else{
            	dir = root + dir + mrNo.substring(0, 2) + "\\"
                + mrNo.substring(2, 4) + "\\" + mrNo.substring(4, 7) + "\\";
            }
            //
			byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					dir + fileName);
			if (data == null){
				viewPanel.removeAll();
				return;
			}
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
		} catch (Exception e) {
		}
		onSavePopedem() ;		
	}

	class Pic extends JLabel {
		Image image;

		public Pic(Image image) {
			this.image = image;
		}

		public void paint(Graphics g) {
			g.setColor(new Color(161, 220, 230));
			g.fillRect(4, 15, 100, 100);
			if (image != null) {
				g.drawImage(image, 4, 15, 105, 142, null);
			}
		}

	}
	/**
	 * ���֤�ŵõ�������
	 */
	public void onIdNo() {
		String homeCode = "";
//		if ("Y".equals(this.getValue("FOREIGNER_FLG"))) {
//			setValue("HOMEPLACE_CODE", "");
//			return;
//		}
		if(!this.getText("ID_TYPE").equals("���֤")){
			setValue("HOMEPLACE_CODE", "");
			return;
		}
		
		String idNo = this.getValueString("IDNO");
		homeCode = StringUtil.getIdNoToHomeCode(idNo);
		setValue("HOMEPLACE_CODE", homeCode);
		this.grabFocus("CTZ1_CODE");
		
		onSavePopedem() ;
	}
	/**
	 * ����������ó�ʼ��
	 * 
	 * @param recptParm
	 *            TParm
	 */
	public void initUI(TParm recptParm) {
		if (!recptParm.checkEmpty("OPD", recptParm)) { // ����ҽ��վ
			recpt = "OPD";
			this.setValue("MR_NO", recptParm.getData("MR_NO"));
			callFunction("UI|new|setEnabled", false);
			callFunction("UI|delete|setEnabled", false);
			callFunction("UI|clear|setEnabled", false);
			callFunction("UI|query|setEnabled", false);
			callFunction("UI|save|setEnabled", false);
			this.onQuery();
		} else if (!recptParm.checkEmpty("ONW", recptParm)) { // ���ﻤʿվ
			recpt = "ONW";
			this.setValue("MR_NO", recptParm.getData("MR_NO"));
			callFunction("UI|new|setEnabled", false);
			callFunction("UI|save|setEnabled", true);
			callFunction("UI|delete|setEnabled", false);
			callFunction("UI|clear|setEnabled", true);
			callFunction("UI|query|setEnabled", true);
			this.onQuery();
		} else if (!recptParm.checkEmpty("ADM", recptParm)) { // סԺ�Ǽ�
			recpt = "ADM";
			this.setValue("MR_NO", recptParm.getData("MR_NO"));
			callFunction("UI|new|setEnabled", false);
			callFunction("UI|save|setEnabled", true);
			callFunction("UI|delete|setEnabled", false);
			callFunction("UI|clear|setEnabled", true);
			callFunction("UI|query|setEnabled", true);
			this.onQuery();
		} else if (!recptParm.checkEmpty("OPE", recptParm)) { // ����
			recpt = "OPE";
			this.setValue("MR_NO", recptParm.getData("MR_NO"));
			callFunction("UI|new|setEnabled", true);
			callFunction("UI|save|setEnabled", true);
			callFunction("UI|delete|setEnabled", false);
			callFunction("UI|clear|setEnabled", true);
			callFunction("UI|query|setEnabled", true);
			this.onQuery();
		} else if (!recptParm.checkEmpty("HRM", recptParm)) { // �������
			recpt = "HRM";
			if (!"N".equals(recptParm.getValue("SAVE_FLG"))) { // ��������½�����
				this.setValue("MR_NO", recptParm.getData("MR_NO"));
				callFunction("UI|new|setEnabled", true);
				callFunction("UI|save|setEnabled", true);
				callFunction("UI|delete|setEnabled", false);
				callFunction("UI|clear|setEnabled", true);
				callFunction("UI|query|setEnabled", true);
				this.onQuery();
			} else {
				onNew();
			}
		} else if (!recptParm.checkEmpty("RESV", recptParm)) { // ԤԼסԺ
			recpt = "RESV";
			this.setValue("MR_NO", recptParm.getData("MR_NO"));
			callFunction("UI|new|setEnabled", true);
			callFunction("UI|save|setEnabled", true);
			callFunction("UI|delete|setEnabled", false);
			callFunction("UI|clear|setEnabled", true);
			callFunction("UI|query|setEnabled", true);
			if (!"".equals(recptParm.getValue("MR_NO"))) {
				this.onQuery();
			} else {
				callFunction("UI|save|setEnabled", false);
			}
		}
		
		onSavePopedem() ;
	}

	public String getParmMap() {
		StringBuffer sb = new StringBuffer();
		// ��Ҫ��Ϣ
		sb.append("MR_NO;"); // ������
		sb.append("FOREIGNER_FLG;"); // �����ע��
		sb.append("ID_TYPE;"); // ֤������  add by huangtt 20140320
		sb.append("IDNO;"); // ���֤��
		sb.append("MERGE_FLG;"); // �ϲ�������ע��
		sb.append("MERGE_TOMRNO;"); // �ϲ�������
		sb.append("IPD_NO;"); // סԺ��
		sb.append("PAT_NAME;"); // ����
		sb.append("PY1;"); // ��ƴ1
		sb.append("PAT_NAME1;"); // ������
		sb.append("FIRST_NAME;"); // Ӣ���� add by huangtt 20140320
		sb.append("LAST_NAME;"); // Ӣ���� add by huangtt 20140320
		sb.append("PY2;"); // ��ƴ2
		sb.append("BIRTH_DATE;"); // ����
		sb.append("SEX_CODE;"); // �Ա�
		sb.append("HOMEPLACE_CODE;"); // ������
		sb.append("CTZ1_CODE;"); // ��ݱ�1
		sb.append("CTZ2_CODE;"); // ��ݱ�2
		sb.append("CTZ3_CODE;"); // ��ݱ�3
		// ������Ϣ
		sb.append("NATION_CODE;"); // ��������
		sb.append("SPECIES_CODE;"); // ����
		sb.append("E_MAIL;"); // email
		sb.append("TEL_HOME;"); // �绰1
		sb.append("TEL_COMPANY;"); // ��λ�绰
		sb.append("OCC_CODE;"); // ְҵ
		sb.append("COMPANY_DESC;"); // ������λ
		sb.append("CELL_PHONE;"); // �ֻ�
		sb.append("MARRIAGE_CODE;"); // ����״̬
		sb.append("HEIGHT;"); // ���
		sb.append("WEIGHT;"); // ����
		sb.append("BLOOD_TYPE;"); // Ѫ��
		sb.append("BLOOD_RH_TYPE;"); // RH
		// ͨ�ŵ�ַ
		sb.append("POST_CODE;"); // �ʱ�
		sb.append("CURRENT_ADDRESS;"); // ��ַ
		// ������ַ
		sb.append("RESID_POST_CODE;"); // �����ʱ�
		sb.append("RESID_ADDRESS;"); // ������ַ
		// ������ϵ��
		sb.append("CONTACTS_NAME;"); // ����
		sb.append("RELATION_CODE;"); // ��ϵ
		sb.append("CONTACTS_TEL;"); // �绰
		sb.append("CONTACTS_ADDRESS;"); // ��ַ
		// ������Ϣ
		sb.append("EDUCATION_CODE;"); // ����
		sb.append("SPECIES_CODE;"); // ����
		sb.append("OCC_CODE;"); // ְҵ
		sb.append("COMPANY_DESC;"); // ������λ
		sb.append("MARRIAGE_CODE;"); // ����״̬
		sb.append("HEIGHT;"); // ���
		sb.append("WEIGHT;"); // ����
		sb.append("BLOOD_TYPE;"); // Ѫ��
		sb.append("POST_CODE;"); // ͨ���ʱ�
		sb.append("ADDRESS_ROAD;"); // ͨ�ŵ�ַ
		sb.append("RESID_POST_CODE;"); // �����ʱ�
		sb.append("RESID_ROAD;"); // ������ַ
		sb.append("CONTACTS_NAME;"); // ������ϵ������
		sb.append("RELATION_CODE;"); // ��ϵ
		sb.append("CONTACTS_TEL;"); // �绰
		sb.append("CONTACTS_POST;"); // ������ϵ���ʱ�
		sb.append("CONTACTS_ADDRESS;"); // ��ַ
		// ������Ϣ
		sb.append("EDUCATION_CODE;"); // �����̶�
		sb.append("RELIGION_CODE;"); // �ڽ�
		sb.append("SPOUSE_IDNO;"); // ��ż���֤��
		sb.append("FATHER_IDNO;"); // �������֤��
		sb.append("MOTHER_IDNO;"); // ĸ�����֤��
		// ������ҽ����
		sb.append("FIRST_ADM_DATE;"); // �������ڣ�
		sb.append("DEAD_DATE;"); // ��������:
		sb.append("RCNT_OPD_DATE;"); // ����������ڣ�
		sb.append("RCNT_OPD_DEPT;"); // �������Ʊ�
		sb.append("RCNT_EMG_DATE;"); // ����������ڣ�
		sb.append("RCNT_EMG_DEPT;"); // �������Ʊ�
		sb.append("RCNT_IPD_DATE;"); // ���סԺ���ڣ�
		sb.append("RCNT_IPD_DEPT;"); // ���סԺ�Ʊ�
		sb.append("RCNT_MISS_DATE;"); // ���ˬԼ���ڣ�
		sb.append("RCNT_MISS_DEPT;"); // ���ˬԼ�Ʊ�
		sb.append("ADULT_EXAM_DATE;"); // ������˽������ڣ�
		sb.append("SMEAR_RCNT_DATE;"); // ���ͿƬ������ڣ�
		sb.append("KID_EXAM_RCNT_DATE;"); // ����׶��������ڣ�
		sb.append("KID_INJ_RCNT_DATE;"); // ����׶�ע�����ڣ�
		sb.append("LMP_DATE;"); // LMP���ڣ�
		sb.append("BREASTFEED_STARTDATE;"); // �������գ�
		sb.append("BREASTFEED_ENDDATE;"); // �������գ�
		sb.append("PAT1_CODE;"); // ������1
		sb.append("PAT2_CODE;"); // ������2
		sb.append("PAT3_CODE;"); // ������3
		sb.append("PREGNANT_DATE;"); // Ԥ����
		sb.append("DESCRIPTION;"); // ��ע
		/*******************************************/
		sb.append("BIRTHPLACE;");// ����
		/*********************************************/
		/*******************************************/
		// ��λ��ַ
		sb.append("ADDRESS_COMPANY;");// ��λ��ַ
		sb.append("POST_COMPANY;");// ��λ�ʱ�
		sb.append("NHICARD_NO;");//��������
		sb.append("NHI_NO;");//ҽ������
		/*********************************************/
		
		onSavePopedem() ;
		return sb.toString();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		/*if (!"".equals(this.getValueString("PY1")) && ! (nameSexFlg || idNoFlg)) {
			TParm inparm = new TParm();
			inparm.setData("PY1", this.getValueString("PY1"));
			Object obj = (TParm) this.openDialog(
					"%ROOT%\\config\\sys\\SYSPatQuery.x", inparm);
			if (obj instanceof TParm) {
				TParm queryParm = (TParm) obj;
				if (queryParm != null) {
					this.setValue("MR_NO", queryParm.getValue("MR_NO"));
				} else {
					return;
				}
			} else {
				return;
			}
		}*/

		pat = Pat.onQueryByMrNo(getValueString("MR_NO").trim());
		if (pat == null) {
			this.messageBox("���޴˲���!");
			callFunction("UI|new|setEnabled", true);
			callFunction("UI|save|setEnabled", false);
			callFunction("UI|delete|setEnabled", false);
			
			onSavePopedem() ;
			this.onQueryPat();//add by huangjw 20150203
			//return;
		}
		callFunction("UI|PHOTO_BOTTON|setEnabled", false);
		// callFunction("UI|new|setEnabled",false);
		TParm parm = pat.getParm();
		// ---���ⰴťsetֵ
		// �����ע��
		if (parm.getData("FOREIGNER_FLG").equals(true))
			setValue("FOREIGNER_FLG", "Y");
		else
			setValue("FOREIGNER_FLG", "N");
		// �ϲ�������ע��
		if (parm.getData("MERGE_FLG").equals(true))
			setValue("MERGE_FLG", "Y");
		else
			setValue("MERGE_FLG", "N");
		this.onMergeFlg();
		// RH����+��RH����-
		if (parm.getData("BLOOD_RH_TYPE").equals("+"))
			setValue("BLOOD_RH_TYPE", "Y");
		else if (parm.getData("BLOOD_RH_TYPE").equals("-")) {
			setValue("tRadioButton_3", "Y");
		}
		setValueForParm(getParmMap(), parm);
		//add by huangtt 20140410 start
        TParm memParm = new TParm();
 		memParm.setData("MR_NO", getValue("MR_NO"));
 		mem = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemInfoAll(getValueString("MR_NO"))));
 		memTrade =new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemTradeCrm(getValueString("MR_NO"))));
 		oldName = pat.getOldName();
        //add by huangtt 20140410 end
        
		// String descHome=this.getName(pat.gethomePlaceCode());
		// setValue("HOMEPLACE_DESC",descHome);
		callFunction("UI|MR_NO|setEnabled", false);
		callFunction("UI|PHOTO_BOTTON|setEnabled", true);
		action = "EDIT";

		//������סԺ������Ϣ-duzhw add 20140317
		setRcntCount(pat);

		// ������Ϣ
		TParm parmBase = new TParm();
//		parmBase.setData("BIRTHPLACE_DESC", this.getName(pat.getBirthPlace()));// ����
		parmBase.setData("NATION_CODE", pat.getNationCode()); // ����
		parmBase.setData("SPECIES_CODE", pat.getSpeciesCode()); // ����
		parmBase.setData("E_MAIL", pat.getEmail()); //
		parmBase.setData("TEL_HOME", pat.getTelHome()); // �绰1
		parmBase.setData("TEL_COMPANY", pat.getTelCompany()); // ��˾�绰
		parmBase.setData("SPECIAL_DIET", pat.getSpecialDiet()); //������ʳ
		parmBase.setData("OCC_CODE", pat.getOccCode()); //  ְҵ
		parmBase.setData("COMPANY_DESC", pat.getCompanyDesc()); // ������λ
		parmBase.setData("CELL_PHONE", pat.getCellPhone()); // �ֻ�
		parmBase.setData("MARRIAGE_CODE", pat.getMarriageCode()); // ����״̬
		parmBase.setData("HEIGHT", pat.getHeight()); // ���
		parmBase.setData("WEIGHT", pat.getWeight()); // ����
		parmBase.setData("NEW_BODY_WEIGHT", pat.getNewBodyWeight()); // ��������
		parmBase.setData("NEW_BODY_HEIGHT", pat.getNewBodyHeight()); // ������ //add by yangjj 20150312
		parmBase.setData("BLOOD_TYPE", pat.getBloodType()); // Ѫ��
		if (pat.getBloodRHType().equals("+")) // + ���� - ����
			callFunction("UI|BLOOD_RH_TYPE");
		parmBase.setData("BLOOD_RH_TYPE", "N");
		parmBase.setData("POST_CODE", pat.getPostCode()); // ͨ���ʱ�
//		this.onPost(); // �ʱ����ʡ����Ϣ
		parmBase.setData("ADDRESS", pat.getAddress()); // ��ַ
		parmBase.setData("RESID_POST_CODE", pat.getResidPostCode()); // �����ʱ�
//		this.onRESIDPOST(); // �ʱ����ʡ����Ϣ
		parmBase.setData("RESID_ADDRESS", pat.getResidAddress()); // ������ַ
		parmBase.setData("ADDRESS_COMPANY", pat.getCompanyAddress());// ��λ��ַ
//		this.onCompanyPost();// �ʱ����ʡ����Ϣ
		parmBase.setData("POST_COMPANY", pat.getCompanyPost());// ��λ�ʱ�
		parmBase.setData("CONTACTS_NAME", pat.getContactsName()); // ������ϵ��
		parmBase.setData("RELATION_CODE", pat.getRelationCode()); // ������ϵ�˹�ϵ
		parmBase.setData("CONTACTS_TEL", pat.getContactsTel()); // ������ϵ�˵绰
		parmBase.setData("CONTACTS_ADDRESS", pat.getContactsAddress()); // ������ϵ�˵�ַ
		parmBase.setData("PAT_BELONG", pat.getPatBelong()); // ����������
		parmBase.setData("PY1",pat.getPy1());//ƴ��
//		this.messageBox(pat.getPatBelong());
//		this.setValue("PAT_BELONG", pat.getPatBelong());
		setValueForParm(
				"NATION_CODE;SPECIES_CODE;E_MAIL;TEL_H1;TEL_H2;OCC_CODE;COMPANY_DESC;TEL_O1;"
						+ "CELL_PHONE;MARRIAGE_CODE;HEIGHT;WEIGHT;NEW_BODY_WEIGHT;" 
						+ "NEW_BODY_HEIGHT;" //add by yangjj 20150312
						+ "BLOOD_TYPE;BLOOD_RH_TYPE;POST_CODE;ADDRESS_ROAD"
						+ ";RESID_POST_CODE;RESID_ROAD;CONTACTS_NAME;RELATION_CODE;CONTACTS_TEL;CONTACTS_POST;"
						+ "CONTACTS_ADDRESS;POST_COMPANY;ADDRESS_COMPANY;PAT_BELONG;PY1",
				parmBase, -1);
		setValueForParm("SPECIAL_DIET", pat.getParm());//������ʳ
		//setValue("SPECIAL_DIET", pat.getSpecialDiet());
		viewPhoto(pat.getMrNo());
		if ("".equals(recpt)) {
			callFunction("UI|save|setEnabled", true);
			callFunction("UI|delete|setEnabled", true);
		}
		onSavePopedem() ;
		if(!modifyFlg){//û���޸�Ȩ��ʱ�����水ťҪ�û� add by huangjw 20150727
    		TMenuItem save=(TMenuItem) this.getComponent("save");
    		save.setEnabled(false);
    	}
	}

	public Pat modifyValue() {
		
		onSavePopedem() ;
		if (getValue("FOREIGNER_FLG").equals("Y")) // �����ע��
			pat.modifyForeignerFlg(true);
		else
			pat.modifyForeignerFlg(false);
		pat.modifyIdType(getValueString("ID_TYPE")); //add by huangtt 20140320 ֤������
		pat.modifyIdNo(getValueString("IDNO")); // ���֤��
		pat.modifyhomePlaceCode(getValueString("HOMEPLACE_CODE")); // ������
		if (getValue("MERGE_FLG").equals("Y")) // �ϲ�������ע��
			pat.modifyMergeFlg(true);
		else
			pat.modifyMergeFlg(false);
		pat.modifyMergeToMrNo(getValueString("MERGE_TOMRNO")); // �ϲ�������
		pat.modifyIpdNo(getValueString("IPD_NO")); // סԺ��
		//if(this.modifyFlg || action.equals("NEW")){
			pat.modifyName(getValueString("PAT_NAME")); // ����
			pat.modifyPy1(getValueString("PY1")); // ��ƴ1
			pat.modifySexCode(getValueString("SEX_CODE")); // �Ա�
			pat.modifyName1(getValueString("PAT_NAME1")); // ������
			pat.modifyBirthdy(TCM_Transform.getTimestamp(getValue("BIRTH_DATE"))); // ��������
		//}
		pat.modifyFirstName(getValueString("FIRST_NAME")); //Ӣ���� add by huangtt 20140320
		pat.modifyLastName(getValueString("LAST_NAME")); //Ӣ����  add by huangtt 20140320
		pat.modifyPy2(getValueString("PY2")); // ��ƴ2
		
		pat.modifyBirthPlace(this.getValueString("BIRTHPLACE"));// ����
		pat.modifyNhicardNo(this.getValueString("NHICARD_NO"));//��������
		pat.modifyNhiNo(this.getValueString("NHI_NO"));//ҽ������
	
		
		
		pat.modifyCtz1Code(getValueString("CTZ1_CODE")); // ���1
		pat.modifyCtz2Code(getValueString("CTZ2_CODE")); // ���2
		pat.modifyCtz3Code(getValueString("CTZ3_CODE")); // ���3
		// ������Ϣ
		pat.modifyNationCode(getValueString("NATION_CODE")); // ����
		pat.modifySpeciesCode(getValueString("SPECIES_CODE")); // ����
		pat.modifyEmail(getValueString("E_MAIL")); // Email
		pat.modifyTelHome(getValueString("TEL_HOME")); // �绰1
		pat.modifyTelCompany(getValueString("TEL_COMPANY")); // �绰2
		pat.modifySpecialDiet(getValueString("SPECIAL_DIET"));//������ʳ
		pat.modifyOccCode(getValueString("OCC_CODE")); // ְҵ
		pat.modifyCompanyDesc(getValueString("COMPANY_DESC")); // ��λ
		pat.modifyCellPhone(getValueString("CELL_PHONE")); // �ֻ�
		pat.modifyMarriageCode(getValueString("MARRIAGE_CODE")); // ����״̬
		pat.modifyHeight(getValueDouble("HEIGHT")); // ���
		pat.modifyWeight(getValueDouble("WEIGHT")); // ����
		pat.modifyNewBodyWeight(getValueDouble("NEW_BODY_WEIGHT")); // �������� add by sun20140624
		pat.modifyNewBodyHeight(getValueDouble("NEW_BODY_HEIGHT")); // ������ add by yangjj 20150312
		pat.modifyBloodType(getValueString("BLOOD_TYPE")); // Ѫ��
		if (getValue("BLOOD_RH_TYPE").equals("Y")) // RH����: +
			pat.modifyBloodRHType("+");
		else if (getValue("tRadioButton_3").equals("Y"))
			pat.modifyBloodRHType("-"); // RH����: -
		else
			pat.modifyBloodRHType(""); // ��
		// ͨ�ŵ�ַ
		pat.modifyPostCode(getValueString("POST_CODE")); // ͨ���ʱ�
//		pat.modifyAddress(getValueString("ADDRESS")); // ͨ�ŵ�ַ
		pat.modifyCurrentAddress(getValueString("CURRENT_ADDRESS")); // ͨ�ŵ�ַ
		// ������ַ
		pat.modifyResidPostCode(getValueString("RESID_POST_CODE")); // �����ʱ�
		pat.modifyResidAddress(getValueString("RESID_ADDRESS")); // ������ַ
		// ***********************************************************************************/
		// ��λ��ַ
		pat.modifyCompanyAddress(this.getValueString("ADDRESS_COMPANY"));
		pat.modifyCompanyPost(this.getValueString("POST_COMPANY"));
		// ������ϵ�˵�ַ
		pat.modifyContactsName(getValueString("CONTACTS_NAME")); // ������ϵ������
		pat.modifyRelationCode(getValueString("RELATION_CODE")); // ������ϵ�˹�ϵ
		pat.modifyContactsTel(getValueString("CONTACTS_TEL")); // ������ϵ�˵绰
		pat.modifyContactsAddress(getValueString("CONTACTS_ADDRESS")); // ������ϵ�˵�ַ
		// ������Ϣ
		pat.modifyEducationCode(getValueString("EDUCATION_CODE")); // �����̶�
		pat.modifyReligionCode(getValueString("RELIGION_CODE"));  // �ڽ�
		pat.modifySpouseIdno(getValueString("SPOUSE_IDNO")); // ��ż���֤��
		pat.modifyFatherIdno(getValueString("FATHER_IDNO")); // �������֤��
		pat.modifyMotherIdno(getValueString("MOTHER_IDNO")); // ĸ�����֤��
		// ������ҽҽ��
		pat.modifyFirstAdmDate(TCM_Transform
				.getTimestamp(getValue("FIRST_ADM_DATE"))); // �������ڣ�
		pat.modifyDeadDate(TCM_Transform.getTimestamp(getValue("DEAD_DATE"))); // ��������:
		pat.modifyRcntOpdDate(TCM_Transform
				.getTimestamp(getValue("RCNT_OPD_DATE"))); // ����������ڣ�
		pat.modifyRcntOpdDept(getValueString("RCNT_OPD_DEPT")); // �������Ʊ�
		pat.modifyRcntEmgDate(TCM_Transform
				.getTimestamp(getValue("RCNT_EMG_DATE"))); // ����������ڣ�
		pat.modifyRcntEmgDept(getValueString("RCNT_EMG_DEPT")); // �������Ʊ�
		pat.modifyRcntIpdDate(TCM_Transform
				.getTimestamp(getValue("RCNT_IPD_DATE"))); // ���סԺ���ڣ�
		pat.modifyRcntIpdDept(getValueString("RCNT_IPD_DEPT")); // ���סԺ�Ʊ�
		pat.modifyRcntMissDate(TCM_Transform
				.getTimestamp(getValue("RCNT_MISS_DATE"))); // ���ˬԼ���ڣ�
		pat.modifyRcntMissDept(getValueString("RCNT_MISS_DEPT")); // ���ˬԼ�Ʊ�
		pat.modifyAdultExamDate(TCM_Transform
				.getTimestamp(getValue("ADULT_EXAM_DATE"))); // ������˽������ڣ�
		pat.modifySmearRcntDate(TCM_Transform
				.getTimestamp(getValue("SMEAR_RCNT_DATE"))); // ���ͿƬ������ڣ�
		pat.modifyKidExamRcntDate(TCM_Transform
				.getTimestamp(getValue("KID_EXAM_RCNT_DATE"))); // ����׶��������ڣ�
		pat.modifyKidInjRcntDate(TCM_Transform
				.getTimestamp(getValue("KID_INJ_RCNT_DATE"))); // ����׶�ע�����ڣ�
		pat.modifyLMPDate(TCM_Transform.getTimestamp(getValue("LMP_DATE"))); // LMP���ڣ�
		pat.modifyBreastfeedStartDate(TCM_Transform
				.getTimestamp(getValue("BREASTFEED_STARTDATE"))); // �������գ�
		pat.modifyBreastfeedEndDate(TCM_Transform
				.getTimestamp(getValue("BREASTFEED_ENDDATE"))); // �������գ�
		pat.modifyPat1Code(getValueString("PAT1_CODE")); // ������1
		pat.modifyPat2Code(getValueString("PAT2_CODE")); // ������2
		pat.modifyPat3Code(getValueString("PAT3_CODE")); // ������3
		pat.modifyPregnantDate(TCM_Transform
				.getTimestamp(getValue("PREGNANT_DATE"))); // Ԥ���ڣ�
		pat.modifyDescription(getValueString("DESCRIPTION")); // ��ע
		if (getValue("BORNIN_FLG").equals("Y")) // ��Ժ����
			pat.modifyBorninFlg(true);
		else
			pat.modifyBorninFlg(false);
		// if (getValue("NEWBORN_SEQ").equals("Y")) //��������̥ע��
		// pat.modifyNewbornSeq(true);
		// else
		// pat.modifyForeignerFlg(false);
		if (getValue("PREMATURE_FLG").equals("Y")) // �����
			pat.modifyPrematureFlg(true);
		else
			pat.modifyPrematureFlg(false);
		if (getValue("HANDICAP_FLG").equals("Y")) // �м�
			pat.modifyHandicapFlg(true);
		else
			pat.modifyHandicapFlg(false);
		if (getValue("BLACK_FLG").equals("Y")) // ������
			pat.modifyBlackFlg(true);
		else
			pat.modifyBlackFlg(false);
		if (getValue("NAME_INVISIBLE_FLG").equals("Y")) // ����
			pat.modifyNameInvisibleFlg(true);
		else
			pat.modifyNameInvisibleFlg(false);
		if (getValue("LAW_PROTECT_FLG").equals("Y")) // �������ر�������
			pat.modifyLawProtectFlg(true);
		else
			pat.modifyLawProtectFlg(false);
		pat.modifyPatBelong(this.getValueString("PAT_BELONG"));
		return pat;
	}

	/**
	 * �����¼� new Ϊ����
	 * 
	 */
	public void onSave() {
//		String idNo = getValueString("IDNO");
//		if (!getValueString("HOMEPLACE_CODE").equals("")
//				&& getValueString("HOMEPLACE_CODE").length() > 0) {
//			if (getValueString("HOMEPLACE_CODE").equals(
//					StringUtil.getIdNoToHomeCode(idNo))) {
//				if (this.messageBox("��ʾ��Ϣ/Tip", "���������������֤���ݲ�һ��,����?", 0) != 0)
//					return;
//			}
//		}
		//add by huangtt 20140320
    	if(getValueString("PAT_NAME").equals("")){
    		if(getValueString("FIRST_NAME").equals("") && !getValueString("LAST_NAME").equals("")){
    			this.messageBox("������firstName!");
    			this.grabFocus("FIRST_NAME");
                return ;
    		}else if(!getValueString("FIRST_NAME").equals("") && getValueString("LAST_NAME").equals("")){
    			this.messageBox("������lastName!");
    			this.grabFocus("LAST_NAME");
                return ;
    		}else if(!getValueString("FIRST_NAME").equals("") && !getValueString("LAST_NAME").equals("")){
    			this.setValue("PAT_NAME1", getValueString("FIRST_NAME")+" "+getValueString("LAST_NAME"));
    			 if(this.messageBox("������ֵ", "�Ƿ�firstName��lastName�ϲ���ֵ��������", 0) != 0) {
    				 this.messageBox("��������Ϊ��!");
    	             return;
    		     } 
    			this.setValue("PAT_NAME", getValueString("FIRST_NAME")+" "+getValueString("LAST_NAME"));
    			
    		}else if(getValueString("FIRST_NAME").equals("") && getValueString("LAST_NAME").equals("")){
    			//if(this.modifyFlg || action.equals("NEW")){
	    			this.messageBox("��������Ϊ��!");
	                this.grabFocus("PAT_NAME");
	                return ;
    			//}
    		}
    	}else{
    		if(getValueString("FIRST_NAME").equals("") && !getValueString("LAST_NAME").equals("")){
    			this.messageBox("������firstName!");
    			this.grabFocus("FIRST_NAME");
                return ;
    		}else if(!getValueString("FIRST_NAME").equals("") && getValueString("LAST_NAME").equals("")){
    			this.messageBox("������lastName!");
    			this.grabFocus("LAST_NAME");
                return ;
    		}else if(!getValueString("FIRST_NAME").equals("") && !getValueString("LAST_NAME").equals("")){
    			this.setValue("PAT_NAME1", getValueString("FIRST_NAME")+" "+getValueString("LAST_NAME"));	
    		}
    	}
    	//add by sunqy 20140606 ----start----
    	double height = this.getValueDouble("HEIGHT");
//    	System.out.println("----height-----:"+height);
    	if(height>300 || height <0){
    		this.messageBox("������뷶Χ��0-300CM!");
    		this.grabFocus("HEIGHT");
    		return;
    	}
    	
		// add by wangbin 2015/1/8 �����������޸�Ϊ������ START
		//if(this.modifyFlg || action.equals("NEW")){
			//add by sunqy 20140606 ----end----
			if (!this.emptyTextCheck("SEX_CODE"))
				return;
			
			if (StringUtils.isEmpty(this.getValueString("BIRTH_DATE"))) {
				this.messageBox("�������������");
				this.grabFocus("BIRTH_DATE");
				return;
			}
		
			// �жϳ��������뵱ǰ����
			boolean dateCheck = SystemTool.getInstance().getDate().before(
					StringTool.getTimestamp(this.getValueString("BIRTH_DATE"),
							"yyyy-MM-dd"));
			if (dateCheck) {
				this.messageBox("�������ڲ��ܴ��ڵ�ǰ����");
				return;
			}
		//}
		// add by wangbin 2015/1/8 �����������޸�Ϊ������ RND
		
		onSavePopedem() ;
		if (action.equals("NEW")) {
			// pat = new Pat();
			pat = this.modifyValue();
			if (!pat.onNew())
				this.messageBox("E0005"); // ʧ��
			else {
				this.messageBox("P0005"); // �ɹ�
				setValue("MR_NO", pat.getMrNo());
				callFunction("UI|PHOTO_BOTTON|setEnabled", true);
				action = "EDIT";
				
				//add by huangtt 20140401 CRM----start
    			if(crmFlg){
    				TParm parm = new TParm();
    				parm.setData("MR_NO", pat.getMrNo());
					parm.setData("PAT_NAME", pat.getName());
					parm.setData("PY1", pat.getPy1());
					parm.setData("FIRST_NAME", pat.getFirstName());
					parm.setData("LAST_NAME", pat.getLastName());
					parm.setData("OLDNAME", "");
					parm.setData("ID_TYPE", pat.getIdType());
					parm.setData("IDNO", pat.getIdNo());
					parm.setData("SEX_CODE", pat.getSexCode());
					parm.setData("BIRTH_DATE", pat.getBirthday());
					parm.setData("NATION_CODE", pat.getNationCode());
					parm.setData("NATION_CODE2", pat.getSpeciesCode());
					parm.setData("MARRIAGE", pat.getMarriageCode());
					parm.setData("RESID_POST_CODE", pat.getResidPostCode());
					parm.setData("RESID_ADDRESS", pat.getResidAddress());
					parm.setData("POST_CODE", pat.getPostCode());
					parm.setData("CURRENT_ADDRESS", pat.getCurrentAddress());
					parm.setData("CELL_PHONE", pat.getCellPhone());
					parm.setData("SPECIAL_DIET", pat.getSpecialDiet().getValue());
					parm.setData("E_MAIL", pat.getEmail());
					parm.setData("TEL_HOME", pat.getTelHome());
					parm.setData("CTZ1_CODE", pat.getCtz1Code());
					parm.setData("CTZ2_CODE", pat.getCtz2Code());
					parm.setData("CTZ3_CODE", pat.getCtz3Code());
					parm.setData("HOMEPLACE_CODE", pat.gethomePlaceCode());
					parm.setData("RELIGION", pat.getReligionCode());
					parm.setData("BIRTH_HOSPITAL", "");
					parm.setData("SCHOOL_NAME", "");
					parm.setData("SCHOOL_TEL", "");
					parm.setData("SOURCE", "");
					parm.setData("INSURANCE_COMPANY1_CODE", "");
					parm.setData("INSURANCE_COMPANY2_CODE", "");
					parm.setData("INSURANCE_NUMBER1", "");
					parm.setData("INSURANCE_NUMBER2", "");
					parm.setData("GUARDIAN1_NAME", "");
					parm.setData("GUARDIAN1_RELATION", "");
					parm.setData("GUARDIAN1_TEL", "");
					parm.setData("GUARDIAN1_PHONE", "");
					parm.setData("GUARDIAN1_COM", "");
					parm.setData("GUARDIAN1_ID_TYPE", "");
					parm.setData("GUARDIAN1_ID_CODE", "");
					parm.setData("GUARDIAN1_EMAIL", "");
					parm.setData("GUARDIAN2_NAME", "");
					parm.setData("GUARDIAN2_RELATION", "");
					parm.setData("GUARDIAN2_TEL", "");
					parm.setData("GUARDIAN2_PHONE", "");
					parm.setData("GUARDIAN2_COM", "");
					parm.setData("GUARDIAN2_ID_TYPE", "");
					parm.setData("GUARDIAN2_ID_CODE", "");
					parm.setData("GUARDIAN2_EMAIL", "");
					parm.setData("REG_CTZ1_CODE", "");
					parm.setData("REG_CTZ2_CODE", "");
					parm.setData("FAMILY_DOCTOR", "");
					parm.setData("ACCOUNT_MANAGER_CODE", "");
					parm.setData("MEM_TYPE", "");
					parm.setData("START_DATE", "");
					parm.setData("END_DATE", "");
					parm.setData("BUY_MONTH_AGE", "");
		    		parm.setData("HAPPEN_MONTH_AGE", "");
		    		parm.setData("MEM_CODE", "");
			    	parm.setData("REASON", "");
			    	parm.setData("START_DATE_TRADE", "");
			    	parm.setData("END_DATE_TRADE", "");
			    	parm.setData("MEM_FEE", "");
			    	parm.setData("INTRODUCER1","");
			    	parm.setData("INTRODUCER2", "");
			    	parm.setData("INTRODUCER3", "");
			    	parm.setData("DESCRIPTION", "");	
        			
                    TParm parmCRM = TIOM_AppServer.executeAction("action.reg.REGCRMAction","createMember1",parm);
        			if(!parmCRM.getBoolean("flg", 0)){
        				this.messageBox("CRM��Ϣ����ͬ��ʧ�ܣ�");
        			}
    			}
    			
    			 //add by huangtt 20140401 CRM----end
			}
		} else {
			// zhangyong20110411 begin
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					"SELECT * FROM SYS_PATINFO WHERE MR_NO = '" + pat.getMrNo()
							+ "'"));
			pat = this.modifyValue();
			if (pat.onSave()) {
				this.messageBox("P0005"); // �ɹ�
				writeLog(pat.getMrNo(), parm.getRow(0), "UPDATE");
				
				TParm mroParm = new TParm();
				mroParm.setData("PagePatInfo", this.getPagePatInfo().getData());// ��һҳǩ����
				TParm result = TIOM_AppServer.executeAction(
						"action.mro.MRORecordAction", "updateDate", mroParm);
				if (result.getErrCode() < -1) {
					this.messageBox(result.getErrText());
				}
				 //add by huangtt 20140401 CRM----start
				if(crmFlg){
					parm = new TParm();					
					parm.setData("MR_NO", pat.getMrNo());
					parm.setData("PAT_NAME", pat.getName());
					parm.setData("PY1", pat.getPy1());
					parm.setData("FIRST_NAME", pat.getFirstName());
					parm.setData("LAST_NAME", pat.getLastName());
					parm.setData("OLDNAME", oldName);
					parm.setData("ID_TYPE", pat.getIdType());
					parm.setData("IDNO", pat.getIdNo());
					parm.setData("SEX_CODE", pat.getSexCode());
					parm.setData("BIRTH_DATE", pat.getBirthday());
					parm.setData("NATION_CODE", pat.getNationCode());
					parm.setData("NATION_CODE2", pat.getSpeciesCode());
					parm.setData("MARRIAGE", pat.getMarriageCode());
					parm.setData("RESID_POST_CODE", pat.getResidPostCode());
					parm.setData("RESID_ADDRESS", pat.getResidAddress());
					parm.setData("POST_CODE", pat.getPostCode());
					parm.setData("CURRENT_ADDRESS", pat.getCurrentAddress());
					parm.setData("CELL_PHONE", pat.getCellPhone());
					parm.setData("SPECIAL_DIET", pat.getSpecialDiet().getValue());
					parm.setData("E_MAIL", pat.getEmail());
					parm.setData("TEL_HOME", pat.getTelHome());
					parm.setData("CTZ1_CODE", pat.getCtz1Code());
					parm.setData("CTZ2_CODE", pat.getCtz2Code());
					parm.setData("CTZ3_CODE", pat.getCtz3Code());
					parm.setData("HOMEPLACE_CODE", pat.gethomePlaceCode());
					parm.setData("RELIGION", pat.getRelationCode());
					parm.setData("BIRTH_HOSPITAL", mem.getValue("BIRTH_HOSPITAL", 0));
					parm.setData("SCHOOL_NAME", mem.getValue("SCHOOL_NAME", 0));
					parm.setData("SCHOOL_TEL", mem.getValue("SCHOOL_TEL", 0));
					parm.setData("SOURCE", mem.getValue("SOURCE", 0));
					parm.setData("INSURANCE_COMPANY1_CODE", mem.getValue("INSURANCE_COMPANY1_CODE", 0));
					parm.setData("INSURANCE_COMPANY2_CODE", mem.getValue("INSURANCE_COMPANY2_CODE", 0));
					parm.setData("INSURANCE_NUMBER1", mem.getValue("INSURANCE_NUMBER1", 0));
					parm.setData("INSURANCE_NUMBER2", mem.getValue("INSURANCE_NUMBER2", 0));
					parm.setData("GUARDIAN1_NAME", mem.getValue("GUARDIAN1_NAME", 0));
					parm.setData("GUARDIAN1_RELATION", mem.getValue("GUARDIAN1_RELATION", 0));
					parm.setData("GUARDIAN1_TEL", mem.getValue("GUARDIAN1_TEL", 0));
					parm.setData("GUARDIAN1_PHONE", mem.getValue("GUARDIAN1_PHONE", 0));
					parm.setData("GUARDIAN1_COM", mem.getValue("GUARDIAN1_COM", 0));
					parm.setData("GUARDIAN1_ID_TYPE", mem.getValue("GUARDIAN1_ID_TYPE", 0));
					parm.setData("GUARDIAN1_ID_CODE", mem.getValue("GUARDIAN1_ID_CODE", 0));
					parm.setData("GUARDIAN1_EMAIL", mem.getValue("GUARDIAN1_EMAIL", 0));
					parm.setData("GUARDIAN2_NAME", mem.getValue("GUARDIAN2_NAME", 0));
					parm.setData("GUARDIAN2_RELATION", mem.getValue("GUARDIAN2_RELATION", 0));
					parm.setData("GUARDIAN2_TEL", mem.getValue("GUARDIAN2_TEL", 0));
					parm.setData("GUARDIAN2_PHONE", mem.getValue("GUARDIAN2_PHONE", 0));
					parm.setData("GUARDIAN2_COM", mem.getValue("GUARDIAN2_COM", 0));
					parm.setData("GUARDIAN2_ID_TYPE", mem.getValue("GUARDIAN2_ID_TYPE", 0));
					parm.setData("GUARDIAN2_ID_CODE", mem.getValue("GUARDIAN2_ID_CODE", 0));
					parm.setData("GUARDIAN2_EMAIL", mem.getValue("GUARDIAN2_EMAIL", 0));
					parm.setData("REG_CTZ1_CODE", mem.getValue("REG_CTZ1_CODE", 0));
					parm.setData("REG_CTZ2_CODE", mem.getValue("REG_CTZ2_CODE", 0));
					parm.setData("FAMILY_DOCTOR", mem.getValue("FAMILY_DOCTOR", 0));
					parm.setData("ACCOUNT_MANAGER_CODE", mem.getValue("ACCOUNT_MANAGER_CODE", 0));
					parm.setData("MEM_TYPE", mem.getValue("MEM_CODE", 0));
					parm.setData("START_DATE", "".equals(mem.getValue("START_DATE", 0)) ? "" : mem.getValue("START_DATE", 0).substring(0, 10));
					parm.setData("END_DATE", "".equals(mem.getValue("END_DATE", 0)) ? "" : mem.getValue("END_DATE", 0).substring(0, 10));
					String sDate = mem.getValue("START_DATE", 0);
			    	String eDate = mem.getValue("END_DATE", 0);
			    	Timestamp date = SystemTool.getInstance().getDate();
			    	if(sDate.length()>0 && eDate.length()>0){
			    		//���㹺������
			    		int buyMonthAge = getBuyMonth(sDate.substring(0, 10).replaceAll("-", ""),
			    				eDate.substring(0, 10).replaceAll("-", ""));
			    		
			    		//��������
			    		int currMonthAge = getBuyMonth(sDate.substring(0, 10).replaceAll("-", ""),
			    				date.toString().substring(0, 10).replaceAll("-", ""));
			    		
			    		parm.setData("BUY_MONTH_AGE", String.valueOf(buyMonthAge));
			    		parm.setData("HAPPEN_MONTH_AGE", String.valueOf(currMonthAge));
			    	}else{
			    		parm.setData("BUY_MONTH_AGE", "");
			    		parm.setData("HAPPEN_MONTH_AGE", "");
			    	}
			    	parm.setData("MEM_CODE", memTrade.getValue("MEM_CODE", 0));
			    	parm.setData("REASON", memTrade.getValue("REASON", 0));
			    	parm.setData("START_DATE_TRADE", "".equals(memTrade.getValue("START_DATE", 0))? "": memTrade.getValue("START_DATE", 0).substring(0, 10));
			    	parm.setData("END_DATE_TRADE", "".equals(memTrade.getValue("END_DATE", 0))? "": memTrade.getValue("END_DATE", 0).substring(0, 10));
			    	parm.setData("MEM_FEE", memTrade.getValue("MEM_FEE", 0));
			    	parm.setData("INTRODUCER1", memTrade.getValue("INTRODUCER1", 0));
			    	parm.setData("INTRODUCER2", memTrade.getValue("INTRODUCER2", 0));
			    	parm.setData("INTRODUCER3", memTrade.getValue("INTRODUCER3", 0));
			    	parm.setData("DESCRIPTION", memTrade.getValue("DESCRIPTION", 0));	
					
					System.out.println("CRM��Ϣ����ͬ��==="+parm);
					TParm parmCRM = TIOM_AppServer.executeAction("action.reg.REGCRMAction","updateMemberByMrNo1",parm);
					if(!parmCRM.getBoolean("flg", 0)){
						this.messageBox("CRM��Ϣ����ͬ��ʧ�ܣ�");
					}
				}
				
				//add by huangtt 20140401 CRM----end
				
			} else
				this.messageBox("E0005"); // ʧ��
			// zhangyong20110411 end

		}
		// ����ǽ��������� ����MR_NO
		if ("HRM".equals(recpt)) {
			this.setReturnValue(pat.getMrNo());
			this.closeWindow();
		}
	}

	/**
	 * ����
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
	 * //ע���������
	 */
	public void onRegist() {
		// ע���������
		JMFRegistry jmfr = new JMFRegistry();
		jmfr.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent event) {
				event.getWindow().dispose();
				System.exit(0);
			}

		});
		jmfr.setVisible(true);

	}

	/**
	 * ������Ƭ
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
			byte[] data = FileTool.getByte(localFileName);
			new File(localFileName).delete();

			String root = TIOM_FileServer.getRoot();
			dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
			
			//�����Ŵ��ڵ���10λ����
            if(mrNo.length()>=10){
            	dir = root + dir + mrNo.substring(0, 3) + "\\"
                + mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";
            //������С��10λ����
            }else{
            	dir = root + dir + mrNo.substring(0, 2) + "\\"
                + mrNo.substring(2, 4) + "\\" + mrNo.substring(4, 7) + "\\";
            }
            //

			TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(), dir
					+ photoName, data);
		} catch (Exception e) {
		}
		this.viewPhoto(pat.getMrNo());
	}

	/**
	 * ����
	 */
	public void onNew() {
		this.onClear();
		this.setValue("MR_NO", "");
		action = "NEW";
		pat = new Pat();
		callFunction("UI|new|setEnabled", false);
		callFunction("UI|save|setEnabled", true);
		callFunction("UI|delete|setEnabled", false);
		callFunction("UI|MR_NO|setEnabled", false);
		onSavePopedem() ;
	}

	/**
	 * ͬͨ�ŵ�ַ
	 */
	public void onSameto1() {
		setValue("RESID_POST_CODE", getValue("POST_CODE"));
//		this.onRESIDPOST();
//		callFunction("UI|SESSION_CODE|onQuery");
		setValue("RESID_ADDRESS", getValue("ADDRESS"));

	}
	/**
	 * ͬͨ�ŵ�ַ
	 */
	public void onSameto3() {
		setValue("POST_COMPANY", getValue("POST_CODE"));
//		this.onRESIDPOST();
//		callFunction("UI|SESSION_CODE|onQuery");
		setValue("ADDRESS_COMPANY", getValue("ADDRESS"));

	}

	/**
	 * ͬͨ�ŵ�ַ
	 */
	public void onSameto2() {
		setValue("CONTACTS_ADDRESS", getValue("ADDRESS"));
	}
	
	/**
	 * ��ⲡ����ͬ���֤��
	 */
	public void onPatIdNo() {
		String idNo = this.getValueString("IDNO");
		if (StringUtil.isNullString(idNo)) {
			return;
		}
		// REPORT_DATE;PAT_NAME;IDNO;SEX_CODE;BIRTH_DATE;POST_CODE;ADDRESS
		String selPat = "SELECT   A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
				+ " POST_CODE, ADDRESS, A.MR_NO,B.EKT_CARD_NO "
				+ " , ID_TYPE, CURRENT_ADDRESS"  //add by huangtt 20131106
				+ " FROM SYS_PATINFO A,EKT_ISSUELOG B "
				+ " WHERE A.IDNO = '"
				+ idNo
				+ "'  " 
//				+ " AND A.ID_TYPE = '"+idType+"'"  //add by huangtt 20131106
				+ " AND A.MR_NO = B.MR_NO (+) "
				+ " ORDER BY A.OPT_DATE";
		// ===zhangp 20120319 end
		TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
		if (same.getErrCode() != 0) {
			this.messageBox_(same.getErrText());
		}
		// ѡ�񲡻���Ϣ
		if (same.getCount("MR_NO") > 0) {
			int sameCount = this.messageBox("��ʾ��Ϣ", "������ͬ������Ϣ,�Ƿ�������������Ϣ", 0);
			if (sameCount != 1) {
				this.grabFocus("CTZ1_CODE");
				return;
			}
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				this.setValue("MR_NO", patParm.getValue("MR_NO"));
				idNoFlg=true;
				this.onQuery();
				return;
			}
		}
		this.grabFocus("CTZ1_CODE");
	}
	
	/**
	 * ��ⲡ����ͬ����
	 */
	public void onPatNameSex() {
		String patName = this.getValueString("PAT_NAME");
//		setPatName1();
		if (StringUtil.isNullString(patName)) {
			return;
		}
		//add by huangtt 20131126
		String sexCode = this.getValueString("SEX_CODE");
		if (StringUtil.isNullString(sexCode)) {
			return;
		}
		String selPat = "SELECT  DISTINCT(A.MR_NO) AS MR_NO, A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
				+ " POST_CODE, ADDRESS, B.EKT_CARD_NO " 
				+ " , ID_TYPE, CURRENT_ADDRESS"  //add by huangtt 20131106
				+ " FROM SYS_PATINFO A,EKT_ISSUELOG B "
				+ " WHERE PAT_NAME = '"
				+ patName
				+ "' AND  SEX_CODE='"
				+ sexCode    // add by huangtt 20131126
				+ "' "
				+ " AND A.MR_NO = B.MR_NO (+) "
				+ " ORDER BY A.OPT_DATE,A.BIRTH_DATE";
		// ===zhangp 20120319 end
		TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
		if (same.getErrCode() != 0) {
			this.messageBox_(same.getErrText());
		}
		// ѡ�񲡻���Ϣ
		if (same.getCount("MR_NO") > 0) {
			int sameCount = this.messageBox("��ʾ��Ϣ", "������ͬ����������Ϣ,�Ƿ�������������Ϣ", 0);
			if (sameCount != 1) {
				// �������
				((TTextField) getComponent("IDNO")).grabFocus();
				return;
			}
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				this.setValue("MR_NO", patParm.getValue("MR_NO"));
				nameSexFlg=true;
				this.onQuery();
				return;
			}
		}
		// �������
		((TTextField) getComponent("IDNO")).grabFocus();
	}

	/**
	 * ���
	 */
	public void onClear() {
		((TRadioButton) this.getComponent("tRadioButton_0")).setSelected(true);
		pat = new Pat();
		TPanel photo = (TPanel) this.getComponent("VIEW_PANEL");
		Image image = null;

		Pic pic = new Pic(image);
		pic.setSize(photo.getWidth(), photo.getHeight());
		pic.setLocation(0, 0);
		photo.removeAll();
		photo.add(pic);
		pic.repaint();
		nameSexFlg=false; //add by haungtt 20140417
		idNoFlg=false; //add by haungtt 20140417
		callFunction("UI|PHOTO_BOTTON|setEnabled", false);
		callFunction("UI|new|setEnabled", true);
		callFunction("UI|save|setEnabled", false);
		callFunction("UI|MR_NO|setEnabled", true);
		clearValue("E_MAIL;COMPANY_DESC;HEIGHT;WEIGHT;BLOOD_TYPE;CELL_PHONE;TEL_COMPANY;SPECIAL_DIET;TEL_HOME;SPECIES_CODE;OCC_CODE;MARRIAGE_CODE;NATION_CODE");
		clearValue("SPOUSE_IDNO;FATHER_IDNO;MOTHER_IDNO;EDUCATION_CODE;RELIGION_CODE");
		clearValue("FIRST_ADM_DATE;DEAD_DATE;RCNT_OPD_DATE;RCNT_OPD_DEPT;RCNT_EMG_DATE;RCNT_EMG_DEPT;RCNT_IPD_DATE;RCNT_IPD_DEPT;RCNT_MISS_DATE;RCNT_MISS_DEPT");
		clearValue("ADULT_EXAM_DATE;SMEAR_RCNT_DATE;KID_EXAM_RCNT_DATE;KID_INJ_RCNT_DATE;DESCRIPTION;BORNIN_FLG;NEWBORN_SEQ;PREMATURE_FLG;HANDICAP_FLG;BLACK_FLG");
		clearValue("NAME_INVISIBLE_FLG;LAW_PROTECT_FLG;LMP_DATE;BREASTFEED_STARTDATE;BREASTFEED_ENDDATE;PAT1_CODE;PAT2_CODE;PAT3_CODE;PREGNANT_DATE;EDUCATION_CODE");
		clearValue("SPOUSE_IDNO;FATHER_IDNO;MOTHER_IDNO;EDUCATION_CODE;RELIGION_CODE");
		clearValue("CONTACTS_NAME;RELATION_CODE;CONTACTS_ADDRESS;tComboBox_9;tComboBox_10;CONTACTS_TEL;RESID_POST_CODE;tComboBox_7;tComboBox_8;RESID_ADDRESS");
		clearValue("POST_CODE;ADDRESS");
		clearValue("PAT_NAME;PY1;PAT_NAME1;PY2;BIRTH_DATE;SEX_CODE;MERGE_FLG;MERGE_TOMRNO;FOREIGNER_FLG;CTZ1_CODE;CTZ2_CODE;IDNO;MR_NO;IPD_NO;CTZ3_CODE;HOMEPLACE_CODE");
		clearValue("BIRTHPLACE;ADDRESS_COMPANY;POST_COMPANY");
		clearValue("NHICARD_NO;NHI_NO;PAT_BELONG");
		clearValue("RCNT_COUNT_O;RCNT_COUNT_E;RCNT_COUNT_I");//�ż��סԺ����
		clearValue("ID_TYPE;FIRST_NAME;LAST_NAME;CURRENT_ADDRESS");
		onSavePopedem() ;
	}

//	/**
//	 * ͨ���ʱ�ĵõ�ʡ��
//	 */
//	public void onPost() {
//		String post = getValueString("POST_CODE");
//		if (post == null || "".equals(post)) {
//			return;
//		}
//		TParm parm = this.getPOST_CODE(post);
//		setValue("POST_P", parm.getData("POST_CODE", 0) == null ? "" : parm
//				.getValue("POST_CODE", 0).substring(0, 2));
//		setValue("POST_C", parm.getValue("POST_CODE", 0).toString());
//	}

	/**
	 * �ϲ�������
	 */
	public void onMergeFlg() {
		TTextField text = (TTextField) this
				.callFunction("UI|MERGE_TOMRNO|getThis");
		text.setEditable(false);
		if ("Y".equals(this.getValue("MERGE_FLG")))
			text.setEditable(true);

	}

//	/**
//	 * �����ʱ�õ�ʡ��
//	 */
//	public void onRESIDPOST() {
//
//		String post = getValueString("RESID_POST_CODE");
//		if (post == null || "".equals(post)) {
//			return;
//		}
//		TParm parm = this.getPOST_CODE(post);
//
//		setValue("RESID_POST_P", parm.getData("POST_CODE", 0).toString()
//				.substring(0, 2));
//		setValue("JG_STAT",
//				parm.getData("POST_CODE", 0).toString().substring(0, 2));
//		setValue("CS_STAT",
//				parm.getData("POST_CODE", 0).toString().substring(0, 2));
//		setValue("RESID_POST_C", parm.getData("POST_CODE", 0).toString());
//		setValue("JG_CITY", parm.getData("POST_CODE", 0).toString());
//		setValue("CS_CITY", parm.getData("POST_CODE", 0).toString());
//	}

//	/**
//	 * ��λ�ʱ�ĵõ�ʡ��
//	 */
//	public void onCompanyPost() {
//		String post = getValueString("POST_COMPANY");
//		if (post == null || "".equals(post)) {
//			return;
//		}
//		TParm parm = this.getPOST_CODE(post);
//		setValue("COMPANY_POST_P", parm.getData("POST_CODE", 0) == null ? ""
//				: parm.getValue("POST_CODE", 0).substring(0, 2));
//		setValue("COMPANY_POST_C", parm.getValue("POST_CODE", 0).toString());
//	}

//	/**
//	 * �õ�ʡ�д���
//	 * 
//	 * @param post
//	 *            String
//	 * @return TParm
//	 */
//	public TParm getPOST_CODE(String post) {
//		TParm result = SYSPostTool.getInstance().getProvinceCity(post);
//		return result;
//	}
//
//	/**
//	 * ͨ�����д�����������1
//	 */
//	public void selectCode_1() {
//		this.setValue("POST_CODE", this.getValue("POST_C"));
//		this.onPost();
//	}
//
//	/**
//	 * ͨ�����д�����������2
//	 */
//	public void selectCode_2() {
//		this.setValue("RESID_POST_CODE", this.getValue("RESID_POST_C"));
//		this.onRESIDPOST();
//	}
//
//	/**
//	 * ͨ�����д�����������3
//	 */
//	public void selectCode_3() {
//		this.setValue("POST_COMPANY", this.getValue("COMPANY_POST_C"));
//		this.onCompanyPost();
//	}

	/**
	 * ɾ����δ��� ���ܲ����ƣ� ����Ȩ�޲��д˹��� ���жϴβ����ѽ��о����趨��ҵ�����ѽ����ߣ�����ɾ����
	 */
	public void onDelate() {
		pat.onDelete();
		// zhangyong20110411
		writeLog(pat.getMrNo(), new TParm(), "DELETE");
	}

	/**
	 * ���ݺ������ƴ������ĸ
	 * 
	 * @return Object
	 */
	public Object onCode() {
		if (TCM_Transform.getString(this.getValue("PAT_NAME")).length() < 1) {
			return null;
		}
		String value = TMessage.getPy(this.getValueString("PAT_NAME"));
		if (null == value || value.length() < 1) {
			return null;
		}
		this.setValue("PAT_NAME1", value);
		// �������
		((TTextField) getComponent("IDNO")).grabFocus();
		return null;
	}

	/**
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		if ("RESV".equals(recpt)) {
			TParm result = new TParm();
			result.setData("MR_NO", this.getValueString("MR_NO"));
			this.setReturnValue(result);
		}
		return true;
	}

	/**
	 * д��LOG��Ϣ
	 * 
	 * @return boolean
	 */
	private void writeLog(String mr_no, TParm patParm, String action) {
		String insert_sql = "INSERT INTO "
				+ "SYS_PATLOG(MR_NO, OPT_DATE, MODI_ITEM, ITEM_OLD, ITEM_NEW, OPT_USER, OPT_TERM ) "
				+ "VALUES('" + mr_no + "', SYSDATE, '#', '#', '#', '"
				+ Operator.getID() + "', '" + Operator.getIP() + "')";
		TParm parm = new TParm();

		String columns[] = {
				"FOREIGNER_FLG",
				"IDNO",
				"HOMEPLACE_CODE",
				"MERGE_FLG",
				"MERGE_TOMRNO", // 1-5
				"IPD_NO",
				"PAT_NAME",
				"PY1",
				"PAT_NAME1",
				"PY2", // 6-10
				"BIRTH_DATE",
				"SEX_CODE",
				"CTZ1_CODE",
				"CTZ2_CODE",
				"CTZ3_CODE", // 11-15
				"NATION_CODE",
				"SPECIES_CODE",
				"E_MAIL",
				"TEL_HOME",
				"TEL_COMPANY", // 16-20
				"OCC_CODE",
				"COMPANY_DESC",
				"CELL_PHONE",
				"MARRIAGE_CODE",
				"HEIGHT", // 21-25
				"WEIGHT",
				"BLOOD_TYPE",
				"BLOOD_RH_TYPE",
				"POST_CODE",
				"ADDRESS", // 26-30
				"RESID_POST_CODE",
				"RESID_ADDRESS",
				"CONTACTS_NAME",
				"RELATION_CODE",
				"CONTACTS_TEL", // 31-35
				"CONTACTS_ADDRESS",
				"EDUCATION_CODE",
				"RELIGION_CODE",
				"SPOUSE_IDNO",
				"FATHER_IDNO", // 36-40
				"MOTHER_IDNO",
				"FIRST_ADM_DATE",
				"DEAD_DATE",
				"RCNT_OPD_DATE",
				"RCNT_OPD_DEPT", // 41-45
				"RCNT_EMG_DATE",
				"RCNT_EMG_DEPT",
				"RCNT_IPD_DATE",
				"RCNT_IPD_DEPT",
				"RCNT_MISS_DATE", // 46-50
				"RCNT_MISS_DEPT",
				"ADULT_EXAM_DATE",
				"SMEAR_RCNT_DATE",
				"KID_EXAM_RCNT_DATE",
				"KID_INJ_RCNT_DATE", // 51-55
				"LMP_DATE", "BREASTFEED_STARTDATE",
				"BREASTFEED_ENDDATE",
				"PAT1_CODE",
				"PAT2_CODE", // 56-60
				"PAT3_CODE", "PREGNANT_DATE", "DESCRIPTION",
				"BORNIN_FLG",
				"PREMATURE_FLG", // 61-65
				"HANDICAP_FLG", "BLACK_FLG", "NAME_INVISIBLE_FLG",
				"LAW_PROTECT_FLG", "MR_NO", // 66-70
				"DELETE_FLG", "BIRTHPLACE", "ADDRESS_COMPANY", "POST_COMPANY",// 71
				"NHI_NO","NHICARD_NO"
		};

		String columnNames[] = { "�����ע��", "���֤��", "������", "�ϲ�ע��", "ĸ�׵Ĳ�����", // 1-5
				"סԺ��", "����", "ƴ����", "����2", "������", // 6-10
				"��������", "�Ա����", "���һ", "��ݶ�", "�����", // 11-15
				"��������", "����", "����", "����绰", "��λ�绰", // 16-20
				"ְҵ������", "������λ", "�ֻ�����", "����״̬", "���", // 21-25
				"����", "Ѫ��", "RHѪ��", "�ʱ�", "ͨ�ŵ�ַ", // 26-30
				"�����ʱ�", "������ַ", "������ϵ��", "������ϵ�˹�ϵ", "������ϵ�˵绰", // 31-35
				"������ϵ�˵�ַ", "�����̶ȴ���", "�ڽ�", "��ż���֤��", "�������֤��", // 36-40
				"ĸ�����֤��", "��������", "��������", "�����������", "�������Ʊ�", // 41-45
				"�����������", "�������Ʊ�", "���סԺ����", "���סԺ�Ʊ�", "���ˬԼ����", // 46-50
				"���ˬԼ�Ʊ�", "������˽�������", "���ĨƬ�������", "����׶���������", "����׶�ע������", // 51-55
				"LMP����", "������ʼ��", "��������", "������״̬һ", "������״̬��", // 56-60
				"������״̬��", "Ԥ����", "��ע", "��Ժ����", "�����", // 61-65
				"�м�", "������", "����ע��", "�������ܱ�������", "������", // 66-70
				"ɾ��", "����", "��λ��ַ", "��λ�ʱ�",// 71
				"��������","ҽ������"
		};

		if ("UPDATE".equals(action)) {
			for (int i = 0; i < columns.length; i++) {
				if ("DELETE_FLG".equals(columns[i])) {
					continue;
				}
				if ("BLOOD_RH_TYPE".equals(columns[i])) {
					String rh_type = "";
					if ("Y".equals(this.getValueString("BLOOD_RH_TYPE"))) {
						rh_type = "+";
					} else if ("Y"
							.equals(this.getValueString("tRadioButton_3"))) {
						rh_type = "-";
					} else {
						rh_type = "";
					}
					if (!rh_type.equals(patParm.getData(columns[i]))) {
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", patParm.getData(columns[i]));
						parm.addData("ITEM_NEW", rh_type);
					}
					continue;
				}

				if ("BIRTH_DATE".equals(columns[i])
						|| "FIRST_ADM_DATE".equals(columns[i])
						|| "RCNT_OPD_DATE".equals(columns[i])
						|| "RCNT_EMG_DATE".equals(columns[i])
						|| "RCNT_IPD_DATE".equals(columns[i])
						|| "RCNT_MISS_DATE".equals(columns[i])
						|| "ADULT_EXAM_DATE".equals(columns[i])
						|| "KID_EXAM_RCNT_DATE".equals(columns[i])
						|| "LMP_DATE".equals(columns[i])
						|| "PREGNANT_DATE".equals(columns[i])
						|| "DEAD_DATE".equals(columns[i])
						|| "SMEAR_RCNT_DATE".equals(columns[i])
						|| "KID_INJ_RCNT_DATE".equals(columns[i])
						|| "BREASTFEED_STARTDATE".equals(columns[i])
						|| "BREASTFEED_ENDDATE".equals(columns[i])) {
					String new_date_time = this.getValueString(columns[i]);
					String old_date_time = TypeTool.getString(patParm
							.getData(columns[i]));
					if (old_date_time != null && old_date_time.length() >= 10) {
						new_date_time = new_date_time.substring(0, 10);
						old_date_time = old_date_time.substring(0, 10);
						if (!old_date_time.equals(new_date_time)) {
							parm.addData("MODI_ITEM", columnNames[i]);
							parm.addData("ITEM_OLD", old_date_time);
							parm.addData("ITEM_NEW", new_date_time);
						} else {
							continue;
						}
					} else {
						if (new_date_time != null
								&& new_date_time.length() > 10) {
							parm.addData("MODI_ITEM", columnNames[i]);
							parm.addData("ITEM_OLD", "");
							parm.addData("ITEM_NEW",
									new_date_time.substring(0, 10));
						} else {
							continue;
						}
					}
				} else if ("HEIGHT".equals(columns[i])
						|| "WEIGHT".equals(columns[i])) {
					double new_double = TypeTool
							.getDouble(getValueString(columns[i]));
					double old_double = TypeTool.getDouble(patParm
							.getData(columns[i]));
					if (new_double != old_double) {
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", old_double);
						parm.addData("ITEM_NEW", new_double);
					}
				} else {
					if (!this.getValueString(columns[i]).equals(
							patParm.getData(columns[i]))) {
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", patParm.getData(columns[i]));
						parm.addData("ITEM_NEW",
								this.getValueString(columns[i]));
					}
				}
			}
		} else {
			for (int i = 0; i < columns.length; i++) {
				parm.addData("MODI_ITEM", "ɾ��");
				parm.addData("ITEM_OLD", "N");
				parm.addData("ITEM_NEW", "Y");
			}
		}
		parm.setData("SQL", insert_sql);
		if (parm == null || parm.getCount("MODI_ITEM") <= 0) {
			return;
		} else {
			// System.out.println("parm----"+parm);
			// ִ����������
			TParm result = TIOM_AppServer.executeAction(
					"action.sys.SYSWriteLogAction", "onSYSPatLog", parm);
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("LOGд��ʧ�ܣ�");
			} else {
				// this.messageBox("LOGд��ɹ���");
			}
		}
	}

	/**
	 * ����������סԺ���� duzhw add 20140317
	 * @param pat
	 */
	public void setRcntCount(Pat pat){
		//�����������
		TParm rcntCountO = new TParm(TJDODBTool.getInstance().select(getRcntCountOESql(pat, "O")));
		this.setValue("RCNT_COUNT_O", rcntCountO.getValue("RCNT_COUNT", 0));
		//���ü������
		TParm rcntCountE = new TParm(TJDODBTool.getInstance().select(getRcntCountOESql(pat, "E")));
		this.setValue("RCNT_COUNT_E", rcntCountE.getValue("RCNT_COUNT", 0));
		//����סԺ����
		TParm rcntCountI = new TParm(TJDODBTool.getInstance().select(getRentCountISql(pat)));
		this.setValue("RCNT_COUNT_I", rcntCountI.getValue("RCNT_COUNT", 0));
	}
	/**
	 * ͳ���ż������sql
	 */
	public String getRcntCountOESql(Pat pat,String admType){
		String sql = "SELECT COUNT(1) AS RCNT_COUNT FROM REG_PATADM WHERE MR_NO = '"+pat.getMrNo()+"' " +
				" AND ADM_TYPE = '"+admType+"' AND REGCAN_DATE IS NULL ";
		return sql;
	}
	/**
	 * ͳ��סԺ����sql
	 */
	public String getRentCountISql(Pat pat){
		String sql = "SELECT COUNT(1) AS RCNT_COUNT FROM ADM_INP WHERE MR_NO = '"+pat.getMrNo()+"' AND CANCEL_FLG = 'N'";
		return sql;
	}
	
	/**
	 * ��ȡ��������
	 */
	public int getBuyMonth(String s, String s1){
		Date m=new Date();
		Date d = null;
		Date d1 = null;
		DateFormat df=new SimpleDateFormat("yyyyMMdd");
		try {  
		    d = df.parse(s);
		    d1=df.parse(s1);
		    }catch (ParseException e){    
		    	e.printStackTrace(); }
		    Calendar c = Calendar.getInstance();		    
		    c.setTime(d);		  
		    int year = c.get(Calendar.YEAR);
		    int month = c.get(Calendar.MONTH);		    
		    c.setTime(d1);
		    int year1 = c.get(Calendar.YEAR);
		    int month1 = c.get(Calendar.MONTH);		    
		    int result;
		    if(year==year1){
		    	result=month1-month;//�������������£����·ݲ�
		    }else{
		    	result=12*(year1-year)+month1-month;//�������������£����·ݲ�
		    }
		return result;
	}
	
	public void onCrmQuery(){
		TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
//		String ip = config.getString("", "WEB_SERVICES_IP_CRM");
//		String path = config.getString("", "crm.path");
//		String key = config.getString("", "crm.key");
//		String url = "http://"+ip+path+"?key="+key;
		String hisIp = config.getString("","his.ip");
		String url = "http://"+hisIp+":8080/web/findOrderInfo.html";
		SystemTool aa = new SystemTool();
		aa.OpenIE(url);
//		try {
//			Runtime.getRuntime().exec("rundll32  url.dll,FileProtocolHandler "+
//					"http://localhost:8080/web/findOrderInfo.html" );
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//http://192.168.8.231:8081/crm/order/visitNum/findOrderInfo.action?key=C5F93FA77F381D70A4893B51A6264F9D
//		http://localhost:8080/web/findOrderInfo.html
	}
	/**
     * ������ѯ
     */
    public void onQueryPat() {
        TParm sendParm = new TParm();
        TParm reParm = (TParm)this.openDialog(
                "%ROOT%\\config\\adm\\ADMPatQuery.x", sendParm);
        if (reParm == null)
            return;
        this.setValue("MR_NO", reParm.getValue("MR_NO"));
        this.onQuery();
    }
    /**
     * ��ȡ������Ϣҳ���Ӧ������ҳ��һҳǩ���ֵ�����
     * @return
     */
    public TParm getPagePatInfo() {
		TParm parm = new TParm();
		parm.setData("PAT_NAME", this.getValue("PAT_NAME")); // ����
		parm.setData("IDNO", this.getValue("IDNO")); // ���֤��
		parm.setData("SEX", this.getValue("SEX_CODE")); // �Ա�
		if (this.getValue("BIRTH_DATE") == null)
			parm.setData("BIRTH_DATE", new TNull(Timestamp.class)); // ����
		else
			parm.setData("BIRTH_DATE", this.getValue("BIRTH_DATE")); // ����
		parm.setData("AGE", com.javahis.util.StringUtil.showAge(
		pat.getBirthday(), Timestamp.valueOf(this.getValueString("BIRTH_DATE")))); // ����
		parm.setData("MARRIGE", this.getValue("MARRIAGE_CODE")); // ����
		parm.setData("NATION", this.getValue("NATION_CODE")); // ����
		//parm.setData("IN_COUNT", this.getValue("TP1_INNUM")); // סԺ����
		parm.setData("FOLK", this.getValue("SPECIES_CODE")); // ����
		parm.setData("CTZ1_CODE", this.getValue("CTZ1_CODE")); // ���ʽ
		parm.setData("HOMEPLACE_CODE", this.getValueString("HOMEPLACE_CODE"));// �����ش���
		parm.setData("H_ADDRESS", this.getValue("RESID_ADDRESS")); // ������ַ
		parm.setData("H_POSTNO", this.getValue("RESID_POST_CODE")); // �����ʱ�
		parm.setData("OCCUPATION", this.getValue("OCC_CODE")); // ְҵ
		parm.setData("OFFICE", this.getValue("COMPANY_DESC")); // ��λ
		parm.setData("O_TEL", this.getValue("TEL_COMPANY")); // ��λ�绰
		parm.setData("O_ADDRESS", this.getValue("ADDRESS_COMPANY")); // ��λ��ַ
		parm.setData("O_POSTNO", this.getValue("POST_COMPANY")); // ��λ�ʱ�
		parm.setData("CONTACTER", this.getValue("CONTACTS_NAME")); // ��ϵ������
		parm.setData("RELATIONSHIP", this.getValue("RELATION_CODE")); // ��ϵ�˹�ϵ
		parm.setData("CONT_TEL", this.getValue("CONTACTS_TEL")); // ��ϵ�˵绰
		parm.setData("CONT_ADDRESS", this.getValue("CONTACTS_ADDRESS")); // ��ϵ�˵�ַ
		/*-------------------------------------------------------------------*/
		//parm.setData("MRO_CTZ", this.getValue("MRO_CTZ"));// ������ҳ���
		parm.setData("BIRTHPLACE", this.getValue("BIRTHPLACE"));// ����
		parm.setData("ADDRESS", this.getValue("CURRENT_ADDRESS"));// ͨ�ŵ�ַ
		parm.setData("POST_NO", this.getValue("POST_CODE"));// ͨ���ʱ�
		parm.setData("TEL", this.getValue("TEL_HOME")); // �绰
		parm.setData("NHI_NO", this.getValue("NHICARD_NO")); // �������� 
		parm.setData("NHI_CARDNO", this.getValue("NHI_NO")); // ҽ������ 
		parm.setData("NB_WEIGHT", this.getValue("NEW_BODY_WEIGHT")); // ��������
		parm.setData("MR_NO", this.getValue("MR_NO")); // ������ 
		return parm;
	}
		
}
