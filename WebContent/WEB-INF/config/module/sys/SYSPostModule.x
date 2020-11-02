Module.item=selectdata;deletedata;insertdata;updatedata;getState;getCity

//��ѯְ�ƴ��룬ְ��˵����ְ����𣬲�����Ա���������ڣ�˳���ţ�ƴ�����룬ע�Ƿ�����ע
selectdata.Type=TSQL
selectdata.SQL=SELECT POST_NO3,STATE,STATE_PY1,STATE_PY2,CITY,CITY_PY1,CITY_PY2,D_CITY_FLG,DESCRIPTION,SEQ,OPT_USER,OPT_DATE,OPT_TERM FROM SYS_POSTCODE ORDER BY STATE,CITY,POST_NO3, SEQ
selectdata.item=STATE,CITY;POST_NO3
selectdata.STATE=STATE=<STATE>
selectdata.CITY=CITY=<CITY>
selectdata.POST_NO3=POST_NO3=<POST_NO3>
selectdata.Debug=N

//�õ�ʡ
getState.Type=TSQL
getState.SQL=SELECT STATE FROM SYS_POSTCODE WHERE POST_NO3=<POST_NO3>
getState.Debug=N

//�õ�����
getCity.Type=TSQL
getCity.SQL=SELECT CITY FROM SYS_POSTCODE WHERE POST_NO3=<POST_NO3>
getCity.Debug=N

//ɾ��ְ�ƴ��룬ְ��˵����ְ����𣬲�����Ա���������ڣ�˳���ţ�ƴ�����룬ע�Ƿ�����ע
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM SYS_POSTCODE WHERE POST_NO3 = <POST_NO3>
deletedata.Debug=N

//SEQ;STATE;CITY;POST_NO3;D_CITY_FLG;DESCRIPTION;STATE_PY1;STATE_PY2;CITY_PY1;CITY_PY2
//����ְ�ƴ��룬ְ��˵����ְ����𣬲�����Ա���������ڣ�˳���ţ�ƴ�����룬ע�Ƿ�����ע
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO SYS_POSTCODE VALUES(<POST_NO3>,<STATE>,<STATE_PY1>,<STATE_PY2>,<CITY>,<CITY_PY1>,<CITY_PY2>,<D_CITY_FLG>,<DESCRIPTION>,<SEQ>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertdata.Debug=N

//SEQ:SEQ:int;STATE:STATE;CITY:CITY;POST_NO3:POST_NO3:int;D_CITY_FLG:D_CITY_FLG;DESCRIPTION:DESCRIPTION;STATE_PY1:STATE_PY1;STATE_PY2:STATE_PY2;CITY_PY1:CITY_PY1;CITY_PY2:CITY_PY2
//����ְ��˵����ְ����𣬲�����Ա���������ڣ�˳���ţ�ƴ�����룬ע�Ƿ�����ע
//,OPT_TERM=<OPT_TERM>
updatedata.Type=TSQL
updatedata.SQL=UPDATE SYS_POSTCODE SET STATE=<STATE>,STATE_PY1=<STATE_PY1>,STATE_PY2=<STATE_PY2>,CITY=<CITY>,CITY_PY1=<CITY_PY1>,D_CITY_FLG=<D_CITY_FLG>,DESCRIPTION=<DESCRIPTION>,SEQ=<SEQ>,OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> WHERE POST_NO3=<POST_NO3>
updatedata.Debug=N



