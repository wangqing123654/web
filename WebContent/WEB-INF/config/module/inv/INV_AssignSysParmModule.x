   #
   # Title: ���ʲ��������趨��
   #
   # Description: ���ʲ��������趨��
   #
   # Copyright: JavaHis (c) 2013
   #
   # @author fux 2013/05/27
 
Module.item=insert;updatesysparm

//�������ʲ��������趨��
insert.Type=TSQL
insert.SQL=INSERT INTO INV_SYSPARM( &
			ADD_STOCK_FLG,FIXEDAMOUNT_FLG,REUPRICE_FLG,HISTORY_DAYS,DISCHECK_FLG, &
			MM_DAY,HIGH_VALUE_INV, &
			OPT_USER, OPT_DATE, OPT_TERM ) &
		      VALUES( &
	    	        <ADD_STOCK_FLG>, <FIXEDAMOUNT_FLG>, <REUPRICE_FLG>, <HISTORY_DAYS>, <DISCHECK_FLG>, &
			<MM_DAY>, HIGH_VALUE_INV>,&
			<OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insert.Debug=N 
                                             
//�������ʲ��������趨�� 
  
updatesysparm.Type=TSQL 
updatesysparm.SQL=UPDATE INV_SYSPARM SET &       
			 FIXEDAMOUNT_FLG=<FIXEDAMOUNT_FLG>, AUTO_FILL_TYPE=<AUTO_FILL_TYPE> WHERE 1=1  
//updatesysparm.SQL=SELECT * FROM INV_SYSPARM  
updatesysparm.Debug=Y   
 
  



