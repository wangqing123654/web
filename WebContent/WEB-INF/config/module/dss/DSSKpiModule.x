# 
#  Title:KPIָ��module
# 
#  Description:KPIָ��module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.10.26
#  version 1.0
#
Module.item=initCombo

//���ݿ��ң��������ڲ�ѯ��Ӧ�Ĳ�����¹�
initCombo.Type=TSQL
initCombo.SQL=SELECT KPI_CODE AS ID,KPI_DESC AS NAME,ENNAME,PY1,PY2 &
		FROM DSS_KPI &
	       ORDER BY SEQ,KPI_CODE 		  
initCombo.Debug=N

