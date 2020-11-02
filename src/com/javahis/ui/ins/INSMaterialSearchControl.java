package com.javahis.ui.ins;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Vector;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ҽ�����ϲ�ѯ</p>
 *
 * <p>Description:ҽ�����ϲ�ѯ</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author lim
 * @version 1.0
 */
public class INSMaterialSearchControl extends TControl {

    private int currentTabPage = 0;

    private TTabbedPane tablePanel;

    //��һҳǩ�б�ѡ�е��к�.
    private int brlbTableSelectedRow = -1;

    //�������
    private String admSeq;

    private String ctzDesc;

    private String nhi_no;
    private TTable brlbTable;
    private TTable fymxxxTable;
 // ����
	private Compare compare = new Compare();
	// ����
	private boolean ascending = false;
	// ����
	private int sortColumn = -1;

    /**
     * ��ʼ��
     */
    public void onInit() {
    	
        this.tablePanel = ((TTabbedPane) getComponent("TablePanel"));
        this.brlbTable = ((TTable) getComponent("BRLBTABLE"));
        this.fymxxxTable = ((TTable) getComponent("FYMXXXTABLE"));
        tablePanel.setSelectedIndex(0);
        this.tablePanel.getComponentAt(1).setEnabled(false);
        this.tablePanel.getComponentAt(2).setEnabled(false);
        this.tablePanel.getComponentAt(3).setEnabled(false);

        Timestamp date = SystemTool.getInstance().getDate() ;
//        String[] dateStrArray = date.toString().split(" ") ;
//        this.setValue("START_DATE", dateStrArray[0].replace("-", "/")+" 00:00:00") ;
        //this.setValue("START_DATE",date) ;//delete by wanglong 20121011
        this.setValue("END_DATE", date) ;
        date.setMonth(date.getMonth()-3);//add by wanglong 20121011
        this.setValue("START_DATE",date) ;//add by wanglong 20121011
        this.setValue("DBZ", "PTBR") ;
        this.setValue("NYBLB", "1") ;
        
        TParm result2 = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // ���ҽ���������
        this.nhi_no = result2.getValue("NHI_NO", 0);

        if (this.getPopedem("LeaderEnabled")) {
        	((TButton)this.getComponent("Button4")).setEnabled(true) ;
        }
        addListener(brlbTable);
        addListener1(fymxxxTable);
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        String startDate = this.getValueString("START_DATE"); //��ʼʱ��
        String endDate = this.getValueString("END_DATE"); //����ʱ��
        String dBZ = this.getValueString("DBZ"); //������.
        String yblb = this.getValueString("NYBLB"); //ҽ�����.
        String  in_status = this.getValueString("IN_STATUS");
        String mrno = getValueString("MR_NO");
        if(in_status.equals("5"))
        	callFunction("UI|Button4|setEnabled", false);
        else   
        	callFunction("UI|Button4|setEnabled", true);
        if (this.currentTabPage == 0) { //�����б�
            if (checkInput(this.currentTabPage)) {
                String sql = "";
                if ("PTBR".equals(dBZ)) { //��ͨ����.
                    sql =
                            "SELECT A.ADM_SEQ ,TO_CHAR(A.UP_DATE,'yyyy/mm/dd') AS TOCHAR_UP_DATE,A.CONFIRM_NO,A.PAT_NAME, CASE A.SEX_CODE WHEN '1' THEN '��' WHEN '2' THEN 'Ů' ELSE '' END  AS CHN_DESC,A.IDNO,C.CTZ_DESC,A.IN_STATUS ," +
                            "TO_CHAR(A.DOWN_DATE,'yyyy/mm/dd') AS TOCHAR_DOWN_DATE ,D.CASE_NO,D.MR_NO,TO_CHAR(A.IN_DATE,'yyyy/mm/dd') AS TOCHAR_INDATE,' ' AS SDISEASE_CODE,' ' AS DISEASE_DESC," +
                            "TO_CHAR(D.DS_DATE,'yyyy/mm/dd') AS TOCHAR_DSDATE,A.HIS_CTZ_CODE" +
                            " FROM INS_ADM_CONFIRM A, SYS_CTZ C ,ADM_INP D" +
                            " WHERE A.CASE_NO = D.CASE_NO " +
//                            " AND S.GROUP_ID = 'SYS_SEX'" +
//                            " AND A.SEX_CODE=S.ID(+)" +
//                            " AND A.CTZ1_CODE=C.NHI_NO" +
                            " AND A.HIS_CTZ_CODE = C.CTZ_CODE"+
                            " AND C.NHI_CTZ_FLG = 'Y'" +
                            " AND TO_CHAR(A.IN_DATE,'yyyymmdd') <= '" +
                            transferDate(endDate) + "'" +
                            " AND TO_CHAR(A.IN_DATE,'yyyymmdd') >= '" +
                            transferDate(startDate) + "'" +
                            " AND A.SDISEASE_CODE IS NULL" ;
//                            " AND A.IN_STATUS<>'5'";
                    if(!mrno.equals("")){
                    	sql += " AND D.MR_NO = '" + mrno + "'";
                    }
                    //==============chenxi  modify  2012.05.28
                    if(!in_status.equals("")){
                    	sql += " AND A.IN_STATUS = '" + in_status + "'" ;
                    }
                    //==================chenxi   modify  2012.05.28
                    

                } else if ("DBZBR".equals(dBZ)) { //�����ֲ���
                    sql =
                            "SELECT A.ADM_SEQ ,TO_CHAR(a.UP_DATE,'yyyy/mm/dd') as TOCHAR_UP_DATE,a.CONFIRM_NO," +
                            "a.PAT_NAME,s.CHN_DESC,a.IDNO,c.CTZ_DESC,a.IN_STATUS ,TO_CHAR(a.DOWN_DATE,'yyyy/mm/dd') " +
                            "as TOCHAR_DOWN_DATE ,d.CASE_NO,d.MR_NO,TO_CHAR(a.in_date,'yyyy/mm/dd') as TOCHAR_INDATE,A.SDISEASE_CODE," +
                            "F.CHN_DESC AS DISEASE_DESC,TO_CHAR(d.ds_date,'yyyy/mm/dd') as TOCHAR_DSDATE,A.HIS_CTZ_CODE" +
                            " FROM INS_ADM_CONFIRM A, SYS_DICTIONARY S, SYS_CTZ C ,ADM_INP D,SYS_DICTIONARY F " +
                            " WHERE A.CASE_NO = D.CASE_NO " +
                            " AND S.GROUP_ID = 'SYS_SEX'" +
                            " AND A.SEX_CODE=S.ID" +
//                            " AND A.CTZ1_CODE=C.NHI_NO" +
                            " AND A.HIS_CTZ_CODE = C.CTZ_CODE"+
                            " AND C.NHI_CTZ_FLG = 'Y'" +
                            " AND TO_CHAR(A.IN_DATE,'yyyymmdd') <= '" +
                            transferDate(endDate) +
                            "'" +
                            " AND TO_CHAR(A.IN_DATE,'yyyymmdd') >= '" +
                            transferDate(startDate) +
                            "'" +
                            " AND F.GROUP_ID='SIN_DISEASE' "+
                            " AND F.ID = A.SDISEASE_CODE "+
//                            " AND A.sdisease_code=E.disease_code" +
                            " AND A.SDISEASE_CODE IS NOT NULL"+
                            " AND A.IN_STATUS<>'5'";
                    if(!mrno.equals("")){
                    	sql += " AND D.MR_NO = '" + mrno + "'";
                    }
                    //==============chenxi  modify  2012.05.28
                    if(!in_status.equals("")){
                    	sql += " AND A.IN_STATUS = '" + in_status + "'" ;
                    }
                    //==================chenxi   modify  2012.05.28
                }
                StringBuilder sbuilder = new StringBuilder(sql);
//                if (!"".equals(yblb)) {
//                    sbuilder.append(" AND substr(C.CTZ_CODE,0,1)='" + yblb +"'");
//                }
                if("1".equals(yblb)){
                	sbuilder.append(" AND C.CTZ_CODE IN ('11','12','13')");
                }else if("2".equals(yblb)){
                	sbuilder.append(" AND C.CTZ_CODE IN ('21','22','23')");
                }else{
                	sbuilder.append(" AND C.CTZ_CODE IN ('11','12','13','21','22','23')");
                }

                TParm result = new TParm(TJDODBTool.getInstance().select(
                        sbuilder.toString()));
                if (result.getErrCode() < 0) {
                    messageBox(result.getErrText());
                    return;
                }
                if (result.getCount() <= 0) {
                    messageBox("��������");
                    this.callFunction("UI|BRLBTABLE|setParmValue", new TParm());
                    return;
                }
                this.callFunction("UI|BRLBTABLE|setParmValue", result);

                this.clearAttr();
            }
        }
    }
    /**
     * �˲�ȫ���ʸ�ȷ����
     */
    public void checkAll() {
        TTable brlb = ((TTable)this.getComponent("BRLBTABLE"));
        TParm tableData = brlb.getParmValue();

        if (tableData.getCount() <= 0) {
            messageBox("û��Ҫ�˲������.");
            return;
        }
        tableData.addData("HOSP_NHI_NO", this.nhi_no);

        TParm result = TIOM_AppServer.executeAction(
                "action.ins.INSMaterialSearchAction", "checkAllAction",
                tableData);

        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        } else {
            messageBox("�˲�ͨ��.");
            int tempRow = this.brlbTableSelectedRow;
            this.onQuery();
            brlb.setSelectedRow(tempRow);
        }
    }

    /**
     * �˲�ѡ���ʸ�ȷ����
     */
    public void checkSelected() {

        if (this.brlbTableSelectedRow != -1) {
            TParm inparm = new TParm();
            TTable brlb = ((TTable)this.getComponent("BRLBTABLE"));
            TParm tableData = brlb.getParmValue();
            inparm.addData("ADM_SEQ", this.getAdmSeq());
            inparm.addData("HOSP_NHI_NO", this.getNhi_no());
            inparm.setData("INS_TYPE",tableData.getValue("HIS_CTZ_CODE",this.brlbTableSelectedRow).substring(0,1));
            inparm.addData("CONFIRM_NO",
                           tableData.getValue("CONFIRM_NO",
                                              this.brlbTableSelectedRow));
            TParm result = TIOM_AppServer.executeAction(
                    "action.ins.INSMaterialSearchAction", "checkSelectedAction",
                    inparm);

            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            } else {
                messageBox("�˲�ͨ��.");
                int tempRow = this.brlbTableSelectedRow;
                this.onQuery();
                brlb.setSelectedRow(tempRow);
            }
        } else {
            messageBox("��ѡ��Ҫ�˲�Ĳ�����Ϣ.");
            return;
        }
    }

    /**
     * �����ʸ�ȷ����
     */
    public void cancelSelected() {
        if (this.brlbTableSelectedRow != -1) {
            TParm inparm = new TParm();
            TTable brlb = ((TTable)this.getComponent("BRLBTABLE"));
            TParm tableData = brlb.getParmValue();
            inparm.addData("ADM_SEQ", this.getAdmSeq());
            inparm.addData("HOSP_NHI_NO", this.getNhi_no());
            inparm.addData("CONFIRM_NO",
                           tableData.getValue("CONFIRM_NO",
                                              this.brlbTableSelectedRow));
            inparm.setData("INS_TYPE",tableData.getValue("HIS_CTZ_CODE",this.brlbTableSelectedRow).substring(0,1));
            TParm result = TIOM_AppServer.executeAction(
                    "action.ins.INSMaterialSearchAction",
                    "cancelSelectedAction", inparm);

            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            } else {
                messageBox("����ͨ��.");
                int tempRow = this.brlbTableSelectedRow;
                this.onQuery();
                brlb.setSelectedRow(tempRow);
            }
        } else {
            messageBox("��ѡ��Ҫ�����˲�Ĳ�����Ϣ.");
            return;
        }
    }

    /**
     * ���
     */
    public void onExport() {
        if (this.currentTabPage == 0) { //�����б�
            TTable table = (TTable)this.getComponent("BRLBTABLE");
            if (table.getRowCount() <= 0) {
                this.messageBox("û�л������");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(table, "�����б�");
        } else if (this.currentTabPage == 2) { //������ϸ��Ϣ
            TTable table = (TTable)this.getComponent("FYMXXXTABLE");
            if (table.getRowCount() <= 0) {
                this.messageBox("û�л������");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(table, "������ϸ��Ϣ");
        } else if (this.currentTabPage == 3) { //ҽ���������ط�����ϸ
            TTable table = (TTable)this.getComponent("YBZXXZFYMX");
            if (table.getRowCount() <= 0) {
                this.messageBox("û�л������");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(table, "ҽ���������ط�����ϸ");
        }
    }

    /**
     * �ʸ�ȷ�����ӡ
     */
    public void onQualificationPrint() {

        if (this.brlbTableSelectedRow == -1) {
            messageBox("û��ѡ�л���.");
            return;
        }
        TTable brlb = ((TTable)this.getComponent("BRLBTABLE"));
        TParm parm = brlb.getParmValue();
        String mrNo = parm.getValue("MR_NO", this.brlbTableSelectedRow); //������
        String inDate = parm.getValue("TOCHAR_INDATE",
                                      this.brlbTableSelectedRow); //��Ժ����
        String ctzDesc = parm.getValue("CTZ_DESC", this.brlbTableSelectedRow); //��Ա���
        String sexDesc = parm.getValue("CHN_DESC", this.brlbTableSelectedRow); //�Ա�
        String confirmNo = parm.getValue("CONFIRM_NO", this.brlbTableSelectedRow); //�ʸ�ȷ�����
//        String Sqlstr =
//                " SELECT M.CONFIRM_NO ,F.CHN_DESC AS CBQX, M.NHIHOSP_NO , M.HOSP_CLASS_CODE ,D.CHN_DESC AS ADM_CATEGORY, M.PERSONAL_NO , " +
//                " M.IDNO , M.PAT_NAME , M.SEX_CODE , M.BIRTH_DATE , M.PAT_AGE , M.CTZ1_CODE , M.UNIT_NO , " +
//                " M.INS_UNIT , M.UNIT_CODE , M.UNIT_DESC , M.DEPT_DESC , M.DIAG_DESC , M.IN_DATE , " +
//                " M.EMG_FLG , M.INP_TIME , M.INS_FLG , M.TRANHOSP_NO , M.TRANHOSP_DESC , M.ADM_SEQ , " +
//                " M.TRAN_CLASS , M.TRAN_NUM , M.HOMEBED_TYPE , M.HOMEDIAG_DESC , M.HOMEBED_TIME , " +
//                " M.HOMEBED_DAYS , M.ADDINS_AMT , M.INSBRANCH_CODE , M.ADDOWN_AMT , M.ADDPAY_AMT , " +
//                " M.ADDNUM_AMT , M.INSBASE_LIMIT_BALANCE , M.INS_LIMIT_BALANCE , M.START_STANDARD_AMT , " +
//                " M.RESTART_STANDARD_AMT , M.OWN_RATE , M.DECREASE_RATE , M.REALOWN_RATE , M.INSOWN_RATE , " +
//                " M.STATION_DESC , M.BED_NO , M.TRANHOSP_RESTANDARD_AMT , M.DS_DATE , M.DSDIAG_CODE , M.DSDIAG_DESC , " +
//                " M.DSDIAG_DESC2 , M.DEPT_CODE , M.TRANHOSP_DAYS , M.INLIMIT_DATE , M.ADM_PRJ , M.SPEDRS_CODE , " +
//                " M.INSOCC_CODE , M.MR_NO , M.OVERINP_FLG , M.INSCASE_NO , M.OPEN_FLG , M.SOURCE_CODE , M.CANCEL_FLG , " +
//                " M.CONFIRM_SRC , M.OPT_USER , M.OPT_DATE , M.OPT_TERM , M.IN_STATUS , M.UP_DATE , " +
//                " M.DOWN_DATE , M.AUD_DATE , M.DOWND_DATE , M.CASE_NO, E.CHN_DESC AS SPECIAL_PAT, M.TREATMENT_CLASS," +
//                " C.CONTACTS_ADDRESS, C.TEL_HOME, M.DEPT_CODE , ZFBL2  " + // add �Ը�����2
//                " FROM INS_ADM_CONFIRM M , ADM_INP P , SYS_PATINFO C,SYS_DICTIONARY D,SYS_DICTIONARY E,SYS_DICTIONARY F " +
//                " WHERE M.MR_NO = '" + mrNo + "' " +
//                " AND TO_CHAR(M.IN_DATE,'yyyy/mm/dd')='" + inDate + "'" +
//                " AND D.GROUP_ID='INS_JYLB' AND D.ID = M.ADM_CATEGORY "+
//                " AND E.GROUP_ID='SP_PRESON_TYPE' AND E.ID = M.SPECIAL_PAT "+
//                " AND F.GROUP_ID='INS_FZX' AND F.ID=M.INSBRANCH_CODE "+
//                " and m.case_no = p.case_no " +
//                " AND M.MR_NO = C.MR_NO" +
//                " AND M.IN_STATUS <> '5' ";
        String Sqlstr =
            " SELECT M.CONFIRM_NO ,M.INSBRANCH_CODE, M.NHIHOSP_NO , M.HOSP_CLASS_CODE ,M.ADM_CATEGORY, M.PERSONAL_NO , " +
            " M.IDNO , M.PAT_NAME , M.SEX_CODE , M.BIRTH_DATE , M.PAT_AGE , M.CTZ1_CODE , M.UNIT_NO , " +
            " M.INS_UNIT , M.UNIT_CODE , M.UNIT_DESC , M.DEPT_DESC , M.DIAG_DESC , M.IN_DATE , " +
            " M.EMG_FLG , M.INP_TIME , M.INS_FLG , M.TRANHOSP_NO , M.TRANHOSP_DESC , M.ADM_SEQ , " +
            " M.TRAN_CLASS , M.TRAN_NUM , M.HOMEBED_TYPE , M.HOMEDIAG_DESC , M.HOMEBED_TIME , " +
            " M.HOMEBED_DAYS , M.ADDINS_AMT , M.INSBRANCH_CODE , M.ADDOWN_AMT , M.ADDPAY_AMT , " +
            " M.ADDNUM_AMT , M.INSBASE_LIMIT_BALANCE , M.INS_LIMIT_BALANCE , M.START_STANDARD_AMT , " +
            " M.RESTART_STANDARD_AMT , M.OWN_RATE , M.DECREASE_RATE , M.REALOWN_RATE , M.INSOWN_RATE , " +
            " M.STATION_DESC , M.BED_NO , M.TRANHOSP_RESTANDARD_AMT , M.DS_DATE , M.DSDIAG_CODE , M.DSDIAG_DESC , " +
            " M.DSDIAG_DESC2 , M.DEPT_CODE , M.TRANHOSP_DAYS , M.INLIMIT_DATE , M.ADM_PRJ , M.SPEDRS_CODE , " +
            " M.INSOCC_CODE , M.MR_NO , M.OVERINP_FLG , M.INSCASE_NO , M.OPEN_FLG , M.SOURCE_CODE , M.CANCEL_FLG , " +
            " M.CONFIRM_SRC , M.OPT_USER , M.OPT_DATE , M.OPT_TERM , M.IN_STATUS , M.UP_DATE , " +
            " M.DOWN_DATE , M.AUD_DATE , M.DOWND_DATE , M.CASE_NO, M.SPECIAL_PAT, M.TREATMENT_CLASS," +
            " C.CONTACTS_ADDRESS, C.TEL_HOME, M.DEPT_CODE , ZFBL2  " + // add �Ը�����2
            " FROM INS_ADM_CONFIRM M , ADM_INP P , SYS_PATINFO C" +
            " WHERE M.MR_NO = '" + mrNo + "' " +
            " AND M.CONFIRM_NO = '"+confirmNo+"'"+
            " AND M.CASE_NO = P.CASE_NO " +
            " AND TO_CHAR(M.IN_DATE,'yyyy/mm/dd')='" + inDate + "'" +
            " AND M.MR_NO = C.MR_NO" +
            " AND M.IN_STATUS <> '5' ";

        TParm result1 = new TParm(TJDODBTool.getInstance().select(Sqlstr));

        if (result1.getCount() <= 0) {
            messageBox("û�д�ӡ����!");
            return;
        }
        DecimalFormat df = new DecimalFormat("##########0.00");
        TParm exportData = new TParm();
        //============================lim modify 2012/04/08 begin
        String message="";
        if (result1.getValue("ADM_PRJ", 0).equals("1")) {
        	message="����";
		}else if(result1.getValue("ADM_PRJ", 0).equals("2")){
			message="סԺ";
		}else if(result1.getValue("ADM_PRJ", 0).equals("3")){
			message="ת��תԺ";
		}

        exportData.setData("JYXM", "TEXT", message); //��ҽ��Ŀ
        //===========================lim modify 2012/04/08 end

//        exportData.setData("JYXM", "TEXT", result1.getValue("ADM_PRJ", 0)); //��ҽ��Ŀ

        exportData.setData("DWDM", "TEXT", result1.getValue("UNIT_CODE", 0)); //��λ����
		exportData.setData("CBQX", "TEXT",getNameDesc("INS_FZX", result1.getValue("INSBRANCH_CODE", 0)) ) ;//�α�����
        exportData.setData("BM", "TEXT", result1.getValue("CONFIRM_NO", 0)); //����

        exportData.setData("DWMCH", "TEXT", result1.getValue("UNIT_DESC", 0)); //��λ����

        exportData.setData("GMSFHM", "TEXT", result1.getValue("IDNO", 0)); //�������֤����
        exportData.setData("XM", "TEXT", result1.getValue("PAT_NAME", 0)); //����
        exportData.setData("XB", "TEXT", sexDesc); //�Ա�
        exportData.setData("NL", "TEXT", result1.getValue("PAT_AGE", 0)); //����

        exportData.setData("RYLB", "TEXT", ctzDesc); //��Ա���
        exportData.setData("SFJZ", "TEXT",
                           String.valueOf("N".equals(result1.
                getValue("EMG_FLG", 0)) ? "��" : "��")); //�Ƿ���
        exportData.setData("TSRYBS", "TEXT",getNameDesc("SP_PRESON_TYPE", result1.getValue("SPECIAL_PAT", 0))); //������Ա��ʶ

        exportData.setData("JZHYYMC", "TEXT", Operator.getHospitalCHNFullName()); //����ҽԺ����
        exportData.setData("JB", "TEXT", "����"); //����
        //String SQLDept = " SELECT DEPT_DESC FROM SYS_DEPT WHERE DEPT_CODE = '"+hospDeptCode+"'";
        exportData.setData("KB", "TEXT", result1.getValue("DEPT_DESC", 0)); //�Ʊ�-----------------------
        exportData.setData("BAH", "TEXT", mrNo); //������

        exportData.setData("RYZHD", "TEXT", result1.getValue("DIAG_DESC", 0)); //��Ժ���
        exportData.setData("DJCZHY", "TEXT", result1.getValue("INP_TIME", 0)); //�ڼ���סԺ
        exportData.setData("JYLB", "TEXT", getNameDesc("INS_JYLB", result1.getValue("ADM_CATEGORY", 0))); //��ҽ���------------------

        exportData.setData("ZHYRQ", "TEXT", inDate); //סԺ����
        exportData.setData("ZHYZHCHJZRQ", "TEXT",
                           null!=result1.getValue("INLIMIT_DATE", 0)&&result1.getValue("INLIMIT_DATE", 0).length()>0?result1.getValue("INLIMIT_DATE", 0).substring(0,10):""); //סԺ���ֹ����

        exportData.setData("ZHZHDJBH", "TEXT",
                           (result1.getValue("TRAN_NUM", 0) == null ? " " :
                            result1.getValue("TRAN_NUM", 0))); //ת��ǼǱ��
        exportData.setData("ZHZHYLJZYTS", "TEXT",
                           result1.getValue("TRANHOSP_DAYS", 0)); //ת��Ժ�ۼ�סԺ����

        exportData.setData("ZHCHYMCH", "TEXT",
                           (result1.getValue("TRANHOSP_DESC", 0) == null ? " " :
                            result1.getValue("TRANHOSP_DESC", 0))); //ת��Ժ����
        exportData.setData("ZHCHJB", "TEXT",
                           (result1.getValue("TRAN_CLASS", 0) == null ? " " :
                            result1.getValue("TRAN_CLASS", 0))); //����
        exportData.setData("MTZHY", "TEXT",
                           (result1.getValue("SPEDRS_CODE", 0) == null ? " " :
                            result1.getValue("SPEDRS_CODE", 0))); //����סԺ

//		exportData.setData("JLJCHJGMC", "TEXT", data) ;//�����Ҵ���������
        exportData.setData("JCHLJTSH", "TEXT",
                           result1.getValue("HOMEBED_DAYS", 0)); //�Ҵ��ۼ�����
        exportData.setData("JCHZHD", "TEXT",
                           result1.getValue("HOMEDIAG_DESC", 0)); //�Ҵ����

//		exportData.setData("TCHZFBL1", "TEXT", data) ;//ͳ���Ը�����1
//		exportData.setData("TCHZFBL2", "TEXT", data) ;//ͳ���Ը�����2

        exportData.setData("CJYLJZH", "TEXT",
                           String.valueOf("Y".equals(result1.
                getValue("INS_FLG", 0)) ? "��" : "��")); //�μ�ҽ�ƾ���
        exportData.setData("JZHZFBL", "TEXT", result1.getValue("INSOWN_RATE", 0)); //�����Ը�����

		exportData.setData("QFBZHSHYE", "TEXT", "0.00") ;//�𸶱�׼ʣ���
        exportData.setData("QFBZH", "TEXT",
                           df.format(result1.getDouble("START_STANDARD_AMT", 0))); //�𸶱�׼
        exportData.setData("SHSHQFBZHJE", "TEXT",
                           df.format(result1.getDouble("RESTART_STANDARD_AMT",
                0))); //ʵ���𸶱�׼���

        exportData.setData("WJSYLJGFSHJE", "TEXT", "0.00"); //δ����ҽ�ƻ����������
        exportData.setData("WJSYLJGSHBJE", "TEXT", "0.00"); //δ����ҽ�ƻ����걨���
        exportData.setData("YJSLJSHPJE", "TEXT",
                           df.format(result1.getDouble("ADDNUM_AMT", 0))); //�ѽ����ۼ��������
        exportData.setData("TCHJJZGZFXESHYE", "TEXT",
                           df.format(result1.
                                     getDouble("INSBASE_LIMIT_BALANCE", 0))); //ͳ��������֧���޶�ʣ���
        exportData.setData("YLJZHZHGZFXESHYE", "TEXT",
                           df.format(result1.getDouble("INS_LIMIT_BALANCE", 0))); //ҽ�ƾ������֧���޶�ʣ���
        exportData.setData("DEJZHCHBDW", "TEXT",
        		getNameDesc("INS_DEJZDW",result1.getValue("INSOCC_CODE", 0))); //�������а쵥λ

        this.openPrintWindow("%ROOT%\\config\\prt\\INS\\QualificationTable.jhw",
                             exportData);
    }

    /**
     * �������˵��
     * @param groupId
     * @param id
     * @return
     */
    private String getNameDesc(String groupId ,String id){
    	String sql=" SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID ='"+groupId+"' AND ID='"+id+"'";
    	TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
    	if (result1.getErrCode()<0) {
			return id;
		}
    	return result1.getValue("CHN_DESC",0);
    }

    /**
     * ���ű��ӡ
     */
    public void onPrint2() {
    	
        if (this.brlbTableSelectedRow == -1) {
            messageBox("û��ѡ�л���.");
            return;
        }
        TTable brlb = ((TTable)this.getComponent("BRLBTABLE"));
        TParm parm = brlb.getParmValue();
        String mrNo = parm.getValue("MR_NO", this.brlbTableSelectedRow); //������
        //String dsDate = parm.getValue("TOCHAR_DSDATE", this.brlbTableSelectedRow) ;//��Ժ����
        String confirmNo = parm.getValue("CONFIRM_NO",
                                         this.brlbTableSelectedRow); //�ʸ�ȷ�����
        String admSeq = parm.getValue("ADM_SEQ",
                this.brlbTableSelectedRow); //�ʸ�ȷ�����
        String dBZ = this.getValueString("DBZ"); //������.
        //��Ժ���ڲ�ѯ
        String searchSql = "SELECT DS_DATE,IN_STATUS FROM INS_ADM_CONFIRM WHERE CONFIRM_NO = '" +
                           confirmNo + "'";
        //System.out.println("��Ժ���ڲ�ѯsql"+searchSql);
        TParm searchResult = new TParm(TJDODBTool.getInstance().select(
                searchSql));
        String dsDate = searchResult.getValue("DS_DATE", 0); //��Ժ����
        String totString=null;
        if (null!=searchResult.getValue("IN_STATUS",0) && searchResult.getValue("IN_STATUS",0).equals("4")) {
        	totString=" M.PHA_AMT+M.EXM_AMT+M.TREAT_AMT+M.OP_AMT+M.BED_AMT+M.MATERIAL_AMT+M.BLOODALL_AMT+M.BLOOD_AMT+M.OTHER_AMT + M.OTHER_OWN_AMT AS HJF, " ;
		}else{
			totString=" M.PHA_AMT+M.EXM_AMT+M.TREAT_AMT+M.OP_AMT+M.BED_AMT+M.MATERIAL_AMT+M.BLOODALL_AMT+M.BLOOD_AMT+M.OTHER_AMT AS HJF, " ;
		}
        String[] dsDateArray = dsDate.split(" ");

        if ("PTBR".equals(dBZ)) { //��ͨ����
            String sql =
                    " SELECT SUBSTR(M.YEAR_MON,1,4)||'��'||SUBSTR(M.YEAR_MON,5,2)||'��' AS SJ, " +
                    " M.ADM_CATEGORY , " +
//                    " case " +
//                    " when  M.ADM_CATEGORY = '21' then '��ͨסԺ' " +
//                    " when  M.ADM_CATEGORY = '24' then '����סԺ' " +
//                    " when  M.ADM_CATEGORY = '26' then '���ഫȾ��סԺ' " +
//                    " when  M.ADM_CATEGORY = '27' then '�Ǽ��ഫȾ��' " +
//                    " when  M.ADM_CATEGORY = '31' then 'I��סԺ���ⲡ' " +
//                    " when  M.ADM_CATEGORY = '32' then 'II��סԺ���ⲡ' " +
//                    " when  M.ADM_CATEGORY = '33' then '����I�����ؼҴ�' " +
//                    " when  M.ADM_CATEGORY = '34' then '����II�����ؼҴ�' " +
//                    " else ''" +
//                    " end AS JYLB," +
                    " V.CHN_DESC AS JYLB ,"+
                    "I.IPD_NO," +
                    " M.IDNO , M.PAT_NAME , X.CHN_DESC , M.PAT_AGE , Z.CTZ_DESC , " +
                    " M.UNIT_DESC , M.UNIT_CODE, " +
//                    " case " +
//                    " when  A.SPECIAL_PAT = '01' then '����' " +
//                    " when  A.SPECIAL_PAT = '02' then '��������' " +
//                    " when  A.SPECIAL_PAT = '03' then '�м���ģ' " +
//                    " when  A.SPECIAL_PAT = '04' then '�˲о���' " +
//                    " when  A.SPECIAL_PAT = '05' then '������Ա' " +
//                    " when  A.SPECIAL_PAT = '06' then '����Ա' " +
//                    " when  A.SPECIAL_PAT = '07' then '����������Ա' " +
//                    " when  A.SPECIAL_PAT = '08' then '�Ÿ�����' " +
//                    " when  A.SPECIAL_PAT = '09' then '�ǵ����֢'  " +
//                    " else '' " +
//                    " end AS RYBS, " +
                    " U.CHN_DESC AS RYBS,"+
                    " M.CONFIRM_NO , O.USER_NAME , B.CHN_DESC AS CBQX, " + //�ʸ�ȷ�����12,����ҽʦ13,�α�����
                    " to_char(M.IN_DATE,'yyyymmdd') AS IN_DATE,to_char(M.DS_DATE,'yyyymmdd') AS DS_DATE,M.IN_DATE AS IN_DATETEMP ,M.DS_DATE AS DS_DATETEMP," +
                   // "CASE WHEN ROUND(TO_NUMBER(M.DS_DATE-M.IN_DATE))<=0 THEN 1 ELSE ROUND(TO_NUMBER(M.DS_DATE-M.IN_DATE)) END AS REAL_STAY_DAYS," +
                    "I.ADM_DAYS AS ZHYTSH, M.MR_NO , " + //סԺ����,��Ժ����,סԺ����,סԺ��
                    " M.BED_NO , " + //��λ��
                    " case " +
                    " when  M.HOSP_CLS_CODE = '01' then 'һ��' " +
                    " when  M.HOSP_CLS_CODE = '02' then '����' " +
                    " when  M.HOSP_CLS_CODE = '03' then '����' " +
                    " else ''" +
                    " end AS JB, " + //,����
                    " M.DEPT_DESC , M.START_STANDARD_AMT , T.CHN_DESC , " + //,�Ʊ�,�𸶱�׼,���ز���//
                    " M.TRANHOSP_DESC , " + //ת��Ժ����
                    " case " +
                    " when  M.TRAN_CLASS = '01' then 'һ��' " +
                    " when  M.TRAN_CLASS = '02' then '����' " +
                    " when  M.TRAN_CLASS = '03' then '����' " +
                    " else '' " +
                    " end AS ZHYJB, " + //,ת��Ժ����
                    " M.TRANHOSP_RESTANDARD_AMT , " + //,ת��Ժʵ���𸶱�׼,
                    " M.INP_TIME , M.HOMEDIAG_DESC , M.DIAG_CODE||M.DIAG_DESC||' '||" + //�Ҵ�,��ͥ����
                    " (case " +
                    " when  M.TRAN_CLASS = '1' then '����'" +
                    " when  M.TRAN_CLASS = '2' then '��ת'" +
                    " when  M.TRAN_CLASS = '3' then 'δ��'" +
                    " when  M.TRAN_CLASS = '4' then '����'" +
                    " when  M.TRAN_CLASS = '5' then '����'" +
                    " else ''" +
                    " end) AS CHYZHD " + //,��Ժ���1
                    ", M.DIAG_DESC2 , " + //,��Ժ���2
                    " M.BASEMED_BALANCE , M.INS_BALANCE , " + //�ϴ�ҽ������֧���޶�ʣ�����,�ϴξ�������֧��ʣ���
                    " case when M.OWN_RATE <= 0 then 0 else M.OWN_RATE end AS ZFBL1, " + //�Ը�����1
                    " case when A.zfbl2 <= 0 then 0 else A.zfbl2 end AS ZFBL2, " + //,�Ը�����2
                    " case when M.INSOWN_RATE <= 0 then 0 else M.INSOWN_RATE end AS JZHZF, " + //,�����Ը�����,
                    " M.PERSON_ACCOUNT_AMT , " + //�����˻����
                    " case when M.DECREASE_RATE <= 0 then 0 else M.DECREASE_RATE end AS JFBL1, " + //,��������
                    " case when M.REALOWN_RATE <= 0 then 0 else M.REALOWN_RATE end AS SHJZF, " + //,ʵ���Ը�����
                    // �²���ʼ
                    " M.RESTART_STANDARD_AMT , STARTPAY_OWN_AMT , " + //ʵ���𸶱�׼���,���𸶱�׼�Ը��������
                    " M.INS_HIGHLIMIT_AMT , M.PERCOPAYMENT_RATE_AMT , " + //����޶����Ͻ��,ҽ�ƾ����Ը��������
                    " M.OWN_AMT , M.ADD_AMT , M.RESTART_STANDARD_AMT+M.STARTPAY_OWN_AMT+M.OWN_AMT+M.PERCOPAYMENT_RATE_AMT+M.ADD_AMT+M.INS_HIGHLIMIT_AMT AS GRRHJ, " + //�Էѽ��,�������,�ϼ�,
                    //" M.ACCOUNT_PAY_AMT , M.RESTART_STANDARD_AMT+M.STARTPAY_OWN_AMT+M.OWN_AMT+M.PERCOPAYMENT_RATE_AMT+M.ADD_AMT+M.INS_HIGHLIMIT_AMT-M.ACCOUNT_PAY_AMT - case when M.ARMYAI_AMT is null then 0 else M.ARMYAI_AMT end AS SJZFJE , " + //�����˻�����,ʵ���Ը����,
                    " M.ACCOUNT_PAY_AMT , " +//�����˻�����
			        " case when M.RESTART_STANDARD_AMT is null then 0 else M.RESTART_STANDARD_AMT end "+
			        " + "+
			        " case when M.STARTPAY_OWN_AMT is null then 0 else M.STARTPAY_OWN_AMT end "+
			        " + "+
			        " case when M.OWN_AMT is null then 0 else M.OWN_AMT end "+
			        " + "+
			        " case when M.PERCOPAYMENT_RATE_AMT is null then 0 else M.PERCOPAYMENT_RATE_AMT end "+
			        " + "+
			        " case when M.ADD_AMT is null then 0 else M.ADD_AMT end "+
			        " + "+
			        " case when M.INS_HIGHLIMIT_AMT is null then 0 else M.INS_HIGHLIMIT_AMT end "+
			        " - "+
			        " case when M.ACCOUNT_PAY_AMT is null then 0 else M.ACCOUNT_PAY_AMT end "+
			        " - "+
			        " case when M.ARMYAI_AMT is null then 0 else M.ARMYAI_AMT end AS SJZFJE," + //ʵ���Ը����,
                    " M.NHI_PAY , M.TOT_PUBMANADD_AMT , M.ARMYAI_AMT , M.ACCOUNT_PAY_AMT , " + //ͳ��,����,����,����
                    " M.NHI_PAY_REAL , case when M.TOT_PUBMANADD_AMT is null then 0 else M.TOT_PUBMANADD_AMT end AS GZ, " + //ͳ֧,��֧
                    " case when M.ARMYAI_AMT is null then 0 else M.ARMYAI_AMT end AS JUNZ, " + //,��֧,
                    " case when M.ACCOUNT_PAY_AMT is null  then 0 else M.ACCOUNT_PAY_AMT  end AS GEZHI, " + //��֧
                    " M.NHI_PAY+ " +
                    " (case when M.TOT_PUBMANADD_AMT is null then 0 else M.TOT_PUBMANADD_AMT end) + " +
                    " (case when M.ARMYAI_AMT is null then 0 else M.ARMYAI_AMT end)+ " +
                    " (case when M.ACCOUNT_PAY_AMT is null  then 0 else M.ACCOUNT_PAY_AMT  end) AS YLJGSHQHJ, '��' AS YLJGSHQJEHEDX, " + //ҽ�ƻ���������ϼ�,ҽ�ƻ���������ϼƴ�д
                    " M.NHI_PAY_REAL+ " +
                    " (case when M.TOT_PUBMANADD_AMT is null then 0 else M.TOT_PUBMANADD_AMT end) + " +
                    " (case when M.ARMYAI_AMT is null then 0 else M.ARMYAI_AMT end)+ " +
                    " (case when M.ACCOUNT_PAY_AMT is null  then 0 else M.ACCOUNT_PAY_AMT  end) AS SHBZHFJEHJ, '��' AS SHBZHFJEHJDX, " + //�籣֧�����ϼ�,�籣֧�����ϼƴ�д
                    " M.APPLY_AMT , '��'AS APPLY_AMT_CHI , " + //ҽ�ƻ���������,ҽ�ƻ����������д
                    " M.HOSP_APPLY_AMT , '��' AS HOSP_APPLY_AMT_CHI, " + //�������֧�����,�������֧������д
                    // �м䲿�֣��������ܸ�������˽���֪��дʲô
                    " M.PHA_AMT , M.PHA_OWN_AMT , M.PHA_ADD_AMT , M.PHA_NHI_AMT , '' AS YSH, " + //ҩ��,ҩ��,ҩ��,ҩ��,ҩ��
                    " M.EXM_AMT , M.EXM_OWN_AMT , M.EXM_ADD_AMT , M.EXM_NHI_AMT , '' AS JSH, " + //�췢,����,����,����,����,
                    " M.TREAT_AMT , M.TREAT_OWN_AMT , M.TREAT_ADD_AMT , M.TREAT_NHI_AMT , '' AS ZHSH , " + //�η�,����,����,����,����,
                    " M.OP_AMT , M.OP_OWN_AMT , M.OP_ADD_AMT , M.OP_NHI_AMT , '' AS SHSHEN, " + //�ַ�,����,����,����,����
                    " M.BED_AMT , M.BED_OWN_AMT , M.BED_ADD_AMT , M.BED_NHI_AMT , '' AS CHSHEN, " + //����,����,����,����,����
                    " M.MATERIAL_AMT , M.MATERIAL_OWN_AMT , M.MATERIAL_ADD_AMT , M.MATERIAL_NHI_AMT , '' AS YSHEN, " + //ҽ��,ҽ��,ҽ��,ҽ��,ҽ��
                    " M.BLOODALL_AMT , M.BLOODALL_OWN_AMT , M.BLOODALL_ADD_AMT , M.BLOODALL_NHI_AMT , '' AS SHUSHEN, " + //�䷢,����,����,����,����
                    " M.BLOOD_AMT , M.BLOOD_OWN_AMT , M.BLOOD_ADD_AMT, M.BLOOD_NHI_AMT , '' AS CHENSH, " + //�ɷ�,����,����,����,����
                    " M.OTHER_AMT , M.OTHER_OWN_AMT , M.OTHER_ADD_AMT , M.OTHER_NHI_AMT , '' AS QSHEN, " + //�䷢,����,����,����,����,
                    // �����У����ϡ���Ŀ��֪��дʲô
                    " '' AS CAIF, '' AS CAIZ , '' AS CAIZENG , '' AS CAISHEN, '' AS CAISH, " + //�ķ�,����,����,����,����,
                    totString+
                    " M.PHA_OWN_AMT+M.EXM_OWN_AMT+M.TREAT_OWN_AMT+M.OP_OWN_AMT+M.BED_OWN_AMT+M.MATERIAL_OWN_AMT+M.BLOODALL_OWN_AMT+M.BLOOD_OWN_AMT+M.OTHER_OWN_AMT AS HEZ, " +
                    " M.PHA_ADD_AMT+M.EXM_ADD_AMT+M.TREAT_ADD_AMT+M.OP_ADD_AMT+M.BED_ADD_AMT+M.MATERIAL_ADD_AMT+M.BLOODALL_ADD_AMT+M.BLOOD_ADD_AMT+M.OTHER_ADD_AMT AS HEZENG, " +
                    " M.PHA_NHI_AMT+M.EXM_NHI_AMT+M.TREAT_NHI_AMT+M.OP_NHI_AMT+M.BED_NHI_AMT+M.MATERIAL_NHI_AMT+M.BLOODALL_NHI_AMT+M.BLOOD_NHI_AMT+M.OTHER_NHI_AMT AS HESHEN, " +
                    " '' AS HESH, " + //�ϼƷ�,�ϼ���,�ϼ���,�ϼ���,�ϼ���,
                    " M.REGION_CODE , M.ADM_SEQ , M.CONFIRM_NO, A.SPECIAL_PAT, M.ARMYAI_AMT,M.NHI_COMMENT " +
                    " FROM INS_IBS M " +
                    " left JOIN INS_ADM_CONFIRM A " +
                    " ON M.CONFIRM_NO = A.CONFIRM_NO " +
                    " left JOIN ADM_INP I " +
                    " ON M.REGION_CODE = I.REGION_CODE " +
                    " AND M.MR_NO = I.MR_NO " +
                    " AND M.CASE_NO = I.CASE_NO " +
                    " left JOIN SYS_OPERATOR O " +
                    " ON I.REGION_CODE = O.REGION_CODE " +
                    " AND I.VS_DR_CODE = O.USER_ID " +
                    " left JOIN SYS_DICTIONARY X " +
                    " ON M.SEX_CODE = X.ID AND X.GROUP_ID = 'SYS_SEX' " +
                    " INNER JOIN SYS_CTZ Z " +
//                    " ON M.CTZ1_CODE = Z.CTZ_CODE " +//modify by lim 2012/0426
                    " ON A.HIS_CTZ_CODE = Z.CTZ_CODE " +//modify by lim 2012/0426
                    " left JOIN SYS_DICTIONARY B " +
                    " ON M.INSBRANCH_CODE = B.ID AND B.GROUP_ID='INS_FZX' " +
                    " left JOIN SYS_DICTIONARY T " +
                    " ON M.SPEDRS_CODE = T.ID AND T.GROUP_ID = 'INS_MTLBA' " +
                    " left JOIN SYS_DICTIONARY V "+
                    " ON V.GROUP_ID = 'INS_JYLB' AND V.ID = M.ADM_CATEGORY "+
                    " left JOIN SYS_DICTIONARY U "+
                    " ON U.GROUP_ID = 'SP_PRESON_TYPE' AND U.ID = A.SPECIAL_PAT "+
                    " WHERE M.MR_NO = '" + mrNo + "' AND M.ADM_SEQ='"+admSeq+"'" ;
//                    " AND TO_CHAR(M.DS_DATE,'yyyy-mm-dd') = '" + dsDateArray[0] + "' ";
//                    System.out.println("���ű�����sql:::::::::::::::::::::::::::::::::::::::::"+sql);
           
            TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));

            if (result1.getCount() <= 0) {
                messageBox("û�д�ӡ����!");
                return;
            }

            DecimalFormat df = new DecimalFormat("##########0.00");
            TParm exportData = new TParm();
            exportData.setData("YLJGDM", "TEXT", "0124765-6"); //ҽ�ƻ�������------------
            exportData.setData("TITLERQ", "TEXT", result1.getValue("SJ", 0)); //����
            exportData.setData("BH", "TEXT", "���籣ҽ֧��"); //���//TODO:

            exportData.setData("YLJGMCH", "TEXT", "�����̩�������Ѫ�ܲ�ҽԺ"); //ҽ�ƻ�������
            exportData.setData("JYLB", "TEXT", result1.getValue("JYLB", 0)); //��ҽ���
            exportData.setData("DW", "TEXT", "Ԫ"); //��λ

            exportData.setData("GMSHFZHHM", "TEXT", result1.getValue("IDNO", 0)); //�������֤����
            exportData.setData("XM", "TEXT", result1.getValue("PAT_NAME", 0)); //����
            exportData.setData("XB", "TEXT", result1.getValue("CHN_DESC", 0)); //�Ա�
            exportData.setData("NL", "TEXT", result1.getValue("PAT_AGE", 0)); //����
            exportData.setData("RYLB", "TEXT", result1.getValue("CTZ_DESC", 0)); //��Ա���

            exportData.setData("DWMCH", "TEXT", result1.getValue("UNIT_DESC", 0)); //��λ����
            exportData.setData("DWDM", "TEXT", result1.getValue("UNIT_CODE", 0)); //��λ����
            exportData.setData("RYBSH", "TEXT", result1.getValue("RYBS", 0)); //��Ա��ʶ

            exportData.setData("ZGQRSHH", "TEXT",
                               result1.getValue("CONFIRM_NO", 0)); //�ʸ�ȷ�����
            exportData.setData("JZHYSH", "TEXT",
                               result1.getValue("USER_NAME", 0)); //����ҽʦ
            exportData.setData("CBQX", "TEXT", result1.getValue("CBQX", 0)); //�α�����

            String inDate = result1.getValue("IN_DATE", 0);
            String outDate = result1.getValue("DS_DATE", 0);
            Timestamp outDatetemp=result1.getTimestamp("DS_DATETEMP", 0);
            Timestamp inDatetemp=result1.getTimestamp("IN_DATETEMP", 0);
            if(inDate!=null && !"".equals(inDate)){
				exportData.setData("ZHYRQ", "TEXT", (inDate.substring(0, 4)
						+ "��" + inDate.substring(4, 6) + "��"
						+ inDate.substring(6, 8) + "��")); // סԺ����
			}
            else{
            	exportData.setData("ZHYRQ", "TEXT","") ;
            }
            if(outDate!=null && !"".equals(outDate)){
	            exportData.setData("CHYRQ", "TEXT",
	                               (outDate.substring(0, 4) + "��" +
	                                outDate.substring(4, 6) + "��" +
	                                outDate.substring(6, 8) + "��")); //��Ժ����
            }else{
            	exportData.setData("CHYRQ", "TEXT","");
            }

//            String inDays = "" ;
//            if( (inDate!=null && !"".equals(inDate)) && (outDate!=null && !"".equals(outDate))){
//            	if(inDate.equals(outDate)){
//            		inDays = "1" ;
//            	}else{
//                	Date inDateNew = new Date(Integer.parseInt(inDate.substring(0, 4)), Integer.parseInt(inDate.substring(4, 6)), Integer.parseInt(inDate.substring(6, 8))) ;
//                	Date outDateNew = new Date(Integer.parseInt(outDate.substring(0, 4)), Integer.parseInt(outDate.substring(4, 6)), Integer.parseInt(outDate.substring(6, 8))) ;
//                	long inDateTs = inDateNew.getTime() ;
//                	long outDateTs = outDateNew.getTime() ;
//                	long diffTime = outDateTs - inDateTs ;
//                	inDays = (diffTime/(24*60*60*1000))+"" ;
//            	}
//            }
            int rollDate =0;
            if(inDate.equals(outDate))
            	rollDate =1;
            else
                rollDate = StringTool.getDateDiffer(outDatetemp, inDatetemp);
            //exportData.setData("ZHYTSH", "TEXT", result1.getValue("ZHYTSH", 0)); //סԺ����
            exportData.setData("ZHYTSH", "TEXT",  rollDate); //סԺ����
            exportData.setData("ZHYH", "TEXT", result1.getValue("IPD_NO", 0)); //סԺ��

            exportData.setData("CHWH", "TEXT", result1.getValue("BED_NO", 0)); //��λ��
            exportData.setData("JB", "TEXT", result1.getValue("JB", 0)); //����
            exportData.setData("KB", "TEXT", result1.getValue("DEPT_DESC", 0)); //�Ʊ�
            exportData.setData("QFBZH", "TEXT",
                               result1.getValue("START_STANDARD_AMT", 0)); //�𸶱�׼
            exportData.setData("MTBZH", "TEXT",
                               result1.getValue("MT_DISEASE_DESC", 0)); //���ز���

            exportData.setData("ZHCHYYMC", "TEXT",
                               result1.getValue("TRANHOSP_DESC", 0)); //ת��Ժ����
            exportData.setData("ZHCYYJB", "TEXT", result1.getValue("ZHYJB", 0)); //ת��Ժ����
            exportData.setData("ZHCHYSHSHQFBZH", "TEXT",
                               result1.getValue("TRANHOSP_RESTANDARD_AMT", 0)); //ת��Ժʵ���𸶱�׼

            exportData.setData("DJCZHY", "TEXT", result1.getValue("INP_TIME", 0)); //�ڼ���סԺ
            exportData.setData("JCHBZH", "TEXT",
                               result1.getValue("HOMEDIAG_DESC", 0)); //�Ҵ�����
            exportData.setData("JCHZHD", "TEXT", " "); //�Ҵ����------------

            exportData.setData("CHYZHD1", "TEXT", result1.getValue("CHYZHD", 0)); //��Ժ���1
            exportData.setData("CHYZHD2", "TEXT",
                               result1.getValue("DIAG_DESC2", 0)); //��Ժ���2

            exportData.setData("ZFBL1", "TEXT", result1.getValue("ZFBL1", 0)); //�Ը�����1
            exportData.setData("ZFBL2", "TEXT", result1.getValue("ZFBL2", 0)); //�Ը�����2
            exportData.setData("JFBL1", "TEXT", result1.getValue("JFBL1", 0)); //�縺����
            exportData.setData("SHJZF", "TEXT", result1.getValue("SHJZF", 0)); //ʵ���Ը�
            exportData.setData("JZHZF", "TEXT", result1.getValue("JZHZF", 0)); //�����Ը�

            exportData.setData("SHCYB", "TEXT",
                               df.format(result1.getDouble("BASEMED_BALANCE", 0))); //�ϴ�ҽ������֧���޶�
            exportData.setData("SHCJZH", "TEXT",
                               df.format(result1.getDouble("INS_BALANCE", 0))); //�ϴξ�������
//            exportData.setData("GRZHHYE", "TEXT",
//                               df.format(result1.getDouble("PERSON_ACCOUNT_AMT",
//                    0))); //�����˻����
            exportData.setData("GRZHHYE", "TEXT",""); //�����˻����

            //ҩƷ��
            exportData.setData("YPF01", "TEXT",
                               df.format(result1.getDouble("PHA_AMT", 0))); //�������
            exportData.setData("YPF02", "TEXT",
                               df.format(result1.getDouble("PHA_OWN_AMT", 0))); //�Է�
            exportData.setData("YPF03", "TEXT",
                               df.format(result1.getDouble("PHA_ADD_AMT", 0))); //����
            exportData.setData("YPF04", "TEXT",
                               df.format(result1.getDouble("PHA_NHI_AMT", 0))); //�걨���
            exportData.setData("YPF05", "TEXT", " "); //�ܸ�
            exportData.setData("YPF06", "TEXT",
                               df.format(result1.getDouble("YSH", 0))); //��˽��

            //����
            exportData.setData("JCF01", "TEXT",
                               df.format(result1.getDouble("EXM_AMT", 0))); //�������
            exportData.setData("JCF02", "TEXT",
                               df.format(result1.getDouble("EXM_OWN_AMT", 0))); //�Է�
            exportData.setData("JCF03", "TEXT",
                               df.format(result1.getDouble("EXM_ADD_AMT", 0))); //����
            exportData.setData("JCF04", "TEXT",
                               df.format(result1.getDouble("EXM_NHI_AMT", 0))); //�걨���
            exportData.setData("JCF05", "TEXT", " "); //�ܸ�
            exportData.setData("JCF06", "TEXT",
                               df.format(result1.getDouble("JSH", 0))); //��˽��

            //���Ʒ�
            exportData.setData("ZLF01", "TEXT",
                               df.format(result1.getDouble("TREAT_AMT", 0))); //�������
            exportData.setData("ZLF02", "TEXT",
                               df.format(result1.getDouble("TREAT_OWN_AMT", 0))); //�Է�
            exportData.setData("ZLF03", "TEXT",
                               df.format(result1.getDouble("TREAT_ADD_AMT", 0))); //����
            exportData.setData("ZLF04", "TEXT",
                               df.format(result1.getDouble("TREAT_NHI_AMT", 0))); //�걨���
            exportData.setData("ZLF05", "TEXT", " "); //�ܸ�
            exportData.setData("ZLF06", "TEXT",
                               df.format(result1.getDouble("ZHSH", 0))); //��˽��

            //������
            exportData.setData("SSF01", "TEXT",
                               df.format(result1.getDouble("OP_AMT", 0))); //�������
            exportData.setData("SSF02", "TEXT",
                               df.format(result1.getDouble("OP_OWN_AMT", 0))); //�Է�
            exportData.setData("SSF03", "TEXT",
                               df.format(result1.getDouble("OP_ADD_AMT", 0))); //����
            exportData.setData("SSF04", "TEXT",
                               df.format(result1.getDouble("OP_NHI_AMT", 0))); //�걨���
            exportData.setData("SSF05", "TEXT", " "); //�ܸ�
            exportData.setData("SSF06", "TEXT",
                               df.format(result1.getDouble("SHSHEN", 0))); //��˽��

            //��λ��
            exportData.setData("CWF01", "TEXT",
                               df.format(result1.getDouble("BED_AMT", 0))); //�������
            exportData.setData("CWF02", "TEXT",
                               df.format(result1.getDouble("BED_OWN_AMT", 0))); //�Է�
            exportData.setData("CWF03", "TEXT",
                               df.format(result1.getDouble("BED_ADD_AMT", 0))); //����
            exportData.setData("CWF04", "TEXT",
                               df.format(result1.getDouble("BED_NHI_AMT", 0))); //�걨���
            exportData.setData("CWF05", "TEXT", " "); //�ܸ�
            exportData.setData("CWF06", "TEXT",
                               df.format(result1.getDouble("CHSHEN", 0))); //��˽��

            //ҽ�ò���
            exportData.setData("YYCL01", "TEXT",
                               df.format(result1.getDouble("MATERIAL_AMT", 0))); //�������
            exportData.setData("YYCL02", "TEXT",
                               df.format(result1.getDouble("MATERIAL_OWN_AMT",
                    0))); //�Է�
            exportData.setData("YYCL03", "TEXT",
                               df.format(result1.getDouble("MATERIAL_ADD_AMT",
                    0))); //����
            exportData.setData("YYCL04", "TEXT",
                               df.format(result1.getDouble("MATERIAL_NHI_AMT",
                    0))); //�걨���
            exportData.setData("YYCL05", "TEXT", " "); //�ܸ�
            exportData.setData("YYCL06", "TEXT",
                               df.format(result1.getDouble("YSHEN", 0))); //��˽��

            //��ȫѪ
            exportData.setData("SQX01", "TEXT",
                               df.format(result1.getDouble("BLOODALL_AMT", 0))); //�������
            exportData.setData("SQX02", "TEXT",
                               df.format(result1.getDouble("BLOODALL_OWN_AMT",
                    0))); //�Է�
            exportData.setData("SQX03", "TEXT",
                               df.format(result1.getDouble("BLOODALL_ADD_AMT",
                    0))); //����
            exportData.setData("SQX04", "TEXT",
                               df.format(result1.getDouble("BLOODALL_NHI_AMT",
                    0))); //�걨���
            exportData.setData("SQX05", "TEXT", " "); //�ܸ�
            exportData.setData("SQX06", "TEXT",
                               df.format(result1.getDouble("SHUSHEN", 0))); //��˽��

            //�ɷ���Ѫ
            exportData.setData("CFSX01", "TEXT",
                               df.format(result1.getDouble("BLOOD_AMT", 0))); //�������
            exportData.setData("CFSX02", "TEXT",
                               df.format(result1.getDouble("BLOOD_OWN_AMT", 0))); //�Է�
            exportData.setData("CFSX03", "TEXT",
                               df.format(result1.getDouble("BLOOD_ADD_AMT", 0))); //����
            exportData.setData("CFSX04", "TEXT",
                               df.format(result1.getDouble("BLOOD_NHI_AMT", 0))); //�걨���
            exportData.setData("CFSX05", "TEXT", " "); //�ܸ�
            exportData.setData("CFSX06", "TEXT",
                               df.format(result1.getDouble("CHENSH", 0))); //��˽��

            //����
            exportData.setData("QT01", "TEXT",
                               df.format(result1.getDouble("OTHER_AMT", 0))); //�������
            exportData.setData("QT2", "TEXT",
                               df.format(result1.getDouble("OTHER_OWN_AMT", 0))); //�Է�
            exportData.setData("QT3", "TEXT",
                               df.format(result1.getDouble("OTHER_ADD_AMT", 0))); //����
            exportData.setData("QT4", "TEXT",
                               df.format(result1.getDouble("OTHER_NHI_AMT", 0))); //�걨���
            exportData.setData("QT5", "TEXT", " "); //�ܸ�
            exportData.setData("QT6", "TEXT",
                               df.format(result1.getDouble("QSHEN", 0))); //��˽��

            //����һ���Բ���
            exportData.setData("QZH01", "TEXT",
                               df.format(result1.getDouble("CAIF", 0))); //�������
            exportData.setData("QZH02", "TEXT",
                               df.format(result1.getDouble("CAIZ", 0))); //�Է�
            exportData.setData("QZH03", "TEXT",
                               df.format(result1.getDouble("CAIZENG", 0))); //����
            exportData.setData("QZH04", "TEXT",
                               df.format(result1.getDouble("CAISHEN", 0))); //�걨���
            exportData.setData("QZH05", "TEXT", " "); //�ܸ�
            exportData.setData("QZH06", "TEXT",
                               df.format(result1.getDouble("CAISH", 0))); //��˽��

            //�ϼ�
            exportData.setData("HJ01", "TEXT",
                               df.format(result1.getDouble("HJF", 0))); //�������
            exportData.setData("HJ02", "TEXT",
                               df.format(result1.getDouble("HEZ", 0))); //�Է�
            exportData.setData("HJ03", "TEXT",
                               df.format(result1.getDouble("HEZENG", 0))); //����
            exportData.setData("HJ04", "TEXT",
                               df.format(result1.getDouble("HESHEN", 0))); //�걨���
            exportData.setData("HJ05", "TEXT", " "); //�ܸ�
            exportData.setData("HJ06", "TEXT",
                               df.format(result1.getDouble("HESH", 0))); //��˽��

            exportData.setData("GRSSQFBZ", "TEXT",
                               df.format(result1.getDouble("RESTART_STANDARD_AMT",
                    0))); //ʵ����֧����׼���
            exportData.setData("GRZF", "TEXT",
                               df.format(result1.getDouble("OWN_AMT", 0))); //�Էѽ��
            exportData.setData("GRZFJE", "TEXT",
                               df.format(result1.getDouble("ADD_AMT", 0))); //�������
            exportData.setData("GRHJ", "TEXT",
                               df.format(result1.getDouble("GRRHJ", 0))); //�ϼ�

            exportData.setData("GRCQFBZZFBL", "TEXT",
                               df.format(result1.
                                         getDouble("STARTPAY_OWN_AMT", 0))); //���𸶱�׼�Ը��������
            exportData.setData("GRYLJZZFBL", "TEXT",
                               df.format(result1.
                                         getDouble("PERCOPAYMENT_RATE_AMT", 0))); //ҽ�ƾ����Ը��������
            exportData.setData("GRZGXE", "TEXT",
                               df.format(result1.getDouble("INS_HIGHLIMIT_AMT",
                    0))); //����޶����Ͻ��
            exportData.setData("GRSJZFJE", "TEXT",
                               df.format(result1.getDouble("SJZFJE", 0))); //ʵ���Ը����

            //ҽ������
            exportData.setData("YBJJYLJG", "TEXT",
                               df.format(result1.getDouble("NHI_PAY", 0))); //ҽ�ƻ���������
            exportData.setData("YBJJYLJG2", "TEXT",
                               StringUtil.getInstance().
                               numberToWord(result1.getDouble("NHI_PAY", 0))); //����
            exportData.setData("YBJJSHBZHF", "TEXT",
                               df.format(result1.getDouble("NHI_PAY_REAL", 0))); //�籣֧�����
            exportData.setData("YBJJSHBZHF2", "TEXT",
                               StringUtil.getInstance().
                               numberToWord(result1.getDouble("NHI_PAY_REAL", 0))); //����

            //������
            exportData.setData("DEJZYLJG", "TEXT",
                               df.format(result1.getDouble("NHI_COMMENT", 0))); //ҽ�ƻ���������
            exportData.setData("DEJZYLJG2", "TEXT",
            		           StringUtil.getInstance().
            		numberToWord(result1.getDouble("NHI_COMMENT", 0))); //����
            exportData.setData("DEJZSHBJY", "TEXT",
                               df.format(result1.getDouble("NHI_COMMENT", 0))); //�籣����֧�����
            exportData.setData("DEJZSHBJY2", "TEXT",
                               StringUtil.getInstance().
                               numberToWord(result1.
                                            getDouble("NHI_COMMENT", 0))); //����
            //������ʾ
            String  spPatType = result1.getValue("SPECIAL_PAT",0);
            double  agentAmt = result1.getDouble("ARMYAI_AMT",0);
            //����Ա����
            if("06".equals(spPatType))
            {
            exportData.setData("GWYYLJG", "TEXT",df.format(agentAmt)); //ҽ�ƻ���������
            exportData.setData("GWYYLJG2", "TEXT",StringUtil.getInstance().numberToWord(agentAmt)); //����
            exportData.setData("GWYSHBZF", "TEXT",df.format(agentAmt)); //�籣֧�����
            exportData.setData("GWYSHBZF2", "TEXT",StringUtil.getInstance().numberToWord(agentAmt)); //����
            }
            if("04".equals(spPatType))
            {
            //���в���
            exportData.setData("JCBZHYLJG", "TEXT",df.format(agentAmt)); //ҽ�ƻ���������
            exportData.setData("JCBZHYLJG2", "TEXT",StringUtil.getInstance().numberToWord(agentAmt)); //����
            exportData.setData("JCBZHSHBZHF", "TEXT",df.format(agentAmt)); //�籣֧�����
            exportData.setData("JCBZHSHBZHF2", "TEXT",StringUtil.getInstance().numberToWord(agentAmt)); //����
            }
            if("07".equals(spPatType)||"08".equals(spPatType))
            {
            //��������-----------
            exportData.setData("MZHBZHYLJG", "TEXT", df.format(agentAmt)); //ҽ�ƻ���������      
            exportData.setData("MZHBZHYLJG2", "TEXT", StringUtil.getInstance().numberToWord(agentAmt)); //����
            exportData.setData("MZHBZHSHBZHF", "TEXT", df.format(agentAmt)); //�籣֧�����
            exportData.setData("MZHBZHSHBZHF2", "TEXT", StringUtil.getInstance().numberToWord(agentAmt)); //����
            }
            if("09".equals(spPatType))
            {
            //�ǵ䲹��------------
            exportData.setData("FDBZHYLJG", "TEXT", df.format(agentAmt)); //ҽ�ƻ���������
            exportData.setData("FDBZHYLJG2", "TEXT", StringUtil.getInstance().numberToWord(agentAmt)); //����
            exportData.setData("FDBZHSHBZHF", "TEXT", df.format(agentAmt)); //�籣֧�����
            exportData.setData("FDBZHSHBZHF2", "TEXT", StringUtil.getInstance().numberToWord(agentAmt)); //����
            }
            //�����˻�֧��
            exportData.setData("GRZHHYLJG", "TEXT",
                               df.format(result1.getDouble("ACCOUNT_PAY_AMT", 0))); //ҽ�ƻ���������
            exportData.setData("GRZHHYLJG2", "TEXT",
                               StringUtil.getInstance().
                               numberToWord(result1.
                                            getDouble("ACCOUNT_PAY_AMT", 0))); //����
            exportData.setData("GRZHHSHBZHF", "TEXT",
                               df.format(result1.getDouble("GEZHI", 0))); //�籣֧�����
            exportData.setData("GRZHHSHBZHF2", "TEXT",
                               StringUtil.getInstance().
                               numberToWord(result1.getDouble("GEZHI", 0))); //����
            //TODO:���˻�֧����ϼ�֧����ͬ��������
            //caowl 20130321 �ܼƵĴ�Сд�Բ���
            exportData.setData("HJZHFYLJG", "TEXT",
                               df.format(result1.getDouble("YLJGSHQHJ", 0))); //ҽ�ƻ���������
            exportData.setData("HJZHFYLJG2", "TEXT",
            		StringUtil.getInstance().
                    numberToWord(result1.getDouble("YLJGSHQHJ", 0))); //����  //caowl 20130321 �ܼƵĴ�Сд�Բ���
            exportData.setData("HJZHFSHBZHF", "TEXT",
                               df.format(result1.getDouble("SHBZHFJEHJ", 0))); //�籣֧�����

            exportData.setData("HJZHFSHBZHF2", "TEXT",
            		 StringUtil.getInstance().
                     numberToWord(result1.getDouble("SHBZHFJEHJ", 0))); //����  //caowl 20130321 �ܼƵĴ�Сд�Բ���
            this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INS_PRINT3.jhw",
                                 exportData);
        } 
        //�����ֲ���.
        else if ("DBZBR".equals(dBZ)) 
        {  
        	
            // �ϲ���ʼ
            String sql = " SELECT " +
                         " case " +
                         " when  substr(ctz_code,0,1) = '1' then '��ְ' " +
                         " when  substr(ctz_code,0,1) = '2' then '����' " +
                         " end as XZ," +
                         " SUBSTR(M.YEAR_MON,1,4)||'��'||SUBSTR(M.YEAR_MON,5,2)||'��' AS YDATE, B.CHN_DESC AS CBQX,M.DEPT_DESC, " +
                         " V.CHN_DESC AS JYLB ,"+
                         " M.IDNO , M.PAT_NAME , X.CHN_DESC AS SEX_DESC , M.PAT_AGE , Z.CTZ_DESC , " +
                         " M.UNIT_DESC , M.UNIT_CODE, " +
                         " U.CHN_DESC AS RYBS ,"+
                         " to_char(M.IN_DATE,'yyyymmdd') AS IN_DATE,to_char(M.DS_DATE,'yyyymmdd') AS DS_DATE,I.ADM_DAYS, M.MR_NO , " +
                         " M.BED_NO , " +
                         " '����' AS JB, " +
                         //�൱�𸶱�׼�����ֱ��룬�ʸ�ȷ����ţ�סԺ����
                         " M.START_STANDARD_AMT , A.SDISEASE_CODE ,M.CONFIRM_NO, M.INP_TIME , " +
                         //ҽ�ƾ���֧������
                         " M.PERCOPAYMENT_RATE_AMT , " + 
                         //ת��ҽԺ����
                         " M.TRANHOSP_DESC , " +
                         //ת��ҽԺ����
                         " case " +
                         " when  M.TRAN_CLASS = '01' then 'һ��' " +
                         " when  M.TRAN_CLASS = '02' then '����' " +
                         " when  M.TRAN_CLASS = '03' then '����' " +
                         " else '' " +
                         " end AS TRANS_HOSP_JB, " +
                         //ת��ҽԺ�𸶱�׼
                         " M.TRANHOSP_RESTANDARD_AMT , " +
                         //��Ժ���||ת���
                         " M.DIAG_CODE||M.DIAG_DESC||' '||" +
                         " (case " +
                         " when  M.TRAN_CLASS = '1' then '����'" +
                         " when  M.TRAN_CLASS = '2' then '��ת'" +
                         " when  M.TRAN_CLASS = '3' then 'δ��'" +
                         " when  M.TRAN_CLASS = '4' then '����'" +
                         " when  M.TRAN_CLASS = '5' then '����'" +
                         " else ''" +
                         " end) AS OUT_DIAG," +
                         //�Ҵ���ϣ����ָ��ѱ�׼�������Ը���׼
                         " ' ' AS HOME_DIAG, M.SINGLE_STANDARD_AMT_T ,STARTPAY_OWN_AMT ," +
                         //�ϴ�ͳ�����֧���޶�ʣ���Σ��ϴ�ҽ�ƾ���֧���޶�ʣ���
                         " M.BASEMED_BALANCE , M.INS_BALANCE , " +
                         //�����걨���
                         "M.PHA_AMT+M.EXM_AMT+M.TREAT_AMT+M.OP_AMT+M.BED_AMT+M.MATERIAL_AMT+M.BLOODALL_AMT+M.BLOOD_AMT+M.OTHER_AMT - case when M.MATERIAL_SINGLE_AMT is null then 0 else M.MATERIAL_SINGLE_AMT end-case when M.BED_SINGLE_AMT is null then 0 else M.BED_SINGLE_AMT end AS DISEASE_NHI_AMT, " +
                         //���־ܸ���ҽԺ�����ֱ�׼�Ը�������ҽ�Ʊ��ղ�����
                         "REFUSE_TOTAL_AMT, M.SINGLE_STANDARD_OWN_AMT ,  SINGLE_SUPPLYING_AMT,  " +
                         // �²���ʼ   
                         //ʵ���𸶱�׼��ͳ������Ը���׼���
                         " '0.00' AS REAL_PAY_STANDER_AMT , M.STARTPAY_OWN_AMT AS NHI_OWN_PAY , " + 
                         //ҽ�ƾ����Ը���׼�������Էѽ�����޶����Ͻ��
                         " M.PERCOPAYMENT_RATE_AMT AS APPLY_OWN_AMT ,case when M.BED_SINGLE_AMT is null then 0 else M.BED_SINGLE_AMT end+case when M.MATERIAL_SINGLE_AMT is null then 0 else M.MATERIAL_SINGLE_AMT end AS TX_OWN_AMT, M.INS_HIGHLIMIT_AMT , " +
                         //ʵ���Ը�
					     " case when M.STARTPAY_OWN_AMT is null then 0 else M.STARTPAY_OWN_AMT end "+
					     " + "+
					     " case when M.BED_SINGLE_AMT is null then 0 else M.BED_SINGLE_AMT end "+
					     " + "+
					     " case when M.MATERIAL_SINGLE_AMT is null then 0 else M.MATERIAL_SINGLE_AMT end "+
					     " + "+
					     " case when M.PERCOPAYMENT_RATE_AMT is null then 0 else M.PERCOPAYMENT_RATE_AMT end "+
					     " + "+
					     " case when M.INS_HIGHLIMIT_AMT is null then 0 else M.INS_HIGHLIMIT_AMT end "+
					     " - "+
					     " case when M.ACCOUNT_PAY_AMT is null then 0 else M.ACCOUNT_PAY_AMT end "+
					     " - "+
					     " case when M.ARMYAI_AMT is null then 0 else M.ARMYAI_AMT end  AS REAL_TOTAL_OWN_AMT,"+
					     //�����Ը��ϼ�   
                         " M.STARTPAY_OWN_AMT+case when M.BED_SINGLE_AMT is null then 0 else M.BED_SINGLE_AMT end+case when M.MATERIAL_SINGLE_AMT is null then 0 else M.MATERIAL_SINGLE_AMT end+M.PERCOPAYMENT_RATE_AMT+M.INS_HIGHLIMIT_AMT AS TOTAL_OWN_AMT," +
                         //ͳ��������������������������˻�������
                         " M.NHI_PAY ,M.HOSP_APPLY_AMT, M.ACCOUNT_PAY_AMT  ," + 
                         //ͳ�����֧����������֧�����
                         " M.NHI_PAY_REAL ,M.HOSP_APPLY_AMT," + 
                         // �м䲿�֣��������ܸ�����˽��
                         " M.PHA_AMT , M.PHA_OWN_AMT ,'0.00' AS PHA_OWN_AMT_S, M.PHA_ADD_AMT , M.PHA_NHI_AMT , '' AS PHA_SH_AMT, " +
                         " M.EXM_AMT , M.EXM_OWN_AMT ,'0.00' AS EXM_OWN_AMT_S, M.EXM_ADD_AMT , M.EXM_NHI_AMT , '' AS EXM_SH_AMT, " +
                         " M.TREAT_AMT , M.TREAT_OWN_AMT ,'0.00' AS TREAT_OWN_AMT_S, M.TREAT_ADD_AMT , M.TREAT_NHI_AMT , '' AS TREAT_SH_AMT , " +
                         " M.OP_AMT , M.OP_OWN_AMT , '0.00' AS OP_OWN_AMT_S,M.OP_ADD_AMT , M.OP_NHI_AMT , '' AS OP_SH_AMT, " +
                         " M.BED_AMT , M.BED_OWN_AMT ,case when M.BED_SINGLE_AMT is null then 0 else M.BED_SINGLE_AMT end AS BED_OWN_AMT_S, M.BED_ADD_AMT , M.BED_NHI_AMT , '' AS BED_SH_AMT, " +
                         " M.MATERIAL_AMT , M.MATERIAL_OWN_AMT , case when M.MATERIAL_SINGLE_AMT is null then 0 else M.MATERIAL_SINGLE_AMT end AS MATERIAL_OWN_AMT_S, M.MATERIAL_ADD_AMT , M.MATERIAL_NHI_AMT , '' AS MATERIAL_SH_AMT , " +
                         " M.BLOODALL_AMT , M.BLOODALL_OWN_AMT , '0.00' AS BLOODALL_OWN_AMT_S,M.BLOODALL_ADD_AMT , M.BLOODALL_NHI_AMT , '' AS BLOODALL_SH_AMT, " +
                         " M.BLOOD_AMT , M.BLOOD_OWN_AMT ,'0.00' AS BLOOD_OWN_AMT_S, M.BLOOD_ADD_AMT, M.BLOOD_NHI_AMT , '' AS BLOOD_SH_AMT , " +
                         " M.OTHER_AMT , M.OTHER_OWN_AMT , '0.00' AS OTHER_OWN_AMT_S,M.OTHER_ADD_AMT , M.OTHER_NHI_AMT , '' AS OTHER_SH_AMT, " +
                         // �����У����ϡ���Ŀ��֪��дʲô
                         " '' AS YCXCL_AMT, '' AS YCXCL_OWN_AMT, '' AS  YCXCL_OWN_AMT_S, '' AS YCXCL_ADD_AMT, '' AS YCXCL_NHI_AMT, '' AS YCXCL_SH_AMT, " +
                         " M.PHA_AMT+M.EXM_AMT+M.TREAT_AMT+M.OP_AMT+M.BED_AMT+M.MATERIAL_AMT+M.BLOODALL_AMT+M.BLOOD_AMT+M.OTHER_AMT AS TOT_AMT, " +
                         " M.PHA_OWN_AMT+M.EXM_OWN_AMT+M.TREAT_OWN_AMT+M.OP_OWN_AMT+M.BED_OWN_AMT+M.MATERIAL_OWN_AMT+M.BLOODALL_OWN_AMT+M.BLOOD_OWN_AMT+M.OTHER_OWN_AMT AS TOT_OWN_AMT, " +
                         " case when M.MATERIAL_SINGLE_AMT is null then 0 else M.MATERIAL_SINGLE_AMT end+case when M.BED_SINGLE_AMT is null then 0 else M.BED_SINGLE_AMT end AS TOT_OWN_AMT_S, " +
                         " M.PHA_ADD_AMT+M.EXM_ADD_AMT+M.TREAT_ADD_AMT+M.OP_ADD_AMT+M.BED_ADD_AMT+M.MATERIAL_ADD_AMT+M.BLOODALL_ADD_AMT+M.BLOOD_ADD_AMT+M.OTHER_ADD_AMT AS TOT_ADD_AMT, " +
                         " M.PHA_NHI_AMT+M.EXM_NHI_AMT+M.TREAT_NHI_AMT+M.OP_NHI_AMT+M.BED_NHI_AMT+M.MATERIAL_NHI_AMT+M.BLOODALL_NHI_AMT+M.BLOOD_NHI_AMT+M.OTHER_NHI_AMT AS TOT_NHI_AMT, " +
                         " '' AS TOT_SH_AMT, " +
                         
                         " M.REGION_CODE , M.ADM_SEQ , M.CONFIRM_NO, A.SPECIAL_PAT, M.ARMYAI_AMT,O.USER_NAME,M.IN_DATE AS IN_DATETEMP ,M.DS_DATE AS DS_DATETEMP " +
                         " FROM INS_IBS M " +
                         " left JOIN INS_ADM_CONFIRM A " +
                         " ON M.CONFIRM_NO = A.CONFIRM_NO " +
                         " left JOIN ADM_INP I " +
                         " ON M.REGION_CODE = I.REGION_CODE " +
                         " AND M.MR_NO = I.MR_NO " +
                         " AND M.CASE_NO = I.CASE_NO " +
                         " left JOIN SYS_OPERATOR O " +
                         " ON I.REGION_CODE = O.REGION_CODE " +
                         " AND I.VS_DR_CODE = O.USER_ID " +
                         " left JOIN SYS_DICTIONARY X " +
                         " ON M.SEX_CODE = X.ID AND X.GROUP_ID='SYS_SEX' " +
                         " INNER JOIN SYS_CTZ Z " +
                         " ON A.HIS_CTZ_CODE = Z.CTZ_CODE " +//modify by lim 2012/04/26
                         " left JOIN SYS_DICTIONARY B " +
                         " ON M.INSBRANCH_CODE = B.ID AND B.GROUP_ID='INS_FZX' " +
                         " left JOIN INS_MT_DISEASE T " +
                         " ON M.SPEDRS_CODE = T.MT_DISEASE_CODE " +
                         " left JOIN SYS_DICTIONARY K "+
                         " ON K.GROUP_ID='SIN_DISEASE' AND A.SDISEASE_CODE = K.ID "+
                         " left JOIN SYS_DICTIONARY U "+
                         " ON U.GROUP_ID='SP_PRESON_TYPE' AND  U.ID = A.SPECIAL_PAT "+
                         " left JOIN SYS_DICTIONARY V "+
                         " ON V.GROUP_ID = 'INS_JYLB' AND V.ID = M.ADM_CATEGORY "+
                         " WHERE M.MR_NO = '" + mrNo + "' AND M.ADM_SEQ='"+admSeq+"'" ;
//                         " WHERE M.MR_NO = '" + mrNo + "' " +
//                         " AND TO_CHAR(M.DS_DATE,'yyyy-mm-dd') = '" +
//                         dsDateArray[0] + "' ";
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
           //System.out.println("������sql��"+sql);
            //System.out.println("result1:"+result);
            if (result.getCount() <= 0) {
                messageBox("û�д�ӡ����!");
                return;
            }
            TParm exportData  =  new TParm();
            DecimalFormat df = new DecimalFormat("##########0.00");
            //���
            exportData.setData("BH", "TEXT", "���籣ҽ֧��2-3��"); 
            //ҽ�ƻ�������
            exportData.setData("YLJGMCH", "TEXT", "�����̩�������Ѫ�ܲ�ҽԺ");
            //��ҽ���
            exportData.setData("JYLB", "TEXT", result.getValue("JYLB", 0));   
            //����
            exportData.setData("YDATE", "TEXT", result.getValue("YDATE", 0)); 
            //��λ
            exportData.setData("DW", "TEXT", "Ԫ"); 
            //����
            exportData.setData("XZ", "TEXT", result.getValue("XZ", 0)); 
            //�������֤����
            exportData.setData("IDNO", "TEXT", result.getValue("IDNO", 0)); 
            //����
            exportData.setData("PAT_NAME", "TEXT", result.getValue("PAT_NAME", 0)); 
            //�Ա�
            exportData.setData("SEX_DESC", "TEXT", result.getValue("SEX_DESC", 0)); 
            //����
            exportData.setData("PAT_AGE", "TEXT", result.getValue("PAT_AGE", 0)); 
            //��Ա���
            exportData.setData("CTZ_DESC", "TEXT", result.getValue("CTZ_DESC", 0)); 
            //��λ����
            exportData.setData("UNIT_DESC", "TEXT", result.getValue("UNIT_DESC", 0)); 
            //��λ����
            exportData.setData("UNIT_CODE", "TEXT", result.getValue("UNIT_CODE", 0)); 
            //��Ա��ʶ
            exportData.setData("RYBS", "TEXT", result.getValue("RYBS", 0)); 
            //�α�����
            exportData.setData("CBQX", "TEXT", result.getValue("CBQX", 0)); 
            //סԺ����
            String inDate = result.getValue("IN_DATE", 0);
            String outDate = result.getValue("DS_DATE", 0);
            Timestamp outDatetemp=result.getTimestamp("DS_DATETEMP", 0);
            Timestamp inDatetemp=result.getTimestamp("IN_DATETEMP", 0);
            if(inDate!=null && !"".equals(inDate))
                exportData.setData("IN_DATE", "TEXT",
                		(inDate.substring(0, 4) + "��" +
                         inDate.substring(4, 6) + "��" +
                         inDate.substring(6, 8) + "��"));
            else
            	exportData.setData("IN_DATE", "TEXT","");
            //��Ժ����
            if(outDate!=null && !"".equals(outDate))
                exportData.setData("DS_DATE", "TEXT",
                        (outDate.substring(0, 4) + "��" +
                         outDate.substring(4, 6) + "��" +
                         outDate.substring(6, 8) + "��")); 
            else
            	exportData.setData("DS_DATE", "TEXT","");
            int rollDate =0;
            if(inDate.equals(outDate))
            	rollDate =1;
            else
                rollDate = StringTool.getDateDiffer(outDatetemp, inDatetemp);
            //סԺ����
            exportData.setData("ADM_DAYS", "TEXT", rollDate); 
            //סԺ��
            exportData.setData("MR_NO", "TEXT", result.getValue("MR_NO", 0));
            //��λ��
            exportData.setData("BED_NO", "TEXT", result.getValue("BED_NO", 0)); 
            //����
            exportData.setData("JB", "TEXT", result.getValue("JB", 0)); 
            //�Ʊ�
            exportData.setData("DEPT_DESC", "TEXT", result.getValue("DEPT_DESC", 0)); 
            //�൱�𸶱�׼
            exportData.setData("START_STANDARD_AMT", "TEXT",result.getValue("START_STANDARD_AMT", 0));
            //���ֱ���
            exportData.setData("SDISEASE_CODE", "TEXT",result.getValue("SDISEASE_CODE", 0));
            //�ʸ�ȷ�����
            exportData.setData("CONFIRM_NO", "TEXT",result.getValue("CONFIRM_NO", 0));
            //�ڼ���סԺ
            exportData.setData("INP_TIME", "TEXT", result.getValue("INP_TIME", 0));  
            //ҽ�ƾ���֧������
            exportData.setData("PERCOPAYMENT_RATE_AMT", "TEXT", result.getValue("PERCOPAYMENT_RATE_AMT", 0));
            //ת��Ժ����
            exportData.setData("TRANHOSP_DESC", "TEXT",result.getValue("TRANHOSP_DESC", 0));
            //ת��Ժ����
            exportData.setData("TRANS_HOSP_JB", "TEXT", result.getValue("TRANS_HOSP_JB", 0)); 
            //ת��Ժʵ���𸶱�׼
            exportData.setData("TRANHOSP_RESTANDARD_AMT", "TEXT",result.getValue("TRANHOSP_RESTANDARD_AMT", 0));
            //��Ժ���
            exportData.setData("OUT_DIAG", "TEXT", result.getValue("OUT_DIAG", 0));
            //�Ҵ����
            exportData.setData("HOME_DIAG", "TEXT", result.getValue("HOME_DIAG", 0)); 
            //���ָ��ѱ�׼
            exportData.setData("SINGLE_STANDARD_AMT_T", "TEXT", df.format(result.getDouble("SINGLE_STANDARD_AMT_T", 0)));
            //���ָ��ѱ�׼
            exportData.setData("STARTPAY_OWN_AMT", "TEXT", df.format(result.getDouble("STARTPAY_OWN_AMT", 0)));
            //�ϴ�ҽ������֧���޶�ʣ���
            exportData.setData("BASEMED_BALANCE", "TEXT",df.format(result.getDouble("BASEMED_BALANCE", 0)));
            //�ϴ�ҽ�ƾ���֧���޶�ʣ���
            exportData.setData("INS_BALANCE", "TEXT",df.format(result.getDouble("INS_BALANCE", 0)));
            //ҩƷ��
            exportData.setData("PHA_AMT", "TEXT",df.format(result.getDouble("PHA_AMT", 0))); 
            exportData.setData("PHA_OWN_AMT", "TEXT",df.format(result.getDouble("PHA_OWN_AMT", 0)));
            exportData.setData("PHA_OWN_AMT_S", "TEXT",df.format(result.getDouble("PHA_OWN_AMT_S", 0)));
            exportData.setData("PHA_ADD_AMT", "TEXT",df.format(result.getDouble("PHA_ADD_AMT", 0)));
            exportData.setData("PHA_NHI_AMT", "TEXT",df.format(result.getDouble("PHA_NHI_AMT", 0))); 
            exportData.setData("PHA_REF_AMT", "TEXT","0.00"); 
            exportData.setData("PHA_SH_AMT", "TEXT","0.00"); 
            //����
            exportData.setData("EXM_AMT", "TEXT",df.format(result.getDouble("EXM_AMT", 0))); 
            exportData.setData("EXM_OWN_AMT", "TEXT",df.format(result.getDouble("EXM_OWN_AMT", 0)));
            exportData.setData("EXM_OWN_AMT_S", "TEXT",df.format(result.getDouble("EXM_OWN_AMT_S", 0)));
            exportData.setData("EXM_ADD_AMT", "TEXT",df.format(result.getDouble("EXM_ADD_AMT", 0)));
            exportData.setData("EXM_NHI_AMT", "TEXT",df.format(result.getDouble("EXM_NHI_AMT", 0))); 
            exportData.setData("EXM_REF_AMT", "TEXT","0.00"); 
            exportData.setData("EXM_SH_AMT", "TEXT","0.00"); 
            //���Ʒ�
            exportData.setData("TREAT_AMT", "TEXT",df.format(result.getDouble("TREAT_AMT", 0))); 
            exportData.setData("TREAT_OWN_AMT", "TEXT",df.format(result.getDouble("TREAT_OWN_AMT", 0)));
            exportData.setData("TREAT_OWN_AMT_S", "TEXT",df.format(result.getDouble("TREAT_OWN_AMT_S", 0)));
            exportData.setData("TREAT_ADD_AMT", "TEXT",df.format(result.getDouble("TREAT_ADD_AMT", 0)));
            exportData.setData("TREAT_NHI_AMT", "TEXT",df.format(result.getDouble("TREAT_NHI_AMT", 0))); 
            exportData.setData("TREAT_REF_AMT", "TEXT","0.00"); 
            exportData.setData("TREAT_SH_AMT", "TEXT","0.00");
            //������
            exportData.setData("OP_AMT", "TEXT",df.format(result.getDouble("OP_AMT", 0))); 
            exportData.setData("OP_OWN_AMT", "TEXT",df.format(result.getDouble("OP_OWN_AMT", 0)));
            exportData.setData("OP_OWN_AMT_S", "TEXT",df.format(result.getDouble("OP_OWN_AMT_S", 0)));
            exportData.setData("OP_ADD_AMT", "TEXT",df.format(result.getDouble("OP_ADD_AMT", 0)));
            exportData.setData("OP_NHI_AMT", "TEXT",df.format(result.getDouble("OP_NHI_AMT", 0))); 
            exportData.setData("OP_REF_AMT", "TEXT","0.00"); 
            exportData.setData("OP_SH_AMT", "TEXT","0.00");
            //��λ��
            exportData.setData("BED_AMT", "TEXT",df.format(result.getDouble("BED_AMT", 0))); 
            exportData.setData("BED_OWN_AMT", "TEXT",df.format(result.getDouble("BED_OWN_AMT", 0)));
            exportData.setData("BED_OWN_AMT_S", "TEXT",df.format(result.getDouble("BED_OWN_AMT_S", 0)));
            exportData.setData("BED_ADD_AMT", "TEXT",df.format(result.getDouble("BED_ADD_AMT", 0)));
            exportData.setData("BED_NHI_AMT", "TEXT",df.format(result.getDouble("BED_NHI_AMT", 0))); 
            exportData.setData("BED_REF_AMT", "TEXT","0.00"); 
            exportData.setData("BED_SH_AMT", "TEXT","0.00");            
            //ҽ�ò���
            exportData.setData("MATERIAL_AMT", "TEXT",df.format(result.getDouble("MATERIAL_AMT", 0))); 
            exportData.setData("MATERIAL_OWN_AMT", "TEXT",df.format(result.getDouble("MATERIAL_OWN_AMT", 0)));
            exportData.setData("MATERIAL_OWN_AMT_S", "TEXT",df.format(result.getDouble("MATERIAL_OWN_AMT_S", 0)));
            exportData.setData("MATERIAL_ADD_AMT", "TEXT",df.format(result.getDouble("MATERIAL_ADD_AMT", 0)));
            exportData.setData("MATERIAL_NHI_AMT", "TEXT",df.format(result.getDouble("MATERIAL_NHI_AMT", 0))); 
            exportData.setData("MATERIAL_REF_AMT", "TEXT","0.00"); 
            exportData.setData("MATERIAL_SH_AMT", "TEXT","0.00");               
            //��ȫѪ
            exportData.setData("BLOODALL_AMT", "TEXT",df.format(result.getDouble("BLOODALL_AMT", 0))); 
            exportData.setData("BLOODALL_OWN_AMT", "TEXT",df.format(result.getDouble("BLOODALL_OWN_AMT", 0)));
            exportData.setData("BLOODALL_OWN_AMT_S", "TEXT",df.format(result.getDouble("BLOODALL_OWN_AMT_S", 0)));
            exportData.setData("BLOODALL_ADD_AMT", "TEXT",df.format(result.getDouble("BLOODALL_ADD_AMT", 0)));
            exportData.setData("BLOODALL_NHI_AMT", "TEXT",df.format(result.getDouble("BLOODALL_NHI_AMT", 0))); 
            exportData.setData("BLOODALL_REF_AMT", "TEXT","0.00"); 
            exportData.setData("BLOODALL_SH_AMT", "TEXT","0.00"); 
            //�ɷ���Ѫ
            exportData.setData("BLOOD_AMT", "TEXT",df.format(result.getDouble("BLOOD_AMT", 0))); 
            exportData.setData("BLOOD_OWN_AMT", "TEXT",df.format(result.getDouble("BLOOD_OWN_AMT", 0)));
            exportData.setData("BLOOD_OWN_AMT_S", "TEXT",df.format(result.getDouble("BLOOD_OWN_AMT_S", 0)));
            exportData.setData("BLOOD_ADD_AMT", "TEXT",df.format(result.getDouble("BLOOD_ADD_AMT", 0)));
            exportData.setData("BLOOD_NHI_AMT", "TEXT",df.format(result.getDouble("BLOOD_NHI_AMT", 0))); 
            exportData.setData("BLOOD_REF_AMT", "TEXT","0.00"); 
            exportData.setData("BLOOD_SH_AMT", "TEXT","0.00");            
            //����
            exportData.setData("OTHER_AMT", "TEXT",df.format(result.getDouble("OTHER_AMT", 0))); 
            exportData.setData("OTHER_OWN_AMT", "TEXT",df.format(result.getDouble("OTHER_OWN_AMT", 0)));
            exportData.setData("OTHER_OWN_AMT_S", "TEXT",df.format(result.getDouble("OTHER_OWN_AMT_S", 0)));
            exportData.setData("OTHER_ADD_AMT", "TEXT",df.format(result.getDouble("OTHER_ADD_AMT", 0)));
            exportData.setData("OTHER_NHI_AMT", "TEXT",df.format(result.getDouble("OTHER_NHI_AMT", 0))); 
            exportData.setData("OTHER_REF_AMT", "TEXT","0.00"); 
            exportData.setData("OTHER_SH_AMT", "TEXT","0.00");     
            //����һ���Բ���
            exportData.setData("YCXCL_AMT", "TEXT","0.00"); 
            exportData.setData("YCXCL_OWN_AMT", "TEXT","0.00");
            exportData.setData("YCXCL_OWN_AMT_S", "TEXT","0.00");
            exportData.setData("YCXCL_ADD_AMT", "TEXT","0.00");
            exportData.setData("YCXCL_NHI_AMT", "TEXT","0.00"); 
            exportData.setData("YCXCL_REF_AMT", "TEXT","0.00"); 
            exportData.setData("YCXCL_SH_AMT", "TEXT","0.00");  
            //�ϼ�
            exportData.setData("TOT_AMT", "TEXT",df.format(result.getDouble("TOT_AMT", 0))); 
            exportData.setData("TOT_OWN_AMT", "TEXT",df.format(result.getDouble("TOT_OWN_AMT", 0)));
            exportData.setData("TOT_OWN_AMT_S", "TEXT",df.format(result.getDouble("TOT_OWN_AMT_S", 0)));
            exportData.setData("TOT_ADD_AMT", "TEXT",df.format(result.getDouble("TOT_ADD_AMT", 0)));
            exportData.setData("TOT_NHI_AMT", "TEXT",df.format(result.getDouble("TOT_NHI_AMT", 0))); 
            exportData.setData("TOT_REF_AMT", "TEXT","0.00"); 
            exportData.setData("TOT_SH_AMT", "TEXT","0.00");  
            //�����Ը����
            exportData.setData("DISEASE_NHI_AMT", "TEXT",df.format(result.getDouble("DISEASE_NHI_AMT", 0)));
            //���־ܸ����
            exportData.setData("REFUSE_TOTAL_AMT", "TEXT",df.format(result.getDouble("REFUSE_TOTAL_AMT", 0)));
            //ҽԺ�������𸶱�׼�Ը����
            exportData.setData("SINGLE_STANDARD_OWN_AMT", "TEXT",df.format(result.getDouble("SINGLE_STANDARD_OWN_AMT", 0)));
            //����ҽ�Ʊ��ղ�����
            exportData.setData("SINGLE_SUPPLYING_AMT", "TEXT",df.format(result.getDouble("SINGLE_SUPPLYING_AMT", 0)));
            //ʵ���𸶱�׼���
            exportData.setData("REAL_PAY_STANDER_AMT", "TEXT",df.format(result.getDouble("REAL_PAY_STANDER_AMT", 0)));
            //ͳ������Ը���׼���
            exportData.setData("NHI_OWN_PAY", "TEXT",df.format(result.getDouble("NHI_OWN_PAY", 0)));
            //ҽ�ƾ����Ը���׼���
            exportData.setData("APPLY_OWN_AMT", "TEXT",df.format(result.getDouble("APPLY_OWN_AMT", 0)));
            //�����Էѽ��
            exportData.setData("TX_OWN_AMT", "TEXT",df.format(result.getDouble("TX_OWN_AMT", 0)));
            //����޶����Ͻ��
            exportData.setData("INS_HIGHLIMIT_AMT", "TEXT",df.format(result.getDouble("INS_HIGHLIMIT_AMT", 0)));
            //�����Ը��ϼ�
            exportData.setData("TOTAL_OWN_AMT", "TEXT",df.format(result.getDouble("TOTAL_OWN_AMT", 0)));
            //ʵ���Ը����
            exportData.setData("REAL_TOTAL_OWN_AMT", "TEXT",df.format(result.getDouble("REAL_TOTAL_OWN_AMT", 0)));
            //ͳ��������룬֧������д
            exportData.setData("NHI_PAY", "TEXT",df.format(result.getDouble("NHI_PAY", 0)));
            exportData.setData("NHI_PAY_REAL", "TEXT",df.format(result.getDouble("NHI_PAY", 0)));
            exportData.setData("NHI_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(result.getDouble("NHI_PAY", 0)));
            exportData.setData("NHI_PAY_REAL_DX", "TEXT",StringUtil.getInstance().numberToWord(result.getDouble("NHI_PAY", 0)));
            //���������룬֧������д
            exportData.setData("HOSP_APPLY_AMT", "TEXT",df.format(result.getDouble("HOSP_APPLY_AMT", 0)));
            exportData.setData("APPLY_PAY_REAL", "TEXT",df.format(result.getDouble("HOSP_APPLY_AMT", 0)));
            exportData.setData("APPLY_AMT_DX", "TEXT",StringUtil.getInstance().numberToWord(result.getDouble("HOSP_APPLY_AMT", 0)));
            exportData.setData("APPLY_PAY_REAL_DX", "TEXT",StringUtil.getInstance().numberToWord(result.getDouble("HOSP_APPLY_AMT", 0)));
            //�����˻����룬֧������д
            exportData.setData("ACCOUNT_NHI_PAY", "TEXT",df.format(result.getDouble("ACCOUNT_PAY_AMT", 0)));
            exportData.setData("ACCOUNT_REAL_PAY", "TEXT",df.format(result.getDouble("ACCOUNT_PAY_AMT", 0)));
            exportData.setData("ACCOUNT_NHI_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(result.getDouble("ACCOUNT_PAY_AMT", 0)));
            exportData.setData("ACCOUNT_REAL_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(result.getDouble("ACCOUNT_PAY_AMT", 0)));           
            //��������
            String  spPatType = result.getValue("SPECIAL_PAT",0);
            double  agentAmt = result.getDouble("ARMYAI_AMT",0);
            //�˲о���
            if("04".equals(spPatType))
            {
              exportData.setData("JC_NHI_PAY", "TEXT",df.format(agentAmt));
              exportData.setData("JC_REAL_PAY", "TEXT",df.format(agentAmt));
              exportData.setData("JC_NHI_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(agentAmt));
              exportData.setData("JC_REAL_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(agentAmt));
            }
            //����Ա����
            if("06".equals(spPatType))
            {
              exportData.setData("GWY_NHI_PAY", "TEXT",df.format(agentAmt));
              exportData.setData("GWY_REAL_PAY", "TEXT",df.format(agentAmt));
              exportData.setData("GWY_NHI_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(agentAmt));
              exportData.setData("GWY_REAL_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(agentAmt));
              
            }
            //�������� �� �Ÿ�����
            if("07".equals(spPatType)||"08".equals(spPatType))
            {
              exportData.setData("MZ_NHI_PAY", "TEXT",df.format(agentAmt));
              exportData.setData("MZ_REAL_PAY", "TEXT",df.format(agentAmt));
              exportData.setData("MZ_NHI_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(agentAmt));
              exportData.setData("MZ_REAL_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(agentAmt));
            }
            //�ǵ䲹��
            if("09".equals(spPatType))
            {
              exportData.setData("FD_NHI_PAY", "TEXT",df.format(agentAmt));
              exportData.setData("FD_REAL_PAY", "TEXT",df.format(agentAmt));
              exportData.setData("FD_NHI_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(agentAmt));
              exportData.setData("FD_REAL_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(agentAmt));
            }            
            //�ϼ�
            double sumNhiPay = 0;
            sumNhiPay  = result.getDouble("NHI_PAY", 0) +
                         result.getDouble("HOSP_APPLY_AMT", 0)+
                         result.getDouble("ACCOUNT_PAY_AMT", 0)+
                         agentAmt;
            exportData.setData("TOTAL_NHI_PAY", "TEXT",df.format(sumNhiPay));
            exportData.setData("TOTAL_REAL_PAY", "TEXT",df.format(sumNhiPay));
            exportData.setData("TOTAL_NHI_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(sumNhiPay));
            exportData.setData("TOTAL_REAL_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(sumNhiPay));
            openPrintWindow("%ROOT%\\config\\prt\\INS\\INS_NO2_SINGLE.jhw",exportData);
        }
       
    }

    /**
     * ���
     */
    public void onClear() {
        this.clearQueryCondition();
        this.clearAttr();
        this.clearFirst();
        this.clearSecond();
        this.clearThird();
        this.clearFourth();
        tablePanel.setSelectedIndex(0);
        //===zhangp 20120417 start
        clearValue("MR_NO;PAT_NAME");
        //===zhangp 20120417 end
    }

    public void onChangedPanel() {
        int index = ((TTabbedPane) getComponent("TablePanel")).getSelectedIndex();
        this.setCurrentTabPage(index);

        //����û��Ƿ�ѡ��һ��������Ϣ.
        if (index != 0 && this.getBrlbTableSelectedRow() == -1) {
            this.tablePanel.setSelectedIndex(0);
            messageBox("��ѡ��һ��������Ϣ.");
            return;
        }

        if (index == 1) { //���ý�����Ϣ
            String sql = "SELECT CONFIRM_NO, YEAR_MON, ADM_PRJ, IDNO, SEX_CODE, PAT_AGE, CTZ1_CODE,UNIT_CODE, ADM_CATEGORY, " +
                         "RECEIPT_USER, UNIT_DESC,TO_CHAR (DS_DATE, 'YYYYMMDD') AS DS_DATE, UPLOAD_FLG, START_STANDARD_AMT,SPEDRS_CODE, " +
                         "INS_UNIT, DEPT_DESC, STATION_DESC, BED_NO, HOSP_CLS_CODE,INP_TIME, HOMEBED_TIME, HOMEBED_DAYS, TRANHOSP_RESTANDARD_AMT," +
                         "TRANHOSP_DESC, TRAN_CLASS, DIAG_CODE, DIAG_DESC, SOURCE_CODE," +
                         "HOMEDIAG_DESC, CHEMICAL_DESC, OWN_RATE, DECREASE_RATE, REALOWN_RATE," +
                         "PHA_AMT, PHA_OWN_AMT, PHA_ADD_AMT, PHA_NHI_AMT, EXM_AMT, EXM_OWN_AMT," +
                         "EXM_ADD_AMT, EXM_NHI_AMT, TREAT_AMT, TREAT_OWN_AMT, TREAT_ADD_AMT," +
                         "TREAT_NHI_AMT, OP_AMT, OP_OWN_AMT, OP_ADD_AMT, OP_NHI_AMT, BED_AMT," +
                         "BED_OWN_AMT, BED_ADD_AMT, BED_NHI_AMT, MATERIAL_AMT, MATERIAL_OWN_AMT," +
                         "MATERIAL_ADD_AMT, MATERIAL_NHI_AMT, BLOODALL_AMT, BLOODALL_OWN_AMT," +
                         "BLOODALL_ADD_AMT, BLOODALL_NHI_AMT, BLOOD_AMT, BLOOD_OWN_AMT," +
                         "BLOOD_ADD_AMT, BLOOD_NHI_AMT, OTHER_AMT, OTHER_OWN_AMT, OTHER_ADD_AMT," +
                         "OTHER_NHI_AMT, RESTART_STANDARD_AMT, STARTPAY_OWN_AMT, OWN_AMT," +
                         "PERCOPAYMENT_RATE_AMT, ADD_AMT, INS_HIGHLIMIT_AMT, TRANBLOOD_OWN_AMT," +
                         "NHI_PAY,APPLY_AMT,HOSP_APPLY_AMT, NHI_COMMENT, TO_CHAR (IN_DATE, 'YYYYMMDD'), HOSP_NHI_NO," +
                         "ADM_SEQ, MR_NO, CASE_NO " +
                         "FROM INS_IBS " +
                         "WHERE ADM_SEQ = '" + this.getAdmSeq() + "'";
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            if (result.getCount() <= 0) {
                messageBox("��������");
                this.clearSecond();
                return;
            }
            DecimalFormat df = new DecimalFormat("##########0.00");
            this.setValue("QH", result.getValue("YEAR_MON", 0)); //�ں�
            this.setValue("JZXH", result.getValue("ADM_SEQ", 0)); //�������
            this.setValue("BAH", result.getValue("MR_NO", 0)); //������
            this.setValue("ZYH", result.getValue("CASE_NO", 0)); //סԺ��
            this.setValue("RYLB", this.ctzDesc); //��Ա���
            this.setValue("SJZFBL", result.getValue("REALOWN_RATE", 0)); //ʵ���Ը�����
            this.setValue("CYZD", result.getValue("DIAG_DESC", 0)); //��Ժ���

            //ҩƷ��
            this.setValue("YPFSJE", df.format(result.getDouble("PHA_AMT", 0))); //�������
            this.setValue("YPZFJE", df.format(result.getDouble("PHA_OWN_AMT", 0))); //�Ը����
            this.setValue("YPZHFJE",
                          df.format(result.getDouble("PHA_ADD_AMT", 0))); //�������
            this.setValue("YPSBJE", df.format(result.getDouble("PHA_NHI_AMT", 0))); //�걨���
            //����
            this.setValue("JCFSJE", df.format(result.getDouble("EXM_AMT", 0))); //�������
            this.setValue("JCZFJE", df.format(result.getDouble("EXM_OWN_AMT", 0))); //�Ը����
            this.setValue("JCZHFJE",
                          df.format(result.getDouble("EXM_ADD_AMT", 0))); //�������
            this.setValue("JCSBJE", df.format(result.getDouble("EXM_NHI_AMT", 0))); //�걨���
            //���Ʒ�
            this.setValue("ZLFSJE", df.format(result.getDouble("TREAT_AMT", 0))); //�������
            this.setValue("ZLZFJE",
                          df.format(result.getDouble("TREAT_OWN_AMT", 0))); //�Ը����
            this.setValue("ZLZHFJE",
                          df.format(result.getDouble("TREAT_ADD_AMT", 0))); //�������
            this.setValue("ZLSBJE",
                          df.format(result.getDouble("TREAT_NHI_AMT", 0))); //�걨���
            //������
            this.setValue("SSFSJE", df.format(result.getDouble("OP_AMT", 0))); //�������
            this.setValue("SSZFJE", df.format(result.getDouble("OP_OWN_AMT", 0))); //�Ը����
            this.setValue("SSZHFJE", df.format(result.getDouble("OP_ADD_AMT", 0))); //�������
            this.setValue("SSSBJE", df.format(result.getDouble("OP_NHI_AMT", 0))); //�걨���
            //��λ��
            this.setValue("CWFSJE", df.format(result.getDouble("BED_AMT", 0))); //�������
            this.setValue("CWZFJE", df.format(result.getDouble("BED_OWN_AMT", 0))); //�Ը����
            this.setValue("CWZHFJE",
                          df.format(result.getDouble("BED_ADD_AMT", 0))); //�������
            this.setValue("CWSBJE", df.format(result.getDouble("BED_NHI_AMT", 0))); //�걨���
            //ҽ�ò��Ϸ�
            this.setValue("YYCLFSJE",
                          df.format(result.getDouble("MATERIAL_AMT", 0))); //�������
            this.setValue("YYCLZFJE",
                          df.format(result.getDouble("MATERIAL_OWN_AMT", 0))); //�Ը����
            this.setValue("YYCLZHFJE",
                          df.format(result.getDouble("MATERIAL_ADD_AMT", 0))); //�������
            this.setValue("YYCLSBJE",
                          df.format(result.getDouble("MATERIAL_NHI_AMT", 0))); //�걨���
            //��ȫѪ
            this.setValue("SQXFSJE",
                          df.format(result.getDouble("BLOODALL_AMT", 0))); //�������
            this.setValue("SQXZFJE",
                          df.format(result.getDouble("BLOODALL_OWN_AMT", 0))); //�Ը����
            this.setValue("SQXZHFJE",
                          df.format(result.getDouble("BLOODALL_ADD_AMT", 0))); //�������
            this.setValue("SQXSBJE",
                          df.format(result.getDouble("BLOODALL_NHI_AMT", 0))); //�걨���
            //�ɷ���Ѫ
            this.setValue("CFSXFSJE", df.format(result.getDouble("BLOOD_AMT", 0))); //�������
            this.setValue("CFSXZFJE",
                          df.format(result.getDouble("BLOOD_OWN_AMT", 0))); //�Ը����
            this.setValue("CFSXZHFJE",
                          df.format(result.getDouble("BLOOD_ADD_AMT", 0))); //�������
            this.setValue("CFSXSBJE",
                          df.format(result.getDouble("BLOOD_NHI_AMT", 0))); //�걨���
            //����
            this.setValue("QTFSJE", df.format(result.getDouble("OTHER_AMT", 0))); //�������
            this.setValue("QTZFJE",
                          df.format(result.getDouble("OTHER_OWN_AMT", 0))); //�Ը����
            this.setValue("QTZHFJE",
                          df.format(result.getDouble("OTHER_ADD_AMT", 0))); //�������
            this.setValue("QTSBJE",
                          df.format(result.getDouble("OTHER_NHI_AMT", 0))); //�걨���
            //�ϼ�
            double hjfsje = result.getDouble("PHA_AMT", 0) +
                            result.getDouble("EXM_AMT", 0) +
                            result.getDouble("TREAT_AMT", 0) +
                            result.getDouble("OP_AMT", 0) +
                            result.getDouble("BED_AMT", 0) +
                            result.getDouble("MATERIAL_AMT", 0) +
                            result.getDouble("BLOODALL_AMT", 0) +
                            result.getDouble("BLOOD_AMT", 0) +
                            result.getDouble("OTHER_AMT", 0);
            double hjzfje = result.getDouble("PHA_OWN_AMT", 0) +
                            result.getDouble("EXM_OWN_AMT", 0) +
                            result.getDouble("TREAT_OWN_AMT", 0) +
                            result.getDouble("OP_OWN_AMT", 0) +
                            result.getDouble("BED_OWN_AMT", 0) +
                            result.getDouble("MATERIAL_OWN_AMT", 0) +
                            result.getDouble("BLOODALL_OWN_AMT", 0) +
                            result.getDouble("BLOOD_OWN_AMT", 0) +
                            result.getDouble("OTHER_OWN_AMT", 0);
            double hezhfje = result.getDouble("PHA_ADD_AMT", 0) +
                             result.getDouble("EXM_ADD_AMT", 0) +
                             result.getDouble("TREAT_ADD_AMT", 0) +
                             result.getDouble("OP_ADD_AMT", 0) +
                             result.getDouble("BED_ADD_AMT", 0) +
                             result.getDouble("MATERIAL_ADD_AMT", 0) +
                             result.getDouble("BLOODALL_ADD_AMT", 0) +
                             result.getDouble("BLOOD_ADD_AMT", 0) +
                             result.getDouble("OTHER_ADD_AMT", 0);
            double hjsbje = result.getDouble("PHA_NHI_AMT", 0) +
                            result.getDouble("EXM_NHI_AMT", 0) +
                            result.getDouble("TREAT_NHI_AMT", 0) +
                            result.getDouble("OP_NHI_AMT", 0) +
                            result.getDouble("BED_NHI_AMT", 0) +
                            result.getDouble("MATERIAL_NHI_AMT", 0) +
                            result.getDouble("BLOODALL_NHI_AMT", 0) +
                            result.getDouble("BLOOD_NHI_AMT", 0) +
                            result.getDouble("OTHER_NHI_AMT", 0);

            this.setValue("HJFSJE", df.format(hjfsje)); //�������
            this.setValue("HJZFJE", df.format(hjzfje)); //�Ը����
            this.setValue("HJZHFJE", df.format(hezhfje)); //�������
            this.setValue("HJSBJE", df.format(hjsbje)); //�걨���

            this.setValue("BCSSQFBZJE",
                          df.format(result.getDouble("RESTART_STANDARD_AMT", 0))); //����ʵ�������׼���
            this.setValue("QFBZYSZFBLJE",
                          df.format(result.getDouble("STARTPAY_OWN_AMT", 0))); //�𸶱�׼�����Ը��������
            this.setValue("ZFXMJE", df.format(result.getDouble("OWN_AMT", 0))); //�Է���Ŀ���
            this.setValue("YLJZGRABLFDJE",
                          df.format(result.getDouble("PERCOPAYMENT_RATE_AMT", 0))); //�Է���Ŀ���
            this.setValue("ZHFXMJE", df.format(result.getDouble("ADD_AMT", 0))); //������Ŀ���
            this.setValue("YLJZZGXEYSJE",
                          df.format(result.getDouble("INS_HIGHLIMIT_AMT", 0))); //ҽ�ƾ�������޶����Ͻ��
            this.setValue("SXZFJE",
                          df.format(result.getDouble("TRANBLOOD_OWN_AMT", 0))); //��Ѫ�Ը����
            double grzfhj = result.getDouble("RESTART_STANDARD_AMT", 0) +
                            result.getDouble("STARTPAY_OWN_AMT", 0) +
                            result.getDouble("OWN_AMT", 0) +
                            result.getDouble("PERCOPAYMENT_RATE_AMT", 0) +
                            result.getDouble("ADD_AMT", 0) +
                            result.getDouble("INS_HIGHLIMIT_AMT", 0) +
                            result.getDouble("TRANBLOOD_OWN_AMT", 0);
            this.setValue("GRZFHJ", df.format(grzfhj)); //�ϼ�

            this.setValue("TCJJZFYYSQ",
                          df.format(result.getDouble("APPLY_AMT", 0))); //ͳ�����֧��ҽԺ����
            this.setValue("YLJZYYSQ",
                          df.format(result.getDouble("HOSP_APPLY_AMT", 0))); //ͳ�����֧��ҽԺ����
        } else if (index == 2) { //������ϸ��Ϣ

        } else if (index == 3) { //ҽ���������ط�����ϸ

        }
    }

    /**
     *����ҳǩ: ������ϸ��ϢTabҳ�е����ѯ��ť�����Ĳ���.
     */
    public void onSearchFeeDetail() {
        String fylb = this.getValueString("FYLB1");
        String sql =
                "SELECT DISTINCT A.SEQ_NO, A.ORDER_DESC, B.XMMC, D.DOSE_CHN_DESC, A.STANDARD," +
                "A.PRICE, A.QTY, A.TOTAL_AMT, A.REFUSE_AMT," +
                "A.REFUSE_REASON_NOTE, A.OWN_AMT, A.ADDPAY_AMT," +
                "A.TOTAL_NHI_AMT, A.OP_FLG, A.CARRY_FLG, A.ADDPAY_FLG," +
                "A.PHAADD_FLG, A.OWN_RATE, A.NHI_ORD_CLASS_CODE, A.OPT_DATE," +
                "A.NHI_ORDER_CODE, A.REFUSE_REASON_CODE, A.ADM_SEQ" +
                " FROM INS_IBS_UPLOAD A LEFT OUTER JOIN PHA_DOSE D" +
                " ON A.DOSE_CODE = D.DOSE_CODE" +
                " LEFT OUTER JOIN INS_RULE B" +
                " ON A.NHI_ORDER_CODE = B.SFXMBM" +
                " AND A.CHARGE_DATE BETWEEN B.KSSJ AND B.JSSJ" +
                " WHERE A.TOTAL_AMT <> 0 AND A.ADM_SEQ = '" + this.getAdmSeq() +
                "'";
        StringBuilder sbuilder = new StringBuilder(sql);
        if (!"".equals(fylb)) {
            sbuilder.append(" And A.NHI_ORD_CLASS_CODE='" + fylb + "'");
        }
        sbuilder.append(" ORDER BY A.SEQ_NO  ");

        TParm result = new TParm(TJDODBTool.getInstance().select(sbuilder.
                toString()));
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        if (result.getCount() <= 0) {
            messageBox("��������");
            this.callFunction("UI|FYMXXXTABLE|setParmValue", new TParm());
            return;
        }
        this.callFunction("UI|FYMXXXTABLE|setParmValue", result);
    }

    /**
     *����ҳǩ: ҽ���������ط�����ϸ.
     */
    public void searchDownloadFeeDetail() {
        String fylb2 = this.getValueString("FYLB2");
        String sql =
                "SELECT DISTINCT A.SEQ_NO, A.ORDER_DESC, B.XMMC, D.DOSE_CHN_DESC, A.STANDARD," +
                "A.PRICE, A.QTY, A.TOTAL_AMT, A.REFUSE_AMT," +
                "A.REFUSE_REASON_NOTE, A.OWN_AMT, A.ADDPAY_AMT," +
                "A.TOTAL_NHI_AMT, A.OP_FLG, A.CARRY_FLG, A.PHAADD_FLG," +
                "A.PHAADD_FLG, A.OWN_RATE, A.NHI_ORD_CLASS_CODE, A.OPT_DATE," +
                "A.NHI_ORDER_CODE, A.REFUSE_REASON_CODE, A.ADM_SEQ" +
                " FROM INS_IBSORDER_DOWNLOAD A LEFT OUTER JOIN PHA_DOSE D" +
                " ON A.DOSE_CODE = D.DOSE_CODE" +
                " LEFT OUTER JOIN INS_RULE B" +
                " ON A.NHI_ORDER_CODE = B.SFXMBM" +
                " AND A.CHARGE_DATE BETWEEN B.KSSJ AND B.JSSJ" +
                //" , SYS_FEE C"+
                " WHERE A.TOTAL_AMT <> 0" +
                " AND A.ADM_SEQ = '" + this.getAdmSeq() + "' ";
        //" AND A.ORDER_CODE = C.ORDER_CODE";

        StringBuilder sbuilder = new StringBuilder(sql);
        if (!"".equals(fylb2)) {
            sbuilder.append(" And A.NHI_ORD_CLASS_CODE='" + fylb2 + "'");
        }
        sbuilder.append(" ORDER BY A.SEQ_NO");

        TParm result = new TParm(TJDODBTool.getInstance().select(sbuilder.
                toString()));
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        if (result.getCount() <= 0) {
            messageBox("��������");
            this.callFunction("UI|YBZXXZFYMX|setParmValue", new TParm());
            return;
        }
        this.callFunction("UI|YBZXXZFYMX|setParmValue", result);
    }

    /**
     *
     * ת��ʱ��.
     * @param date
     * @return
     */
    private String transferDate(String date) {
        String[] dateArray = date.split(" ");
        String[] newDateArray = dateArray[0].split("-");
        return newDateArray[0] + newDateArray[1] + newDateArray[2];
    }

    /**
     *
     * �˲��ѯ����.
     * @return
     */
    private boolean checkInput(int index) {
        String startDate = this.getValueString("START_DATE"); //��ʼʱ��
        String endDate = this.getValueString("END_DATE"); //����ʱ��
        String dBZ = this.getValueString("DBZ"); //������.
        if ("".equals(startDate)) {
            this.messageBox("��ʼʱ�䲻��Ϊ��");
            ((TTextField)this.getComponent("START_DATE")).grabFocus();
            return false;
        }
        if ("".equals(endDate)) {
            this.messageBox("��ֹʱ�䲻��Ϊ��");
            ((TTextField)this.getComponent("END_DATE")).grabFocus();
            return false;
        }
        if ("".equals(dBZ)) {
            this.messageBox("�����ֲ���Ϊ��");
            ((TTextField)this.getComponent("DBZ")).grabFocus();
            return false;
        }
        return true;
    }

    /**
     * BRLBTABLE ����.
     */
    public void brlbTableClicked() {
        TTable brlb = ((TTable)this.getComponent("BRLBTABLE"));
        this.brlbTableSelectedRow = brlb.getSelectedRow();
        TParm parm = brlb.getParmValue();
        this.admSeq = parm.getValue("ADM_SEQ", this.brlbTableSelectedRow); //��þ������.
        this.ctzDesc = parm.getValue("CTZ_DESC", this.brlbTableSelectedRow); //�����Ա���.
    }

    /**
     * ������ò�ѯ������Ϣ.
     */
    private void clearQueryCondition() {
        this.setValue("START_DATE", "");
        this.setValue("END_DATE", "");
        this.setValue("DBZ", "");
        this.setValue("NYBLB", "");
        //===========CHENXI  2012.05.29
        this.setValue("IN_STATUS", "");
        //==============chenxi  2012.05.29
    }

    /**
     * �����һҳǩ��Ϣ.
     */
    private void clearFirst() {
        this.callFunction("UI|BRLBTABLE|setParmValue", new TParm());
    }

    /**
     * ����ڶ�ҳǩ��Ϣ.
     */
    private void clearSecond() {
        String clearStr =
                "QH;JZXH;BAH;ZYH;RYLB;SJZFBL;CYZD;YPFSJE;YPZFJE;YPZHFJE;YPSBJE;JCFSJE;JCZFJE;JCZHFJE;JCSBJE;ZLFSJE;"+
                "ZLZFJE;ZLZHFJE;ZLSBJE;SSFSJE;SSZFJE;SSZHFJE;SSSBJE;CWFSJE;CWZFJE;CWZHFJE;CWSBJE;YYCLFSJE;YYCLZFJE;"+
                "YYCLZHFJE;YYCLSBJE;SQXFSJE;SQXZFJE;SQXZHFJE;SQXSBJE;CFSXFSJE;CFSXZFJE;CFSXZHFJE;CFSXSBJE;QTFSJE;"+
                "QTZFJE;QTZHFJE;QTSBJE;HJFSJE;HJZFJE;HJZHFJE;HJSBJE;BCSSQFBZJE;ZFXMJE;ZHFXMJE;SXZFJE;QFBZYSZFBLJE;"+
                "YLJZGRABLFDJE;YLJZZGXEYSJE;GRZFHJ;TCJJZFYYSQ;YLJZYYSQ";
        this.clearValue(clearStr);
    }

    /**
     * �������ҳǩ��Ϣ.
     */
    private void clearThird() {
        this.setValue("FYLB1", "");
        this.callFunction("UI|FYMXXXTABLE|setParmValue", new TParm());
    }

    /**
     * �������ҳǩ��Ϣ.
     */
    private void clearFourth() {
        this.setValue("FYLB2", "");
        this.callFunction("UI|YBZXXZFYMX|setParmValue", new TParm());
    }

    /**
     * �����������
     */
    private void clearAttr() {
        this.brlbTableSelectedRow = -1;
        this.admSeq = "";
        this.ctzDesc = "";
    }

    private String specialPat(String specialPat) {
        if (specialPat.equals("01"))
            return "����";
        else if (specialPat.equals("02"))
            return "��������";
        else if (specialPat.equals("03"))
            return "�м���ģ";
        else if (specialPat.equals("04"))
            return "����";
        else if (specialPat.equals("05"))
            return "������Ա";
        else if (specialPat.equals("06"))
            return "����Ա";
        return "";
    }

    public int getCurrentTabPage() {
        return currentTabPage;
    }

    public void setCurrentTabPage(int currentTabPage) {
        this.currentTabPage = currentTabPage;
    }

    public int getBrlbTableSelectedRow() {
        return brlbTableSelectedRow;
    }

    public void setBrlbTableSelectedRow(int brlbTableSelectedRow) {
        this.brlbTableSelectedRow = brlbTableSelectedRow;
    }

    public String getAdmSeq() {
        return admSeq;
    }

    public void setAdmSeq(String admSeq) {
        this.admSeq = admSeq;
    }

    public String getCtzDesc() {
        return ctzDesc;
    }

    public void setCtzDesc(String ctzDesc) {
        this.ctzDesc = ctzDesc;
    }

    public String getNhi_no() {
        return nhi_no;
    }

    public void setNhi_no(String nhiNo) {
        nhi_no = nhiNo;
    }
    /**
     * ������ѯ
     * ====zhangp 20120417
     */
    public void onQueryPat(){
    	String mrno = getValueString("MR_NO");
    	Pat pat = Pat.onQueryByMrNo(mrno);
    	setValue("MR_NO", pat.getMrNo());
    	setValue("PAT_NAME", pat.getName());
    	onQuery();//add by wanglong 20121011
    }
    /**
	 * �����������������
	 * 
	 * @param table
	 *            TTable
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = brlbTable.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = brlbTable.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				getTMenuItem("save").setEnabled(false);
			}
		});
	}
    /**
	 * �����������������
	 * 
	 * @param table
	 *            TTable
	 */
	public void addListener1(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = fymxxxTable.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = fymxxxTable.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam1(vct, new TParm(), strNames);

				getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * vectoryת��param
	 * 
	 * @param vectorTable
	 *            Vector
	 * @param parmTable
	 *            TParm
	 * @param columnNames
	 *            String
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		brlbTable.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}
	/**
	 * vectoryת��param
	 * 
	 * @param vectorTable
	 *            Vector
	 * @param parmTable
	 *            TParm
	 * @param columnNames
	 *            String
	 */
	private void cloneVectoryParam1(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		fymxxxTable.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}
	/**
	 * �õ� Vector ֵ
	 * 
	 * @param parm
	 *            TParm
	 * @param group
	 *            String
	 * @param names
	 *            String
	 * @param size
	 *            int
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * ת��parm�е���
	 * 
	 * @param columnName
	 *            String[]
	 * @param tblColumnName
	 *            String
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}
	/**
	 * �õ��˵�
	 * 
	 * @param tag
	 *            String
	 * @return TMenuItem
	 */
	public TMenuItem getTMenuItem(String tag) {
		return (TMenuItem) this.getComponent(tag);
	}
}
