package jdo.ind;

import java.sql.Timestamp;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import java.util.List;

/**
 * <p>
 * Title: ҩ������ϸ��Tool
 * </p>
 *
 * <p>
 * Description: ҩ������ϸ��Tool
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
 * @author zhangy 2009.04.29
 * @version 1.0
 */

public class IndStockDTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static IndStockDTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return IndStockDTool
     */
    public static IndStockDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndStockDTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public IndStockDTool() {
        setModuleName("ind\\INDStockDModule.x");
        onInit();
    }

    /**
     * ����ҩ���ż�ҩƷ�����ѯҩ������
     *
     * @param parm
     * @return
     */
    public TParm onQueryStockQTY(TParm parm) {
        TParm result = this.query("queryStockQTY", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����ҩ���ż�ҩƷ�����ѯҩƷ��������źͿ�沢����Ч�ڽ�������
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryStockBatchAndQty(String org_code, String order_code,
                                         String sort) {
        TParm result = new TParm(TJDODBTool.getInstance().select(
            INDSQL.getIndStockBatchAndQty(org_code, order_code, sort)));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ����ҩ���ż�ҩƷ�����ѯҩƷ��������źͿ�沢����Ч�ڽ�������
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryStockBatchNo(String org_code, String order_code,
                                         String sort,String batch_no) {
        TParm result = new TParm(TJDODBTool.getInstance().select(
            INDSQL.getIndStockBatchNo(org_code, order_code, sort, batch_no)));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����ҩ���ż�ҩƷ�����ѯҩƷ��������źͿ�沢����Ч�ڽ�������(������ҩ)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryStockQty(String org_code, String order_code,
                                 String sort) {
        TParm result = new TParm(TJDODBTool.getInstance().select(INDSQL.
            getIndStockQty(org_code, order_code, sort)));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * ����ҩ���ż�ҩƷ�����Լ�������Ų�ѯ�����Ϣ(������ҩ)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryStockQty(String org_code, String order_code,int batchSeq,
                                 String sort) {
        TParm result = new TParm(TJDODBTool.getInstance().select(INDSQL.
            getIndStockQty(org_code, order_code,batchSeq, sort)));  
        System.out.println("indSql:::::::::::"+INDSQL.
            getIndStockQty(org_code, order_code,batchSeq, sort));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ����ҩ���ż�ҩƷ�����ѯҩƷ��������źͿ�沢����Ч�ڽ�������(������ҩ)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryStockQtyTwo(String org_code, String order_code,
                                 String sort) {
        TParm result = new TParm(TJDODBTool.getInstance().select(INDSQL.
            getIndStockQtyTwo(org_code, order_code, sort)));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }    


    /**
     * ����ҩ���ż�ҩƷ�����ѯҩƷ������������
     *
     * @param org_code
     * @param order_code
     * @return
     */
    public TParm onQueryStockMaxBatchSeq(String org_code, String order_code) {
        TParm result = new TParm(TJDODBTool.getInstance().select(
            INDSQL.getIndStockMaxBatchSeq(org_code, order_code)));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����ҩƷ��batchSeq orgCode orderCode ����ҩƷ��Ϣ
     * luhai add 2012-1-30
     * @param org_code
     * @param order_code
     * @param batchSeq
     * @return
     */
    public TParm onQueryStockWithBatchSeq(String org_code, String order_code,int batchSeq) {
    	TParm result = new TParm(TJDODBTool.getInstance().select(
    			INDSQL.getIndStockBatchSeq(org_code, order_code,batchSeq+"")));
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText()
    				+ result.getErrName());
    		return result;
    	}
    	return result;
    }


    /**
     * ����ҩ����,ҩƷ����,����,��Ч�ڲ�ѯҩƷ���������
     *
     * @param org_code
     * @param order_code
     * @param batch_no
     * @param valid_date
     * @return
     */
    public TParm onQueryStockBatchSeq(String org_code, String order_code,
                                      String batch_no, String valid_date) {
        TParm result = new TParm(TJDODBTool.getInstance().select(
            INDSQL.getIndStockBatchSeq(org_code, order_code, batch_no,
                                       valid_date)));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     *
     * ����ҩ����,ҩƷ����,����,��Ч��,���ռ۸��ѯҩƷ���������
     *
     *luhai 2012-01-10 add ҩƷ���������޸�
     * @param org_code
     * @param order_code
     * @param batch_no
     * @param valid_date
     * @return
     */
    public TParm onQueryStockBatchSeq(String org_code, String order_code,
    		String batch_no, String valid_date,String verifyInPrice) {
    	TParm result = new TParm(TJDODBTool.getInstance().select(
    			INDSQL.getIndStockBatchSeq(org_code, order_code, batch_no,
    					valid_date,verifyInPrice)));
//    	System.out.println("sql:"+INDSQL.getIndStockBatchSeq(org_code, order_code, batch_no,
//    					valid_date,verifyInPrice));
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText()
    				+ result.getErrName());
    		return result;
    	}
    	return result;
    }

    /**
     * <ҩ��>���¿����(�ۿ�)
     * @param org_code String
     * @param order_code String
     * @param batch_seq int
     * @param out_qty double
     * @param out_amt double
     * @param opt_user String
     * @param opt_date Timestamp
     * @param opt_term String
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyOut(String org_code, String order_code,
                                int batch_seq, double out_qty, double out_amt,
                                String opt_user,
                                Timestamp opt_date, String opt_term,
                                TConnection conn) {
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);
        parm.setData("ORDER_CODE", order_code);
        parm.setData("BATCH_SEQ", batch_seq);
        parm.setData("OUT_QTY", out_qty);
        parm.setData("OUT_AMT", out_amt);
        parm.setData("STOCK_QTY", out_qty);
        parm.setData("DOSEAGE_QTY", out_qty);
        parm.setData("DOSAGE_AMT", out_amt);
        parm.setData("OPT_USER", opt_user);
        parm.setData("OPT_DATE", opt_date);
        parm.setData("OPT_TERM", opt_term);
//        TParm result = new TParm(TJDODBTool.getInstance().update(
//            INDSQL.updateStockQtyOut(org_code, order_code, batch_seq,
//                                     out_qty, out_amt, opt_user, opt_date,
//                                     opt_term), conn));
        TParm result = this.update("updateStockQtyOut", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * <ҩ��>���¿����(ȡ����ҩ)
     * @param org_code String
     * @param order_code String
     * @param batch_seq int
     * @param out_qty double
     * @param out_amt double
     * @param opt_user String
     * @param opt_date Timestamp
     * @param opt_term String
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyIn(String org_code, String order_code,
                               int batch_seq, double in_qty, double in_amt,
                               String opt_user,
                               Timestamp opt_date, String opt_term,
                               TConnection conn) {
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);
        parm.setData("ORDER_CODE", order_code);
        parm.setData("BATCH_SEQ", batch_seq);
        parm.setData("IN_QTY", in_qty);
        parm.setData("IN_AMT", in_amt);
        parm.setData("STOCK_QTY", in_qty);
        parm.setData("DOSEAGE_QTY", in_qty);
        parm.setData("DOSAGE_AMT", in_amt);
        parm.setData("OPT_USER", opt_user);
        parm.setData("OPT_DATE", opt_date);
        parm.setData("OPT_TERM", opt_term);

        TParm result = this.update("updateStockQtyIn", parm, conn);

        //        TParm result = new TParm(TJDODBTool.getInstance().update(
        //            INDSQL.updateStockQtyIn(org_code, order_code, batch_seq,
        //                                    in_qty, in_amt, opt_user, opt_date,
        //                                    opt_term), conn));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * <ҩ��>���¿����(��ҩ���)
     * @param org_code String
     * @param order_code String
     * @param batch_seq int
     * @param in_qty double
     * @param in_amt double
     * @param opt_user String
     * @param opt_date Timestamp
     * @param opt_term String
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyRegIn(String org_code, String order_code,
                                  int batch_seq, double in_qty, double in_amt,
                                  String opt_user,
                                  Timestamp opt_date, String opt_term,
                                  TConnection conn) {
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);  
        parm.setData("ORDER_CODE", order_code);
        parm.setData("BATCH_SEQ", batch_seq);
        parm.setData("IN_QTY", in_qty);
        parm.setData("IN_AMT", in_amt);
        parm.setData("STOCK_QTY", in_qty);
        parm.setData("REGRESSDRUG_QTY", in_qty);  
        parm.setData("REGRESSDRUG_AMT", in_amt);
        parm.setData("OPT_USER", opt_user);  
        parm.setData("OPT_DATE", opt_date);  
        parm.setData("OPT_TERM", opt_term);

        TParm result = this.update("updateStockQtyRegIn", parm, conn);

//        TParm result = new TParm(TJDODBTool.getInstance().update(
//            INDSQL.updateStockQtyRegIn(org_code, order_code, batch_seq,
//                                       in_qty, in_amt, opt_user, opt_date,
//                                       opt_term), conn));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * <ҩ��>���¿����(ȡ����ҩ�ۿ�)
     * @param org_code String
     * @param order_code String
     * @param batch_seq int
     * @param out_qty double
     * @param out_amt double
     * @param opt_user String
     * @param opt_date Timestamp
     * @param opt_term String
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyRegOut(String org_code, String order_code,
                                   int batch_seq, double out_qty,
                                   double out_amt,
                                   String opt_user,
                                   Timestamp opt_date, String opt_term,
                                   TConnection conn) {
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);
        parm.setData("ORDER_CODE", order_code);
        parm.setData("BATCH_SEQ", batch_seq);
        parm.setData("OUT_QTY", out_qty);
        parm.setData("OUT_AMT", out_amt);
        parm.setData("STOCK_QTY", out_qty);
        parm.setData("REGRESSDRUG_QTY", out_qty);
        parm.setData("REGRESSDRUG_AMT", out_amt);
        parm.setData("OPT_USER", opt_user);
        parm.setData("OPT_DATE", opt_date);
        parm.setData("OPT_TERM", opt_term);

        TParm result = this.update("updateStockQtyRegOut", parm, conn);

//        TParm result = new TParm(TJDODBTool.getInstance().update(
//            INDSQL.updateStockQtyRegOut(org_code, order_code, batch_seq,
//                                        out_qty, out_amt, opt_user, opt_date,
//                                        opt_term), conn));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿����(���뵥����ۿ�)
     * @param request_type String
     * @param org_code String
     * @param order_code String
     * @param batch_seq int
     * @param out_qty double
     * @param out_amt double
     * @param opt_user String
     * @param opt_date Timestamp
     * @param opt_term String
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyRequestOut(String request_type, String org_code,
                                       String order_code,
                                       int batch_seq, double out_qty,
                                       double out_amt, String opt_user,
                                       Timestamp opt_date, String opt_term,
                                       TConnection conn) {
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);
        parm.setData("ORDER_CODE", order_code);
        parm.setData("BATCH_SEQ", batch_seq);
        parm.setData("OUT_QTY", out_qty);
        parm.setData("OUT_AMT", out_amt);
        parm.setData("STOCK_QTY", out_qty);
        parm.setData("STOCKOUT_QTY", out_qty);
        parm.setData("STOCKOUT_AMT", out_amt);
        parm.setData("OPT_USER", opt_user);
        parm.setData("OPT_DATE", opt_date);
        parm.setData("OPT_TERM", opt_term);
        //System.out.println("parm==="+parm);
        TParm result = new TParm();
        if ("DEP".equals(request_type) || "TEC".equals(request_type) ||
            "EXM".equals(request_type)) {
            result = this.onUpdateStockQtyDisOutReq(parm, conn);
        }
        else if ("GIF".equals(request_type)) {
            result = this.onUpdateStockQtyDisOutGif(parm, conn);
        }
        else if ("RET".equals(request_type)) {
            result = this.onUpdateStockQtyDisOutRet(parm, conn);
        }
        else if ("WAS".equals(request_type)) {
            result = this.onUpdateStockQtyDisOutWas(parm, conn);
        }
        else if ("THO".equals(request_type)) {
            result = this.onUpdateStockQtyDisOutTho(parm, conn);
        }
        else if ("COS".equals(request_type)) {
            result = this.onUpdateStockQtyDisOutCos(parm, conn);
        }

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿����(���뵥�������)
     * @param request_type String
     * @param org_code String
     * @param order_code String
     * @param batch_seq int
     * @param in_qty double
     * @param in_amt double
     * @param opt_user String
     * @param opt_date Timestamp
     * @param opt_term String
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyRequestIn(String request_type, String org_code,
                                      String order_code,
                                      int batch_seq, double in_qty,
                                      double in_amt, String opt_user,
                                      Timestamp opt_date, String opt_term,
                                      TConnection conn) {
        TParm result = new TParm();
        if ("".equals(request_type)) {
            result = this.onUpdateQtyRequestInReq(org_code, order_code,
                                                  batch_seq,
                                                  in_qty, in_amt, opt_user,
                                                  opt_date, opt_term, conn);
        }

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿����(�����ҵ--DEP,TEC)
     *
     * @param org_code
     * @param order_code
     * @param batch_seq
     * @param out_qty
     * @param out_amt
     * @param conn
     * @return
     */
    public TParm onUpdateQtyRequestInReq(String org_code, String order_code,
                                         int batch_seq, double in_qty,
                                         double in_amt, String opt_user,
                                         Timestamp opt_date, String opt_term,
                                         TConnection conn) {
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);
        parm.setData("ORDER_CODE", order_code);
        parm.setData("BATCH_SEQ", batch_seq);
        parm.setData("IN_QTY", in_qty);
        parm.setData("IN_AMT", in_amt);
        parm.setData("STOCK_QTY", in_qty);
        parm.setData("STOCKIN_QTY", in_qty);
        parm.setData("STOCKIN_AMT", in_amt);
        parm.setData("OPT_USER", opt_user);
        parm.setData("OPT_DATE", opt_date);
        parm.setData("OPT_TERM", opt_term);
        //parm.setData("RETAIL_PRICE", in_amt / in_qty);
        TParm result = this.update("updateStockQtyDisInReq", parm, conn);

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿����(�����ҵ--GIF)
     *
     * @param org_code
     * @param order_code
     * @param batch_seq
     * @param out_qty
     * @param out_amt
     * @param conn
     * @return
     */
    public TParm onUpdateQtyRequestInGif(String org_code, String order_code,
                                         int batch_seq, double in_qty,
                                         double in_amt, String opt_user,
                                         Timestamp opt_date, String opt_term,
                                         TConnection conn) {
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);
        parm.setData("ORDER_CODE", order_code);
        parm.setData("BATCH_SEQ", batch_seq);
        parm.setData("IN_QTY", in_qty);
        parm.setData("IN_AMT", in_amt);
        parm.setData("STOCK_QTY", in_qty);
        parm.setData("STOCKIN_QTY", in_qty);
        parm.setData("STOCKIN_AMT", in_amt);
        parm.setData("OPT_USER", opt_user);
        parm.setData("OPT_DATE", opt_date);
        parm.setData("OPT_TERM", opt_term);
        TParm result = this.update("updateStockQtyDisInGif", parm, conn);

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿����(�����ҵ--RET)
     *
     * @param org_code
     * @param order_code
     * @param batch_seq
     * @param out_qty
     * @param out_amt
     * @param conn
     * @return
     */
    public TParm onUpdateQtyRequestInRet(String org_code, String order_code,
                                         int batch_seq, double in_qty,
                                         double in_amt, String opt_user,
                                         Timestamp opt_date, String opt_term,
                                         TConnection conn) {
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);
        parm.setData("ORDER_CODE", order_code);
        parm.setData("BATCH_SEQ", batch_seq);
        parm.setData("IN_QTY", in_qty);
        parm.setData("IN_AMT", in_amt);
        parm.setData("STOCK_QTY", in_qty);
        parm.setData("STOCKIN_QTY", in_qty);
        parm.setData("STOCKIN_AMT", in_amt);
        parm.setData("OPT_USER", opt_user);
        parm.setData("OPT_DATE", opt_date);
        parm.setData("OPT_TERM", opt_term);
        TParm result = this.update("updateStockQtyDisInRet", parm, conn);

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿����(�����ҵ--THI)
     *
     * @param org_code
     * @param order_code
     * @param batch_seq
     * @param out_qty
     * @param out_amt
     * @param conn
     * @return
     */
    public TParm onUpdateQtyRequestInThi(String org_code, String order_code,
                                         int batch_seq, double in_qty,
                                         double in_amt, String opt_user,
                                         Timestamp opt_date, String opt_term,
                                         TConnection conn) {
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);
        parm.setData("ORDER_CODE", order_code);
        parm.setData("BATCH_SEQ", batch_seq);
        parm.setData("IN_QTY", in_qty);
        parm.setData("IN_AMT", in_amt);
        parm.setData("STOCK_QTY", in_qty);
        parm.setData("STOCKIN_QTY", in_qty);
        parm.setData("STOCKIN_AMT", in_amt);
        parm.setData("OPT_USER", opt_user);
        parm.setData("OPT_DATE", opt_date);
        parm.setData("OPT_TERM", opt_term);
        TParm result = this.update("updateStockQtyDisInThi", parm, conn);

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿����(�������)
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateQtyVer(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyVer", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿����(�˻�����)
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateQtyReg(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyReg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �������ϸ��
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createNewStockD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯָ������ҩƷ���ⲿ�ŵĿ��
     *
     * @param parm
     * @return
     */
    public TParm onQueryStockQTYByBatch(TParm parm) {
        TParm result = this.query("queryStockQTYByBatch", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿����(������ҵ)
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateStockQtyDisOut(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyDisOut", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿����(������ҵ--DEP,TEC,EXM)
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateStockQtyDisOutReq(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyDisOutReq", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿����(������ҵ--GIF)
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateStockQtyDisOutGif(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyDisOutGif", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿����(������ҵ--RET)
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateStockQtyDisOutRet(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyDisOutRet", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿����(������ҵ--WAS)
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateStockQtyDisOutWas(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyDisOutWas", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿����(������ҵ--THO)
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateStockQtyDisOutTho(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyDisOutTho", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿����(������ҵ--COS)
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateStockQtyDisOutCos(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyDisOutCos", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿����(�����ҵ)
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateStockQtyDisIn(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyDisIn", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ָ�����ź�Ч��(�����ҵδ���ָ��Ч������,ץȡ����������+1����)
     * @param request_type String
     * @param org_code String
     * @param order_code String
     * @param batch_seq int
     * @param valid_date String
     * @param batch_no String
     * @param dosage_qty double
     * @param retail_price double
     * @param opt_user String
     * @param opt_date Timestamp
     * @param opt_term String
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateStockByBatchVaildIn(String request_type,
                                             String org_code,
                                             String order_code, int batch_seq,
                                             String valid_date, String batch_no,
                                             double dosage_qty,
                                             double retail_price,
                                             String opt_user,
                                             Timestamp opt_date,
                                             String opt_term,
                                             List list, TConnection conn) {
    	//luhai modify 2012-01-13 modify �������ʱ����seq���������� begin
//        // ��ѯ�����ź�Ч�ڵ�ҩƷ�Ƿ����
//        TParm result = new TParm(TJDODBTool.getInstance().select(
//            INDSQL.getIndStockBatchSeq(org_code, order_code, batch_no,
//                                       valid_date)));
//        if (result.getErrCode() < 0) {
//            return result;
//        }
        TParm result = new TParm(TJDODBTool.getInstance().select(
            INDSQL.getIndStockBatchSeq(org_code, order_code,String.valueOf(batch_seq))));
        if (result.getErrCode() < 0) {
            return result;
        }
    	//luhai modify 2012-01-13 modify �������ʱ����seq���������� begin
        if (result.getCount("BATCH_SEQ") > 0) {
            // ����,����
            if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
                result = this.onUpdateQtyRequestInReq(org_code, order_code,
                    result.getInt("BATCH_SEQ", 0), dosage_qty,
                    StringTool.round(dosage_qty * retail_price, 2), opt_user,
                    opt_date, opt_term, conn);
            }
            else if ("GIF".equals(request_type)) {
                result = this.onUpdateQtyRequestInGif(org_code, order_code,
                    result.getInt("BATCH_SEQ", 0), dosage_qty,
                    StringTool.round(dosage_qty * retail_price, 2), opt_user,
                    opt_date, opt_term, conn);
            }
            else if ("RET".equals(request_type)) {
                result = this.onUpdateQtyRequestInRet(org_code, order_code,
                    result.getInt("BATCH_SEQ", 0), dosage_qty,
                    StringTool.round(dosage_qty * retail_price, 2), opt_user,
                    opt_date, opt_term, conn);
            }
            else if ("THI".equals(request_type)) {
                result = this.onUpdateQtyRequestInThi(org_code, order_code,
                    result.getInt("BATCH_SEQ", 0), dosage_qty,
                    StringTool.round(dosage_qty * retail_price, 2), opt_user,
                    opt_date, opt_term, conn);
            }

            if (result.getErrCode() < 0) {
                return result;
            }
        }
        else {
        	//luhai modify 2012-05-03 ������batchSeq ������������batchSeq��ҩƷ begin 
//            // ������,����������+1����
//            int count = 0;
//            for (int i = 0; i < list.size(); i++) {
//                if (order_code.equals(list.get(i))) {
//                    count++;
//                }
//            }
//            batch_seq = count;
            // ������,����������+1
//        	TParm parmSeq = new TParm(TJDODBTool.getInstance().select(
//        			INDSQL.getIndStockMaxBatchSeq(org_code, order_code)));
//        	String material_loc_code = "";
//        	if (parmSeq.getCount("BATCH_SEQ") > 0) {
//        		batch_seq = parmSeq.getInt("BATCH_SEQ", 0) + count;
//        		material_loc_code = parmSeq.getValue("MATERIAL_LOC_CODE", 0);
//        	}
//            batch_seq = result.getInt("BATCH_SEQ", 0);
        	TParm parmSeq = new TParm(TJDODBTool.getInstance().select(
        			INDSQL.getIndStockMaxBatchSeq(org_code, order_code)));
        	String material_loc_code = "";
        	if (parmSeq.getCount("BATCH_SEQ") > 0) {
//        		batch_seq = parmSeq.getInt("BATCH_SEQ", 0) + count;
        		material_loc_code = parmSeq.getValue("MATERIAL_LOC_CODE", 0);
        	}
            //luhai modify 2012-05-03 ������batchSeq ������������batchSeq��ҩƷ end  
            String sql = "SELECT VERIFYIN_PRICE, REGION_CODE FROM IND_STOCK " +
                "WHERE ORDER_CODE = '" + order_code
                + "' AND BATCH_NO = '" + batch_no
                + "' AND VALID_DATE = TO_DATE('" + valid_date
                + "','yyyy-MM-dd') ORDER BY BATCH_SEQ ";
            TParm parmVerifyPrice = new TParm(TJDODBTool.getInstance().select(
                sql));
            double verifyin_price = parmVerifyPrice.getDouble("VERIFYIN_PRICE",
                0);
            String region_code = parmVerifyPrice.getValue("REGION_CODE", 0);

            TParm parm = new TParm();
            String[] key = {
                "ORG_CODE", "ORDER_CODE", "BATCH_SEQ", "BATCH_NO",
                "VALID_DATE", "REGION_CODE", "MATERIAL_LOC_CODE",
                "ACTIVE_FLG", "STOCK_FLG", "READJUSTP_FLG", "STOCK_QTY",
                "LAST_TOTSTOCK_QTY", "LAST_TOTSTOCK_AMT", "IN_QTY",
                "IN_AMT", "OUT_QTY", "OUT_AMT", "CHECKMODI_QTY",
                "CHECKMODI_AMT", "VERIFYIN_QTY", "VERIFYIN_AMT",
                "FAVOR_QTY", "REGRESSGOODS_QTY", "REGRESSGOODS_AMT",
                "DOSEAGE_QTY", "DOSAGE_AMT", "REGRESSDRUG_QTY",
                "REGRESSDRUG_AMT", "FREEZE_TOT", "PROFIT_LOSS_AMT",
                "VERIFYIN_PRICE", "STOCKIN_QTY", "STOCKIN_AMT",
                "STOCKOUT_QTY", "STOCKOUT_AMT", "OPT_USER", "OPT_DATE",
                "OPT_TERM", "REQUEST_IN_QTY", "REQUEST_IN_AMT",
                "REQUEST_OUT_QTY", "REQUEST_OUT_AMT",
                "GIF_IN_QTY", "GIF_IN_AMT", "GIF_OUT_QTY", "GIF_OUT_AMT",
                "RET_IN_QTY",
                "RET_IN_AMT", "RET_OUT_QTY", "RET_OUT_AMT", "WAS_OUT_QTY",
                "WAS_OUT_AMT",
                "THO_OUT_QTY", "THO_OUT_AMT", "THI_IN_QTY", "THI_IN_AMT",
                "COS_OUT_QTY", "COS_OUT_AMT" , "RETAIL_PRICE"};
            Timestamp date = StringTool.getTimestamp(valid_date, "yyyy-MM-dd");
            Object[] value = {
                org_code, order_code, batch_seq, batch_no,
                date, region_code, material_loc_code, "Y", "N", "N", dosage_qty, 0, 0,
                dosage_qty,
                StringTool.round(dosage_qty * retail_price, 2), 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, verifyin_price, dosage_qty,
                StringTool.round(dosage_qty * retail_price, 2), 0, 0,
                opt_user, opt_date, opt_term, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, retail_price};
            parm = this.setIndStockParmValue(parm, key, value);
            if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
                parm.setData("REQUEST_IN_QTY", dosage_qty);
                parm.setData("REQUEST_IN_AMT",
                             StringTool.round(dosage_qty * retail_price, 2));
            }
            else if ("GIF".equals(request_type)) {
                parm.setData("GIF_IN_QTY", dosage_qty);
                parm.setData("GIF_IN_AMT",
                             StringTool.round(dosage_qty * retail_price, 2));

            }
            else if ("RET".equals(request_type)) {
                parm.setData("RET_IN_QTY", dosage_qty);
                parm.setData("RET_IN_AMT",
                             StringTool.round(dosage_qty * retail_price, 2));
            }
            else if ("THI".equals(request_type)) {
                parm.setData("THI_IN_QTY", dosage_qty);
                parm.setData("THI_IN_AMT",
                             StringTool.round(dosage_qty * retail_price, 2));
            }

            result = this.onInsert(parm, conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    }

    /**
     * ���IND_STOCK����
     *
     * @param parm
     * @param key
     * @param value
     * @return
     */
    private TParm setIndStockParmValue(TParm parm, String[] key, Object[] value) {
        for (int i = 0; i < key.length; i++) {
            parm.setData(key[i], value[i]);
        }
        return parm;
    }

    /**
     * �̵����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyCheck(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyCheck", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �̵����������
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateUnLockQtyCheck(TParm parm, TConnection conn) {
        TParm result = this.update("updateUnLockQtyCheck", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ�տ�潻�׵�
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("getDDStock", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��������¼����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateOutQtyToZero(TParm parm, TConnection conn) {
        TParm result = this.update("updateOutQtyToZero", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����IND_STOCK�еĵ�������
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateProfitLossAmt(TParm parm, TConnection conn) {
        TParm result = this.update("updateProfitLossAmt", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ſ���ѯ(��ʾ���ź�Ч��)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryOrgStockQuery(TParm parm) {
        TParm result = this.query("getOrgStockQuery", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ���ſ���ѯ(��ʾ���ź�Ч��,�����龫)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm getOrgStockDrugQuery(TParm parm) {
        TParm result = this.query("getOrgStockDrugQuery", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    
    /**
     * ���ſ���ѯ(����ʾ���ź�Ч��)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryOrgStockQueryNotBatch(TParm parm) {
        TParm result = this.query("getOrgStockQueryNotBatch", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

        /**
		      * ���������ѯ
		      * @ parm
		      * @return TParm
		      */
		     public TParm getDrugMianStockQuery(TParm parm) {
		         TParm result = this.query("getDrugMianStockQuery", parm);
		         if (result.getErrCode() < 0) {
		             err("ERR:" + result.getErrCode() + result.getErrText()
		                 + result.getErrName());
		             return result;
		         }
		         return result;
     }

	/**
	 * ����ҩ���ż�ҩƷ�����ѯҩ������ ��ѯ������ֶο���� �������汾
	 * 
	 * @param parm
	 * @return =====pangben 2013-11-4
	 */
	public TParm onQueryLockQTY(TParm parm) {
		TParm result = this.query("onQueryLockQTY", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸�������ֶο��������
	 * 
	 * @return ===========pangben 2013-11-6
	 */
	public TParm updateIndStockLockQty(TParm parm, TConnection conn) {
		TParm result = this.update("updateIndStockLockQty", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
