# #  Title:中间档工作日志UI# #  Description:中间档工作日志UI# #  Copyright: Copyright (c) Javahis 2008# #  author zhangk 2009.4.24#  version 1.0#<Type=TFrame>UI.Title=工作日志UI.MenuConfig=%ROOT%\config\sta\STAWorkLogMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.sta.STAWorkLogControlUI.Item=tTabbedPane_0UI.TopMenu=YUI.TopToolBar=YUI.ShowMenu=NUI.ShowTitle=NtTabbedPane_0.Type=TTabbedPanetTabbedPane_0.X=137tTabbedPane_0.Y=124tTabbedPane_0.Width=81tTabbedPane_0.Height=738tTabbedPane_0.AutoX=YtTabbedPane_0.AutoY=YtTabbedPane_0.AutoHeight=YtTabbedPane_0.AutoWidth=YtTabbedPane_0.Item=tPanel_0;tPanel_1tTabbedPane_0.ChangedAction=onChooseRBTtPanel_1.Type=TPaneltPanel_1.X=40tPanel_1.Y=11tPanel_1.Width=81tPanel_1.Height=81tPanel_1.Name=月  报tPanel_1.Item=tPanel_3;TableMouthTableMouth.Type=TTableTableMouth.X=83TableMouth.Y=90TableMouth.Width=999TableMouth.Height=614TableMouth.SpacingRow=1TableMouth.RowHeight=20TableMouth.AutoX=YTableMouth.AutoWidth=YTableMouth.AutoHeight=YTableMouth.Header=科室,100,DEPT;诊疗人数总计,90,int;门诊人次数,80,int;急诊人次计,80,int;急诊死亡人次,90,int;留观病人数,75,int;留观死亡人数,90,int;期初实有病人,90,int;入院人数,65,int;他科转入,65,int;出院人数总计,90,int;病人数计,65,int;治愈,40,int;好转,40,int;未愈,40,int;死亡,40,int;其他,40,int;转往他科人数,90,int;实有病人数,75,int;期末实有病床,90,int;实际开放总床日,100,int;平均开放病床数,100,int;实际占用总床数,100,int;出院者住院日数,100,int;门诊诊断符合数,100,int;病理诊断符合数,100,int;入院诊断符合数,100,int;术前术后符合数,100,int;无菌切口手术数,100,int;无菌切口化脓数,100,int;危重病人抢救数,100,int;危重病人抢救成功,110,int;陪人数,55,int;治愈率,55,double;好转率,55,double;病死率,55,double;病床周转(次),90,double;病床工作日,80,int;病床使用率,80,double;患者平均住院日,110,int;诊断符合率,80,double;无菌切口化脓率,100,double;危重病人抢救成功,120,double;陪人率,60,double;病理诊断符合率,100,double;术前术后诊断符合率,130,doubleTableMouth.ColumnHorizontalAlignmentData=0,left;1,right;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,right;23,right;24,right;25,right;26,right;27,right;28,right;29,right;30,right;31,right;32,right;33,right;34,right;35,right;36,right;37,right;38,right;39,right;40,right;41,right;42,right;43,right;44,right;45,right;46,rightTableMouth.ParmMap=DEPT_CODE;DATA_01;DATA_02;DATA_03;DATA_04;DATA_05;DATA_06;DATA_07;DATA_08;DATA_08_1;DATA_09;DATA_10;DATA_11;DATA_12;DATA_13;DATA_14;DATA_15;DATA_15_1;DATA_16;DATA_17;DATA_18;DATA_19;DATA_20;DATA_21;DATA_22;DATA_23;DATA_24;DATA_25;DATA_26;DATA_27;DATA_28;DATA_29;DATA_30;DATA_31;DATA_32;DATA_33;DATA_34;DATA_35;DATA_36;DATA_37;DATA_38;DATA_39;DATA_40;DATA_41;DATA_41_1;DATA_41_2TableMouth.LockColumns=0TableMouth.Item=DEPTTableMouth.AutoModifyDataStore=NTableMouth.FocusIndexList=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46TableMouth.ChangeValueAction=onChangeTableValuetPanel_3.Type=TPaneltPanel_3.X=5tPanel_3.Y=5tPanel_3.Width=999tPanel_3.Height=80tPanel_3.Border=凸tPanel_3.AutoX=YtPanel_3.AutoY=YtPanel_3.AutoWidth=YtPanel_3.Item=tLabel_4;STA_DATE2;Submit2;DEPT_CODE2;MONTH_DATE;MONTH_PERIOD;MONTH_START_DATE;tLabel_3;MONTH_END_DATEMONTH_END_DATE.Type=TTextFormatMONTH_END_DATE.X=258MONTH_END_DATE.Y=48MONTH_END_DATE.Width=120MONTH_END_DATE.Height=20MONTH_END_DATE.Text=MONTH_END_DATE.FormatType=dateMONTH_END_DATE.Format=yyyy/MMMONTH_END_DATE.showDownButton=NMONTH_END_DATE.HorizontalAlignment=4tLabel_3.Type=TLabeltLabel_3.X=234tLabel_3.Y=51tLabel_3.Width=19tLabel_3.Height=15tLabel_3.Text=～MONTH_START_DATE.Type=TTextFormatMONTH_START_DATE.X=103MONTH_START_DATE.Y=48MONTH_START_DATE.Width=120MONTH_START_DATE.Height=20MONTH_START_DATE.Text=MONTH_START_DATE.FormatType=dateMONTH_START_DATE.Format=yyyy/MMMONTH_START_DATE.showDownButton=NMONTH_START_DATE.HorizontalAlignment=4MONTH_PERIOD.Type=TRadioButtonMONTH_PERIOD.X=16MONTH_PERIOD.Y=47MONTH_PERIOD.Width=81MONTH_PERIOD.Height=23MONTH_PERIOD.Text=查询区间MONTH_PERIOD.Group=2MONTH_PERIOD.Color=蓝MONTH_PERIOD.Action=onChooseRBTMONTH_DATE.Type=TRadioButtonMONTH_DATE.X=16MONTH_DATE.Y=15MONTH_DATE.Width=81MONTH_DATE.Height=23MONTH_DATE.Text=统计日期MONTH_DATE.Group=2MONTH_DATE.Color=蓝MONTH_DATE.Selected=YMONTH_DATE.Action=onChooseRBTDEPT_CODE2.Type=TTextFormatDEPT_CODE2.X=336DEPT_CODE2.Y=16DEPT_CODE2.Width=120DEPT_CODE2.Height=20DEPT_CODE2.Text=DEPT_CODE2.PopupMenuWidth=310DEPT_CODE2.PopupMenuHeight=300DEPT_CODE2.FormatType=comboDEPT_CODE2.showDownButton=YDEPT_CODE2.HorizontalAlignment=2DEPT_CODE2.PopupMenuHeader=科室CODE,100;科室名称,200DEPT_CODE2.PopupMenuSQL=SELECT DISTINCT DEPT_CODE, DEPT_DESC, DEPT_LEVEL, OE_DEPT_CODE, IPD_DEPT_CODE, PY1   FROM STA_OEI_DEPT_LIST  WHERE DEPT_LEVEL='4'DEPT_CODE2.HisOneNullRow=YDEPT_CODE2.PopupMenuFilter=DEPT_CODE,1;DEPT_DESC,1;PY1,1DEPT_CODE2.ShowColumnList=DEPT_DESCDEPT_CODE2.ValueColumn=DEPT_CODEDEPT_CODE2.DynamicDownload=YSubmit2.Type=TCheckBoxSubmit2.X=534Submit2.Y=15Submit2.Width=81Submit2.Height=23Submit2.Text=提  交STA_DATE2.Type=TTextFormatSTA_DATE2.X=103STA_DATE2.Y=16STA_DATE2.Width=120STA_DATE2.Height=20STA_DATE2.Text=STA_DATE2.FormatType=dateSTA_DATE2.Format=yyyy/MMSTA_DATE2.showDownButton=NSTA_DATE2.HorizontalAlignment=4tLabel_4.Type=TLabeltLabel_4.X=257tLabel_4.Y=19tLabel_4.Width=65tLabel_4.Height=15tLabel_4.Text=科    室tLabel_4.Color=蓝tPanel_0.Type=TPaneltPanel_0.X=74tPanel_0.Y=71tPanel_0.Width=81tPanel_0.Height=81tPanel_0.Title=tPanel_0.Text=tPanel_0.Name=日  报tPanel_0.Item=tPanel_2;TableDayTableDay.Type=TTableTableDay.X=3TableDay.Y=90TableDay.Width=999TableDay.Height=614TableDay.SpacingRow=1TableDay.RowHeight=20TableDay.AutoX=YTableDay.AutoWidth=YTableDay.AutoHeight=YTableDay.Header=提交,40,boolean;科室,100,DEPT;诊疗人数总计,90,int;门诊人次数,80,int;急诊人次计,80,int;急诊死亡人次,90,int;留观病人数,85,int;留观死亡人数,90,int;期初实有病人,90,int;入院人数,65,int;他科转入,65,int;出院人数总计,90,int;病人数计,65,int;治愈,40,int;好转,40,int;未愈,40,int;死亡,40,int;其他,40,int;转往他科人数,90,int;实有病人数,85,int;期末实有病床,90,int;实际开放总床日,100,int;平均开放病床数,100,int;实际占用总床数,100,int;出院者住院日数,100,int;门诊诊断符合数,100,int;病理诊断符合数,100,int;入院诊断符合数,100,int;术前术后符合数,100,int;无菌切口手术数,100,int;无菌切口化脓数,100,int;危重病人抢救数,100,int;危重病人抢救成功,115,int;陪人数,55,int;治愈率,55,double;好转率,55,double;病死率,55,double;病床周转(次),90,double;病床工作日,80,int;病床使用率,80,double;患者平均住院日,110,int;诊断符合率,80,double;无菌切口化脓率,100,double;危重病人抢救成功,120,double;陪人率,60,double;病理诊断符合率,100,double;术前术后诊断符合率,130,doubleTableDay.ParmMap=SUBMIT_FLG;DEPT_CODE;DATA_01;DATA_02;DATA_03;DATA_04;DATA_05;DATA_06;DATA_07;DATA_08;DATA_08_1;DATA_09;DATA_10;DATA_11;DATA_12;DATA_13;DATA_14;DATA_15;DATA_15_1;DATA_16;DATA_17;DATA_18;DATA_19;DATA_20;DATA_21;DATA_22;DATA_23;DATA_24;DATA_25;DATA_26;DATA_27;DATA_28;DATA_29;DATA_30;DATA_31;DATA_32;DATA_33;DATA_34;DATA_35;DATA_36;DATA_37;DATA_38;DATA_39;DATA_40;DATA_41;DATA_41_1;DATA_41_2TableDay.LockColumns=0,1TableDay.ColumnHorizontalAlignmentData=1,left;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,right;23,right;24,right;25,right;26,right;27,right;28,right;29,right;30,right;31,right;32,right;33,right;34,right;35,right;36,right;37,right;38,right;39,right;40,right;41,right;42,right;43,right;44,right;45,right;46,rightTableDay.Item=DEPTTableDay.AutoModifyDataStore=NTableDay.FocusIndexList=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46TableDay.ChangeValueAction=onChangeTableValuetPanel_2.Type=TPaneltPanel_2.X=5tPanel_2.Y=5tPanel_2.Width=999tPanel_2.Height=80tPanel_2.Border=凸tPanel_2.AutoX=YtPanel_2.AutoY=YtPanel_2.AutoWidth=YtPanel_2.Item=tLabel_1;tLabel_2;SubmitFlg;STA_DATE1;Submit1;DEPT;DEPT_CODE1;DAY_DATE;DAY_PERIOD;DAY_START_DATE;tLabel_0;DAY_END_DATEDAY_END_DATE.Type=TTextFormatDAY_END_DATE.X=258DAY_END_DATE.Y=48DAY_END_DATE.Width=120DAY_END_DATE.Height=20DAY_END_DATE.Text=DAY_END_DATE.FormatType=dateDAY_END_DATE.Format=yyyy/MM/ddDAY_END_DATE.showDownButton=YDAY_END_DATE.HorizontalAlignment=4tLabel_0.Type=TLabeltLabel_0.X=234tLabel_0.Y=51tLabel_0.Width=18tLabel_0.Height=15tLabel_0.Text=～DAY_START_DATE.Type=TTextFormatDAY_START_DATE.X=103DAY_START_DATE.Y=48DAY_START_DATE.Width=120DAY_START_DATE.Height=20DAY_START_DATE.Text=DAY_START_DATE.FormatType=dateDAY_START_DATE.Format=yyyy/MM/ddDAY_START_DATE.showDownButton=YDAY_START_DATE.HorizontalAlignment=4DAY_PERIOD.Type=TRadioButtonDAY_PERIOD.X=16DAY_PERIOD.Y=47DAY_PERIOD.Width=81DAY_PERIOD.Height=23DAY_PERIOD.Text=查询区间DAY_PERIOD.Group=1DAY_PERIOD.Color=蓝DAY_PERIOD.Action=onChooseRBTDAY_DATE.Type=TRadioButtonDAY_DATE.X=16DAY_DATE.Y=15DAY_DATE.Width=81DAY_DATE.Height=23DAY_DATE.Text=统计日期DAY_DATE.Group=1DAY_DATE.Color=蓝DAY_DATE.Selected=YDAY_DATE.Action=onChooseRBTDEPT_CODE1.Type=TTextFormatDEPT_CODE1.X=336DEPT_CODE1.Y=16DEPT_CODE1.Width=120DEPT_CODE1.Height=20DEPT_CODE1.Text=DEPT_CODE1.FormatType=comboDEPT_CODE1.showDownButton=YDEPT_CODE1.PopupMenuHeader=科室CODE,100;科室名称,200DEPT_CODE1.PopupMenuWidth=310DEPT_CODE1.PopupMenuHeight=300DEPT_CODE1.PopupMenuSQL=SELECT DISTINCT DEPT_CODE, DEPT_DESC, DEPT_LEVEL, OE_DEPT_CODE, IPD_DEPT_CODE, PY1 FROM STA_OEI_DEPT_LIST  WHERE DEPT_LEVEL='4'DEPT_CODE1.HisOneNullRow=YDEPT_CODE1.PopupMenuFilter=DEPT_CODE,1;DEPT_DESC,1;PY1,1DEPT_CODE1.ShowColumnList=DEPT_DESCDEPT_CODE1.ValueColumn=DEPT_CODEDEPT_CODE1.DynamicDownload=YDEPT_CODE1.HorizontalAlignment=2DEPT.Type=TTextFormatDEPT.X=885DEPT.Y=27DEPT.Width=81DEPT.Height=23DEPT.Text=DEPT.FormatType=comboDEPT.showDownButton=YDEPT.PopupMenuHeader=科室CODE,100;科室名称,200DEPT.PopupMenuWidth=310DEPT.PopupMenuHeight=300DEPT.PopupMenuSQL=SELECT DISTINCT DEPT_CODE, DEPT_DESC, DEPT_LEVEL, OE_DEPT_CODE, IPD_DEPT_CODE, PY1 FROM STA_OEI_DEPT_LIST WHERE DEPT_LEVEL='4' UNION SELECT '合计' DEPT_CODE,'合计:' DEPT_DESC,null DEPT_LEVEL,'' OE_DEPT_CODE,'' IPD_DEPT_CODE,'' PY1 FROM DUALDEPT.HisOneNullRow=YDEPT.PopupMenuFilter=DEPT_CODE,1;DEPT_DESC,1;PY1,1DEPT.ShowColumnList=DEPT_DESCDEPT.ValueColumn=DEPT_CODEDEPT.DynamicDownload=YDEPT.HorizontalAlignment=2DEPT.Visible=NSubmit1.Type=TCheckBoxSubmit1.X=729Submit1.Y=28Submit1.Width=81Submit1.Height=23Submit1.Text=提  交STA_DATE1.Type=TTextFormatSTA_DATE1.X=103STA_DATE1.Y=16STA_DATE1.Width=120STA_DATE1.Height=20STA_DATE1.Text=STA_DATE1.FormatType=dateSTA_DATE1.Format=yyyy/MM/ddSTA_DATE1.showDownButton=YSTA_DATE1.HorizontalAlignment=4SubmitFlg.Type=TComboBoxSubmitFlg.X=571SubmitFlg.Y=15SubmitFlg.Width=120SubmitFlg.Height=23SubmitFlg.Text=TButtonSubmitFlg.showID=YSubmitFlg.Editable=YSubmitFlg.StringData=[[id,text],[,],[Y,已提交],[N,未提交]]SubmitFlg.TableShowList=texttLabel_2.Type=TLabeltLabel_2.X=493tLabel_2.Y=19tLabel_2.Width=72tLabel_2.Height=15tLabel_2.Text=状    态tLabel_2.Color=蓝tLabel_1.Type=TLabeltLabel_1.X=257tLabel_1.Y=19tLabel_1.Width=61tLabel_1.Height=15tLabel_1.Text=科    室tLabel_1.Color=蓝