## TBuilder Config File ## Title:## Company:JavaHis## Author:杨菁菁 2015.05.19## version 1.0#<Type=TFrame>UI.Title=门急诊及住院病人人均费用同期对比表UI.MenuConfig=%ROOT%\config\sta\STATotalMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.sta.STATotalControlUI.item=TABLE;tPanel_1UI.layout=nullUI.Text=门急诊及住院病人人均费用同期对比表UI.Tip=门急诊及住院病人人均费用同期对比表UI.TopMenu=YUI.TopToolBar=YtPanel_1.Type=TPaneltPanel_1.X=5tPanel_1.Y=14tPanel_1.Width=1008tPanel_1.Height=81tPanel_1.Border=组tPanel_1.Item=tLabel_0;RADIO_YEAR;START_YEAR;tLabel_5;END_YEAR;RADIO_MONTH;START_MONTH;tLabel_6;END_MONTH;tLabel_7;DEPT_CODE;STATION_CODESTATION_CODE.Type=病区STATION_CODE.X=424STATION_CODE.Y=50STATION_CODE.Width=81STATION_CODE.Height=23STATION_CODE.Text=STATION_CODE.HorizontalAlignment=2STATION_CODE.PopupMenuHeader=代码,100;名称,100STATION_CODE.PopupMenuWidth=300STATION_CODE.PopupMenuHeight=300STATION_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1STATION_CODE.FormatType=comboSTATION_CODE.ShowDownButton=YSTATION_CODE.Tip=病区STATION_CODE.ShowColumnList=NAMESTATION_CODE.HisOneNullRow=YSTATION_CODE.Visible=NDEPT_CODE.Type=科室下拉列表DEPT_CODE.X=471DEPT_CODE.Y=18DEPT_CODE.Width=190DEPT_CODE.Height=23DEPT_CODE.Text=TButtonDEPT_CODE.showID=YDEPT_CODE.showName=YDEPT_CODE.showText=NDEPT_CODE.showValue=NDEPT_CODE.showPy1=NDEPT_CODE.showPy2=NDEPT_CODE.Editable=YDEPT_CODE.Tip=科室DEPT_CODE.TableShowList=nameDEPT_CODE.Classify=0tLabel_7.Type=TLabeltLabel_7.X=422tLabel_7.Y=20tLabel_7.Width=46tLabel_7.Height=15tLabel_7.Text=科室:tLabel_7.Color=蓝END_MONTH.Type=TTextFormatEND_MONTH.X=271END_MONTH.Y=49END_MONTH.Width=80END_MONTH.Height=20END_MONTH.Text=TTextFormatEND_MONTH.FormatType=dateEND_MONTH.Format=yyyy/MMEND_MONTH.HorizontalAlignment=2END_MONTH.Action=tLabel_6.Type=TLabeltLabel_6.X=251tLabel_6.Y=51tLabel_6.Width=17tLabel_6.Height=15tLabel_6.Text=至START_MONTH.Type=TTextFormatSTART_MONTH.X=166START_MONTH.Y=49START_MONTH.Width=80START_MONTH.Height=20START_MONTH.Text=TTextFormatSTART_MONTH.FormatType=dateSTART_MONTH.Format=yyyy/MMSTART_MONTH.HorizontalAlignment=2RADIO_MONTH.Type=TRadioButtonRADIO_MONTH.X=118RADIO_MONTH.Y=48RADIO_MONTH.Width=44RADIO_MONTH.Height=23RADIO_MONTH.Text=月RADIO_MONTH.Group=RADIO_MONTH.Action=onMonthSelectedEND_YEAR.Type=TTextFormatEND_YEAR.X=271END_YEAR.Y=18END_YEAR.Width=80END_YEAR.Height=20END_YEAR.Text=END_YEAR.FormatType=dateEND_YEAR.Format=yyyyEND_YEAR.HorizontalAlignment=2tLabel_5.Type=TLabeltLabel_5.X=251tLabel_5.Y=20tLabel_5.Width=20tLabel_5.Height=15tLabel_5.Text=至START_YEAR.Type=TTextFormatSTART_YEAR.X=166START_YEAR.Y=18START_YEAR.Width=80START_YEAR.Height=20START_YEAR.Text=START_YEAR.FormatType=dateSTART_YEAR.Format=yyyySTART_YEAR.HorizontalAlignment=2RADIO_YEAR.Type=TRadioButtonRADIO_YEAR.X=118RADIO_YEAR.Y=15RADIO_YEAR.Width=42RADIO_YEAR.Height=25RADIO_YEAR.Text=年RADIO_YEAR.Group=RADIO_YEAR.Selected=YRADIO_YEAR.Action=onYearSelectedtLabel_0.Type=TLabeltLabel_0.X=40tLabel_0.Y=20tLabel_0.Width=72tLabel_0.Height=15tLabel_0.Text=统计时间:tLabel_0.Color=蓝TABLE.Type=TTableTABLE.X=5TABLE.Y=99TABLE.Width=1008TABLE.Height=488TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.Header=日期,120;科室,125,DEPT_CODE;门诊实收,80;门诊人次,80;人均门诊（元）,100;急诊实收,80;急诊人次,80;人均急诊（元）,100;住院实收,80;住院人次,80;住院人均（元）,100TABLE.ParmMap=DAY;DEPT_CODE;O_AR_AMT;O_TOT_COUNT;O_PER;E_AR_AMT;E_TOT_COUNT;E_PER;I_AR_AMT;I_TOT_COUNT;I_PERTABLE.AutoX=YTABLE.AutoHeight=YTABLE.Item=DEPT_CODETABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,rightTABLE.LockColumns=all