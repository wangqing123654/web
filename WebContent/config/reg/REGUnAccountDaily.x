## TBuilder Config File ## Title:门诊日结## Company:JavaHis## Author:fudw 2009.07.21## version 1.0#<Type=TFrame>UI.Title=门诊日结UI.MenuConfig=%ROOT%\config\reg\REGUnAccountDailyMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.reg.REGUnAccountDailyUI.item=tPanel_0;tPanel_1;TABLEUI.layout=nullUI.Name=门急诊挂号对账查询UI.Text=门急诊挂号对账查询UI.Tip=门急诊挂号对账查询UI.TopMenu=YUI.TopToolBar=YTABLE.Type=TTableTABLE.X=4TABLE.Y=153TABLE.Width=1009TABLE.Height=590TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoY=NTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.AutoX=YTABLE.Header=收费员,60;票号,100;病案号,100;病患姓名,60;性别,40;票据状态,60;门级别,50;总金额,80,double,########0.00;现金,80,double,########0.00;刷卡,80,double,########0.00;支票,80,double,########0.00;医疗卡,100,double,########0.00;医保卡,80,double,########0.00;特批款,80,double,########0.00;打印时间,90;日结号,100;日结时间,90;日结人员,60,ACCOUNT_SEQ_TTABLE.ParmMap=USER_NAME;PRINT_NO;MR_NO;PAT_NAME;SEX;RESET_TYPE;ADM_TYPE;AR_AMT;PAY_CASH;PAY_BANK_CARD;PAY_CHECK;PAY_MEDICAL_CARD;PAY_INS_CARD;OTHER_FEE1;PRINT_DATE;ACCOUNT_SEQ;ACCOUNT_DATE;ACCOUNT_USERTABLE.LockColumns=allTABLE.HorizontalAlignmentData=TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,left;15,left;16,left;17,leftTABLE.Item=ACCOUNT_SEQ_TtPanel_1.Type=TPaneltPanel_1.X=5tPanel_1.Y=103tPanel_1.Width=1014tPanel_1.Height=48tPanel_1.Border=组tPanel_1.AutoX=YtPanel_1.AutoWidth=YtPanel_1.Item=tLabel_4;START_DATE;tLabel_6;END_DATEEND_DATE.Type=TTextFormatEND_DATE.X=594END_DATE.Y=6END_DATE.Width=161END_DATE.Height=20END_DATE.Text=END_DATE.Format=yyyy/MM/dd HH:mm:ssEND_DATE.FormatType=dateEND_DATE.showDownButton=YEND_DATE.HorizontalAlignment=2tLabel_6.Type=TLabeltLabel_6.X=529tLabel_6.Y=9tLabel_6.Width=64tLabel_6.Height=15tLabel_6.Text=查询迄日:tLabel_6.Color=蓝START_DATE.Type=TTextFormatSTART_DATE.X=335START_DATE.Y=6START_DATE.Width=161START_DATE.Height=20START_DATE.Text=START_DATE.Format=yyyy/MM/dd HH:mm:ssSTART_DATE.showDownButton=YSTART_DATE.FormatType=dateSTART_DATE.HorizontalAlignment=2tLabel_4.Type=TLabeltLabel_4.X=266tLabel_4.Y=9tLabel_4.Width=66tLabel_4.Height=15tLabel_4.Text=查询起日:tLabel_4.Color=蓝tPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=97tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.Border=组tPanel_0.Item=tLabel_0;tLabel_1;tLabel_7;REGION_CODE;CASH_CODE;tLabel_55;ADM_TYPE;ACCOUNT_SEQ_TtPanel_0.AutoHeight=NACCOUNT_SEQ_T.Type=人员ACCOUNT_SEQ_T.X=106ACCOUNT_SEQ_T.Y=47ACCOUNT_SEQ_T.Width=119ACCOUNT_SEQ_T.Height=23ACCOUNT_SEQ_T.Text=ACCOUNT_SEQ_T.HorizontalAlignment=2ACCOUNT_SEQ_T.PopupMenuHeader=代码,100;名称,100ACCOUNT_SEQ_T.PopupMenuWidth=300ACCOUNT_SEQ_T.PopupMenuHeight=300ACCOUNT_SEQ_T.PopupMenuFilter=ID,1;NAME,1;PY1,1ACCOUNT_SEQ_T.FormatType=comboACCOUNT_SEQ_T.ShowDownButton=YACCOUNT_SEQ_T.Tip=人员ACCOUNT_SEQ_T.ShowColumnList=NAMEACCOUNT_SEQ_T.Visible=NADM_TYPE.Type=TComboBoxADM_TYPE.X=590ADM_TYPE.Y=60ADM_TYPE.Width=106ADM_TYPE.Height=23ADM_TYPE.Text=TButtonADM_TYPE.showID=YADM_TYPE.Editable=YADM_TYPE.StringData=[[id,text],[,],[O,门诊],[E,急诊]]ADM_TYPE.TableShowList=textADM_TYPE.SelectedAction=onAdmTypeClickADM_TYPE.Action=tLabel_55.Type=TLabeltLabel_55.X=529tLabel_55.Y=64tLabel_55.Width=54tLabel_55.Height=15tLabel_55.Text=门急别:tLabel_55.Color=蓝CASH_CODE.Type=人员CASH_CODE.X=808CASH_CODE.Y=59CASH_CODE.Width=97CASH_CODE.Height=23CASH_CODE.Text=CASH_CODE.HorizontalAlignment=2CASH_CODE.PopupMenuHeader=代码,100;名称,100CASH_CODE.PopupMenuWidth=300CASH_CODE.PopupMenuHeight=300CASH_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1CASH_CODE.FormatType=comboCASH_CODE.ShowDownButton=YCASH_CODE.Tip=人员CASH_CODE.ShowColumnList=NAMECASH_CODE.PosType=5CASH_CODE.HisOneNullRow=YREGION_CODE.Type=区域下拉列表REGION_CODE.X=363REGION_CODE.Y=60REGION_CODE.Width=135REGION_CODE.Height=23REGION_CODE.Text=TButtonREGION_CODE.showID=YREGION_CODE.showName=YREGION_CODE.showText=NREGION_CODE.showValue=NREGION_CODE.showPy1=NREGION_CODE.showPy2=NREGION_CODE.Editable=YREGION_CODE.Tip=区域REGION_CODE.TableShowList=nameREGION_CODE.ModuleParmString=REGION_CODE.ModuleParmTag=REGION_CODE.Enabled=NREGION_CODE.ExpandWidth=80tLabel_7.Type=TLabeltLabel_7.X=291tLabel_7.Y=64tLabel_7.Width=67tLabel_7.Height=15tLabel_7.Text=区    域:tLabel_7.Color=蓝tLabel_1.Type=TLabeltLabel_1.X=741tLabel_1.Y=63tLabel_1.Width=65tLabel_1.Height=15tLabel_1.Text=收 费 员:tLabel_1.Color=蓝tLabel_0.Type=TLabeltLabel_0.X=369tLabel_0.Y=17tLabel_0.Width=334tLabel_0.Height=24tLabel_0.Text=门 急 诊 挂 号 对 账 查 询tLabel_0.FontSize=24tLabel_0.FontName=宋体tLabel_0.Color=蓝tLabel_0.VerticalAlignment=0