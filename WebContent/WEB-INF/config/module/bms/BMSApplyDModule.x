   #
   # Title:��Ѫ���뵥ϸ��
   #
   # Description:��Ѫ���뵥ϸ��
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/04/29

Module.item=ApplyInsert;ApplyDelete;ApplyQuery

//����
ApplyInsert.Type=TSQL
ApplyInsert.SQL=INSERT INTO BMS_APPLYD(APPLY_NO, BLD_CODE, APPLY_QTY, UNIT_CODE,APPLY_BLD,APPLY_RH_TYPE,IRRADIATION, & 
				 PRE_DATE, OPT_USER, OPT_DATE, OPT_TERM) &
			  VALUES(<APPLY_NO>, <BLD_CODE>, <APPLY_QTY>, <UNIT_CODE>, <APPLY_BLD>, <APPLY_RH_TYPE> ,<IRRADIATION>,& 
				 <PRE_DATE>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
ApplyInsert.Debug=N


//ɾ��
ApplyDelete.Type=TSQL
ApplyDelete.SQL=DELETE FROM BMS_APPLYD WHERE APPLY_NO=<APPLY_NO>
ApplyDelete.Debug=N


//��ѯ
ApplyQuery.Type=TSQL
ApplyQuery.SQL=SELECT APPLY_NO, BLD_CODE, SUBCAT_CODE, &
   		      APPLY_QTY, APPLY_VOL, UNIT_CODE, &
		      APPLY_BLD, APPLY_RH_TYPE, IRRADIATION,&
   		      PRE_DATE, OPT_USER, OPT_DATE, OPT_TERM &
		 FROM BMS_APPLYD 
ApplyQuery.ITEM=APPLY_NO;BLD_CODE
ApplyQuery.APPLY_NO=APPLY_NO=<APPLY_NO>
ApplyQuery.BLD_CODE=BLD_CODE=<BLD_CODE>
ApplyQuery.Debug=N