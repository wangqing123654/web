# #  Title:检验条码打印# #  Description:检验条码打印# #  Copyright: Copyright (c) Javahis 2009# #  author JiaoY 2009.05.26#  version 1.0#<Type=TFrame>UI.Title=采样执行UI.MenuConfig=%ROOT%\config\med\MEDApplyExecMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.med.MEDApplyExecControlUI.Item=tPanel_1;TABLEUI.AutoSize=3UI.TopMenu=YUI.TopToolBar=YUI.FocusList=MR_NO;BAR_CODEUI.MoveType=TABLE.Type=TTableTABLE.X=8TABLE.Y=182TABLE.Width=81TABLE.Height=589TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=YTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.AutoSize=3TABLE.Header=选,30,boolean;急,30,boolean;区域,120;医嘱名称,160;科室,100,DEPT_CODE;病区,100,STATION_CODE;姓名,100;条码号,100;报告类别,120,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100;采样人员,70,USER_CODE;采样时间,140TABLE.Item=DEPT_CODE;STATION_CODE;RPTTYPE_CODE;ITEM_CODE;DEV_CODE;CLINICAREA_CODE;CLINICROOM_CODE;USER_CODETABLE.LockColumns=1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19TABLE.ColumnHorizontalAlignmentData=2,Left;3,Left;4,Left;5,Left;6,Left;7,Left;8,Left;9,Left;10,Left;11,Left;12,Left;13,Left;14,Left;15,Left;16,Left;17,Left;18,Left;19,LeftTABLE.RightClickedAction=showPopMenutPanel_1.Type=TPaneltPanel_1.X=1tPanel_1.Y=1tPanel_1.Width=1022tPanel_1.Height=177tPanel_1.Border=组|基本信息tPanel_1.AutoX=YtPanel_1.AutoWidth=YtPanel_1.AutoW=NtPanel_1.AutoY=YtPanel_1.Item=O;E;I;H;tLabel_0;tLabel_1;END_DATE;tLabel_2;tLabel_3;tLabel_4;MR_NO;tLabel_5;IPD_NO;ALL;ONPRINT;YESPRINT;ALLCHECK;PAT_NAME;BED_NO;tLabel_6;tLabel_7;tLabel_8;CLINICROOM_CODEMED;CLINICAREA_CODEMED;tLabel_9;REGION_CODE;START_DATE;tButton_0;tButton_1;tButton_2;USER_ID;STATION_CODEMED;DEPT_CODEMED;tLabel_10;tLabel_11;tLabel_12;tLabel_13;COMPANY_CODE;CONTRACT_CODE;START_SEQ_NO;END_SEQ_NO;tLabel_14;CONFIRM_Y;CONFIRM_N;USER_CODE;tLabel_15;BAR_CODE;tButton_3tButton_3.Type=TButtontButton_3.X=755tButton_3.Y=143tButton_3.Width=89tButton_3.Height=23tButton_3.Text=补充计费tButton_3.Action=onCharge|falseBAR_CODE.Type=TTextFieldBAR_CODE.X=484BAR_CODE.Y=142BAR_CODE.Width=139BAR_CODE.Height=20BAR_CODE.Text=BAR_CODE.Action=onQueryBartLabel_15.Type=TLabeltLabel_15.X=434tLabel_15.Y=145tLabel_15.Width=46tLabel_15.Height=15tLabel_15.Text=条码：tLabel_15.Color=蓝USER_CODE.Type=人员下拉列表USER_CODE.X=884USER_CODE.Y=142USER_CODE.Width=81USER_CODE.Height=23USER_CODE.Text=TButtonUSER_CODE.showID=YUSER_CODE.showName=YUSER_CODE.showText=NUSER_CODE.showValue=NUSER_CODE.showPy1=YUSER_CODE.showPy2=YUSER_CODE.Editable=YUSER_CODE.Tip=人员USER_CODE.TableShowList=nameUSER_CODE.ModuleParmString=USER_CODE.ModuleParmTag=USER_CODE.Visible=NCONFIRM_N.Type=TRadioButtonCONFIRM_N.X=253CONFIRM_N.Y=142CONFIRM_N.Width=69CONFIRM_N.Height=23CONFIRM_N.Text=未执行CONFIRM_N.Group=CONFIRM_TYPECONFIRM_N.Selected=YCONFIRM_N.Action=onConRadioButton|NCONFIRM_N.Color=蓝CONFIRM_Y.Type=TRadioButtonCONFIRM_Y.X=331CONFIRM_Y.Y=142CONFIRM_Y.Width=68CONFIRM_Y.Height=23CONFIRM_Y.Text=已执行CONFIRM_Y.Group=CONFIRM_TYPECONFIRM_Y.Action=onConRadioButton|YCONFIRM_Y.Color=蓝tLabel_14.Type=TLabeltLabel_14.X=178tLabel_14.Y=146tLabel_14.Width=72tLabel_14.Height=15tLabel_14.Text=执行状态：END_SEQ_NO.Type=TTextFieldEND_SEQ_NO.X=925END_SEQ_NO.Y=106END_SEQ_NO.Width=77END_SEQ_NO.Height=22END_SEQ_NO.Text=START_SEQ_NO.Type=TTextFieldSTART_SEQ_NO.X=822START_SEQ_NO.Y=106START_SEQ_NO.Width=77START_SEQ_NO.Height=22START_SEQ_NO.Text=CONTRACT_CODE.Type=TTextFormatCONTRACT_CODE.X=823CONTRACT_CODE.Y=66CONTRACT_CODE.Width=180CONTRACT_CODE.Height=22CONTRACT_CODE.Text=CONTRACT_CODE.FormatType=comboCONTRACT_CODE.showDownButton=YCONTRACT_CODE.PopupMenuHeader=合同代码,100;合同名称,100CONTRACT_CODE.ShowColumnList=NAMECONTRACT_CODE.ValueColumn=IDCONTRACT_CODE.PopupMenuFilter=ID,1;PY1,1CONTRACT_CODE.HisOneNullRow=NCONTRACT_CODE.PopupMenuSQL=CONTRACT_CODE.HorizontalAlignment=2CONTRACT_CODE.Action=onContractChooseCOMPANY_CODE.Type=健康检查团体下拉区域COMPANY_CODE.X=823COMPANY_CODE.Y=27COMPANY_CODE.Width=180COMPANY_CODE.Height=22COMPANY_CODE.Text=COMPANY_CODE.HorizontalAlignment=2COMPANY_CODE.PopupMenuHeader=代码,100;名称,100COMPANY_CODE.PopupMenuWidth=300COMPANY_CODE.PopupMenuHeight=300COMPANY_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1COMPANY_CODE.FormatType=comboCOMPANY_CODE.ShowDownButton=YCOMPANY_CODE.Tip=健康检查团体COMPANY_CODE.ShowColumnList=NAMECOMPANY_CODE.Action=onCompanyChooseCOMPANY_CODE.HisOneNullRow=YtLabel_13.Type=TLabeltLabel_13.X=906tLabel_13.Y=110tLabel_13.Width=19tLabel_13.Height=15tLabel_13.Text=至tLabel_12.Type=TLabeltLabel_12.X=755tLabel_12.Y=110tLabel_12.Width=70tLabel_12.Height=15tLabel_12.Text=员工序号：tLabel_11.Type=TLabeltLabel_11.X=754tLabel_11.Y=70tLabel_11.Width=70tLabel_11.Height=15tLabel_11.Text=合同名称：tLabel_10.Type=TLabeltLabel_10.X=754tLabel_10.Y=31tLabel_10.Width=71tLabel_10.Height=15tLabel_10.Text=团体名称：DEPT_CODEMED.Type=科室DEPT_CODEMED.X=478DEPT_CODEMED.Y=67DEPT_CODEMED.Width=103DEPT_CODEMED.Height=23DEPT_CODEMED.Text=DEPT_CODEMED.HorizontalAlignment=2DEPT_CODEMED.PopupMenuHeader=代码,100;名称,100DEPT_CODEMED.PopupMenuWidth=300DEPT_CODEMED.PopupMenuHeight=300DEPT_CODEMED.PopupMenuFilter=ID,1;NAME,1;PY1,1DEPT_CODEMED.FormatType=comboDEPT_CODEMED.ShowDownButton=YDEPT_CODEMED.Tip=科室DEPT_CODEMED.ShowColumnList=NAMEDEPT_CODEMED.HisOneNullRow=YDEPT_CODEMED.StationCode=<STATION_CODEMED>STATION_CODEMED.Type=用户病区诊区STATION_CODEMED.X=636STATION_CODEMED.Y=66STATION_CODEMED.Width=99STATION_CODEMED.Height=23STATION_CODEMED.Text=STATION_CODEMED.HorizontalAlignment=2STATION_CODEMED.PopupMenuHeader=代码,100;名称,100STATION_CODEMED.PopupMenuWidth=300STATION_CODEMED.PopupMenuHeight=300STATION_CODEMED.PopupMenuFilter=ID,1;NAME,1;PY1,1STATION_CODEMED.FormatType=comboSTATION_CODEMED.ShowDownButton=YSTATION_CODEMED.Tip=用户病区诊区STATION_CODEMED.ShowColumnList=NAMESTATION_CODEMED.HisOneNullRow=YSTATION_CODEMED.Stype=2STATION_CODEMED.UserID=<USER_ID>STATION_CODEMED.Action=DEPT_CODEMED|onQuery;DEPT_CODEMED|setText|USER_ID.Type=人员USER_ID.X=914USER_ID.Y=202USER_ID.Width=81USER_ID.Height=23USER_ID.Text=USER_ID.HorizontalAlignment=2USER_ID.PopupMenuHeader=代码,100;名称,100USER_ID.PopupMenuWidth=300USER_ID.PopupMenuHeight=300USER_ID.PopupMenuFilter=ID,1;NAME,1;PY1,1USER_ID.FormatType=comboUSER_ID.ShowDownButton=YUSER_ID.Tip=人员USER_ID.ShowColumnList=NAMEUSER_ID.HisOneNullRow=YUSER_ID.Action=STATION_CODEMED|onQuery;STATION_CODEMED|setText|tButton_2.Type=TButtontButton_2.X=925tButton_2.Y=141tButton_2.Width=81tButton_2.Height=23tButton_2.Text=重叫tButton_2.Action=onReCalltButton_2.Enabled=NtButton_2.Visible=NtButton_1.Type=TButtontButton_1.X=644tButton_1.Y=143tButton_1.Width=81tButton_1.Height=23tButton_1.Text=下一个tButton_1.Action=onNextCalltButton_1.Enabled=NtButton_1.Visible=NtButton_0.Type=TButtontButton_0.X=672tButton_0.Y=138tButton_0.Width=81tButton_0.Height=23tButton_0.Text=排队tButton_0.Action=onQueueCalltButton_0.Enabled=NtButton_0.Visible=NSTART_DATE.Type=TTextFormatSTART_DATE.X=86START_DATE.Y=106START_DATE.Width=160START_DATE.Height=22START_DATE.Text=TTextFormatSTART_DATE.FormatType=dateSTART_DATE.Format=yyyy/MM/dd HH:mm:ssSTART_DATE.showDownButton=YREGION_CODE.Type=区域下拉列表REGION_CODE.X=86REGION_CODE.Y=27REGION_CODE.Width=134REGION_CODE.Height=23REGION_CODE.Text=TButtonREGION_CODE.showID=YREGION_CODE.showName=YREGION_CODE.showText=NREGION_CODE.showValue=NREGION_CODE.showPy1=YREGION_CODE.showPy2=YREGION_CODE.Editable=YREGION_CODE.Tip=区域REGION_CODE.TableShowList=nameREGION_CODE.ModuleParmString=REGION_CODE.ModuleParmTag=REGION_CODE.Enabled=YtLabel_9.Type=TLabeltLabel_9.X=34tLabel_9.Y=28tLabel_9.Width=60tLabel_9.Height=22tLabel_9.Text=区域：tLabel_9.HorizontalAlignment=0tLabel_9.Color=蓝DEV_CODE.Type=分类规则下拉列表DEV_CODE.X=812DEV_CODE.Y=104DEV_CODE.Width=81DEV_CODE.Height=23DEV_CODE.Text=TButtonDEV_CODE.showID=YDEV_CODE.showName=YDEV_CODE.showText=NDEV_CODE.showValue=NDEV_CODE.showPy1=NDEV_CODE.showPy2=NDEV_CODE.Editable=YDEV_CODE.Tip=分类规则DEV_CODE.TableShowList=nameDEV_CODE.ModuleParmString=DEV_CODE.ModuleParmTag=DEV_CODE.RuleType=DEV_TYPERPTTYPE_CODE.Type=检验检查分类RPTTYPE_CODE.X=644RPTTYPE_CODE.Y=104RPTTYPE_CODE.Width=81RPTTYPE_CODE.Height=23RPTTYPE_CODE.Text=TButtonRPTTYPE_CODE.showID=YRPTTYPE_CODE.showName=YRPTTYPE_CODE.showText=NRPTTYPE_CODE.showValue=NRPTTYPE_CODE.showPy1=NRPTTYPE_CODE.showPy2=NRPTTYPE_CODE.Editable=YRPTTYPE_CODE.Tip=检验检查分类RPTTYPE_CODE.TableShowList=nameRPTTYPE_CODE.CLASSIFY3=RPTTYPE_CODE.CLASSIFY2=YRPTTYPE_CODE.ExpandWidth=30ITEM_CODE.Type=部位代码下拉列表ITEM_CODE.X=727ITEM_CODE.Y=104ITEM_CODE.Width=86ITEM_CODE.Height=23ITEM_CODE.Text=TButtonITEM_CODE.showID=YITEM_CODE.showName=YITEM_CODE.showText=NITEM_CODE.showValue=NITEM_CODE.showPy1=YITEM_CODE.showPy2=YITEM_CODE.Editable=YITEM_CODE.Tip=部位代码下拉列表ITEM_CODE.TableShowList=nameITEM_CODE.ModuleParmString=GROUP_ID:SYS_OPTITEMITEM_CODE.ModuleParmTag=CLINICAREA_CODEMED.Type=诊区CLINICAREA_CODEMED.X=477CLINICAREA_CODEMED.Y=27CLINICAREA_CODEMED.Width=103CLINICAREA_CODEMED.Height=22CLINICAREA_CODEMED.Text=CLINICAREA_CODEMED.HorizontalAlignment=2CLINICAREA_CODEMED.PopupMenuHeader=ID,100;NAME,100CLINICAREA_CODEMED.PopupMenuWidth=300CLINICAREA_CODEMED.PopupMenuHeight=300CLINICAREA_CODEMED.PopupMenuFilter=ID,1;NAME,1;PY1,1CLINICAREA_CODEMED.FormatType=comboCLINICAREA_CODEMED.ShowDownButton=YCLINICAREA_CODEMED.Tip=诊区CLINICAREA_CODEMED.ShowColumnList=NAMECLINICAREA_CODEMED.Action=CLINICROOM_CODEMED|onQuery;CLINICROOM_CODEMED|setTextCLINICROOM_CODEMED.Type=诊间CLINICROOM_CODEMED.X=635CLINICROOM_CODEMED.Y=27CLINICROOM_CODEMED.Width=101CLINICROOM_CODEMED.Height=22CLINICROOM_CODEMED.Text=CLINICROOM_CODEMED.HorizontalAlignment=2CLINICROOM_CODEMED.PopupMenuHeader=ID,100;NAME,100CLINICROOM_CODEMED.PopupMenuWidth=300CLINICROOM_CODEMED.PopupMenuHeight=300CLINICROOM_CODEMED.PopupMenuFilter=ID,1;NAME,1;PY1,1CLINICROOM_CODEMED.FormatType=comboCLINICROOM_CODEMED.ShowDownButton=YCLINICROOM_CODEMED.Tip=诊间CLINICROOM_CODEMED.ShowColumnList=NAMECLINICROOM_CODEMED.ClinicArea=<CLINICAREA_CODEMED>CLINICROOM_CODEMED.ActiveFlg=YCLINICROOM_CODE.Type=诊室下拉列表CLINICROOM_CODE.X=900CLINICROOM_CODE.Y=54CLINICROOM_CODE.Width=86CLINICROOM_CODE.Height=22CLINICROOM_CODE.Text=TButtonCLINICROOM_CODE.showID=YCLINICROOM_CODE.showName=YCLINICROOM_CODE.showText=NCLINICROOM_CODE.showValue=NCLINICROOM_CODE.showPy1=YCLINICROOM_CODE.showPy2=YCLINICROOM_CODE.Editable=YCLINICROOM_CODE.Tip=诊室CLINICROOM_CODE.TableShowList=nameCLINICROOM_CODE.ModuleParmTag=tLabel_8.Type=TLabeltLabel_8.X=592tLabel_8.Y=27tLabel_8.Width=42tLabel_8.Height=22tLabel_8.Text=诊室：tLabel_8.Color=蓝CLINICAREA_CODE.Type=诊区下拉列表CLINICAREA_CODE.X=814CLINICAREA_CODE.Y=54CLINICAREA_CODE.Width=86CLINICAREA_CODE.Height=22CLINICAREA_CODE.Text=TButtonCLINICAREA_CODE.showID=YCLINICAREA_CODE.showName=YCLINICAREA_CODE.showText=NCLINICAREA_CODE.showValue=NCLINICAREA_CODE.showPy1=YCLINICAREA_CODE.showPy2=YCLINICAREA_CODE.Editable=YCLINICAREA_CODE.Tip=诊区CLINICAREA_CODE.TableShowList=nameCLINICAREA_CODE.ModuleParmTag=tLabel_7.Type=TLabeltLabel_7.X=435tLabel_7.Y=27tLabel_7.Width=43tLabel_7.Height=22tLabel_7.Text=诊区：tLabel_7.Color=蓝tLabel_6.Type=TLabeltLabel_6.X=434tLabel_6.Y=105tLabel_6.Width=42tLabel_6.Height=22tLabel_6.Text=床号：BED_NO.Type=TTextFieldBED_NO.X=482BED_NO.Y=106BED_NO.Width=100BED_NO.Height=22BED_NO.Text=PAT_NAME.Type=TTextFieldPAT_NAME.X=191PAT_NAME.Y=67PAT_NAME.Width=77PAT_NAME.Height=22PAT_NAME.Text=PAT_NAME.Enabled=NALLCHECK.Type=TCheckBoxALLCHECK.X=12ALLCHECK.Y=144ALLCHECK.Width=55ALLCHECK.Height=22ALLCHECK.Text=全选ALLCHECK.Action=onSelAllYESPRINT.Type=TRadioButtonYESPRINT.X=577YESPRINT.Y=143YESPRINT.Width=67YESPRINT.Height=22YESPRINT.Text=已打印YESPRINT.Group=YESPRINT.Action=onQueryYESPRINT.Visible=NONPRINT.Type=TRadioButtonONPRINT.X=595ONPRINT.Y=141ONPRINT.Width=70ONPRINT.Height=22ONPRINT.Text=未打印ONPRINT.Group=ONPRINT.Selected=YONPRINT.Action=onQueryONPRINT.Visible=NALL.Type=TRadioButtonALL.X=658ALL.Y=140ALL.Width=54ALL.Height=22ALL.Text=全部ALL.Group=ALL.Selected=NALL.Action=onQueryALL.Visible=NIPD_NO.Type=TTextFieldIPD_NO.X=649IPD_NO.Y=104IPD_NO.Width=106IPD_NO.Height=22IPD_NO.Text=IPD_NO.Action=onQueryIPD_NO.Visible=NtLabel_5.Type=TLabeltLabel_5.X=602tLabel_5.Y=104tLabel_5.Width=50tLabel_5.Height=22tLabel_5.Text=住院号tLabel_5.Visible=NMR_NO.Type=TTextFieldMR_NO.X=86MR_NO.Y=67MR_NO.Width=103MR_NO.Height=22MR_NO.Text=MR_NO.Action=onQuerytLabel_4.Type=TLabeltLabel_4.X=26tLabel_4.Y=66tLabel_4.Width=60tLabel_4.Height=22tLabel_4.Text=病案号：tLabel_4.VerticalAlignment=0tLabel_4.HorizontalAlignment=0tLabel_4.Color=蓝STATION_CODE.Type=病区下拉列表STATION_CODE.X=728STATION_CODE.Y=54STATION_CODE.Width=86STATION_CODE.Height=22STATION_CODE.Text=TButtonSTATION_CODE.showID=YSTATION_CODE.showName=YSTATION_CODE.showText=NSTATION_CODE.showValue=NSTATION_CODE.showPy1=YSTATION_CODE.showPy2=YSTATION_CODE.Editable=YSTATION_CODE.Tip=病区STATION_CODE.TableShowList=nametLabel_3.Type=TLabeltLabel_3.X=592tLabel_3.Y=66tLabel_3.Width=43tLabel_3.Height=22tLabel_3.Text=病区：tLabel_3.Color=蓝DEPT_CODE.Type=科室下拉列表DEPT_CODE.X=644DEPT_CODE.Y=54DEPT_CODE.Width=84DEPT_CODE.Height=22DEPT_CODE.Text=TButtonDEPT_CODE.showID=YDEPT_CODE.showName=YDEPT_CODE.showText=NDEPT_CODE.showValue=NDEPT_CODE.showPy1=NDEPT_CODE.showPy2=NDEPT_CODE.Editable=YDEPT_CODE.Tip=科室DEPT_CODE.TableShowList=nameDEPT_CODE.ExpandWidth=20DEPT_CODE.OpdFitFlg=DEPT_CODE.FinalFlg=YDEPT_CODE.Visible=YtLabel_2.Type=TLabeltLabel_2.X=434tLabel_2.Y=66tLabel_2.Width=42tLabel_2.Height=22tLabel_2.Text=科室：tLabel_2.Color=蓝END_DATE.Type=TTextFormatEND_DATE.X=265END_DATE.Y=106END_DATE.Width=160END_DATE.Height=22END_DATE.Text=TTextFormatEND_DATE.FormatType=dateEND_DATE.Format=yyyy/MM/dd HH:mm:ssEND_DATE.showDownButton=YtLabel_1.Type=TLabeltLabel_1.X=249tLabel_1.Y=106tLabel_1.Width=16tLabel_1.Height=22tLabel_1.Text=～tLabel_0.Type=TLabeltLabel_0.X=15tLabel_0.Y=106tLabel_0.Width=70tLabel_0.Height=22tLabel_0.Text=申请日期：tLabel_0.Color=蓝H.Type=TRadioButtonH.X=367H.Y=27H.Width=54H.Height=22H.Text=健检H.Group=ADM_TYPEH.Action=onSelRadioButton|HH.Color=蓝I.Type=TRadioButtonI.X=383I.Y=64I.Width=57I.Height=22I.Text=住院I.Group=ADM_TYPEI.Action=onSelRadioButton|II.Selected=NI.Visible=NE.Type=TRadioButtonE.X=312E.Y=27E.Width=54E.Height=22E.Text=急诊E.Group=ADM_TYPEE.Action=onSelRadioButton|EE.Color=蓝O.Type=TRadioButtonO.X=256O.Y=27O.Width=57O.Height=22O.Text=门诊O.Group=ADM_TYPEO.Selected=YO.Action=onSelRadioButton|OO.Color=蓝