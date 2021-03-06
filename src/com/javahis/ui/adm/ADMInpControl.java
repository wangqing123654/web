package com.javahis.ui.adm;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMResvTool;
import jdo.adm.ADMTool;
import jdo.bil.BILLumpWorkTool;
import jdo.bil.BILPayTool;
import jdo.clp.CLPSingleDiseTool;
import jdo.ekt.EKTIO;
import jdo.hl7.Hl7Communications;
import jdo.mem.MEMLumpworkTool;
import jdo.mem.MEMPatRegisterTool;
import jdo.mem.MEMSQL;
import jdo.mro.MROBorrowTool;
import jdo.mro.MROQueueTool;
import jdo.mro.MROTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSBedTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.bluecore.cardreader.CardInfoBO;
import com.bluecore.cardreader.IdCardReaderUtil;
import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.root.client.SocketLink;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.FileTool;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.device.JMFRegistry;
import com.javahis.device.JMStudio;
import com.javahis.device.NJCityInwDriver;
import com.javahis.system.textFormat.TextFormatADMPackageType;

import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * 住院登记
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
 * Company:Javahis
 * </p>
 *
 * @author JiaoY 2008
 * @version 1.0
 */
public class ADMInpControl extends TControl {
	private static final String TTextFormat = null;
	Pat pat;
	String patType = ""; // 判断修改或新增病患
	String saveType = "NEW"; // 判断新增/修改 “NEW” 新增，“UPDATE”修改
	String caseNo = ""; // 就诊序号
	String McaseNo = ""; // 母亲就诊序号
	String BED_NO = ""; // 床位号
	String IPD_NO = "";
	String MR_NO = ""; // 病案号
	private TParm parmEKT;// 医疗卡集合
	private boolean crmFlg = true; // crm接口开关
	private TParm mem = new TParm(); // 会员参数 huangtt
	private TParm memTrade = new TParm(); // 会员交易表参数 huangtt
	private TParm reParm = new TParm();// 保存唯一预约住院数据pangben 2014-7-30
	private boolean modifyFlg = false;
	Timestamp birthDay = null;// 出生日期
	Timestamp sysDate = SystemTool.getInstance().getDate();

	public void onInit() {
		super.onInit();
		this.setMenu(false); // menu botton
		callFunction("UI|PHOTO_BOTTON|setEnabled", false);
		callFunction("UI|AGN_CODE|setEnabled", false); // 31天内再次住院等级
		callFunction("UI|AGN_INTENTION|setEnabled", false); // 31天内再次住院等级
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
		String date = df.format(SystemTool.getInstance().getDate());
		setValue("IN_DATE", StringTool.getTimestamp(date, "yyyyMMddHH")); // 预定日期
		crmFlg = StringTool.getBoolean(TConfig.getSystemValue("crm.switch"));
		if (this.getPopedem("MODIFY")) {
			modifyFlg = true;
		}
	}

	/**
	 * menu botton 显示控制
	 *
	 * @param flg
	 *            boolean
	 */
	public void setMenu(boolean flg) {
		callFunction("UI|save|setEnabled", flg); // 保存按钮
		callFunction("UI|stop|setEnabled", flg); // 取消住院
		// callFunction("UI|picture|setEnabled", flg); // 拍照
		callFunction("UI|patinfo|setEnabled", flg); // 病患资料
		callFunction("UI|bed|setEnabled", flg); // 包床
		callFunction("UI|bilpay|setEnabled", flg); // 预交金
		callFunction("UI|greenpath|setEnabled", flg); // 绿色通道
		callFunction("UI|print|setEnabled", flg); // 住院证打印
		// callFunction("UI|child|setEnabled", flg); //新生儿注记
		callFunction("UI|immunity|setEnabled", flg); // 新生儿免疫
	}

	/**
	 * 修改病患信息读取参数
	 *
	 * @param modifyPat
	 *            Pat
	 * @return Pat
	 */
	public Pat readModifyPat(Pat modifyPat) {
		// if(this.modifyFlg){//有"MODIFY"权限的才可以更新这三个字段
		modifyPat.modifyName(getValueString("PAT_NAME")); // 姓名
		modifyPat.modifySexCode(getValueString("SEX_CODE")); // 性别
		modifyPat.modifyBirthdy(TCM_Transform.getTimestamp(getValue("BIRTH_DATE"))); // 出生日期
		// }
		modifyPat.modifyCtz1Code(getValueString("PAT_CTZ")); // 付款方式1
		modifyPat.modifyhomePlaceCode(getValueString("HOMEPLACE_CODE")); // 出生地
		modifyPat.modifyOccCode(getValueString("OCC_CODE")); // 职业
		modifyPat.modifyIdNo(getValueString("IDNO")); // 身份证号
		modifyPat.modifyIdType(getValueString("ID_TYPE"));
		modifyPat.modifySpeciesCode(getValueString("SPECIES_CODE")); // 种族
		modifyPat.modifyNationCode(getValueString("NATION_CODE")); // 国籍
		modifyPat.modifyMarriageCode(getValueString("MARRIAGE_CODE")); // 婚姻状态
		modifyPat.modifyCompanyDesc(getValueString("COMPANY_DESC")); // 单位
		modifyPat.modifyTelCompany(getValueString("TEL_COMPANY")); // 公司电话
		modifyPat.modifyCellPhone(getValueString("CELL_PHONE"));// add by huangjw 20140729
		modifyPat.modifyPostCode(getValueString("POST_CODE")); // 邮编
		modifyPat.modifyResidAddress(getValueString("RESID_ADDRESS")); // 户籍地址
		modifyPat.modifyResidPostCode(getValueString("RESID_POST_CODE"));
		modifyPat.modifyContactsName(getValueString("CONTACTS_NAME"));
		modifyPat.modifyRelationCode(getValueString("RELATION_CODE"));
		modifyPat.modifyContactsTel(getValueString("CONTACTS_TEL"));
		modifyPat.modifyContactsAddress(getValueString("CONTACTS_ADDRESS"));
		modifyPat.modifyTelHome(getValueString("TEL_HOME")); // 家庭电话
		modifyPat.modifyAddress(getValueString("ADDRESS")); // 家庭住址
		modifyPat.modifyCurrentAddress(getValueString("ADDRESS"));
		modifyPat.modifyForeignerFlg(TypeTool.getBoolean(getValue("FOREIGNER_FLG"))); // 外国人注记
		// shiblmodify 20120107
		modifyPat.modifyBirthPlace(this.getValueString("BIRTHPLACE")); // 籍贯
		modifyPat.modifyCompanyAddress(this.getValueString("ADDRESS_COMPANY")); // 单位地址
		modifyPat.modifyCompanyPost(this.getValueString("POST_COMPANY")); // 单位地址
		modifyPat.modifySpecialDiet(this.getValueString("SPECIAL_DIET"));// 特殊饮食

		// add by huangjw 20140804 start
		modifyPat.modifyCtz1Code(this.getValueString("CTZ1_CODE"));// 身份一
		modifyPat.modifyCtz2Code(this.getValueString("CTZ2_CODE"));// 身份二
		modifyPat.modifyCtz3Code(this.getValueString("CTZ3_CODE"));// 身份三
		modifyPat.modifyNhiNo(this.getValueString("NHI_NO"));// 医保号(这里的医保号为空值，只是为了解决sql报错)
		// add by huangjw 20140804 end
		return modifyPat;
	}

	/**
	 * 新增/修改病患 botton 事件
	 */
	public void onNewpat() {
		// 得到新增变换checkbox
		TCheckBox checkbox = (TCheckBox) this.callFunction("UI|NEW_PAT_INFO|getThis");
		// if(pat == null || checkbox.isSelected() || this.modifyFlg){
		if ("".equals(getValue("PAT_NAME"))) {
			this.messageBox("请输入姓名");
			this.grabFocus("PAT_NAME");
			return;
		}
		if ("".equals(getValue("TEL_HOME"))) {
			this.messageBox("请输入电话");
			this.grabFocus("TEL_HOME");
			return;
		}
		if ("".equals(getValue("SEX_CODE"))) {
			this.messageBox("请选择性别");
			this.grabFocus("SEX_CODE");
			return;
		}
		if ("".equals(this.getValueString("BIRTH_DATE"))) {
			this.messageBox_("请选择出生年月");
			this.grabFocus("BIRTH_DATE");
			return;
		}

		if ("".equals(getValue("PAT_CTZ"))) {
			this.messageBox("付款方式");
			return;
		}
		// ================= 病案室要求在住院登记填写国籍和身份证号

		if (this.getTextFormat("ID_TYPE").getText().equals("")) {
			this.messageBox_("请选择证件类型");
			this.grabFocus("ID_TYPE");
			return;
		}
		if (!this.getTextFormat("ID_TYPE").getText().equals("其他")) {// moidfy by huangjw 20140801
			if (!this.getValueBoolean("FOREIGNER_FLG")) {
				if ("".equals(this.getValue("IDNO"))) {
					this.messageBox_("请输入身份证号");
					this.grabFocus("IDNO");
					return;
				}
			}
		}
		// ----------------------- St yanmm 2017/7/25 this.grabFocus("?") ;鼠标跟踪
		if (this.getTextFormat("HOMEPLACE_CODE").getText().equals("")) {
			this.messageBox_("请选择出生地");
			this.grabFocus("HOMEPLACE_CODE");
			return;
		}
		if (this.getTextFormat("BIRTHPLACE").getText().equals("")) {
			this.messageBox_("请选择籍贯");
			this.grabFocus("BIRTHPLACE");
			return;
		}
		if ("".equals(this.getValue("SPECIES_CODE"))) {
			this.messageBox_("请选择民族");
			this.grabFocus("SPECIES_CODE");
			return;
		}
		if ("".equals(this.getValue("MARRIAGE_CODE"))) {
			this.messageBox_("请选择婚姻状态");
			this.grabFocus("MARRIAGE_CODE");
			return;
		}
		if (this.getTextFormat("NATION_CODE").getText().equals("")) {
			this.messageBox_("请选择国籍");
			this.grabFocus("NATION_CODE");
			return;
		}
		if ("".equals(this.getValue("OCC_CODE"))) {
			this.messageBox_("请选择职业");
			this.grabFocus("OCC_CODE");
			return;
		}
		if ("".equals(this.getValue("COMPANY_DESC"))) {
			this.messageBox_("请输入工作单位");
			this.grabFocus("COMPANY_DESC");
			return;
		}
		if ("".equals(this.getValue("TEL_COMPANY"))) {
			this.messageBox_("请输入公司电话");
			this.grabFocus("TEL_COMPANY");
			return;
		}
		if ("".equals(this.getValue("ADDRESS"))) {
			this.messageBox_("请输入现住地址");
			this.grabFocus("ADDRESS");
			return;
		}
		if (this.getTextFormat("POST_CODE").getText().equals("")) {
			this.messageBox_("请选择现住邮编");
			this.grabFocus("POST_CODE");
			return;
		}
		if ("".equals(this.getValue("RESID_ADDRESS"))) {
			this.messageBox_("请输入户口地址");
			this.grabFocus("RESID_ADDRESS");
			return;
		}
		if (this.getTextFormat("RESID_POST_CODE").getText().equals("")) {
			this.messageBox_("请选择户口邮编");
			this.grabFocus("RESID_POST_CODE");
			return;
		}
		if ("".equals(this.getValue("ADDRESS_COMPANY"))) {
			this.messageBox_("请输入单位住址");
			this.grabFocus("ADDRESS_COMPANY");
			return;
		}
		if (this.getTextFormat("POST_COMPANY").getText().equals("")) {
			this.messageBox_("请选择单位邮编");
			this.grabFocus("POST_COMPANY");
			return;
		}
		if ("".equals(this.getValue("CONTACTS_NAME"))) {
			this.messageBox_("请输入联系人姓名");
			this.grabFocus("CONTACTS_NAME");
			return;
		}
		if ("".equals(this.getValue("RELATION_CODE"))) {
			this.messageBox_("请选择关系");
			this.grabFocus("RELATION_CODE");
			return;
		}
		if ("".equals(this.getValue("CONTACTS_TEL"))) {
			this.messageBox_("请输入联系人电话");
			this.grabFocus("CONTACTS_TEL");
			return;
		}
		if ("".equals(this.getValue("CONTACTS_ADDRESS"))) {
			this.messageBox_("请输入联系人地址");
			this.grabFocus("CONTACTS_ADDRESS");
			return;
		}
		// -------------en yanmm
		if (pat == null || checkbox.isSelected()) {
			if (!newPatInfo()) // 新增病患
				return;
		} else {
			if (!modifyPatInfo()) // 修改病患信息
				return;
		}
		this.messageBox("P0005");
		// ===物联网 start
		if (Operator.getSpcFlg().equals("Y")) {
			// SYSPatinfoClientTool sysPatinfoClientTool = new SYSPatinfoClientTool(
			// this.getValue("MR_NO").toString());
			// SysPatinfo syspat = sysPatinfoClientTool.getSysPatinfo();
			// SpcPatInfoService_SpcPatInfoServiceImplPort_Client
			// serviceSpcPatInfoServiceImplPortClient = new
			// SpcPatInfoService_SpcPatInfoServiceImplPort_Client();
			// String msg = serviceSpcPatInfoServiceImplPortClient
			// .onSaveSpcPatInfo(syspat);
			// if (!msg.equals("OK")) {
			// System.out.println(msg);
			// }
			TParm spcParm = new TParm();
			spcParm.setData("MR_NO", this.getValue("MR_NO").toString());
			TParm spcReturn = TIOM_AppServer.executeAction("action.sys.SYSSPCPatAction", "getPatName", spcParm);
		}
		// ===物联网 end
		// callFunction("UI|picture|setEnabled", true); // 拍照
		callFunction("UI|patinfo|setEnabled", true); // 病患资料
		// callFunction("UI|child|setEnabled", true); //新生儿注记
	}

	/**
	 * 新增病患方法
	 * 
	 * @return boolean
	 */
	public boolean newPatInfo() {
		if (!checkPatInfo())
			return false;
		pat = new Pat();
		pat = this.readModifyPat(pat);
		if (!pat.onNew()) {
			this.messageBox("E0005"); // 失败
			return false;
		} else {
			TParm r = new TParm();
			r.setData("MR_NO", pat.getMrNo());
			r.setData("CUSTOMER_SOURCE", this.getValue("CUSTOMER_SOURCE"));
			TParm re = MEMPatRegisterTool.getInstance().insertMemPat(r);
			if (re.getErrCode() < 0) {
				this.messageBox("E0005"); // 失败
				return false;
			}

			setValue("MR_NO", pat.getMrNo());
			callFunction("UI|MR_NO|setEnabled", false); // 病案号
			callFunction("UI|IPD_NO|setEnabled", false); // 住院号
			callFunction("UI|patinfo|setEnabled", true); // 病患信息
			// callFunction("UI|picture|setEnabled", true); // 拍照
			// callFunction("UI|child|setEnabled", true); //新生儿注记
			this.callFunction("UI|NEW_PAT|setText", "修改病患保存");
			if (this.modifyFlg) {
				callFunction("UI|NEW_PAT|setEnabled", true); // 病患保存botton
			} else {
				callFunction("UI|NEW_PAT|setEnabled", false); // 病患保存botton
			}
			callFunction("UI|PHOTO_BOTTON|setEnabled", true);
			MR_NO = pat.getMrNo();
			// this.clearValue("NEW_PAT_INFO");//清空 “新建病患”checkbox

			// add by huangtt 20140401 CRM----start
			if (crmFlg) {
				TParm parm = new TParm();
				parm.setData("MR_NO", pat.getMrNo());
				parm.setData("PAT_NAME", pat.getName());
				parm.setData("LAST_NAME", "");
				parm.setData("FIRST_NAME", "");
				parm.setData("OLDNAME", "");
				parm.setData("PY1", "");
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
				parm.setData("CTZ1_CODE", pat.getCtz1Code());// modify by huangjw 20140804
				parm.setData("CTZ2_CODE", pat.getCtz2Code());// modify by huangjw 20140804
				parm.setData("CTZ3_CODE", pat.getCtz2Code());// modify by huangjw 20140804
				parm.setData("SPECIAL_DIET", pat.getSpecialDiet().getValue());
				parm.setData("E_MAIL", "");
				parm.setData("TEL_HOME", pat.getTelHome());
				parm.setData("HOMEPLACE_CODE", pat.gethomePlaceCode());
				parm.setData("RELIGION", "");
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
				parm.setData("INTRODUCER1", "");
				parm.setData("INTRODUCER2", "");
				parm.setData("INTRODUCER3", "");
				parm.setData("DESCRIPTION", "");
				TParm parmCRM = TIOM_AppServer.executeAction("action.reg.REGCRMAction", "createMember1", parm);
				if (!parmCRM.getBoolean("flg", 0)) {
					this.messageBox("CRM信息新增同步失败！");
					return false;
				}
			}

			// add by huangtt 20140401 CRM----end
			return true;
		}
	}

