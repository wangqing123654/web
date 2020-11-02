Module.item=selectNewArrivalSignDtl;selectNewArrivalSign;selectNewArrExmUser;selectOneDayDtl;selectOneDayMst;insertNewArrival;insertNewArrivalDtl;checkIsExist;updateNewArrival;updateNewArrivalDtl;getNewAdmWeight;getNewBornWeight;getFirstDayWeight



//检查时间的数据是否已经存在，并且是否已经作废
checkIsExist.Type=TSQL

checkIsExist.SQL=SELECT COUNT(DISPOSAL_FLG) AS COUNT,DISPOSAL_FLG &
		
		FROM SUM_NEWARRIVALSIGN &
		
		WHERE  ADM_TYPE = <ADM_TYPE> &
		
		AND CASE_NO = <CASE_NO> &
		
		AND EXAMINE_DATE = <EXAMINE_DATE> &
		
		GROUP BY DISPOSAL_FLG
		
checkIsExist.Debug=N



//查询检查时间和人员
selectNewArrExmUser.Type=TSQL

selectNewArrExmUser.SQL=SELECT EXAMINE_DATE, B.USER_NAME AS USER_ID &
		
		FROM SUM_NEWARRIVALSIGN A , SYS_OPERATOR B &
		
		WHERE  A.ADM_TYPE = <ADM_TYPE> &
		
		AND A.CASE_NO = <CASE_NO> &
		
		AND A.DISPOSAL_FLG IS NULL &
		
		AND A.USER_ID=B.USER_ID(+) &
		
		ORDER BY A.EXAMINE_DATE
		
selectNewArrExmUser.Debug=N


//查询某一天的数据SUM_NEWARRIVALSIGNDTL
selectOneDayDtl.Type=TSQL

selectOneDayDtl.SQL=SELECT   A.* &

  		FROM   SUM_NEWARRIVALSIGNDTL A, SUM_NEWARRIVALSIGN B &
  
 		WHERE  A.ADM_TYPE = <ADM_TYPE> &
 
         	AND A.CASE_NO = <CASE_NO> &
         
         	AND A.EXAMINE_DATE = <EXAMINE_DATE> &
         
         	AND A.ADM_TYPE = B.ADM_TYPE &
         
         	AND A.CASE_NO = B.CASE_NO &
         
         	AND A.EXAMINE_DATE = B.EXAMINE_DATE &
         
         	AND B.DISPOSAL_FLG IS NULL &
         	
         	ORDER BY EXAMINESESSION
         	
selectOneDayDtl.Debug=N


//查询某一天的数据--主表SUM_NEWARRIVALSIGN
selectOneDayMst.Type=TSQL

selectOneDayMst.SQL=SELECT   * &

  		FROM   SUM_NEWARRIVALSIGN &
  
 		WHERE  ADM_TYPE = <ADM_TYPE> &
 
         	AND CASE_NO = <CASE_NO> &
         
         	AND EXAMINE_DATE = <EXAMINE_DATE> &
         
         	AND DISPOSAL_FLG IS NULL 
         	
selectOneDayMst.Debug=N





//新生儿体温单--细表SUM_NEWARRIVALSIGNDTL
selectNewArrivalSignDtl.Type=TSQL

selectNewArrivalSignDtl.SQL=SELECT   A.EXAMINE_DATE||A.EXAMINESESSION AS EXAMINEFLG , A.TEMPERATURE , A.WEIGHT , A.NOTPRREASONCODE , A.PHYSIATRICS ,&
				
		F.CHN_DESC AS PTMOVECATECODE , A.PTMOVECATEDESC &

  		FROM    SUM_NEWARRIVALSIGNDTL A LEFT JOIN SYS_DICTIONARY F ON F.GROUP_ID='SUM_VTSNPTMOVECATE' AND A.PTMOVECATECODE = F.ID ,&
  			
  			SUM_NEWARRIVALSIGN B &
  		
		WHERE   A.ADM_TYPE = <ADM_TYPE> &
		
		AND A.CASE_NO = <CASE_NO> &
		
         	AND TO_DATE (A.EXAMINE_DATE, 'YYYYMMDD') BETWEEN <START_DATE> AND <END_DATE> &
         	
         	AND A.ADM_TYPE=B.ADM_TYPE &
         	
		AND A.CASE_NO=B.CASE_NO &
		
		AND A.EXAMINE_DATE=B.EXAMINE_DATE &
		
        	AND B.DISPOSAL_FLG IS NULL &
        	
        	ORDER BY A.EXAMINE_DATE
         	
selectNewArrivalSignDtl.Debug=N


//新生儿体温单--主表SUM_NEWARRIVALSIGN
selectNewArrivalSign.Type=TSQL

selectNewArrivalSign.SQL=SELECT   A.EXAMINE_DATE, A.BORNWEIGHT, A.URINETIMES, A.DRAINTIMES, C.QUALITY_DESC AS DRAINQUALITY,A.WEIGHT,&
		
		A.DRINKQTY, D.FEEDWAY_DESC AS FEEDWAY, A.ADDDARIYQTY, A.VOMIT, B.BATHEDWAY_DESC AS BATHEDWAY, A.EYE, A.EAR_NOSE, E.UNBILICAL_DESC AS UNBILICAL, A.BUTTRED, A.ICTERUSINDEX, A.ELES &

  		FROM   SUM_NEWARRIVALSIGN A,SUM_BATHEDWAY B,SUM_DRAINQUALITY C,SUM_FEEDWAY D,SUM_UNBILICAL E &
  		
		WHERE   A.ADM_TYPE = <ADM_TYPE> &
		
		AND A.CASE_NO = <CASE_NO> &
		
         	AND TO_DATE (A.EXAMINE_DATE, 'YYYYMMDD') BETWEEN <START_DATE> AND <END_DATE> &
         	
         	AND A.DRAINQUALITY = C.QUALITY_CODE(+) &
         	
		AND A.FEEDWAY = D.FEEDWAY_CODE(+) &
		
		AND A.BATHEDWAY = B.BATHEDWAY_CODE(+) &
		
 		AND A.UNBILICAL = E.UNBILICAL_CODE(+) &
 		
 		AND A.DISPOSAL_FLG IS NULL &
 		
 		ORDER BY A.EXAMINE_DATE
         	
selectNewArrivalSign.Debug=N


//新生儿体温单主表插入--主表SUM_NEWARRIVALSIGN
insertNewArrival.Type=TSQL

insertNewArrival.SQL=INSERT INTO SUM_NEWARRIVALSIGN  (ADM_TYPE,CASE_NO,EXAMINE_DATE,IPD_NO,MR_NO, &

		INHOSPITALDAYS,USER_ID,BORNWEIGHT,URINETIMES,DRAINTIMES, &
		
		OPE_DAYS,ECTTIMES,DISPOSAL_FLG,DISPOSAL_REASON, &
		
		DRAINQUALITY,DRINKQTY,FEEDWAY,ADDDARIYQTY,VOMIT, &
		
		BATHEDWAY,EYE,EAR_NOSE,UNBILICAL,BUTTRED,ICTERUSINDEX, &
		
		ELES,OPT_USER,OPT_DATE,OPT_TERM,WEIGHT ) &

		VALUES (<ADM_TYPE>,<CASE_NO>,<EXAMINE_DATE>,<IPD_NO>,<MR_NO>, &

		<INHOSPITALDAYS>,<USER_ID>,<BORNWEIGHT>,<URINETIMES>,<DRAINTIMES>, &
		
		<OPE_DAYS>,<ECTTIMES>,<DISPOSAL_FLG>,<DISPOSAL_REASON>, &
		
		<DRAINQUALITY>,<DRINKQTY>,<FEEDWAY>,<ADDDARIYQTY>,<VOMIT>, &
		
		<BATHEDWAY>,<EYE>,<EAR_NOSE>,<UNBILICAL>,<BUTTRED>,<ICTERUSINDEX>, &
		
		<ELES>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<WEIGHT>)
		
insertNewArrival.Debug=N


//新生儿体温单细表插入--主表SUM_NEWARRIVALSIGNDTL
insertNewArrivalDtl.Type=TSQL

insertNewArrivalDtl.SQL=INSERT INTO SUM_NEWARRIVALSIGNDTL (ADM_TYPE,CASE_NO,EXAMINE_DATE,EXAMINESESSION,PHYSIATRICS, &

		RECTIME,SPCCONDCODE,TMPTRKINDCODE,TEMPERATURE,WEIGHT, &
		
		NOTPRREASONCODE,PTMOVECATECODE,PTMOVECATEDESC,USER_ID,OPT_USER,&
		
		OPT_DATE,OPT_TERM) &

		VALUES( <ADM_TYPE>,<CASE_NO>,<EXAMINE_DATE>,<EXAMINESESSION>,<PHYSIATRICS>, &

		<RECTIME>,<SPCCONDCODE>,<TMPTRKINDCODE>,<TEMPERATURE>,<WEIGHT>, &

		<NOTPRREASONCODE>,<PTMOVECATECODE>,<PTMOVECATEDESC>,<USER_ID>,<OPT_USER>, &

		<OPT_DATE>,<OPT_TERM>)
		
