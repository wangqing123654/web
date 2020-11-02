package com.javahis.ui.hrm;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;

import jdo.bil.BilInvoice;
import jdo.hl7.Hl7Communications;
import jdo.hrm.HRMContractD;
import jdo.hrm.HRMInvRcp;
import jdo.hrm.HRMOpbReceipt;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMPackageD;
import jdo.hrm.HRMPatAdm;
import jdo.hrm.HRMPatInfo;
import jdo.odo.MedApply;
import jdo.opb.OPBReceiptTool;
import jdo.reg.REGSysParmTool;
import jdo.reg.Reg;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.bluecore.cardreader.CardInfoBO;
import com.bluecore.cardreader.IdCardReaderUtil;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.FileTool;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.device.JMFRegistry;
import com.javahis.device.JMStudio;
import com.javahis.system.combo.TComboSex;
import com.javahis.system.textFormat.TextFormatDept;
import com.javahis.util.StringUtil;

/**
 * <p> Title: ���������˱��������� </p>
 *
 * <p> Description: ���������˱��������� </p>
 *
 * <p> Copyright: javahis 20090922 </p>
 *
 * <p> Company:JavaHis </p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMPersonReportControl
    extends TControl {
    //ҽ��TABLE
    private TTable table;
    //���ﲡ��TCheckBox
    private TCheckBox oldPat;
    //MR_NO,PAT_NAME
    private TTextField mrNo, patName, idNo;
    //�ײͣ����壬��ͬTTextFormat
    private TTextFormat pack, company, contract, reportlist, introUser;
    //��������
    private TextFormatDept deptCodeCom;
    //�Ա�
    private TComboSex sex;
    //���没�����շѰ�ť
    private TButton savePat, fee;
    //ҽ������
    private HRMOrder order;
    //������Ϣ����
    private HRMPatInfo pat;
    //��������
    private HRMPatAdm adm;
    //Ʊ�ݶ���
    private HRMOpbReceipt receipt;
    //��ͬϸ�����
    private HRMContractD contractD;
    //�ײ�ϸ�����
    private HRMPackageD packageD;
    
    //�������
    private MedApply med;
    
    //�վݶ���
    private HRMInvRcp invRcp;
    //Ʊ�Ź������
    private BilInvoice bilInvoice;
    //�������
    private String caseNo = "";
    //���²���ע��
    private boolean isNewPat;
    //�ҺŲ��������õ�Ĭ��֧����ʽ
    private String payType = "";
    //����ҳ�����
    private String progamesName = "";
    
    /**
     * �ж��Ƿ���˱������ǻ���գ����ˣ����գ����屨������
     */
    private String flg = "";
    
    /**
     * ������˲����
     */
    private double  discnt = 0;
    /**
     * ��ʼ���¼�
     * 
     */
    public void onInit() {
        super.onInit();
        //��ʼ���ؼ�
        initComponent();       
        //��ʼ������
        initData();
        //���
        onClear();
        
        Object obj = this.getParameter();
        
        if (obj != null && obj.toString().length() != 0) {
            this.setTitle("���廻��");
            
            TParm initParm = (TParm) obj;
            flg = "Y";
            discnt = initParm.getDouble("DISCNT");
                        
            progamesName = initParm.getValue("PRO");
            caseNo = initParm.getValue("CASE_NO");
            this.setValue("MR_NO", initParm.getValue("MR_NO"));
            this.setValue("COM_PAY_FLG", initParm.getValue("COMPANY_PAY_FLG"));
            onCompanyPay();
            mrNo.setEnabled(false);
            this.onMrNo();
            oldPat.setEnabled(false);
            patName.setEnabled(false);
            idNo.setEnabled(false);
            sex.setEnabled(false);
//                if("Y".equals(initParm.getValue("COMPANY_PAY_FLG"))){
            this.callFunction("UI|COM_PAY_FLG|setEnabled", false);
            this.setValue("COMPANY_CODE", initParm.getValue("COMPANY_CODE"));
            this.setValue("CONTRACT_CODE", initParm.getValue("CONTRACT_CODE"));
            this.setValue("REPORTLIST", initParm.getValue("REPORTLIST"));
            this.setValue("INTRO_USER", initParm.getValue("INTRO_USER"));
            this.setValue("PACKAGE_CODE", initParm.getValue("PACKAGE_CODE"));
            this.setValue("DEPT_CODE_COM", initParm.getValue("DEPT_CODE"));
            pack.setEnabled(false);
            company.setEnabled(false);
            contract.setEnabled(false);
            reportlist.setEnabled(false);
            introUser.setEnabled(false);
           // deptCodeCom.setEnabled(false);
            fee.setEnabled(false);
            this.setValue("CHARGE", order.getArAmt());
            this.setValue("REAL", order.getArAmt());
            TNumberTextField real = (TNumberTextField)this.getComponent("REAL");
            real.setEnabled(false);
            //add by lx 2012/05/18 start ��ʾҽ����Ŀ��
            //��ҽ�������
           // caseNo
             onPackageChooseP();
            //��ҽ�������
            /**order.setFilter("");
            order.filter();
            order.setFilter("CASE_NO='" + caseNo +
            "' AND SETMAIN_FLG='Y' AND HIDE_FLG='N' AND BILL_FLG='N' AND EXEC_FLG='N'");
			order.filter();
			//table.setDataStore(order);
			table.setDSValue();
			this.setTableOrderLock();c
			double arAmt = order.getArAmt();
			this.messageBox("arAmt"+arAmt);
			this.setValue("CHARGE", arAmt);
			this.setValue("REAL", arAmt);**/
			 //add by lx 2012/05/18 end 
            
            setTableOrderLock();
            
        }
//            }
        if (progamesName.length() == 0) {
            this.callFunction("UI|save|setEnabled", false);
            this.callFunction("UI|COM_PAY_FLG|setEnabled", false);
        }
        else if (!"Y".equals(this.getValueString("COM_PAY_FLG"))) {
            this.callFunction("UI|save|setEnabled", false);
            fee.setEnabled(true);
            TNumberTextField real = (TNumberTextField)this.getComponent("REAL");
            real.setEnabled(true);
            this.callFunction("UI|COM_PAY_FLG|setEnabled", false);
        }
    }

    /**
     * ����ҽ����
     */
    public void setTableOrderLock() {
        int rowCount = table.getDataStore().rowCount();
        for (int i = 0; i < rowCount; i++) {
            TParm temp = table.getDataStore().getRowParm(i);
            if (!table.getDataStore().isActive(i)) {
                table.setLockCellRow(i, false);
            }
            else {
                table.setLockCellRow(i, true);
            }
        }
        int newRow[] = table.getDataStore().getModifiedRows();
        for (int i : newRow) {
            table.setLockCellRow(i, false);
        }
    }

    /**
     * ��ʼ���ؼ�
     */
    private void initComponent() {
        table = (TTable)this.getComponent("TABLE");
        table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                               "onOrderEditComponent");
        //�ײ�����ֵ�ı��¼�
        table.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE, this,
                               "onValueChanged");
        oldPat = (TCheckBox)this.getComponent("OLD_PAT");
        mrNo = (TTextField)this.getComponent("MR_NO");
        patName = (TTextField)this.getComponent("PAT_NAME");
        savePat = (TButton)this.getComponent("SAVE_PAT");
        fee = (TButton)this.getComponent("FEE");
        pack = (TTextFormat)this.getComponent("PACKAGE_CODE");
        company = (TTextFormat)this.getComponent("COMPANY_CODE");
        contract = (TTextFormat)this.getComponent("CONTRACT_CODE");
        reportlist = (TTextFormat)this.getComponent("REPORTLIST");
        introUser = (TTextFormat)this.getComponent("INTRO_USER");
        deptCodeCom = (TextFormatDept)this.getComponent("DEPT_CODE_COM");
        idNo = (TTextField)this.getComponent("IDNO");
        sex = (TComboSex)this.getComponent("SEX_CODE");
        this.setValue("DEPT_CODE_COM", Operator.getDept());
    }

    /**
     * ֵ�ı��¼�
     * @param tNode TTableNode
     * @return boolean
     */
    public boolean onValueChanged(TTableNode tNode) {
        int column = tNode.getColumn();
        int row = tNode.getRow();
        String colName = table.getParmMap(column);
        //�Ʊ�����
//        this.messageBox("xueyf 1");
        if ("DEPT_ATTRIBUTE".equalsIgnoreCase(colName)) {
            String caseNo = order.getItemString(row, "CASE_NO");
            String deptAttribute = tNode.getValue() + "";
            String orderCode = order.getItemString(row, "ORDER_CODE");
            int groupNo = order.getItemInt(row, "ORDERSET_GROUP_NO");
            order.updateDeptAttribute(deptAttribute, caseNo, orderCode, groupNo);
        }else if("DISPENSE_QTY".equalsIgnoreCase(colName)){
        	double arAmt = 0.0;
        	String comPayFlg = this.getValueString("COM_PAY_FLG");
        	//�����䣬�ܼ۱䣬Ӧ�ս���
        	/**
	    	if(progamesName != null && !progamesName.equals("")){
	 	        if(comPayFlg.equals("Y")){
	 	        	arAmt = order.getArAmt();
	 	        }else{
	 	        	arAmt = order.getArAmt(comPayFlg);
	 	        }
	         }else{
	        	 String filter = order.getFilter() ;
	        	 order.setFilter("");
	        	 order.filter();
	        	 int count = order.rowCount() ;
	        	 for(int i = 0 ; i < count ; i++) {
	        		 double qty = order.getItemDouble(i, "DISPENSE_QTY");
	        		 double ownprice = order.getItemDouble(i, "OWN_PRICE");
	        		 arAmt += StringTool.round(qty*ownprice, 2);
	        	 }
	        	 order.setFilter(filter);
	        	 order.filter();
	         }
	    	
	    	
	         this.setValue("CHARGE", arAmt);
	         this.setValue("REAL", arAmt);
	         */
        	
        }

        return false;
    }

    /**
     * ��ʼ������
     */
    private void initData() {
       
        order = new HRMOrder();
        order.onQuery();

        pat = new HRMPatInfo();
        pat.onQuery();

        packageD = new HRMPackageD();
        contractD = new HRMContractD();

        adm = new HRMPatAdm();
        adm.onQuery();

        receipt = new HRMOpbReceipt();
        invRcp = new HRMInvRcp();
        

        table.setDataStore(order);
        
        
        TParm regParm = REGSysParmTool.getInstance().selPayWay();
        
        
        if (regParm.getErrCode() != 0) {
            this.messageBox_("ȡ������ʧ��");
            return;
        }
//		payType=regParm.getValue("DEFAULT_PAY_WAY",0);
        payType = "C0";
    }

    /**
     * ����¼�����տؼ�������ʼֵ������ȡ����һƱ�ţ���Ԥ���Ƿ�Ϊ���һ�ŷ�Ʊ���ж�
     */
    public void onClear() {
        this.clearValue("PAT_NAME;MR_NO;IDNO;SEX_CODE;BIRTH_DATE;POST_CODE;PROVINCE;CITY;ADDRESS;TEL;DEPT_CODE;REPORT_DATE;COMPANY_CODE;CONTRACT_CODE;CHARGE;CHANGE");
        mrNo.setEnabled(false);
        //��ʼ��������
        TParm selVisitCode = REGSysParmTool.getInstance().selVisitCode();
        if (selVisitCode.getValue("DEFAULT_VISIT_CODE", 0).equals("1")) {
            this.setValue("OLD_PAT", "Y");
            this.callFunction("UI|MR_NO|setEnabled", true);
        }
        else {
            this.setValue("OLD_PAT", "N");
            this.callFunction("UI|MR_NO|setEnabled", false);
        }

        this.setValue("DEPT_CODE_COM", Operator.getDept());
        this.setValue("REPORTLIST", "");
        this.setValue("INTRO_USER", "");
        savePat.setEnabled(true);
        bilInvoice = new BilInvoice("OPB");

        Timestamp now = TJDODBTool.getInstance().getDBTime();
        this.setValue("REPORT_DATE", now);

        this.callFunction("UI|LBL_COM|setVisible", false);
        this.callFunction("UI|LBL_CON|setVisible", false);
        this.callFunction("UI|COMPANY_CODE|setVisible", false);
        this.callFunction("UI|CONTRACT_CODE|setVisible", false);
        // bilInvoice.initBilInvoice("OPB");
        Object obj = this.getParameter();
        if (obj == null || obj.toString().length() == 0) {
            String updateNo = bilInvoice.getUpdateNo();
            this.setValue("UPDATE_NO", updateNo);
            //��˵�ǰƱ��
            if (updateNo.length() == 0 ||
                updateNo == null) {
                this.messageBox_("�޿ɴ�ӡ��Ʊ��!");
                this.callFunction("UI|FEE|setEnabled", false);
            }
            if (updateNo.equals(bilInvoice.getEndInvno())) {
                this.messageBox_("���һ��Ʊ��!");
            }
            if (StringTool.bitDifferOfString(updateNo, bilInvoice.getEndInvno()) <
                0) {
                this.messageBox_("�޿ɴ�ӡ��Ʊ��!");
                this.callFunction("UI|FEE|setEnabled", false);
            }
        }
        else {
            TParm opbParm = (TParm) obj;
            if (!"Y".equals(opbParm.getValue("COMPANY_PAY_FLG"))) {
                String updateNo = bilInvoice.getUpdateNo();
                this.setValue("UPDATE_NO", updateNo);
                //��˵�ǰƱ��
                if (updateNo.length() == 0 ||
                    updateNo == null) {
                    this.messageBox_("�޿ɴ�ӡ��Ʊ��!");
                    this.callFunction("UI|FEE|setEnabled", false);
                }
                if (updateNo.equals(bilInvoice.getEndInvno())) {
                    this.messageBox_("���һ��Ʊ��!");
                }
                if (StringTool.bitDifferOfString(updateNo,
                                                 bilInvoice.getEndInvno()) < 0) {
                    this.messageBox_("�޿ɴ�ӡ��Ʊ��!");
                    this.callFunction("UI|FEE|setEnabled", false);
                }
            }
        }
        order.onClear();
        order.resetModify();
        order.resetMedApply();
        table.setDSValue();
        adm.resetModify();
        pat.resetModify();
        TPanel viewPanel = (TPanel) getComponent("PHOTO_PANEL");
        viewPanel.removeAll();
//		viewPanel.validate();
        viewPanel.repaint();
        pack.setValue("");
        company.setValue("");
        contract.setValue("");

        this.setValue("PAY_TYPE", payType);

        TComboBox sex = (TComboBox)this.getComponent("SEX_CODE");
        sex.setValue("1");
        TNumberTextField change = (TNumberTextField)this.getComponent("CHANGE");
        change.setText("0.00");
        change = (TNumberTextField)this.getComponent("REAL");
        change.setText("0.00");

        setEnableds(true);

        isNewPat = false;
    }

    /**
     * ����CHECK_BOX
     */
    public void isOldPat() {
        // ѡ��״̬
        if (oldPat.isSelected()) {
            mrNo.setValue("");
            this.callFunction("UI|MR_NO|grabFocus");
            mrNo.setEnabled(true);
        }
        else {
            mrNo.setEnabled(false);
        }
    }

    /**
     * ��˾֧��
     */
    public void onCompanyPay() {
//    	this.messageBox("xueyf "+this.getValueString("MR_NO"));
        if (StringUtil.isNullString(this.getValueString("MR_NO"))) {
            return;
        }
        if (TypeTool.getBoolean(this.getValue("COM_PAY_FLG"))) {
            this.callFunction("UI|LBL_COM|setVisible", true);
            this.callFunction("UI|LBL_CON|setVisible", true);
            this.callFunction("UI|COMPANY_CODE|setVisible", true);
            this.callFunction("UI|CONTRACT_CODE|setVisible", true);
        }
        else {
            if (progamesName.length() != 0) {
                this.callFunction("UI|LBL_COM|setVisible", true);
                this.callFunction("UI|LBL_CON|setVisible", true);
                this.callFunction("UI|COMPANY_CODE|setVisible", true);
                this.callFunction("UI|CONTRACT_CODE|setVisible", true);
                return;
            }
            this.callFunction("UI|LBL_COM|setVisible", false);
            this.callFunction("UI|LBL_CON|setVisible", false);
            this.callFunction("UI|COMPANY_CODE|setVisible", false);
            this.callFunction("UI|CONTRACT_CODE|setVisible", false);
        }

    }

    /**
     * �����������齹���¼�����������ݣ�������һ���²�����¼������MR_NO�ŵ�������
     */
    public void onPatName() {
    	//if (this.getValueBoolean("OLD_PAT")) {
        //    return;
        //}
        String patName = this.getValueString("PAT_NAME");
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(patName)) {
            return;
        }
        TParm same = pat.isSamePatByName(patName);
        // System.out.println("same============" + same);
        if (same.getErrCode() != 0) {
            this.messageBox_("ȡ������ʧ��");
        }
        if (same.getCount("MR_NO") > 0) {

            Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
            TParm patParm = new TParm();
            if (obj != null) {
                patParm = (TParm) obj;
                onShowHistory(patParm);
                this.setValue("OLD_PAT", "Y");
                return;
            }
        }
    }

    /**
     * ���֤�Ž����¼��������ȷ�ԣ������ȷ�����������ں��Ա�ֵ
     */
    public void onIdNo() {
        if (this.getValueBoolean("OLD_PAT")) {
            return;
        }
        String idNo = this.getValueString("IDNO");
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(idNo)) {
            return;
        }
        if (!StringTool.isId(idNo)) {
            this.messageBox_("���֤�Ų���ȷ");
            return;
        }
        TParm same = pat.isSamePatByID(idNo);
        if (same.getErrCode() != 0) {
            this.messageBox_("ȡ������ʧ��");
            return;
        }
        // System.out.println("same===========" + same);
        TParm patParm = new TParm();
        if (same.getCount("MR_NO") > 0) {

            Object obj = (TParm) openDialog(
                "%ROOT%\\config\\sys\\SYSPatChoose.x", same);
            if (obj != null) {
                patParm = (TParm) obj;
                onShowHistory(patParm);
                return;
            }
        }
        Timestamp birthday = StringTool.getBirdayFromID(idNo);
        this.setValue("BIRTH_DATE", birthday);
        String sexCode = StringTool.isMaleFromID(idNo);
        this.setValue("SEX_CODE", sexCode);
        mrNo = pat.getMrNo();
        this.setValue("MR_NO", mrNo);
    }

    /**
     * �����ע��
     */
    public void onForeign() {
        if (this.getValueBoolean("OLD_PAT"))
            return;
        boolean isForeign = this.getValueBoolean("FOREIGNER_FLG");
        if (StringUtil.isNullString(this.getValueString("PAT_NAME"))) {
            return;
        }
        if (isForeign) {
            if (StringUtil.isNullString(this.getValueString("MR_NO"))) {
                String mrNo = pat.getMrNo();
                this.setValue("MR_NO", mrNo);
            }
        }
    }

    /**
     * ���ݷ��ص���ʷ��ʾ��ʷ����
     * @param patParm TParm
     */
    private void onShowHistory(TParm patParm) {
        this.setValueForParm(
            "PAT_NAME;MR_NO;IDNO;BIRTH_DATE;SEX_CODE;ADDRESS;IDNO;TEL;POST_CODE",
            patParm);
        this.setValue("IDNO", patParm.getValue("ID_NO"));
        String postCode = patParm.getValue("POST_CODE");
        if (!StringUtil.isNullString(postCode)) {
            if (postCode.trim().length() == 6) {
                this.setValue("PROVINCE", postCode.substring(0, 2));
                this.setValue("CITY", postCode);
            }
        }
        savePat.setEnabled(false);
        int row = -1;
        caseNo = patParm.getValue("CASE_NO");
        this.onMrNo();
//                this.messageBox_(caseNo);
//		if(order.rowCount()-1<0){
//                    row = order.insertRow( -1, caseNo);
//                    order.setActive(row, false);
//		}
//		if(order.rowCount()-1>-1&&!StringUtil.isNullString(order.getItemString(order.rowCount()-1, "ORDER_CODE"))){
//                    row = order.insertRow( -1, caseNo);
//                    order.setActive(row, false);
//		}
//		order.setFilter("CASE_NO='"+caseNo+"' AND SETMAIN_FLG='Y' AND HIDE_FLG='N' AND BILL_FLG='N' AND EXEC_FLG='N'");
//		order.filter();
//		table.setDSValue();
//		viewPhoto(patParm.getValue("MR_NO"));
//		isNewPat=false;
    }

    /**
     * ���ý���ؼ�ENEABLE
     * @param enabled boolean
     */
    private void setEnableds(boolean enabled) {
        this.callFunction("UI|OLD_PAT|setEnabled", enabled);
        this.callFunction("UI|PAT_NAME|setEnabled", enabled);
        this.callFunction("UI|IDNO|setEnabled", enabled);
        this.callFunction("UI|SEX_CODE|setEnabled", enabled);
        this.callFunction("UI|BIRTH_DATE|setEnabled", enabled);
        this.callFunction("UI|POST_CODE|setEnabled", enabled);
        this.callFunction("UI|ADDRESS|setEnabled", enabled);
        this.callFunction("UI|TEL|setEnabled", enabled);
        this.callFunction("UI|DEPT_CODE_COM|setEnabled", enabled);
        this.callFunction("UI|REPORT_DATE|setEnabled", enabled);
        this.callFunction("UI|PACKAGE_CODE|setEnabled", enabled);
//		this.callFunction("UI|COM_PAY_FLG|setEnabled",enabled);
    }

    /**
     * ���SYS_FEE��������
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onOrderEditComponent(Component com, int row, int column) {
        // �����ǰ�к�
        column = table.getColumnModel().getColumnIndex(column);
        String columnName = table.getParmMap(column);
        if (!"ORDER_DESC".equalsIgnoreCase(columnName)) {
            return;
        }
        if (! (com instanceof TTextField)) {
            return;
        }
        if (!StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        TParm parm = new TParm();
        parm.setData("HRM_TYPE", "ANYCHAR");
        // ��table�ϵ���text����sys_fee��������
        textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // ����text���ӽ���sys_fee�������ڵĻش�ֵ
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                   "popOrderReturn");
    }

    /**
     * ����ҽ��
     * @param tag String
     * @param obj Object
     */
    public void popOrderReturn(String tag, Object obj) {
        table.acceptText();
        TParm parm = (TParm) obj;
        parm.setData("MR_NO", this.getValueString("MR_NO"));
        parm.setData("CASE_NO", caseNo);
        int clickRow = table.getSelectedRow();
        
        String flag = (String) table.getItemData(clickRow, 0);
		if (flag == null || flag.equals("")) {
			flag = "N";
		}

        //����Ĭ��Ϊ����
        parm.setData("NEW_FLG",flag);
        parm.setData("DEPT", this.getValueString("DEPT_CODE_COM"));
        TParm patParm = this.getParmForTag(
            "PAT_NAME;MR_NO;IDNO;BIRTH_DATE;SEX_CODE;ADDRESS;IDNO;TEL;POST_CODE");
        
        int selRow = order.rowCount()-1;
    	// �ж��Ƿ��Ǽ���ҽ��
		if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
			order.initOrder(parm, patParm,flg,discnt,clickRow);
		}else{
			order.initNotOrderSet(parm, patParm,flg,discnt,clickRow);
			selRow = order.insertRow( -1, caseNo);
            order.setItem(selRow, "CASE_NO", caseNo);
            order.setItem(selRow, "SETMAIN_FLG", "Y");
            order.setItem(selRow, "HIDE_FLG", "N");
            order.setItem(selRow, "BILL_FLG", "N");
            order.setItem(selRow, "EXEC_FLG", "N");
            order.setItem(selRow, "NEW_FLG", flag);
            order.setActive(selRow, false);
	
		}
		if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
	        int row = -1;
	        if (!StringUtil.isNullString(order.getItemString(order.rowCount() - 1,
	            "ORDER_CODE"))) {
	            row = order.insertRow( -1, caseNo);
	            order.setItem(row, "CASE_NO", caseNo);
	            order.setItem(row, "SETMAIN_FLG", "Y");
	            order.setItem(row, "HIDE_FLG", "N");
	            order.setItem(row, "BILL_FLG", "N");
	            order.setItem(row, "EXEC_FLG", "N");
	            order.setItem(row, "NEW_FLG", flag);
	            order.setActive(row, false);
	        }
		}

        order.filter();
        
        table.setDSValue();
        table.getTable().grabFocus();
        table.setSelectedRow(clickRow);
        int column = table.getColumnIndex("ORDER_DESC");
        table.setSelectedColumn(column);
               
        String comPayFlg = this.getValueString("COM_PAY_FLG");
        double arAmt =  0.0;
        if(progamesName != null && !progamesName.equals("")){
	        if(comPayFlg.equals("Y")){
	        	arAmt = order.getArAmt();
	        }else{
	        	arAmt = order.getArAmt(comPayFlg);
	        }
        }else{
        	arAmt = order.getArAmt();
        }
        this.setValue("CHARGE", arAmt);
        this.setValue("REAL", arAmt);
        final TNumberTextField real = (TNumberTextField)this.getComponent(
            "REAL");
        real.grabFocus();
        real.selectAll();
