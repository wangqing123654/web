# 
#  Title:��ĩ����module
# 
#  Description:��ĩ����module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangy 2009.06.15
#  version 1.0
#
Module.item=selectdata


//ȡ����ݼ��ۿ���Ϣ
selectdata.Type=TSQL
selectdata.SQL=SELECT TERM_NO, TERM_CHN_DESC, TERM_ENG_DESC, PY1, PY2, &
		      SEQ, DESCRIPTION, TERM_IP, TEL_EXT, LOC_CODE, &
		      PRINTER_NO, DESK_NO, OPT_USER, OPT_DATE, OPT_TERM &
		 FROM SYS_TERMINAL &
		WHERE TERM_NO=<TERM_NO> &
	        ORDER BY SEQ 
selectdata.Debug=N