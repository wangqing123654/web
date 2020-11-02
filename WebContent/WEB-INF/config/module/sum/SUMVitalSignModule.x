Module.item=checkIsExist;selectExmUser;selectOneDayDtl;selectOneDayMst;selectVitalSignDtl;selectVitalSign;insertVitalSign;insertVitalSignDtl;updateVitalSign;updateVitalSignDtl;updateHWForAdminp;updateCorNVitalSign;insertCorNVitalSign;getWeight



//检查该时间的数据是否已经存在，并且是否已经作废
checkIsExist.Type=TSQL

checkIsExist.SQL=SELECT COUNT(DISPOSAL_FLG) AS COUNT,DISPOSAL_FLG &
		
		FROM SUM_VITALSIGN &
		
		WHERE  ADM_TYPE = <ADM_TYPE> &
		
		AND CASE_NO = <CASE_NO> &
		
		AND EXAMINE_DATE = <EXAMINE_DATE> &
		
		GROUP BY DISPOSAL_FLG
		
checkIsExist.Debug=N


//查询检查时间和人员
selectExmUser.Type=TSQL

selectExmUser.SQL=SELECT EXAMINE_DATE, B.USER_NAME AS USER_ID,A.INHOSPITALDAYS &
		
		FROM SUM_VITALSIGN A , SYS_OPERATOR B &
		
		WHERE  A.ADM_TYPE = <ADM_TYPE> &
		
		AND A.CASE_NO = <CASE_NO> &
		
		AND A.DISPOSAL_FLG IS NULL &
		
		AND A.USER_ID=B.USER_ID(+) &
		
		ORDER BY A.EXAMINE_DATE
		
selectExmUser.Debug=N



//查询某一天的数据SUM_VTSNTPRDTL
selectOneDayDtl.Type=TSQL

selectOneDayDtl.SQL=SELECT   * &

  		FROM   SUM_VTSNTPRDTL A, SUM_VITALSIGN B &
  
 		WHERE  A.ADM_TYPE = <ADM_TYPE> &
 
         	AND A.CASE_NO = <CASE_NO> &
         
         	AND A.EXAMINE_DATE = <EXAMINE_DATE> &
         
         	AND A.ADM_TYPE = B.ADM_TYPE &
         
         	AND A.CASE_NO = B.CASE_NO &
         
         	AND A.EXAMINE_DATE = B.EXAMINE_DATE &
         
         	AND B.DISPOSAL_FLG IS NULL &
         	
         	ORDER BY EXAMINESESSION
         	
selectOneDayDtl.Debug=N


//查询某一天的数据--主表SUM_VITALSIGN
selectOneDayMst.Type=TSQL

selectOneDayMst.SQL=SELECT   * &

  		FROM   SUM_VITALSIGN &
  
 		WHERE  ADM_TYPE = <ADM_TYPE> &
 
         	AND CASE_NO = <CASE_NO> &
         
         	AND EXAMINE_DATE = <EXAMINE_DATE> &
         
         	AND DISPOSAL_FLG IS NULL 
         	
selectOneDayMst.Debug=N

//体温细表--SUM_VTSNTPRDTL
selectVitalSignDtl.Type=TSQL

selectVitalSignDtl.SQL=SELECT   A.EXAMINE_DATE||A.EXAMINESESSION AS EXAMINEFLG , A.TEMPERATURE , A.PLUSE , A.RESPIRE , A.NOTPRREASONCODE , A.PHYSIATRICS ,&
		
		A.SYSTOLICPRESSURE , A.DIASTOLICPRESSURE ,  &

  		F.CHN_DESC AS PTMOVECATECODE , A.PTMOVECATEDESC, A.HEART_RATE,A.TMPTRKINDCODE &
  		
  		FROM    SUM_VTSNTPRDTL A LEFT JOIN SYS_DICTIONARY F ON F.GROUP_ID='SUM_VTSNPTMOVECATE' AND A.PTMOVECATECODE = F.ID ,&
		  			
  			SUM_VITALSIGN B &
  		
		         	
         	WHERE   A.ADM_TYPE = <ADM_TYPE> &
				
		AND A.CASE_NO = <CASE_NO> &
				
		AND TO_DATE (A.EXAMINE_DATE, 'YYYYMMDD') BETWEEN <START_DATE> AND <END_DATE> &
		         	
		AND A.ADM_TYPE=B.ADM_TYPE &
		         	
		AND A.CASE_NO=B.CASE_NO &
				
		AND A.EXAMINE_DATE=B.EXAMINE_DATE &
				
		AND B.DISPOSAL_FLG IS NULL &
		        	
        	ORDER BY A.EXAMINE_DATE
         	
