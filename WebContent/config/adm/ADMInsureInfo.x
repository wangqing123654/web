## TBuilder Config File ## Title:## Company:JavaHis## Author:pangben 2014.07.30## version 1.0#<Type=TFrame>UI.Title=住院记录UI.MenuConfig=%ROOT%/config/adm/ADMInsureInfoMenu.xUI.Width=568UI.Height=427UI.toolbar=YUI.controlclassname=com.javahis.ui.adm.ADMInsureInfoControlUI.item=tPanel_1;tPanel_3UI.layout=nullUI.LoadFlg=YUI.TopMenu=YUI.ShowMenu=YtPanel_3.Type=TPaneltPanel_3.X=3tPanel_3.Y=61tPanel_3.Width=560tPanel_3.Height=361tPanel_3.Border=组|住院记录tPanel_3.AutoWidth=YtPanel_3.AutoHeight=YtPanel_3.Item=TABLE;DEPT_CODE;DR_CODEDR_CODE.Type=人员DR_CODE.X=88DR_CODE.Y=-26DR_CODE.Width=81DR_CODE.Height=23DR_CODE.Text=DR_CODE.HorizontalAlignment=2DR_CODE.PopupMenuHeader=代码,100;名称,100DR_CODE.PopupMenuWidth=300DR_CODE.PopupMenuHeight=300DR_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DR_CODE.FormatType=comboDR_CODE.ShowDownButton=YDR_CODE.Tip=人员DR_CODE.ShowColumnList=NAMEDEPT_CODE.Type=科室DEPT_CODE.X=414DEPT_CODE.Y=-31DEPT_CODE.Width=81DEPT_CODE.Height=23DEPT_CODE.Text=DEPT_CODE.HorizontalAlignment=2DEPT_CODE.PopupMenuHeader=代码,100;名称,100DEPT_CODE.PopupMenuWidth=300DEPT_CODE.PopupMenuHeight=300DEPT_CODE.FormatType=comboDEPT_CODE.ShowDownButton=YDEPT_CODE.Tip=科室DEPT_CODE.ShowColumnList=NAMEDEPT_CODE.HisOneNullRow=YTABLE.Type=TTableTABLE.X=10TABLE.Y=22TABLE.Width=539TABLE.Height=328TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Header=病案号,100;姓名,80;性别,40,SEX_CODE;入院日期,150,Timestamp,yyyy/MM/dd HH:mm:ss;出院日期,150,Timestamp,yyyy/MM/dd HH:mm:ssTABLE.ParmMap=MR_NO;PAT_NAME;SEX_CODE;IN_DATE;DS_DATETABLE.LockColumns=allTABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,right;8,right;9,left;10,left;11,leftTABLE.Item=SEX_CODE;DEPT_CODE;STATION_CODE;DR_CODE;LUMPWORK_CODETABLE.DoubleClickedAction=onSavetPanel_1.Type=TPaneltPanel_1.X=3tPanel_1.Y=-1tPanel_1.Width=560tPanel_1.Height=61tPanel_1.Border=组|基本信息tPanel_1.AutoWidth=YtPanel_1.Item=tLabel_12;tLabel_13;tLabel_14;MR_NO;PAT_NAME;SEX_CODE;STATION_CODE;SEX_CODE1;LUMPWORK_CODELUMPWORK_CODE.Type=包干套餐下拉区域LUMPWORK_CODE.X=415LUMPWORK_CODE.Y=66LUMPWORK_CODE.Width=100LUMPWORK_CODE.Height=23LUMPWORK_CODE.Text=LUMPWORK_CODE.HorizontalAlignment=2LUMPWORK_CODE.PopupMenuHeader=代码,40;名称,100;金额,56LUMPWORK_CODE.PopupMenuWidth=250LUMPWORK_CODE.PopupMenuHeight=100LUMPWORK_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1LUMPWORK_CODE.FormatType=comboLUMPWORK_CODE.ShowDownButton=YLUMPWORK_CODE.Tip=包干套餐LUMPWORK_CODE.ShowColumnList=NAME;FEELUMPWORK_CODE.HisOneNullRow=YSEX_CODE1.Type=性别下拉区域SEX_CODE1.X=431SEX_CODE1.Y=21SEX_CODE1.Width=81SEX_CODE1.Height=23SEX_CODE1.Text=SEX_CODE1.HorizontalAlignment=2SEX_CODE1.PopupMenuHeader=代码,100;名称,100SEX_CODE1.PopupMenuWidth=300SEX_CODE1.PopupMenuHeight=300SEX_CODE1.PopupMenuFilter=ID,1;NAME,1;PY1,1SEX_CODE1.FormatType=comboSEX_CODE1.ShowDownButton=YSEX_CODE1.Tip=性别下拉区域SEX_CODE1.ShowColumnList=NAMESEX_CODE1.Visible=YSEX_CODE1.Enabled=NSTATION_CODE.Type=病区STATION_CODE.X=77STATION_CODE.Y=74STATION_CODE.Width=81STATION_CODE.Height=23STATION_CODE.Text=STATION_CODE.HorizontalAlignment=2STATION_CODE.PopupMenuHeader=代码,100;名称,100STATION_CODE.PopupMenuWidth=300STATION_CODE.PopupMenuHeight=300STATION_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1STATION_CODE.FormatType=comboSTATION_CODE.ShowDownButton=YSTATION_CODE.Tip=病区STATION_CODE.ShowColumnList=NAMESEX_CODE.Type=性别下拉列表SEX_CODE.X=527SEX_CODE.Y=66SEX_CODE.Width=113SEX_CODE.Height=23SEX_CODE.Text=TButtonSEX_CODE.showID=YSEX_CODE.showName=YSEX_CODE.showText=NSEX_CODE.showValue=NSEX_CODE.showPy1=NSEX_CODE.showPy2=NSEX_CODE.Editable=YSEX_CODE.Tip=性别SEX_CODE.TableShowList=nameSEX_CODE.ModuleParmString=GROUP_ID:SYS_SEXSEX_CODE.ModuleParmTag=SEX_CODE.Enabled=YPAT_NAME.Type=TTextFieldPAT_NAME.X=265PAT_NAME.Y=22PAT_NAME.Width=116PAT_NAME.Height=20PAT_NAME.Text=PAT_NAME.Enabled=NMR_NO.Type=TTextFieldMR_NO.X=82MR_NO.Y=22MR_NO.Width=123MR_NO.Height=20MR_NO.Text=MR_NO.Enabled=NtLabel_14.Type=TLabeltLabel_14.X=392tLabel_14.Y=25tLabel_14.Width=38tLabel_14.Height=15tLabel_14.Text=性别:tLabel_13.Type=TLabeltLabel_13.X=222tLabel_13.Y=25tLabel_13.Width=39tLabel_13.Height=15tLabel_13.Text=姓名:tLabel_12.Type=TLabeltLabel_12.X=30tLabel_12.Y=25tLabel_12.Width=54tLabel_12.Height=15tLabel_12.Text=病案号: