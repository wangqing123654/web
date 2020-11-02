package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

/**
 *
 * <p>Title:����ҽ����˹����� </p>
 *
 * <p>Description:����ҽ����˹����� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2008.11.05
 * @version JavaHis 1.0
 */
public class INSOpdApproveTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static INSOpdApproveTool instanceObject;
    /**
     * �õ�ʵ��
     * @return INSOpdApproveTool
     */
    public static INSOpdApproveTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INSOpdApproveTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public INSOpdApproveTool() {
        setModuleName("ins\\INSOpdApproveModule.x");
        onInit();
    }

    /**
     * �õ�������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selPatInfo(TParm parm) {
        TParm initparm = new TParm();
        initparm.setData("ADM_DATE", parm.getValue("ADM_DATE"));
        initparm.setData("MR_NO", parm.getValue("MR_NO"));
        TParm result = new TParm();
        result = this.query("selPatInfo", initparm);
        return result;
    }

    /**
     * ��������ҽ�������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm, TConnection conn) {
        TParm result = update("updatedata", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
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
     * �����������
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection conn) {
        TParm result = new TParm();
        //��ѯOPB_TRAN������order
        TParm parm1 = new TParm();
        parm1.setRowData(parm);
        TParm data1 = query("selOpbTran", parm1);
        if (data1.getErrCode() < 0) {
            err("ERR:" + data1.getErrCode() + data1.getErrText()
                + data1.getErrName());
            result.setErr(data1);
            return result;
        }
        int count1 = data1.getCount();
        for (int i = 0; i < count1; i++) {
            //ҽ������
            String billDate = StringTool.getString((Timestamp) data1.getData(
                    "ORDER_TIME", i), "yyyyMMdd");
            String orderNo = data1.getValue("ORDER_NO", i);
            int orderSeq = data1.getInt("ORDER_SEQ", i);
            String orderCode = data1.getValue("ORDER_CODE", i);
            String orderSet = data1.getValue("ORDERSET_CODE", i);
            String ordsetGroupNo = data1.getValue("ORDSET_GROUP_NO", i);
            String deptcode = data1.getValue("DEPT_CODE", i);
            String drCode = data1.getValue("DR_CODE", i);
            String rborderDeptCode = data1.getValue("RBORDER_DEPT_CODE", i);
            String doseCode = data1.getValue("DOSE_CODE", i);
            double price = data1.getDouble("OWN_PRICE", i);
            double totQty = data1.getDouble("TOT_QTY", i);
            double totalAmt = data1.getDouble("TOT_AMT", i);
            //��ѯ֧����׼�е�����δ���order
            TParm parm2 = new TParm();
            parm2.setData("ORDER_CODE", orderCode);
            parm2.setData("CTZ1_CODE", parm1.getValue("CTZ1_CODE"));
            if (parm1.getValue("CTZ2_CODE") == null ||
                parm1.getValue("CTZ2_CODE").length() < 1) {
                parm2.setData("CZT2_CODENULL", "00");
            } else {
                parm2.setData("CTZ2_CODE", parm1.getValue("CTZ2_CODE"));
            }
            TParm data2 = query("getOrderInRule", parm2);
            if (data2.getErrCode() < 0) {
                err("ERR:" + data2.getErrCode() + data2.getErrText()
                    + data2.getErrName());
                return result;
            }
            if (data2.getCount() <= 0)
                continue;
            TParm parm4 = new TParm();
            TParm endData = new TParm();

            parm4.setData("CASE_NO",parm.getData("CASE_NO"));
            parm4.setData("ORDER_CODE", orderCode);
            parm4.setData("ORDER_NO", orderNo);
            parm4.setData("ORDER_SEQ", orderSeq);
            //�ж��Ƿ��Ѵ��������������
            endData = this.existsOrder(parm4);
            if (endData.getCount() > 0)
                continue;
            TParm parm5 = new TParm(); //������˵�����
            parm5.setData("CASE_NO", data1.getData("CASE_NO", 0)); //�������
            parm5.setData("BILL_DATE", billDate); //��ϸ������ʱ��
            parm5.setData("ORDER_NO", orderNo); //����ǩ��
            parm5.setData("ORDER_SEQ", orderSeq); //ҽ�����
            parm5.setData("ORDER_CODE", orderCode); //ҽ������
            parm5.setData("NHI_ORDER_CODE", orderCode); //ҽ��ҽ������
            parm5.setData("ORDERSET_CODE", orderSet); //����ҽ��
            parm5.setData("ORDER_GROUP_NO", ordsetGroupNo); //����ҽ��Ⱥ��
            parm5.setData("DEPT_CODE", deptcode); //��������
            parm5.setData("DR_CODE", drCode); //����ҽ��
            parm5.setData("EXE_DEPT_CODE", rborderDeptCode); //ִ�п���
            parm5.setData("APPROVE_FLG", parm.getData("APPROVE_FLG")); //���״̬

            TParm parm6 = new TParm(); //
            parm6.setData("ORDER_CODE", orderCode);
            TParm getOrderInfo1 = this.getOrderInfo1(parm6);
            parm5.setData("ORDER_DESC", getOrderInfo1.getValue("ORDER_DESC", 0)); //ҽ������
            parm5.setData("STANDARD", getOrderInfo1.getValue("DESCRIPTION", 0)); //���
            TParm parm7 = new TParm();
            parm7.setData("DOSE_CODE", doseCode);
            TParm getOrderInfo2 = this.getOrderInfo2(parm7);
            parm5.setData("DOSE_CODE", doseCode); //���ʹ���
            parm5.setData("DOSE_DESC",
                          getOrderInfo2.getValue("DOSE_CHN_DESC", 0)); //��������
            TParm parm8 = new TParm();
            parm8.setData("CTZ1_CODE", parm1.getData("CTZ1_CODE"));
            if (parm1.getValue("CTZ2_CODE") == null ||
                parm1.getValue("CTZ2_CODE").length() < 1) {
                parm8.setData("CZT2_CODENULL", "00");
            } else {
                parm8.setData("CTZ2_CODE", parm1.getValue("CTZ2_CODE"));
            }
            parm8.setData("ORDER_CODE", orderCode);
            TParm getOrderInfo3 = this.getOrderInfo3(parm8);
            parm5.setData("OWN_RATE", getOrderInfo3.getDouble("OWN_RATE", 0)); //�Ը�����
            double ownRate = getOrderInfo3.getDouble("OWN_RATE", 0);
            double ownAmt = ownRate * totalAmt;
            double nhiAmt = totalAmt - ownAmt;
//            System.out.println("1111"+ownRate);
//            System.out.println("2222"+ownAmt);
//            System.out.println("3333"+nhiAmt);
            parm5.setData("PRICE", StringTool.round(price, 4)); //����
            parm5.setData("QTY", StringTool.round(totQty, 2)); //����
            parm5.setData("OWN_AMT", StringTool.round(ownAmt, 2)); //�Ը����
            parm5.setData("NHI_AMT", StringTool.round(nhiAmt, 2)); //ҽ��֧�����
            parm5.setData("TOTAL_AMT", StringTool.round(totalAmt, 2)); //ҽ���걨���
            parm5.setData("OPT_USER", parm.getData("OPT_USER")); //������Ա
            parm5.setData("OPT_TERM", parm.getData("OPT_TERM")); //������ĩ
            result = this.update("insertInfo", parm5, conn);

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
     * ͨ�������ŵõ�������ż�
     * @param parm TParm
     * @return TParm
     */
    public TParm getCaseNoByMrNo(TParm parm) {
        TParm data = new TParm();
        data.setData("ADM_DATE", parm.getValue("ADM_DATE"));
        data.setData("MR_NO", parm.getValue("MR_NO"));
        TParm result = query("getCaseNoByMrNo", data);
        return result;
    }

    /**
     * �ж�order�Ƿ��Ѵ���
     * @param parm TParm
     * @return TParm
     */
    public TParm existsOrder(TParm parm) {
        TParm result = query("existsOrder", parm);
        return result;
    }

    /**
     * order��Ϣȱʡ�ֶβ�ѯ,ҽ������,��������,���
     * @param parm TParm
     * @return TParm
     */
    public TParm getOrderInfo1(TParm parm) {
        TParm result = query("selOrderInfo", parm);
        return result;
    }

    /**
     * order��Ϣȱʡ�ֶβ�ѯ,��������
     * @param parm TParm
     * @return TParm
     */
    public TParm getOrderInfo2(TParm parm) {
        TParm result = query("selDoseDesc", parm);
        return result;
    }

    /**
     * order��Ϣȱʡ�ֶβ�ѯ,�Ը�����
     * @param parm TParm
     * @return TParm
     */
    public TParm getOrderInfo3(TParm parm) {
        TParm result = query("selOwnRate", parm);
        return result;
    }

    /**
     * ��������ݵõ���Լ�����ش���
     * @param ctzCode String
     * @return TParm
     */
    public TParm getCompanyByCtz(String ctzCode) {
        TParm data = new TParm();
        data.setData("CTZ_CODE", ctzCode);
        TParm result = query("getCompanyByCtz", data);
        return result;
    }

    /**
     * �ӹҺ������õ������
     * @param caseNo String
     * @return TParm
     */
    public TParm getCtzInRegPatadm(String caseNo) {
       // System.out.println("����jdo");
        TParm data = new TParm();
        data.setData("CASE_NO", caseNo);
        TParm result = query("getCtzInRegPatadm", data);
       // System.out.println("result"+result);
        return result;
    }

    /**
     * ������ݴ���õ�ҽ����ݱ��
     * @param ctzCode String
     * @return TParm
     */
    public TParm getFlgByCtz(String ctzCode) {
        TParm data = new TParm();
        data.setData("CTZ_CODE", ctzCode);
        TParm result = query("getFlgByCtz", data);
        return result;
    }

    /**
     * ��ѯ֧����׼�еı�ע
     * @param ctzCode String
     * @param orderCode String
     * @return TParm
     */
    public TParm selDescription(String ctzCode, String ctzCode2,
                                String orderCode) {
        TParm data = new TParm();
        data.setData("CTZ1_CODE", ctzCode);
        data.setData("ORDER_CODE", orderCode);
        if (ctzCode2 == null || ctzCode2.length() < 1) {
            data.setData("CZT2_CODENULL", "00");
        } else {
            data.setData("CTZ2_CODE", ctzCode2);
        }
        TParm result = query("selDescription", data);
        return result;

    }

    /**
     * ���½������
     * @param parm TParm
     * @return TParm
     */
    public TParm updateCtz(TParm parm, TConnection conn) {
        TParm result = update("updateCtz", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ����˵�����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm delApprove(TParm parm, TConnection conn) {
        TParm result = update("delApprove", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���µ�ǰ���ﲡ������order���״̬λ
     * @param parm TParm
     * @return TParm
     */
    public TParm updateAllApprove(TParm parm) {
        TParm result = update("updateAllApprove", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
