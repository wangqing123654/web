## TBuilder Config File ## Title:## Company:JavaHis## Author:fuxin 2012.04.19## version 1.0  #<Type=TFrame>UI.Title=血库历史查询UI.MenuConfig=%ROOT%\config\bms\BMSHistoryQueryMenu.xUI.Width=1193UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.bms.BMSHistoryQueryControlUI.item=tPanel_2;TABLEUI.layout=nullUI.Name=血库历史查询UI.Text=血库历史查询UI.Tip=血库历史查询UI.TopToolBar=YUI.TopMenu=YUI.ShowMenu=YTABLE.Type=TTableTABLE.X=11TABLE.Y=151TABLE.Width=1153TABLE.Height=592TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,left;17,left;18,left;19,left;20,left;21,left;22,left;23,left;24,leftTABLE.Header=血袋号码,120;血品,150,BLD_CODE;规格,150,SUBCAT_CODE;入库日期,100;血袋状况,80,STATE_CODE;血型,40,BLD_TYPE;抗体筛选,100,SHIT_FLG;效期,80;备血单号,100;病案号,100,MR_NO;姓名,80;用血日期,100;大交叉,100;小交叉,100;Anti-A,60;Anti-B,60;配血结果,80;检验日期,100;检验人员,80,DR_CODE;出库日期,150,timestamp,yyyy/MM/dd HH:mm:ss;出库单号,120;变更原因,80;变更日期,80;操作人员,80,DR_CODE;操作日期,80;操作端末,120;用血科室,80,DEPT_CODE TABLE.LockColumns=allTABLE.ParmMap=BLOOD_NO;BLD_CODE;SUBCAT_CODE;IN_DATE;STATE_CODE;BLD_TYPE;SHIT_FLG;END_DATE;APPLY_NO;MR_NO;PAT_NAME;USE_DATE;crossMatchL;crossMatchS;ANTI_A;ANTI_B;crossResult;TEST_DATE;TEST_USER;OUT_DATE;OUT_NO;TRAN_RESN;TRAN_DATE;OPT_USER;OPT_DATE;OPT_TERM;DEPT_CODETABLE.ClickedAction=onTableClickedTABLE.Item=BLD_CODE;SUBCAT_CODE;IN_DATE;STATE_CODE;BLD_TYPE;DEPT_CODE;DR_CODE;DEPT_CODETABLE.AutoWidth=YTABLE.AutoHeight=YtPanel_2.Type=TPaneltPanel_2.X=12tPanel_2.Y=13tPanel_2.Width=1176tPanel_2.Height=132tPanel_2.Border=组|tPanel_2.Item=tLabel_3;tLabel_4;tLabel_5;tLabel_6;tLabel_7;tLabel_8;tLabel_10;tLabel_11;tLabel_12;S_DATE;E_DATE;tLabel_13;BLD_CODE;SUBCAT_CODE;BLD_TYPE;IN_DATE;DEPT_CODE;BLOOD_VOL;tLabel_14;MR_NO;STATE_CODE;tLabel_0;OUT_NO;DR_CODE;DEPT_CODEtPanel_2.Tip=～tPanel_2.AutoWidth=YDEPT_CODE.Type=科室属性下拉区域DEPT_CODE.X=853DEPT_CODE.Y=146DEPT_CODE.Width=81DEPT_CODE.Height=23DEPT_CODE.Text=DEPT_CODE.HorizontalAlignment=2DEPT_CODE.PopupMenuHeader=代码,100;名称,100DEPT_CODE.PopupMenuWidth=300DEPT_CODE.PopupMenuHeight=300DEPT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DEPT_CODE.FormatType=comboDEPT_CODE.ShowDownButton=YDEPT_CODE.Tip=科室属性DEPT_CODE.ShowColumnList=NAMEDR_CODE.Type=人员DR_CODE.X=733DR_CODE.Y=141DR_CODE.Width=81DR_CODE.Height=23DR_CODE.Text=DR_CODE.HorizontalAlignment=2DR_CODE.PopupMenuHeader=代码,100;名称,100DR_CODE.PopupMenuWidth=300DR_CODE.PopupMenuHeight=300DR_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DR_CODE.FormatType=comboDR_CODE.ShowDownButton=YDR_CODE.Tip=人员DR_CODE.ShowColumnList=NAMEOUT_NO.Type=TTextFieldOUT_NO.X=86OUT_NO.Y=82OUT_NO.Width=166OUT_NO.Height=20OUT_NO.Text=tLabel_0.Type=TLabeltLabel_0.X=16tLabel_0.Y=81tLabel_0.Width=72tLabel_0.Height=18tLabel_0.Text=出库单号:tLabel_0.Color=蓝STATE_CODE.Type=TComboBoxSTATE_CODE.X=759STATE_CODE.Y=48STATE_CODE.Width=87STATE_CODE.Height=23STATE_CODE.Text=TButtonSTATE_CODE.showID=YSTATE_CODE.Editable=YSTATE_CODE.StringData=[[id,name],[,],[0,入库],[1,交叉配血],[2,出库],[3,报废]]STATE_CODE.TableShowList=nameSTATE_CODE.ShowName=YSTATE_CODE.ShowText=NMR_NO.Type=TTextFieldMR_NO.X=336MR_NO.Y=79MR_NO.Width=112MR_NO.Height=23MR_NO.Text=MR_NO.Action=onMrNoActiontLabel_14.Type=TLabeltLabel_14.X=639tLabel_14.Y=76tLabel_14.Width=104tLabel_14.Height=28tLabel_14.Text=mlBLOOD_VOL.Type=TNumberTextFieldBLOOD_VOL.X=549BLOOD_VOL.Y=78BLOOD_VOL.Width=81BLOOD_VOL.Height=23BLOOD_VOL.Text=0BLOOD_VOL.Format=#########0DEPT_CODE.Type=科室下拉列表DEPT_CODE.X=760DEPT_CODE.Y=18DEPT_CODE.Width=107DEPT_CODE.Height=23DEPT_CODE.Text=TButtonDEPT_CODE.showID=YDEPT_CODE.showName=YDEPT_CODE.showText=NDEPT_CODE.showValue=NDEPT_CODE.showPy1=NDEPT_CODE.showPy2=NDEPT_CODE.Editable=YDEPT_CODE.Tip=科室DEPT_CODE.TableShowList=nameDEPT_CODE.Name=DEPT_CODE.ExpandWidth=60IN_DATE.Type=TTextFormatIN_DATE.X=547IN_DATE.Y=16IN_DATE.Width=124IN_DATE.Height=23IN_DATE.Text=IN_DATE.FormatType=dateIN_DATE.Format=yyyy/MM/dd IN_DATE.showDownButton=YBLD_TYPE.Type=血型下拉列表BLD_TYPE.X=548BLD_TYPE.Y=45BLD_TYPE.Width=84BLD_TYPE.Height=23BLD_TYPE.Text=TButtonBLD_TYPE.showID=YBLD_TYPE.showName=YBLD_TYPE.showText=NBLD_TYPE.showValue=NBLD_TYPE.showPy1=NBLD_TYPE.showPy2=NBLD_TYPE.Editable=YBLD_TYPE.Tip=血型下拉列表BLD_TYPE.TableShowList=nameBLD_TYPE.ModuleParmString=GROUP_ID:SYS_BLOODBLD_TYPE.ModuleParmTag=SUBCAT_CODE.Type=血品规格SUBCAT_CODE.X=336SUBCAT_CODE.Y=46SUBCAT_CODE.Width=153SUBCAT_CODE.Height=23SUBCAT_CODE.Text=TButtonSUBCAT_CODE.showID=YSUBCAT_CODE.showName=YSUBCAT_CODE.showText=NSUBCAT_CODE.showValue=NSUBCAT_CODE.showPy1=NSUBCAT_CODE.showPy2=NSUBCAT_CODE.Editable=YSUBCAT_CODE.Tip=诊室SUBCAT_CODE.TableShowList=nameSUBCAT_CODE.ModuleParmTag=SUBCAT_CODE.ExpandWidth=60BLD_CODE.Type=血品BLD_CODE.X=86BLD_CODE.Y=50BLD_CODE.Width=165BLD_CODE.Height=23BLD_CODE.Text=TButtonBLD_CODE.showID=YBLD_CODE.showName=YBLD_CODE.showText=NBLD_CODE.showValue=NBLD_CODE.showPy1=NBLD_CODE.showPy2=NBLD_CODE.Editable=YBLD_CODE.Tip=血品BLD_CODE.TableShowList=nameBLD_CODE.ModuleParmTag=BLD_CODE.Action=onChangeBldBLD_CODE.SelectedAction=onChangeBldBLD_CODE.ExpandWidth=20tLabel_13.Type=TLabeltLabel_13.X=265tLabel_13.Y=16tLabel_13.Width=18tLabel_13.Height=23tLabel_13.Text=～E_DATE.Type=TTextFormatE_DATE.X=287E_DATE.Y=16E_DATE.Width=160E_DATE.Height=23E_DATE.Text=E_DATE.Format=yyyy/MM/dd HH:mm:ssE_DATE.FormatType=dateE_DATE.showDownButton=YS_DATE.Type=TTextFormatS_DATE.X=86S_DATE.Y=16S_DATE.Width=167S_DATE.Height=23S_DATE.Text=S_DATE.Format=yyyy/MM/dd HH:mm:ssS_DATE.FormatType=dateS_DATE.showDownButton=YtLabel_12.Type=TLabeltLabel_12.X=479tLabel_12.Y=79tLabel_12.Width=72tLabel_12.Height=25tLabel_12.Text=血量统计：tLabel_12.Color=黑tLabel_11.Type=TLabeltLabel_11.X=281tLabel_11.Y=82tLabel_11.Width=62tLabel_11.Height=18tLabel_11.Text=病案号：tLabel_11.Color=蓝tLabel_10.Type=TLabeltLabel_10.X=682tLabel_10.Y=23tLabel_10.Width=72tLabel_10.Height=15tLabel_10.Text=用血科室：tLabel_10.Color=蓝tLabel_8.Type=TLabeltLabel_8.X=505tLabel_8.Y=49tLabel_8.Width=44tLabel_8.Height=15tLabel_8.Text=血型：tLabel_8.Color=蓝tLabel_7.Type=TLabeltLabel_7.X=682tLabel_7.Y=45tLabel_7.Width=72tLabel_7.Height=31tLabel_7.Text=血液状况：tLabel_7.Color=蓝tLabel_6.Type=TLabeltLabel_6.X=476tLabel_6.Y=18tLabel_6.Width=72tLabel_6.Height=15tLabel_6.Text=入库日期：tLabel_6.Color=蓝tLabel_5.Type=TLabeltLabel_5.X=267tLabel_5.Y=47tLabel_5.Width=72tLabel_5.Height=20tLabel_5.Text=血品规格：tLabel_5.Color=蓝tLabel_4.Type=TLabeltLabel_4.X=14tLabel_4.Y=19tLabel_4.Width=71tLabel_4.Height=18tLabel_4.Text=出库时间：tLabel_4.Color=蓝tLabel_3.Type=TLabeltLabel_3.X=43tLabel_3.Y=49tLabel_3.Width=42tLabel_3.Height=18tLabel_3.Text=血品：tLabel_3.Color=蓝