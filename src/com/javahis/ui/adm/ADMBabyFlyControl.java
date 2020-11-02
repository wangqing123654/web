package com.javahis.ui.adm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.Pat;
import jdo.adm.ADMInpTool;

/**
 * <p>Title:������ע�� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author JiaoY 2009.04.30
 * @version 1.0
 */
public class ADMBabyFlyControl extends TControl {
    TParm acceptData = new TParm(); //�Ӳ�
    Pat pat;
    String McaseNo ;
    public void onInit() {
        acceptData=(TParm)this.getParameter();
        callFunction("UI|save|setEnabled", false); //����
    }

    public void onMrNo() {
        pat = new Pat();
        pat = pat.onQueryByMrNo(getValue("MR_NO").toString());
        if (pat == null) {
            this.messageBox("���޲�����");
            this.onClear();
            return;
        }
        String accpMrNo = acceptData.getData("MR_NO").toString();
        if (accpMrNo.equals(pat.getMrNo())) {
            this.messageBox_("��������ͬ��");
            this.onClear();
            return;
        }
        TParm queryParm = new TParm();
        queryParm.setData("MR_NO", pat.getMrNo());
        TParm parm = ADMInpTool.getInstance().queryCaseNo(queryParm);
        if (parm.getData("IPD_NO", 0) == null || "".equals(parm.getData("IPD_NO", 0))) {
            this.messageBox("�˲���δסԺ��");
            this.onClear();
            return;
        }

        if( parm.getData("BED_NO", 0)==null||"".equals( parm.getData("BED_NO", 0))){
            this.messageBox("�˲���δ��ס��");
            this.onClear();
             return;
        }
        this.setValue("MR_NO", parm.getData("MR_NO", 0));
        this.setValue("IPD_NO", parm.getData("IPD_NO", 0));
        this.setValue("PAT_NAME", pat.getName());
        this.setValue("DEPT_CODE", parm.getData("DEPT_CODE", 0));
        this.setValue("STATION_CODE", parm.getData("STATION_CODE", 0));
        this.setValue("BED_NO", parm.getData("BED_NO", 0));
        McaseNo=  parm.getData("CASE_NO", 0).toString();
        callFunction("UI|save|setEnabled", true); //����

    }

    /**
     * ����
     */
    public void onSave() {
        TParm sendParm = new TParm();
        pat = new Pat();
        String mrNo = acceptData.getData("MR_NO").toString();
        pat = pat.onQueryByMrNo(mrNo);
        String motherNo = getValue("MR_NO").toString();
        if (motherNo == null || "".equals(motherNo)) {
            this.messageBox_("ĸ�ײ�����Ϊ�գ�");
            return;
        }
        pat.modifyMotherMrNo(motherNo);
        if (pat.onSave()) {
            this.messageBox("P0005");
            sendParm.setData("IPD_NO",getValue("IPD_NO"));
            sendParm.setData("M_CASE_NO",McaseNo);
            this.setReturnValue(sendParm);
            this.closeWindow();
        } else {
            this.messageBox("E0005");
            return;
        }
    }
    /**
     * ������ѯ
     */
    public void onPatInfo (){
        TParm sendParm = new TParm();
        TParm reParm = (TParm)this.openDialog(
                      "%ROOT%\\config\\adm\\ADMPatQuery.x", sendParm);
        this.setValue("MR_NO",reParm.getData("MR_NO"));
        this.onMrNo();
    }
    /**
     * ���
     */
    public void onClear(){
        this.clearValue("MR_NO;IPD_NO;PAT_NAME;DEPT_CODE;STATION_CODE;BED_NO");
    }

}
