## TBuilder Config File ## Title:客户获取方式统计报表## Company:JavaHis## Author:黄婷婷 2015.12.11## version 1.0#<Type=TFrame>UI.Title=挂号未看诊查询UI.MenuConfig=%ROOT%\config\reg\REGRegNoSeeQueryMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.reg.REGRegNoSeeQueryControlUI.item=tPanel_0;TABLEUI.layout=nullUI.TopMenu=YUI.TopToolBar=YTABLE.Type=TTableTABLE.X=8TABLE.Y=110TABLE.Width=81TABLE.Height=81TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=YTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Header=挂号日期,160;时段,80;号别,150;就诊号,110;病案号,90;患者姓名,90;挂号科室,110;挂号医生,110TABLE.LockColumns=allTABLE.Item=TABLE.ParmMap=REG_DATE;SESSION_DESC;CLINICTYPE_DESC;CASE_NO;MR_NO;PAT_NAME;DEPT_DESC;DR_DESCTABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,leftTABLE.ClickedAction=TABLE.ChangeValueAction=TABLE.ChangeAction=TABLE.ColumnChangeAction=TABLE.RowChangeAction=TABLE.DoubleClickedAction=TABLE.RightClickedAction=tPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=6tPanel_0.Width=1014tPanel_0.Height=96tPanel_0.Border=组|查询条件tPanel_0.AutoX=YtPanel_0.AutoWidth=YtPanel_0.Item=tLabel_8;tLabel_10;S_DATE;E_DATE;tLabel_0;SESSION_CODE;tLabel_13;CLINICTYPE_CODE;tLabel_16;tLabel_17;DEPT_CODE;DR_CODE;tLabel_18;MR_NOMR_NO.Type=TTextFieldMR_NO.X=551MR_NO.Y=65MR_NO.Width=139MR_NO.Height=20MR_NO.Text=MR_NO.Action=onMrNotLabel_18.Type=TLabeltLabel_18.X=492tLabel_18.Y=67tLabel_18.Width=61tLabel_18.Height=15tLabel_18.Text=病案号：tLabel_18.Color=蓝DR_CODE.Type=人员DR_CODE.X=321DR_CODE.Y=62DR_CODE.Width=147DR_CODE.Height=23DR_CODE.Text=DR_CODE.HorizontalAlignment=2DR_CODE.PopupMenuHeader=代码,100;名称,100DR_CODE.PopupMenuWidth=300DR_CODE.PopupMenuHeight=300DR_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DR_CODE.FormatType=comboDR_CODE.ShowDownButton=YDR_CODE.Tip=人员DR_CODE.ShowColumnList=NAMEDR_CODE.HisOneNullRow=YDR_CODE.Dept=<DEPT_CODE>DR_CODE.PosType=1DEPT_CODE.Type=科室DEPT_CODE.X=85DEPT_CODE.Y=62DEPT_CODE.Width=146DEPT_CODE.Height=23DEPT_CODE.Text=DEPT_CODE.HorizontalAlignment=2DEPT_CODE.PopupMenuHeader=代码,100;名称,100DEPT_CODE.PopupMenuWidth=300DEPT_CODE.PopupMenuHeight=300DEPT_CODE.FormatType=comboDEPT_CODE.ShowDownButton=YDEPT_CODE.Tip=科室DEPT_CODE.ShowColumnList=NAMEDEPT_CODE.HisOneNullRow=YDEPT_CODE.FinalFlg=YDEPT_CODE.Action=DR_CODE|onQueryDEPT_CODE.ClassIfy=0tLabel_17.Type=TLabeltLabel_17.X=272tLabel_17.Y=67tLabel_17.Width=48tLabel_17.Height=15tLabel_17.Text=医生：tLabel_17.Color=蓝tLabel_16.Type=TLabeltLabel_16.X=14tLabel_16.Y=67tLabel_16.Width=45tLabel_16.Height=15tLabel_16.Text=科室：tLabel_16.Color=蓝CLINICTYPE_CODE.Type=号别CLINICTYPE_CODE.X=759CLINICTYPE_CODE.Y=28CLINICTYPE_CODE.Width=162CLINICTYPE_CODE.Height=23CLINICTYPE_CODE.Text=CLINICTYPE_CODE.HorizontalAlignment=2CLINICTYPE_CODE.PopupMenuHeader=代码,100;名称,100CLINICTYPE_CODE.PopupMenuWidth=300CLINICTYPE_CODE.PopupMenuHeight=300CLINICTYPE_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1CLINICTYPE_CODE.FormatType=comboCLINICTYPE_CODE.ShowDownButton=YCLINICTYPE_CODE.Tip=号别CLINICTYPE_CODE.ShowColumnList=NAMECLINICTYPE_CODE.HisOneNullRow=YtLabel_13.Type=TLabeltLabel_13.X=716tLabel_13.Y=33tLabel_13.Width=45tLabel_13.Height=15tLabel_13.Text=号别：tLabel_13.Color=蓝SESSION_CODE.Type=时段下拉列表SESSION_CODE.X=550SESSION_CODE.Y=28SESSION_CODE.Width=140SESSION_CODE.Height=23SESSION_CODE.Text=TButtonSESSION_CODE.showID=YSESSION_CODE.showName=YSESSION_CODE.showText=NSESSION_CODE.showValue=NSESSION_CODE.showPy1=YSESSION_CODE.showPy2=YSESSION_CODE.Editable=YSESSION_CODE.Tip=时段SESSION_CODE.TableShowList=nameSESSION_CODE.ModuleParmString=SESSION_CODE.ModuleParmTag=tLabel_0.Type=TLabeltLabel_0.X=505tLabel_0.Y=33tLabel_0.Width=43tLabel_0.Height=15tLabel_0.Text=时段：tLabel_0.Color=蓝E_DATE.Type=TTextFormatE_DATE.X=290E_DATE.Y=31E_DATE.Width=178E_DATE.Height=20E_DATE.Text=TTextFormatE_DATE.FormatType=dateE_DATE.Format=yyyy/MM/dd HH:mm:ssE_DATE.showDownButton=YS_DATE.Type=TTextFormatS_DATE.X=83S_DATE.Y=31S_DATE.Width=178S_DATE.Height=20S_DATE.Text=TTextFormatS_DATE.FormatType=dateS_DATE.Format=yyyy/MM/dd HH:mm:ssS_DATE.showDownButton=YtLabel_10.Type=TLabeltLabel_10.X=270tLabel_10.Y=33tLabel_10.Width=18tLabel_10.Height=15tLabel_10.Text=至tLabel_10.Color=bluetLabel_8.Type=TLabeltLabel_8.X=15tLabel_8.Y=33tLabel_8.Width=72tLabel_8.Height=15tLabel_8.Text=挂号时间：tLabel_8.Color=blue