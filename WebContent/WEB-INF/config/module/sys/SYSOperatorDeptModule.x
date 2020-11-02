Module.item=select;delete;update;insert

select.TYPE=TSQL
select.SQL=SELECT DEPT_CODE,MAIN_FLG FROM SYS_OPERATOR_DEPT WHERE USER_ID=<USER_ID> ORDER BY DEPT_CODE
select.Debug=N

delete.TYPE=TSQL
delete.SQL=DELETE SYS_OPERATOR_DEPT WHERE USER_ID=<USER_ID>

update.TYPE=TSQL
update.SQL=UPDATE SYS_OPERATOR_DEPT SET DEPT_CODE=<DEPT_CODE>,MAIN_FLG=<MAIN_FLG>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> WHERE USER_ID=<USER_ID>

insert.TYPE=TSQL
insert.SQL=INSERT SYS_OPERATOR_DEPT VALUES(<USER_ID>,<DEPT_CODE>,<MAIN_FLG>,<OPT_USER>,SYSDATE,<OPT_TERM>)