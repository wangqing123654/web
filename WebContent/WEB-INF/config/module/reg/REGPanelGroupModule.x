# 
#  Title:�������module
# 
#  Description:�������module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=queryTree;selectdata;deletedata;insertdata;updatedata;existsQueGroup;getMaxQue;getVipFlg;getInfobyClinicType

//��ѯ�������
queryTree.Type=TSQL
queryTree.SQL=SELECT QUEGROUP_CODE,QUEGROUP_DESC,PY1,PY2,SEQ,&
		     DESCRIPTION,MAX_QUE,VIP_FLG,OPT_USER,OPT_DATE,&
		     OPT_TERM &
		FROM REG_QUEGROUP &
	    ORDER BY QUEGROUP_CODE,SEQ
queryTree.Debug=N


//��ѯ�������,�ű�˵��,ƴ����,ע����,˳����,��ע,������,VIPע��,������Ա,��������,�����ն�
selectdata.Type=TSQL
selectdata.SQL=SELECT QUEGROUP_CODE,QUEGROUP_DESC,PY1,PY2,SEQ,&
		      DESCRIPTION,MAX_QUE,VIP_FLG,OPT_USER,OPT_DATE,&
		      OPT_TERM,SESSION_CODE,ADM_TYPE &
		 FROM REG_QUEGROUP &
	     ORDER BY QUEGROUP_CODE,SEQ
selectdata.item=QUEGROUP_CODE
selectdata.QUEGROUP_CODE=QUEGROUP_CODE=<QUEGROUP_CODE>
selectdata.Debug=N

//ɾ���������,�ű�˵��,ƴ����,ע����,˳����,��ע,������,VIPע��,������Ա,��������,�����ն�
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM REG_QUEGROUP &
		     WHERE QUEGROUP_CODE = <QUEGROUP_CODE>
deletedata.Debug=N

//�����������,�ű�˵��,ƴ����,ע����,˳����,��ע,������,VIPע��,������Ա,��������,�����ն�
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_QUEGROUP &
			   (QUEGROUP_CODE,QUEGROUP_DESC,PY1,PY2,SEQ,&
			   DESCRIPTION,MAX_QUE,VIP_FLG,OPT_USER,OPT_DATE,&
			   OPT_TERM,SESSION_CODE,ADM_TYPE) &
		    VALUES (<QUEGROUP_CODE>,<QUEGROUP_DESC>,<PY1>,<PY2>,<SEQ>,&
			   <DESCRIPTION>,<MAX_QUE>,<VIP_FLG>,<OPT_USER>,SYSDATE,&
			   <OPT_TERM>,<SESSION_CODE>,<ADM_TYPE>)
insertdata.Debug=N

//���¸������,�ű�˵��,ƴ����,ע����,˳����,��ע,������,VIPע��,������Ա,��������,�����ն�
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_QUEGROUP &
		  SET QUEGROUP_CODE=<QUEGROUP_CODE>,QUEGROUP_DESC=<QUEGROUP_DESC>,PY1=<PY1>,&
		      PY2=<PY2>,SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,&
		      MAX_QUE=<MAX_QUE>,VIP_FLG=<VIP_FLG>,OPT_USER=<OPT_USER>,&
		      OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>,SESSION_CODE=<SESSION_CODE>,ADM_TYPE=<ADM_TYPE> &
                WHERE QUEGROUP_CODE = <QUEGROUP_CODE>
updatedata.Debug=N

//�Ƿ���ڸ������
existsQueGroup.type=TSQL
existsQueGroup.SQL=SELECT COUNT(*) AS COUNT &
		     FROM REG_QUEGROUP &
		    WHERE QUEGROUP_CODE = <QUEGROUP_CODE>

//�õ�������
getMaxQue.type=TSQL
getMaxQue.SQL=SELECT MAX_QUE &
		FROM REG_QUEGROUP &
	       WHERE QUEGROUP_CODE = <QUEGROUP_CODE>

//�õ�VIPע��
getVipFlg.type=TSQL
getVipFlg.SQL=SELECT VIP_FLG &
		FROM REG_QUEGROUP &
	       WHERE QUEGROUP_CODE = <QUEGROUP_CODE>

//���ݺű��ѯ������ţ�������VIPע�ǣ���ʱ��
getInfobyClinicType.Type=TSQL
getInfobyClinicType.SQL=SELECT MAX_QUE,VIP_FLG &
			  FROM REG_QUEGROUP &
			 WHERE QUEGROUP_CODE=<QUEGROUP_CODE>
getInfobyClinicType.Debug=N