insertNewArrivalDtl.Debug=N


//新生儿体温单主表更新--主表SUM_NEWARRIVALSIGN
updateNewArrival.Type=TSQL

updateNewArrival.SQL=UPDATE SUM_NEWARRIVALSIGN  SET &

		USER_ID=<USER_ID>,BORNWEIGHT=<BORNWEIGHT>,URINETIMES=<URINETIMES>,DRAINTIMES=<DRAINTIMES>, &
		
		OPE_DAYS=<OPE_DAYS>,ECTTIMES=<ECTTIMES>,DISPOSAL_FLG=<DISPOSAL_FLG>,DISPOSAL_REASON=<DISPOSAL_REASON>,&
		
		DRAINQUALITY=<DRAINQUALITY>,DRINKQTY=<DRINKQTY>,FEEDWAY=<FEEDWAY>,ADDDARIYQTY=<ADDDARIYQTY>,VOMIT=<VOMIT>,&
		
		BATHEDWAY=<BATHEDWAY>,EYE=<EYE>,EAR_NOSE=<EAR_NOSE>,UNBILICAL=<UNBILICAL>,BUTTRED=<BUTTRED>,ICTERUSINDEX=<ICTERUSINDEX>,&
		
		ELES=<ELES>,OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> ,WEIGHT=<WEIGHT> &
		
		WHERE ADM_TYPE=<ADM_TYPE> &
		
		AND CASE_NO=<CASE_NO> &
		
		AND EXAMINE_DATE=<EXAMINE_DATE>		
		
updateNewArrival.Debug=N


//新生儿体温单细表更新--主表SUM_NEWARRIVALSIGNDTL
updateNewArrivalDtl.Type=TSQL

updateNewArrivalDtl.SQL=UPDATE SUM_NEWARRIVALSIGNDTL SET &

		PHYSIATRICS=<PHYSIATRICS>,RECTIME=<RECTIME>,SPCCONDCODE=<SPCCONDCODE>,TMPTRKINDCODE=<TMPTRKINDCODE>,TEMPERATURE=<TEMPERATURE>,WEIGHT=<WEIGHT>, &
		
		NOTPRREASONCODE=<NOTPRREASONCODE>,PTMOVECATECODE=<PTMOVECATECODE>,PTMOVECATEDESC=<PTMOVECATEDESC>,USER_ID=<USER_ID>,OPT_USER=<OPT_USER>,&
		
		OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> &
		
		WHERE ADM_TYPE=<ADM_TYPE> &
				
		AND CASE_NO=<CASE_NO> &
				
		AND EXAMINE_DATE=<EXAMINE_DATE> &
		
		AND EXAMINESESSION=<EXAMINESESSION>
		
updateNewArrivalDtl.Debug=N

//新生儿出生体重
getNewBornWeight.Type=TSQL
getNewBornWeight.SQL=SELECT  BORNWEIGHT  FROM SUM_NEWARRIVALSIGN &
		             WHERE  CASE_NO = <CASE_NO> &
		               AND  ADM_TYPE=<ADM_TYPE>  &
		               ORDER BY TO_DATE(EXAMINE_DATE,'YYYYMMDD')	
getNewBornWeight.Debug=N

//新生儿入院体重
getNewAdmWeight.Type=TSQL
getNewAdmWeight.SQL=SELECT  WEIGHT,MIN(TO_DATE(EXAMINE_DATE,'YYYYMMDD')) FROM SUM_NEWARRIVALSIGNDTL &
		             WHERE  CASE_NO = <CASE_NO> &
		               AND  ADM_TYPE= <ADM_TYPE>  &
		               GROUP BY WEIGHT,TO_DATE(EXAMINE_DATE,'YYYYMMDD'),EXAMINESESSION &
		               ORDER BY EXAMINESESSION
getNewAdmWeight.Debug=N

//新生儿入院体重（病案首页界面）
getFirstDayWeight.Type=TSQL
getFirstDayWeight.SQL=SELECT WEIGHT,EXAMINE_DATE FROM SUM_NEWARRIVALSIGN  &
						  WHERE  CASE_NO = <CASE_NO>  &
		              	 AND  ADM_TYPE= <ADM_TYPE>   &
		              	  ORDER BY TO_DATE(EXAMINE_DATE,'YYYYMMDD')
getFirstDayWeight.Debug=N
