## TBuilder Config File ## Title:药品不良反应复合统计## Company:BlueCore## Author:wanglong 2013.09.30## version 1.0#<Type=TFrame>UI.Title=药品不良反应复合统计UI.MenuConfig=%ROOT%\config\aci\ACIADRComplexStatisticsMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.aci.ACIADRComplexStatisticsControlUI.item=tPanel_0;tPanel_1UI.layout=nullUI.TopToolBar=YUI.TopMenu=YtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=96tPanel_0.Border=组|查询tPanel_0.AutoWidth=YtPanel_0.Item=YEAR;MONTH;DAY;DATE;PHA_AND_DEPT;NAME_AND_DEPT;NAME_AND_PHANAME_AND_PHA.Type=TRadioButtonNAME_AND_PHA.X=351NAME_AND_PHA.Y=55NAME_AND_PHA.Width=150NAME_AND_PHA.Height=23NAME_AND_PHA.Text=事件名称/药品分类NAME_AND_PHA.Group=2NAME_AND_PHA.Action=onChooseNameAndPhaNAME_AND_DEPT.Type=TRadioButtonNAME_AND_DEPT.X=188NAME_AND_DEPT.Y=55NAME_AND_DEPT.Width=150NAME_AND_DEPT.Height=23NAME_AND_DEPT.Text=事件名称/上报科室NAME_AND_DEPT.Group=2NAME_AND_DEPT.Action=onChooseNameAndDeptPHA_AND_DEPT.Type=TRadioButtonPHA_AND_DEPT.X=24PHA_AND_DEPT.Y=55PHA_AND_DEPT.Width=150PHA_AND_DEPT.Height=23PHA_AND_DEPT.Text=药品分类/上报科室PHA_AND_DEPT.Group=2PHA_AND_DEPT.Action=onChoosePhaAndDeptPHA_AND_DEPT.Selected=YDATE.Type=TTextFormatDATE.X=183DATE.Y=25DATE.Width=123DATE.Height=20DATE.Text=TTextFormatDATE.showDownButton=YDATE.FormatType=dateDATE.Format=yyyy/MM/ddDATE.HorizontalAlignment=0DAY.Type=TRadioButtonDAY.X=128DAY.Y=24DAY.Width=43DAY.Height=23DAY.Text=日DAY.Group=1DAY.Action=onChooseDayDAY.Selected=YMONTH.Type=TRadioButtonMONTH.X=76MONTH.Y=24MONTH.Width=43MONTH.Height=23MONTH.Text=月MONTH.Group=1MONTH.Action=onChooseMonthYEAR.Type=TRadioButtonYEAR.X=24YEAR.Y=24YEAR.Width=43YEAR.Height=23YEAR.Text=年YEAR.Group=1YEAR.Action=onChooseYeartPanel_1.Type=TPaneltPanel_1.X=5tPanel_1.Y=98tPanel_1.Width=1014tPanel_1.Height=639tPanel_1.Border=组|统计tPanel_1.AutoHeight=YtPanel_1.AutoWidth=YtPanel_1.Item=TABLETABLE.Type=TTableTABLE.X=10TABLE.Y=18TABLE.Width=641TABLE.Height=398TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.ColumnHorizontalAlignmentData=0,leftTABLE.ParmMap= TABLE.Header= TABLE.AutoModifyDataStore=NTABLE.AutoY=YTABLE.AutoX=YTABLE.Item=DEPT_CODE;PHA_RULE;ADR_IDTABLE.LockColumns=allDEPT_CODE.Type=TTextFormatDEPT_CODE.X=196DEPT_CODE.Y=55DEPT_CODE.Width=120DEPT_CODE.Height=23DEPT_CODE.Text=DEPT_CODE.HorizontalAlignment=2DEPT_CODE.PopupMenuHeader=代码,60;名称,150DEPT_CODE.PopupMenuWidth=232DEPT_CODE.PopupMenuHeight=300DEPT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DEPT_CODE.PopupMenuSQL=SELECT DEPT_CODE AS ID, DEPT_ABS_DESC AS NAME FROM SYS_DEPT WHERE ACTIVE_FLG = 'Y' UNION SELECT '合计' AS ID, '合计' AS NAME FROM DUAL ORDER BY IDDEPT_CODE.FormatType=comboDEPT_CODE.ShowDownButton=YDEPT_CODE.Tip=上报科室DEPT_CODE.ShowColumnList=NAMEDEPT_CODE.HisOneNullRow=YDEPT_CODE.ShowName=YPHA_RULE.Type=TTextFormatPHA_RULE.X=177PHA_RULE.Y=55PHA_RULE.Width=110PHA_RULE.Height=23PHA_RULE.Text=PHA_RULE.FormatType=comboPHA_RULE.showDownButton=YPHA_RULE.PopupMenuWidth=225PHA_RULE.PopupMenuHeight=250PHA_RULE.PopupMenuHeader=代码,50;名称,150PHA_RULE.PopupMenuSQL=SELECT CATEGORY_CODE ID,CATEGORY_CHN_DESC NAME,PY1,SEQ FROM SYS_CATEGORY WHERE RULE_TYPE='PHA_RULE' AND DETAIL_FLG='N' AND LENGTH(CATEGORY_CODE)=2 UNION SELECT '合计' ID,'合计' NAME,'' PY1,0 SEQ FROM DUAL ORDER BY SEQ ASC NULLS LASTPHA_RULE.PopupMenuFilter=ID,1;NAME,1;PY1,1PHA_RULE.HisOneNullRow=YPHA_RULE.ShowColumnList=NAMEPHA_RULE.HorizontalAlignment=2PHA_RULE.Tip=药品分类ADR_ID.Type=TTextFormatADR_ID.X=67ADR_ID.Y=255ADR_ID.Width=110ADR_ID.Height=23ADR_ID.Text=ADR_ID.FormatType=comboADR_ID.showDownButton=YADR_ID.PopupMenuWidth=225ADR_ID.PopupMenuHeight=250ADR_ID.PopupMenuHeader=代码,60;名称,100ADR_ID.PopupMenuSQL=SELECT ADR_ID AS ID, ADR_DESC AS NAME FROM ACI_ADRNAME UNION SELECT '合计' ID,'合计' NAME FROM DUAL ORDER BY ID ASC NULLS LASTADR_ID.PopupMenuFilter=ID,1;NAME,1ADR_ID.HisOneNullRow=YADR_ID.ShowColumnList=NAMEADR_ID.HorizontalAlignment=2ADR_ID.Tip=药品不良事件名称