Module.item=getGroupList
getGroupList.Type=TSQL
getGroupList.SQL=SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,TYPE,DATA FROM SYS_DICTIONARY WHERE GROUP_ID=<GROUP_ID> ORDER BY SEQ,ID
getGroupList.Debug=N
