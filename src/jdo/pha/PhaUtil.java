package jdo.pha;

import java.util.Vector;
import jdo.opd.OrderList;
import com.dongyang.data.TParm;
import jdo.sys.Pat;
import java.util.Map;
import java.util.Date;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;


/**
 *
 * <p>Title: ����ҩ��������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS (c)</p>
 *
 * @author ZangJH 2008.09.26
 *
 * @version 1.0
 */

public class PhaUtil {

    public Vector initParm(TParm parm) {
        //��Ų�ѯ����������orderLsit����
        Vector orderListVector = new Vector();

        //��OPD�з��ص�ʱ��keyΪLIST
        int count = parm.getCount("LIST");

        //ѭ�����ΰ�OrderListװ��Vector
        for (int i = 0; i < count; i++) {
            TParm listParm = new TParm();
            //��TParm�еõ�MAPȻ����setData�ٰ�װ��TParm
            listParm.setData( (Map) parm.getData("LIST", i));
            if (listParm.getCount() <= 0)
                continue;
            String mrNo = listParm.getValue("MR_NO", 0);
            Pat pat = new Pat();
            //��һ���ٵ�PAT����
            pat.setMrNo(mrNo);
            OrderList ordList = new OrderList();
            ordList.setPat(pat);
            //��ʼ��һ��orderList
            ordList.initParm(listParm);
            orderListVector.add(ordList);
        }
        return orderListVector;
    }


    /**
     * ����ָ���±귵��ĳһ��OrderList
     * @param count
     * @return OrderList
     */
    public OrderList getCertainOrdListByRow(int count, Vector orderListVector) {

        if (orderListVector == null || orderListVector.size() == 0)
            return null;
        return (OrderList) orderListVector.get(count);
    }
    /**
     * ȡ������OrderList��parm�ṹ��Ϊ��ʾʹ��
     * @return TParm
     */
    public TParm getAllOrderListParm(Vector ordListVector) {
        TParm parm = new TParm();
        //�õ�orderList������
        int count = this.getOrderListCount(ordListVector);
        for (int i = 0; i < count; i++) {
            //�õ�OrderList
            OrderList orderList = (OrderList) getCertainOrdListByRow(i,
                ordListVector);
            //���ΰ�orderList�����ݷ���parm��
            orderList.getOrderListParm(parm);
        }
        return parm;
    }

    /**
     * ����OrderList������
     * @return int
     */
    public int getOrderListCount(Vector orderListVector) {
        if (orderListVector == null || orderListVector.size() == 0)
            return 0;
        return orderListVector.size();
    }

