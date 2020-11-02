# 
#  Title:���ۼƻ�����
# 
#  Description:���ۼƻ�����
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangy 2009.07.22
#
#  version 1.0
#
Module.item=insert;update;delete;query;updateReadjustStatus


//��ѯ���ۼƻ�����
query.Type=TSQL
query.SQL=SELECT RPP_CODE, RPP_DESC, DESCRIPTION, RPP_USER, RPP_DATE, &
	         CHK_USER, CHK_DATE, READJUST_DATE, READJUSTOP_DATE, RPP_STATUS &
          FROM SYS_FEE_READJUSTM &
          WHERE RPP_DATE >= <START_DATE> AND RPP_DATE <= <END_DATE> &
                AND RPP_STATUS = <RPP_STATUS>
query.ITEM=RPP_CODE;RPP_DESC;RPP_USER;CHK_USER
query.RPP_CODE=RPP_CODE=<RPP_CODE>
query.RPP_DESC=RPP_DESC=<RPP_DESC>
query.RPP_USER=RPP_USER=<RPP_USER>
query.CHK_USER=CHK_USER=<CHK_USER>
query.Debug=N


//�������ۼƻ�����
insert.Type=TSQL
insert.SQL=INSERT INTO SYS_FEE_READJUSTM( &
		RPP_CODE, RPP_DESC, DESCRIPTION, RPP_USER, RPP_DATE, &
		CHK_USER, CHK_DATE, READJUST_DATE, READJUSTOP_DATE, RPP_STATUS, &
		OPT_USER, OPT_DATE, OPT_TERM ) &
           VALUES( &
           	<RPP_CODE>, <RPP_DESC>, <DESCRIPTION>,<RPP_USER>, <RPP_DATE>, &
	   	<CHK_USER>, <CHK_DATE>, <READJUST_DATE>, <READJUSTOP_DATE>, <RPP_STATUS>, &
		<OPT_USER>, <OPT_DATE>, <OPT_TERM> )
insert.Debug=N

//���µ��ۼƻ�����
update.Type=TSQL
update.SQL=UPDATE SYS_FEE_READJUSTM SET &
		RPP_DESC = <RPP_DESC> , &
		DESCRIPTION = <DESCRIPTION> , &
		RPP_USER = <RPP_USER> , &
		RPP_DATE = <RPP_DATE> , &
		CHK_USER = <CHK_USER> , &
		CHK_DATE = <CHK_DATE> , &
		READJUST_DATE = <READJUST_DATE> , &
		READJUSTOP_DATE = <READJUSTOP_DATE>, &
		RPP_STATUS = <RPP_STATUS> , &
		OPT_USER = <OPT_USER> , &
		OPT_DATE = <OPT_DATE> , &
		OPT_TERM = <OPT_TERM> &
	    WHERE RPP_CODE = <RPP_CODE>
update.Debug=N

//ɾ�����ۼƻ�����
delete.Type=TSQL
delete.SQL=DELETE FROM SYS_FEE_READJUSTM WHERE RPP_CODE = <RPP_CODE>
delete.Debug=N


//���µ��ۼƻ������е�ִ��չ������ʱ������״̬
updateReadjustStatus.Type=TSQL
updateReadjustStatus.SQL=UPDATE SYS_FEE_READJUSTM SET &
				READJUSTOP_DATE = <READJUSTOP_DATE>, &
				RPP_STATUS = <RPP_STATUS>  &
	    		  WHERE RPP_CODE = <RPP_CODE>
updateReadjustStatus.Debug=N