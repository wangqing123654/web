# 
#  Title:�ҺŲ���module
# 
#  Description:�ҺŲ���module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=selectdata;updatedata;selPayWay;selVisitCode;insertdata;selPayWay;selVisitCode;selEffectDays;selOthHospRegFlg;selTriageFlg;selAeadFlg;selTicketFlg;selSingleFlg

//��ѯ����ע�ǣ��˹Ҳ�ռ�ţ���Ժ���Һţ����������,�����������,Ĭ�ϵ�֧����ʽ,Ĭ�ϳ�����,�Һ��վ���Ч����
selectdata.Type=TSQL
selectdata.SQL=SELECT CHECKIN_FLG,QUEREUSE_FLG,OTHHOSP_REG_FLG,TRIAGE_FLG,APPTCONTI_FLG,&
		      EFFECT_DAYS,DEFAULT_PAY_WAY,DEFAULT_VISIT_CODE,AHEAD_FLG,TICKET_FLG,SINGLE_FLG &
		 FROM REG_SYSPARM
selectdata.Debug=N



//���±���ע�ǣ��˹Ҳ�ռ�ţ���Ժ���Һţ����������,�����������,Ĭ�ϵ�֧����ʽ,Ĭ�ϳ�����,�Һ��վ���Ч����,������Ա���������ڣ������ն�
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_SYSPARM &
		  SET CHECKIN_FLG=<CHECKIN_FLG>,QUEREUSE_FLG=<QUEREUSE_FLG>,OTHHOSP_REG_FLG=<OTHHOSP_REG_FLG>,&
		      TRIAGE_FLG=<TRIAGE_FLG>,APPTCONTI_FLG=<APPTCONTI_FLG>,EFFECT_DAYS=<EFFECT_DAYS>,&
		      DEFAULT_PAY_WAY=<DEFAULT_PAY_WAY>,DEFAULT_VISIT_CODE=<DEFAULT_VISIT_CODE>,OPT_USER=<OPT_USER>,&
		      OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>,AHEAD_FLG=<AHEAD_FLG>,TICKET_FLG=<TICKET_FLG>,SINGLE_FLG=<SINGLE_FLG>
updatedata.Debug=N

//��������ע�ǣ��˹Ҳ�ռ�ţ���Ժ���Һţ����������,�����������,Ĭ�ϵ�֧����ʽ,Ĭ�ϳ�����,�Һ��վ���Ч����,������Ա���������ڣ������ն�
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_SYSPARM &
		           (CHECKIN_FLG,QUEREUSE_FLG,OTHHOSP_REG_FLG,TRIAGE_FLG,APPTCONTI_FLG,&
		           EFFECT_DAYS,DEFAULT_PAY_WAY,DEFAULT_VISIT_CODE,OPT_USER,OPT_DATE,&
		           OPT_TERM, AHEAD_FLG,TICKET_FLG,SINGLE_FLG) &
		    VALUES (<CHECKIN_FLG>,<QUEREUSE_FLG>,<OTHHOSP_REG_FLG>,<TRIAGE_FLG>,<APPTCONTI_FLG>,&
		    	   <EFFECT_DAYS>,<DEFAULT_PAY_WAY>,<DEFAULT_VISIT_CODE>,<OPT_USER>,SYSDATE,&
		    	   <OPT_TERM>,<AHEAD_FLG>,<TICKET_FLG>,<SINGLE_FLG>)
insertdata.Debug=N


//��ʼ��֧����ʽ
selPayWay.Type=TSQL
selPayWay.SQL=SELECT DEFAULT_PAY_WAY &
		FROM REG_SYSPARM
selPayWay.Debug=N

//��ʼ��������
selVisitCode.Type=TSQL
selVisitCode.SQL=SELECT DEFAULT_VISIT_CODE &
		   FROM REG_SYSPARM
selVisitCode.Debug=N

//��ѯ�Һ���Ч����
selEffectDays.Type=TSQL
selEffectDays.SQL=SELECT EFFECT_DAYS &
		    FROM REG_SYSPARM
selEffectDays.Debug=N

//��ѯ�Ƿ���Կ�Ժ���Һ�
selOthHospRegFlg.Type=TSQL
selOthHospRegFlg.SQL=SELECT OTHHOSP_REG_FLG &
		       FROM REG_SYSPARM
selOthHospRegFlg.Debug=N

//��ѯ������˱��
selTriageFlg.Type=TSQL
selTriageFlg.SQL=SELECT TRIAGE_FLG &
		       FROM REG_SYSPARM
selTriageFlg.Debug=N

//��ѯ������˱��
selAeadFlg.Type=TSQL
selAeadFlg.SQL=SELECT AHEAD_FLG &
		       FROM REG_SYSPARM
selAeadFlg.Debug=N


//��ѯ�Һ��Ƿ��Ʊ
selTicketFlg.Type=TSQL
selTicketFlg.SQL=SELECT TICKET_FLG &
		       FROM REG_SYSPARM
selTicketFlg.Debug=N

//��ѯ�Һ��Ƿ���ﵥ add by huangtt 20140409
selSingleFlg.Type=TSQL
selSingleFlg.SQL=SELECT SINGLE_FLG &
		       FROM REG_SYSPARM
selSingleFlg.Debug=N