package com.javahis.ui.mro;

import jdo.mro.MROQlayControlMTool;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.javahis.system.textFormat.TextFormatDept;
import com.javahis.system.textFormat.TextFormatSYSStation;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextFormat;

/**
 * <p>Title: �Զ��ʿز�ѯ</p>
 *
 * <p>Description: �Զ��ʿز�ѯ</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2011.6.24
 * @version 1.0
 */
public class MROQlayQueryControl
    extends TControl {

    private TTable table_m;

    private TTable table_d;

    private TTable table_dd;

    public MROQlayQueryControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        table_dd = getTable("TABLE_DD");
        this.setValue("REGION_CODE", Operator.getRegion());
        getTextFormat("START_DATE").setEnabled(false);
        getTextFormat("END_DATE").setEnabled(false);

        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/'));
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        if ("".equals(getValueString("REGION_CODE"))) {
            this.messageBox("��ѡ������");
            return;
        }
        TParm parm = new TParm();
        parm.setData("REGION_CODE", getValueString("REGION_CODE"));
        if (!"".equals(this.getValueString("DEPT_CODE"))) {
            parm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
        }
        if (!"".equals(this.getValueString("STATION_CODE"))) {
            parm.setData("STATION_CODE", getValueString("STATION_CODE"));
        }
        if (!"".equals(this.getValueString("VS_DR_CODE"))) {
            parm.setData("VS_DR_CODE", getValueString("VS_DR_CODE"));//modify by wanglong 20121127
        }
        if (!"".equals(this.getValueString("STATUS"))) {
            parm.setData("STATUS", getValueString("STATUS"));
        }
        if (!"".equals(this.getValueString("MR_NO"))) {
            parm.setData("MR_NO", getValueString("MR_NO"));
        }
        if (!"".equals(this.getValueString("IPD_NO"))) {
            parm.setData("IPD_NO", getValueString("IPD_NO"));
        }
        if (this.getRadioButton("TYPE_IN").isSelected()) {
            parm.setData("TYPE_IN", "TYPE_IN");
        }
        else {
            parm.setData("TYPE_OUT", "TYPE_OUT");
			Timestamp startDate = (Timestamp) this.getValue("START_DATE");//modify by wanglong 20121127
			Timestamp endDate = (Timestamp) this.getValue("END_DATE");
			endDate = new Timestamp(endDate.getTime() + 24 * 60 * 60 * 1000 - 1);
            parm.setData("START_DATE", startDate);//modify by wanglong 20121127
            parm.setData("END_DATE", endDate);//modify by wanglong 20121127
        }

        /**
         * ��ѯ�ʿسɼ�
         */
        TParm result = MROQlayControlMTool.getInstance().
            onQueryQlayControlScore(parm);
        if (result == null || result.getCount("CASE_NO") <= 0) {
            this.messageBox("û�в�ѯ���");
        }
        else {
            table_m.setParmValue(result);
        }
    }

    /**
     * ��շ���
     */
    public void onClear() {
        this.setValue("REGION_CODE", "");
        this.setValue("DEPT_CODE", "");
        this.setValue("STATION_CODE", "");
        this.setValue("USER_ID", "");
        this.setValue("STATUS", "");
        this.setValue("MR_NO", "");
        this.setValue("IPD_NO", "");
        this.setValue("PAT_NAME", "");
        this.setValue("AGE", "");
        this.setValue("REGION_CODE", Operator.getRegion());
        getTable("TABLE_M").removeRowAll();
        getTable("TABLE_D").removeRowAll();
        getTable("TABLE_DD").removeRowAll();
        onChangeRegion();
        onChangeDept();
        this.getRadioButton("TYPE_IN").setSelected(true);
        getTextFormat("START_DATE").setEnabled(false);
        getTextFormat("END_DATE").setEnabled(false);
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/'));
    }

    /**
     *
     */
    public void onChangeType(){
        if (this.getRadioButton("TYPE_IN").isSelected()) {
            getTextFormat("START_DATE").setEnabled(false);
            getTextFormat("END_DATE").setEnabled(false);
        }
        else {
            getTextFormat("START_DATE").setEnabled(true);
            getTextFormat("END_DATE").setEnabled(true);
        }
    }

    /**
     * ��񵥻��¼�
     */
    public void onTableClicked() {
        int row = table_m.getSelectedRow();
        TParm parm = table_m.getParmValue().getRow(row);
        String sql = "SELECT B.EXAMINE_DESC, B.SCORE, C.USER_NAME, "
            + " A.CHECK_DATE, A.EXAMINE_CODE, A.STATUS, A.CASE_NO "
            + " FROM MRO_QLAYCONTROLM A, MRO_CHRTVETSTD B, SYS_OPERATOR C "
            + " WHERE A.EXAMINE_CODE = B.EXAMINE_CODE "
            + " AND A.CHECK_USER = C.USER_ID "
            + " AND A.CASE_NO = '" + parm.getValue("CASE_NO") +
            "' ORDER BY A.CHECK_DATE";
      //  System.out.println("sql -----onTableClicked---- "+ sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        table_d.setParmValue(result);
    }

    /**
     * ��񵥻��¼�
     */
    public void onTableDClicked() {
        int row = table_d.getSelectedRow();
        TParm parm = table_d.getParmValue().getRow(row);
        String sql = "SELECT TO_DATE (A.EXAMINE_DATE, 'YYYYMMDDHH24MISS') "
            + " AS EXAMINE_DATE, B.EXAMINE_DESC, B.SCORE, C.USER_NAME, "
            + " A.CHECK_DATE, A.EXAMINE_CODE, A.STATUS"
            + " FROM MRO_QLAYCONTROLD A, MRO_CHRTVETSTD B, SYS_OPERATOR C "
            + " WHERE A.EXAMINE_CODE = B.EXAMINE_CODE "
            + " AND A.CHECK_USER = C.USER_ID "
            + " AND A.CASE_NO = '" + parm.getValue("CASE_NO")
            + "' AND A.EXAMINE_CODE = '" + parm.getValue("EXAMINE_CODE")
            + "' ORDER BY A.EXAMINE_DATE";
      //  System.out.println("sql -----onTableDClicked---- "+ sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        table_dd.setParmValue(result);
    }

    /**
     * �������¼�
     */
    public void onChangeRegion() {
        String region_code = this.getValueString("REGION_CODE");
        // ���˿���
        TextFormatDept dept_code = (TextFormatDept)this.getComponent(
            "DEPT_CODE");
        dept_code.setRegionCode(region_code);
        dept_code.onQuery();
        // ���˲���
        TextFormatSYSStation station_code = (TextFormatSYSStation)this.
            getComponent("STATION_CODE");
        station_code.setRegionCode(region_code);
        station_code.onQuery();
        // ����ҽ��
        TextFormatSYSOperator user_id = (TextFormatSYSOperator)this.
            getComponent("USER_ID");
        user_id.setRegionCode(region_code);
        user_id.onQuery();
    }

    /**
     * ���ұ���¼�
     */
    public void onChangeDept() {
        String dept_code = this.getValueString("DEPT_CODE");
        // ����ҽ��
        TextFormatSYSOperator user_id = (TextFormatSYSOperator)this.
            getComponent("USER_ID");
        user_id.setDept(dept_code);
        user_id.onQuery();
    }

    /**
     * �����Żس��¼�
     */
    public void onMrNo() {
        String mr_no = this.getValueString("MR_NO");
        Pat pat = Pat.onQueryByMrNo(mr_no);
        if (pat == null) {
            this.messageBox("û�в�ѯ����");
            this.setValue("MR_NO", "");
            this.setValue("IPD_NO", "");
            this.setValue("PAT_NAME", "");
            this.setValue("AGE", "");
            return;
        }
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("MR_NO", pat.getMrNo());
        this.setValue("IPD_NO", pat.getIpdNo());
        this.setValue("PAT_NAME", pat.getName());
        this.setValue("AGE", StringUtil.showAge(pat.getBirthday(), date));
    }

    /**
     * סԺ�Żس��¼�
     */
    public void onIpdNo() {
        String ipd_no = this.getValueString("IPD_NO");
        this.setValue("IPD_NO", StringTool.fill0(ipd_no, PatTool.getInstance().getIpdNoLength())); //==========chenxi 
    }
    
	/**
	 * ���Excel
	 */
	public void onExport() {//add by wangbin 20140704
		if (table_m.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table_m, "�Զ��ʿ�");
		} else {
			this.messageBox("û����Ҫ����������");
			return;
		}
	}
	
    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

}
