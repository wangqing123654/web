# 
#  Title:�豸�������趨module
# 
#  Description:���ⲿ���趨module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author sundx 2009.06.15
#  version 1.0
#
Module.item=selectDevType;getDevTypeLength;getDevRule;insertDevBase;updateDevBase;selectDevBaseMaxSeq;getDevMaxSerialNumber;selectDevBase;deleteDevBase

//�����豸������Ϣ
selectDevType.Type=TSQL
selectDevType.SQL=SELECT CATEGORY_CODE,CATEGORY_CHN_DESC,DETAIL_FLG &
                  FROM   SYS_CATEGORY &
                  WHERE  RULE_TYPE = 'DEV_RULE' &
                  ORDER  BY SEQ
selectDevType.Debug=N


//��ѯ�豸���볤��
getDevTypeLength.Type=TSQL
getDevTypeLength.SQL=SELECT DISTINCT LENGTH(CATEGORY_CODE) CATEGORY_LENGTH &
                     FROM   SYS_CATEGORY &
                     WHERE  RULE_TYPE = 'DEV_RULE' &
                     ORDER  BY CATEGORY_LENGTH
getDevTypeLength.Debug=N



//��ѯ�豸�������
getDevRule.Type=TSQL
getDevRule.SQL=SELECT CLASSIFY1,CLASSIFY2,CLASSIFY3,CLASSIFY4,CLASSIFY5,&
                      SERIAL_NUMBER,TOT_NUMBER,RULE_TYPE &
               FROM   SYS_RULE &
               WHERE RULE_TYPE = 'DEV_RULE'  
getDevRule.Debug=N


//д���豸������
insertDevBase.Type=TSQL
insertDevBase.SQL=INSERT INTO DEV_BASE(DEV_CODE,ACTIVE_FLG,DEVKIND_CODE,DEVTYPE_CODE,DEVPRO_CODE, &
                                       DEV_CHN_DESC,PY1,SEQ,DESCRIPTION,DEV_ENG_DESC, & 
                                       DEV_ABS_DESC,SPECIFICATION,UNIT_CODE,BUYWAY_CODE,SEQMAN_FLG,&
                                       DEPR_METHOD,MEASURE_FLG,MEASURE_ITEMDESC,MEASURE_FREQ,USE_DEADLINE,&
                                       BENEFIT_FLG,DEV_CLASS,OPT_USER,OPT_DATE,OPT_TERM,MAN_CODE,MAN_NATION,UNIT_PRICE) &
                                VALUES(<DEV_CODE>,<ACTIVE_FLG>,<DEVKIND_CODE>,<DEVTYPE_CODE>,<DEVPRO_CODE>, &
                                       <DEV_CHN_DESC>,<PY1>,<SEQ>,<DESCRIPTION>,<DEV_ENG_DESC>, & 
                                       <DEV_ABS_DESC>,<SPECIFICATION>,<UNIT_CODE>,<BUYWAY_CODE>,<SEQMAN_FLG>,&
                                       <DEPR_METHOD>,<MEASURE_FLG>,<MEASURE_ITEMDESC>,<MEASURE_FREQ>,<USE_DEADLINE>,&
                                       <BENEFIT_FLG>,<DEV_CLASS>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<MAN_CODE>,<MAN_NATION>,<UNIT_PRICE>)
insertDevBase.Debug=N


//�����豸������
updateDevBase.Type=TSQL
updateDevBase.SQL=UPDATE DEV_BASE SET DEV_CODE = <DEV_CODE>,ACTIVE_FLG = <ACTIVE_FLG>,DEVKIND_CODE = <DEVKIND_CODE>,DEVTYPE_CODE = <DEVTYPE_CODE>,DEVPRO_CODE = <DEVPRO_CODE>, &
                                      DEV_CHN_DESC = <DEV_CHN_DESC>,PY1 = <PY1>,SEQ = <SEQ>,DESCRIPTION = <DESCRIPTION>,DEV_ENG_DESC = <DEV_ENG_DESC>, & 
                                      DEV_ABS_DESC = <DEV_ABS_DESC>,SPECIFICATION = <SPECIFICATION>,UNIT_CODE = <UNIT_CODE>,BUYWAY_CODE = <BUYWAY_CODE>,SEQMAN_FLG = <SEQMAN_FLG>,&
                                      DEPR_METHOD = <DEPR_METHOD>,MEASURE_FLG = <MEASURE_FLG>,MEASURE_ITEMDESC = <MEASURE_ITEMDESC>,MEASURE_FREQ = <MEASURE_FREQ>,USE_DEADLINE = <USE_DEADLINE>,&
                                      BENEFIT_FLG = <BENEFIT_FLG>,DEV_CLASS = <DEV_CLASS>,OPT_USER = <OPT_USER>,OPT_DATE = <OPT_DATE>,OPT_TERM = <OPT_TERM>,MAN_CODE = <MAN_CODE>,&
                                      MAN_NATION = <MAN_NATION>,UNIT_PRICE = <UNIT_PRICE> &
                               WHERE  DEV_CODE = <DEV_CODE>   
updateDevBase.Debug=N



//�����豸������
selectDevBase.Type=TSQL
selectDevBase.SQL=SELECT DEV_CODE,ACTIVE_FLG,DEVKIND_CODE,DEVTYPE_CODE,DEVPRO_CODE, &
                         DEV_CHN_DESC,PY1,SEQ,DESCRIPTION,DEV_ENG_DESC, & 
                         DEV_ABS_DESC,SPECIFICATION,UNIT_CODE,BUYWAY_CODE,SEQMAN_FLG,&
                         DEPR_METHOD,MEASURE_FLG,MEASURE_ITEMDESC,MEASURE_FREQ,USE_DEADLINE,&
                         BENEFIT_FLG,DEV_CLASS,OPT_USER,OPT_DATE,OPT_TERM,MAN_CODE,MAN_NATION,UNIT_PRICE &
                  FROM   DEV_BASE &
                  WHERE  DEV_CODE = <DEV_CODE>
selectDevBase.Debug=N



//�����豸������������
selectDevBaseMaxSeq.Type=TSQL
selectDevBaseMaxSeq.SQL=SELECT MAX(SEQ) SEQ &
                        FROM   DEV_BASE
selectDevBaseMaxSeq.Debug=N


//�����豸�����������ˮ��
getDevMaxSerialNumber.Type=TSQL
getDevMaxSerialNumber.SQL=SELECT MAX(DEV_CODE) DEV_CODE FROM DEV_BASE WHERE DEV_CODE LIKE <TYPE_CODE>||'%'
getDevMaxSerialNumber.Debug=N


//ɾ���豸��Ϣ
deleteDevBase.Type=TSQL
deleteDevBase.SQL=DELETE FROM DEV_BASE WHERE DEV_CODE = <DEV_CODE>
deleteDevBase.Debug=N

