<Type=TFrame>UI.Title=挂号病历待出库确认UI.MenuConfig=%ROOT%\config\mro\MRORegReadyOutMenu.xUI.Width=1131UI.Height=773UI.toolbar=YUI.controlclassname=com.javahis.ui.mro.MROReadyOutControlUI.item=tPanel_3;tPanel_2;SELECT_ALLUI.layout=nullUI.FocusList=UI.ShowMenu=YUI.ShowTitle=NUI.TopMenu=YUI.TopToolBar=YSELECT_ALL.Type=TCheckBoxSELECT_ALL.X=4SELECT_ALL.Y=118SELECT_ALL.Width=56SELECT_ALL.Height=23SELECT_ALL.Text=全选SELECT_ALL.Action=onCheckSelectAlltPanel_2.Type=TPaneltPanel_2.X=5tPanel_2.Y=5tPanel_2.Width=1014tPanel_2.Height=107tPanel_2.Border=组|查询条件tPanel_2.AutoY=YtPanel_2.AutoWidth=YtPanel_2.AutoX=YtPanel_2.Item=tLabel_16;S_DATE;tLabel_17;tLabel_18;E_DATE;STATUS;PAT_NAME;tLabel_0;MR_NO;tLabel_1;tTextField_0;tTextField_1;tLabel_2;tLabel_3;CONFIRM_STATUS_NO;CONFIRM_STATUS_YES;tLabel_4;SESSION_CODE;REG_DEPT_CODE;REG_DR_CODEREG_DR_CODE.Type=人员REG_DR_CODE.X=888REG_DR_CODE.Y=31REG_DR_CODE.Width=110REG_DR_CODE.Height=21REG_DR_CODE.Text=REG_DR_CODE.HorizontalAlignment=2REG_DR_CODE.PopupMenuHeader=代码,100;名称,100REG_DR_CODE.PopupMenuWidth=300REG_DR_CODE.PopupMenuHeight=300REG_DR_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1REG_DR_CODE.FormatType=comboREG_DR_CODE.ShowDownButton=YREG_DR_CODE.Tip=人员REG_DR_CODE.ShowColumnList=NAMEREG_DR_CODE.Dept=<REG_DEPT_CODE>REG_DR_CODE.HisOneNullRow=YREG_DR_CODE.PosType=1REG_DEPT_CODE.Type=科室REG_DEPT_CODE.X=676REG_DEPT_CODE.Y=30REG_DEPT_CODE.Width=126REG_DEPT_CODE.Height=21REG_DEPT_CODE.Text=REG_DEPT_CODE.HorizontalAlignment=2REG_DEPT_CODE.PopupMenuHeader=代码,100;名称,100REG_DEPT_CODE.PopupMenuWidth=300REG_DEPT_CODE.PopupMenuHeight=300REG_DEPT_CODE.FormatType=comboREG_DEPT_CODE.ShowDownButton=YREG_DEPT_CODE.Tip=科室REG_DEPT_CODE.ShowColumnList=NAMEREG_DEPT_CODE.HisOneNullRow=YREG_DEPT_CODE.Action=REG_DR_CODE|onQueryREG_DEPT_CODE.ClassIfy=0SESSION_CODE.Type=时段下拉列表SESSION_CODE.X=509SESSION_CODE.Y=30SESSION_CODE.Width=88SESSION_CODE.Height=20SESSION_CODE.Text=TButtonSESSION_CODE.showID=NSESSION_CODE.showName=YSESSION_CODE.showText=NSESSION_CODE.showValue=NSESSION_CODE.showPy1=NSESSION_CODE.showPy2=NSESSION_CODE.Editable=YSESSION_CODE.Tip=时段SESSION_CODE.TableShowList=nameSESSION_CODE.ModuleParmString=SESSION_CODE.ModuleParmTag=SESSION_CODE.ExpandWidth=0SESSION_CODE.AdmType=tLabel_4.Type=TLabeltLabel_4.X=469tLabel_4.Y=74tLabel_4.Width=72tLabel_4.Height=15tLabel_4.Text=待出库状态tLabel_4.Color=蓝CONFIRM_STATUS_YES.Type=TRadioButtonCONFIRM_STATUS_YES.X=611CONFIRM_STATUS_YES.Y=69CONFIRM_STATUS_YES.Width=70CONFIRM_STATUS_YES.Height=23CONFIRM_STATUS_YES.Text=已确认CONFIRM_STATUS_YES.Group=CONFIRM_STATUS_YES.Color=蓝CONFIRM_STATUS_YES.Action=onQueryCONFIRM_STATUS_NO.Type=TRadioButtonCONFIRM_STATUS_NO.X=545CONFIRM_STATUS_NO.Y=69CONFIRM_STATUS_NO.Width=70CONFIRM_STATUS_NO.Height=23CONFIRM_STATUS_NO.Text=未确认CONFIRM_STATUS_NO.Group=CONFIRM_STATUS_NO.Color=蓝CONFIRM_STATUS_NO.Selected=YCONFIRM_STATUS_NO.Action=onQuerytLabel_3.Type=TLabeltLabel_3.X=824tLabel_3.Y=34tLabel_3.Width=62tLabel_3.Height=15tLabel_3.Text=挂号医生tLabel_3.Color=蓝tLabel_2.Type=TLabeltLabel_2.X=612tLabel_2.Y=33tLabel_2.Width=60tLabel_2.Height=15tLabel_2.Text=挂号科室tLabel_2.Color=蓝tTextField_1.Type=TTextFieldtTextField_1.X=389tTextField_1.Y=29tTextField_1.Width=65tTextField_1.Height=20tTextField_1.Text=23:59:59tTextField_1.Enabled=NtTextField_0.Type=TTextFieldtTextField_0.X=195tTextField_0.Y=29tTextField_0.Width=61tTextField_0.Height=20tTextField_0.Text=00:00:00tTextField_0.Enabled=NtLabel_1.Type=TLabeltLabel_1.X=263tLabel_1.Y=35tLabel_1.Width=13tLabel_1.Height=19tLabel_1.Text=~tLabel_1.FontSize=18MR_NO.Type=TTextFormatMR_NO.X=92MR_NO.Y=70MR_NO.Width=113MR_NO.Height=20MR_NO.Text=MR_NO.HorizontalAlignment=2MR_NO.Action=onQueryByMrNotLabel_0.Type=TLabeltLabel_0.X=24tLabel_0.Y=31tLabel_0.Width=60tLabel_0.Height=15tLabel_0.Text=挂号日期tLabel_0.Color=蓝tPanel_3.Type=TPaneltPanel_3.X=8tPanel_3.Y=139tPanel_3.Width=1121tPanel_3.Height=629tPanel_3.Border=组|详情tPanel_3.AutoX=YtPanel_3.AutoWidth=YtPanel_3.AutoHeight=YtPanel_3.Item=TABLE;SEX_CODESEX_CODE.Type=性别下拉列表SEX_CODE.X=152SEX_CODE.Y=453SEX_CODE.Width=81SEX_CODE.Height=23SEX_CODE.Text=TButtonSEX_CODE.showID=YSEX_CODE.showName=YSEX_CODE.showText=NSEX_CODE.showValue=NSEX_CODE.showPy1=YSEX_CODE.showPy2=YSEX_CODE.Editable=YSEX_CODE.Tip=性别SEX_CODE.TableShowList=nameSEX_CODE.ModuleParmString=GROUP_ID:SYS_SEXSEX_CODE.ModuleParmTag=TABLE.Type=TTableTABLE.X=10TABLE.Y=24TABLE.Width=1099TABLE.Height=589TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=YTABLE.AutoY=YTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Header=选,30,boolean;挂号日期,80;挂号时段,130,SESSION_CODE;就诊序号,60;病案号,100;姓名,60;性别,60,SEX_CODE;电话,120;在库状态,120,STATUS;挂号科室,120,REG_DEPT_CODE;挂号医生,80,REG_DR_CODE;案卷号,60;册数,50;货位号,80;取消状态,100TABLE.LockColumns=1,2,3,4,5,6,7,8,9,10,11,12,13,14TABLE.ColumnHorizontalAlignmentData=1,left;2,left;3,right;4,center;5,left;6,left;7,left;8,left;9,left;10,left;11,center;12,center;13,left;14,leftTABLE.ParmMap=FLG;ADM_DATE;SESSION_CODE;QUE_NO;MR_NO;PAT_NAME;SEX_CODE;CELL_PHONE;STATUS;DEPT_CODE;DR_CODE;BOX_CODE;BOOK_NO;CURT_LOCATION;CANCEL_STATUSTABLE.Item=SESSION_CODE;SEX_CODE;STATUS;REG_DEPT_CODE;REG_DR_CODETABLE.LockRows=STATUS.Type=TComboBoxSTATUS.X=352STATUS.Y=70STATUS.Width=102STATUS.Height=23STATUS.Text=TButtonSTATUS.showID=NSTATUS.Editable=NSTATUS.StringData=[[id,text],[,],[0,未建档],[1,在库],[2,已出库]]STATUS.ShowName=NSTATUS.ShowValue=NSTATUS.TableShowList=STATUS.ModuleParmString=STATUS.ParmMap=STATUS.ShowText=YPAT_NAME.Type=TTextFieldPAT_NAME.X=215PAT_NAME.Y=70PAT_NAME.Width=69PAT_NAME.Height=20PAT_NAME.Text=PAT_NAME.Enabled=NPAT_NAME.HorizontalAlignment=2tLabel_18.Type=TLabeltLabel_18.X=24tLabel_18.Y=72tLabel_18.Width=52tLabel_18.Height=15tLabel_18.Text=病案号tLabel_18.Color=蓝tLabel_17.Type=TLabeltLabel_17.X=312tLabel_17.Y=73tLabel_17.Width=42tLabel_17.Height=15tLabel_17.Text=状态tLabel_17.Color=bluetLabel_16.Type=TLabeltLabel_16.X=471tLabel_16.Y=32tLabel_16.Width=32tLabel_16.Height=15tLabel_16.Text=时段tLabel_16.Color=蓝E_DATE.Type=TTextFormatE_DATE.X=284E_DATE.Y=29E_DATE.Width=97E_DATE.Height=20E_DATE.Text=TTextFormatE_DATE.showDownButton=YE_DATE.FormatType=dateE_DATE.Format=yyyy/MM/ddE_DATE.HorizontalAlignment=2S_DATE.Type=TTextFormatS_DATE.X=90S_DATE.Y=29S_DATE.Width=98S_DATE.Height=20S_DATE.Text=TTextFormatS_DATE.showDownButton=YS_DATE.FormatType=dateS_DATE.Format=yyyy/MM/ddS_DATE.HorizontalAlignment=2