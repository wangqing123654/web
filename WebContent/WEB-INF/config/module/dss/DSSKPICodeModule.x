# 
#  Title:6.1.1.	ָ��ȼ��趨module
# 
#  Description:6.1.1.	ָ��ȼ��趨module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author sundx 2009.10.12
#  version 1.0
#
Module.item=queryDSSKPI;queryDSSKPIByCode;queryDSSKPIByParentCode;updateDSSKPIByCode;insertDSSKPI;deleteDSSKPI;getMaxSeq
//��ѯ�ȼ�ָ����Ϣ
queryDSSKPI.Type=TSQL
queryDSSKPI.SQL=SELECT KPI_CODE,KPI_DESC,PARENT_CODE,LEAF,PY1,&
                       PY2,SEQ,DESCRIPTION,KPI_VALUE,KPI_GOAL,&
                       KPI_STATUS,KPI_ATTRIBUTE,KPI_KIND,OPT_USER,OPT_DATE, &
                       OPT_TERM &
                FROM   DSS_KPI &
                ORDER BY KPI_CODE
queryDSSKPI.Debug=N


//����KPI_CODE��ѯ�ȼ�ָ����Ϣ
queryDSSKPIByCode.Type=TSQL
queryDSSKPIByCode.SQL=SELECT KPI_CODE,KPI_DESC,PARENT_CODE,LEAF,PY1,&
                             PY2,SEQ,DESCRIPTION,KPI_VALUE,KPI_GOAL,&
                             KPI_STATUS,KPI_ATTRIBUTE,KPI_KIND,OPT_USER,OPT_DATE, &
                             OPT_TERM &
                       FROM  DSS_KPI &
                       WHERE KPI_CODE = <KPI_CODE>
queryDSSKPIByCode.Debug=N

//����PARENT_CODE��ѯ�ȼ�ָ����Ϣ
queryDSSKPIByParentCode.Type=TSQL
queryDSSKPIByParentCode.SQL=SELECT KPI_CODE,KPI_DESC,PARENT_CODE,LEAF,A.PY1,&
                                   A.PY2,A.SEQ,A.DESCRIPTION,KPI_VALUE,KPI_GOAL,&
                                   KPI_STATUS,KPI_ATTRIBUTE,KPI_KIND,B.USER_NAME OPT_USER,A.OPT_DATE, &
                                   A.OPT_TERM &
                            FROM  DSS_KPI A, SYS_OPERATOR B &
                            WHERE PARENT_CODE = <PARENT_CODE> &
                            AND   A.OPT_USER = B.USER_ID
queryDSSKPIByParentCode.Debug=N


//����KPI_CODE����KPI�����Ϣ
updateDSSKPIByCode.Type=TSQL
updateDSSKPIByCode.SQL=UPDATE DSS_KPI &
                       SET    KPI_CODE = <KPI_CODE>,KPI_DESC = <KPI_DESC>,PARENT_CODE=<PARENT_CODE>,&
                              LEAF = <LEAF>,PY1 = <PY1>,PY2 = <PY2>,&
                              SEQ = <SEQ>,DESCRIPTION = <DESCRIPTION>,KPI_VALUE = <KPI_VALUE>,&
                              KPI_GOAL = <KPI_GOAL>,KPI_STATUS = <KPI_STATUS>,KPI_ATTRIBUTE = <KPI_ATTRIBUTE>,&
                              KPI_KIND = <KPI_KIND>,OPT_USER = <OPT_USER>,OPT_DATE = <OPT_DATE>,&
                              OPT_TERM = <OPT_TERM> &
                       WHERE KPI_CODE = <KPI_CODE>
updateDSSKPIByCode.Debug=N



//����DSSKPI
insertDSSKPI.Type=TSQL
insertDSSKPI.SQL=INSERT INTO DSS_KPI &
                             (KPI_CODE,KPI_DESC,PARENT_CODE,&
                              LEAF,PY1,PY2,&
                              SEQ,DESCRIPTION,KPI_VALUE,&
                              KPI_GOAL,KPI_STATUS,KPI_ATTRIBUTE,&
                              KPI_KIND,OPT_USER,OPT_DATE,&
                              OPT_TERM)&
                      VALUES(<KPI_CODE>,<KPI_DESC>,<PARENT_CODE>,&
                             <LEAF>,<PY1>,<PY2>,&
                             <SEQ>,<DESCRIPTION>,<KPI_VALUE>,&
                             <KPI_GOAL>,<KPI_STATUS>,<KPI_ATTRIBUTE>,&
                             <KPI_KIND>,<OPT_USER>,<OPT_DATE>,&
                             <OPT_TERM>)
insertDSSKPI.Debug=N

//ɾ��DSSKPI
deleteDSSKPI.Type=TSQL
deleteDSSKPI.SQL=DELETE FROM DSS_KPI WHERE KPI_CODE = <KPI_CODE>
deleteDSSKPI.Debug=N

//ɾ��DSSKPI
getMaxSeq.Type=TSQL
getMaxSeq.SQL=SELECT MAX(SEQ) AS SEQ FROM DSS_KPI
getMaxSeq.Debug=N