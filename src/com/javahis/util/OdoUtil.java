package com.javahis.util;

import java.awt.Color;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import jdo.bil.BIL;
import jdo.odo.OpdOrder;
import jdo.opd.DiagRec;
import jdo.opd.DiagRecList;
import jdo.opd.DrugAllergy;
import jdo.opd.DrugAllergyList;
import jdo.opd.Medhistory;
import jdo.opd.MedhistoryList;
import jdo.opd.ODO;
import jdo.opd.OPDSysParmTool;
import jdo.opd.Order;
import jdo.opd.OrderHistory;
import jdo.opd.OrderHistoryList;
import jdo.opd.OrderList;
import jdo.opd.PrescriptionList;
import jdo.opd.TotQtyTool;
import jdo.pha.PhaBaseTool;
import jdo.reg.Reg;
import jdo.sys.CTZTool;
import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import jdo.sys.Pat;
import jdo.sys.SYSOperatorTool;
import jdo.sys.SYSOrderSetDetailTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import jdo.sys.SYSFeeTool;
import jdo.bil.BILStrike;
import jdo.sys.DeptTool;

/**
 *
 * <p>
 * Title: ODO��������
 *
 * <p>
 * Description: ODO��������
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company: javahis
 *
 * @author ehui 20081009
 * @version 1.0
 */
public class OdoUtil {

    private static String chnMedGroupName = "4";
    public static TDataStore ICD = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
    public static TDataStore SYS_FEE = TIOM_Database.getLocalTable("SYS_FEE");
    private static TParm sysfeeParm = SYS_FEE.getBuffer(SYS_FEE.isFilter() ?
        SYS_FEE.FILTER : SYS_FEE.PRIMARY);
    private static Vector sysFeeCode = (Vector) sysfeeParm.getData("ORDER_CODE");

    private static TParm icdParm = ICD.getBuffer(ICD.isFilter() ? ICD.FILTER :
                                                 ICD.PRIMARY);
    private static Vector icdCode = (Vector) icdParm.getData("ICD_CODE");

    private static final String GET_PHABASE = "SELECT * FROM PHA_BASE";
    private static TDS phaBase;
    /**
     * ���ݸ�����������������ORDER��TParm
     * @param odo ODO����
     * @param groupName String �����ORDERLIST�����ORDERLIST������
     * @param indexOfOrderList String PRESCRIPTIONLIST�и�ORDERLIST���±�
     * @return TParm ����ORDER��TParm
     */
    public static TParm newOrderTParm(ODO odo,
                                      String groupName, String indexOfOrderList) {
        OrderList ol;
        int index = TCM_Transform.getInt(indexOfOrderList);
        //PRESCRIPTIONLIST��û�д�ORDERLIST
        if (odo.getPrescriptionList().getOrderList(groupName, index) == null) {
            ol = odo.getPrescriptionList().newOrderList(groupName);
        }
        //�д�ORDERLIST���ڴ�ORDERLIST��ȡ�����SEQ_NO����׷���µ�ORDER
        else {
            ol = odo.getPrescriptionList().getOrderList(groupName, index);
        }
        Order o = ol.newOrder();
        o.setSeqNo(ol.size());
        TParm parm = new TParm();
        parm = o.getParm();
        //�����ۿ۽����һ��
        calculatePayAmount(parm);
        return parm;

    }

    /**
     * ���ݸ�����������������һ�����ݵ�TParm����4��Order
     * @return TParm ����ORDER��TParm
     * @param ol OrderList
     * @return TParm
     */
    public static TParm newOrderTParmChn(OrderList ol) {
        if (ol == null)
            return null;
        Order o;
        //��ҩ��Ƭһ�����ݾ���4��ҽ��
        for (int i = 0; i < 4; i++) {
            o = ol.newOrder();
        }
        TParm result = chnMedicReArrange(ol.getParm());
        return result;
    }

    /**
     *
     * @param odo ODO
     * @param indexOfOrderList String
     */
    public static void deleteOrderChn(ODO odo, String indexOfOrderList) {

    }

    /**
     * �����ۿ۽��
     * @param parm TParm
     * @return TParm
     */
    public static TParm calculatePayAmount(TParm parm) {
        int count = parm.getCount();
        if (count == -1) {
            double discountAmount = parm.getDouble("OWN_AMT") -
                parm.getDouble("AR_AMT");
            if (discountAmount == -0.0)
                discountAmount = 0.0;
            parm.setData("PAYAMOUNT", discountAmount);
            return parm;
        }
        for (int i = 0; i < count; i++) {
            double discountAmount = parm.getDouble("OWN_AMT", i) -
                parm.getDouble("AR_AMT", i);
            if (discountAmount == -0.0)
                discountAmount = 0.0;
            parm.addData("PAYAMOUNT", discountAmount);
        }
        return parm;
    }

    /**
     * �������²�����ѯ��ʵ����ODO����
     * @param caseNo String
     * @param mrNo String
     * @param admType String
     * @return ODO
     */
    public static ODO queryByCaseNO(String caseNo, String mrNo, String admType) {
        ODO odo = new ODO();
        Pat pat = Pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            System.out.println("pat is null");
            return null;
        }

        Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
        if (reg == null) {
            System.out.println("reg is null");

            return null;
        }

        odo.setPat(pat);
        odo.setReg(reg);
        if (!odo.onQuery(caseNo, admType)) {

            return null;
        }
        odo.setAdmType(admType);
        return odo;
    }

    /**
     * ΪRX_NO�ؼ�׼����ʾ������
     * @param odo ODO
     * @param groupName String
     * @return TParm
     */
    public static TParm prepareRxNoTParm(ODO odo, String groupName) {
        Vector prescrption = odo.getPrescriptionList().getGroup(groupName);
        if (prescrption == null || prescrption.size() == 0)
            return null;
        OrderList ol = new OrderList();
        String drName = "";
        TParm rxparm = new TParm();
        int orignalrows = prescrption.size();
        for (int i = 0; i < orignalrows; i++) {
            ol = (OrderList) prescrption.get(i);
            drName = OperatorTool.getInstance().getOperatorName(ol.getDrCode());
            rxparm.addData("ID", ol.getPresrtNo());
            rxparm.addData("TEXT", "��" + ol.getPresrtNo() + "��" + drName);
        }
        rxparm.setData("ACTION", "COUNT", orignalrows);
        return rxparm;
    }

    /**
     * Ϊ�������ܼ�����������Ƿ�ȫ����ɾ���������ݣ�ODO����ʹ��
     * @param odo ODO
     * @return boolean
     */
    public static boolean checkDataForOdo(ODO odo) {
        if (!checkDataForOrder(odo)) {
            return false;
        }
        if (!checkDataForMedHistory(odo.getMedhistoryList())) {
            return false;
        }
        if (!checkDataForDrugAllergy(odo.getDrugAllergyList())) {
            return false;
        }
        return true;
    }

    /**
     * Ϊ�������ܼ�����������Ƿ�ȫ������ʷ����ʹ��
     * @param mhl MedhistoryList
     * @return boolean
     */
    public static boolean checkDataForMedHistory(MedhistoryList mhl) {
        if (mhl == null) {
            return false;
        }
        int mhlCount = 0;
        if (mhl != null) {
            mhlCount = mhl.size();
        }

        for (int i = mhlCount - 1; i >= 0; i--) {
            if (i == -1)
                return false;
            Medhistory mh = mhl.getMedhistory(i);
            if (mh == null)
                continue;
            if (StringUtil.isNullString(mh.getIcdCode())) {
                mhl.removeData(i);
                continue;
            }
            if (StringUtil.isNullString(mh.getIcdType())) {
                mhl.removeData(i);
                continue;
            }
            if (StringUtil.isNullString(mh.getAdmDate())) {
                mhl.removeData(i);
                continue;
            }
        }
        return true;
    }

    /**
     * Ϊ�������ܼ�����������Ƿ�ȫ������ʷ����ʹ��
     * @param dal DrugAllergyList
     * @return boolean
     */
    public static boolean checkDataForDrugAllergy(DrugAllergyList dal) {
        if (dal == null) {
            return false;
        }
        int dalCount = 0;
        if (dal != null) {
            dalCount = dal.size();
        }
        for (int i = dalCount - 1; i >= 0; i--) {
            if (i == -1)
                return false;
            DrugAllergy da = dal.getDrugAllergy(i);
            if (da == null)
                continue;
            if (StringUtil.isNullString(da.getMrNo())) {
                dal.removeData(i);
                continue;
            }
            if (StringUtil.isNullString(da.getAdmDate())) {
                dal.removeData(i);
                continue;
            }
            if (StringUtil.isNullString(da.getDrugType())) {
                dal.removeData(i);
                continue;
            }
            if (StringUtil.isNullString(da.getDrugOringrdCode())) {
                dal.removeData(i);
                continue;
            }
        }
        return true;
    }

    /**
     * Ϊ�������ܼ�����������Ƿ�ȫ����ϱ���ʹ��
     * @param drl DiagRecList
     * @return boolean
     */
    public static boolean checkDataForDiagRec(DiagRecList drl) {
        if (drl == null) {
            return false;
        }
        boolean mainDiag = false;
        int drlCount = 0;
        String maindiagFlg = "";
        if (drl != null) {
            drlCount = drl.size();
        }
        for (int i = drlCount - 1; i >= 0; i--) {

            if (i == -1)
                return false;
            DiagRec dr = drl.getDiagRec(i);
            if (dr == null) {
                continue;
            }
            if (StringUtil.isNullString(dr.getIcdType())) {
                drl.removeData(i);
                continue;
            }
            if (StringUtil.isNullString(dr.getIcdCode())) {
                drl.removeData(i);
                continue;
            }
            maindiagFlg = dr.getMainDiagFlg();
            if (!StringUtil.isNullString(maindiagFlg) &&
                "Y".equalsIgnoreCase(maindiagFlg)) {
                mainDiag = true;
            }
        }
        drlCount = drl.size();
        if (!mainDiag && drlCount >= 0) {
            return false;
        }
        return true;
    }

    /**
     * Ϊ�������ܼ�����������Ƿ�ȫ��Order����ʹ��
     * @param odo ODO
     * @return boolean Y:�棬�������������������ݿ�������N���٣�����������ȫ
     */
    public static boolean checkDataForOrder(ODO odo) {
        if (odo == null)
            return false;
        PrescriptionList pl = odo.getPrescriptionList();
        String[] groupName = pl.getGroupNames();
        OrderList ol = new OrderList();

        Order o = new Order();
        int orderListCount = groupName.length;
        int orderCount = 0;

        for (int i = 0; i < orderListCount; i++) {
            orderCount = pl.getGroupPrsptSize(groupName[i]);
            for (int j = 0; j < orderCount; j++) {
                ol = pl.getOrderList(groupName[i], j);
                //ͬһ�Ŵ���ǩ��ִ�п���Ҫһ��
                String rbdept = ol.getExecDeptCode();
                int order = ol.size();
                for (int x = order - 1; x >= 0; x--) {
                    if (x == -1) {
                        return false;
                    }

                    o = ol.getOrder(x);

                    if (o == null) {
                        continue;
                    }

                    if (StringUtil.isNullString(o.getOrderCode())) {
                        ol.removeData(x);
                        continue;
                    }
                }
            }
        }

        return true;
    }

    /**
     * ɾ��ǰ�ж��Ƿ����ڵĲ�����Ա�뿪���˼�¼����Ա��ͬ����ͬ�򷵻�Y���粻ͬ�򷵻�N��������ɾ��
     * @param drl DiagRecList
     * @param drCode String
     * @return boolean Y:�棬N����
     */
    public static boolean checkDiagRecForDelete(DiagRecList drl, String drCode) {
        if (drl == null) {
            return false;
        }
        int drlCount = 0;
        if (drl != null) {
            drlCount = drl.size();
        }
        String thisdrCode = "";
        boolean allow = true;
        for (int i = 0; i < drlCount; i++) {
            DiagRec dr = drl.getDiagRec(i);
            thisdrCode = dr.getDrCode();
            if (StringUtil.isNullString(thisdrCode) &&
                (!thisdrCode.equalsIgnoreCase(drCode))) {
                allow = false;
            }
        }
        if (!allow) {
            return false;
        }
        return true;
    }

    /**
     * �ж�ĳ����ǩ�Ƿ��Ѿ��Ƽ�
     * @param odo ODO
     * @return boolean
     */
    public static boolean isChargedOrder(ODO odo) {

        Order o = new Order();
        String bilflg = "";
        PrescriptionList pl = odo.getPrescriptionList();
        String[] groupName = pl.getGroupNames();
        OrderList ol = new OrderList();
        int orderListCount = groupName.length;
        int orderCount = 0;
        for (int i = 0; i < orderListCount; i++) {
            orderCount = pl.getGroupPrsptSize(groupName[i]);
            for (int j = 0; j < orderCount; j++) {
                ol = pl.getOrderList(groupName[i], j);
                int order = ol.size();
                for (int x = 0; x < order; x++) {
                    o = ol.getOrder(x);
                    bilflg = o.getBillFlg();
                    if (StringUtil.isNullString(bilflg) &&
                        "Y".equalsIgnoreCase(bilflg)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * �ж�ĳ��ҽ���Ƿ��ԼƼ�
     * @param order Order
     * @return boolean
     */
    public static boolean isChargedOrder(Order order) {
        if (order == null)
            return true;
        String billflg = TCM_Transform.getString(order.getBillFlg());
        if ("Y".equalsIgnoreCase(billflg))
            return true;
        return false;
    }

    /**
     * ���ݸ����Ĵ������ͺ��±�ɾ������OrderList
     * @param ol OrderList
     * @return boolean
     */
    public static boolean deleteOrderList(OrderList ol) {
        if (ol == null)
            return false;
        int count = ol.size();
        Order o = new Order();
        for (int i = 0; i < count; i++) {
            o = ol.getOrder(i);
            if (o == null)
                continue;
            if (OdoUtil.isChargedOrder(o)) {
                return false;
            }
        }
        ol.removeDataAll();
        return true;
    }

    /**
     * ����TABLE�����кŷ��ظ����������ĸ�Order
     * @param row TABLE �е��к�
     * @param column TABLE�е��к�
     * @return OrderList�е��±�
     */
    public static int calculateOrderIndexForChnMed(int row, int column) {
        column = column / 3;
        row = row * 4;
        return row + column;
    }

    /**
     * Ϊ����TABLE��ʾ�õ�����ҩ��TPARM
     * @param parm TParm
     * @return TParm
     */
    public static TParm chnMedicReArrange(TParm parm) {
        if (parm == null)
            return parm;
        TParm result = new TParm();
        int index = parm.getCount();
        for (int j = 0; j < index; j++) {
            int mod = j % 4 + 1;
            String orderCode = TCM_Transform.getString(parm.getData(
                "ORDER_DESC", j));
            result.setData("ORDER_CODE" + mod, j, orderCode);
            result.setData("TAKE_QTY" + mod, j, parm.getData("MEDI_QTY", j));
            result.setData("TOT_QTY" + mod, j, parm.getData("DOSAGE_QTY", j));
            result.setData("DCTEXCEP_CODE" + mod, j,
                           parm.getData("DCTEXCEP_CODE", j));
            result.setData("RELEASE_FLG" + mod, j,
            		parm.getData("RELEASE_FLG", j));
        }
        return result;
    }

    /**
     * ɾ��Order����ʱ��������׼���ã�����OrderHistory��
     * @param odo ODO ����
     * @return �棺׼���ɹ����٣�׼��ʧ��
     */
    public static boolean prepareOrderHistory(ODO odo) {
        if (odo == null)
            return false;
        Order o = new Order();
        PrescriptionList pl = odo.getPrescriptionList();
        String[] groupName = pl.getGroupNames();
        OrderList ol = new OrderList();
        OrderHistoryList ohl = new OrderHistoryList();
        odo.setOrderhistoryList(ohl);
        OrderHistory oh = new OrderHistory();
        int orderListCount = groupName.length;
        int orderCount = 0;
        for (int i = 0; i < orderListCount; i++) {
            orderCount = pl.getGroupPrsptSize(groupName[i]);
            for (int j = 0; j < orderCount; j++) {
                ol = pl.getOrderList(groupName[i], j);
                int order = ol.size();
                for (int x = 0; x < order; x++) {
                    o = ol.getOrder(x);
                    oh = ohl.newOrderHistory();

                    transferOrderToOrderHistory(o, oh);
                }
            }
        }
//		odo.setOrderhistoryList(ohl);
        return false;
    }

    public static void transferOrderToOrderHistory(Order o, OrderHistory oh) {
        if (o == null || oh == null)
            return;
        oh.setCaseNo(o.getCaseNo());
        oh.setRxNo(o.getRxNo());
        oh.setSeqNo(o.getSeqNo());
        oh.setPresrtNo(o.getPresrtNo());
        oh.setRegionCode(o.getRegionCode());
        oh.setMrNo(o.getMrNo());
        oh.setAdmType(o.getAdmType());
        oh.setRxType(o.getRxType());
        oh.setReleaseFlg(o.getReleaseFlg());
        oh.setLinkmainFlg(o.getLinkmainFlg());
//		oh.setLinkNo(o.getLinkNo());
        oh.setOrderCode(o.getOrderCode());
        oh.setOrderDesc(o.getOrderDesc());
        oh.setGoodsDesc(o.getGoodsDesc());
//		oh.setOrderCat1(o.getOrderCat1());
//		oh.setTakeQty(o.getTakeQty());
        oh.setMediUnit(o.getMediUnit());
        oh.setFreqCode(o.getFreqCode());
        oh.setRouteCode(o.getRouteCode());
        oh.setTakeDays(o.getTakeDays());
//		oh.setTotQty(o.getTotQty());
//		oh.setDgtTot(o.getDgtTot());
        oh.setDispenseUnit(o.getDispenseUnit());
//		oh.setOpdgiveboxFlg(o.getGiveboxFlg());
        oh.setOwnPrice(o.getOwnPrice());
        oh.setNhiPrice(o.getNhiPrice());
//		oh.setDiscnrate(o.getDiscnrate());
        oh.setOwnAmt(o.getOwnAmt());
        oh.setTotAmt(o.getArAmt());
        oh.setDescription(o.getDrNote());
        oh.setNsNote(o.getNsNote());
        oh.setDrCode(o.getDrCode());
        oh.setOrderDate(o.getOrderDate());
        oh.setDeptCode(o.getDeptCode());
        oh.setRborderDeptCode(o.getExecDeptCode());
        oh.setSetmainFlg(o.getSetmainFlg());
        oh.setOrdsetGroupNo(o.getOrderSetGroupNo());
        oh.setOrdersetCode(o.getOrdersetCode());
        oh.setHideFlg(o.getHideFlg());
        oh.setRpttypeCode(o.getRpttypeCode());
        oh.setOptitemCode(o.getOptitemCode());
        oh.setDevCode(o.getDevCode());
        oh.setMrCode(o.getMrCode());
        oh.setFileNo(o.getFileNo());
        oh.setDegreeCode(o.getDegreeCode());
        oh.setUrgentFlg(o.getUrgentFlg());
        oh.setInspayType(o.getInspayType());
        oh.setPhaType(o.getPhaType());
        oh.setDoseType(o.getDoseType());
        oh.setExpensiveFlg(o.getExpensiveFlg());
        oh.setPrinttypeflgInfant(o.getPrinttypeflgInfant());
        oh.setCtrldrugclassCode(o.getCtrldrugclassCode());
        oh.setDctagentCode(o.getDctagentCode());
        oh.setDctexcepCode(o.getDctexcepCode());
        oh.setDctTakeQty(o.getDctTakeQty());
        oh.setPackageTot(o.getPackageTot());

    }

    /**
     * ���ݲ������պʹ���Ľ������ڣ����㲡�����䲢�����Ƿ�Ϊ��ͯ�Բ�ͬ����ʽ��ʾ���䣬���Ƕ�ͯ����ʾX��X��X�գ����������ʾx��
     * @param birthday Timestamp
     * @param t Timestamp
     * @return String
     */
    public static String showAge(Timestamp birthday, Timestamp t) {
        String age = "";
        String[] res;
        if (birthday == null)
            return age;
        if (t == null)
            return age;
        res = StringTool.CountAgeByTimestamp(birthday, t);
        if (OPDSysParmTool.getInstance().isChild(birthday)) {
            if ("en".equalsIgnoreCase(Operator.getLanguage())) {
                age = res[0] + "Y" + res[1] + "M" + res[2] + "D";
            }
            else {
                age = res[0] + "��" + res[1] + "��" + res[2] + "��";
            }

        }
        else {
            if ("en".equalsIgnoreCase(Operator.getLanguage())) {
                age = res[0] + "Y";
            }
            else {
                age = res[0] + "��";
            }

        }
        return age;
    }

    /**
     * ���ݲ������պʹ���Ľ������ڣ����㲡�����䲢�����Ƿ�Ϊ��ͯ�Բ�ͬ����ʽ��ʾ���䣬���Ƕ�ͯ����ʾX��X��X�գ����������ʾx��(Ӣ�İ�)
     * @param birthday Timestamp
     * @param t Timestamp
     * @return String
     */
    public static String showEngAge(Timestamp birthday, Timestamp t) {

        String age = "";
        String[] res;
        if (birthday == null)
            return age;
        if (t == null)
            return age;
        res = StringTool.CountAgeByTimestamp(birthday, t);
        if (OPDSysParmTool.getInstance().isChild(birthday)) {
            age = res[0] + "Y" + res[1] + "M" + res[2] + "D";
        }
        else {
            age = res[0] + "Y";
        }
        return age;

    }

    /**
     * ���ݲ������ϵ�LMP����õ������ڼ���������
     * @param lmp Timestamp
     * @return Timestamp
     */
    public Timestamp getDate(Timestamp lmp) {
        if (lmp == null) {
            return null;
        }
        String lmpdate = StringTool.getString(lmp, "yyyyMMdd");
        /*Ԥ����=LMP+9����+7��*/
        String L_year = String.valueOf(lmpdate.substring(0, 4));
        String L_month = String.valueOf(lmpdate.substring(4, 6));
        String L_day = String.valueOf(lmpdate.substring(6, 8));
        /*���ж��·�+9���Ƿ����12����*/
        L_month = String.valueOf(Integer.parseInt(L_month) + 9);
        if (Integer.parseInt(L_month) > 12) {
            L_year = String.valueOf(Integer.parseInt(L_year) + 1);
            L_month = String.valueOf(Integer.parseInt(L_month) - 12);
        }
        /**
         * �ж���+7���Ƿ���ڸ��·ݵ���������������·ݱ����һ�·�
         */
        L_day = String.valueOf(Integer.parseInt(L_day) + 7);
        /**
         * �Ƿ�Ϊ����
         */
        if (StringTool.IsLeapYear(L_year)) {
            if (L_month.equals("2") || L_month.equals("02")) {
                if (Integer.parseInt(L_day) > 29) {
                    L_month = String.valueOf(Integer.parseInt(L_month) + 1);
                    L_day = String.valueOf(Integer.parseInt(L_day) - 29);
                }
            }
        }
        else {
            if (L_month.equals("2") || L_month.equals("02")) {
                if (Integer.parseInt(L_day) > 28) {
                    L_month = String.valueOf(Integer.parseInt(L_month) + 1);
                    L_day = String.valueOf(Integer.parseInt(L_day) - 28);
                }
            }
        }
        if (!L_month.equals("2") && !L_month.equals("02")) {
            if (L_month.equals("1") || L_month.equals("3") ||
                L_month.equals("5") ||
                L_month.equals("7") ||
                L_month.equals("8") || L_month.equals("10") ||
                L_month.equals("12")) {
                if (Integer.parseInt(L_day) > 31) {
                    L_month = String.valueOf(Integer.parseInt(L_month) + 1);
                    L_day = String.valueOf(Integer.parseInt(L_day) - 31);
                }
                else {}
            }
            else {
                if (Integer.parseInt(L_day) > 30) {
                    L_month = String.valueOf(Integer.parseInt(L_month) + 1);
                    L_day = String.valueOf(Integer.parseInt(L_day) - 30);
                }
            }
        }
        if (Integer.parseInt(L_month) > 12) {
            L_year = String.valueOf(Integer.parseInt(L_year) + 1);
            L_month = String.valueOf(Integer.parseInt(L_month) - 12);
        }

        if (L_month.length() < 2)
            L_month = "0" + L_month;
        if (L_day.length() < 2)
            L_day = "0" + L_day;
        String MedicineDate = String.valueOf(L_year + L_month + L_day);
        return StringTool.getTimestamp(MedicineDate, "yyyyMMdd");
    }

    /**
     * ���㻳������
     * @param t1 ��������
     * @param t2 LMP
     * @return week int ��������
     */
    public static int getPreWeek(Timestamp t1, Timestamp t2) {
        /*�Զ����㻳������=ǿ�ƽ�λ((��������-LMP+1)/7)*/
        if (t1 == null || t2 == null) {
            return 0;
        }
        String date = t1.toString().trim().replaceAll("-", "");
        date = date.substring(0, date.indexOf(" "));
        float Medicinecount = (Float.parseFloat(String.valueOf(StringTool.
            getDateDiffer(t1, t2) + 1))) / 7;
        BigDecimal bd = new BigDecimal(Medicinecount);
        if (bd.signum() == -1) {
            bd = bd.negate();
        }
        int IntgerValue = bd.intValue();
        if (bd.scale() > 0)
            IntgerValue++;
        return IntgerValue; /*��������*/
    }
    
    /**
     * ���㻳����������ȷ���죩
     * @param t1 ��ǰʱ����߾���ʱ��
     * @param t2 LMP
     * @return
     */
    public static String getPreWeekNew(Timestamp t1, Timestamp t2){
    	if (t1 == null || t2 == null) {
            return "";
        }
    	int count = StringTool.getDateDiffer(t1, t2);
    	int weekCount = count/7;
    	int dateCount = count%7;
    	
    	//add by huangtt 20170112 start���ܳ���42�ܾͲ���ʾ 
    	if((weekCount == 42 && dateCount > 0) || weekCount > 42){
    		return "";
    	}
    	//add by huangtt 20170112 end���ܳ���42�ܾͲ���ʾ 
    	
    	if(dateCount > 0){
    		return weekCount+"��+"+dateCount+"��";
    	}else{
    		return weekCount+"��";
    	}
    }
    
    /**
     * ���ز�ѯ��Ƶ�Σ�;������λ�󲢽���Щָд��order�����е�order
     * @param order Order
     * @param sysFee TParm
     * @param ctz String[]
     * @return Order
     */
    public static Order fillOrder(Order order, TParm sysFee, String[] ctz) {
//        System.out.println("ҽ��ϸ����Ⱥ��1111111111111"+sysFee.getValue("CAT1_TYPE"));
        order.modifyOrderCode(sysFee.getValue("ORDER_CODE"));
        order.modifyOrderDesc(sysFee.getValue("ORDER_DESC").replaceFirst("(" +
            sysFee.getValue("SPECIFICATION") + ")", ""));
        order.modifyGoodsDesc(sysFee.getValue("GOODS_DESC"));
        order.modifyExpensiveFlg(sysFee.getValue("EXPENSIVE_FLG"));
        order.modifyOrderCat1Code(sysFee.getValue("ORDER_CAT1_CODE"));
        order.modifyCat1Type(sysFee.getValue("CAT1_TYPE"));
        order.modifyOwnPrice(StringTool.getDouble(sysFee.getValue("OWN_PRICE")));
        order.modifySpecification(sysFee.getValue("SPECIFICATION"));
        order.modifySetmainFlg("N");
        String cat1Code = sysFee.getValue("ORDER_CAT1_CODE");
        if (!StringUtil.isNullString(cat1Code) && cat1Code.contains("PHA")) {
            TParm parm = PhaBaseTool.getInstance().selectByOrder(order.
                getOrderCode());
            if (parm.getErrCode() < 0) {
                System.out.println("phaBase is wrong");
                return order;
            }
            order.modifyPhaType(parm.getValue("PHA_TYPE", 0));
            order.modifyFreqCode(parm.getValue("FREQ_CODE", 0));
            order.modifyRouteCode(parm.getValue("ROUTE_CODE", 0));
            order.modifyMediUnit(parm.getValue("MEDI_UNIT", 0));
            order.modifyCtrldrugclassCode(parm.getValue("CTRLDRUGCLASS_CODE", 0));
            order.modifyGiveboxFlg(parm.getValue("GIVEBOX_FLG", 0));
            if ("Y".equalsIgnoreCase(order.getGiveboxFlg())) {
                order.modifyDosageUnit(parm.getValue("STOCK_UNIT", 0));
                order.modifyDispenseUnit(order.getDosageUnit());
                order.modifyMediUnit(order.getDosageUnit());
            }
            else {
                order.modifyDosageUnit(parm.getValue("DOSAGE_UNIT", 0));
                order.modifyDispenseUnit(order.getDosageUnit());
                order.modifyMediUnit(order.getDosageUnit());
            }

            //TAKE_QTY
            String TAKE_QTY = parm.getValue("MEDI_QTY", 0);
            double takeQty = (TAKE_QTY == null || TAKE_QTY.trim().length() < 1 ||
                              "0.0".equalsIgnoreCase(TAKE_QTY)) ? 1.0 :
                Double.valueOf(TAKE_QTY);
            order.modifyMediQty(takeQty);
            String TAKE_DAY = parm.getValue("TAKE_DAYS", 0);
            int takedays = (TAKE_DAY == null || TAKE_DAY.trim().length() < 1 ||
                            "0".equalsIgnoreCase(TAKE_DAY)) ? 1 :
                Integer.valueOf(TAKE_DAY);
            order.modifyTakeDays(takedays);
            order.modifyCtrldrugclassCode(parm.getValue("CTRLDRUGCLASS_CODE", 0));
            order.modifyGiveboxFlg(parm.getValue("GIVEBOX_FLG", 0));
            order.modifyCtz1Code(ctz[0]);
            order.modifyCtz2Code(ctz[1]);
            order.modifyCtz3Code(ctz[2]);
            OdoUtil.calcuQty(order);
            order.modifyOwnAmt(StringTool.round(order.getOwnPrice() *
                                                order.getDosageQty(), 2));
            order.modifyArAmt(BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
                                               order.getOrderCode(), order
                                               .getDosageQty()) <= 0 ? 0.00 :
                              BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
                                               order.getOrderCode(), order
                                               .getDosageQty())
                );
            order.modifyRexpCode(BIL.getRexpCode(sysFee.getValue(
                "CHARGE_HOSP_CODE"), "O"));
            order.modifyHexpCode(sysFee.getValue("CHARGE_HOSP_CODE"));
        }
        else {
            order.modifyDosageQty(1.0);
            order.modifyDispenseQty(1.0);
            order.modifyMediQty(1.0);
            order.modifyFreqCode("STAT");
            order.modifyMediUnit(sysFee.getValue("UNIT_CODE"));
            order.modifyDosageUnit(sysFee.getValue("UNIT_CODE"));
            order.modifyDispenseUnit(sysFee.getValue("UNIT_CODE"));
            order.modifyTakeDays(1);
            order.modifyCtz1Code(ctz[0]);
            order.modifyCtz2Code(ctz[1]);
            order.modifyCtz3Code(ctz[2]);
            order.modifyOwnAmt(StringTool.round(order.getOwnPrice() *
                                                order.getDosageQty(), 2));
            order.modifyArAmt(BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
                                               order.getOrderCode(), order
                                               .getDosageQty()) < 0 ? 0.00 :
                              BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
                                               order.getOrderCode(), order
                                               .getDosageQty())
                );

            order.modifyRexpCode(BIL.getRexpCode(sysFee.getValue(
                "CHARGE_HOSP_CODE"), "O"));

            order.modifyHexpCode(sysFee.getValue("CHARGE_HOSP_CODE"));
        }

        return order;
    }
    /**
     * =====pangben 2013-11-26 ���flg���� �����շѽ��������ײͻش�����ʹ��
     * @param order
     * @param sysFee
     * @param ctz
     * @param Level
     * @param flg :true ��������  false:�����ײʹ���
     * @return
     */
    public static Order fillOrder(Order order, TParm sysFee, String[] ctz,
    		double dosage_qty,String dosage_unit, String Level,boolean flg) {
        order.modifyOrderCode(sysFee.getValue("ORDER_CODE"));
        order.modifyOrderDesc(sysFee.getValue("ORDER_DESC").replaceFirst("(" +
            sysFee.getValue("SPECIFICATION") + ")", ""));
        order.modifyGoodsDesc(sysFee.getValue("GOODS_DESC"));
        order.modifyExpensiveFlg(sysFee.getValue("EXPENSIVE_FLG"));
        order.modifyOrderCat1Code(sysFee.getValue("ORDER_CAT1_CODE"));
        order.modifyRxType("0");
        order.modifyCat1Type(sysFee.getValue("CAT1_TYPE"));
        if(!"PHA".equals(sysFee.getValue("CAT1_TYPE"))){
            String deptCode = Operator.getDept();
            TParm deptParm = DeptTool.getInstance().selUserDept(deptCode);
            //ִ�п���
            if (deptParm.getCount("DEPT_CODE") > 0)
                deptCode = Operator.getDept();
            else
                deptCode = "";
            order.modifyExecDeptCode(sysFee.getValue("EXEC_DEPT_CODE")==null||sysFee.getValue("EXEC_DEPT_CODE").length()==0?deptCode:sysFee.getValue("EXEC_DEPT_CODE"));
            order.modifyFreqCode("STAT");
        }
        if ("2".equals(Level)) {
            order.modifyOwnPrice(StringTool.getDouble(sysFee.getValue(
                "OWN_PRICE2")));

        }
        else if ("3".equals(Level)) {
            order.modifyOwnPrice(StringTool.getDouble(sysFee.getValue(
                "OWN_PRICE3")));
        }
        else
            order.modifyOwnPrice(StringTool.getDouble(sysFee.getValue(
                "OWN_PRICE")));
        order.modifySpecification(sysFee.getValue("SPECIFICATION"));
        order.modifySetmainFlg("N");
//        if (!StringUtil.isNullString(cat1Code) && cat1Code.contains("PHA")) {
            TParm parm = PhaBaseTool.getInstance().selectByOrder(order.
                getOrderCode());
            if (parm.getErrCode() < 0) {
                System.out.println("phaBase is wrong");
                return order;
            }
            order.modifyPhaType(parm.getValue("PHA_TYPE", 0));
            order.modifyRouteCode(parm.getValue("ROUTE_CODE", 0));
            if (parm.getValue("MEDI_UNIT", 0) == null ||
                parm.getValue("MEDI_UNIT", 0).length() == 0)
                order.modifyMediUnit(sysFee.getValue("UNIT_CODE"));
            else
                order.modifyMediUnit(parm.getValue("MEDI_UNIT", 0));


            order.modifyCtrldrugclassCode(parm.getValue("CTRLDRUGCLASS_CODE", 0));
            //========pangben 2013-11-26 �����շѽ���û�кз�ҩ��ӻش�Ĭ��
			order.modifyGiveboxFlg("N");
            if ("Y".equalsIgnoreCase(order.getGiveboxFlg())) {
                order.modifyDosageUnit(parm.getValue("DOSAGE_UNIT", 0));//===pangben 2013-8-30 �޸ĺ�װҩ��λ
                order.modifyDispenseUnit(parm.getValue("STOCK_UNIT", 0));//===pangben 2013-8-30 �޸ĺ�װҩ��λ
            }
            else {
            	if (flg) {
            		 if (parm.getValue("DOSAGE_UNIT", 0) == null ||
                             parm.getValue("DOSAGE_UNIT", 0).length() == 0)
                             order.modifyDosageUnit(sysFee.getValue("UNIT_CODE"));
                         else
                             order.modifyDosageUnit(parm.getValue("DOSAGE_UNIT", 0));
				}else{
					order.modifyDosageUnit(dosage_unit);
				}
               
                order.modifyDispenseUnit(order.getDosageUnit());
            }

            //TAKE_QTY
            String TAKE_QTY = parm.getValue("MEDI_QTY", 0);
            double takeQty = (TAKE_QTY == null || TAKE_QTY.trim().length() < 1 ||
                              "0.0".equalsIgnoreCase(TAKE_QTY)) ? 1.0 :
                Double.valueOf(TAKE_QTY);
            order.modifyMediQty(takeQty);
            String TAKE_DAY = parm.getValue("TAKE_DAYS", 0);
            int takedays = (TAKE_DAY == null || TAKE_DAY.trim().length() < 1 ||
                            "0".equalsIgnoreCase(TAKE_DAY)) ? 1 :
                Integer.valueOf(TAKE_DAY);
            order.modifyTakeDays(takedays);
            order.modifyCtrldrugclassCode(parm.getValue("CTRLDRUGCLASS_CODE", 0));
            //order.modifyGiveboxFlg(parm.getValue("GIVEBOX_FLG", 0));
            order.modifyCtz1Code(ctz[0]);
            order.modifyCtz2Code(ctz[1]);
            order.modifyCtz3Code(ctz[2]);
            OdoUtil.calcuQty(order);
            order.modifyOwnAmt(StringTool.round(order.getOwnPrice() *
                                                order.getDosageQty(), 2));
            double ctzRate=BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
                    order.getOrderCode(), order.getDosageQty(), Level);
            order.modifyArAmt(ctzRate<= 0 ?0.00 : ctzRate);
            //=====pangben 2013-8-30 �޸��ۿ������
            double d[] = BILStrike.getInstance().chargeC(ctz[0],ctz[1], ctz[2],order.getOrderCode(),
            		sysFee.getValue("CHARGE_HOSP_CODE"),Level);
            order.modifyDiscountRate(d[1]);
            order.modifyRexpCode(BIL.getRexpCode(sysFee.getValue(
                "CHARGE_HOSP_CODE"), "O"));
            order.modifyHexpCode(sysFee.getValue("CHARGE_HOSP_CODE"));
//        }
//        else {
//            order.modifyDosageQty(1.0);
//            order.modifyDispenseQty(1.0);
//            order.modifyMediQty(1.0);
//            order.modifyFreqCode("STAT");
//            order.modifyMediUnit(sysFee.getValue("UNIT_CODE"));
//            order.modifyDosageUnit(sysFee.getValue("UNIT_CODE"));
//            order.modifyDispenseUnit(sysFee.getValue("UNIT_CODE"));
//            order.modifyTakeDays(1);
//            order.modifyCtz1Code(ctz[0]);
//            order.modifyCtz2Code(ctz[1]);
//            order.modifyCtz3Code(ctz[2]);
//            order.modifyOwnAmt(StringTool.round(order.getOwnPrice() *
//                                                order.getDosageQty(), 2));
//            order.modifyArAmt(BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
//                                               order.getOrderCode(), order
//                                               .getDosageQty(), Level) <= 0 ?
//                              0.00 : BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
//                order.getOrderCode(), order
//                .getDosageQty(), Level)
//                );
//
//            order.modifyRexpCode(BIL.getRexpCode(sysFee.getValue(
//                "CHARGE_HOSP_CODE"), "O"));
//
//            order.modifyHexpCode(sysFee.getValue("CHARGE_HOSP_CODE"));
//        }
        return order;
    }

    /**
     * ����ҽ��������ҽ�������Ƶ�Σ�ִ�п��ң��շݣ�Ƶ����ͬ
     * @param o Order
     * @param orderMain Order
     * @param linkno int
     * @return Order
     */
    public static Order linkOrder(Order o, Order orderMain, int linkno) {
        o.modifyRouteCode(orderMain.getRouteCode());
        o.modifyExecDeptCode(orderMain.getExecDeptCode());
        o.modifyTakeDays(orderMain.getTakeDays());
        o.modifyFreqCode(orderMain.getFreqCode());
        o.modifyLinkNo(TCM_Transform.getString(linkno));
        return o;
    }

    /**
     *
     * @param o ����Order
     * @return �������������ݵ�Order
     */
    public static Order calcuQty(Order o) {
        TotQtyTool t = new TotQtyTool();
        TParm orderParm = o.getParm();
        String orderCode = orderParm.getValue("ORDER_CODE");
        TParm sysParm = SYSFeeTool.getInstance().getCat1Code(orderCode);
        orderParm.setData("CAT1_TYPE", sysParm.getData("CAT1_TYPE", 0));
//        System.out.println("������������"+o.getParm());
        TParm qty = t.getTotQty(orderParm);
//        System.out.println("������������"+qty);
        //System.out.println("------>if " + "Y".equalsIgnoreCase(o.getGiveboxFlg()));
        if ("Y".equalsIgnoreCase(o.getGiveboxFlg())) {
            o.modifyDispenseQty(StringTool.getDouble(TCM_Transform.getString(
                qty.getData("QTY_FOR_STOCK_UNIT"))));
            o.modifyDosageQty(StringTool.getDouble(TCM_Transform.getString(qty.
                getData("TOT_QTY"))));
            o.modifyDispenseUnit(TCM_Transform.getString(qty.getData(
                "STOCK_UNIT")));
        }
        else {
            o.modifyDosageQty(StringTool.getDouble(TCM_Transform.getString(qty.
                getData("QTY"))));
            o.modifyDispenseUnit(TCM_Transform.getString(qty.getData(
                "DOSAGE_UNIT")));
            o.modifyDispenseQty(o.getDosageQty());
        }
//        System.out.println("����"+o.getDosageQty());
//        System.out.println("�Է��ܼ�"+StringTool.round(o.getOwnPrice() * o.getDosageQty(), 2));
//        System.out.println("�ܼ�"+BIL.chargeTotCTZ(o.getCtz1Code(), o.getCtz2Code(),
//                                       o.getCtz3Code(), o.getOrderCode(), o
//                                       .getDosageQty()));
        o.modifyOwnAmt(StringTool.round(o.getOwnPrice() * o.getDosageQty(), 2));
        o.modifyArAmt(BIL.chargeTotCTZ(o.getCtz1Code(), o.getCtz2Code(),
                                       o.getCtz3Code(), o.getOrderCode(), o
                                       .getDosageQty()));
//        System.out.println("�����ܼ�"+o.getArAmt());
        return o;
    }

    public static Order calcuQty(Order o, String level) {
        TotQtyTool t = new TotQtyTool();
        TParm parm = PhaBaseTool.getInstance().selectByOrder(o.
            getOrderCode());
        if (parm.getErrCode() < 0) {
            System.out.println("phaBase is wrong");
            return o;
        }
        TParm orderParm = o.getParm();
        String orderCode = orderParm.getValue("ORDER_CODE");
        TParm sysParm = SYSFeeTool.getInstance().getCat1Code(orderCode);
        orderParm.setData("CAT1_TYPE", sysParm.getData("CAT1_TYPE", 0));
        orderParm.setData("PHA_TYPE", parm.getData("PHA_TYPE", 0));
        TParm qty = t.getTotQty(orderParm);

        if ("Y".equalsIgnoreCase(o.getGiveboxFlg())) {
            o.modifyDispenseQty(StringTool.getDouble(TCM_Transform.getString(
                qty.getData("QTY_FOR_STOCK_UNIT"))));
            o.modifyDosageQty(StringTool.getDouble(TCM_Transform.getString(qty.
                getData("TOT_QTY"))));
            o.modifyDispenseUnit(TCM_Transform.getString(qty.getData(
                "STOCK_UNIT")));
        }
        else {
            o.modifyDosageQty(StringTool.getDouble(TCM_Transform.getString(qty.
                getData("QTY"))));
            o.modifyDispenseUnit(TCM_Transform.getString(qty.getData(
                "DOSAGE_UNIT")));
            o.modifyDispenseQty(o.getDosageQty());
        }
        o.modifyOwnAmt(StringTool.round(o.getOwnPrice() * o.getDosageQty(), 2));
        o.modifyArAmt(BIL.chargeTotCTZ(o.getCtz1Code(), o.getCtz2Code(),
                                       o.getCtz3Code(), o.getOrderCode(), o
                                       .getDosageQty(), level));
        return o;
    }

    /**
     * �����޸�
     * @param o Order
     * @param level String
     * @return Order
     */
    public static Order calcuQtyAll(Order o, String level) {
        o.modifyOwnAmt(StringTool.round(o.getOwnPrice(), 2));
        o.modifyArAmt(BIL.chargeTotCTZ(o.getCtz1Code(), o.getCtz2Code(),
                                       o.getCtz3Code(), o.getOrderCode(), o
                                       .getDosageQty(), level) );
        return o;
    }

    /**
     *
     * @param o ����Order
     * @return �������������ݵ�Order
     */
    public static Order calcuMediQty(Order o) {
        TotQtyTool t = new TotQtyTool();
        TParm qty = t.getTakeQty(o.getParm());
        o.modifyMediQty(TCM_Transform.getDouble(qty.getData("QTY")));
        o.modifyDosageQty(TCM_Transform.getDouble(qty.getData("QTY")));
        o.modifyDispenseQty(TCM_Transform.getDouble(qty.getData("QTY")));
        return o;
    }

    /**
     * ���ݴ���Ĵ���ǩ���Ϻ����������ش����е���󼯺�ҽ����ţ����ޣ��򷵻��ޣ�Ҳ֤�����Ŵ���ǩ��û�м���ҽ��
     * @param ol OrderList
     * @return int �˼���ҽ���е����
     */
    public static int getOrderSetGroupNo(OrderList ol) {
        int orderSet = 0;
        if (ol == null || ol.size() < 1)
            return 0;
        Order o = new Order();
        for (int i = 0; i < ol.size(ol.PRIMARY); i++) {
            o = ol.getOrder(i);
            if (o == null)
                return 0;
            if (!"Y".equalsIgnoreCase(o.getSetmainFlg()))
                continue;
            if (o.getOrderSetGroupNo() > orderSet)
                orderSet = o.getOrderSetGroupNo();
        }
        return orderSet;
    }

    /**
     * ��ʼ������ҽ����������
     * @param reg Reg
     * @param parm TParm
     * @param o Order
     * @param isChild boolean
     * @return Order
     */
    public static Order initExaOrder(Reg reg, TParm parm, Order o,
                                     boolean isChild) {
        o.modifyOrderCode(parm.getValue("ORDER_CODE"));
        o.modifyOrderDesc(parm.getValue("ORDER_DESC"));
        o.modifyGoodsDesc(parm.getValue("GOODS_DESC"));
        o.modifyOptitemCode(parm.getValue("OPTITEM_CODE"));
        o.modifyDispenseUnit(parm.getValue("UNIT_CODE"));
        o.modifyDosageUnit(parm.getValue("UNIT_CODE"));
        o.modifyMediUnit(parm.getValue("UNIT_CODE"));
        o.modifyTakeDays(1);
        o.modifyDosageQty(1.0);
        o.modifyDispenseQty(1.0);
        o.modifyMediQty(1.0);
        o.modifyOwnPrice(StringTool.getDouble(parm.getValue("OWN_PRICE")));
        o.modifyOwnAmt(StringTool.round(o.getOwnPrice() * o.getDosageQty(), 2));
        o.modifyArAmt(BIL.chargeTotCTZ(reg.getCtz1Code(), reg.getCtz2Code(),
                                       reg.getCtz3Code(), o.getOrderCode(),
                                       o.getDosageQty()) <= 0 ? 0.00 :
                      BIL.chargeTotCTZ(reg.getCtz1Code(), reg.getCtz2Code(),
                                       reg.getCtz3Code(), o.getOrderCode(),
                                       o.getDosageQty())
            );
        if(!"PHA".equals(parm.getValue("CAT1_TYPE"))){
            String deptCode = Operator.getDept();
            TParm deptParm = DeptTool.getInstance().selUserDept(deptCode);
            //ִ�п���
            if (deptParm.getCount("DEPT_CODE") > 0)
                deptCode = Operator.getDept();
            else
                deptCode = "";
            o.modifyExecDeptCode(parm.getValue("EXEC_DEPT_CODE")==null||parm.getValue("EXEC_DEPT_CODE").length()==0?deptCode:parm.getValue("EXEC_DEPT_CODE"));
            o.modifyFreqCode("STAT");
        }

        o.modifyInspayType(parm.getValue("INSPAY_TYPE"));
        o.modifyOrderCat1Code(parm.getValue("ORDER_CAT1_CODE"));
        o.modifyRpttypeCode(parm.getValue("RPTTYPE_CODE"));
        o.modifyDegreeCode(parm.getValue("DEGREE_CODE"));
        if (isChild) {
            o.modifyPrinttypeflgInfant("Y");
        }
        else {
            o.modifyPrinttypeflgInfant("N");
        }
        o.modifyDevCode(parm.getValue("CHARGE_HOSP_CODE"));
        o.modifyCtz1Code(reg.getCtz1Code());
        o.modifyCtz2Code(reg.getCtz2Code());
        o.modifyCtz3Code(reg.getCtz3Code());
        double d[] = BILStrike.getInstance().chargeC(reg.getCtz1Code(), reg.getCtz2Code(), reg.getCtz3Code(),
                                                     parm.getValue("ORDER_CODE"),
                                                     parm.getValue("CHARGE_HOSP_CODE"),
                                                     reg.getServiceLevel());
        o.modifyDiscountRate(d[1]);
        o.modifyRexpCode(BIL.getRexpCode(parm.getValue("CHARGE_HOSP_CODE"), "O"));
        o.modifyHexpCode(parm.getValue("CHARGE_HOSP_CODE"));
        o.modifyBillFlg("N");
        return o;
    }

    public static Order initExaOrder(Reg reg, TParm parm, Order o,
                                     boolean isChild, String level) {
        o.modifyOrderCode(parm.getValue("ORDER_CODE"));
        o.modifyRxType("0");
        o.modifyFreqCode("STAT");
        o.modifyOrderDesc(parm.getValue("ORDER_DESC"));
        o.modifyGoodsDesc(parm.getValue("GOODS_DESC"));
        o.modifyOptitemCode(parm.getValue("OPTITEM_CODE"));
        o.modifyDispenseUnit(parm.getValue("UNIT_CODE"));
        o.modifyDosageUnit(parm.getValue("UNIT_CODE"));
        o.modifyMediUnit(parm.getValue("UNIT_CODE"));
        o.modifyCat1Type(parm.getValue("CAT1_TYPE"));
        if(!"PHA".equals(parm.getValue("CAT1_TYPE"))){
            String deptCode = Operator.getDept();
            TParm deptParm = DeptTool.getInstance().selUserDept(deptCode);
            //ִ�п���
            if (deptParm.getCount("DEPT_CODE") > 0)
                deptCode = Operator.getDept();
            else
                deptCode = "";
            o.modifyExecDeptCode(parm.getValue("EXEC_DEPT_CODE")==null||parm.getValue("EXEC_DEPT_CODE").length()==0?deptCode:parm.getValue("EXEC_DEPT_CODE"));
            o.modifyFreqCode("STAT");
        }
        o.modifyTakeDays(1);
        o.modifyDosageQty(1.0);
        o.modifyDispenseQty(1.0);
        o.modifyMediQty(1.0);
        TParm feeParm =new TParm();
        feeParm.setData("ORDERSET_CODE", parm.getValue("ORDER_CODE"));
        o.modifyOwnPrice(0.00);//====pangben 2013-8-30����ҽ������û�н��
        o.modifyOwnAmt(StringTool.round(o.getOwnPrice() * o.getDosageQty(), 2));
        double ctzRate=BIL.chargeTotCTZ(reg.getCtz1Code(), reg.getCtz2Code(),
                reg.getCtz3Code(), o.getOrderCode(),
                o.getDosageQty());
        o.modifyArAmt(ctzRate<= 0 ? 0.00 :ctzRate);
        o.modifyInspayType(parm.getValue("INSPAY_TYPE"));
        o.modifyOrderCat1Code(parm.getValue("ORDER_CAT1_CODE"));
        o.modifyRpttypeCode(parm.getValue("RPTTYPE_CODE"));
        o.modifyDegreeCode(parm.getValue("DEGREE_CODE"));
        if (isChild) {
            o.modifyPrinttypeflgInfant("Y");
        }
        else {
            o.modifyPrinttypeflgInfant("N");
        }
        o.modifyDevCode(parm.getValue("CHARGE_HOSP_CODE"));
        o.modifyCtz1Code(reg.getCtz1Code());
        o.modifyCtz2Code(reg.getCtz2Code());
        o.modifyCtz3Code(reg.getCtz3Code());
        double d[] = BILStrike.getInstance().chargeC(reg.getCtz1Code(), reg.getCtz2Code(), reg.getCtz3Code(),
                                                     parm.getValue("ORDER_CODE"),
                                                     parm.getValue("CHARGE_HOSP_CODE"),
                                                     reg.getServiceLevel());
        o.modifyDiscountRate(d[1]);
        o.modifyRexpCode(BIL.getRexpCode(parm.getValue("CHARGE_HOSP_CODE"), "O"));
        o.modifyHexpCode(parm.getValue("CHARGE_HOSP_CODE"));
        o.modifyBillFlg("N");
        return o;
    }

    public static Order initOpOrder(ODO odo, TParm parm, Order o) {
        o.modifyOrderCode(parm.getValue("ORDER_CODE"));
        o.modifyOrderDesc(parm.getValue("ORDER_DESC"));
        o.modifyGoodsDesc(parm.getValue("GOODS_DESC"));
        o.modifyDosageQty(1.0);
        o.modifyFreqCode("STAT");
        o.modifyTakeDays(1);

        o.modifyOptitemCode(parm.getValue("OPTITEM_CODE"));
        o.modifyDispenseUnit(parm.getValue("UNIT_CODE"));
        o.modifyDosageUnit(parm.getValue("UNIT_CODE"));
        String rbdeptCode = parm.getValue("EXEC_DEPT_CODE");
        if (!StringUtil.isNullString(rbdeptCode)) {
            o.modifyExecDeptCode(rbdeptCode);
        }

        o.modifyInspayType(parm.getValue("INSPAY_TYPE"));
        o.modifyOrderCat1Code(parm.getValue("ORDER_CAT1_CODE"));
        o.modifyRpttypeCode(parm.getValue("RPTTYPE_CODE"));
        o.modifyDegreeCode(parm.getValue("DEGREE_CODE"));
        if (odo.isChild()) {
            o.modifyPrinttypeflgInfant("Y");
        }
        else {
            o.modifyPrinttypeflgInfant("N");
        }
        o.modifyDevCode(parm.getValue("CHARGE_HOSP_CODE"));
        o.modifyCtz1Code(odo.getReg().getCtz1Code());
        o.modifyCtz2Code(odo.getReg().getCtz2Code());
        o.modifyCtz3Code(odo.getReg().getCtz3Code());
        o.modifyDiscountRate(BIL.getOwnRate(odo.getReg().getCtz1Code(),
                                            odo.getReg().getCtz2Code(),
                                            odo.getReg().getCtz3Code(),
                                            parm.getValue("CHARGE_HOSP_CODE")));
        o.modifyRexpCode(BIL.getRexpCode(parm.getValue("CHARGE_HOSP_CODE"), "O"));
        o.modifyHexpCode(parm.getValue("CHARGE_HOSP_CODE"));
        o.modifyBillFlg("N");
//	        OdoUtil.fillOrder(o);
        return o;
    }

    /**
     * ��Ӽ���ҽ��
     * @param reg Reg
     * @param ol OrderList
     * @param index int
     * @param isChild boolean
     * @return OrderList
     */
    public static OrderList addOrder(Reg reg, OrderList ol, int index,
                                     boolean isChild) {
        double own_price = 0.0;
        double own_amt = 0.0;
        double ar_amt = 0.0;
        if (reg == null || ol == null || ol.size() < 1)
            return null;
        int groupNo = OdoUtil.getOrderSetGroupNo(ol) + 1;
        Order o = ol.getOrder(index);
        if (o == null || StringUtil.isNullString(o.getOrderCode()))
            return null;
        o.modifyOrdersetCode(o.getOrderCode());
        o.modifyOrderSetGroupNo(groupNo);
        o.modifySetmainFlg("Y");
        TParm parm = SYSOrderSetDetailTool.getInstance().selectByOrderSetCode(
            ol.getOrder(index).getOrderCode());
        if (parm.getErrCode() != 0) {
            System.out.println("get order set wrong");
            return null;
        }
        Order newo;
        for (int i = 0; i < parm.getCount(); i++) {
            newo = ol.newOrder();
            newo.modifyOrderCode(parm.getValue("ORDER_CODE", i));
            newo.modifyOrderDesc(parm.getValue("ORDER_DESC", i));
            newo.modifyGoodsDesc(parm.getValue("GOODS_DESC", i));
            newo.modifyOptitemCode(parm.getValue("OPTITEM_CODE", i));
            newo.modifyDispenseUnit(parm.getValue("UNIT_CODE", i));
            newo.modifyDosageUnit(parm.getValue("UNIT_CODE", i));
            newo.modifyMediUnit(parm.getValue("UNIT_CODE", i));
            newo.modifyDosageQty(StringTool.getDouble(parm.getValue("TOTQTY", i)));
            newo.modifyMediQty(StringTool.getDouble(parm.getValue("TOTQTY", i)));
            newo.modifyDispenseQty(StringTool.getDouble(parm.getValue("TOTQTY",
                i)));
            newo.modifyOwnPrice(StringTool.getDouble(parm.getValue("OWN_PRICE",
                i)));
            newo.modifyCaseNo(reg.caseNo());
            newo.modifyFreqCode(o.getFreqCode());
            String rbdeptCode = parm.getValue("EXEC_DEPT_CODE", i);
            if (!StringUtil.isNullString(rbdeptCode)) {
                o.modifyExecDeptCode(rbdeptCode);
            }

            newo.modifyInspayType(parm.getValue("INSPAY_TYPE", i));
            newo.modifyOrderCat1Code(parm.getValue("ORDER_CAT1_CODE", i));
            newo.modifyRpttypeCode(parm.getValue("RPTTYPE_CODE", i));
            newo.modifyDegreeCode(parm.getValue("DEGREE_CODE", i));
            if (isChild) {
                newo.modifyPrinttypeflgInfant("Y");
            }
            else {
                newo.modifyPrinttypeflgInfant("N");
            }
            newo.modifyDevCode(parm.getValue("CHARGE_HOSP_CODE", i));
            newo.modifySetmainFlg("N");
            newo.modifyCtz1Code(reg.getCtz1Code());
            newo.modifyCtz2Code(reg.getCtz2Code());
            newo.modifyCtz3Code(reg.getCtz3Code());
            double d[] = BILStrike.getInstance().chargeC(reg.getCtz1Code(), reg.getCtz2Code(), reg.getCtz3Code(),
                                                         parm.getValue("ORDER_CODE"),
                                                         parm.getValue("CHARGE_HOSP_CODE"),
                                                         reg.getServiceLevel());
            newo.modifyDiscountRate(d[1]);
            newo.modifyRexpCode(BIL.getRexpCode(parm.getValue(
                "CHARGE_HOSP_CODE", i), "O"));
            newo.modifyHexpCode(parm.getValue("CHARGE_HOSP_CODE", i));
            newo.modifyBillFlg("N");
            newo.modifyHideFlg(parm.getValue("HIDE_FLG", i));
            newo.modifyOrdersetCode(o.getOrdersetCode());
            newo.modifyOrderSetGroupNo(groupNo);
            double eachown = StringTool.getDouble(parm.getValue("TOTQTY", i)) *
                StringTool.getDouble(parm.getValue("OWN_PRICE", i));
            double eachtot = BIL.chargeTotCTZ(reg.
                                              getCtz1Code(), reg.getCtz2Code(),
                                              reg.getCtz3Code(),
                                              newo.getOrderCode(),
                                              StringTool.getDouble(parm.
                getValue("TOTQTY", i))) <= 0 ? 0.00 : BIL.chargeTotCTZ(reg.
                getCtz1Code(), reg.getCtz2Code(),
                reg.getCtz3Code(),
                newo.getOrderCode(),
                StringTool.getDouble(parm.getValue("TOTQTY", i)));
            newo.modifyOwnAmt(eachown);
            newo.modifyArAmt(eachtot);
            own_price += eachown;
            own_amt += eachown;
            ar_amt += eachtot;
        }
        return ol;
    }

    public static OrderList addOrder(Reg reg, OrderList ol, int index,
                                     boolean isChild, String level) {
        double own_price = 0.0;
        double own_amt = 0.0;
        double ar_amt = 0.0;
        if (reg == null || ol == null || ol.size() < 1)
            return null;
        int groupNo = OdoUtil.getOrderSetGroupNo(ol) + 1;
        Order o = ol.getOrder(index);
        if (o == null || StringUtil.isNullString(o.getOrderCode()))
            return null;
        o.modifyOrdersetCode(o.getOrderCode());
        o.modifyOrderSetGroupNo(groupNo);
        o.modifySetmainFlg("Y");
        //zhangp 20140109
        o.modifyExecFlg("Y");
        TParm parm = SYSOrderSetDetailTool.getInstance().selectByOrderSetCode(
            ol.getOrder(index).getOrderCode());
//        System.out.println("ҽ����Ϣ"+parm);
        if (parm.getErrCode() != 0) {
            System.out.println("get order set wrong");
            return null;
        }
        Order newo;
        for (int i = 0; i < parm.getCount(); i++) {
            TParm sysParm = SYSFeeTool.getInstance().getFeeAllData(parm.getValue("ORDER_CODE", i));
            newo = ol.newOrder();
            newo.modifyOrderCode(parm.getValue("ORDER_CODE", i));
            newo.modifyOrderDesc(parm.getValue("ORDER_DESC", i));
            newo.modifyGoodsDesc(parm.getValue("GOODS_DESC", i));
            newo.modifyOptitemCode(parm.getValue("OPTITEM_CODE", i));
            newo.modifyRxType("0");
            newo.modifyDispenseUnit(parm.getValue("UNIT_CODE", i));
            newo.modifyDosageUnit(parm.getValue("UNIT_CODE", i));
            newo.modifyMediUnit(parm.getValue("UNIT_CODE", i));
            newo.modifyDosageQty(StringTool.getDouble(parm.getValue("TOTQTY", i)));
            newo.modifyMediQty(StringTool.getDouble(parm.getValue("TOTQTY", i)));
            newo.modifyDispenseQty(StringTool.getDouble(parm.getValue("TOTQTY",
                i)));
            if ("2".equals(level)) {
                newo.modifyOwnPrice(StringTool.getDouble(parm.getValue(
                    "OWN_PRICE3", i)));
            }
            else if ("3".equals(level)) {
                newo.modifyOwnPrice(StringTool.getDouble(parm.getValue(
                    "OWN_PRICE3", i)));
            }
            else
                newo.modifyOwnPrice(StringTool.getDouble(parm.getValue(
                    "OWN_PRICE", i)));
            newo.modifyCaseNo(reg.caseNo());
            newo.modifyFreqCode(o.getFreqCode());
            if(!"PHA".equals(parm.getValue("CAT1_TYPE",i))){
                String deptCode = Operator.getDept();
                TParm deptParm = DeptTool.getInstance().selUserDept(deptCode);
                //ִ�п���
                if (deptParm.getCount("DEPT_CODE") > 0)
                    deptCode = Operator.getDept();
                else
                    deptCode = "";
                newo.modifyExecDeptCode(sysParm.getValue("EXEC_DEPT_CODE",0)==null||sysParm.getValue("EXEC_DEPT_CODE",0).length()==0?deptCode:sysParm.getValue("EXEC_DEPT_CODE",0));
                newo.modifyFreqCode("STAT");
        }
            newo.modifyInspayType(parm.getValue("INSPAY_TYPE", i));
            newo.modifyOrderCat1Code(parm.getValue("ORDER_CAT1_CODE", i));
//            System.out.println("ҽ��ϸ����Ⱥ��222222222"+parm.getValue("CAT1_TYPE"));
            newo.modifyCat1Type(parm.getValue("CAT1_TYPE", i));
            newo.modifyRpttypeCode(parm.getValue("RPTTYPE_CODE", i));
            newo.modifyDegreeCode(parm.getValue("DEGREE_CODE", i));
            if (isChild) {
                newo.modifyPrinttypeflgInfant("Y");
            }
            else {
                newo.modifyPrinttypeflgInfant("N");
            }
            newo.modifyDevCode(parm.getValue("CHARGE_HOSP_CODE", i));
            newo.modifySetmainFlg("N");
            newo.modifyCtz1Code(reg.getCtz1Code());
            newo.modifyCtz2Code(reg.getCtz2Code());
            newo.modifyCtz3Code(reg.getCtz3Code());
            double d[] = BILStrike.getInstance().chargeC(reg.getCtz1Code(), reg.getCtz2Code(), reg.getCtz3Code(),
                                                         parm.getValue("ORDER_CODE"),
                                                         parm.getValue("CHARGE_HOSP_CODE"),
                                                         reg.getServiceLevel());
            newo.modifyDiscountRate(d[1]);
            newo.modifyRexpCode(BIL.getRexpCode(parm.getValue(
                "CHARGE_HOSP_CODE", i), "O"));
            newo.modifyHexpCode(parm.getValue("CHARGE_HOSP_CODE", i));
            newo.modifyBillFlg("N");
            newo.modifyHideFlg(parm.getValue("HIDE_FLG", i));
            newo.modifyOrdersetCode(o.getOrdersetCode());
            newo.modifyOrderSetGroupNo(groupNo);
            //zhangp 20140109
            newo.modifyExecFlg("Y");
            newo.modifyExecDeptCode(sysParm.getValue("EXEC_DEPT_CODE",0) == null||sysParm.getValue("EXEC_DEPT_CODE",0).length()==0 ?
                                    Operator.getDept() :
                                    sysParm.getValue("EXEC_DEPT_CODE",0));
            double ownPrice = 0.00;
            if ("2".equals(level)) {
                ownPrice = StringTool.getDouble(parm.getValue("OWN_PRICE2", i));
            }
            else if ("3".equals(level)) {
                ownPrice = StringTool.getDouble(parm.getValue("OWN_PRICE3", i));
            }
            else
                ownPrice = StringTool.getDouble(parm.getValue("OWN_PRICE", i));
            double eachown = StringTool.getDouble(parm.getValue("TOTQTY", i)) *
                ownPrice;
            double eachtot = BIL.chargeTotCTZ(reg.
                                              getCtz1Code(), reg.getCtz2Code(),
                                              reg.getCtz3Code(),
                                              newo.getOrderCode(),
                                              StringTool.getDouble(parm.
                getValue("TOTQTY", i)), level);
//            System.out.println("OdoUtilҽ������" + eachtot);
            newo.modifyOwnAmt(eachown);
            newo.modifyArAmt(eachtot);
            own_price += eachown;
            own_amt += eachown;
            ar_amt += eachtot;
        }
        return ol;
    }

    /**
     * ��ϸ����ѹ��������
     * @param ol ����OrderList
     * @return TParm ѹ��ϸ�����OrderList ��TParm
     */
    public static TParm showOrderSet(OrderList ol) {
        if (ol == null || ol.size() < 1)
            return null;
        TParm parm;
        int ordergroupno;
        String ordersetCode = "";
        double own_price = 0.0;
        double own_amt = 0.0;
        double ar_amt = 0.0;
        //ORDER_CODE;OPTITEM_CODE;MEDI_QTY;
        //DOSAGE_UNIT;OWN_PRICE;AR_AMT;PAYAMOUNT;OWN_AMT;
        //DR_NOTE;NS_NOTE;EXEC_DEPT_CODE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;REGISTER_DATE;PHA_CHECK_DATE
        for (int i = ol.size() - 1; i >= 0; i--) {
            try {
                Order o = ol.getOrder(i);
                if (o == null)
                    return null;
                own_amt += o.getOwnAmt();
                ar_amt += o.getArAmt();
                if ("Y".equalsIgnoreCase(o.getSetmainFlg()) &&
                    !ordersetCode.equalsIgnoreCase(o.getOrdersetCode())) {
                    ordersetCode = o.getOrdersetCode();
                    ordergroupno = o.getOrderSetGroupNo();
                    o.modifyOwnAmt(own_amt);
                    o.modifyArAmt(ar_amt);
                    o.modifyOwnPrice(own_amt);
                    continue;
                }
            }
            catch (Exception e) {
//				  System.out.println("i============="+i);
            }

        }
        parm = ol.getParm(ol.PRIMARY);
        for (int i = parm.getCount() - 1; i >= 0; i--) {
            if (parm.getValue("SETMAIN_FLG", i).equalsIgnoreCase("N") ||
                "".equalsIgnoreCase(parm.getValue("SETMAIN_FLG", i))) {
                parm.removeRow(i);
            }
        }
        return parm;
    }

    /**
     * ɾ������ҽ��
     * @param ol OrderList
     * @param o Order
     * @return OrderList
     */
    public static OrderList deleteOrderSet(OrderList ol, Order o) {
        if (ol == null || ol.size() < 1)
            return ol;
        if (!"Y".equalsIgnoreCase(o.getSetmainFlg())) {
            return ol;
        }
        int ordersetGroupNo = -1;
        if (o != null) {
            ordersetGroupNo = o.getOrderSetGroupNo();
        }
        Order temp;
        for (int i = ol.size() - 1; i > -1; i--) {
            temp = ol.getOrder(i);
            if (temp.getOrderSetGroupNo() == ordersetGroupNo) {
                ol.removeData(i);
            }
        }
        return ol;
    }

    /**
     * ��ʼ��һ��ҽ��
     * @param table TTable
     * @param order OpdOrder
     * @param row int
     * @param parm TParm
     * @param parmBase TParm
     * @param ctz String[]
     */
    public static void initOrder(TTable table, OpdOrder order, int row,
                                 TParm parm,
                                 TParm parmBase, String[] ctz) {
        order.itemNow = true;
        String rxNo = order.getItemString(row, "RX_NO");
        String rxType = order.getItemString(row, "RX_TYPE");
        order.setItem(row, "PRESRT_NO", row + 1);
        order.setItem(row, "REGION_CODE", Operator.getRegion());
        order.setItem(row, "RELEASE_FLG", "N");
        order.setItem(row, "LINKMAIN_FLG", "N");
        order.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
        order.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
        order.setItem(row, "GOODS_DESC", parm.getValue("ORDER_DESC")
                      .replaceFirst("(" + parm.getValue("SPECIFICATION") + ")",
                                    ""));
        order.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION"));
        order.setItem(row, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE"));
        order.setItem(row, "OWN_PRICE", parm.getValue("OWN_PRICE"));
        order.setItem(row, "CHARGE_HOSP_CODE", parm
                      .getValue("CHARGE_HOSP_CODE"));
        order.setItem(row, "REXP_CODE", BIL.getRexpCode(parm
            .getValue("CHARGE_HOSP_CODE"), "O"));
        order.setItem(row, "SETMAIN_FLG", "N");
        order.setItem(row, "ORDERSET_GROUP_NO", 0);
        order.setItem(row, "CTZ1_CODE", ctz[0]);
        order.setItem(row, "CTZ2_CODE", ctz[1]);
        order.setItem(row, "CTZ3_CODE", ctz[2]);
        double TAKE_QTY = parm.getDouble("MEDI_QTY", 0);
        order.setItem(row, "MEDI_QTY", 1.0);
        order.setItem(row, "DISPENSE_QTY", 1.0);
        order.setItem(row, "DOSAGE_QTY", 1.0);
        order.setItem(row, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
        order.setItem(row, "DISPENSE_UNIT", parm.getValue("UNIT_CODE"));
        order.setItem(row, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
        if (parmBase != null) {
            double takeQty = (TAKE_QTY < 1.0) ? 1.0 : TAKE_QTY;
            order.setItem(row, "MEDI_QTY", parmBase.getDouble("MEDI_QTY", 1));

            order.setItem(row, "FREQ_CODE", parmBase.getValue("FREQ_CODE", 0));
            order
                .setItem(row, "ROUTE_CODE", parmBase.getValue("ROUTE_CODE",
                0));
            String TAKE_DAY = parmBase.getValue("TAKE_DAYS", 0);
            int takedays = (TAKE_DAY == null || TAKE_DAY.trim().length() < 1 ||
                            "0"
                            .equalsIgnoreCase(TAKE_DAY)) ? 1 : Integer
                .valueOf(TAKE_DAY);
            order.setItem(row, "TAKE_DAYS", takedays);
            order.setItem(row, "CTRLDRUGCLASS_CODE", parmBase.getValue(
                "CTRLDRUGCLASS_CODE", 0));
            order.setItem(row, "GIVEBOX_FLG", parmBase.getValue("GIVEBOX_FLG",
                0));
            if ("Y".equalsIgnoreCase(parmBase.getValue("GIVEBOX_FLG", 0))) {
                order.setItem(row, "DOSAGE_UNIT", parmBase.getValue(
                    "STOCK_UNIT", 0));
                order.setItem(row, "DISPENSE_UNIT", parmBase.getValue(
                    "STOCK_UNIT", 0));
                order.setItem(row, "MEDI_UNIT", parmBase.getValue("STOCK_UNIT",
                    0));
            }
            else {
                order.setItem(row, "DOSAGE_UNIT", parmBase.getValue(
                    "DOSAGE_UNIT", 0));
                order.setItem(row, "DISPENSE_UNIT", parmBase.getValue(
                    "DOSAGE_UNIT", 0));
                order.setItem(row, "MEDI_UNIT", parmBase.getValue(
                    "MEDI_UNIT", 0));
            }
        }

        double ownAmt = StringTool.round(order.getItemDouble(row, "OWN_PRICE")
                                         *
                                         order.getItemDouble(row, "DOSAGE_QTY"),
                                         2);
        double arAmt = BIL
            .chargeTotCTZ(ctz[0], ctz[1], ctz[2], parm
                          .getValue("ORDER_CODE"), order.getItemDouble(row,
            "DOSAGE_QTY"));
        order.setItem(row, "OWN_AMT", ownAmt);
        order.setItem(row, "AR_AMT", arAmt);
        order.setItem(row, "PAYAMOUNT", ownAmt - arAmt);
        order.itemNow = false;
    }

    /**
     * ����icd�еĿ�ʼ���䣬�������䣬���ƿ��ң������Ա𡢴�������������жϴ���ҽ���Ƿ��ܿ���
     * @param icd TParm
     * @param dept String
     * @param sex String
     * @param birthday Timestamp
     * @param admDate Timestamp
     * @return boolean
     */
    public static boolean isAllowDiag(TParm icd, String dept, String sex,
                                      Timestamp birthday, Timestamp admDate) {
        String limitSex = icd.getValue("LIMIT_SEX_CODE");
        //�Ա�����
        if (!StringUtil.isNullString(limitSex) &&
            !"N".equalsIgnoreCase(limitSex) && !sex.equalsIgnoreCase(limitSex)) {
            System.out.println("limitSex");
            return false;
        }
        String[] ages = StringTool.CountAgeByTimestamp(birthday, admDate);
        String[] reverseAges = StringTool.CountAgeByTimestamp(admDate, birthday);
        if (StringUtil.isNullArray(ages)) {
            return false;
        }
        long age = StringTool.getLong(ages[0]);
        long startAge = icd.getLong("START_AGE");
        long endAge = icd.getLong("END_AGE");
        if (icd.getBoolean("CAT_FLG")) {
            System.out.println("catFlg");
            return false;
        }
        //��������
        if (startAge > 0 && age < startAge) {
            return false;
        }
        if (endAge > 0 && age > endAge) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param icd TParm
     * @return boolean
     */
    public static boolean isAllowChnDiag(TParm icd) {
        if (icd.getBoolean("SYNDROME_FLG"))
            return false;
        return true;
    }

    /**
     * �ж�һ������Ƿ�����������
     * @param icdCode String
     * @return boolean
     */
    public static boolean isAllowMainDiag(String icdCode) {

        TParm icdParm = ICD.getBuffer(ICD.isFilter() ? ICD.FILTER : ICD.PRIMARY);
        Vector code = (Vector) icdParm.getData("ICD_CODE");
        int row = code.indexOf(icdCode);
        if (row < 0) {
            return false;
        }
        TParm parm = icdParm.getRow(row);
        return icdParm.getBoolean("MAIN_DIAG_FLG", row);
    }

    /**
     * ������ϴ��뷵������
     * @param icdCode String ��ϴ���
     * @return icdDesc String �������
     */
    public static String getIcdDesc(String icdCode) {
        TParm icdParm = ICD.getBuffer(ICD.isFilter() ? ICD.FILTER : ICD.PRIMARY);
        Vector code = (Vector) icdParm.getData("ICD_CODE");
        int row = code.indexOf(icdCode);
        if (row < 0) {
            return "";
        }
        TParm parm = icdParm.getRow(row);
        return icdParm.getValue("ICD_CHN_DESC", row);
    }

    /**
     * �����ҽ���Է�ҩ����ʾ��ɫ
     * @param ctz String[]
     * @param order TDataStore
     * @param whetherCallInsItf boolean
     * @return Map
     */
    public static Map getInsColor(String[] ctz, TDataStore order,
                                  boolean whetherCallInsItf) {

        Map color = new HashMap();
        if (!CTZTool.getInstance().getFlgByCtz(ctz[0]).getBoolean("NHI_CTZ_FLG",
            0))
            return color;
        String insPay;
        Color purple = new Color(128, 0, 128);
        for (int i = 0; i < order.rowCount(); i++) {
            int row = sysFeeCode.indexOf(order.getItemString(i, "ORDER_CODE"));
            if (row < 0)
                return color;
            insPay = sysfeeParm.getValue("INSPAY_TYPE", row);
            if ("C".equalsIgnoreCase(insPay)) {
                color.put(i, purple);
                continue;
            }
        }
        return color;
    }

    /**
     * �Ƿ���Ȩ�޿�����ҩƷ
     * @param orderCode String
     * @param userId String
     * @return boolean
     */
    public static boolean isHavingLiciense(String orderCode, String userId) {

        String sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE='" + orderCode +
            "'";
        TParm sysFee = new TParm(TJDODBTool.getInstance().select(sql));
        if (sysFee.getErrCode() != 0 || sysFee.getCount() != 1) {
            return false;
        }
        sysFee = sysFee.getRow(0);
        String lcsClassCode = sysFee.getValue("LCS_CLASS_CODE");
        if (StringUtil.isNullString(lcsClassCode)) {
            return true;
        }
        String lcsCode;

        TParm lcs = SYSOperatorTool.getInstance().getLcsCode(userId);
        if (lcs.getErrCode() != 0) {
            return false;
        }
        int count = lcs.getCount("LCS_CLASS_CODE");
        for (int i = 0; i < count; i++) {
            lcsCode = lcs.getValue("LCS_CLASS_CODE", i);
            if (lcsCode.equalsIgnoreCase(lcsClassCode)) {
                return true;
            }
        }

        return false;
    }

    /**
     * ����ҩƷ����TABLE��ɫ���ϣ���͹���ҩƷ����TABLE����ʾ��ɫ
     * @param color Map
     * @param order TDataStore
     * @return Map
     */
    public static Map getCtrlColor(Map color, TDataStore order) {

    	 Color red = new Color(255, 0, 0);    
         Color blue = new Color(0, 0, 255); // chenxi ��ʾ��ɫ
         TParm phaBaseParm = getPhaBase().getBuffer(TDS.PRIMARY);
         Vector baseCode = (Vector) phaBaseParm.getData("ORDER_CODE");
         for (int i = 0; i < order.rowCount(); i++) {
             String orderCode = order.getItemString(i, "ORDER_CODE"); 
             //$$=========== add by chenx start ==========$$//
 			String sql = "SELECT ORDER_CODE,DRUG_NOTES_DR FROM SYS_FEE WHERE ORDER_CODE = '" +orderCode+ "' " ;
 			TParm sysfeeParm = new TParm(TJDODBTool.getInstance().select(sql)) ;
 			
 			//�жϸ�ΣҩƷ
 	 		String sql2 = "SELECT HIGH_RISK_FLG FROM PHA_BASE WHERE ORDER_CODE='"
 				+orderCode
 				+ "' ";
 		    TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
 			//System.out.println("======chenxui======="+sql);
 			// sysfeeParm.getValue("DRUG_NOTES_DR", 0) ;
 			//$$=========== add by chenx end ==========$$//
             int row = baseCode.indexOf(orderCode);
             if (row < 0)
                 return color;
             //fux ��ΣҩƷҲ��Ϊ��ɫ 20150821  
             if (!StringUtil.isNullString(phaBaseParm.getValue(
                 "CTRLDRUGCLASS_CODE", row))||parm2.getValue("HIGH_RISK_FLG", 0).equals("Y")) {
                 color.put(i, red);
             }else if(!StringUtil.isNullString(sysfeeParm.getValue("DRUG_NOTES_DR", 0))){
             	
             	color.put(i, blue);    //==============���  DRUG_NOTES_DR��ֵ��tableҩ����ɫ��Ϊ��ɫ
         }
             }
         
        return color;
    }
    


    /**
     * ����PHA_BASE
     * @return TDS
     */
    public static TDS getPhaBase() {
        if (phaBase == null) {
            phaBase = new TDS();
            phaBase.setSQL(GET_PHABASE);
            phaBase.retrieve();
        }
        return phaBase;
    }

    /**
     * ���ؿ�����Ĭ��ʹ������
     * @param takeDays int
     * @param orderCode String
     * @return boolean
     */
    public static boolean isAllowAntiBiotic(int takeDays, String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        int limitDays = PhaBaseTool.getInstance().getAntiDrugDays(parm).getInt(
            "TAKE_DAYS", 0);

        return (limitDays > 0 && takeDays > limitDays);
    }

    /**
     * ���õ����ܼۣ�CHARGE_HOSP_CODE
     * @param orderCode String
     * @return TParm
     */
    public static TParm getPrice(String orderCode) {
        TParm result = new TParm();
        TParm sysFee = SYS_FEE.getBuffer(SYS_FEE.isFilter() ? SYS_FEE.FILTER :
                                         SYS_FEE.PRIMARY);

        Vector code = (Vector) sysFee.getData("ORDER_CODE");
        int row = code.indexOf(orderCode);
        if (row < 0)
            return result;
        result.setData("OWN_PRICE", sysFee.getDouble("OWN_PRICE", row));
        result.setData("CHARGE_HOSP_CODE",
                       sysFee.getValue("CHARGE_HOSP_CODE", row));

        return result;
    }

    public static void main(String[] args) {
//		JavaHisDebug.initClient();
        Timestamp t1 = StringTool.getTimestamp("20090821114800",
                                               "yyyyMMddHHmmss");
        Timestamp t2 = StringTool.getTimestamp("20090901080000",
                                               "yyyyMMddHHmmss");
        int days = StringTool.getDateDiffer(t1, t2);
//		System.out.println(2/3);
//		ODO odo = new ODO();
//		odo.setPat(new Pat());
//		odo.getPat().setMrNo("1");
//		odo.onQuery("ABC");
//		odo.getPrescriptionList().newOrderList("2");
//		System.out.println(TCM_Transform.getString(odo.getPrescriptionList().getGroupPrsptSize("2")-1)+"count");
//		TParm parm=OdoUtil.newOrderTParm(odo, "2",TCM_Transform.getString(odo.getPrescriptionList().getGroupPrsptSize("2")-1));
    }
}
