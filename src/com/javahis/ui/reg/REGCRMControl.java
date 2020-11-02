package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import jdo.sys.CRMInfo;
import jdo.sys.MediumMember;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.alibaba.fastjson.JSON;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;

public class REGCRMControl extends TControl {

	private TTable tablePat;
	private static TCheckBox all;
	private TParm parmPro; // 病患属性
	private boolean patNew = false; // 病患是否为新增的
	private String pro="";// 病患插入的属性
	private String value="";// 属性的值

	public void onInit() {
		super.onInit();
		// Object obj = this.getParameter();
		// if (obj instanceof TParm) {
		// TParm parm = (TParm) obj;
		// if(!parm.getValue("MR_NO", 0).equals("")){
		// String mrNo=PatTool.getInstance().checkMrno(TypeTool.getString(
		// parm.getValue("MR_NO", 0)));
		// this.setValue("MR_NO",mrNo);
		// }
		//	    		 
		// }

		tablePat = (TTable) this.getComponent("TABLEPAT");
		this.callFunction("UI|TABLEPAT|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this, "onTablePatClicked");
		all = (TCheckBox) getComponent("ALL");
		parmPro = new TParm(TJDODBTool.getInstance().select(
				"SELECT PRO_CODE,PRO_NAME FROM REG_PAT_PRO "));
	}

	public void onQuery() {

		TParm parmC = new TParm();
		parmC.addData("orderNum", this.getValueString("orderNum"));
		TParm parmCRM = TIOM_AppServer.executeAction("action.reg.REGCRMAction",
				"findMember", parmC);
		System.out.println("parmCRM==;;===" + parmCRM);
		if(parmCRM.getCount()<0){
			this.messageBox("没有要查询的数据，请重新输入预约号");
			return;
		}
		
		this.setValue("ID", parmCRM.getValue("ID", 0));
		TParm pat = new TParm();
		for (int i = 0; i < parmPro.getCount(); i++) {
			if (!parmCRM.getValue(parmPro.getValue("PRO_CODE", i), 0)
					.equals("")) {
				
				if (parmPro.getValue("PRO_CODE", i).equals("BIRTH_DATE")) {
					pro = pro + parmPro.getValue("PRO_CODE", i) + ",";
					String birthday = parmCRM.getValue(parmPro.getValue(
							"PRO_CODE", i), 0);
					value = value + "TO_DATE('"
							+ birthday.replace("-", "").substring(0, 8)
							+ "','YYYYMMDD'),";
				} else {
					pro = pro + parmPro.getValue("PRO_CODE", i) + ",";
					value = value
							+ "'"
							+ parmCRM.getValue(parmPro.getValue("PRO_CODE", i),
									0) + "',";
				}
				
				pat.addData("NAME", parmPro.getValue("PRO_NAME", i));
				if (parmPro.getValue("PRO_CODE", i).equals("ID_TYPE")) {
					String sql = "SELECT CHN_DESC  FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_IDTYPE' AND ID='"+parmCRM.getValue(parmPro.getValue(
							"PRO_CODE", i), 0)+"'";
					TParm parmSex = new TParm(TJDODBTool.getInstance().select(sql));
					if(parmSex.getCount()>0){
						pat.addData("CRM", parmSex.getValue("CHN_DESC", 0));
					}else{
						pat.addData("CRM", parmCRM.getValue(parmPro.getValue(
								"PRO_CODE", i), 0));
					}
					
					
				}else if (parmPro.getValue("PRO_CODE", i).equals("SEX_CODE")) {
					String sql = "SELECT CHN_DESC SEX_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX' AND ID='"+parmCRM.getValue(parmPro.getValue(
							"PRO_CODE", i), 0)+"'";
					TParm parmSex = new TParm(TJDODBTool.getInstance().select(sql));
					if(parmSex.getCount()>0){
						pat.addData("CRM", parmSex.getValue("SEX_DESC", 0));
					}else{
						pat.addData("CRM", parmCRM.getValue(parmPro.getValue(
								"PRO_CODE", i), 0));
					}
					
					
				}else{
					pat.addData("CRM", parmCRM.getValue(parmPro.getValue(
							"PRO_CODE", i), 0));
				}
			}

		}
		tablePat.setParmValue(pat);

	}

	public void onTablePatClicked(Object obj) {
		tablePat = (TTable) obj;
		int row = tablePat.getSelectedRow();
		tablePat.setItem(row, "HIS", tablePat.getItemData(row, "CRM"));
	}

	public void onSelectAll() {
		tablePat.acceptText();
		String flg = "N";
		if (all.isSelected()) {
			flg = "Y";
		} else {
			flg = "N";
		}
		TParm parmPat = tablePat.getParmValue();
		for (int i = 0; i < parmPat.getCount("FLG"); i++) {
			if (flg.equals("Y")) {
				if (!parmPat.getData("CRM", i).equals("")) {
					parmPat.setData("FLG", i, flg);
					parmPat.setData("HIS", i, parmPat.getData("CRM", i));
				}
			} else {
				parmPat.setData("FLG", i, flg);
			}

		}
		tablePat.setParmValue(parmPat);

	}

	public void onSave() {
		if(pro.equals("")){
			return;
		}
		String sql = "";
		String pro1 = pro + "MR_NO,OPT_USER,OPT_DATE,OPT_TERM";
		String newMrNo = SystemTool.getInstance().getMrNo();
		if (newMrNo == null || newMrNo.length() == 0) {
			err("-1 取病案号错误!");
			return;
		}
		this.setValue("MR_NO", newMrNo);
		Timestamp date = SystemTool.getInstance().getDate();
		value = value + "'" + newMrNo + "','" + Operator.getID()
				+ "',SYSDATE,'" + Operator.getIP() + "'";
		sql = "INSERT INTO SYS_PATINFO (" + pro1 + ") VALUES(" + value + ")";
		System.out.println("sql==" + sql);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("病患插入失败！");
			return;
		}
		Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		TParm updateParm = new TParm();
//		updateParm.setData("pat", pat.getParm());
		updateParm.setData("orderNum", this.getValueString("ID"));
		updateParm.setData("MR_NO", pat.getMrNo());
		updateParm.setData("PAT_NAME", pat.getName());
		updateParm.setData("SEX_CODE", pat.getSexCode());
		updateParm.setData("BIRTH_DATE", pat.getBirthday());
		updateParm.setData("CELL_PHONE", pat.getCellPhone());
		updateParm.setData("ID_TYPE", pat.getIdType());
		updateParm.setData("IDNO", pat.getIdNo());
		updateParm.setData("E_MAIL", pat.getEmail());
		
		System.out.println("updateParm=="+updateParm);
		result = TIOM_AppServer.executeAction("action.reg.REGCRMAction",
				"updateMember", updateParm);
		if (result.getBoolean("flg", 0)) {
			this.messageBox("CRM信息更新成功");
			this.onOk();
		} else {
			this.messageBox("CRM信息更新失败");
		}
		
	}

	public void onClear() {
		tablePat.removeRowAll();
		this.clearValue("MR_NO;orderNum;ID");
		patNew = false;
		all.setSelected(false);
		this.grabFocus("orderNum");

	}

	public void onOk() {

		String mrNo = this.getValueString("MR_NO");
		TParm retDate = new TParm();
		retDate.addData("MR_NO", mrNo);
		this.setReturnValue(retDate);
		this.closeWindow();
	}

	public void onC() {
		this.closeWindow();
	}

}
