package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import java.util.Vector;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * <p>Title: ����ҽ��ʡҽ���ָ����</p>
 *
 * <p>Description: ����ҽ��ʡҽ���ָ����</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version JavaHis 1.0
 */
public class INSOpdOrderSXTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static INSOpdOrderSXTool instanceObject;
    /**
     * �õ�ʵ��
     * @return INSOpdApproveTool
     */
    public static INSOpdOrderSXTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INSOpdOrderSXTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public INSOpdOrderSXTool() {
        setModuleName("ins\\INSOpdOrderSXModule.x");
        onInit();
    }
    /**
     * ɾ������
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm deldata(TParm parm,TConnection conn){
    TParm result = new TParm();
    String caseNo = parm.getValue("CASE_NO");
    String orderCode = parm.getValue("ORDER_CODE");
    String orderNo = parm.getValue("ORDER_NO");
    int orderSeq = TCM_Transform.getInt(parm.getValue("ORDER_SEQ"));
    String billDate = TCM_Transform.getString(parm.getValue("BILL_DATE"));
    String ordersetCode = parm.getValue("ORDERSET_CODE");
    int orderGroupNo = parm.getInt("ORDER_GROUP_NO");
    String deptCode = parm.getValue("DEPT_CODE");
    String drCode = parm.getValue("DR_CODE");
    String exeDeptCode = parm.getValue("EXE_DEPT_CODE");
    String doseCode = parm.getValue("DOSE_CODE");
    double price = parm.getDouble("PRICE");
    double totQty = parm.getDouble("QTY");
    double totalAmt = parm.getDouble("TOT_AMT");

    TParm getCtzInRegPatadm = INSOpdApproveTool.getInstance().
                              getCtzInRegPatadm(caseNo);
    String ctzCode1 = getCtzInRegPatadm.getValue("CTZ1_CODE", 0);
    String ctzCode2 = getCtzInRegPatadm.getValue("CTZ2_CODE", 0);
    TParm parm1 = new TParm();
    parm1.setData("CASE_NO", caseNo);
    parm1.setData("ORDER_CODE", orderCode);
    parm1.setData("ORDER_NO", orderNo);
    parm1.setData("ORDER_SEQ", orderSeq);
    //��ѯ֧����׼�е����������,����˵������ͨ��order
    TParm parm2 = new TParm();
    parm2.setData("CASE_NO", caseNo);
    parm2.setData("ORDER_CODE", orderCode);
    parm2.setData("CTZ1_CODE", ctzCode1);
    TJDODBTool tool = TJDODBTool.getInstance();
    TParm data2 = new TParm();
    if (ctzCode2 == null || ctzCode2.trim().length() == 0 ||
        ctzCode2.equals("null")) {
        parm2.setData("CZT2_CODENULL", "00");
        String sql =
                "    SELECT A.ORDER_CODE,A.OWN_RATE,A.ADDPAY_FLG,A.ADDPAY_RATE," +
                "           A.APPROVE_FLG AS APPROVE_FLGA,NVL(B.APPROVE_FLG,'01') AS APPROVE_FLG " +
                "      FROM INS_OPD_RULE A " +
                " LEFT JOIN INS_OPD_APPROVE B ON A.ORDER_CODE=B.ORDER_CODE AND B.CASE_NO = '" +
                caseNo + "' " +
                "       AND A.ORDER_CODE = '" + orderCode +
                "' AND A.CTZ1_CODE='" + ctzCode1 + "' " +
                "  AND (A.CTZ2_CODE IS NULL OR A.CTZ2_CODE= '00')  " +
                "  AND ORDER_GROUP_NO= '" + orderGroupNo + "'" +
                "  WHERE A.ORDER_CODE = '" + orderCode +
                "' AND A.CTZ1_CODE='" + ctzCode1 + "' " +
                "  AND (A.CTZ2_CODE IS NULL OR A.CTZ2_CODE= '00') ";
//            System.out.println("==========="+sql);
        data2 = new TParm(tool.select(sql));
        if (data2.getErrCode() < 0) {
            err("ERR:" + data2.getErrCode() + data2.getErrText()
                + data2.getErrName());
            return data2;
        }
    } else {
        parm2.setData("CTZ2_CODE", ctzCode2);
        data2 = new TParm(tool.select(
                "    SELECT A.ORDER_CODE,A.OWN_RATE,A.ADDPAY_FLG,A.ADDPAY_RATE," +
                "           A.APPROVE_FLG AS APPROVE_FLGA,NVL(B.APPROVE_FLG,'01') AS APPROVE_FLG " +
                "      FROM INS_OPD_RULE A " +
                " LEFT JOIN INS_OPD_APPROVE B ON A.ORDER_CODE=B.ORDER_CODE AND B.CASE_NO = '" +
                caseNo + "' " +
                "       AND A.ORDER_CODE = '" + orderCode +
                "' AND A.CTZ1_CODE='" + ctzCode1 + "' " +
                "  AND A.CTZ2_CODE='" + ctzCode2 + "' " +
                "  AND ORDER_GROUP_NO= '" + orderGroupNo + "'" +
                "  WHERE A.ORDER_CODE = '" + orderCode +
                "' AND A.CTZ1_CODE='" + ctzCode1 + "' " +
                "  AND A.CTZ2_CODE='" + ctzCode2 + "' "));
        if (data2.getErrCode() < 0) {
            err("ERR:" + data2.getErrCode() + data2.getErrText()
                + data2.getErrName());
            return data2;
        }
    }
    int rowCount = data2.getInt("ACTION", "COUNT");
    if (rowCount == 0) {
        if (TCM_Transform.getString(data2.getData("APPROVE_FLGA", 0)) == null ||
            TCM_Transform.getString(data2.getData("APPROVE_FLG", 0)) == null) {
            Vector vctdata2 = new Vector();
            vctdata2.add(0, 1);
            data2.setData("OWN_RATE", vctdata2);
        }
        if (TCM_Transform.isNull(data2.getData("OWN_RATE", 0))) {
            data2.addData("OWN_RATE", 1);
            data2.addData("APPROVE_FLG", "04");
        }
    }
    for (int i = 0; i < rowCount; i++) {
        if (TCM_Transform.getString(data2.getData("APPROVE_FLGA", i)) == null ||
            TCM_Transform.getString(data2.getData("APPROVE_FLG", i)).
            equals("04") ||
            (TCM_Transform.getString(data2.getData("APPROVE_FLG", i)).
             equals("01") &&
             TCM_Transform.getString(data2.getData("APPROVE_FLGA",
                i)).equals("Y")) ||
            TCM_Transform.getString(data2.getData("APPROVE_FLG", i)) == null ||
            TCM_Transform.getString(data2.getData("APPROVE_FLG", i)).
            length() == 0 ||
            TCM_Transform.getString(data2.getData("APPROVE_FLG", i)).
            equals("02")) {
            Vector vctdata2 = new Vector();
            vctdata2.add(0, 1);
            data2.setData("OWN_RATE", vctdata2);
        }
        if (TCM_Transform.isNull(data2.getData("OWN_RATE", i))) {
            data2.addData("OWN_RATE", 1);
            data2.addData("APPROVE_FLG", "04");
        }
    }

    TParm parm4 = new TParm();
    TParm endData = new TParm();
    parm4.setData("CASE_NO", caseNo);
    parm4.setData("ORDER_NO", orderNo);
    parm4.setData("ORDER_SEQ", orderSeq);
    //�ж��Ƿ��Ѵ����ڷָ�������
    endData = INSOpdOrderSXTool.getInstance().existsOrder(parm4);
    if (endData.getCount() > 0){
        this.deleteOrder(parm4,conn);
    }
    return result;

}
    /**
     * ����ָ�����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection conn) {
        TParm result = new TParm();
        String caseNo = parm.getValue("CASE_NO");
        String orderCode = parm.getValue("ORDER_CODE");
        String orderNo = parm.getValue("ORDER_NO");
        int orderSeq = TCM_Transform.getInt(parm.getValue("ORDER_SEQ"));
        String billDate = TCM_Transform.getString(parm.getValue("BILL_DATE"));
        String ordersetCode = parm.getValue("ORDERSET_CODE");
        int orderGroupNo = parm.getInt("ORDER_GROUP_NO");
        String deptCode = parm.getValue("DEPT_CODE");
        String drCode = parm.getValue("DR_CODE");
        String exeDeptCode = parm.getValue("EXE_DEPT_CODE");
        String doseCode = parm.getValue("DOSE_CODE");
        double price = parm.getDouble("PRICE");
        double totQty = parm.getDouble("QTY");
        double totalAmt = parm.getDouble("TOT_AMT");

        TParm getCtzInRegPatadm = INSOpdApproveTool.getInstance().
                                  getCtzInRegPatadm(caseNo);
        String ctzCode1 = getCtzInRegPatadm.getValue("CTZ1_CODE", 0);
        String ctzCode2 = getCtzInRegPatadm.getValue("CTZ2_CODE", 0);
        TParm parm1 = new TParm();
        parm1.setData("CASE_NO", caseNo);
        parm1.setData("ORDER_CODE", orderCode);
        parm1.setData("ORDER_NO", orderNo);
        parm1.setData("ORDER_SEQ", orderSeq);
        //��ѯ֧����׼�е����������,����˵������ͨ��order
        TParm parm2 = new TParm();
        parm2.setData("CASE_NO", caseNo);
        parm2.setData("ORDER_CODE", orderCode);
        parm2.setData("CTZ1_CODE", ctzCode1);
        TJDODBTool tool = TJDODBTool.getInstance();
        TParm data2 = new TParm();
        if (ctzCode2 == null || ctzCode2.trim().length() == 0 ||
            ctzCode2.equals("null")) {
            parm2.setData("CZT2_CODENULL", "00");
            String sql =
                    "    SELECT A.ORDER_CODE,A.OWN_RATE,A.ADDPAY_FLG,A.ADDPAY_RATE," +
                    "           A.APPROVE_FLG AS APPROVE_FLGA,NVL(B.APPROVE_FLG,'01') AS APPROVE_FLG " +
                    "      FROM INS_OPD_RULE A " +
                    " LEFT JOIN INS_OPD_APPROVE B ON A.ORDER_CODE=B.ORDER_CODE AND B.CASE_NO = '" +
                    caseNo + "' " +
                    "       AND A.ORDER_CODE = '" + orderCode +
                    "' AND A.CTZ1_CODE='" + ctzCode1 + "' " +
                    "  AND (A.CTZ2_CODE IS NULL OR A.CTZ2_CODE= '00')  " +
                    "  AND ORDER_GROUP_NO= '" + orderGroupNo + "'" +
                    "  WHERE A.ORDER_CODE = '" + orderCode +
                    "' AND A.CTZ1_CODE='" + ctzCode1 + "' " +
                    "  AND (A.CTZ2_CODE IS NULL OR A.CTZ2_CODE= '00') ";
//            System.out.println("==========="+sql);
            data2 = new TParm(tool.select(sql));
            if (data2.getErrCode() < 0) {
                err("ERR:" + data2.getErrCode() + data2.getErrText()
                    + data2.getErrName());
                return data2;
            }
        } else {
            parm2.setData("CTZ2_CODE", ctzCode2);
            data2 = new TParm(tool.select(
                    "    SELECT A.ORDER_CODE,A.OWN_RATE,A.ADDPAY_FLG,A.ADDPAY_RATE," +
                    "           A.APPROVE_FLG AS APPROVE_FLGA,NVL(B.APPROVE_FLG,'01') AS APPROVE_FLG " +
                    "      FROM INS_OPD_RULE A " +
                    " LEFT JOIN INS_OPD_APPROVE B ON A.ORDER_CODE=B.ORDER_CODE AND B.CASE_NO = '" +
                    caseNo + "' " +
                    "       AND A.ORDER_CODE = '" + orderCode +
                    "' AND A.CTZ1_CODE='" + ctzCode1 + "' " +
                    "  AND A.CTZ2_CODE='" + ctzCode2 + "' " +
                    "  AND ORDER_GROUP_NO= '" + orderGroupNo + "'" +
                    "  WHERE A.ORDER_CODE = '" + orderCode +
                    "' AND A.CTZ1_CODE='" + ctzCode1 + "' " +
                    "  AND A.CTZ2_CODE='" + ctzCode2 + "' "));
            if (data2.getErrCode() < 0) {
                err("ERR:" + data2.getErrCode() + data2.getErrText()
                    + data2.getErrName());
                return data2;
            }
        }
        int rowCount = data2.getInt("ACTION", "COUNT");
        if (rowCount == 0) {
            if (TCM_Transform.getString(data2.getData("APPROVE_FLGA", 0)) == null ||
                TCM_Transform.getString(data2.getData("APPROVE_FLG", 0)) == null) {
                Vector vctdata2 = new Vector();
                vctdata2.add(0, 1);
                data2.setData("OWN_RATE", vctdata2);
            }
            if (TCM_Transform.isNull(data2.getData("OWN_RATE", 0))) {
                data2.addData("OWN_RATE", 1);
                data2.addData("APPROVE_FLG", "04");
            }
        }
        for (int i = 0; i < rowCount; i++) {
            if (TCM_Transform.getString(data2.getData("APPROVE_FLGA", i)) == null ||
                TCM_Transform.getString(data2.getData("APPROVE_FLG", i)).
                equals("04") ||
                (TCM_Transform.getString(data2.getData("APPROVE_FLG", i)).
                 equals("01") &&
                 TCM_Transform.getString(data2.getData("APPROVE_FLGA",
                    i)).equals("Y")) ||
                TCM_Transform.getString(data2.getData("APPROVE_FLG", i)) == null ||
                TCM_Transform.getString(data2.getData("APPROVE_FLG", i)).
                length() == 0 ||
                TCM_Transform.getString(data2.getData("APPROVE_FLG", i)).
                equals("02")) {
                Vector vctdata2 = new Vector();
                vctdata2.add(0, 1);
                data2.setData("OWN_RATE", vctdata2);
            }
            if (TCM_Transform.isNull(data2.getData("OWN_RATE", i))) {
                data2.addData("OWN_RATE", 1);
                data2.addData("APPROVE_FLG", "04");
            }
        }

        TParm parm4 = new TParm();
        TParm endData = new TParm();
        parm4.setData("CASE_NO", caseNo);
        parm4.setData("ORDER_NO", orderNo);
        parm4.setData("ORDER_SEQ", orderSeq);
        //�ж��Ƿ��Ѵ����ڷָ�������
        endData = INSOpdOrderSXTool.getInstance().existsOrder(parm4);
        if (endData.getCount() > 0){
            this.deleteOrder(parm4,conn);
        }
        TParm parm5 = new TParm();
        parm5.setData("CASE_NO", caseNo); //�������
//        String billDate = StringTool.getString((Timestamp) data1.getData(
//                "ORDER_TIME", 0), "yyyyMMdd");//��������
        parm5.setData("BILL_DATE", billDate); //��ϸ������ʱ��
        parm5.setData("ORDER_NO", orderNo); //����ǩ��
        parm5.setData("ORDER_SEQ", orderSeq); //ҽ�����
        parm5.setData("ORDER_CODE", orderCode); //ҽ������
        parm5.setData("NHI_ORDER_CODE", orderCode); //ҽ��ҽ������
        parm5.setData("ORDERSET_CODE", ordersetCode); //����ҽ��
        parm5.setData("ORDER_GROUP_NO", orderGroupNo); //����ҽ��Ⱥ��
        parm5.setData("DEPT_CODE", deptCode); //��������
        parm5.setData("DR_CODE", drCode); //����ҽ��
        parm5.setData("EXE_DEPT_CODE", exeDeptCode); //ִ�п���
        parm5.setData("APPROVE_FLG", data2.getValue("APPROVE_FLG", 0)); //���״̬
        TParm parm6 = new TParm();
        parm6.setData("ORDER_CODE", orderCode);
        TParm getOrderInfo1 = INSOpdApproveTool.getInstance().getOrderInfo1(
                parm6);
        parm5.setData("ORDER_DESC", getOrderInfo1.getValue("ORDER_DESC", 0)); //ҽ������
        parm5.setData("STANDARD", getOrderInfo1.getValue("DESCRIPTION", 0)); //���
        TParm parm7 = new TParm();
        parm7.setData("DOSE_CODE", doseCode);
        TParm getOrderInfo2 = INSOpdApproveTool.getInstance().getOrderInfo2(
                parm7);
        String doseDesc = TCM_Transform.getString(getOrderInfo2.getData(
                "DOSE_CHN_DESC", 0));
        parm5.setData("DOSE_CODE", doseCode); //���ʹ���
        parm5.setData("DOSE_DESC", doseDesc); //��������
        parm5.setData("OWN_RATE", data2.getDouble("OWN_RATE", 0)); //�Ը�����
        double ownRate = data2.getDouble("OWN_RATE", 0);
        double ownAmt = ownRate * totalAmt;
        double nhiAmt = totalAmt - ownAmt;
        String addpayFlg = TCM_Transform.getString(data2.getData("ADDPAY_FLG",
                0));
        double addpayRate = TCM_Transform.getDouble(data2.getData("ADDPAY_RATE",
                0));
        if (addpayFlg.equals("Y")) {
            ownAmt = ownAmt + nhiAmt * addpayRate;
            nhiAmt = totalAmt - ownAmt;
        }
        parm5.setData("PRICE", StringTool.round(price, 4)); //����
        parm5.setData("QTY", StringTool.round(totQty, 2)); //����
        parm5.setData("OWN_AMT", StringTool.round(ownAmt, 2)); //�Ը����
        parm5.setData("NHI_AMT", StringTool.round(nhiAmt, 2)); //ҽ��֧�����
        parm5.setData("TOTAL_AMT", StringTool.round(totalAmt, 2)); //ҽ���걨���
        parm5.setData("OPT_USER", parm.getData("OPT_USER")); //������Ա
        parm5.setData("OPT_TERM", parm.getData("OPT_TERM")); //������ĩ
        parm5.setData("BKC023", parm.getData("BKC023")); //���׺�
        result = this.update("insertInfo", parm5, conn);
        parm5.setErrCode(result.getErrCode());
        parm5.setErrText(result.getErrText());
        parm5.setErrName(result.getErrName());
        return parm5;
    }

    /**
     * ��ѯ���������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        TParm result = new TParm();
        result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯopb_tran��Ҫ���з��÷ָ��order
     * @param parm TParm
     * @return TParm
     */
    public TParm selOpbTran(TParm parm) {
        TParm result = new TParm();
        result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��֧����׼�в������˵�ҽ������
     * @param parm TParm
     * @return TParm
     */
    public TParm getOrderInRule(TParm parm) {
        TParm result = query("getOrderInRule", parm);
        return result;
    }

    /**
     * �ж�order�Ƿ��Ѵ����ڷָ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm existsOrder(TParm parm) {
        TParm result = query("existsOrder", parm);
        return result;
    }

    /**
     * �õ���˱��λ
     * @param parm TParm
     * @return TParm
     */
    public TParm getApproveFlg(TParm parm) {
        TParm result = query("getApproveFlg", parm);
        return result;
    }

    /**
     * ��ѯ�ָ��������
     * @return TParm
     */
    public TParm selectalldata(TParm parm) {
        TParm result = query("selectalldata", parm);
        return result;

    }

    /**
     * �����������
     * @param parm TParm
     * @return TParm
     */
    public TParm insertPayMInfo(TParm parm, TConnection conn) {
        TParm result = update("insertPayMInfo", parm, conn);
        return result;
    }

    /**
     * Ϊ����������ȡ����
     * @param parm TParm
     * @return TParm
     */
    public TParm seldataforpaym(TParm parm) {
        TParm result = query("seldataforpaym", parm);
        return result;
    }

    /**
     * �ж��Ƿ��Ѵ����ڽ�������
     * @param parm TParm
     * @return TParm
     */
    public TParm existsPaym(TParm parm) {
        TParm result = query("existsPaym", parm);
        return result;
    }

    /**
     * ȡ�ò���Ƽ�order_seq
     * @param parm TParm
     * @return TParm
     */
    public TParm getAddOrderSeq(TParm parm) {
        TParm result = query("getAddOrderSeq", parm);
        return result;
    }

    /**
     * ɾ���ָ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteOrder(TParm parm,TConnection connection) {
        TParm result = update("deleteOrder", parm,connection);
        return result;
    }

    /**
     * ɾ����������
     * @param parm TParm
     * @return TParm
     */
    public TParm deletePaym(TParm parm) {
        TParm result = update("deletePaym", parm);
        return result;
    }

    /**
     * ��֧���ֵ��в�ѯҽ��֧�����
     * @param parm TParm
     * @return TParm
     */
    public TParm selInsPayTypeInRule(TParm parm) {
        TParm result = query("selInsPayTypeInRule", parm);
        return result;

    }
}
