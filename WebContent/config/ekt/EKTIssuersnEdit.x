<Type=TFrame>UI.Title=字典维护窗口UI.Width=1500UI.Height=1500UI.toolbar=YUI.Item=tPanel_1;TABLEUI.AutoWidth=YUI.AutoX=YUI.AutoY=YUI.AutoHeight=YUI.AutoSize=0UI.Y=0UI.ShowTitle=YUI.MenuConfig=%ROOT%\config\ekt\EKTIssuersnEditMenu.xUI.TopToolBar=YUI.TopMenu=YUI.ControlClassName=com.javahis.ui.ekt.EKTIssuersnEditControlUI.FocusList=ID;NAME;PY1;PY2;ENG_DESC;SEQ;DESCRIPTIONTABLE.Type=TTableTABLE.X=1TABLE.Y=120TABLE.Width=81TABLE.Height=1319TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=YTABLE.AutoHeight=YTABLE.AutoWidth=YTABLE.ParmMap=ISSUERSN_CODE;ISSUERSN_DESC;FACTORAGE_FEE;OPT_USER;OPT_DATE;OPT_TERM;FLGTABLE.Header=编号,100;名称,100;手续费,100,double,#########0.00;操作人,100,OPT_USER;操作时间,100;操作IP,120;默认,40,booleanTABLE.LockRows=TABLE.LockColumns=allTABLE.ClickedAction=onClickTableTABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,right;3,left;4,left;5,leftTABLE.Item=OPT_USERtPanel_1.Type=TPaneltPanel_1.X=7tPanel_1.Y=0tPanel_1.Width=1500tPanel_1.Height=120tPanel_1.Border=组|信息维护tPanel_1.AutoX=YtPanel_1.AutoY=YtPanel_1.AutoWidth=YtPanel_1.AutoHeight=NtPanel_1.AutoSize=0tPanel_1.Item=tLabel_16;ISSUERSN_CODE;tLabel_17;NAME;tLabel_19;FACTORAGE_FEE;GROUP;tLabel_0;tLabel_1;tLabel_2;FLG;OPT_USEROPT_USER.Type=人员OPT_USER.X=465OPT_USER.Y=89OPT_USER.Width=81OPT_USER.Height=23OPT_USER.Text=OPT_USER.HorizontalAlignment=2OPT_USER.PopupMenuHeader=代码,100;名称,100OPT_USER.PopupMenuWidth=300OPT_USER.PopupMenuHeight=300OPT_USER.PopupMenuFilter=ID,1;NAME,1;PY1,1OPT_USER.FormatType=comboOPT_USER.ShowDownButton=YOPT_USER.Tip=人员OPT_USER.ShowColumnList=NAMEOPT_USER.Visible=NFLG.Type=TCheckBoxFLG.X=310FLG.Y=74FLG.Width=81FLG.Height=23FLG.Text=默认注记tLabel_2.Type=TLabeltLabel_2.X=13tLabel_2.Y=21tLabel_2.Width=115tLabel_2.Height=16tLabel_2.Text=医疗卡发卡原因tLabel_2.Color=蓝tLabel_1.Type=TLabeltLabel_1.X=455tLabel_1.Y=49tLabel_1.Width=10tLabel_1.Height=15tLabel_1.Text=*tLabel_1.Color=红tLabel_0.Type=TLabeltLabel_0.X=211tLabel_0.Y=50tLabel_0.Width=10tLabel_0.Height=15tLabel_0.Text=*tLabel_0.Color=红GROUP.Type=TLabelGROUP.X=14GROUP.Y=22GROUP.Width=436GROUP.Height=15GROUP.Text=GROUP.Color=蓝FACTORAGE_FEE.Type=TNumberTextFieldFACTORAGE_FEE.X=70FACTORAGE_FEE.Y=77FACTORAGE_FEE.Width=139FACTORAGE_FEE.Height=20FACTORAGE_FEE.Text=0.00FACTORAGE_FEE.Format=#########0.00tLabel_19.Type=TLabeltLabel_19.X=13tLabel_19.Y=80tLabel_19.Width=56tLabel_19.Height=15tLabel_19.Text=手续费:NAME.Type=TTextFieldNAME.X=313NAME.Y=47NAME.Width=139NAME.Height=20NAME.Text=NAME.Tip=名称NAME.InputLength=200NAME.Action=onUserNameActiontLabel_17.Type=TLabeltLabel_17.X=239tLabel_17.Y=50tLabel_17.Width=56tLabel_17.Height=15tLabel_17.Text=名称:tLabel_17.Color=蓝ISSUERSN_CODE.Type=TTextFieldISSUERSN_CODE.X=70ISSUERSN_CODE.Y=47ISSUERSN_CODE.Width=139ISSUERSN_CODE.Height=20ISSUERSN_CODE.Text=ISSUERSN_CODE.Tip=编号ISSUERSN_CODE.InputLength=20ISSUERSN_CODE.Enabled=YtLabel_16.Type=TLabeltLabel_16.X=13tLabel_16.Y=50tLabel_16.Width=56tLabel_16.Height=15tLabel_16.Text=编号:tLabel_16.Color=BLUE