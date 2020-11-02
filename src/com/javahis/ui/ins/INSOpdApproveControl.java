package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.manager.TCM_Transform;
import jdo.ins.INSOpdApproveTool;
import jdo.sys.SystemTool;
import com.dongyang.manager.TIOM_AppServer;

/**
 *
 * <p>Title:����ҽ����˿����� </p>
 *
 * <p>Description:����ҽ����˿����� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2008.11.05
 * @version JavaHis 1.0
 */
public class INSOpdApproveControl extends TControl {
    TParm approvedata;
    int selectRow = -1;
    //���һ
    String ctz1 = "";
    //��ݶ�
    String ctz2 = "";
    //�����
    String ctz3 = "";
    //�������
    String case_no = "";
    private static final String actionName = "action.ins.InsOpbAction";

    public void onInit() {
        super.onInit();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
        //��ʼ������ʱ��
        this.callFunction("UI|ADM_DATE|setValue",
                          SystemTool.getInstance().getDate());

    }

    /**
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {

        if (row < 0)
            return;
//        TParm allData = new TParm();
//        TParm parm = this.getSelectRowData("Table");
//        allData = INSOpdApproveTool.getInstance().selectdata(parm);
//        setValueForParm("APPROVE_FLG",
//                        allData, row);
//        selectRow = row;
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = new TParm();
        //�����Ų���
        String mrNo = TCM_Transform.getString(getValue("MR_NO"));
        int mrno = mrNo.trim().length();
        mrNo = "000000000000".substring(0, 12 - mrno) + mrNo.trim();
        parm.setData("MR_NO", mrNo);
        String admDate = TCM_Transform.getString(getValue("ADM_DATE")).
                         substring(0, 10);
        parm.setData("ADM_DATE", admDate);
        TParm patParm = new TParm();
        patParm = INSOpdApproveTool.getInstance().selPatInfo(parm);
        TParm result = INSOpdApproveTool.getInstance().getCaseNoByMrNo(parm);
        case_no = "";
        if (result.getErrCode() < 0)
            return;
        //����鲻�������Һ���Ϣ
        if (result.getCount() == 0)
            this.messageBox("�����޹Һ���Ϣ");

        //����ֻ��һ�ιҺ���Ϣ
        if (result.getCount() == 1) {
            setValueForParm(
                    "MR_NO;PAT_NAME;IDNO;SEX_CODE",
                    patParm,
                    0);
//            setValue("APPROVE_FLG", "02");
            case_no = patParm.getValue("CASE_NO", 0);

        }
        //���ն�ιҺ�
        if (result.getCount() > 1) {
            TParm data = new TParm();
            setValueForParm(
                    "MR_NO;PAT_NAME;IDNO;SEX_CODE",
                    patParm,
                    0);
//            setValue("APPROVE_FLG", "02");
            data.setData("PATINFO", patParm.getData());
            //�ж��Ǵ�mrno�¼��е����ľ����ѡ��
            data.setData("count", "1");
            case_no = (String) openDialog(
                    "%ROOT%\\config\\ins\\INSOpdApproveDialog.x", data);

        }
        if (case_no == null || case_no.length() == 0)
            return;
        TParm getCtzInRegPatadm = INSOpdApproveTool.getInstance().
                                  getCtzInRegPatadm(case_no);
        String ctzCode1 = getCtzInRegPatadm.getValue("CTZ1_CODE", 0);
        String ctzCode2 = getCtzInRegPatadm.getValue("CTZ2_CODE", 0);
        String ctzCode3 = getCtzInRegPatadm.getValue("CTZ3_CODE", 0);
        this.ctz1 = ctzCode1;
        this.ctz2 = ctzCode2;
        this.ctz3 = ctzCode3;
        setValue("CTZ1_CODE", ctzCode1);
        setValue("CTZ2_CODE", ctzCode2);
        setValue("CTZ3_CODE", ctzCode3);
        TParm getFlgByCtz = INSOpdApproveTool.getInstance().getFlgByCtz(
                ctzCode1);
        if (!getFlgByCtz.getBoolean("NHI_CTZ_FLG", 0)) {
            this.messageBox("����ҽ�����");
            return;
        }
        TParm allData = new TParm();
        //����Action����
        allData.setData("CASE_NO", case_no);
        allData.setData("APPROVE_FLG", getValue("APPROVE_FLG"));
        allData.setData("CTZ1_CODE", ctzCode1);
        allData.setData("CTZ2_CODE", ctzCode2);
        allData.setData("OPT_USER", Operator.getID());
        allData.setData("OPT_TERM", Operator.getIP());
        TParm actionParm = TIOM_AppServer.executeAction(actionName,
                "saveInsOpdApprove", allData);
        int dCount = actionParm.getCount("ORDER_CODE");
        TParm descriptionParm = new TParm();
        for (int j = 0; j < dCount; j++) {
            String orderCode = TCM_Transform.getString(actionParm.getData(
                    "ORDER_CODE", j));
            if (ctzCode2 == null || ctzCode2.length() < 1) {
                ctzCode2 = "";
            }
            TParm selDescription = INSOpdApproveTool.getInstance().
                                   selDescription(ctzCode1, ctzCode2, orderCode);
            descriptionParm.addData("DESCRIPTION",
                                    selDescription.getData("DESCRIPTION", 0));
            actionParm.addData("DESCRIPTION",
                               selDescription.getData("DESCRIPTION", 0));
        }
        if (actionParm.getErrCode() < 0) {
            messageBox(actionParm.getErrText());
            return;
        }
        this.callFunction("UI|Table|setParmValue", actionParm);
        getCompanyByCtz();

    }

    /**
     * ���������¼����
     */
    public void onRecord() {
        if (getValueString("MR_NO").equals("")) {
            this.messageBox("�����벡����");
            return;
        }
        onQuery();
    }