//      receipt.onQuery();
//      // System.out.println(receipt.getSQL());
//      String receiptNo = receipt.getItemString(receipt
//              .rowCount() - 1, "RECEIPT_NO");
//      double arAmt1 = OPBReceiptTool.getInstance().getOneReceipt(receiptNo).getDouble("AR_AMT", 0);
//     // this.messageBox(arAmt1+"");
    }

    /**
     *  ���没����Ϣ
     */
    public void onSavePat() {
        TParm parm = this
            .getParmForTag(
                "PAT_NAME;MR_NO;IDNO;BIRTH_DATE;SEX_CODE;ADDRESS;IDNO;TEL;POST_CODE");
        parm.setData("FOREIGNER_FLG", this.getValueString("FOREIGNER_FLG"));
        String mrNo = this.getValueString("MR_NO");
        int oldPat = pat.isOldPat(mrNo);
        if (oldPat == -1) {
            this.messageBox_("ȡ������ʧ��");
            return;
        }
        //�²���
        if (oldPat == 1) {
            pat.newPat(parm);
        }
        else {
            pat.updatePat(parm);
        }
        int count = order.rowCount();
        if (count >= 1) {
            this.messageBox("�Ѿ�����ҽ������Ա�����ٱ��������Ϣ");
            return;
        }
        if (this.getValueString("PAT_NAME").length() == 0) {
            this.messageBox("��������Ϊ�գ�");
            return;
        }
        adm.onNewAdm(parm);
        TParm result = pat.onSave();
        if (result.getErrCode() != 0) {
//			 this.messageBox_(result.getErrText());
            this.messageBox("E0001");
            return;
        }
        isNewPat = true;
        this.messageBox("P0001");
        caseNo = adm.getItemString(adm.rowCount() - 1, "CASE_NO");
        order.filt(caseNo);
        int row = -1;
        if (order.rowCount() < 1) {
            row = order.insertRow( -1, caseNo);
        }
        else if (order.rowCount() >= 1 &&
                 !StringUtil.isNullString(order.
                                          getItemString(order.rowCount() - 1,
            "ORDER_CODE"))) {
            row = order.insertRow( -1, caseNo);
        }
        order.setActive(row, false);
        table.setDSValue();
        this.setTableOrderLock();
        Image image = (Image)this.getValue("PHOTO_PANEL");
        if (image != null) {
            this.sendpic(image);
        }

    }

    /**
     * ��˶�����Ϣ
     */
    private void checkReportList() {
        String reportlist = this.getValueString("REPORTLIST");
        if (StringUtil.isNullString(reportlist)) {
            this.messageBox_("������ϢΪ�գ��ڵ��������޷���ӡ");
            return;
        }
    }

    /**
     * ��˵�����Ա
     */
    private void checkIntroUser() {
        String introUser = this.getValueString("INTRO_USER");
        if (StringUtil.isNullString(introUser)) {
            this.messageBox_("������ԱΪ�գ��ڵ��������޷���ӡ");
            return;
        }
    }

    /**
     * ʵ�ս��س��¼�
     */
    public void onReal() {
        double real = this.getValueDouble("REAL");
        double charge = this.getValueDouble("CHARGE");
        if (real < charge) {
            return;
        }
        double change = real - charge;
        this.setValue("CHANGE", change);
        fee.grabFocus();
    }

    /**
     * ����
     */
    public void onSaveCom() {
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(mrNo)) {
            this.messageBox_("�޿��շѲ���");
            return;
        }
        int row = table.getRowCount() - 1;
        if (row < 1) {
            this.messageBox_("�޿��շ�ҽ��");
            return;
        }
        if (StringUtil.isNullString(caseNo)) {
            this.messageBox_("ȡ�þ�����Ŵ���");
            return;
        }
        if (StringUtil.isNullString(this.getValueString("DEPT_CODE_COM"))) {
            this.messageBox_("�������Ų���Ϊ��");
            return;
        }
        String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
        int newRow[] = order.getNewRows(buff);
        int medRow[] = order.getModifiedRows(buff);
        if (newRow.length + medRow.length + order.getDeleteCount() <= 0) {
            this.messageBox("�ޱ�������");
            return;
        }
        //���������֧���Ļ�����Ʊ
        if (!StringUtil.isNullString(this.getValueString("COMPANY_CODE")) &&
            !StringUtil.isNullString(this.getValueString("CONTRACT_CODE"))) {
            String filterString = order.getFilter();
            order.setFilter("CASE_NO='" + caseNo + "'");
            order.filter();
            if (!order.fillSeqNo(this.getValueString("DEPT_CODE_COM"))) {
//				messageBox("E0001");
                onClear();
                return;
            }
            //�����������������д��ORDER
            onComPayOrder();
//                        adm.setItem(adm.rowCount()-1, "PACKAGE_CODE", this.getValue("PACKAGE_CODE"));
//                        adm.setItem(adm.rowCount()-1, "REPORTLIST", this.getValue("REPORTLIST"));
//                        adm.setItem(adm.rowCount()-1, "INTRO_USER", this.getValue("INTRO_USER"));
            TParm delParm = order.getBuffer(order.DELETE);
            
            int rowCount = order.rowCount() ;
            for(int i = 0 ; i < rowCount ; i++ ){
            	String newFlg = order.getItemString(order.rowCount() - 1,"NEW_FLG");
            	if(!"N".equals(newFlg)){
            		order.setActive(i,false);
            	}
            }
            String[] sql = order.getUpdateSQL();
  
            sql = StringTool.copyArray(sql, order.getMedApply().getUpdateSQL());
//                        sql=StringTool.copyArray(sql, adm.getUpdateSQL());
            TParm inParm = new TParm();

            Map inMap = new HashMap();
            
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm result = TIOM_AppServer.executeAction(
                "action.hrm.HRMPersonReportAction", "onSave", inParm);
            if (result.getErrCode() != 0) {
//				this.messageBox_(result.getErrText());
                this.messageBox("E0001");
                return;
            }else{
            	this.messageBox("����ɹ�");
            }
            List listHl7 = new ArrayList();
//			onBarCode();
//                        TParm sendHl7=order.sendHl7();
//                        if(sendHl7.getErrCode()<0){
////				this.messageBox_(sendHl7.getErrText());
//                                this.messageBox_("������Ϣʧ��");
//                        }
            //ȡ��ҽ������
            if (delParm.getCount("ORDER_CODE") > 0) {
                List listHl7Del = new ArrayList();
                getHl7ListDel(listHl7Del, caseNo, delParm);
                // System.out.println("LISTDEL" + listHl7Del);
                TParm hl7ParmDel = Hl7Communications.getInstance().Hl7Message(
                    listHl7Del);
                if (hl7ParmDel.getErrCode() < 0) {
                    this.messageBox(hl7ParmDel.getErrText());
                }
            }
            //��ҽ������
            if (newRow.length > 0) {
                getHl7List(listHl7, caseNo);
                // System.out.println("LIST" + listHl7);
                TParm hl7Parm = Hl7Communications.getInstance().Hl7Message(
                    listHl7);
                if (hl7Parm.getErrCode() < 0) {
                    this.messageBox(hl7Parm.getErrText());
                }
            }

            reportPrint();
            onClear();
            table.getDataStore().setFilter(filterString);
            table.getDataStore().filter();
            table.setDSValue();
            setTableOrderLock();
            onInit();
            return;
        }

    }

    /**
     * �õ�HL7����
     * @param listHl7 List
     * @param caseNo String
     * @param order TParm
     * @return List
     */
    public List getHl7ListDel(List listHl7, String caseNo, TParm order) {
        int rowDelCount = order.getCount("SETMAIN_FLG");
        // System.out.println("DELORDER" + order + "==" + rowDelCount);
        for (int i = 0; i < rowDelCount; i++) {
            TParm tempD = order.getRow(i);
            if ("Y".equals(tempD.getValue("SETMAIN_FLG"))) {
                String sql = "SELECT CAT1_TYPE,PAT_NAME,CASE_NO,APPLICATION_NO AS LAB_NO,ORDER_NO,SEQ_NO FROM MED_APPLY WHERE ADM_TYPE='H' AND CASE_NO='" +
                    caseNo + "' AND ORDER_CODE='" + tempD.getValue("ORDER_CODE") +
                    "' AND APPLICATION_NO='" + tempD.getValue("MED_APPLY_NO") +
                    "' AND SEND_FLG=2";
                // System.out.println("SQLMEDDEL==" + sql);
                TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
                int rowCount = parm.getCount();
                for (int j = 0; j < rowCount; j++) {
                    TParm temp = parm.getRow(j);
                    temp.setData("ADM_TYPE", "H");
                    temp.setData("FLG", 1);
                    listHl7.add(temp);
                }
            }
        }
        return listHl7;
    }

    /**
     * �õ�HL7����
     * @param listHl7 List
     * @param caseNo String
     * @return List
     */
    public List getHl7List(List listHl7, String caseNo) {
        String sql = "SELECT CAT1_TYPE,PAT_NAME,CASE_NO,APPLICATION_NO AS LAB_NO,ORDER_NO,SEQ_NO FROM MED_APPLY WHERE ADM_TYPE='H' AND CASE_NO='" +
            caseNo + "' AND SEND_FLG < 2";
        // System.out.println("SQLMED==" + sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        // System.out.println("parm=====" + parm);
        int rowCount = parm.getCount();
        for (int i = 0; i < rowCount; i++) {
            TParm temp = parm.getRow(i);
            temp.setData("ADM_TYPE", "H");
            temp.setData("FLG", 0);
            listHl7.add(temp);
        }
        return listHl7;
    }

    private void forSeq(){
        int row = order.rowCount();
        int seq = 1;
        for(int i = 0;i < row;i++){
            if(!order.isActive(i))
                continue;
            order.setItem(i, "SEQ_NO", seq);
            TParm temp = order.getRowParm(i);
            int rowMedCount = order.getMedApply().rowCount();
            for(int j=0;j<rowMedCount;j++){
                if(!order.getMedApply().isActive(j))
                    continue;
                TParm tempMed = order.getMedApply().getRowParm(j);
                if(temp.getValue("CAT1_TYPE").equals(tempMed.getValue("CAT1_TYPE"))&&temp.getValue("MED_APPLY_NO").equals(tempMed.getValue("APPLICATION_NO"))&&temp.getValue("CASE_NO").equals(tempMed.getValue("ORDER_NO"))&&temp.getValue("ORDER_CODE").equals(tempMed.getValue("ORDER_CODE"))){
                    order.getMedApply().setItem(j,"SEQ_NO",seq);
                }
            }
            seq++;
        }
    }

    /**
     * �շ��¼�,����hrm_patadm,hrm_order
     */
    public void onFee() {
        table.acceptText();
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(mrNo)) {
            this.messageBox_("�޿��շѲ���");
            return;
        }
        int row = table.getRowCount() - 1;
        if (row < 1) {
            this.messageBox_("�޿��շ�ҽ��");
            return;
        }
        if (StringUtil.isNullString(caseNo)) {
            this.messageBox_("ȡ�þ�����Ŵ���");
            return;
        }
        if (StringUtil.isNullString(this.getValueString("DEPT_CODE_COM"))) {
            this.messageBox_("�������Ų���Ϊ��");
            return;
        }
        double real = StringTool.getDouble(this.getValue("REAL") + "");
        double charge = StringTool.getDouble(this.getValue("CHARGE") + "");
        String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
        int newRow[] = order.getNewRows(buff);
        int medRow[] = order.getModifiedRows(buff);
        if (newRow.length + medRow.length + order.getDeleteCount() <= 0) {
            this.messageBox("�ޱ�������");
            return;
        }
        if (real < charge) {
            this.messageBox_("�շѽ��������ܽ��");
            return;
        }
        String filterString = order.getFilter();
        order.setFilter("CASE_NO='" + caseNo + "'");
        order.filter();
        
        //progamesNames.length==0�Ǹ��˱���������Ϊ���屨����
        if(progamesName.length() == 0){
            forSeq();
        }else {
	        if (!order.setSeqNoOrder(caseNo)) {
     			messageBox("E0001");
	            onClear();
	            return;
	        }
        }
       
        if (this.progamesName.length() == 0) {
            if (!order.isHaveMainOrder()) {
                this.messageBox("�������ܼ�ҽ��");
                order.setFilter(filterString);
                order.filter();
                return;
            }
        }
        //�õ�Ҫ�Ʒѵ���Ŀ
        TParm newParmOrder = new TParm();
        int newParmOrderCount = order.rowCount();
        for (int p = 0; p < newParmOrderCount; p++) {
            if (!order.isActive(p))
                continue;
            if (!"Y".equals(order.getItemString(p, "#NEW#")))
                continue;
            newParmOrder.setRowData(p, order.getBuffer(order.PRIMARY).getRow(p));
        }
        newParmOrder.setCount(newParmOrder.getCount("REXP_CODE"));
        //ҽ�ƿ��շѿ۷�
        String payTypePage = this.getValueString("PAY_TYPE");
        if ("EKT".equals(payTypePage)) {
            //ҽ�ƿ��շ�
            this.messageBox("��֧��ҽ�ƿ��շѣ�");
            return;

        }else {
            // �շѽӿ�
            receipt.onQuery();
            TParm receiptParm = new TParm();
            receiptParm.setData("CASE_NO", caseNo);
            //============xueyf modify 20120308 start
            receiptParm.setData("PAY_REMARK", this.getValueString("PAY_REMARK"));
          //============xueyf modify 20120308 stop
            receiptParm.setData("MR_NO", this.getValueString("MR_NO"));
            receiptParm.setData("PRINT_NO",
                                this.getValueString("UPDATE_NO"));
            receiptParm.setData("ORDER_PARM", newParmOrder);
            receiptParm.setData("PAY_TYPE",
                                this.getValueString("PAY_TYPE"));
            if (!receipt.insert(receiptParm, 1)) {
                this.messageBox_("ȡ�÷�Ʊ��ʧ��");
                onClear();
                return;
            }
            TParm delParm = order.getBuffer(order.DELETE);
            order.setFilter(filterString);
            order.filter();
            invRcp.onQuery();
            TParm invRcpParm = new TParm();
            invRcpParm.setData("RECP_TYPE", "OPB");
            invRcpParm.setData("CANCEL_FLG", "0");
            invRcpParm.setData("INV_NO",
                               this.getValueString("UPDATE_NO"));
            String receiptNo = receipt.getItemString(receipt
                .rowCount() - 1, "RECEIPT_NO");
            invRcpParm.setData("RECEIPT_NO", receiptNo);
            invRcpParm.setData("AR_AMT", receipt.getItemDouble(
                receipt.rowCount() - 1, "TOT_AMT"));
            invRcpParm.setData("STATUS", "0");
            invRcp.insert(invRcpParm);
            String orderReceipt = receipt.getItemString(receipt
                .rowCount() - 1, "RECEIPT_NO");
            order.updateReceiptNo(orderReceipt,
                                  this.getValueString("DEPT_CODE_COM"));
            String[] sql = new String[] {};
            checkReportList();
            checkIntroUser();
            if (this.progamesName.length() == 0) {
                adm.setItem(adm.rowCount() - 1, "PACKAGE_CODE",
                            this.getValue("PACKAGE_CODE"));
                adm.setItem(adm.rowCount() - 1, "REPORTLIST",
                            this.getValue("REPORTLIST"));
                adm.setItem(adm.rowCount() - 1, "INTRO_USER",
                            this.getValue("INTRO_USER"));
                sql = adm.getUpdateSQL();
            }
             
           
            sql = StringTool.copyArray(sql, order.getUpdateSQL());
            sql = StringTool.copyArray(sql, receipt.getUpdateSQL());
            sql = StringTool.copyArray(sql, invRcp.getUpdateSQL());

            String sqlUpdate =
                "UPDATE BIL_INVOICE SET UPDATE_NO='#' WHERE RECP_TYPE='OPB' AND START_INVNO='#'";
            sqlUpdate = sqlUpdate.replaceFirst("#",
                                               StringTool.addString(bilInvoice
                .getUpdateNo())).replaceFirst("#",
                                              bilInvoice.getStartInvno());
            String[] sqlUpdateInvoice = new String[] {
                sqlUpdate};
            sql = StringTool.copyArray(sql, sqlUpdateInvoice);
            sql = StringTool.copyArray(sql,
                                       order.getMedApply().getUpdateSQL());
            TParm inParm = new TParm();

            Map inMap = new HashMap();
            inMap.put("SQL", sql);
          
            inParm.setData("IN_MAP", inMap);
            //����HIS���ݿ�
            TParm result = TIOM_AppServer.executeAction(
                "action.hrm.HRMPersonReportAction", "onSave", inParm);
            if (result.getErrCode() != 0) {
                this.messageBox("E0001");
                return;
            }
            List listHl7 = new ArrayList();
            //ȡ��ҽ������
            if (delParm.getCount("ORDER_CODE") > 0) {
                List listHl7Del = new ArrayList();
                getHl7ListDel(listHl7Del, caseNo, delParm);
                // System.out.println("LISTDEL" + listHl7Del);
                TParm hl7ParmDel = Hl7Communications.getInstance().
                    Hl7Message(listHl7Del);
                if (hl7ParmDel.getErrCode() < 0) {
                    this.messageBox(hl7ParmDel.getErrText());
                }
            }
            //��ҽ������
            //xueyf ��ʱ����
//            if (newRow.length > 0) {
//                getHl7List(listHl7, caseNo);
//                // System.out.println("LIST" + listHl7);
//                TParm hl7Parm = Hl7Communications.getInstance().
//                    Hl7Message(listHl7);
//                if (hl7Parm.getErrCode() < 0) {
//                    this.messageBox(hl7Parm.getErrText());
//                }
//            }
            //xueyf ��ʱ����
            String[] receipt = new String[] {
                receiptNo};
            dealPrintData(receipt, this.getValueString("PAT_NAME"),
                          this.getValueString("DEPT_CODE_COM"),
                          this.getValueString("SEX_CODE"));
            reportPrint();
            table.setDSValue();
            this.onInit();
        }
        return;
    }

    /**
     * ��ӡ������
     */
    public void onReportPrint() {

    }

    /**
     * ȡ�õ��������ݲ���ӡ
     */
    private void reportPrint() {
        TParm parm = new TParm();
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(mrNo) || StringUtil.isNullString(caseNo)) {
//                        this.messageBox_(1);
            this.messageBox_("ȡ������ʧ��");
            return;
        }
        parm = order.getReportTParm(mrNo, caseNo);
        if (parm == null) {
//                    this.messageBox_(2);
            this.messageBox_("ȡ������ʧ��");
            return;
        }
        if (parm.getErrCode() != 0) {
//			this.messageBox_(parm.getErrText());
//                    this.messageBox_(3);
            this.messageBox_("ȡ������ʧ��");
            return;
        }
        openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMReportSheetNew.jhw", parm, false);
    }

    /**
     * ɾ��
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
        TParm orderParm = table.getDataStore().getRowParm(row);
        String orderSetCode = orderParm.getValue("ORDER_CODE");
        int groupNo = orderParm.getInt("ORDERSET_GROUP_NO");
        int rowId = (Integer) table.getDataStore().getItemData(row, "#ID#",
            buff);
        if ("Y".equals(orderParm.getValue("EXEC_FLG"))) {
            this.messageBox("�Ѿ�ִ�в�����ɾ����");
            return;
        }
        if ("Y".equals(orderParm.getValue("BILL_FLG"))) {
                this.messageBox("�Ѿ��շ�ҽ��������ɾ����");
                return;
            }
        String filterStr = order.getFilter();
        order.setFilter("");
        order.filter();
        int rowRCount = order.isFilter() ? order.rowCountFilter() :
            order.rowCount();
        if ("Y".equals(orderParm.getValue("SETMAIN_FLG"))) {
        	
        	//����ҽ����ҩƷ��ֻɾ���Լ�����û��ϸ��
        	if("PHA_W".equals(orderParm.getValue("ORDER_CAT1_CODE"))){
        		String orderCode = orderParm.getValue("ORDER_CODE");
        		for(int i = rowRCount -1  ; i > -1 ; i--  ){
        			if(orderCode.equals(order.getItemData(i, "ORDER_CODE"))){
        				order.deleteRow(i);
        			}
        		}
        	}else{
	            for (int i = rowRCount - 1; i > -1; i--) {
	                if (orderSetCode.equalsIgnoreCase(order.getItemString(i,
	                    "ORDERSET_CODE")) && groupNo == order.getItemInt(i, "ORDERSET_GROUP_NO")) {
	                    order.deleteRow(i);
	                }
	            }
        	}
        }else {
            for (int i = rowRCount - 1; i > -1; i--) {
                if ( (Integer) order.getItemData(i, "#ID#",
                                                 order.isFilter() ?
                                                 order.FILTER : order.PRIMARY) ==rowId)
                    order.deleteRow(i,
                                    order.isFilter() ? order.FILTER :
                                    order.PRIMARY);
            }
        }
//		if(!order.removeSetOrder(row)){
//			this.messageBox_("ɾ��ʧ��");
//		}
        table.setFilter(filterStr);
        table.filter();
        table.setDSValue();
        this.setTableOrderLock();
        this.setValue("CHARGE", order.getArAmt());
        this.setValue("REAL", order.getArAmt());
    }
	/**
	 * �����¼ѡ��
	 */
	public void onRecord() {
		// ��ʼ��pat
		Pat	pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null) {
			messageBox_("���޴˲�����!");
			// ���޴˲��������ܲ��ҹҺ���Ϣ
			callFunction("UI|record|setEnabled", false);
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("SEX_CODE", pat.getSexCode());
		parm.setData("AGE", getValue("AGE"));
		// �ж��Ƿ����ϸ�㿪�ľ����ѡ��
		parm.setData("count", "0");
		String caseNo = (String) openDialog(
				"%ROOT%\\config\\opb\\OPBChooseVisit.x", parm);
		if (caseNo == null || caseNo.length() == 0 || caseNo.equals("null")) {
			return;
		}
		Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
		if (reg == null) {
			messageBox("�Һ���Ϣ����!");
			return;
		}
		// reg�õ������ݷ������
//		afterRegSetValue();
//		// ͨ��reg��caseNo�õ�pat
//		opb = OPB.onQueryByCaseNo(reg);
//		serviceLevel = opb.getReg().getServiceLevel();
//		this.setValue("SERVICE_LEVEL", serviceLevel);
//		onlyCaseNo = opb.getReg().caseNo();
//		if (opb == null) {
//			// this.messageBox_(33333333);
//			this.messageBox_("�˲�����δ����!");
//			return;
//		}
//		// ��ʼ��opb�����ݴ���
//		afterInitOpb();
	}
    /**
     * MR_NO�س��¼�
     */
    public void onMrNo() {
    	//�ϲ������Ŵ���
    	
    	if (!this.queryPat(this.getValueString("MR_NO").trim()))
              return;
    	Pat pat1=Pat.onQueryByMrNo(this.getValueString("MR_NO").trim());
        String mrNo =pat1.getMrNo();
        mrNo = PatTool.getInstance().checkMrno(mrNo);
        this.setValue("MR_NO", mrNo);
       
        TParm parm = this.getParmForTag(
            "PAT_NAME;MR_NO;IDNO;BIRTH_DATE;SEX_CODE;ADDRESS;IDNO;TEL;POST_CODE");
        // System.out.println("MR_NO" + mrNo);
        pat.onQuery(mrNo);
        if (pat.rowCount() <= 0) {
            this.messageBox_("�����Ų�����");
            onClear();
            return;
        }
        TParm patParm = pat.getBuffer(pat.PRIMARY).getRow(0);
        this.setValueForParm("PAT_NAME;MR_NO;IDNO;BIRTH_DATE;SEX_CODE;ADDRESS;IDNO;TEL;POST_CODE;FOREIGNER_FLG",
                             patParm);
        String postCode = patParm.getValue("POST_CODE");
        if (!StringUtil.isNullString(postCode)) {
            if (postCode.trim().length() == 6) {
                this.setValue("PROVINCE", postCode.substring(0, 2));
                this.setValue("CITY", postCode);
            }
        }
        this.setValue("TEL", patParm.getValue("TEL_HOME"));
        if(progamesName.length() == 0)
            caseNo = "";
        if (this.caseNo.length() == 0) {
            TParm caseNoParm = new TParm();
            caseNoParm.setData("MR_NO", mrNo);
            TParm pats = new TParm(TJDODBTool.getInstance().select("SELECT CASE_NO,PAT_NAME,SEX_CODE,COMPANY_CODE,CONTRACT_CODE,PACKAGE_CODE,MR_NO FROM HRM_PATADM WHERE MR_NO='" +
                mrNo + "'"));
            if (pats.getCount() > 0 &&
                this.messageBox("��ʾ��Ϣ", "�Ƿ����¾��", this.YES_NO_OPTION) !=0) {
                if(pats.getCount() > 1){
                //============xueyf modify 20120301 start
                    Object obj = this.openDialog(
                            "%ROOT%\\config\\hrm\\HRMCheckCaseNOUI.x",
                            caseNoParm);
                    //============xueyf modify 20120301 stop
                    if (obj != null) {
                        TParm patCaseParm = (TParm) obj;
                        order.onQueryByCaseNo(patCaseParm.getValue("CASE_NO"));
                        caseNo = patCaseParm.getValue("CASE_NO");
                    } else {
                        adm.onNewAdm(patParm);
                        caseNo = adm.getItemString(adm.rowCount() - 1,"CASE_NO");
                        order.onQueryByCaseNo(caseNo);
                    }
                }
                else{
                    order.onQueryByCaseNo(pats.getValue("CASE_NO",0));
                    caseNo = pats.getValue("CASE_NO",0);
                }

            }
            else {
                adm.onNewAdm(patParm);
                caseNo = adm.getItemString(adm.rowCount() - 1, "CASE_NO");
                order.onQueryByCaseNo(caseNo);
            }
            /*if (pats.getCount() > 1) {
                Object obj = this.openDialog(
                    "%ROOT%\\config\\hrm\\HRMCheckCaseNOUI.x", caseNoParm);
                if (obj != null) {
                    TParm patCaseParm = (TParm) obj;
                    order.onQueryByCaseNo(patCaseParm.getValue("CASE_NO"));
                }
                else {
                    adm.onNewAdm(patParm);
                    caseNo = adm.getItemString(adm.rowCount() - 1, "CASE_NO");
                    order.onQueryByCaseNo(caseNo);
                }
            }
            else {
                adm.onNewAdm(patParm);
                caseNo = adm.getItemString(adm.rowCount() - 1, "CASE_NO");
                order.onQueryByCaseNo(caseNo);
            }*/
        }
        else {
            if (!TypeTool.getBoolean(this.getValue("COM_PAY_FLG"))) {
//                        adm.onNewAdm(patParm);
//                        caseNo=adm.getItemString(adm.rowCount()-1, "CASE_NO");
                if (this.progamesName.length() == 0) {
                    if (this.messageBox("��ʾ��Ϣ", "�Ƿ����¾��", this.YES_NO_OPTION) !=
                        0) {
                        TParm caseNoParm = new TParm();
                        caseNoParm.setData("MR_NO", mrNo);
                        TParm pats = new TParm(TJDODBTool.getInstance().select("SELECT CASE_NO,PAT_NAME,SEX_CODE,COMPANY_CODE,CONTRACT_CODE,PACKAGE_CODE,MR_NO FROM HRM_PATADM WHERE MR_NO='" +
                            mrNo + "'"));
                        if (pats.getCount() > 1) {
                        	 //============xueyf modify 20120301 start
                            Object obj = this.openDialog(
                                    "%ROOT%\\config\\hrm\\HRMCheckCaseNOUI.x",
                                    caseNoParm);
                            //============xueyf modify 20120301 stop

                            if (obj != null) {
                                TParm patCaseParm = (TParm) obj;
                                order.onQueryByCaseNo(patCaseParm.getValue(
                                    "CASE_NO"));
                            }
                            else {
                                order.onQueryByCaseNo(caseNo);
                            }
                        }
                        else {
                            order.onQueryByCaseNo(caseNo);
                        }
                    }
                    else {
                        adm.onNewAdm(patParm);
                        caseNo = adm.getItemString(adm.rowCount() - 1,
                            "CASE_NO");
                        order.onQueryByCaseNo(caseNo);
                    }
                }
                else {
                    order.onQueryByCaseNo("#");
                }
            }
            else {
                order.onQueryByCaseNo(caseNo);
            }
        }
        //xueyf �����Ա����ҽ��
        setTableOrderLock();
//		order.filt(caseNo);
        int row = order.insertRow( -1, caseNo);
        order.setItem(row, "CASE_NO", caseNo);
        order.setItem(row, "SETMAIN_FLG", "Y");
        order.setItem(row, "HIDE_FLG", "N");
        order.setItem(row, "BILL_FLG", "N");
        order.setItem(row, "EXEC_FLG", "N");
        order.setActive(row, false);
        table.setDataStore(order);
        table.setDSValue();
        viewPhoto(mrNo);
        table.getTable().grabFocus();
    }

    /**
     * �˷ѽ���
     */
    public void onBackReceipt() {
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(mrNo)) {
            this.messageBox_("û������");
            return;
        }
        TParm same = pat.getCaseByMr(mrNo);
        String tempCase = "";
        if (same.getCount() > 0) {
            TParm patParm = new TParm();
            Object obj = openDialog(
                "%ROOT%\\config\\sys\\SYSPatChoose.x", same);
            if (obj != null) {
                patParm = (TParm) obj;
                tempCase = patParm.getValue("CASE_NO");
            }
            else {
                return;
            }
        }
        TParm parm = new TParm();
        parm.setData("CASE_NO", tempCase);
        TParm patParm = this.getParmForTag(
            "PAT_NAME;MR_NO;IDNO;BIRTH_DATE;SEX_CODE;ADDRESS;IDNO;TEL;POST_CODE");
        parm.setData("PAT_PARM", patParm);
        this.openDialog("%ROOT%\\config\\hrm\\HRMBackReceipt.x", parm);
        onClear();
        this.onInit();
    }

    /**
     * ����
     * @throws IOException
     */
    public void onPhoto() throws IOException {

        String mrNo = getValue("MR_NO").toString();
        if (StringUtil.isNullString(mrNo)) {
            return;
        }
        String photoName = mrNo + ".jpg";
        String dir = TIOM_FileServer.getPath("PatInfPIC.LocalPath");
        new File(dir).mkdirs();
        JMStudio jms = JMStudio.openCamera(dir + photoName);
        jms.addListener("onCameraed", this, "sendpic");
    }

    /**
     * //ע���������
     */
    public void onRegist() {
        // ע���������
        JMFRegistry jmfr = new JMFRegistry();
        jmfr.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent event) {
                event.getWindow().dispose();
                System.exit(0);
            }

        });
        jmfr.setVisible(true);

    }

    /**
     * �ʱ����¼�
     */
    public void onPostCode() {
        String postCode = this.getValueString("POST_CODE");
        if (StringUtil.isNullString(postCode)) {
            return;
        }
        if (postCode.trim().length() != 6) {
            return;
        }
        String sql =
            "SELECT STATE||CITY ADDRESS FROM SYS_POSTCODE WHERE POST_CODE='" +
            postCode + "'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        String add = result.getValue("ADDRESS", 0);
        this.setValue("ADDRESS", add);
        this.setValue("PROVINCE", postCode.substring(0, 2));
        this.setValue("CITY", postCode);
        this.callFunction("UI|ADDRESS|grabFocus");
    }

    /**
     * ������Ƭ
     *
     * @param image
     *            Image
     */
    public void sendpic(Image image) {
        String mrNo = getValue("MR_NO").toString();
        String photoName = mrNo + ".jpg";
        String dir = TIOM_FileServer.getPath("PatInfPIC.LocalPath");
        String localFileName = dir + photoName;
        try {
            byte[] data = FileTool.getByte(localFileName);
            if (data == null || data.length <= 0)
                return;
            new File(localFileName).delete();

            String root = TIOM_FileServer.getRoot();
            dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
            //�����Ŵ��ڵ���10λ����
            if(mrNo.length()>=10){
            	dir = root + dir + mrNo.substring(0, 3) + "\\"
                + mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";
            //������С��10λ����
            }else{
            	dir = root + dir + mrNo.substring(0, 2) + "\\"
                + mrNo.substring(2, 4) + "\\" + mrNo.substring(4, 7) + "\\";
            }
            //
            TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(), dir
                                      + photoName, data);
        }
        catch (Exception e) {
//			this.messageBox_("wrong");
            e.printStackTrace();
        }
        this.viewPhoto(mrNo);

    }

    /**
     * ��ʾphoto
     * @param mrNo String
     */
    public void viewPhoto(String mrNo) {

        String photoName = mrNo + ".jpg";
        String fileName = photoName;
        try {
            TPanel viewPanel = (TPanel) getComponent("PHOTO_PANEL");
            String root = TIOM_FileServer.getRoot();
            String dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
            //�����Ŵ��ڵ���10λ����
            if(mrNo.length()>=10){
            	dir = root + dir + mrNo.substring(0, 3) + "\\"
                + mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";
            //������С��10λ����
            }else{
            	dir = root + dir + mrNo.substring(0, 2) + "\\"
                + mrNo.substring(2, 4) + "\\" + mrNo.substring(4, 7) + "\\";
            }
            //

            byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
                dir + fileName);
            if (data == null)
                return;
            double scale = 0.2;
            boolean flag = true;
            Image image = ImageTool.scale(data, scale, flag);
            // Image image = ImageTool.getImage(data);
            Pic pic = new Pic(image);
            pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
            pic.setLocation(0, 0);
            viewPanel.removeAll();
            viewPanel.add(pic);
            pic.repaint();
        }
        catch (Exception e) {
        }
    }
    
    /**
     * ���屨�������ײ�ѡ��
     */
    public void onPackageChooseP(){
    	 String mrNo = this.getValueString("MR_NO");
         if (StringUtil.isNullString(mrNo)) {
             pack.setValue("");
             return;
         }
         if (order == null) {
             pack.setValue("");
             return;
         }
         if (order.rowCount() < 1) {
             pack.setValue("");
             return;
         }

         String packCode = this.getValueString("PACKAGE_CODE");

         if (StringUtil.isNullString(packCode)) {
             return;
         }
         
         String filterStr = order.getFilter();
         order.setFilter("");
         order.filter();
         
         
         int delRow = order.rowCount();
         for (int i = delRow - 1; i > -1; i--) {
             if(this.progamesName.length() != 0){
                 order.deleteRow(i);
                 continue;
             }
             boolean isNew = false;
             for(int j = 0;j < order.getNewRows().length ; j++){
                 if(i == order.getNewRows()[j]){
                     isNew = true;
                     break;
                 }
             }
             if(isNew)
                 order.deleteRow(i);
         }
         
         /**
         med = order.getMedApply() ;
         med.onQueryByCaseNo(caseNo);
         med.setFilter("");
         med.filter();
         int delMedApplyRow = med.rowCount();
         for (int i = delMedApplyRow - 1; i > -1; i--) {
             if(this.progamesName.length() != 0){
                 med.setActive(i, false);
                 continue;
             }
             boolean isNew = false;
             for(int j = 0;j < med.getNewRows().length ; j++){
                 if(i == med.getNewRows()[j]){
                     isNew = true;
                     break;
                 }
             }
             if(isNew)
                 med.deleteRow(i);
         }*/
         order.setFilter(filterStr);
         order.filter();
         table.setDSValue();
         this.setTableOrderLock();
        
         
         /**�Ƿ��������˾֧��*/
         String comPayFlg = this.getValueString("COM_PAY_FLG");
         double arAmts =  0.0;
         
         if(comPayFlg.equals("Y")){
        	 arAmts = order.getArAmt();
         }else{
        	 arAmts = order.getArAmt(comPayFlg);
         }
         
         
         //Ӧ�ս��
         this.setValue("CHARGE", arAmts);
         
         //ʵ�ս��
         this.setValue("REAL", arAmts);

         TParm patParm = this.getParmForTag("PAT_NAME;MR_NO;IDNO;BIRTH_DATE;SEX_CODE;ADDRESS;IDNO;TEL;POST_CODE");
         patParm.setData("DISCNT",discnt);
         order.setFilter("");
         order.filter();
         // System.out.println("packCode" + packCode);
         if (!order.initOrderByTParmP(packCode, caseNo, mrNo,
                                      this.getValueString("CONTRACT_CODE"),
                                      patParm)) {
             this.messageBox_("ȡ������ʧ��");
         }
         //ORDER_DESC;DISPENSE_QTY;DISPENSE_UNIT;OWN_PRICE_MAIN;DR_CODE;DEPT_CODE;EXEC_DEPT_CODE;DEPT_ATTRIBUTE;AR_AMT_MAIN
         
         order.setFilter("CASE_NO='" + caseNo +
                         "' AND ( SETMAIN_FLG='Y' OR SETMAIN_FLG='' ) AND HIDE_FLG='N' AND BILL_FLG='N' AND EXEC_FLG='N'");
         order.filter();
         table.setDSValue();
         this.setTableOrderLock();
         double arAmt = 0.0;
         
         if(comPayFlg.equals("Y")){
        	 arAmt = order.getArAmt();
         }else{
        	 arAmt = order.getArAmt(comPayFlg);
         }
         this.setValue("CHARGE", arAmt);
         this.setValue("REAL", arAmt);
        
    }
    

    /**
     * �����ײ�ѡ���¼�
     */
    public void onPackageChoose() {
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(mrNo)) {
            pack.setValue("");
            return;
        }
        if (order == null) {
            pack.setValue("");
            return;
        }
        if (order.rowCount() < 1) {
            pack.setValue("");
            return;
        }
        String packCode = this.getValueString("PACKAGE_CODE");

        if (StringUtil.isNullString(packCode)) {
            return;
        }
        String filterStr = order.getFilter();
        order.setFilter("");
        order.filter();
        int delRow = order.rowCount();
        for (int i = delRow - 1; i > -1; i--) {
            if(this.progamesName.length() != 0){
                order.deleteRow(i);
                continue;
            }
            boolean isNew = false;
            for(int j = 0;j < order.getNewRows().length ; j++){
                if(i == order.getNewRows()[j]){
                    isNew = true;
                    break;
                }
            }
            if(isNew)
                order.deleteRow(i);
        }
        
       
        int delMedApplyRow = order.getMedApply().rowCount();
        for (int i = delMedApplyRow - 1; i > -1; i--) {
            if(this.progamesName.length() != 0){
                order.getMedApply().deleteRow(i);
                continue;
            }
            boolean isNew = false;
            for(int j = 0;j < order.getMedApply().getNewRows().length ; j++){
                if(i == order.getMedApply().getNewRows()[j]){
                    isNew = true;
                    break;
                }
            }
            if(isNew)
                order.getMedApply().deleteRow(i);
        }
        order.setFilter(filterStr);
        order.filter();
        table.setDSValue();
        this.setTableOrderLock();
        double arAmts = order.getArAmt();
        this.setValue("CHARGE", arAmts);
        this.setValue("REAL", arAmts);
