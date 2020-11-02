package com.javahis.ui.sys;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import jdo.sys.SYSDictionaryTool;
import jdo.sys.SYSNewRegionTool;
import jdo.sys.SYSPositonTool;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TPasswordField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title:�û�����
 * </p>
 *
 * <p>
 * Description:�û�����
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.6.08
 * @version 1.0
 */
public class SYSOperatorControl extends TControl {
    /**
     * ��ʼ������
     */
   // private boolean pwdMessageShow = false; //������Ϣ����ʾ״̬===pangben modify 20110526
    public void onInit() {
        //============pangben modify 20110429 start
        //��ѯ�����������Ƿ���ʾУ��ǿ�� ��������ݿ�PWD_STRENGTH�ֶ�Ϊ'N'������ʾУ�飬������ʾ
        TParm parm = null;
        if (null != Operator.getRegion() && !"".equals(Operator.getRegion())) {

            String pwdSQL =
                    "SELECT PWD_STRENGTH FROM SYS_REGION WHERE REGION_CODE='" +
                    Operator.getRegion() + "'";
            parm = new TParm(TJDODBTool.getInstance().select(pwdSQL));
        } else {
            parm = new TParm();
            parm.setData("PWD_STRENGTH",0, "N");
        }
        if ("N".equals(parm.getValue("PWD_STRENGTH", 0))) {
            callFunction("UI|pwd_LBL|setVisible", false);
            callFunction("UI|PWD_POOFSTH|setVisible", false);
        } else {
            callFunction("UI|pwd_LBL|setVisible", true);
            callFunction("UI|PWD_POOFSTH|setVisible", true);
        }
        this.setValue("pwd_LBL", ""); //��ʦ���治��ʾ
        //============pangben modify 20110429 stop
        // ��ʼ��Ȩ��
        if (!getPopedem("delPopedem")) {
            ((TMenuItem) getComponent("delete")).setVisible(false);
        }
        // ��TABLEDEPT�е�CHECKBOX��������¼�
        callFunction("UI|TABLEDEPT|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableDeptCheckBoxClicked");
        // ��OPT_CLINICAREA_TABLE�е�CHECKBOX��������¼�
        callFunction("UI|OPT_CLINICAREA_TABLE|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableOPT_ClinicareaCheckBoxClicked");
        // ��OPT_STATION_TABLE�е�CHECKBOX��������¼�
        callFunction("UI|OPT_STATION_TABLE|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableOPT_StationCheckBoxClicked");
        callFunction("UI|OPT_STATION_TABLE|addEventListener",
                TTableEvent.CHECK_BOX_CLICKED, this,
                "onTableOPT_StationCheckBoxClicked");
        // ��ʼ��ʹ�������������б�
        showOperatorDept();
        // ��ʼ��ʹ�������������б�
        showOperatorClinicarea();
        // ��ʼ��ʹ�������������б�
        showOperatorStation();
        // ����ɾ����ť״̬
        callFunction("UI|delete|setEnabled", false);
        // ���ñ��水ť״̬
        callFunction("UI|save|setEnabled", false);
        // ����������ť״̬
        callFunction("UI|new|setEnabled", true);
        // ��ʼ����ǰʱ��
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("ACTIVE_DATE", date);
        this.setValue("END_DATE", "9999/12/31");
        //============pangben modify 20110328 start
        //����ʧЧ�����趨
        this.setValue("PWD_ENDDATE", "9999/12/31");
        //============pangben modify 20110328 stop
        // TABLE
        TTable table = getTable("TABLE");
        table.removeRowAll();
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSOperator(Operator.getRegion())); //============pangben modify 20110524
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
        int seq = getMaxSeq(table.getDataStore(), "SEQ", table.getDataStore()
                            .isFilter() ? table.getDataStore().FILTER : table
                            .getDataStore().PRIMARY);
        setValue("SEQ", seq);
        TTextField a=(TTextField)this.getComponent("USER_ID");
//      System.out.println("aseeee:"+a.getFocusLostAction());

    }
    /**
     * ��������
     */
    public void onExport() {
    	TTable table = (TTable) getComponent("TABLE");
    	
    	TDataStore td = table.getDataStore();
    	String buff = td.isFilter()? td.FILTER : td.PRIMARY;
    	int rows  = td.rowCount();
    	TParm parm = new TParm();
    	int count = 0;
    	TParm rowParm = null;
    	Map<String,String> map = SYSDictionaryTool.getInstance().getSexMap();
    	Map<String,String> map1 = SYSPositonTool.getInstance().getPosCodeMap();
    	Map<String,String> map2 = SYSDictionaryTool.getInstance().getRoleMap();
    	//this.messageBox(map1 + "");
    	for(int i=0; i < rows; i++){
    		rowParm = td.getRowParm(i,buff);
    		rowParm.setData("REGION_CODE",this.getTComboBox("REGION_CODE").getSelectedName());
    		rowParm.setData("SEX_CODE",map.get(rowParm.getValue("SEX_CODE")));
    		rowParm.setData("POS_CODE",map1.get(rowParm.getValue("POS_CODE")));
    		rowParm.setData("ROLE_ID",map2.get(rowParm.getValue("ROLE_ID")));
    		parm.setRowData(count, rowParm);
    		count++;
    	}
    	this.setValue("UNIT_CODE", "");
    	parm.setCount(count);
    	if (parm.getCount()<= 0) {
    		this.messageBox("û�л������");
    		return;
    	}
    	//System.out.println(parm);
    	ExportExcelUtil.getInstance().exportExcel(table.getHeader(),table.getParmMap(),parm, "�û���Ϣ");

    }
    
    /**
     * TComboBox
     * @param tagName
     * @return
     */
    public TComboBox getTComboBox(String tagName){
    	return (TComboBox)this.getComponent(tagName);
    }
    

    /**
     * ��ѯ����
     */
    public void onQuery() {
        if (getValueString("DEPT_CODE").length() == 0) {
            onFilter();
            TTable table = getTable("TABLE");
            if (table.getRowCount() > 0) {
                table.setSelectedRow(0);
                this.onTableClicked();
            }
            return;
        }
        onComboBoxSelected();
      //  pwdMessageShow = false; //=====pangben modify 20110526
    }

    /**
	 * ��շ���
	 */
    public void onClear() {
        // ============pangben modify 20110328 start
        // ����ǿ�ȱ�ǩ����ʾ
        this.setValue("pwd_LBL", "");
        //============pangben modify 20110328 stop
        String clearObj =
                "USER_ID;USER_NAME;DEPT_CODE;REGION_CODE;USER_ENG_NAME;"
                + "LCS_CLASS_CODE;LCS_NO;EFF_LCS_DATE;END_LCS_DATE;"
                + "POS_CODE;ROLE_ID;USER_PASSWORD;ACTIVE_DATE;END_DATE;"
                + "RCNT_LOGIN_DATE;RCNT_LOGOUT_DATE;RCNT_IP;PUB_FUNCTION;"
                + "PY1;PY2;SEQ;FOREIGNER_FLG;ID_NO;DESCRIPTION;FULLTIME_FLG;UKEY_FLG;"//xueyf add UKEY_FLG
                +
                "CTRL_FLG;E_MAIL;SEX_CODE;ABNORMAL_TIMES;OPERATOR_STATION;OPT_CLINICAREA_TABLE;COST_CENTER_CODE;TEL1;TEL2;IS_OUT_FLG";
        clearValue(clearObj);
        // ��տ�����Ϣ��
        getTable("TABLEDEPT").removeRowAll();
        // ���֤����Ϣ��
        getTable("TABLELISCENSE").removeRowAll();
        // ���ʹ��������������Ϣ��
        getTable("OPT_CLINICAREA_TABLE").removeRowAll();
        // ���ʹ��������������Ϣ��
        getTable("OPT_STATION_TABLE").removeRowAll();
        // ��ʼ��ʹ�������������б�
        showOperatorDept();
        // ��ʾ���в�����Ա�б�
        showOperator();
        // ��ʼ��ʹ�������������б�
        showOperatorClinicarea();
        // ��ʼ��ʹ�������������б�
        showOperatorStation();
        // ��ʼ��ҳ��״̬
        callFunction("UI|USER_ID|setEnabled", true);
        callFunction("UI|new|setEnabled", true);
        callFunction("UI|delete|setEnabled", false);
        callFunction("UI|save|setEnabled", false);
        // ��ʼ����ǰʱ��
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("ACTIVE_DATE", date);
        this.setValue("END_DATE", "9999/12/31");
        TTable table = getTable("TABLE");
        int seq = getMaxSeq(table.getDataStore(), "SEQ", table.getDataStore()
                            .isFilter() ? table.getDataStore().FILTER : table
                            .getDataStore().PRIMARY);
        setValue("SEQ", seq);
    //    pwdMessageShow = false; //=====pangben modify 20110526
    }

    /**
     * ���һ���µĲ�������Ϣ�����¼�
     */
    public void onNew() {

    	//

        String userId = getValueString("USER_ID").trim();
        if (userId.length() == 0) {
            messageBox("��������ʹ����");
            return;
        }
        TTable table = getTable("TABLE");
        //============pangben modify 20110607 start
        String userSQL = "SELECT * FROM SYS_OPERATOR WHERE USER_ID='" + userId +
                         "'";
        TParm selParm = new TParm(TJDODBTool.getInstance().select(userSQL));
        //============pangben modify 20110607 stop
        if (selParm.getCount("USER_ID")>0) {
            messageBox("ʹ�����Ѵ���");
            return;
        }
        int row = table.addRow();
        Timestamp date = StringTool.getTimestamp(new Date());
        table.setItem(row, "USER_ID", userId);
        table.setSelectedRow(row);
        callFunction("UI|USER_ID|setEnabled", false);
        // ��տ�����Ϣ��
        getTable("TABLEDEPT").removeRowAll();
        // ���֤����Ϣ��
        getTable("TABLELISCENSE").removeRowAll();
        // ���ʹ��������������Ϣ��
        getTable("OPT_CLINICAREA_TABLE").removeRowAll();
        // ���ʹ��������������Ϣ��
        getTable("OPT_STATION_TABLE").removeRowAll();
        // ��ʼ��ʹ�������������б�
        showOperatorDept();
        // ��ʼ��ʹ�������������б�
        showOperatorClinicarea();
        // ��ʼ��ʹ�������������б�
        showOperatorStation();
        onTableClicked();
        int seq = getMaxSeq(table.getDataStore(), "SEQ", table.getDataStore()
                            .isFilter() ? table.getDataStore().FILTER : table
                            .getDataStore().PRIMARY);
        setValue("SEQ", seq);
        // ����ҳ��״̬
        callFunction("UI|new|setEnabled", false);
        callFunction("UI|save|setEnabled", true);
        // ���ù��
        ((TTextField) getComponent("USER_NAME")).grabFocus();
        // ��ʼ����ǰʱ��
        this.setValue("ACTIVE_DATE", date);
        this.setValue("END_DATE", "9999/12/31");


        //# - ����ldap��֤
        boolean isLdap = SYSNewRegionTool.getInstance().isLDAP(Operator.getRegion());
        // messageBox_(" ======= isLdap: " + isLdap );
        if( isLdap ){

			//this.messageBox("----------");
        	String user = this.getValueString("USER_ID");

        	TParm parm = new TParm();
        	parm.setData("USER",  user);
            TParm result = TIOM_AppServer.executeAction(
                    "action.sys.LDAPLoginAction", "getLDAPUser", parm);

            setValue("USER_NAME", result.getValue("USER_NAME"));
            setValue("USER_PASSWORD", "AAAaaa111");
            setValue("TEL1", result.getValue("TEL1"));
            setValue("DESCRIPTION", result.getValue("DESCRIPTION"));
            setValue("E_MAIL", result.getValue("E_MAIL"));

            //
            ( (TPasswordField)this.getComponent("USER_PASSWORD") ).setEditable(false);
        }

    }
    /**
     * ���淽��
     * @return boolean
     */
    public boolean onSave() {

        TParm parm = new TParm();
        if (!checkNewData()) {
            return false;
        }
        pwdPoofSth();
        //=======pangben modify 20110631 start
        //��֤����ǿ�ȣ���ǿ�ȹ��Ͳ����Ա���
        TLabel pwdLbl = (TLabel)this.getComponent("PWD_POOFSTH");
        TLabel lbl = (TLabel)this.getComponent("pwd_LBL");
        if (pwdLbl.isVisible() && lbl.getValue().equals("��")) {
            this.messageBox("����ǿ��̫��,�����Ա���!");
            return false;
        }
        //=======pangben modify 20110631 stop
        TTable table = getTable("TABLE");
        int row = table.getSelectedRow();
        TDataStore dataStore = table.getDataStore();
        // �������
        TTable tabledept = getTable("TABLEDEPT");
        if (tabledept.getDataStore().isModified()) {
            tabledept.acceptText();
            if (tabledept.getDataStore().rowCount() > 0) {
                Vector main_flg = new Vector();
                main_flg = tabledept.getDataStore().getVector("MAIN_FLG");
                boolean flg = false;
                for (int i = 0; i < main_flg.size(); i++) {
                    if (((Vector) main_flg.get(i)).get(0).toString()
                        .equals("Y"))
                        flg = true;
                }
                if (!flg) {
                    this.messageBox("��ѡ��������");
                    return false;
                }
            }
            String[] dept_sql = tabledept.getDataStore().getUpdateSQL();
            parm.setData("DEPT", dept_sql);
            tabledept.setDSValue();
        }

        // ����֤��
        String userId = (String) table.getItemData(table.getSelectedRow(),
                "USER_ID");
        //============pangben modify 20110428 start
        //==========���������ʼ���ڡ���Ч���ڡ���������
        TParm resultPwd = OperatorTool.getInstance().getUserInfo(userId);

        /*
         * ZhenQin - 2011-05-06����ʵ��,
         * ��¼�����û��Ľ�ɫ�������־,���û�Ȩ�ޣ�Ȩ���飬��ɫ�����仯ʱ,д�����Ϣ��SYS_OPERATORLOG��
         *
         */

        //�ɵĽ�ɫ
        String user_RoleId_old = null;
        try {
            //��ʱ��һ��ʱ������,����û��ѡ����
            user_RoleId_old = (String) table.getItemData(table.getSelectedRow(),
                    "ROLE_ID");
        } catch (Exception e) {
            user_RoleId_old = "";
        } finally {
            //��֤user_RoleId_old != null;
            if (user_RoleId_old == null) {
                user_RoleId_old = "";
            }
            user_RoleId_old = user_RoleId_old.trim();
        }
        //�µĽ�ɫ
        String user_RoleId_new = this.getValueString("ROLE_ID");
        //������ʽ�ĸ�nullֵ,��tool��,��Tparm��existData("")�ķ�ʽ�ж��Ƿ���ڴ�����,���ڱ�ʾ��Ҫ��¼��־
        TParm userInfoChange = null;
        //��user_RoleId_old��user_RoleId_new��ֵ�����,��ʾ�����߸������û���ɫ
        if (!user_RoleId_new.equals(user_RoleId_old)) {
            userInfoChange = new TParm();
            /*
             * ��������add�ķ�ʽ�������,Ŀǰ֧�ּ�¼�û���ɫʱ������־,
             * �Ժ���ܼ�¼������Ϣ,��add�ķ�ʽ������ݼ���
             */
            userInfoChange.addData("MODI_ITEM",
                                   user_RoleId_old.equals("") ? "I" : "R");
            userInfoChange.addData("MODI_ITEM_CHN",
                                   user_RoleId_old.equals("") ? "�����û���ɫ" :
                                   "�ı��ɫ");
            userInfoChange.addData("ITEM_OLD",
                                   user_RoleId_old.equals("") ? "" : user_RoleId_old);
            userInfoChange.addData("ITEM_NEW", user_RoleId_new);
            userInfoChange.addData("DESCRIPTION",
                                   user_RoleId_old.equals("") ? "�����û���ɫ" :
                                   "�ı��ɫ");

            /*
             * ������ÿ���ύ�������Զ�ǵ����û�������,������Щ������һ����������һ������
             */
            userInfoChange.setData("USER_ID", this.getValue("USER_ID"));
            userInfoChange.setData("REGION_CODE", Operator.getRegion());
            userInfoChange.setData("OPT_USER", Operator.getID());
            userInfoChange.setData("OPT_TERM", Operator.getIP());
            userInfoChange.setData("OPT_DATE", SystemTool.getInstance().getDate());

            parm.setData("userInfoChange", userInfoChange.getData());
        }
        //����
        String oldPWD = OperatorTool.getInstance().decrypt(
                resultPwd.getValue("USER_PASSWORD", 0));
        //============pangben modify 20110428 start
        Timestamp date = StringTool.getTimestamp(new Date());
        TTable tableliscense = getTable("TABLELISCENSE");
        tableliscense.acceptText();
        TDataStore dataStore2 = tableliscense.getDataStore();
        int rows[] = dataStore2.getModifiedRows(dataStore2.PRIMARY);
        if (dataStore2.isModified()) {
            for (int i = 0; i < rows.length; i++) {
                dataStore2.setItem(rows[i], "USER_ID", userId);
                dataStore2.setItem(rows[i], "OPT_USER", Operator.getID());
                dataStore2.setItem(rows[i], "OPT_DATE", date);
                dataStore2.setItem(rows[i], "OPT_TERM", Operator.getIP());
            }
            String[] liscense_sql = tableliscense.getDataStore().getUpdateSQL();
            parm.setData("LISCENSE", liscense_sql);
            tableliscense.setDSValue();
        }

        TTable clinicarea = getTable("OPT_CLINICAREA_TABLE");
        TDataStore clinicareaDS = clinicarea.getDataStore();
        TTable station = getTable("OPT_STATION_TABLE");
        TDataStore stationDS = station.getDataStore();

        // �ж��Ƿ���ڶ������������������
        if (clinicareaDS.rowCount() > 0 || stationDS.rowCount() > 0) {
            int countNum = 0;
            for (int i = 0; i < clinicareaDS.rowCount(); i++) {
                Vector main_flg = new Vector();
                main_flg = clinicareaDS.getVector("MAIN_FLG");
                if (((Vector) main_flg.get(i)).get(0).toString().equals("Y"))
                    countNum++;
            }
            for (int i = 0; i < stationDS.rowCount(); i++) {
                Vector main_flg = new Vector();
                main_flg = stationDS.getVector("MAIN_FLG");
                if (((Vector) main_flg.get(i)).get(0).toString().equals("Y"))
                    countNum++;
            }
            if (countNum == 0) {
                this.messageBox("��ѡ����������������");
                return false;
            }
            if (countNum > 1) {
                this.messageBox("ֻ��ѡ��һ����������������");
                return false;
            }
        }

        // ��������
        if (clinicareaDS.isModified()) {
            clinicarea.acceptText();
            String[] clinicarea_sql = clinicarea.getDataStore().getUpdateSQL();
            parm.setData("CLINICAREA", clinicarea_sql);
            clinicarea.setDSValue();
        }

        // ���没��
        if (stationDS.isModified()) {
            station.acceptText();
            String[] station_sql = station.getDataStore().getUpdateSQL();
            parm.setData("STATION", station_sql);
            station.setDSValue();
        }

        // ��DataStore�������
        dataStore.setItem(row, "USER_NAME", getValueString("USER_NAME"));
        dataStore.setItem(row, "PY1", getValueString("PY1"));
        dataStore.setItem(row, "PY2", getValueString("PY2"));
        dataStore.setItem(row, "SEQ", getValueInt("SEQ"));
        dataStore.setItem(row, "USER_ENG_NAME", getValueString("USER_ENG_NAME"));
        dataStore.setItem(row, "FOREIGNER_FLG", getValue("FOREIGNER_FLG"));
        dataStore.setItem(row, "ID_NO", getValueString("ID_NO"));
        dataStore.setItem(row, "SEX_CODE", getValueString("SEX_CODE"));
        dataStore.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));
        dataStore.setItem(row, "POS_CODE", getValueString("POS_CODE"));
        String password = OperatorTool.getInstance().encrypt(
                getValueString("USER_PASSWORD"));
        //===============pangben modify 20110428 start
        //�ж������Ƿ���ͬ,�����ͬ��ʾ��Ϣ��,�ж�������������Ƿ��ǵ�ǰʱ��
        // if(pwdMessageShow){//�ж������Ƿ��޸�
        Timestamp loginTime = resultPwd.getTimestamp("PWD_STARTDATE", 0);
        if (null != loginTime && !loginTime.equals("")) {
           // if(){//�ж������Ƿ��޸�
           if (!oldPWD.equals(getValueString("USER_PASSWORD"))) {
              dataStore.setItem(row, "PWD_STARTDATE", date);
           }
       }
        // }
       //  pwdMessageShow = false; //������Ϣ����ʾ״̬
        //===============pangben modify 20110428 start
        dataStore.setItem(row, "USER_PASSWORD", password);
        dataStore.setItem(row, "FULLTIME_FLG", getValue("FULLTIME_FLG"));
        dataStore.setItem(row, "CTRL_FLG", getValue("CTRL_FLG"));
        //xueyf modify 2011-12-28 start
        dataStore.setItem(row, "UKEY_FLG", getValue("UKEY_FLG"));
        //xueyf modify 2011-12-28 end
        dataStore.setItem(row, "E_MAIL", getValue("E_MAIL"));
        dataStore.setItem(row, "REGION_CODE", getValueString("REGION_CODE"));
        dataStore.setItem(row, "ROLE_ID", getValueString("ROLE_ID"));
        Timestamp begin = (Timestamp) getValue("ACTIVE_DATE");
        Timestamp end = (Timestamp) getValue("END_DATE");

        dataStore.setItem(row, "ACTIVE_DATE", begin);
        dataStore.setItem(row, "END_DATE", end);
        dataStore.setItem(row, "IS_OUT_FLG", getValue("IS_OUT_FLG"));
        //���ӹ�������ܴa��Ч�r�g
        //===============pangben modify 20110324 start
        Timestamp PWD_ENDDATE = (Timestamp) getValue("PWD_ENDDATE");
        dataStore.setItem(row, "PWD_ENDDATE", PWD_ENDDATE);
        //===============pangben modify 20110324 stop
        dataStore.setItem(row, "ABNORMAL_TIMES", getValueInt("ABNORMAL_TIMES"));
        dataStore.setItem(row, "PUB_FUNCTION", getValueString("PUB_FUNCTION"));
        dataStore.setItem(row, "OPT_USER", Operator.getID());
        dataStore.setItem(row, "OPT_DATE", date);
        dataStore.setItem(row, "OPT_TERM", Operator.getIP());
        //add shibaoliu 20120315
        dataStore.setItem(row, "COST_CENTER_CODE", this.getValueString("COST_CENTER_CODE"));
        dataStore.setItem(row, "TEL1", this.getValueString("TEL1"));
        dataStore.setItem(row, "TEL2", this.getValueString("TEL2"));
        // ��SYS_OPERATOR�������֤����Ϣ
        if (tableliscense.getDataStore().rowCount() > 0) {
            String lsc_no = tableliscense.getItemString(0, "LCS_NO");
            Timestamp eff_date = tableliscense.getItemTimestamp(0,
                    "EFF_LCS_DATE");
            Timestamp end_date = tableliscense.getItemTimestamp(0,
                    "END_LCS_DATE");
            dataStore.setItem(row, "LCS_NO", lsc_no);
            dataStore.setItem(row, "EFF_LCS_DATE", eff_date);
            dataStore.setItem(row, "END_LCS_DATE", end_date);
        } else {
            dataStore.setItem(row, "LCS_NO", "");
            dataStore.setItem(row, "EFF_LCS_DATE", "");
            dataStore.setItem(row, "END_LCS_DATE", "");
        }
        // ����ʹ��������
        if (table.getDataStore().isModified()) {
            table.acceptText();
            String[] operator_sql = table.getDataStore().getUpdateSQL();
//            int count = operator_sql.length;
            parm.setData("OPERATOR", operator_sql);
            table.setDSValue();
        }
        // ���ñ��涯��
        TParm result = TIOM_AppServer.executeAction(
                "action.sys.SYSOperatorAction",
                "onSaveOperator", parm);
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return false;
        }
        table.setSelectedRow(row);
        messageBox("P0001");
        // ����ҳ��״̬
        callFunction("UI|new|setEnabled", true);
        callFunction("UI|save|setEnabled", false);
        return true;
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        TTable table = getTable("TABLE");
        int row = table.getTable().getSelectedRow();
        if (row < 0)
            return;
        // ������֤��
        int count1 = this.getTable("TABLELISCENSE").getRowCount();
        if (count1 > 0) {
            this.messageBox("�ò����ߴ���֤����Ϣ����ɾ��");
            return;
        }
        // ��������������
        int count2 = this.getTable("TABLEDEPT").getRowCount();
        if (count2 > 0) {
            this.messageBox("�ò����ߴ����������ң���ɾ��");
            return;
        }
        // ɾ������ȷ��
        switch (messageBox("��ʾ��Ϣ", "�Ƿ�ɾ���ò�����?", 0)) {
        case 0:
            table.removeRow(row);

            // ɾ������
            TTable tabledept = getTable("TABLEDEPT");
            if (tabledept.getDataStore().isModified()) {
                tabledept.acceptText();
            }
            if (!tabledept.update()) {
                return;
            }
            tabledept.setDSValue();

            // ɾ��֤��
            TTable tableliscense = getTable("TABLELISCENSE");
            tableliscense.acceptText();
            TDataStore dataStore2 = tableliscense.getDataStore();
            if (dataStore2.isModified()) {
                if (!tableliscense.update()) {
                    return;
                }
                tableliscense.setDSValue();
            }

            TTable clinicarea = getTable("OPT_CLINICAREA_TABLE");
            TDataStore clinicareaDS = clinicarea.getDataStore();
            TTable station = getTable("OPT_STATION_TABLE");
            TDataStore stationDS = station.getDataStore();

            // ɾ������
            if (clinicareaDS.isModified()) {
                clinicarea.acceptText();
                if (!clinicarea.update()) {
                    return;
                }
                clinicarea.setDSValue();
            }

            // ɾ������
            if (stationDS.isModified()) {
                station.acceptText();
                if (!station.update()) {
                    return;
                }
                station.setDSValue();
            }

            // ɾ��ʹ��������
            if (table.getDataStore().isModified()) {
                table.acceptText();
                if (!table.update()) {
                    messageBox("ɾ��ʧ��");
                    return;
                }
                table.setDSValue();
            }
            messageBox("ɾ���ɹ�");

            break;
        case 1:
            return;

        }
        // ɾ����ť״̬����
        if (table.getRowCount() > 0) {
            table.setSelectedRow(0);
            onTableClicked();
        } else {
            callFunction("UI|delete|setEnabled", false);
        }

    }

    /**
     * �û��ƿ�
     */
    public void onCreatEKT() {
        openDialog("%ROOT%\\config\\ekt\\SaveUseIDDialog.x");
    }

    /**
     * ��������
     */
    public void onFilter() {
        boolean flg = ((TTextField) getComponent("USER_ID")).isEnabled();
        // ���Ϊ����ʹ��״̬����������ݸ���
        if (!flg) {
            TTable table = getTable("TABLE");
            int row = table.getSelectedRow();
            // ʹ��������
            String userName = getValueString("USER_NAME");
            table.setItem(row, "USER_NAME", userName);
            // ƴ��
            String py = TMessage.getPy(userName);
            setValue("PY1", py);
            // �Ա�
            String sex = getValueString("SEX_CODE");
            table.setItem(row, "SEX_CODE", sex);
            // ����
            String region = getValueString("REGION_CODE");
            table.setItem(row, "REGION_CODE", region);
            // ְ��
            String pos = getValueString("POS_CODE");
            table.setItem(row, "POS_CODE", pos);
            // ��ɫ ROLE_ID
            String role = getValueString("ROLE_ID");
            table.setItem(row, "ROLE_ID", role);
            return;
        }
        // ��ѯ������
        String value = "";
        String str = getValueString("USER_NAME");
        if (str.length() != 0)
            value += "USER_NAME like '" + str + "%'";
        str = getValueString("USER_ID");
        if (str.length() != 0) {
            if (value.length() != 0)
                value += " AND ";
            value += "USER_ID like '" + str + "%'";
        }
        str = getValueString("REGION_CODE");
        if (str.length() != 0) {
            if (value.length() != 0)
                value += " AND ";
            value += "REGION_CODE = '" + str + "'";
        }
        str = getValueString("POS_CODE");
        if (str.length() != 0) {
            if (value.length() != 0)
                value += " AND ";
            value += "POS_CODE = '" + str + "'";
        }
        str = getValueString("ROLE_ID");
        if (str.length() != 0) {
            if (value.length() != 0)
                value += " AND ";
            value += "ROLE_ID = '" + str + "'";
        }
        TTable table1 = getTable("TABLE");
        if (value.length() > 0) {
            table1.setFilter(value);
            table1.filter();
            return;
        }
        table1.setFilter("");
        table1.filter();
    }

    /**
     * UserId�س��¼�
     */
    public void onUserIdAction() {
        ((TTextField) getComponent("USER_NAME")).grabFocus();
    }

    /**
     * UserName�س��¼�
     */
    public void onUserNameAction() {
        boolean flg = ((TTextField) getComponent("USER_ID")).isEnabled();
        if (!flg) {
            TTable table = getTable("TABLE");
            int row = table.getSelectedRow();
            String userName = getValueString("USER_NAME");
            table.setItem(row, "USER_NAME", userName);
            String py = TMessage.getPy(userName);
            setValue("PY1", py);
            setValue("USER_ENG_NAME", py);
        }
        ((TTextField) getComponent("PY1")).grabFocus();
    }

    /**
     * POS_CODE�س��¼�
     */
    public void onPosCodeAction() {
        ((TCheckBox) getComponent("FULLTIME_FLG")).grabFocus();
    }


    /**
     * REGION_CODE�س��¼�
     */
    public void onRegionCodeAction() {
        ((TTextFormat) getComponent("POS_CODE")).grabFocus();
    }

    /**
     * ROLE_ID�س��¼�
     */
    public void onRoleIdAction() {
        ((TTextFormat) getComponent("ACTIVE_DATE")).grabFocus();
    }

    /**
     * ACTIVE_DATE�س��¼�
     */
    public void onActiveDateAction() {
        ((TTextFormat) getComponent("END_DATE")).grabFocus();
    }

    /**
     * END_DATE�س��¼�
     */
    public void onEndDateAction() {
        ((TTextField) getComponent("PUB_FUNCTION")).grabFocus();
    }

    /**
     * ID_NO�س��¼�
     */
    public void onIdNOAction() {
        String flg = getValueString("FOREIGNER_FLG");
        String id_no = getValueString("ID_NO");
        if (flg.equals("N") && StringTool.isId(id_no)) {
            String sex = StringTool.isMaleFromID(id_no);
            this.setValue("SEX_CODE", sex);
        }
        ((TComboBox) getComponent("SEX_CODE")).grabFocus();
    }

    /**
     * EFF_LCS_DATE�س��¼�
     */
    public void onEffLCSDateAction() {
        ((TTextFormat) getComponent("END_LCS_DATE")).grabFocus();
    }

    /**
     * ����ComboBoxѡ���¼�
     */
    public void onComboBoxSelected() {
        String dept = getValueString("DEPT_CODE");
        if (((TMenuItem) getComponent("new")).isEnabled()) {
            // ���ݿ��Ҳ�ѯ��Ա
            String sql = "SELECT USER_ID,DEPT_CODE FROM SYS_OPERATOR_DEPT";
            if (dept.length() > 0) {
                sql += " WHERE DEPT_CODE = '" + dept + "'";
            }
            TDataStore st = new TDataStore();
            st.setSQL(sql);
            st.retrieve();
            int count = st.rowCount();
            //============pangben modify 20110524
            String sql2 = SYSSQL.getSYSOperator(Operator.getRegion()) +
                          " AND USER_ID IN ("
                          + "'" + st.getItemData(0, "USER_ID") + "'";
            for (int i = 1; i < count; i++) {
                sql2 = sql2 + ",'" + st.getItemData(i, "USER_ID") + "'";
            }
            sql2 += ")";
            TDataStore dataStroe = new TDataStore();
            dataStroe.setSQL(sql2);
            dataStroe.retrieve();
            TTable table1 = getTable("TABLE");
            table1.setDataStore(dataStroe);
            table1.setDSValue();
            onFilter();
        }
    }

    /**
     * ��������Ϣ���(TABLE)�����¼�
     */
    public void onTableClicked() {
        //====pangben modify 20110429 start ���ǿ��У��
    //    pwdMessageShow = false; //����ʾ�����޸���Ϣ��
        this.setValue("pwd_LBL", "");
        //====pangben modify 20110429 stop
        // ��տ���
        this.setValue("DEPT_CODE", "");
        // ����������ɾ����ť״̬
        if (getTable("TABLE").getSelectedRow() != -1) {
            callFunction("UI|delete|setEnabled", true);
            callFunction("UI|new|setEnabled", false);
        }
        // �õ���ѡ���е�����
        TParm parm = getTable("TABLE").getDataStore().getRowParm(
                getTable("TABLE").getSelectedRow());
        // ��UI����ʾѡ���е�����
        setValue("USER_ID", parm.getData("USER_ID"));
        setValue("USER_NAME", parm.getData("USER_NAME"));
        setValue("USER_ENG_NAME", parm.getData("USER_ENG_NAME"));
        setValue("PY1", parm.getData("PY1"));
        setValue("PY2", parm.getData("PY2"));
        setValue("SEQ", parm.getData("SEQ"));
        setValue("FOREIGNER_FLG", parm.getData("FOREIGNER_FLG"));
        setValue("ID_NO", parm.getData("ID_NO"));
        setValue("SEX_CODE", parm.getData("SEX_CODE"));
        setValue("DESCRIPTION", parm.getData("DESCRIPTION"));
        setValue("POS_CODE", parm.getData("POS_CODE"));
        String pwd = OperatorTool.getInstance().decrypt(
                (String) parm.getData("USER_PASSWORD"));
        setValue("USER_PASSWORD", pwd);
        setValue("FULLTIME_FLG", parm.getData("FULLTIME_FLG"));
        setValue("CTRL_FLG", parm.getData("CTRL_FLG"));
        //xueyf add UKEY_FLG
        setValue("UKEY_FLG", parm.getData("UKEY_FLG"));
        setValue("E_MAIL", parm.getData("E_MAIL"));
        setValue("REGION_CODE", parm.getData("REGION_CODE"));
        setValue("ROLE_ID", parm.getData("ROLE_ID"));
        setValue("ACTIVE_DATE", parm.getData("ACTIVE_DATE"));
        setValue("END_DATE", parm.getData("END_DATE"));
        setValue("PUB_FUNCTION", parm.getData("PUB_FUNCTION"));
        setValue("RCNT_LOGIN_DATE", parm.getData("RCNT_LOGIN_DATE"));
        setValue("RCNT_LOGOUT_DATE", parm.getData("RCNT_LOGOUT_DATE"));
        setValue("RCNT_IP", parm.getData("RCNT_IP"));
        setValue("ABNORMAL_TIMES", parm.getData("ABNORMAL_TIMES"));
        setValue("COST_CENTER_CODE", parm.getData("COST_CENTER_CODE"));
        setValue("TEL1", parm.getData("TEL1"));
        setValue("TEL2", parm.getData("TEL2"));
        setValue("IS_OUT_FLG", parm.getData("IS_OUT_FLG"));
        //===========pangben modify 20110324 start
        if (null != parm.getData("PWD_ENDDATE"))
            setValue("PWD_ENDDATE", parm.getData("PWD_ENDDATE"));
        else
            setValue("PWD_ENDDATE", "9999/12/31");
        //===========pangben modify 20110324 stop
        // ��ʾָ�������ߵ���������
        String userId = parm.getValue("USER_ID");
        TTable table = getTable("TABLEDEPT");
        String sql = SYSSQL.getOperatorDeptByUserId(userId);
        TDataStore dataStroe = new TDataStore();
        dataStroe.setSQL(sql);
        dataStroe.retrieve();
        table.setDataStore(dataStroe);
        table.setDSValue();
        // ��ʾָ�������ߵ���������
        TTable clinicarea_table = getTable("OPT_CLINICAREA_TABLE");
        sql = SYSSQL.getSYSOperatorStation("1", userId);
        TDataStore dataStroeClinic = new TDataStore();
        dataStroeClinic.setSQL(sql);
        dataStroeClinic.retrieve();
        clinicarea_table.setDataStore(dataStroeClinic);
        clinicarea_table.setDSValue();
        // ��ʾָ�������ߵ���������
        TTable station_table = getTable("OPT_STATION_TABLE");
        sql = SYSSQL.getSYSOperatorStation("2", userId);
        TDataStore dataStroeStation = new TDataStore();
        dataStroeStation.setSQL(sql);
        dataStroeStation.retrieve();
        station_table.setDataStore(dataStroeStation);
        station_table.setDSValue();
        // ��ѯָ���û���֤����Ϣ
        showOperatorLicense(userId);
        // �ı������״̬
        callFunction("UI|USER_ID|setEnabled", false);
        // ���ñ��水ť״̬
        callFunction("UI|save|setEnabled", true);
    }

    /**
     * ֤����Ϣ���(TableLiscense)�����¼�
     */
    public void onTableLiscenseClicked() {
        TParm parm = getTable("TABLELISCENSE").getDataStore().getRowParm(
                getTable("TABLELISCENSE").getSelectedRow());
        setValue("LCS_CLASS_CODE", parm.getData("LCS_CLASS_CODE"));
        setValue("LCS_NO", parm.getData("LCS_NO"));
        setValue("EFF_LCS_DATE", parm.getData("EFF_LCS_DATE"));
        setValue("END_LCS_DATE", parm.getData("END_LCS_DATE"));
    }

    /**
     * ��ӿ��Ұ�ť(INSERT_DEPT)
     */
    public void onInsertDeptClicked() {
        Timestamp date = StringTool.getTimestamp(new Date());
        String dept_code = this.getValueString("DEPT_CODE");
        if ("".equals(dept_code)) {
            this.messageBox("��ѡ����ӿ���");
            return;
        }
        TTable table = getTable("TABLEDEPT");
        for (int i = 0; i < table.getRowCount(); i++) {
            if (dept_code.equals(table.getDataStore().getItemString(i,
                    "DEPT_CODE"))) {
                this.messageBox("�����Ѵ��ڣ��������");
                return;
            }
        }
        int count = table.getRowCount();
        int newRow = table.addRow();
        String userId = getTable("TABLE").getDataStore().getItemString(
                getTable("TABLE").getSelectedRow(), "USER_ID");
        if (count == 0)
            table.setItem(newRow, "MAIN_FLG", "Y");
        else
            table.setItem(newRow, "MAIN_FLG", "N");
        table.setItem(newRow, "DEPT_CODE", dept_code);
        table.setItem(newRow, "USER_ID", userId);
        table.setItem(newRow, "OPT_USER", Operator.getID());
        table.setItem(newRow, "OPT_DATE", date);
        table.setItem(newRow, "OPT_TERM", Operator.getIP());
    }

    /**
     * �Ƴ����ҵ�����ť(REMOVE_DEPT)
     */
    public void onRemoveDeptClicked() {
        if (getTable("TABLEDEPT").getSelectedRow() == -1)
            return;
        getTable("TABLEDEPT").removeRow(getTable("TABLEDEPT").getSelectedRow());
    }

    /**
     * ����Insert_CLINICAREA��ť
     */
    public void onInsertClinicareaClicked() {
        Timestamp date = StringTool.getTimestamp(new Date());
        String clinic_area = this.getValueString("OPERATOR_CLINIC");
        if ("".equals(clinic_area)) {
            this.messageBox("��ѡ���������");
            return;
        }
        TTable table = getTable("OPT_CLINICAREA_TABLE");
        for (int i = 0; i < table.getRowCount(); i++) {
            if (clinic_area.equals(table.getDataStore().getItemString(i,
                    "STATION_CLINIC_CODE"))) {
                this.messageBox("�����Ѵ��ڣ��������");
                return;
            }
        }
        int newRow = table.addRow();
        String userId = getTable("TABLE").getDataStore().getItemString(
                getTable("TABLE").getSelectedRow(), "USER_ID");
        table.setItem(newRow, "MAIN_FLG", "N");
        table.setItem(newRow, "STATION_CLINIC_CODE", clinic_area);
        table.setItem(newRow, "USER_ID", userId);
        table.setItem(newRow, "TYPE", 1);
        table.setItem(newRow, "OPT_USER", Operator.getID());
        table.setItem(newRow, "OPT_DATE", date);
        table.setItem(newRow, "OPT_TERM", Operator.getIP());
    }

    /**
     * ����Remove_CLINICAREA��ť
     */
    public void onRemoveClinicareaClicked() {
        if (getTable("OPT_CLINICAREA_TABLE").getSelectedRow() == -1)
            return;
        getTable("OPT_CLINICAREA_TABLE").removeRow(getTable(
                "OPT_CLINICAREA_TABLE").getSelectedRow());
    }

    /**
     * ����Insert_Station��ť
     */
    public void onInsertStationClicked() {
        Timestamp date = StringTool.getTimestamp(new Date());
        String station_area = this.getValueString("OPERATOR_STATION");
        if ("".equals(station_area)) {
            this.messageBox("��ѡ����Ӳ���");
            return;
        }
        TTable table = getTable("OPT_STATION_TABLE");
        for (int i = 0; i < table.getRowCount(); i++) {
            if (station_area.equals(table.getDataStore().getItemString(i,
                    "STATION_CLINIC_CODE"))) {
                this.messageBox("�����Ѵ��ڣ��������");
                return;
            }
        }
        int newRow = table.addRow();
        String userId = getTable("TABLE").getDataStore().getItemString(
                getTable("TABLE").getSelectedRow(), "USER_ID");
        table.setItem(newRow, "MAIN_FLG", "N");
        table.setItem(newRow, "STATION_CLINIC_CODE", station_area);
        table.setItem(newRow, "USER_ID", userId);
        table.setItem(newRow, "TYPE", 2);
        table.setItem(newRow, "OPT_USER", Operator.getID());
        table.setItem(newRow, "OPT_DATE", date);
        table.setItem(newRow, "OPT_TERM", Operator.getIP());
    }

    /**
     * ����Remove_STATION��ť
     */
    public void onRemoveStationClicked() {
        if (getTable("OPT_STATION_TABLE").getSelectedRow() == -1)
            return;
        getTable("OPT_STATION_TABLE").removeRow(getTable(
                "OPT_STATION_TABLE").getSelectedRow());
    }
    /**
     * �������������ұ��(TABLEDEPT)��ѡ��ı��¼�
     * @param obj Object
     */
    public void onTableDeptCheckBoxClicked(Object obj) {
        // ��õ����table����
        TTable tableDown = (TTable) obj;
        // ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
        tableDown.acceptText();
        // ���ѡ�е���
        int row = tableDown.getSelectedRow();
        // ȡ������ѡ��
        for (int i = 0; i < tableDown.getRowCount(); i++) {
            tableDown.setItem(i, "MAIN_FLG", "N");
            tableDown.getDataStore().setItem(i, "MAIN_FLG", "N");
        }
        // ѡ��ѡ����
        tableDown.setItem(row, "MAIN_FLG", "Y");
        tableDown.getDataStore().setItem(row, "MAIN_FLG", "Y");
    }
    /**
     * �����������������(OPT_CLINICAREA_TABLE)��ѡ��ı��¼�
     * @param obj Object
     */
    public void onTableOPT_ClinicareaCheckBoxClicked(Object obj) {
        // ��õ����table����
        TTable tableDown = (TTable) obj;
        // ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
        tableDown.acceptText();
        // ���ѡ�е���
        int row = tableDown.getSelectedRow();
        // ȡ������ѡ��
        for (int i = 0; i < tableDown.getRowCount(); i++) {
            if (row == i)
                continue;
            tableDown.setItem(i, "MAIN_FLG", "N");
            tableDown.getDataStore().setItem(i, "MAIN_FLG", "N");
        }
    }
    /**
     * �����������������(OPT_Station_TABLE)��ѡ��ı��¼�
     * @param obj Object
     */
    public void onTableOPT_StationCheckBoxClicked(Object obj) {
        // ��õ����table����
        TTable tableDown = (TTable) obj;
        // ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
        tableDown.acceptText();
        // ���ѡ�е���
        int row = tableDown.getSelectedRow();
        // ȡ������ѡ��
        for (int i = 0; i < tableDown.getRowCount(); i++) {
            if (row == i)
                continue;
            tableDown.setItem(i, "MAIN_FLG", "N");
            tableDown.getDataStore().setItem(i, "MAIN_FLG", "N");
        }
    }

    /**
     * ����µ�֤��
     */
    public void onInsertLiscenseClicked() {
        if (getTable("TABLE").getSelectedRow() == -1) {
            messageBox("��ѡ��ʹ����");
            return;
        }
        String code = getValueString("LCS_CLASS_CODE").trim();
        TTable table = getTable("TABLELISCENSE");
        if (table.getDataStore().exist("LCS_CLASS_CODE='" + code + "'")) {
            messageBox("֤���Ѵ���");
            return;
        }
        if (getValueString("LCS_CLASS_CODE").trim().length() == 0) {
            messageBox("֤�������Ϊ��");
            return;
        }
        if (getValueString("LCS_NO").trim().length() == 0) {
            messageBox("֤�պ��벻��Ϊ��");
            return;
        }
        Timestamp begin = (Timestamp) getValue("EFF_LCS_DATE");
        if (begin == null) {
            messageBox("֤�����ղ���Ϊ��");
            return;
        }
        Timestamp end = (Timestamp) getValue("END_LCS_DATE");
        if (end == null) {
            messageBox("֤�����ղ���Ϊ��");
            return;
        }
        if (end.compareTo(begin) <= 0) {
            messageBox("֤�����ղ��ܴ���֤������");
            return;
        }
        int row = table.addRow();
        table.setItem(row, "LCS_CLASS_CODE", code);
        table.setItem(row, "LCS_NO", getValueString("LCS_NO").trim());
        table.setItem(row, "EFF_LCS_DATE", begin);
        table.setItem(row, "END_LCS_DATE", end);
    }


    /**
     * ����֤��
     */
    public void onUpdateLiscenseClicked() {
        String code = getValueString("LCS_CLASS_CODE").trim();
        TTable table = getTable("TABLELISCENSE");
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ���޸�֤��");
            return;
        }
        if (getValueString("LCS_CLASS_CODE").trim().length() == 0) {
            messageBox("֤�������Ϊ��");
            return;
        }
        if (getValueString("LCS_NO").trim().length() == 0) {
            messageBox("֤�պ��벻��Ϊ��");
            return;
        }
        Timestamp begin = (Timestamp) getValue("EFF_LCS_DATE");
        if (begin == null) {
            messageBox("֤�����ղ���Ϊ��");
            return;
        }
        Timestamp end = (Timestamp) getValue("END_LCS_DATE");
        if (end == null) {
            messageBox("֤�����ղ���Ϊ��");
            return;
        }
        if (end.compareTo(begin) <= 0) {
            messageBox("֤�����ղ��ܴ���֤������");
            return;
        }
        table.setItem(row, "LCS_CLASS_CODE", code);
        table.setItem(row, "LCS_NO", getValueString("LCS_NO").trim());
        table.setItem(row, "EFF_LCS_DATE", begin);
        table.setItem(row, "END_LCS_DATE", end);
    }


    /**
     * �Ƴ�֤��
     */
    public void onRemoveLiscenseClicked() {
        TTable table = getTable("TABLELISCENSE");
        int row = table.getSelectedRow();
        if (row == -1) {
            messageBox("��ѡ��֤��");
            return;
        }
        table.removeRow(row);
    }

    /**
     * ���ø���Ȩ��
     */
    public void onAuth(){

    	TTable tb = getTable("TABLE");
    	int i = tb.getSelectedRow();

    	if( i==-1 ){
    		this.messageBox("����û��б���ѡ��һ���û�!");
    		return;
    	}

    	TParm parm = tb.getDataStore().getRowParm(i);

		this.openDialog("%ROOT%\\config\\sys\\SYSUserAuth.x", parm);
    }

    /**
     * ��ʾʹ�������������б�
     */
    public void showOperatorDept() {
        TTable table = getTable("TABLEDEPT");
        String sql = SYSSQL.getOperatorDeptByUserId(getValueString("USER_ID"));
        TDataStore dataStroe = new TDataStore();
        dataStroe.setSQL(sql);
        dataStroe.retrieve();
        table.setDataStore(dataStroe);
    }
    /**
     * ��ѯָ���û���֤����Ϣ
     * @param userId String
     */
    public void showOperatorLicense(String userId) {
        TDataStore dataStroe2 = new TDataStore();
        dataStroe2.setSQL(SYSSQL.getSYSLicenseDetail(userId));
        dataStroe2.retrieve();
        TTable table2 = getTable("TABLELISCENSE");
        table2.setDataStore(dataStroe2);
        table2.setDSValue();
    }

    /**
     * ��ʾʹ�������������б�
     */
    public void showOperatorClinicarea() {
        TTable table = getTable("OPT_CLINICAREA_TABLE");
        TDataStore dataStroe = new TDataStore();
        String sql = SYSSQL.getSYSOperatorStation("1",
                                                  this.getValueString("USER_ID"));
        dataStroe.setSQL(sql);
        dataStroe.retrieve();
        table.setDataStore(dataStroe);
    }

    /**
     * ��ʾʹ�������������б�
     */
    public void showOperatorStation() {
        TTable table = getTable("OPT_STATION_TABLE");
        TDataStore dataStroe = new TDataStore();
        dataStroe.setSQL(SYSSQL.getSYSOperatorStation("2",
                this.getValueString("USER_ID")));
        dataStroe.retrieve();
        table.setDataStore(dataStroe);
    }

    /**
     * ��ʾ���в�����Ա�б�
     */
    public void showOperator() {
        TDataStore dataStore = new TDataStore();
        //=======pangben modify 20110524 start
        dataStore.setSQL(SYSSQL.getSYSOperator(Operator.getRegion()));
        //=======pangben modify 20110524 stop
        dataStore.retrieve();
        TTable table = getTable("TABLE");
        table.setDataStore(dataStore);
        table.setDSValue();
    }
    /**
     * ������ݵ������Ժ�׼ȷ��
     * @return boolean
     */
    public boolean checkNewData() {
        if (getValueString("USER_NAME").equals("")) {
            this.messageBox("�û���������Ϊ��");
            return false;
        }
        if (this.getValue("FOREIGNER_FLG").equals("N")) {
            String id_no = getValueString("ID_NO");
            if (!StringTool.isId(id_no)) {
                this.messageBox("���֤����ȷ");
                return false;
            }
        }
        if (getValueString("SEX_CODE").equals("")) {
            this.messageBox("�Ա���Ϊ��");
            return false;
        }
        if (getValueString("POS_CODE").equals("")) {
            this.messageBox("ְ�Ʋ���Ϊ��");
            return false;
        }
        if (getValueString("USER_PASSWORD").equals("")) {
            this.messageBox("�����Ϊ��");
            return false;
        }
        // E_MAIL
        String e_mail = getValueString("E_MAIL");
        if (!e_mail.equals("")) {
            // ������֤
            if (!StringTool.isEmail(e_mail)) {
                this.messageBox("�������벻�Ϸ�");
                return false;
            }
        }
//        if (getValueString("REGION_CODE").equals("")) {
//            this.messageBox("������Ϊ��");
//            return false;
//        }
        if (getValueString("ROLE_ID").equals("")) {
            this.messageBox("��ɫ����Ϊ��");
            return false;
        }
        Timestamp begin = (Timestamp) getValue("ACTIVE_DATE");
        if (begin == null) {
            messageBox("��Ч���ڲ���Ϊ��");
            return false;
        }
        Timestamp end = (Timestamp) getValue("END_DATE");
        if (end == null) {
            messageBox("ʧЧ���ڲ���Ϊ��");
            return false;
        }
        if (end.compareTo(begin) <= 0) {
            this.messageBox("��Ч���ڲ��ܴ���ʧЧ����");
            return false;
        }
        int count = this.getValueInt("ABNORMAL_TIMES");
        if (count < 0) {
            this.messageBox("�쳣��½��������С��0");
            return false;
        }
        return true;
    }
    /**
     * �õ�ҳ����Table����
     * @param tag String
     * @return TTable
     */
    private TTable getTable(String tag) {
        return (TTable) callFunction("UI|" + tag + "|getThis");
    }
    /**
     * �õ����ı�� +1
     * @param dataStore TDataStore
     * @param columnName String
     * @param dbBuffer String
     * @return int
     */
    public int getMaxSeq(TDataStore dataStore, String columnName,
                         String dbBuffer) {
        if (dataStore == null)
            return 0;
        // ����������
        int count = dataStore.getBuffer(dbBuffer).getCount();
        // ��������
        int max = 0;
        for (int i = 0; i < count; i++) {
            int value = TCM_Transform.getInt(dataStore.getItemData(i,
                    columnName, dbBuffer));
            // �������ֵ
            if (max < value) {
                max = value;
                continue;
            }
        }
        // ���ż�1
        max++;
        return max;
    }

    /**
     * �����ı�����ʾ����ǿ�ȷ���
     * pangben modify 20110324
     */
    public void pwdPoofSth() {
        //pwdMessageShow = true;
        //ֻ�д����û�������ʾУ��ǿ��
        TTextField txtUserID = (TTextField)this.getComponent("USER_ID");
        if (null != txtUserID.getValue() && !"".equals(txtUserID.getValue()) &&
            !txtUserID.isEnabled()) {
            String sPW = this.getValueString("USER_PASSWORD");
            TLabel lbl = (TLabel)this.getComponent("pwd_LBL");
            if (sPW.length() <= 4) {
                this.setValue("pwd_LBL", "��");
                lbl.setColor("RED");
                return; //����̫��
            }
            String modes = "";
            for (int i = 0; i < sPW.length(); i++) {
                //����ÿһ���ַ������ͳ��һ���ж�����ģʽ.
                modes = modes + getCharCode(sPW.charAt(i));
            }
            int result = bitTotal(modes);
            switch (result) {
            case 0:
                this.setValue("pwd_LBL", "��");
                lbl.setColor("RED");
                break;
            case 1:
                this.setValue("pwd_LBL", "��");
                lbl.setColor("��");
                break;
            case 2:
                this.setValue("pwd_LBL", "ǿ");
                lbl.setColor("BLUE");
                break;
            default:
                this.setValue("pwd_LBL", "��");
                lbl.setColor("RED");
                break;
            }
        }
    }

    /**
     * ��֤����ǿ�ȷ�����ʾǿ�ȵ����֣�0 �ͣ�1 �У�2 ǿ
     * @param modes String
     * @return int
     * pangben modify 20110324
     */
    public int bitTotal(String modes) {
        int ans = 0;
        if (modes.contains("1") && modes.contains("2") && modes.contains("3"))
            ans = 2;
        else if (modes.contains("1") && modes.contains("2"))
            ans = 1;
        else if (modes.contains("2") && modes.contains("3"))
            ans = 1;
        else if (modes.contains("1") && modes.contains("3"))
            ans = 1;
        else
            ans = 0;
        return ans;
    }

    /**
     * ͨ����֤ÿһ���ַ���ASCII��ֵ�����Ҫ����ֵ
     * @param iN char
     * @return String
     * pangben modify 20110324
     */
    public String getCharCode(char iN) {
        if (iN >= 48 && iN <= 57) //����
            return "1";
        if (iN >= 65 && iN <= 90) //��д��ĸ
            return "2";
        if (iN >= 97 && iN <= 122) //Сд
            return "2";
        else
            return "3"; //�����ַ�
    }
    /**
     * У������ǿ��
     * @return boolean
     */
    public boolean checkStrong(){
        String value = this.getValueString("pwd_LBL");
        if("��".equals(value)){
            this.messageBox("����ǿ�ȹ���,���޸�����");
            return true;
        }
        return false;
    }
    public static void main(String[] args) {
        com.javahis.util.JavaHisDebug.initClient();
        //  pwdPoofSth();

    }
}