selectVitalSignDtl.Debug=N


//新生儿体温单--主表SUM_VITALSIGN
selectVitalSign.Type=TSQL

selectVitalSign.SQL=  SELECT   A.EXAMINE_DATE ,A.OPE_DAYS ,A.OUTPUTURINEQTY ,A.STOOL ,A.INTAKEFLUIDQTY ,A.HEIGHT ,A.WEIGHT, &

                               B.CHN_DESC USER_DEFINE_1,USER_DEFINE_1_VALUE,USER_DEFINE_1 USER_DEFINE_CODE_1, &
                               
                               C.CHN_DESC USER_DEFINE_2,USER_DEFINE_2_VALUE,USER_DEFINE_2 USER_DEFINE_CODE_2, &
                               
                               D.CHN_DESC USER_DEFINE_3,USER_DEFINE_3_VALUE,USER_DEFINE_3 USER_DEFINE_CODE_3, &
                               
                               A.DRAINAGE,A.ENEMA,A.AUTO_STOOL,A.URINETIMES,&

			       A.VOMIT,A.HEAD_CIRCUM,A.ABDOMEN_CIRCUM,A.WEIGHT_G,&
			       
			       A.WEIGHT_REASON,A.STOOLX,A.STOOLX_NUM,A.STOOLW,A.STOOLW_NUM,&
			       
			       A.STOOLB,A.E_STOOL,A.E_STOOLX,A.E_STOOLX_NUM,A.E_STOOLW,  &
			       
			       A.E_STOOLW_NUM,A.E_STOOLB,A.URINETIMES_NUM,A.BM_FLG &

   		FROM   SUM_VITALSIGN A LEFT JOIN SYS_DICTIONARY B ON B.GROUP_ID = 'SUM_USERDEFINE' AND A.USER_DEFINE_1 = B.ID &
   		
   		                       LEFT JOIN SYS_DICTIONARY C ON C.GROUP_ID = 'SUM_USERDEFINE' AND A.USER_DEFINE_2 = C.ID &
   		                       
   		                       LEFT JOIN SYS_DICTIONARY D ON D.GROUP_ID = 'SUM_USERDEFINE' AND A.USER_DEFINE_3 = D.ID &
   		
   		WHERE  A.ADM_TYPE = <ADM_TYPE> &
   		
           	AND A.CASE_NO = <CASE_NO> &
           	
           	AND TO_DATE (A.EXAMINE_DATE, 'YYYYMMDD') BETWEEN <START_DATE> AND <END_DATE> &
           	
           	AND A.DISPOSAL_FLG IS NULL &
           	           	
		ORDER BY   A.EXAMINE_DATE
         	
selectVitalSign.Debug=N


//体温单主表插入--主表SUM_VITALSIGN
insertVitalSign.Type=TSQL

insertVitalSign.SQL=INSERT INTO SUM_VITALSIGN ( &
		
		ADM_TYPE,CASE_NO,EXAMINE_DATE,IPD_NO, &
		
		MR_NO,INHOSPITALDAYS,OPE_DAYS,HEIGHT,WEIGHT, &
		
		OUTPUTURINEQTY,STOOL,INTAKEFLUIDQTY,USER_ID, &
		
		USER_DEFINE_1,USER_DEFINE_1_VALUE,USER_DEFINE_2,USER_DEFINE_2_VALUE, &
		
		USER_DEFINE_3,USER_DEFINE_3_VALUE,OPT_USER,OPT_DATE,OPT_TERM, &
		
		AUTO_STOOL,ENEMA,DRAINAGE,URINETIMES,&

	        VOMIT,HEAD_CIRCUM,ABDOMEN_CIRCUM,WEIGHT_G,&
			       
	        WEIGHT_REASON) &
		
		VALUES (<ADM_TYPE>,<CASE_NO>,<EXAMINE_DATE>,<IPD_NO>, &
		
		<MR_NO>,<INHOSPITALDAYS>,<OPE_DAYS>,<HEIGHT>,<WEIGHT>, &
		
		<OUTPUTURINEQTY>,<STOOL>,<INTAKEFLUIDQTY>,<USER_ID>, &
		
		<USER_DEFINE_1>,<USER_DEFINE_1_VALUE>,<USER_DEFINE_2>,<USER_DEFINE_2_VALUE>, &
		
		<USER_DEFINE_3>,<USER_DEFINE_3_VALUE>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>, &
		
		<AUTO_STOOL>,<ENEMA>,<DRAINAGE>,<URINETIMES>,&

	        <VOMIT>,<HEAD_CIRCUM>,<ABDOMEN_CIRCUM>,<WEIGHT_G>,&
			       
	        <WEIGHT_REASON> )
		
insertVitalSign.Debug=N


//体温单细表插入--主表SUM_VTSNTPRDTL
insertVitalSignDtl.Type=TSQL

insertVitalSignDtl.SQL=INSERT INTO SUM_VTSNTPRDTL ( &

		ADM_TYPE,CASE_NO,EXAMINE_DATE,EXAMINESESSION,PHYSIATRICS, &
		
		RECTIME,SPCCONDCODE,TMPTRKINDCODE,TEMPERATURE,PLUSE, &
		
		RESPIRE,SYSTOLICPRESSURE,DIASTOLICPRESSURE,NOTPRREASONCODE,PTMOVECATECODE, &
		
		PTMOVECATEDESC,USER_ID,OPT_USER,OPT_DATE,OPT_TERM,HEART_RATE &

		) &
		
		VALUES (<ADM_TYPE>,<CASE_NO>,<EXAMINE_DATE>,<EXAMINESESSION>,<PHYSIATRICS>, &
		
		<RECTIME>,<SPCCONDCODE>,<TMPTRKINDCODE>,<TEMPERATURE>,<PLUSE>,&
		
		<RESPIRE>,<SYSTOLICPRESSURE>,<DIASTOLICPRESSURE>,<NOTPRREASONCODE>,<PTMOVECATECODE>,&
		
		<PTMOVECATEDESC>,<USER_ID>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<HEART_RATE> &
		
		)
		
insertVitalSignDtl.Debug=N


//体温单主表更新--主表SUM_NEWARRIVALSIGN
updateVitalSign.Type=TSQL

updateVitalSign.SQL=UPDATE SUM_VITALSIGN  SET &

		DISPOSAL_FLG=NULL,DISPOSAL_REASON=NULL, &

		HEIGHT=<HEIGHT>,WEIGHT=<WEIGHT>, &
				
		OUTPUTURINEQTY=<OUTPUTURINEQTY>,STOOL=<STOOL>,INTAKEFLUIDQTY=<INTAKEFLUIDQTY>,USER_ID=<USER_ID>, &
				
		USER_DEFINE_1=<USER_DEFINE_1>,USER_DEFINE_1_VALUE=<USER_DEFINE_1_VALUE>,USER_DEFINE_2=<USER_DEFINE_2>,USER_DEFINE_2_VALUE=<USER_DEFINE_2_VALUE>, &
				
		USER_DEFINE_3=<USER_DEFINE_3>,USER_DEFINE_3_VALUE=<USER_DEFINE_3_VALUE>,OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM>,&

		AUTO_STOOL =<AUTO_STOOL>,ENEMA = <ENEMA>,OPE_DAYS = <OPE_DAYS>,DRAINAGE = <DRAINAGE>,URINETIMES=<URINETIMES>,&

	        VOMIT=<VOMIT>,HEAD_CIRCUM=<HEAD_CIRCUM>,ABDOMEN_CIRCUM=<ABDOMEN_CIRCUM>,WEIGHT_G=<WEIGHT_G>,&
			       
	        WEIGHT_REASON=<WEIGHT_REASON> &
		
		WHERE ADM_TYPE=<ADM_TYPE> &
		
		AND CASE_NO=<CASE_NO> &
		
		AND EXAMINE_DATE=<EXAMINE_DATE>		
		