	/**
	 * 修改病患信息方法
	 * 
	 * @return boolean
	 */
	public boolean modifyPatInfo() {
		if (pat.getMrNo() == null || "".equals(pat.getMrNo())) {
			return false;
		}
		pat = this.readModifyPat(pat);
		if (!pat.onSave()) {
			this.messageBox("E0005"); // 失败
			return false;
		} else {
			TParm mrNoParm = new TParm();
			mrNoParm.addData("MR_NO", this.getValueString("MR_NO"));
			TParm memParm = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction", "onQueryMem", mrNoParm);
			TParm rParm = new TParm();
			rParm.setData("MR_NO", this.getValueString("MR_NO"));
			rParm.setData("CUSTOMER_SOURCE", this.getValueString("CUSTOMER_SOURCE"));
			TParm re = new TParm();
			if (memParm.getCount() > 0) {
				re = MEMPatRegisterTool.getInstance().updateMemPat(rParm);
			} else {
				re = MEMPatRegisterTool.getInstance().insertMemPat(rParm);
			}
			if (re.getErrCode() < 0) {
				this.messageBox("E0005"); // 失败
				return false;
			}

			setValue("MR_NO", pat.getMrNo());
			callFunction("UI|new|setEnabled", false);
			callFunction("UI|save|setEnabled", true);
			this.setValue("NEW_PAT_INFO", "N");
			// add by huangtt 20140401 CRM----start
			if (crmFlg) {
				TParm parm = new TParm();
				parm.setData("MR_NO", pat.getMrNo());
				parm.setData("PAT_NAME", pat.getName());
				parm.setData("PY1", pat.getPy1());
				parm.setData("FIRST_NAME", pat.getFirstName());
				parm.setData("LAST_NAME", pat.getLastName());
				parm.setData("OLDNAME", pat.getOldName());
				parm.setData("ID_TYPE", pat.getIdType());
				parm.setData("IDNO", pat.getIdNo());
				parm.setData("SEX_CODE", pat.getSexCode());
				parm.setData("BIRTH_DATE", pat.getBirthday());
				parm.setData("NATION_CODE", pat.getNationCode());
				parm.setData("NATION_CODE2", pat.getSpeciesCode());
				parm.setData("MARRIAGE", pat.getMarriageCode());
				parm.setData("RELIGION", pat.getReligionCode());
				parm.setData("RESID_POST_CODE", pat.getResidPostCode());
				parm.setData("RESID_ADDRESS", pat.getResidAddress());
				parm.setData("POST_CODE", pat.getPostCode());
				parm.setData("CURRENT_ADDRESS", pat.getCurrentAddress());
				parm.setData("CELL_PHONE", pat.getCellPhone());
				parm.setData("SPECIAL_DIET", pat.getSpecialDiet().getValue());
				parm.setData("TEL_HOME", pat.getTelHome());
				parm.setData("HOMEPLACE_CODE", pat.gethomePlaceCode());
				parm.setData("CTZ1_CODE", pat.getCtz1Code());
				parm.setData("CTZ2_CODE", pat.getCtz2Code());
				parm.setData("CTZ3_CODE", pat.getCtz3Code());
				parm.setData("E_MAIL", pat.getEmail());
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
				parm.setData("START_DATE",
						"".equals(mem.getValue("START_DATE", 0)) ? "" : mem.getValue("START_DATE", 0).substring(0, 10));
				parm.setData("END_DATE",
						"".equals(mem.getValue("END_DATE", 0)) ? "" : mem.getValue("END_DATE", 0).substring(0, 10));
				String sDate = mem.getValue("START_DATE", 0);
				String eDate = mem.getValue("END_DATE", 0);
				Timestamp date = SystemTool.getInstance().getDate();
				if (sDate.length() > 0 && eDate.length() > 0) {
					// 计算购买月龄
					int buyMonthAge = getBuyMonth(sDate.substring(0, 10).replaceAll("-", ""),
							eDate.substring(0, 10).replaceAll("-", ""));

					// 发生月龄
					int currMonthAge = getBuyMonth(sDate.substring(0, 10).replaceAll("-", ""),
							date.toString().substring(0, 10).replaceAll("-", ""));

					parm.setData("BUY_MONTH_AGE", String.valueOf(buyMonthAge));
					parm.setData("HAPPEN_MONTH_AGE", String.valueOf(currMonthAge));
				} else {
					parm.setData("BUY_MONTH_AGE", "");
					parm.setData("HAPPEN_MONTH_AGE", "");
				}
				parm.setData("MEM_CODE", memTrade.getValue("MEM_CODE", 0));
				parm.setData("REASON", memTrade.getValue("REASON", 0));
				parm.setData("START_DATE_TRADE", "".equals(memTrade.getValue("START_DATE", 0)) ? ""
						: memTrade.getValue("START_DATE", 0).substring(0, 10));
				parm.setData("END_DATE_TRADE", "".equals(memTrade.getValue("END_DATE", 0)) ? ""
						: memTrade.getValue("END_DATE", 0).substring(0, 10));
				parm.setData("MEM_FEE", memTrade.getValue("MEM_FEE", 0));
				parm.setData("INTRODUCER1", memTrade.getValue("INTRODUCER1", 0));
				parm.setData("INTRODUCER2", memTrade.getValue("INTRODUCER2", 0));
				parm.setData("INTRODUCER3", memTrade.getValue("INTRODUCER3", 0));
				parm.setData("DESCRIPTION", memTrade.getValue("DESCRIPTION", 0));

				// System.out.println("CRM信息更新同步==="+parm);
				TParm parmCRM = TIOM_AppServer.executeAction("action.reg.REGCRMAction", "updateMemberByMrNo1", parm);
				if (!parmCRM.getBoolean("flg", 0)) {
					this.messageBox("CRM信息更新同步失败！");
					return false;
				}
			}

			// add by huangtt 20140401 CRM----end
			return true;
		}
	}

	/**
	 * 保险专员录入信息按钮事件 add by huangjw 2014/07/21
	 */
	public void onInsureInfo() {
		TParm parm = new TParm();
		parm.setData("MR_NO", this.getValueString("MR_NO"));
		parm.setData("EDIT", "N");
		TParm reParm = (TParm) this.openDialog("%ROOT%\\config\\mem\\MEMInsureInfo.x", parm);
	}

