# 
#  Title:VIP���Ӿ����module
# 
#  Description:VIP���Ӿ����module
# 
#  Copyright: Copyright (c) Javahis 2010
# 
#  author wangl 2010.07.14
#  version 1.0
#
Module.item=addRegQue


//VIP���Ӿ����

addRegQue.Type=TSQL
addRegQue.SQL=INSERT INTO REG_CLINICQUE ( &
		ADM_TYPE,ADM_DATE,SESSION_CODE,CLINICROOM_NO,QUE_NO,&
		QUE_STATUS,VISIT_CODE,APPT_CODE,REGMETHOD_CODE,START_TIME,&
		OPT_USER,OPT_DATE,OPT_TERM,ADD_FLG &
		)VALUES(&
		<ADM_TYPE>,<ADM_DATE>,<SESSION_CODE>,<CLINICROOM_NO>,<QUE_NO>,&
		<QUE_STATUS>,<VISIT_CODE>,<APPT_CODE>,<REGMETHOD_CODE>,&
		<START_TIME>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<ADD_FLG>)
addRegQue.Debug=N

