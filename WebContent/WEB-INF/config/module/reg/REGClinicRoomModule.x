# 
#  Title:����module
# 
#  Description:����module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=queryTree;selectdata;deletedata;insertdata;updatedata;existsClinicRoom;initclinicroomno;initclinicroomreg;getClinicRoomForAdmType;&
		getAreaByRoom;getOrgCodeByRoomNo;getOrgByODO;selActiveFlg;getRoomNo

//����Ժ�����룬�������ڣ�����ʱ�Σ��ż������ҽʦ��ѯ��϶�Ӧ��ҩ��
getOrgByODO.Type=TSQL
getOrgByODO.SQL=SELECT A.ORG_CODE &
		  FROM REG_CLINICROOM A,REG_SCHDAY B &
		 WHERE B.REGION_CODE=<REGION_CODE> &
		   AND B.ADM_DATE=<ADM_DATE> &
		   AND B.SESSION_CODE=<SESSION_CODE> &
		   AND B.ADM_TYPE=<ADM_TYPE> &
		   AND B.REALDR_CODE=<REALDR_CODE> &
		   AND B.CLINICROOM_NO=A.CLINICROOM_NO
getOrgByODO.Debug=N

//��ѯ���
queryTree.Type=TSQL
queryTree.SQL=SELECT CLINICROOM_NO,CLINICROOM_DESC,PY1,PY2,SEQ,&
		     DESCRIPTION,ADM_TYPE,REGION_CODE,CLINICAREA_CODE,LOCATION,&
		     ORG_CODE,ACTIVE_FLG,OPT_USER,OPT_DATE,OPT_TERM &
		FROM REG_CLINICROOM &
	       WHERE CLINICROOM_NO=<CLINICROOM_NO> &
	    ORDER BY CLINICROOM_NO
queryTree.Debug=N

//��ѯ����,����˵��,ƴ����,ע����,˳����,��ע,�ż���,��Ժ������,������,���λ��,Ԥ��ҩ��,���ñ��,������Ա,��������,�����ն�
selectdata.Type=TSQL
selectdata.SQL=SELECT CLINICROOM_NO,CLINICROOM_DESC,PY1,PY2,SEQ,&
		      DESCRIPTION,ADM_TYPE,REGION_CODE,CLINICAREA_CODE,LOCATION,&
		      ORG_CODE,ACTIVE_FLG,PHA_REGION_CODE,OPT_USER,OPT_DATE,OPT_TERM &
		 FROM REG_CLINICROOM &
	     ORDER BY CLINICROOM_NO
selectdata.item=CLINICROOM_NO;CLINICAREA_CODE
selectdata.CLINICROOM_NO=CLINICROOM_NO=<CLINICROOM_NO>
selectdata.CLINICAREA_CODE=CLINICAREA_CODE=<CLINICAREA_CODE>
selectdata.Debug=N

//ɾ������,����˵��,ƴ����,ע����,˳����,��ע,�ż���,��Ժ������,������,���λ��,Ԥ��ҩ��,���ñ��,������Ա,��������,�����ն�
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM REG_CLINICROOM &
		     WHERE CLINICROOM_NO=<CLINICROOM_NO>
deletedata.Debug=N