    /**
     * �õ�ѡ��������
     * @param tableTag String
     * @return TParm
     */
    public TParm getSelectRowData(String tableTag) {
        int selectRow = (Integer) callFunction("UI|" + tableTag +
                                               "|getSelectedRow");
        TParm parm = (TParm) callFunction("UI|" + tableTag + "|getParmValue");
        TParm parmRow = parm.getRow(selectRow);
        parmRow.setData("OPT_USER", Operator.getID());
        parmRow.setData("OPT_TERM", Operator.getIP());
        return parmRow;
    }

    /**
     * ����
     */
    public void onUpdate() {
        String ctzOne = this.getValue("CTZ1_CODE").toString();
        String ctzTwo = this.getValue("CTZ2_CODE").toString();
        String ctzThree = this.getValue("CTZ3_CODE").toString();
        if (!ctzOne.equals(this.ctz1) || !ctzTwo.equals(this.ctz2) ||
            !ctzThree.equals(this.ctz3)) {
            if (messageBox("��ʾ��Ϣ", "����Ѿ��ı��Ƿ��޸�?", this.YES_NO_OPTION) != 0) {
                this.setValue("CTZ1_CODE", this.ctz1);
                this.setValue("CTZ2_CODE", this.ctz2);
                this.setValue("CTZ3_CODE", this.ctz3);
                return;
            }
            //����REG���/ɾ����˵�����COMMIT
            TParm action = new TParm();
            action.setData("MR_NO", this.getValue("MR_NO").toString());
            action.setData("CASE_NO", case_no);
            action.setData("CTZ1_CODE", ctzOne);
            action.setData("CTZ2_CODE", ctzTwo.equals("00") ? "" : ctzTwo);
//            System.out.println("�����ctzTwo" + ctzTwo);
            action.setData("CTZ3_CODE", ctzThree);
            TParm actionParm = TIOM_AppServer.executeAction(actionName,
                    "upDateCTZ", action);
            if (actionParm.getErrCode() < 0) {
                this.messageBox_("��ݸ���ʧ��!");
                return;
            } else {
                //���²����������
                if (messageBox("��ʾ��Ϣ", "��ݸ��³ɹ��Ƿ����������˵�����?", this.YES_NO_OPTION) !=
                    0) {
                    this.onClear();
                    return;
                }
                //�����������
                this.onQuery();
                return;
            }
        }
        this.callFunction("UI|Table|exeModifyUpdate");
        TParm allParm = new TParm();
        this.callFunction("UI|TABLE|acceptText"); //�����뿪������table�ı��ı�
        allParm.setData("Table", getValue("Table")); //�õ�ҳ��Table����
//        System.out.println("allParm"+allParm);
        TParm ddd = allParm.getRow();
        int count = ((TParm)this.callFunction("UI|Table|getParmValue")).
                    getCount();
//        System.out.println("count"+count);
//        System.out.println("����"+ddd);
        TParm parm2 = new TParm();
        parm2.setData("MR_NO", getValue("MR_NO"));
        parm2.setData("CTZ1_CODE", getValue("CTZ1_CODE"));
        parm2.setData("CTZ2_CODE",
                      getValue("CTZ2_CODE").equals("00") ? "" :
                      getValue("CTZ2_CODE"));
        parm2.setData("CTZ3_CODE", getValue("CTZ3_CODE"));

        TParm parm = (TParm) callFunction("UI|Table|getParmValue");
        TParm allData = new TParm();
        allData.setRowData(allParm);
        allData.setRowData(parm);
        allData.setData("MR_NO", parm2.getData("MR_NO"));
        allData.setData("CTZ1_CODE", parm2.getData("CTZ1_CODE"));
        allData.setData("CTZ2_CODE", parm2.getData("CTZ2_CODE"));
        allData.setData("CTZ3_CODE", parm2.getData("CTZ3_CODE"));
        TParm result = TIOM_AppServer.executeAction(actionName,
                "updateInfo", allData);
        if (result.getErrCode() < 0) {
            messageBox("����ʧ��!");
          //  System.out.println("ERR: + result.getErrText");
            return;
        }
        this.messageBox("���³ɹ�!");
    }

