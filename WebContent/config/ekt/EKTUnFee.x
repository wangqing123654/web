## TBuilder Config File ## Title:## Company:JavaHis## Author:庞犇 2011.09.29## version 1.0#<Type=TFrame>UI.Title=医疗卡退费UI.MenuConfig=%ROOT%\config\ekt\EKTUnFeeMenu.xUI.Width=995UI.Height=713UI.toolbar=YUI.controlclassname=com.javahis.ui.ekt.EKTUnFeeControlUI.item=tPanel_11;tPanel_12;tPanel_0;tLabel_9;tPanel_1UI.layout=nullUI.TopMenu=YUI.TopToolBar=YUI.ShowMenu=YUI.FocusList=MR_NO;PAT_NAME;OLD_EKTFEE;DESCRIPTION;UN_FEEUI.AutoHeight=YtPanel_1.Type=TPaneltPanel_1.X=632tPanel_1.Y=432tPanel_1.Width=985tPanel_1.Height=318tPanel_1.Border=组|历史充值记录tPanel_1.AutoX=NtPanel_1.AutoWidth=YtPanel_1.AutoHeight=YtPanel_1.Item=TABLE1TABLE1.Type=TTableTABLE1.X=44TABLE1.Y=34TABLE1.Width=336TABLE1.Height=277TABLE1.SpacingRow=1TABLE1.RowHeight=20TABLE1.AutoX=YTABLE1.AutoY=YTABLE1.AutoWidth=YTABLE1.AutoHeight=YTABLE1.Header=充值日期,80;充值金额,80,double,###0.00;充值方式,100,GATHER_TYPE;备注,150TABLE1.LockColumns=ALLTABLE1.ColumnHorizontalAlignmentData=0,left;1,right;2,leftTABLE1.Item=GATHER_TYPETABLE1.ParmMap=OPT_DATE;AMT;GATHER_TYPE;DESCRIPTIONtLabel_9.Type=TLabeltLabel_9.X=42tLabel_9.Y=15tLabel_9.Width=106tLabel_9.Height=79tLabel_9.Text=退费tLabel_9.AutoSize=20tLabel_9.FontSize=50tLabel_9.Color=红tPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=432tPanel_0.Width=626tPanel_0.Height=318tPanel_0.Border=组|退费记录tPanel_0.Item=TABLE;ACCNT_TYPEtPanel_0.AutoHeight=YtPanel_0.AutoX=NtPanel_0.AutoWidth=NACCNT_TYPE.Type=TComboBoxACCNT_TYPE.X=88ACCNT_TYPE.Y=212ACCNT_TYPE.Width=81ACCNT_TYPE.Height=23ACCNT_TYPE.Text=TButtonACCNT_TYPE.showID=YACCNT_TYPE.Editable=YACCNT_TYPE.TableShowList=textACCNT_TYPE.StringData=[[id,text],[4,充值],[6,退费]]ACCNT_TYPE.Visible=NTABLE.Type=TTableTABLE.X=9TABLE.Y=21TABLE.Width=604TABLE.Height=277TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=YTABLE.AutoWidth=YTABLE.AutoY=YTABLE.AutoHeight=YTABLE.Header=卡号,120;退费序号,120;退费日期,80;退费金额,80,double,###0.00;退费方式,60,GATHER_TYPE;退费人员,80,CREAT_USER;类型,60,ACCNT_TYPETABLE.ParmMap=EKT_CARD_NO;BIL_BUSINESS_NO;OPT_DATE;AMT;GATHER_TYPE;CREAT_USER;ACCNT_TYPETABLE.Item=ACCNT_TYPE;GATHER_TYPE;CREAT_USERTABLE.LockColumns=allTABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,right;4,left;5,left;6,leftTABLE.ClickedAction=onTABLEclickedtPanel_12.Type=TPaneltPanel_12.X=5tPanel_12.Y=193tPanel_12.Width=985tPanel_12.Height=231tPanel_12.Border=组|退费tPanel_12.Item=tLabel_2;tLabel_3;tLabel_4;tLabel_5;tLabel_6;tLabel_7;tLabel_8;OLD_EKTFEE;SEQ;BIL_CODE;UN_FEE;SUM_EKTFEE;DESCRIPTION;GATHER_TYPE;CREAT_USER;tLabel_10;RE_EKTFEE;tLabel_11;CARD_TYPE;tLabel_12;RE_LPK;tLabel_13;RE_DJQtPanel_12.AutoWidth=YtPanel_12.TopToolBar=NtPanel_12.TopMenu=NtPanel_12.ToolBar=NtPanel_12.AutoX=YRE_DJQ.Type=TNumberTextFieldRE_DJQ.X=649RE_DJQ.Y=57RE_DJQ.Width=135RE_DJQ.Height=20RE_DJQ.Text=0RE_DJQ.Format=#########0.00RE_DJQ.Enabled=NtLabel_13.Type=TLabeltLabel_13.X=538tLabel_13.Y=60tLabel_13.Width=120tLabel_13.Height=15tLabel_13.Text=代金券可退金额：RE_LPK.Type=TNumberTextFieldRE_LPK.X=375RE_LPK.Y=58RE_LPK.Width=135RE_LPK.Height=20RE_LPK.Text=0RE_LPK.Format=#########0.00RE_LPK.Enabled=NtLabel_12.Type=TLabeltLabel_12.X=264tLabel_12.Y=62tLabel_12.Width=120tLabel_12.Height=15tLabel_12.Text=礼品卡可退金额：CARD_TYPE.Type=卡类型CARD_TYPE.X=115CARD_TYPE.Y=141CARD_TYPE.Width=102CARD_TYPE.Height=21CARD_TYPE.Text=CARD_TYPE.HorizontalAlignment=2CARD_TYPE.PopupMenuHeader=号码,100;名称,150CARD_TYPE.PopupMenuWidth=250CARD_TYPE.PopupMenuHeight=100CARD_TYPE.PopupMenuFilter=ID,1;NAME,1CARD_TYPE.FormatType=comboCARD_TYPE.ShowDownButton=YCARD_TYPE.Tip=卡类型下拉区域CARD_TYPE.ShowColumnList=NAMECARD_TYPE.HisOneNullRow=YtLabel_11.Type=TLabeltLabel_11.X=49tLabel_11.Y=144tLabel_11.Width=59tLabel_11.Height=15tLabel_11.Text=卡类型：RE_EKTFEE.Type=TNumberTextFieldRE_EKTFEE.X=116RE_EKTFEE.Y=60RE_EKTFEE.Width=114RE_EKTFEE.Height=20RE_EKTFEE.Text=0RE_EKTFEE.Format=#########0.00RE_EKTFEE.Enabled=NtLabel_10.Type=TLabeltLabel_10.X=36tLabel_10.Y=62tLabel_10.Width=72tLabel_10.Height=17tLabel_10.Text=可退金额：CREAT_USER.Type=人员下拉列表CREAT_USER.X=53CREAT_USER.Y=199CREAT_USER.Width=81CREAT_USER.Height=23CREAT_USER.Text=TButtonCREAT_USER.showID=YCREAT_USER.showName=YCREAT_USER.showText=NCREAT_USER.showValue=NCREAT_USER.showPy1=YCREAT_USER.showPy2=YCREAT_USER.Editable=YCREAT_USER.Tip=人员CREAT_USER.TableShowList=nameCREAT_USER.ModuleParmString=CREAT_USER.ModuleParmTag=CREAT_USER.Visible=NGATHER_TYPE.Type=支付方式下拉区域(医疗卡)GATHER_TYPE.X=115GATHER_TYPE.Y=100GATHER_TYPE.Width=101GATHER_TYPE.Height=23GATHER_TYPE.Text=GATHER_TYPE.HorizontalAlignment=2GATHER_TYPE.PopupMenuHeader=代码,100;名称,100GATHER_TYPE.PopupMenuWidth=300GATHER_TYPE.PopupMenuHeight=300GATHER_TYPE.PopupMenuFilter=ID,1;NAME,1;PY1,1GATHER_TYPE.FormatType=comboGATHER_TYPE.ShowDownButton=YGATHER_TYPE.Tip=支付方式GATHER_TYPE.ShowColumnList=NAMEGATHER_TYPE.HisOneNullRow=YDESCRIPTION.Type=TTextFieldDESCRIPTION.X=344DESCRIPTION.Y=141DESCRIPTION.Width=170DESCRIPTION.Height=20DESCRIPTION.Text=SUM_EKTFEE.Type=TNumberTextFieldSUM_EKTFEE.X=345SUM_EKTFEE.Y=189SUM_EKTFEE.Width=116SUM_EKTFEE.Height=20SUM_EKTFEE.Text=0.00SUM_EKTFEE.Format=#########0.00SUM_EKTFEE.Enabled=NUN_FEE.Type=TNumberTextFieldUN_FEE.X=114UN_FEE.Y=189UN_FEE.Width=102UN_FEE.Height=20UN_FEE.Text=0.00UN_FEE.Format=#########0.00UN_FEE.Enabled=YUN_FEE.Action=addFeeUN_FEE.Visible=YBIL_CODE.Type=TTextFieldBIL_CODE.X=344BIL_CODE.Y=102BIL_CODE.Width=170BIL_CODE.Height=20BIL_CODE.Text=SEQ.Type=TTextFieldSEQ.X=332SEQ.Y=25SEQ.Width=86SEQ.Height=20SEQ.Text=SEQ.Enabled=NOLD_EKTFEE.Type=TNumberTextFieldOLD_EKTFEE.X=115OLD_EKTFEE.Y=24OLD_EKTFEE.Width=121OLD_EKTFEE.Height=21OLD_EKTFEE.Text=0.00OLD_EKTFEE.Format=#########0.00OLD_EKTFEE.Enabled=NtLabel_8.Type=TLabeltLabel_8.X=264tLabel_8.Y=192tLabel_8.Width=72tLabel_8.Height=15tLabel_8.Text=卡内金额:tLabel_7.Type=TLabeltLabel_7.X=37tLabel_7.Y=192tLabel_7.Width=65tLabel_7.Height=15tLabel_7.Text=退款金额:tLabel_6.Type=TLabeltLabel_6.X=293tLabel_6.Y=144tLabel_6.Width=36tLabel_6.Height=15tLabel_6.Text=备注:tLabel_5.Type=TLabeltLabel_5.X=266tLabel_5.Y=105tLabel_5.Width=69tLabel_5.Height=15tLabel_5.Text=票据号码:tLabel_4.Type=TLabeltLabel_4.X=36tLabel_4.Y=105tLabel_4.Width=72tLabel_4.Height=15tLabel_4.Text=退款方式:tLabel_3.Type=TLabeltLabel_3.X=276tLabel_3.Y=28tLabel_3.Width=53tLabel_3.Height=15tLabel_3.Text=卡序号:tLabel_2.Type=TLabeltLabel_2.X=36tLabel_2.Y=28tLabel_2.Width=76tLabel_2.Height=15tLabel_2.Text=卡内余额:tPanel_11.Type=TPaneltPanel_11.X=5tPanel_11.Y=98tPanel_11.Width=985tPanel_11.Height=94tPanel_11.Border=组|基本信息tPanel_11.Item=tLabel_14;tLabel_28;tLabel_0;tLabel_1;MR_NO;PAT_NAME;SEX_CODE;BIRTH_DATEtPanel_11.AutoWidth=YtPanel_11.AutoX=YBIRTH_DATE.Type=TTextFormatBIRTH_DATE.X=351BIRTH_DATE.Y=56BIRTH_DATE.Width=118BIRTH_DATE.Height=20BIRTH_DATE.Text=BIRTH_DATE.HorizontalAlignment=0BIRTH_DATE.FormatType=dateBIRTH_DATE.Format=yyyy/MM/ddBIRTH_DATE.showDownButton=YSEX_CODE.Type=性别下拉列表SEX_CODE.X=106SEX_CODE.Y=55SEX_CODE.Width=81SEX_CODE.Height=23SEX_CODE.Text=TButtonSEX_CODE.showID=YSEX_CODE.showName=YSEX_CODE.showText=NSEX_CODE.showValue=NSEX_CODE.showPy1=NSEX_CODE.showPy2=NSEX_CODE.Editable=YSEX_CODE.Tip=性别SEX_CODE.TableShowList=nameSEX_CODE.ModuleParmString=GROUP_ID:SYS_SEXSEX_CODE.ModuleParmTag=PAT_NAME.Type=TTextFieldPAT_NAME.X=349PAT_NAME.Y=23PAT_NAME.Width=118PAT_NAME.Height=20PAT_NAME.Text=PAT_NAME.Action=onQueryPatNameMR_NO.Type=TTextFieldMR_NO.X=105MR_NO.Y=21MR_NO.Width=151MR_NO.Height=20MR_NO.Text=MR_NO.Action=onQuerytLabel_1.Type=TLabeltLabel_1.X=274tLabel_1.Y=59tLabel_1.Width=72tLabel_1.Height=15tLabel_1.Text=出生日期:tLabel_0.Type=TLabeltLabel_0.X=60tLabel_0.Y=59tLabel_0.Width=40tLabel_0.Height=15tLabel_0.Text=性别:tLabel_28.Type=TLabeltLabel_28.X=300tLabel_28.Y=25tLabel_28.Width=42tLabel_28.Height=15tLabel_28.Text=姓名:tLabel_14.Type=TLabeltLabel_14.X=45tLabel_14.Y=26tLabel_14.Width=55tLabel_14.Height=15tLabel_14.Text=病案号: