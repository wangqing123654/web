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
Module.item=queryTree;selectdata;deletedata;insertdata;updatedata;existsQueMethod;getqueGropCombo;getqueGropNotVipCombo;updatQueNo;updateArriveTime;&
	    seldataForTable

//��ѯ���ŷ�ʽ
queryTree.Type=TSQL
queryTree.SQL=SELECT QUEGROUP_CODE,QUE_NO,VISIT_CODE,APPT_CODE,REGMETHOD_CODE,&
		     START_TIME,OPT_USER,OPT_DATE,OPT_TERM &
		FROM REG_QUEMETHOD &
	       WHERE QUEGROUP_CODE = <QUEGROUP_CODE> &
	         AND QUE_NO=<QUE_NO> &
	    ORDER BY QUEGROUP_CODE,QUE_NO
queryTree.Debug=N


//��ѯ�������,�����,������,����ԤԼ,�Һŷ�ʽ,��Ժʱ��,������Ա,��������,�����ն�
selectdata.Type=TSQL
selectdata.SQL=SELECT QUEGROUP_CODE,QUE_NO,VISIT_CODE,APPT_CODE,REGMETHOD_CODE,&
		      START_TIME,OPT_USER,OPT_DATE,OPT_TERM &
		 FROM REG_QUEMETHOD &
	     ORDER BY QUEGROUP_CODE,QUE_NO
selectdata.item=QUEGROUP_CODE;QUE_NO
selectdata.QUEGROUP_CODE=QUEGROUP_CODE=<QUEGROUP_CODE>
selectdata.QUE_NO=QUE_NO=<QUE_NO>
selectdata.Debug=N

//ɾ���������,�����,������,����ԤԼ,�Һŷ�ʽ,��Ժʱ��,������Ա,��������,�����ն�
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM REG_QUEMETHOD &
		     WHERE QUEGROUP_CODE = <QUEGROUP_CODE> &
		       AND QUE_NO=<QUE_NO>
deletedata.Debug=N

//�����������,�����,������,����ԤԼ,�Һŷ�ʽ,��Ժʱ��,������Ա,��������,�����ն�
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_QUEMETHOD &
			   (QUEGROUP_CODE,QUE_NO,VISIT_CODE,APPT_CODE,REGMETHOD_CODE,&
			   START_TIME,OPT_USER,OPT_DATE,OPT_TERM) &
		    VALUES (<QUEGROUP_CODE>,<QUE_NO>,<VISIT_CODE>,<APPT_CODE>,<REGMETHOD_CODE>,&
		           <START_TIME>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertdata.Debug=N

//���¸������,�����,������,����ԤԼ,�Һŷ�ʽ,��Ժʱ��,������Ա,��������,�����ն�
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_QUEMETHOD &
		  SET QUEGROUP_CODE=<QUEGROUP_CODE>,QUE_NO=<QUE_NO>,VISIT_CODE=<VISIT_CODE>,&
		      APPT_CODE=<APPT_CODE>,REGMETHOD_CODE=<REGMETHOD_CODE>,START_TIME=<START_TIME>,&
		      OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
                WHERE QUEGROUP_CODE = <QUEGROUP_CODE> &
                  AND QUE_NO=<QUE_NO>
updatedata.Debug=N

//�Ƿ���ڸ������
existsQueMethod.type=TSQL
existsQueMethod.SQL=SELECT COUNT(*) AS COUNT &
		      FROM REG_QUEMETHOD &
		     WHERE QUEGROUP_CODE = <QUEGROUP_CODE> &
		       AND QUE_NO=<QUE_NO>

//ȡ�ø������combo��Ϣ
getqueGropCombo.Type=TSQL
getqueGropCombo.SQL=SELECT QUEGROUP_CODE AS ID,QUEGROUP_DESC AS NAME,PY1,PY2 &
		      FROM REG_QUEGROUP &
		  ORDER BY QUEGROUP_CODE
getqueGropCombo.Debug=N

//ȡ�ø������ ��ͨ�� combo��Ϣ
getqueGropNotVipCombo.Type=TSQL
getqueGropNotVipCombo.SQL=SELECT QUEGROUP_CODE AS ID,QUEGROUP_DESC AS NAME,PY1,PY2 &
			    FROM REG_QUEGROUP &
			   WHERE VIP_FLG='N' &
			ORDER BY QUEGROUP_CODE
getqueGropNotVipCombo.Debug=N

//���¾����
updatQueNo.Type=TSQL
updatQueNo.SQL=UPDATE REG_QUEMETHOD &
		  SET VISIT_CODE=<VISIT_CODE>,APPT_CODE=<APPT_CODE>,REGMETHOD_CODE=<REGMETHOD_CODE>,&
		      OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
                WHERE QUEGROUP_CODE = <QUEGROUP_CODE> &
                  AND QUE_NO=<QUE_NO>
updatQueNo.Debug=N

//���µ�Ժʱ��
updateArriveTime.Type=TSQL
updateArriveTime.SQL=UPDATE REG_QUEMETHOD &
			SET START_TIME=<START_TIME>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,&
			    OPT_TERM=<OPT_TERM> &
		      WHERE QUEGROUP_CODE = <QUEGROUP_CODE> &
		      	AND QUE_NO=<QUE_NO>
updateArriveTime.Debug=N

//ˢ��table�ò�ѯ
seldataForTable.Type=TSQL
seldataForTable.SQL=SELECT QUEGROUP_CODE,QUE_NO,VISIT_CODE,APPT_CODE,REGMETHOD_CODE,&
			   START_TIME,OPT_USER,OPT_DATE,OPT_TERM &
		      FROM REG_QUEMETHOD &
		  ORDER BY QUEGROUP_CODE,QUE_NO
seldataForTable.item=QUEGROUP_CODE
seldataForTable.QUEGROUP_CODE=QUEGROUP_CODE=<QUEGROUP_CODE>
seldataForTable.Debug=N
