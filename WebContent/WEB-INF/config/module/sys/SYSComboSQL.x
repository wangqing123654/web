Module.item=getGroupList;getInsFeeTypeCode;getSysCtzCode;getInsPayKind;getSysDept;getSysSexCode;getAdmRetnCode;getDoseCode;getOrderCode;getUnitCode;getNhiFeeCode;getFreqcode;getRouteCode;getOrderCat1Code;getChargeHospCode
//得特约请款机关
getGroupList.Type=TSQL
getGroupList.SQL=SELECT COMPANY_CODE,COMPANY_DESC FROM SYS_COMPANY WHERE HOSP_AREA = <HOSP_AREA> ORDER BY COMPANY_CODE
getGroupList.Debug=N
//得到收费等级
getInsFeeTypeCode.Type=TSQL
getInsFeeTypeCode.SQL=SELECT FEE_TYPE,FEE_TYPE_DESC FROM INS_FEE_TYPE ORDER BY FEE_TYPE
getInsFeeTypeCode.Debug=N
//得到身份
getSysCtzCode.Type=TSQL
getSysCtzCode.SQL=SELECT CTZ_CODE,CTZ_DESC FROM SYS_CTZ ORDER BY CTZ_CODE
getSysCtzCode.Item=COMPANY_CODE
getSysCtzCode.COMPANY_CODE=COMPANY_CODE=<COMPANY_CODE>
getSysCtzCode.Debug=N
//得到人员类别COMBO
getInsPayKind.Type=TSQL
getInsPayKind.SQL=SELECT PAY_KIND_CODE,PAY_KIND_DESC FROM INS_PAY_KIND ORDER BY PAY_KIND_CODE
getInsPayKind.Item=NHI_COMPANY
getInsPayKind.NHI_COMPANY=NHI_COMPANY=<NHI_COMPANY>
getInsPayKind.Debug=N
//科室COMBO
getSysDept.Type=TSQL
getSysDept.SQL=SELECT DEPT_CODE,DEPT_DESC FROM SYS_DEPT WHERE HOSP_AREA='HIS' AND ACTIVE_FLG='Y' AND FINAL_FLG='Y' AND STATISTICS_FLG='Y' AND DEPT_GRADE = '3'
getSysDept.Item=IPD_FIT_FLG;OPD_FIT_FLG;EMG_FIT_FLG;HRM_FIT_FLG;CLASSIFY
getSysDept.IPD_FIT_FLG=IPD_FIT_FLG=<IPD_FIT_FLG>
getSysDept.OPD_FIT_FLG=OPD_FIT_FLG=<OPD_FIT_FLG>
getSysDept.EMG_FIT_FLG=EMG_FIT_FLG=<EMG_FIT_FLG>
getSysDept.HRM_FIT_FLG=HRM_FIT_FLG=<HRM_FIT_FLG>
getSysDept.CLASSIFY=CLASSIFY=<CLASSIFY>
getSysDept.Debug=N
//性别
getSysSexCode.Type=TSQL
getSysSexCode.SQL=SELECT SEX_CODE,SEX_DESC FROM SYS_SEX ORDER BY SEX_CODE
getSysSexCode.Debug=N
//治愈情况
getAdmRetnCode.Type=TSQL
getAdmRetnCode.SQL=SELECT RETN_CODE,RETN_DESC FROM ADM_RETN ORDER BY RETN_CODE
getAdmRetnCode.Debug=N
//剂型
getDoseCode.Type=TSQL
getDoseCode.SQL=SELECT DOSE_CODE,DOSE_CHN_DESC FROM PHA_DOSE  ORDER BY DOSE_CODE
getDoseCode.Debug=N
//医嘱
getOrderCode.Type=TSQL
getOrderCode.SQL=SELECT ORDER_CODE AS ID,ORDER_DESC AS NAME,ORD_PYCODE AS PY1 FROM SYS_FEE  ORDER BY ORDER_CODE
getOrderCode.Debug=N
//单位
getUnitCode.Type=TSQL
getUnitCode.SQL=SELECT UNIT_CODE,UNIT_DESC FROM SYS_UNIT ORDER BY UNIT_CODE
getUnitCode.Debug=N
//健保申报费用别
getNhiFeeCode.Type=TSQL
getNhiFeeCode.SQL=SELECT NHI_FEE_CODE,NHI_FEE_DESC FROM SYS_NHI_FEE ORDER BY NHI_FEE_CODE
getNhiFeeCode.Debug=N
//频次
getFreqcode.Type=TSQL
getFreqcode.SQL=SELECT FREQ_CODE,FREQ_DESC FROM SYS_PHAFREQ ORDER BY FREQ_CODE
getFreqcode.Debug=N
//用法
getRouteCode.Type=TSQL
getRouteCode.SQL=SELECT ROUTE_CODE,ROUTE_CHN_DESC FROM SYS_PHAROUTE ORDER BY ROUTE_CODE
getRouteCode.Debug=N
//医领细分类
getOrderCat1Code.Type=TSQL
getOrderCat1Code.SQL=SELECT ORDER_CAT1_CODE,ORDER_CAT1_DESC FROM SYS_ORDER_CAT1 WHERE HOSP_AREA = 'HIS' ORDER BY ORDER_CAT1_CODE
getOrderCat1Code.Debug=N
//收费类别
getChargeHospCode.Type=TSQL
getChargeHospCode.SQL=SELECT CHARGE_HOSP_CODE,CHARGE_HOSP_DESC FROM SYS_CHARGE_HOSP ORDER BY CHARGE_HOSP_CODE
getChargeHospCode.Debug=N