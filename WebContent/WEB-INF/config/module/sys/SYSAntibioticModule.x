# 
#  Title:�����صȼ�module
# 
#  Description:�����صȼ�module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.04.24
#  version 1.0
#
Module.item=initSYSAntibioticCode;selectdata

//�����صȼ�combo
initSYSAntibioticCode.Type=TSQL
initSYSAntibioticCode.SQL=SELECT ANTIBIOTIC_CODE AS ID,ANTIBIOTIC_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 &
			    FROM SYS_ANTIBIOTIC &
			ORDER BY ANTIBIOTIC_CODE
initSYSAntibioticCode.Debug=N

//ȡ�ÿ����صȼ���Ϣ
selectdata.Type=TSQL
selectdata.SQL=SELECT ANTIBIOTIC_CODE, ANTIBIOTIC_DESC, PY1, PY2, DESCRIPTION, &
		      TAKE_DAYS, OPT_USER, MR_CODE, OPT_DATE, OPT_TERM &
		 FROM SYS_ANTIBIOTIC &
		WHERE ANTIBIOTIC_CODE=<ANTIBIOTIC_CODE> &
		  AND ANTIBIOTIC_DESC=<ANTIBIOTIC_DESC> &
		  ORDER BY ANTIBIOTIC_CODE 
selectdata.Debug=N