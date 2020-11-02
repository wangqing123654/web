package com.javahis.ui.ins;

import org.hibernate.sql.Template;

import jdo.ins.INSTJTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
/**
 * 
 * <p>
 * Title:סԺҽ���ʸ�ȷ������ʷ��¼��ѯ
 * </p>
 * 
 * <p>
 * Description:סԺҽ���ʸ�ȷ������ʷ��¼��ѯ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author pangb 2012-8-14
 * @version 4.0
 */
public class INSSearchConfirmControl extends TControl{

	private TParm regionParm;
	private TTable table;
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// ���ҽ���������
		table=(TTable) this.getComponent("TABLE");
		this.setValue("CROWD_TYPE", "1");
	}
	public void onQuery(){
		TParm rsParm=null;
		TParm parm=new TParm();
		parm.setData("CONFIRM_NO",this.getValue("CONFIRM_NO"));
		parm.setData("NHI_REGION_CODE",regionParm.getValue("NHI_NO", 0));// ҽ���������
		parm.setData("IDNO",this.getValue("IDNO"));
		if (this.getValueInt("CROWD_TYPE") == 1) {// 1 ��ְ 2.�Ǿ�
			rsParm = INSTJTool.getInstance().DataDown_rs_B(parm, "");

		} else if (this.getValueInt("CROWD_TYPE") == 2) {// 1 ��ְ 2.�Ǿ�
			rsParm = INSTJTool.getInstance().DataDown_czyd_B(parm, "");
		}
		if (!INSTJTool.getInstance().getErrParm(rsParm)) {
			this.messageBox(rsParm.getErrText());
			return;
		}
		table.setParmValue(rsParm);
	}
	/**
	 * ��ѯ��������/���֤����
	 */
	public void onMrNo(){
		Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
        if (pat == null) {
            this.messageBox("�޴˲�����!");
            return;
        }
        this.setValue("MR_NO",pat.getMrNo());
        this.setValue("IDNO",pat.getIdNo());
        this.setValue("PAT_NAME",pat.getName());
	}
	public void onClear(){
		this.setValue("MR_NO", "");
		this.setValue("IDNO", "");
		this.setValue("PAT_NAME", "");
		this.setValue("CONFIRM_NO", "");
		table.removeRowAll();
		this.setValue("CROWD_TYPE", "1");
	}
}
