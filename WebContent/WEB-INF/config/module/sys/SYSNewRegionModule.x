Module.item=selectfirst;selectStateList;selectByConditions;update;insertdata;delete;getGroupList;selectStateList2

//查询全字段
//=============pangben modify 20110602 添加密码强度校验、密码校验时间天数列
selectfirst.Type=TSQL
selectfirst.SQL=SELECT REGION_CODE, MAIN_FLG, NHI_NO, &
   REGION_CHN_DESC, REGION_CHN_ABN, REGION_ENG_DESC, &
   REGION_ENG_ABN, PY1, PY2, &
   SEQ, DESCRIPTION, REGION_TEL,& 
   REGION_FAX, REGION_ADDR, E_MAIL,& 
   AP_IP_ADDR, IP_RANGE_START, IP_RANGE_END,& 
   ACTIVE_FLG, ACTIVE_DATE, HOSP_CLASS, &
   SUPERINTENDENT, NHIMAIN_NAME, STATE_LIST,& 
   TOP_BEDFEE, OPT_USER, OPT_DATE, &
   OPT_TERM,PWD_STRENGTH,DETECTPWDTIME,LOCK_FLG,SPC_FLG FROM SYS_REGION ORDER BY SEQ
selectfirst.Debug=N

//查询字典表中与StateList列相关的数据
selectStateList.Type=TSQL
selectStateList.SQL=SELECT ID,ENG_DESC FROM SYS_DICTIONARY WHERE GROUP_ID=<GROUP_ID> ORDER BY SEQ
selectStateList.Debug=N

//根据条件查询
selectByConditions.Type=TSQL
selectByConditions.SQL=SELECT REGION_CODE, MAIN_FLG, NHI_NO, &
   REGION_CHN_DESC, REGION_CHN_ABN, REGION_ENG_DESC, &
   REGION_ENG_ABN, PY1, PY2, &
   SEQ, DESCRIPTION, REGION_TEL,& 
   REGION_FAX, REGION_ADDR, E_MAIL,& 
   AP_IP_ADDR, IP_RANGE_START, IP_RANGE_END,& 
   ACTIVE_FLG, ACTIVE_DATE, HOSP_CLASS, &
   SUPERINTENDENT, NHIMAIN_NAME, STATE_LIST,& 
   TOP_BEDFEE, OPT_USER, OPT_DATE,DETECTPWDTIME, &
   OPT_TERM,PWD_STRENGTH,SPC_FLG,PIVAS_FLG,LOCK_FLG FROM SYS_REGION 
selectByConditions.Item=REGION_CODE;NHI_NO;HOSP_CLASS;REGION_CHN_DESC;REGION_CHN_ABN;PY1;REGION_ENG_DESC;REGION_ENG_ABN;&
			DESCRIPTION;PY2;SUPERINTENDENT;NHIMAIN_NAME;REGION_TEL;REGION_FAX;REGION_ADDR;E_MAIL;IP_RANGE_START;IP_RANGE_END;AP_IP_ADDR
selectByConditions.REGION_CODE=REGION_CODE=<REGION_CODE>
selectByConditions.NHI_NO=NHI_NO=<NHI_NO>
selectByConditions.HOSP_CLASS=HOSP_CLASS=<HOSP_CLASS>
selectByConditions.REGION_CHN_DESC=REGION_CHN_DESC=<REGION_CHN_DESC>
selectByConditions.REGION_CHN_ABN=REGION_CHN_ABN=<REGION_CHN_ABN>
selectByConditions.PY1=PY1=<PY1>
selectByConditions.REGION_ENG_DESC=REGION_ENG_DESC=<REGION_ENG_DESC>
selectByConditions.REGION_ENG_ABN=REGION_ENG_ABN=<REGION_ENG_ABN>
selectByConditions.DESCRIPTION=DESCRIPTION=<DESCRIPTION>
selectByConditions.PY2=PY2=<PY2>
selectByConditions.SUPERINTENDENT=SUPERINTENDENT=<SUPERINTENDENT>
selectByConditions.NHIMAIN_NAME=NHIMAIN_NAME=<NHIMAIN_NAME>
selectByConditions.REGION_TEL=REGION_TEL=<REGION_TEL>
selectByConditions.REGION_FAX=REGION_FAX=<REGION_FAX>
selectByConditions.REGION_ADDR=REGION_ADDR=<REGION_ADDR>
selectByConditions.E_MAIL=E_MAIL=<E_MAIL>
selectByConditions.IP_RANGE_START=IP_RANGE_START=<IP_RANGE_START>
selectByConditions.IP_RANGE_END=IP_RANGE_END=<IP_RANGE_END>
selectByConditions.AP_IP_ADDR=AP_IP_ADDR=<AP_IP_ADDR>
selectByConditions.Debug=N

