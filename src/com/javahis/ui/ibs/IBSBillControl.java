package com.javahis.ui.ibs;

import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import jdo.ibs.IBSOrdermTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.ibs.IBSOrderdTool;
import jdo.sys.PatTool;
import com.dongyang.manager.TCM_Transform;
import jdo.sys.Operator;
import com.javahis.system.combo.TComboSYSBed;
import com.javahis.system.combo.TComboSYSStationCode;
import com.dongyang.ui.TTable;

/**
 *
 * <p>Title: �µ��н������</p>
 *
 * <p>Description: �µ��н������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class IBSBillControl
    extends TControl {
    private TRadioButton all;
    private TRadioButton rStationCode;
    private TRadioButton rBedNo;
    private TRadioButton rMrNo;
    private TRadioButton rIpdNo;
    private TComboSYSStationCode STATION_CODE;
    private TComboSYSBed BED_NO;
    private TTextField MR_NO;
    private TTextField IPD_NO;
    private TParm caseParm = new TParm();
    private static final String actionName = "action.ibs.IBSAction";
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        initPage();
    }

    /**
     * ��ʼ������
     */
    public void initPage() {
        Timestamp today = SystemTool.getInstance().getDate();
        Timestamp yesterday = StringTool.rollDate(today, -1);
        String todayTime = StringTool.getString(yesterday,"yyyy/MM/dd");
        setValue("CUT_BILL_DATE", todayTime+" "+"23:59:59");
        all = (TRadioButton)this.getComponent("All");
        rStationCode = (TRadioButton)this.getComponent("R_STATION_CODE");
        rBedNo = (TRadioButton)this.getComponent("R_BED_NO");
        rMrNo = (TRadioButton)this.getComponent("R_MR_NO");
        rIpdNo = (TRadioButton)this.getComponent("R_IPD_NO");
        STATION_CODE = (TComboSYSStationCode)this.getComponent("STATION_CODE");
        BED_NO = (TComboSYSBed)this.getComponent("BED_NO");
        MR_NO = (TTextField)this.getComponent("MR_NO");
        IPD_NO = (TTextField)this.getComponent("IPD_NO");
        all.setSelected(true);
        STATION_CODE.setEnabled(false);
        BED_NO.setEnabled(false);
        MR_NO.setEnabled(false);
        IPD_NO.setEnabled(false);
    }

    /**
     * ȫԺradiobutton�¼�
     */
    public void onSelall() {
        if (all.getValue().equals("Y")) {
            STATION_CODE.setEnabled(false);
            BED_NO.setEnabled(false);
            MR_NO.setEnabled(false);
            IPD_NO.setEnabled(false);
            setValue("STATION_CODE", "");
            setValue("BED_NO", "");
            setValue("MR_NO", "");
            setValue("IPD_NO", "");
            this.callFunction("UI|RegionTable|removeRowAll");
            this.callFunction("UI|StationTable|removeRowAll");
            this.callFunction("UI|OrderTable|removeRowAll");
        }
    }

    /**
     * ����radiobutton�¼�
     */
    public void onSelrStationCode() {
        if (rStationCode.getValue().equals("Y")) {
            STATION_CODE.setEnabled(true);
            BED_NO.setEnabled(false);
            MR_NO.setEnabled(false);
            IPD_NO.setEnabled(false);
            setValue("BED_NO", "");
            setValue("MR_NO", "");
            setValue("IPD_NO", "");
            this.callFunction("UI|RegionTable|removeRowAll");
            this.callFunction("UI|StationTable|removeRowAll");
            this.callFunction("UI|OrderTable|removeRowAll");
        }

    }

    /**
     * ����radiobutton�¼�
     */
    public void onSelrBedNo() {
        if (rBedNo.getValue().equals("Y")) {
            STATION_CODE.setEnabled(false);
            BED_NO.setEnabled(true);
            MR_NO.setEnabled(false);
            IPD_NO.setEnabled(false);
            setValue("STATION_CODE", "");
            setValue("MR_NO", "");
            setValue("IPD_NO", "");
            this.callFunction("UI|RegionTable|removeRowAll");
            this.callFunction("UI|StationTable|removeRowAll");
            this.callFunction("UI|OrderTable|removeRowAll");
        }
    }

    /**
     * ������radiobutton�¼�
     */
    public void onSelrMrNo() {
        if (rMrNo.getValue().equals("Y")) {
            STATION_CODE.setEnabled(false);
            BED_NO.setEnabled(false);
            MR_NO.setEnabled(true);
            IPD_NO.setEnabled(false);
            setValue("STATION_CODE", "");
            setValue("BED_NO", "");
            setValue("IPD_NO", "");
            this.callFunction("UI|RegionTable|removeRowAll");
            this.callFunction("UI|StationTable|removeRowAll");
            this.callFunction("UI|OrderTable|removeRowAll");
        }
    }

    /**
     * ������radiobutton�¼�
     */
    public void onSelrIpdNo() {
        if (rIpdNo.getValue().equals("Y")) {
            STATION_CODE.setEnabled(false);
            BED_NO.setEnabled(false);
            MR_NO.setEnabled(false);
            IPD_NO.setEnabled(true);
            setValue("STATION_CODE", "");
            setValue("MR_NO", "");
            setValue("BED_NO", "");
            this.callFunction("UI|RegionTable|removeRowAll");
            this.callFunction("UI|StationTable|removeRowAll");
            this.callFunction("UI|OrderTable|removeRowAll");
        }
    }

    /**
     * �õ�TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }

    /**
     *���Ӷ�ȫԺRegionTable�ļ���
     */
    public void onRegionTableClicked() {
        TTable regionTable = getTTable("RegionTable");
        TTable stationTable = getTTable("StationTable");
        int row = regionTable.getSelectedRow();
        if (row < 0)
            return;
        TParm parm = new TParm();
        TParm regionParm = regionTable.getParmValue();
        parm.setData("STATION_CODE", regionParm.getData("STATION_CODE", row));

        TParm data = IBSOrdermTool.getInstance().selStationPatInfo(parm);
        stationTable.setParmValue(data);
    }

    /**
     *���ӶԲ���StationTable�ļ���
     */
    public void onStationTableClicked() {
        TTable stationTable = getTTable("StationTable");
        TTable orderTable = getTTable("OrderTable");
        int row = stationTable.getSelectedRow();
        if (row < 0)
            return;
        TParm parm = new TParm();
        TParm regionParm = stationTable.getParmValue();
        parm.setData("CASE_NO", regionParm.getData("CASE_NO", row));
        TParm data = IBSOrderdTool.getInstance().selPatOrderData(parm);
        int sumQty=0;
        double sumNhiPrice=0.00;//ҽ���ܽ��
        double sumOwnPrice=0.00;//�Է��ܽ��
        double sumTotAmt=0.00; //�ܽ��

        for (int i = 0; i < data.getCount(); i++) {
        	sumQty+=data.getDouble("DOSAGE_QTY",i);
        	sumNhiPrice+=data.getDouble("NHI_PRICE",i);
        	sumOwnPrice+=data.getDouble("OWN_PRICE",i);
        	sumTotAmt+=data.getDouble("TOT_AMT",i);
		}
        data.addData("BILL_DATE", data.getData("BILL_DATE",0));
        data.addData("ORDER_DESC", "�ܼ�:");
        data.addData("OWN_FLG", "Y");
        data.addData("DOSAGE_QTY", sumQty);
        data.addData("NHI_PRICE", sumNhiPrice);
        data.addData("OWN_PRICE", sumOwnPrice);
        data.addData("TOT_AMT", sumTotAmt);
        data.addData("OPT_USER", data.getData("OPT_USER",0));
        orderTable.setParmValue(data);
    }


    /**
     * ��ѯ
     */
    public void onQuery() {
        TTable regionTable = getTTable("RegionTable");
        TParm parm = new TParm();
        TParm regionParm = new TParm();
        //ȫԺ
        //if (all.getValue().equals("Y")) {
        //    parm = new TParm();
        //}
        //����
        if (rStationCode.getValue().equals("Y")) {
            parm = new TParm();
            if (getValue("STATION_CODE").equals(null) ||
                getValue("STATION_CODE").toString().length() == 0) {
                this.messageBox("��ѡ����");
                return;
            }
            parm.setData("STATION_CODE", getValue("STATION_CODE"));
        }
        //��λ��
        if (rBedNo.getValue().equals("Y")) {
            parm = new TParm();
            if (getValue("BED_NO").equals(null) ||
                getValue("BED_NO").toString().length() == 0) {
                this.messageBox("�����봲λ��");
                return;
            }
            parm.setData("BED_NO", getValue("BED_NO"));
        }
        //������
        if (rMrNo.getValue().equals("Y")) {
            parm = new TParm();
            if (getValue("MR_NO").equals(null) ||
                getValue("MR_NO").toString().length() == 0) {
                this.messageBox("�����벡����");
                return;
            }
            setValue("MR_NO",
                     PatTool.getInstance().checkMrno(TCM_Transform.
                getString(getValue("MR_NO"))));
            parm.setData("MR_NO", this.getValueString("MR_NO"));
            TParm selPatInfo = PatTool.getInstance().getInfoForMrno(getValueString("MR_NO"));
            setValue("PAT_NAME",selPatInfo.getValue("PAT_NAME",0));
        }
        //סԺ��
        if (rIpdNo.getValue().equals("Y")) {
            parm = new TParm();
            if (getValue("IPD_NO").equals(null) ||
                getValue("IPD_NO").toString().length() == 0) {
                this.messageBox("������סԺ��");
                return;
            }
            setValue("IPD_NO",
                     PatTool.getInstance().checkIpdno(TCM_Transform.
                getString(getValue("IPD_NO"))));
            parm.setData("IPD_NO", getValue("IPD_NO"));
        }
        if (!"".equals(Operator.getRegion()))
            parm.setData("REGION_CODE", Operator.getRegion());
        regionParm = IBSOrdermTool.getInstance().selRegionStation(parm);
        caseParm = IBSOrdermTool.getInstance().selRegionStationCaseNo(parm);
        regionTable.setParmValue(regionParm);
    }

    /**
     * ���
     */
    public void onClear() {

        initPage();
        setValue("STATION_CODE", "");
        setValue("BED_NO", "");
        setValue("MR_NO", "");
        setValue("IPD_NO", "");
        this.callFunction("UI|RegionTable|removeRowAll");
        this.callFunction("UI|StationTable|removeRowAll");
        this.callFunction("UI|OrderTable|removeRowAll");
        caseParm = new TParm();
        setValue("PAT_NAME","");
    }

    /**
     * ����
     * @return boolean
     */
    public boolean onSave() {
        if(caseParm.getCount() <= 0)
        {
            this.messageBox("û�в�ѯ���!");
            return false;
        }
        caseParm.setData("BILL_DATE", getValue("CUT_BILL_DATE"));
        caseParm.setData("FLG", "N");
        caseParm.setData("OPT_USER", Operator.getID());
        caseParm.setData("OPT_TERM", Operator.getIP());
//        System.out.println("�½����"+caseParm);
        TParm result = TIOM_AppServer.executeAction(actionName,
            "onNewIBSBill", caseParm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;
        }
        onClear();
        //����ɹ�
        this.messageBox("P0001");
        return true;
    }

//    /**
//     * �ر��¼�
//     * @return boolean
//     */
//    public boolean onClosing() {
//        switch (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�?", this.YES_NO_CANCEL_OPTION)) {
//            case 0:
//                if (!onSave())
//                    return false;
//                break;
//            case 1:
//                break;
//            case 2:
//                return false;
//        }
//        return true;
//    }
}
