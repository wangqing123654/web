package com.javahis.ui.ins;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.dongyang.control.TControl;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.StringUtil;
import jdo.ins.INSUpLoadTool;
import jdo.sys.PatTool;

/**
 *
 * <p>
 * Title:
 * </p>
 *
 * <p>
 * Description:ȡ���걨
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author xueyf 2012.02.11
 * @version 1.0
 */
public class INSCancelDeclareContrl extends TControl {
    /**
     * �û����� [1,��ְ],[2,�Ǿ�]
     */
    String ctzCode;
    /**
     * ��ְ
     */
    public static String CHENGZHI = "1";
    /**
     * �Ǿ�
     */
    public static String CHENGJU = "2";

    public void onInit() {
        super.onInit();
        //��ʼ����������
        initPage();
    }

    /**
     * ��ʼ����������
     */
    public void initPage() {
        this.callFunction("UI|tPanel_0|startDate|setValue",
                          SystemTool.getInstance().getDate());
        this.callFunction("UI|tPanel_0|endDate|setValue",
                          SystemTool.getInstance().getDate());
        this.callFunction("UI|tPanel_0|sdiseaseCode|setValue",
                          "0");

    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        String mrNo = "";
        mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
        setValue("MR_NO", mrNo);
        if (StringUtil.isNullString(getText("startDate"))) {
            this.messageBox("��¼����Ժ��ʼ���ڡ�");
            return;
        }
        if (StringUtil.isNullString(getText("endDate"))) {
            this.messageBox("��¼����Ժ�������ڡ�");
            return;
        }

        TParm parm = new TParm();
        TParm data = new TParm();
        String sdiseaseCode = (String) ((TComboBox) getComponent("sdiseaseCode"))
                              .getValue();
        ctzCode = (String) ((TComboBox) getComponent("ctzCode")).getValue();
        if (StringUtil.isNullString(sdiseaseCode)) {
            this.messageBox("��ѡ�������͡�");
            return;
        }
        if (StringUtil.isNullString(ctzCode)) {
            this.messageBox("��ѡ����Ա���");
            return;
        }
        parm.setData("REGION_CODE", Operator.getRegion());
        if (sdiseaseCode.equals("0")) {

            parm.setData("SDISEASECODENULL", "0");
        } else {

            parm.setData("SDISEASECODE", "1");
        }
        parm.setData("CTZCODE", ctzCode);
        parm.setData("STARTDATE", getText("startDate"));
        parm.setData("ENDDATE", getText("endDate"));
        if (!"".equals(this.getValueString("MR_NO")) &&
            this.getValueString("MR_NO") != null)
            parm.setData("MR_NO", this.getValue("MR_NO"));
        data = TIOM_AppServer.executeAction(
                "action.ins.INSCancelDeclareAction", "selectdata", parm);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        this.callFunction("UI|TABLE|setParmValue", data);
    }

    /**
     * ȡ���걨
     */
    public void onCancel() {
        int row = ((TTable) getComponent("TABLE")).getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ����Ҫȡ���Ĳ�����");
            return;
        }

        TParm tableData = ((TTable) getComponent("TABLE")).getParmValue();
        TParm parm = new TParm();
        Map map = (Map) tableData.getRow(row).getData().get("Data");
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            parm.addData(entry.getKey() + "", entry.getValue());
        }
        TParm result = new TParm();
        parm.setCount(parm.getCount("MR_NO"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm nhiHospParm = INSUpLoadTool.getInstance().getNhiHospCode(Operator.
                getRegion());
        String nhiHospCode = nhiHospParm.getValue("NHI_NO", 0);
        parm.setData("HOSP_NHI_NO", nhiHospCode);
        if (ctzCode.equals(CHENGZHI)) {
            result = cancelByCZ(parm);

        } else {
            result = cancelByCJ(parm);
        }
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        this.messageBox("P0005");
        this.onQuery();

    }

    /**
     * ȡ���걨 ����������ְ
     * @param tableData TParm
     * @return TParm
     */
    private TParm cancelByCZ(TParm tableData) {

        return TIOM_AppServer.executeAction(
                "action.ins.INSCancelDeclareAction", "cancelByCZ", tableData);
    }

    /**
     * ȡ���걨 ���������Ǿ�
     * @param tableData TParm
     * @return TParm
     */
    private TParm cancelByCJ(TParm tableData) {
        return TIOM_AppServer.executeAction(
                "action.ins.INSCancelDeclareAction", "cancelByCJ", tableData);
    }

    /**
     *���
     */
    public void onClear() {
        initPage();
        this.setValue("ctzCode", "");
        this.setValue("MR_NO", "");
        this.callFunction("UI|TABLE|clearSelection");

    }

}
