package jdo.ind;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.dongyang.util.StringTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool; 
import java.sql.Timestamp;
import com.dongyang.db.TConnection;

/**
 * <p>Title: �����ս����</p>
 *
 * <p>Description: �����ս����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2011.2.18
 * @version 1.0
 */
public class IndReStockBatchTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static IndReStockBatchTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return IndReStockBatchTool
     */
    public static IndReStockBatchTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndReStockBatchTool();
        return instanceObject;
    }

    /**
     * �ս����
     * @param org_code String
     * @param date String
     * @return boolean
     */
    public boolean onReStockBatch(String org_code, String date) {
//    	 System.out.println("����ִ�п�ʼ32332=================");
        // 1.ɾ�������IND_DDSTOCK����
        boolean flg = deleteIndDDstock(org_code, date);
        if (!flg) {
            return false;
        }
//        System.out.println("����ִ�п�ʼbbb=================");
        // 2.ȡ�õ�ǰIND_STOCK����
        //��Ϊ�����ddstockΪ�ã�
        TParm ind_stock = getIndStock(org_code);     
        if (ind_stock == null) {
            return false;
        }
//        System.out.println("����ִ�п�ʼaaa=================");
        // 3.����IND_DDSTOCK���ݼ�
        TDataStore ind_ddstock = getIndDDStock(org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
        // 4.��IND_STOCK��IND_DDSTOCK��ֵ����
        ind_ddstock = setDataStoreValue(ind_ddstock, ind_stock, date);
        if (ind_ddstock == null) {
            return false;  
        }  
//        System.out.println("����ִ�п�ʼ111=================");
        // 5.ȡ�����ڿ���������LAST_TOTSTOCK_QTY,LAST_TOTSTOCK_AMT
        ind_ddstock = setLastTotStock(ind_ddstock, org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
        // 6.ȡ�ñ����̵����������CHECKMODI_QTY,CHECKMODI_AMT
        ind_ddstock = setCheckModi(ind_ddstock, org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
        // 7.ȡ����������������VERIFYIN_QTY,VERIFYIN_AMT      
        ind_ddstock = setVerifyin(ind_ddstock, org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
//        System.out.println("����ִ�п�ʼ222=================");
        // 8.ȡ����������������REQUEST_IN_QTY,REQUEST_IN_AMT  
        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "DEP");
        if (ind_ddstock == null) {
            return false;
        }  
        // 9.ȡ�ñ�ҩ����������REQUEST_IN_QTY,REQUEST_IN_AMT
        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "TEC");
        if (ind_ddstock == null) {
            return false;
        }
        // 10.ȡ�ò�������������GIF_IN_QTY,GIF_IN_AMT
        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "GIF");
        if (ind_ddstock == null) {
            return false;
        }
        // 11.ȡ���˿�����������RET_IN_QTY,RET_IN_AMT
        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "RET");
        if (ind_ddstock == null) {
            return false;
        }
        // 12.ȡ����������������THI_IN_QTY,THI_IN_AMT
        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "THI");
        if (ind_ddstock == null) {
            return false;
        }
        // 8.ȡ���˻������������REGRESSGOODS_QTY,REGRESSGOODS_AMT
        ind_ddstock = setRegerssgoods(ind_ddstock, org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
        // 14.ȡ����������������REQUEST_OUT_QTY,REQUEST_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "DEP");
        if (ind_ddstock == null) {
            return false;
        }
        // 15.ȡ�ñ�ҩ�����������REQUEST_OUT_QTY,REQUEST_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "TEC");
        if (ind_ddstock == null) {
            return false;
        }
        // 16.ȡ�ò���Ʒѳ����������REQUEST_OUT_QTY,REQUEST_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "EXM");
        if (ind_ddstock == null) {
            return false;
        }
        // 17.ȡ�ò��������������GIF_OUT_QTY,GIF_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "GIF");
        if (ind_ddstock == null) {
            return false;
        }
        // 18.ȡ���˿�����������RET_OUT_QTY,RET_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "RET");
        if (ind_ddstock == null) {
            return false;
        }
        // 19.ȡ�ú�������������WAS_OUT_QTY,WAS_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "WAS");
        if (ind_ddstock == null) {
            return false;
        }
        // 20.ȡ�����������������THO_OUT_QTY,THO_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "THO");
        if (ind_ddstock == null) {
            return false;
        }
        // 21.ȡ�����Ĳĳ����������COS_OUT_QTY,COS_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "COS");
        if (ind_ddstock == null) {
            return false;
        }
        // 22.���㵱ǰ����������STOCK_QTY,STOCK_AMT
        ind_ddstock = setStock(ind_ddstock);
        if (ind_ddstock == null) {  
            return false;
        }
        // 23.ȡ����ҩ�������REGRESSDRUG_QTY,REGRESSDRUG_AMT
        ind_ddstock = setRegeessDrug(ind_ddstock, org_code, date);
        if (ind_ddstock == null) {
            return false;
        }  
//        System.out.println("����ִ�п�ʼ333=================");
        // 24.ȡ�÷�ҩ�������DOSAGE_QTY,DOSAGE_AMT
        ind_ddstock = setDosage(ind_ddstock, org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
        // 25.�������������PROFIT_LOSS_AMT  
        ind_ddstock = setProfitLoss(ind_ddstock);
        if (ind_ddstock == null) {
            return false;  
        }
//        System.out.println("����ִ�п�ʼ444=================");
        String[] sql = ind_ddstock.getUpdateSQL();  
        TParm parm = new TParm();  
        for (int i = 0; i < sql.length; i++) {
            parm = new TParm(TJDODBTool.getInstance().update(sql[i]));
            if (parm == null || parm.getErrCode() < 0) {  
            	System.out.println("IND_DDSTOCK���ݲ���ʧ�ܣ�����");
                return false;
            }
        }
        /*begin delete by guoyi 20120517 for ��ɾ�����Ϊ��Ĵ���ע�͵�������̵�ȹ��������ѯ������*/
        //������տ��Ϊ0���ݹ���
        //luhai add 2012-2-10 begin
        /*StringBuffer sqlbf = new StringBuffer();
        sqlbf.append(" DELETE  FROM IND_STOCK WHERE (LAST_TOTSTOCK_QTY+IN_QTY-OUT_QTY-CHECKMODI_QTY)<=0 ");
        parm = new TParm();
        parm = new TParm(TJDODBTool.getInstance().update(sqlbf.toString()));
        if (parm == null || parm.getErrCode() < 0) {
        	System.out.println("IND_STOCK��տ��Ϊ0����ʧ�ܣ�����");
            return false;
        }*/
        //������տ��Ϊ0���ݹ���end
        /*end delete by guoyi 20120517 for ��ɾ�����Ϊ��Ĵ���ע�͵�������̵�ȹ��������ѯ������*/
        return true;
    }

    /**
     * �ս����
     * @param org_code String
     * @param date String
     * @param conn TConnection
     * @return boolean
     */
    public boolean onReStockBatch(String org_code, String date,
                                  TConnection conn) {
        // 1.ɾ�������IND_DDSTOCK����
        boolean flg = deleteIndDDstock(org_code, date);
        if (!flg) {
            return false;
        }
        // 2.ȡ�õ�ǰIND_STOCK����
        TParm ind_stock = getIndStock(org_code);
        if (ind_stock == null) {
            return false;
        }
        // 3.����IND_DDSTOCK���ݼ�
        TDataStore ind_ddstock = getIndDDStock(org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
        // 4.��IND_STOCK��IND_DDSTOCK��ֵ����
        ind_ddstock = setDataStoreValue(ind_ddstock, ind_stock, date);
        if (ind_ddstock == null) {
            return false;  
        }
        // 5.ȡ�����ڿ���������LAST_TOTSTOCK_QTY,LAST_TOTSTOCK_AMT
        ind_ddstock = setLastTotStock(ind_ddstock, org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
//        // 6.ȡ�ñ����̵����������CHECKMODI_QTY,CHECKMODI_AMT
//        //del by lx 2012-06-26Ӧ�ò���Ҫ���·��������������Ѿ�д��ind_stock
//        ind_ddstock = setCheckModi(ind_ddstock, org_code, date);
//        if (ind_ddstock == null) {
//            return false;
//        }
//        
//        // 7.ȡ����������������VERIFYIN_QTY,VERIFYIN_AMT
//        //lxδ��
//        ind_ddstock = setVerifyin(ind_ddstock, org_code, date);
//        if (ind_ddstock == null) {
//            return false;
//        }
//        // 8.ȡ���˻������������REGRESSGOODS_QTY,REGRESSGOODS_AMT
//        ind_ddstock = setRegerssgoods(ind_ddstock, org_code, date);
//        if (ind_ddstock == null) {
//            return false;
//        }
//        // 9.ȡ����������������REQUEST_IN_QTY,REQUEST_IN_AMT
//        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "DEP");
//        if (ind_ddstock == null) {
//            return false;
//        }
//        // 10.ȡ�ñ�ҩ����������REQUEST_IN_QTY,REQUEST_IN_AMT
//        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "TEC");
//        if (ind_ddstock == null) {
//            return false;
//        }
//        // 11.ȡ�ò�������������GIF_IN_QTY,GIF_IN_AMT
//        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "GIF");
//        if (ind_ddstock == null) {
//            return false;
//        }
//        // 12.ȡ���˿�����������RET_IN_QTY,RET_IN_AMT
//        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "RET");
//        if (ind_ddstock == null) {
//            return false;
//        }
//        // 13.ȡ����������������THI_IN_QTY,THI_IN_AMT
//        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "THI");
//        if (ind_ddstock == null) {
//            return false;
//        }
//        // 14.ȡ����������������REQUEST_OUT_QTY,REQUEST_OUT_AMT
//        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "DEP");
//        if (ind_ddstock == null) {
//            return false;
//        }
//        // 15.ȡ�ñ�ҩ�����������REQUEST_OUT_QTY,REQUEST_OUT_AMT
//        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "TEC");
//        if (ind_ddstock == null) {
//            return false;
//        }
//        // 16.ȡ�ò���Ʒѳ����������REQUEST_OUT_QTY,REQUEST_OUT_AMT
//        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "EXM");
//        if (ind_ddstock == null) {
//            return false;
//        }
//        // 17.ȡ�ò��������������GIF_OUT_QTY,GIF_OUT_AMT
//        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "GIF");
//        if (ind_ddstock == null) {
//            return false;
//        }
//        // 18.ȡ���˿�����������RET_OUT_QTY,RET_OUT_AMT
//        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "RET");
//        if (ind_ddstock == null) {
//            return false;
//        }
//        // 19.ȡ�ú�������������WAS_OUT_QTY,WAS_OUT_AMT
//        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "WAS");
//        if (ind_ddstock == null) {
//            return false;
//        }
//        // 20.ȡ�����������������THO_OUT_QTY,THO_OUT_AMT
//        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "THO");
//        if (ind_ddstock == null) {
//            return false;
//        }
//        // 21.ȡ�����Ĳĳ����������COS_OUT_QTY,COS_OUT_AMT
//        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "COS");
//        if (ind_ddstock == null) {
//            return false;
//        }   
        // 22.���㵱ǰ����������STOCK_QTY,STOCK_AMT
        ind_ddstock = setStock(ind_ddstock);
        if (ind_ddstock == null) {
            return false;
        }
        // 23.ȡ�÷�ҩ�������DOSAGE_QTY,DOSAGE_AMT
        ind_ddstock = setDosage(ind_ddstock, org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
//        // 24.ȡ����ҩ�������REGRESSDRUG_QTY,REGRESSDRUG_AMT
//        ind_ddstock = setRegeessDrug(ind_ddstock, org_code, date);
//        if (ind_ddstock == null) {
//            return false;
//        }
//        // 25.�������������PROFIT_LOSS_AMT
//        ind_ddstock = setProfitLoss(ind_ddstock);
//        if (ind_ddstock == null) {
//            return false;
//        }

        String[] sql = ind_ddstock.getUpdateSQL();
        TParm parm = new TParm();
        for (int i = 0; i < sql.length; i++) {
            //System.out.println("sql=================[" + i + "]---" + sql[i]);
            parm = new TParm(TJDODBTool.getInstance().update(sql[i], conn));
            if (parm == null || parm.getErrCode() < 0) {
                System.out.println("IND_DDSTOCK���ݲ���ʧ�ܣ�����");
                return false;
            }
        }
        /*begin delete by guoyi 20120517 for ��ɾ�����Ϊ��Ĵ���ע�͵�������̵�ȹ��������ѯ������*/
        //������տ��Ϊ0���ݹ���
        //luhai add 2012-2-10 begin
        /*StringBuffer sqlbf = new StringBuffer();
        sqlbf.append(" DELETE  FROM IND_STOCK WHERE (LAST_TOTSTOCK_QTY+IN_QTY-OUT_QTY+CHECKMODI_QTY)<=0   ");
        sqlbf.append(" AND ORG_CODE IN ( SELECT ORG_CODE FROM IND_ORG WHERE ORG_TYPE <> 'A' )  ");
        parm = new TParm();
        parm = new TParm(TJDODBTool.getInstance().update(sqlbf.toString()));
        if (parm == null || parm.getErrCode() < 0) {
        	System.out.println("IND_STOCK��տ��Ϊ0����ʧ�ܣ�����");
            return false;
        }*/
        //������տ��Ϊ0���ݹ���end
        /*end delete by guoyi 20120517 for ��ɾ�����Ϊ��Ĵ���ע�͵�������̵�ȹ��������ѯ������*/
        //luhai add 2012-2-10 end
        return true;
    }


    /**
     * 1.ɾ�������IND_DDSTOCK����
     * @param org_code String
     * @param date String
     */
    private boolean deleteIndDDstock(String org_code, String tran_date) {
        String DELETE_IND_DDSTOCK_SQL =
            "DELETE FROM IND_DDSTOCK WHERE TRANDATE = '" + tran_date +
            "' AND ORG_CODE = '" + org_code + "'";
        //System.out.println("DELETE_IND_DDSTOCK_SQL---"+DELETE_IND_DDSTOCK_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().update(
            DELETE_IND_DDSTOCK_SQL));
        //System.out.println("parm---"+parm);
        if (parm == null || parm.getErrCode() < 0) {
            System.out.println("���ţ�"+org_code+", IND_DDSTOCK����ɾ��ʧ�ܣ�����");
            return false;
        }
        return true;
    }

    /**
     * 1.ɾ�������IND_DDSTOCK����
     * @param org_code String
     * @param tran_date String
     * @param conn TConnection
     * @return boolean
     */
    private boolean deleteIndDDstock(String org_code, String tran_date,
                                     TConnection conn) {
        String DELETE_IND_DDSTOCK_SQL =
            "DELETE FROM IND_DDSTOCK WHERE TRANDATE = '" + tran_date +
            "' AND ORG_CODE = '" + org_code + "'";
//        System.out.println("DELETE_IND_DDSTOCK_SQL---"+DELETE_IND_DDSTOCK_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().update(
            DELETE_IND_DDSTOCK_SQL, conn));
//        System.out.println("parm---"+parm);
        if (parm == null || parm.getErrCode() < 0) {
            System.out.println("���ţ�"+org_code+", IND_DDSTOCK����ɾ��ʧ�ܣ�����");
            return false;
        }
        return true;
    }


    /**
     * 2.ȡ�õ�ǰIND_STOCK����
     * @param org_code String
     */
    private TParm getIndStock(String org_code) {
    	
//    	/* Formatted on 2014/12/3 13:45:32 (QP5 v5.185.11230.41888) */
//    	  SELECT ORDER_CODE, VALID_DATE, BATCH_NO
//    	    FROM IND_DDSTOCK
//    	   WHERE ORG_CODE = '030701'
//    	GROUP BY ORDER_CODE, VALID_DATE, BATCH_NO   
//    	  HAVING COUNT (ORDER_CODE) > 1  
    	 
        String SELECT_IND_STOCK_SQL =
            "SELECT * FROM IND_STOCK WHERE ORG_CODE = '" + org_code +
            //�ܺ� �ȸ���valid_day ����
            "' ORDER BY ORDER_CODE, VALID_DATE, BATCH_SEQ";// lx 2012-06-26 ����ֻ��BATCH_SEQ
//        System.out.println("SELECT_IND_STOCK_SQL---"+SELECT_IND_STOCK_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_STOCK_SQL));
        if (parm == null || parm.getErrCode() < 0 ||
            parm.getCount("ORDER_CODE") <= 0) {
            System.out.println("���ţ�"+org_code+", IND_STOCK����ȡ��ʧ�ܣ�����");
            return null;
        }
        return parm;
    }

    /**
     * 3.����IND_DDSTOCK���ݼ�
     * @param org_code String
     * @param tran_date String
     * @return TDataStore
     */
    private TDataStore getIndDDStock(String org_code, String tran_date) {
        String SELECT_IND_DDSTOCK_SQL =
            "SELECT * FROM IND_DDSTOCK WHERE ORG_CODE = '" + org_code +  
            "' AND TRANDATE = '" + tran_date +
            "' ORDER BY ORDER_CODE, BATCH_SEQ";
        //System.out.println("SELECT_IND_DDSTOCK_SQL---"+SELECT_IND_DDSTOCK_SQL);
        TDataStore dateStore = new TDataStore();
        dateStore.setSQL(SELECT_IND_DDSTOCK_SQL);
        dateStore.retrieve();
        if (dateStore.rowCount() > 0) {
            System.out.println("���ţ�"+org_code+", IND_DDSTOCK����δ��ȫɾ��������");
            return null;
        }
        return dateStore;
    }

    /**
     * 4.��IND_STOCK��IND_DDSTOCK��ֵ����
     * @param dataStore TDataStore
     * @param parm TParm
     * @param trandate String
     * @return TDataStore
     */
    private TDataStore setDataStoreValue(TDataStore dataStore, TParm parm,
                                         String trandate) {
        String order_code = "";
        Timestamp date = StringTool.getTimestamp(new Date());
        //del by lx 2012-06-26 ��ο���û��
        System.out.println("count:"+parm.getCount("ORDER_CODE"));
        int batchSeq = 9999; 
        Map<String, Integer> map = new TreeMap<String, Integer>();
        for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
            order_code = parm.getValue("ORDER_CODE", i);
            String sql = "SELECT A.TRADE_PRICE, B.OWN_PRICE "  
                + " FROM PHA_BASE A, SYS_FEE_HISTORY B "
                + " WHERE A.ORDER_CODE = B.ORDER_CODE "  
                + " AND TO_DATE ('" + trandate +
                "235959', 'YYYYMMDDHH24MISS') "               
                + " BETWEEN TO_DATE (B.START_DATE, 'YYYYMMDDHH24MISS') "
                + " AND TO_DATE (B.END_DATE, 'YYYYMMDDHH24MISS') "
                + " AND A.ORDER_CODE = ";
            String order_by = "  ORDER BY B.END_DATE DESC ";
            sql = sql + "'" + order_code + "'" + order_by;

            TParm pha_base = new TParm(TJDODBTool.getInstance().select(sql)); 
            /* begin update by guoyi 20120523 for ����ҩƷ���ۺ��ж�PHA_BASE�е�TRADE_PRICE*/
//            if (pha_base == null || pha_base.getCount("TRADE_PRICE") <= 0) {
//            	System.out.println("�����ڼ���õ�ҩƷsql----" + sql);
//            	continue;
//            }
            if (pha_base == null || pha_base.getErrCode() < 0 ) {
            	System.out.println("�����ڼ���õ�ҩƷsql----" + sql);
            	continue;
            }
            /* end update by guoyi 20120523 for ����ҩƷ���ۺ��ж�PHA_BASE�е�TRADE_PRICE*/

            int row = dataStore.insertRow();
            // 1-5  
            dataStore.setItem(row, "TRANDATE", trandate);
            dataStore.setItem(row, "ORG_CODE", parm.getValue("ORG_CODE", i));
            dataStore.setItem(row, "ORDER_CODE", order_code);
            //fux modify      
                
            
            if (map.containsKey(order_code)) {
            	batchSeq = (Integer) map.get(order_code);
            	batchSeq =  batchSeq-1;  
				map.remove(order_code);     
				map.put(order_code,    
						batchSeq);
				dataStore.setItem(row, "BATCH_SEQ", batchSeq);  
               
            }    
            else {
            	batchSeq = 9999;     
				map.put(order_code,
						batchSeq);    
				dataStore.setItem(row, "BATCH_SEQ", batchSeq);
			}    
               
            dataStore.setItem(row, "BATCH_NO", parm.getValue("BATCH_NO", i));  
            // 6-10
            dataStore.setItem(row, "VALID_DATE",
                              parm.getTimestamp("VALID_DATE", i));
            dataStore.setItem(row, "REGION_CODE",
                              parm.getValue("REGION_CODE", i));
            dataStore.setItem(row, "STOCK_QTY", 0);
            dataStore.setItem(row, "STOCK_AMT", 0);
            dataStore.setItem(row, "LAST_TOTSTOCK_QTY", 0);     
            // 11-15
            dataStore.setItem(row, "LAST_TOTSTOCK_AMT", 0);
            dataStore.setItem(row, "IN_QTY", 0);
            dataStore.setItem(row, "IN_AMT", 0);
            dataStore.setItem(row, "OUT_QTY", 0);
            dataStore.setItem(row, "OUT_AMT", 0);
            // 16-20
            dataStore.setItem(row, "CHECKMODI_QTY", 0);
            dataStore.setItem(row, "CHECKMODI_AMT", 0);
            dataStore.setItem(row, "VERIFYIN_QTY", 0);
            dataStore.setItem(row, "VERIFYIN_AMT", 0);
            dataStore.setItem(row, "FAVOR_QTY", 0);
            // 21-25
            dataStore.setItem(row, "REGRESSGOODS_QTY", 0);
            dataStore.setItem(row, "REGRESSGOODS_AMT", 0);
            dataStore.setItem(row, "DOSAGE_QTY", 0);
            dataStore.setItem(row, "DOSAGE_AMT", 0);
            dataStore.setItem(row, "REGRESSDRUG_QTY", 0);
            // 26-30
            dataStore.setItem(row, "REGRESSDRUG_AMT", 0);
            dataStore.setItem(row, "PROFIT_LOSS_AMT", 0);
            dataStore.setItem(row, "VERIFYIN_PRICE",
                              parm.getDouble("VERIFYIN_PRICE", i));
            dataStore.setItem(row, "STOCK_PRICE",
                              parm.getDouble("VERIFYIN_PRICE", i));
            dataStore.setItem(row, "RETAIL_PRICE",
                              pha_base.getDouble("OWN_PRICE", 0));//Modified by lx 2012-06-26 INDSTock->RETAIL_PRICE ?????
            // 31-35
            dataStore.setItem(row, "TRADE_PRICE",
                              pha_base.getDouble("TRADE_PRICE", 0));//Modified by lx 2012-06-26 INDSTock->VERIFYIN_PRICE ????
            dataStore.setItem(row, "STOCKIN_QTY", 0);
            dataStore.setItem(row, "STOCKIN_AMT", 0);
            dataStore.setItem(row, "STOCKOUT_QTY", 0);
            dataStore.setItem(row, "STOCKOUT_AMT", 0);
            // 36-40
            dataStore.setItem(row, "OPT_USER", "IND_RE");
            dataStore.setItem(row, "OPT_DATE", date);
            dataStore.setItem(row, "OPT_TERM", "127.0.0.1");
            dataStore.setItem(row, "REQUEST_IN_QTY", 0);
            dataStore.setItem(row, "REQUEST_IN_AMT", 0);
            // 41-45
            dataStore.setItem(row, "REQUEST_OUT_QTY", 0);
            dataStore.setItem(row, "REQUEST_OUT_AMT", 0);
            dataStore.setItem(row, "GIF_IN_QTY", 0);
            dataStore.setItem(row, "GIF_IN_AMT", 0);
            dataStore.setItem(row, "GIF_OUT_QTY", 0);
            // 46-50
            dataStore.setItem(row, "GIF_OUT_AMT", 0);
            dataStore.setItem(row, "RET_IN_QTY", 0);
            dataStore.setItem(row, "RET_IN_AMT", 0);
            dataStore.setItem(row, "RET_OUT_QTY", 0);
            dataStore.setItem(row, "RET_OUT_AMT", 0); 
            // 51-55
            dataStore.setItem(row, "WAS_OUT_QTY", 0);
            dataStore.setItem(row, "WAS_OUT_AMT", 0);
            dataStore.setItem(row, "THO_OUT_QTY", 0);
            dataStore.setItem(row, "THO_OUT_AMT", 0);   
            dataStore.setItem(row, "THI_IN_QTY", 0);
            // 56-58
            dataStore.setItem(row, "THI_IN_AMT", 0);
            dataStore.setItem(row, "COS_OUT_QTY", 0);
            dataStore.setItem(row, "COS_OUT_AMT", 0);
        }
        return dataStore;
    }

    /**
     * 5.ȡ�����ڿ���������LAST_TOTSTOCK_QTY,LAST_TOTSTOCK_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     */
    private TDataStore setLastTotStock(TDataStore dataStore, String org_code,
                                       String date) {
        Timestamp datetime = StringTool.getTimestamp(date, "yyyyMMdd");
        Timestamp lastdate = StringTool.rollDate(datetime, -1); //modified by lx 2012-06-26 ���ǰһ��δ���ˣ�Ӧ��ȡ���һ�ι��˵�ʱ��;
        String last_date = StringTool.getString(lastdate, "yyyyMMdd");
        String SELECT_IND_DDSTOCK_SQL =
            "SELECT ORDER_CODE, BATCH_SEQ,BATCH_NO,VALID_DATE,SUP_CODE, VERIFYIN_PRICE,STOCK_QTY, STOCK_AMT " +
            "FROM IND_DDSTOCK WHERE ORG_CODE = '" +
            org_code + "' AND TRANDATE = '" + last_date +    
            "' ORDER BY ORDER_CODE, VALID_DATE,BATCH_SEQ";  
        //System.out.println("SELECT_IND_DDSTOCK_SQL---"+SELECT_IND_DDSTOCK_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_DDSTOCK_SQL)); 
        String order_code = "";  
        String  batch_no = "";           
        String valid_date = "";  
        double verifyinPrice = 0.00;
        int batch_seq = 0;
        //130  4ge1 
        for (int i = 0; i < dataStore.rowCount(); i++) {
            order_code = dataStore.getItemString(i, "ORDER_CODE");  
            batch_seq = dataStore.getItemInt(i, "BATCH_SEQ");
//            batch_no = dataStore.getItemString(i, "BATCH_NO");           
//            valid_date = StringTool.getString(dataStore.getItemTimestamp(i,
//            "VALID_DATE"), "yyyyMMdd"); 
//            verifyinPrice =  dataStore.getItemDouble(i, "VERIFYIN_PRICE");
//            System.out.println("order_code:"+order_code);  
//            System.out.println("batch_no:"+batch_no);      
//            System.out.println("valid_date:"+valid_date);  
//            System.out.println("verifyinPrice:"+verifyinPrice);         
            for (int j = 0; j < parm.getCount("ORDER_CODE"); j++) {  
                if (order_code.equals(parm.getValue("ORDER_CODE", j)) &&  
                    dataStore.getItemInt(i, "BATCH_SEQ") == parm.getInt("BATCH_SEQ", j)                 
//                    batch_no.equals(parm.getValue("BATCH_NO", j))&&  
//                    valid_date.equals(StringTool.getString(parm.    
//                            getTimestamp("VALID_DATE", j), "yyyyMMdd")
                   //&&verifyinPrice==parm.getDouble("VERIFYIN_PRICE", j)                     
                   )      
                     {
                    dataStore.setItem(i, "LAST_TOTSTOCK_QTY",         
                                      parm.getDouble("STOCK_QTY", j));  
                    dataStore.setItem(i, "LAST_TOTSTOCK_AMT",         
                                      parm.getDouble("STOCK_AMT", j));
                    continue;
                }    
            }

        }  
        return dataStore;
//        String SELECT_IND_DDSTOCK_SQL =
//            "SELECT ORDER_CODE, BATCH_SEQ, STOCK_QTY, STOCK_AMT " +
//            "FROM IND_DDSTOCK WHERE ORG_CODE = '" +
//            org_code + "' AND TRANDATE = '" + last_date +
//            "' ORDER BY ORDER_CODE, BATCH_SEQ";
//        //System.out.println("SELECT_IND_DDSTOCK_SQL---"+SELECT_IND_DDSTOCK_SQL);
//        TParm parm = new TParm(TJDODBTool.getInstance().select(
//            SELECT_IND_DDSTOCK_SQL));
//        String order_code = "";
//        int batch_seq = 0;
//        for (int i = 0; i < dataStore.rowCount(); i++) {
//            order_code = dataStore.getItemString(i, "ORDER_CODE");
//            batch_seq = dataStore.getItemInt(i, "BATCH_SEQ");
//            for (int j = 0; j < parm.getCount("ORDER_CODE"); j++) {
//                if (order_code.equals(parm.getValue("ORDER_CODE", j)) &&
//                    batch_seq == parm.getInt("BATCH_SEQ", j)) {
//                    dataStore.setItem(i, "LAST_TOTSTOCK_QTY",
//                                      parm.getDouble("STOCK_QTY", j));
//                    dataStore.setItem(i, "LAST_TOTSTOCK_AMT",
//                                      parm.getDouble("STOCK_AMT", j));
//                    continue;
//                }
//            }
//        }
//        return dataStore;
    }

    /**
     * 6.ȡ�ñ����̵����������CHECKMODI_QTY,CHECKMODI_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     * @return TDataStore
     */
    private TDataStore setCheckModi(TDataStore dataStore, String org_code,
                                    String date) {
        String start_date = date + "000000";  
        String end_date = date + "235959";
        //
        String SELECT_IND_QTYCHECK_SQL =
            "SELECT ORDER_CODE, BATCH_SEQ, RETAIL_PRICE * MODI_QTY AS MODI_AMT, " +    
            "MODI_QTY FROM IND_QTYCHECK WHERE ORG_CODE = '" +
            org_code +
            "' AND TO_DATE(FROZEN_DATE, 'YYYYMMDDHH24MISS') BETWEEN " +  
            "TO_DATE('" + start_date + "', 'YYYYMMDDHH24MISS') AND TO_DATE('" +
            end_date + "', 'YYYYMMDDHH24MISS')" +
            " ORDER BY ORDER_CODE, BATCH_SEQ";
        //System.out.println("SELECT_IND_QTYCHECK_SQL---"+SELECT_IND_QTYCHECK_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_QTYCHECK_SQL));
        String order_code = "";
        int batch_seq = 0;
        for (int i = 0; i < dataStore.rowCount(); i++) {  
            order_code = dataStore.getItemString(i, "ORDER_CODE");
            batch_seq = dataStore.getItemInt(i, "BATCH_SEQ");
            for (int j = 0; j < parm.getCount("ORDER_CODE"); j++) {
                if (order_code.equals(parm.getValue("ORDER_CODE", j)) &&
                    batch_seq == parm.getInt("BATCH_SEQ", j)) {
                    dataStore.setItem(i, "CHECKMODI_QTY",
                                      parm.getDouble("MODI_QTY", j));
                    dataStore.setItem(i, "CHECKMODI_AMT",
                                      parm.getDouble("MODI_AMT", j));
                    continue;
                }
            }
        }
        return dataStore;
    }

    /**
     * 7.ȡ����������������VERIFYIN_QTY,VERIFYIN_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     * @return TDataStore
     */
    private TDataStore setVerifyin(TDataStore dataStore, String org_code,
                                   String date) {
        String start_date = date + "000000";
        String end_date = date + "235959";
        String SELECT_IND_VERIFYIN_SQL =
            "SELECT A.ORDER_CODE, A.BATCH_NO, A.VALID_DATE, " +
            " A.VERIFYIN_QTY * C.DOSAGE_QTY * C.STOCK_QTY  AS QTY, " +
            " A.VERIFYIN_QTY * A.RETAIL_PRICE AS AMT " +
            " FROM IND_VERIFYIND A, IND_VERIFYINM B, PHA_TRANSUNIT C " +
            " WHERE A.VERIFYIN_NO = B.VERIFYIN_NO " +
            " AND A.ORDER_CODE = C.ORDER_CODE " +   
            " AND B.ORG_CODE = '" + org_code + "' " +
            " AND B.CHECK_DATE BETWEEN TO_DATE ('" + start_date +         
            "', 'YYYYMMDDHH24MISS') AND TO_DATE ('" + end_date +            
            "', 'YYYYMMDDHH24MISS') ORDER BY A.ORDER_CODE ";    
        System.out.println("SELECT_IND_VERIFYIN_SQL---"+SELECT_IND_VERIFYIN_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_VERIFYIN_SQL));  
        String order_code = "";
        String batch_no = "";  
        String valid_date = "";  
        for (int i = 0; i < dataStore.rowCount(); i++) {
            order_code = dataStore.getItemString(i, "ORDER_CODE");
            batch_no = dataStore.getItemString(i, "BATCH_NO");
            valid_date = StringTool.getString(dataStore.getItemTimestamp(i,
                "VALID_DATE"), "yyyyMMdd");
            for (int j = 0; j < parm.getCount("ORDER_CODE"); j++) {
                if (order_code.equals(parm.getValue("ORDER_CODE", j)) &&
                	9999==dataStore.getItemInt(i, "BATCH_SEQ")       
//                    batch_no.equals(parm.getValue("BATCH_NO", j)) &&
//                    valid_date.equals(StringTool.getString(parm.
//                    getTimestamp("VALID_DATE", j), "yyyyMMdd"))
                    ) {
                    dataStore.setItem(i, "VERIFYIN_QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, "VERIFYIN_QTY"));
                    dataStore.setItem(i, "VERIFYIN_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, "VERIFYIN_AMT"));
                    dataStore.setItem(i, "IN_QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, "IN_QTY"));
                    dataStore.setItem(i, "IN_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, "IN_AMT"));
                    dataStore.setItem(i, "STOCKIN_QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, "STOCKIN_QTY"));
                    dataStore.setItem(i, "STOCKIN_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, "STOCKIN_AMT"));
                    continue;
                }
            }
        }  
        return dataStore;
    }

    /**  
     * 8.ȡ���˻������������REGRESSGOODS_QTY,REGRESSGOODS_AMT  
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     * @return TDataStore
     */
    private TDataStore setRegerssgoods(TDataStore dataStore, String org_code,
                                       String date) {
        String start_date = date + "000000";
        String end_date = date + "235959";
        String SELECT_IND_REGRESSGOODS_SQL =
            "SELECT   A.ORDER_CODE, A.BATCH_NO, A.VALID_DATE, " +
            " A.QTY * C.DOSAGE_QTY * C.STOCK_QTY AS QTY, " +
            " A.QTY * A.RETAIL_PRICE AS AMT " +
            " FROM IND_REGRESSGOODSD A, IND_REGRESSGOODSM B, PHA_TRANSUNIT C " +
            " WHERE A.REGRESSGOODS_NO = B.REGRESSGOODS_NO " +
            " AND A.ORDER_CODE = C.ORDER_CODE " +
            " AND B.ORG_CODE = '" + org_code + "' " +
            " AND B.CHECK_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') " +
            " AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS') " +
            " ORDER BY A.ORDER_CODE ";
        //System.out.println("SELECT_IND_REGRESSGOODS_SQL---"+SELECT_IND_REGRESSGOODS_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_REGRESSGOODS_SQL));
        String order_code = "";
        String batch_no = "";
        String valid_date = "";
        for (int i = 0; i < dataStore.rowCount(); i++) {
            order_code = dataStore.getItemString(i, "ORDER_CODE");
            batch_no = dataStore.getItemString(i, "BATCH_NO");
            valid_date = StringTool.getString(dataStore.getItemTimestamp(i,
                "VALID_DATE"), "yyyyMMdd");
            for (int j = 0; j < parm.getCount("ORDER_CODE"); j++) {
                if (order_code.equals(parm.getValue("ORDER_CODE", j)) &&
                		9999==dataStore.getItemInt(i, "BATCH_SEQ")  
//                    batch_no.equals(parm.getValue("BATCH_NO", j)) &&
//                    valid_date.equals(StringTool.getString(parm.
//                    getTimestamp("VALID_DATE", j), "yyyyMMdd"))
                    ) {
                    dataStore.setItem(i, "REGRESSGOODS_QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i,
                        "REGRESSGOODS_QTY"));
                    dataStore.setItem(i, "REGRESSGOODS_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i,
                        "REGRESSGOODS_AMT"));
                    dataStore.setItem(i, "OUT_QTY",
                                      parm.getDouble("QTY", j) +   
                                      dataStore.getItemDouble(i, "OUT_QTY"));
                    dataStore.setItem(i, "OUT_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, "OUT_AMT"));
                    dataStore.setItem(i, "STOCKOUT_QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, "STOCKOUT_QTY"));
                    dataStore.setItem(i, "STOCKOUT_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, "STOCKOUT_AMT"));
                    continue;
                }
            }
        }                
        return dataStore;
    }

    /**
     * 8-12.ȡ������������IN_QTY,IN_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     * @param request_type String
     * @return TDataStore
     */
    private TDataStore setDispenseIn(TDataStore dataStore, String org_code,
                                     String date, String request_type) {
        String start_date = date + "000000";
        String end_date = date + "235959";
        String org_where = "";
        String qty_where = "";
        if ("DEP".equals(request_type) || "THI".equals(request_type)) {
            org_where = " AND B.APP_ORG_CODE = '" + org_code + "' ";
            qty_where = " A.QTY * C.DOSAGE_QTY * C.STOCK_QTY AS QTY, ";
        }
        else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
            org_where = " AND B.TO_ORG_CODE = '" + org_code + "' ";
            qty_where = " A.QTY * C.DOSAGE_QTY * C.STOCK_QTY AS QTY, ";
        }
        else if ("TEC".equals(request_type)) {
            org_where = " AND B.APP_ORG_CODE = '" + org_code + "' ";
            qty_where = " A.QTY , ";
        }
        else {
            org_where = " AND B.TO_ORG_CODE = '" + org_code + "' ";
            qty_where = " A.QTY , ";
        }
        String SELECT_IND_DISPENSE_IN_SQL =
            "SELECT   A.ORDER_CODE, A.BATCH_NO, A.VALID_DATE, " + qty_where +
            " A.QTY * A.RETAIL_PRICE AS AMT " +
            " FROM IND_DISPENSED A, IND_DISPENSEM B, PHA_TRANSUNIT C " +
            " WHERE A.DISPENSE_NO = B.DISPENSE_NO " +
            " AND A.ORDER_CODE = C.ORDER_CODE " + org_where +
            " AND B.WAREHOUSING_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') " +
            " AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS') " +
            " AND B.REQTYPE_CODE = '" + request_type + "'" +
            " AND B.UPDATE_FLG <> '2' " +  
            " ORDER BY A.ORDER_CODE, A.BATCH_SEQ ";
        //System.out.println("SELECT_IND_DISPENSE_IN_SQL---"+SELECT_IND_DISPENSE_IN_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_DISPENSE_IN_SQL));
        String order_code = "";
        String batch_no = "";   
        String valid_date = "";                                          
        String type = "";
        if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
            type = "REQUEST_IN_";
        }
        else if ("GIF".equals(request_type)) {
            type = "GIF_IN_";
        }
        else if ("RET".equals(request_type)) {
            type = "RET_IN_";
        }
        else {
            type = "THI_IN_";
        }
  
        for (int i = 0; i < dataStore.rowCount(); i++) {
            order_code = dataStore.getItemString(i, "ORDER_CODE");
            batch_no = dataStore.getItemString(i, "BATCH_NO");
            valid_date = StringTool.getString(dataStore.getItemTimestamp(i,
                "VALID_DATE"), "yyyyMMdd");
            for (int j = 0; j < parm.getCount("ORDER_CODE"); j++) {
                if (order_code.equals(parm.getValue("ORDER_CODE", j)) &&
                		9999==dataStore.getItemInt(i, "BATCH_SEQ")  
//                    batch_no.equals(parm.getValue("BATCH_NO", j)) &&
//                    valid_date.equals(StringTool.getString(parm.
//                    getTimestamp("VALID_DATE", j), "yyyyMMdd"))
                    ) {
                    dataStore.setItem(i, type + "QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, type + "QTY"));
                    dataStore.setItem(i, type + "AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, type + "AMT"));
                    dataStore.setItem(i, "IN_QTY",  
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, "IN_QTY"));  
                    dataStore.setItem(i, "IN_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, "IN_AMT"));
                    dataStore.setItem(i, "STOCKIN_QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, "STOCKIN_QTY"));
                    dataStore.setItem(i, "STOCKIN_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, "STOCKIN_AMT"));
                    continue;
                }
            }
        }
        return dataStore;
    }

    /**
     * 14-21.ȡ�ó����������OUT_QTY,OUT_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     * @param request_type String   
     * @return TDataStore
     */
    private TDataStore setDispenseOut(TDataStore dataStore, String org_code,
                                      String date, String request_type) {
        String start_date = date + "000000";
        String end_date = date + "235959";
        String org_where = "";
        String qty_where = "";
        if ("DEP".equals(request_type)) {
            org_where = " AND B.TO_ORG_CODE = '" + org_code + "' ";
            qty_where = " A.QTY * C.DOSAGE_QTY * C.STOCK_QTY AS QTY, ";
        }
        else if ("GIF".equals(request_type) || "RET".equals(request_type) ||
                 "WAS".equals(request_type) || "THO".equals(request_type)) {
            org_where = " AND B.APP_ORG_CODE = '" + org_code + "' ";
            qty_where = " A.QTY * C.DOSAGE_QTY * C.STOCK_QTY AS QTY, ";
        }
        else if ("TEC".equals(request_type) || "EXM".equals(request_type) ||
                 "COS".equals(request_type)) {
            org_where = " AND B.TO_ORG_CODE = '" + org_code + "' ";
            qty_where = " A.QTY , ";
        }
        
        String SELECT_IND_DISPENSE_OUT_SQL =
            "SELECT   A.ORDER_CODE, A.BATCH_NO, A.VALID_DATE, " + qty_where +
            " A.QTY * A.RETAIL_PRICE AS AMT " +
            " FROM IND_DISPENSED A, IND_DISPENSEM B, PHA_TRANSUNIT C " +
            " WHERE A.DISPENSE_NO = B.DISPENSE_NO " +
            " AND A.ORDER_CODE = C.ORDER_CODE " + org_where +
            " AND B.DISPENSE_DATE BETWEEN TO_DATE ('" + start_date +  
            "', 'YYYYMMDDHH24MISS') " +
            " AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS') " +
            " AND B.REQTYPE_CODE = '" + request_type + "'" +
            " AND B.UPDATE_FLG <> '2' " +
            " ORDER BY A.ORDER_CODE, A.BATCH_SEQ ";
        //System.out.println("SELECT_IND_DISPENSE_OUT_SQL---"+SELECT_IND_DISPENSE_OUT_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_DISPENSE_OUT_SQL));
        String order_code = "";
        String batch_no = "";
        String valid_date = "";
        String type = "";
        if ("DEP".equals(request_type) || "TEC".equals(request_type) ||
            "EXM".equals(request_type)) {
            type = "REQUEST_OUT_";
        }
        else if ("GIF".equals(request_type)) {
            type = "GIF_OUT_";
        }
        else if ("RET".equals(request_type)) {
            type = "RET_OUT_";
        }
        else if ("WAS".equals(request_type)) {
            type = "WAS_OUT_";
        }
        else if ("THO".equals(request_type)) {
            type = "THO_OUT_";
        }
        else if ("COS".equals(request_type)) {
            type = "COS_OUT_";
        }

        for (int i = 0; i < dataStore.rowCount(); i++) {
            order_code = dataStore.getItemString(i, "ORDER_CODE");
            batch_no = dataStore.getItemString(i, "BATCH_NO");
            valid_date = StringTool.getString(dataStore.getItemTimestamp(i,
                "VALID_DATE"), "yyyyMMdd");  
            for (int j = 0; j < parm.getCount("ORDER_CODE"); j++) {
                if (order_code.equals(parm.getValue("ORDER_CODE", j)) &&   
//                    batch_no.equals(parm.getValue("BATCH_NO", j)) &&
//                    valid_date.equals(StringTool.getString(parm.
//                    getTimestamp("VALID_DATE", j), "yyyyMMdd"))
                		9999==dataStore.getItemInt(i, "BATCH_SEQ")  
                    ) {
                    dataStore.setItem(i, type + "QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, type + "QTY"));
                    dataStore.setItem(i, type + "AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, type + "AMT"));
                    dataStore.setItem(i, "OUT_QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, "OUT_QTY"));
                    dataStore.setItem(i, "OUT_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, "OUT_AMT"));
                    dataStore.setItem(i, "STOCKOUT_QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, "STOCKOUT_QTY"));
                    dataStore.setItem(i, "STOCKOUT_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, "STOCKOUT_AMT"));
                    continue;
                }
            }
        }
        return dataStore;
    }

    /**
     * 22.���㵱ǰ����������STOCK_QTY,STOCK_AMT
     * @param dataStore TDataStore
     * @return TDataStore
     */
    private TDataStore setStock(TDataStore dataStore) {
        double qty = 0;
        double own_price = 0;
        for (int i = 0; i < dataStore.rowCount(); i++) {  
            qty = dataStore.getItemDouble(i, "LAST_TOTSTOCK_QTY")   
                + dataStore.getItemDouble(i, "IN_QTY")
                - dataStore.getItemDouble(i, "OUT_QTY")  
                + dataStore.getItemDouble(i, "CHECKMODI_QTY");
            own_price = dataStore.getItemDouble(i, "RETAIL_PRICE");
            dataStore.setItem(i, "STOCK_QTY", qty);
            dataStore.setItem(i, "STOCK_AMT", qty * own_price);
        }
        return dataStore;
    }

    /**  
     * 24.ȡ�÷�ҩ�������DOSAGE_QTY,DOSAGE_AMT
     * @param dataStore TDataStore  
     * @param org_code String
     * @param date String
     * @return TDataStore
     */
    private TDataStore setDosage(TDataStore dataStore, String org_code,
                                 String date) {
        String start_date = date + "000000";
        String end_date = date + "235959";
        String order_code = "";
        //����ʵ�ʵĽ��׵���д���﷢ҩ
//        // ���ﲿ�ַ�ҩ
//        String SELECT_IND_DOSAGE_OE_SQL =
//            "SELECT   A.ORDER_CODE, SUM (A.DOSAGE_QTY) AS QTY " +
//            " FROM OPD_ORDER A , PHA_BASE B " +
//            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
//            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
//            //" AND A.PHA_RETN_CODE IS NULL " +
//            " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('" + start_date +
//            "', 'YYYYMMDDHH24MISS') " +
//            " AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS') " +
//            " GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE ";
////        System.out.println("SELECT_IND_DOSAGE_OE_SQL---" +
////                           SELECT_IND_DOSAGE_OE_SQL);
//        TParm parmOE = new TParm(TJDODBTool.getInstance().select(
//            SELECT_IND_DOSAGE_OE_SQL));
        
        String SELECT_IND_DOSAGE_OE_SQL =  
            "SELECT   A.ORDER_CODE, SUM (A.DISPENSE_QTY1) AS QTY1, " +
            " SUM (A.DISPENSE_QTY2) AS QTY2,SUM (A.DISPENSE_QTY3) AS QTY3 "+
            " FROM OPD_ORDER A , PHA_BASE B " +
            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +   
//            " AND A.PHA_RETN_DATE IS NULL " +
            " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') " +
            " AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS') " +
            " GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE";
//        String SELECT_IND_DOSAGE_OE_SQL =
//            "SELECT   A.ORDER_CODE, SUM (A.DISPENSE_QTY1) AS QTY1, " +
//            " SUM (A.DISPENSE_QTY2) AS QTY2,SUM (A.DISPENSE_QTY3) AS QTY3, "+
//            " BATCH_SEQ1,BATCH_SEQ2,BATCH_SEQ3 "+
//            " FROM OPD_ORDER A , PHA_BASE B " +
//            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
//            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
////            " AND A.PHA_RETN_DATE IS NULL " +
//            " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('" + start_date +
//            "', 'YYYYMMDDHH24MISS') " +
//            " AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS') " +
//            " GROUP BY A.ORDER_CODE,batch_seq1, batch_seq2, batch_seq3  ORDER BY A.ORDER_CODE,batch_seq1, batch_seq2, batch_seq3 ";//,BATCH_SEQ1,BATCH_SEQ2,BATCH_SEQ3
        //System.out.println("���﷢ҩsql:"+SELECT_IND_DOSAGE_OE_SQL);
        TParm parmOE = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_DOSAGE_OE_SQL));
        //fux modify 20141203  
        parmOE=getParmFromDispenseDetail(parmOE);
      //luhai modify 2012-2-9 begin ����ʵ�ʵ�seq��д  end
        double stock_qty = 0;    
        double dosage_qty = 0;  
        double own_price = 0;     
        String batchSeq="9999";
        //fux 20141203 ȥ������������ ��Ϊ��vaild_day������
      //luhai modify 2012-2-9 begin ����ʵ�ʵ�seq��д  begin       
       for (int i = 0; i < parmOE.getCount("ORDER_CODE"); i++) {
            order_code = parmOE.getValue("ORDER_CODE", i);              
            dosage_qty = parmOE.getDouble("QTY", i);                 
            batchSeq=parmOE.getValue("BATCH_SEQ",i);              
            for (int j = 0; j < dataStore.rowCount(); j++) { 
//                if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))) {   
            if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))&&
            		"9999".equals(dataStore.getItemString(j, "BATCH_SEQ"))       
                       ) {      
        //luhai modify 2012-2-9 begin ����ʵ�ʵ�seq��д  end                 
                    stock_qty = dataStore.getItemDouble(j, "STOCK_QTY");  
                    own_price = dataStore.getItemDouble(j, "RETAIL_PRICE");
                    if (stock_qty >= dosage_qty) {  
                        dataStore.setItem(j, "STOCK_QTY", dataStore.       
                                          getItemDouble(j, "STOCK_QTY") -
                                          dosage_qty);
                        dataStore.setItem(j, "STOCK_AMT", dataStore.
                                          getItemDouble(j, "STOCK_AMT") - 
                                          dosage_qty * own_price);
                        dataStore.setItem(j, "OUT_QTY", dataStore.
                                          getItemDouble(j, "OUT_QTY") +
                                          dosage_qty);    
                        dataStore.setItem(j, "OUT_AMT", dataStore.
                                          getItemDouble(j, "OUT_AMT") +
                                          dosage_qty * own_price);
                        dataStore.setItem(j, "STOCKOUT_QTY", dataStore.     
                                          getItemDouble(j, "STOCKOUT_QTY") +
                                          dosage_qty);  
                        dataStore.setItem(j, "STOCKOUT_AMT", dataStore.
                                          getItemDouble(j, "STOCKOUT_AMT") +
                                          dosage_qty * own_price);
                        
                        
                        dataStore.setItem(j, "DOSAGE_QTY", dataStore.
                                          getItemDouble(j, "DOSAGE_QTY") +
                                          dosage_qty);
                        dataStore.setItem(j, "DOSAGE_AMT", dataStore.
                                          getItemDouble(j, "DOSAGE_AMT") +
                                          dosage_qty * own_price);
                        break;
                    }
                    else {
                        dataStore.setItem(j, "STOCK_QTY", dataStore.
                                          getItemDouble(j, "STOCK_QTY") -
                                          stock_qty);
                        dataStore.setItem(j, "STOCK_AMT", dataStore.
                                          getItemDouble(j, "STOCK_AMT") -
                                          stock_qty * own_price);
                        dataStore.setItem(j, "OUT_QTY", dataStore.
                                          getItemDouble(j, "OUT_QTY") +
                                          stock_qty);
                        dataStore.setItem(j, "OUT_AMT", dataStore.
                                          getItemDouble(j, "OUT_AMT") +
                                          stock_qty * own_price);
                        dataStore.setItem(j, "STOCKOUT_QTY", dataStore.
                                          getItemDouble(j, "STOCKOUT_QTY") +
                                          stock_qty);
                        dataStore.setItem(j, "STOCKOUT_AMT", dataStore.
                                          getItemDouble(j, "STOCKOUT_AMT") + 
                                          stock_qty * own_price);
                        dataStore.setItem(j, "DOSAGE_QTY", dataStore.  
                                          getItemDouble(j, "DOSAGE_QTY") +
                                          stock_qty);
                        dataStore.setItem(j, "DOSAGE_AMT", dataStore.
                                          getItemDouble(j, "DOSAGE_AMT") +
                                          stock_qty * own_price);
                        dosage_qty = dosage_qty - stock_qty;  
                    }
                    
                }  
             
            }
        }
  
        // סԺ��ҩ����
        //zhangyong20110314
        //luhai 2012-2-10 �������ʵ�ʷ�ҩ��ϸ��Ϣ��дddstock ��ҩ���� begin 
//        String SELECT_IND_DOSAGE_I_SQL =
//            "SELECT   A.ORDER_CODE, SUM (A.DOSAGE_QTY) AS QTY " +
//            " FROM ODI_DSPNM A, PHA_BASE B " +
//            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
//            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
//            " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('" + start_date + "', " +
//            " 'YYYYMMDDHH24MISS' ) AND TO_DATE ('" + end_date +
//            "', 'YYYYMMDDHH24MISS' )" +
//            " GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE";
//        //System.out.println("SELECT_IND_DOSAGE_I_SQL---"+SELECT_IND_DOSAGE_I_SQL);
//        TParm parmI = new TParm(TJDODBTool.getInstance().select(
//            SELECT_IND_DOSAGE_I_SQL));
        //luhai delete 2012-04-24 begin ����ibsordd ��ʽʵ�� begin
//        String SELECT_IND_DOSAGE_I_SQL =
//            "SELECT   A.ORDER_CODE, SUM (A.DISPENSE_QTY1) AS QTY1, " +
//            " SUM (A.DISPENSE_QTY2) AS QTY2,SUM (A.DISPENSE_QTY3) AS QTY3, "+
//            " BATCH_SEQ1,BATCH_SEQ2,BATCH_SEQ3 "+
//            " FROM ODI_DSPNM A, PHA_BASE B " +
//            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
//            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
//            " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('" + start_date + "', " +
//            " 'YYYYMMDDHH24MISS' ) AND TO_DATE ('" + end_date +
//            "', 'YYYYMMDDHH24MISS' )" +
//            " GROUP BY A.ORDER_CODE,BATCH_SEQ1,BATCH_SEQ2,BATCH_SEQ3 ORDER BY A.ORDER_CODE ";
      //luhai delete 2012-04-24 begin ����ibsordd ��ʽʵ�� end
//        System.out.println("סԺ��ҩsql:"+SELECT_IND_DOSAGE_I_SQL);
        
        
        //******************************************************
        //luhai 2012-04-24 ����ibs_ordd ��ʽʵ��סԺ��ҩ begin 
        //******************************************************
//        String SELECT_IND_DOSAGE_I_SQL =
//            "SELECT   A.ORDER_CODE, SUM (A.DISPENSE_QTY1) AS QTY1, " +
//            " SUM (A.DISPENSE_QTY2) AS QTY2,SUM (A.DISPENSE_QTY3) AS QTY3, "+
//            " BATCH_SEQ1,BATCH_SEQ2,BATCH_SEQ3 "+
//            " FROM ODI_DSPNM A, PHA_BASE B " +
//            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
//            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
//            " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('" + start_date + "', " +
//            " 'YYYYMMDDHH24MISS' ) AND TO_DATE ('" + end_date +
//            "', 'YYYYMMDDHH24MISS' )" +
//            " GROUP BY A.ORDER_CODE,BATCH_SEQ1,BATCH_SEQ2,BATCH_SEQ3 ORDER BY A.ORDER_CODE ";
        String SELECT_IND_DOSAGE_I_SQL =
            "SELECT   A.ORDER_CODE, SUM (DOSAGE_QTY) AS QTY1, " +
            " 0 AS QTY2,0 AS QTY3, "+
            " 0 AS BATCH_SEQ1,0 AS BATCH_SEQ2,0 AS BATCH_SEQ3 "+
            " FROM IBS_ORDD A, PHA_BASE B " +
            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
            " AND A.EXE_DEPT_CODE = '" + org_code + "' " +
            " AND A.BILL_DATE BETWEEN TO_DATE ('" + start_date + "', " +
            " 'YYYYMMDDHH24MISS' ) AND TO_DATE ('" + end_date +
            "', 'YYYYMMDDHH24MISS' )" +
            " AND DOSAGE_QTY > 0  "+
            " GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE ";//,BATCH_SEQ1,BATCH_SEQ2,BATCH_SEQ3
        //System.out.println("סԺ��ҩ��ѯsql:"+SELECT_IND_DOSAGE_I_SQL);
        //******************************************************
        //luhai 2012-04-24 ����ibs_ordd ��ʽʵ��סԺ��ҩ end 
        //******************************************************
        TParm parmI = new TParm(TJDODBTool.getInstance().select(
                SELECT_IND_DOSAGE_I_SQL));  
         parmI=getParmFromDispenseDetail(parmI);
//         System.out.println("�����סԺ��ҩparm:"+parmI);
          //luhai 2012-2-10 �������ʵ�ʷ�ҩ��ϸ��Ϣ��дddstock ��ҩ���� end
        stock_qty = 0;
        dosage_qty = 0;  
        own_price = 0;
        batchSeq="";
      //luhai 2012-2-10 �������ʵ�ʷ�ҩ��ϸ��Ϣ��дddstock ��ҩ���� begin
        for (int i = 0; i < parmI.getCount("ORDER_CODE"); i++) {
            order_code = parmI.getValue("ORDER_CODE", i);
            dosage_qty = parmI.getDouble("QTY", i);
            batchSeq=parmI.getValue("BATCH_SEQ",i);
            for (int j = 0; j < dataStore.rowCount(); j++) {
                if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))) {
//            	System.out.println("batchSeq;"+batchSeq+"||"+dataStore.getItemString(j, "BATCH_SEQ"));
//                if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))&&
//                        batchSeq.equals(dataStore.getItemString(j, "BATCH_SEQ"))
//                       ) {
      //luhai 2012-2-10 �������ʵ�ʷ�ҩ��ϸ��Ϣ��дddstock ��ҩ���� end
                    stock_qty = dataStore.getItemDouble(j, "STOCK_QTY");
                    own_price = dataStore.getItemDouble(j, "RETAIL_PRICE");
                    if (stock_qty >= dosage_qty) {
                        dataStore.setItem(j, "STOCK_QTY", dataStore.
                                          getItemDouble(j, "STOCK_QTY") -
                                          dosage_qty);
                        dataStore.setItem(j, "STOCK_AMT", dataStore.
                                          getItemDouble(j, "STOCK_AMT") -
                                          dosage_qty * own_price);
                        dataStore.setItem(j, "OUT_QTY", dataStore.
                                          getItemDouble(j, "OUT_QTY") +
                                          dosage_qty);
                        dataStore.setItem(j, "OUT_AMT", dataStore.
                                          getItemDouble(j, "OUT_AMT") +
                                          dosage_qty * own_price);
                        dataStore.setItem(j, "STOCKOUT_QTY", dataStore.
                                          getItemDouble(j, "STOCKOUT_QTY") +
                                          dosage_qty);
                        dataStore.setItem(j, "STOCKOUT_AMT", dataStore.
                                          getItemDouble(j, "STOCKOUT_AMT") +
                                          dosage_qty * own_price);
                        dataStore.setItem(j, "DOSAGE_QTY", dataStore.
                                          getItemDouble(j, "DOSAGE_QTY") +
                                          dosage_qty);
                        dataStore.setItem(j, "DOSAGE_AMT", dataStore.
                                          getItemDouble(j, "DOSAGE_AMT") +
                                          dosage_qty * own_price);
                        break;
                    }
                    else {
                        dataStore.setItem(j, "STOCK_QTY", dataStore.
                                          getItemDouble(j, "STOCK_QTY") -
                                          stock_qty);
                        dataStore.setItem(j, "STOCK_AMT", dataStore.
                                          getItemDouble(j, "STOCK_AMT") -
                                          stock_qty * own_price);
                        dataStore.setItem(j, "OUT_QTY", dataStore.
                                          getItemDouble(j, "OUT_QTY") +
                                          stock_qty);
                        dataStore.setItem(j, "OUT_AMT", dataStore.
                                          getItemDouble(j, "OUT_AMT") +
                                          stock_qty * own_price);
                        dataStore.setItem(j, "STOCKOUT_QTY", dataStore.
                                          getItemDouble(j, "STOCKOUT_QTY") +
                                          stock_qty);
                        dataStore.setItem(j, "STOCKOUT_AMT", dataStore.
                                          getItemDouble(j, "STOCKOUT_AMT") +
                                          stock_qty * own_price);
                        dataStore.setItem(j, "DOSAGE_QTY", dataStore.
                                          getItemDouble(j, "DOSAGE_QTY") +
                                          stock_qty);
                        dataStore.setItem(j, "DOSAGE_AMT", dataStore.
                                          getItemDouble(j, "DOSAGE_AMT") +
                                          stock_qty * own_price);
                        dosage_qty = dosage_qty - stock_qty;
                    }
                }
            }
        }

        return dataStore;
    }

    /**
     * 23.ȡ����ҩ�������REGRESSDRUG_QTY,REGRESSDRUG_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     * @return TDataStore
     */
    private TDataStore setRegeessDrug(TDataStore dataStore, String org_code,
                                      String date) {
        String start_date = date + "000000";
        String end_date = date + "235959";
        String order_code = "";

        // סԺ��ҩ����
//        String SELECT_IND_REGRESSDRUG_I_SQL = "SELECT A.ORDER_CODE, " +
//            "SUM (A.RTN_DOSAGE_QTY - A.CANCEL_DOSAGE_QTY) AS QTY " +
//            " FROM ODI_DSPNM A, PHA_BASE B " +
//            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
//            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
//            " AND A.DSPN_KIND = 'RT' " +
//            " AND A.PHA_RETN_DATE BETWEEN TO_DATE ('" + start_date +
//            "', 'YYYYMMDDHH24MISS') AND TO_DATE ('" + end_date +
//            "', 'YYYYMMDDHH24MISS') GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE";
        //zhangyong20110314
        //************************************************************************************************
        //luhai 2012-2-10 ������ҩ����ҩʱд�����ÿ����ҩ��������¼һ����ҩ��batchSeq���ʽ�ȡdispense_qty1
        //��ֹ A.CANCEL_DOSAGE_QTY ����׼ȷ��Ӧ��batchSeq �ʽ�������batchSeq1
        //************************************************************************************************
//        String SELECT_IND_REGRESSDRUG_I_SQL = "SELECT A.ORDER_CODE, " +
//            "SUM (A.RTN_DOSAGE_QTY - A.CANCEL_DOSAGE_QTY) AS QTY " +
//            " FROM ODI_DSPNM A, PHA_BASE B " +
//            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
//            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
//            " AND A.DSPN_KIND = 'RT' " +  
//            " AND A.PHA_RETN_DATE BETWEEN TO_DATE ('" + start_date +
//            "', 'YYYYMMDDHH24MISS') AND TO_DATE ('" + end_date +
//            "', 'YYYYMMDDHH24MISS') GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE";
        //luhai 2012-04-24 delete ����ibs_ordd ����begin 
//        String SELECT_IND_REGRESSDRUG_I_SQL = "SELECT A.ORDER_CODE, " +
//            "SUM (A.RTN_DOSAGE_QTY - A.CANCEL_DOSAGE_QTY) AS QTY,BATCH_SEQ1 AS BATCH_SEQ " +
//            " FROM ODI_DSPNM A, PHA_BASE B " +
//            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
//            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
//            " AND A.DSPN_KIND = 'RT' " +
//            " AND A.PHA_RETN_DATE BETWEEN TO_DATE ('" + start_date +
//            "', 'YYYYMMDDHH24MISS') AND TO_DATE ('" + end_date +
//            "', 'YYYYMMDDHH24MISS') GROUP BY A.ORDER_CODE,A.BATCH_SEQ1 ORDER BY A.ORDER_CODE";
      //luhai 2012-04-24 delete ����ibs_ordd ����end 
//        System.out.println("סԺ��ҩ��䣺"+SELECT_IND_REGRESSDRUG_I_SQL);
        //System.out.println("SELECT_IND_REGRESSDRUG_I_SQL---" + SELECT_IND_REGRESSDRUG_I_SQL);
        //**************************************************************************************
        //luhai modify end
        //**************************************************************************************
        
        
        //**************************************************************************************
        //luhai modify 2012-04-24 begin ����ibs_ordd ��ѯ  
        //**************************************************************************************
//        String SELECT_IND_REGRESSDRUG_I_SQL = "SELECT A.ORDER_CODE, " +
//            "SUM (A.RTN_DOSAGE_QTY - A.CANCEL_DOSAGE_QTY) AS QTY,BATCH_SEQ1 AS BATCH_SEQ " +
//            " FROM ODI_DSPNM A, PHA_BASE B " +
//            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
//            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
//            " AND A.DSPN_KIND = 'RT' " +
//            " AND A.PHA_RETN_DATE BETWEEN TO_DATE ('" + start_date +
//            "', 'YYYYMMDDHH24MISS') AND TO_DATE ('" + end_date +
//            "', 'YYYYMMDDHH24MISS') GROUP BY A.ORDER_CODE,A.BATCH_SEQ1 ORDER BY A.ORDER_CODE";
        String SELECT_IND_REGRESSDRUG_I_SQL = "SELECT A.ORDER_CODE, " +
            " ABS(SUM (DOSAGE_QTY)) AS QTY  " +
            " FROM IBS_ORDD A, PHA_BASE B " +
            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
            " AND A.EXE_DEPT_CODE = '" + org_code + "' " +
            " AND ORDER_NO IS NOT NULL AND DOSAGE_QTY < 0  " +
            " AND A.BILL_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') AND TO_DATE ('" + end_date +
            "', 'YYYYMMDDHH24MISS') GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE";
        //System.out.println("סԺ��ҩsql:"+SELECT_IND_REGRESSDRUG_I_SQL);
        //**************************************************************************************
        //luhai modify 2012-04-24 end ����ibs_ordd ��ѯ 
        //**************************************************************************************
        TParm parmI = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_REGRESSDRUG_I_SQL));

        double ret_qty = 0;
        double own_price = 0;
        String batchSeq="";
        for (int i = 0; i < parmI.getCount("ORDER_CODE"); i++) {
            order_code = parmI.getValue("ORDER_CODE", i);
            ret_qty = parmI.getDouble("QTY", i);
            batchSeq = parmI.getValue("BATCH_SEQ", i);//luhai 2012-2-10 add
            for (int j = dataStore.rowCount() - 1; j >= 0; j--) {
            	//luhai modify ����seq���޶� begin
            	//delete 2012-04-24 ����ԭ�з�ʽ��ɾ��batchSeq ���ж�
//                if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))&&batchSeq.equals(dataStore.getItemString(j, "BATCH_SEQ"))) {
                	if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))) {
                //luhai modify ����seq���޶� end
                    own_price = dataStore.getItemDouble(j, "RETAIL_PRICE");
                    dataStore.setItem(j, "STOCK_QTY", dataStore.
                                      getItemDouble(j, "STOCK_QTY") +
                                      ret_qty);
                    dataStore.setItem(j, "STOCK_AMT", dataStore.
                                      getItemDouble(j, "STOCK_AMT") +
                                      ret_qty * own_price);
                    dataStore.setItem(j, "IN_QTY", dataStore.
                                      getItemDouble(j, "IN_QTY") +
                                      ret_qty);
                    dataStore.setItem(j, "OUT_AMT", dataStore.
                                      getItemDouble(j, "IN_AMT") +
                                      ret_qty * own_price);
                    dataStore.setItem(j, "STOCKIN_QTY", dataStore.
                                      getItemDouble(j, "STOCKIN_QTY") +
                                      ret_qty);
                    dataStore.setItem(j, "STOCKIN_AMT", dataStore.
                                      getItemDouble(j, "STOCKIN_AMT") +
                                      ret_qty * own_price);
                    dataStore.setItem(j, "REGRESSDRUG_QTY", dataStore.
                                      getItemDouble(j, "REGRESSDRUG_QTY") +
                                      ret_qty);
                    dataStore.setItem(j, "REGRESSDRUG_AMT", dataStore.
                                      getItemDouble(j, "REGRESSDRUG_AMT") +
                                      ret_qty * own_price);
                    break;
                }
            }
        }

        //������ҩ����
        //************************************************************************************************
        //luhai 2012-2-10 ������ҩ����ҩʱд�����ÿ����ҩ��������¼һ����ҩ��batchSeq���ʽ�ȡdispense_qty1
        //��ֹ A.CANCEL_DOSAGE_QTY ����׼ȷ��Ӧ��batchSeq �ʽ�������batchSeq1
        //************************************************************************************************
//        String SELECT_IND_REGRESSDRUG_O_SQL =
//            " SELECT A.ORDER_CODE, SUM (A.DOSAGE_QTY) AS QTY "
//            + " FROM PHA_ORDER_HISTORY A , OPD_ORDER B "
//            + " WHERE A.CASE_NO = B.CASE_NO "
//            + " AND A.RX_NO = B.RX_NO "
//            + " AND A.SEQ_NO = B.SEQ_NO "
//            + " AND A.EXEC_DEPT_CODE = '" + org_code + "' "
//            + " AND A.PHA_RETN_DATE >= '" + start_date + "' "
//            + " AND A.PHA_RETN_DATE <= '" + end_date + "' "
//            + " GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE";
        String SELECT_IND_REGRESSDRUG_O_SQL =
            " SELECT A.ORDER_CODE, SUM (B.DISPENSE_QTY1) AS QTY1,SUM (B.DISPENSE_QTY2) AS QTY2,SUM (B.DISPENSE_QTY3) AS QTY3,"
        	+" B.BATCH_SEQ1,B.BATCH_SEQ2,B.BATCH_SEQ3 "
            + " FROM PHA_ORDER_HISTORY A , OPD_ORDER B "
            + " WHERE A.CASE_NO = B.CASE_NO "
            + " AND A.RX_NO = B.RX_NO "
            + " AND A.SEQ_NO = B.SEQ_NO "
            + " AND A.EXEC_DEPT_CODE = '" + org_code + "' "  
            + " AND A.PHA_RETN_DATE >= '" + start_date + "' "
            + " AND A.PHA_RETN_DATE <= '" + end_date + "' "  
            + " GROUP BY A.ORDER_CODE,B.BATCH_SEQ1,B.BATCH_SEQ2,B.BATCH_SEQ3 ORDER BY A.ORDER_CODE  ";
        //************************************************************************************************
        //luhai 2012-2-10 ������ҩ����ҩʱд�����ÿ����ҩ��������¼һ����ҩ��batchSeq���ʽ�ȡdispense_qty1
        //��ֹ A.CANCEL_DOSAGE_QTY ����׼ȷ��Ӧ��batchSeq �ʽ�������batchSeq1 END
        //************************************************************************************************
//        System.out.println("������ҩ��䣺"+SELECT_IND_REGRESSDRUG_O_SQL);
        TParm parmO = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_REGRESSDRUG_O_SQL));
        parmO=getParmFromDispenseDetail(parmO);
        ret_qty = 0;
        own_price = 0;
        batchSeq="";
        for (int i = 0; i < parmO.getCount("ORDER_CODE"); i++) {
            order_code = parmO.getValue("ORDER_CODE", i);
            ret_qty = parmO.getDouble("QTY", i);
            batchSeq=parmO.getValue("BATCH_SEQ",i);//luhai add 2012-2-10
            for (int j = dataStore.rowCount() - 1; j >= 0; j--) {
            	//luhai modify ����seq���޶� begin
//                if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))) {
                if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))&&batchSeq.equals(dataStore.getItemString(j, "BATCH_SEQ"))) {
                	//luhai modify ����seq���޶� end
                    own_price = dataStore.getItemDouble(j, "RETAIL_PRICE");
                    dataStore.setItem(j, "STOCK_QTY", dataStore.
                                      getItemDouble(j, "STOCK_QTY") +
                                      ret_qty);
                    dataStore.setItem(j, "STOCK_AMT", dataStore.
                                      getItemDouble(j, "STOCK_AMT") +
                                      ret_qty * own_price);
                    dataStore.setItem(j, "IN_QTY", dataStore.
                                      getItemDouble(j, "IN_QTY") +
                                      ret_qty);
                    dataStore.setItem(j, "OUT_AMT", dataStore.
                                      getItemDouble(j, "IN_AMT") +
                                      ret_qty * own_price);
                    dataStore.setItem(j, "STOCKIN_QTY", dataStore.
                                      getItemDouble(j, "STOCKIN_QTY") +
                                      ret_qty);
                    dataStore.setItem(j, "STOCKIN_AMT", dataStore.
                                      getItemDouble(j, "STOCKIN_AMT") +
                                      ret_qty * own_price);
                    dataStore.setItem(j, "REGRESSDRUG_QTY", dataStore.
                                      getItemDouble(j, "REGRESSDRUG_QTY") +
                                      ret_qty);
                    dataStore.setItem(j, "REGRESSDRUG_AMT", dataStore.
                                      getItemDouble(j, "REGRESSDRUG_AMT") +
                                      ret_qty * own_price);
                    break;
                }
            }
        }

        return dataStore;
    }

    /**
     * 25.�������������PROFIT_LOSS_AMT
     * @param dataStore TDataStore
     * @return TDataStore
     */
    private TDataStore setProfitLoss(TDataStore dataStore) {
        double profot_loss_amt = 0;
        double stock_amt = 0;
        for (int i = 0; i < dataStore.rowCount(); i++) {
            stock_amt = dataStore.getItemDouble(i, "LAST_TOTSTOCK_AMT")
                + dataStore.getItemDouble(i, "IN_AMT")
                - dataStore.getItemDouble(i, "OUT_AMT")
                + dataStore.getItemDouble(i, "CHECKMODI_AMT");
            profot_loss_amt = dataStore.getItemDouble(i, "STOCK_AMT") -
                stock_amt;
            dataStore.setItem(i, "PROFIT_LOSS_AMT", profot_loss_amt);
        }

        return dataStore;
    }
    /**
     *
     * �Ӵ���batchSeq��Ϣ��parm�в��ҩƷ
     * @param detailParm
     * @return
     */
    private TParm getParmFromDispenseDetail(TParm detailParm){
    	TParm returnParm = new TParm();
    	for(int i = 0;i<detailParm.getCount("ORDER_CODE");i++){
    		//fux modify 20141203                                                   
    		//batchSeq 1  
    		returnParm.addData("ORDER_CODE", detailParm.getValue("ORDER_CODE",i));  
    		returnParm.addData("BATCH_SEQ", detailParm.getValue("BATCH_SEQ1",i));
    		returnParm.addData("QTY", detailParm.getValue("QTY1",i));
//    		//batchSeq 2
//    		returnParm.addData("ORDER_CODE", detailParm.getValue("ORDER_CODE",i));
//    		//returnParm.addData("QTY", detailParm.getValue("QTY2",i));
//    		returnParm.addData("BATCH_SEQ", detailParm.getValue("BATCH_SEQ2",i));
//    		//batchSeq 3
//    		returnParm.addData("ORDER_CODE", detailParm.getValue("ORDER_CODE",i));
//    		//returnParm.addData("QTY", detailParm.getValue("QTY3",i));
//    		returnParm.addData("BATCH_SEQ", detailParm.getValue("BATCH_SEQ3",i));
    	}
    	return returnParm;
    }
    
    /**
     * ���ݲ��ű����ѯ����������ս� ����
     * @param orgCode ���ű���
     * @author liyh
     * @date 20120629
     * @return str 
     */
    public  String getLastTranDateByOrgCode(String orgCode){
    	String resultStr = "-1";
    	String sql = " SELECT MAX(TRANDATE) AS TRANDATE FROM IND_DDSTOCK WHERE ORG_CODE='" + orgCode + "' ";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(null != parm && parm.getCount("TRANDATE") >0 ){
    		resultStr = parm.getValue("TRANDATE", 0);
    	}else{
    		resultStr = "-1";
    	}
    	return resultStr;
    }

}
