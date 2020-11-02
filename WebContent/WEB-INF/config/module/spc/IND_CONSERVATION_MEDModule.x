  #
   # Title�����������
   #
   # Description:�����������
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author shibl 2011/08/18

Module.item=query;update;delete;insert

//��ѯ�����������
query.Type=TSQL
query.SQL= SELECT * FROM IND_CONSERVATION_MED  WHERE ORG_CODE=<ORG_CODE> AND BATCH_SEQ=<BATCH_SEQ> AND ORDER_CODE=<ORDER_CODE>
query.Debug=N

//��������������
insert.Type=TSQL
insert.SQL=INSERT INTO IND_CONSERVATION_MED &
            		  (ORG_CODE, BATCH_SEQ,ORDER_CODE,ORDER_DESC,&
             		   SPECIFICATION, UNIT_CODE, MAN_CODE, BATCH_NO, VALID_DATE, WAS_QTY, WAS_REASON,QUALIFIED_QTY,OPT_USER,OPT_DATE,OPT_TERM) &
     		   VALUES (<ORG_CODE>,<BATCH_SEQ>,<ORDER_CODE>,<ORDER_DESC>,<SPECIFICATION>,&
             		   <UNIT_CODE>, <MAN_CODE>, <BATCH_NO>,TO_DATE(<VALID_DATE>,'YYYYMMDD'), <WAS_QTY>, <WAS_REASON>,<QUALIFIED_QTY>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insert.Debug=N


//���¿����������
update.Type=TSQL
update.SQL=UPDATE IND_CONSERVATION_MED SET &
			  WAS_QTY=<WAS_QTY>,WAS_REASON=<WAS_REASON> &
	            WHERE ORG_CODE=<ORG_CODE> AND BATCH_SEQ=<BATCH_SEQ> AND ORDER_CODE=<ORDER_CODE>
update.Debug=N

//ɾ�������������
delete.Type=TSQL
delete.SQL=DELETE FROM IND_CONSERVATION_MED  WHERE ORG_CODE=<ORG_CODE> AND BATCH_SEQ=<BATCH_SEQ> AND ORDER_CODE=<ORDER_CODE>
delete.Debug=N			