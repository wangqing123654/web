package com.javahis.manager;

import java.util.Vector;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.util.StringTool;
import jdo.spc.INDTool;

/**
 * <p>
 * Title: ҩ���������۲���
 * </p>
 *
 * <p>
 * Description: ҩ���������۲���
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
 * @author zhangy 2009.05.24 
 * @version 1.0
 */
public class IndRequestObserver
    extends TObserverAdapter {
	
	private String orgCode="";
	 
    public IndRequestObserver() {

    }

    // ���prefech��������
    TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");

    /**
     *
     * @param s
     *            ���ݵ�ORDER_CODE
     * @param colName
     *            Ҫ�������
     * @return String
     */
    public String getTableShowValue(String s, String colName) {
        if (dataStore == null)
            return s;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER
            : dataStore.PRIMARY;
        TParm parm = dataStore.getBuffer(bufferString);
        Vector vKey = (Vector) parm.getData("ORDER_CODE");
        Vector vDesc = (Vector) parm.getData(colName);
        int count = vKey.size();
        for (int i = 0; i < count; i++) {
            if (s.equals(vKey.get(i)))
                return "" + vDesc.get(i);
        }
        return s;
    }

    /**
     * �õ�����������
     *
     * @param parm
     *            TParm
     * @param row
     *            int
     * @param column
     *            String
     * @return Object
     */
    public Object getOtherColumnValue(TDS ds, TParm parm, int row,
                                      String column) {
        if ("ORDER".equals(column)) {
            String orderCode = parm.getValue("ORDER_CODE", row);
            String icd_name = getTableShowValue(orderCode, "ORDER_DESC");
            String goods_desc = getTableShowValue(orderCode, "GOODS_DESC");
            icd_name = INDTool.getInstance().getOrderDescAndGoodsDesc("",
                icd_name, goods_desc);
            if ("".equals(icd_name))
                return "";
            // �������һ��ֵ
            return icd_name;
        }
        // ���
        if ("SPECIFICATION".equals(column)) {
            String orderCode = parm.getValue("ORDER_CODE", row);
            String icd_name = getTableShowValue(orderCode, "SPECIFICATION");
            if ("".equals(icd_name))
                return "";
            // �������һ��ֵ
            return icd_name;
        }
        // �ɱ����
        if ("SUM_STOCK_PRICE".equals(column)) {
            double price = 0;
            double qty = 0;
            double sum = 0;
            price = parm.getDouble("STOCK_PRICE", row);
            qty = parm.getDouble("QTY", row);
            sum = StringTool.round(price * qty, 2);
            return sum;
        }
        // ���۽��
        if ("SUM_RETAIL_PRICE".equals(column)) {
            double price = 0;
            double qty = 0;
            double sum = 0;
            price = parm.getDouble("RETAIL_PRICE", row);
            qty = parm.getDouble("QTY", row);
            sum = StringTool.round(price * qty, 2);
            return sum;
        }
        // �������
        if ("DIFF_SUM".equals(column)) {
            double price1 = parm.getDouble("STOCK_PRICE", row);
            double amount1 = parm.getDouble("QTY", row);
            double amt1 = StringTool.round(price1 * amount1, 2);
            double price2 = parm.getDouble("RETAIL_PRICE", row);
            double amt2 = StringTool.round(price2 * amount1, 2);
            return amt2 - amt1;
        }
        // ��ֹ���
        if ("END_FLG".equals(column)) {
            String flg = parm.getValue("UPDATE_FLG", row);
            if ("2".equals(flg)) {
                return "Y";
            }
            return "N"; 
        }
        //luhai 2012-2-13 ����ɱ���� begin
        if ("VERIFYIN_AMT".equals(column)) {
        	double verifyinPrice = parm.getDouble("VERIFYIN_PRICE",row);
        	double qty  = parm.getDouble("QTY",row);
        	return StringTool.round(verifyinPrice*qty, 2)+"";
        }
        if("TOT_QTY".equals(column)){
/* 			double qty = jdo.spc.INDTool.getInstance().getStockQTY(orgCode, parm.getValue("ORDER_CODE",row));
			if(qty <  0){  
				qty = 0 ;
			} 
        	return qty;  */
        	String qty = jdo.spc.INDTool.getInstance().getCurrentStockQty(orgCode, parm.getValue("ORDER_CODE",row));
        	return qty;
        }
        if("PACK_UNIT".equals(column)){
        	String packUnit="";  
			TParm result = jdo.spc.INDTool.getInstance().getPackUnit(parm.getValue("ORDER_CODE",row));
			if (result.getErrCode() < 0||result.getCount()<=0) {
				packUnit=""; 
			} 
			packUnit = result.getValue("PACK_UNIT", 0);
        	return packUnit;   
        }
        
        if("YS_PRICE".equals(column)){
        	double price=0;  
			TParm result = jdo.spc.INDTool.getInstance().getVerifyinPrice(orgCode, parm.getValue("ORDER_CODE",row));

			if (result.getErrCode() < 0||result.getCount()<=0) {
				price=0; 
			} else {
				price = result.getDouble("PRICE",0);	
			}			
			int qty = 1;
			result = jdo.spc.INDTool.getInstance().getDosageQty(orgCode, parm.getValue("ORDER_CODE",row));
			if (result.getErrCode() < 0||result.getCount()<=0) {
				qty=1; 
			} 	else {		
				qty = result.getInt("DOSAGE_QTY",0);	
			}
			price= price*qty;		
        	return price;      
        }
        //luhai 2012-2-13 ����ɱ���� begin
        return "";
    }

    /**
     * ��������������
     *
     * @param parm
     *            TParm
     * @param row
     *            int �����ö�������(table)
     * @param column
     *            String �����ö�������(table)
     * @param value
     *            Object
     * @return boolean ����Ŀǰ��ֵ
     */
    public boolean setOtherColumnValue(TDS ds, TParm parm, int row,
                                       String column, Object value) {
        if (column.equals("ORDER")) {
            ds.setItem(row, "ORDER_CODE", parm.getValue("ORDER", row));
            return true;
        }
        return true;
    }
    
	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
}
