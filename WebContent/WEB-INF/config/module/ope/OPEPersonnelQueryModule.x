####################################################
#  Title:手术排程查询module
# 
#  Description:手术排程查询module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.12.09
#  version 4.0
####################################################
Module.item=selectData

//查询手术排程
selectData.Type=TSQL
selectData.SQL=SELECT &
                //=========================modify by wanglong 20130805 修改OP_ROOM的连表查询条件
                (SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE B.GROUP_ID = 'OPE_OPROOM' AND A.ROOM_NO = B.ID(+)) AS OP_ROOM, &
               A.URGBLADE_FLG,A.OP_DATE,C.PAT_NAME,D.DEPT_CHN_DESC, &
               E.STATION_DESC,F.CHN_DESC AS SEX,C.BIRTH_DATE,C.HEIGHT, &
               //==========================================门诊体重从REG_PATADM中获取，住院体重从ADM_INP中获取 modify by huangjw 20140904
                (SELECT B.WEIGHT  FROM (SELECT CASE_NO, MR_NO, WEIGHT  FROM ADM_INP A  &
                  UNION  &
                  SELECT CASE_NO, MR_NO, WEIGHT  FROM REG_PATADM A) B  WHERE A.CASE_NO = B.CASE_NO(+) AND A.MR_NO = B.MR_NO(+)) &
                AS WEIGHT, &
               //==========================================门诊体重从REG_PATADM中获取，住院体重从ADM_INP中获取 modify by huangjw 20140904
               A.MR_NO,G.ICD_CHN_DESC,H.OPT_CHN_DESC,A.MAIN_SURGEON,A.BOOK_AST_1, &
               A.BOOK_AST_2,A.BOOK_AST_3,A.BOOK_AST_4,A.CIRCULE_USER1,A.CIRCULE_USER2, &
               A.CIRCULE_USER3,A.CIRCULE_USER4,A.SCRUB_USER1,A.SCRUB_USER2,A.SCRUB_USER3, &
               A.SCRUB_USER4,A.ANA_USER1,A.ANA_USER2,A.EXTRA_USER1, & 
               A.EXTRA_USER2,A.REMARK,K.REALDEPT_CODE,K.REALDR_CODE,A.ADM_TYPE, &
               A.TYPE_CODE,L.BED_NO_DESC,'' AS FLG,A.OPBOOK_SEQ,M.CLINIC_DESC, &
               A.APROVE_DATE,A.STATE,A.ANA_CODE &
               //=========================add by wanglong 20121206
               ,A.PART_CODE,A.ISO_FLG &
               //=========================
	       //------------add by sunqy 20140721
	       ,C.IDNO AS ID_NO &
	       //------------
		   //------------add by wangjingchun 20150113 start
		   ,A.MIRROR_FLG,A.STERILE_FLG,A.CASE_NO,A.BED_NO, A.OP_CODE1, A.OP_CODE2 &
		   //------------add by wangjingchun 20150113 end
		   //------------add by yanglu 20181115 start
		   ,A.OP_ORDER_DATE, A.APROVE_DATE AS SCHEDULE_OPT_DATE & 
		   //------------add by yanglu 20181115 end
               FROM OPE_OPBOOK A,SYS_PATINFO C,SYS_DEPT D,SYS_STATION E,SYS_DICTIONARY F, &
               SYS_DIAGNOSIS G,SYS_OPERATIONICD H,REG_PATADM K,SYS_BED L,REG_CLINICAREA M, &
               OPE_OPDETAIL Q &
               WHERE A.MR_NO=C.MR_NO &
               AND A.OP_DEPT_CODE=D.DEPT_CODE &
               AND A.OP_STATION_CODE=E.STATION_CODE(+) &
               AND F.GROUP_ID='SYS_SEX' &
               AND C.SEX_CODE=F.ID(+) &
               AND A.DIAG_CODE1=G.ICD_CODE(+) &
               AND A.OP_CODE1=H.OPERATION_ICD(+) &
               AND A.CASE_NO=K.CASE_NO(+) &
               AND A.BED_NO=L.BED_NO(+) &
               AND A.OP_STATION_CODE=M.CLINICAREA_CODE(+) &
               AND A.OPBOOK_SEQ=Q.OPBOOK_NO(+) &
               ORDER BY A.OP_DATE,OPBOOK_SEQ
selectData.item=DATE_S;DATE_E;MR_NO;TYPE_CODE;ADM_TYPE;REALDEPT_CODE;REALDR_CODE;BOOK_DEPT_CODE;STATE;REGION_CODE;STATE_BEYOND;ISO_FLG;MIRROR_FLG;STERILE_FLG;REV_STATE
selectData.DATE_S=A.OP_DATE>=TO_DATE(<DATE_S>,'YYYYMMDD')
selectData.DATE_E=A.OP_DATE<=TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS')
selectData.MR_NO=A.MR_NO=<MR_NO>
//===============pangben modify 20110630 区域添加
selectData.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selectData.TYPE_CODE=A.TYPE_CODE=<TYPE_CODE>
selectData.ADM_TYPE=A.ADM_TYPE=<ADM_TYPE>
selectData.REALDEPT_CODE=K.REALDEPT_CODE=<REALDEPT_CODE>
selectData.REALDR_CODE=K.REALDR_CODE=<REALDR_CODE>
selectData.BOOK_DEPT_CODE=A.BOOK_DEPT_CODE=<BOOK_DEPT_CODE>
selectData.STATE=A.STATE=<STATE>
selectData.ISO_FLG=A.ISO_FLG=<ISO_FLG>
selectData.MIRROR_FLG=A.MIRROR_FLG=<MIRROR_FLG>
selectData.STERILE_FLG=A.STERILE_FLG=<STERILE_FLG>
//wanglong add 20140422
selectData.STATE_BEYOND=A.STATE><STATE_BEYOND>
//wanglong add 20150330
selectData.REV_STATE=A.STATE<><REV_STATE>
selectData.Debug=N