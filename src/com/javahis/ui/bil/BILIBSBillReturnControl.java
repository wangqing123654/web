package com.javahis.ui.bil;

import com.dongyang.ui.TTable;
import jdo.adm.ADMInpTool;
import jdo.bil.BILInvrcptTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import jdo.ibs.IBSBillmTool;
import com.javahis.util.StringUtil;
import java.sql.Timestamp;
import jdo.sys.PatTool;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title: �����˵�������</p>
 *
 * <p>Description: �����˵�������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class BILIBSBillReturnControl
    extends TControl {
    //���㲡��סԺ��������
    //int i = ADMTool.getInstance().getAdmDays(CASE_NO);
    TParm mainParm = new TParm();
    String caseNo = "";
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        //�˵�����tableר�õļ���
        getTTable("MainTable").addEventListener(TTableEvent.
                                                CHECK_BOX_CLICKED, this,
                                                "onMainTableComponent");
        this.onClear();
    }

    /**
     * �˵�����table�����¼�
     * @param obj Object
     * @return boolean
     */
    public boolean onMainTableComponent(Object obj) {
        TTable mainTable = (TTable) obj;
        mainTable.acceptText();
        TParm tableParm = mainTable.getParmValue();
        mainParm = new TParm();
        int count = tableParm.getCount("BILL_NO");
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("PAY_SEL", i)) {
                mainParm.addData("BILL_NO",
                                 tableParm.getValue("BILL_NO", i));
                mainParm.addData("IPD_NO",
                                 tableParm.getValue("IPD_NO", i));
                mainParm.addData("CASE_NO",
                                 tableParm.getValue("CASE_NO", i));
                mainParm.addData("MR_NO",
                                 tableParm.getValue("MR_NO", i));
                mainParm.addData("RECEIPT_NO",
                                 tableParm.getValue("RECEIPT_NO", i));
                mainParm.addData("AR_AMT",
                                 tableParm.getValue("AR_AMT", i));
            }
            getTTable("MainTable").setValueAt("N", i, 0);
        }
        int row = getTTable("MainTable").getSelectedRow();
        getTTable("MainTable").setValueAt("Y", row, 0);
        getTTable("MainTable").acceptText();
        return true;
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
     * ��ѯ
     */
    public void onQuery() {
        TTable mainTable = getTTable("MainTable");
        String mrNo = this.getValueString("MR_NO");
        String realMrNo = PatTool.getInstance().checkMrno(mrNo);
        String ipdNo = this.getValueString("IPD_NO");
        String realIpdNo = PatTool.getInstance().checkIpdno(ipdNo);
        TParm parm = new TParm();
        if (getValueString("MR_NO").length() == 0 &&
            getValueString("IPD_NO").length() == 0) {
            this.messageBox("�����벡���Ż�סԺ��");
            return;
        }

        if (getValueString("MR_NO").length() != 0)
            parm.setData("MR_NO", realMrNo);
        if (getValueString("IPD_NO").length() != 0)
            parm.setData("IPD_NO", realIpdNo);
        TParm mParm = IBSBillmTool.getInstance().selDataForBill(parm);
       
//        System.out.println("mParm" + mParm);
        if (mParm.getCount("BILL_NO") <= 0) {
            this.messageBox("�˻������վ�");
            return;
        }
        //$---add caoyong �ۿ۽�ֵ 2014/4/22
        double reduce_amt=0;
        for(int i=0;i<mParm.getCount();i++){
        	reduce_amt=mParm.getDouble("OWN_AMT", i)-mParm.getDouble("AR_AMT", i);
        	mParm.setData("REDUCE_AMT", i, reduce_amt);
        }
        //$---end caoyong �ۿ۽�ֵ 2014/4/22
        setValue("MR_NO", mParm.getValue("MR_NO", 0));
        setValue("IPD_NO", mParm.getValue("IPD_NO", 0));
        caseNo = mParm.getValue("CASE_NO", 0);
        mainTable.setParmValue(mParm);
        TParm patInfoParm = PatTool.getInstance().getInfoForMrno(mParm.getValue(
            "MR_NO", 0));
        String patName = patInfoParm.getValue("PAT_NAME", 0);
        String sexCode = patInfoParm.getValue("SEX_CODE", 0);
        Timestamp birthDate = patInfoParm.getTimestamp("BIRTH_DATE", 0);
        setValue("PAT_NAME", patName);
        setValue("SEX_CODE", sexCode);
        TParm admInpParm = new TParm();
        admInpParm.setData("CASE_NO", caseNo);
        TParm admInpInfoParm = ADMInpTool.getInstance().selectall(admInpParm);
        Timestamp inDate = admInpInfoParm.getTimestamp("IN_DATE", 0);
        String AGE = StringUtil.showAge(birthDate, inDate);
        setValue("AGE", AGE);
    }
    /**
     * �����¼�
     */
    public void onReturnFee() {
        TTable table = (TTable)this.getComponent("MainTable");
        if(table.getParmValue().getCount()<=0){
        	this.messageBox("û��Ҫ���ϵ��˵�");
        	return;
        }
        boolean flg=false;
        for (int i = 0; i < table.getParmValue().getCount(); i++) {
			if(table.getParmValue().getBoolean("PAY_SEL",i)){
				flg=true;
				break;
			}
		}
        if(!flg){
        	this.messageBox("��ѡ��Ҫ���ϵ��˵�");
        	return;
        }
//        System.out.println("mainParm"+mainParm);
//        System.out.println("caseNo"+caseNo);
//        int selRow =  table.getSelectedRow();
        TParm parm = new TParm();
        parm.setData("BILL_NO", mainParm.getValue("BILL_NO",0));
        parm.setData("MR_NO", this.getValueString("MR_NO"));
        parm.setData("IPD_NO", this.getValueString("IPD_NO"));
        parm.setData("CASE_NO", caseNo);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("REGION_CODE",Operator.getRegion());
        TParm actionParm = new TParm();
        actionParm.setData("DATA", parm.getData());
        actionParm.setData("RETURN_FLG", "Y");//�����˵�ע��============pangben 2011-11-17
        TParm result = TIOM_AppServer.executeAction("action.ibs.IBSAction",
            "onSaveBillReturn", actionParm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            this.messageBox("E0005");
            return;
        }else{
            this.messageBox("P0005");
            onClear();
        }
    }


    /**
     * ���
     */
    public void onClear() {
        clearValue("MR_NO;IPD_NO;PAT_NAME;SEX_CODE;AGE");
        this.callFunction("UI|MainTable|removeRowAll");
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
