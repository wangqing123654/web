# 
#  Title:��ݼ��ۿ���ϸ��module
# 
#  Description:��ݼ��ۿ���ϸ��module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.04.24
#  version 1.0
#
Module.item=selectdata


//ȡ����ݼ��ۿ���Ϣ
selectdata.Type=TSQL
selectdata.SQL=SELECT CTZ_CODE, CHARGE_HOSP_CODE, DISCOUNT_RATE, OPT_USER, OPT_DATE, &
		      OPT_TERM &
		 FROM SYS_CHARGE_DETAIL &
		WHERE CTZ_CODE=<CTZ_CODE> &
		  AND CHARGE_HOSP_CODE=<CHARGE_HOSP_CODE> &
		  ORDER BY CTZ_CODE 
selectdata.Debug=N