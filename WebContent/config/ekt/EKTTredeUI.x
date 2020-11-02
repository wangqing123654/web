## TBuilder Config File ## Title:## Company:JavaHis## Author:zhangy 2010.09.16## version 1.0#<Type=TFrame>UI.Title=医疗卡交易记录UI.MenuConfig=%ROOT%\config\ekt\EKTTredeMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.ekt.EKTTredeControlUI.item=tLabel_0;PAT_NAME;tLabel_1;CARD_NO;tLabel_2;MR_NO;tLabel_3;CASE_NO;tLabel_4;BUSINESS_NO;tLabel_5;STATE;tLabel_6;BUSINESS_TYPE;TABLE;起讫时间:;START_DATE;tLabel_8;END_DATE;tLabel_9;USER_IDUI.layout=nullUI.ShowTitle=NUI.TopToolBar=YUI.TopMenu=YUSER_ID.Type=人员USER_ID.X=522USER_ID.Y=11USER_ID.Width=130USER_ID.Height=23USER_ID.Text=USER_ID.HorizontalAlignment=2USER_ID.PopupMenuHeader=代码,100;名称,100USER_ID.PopupMenuWidth=300USER_ID.PopupMenuHeight=300USER_ID.PopupMenuFilter=ID,1;NAME,1;PY1,1USER_ID.FormatType=comboUSER_ID.ShowDownButton=YUSER_ID.Tip=人员USER_ID.ShowColumnList=NAMEUSER_ID.HisOneNullRow=YUSER_ID.PosType=5tLabel_9.Type=TLabeltLabel_9.X=444tLabel_9.Y=16tLabel_9.Width=72tLabel_9.Height=15tLabel_9.Text=收费人员:tLabel_9.Color=blueEND_DATE.Type=TTextFormatEND_DATE.X=272END_DATE.Y=13END_DATE.Width=160END_DATE.Height=20END_DATE.Text=END_DATE.showDownButton=YEND_DATE.FormatType=dateEND_DATE.Format=yyyy/MM/dd HH:mm:sstLabel_8.Type=TLabeltLabel_8.X=247tLabel_8.Y=16tLabel_8.Width=20tLabel_8.Height=15tLabel_8.Text=至tLabel_8.Color=blueSTART_DATE.Type=TTextFormatSTART_DATE.X=78START_DATE.Y=13START_DATE.Width=160START_DATE.Height=20START_DATE.Text=START_DATE.FormatType=dateSTART_DATE.showDownButton=YSTART_DATE.Format=yyyy/MM/dd HH:mm:ss起讫时间:.Type=TLabel起讫时间:.X=9起讫时间:.Y=16起讫时间:.Width=66起讫时间:.Height=15起讫时间:.Text=起讫时间:起讫时间:.Color=blueTABLE.Type=TTableTABLE.X=15TABLE.Y=81TABLE.Width=81TABLE.Height=662TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=YTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Header=病案号,100;医疗卡卡号,120;就诊序号,100;内部交易号,100;病患名称,100;刷卡前金额,90,double,###0.00;刷卡金额,90,double,###0.00;状态,50,STATE;类型,120,BUSINESS_TYPE;操作人,100,USER_ID;操作时间,160,timestamp,yyyy/MM/dd HH:mm:ss;操作IP,120TABLE.ParmMap=MR_NO;CARD_NO;CASE_NO;BUSINESS_NO;PAT_NAME;OLD_AMT;AMT;STATE;BUSINESS_TYPE;OPT_USER;OPT_DATE;OPT_TERM TABLE.LockColumns=allTABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,right;6,right;7,left;8,left;9,left;10,left;11,leftTABLE.Item=STATE;BUSINESS_TYPE;USER_IDBUSINESS_TYPE.Type=TComboBoxBUSINESS_TYPE.X=899BUSINESS_TYPE.Y=47BUSINESS_TYPE.Width=120BUSINESS_TYPE.Height=23BUSINESS_TYPE.Text=TButtonBUSINESS_TYPE.showID=YBUSINESS_TYPE.Editable=YBUSINESS_TYPE.ShowName=YBUSINESS_TYPE.ShowText=NBUSINESS_TYPE.TableShowList=nameBUSINESS_TYPE.StringData=[[id,name],[,],[REG,挂号收费],[REGT,挂号退费],[OPB,门诊收费收费],[OPBT,医保退费],[ODO,门诊医生站收费]]BUSINESS_TYPE.ExpandWidth=20tLabel_6.Type=TLabeltLabel_6.X=836tLabel_6.Y=51tLabel_6.Width=51tLabel_6.Height=15tLabel_6.Text=类型:tLabel_6.Color=blueSTATE.Type=TComboBoxSTATE.X=707STATE.Y=46STATE.Width=120STATE.Height=23STATE.Text=TButtonSTATE.showID=YSTATE.Editable=YSTATE.ShowText=NSTATE.ShowName=YSTATE.TableShowList=nameSTATE.StringData=[[id,name],[,],[1,收费],[3,作废],[0,充值],[4,制卡],[5,补卡],[7,退费],[8,换卡]]tLabel_5.Type=TLabeltLabel_5.X=656tLabel_5.Y=50tLabel_5.Width=49tLabel_5.Height=14tLabel_5.Text=状态:tLabel_5.Color=blueBUSINESS_NO.Type=TTextFieldBUSINESS_NO.X=522BUSINESS_NO.Y=47BUSINESS_NO.Width=130BUSINESS_NO.Height=20BUSINESS_NO.Text=tLabel_4.Type=TLabeltLabel_4.X=443tLabel_4.Y=50tLabel_4.Width=83tLabel_4.Height=15tLabel_4.Text=内部交易号:tLabel_4.Color=blueCASE_NO.Type=TTextFieldCASE_NO.X=899CASE_NO.Y=13CASE_NO.Width=120CASE_NO.Height=20CASE_NO.Text=tLabel_3.Type=TLabeltLabel_3.X=835tLabel_3.Y=16tLabel_3.Width=63tLabel_3.Height=15tLabel_3.Text=就诊序号:tLabel_3.Color=blueMR_NO.Type=TTextFieldMR_NO.X=707MR_NO.Y=13MR_NO.Width=120MR_NO.Height=20MR_NO.Text=MR_NO.Action=onMrNoActiontLabel_2.Type=TLabeltLabel_2.X=656tLabel_2.Y=16tLabel_2.Width=50tLabel_2.Height=14tLabel_2.Text=病案号:tLabel_2.Color=blueCARD_NO.Type=TTextFieldCARD_NO.X=78CARD_NO.Y=47CARD_NO.Width=120CARD_NO.Height=20CARD_NO.Text=tLabel_1.Type=TLabeltLabel_1.X=9tLabel_1.Y=50tLabel_1.Width=68tLabel_1.Height=15tLabel_1.Text=医疗卡号:tLabel_1.Color=bluePAT_NAME.Type=TTextFieldPAT_NAME.X=302PAT_NAME.Y=47PAT_NAME.Width=130PAT_NAME.Height=20PAT_NAME.Text=tLabel_0.Type=TLabeltLabel_0.X=217tLabel_0.Y=50tLabel_0.Width=82tLabel_0.Height=15tLabel_0.Text=病患姓名:tLabel_0.Color=blue