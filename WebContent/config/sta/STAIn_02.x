# #  Title: STA_IN_02医院门、急诊工作统计报表# #  Description: STA_IN_02医院门、急诊工作统计报表# #  Copyright: Copyright (c) Javahis 2008# #  author zhangk 2009.6.3#  version 1.0#<Type=TFrame>UI.Title=门、急诊工作统计UI.MenuConfig=%ROOT%\config\sta\STAIn_02Menu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.sta.STAIn_02ControlUI.Item=tPanel_0;Table;Table_ReadUI.TopMenu=YUI.TopToolBar=YUI.ShowTitle=NUI.ShowMenu=NTable_Read.Type=TTableTable_Read.X=5Table_Read.Y=100Table_Read.Width=1014Table_Read.Height=643Table_Read.SpacingRow=1Table_Read.RowHeight=20Table_Read.Header=科别,120,STA_DEPT;诊疗人次总计,100,int;门急诊人次计,100,int;门诊人次,80,int;急诊人次计,90,int;急诊死亡人数,100,int;其他诊次,80,int;挂号室挂号数,100,int;观察室收容病人数,120,int;观察室死亡人数,120,int;上门诊正副主任医师,130,int;上门诊主治医师,110,int;上门诊住院医师,110,int;上门诊进修医师,110,int;工作小时,80,int;门诊实际工作日,110,int;日最高人次,100,int;日最低人次,100,int;平均每日门诊人次,120,double;平均每日急诊人次,120,double;每医师每小时诊疗人次,150,double;每床与每日门急诊次之比,160,double;每百门急诊的入院人次,140,double;门急诊诊次占总诊次的百分比,180,double;急诊抢救人数,100,int;抢救成功人数,100,int;急诊死亡率,90,double;急诊抢救成功率,110,double;观察室死亡率,100,double;门急诊人次任务数,120,int;门急诊人次任务完成率,140,double;门急诊治疗人次,110,int;本院医师比进修医师,130,double;主任主治医师比住院医师,150,double;手术人次,110,intTable_Read.LockColumns=allTable_Read.FocusIndexList=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33Table_Read.ColumnHorizontalAlignmentData=0,left;1,right;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,right;23,right;24,right;25,right;26,right;27,right;28,right;29,right;30,right;31,right;32,right;33,rightTable_Read.Item=STA_DEPTTable_Read.ParmMap=DEPT_CODE;DATA_02;DATA_03;DATA_04;DATA_05;DATA_06;DATA_07;DATA_08;DATA_09;DATA_10;DATA_11;DATA_12;DATA_13;DATA_14;DATA_15;DATA_16;DATA_17;DATA_18;DATA_19;DATA_20;DATA_21;DATA_22;DATA_23;DATA_24;DATA_25;DATA_26;DATA_27;DATA_28;DATA_29;DATA_30;DATA_31;DATA_32;DATA_33;DATA_34;DATA_35Table_Read.AutoX=YTable_Read.AutoWidth=YTable_Read.AutoHeight=YTable_Read.Visible=NTable.Type=TTableTable.X=112Table.Y=100Table.Width=81Table.Height=643Table.SpacingRow=1Table.RowHeight=20Table.AutoX=YTable.AutoWidth=YTable.AutoHeight=YTable.Header=科别,120,STA_DEPT;诊疗人次总计,100,int;门急诊人次计,100,int;门诊人次,80,int;急诊人次计,90,int;急诊死亡人数,100,int;其他诊次,80,int;挂号室挂号数,100,int;观察室收容病人数,120,int;观察室死亡人数,120,int;上门诊正副主任医师,130,int;上门诊主治医师,110,int;上门诊住院医师,110,int;上门诊进修医师,110,int;工作小时,80,int;门诊实际工作日,110,int;日最高人次,100,int;日最低人次,100,int;平均每日门诊人次,120,double;平均每日急诊人次,120,double;每医师每小时诊疗人次,150,double;每床与每日门急诊次之比,160,double;每百门急诊的入院人次,140,double;门急诊诊次占总诊次的百分比,180,double;急诊抢救人数,100,int;抢救成功人数,100,int;急诊死亡率,90,double;急诊抢救成功率,110,double;观察室死亡率,100,double;门急诊人次任务数,120,int;门急诊人次任务完成率,140,double;门急诊治疗人次,110,int;本院医师比进修医师,130,double;主任主治医师比住院医师,150,double;手术人次,110,intTable.Item=STA_DEPTTable.ParmMap=DEPT_CODE;DATA_02;DATA_03;DATA_04;DATA_05;DATA_06;DATA_07;DATA_08;DATA_09;DATA_10;DATA_11;DATA_12;DATA_13;DATA_14;DATA_15;DATA_16;DATA_17;DATA_18;DATA_19;DATA_20;DATA_21;DATA_22;DATA_23;DATA_24;DATA_25;DATA_26;DATA_27;DATA_28;DATA_29;DATA_30;DATA_31;DATA_32;DATA_33;DATA_34;DATA_35Table.LockColumns=0Table.FocusIndexList=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33Table.ColumnHorizontalAlignmentData=0,left;1,right;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,right;23,right;24,right;25,right;26,right;27,right;28,right;29,right;30,right;31,right;32,right;33,rightTable.AutoModifyDataStore=NtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=90tPanel_0.Border=凸tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.Item=tLabel_0;DATE_S;Submit;tLabel_28;DEPT_CODE;tLabel_10;PRINT_TYPE;label21;DATE_E;R_MONTH;R_DAYS;STA_DATE;tButton_0tButton_0.Type=TButtontButton_0.X=487tButton_0.Y=59tButton_0.Width=95tButton_0.Height=23tButton_0.Text=显示数据tButton_0.Action=onGenerateSTA_DATE.Type=TTextFormatSTA_DATE.X=120STA_DATE.Y=34STA_DATE.Width=100STA_DATE.Height=20STA_DATE.Text=TTextFormatSTA_DATE.FormatType=dateSTA_DATE.Format=yyyy/MMSTA_DATE.HorizontalAlignment=4R_DAYS.Type=TRadioButtonR_DAYS.X=130R_DAYS.Y=7R_DAYS.Width=101R_DAYS.Height=23R_DAYS.Text=日期段统计R_DAYS.Group=S_TYPER_DAYS.Action=onR_DAYS_ClickR_MONTH.Type=TRadioButtonR_MONTH.X=44R_MONTH.Y=7R_MONTH.Width=81R_MONTH.Height=23R_MONTH.Text=月统计R_MONTH.Group=S_TYPER_MONTH.Selected=YR_MONTH.Action=onR_MONTH_ClickDATE_E.Type=TTextFormatDATE_E.X=243DATE_E.Y=33DATE_E.Width=100DATE_E.Height=20DATE_E.Text=TTextFormatDATE_E.FormatType=dateDATE_E.Format=yyyy/MM/ddDATE_E.showDownButton=YDATE_E.Visible=NDATE_E.HorizontalAlignment=4label21.Type=TLabellabel21.X=223label21.Y=36label21.Width=20label21.Height=15label21.Text=至label21.Visible=NPRINT_TYPE.Type=TComboBoxPRINT_TYPE.X=348PRINT_TYPE.Y=59PRINT_TYPE.Width=120PRINT_TYPE.Height=23PRINT_TYPE.Text=TButtonPRINT_TYPE.showID=YPRINT_TYPE.Editable=YPRINT_TYPE.StringData=[[id,text],[3,三级科室],[4,四级科室]]PRINT_TYPE.TableShowList=texttLabel_10.Type=TLabeltLabel_10.X=275tLabel_10.Y=64tLabel_10.Width=65tLabel_10.Height=15tLabel_10.Text=打印模式DEPT_CODE.Type=TTextFormatDEPT_CODE.X=120DEPT_CODE.Y=61DEPT_CODE.Width=120DEPT_CODE.Height=20DEPT_CODE.Text=DEPT_CODE.showDownButton=YDEPT_CODE.FormatType=comboDEPT_CODE.PopupMenuWidth=310DEPT_CODE.PopupMenuHeight=300DEPT_CODE.HisOneNullRow=YDEPT_CODE.PopupMenuHeader=科室CODE,100;科室名称,200DEPT_CODE.PopupMenuSQL=SELECT DISTINCT DEPT_CODE, DEPT_DESC, DEPT_LEVEL, OE_DEPT_CODE, IPD_DEPT_CODE, PY1   FROM STA_OEI_DEPT_LIST  WHERE DEPT_LEVEL='4' AND OE_DEPT_CODE IS NOT NULLDEPT_CODE.PopupMenuFilter=DEPT_CODE,1;DEPT_DESC,1;PY1,1DEPT_CODE.ShowColumnList=DEPT_DESCDEPT_CODE.ValueColumn=DEPT_CODEDEPT_CODE.DynamicDownload=YDEPT_CODE.HorizontalAlignment=2tLabel_28.Type=TLabeltLabel_28.X=47tLabel_28.Y=64tLabel_28.Width=63tLabel_28.Height=15tLabel_28.Text=查询科室tLabel_28.Color=蓝STA_DEPT.Type=中间对照部门下拉列表STA_DEPT.X=426STA_DEPT.Y=28STA_DEPT.Width=81STA_DEPT.Height=23STA_DEPT.Text=TButtonSTA_DEPT.showID=YSTA_DEPT.showName=YSTA_DEPT.showText=NSTA_DEPT.showValue=NSTA_DEPT.showPy1=YSTA_DEPT.showPy2=YSTA_DEPT.Editable=YSTA_DEPT.Tip=对照科室STA_DEPT.TableShowList=nameSubmit.Type=TCheckBoxSubmit.X=270Submit.Y=27Submit.Width=81Submit.Height=23Submit.Text=提  交Submit.Visible=NDATE_S.Type=TTextFormatDATE_S.X=119DATE_S.Y=33DATE_S.Width=100DATE_S.Height=20DATE_S.Text=TTextFormatDATE_S.FormatType=dateDATE_S.Format=yyyy/MM/ddDATE_S.HorizontalAlignment=4DATE_S.Action=onQueryDATE_S.showDownButton=YDATE_S.Visible=NtLabel_0.Type=TLabeltLabel_0.X=48tLabel_0.Y=37tLabel_0.Width=66tLabel_0.Height=15tLabel_0.Text=统计月份tLabel_0.Color=蓝