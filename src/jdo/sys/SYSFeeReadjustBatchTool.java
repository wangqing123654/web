package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.pha.PhaBaseTool;
import jdo.ind.INDSQL;
import com.dongyang.jdo.TJDODBTool;
import jdo.ind.IndStockDTool;
import java.sql.Timestamp;

/**
 * <p>
 * Title:���ۼƻ�����Tool
 * </p>
 *
 * <p>
 * Description:���ۼƻ�����Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.9.18
 * @version 1.0
 */
public class SYSFeeReadjustBatchTool
    extends TJDOTool {
    public SYSFeeReadjustBatchTool() {
        onInit();
    }

    /**
     * ʵ��
     */
    public static SYSFeeReadjustBatchTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return
     */
    public static SYSFeeReadjustBatchTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSFeeReadjustBatchTool();
        return instanceObject;
    }

    /**
     * ִ�е��ۼƻ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm onSYSFeeReadjustBatchAction(TParm parm, TConnection conn) {
        Timestamp date = SystemTool.getInstance().getDate();
        TParm result = new TParm();
        //System.out.println(parm);
        String[] order_code = parm.getValue("ORDER_CODE").split(";");
        String[] own_price = parm.getValue("OWN_PRICE").split(";");
        String[] own2_price = parm.getValue("OWN_PRICE2").split(";");
        String[] own3_price = parm.getValue("OWN_PRICE3").split(";");
        //String[] nhi_price = parm.getValue("NHI_PRICE").split(";");
        //String[] gov_price = parm.getValue("GOV_PRICE").split(";");
        String rpp_code = parm.getValue("RPP_CODE");

        for (int i = 0; i < order_code.length; i++) {
            // 1.����SYS_FEE����(��SYS_FEE_HISTORY��ȡ���������ݸ���SYS_FEE)
            TParm historyParm = new TParm(TJDODBTool.getInstance().select(
                SYSSQL.getSYSFeeHistory(order_code[i], true)));
            System.out.println("sql----6----"+SYSSQL.getSYSFeeHistory(order_code[i], true));
            System.out.println("historyParm----6----"+historyParm);
            //System.out.println("----6----");
            TParm newParm = new TParm();
            newParm = this.setParmData(newParm, historyParm);
            System.out.println("newParm----6----"+newParm);
            newParm.setData("OWN_PRICE", own_price[i]);
            newParm.setData("OWN_PRICE2", own2_price[i]);
            newParm.setData("OWN_PRICE3", own3_price[i]);
            String order_cat1_code = newParm.getValue("ORDER_CAT1_CODE");
            TParm cat1Parm = new TParm(TJDODBTool.getInstance().select(SYSSQL.
                getSysOrderCat1(order_cat1_code)));
            //System.out.println("----7----");
            //System.out.println("CAT1_TYPE---"+cat1Parm);
            newParm.setData("CAT1_TYPE", cat1Parm.getValue("CAT1_TYPE", 0));

            // 1.1-��ѯҽ��
            result = SYSFeeTool.getInstance().getFeeOldPrice(order_code[i]);
            //System.out.println("----8----");
            //System.out.println("ORDER_CODE---"+result);
            double old_own_price = 0;
            if (result.getCount() > 0) {
                old_own_price = result.getDouble("OWN_PRICE", 0);
                // 1.2-����ҽ��
                result = SYSFeeTool.getInstance().onUpdate(newParm, conn);
                if (result.getErrCode() < 0) {
                    return result;
                }
                //System.out.println("----9-1----");
            }
            else {
                old_own_price = Double.parseDouble(own_price[i]);
                // 1.3-����ҽ��
                System.out.println("11111uuuuuuuuparmu  newParm newParm is ::"+newParm);
                result = SYSFeeTool.getInstance().onInsert(newParm, conn);
                if (result.getErrCode() < 0) {
                    return result;
                }
                //System.out.println("----9-2----");
            }

            if ("PHA".equals(cat1Parm.getValue("CAT1_TYPE", 0))) {
                // 1.3-����PHA_BASE�е����ۼۺ�������
                TParm phaParm = new TParm();
                phaParm.setData("ORDER_CODE", order_code[i]);
                phaParm.setData("RETAIL_PRICE", Double.parseDouble(own_price[i]));
                phaParm.setData("TRADE_PRICE", Double.parseDouble(own_price[i]));
                result = PhaBaseTool.getInstance().onUpdateRetailPrice(phaParm,
                    conn);
                if (result.getErrCode() < 0) {
                    return result;
                }
                // 2.���е�������
                // 2.1-��ѯ��ҩƷ�Ŀ��
                TParm stockParm = new TParm(TJDODBTool.getInstance().select(
                    INDSQL.getINDStock(order_code[i], "")));
                //System.out.println("stockParm---"+stockParm);
                for (int j = 0; j < stockParm.getCount("ORDER_CODE"); j++) {
                    // 2.2-�������ۿ����������-SYS_INDRPPANDL(ҩƷ���ո��������)
                    TParm indrppParm = new TParm();
                    indrppParm.setData("READJUST_DATE",
                                       newParm.getValue("START_DATE"));
                    indrppParm.setData("ORG_CODE",
                                       stockParm.getValue("ORG_CODE", j));
                    indrppParm.setData("ORDER_CODE",
                                       stockParm.getValue("ORDER_CODE", j));
                    indrppParm.setData("BATCH_SEQ",
                                       stockParm.getInt("BATCH_SEQ", j));
                    indrppParm.setData("UNIT_CODE",
                                       newParm.getValue("UNIT_CODE"));
                    indrppParm.setData("STOCK_QTY",
                                       stockParm.getDouble("STOCK_QTY", j));
                    indrppParm.setData("OWN_PRICE_OLD", old_own_price);
                    indrppParm.setData("OWN_PRICE_NEW",
                                       Double.parseDouble(own_price[i]));
                    indrppParm.setData("OPT_USER", "IND");
                    indrppParm.setData("OPT_DATE", date);
                    indrppParm.setData("OPT_TERM", "192.168.1.100");
                    result = SYSIndRppAndlTool.getInstance().onInsert(
                        indrppParm, conn);
                    if (result.getErrCode() < 0) {
                        return result;
                    }

                    // 2.3-����IND_STOCK�еĵ�������, ��������û�е�������
//                    TParm orgParm = new TParm(TJDODBTool.getInstance().select(
//                        INDSQL.getINDORG(stockParm.getValue("ORG_CODE", j))));
//                    if (!"A".equals(orgParm.getValue("ORG_TYPE", 0))) {
                    TParm indstockProfit = new TParm();
                    indstockProfit.setData("ORG_CODE",
                                           stockParm.getValue("ORG_CODE", j));
                    indstockProfit.setData("ORDER_CODE",
                                           stockParm.getValue("ORDER_CODE",
                        j));
                    indstockProfit.setData("BATCH_SEQ",
                                           stockParm.getInt("BATCH_SEQ", j));
                    indstockProfit.setData("OWN_PRICE",
                                           Double.parseDouble(own_price[i]));
                    indstockProfit.setData("OPT_USER", "IND");
                    indstockProfit.setData("OPT_DATE", date);
                    indstockProfit.setData("OPT_TERM", "192.168.1.100");
                    //System.out.println("indstockProfit---" + indstockProfit);
                    result = IndStockDTool.getInstance().
                        onUpdateProfitLossAmt(
                            indstockProfit, conn);
                    if (result.getErrCode() < 0) {
                        return result;
                    }
  //                  }
                }
            }
            else {
                // 2.2-�������ۿ����������-SYS_INDRPPANDL(��ҩƷԤ��Ϊ0)
            }
            //System.out.println("----10----");
        }

        // 3.���µ��ۼƻ������е�ִ��չ������ʱ������״̬
        TParm rppParm = new TParm();
        rppParm.setData("RPP_CODE", rpp_code);
        rppParm.setData("READJUSTOP_DATE", date);
        rppParm.setData("RPP_STATUS", "2");
        result = SYSFeeReadjustMTool.getInstance().onUpdateReadjustStatus(
            rppParm,
            conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        //System.out.println("----11----");
        return result;
    }

    /**
     * ��PARM��ֵ
     * @param parm TParm
     * @param data TParm
     * @return TParm
     */
    public TParm setParmData(TParm parm, TParm data) {
        String[] arrays = {
            "ORDER_CODE", "START_DATE", "END_DATE", "ORDER_DESC", "ACTIVE_FLG", //1-5
            "LAST_FLG", "PY1", "PY2", "SEQ", "DESCRIPTION", //6-10
            "TRADE_ENG_DESC", "GOODS_DESC", "GOODS_PYCODE", "ALIAS_DESC",
            "ALIAS_PYCODE", //11-15
            "SPECIFICATION", "NHI_FEE_DESC", "HABITAT_TYPE", "MAN_CODE",
            "HYGIENE_TRADE_CODE", //16-20
            "ORDER_CAT1_CODE", "CHARGE_HOSP_CODE", "OWN_PRICE", "NHI_PRICE",
            "GOV_PRICE", //21-25
            "UNIT_CODE", "LET_KEYIN_FLG", "DISCOUNT_FLG", "EXPENSIVE_FLG",
            "OPD_FIT_FLG", //26-30
            "EMG_FIT_FLG", "IPD_FIT_FLG", "HRM_FIT_FLG", "DR_ORDER_FLG",
            "INTV_ORDER_FLG", //31-35
            "LCS_CLASS_CODE", "TRANS_OUT_FLG", "TRANS_HOSP_CODE",
            "USEDEPT_CODE", "EXEC_ORDER_FLG", //36-40
            "EXEC_DEPT_CODE", "INSPAY_TYPE", "ADDPAY_RATE", "ADDPAY_AMT",
            "NHI_CODE_O", //41-45
            "NHI_CODE_E", "NHI_CODE_I", "CTRL_FLG", "CLPGROUP_CODE",
            "ORDERSET_FLG", //46-50
            "INDV_FLG", "SUB_SYSTEM_CODE", "RPTTYPE_CODE", "DEV_CODE", //51-55
            "OPTITEM_CODE", "MR_CODE", "DEGREE_CODE", "CIS_FLG", "OPT_USER", //56-56
            "OPT_DATE", "OPT_TERM", "ATC_FLG", "OWN_PRICE2", "OWN_PRICE3","EXE_PLACE","TIME_LIMIT", //61-65yanjing 20140319
            //===========pangben modify 20110816 ���SYS_GRUG_CLASS��NOADDTION_FLG,SYS_PHA_CLASS
            "TUBE_TYPE", "IS_REMARK", "ACTION_CODE", "ATC_FLG_I", "CAT1_TYPE" , "REGION_CODE","SYS_GRUG_CLASS","NOADDTION_FLG","SYS_PHA_CLASS"}; //66-73
        for (int i = 0; i < arrays.length; i++) {
            parm.setData(arrays[i], data.getData(arrays[i], 0));
        }
        return parm;
    }

}
