Module.item=getName;getGroupName;getGroupList;getList;getListAll;insert;delete;update;query;getSysCharge;getEnName;updateFlg

//得到名称
getName.Type=TSQL
getName.SQL=SELECT CHN_DESC AS NAME FROM SYS_DICTIONARY WHERE GROUP_ID=<GROUP_ID> AND ID=<ID>
getName.Debug=N

//得到英文名称
getEnName.Type=TSQL
getEnName.SQL=SELECT ENG_DESC AS NAME FROM SYS_DICTIONARY WHERE GROUP_ID=<GROUP_ID> AND ID=<ID>
getEnName.Debug=N


//得到组名称
getGroupName.Type=TSQL
getGroupName.SQL=SELECT CHN_DESC AS NAME FROM SYS_DICTIONARY WHERE GROUP_ID='GROUP' AND ID=<ID>

//得到组列表
getGroupList.Type=TSQL
getGroupList.SQL=SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,TYPE,DATA FROM SYS_DICTIONARY WHERE GROUP_ID=<GROUP_ID> ORDER BY SEQ,ID
getGroupList.Debug=N

//列表整组数据
getListAll.Type=TSQL
getListAll.SQL=SELECT ACTIVE_FLG, GROUP_ID,ID,CHN_DESC AS NAME,ENG_DESC,PY1,PY2,SEQ,TYPE,PARENT_ID,STATE,DATA,DESCRIPTION,OPT_USER,OPT_TERM,OPT_DATE,FLG,STA1_CODE,STA2_CODE,STA3_CODE FROM SYS_DICTIONARY WHERE GROUP_ID=<GROUP_ID> ORDER BY SEQ

insert.Type=TSQL
//insert.SQL=INSERT INTO SYS_DICTIONARY(GROUP_ID,ID,CHN_DESC,TYPE,PARENT_ID,STATE,DATA,DESCRIPTION,OPT_ID,OPT_IP,SEQ, ACTIVE_FLG) values(<GROUP_ID>,<ID>,<NAME>,<TYPE>,<PARENT_ID>,<STATE>,<DATA>,<DESCRIPTION>,<OPT_ID>,<OPT_IP>,<SEQ>, <ACTIVE_FLG>)

insert.SQL=INSERT INTO SYS_DICTIONARY(GROUP_ID,ID,CHN_DESC,ENG_DESC,PY1,PY2,SEQ,TYPE,PARENT_ID,STATE,DATA,DESCRIPTION,OPT_USER,OPT_TERM,OPT_DATE,FLG,STA1_CODE,STA2_CODE,STA3_CODE,ACTIVE_FLG) values(<GROUP_ID>,<ID>,<NAME>,<ENG_DESC>,<PY1>,<PY2>,<SEQ>,<TYPE>,<PARENT_ID>,<STATE>,<DATA>,<DESCRIPTION>,<OPT_USER>,<OPT_TERM>,SYSDATE,<FLG>,<STA1_CODE>,<STA2_CODE>,<STA3_CODE>,<ACTIVE_FLG>)
insert.Debug=Y

delete.Type=TSQL
delete.SQL=DELETE FROM SYS_DICTIONARY WHERE GROUP_ID=<GROUP_ID> AND ID=<ID>
delete.Debug=N

update.Type=TSQL
update.SQL=UPDATE SYS_DICTIONARY SET CHN_DESC=<NAME>,ENG_DESC=<ENG_DESC>,PY1=<PY1>,PY2=<PY2>,SEQ=<SEQ>,TYPE=<TYPE>,PARENT_ID=<PARENT_ID>,STATE=<STATE>,DATA=<DATA>,DESCRIPTION=<DESCRIPTION>,OPT_USER=<OPT_USER>,OPT_TERM=<OPT_TERM>,OPT_DATE=SYSDATE,FLG=<FLG>,STA1_CODE=<STA1_CODE>,STA2_CODE=<STA2_CODE>,STA3_CODE=<STA3_CODE>, ACTIVE_FLG=<ACTIVE_FLG> WHERE GROUP_ID=<GROUP_ID> AND ID=<ID>
update.Debug=N

query.Type=TSQL
query.SQL=SELECT ACTIVE_FLG, GROUP_ID,ID,CHN_DESC AS NAME,ENG_DESC,PY1,PY2,SEQ,TYPE,PARENT_ID,STATE,DATA,DESCRIPTION,OPT_USER,OPT_TERM,OPT_DATE,FLG,STA1_CODE,STA2_CODE,STA3_CODE FROM SYS_DICTIONARY WHERE GROUP_ID=<GROUP_ID> AND ID LIKE <ID>

//查找
getSysCharge.Type=TSQL
getSysCharge.SQL=SELECT GROUP_ID,ID,CHN_DESC &
            FROM SYS_DICTIONARY &
            WHERE GROUP_ID=<GROUP_ID>
getSysCharge.Debug=N
//默认选择
updateFlg.Type=TSQL
updateFlg.SQL=UPDATE SYS_DICTIONARY SET FLG=<FLG> WHERE GROUP_ID=<GROUP_ID> AND ID=<ID>
updateFlg.Debug=N