updateVitalSign.Debug=N


//新生儿体温单细表更新--主表SUM_VTSNTPRDTL
updateVitalSignDtl.Type=TSQL

updateVitalSignDtl.SQL=UPDATE SUM_VTSNTPRDTL SET &

		PHYSIATRICS=<PHYSIATRICS>,RECTIME=<RECTIME>,SPCCONDCODE=<SPCCONDCODE>,TMPTRKINDCODE=<TMPTRKINDCODE>,TEMPERATURE=<TEMPERATURE>,PLUSE=<PLUSE>, &
				
		RESPIRE=<RESPIRE>,SYSTOLICPRESSURE=<SYSTOLICPRESSURE>,DIASTOLICPRESSURE=<DIASTOLICPRESSURE>,NOTPRREASONCODE=<NOTPRREASONCODE>,PTMOVECATECODE=<PTMOVECATECODE>, &
				
		PTMOVECATEDESC=<PTMOVECATEDESC>,USER_ID=<USER_ID>,OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM>,HEART_RATE = <HEART_RATE> &	
		
		WHERE ADM_TYPE=<ADM_TYPE> &
				
		AND CASE_NO=<CASE_NO> &
				
		AND EXAMINE_DATE=<EXAMINE_DATE> &
		
		AND EXAMINESESSION=<EXAMINESESSION>
		
updateVitalSignDtl.Debug=N


//体温单模块回写住院ADM_INP身高体重
updateHWForAdminp.Type=TSQL

updateHWForAdminp.SQL=UPDATE ADM_INP SET &

		HEIGHT=<HEIGHT>,WEIGHT=<WEIGHT> &
				
		WHERE CASE_NO=<CASE_NO>
		
updateHWForAdminp.Debug=N


//儿童体温单主表插入--主表SUM_VITALSIGN
insertCorNVitalSign.Type=TSQL

insertCorNVitalSign.SQL=INSERT INTO SUM_VITALSIGN ( &
		
		ADM_TYPE,CASE_NO,EXAMINE_DATE,IPD_NO, &
		
		MR_NO,INHOSPITALDAYS,OPE_DAYS,HEIGHT,WEIGHT, &
		
		OUTPUTURINEQTY,STOOL,INTAKEFLUIDQTY,USER_ID, &
		
		USER_DEFINE_1,USER_DEFINE_1_VALUE,USER_DEFINE_2,USER_DEFINE_2_VALUE, &
		
		USER_DEFINE_3,USER_DEFINE_3_VALUE,OPT_USER,OPT_DATE,OPT_TERM, &
		
		ENEMA,DRAINAGE,URINETIMES,&

	        VOMIT,HEAD_CIRCUM,ABDOMEN_CIRCUM,WEIGHT_G,&
			       
	        WEIGHT_REASON,STOOLX,STOOLX_NUM,STOOLW,STOOLW_NUM,&

		STOOLB,E_STOOL,E_STOOLX,E_STOOLX_NUM,E_STOOLW,&

		E_STOOLW_NUM,E_STOOLB,URINETIMES_NUM,BM_FLG) &
		
		VALUES (<ADM_TYPE>,<CASE_NO>,<EXAMINE_DATE>,<IPD_NO>, &
		
		<MR_NO>,<INHOSPITALDAYS>,<OPE_DAYS>,<HEIGHT>,<WEIGHT>, &
		
		<OUTPUTURINEQTY>,<STOOL>,<INTAKEFLUIDQTY>,<USER_ID>, &
		
		<USER_DEFINE_1>,<USER_DEFINE_1_VALUE>,<USER_DEFINE_2>,<USER_DEFINE_2_VALUE>, &
		
		<USER_DEFINE_3>,<USER_DEFINE_3_VALUE>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>, &
		
		<ENEMA>,<DRAINAGE>,<URINETIMES>,&

	        <VOMIT>,<HEAD_CIRCUM>,<ABDOMEN_CIRCUM>,<WEIGHT_G>,&
			       
	        <WEIGHT_REASON>,<STOOLX>,<STOOLX_NUM>,<STOOLW>,<STOOLW_NUM>,&

		<STOOLB>,<E_STOOL>,<E_STOOLX>,<E_STOOLX_NUM>,<E_STOOLW>,&

		<E_STOOLW_NUM>,<E_STOOLB>,<URINETIMES_NUM>,<BM_FLG>)
		
