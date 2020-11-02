package com.javahis.ui.udd;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import jdo.adm.ADMInpTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.udd.UddChnCheckTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import jdo.sys.SystemTool;

/**
 * <p> Title: סԺҩ����ҽ�䷢ </p>
 *
 * <p> Description: סԺҩ����ҽ�䷢ </p>
 *
 * <p> Copyright: javahis 20090311 </p>
 *
 * <p> Company: </p>
 *
 * @author ehui
 * @version 1.0
 */
public class UddChnMedDispense
    extends TControl {
    //����Y
    public static final String Y = "Y";
    //����N
    public static final String N = "N";
    //�������ַ�
    public static final String NULL = "";
    //����TABLE,ҩƷTABLE
    private TTable tblPat, tblDtl;
    //ִ���б�
    private List execList = new ArrayList();
    //�������
    private TParm saveParm = new TParm();
    //��ҩ��ҩ���֣��ӿ�ܴ��롣YΪ��ҩ��NΪ��ҩ
    private boolean isDosage;
    //�ӿ�ܴ��룬��ǰ������:����ҩ���Ƿ�ҩ
    private String controlName;
    //�ۿ�㣬�ӿ�ܴ��롣YΪ��ҩ�ۿ⣬NΪ��ҩ�ۿ�
    private String charge;
    //�Ƿ���Ҫ��ˣ��ӿ�ܴ��롣YΪ��Ҫ��ˣ�NΪ����Ҫ���
    private boolean isCheckNeeded;
    /**
     * ��ʼ������
     */
    public void onInitParameter() {
        controlName = this.getParameter() + "";
        if ("C_DOSAGE".equalsIgnoreCase(controlName)) {
            this.setTitle("��ҩ����");
        }
        else {
            this.setTitle("��ҩ��ҩ");
        }
    }

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        isDosage = TypeTool.getBoolean(TConfig.getSystemValue("IS_DOSAGE"));
        isCheckNeeded = TypeTool.getBoolean(TConfig.getSystemValue("IS_CHECK"));
        charge = TConfig.getSystemValue("CHARGE_POINT");
        tblPat = (TTable)this.getComponent("TBL_PAT");
        tblDtl = (TTable)this.getComponent("TBL_DTL");
        onClear();
    }

    /**
     * ���
     */
    public void onClear() {
        Timestamp t = StringTool.rollDate(TJDODBTool.getInstance().getDBTime(),
                                          1);
        this.setValue("START_DATE", StringTool.rollDate(t, -7));
        this.setValue("END_DATE", t);
        this.setValue("EXEC_DEPT_CODE", Operator.getDept());
        this.setValue("AGENCY_ORG_CODE", NULL);
        this.setValue("STA", Y);
        this.setValue("UNDONE", Y);
        this.setValue("UDST", "Y");
        tblPat.removeRowAll();
        tblDtl.removeRowAll();
        this.setValue("TAKE_DAYS", 0);
        this.setValue("DCT_TAKE_QTY", NULL);
        this.setValue("FREQ_CODE", NULL);
        this.setValue("ROUTE_CODE", NULL);
        this.setValue("TOT_GRAM", NULL);
        this.setValue("PACKAGE_TOT", NULL);
        this.setValue("DCTAGENT_CODE", NULL);
        this.setValue("DR_NOTE", NULL);
        this.setValue("NO", NULL);
        this.setValue("NAME", NULL);
        setEnableMenu();
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
//        //ִ,40,boolean;����,60,boolean;����,80;����,80;������,100;��ʿվ,120;������,120;סԺ��,120
//        //2,right;3,left;4,right;5,left;6,right;7,right
//        //EXEC;URGENT_FLG;BED_NO;PAT_NAME;PRESRT_NO;STATION_CODE;MR_NO;IPD_NO
//        StringBuffer sql = new StringBuffer();
//        sql.append("SELECT 'N' AS EXEC, A.DCTAGENT_FLG, A.CASE_NO,A.ORDER_NO,A.BED_NO,B.PAT_NAME,A.RX_NO,A.STATION_CODE,A.MR_NO,A.PRESRT_NO,A.IPD_NO, A.PHA_DOSAGE_CODE, A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE FROM ODI_DSPNM A , SYS_PATINFO B ,SYS_BED C WHERE  B.MR_NO(+)=A.MR_NO AND C.BED_NO=A.BED_NO AND  (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG='Y')  AND A.DSPN_KIND='IG' ").
//            append(getWhere());
//        String sqlStr = sql.append(" GROUP BY A.CASE_NO,A.ORDER_NO,A.BED_NO,B.PAT_NAME,A.RX_NO,A.STATION_CODE,A.MR_NO,A.IPD_NO,A.PRESRT_NO ,A.PHA_DOSAGE_CODE, A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE, A.DCTAGENT_FLG ").
//            append(" ORDER BY A.CASE_NO,A.ORDER_NO,A.RX_NO ").toString();
        //===========pangben modify 20110512 start
                    String region = "";
                    if (null != Operator.getRegion() &&
                        Operator.getRegion().length() > 0) {
                        region = " AND A.REGION_CODE='" + Operator.getRegion() +
                                 "' ";
                    }
            //===========pangben modify 20110512 stop
        String sqlStr = "SELECT 'N' AS EXEC, A.DCTAGENT_FLG, A.CASE_NO, "
            + " A.ORDER_NO,A.BED_NO,B.PAT_NAME,A.RX_NO,A.STATION_CODE, "
            + " A.MR_NO,A.PRESRT_NO,A.IPD_NO, A.PHA_DOSAGE_CODE, "
            + " A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE "
            + " FROM ODI_DSPNM A , SYS_PATINFO B ,SYS_BED C "
            + " WHERE  B.MR_NO(+)=A.MR_NO AND C.BED_NO=A.BED_NO "+region
            //+ " AND  (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG='Y')  "
            + " AND A.DSPN_KIND='IG' " + getWhere()
            + " GROUP BY A.CASE_NO,A.ORDER_NO,A.BED_NO, B.PAT_NAME, A.RX_NO, "
            + " A.STATION_CODE,A.MR_NO,A.IPD_NO,A.PRESRT_NO ,A.PHA_DOSAGE_CODE, "
            + " A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE, "
            + " A.DCTAGENT_FLG "
            + " ORDER BY A.CASE_NO,A.ORDER_NO,A.RX_NO ";

        //System.out.println("sqlStr=" + sqlStr);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sqlStr));
        //System.out.println("onQuery->"+sql.toString());
        tblPat.setParmValue(parm);
        if (StringTool.getBoolean(this.getValueString("MR")) ||
            StringTool.getBoolean(this.getValueString("BED"))) {
            this.setValue("NAME", parm.getValue("PAT_NAME", 0));
        }
        setEnableMenu();
    }

    /**
     * ȡ�ò�ѯ����
     * @return
     */
    public String getWhere() {
        StringBuffer result = new StringBuffer();
        String startDate = StringTool.getString(TCM_Transform.getTimestamp(this.
            getValue("START_DATE")), "yyyyMMddHHmmss");
        String endDate = StringTool.getString(TCM_Transform.getTimestamp(this.
            getValue("END_DATE")), "yyyyMMddHHmmss").substring(0, 8) + "235959";
        if (TypeTool.getBoolean(this.getValue("UNDONE"))) {
            //�������ҩ����
            if ("C_DOSAGE".equalsIgnoreCase(controlName)) {
                //�����Ҫ���,�����ʱ��ȥ��
                if (isCheckNeeded) {
                    result.append(
                        " AND A.PHA_CHECK_CODE IS NOT NULL AND A.PHA_CHECK_DATE >=TO_DATE('" +
                        startDate +
                        "','YYYYMMDDHH24MISS') AND A.PHA_CHECK_DATE<=TO_DATE('" +
                        endDate +
                        "','YYYYMMDDHH24MISS') AND A.PHA_DOSAGE_CODE IS NULL ");
                }
                //�����ÿ�ʼ������ʱ��ȥ��
                else {
                    result.append(" AND A.START_DTTM >='" + startDate +
                                  "' AND A.START_DTTM<='" + endDate +
                                  "' AND A.PHA_DOSAGE_CODE IS NULL");
                }
            }
            //����Ƿ�ҩ����
            else {
                //�����Ҫ��ҩ,����ҩ����ȥ��
                if (isDosage) {
                    result.append(
                        " AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.PHA_DOSAGE_DATE >=TO_DATE('" +
                        startDate +
                        "','YYYYMMDDHH24MISS') AND A.PHA_DOSAGE_DATE<=TO_DATE('" +
                        endDate +
                        "','YYYYMMDDHH24MISS') AND A.PHA_DISPENSE_CODE IS NULL "
                        +
                        " AND (A.DCTAGENT_FLG <> 'Y' OR A.DCTAGENT_FLG IS NULL OR (A.DCTAGENT_FLG = 'Y' AND A.FINAL_TYPE = 'O')) ");
                }
                //�������Ҫ��ҩ���ж��Ƿ���Ҫ��ˣ�����Ҫ����������ʱ��ȥ���������ÿ�ʼ������ʱ��ȥ��
                else {
                    if (isCheckNeeded) {
                        result.append(
                            " AND A.PHA_CHECK_CODE IS NOT NULL AND A.PHA_CHECK_DATE >=TO_DATE('" +
                            startDate +
                            "','YYYYMMDDHH24MISS') AND A.PHA_CHECK_DATE<=TO_DATE('" +
                            endDate +
                            "','YYYYMMDDHH24MISS') AND A.PHA_DISPENSE_CODE IS NULL "
                            +
                            " AND (A.DCTAGENT_FLG <> 'Y' OR A.DCTAGENT_FLG IS NULL OR (A.DCTAGENT_FLG = 'Y' AND A.FINAL_TYPE = 'O')) ");
                    }
                    else {

                    }
                    result.append(" AND A.START_DTTM >='" + startDate +
                                  "' AND A.START_DTTM<='" + endDate +
                                  "' AND A.PHA_DISPENSE_CODE IS NULL");
                }

            }

        }
        else {
            //�������ҩ����ֱ������ҩʱ��ȥ��
            if ("C_DOSAGE".equalsIgnoreCase(controlName)) {
                result.append(
                    " AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.PHA_DOSAGE_DATE >=TO_DATE('" +
                    startDate +
                    "','YYYYMMDDHH24MISS') AND A.PHA_DOSAGE_DATE<=TO_DATE('" +
                    endDate +
                    "','YYYYMMDDHH24MISS')  ");
            }
            //����Ƿ�ҩ���������ж��Ƿ���Ҫ��ҩ���Ƿ���Ҫ���
            else {
                result.append(
                    " AND A.PHA_DISPENSE_CODE IS NOT NULL AND A.PHA_DISPENSE_DATE >=TO_DATE('" +
                    startDate +
                    "','YYYYMMDDHH24MISS') AND A.PHA_DISPENSE_DATE<=TO_DATE('" +
                    endDate +
                    "','YYYYMMDDHH24MISS')  ");
            }
        }
        result.append(" AND A.EXEC_DEPT_CODE='" +
                      this.getValueString("EXEC_DEPT_CODE") +
                      "'");
        if (!StringUtil.isNullString(this.getValueString("AGENCY_ORG_CODE"))) {
            result.append(" AND A.AGENCY_ORG_CODE='" +
                          this.getValueString("AGENCY_ORG_CODE") +
                          "'");
        }
        if (StringTool.getBoolean(this.getValueString("STA"))) {
            if (!StringUtil.isNullString(this.getValueString("STATION"))) {
                result.append(" AND A.STATION_CODE='" +
                              this.getValueString("STATION") +
                              "'");
            }
        }
        else if (StringTool.getBoolean(this.getValueString("MR"))) {
            String mrNo = StringTool.fill0(this.getValueString("NO"), PatTool.getInstance().getMrNoLength());//====chenxi
            this.setValue("NO", mrNo);
            result.append(" AND A.MR_NO='" + mrNo +
                          "'");
        }
        else {
            result.append(" AND A.BED_NO='" + this.getValueString("NO") +
                          "' ");
        }
        if (!StringUtil.isNullString(this.getValueString("DECOCT_CODE")))
            result.append(" AND A.DECOCT_CODE='" +
                          this.getValueString("DECOCT_CODE") +
                          "'");

        return result.toString();
    }

    /**
     * ����TABLE�����¼�,���ò���ҽ����ʾ����
     */
    public void onTblPatClick() {
        TParm parm = tblPat.getParmValue();
        int rows = tblPat.getSelectedRow();
        for (int i = 0; i < tblPat.getRowCount(); i++) {
            tblPat.setValueAt(false, i, 0);
            parm.setData("EXEC", i, N);
        }
        tblPat.setValueAt(true, rows, 0);
        tblDtl.removeRowAll();
        parm.setData("EXEC", rows, Y);
        //===========pangben modify 20110513 start
        String region = "";
        if (null != Operator.getRegion() &&
            Operator.getRegion().length() > 0) {
            region = " AND A.REGION_CODE='" + Operator.getRegion() +
                     "' ";
        }
        //===========pangben modify 20110513 stop

        String sql =
            " SELECT A.CASE_NO, A.ORDER_NO, A.ORDER_SEQ, A.START_DTTM, A.ORDER_DESC, A.MEDI_QTY," +
            "	 A.FREQ_CODE, A.ROUTE_CODE, A.TAKE_DAYS, A.DCT_TAKE_QTY, A.DCTAGENT_CODE, A.DR_NOTE," +
            "	 A.DCTEXCEP_CODE, A.PACKAGE_AMT, A.RX_NO, A.ORDER_CODE, A.IPD_NO, A.MR_NO," +
            "	 A.DEPT_CODE, A.STATION_CODE, A.BED_NO, A.EXEC_DEPT_CODE, A.DOSAGE_QTY," +
            "	 A.DOSAGE_UNIT, A.ORDER_DEPT_CODE, A.ORDER_DR_CODE, A.ORDERSET_CODE, A.MEDI_UNIT," +
            "	 A.ORDERSET_GROUP_NO, A.CAT1_TYPE, A.ORDER_DATE, " +
            "    CASE WHEN B.SERVICE_LEVEL = 2 THEN C.OWN_PRICE2 WHEN B.SERVICE_LEVEL = 3 THEN C.OWN_PRICE3 ELSE C.OWN_PRICE END AS OWN_PRICE,A.END_DTTM,G.STOCK_PRICE * A.DOSAGE_QTY AS COST_AMT " +
            " FROM ODI_DSPNM A, ADM_INP B, SYS_FEE C, PHA_BASE G " +
            " WHERE A.CASE_NO = B.CASE_NO AND A.ORDER_CODE = C.ORDER_CODE AND A.ORDER_CODE = G.ORDER_CODE " +region+//===========pangben modify 20110513
            " AND A.CASE_NO='" + parm.getValue("CASE_NO", rows) +
            "' AND A.RX_NO='" + parm.getValue("RX_NO", rows) +
            "' ORDER BY A.ORDER_SEQ ";
        //System.out.println("sql->"+sql);
        saveParm = new TParm(TJDODBTool.getInstance().select(sql));
        TParm parmShow = new TParm();
        int count = saveParm.getCount("ORDER_SEQ");
        if (count <= 0)
            return;
        if (count % 4 == 0)
            count = count / 4 * 4;
        else
            count = count / 4 * 4 + 4;
        double totQty = 0.0;
        for (int i = 0; i < count; i++) {
            //System.out.println(i%4);
            //System.out.println(saveParm.getDouble("MEDI_QTY",i));
            parmShow.addData("ORDER_DESC" + (i % 4),
                             saveParm.getValue("ORDER_DESC", i));
            parmShow.addData("MEDI_QTY" + (i % 4),
                             saveParm.getDouble("MEDI_QTY", i));
            parmShow.addData("DCTEXCEP_CODE" + (i % 4),
                             saveParm.getValue("DCTEXCEP_CODE", i));
            totQty += saveParm.getDouble("MEDI_QTY", i);
        }
        this.setValue("TAKE_DAYS", saveParm.getInt("TAKE_DAYS", 0));
        this.setValue("DCT_TAKE_QTY",
                      String.valueOf(saveParm.getDouble("DCT_TAKE_QTY", 0)));
        this.setValue("FREQ_CODE", saveParm.getValue("FREQ_CODE", 0));
        this.setValue("ROUTE_CODE", saveParm.getValue("ROUTE_CODE", 0));
        this.setValue("TOT_GRAM", String.valueOf(totQty));
        this.setValue("PACKAGE_AMT", saveParm.getDouble("PACKAGE_AMT", 0));
        this.setValue("DCTAGENT_CODE", saveParm.getValue("DCTAGENT_CODE", 0));
        this.setValue("DR_NOTE", saveParm.getValue("DR_NOTE", 0));
        //System.out.println("parmShow->"+parmShow);

        tblDtl.setParmValue(parmShow);
    }

    /**
     * ����
     */
    public void onSave() {
        if (!TypeTool.getBoolean(this.getValue("UNDONE"))) {
            this.messageBox_("���������ݲ����ظ�����");
            return;
        }

        //�������ҩȷ�ϱ���
        if ("DOSAGE".equalsIgnoreCase(controlName)) {
            String sql = "";
            String case_no = "";
            String order_no = "";
            int order_seq = 0;
            String start_dttm = "";
            for (int i = 0; i < saveParm.getCount("ORDER_CODE"); i++) {
                case_no = saveParm.getValue("CASE_NO", i);
                order_no = saveParm.getValue("ORDER_NO", i);
                order_seq = saveParm.getInt("ORDER_SEQ", i);
                start_dttm = saveParm.getValue("START_DTTM", i);
                sql = "SELECT PHA_DOSAGE_DATE FROM ODI_DSPNM WHERE CASE_NO = '" +
                    case_no + "' AND ORDER_NO = '" + order_no +
                    "' AND ORDER_SEQ = " + order_seq + " AND START_DTTM = '" +
                    start_dttm + "'";

                TParm checkParm = new TParm(TJDODBTool.getInstance().select(sql));
                //System.out.println("checkParm---"+checkParm);
                if (checkParm.getCount("PHA_DOSAGE_DATE") > 0 &&
                    checkParm.getData("PHA_DOSAGE_DATE", 0) != null &&
                    !"".equals(checkParm.getValue("PHA_DOSAGE_DATE", 0))) {
                    this.messageBox("ҩƷ�Ѿ���ҩ�������²�ѯ");
                    return;
                }
            }
        }

        TParm parm = new TParm();
        for (int i = 0; i < saveParm.getCount("RX_NO"); i++) {
//			this.messageBox_(saveParm.getValue("CASE_NO",i));
            parm = new TParm();
            parm.setData("CASE_NO", saveParm.getValue("CASE_NO", i));
            TParm admInp = ADMInpTool.getInstance().selectall(parm);
            if (admInp.getErrCode() != 0) {
                //System.out.println("admInp==" + admInp);
                continue;
            }
            saveParm.addData("CTZ1_CODE", admInp.getValue("CTZ1_CODE", 0));
            saveParm.addData("CTZ2_CODE", admInp.getValue("CTZ2_CODE", 0));
            saveParm.addData("CTZ3_CODE", admInp.getValue("CTZ3_CODE", 0));
            saveParm.addData("OPT_USER", Operator.getID());
            saveParm.addData("OPT_TERM", Operator.getIP());
            saveParm.addData("PHA_DISPENSE_NO", "");
//			parm.addData("OPT_USER", Operator.getID());
//			parm.addData("OPT_TERM", Operator.getIP());
//
//			parm.addData("ORDER_NO", saveParm.getValue("ORDER_NO",i));
//			parm.addData("RX_NO", saveParm.getValue("RX_NO",i));
        }
        //System.out.println("saveParm=======" + saveParm);
        saveParm.setData("FLG", "ADD");
        //zhangyong20110516 �������REGION_CODE
        saveParm.setData("REGION_CODE", Operator.getRegion());
        saveParm.setData("CHARGE", this.charge);

        if ("C_DOSAGE".equalsIgnoreCase(controlName)) {
//			this.messageBox_("herer");

            parm = TIOM_AppServer.executeAction(
                "action.udd.UddAction", "onUpdateMedDosage", saveParm);
        }
        else {
            parm = TIOM_AppServer.executeAction(
                "action.udd.UddAction", "onUpdateMedDispense", saveParm);
        }

        Object list = parm.getData("ERR_LIST");
        StringBuffer sbErr = new StringBuffer();
        if (list != null) {
            TParm errList = new TParm( (Map) list);
            if (errList != null) {
                //System.out.println("errList=" + errList);
                int countErr = errList.getCount();
                if (countErr > 0) {
                    for (int i = 0; i < countErr; i++) {
                        String err = errList.getValue("MSG", i);
                        this.messageBox_(err);
                        sbErr.append(err).append("\r\n");
                    }
                }
            }
        }
        else {
            if (parm.getErrCode() != 0) {
                this.messageBox("E0001");
//				this.messageBox_(parm.getErrText());

                return;
            }
            else {
                this.messageBox("P0001");
                onClear();
                return;
            }
        }
    }

    /**
     * ȡ���¼�
     */
    public void onDelete() {
        if (TypeTool.getBoolean(this.getValue("UNDONE"))) {
            this.messageBox_("δ�������ݲ���ȡ��");
            return;
        }
        if (saveParm == null) {
            this.messageBox_("û������");
            return;
        }
        int count = saveParm.getCount();
        if (count < 1) {
            this.messageBox_("û������");
            return;
        }

        Timestamp date = SystemTool.getInstance().getDate();
        for (int i = 0; i < count; i++) {
            TParm parm = new TParm();
            parm.setData("CASE_NO", saveParm.getValue("CASE_NO", i));
            TParm admInp = ADMInpTool.getInstance().selectall(parm);
            if (admInp.getErrCode() != 0) {
                //System.out.println("admInp==" + admInp);
                continue;
            }
            saveParm.addData("CTZ1_CODE", admInp.getValue("CTZ1_CODE", 0));
            saveParm.addData("CTZ2_CODE", admInp.getValue("CTZ2_CODE", 0));
            saveParm.addData("CTZ3_CODE", admInp.getValue("CTZ3_CODE", 0));
            saveParm.addData("SERVICE_LEVEL", admInp.getValue("SERVICE_LEVEL", 0));
            saveParm.addData("OPT_USER", Operator.getID());
            saveParm.addData("OPT_DATE", date);
            saveParm.addData("OPT_TERM", Operator.getIP());
        }
        TParm result = new TParm();
        saveParm.setData("CHARGE", this.charge);
//		this.messageBox_(charge);
        saveParm.setData("FLG", "DELETE");
        //zhangyong20110516 �������REGION_CODE
        saveParm.setData("REGION_CODE", Operator.getRegion());
//		this.messageBox_(controlName);
        TParm patParm = tblPat.getParmValue().getRow(tblPat.getSelectedRow());
        if ("C_DOSAGE".equalsIgnoreCase(controlName)) {
//			this.messageBox_("here");
            if (!"".equals(patParm.getValue("PHA_DISPENSE_CODE"))) {
                this.messageBox("��ҩƷ�ѷ�ҩ������ȡ����ҩ");
                return;
            }
            result = TIOM_AppServer.executeAction(
                "action.udd.UddAction", "onCnclUpdateMedDosage", saveParm);
        }
        else {
            result = TIOM_AppServer.executeAction(
                "action.udd.UddAction", "onCnclUpdateMedDispense", saveParm);
        }

        if (result.getErrCode() != 0) {
            this.messageBox("E0001");
//			this.messageBox_(result.getErrText());

            return;
        }
        else {
            this.messageBox("P0001");
            onClear();
            return;
        }
    }

    /**
     * ����ҽ����
     */
    public void onDispenseSheet() {
        if (saveParm == null) {
            return;
        }
        int count = saveParm.getCount();
        if (count < 1) {
            return;
        }
        /**
           inParam.setData("ORG_CODE",text,odo.getValue("ORG_CODE"));
           inParam.setData("PAY_TYPE",text,OpdRxSheetTool.getInstance().getPayTypeName(odo.getValue("CTZ1_CODE")));
           inParam.setData("MR_NO",text,"������:"+odo.getValue("MR_NO"));
           inParam.setData("PAT_NAME",text,"����:"+odo.getValue("PAT_NAME"));
           inParam.setData("SEX_CODE",text,"�Ա�:"+odo.getValue("SEX_CODE"));
           inParam.setData("BIRTHDAY",text,"��������:"+odo.getValue("BIRTHDAY"));
           inParam.setData("AGE",text,"����:"+odo.getValue("AGE"));
           inParam.setData("DEPT_CODE",text,"�Ʊ�:"+OpdRxSheetTool.getInstance().getDeptName(realDeptCode));
           inParam.setData("CLINIC_ROOM",text,"");
           inParam.setData("DR_CODE",text,"ҽ��:"+odo.getValue("DR_NAME"));
           inParam.setData("ADM_DATE",text,"����ʱ��:"+odo.getValue("ORDER_DATE"));
           inParam.setData("SESSION_CODE",text,"");
           inParam.setData("BAR_CODE",text,odo.getValue("MR_NO"));
           inParam.setData("ORDER_TYPE",text,"��ҽ");
           inParam.setData("RX_NO",rxNo);
           inParam.setData("CASE_NO",odo.getValue("CASE_NO"));
         */
        TTextFormat orgCode = (TTextFormat)this.getComponent("EXEC_DEPT_CODE");
        TParm parm = new TParm();
        parm.setData("ORG_CODE", orgCode.getSelectText());
        parm.setData("MR_NO", saveParm.getValue("MR_NO", 0));
        parm.setData("CASE_NO", saveParm.getValue("CASE_NO", 0));
        String drName = StringUtil.getDesc("SYS_OPERATOR", "USER_NAME",
                                           "USER_ID='" +
                                           saveParm.getValue("ORDER_DR_CODE", 0) +
                                           "'");
        if (StringUtil.isNullString(drName)) {
            this.messageBox_("ȡ��ҽʦ��Ϣʧ��");
            return;
        }
        parm.setData("DR_NAME", drName);
        parm.setData("ORDER_DATE",
                     StringTool.getString(saveParm.getTimestamp("ORDER_DATE", 0),
                                          "yyyy-MM-dd HH:mm"));
        String realDeptCode = saveParm.getValue("ORDER_DEPT_CODE", 0);
        String rxNo = saveParm.getValue("RX_NO", 0);
        TParm result = UddChnCheckTool.getInstance().getOrderPrintParm(
            realDeptCode, "3", parm, rxNo);
        if (result == null) {
            this.messageBox_("ȡ������ʧ��");
            return;
        }
        this.openPrintDialog(
            "%ROOT%\\config\\prt\\UDD\\UddChnDispenseSheet.jhw", result, false);

    }

    public static void main(String[] args) {
        //System.out.println(31/4*4+4);
        GregorianCalendar t = new GregorianCalendar();
        t.setTime(StringTool.getTimestamp("20090405", "yyyyMMdd"));
        //System.out.println(t.get(t.DAY_OF_WEEK));
    }

    public void setEnableMenu(){
        if (TypeTool.getBoolean(this.getValue("UNDONE"))) {
            callFunction("UI|save|setEnabled", true);
            callFunction("UI|delete|setEnabled", false);
        }else{
            callFunction("UI|save|setEnabled", false);
            callFunction("UI|delete|setEnabled", false);
        }
    }
}
