Module.item=selectdata;deletedata;insertdata;updatedata;existsArvTime

//��ѯ������𣬿�ʼʱ�䣬���ʱ�䣬������Ա���������ڣ������ն�
selectdata.Type=TSQL
selectdata.SQL=SELECT QUE_GROUP,START_TIME,INTERAL_TIME, OPT_USER,OPT_DATE,OPT_TERM &
		 FROM REG_ARVTIME &
	     ORDER BY QUE_GROUP
selectdata.item=QUE_GROUP;START_TIME
selectdata.QUE_GROUP=QUE_GROUP=<QUE_GROUP>
selectdata.START_TIME=START_TIME=<START_TIME>
selectdata.Debug=N

//ɾ��������𣬿�ʼʱ�䣬���ʱ�䣬������Ա���������ڣ������ն�
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM REG_ARVTIME &
		     WHERE QUE_GROUP = <QUE_GROUP> &
		       AND START_TIME = <START_TIME>
deletedata.Debug=N

//����������𣬿�ʼʱ�䣬���ʱ�䣬������Ա���������ڣ������ն�
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_ARVTIME &
			   (QUE_GROUP,START_TIME,INTERAL_TIME, OPT_USER,OPT_DATE,&
			   OPT_TERM) &
		    VALUES (<QUE_GROUP>,<START_TIME>,<INTERAL_TIME>,<OPT_USER>,SYSDATE,&
		    	   <OPT_TERM>)
insertdata.Debug=N

//���¸�����𣬿�ʼʱ�䣬���ʱ�䣬������Ա���������ڣ������ն�
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_ARVTIME &
		  SET QUE_GROUP=<QUE_GROUP>,START_TIME=<START_TIME>,INTERAL_TIME=<INTERAL_TIME>,&
		      OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		WHERE QUE_GROUP = <QUE_GROUP> &
		  AND START_TIME = <START_TIME>
updatedata.Debug=N

//�Ƿ���ڸ������
existsArvTime.type=TSQL
existsArvTime.SQL=SELECT COUNT(*) AS COUNT &
		    FROM REG_ARVTIME &
		   WHERE QUE_GROUP=<QUE_GROUP> &
		     AND START_TIME=<START_TIME>

