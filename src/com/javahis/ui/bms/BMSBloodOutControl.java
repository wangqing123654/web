package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.bms.BMSApplyMTool;
import jdo.bms.BMSApplyDTool;
import com.javahis.util.StringUtil;
import jdo.sys.Pat;
import jdo.sys.SYSOperatorTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import jdo.bms.BMSBloodTool;
import jdo.sys.PatTool;
import com.dongyang.ui.TCheckBox;
import jdo.sys.Operator;
import java.util.Map;
import java.util.HashMap;
import com.dongyang.util.TypeTool;
import java.util.Set;
import java.util.Iterator;
import jdo.adm.ADMTool;
import jdo.bms.BMSBldStockTool;
import jdo.bms.BMSSQL;
import jdo.ibs.IBSOrderD;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.util.Manager;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextFormat;

/** 
 * <p>
 * Title: ѪҺ����
 * </p>
 *
 * <p>
 * Description: ѪҺ����
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.24
 * @version 1.0
 */
public class BMSBloodOutControl
    extends TControl {

    // �ⲿ���ô���
    private TParm parm;

    private TTable table_m;

    private TTable table_d;

    private String case_no = "";

    private String adm_type = "";

    private String apply_no = "";

    private String blood_type = "";
    private IBSOrderD order;
    public BMSBloodOutControl() {
    }

    public void onInit() {
        table_m = this.getTable("TABLE_M");
        table_d = this.getTable("TABLE_D");
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            parm = (TParm) obj;
            apply_no = parm.getValue("APPLY_NO");
            this.setValue("APPLY_NO", apply_no);
            onApplyNoAction();
        }
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        if ("".equals(this.getValueString("APPLY_NO"))) {
            this.messageBox("�����뱸Ѫ����");
            return;
        }
        onApplyNoAction();
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "APPLY_NO;PRE_DATE;USE_DATE;END_DAYS;MR_NO;IPD_NO;"
            + "PAT_NAME;SEX;AGE;ID_NO;TEST_BLD;BLOOD_TEXT;DEPT_CODE;BLOOD_NO;"
            +
            "BLD_CODE;BLD_TYPE;SUBCAT_CODE;SELECT_ALL;RH_A;RH_B;BED_NO;STATION_CODE";
        this.clearValue(clearStr);
        //this.getRadioButton("RH_A").setSelected(true);
        this.getRadioButton("RH_TYPE_A").setSelected(true);
        table_m.removeRowAll();
        table_d.removeRowAll();
        case_no = "";
        adm_type = "";
        apply_no = "";
        blood_type = "";
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (!CheckData()) {
            return;
        }

        TParm parm = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        table_m.acceptText() ;
        //modify by lim 2012/05/22 begin
        String out_no = SystemTool.getInstance().getNo("ALL", "BMS",
                "OUT_NO", "No");    
      //modify by lim 2012/05/22 begin
        for (int i = 0; i < table_m.getRowCount(); i++) {
            if (!"N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
                // ����Ƿ��ѳ���
                TParm bloodParm = new TParm(TJDODBTool.getInstance().select(
                    BMSSQL.getBMSBlood(table_m.getItemString(i, "BLOOD_NO"))));
                if (!"".equals(bloodParm.getValue("OUT_NO", 0))) {
                    this.messageBox("ѪƷ��" + table_m.getItemString(i, "BLOOD_NO") +
                                    "�ѳ���");
                    return;
                }
                // ����ѪƷ������Ϣ
                parm.addData("BLOOD_NO", table_m.getItemData(i, "BLOOD_NO"));
                parm.addData("STATE_CODE", "2");

                parm.addData("OUT_NO", out_no);
                parm.addData("OUT_DATE", date);
                parm.addData("DEPT_CODE", this.getValueString("DEPT_CODE"));
                parm.addData("STATION_CODE", this.getValueString("STATION_CODE"));
                parm.addData("OUT_USER", Operator.getID());
                parm.addData("OPT_USER", Operator.getID());
                parm.addData("OPT_DATE", date);
                parm.addData("OPT_TERM", Operator.getIP());
                // ����Ѫ����
                parm.addData("BLD_CODE", table_m.getItemData(i, "BLD_CODE"));
                parm.addData("BLD_SUBCAT", table_m.getItemData(i, "SUBCAT_CODE"));
                parm.addData("BLD_TYPE", table_m.getItemData(i, "BLD_TYPE"));
                TParm inparm = new TParm(TJDODBTool.getInstance().select(BMSSQL.
                    getBMSBldVol(table_m.getItemString(i, "BLD_CODE"),
                                 table_m.getItemString(i, "SUBCAT_CODE"))));
                parm.addData("BLD_VOL", inparm.getData("BLD_VOL", 0));
                //  add  by  chenxi  20130425
                parm.addData("CASE_NO", case_no) ;
                parm.addData("ADM_TYPE", adm_type) ; 
                parm.addData("DEPT", Operator.getDept()) ;
                parm.addData("REGION", Operator.getRegion()) ;
                parm.addData("STATION", Operator.getStation()) ;
            }
        }
        TParm result = TIOM_AppServer.executeAction(
            "action.bms.BMSBloodAction", "onUpdateBloodOut", parm);  
        // ������ж�
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        TParm feeParm = new TParm() ;
        feeParm.setData( "CASE_NO", case_no);
        feeParm.setData( "PAT_NAME", this.getValueString("PAT_NAME")); 
//        this.messageBox("�Զ��Ʒѳɹ�,�Ʒѽ��Ϊ"+result.getDouble("FEE",0)) ;
        onPrint();
        onClear();
    	TParm feeResult = (TParm) openWindow("%ROOT%\\config\\bms\\BMSQueryFee.x",feeParm);
        
    }

    /**
     * ��ӡ���ⵥ
     */
    public void onPrint() {
        if (table_m.getRowCount() <= 0) {
            this.messageBox("û�г�����Ϣ");
            return;
        }
        boolean flg = false;
        for (int i = 0; i < table_m.getRowCount(); i++) {
            if ("Y".equals(table_m.getItemString(i, "SELECT_FLG"))) {
                flg = true;
                break;
            }
        }
        if (!flg) {
            this.messageBox("û�г�����Ϣ");
            return;
        }

        TParm date = new TParm();
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "ѪƷ���ⵥ");
        date.setData("APPLY_NO", "TEXT", "��Ѫ����: " + this.getValueString("APPLY_NO"));
        date.setData("PAT_NAME", "TEXT", " ��Ѫ��: " + this.getValueString("PAT_NAME"));
        date.setData("AGE", "TEXT", "����: " + this.getValueString("AGE"));
        date.setData("SEX", "TEXT", "�Ա�: " + this.getValueString("SEX"));
        date.setData("BLD_TYPE", "TEXT", " ABOѪ��: " + this.getValueString("TEST_BLD"));
        String rh_type = "";
        if ( (this.getRadioButton("RH_A").isSelected())) {
            rh_type = "����";
        }
        else if ( (this.getRadioButton("RH_B").isSelected())) {
            rh_type = "����";
        }
        date.setData("RH_TYPE", "TEXT", "RHѪ��: " + rh_type);
        date.setData("MR_NO", "TEXT", " ������: " + this.getValueString("MR_NO"));
   
        date.setData("DEPT_CODE", "TEXT",
                     "�Ʊ�: " + this.getComboBox("DEPT_CODE").getSelectedName());

        date.setData("IPD_NO", "TEXT", "סԺ��: " + this.getValueString("IPD_NO"));
        date.setData("STATION_CODE", "TEXT",
                     "����: " +
                     ( (TTextFormat)this.getComponent("STATION_CODE")).getText());
        date.setData("BED_NO", "TEXT",
                     "��λ: " + ( (TTextFormat)this.getComponent("BED_NO")).getText());
       

        String outNo = "" ;//���ⵥ��
        String reason = "" ;//��Ѫԭ��
        String deptCode = "" ; //��Ѫ����
        String diag = "" ;//���
        String optDate = "" ;//�ռ�����
        String outDate = "" ;//��������
        String caseNo = "" ;
        // �������
        TParm parm = new TParm();
        String blood_no = "";
        TParm inparm = new TParm();
        table_m.acceptText() ;
        for (int i = 0; i < table_m.getRowCount(); i++) {
            if (!"Y".equals(table_m.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            blood_no = table_m.getItemString(i, "BLOOD_NO");
            parm.addData("BLOOD_NO", blood_no);
            inparm = new TParm(TJDODBTool.getInstance().select(BMSSQL.
                getBMSBloodOut(blood_no)));
            
            if("".equals(outNo)){
            	outNo = inparm.getValue("OUT_NO", 0) ;
            }
            if("".equals(reason)){
            	reason += this.getTransReason(inparm.getValue("TRANRSN_CODE1", 0)) +" ";
            	reason += this.getTransReason(inparm.getValue("TRANRSN_CODE2", 0)) +" ";
            	reason += this.getTransReason(inparm.getValue("TRANRSN_CODE3", 0)) +" ";
            }
            if("".equals(deptCode)){
            	deptCode = inparm.getValue("DEPT_CHN_DESC", 0) ;
            }
            if("".equals(diag)){
            	diag += this.getDiagDesc(inparm.getValue("DIAG_CODE1", 0)) +" ";
            	diag += this.getDiagDesc(inparm.getValue("DIAG_CODE2", 0)) +" ";
            	diag += this.getDiagDesc(inparm.getValue("DIAG_CODE3", 0)) +" ";
            }
            if("".equals(optDate)){
            	optDate = inparm.getValue("OPT_DATE", 0) ;
            }
            if("".equals(outDate)){
            	outDate = inparm.getValue("OUT_DATE", 0) ;
            }
            
            if("".equals(caseNo)){
            	caseNo = inparm.getValue("CASE_NO", 0) ;
            }
            
            parm.addData("ORG_BARCODE", inparm.getValue("ORG_BARCODE",0)) ;//Ժ������
            
            //modify by lim 2012/05/06 begin
            //parm.addData("BLD_CODE", inparm.getValue("BLDCODE_DESC", 0));//Ժ������
            //parm.addData("SUBCAT_CODE", inparm.getValue("SUBCAT_DESC", 0));//���
            parm.addData("BLD_CODE", inparm.getValue("SUBCAT_DESC", 0));//Ժ������
            parm.addData("SUBCAT_CODE", "");//���
            //modify by lim 2012/05/06 end
            
            parm.addData("BLD_TYPE", table_m.getItemString(i, "BLD_TYPE"));//Ѫ��
            parm.addData("RH_FLG", table_m.getItemString(i, "RH_FLG"));//RHѪ��
//            parm.addData("SHIT_FLG", table_m.getItemString(i, "SHIT_FLG"));//����
            String crossL = " " ;
            if("0".equals(table_m.getItemString(i, "CROSS_MATCH_L"))){
            	crossL = "������,����Ѫ" ;
            }else if("1".equals(table_m.getItemString(i, "CROSS_MATCH_L"))){
            	crossL = "����" ;
            }
            parm.addData("CROSS_MATCH_L",crossL);
            
            String crossS = " " ;
            if("0".equals(table_m.getItemString(i, "CROSS_MATCH_S"))){
            	crossS = "������,����Ѫ" ;
            }else if("1".equals(table_m.getItemString(i, "CROSS_MATCH_S"))){
            	crossS = "����" ;
            }            
            parm.addData("CROSS_MATCH_S",crossS);
//            parm.addData("ANTI_A", table_m.getItemString(i, "ANTI_A"));
//            parm.addData("ANTI_B", table_m.getItemString(i, "ANTI_B"));
            parm.addData("RESULT",
                         "1".equals(table_m.getItemString(i, "RESULT")) ? "���" :
                         "���");
//            parm.addData("TEST_DATE",
//                         table_m.getItemString(i, "TEST_DATE").substring(0, 10).
//                         replace('-', '/'));
            parm.addData("TEST_DATE",
                    table_m.getItemString(i, "TEST_DATE").substring(0, 16).
                    replace('-', '/'));
        }
        
        parm.setCount(parm.getCount("BLOOD_NO"));
        parm.addData("SYSTEM", "COLUMNS", "ORG_BARCODE");
        parm.addData("SYSTEM", "COLUMNS", "BLOOD_NO");
        parm.addData("SYSTEM", "COLUMNS", "BLD_CODE");
        parm.addData("SYSTEM", "COLUMNS", "SUBCAT_CODE");
        parm.addData("SYSTEM", "COLUMNS", "BLD_TYPE");
        parm.addData("SYSTEM", "COLUMNS", "RH_FLG");
//        parm.addData("SYSTEM", "COLUMNS", "SHIT_FLG");
        parm.addData("SYSTEM", "COLUMNS", "CROSS_MATCH_L");
        parm.addData("SYSTEM", "COLUMNS", "CROSS_MATCH_S");
//        parm.addData("SYSTEM", "COLUMNS", "ANTI_A");
//        parm.addData("SYSTEM", "COLUMNS", "ANTI_B");
        parm.addData("SYSTEM", "COLUMNS", "RESULT");
        parm.addData("SYSTEM", "COLUMNS", "TEST_DATE");

        date.setData("TABLE", parm.getData());
        
        //modify by lim 2012/05/17 begin
        String deptDesc = "" ;
        String sql = "SELECT B.DEPT_CHN_DESC FROM ADM_INP A,SYS_DEPT B WHERE A.DEPT_CODE = B.DEPT_CODE AND A.CASE_NO='"+caseNo+"'" ;
        TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
        if(result.getErrCode()<0 || result.getCount()<=0){
        	deptDesc = "" ;
        }else{
        	deptDesc = result.getValue("DEPT_CHN_DESC", 0) ;
        }
        //modify by lim 2012/05/17 end
        
        
        TParm inparm2 = SYSOperatorTool.getInstance().selectdata(table_m.getItemString(0, "TEST_USER"))  ;      
        date.setData("PEIXUEZHE","TEXT","��Ѫ��:"+inparm2.getData("USER_NAME", 0)) ;
        date.setData("OUT_NO", "TEXT", "���ⵥ��: " + outNo);
        date.setData("REASON", "TEXT","��Ѫԭ��:"+reason) ;
        date.setData("YXKS", "TEXT", "��Ѫ����:"+deptDesc) ;
//        date.setData("YXKS", "TEXT", "��Ѫ����:"+deptCode) ;
        String recpDate = "" ;
        if(null!=optDate && !"".equals(optDate)){
        	recpDate =optDate.substring(0, 16).replace('-', '/') ;
        }
        date.setData("TEST_DATE","TEXT"," �ռ�����:"+recpDate) ;
        date.setData("JWSH","TEXT","���: "+diag) ;
        if(!"".equals(outDate)&& outDate!=null){
        	outDate = outDate.substring(0, 19).replace("-", "/") ;
        }
        date.setData("OUT_DATE","TEXT",outDate) ;
//        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\BMS\\BMSBloodOut.jhw", date);
    }
    
    private String getDiagDesc(String diagCode){
    	String desc = "" ;
    	if(diagCode!=null && !"".equals(diagCode)){
    		String sql = "SELECT A.ICD_CHN_DESC FROM SYS_DIAGNOSIS A WHERE A.ICD_CODE='"+diagCode+"'" ;
    		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    		if(result.getErrCode()<0){
    			return desc ;
    		}
    		if(result.getCount() <= 0){
    			return desc ;
    		}
    		desc = result.getValue("ICD_CHN_DESC", 0) ; 
    	}
    	return desc ;
    }
    
    private String getTransReason(String transReasonCode){
    	String desc = "" ;
    	if(transReasonCode!=null && !"".equals(transReasonCode)){
    		String sql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='BMS_TRANRSN' AND ID='"+transReasonCode+"'" ;
    		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    		if(result.getErrCode()<0){
    			return desc ;
    		}
    		if(result.getCount() <= 0){
    			return desc ;
    		}
    		desc = result.getValue("CHN_DESC", 0) ; 
    	}
    	return desc ;
    }

    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        TTable table = getTable("TABLE_M");
        int row = table.getSelectedRow();
        if (row != -1) {
            setValue("BLOOD_NO", table.getItemData(row, "BLOOD_NO"));
            setValue("BLD_CODE", table.getItemData(row, "BLD_CODE"));
            setValue("SUBCAT_CODE", table.getItemData(row, "SUBCAT_CODE"));
            setValue("BLD_TYPE", table.getItemData(row, "BLD_TYPE"));
            if ("+".equals(table.getItemString(row, "RH_FLG"))) {
                this.getRadioButton("RH_TYPE_A").setSelected(true);
            }
            else {
                this.getRadioButton("RH_TYPE_B").setSelected(true);
            }
        }
    }

    /**
     * ��Ѫ���Ų�ѯ(�س��¼�)
     */
    public void onApplyNoAction() {
        String apply_no = this.getValueString("APPLY_NO");
        if (!"".equals(apply_no)) {
            TParm parm = new TParm();
            parm.setData("APPLY_NO", apply_no);
            TParm result = BMSApplyMTool.getInstance().onApplyQuery(parm);
            if (result.getCount("APPLY_NO") == 0 || result.getCount() <= 0) {
                this.messageBox("��Ѫ��������");
                this.setValue("APPLY_NO", "");
                return;
            }
            this.setValue("PRE_DATE", result.getData("PRE_DATE", 0));
            this.setValue("USE_DATE", result.getData("USE_DATE", 0));
            this.setValue("END_DAYS", result.getData("END_DAYS", 0));
            this.setValue("MR_NO", result.getData("MR_NO", 0));
            this.setValue("IPD_NO", result.getData("IPD_NO", 0));
            this.setValue("DEPT_CODE", result.getData("DEPT_CODE", 0));

            this.setValue("BED_NO", result.getData("BED_NO", 0));
            String bed_no = result.getValue("BED_NO", 0);
            if (!"".equals(bed_no)) {
                TParm bedParm = new TParm(TJDODBTool.getInstance().select(
                    "SELECT STATION_CODE FROM SYS_BED WHERE BED_NO = '" +
                    bed_no + "'"));
                this.setValue("STATION_CODE",
                              bedParm.getValue("STATION_CODE", 0));
            }

            // ��ѯ������Ϣ
            Pat pat = Pat.onQueryByMrNo(result.getValue("MR_NO", 0));
            this.setValue("PAT_NAME", pat.getName());
            this.setValue("SEX", pat.getSexString());
            Timestamp date = SystemTool.getInstance().getDate();
            this.setValue("AGE",
                          StringUtil.getInstance().showAge(pat.getBirthday(),
                date));
            this.setValue("ID_NO", pat.getIdNo());
            String rh_type = pat.getBloodRHType();
            if ("-".equals(rh_type)) {
                getRadioButton("RH_B").setSelected(true);
            }
            else if ("+".equals(rh_type)) {
                getRadioButton("RH_A").setSelected(true);
            }
            else {
                getRadioButton("RH_A").setSelected(false);
                getRadioButton("RH_B").setSelected(false);
            }
            this.setValue("BLOOD_TEXT", pat.getBloodType());
            this.setValue("BLD_TYPE", pat.getBloodType());

            this.setValue("TEST_BLD", result.getData("TEST_BLD", 0));
            case_no = result.getValue("CASE_NO", 0);
            adm_type = result.getValue("ADM_TYPE", 0);
            blood_type = pat.getBloodType();

            result = BMSApplyDTool.getInstance().onApplyQuery(parm);
            if (result != null && result.getCount() > 0) {
                table_d.setParmValue(result);
            }
            parm.setData("STATE_CODE", "1");
            result = BMSBloodTool.getInstance().onQueryBloodCross(parm);
            if (result != null && result.getCount() > 0) {
                for (int i = 0; i < result.getCount(); i++) {
                    result.addData("SELECT_FLG", "Y");
                }
                table_m.setParmValue(result);
            }
        }
    }

    /**
     * �����Ų�ѯ(�س��¼�)
     */
    public void onMrNoAction() {
        String mr_no = PatTool.getInstance().checkMrno(this.getValueString(
            "MR_NO"));
        TParm parm = new TParm();
        parm.setData("MR_NO", mr_no);
        TParm resultParm = BMSApplyMTool.getInstance().onApplyQuery(parm);
        if (resultParm.getCount("APPLY_NO") == 0 || resultParm.getCount() <= 0) {
            this.messageBox("�����ڸò����ı�Ѫ��");
            this.setValue("MR_NO", "");
            return;
        }
        //modify by liming 2012/03/09 begin
        parm.setData("CASE_NO",resultParm.getData("CASE_NO", 0)) ;
        //modify by liming 2012/03/09 end
        Object result = openDialog("%ROOT%\\config\\bms\\BMSApplyNo.x", parm);
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                return;
            }
            this.setValue("APPLY_NO", addParm.getValue("APPLY_NO"));
            onApplyNoAction();
        }
    }

    /**
     * ȫѡ��ѡ��ѡ���¼�
     */
    public void onCheckSelectAll() {
        table_m.acceptText();
        if (table_m.getRowCount() <= 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table_m.getRowCount(); i++) {
                table_m.setItem(i, "SELECT_FLG", true);
            }
        }
        else {
            for (int i = 0; i < table_m.getRowCount(); i++) {
                table_m.setItem(i, "SELECT_FLG", false);
            }
        }
    }

    /**
     * ���ݼ��
     * @return boolean
     */
    private boolean CheckData() {
        // ѪҺ��Ϣ���
        if (table_m.getRowCount() == 0) {
            this.messageBox("û�г�����Ϣ");
            return false;
        }
        boolean flg = true;
        
        String rh_type = "";
        if ( (this.getRadioButton("RH_A").isSelected())) {
            rh_type = "+";
        }
        else if ( (this.getRadioButton("RH_B").isSelected())) {
            rh_type = "-";
        }        
        
        for (int i = 0; i < table_m.getRowCount(); i++) {
            if (!"N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
                flg = false;
            }
            // ѪҺ�벡��������ѪҺ����
            boolean check_flg = true;
            for (int j = 0; j < table_d.getRowCount(); j++) {
                if (table_d.getItemString(j, "BLD_CODE").equals(table_m.
                    getItemString(i, "BLD_CODE"))) {
                    check_flg = false;
                }
            }
            if (check_flg) {
                if (this.messageBox("��ʾ",
                                    "Ѫ������ " +
                                    table_m.getItemString(i, "BLOOD_NO") +
                                    " ��ѪҺ�벡��������ѪҺ����,�Ƿ���⣿", 2) != 0) {
                    return false;
                }
            }

            // Ѫ�ͱȽ�
            if (!blood_type.equals(table_m.getItemString(i, "BLD_TYPE"))) {
                this.messageBox("Ѫ������ " + table_m.getItemString(i, "BLOOD_NO") +
                                " ��Ѫ���벡����Ѫ�Ͳ���,������ѡ��");
                table_m.setItem(i, "SELECT_FLG", "N");
                return false;
            }

            //modify by lim 2012/04/26 begin
            // ������
//            if ("2".equals(table_m.getItemString(i, "RESULT"))) {
//                this.messageBox("Ѫ������ " + table_m.getItemString(i, "BLOOD_NO") +
//                                " �ļ������ж�Ϊ'���',���ܳ���");
//                return false;
//            }
            
            //RHѪ��     chenxi modify 20130407   
            if(!rh_type.equals(table_m.getItemString(i, "RH_FLG"))){
            	 int check =	messageBox("��Ϣ","Ѫ������ " + table_m.getItemString(i, "BLOOD_NO") +
                            " ��RHѪ���벡��RHѪ�Ͳ���,���ܳ��⡣�Ƿ����?", 0) ;  
            	 if(check==0){   //����У��      chenxi 
            		 if (!checkPW()) {
            				return false;
            			}
            	 }
            	 if(check!=0){
                    	return false ;
                    	}
            }
            //modify by lim 2012/04/26 end
        }
        if (flg) {
            this.messageBox("��ѡ�����ѪҺ");
            return false;
        }

        // ѪҺ������
        Map mapOut = new HashMap();
        for (int i = 0; i < table_m.getRowCount(); i++) {
            if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            if (mapOut.isEmpty()) {
                mapOut.put(table_m.getItemString(i, "BLD_CODE"), 1);
            }
            else {
                if (mapOut.containsKey(table_m.getItemString(i, "BLD_CODE"))) {
                    double qty = TypeTool.getDouble(mapOut.get(table_m.
                        getItemString(i, "BLD_CODE"))) + 1;
                    mapOut.put(table_m.getItemString(i, "BLD_CODE"), qty);
                }
                else {
                    mapOut.put(table_m.getItemString(i, "BLD_CODE"), 1);
                }
            }
        }
        // ѪҺ������
        Map mapReq = new HashMap();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (mapReq.isEmpty()) {
                mapReq.put(table_d.getItemString(i, "BLD_CODE"), 1);
            }
            else {
                if (mapReq.containsKey(table_d.getItemString(i, "BLD_CODE"))) {
                    double qty = TypeTool.getDouble(mapReq.get(table_d.
                        getItemString(i, "BLD_CODE"))) + 1;
                    mapReq.put(table_d.getItemString(i, "BLD_CODE"), qty);
                }
                else {
                    mapReq.put(table_d.getItemString(i, "BLD_CODE"), 1);
                }
            }
        }

