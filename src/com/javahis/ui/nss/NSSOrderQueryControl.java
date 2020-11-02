package com.javahis.ui.nss;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;

import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import jdo.adm.ADMInpTool;
import jdo.sys.Pat;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;
import jdo.nss.NSSSQL;
import jdo.sys.Operator;
import jdo.util.Manager;

/**
 * <p>Title: ���Ͳ�ѯ</p>
 *
 * <p>Description: ���Ͳ�ѯ</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.18
 * @version 1.0
 */
public class NSSOrderQueryControl
    extends TControl {

    private TTable table;

    private String case_no = "";

    public NSSOrderQueryControl() {
    }

    /*
     * ��ʼ��
     */
    public void onInit() {
        // ��TABLEDEPT�е�CHECKBOX��������¼�
        callFunction("UI|TABLE|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        table = (TTable)this.getComponent("TABLE");
        String datetime = SystemTool.getInstance().getDate().toString();
        this.setValue("DIET_DATE", datetime.substring(0, 10).replace("-", "/"));
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        String sql =
            " SELECT 'N' AS SELECT_FLG, TO_DATE(B.DIET_DATE,'YYYY/MM/DD') AS DIET_DATE, "
            + " C.STATION_DESC, D.BED_NO_DESC, E.PAT_NAME, F.MEAL_CHN_DESC, "
            + " B.DIET_KIND, B.PACK_CHN_DESC , G.CALORIES, "
            + " CASE WHEN B.ADD_ON='Y' THEN '�Ӳ�' || '  ' ||B.DESCRIPTION ELSE "
            + " B.DESCRIPTION END AS DESCRIPTION, B.PACK_CODE, B.MEAL_CODE "
            + " FROM ADM_INP A,  NSS_ORDER B, SYS_STATION C, SYS_BED D, "
            + " SYS_PATINFO E, NSS_MEAL F, NSS_PACKM G "
            + " WHERE A.CASE_NO = B.CASE_NO "
            + " AND A.STATION_CODE = C.STATION_CODE "
            + " AND A.BED_NO = D.BED_NO AND A.MR_NO = E.MR_NO "
            + " AND B.MEAL_CODE = F.MEAL_CODE AND B.PACK_CODE = G.PACK_CODE"
            + " AND B.BILL_FLG = 'Y' ";
        // ����
        String station_code = this.getValueString("STATION_CODE");
        if (station_code != null && station_code.length() > 0) {
            sql += " AND A.STATION_CODE = '" + station_code + "' ";
        }
        // �������
        if (case_no != null && case_no.length() > 0) {
            sql += " AND A.CASE_NO = '" + case_no + "' ";
        }
        // �ò�����
        String diet_date = this.getValueString("DIET_DATE");
        diet_date = diet_date.substring(0, 4) + diet_date.substring(5, 7) +
            diet_date.substring(8, 10);
        if (diet_date != null && diet_date.length() > 0) {
            sql += " AND B.DIET_DATE = '" + diet_date + "' ";
        }
        // �ʹ�
        String meal_code = this.getValueString("MEAL_CODE");
        if (meal_code != null && meal_code.length() > 0) {
            sql += " AND B.MEAL_CODE = '" + meal_code + "' ";
        }
        sql = sql + " ORDER BY B.CASE_NO, B.DIET_DATE, B.MEAL_CODE ";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            table.removeRowAll();
            return;
        }
        table.setParmValue(parm);
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String datetime = SystemTool.getInstance().getDate().toString();
        this.setValue("DIET_DATE", datetime.substring(0, 10).replace("-", "/"));
        this.clearValue("STATION_CODE;MR_NO;IPD_NO;PAT_NAME;MEAL_CODE");
        table.removeRowAll();
        case_no = "";
    }

    /**
     * ��ӡ��������
     */
    public void onPrint() {
        TParm parmTable = table.getParmValue();
        // ��ӡ����
        TParm date = new TParm();
        // ��ͷ����
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "���͵�");
        // �������
        TParm parm = new TParm();
        for (int i = 0; i < table.getRowCount(); i++) {
            if ("N".equals(parmTable.getValue("SELECT_FLG", i))) {
                continue;
            }
            parm.addData("DIET_DATE",
                         parmTable.getValue("DIET_DATE", i).substring(0, 10).
                         replace("-", "/"));
            parm.addData("STATION_DESC", parmTable.getValue("STATION_DESC", i));
            parm.addData("BED_NO_DESC", parmTable.getValue("BED_NO_DESC", i));
            parm.addData("PAT_NAME", parmTable.getValue("PAT_NAME", i));
            parm.addData("MEAL_CHN_DESC", parmTable.getValue("MEAL_CHN_DESC", i));
            parm.addData("DIET_KIND",
                         "1".equals(parmTable.getValue("DIET_KIND", i)) ? "��ʳ" :
                         "������ʳ");
            parm.addData("PACK_CHN_DESC", parmTable.getValue("PACK_CHN_DESC", i));
            parm.addData("CALORIES", parmTable.getDouble("CALORIES", i));
            parm.addData("DESCRIPTION", parmTable.getValue("DESCRIPTION", i));
        }
        if (parm.getCount("DIET_DATE") <= 0) {
            this.messageBox("û�д�ӡ����");
            return;
        }

        parm.setCount(parm.getCount("DIET_DATE"));
        parm.addData("SYSTEM", "COLUMNS", "DIET_DATE");
        parm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
        parm.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
        parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        parm.addData("SYSTEM", "COLUMNS", "MEAL_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "DIET_KIND");
        parm.addData("SYSTEM", "COLUMNS", "PACK_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "CALORIES");
        parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");

        date.setData("TABLE", parm.getData());
        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
        date.setData("DATE", "TEXT",
                     "�Ʊ�����: " +
                     SystemTool.getInstance().getDate().toString().substring(0, 10).
                     replace('-', '/'));
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\NSS\\NSSOrderOut.jhw", date);
    }

    /**
     * ��ӡ��ʳ��
     */
    public void onCard() {
        TParm parm = table.getParmValue();
        // ��ӡ����
        TParm printData = new TParm();
        TParm date = new TParm();
        int count = 0;
        //NSSSQL.getNSSPackDD()
        TParm packDD = new TParm();

        for (int i = 0; i < table.getRowCount(); i++) {
            if ("N".equals(parm.getValue("SELECT_FLG", i))) {
                continue;
            }
            packDD = new TParm(TJDODBTool.getInstance().select(NSSSQL.
                getNSSPackDD(parm.getValue("PACK_CODE", i),
                             parm.getValue("MEAL_CODE", i))));
            String meau_list = "";
            for (int j = 0; j < packDD.getCount(); j++) {
                meau_list += "    " + packDD.getValue("MENU_CHN_DESC", j) + "\n\r";
            }
            if (count % 2 == 0) {
                date.addData("TITLE_1", "��ʳ��");
                date.addData("DIET_KIND_1",
                             "1".equals(parm.getValue("DIET_KIND", i)) ? "��ʳ" :
                             "������ʳ");
                date.addData("DIET_DATE_1",
                             parm.getValue("DIET_DATE", i).substring(0, 10).
                             replace("-",
                                     "/"));
                date.addData("MEAL_CHN_DESC_1",
                             parm.getValue("MEAL_CHN_DESC", i));
                date.addData("STATION_DESC_1",
                             parm.getValue("STATION_DESC", i) + "  " +
                             parm.getValue("BED_NO_DESC", i));
                date.addData("PAT_NAME_1", parm.getValue("PAT_NAME", i));
                date.addData("PACK_CHN_DESC_1",
                             parm.getValue("PACK_CHN_DESC", i));
                date.addData("CALORIES_1", parm.getDouble("CALORIES", i) + "��");
                date.addData("PACK_1", "��Ʒ:\r\n"+meau_list);

                date.addData("DESCRIPTION_1",
                             "��ע: " + parm.getValue("DESCRIPTION", i));
            }else{
                date.addData("TITLE_2", "��ʳ��");
                date.addData("DIET_KIND_2",
                             "1".equals(parm.getValue("DIET_KIND", i)) ? "��ʳ" :
                             "������ʳ");
                date.addData("DIET_DATE_2",
                             parm.getValue("DIET_DATE", i).substring(0, 10).
                             replace("-",
                                     "/"));
                date.addData("MEAL_CHN_DESC_2",
                             parm.getValue("MEAL_CHN_DESC", i));
                date.addData("STATION_DESC_2",
                             parm.getValue("STATION_DESC", i) + "  " +
                             parm.getValue("BED_NO_DESC", i));
                date.addData("PAT_NAME_2", parm.getValue("PAT_NAME", i));
                date.addData("PACK_CHN_DESC_2",
                             parm.getValue("PACK_CHN_DESC", i));
                date.addData("CALORIES_2", parm.getDouble("CALORIES", i) + "��");
                date.addData("PACK_2", "��Ʒ:\r\n" + meau_list);
                date.addData("DESCRIPTION_2",
                             "��ע: " + parm.getValue("DESCRIPTION", i));
            }
            count++;
        }

        if (count % 2 != 0) {
            date.addData("TITLE_2", "");
            date.addData("DIET_KIND_2", "");
            date.addData("DIET_DATE_2", "");
            date.addData("MEAL_CHN_DESC_2", "");
            date.addData("STATION_DESC_2", "");
            date.addData("PAT_NAME_2", "");
            date.addData("PACK_CHN_DESC_2", "");
            date.addData("CALORIES_2", "");
            date.addData("PACK_2", "");
            date.addData("DESCRIPTION_2", "");
        }

        if (date.getCount("TITLE_1") <= 0) {
            this.messageBox("��ѡ���ӡ����");
            return;
        }

        date.setCount(date.getCount("TITLE_1"));

        date.addData("SYSTEM", "COLUMNS", "TITLE_1");
        date.addData("SYSTEM", "COLUMNS", "DIET_KIND_1");
        date.addData("SYSTEM", "COLUMNS", "DIET_DATE_1");
        date.addData("SYSTEM", "COLUMNS", "MEAL_CHN_DESC_1");
        date.addData("SYSTEM", "COLUMNS", "STATION_DESC_1");
        date.addData("SYSTEM", "COLUMNS", "PAT_NAME_1");
        date.addData("SYSTEM", "COLUMNS", "PACK_CHN_DESC_1");
        date.addData("SYSTEM", "COLUMNS", "CALORIES_1");
        date.addData("SYSTEM", "COLUMNS", "PACK_1");
        date.addData("SYSTEM", "COLUMNS", "DESCRIPTION_1");

        date.addData("SYSTEM", "COLUMNS", "TITLE_2");
        date.addData("SYSTEM", "COLUMNS", "DIET_KIND_2");
        date.addData("SYSTEM", "COLUMNS", "DIET_DATE_2");
        date.addData("SYSTEM", "COLUMNS", "MEAL_CHN_DESC_2");
        date.addData("SYSTEM", "COLUMNS", "STATION_DESC_2");
        date.addData("SYSTEM", "COLUMNS", "PAT_NAME_2");
        date.addData("SYSTEM", "COLUMNS", "PACK_CHN_DESC_2");
        date.addData("SYSTEM", "COLUMNS", "CALORIES_2");
        date.addData("SYSTEM", "COLUMNS", "PACK_2");
        date.addData("SYSTEM", "COLUMNS", "DESCRIPTION_2");

        printData.setData("TABLE", date.getData());

        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\NSS\\NSSOrderCard.jhw", printData);
    }

    /**
     * ���(TABLE)��ѡ��ı��¼�
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        table.acceptText();
    }

    /**
     * ȫѡ�¼�
     */
    public void onSelectAll() {
        String select_flg = this.getValueString("SELECT_ALL");
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setItem(i, "SELECT_FLG", select_flg);
        }
        table.acceptText();
    }

    /**
     * MR_NO�س��¼�
     */
    public void onMrNoAction() {
        String mr_no = this.getValueString("MR_NO");
        this.setValue("MR_NO", StringTool.fill0(mr_no, PatTool.getInstance().getMrNoLength())); //====cehnxi
        TParm parm = new TParm();
        parm.setData("MR_NO", this.getValue("MR_NO"));
        TParm result = ADMInpTool.getInstance().selectInHosp(parm);
        if (result == null || result.getCount("CASE_NO") <= 0) {
            this.messageBox("�ò�����ǰ����Ժ");
        }
        else {
            setPatInfo(result.getRow(0));
        }
    }

    /**
     * IPD_NO�س��¼�
     */
    public void onIpdNoAction() {
        String mr_no = this.getValueString("IPD_NO");
        this.setValue("IPD_NO", StringTool.fill0(mr_no, PatTool.getInstance().getIpdNoLength())); //====chenxi
        TParm parm = new TParm();
        parm.setData("IPD_NO", this.getValue("IPD_NO"));
        TParm result = ADMInpTool.getInstance().selectInHosp(parm);
        if (result == null || result.getCount("CASE_NO") <= 0) {
            this.messageBox("�ò�����ǰ����Ժ");
        }
        else {
            setPatInfo(result.getRow(0));
        }
    }

    /**
     * ���������Ϣ
     * @param parm TParm
     */
    private void setPatInfo(TParm parm) {
        this.setValue("MR_NO", parm.getValue("MR_NO"));
        this.setValue("IPD_NO", parm.getValue("IPD_NO"));
        Pat pat = Pat.onQueryByMrNo(parm.getValue("MR_NO"));
        this.setValue("PAT_NAME", pat.getName());
        case_no = parm.getValue("CASE_NO");
    }

}