    /**
     * ��������ݵõ���Լ�����ش���
     */
    public void getCompanyByCtz() {
        String ctzCode = TCM_Transform.getString(getValue("CTZ1_CODE"));
        TParm result = INSOpdApproveTool.getInstance().getCompanyByCtz(ctzCode);
        String companyCode = TCM_Transform.getString(result.getValue(
                "COMPANY_CODE", 0));
        setValue("INS_COMPANY", companyCode);
    }

    /**
     * ����
     */
    public void onSave() {
        onUpdate();
    }

    /**
     *���
     */
    public void onClear() {
        clearValue("MR_NO;PAT_NAME;IDNO;SEX_CODE;INS_COMPANY;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;APPROVE_FLG");
        this.callFunction("UI|ADM_DATE|setValue",
                          SystemTool.getInstance().getDate());
        this.callFunction("UI|Table|removeRowAll");
        selectRow = -1;
    }

    /**
     * ���µ�ǰ���ﲡ������order���״̬λ
     */
    public void updateAllApprove() {

        TParm parm = new TParm();
        parm.setData("CASE_NO", case_no);
        parm.setData("ADM_DATE",
                     (getValue("ADM_DATE").toString()).replaceAll("-", "").
                     substring(0, 8));
        parm.setData("APPROVE_FLG", getValue("APPROVE_FLG"));
        TParm result = INSOpdApproveTool.getInstance().updateAllApprove(parm);
        if (result.getErrCode() < 0) {
            messageBox_("����ʧ��!");
          //  System.out.println("ERR: + result.getErrText");
            return;
        }
        TParm parm1 = new TParm();
        TParm endParm = (TParm)callFunction("UI|Table|getParmValue");
        int count = endParm.getCount("APPROVE_FLG");
        //System.out.println("����"+count);
        for(int i=0;i<count;i++){
            parm1.addData("APPROVE_FLG",parm.getData("APPROVE_FLG"));
        }
        endParm.setData("APPROVE_FLG",parm1.getData("APPROVE_FLG"));
        this.callFunction("UI|Table|setParmValue", endParm);
        messageBox_("���³ɹ�!");
    }

}
