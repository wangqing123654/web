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
Module.item=selectdata;deletedata;insertdata;updatedata;exists

//��ѯ�Һŷ�ʽ����ʽ˵����ƴ�����룬ע�Ƿ���˳����,��ע,ԤԼ����,����ˬԼ,�ֶ�ѡ��,�������������Ա���������ڣ������ն�
selectdata.Type=TSQL
selectdata.SQL=SELECT TMPTRKINDCODE,TMPTRKINDDESC,PRESENTNOTATION,OPT_USER,OPT_DATE,OPT_TERM FROM SUM_TMPATUREKIND
selectdata.Debug=N

//ɾ���Һŷ�ʽ����ʽ˵����ƴ�����룬ע�Ƿ���˳����,��ע,ԤԼ����,����ˬԼ,�ֶ�ѡ��,�������������Ա���������ڣ������ն�
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM SUM_TMPATUREKIND WHERE TMPTRKINDCODE = <TMPTRKINDCODE>
deletedata.Debug=N

//�����Һŷ�ʽ����ʽ˵����ƴ�����룬ע�Ƿ���˳����,��ע,ԤԼ����,����ˬԼ,�ֶ�ѡ��,�������������Ա���������ڣ������ն�
insertdata.Type=TSQL
insertdata.SQL= INSERT INTO SUM_TMPATUREKIND (TMPTRKINDCODE,TMPTRKINDDESC,PRESENTNOTATION,OPT_USER,OPT_DATE,OPT_TERM) &
		VALUES(<TMPTRKINDCODE>,<TMPTRKINDDESC>,<PRESENTNOTATION>,&
		<OPT_USER>,SYSDATE,<OPT_TERM>)
insertdata.Debug=N

//���¹Һŷ�ʽ����ʽ˵����ƴ�����룬ע�Ƿ���˳����,��ע,ԤԼ����,����ˬԼ,�ֶ�ѡ��,�������������Ա���������ڣ������ն�
updatedata.Type=TSQL
updatedata.SQL= UPDATE SUM_TMPATUREKIND SET TMPTRKINDCODE=<TMPTRKINDCODE>,TMPTRKINDDESC=<TMPTRKINDDESC>,PRESENTNOTATION=<PRESENTNOTATION>,&
		OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> WHERE TMPTRKINDCODE=<TMPTRKINDCODE>
updatedata.Debug=N

//�Ƿ���ڹҺŷ�ʽ
exists.type=TSQL
exists.SQL=SELECT COUNT(*) AS COUNT FROM SUM_TMPATUREKIND WHERE TMPTRKINDCODE=<TMPTRKINDCODE>