//        // ѪҺ���������������Ƚ�
//        Set set = mapReq.keySet();
//        Iterator iterator = set.iterator();
//        String bld_code = "";
//        double req_qty = 0;
//        double out_qty = 0;
//        while (iterator.hasNext()) {
//            bld_code = TypeTool.getString(iterator.next());
//            req_qty = TypeTool.getDouble(mapReq.get(bld_code));
//            out_qty = TypeTool.getDouble(mapOut.get(bld_code));
//            if (out_qty > req_qty) {
//                this.messageBox(bld_code + " �ĳ������Ѵ���������,������ѡ��!");
//                return false;
//            }
//        }

        // �жϿ����
        for (int i = 0; i < table_m.getRowCount(); i++) {
            if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            TParm parm = new TParm();
            parm.setData("BLD_CODE", table_m.getItemData(i, "BLD_CODE"));
            parm.setData("BLD_SUBCAT", table_m.getItemData(i, "BLD_SUBCAT"));
            parm.setData("BLD_TYPE", table_m.getItemData(i, "BLD_TYPE"));
            TParm result = BMSBldStockTool.getInstance().onQuery(parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("ѪƷ" + table_m.getItemData(i, "BLD_CODE") + "��治��");
                return false;
            }
        }

        // ������Ϣ���
        if ("I".equals(adm_type)) {
            TParm parm = new TParm();
            parm.setData("CASE_NO", case_no);
            TParm result = ADMTool.getInstance().getADM_INFO(parm);
            // �жϸò����Ƿ��Ժ
            if (!"".equals(result.getValue("DS_DATE", 0))) {
                this.messageBox("�˲����ѳ�Ժ");
                return false;
            }
        }

        return true;
    }

    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�CheckBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
    * �õ�ComboBox����
    *
    * @param tagName
    *            Ԫ��TAG����
    * @return
    */
   private TComboBox getComboBox(String tagName) {
       return (TComboBox) getComponent(tagName);
   }

   //=========================  chenxi modify 201305
   /**
	 * ����������֤
	 * 
	 * @return boolean
	 */
	public boolean checkPW() {
		String bmsExe = "bmsExe";
		String value = (String) this.openDialog(
				"%ROOT%\\config\\inw\\passWordCheck.x", bmsExe); 
		if (value == null) {
			return false;
		}
		return value.equals("OK");
	}	
}