//		TParm packParm = packageD.getSysFeeByPack(packCode);
//		if (packParm.getErrCode() != 0) {
//			this.messageBox_("ȡ������ʧ��");
//			return;
//		}
//		int count = packParm.getCount();
//		if (count < 1) {
//			this.messageBox_("û������");
//			return;
//		}
        TParm patParm = this.getParmForTag(
            "PAT_NAME;MR_NO;IDNO;BIRTH_DATE;SEX_CODE;ADDRESS;IDNO;TEL;POST_CODE");
        order.setFilter("");
        order.filter();
        patParm.setData("DISCNT","1");
        // System.out.println("packCode" + packCode);
        if (!order.initOrderByTParmPerson(packCode, caseNo, mrNo,
                                     this.getValueString("CONTRACT_CODE"),
                                     patParm)) {
            this.messageBox_("ȡ������ʧ��");
        }
        //ORDER_DESC;DISPENSE_QTY;DISPENSE_UNIT;OWN_PRICE_MAIN;DR_CODE;DEPT_CODE;EXEC_DEPT_CODE;DEPT_ATTRIBUTE;AR_AMT_MAIN
        
        order.setFilter("CASE_NO='" + caseNo +
                        "' AND SETMAIN_FLG='Y' AND HIDE_FLG='N' AND BILL_FLG='N' AND EXEC_FLG='N'");
        order.filter();
        table.setDSValue();
        this.setTableOrderLock();
        
        double arAmt = order.getArAmt();
        this.setValue("CHARGE", arAmt);
        this.setValue("REAL", arAmt);
       
    }

    /**
     * ��ӡ����
     */
    public void onBarCode() {
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(mrNo)) {
            return;
        }
        HRMPatAdm patAdm = new HRMPatAdm();

        String caseNo = patAdm.getLatestCaseNo(mrNo);
        patAdm.onQueryByCaseNo(caseNo);
        TParm parm = new TParm();
        //����
        parm.setData("DEPT_CODE", Operator.getDept());
        parm.setData("ADM_TYPE", "H");
        parm.setData("CASE_NO", caseNo);
        parm.setData("MR_NO", mrNo);
        parm.setData("PAT_NAME", this.getValueString("PAT_NAME"));
        parm.setData("ADM_DATE", patAdm.getItemTimestamp(0, "REPORT_DATE"));
        parm.setData("POPEDEM", "1");
        String value = (String)this.openDialog(
            "%ROOT%\\config\\med\\MEDApply.x", parm);
    }

    /**
     * �һ�MENU�����¼�
     */
    public void showPopMenu() {
        table.setPopupMenuSyntax("��ʾ����ҽ��ϸ��,onOrderSetShow");
    }

    /**
     * �޸�ҽ��ϸ�࣬�ײ�ϸ��TABLE�һ��¼�������ϸ���б������޸�ϸ����Ϣ
     */
    public void onOrderSetShow() {
        TParm parm = new TParm();
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        String filterString = order.getFilter();
        String orderSetCode = order.getItemString(row, "ORDERSET_CODE");
        int groupNo = order.getItemInt(row, "ORDERSET_GROUP_NO");
        if (StringUtil.isNullString(orderSetCode)) {
            return;
        }
        String packCode = this.getValueString("PACKAGE_CODE");
        parm.setData("PACKAGE_CODE", packCode);
        parm.setData("ORDERSET_CODE", orderSetCode);
        parm.setData("ORDERSET_GROUP_NO", groupNo);
        parm.setData("FLG", "Y");
        parm.setData("DATASTORE",
                     order.getBuffer(order.isFilter() ? order.FILTER :
                                     order.PRIMARY));
        this.openDialog("%ROOT%\\config\\hrm\\HRMOrderSetShow.x", parm);
        table.setFilter(filterString);
        table.filter();
        table.setDSValue();
    }

    /**
     * �����ӡ����
     * @param receiptNo String[]
     * @param patName String
     * @param deptCode String
     * @param sexCode String
     */
    public void dealPrintData(String[] receiptNo, String patName,
                              String deptCode, String sexCode) {
        int size = receiptNo.length;
        for (int i = 0; i < size; i++) {
            //ȡ��һ��Ʊ�ݺ�
            String recpNo = receiptNo[i];
            if (recpNo == null || recpNo.length() == 0)
                return;
            //���ô�ӡһ��Ʊ�ݵķ���
//            this.messageBox("xueyf "+OPBReceiptTool.getInstance().getOneReceipt(recpNo)+"  "+patName+" "+
//                    deptCode+"  "+ sexCode+"  "+recpNo);
            onPrint(OPBReceiptTool.getInstance().getOneReceipt(recpNo), patName,
                    deptCode, sexCode, recpNo);
        }
    }

    /**
     * ��ӡƱ��
     * @param recpParm TParm
     * @param patName String
     * @param deptCode String
     * @param sexCode String
     * @param receiptNo String
     */
    public void onPrint(TParm recpParm, String patName, String deptCode,
                        String sexCode, String receiptNo) {
        if (recpParm == null)
            return;
        TParm oneReceiptParm = new TParm();
        //Ʊ����Ϣ
        oneReceiptParm.setData("CASE_NO", recpParm.getData("CASE_NO", 0));
        oneReceiptParm.setData("RECEIPT_NO", receiptNo);
        oneReceiptParm.setData("MR_NO", recpParm.getData("MR_NO", 0));
        oneReceiptParm.setData("BILL_DATE", recpParm.getData("BILL_DATE", 0));
        oneReceiptParm.setData("CHARGE_DATE", recpParm.getData("CHARGE_DATE", 0));
        oneReceiptParm.setData("CHARGE01", recpParm.getData("CHARGE01", 0));
        oneReceiptParm.setData("CHARGE02", recpParm.getData("CHARGE02", 0));
        oneReceiptParm.setData("CHARGE03", recpParm.getData("CHARGE03", 0));
        oneReceiptParm.setData("CHARGE04", recpParm.getData("CHARGE04", 0));
        oneReceiptParm.setData("CHARGE05", recpParm.getData("CHARGE05", 0));
        oneReceiptParm.setData("CHARGE06", recpParm.getData("CHARGE06", 0));
        oneReceiptParm.setData("CHARGE07", recpParm.getData("CHARGE07", 0));
        oneReceiptParm.setData("CHARGE08", recpParm.getData("CHARGE08", 0));
        oneReceiptParm.setData("CHARGE09", recpParm.getData("CHARGE09", 0));
        oneReceiptParm.setData("CHARGE10", recpParm.getData("CHARGE10", 0));
        oneReceiptParm.setData("CHARGE11", recpParm.getData("CHARGE11", 0));
        oneReceiptParm.setData("CHARGE12", recpParm.getData("CHARGE12", 0));
        oneReceiptParm.setData("CHARGE13", recpParm.getData("CHARGE13", 0));
        oneReceiptParm.setData("CHARGE14", recpParm.getData("CHARGE14", 0));
        oneReceiptParm.setData("CHARGE15", recpParm.getData("CHARGE15", 0));
        oneReceiptParm.setData("CHARGE16", recpParm.getData("CHARGE16", 0));
        oneReceiptParm.setData("CHARGE17", recpParm.getData("CHARGE17", 0));
        oneReceiptParm.setData("CHARGE18", recpParm.getData("CHARGE18", 0));
        oneReceiptParm.setData("CHARGE19", recpParm.getData("CHARGE19", 0));
        oneReceiptParm.setData("CHARGE20", recpParm.getData("CHARGE20", 0));
        oneReceiptParm.setData("CHARGE21", recpParm.getData("CHARGE21", 0));
        oneReceiptParm.setData("CHARGE22", recpParm.getData("CHARGE22", 0));
        oneReceiptParm.setData("CHARGE23", recpParm.getData("CHARGE23", 0));
        oneReceiptParm.setData("CHARGE24", recpParm.getData("CHARGE24", 0));
        oneReceiptParm.setData("CHARGE25", recpParm.getData("CHARGE25", 0));
        oneReceiptParm.setData("CHARGE26", recpParm.getData("CHARGE26", 0));
        oneReceiptParm.setData("CHARGE27", recpParm.getData("CHARGE27", 0));
        oneReceiptParm.setData("CHARGE28", recpParm.getData("CHARGE28", 0));
        oneReceiptParm.setData("CHARGE29", recpParm.getData("CHARGE29", 0));
        oneReceiptParm.setData("CHARGE30", recpParm.getData("CHARGE30", 0));
        oneReceiptParm.setData("TOT_AMT",
                               StringTool.round(recpParm.getDouble("TOT_AMT", 0),
                                                2));
        oneReceiptParm.setData("AR_AMT",
                               StringTool.round(recpParm.getDouble("AR_AMT", 0),
                                                2));
        oneReceiptParm.setData("CASHIER_CODE",
                               recpParm.getData("CASHIER_CODE", 0));

        oneReceiptParm.setData("PAT_NAME", patName);
        oneReceiptParm.setData("DEPT_CODE", deptCode);
        oneReceiptParm.setData("SEX_CODE", sexCode);
        oneReceiptParm.setData("OPT_USER", Operator.getName());
        oneReceiptParm.setData("OPT_ID", Operator.getID());
        oneReceiptParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        oneReceiptParm.setData("PRINT_DATE",
                               StringTool.getString(SystemTool.getInstance().
            getDate(), "yyyy/MM/dd HH:mm:ss"));
        oneReceiptParm.setData("PRINT_NO", recpParm.getData("PRINT_NO", 0));
        TParm patParm = new TParm(TJDODBTool.getInstance().select(
            "SELECT B.DEPT_CHN_DESC FROM HRM_PATADM A,SYS_DEPT B WHERE A.CASE_NO='" +
            this.caseNo + "' AND A.DEPT_CODE=B.DEPT_CODE"));
        oneReceiptParm.setData("MR_NO", "TEXT",
                               "������:" + recpParm.getData("MR_NO", 0));
        oneReceiptParm.setData("DEPT_CODE", "TEXT",
                               "����:" + patParm.getValue("DEPT_CHN_DESC", 0));
        oneReceiptParm.setData("CLINICROOM_DESC", "TEXT", "");
        oneReceiptParm.setData("DR_CODE", "TEXT", "");
        oneReceiptParm.setData("QUE_NO", "TEXT", "");
        oneReceiptParm.setData("PAT_NAME", "TEXT", patName);
        oneReceiptParm.setData("HOSP_DESC", "TEXT",
                               Manager.getOrganization().
                               getHospitalCHNFullName(Operator.getRegion()));
        oneReceiptParm.setData("RECEIPT_NO", "TEXT", receiptNo);
        oneReceiptParm.setData("PRINT_NO", "TEXT",
                               recpParm.getData("PRINT_NO", 0));
        oneReceiptParm.setData("TOT_AMT", "TEXT",
                               StringTool.round(recpParm.getDouble("TOT_AMT", 0),
                                                2));
        oneReceiptParm.setData("TOTAL_AW", "TEXT",//modify by wanglong 20130123
                               "" +
                               StringUtil.getInstance().
                               numberToWord(StringTool.
                                            round(recpParm.getDouble("TOT_AMT",
            0),
                                                  2)));
        oneReceiptParm.setData("OPT_USER", "TEXT", "����Ա:" + Operator.getName());
        String printDate = StringTool.getString(SystemTool.getInstance().
                                                getDate(),
                                                "yyyy/MM/dd HH:mm:ss");
        oneReceiptParm.setData("PRINT_DATE", "TEXT", printDate);
        oneReceiptParm.setData("YEAR", "TEXT", printDate.substring(0, 4));
        oneReceiptParm.setData("MONTH", "TEXT", printDate.substring(5, 7));
        oneReceiptParm.setData("DAY", "TEXT", printDate.substring(8, 10));
        oneReceiptParm.setData("OPT_ID", "TEXT", Operator.getID());
        oneReceiptParm.setData("USER_NAME", "TEXT", "" + Operator.getID());//add by wanglong 20130123
        /**
        oneReceiptParm.setData("CHARGE01", "TEXT",
                               recpParm.getData("CHARGE01", 0));
        oneReceiptParm.setData("CHARGE02", "TEXT",
                               recpParm.getData("CHARGE02", 0));
                               */
        //============================   chenxi modify 20130219 ========
        if(recpParm.getDouble("CHARGE01", 0)+recpParm.getDouble("CHARGE02", 0)==0.0)
          oneReceiptParm.setData("CHARGE01", "TEXT","");
        else  oneReceiptParm.setData("CHARGE01", "TEXT",
        		recpParm.getDouble("CHARGE01", 0)+recpParm.getDouble("CHARGE02", 0));
        
        String[] array = {"CHARGE03","CHARGE04","CHARGE05","CHARGE06","CHARGE07","CHARGE08","CHARGE09",
        		          "CHARGE13","CHARGE14","CHARGE15","CHARGE16","CHARGE17","CHARGE18"} ;
        for(int i=0;i<array.length;i++){
        	if( recpParm.getData(array[i], 0).equals(0.0))
        		 oneReceiptParm.setData(array[i], "TEXT","");
        	else
        		 oneReceiptParm.setData(array[i], "TEXT",
                         recpParm.getData(array[i], 0));
        }
        //=======================   chen  xi  modify  20130219============
//        oneReceiptParm.setData("CHARGE01", "TEXT",recpParm.getDouble("CHARGE01", 0)+recpParm.getDouble("CHARGE02", 0));
//        oneReceiptParm.setData("CHARGE03", "TEXT",
//                               recpParm.getData("CHARGE03", 0));
//        oneReceiptParm.setData("CHARGE04", "TEXT",
//                               recpParm.getData("CHARGE04", 0));
//        oneReceiptParm.setData("CHARGE05", "TEXT",
//                               recpParm.getData("CHARGE05", 0));
//        oneReceiptParm.setData("CHARGE06", "TEXT",
//                               recpParm.getData("CHARGE06", 0));
//        oneReceiptParm.setData("CHARGE07", "TEXT",
//                               recpParm.getData("CHARGE07", 0));
//        oneReceiptParm.setData("CHARGE08", "TEXT",
//                               recpParm.getData("CHARGE08", 0));
//        oneReceiptParm.setData("CHARGE09", "TEXT",
//                               recpParm.getData("CHARGE09", 0));
        oneReceiptParm.setData("CHARGE10", "TEXT",
                               recpParm.getData("CHARGE10", 0));
        oneReceiptParm.setData("CHARGE11", "TEXT",
                               recpParm.getData("CHARGE11", 0));
        if (recpParm.getDouble("CHARGE11", 0) > 0) {// add by wanglong 20130123
            oneReceiptParm.setData("CUSTOM_TEXT1", "TEXT", "����");
        } else {
            oneReceiptParm.setData("CHARGE11", "TEXT", "");
        }
        oneReceiptParm.setData("CHARGE12", "TEXT",
                               recpParm.getData("CHARGE12", 0));
//        oneReceiptParm.setData("CHARGE13", "TEXT",
//                               recpParm.getData("CHARGE13", 0));
//        oneReceiptParm.setData("CHARGE14", "TEXT",
//                               recpParm.getData("CHARGE14", 0));
//        oneReceiptParm.setData("CHARGE15", "TEXT",
//                               recpParm.getData("CHARGE15", 0));
//        oneReceiptParm.setData("CHARGE16", "TEXT",
//                               recpParm.getData("CHARGE16", 0));
//        oneReceiptParm.setData("CHARGE17", "TEXT",
//                               recpParm.getData("CHARGE17", 0));
//        oneReceiptParm.setData("CHARGE18", "TEXT",
//                               recpParm.getData("CHARGE18", 0));
        oneReceiptParm.setData("CHARGE19", "TEXT",
                               recpParm.getData("CHARGE19", 0));
        oneReceiptParm.setData("CHARGE20", "TEXT",
                               recpParm.getData("CHARGE20", 0));
        oneReceiptParm.setData("CHARGE21", "TEXT",
                               recpParm.getData("CHARGE21", 0));
        oneReceiptParm.setData("CHARGE22", "TEXT",
                               recpParm.getData("CHARGE22", 0));
        oneReceiptParm.setData("CHARGE23", "TEXT",
                               recpParm.getData("CHARGE23", 0));
        oneReceiptParm.setData("CHARGE24", "TEXT",
                               recpParm.getData("CHARGE24", 0));
        oneReceiptParm.setData("CHARGE25", "TEXT",
                               recpParm.getData("CHARGE25", 0));
        oneReceiptParm.setData("CHARGE26", "TEXT",
                               recpParm.getData("CHARGE26", 0));
        oneReceiptParm.setData("CHARGE27", "TEXT",
                               recpParm.getData("CHARGE27", 0));
        oneReceiptParm.setData("CHARGE28", "TEXT",
                               recpParm.getData("CHARGE28", 0));
        oneReceiptParm.setData("CHARGE29", "TEXT",
                               recpParm.getData("CHARGE29", 0));
        oneReceiptParm.setData("CHARGE30", "TEXT",
                               recpParm.getData("CHARGE30", 0));
//        this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBRECTPrint.jhw",
//                             oneReceiptParm);
        this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
                             IReportTool.getInstance().getReportParm("OPBRECTPrintForHRM.class", oneReceiptParm));//����ϲ�modify by wanglong 20130730
    }

    /**
     * ��ͬѡ���¼����ж��Ƿ������Ա�������жϸ�Ա���Ƿ�Ϊ��������֧��
     */
    public void onContractChoose() {
        String companyCode = this.getValueString("COMPANY_CODE");
        String contractCode = this.getValueString("CONTRACT_CODE");
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(companyCode) ||
            StringUtil.isNullString(contractCode) ||
            StringUtil.isNullString(mrNo)) {
            this.messageBox_("���塢��ͬ�������Ų���Ϊ��");
            return;
        }

        int comPay = contractD.isPatCompanyPay(companyCode, contractCode, mrNo);
        if (comPay == -1) {
            this.messageBox_("��ѯʧ��");
            return;
        }
        if (comPay == 1) {
            this.messageBox_("�ÿͻ����Ǵ�����˺�ͬ��ָ��������������Ա");
            this.setValue("COMPANY_CODE", "");
            this.setValue("CONTRACT_CODE", "");
            this.setValue("COM_PAY_FLG", "N");
            return;
        }
    }

    /**
     * ����CONTRACT_CODE
     */
    private void onComPayOrder() {
        String companyCode = this.getValueString("COMPANY_CODE");
        String contractCode = this.getValueString("CONTRACT_CODE");
        if (StringUtil.isNullString(companyCode) ||
            StringUtil.isNullString(contractCode)) {
            return;
        }
        order.onCompay(contractCode);
    }

    /**
     * �رս���
     */
    public void onShut() {

    }

    class Pic
        extends JLabel {
        Image image;

        public Pic(Image image) {
            this.image = image;
        }

        public void paint(Graphics g) {
            g.setColor(new Color(161, 220, 230));
            g.fillRect(4, 15, 100, 100);
            if (image != null) {
                g.drawImage(image, 4, 15, null);

            }
        }
    }


    /**
     * �ƿ�
     */
    public void onCreatCard() {
        if (StringUtil.isNullString(this.getValueString("MR_NO"))) {
            this.messageBox("�޲������޷��ƿ���");
            return;
        }

    }

    /**
     * ����
     */
    public void onReadCard() {
        TParm patParm = jdo.ekt.EKTIO.getInstance().getPat();
        if (patParm.getErrCode() < 0) {
            this.messageBox(patParm.getErrName() + " " + patParm.getErrText());
            return;
        }
        this.setValue("MR_NO", patParm.getValue("MR_NO"));
        this.onMrNo();
    }

    public static void main(String[] args) {
        // System.out.println("���" + StringTool.bitDifferOfString("25", "24"));

    }
	/**
     * ��ѯ������Ϣ
     */
    public boolean queryPat(String mrNo) {
    	Pat pat = new Pat();
        pat = Pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            this.messageBox("E0081");
            return false;
        }
        String allMrNo = PatTool.getInstance().checkMrno(mrNo) ;
        if(mrNo!=null && !allMrNo.equals(pat.getMrNo())){
        	//============xueyf modify 20120307 start
        	messageBox("������"+allMrNo+" �Ѻϲ���"+pat.getMrNo()) ;
        	//============xueyf modify 20120307 stop
        }
      
        return true;
    }
	/**
	 * ��������
	 */
	public void unLockPat() {
		if (pat == null) {
			return;
		}
		// �ж��Ƿ����
		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
			TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
			if ("OPB".equals(parm.getValue("PRG_ID", 0))
					&& (Operator.getIP().equals(parm.getValue("OPT_TERM", 0)))
					&& (Operator.getID().equals(parm.getValue("OPT_USER", 0)))) {
				PatTool.getInstance().unLockPat(pat.getMrNo());
			}
		}
		pat = null;
	}
	
	//=================  chenxi add 20130319 �����֤����Ϣ
    public void onIdCardNo(){	
    	String dir = "C:/Program Files/Routon/���֤��������Ķ����"  ;
        CardInfoBO   cardInfo 	= IdCardReaderUtil.getCardInfo(dir);
        if(cardInfo ==null){
        	return ;
        }
        setValue("PAT_NAME", cardInfo.getName()) ;  //����
        setValue("IDNO", cardInfo.getCode()) ;     //���֤��
        setValue("SEX_CODE", cardInfo.getSex().equals("��")? "1" : "2") ; //�Ա�
        setValue("BIRTH_DATE", StringTool.getTimestamp(cardInfo.getBirth(), "yyyyMMdd")) ;  //���� 
        setValue("ADDRESS", cardInfo.getAdd()) ;   //���ڵ�ַ
    	 
    }
	
}
