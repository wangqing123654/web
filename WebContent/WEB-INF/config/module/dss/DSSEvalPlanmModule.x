# 
#  Title:��������module
# 
#  Description:��������module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.10.26
#  version 1.0
#
Module.item=initCombo

//���ݿ��ң��������ڲ�ѯ��Ӧ�Ĳ�����¹�
initCombo.Type=TSQL
initCombo.SQL=SELECT PLAN_CODE AS ID,PLAN_DESC AS NAME,ENNAME,PY1,PY2 &
		FROM DSS_EVAL_PLANM &
	       ORDER BY SEQ,PLAN_CODE 		  
initCombo.Debug=N