//根据 REGION_CODE 更新全字段
//=============pangben modify 20110602 添加密码强度校验、密码校验时间天数列
//=============yanjing modify 20130419 添加物联网标记字段
update.Type=TSQL
update.SQL=UPDATE SYS_REGION SET &
		 	MAIN_FLG=<MAIN_FLG>,&
			SPC_FLG=<SPC_FLG>,&
			NHI_NO=<NHI_NO>,&
			REGION_CHN_DESC=<REGION_CHN_DESC>,&
			REGION_CHN_ABN=<REGION_CHN_ABN>,&
			REGION_ENG_DESC=<REGION_ENG_DESC>,&
			REGION_ENG_ABN=<REGION_ENG_ABN>,&
			PY1=<PY1>,&
			PY2=<PY2>,&
			SEQ=<SEQ>,&
			DESCRIPTION=<DESCRIPTION>,&
			REGION_TEL=<REGION_TEL>,&
			REGION_FAX=<REGION_FAX>,&
			REGION_ADDR=<REGION_ADDR>,&
			E_MAIL=<E_MAIL>,&
			AP_IP_ADDR=<AP_IP_ADDR>,&
			IP_RANGE_START=<IP_RANGE_START>,&
			IP_RANGE_END=<IP_RANGE_END>,&
			ACTIVE_FLG=<ACTIVE_FLG>,&
			ACTIVE_DATE=SYSDATE,&
			HOSP_CLASS=<HOSP_CLASS>,&
			SUPERINTENDENT=<SUPERINTENDENT>,&
			NHIMAIN_NAME=<NHIMAIN_NAME>,&
			STATE_LIST=<STATE_LIST>,&
			TOP_BEDFEE=<TOP_BEDFEE>,&
			OPT_USER=<OPT_USER>,&
			OPT_DATE=SYSDATE,&
			OPT_TERM=<OPT_TERM>, & 
			PWD_STRENGTH=<PWD_STRENGTH>, &
			DETECTPWDTIME=<DETECTPWDTIME>, &	
			PIVAS_FLG=<PIVAS_FLG>, &	
			LOCK_FLG=<LOCK_FLG> &
		 WHERE REGION_CODE=<REGION_CODE>
update.Debug=N

//根据 REGION_CODE 插入全字段
//=============pangben modify 20110602 添加密码强度校验、密码校验时间天数列
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO SYS_REGION &
		       (REGION_CODE,MAIN_FLG,NHI_NO,REGION_CHN_DESC,REGION_CHN_ABN,&
		        REGION_ENG_DESC,REGION_ENG_ABN,PY1,PY2,SEQ,&
		        DESCRIPTION,REGION_TEL,REGION_FAX,REGION_ADDR,E_MAIL,&
		        AP_IP_ADDR,IP_RANGE_START,IP_RANGE_END,ACTIVE_FLG,ACTIVE_DATE,&
		        HOSP_CLASS,SUPERINTENDENT,NHIMAIN_NAME,STATE_LIST,TOP_BEDFEE,&
		        OPT_USER,OPT_DATE,OPT_TERM,PWD_STRENGTH,DETECTPWDTIME,SPC_FLG,PIVAS_FLG,LOCK_FLG) &
		 VALUES (<REGION_CODE>,<MAIN_FLG>,<NHI_NO>,<REGION_CHN_DESC>,<REGION_CHN_ABN>,&
		       <REGION_ENG_DESC>,<REGION_ENG_ABN>,<PY1>,<PY2>,<SEQ>,&
		       <DESCRIPTION>,<REGION_TEL>,<REGION_FAX>,<REGION_ADDR>,<E_MAIL>,&
		       <AP_IP_ADDR>,<IP_RANGE_START>,<IP_RANGE_END>,<ACTIVE_FLG>,SYSDATE,&
		       <HOSP_CLASS>,<SUPERINTENDENT>,<NHIMAIN_NAME>,<STATE_LIST>,<TOP_BEDFEE>,&
		       <OPT_USER>,SYSDATE,<OPT_TERM>,<PWD_STRENGTH>,<DETECTPWDTIME>,<SPC_FLG>,<PIVAS_FLG>,<LOCK_FLG>)
insertdata.Debug=N

//根据 REGION_CODE 删除
delete.Type=TSQL
delete.SQL=DELETE SYS_REGION  WHERE REGION_CODE = <REGION_CODE> 
delete.Debug=N

//根据 REGION_CODE 查询STATE_LIST
selectStateList2.type=TSQL
selectStateList2.SQL=select STATE_LIST from SYS_REGION  WHERE REGION_CODE = <REGION_CODE> 
selectStateList2.Debug=N

//得到组列表
getGroupList.Type=TSQL
getGroupList.SQL=SELECT ID,CHN_DESC AS NAME,TYPE,DATA FROM SYS_DICTIONARY WHERE GROUP_ID=<GROUP_ID> ORDER BY SEQ