	/**
	 * 
	 * 获得TTextFormat事件 add by huangjw 2014/07/21
	 */
	public TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
	}

	/**
	 * 如果证件类型为"其他"，证件号是可以为空的 add by huangjw 2014/07/21
	 */
	public void idTypeSelected() {
		if (this.getTextFormat("ID_TYPE").getText().equals("其他")) {
			callFunction("UI|MARK|Visible", false);
		} else
			callFunction("UI|MARK|Visible", true);
	}

	/**
	 * 医疗卡操作 huangtt 20140318
	 */
	public void onEKTcard() {
		// 读取医疗卡
		parmEKT = EKTIO.getInstance().TXreadEKT();
		if (null == parmEKT || parmEKT.getErrCode() < 0 || parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox(parmEKT.getErrText());
			parmEKT = null;
			return;
		}
		this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
		TextFormatADMPackageType packaGge = (TextFormatADMPackageType) getComponent("LUMPWORK_CODE");
		packaGge.setMrNo(parmEKT.getValue("MR_NO"));
		packaGge.onQuery();
		onMrno();
	}

	/**
	 * 住院号回车事件
	 */
	public void onIpdNo() {
		TParm parm = new TParm();
		parm.setData("IPD_NO", PatTool.getInstance().checkIpdno(this.getValueString("IPD_NO")));
		TParm re = ADMInpTool.getInstance().selectall(parm);
		this.setValue("MR_NO", re.getValue("MR_NO", 0));
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					onMrno();
				} catch (Exception e) {
				}
			}
		});
	}

	/**
	 * 病案号回车查询事件
	 */
	public void onMrno() {
		// ============== chenxi ========== 由于住院处不关界面，导致急诊晚班跨天病人入院时间错误
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
		String date = df.format(SystemTool.getInstance().getDate());
		setValue("IN_DATE", StringTool.getTimestamp(date, "yyyyMMddHH")); // 预定日期
		// ============== chenxi =========
		pat = new Pat();
		String mrno = getValue("MR_NO").toString().trim();
		if (!this.queryPat(mrno))
			return;
		pat = pat.onQueryByMrNo(mrno);
		// System.out.println("------------------>pat"+pat);//==liling 20140513 屏蔽
		if (pat == null || "".equals(getValueString("MR_NO"))) {
			this.messageBox_("查无病患! ");
			this.onClear(); // 清空
			this.setUi(); // 病患信息可编辑
			this.setUIAdmF(); // 住院登记信息不可编辑
			this.setMenu(false);
			return;
		} else {
			callFunction("UI|MR_NO|setEnabled", false); // 病案号
			callFunction("UI|IPD_NO|setEnabled", false); // 住院号
			callFunction("UI|patinfo|setEnabled", true); // 病患信息
			// callFunction("UI|picture|setEnabled", true); // 拍照
			// callFunction("UI|child|setEnabled", true); //新生儿注记

			this.callFunction("UI|NEW_PAT|setText", "修改病患保存");
			if (this.modifyFlg) {
				callFunction("UI|NEW_PAT|setEnabled", true); // 病患保存botton
			} else {
				callFunction("UI|NEW_PAT|setEnabled", false); // 病患保存botton
			}
			callFunction("UI|PHOTO_BOTTON|setEnabled", true);
			MR_NO = pat.getMrNo();
			this.setMenu(true);
		}
		if (ADMInpTool.getInstance().containsAny(MR_NO, "-")) {// 新生儿病案号判断--xiongwg20150715
			// this.messageBox("新生儿不能修改套餐类型");
			callFunction("UI|LUMPWORK_CODE|setEnabled", false);
			this.setValue("PACKAGE_FLG", "N");
		} else {
			callFunction("UI|LUMPWORK_CODE|setEnabled", true);
		}
		this.setPatForUI(pat);
		idTypeSelected();// 校验证件类型 add by huangjw 20140721

		TParm memParm = new TParm();
		memParm.setData("MR_NO", getValue("MR_NO"));
		// System.out.println("-------------->2");//==liling 屏蔽
		mem = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemInfoAll(getValueString("MR_NO"))));
		// System.out.println("mem----------->"+mem);//==liling 屏蔽
		memTrade = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemTradeCrm(getValueString("MR_NO"))));
		// System.out.println("memTrade----------->"+memTrade);//==liling 屏蔽
		// System.out.println("-------------->3");//==liling 屏蔽
		if (checkAdmInp(pat.getMrNo())) {
			this.messageBox_("此病患住院中！");
			this.setUIAdmF();
			this.inInpdata();
			// 调31天再次住院提醒方法
			this.AgnMessage();
			saveType = "UPDATE";
			callFunction("UI|save|setEnabled", true); // 保存
			callFunction("UI|MR_NO|setEnabled", false); // 病案号
			callFunction("UI|IPD_NO|setEnabled", false); // 住院号
			callFunction("UI|bed|setEnabled", true); // 包床
			callFunction("UI|greenpath|setEnabled", true); // 绿色通道

			// 此病患住院中的预交金带入add by sunqy 20140527----start----
			String sql = "SELECT BILPAY FROM ADM_RESV WHERE MR_NO='" + this.getValueString("MR_NO") + "'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			this.setValue("TOTAL_BILPAY", parm.getValue("BILPAY", 0));// add by sunqy 20140527----start----
			// callFunction("UI|child|setEnabled", true); //新生儿注记
			this.setMenu(true);
			// sql="SELECT A.LUMPWORK_CODE,A.CTZ2_CODE,B.AR_AMT FROM ADM_INP A
			// ,MEM_PACKAGE_TRADE_M B,MEM_PAT_PACKAGE_SECTION C " +
			// "WHERE A.CASE_NO=B.CASE_NO AND B.TRADE_NO=C.TRADE_NO AND A.CASE_NO=C.CASE_NO
			// AND A.LUMPWORK_CODE=C.PACKAGE_CODE " +
			// "AND A.MR_NO='"+this.getValueString("MR_NO")+"' AND A.DS_DATE IS NULL AND
			// A.CANCEL_FLG='N' GROUP BY A.LUMPWORK_CODE,A.CTZ2_CODE,B.AR_AMT";
			parm = getAdmInpPackParm(this.getValueString("MR_NO"));
			if (parm.getCount() > 0) {
				this.setValue("LUMPWORK_CODE", parm.getValue("PACKAGE_CODE", 0));// 套餐类型
				this.setValue("LUMPWORK_CTZ_CODE", parm.getValue("CTZ_CODE", 0));// 就诊身份2
				this.setValue("AR_AMT_T", parm.getValue("AR_AMT", 0));// 套餐销售金额
			}

			// add by yangjj 20160428
			this.callFunction("UI|IN_DATE|setEnabled", false);

			this.callFunction("UI|INSURE_INFO_1|setEnabled", true);
			return;
		} else {
			// System.out.println("-------------->4");//==liling 屏蔽
			// 查询预约住院信息
			TParm parm = ADMResvTool.getInstance().selectNotIn(pat.getMrNo());
			// System.out.println("-------------->parm"+parm);//==liling 屏蔽
			if (parm.getCount() <= 0) {
				this.messageBox_("此病患没有预约信息!");
				callFunction("UI|save|setEnabled", false); // 保存
				return;
			}
			// =========pangben 2014-7-30 多次预约住院弹出见面传回操作，包干套餐版本修改
			if (parm.getCount() > 1) {
				reParm.setData("MR_NO", pat.getMrNo());
				reParm.setData("PAT_NAME", pat.getName());
				reParm.setData("SEX_CODE", pat.getSexCode());
				reParm = (TParm) this.openDialog("%ROOT%\\config\\adm\\ADMInQuery.x", reParm);
				if (reParm == null) {
					this.setMenu(false);
					return;
				}
			} else if (parm.getCount() == 1) {
				reParm = parm.getRow(0);
			}
			TParm checkLParm = getCheckPatPackage(pat.getMrNo(), "", "", "0");// 校验身份是否启用
			TParm parmL = getPatPackageData(pat.getMrNo(), "", "", "0");// 获取套餐信息--xiongwg20150702
			// }else{
			// callFunction("UI|LUMPWORK_CODE|setEnabled", true); // 包干套餐
			// }
			// 判断预约信息是否是新生儿
			if (reParm.getBoolean("NEW_BORN_FLG")) {
				this.setValue("NEW_BORN_FLG", reParm.getBoolean("NEW_BORN_FLG"));
				McaseNo = reParm.getValue("M_CASE_NO"); // 查询预约信息中是否有母亲的病案号
				// 查询母亲的住院信息 获取住院号(婴儿的IPD_NO与母亲相同)
				TParm admParm = new TParm();
				admParm.setData("CASE_NO", McaseNo);
				TParm admInfo = ADMTool.getInstance().getADM_INFO(admParm);
				// System.out.println("---------------->admInfo"+admInfo);//==liling 屏蔽
				if (admInfo.getCount() <= 0) {
					this.messageBox_("没有查询到母亲的住院信息");
					callFunction("UI|save|setEnabled", false); // 保存
					return;
				}
				Pat M_PAT = Pat.onQueryByMrNo(admInfo.getValue("MR_NO", 0));
				this.setValue("IPD_NO", admInfo.getValue("IPD_NO", 0));
				this.setValue("M_MR_NO", admInfo.getValue("MR_NO", 0));
				this.setValue("M_NAME", M_PAT.getName());
				this.callFunction("UI|LM1|setVisible", true);
				this.callFunction("UI|LM2|setVisible", true);
				this.callFunction("UI|M_MR_NO|setVisible", true);
				this.callFunction("UI|M_NAME|setVisible", true);
			} else {
				this.setValue("IPD_NO", pat.getIpdNo());
				this.callFunction("UI|LM1|setVisible", false);
				this.callFunction("UI|LM2|setVisible", false);
				this.callFunction("UI|M_MR_NO|setVisible", false);
				this.callFunction("UI|M_NAME|setVisible", false);
			}
			TParm param = new TParm();// add by wanglong 20121025
			param.setData("MR_NO", reParm.getValue("MR_NO"));// add by wanglong 20121025
			TParm result1 = CLPSingleDiseTool.getInstance().queryADMResvSDInfo(param);// add by wanglong 20121025
			this.setValueForParm("DISE_CODE", result1, 0);// add by wanglong 20121025

			callFunction("UI|save|setEnabled", true); // 保存
			this.setValue("ADM_SOURCE", reParm.getData("ADM_SOURCE"));
			this.setValue("TOTAL_BILPAY", reParm.getData("BILPAY"));
			// this.setValue("SERVICE_LEVEL", parm.getData("SERVICE_LEVEL", 0));
			this.setValue("PAT_CTZ_CODE", reParm.getData("CTZ1_CODE"));// 将CTZ1_CODE改为PAT_CTZ_CODE by huangjw 20140804
			this.setValue("DEPT_CODE", reParm.getData("DEPT_CODE"));
			this.setValue("STATION_CODE", reParm.getData("STATION_CODE"));
			this.setValue("OPD_DEPT_CODE", reParm.getData("OPD_DEPT_CODE"));
			this.setValue("OPD_DR_CODE", reParm.getData("OPD_DR_CODE"));
			this.setValue("VS_DR_CODE", reParm.getData("DR_CODE"));
			this.setValue("PATIENT_CONDITION", reParm.getData("PATIENT_CONDITION"));
			this.setValue("YELLOW_SIGN", reParm.getData("YELLOW_SIGN"));
			this.setValue("RED_SIGN", reParm.getData("RED_SIGN"));
			this.setValue("BED_NO", getBedDesc(reParm.getValue("BED_NO")));
			this.setUIT();
			// pangben 2016-4-26 服务等级默认修改，根据第一身份获得服务等级
			// this.setValue("SERVICE_LEVEL", "1"); // 服务等级默认是"自费"
			this.setValue("IN_COUNT", getInCount(MR_NO) + 1); // 获取病人住院的次数
			setValueForParm("SPECIAL_DIET", pat.getParm());// 特殊饮食
			onLumpworkCtzCode();// 校验包干套餐是否绑定
			// this.setValue("LUMPWORK_CODE",reParm.getData("LUMPWORK_CODE"));//==liling
			// 20140512 add LUMPWORK_CODE
			if (parmL.getCount() > 0) {
				// this.setValue("LUMPWORK_CODE", parmL.getValue("PACKAGE_CODE",0));//套餐类型
				// this.setValue("LUMPWORK_CTZ_CODE", parmL.getValue("CTZ_CODE",0));//就诊身份2
				// this.setValue("AR_AMT_T", parmL.getValue("AR_AMT",0));//套餐销售金额
			} else {// =======pangben 2015-9-10
				if (checkLParm.getCount() > 0) {// 存在购买套餐，但是身份已过期
					String packDesc = "";
					for (int i = 0; i < checkLParm.getCount(); i++) {
						packDesc += "'" + checkLParm.getValue("PACKAGE_DESC", i) + "'、";
					}
					if (packDesc.length() > 0) {
						packDesc = packDesc.substring(0, packDesc.lastIndexOf("、"));
					}
					this.messageBox(packDesc + "套餐身份已过期，请修改会员身份字典");
				}
			}
			this.setValue("TOTAL_BILPAY", reParm.getData("BILPAY"));// add by sunqy 20140523
			saveType = "NEW";

			// 调31天再次住院提醒方法
			this.AgnMessage();
			// callFunction("UI|child|setEnabled", true); //新生儿注记
		}
		this.callFunction("UI|INSURE_INFO_1|setEnabled", true);
		boolean isDebug = true;// add by huangjw 20150506 异常处理
		try {
			checkCtz();// 校验就诊身份1是否在有效期内add by huangjw 20150924
		} catch (Exception e) {
			if (isDebug) {
				System.out.println("come in class: ADMInpControl ，method ：checkCtz");
				e.printStackTrace();
			}
		}

	}

	/**
	 * 查询病患信息
	 * 
	 * @param mrNo
	 *            String
	 * @return boolean
	 */
	public boolean queryPat(String mrNo) {
		this.setMenu(false); // MENU 显示控制
		pat = new Pat();
		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.setMenu(false); // MENU 显示控制
			this.messageBox("E0081");
			return false;
		}
		String allMrNo = PatTool.getInstance().checkMrno(mrNo);
		if (mrNo != null && !allMrNo.equals(pat.getMrNo())) {
			// ============xueyf modify 20120307 start
			messageBox("病案号" + allMrNo + " 已合并至" + pat.getMrNo());
			// ============xueyf modify 20120307 stop
		}

		return true;
	}

	/**
	 * 病患信息赋值
	 *
	 * @param patInfo
	 *            Pat
	 */
	public void setPatForUI(Pat patInfo) {
		// System.out.println("------------------------->1");//==liling 20140513 屏蔽
		// 病案号,姓名,性别,生日,职业，民族，国籍，身份证号，婚姻,出生地
		this.setValueForParm(
				"MR_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;OCC_CODE;SPECIES_CODE;NATION_CODE;IDNO;MARRIAGE_CODE;HOMEPLACE_CODE;TEL_HOME;ID_TYPE;CELL_PHONE", // 20140729
																																						// huangjw
																																						// add
																																						// 手机信息
																																						// CELL_PHONE
				patInfo.getParm());
		// 工作单位,单位电话,单位邮编,户口地址,户口邮编，联系人姓名，关系，联系人电话，联系人地址
		this.setValueForParm(
				"COMPANY_DESC;TEL_COMPANY;POST_CODE;ADDRESS;ADDRESS_COMPANY;POST_COMPANY;BIRTHPLACE;RESID_ROAD;RESID_POST_CODE;CONTACTS_NAME;RELATION_CODE;CONTACTS_TEL;CONTACTS_ADDRESS;SERVICE_LEVEL;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE",
				patInfo.getParm());
		// 身份一，身份二，身份三 add by huangjw 20140804
		this.setValueForParm("CTZ1_CODE;CTZ2_CODE;CTZ3_CODE", patInfo.getParm());
		// this.messageBox("现地址"+patInfo.getCurrentAddress());
		// 重赋现地址
		this.setValue("ADDRESS", patInfo.getCurrentAddress());

		// 客户来源 add by huangtt 20180102
		setValue("CUSTOMER_SOURCE", MEMPatRegisterTool.getInstance().getMemCustomerSource(patInfo.getMrNo()));

		//
		this.setValue("PAT_CTZ", patInfo.getCtz1Code());
		this.setText("TEL_O1", patInfo.getTelCompany());
		this.setValue("RESID_ADDRESS", patInfo.getResidAddress());
		this.setValue("FOREIGNER_FLG", patInfo.isForeignerFlg()); // 外国人注记
		setValueForParm("SPECIAL_DIET", patInfo.getParm());// 特殊饮食
		// setValue("AGE",setBirth(pat.getBirthday())); // 计算年龄
		setValue("AGE", DateUtil.showAge(pat.getBirthday(), sysDate)); // 计算年龄
		// onPOST_CODE();
		// onRESID_POST_CODE();
		// this.onCompanyPost();
		this.viewPhoto(pat.getMrNo());
		// System.out.println("-------------------------1>");//==liling 20140513 屏蔽

	}

	/**
	 * 身份证号得到出生地
	 */
	public void onIdNo() {
		String homeCode = "";
		String idNo = this.getValueString("IDNO");
		homeCode = StringUtil.getIdNoToHomeCode(idNo);
		setValue("HOMEPLACE_CODE", homeCode);
	}

	// /**
	// * 户口邮编得到省市
	// */
	// public void onPOST_CODE() {
	// if (getValueString("POST_CODE") == null
	// || "".equals(getValueString("POST_CODE")))
	// return;
	//
	// String post = getValueString("POST_CODE");
	// TParm parm = this.getPOST_CODE(post);
	//
	// if (parm.getData("POST_CODE", 0) == null
	// || "".equals(parm.getData("POST_CODE", 0)))
	// return;
	// setValue("POST_P",
	// parm.getData("POST_CODE", 0).toString().substring(0, 2));
	// setValue("POST_C", parm.getData("POST_CODE", 0).toString());
	// }
	//
	// /**
	// * 户口邮编得到省市
	// */
	// public void onRESID_POST_CODE() {
	// if (getValueString("RESID_POST_CODE") == null
	// || "".equals(getValueString("POST_CODE")))
	// return;
	// String post = getValueString("RESID_POST_CODE");
	// TParm parm = this.getPOST_CODE(post);
	// if (parm.getData("POST_CODE", 0) == null
	// || "".equals(parm.getData("POST_CODE", 0)))
	// return;
	// setValue("RESID_POST_P", parm.getData("POST_CODE", 0).toString()
	// .substring(0, 2));
	// setValue("RESID_POST_C", parm.getData("POST_CODE", 0).toString());
	// }
	//
	// /**
	// * 得到省市代码
	// *
	// * @param post
	// * String
	// * @return TParm
	// */
	// public TParm getPOST_CODE(String post) {
	// TParm result = SYSPostTool.getInstance().getProvinceCity(post);
	// return result;
	// }
	//
	// /**
	// * 通过城市带出邮政编码
	// */
	// public void selectCode_1() {
	// String post = this.getValue("POST_C").toString();
	// if (post.length() == 0 || "".equals(post))
	// return;
	//
	// this.setValue("POST_CODE", this.getValue("POST_C"));
	// this.onPOST_CODE();
	// }
	//
	// /**
	// * 通过城市带出邮政编码
	// */
	// public void selectCode_2() {
	// if (this.getValue("RESID_POST_C") == null
	// || "".equals(getValueString("POST_CODE")))
	// return;
	// this.setValue("RESID_POST_CODE", this.getValue("RESID_POST_C"));
	// this.onRESID_POST_CODE();
	// }
	//
	// /**
	// * 通过城市带出邮政编码3
	// */
	// public void selectCode_3() {
	// this.setValue("POST_COMPANY", this.getValue("COMPANY_POST_C"));
	// this.onCompanyPost();
	// }
	//
	// /**
	// * 单位邮编的得到省市
	// */
	// public void onCompanyPost() {
	// String post = getValueString("POST_COMPANY");
	// if (post == null || "".equals(post)) {
	// return;
	// }
	// TParm parm = this.getPOST_CODE(post);
	// setValue("COMPANY_POST_P", parm.getData("POST_CODE", 0) == null ? ""
	// : parm.getValue("POST_CODE", 0).substring(0, 2));
	// setValue("COMPANY_POST_C", parm.getValue("POST_CODE", 0).toString());
	// }
	/**
	 * 同通信地址
	 */
	public void onSameto1() {
		setValue("RESID_POST_CODE", getValue("POST_CODE"));
		// this.onRESIDPOST();
		// callFunction("UI|SESSION_CODE|onQuery");
		setValue("RESID_ADDRESS", getValue("ADDRESS"));

	}

	/**
	 * 同通信地址
	 */
	public void onSameto3() {
		setValue("POST_COMPANY", getValue("POST_CODE"));
		// this.onRESIDPOST();
		// callFunction("UI|SESSION_CODE|onQuery");
		setValue("ADDRESS_COMPANY", getValue("ADDRESS"));

	}

	/**
	 * 同通信地址
	 */
	public void onSameto2() {
		setValue("CONTACTS_ADDRESS", getValue("ADDRESS"));
	}

	/**
	 * 住院登记保存
	 */
	public void onSave() {
		// if(!checkPatInfo()){
		// return;
		// }
		if ("NEW".equals(saveType)) {
			this.admInpInsert(); // 新增
		} else if ("UPDATE".equals(saveType)) {
			this.admInpUpdata(); // 修改
		} else {
			this.messageBox_("没有保存类型");
		}
	}

	/**
	 * 31天内再次住院提醒方法
	 */
	public void AgnMessage() {
		Timestamp date = SystemTool.getInstance().getDate();
		String mrNo = this.getValueString("MR_NO");
		if (mrNo == null || mrNo.length() <= 0) {
			return;
		}
		TParm inparm = new TParm();
		inparm.setData("MR_NO", mrNo);
		inparm.setData("CANCEL_FLG", "N");
		inparm.setData("REGION_CODE", Operator.getRegion());
		TParm parm = ADMInpTool.getInstance().selectall(inparm);
		if (parm.getCount() <= 0)
			return;
		// 住院次数
		int count = parm.getCount();
		if (count > 0) {
			parm = ADMInpTool.getInstance().queryLastDsdate(inparm);
			// luhai MODIFY 2012-2-21 modify 处理在病患之前没有住院的情
			// 况下，查询出的上次住院日期为空，但查询出的数据条数显示为1的情况 begin
			// if (parm.getCount() <= 0)
			// return;
			if (parm.getCount("DS_DATE") <= 0)
				return;
			if (parm.getTimestamp("DS_DATE", 0) == null) {
				return;
			}
			// luhai MODIFY 2012-2-21 modify 处理在病患之前没有住院的情
			// 况下，查询出的上次住院日期为空，但查询出的数据条数显示为1的情况 end
			Timestamp lastdate = parm.getTimestamp("DS_DATE", 0);
			int time = StringTool.getDateDiffer(date, lastdate);
			if (time <= 31) {
				// 新增提醒
				if ("NEW".equals(saveType)) {
					this.messageBox("此病人出院31天内再次住院！");
				}
				callFunction("UI|AGN_CODE|setEnabled", true); // 31天内再次住院等级
				callFunction("UI|AGN_INTENTION|setEnabled", true); // 31天内再次住院等级
			} else {
				callFunction("UI|AGN_CODE|setEnabled", false); // 31天内再次住院等级
				callFunction("UI|AGN_INTENTION|setEnabled", false); // 31天内再次住院等级
			}
		}
	}

	/**
	 * 新增
	 */
	public void admInpInsert() {
		if (!this.checkData()) // 检查数据
			return;
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
		String date = df.format(SystemTool.getInstance().getDate());
		setValue("IN_DATE", StringTool.getTimestamp(date, "yyyyMMddHH")); // 预定日期
		TParm parm = new TParm();
		parm = this.readData(); // 读取参数
		TParm bed = new TParm();
		bed.setData("BED_NO", parm.getData("BED_NO"));
		TParm checkbed = ADMInpTool.getInstance().QueryBed(bed);
		if (checkbed.getData("ALLO_FLG", 0) != null) {
			if (checkbed.getData("ALLO_FLG", 0).equals("Y")) {
				this.messageBox("此床已占用,请重新选择床位");
				return;
			}
		}
		String mrNo = parm.getValue("MR_NO");
		TParm parmL = null; // =====pangben 2015-7-20 添加套餐校验
		double rate = 0.00;
		if (this.getValue("LUMPWORK_CODE").toString().length() > 0) {
			parmL = getPatPackageData(mrNo, "", this.getValueString("LUMPWORK_CODE"), "0");
			if (parmL.getCount("PACKAGE_CODE") <= 0) {
				this.messageBox("当前选择的套餐已使用或已作废,不可以操作");
				return;
			}
			TParm lumpParm = BILLumpWorkTool.getInstance().getLumpRate("", mrNo,
					this.getValue("LUMPWORK_CODE").toString());
			if (lumpParm.getErrCode() < 0) {
				this.messageBox(lumpParm.getErrText());
				return;
			}
			if (null == lumpParm.getValue("RATE") || lumpParm.getValue("RATE").length() <= 0) {
				this.messageBox("没有获得套餐折扣");
				return;
			}
			rate = lumpParm.getDouble("RATE");
			if (rate <= 0) {
				this.messageBox("此病患套餐折扣存在问题，套餐折扣为:" + rate + ",不可以操作");
				return;
			}
		}
		// System.out.println("adm_1");
		BED_NO = parm.getData("BED_NO").toString();
		// 住院参数表-MR_IPD_FLG=Y 住院号=病案号 duzhw add 20140324
		String admSql = "SELECT MR_IPD_FLG FROM ODI_SYSPARM ";
		TParm admParm = new TParm(TJDODBTool.getInstance().select(admSql));
		if (admParm.getValue("MR_IPD_FLG", 0).equals("Y")) {
			IPD_NO = MR_NO;
		} else {
			// 原来流程
			if ("Y".equals(getValue("NEW_BORN_FLG"))) {
				IPD_NO = getValue("IPD_NO").toString();
			} else {
				IPD_NO = pat.getIpdNo(); // 判断该病患是否住过院
				if ("".equals(IPD_NO))
					IPD_NO = SystemTool.getInstance().getIpdNo();

				if ("".equals(IPD_NO)) {
					this.messageBox_("住院号取参错误！");
					return;
				}
			}
		}
		// =====pangben 2014-7-30 包干套餐逻辑添加，不需要创建就诊号，执行修改ADM_INP表逻辑
		if (null != reParm.getValue("LUMPWORK_CASE_NO") && reParm.getValue("LUMPWORK_CASE_NO").length() > 0) {
			// TParm
			// lumpworkParm=ADMResvTool.getInstance().queryLumpworkCaseNoByResvNo(reParm);
			// if (null==lumpworkParm.getValue("LUMPWORK_CASE_NO",0)
			// ||lumpworkParm.getValue("LUMPWORK_CASE_NO",0).length()<=0) {
			// this.messageBox("没有获得就诊号码");
			// return;
			// }
			caseNo = reParm.getValue("LUMPWORK_CASE_NO");
			parm.setData("LUMPWORK_FLG", "Y");// 存在包干套餐，已经创建ADM_INP数据只能操作修改逻辑，
												// 区分在住院登记界面选择包干套餐添加ADM_INP表
		} else {
			caseNo = SystemTool.getInstance().getNo("ALL", "REG", "CASE_NO", "CASE_NO"); // 调用取号原则
		}

		// 读取界面资料
		parm.setData("CASE_NO", caseNo);
		parm.setData("M_CASE_NO", McaseNo);
		parm.setData("IPD_NO", IPD_NO);
		parm.setData("DATE", SystemTool.getInstance().getDate());
		parm.setData("IN_DEPT_CODE", this.getValue("DEPT_CODE"));
		parm.setData("IN_STATION_CODE", this.getValue("STATION_CODE"));
		parm.setData("VS_DR_CODE", this.getValue("VS_DR_CODE"));
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("ADM_CLERK", Operator.getID()); // 住院登记作业员
		parm.setData("LUMPWORK_CODE", this.getValue("LUMPWORK_CODE"));// ==liling 20140512 包干套餐
		parm.setData("LUMPWORK_RATE", rate);// 套餐折扣
		parm.setData("RESV_NO", reParm.getValue("RESV_NO"));
		// parm.setData("INSURE_INFO", this.getValue("INSURE_INFO_1"));//保险信息 add by
		// huangjw 20150804
		// this.messageBox((String)this.getValue("LUMPWORK_CODE"));
		// ***********modify by lim 2012/02/21 begin
		if (null != parmL) {
			parmL = parmL.getRow(0);
			parmL.setData("MR_NO", mrNo);
			parmL.setData("CASE_NO", caseNo);
			parmL.setData("USED_FLG", "1");
			parm.setData("parmL", parmL.getData());// 更新套餐主档、时程和明细医嘱的parm--xiongwg20150703
		}
		String resvNo = "";
		String fileServerMainRoot = TIOM_FileServer.getPath("FileServer.Main.Root");
		String emrData = TIOM_FileServer.getPath("EmrData");
		TParm resv = ADMResvTool.getInstance().selectAll(reParm);
		// TParm resv = ADMResvTool.getInstance().selectNotIn(mrNo);
		if (resv.getCount() < 0) {
			messageBox("该病患没有预约住院");
			return;
		}

		// resvNo = reParm.getValue("RESV_NO");
		// resvNo = resv.getValue("RESV_NO", 0);
		// 获取预约号
		String resvsql = "SELECT * FROM ADM_RESV WHERE MR_NO = '" + MR_NO + "'" +
		//
				" ORDER BY APP_DATE DESC";
		//
		TParm resvParm = new TParm(TJDODBTool.getInstance().select(resvsql));
		String resvNo_ = resvParm.getValue("RESV_NO", 0);

		String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO ='" + resvNo_ + "'  ORDER BY OPT_DATE DESC ";
		// System.out.println("======sql===###################===="+sql);
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
		if (result1.getCount() <= 0) {

		} else {
			// 移动JHW文件并改名称.
			String oldFileName = result1.getValue("FILE_NAME", 0);
			String oldFilePath = result1.getValue("FILE_PATH", 0);
			String seq = result1.getValue("FILE_SEQ", 0);
			System.out.println(fileServerMainRoot + emrData + oldFilePath + "\\" + oldFileName + ".jhw");

			byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					fileServerMainRoot + emrData + oldFilePath + "\\" + oldFileName + ".jhw");

			Timestamp ts = SystemTool.getInstance().getDate();
			String dateStr = StringTool.getString(ts, "yyyyMMdd");
			// 获得新的文件路径
			StringBuilder filePathSb = new StringBuilder();
			filePathSb.append("JHW\\").append(dateStr.substring(2, 4)).append("\\").append(dateStr.substring(4, 6))
					.append("\\").append(mrNo);
			String newFilePath = filePathSb.toString();

			// 获得新的文件名称
			String[] oldFileNameArray = oldFileName.split("_");

			StringBuilder sb = new StringBuilder(caseNo);
			sb.append("_").append(oldFileNameArray[1]).append("_").append(oldFileNameArray[2]);
			String newFileName = sb.toString();

			try {
				// 移动文件
				TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
						fileServerMainRoot + emrData + newFilePath + "\\" + newFileName + ".jhw", data);
				// this.messageBox("=====resvNo====="+resvNo);
				TParm action = new TParm(this.getDBTool()
						.select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"
								+ caseNo + "'"));
				int index = action.getInt("MAXFILENO", 0);
				// 更新数据库
				String sql1 = "UPDATE EMR_FILE_INDEX SET CASE_NO='" + caseNo + "',FILE_PATH='" + newFilePath
						+ "',FILE_NAME='" + newFileName + "',FILE_SEQ='" + index + "' WHERE CASE_NO='" + resvNo_
						+ "' AND FILE_SEQ='" + seq + "'";

				// System.out.println("======sql11111========"+sql1);
				TParm result2 = new TParm(TJDODBTool.getInstance().update(sql1));
				if (result2.getErrCode() < 0) {
					err(result2.getErrName() + "" + result2.getErrText());
					messageBox("更新失败!");
					return;
				}
				// 删除老文件；
				boolean delFlg = TIOM_FileServer.deleteFile(TIOM_FileServer.getSocket(),
						fileServerMainRoot + emrData + oldFilePath + "\\" + oldFileName + ".jhw");
				if (!delFlg) {
					this.messageBox("删除原文件失败!");
					return;
				}
			} catch (Exception e) {
				this.messageBox("移动文件失败!");
			}
		}
		// ***********modify by lim 2012/02/21 end
		
