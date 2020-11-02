# 
#  Title:�ű�module
# 
#  Description:�ű�module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=queryTree;selectdata;deletedata;insertdata;updatedata;existsClinicType;initclinictype;getDescByCode;selProfFlg

//��ѯ�ű�
queryTree.Type=TSQL
queryTree.SQL=SELECT ADM_TYPE,CLINICTYPE_CODE,CLINICTYPE_DESC,PY1,PY2,&
		     SEQ,DESCRIPTION,PROF_FLG,OPT_USER,OPT_DATE,&
		     OPT_TERM &
		FROM REG_CLINICTYPE &
	    ORDER BY CLINICTYPE_CODE,SEQ
queryTree.Debug=N


//��ѯ�ż���,�ű�,�ű�˵��,ƴ����,ע����,˳����,��ע,ר����ע��,������Ա,��������,�����ն�
selectdata.Type=TSQL
selectdata.SQL=SELECT ADM_TYPE,CLINICTYPE_CODE,CLINICTYPE_DESC,PY1,PY2,&
		      SEQ,DESCRIPTION,PROF_FLG,OPT_USER,OPT_DATE,&
		      OPT_TERM &
		 FROM REG_CLINICTYPE &
	     ORDER BY SEQ
selectdata.item=ADM_TYPE;CLINICTYPE_CODE
selectdata.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
selectdata.CLINICTYPE_CODE=CLINICTYPE_CODE=<CLINICTYPE_CODE>
selectdata.Debug=N

//ɾ���ż���,�ű�,�ű�˵��,ƴ����,ע����,˳����,��ע,ר����ע��,������Ա,��������,�����ն�
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM REG_CLINICTYPE &
		WHERE ADM_TYPE = <ADM_TYPE> &
		  AND CLINICTYPE_CODE = <CLINICTYPE_CODE>
deletedata.Debug=N

//�����ż���,�ű�,�ű�˵��,ƴ����,ע����,˳����,��ע,ר����ע��,������Ա,��������,�����ն�
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_CLINICTYPE &
			   (ADM_TYPE,CLINICTYPE_CODE,CLINICTYPE_DESC, PY1,PY2,&
			   SEQ,DESCRIPTION,PROF_FLG,OPT_USER,OPT_DATE,&
			   OPT_TERM) &
		    VALUES (<ADM_TYPE>,<CLINICTYPE_CODE>,<CLINICTYPE_DESC>,<PY1>,<PY2>,&
		    	   <SEQ>,<DESCRIPTION>,<PROF_FLG>,<OPT_USER>,SYSDATE,&
		    	   <OPT_TERM>)
insertdata.Debug=N

//�����ż���,�ű�,�ű�˵��,ƴ����,ע����,˳����,��ע,ר����ע��,������Ա,��������,�����ն�
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_CLINICTYPE &
		  SET ADM_TYPE=<ADM_TYPE>,CLINICTYPE_CODE=<CLINICTYPE_CODE>,CLINICTYPE_DESC=<CLINICTYPE_DESC>,&
		      PY1=<PY1>,PY2=<PY2>,SEQ=<SEQ>,&
		      DESCRIPTION=<DESCRIPTION>,PROF_FLG=<PROF_FLG>,OPT_USER=<OPT_USER>,&
		      OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
                WHERE ADM_TYPE = <ADM_TYPE> &
                  AND CLINICTYPE_CODE = <CLINICTYPE_CODE>
updatedata.Debug=N

//�Ƿ���ںű�
existsClinicType.type=TSQL
existsClinicType.SQL=SELECT COUNT(*) AS COUNT &
		       FROM REG_CLINICTYPE &
		      WHERE ADM_TYPE = <ADM_TYPE> &
		        AND CLINICTYPE_CODE = <CLINICTYPE_CODE>

//�õ��ű�����
initclinictype.Type=TSQL
initclinictype.SQL=SELECT CLINICTYPE_CODE AS ID,CLINICTYPE_DESC AS NAME ,ENNAME,PY1,PY2 &
		     FROM REG_CLINICTYPE &
		 ORDER BY CLINICTYPE_CODE,SEQ
initclinictype.Debug=N

//���ݺű�CODEȡ�úű�DESC
getDescByCode.Type=TSQL
getDescByCode.SQL=SELECT CLINICTYPE_DESC &
		    FROM REG_CLINICTYPE &
		   WHERE CLINICTYPE_CODE=<CLINICTYPE_CODE>
getDescByCode.Debug=N

//���ݺű�CODEȡ�úű�DESC
selProfFlg.Type=TSQL
selProfFlg.SQL=SELECT PROF_FLG &
		    FROM REG_CLINICTYPE &
		   WHERE CLINICTYPE_CODE=<CLINICTYPE_CODE>
selProfFlg.Debug=N