    //��ORDERת����ΪPHAORDERHISTORY
    TParm orderParmTransPhaHistoryParm(TParm orders) {
        //ת��֮���phaOrderHistory
        TParm phaHistoryParm = new TParm();
        //�õ���ǰʱ��
        Date nowTime = SystemTool.getInstance().getDate();
        //�õ�����
        int count = orders.getCount();
        //����ѭ����ÿһ��ORDER���PHA_ORDER_HISTORY
        for (int i = 0; i < count; i++) {

            phaHistoryParm.setData("CASE_NO", i,
                                   orders.getData("CASE_NO", i));
            phaHistoryParm.setData("RX_NO", i,
                                   orders.getData("RX_NO", i));
            phaHistoryParm.setData("SEQ_NO", i,
                                   orders.getData("SEQ_NO", i));
            phaHistoryParm.setData("PHA_RETN_DATE", i,
                                   StringTool.getString(nowTime,
                "yyyyMMddHHmmss"));
            phaHistoryParm.setData("PRESRT_NO", i,
                                   orders.getData("PRESRT_NO", i)); //5

            phaHistoryParm.setData("REGION_CODE", i,
                                   orders.getData("REGION_CODE", i));
            phaHistoryParm.setData("MR_NO", i,
                                   orders.getData("MR_NO", i));
            phaHistoryParm.setData("ADM_TYPE", i,
                                   orders.getData("ADM_TYPE", i));
            phaHistoryParm.setData("RX_TYPE", i,
                                   orders.getData("RX_TYPE", i));
            phaHistoryParm.setData("LINKMAIN_FLG", i,
                                   orders.getData("LINKMAIN_FLG", i)); //10

            phaHistoryParm.setData("LINK_NO", i, orders.getData("LINK_NO", i));
            phaHistoryParm.setData("ORDER_CODE", i,
                                   orders.getData("ORDER_CODE", i));
            phaHistoryParm.setData("ORDER_DESC", i,
                                   orders.getData("ORDER_DESC", i));
            phaHistoryParm.setData("GOODS_DESC", i,
                                   orders.getData("GOODS_DESC", i));
            phaHistoryParm.setData("SPECIFICATION", i,
                                   orders.getData("SPECIFICATION", i)); //15

            phaHistoryParm.setData("MEDI_QTY", i,
                                   orders.getData("MEDI_QTY", i));
            phaHistoryParm.setData("MEDI_UNIT", i,
                                   orders.getData("MEDI_UNIT", i));
            phaHistoryParm.setData("FREQ_CODE", i,
                                   orders.getData("FREQ_CODE", i));
            phaHistoryParm.setData("ROUTE_CODE", i,
                                   orders.getData("ROUTE_CODE", i));
            phaHistoryParm.setData("TAKE_DAYS", i,
                                   orders.getData("TAKE_DAYS", i)); //20

            phaHistoryParm.setData("DOSAGE_QTY", i,
                                   orders.getData("DOSAGE_QTY", i));
            phaHistoryParm.setData("DOSAGE_UNIT", i,
                                   orders.getData("DOSAGE_UNIT", i));
            phaHistoryParm.setData("DISPENSE_QTY", i,
                                   orders.getData("DISPENSE_QTY", i));
            phaHistoryParm.setData("DISPENSE_UNIT", i,
                                   orders.getData("DISPENSE_UNIT", i));
            phaHistoryParm.setData("GIVEBOX_FLG", i,
                                   orders.getData("GIVEBOX_FLG", i)); //25

            phaHistoryParm.setData("OWN_PRICE", i,
                                   orders.getData("OWN_PRICE", i));
            phaHistoryParm.setData("NHI_PRICE", i,
                                   orders.getData("NHI_PRICE", i));
            phaHistoryParm.setData("DISCOUNT_RATE", i,
                                   orders.getData("DISCOUNT_RATE", i));
            phaHistoryParm.setData("OWN_AMT", i,
                                   orders.getData("OWN_AMT", i));
            phaHistoryParm.setData("AR_AMT", i,
                                   orders.getData("AR_AMT", i)); //30

            phaHistoryParm.setData("DR_NOTE", i, orders.getData("DR_NOTE", i));
            phaHistoryParm.setData("NS_NOTE", i, orders.getData("NS_NOTE", i));
            phaHistoryParm.setData("DR_CODE", i, orders.getData("DR_CODE", i));
            phaHistoryParm.setData("ORDER_DATE", i,
                                   orders.getData("ORDER_DATE", i));
            phaHistoryParm.setData("DEPT_CODE", i,
                                   orders.getData("DEPT_CODE", i)); //35

            phaHistoryParm.setData("DC_DR_CODE", i,
                                   orders.getData("DC_DR_CODE", i));
            phaHistoryParm.setData("DC_ORDER_DATE", i,
                                   orders.getData("DC_ORDER_DATE", i));
            phaHistoryParm.setData("DC_DEPT_CODE", i,
                                   orders.getData("DC_DEPT_CODE", i));
            phaHistoryParm.setData("EXEC_DEPT_CODE", i,
                                   orders.getData("EXEC_DEPT_CODE", i));
            phaHistoryParm.setData("EXEC_DR_CODE", i,
                                   orders.getData("EXEC_DR_CODE", i)); //40

            phaHistoryParm.setData("SETMAIN_FLG", i,
                                   orders.getData("SETMAIN_FLG", i));
            phaHistoryParm.setData("ORDERSET_GROUP_NO", i,
                                   orders.getData("ORDERSET_GROUP_NO", i));
            phaHistoryParm.setData("ORDERSET_CODE", i,
                                   orders.getData("ORDERSET_CODE", i));
            phaHistoryParm.setData("HIDE_FLG", i,
                                   orders.getData("HIDE_FLG", i));
            phaHistoryParm.setData("URGENT_FLG", i,
                                   orders.getData("URGENT_FLG", i)); //45

            phaHistoryParm.setData("INSPAY_TYPE", i,
                                   orders.getData("INSPAY_TYPE", i));
            phaHistoryParm.setData("PHA_TYPE", i,
                                   orders.getData("PHA_TYPE", i));
            phaHistoryParm.setData("DOSE_TYPE", i,
                                   orders.getData("DOSE_TYPE", i));
            phaHistoryParm.setData("EXPENSIVE_FLG", i,
                                   orders.getData("EXPENSIVE_FLG", i));
            phaHistoryParm.setData("PRINTTYPEFLG_INFANT", i,
                                   orders.getData("PRINTTYPEFLG_INFANT", i)); //50

            phaHistoryParm.setData("CTRLDRUGCLASS_CODE", i,
                                   orders.getData("CTRLDRUGCLASS_CODE", i));
            phaHistoryParm.setData("PRESCRIPT_NO", i,
                                   orders.getData("PRESCRIPT_NO", i));
            phaHistoryParm.setData("ATC_FLG", i,
                                   orders.getData("ATC_FLG", i));
            phaHistoryParm.setData("SENDATC_DATE", i,
                                   orders.getData("SENDATC_DATE", i));
            phaHistoryParm.setData("RECEIPT_NO", i,
                                   orders.getData("RECEIPT_NO", i)); //55

            phaHistoryParm.setData("BILL_FLG", i,
                                   orders.getData("BILL_FLG", i));
            phaHistoryParm.setData("BILL_DATE", i,
                                   orders.getData("BILL_DATE", i));
            phaHistoryParm.setData("BILL_USER", i,
                                   orders.getData("BILL_USER", i));
            phaHistoryParm.setData("PRINT_FLG", i,
                                   orders.getData("PRINT_FLG", i));
            phaHistoryParm.setData("REXP_CODE", i,
                                   orders.getData("REXP_CODE", i)); //60

            phaHistoryParm.setData("HEXP_CODE", i,
                                   orders.getData("HEXP_CODE", i));
            phaHistoryParm.setData("CONTRACT_CODE", i,
                                   orders.getData("CONTRACT_CODE", i));
            phaHistoryParm.setData("CTZ1_CODE", i,
                                   orders.getData("CTZ1_CODE", i));
            phaHistoryParm.setData("CTZ2_CODE", i,
                                   orders.getData("CTZ2_CODE", i));
            phaHistoryParm.setData("CTZ3_CODE", i,
                                   orders.getData("CTZ3_CODE", i)); //65
            //����Ҫ��ֵ����Ϊֵû�б��޸�
//            phaHistoryParm.setData("PHA_CHECK_CODE", i,
//                                   orders.getData("PHA_CHECK_CODE_OLD", i));
//            phaHistoryParm.setData("PHA_CHECK_DATE", i,
//                                   orders.getData("PHA_CHECK_DATE_OLD", i));
//            phaHistoryParm.setData("PHA_DOSAGE_CODE", i,
//                                   orders.getData("PHA_DOSAGE_CODE_OLD", i));
//            phaHistoryParm.setData("PHA_DOSAGE_DATE", i,
//                                   orders.getData("PHA_DOSAGE_DATE_OLD", i));
//            phaHistoryParm.setData("PHA_DISPENSE_CODE", i,
//                                   orders.getData("PHA_DISPENSE_CODE_OLD", i)); //70
//
//            phaHistoryParm.setData("PHA_DISPENSE_DATE", i,
//                                   orders.getData("PHA_DISPENSE_DATE_OLD", i));
            phaHistoryParm.setData("PHA_CHECK_CODE", i,
                                   orders.getData("PHA_CHECK_CODE", i));
            phaHistoryParm.setData("PHA_CHECK_DATE", i,
                                   orders.getData("PHA_CHECK_DATE", i));
            phaHistoryParm.setData("PHA_DOSAGE_CODE", i,
                                   orders.getData("PHA_DOSAGE_CODE", i));
            phaHistoryParm.setData("PHA_DOSAGE_DATE", i,
                                   orders.getData("PHA_DOSAGE_DATE", i));
            phaHistoryParm.setData("PHA_DISPENSE_CODE", i,
                                   orders.getData("PHA_DISPENSE_CODE", i)); //70

            phaHistoryParm.setData("PHA_DISPENSE_DATE", i,
                                   orders.getData("PHA_DISPENSE_DATE", i));
            phaHistoryParm.setData("PHA_RETN_CODE", i,
                                   Operator.getID());
            phaHistoryParm.setData("NS_EXEC_CODE", i,
                                   orders.getData("NS_EXEC_CODE", i));
            phaHistoryParm.setData("NS_EXEC_DATE", i,
                                   orders.getData("NS_EXEC_DATE", i));
            phaHistoryParm.setData("NS_EXEC_DEPT", i,
                                   orders.getData("NS_EXEC_DEPT", i)); //75

            phaHistoryParm.setData("DCTAGENT_CODE", i,
                                   orders.getData("DCTAGENT_CODE", i));
            phaHistoryParm.setData("DCTEXCEP_CODE", i,
                                   orders.getData("DCTEXCEP_CODE", i));
            phaHistoryParm.setData("DCT_TAKE_QTY", i,
                                   orders.getData("DCT_TAKE_QTY", i));
            phaHistoryParm.setData("PACKAGE_TOT", i,
                                   orders.getData("PACKAGE_TOT", i));
            phaHistoryParm.setData("AGENCY_ORG_CODE", i,
                                   orders.getData("AGENCY_ORG_CODE", i));

            phaHistoryParm.setData("DCTAGENT_FLG", i,
                                   orders.getData("DCTAGENT_FLG", i));
            phaHistoryParm.setData("DECOCT_CODE", i,
                                   orders.getData("DECOCT_CODE", i));
            phaHistoryParm.setData("OPT_USER", i, Operator.getID());
            phaHistoryParm.setData("OPT_DATE", i, nowTime);
            phaHistoryParm.setData("OPT_TERM", i, Operator.getIP());
            phaHistoryParm.setData("COUNTER_NO", i,
                                               orders.getData("COUNTER_NO", i));
            phaHistoryParm.setData("PRINT_NO", i,
                                               orders.getData("PRINT_NO", i));
        }
        phaHistoryParm.setData("ACTION", "COUNT", count);

        return phaHistoryParm;
    }


}
