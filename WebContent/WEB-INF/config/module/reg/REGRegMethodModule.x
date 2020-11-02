# 
#  Title:�Һŷ�ʽmodule
# 
#  Description:�Һŷ�ʽmodule
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=selectdata;deletedata;insertdata;updatedata;existsRegMethod;getregmethodCombo;selComboFlg;selPrintFlg;selSiteNumFlg

//��ѯ�Һŷ�ʽ����ʽ˵����ƴ�����룬ע�Ƿ���˳����,��ע,ԤԼ����,����ˬԼ,�ֶ�ѡ��,�������������Ա���������ڣ������ն�
//======pangb 2012-2-20 ��Ӵ�Ʊע��PRINT_FLG
selectdata.Type=TSQL
selectdata.SQL=SELECT REGMETHOD_CODE,REGMETHOD_DESC,PY1,PY2,SEQ,&
		      DESCRIPTION,APPT_WEEK,MISSVST_FLG,COMBO_FLG,READIC_FLG,ORDER_CODE,&
		      OPT_USER,OPT_DATE,OPT_TERM,PRINT_FLG,SITENUM_FLG &
		 FROM REG_REGMETHOD &
		WHERE REGMETHOD_CODE LIKE <REGMETHOD_CODE> &
	     ORDER BY SEQ
selectdata.Debug=N

//ɾ���Һŷ�ʽ����ʽ˵����ƴ�����룬ע�Ƿ���˳����,��ע,ԤԼ����,����ˬԼ,�ֶ�ѡ��,�������������Ա���������ڣ������ն�
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM REG_REGMETHOD &
		     WHERE REGMETHOD_CODE = <REGMETHOD_CODE>
deletedata.Debug=N

//�����Һŷ�ʽ����ʽ˵����ƴ�����룬ע�Ƿ���˳����,��ע,ԤԼ����,����ˬԼ,�ֶ�ѡ��,�������������Ա���������ڣ������ն�
//======pangb 2012-2-20 ��Ӵ�Ʊע��PRINT_FLG
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_REGMETHOD &
			   (REGMETHOD_CODE,REGMETHOD_DESC,PY1,PY2,SEQ,&
			   DESCRIPTION,APPT_WEEK,MISSVST_FLG,COMBO_FLG,READIC_FLG,&
			   ORDER_CODE,OPT_USER,OPT_DATE,OPT_TERM,PRINT_FLG,SITENUM_FLG) &
		    VALUES (<REGMETHOD_CODE>,<REGMETHOD_DESC>,<PY1>,<PY2>,<SEQ>,&
		           <DESCRIPTION>,<APPT_WEEK>,<MISSVST_FLG>,<COMBO_FLG>,<READIC_FLG>,&
		           <ORDER_CODE>,<OPT_USER>,SYSDATE,<OPT_TERM>,<PRINT_FLG>,<SITENUM_FLG>)
insertdata.Debug=N

//���¹Һŷ�ʽ����ʽ˵����ƴ�����룬ע�Ƿ���˳����,��ע,ԤԼ����,����ˬԼ,�ֶ�ѡ��,�������������Ա���������ڣ������ն�
//======pangb 2012-2-20 ��Ӵ�Ʊע��PRINT_FLG
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_REGMETHOD &
		  SET REGMETHOD_CODE=<REGMETHOD_CODE>,REGMETHOD_DESC=<REGMETHOD_DESC>,PY1=<PY1>,&
		      PY2=<PY2>,SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,&
		      APPT_WEEK=<APPT_WEEK>,MISSVST_FLG=<MISSVST_FLG>,COMBO_FLG=<COMBO_FLG>,&
		      READIC_FLG=<READIC_FLG>,ORDER_CODE=<ORDER_CODE>,OPT_USER=<OPT_USER>,&
		      OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>,PRINT_FLG=<PRINT_FLG>,SITENUM_FLG=<SITENUM_FLG> &
		WHERE REGMETHOD_CODE=<REGMETHOD_CODE>
updatedata.Debug=N

//�Ƿ���ڹҺŷ�ʽ
existsRegMethod.type=TSQL
existsRegMethod.SQL=SELECT COUNT(*) AS COUNT &
		      FROM REG_REGMETHOD &
		     WHERE REGMETHOD_CODE=<REGMETHOD_CODE>

//ȡ�ùҺŷ�ʽcombo��Ϣ
getregmethodCombo.Type=TSQL
getregmethodCombo.SQL=SELECT REGMETHOD_CODE AS ID,REGMETHOD_DESC AS NAME,ENNAME,PY1,PY2 &
			FROM REG_REGMETHOD &
		    ORDER BY SEQ
getregmethodCombo.item=COMBO_FLG
getregmethodCombo.COMBO_FLG=COMBO_FLG=<COMBO_FLG>
getregmethodCombo.Debug=N

//ȡ�ùҺŷ�ʽcombo��Ϣ
selComboFlg.Type=TSQL
selComboFlg.SQL=SELECT COMBO_FLG &
		  FROM REG_REGMETHOD &
		 WHERE REGMETHOD_CODE = <REGMETHOD_CODE>
selComboFlg.Debug=N

//�õ���Ʊע��
selPrintFlg.Type=TSQL
selPrintFlg.SQL=SELECT PRINT_FLG &
		  FROM REG_REGMETHOD &
		 WHERE REGMETHOD_CODE = <REGMETHOD_CODE>
selPrintFlg.Debug=N

//�õ��ֳ��ű��
selSiteNumFlg.Type=TSQL
selSiteNumFlg.SQL=SELECT SITENUM_FLG &
		  FROM REG_REGMETHOD &
		 WHERE REGMETHOD_CODE = <REGMETHOD_CODE>
selSiteNumFlg.Debug=N