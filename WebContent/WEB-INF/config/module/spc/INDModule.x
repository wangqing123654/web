  #
   # Title: ҩ�⹫�÷�����������
   #
   # Description:ҩ�⹫�÷�����������
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author fudw
Module.item=getStockQty,getIndStockQty,getContractPrice

  #
   #��ѯ���������ת����
   #
   #@author fudw
   #
getStockQty.Type=TSQL
getStockQty.SQL=SELECT STOCK_QTY,DOSAGE_QTY,PURCH_UNIT FROM PHA_TRANSUNIT 
getStockQty.item=ORDER_CODE
getStockQty.ORDER_CODE=ORDER_CODE=<ORDER_CODE>
getOrgCode.Debug=N

 #
   #�жϿ����
   #
   #@author fudw
   #
getIndStockQty.Type=TSQL
getIndStockQty.SQL=SELECT (LAST_TOTSTOCK_QTY + IN_QTY- OUT_QTY + CHECKMODI_QTY)&
                   AS INDSTOCKQTY,BATCH_SEQ,BATCH_NO,VALID_DATE,ORDER_CODE&
                   FROM IND_STOCK
getIndStockQty.item=ORG_CODE,ORDER_CODE,ACTIVE_FLG
getIndStockQty.ORG_CODE=ORG_CODE=<ORG_CODE>
getIndStockQty.ORDER_CODE=ORDER_CODE=<ORDER_CODE>
getIndStockQty.ACTIVE_FLG=ACTIVE_FLG='N'
getOrgCode.Debug=N

 #
   #���Ҷ����۸�
   #
   #@author fudw
   #
getContractPrice.Type=TSQL
getContractPrice.SQL=SELECT CONTRACT_PRICE&
                     FROM IND_AGENT 
getContractPrice.item=SUP_CODE,ORDER_CODE
getContractPrice.SUP_CODE=SUP_CODE=<SUP_CODE>
getContractPrice.ORDER_CODE=ORDER_CODE=<ORDER_CODE>
getContractPrice.Debug=N