//		System.out.println("安心价注记:"+parm.getValue("REASSURE_FLG"));
		
		TParm result = TIOM_AppServer.executeAction("action.adm.ADMInpAction", "insertADMData", parm); // 住院登记保存
		// TParm result = TIOM_AppServer.executeAction("action.adm.ADMInpAction",
		// "insertADMData", parm); // 住院登记保存
		// System.out.println("adm_4");
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
			saveType = "UPDATE";
			this.setValue("IPD_NO", IPD_NO);
			pat.modifyIpdNo(IPD_NO);
			pat.modifyRcntIpdDate((Timestamp) getValue("IN_DATE"));
			pat.modifyRcntIpdDept(getValue("DEPT_CODE").toString());
			pat.onSave();
			// modifyPatInfo(); // 更新病患基本信息 huangjw 20150730 delete
			if (!this.getValueString("DISE_CODE").trim().equals("")) {// add by wanglong 20121025
				updateADMInpSDInfo();// 插入单病种信息
			}
			this.addMRO("new"); // 新建 病历 MRO
			// add by wangbin 20140818 住院登记时将数据插入临时表 START
			this.insertMroReg();
			// add by wangbin 20140818 住院登记时将数据插入临时表 END
			this.setUIAdmF();
			this.setMenu(true);
			// HL7消息
			// this.sendHl7message();
			// 通知护士站socket
			sendInwStationMessages(this.getValueString("MR_NO"), caseNo, pat.getName());
			// //生成信息看板XML
			// try{
			// ADMXMLTool.getInstance().creatXMLFile(caseNo);
			// }catch(Exception e){}
			if (!getConfigBoolean("OPENWINDOW.GREEN_PATH_isOpen"))
				return;
			TParm sendParm = new TParm();
			sendParm.setData("CASE_NO", caseNo);
			sendParm.setData("PRE_AMT", this.getValue("TOTAL_BILPAY"));
			String fileName = getConfigString("OPENWINDOW.BIIL_PAY");
			this.openWindow(fileName, sendParm);
			TParm queryData = new TParm();
			queryData.setData("CASE_NO", caseNo);
			TParm bilPay = ADMInpTool.getInstance().queryCaseNo(queryData);
			setValue("TOTAL_BILPAY", bilPay.getData("TOTAL_BILPAY", 0));

		}
	}

	/**
	 * 修改
	 */
	public void admInpUpdata() {
		if (!this.checkData()) // 检查数据
			return;
		if (caseNo == null || "".equals(caseNo))
			return;
		if (!checkBedNo()) {
			return;
		}
		TParm parm = new TParm();
		parm = this.readData(); // 读取参数

		if (!BED_NO.equals(this.getValueString("BED_DESC"))) {
			parm.setData("UPDATE_BED", "Y");
		} else {
			parm.setData("UPDATE_BED", "N");
		}
		parm.setData("CASE_NO", caseNo);
		TParm adminpParm = ADMInpTool.getInstance().queryCaseNo(parm);
		parm.setData("OLD_LUMPWORK_CODE",
				null != adminpParm.getValue("LUMPWORK_CODE", 0) && adminpParm.getValue("LUMPWORK_CODE", 0).length() > 0
						? adminpParm.getValue("LUMPWORK_CODE", 0)
						: "");// 获得数据库里的套餐
		parm.setData("IPD_NO", getValue("IPD_NO"));
		TParm parmOld = getPatPackageData(this.getValueString("MR_NO"), caseNo, "", "1");
		parm.setData("parmOldL", null != parmOld && parmOld.getCount() > 0 ? parmOld.getRow(0).getData() : null);
		parm.setData("adminpParm", adminpParm.getData());
		double rate = 0.00;
		if (this.getValueString("LUMPWORK_CODE").length() > 0) {// 存在套餐类型
			TParm parmL = getPatPackageData(this.getValueString("MR_NO"), "", this.getValueString("LUMPWORK_CODE"),
					"0");
			if (null != parmL && parmL.getCount() > 0) {// 修改套餐类型的情况
				parmL = parmL.getRow(0);
				parmL.setData("MR_NO", this.getValueString("MR_NO"));
				parmL.setData("CASE_NO", caseNo);
				parmL.setData("USED_FLG", "1");
				parm.setData("parmL", parmL.getData());// 更新套餐主档、时程和明细医嘱的parm--xiongwg20150703
			} else {
				this.messageBox("当前选择的套餐已使用或已作废,不可以操作");
				return;
			}
			// 修改套餐校验折扣
			if (null != adminpParm.getValue("LUMPWORK_CODE", 0)
					&& !adminpParm.getValue("LUMPWORK_CODE", 0).equals(this.getValueString("LUMPWORK_CODE"))) {
				TParm lumpParm = BILLumpWorkTool.getInstance().getLumpRate("", this.getValueString("MR_NO"),
						this.getValue("LUMPWORK_CODE").toString());
				if (lumpParm.getErrCode() < 0) {
					this.messageBox(lumpParm.getErrText());
					return;
				}
				if (null == lumpParm.getValue("RATE") || lumpParm.getValue("RATE").length() <= 0) {
					this.messageBox("没有获得套餐折扣");
					return;
				}
				rate = lumpParm.getDouble("RATE");
//				if (rate <= 0) {
//					this.messageBox("此病患套餐折扣存在问题，套餐折扣为:" + rate + ",不可以操作");
//					return;
//				}
			}
		}
		parm.setData("LUMPWORK_RATE", rate);// 套餐折扣
		TParm result = TIOM_AppServer.executeAction("action.adm.ADMInpAction", "upDataAdmInp", parm); // 住院登记保存===liing
		if (result.getErrCode() < 0)
			this.messageBox(result.getErrText());
		else {
			this.messageBox("P0005");
			boolean newbabyFlg = result.getBoolean("newbabyFlg");
			if (newbabyFlg) {
				this.messageBox("母婴套餐类型已修改");
			}
			// modifyPatInfo(); huangjw 20150730 delete
			// //生成信息看板XML
			// try{
			// ADMXMLTool.getInstance().creatXMLFile(caseNo);
			// }catch(Exception e){}
			this.addMRO("update"); // 修改 病历 MRO
			this.setUIAdmF();
		}
	}

	/**
	 * 检查是否可以修改床位 true 允许修改 false 不允许
	 *
	 * @return boolean
	 */
	public boolean checkBedNo() {
		boolean check = false;
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm result = SYSBedTool.getInstance().checkInBed(parm);
		if (result.getErrCode() < 0) {
			this.messageBox_(result.getErrText());
			return false;
		}
		int count = result.getCount("BED_STATUS");
		if (count == -1 || count == 0) {
			check = true;
			return check;
		}
		for (int i = 0; i < count; i++) {
			if (result.getData("BED_STATUS", i) == null || "".equals(result.getData("BED_STATUS", i))
					|| "0".equals(result.getData("BED_STATUS", i))) {
				check = true;
			} else if (result.getValue("BED_STATUS", i).equals("1") && result.getValue("BED_OCCU_FLG", i).equals("N")) {
				BED_NO = result.getValue("BED_NO", i);
				this.setValue("BED_DESC", BED_NO);
			}
		}
		for (int i = 0; i < count; i++) {
			if (result.getData("BED_OCCU_FLG", i) == null || "".equals(result.getData("BED_OCCU_FLG", i))
					|| "N".equals(result.getData("BED_OCCU_FLG", i))) {
				check = true;
			} else {
				check = false;
			}
		}
		if (!check) {
			int message = this.messageBox("消息", "此病患已包床是否继续？", 0);
			if (message == 0) {
				check = true;
			} else {
				check = false;
				return check;
			}
		}

		TParm bedNo = new TParm();
		bedNo.setData("BED_NO", this.getValue("BED_DESC"));
		TParm checkbed = ADMInpTool.getInstance().QueryBed(bedNo);
		String mrNo = this.getValueString("MR_NO");
		if (checkbed.getData("ALLO_FLG", 0) != null) {
			if (checkbed.getData("ALLO_FLG", 0).equals("Y") && !(mrNo.equals(checkbed.getData("MR_NO", 0)))) {
				this.messageBox("此床已占用,请从新选择床位");
				return false;
			}
		}
		return check;
	}

	/**
	 * 检核是否可以取消住院
	 *
	 * @return boolean
	 */
	public boolean checkCanInp() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		boolean result = ADMTool.getInstance().checkCancelOutInp(parm);
		return result;
	}

	/**
	 * 校验预交金余额
	 * 
	 * @return boolean
	 */
	public boolean checkBilPay() {
		TParm parm = BILPayTool.getInstance().selBilPayLeft(caseNo);
		if (parm.getErrCode() < 0) {
			return false;
		}
		if (parm.getDouble("PRE_AMT", 0) > 0) {
			return false;
		}

		return true;
	}

	/**
	 * 读取UI界面资料
	 *
	 * @return TParm
	 */
	public TParm readData() {
		TParm parm = new TParm();
		parm.setData("MR_NO", getValue("MR_NO")); // 病案号
		parm.setData("NEW_BORN_FLG", getValue("NEW_BORN_FLG")); // 新生儿注记
		parm.setData("ADM_SOURCE", getValue("ADM_SOURCE")); // 病患来源
		parm.setData("DEPT_CODE", getValue("DEPT_CODE")); // 住院科别
		parm.setData("TOTAL_BILPAY", getValueDouble("TOTAL_BILPAY")); // 预交金
		parm.setData("PATIENT_CONDITION", getValue("PATIENT_CONDITION")); // 入院状态
		parm.setData("SERVICE_LEVEL", getValue("SERVICE_LEVEL")); // 服务等级
		parm.setData("CTZ1_CODE", getValue("PAT_CTZ_CODE")); // 将付款方式一 改为 就诊身份 by huangjw 20140804
		parm.setData("CTZ2_CODE", null != this.getValue("LUMPWORK_CTZ_CODE") ? this.getValue("LUMPWORK_CTZ_CODE") : ""); // 2====pangben
																															// 2015-6-19
																															// 添加套餐第二身份
		parm.setData("CTZ3_CODE", ""); // 3
		parm.setData("IN_COUNT", getText("IN_COUNT")); // 入院次数
		parm.setData("IN_DATE", getValue("IN_DATE")); // 3入院日期
		parm.setData("DEPT_CODE", getValue("DEPT_CODE")); // 住院科别
		parm.setData("STATION_CODE", getValue("STATION_CODE")); // 住院病区
		parm.setData("BED_NO", getValue("BED_DESC")); // 床位号
		parm.setData("OPD_DR_CODE", this.getValue("OPD_DR_CODE")); // 门急诊医师
		parm.setData("OPD_DEPT_CODE", this.getValue("OPD_DEPT_CODE")); // 门急诊科室 duzhw add
		parm.setData("VS_DR_CODE", getValue("VS_DR_CODE")); // 经治医师
		parm.setData("ATTEND_DR_CODE", getValue("ATTEND_DR_CODE")); // 主治医师
		parm.setData("ADM_DATE", getValue("ADM_DATE")); // 登记日期
		parm.setData("RED_SIGN", getValueDouble("RED_SIGN")); // 红色警戒
		parm.setData("YELLOW_SIGN", getValueDouble("YELLOW_SIGN")); // 黄色警戒
		parm.setData("AGN_CODE", this.getValueString("AGN_CODE"));
		parm.setData("AGN_INTENTION", this.getValueString("AGN_INTENTION"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("SPECIAL_DIET", getValueString("SPECIAL_DIET"));// 特殊饮食
		parm.setData("ID_TYPE", this.getValueString("ID_TYPE"));// 证件类型
		parm.setData("LUMPWORK_CODE", null != this.getValue("LUMPWORK_CODE") ? this.getValue("LUMPWORK_CODE") : "");// ==liling
																													// 20140512
																													// 包干套餐
		if (this.getValue("INSURE_INFO_1") != null && !this.getValue("INSURE_INFO_1").equals("")) {
			parm.setData("INSURE_INFO", this.getValue("INSURE_INFO_1"));// 保险信息 add by huangjw 20150804
		} else {
			parm.setData("INSURE_INFO", "");// 保险信息 add by huangjw 20150804
		}
		// parm.setData("LUMPWORK_CTZ_CODE",
		// this.getValue("LUMPWORK_CTZ_CODE"));//====pangben 2015-6-19 套餐身份

		// 安心价注记
		parm.setData("REASSURE_FLG", this.getValueBoolean("REASSURE_FLG") ? "Y" : "N");

		return parm;
	}

	/**
	 * 
	 * @Title: getCheckPatPackage @Description: TODO(校验套餐身份是否启用) @author
	 *         Dangzhang @param mr_no @param caseNo @param packCode @param
	 *         used_flg @return @throws
	 */
	public TParm getCheckPatPackage(String mr_no, String caseNo, String packCode, String used_flg) {
		// 套餐信息sql
		String sqlL = " SELECT B.PACKAGE_CODE, C.PACKAGE_DESC, A.AR_AMT," + " A.TRADE_NO,C.CTZ_CODE,B.CASE_NO "
				+ " FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B, MEM_PACKAGE C "
				+ " WHERE A.TRADE_NO = B.TRADE_NO AND B.PACKAGE_CODE = C.PACKAGE_CODE " + "  AND C.ADM_TYPE = 'I' "
				+ " AND A.MR_NO='" + mr_no + "' AND B.REST_TRADE_NO IS NULL ";
		if (!used_flg.equals("") && used_flg.length() > 0) {
			sqlL += " AND A.USED_FLG = '" + used_flg + "'";
		}
		if (!packCode.equals("") && packCode.length() > 0) {
			sqlL += " AND B.PACKAGE_CODE = '" + packCode + "'";
		}
		if (caseNo.length() > 0) {
			sqlL += " AND A.CASE_NO='" + caseNo + "' ";
		}
		sqlL += " GROUP BY B.PACKAGE_CODE, C.PACKAGE_DESC, A.AR_AMT, A.TRADE_NO,C.CTZ_CODE,B.CASE_NO";
		TParm parmL = new TParm(TJDODBTool.getInstance().select(sqlL));
		return parmL;
	}

	/**
	 * 病患套餐信息汇总 (mr_no为病案号,used_flg为套餐使用状态注记:"0"为未使用,"1"是使用)
	 * 
	 * @param mr_no
	 *            String
	 * @param used_flg
	 *            String
	 * @return parmL TParm
	 */
	public TParm getPatPackageData(String mr_no, String caseNo, String packCode, String used_flg) {
		// 套餐信息sql
		String sqlL = " SELECT B.PACKAGE_CODE, C.PACKAGE_DESC, A.AR_AMT," + " A.TRADE_NO,C.CTZ_CODE,B.CASE_NO "
				+ " FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B, MEM_PACKAGE C "
				+ " WHERE A.TRADE_NO = B.TRADE_NO AND B.PACKAGE_CODE = C.PACKAGE_CODE  " + "  AND C.ADM_TYPE = 'I' "
				+ " AND A.MR_NO='" + mr_no + "' AND B.REST_TRADE_NO IS NULL ";
		if (!used_flg.equals("") && used_flg.length() > 0) {
			sqlL += " AND A.USED_FLG = '" + used_flg + "'";
		}
		if (!packCode.equals("") && packCode.length() > 0) {
			sqlL += " AND B.PACKAGE_CODE = '" + packCode + "'";
		}
		if (caseNo.length() > 0) {
			sqlL += " AND A.CASE_NO='" + caseNo + "' ";
		}
		sqlL += " GROUP BY B.PACKAGE_CODE, C.PACKAGE_DESC, A.AR_AMT, A.TRADE_NO,C.CTZ_CODE,B.CASE_NO";
		TParm parmL = new TParm(TJDODBTool.getInstance().select(sqlL));
		return parmL;
	}

	/**
	 * 
	 * @Title: getAdmInpPackParm @Description: TODO(在院病患查询套餐数据) @author
	 *         pangben @param mrNo @return @throws
	 */
	private TParm getAdmInpPackParm(String mrNo) {
		String sql = "SELECT A.LUMPWORK_CODE AS PACKAGE_CODE,A.CTZ2_CODE AS CTZ_CODE,B.AR_AMT,B.TRADE_NO FROM ADM_INP A ,MEM_PACKAGE_TRADE_M B,MEM_PAT_PACKAGE_SECTION C "
				+ "WHERE A.CASE_NO=B.CASE_NO AND B.TRADE_NO=C.TRADE_NO AND A.CASE_NO=C.CASE_NO AND A.LUMPWORK_CODE=C.PACKAGE_CODE "
				+ "AND A.MR_NO='" + mrNo
				+ "' AND A.DS_DATE IS NULL AND A.CANCEL_FLG='N' GROUP BY A.LUMPWORK_CODE,A.CTZ2_CODE,B.AR_AMT,B.TRADE_NO";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}

	/**
	 * 界面数据检查
	 * 
	 * @return Boolean
	 */
	public Boolean checkData() {
		// ------------------------------st yanmm 20170803 界面校验
		if (this.getValueString("PAT_NAME").length() <= 0 || this.getValueString("PAT_NAME") == null) {
			this.messageBox_("姓名不能为空！");
			this.grabFocus("PAT_NAME");
			return false;
		}
		if ("".equals(getValue("TEL_HOME"))) {
			this.messageBox("请输入电话");
			this.grabFocus("TEL_HOME");
			return false;
		}
		if (this.getTextFormat("ID_TYPE").getText().equals("")) {
			this.messageBox_("请选择证件类型");
			this.grabFocus("ID_TYPE");
			return false;
		}
		if (!getTextFormat("ID_TYPE").getText().equals("其他")) {// 校验证件类型 add by
																// huangjw
																// 20140721
			if (!this.getValueBoolean("FOREIGNER_FLG")) {
				if ("".equals(this.getValueString("IDNO"))) {
					this.messageBox_("请输入身份证号");
					this.grabFocus("IDNO");
					return false;
				}
			}
		}
		if ("".equals(this.getValueString("SEX_CODE"))) {
			this.messageBox_("请选择性别！");
			this.grabFocus("SEX_CODE");
			return false;
		}

		if ("".equals(this.getValueString("BIRTH_DATE"))) {
			this.messageBox_("请选择出生年月");
			this.grabFocus("BIRTH_DATE");
			return false;
		}

		// if ("".equals(getValue("PAT_CTZ"))) {
		// this.grabFocus("PAT_CTZ") ;
		// this.messageBox("付款方式");
		// return false;
		// }
		//

		if (!this.getTextFormat("ID_TYPE").getText().equals("其他")) {
			if (!this.getValueBoolean("FOREIGNER_FLG")) {
				if ("".equals(this.getValue("IDNO"))) {
					this.messageBox_("请输入身份证号");
					this.grabFocus("IDNO");
					return false;
				}
			}
		}
		if (this.getTextFormat("HOMEPLACE_CODE").getText().equals("")) {
			this.messageBox_("请选择出生地");
			this.grabFocus("HOMEPLACE_CODE");
			return false;
		}
		if (this.getTextFormat("BIRTHPLACE").getText().equals("")) {
			this.messageBox_("请选择籍贯");
			this.grabFocus("BIRTHPLACE");
			return false;
		}
		if ("".equals(this.getValue("SPECIES_CODE"))) {
			this.messageBox_("请选择民族");
			this.grabFocus("SPECIES_CODE");
			return false;
		}
		if ("".equals(this.getValue("MARRIAGE_CODE"))) {
			this.messageBox_("请选择婚姻状态");
			this.grabFocus("MARRIAGE_CODE");
			return false;
		}
		if (this.getTextFormat("NATION_CODE").getText().equals("")) {
			this.messageBox_("请选择国籍");
			this.grabFocus("NATION_CODE");
			return false;
		}
		if ("".equals(this.getValue("OCC_CODE"))) {
			this.messageBox_("请选择职业");
			this.grabFocus("OCC_CODE");
			return false;
		}
		if ("".equals(this.getValue("COMPANY_DESC"))) {
			this.messageBox_("请输入工作单位");
			this.grabFocus("COMPANY_DESC");
			return false;
		}
		if ("".equals(this.getValue("TEL_COMPANY"))) {
			this.messageBox_("请输入公司电话");
			this.grabFocus("TEL_COMPANY");
			return false;
		}
		if ("".equals(this.getValue("ADDRESS"))) {
			this.messageBox_("请输入现住地址");
			this.grabFocus("ADDRESS");
			return false;
		}
		if (this.getTextFormat("POST_CODE").getText().equals("")) {
			this.messageBox_("请选择现住邮编");
			this.grabFocus("POST_CODE");
			return false;
		}
		if ("".equals(this.getValue("RESID_ADDRESS"))) {
			this.messageBox_("请输入户口地址");
			this.grabFocus("RESID_ADDRESS");
			return false;
		}
		if (this.getTextFormat("RESID_POST_CODE").getText().equals("")) {
			this.messageBox_("请选择户口邮编");
			this.grabFocus("RESID_POST_CODE");
			return false;
		}
		if ("".equals(this.getValue("ADDRESS_COMPANY"))) {
			this.messageBox_("请输入单位住址");
			this.grabFocus("ADDRESS_COMPANY");
			return false;
		}
		if (this.getTextFormat("POST_COMPANY").getText().equals("")) {
			this.messageBox_("请选择单位邮编");
			this.grabFocus("POST_COMPANY");
			return false;
		}
		if ("".equals(this.getValue("CONTACTS_NAME"))) {
			this.messageBox_("请输入联系人姓名");
			this.grabFocus("CONTACTS_NAME");
			return false;
		}
		if ("".equals(this.getValue("RELATION_CODE"))) {
			this.messageBox_("请选择关系");
			this.grabFocus("RELATION_CODE");
			return false;
		}
		if ("".equals(this.getValue("CONTACTS_TEL"))) {
			this.messageBox_("请输入联系人电话");
			this.grabFocus("CONTACTS_TEL");
			return false;
		}
		if ("".equals(this.getValue("CONTACTS_ADDRESS"))) {
			this.messageBox_("请输入联系人地址");
			this.grabFocus("CONTACTS_ADDRESS");
			return false;
		}
		// -------------en yanmm
		if ("".equals(this.getValueString("ADM_SOURCE"))) {
			this.messageBox_("请输入病患来源");
			return false;
		}
		if (!"09".equals(this.getValueString("ADM_SOURCE"))) {// 病患来源为其他则不用校验
			if (!"03".equals(this.getValueString("ADM_SOURCE"))) {// add by
																	// sunqy
																	// 20140606"其他医疗机构转入"也不用校验
				if ("".equals(this.getValueString("OPD_DEPT_CODE"))) {
					this.messageBox_("请输入门急诊科室");
					return false;
				}
				if ("".equals(this.getValueString("OPD_DR_CODE"))) {
					this.messageBox_("请输入门急诊医师");
					return false;
				}
			}
		}
		if ("".equals(this.getValueString("LUMPWORK_CODE")) && "Y".equals(this.getValueString("PACKAGE_FLG"))) {// add
																												// by
																												// huangjw
																												// 20150312
			this.messageBox("请输入套餐类型");
			return false;
		}
		if ("".equals(this.getValueString("PATIENT_CONDITION"))) {
			this.messageBox_("请输入入院状态");
			return false;
		}
		if ("".equals(this.getValueString("SERVICE_LEVEL"))) {
			this.messageBox_("请选择服务等级");
			return false;
		}
		if ("".equals(this.getValueString("PAT_CTZ_CODE"))) {// 将 付款方式 修改为 就诊身份
			this.messageBox_("请输入就诊身份");
			return false;
		}
		// if ("".equals(this.getValueString("TOTAL_BILPAY"))) {//delete by
		// sunqy 20140523
		// this.messageBox_("请输预交金");
		// return false;
		// }
		if ("".equals(this.getValueString("DEPT_CODE"))) {
			this.messageBox_("请输入住院科别");
			return false;
		}
		if ("".equals(this.getValueString("STATION_CODE"))) {
			this.messageBox_("请输入住院病区");
			return false;
		}

		// modify by yangjj 20160606
		/*
		 * if ("".equals(this.getValueString("VS_DR_CODE"))) {
		 * this.messageBox_("请输入经治医师"); return false; }
		 */

		if ("".equals(this.getValueString("IN_DATE"))) {
			this.messageBox_("请输入入院日期");
			return false;
		}
		// ====zhangp 20120828 start
		// if ("".equals(this.getValueString("BIRTH_DATE"))) {
		// this.messageBox_("请输入出生年月");
		// return false;
		// }
		// ===zhangp 20120828 end
		if ("Y".equals(getValue("NEW_BORN_FLG"))) {
			if (getValue("IPD_NO") == null || "".equals(getValue("IPD_NO"))) {
				this.messageBox_("未选择母亲");
				return false;
			}
		}
		// ============chenxi modify 20130422===== 病案室要求在住院登记填写国籍和身份证号
		/*
		 * if ("".equals(this.getValueString("NATION_CODE"))) {
		 * this.messageBox_("请输入国籍"); this.grabFocus("NATION_CODE") ; return false; }
		 */
		/*
		 * if(this.getValueString("COMPANY_DESC") == null ||
		 * this.getValueString("COMPANY_DESC").length()<=0 ){
		 * this.messageBox("请填写工作单位,没有请填写'无'"); this.grabFocus("COMPANY_DESC"); return
		 * false; }
		 */

		if (this.getValue("INSURE_INFO_1") != null && !this.getValue("INSURE_INFO_1").equals("")) {
			if (!onCheckInsure()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 套餐类型挑勾add by huangjw 20150320
	 */
	public void selectEvent() {
		if ("Y".equals(this.getValueString("PACKAGE_FLG"))) {
			if (ADMInpTool.getInstance().containsAny(getValueString("MR_NO"), "-")) {// 新生儿病案号判断--xiongwg20150715
				this.messageBox("新生儿不能修改套餐类型");
				callFunction("UI|LUMPWORK_CODE|setEnabled", false);
				this.setValue("PACKAGE_FLG", "N");
			} else {
				this.getTextFormat("LUMPWORK_CODE").setEnabled(true);
			}

		} else {
			this.setValue("LUMPWORK_CODE", "");
			this.setValue("LUMPWORK_CTZ_CODE", "");
			this.setValue("AR_AMT_T", 0.00);
			// this.clearValue("LUMPWORK_CTZ_CODE;AR_AMT_T");
			this.getTextFormat("LUMPWORK_CODE").setEnabled(false);
			// onLumpworkCtzCode();
		}
	}

	/**
	 * 调用病患信息界面
	 */
	public void onPatInfo() {
		TParm parm = new TParm();
		parm.setData("ADM", "ADM");
		parm.setData("MR_NO", this.getValueString("MR_NO").trim());
		this.openDialog("%ROOT%\\config\\sys\\SYSPatInfo.x", parm);
	}

	/**
	 * 检查是否住院中 false 未住院 true 住院中
	 *
	 * @param MrNo
	 *            String
	 * @return boolean
	 */
	public boolean checkAdmInp(String MrNo) {
		TParm parm = new TParm();
		parm.setData("MR_NO", MrNo);
		TParm result = ADMInpTool.getInstance().checkAdmInp(parm);
		if (result.checkEmpty("IPD_NO", result))
			return false;
		caseNo = result.getData("CASE_NO", 0).toString();
		return true;
	}

	/**
	 * 清空
	 */
	public void onClear() {
		pat = new Pat();
		caseNo = "";
		McaseNo = "";
		saveType = "NEW";
		BED_NO = "";
		parmEKT = new TParm();
		reParm = new TParm();
		TPanel photo = (TPanel) this.getComponent("VIEW_PANEL");
		Image image = null;
		Pic pic = new Pic(image);
		pic.setSize(photo.getWidth(), photo.getHeight());
		pic.setLocation(0, 0);
		photo.removeAll();
		photo.add(pic);
		pic.repaint();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
		String date = df.format(SystemTool.getInstance().getDate());

		// add by yangjj 20160428
		this.callFunction("UI|IN_DATE|setEnabled", true);

		setValue("IN_DATE", StringTool.getTimestamp(date, "yyyyMMddHH")); // 预定日期
		setValue("IN_COUNT", "1"); // 预定日期
		this.setValue("NEW_BORN_FLG", "N");
		callFunction("UI|NEW_PAT|setEnabled", false); // 病患保存
		this.callFunction("UI|NEW_PAT|setText", "新增病患保存");
		callFunction("UI|MR_NO|setEnabled", true); // 病案号
		callFunction("UI|IPD_NO|setEnabled", true); // 住院号
		clearValue("MR_NO");
		clearValue(
				"ADM_SOURCE;PR_DEPT_CODE;OPD_DR_CODE;TOTAL_BILPAY;SERVICE_LEVEL;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;YELLOW_SIGN;RED_SIGN;DEPT_CODE;VS_DR_CODE;PATIENT_CONDITION;DEPT_CODE;BED_NO;BED_DESC;STATION_CODE");
		clearValue(
				"RESID_ADDRESS;MR_NO;IPD_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;AGE;HOMEPLACE_CODE;MARRIAGE_CODE;OCC_CODE;SPECIES_CODE;tComboBox_0;NATION_CODE;IDNO;COMPANY_DESC;TEL_01;POST_CODE;RESID_ROAD;RESID_POST_CODE;CONTACTS_NAME;CONTACTS_ADDRESS;CONTACTS_TEL;NEW_PAT;RELATION_CODE;SPECIAL_DIET;ID_TYPE");// 特殊饮食-duzhw
																																																																												// add
		clearValue("PAT_CTZ;;TEL_COMPANY;TEL_HOME;M_MR_NO;M_NAME;FOREIGNER_FLG;ADDRESS");
		clearValue("ADDRESS_COMPANY;POST_COMPANY;AGN_CODE;AGN_INTENTION;BIRTHPLACE;CUSTOMER_SOURCE");
		clearValue("LUMPWORK_CODE");// ==liling 20140512 add
		clearValue("CELL_PHONE");
		clearValue("PAT_CTZ_CODE");
		clearValue("AR_AMT_T");
		this.clearValue("INSURE_INFO_1");
		// this.setValue("POST_C", "");
		// this.setValue("POST_P", "");
		// ((TComboBox) this.getComponent("POST_P")).onQuery();
		// ((TComboBox) this.getComponent("POST_C")).onQuery();
		// this.setValue("RESID_POST_C", "");
		// this.setValue("RESID_POST_P", "");
		// ((TComboBox) this.getComponent("RESID_POST_P")).onQuery();
		// ((TComboBox) this.getComponent("RESID_POST_C")).onQuery();
		// this.setValue("COMPANY_POST_P", "");
		// this.setValue("COMPANY_POST_C", "");
		// ((TComboBox) this.getComponent("COMPANY_POST_P")).onQuery();
		// ((TComboBox) this.getComponent("COMPANY_POST_C")).onQuery();
		this.setValue("OPD_DEPT_CODE", "");
		this.setValue("OPD_DR_CODE", "");
		this.setValue("DEPT_CODE", "");
		this.setValue("STATION_CODE", "");
		this.setValue("VS_DR_CODE", "");
		setUIT();
		setMenu(false);
		this.callFunction("UI|LM1|setVisible", false);
		this.callFunction("UI|LM2|setVisible", false);
		this.callFunction("UI|M_MR_NO|setVisible", false);
		this.callFunction("UI|M_NAME|setVisible", false);
		callFunction("UI|AGN_CODE|setEnabled", false); // 31天内再次住院等级
		callFunction("UI|AGN_INTENTION|setEnabled", false); // 31天内再次住院等级
		callFunction("UI|PHOTO_BOTTON|setEnabled", false);
		this.setValue("PACKAGE_FLG", "Y");// 清空时套餐类型挑勾add by huangjw 20150320
		this.getTextFormat("LUMPWORK_CODE").setEnabled(true);// 回复可选add by huangjw 20150320
		this.setValue("LUMPWORK_CTZ_CODE", "");// 套餐身份设置
		this.callFunction("UI|INSURE_INFO_1|setEnabled", false);
		//
		this.setValue("REASSURE_FLG", "N");// 安心价
	}

	/**
	 * 病患住院中控件不可编辑
	 */
	public void setUIAdmF() {
		callFunction("UI|ADM_SOURCE|setEnabled", false);
		callFunction("UI|OPD_DEPT_CODE|setEnabled", false);
		callFunction("UI|OPD_DR_CODE|setEnabled", false);
		callFunction("UI|PATIENT_CONDITION|setEnabled", false);
		// callFunction("UI|SERVICE_LEVEL|setEnabled", false);
		callFunction("UI|PAT_CTZ_CODE|setEnabled", false);// 将 CTZ2_CODE1 改为PAT_CTZ_CODE by huangjw 20140804
		callFunction("UI|CTZ2_CODE|setEnabled", false);
		callFunction("UI|CTZ3_CODE|setEnabled", false);
		callFunction("UI|tNumberTextField_3|setEnabled", false);
		callFunction("UI|DEPT_CODE|setEnabled", false);
		callFunction("UI|STATION_CODE|setEnabled", false);
		callFunction("UI|VS_DR_CODE|setEnabled", false);
		callFunction("UI|ATTEND_DR_CODE|setEnabled", false);
		callFunction("UI|TOTAL_BILPAY|setEnabled", false);
		callFunction("UI|tButton_0|setEnabled", false);
	}

	/**
	 * 控件可编辑
	 */
	public void setUIT() {
		callFunction("UI|ADM_SOURCE|setEnabled", true);
		callFunction("UI|OPD_DEPT_CODE|setEnabled", true);
		callFunction("UI|OPD_DR_CODE|setEnabled", true);
		callFunction("UI|PATIENT_CONDITION|setEnabled", true);
		callFunction("UI|SERVICE_LEVEL|setEnabled", true);
		callFunction("UI|PAT_CTZ_CODE|setEnabled", true);// 将 CTZ2_CODE1 改为PAT_CTZ_CODE by huangjw 20140804
		callFunction("UI|CTZ2_CODE|setEnabled", true);
		callFunction("UI|CTZ3_CODE|setEnabled", true);
		callFunction("UI|tNumberTextField_3|setEnabled", true);
		callFunction("UI|DEPT_CODE|setEnabled", true);
		callFunction("UI|STATION_CODE|setEnabled", true);
		callFunction("UI|VS_DR_CODE|setEnabled", true);
		callFunction("UI|ATTEND_DR_CODE|setEnabled", true);
		callFunction("UI|TOTAL_BILPAY|setEnabled", true);
		callFunction("UI|tButton_0|setEnabled", true);
	}

	/**
	 * 查询住院中病患基本信息
	 */
	private void inInpdata() {
		TParm parm = new TParm();
		parm.setData("MR_NO", getValue("MR_NO"));
		parm.setDataN("IPD_NO", getValue("IPD_NO"));
		// 查询在院病患的基本信息
		TParm result = ADMInpTool.getInstance().queryCaseNo(parm);
		TParm resvParm = new TParm();
		resvParm.setData("MR_NO", getValue("MR_NO"));
		// this.messageBox("MR_NO=>"+getValue("MR_NO"));//==liling
		resvParm.setData("IN_CASE_NO", result.getData("CASE_NO", 0));
		TParm resv = ADMResvTool.getInstance().selectAll(resvParm);
		// System.out.println("resv=====>"+resv);
		this.setValue("OPD_DEPT_CODE", resv.getData("OPD_DEPT_CODE", 0));
		this.setValue("PATIENT_CONDITION", resv.getData("PATIENT_CONDITION", 0));
		// this.messageBox((String)resv.getData("PATIENT_CONDITION", 0));//==liling
		BED_NO = result.getValue("BED_NO", 0);
		IPD_NO = result.getValue("IPD_NO", 0);
		this.setValue("IPD_NO", result.getValue("IPD_NO", 0));
		this.setValue("OPD_DR_CODE", result.getData("OPD_DR_CODE", 0));
		this.setValue("ADM_SOURCE", result.getData("ADM_SOURCE", 0));
		this.setValue("TOTAL_BILPAY", result.getData("TOTAL_BILPAY", 0));
		this.setValue("SERVICE_LEVEL", result.getData("SERVICE_LEVEL", 0));
		this.setValue("PAT_CTZ_CODE", result.getData("CTZ1_CODE", 0));// 将 CTZ1_CODE 改为 PAT_CTZ_CODE by huangjw 20140804
		this.setValue("CTZ2_CODE", result.getData("CTZ2_CODE", 0));
		this.setValue("CTZ3_CODE", result.getData("CTZ3_CODE", 0));
		this.setValue("DEPT_CODE", result.getData("DEPT_CODE", 0));
		this.setValue("OPD_DEPT_CODE", result.getData("OPD_DEPT_CODE", 0));// 门急诊科室 duzhw add
		this.setValue("STATION_CODE", result.getData("STATION_CODE", 0));
		this.setValue("VS_DR_CODE", result.getData("VS_DR_CODE", 0));
		this.setValue("YELLOW_SIGN", result.getData("YELLOW_SIGN", 0));
		this.setValue("RED_SIGN", result.getData("RED_SIGN", 0));
		this.setValue("IN_DATE", result.getData("IN_DATE", 0));
		this.setValue("NEW_BORN_FLG", result.getBoolean("NEW_BORN_FLG", 0));
		this.setValue("IN_COUNT", result.getData("IN_COUNT", 0));
		this.setValue("AGN_CODE", result.getData("AGN_CODE", 0)); // 31天再住院等级
		this.setValue("AGN_INTENTION", result.getData("AGN_INTENTION", 0)); // 31天再住院原因
		this.setValue("BED_NO", getBedDesc(BED_NO));
		this.setValue("INSURE_INFO_1", result.getData("INSURE_INFO", 0));
		TParm result1 = CLPSingleDiseTool.getInstance().queryADMResvSDInfo(parm);// add by wanglong 20121025
		this.setValueForParm("DISE_CODE", result1, 0);// add by wanglong 20121025
		this.setValue("LUMPWORK_CODE", result.getData("LUMPWORK_CODE", 0));// ==liling 201404512
		if (null != result.getData("LUMPWORK_CODE", 0) && result.getValue("LUMPWORK_CODE", 0).length() > 0) {
			this.setValue("LUMPWORK_CTZ_CODE", result.getData("CTZ2_CODE", 0));// 显示套餐第二身份
		}
		// 判断该病患是否是新生儿
		if (result.getBoolean("NEW_BORN_FLG", 0)) {
			McaseNo = result.getValue("M_CASE_NO", 0); // 查询预约信息中是否有母亲的病案号
			// 查询母亲的住院信息 获取住院号(婴儿的IPD_NO与母亲相同)
			TParm admParm = new TParm();
			admParm.setData("CASE_NO", McaseNo);
			TParm admInfo = ADMTool.getInstance().getADM_INFO(admParm);
			Pat M_PAT = Pat.onQueryByMrNo(admInfo.getValue("MR_NO", 0));
			this.setValue("M_MR_NO", admInfo.getValue("MR_NO", 0));
			this.setValue("M_NAME", M_PAT.getName());
			this.callFunction("UI|LM1|setVisible", true);
			this.callFunction("UI|LM2|setVisible", true);
			this.callFunction("UI|M_MR_NO|setVisible", true);
			this.callFunction("UI|M_NAME|setVisible", true);
		} else {
			this.callFunction("UI|LM1|setVisible", false);
			this.callFunction("UI|LM2|setVisible", false);
			this.callFunction("UI|M_MR_NO|setVisible", false);
			this.callFunction("UI|M_NAME|setVisible", false);
		}
		if ("".equals(result.getData("LUMPWORK_CODE", 0)) || result.getData("LUMPWORK_CODE", 0) == null) {
			this.callFunction("UI|LUMPWORK_CODE|setEnabled", false);
			this.setValue("PACKAGE_FLG", "N");
		}
	}

	/**
	 * 计算年龄
	 */
	/*
	 * public void setBirth() { if (getValue("BIRTH_DATE") == null ||
	 * "".equals(getValue("BIRTH_DATE"))) return;
	 * 
	 * String AGE =DateUtil.showAge( (Timestamp) getValue("BIRTH_DATE"), (Timestamp)
	 * getValue("IN_DATE")); setValue("AGE", AGE); }
	 */
	/**
	 * 计算年龄
	 */

	/*
	 * public String setBirth(Timestamp birthDay) {
	 * 
	 * //birthDay=(Timestamp) getValue("BIRTH_DATE");
	 * 
	 * Timestamp sysDate = SystemTool.getInstance().getDate(); Timestamp temp =
	 * birthDay == null ? sysDate : birthDay; String age = "0"; age =
	 * DateUtil.showAge(temp, sysDate); setValue("AGE", age); return age; }
	 */
	/**
	 * 新增病患控件可编辑
	 */
	public void setUi() {
		callFunction("UI|PAT_NAME|setEnabled", true);
		callFunction("UI|SEX_CODE|setEnabled", true);
		callFunction("UI|BIRTH_DATE|setEnabled", true);
		callFunction("UI|AGE|setEnabled", true);
		callFunction("UI|OCC_CODE|setEnabled", true);
		callFunction("UI|BORN|setEnabled", true);
		callFunction("UI|IDNO|setEnabled", true);
		callFunction("UI|SPECIES_CODE|setEnabled", true);
		callFunction("UI|NATION_CODE|setEnabled", true);
		callFunction("UI|MARRIAGE_CODE|setEnabled", true);
		callFunction("UI|COMPANY_DESC|setEnabled", true);
		callFunction("UI|POST_CODE|setEnabled", true);
		callFunction("UI|RESID_POST_CODE|setEnabled", true);
		callFunction("UI|TEL_01|setEnabled", true);
		callFunction("UI|RESID_ROAD|setEnabled", true);
		callFunction("UI|CONTACTS_NAME|setEnabled", true);
		callFunction("UI|RELATION_CODE|setEnabled", true);
		callFunction("UI|CONTACTS_TEL|setEnabled", true);
		callFunction("UI|CONTACTS_ADDRESS|setEnabled", true);

	}

	/**
	 * 新增病患点选事件
	 */
	public void onNewPatInfo() {
		this.onClear();
		TCheckBox checkbox = (TCheckBox) this.callFunction("UI|NEW_PAT_INFO|getThis");
		if (checkbox.isSelected()) {
			callFunction("UI|NEW_BORN_FLG|setEnabled", false);
			callFunction("UI|MR_NO|setEnabled", false);
			callFunction("UI|IPD_NO|setEnabled", false);
			callFunction("UI|NEW_PAT|setEnabled", true);
			this.callFunction("UI|NEW_PAT|setText", "新增病患保存");
		} else {
			callFunction("UI|NEW_BORN_FLG|setEnabled", true);
			callFunction("UI|MR_NO|setEnabled", true);
			callFunction("UI|IPD_NO|setEnabled", true); // 住院号
			callFunction("UI|NEW_PAT|setEnabled", false);
			this.callFunction("UI|NEW_PAT|setText", "修改病患保存");
		}
	}

	/**
	 * 床位检索
	 */
	public void onBedNo() {
		TParm sendParm = new TParm();
		if (getValue("DEPT_CODE") == null || "".equals(getValue("DEPT_CODE"))) {
			this.messageBox("请选择科室！");
			return;
		}
		if (getValue("STATION_CODE") == null || "".equals(getValue("STATION_CODE"))) {
			this.messageBox("请选择病区！");
			return;
		}
		sendParm.setData("DEPT_CODE", getValue("DEPT_CODE"));
		sendParm.setData("STATION_CODE", getValue("STATION_CODE"));
		sendParm.setData("TYPE", "RESV"); // ===== chenxi modify 20130301 预约床时，占床也可预约
		TParm reParm = (TParm) this.openDialog("%ROOT%\\config\\adm\\ADMQueryBed.x", sendParm);
		if (reParm != null) {
			this.setValue("BED_NO", getBedDesc(reParm.getValue("BED_NO", 0)));
			this.setValue("YELLOW_SIGN", reParm.getData("YELLOW_SIGN", 0));
			this.setValue("RED_SIGN", reParm.getData("RED_SIGN", 0));
		}
	}

	/**
	 * 包床管理
	 */
	public void onBed() {
		if (this.getValue("BED_DESC") == null || "".equals(this.getValue("BED_DESC"))) {
			this.messageBox("此病患还未安排床位！");
			return;
		}
		TParm bed = new TParm();

		TParm parm = new TParm();
		parm.setData("MR_NO", getValue("MR_NO"));
		parm.setData("IPD_NO", getValue("IPD_NO"));
		TParm result = ADMInpTool.getInstance().queryCaseNo(parm);
		TParm sendParm = new TParm();
		sendParm.setData("CASE_NO", result.getData("CASE_NO", 0));
		sendParm.setData("MR_NO", result.getData("MR_NO", 0));
		sendParm.setData("IPD_NO", result.getData("IPD_NO", 0));
		sendParm.setData("DEPT_CODE", getValue("DEPT_CODE"));
		sendParm.setData("STATION_CODE", getValue("STATION_CODE"));
		sendParm.setData("BED_NO", getValue("BED_DESC"));
		bed.setData("BED_NO", getValue("BED_DESC"));
		TParm check = SYSBedTool.getInstance().queryRoomBed(bed);
		String caseNo = result.getData("CASE_NO", 0).toString().trim();
		int count = check.getCount("BED_NO");

		for (int i = 0; i < count; i++) {
			if ("Y".equals(check.getData("ALLO_FLG", i)) && !caseNo.equals(check.getData("CASE_NO", i))) {
				this.messageBox("此病房已有其他病患！");
				return;
			}
		}
		TParm reParm = (TParm) this.openDialog("%ROOT%\\config\\adm\\ADMSysBedAllo.x", sendParm);
	}

	/**
	 * 绿色通道
	 */
	public void onGreenPath() {
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", "O");
		parm.setData("MR_NO", getValue("MR_NO"));
		this.openWindow("%ROOT%\\config\\bil\\BILGreenPath.x", parm);
	}

	/**
	 * 预交金
	 */
	public void onBilpay() {
		TParm sendParm = new TParm();
		sendParm.setData("CASE_NO", caseNo);
		sendParm.setData("PRE_AMT", this.getValueString("TOTAL_BILPAY"));// add by sunqy 20140523
		if (caseNo == null || "".equals(caseNo)) {
			this.messageBox("请先保存再选择预交金!");
			return;
		}
		sendParm.setData("BILL_PAY_FLG", "Y");
		this.openWindow("%ROOT%\\config\\bil\\BILPay.x", sendParm);
		// TParm parm = ADMInpTool.getInstance().queryCaseNo(sendParm);
		// this.setValue("TOTAL_BILPAY", parm.getData("TOTAL_BILPAY", 0));//delete by
		// sunqy 20140523

	}

	/**
	 * 新生儿注记 (移到预约住院此方法暂时无用了)
	 */
	public void onChild() {
		if (pat == null) {
			this.messageBox("没有病患信息！");
			return;
		}
		TParm sendParm = new TParm();
		sendParm.setData("MR_NO", this.getValue("MR_NO"));
		TParm reParm = (TParm) this.openDialog("%ROOT%\\config\\adm\\ADMBabyFlg.x", sendParm);
		if (reParm == null) {
			setValue("NEW_BORN_FLG", "N");
			return;
		}
		if (reParm.checkEmpty("IPD_NO", reParm)) {
			setValue("NEW_BORN_FLG", "N");
		} else {
			setValue("NEW_BORN_FLG", "Y");
			this.setValue("IPD_NO", reParm.getData("IPD_NO"));
			McaseNo = reParm.getData("M_CASE_NO").toString();
		}
	}

	/**
	 * 病患查询
	 */
	public void onQuery() {
		TParm sendParm = new TParm();
		TParm reParm = (TParm) this.openDialog("%ROOT%\\config\\adm\\ADMPatQuery.x", sendParm);
		if (reParm == null)
			return;
		this.setValue("MR_NO", reParm.getValue("MR_NO"));
		this.onMrno();
	}

	/**
	 * 取消住院
	 */
	public void onStop() {
		if (caseNo.length() <= 0) {
			this.messageBox("没有获得就诊病患信息");
			return;
		}
		if (!checkCanInp()) {
			this.messageBox_("此病患已经入住到床位,不可取消住院");
			return;
		}
		if (!checkBilPay()) {
			this.messageBox_("此病患还有预交金未退,不可取消住院");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm admParm = ADMInpTool.getInstance().queryCaseNo(parm);
		if (admParm.getCount() > 0) {
			if (null == admParm.getValue("IN_DATE", 0) || admParm.getValue("IN_DATE", 0).length() <= 0) {
				this.messageBox("此病患没有办理入院,不需要取消住院操作");
				return;
			}
		}
		// =====pangben 2015-10-29 校验此病患是否取消套餐
		if (null != admParm && null != admParm.getValue("LUMPWORK_CODE", 0)
				&& admParm.getValue("LUMPWORK_CODE", 0).length() > 0) {
			this.messageBox("此病患存在使用中的套餐，不可取消住院");
			return;
		}
		// =======pangben 2015-11-11 校验此病患是否有费用
		String sql = "SELECT SUM(TOT_AMT) TOT_AMT FROM IBS_ORDD WHERE CASE_NO='" + caseNo + "'";
		TParm data = new TParm(TJDODBTool.getInstance().select(sql));
		if (data.getCount() > 0 && data.getDouble("TOT_AMT", 0) != 0) {
			this.messageBox("此病患存在费用,不可以取消住院");
			return;
		}
		int check = this.messageBox("消息", "是否取消？", 0);
		if (check == 0) {
			// TParm parm = new TParm();
			// parm.setData("CASE_NO", caseNo);
			parm.setData("PSF_KIND", "INC");
			parm.setData("PSF_HOSP", "");
			parm.setData("CANCEL_FLG", "Y");
			parm.setData("CANCEL_DATE", SystemTool.getInstance().getDate());
			parm.setData("CANCEL_USER", Operator.getID());
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());

			String mrNo = getValueString("MR_NO");
			TParm parmL = getAdmInpPackParm(this.getValueString("MR_NO"));
			if (parmL.getCount() > 0) {
				parmL = parmL.getRow(0);
				parmL.setData("MR_NO", mrNo);
				parmL.setData("CASE_NO", "");
				parmL.setData("USED_FLG", "0");
				parm.setData("parmL", parmL.getData());// 更新套餐主档、时程和明细医嘱的parm--xiongwg20150703
			}
			// ======pangben modify 20110617 start
			if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
				parm.setData("REGION_CODE", Operator.getRegion());
			}
			// ======pangben modify 20110617 start
			TParm result = TIOM_AppServer.executeAction("action.adm.ADMInpAction", "ADMCanInp", parm); //
			if (result.getErrCode() < 0) {
				this.messageBox("E0005");
			} else {
				this.messageBox("P0005");

				// add by wangbin 20140905 取消住院删除临时表对应的数据 START
				this.cancelMroRegAppointment(caseNo);
				// add by wangbin 20140905 取消住院删除临时表对应的数据 END

				// add by wangbin 20141017 针对已出库后再取消住院的数据进行归还日期设定 START
				this.updateRtnDateByCaseNo(caseNo);
				// add by wangbin 20141017 针对已出库后再取消住院的数据进行归还日期设定 END
			}
			this.setMenu(false);
		} else {
			this.setMenu(true);
			return;
		}

	}

	/**
	 * 拍照
	 *
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
	 * //注册照相组件
	 */
	public void onRegist() {
		// 注册照相组件
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
			File file = new File(localFileName);
			byte[] data = FileTool.getByte(localFileName);
			if (file.exists()) {
				new File(localFileName).delete();
			}
			String root = TIOM_FileServer.getRoot();
			dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
			// 病案号大于等于10位处理
			if (mrNo.length() >= 10) {
				dir = root + dir + mrNo.substring(0, 3) + "\\" + mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9)
						+ "\\";
				// 病案号小于10位处理
			} else {
				dir = root + dir + mrNo.substring(0, 2) + "\\" + mrNo.substring(2, 4) + "\\" + mrNo.substring(4, 7)
						+ "\\";
			}
			//
			TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(), dir + photoName, data);
		} catch (Exception e) {
			System.out.println("e::::" + e.getMessage());
		}
		this.viewPhoto(pat.getMrNo());

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
			// 病案号大于10位处理
			if (mrNo.length() >= 10) {
				dir = root + dir + mrNo.substring(0, 3) + "\\" + mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9)
						+ "\\";
				// 病案号小于10位处理
			} else {
				dir = root + dir + mrNo.substring(0, 2) + "\\" + mrNo.substring(2, 4) + "\\" + mrNo.substring(4, 7)
						+ "\\";
			}
			//
			byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(), dir + fileName);
			if (data == null) {
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
				g.drawImage(image, 4, 15, null);

			}
		}
	}

	/**
	 * 住院科别Combo事件
	 */
	public void onDEPT_CODE() {
		// 清空住院病区，经治医师，床位号的选中值
		this.clearValue("STATION_CODE;VS_DR_CODE;BED_NO;BED_DESC");
	}

	/**
	 * 门诊科别Combo事件
	 */
	public void onOPD_DEPT_CODE() {
		// 清空门诊医师的选中值
		this.clearValue("OPD_DR_CODE");
	}

	/**
	 * 病案处理
	 *
	 * @param type
	 *            String new:新建 update:修改
	 */
	public void addMRO(String type) {
		String user_id = Operator.getID();
		String user_ip = Operator.getIP();
		String mr_no = this.getValueString("MR_NO");
		String hospid = "";
		TParm regionParm = SYSRegionTool.getInstance().selectdata(Operator.getRegion());
		if (regionParm.getCount() > 0) {
			hospid = regionParm.getValue("NHI_NO", 0);
		}
		// System.out.println("------------------"+hospid);
		TParm result = new TParm();
		// 判断是否新建病案
		if ("new".equals(type)) {
			TParm creat = new TParm();
			creat.setData("MR_NO", mr_no);
			creat.setData("CASE_NO", caseNo);
			creat.setData("OPT_USER", user_id);
			creat.setData("OPT_TERM", user_ip);
			// ============pangben modify 20110617 start
			if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
				creat.setData("REGION_CODE", Operator.getRegion());
			// ============pangben modify 20110617 stop
			creat.setData("HOSP_ID", hospid);
			// 新建病案
			result = MROTool.getInstance().insertMRO(creat);
			if (result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return;
			}

			// modify by wangbin 20140820 病历册主档表新增门急住标识,修改查询条件 START
			TParm queryParm = new TParm();
			// 病案号
			queryParm.setData("MR_NO", mr_no);
			// 门急住标识
			queryParm.setData("ADM_TYPE", "I");
			// 查询病历册主档表中是否存在该病患的数据
			queryParm = MROQueueTool.getInstance().selectMRO_MRV(queryParm);

			if (queryParm.getCount() <= 0) {
				TParm mroMrv = new TParm();
				String region = Operator.getRegion();
				mroMrv.setData("MR_NO", mr_no);
				// add by wangbin 20140812 病历册主档表新增字段
				mroMrv.setData("SEQ", 1);
				mroMrv.setData("ADM_TYPE", "I");
				mroMrv.setData("IPD_NO", IPD_NO);
				mroMrv.setData("CREATE_HOSP", region);
				// modify by wangbin 20140812 住院插入病历册主档表时默认插入未建档
				mroMrv.setData("IN_FLG", "0");
				mroMrv.setData("CURT_HOSP", region);
				mroMrv.setData("CURT_LOCATION", "");
				mroMrv.setData("TRAN_HOSP", region);
				mroMrv.setData("BOX_CODE", "");
				mroMrv.setData("BOOK_NO", "");
				mroMrv.setData("OPT_USER", Operator.getID());
				mroMrv.setData("OPT_TERM", Operator.getIP());
				// 增加当前案卷借阅科室和借阅人
				mroMrv.setData("CURT_LEND_DEPT_CODE", "");
				mroMrv.setData("CURT_LEND_DR_CODE", "");

				result = MROQueueTool.getInstance().insertMRO_MRV(mroMrv);
				if (result.getErrCode() < 0) {
					this.messageBox_("病历入库失败！");
				}
			}
			// modify by wangbin 20140820 病历册主档表新增门急住标识,修改查询条件 END

			// 查询病患的预约信息 用来获取门急诊诊断
			TParm resv = ADMResvTool.getInstance().selectNotIn(mr_no);
			String OE_DIAG_CODE = "";
			// 如果病患有预约信息
			if (resv.getCount() > 0) {
				OE_DIAG_CODE = resv.getValue("DIAG_CODE", 0);
			}
			TParm b_parm = new TParm();
			b_parm.setData("BED_NO", BED_NO);
			TParm bed = SYSBedTool.getInstance().queryAll(b_parm);
			// 修改病案 住院信息
			TParm adm = new TParm();
			adm.setData("IPD_NO", this.getValueString("IPD_NO"));
			adm.setData("IN_DATE", StringTool.getString((Timestamp) this.getValue("IN_DATE"), "yyyyMMddHHmmss"));
			adm.setData("IN_DEPT", this.getValueString("DEPT_CODE"));
			adm.setData("IN_STATION", this.getValueString("STATION_CODE"));
			adm.setData("IN_ROOM_NO", bed.getValue("ROOM_CODE", 0)); // 入院病室
			// 根据床位号
			// 查询出
			adm.setData("OE_DIAG_CODE", OE_DIAG_CODE); // 门急诊诊断
			adm.setData("IN_CONDITION", this.getValue("PATIENT_CONDITION")); // 入院状态
			adm.setData("IN_COUNT", this.getValue("IN_COUNT") == null ? "1" : this.getValue("IN_COUNT")); // 住院次数
			adm.setData("PG_OWNER", Operator.getID()); // 首页建立者
			adm.setData("STATUS", "0"); // 状态 0 在院；1 出院未完成；2 出院已完成
			adm.setData("CASE_NO", caseNo);
			adm.setData("ADM_SOURCE", this.getValue("ADM_SOURCE")); // 病患来源
			adm.setData("AGN_CODE", this.getValue("AGN_CODE") == null ? "" : this.getValue("AGN_CODE")); // 31天再住院
			adm.setData("AGN_INTENTION", this.getValue("AGN_INTENTION")); // 31天再住院原因
			// System.out.println("-=-------------------" + adm);
			result = MROTool.getInstance().updateADMData(adm);
			if (result.getErrCode() < 0) {
				this.messageBox_(result.getErrText());
			}
		}

		// 修改病案 患者基本信息
		TParm opt = new TParm();
		opt.setData("MR_NO", mr_no);
		opt.setData("CASE_NO", caseNo);
		opt.setData("OPT_USER", user_id);
		opt.setData("OPT_TERM", user_ip);
		result = MROTool.getInstance().updateMROPatInfo(opt);
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
		}
	}

	/**
	 * 根据床位code
	 *
	 * @param Bed_Code
	 *            String
	 * @return String
	 */
	private String getBedDesc(String Bed_Code) {
		this.setValue("BED_DESC", Bed_Code);
		TComboBox combo = (TComboBox) this.getComponent("BED_DESC");
		return combo.getSelectedName();
	}

	/**
	 * 打印住院证
	 */
	public void onPrint() {
		if (StringUtils.isEmpty(caseNo)) {
			this.messageBox("请先保存");
			return;
		}
		this.print();
	}
	
	/**
	 * 打印
	 */
	private void print() {
		String subClassCode = TConfig.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
		String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
		String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO='" + caseNo + "'";
		sql += " AND CLASS_CODE='" + classCode + "' AND  SUBCLASS_CODE='" + subClassCode + "'";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
		if (result1.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		String path,fileName, seq;
		boolean flg;// “病历是否存在”标识
		if (result1.getCount() < 0) {
			path = TConfig.getSystemValue("ADMEmrINHOSPPATH");
			fileName = TConfig.getSystemValue("ADMEmrINHOSPFILENAME");
			seq = "";
			flg = false;
		}else {
			path = result1.getValue("FILE_PATH", 0);
			fileName = result1.getValue("FILE_NAME", 0);
			seq = result1.getValue("FILE_SEQ", 0);
			flg = true;
		}
		//
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		// 查询在院病患的基本信息
		TParm casePrint = ADMInpTool.getInstance().queryCaseNo(parm);
		//
		TParm actionParm = new TParm();
		actionParm.setData("MR_NO", pat.getMrNo());
		actionParm.setData("IPD_NO", pat.getIpdNo());
		actionParm.setData("PAT_NAME", pat.getName());
		actionParm.setData("SEX", pat.getSexString());
		actionParm.setData("AGE", DateUtil.showAge(pat.getBirthday(), sysDate)); // 年龄
		Timestamp ts = SystemTool.getInstance().getDate();
		actionParm.setData("CASE_NO", caseNo);
		actionParm.setData("ADM_TYPE", "O");
		actionParm.setData("DEPT_CODE", casePrint.getValue("DEPT_CODE", 0));
		actionParm.setData("STATION_CODE", casePrint.getValue("STATION_CODE", 0));
		actionParm.setData("ADM_DATE", ts);// 打印日期
		actionParm.setData("STYLETYPE", "1");
		actionParm.setData("RULETYPE", "3");
		actionParm.setData("SYSTEM_TYPE", "ODO");
		//
		TParm emrFileData = new TParm();
		emrFileData.setData("TEMPLET_PATH", path);
		emrFileData.setData("EMT_FILENAME", fileName);
		emrFileData.setData("FILE_PATH",path)  ;
		emrFileData.setData("FILE_NAME",fileName) ;
		emrFileData.setData("SUBCLASS_CODE", subClassCode);
		emrFileData.setData("CLASS_CODE", classCode);
		emrFileData.setData("FILE_SEQ", seq);
		emrFileData.setData("FLG", flg);
		//
		actionParm.setData("EMR_FILE_DATA", emrFileData);
		//
		sql = "SELECT A.IN_CASE_NO, A.RESV_NO FROM ADM_RESV A WHERE A.IN_CASE_NO = '"+caseNo+"'";
		result1 = new TParm(TJDODBTool.getInstance().select(sql));
		if (result1.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		actionParm.setData("RESV_NO", result1.getValue("RESV_NO", 0));// 预约号
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", actionParm);
	}

	/**
	 * 向对应的护士站发送消息
	 *
	 * @param MR_NO
	 *            String 病案号
	 * @param CASE_NO
	 *            String 就诊序号
	 * @param PAT_NAME
	 *            String 患者姓名
	 */
	public void sendInwStationMessages(String MR_NO, String CASE_NO, String PAT_NAME) {

		// $$ ============ Modified by lx 医生按部门,护士按病区收发消息2012/02/27
		// START==================$$//
		// SocketLink client1 = SocketLink.running("", "ODISTATION", "odi");
		SocketLink client1 = SocketLink.running("", Operator.getDept(), Operator.getDept());
		if (client1.isClose()) {
			out(client1.getErrText());
			return;
		}
		/**
		 * client1.sendMessage("INWSTATION", "CASE_NO:" + CASE_NO + "|STATION_CODE:" +
		 * Operator.getStation() + "|MR_NO:" + MR_NO + "|PAT_NAME:" + PAT_NAME);
		 **/
		client1.sendMessage(Operator.getStation(), "CASE_NO:" + CASE_NO + "|STATION_CODE:" + Operator.getStation()
				+ "|MR_NO:" + MR_NO + "|PAT_NAME:" + PAT_NAME);

		if (client1 == null)
			return;
		client1.close();

		// $$ ============ Modified by lx 医生按部门,护士按病区收发消息2012/02/27
		// END==================$$//

	}

	/**
	 * 模糊查询（内部类） 诊断中文替换
	 */
	public class OrderList extends TLabel {
		TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");

		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER : dataStore.PRIMARY;
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
	 * 新生儿免疫
	 */
	public void onImmunity() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", MR_NO);
		parm.setData("IPD_NO", IPD_NO);
		this.openDialog("%ROOT%\\config\\adm\\ADMChildImmunity.x", parm);

	}

	/**
	 * 检查病患基本信息是否可以提交
	 *
	 * @return boolean
	 */
	private boolean checkPatInfo() {
		// emr结构化病历需要给别字段必填
		if (this.getValueString("TEL_HOME").length() <= 0) {
			this.messageBox_("请填写家庭电话！");
			this.grabFocus("TEL_HOME");
			return false;
		}
		if (this.getValueString("RESID_ADDRESS").length() <= 0) {
			this.messageBox_("请填写户口地址！");
			this.grabFocus("RESID_ADDRESS");
			return false;
		}
		return true;
	}

	/**
	 * 查询病人的住院次数
	 * 
	 * @param mrNo
	 *            String
	 * @return int
	 */
	private int getInCount(String mrNo) {
		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		parm.setData("CANCEL_FLG", "N");
		TParm result = ADMInpTool.getInstance().selectall(parm); // 查询该病人的所有未取消的住院信息
		if (result.getErrCode() < 0) {
			return 1;
		}
		return result.getCount();
	}

	/**
	 * 腕带打印
	 */
	public void onWrist() {
		if (this.getValueString("MR_NO").length() == 0 || pat == null) {
			return;
		}
		TParm print = new TParm();
		// =============================================================
		print.setData("MR_NO", pat.getMrNo());
		print.setData("CASE_NO", caseNo);

		this.openDialog("%ROOT%\\config\\adm\\ADMWristPrint.x", print);
		// =============================================================

		/*
		 * print.setData("PatName", "TEXT", "姓名:"+pat.getName()); //
		 * print.setData("Sex", "TEXT", pat.getSexString()); //
		 * print.setData("BirthDay", "TEXT", // StringTool.getString(pat.getBirthday(),
		 * "yyyy/MM/dd")); TParm admParm = new TParm(); admParm.setData("CASE_NO",
		 * caseNo); TParm admInfo = ADMTool.getInstance().getADM_INFO(admParm);
		 * Timestamp sysDate = SystemTool.getInstance().getDate(); String age =
		 * DateUtil.showAge(pat.getBirthday(), sysDate);
		 * 
		 * 
		 * TParm newBornParm=new TParm(TJDODBTool.getInstance().
		 * select("select new_born_flg from adm_inp where case_no='"+caseNo+"'"));
		 * 
		 * TParm childParm=new
		 * TParm(TJDODBTool.getInstance().select("select ch_age from opd_sysparm"));
		 * if("Y".equals(newBornParm.getValue("NEW_BORN_FLG",0))){//新生儿
		 * print.setData("Barcode", "TEXT", pat.getMrNo()); print.setData("Birth",
		 * "TEXT", "出生日期:"+StringTool.getString(pat.getBirthday(), "yyyy/MM/dd HH:mm"));
		 * print.setData("Weight","TEXT","体重:"+pat.getNewBodyWeight()+"g"); }else{
		 * print.setData("Barcode1", "TEXT", pat.getMrNo()); print.setData("Age",
		 * "TEXT", "年龄:"+age);//wanglong add 20150204 print.setData("Dept", "TEXT",
		 * "科室:"+StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC", "DEPT_CODE='" +
		 * admInfo.getValue("DEPT_CODE", 0) + "'"));//wanglong add 20150204 }
		 * print.setData("BedNO","TEXT","床号:"); if
		 * (Integer.parseInt(DateUtil.CountAgeByTimestamp(pat.getBirthday(),
		 * sysDate)[0]) > Integer .parseInt(childParm.getValue("CH_AGE", 0)) &&
		 * "N".equals(newBornParm.getValue("NEW_BORN_FLG", 0))) {
		 * this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMWristAdult.jhw", print); }
		 * else { this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMWrist.jhw", print);
		 * }
		 */
	}

	/**
	 * 生成xml文件：住院登记xml =======================pangben modify 20110812
	 */
	public void djXML() {
		// 1.构造数据
		if (this.getValueString("MR_NO").length() <= 0) {
			return;
		}
		TParm inparm = new TParm();
		inparm.insertData("TBR", 0, this.getValue("MR_NO")); // 病案号
		inparm.insertData("XM", 0, this.getValue("PAT_NAME")); // 姓名
		inparm.insertData("XB", 0, this.getValue("SEX_CODE")); // 性别
		inparm.insertData("CSNY", 0, StringTool.getString((Timestamp) this.getValue("BIRTH_DATE"), "yyyyMMdd")); // 出生年月
		inparm.insertData("SFZH", 0, this.getValue("IDNO")); // 身份证号
		inparm.insertData("YRXZ", 0, "在职"); // 人员性质
		inparm.insertData("XH", 0, caseNo); // 入院序号
		inparm.insertData("RYSJ", 0, StringTool.getString((Timestamp) this.getValue("IN_DATE"), "yyyyMMddHHmmss")); // 入院时间
		inparm.insertData("LXDH", 0, this.getValue("CONTACTS_TEL")); // 联系电话
		inparm.insertData("KSM", 0, this.getValue("DEPT_CODE")); // 科室码
		inparm.insertData("ZYH", 0, this.getValue("IPD_NO")); // 住院号
		TTextFormat format = (TTextFormat) this.getComponent("STATION_CODE");
		inparm.insertData("BQMC", 0, format.getText()); // 病区名称
		inparm.insertData("CWH", 0, this.getValue("BED_NO")); // 床位号
		inparm.insertData("ZHYE", 0, "0"); // 个人账户余额
		inparm.insertData("YSM", 0, this.getValue("VS_DR_CODE")); // 收治医生码
		inparm.insertData("XZMC", 0, "0"); // 险种
		inparm.addData("SYSTEM", "COLUMNS", "TBR");
		inparm.addData("SYSTEM", "COLUMNS", "XM");
		inparm.addData("SYSTEM", "COLUMNS", "XB");
		inparm.addData("SYSTEM", "COLUMNS", "CSNY");
		inparm.addData("SYSTEM", "COLUMNS", "SFZH");
		inparm.addData("SYSTEM", "COLUMNS", "YRXZ");
		inparm.addData("SYSTEM", "COLUMNS", "XH");
		inparm.addData("SYSTEM", "COLUMNS", "RYSJ");
		inparm.addData("SYSTEM", "COLUMNS", "LXDH");
		inparm.addData("SYSTEM", "COLUMNS", "KSM");
		inparm.addData("SYSTEM", "COLUMNS", "ZYH");
		inparm.addData("SYSTEM", "COLUMNS", "BQMC");
		inparm.addData("SYSTEM", "COLUMNS", "CWH");
		inparm.addData("SYSTEM", "COLUMNS", "ZHYE");
		inparm.addData("SYSTEM", "COLUMNS", "YSM");
		inparm.addData("SYSTEM", "COLUMNS", "XZMC");
		inparm.setCount(1);
		// System.out.println("=======inparm=============" + inparm);
		// 2.生成文件
		NJCityInwDriver.createXMLFile(inparm, "c:/NGYB/zydjxx.xml");
		this.messageBox("生成成功");
	}

	/**
	 * 获得数据:记录医保病人住院序号
	 */
	public void readDjXML() {
		TParm parm = NJCityInwDriver.getPame("c:/NGYB/mzghxx.xml");
		if (null == parm)
			return;
		this.setValue("IPD_NO", parm.getValue("ZYH").substring(1, parm.getValue("ZYH").indexOf("]")));
	}

	/**
	 * 血糖Hl7接口
	 */
	public void sendHl7message() {
		TParm parm = new TParm();
		String type = "ADM_IN";
		List list = new ArrayList();
		parm.setData("ADM_TYPE", "I");
		parm.setData("CASE_NO", this.caseNo);
		parm.setData("IPD_NO", this.IPD_NO);
		list.add(parm);
		// 调用接口
		TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
		if (resultParm.getErrCode() < 0)
			this.messageBox(resultParm.getErrText());
	}

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 更新住院登记表中的单病种信息
	 */
	public void updateADMInpSDInfo() {// add by wanglong 20121025
		TParm action = new TParm();
		action.setData("CASE_NO", caseNo);
		action.setData("DISE_CODE", this.getValue("DISE_CODE") + "");
		TParm result = CLPSingleDiseTool.getInstance().updateADMInpSDInfo(action);
		if (result.getErrCode() < 0) {
			messageBox("单病种信息保存失败");
			return;
		}
	}

	// ================= chenxi add 20130319 读身份证卡信息
	public void onIdCardNo() {
		// String dir = "C:/Program Files/Routon/身份证核验机具阅读软件" ;
		String dir = SystemTool.getInstance().Getdir();
		CardInfoBO cardInfo = null;
		try {
			cardInfo = IdCardReaderUtil.getCardInfo(dir);
		} catch (Exception e) {
			this.messageBox("重新获取信息");
			System.out.println("重新获取信息:" + e.getMessage());
			// TODO: handle exception
		}
		// CardInfoBO cardInfo = IdCardReaderUtil.getCardInfo(dir);
		if (cardInfo == null) {
			this.messageBox("未获得身份证信息,请重新操作");
			return;
		}
		// 通过身份证号查询病患信息
		TParm parm = new TParm();
		parm.setData("IDNO", cardInfo.getCode().trim());// 身份证号
		TParm infoParm = PatTool.getInstance().getInfoForIdNo(parm);
		if (infoParm.getCount() > 0) {
			// this.messageBox("已存在此就诊病患信息");
		} else {
			String sql = "SELECT MR_NO,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS FROM SYS_PATINFO WHERE PAT_NAME LIKE '"
					+ cardInfo.getName() + "%'";
			infoParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (infoParm.getCount() <= 0) {
				this.messageBox("不存在此就诊病患信息");
			}
		}
		if (infoParm.getCount() > 0) {// 多行数据显示===pangben 2013-8-6
			if (infoParm.getCount() == 1) {// 只存在一条数据
				if (!checkPatInfo(infoParm.getValue("MR_NO", 0))) {
					return;
				}
				this.setValue("MR_NO", infoParm.getValue("MR_NO", 0));
			} else {
				Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", // 获得唯一的病案号
						infoParm);
				TParm patParm = new TParm();
				if (obj != null) {
					patParm = (TParm) obj;
					if (!checkPatInfo(patParm.getValue("MR_NO"))) {
						return;
					}
					this.setValue("MR_NO", patParm.getValue("MR_NO"));
				} else {
					return;
				}
			}
			this.sendpic(cardInfo.getImagesPath().get(2));
			onMrno();
			setValue("RESID_ADDRESS", cardInfo.getAdd()); // 户口地址
			setValue("SPECIES_CODE", onGetSPECIES_CODE(cardInfo.getFolk() + "族")); // 名族
		} else {
			this.onClear();// 没有查询到数据将界面数据清空
			setValue("PAT_NAME", cardInfo.getName()); // 姓名
			setValue("IDNO", cardInfo.getCode()); // 身份证号
			setValue("SEX_CODE", cardInfo.getSex().equals("男") ? "1" : "2"); // 性别
			setValue("BIRTH_DATE", StringTool.getTimestamp(cardInfo.getBirth(), "yyyyMMdd")); // 生日
			setValue("RESID_ADDRESS", cardInfo.getAdd()); // 户口地址
			setValue("SPECIES_CODE", onGetSPECIES_CODE(cardInfo.getFolk() + "族")); // 名族
			// setBirth(pat.getBirthday()); // 计算年龄
			setValue(cardInfo.getBirth(), sysDate);
		}
		// IdCardO.getInstance().delFolder(dir) ;
	}

	/**
	 * 校验病患信息
	 * 
	 * @return ===========pangben 2013-8-6 二代身份证校验
	 */
	private boolean checkPatInfo(String mrNo) {
		if (this.getValue("MR_NO").toString().length() > 0 && null != pat) {
			if (!this.getValue("MR_NO").equals(mrNo)) {
				if (this.messageBox("提示", "身份证信息与当前就诊病患信息不符,是否继续", 2) != 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 传送照片
	 *
	 * @param image
	 *            Image
	 */
	public void sendpic(String localFileName) {
		String dir = "";
		String mrNo = getValue("MR_NO").toString();
		String photoName = mrNo + ".jpg";
		File file = new File(localFileName);
		try {
			// String root = TIOM_FileServer.getRoot();
			dir = TIOM_FileServer.getPath("PatInfPIC.LocalPath");
			// dir = dir + mrNo.substring(0, 3) + "\\"
			// + mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";
			File filepath = new File(dir);
			if (!filepath.exists())
				filepath.mkdirs();
			BufferedImage input = ImageIO.read(file);
			Image scaledImage = input.getScaledInstance(300, 400, Image.SCALE_DEFAULT);
			BufferedImage output = new BufferedImage(300, 400, BufferedImage.TYPE_INT_BGR);
			output.createGraphics().drawImage(scaledImage, 0, 0, null); // 画图
			ImageIO.write(output, "jpg", new File(dir + photoName));
			sendpic(scaledImage);
		} catch (Exception e) {
		}
	}

	/**
	 * 取名族code
	 */
	public String onGetSPECIES_CODE(String name) {
		String code = "";
		String sql = "SELECT ID FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SPECIES' " + "  AND CHN_DESC = '" + name
				+ "'";
		// System.out.println("name======"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() <= 0)
			return code;
		code = parm.getValue("ID", 0);
		return code;

	}
	// ================= chenxi add 20130319 读身份证卡信息

	/**
	 * 获取购买月龄
	 */
	public int getBuyMonth(String s, String s1) {
		Date m = new Date();
		Date d = null;
		Date d1 = null;
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		try {
			d = df.parse(s);
			d1 = df.parse(s1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		c.setTime(d1);
		int year1 = c.get(Calendar.YEAR);
		int month1 = c.get(Calendar.MONTH);
		int result;
		if (year == year1) {
			result = month1 - month;// 两个日期相差几个月，即月份差
		} else {
			result = 12 * (year1 - year) + month1 - month;// 两个日期相差几个月，即月份差
		}
		return result;
	}

	/**
	 * 回车光标跳转 add by huangjw 20140801
	 */
	public void onUnitCode() {
		((TComboBox) getComponent("SEX_CODE")).grabFocus();
	}

	/**
	 * 将住院数据插入临时表MRO_REG
	 * 
	 * @author wangbin
	 */
	public void insertMroReg() {
		TParm queryParm = new TParm();
		queryParm.setData("BOOK_ID", reParm.getValue("RESV_NO"));
		// 插入前根据当前预约号判断临时表中是否已经存在该数据,如果不存在则插入
		TParm mroRegInfoParm = MROBorrowTool.getInstance().queryMroRegAppointment(queryParm);

		if (mroRegInfoParm.getCount() <= 0) {
			TParm parm = new TParm();
			parm.setData("MRO_REGNO", SystemTool.getInstance().getNo("ALL", "MRO", "MRO_REGNO", "MRO_REGNO"));
			parm.setData("SEQ", 1);
			// 预约号
			parm.setData("BOOK_ID", reParm.getValue("RESV_NO"));
			// 0_预约挂号(APP), 1_现场挂号(LOC), 2_住院登记
			parm.setData("ORIGIN_TYPE", "2");
			// 门急住标识
			parm.setData("ADM_TYPE", "I");
			// 病案号
			parm.setData("MR_NO", this.getValueString("MR_NO"));
			// 就诊号
			parm.setData("CASE_NO", caseNo);
			// 入院日期
			parm.setData("ADM_DATE", this.getValueString("IN_DATE").substring(0, 10).replace("-", "").replace("/", ""));
			// 病区
			parm.setData("ADM_AREA_CODE", this.getValue("STATION_CODE"));
			// 时段
			parm.setData("SESSION_CODE", "");
			// 就诊序号
			parm.setData("QUE_NO", "");
			// 病患姓名
			parm.setData("PAT_NAME", this.getValueString("PAT_NAME"));
			// 性别
			parm.setData("SEX_CODE", this.getValueString("SEX_CODE"));
			// 出生年月
			parm.setData("BIRTH_DATE", this.getValueString("BIRTH_DATE").substring(0, 10));
			// 电话号码
			parm.setData("CELL_PHONE", this.getValueString("CELL_PHONE"));
			// 住院科室
			parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
			// 主治医生
			parm.setData("DR_CODE", this.getValueString("ATTEND_DR_CODE"));

			if (StringUtils.isEmpty(this.getValueString("ATTEND_DR_CODE"))) {
				TParm admResvParm = new TParm();
				admResvParm.setData("MR_NO", this.getValueString("MR_NO"));
				admResvParm.setData("RESV_NO", reParm.getValue("RESV_NO"));

				// 查询病患的预约信息用来获取预约主治医生
				TParm resv = ADMResvTool.getInstance().selectallByPatInfo(admResvParm);
				if (resv.getCount() > 0) {
					parm.setData("DR_CODE", resv.getValue("DR_CODE", 0));
				}
			}

			// 待出库确认状态
			parm.setData("CONFIRM_STATUS", "0");
			// 取消注记(Y_取消,N_未取消)
			parm.setData("CANCEL_FLG", "N");
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());

			TParm mroRegResult = MROBorrowTool.getInstance().insertMroRegByLoc(parm);

			if (mroRegResult.getErrCode() < 0) {
				this.messageBox("数据插入临时表失败");
				err(mroRegResult.getErrCode() + " " + mroRegResult.getErrText());
			}
		}
	}

	/**
	 * 病历待出库取消
	 * 
	 * @param caseNo
	 *            就诊号
	 * @author wangbin 2014/09/05
	 */
	private void cancelMroRegAppointment(String caseNo) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());

		TParm result = MROBorrowTool.getInstance().cancelMroRegAppointment(parm, null);

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
		}
	}

	/**
	 * 针对已出库后再取消住院的数据进行归还日期设定
	 * 
	 * @param caseNo
	 *            就诊号
	 * @author wangbin 2014/10/17
	 */
	private void updateRtnDateByCaseNo(String caseNo) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);

		TParm result = MROQueueTool.getInstance().updateRtnDateByCaseNo(parm);

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
		}
	}

	/**
	 * 
	 * @Title: onLumpworkCtzCode @Description: TODO(获得套餐绑定身份) @author pangben
	 *         2015-6-18 @throws
	 */
	public void onLumpworkCtzCode() {
		String lumpWork = this.getValueString("LUMPWORK_CODE");
		if (lumpWork.length() > 0) {
			TParm parm = new TParm();
			parm.setData("LUMPWORK_CODE", lumpWork);
			String sql = "SELECT B.PACKAGE_CODE, C.PACKAGE_DESC, A.AR_AMT,C.CTZ_CODE,A.TRADE_NO "
					+ " FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B, MEM_PACKAGE C "
					+ " WHERE A.TRADE_NO = B.TRADE_NO " + " AND B.PACKAGE_CODE = C.PACKAGE_CODE "
					+ " AND B.PACKAGE_CODE='" + lumpWork + "' AND A.MR_NO='" + MR_NO
					+ "' AND   C.PARENT_PACKAGE_CODE IS NOT NULL AND "
					+ "B.REST_TRADE_NO IS NULL AND C.ADM_TYPE='I'  "
					//
					+ " AND A.USED_FLG = '0' "// 未使用
					//
					+ " GROUP BY B.PACKAGE_CODE, C.PACKAGE_DESC, A.AR_AMT,C.CTZ_CODE,A.TRADE_NO";
			System.out.println("sql111:::::"+sql);
			TParm data = new TParm(TJDODBTool.getInstance().select(sql));// 获取套餐身份信息--xiongwg20150702
			if (data.getCount() <= 0) {
				this.messageBox("套餐数据有问题");
				return;
			}
			// if (data.getValue("CTZ_CODE",0).length()<=0) {
			// this.messageBox("没有绑定套餐身份,不可以操作");
			// this.setValue("LUMPWORK_CTZ_CODE", "");
			// this.setValue("LUMPWORK_CODE", "");
			// clearValue("AR_AMT_T");
			// return;
			// }
			// 添加校验身份启用状态
			// sql="SELECT CTZ_CODE,CTZ_DESC,USE_FLG FROM SYS_CTZ WHERE
			// CTZ_CODE='"+data.getValue("CTZ_CODE",0)+"'";
			// TParm ctzParm = new TParm(TJDODBTool.getInstance().select(sql));
			// if
			// (null==ctzParm.getValue("USE_FLG",0)||ctzParm.getValue("USE_FLG",0).length()<=0
			// ||ctzParm.getValue("USE_FLG",0).equals("N")) {
			// this.messageBox("此套餐身份未启用,不可以操作");
			// this.setValue("LUMPWORK_CTZ_CODE", "");
			// this.setValue("LUMPWORK_CODE", "");
			// clearValue("AR_AMT_T");
			// return;
			// }
			// this.setValue("LUMPWORK_CTZ_CODE", data.getValue("CTZ_CODE",0));
			this.setValue("AR_AMT_T", data.getValue("AR_AMT", 0));
		} else {
			// this.setValue("LUMPWORK_CTZ_CODE", "");
			clearValue("AR_AMT_T");
		}
	}

	/**
	 * 校验保险信息是否有效
	 */
	public boolean onCheckInsure() {
		TParm checkParm = new TParm(TJDODBTool.getInstance()
				.select("SELECT VALID_FLG FROM MEM_INSURE_INFO WHERE MR_NO= '" + this.getValueString("MR_NO")
						+ "' AND CONTRACTOR_CODE = '" + this.getValue("INSURE_INFO_1") + "' "));
		if (checkParm.getCount() <= 0) {
			this.messageBox("该病患没有购买此保险");
			return false;
		}
		if (!checkParm.getValue("VALID_FLG", 0).equals("Y")) {
			this.messageBox("该保险无效，请重新选择");
			return false;
		}
		return true;
	}

	/**
	 * 
	 */
	public void checkInsure() {
		if (this.getValue("INSURE_INFO_1") == null || this.getValue("INSURE_INFO_1").toString().length() <= 0) {
			return;
		}
		String sql = "SELECT VALID_FLG FROM MEM_INSURE_INFO " + " WHERE MR_NO = '" + this.getValueString("MR_NO")
				+ "' AND CONTRACTOR_CODE = '" + this.getValue("INSURE_INFO_1") + "'";
		// System.out.println(""+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (!"Y".equals(parm.getValue("VALID_FLG", 0))) {
			this.messageBox("该保险信息无效，请重新选择");
			this.setValue("INSURE_INFO_1", "");
			return;
		}

		sql = "SELECT CONTRACTOR_CODE FROM MEM_INSURE_INFO " + " WHERE MR_NO = '" + pat.getMrNo()
				+ "' AND VALID_FLG = 'Y'"
				+ " AND START_DATE <= TRUNC (SYSDATE, 'dd') AND END_DATE >= TRUNC (SYSDATE, 'dd')";
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() < 0) {
			if (JOptionPane.showConfirmDialog(null, "该病患保险不在有效期内，是否继续", "信息", JOptionPane.YES_NO_OPTION) == 0) {

			} else {
				this.setValue("INSURE_INFO_1", "");
			}
		}
	}

	/**
	 * 校验就诊身份1 add by huangjw 20150923
	 */
	public void checkCtz() {
		if (this.getValue("PAT_CTZ_CODE") == null || this.getValue("PAT_CTZ_CODE").toString().length() <= 0) {
			return;
		}
		// 添加校验身份启用状态
		String sql = "SELECT CTZ_CODE,CTZ_DESC,USE_FLG FROM SYS_CTZ WHERE CTZ_CODE='" + this.getValue("PAT_CTZ_CODE")
				+ "'";
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (null == ctzParm.getValue("USE_FLG", 0) || ctzParm.getValue("USE_FLG", 0).length() <= 0
				|| ctzParm.getValue("USE_FLG", 0).equals("N")) {
			this.messageBox("此身份未启用");
			this.setValue("PAT_CTZ_CODE", "");
			this.grabFocus("PAT_CTZ_CODE");
			return;
		}
		sql = "SELECT CTZ_CODE,CTZ_DESC,SERVICE_LEVEL FROM SYS_CTZ WHERE CTZ_CODE = '" + this.getValue("PAT_CTZ_CODE")
				+ "' AND START_DATE <= TRUNC (SYSDATE, 'dd') AND END_DATE >= TRUNC (SYSDATE, 'dd')";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() < 0) {
			TComboBox tcb = (TComboBox) this.getComponent("PAT_CTZ_CODE");
			if (JOptionPane.showConfirmDialog(null, "就诊身份1 " + tcb.getSelectedName() + " 不在有效期内，是否继续", "信息",
					JOptionPane.YES_NO_OPTION) == 0) {
			} else {
				this.setValue("PAT_CTZ_CODE", "");
				this.grabFocus("PAT_CTZ_CODE");
			}
		}
		// pangben 2016-4-26 服务等级默认修改，根据第一身份获得服务等级
		this.setValue("SERVICE_LEVEL", parm.getValue("SERVICE_LEVEL", 0)); // 服务等级
	}

}