//��������,����˵��,ƴ����,ע����,˳����,��ע,�ż���,��Ժ������,������,���λ��,Ԥ��ҩ��,���ñ��,������Ա,��������,�����ն�
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_CLINICROOM &
			   (CLINICROOM_NO,CLINICROOM_DESC,PY1,PY2,SEQ,&
			   DESCRIPTION,ADM_TYPE,REGION_CODE,CLINICAREA_CODE,LOCATION,&
			   ORG_CODE,ACTIVE_FLG,OPT_USER,OPT_DATE,OPT_TERM) &
		    VALUES (<CLINICROOM_NO>,<CLINICROOM_DESC>,<PY1>,<PY2>,<SEQ>,&
			   <DESCRIPTION>,<ADM_TYPE>,<REGION_CODE>,<CLINICAREA_CODE>,<LOCATION>,&
			   <ORG_CODE>,<ACTIVE_FLG>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertdata.Debug=N            

//��������,����˵��,ƴ����,ע����,˳����,��ע,�ż���,��Ժ������,������,���λ��,Ԥ��ҩ��,���ñ��,������Ա,��������,�����ն�
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_CLINICROOM &
		  SET CLINICROOM_NO=<CLINICROOM_NO>,CLINICROOM_DESC=<CLINICROOM_DESC>,PY1=<PY1>,&
		      PY2=<PY2>,SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,&
		      ADM_TYPE=<ADM_TYPE>,REGION_CODE=<REGION_CODE>,CLINICAREA_CODE=<CLINICAREA_CODE>,&
		      LOCATION=<LOCATION>,ORG_CODE=<ORG_CODE>,ACTIVE_FLG=<ACTIVE_FLG>,&
		      OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		WHERE CLINICROOM_NO=<CLINICROOM_NO>						
updatedata.Debug=N

//�Ƿ�������
existsClinicRoom.type=TSQL
existsClinicRoom.SQL=SELECT COUNT(*) AS COUNT &
		       FROM REG_CLINICROOM &
		      WHERE CLINICROOM_NO=<CLINICROOM_NO>

//ȡ������combo��Ϣ
initclinicroomno.Type=TSQL
initclinicroomno.SQL=SELECT CLINICROOM_NO AS ID,CLINICROOM_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 &
		       FROM REG_CLINICROOM &
		      WHERE ACTIVE_FLG = 'Y' &
		   ORDER BY CLINICROOM_NO,SEQ
initclinicroomno.item=CLINICAREA_CODE;REGION_CODE_ALL
initclinicroomno.CLINICAREA_CODE=CLINICAREA_CODE=<CLINICAREA_CODE>
initclinicroomno.REGION_CODE_ALL=REGION_CODE=<REGION_CODE_ALL>
initclinicroomno.Debug=N

//�õ�����(������)
initclinicroomreg.Type=TSQL
initclinicroomreg.SQL=SELECT DISTINCT A.CLINICROOM_NO AS ID,B.CLINICROOM_DESC AS NAME,B.ENG_DESC AS ENNAME,B.PY1 AS PY1, B.PY2 AS PY2 &
			FROM REG_SCHDAY A,REG_CLINICROOM B &
		       WHERE B.ACTIVE_FLG = 'Y' &
		         AND A.CLINICROOM_NO = B.CLINICROOM_NO &
		    ORDER BY A.CLINICROOM_NO
initclinicroomreg.item=REGION_CODE;ADM_TYPE;SESSION_CODE;ADM_DATE;REGION_CODE_ALL
initclinicroomreg.REGION_CODE=A.REGION_CODE=<REGION_CODE>
initclinicroomreg.ADM_TYPE=A.ADM_TYPE=<ADM_TYPE>
initclinicroomreg.SESSION_CODE=A.SESSION_CODE=<SESSION_CODE>
initclinicroomreg.ADM_DATE=A.ADM_DATE=<ADM_DATE>
initclinicroomreg.REGION_CODE_ALL=B.REGION_CODE=<REGION_CODE_ALL>
initclinicroomreg.Debug=N

//�õ����������ң�For ONW��
getClinicRoomForAdmType.Type=TSQL
getClinicRoomForAdmType.SQL=SELECT CLINICAREA_CODE,CLINICROOM_NO,REGION_CODE &
			      FROM REG_CLINICROOM &
			     WHERE ACTIVE_FLG = 'Y' &
			       AND ADM_TYPE=<ADM_TYPE>
getClinicRoomForAdmType.item=CLINICAREA_CODE;REGION_CODE
getClinicRoomForAdmType.CLINICAREA_CODE=CLINICAREA_CODE=<CLINICAREA_CODE>
getClinicRoomForAdmType.REGION_CODE=REGION_CODE=<REGION_CODE>
getClinicRoomForAdmType.Debug=N

//�������ҵõ�������For REG��
getAreaByRoom.Type=TSQL
getAreaByRoom.SQL=SELECT CLINICAREA_CODE &
		    FROM REG_CLINICROOM &
		   WHERE CLINICROOM_NO=<CLINICROOM_NO>
getAreaByRoom.Debug=N

//�������ҵõ�ҩ����For OPD��
getOrgCodeByRoomNo.Type=TSQL
getOrgCodeByRoomNo.SQL=SELECT ORG_CODE &
			 FROM REG_CLINICROOM &
			WHERE CLINICROOM_NO=<CLINICROOM_NO>
getOrgCodeByRoomNo.Debug=N

//�����Ƿ�����
selActiveFlg.Type=TSQL
selActiveFlg.SQL=SELECT ACTIVE_FLG &
		   FROM REG_CLINICROOM &
		  WHERE CLINICROOM_NO = <CLINICROOM_NO>
selActiveFlg.Debug=N
//�õ����Һ���
getRoomNo.Type=TSQL
getRoomNo.SQL=SELECT CLINICROOM_NO FROM REG_SCHDAY &
					WHERE REGION_CODE=<REGION_CODE> &
				 	  AND ADM_TYPE=<ADM_TYPE> &
				 	  AND ADM_DATE=<ADM_DATE> &
				 	  AND SESSION_CODE=<SESSION_CODE> &
				 	  AND DEPT_CODE=<DEPT_CODE> &
				 	  AND DR_CODE=<DR_CODE> 
getRoomNo.Debug=N
