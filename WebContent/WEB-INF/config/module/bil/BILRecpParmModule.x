# 
#  Title:�վݴ�ӡ�趨��module
# 
#  Description:�վݴ�ӡ�趨��module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.05.07
#  version 1.0
#
Module.item=selectChargeCode;
//�����վ����Ͳ�ѯ
//�������ż�ס�� ADM_TYPE
selectChargeCode.Type=TSQL
selectChargeCode.SQL=SELECT CHARGE01,CHARGE02,CHARGE03,CHARGE04,CHARGE05, &
                            CHARGE06,CHARGE07,CHARGE08,CHARGE09,CHARGE10, &
                            CHARGE11,CHARGE12,CHARGE13,CHARGE14,CHARGE15, &
                            CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20 &
                            FROM BIL_RECPPARM  &
                            WHERE ADM_TYPE=<ADM_TYPE>
selectChargeCode.Debug=N



