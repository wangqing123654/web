package com.javahis.ui.sum;

import java.awt.Color;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.ArrayList;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TDS;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

import jdo.adm.ADMTool;
import jdo.sum.SUMNewArrivalTool;
import jdo.sys.Pat;
import jdo.sys.Operator;

/**
 * <p>Title: ���������µ� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: javahis </p>
 *
 * @author ZangJH
 *
 * @version 1.0
 */
public class SUMNewArrivalMainControl extends TControl {

    TTable masterTable; // ���¼�¼table������
    TTable detailTable; // ������ϸtable��ϸ��
    int masterRow = -1;// ��tableѡ���к�
    int detailRow = -1;// ϸtableѡ���к�
    TParm patInfo = new TParm(); // ��������Ϣ
    TParm patMotherInfo = new TParm(); // ������ĸ����Ϣ
    String admType = "I";// �ż�ס��
    String caseNo = ""; // �����
    TParm tprDtl = new TParm(); // ���±�����
    TParm inParm = new TParm();// ���

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        masterTable = (TTable) this.getComponent("masterTable");// ��ʼ�����
        detailTable = (TTable) this.getComponent("detailTable");
        this.callFunction("UI|masterTable|addEventListener", "masterTable->" + TTableEvent.CLICKED,
                          this, "onMasterTableClicked");// table����¼�
        inParm = this.getInputParm();
        if (inParm != null) {
            admType = inParm.getValue("SUM", "ADM_TYPE");
            caseNo = inParm.getValue("SUM", "CASE_NO");
            onQuery();
        }
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        this.setValue("RECTIME", now);// ��¼ʱ��
        this.setValue("TMPTRKINDCODE", "3");// �������ࣺ����
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        parm.setData("ADM_TYPE", admType);
        patInfo = ADMTool.getInstance().getADM_INFO(parm); // ��������Ϣ
        patInfo.setData("ADM_DAYS", 0, ADMTool.getInstance().getAdmDays(caseNo)+1);
        patMotherInfo = ADMTool.getInstance().getMotherInfo(parm); // ������ĸ����Ϣ
        TParm result = SUMNewArrivalTool.getInstance().selectExmDateUser(parm);// ���¼�¼
        // ��ʼ�����¼�¼table
        for (int row = 0; row < result.getCount(); row++) {
            result.setData("EXAMINE_DATE", row, StringTool.getString(StringTool.getDate(result
                    .getValue("EXAMINE_DATE", row), "yyyyMMdd"), "yyyy/MM/dd"));
            if ((row + 0) % 7 == 0) {// ÿ��������һ������ɫ
                masterTable.setRowColor(row - 1, new Color(255, 255, 132));
            }
        }
        masterTable.getTable().repaint();
        masterTable.setParmValue(result);
        if (masterTable.getRowCount() > 0) {
            masterTable.setSelectedRow(masterTable.getRowCount() - 1); // Ĭ��ѡ�����һ��
            onMasterTableClicked(masterTable.getRowCount() - 1); // �ֶ�ִ�е����¼�
        }
    }

    /**
     * ��ת��
     * 
     * @param tprDtl
     *            TParm
     * @return TParm
     */
    public TParm rowToColumn(TParm tprDtl) {
        TParm result = new TParm();
        for (int i = 0; i < tprDtl.getCount(); i++) {
            result.addData("" + i, tprDtl.getData("TEMPERATURE", i)); // ����
            result.addData("" + i, tprDtl.getData("WEIGHT", i)); // ����
        }
        return result;
    }

    /**
     * �½�
     */
    public void onNew() {
        // �õ�������/���ݿ⵱ǰʱ��
        //String today = StringTool.getString(TJDODBTool.getInstance().getDBTime(), "yyyyMMdd");
        
        //add by yangjj 20150317 ����ʱ��������ѡ����ʾ��
    	String today = (String) openDialog("%ROOT%\\config\\sum\\SUMTemperatureDateChoose.x");
        if (today.length() == 0) {
            messageBox("δѡ�����ʱ��");
            return;
        }
        
        for (int i = 0; i < masterTable.getRowCount(); i++) {
            if (today.equals(StringTool.getString(StringTool.getDate(masterTable
                    .getItemString(i, "EXAMINE_DATE"), "yyyy/MM/dd"), "yyyyMMdd"))) {
                this.messageBox("�Ѵ��ڽ�������\n�������½�");
                return;
            }
        }
        // ����һ������
        TParm MData = new TParm();
        MData.setData("EXAMINE_DATE", today.substring(0, 4) + "/" + today.substring(4, 6) + "/"
                + today.substring(6));
        MData.setData("USER_ID", Operator.getName());
        // Ĭ��ѡ��������
        int newRow = masterTable.addRow(MData);
        masterTable.setSelectedRow(newRow);
        onMasterTableClicked(newRow);// �ֶ�ִ�е����¼�
        // �½���ʱ��д�뵱ǰʱ��
        this.setValue("INHOSPITALDAYS", patInfo.getData("ADM_DAYS", 0));// סԺ����
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        this.setValue("RECTIME", now);
        this.setValue("TMPTRKINDCODE", "3");// �������ࣺ����
    }

    /**
     * ���¼�¼table�����¼�
     */
    public void onMasterTableClicked(int row) {
        if (row == 0) { // ��ֻ�е��г���������(��һ��)���������ء��ſɱ༭
            this.callFunction("UI|BORNWEIGHT|setEnabled", true);
        } else {
            this.callFunction("UI|BORNWEIGHT|setEnabled", false);
        }
        masterRow = row; // ��tableѡ���к�
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("CASE_NO", caseNo);
        parm.setData("EXAMINE_DATE", StringTool.getString(StringTool.getDate(masterTable
                .getItemString(row, "EXAMINE_DATE"), "yyyy/MM/dd"), "yyyyMMdd"));
        // =========================��������
        TParm master = SUMNewArrivalTool.getInstance().selectOneDateMst(parm);
        this.clearComponent();// ������
        this.setValue("INHOSPITALDAYS", patInfo.getData("ADM_DAYS", 0)); // סԺ����
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        this.setValue("RECTIME", now);
        this.setValue("TMPTRKINDCODE", "3");// �������ࣺ����
        // =========================ϸ������
        tprDtl = SUMNewArrivalTool.getInstance().selectOneDateDtl(parm);
        if (tprDtl.getCount() == 0) {
            detailTable.removeRowAll();
            detailTable.addRow();
            detailTable.addRow();
//            return;
        } else {
            detailTable.setParmValue(rowToColumn(tprDtl));
        }
        // =========================ϸ����������
        if (master.getInt("BORNWEIGHT", 0) > 0) {
            this.setValue("BORNWEIGHT", master.getData("BORNWEIGHT", 0));// ��������
        } else {
            Pat pat = Pat.onQueryByMrNo(patInfo.getValue("MR_NO", 0));// wanglong add 20130504
            this.setValue("BORNWEIGHT", pat.getNewBodyWeight());// ��������
        }
        this.setValue("URINETIMES", master.getValue("URINETIMES", 0));// С�����
        this.setValue("DRAINTIMES", master.getValue("DRAINTIMES", 0));// ������
        this.setValue("DRAINQUALITY", master.getValue("DRAINQUALITY", 0));// �������
        this.setValue("DRINKQTY", master.getValue("DRINKQTY", 0));// ��ˮ��
        this.setValue("FEEDWAY", master.getValue("FEEDWAY", 0));// ι������
        this.setValue("ADDDARIYQTY", master.getValue("ADDDARIYQTY", 0));// ����Ʒ����
        this.setValue("VOMIT", master.getValue("VOMIT", 0));// Ż��
        this.setValue("BATHEDWAY", master.getValue("BATHEDWAY", 0));// ��ԡ����
        this.setValue("EYE", master.getValue("EYE", 0));// �۾�
        this.setValue("EAR_NOSE", master.getValue("EAR_NOSE", 0));// �Ƕ�
        this.setValue("UNBILICAL", master.getValue("UNBILICAL", 0));// ���
        this.setValue("BUTTRED", master.getValue("BUTTRED", 0));// �κ�
        this.setValue("ICTERUSINDEX", master.getValue("ICTERUSINDEX", 0));// ����ָ��
        this.setValue("ELES", master.getValue("ELES", 0));// ����
        this.setValue("WEIGHT", master.getValue("WEIGHT", 0));
    }

    /**
     * ������ϸtable�����¼�(ͨ������ע�ᷨ)
     */
    public void onDTableFocusChange() {
        // PS:���ڴ��ھ���ת�������⣬����ѡ�е���Ϊ����PARM����
        // ��ʼ��ϸtable��ѡ�е��У�����table���кţ�
        detailRow = detailTable.getSelectedColumn();
        ((TComboBox) this.getComponent("EXAMINESESSION")).setSelectedIndex(detailRow + 1);
        this.setValue("RECTIME", tprDtl.getValue("RECTIME", detailRow));// ��¼ʱ��
        if (tprDtl.getValue("RECTIME", detailRow).equals("")) {
            Timestamp now = TJDODBTool.getInstance().getDBTime();
            this.setValue("RECTIME", now);// ��¼ʱ��
        }
        this.setValue("SPCCONDCODE", tprDtl.getValue("SPCCONDCODE", detailRow));// ���±仯�������
        this.setValue("PHYSIATRICS", tprDtl.getValue("PHYSIATRICS", detailRow));// ������
        this.setValue("TMPTRKINDCODE", tprDtl.getValue("TMPTRKINDCODE", detailRow));// ��������
        if (tprDtl.getValue("TMPTRKINDCODE", detailRow).equals("")) {
            this.setValue("TMPTRKINDCODE", "3");// �������ࣺ����
        }
        this.setValue("NOTPRREASONCODE", tprDtl.getValue("NOTPRREASONCODE", detailRow));// δ��ԭ��
        this.setValue("PTMOVECATECODE", tprDtl.getValue("PTMOVECATECODE", detailRow));// ���˶�̬
        this.setValue("PTMOVECATEDESC", tprDtl.getValue("PTMOVECATEDESC", detailRow));// ���˶�̬��ע
    }

    /**
     * ����
     */
    public boolean onSave() {
        masterTable.acceptText();
        detailTable.acceptText();
        if (masterRow < 0) {
            this.messageBox("��ѡ��һ����¼��");
            return false;
        }
        TParm saveParm = getValueFromUI();
        if (saveParm.getErrCode() < 0) {
            this.messageBox(saveParm.getErrText());
            return false;
        }
        
        //add by yangjj 20150729
        String updateMroRecordSql = " UPDATE " +
        								" mro_record " +
        							" SET " +
        								" NB_OUT_WEIGHT = " +
        									" (SELECT " +
        										" WEIGHT " +
        									" FROM (  " +
        										" SELECT " +
        											" WEIGHT "+
        										" FROM " +
        											" SUM_NEWARRIVALSIGNDTL " +
        										" WHERE " +
        											" CASE_NO = '"+caseNo+"' " +
        											" AND WEIGHT != 0 " +
        										" ORDER BY " +
        											" EXAMINE_DATE DESC, " +
        											" EXAMINESESSION DESC " +
        											" ) " +
        									" WHERE " +
        										" ROWNUM = 1 " +
        									" ) " +
        								" WHERE " +
        									" CASE_NO = '"+caseNo+"'";
        
        // �ж��Ƿ����и����ݲ���/����
        String saveDate = saveParm.getParm("MASET").getValue("EXAMINE_DATE");
        // �õ����table������
        TParm checkDate = new TParm();
        checkDate.setData("CASE_NO", caseNo);
        checkDate.setData("ADM_TYPE", admType);
        checkDate.setData("EXAMINE_DATE", saveDate);
        TParm existParm = SUMNewArrivalTool.getInstance().checkIsExist(checkDate);
        // û�и������ݣ�ֱ�ӱ���
        if (existParm.getCount() == 0) {// �����ڼ�¼���½�
            saveParm.setData("I", true);
            // ����actionִ������
            TParm result =
                    TIOM_AppServer.executeAction("action.sum.SUMNewArrivalAction", "onSave",
                                                 saveParm);
            // ���ñ���
            if (result.getErrCode() < 0) {
                this.messageBox_(result);
                this.messageBox("E0001");
                return false;
            }
            
            //add by yangjj 20150730
            TJDODBTool.getInstance().update(updateMroRecordSql);
            
            this.messageBox("P0001");
            return true;
        }
        // ���ǲ���--update
        saveParm.setData("I", false);
        // ������0˵���Ѿ��д��ڵĸ���������--���ϡ�û����--���¶���
        if (existParm.getData("DISPOSAL_FLG", 0) != null
                && existParm.getData("DISPOSAL_FLG", 0).equals("Y")) {
            if (0 == this.messageBox("", "�������Ѿ����Ϲ���\n�Ƿ���ȷ�����棿", this.YES_NO_OPTION)) {
                // ����actionִ������
                TParm result =
                        TIOM_AppServer.executeAction("action.sum.SUMNewArrivalAction", "onSave",
                                                     saveParm);
                // ���ñ���
                if (result.getErrCode() < 0) {
                    this.messageBox_(result);
                    this.messageBox("E0001");
                    return false;
                }
                
                //add by yangjj 20150730
                TJDODBTool.getInstance().update(updateMroRecordSql);
                
                this.messageBox("P0001");
                return true;
            } else {
                this.messageBox("û�и������ݣ�");
                return true;
            }
        }
        // ֱ��������--DISPOSAL_FLG==null����N
        TParm result =
                TIOM_AppServer.executeAction("action.sum.SUMNewArrivalAction", "onSave", saveParm);
        // ���ñ���
        if (result.getErrCode() < 0) {
            this.messageBox_(result);
            this.messageBox("E0001");
            return false;
        }
        this.messageBox("P0001");
        if (masterRow == 0 && saveParm.getParm("MASET").getDouble("BORNWEIGHT") != 0) {
            String sql =
                    "UPDATE SUM_NEWARRIVALSIGN SET BORNWEIGHT = # WHERE ADM_TYPE = '@' AND CASE_NO = '&'";
            sql = sql.replaceFirst("#", saveParm.getParm("MASET").getValue("BORNWEIGHT"));
            sql = sql.replaceFirst("@", saveParm.getParm("MASET").getValue("ADM_TYPE"));
            sql = sql.replaceFirst("&", saveParm.getParm("MASET").getValue("CASE_NO"));
            result = new TParm(TJDODBTool.getInstance().update(sql));
        }
        
        //add by yangjj 20150730
        TJDODBTool.getInstance().update(updateMroRecordSql);
        
        return true;
    }

    /**
     * ���棺�ӿؼ�������ֵ
     */
    public TParm getValueFromUI() {
        TParm saveData = new TParm();
        TParm masterParm = new TParm();
        TParm detailParm = new TParm();
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        String examineDate =
                StringTool.getString(StringTool.getDate(masterTable.getItemString(masterRow,
                                                                                  "EXAMINE_DATE"),
                                                        "yyyy/MM/dd"), "yyyyMMdd");
        masterParm.setData("ADM_TYPE", admType);
        masterParm.setData("CASE_NO", caseNo);
        masterParm.setData("EXAMINE_DATE", examineDate);// �������
        masterParm.setData("IPD_NO", patInfo.getValue("IPD_NO", 0));
        masterParm.setData("MR_NO", patInfo.getValue("MR_NO", 0));
        masterParm.setData("INHOSPITALDAYS", this.getValueInt("INHOSPITALDAYS"));// סԺ����
        masterParm.setData("OPE_DAYS", "");// ������������ʱû�ã�
        masterParm.setData("ECTTIMES", "");// ĿǰECT��������ʱû�ã�
        masterParm.setData("DISPOSAL_FLG", ""); // ����Ժ��Ϊ������ΪNULL�Ϳ��Ըĳ�--��N�����޸Ķ�Ӧ��SQL
        masterParm.setData("DISPOSAL_REASON", "");
        masterParm.setData("USER_ID", Operator.getID());// ��¼��Ա
        masterParm.setData("BORNWEIGHT", this.getValueDouble("BORNWEIGHT"));// ��������
        masterParm.setData("URINETIMES", this.getValueDouble("URINETIMES"));// С�����
        masterParm.setData("DRAINTIMES", this.getValueDouble("DRAINTIMES"));// ������
        masterParm.setData("DRAINQUALITY", this.getValue("DRAINQUALITY"));// �������
        masterParm.setData("DRINKQTY", this.getValueInt("DRINKQTY"));// ��ˮ��
        masterParm.setData("FEEDWAY", this.getValue("FEEDWAY"));// ι������
        masterParm.setData("ADDDARIYQTY", this.getValueDouble("ADDDARIYQTY"));// ����Ʒ����
        masterParm.setData("VOMIT", this.getValueDouble("VOMIT"));// Ż��
        masterParm.setData("BATHEDWAY", this.getValue("BATHEDWAY"));// ��ԡ����
        masterParm.setData("EYE", this.getValue("EYE"));// �۾�
        masterParm.setData("EAR_NOSE", this.getValue("EAR_NOSE"));// �Ƕ�
        masterParm.setData("UNBILICAL", this.getValue("UNBILICAL"));// ���
        masterParm.setData("BUTTRED", this.getValue("BUTTRED"));// �κ�
        masterParm.setData("ICTERUSINDEX", this.getValue("ICTERUSINDEX"));// ����ָ��
        masterParm.setData("ELES", this.getValue("ELES"));// ����
        masterParm.setData("WEIGHT", this.getValueString("WEIGHT"));// ����
        masterParm.setData("OPT_USER", Operator.getID());
        masterParm.setData("OPT_DATE", now);
        masterParm.setData("OPT_TERM", Operator.getIP());
        // ѡ�е�ʱ���
        String columnIndex = this.getValueString("EXAMINESESSION");
//        this.messageBox("" + columnIndex);
        for (int i = 0; i < 6; i++) {// ʱ����6��
            TParm oneParm = new TParm();
            oneParm.setData("ADM_TYPE", admType);
            oneParm.setData("CASE_NO", caseNo);
            oneParm.setData("EXAMINE_DATE", examineDate);
            oneParm.setData("EXAMINESESSION", i);
            if (("" + i).equals(columnIndex)) {
                oneParm.setData("RECTIME", this.getText("RECTIME"));// ��¼ʱ��
                oneParm.setData("SPCCONDCODE", this.getValue("SPCCONDCODE"));// ���±仯�������
                oneParm.setData("PHYSIATRICS", this.getValue("PHYSIATRICS"));// ������
                oneParm.setData("TMPTRKINDCODE", this.getValue("TMPTRKINDCODE"));// ��������
                oneParm.setData("NOTPRREASONCODE", this.getValue("NOTPRREASONCODE"));// δ��ԭ��
                oneParm.setData("PTMOVECATECODE", this.getValue("PTMOVECATECODE"));// ���˶�̬
                if (!StringUtil.isNullString(this.getValueString("PTMOVECATECODE"))
                        && StringUtil.isNullString(this.getValueString("PTMOVECATEDESC"))) {
                    TParm errParm = new TParm();
                    errParm.setErr(-1, "����д���˶�̬��ע");
                    return errParm;
                }
                oneParm.setData("PTMOVECATEDESC", this.getValue("PTMOVECATEDESC"));// ���˶�̬��ע
            } else {
                oneParm.setData("RECTIME", tprDtl.getValue("RECTIME", i));// ��¼ʱ��
                oneParm.setData("SPCCONDCODE", tprDtl.getValue("SPCCONDCODE", i));// ���±仯�������
                oneParm.setData("PHYSIATRICS", tprDtl.getValue("PHYSIATRICS", i));// ������
                oneParm.setData("TMPTRKINDCODE", tprDtl.getValue("TMPTRKINDCODE", i).equals("")// wanglong
                                                                                               // modify
                                                                                               // 20140428
                        ? this.getValue("TMPTRKINDCODE") : tprDtl.getValue("TMPTRKINDCODE", i));
                oneParm.setData("NOTPRREASONCODE", tprDtl.getValue("NOTPRREASONCODE", i));// δ��ԭ��
                oneParm.setData("PTMOVECATECODE", tprDtl.getValue("PTMOVECATECODE", i));// ���˶�̬
                oneParm.setData("PTMOVECATEDESC", tprDtl.getValue("PTMOVECATEDESC", i));// ���˶�̬��ע
            }
            // �õ�table�ϵ�������
            oneParm.setData("TEMPERATURE", TCM_Transform.getDouble(detailTable.getValueAt(0, i)));// ����
            oneParm.setData("WEIGHT", TCM_Transform.getInt(detailTable.getValueAt(1, i)));// ����
            oneParm.setData("USER_ID", Operator.getID());
            oneParm.setData("OPT_USER", Operator.getID());
            oneParm.setData("OPT_DATE", now);
            oneParm.setData("OPT_TERM", Operator.getIP());
            detailParm.setData(i + "PARM", oneParm.getData());
            detailParm.setCount(i + 1);
        }
        saveData.setData("MASET", masterParm.getData());
        saveData.setData("DETAIL", detailParm.getData());
        return saveData;
    }

    /**
     * ���
     */
    public void onClear() {
        // ��������ȫ�ֱ���
        masterRow = -1;
        detailRow = -1;
        this.clearComponent();
        detailTable.removeRowAll();
        onQuery();// ִ�в�ѯ
    }

    /**
     * ������
     */
    public void clearComponent() {
        // �����ϰ벿��
        this.clearValue("EXAMINESESSION;RECTIME;"// INHOSPITALDAYSסԺ���������
                + "SPCCONDCODE;PHYSIATRICS;"// TMPTRKINDCODE�������಻���
                + "NOTPRREASONCODE;PTMOVECATECODE;PTMOVECATEDESC");
        // �����°벿��
        this.clearValue("BORNWEIGHT;URINETIMES;DRAINTIMES;DRAINQUALITY;DRINKQTY;FEEDWAY;ADDDARIYQTY;VOMIT;"
                + "BATHEDWAY;EYE;EAR_NOSE;UNBILICAL;BUTTRED;ICTERUSINDEX;ELES;WEIGHT");
    }

    /**
     * ��������
     */
    public void onDefeasance() {
        int selRow = masterTable.getSelectedRow();
        if (selRow < 0) {
            this.messageBox("��ѡ���������ݣ�");
            return;
        }
        String value = (String) this.openDialog("%ROOT%\\config\\sum\\SUMDefeasance.x");
        // �õ�ѡ���е�EXAMINE_DATE
        String examineDate =
                StringTool.getString(StringTool.getDate(masterTable.getItemString(selRow,
                                                                                  "EXAMINE_DATE"),
                                                        "yyyy/MM/dd"), "yyyyMMdd");
        String defSel =
                "SELECT * FROM SUM_NEWARRIVALSIGN WHERE ADM_TYPE = '" + admType
                        + "' AND CASE_NO = '" + caseNo + "' AND EXAMINE_DATE = '" + examineDate
                        + "'";
        TDS defData = new TDS();
        defData.setSQL(defSel);
        defData.retrieve();
        defData.setItem(0, "DISPOSAL_REASON", value);
        defData.setItem(0, "DISPOSAL_FLG", "Y");
        if (!defData.update()) {
            this.messageBox("����ʧ�ܣ�");
            return;
        }
        this.messageBox("���ϳɹ���");
        onClear();
    }

    /**
     * ��ӡ
     */
    public void onPrint() {
        if (masterTable.getRowCount() <= 0) {
            this.messageBox("û�д�ӡ���ݣ�");
            return;
        }
        
        
        //add by yangjj 20151026
        TParm prtForSheetParm = new TParm();
        // ��ô�ӡ������
        TParm parmDate = new TParm();
        // ��Ժ����ʱ��
        Timestamp inDate = patInfo.getTimestamp("IN_DATE", 0);
        parmDate.setData("IN_DATE", inDate);
        TParm value = (TParm) this.openDialog("%ROOT%\\config\\sum\\SUMChoiceDate.x", parmDate);
        
        System.out.println("value:"+value);
        if (value == null) {
            return ;
        }
        
        
        
        Timestamp date1 = value.getTimestamp("START_DATE");
        Timestamp date2 = value.getTimestamp("END_DATE");
        Timestamp temp = date1;
        TParm value1 = new TParm();
        Timestamp temp1 = new Timestamp(temp.getTime());
        int pageNo = 1;
		for(;;){
			long time = temp.getTime();
			time += 6*24*60*60*1000;
			temp = new Timestamp(time);
			
			System.out.println(temp.getTime()+" "+date2.getTime() + " "+(temp.getTime() >= date2.getTime()));
			if(temp.getTime() >= date2.getTime()){
				value1.setData("START_DATE", temp1);
				value1.setData("END_DATE", date2);
				value1.setData("SUM_JHW", "");
				TParm printData = getValueForPrt(temp1,date2,value1,pageNo); // �����ӡ����
	            if (printData.getData("STOP") != null) {
	                 this.messageBox(printData.getValue("STOP"));
	                 return;
	            }
	            this.openPrintDialog("%ROOT%\\config\\prt\\sum\\SUMNewArrival_PrtSheet.jhw", printData);
				return;
			}else{
				value1.setData("START_DATE", temp1);
				value1.setData("END_DATE", temp);
				value1.setData("SUM_JHW", "");
				 TParm printData = getValueForPrt(temp1,temp,value1,pageNo); // �����ӡ����
	             if (printData.getData("STOP") != null) {
	                 this.messageBox(printData.getValue("STOP"));
	                 return;
	             }
	             this.openPrintDialog("%ROOT%\\config\\prt\\sum\\SUMNewArrival_PrtSheet.jhw", printData);
	             long time1 = temp.getTime();
	 			 time1 += 24*60*60*1000;
	 			 temp1 = new Timestamp(time1);
	 			 time = temp.getTime();
				 time += 24*60*60*1000;
				 temp = new Timestamp(time);
				 pageNo++;
			}
		}
       
    }
    

    /**
     * �õ�UI�ϵĲ�������ӡ����
     * 
     * @return TPram
     */
    private TParm getValueForPrt(Timestamp startDate,Timestamp endDate,TParm value,int pageNo) {
    	TParm prtForSheetParm = new TParm();
    	
    	//modify by yangjj 20151026
    	/*
        // ��ô�ӡ������
        TParm parmDate = new TParm();
        // ��Ժ����ʱ��
        Timestamp inDate = patInfo.getTimestamp("IN_DATE", 0);
        parmDate.setData("IN_DATE", inDate);
        TParm value = (TParm) this.openDialog("%ROOT%\\config\\sum\\SUMChoiceDate.x", parmDate);
        if (value == null) {
            prtForSheetParm.setData("STOP", "ȡ����ӡ��");
            return prtForSheetParm;
        }
        */
        // �õ�ѡ��ʱ��֮��ġ������+1===>��ӡ������
        
        //modify by yangjj 20151026
        int differCount =
                StringTool.getDateDiffer(endDate,startDate) + 1;
        System.out.println(startDate+" "+endDate+" "+differCount);
        if (differCount <= 0) {
            prtForSheetParm.setData("STOP", "��ѯ�������");
            return prtForSheetParm;
        }
        // ��������������
        Vector tprSign = getVitalSignDate(value);
        // ��ӡ�����㷨��������ת��������
        prtForSheetParm = dataToCoordinate(tprSign, differCount, pageNo);
        String stationCode = patInfo.getValue("STATION_CODE", 0);
        String mrNo = patInfo.getValue("MR_NO", 0);
        // ͨ��MR_NO�õ��Ա�
        Pat pat = Pat.onQueryByMrNo(mrNo);
        String sex = pat.getSexString();
        String ipdNo = patInfo.getValue("IPD_NO", 0);
        String bedNo = patInfo.getValue("BED_NO", 0);
        String motherName = (String) patMotherInfo.getValue("PAT_NAME", 0);
        String dept = patInfo.getValue("DEPT_CODE", 0);
        prtForSheetParm.setData("MR_NO", "TEXT", mrNo);
        prtForSheetParm.setData("IPD_NO", "TEXT", ipdNo);
        prtForSheetParm.setData("SEX", "TEXT", pat.getSexString());
        prtForSheetParm.setData("PAT_NAME", "TEXT", pat.getName());
        prtForSheetParm.setData("BIRTH", "TEXT", StringTool.getString(pat.getBirthday(), "yyyy/MM/dd"));
        prtForSheetParm.setData("BED_NO", "TEXT", bedNo);
        prtForSheetParm.setData("STATION",
                                "TEXT",
                                StringUtil.getDesc("SYS_STATION", "STATION_DESC", "STATION_CODE='"
                                        + stationCode + "'"));
        prtForSheetParm.setData("NAME", "TEXT", motherName);
        prtForSheetParm.setData("SEX", "TEXT", sex);
        prtForSheetParm.setData("DEPT",
                                "TEXT",
                                StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC", "DEPT_CODE='"
                                        + dept + "'"));
        prtForSheetParm.setData("PAT_NAME",
                                "TEXT",
                                StringUtil.getDesc("SYS_PATINFO", "PAT_NAME", "MR_NO='" + mrNo
                                        + "'"));
        return prtForSheetParm;
    }

    /**
     * �õ���Ҫ��ӡ��������
     * 
     * @param date
     *            TParm
     */
    public Vector getVitalSignDate(TParm date) {
        Vector tprSign = new Vector();
        date.setData("ADM_TYPE", admType);
        date.setData("CASE_NO", caseNo);
        // ���£���������
        TParm newArrivalMstParm = SUMNewArrivalTool.getInstance().selectdataMst(date);
        TParm newArrivalDtlParm = SUMNewArrivalTool.getInstance().selectdataDtl(date);
        // ������ǽ����0-������Ϣ 1-ϸ����Ϣ
        tprSign.add(newArrivalMstParm);
        tprSign.add(newArrivalDtlParm);
        return tprSign;
    }

    /**
     * ��ӡ�����㷨��������ת��������
     * 
     * @param tprSign
     *            Vector ��Ҫ����
     * @param differCount
     *            int Ҫ��ӡ��������endDate-startDate��
     */
    public TParm dataToCoordinate(Vector tprSign, int differCount,int pageNo) {
        TParm mainPrtData = new TParm();
        // �õ���һ���������Ϊ��׼
        String getFistDateSQL =
                "SELECT * FROM SUM_NEWARRIVALSIGN WHERE ADM_TYPE='" + admType + "' AND CASE_NO='"
                        + caseNo + "' AND BORNWEIGHT IS NOT NULL"; // �������ز�Ϊ�յ�
        TParm firstData = new TParm(TJDODBTool.getInstance().select(getFistDateSQL));
        String mrNo = patInfo.getValue("MR_NO", 0);
        // ͨ��MR_NO�õ��Ա�
        Pat pat = Pat.onQueryByMrNo(mrNo);
        // ����ʱ��
        Timestamp bornDate =pat.getBirthday();
        // �õ�����������--��׼����
        int bornWeight = firstData.getInt("BORNWEIGHT", 0);
        // ��ϸ������
        TParm master = (TParm) tprSign.get(0); // ���������
        TParm detail = (TParm) tprSign.get(1); // (����)
        // �õ��������������
        int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0, c7 = 0, c8 = 0, c9 = 0, c10 = 0, c11 =
                0, c12 = 0, c13 = 0, c14 = 0;
        // ���������ó�����
        int countWord = 0;
        // ���ѡ�����������>����/6˵�����������ݣ�������/6Ϊ���¡�����--���ص���Ч����/6
        int newDates = detail.getCount("WEIGHT") / 6;
        if (differCount > newDates) differCount = newDates;
        // ���ݣ�����/7���õ���Ҫ������ҳ��
        //int pageCount = (differCount-1) / 7 + 1;
        
        //modify by yangjj 20151026
        int pageCount = 1;
        
        TParm controlPage = new TParm();
        // ������ҳ
        for (int i = 1; i <= pageCount; i++) {
            ArrayList dotList_T = new ArrayList();
            ArrayList dotList_W = new ArrayList();
            ArrayList dotList_P = new ArrayList();
            ArrayList lineList_W = new ArrayList();
            ArrayList lineList_T = new ArrayList();
            ArrayList lineList_P = new ArrayList();
            // ����ҳ��
            //controlPage.addData("PAGE", "" + i);
            
            //modify by yangjj 20151102
            controlPage.addData("PAGE", "" + pageNo);
            
            // Ƕ����ѭ��������----------------------start-------------------------
            int date = differCount - (i * 7) % 7;
            int xT = -1;
            int yT = -1;
            int xW = -1;
            int yW = -1;
            for (int j = 1; j <= date; j++) {
                // ���ڲ����ʱ��
                for (int exa = 1; exa <= 6; exa++) {
                    // �õ�����-------------------start---------------------------
                    double temper = detail.getDouble("TEMPERATURE", exa + (j - 1) * 6 - 1);
                    // ��ΪNULL��ʱ��Ϊ������������Զ�ת����0����ô��Ϊ0��ʱ�򲻻���
//                    if (temper == 0.0) continue;
                    
                    //modify by yangjj 20150319
                    int temper_flag = 0;
                    
                    // ���¶�<=35��ʱ��д�����²�����
                    if (temper>0&&temper <35) {
                        // ��ͱ߽�35��
                      
                        controlPage.addData("NORAISE" + (exa + (j - 1) * 6), "���²���");
                        
                    }else if(temper>=35){
                    	//add by yangjj 20150319
                        temper_flag = 1;
                    }
                    int temperHorizontal = countHorizontal(j, exa);
                	int temperVertical = (int) (getVertical(temper) + 0.5); // ȡ��
                    
                	//modify by yangjj 20150319
                    if(temper_flag == 1){
                    	// �õ����µĺ������꣨�㣩
                    	
                    	// �õ�һ���������
                    	int dataTemper[] = new int[]{temperHorizontal, temperVertical, 6, 6, 4 };
                    	// �������е�
                    	dotList_T.add(dataTemper);
                    }
                    
                    // --------------------------end-----------------------------
                    // �õ����صĵ�----------------start--------------------------
                    int weight = detail.getInt("WEIGHT", exa + (j - 1) * 6 - 1);
                    // ��ΪNULL��ʱ��Ϊ������������Զ�ת����0����ô��Ϊ0��ʱ�򲻻���
                    
                    //add by yangjj 20150319
                    int weight_flag = 0;
                    //modify by yangjj 20150319
                    if (weight != 0){
                    	weight_flag = 1;
                    }
                    
                    int weightHorizontal = countHorizontal(j, exa);
                    int weightVertical = (int) (getVertical(weight, bornWeight) + 0.5); // ȡ��
                    //modify by yangjj 20150319
                    if(weight_flag == 1){
                    	// �õ����صĺ������꣨�㣩
                        
                        // �õ�һ���������
                        int dataWeight[] = new int[]{weightHorizontal, weightVertical, 6, 6, 4 };
                        // �������е�
                        dotList_W.add(dataWeight);
                    }
                    
                    // ---------------------------end----------------------------
                    // �õ�������-----------------start--------------------------
                    String tempPhsi = detail.getValue("PHYSIATRICS", exa + (j - 1) * 6 - 1);
                    if (!StringUtil.isNullString(tempPhsi)) {
                        // �õ��������͵�
                        double phsiatrics = TCM_Transform.getDouble(tempPhsi);
                        if (phsiatrics <= 35) {
                            // ��ͱ߽�35��
                            phsiatrics = 35;
                        }
                        // �õ����µĺ������꣨�㣩
                        int phsiHorizontal = countHorizontal(j, exa);
                        int phsiVertical = (int) (getVertical(phsiatrics) + 0.5); // ȡ��
                        // �õ�һ���������
                        int dataPhsi[] = new int[]{phsiHorizontal, phsiVertical, 6, 6, 6 };
                        // �������е�
                        dotList_P.add(dataPhsi);
                        // �õ���������-----------start--------------------------
                        int dataTempLine[] =
                                new int[]{temperHorizontal + 3, temperVertical + 3,
                                        phsiHorizontal + 3, phsiVertical + 3, 1 };
                        lineList_P.add(dataTempLine);
                    }
                    // ----------------------------end---------------------------
                    // �õ�Ϊ����ԭ��
                    String not =
                            nullToEmptyStr(detail.getValue("NOTPRREASONCODE",
                                                           (exa + (j - 1) * 6) - 1));
                    // �õ����µ���----------------start--------------------------
                    //modify by yangjj 20150319
                    if(temper_flag == 1){
                    	if (xT != -1 && yT != -1 && StringUtil.isNullString(not)) {
                            int dataTempLine[] =
                                    new int[]{xT + 3, yT + 3, temperHorizontal + 3, temperVertical + 3,
                                            1 };
                            lineList_T.add(dataTempLine);
                        }
                        xT = temperHorizontal;
                        yT = temperVertical;
                    }
                    
                    // --------------------------end----------------------------
                    // �õ����ص���----------------start--------------------------
                    
                    //modify by yangjj 20150319
                    if(weight_flag == 1){
                    	if (xW != -1 && yW != -1 && StringUtil.isNullString(not)) {
                            int dataWeightLine[] =
                                    new int[]{xW + 3, yW + 3, weightHorizontal + 3, weightVertical + 3,
                                            1 };
                            lineList_W.add(dataWeightLine);
                        }
                        xW = weightHorizontal;
                        yW = weightVertical;
                    }
                    
                    // --------------------------end----------------------------
                    // ���˶�̬��Ϣ----------------start--------------------------
                    String ptMoveCode =
                            nullToEmptyStr(detail.getValue("PTMOVECATECODE", exa + (j - 1) * 6 - 1));
                    if (!StringUtil.isNullString(ptMoveCode)) {
                        String ptMoveDesc =
                                nullToEmptyStr(detail.getValue("PTMOVECATEDESC", exa + (j - 1) * 6
                                        - 1));
                        controlPage.addData("MOVE" + (exa + (j - 1) * 6), ptMoveCode + "||"
                                + ptMoveDesc);
                    }
                }
                // �õ�����-------------------------start-------------------------
                String tenmpDate = master.getValue("EXAMINE_DATE", countWord++);
				String fomatDate = "";
				
				
				if (countWord - 1 == 0) {
					fomatDate = tenmpDate.substring(0, 4) + "."
							+ tenmpDate.substring(4, 6) + "."
							+ tenmpDate.substring(6);
					controlPage.addData("DATE" + j, fomatDate);
				} else {
					String tenmpDateForward = master.getValue("EXAMINE_DATE", countWord - 2);
					if (!tenmpDateForward.substring(2, 4).equals(
							tenmpDate.substring(2, 4)))
						fomatDate = tenmpDate.substring(0, 4) + "."
								+ tenmpDate.substring(4, 6) + "."
								+ tenmpDate.substring(6);
					else if (!tenmpDateForward.substring(4, 6).equals(
							tenmpDate.substring(4, 6)))
						fomatDate = tenmpDate.substring(4, 6) + "."
								+ tenmpDate.substring(6);
					else
						fomatDate = tenmpDate.substring(6);
					controlPage.addData("DATE" + j, fomatDate);
				}
                // �õ��ó���������������-�������ӣ�-------------------------------
                // int dates = getBornDateDiffer(tenmpDate, bornDate);
                String dates = getBornDateDiffer(tenmpDate, StringTool.getTimestampDate(bornDate));
                // controlPage.addData("BRON" + j, dates == 0 ? "" : dates);
                controlPage.addData("BRON" + j, dates);
                // �õ��������������----------------------start------------------------
                controlPage.addData("L1" + j, master.getData("URINETIMES", c1++));
                controlPage.addData("L2" + j, master.getData("DRAINTIMES", c2++));
                controlPage.addData("L3" + j, master.getData("DRAINQUALITY", c3++));
                controlPage.addData("L5" + j, master.getData("FEEDWAY", c5++));
                controlPage.addData("L6" + j, master.getData("ADDDARIYQTY", c6++));
                controlPage.addData("L7" + j, master.getData("VOMIT", c7++));
                controlPage.addData("L8" + j, master.getData("WEIGHT", c8++));
            }
            // ���µ�
            int pageDataForT[][] = new int[dotList_T.size()][5];
            for (int j = 0; j < dotList_T.size(); j++)
                pageDataForT[j] = (int[]) dotList_T.get(j);
            // ���ص�
            int pageDataForW[][] = new int[dotList_W.size()][5];
            for (int j = 0; j < dotList_W.size(); j++)
                pageDataForW[j] = (int[]) dotList_W.get(j);
            // �����µ�
            int pageDataForP[][] = new int[dotList_P.size()][5];
            for (int j = 0; j < dotList_P.size(); j++)
                pageDataForP[j] = (int[]) dotList_P.get(j);
            // ������
            int pageDataForTLine[][] = new int[lineList_T.size()][5];
            for (int j = 0; j < lineList_T.size(); j++)
                pageDataForTLine[j] = (int[]) lineList_T.get(j);
            // ������
            int pageDataForWLine[][] = new int[lineList_W.size()][5];
            for (int j = 0; j < lineList_W.size(); j++)
                pageDataForWLine[j] = (int[]) lineList_W.get(j);
            // ������
            int pageDataForPLine[][] = new int[lineList_P.size()][5];
            for (int j = 0; j < lineList_P.size(); j++)
                pageDataForPLine[j] = (int[]) lineList_P.get(j);
            controlPage.addData("PAGE", "" + i);
            controlPage.addData("TEMPDOT", pageDataForT);
            controlPage.addData("WEIGHTDOT", pageDataForW);
            controlPage.addData("PHSIDOT", pageDataForP);
            controlPage.addData("TEMPLINE", pageDataForTLine);
            controlPage.addData("WEIGHTLINE", pageDataForWLine);
            controlPage.addData("PHSILINE", pageDataForPLine);
            // ----------------------------end----------------------------------
            // ��������
            controlPage.addData("BRONWEIGHT", bornWeight + "g");
        }
        // ����ҳ��
        controlPage.setCount(pageCount);
        controlPage.addData("SYSTEM", "COLUMNS", "PAGE");
        mainPrtData.setData("TABLE", controlPage.getData());
        return mainPrtData;
    }

    /**
     * �õ�����������Ĳ�
     * 
     * @param nowDate
     *            String
     * @return int
     */
    public String getBornDateDiffer(String date, Timestamp bornDate) {
        Timestamp nowDate = StringTool.getTimestamp(date, "yyyyMMdd");
//        return OdiUtil.getInstance().showAge(bornDate, nowDate);
        return (StringTool.getDateDiffer(nowDate, bornDate)+1)+"";
    }

    /**
     * �����������λ��
     * 
     * @param date
     *            int
     * @param examineSession
     *            int
     * @return int
     */
    private int countHorizontal(int date, int examineSession) {
        int adaptX = 7;
        return 10 * ((date - 1) * 6 + examineSession) - adaptX;
    }

    /**
     * �������������µ�����������--����
     * 
     * @param value
     *            double
     * @return double
     */
    private double getVertical(double value) {
        int adaptY = 8;
        return (225 - countVerticalForT(value, 40, 35, 25) * 7) - adaptY;
    }

    /**
     * �������������µ�����������--����
     * 
     * @param value
     *            double
     * @return double
     */
    private double getVertical(double value, double bronWeight) {
        int adaptY = 2;
        return (50 - countVerticalForW(value - bronWeight, 500, 0, 25) * 7) + adaptY;
    }

    /**
     * ����������λ��--����
     * 
     * @param value
     *            int ���ݿ��м�¼������
     * @param topValue
     *            int ���������ֵ--��
     * @param butValue
     *            int �������С��ֵ--��
     * @param level
     *            int �������С֮����ж��ٵȼ�-����
     * @return int
     */
    private double countVerticalForT(double value, double topValue, double butValue, int level) {
        return (value - butValue) / ((topValue - butValue) / level) - 1;
    }

    private double countVerticalForW(double value, double topValue, double butValue, int level) {
        return value / ((topValue - butValue) / level) + 1;
    }

    /**
     * TParm�С�null����תΪ���ַ���
     * 
     * @param str
     * @return
     */
    public String nullToEmptyStr(String str) {
        if (str == null || str.equalsIgnoreCase("null")) {
            return "";
        }
        return str;
    }

    /**
     * �ر��¼�
     * 
     * @return boolean
     */
    public boolean onClosing() {
        // switch (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�?", this.YES_NO_CANCEL_OPTION)) {
        // case 0:
        // if (!onSave())
        // return false;
        // break;
        // case 1:
        // break;
        // case 2:
        // return false;
        // }
        return true;
    }

    public static void main(String[] args) {
        // JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("sum\\SUMNewArrival.x");
    }

}
