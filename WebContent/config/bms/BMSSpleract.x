## TBuilder Config File ## Title:输血反应记录## Company:JavaHis## Author:zhangy 2009.09.28## version 1.0#<Type=TFrame>UI.Title=输血反应记录UI.MenuConfig=%ROOT%\config\bms\BMSSpleractMenu.xUI.Width=1176UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.bms.BMSSpleractControlUI.item=tPanel_0;tPanel_1;tPanel_4;tPanel_5UI.layout=nullUI.Name=输血反应记录UI.Text=输血反应记录UI.Tip=输血反应记录UI.TopMenu=YUI.TopToolBar=YtPanel_5.Type=TPaneltPanel_5.X=5tPanel_5.Y=112tPanel_5.Width=1166tPanel_5.Height=165tPanel_5.Border=组|申请单信息tPanel_5.AutoX=YtPanel_5.AutoWidth=YtPanel_5.Item=tLabel_28;tLabel_29;tLabel_30;tLabel_31;OUT_DATE;BLD_TYPE;RH_A1;RH_B1;BLOOD_VOL;tLabel_22;APPLY_NO;tLabel_23;tLabel_24;PREGNANCY;tLabel_25;INFANT;tLabel_26;PAT_OTH;tLabel_27;DIAG_CODE1;TRANS_HISTORY;BLD_CODE;tLabel_20;BLOOD_NO;tLabel_21;BLOOD_SOURCE;tLabel_11;DIAG_CODE2;DIAG_CODE3;UNIT_CODEUNIT_CODE.Type=计量单位下拉列表UNIT_CODE.X=803UNIT_CODE.Y=56UNIT_CODE.Width=55UNIT_CODE.Height=20UNIT_CODE.Text=TButtonUNIT_CODE.showID=YUNIT_CODE.showName=YUNIT_CODE.showText=NUNIT_CODE.showValue=NUNIT_CODE.showPy1=NUNIT_CODE.showPy2=NUNIT_CODE.Editable=YUNIT_CODE.Tip=计量单位UNIT_CODE.TableShowList=nameUNIT_CODE.Enabled=NDIAG_CODE3.Type=TTextFieldDIAG_CODE3.X=619DIAG_CODE3.Y=126DIAG_CODE3.Width=240DIAG_CODE3.Height=20DIAG_CODE3.Text=DIAG_CODE3.Enabled=NDIAG_CODE2.Type=TTextFieldDIAG_CODE2.X=366DIAG_CODE2.Y=126DIAG_CODE2.Width=240DIAG_CODE2.Height=20DIAG_CODE2.Text=DIAG_CODE2.Enabled=NtLabel_11.Type=TLabeltLabel_11.X=18tLabel_11.Y=57tLabel_11.Width=72tLabel_11.Height=15tLabel_11.Text=反应血品BLOOD_SOURCE.Type=血液来源BLOOD_SOURCE.X=645BLOOD_SOURCE.Y=19BLOOD_SOURCE.Width=212BLOOD_SOURCE.Height=20BLOOD_SOURCE.Text=TButtonBLOOD_SOURCE.showID=YBLOOD_SOURCE.showName=YBLOOD_SOURCE.showText=NBLOOD_SOURCE.showValue=NBLOOD_SOURCE.showPy1=NBLOOD_SOURCE.showPy2=NBLOOD_SOURCE.Editable=YBLOOD_SOURCE.Tip=血液来源BLOOD_SOURCE.TableShowList=nameBLOOD_SOURCE.ModuleParmString=GROUP_ID:BMS_BLDRESUBLOOD_SOURCE.ModuleParmTag=BLOOD_SOURCE.Enabled=YtLabel_21.Type=TLabeltLabel_21.X=564tLabel_21.Y=20tLabel_21.Width=72tLabel_21.Height=15tLabel_21.Text=血液来源BLOOD_NO.Type=TTextFieldBLOOD_NO.X=109BLOOD_NO.Y=21BLOOD_NO.Width=158BLOOD_NO.Height=20BLOOD_NO.Text=BLOOD_NO.Action=onBloodNoActiontLabel_20.Type=TLabeltLabel_20.X=18tLabel_20.Y=23tLabel_20.Width=62tLabel_20.Height=15tLabel_20.Text=院内条码BLD_CODE.Type=血品BLD_CODE.X=108BLD_CODE.Y=56BLD_CODE.Width=160BLD_CODE.Height=20BLD_CODE.Text=TButtonBLD_CODE.showID=YBLD_CODE.showName=YBLD_CODE.showText=NBLD_CODE.showValue=NBLD_CODE.showPy1=NBLD_CODE.showPy2=NBLD_CODE.Editable=YBLD_CODE.Tip=血品BLD_CODE.TableShowList=nameBLD_CODE.ModuleParmTag=BLD_CODE.Enabled=NTRANS_HISTORY.Type=既往输血史下拉区域TRANS_HISTORY.X=363TRANS_HISTORY.Y=88TRANS_HISTORY.Width=81TRANS_HISTORY.Height=20TRANS_HISTORY.Text=TRANS_HISTORY.HorizontalAlignment=2TRANS_HISTORY.PopupMenuHeader=代码,100;名称,100TRANS_HISTORY.PopupMenuWidth=300TRANS_HISTORY.PopupMenuHeight=300TRANS_HISTORY.PopupMenuFilter=ID,1;NAME,1;PY1,1TRANS_HISTORY.FormatType=comboTRANS_HISTORY.ShowDownButton=YTRANS_HISTORY.Tip=既往输血史TRANS_HISTORY.ShowColumnList=NAMETRANS_HISTORY.HisOneNullRow=YTRANS_HISTORY.Enabled=NTRANS_HISTORY.ValueColumn=DIAG_CODE1.Type=TTextFieldDIAG_CODE1.X=107DIAG_CODE1.Y=125DIAG_CODE1.Width=239DIAG_CODE1.Height=20DIAG_CODE1.Text=DIAG_CODE1.Enabled=NtLabel_27.Type=TLabeltLabel_27.X=18tLabel_27.Y=127tLabel_27.Width=72tLabel_27.Height=15tLabel_27.Text=诊断信息PAT_OTH.Type=TTextFieldPAT_OTH.X=645PAT_OTH.Y=86PAT_OTH.Width=212PAT_OTH.Height=20PAT_OTH.Text=PAT_OTH.Enabled=NtLabel_26.Type=TLabeltLabel_26.X=604tLabel_26.Y=88tLabel_26.Width=35tLabel_26.Height=15tLabel_26.Text=其他INFANT.Type=TTextFieldINFANT.X=553INFANT.Y=86INFANT.Width=30INFANT.Height=20INFANT.Text=INFANT.Enabled=NtLabel_25.Type=TLabeltLabel_25.X=530tLabel_25.Y=90tLabel_25.Width=23tLabel_25.Height=15tLabel_25.Text=产PREGNANCY.Type=TTextFieldPREGNANCY.X=489PREGNANCY.Y=87PREGNANCY.Width=30PREGNANCY.Height=20PREGNANCY.Text=PREGNANCY.Enabled=NtLabel_24.Type=TLabeltLabel_24.X=464tLabel_24.Y=88tLabel_24.Width=24tLabel_24.Height=15tLabel_24.Text=孕tLabel_23.Type=TLabeltLabel_23.X=288tLabel_23.Y=89tLabel_23.Width=57tLabel_23.Height=15tLabel_23.Text=输血史APPLY_NO.Type=TTextFieldAPPLY_NO.X=108APPLY_NO.Y=91APPLY_NO.Width=160APPLY_NO.Height=20APPLY_NO.Text=APPLY_NO.Enabled=NtLabel_22.Type=TLabeltLabel_22.X=17tLabel_22.Y=92tLabel_22.Width=86tLabel_22.Height=15tLabel_22.Text=备血申请单号BLOOD_VOL.Type=TTextFieldBLOOD_VOL.X=728BLOOD_VOL.Y=56BLOOD_VOL.Width=75BLOOD_VOL.Height=20BLOOD_VOL.Text=BLOOD_VOL.Enabled=NRH_B1.Type=TRadioButtonRH_B1.X=605RH_B1.Y=56RH_B1.Width=69RH_B1.Height=23RH_B1.Text=RH阴性RH_B1.Group=RHRH_B1.Enabled=NRH_A1.Type=TRadioButtonRH_A1.X=533RH_A1.Y=55RH_A1.Width=68RH_A1.Height=23RH_A1.Text=RH阳性RH_A1.Group=RHRH_A1.Enabled=NBLD_TYPE.Type=血型下拉列表BLD_TYPE.X=362BLD_TYPE.Y=56BLD_TYPE.Width=61BLD_TYPE.Height=20BLD_TYPE.Text=TButtonBLD_TYPE.showID=YBLD_TYPE.showName=YBLD_TYPE.showText=NBLD_TYPE.showValue=NBLD_TYPE.showPy1=NBLD_TYPE.showPy2=NBLD_TYPE.Editable=YBLD_TYPE.Tip=血型下拉列表BLD_TYPE.TableShowList=nameBLD_TYPE.ModuleParmString=GROUP_ID:SYS_BLOODBLD_TYPE.ModuleParmTag=BLD_TYPE.Enabled=NOUT_DATE.Type=TTextFormatOUT_DATE.X=362OUT_DATE.Y=20OUT_DATE.Width=160OUT_DATE.Height=20OUT_DATE.Text=OUT_DATE.FormatType=dateOUT_DATE.Format=yyyy/MM/dd HH:mm:ssOUT_DATE.showDownButton=YOUT_DATE.Enabled=NtLabel_31.Type=TLabeltLabel_31.X=283tLabel_31.Y=22tLabel_31.Width=72tLabel_31.Height=15tLabel_31.Text=发血时间tLabel_30.Type=TLabeltLabel_30.X=689tLabel_30.Y=58tLabel_30.Width=35tLabel_30.Height=15tLabel_30.Text=血量tLabel_29.Type=TLabeltLabel_29.X=455tLabel_29.Y=57tLabel_29.Width=72tLabel_29.Height=15tLabel_29.Text=血袋阴阳性tLabel_28.Type=TLabeltLabel_28.X=287tLabel_28.Y=56tLabel_28.Width=63tLabel_28.Height=16tLabel_28.Text=血袋血型tPanel_4.Type=TPaneltPanel_4.X=5tPanel_4.Y=275tPanel_4.Width=1166tPanel_4.Height=127tPanel_4.Border=组|输血反应记录tPanel_4.AutoX=YtPanel_4.AutoWidth=YtPanel_4.AutoHeight=NtPanel_4.Item=tLabel_10;tLabel_12;tLabel_13;START_DATE;tLabel_14;END_DATE;tLabel_15;tLabel_16;tLabel_17;tLabel_18;REACT_OTH;TREAT;RECAT_SYMPTOM;REACT_CLASS;REACTION_CODE;REACT_HIS;tLabel_32;R_FEVER;tLabel_33;tLabel_34;R_SHIVER;tLabel_35;R_URTICARIA;tLabel_36;R_HEMOLYSIS;tLabel_37;R_HEMOGLOBINURIA;tLabel_38;R_OTHERR_OTHER.Type=TComboBoxR_OTHER.X=755R_OTHER.Y=95R_OTHER.Width=50R_OTHER.Height=20R_OTHER.Text=TButtonR_OTHER.showID=YR_OTHER.Editable=YR_OTHER.ShowName=YR_OTHER.ShowText=NR_OTHER.TableShowList=nameR_OTHER.StringData=[[id,name],[,],[0,无],[1,有]]tLabel_38.Type=TLabeltLabel_38.X=715tLabel_38.Y=97tLabel_38.Width=37tLabel_38.Height=15tLabel_38.Text=其他R_HEMOGLOBINURIA.Type=TComboBoxR_HEMOGLOBINURIA.X=643R_HEMOGLOBINURIA.Y=95R_HEMOGLOBINURIA.Width=50R_HEMOGLOBINURIA.Height=20R_HEMOGLOBINURIA.Text=TButtonR_HEMOGLOBINURIA.showID=YR_HEMOGLOBINURIA.Editable=YR_HEMOGLOBINURIA.ShowName=YR_HEMOGLOBINURIA.ShowText=NR_HEMOGLOBINURIA.StringData=[[id,name],[,],[0,无],[1,有]]R_HEMOGLOBINURIA.TableShowList=nametLabel_37.Type=TLabeltLabel_37.X=563tLabel_37.Y=97tLabel_37.Width=75tLabel_37.Height=15tLabel_37.Text=血红蛋白尿R_HEMOLYSIS.Type=TComboBoxR_HEMOLYSIS.X=495R_HEMOLYSIS.Y=95R_HEMOLYSIS.Width=50R_HEMOLYSIS.Height=20R_HEMOLYSIS.Text=TButtonR_HEMOLYSIS.showID=YR_HEMOLYSIS.Editable=YR_HEMOLYSIS.ShowName=YR_HEMOLYSIS.ShowText=NR_HEMOLYSIS.TableShowList=nameR_HEMOLYSIS.StringData=[[id,name],[,],[0,无],[1,有]]tLabel_36.Type=TLabeltLabel_36.X=459tLabel_36.Y=97tLabel_36.Width=36tLabel_36.Height=15tLabel_36.Text=溶血R_URTICARIA.Type=TComboBoxR_URTICARIA.X=388R_URTICARIA.Y=95R_URTICARIA.Width=50R_URTICARIA.Height=20R_URTICARIA.Text=TButtonR_URTICARIA.showID=YR_URTICARIA.Editable=YR_URTICARIA.ShowName=YR_URTICARIA.ShowText=NR_URTICARIA.TableShowList=nameR_URTICARIA.StringData=[[id,name],[,],[0,无],[1,有]]tLabel_35.Type=TLabeltLabel_35.X=340tLabel_35.Y=97tLabel_35.Width=48tLabel_35.Height=15tLabel_35.Text=荨麻疹R_SHIVER.Type=TComboBoxR_SHIVER.X=266R_SHIVER.Y=95R_SHIVER.Width=50R_SHIVER.Height=20R_SHIVER.Text=TButtonR_SHIVER.showID=YR_SHIVER.Editable=YR_SHIVER.StringData=[[id,name],[,],[0,无],[1,有]]R_SHIVER.ShowName=YR_SHIVER.ShowText=NR_SHIVER.TableShowList=nametLabel_34.Type=TLabeltLabel_34.X=225tLabel_34.Y=97tLabel_34.Width=38tLabel_34.Height=15tLabel_34.Text=寒战tLabel_33.Type=TLabeltLabel_33.X=185tLabel_33.Y=97tLabel_33.Width=24tLabel_33.Height=15tLabel_33.Text= ℃R_FEVER.Type=TNumberTextFieldR_FEVER.X=131R_FEVER.Y=95R_FEVER.Width=50R_FEVER.Height=20R_FEVER.Text=R_FEVER.Format=#########0.0tLabel_32.Type=TLabeltLabel_32.X=94tLabel_32.Y=97tLabel_32.Width=36tLabel_32.Height=15tLabel_32.Text=发热REACT_HIS.Type=TComboBoxREACT_HIS.X=613REACT_HIS.Y=22REACT_HIS.Width=56REACT_HIS.Height=20REACT_HIS.Text=TButtonREACT_HIS.showID=YREACT_HIS.Editable=YREACT_HIS.StringData=[[id,name],[,],[0,无],[1,有]]REACT_HIS.ShowText=NREACT_HIS.ShowName=YREACT_HIS.TableShowList=nameREACTION_CODE.Type=TComboBoxREACTION_CODE.X=403REACTION_CODE.Y=198REACTION_CODE.Width=160REACTION_CODE.Height=18REACTION_CODE.Text=TButtonREACTION_CODE.showID=YREACTION_CODE.Editable=YREACTION_CODE.ShowText=NREACTION_CODE.ShowName=YREACTION_CODE.TableShowList=nameREACTION_CODE.StringData=[[id,name],[,],[01,发热],[02,过敏],[03,溶血],[04,其他]]REACTION_CODE.Visible=NREACTION_CODE.Enabled=NREACT_CLASS.Type=TComboBoxREACT_CLASS.X=747REACT_CLASS.Y=23REACT_CLASS.Width=56REACT_CLASS.Height=20REACT_CLASS.Text=TButtonREACT_CLASS.showID=YREACT_CLASS.Editable=YREACT_CLASS.StringData=[[id,name],[,],[0,无],[1,轻],[2,重]]REACT_CLASS.ShowText=NREACT_CLASS.ShowName=YREACT_CLASS.TableShowList=nameRECAT_SYMPTOM.Type=TTextFieldRECAT_SYMPTOM.X=91RECAT_SYMPTOM.Y=61RECAT_SYMPTOM.Width=160RECAT_SYMPTOM.Height=20RECAT_SYMPTOM.Text=TREAT.Type=TTextFieldTREAT.X=609TREAT.Y=61TREAT.Width=195TREAT.Height=20TREAT.Text=REACT_OTH.Type=TTextFieldREACT_OTH.X=343REACT_OTH.Y=61REACT_OTH.Width=160REACT_OTH.Height=20REACT_OTH.Text=tLabel_18.Type=TLabeltLabel_18.X=534tLabel_18.Y=63tLabel_18.Width=42tLabel_18.Height=15tLabel_18.Text=治疗tLabel_17.Type=TLabeltLabel_17.X=528tLabel_17.Y=25tLabel_17.Width=72tLabel_17.Height=15tLabel_17.Text=过敏反应史tLabel_16.Type=TLabeltLabel_16.X=272tLabel_16.Y=63tLabel_16.Width=64tLabel_16.Height=14tLabel_16.Text=其他反应tLabel_15.Type=TLabeltLabel_15.X=683tLabel_15.Y=25tLabel_15.Width=72tLabel_15.Height=15tLabel_15.Text=反应等级END_DATE.Type=TTextFormatEND_DATE.X=344END_DATE.Y=22END_DATE.Width=160END_DATE.Height=20END_DATE.Text=TTextFormatEND_DATE.Format=yyyy/MM/dd HH:mm:ssEND_DATE.FormatType=dateEND_DATE.showDownButton=YtLabel_14.Type=TLabeltLabel_14.X=273tLabel_14.Y=25tLabel_14.Width=72tLabel_14.Height=15tLabel_14.Text=完成时间START_DATE.Type=TTextFormatSTART_DATE.X=90START_DATE.Y=22START_DATE.Width=160START_DATE.Height=20START_DATE.Text=START_DATE.Format=yyyy/MM/dd HH:mm:ssSTART_DATE.FormatType=dateSTART_DATE.showDownButton=YtLabel_13.Type=TLabeltLabel_13.X=14tLabel_13.Y=63tLabel_13.Width=64tLabel_13.Height=15tLabel_13.Text=反应症状tLabel_12.Type=TLabeltLabel_12.X=14tLabel_12.Y=97tLabel_12.Width=72tLabel_12.Height=15tLabel_12.Text=输血反应tLabel_10.Type=TLabeltLabel_10.X=14tLabel_10.Y=25tLabel_10.Width=72tLabel_10.Height=15tLabel_10.Text=输血时间tPanel_1.Type=TPaneltPanel_1.X=5tPanel_1.Y=406tPanel_1.Width=1166tPanel_1.Height=337tPanel_1.AutoX=YtPanel_1.AutoWidth=YtPanel_1.Border=凹tPanel_1.Item=TABLEtPanel_1.AutoHeight=YTABLE.Type=TTableTABLE.X=3TABLE.Y=2TABLE.Width=1162TABLE.Height=333TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=YTABLE.AutoY=YTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.AutoSize=0TABLE.Header=反应单号,150;科室,160,DEPT_CODE;病区,160,STATION_CODE;病案号,150;住院号,150;姓名,120TABLE.ParmMap=REACT_NO;DEPT_CODE;STATION_CODE;MR_NO;IPD_NO;PAT_NAMETABLE.LockColumns=allTABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,leftTABLE.Item=DEPT_CODE;STATION_CODETABLE.ClickedAction=onTableClickedtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=104tPanel_0.Border=组tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.Item=tLabel_0;tLabel_1;tLabel_2;tLabel_3;MR_NO;IPD_NO;tLabel_4;PAT_NAME;tLabel_5;tLabel_6;tLabel_7;ID_NO;tLabel_8;TEST_BLD;tPanel_2;tPanel_3;tLabel_9;REACT_NO;SEX;tLabel_19;CASE_NO;AGE;DEPT_CODE;STATION_CODE;tLabel_39;BED_NOBED_NO.Type=床位下拉区域BED_NO.X=721BED_NO.Y=42BED_NO.Width=62BED_NO.Height=20BED_NO.Text=BED_NO.HorizontalAlignment=2BED_NO.PopupMenuHeader=代码,100;名称,100BED_NO.PopupMenuWidth=300BED_NO.PopupMenuHeight=300BED_NO.PopupMenuFilter=ID,1;NAME,1;PY1,1BED_NO.FormatType=comboBED_NO.ShowDownButton=YBED_NO.Tip=床位BED_NO.ShowColumnList=NAMEBED_NO.ValueColumn=IDBED_NO.Enabled=NtLabel_39.Type=TLabeltLabel_39.X=688tLabel_39.Y=44tLabel_39.Width=35tLabel_39.Height=15tLabel_39.Text=床号STATION_CODE.Type=病区STATION_CODE.X=528STATION_CODE.Y=12STATION_CODE.Width=147STATION_CODE.Height=23STATION_CODE.Text=STATION_CODE.HorizontalAlignment=2STATION_CODE.PopupMenuHeader=代码,100;名称,100STATION_CODE.PopupMenuWidth=300STATION_CODE.PopupMenuHeight=300STATION_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1STATION_CODE.FormatType=comboSTATION_CODE.ShowDownButton=YSTATION_CODE.Tip=病区STATION_CODE.ShowColumnList=NAMESTATION_CODE.HisOneNullRow=YDEPT_CODE.Type=科室DEPT_CODE.X=297DEPT_CODE.Y=13DEPT_CODE.Width=150DEPT_CODE.Height=23DEPT_CODE.Text=DEPT_CODE.HorizontalAlignment=2DEPT_CODE.PopupMenuHeader=代码,100;名称,100DEPT_CODE.PopupMenuWidth=300DEPT_CODE.PopupMenuHeight=300DEPT_CODE.FormatType=comboDEPT_CODE.ShowDownButton=YDEPT_CODE.Tip=科室DEPT_CODE.ShowColumnList=NAMEDEPT_CODE.HisOneNullRow=YDEPT_CODE.DeptGrade=3DEPT_CODE.ClassIfy=0AGE.Type=TTextFieldAGE.X=355AGE.Y=71AGE.Width=89AGE.Height=20AGE.Text=AGE.Enabled=NCASE_NO.Type=TTextFieldCASE_NO.X=296CASE_NO.Y=42CASE_NO.Width=150CASE_NO.Height=20CASE_NO.Text=tLabel_19.Type=TLabeltLabel_19.X=237tLabel_19.Y=45tLabel_19.Width=59tLabel_19.Height=15tLabel_19.Text=就诊序号tLabel_19.Color=blueSEX.Type=性别下拉列表SEX.X=239SEX.Y=72SEX.Width=60SEX.Height=20SEX.Text=TButtonSEX.showID=YSEX.showName=YSEX.showText=NSEX.showValue=NSEX.showPy1=NSEX.showPy2=NSEX.Editable=YSEX.Tip=性别SEX.TableShowList=nameSEX.ModuleParmString=GROUP_ID:SYS_SEXSEX.ModuleParmTag=SEX.Enabled=NREACT_NO.Type=TTextFieldREACT_NO.X=81REACT_NO.Y=11REACT_NO.Width=140REACT_NO.Height=20REACT_NO.Text=tLabel_9.Type=TLabeltLabel_9.X=14tLabel_9.Y=14tLabel_9.Width=58tLabel_9.Height=15tLabel_9.Text=反应单号tLabel_9.Color=bluetPanel_3.Type=TPaneltPanel_3.X=900tPanel_3.Y=11tPanel_3.Width=140tPanel_3.Height=84tPanel_3.Border=组tPanel_3.Item=BLD_TEXTBLD_TEXT.Type=TLabelBLD_TEXT.X=3BLD_TEXT.Y=3BLD_TEXT.Width=137BLD_TEXT.Height=80BLD_TEXT.Text=BLD_TEXT.FontSize=60BLD_TEXT.VerticalAlignment=0BLD_TEXT.Color=redBLD_TEXT.HorizontalAlignment=0tPanel_2.Type=TPaneltPanel_2.X=785tPanel_2.Y=3tPanel_2.Width=112tPanel_2.Height=96tPanel_2.Border=组|RH血型tPanel_2.Item=RH_A;RH_BRH_B.Type=TRadioButtonRH_B.X=21RH_B.Y=57RH_B.Width=81RH_B.Height=23RH_B.Text=RH阴性RH_B.Group=group1RH_A.Type=TRadioButtonRH_A.X=21RH_A.Y=24RH_A.Width=81RH_A.Height=23RH_A.Text=RH阳性RH_A.Group=group1RH_A.Selected=NTEST_BLD.Type=血型下拉列表TEST_BLD.X=720TEST_BLD.Y=72TEST_BLD.Width=63TEST_BLD.Height=20TEST_BLD.Text=TButtonTEST_BLD.showID=YTEST_BLD.showName=YTEST_BLD.showText=NTEST_BLD.showValue=NTEST_BLD.showPy1=NTEST_BLD.showPy2=NTEST_BLD.Editable=YTEST_BLD.Tip=血型下拉列表TEST_BLD.TableShowList=nameTEST_BLD.ModuleParmString=GROUP_ID:SYS_BLOODTEST_BLD.ModuleParmTag=TEST_BLD.Enabled=NtLabel_8.Type=TLabeltLabel_8.X=659tLabel_8.Y=74tLabel_8.Width=62tLabel_8.Height=15tLabel_8.Text=检验血型ID_NO.Type=TTextFieldID_NO.X=529ID_NO.Y=72ID_NO.Width=120ID_NO.Height=20ID_NO.Text=ID_NO.Enabled=NtLabel_7.Type=TLabeltLabel_7.X=472tLabel_7.Y=75tLabel_7.Width=57tLabel_7.Height=15tLabel_7.Text=身份证号tLabel_6.Type=TLabeltLabel_6.X=320tLabel_6.Y=74tLabel_6.Width=34tLabel_6.Height=15tLabel_6.Text=年龄tLabel_5.Type=TLabeltLabel_5.X=201tLabel_5.Y=75tLabel_5.Width=36tLabel_5.Height=15tLabel_5.Text=性别PAT_NAME.Type=TTextFieldPAT_NAME.X=82PAT_NAME.Y=72PAT_NAME.Width=93PAT_NAME.Height=20PAT_NAME.Text=PAT_NAME.Enabled=NtLabel_4.Type=TLabeltLabel_4.X=14tLabel_4.Y=75tLabel_4.Width=36tLabel_4.Height=15tLabel_4.Text=姓名IPD_NO.Type=TTextFieldIPD_NO.X=526IPD_NO.Y=42IPD_NO.Width=150IPD_NO.Height=20IPD_NO.Text=MR_NO.Type=TTextFieldMR_NO.X=81MR_NO.Y=42MR_NO.Width=140MR_NO.Height=20MR_NO.Text=MR_NO.Action=onMrNoActiontLabel_3.Type=TLabeltLabel_3.X=14tLabel_3.Y=45tLabel_3.Width=49tLabel_3.Height=15tLabel_3.Text=病案号tLabel_3.Color=bluetLabel_2.Type=TLabeltLabel_2.X=471tLabel_2.Y=45tLabel_2.Width=49tLabel_2.Height=15tLabel_2.Text=住院号tLabel_2.Color=bluetLabel_1.Type=TLabeltLabel_1.X=471tLabel_1.Y=14tLabel_1.Width=38tLabel_1.Height=15tLabel_1.Text=病区tLabel_1.Color=bluetLabel_0.Type=TLabeltLabel_0.X=237tLabel_0.Y=14tLabel_0.Width=49tLabel_0.Height=15tLabel_0.Text=科室tLabel_0.Color=blue