insertCorNVitalSign.Debug=N
//儿童体温单主表更新--主表SUM_NEWARRIVALSIGN
updateCorNVitalSign.Type=TSQL

updateCorNVitalSign.SQL=UPDATE SUM_VITALSIGN  SET &

		DISPOSAL_FLG=NULL,DISPOSAL_REASON=NULL, &

		HEIGHT=<HEIGHT>,WEIGHT=<WEIGHT>, &
				
		OUTPUTURINEQTY=<OUTPUTURINEQTY>,STOOL=<STOOL>,INTAKEFLUIDQTY=<INTAKEFLUIDQTY>,USER_ID=<USER_ID>, &
				
		USER_DEFINE_1=<USER_DEFINE_1>,USER_DEFINE_1_VALUE=<USER_DEFINE_1_VALUE>,USER_DEFINE_2=<USER_DEFINE_2>,USER_DEFINE_2_VALUE=<USER_DEFINE_2_VALUE>, &
				
		USER_DEFINE_3=<USER_DEFINE_3>,USER_DEFINE_3_VALUE=<USER_DEFINE_3_VALUE>,OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM>,&

		ENEMA = <ENEMA>,OPE_DAYS = <OPE_DAYS>,DRAINAGE = <DRAINAGE>,URINETIMES=<URINETIMES>,&

	        VOMIT=<VOMIT>,HEAD_CIRCUM=<HEAD_CIRCUM>,ABDOMEN_CIRCUM=<ABDOMEN_CIRCUM>,WEIGHT_G=<WEIGHT_G>,&

		STOOLX=<STOOLX>,STOOLX_NUM=<STOOLX_NUM>,STOOLW=<STOOLW>,STOOLW_NUM=<STOOLW_NUM>,&

		STOOLB=<STOOLB>,E_STOOL=<E_STOOL>,E_STOOLX=<E_STOOLX>,E_STOOLX_NUM=<E_STOOLX_NUM>,E_STOOLW=<E_STOOLW>,&

		E_STOOLW_NUM=<E_STOOLW_NUM>,E_STOOLB=<E_STOOLB>,URINETIMES_NUM=<URINETIMES_NUM>,BM_FLG=<BM_FLG>,&
			       
	        WEIGHT_REASON=<WEIGHT_REASON> &
		
		WHERE ADM_TYPE=<ADM_TYPE> &
		
		AND CASE_NO=<CASE_NO> &
		
		AND EXAMINE_DATE=<EXAMINE_DATE>		
		
updateCorNVitalSign.Debug=N


//获取新生儿的出生体重
getWeight.Type=TSQL
getWeight.SQL=SELECT  WEIGHT , WEIGHT_G FROM SUM_VITALSIGN &
		             WHERE  CASE_NO = <CASE_NO> &
		               AND  ADM_TYPE = <ADM_TYPE>  &
		               ORDER BY TO_DATE(EXAMINE_DATE,'YYYYMMDD')	
getWeight.Debug=N