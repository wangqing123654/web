# 
#  Title:�ṹ���������뵥module
# 
#  Description:�ṹ���������뵥module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.06.05
#  version 1.0
#
Module.item=initCombo

//�ṹ���������뵥����combo
initCombo.Type=TSQL
initCombo.SQL=SELECT SUBCLASS_CODE AS ID,SUBCLASS_DESC AS NAME ,ENNAME AS ENNAME &
		FROM EMR_TEMPLET &
	       ORDER BY SUBCLASS_CODE
initCombo.Debug=N
