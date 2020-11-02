package com.javahis.ui.adm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import jdo.adm.ADMChgTool;
import jdo.sys.SystemTool;
import jdo.sys.Pat;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ADMQueryChgLogControl extends TControl {
    TParm data;
    int selectRow = -1;
    TParm recptParm = new TParm(); //�Ӳ�
    public void onInit() {
        Object obj = this.getParameter();
        recptParm = new TParm();
        if (obj instanceof TParm) {
            recptParm = (TParm) obj;
            this.initUI(recptParm);
        }

        this.onQuery();
    }

    /**
     * ��ʼ������
     * @param recptParm TParm
     */
    public void initUI(TParm recptParm) {
        setValue("MR_NO", recptParm.getData("ADM", "MR_NO").toString());
        setValue("IPD_NO", recptParm.getData("ADM", "IPD_NO"));
        Pat pat = new Pat();
        pat = pat.onQueryByMrNo(recptParm.getData("ADM", "MR_NO").toString());
        setValue("PAT_NAME", pat.getName());
        setValue("CASE_NO", recptParm.getData("ADM", "CASE_NO"));
        String admFlg = recptParm.getData("ADM", "ADM_FLG").toString();
        if ("Y".equals(admFlg)) {
            setValue("ADMIN", "Y");
        } else {
            setValue("ADMOUT", "Y");
        }
        setValue("START_DATE", recptParm.getData("ADM", "ADM_DATE")); //Ԥ������
        setValue("END_DATE", SystemTool.getInstance().getDate()); //Ԥ������
    }


    /**
     * ���Ӷ�Table�ļ���
     *
     * @param row
     *            int
     */
    public void onTableClicked(int row) {
        // ѡ����
        if (row < 0)
            return;
//
        selectRow = row;
        // ���ɱ༭
//       ((TTextField) getComponent("ORDER_CODE")).setEnabled(false);
        // ����ɾ����ť״̬
//       ((TMenuItem) getComponent("delete")).setEnabled(true);
    }

    public void onMrNo() {
        Pat pat = new Pat();
        String mrNo = getValue("MR_NO").toString().trim();
        pat = pat.onQueryByMrNo(mrNo);
        setValue("MR_NO", pat.getMrNo());
        setValue("PAT_NAME", pat.getName());
        setValue("MR_NO", pat.getMrNo());
        setValue("IPD_NO", pat.getIpdNo());
        if (pat.getIpdNo() == null || "".equals(pat.getIpdNo()))
            this.messageBox("�޲�����Ϣ");
    }

    /**
     * ����ǰTOOLBAR
     */
    public void onShowWindowsFunction() {
        //��ʾUIshowTopMenu
        callFunction("UI|showTopMenu");
    }

    /**
     * ��ѯ�¼�
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setData("CASE_NO", getValue("CASE_NO"));
        parm.setData("MR_NO", getValue("MR_NO"));
        parm.setData("START_DATE", getValue("START_DATE"));
        parm.setData("END_DATE", getValue("END_DATE"));
//    if("Y".equals(getValue("ADMIN")))
//        parm.setData("END_DATE",getValue("END_DATE"));
//    else
//        parm.setData("END_DATE",getValue("END_DATE"));
        TParm result = ADMChgTool.getInstance().ADMQueryChgLog(parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        ((TTable) getComponent("TABLE_CHG")).setParmValue(result);
    }


}
