## TBuilder Config File ## Title:## Company:JavaHis## Author:庞犇 2011.11.07## version 1.0#<Type=TFrame>UI.Title=资格确认书下载/开立UI.MenuConfig=%ROOT%\config\ins\INSADMConfirmMenu.xUI.Width=1033UI.Height=650UI.toolbar=YUI.controlclassname=com.javahis.ui.ins.INSADMConfirmControlUI.item=tPanel_0;tTabbedPane_1UI.layout=nullUI.LoadFlg=NUI.TopMenu=YUI.TopToolBar=YUI.FocusList=tTabbedPane_1.Type=TTabbedPanetTabbedPane_1.X=16tTabbedPane_1.Y=68tTabbedPane_1.Width=81tTabbedPane_1.Height=577tTabbedPane_1.AutoX=YtTabbedPane_1.AutoWidth=YtTabbedPane_1.AutoHeight=YtTabbedPane_1.Item=tPanel_9;tPanel_12;tPanel_13tTabbedPane_1.Name=tPanel_13.Type=TPaneltPanel_13.X=110tPanel_13.Y=11tPanel_13.Width=81tPanel_13.Height=81tPanel_13.Name=住院信息tPanel_13.Item=tLabel_29;tLabel_43;tLabel_44;tLabel_46;tLabel_47;tLabel_48;tLabel_49;tLabel_50;tLabel_51;tLabel_52;tLabel_53;tLabel_54;tLabel_55;tLabel_56;tLabel_57;tLabel_58;tLabel_59;tLabel_60;tLabel_61;tLabel_62;OVERINP_FLG;OWN_RATE;DECREASE_RATE;REALOWN_RATE;INSOWN_RATE;BED_NO;TRANHOSP_RESTANDARD_AMT;START_STANDARD_AMT;ADM_PRJ;REGION_CODE2;STATION_DESC;DEPT_CODE;DEPT_DESC;RESTART_STANDARD_AMT;INSCASE_NO;ADM_CATEGORY;SPEDRS_CODESPEDRS_CODE.Type=医保门特类别下拉区域SPEDRS_CODE.X=561SPEDRS_CODE.Y=69SPEDRS_CODE.Width=107SPEDRS_CODE.Height=23SPEDRS_CODE.Text=SPEDRS_CODE.HorizontalAlignment=2SPEDRS_CODE.PopupMenuHeader=代码,100;名称,100SPEDRS_CODE.PopupMenuWidth=300SPEDRS_CODE.PopupMenuHeight=300SPEDRS_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1SPEDRS_CODE.FormatType=comboSPEDRS_CODE.ShowDownButton=YSPEDRS_CODE.Tip=医保门特类别SPEDRS_CODE.ShowColumnList=NAMESPEDRS_CODE.HisOneNullRow=YADM_CATEGORY.Type=医保就医类别下拉区域ADM_CATEGORY.X=357ADM_CATEGORY.Y=69ADM_CATEGORY.Width=119ADM_CATEGORY.Height=23ADM_CATEGORY.Text=ADM_CATEGORY.HorizontalAlignment=2ADM_CATEGORY.PopupMenuHeader=代码,100;名称,100ADM_CATEGORY.PopupMenuWidth=300ADM_CATEGORY.PopupMenuHeight=300ADM_CATEGORY.PopupMenuFilter=ID,1;NAME,1;PY1,1ADM_CATEGORY.FormatType=comboADM_CATEGORY.ShowDownButton=YADM_CATEGORY.Tip=医保就医类别ADM_CATEGORY.ShowColumnList=NAMEADM_CATEGORY.HisOneNullRow=YINSCASE_NO.Type=TTextFieldINSCASE_NO.X=130INSCASE_NO.Y=222INSCASE_NO.Width=128INSCASE_NO.Height=20INSCASE_NO.Text=RESTART_STANDARD_AMT.Type=TNumberTextFieldRESTART_STANDARD_AMT.X=359RESTART_STANDARD_AMT.Y=119RESTART_STANDARD_AMT.Width=89RESTART_STANDARD_AMT.Height=20RESTART_STANDARD_AMT.Text=0RESTART_STANDARD_AMT.Format=#########0.00DEPT_DESC.Type=TTextFieldDEPT_DESC.X=363DEPT_DESC.Y=267DEPT_DESC.Width=181DEPT_DESC.Height=20DEPT_DESC.Text=DEPT_CODE.Type=TTextFieldDEPT_CODE.X=131DEPT_CODE.Y=268DEPT_CODE.Width=128DEPT_CODE.Height=20DEPT_CODE.Text=STATION_DESC.Type=TTextFieldSTATION_DESC.X=363STATION_DESC.Y=220STATION_DESC.Width=140STATION_DESC.Height=20STATION_DESC.Text=REGION_CODE2.Type=区域下拉列表REGION_CODE2.X=131REGION_CODE2.Y=24REGION_CODE2.Width=131REGION_CODE2.Height=23REGION_CODE2.Text=TButtonREGION_CODE2.showID=YREGION_CODE2.showName=YREGION_CODE2.showText=NREGION_CODE2.showValue=NREGION_CODE2.showPy1=YREGION_CODE2.showPy2=YREGION_CODE2.Editable=YREGION_CODE2.Tip=区域REGION_CODE2.TableShowList=nameREGION_CODE2.ModuleParmString=REGION_CODE2.ModuleParmTag=ADM_PRJ.Type=TComboBoxADM_PRJ.X=131ADM_PRJ.Y=70ADM_PRJ.Width=132ADM_PRJ.Height=23ADM_PRJ.Text=TButtonADM_PRJ.showID=YADM_PRJ.Editable=YADM_PRJ.StringData=[[id,text],[,],[1,门诊],[2,住院],[3,转诊转院]]START_STANDARD_AMT.Type=TNumberTextFieldSTART_STANDARD_AMT.X=131START_STANDARD_AMT.Y=120START_STANDARD_AMT.Width=98START_STANDARD_AMT.Height=20START_STANDARD_AMT.Text=0START_STANDARD_AMT.Format=#########0.00TRANHOSP_RESTANDARD_AMT.Type=TNumberTextFieldTRANHOSP_RESTANDARD_AMT.X=653TRANHOSP_RESTANDARD_AMT.Y=120TRANHOSP_RESTANDARD_AMT.Width=92TRANHOSP_RESTANDARD_AMT.Height=20TRANHOSP_RESTANDARD_AMT.Text=0TRANHOSP_RESTANDARD_AMT.Format=#########0.00BED_NO.Type=TTextFieldBED_NO.X=581BED_NO.Y=221BED_NO.Width=112BED_NO.Height=20BED_NO.Text=INSOWN_RATE.Type=TNumberTextFieldINSOWN_RATE.X=846INSOWN_RATE.Y=172INSOWN_RATE.Width=92INSOWN_RATE.Height=20INSOWN_RATE.Text=0INSOWN_RATE.Format=#########0.00REALOWN_RATE.Type=TNumberTextFieldREALOWN_RATE.X=580REALOWN_RATE.Y=172REALOWN_RATE.Width=88REALOWN_RATE.Height=20REALOWN_RATE.Text=0REALOWN_RATE.Format=#########0.00DECREASE_RATE.Type=TNumberTextFieldDECREASE_RATE.X=360DECREASE_RATE.Y=172DECREASE_RATE.Width=88DECREASE_RATE.Height=20DECREASE_RATE.Text=0DECREASE_RATE.Format=#########0.00OWN_RATE.Type=TNumberTextFieldOWN_RATE.X=131OWN_RATE.Y=172OWN_RATE.Width=97OWN_RATE.Height=20OWN_RATE.Text=0OWN_RATE.Format=#########0.00OVERINP_FLG.Type=TCheckBoxOVERINP_FLG.X=296OVERINP_FLG.Y=24OVERINP_FLG.Width=101OVERINP_FLG.Height=23OVERINP_FLG.Text=跨年度住院tLabel_62.Type=TLabeltLabel_62.X=267tLabel_62.Y=271tLabel_62.Width=94tLabel_62.Height=15tLabel_62.Text=住院科别名称:tLabel_61.Type=TLabeltLabel_61.X=31tLabel_61.Y=271tLabel_61.Width=94tLabel_61.Height=15tLabel_61.Text=住院科别代码:tLabel_60.Type=TLabeltLabel_60.X=469tLabel_60.Y=123tLabel_60.Width=178tLabel_60.Height=15tLabel_60.Text=转出医院实收起付标准金额:tLabel_59.Type=TLabeltLabel_59.X=523tLabel_59.Y=224tLabel_59.Width=54tLabel_59.Height=15tLabel_59.Text=病床号:tLabel_58.Type=TLabeltLabel_58.X=290tLabel_58.Y=224tLabel_58.Width=65tLabel_58.Height=15tLabel_58.Text=住院病区:tLabel_57.Type=TLabeltLabel_57.X=42tLabel_57.Y=224tLabel_57.Width=78tLabel_57.Height=15tLabel_57.Text=医保住院号:tLabel_56.Type=TLabeltLabel_56.X=948tLabel_56.Y=175tLabel_56.Width=8tLabel_56.Height=15tLabel_56.Text=%tLabel_55.Type=TLabeltLabel_55.X=678tLabel_55.Y=175tLabel_55.Width=7tLabel_55.Height=15tLabel_55.Text=%tLabel_54.Type=TLabeltLabel_54.X=455tLabel_54.Y=175tLabel_54.Width=12tLabel_54.Height=15tLabel_54.Text=%tLabel_53.Type=TLabeltLabel_53.X=235tLabel_53.Y=175tLabel_53.Width=13tLabel_53.Height=15tLabel_53.Text=%tLabel_52.Type=TLabeltLabel_52.X=719tLabel_52.Y=175tLabel_52.Width=126tLabel_52.Height=15tLabel_52.Text=医疗救助自负比例:tLabel_51.Type=TLabeltLabel_51.X=57tLabel_51.Y=125tLabel_51.Width=63tLabel_51.Height=15tLabel_51.Text=起付标准:tLabel_50.Type=TLabeltLabel_50.X=491tLabel_50.Y=74tLabel_50.Width=72tLabel_50.Height=15tLabel_50.Text=门特类别:tLabel_49.Type=TLabeltLabel_49.X=55tLabel_49.Y=175tLabel_49.Width=65tLabel_49.Height=15tLabel_49.Text=自负比例:tLabel_48.Type=TLabeltLabel_48.X=289tLabel_48.Y=175tLabel_48.Width=64tLabel_48.Height=15tLabel_48.Text=减负比例:tLabel_47.Type=TLabeltLabel_47.X=482tLabel_47.Y=175tLabel_47.Width=99tLabel_47.Height=15tLabel_47.Text=实际自负比例:tLabel_46.Type=TLabeltLabel_46.X=233tLabel_46.Y=123tLabel_46.Width=124tLabel_46.Height=15tLabel_46.Text=本次实际起付标准:tLabel_44.Type=TLabeltLabel_44.X=289tLabel_44.Y=74tLabel_44.Width=72tLabel_44.Height=15tLabel_44.Text=就医类别:tLabel_43.Type=TLabeltLabel_43.X=55tLabel_43.Y=28tLabel_43.Width=65tLabel_43.Height=15tLabel_43.Text=医院编码:tLabel_29.Type=TLabeltLabel_29.X=56tLabel_29.Y=74tLabel_29.Width=64tLabel_29.Height=15tLabel_29.Text=就医专案:tPanel_12.Type=TPaneltPanel_12.X=35tPanel_12.Y=8tPanel_12.Width=81tPanel_12.Height=81tPanel_12.Name=资格确认书tPanel_12.Item=tLabel_28;tLabel_2;tLabel_5;tLabel_6;tPanel_1;tPanel_2;CONFIRM_NO;UNIT_DESC;INS_UNIT;UNIT_CODEUNIT_CODE.Type=TTextFieldUNIT_CODE.X=99UNIT_CODE.Y=16UNIT_CODE.Width=122UNIT_CODE.Height=20UNIT_CODE.Text=INS_UNIT.Type=TComboBoxINS_UNIT.X=349INS_UNIT.Y=14INS_UNIT.Width=192INS_UNIT.Height=23INS_UNIT.Text=TButtonINS_UNIT.showID=YINS_UNIT.Editable=YINS_UNIT.Visible=YUNIT_DESC.Type=TTextFieldUNIT_DESC.X=99UNIT_DESC.Y=50UNIT_DESC.Width=618UNIT_DESC.Height=20UNIT_DESC.Text=CONFIRM_NO.Type=TTextFieldCONFIRM_NO.X=688CONFIRM_NO.Y=15CONFIRM_NO.Width=121CONFIRM_NO.Height=20CONFIRM_NO.Text=tPanel_2.Type=TPaneltPanel_2.X=11tPanel_2.Y=357tPanel_2.Width=1002tPanel_2.Height=186tPanel_2.Border=组|年内医疗费用详细情况tPanel_2.AutoWidth=YtPanel_2.AutoHeight=YtPanel_2.Item=tLabel_37;tLabel_38;tLabel_39;tLabel_40;tLabel_41;tLabel_42;ADDPAY_AMT;ADDINS_AMT;ADDNUM_AMT;INSBASE_LIMIT_BALANCE;INS_LIMIT_BALANCE;INSOCC_CODEINSOCC_CODE.Type=医保大额救助单位下拉区域INSOCC_CODE.X=773INSOCC_CODE.Y=22INSOCC_CODE.Width=208INSOCC_CODE.Height=23INSOCC_CODE.Text=INSOCC_CODE.HorizontalAlignment=2INSOCC_CODE.PopupMenuHeader=代码,100;名称,100INSOCC_CODE.PopupMenuWidth=300INSOCC_CODE.PopupMenuHeight=300INSOCC_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1INSOCC_CODE.FormatType=comboINSOCC_CODE.ShowDownButton=YINSOCC_CODE.Tip=医保大额救助单位INSOCC_CODE.ShowColumnList=NAMEINSOCC_CODE.HisOneNullRow=YINS_LIMIT_BALANCE.Type=TNumberTextFieldINS_LIMIT_BALANCE.X=520INS_LIMIT_BALANCE.Y=53INS_LIMIT_BALANCE.Width=109INS_LIMIT_BALANCE.Height=20INS_LIMIT_BALANCE.Text=0INS_LIMIT_BALANCE.Format=#########0.00INSBASE_LIMIT_BALANCE.Type=TNumberTextFieldINSBASE_LIMIT_BALANCE.X=519INSBASE_LIMIT_BALANCE.Y=25INSBASE_LIMIT_BALANCE.Width=110INSBASE_LIMIT_BALANCE.Height=20INSBASE_LIMIT_BALANCE.Text=0INSBASE_LIMIT_BALANCE.Format=#########0.00ADDNUM_AMT.Type=TNumberTextFieldADDNUM_AMT.X=200ADDNUM_AMT.Y=80ADDNUM_AMT.Width=106ADDNUM_AMT.Height=20ADDNUM_AMT.Text=0ADDNUM_AMT.Format=#########0.00ADDINS_AMT.Type=TNumberTextFieldADDINS_AMT.X=200ADDINS_AMT.Y=54ADDINS_AMT.Width=106ADDINS_AMT.Height=20ADDINS_AMT.Text=0ADDINS_AMT.Format=#########0.00ADDPAY_AMT.Type=TNumberTextFieldADDPAY_AMT.X=199ADDPAY_AMT.Y=24ADDPAY_AMT.Width=107ADDPAY_AMT.Height=20ADDPAY_AMT.Text=0ADDPAY_AMT.Format=#########0.00tLabel_42.Type=TLabeltLabel_42.X=647tLabel_42.Y=28tLabel_42.Width=122tLabel_42.Height=15tLabel_42.Text=大额医疗救助单位:tLabel_41.Type=TLabeltLabel_41.X=324tLabel_41.Y=58tLabel_41.Width=194tLabel_41.Height=15tLabel_41.Text=医疗救助最高支付限额剩余额:tLabel_40.Type=TLabeltLabel_40.X=324tLabel_40.Y=28tLabel_40.Width=192tLabel_40.Height=15tLabel_40.Text=统筹基本最高支付限额剩余额:tLabel_39.Type=TLabeltLabel_39.X=59tLabel_39.Y=83tLabel_39.Width=138tLabel_39.Height=15tLabel_39.Text=已结算累计审批金额:tLabel_38.Type=TLabeltLabel_38.X=31tLabel_38.Y=56tLabel_38.Width=163tLabel_38.Height=15tLabel_38.Text=未结算医疗机构申报金额:tLabel_37.Type=TLabeltLabel_37.X=32tLabel_37.Y=28tLabel_37.Width=165tLabel_37.Height=15tLabel_37.Text=未结算医疗机构发生金额:tPanel_1.Type=TPaneltPanel_1.X=14tPanel_1.Y=77tPanel_1.Width=999tPanel_1.Height=281tPanel_1.Border=组tPanel_1.Item=tLabel_13;tLabel_15;tLabel_16;tLabel_17;tLabel_18;tLabel_19;tLabel_20;tLabel_21;tLabel_22;tLabel_23;tLabel_24;tLabel_25;tLabel_26;tLabel_27;tLabel_30;tLabel_31;tLabel_32;tLabel_33;tLabel_34;tLabel_35;tLabel_36;CANCEL_FLG;EMG_FLG;INS_FLG;IDNO;PAT_NAME;IN_START_DATE;INP_TIME;TRAN_NUM;INLIMIT_DATE;HOMEDIAG_DESC;HOMEBED_TIME;HOMEBED_DAYS;SEX_CODE;USER_ID;CTZ1_CODE;TRAN_CLASS;HOSP_CLASS_CODE;REGION_CODE;DIAG_DESC;HOMEBED_TYPE;TRANHOSP_DAYS;TRANHOSP_DESC;PAT_AGE;INSBRANCH_CODEtPanel_1.AutoWidth=YINSBRANCH_CODE.Type=医保分中心下拉区域INSBRANCH_CODE.X=716INSBRANCH_CODE.Y=246INSBRANCH_CODE.Width=193INSBRANCH_CODE.Height=23INSBRANCH_CODE.Text=INSBRANCH_CODE.HorizontalAlignment=2INSBRANCH_CODE.PopupMenuHeader=代码,100;名称,100INSBRANCH_CODE.PopupMenuWidth=300INSBRANCH_CODE.PopupMenuHeight=300INSBRANCH_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1INSBRANCH_CODE.FormatType=comboINSBRANCH_CODE.ShowDownButton=YINSBRANCH_CODE.Tip=医保分中心INSBRANCH_CODE.ShowColumnList=NAMEINSBRANCH_CODE.HisOneNullRow=YPAT_AGE.Type=TNumberTextFieldPAT_AGE.X=590PAT_AGE.Y=10PAT_AGE.Width=114PAT_AGE.Height=20PAT_AGE.Text=0PAT_AGE.Format=#########0TRANHOSP_DESC.Type=TTextFieldTRANHOSP_DESC.X=132TRANHOSP_DESC.Y=149TRANHOSP_DESC.Width=208TRANHOSP_DESC.Height=20TRANHOSP_DESC.Text=TRANHOSP_DAYS.Type=TNumberTextFieldTRANHOSP_DAYS.X=132TRANHOSP_DAYS.Y=183TRANHOSP_DAYS.Width=96TRANHOSP_DAYS.Height=20TRANHOSP_DAYS.Text=0TRANHOSP_DAYS.Format=#########0HOMEBED_TYPE.Type=TTextFieldHOMEBED_TYPE.X=752HOMEBED_TYPE.Y=183HOMEBED_TYPE.Width=117HOMEBED_TYPE.Height=20HOMEBED_TYPE.Text=DIAG_DESC.Type=TTextFieldDIAG_DESC.X=132DIAG_DESC.Y=79DIAG_DESC.Width=337DIAG_DESC.Height=20DIAG_DESC.Text=REGION_CODE.Type=区域下拉列表REGION_CODE.X=132REGION_CODE.Y=43REGION_CODE.Width=154REGION_CODE.Height=23REGION_CODE.Text=TButtonREGION_CODE.showID=YREGION_CODE.showName=YREGION_CODE.showText=NREGION_CODE.showValue=NREGION_CODE.showPy1=NREGION_CODE.showPy2=NREGION_CODE.Editable=YREGION_CODE.Tip=区域REGION_CODE.TableShowList=nameREGION_CODE.ModuleParmString=REGION_CODE.ModuleParmTag=HOSP_CLASS_CODE.Type=医院等级下拉列表HOSP_CLASS_CODE.X=381HOSP_CLASS_CODE.Y=43HOSP_CLASS_CODE.Width=104HOSP_CLASS_CODE.Height=23HOSP_CLASS_CODE.Text=TButtonHOSP_CLASS_CODE.showID=YHOSP_CLASS_CODE.showName=YHOSP_CLASS_CODE.showText=NHOSP_CLASS_CODE.showValue=NHOSP_CLASS_CODE.showPy1=NHOSP_CLASS_CODE.showPy2=NHOSP_CLASS_CODE.Editable=YHOSP_CLASS_CODE.Tip=医院等级HOSP_CLASS_CODE.TableShowList=nameHOSP_CLASS_CODE.ModuleParmString=GROUP_ID:SYS_HOSPITAL_CLASSHOSP_CLASS_CODE.ModuleParmTag=TRAN_CLASS.Type=医院等级下拉列表TRAN_CLASS.X=486TRAN_CLASS.Y=147TRAN_CLASS.Width=126TRAN_CLASS.Height=23TRAN_CLASS.Text=TButtonTRAN_CLASS.showID=YTRAN_CLASS.showName=YTRAN_CLASS.showText=NTRAN_CLASS.showValue=NTRAN_CLASS.showPy1=NTRAN_CLASS.showPy2=NTRAN_CLASS.Editable=YTRAN_CLASS.Tip=医院等级TRAN_CLASS.TableShowList=nameTRAN_CLASS.ModuleParmString=GROUP_ID:SYS_HOSPITAL_CLASSTRAN_CLASS.ModuleParmTag=CTZ1_CODE.Type=身份折扣下拉列表CTZ1_CODE.X=590CTZ1_CODE.Y=43CTZ1_CODE.Width=114CTZ1_CODE.Height=23CTZ1_CODE.Text=TButtonCTZ1_CODE.showID=YCTZ1_CODE.showName=YCTZ1_CODE.showText=NCTZ1_CODE.showValue=NCTZ1_CODE.showPy1=NCTZ1_CODE.showPy2=NCTZ1_CODE.Editable=YCTZ1_CODE.Tip=身份CTZ1_CODE.TableShowList=nameUSER_ID.Type=人员USER_ID.X=753USER_ID.Y=114USER_ID.Width=81USER_ID.Height=23USER_ID.Text=USER_ID.HorizontalAlignment=2USER_ID.PopupMenuHeader=代码,100;名称,100USER_ID.PopupMenuWidth=300USER_ID.PopupMenuHeight=300USER_ID.PopupMenuFilter=ID,1;NAME,1;PY1,1USER_ID.FormatType=comboUSER_ID.ShowDownButton=YUSER_ID.Tip=人员USER_ID.ShowColumnList=NAMESEX_CODE.Type=性别下拉列表SEX_CODE.X=754SEX_CODE.Y=9SEX_CODE.Width=81SEX_CODE.Height=23SEX_CODE.Text=TButtonSEX_CODE.showID=YSEX_CODE.showName=YSEX_CODE.showText=NSEX_CODE.showValue=NSEX_CODE.showPy1=NSEX_CODE.showPy2=NSEX_CODE.Editable=YSEX_CODE.Tip=性别SEX_CODE.TableShowList=nameSEX_CODE.ModuleParmString=GROUP_ID:SYS_SEXSEX_CODE.ModuleParmTag=SEX_CODE.ExpandWidth=50HOMEBED_DAYS.Type=TNumberTextFieldHOMEBED_DAYS.X=388HOMEBED_DAYS.Y=247HOMEBED_DAYS.Width=94HOMEBED_DAYS.Height=21HOMEBED_DAYS.Text=0HOMEBED_DAYS.Format=#########0HOMEBED_TIME.Type=TNumberTextFieldHOMEBED_TIME.X=132HOMEBED_TIME.Y=247HOMEBED_TIME.Width=95HOMEBED_TIME.Height=20HOMEBED_TIME.Text=0HOMEBED_TIME.Format=#########0HOMEDIAG_DESC.Type=TTextFieldHOMEDIAG_DESC.X=132HOMEDIAG_DESC.Y=214HOMEDIAG_DESC.Width=672HOMEDIAG_DESC.Height=20HOMEDIAG_DESC.Text=INLIMIT_DATE.Type=TTextFormatINLIMIT_DATE.X=486INLIMIT_DATE.Y=183INLIMIT_DATE.Width=127INLIMIT_DATE.Height=20INLIMIT_DATE.Text=TTextFormatINLIMIT_DATE.FormatType=dateINLIMIT_DATE.Format=yyyy/MM/ddINLIMIT_DATE.showDownButton=YTRAN_NUM.Type=TTextFieldTRAN_NUM.X=753TRAN_NUM.Y=149TRAN_NUM.Width=149TRAN_NUM.Height=20TRAN_NUM.Text=INP_TIME.Type=TNumberTextFieldINP_TIME.X=377INP_TIME.Y=115INP_TIME.Width=77INP_TIME.Height=20INP_TIME.Text=0INP_TIME.Format=#########0IN_START_DATE.Type=TTextFormatIN_START_DATE.X=132IN_START_DATE.Y=115IN_START_DATE.Width=140IN_START_DATE.Height=20IN_START_DATE.Text=TTextFormatIN_START_DATE.showDownButton=YIN_START_DATE.FormatType=dateIN_START_DATE.Format=yyyy/MM/ddPAT_NAME.Type=TTextFieldPAT_NAME.X=381PAT_NAME.Y=10PAT_NAME.Width=104PAT_NAME.Height=20PAT_NAME.Text=IDNO.Type=TTextFieldIDNO.X=132IDNO.Y=10IDNO.Width=154IDNO.Height=20IDNO.Text=INS_FLG.Type=TCheckBoxINS_FLG.X=542INS_FLG.Y=114INS_FLG.Width=106INS_FLG.Height=23INS_FLG.Text=享医疗救助EMG_FLG.Type=TCheckBoxEMG_FLG.X=481EMG_FLG.Y=114EMG_FLG.Width=55EMG_FLG.Height=23EMG_FLG.Text=急诊CANCEL_FLG.Type=TCheckBoxCANCEL_FLG.X=517CANCEL_FLG.Y=78CANCEL_FLG.Width=81CANCEL_FLG.Height=23CANCEL_FLG.Text=取消注记tLabel_36.Type=TLabeltLabel_36.X=562tLabel_36.Y=250tLabel_36.Width=153tLabel_36.Height=15tLabel_36.Text=开具资格确认书分中心:tLabel_35.Type=TLabeltLabel_35.X=283tLabel_35.Y=250tLabel_35.Width=100tLabel_35.Height=15tLabel_35.Text=家床累计天数:tLabel_34.Type=TLabeltLabel_34.X=38tLabel_34.Y=250tLabel_34.Width=79tLabel_34.Height=15tLabel_34.Text=年家床次数:tLabel_33.Type=TLabeltLabel_33.X=52tLabel_33.Y=217tLabel_33.Width=65tLabel_33.Height=15tLabel_33.Text=家床病种:tLabel_32.Type=TLabeltLabel_32.X=654tLabel_32.Y=186tLabel_32.Width=92tLabel_32.Height=15tLabel_32.Text=家床病种类别:tLabel_31.Type=TLabeltLabel_31.X=25tLabel_31.Y=186tLabel_31.Width=92tLabel_31.Height=15tLabel_31.Text=转院住院天数:tLabel_30.Type=TLabeltLabel_30.X=641tLabel_30.Y=152tLabel_30.Width=108tLabel_30.Height=15tLabel_30.Text=转诊转院审批号:tLabel_27.Type=TLabeltLabel_27.X=380tLabel_27.Y=152tLabel_27.Width=94tLabel_27.Height=15tLabel_27.Text=转出医院等级:tLabel_26.Type=TLabeltLabel_26.X=24tLabel_26.Y=152tLabel_26.Width=93tLabel_26.Height=15tLabel_26.Text=转出医院名称:tLabel_25.Type=TLabeltLabel_25.X=51tLabel_25.Y=82tLabel_25.Width=66tLabel_25.Height=15tLabel_25.Text=入院诊断:tLabel_24.Type=TLabeltLabel_24.X=295tLabel_24.Y=118tLabel_24.Width=80tLabel_24.Height=15tLabel_24.Text=年住院次数:tLabel_23.Type=TLabeltLabel_23.X=550tLabel_23.Y=47tLabel_23.Width=40tLabel_23.Height=15tLabel_23.Text=身份:tLabel_22.Type=TLabeltLabel_22.X=683tLabel_22.Y=118tLabel_22.Width=67tLabel_22.Height=15tLabel_22.Text=操作人员:tLabel_21.Type=TLabeltLabel_21.X=712tLabel_21.Y=13tLabel_21.Width=42tLabel_21.Height=15tLabel_21.Text=性别:tLabel_20.Type=TLabeltLabel_20.X=351tLabel_20.Y=186tLabel_20.Width=120tLabel_20.Height=15tLabel_20.Text=本次住院有效日期:tLabel_19.Type=TLabeltLabel_19.X=550tLabel_19.Y=12tLabel_19.Width=37tLabel_19.Height=17tLabel_19.Text=年龄:tLabel_18.Type=TLabeltLabel_18.X=22tLabel_18.Y=118tLabel_18.Width=95tLabel_18.Height=15tLabel_18.Text=住院开始时间:tLabel_17.Type=TLabeltLabel_17.X=340tLabel_17.Y=47tLabel_17.Width=38tLabel_17.Height=15tLabel_17.Text=等级:tLabel_16.Type=TLabeltLabel_16.X=22tLabel_16.Y=47tLabel_16.Width=95tLabel_16.Height=15tLabel_16.Text=就诊医院名称:tLabel_15.Type=TLabeltLabel_15.X=339tLabel_15.Y=13tLabel_15.Width=39tLabel_15.Height=15tLabel_15.Text=姓名:tLabel_13.Type=TLabeltLabel_13.X=64tLabel_13.Y=13tLabel_13.Width=53tLabel_13.Height=15tLabel_13.Text=身份证:tLabel_6.Type=TLabeltLabel_6.X=566tLabel_6.Y=18tLabel_6.Width=109tLabel_6.Height=15tLabel_6.Text=资格确认书编码:tLabel_5.Type=TLabeltLabel_5.X=21tLabel_5.Y=53tLabel_5.Width=72tLabel_5.Height=15tLabel_5.Text=单位名称:tLabel_2.Type=TLabeltLabel_2.X=22tLabel_2.Y=18tLabel_2.Width=66tLabel_2.Height=15tLabel_2.Text=单位代码:tLabel_28.Type=TLabeltLabel_28.X=249tLabel_28.Y=18tLabel_28.Width=100tLabel_28.Height=15tLabel_28.Text=所属医保机构:tPanel_9.Type=TPaneltPanel_9.X=141tPanel_9.Y=150tPanel_9.Width=81tPanel_9.Height=81tPanel_9.Name=下载和开立tPanel_9.Item=tLabel_14;tPanel_10;tLabel_9;tLabel_45;tLabel_69;tLabel_71;tLabel_73;tLabel_75;tLabel_76;tLabel_78;tLabel_79;tLabel_80;tLabel_81;tLabel_82;tLabel_83;tLabel_84;tLabel_85;tLabel_90;OVERINP_FLG1;EMG_FLG1;CONFIRM_NO1;RESV_NO;MR_NO;IDNO1;PAT_NAME1;APP_DATE;IN_DATE;PERSONAL_NO;DEPT_CODE1;TRAN_NUM1;DIAG_DESC1;REGION_CODE1;ADM_PRJ1;tPanel_3;tPanel_5;tLabel_67;SPE_DISEASE;ADM_CATEGORY1;SPEDRS_CODE1;INSBRANCH_CODE1;INSOCC_CODE1INSOCC_CODE1.Type=医保大额救助单位下拉区域INSOCC_CODE1.X=514INSOCC_CODE1.Y=207INSOCC_CODE1.Width=323INSOCC_CODE1.Height=23INSOCC_CODE1.Text=INSOCC_CODE1.HorizontalAlignment=2INSOCC_CODE1.PopupMenuHeader=代码,100;名称,100INSOCC_CODE1.PopupMenuWidth=300INSOCC_CODE1.PopupMenuHeight=300INSOCC_CODE1.PopupMenuFilter=ID,1;NAME,1;PY1,1INSOCC_CODE1.FormatType=comboINSOCC_CODE1.ShowDownButton=YINSOCC_CODE1.Tip=医保大额救助单位INSOCC_CODE1.ShowColumnList=NAMEINSOCC_CODE1.HisOneNullRow=YINSBRANCH_CODE1.Type=医保分中心下拉区域INSBRANCH_CODE1.X=185INSBRANCH_CODE1.Y=207INSBRANCH_CODE1.Width=194INSBRANCH_CODE1.Height=23INSBRANCH_CODE1.Text=INSBRANCH_CODE1.HorizontalAlignment=2INSBRANCH_CODE1.PopupMenuHeader=代码,100;名称,100INSBRANCH_CODE1.PopupMenuWidth=300INSBRANCH_CODE1.PopupMenuHeight=300INSBRANCH_CODE1.PopupMenuFilter=ID,1;NAME,1;PY1,1INSBRANCH_CODE1.FormatType=comboINSBRANCH_CODE1.ShowDownButton=YINSBRANCH_CODE1.Tip=医保分中心INSBRANCH_CODE1.ShowColumnList=NAMEINSBRANCH_CODE1.HisOneNullRow=YSPEDRS_CODE1.Type=医保门特类别下拉区域SPEDRS_CODE1.X=701SPEDRS_CODE1.Y=133SPEDRS_CODE1.Width=106SPEDRS_CODE1.Height=23SPEDRS_CODE1.Text=SPEDRS_CODE1.HorizontalAlignment=2SPEDRS_CODE1.PopupMenuHeader=代码,100;名称,100SPEDRS_CODE1.PopupMenuWidth=300SPEDRS_CODE1.PopupMenuHeight=300SPEDRS_CODE1.PopupMenuFilter=ID,1;NAME,1;PY1,1SPEDRS_CODE1.FormatType=comboSPEDRS_CODE1.ShowDownButton=YSPEDRS_CODE1.Tip=医保门特类别SPEDRS_CODE1.ShowColumnList=NAMESPEDRS_CODE1.HisOneNullRow=YADM_CATEGORY1.Type=医保就医类别下拉区域ADM_CATEGORY1.X=469ADM_CATEGORY1.Y=133ADM_CATEGORY1.Width=138ADM_CATEGORY1.Height=23ADM_CATEGORY1.Text=ADM_CATEGORY1.HorizontalAlignment=2ADM_CATEGORY1.PopupMenuHeader=代码,100;名称,100ADM_CATEGORY1.PopupMenuWidth=300ADM_CATEGORY1.PopupMenuHeight=300ADM_CATEGORY1.PopupMenuFilter=ID,1;NAME,1;PY1,1ADM_CATEGORY1.FormatType=comboADM_CATEGORY1.ShowDownButton=YADM_CATEGORY1.Tip=医保就医类别ADM_CATEGORY1.ShowColumnList=NAMEADM_CATEGORY1.HisOneNullRow=YSPE_DISEASE.Type=TTextFieldSPE_DISEASE.X=185SPE_DISEASE.Y=275SPE_DISEASE.Width=421SPE_DISEASE.Height=20SPE_DISEASE.Text=tLabel_67.Type=TLabeltLabel_67.X=95tLabel_67.Y=277tLabel_67.Width=64tLabel_67.Height=15tLabel_67.Text=专科疾病:tPanel_5.Type=TPaneltPanel_5.X=3tPanel_5.Y=375tPanel_5.Width=1010tPanel_5.Height=118tPanel_5.Border=组|城居tPanel_5.AutoWidth=YtPanel_5.Item=tLabel_12;tLabel_64;tLabel_65;BEARING_OPERATIONS_TYPE;HOMEDIAG_CODE1;TRAMA_ATTESTTRAMA_ATTEST.Type=TTextAreaTRAMA_ATTEST.X=185TRAMA_ATTEST.Y=52TRAMA_ATTEST.Width=421TRAMA_ATTEST.Height=58TRAMA_ATTEST.SpacingRow=1TRAMA_ATTEST.RowHeight=20TRAMA_ATTEST.Enabled=YTRAMA_ATTEST.Visible=YHOMEDIAG_CODE1.Type=TComboBoxHOMEDIAG_CODE1.X=475HOMEDIAG_CODE1.Y=16HOMEDIAG_CODE1.Width=130HOMEDIAG_CODE1.Height=23HOMEDIAG_CODE1.Text=TButtonHOMEDIAG_CODE1.showID=YHOMEDIAG_CODE1.Editable=YBEARING_OPERATIONS_TYPE.Type=TComboBoxBEARING_OPERATIONS_TYPE.X=185BEARING_OPERATIONS_TYPE.Y=17BEARING_OPERATIONS_TYPE.Width=116BEARING_OPERATIONS_TYPE.Height=23BEARING_OPERATIONS_TYPE.Text=TButtonBEARING_OPERATIONS_TYPE.showID=YBEARING_OPERATIONS_TYPE.Editable=YtLabel_65.Type=TLabeltLabel_65.X=100tLabel_65.Y=60tLabel_65.Width=64tLabel_65.Height=15tLabel_65.Text=外伤证明:tLabel_64.Type=TLabeltLabel_64.X=406tLabel_64.Y=20tLabel_64.Width=65tLabel_64.Height=15tLabel_64.Text=家床病种:tLabel_12.Type=TLabeltLabel_12.X=73tLabel_12.Y=21tLabel_12.Width=93tLabel_12.Height=15tLabel_12.Text=计生手术类别:tPanel_3.Type=TPaneltPanel_3.X=4tPanel_3.Y=295tPanel_3.Width=1009tPanel_3.Height=82tPanel_3.Item=tLabel_0;tLabel_3;tLabel_4;tLabel_7;tLabel_8;tLabel_10;GS_CONFIRM_NO;PRE_OWN_AMT;PRE_ADD_AMT;PRE_CONFIRM_NO;PRE_NHI_AMT;PRE_OUT_TIMEtPanel_3.Border=组|城职tPanel_3.AutoWidth=YPRE_OUT_TIME.Type=TTextFormatPRE_OUT_TIME.X=710PRE_OUT_TIME.Y=50PRE_OUT_TIME.Width=143PRE_OUT_TIME.Height=20PRE_OUT_TIME.Text=PRE_OUT_TIME.FormatType=datePRE_OUT_TIME.Format=yyyy/MM/ddPRE_OUT_TIME.showDownButton=YPRE_NHI_AMT.Type=TNumberTextFieldPRE_NHI_AMT.X=471PRE_NHI_AMT.Y=50PRE_NHI_AMT.Width=98PRE_NHI_AMT.Height=20PRE_NHI_AMT.Text=0PRE_NHI_AMT.Format=#########0.00PRE_CONFIRM_NO.Type=TTextFieldPRE_CONFIRM_NO.X=185PRE_CONFIRM_NO.Y=50PRE_CONFIRM_NO.Width=128PRE_CONFIRM_NO.Height=20PRE_CONFIRM_NO.Text=PRE_ADD_AMT.Type=TNumberTextFieldPRE_ADD_AMT.X=711PRE_ADD_AMT.Y=21PRE_ADD_AMT.Width=99PRE_ADD_AMT.Height=20PRE_ADD_AMT.Text=0PRE_ADD_AMT.Format=#########0.00PRE_OWN_AMT.Type=TNumberTextFieldPRE_OWN_AMT.X=471PRE_OWN_AMT.Y=21PRE_OWN_AMT.Width=98PRE_OWN_AMT.Height=20PRE_OWN_AMT.Text=0PRE_OWN_AMT.Format=#########0.00GS_CONFIRM_NO.Type=TTextFieldGS_CONFIRM_NO.X=185GS_CONFIRM_NO.Y=21GS_CONFIRM_NO.Width=127GS_CONFIRM_NO.Height=20GS_CONFIRM_NO.Text=tLabel_10.Type=TLabeltLabel_10.X=613tLabel_10.Y=53tLabel_10.Width=92tLabel_10.Height=15tLabel_10.Text=上次出院时间:tLabel_8.Type=TLabeltLabel_8.X=377tLabel_8.Y=53tLabel_8.Width=93tLabel_8.Height=15tLabel_8.Text=上次申报金额:tLabel_7.Type=TLabeltLabel_7.X=33tLabel_7.Y=53tLabel_7.Width=136tLabel_7.Height=15tLabel_7.Text=上次资格确认书编号:tLabel_4.Type=TLabeltLabel_4.X=585tLabel_4.Y=24tLabel_4.Width=120tLabel_4.Height=15tLabel_4.Text=上次增负项目金额:tLabel_3.Type=TLabeltLabel_3.X=349tLabel_3.Y=24tLabel_3.Width=121tLabel_3.Height=15tLabel_3.Text=上次自费项目金额:tLabel_0.Type=TLabeltLabel_0.X=34tLabel_0.Y=24tLabel_0.Width=137tLabel_0.Height=15tLabel_0.Text=工伤资格确认书编号:ADM_PRJ1.Type=TComboBoxADM_PRJ1.X=185ADM_PRJ1.Y=133ADM_PRJ1.Width=130ADM_PRJ1.Height=23ADM_PRJ1.Text=TButtonADM_PRJ1.showID=YADM_PRJ1.Editable=YADM_PRJ1.StringData=[[id,text],[,],[1,门诊],[2,住院],[3,转诊转院]]ADM_PRJ1.TableShowList=textADM_PRJ1.Tip=就医专案REGION_CODE1.Type=区域下拉列表REGION_CODE1.X=185REGION_CODE1.Y=100REGION_CODE1.Width=131REGION_CODE1.Height=23REGION_CODE1.Text=TButtonREGION_CODE1.showID=YREGION_CODE1.showName=YREGION_CODE1.showText=NREGION_CODE1.showValue=NREGION_CODE1.showPy1=NREGION_CODE1.showPy2=NREGION_CODE1.Editable=YREGION_CODE1.Tip=区域REGION_CODE1.TableShowList=nameREGION_CODE1.ModuleParmString=REGION_CODE1.ModuleParmTag=REGION_CODE1.ExpandWidth=80DIAG_DESC1.Type=TTextFieldDIAG_DESC1.X=469DIAG_DESC1.Y=169DIAG_DESC1.Width=390DIAG_DESC1.Height=20DIAG_DESC1.Text=DIAG_DESC1.Tip=住院诊断TRAN_NUM1.Type=TTextFieldTRAN_NUM1.X=683TRAN_NUM1.Y=244TRAN_NUM1.Width=155TRAN_NUM1.Height=20TRAN_NUM1.Text=TRAN_NUM1.Tip=转诊转院审批号DEPT_CODE1.Type=科室DEPT_CODE1.X=185DEPT_CODE1.Y=169DEPT_CODE1.Width=130DEPT_CODE1.Height=23DEPT_CODE1.Text=DEPT_CODE1.HorizontalAlignment=2DEPT_CODE1.PopupMenuHeader=代码,100;名称,100DEPT_CODE1.PopupMenuWidth=300DEPT_CODE1.PopupMenuHeight=300DEPT_CODE1.FormatType=comboDEPT_CODE1.ShowDownButton=YDEPT_CODE1.Tip=住院科室DEPT_CODE1.ShowColumnList=NAMEPERSONAL_NO.Type=TTextFieldPERSONAL_NO.X=185PERSONAL_NO.Y=245PERSONAL_NO.Width=97PERSONAL_NO.Height=20PERSONAL_NO.Text=PERSONAL_NO.Tip=个人编号PERSONAL_NO.Enabled=NIN_DATE.Type=TTextFormatIN_DATE.X=393IN_DATE.Y=246IN_DATE.Width=149IN_DATE.Height=20IN_DATE.Text=IN_DATE.Format=yyyy/MM/ddIN_DATE.FormatType=dateIN_DATE.showDownButton=YIN_DATE.Tip=住院日期APP_DATE.Type=TTextFormatAPP_DATE.X=469APP_DATE.Y=101APP_DATE.Width=138APP_DATE.Height=20APP_DATE.Text=APP_DATE.showDownButton=YAPP_DATE.FormatType=dateAPP_DATE.Format=yyyy/MM/ddAPP_DATE.Tip=申请日期PAT_NAME1.Type=TTextFieldPAT_NAME1.X=709PAT_NAME1.Y=64PAT_NAME1.Width=77PAT_NAME1.Height=20PAT_NAME1.Text=PAT_NAME1.Tip=姓名IDNO1.Type=TTextFieldIDNO1.X=469IDNO1.Y=64IDNO1.Width=137IDNO1.Height=20IDNO1.Text=IDNO1.Tip=身份证IDNO1.Enabled=YIDNO1.Action=MR_NO.Type=TTextFieldMR_NO.X=185MR_NO.Y=64MR_NO.Width=130MR_NO.Height=20MR_NO.Text=MR_NO.Tip=病案号MR_NO.Action=onMrNoRESV_NO.Type=TTextFieldRESV_NO.X=655RESV_NO.Y=18RESV_NO.Width=167RESV_NO.Height=20RESV_NO.Text=RESV_NO.Tip=预约单号RESV_NO.Enabled=NCONFIRM_NO1.Type=TTextFieldCONFIRM_NO1.X=387CONFIRM_NO1.Y=18CONFIRM_NO1.Width=142CONFIRM_NO1.Height=20CONFIRM_NO1.Text=CONFIRM_NO1.Tip=资格确认书编号EMG_FLG1.Type=TCheckBoxEMG_FLG1.X=737EMG_FLG1.Y=100EMG_FLG1.Width=81EMG_FLG1.Height=23EMG_FLG1.Text=急诊OVERINP_FLG1.Type=TCheckBoxOVERINP_FLG1.X=630OVERINP_FLG1.Y=100OVERINP_FLG1.Width=96OVERINP_FLG1.Height=23OVERINP_FLG1.Text=跨年度住院OVERINP_FLG1.Visible=YOVERINP_FLG1.Enabled=YtLabel_90.Type=TLabeltLabel_90.X=391tLabel_90.Y=211tLabel_90.Width=125tLabel_90.Height=15tLabel_90.Text=大额医疗救助单位:tLabel_85.Type=TLabeltLabel_85.X=13tLabel_85.Y=211tLabel_85.Width=148tLabel_85.Height=15tLabel_85.Text=开具资格确认书分中心:tLabel_84.Type=TLabeltLabel_84.X=568tLabel_84.Y=246tLabel_84.Width=109tLabel_84.Height=17tLabel_84.Text=转诊转院审批号:tLabel_83.Type=TLabeltLabel_83.X=316tLabel_83.Y=249tLabel_83.Width=72tLabel_83.Height=15tLabel_83.Text=住院日期:tLabel_82.Type=TLabeltLabel_82.X=389tLabel_82.Y=172tLabel_82.Width=65tLabel_82.Height=15tLabel_82.Text=住院诊断:tLabel_81.Type=TLabeltLabel_81.X=96tLabel_81.Y=170tLabel_81.Width=65tLabel_81.Height=19tLabel_81.Text=住院科别:tLabel_80.Type=TLabeltLabel_80.X=632tLabel_80.Y=137tLabel_80.Width=65tLabel_80.Height=15tLabel_80.Text=门特类别:tLabel_79.Type=TLabeltLabel_79.X=388tLabel_79.Y=137tLabel_79.Width=66tLabel_79.Height=15tLabel_79.Text=就医类别:tLabel_78.Type=TLabeltLabel_78.X=96tLabel_78.Y=137tLabel_78.Width=65tLabel_78.Height=15tLabel_78.Text=就医专案:tLabel_76.Type=TLabeltLabel_76.X=389tLabel_76.Y=104tLabel_76.Width=65tLabel_76.Height=15tLabel_76.Text=申请日期:tLabel_75.Type=TLabeltLabel_75.X=96tLabel_75.Y=104tLabel_75.Width=64tLabel_75.Height=15tLabel_75.Text=医院编码:tLabel_73.Type=TLabeltLabel_73.X=635tLabel_73.Y=67tLabel_73.Width=56tLabel_73.Height=15tLabel_73.Text=姓   名:tLabel_71.Type=TLabeltLabel_71.X=403tLabel_71.Y=67tLabel_71.Width=51tLabel_71.Height=15tLabel_71.Text=身份证:tLabel_69.Type=TLabeltLabel_69.X=109tLabel_69.Y=67tLabel_69.Width=51tLabel_69.Height=15tLabel_69.Text=病案号:tLabel_45.Type=TLabeltLabel_45.X=573tLabel_45.Y=21tLabel_45.Width=72tLabel_45.Height=15tLabel_45.Text=预约单号:tLabel_9.Type=TLabeltLabel_9.X=265tLabel_9.Y=21tLabel_9.Width=109tLabel_9.Height=15tLabel_9.Text=资格确认书编号:tPanel_10.Type=TPaneltPanel_10.X=25tPanel_10.Y=8tPanel_10.Width=162tPanel_10.Height=45tPanel_10.Border=组tPanel_10.Item=RO_Upd;RO_OpenRO_Open.Type=TRadioButtonRO_Open.X=92RO_Open.Y=11RO_Open.Width=55RO_Open.Height=23RO_Open.Text=开立RO_Open.Group=groupRO_Open.Action=onExeRO_Upd.Type=TRadioButtonRO_Upd.X=17RO_Upd.Y=11RO_Upd.Width=59RO_Upd.Height=23RO_Upd.Text=下载RO_Upd.Group=groupRO_Upd.Selected=YRO_Upd.Action=onExetLabel_14.Type=TLabeltLabel_14.X=53tLabel_14.Y=248tLabel_14.Width=108tLabel_14.Height=15tLabel_14.Text=个人编号(卡号):tPanel_0.Type=TPaneltPanel_0.X=7tPanel_0.Y=12tPanel_0.Width=1021tPanel_0.Height=51tPanel_0.Border=凸tPanel_0.Item=lbl;CONFIRM_NO2;tLabel_1;tLabel_63;INS_CROWD_TYPE;INSCASE_NO1;tButton_0tPanel_0.AutoWidth=YtButton_0.Type=TButtontButton_0.X=577tButton_0.Y=16tButton_0.Width=81tButton_0.Height=23tButton_0.Text=查询tButton_0.Action=onQueryInsInfoINSCASE_NO1.Type=TTextFieldINSCASE_NO1.X=428INSCASE_NO1.Y=17INSCASE_NO1.Width=119INSCASE_NO1.Height=20INSCASE_NO1.Text=INS_CROWD_TYPE.Type=TComboBoxINS_CROWD_TYPE.X=759INS_CROWD_TYPE.Y=15INS_CROWD_TYPE.Width=81INS_CROWD_TYPE.Height=23INS_CROWD_TYPE.Text=TButtonINS_CROWD_TYPE.showID=YINS_CROWD_TYPE.Editable=YINS_CROWD_TYPE.StringData=[[id,text],[,],[1,城职],[2,城居]]INS_CROWD_TYPE.TableShowList=textINS_CROWD_TYPE.Enabled=NINS_CROWD_TYPE.Tip=人群类别tLabel_63.Type=TLabeltLabel_63.X=684tLabel_63.Y=19tLabel_63.Width=66tLabel_63.Height=15tLabel_63.Text=人群类别:tLabel_1.Type=TLabeltLabel_1.X=331tLabel_1.Y=21tLabel_1.Width=95tLabel_1.Height=15tLabel_1.Text=医保住院编号:tLabel_1.Color=蓝CONFIRM_NO2.Type=TTextFieldCONFIRM_NO2.X=170CONFIRM_NO2.Y=18CONFIRM_NO2.Width=116CONFIRM_NO2.Height=20CONFIRM_NO2.Text=lbl.Type=TLabellbl.X=51lbl.Y=20lbl.Width=110lbl.Height=17lbl.Text=资格确认书编号:lbl.Color=蓝