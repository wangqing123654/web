package jdo.erd;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: �������۱������Tool</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class ErdOrderSheetTool
    extends TJDOTool {

    /**
     * ʵ��
     */
    private static ErdOrderSheetTool instanceObject;

    /**
     * �õ�ʵ��
     * @return PatTool
     */
    public static ErdOrderSheetTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ErdOrderSheetTool();
        return instanceObject;
    }


    public ErdOrderSheetTool() {
    }

    /**
     *CASE_NO NAME MR_NO DEPT
     * @param data TParm
     * @return TParm
     */
    public TParm getPrintData(TParm parm) {
        TParm orderParm = new TParm();
        //�õ���ӡ����
        orderParm = getSTOrderParm(parm.getValue("CASE_NO"));
        orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_DAY");
        orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_TIME");
        orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DR_CODE");
        orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
        orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_CODE");
        TParm printData = new TParm();
        printData.setData("TABLE", orderParm.getData());
        printData.setData("NAME", "TEXT", parm.getValue("NAME"));
        printData.setData("MR_NO", "TEXT", parm.getValue("MR_NO"));
        printData.setData("DEPT", "TEXT", parm.getValue("DEPT"));

        return printData;

    }

    /**
     * ��øò�����ʱҽ��
     * @return TParm
     */
    private TParm getSTOrderParm(String caseNo) {
        TParm STparm = new TParm(TJDODBTool.getInstance().select(this.
            getSelectSQL(caseNo)));
        TParm printData = arrangeData(STparm);
        return printData;
    }


    private String getSelectSQL(String caseNo) {
        String sql =
            " SELECT   TO_CHAR (A.ORDER_DATE, 'MM/DD') AS EFF_DATE_DAY, " +
            " TO_CHAR (A.ORDER_DATE, 'HH24:MI') AS EFF_DATE_TIME, " +
            " A.DR_CODE AS ORDER_DR_CODE,A.ORDER_DESC,A.MEDI_QTY,F.UNIT_CHN_DESC,A.FREQ_CODE, " +
            " A.DOSE_TYPE,A.LINKMAIN_FLG,A.LINK_NO,A.DR_NOTE,A.ORDER_CODE,A.CAT1_TYPE, " +
            " TO_CHAR (A.NS_EXEC_DATE,'MM/DD HH24:MI') AS NS_EXEC_DATE,A.NS_EXEC_CODE,A.RX_TYPE AS RX_KIND " +
            " FROM   OPD_ORDER A, SYS_UNIT F " +
            " WHERE  A.CASE_NO='" + caseNo + "'" +
            " AND A.HIDE_FLG = 'N' " +
            " AND A.MEDI_UNIT = F.UNIT_CODE " +
            " ORDER BY   A.ORDER_DATE ";

        return sql;
    }

    /**
     * ��������
     * @param parm TParm
     * @return TParm
     */
    private TParm arrangeData(TParm parm) {
        TParm result = new TParm();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm order = parm.getRow(i);
            //�ж�����ҽ��
            if (ifLinkOrder(order)) {
                //���Ϊ����ҽ��ϸ�����账��
                if (ifLinkOrderSubItem(order))
                    continue;
                String finalOrder = getLinkOrder(order, parm);
                result.addData("ORDER_DESC", finalOrder);
            }
            else { //��ͨҽ��
                String drNote = (String) order.getData("DR_NOTE");
                String desc = (String) order.getData("ORDER_DESC");
                String qty = order.getData("MEDI_QTY") + "";
                String unit = (String) order.getData("UNIT_CHN_DESC");
                String freq = (String) order.getData("FREQ_CODE");
                String dose = (String) order.getData("DOSE_TYPE");
                String cat1 = (String) order.getData("DOSE_TYPE");
                //�ж��Ƿ���ҽ����ע
                if (ifZ00Order(order)) {
                    desc = drNote;
                    drNote = "";
                    qty = "";
                    unit = "";
                    freq = "";
                    dose = "";
                }
                //�����ҽ���Ƿ�PHA
                if ( (!checkOrderCat1(cat1)) && chackFreq(freq)) {
                    qty = "";
                    unit = "";
                    freq = "";
                    dose = "";
                }
                String finalDesc = "" + desc + "\r" + qty + " " + unit + " " +
                    freq +
                    " " + dose +
                    (drNote != null && drNote.length() != 0 ?
                     "\r" + "(" + drNote + ")" : "");
                //��Ҫ����--ҽ��
                result.addData("ORDER_DESC", finalDesc);
            }
            //����ҽ���������ò�ͬ��������

            result.addData("EFF_DATE_DAY", order.getData("EFF_DATE_DAY"));
            result.addData("EFF_DATE_TIME", order.getData("EFF_DATE_TIME"));
            result.addData("ORDER_DR_CODE", order.getData("ORDER_DR_CODE"));
            result.addData("NS_EXEC_DATE", order.getData("NS_EXEC_DATE"));
            result.addData("NS_EXEC_CODE", order.getData("NS_EXEC_CODE"));

        }
        result.setCount(result.getCount("EFF_DATE_DAY"));
        return result;
    }

    /**
     * �ж��Ƿ�������ҽ��
     * @return boolean
     */
    private boolean ifLinkOrder(TParm oneOrder) {
        String LinkNo = (String) oneOrder.getData("LINK_NO");
        if (LinkNo == null || LinkNo.length() == 0)
            return false;
        return true;
    }

    /**
     * �ж��Ƿ�������ҽ������
     * @return boolean
     */
    private boolean ifLinkOrderSubItem(TParm oneOrder) {
        return!TypeTool.getBoolean(oneOrder.getData("LINKMAIN_FLG"));
    }

    /**
     * �жϸ�ҽ���Ƿ���PHA����
     * @param code String
     * @return boolean
     */
    private boolean checkOrderCat1(String code) {
        return "PHA".equals(code);
    }

    /**
     * ��PHAҽ�� ��AW1,.,STAT ����ʾƾ��Ƶ�Σ�
     * @param freq String
     * @return boolean
     */
    private boolean chackFreq(String freq) {
        return "AW1".equals(freq) || ".".equals(freq) || "STAT".equals(freq);
    }

    /**
     * �ж�������ҽ����ע
     * @param parm TParm
     * @return boolean
     */
    private boolean ifZ00Order(TParm parm) {
        String orderCode = (String) parm.getData("ORDER_CODE");
        return orderCode.startsWith("Z");
    }

    /**
     * ��������ҽ��ORDER_DESC
     * @param order TParm
     * @param parm TParm
     * @return String
     */
    private String getLinkOrder(TParm order, TParm parm) {
        String resultDesc = "";
        String mainOrder = (String) order.getData("ORDER_DESC");
        String mainNote = (String) order.getData("DR_NOTE");
        String mainmediQty = order.getData("MEDI_QTY") + "";
        String mainUnit = (String) order.getData("UNIT_CHN_DESC");
        String mainFreq = (String) order.getData("FREQ_CODE");
        String mainDose = (String) order.getData("DOSE_TYPE");
        String mainLinkNo = (String) order.getData("LINK_NO");
        String mainRxKind = (String) order.getData("RX_KIND");
        resultDesc = mainOrder + " " + mainmediQty + "" + mainUnit +
            (mainNote != null && mainNote.length() != 0 ?
             "\r" + "(" + mainNote + ")" : "");
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            String linkNo = (String) parm.getData("LINK_NO", i);
            String rxKind = (String) parm.getData("RX_KIND", i);
            if (rxKind.equals(mainRxKind) && mainLinkNo.equals(linkNo) &&
                !TypeTool.getBoolean(parm.getData("LINKMAIN_FLG", i))) {
                String subOrder = (String) parm.getData("ORDER_DESC", i);
                String submediQty = parm.getData("MEDI_QTY", i) + "";
                String subUnit = (String) parm.getData("UNIT_CHN_DESC", i);
                String subNote = (String) parm.getData("DR_NOTE", i);
                resultDesc += "\r" + subOrder + " " + submediQty + "" + subUnit +
                    (subNote != null && subNote.length() != 0 ?
                     "\r" + "(" + subNote + ")" : "");
            }
            else
                continue;
        }
        resultDesc += "\r     " + mainFreq + " " + mainDose + " " + "�� " +
            mainLinkNo + " ��";
        return resultDesc;
    }

}
