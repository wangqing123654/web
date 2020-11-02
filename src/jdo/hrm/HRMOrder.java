package jdo.hrm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import jdo.bil.BIL;
import jdo.hl7.Hl7Communications;
import jdo.odo.MedApply;
import jdo.reg.PatAdmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Personal;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
 * <p>Title: �������ҽ��</p>
 *
 * <p>Description: �������ҽ��</p>
 *
 * <p>Copyright: javahis 20090922</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMOrder extends TDataStore {

    //ȡ�õ���������
    private static final String GET_REPORT_DATE_OLD =
            "SELECT A.ORDER_DESC,'' EXEC_DR,C.CHN_DESC COL1,'' COL2,A.ORDER_CODE,A.DR_NOTE "
                    + " FROM HRM_ORDER A,SYS_FEE B,SYS_DICTIONARY C,(SELECT ORDER_CODE,CHECK_SEQ FROM HRM_PACKAGED WHERE PACKAGE_CODE = '#') D "
                    + "WHERE A.CASE_NO = '#' "
                    + "  AND ((A.ORDERSET_CODE IS NULL OR A.ORDERSET_CODE = '') OR A.SETMAIN_FLG = 'Y') "
                    + "  AND A.ORDER_CODE = B.ORDER_CODE(+) " + "AND B.OPTITEM_CODE = C.ID(+) "
                    + "  AND A.DEPT_ATTRIBUTE IS NULL " + "AND A.ORDER_CODE = D.ORDER_CODE(+) "
                    + "ORDER BY A.ORDER_CODE,D.CHECK_SEQ";// AND D.PACKAGE_CODE='#'
    
    //����CASE_NO�õ���������
    private static final String GET_DEPOSIT_BY_CASENO =
            "SELECT A.BILL_FLG,'+' DETAIL,A.MR_NO, B.PAT_NAME,SUM(A.OWN_AMT) OWN_AMT ,A.CASE_NO "
                    + " FROM HRM_ORDER A, HRM_PATADM B " 
                    + "WHERE B.COMPANY_CODE='#' "
                    + "  AND B.CONTRACT_CODE='#' "
                    + "  AND A.CASE_NO=B.CASE_NO "
                    + "  AND A.BILL_FLG ='N' "
                    + "GROUP BY A.BILL_FLG,A.MR_NO,B.PAT_NAME,A.CASE_NO "
                    + "ORDER BY A.CASE_NO";

    private String ctz;
    private Map map;
    private String caseNo;
    private HRMPatAdm adm=new HRMPatAdm();

    /**
     * MED_APPLY_NO���ϣ����桢��ѯʱ���
     */
    private Map labMap;
    private MedApply med;
    /**
     * ��ѯ�¼�
     *
     * @return
     */
    public int onQuery() {
        // ��ʼ��SQL
        this.setSQL("SELECT * FROM HRM_ORDER ORDER BY SEQ_NO");
        if(map==null){
            map=new HashMap();
        }
        if(labMap==null){
            labMap=new HashMap();
        }
        ctz=Personal.getDefCtz();
        return 0;
    }

    /**
     * ��ѯ�¼�
     * 
     * @return
     */
    public int onQuery(String caseNo, String mrNo) {
        //��ʼ��CASE_NO
        this.setSQL("SELECT * FROM HRM_ORDER WHERE CASE_NO='#' AND MR_NO='#' ORDER BY SEQ_NO".replaceFirst("#", caseNo).replaceFirst("#", mrNo));
        if (map == null) {
            map = new HashMap();
        }
        if (labMap == null) {
            labMap = new HashMap();
        }
        ctz = Personal.getDefCtz();
        return 0;
    }

    /**
     * ����CASE_NO��ʼ������
     * @param caseNo
     * @return
     */
    public int onQueryByCaseNo(String caseNo) {
        if (StringUtil.isNullString(caseNo)) {
            return -1;
        }
        // ���ݾ�����ų�ʼ��
        String sql = "SELECT * FROM HRM_ORDER WHERE CASE_NO='#' ORDER BY SEQ_NO".replaceFirst("#", caseNo);
        // System.out.println("sql======"+sql);
        this.setSQL(sql);
        if (map == null) {
            map = new HashMap();
        }
        if (labMap == null) {
            labMap = new HashMap();
        }
        ctz = Personal.getDefCtz();
        int result = this.retrieve();
        if (result < 0) {
            return result;
        }
        this.setFilter("EXEC_DR_CODE is null AND SETMAIN_FLG='Y' AND HIDE_FLG='N' AND EXEC_FLG='N'");
        this.filter();
        // this.filterObject(this, "filterObject");
        // System.out.println("in order");
        return 0;
    }

    /**
     * ����CASE_NO��ʼ������
     * 
     * @param caseNo
     * @return
     */
    public int onQueryByCaseNo(String caseNo, String type) {
        if (StringUtil.isNullString(caseNo)) {
            return -1;
        }
        // ���ݾ�����ų�ʼ��
        String sql = "SELECT * FROM HRM_ORDER WHERE CASE_NO='#' ORDER BY SEQ_NO".replaceFirst("#", caseNo);
        // System.out.println("sql======"+sql);
        this.setSQL(sql);
        if (map == null) {
            map = new HashMap();
        }
        if (labMap == null) {
            labMap = new HashMap();
        }
        ctz = Personal.getDefCtz();
        int result = this.retrieve();
        if (result < 0) {
            return result;
        }
        if ("Y".equals(type)) {
            this.setFilter(" EXEC_DR_CODE ='' AND SETMAIN_FLG='Y' AND HIDE_FLG='N' AND BILL_FLG='N' AND EXEC_FLG='N'");
        }
        if ("N".equals(type)) {
            this.setFilter(" EXEC_DR_CODE ='' AND BILL_FLG='N' AND EXEC_FLG='N'");
        }
        this.filter();
        // this.filterObject(this, "filterObject");
        // System.out.println("in order");
        return 0;
    }

    /**
     * �ܼ�ʱ���ݸ���CASE_NO��ѯ
     * @param caseNo
     * @return
     */
    public int onQueryBaCaseNoForTot(String caseNo) {
        if (StringUtil.isNullString(caseNo)) {
            return -1;
        }
        // ���ݾ�����ų�ʼ��
        String sql = "SELECT * FROM HRM_ORDER WHERE CASE_NO='#' ORDER BY SEQ_NO".replaceFirst("#", caseNo);
        // System.out.println("onQueryBaCaseNoForTot.sql======"+sql);
        this.setSQL(sql);
        if (map == null) {
            map = new HashMap();
        }
        if (labMap == null) {
            labMap = new HashMap();
        }
        ctz = Personal.getDefCtz();
        int result = this.retrieve();
        if (result < 0) {
            return result;
        }
        this.setFilter(" SETMAIN_FLG='Y' AND HIDE_FLG='N'");
        this.filter();
        // this.filterObject(this, "filterObject");
        // *("in order");
        return 0;
    }
    /**
     * ����Ʊ�ݺų�ʼ��
     * @param receiptNo
     * @return
     */
    public boolean onQueryByReceiptNo(String receiptNo) {
        if (StringUtil.isNullString(receiptNo)) {
            return false;
        }
        // ����Ʊ�ݺų�ʼ��
        String sql = "SELECT * FROM HRM_ORDER WHERE RECEIPT_NO='#' ORDER BY SEQ_NO".replaceFirst("#", receiptNo);
        // System.out.println("INIT_BY_RECEIPTNO"+sql);
        this.setSQL(sql);
        this.retrieve();
        this.med = new MedApply();
        if (map == null) {
            this.map = new HashMap();
        }
        if (labMap == null) {
            labMap = new HashMap();
        }
        med.onQueryByCaseNo(this.getItemString(0, "CASE_NO"));
        return true;
    }
    
    /**
     * ���ݽ��㵥�ų�ʼ��
     * @param BillNo
     * @return
     */
    public boolean onQueryByBillNo(String BillNo) {//add by wanglong 20130415
        if (StringUtil.isNullString(BillNo)) {
            return false;
        }
        // ���ݽ��㵥�ų�ʼ��
        String sql = "SELECT * FROM HRM_ORDER WHERE BILL_NO='#' ORDER BY SEQ_NO".replaceFirst("#", BillNo);
        this.setSQL(sql);
        this.retrieve();
        this.med = new MedApply();
        if (map == null) {
            this.map = new HashMap();
        }
        if (labMap == null) {
            labMap = new HashMap();
        }
//        med.onQueryByCaseNo(this.getItemString(0, "CASE_NO"));
        return true;
    }
    
    /**
     * ��ѯ
     * @param caseNo
     * @return
     */
    public int onQuerByCaseNoForBill(String caseNo) {
        if (StringUtil.isNullString(caseNo)) {
            return -1;
        }
        // ���ݾ�����ų�ʼ��
        String sql = "SELECT * FROM HRM_ORDER WHERE CASE_NO='#' ORDER BY SEQ_NO".replaceFirst("#", caseNo);
        this.setSQL(sql);
        if (map == null) {
            map = new HashMap();
        }
        if (labMap == null) {
            labMap = new HashMap();
        }
        ctz = Personal.getDefCtz();
        int result = this.retrieve();
        return result;
    }
    /**
     * ����contractCode��ѯҽ��
     * @param contractCode
     * @return
     */
    public boolean onQueryByContractCode(String contractCode) {
        if (StringUtil.isNullString(contractCode)) {
            return false;
        }
        String sql = "SELECT * FROM HRM_ORDER WHERE CONTRACT_CODE='#' ORDER BY CASE_NO,SEQ_NO".replaceFirst("#", contractCode);
        this.setSQL(sql);
        this.retrieve();
        return true;
    }
    /**
     * ����MrNo��ѯҽ��
     * @param mrNo
     * @return
     */
    public boolean onQueryByMrNo(String mrNo) {//add by wanglong 20130317
        if (StringUtil.isNullString(mrNo)) {
            return false;
        }
        String sql = "SELECT * FROM HRM_ORDER WHERE MR_NO='#' ORDER BY CASE_NO,SEQ_NO".replaceFirst("#", mrNo);
        this.setSQL(sql);
        this.retrieve();
        return true;
    }
    /**
     * ���ݲ����ų�ʼ��HRM_ORDER
     * @param mrNo
     * @return
     */
    public boolean onQuery(String mrNo) {
        if (StringUtil.isNullString(mrNo)) {
            return false;
        }
        String caseNo = HRMPatAdm.getLatestCaseNo(mrNo);
        if (StringUtil.isNullString(caseNo)) {
            return false;
        }
        if (labMap == null) {
            labMap = new HashMap();
        }
        // ���ݾ�����ų�ʼ��
        this.setSQL("SELECT * FROM HRM_ORDER WHERE CASE_NO='#' ORDER BY SEQ_NO".replace("#", caseNo));
        this.retrieve();
        return true;
    }
    /**
     * ����¼�
     */
    public void onClear() {
        this.filt("#");
    }
    /**
     * ���ݸ����ľ�����Ź���
     * @param caseNo
     * @return boolean
     */
    public boolean filt(String caseNo) {
        if (StringUtil.isNullString(caseNo)) {
            return false;
        }
        this.caseNo = caseNo;
        this.setFilter("CASE_NO='" + caseNo + "' AND BILL_FLG='N' AND EXEC_FLG='N'");
        // boolean result=this.filter();
        filterObject(this, "filter1");
        int count = this.rowCount();
        for (int i = 0; i < count; i++) {
            this.setActive(i, false);
        }
        // System.out.println("after filt");
        return true;
        // return result;
    }

    /**
     * ���˷���
     * 
     * @param parm TParm
     * @param row int
     * @return boolean
     */
    public boolean filter1(TParm parm, int row) {
        return caseNo.equals(parm.getValue("CASE_NO", row)) && !parm.getBoolean("BILL_FLG", row)
                && !parm.getBoolean("EXEC_FLG", row);
    }

    /**
     * ���ݸ����ľ�����Ź���
     * 
     * @param caseNo
     * @return boolean
     */
    public boolean filtCase(String caseNo) {
        if (StringUtil.isNullString(caseNo)) {
            return false;
        }
        this.caseNo = caseNo;
        // this.setFilter("CASE_NO='" +caseNo+"' AND BILL_FLG='N' AND EXEC_FLG='N'");
        // boolean result=this.filter();
        int count = this.rowCount();
        for (int i = 0; i < count; i++) {
            this.setActive(i, false);
        }
        // System.out.println("after filt");
        return true;
    }

    /**
     * ����CASE_NO��ó��ܼ�֮�������
     * @param caseNo
     * @return
     */
    public TParm getParmForReview(String caseNo) {
        TParm result = new TParm();
        if (StringUtil.isNullString(caseNo)) {
            return null;
        }
        this.caseNo = caseNo;

        String sql =
                "SELECT CASE WHEN (A.EXEC_DR_DESC IS NULL OR A.EXEC_DR_CODE = '') THEN 'N' ELSE 'Y' END DONE,'N' SFLG,A.* "
                        + "  FROM HRM_ORDER A " 
                        + " WHERE A.CASE_NO = '#' "
                        + "   AND A.SETMAIN_FLG = 'Y' "
                        + "ORDER BY SEQ_NO";
        sql = sql.replaceFirst("#", caseNo);
        // System.out.println("getParmForReview.sql="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    /**
     * �˷ѽ�����bill_flgʱ�������BILL_FLG��ΪN����ͬʱ���HRM_ORDER��BILL_NO
     * @return
     */
    public boolean setBillFlg(){
        return true;
    }
    /**
     * ���ݴ����sysFee�������
     * @param sysFee
     * @return
     */
    public boolean initOrder(TParm sysFee, TParm patParm, String flg, double discnt, int row) {
        // System.out.println("sysFee="+sysFee);
        String caseNo = sysFee.getValue("CASE_NO");
        // System.out.println("case_no="+caseNo);
        boolean isSetMain = sysFee.getBoolean("ORDERSET_FLG");
        String setMainStr = (isSetMain) ? "Y" : "N";
        String contractCode = sysFee.getValue("CONTRACT_CODE");
        double ownAmt = 0.0;
        double arAmt = 0.0;
        // int row=this.rowCount()-1;
        String orderCode = sysFee.getValue("ORDER_CODE");
        // this.setItem(row, "SEQ_NO", setSeqNoOrder(caseNo));
        this.setItem(row, "REGION_CODE", Operator.getRegion());
        this.setItem(row, "MR_NO", sysFee.getValue("MR_NO"));
        this.setItem(row, "ORDER_CODE", orderCode);
        this.setItem(row, "ORDER_DESC", sysFee.getValue("ORDER_DESC"));
        // System.out.println("order_desc="+sysFee.getValue("ORDER_DESC"));
        this.setItem(row, "GOODS_DESC", sysFee.getValue("GOODS_DESC"));
        this.setItem(row, "DISPENSE_QTY", 1.0);
        this.setItem(row, "CTZ1_CODE", ctz);
        this.setItem(row, "CTZ2_CODE", "");
        this.setItem(row, "CTZ3_CODE", "");
        this.setItem(row, "CONTRACT_CODE", contractCode);
        this.setItem(row, "DISPENSE_UNIT", sysFee.getValue("UNIT_CODE"));
        this.setItem(row, "NEW_FLG", sysFee.getValue("NEW_FLG"));
        this.setItem(row, "HEXP_CODE", sysFee.getValue("CHARGE_HOSP_CODE"));
        this.setItem(row, "REXP_CODE", BIL.getRexpCode(sysFee.getValue("CHARGE_HOSP_CODE"), "H"));
        this.setItem(row, "SPECIFICATION", sysFee.getValue("SPECIFICATION"));
        this.setItem(row, "ORDER_CAT1_CODE", sysFee.getValue("ORDER_CAT1_CODE"));
        this.setItem(row, "CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
        this.setItem(row, "OWN_PRICE", sysFee.getData("OWN_PRICE"));
        TParm selLevelParm = new TParm();
        selLevelParm.setData("CASE_NO", caseNo);
        TParm selLevel = PatAdmTool.getInstance().selectdata(selLevelParm);
        String level = selLevel.getValue("SERVICE_LEVEL", 0);
        ownAmt = BIL.getFee(orderCode, 1.0, level);
        this.setItem(row, "OWN_AMT", ownAmt);
        // ��������屨�����˻���
        if (flg != null && !flg.equals("")) {
            arAmt = StringTool.round(ownAmt * discnt, 2);
            this.setItem(row, "AR_AMT", arAmt);
        } else {
            this.setItem(row, "AR_AMT", ownAmt);
        }
        this.setItem(row, "NHI_PRICE", sysFee.getValue("NHI_PRICE"));
        this.setItem(row, "DR_CODE", Operator.getID());
        Timestamp now = this.getDBTime();
         this.setItem(row, "ORDER_DATE", now);//add by wanglong 20131114
        this.setItem(row, "DEPT_CODE", Operator.getDept());
        if (StringUtil.isNullString(sysFee.getValue("EXEC_DEPT_CODE"))) {
            this.setItem(row, "EXEC_DEPT_CODE", Operator.getDept());
        } else {
            this.setItem(row, "EXEC_DEPT_CODE", sysFee.getValue("EXEC_DEPT_CODE"));
        }
        this.setItem(row, "SETMAIN_FLG", setMainStr);
        int groupNo = getMaxGroupNo();
        if (isSetMain) {
            this.setItem(row, "ORDERSET_GROUP_NO", groupNo);
            this.setItem(row, "ORDERSET_CODE", orderCode);
            this.setItem(row, "HIDE_FLG", "N");
        }
        // HIDE_FLG
        this.setItem(row, "RPTTYPE_CODE", sysFee.getData("RPTTYPE_CODE"));
        this.setItem(row, "OPTITEM_CODE", sysFee.getData("OPTITEM_CODE"));
        this.setItem(row, "DEV_CODE", sysFee.getData("DEV_CODE"));
        this.setItem(row, "MR_CODE", sysFee.getData("MR_CODE"));
        this.setItem(row, "DEGREE_CODE", sysFee.getData("DEGREE_CODE"));
        this.setItem(row, "OPT_USER", Operator.getID());
        this.setItem(row, "OPT_DATE", now);
        this.setItem(row, "OPT_TERM", Operator.getIP());
        String labMapKey = "";
        if (isSetMain && StringUtil.isNullString(sysFee.getValue("DEPT_ATTRIBUTE"))) {
            labMapKey = this.getLabNo(row, patParm);
            this.setItem(row, "MED_APPLY_NO", labMapKey);
        }
        this.setActive(row, true);
        // ����ҽ����ҩƷ�������
        if (isSetMain) {
            String sql = // ��������ҽ��ϸ��
                    "SELECT B.DOSAGE_QTY,A.* "
                            + "  FROM SYS_ORDERSETDETAIL B,SYS_FEE A "
                            + " WHERE B.ORDERSET_CODE='#' "
                            + "   AND B.ORDER_CODE=A.ORDER_CODE".replace("#", sysFee.getValue("ORDER_CODE"));
            TParm orderSet = new TParm(TJDODBTool.getInstance().select(sql));
            if (orderSet.getErrCode() != 0) {
                // System.out.println("orderSet.getErrText="+orderSet.getErrText());
                return false;
            }
            int count = orderSet.getCount("ORDER_CODE");
            for (int i = 0; i < count; i++) {
                row = this.insertRow(-1, caseNo);
                // this.setItem(row, "SEQ_NO", getMaxSeqNo(caseNo));
                this.setItem(row, "REGION_CODE", Operator.getRegion());
                this.setItem(row, "MR_NO", sysFee.getValue("MR_NO"));
                this.setItem(row, "ORDER_CODE", orderSet.getValue("ORDER_CODE", i));
                this.setItem(row, "ORDER_DESC", orderSet.getValue("ORDER_DESC", i));
                this.setItem(row, "GOODS_DESC", orderSet.getValue("GOODS_DESC", i));
                this.setItem(row, "DISPENSE_QTY", orderSet.getValue("DOSAGE_QTY", i));
                this.setItem(row, "DISPENSE_UNIT", orderSet.getValue("UNIT_CODE", i));
                this.setItem(row, "SPECIFICATION", orderSet.getValue("SPECIFICATION", i));
                this.setItem(row, "ORDER_CAT1_CODE", orderSet.getValue("ORDER_CAT1_CODE", i));
                this.setItem(row, "CTZ1_CODE", ctz);
                this.setItem(row, "CTZ2_CODE", "");
                this.setItem(row, "CTZ3_CODE", "");
                this.setItem(row, "HEXP_CODE", orderSet.getValue("CHARGE_HOSP_CODE", i));
                this.setItem(row, "REXP_CODE", BIL.getRexpCode(orderSet.getValue("CHARGE_HOSP_CODE", i), "H"));
                this.setItem(row, "CAT1_TYPE", orderSet.getValue("CAT1_TYPE", i));
                this.setItem(row, "OWN_PRICE", orderSet.getValue("OWN_PRICE", i));
                double tempOwnAmt = StringTool.round(this.getItemDouble(row, "DISPENSE_QTY")
                                  * this.getItemDouble(row, "OWN_PRICE"), 2);
                this.setItem(row, "OWN_AMT", tempOwnAmt);
                if (flg != null && !flg.equals("")) {// ��������屨�����˻���
                    arAmt = StringTool.round(tempOwnAmt * discnt, 2);
                    this.setItem(row, "AR_AMT", arAmt);
                } else {
                    this.setItem(row, "AR_AMT", tempOwnAmt);
                }
                this.setItem(row, "NHI_PRICE", orderSet.getValue("NHI_PRICE", i));
                this.setItem(row, "DR_CODE", Operator.getID());
                this.setItem(row, "NEW_FLG", sysFee.getValue("NEW_FLG"));
                this.setItem(row, "ORDER_DATE", now);//add by wanglong 20131114
                // DEPT_CODE
                this.setItem(row, "DEPT_CODE", sysFee.getValue("DEPT_CODE"));
                this.setItem(row, "EXEC_DEPT_CODE", orderSet.getValue("EXEC_DEPT_CODE", i));
                // EXEC_DR_CODE
                this.setItem(row, "SETMAIN_FLG", "N");
                this.setItem(row, "ORDERSET_GROUP_NO", groupNo);
                this.setItem(row, "ORDERSET_CODE", orderCode);
                this.setItem(row, "HIDE_FLG", "Y");
                this.setItem(row, "RPTTYPE_CODE", orderSet.getData("RPTTYPE_CODE", i));
                this.setItem(row, "OPTITEM_CODE", orderSet.getData("OPTITEM_CODE", i));
                this.setItem(row, "DEV_CODE", orderSet.getData("DEV_CODE", i));
                this.setItem(row, "MR_CODE", orderSet.getData("MR_CODE", i));
                // FILE_NO
                this.setItem(row, "DEGREE_CODE", orderSet.getData("DEGREE_CODE", i));
                // REQUEST_FLG
                // this.setItem(row, "REQUEST_FLG", sysFee.getData("REQUEST_FLG"));
                // REQUEST_NO
                // this.setItem(row, "REQUEST_NO", sysFee.getData("REQUEST_NO"));
                this.setItem(row, "OPT_USER", Operator.getID());
                this.setItem(row, "OPT_DATE", now);
                this.setItem(row, "OPT_TERM", Operator.getIP());
                // labMapKey=this.getLabNo(row, patParm);
                // if(StringUtil.isNullString(labMapKey)){
                //     return false;
                // }
                this.setItem(row, "MED_APPLY_NO", labMapKey);
                this.setActive(row, true);
            }
        }
        return true;
    }
    
    /**
     * �Ǽ���ҽ������һ��
     * @param sysFee
     * @param patParm
     * @param flg
     * @param discnt
     * @param row
     */
    public void initNotOrderSet(TParm sysFee, TParm patParm, String flg, double discnt, int row) {
        String caseNo = sysFee.getValue("CASE_NO");
        String contractCode = sysFee.getValue("CONTRACT_CODE");
        double ownAmt = 0.0;
        double arAmt = 0.0;
        String orderCode = sysFee.getValue("ORDER_CODE");
        this.setItem(row, "REGION_CODE", Operator.getRegion());
        this.setItem(row, "MR_NO", sysFee.getValue("MR_NO"));
        this.setItem(row, "ORDER_CODE", orderCode);
        this.setItem(row, "ORDER_DESC", sysFee.getValue("ORDER_DESC"));
        this.setItem(row, "ORDER_CAT1_CODE", sysFee.getValue("ORDER_CAT1_CODE"));
        this.setItem(row, "GOODS_DESC", sysFee.getValue("GOODS_DESC"));
        this.setItem(row, "DISPENSE_QTY", 1.0);
        this.setItem(row, "CTZ1_CODE", ctz);
        this.setItem(row, "CTZ2_CODE", "");
        this.setItem(row, "CTZ3_CODE", "");
        this.setItem(row, "CONTRACT_CODE", contractCode);
        this.setItem(row, "DISPENSE_UNIT", sysFee.getValue("UNIT_CODE"));
        this.setItem(row, "NEW_FLG", sysFee.getValue("NEW_FLG"));
        this.setItem(row, "HEXP_CODE", sysFee.getValue("CHARGE_HOSP_CODE"));
        this.setItem(row, "REXP_CODE", BIL.getRexpCode(sysFee.getValue("CHARGE_HOSP_CODE"), "H"));
        this.setItem(row, "SPECIFICATION", sysFee.getValue("SPECIFICATION"));
        this.setItem(row, "ORDER_CAT1_CODE", sysFee.getValue("ORDER_CAT1_CODE"));
        this.setItem(row, "CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
        this.setItem(row, "OWN_PRICE", sysFee.getData("OWN_PRICE"));
        TParm selLevelParm = new TParm();
        selLevelParm.setData("CASE_NO", caseNo);
        TParm selLevel = PatAdmTool.getInstance().selectdata(selLevelParm);
        String level = selLevel.getValue("SERVICE_LEVEL", 0);
        ownAmt = BIL.getFee(orderCode, 1.0, level);
        this.setItem(row, "OWN_AMT", ownAmt);
        // ��������屨�����˻���
        if (flg != null && !flg.equals("")) {
            arAmt = StringTool.round(ownAmt * discnt, 2);
            this.setItem(row, "AR_AMT", arAmt);
        } else {
            this.setItem(row, "AR_AMT", ownAmt);
        }
        this.setItem(row, "NHI_PRICE", sysFee.getValue("NHI_PRICE"));
        this.setItem(row, "DR_CODE", Operator.getID());
        Timestamp now = this.getDBTime();
        this.setItem(row, "ORDER_DATE", now);//add by wanglong 20131114
        this.setItem(row, "DEPT_CODE", Operator.getDept());
        if (StringUtil.isNullString(sysFee.getValue("EXEC_DEPT_CODE"))) {
            this.setItem(row, "EXEC_DEPT_CODE", Operator.getDept());
        } else {
            this.setItem(row, "EXEC_DEPT_CODE", sysFee.getValue("EXEC_DEPT_CODE"));
        }
        this.setItem(row, "SETMAIN_FLG", "Y");
        this.setItem(row, "RPTTYPE_CODE", sysFee.getData("RPTTYPE_CODE"));
        this.setItem(row, "OPTITEM_CODE", sysFee.getData("OPTITEM_CODE"));
        this.setItem(row, "DEV_CODE", sysFee.getData("DEV_CODE"));
        this.setItem(row, "MR_CODE", sysFee.getData("MR_CODE"));
        this.setItem(row, "DEGREE_CODE", sysFee.getData("DEGREE_CODE"));
        this.setItem(row, "OPT_USER", Operator.getID());
        this.setItem(row, "OPT_DATE", now);
        this.setItem(row, "OPT_TERM", Operator.getIP());
        this.setItem(row, "CASE_NO", caseNo);
        this.setItem(row, "SETMAIN_FLG", "Y");
        this.setItem(row, "HIDE_FLG", "N");
        this.setItem(row, "BILL_FLG", "N");
        this.setItem(row, "EXEC_FLG", "N");
        this.setActive(row, true);
    }
    /**
     * �����ݿ��ѯȡ��Ӧ�����GroupNo
     * @param caseNo   ����š�
     * @return
     */
    public int getOrderMaxGroupNo(String caseNo) {// add by wanglong 20130321
       String sql = "SELECT NVL(MAX(ORDERSET_GROUP_NO),0) AS GROUP_NO FROM HRM_ORDER WHERE CASE_NO='"+caseNo+"'";
       TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
       int maxGroupNo = parm.getInt("GROUP_NO",0);
       return maxGroupNo ;
    }
    /**
     * �����ݿ��ѯȡ��Ӧ�����SEQ
     * @param caseNo   ����š�
     * @return
     */
    public int getOrderMaxSeqNo(String caseNo){
       String sql = "SELECT NVL(MAX(SEQ_NO),0)+1 AS SEQ_NO FROM HRM_ORDER WHERE CASE_NO='"+caseNo+"'";
       TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
       int maxSeq = parm.getInt("SEQ_NO",0);
       return maxSeq ;
    }
    /**
     *
     * @param caseNo
     * @return
     */
    public int getMaxSeqNo(String caseNo) {
        TParm p = new TParm();
        if (isFilter()) p = getBuffer(FILTER);
        else p = getBuffer(PRIMARY);
        int count = p.getCount();
        int seq = 0;
        for (int i = 0; i < count; i++) {
            if (!caseNo.equals(p.getValue("CASE_NO", i))) continue;
            int x = p.getInt("SEQ_NO", i);
            if (seq < x) seq = x;
        }
        return seq + 1;
    }
    /**
     * ȡ����󼯺�ҽ�����
     * @return
     */
    public int getMaxGroupNo() {
        String filter = this.getFilter();
        this.setFilter("SETMAIN_FLG='Y'");
        this.filter();
        int count = this.rowCount();
        int no = -1;
        for (int i = 0; i < count; i++) {
            int temp = this.getItemInt(i, "ORDERSET_GROUP_NO");
            if (temp > no) no = temp;
        }
        this.setFilter(filter);
        this.filter();
        return (no + 1);
    }
    /**
     * ȡ���ܽ��,û�й�������
     * @return
     */
    public double getArAmt() {
        double arAmt = 0.0;
        String filterString = this.getFilter();
        String caseNo = this.getItemString(0, "CASE_NO");
        this.setFilter("CASE_NO='" + caseNo + "' AND BILL_FLG='N'  ");
        this.filter();
        // System.out.println(filterString+"xueyf sub=="+this.getSQL() );
        this.getUpdateSQL();
        int count = this.rowCount();
        for (int i = 0; i < count; i++) {
            arAmt += this.getItemDouble(i, "AR_AMT");
            // System.out.println(arAmt+"xueyf AR_AMT==��"+i+"����������������������"+this.getItemDouble(i, "AR_AMT")+"      "+ arAmt);
        }
        arAmt = StringTool.round(arAmt, 2);
        this.setFilter(filterString);
        this.filter();
        // System.out.println(arAmt+"xueyf sub11=="+this.getSQL() );
        // System.out.println("���췵���ܼ�"+arAmt);
        return arAmt;
    }
    
    /**
     * ȡ���ܽ��,û�й�������
     * @return
     */
    public double getArAmt(String comPayFlg) {
        double arAmt = 0.0;
        String filterString = this.getFilter();
        String caseNo = this.getItemString(0, "CASE_NO");
        this.setFilter("CASE_NO='" + caseNo + "' AND BILL_FLG='N'  AND NEW_FLG='N' ");
        this.filter();
        // System.out.println(filterString+"xueyf sub=="+this.getSQL() );
        // this.getUpdateSQL();
        int count = this.rowCount();
        for (int i = 0; i < count; i++) {
            String name = this.getItemString(i, "ORDER_DESC");
            arAmt += StringTool.round(this.getItemDouble(i, "DISPENSE_QTY") * this.getItemDouble(i, "OWN_PRICE"), 2);
            // arAmt+=this.getItemDouble(i, "AR_AMT");
            // System.out.println(arAmt+"xueyf AR_AMT==��"+i+"����������������������"+this.getItemDouble(i, "AR_AMT")+"      "+ arAmt);
        }
        arAmt = StringTool.round(arAmt, 2);
        this.setFilter(filterString);
        this.filter();
        // System.out.println(arAmt+"xueyf sub11=="+this.getSQL() );
        // System.out.println("���췵���ܼ�"+arAmt);
        return arAmt;
    }
    
    /**
     * ��caseNoΪ�յ�������д��case_no
     * @return
     */
    public boolean supplementCaseNo(String caseNo) {
        if (StringUtil.isNullString(caseNo)) {
            return false;
        }
        int count = this.rowCount();
        for (int i = 0; i < count; i++) {
            String temp = this.getItemString(i, "CASE_NO");
            if ("#".equalsIgnoreCase(temp)) {
                this.setItem(i, "CASE_NO", caseNo);
            }
        }
        return true;
    }
    /**
     * �����к�ɾ��,�ж��Ǽ���ҽ������,������ҽ��ɾ��
     * @param row
     * @return
     */
    public boolean removeRow(int row) {
        // System.out.println("SETMAIN_FLG"+StringTool.getBoolean(this.getItemString(row, "SETMAIN_FLG")));
        if (StringTool.getBoolean(this.getItemString(row, "SETMAIN_FLG"))) {
            String filter = this.getFilter();
            // System.out.println("filter="+filter);
            String caseNo = this.getItemString(row, "CASE_NO");
            String orderCode = this.getItemString(row, "ORDER_CODE");
            this.setFilter("CASE_NO='" + caseNo + "' AND AND ORDERSET_CODE='" + orderCode + "'");
            this.filter();
            int count = this.rowCount() - 1;
            for (int i = count; i > -1; i--) {
                this.deleteRow(i);
            }
            this.setFilter(filter);
            this.filter();
        } else {
            this.deleteRow(row);
        }
        return true;
    }
    /**
     * ���ݸ����к�ɾ������ҽ��
     * @param row
     * @return
     */
    public boolean removeSetOrder(int row){
        if(row<0){
            // System.out.println("removeSetOrder.row<0");
            return false;
        }
        int count=this.rowCount();
        if(count<=0){
            // System.out.println("removeSetOrder.count<=0");
            return false;
        }
        if(!TypeTool.getBoolean(this.getItemData(row, "SETMAIN_FLG"))){
            // System.out.println("removeSetOrder setmainFlg is false");
            return false;
        }
        String orderSetCode=this.getItemString(row, "ORDER_CODE");
        String medApplyNo=this.getItemString(row, "MED_APPLY_NO");
        int groupNo=this.getItemInt(row, "ORDERSET_GROUP_NO");
        String filterString =this.getFilter();
        this.setFilter("CASE_NO='" +caseNo+"' AND BILL_FLG='N'");
        this.filter();
        count=this.rowCount();
        for(int i=count-1;i>-1;i--){
            if(orderSetCode.equalsIgnoreCase(this.getItemString(i, "ORDERSET_CODE"))&&groupNo==this.getItemInt(i, "ORDERSET_GROUP_NO")){
                this.deleteRow(i);
            }
        }
        this.setFilter(filterString);
        this.filter();
        return true;
    }
    /**
     *
     * @param parm
     * @return
     */
    public boolean initOrderByTParm(String packCode,String caseNo,String mrNo,String contractCode,TParm patParm){
        if (StringUtil.isNullString(packCode)) {
            return false;
        }
        TParm sysFee = HRMPackageD.getSysFeeByPack(packCode);
        // System.out.println("sysFee tot of parm="+sysFee);
        if (sysFee.getErrCode() != 0) {
            // System.out.println("HRMOrder.initOrderByTParm->sysFee is null");
            return false;
        }
        //===============add by wanglong 20130325
        String sql = "SELECT * FROM HRM_PACKAGEM WHERE PACKAGE_CODE='" + packCode + "'";
        // System.out.println("-----------sql---------------"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            return false;
        }
        String totMrCode = "";
        if (result.getCount() > 0) {
            totMrCode = result.getValue("TOT_MR_CODE", 0);
        }
        //===============add end
        int countFee = sysFee.getCount();
        for (int j = 0; j < countFee; j++) {
            TParm row = sysFee.getRow(j);
            row.setData("CASE_NO", caseNo);
            row.setData("MR_NO", mrNo);
            row.setData("CONTRACT_CODE", contractCode);
            if (row.getValue("DEPT_ATTRIBUTE").equals("04")
                    && row.getValue("HIDE_FLG").equals("N")) {// modify by wanglong 20130417
                row.setData("MR_CODE", totMrCode);
            }
            initOrderBySysFee(row, patParm);
        }
        // this.showDebug();
        return true;
    }
    
     /**
     *  ���˱������˶�Ӧ��ҽ��ϸ��
     * @param parm
     * @return
     */
    public boolean initOrderByTParmPerson(String packCode, String caseNo, String mrNo,
                                          String contractCode, TParm patParm) {
        if (StringUtil.isNullString(packCode)) {
            return false;
        }
        TParm sysFee = HRMPackageD.getSysFeeByPack(packCode);
        // System.out.println("sysFee tot of parm="+sysFee);
        if (sysFee.getErrCode() != 0) {
            // System.out.println("HRMOrder.initOrderByTParm->sysFee is null");
            return false;
        }
        int countFee = sysFee.getCount();
        for (int j = 0; j < countFee; j++) {
            TParm row = sysFee.getRow(j);
            row.setData("CASE_NO", caseNo);
            row.setData("MR_NO", mrNo);
            row.setData("CONTRACT_CODE", contractCode);
            int rows = this.isNewRow();
            if (rows < 0) {
                rows = this.insertRow();
                this.setItem(rows, "CASE_NO", caseNo);
            }
            initOrderBySysFeeP(row, patParm, rows);
        }
        if (isNewRow() < 0) {
            int rowid = this.insertRow();
            this.setItem(rowid, "CASE_NO", caseNo);
            this.setItem(rowid, "SETMAIN_FLG", "Y");
            this.setItem(rowid, "HIDE_FLG", "N");
            this.setItem(rowid, "BILL_FLG", "N");
            this.setItem(rowid, "EXEC_FLG", "N");
            this.setActive(rowid, false);
        }
        // this.showDebug();
        return true;
    }
    
    
    /**
     *�����и��˻���õ����˶�Ӧ��ҽ��ϸ��
     * @param parm
     * @return
     */
    public boolean initOrderByTParmP(String packCode, String caseNo, String mrNo,
                                     String contractCode, TParm patParm) {
        if (StringUtil.isNullString(packCode)) {
            return false;
        }
        TParm sysFee = HRMPackageD.getSysFeeByHrmOrder(caseNo);
        // System.out.println("sysFee tot of parm="+sysFee);
        if (sysFee.getErrCode() != 0) {
            // System.out.println("HRMOrder.initOrderByTParm->sysFee is null");
            return false;
        }
        int countFee = sysFee.getCount();
        for (int j = 0; j < countFee; j++) {
            TParm row = sysFee.getRow(j);
            row.setData("CASE_NO", caseNo);
            row.setData("MR_NO", mrNo);
            row.setData("CONTRACT_CODE", contractCode);
            int rows = this.isNewRow();
            if (rows < 0) {
                rows = this.insertRow();
                this.setItem(rows, "CASE_NO", caseNo);
            }
            initOrderBySysFeeP(row, patParm, rows);
        }
        if (isNewRow() < 0) {
            int rowid = this.insertRow();
            this.setItem(rowid, "CASE_NO", caseNo);
            this.setItem(rowid, "SETMAIN_FLG", "Y");
            this.setItem(rowid, "HIDE_FLG", "N");
            this.setItem(rowid, "BILL_FLG", "N");
            this.setItem(rowid, "EXEC_FLG", "N");
            this.setActive(rowid, false);
        }
        // this.showDebug();
        return true;
    }
    
    /**
     * ��������������Ŀ��Ӧ��ҽ��ϸ��
     * @param contractCode
     * @param mrNo
     * @param patParm
     * @return
     */
    public boolean initOrderByCaseNo(String contractCode, String mrNo,
                                     TParm patParm) {//add by wanglong 20130508
        if (StringUtil.isNullString(contractCode)||StringUtil.isNullString(mrNo)) {
            return false;
        }
        TParm sysFee = HRMPackageD.getSysFeeByHrmOrder(contractCode, mrNo);
        if (sysFee.getErrCode() != 0) {
            // System.out.println("HRMOrder.initOrderByTParm->sysFee is null");
            return false;
        }
        int count = sysFee.getCount();
        for (int i = 0; i < count; i++) {
            TParm row = sysFee.getRow(i);
            row.setData("CASE_NO", patParm.getValue("CASE_NO"));
            row.setData("MR_NO", patParm.getValue("MR_NO"));
            row.setData("CONTRACT_CODE", patParm.getValue("CONTRACT_CODE"));
            int rows = this.isNewRow();
            if (rows < 0) {
                rows = this.insertRow();
                this.setItem(rows, "CASE_NO", patParm.getValue("CASE_NO"));
            }
            initOrderBySysFeeC(row, patParm, rows);
        }
        if (isNewRow() < 0) {
            int rowid = this.insertRow();
            this.setItem(rowid, "CASE_NO", caseNo);
            this.setItem(rowid, "SETMAIN_FLG", "Y");
            this.setItem(rowid, "HIDE_FLG", "N");
            this.setItem(rowid, "BILL_FLG", "N");
            this.setItem(rowid, "EXEC_FLG", "N");
            this.setActive(rowid, false);
        }
        // this.showDebug();
        return true;
    }

    /**
     * �Ƿ�������
     * 
     * @return boolean
     */
    public int isNewRow() {
        int rowCount = this.rowCount();
        for (int i = 0; i < rowCount; i++) {
            if (!this.isActive(i)) return i;
        }
        return -1;
    }

    /**
     * ���ݸ���SYS_FEE���ݳ�ʼ��һ��ҽ����¼
     * 
     * @param sysFee
     * @return
     */
    private boolean initOrderBySysFeeP(TParm sysFee, TParm patParm, int row) {
        // System.out.println("patParm="+patParm);
        // System.out.println("sysFee="+sysFee);
        // System.out.println("this.rowCount()=="+this.rowCount());
        // System.out.println("row"+row);
        // if(row<0){
        //     row=this.insertRow(-1,sysFee.getValue("CASE_NO"));
        //     this.setActive(row,false);
        // }
        // System.out.println("initOrderBySysFee.row="+row);
        double ownAmt = 0.0;
        boolean isSetMain = sysFee.getBoolean("ORDERSET_FLG");
        String setMainStr = (isSetMain) ? "Y" : "N";
        String orderCode = sysFee.getValue("ORDER_CODE");
        String contractCode = sysFee.getValue("CONTRACT_CODE");
        this.setItem(row, "SEQ_NO", getMaxSeqNo(caseNo));
        this.setItem(row, "REGION_CODE", Operator.getRegion());
        this.setItem(row, "MR_NO", sysFee.getValue("MR_NO"));
        this.setItem(row, "ORDER_CODE", orderCode);
        this.setItem(row, "ORDER_DESC", sysFee.getValue("ORDER_DESC"));
        // System.out.println("order_desc="+sysFee.getValue("ORDER_DESC"));
        this.setItem(row, "GOODS_DESC", sysFee.getValue("GOODS_DESC"));
        this.setItem(row, "DISPENSE_QTY", this.getItemDouble(row, "DISPENSE_QTY"));
        this.setItem(row, "CTZ1_CODE", ctz);
        this.setItem(row, "CTZ2_CODE", "");
        this.setItem(row, "CONTRACT_CODE", contractCode);
        this.setItem(row, "CTZ3_CODE", "");
        this.setItem(row, "DISPENSE_QTY", sysFee.getData("DISPENSE_QTY"));
        this.setItem(row, "DISPENSE_UNIT", sysFee.getValue("DISPENSE_UNIT"));
        this.setItem(row, "HEXP_CODE", sysFee.getValue("CHARGE_HOSP_CODE"));
        this.setItem(row, "REXP_CODE", BIL.getRexpCode(sysFee.getValue("CHARGE_HOSP_CODE"), "H"));
        this.setItem(row, "SPECIFICATION", sysFee.getValue("SPECIFICATION"));
        this.setItem(row, "ORDER_CAT1_CODE", sysFee.getValue("ORDER_CAT1_CODE"));
        this.setItem(row, "CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
        this.setItem(row, "CONTRACT_CODE", sysFee.getValue("CONTRACT_CODE"));
        this.setItem(row, "OWN_PRICE", sysFee.getData("PACKAGE_PRICE"));
        ownAmt = StringTool.round(this.getItemDouble(row, "OWN_PRICE") * this.getItemDouble(row, "DISPENSE_QTY"), 2);
        this.setItem(row, "OWN_AMT", ownAmt);
        double discountRate = this.getItemDouble(row, "DISCOUNT_RATE");
        this.setItem(row, "AR_AMT", StringTool.round(ownAmt * patParm.getDouble("DISCNT"), 2));
        this.setItem(row, "DISCOUNT_RATE", discountRate);
        this.setItem(row, "NHI_PRICE", sysFee.getValue("NHI_PRICE"));
        this.setItem(row, "DR_CODE", Operator.getID());
        Timestamp now = this.getDBTime();
        this.setItem(row, "ORDER_DATE", now);//add by wanglong 20131114
        if (!StringUtil.isNullString(sysFee.getValue("DEPT_CODE"))) {
            this.setItem(row, "DEPT_CODE", sysFee.getValue("DEPT_CODE"));
        } else {
            this.setItem(row, "DEPT_CODE", Operator.getDept());
        }
        // System.out.println("sysFee.execDept="+sysFee.getValue("EXEC_DEPT_CODE"));
        if (!StringUtil.isNullString(sysFee.getValue("HRM_EXE_DEPT"))) {
            this.setItem(row, "EXEC_DEPT_CODE", sysFee.getValue("HRM_EXE_DEPT"));
        } else {
            this.setItem(row, "EXEC_DEPT_CODE", Operator.getDept());
        }
        // EXEC_DR_CODE
        this.setItem(row, "NEW_FLG", sysFee.getValue("NEW_FLG"));
        // �����ҩ��������SETMAIN_FLGΪY������ʾ
        if ("PHA_W".equals(sysFee.getValue("ORDER_CAT1_CODE"))
                || ("MAT".equals(sysFee.getValue("ORDER_CAT1_CODE")) 
                && "N".equals(sysFee.getValue("HIDE_FLG")))) {
            this.setItem(row, "SETMAIN_FLG", "Y");
        } else {
            this.setItem(row, "SETMAIN_FLG", setMainStr);
        }
        int groupNo = getMaxGroupNo();
        // ����
        if ("PHA_W".equals(sysFee.getValue("ORDER_CAT1_CODE"))
                || ("MAT".equals(sysFee.getValue("ORDER_CAT1_CODE")) 
                && "N".equals(sysFee.getValue("HIDE_FLG")))) {
            this.setItem(row, "ORDERSET_GROUP_NO", 0);
            this.setItem(row, "ORDERSET_CODE", orderCode);
        } else {
            if (isSetMain) {
                this.setItem(row, "ORDERSET_GROUP_NO", groupNo);
                this.setItem(row, "ORDERSET_CODE", orderCode);
            } else {
                this.setItem(row, "ORDERSET_GROUP_NO", this.getItemData(row - 1, "ORDERSET_GROUP_NO"));
                this.setItem(row, "ORDERSET_CODE", this.getItemData(row - 1, "ORDERSET_CODE"));
            }
        }
        this.setItem(row, "HIDE_FLG", sysFee.getData("HIDE_FLG"));
        this.setItem(row, "RPTTYPE_CODE", sysFee.getData("RPTTYPE_CODE"));
        this.setItem(row, "OPTITEM_CODE", sysFee.getData("OPTITEM_CODE"));
        this.setItem(row, "DEV_CODE", sysFee.getData("DEV_CODE"));
        this.setItem(row, "MR_CODE", sysFee.getData("MR_CODE"));
        // FILE_NO
        this.setItem(row, "DEGREE_CODE", sysFee.getData("DEGREE_CODE"));
        // System.out.println("deptAttribute=-==="+sysFee.getData("DEPT_ATTRIBUTE"));
        this.setItem(row, "DEPT_ATTRIBUTE", sysFee.getData("DEPT_ATTRIBUTE"));
        // REXP_CODE
        // HEXP_CODE
        // REQUEST_FLG
        // this.setItem(row, "RPTTYPE_CODE", sysFee.getData("RPTTYPE_CODE"));
        // REQUEST_NO
        // this.setItem(row, "RPTTYPE_CODE", sysFee.getData("RPTTYPE_CODE"));
        this.setItem(row, "OPT_USER", Operator.getID());
        this.setItem(row, "OPT_DATE", now);
        this.setItem(row, "OPT_TERM", Operator.getIP());
        // System.out.println("setmainStr="+setMainStr);
        if (StringUtil.isNullString(sysFee.getValue("DEPT_ATTRIBUTE"))
                && "Y".equalsIgnoreCase(setMainStr)
                && ("LIS".equals(sysFee.getValue("CAT1_TYPE")) 
                || "RIS".equals(sysFee.getValue("CAT1_TYPE")))) {
            if (StringUtil.isNullString(sysFee.getValue("MED_APPLY_NO"))) {
                String labNo = this.getLabNo(row, patParm);
                this.setItem(row, "MED_APPLY_NO", labNo);
            } else {
                this.setItem(row, "MED_APPLY_NO", sysFee.getValue("MED_APPLY_NO"));
            }
        }
        this.setActive(row, true);
        return true;
    }
    
    /**
     * ���ݸ���SYS_FEE���ݳ�ʼ��һ��ҽ����¼(���ڸ���ҽ��)
     * 
     * @param sysFee
     * @return
     */
    private boolean initOrderBySysFeeC(TParm sysFee, TParm patParm, int row) {//add by wanglong 20130509
        double ownAmt = 0.0;
        double arAmt = 0.0;
        boolean isSetMain = sysFee.getBoolean("ORDERSET_FLG");
        String setMainStr = (isSetMain) ? "Y" : "N";
        String orderCode = sysFee.getValue("ORDER_CODE");
        String contractCode = sysFee.getValue("CONTRACT_CODE");
        this.setItem(row, "SEQ_NO", getMaxSeqNo(caseNo));
        this.setItem(row, "REGION_CODE", Operator.getRegion());
        this.setItem(row, "MR_NO", sysFee.getValue("MR_NO"));
        this.setItem(row, "ORDER_CODE", orderCode);
        this.setItem(row, "ORDER_DESC", sysFee.getValue("ORDER_DESC"));
        // System.out.println("order_desc="+sysFee.getValue("ORDER_DESC"));
        this.setItem(row, "GOODS_DESC", sysFee.getValue("GOODS_DESC"));
        this.setItem(row, "DISPENSE_QTY", this.getItemDouble(row, "DISPENSE_QTY"));
        this.setItem(row, "CTZ1_CODE", ctz);
        this.setItem(row, "CTZ2_CODE", "");
        this.setItem(row, "CONTRACT_CODE", contractCode);
        this.setItem(row, "CTZ3_CODE", "");
        this.setItem(row, "DISPENSE_QTY", sysFee.getData("DISPENSE_QTY"));
        this.setItem(row, "DISPENSE_UNIT", sysFee.getValue("DISPENSE_UNIT"));
        this.setItem(row, "HEXP_CODE", sysFee.getValue("CHARGE_HOSP_CODE"));
        this.setItem(row, "REXP_CODE", BIL.getRexpCode(sysFee.getValue("CHARGE_HOSP_CODE"), "H"));
        this.setItem(row, "SPECIFICATION", sysFee.getValue("SPECIFICATION"));
        this.setItem(row, "ORDER_CAT1_CODE", sysFee.getValue("ORDER_CAT1_CODE"));
        this.setItem(row, "CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
        this.setItem(row, "CONTRACT_CODE", sysFee.getValue("CONTRACT_CODE"));
        this.setItem(row, "OWN_PRICE", sysFee.getData("PACKAGE_PRICE"));
        ownAmt = StringTool.round(this.getItemDouble(row, "OWN_PRICE") * this.getItemDouble(row, "DISPENSE_QTY"), 2);
        this.setItem(row, "OWN_AMT", ownAmt);
        if (sysFee.getValue("CAT1_TYPE").equals("PHA")
//                || sysFee.getValue("ORDER_CAT1_CODE").equals("MAT")//̩�Ĳ����ܴ���
                ) {// modify by wanglong 20130306
            arAmt = ownAmt;
        } else
        arAmt = StringTool.round(ownAmt * patParm.getDouble("DISCNT"), 2);
        this.setItem(row, "AR_AMT", arAmt);
        if (sysFee.getValue("CAT1_TYPE").equals("PHA")
//                || sysFee.getValue("ORDER_CAT1_CODE").equals("MAT")//̩�Ĳ����ܴ���
                ) {// modify by wanglong 20130306
            this.setItem(row, "DISCOUNT_RATE", 1);
        } else
        this.setItem(row, "DISCOUNT_RATE", patParm.getDouble("DISCNT"));
        this.setItem(row, "NHI_PRICE", sysFee.getValue("NHI_PRICE"));
        this.setItem(row, "DR_CODE", Operator.getID());
        Timestamp now = this.getDBTime();
        this.setItem(row, "ORDER_DATE", now);
        this.setItem(row, "DEPT_CODE", Operator.getDept());
        // System.out.println("sysFee.execDept="+sysFee.getValue("EXEC_DEPT_CODE"));
        if (isSetMain
                || "PHA_W".equals(sysFee.getValue("ORDER_CAT1_CODE"))
                || ("MAT".equals(sysFee.getValue("ORDER_CAT1_CODE")) && "N".equals(sysFee
                        .getValue("HIDE_FLG")))) {//modify by wanglong 20130507
            if (!StringUtil.isNullString(sysFee.getValue("HRM_EXE_DEPT"))) {
                this.setItem(row, "EXEC_DEPT_CODE", sysFee.getValue("HRM_EXE_DEPT"));
            } else if (!StringUtil.isNullString(sysFee.getValue("EXEC_DEPT_CODE"))) {
                this.setItem(row, "EXEC_DEPT_CODE", sysFee.getValue("EXEC_DEPT_CODE"));
            } else {
                this.setItem(row, "EXEC_DEPT_CODE", Operator.getDept());
            }
        } else {
            this.setItem(row, "EXEC_DEPT_CODE", this.getItemData(row - 1, "EXEC_DEPT_CODE"));
        }
        // EXEC_DR_CODE
        this.setItem(row, "NEW_FLG", sysFee.getValue("NEW_FLG"));
        // �����ҩ��������SETMAIN_FLGΪY������ʾ
        if ("PHA_W".equals(sysFee.getValue("ORDER_CAT1_CODE"))
                || ("MAT".equals(sysFee.getValue("ORDER_CAT1_CODE")) 
                && "N".equals(sysFee.getValue("HIDE_FLG")))) {
            this.setItem(row, "SETMAIN_FLG", "Y");
        } else {
            this.setItem(row, "SETMAIN_FLG", setMainStr);
        }
        int groupNo = getMaxGroupNo();
        if (isSetMain) {
            this.setItem(row, "ORDERSET_GROUP_NO", groupNo);
            this.setItem(row, "ORDERSET_CODE", orderCode);
        } else {
            // �����ҩƷ
            if ("PHA_W".equals(sysFee.getValue("ORDER_CAT1_CODE"))
                    || ("MAT".equals(sysFee.getValue("ORDER_CAT1_CODE")) 
                    && "N".equals(sysFee.getValue("HIDE_FLG")))) {
                this.setItem(row, "ORDERSET_GROUP_NO", 0);
                this.setItem(row, "ORDERSET_CODE", this.getItemData(row, "ORDER_CODE"));
            } else {
                this.setItem(row, "ORDERSET_GROUP_NO", this.getItemData(row - 1, "ORDERSET_GROUP_NO"));
                this.setItem(row, "ORDERSET_CODE", this.getItemData(row - 1, "ORDERSET_CODE"));
            }
        }
        this.setItem(row, "HIDE_FLG", sysFee.getData("HIDE_FLG"));
        this.setItem(row, "RPTTYPE_CODE", sysFee.getData("RPTTYPE_CODE"));
        this.setItem(row, "OPTITEM_CODE", sysFee.getData("OPTITEM_CODE"));
        this.setItem(row, "DEV_CODE", sysFee.getData("DEV_CODE"));
        this.setItem(row, "MR_CODE", sysFee.getData("MR_CODE"));
        // FILE_NO
        this.setItem(row, "DEGREE_CODE", sysFee.getData("DEGREE_CODE"));
        this.setItem(row, "DEPT_ATTRIBUTE", sysFee.getData("DEPT_ATTRIBUTE"));
        // REXP_CODE
        // HEXP_CODE
        // REQUEST_FLG
        // this.setItem(row, "RPTTYPE_CODE", sysFee.getData("RPTTYPE_CODE"));
        // REQUEST_NO
        // this.setItem(row, "RPTTYPE_CODE", sysFee.getData("RPTTYPE_CODE"));
        this.setItem(row, "OPT_USER", Operator.getID());
        this.setItem(row, "OPT_DATE", now);
        this.setItem(row, "OPT_TERM", Operator.getIP());
        // System.out.println("setmainStr="+setMainStr);
        if ("Y".equalsIgnoreCase(setMainStr) && ("LIS".equals(sysFee.getValue("CAT1_TYPE")) || "RIS".equals(sysFee.getValue("CAT1_TYPE")))) {
            // System.out.println("=====sysFee===="+sysFee);
            String labNo = this.getLabNo(row, patParm);
            this.setItem(row, "MED_APPLY_NO", labNo);
        }
        this.setActive(row, true);
        return true;
    }
    
    /**
     * ���ݸ���SYS_FEE���ݳ�ʼ��һ��ҽ����¼
     * @param sysFee
     * @return
     */
    private boolean initOrderBySysFee(TParm sysFee, TParm patParm) {
        // System.out.println("patParm="+patParm);
        int row = this.rowCount() - 1;
        // System.out.println("row"+row);
        if (row < 0) {
            row = this.insertRow(-1, sysFee.getValue("CASE_NO"));
            this.setActive(row, false);
        }
        // System.out.println("initOrderBySysFee.row="+row);
        double ownAmt = 0.0;
        double arAmt = 0.0;
        boolean isSetMain = sysFee.getBoolean("ORDERSET_FLG");
        String setMainStr = (isSetMain) ? "Y" : "N";
        String orderCode = sysFee.getValue("ORDER_CODE");
        String contractCode = sysFee.getValue("CONTRACT_CODE");
        this.setItem(row, "SEQ_NO", getMaxSeqNo(sysFee.getValue("CASE_NO")));
        this.setItem(row, "REGION_CODE", Operator.getRegion());
        this.setItem(row, "MR_NO", sysFee.getValue("MR_NO"));
        this.setItem(row, "ORDER_CODE", orderCode);
        this.setItem(row, "ORDER_DESC", sysFee.getValue("ORDER_DESC"));
        // System.out.println("order_desc="+sysFee.getValue("ORDER_DESC"));
        this.setItem(row, "GOODS_DESC", sysFee.getValue("GOODS_DESC"));
        this.setItem(row, "DISPENSE_QTY", 1.0);
        this.setItem(row, "CTZ1_CODE", ctz);
        this.setItem(row, "CTZ2_CODE", "");
        this.setItem(row, "CONTRACT_CODE", contractCode);
        this.setItem(row, "CTZ3_CODE", "");
        this.setItem(row, "DISPENSE_QTY", sysFee.getData("DISPENSE_QTY"));
        this.setItem(row, "DISPENSE_UNIT", sysFee.getValue("DISPENSE_UNIT"));
        this.setItem(row, "HEXP_CODE", sysFee.getValue("CHARGE_HOSP_CODE"));
        this.setItem(row, "REXP_CODE", BIL.getRexpCode(sysFee.getValue("CHARGE_HOSP_CODE"), "H"));
        this.setItem(row, "SPECIFICATION", sysFee.getValue("SPECIFICATION"));
        this.setItem(row, "ORDER_CAT1_CODE", sysFee.getValue("ORDER_CAT1_CODE"));
        this.setItem(row, "CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
        this.setItem(row, "CONTRACT_CODE", sysFee.getValue("CONTRACT_CODE"));
        this.setItem(row, "OWN_PRICE", sysFee.getData("PACKAGE_PRICE"));
        ownAmt = StringTool.round(this.getItemDouble(row, "OWN_PRICE") * this.getItemDouble(row, "DISPENSE_QTY"), 2);
        this.setItem(row, "OWN_AMT", ownAmt);
        if (sysFee.getValue("CAT1_TYPE").equals("PHA")
//                || sysFee.getValue("ORDER_CAT1_CODE").equals("MAT")//̩�Ĳ����ܴ���
                ) {// modify by wanglong 20130306
            arAmt = ownAmt;
        } else
        arAmt = StringTool.round(ownAmt * patParm.getDouble("DISCNT"), 2);
        this.setItem(row, "AR_AMT", arAmt);
        if (sysFee.getValue("CAT1_TYPE").equals("PHA")
//                || sysFee.getValue("ORDER_CAT1_CODE").equals("MAT")//̩�Ĳ����ܴ���
                ) {// modify by wanglong 20130306
            this.setItem(row, "DISCOUNT_RATE", 1);
        } else
        this.setItem(row, "DISCOUNT_RATE", patParm.getDouble("DISCNT"));
        this.setItem(row, "NHI_PRICE", sysFee.getValue("NHI_PRICE"));
        this.setItem(row, "DR_CODE", Operator.getID());
        Timestamp now = this.getDBTime();
        this.setItem(row, "ORDER_DATE", now);
        this.setItem(row, "DEPT_CODE", Operator.getDept());
        // System.out.println("sysFee.execDept="+sysFee.getValue("EXEC_DEPT_CODE"));
        if (isSetMain
                || "PHA_W".equals(sysFee.getValue("ORDER_CAT1_CODE"))
                || ("MAT".equals(sysFee.getValue("ORDER_CAT1_CODE")) && "N".equals(sysFee
                        .getValue("HIDE_FLG")))) {//modify by wanglong 20130507
            if (!StringUtil.isNullString(sysFee.getValue("HRM_EXE_DEPT"))) {
                this.setItem(row, "EXEC_DEPT_CODE", sysFee.getValue("HRM_EXE_DEPT"));
            } else if (!StringUtil.isNullString(sysFee.getValue("EXEC_DEPT_CODE"))) {
                this.setItem(row, "EXEC_DEPT_CODE", sysFee.getValue("EXEC_DEPT_CODE"));
            } else {
                this.setItem(row, "EXEC_DEPT_CODE", Operator.getDept());
            }
        } else {
            this.setItem(row, "EXEC_DEPT_CODE", this.getItemData(row - 1, "EXEC_DEPT_CODE"));
        }
        // �����ҩ��������SETMAIN_FLGΪY������ʾ
        if ("PHA_W".equals(sysFee.getValue("ORDER_CAT1_CODE"))
                || ("MAT".equals(sysFee.getValue("ORDER_CAT1_CODE")) 
                && "N".equals(sysFee.getValue("HIDE_FLG")))) {
            this.setItem(row, "SETMAIN_FLG", "Y");
        } else {
            this.setItem(row, "SETMAIN_FLG", setMainStr);
        }
        int groupNo = getMaxGroupNo();
        if (isSetMain) {
            this.setItem(row, "ORDERSET_GROUP_NO", groupNo);
            this.setItem(row, "ORDERSET_CODE", orderCode);
        } else {
            // �����ҩƷ
            if ("PHA_W".equals(sysFee.getValue("ORDER_CAT1_CODE"))
                    || ("MAT".equals(sysFee.getValue("ORDER_CAT1_CODE")) 
                    && "N".equals(sysFee.getValue("HIDE_FLG")))) {
                this.setItem(row, "ORDERSET_GROUP_NO", 0);
                this.setItem(row, "ORDERSET_CODE", this.getItemData(row, "ORDER_CODE"));
            } else {
                this.setItem(row, "ORDERSET_GROUP_NO", this.getItemData(row - 1, "ORDERSET_GROUP_NO"));
                this.setItem(row, "ORDERSET_CODE", this.getItemData(row - 1, "ORDERSET_CODE"));
            }
        }
        this.setItem(row, "HIDE_FLG", sysFee.getData("HIDE_FLG"));
        this.setItem(row, "RPTTYPE_CODE", sysFee.getData("RPTTYPE_CODE"));
        this.setItem(row, "OPTITEM_CODE", sysFee.getData("OPTITEM_CODE"));
        this.setItem(row, "DEV_CODE", sysFee.getData("DEV_CODE"));
        this.setItem(row, "MR_CODE", sysFee.getData("MR_CODE"));
        // FILE_NO
        this.setItem(row, "DEGREE_CODE", sysFee.getData("DEGREE_CODE"));
        this.setItem(row, "DEPT_ATTRIBUTE", sysFee.getData("DEPT_ATTRIBUTE"));
        // REXP_CODE
        // HEXP_CODE
        // REQUEST_FLG
        // this.setItem(row, "RPTTYPE_CODE", sysFee.getData("RPTTYPE_CODE"));
        // REQUEST_NO
        // this.setItem(row, "RPTTYPE_CODE", sysFee.getData("RPTTYPE_CODE"));
        this.setItem(row, "OPT_USER", Operator.getID());
        this.setItem(row, "OPT_DATE", now);
        this.setItem(row, "OPT_TERM", Operator.getIP());
        // System.out.println("setmainStr="+setMainStr);
        // System.out.println( StringUtil.isNullString(sysFee.getValue("DEPT_ATTRIBUTE"))
        // +":"+"Y".equalsIgnoreCase(setMainStr)+":"+("LIS".equals(sysFee.getValue("CAT1_TYPE"))||"RIS".equals(sysFee.getValue("CAT1_TYPE"))));
        // ȥ���Ʊ������ж�
        if ("Y".equalsIgnoreCase(setMainStr) && ("LIS".equals(sysFee.getValue("CAT1_TYPE")) || "RIS".equals(sysFee.getValue("CAT1_TYPE")))) {
            // System.out.println("=====sysFee===="+sysFee);
            String labNo = this.getLabNo(row, patParm);
            this.setItem(row, "MED_APPLY_NO", labNo);
        }
        this.setActive(row, true);
        row = this.insertRow(-1, sysFee.getValue("CASE_NO"));
        this.setActive(row, false);
        return true;
    }
    /**
     * �õ�����������
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TParm parm, int row, String column) {
        // ����ҽ����Ǽ���ҽ��(Y:����ҽ��)
        String orderSet = parm.getValue("ORDER_CAT1_CODE", row);
        if ("ORDER_SPECIFICATION".equalsIgnoreCase(column)) {
            return parm.getValue("ORDER_DESC", row) + " " + parm.getValue("SPECIFICATION", row);
        } else if ("EXEC".equalsIgnoreCase(column)) {
            return !StringUtil.isNullString(parm.getValue("EXEC_DR_CODE", row));
        } else if ("REFOUND".equalsIgnoreCase(column)) {
            return false;
        } else if ("OWN_PRICE_MAIN".equalsIgnoreCase(column)) {
            double totAmt = 0.0;
            String orderSetCode = parm.getValue("ORDERSET_CODE", row);
            String caseNo = parm.getValue("CASE_NO", row);
            int groupNo = parm.getInt("ORDERSET_GROUP_NO", row);
            String filterString = this.getFilter();
            String filter = "";
            if ("PHA_W".equals(orderSet)
                    || ("MAT".equals(orderSet) && "N".equals(parm.getValue("HIDE_FLG", row)))) {
                filter = "CASE_NO='" + caseNo + "' AND ORDER_CODE='" + parm.getValue("ORDER_CODE", row) + "' ";
                this.setFilter(filter);
                if (!this.filter()) {
                    return totAmt;
                }
                int count = this.rowCount();
                for (int i = 0; i < count; i++) {
                    totAmt = totAmt + StringTool.round(this.getItemDouble(i, "OWN_PRICE"), 2);
                }
            } else {
                filter = "CASE_NO='" + caseNo + "' AND ORDERSET_CODE='" + parm.getValue("ORDER_CODE", row) + "' AND ORDERSET_GROUP_NO=" + groupNo + " AND SETMAIN_FLG='N'";
                this.setFilter(filter);
                if (!this.filter()) {
                    return totAmt;
                }
                int count = this.rowCount();
                for (int i = 0; i < count; i++) {
                    totAmt = totAmt + StringTool.round(this.getItemDouble(i, "DISPENSE_QTY") * this.getItemDouble(i, "OWN_PRICE"), 2);
                }
            }
            this.setFilter(filterString);
            this.filter();
            return totAmt;
        } else if ("AR_AMT_MAIN".equalsIgnoreCase(column)) {
            double arAmt = 0.0;
            double ownPrice = 0.0;
            String orderSetCode = parm.getValue("ORDERSET_CODE", row);
            String caseNo = parm.getValue("CASE_NO", row);
            int groupNo = parm.getInt("ORDERSET_GROUP_NO", row);
            String filterString = this.getFilter();
            String filter = "";
            if ("PHA_W".equals(orderSet)
                    || ("MAT".equals(orderSet) && "N".equals(parm.getValue("HIDE_FLG", row)))) {
                filter = "CASE_NO='" + caseNo + "' AND ORDER_CODE='" + parm.getValue("ORDER_CODE", row) + "' ";
            } else {
                filter = "CASE_NO='" + caseNo + "' AND ORDERSET_CODE='" + orderSetCode + "' AND ORDERSET_GROUP_NO=" + groupNo + " AND SETMAIN_FLG='N'";
            }
            this.setFilter(filter);
            if (!this.filter()) {
                return arAmt;
            }
            int count = this.rowCount();
            for (int i = 0; i < count; i++) {
                ownPrice = ownPrice + StringTool.round(this.getItemDouble(i, "DISPENSE_QTY") * this.getItemDouble(i, "OWN_PRICE"), 2);
            }
            this.setFilter(filterString);
            this.filter();
            return arAmt + ownPrice;
        } else if ("PAT_NAME".equalsIgnoreCase(column)) {
            String caseNo = parm.getValue("CASE_NO", row);
            HRMPatAdm adm = new HRMPatAdm();
            String patName = adm.getPatNameByCase(caseNo);
            return patName;
        }
        return "";
    }
    /**
     * ���ݸ����к���дҽ����ִ��ҽ����ʱ��
     * @param row
     * @return
     */
    public boolean execOrder(int row){
        if(row<0){
            return false;
        }
        Timestamp now=this.getDBTime();
        if(!TypeTool.getBoolean(this.getItemData(row, "SETMAIN_FLG"))){
            this.setItem(row, "EXEC_DR_CODE", Operator.getID());
            this.setItem(row, "ORDER_DATE", now);
            this.setItem(row, "OPT_USER", Operator.getID());
            this.setItem(row, "OPT_DATE", now);
            this.setItem(row, "OPT_TERM", Operator.getIP());
            return true;
        }
        String filterString=this.getFilter();
        this.setFilter("ORDERSET_CODE='"+this.getItemString(row, "ORDER_CODE")+"'");
        this.filter();
        int count=this.rowCount();
        for(int i=0;i<count;i++){
            this.setItem(i, "EXEC_DR_CODE", Operator.getID());
            this.setItem(i, "ORDER_DATE", now);
            this.setItem(i, "OPT_USER", Operator.getID());
            this.setItem(i, "OPT_DATE", now);
            this.setItem(i, "OPT_TERM", Operator.getIP());
        }
        this.setFilter(filterString);
        this.filter();
        return true;
    }
    /**
     * ���ݸ����кź�CASE_NO����һ��
     * @param row
     * @param caseNo
     * @return
     */
    public int insertRow(int row,String caseNo){
        if(row==-1){
            row=super.insertRow();
            this.setItem(row, "CASE_NO", caseNo);
        }
        return row;
    }
    /**
     * ���ﱣ��
     * @param regionCode
     * @param deptCode
     * @param drCode
     * @param caseNo
     * @return
     */
    public TParm saveByCheck(String regionCode, String deptCode, String caseNo,
                             String deptAttribute, String fileNo, boolean isFirst) {
        TParm result = new TParm();
        if (StringUtil.isNullString(regionCode) || StringUtil.isNullString(caseNo)
                || StringUtil.isNullString(deptAttribute)) {
            // System.out.println("parm is null");
            return result;
        }
        // ���ﱣ��SQL
        String sql =
                "SELECT * FROM HRM_ORDER WHERE CASE_NO='#' "
                        + "                AND REGION_CODE='#'  "
                        + "                AND DEPT_ATTRIBUTE='#'";
        sql = sql.replaceFirst("#", caseNo).replaceFirst("#", regionCode).replaceFirst("#", deptAttribute);
        // System.out.println("saveByCheck.sql="+sql);
        this.setSQL(sql);
        this.retrieve();
        int count = this.rowCount();
        if (count <= 0) {
            // System.out.println("count c<=0");
            return result;
        }
        // System.out.println("after retrieve");
        Timestamp now = this.getDBTime();
        // System.out.println("count===="+count);
        for (int i = 0; i < count; i++) {
            this.setItem(i, "EXEC_DEPT_CODE", Operator.getDept());
            this.setItem(i, "EXEC_DR_CODE", Operator.getID());
            this.setItem(i, "EXEC_DR_DESC", Operator.getName());
//          this.setItem(i, "ORDER_DATE", now);//delete by wanglong 20131114
            if (isFirst) {
                this.setItem(i, "FILE_NO", fileNo);
            }
            this.setItem(i, "OPT_USER", Operator.getID());
            this.setItem(i, "OPT_DATE", now);
            this.setItem(i, "OPT_TERM", Operator.getIP());
        }
        for (int i = 0; i < this.getUpdateSQL().length; i++) {
            // System.out.println("xueyf this.getUpdateSQL()======="+this.getUpdateSQL()[i]);
        }
        result = new TParm(TJDODBTool.getInstance().update(this.getUpdateSQL()));
        return result;
    }

    /**
     * �����Ƿ�ȫ������
     * @param regionCode String
     * @param deptCode String
     * @param caseNo String
     * @return result int
     */
    public int isDone(String regionCode, String deptCode, String caseNo, String deptAttribute) {
        if (StringUtil.isNullString(regionCode) || StringUtil.isNullString(deptCode)
                || StringUtil.isNullString(caseNo)) {
            // System.out.println("something is null");
            return 0;
        }
        // ���ﱣ��SQL
        String sql =
                "SELECT * FROM HRM_ORDER WHERE CASE_NO='#' "
                        + "                AND REGION_CODE='#' "
                        + "                AND DEPT_CODE='#' "
                        + "                AND DEPT_ATTRIBUTE='#'";
        sql = sql.replaceFirst("#", caseNo).replaceFirst("#", regionCode).replaceFirst("#", deptCode).replaceFirst("#", deptAttribute);
        // System.out.println("saveByCheck.sql="+sql);
        this.setSQL(sql);
        this.retrieve();
        int count = this.rowCount();
        if (count < 1) {
            // System.out.println("count<1");
            return 0;
        }
        int result = 1;
        for (int i = 0; i < count; i++) {
            if (StringUtil.isNullString(this.getItemString(i, "EXEC_DR_CODE"))) {
                result = -1;
            }
        }
        return result;
    }
    /**
     * ����CASE_NO����TOT_AMT
     * @param caseNo
     * @return
     */
    public double getTotAmt(String caseNo) {
        double totAmt = 0.0;
        if (StringUtil.isNullString(caseNo)) {
            return totAmt;
        }
        //��ѯһ�������ķ����ܺ�
        String sql = "SELECT TO_NUMBER(SUM(AR_AMT)) AR_AMT FROM HRM_ORDER WHERE CASE_NO='#'".replaceFirst("#", caseNo);
        // System.out.println("getTotAmt.sql="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        // System.out.println("getTotAmt.result="+result);
        if (result.getErrCode() != 0) {
            return -1;
        }
        totAmt = result.getDouble("AR_AMT", 0);
        return totAmt;
    }
    /**
     * ���ݸ���������ѯCASE_NO
     * @param companyCode
     * @param contractCode
     * @param mrNo
     * @return
     */
    public String getCaseNo(String companyCode, String contractCode, String mrNo) {
        String caseNo = "";
        if (StringUtil.isNullString(companyCode) || StringUtil.isNullString(contractCode)
                || StringUtil.isNullString(mrNo)) {
            return caseNo;
        }
        //��ѯһ��������CASE_NO
        String sql =
                "SELECT CASE_NO FROM HRM_PATADM A "
                        + "    WHERE A.COMPANY_CODE = '#' "
                        + "      AND A.CONTRACT_CODE = '#' "
                        + "      AND A.MR_NO = '#'";
        sql = sql.replaceFirst("#", companyCode).replaceFirst("#", contractCode).replaceFirst("#", mrNo);
        // System.out.println("getCaseNo.sql="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        // System.out.println("getCaseNo.result="+result);
        if (result.getErrCode() != 0) {
            return caseNo;
        }
        caseNo = result.getValue("CASE_NO", 0);
        return caseNo;
    }
    /**
     * �����������õ��Ʒ��б�
     * @param companyCode
     * @return
     */
    public TParm getChargeParm(String companyCode, String companyDesc, String contractCode) {
        TParm result = new TParm();
        HRMContractD contract = new HRMContractD();
        contract.onQuery();
        contract.setFilter("COMPANY_CODE='" + companyCode + "' AND CONTRACT_CODE='" + contractCode + "' AND (BILL_FLG='' OR BILL_FLG='N' )");
        if (!contract.filter()) {
            // System.out.println("contract.ifFilter is wrong");
            return result;
        }
        result = contract.getBuffer(contract.PRIMARY);
        int count = result.getCount();
        if (count < 1) {
            return result;
        }
        String packageDesc =
                StringUtil.getDesc("HRM_PACKAGEM", "PACKAGE_DESC", "PACKAGE_CODE='" + contract.getItemString(0, "PACKAGE_CODE") + "'");
   for (int i = 0; i < count; i++) {
            String mrNo = result.getValue("MR_NO", i);
            String caseNo = "";
            if (StringUtil.isNullString(mrNo)) {
                continue;
            }
            caseNo = adm.getLatestCaseNo(mrNo);
            double arAmt = this.getTotAmt(caseNo);
            if (arAmt == -1) {
                continue;
            }
            result.setData("AR_AMT", i, arAmt);
            result.setData("COMPANY_DESC", i, companyDesc);
            result.setData("PACKAGE_DESC", i, packageDesc);
            result.setData("CASE_NO", i, caseNo);
        }
        return result;
    }
    /**
     * ���ݸ���CASE_NO�õ�HRM_ORDER������
     * @param caseNo
     * @return
     */
    public TParm getFeeParm(String caseNo) {
        TParm result = new TParm();
        if (StringUtil.isNullString(caseNo)) {
            return result;
        }
        if (!this.filt(caseNo)) {
            return result;
        }
        TParm data = this.getBuffer(this.PRIMARY);
        String[] names = data.getNames();
        int count = data.getCount();
        if (count < 1) {
            return result;
        }
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < names.length; j++) {
                result.setData(names[j], i, result.getValue(names[j], i));
            }
        }
        result.setCount(result.getCount("CASE_NO"));
        // System.out.println("getFeeParm.result==="+result);
        count = result.getCount();
        for (int i = 0; i < count; i++) {
            String patName = StringUtil.getDesc("HRM_CONTRACTD", "PAT_NAME", "MR_NO='" + result.getValue("MR_NO", i) + "'");
            // System.out.println("patName====="+patName);
            result.setData("DONE", i, "Y");
            result.setData("PAT_NAME", i, patName);
            result.setData("ORDER_DESC", i, result.getValue("ORDER_DESC", i) + " " + result.getValue("SPECIFICATION", i));
        }
        return result;
    }
    /**
     * ��ѯ�����Ŀ��������ʹ�ã�
     * @param caseNo
     * @param packageCode
     * @return
     */
    public TParm getReportSheet(String caseNo, String packageCode) {
        TParm result = new TParm();
        if (StringUtil.isNullString(caseNo)) {
            // System.out.println("caseNo is not null");
            return null;
        }
        String sql =
                "SELECT A.ORDER_DESC, '' EXEC_DR, '' COL1, '' COL2, A.ORDER_CODE, A.DR_NOTE "
                        + " FROM HRM_ORDER A            " 
                        + "WHERE A.CASE_NO='#'          "
                        + "  AND A.SETMAIN_FLG = 'Y'     "
                        + "  AND A.RPTTYPE_CODE IS NOT NULL  "
                        + "  AND (A.CAT1_TYPE='LIS' OR A.CAT1_TYPE='RIS' ) "
                        + "ORDER BY A.ORDER_CODE";
        sql = sql.replaceFirst("#", caseNo);
        // String sql=GET_REPORT_DATE.replaceFirst("#", caseNo);
        // System.out.println("xueyf sqlORDER======="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        // System.out.println("result===="+result);
        if (result.getErrCode() != 0) {
            // System.out.println("result.getErrCode="+result.getErrText());
            return null;
        }
        int count = result.getCount();
        // System.out.println("result.getcount="+result.getCount());
        Map map = new LinkedHashMap();
        for (int i = 0; i < count; i++) {
            String code = result.getValue("ORDER_CODE", i).substring(0, 3);
            result.addData("TABLE_VALUE", "HLine=Y;#0.Bold=N");
            String desc = getCateDesc(code);
            if (!map.containsValue(desc)) {
                map.put(i, desc);
            }
        }
        // System.out.println("map==="+map);
        Iterator it = map.keySet().iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            list.add(it.next());
        }
        // System.out.println("list====="+list);
        for (int i = list.size() - 1; i > -1; i--) {
            int row = StringTool.getInt(list.get(i) + "");
            // System.out.println("row==="+row);
            result.insertData("ORDER_DESC", row, map.get(list.get(i)));
            result.insertData("EXEC_DR", row, "");
            result.insertData("COL1", row, "");
            result.insertData("COL2", row, "");
            result.insertData("TABLE_VALUE", row, "HLine=N;#0.Bold=Y;#0.FontSize=12");
            result.setCount(result.getCount("ORDER_DESC"));
        }
        // System.out.println("RRRR"+result);
        return result;
    }
    
    /**
     * ���ݸ��������ҽ����������
     * @param code
     * @return
     */
    private String getCateDesc(String code) {
        String desc = "";
        if (StringUtil.isNullString(code)) {
            return desc;
        }
        //��������ҽ����������
        String descSql = "SELECT CATEGORY_CHN_DESC FROM SYS_CATEGORY WHERE RULE_TYPE='EXM_RULE' AND CATEGORY_CODE='#'".replaceFirst("#", code);
        TParm descParm = new TParm(TJDODBTool.getInstance().select(descSql));
        if (descParm.getErrCode() != 0) {
            // System.out.println("getCateDesc.descSql.errText="+descParm.getErrText());
            // System.out.println("getCateDesc.descSql"+descSql);
            return desc;
        }
        desc = descParm.getValue("CATEGORY_CHN_DESC", 0);
        return desc;
    }
    /**
     * ȡ�ý�������
     * @param companyCode
     * @param contractCode
     * @return
     */
    public TParm getDepositParm(String companyCode, String contractCode) {
        TParm result = new TParm();
        //ȡ�ý�������
        String sql =
                "SELECT A.BILL_FLG, '+' DETAIL, A.MR_NO, B.PAT_NAME, SUM(A.OWN_AMT) OWN_AMT, A.CASE_NO "
                        + " FROM HRM_ORDER A, HRM_PATADM B "
                        + "WHERE B.COMPANY_CODE = '#' "
                        + "  AND B.CONTRACT_CODE = '#' "
                        + "  AND A.CASE_NO = B.CASE_NO "
                        + "  AND A.BILL_FLG = 'N' "
                        + "GROUP BY A.BILL_FLG,A.MR_NO,B.PAT_NAME,A.CASE_NO "
                        + "ORDER BY A.CASE_NO";
        sql = sql.replaceFirst("#", companyCode).replaceFirst("#", contractCode);
        // System.out.println("sql========="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    
    /**
     * ���ݸ���ĺ�ͬ��������Ϣ���鿴��Ա��ҽ������
     * @param companyCode
     * @param contractCode
     * @param caseNo
     * @return
     */
    public TParm getDepositParm(String companyCode, String contractCode, String caseNo) {//refactor by wanglong 20130317
        TParm result = new TParm();
        if (StringUtil.isNullString(companyCode) || StringUtil.isNullString(contractCode)) {
            return null;
        }
        if (StringUtil.isNullString(caseNo)) {
            return null;
        }
        //�����������ͺ�ͬ����ȡ�ô���������ҽ��
        String sql = // modify by wanglong 20130424
                "SELECT DISTINCT A.MR_NO,A.PAT_NAME,A.ORDERSET_CODE ORDER_CODE,B.ORDER_DESC,A.DISPENSE_QTY,"
                        + "A.OWN_PRICE/A.DISPENSE_QTY OWN_PRICE,A.OWN_PRICE OWN_AMT,A.DISCOUNT_RATE,A.OWN_AMT AR_AMT,A.BILL_FLG "
                        + " FROM (SELECT MR_NO,CASE_NO,PAT_NAME,ORDERSET_CODE,DISCOUNT_RATE,"
                        + "              SUM(DISPENSE_QTY) DISPENSE_QTY,SUM(OWN_PRICE) OWN_PRICE,SUM(OWN_AMT) OWN_AMT,BILL_FLG "
                        + "         FROM (SELECT A.MR_NO,B.CASE_NO,A.PAT_NAME,B.ORDERSET_CODE,"
                        + "                      SUM(CASE WHEN B.ORDER_CODE = B.ORDERSET_CODE THEN B.DISCOUNT_RATE ELSE 0 END) DISCOUNT_RATE,"
                        + "                      SUM(CASE WHEN B.ORDER_CODE = B.ORDERSET_CODE THEN B.DISPENSE_QTY ELSE 0 END) DISPENSE_QTY,"
                        + "                      SUM(B.OWN_AMT) OWN_PRICE,SUM(B.AR_AMT) OWN_AMT,B.BILL_FLG,B.ORDERSET_GROUP_NO "
                        + "                 FROM HRM_CONTRACTD A, HRM_ORDER B "
                        + "                WHERE A.MR_NO = B.MR_NO "
                        + "                  AND A.CONTRACT_CODE = B.CONTRACT_CODE "
                        + "                  AND A.COMPANY_CODE = '#' "
                        + "                  AND A.CONTRACT_CODE = '#' "
                        + "                  AND B.CASE_NO = '#' "
                        + "             GROUP BY A.MR_NO,B.CASE_NO,A.PAT_NAME,B.ORDERSET_CODE,B.BILL_FLG,B.ORDERSET_GROUP_NO) "
                        + "     GROUP BY MR_NO,CASE_NO,PAT_NAME,ORDERSET_CODE,DISCOUNT_RATE,OWN_PRICE,BILL_FLG) A, HRM_ORDER B "
                        + " WHERE A.ORDERSET_CODE = B.ORDER_CODE AND A.CASE_NO = B.CASE_NO "
                        + "ORDER BY A.MR_NO";
        sql = sql.replaceFirst("#", companyCode).replaceFirst("#", contractCode).replaceFirst("#", caseNo);
        // System.out.println("getDepositParm.sql="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            return null;
        }
        return result;
    }
    
    /**
     * ��ȡ�˵�Parm
     * @param companyCode
     * @param contractCode
     * @param caseNo
     * @return
     */
    public TParm getDepositBillParm(String companyCode, String contractCode, List caseNos) {//modify by wanglong 20130806
        TParm result = new TParm();
        if (StringUtil.isNullString(companyCode) || StringUtil.isNullString(contractCode)) {
            return null;
        }
        String sql =
                "SELECT SUM(OWN_AMT) OWN_AMT,SUM(A.AR_AMT) AR_AMT "
                        + " FROM HRM_ORDER A ,HRM_PATADM B "
                        + "WHERE A.CASE_NO=B.CASE_NO "
                        + "  AND B.COMPANY_CODE='#' "
                        + "  AND B.CONTRACT_CODE='#' "
                        + "  AND A.BILL_FLG='N' # ";
        sql = sql.replaceFirst("#", companyCode).replaceFirst("#", contractCode);
        if (caseNos.size() == 0) {
            sql = sql.replaceFirst("#", " AND B.CASE_NO = ''");
        } else {
            sql = sql.replaceFirst("#", " AND (" + getInStatement("B.CASE_NO", caseNos) + ")");
        }
//        System.out.println("getDepositParm.sql="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            return null;
        }
        result.setData("OWN_AMT", result.getDouble("OWN_AMT", 0));
        result.setData("AR_AMT", result.getDouble("AR_AMT", 0));
        return result;
    }
    /**
     * �����������ͺ�ͬ�����ʼ�����ݣ������CASE_NO����Ҳ��CASE_NO
     * @param companyCode
     * @param contractCode
     * @param caseNo
     * @return
     */
    public boolean onQueryByCom_Case(String companyCode, String contractCode, String caseNo) {
        if (StringUtil.isNullString(companyCode) || StringUtil.isNullString(contractCode)) {
            return false;
        }
        String caseSql = "";
        if (!StringUtil.isNullString(caseSql)) {
            caseSql = "AND CASE_NO='" + caseNo + "'";
        }
        //����������Ϣ�ͺ�ͬ��Ϣ��ʼ�����ݣ������CASE_NO��Ҳ��CASE_NO
        String sql ="SELECT * FROM HRM_ORDER # ORDER BY SEQ_NO".replaceFirst("#", " WHERE CONTRACT_CODE='" + contractCode + "'" + caseSql + " ");
        // System.out.println("sql="+sql);
        this.setSQL(sql);
        this.retrieve();
        this.setFilter("SETMAIN_FLG='Y'");
        this.filter();
        return true;
    }
    /**
     * ȡ��ĳ���˵�ҽ��ϸ��
     * @param billFlg
     * @return
     */
    public TParm getDepositDetailParm(String billFlg) {
        TParm parm = this.getBuffer(this.PRIMARY);
        TParm result = new TParm();
        if (StringUtil.isNullString(billFlg)) {
            return null;
        }
        String[] names = parm.getNames();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < names.length; j++) {
                if ("BILL_FLG".equalsIgnoreCase(names[j])
                        || "MR_NO".equalsIgnoreCase(names[j])
                        || "OWN_AMT".equalsIgnoreCase(names[j])
                        || "CASE_NO".equalsIgnoreCase(names[j])
                        || "SEQ_NO".equalsIgnoreCase(names[j])) {
                    result.setData(names[j], i, parm.getData(names[j], i));
                }
            }
            result.setData("DETAIL", i, "");
            result.setData("PAT_NAME", i, this.getItemString(i, "ORDER_DESC"));
        }
        result.setCount(parm.getCount());
        return result;
    }
    /**
     * Ĭ���Ѿ�����CASE_NO��FILTER�����Ĳ�ѯ����ҽ���󣬸���SEQ_NO�����Ӧ����
     * @param seqNo
     * @return
     */
    public int getRealRow(int seqNo) {
        int count = this.rowCount();
        for (int i = 0; i < count; i++) {
            if (this.getItemInt(i, "SEQ_NO") == seqNo) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * ���ݽ��㵥��ȡ���˵���ϸ�ÿ����Ա�ķ��ã�
     * @param billNo
     * @param isCharged
     * @return
     */
    public TParm getChargeData(String billNo, boolean isCharged) {
        TParm result = new TParm();
        if (StringUtil.isNullString(billNo)) {
            return null;
        }
        String sql =
                "SELECT CASE WHEN (A.RECEIPT_NO IS NULL OR A.RECEIPT_NO = 'N') THEN 'N'  ELSE 'Y' END CHARGE,"
                        + "B.PAT_NAME,A.CONTRACT_CODE,B.MR_NO,SUM(A.AR_AMT) AR_AMT,A.CASE_NO,A.BILL_NO "//modify by wanglong 20130324
                        + " FROM HRM_ORDER A,HRM_PATADM B "
                        + " WHERE A.BILL_NO IN (#) "// modify by wanglong 20130510
                        + "  AND A.CASE_NO=B.CASE_NO AND  # "
                        + " GROUP BY A.CASE_NO,B.PAT_NAME,A.CONTRACT_CODE,B.MR_NO,A.RECEIPT_NO,A.BILL_NO "//modify by wanglong 20130324
                        + " ORDER BY A.CASE_NO";
        sql = sql.replaceFirst("#", billNo);
        if (isCharged) {
            sql = sql.replaceFirst("#", "(A.RECEIPT_NO IS NOT NULL)");
        } else {
            sql = sql.replaceFirst("#", "(A.RECEIPT_NO IS NULL OR A.RECEIPT_NO ='')");
        }
        // System.out.println("getChargeData="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * �����˵���ȡ���˵���ϸ��
     * @param billNo
     * @param mrNo
     * @return
     */
    public TParm getChargeDataByMrNo(String billNo, String mrNo) {
        TParm result = new TParm();
        if (StringUtil.isNullString(billNo) || StringUtil.isNullString(mrNo)) {
            return null;
        }
        String sql =
                "SELECT CASE WHEN (A.RECEIPT_NO IS NULL OR A.RECEIPT_NO = 'N') THEN 'N'  ELSE 'Y' END CHARGE,"
                        + "B.PAT_NAME,B.MR_NO,SUM(A.AR_AMT) AR_AMT,A.CASE_NO "
                        + " FROM HRM_ORDER A,HRM_PATADM B "
                        + "WHERE A.BILL_NO='" + billNo
                        + "' AND A.CASE_NO=B.CASE_NO "
                        + "  AND A.MR_NO='" + mrNo
                        + "' AND (A.RECEIPT_NO IS NULL OR A.RECEIPT_NO ='') "
                        + "GROUP BY A.CASE_NO,B.PAT_NAME,B.MR_NO,A.RECEIPT_NO "
                        + "ORDER BY A.CASE_NO";
        // System.out.println("getChargeDataByMrNo.sql=-==============="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    /**
     * ȡ�ü����
     * @param patParm
     * @return
     */
    public String getLabNo(int row, TParm patParm) {
        String labNo = "";
        // System.out.println("deptAttribute="+this.getItemString(row, "DEPT_ATTRIBUTE"));
        String labMapKey = this.getItemString(row, "DEV_CODE") + this.getItemString(row, "OPTITEM_CODE")
                        + this.getItemString(row, "RPTTYPE_CODE") + this.getItemString(row, "CASE_NO");
        String cat1Type = this.getItemString(row, "CAT1_TYPE");
        if ("RIS".equalsIgnoreCase(cat1Type)) {
            labNo = SystemTool.getInstance().getNo("ALL", "MED", "LABNO", "LABNO");
            labMap.put(labMapKey, labNo);
        } else {
            if (labMap.get(labMapKey) == null) {
                labNo = SystemTool.getInstance().getNo("ALL", "MED", "LABNO", "LABNO");
                if (StringUtil.isNullString(labNo)) {
                    return labNo;
                }
                labMap.put(labMapKey, labNo);
            } else {
                labNo = (String) labMap.get(labMapKey);
            }
        }
        MedApply med = this.getMedApply();
        int medRow = med.insertRow();
        med.setItem(medRow, "APPLICATION_NO", labNo);
        med.setItem(medRow, "CAT1_TYPE", this.getItemData(row, "CAT1_TYPE"));
        med.setItem(medRow, "ORDER_NO", this.getItemData(row, "CASE_NO"));
        med.setItem(medRow, "SEQ_NO", this.getItemData(row, "SEQ_NO"));
        med.setItem(medRow, "ORDER_CODE", this.getItemData(row, "ORDER_CODE"));
        med.setItem(medRow, "ORDER_DESC", this.getItemData(row, "ORDER_DESC"));
        med.setItem(medRow, "ORDER_DR_CODE", Operator.getID());
        Timestamp now = this.getDBTime();
        med.setItem(medRow, "ORDER_DATE", now);
        med.setItem(medRow, "ORDER_DEPT_CODE", Operator.getDept());//modify by wanglong 20131112
        med.setItem(medRow, "START_DTTM", now);
        med.setItem(medRow, "EXEC_DEPT_CODE", this.getItemData(row, "EXEC_DEPT_CODE"));
        med.setItem(medRow, "EXEC_DR_CODE", "");
        med.setItem(medRow, "OPTITEM_CODE", this.getItemData(row, "OPTITEM_CODE"));
        String optitemDesc =
                StringUtil.getDesc("SYS_DICTIONARY", "CHN_DESC"," GROUP_ID = 'SYS_OPTITEM' AND ID='" + this.getItemData(row, "OPTITEM_CODE") + "'");
        med.setItem(medRow, "OPTITEM_CHN_DESC", optitemDesc);
        med.setItem(medRow, "ORDER_CAT1_CODE", this.getItemData(row, "ORDER_CAT1_CODE"));
        String dealSystem =
                StringUtil.getDesc("SYS_OPTITEM", "OPTITEM_CHN_DESC", "OPTITEM_CODE='" + this.getItemData(row, "OPTITEM_CODE") + "'");
        med.setItem(medRow, "DEAL_SYSTEM", dealSystem);
        med.setItem(medRow, "RPTTYPE_CODE", this.getItemData(row, "RPTTYPE_CODE"));
        med.setItem(medRow, "DEV_CODE", this.getItemData(row, "DEV_CODE"));
        med.setItem(medRow, "REMARK", this.getItemData(row, "DR_NOTE"));
        med.setItem(medRow, "OWN_AMT", this.getItemData(row, "OWN_AMT"));
        med.setItem(medRow, "AR_AMT", this.getItemData(row, "AR_AMT"));
        med.setItem(medRow, "CASE_NO", this.getItemData(row, "CASE_NO"));
        med.setItem(medRow, "MR_NO", this.getItemData(row, "MR_NO"));
        med.setItem(medRow, "ADM_TYPE", "H");
        med.setItem(medRow, "PAT_NAME", patParm.getValue("PAT_NAME"));
        med.setItem(medRow, "PAT_NAME1", TMessage.getPy(patParm.getValue("PAT_NAME")));
        String birthDate = patParm.getValue("BIRTH_DATE") + "";
        if (!StringUtil.isNullString(birthDate)) {
            birthDate = birthDate.substring(0, birthDate.indexOf(" "));
        }
        med.setItem(medRow, "BIRTH_DATE", StringTool.getTimestamp(birthDate, "yyyy-MM-dd"));
        med.setItem(medRow, "SEX_CODE", patParm.getValue("SEX_CODE"));
        med.setItem(medRow, "POST_CODE", patParm.getValue("POST_CODE"));
        med.setItem(medRow, "ADDRESS", patParm.getValue("ADDRESS"));
        med.setItem(medRow, "COMPANY", patParm.getValue("COMPANY_CODE"));
        med.setItem(medRow, "TEL", patParm.getValue("TEL"));
        med.setItem(medRow, "IDNO", patParm.getValue("IDNO"));
        med.setItem(medRow, "DEPT_CODE", Operator.getDept());
        med.setItem(medRow, "REGION_CODE", Operator.getRegion());
        med.setItem(medRow, "CLINICROOM_NO", "");
        med.setItem(medRow, "ICD_TYPE", "");
        med.setItem(medRow, "ICD_CODE", "");
        med.setItem(medRow, "STATUS", "0");
        med.setItem(medRow, "SEND_FLG", "0");
        med.setItem(medRow, "BILL_FLG", "Y");
        med.setItem(medRow, "DEPT_CODE", Operator.getDept());
        med.setItem(medRow, "OPT_USER", Operator.getID());
        med.setItem(medRow, "OPT_DATE", now);
        med.setItem(medRow, "OPT_TERM", Operator.getIP());
        med.setItem(medRow, "PRINT_FLG", "N");
        med.setActive(medRow, true);
        return labNo;
    }
    /**
     * �õ�MED_APPLY
     * @return
     */
    public MedApply getMedApply() {
        if (med == null) {
            med = new MedApply();
            med.onQuery();
        }
        return med;
    }
    
    /**
     * �õ�MED_APPLY
     * @return
     */
    public MedApply getMedApply(String caseNo) {
        if (med == null) {
            med = new MedApply();
            med.onQueryByCaseNo(caseNo);
        }
        return med;
    }
    
    /**
     * �؆�MED_APPLY
     */
    public void resetMedApply() {
        med = new MedApply();
        med.onQuery();
        this.labMap = new HashMap();
    }
    /**
     * �շ��ǲ����վݺ�
     * @return
     */
    public boolean updateReceiptNo(String receiptNo, String deptCode) {
        if (StringUtil.isNullString(receiptNo) || StringUtil.isNullString(deptCode)) {
            // System.out.println("nulll"+deptCode);
            return false;
        }
        String filterString = this.getFilter();
        String caseNo = this.getItemString(0, "CASE_NO");
        this.setFilter("CASE_NO='" + caseNo + "'");
        this.filter();
        // System.out.println("in updateReceiptNo .");
        // this.showDebug();
        int count = this.rowCount();
        if (count < 1) {
            return false;
        }
        // System.out.println("�շѱ��棡"+count);
        Timestamp now = this.getDBTime();
        for (int i = 0; i < count; i++) {
            // /System.out.println(""+this.getItemString(i,"#NEW#"));
            if (!"Y".equals(this.getItemString(i, "#NEW#"))) continue;
            this.setItem(i, "RECEIPT_NO", receiptNo);
            this.setItem(i, "DEPT_CODE", deptCode);
            this.setItem(i, "PRINT_FLG", "Y");
            this.setItem(i, "BILL_FLG", "Y");
            this.setItem(i, "BILL_USER", Operator.getID());
            this.setItem(i, "BILL_DATE", now);
        }
        this.setFilter(filterString);
        this.filter();
        return true;
    }
    /**
     * ���ݸ����к�ɾ��ҽ��������Ǽ���ҽ������ɾ��ϸ��
     * @param row
     * @return
     */
    public boolean onDeleteRow(int row) {
        int count = this.rowCount();
        if (row >= count) {
            return false;
        }
        if (!StringUtil.isNullString(this.getItemString(row, "EXEC_DR_CODE"))) {
            return false;
        }
        String caseNo = this.getItemString(row, "CASE_NO");
        String medApply = this.getItemString(row, "MED_APPLY_NO");
        if (!TypeTool.getBoolean(this.getItemData(row, "SETMAIN_FLG"))) { //���Ǽ���ҽ��
            return this.removeRow(row);
        }
        String ordersetCode = this.getItemString(row, "ORDER_CODE");
        for (int i = count - 1; i > -1; i--) {
            if (ordersetCode.equalsIgnoreCase(this.getItemString(i, "ORDERSET_CODE"))) {
                this.removeRow(i);
            }
        }
        return true;
    }
    /**
     * ���ݿ��ҺͿƱ����Բ�ѯҽ��
     * @param deptCode
     * @param deptAttribute
     * @return
     */
    public TParm onQueryByDeptAttribute(String deptAttribute, boolean isDone, String startDate,
                                        String endDate) {
    	//System.out.println("onQueryByDeptAttribute Begin");
        TParm result = new TParm();
        if (StringUtil.isNullString(deptAttribute)) {
            return null;
        }
        //���ݿ��Ҵ���ͿƱ����Եõ�ҽ��
        String sql =
                "SELECT CASE WHEN (A.EXEC_DR_CODE IS NULL OR A.EXEC_DR_CODE = '') THEN 'N' ELSE 'Y' END DONE, "
                        + "A.*,B.PAT_NAME,B.SEX_CODE,B.REPORT_DATE ,A.DEPT_CODE,B.BIRTHDAY "
                        + "  FROM HRM_ORDER A, HRM_PATADM B "
                        + " WHERE A.DEPT_ATTRIBUTE = '#' "
                        + "   AND A.CASE_NO = B.CASE_NO ";
        //caowl 20130326 start
        if(startDate!=null && !startDate.equals("") && endDate != null && !endDate.equals("")){
        	sql += "   AND B.REPORT_DATE >= TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') "
                + "   AND B.REPORT_DATE < TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ";
        }
        //caowl 20130326 end
                       
                       sql += "   AND A.SETMAIN_FLG = 'Y' "
                        + " #  ORDER BY A.MR_NO";//modify by wanglong 20130319
        sql = sql.replaceFirst("#", deptAttribute);
        if (isDone) {
            sql = sql.replaceFirst("#", "AND A.EXEC_DR_CODE IS NOT NULL");
        } else {
            sql = sql.replaceFirst("#", "AND A.EXEC_DR_CODE IS NULL");
        }
         //System.out.println("xueyf query By .sql=============================="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        //System.out.println("onQueryByDeptAttribute end");
        return result;
    }
    
    /**
     * ���ݿ��ҺͿƱ������Լ������Ų�ѯҽ��
     * @param deptAttribute
     * @param mrNo
     * @param isDone
     * @param startDate
     * @param endDate
     * @return
     */
    public TParm onQueryByDeptAttribute(String deptAttribute, String mrNo, boolean isDone,
                                          String startDate, String endDate) {//add by wanglong 20130515
        TParm result = new TParm();
        if (StringUtil.isNullString(deptAttribute)) {
            return null;
        }
        //���ݿ��Ҵ���ͿƱ����Եõ�ҽ��
        String sql =
                "SELECT CASE WHEN (A.EXEC_DR_CODE IS NULL OR A.EXEC_DR_CODE = '') THEN 'N' ELSE 'Y' END DONE, "
                        + "A.*,B.PAT_NAME,B.SEX_CODE,B.REPORT_DATE ,A.DEPT_CODE,B.BIRTHDAY "
                        + "  FROM HRM_ORDER A, HRM_PATADM B "
                        + " WHERE A.DEPT_ATTRIBUTE = '#' "
                        + "   AND A.CASE_NO = B.CASE_NO "
                        + "   AND A.SETMAIN_FLG = 'Y' "
                        + "  #  #  #  ORDER BY A.MR_NO";//modify by wanglong 20130319
        sql = sql.replaceFirst("#", deptAttribute);
        if (StringUtil.isNullString(mrNo)) {
            sql = sql.replaceFirst("#", "");
        } else {
            sql = sql.replaceFirst("#", " AND B.MR_NO='" + mrNo + "'");
        }
        //caowl 20130326 start
        if (!StringUtil.isNullString(startDate) && !StringUtil.isNullString(endDate)) {
            sql =
                    sql.replaceFirst("#", " AND B.REPORT_DATE BETWEEN TO_DATE('" + startDate
                            + "','YYYYMMDDHH24MISS') " + " AND TO_DATE('" + endDate
                            + "','YYYYMMDDHH24MISS') ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        //caowl 20130326 end
        if (isDone) {
            sql = sql.replaceFirst("#", "AND A.EXEC_DR_CODE IS NOT NULL");
        } else {
            sql = sql.replaceFirst("#", "AND A.EXEC_DR_CODE IS NULL");
        }
//        System.out.println("onQueryByDeptAttrAndMrNo.sql================"+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    
    /**
     * ���ݿ��ҺͿƱ����Բ�ѯҽ��
     * 
     * @param deptCode
     * @param deptAttribute
     * @return
     */
    public TParm onQueryDeptAttribute(String deptAttribute, boolean isDone, String startDate,
                                      String endDate) {// add by wanglong 20130319
        TParm result = new TParm();
        if (StringUtil.isNullString(deptAttribute)) {
            return null;
        }
        // ���ݿ��Ҵ���ͿƱ����Եõ�ҽ��
        String sql =
                "SELECT CASE WHEN SUM(DONE) > 0 THEN 'Y' ELSE 'N' END DONE,"
                        + "CASE_NO,MR_NO,PAT_NAME,SEX_CODE,REPORT_DATE,DEPT_CODE,BIRTHDAY "
                        + " FROM (SELECT (CASE WHEN (A.EXEC_DR_CODE IS NULL OR A.EXEC_DR_CODE = '') THEN 0 ELSE 1 END) DONE,"
                        + "              B.CASE_NO,B.MR_NO,B.PAT_NAME,B.SEX_CODE,B.REPORT_DATE,A.DEPT_CODE,B.BIRTHDAY "
                        + "         FROM HRM_ORDER A, HRM_PATADM B "
                        + "        WHERE A.CASE_NO = B.CASE_NO "
                        + "  #  "
                        // + "           AND A.DEPT_ATTRIBUTE IS NOT NULL "
                        + "          AND B.REPORT_DATE BETWEEN TO_DATE( '#', 'YYYYMMDDHH24MISS') AND TO_DATE( '#', 'YYYYMMDDHH24MISS') "
                        + "          AND A.SETMAIN_FLG = 'Y' "
                        + "     GROUP BY A.EXEC_DR_CODE,B.CASE_NO,B.MR_NO,B.PAT_NAME,B.SEX_CODE,B.REPORT_DATE,A.DEPT_CODE,B.BIRTHDAY) "
                        // + "WHERE 1 = 1 # "
                        + "GROUP BY CASE_NO,MR_NO,PAT_NAME,SEX_CODE,REPORT_DATE,DEPT_CODE, BIRTHDAY "
                        + " # " 
                        + "ORDER BY MR_NO";
        if (deptAttribute.equals("04")) {
            sql = sql.replaceFirst("#", " AND A.DEPT_ATTRIBUTE = '04' ");
        } else {
            sql = sql.replaceFirst("#", " AND A.DEPT_ATTRIBUTE IN ('03','05','02','08','07','06','01') ");
        }
        sql = sql.replaceFirst("#", startDate).replaceFirst("#", endDate);
        if (isDone) {
            sql = sql.replaceFirst("#", " HAVING SUM(DONE) > 0 ");
        } else {
            sql = sql.replaceFirst("#", " HAVING SUM(DONE) <= 0 ");
        }
        // System.out.println("=====onQueryDeptAttribute.sql====================="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    
    /**
     * ���ݲ����Ų�ѯ�����ҽ��
     * @param deptCode
     * @param deptAttribute
     * @param startDate
     * @param endDate
     * @param mrNo
     * @return
     */
    public TParm onQueryByMr(String deptCode, String deptAttribute, String startDate,
                             String endDate, String mrNo) {
        TParm result = new TParm();
        if (StringUtil.isNullString(deptCode) || StringUtil.isNullString(deptAttribute)) {
            // System.out.println("param is null");
            return null;
        }
        //���ݿ��Ҵ���ͿƱ����Եõ�ҽ��
        String sql =
                "SELECT CASE WHEN (A.EXEC_DR_CODE IS NULL OR A.EXEC_DR_CODE = '') THEN 'N' ELSE 'Y' END DONE, "
                        + "A.*,B.PAT_NAME,B.SEX_CODE,B.REPORT_DATE ,A.DEPT_CODE,B.BIRTHDAY "
                        + "  FROM HRM_ORDER A, HRM_PATADM B "
                        + " WHERE A.DEPT_ATTRIBUTE = '#' "
                        + "   AND A.CASE_NO = B.CASE_NO "
                        + "   AND B.REPORT_DATE >= TO_DATE('#','YYYYMMDDHH24MISS') "
                        + "   AND B.REPORT_DATE < TO_DATE('#','YYYYMMDDHH24MISS') "
                        + "   AND A.SETMAIN_FLG = 'Y' "
                        + " #  ORDER BY A.CASE_NO";
        sql = sql.replaceFirst("#", deptCode).replaceFirst("#", deptAttribute).replaceFirst("#", startDate).replaceFirst("#", endDate);
        sql = sql.replaceFirst("#", " AND B.MR_NO='" + mrNo + "'");
        // System.out.println("onQueryByMr.sql="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    /**
     * ���ݸ���DEPT_ATTRIBUTE�޸�
     * @param deptAttribute
     * @return
     */
    public boolean updateDeptAttribute(String deptAttribute,String caseNo,String orderCode,int groupNo){
        if(StringUtil.isNullString(deptAttribute)||StringUtil.isNullString(caseNo)||StringUtil.isNullString(orderCode)){
            return false;
        }
        String filterString = this.getFilter();
        this.setFilter("CASE_NO='" + caseNo + "' AND ORDERSET_CODE='" + orderCode + "' AND ORDERSET_GROUP_NO=" + groupNo);
        this.filter();
        int count = this.rowCount();
        String applyNo = "";
        String cat1Type = "";
        int seq = -1;
        for (int i = 0; i < count; i++) {
            if (StringTool.getBoolean(this.getItemString(i, "SETMAIN_FLG"))) {
                applyNo = this.getItemString(i, "MED_APPLY_NO");
                seq = this.getItemInt(i, "SEQ_NO");
                cat1Type = this.getItemString(i, "CAT1_TYPE");
            }
            this.setItem(i, "DEPT_ATTRIBUTE", deptAttribute);
        }
        this.getMedApply().deleteRowBy(applyNo, caseNo, seq, cat1Type);
        this.setFilter(filterString);
        this.filter();
        return true;
    }
    
    /**
     * ȡ�ô��ܼ�Ĳ����б�
     * @return
     */
    public TParm getFinalCheckPat(String startDate, String endDate, String isUnDone) {
        TParm result = new TParm();
        //caowl 20130326 start
        //ȡ�ô��ܼ�Ĳ����б�
        String sql =
                "SELECT DISTINCT A.*, B.PAT_NAME, B.SEX_CODE, B.REPORT_DATE, A.DEPT_CODE, B.BIRTHDAY, B.REPORT_STATUS, B.TEL "
                        + "  FROM HRM_ORDER A,HRM_PATADM B "
                        + " WHERE A.EXEC_DEPT_CODE = '#' "
                        + "   AND A.DEPT_ATTRIBUTE = '04' "
                        + "   AND A.CASE_NO = B.CASE_NO ";
        if(startDate!=null && !startDate.equals("") && endDate!=null && !endDate.equals("") ){
        	sql += "   AND B.REPORT_DATE >= TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') "
                + "   AND B.REPORT_DATE < TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ";
        }
                        
                        sql+= "   # "
                        + "   AND A.SETMAIN_FLG='Y' "
                        + " ORDER BY A.SEQ_NO";
        if ("1".equalsIgnoreCase(isUnDone)) {
            sql = sql.replaceFirst("#", Operator.getDept())
                     .replaceFirst("#", "AND A.EXEC_DR_CODE IS  NULL");
        } else if ("2".equalsIgnoreCase(isUnDone)) {
            sql = sql.replaceFirst("#", Operator.getDept())
                     .replaceFirst("#", "AND A.EXEC_DR_CODE IS NOT NULL");
        } else {
            sql = sql.replaceFirst("#", Operator.getDept())
                     .replaceFirst("#", "");
        }
        //caowl 20130326 end
        //System.out.println("getFinalCheckPat.sql================"+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * ͨ��������ȡ�ô��ܼ�Ĳ����б�
     * @param mrNo
     * @param startDate
     * @param endDate
     * @param isUnDone
     * @return
     */
    public TParm getFinalCheckPat(String mrNo,String startDate, String endDate, String isUnDone) {//add by wanglong 20130515
        TParm result = new TParm();
        //caowl 20130326 start
        //ȡ�ô��ܼ�Ĳ����б�
        String sql =
                "SELECT DISTINCT CASE WHEN A.EXEC_DR_CODE IS NULL THEN 'N' ELSE 'Y' END DONE, "
                        + "A.*, B.PAT_NAME, B.SEX_CODE, B.REPORT_DATE, A.DEPT_CODE, B.BIRTHDAY, B.REPORT_STATUS, B.TEL "
                        + "  FROM HRM_ORDER A,HRM_PATADM B "
                        + " WHERE A.EXEC_DEPT_CODE = '#' "
                        + "   AND A.DEPT_ATTRIBUTE = '04' "
                        + "   AND A.CASE_NO = B.CASE_NO   "
                        + "   AND A.SETMAIN_FLG='Y'       " + "   #  #  #   ORDER BY B.MR_NO ";
        sql = sql.replaceFirst("#", Operator.getDept());
        if (StringUtil.isNullString(mrNo)) {
            sql = sql.replaceFirst("#", "");
        } else {
            sql = sql.replaceFirst("#", " AND B.MR_NO='" + mrNo + "'");
        }
        // caowl 20130326 start
        if (!StringUtil.isNullString(startDate) && !StringUtil.isNullString(endDate)) {
            sql =
                    sql.replaceFirst("#", " AND B.REPORT_DATE BETWEEN TO_DATE('" + startDate
                            + "','YYYYMMDDHH24MISS') " + " AND TO_DATE('" + endDate
                            + "','YYYYMMDDHH24MISS') ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        // caowl 20130326 end
        if ("1".equalsIgnoreCase(isUnDone)) {
            sql = sql.replaceFirst("#", "AND A.EXEC_DR_CODE IS  NULL");
        } else if ("2".equalsIgnoreCase(isUnDone)) {
            sql = sql.replaceFirst("#", "AND A.EXEC_DR_CODE IS NOT NULL");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        //caowl 20130326 end
//        System.out.println("getFinalCheckPat.sql================"+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    
    /**
     * ����MR_NOȡ�ô��ܼ�Ĳ����б�
     * @param mrNo
     * @param startDate
     * @param endDate
     * @return
     */
    public TParm getFinalCheckPatByMr(String mrNo, String startDate, String endDate, String isUnDone) {
        TParm result = new TParm();
        if (StringUtil.isNullString(mrNo) || StringUtil.isNullString(startDate) || StringUtil.isNullString(endDate)) {
            // System.out.println("param is null");
            return null;
        }
        // if(startDate.length()!=8||endDate.length()!=8){
        // System.out.println("length is not null");
        // return null;
        // }
        //ȡ�ô��ܼ�Ĳ����б�
        String sql =
                "SELECT DISTINCT A.*, B.PAT_NAME, B.SEX_CODE, B.REPORT_DATE, A.DEPT_CODE, B.BIRTHDAY, B.REPORT_STATUS, B.TEL "
                        + "  FROM HRM_ORDER A,HRM_PATADM B "
                        + " WHERE A.EXEC_DEPT_CODE = '#' "
                        + "   AND A.DEPT_ATTRIBUTE = '04' "
                        + "   AND A.CASE_NO = B.CASE_NO "
                        + "   AND B.REPORT_DATE >= TO_DATE('#','YYYYMMDDHH24MISS') "
                        + "   AND B.REPORT_DATE < TO_DATE('#','YYYYMMDDHH24MISS') "
                        + "   # "
                        + "   AND A.SETMAIN_FLG='Y' "
                        + " ORDER BY A.SEQ_NO";
        if ("1".equalsIgnoreCase(isUnDone)) {
            sql = sql.replaceFirst("#", Operator.getDept())
                     .replaceFirst("#", startDate)
                     .replaceFirst("#", endDate)
                     .replaceFirst("#", "AND A.EXEC_DR_CODE IS  NULL");
        } else if ("2".equalsIgnoreCase(isUnDone)) {
            sql = sql.replaceFirst("#", Operator.getDept())
                     .replaceFirst("#", startDate)
                     .replaceFirst("#", endDate)
                     .replaceFirst("#", "AND A.EXEC_DR_CODE IS NOT NULL");
        } else {
            sql = sql.replaceFirst("#", Operator.getDept())
                     .replaceFirst("#", startDate)
                     .replaceFirst("#", endDate)
                     .replaceFirst("#", "");
        }
        // System.out.println("getFinalCheckPatByMr.sql================"+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    /**
     * ����������������ȡ���ܼ�,����BILL_FLG
     * @param caseNo
     * @param orderCode
     * @param seqNo
     * @return
     */
    public double getAmt(String caseNo, String orderCode, int groupNo, String billFlg, String billNo) {
        double amt = 0.0;
        if (StringUtil.isNullString(caseNo) || StringUtil.isNullString(orderCode)) {
            // System.out.println("param is null");
            return amt;
        }
        String filter = this.getFilter();
        this.setFilter("CASE_NO='" + caseNo + "' AND ORDERSET_CODE='" + orderCode + "' AND ORDERSET_GROUP_NO=" + groupNo);
        this.filter();
        // this.showDebug();
        int count = this.rowCount();
        for (int i = 0; i < count; i++) {
            if (billFlg.equalsIgnoreCase(this.getItemString(i, "BILL_FLG"))) {
                continue;
            }
            if ("Y".equalsIgnoreCase(billFlg)) {
                this.setItem(i, "BILL_FLG", billFlg);
                this.setItem(i, "BILL_NO", billNo);
                this.setActive(i, true);
            } else {
                this.setItem(i, "BILL_FLG", billFlg);
                this.setItem(i, "BILL_NO", "");
                this.setActive(i, true);
            }
            amt += this.getItemDouble(i, "AR_AMT");
        }
        this.setFilter(filter);
        this.filter();
        return amt;
    }
    /**
     * ����updateRows,ѭ���޸�OPT_DATE,OPT_USER,OPT_TERM��ֵ
     * @return
     */
    public boolean updateOpt() {
        int[] rows = this.getModifiedRows();
        if (rows == null || rows.length <= 0) {
            return false;
        }
        Timestamp now = this.getDBTime();
        for (int i = 0; i < rows.length; i++) {
            int row = rows[i];
            this.setItem(row, "OPT_DATE", now);
            this.setItem(row, "OPT_USER", Operator.getID());
            this.setItem(row, "OPT_TERM", Operator.getIP());
            this.setActive(row, true);
        }
        return true;
    }
    /**
     * �����˵��Ų�ѯ�˵�ϸ��(����������½��˵���ϸ)
     * @param billNo
     * @return
     */
    public TParm getParmBybillNo(String billNo) {// rafactor by wanglong 20130317
        TParm result = new TParm();
        if (StringUtil.isNullString(billNo)) {
            return null;
        }
        //�����˵��Ų�ѯ����
        //�����������ͺ�ͬ����ȡ�ô���������ҽ��
        String sql = // modify by wanglong 20130424
                "SELECT DISTINCT A.MR_NO,A.PAT_NAME,A.ORDERSET_CODE ORDER_CODE,B.ORDER_DESC,A.DISPENSE_QTY,"
                        + "A.OWN_PRICE/A.DISPENSE_QTY OWN_PRICE,A.OWN_PRICE OWN_AMT,A.DISCOUNT_RATE,A.OWN_AMT AR_AMT "
                        + " FROM (SELECT MR_NO,CASE_NO,PAT_NAME,ORDERSET_CODE,DISCOUNT_RATE,SUM(DISPENSE_QTY) "
                        + "              DISPENSE_QTY,SUM(OWN_PRICE) OWN_PRICE,SUM(OWN_AMT) OWN_AMT "
                        + "        FROM (SELECT A.MR_NO,B.CASE_NO,A.PAT_NAME,B.ORDERSET_CODE,"
                        + "                     SUM(CASE WHEN B.ORDER_CODE = B.ORDERSET_CODE THEN B.DISCOUNT_RATE ELSE 0 END) DISCOUNT_RATE,"
                        + "                     SUM(CASE WHEN B.ORDER_CODE = B.ORDERSET_CODE THEN B.DISPENSE_QTY ELSE 0 END) DISPENSE_QTY,"
                        + "                     SUM(B.OWN_AMT) OWN_PRICE,SUM(B.AR_AMT) OWN_AMT,B.ORDERSET_GROUP_NO "
                        + "               FROM HRM_CONTRACTD A, HRM_ORDER B "
                        + "              WHERE A.MR_NO = B.MR_NO "
                        + "                AND A.CONTRACT_CODE = B.CONTRACT_CODE "
                        + "                AND B.BILL_NO = '#' "
                        + "           GROUP BY A.MR_NO,B.CASE_NO,A.PAT_NAME,B.ORDERSET_CODE,B.ORDERSET_GROUP_NO) "
                        + "     GROUP BY MR_NO,CASE_NO,PAT_NAME,ORDERSET_CODE,DISCOUNT_RATE,OWN_PRICE) A, HRM_ORDER B "
                        + " WHERE A.ORDERSET_CODE = B.ORDER_CODE" + " AND A.CASE_NO = B.CASE_NO "
                        + "ORDER BY A.PAT_NAME, ORDER_DESC";
        sql = sql.replaceFirst("#", billNo);
        // System.out.println("getParmBybillNo.sql="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            return null;
        }
        int count = result.getCount();
        if (count <= 0) {
            return null;
        }

        return result;
    }
    /**
     * �����վݺŲ�ѯ�����嵥����
     * @param receiptNo
     * @return
     */
    public TParm getParmByReceiptNo(String receiptNo) {
        TParm result = new TParm();
        if (StringUtil.isNullString(receiptNo)) {
            return null;
        }
        //�����վݺŲ�ѯ����
        String sql =
                "SELECT B.PAT_NAME, A.ORDER_DESC||' '||A.SPECIFICATION, A.OWN_PRICE, A.DISPENSE_QTY,"
                        + "C.UNIT_CHN_DESC, A.AR_AMT, A.ORDERSET_CODE, A.ORDERSET_GROUP_NO, A.CASE_NO "
                        + " FROM HRM_ORDER A, HRM_PATADM B,SYS_UNIT C "
                        + "WHERE A.RECEIPT_NO = '#' "
                        + "  AND A.CASE_NO = B.CASE_NO "
                        + "  AND A.SETMAIN_FLG = 'Y' "
                        + "  AND A.DISPENSE_UNIT = C.UNIT_CODE(+) "
                        + "ORDER BY A.CASE_NO,A.SEQ_NO";
        sql = sql.replaceFirst("#", receiptNo);
        // System.out.println("getParmByReceiptNo.sql="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            // System.out.println("getParmByReceiptNo.errText="+result.getErrText());
            return null;
        }
        int count = result.getCount();
        if (count <= 0) {
            return null;
        }
        // "SELECT SUM(OWN_PRICE),SUM(AR_AMT) FROM HRM_ORDER WHERE CASE_NO='#' AND ORDERSET_CODE='#' AND ORDERSET_GROUP_NO=#";
        for (int i = 0; i < count; i++) {
            //����������Ϣ��ϸ�൥���ܼ�
            String sqlDetail =
                    "SELECT SUM(OWN_PRICE) OWN_PRICE,SUM(AR_AMT) AR_AMT "
                            + "  FROM HRM_ORDER "
                            + " WHERE CASE_NO = '#' "
                            + "   AND ORDERSET_CODE = '#' "
                            + "   AND ORDERSET_GROUP_NO = #";
            sqlDetail = sqlDetail.replaceFirst("#", result.getValue("CASE_NO", i))
                                 .replaceFirst("#", result.getValue("ORDERSET_CODE", i))
                                 .replaceFirst("#", result.getInt("ORDERSET_GROUP_NO", i) + "");
            // System.out.println("sqlDetail.sql="+sqlDetail);
            TParm detail = new TParm(TJDODBTool.getInstance().select(sqlDetail));
            if (detail.getErrCode() != 0) {
                return null;
            }
            // System.out.println("detail="+detail);
            result.setData("OWN_PRICE", i, detail.getDouble("OWN_PRICE", 0));
            result.setData("AR_AMT", i, detail.getDouble("AR_AMT", 0));
        }
        return result;
    }
    
    /**
     * �����վݺŲ�ѯ�����嵥����(������㣬�����嵥��ӡר��)
     * @param billNo
     * @return
     */
    public TParm getParmByBillNo(String billNo) {// add by wanglong 20130403
        String sql = // modify by wanglong 20130424
                "SELECT DISTINCT A.PAT_NAME,B.ORDER_DESC||' '||B.SPECIFICATION AS ORDER_DESC,A.OWN_PRICE/A.DISPENSE_QTY OWN_PRICE,"
                        + "A.DISPENSE_QTY,C.UNIT_CHN_DESC,A.OWN_AMT AR_AMT,A.MR_NO,A.ORDERSET_CODE ORDER_CODE,"
                        + "A.OWN_PRICE OWN_AMT,A.DISCOUNT_RATE "
                        + " FROM (SELECT MR_NO,CASE_NO,PAT_NAME,ORDERSET_CODE,DISCOUNT_RATE,"
                        + "              SUM(DISPENSE_QTY) DISPENSE_QTY,SUM(OWN_PRICE) OWN_PRICE,SUM(OWN_AMT) OWN_AMT "
                        + "         FROM (SELECT A.MR_NO,B.CASE_NO,A.PAT_NAME,B.ORDERSET_CODE,"
                        + "                      SUM(CASE WHEN B.ORDER_CODE = B.ORDERSET_CODE THEN B.DISCOUNT_RATE ELSE 0 END) DISCOUNT_RATE,"
                        + "                      SUM(CASE WHEN B.ORDER_CODE = B.ORDERSET_CODE THEN B.DISPENSE_QTY ELSE 0 END) DISPENSE_QTY,"
                        + "                      SUM(B.OWN_AMT) OWN_PRICE,SUM(B.AR_AMT) OWN_AMT,B.ORDERSET_GROUP_NO "
                        + "                 FROM HRM_CONTRACTD A, HRM_ORDER B "
                        + "                WHERE A.MR_NO = B.MR_NO "
                        + "                  AND A.CONTRACT_CODE = B.CONTRACT_CODE "
                        + "                  AND B.BILL_NO = '#' "
                        + "             GROUP BY A.MR_NO,B.CASE_NO,A.PAT_NAME,B.ORDERSET_CODE,B.ORDERSET_GROUP_NO) "
                        + "      GROUP BY MR_NO,CASE_NO,PAT_NAME,ORDERSET_CODE,DISCOUNT_RATE,OWN_PRICE) A, HRM_ORDER B, SYS_UNIT C "
                        + " WHERE A.ORDERSET_CODE = B.ORDER_CODE "
                        + "   AND A.CASE_NO = B.CASE_NO "
                        + "   AND B.DISPENSE_UNIT = C.UNIT_CODE "
                        + "ORDER BY A.PAT_NAME,ORDER_DESC";// add by wanglong 20130418
        sql = sql.replaceFirst("#", billNo);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            return result;
        }
        if (result.getCount() <= 0) {
            return null;
        }
        for (int i = 0; i < result.getCount(); i++) {
            result.setData("OWN_PRICE", i, result.getDouble("OWN_PRICE", i));
            result.setData("AR_AMT", i, result.getDouble("AR_AMT", i));
            result.setData("OWN_AMT", i, result.getDouble("OWN_AMT", i));
        }
        return result;
    }
    
    /**
     * �����վݺŲ�ѯ�����嵥����
     * @param receiptNo
     * @return
     */
    public TParm getParmByBillNoMain(String billNo) {
        TParm result = new TParm();
        if (StringUtil.isNullString(billNo)) {
            // System.out.println("bill no is null");
            return null;
        }
        //�����˵��Ų�ѯ��������
        String sql =
                "SELECT B.PAT_NAME, A.ORDER_DESC||' '||A.SPECIFICATION,A.OWN_PRICE,"
                        + "A.DISPENSE_QTY, C.UNIT_CHN_DESC, A.AR_AMT, A.ORDERSET_CODE, A.ORDERSET_GROUP_NO, A.CASE_NO "
                        + " FROM HRM_ORDER A, HRM_PATADM B, SYS_UNIT C "
                        + "WHERE A.BILL_NO='#' "
                        + "  AND A.CASE_NO=B.CASE_NO "
                        + "  AND A.SETMAIN_FLG='Y' "
                        + "  AND A.DISPENSE_UNIT=C.UNIT_CODE "
                        + "ORDER BY A.CASE_NO,A.SEQ_NO";
        sql = sql.replaceFirst("#", billNo);
        // System.out.println("getParmByBillNoMain.sql="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            // System.out.println("getParmByBillNoMain.errText="+result.getErrText());
            return null;
        }
        int count = result.getCount();
        if (count <= 0) {
            // System.out.println("getParmByBillNoMain.count<=0");
            return null;
        }
        // "SELECT SUM(OWN_PRICE),SUM(AR_AMT) FROM HRM_ORDER WHERE CASE_NO='#' AND ORDERSET_CODE='#' AND ORDERSET_GROUP_NO=#";
        for (int i = 0; i < count; i++) {
            //����������Ϣ��ϸ�൥���ܼ�
            String detailSql =
                    "SELECT SUM(OWN_PRICE) OWN_PRICE,SUM(AR_AMT) AR_AMT "
                            + "  FROM HRM_ORDER "
                            + " WHERE CASE_NO = '#' "
                            + "   AND ORDERSET_CODE = '#' "
                            + "   AND ORDERSET_GROUP_NO = #";
            detailSql = detailSql.replaceFirst("#", result.getValue("CASE_NO", i))
                                 .replaceFirst("#", result.getValue("ORDERSET_CODE", i))
                                 .replaceFirst("#", result.getInt("ORDERSET_GROUP_NO", i) + "");
            // System.out.println("detailSql="+detailSql);
            TParm detail = new TParm(TJDODBTool.getInstance().select(detailSql));
            if (detail.getErrCode() != 0) {
                // out.println("getParmByBillNoMain.detail.errText="+detail.getErrText());
                return null;
            }
            result.setData("OWN_PRICE", i, detail.getDouble("OWN_PRICE", 0));
            result.setData("AR_AMT", i, detail.getDouble("AR_AMT", 0));
        }
        return result;
    }
    /**
     * �ж�һ��ҽ���Ƿ��ɾ�����������Ƿ��Ѿ�������ҩƷ�Ƿ��Ѿ���ҩ
     * @return boolean ,true:����ɾ��,false:������ɾ��
     */
    public boolean isOrderRemovable(int row) {
        if (row < 0) {
            return false;
        }
        int count = this.rowCount();
        if (count <= 0) {
            return false;
        }
        // out.println("int i="+row);
        String orderCode = this.getItemString(row, "ORDER_CODE");
        int seqNo = this.getItemInt(row, "SEQ_NO");
        String caseNo = this.getItemString(row, "CASE_NO");
        String devCode = this.getItemString(row, "DEV_CODE");
        String optItemCode = this.getItemString(row, "OPTITEM_CODE");
        String labKey = devCode + optItemCode;
        String cat1Type = this.getItemString(row, "CAT1_TYPE");
        String medApplyNo = this.getItemString(row, "MED_APPLY_NO");
        String deptAttribute = this.getItemString(row, "DEPT_ATTRIBUTE");
        if (StringUtil.isNullString(deptAttribute) && ("LIS".equalsIgnoreCase(cat1Type) || "RIS".equalsIgnoreCase(cat1Type))) {
            if (!this.getMedApply().isRemovable(cat1Type, medApplyNo, caseNo, seqNo)) {
                return false;
            }
        } else if (!StringUtil.isNullString(deptAttribute)
                && (!StringUtil.isNullString(this.getItemString(row, "EXEC_DR_CODE")))) {
            return false;
        }
        return true;
    }
    /**
     * ���ݸ����������ɵ���������
     * 20130825ʹ�ñ���ϲ����ܺ󣬴˷�����ʱû���ˣ���HRMReportSheetReportTool.getReportParm()����
     * @param mrNo
     * @param caseNo
     * @return
     */
    public TParm getReportTParm(String mrNo, String caseNo) {
        TParm result = new TParm();
        if (StringUtil.isNullString(mrNo) || StringUtil.isNullString(caseNo)) {
            // out.println("mrNo,caseNo is null");
            return null;
        }
        TParm patParm = getPatParm(caseNo);
        if (patParm == null) {
            // out.println(":patParm is null");
            return null;
        }
        result.setData("SEX_CODE", patParm.getValue("SEX_CODE", 0));
        result.setData("BIRTH_DATE", patParm.getData("BIRTH_DATE", 0));
        result.setData("PACKAGE_DESC", patParm.getValue("PACKAGE_DESC", 0));
        result.setData("MR_NO", mrNo);
        result.setData("PAT_NAME", patParm.getValue("PAT_NAME", 0));
        result.setData("MR_CODE", "TEXT", mrNo);
        String introUser = patParm.getValue("INTRO_USER", 0);
        String reportlist = patParm.getValue("REPORTLIST", 0);
        String introUserName =
                StringUtil.getDesc("SYS_OPERATOR", "USER_NAME", "USER_ID='" + introUser + "'");
        String reportlistName = getReportlist(reportlist);
        String packageCode = patParm.getValue("PACKAGE_CODE", 0);
        if (reportlistName == null) {
            reportlistName = "";
        }
        result.setData("INTRO_USER", introUserName);
        result.setData("REPORTLIST", "���˳��" + reportlistName);
        TParm orderParm = this.getReportSheet(caseNo, packageCode);
        if (orderParm == null) {
            // out.println("orderParm is null");
            return null;
        }
        if (orderParm.getErrCode() != 0) {
            // out.println("getReportTParm.getErrText="+orderParm.getErrText());
            return null;
        }
        TParm deptParm = getDeptTParm(caseNo, packageCode);
        if (deptParm == null) {
            return null;
        }
        if (deptParm.getErrCode() != 0) {
            return deptParm;
        }
        orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        orderParm.addData("SYSTEM", "COLUMNS", "EXEC_DR");
        orderParm.addData("SYSTEM", "COLUMNS", "COL1");
        orderParm.addData("SYSTEM", "COLUMNS", "COL2");
        result.setData("DEPT_TABLE", deptParm.getData());
        result.setData("ORDER_TABLE", orderParm.getData());
        return result;
    }
    
    /**
     * ���ݸ���CASE_NO��ѯPAT���ݣ��ڵ�������ʾ��
     * @param caseNo
     * @return
     */
    private TParm getPatParm(String caseNo) {
        TParm parm = new TParm();
        if (StringUtil.isNullString(caseNo)) {
            return null;
        }
        String sql =
                "SELECT B.PACKAGE_DESC,A.PAT_NAME,TO_CHAR(A.BIRTHDAY,'YYYY/MM/DD') BIRTH_DATE,A.SEX_CODE,"
                        + "A.REPORTLIST,A.INTRO_USER,A.PACKAGE_CODE "
                        + "FROM HRM_PATADM A,HRM_PACKAGEM B "
                        + "WHERE A.CASE_NO='#' "
                        + "AND A.PACKAGE_CODE=B.PACKAGE_CODE(+) ";
        sql = sql.replaceFirst("#", caseNo);
        // System.out.println("getPatParm.sql="+sql);
        parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm.getErrCode() != 0) {
            // System.out.println("getPatParm.errText="+parm.getErrText());
            return null;
        }
        int count = parm.getCount();
        if (count <= 0) {
            // System.out.println("getPatParm.count <=0");
            return null;
        }
        return parm;
    }

    /**
     * ��ÿƱ�������Ϣ��������ʹ�ã�
     * @param caseNo
     * @param packageCode
     * @return
     */
    private TParm getDeptTParm(String caseNo, String packageCode) {
        TParm result = new TParm();
        if (StringUtil.isNullString(caseNo)) {
            return null;
        }
        // String sql=GET_DEPT_TABLE_TPARM.replaceFirst("#", caseNo).replaceFirst("#",packageCode);
        // String sql=GET_DEPT_TABLE_TPARM.replaceFirst("#", caseNo).replaceFirst("#",packageCode);
        String sqlN =
                "SELECT   B.CHN_DESC, '' OPT_USER, A.DR_NOTE "
                        + " FROM   HRM_ORDER A, SYS_DICTIONARY B " + " WHERE       A.CASE_NO = '"
                        + caseNo + "'" + " AND A.DEPT_ATTRIBUTE IS NOT NULL "
                        + " AND A.DEPT_ATTRIBUTE = B.ID(+) "
                        + " AND B.GROUP_ID = 'SYS_DEPT_ATTRIBUTE' "
                        + " AND A.SETMAIN_FLG = 'Y' ORDER BY A.SEQ_NO";
        // System.out.println("getDeptTParm.sql=="+sql);
        // result=new TParm(TJDODBTool.getInstance().select(sql));
        result = new TParm(TJDODBTool.getInstance().select(sqlN));
        /**
         * if(result.getCount()<0){
         * result = new TParm(TJDODBTool.getInstance().select(sqlN));
         * }
         */
        if (result.getErrCode() != 0) {
            // System.out.println("getDeptTParm.errText="+result.getErrText());
            return null;
        }
        int count = result.getCount();
        // System.out.println("getDeptTParm.result="+result);
        if (count <= 0) {
            // System.out.println("getDeptTParm.count<=0");
            result.setErrCode(-1);
            result.setErrText("���뿪���ܼ����͵�ҽ��");
            return null;
        }
        // if(count%2!=0){
        // result.addData("CHN_DESC","");
        // result.addData("OPT_USER","");
        // result.setCount(result.getCount("CHN_DESC"));
        // }
        count = result.getCount();
        TParm parm = new TParm();
        for (int i = 0; i < count; i++) {
            // if(i%2==0){
            parm.addData("DEPT1", result.getData("CHN_DESC", i));
            parm.addData("OPT_USER1", result.getData("OPT_USER", i));
            parm.addData("DR_NOTE", result.getData("DR_NOTE", i));
            // parm.addData("TABLE_VALUE","HLine=Y");
            // }else{
            // parm.addData("DEPT2", result.getData("CHN_DESC",i));
            // parm.addData("OPT_USER2", result.getData("OPT_USER",i));
            // }
        }
        parm.addData("SYSTEM", "COLUMNS", "DEPT1");
        parm.addData("SYSTEM", "COLUMNS", "OPT_USER1");
        parm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
        // parm.addData("SYSTEM","COLUMNS", "DEPT2");
        // parm.addData("SYSTEM","COLUMNS", "OPT_USER2");
        parm.setCount(parm.getCount("DEPT1"));
        return parm;
    }

    /**
     * ����HL7��Ϣ
     * @return
     */
    public TParm sendHl7() {
        TParm result = new TParm();
        List list = new ArrayList();
        TParm parm = this.getBuffer(this.PRIMARY);
        int count = parm.getCount();
        if (count <= 0) {
            return null;
        }
        HRMPatAdm pat = new HRMPatAdm();
        for (int i = 0; i < count; i++) {
            TParm row = new TParm();
            String cat1Type = parm.getValue("CAT1_TYPE", i);
            // System.out.println("cat1Type=="+cat1Type);
            if (!StringTool.getBoolean(parm.getValue("SETMAIN_FLG"))) {
                continue;
            }
            if (!"LIS".equalsIgnoreCase(cat1Type) && !"RIS".equalsIgnoreCase(cat1Type)) {
                continue;
            }
            // System.out.println("medApplyNo="+parm.getValue("MED_APPLY_NO",i));
            if (!StringUtil.isNullString(parm.getValue("DEPT_ATTRIBUTE", i))) {
                continue;
            }
            row.setData("ADM_TYPE", "H");
            row.setData("CAT1_TYPE", parm.getValue("CAT1_TYPE", i));
            String patName = pat.getPatNameByCase(parm.getValue("CASE_NO", i));
            row.setData("PAT_NAME", patName);
            row.setData("CASE_NO", parm.getValue("CASE_NO", i));
            row.setData("ORDER_NO", parm.getValue("CASE_NO", i));
            row.setData("SEQ_NO", parm.getData("SEQ_NO", i));
            row.setData("LAB_NO", parm.getValue("MED_APPLY_NO", i));
            row.setData("FLG", "0");
            // System.out.println("row"+i+":"+row);
            list.add(row);
        }
        // System.out.println("list="+list);
        // ���ýӿ�
        result = Hl7Communications.getInstance().Hl7Message(list);
        // System.out.println(result);
        return result;
    }
    /**
     * ȡ��ҽ������
     * @return
     */
    public TParm dischargeHl7() {
        TParm result = new TParm();
        List list = new ArrayList();
        TParm parm = this.getBuffer(this.PRIMARY);
        int count = parm.getCount();
        if (count <= 0) {
            return null;
        }
        HRMPatAdm pat = new HRMPatAdm();
        for (int i = 0; i < count; i++) {
            TParm row = new TParm();
            String cat1Type = parm.getValue("CAT1_TYPE", i);
            // System.out.println("cat1Type=="+cat1Type);
            if (!"LIS".equalsIgnoreCase(cat1Type) && !"RIS".equalsIgnoreCase(cat1Type)) {
                continue;
            }
            // System.out.println("medApplyNo="+parm.getValue("DEPT_ATTRIBUTE",i));
            if (!StringUtil.isNullString(parm.getValue("DEPT_ATTRIBUTE", i))) {
                continue;
            }
            row.setData("ADM_TYPE", "H");
            row.setData("CAT1_TYPE", parm.getValue("CAT1_TYPE", i));
            String patName = pat.getPatNameByCase(parm.getValue("CASE_NO", i));
            row.setData("PAT_NAME", patName);
            row.setData("CASE_NO", parm.getValue("CASE_NO", i));
            row.setData("ORDER_NO", parm.getValue("CASE_NO", i));
            row.setData("SEQ_NO", parm.getData("SEQ_NO", i));
            row.setData("LAB_NO", parm.getValue("MED_APPLY_NO", i));
            row.setData("FLG", "1");
            // System.out.println("row"+i+":"+row);
            list.add(row);
        }
        // System.out.println("list="+list);
        // ���ýӿ�
        result = Hl7Communications.getInstance().Hl7Message(list);
        // System.out.println(result);
        return result;
    }
    /**
     *
     * @param billNo
     * @return
     */
    public boolean removeBillByBillNo(String billNo) {
        if (StringUtil.isNullString(billNo)) {
            return false;
        }
        String filterString = this.getFilter();
        String filter = "BILL_NO='" + billNo + "'";
        this.setFilter(filter);
        this.filter();
        int count = this.rowCount();
        if (count <= 0) {
            // System.out.println("removeBillByBillNo.count<=0");
            return false;
        }
        Timestamp now = this.getDBTime();
        for (int i = 0; i < count; i++) {
            this.setItem(i, "BILL_DATE", "");
            this.setItem(i, "BILL_FLG", "N");
            this.setItem(i, "BILL_NO", "");
            this.setItem(i, "BILL_USER", "");
            this.setItem(i, "OPT_USER", Operator.getID());
            this.setItem(i, "OPT_DATE", now);
            this.setItem(i, "OPT_TERM", Operator.getIP());
        }
        this.setFilter(filterString);
        this.filter();
        return true;
    }
    /**
     * ����BILL_NO��ѯ��������
     * @param billNo
     * @return
     */
    public TParm getTParmByBill(String billNo) {
        TParm result = new TParm();
        if (StringUtil.isNullString(billNo)) {
            return null;
        }
        //�����˵��Ų�ѯ���ݣ���ȫ�����ݣ����ڼ�����ã�
        result = new TParm(TJDODBTool.getInstance().select("SELECT * FROM HRM_ORDER WHERE BILL_NO = '#'".replaceFirst("#", billNo)));
        return result;
    }
    
    /**
     * ����BILL_NO�б��ѯ��������
     * @param billNoList
     * @return
     */
    public TParm getTParmByBillNoList(String billNoList) {//add by wanglong 20130510
        TParm result = new TParm();
        if (StringUtil.isNullString(billNoList)) {
            return null;
        }
        //�����˵��Ų�ѯ���ݣ���ȫ�����ݣ����ڼ�����ã�
        result = new TParm(TJDODBTool.getInstance().select("SELECT * FROM HRM_ORDER WHERE BILL_NO IN (#)".replaceFirst("#", billNoList)));
        return result;
    }
    
    /**
     * �Ƿ�������
     * @return boolean
     */
    public boolean isNumber(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * ����CASE_NO�õ�������������
     * @param caseNo
     * @return
     */
    public TParm getLisPositiveParmByCase(String caseNo) {
        TParm result = new TParm();
        if (StringUtil.isNullString(caseNo)) {
            return null;
        }
        //����CASE_NO���LIS��������
        String sql =
                "SELECT A.PAT_NAME,A.MR_NO,A.ORDER_DESC,B.TESTITEM_CHN_DESC,B.TEST_VALUE,B.TEST_UNIT,"
                        + "B.LOWER_LIMIT,B.UPPE_LIMIT,A.CASE_NO,A.SEX_CODE,A.BIRTH_DATE,A.ORDER_CODE "
                        + "   FROM MED_APPLY A, MED_LIS_RPT B " 
                        + "  WHERE A.CASE_NO = '#' "
                        + "    AND A.CAT1_TYPE = B.CAT1_TYPE "
                        + "    AND A.APPLICATION_NO = B.APPLICATION_NO "
                        + "    AND A.ORDER_NO = B.ORDER_NO "
                        + "    AND A.SEQ_NO=B.SEQ_NO "
                        + "    AND A.ADM_TYPE = 'H' "
                        + "    AND (B.TEST_VALUE > B.UPPE_LIMIT OR B.TEST_VALUE < B.LOWER_LIMIT) "
                        // + "    AND (TEST_VALUE <> '' OR TEST_VALUE IS NOT NULL) "
                        // + "    AND (UPPE_LIMIT <> '' OR UPPE_LIMIT IS NOT NULL) "
                        // + "    AND (LOWER_LIMIT <> '' OR LOWER_LIMIT IS NOT NULL) "
                        + " ORDER BY A.SEQ_NO,B.RPDTL_SEQ";
        sql = sql.replaceFirst("#", caseNo);
        // System.out.println("getLisPositiveParmByCase.sql="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            // System.out.println("getLisPositiveParmByCase.errText="+result.getErrText());
            return null;
        }
        int conut = result.getCount();
        for (int i = conut - 1; i >= 0; i--) {
            String testValue = result.getValue("TEST_VALUE", i);
            String uppeLimit = result.getValue("UPPE_LIMIT", i);
            String lowerLimit = result.getValue("LOWER_LIMIT", i);
            if (isNumber(testValue) && isNumber(uppeLimit) && isNumber(lowerLimit)) {
                Double test = Double.parseDouble(testValue);
                // System.out.println("����ֵ==="+test);
                Double uppe = Double.parseDouble(uppeLimit);
                // System.out.println("����ֵ==="+uppe);
                Double lower = Double.parseDouble(lowerLimit);
                // System.out.println("����ֵ==="+uppe);
                if (test > uppe) {
                    result.setData("TEST_VALUE", i, result.getValue("TEST_VALUE", i) + "��");
                }
                if (test < lower) {
                    result.setData("TEST_VALUE", i, result.getValue("TEST_VALUE", i) + "��");
                }
                if (test <= uppe && test >= lower) {
                    result.removeRow(i);
                }
            } else {
                result.removeRow(i);
            }
        }
        return result;
    }
    /**
     * ����CASE_NO�õ������������
     * @param caseNo
     * @return
     */
    public TParm getRisPositiveParmByCase(String caseNo) {
        TParm result = new TParm();
        if (StringUtil.isNullString(caseNo)) {
            return null;
        }
        String sql =
                "SELECT A.PAT_NAME,A.MR_NO,A.ORDER_DESC,B.OUTCOME_DESCRIBE,B.OUTCOME_CONCLUSION "
                        + "   FROM MED_APPLY A, MED_RPTDTL B "
                        + "   WHERE A.CASE_NO = '#' "
                        + "     AND A.CAT1_TYPE = B.CAT1_TYPE "
                        + "     AND A.APPLICATION_NO = B.APPLICATION_NO "
                        + "     AND A.ADM_TYPE = 'H' " 
                        + "     AND B.OUTCOME_TYPE = 'T' "
                        + "   ORDER BY A.SEQ_NO,B.RPDTL_SEQ";
        sql = sql.replaceFirst("#", caseNo);
        // System.out.println("getRisPositiveParmByCase.sql="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            // System.out.println("getRisPositiveParmByCase.errText="+result.getErrText());
            return null;
        }
        return result;
    }
    /**
     * ����ORDER�����Ѿ�����RECEIPT_NO��ʼ������ѭ������ÿ�ʼ�¼��
     * �������LIS����RIS����ɾ����RECEIPT_NO��
     * �����ϸ��ͬ��������
     * �����RIS/LIS�������ȡ����APP_NO��ɾ��MED_APPLY������
     * @return
     */
    public boolean deleteMedApplyByAppNo() {
        int count = this.rowCount();
        if (count <= 0) {
            return false;
        }
        Timestamp now = this.getDBTime();
        for (int i = 0; i < count; i++) {
            String cat1Type = this.getItemString(i, "CAT1_TYPE");
            if (!"LIS".equalsIgnoreCase(cat1Type) && !"RIS".equalsIgnoreCase(cat1Type)) {
                this.setItem(i, "RECEIPT_NO", "");
                this.setItem(i, "OPT_USER", Operator.getID());
                this.setItem(i, "OPT_DATE", now);
                this.setItem(i, "OPT_TERM", Operator.getIP());
            } else {
                if (!TypeTool.getBoolean(this.getItemData(i, "SETMAIN_FLG"))) {
                    this.setItem(i, "RECEIPT_NO", "");
                    this.setItem(i, "OPT_USER", Operator.getID());
                    this.setItem(i, "OPT_DATE", now);
                    this.setItem(i, "OPT_TERM", Operator.getIP());
                } else {
                    String appNo = this.getItemString(i, "MED_APPLY_NO");
                    this.getMedApply().onDeleteOrder(appNo);
                    this.setItem(i, "RECEIPT_NO", "");
                    this.setItem(i, "OPT_USER", Operator.getID());
                    this.setItem(i, "OPT_DATE", now);
                    this.setItem(i, "OPT_TERM", Operator.getIP());
                }
            }
        }
        return true;
    }
    /**
     * �ж�ҽ�����Ƿ����ܼ�ҽ��
     * @return
     */
    public boolean isHaveMainOrder() {
        int count = this.rowCount();
        for (int i = 0; i < count; i++) {
            // System.out.println("REVIEW====="+this.getItemString(i, "DEPT_ATTRIBUTE"));
            if ("04".equalsIgnoreCase(this.getItemString(i, "DEPT_ATTRIBUTE"))) {
                return true;
            }
        }
        return false;
    }
    /**
     * �Ƿ�ɻᷢHL7��Ϣ
     * @param i
     * @return
     */
    public boolean isDischargableSet(int i) {
        String cat1Type = this.getItemString(i, "CAT1_TYPE");
        boolean setMainFlg = StringTool.getBoolean(this.getItemString(i, "SETMAIN_FLG"));
        boolean isNotAttr = !StringUtil.isNullString(this.getItemString(i, "DEPT_ATTRIBUTE"));
        if (("LIS".equalsIgnoreCase(cat1Type) || "RIS".equalsIgnoreCase(cat1Type)) && setMainFlg && isNotAttr) {
            return true;
        }
        return false;
    }
    /**
     * ���SEQ_NO
     * @return
     */
    public boolean fillSeqNo() {
        int count = this.rowCount();
        if (count <= 0) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            if (!this.isActive(i)) {
                continue;
            }
            this.setItem(i, "SEQ_NO", i + 1);
        }
        return true;
    }
    /**
     * ����SEQ_NO
     * @param caseNo String
     * @return int
     */
    public boolean setSeqNoOrder(String caseNo) {
        String sql = "SELECT NVL(MAX(SEQ_NO),0)+1 AS SEQ_NO FROM HRM_ORDER WHERE CASE_NO='" + caseNo + "'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        int maxSeq = parm.getInt("SEQ_NO", 0);
        int count = this.rowCount();
        if (count <= 0) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            if (!this.isActive(i)) {
                continue;
            }
            if (!"N".equals(getItemString(i, "NEW_FLG"))) {
                this.setActive(i, false);
                continue;
            }
            TParm temp = this.getRowParm(i);
            this.setItem(i, "SEQ_NO", maxSeq);
            int rowMedCount = this.getMedApply().rowCount();
            for (int j = 0; j < rowMedCount; j++) {
                if (!this.getMedApply().isActive(j)) continue;
                TParm tempMed = this.getMedApply().getRowParm(j);
                // System.out.println("tempMed"+tempMed);
                if (temp.getValue("CAT1_TYPE").equals(tempMed.getValue("CAT1_TYPE"))
                        && temp.getValue("MED_APPLY_NO").equals(tempMed.getValue("APPLICATION_NO"))
                        && temp.getValue("CASE_NO").equals(tempMed.getValue("ORDER_NO"))
                        && temp.getValue("ORDER_CODE").equals(tempMed.getValue("ORDER_CODE"))) {
                    this.getMedApply().setItem(j, "SEQ_NO", maxSeq);
                }
            }
            maxSeq++;
        }
        return true;
    }
    /**
     * �õ�SEQNO����
     * @param deptCode String
     * @return boolean
     */
    public boolean fillSeqNo(String deptCode) {
        String buff = this.isFilter() ? this.FILTER : this.PRIMARY;
        int newRow[] = this.getNewRows(buff);
        for (int temp : newRow) {
            this.setItem(temp, "SEQ_NO", getMaxSeq());
            this.setItem(temp, "DEPT_CODE", deptCode);
        }
        return true;
    }
    /**
     * �õ����SEQ_NO
     * @return int
     */
    public int getMaxSeq() {
        int seqNo = 0;
        String buff = this.isFilter() ? this.FILTER : this.PRIMARY;
        TParm parm = this.getBuffer(buff);
        int rowCount = parm.getCount();
        for (int i = 0; i < rowCount; i++) {
            if (parm.getInt("SEQ_NO", i) > seqNo) seqNo = parm.getInt("SEQ_NO", i);
        }
        return seqNo + 1;
    }
    /**
     * ���ݸ���REPORTLIST���õ��ö�������
     * @param reportList
     * @return
     */
    public String getReportlist(String reportList) {
        StringBuffer sb = new StringBuffer();
        if (StringUtil.isNullString(reportList)) {
            return null;
        }
        String sql = "SELECT ID||'.'||CHN_DESC NAME FROM SYS_DICTIONARY WHERE GROUP_ID='#' ORDER BY SEQ".replaceFirst("#", reportList);
        // System.out.println("getReportlist=="+sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm.getErrCode() != 0 || parm.getCount() <= 0) {
            return null;
        }
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            sb.append(parm.getValue("NAME", i)).append("  ");
        }
        return sb.toString();
    }
    /**
     * ���ݸ���CONTRACT_CODEд��ORDER
     * @param contractCode
     */
    public void onCompay(String contractCode) {
        if (StringUtil.isNullString(contractCode)) {
            return;
        }
        String buff = this.isFilter() ? this.FILTER : this.PRIMARY;
        int newRow[] = this.getNewRows(buff);
        for (int temp : newRow) {
            this.setItem(temp, "CONTRACT_CODE", contractCode);
        }
    }
    /**
     * ȥ���������ظ��ļ�¼    add by wanglong 20130131
     * @param a
     * @return
     */
    public String[] arrayUnique(String[] a) {
        List<String> list = new LinkedList<String>();
        for (int i = 0; i < a.length; i++) {
            if (!list.contains(a[i])) {
                list.add(a[i]);
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }
    /**
     * ��������ȡ��    add by wanglong 20130131
     * @return
     */
    public int ceil(int a, int b) {
        return (double) a / (double) b > a / b ? a / b + 1 : a / b;
    }
    
    /**
     * ���ɳ���1000Ԫ�ص�IN���
     * @param name
     * @param Nos
     * @return
     */
    public static String getInStatement(String name, List Nos) {//add by wanglong 20130806
        if (Nos.size() == 0) {
            return " 1=1 ";
        }
        StringBuffer inStr = new StringBuffer();
        inStr.append(name + " IN ('");
        for (int i = 0; i < Nos.size(); i++) {
            inStr.append(Nos.get(i).toString());
            if ((i + 1) != Nos.size()) {
                if ((i + 1) % 999 != 0) {
                    inStr.append("','");
                } else if (((i + 1) % 999 == 0)) {
                    inStr.append("') OR " + name + " IN ('");
                }
            }
        }
        inStr.append("')");
        return inStr.toString();
    }
}
