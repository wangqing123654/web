## TBuilder Config File ## Title:## Company:JavaHis## Author:张建国 2011.04.20## version 1.0#<Type=TFrame>UI.Title=UI.MenuConfig=%ROOT%\config\clp\CLPBscInfoMenu.xUI.Width=1490UI.Height=1485UI.toolbar=YUI.controlclassname=com.javahis.ui.clp.CLPBscInfoControlUI.item=tPanel_0UI.layout=nullUI.TopMenu=YUI.TopToolBar=YUI.Text=临床路径标准设定UI.zhTitle=临床路径标准设定UI.enTitle=临床路径标准设定UI.AutoHeight=NUI.AutoWidth=NUI.X=5UI.AutoX=NUI.Y=5UI.AutoY=NUI.AutoH=NUI.AutoW=NUI.AutoSize=5UI.AutoHSize=0UI.ShowMenu=YtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1480tPanel_0.Height=1475tPanel_0.Border=凸tPanel_0.Item=tTabbedPane_0tPanel_0.AutoWidth=YtPanel_0.AutoHeight=YtTabbedPane_0.Type=TTabbedPanetTabbedPane_0.X=5tTabbedPane_0.Y=8tTabbedPane_0.Width=1468tTabbedPane_0.Height=1460tTabbedPane_0.AutoWidth=YtTabbedPane_0.AutoHeight=YtTabbedPane_0.Name=tTabbedPane_0.Item=CLP01;CLP02;CLP03;CLP04;CLP05tTabbedPane_0.Visible=YtTabbedPane_0.ChangedAction=onChangeCLP05.Type=TPanelCLP05.X=236CLP05.Y=12CLP05.Width=81CLP05.Height=81CLP05.Name=护理计划CLP05.Item=tLabel_21;tLabel_22;CLP_PACK03;tLabel_27;VERSION03;CLNCPATH_CODE04;SCHD_CODE03CLP05.FocusList=CLNCPATH_CODE04;SCHD_CODE03;VERSION03SCHD_CODE03.Type=临床路径时程下拉区域SCHD_CODE03.X=365SCHD_CODE03.Y=8SCHD_CODE03.Width=200SCHD_CODE03.Height=20SCHD_CODE03.Text=SCHD_CODE03.HorizontalAlignment=2SCHD_CODE03.PopupMenuHeader=代码,100;名称,100SCHD_CODE03.PopupMenuWidth=300SCHD_CODE03.PopupMenuHeight=300SCHD_CODE03.FormatType=comboSCHD_CODE03.ShowDownButton=YSCHD_CODE03.Tip=临床路径时程SCHD_CODE03.ShowColumnList=NAMECLNCPATH_CODE04.Type=TTextFormatCLNCPATH_CODE04.X=72CLNCPATH_CODE04.Y=9CLNCPATH_CODE04.Width=200CLNCPATH_CODE04.Height=20CLNCPATH_CODE04.Text=CLNCPATH_CODE04.showDownButton=YCLNCPATH_CODE04.HorizontalAlignment=2CLNCPATH_CODE04.PopupMenuHeight=200CLNCPATH_CODE04.PopupMenuHeader=路径代码,60;路径名称,135CLNCPATH_CODE04.PopupMenuSQL=SELECT CLNCPATH_CODE AS ID, CLNCPATH_CHN_DESC AS NAME FROM CLP_BSCINFO ORDER BY SEQCLNCPATH_CODE04.ShowColumnList=NAMECLNCPATH_CODE04.PopupMenuFilter=ID;NAMECLNCPATH_CODE04.ValueColumn=IDCLNCPATH_CODE04.FormatType=comboVERSION03.Type=TTextFieldVERSION03.X=633VERSION03.Y=8VERSION03.Width=77VERSION03.Height=20VERSION03.Text=tLabel_27.Type=TLabeltLabel_27.X=599tLabel_27.Y=8tLabel_27.Width=30tLabel_27.Height=20tLabel_27.Text=版本CLP_PACK03.Type=TTableCLP_PACK03.X=3CLP_PACK03.Y=39CLP_PACK03.Width=1455CLP_PACK03.Height=1387CLP_PACK03.SpacingRow=1CLP_PACK03.RowHeight=20CLP_PACK03.AutoWidth=YCLP_PACK03.AutoHeight=YCLP_PACK03.Header=选,25,Boolean;护嘱类别,60,ORDER_TYPE;临床路径项目,90;护嘱名称,250;用量,50,Double;单位,75,DOSE_UNIT;频次,75,FREQ_CODE;医嘱备注,200;执行人员,75,CHKUSER_CODE;核查类别,75,CHKTYPE_CODE;必执,30,Boolean;变异标准差(%),100;版本,50CLP_PACK03.ParmMap=SEL_FLG;ORDER_TYPE;TYPE_CHN_DESC;ORDER_CHN_DESC;DOSE;DOSE_UNIT;FREQ_CODE;NOTE;CHKUSER_CODE;CHKTYPE_CODE;EXEC_FLG;STANDARD;VERSION;ORDER_SEQ_NO;SEQ;ORDER_CODE;ORDTYPE_CODE;ROUT_CODE;DOSE_DAYS;RBORDER_DEPT_CODE;URGENT_FLGCLP_PACK03.ColumnHorizontalAlignmentData=1,left;2,left;3,left;4,right;5,left;6,left;7,left;8,left;9,left;11,right;12,rightCLP_PACK03.LockColumns=allCLP_PACK03.Item=ORDER_TYPE;DOSE_UNIT;FREQ_CODE;CHKTYPE_CODE;CHKUSER_CODECLP_PACK03.HorizontalAlignmentData=tLabel_22.Type=TLabeltLabel_22.X=304tLabel_22.Y=9tLabel_22.Width=60tLabel_22.Height=20tLabel_22.Text=时程代码tLabel_21.Type=TLabeltLabel_21.X=10tLabel_21.Y=9tLabel_21.Width=60tLabel_21.Height=20tLabel_21.Text=路径项目CLP04.Type=TPanelCLP04.X=234CLP04.Y=7CLP04.Width=81CLP04.Height=81CLP04.Name=关键诊疗套餐CLP04.Item=tLabel_19;tLabel_20;CLP_PACK02;tLabel_26;VERSION02;CLNCPATH_CODE03;SCHD_CODE02CLP04.FocusList=CLNCPATH_CODE03;SCHD_CODE02;VERSION02SCHD_CODE02.Type=临床路径时程下拉区域SCHD_CODE02.X=369SCHD_CODE02.Y=9SCHD_CODE02.Width=200SCHD_CODE02.Height=20SCHD_CODE02.Text=SCHD_CODE02.HorizontalAlignment=2SCHD_CODE02.PopupMenuHeader=代码,100;名称,100SCHD_CODE02.PopupMenuWidth=300SCHD_CODE02.PopupMenuHeight=300SCHD_CODE02.FormatType=comboSCHD_CODE02.ShowDownButton=YSCHD_CODE02.Tip=临床路径时程SCHD_CODE02.ShowColumnList=NAMECLNCPATH_CODE03.Type=TTextFormatCLNCPATH_CODE03.X=71CLNCPATH_CODE03.Y=11CLNCPATH_CODE03.Width=200CLNCPATH_CODE03.Height=20CLNCPATH_CODE03.Text=CLNCPATH_CODE03.showDownButton=YCLNCPATH_CODE03.HorizontalAlignment=2CLNCPATH_CODE03.PopupMenuHeight=200CLNCPATH_CODE03.PopupMenuHeader=路径代码,60;路径名称,135CLNCPATH_CODE03.PopupMenuSQL=SELECT CLNCPATH_CODE AS ID, CLNCPATH_CHN_DESC AS NAME FROM CLP_BSCINFO ORDER BY SEQCLNCPATH_CODE03.PopupMenuFilter=ID;NAMECLNCPATH_CODE03.ShowColumnList=NAMECLNCPATH_CODE03.ValueColumn=IDCLNCPATH_CODE03.FormatType=comboVERSION02.Type=TTextFieldVERSION02.X=631VERSION02.Y=11VERSION02.Width=77VERSION02.Height=20VERSION02.Text=tLabel_26.Type=TLabeltLabel_26.X=598tLabel_26.Y=11tLabel_26.Width=30tLabel_26.Height=20tLabel_26.Text=版本CLP_PACK02.Type=TTableCLP_PACK02.X=4CLP_PACK02.Y=40CLP_PACK02.Width=1454CLP_PACK02.Height=1386CLP_PACK02.SpacingRow=1CLP_PACK02.RowHeight=20CLP_PACK02.AutoWidth=YCLP_PACK02.AutoHeight=YCLP_PACK02.Header=选,25,Boolean;医嘱类别,60,ORDER_TYPE;临床路径项目,90;医嘱名称,250;用量,50,Double;单位,75,DOSE_UNIT;频次,75,FREQ_CODE;执行人员,75,CHKUSER_CODE;核查类别,130,CHKTYPE_CODE;必执,30,Boolean;变异标准差(%),100;版本,50;顺序号,60CLP_PACK02.ParmMap=SEL_FLG;ORDER_TYPE;TYPE_CHN_DESC;ORDER_CHN_DESC;DOSE;DOSE_UNIT;FREQ_CODE;CHKUSER_CODE;CHKTYPE_CODE;EXEC_FLG;STANDARD;VERSION;ORDER_SEQ_NO;SEQ;ORDER_CODE;ORDTYPE_CODE;ROUT_CODE;DOSE_DAYS;RBORDER_DEPT_CODE;URGENT_FLG;NOTECLP_PACK02.LockColumns=allCLP_PACK02.ColumnHorizontalAlignmentData=1,left;2,left;3,left;4,right;5,left;6,left;7,left;8,left;10,right;11,right;12,rightCLP_PACK02.Item=ORDER_TYPE;DOSE_UNIT;FREQ_CODE;CHKTYPE_CODE;CHKUSER_CODECLP_PACK02.HorizontalAlignmentData=CLP_PACK02.FocusIndexList=0,1,2,3,4,5,6,7,8,9,10,11,12tLabel_20.Type=TLabeltLabel_20.X=301tLabel_20.Y=11tLabel_20.Width=60tLabel_20.Height=20tLabel_20.Text=时程代码tLabel_19.Type=TLabeltLabel_19.X=10tLabel_19.Y=11tLabel_19.Width=60tLabel_19.Height=20tLabel_19.Text=路径项目CLP03.Type=TPanelCLP03.X=139CLP03.Y=19CLP03.Width=81CLP03.Height=81CLP03.Name=医嘱套餐CLP03.Item=tLabel_16;tLabel_17;CLP_PACK01;tLabel_18;FEES;COUNT;tLabel_25;VERSION01;CLNCPATH_CODE02;SCHD_CODE01;CHKUSER_CODE;ACTIVE;tLabel_5;CLP_PACKNEW;tLabel_9;PACK_CODE;CHK_PACK_FLGCLP03.FocusList=CLNCPATH_CODE02;SCHD_CODE01;VERSION01;FEESCHK_PACK_FLG.Type=TCheckBoxCHK_PACK_FLG.X=711CHK_PACK_FLG.Y=10CHK_PACK_FLG.Width=81CHK_PACK_FLG.Height=23CHK_PACK_FLG.Text=套餐更新CHK_PACK_FLG.Action=onSelPackPACK_CODE.Type=临床路径套餐字典PACK_CODE.X=572PACK_CODE.Y=10PACK_CODE.Width=136PACK_CODE.Height=23PACK_CODE.Text=PACK_CODE.HorizontalAlignment=2PACK_CODE.PopupMenuHeader=代码,100;名称,100PACK_CODE.PopupMenuWidth=300PACK_CODE.PopupMenuHeight=300PACK_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1PACK_CODE.FormatType=comboPACK_CODE.ShowDownButton=YPACK_CODE.Tip=临床路径套餐字典PACK_CODE.ShowColumnList=NAMEPACK_CODE.HisOneNullRow=YPACK_CODE.sqlFlg=YtLabel_9.Type=TLabeltLabel_9.X=510tLabel_9.Y=14tLabel_9.Width=63tLabel_9.Height=15tLabel_9.Text=套餐名称:CLP_PACKNEW.Type=TTableCLP_PACKNEW.X=42CLP_PACKNEW.Y=49CLP_PACKNEW.Width=81CLP_PACKNEW.Height=81CLP_PACKNEW.SpacingRow=1CLP_PACKNEW.RowHeight=20CLP_PACKNEW.Header=选,25,Boolean;时程,80,SCHD_CODE;序号,50;套餐名称,120,PACK_CODE;医嘱类别,60,ORDER_TYPE;临床路径项目,90;医嘱名称,250;启,25,Boolean;用量,50,Double,#######0.000;单位,75,DOSE_UNIT;频次,75,FREQ_CODE;途径,75,ROUT_CODE;第几天,50;日份,50;总量,100,Double;单价,100,Double,#########0.0000;总金额,100,Double;医嘱备注,200;执行人员,75,CHKUSER_CODE;执行科室,75,DEPT_CODE_CLP;核查类别,75,CHKTYPE_CODE;急作,30,Boolean;必执,30,Boolean;变异标准差(%),100;版本,50;顺序号,40;医嘱代码,80;类型代码,80;收费类型,60;用量单位,70;配药单位,70CLP_PACKNEW.LockColumns=allCLP_PACKNEW.FocusIndexList=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23CLP_PACKNEW.ColumnHorizontalAlignmentData=2,left;3,left;4,left;6,right;7,left;8,left;9,left;10,right;11,right;12,right;13,right;14,right;15,left;16,left;17,left;18,left;21,right;22,right;23,rightCLP_PACKNEW.Item=ORDER_TYPE;DOSE_UNIT;FREQ_CODE;ROUT_CODE;DEPT_CODE_CLP;CHKTYPE_CODE;CHKUSER_CODE;SCHD_CODE;PACK_CODECLP_PACKNEW.ParmMap=SEL_FLG;SCHD_CODE;SEQ;PACK_CODE;ORDER_TYPE;TYPE_CHN_DESC;ORDER_CHN_DESC;ACTIVE_FLG;DOSE;DOSE_UNIT;FREQ_CODE;ROUT_CODE;START_DAY;DOSE_DAYS;TOTAL;OWN_PRICE;FEES;NOTE;CHKUSER_CODE;RBORDER_DEPT_CODE;CHKTYPE_CODE;URGENT_FLG;EXEC_FLG;STANDARD;VERSION;ORDER_SEQ_NO;ORDER_CODE;ORDTYPE_CODE;CAT1_TYPE;MEDI_UNIT;DISPENSE_UNITCLP_PACKNEW.Visible=NtLabel_5.Type=TLabeltLabel_5.X=802tLabel_5.Y=14tLabel_5.Width=86tLabel_5.Height=15tLabel_5.Text=路径启用状态tLabel_5.Color=蓝ACTIVE.Type=TComboBoxACTIVE.X=888ACTIVE.Y=11ACTIVE.Width=81ACTIVE.Height=20ACTIVE.Text=TButtonACTIVE.showID=YACTIVE.Editable=YACTIVE.StringData=[[id,name],[ALL,全部],[Y,启用],[N,已停用]]ACTIVE.TableShowList=nameACTIVE.ShowName=YACTIVE.ShowText=NACTIVE.ShowValue=YACTIVE.ShowPy1=YACTIVE.ShowPy2=YACTIVE.Color=蓝ACTIVE.Action=onQueryACTIVE.Enabled=YACTIVE.SelectedAction=CHKUSER_CODE.Type=临床路径执行人员下拉区域CHKUSER_CODE.X=296CHKUSER_CODE.Y=50CHKUSER_CODE.Width=81CHKUSER_CODE.Height=23CHKUSER_CODE.Text=CHKUSER_CODE.HorizontalAlignment=2CHKUSER_CODE.PopupMenuHeader=代码,100;名称,100CHKUSER_CODE.PopupMenuWidth=300CHKUSER_CODE.PopupMenuHeight=300CHKUSER_CODE.FormatType=comboCHKUSER_CODE.ShowDownButton=YCHKUSER_CODE.Tip=临床路径执行人员CHKUSER_CODE.ShowColumnList=NAMECHKUSER_CODE.ChkFlg=SCHD_CODE01.Type=临床路径时程下拉区域SCHD_CODE01.X=314SCHD_CODE01.Y=11SCHD_CODE01.Width=96SCHD_CODE01.Height=20SCHD_CODE01.Text=SCHD_CODE01.HorizontalAlignment=2SCHD_CODE01.PopupMenuHeader=代码,100;名称,100SCHD_CODE01.PopupMenuWidth=300SCHD_CODE01.PopupMenuHeight=300SCHD_CODE01.FormatType=comboSCHD_CODE01.ShowDownButton=YSCHD_CODE01.Tip=临床路径时程SCHD_CODE01.ShowColumnList=NAMECLNCPATH_CODE02.Type=TTextFormatCLNCPATH_CODE02.X=73CLNCPATH_CODE02.Y=11CLNCPATH_CODE02.Width=171CLNCPATH_CODE02.Height=20CLNCPATH_CODE02.Text=CLNCPATH_CODE02.showDownButton=YCLNCPATH_CODE02.HorizontalAlignment=2CLNCPATH_CODE02.PopupMenuHeight=200CLNCPATH_CODE02.PopupMenuHeader=路径代码,60;路径名称,135CLNCPATH_CODE02.PopupMenuSQL=SELECT CLNCPATH_CODE AS ID, CLNCPATH_CHN_DESC AS NAME FROM CLP_BSCINFO ORDER BY SEQCLNCPATH_CODE02.PopupMenuFilter=ID;NAMECLNCPATH_CODE02.ShowColumnList=NAMECLNCPATH_CODE02.ValueColumn=IDCLNCPATH_CODE02.FormatType=comboVERSION01.Type=TTextFieldVERSION01.X=443VERSION01.Y=11VERSION01.Width=62VERSION01.Height=20VERSION01.Text=tLabel_25.Type=TLabeltLabel_25.X=414tLabel_25.Y=11tLabel_25.Width=30tLabel_25.Height=20tLabel_25.Text=版本COUNT.Type=TButtonCOUNT.X=1107COUNT.Y=11COUNT.Width=90COUNT.Height=20COUNT.Text=计算总价COUNT.Action=onCountFeesFEES.Type=TTextFieldFEES.X=1013FEES.Y=11FEES.Width=77FEES.Height=20FEES.Text=tLabel_18.Type=TLabeltLabel_18.X=980tLabel_18.Y=11tLabel_18.Width=30tLabel_18.Height=20tLabel_18.Text=总价CLP_PACK01.Type=TTableCLP_PACK01.X=3CLP_PACK01.Y=40CLP_PACK01.Width=1455CLP_PACK01.Height=1386CLP_PACK01.SpacingRow=1CLP_PACK01.RowHeight=20CLP_PACK01.AutoWidth=YCLP_PACK01.AutoHeight=YCLP_PACK01.Header=选,25,Boolean;顺序号,50;套餐名称,150,PACK_CODE;医嘱类别,80,ORDER_TYPE;临床路径项目,90;医嘱名称,250;启,25,Boolean;用量,50,Double,#######0.000;单位,75,DOSE_UNIT;频次,75,FREQ_CODE;途径,75,ROUT_CODE;第几天,50;日份,50;总量,100,Double;标准单价,100,Double,#########0.0000;标准总金额,100,Double;当前单价,100,Double,#########0.0000;当前总金额,100,Double,#########0.0000;医嘱备注,200;执行人员,75,CHKUSER_CODE;执行科室,75,DEPT_CODE_CLP;核查类别,75,CHKTYPE_CODE;急作,30,Boolean;必执,30,Boolean;变异标准差(%),100;版本,50;序号,40;医嘱代码,80;类型代码,80;收费类型,60;用量单位,70;配药单位,70CLP_PACK01.LockColumns=allCLP_PACK01.ParmMap=SEL_FLG;SEQ;PACK_CODE;ORDER_TYPE;TYPE_CHN_DESC;ORDER_CHN_DESC;ACTIVE_FLG;DOSE;DOSE_UNIT;FREQ_CODE;ROUT_CODE;START_DAY;DOSE_DAYS;TOTAL;OWN_PRICE;FEES;OWN_PRICE_REAL;FEES_REAL;NOTE;CHKUSER_CODE;RBORDER_DEPT_CODE;CHKTYPE_CODE;URGENT_FLG;EXEC_FLG;STANDARD;VERSION;SEQ;ORDER_CODE;ORDTYPE_CODE;CAT1_TYPE;MEDI_UNIT;DISPENSE_UNIT;ORDER_SEQ_NOCLP_PACK01.ColumnHorizontalAlignmentData=2,left;3,left;4,left;5,left;7,right;8,left;9,left;10,left;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,left;19,left;20,left;21,left;24,right;25,rightCLP_PACK01.Item=ORDER_TYPE;DOSE_UNIT;FREQ_CODE;ROUT_CODE;DEPT_CODE_CLP;CHKTYPE_CODE;CHKUSER_CODE;PACK_CODECLP_PACK01.ChangeValueAction=CLP_PACK01.ChangeAction=CLP_PACK01.ControlClassName=CLP_PACK01.HorizontalAlignmentData=CLP_PACK01.FocusIndexList=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23CLP_PACK01.RightClickedAction=showPopMenuCLP_PACK01.ClickedAction=onTableClickedCLP_PACK01.Visible=YtLabel_17.Type=TLabeltLabel_17.X=254tLabel_17.Y=11tLabel_17.Width=60tLabel_17.Height=20tLabel_17.Text=时程代码tLabel_16.Type=TLabeltLabel_16.X=10tLabel_16.Y=11tLabel_16.Width=60tLabel_16.Height=20tLabel_16.Text=路径项目CLP02.Type=TPanelCLP02.X=260CLP02.Y=12CLP02.Width=81CLP02.Height=81CLP02.Name=治疗时程CLP02.Item=tLabel_12;tLabel_13;tLabel_14;tLabel_15;SCHD_DAY;SUSTAINED_DAYS;CLP_THRPYSCHDM;SCHD_CODE_delete;CLNCPATH_CODE01;SCHD_CODE;tLabel_8;SCHD_AMTCLP02.FocusList=SCHD_CODE;SCHD_DAY;SUSTAINED_DAYSCLP02.MoveType=0SCHD_AMT.Type=TNumberTextFieldSCHD_AMT.X=1004SCHD_AMT.Y=12SCHD_AMT.Width=109SCHD_AMT.Height=20SCHD_AMT.Text=0SCHD_AMT.Format=#########0.00tLabel_8.Type=TLabeltLabel_8.X=938tLabel_8.Y=15tLabel_8.Width=72tLabel_8.Height=15tLabel_8.Text=时程金额:SCHD_CODE.Type=临床路径时程下拉区域SCHD_CODE.X=373SCHD_CODE.Y=11SCHD_CODE.Width=200SCHD_CODE.Height=20SCHD_CODE.Text=SCHD_CODE.HorizontalAlignment=2SCHD_CODE.PopupMenuHeader=代码,100;名称,100SCHD_CODE.PopupMenuWidth=300SCHD_CODE.PopupMenuHeight=300SCHD_CODE.FormatType=comboSCHD_CODE.ShowDownButton=YSCHD_CODE.Tip=临床路径时程SCHD_CODE.ShowColumnList=NAMESCHD_CODE.HisOneNullRow=YCLNCPATH_CODE01.Type=TTextFormatCLNCPATH_CODE01.X=76CLNCPATH_CODE01.Y=12CLNCPATH_CODE01.Width=200CLNCPATH_CODE01.Height=20CLNCPATH_CODE01.Text=CLNCPATH_CODE01.showDownButton=YCLNCPATH_CODE01.HorizontalAlignment=2CLNCPATH_CODE01.PopupMenuSQL=SELECT CLNCPATH_CODE AS ID, CLNCPATH_CHN_DESC AS NAME FROM CLP_BSCINFO ORDER BY SEQCLNCPATH_CODE01.PopupMenuFilter=ID;NAMECLNCPATH_CODE01.ShowColumnList=NAMECLNCPATH_CODE01.ValueColumn=IDCLNCPATH_CODE01.FormatType=comboCLNCPATH_CODE01.PopupMenuHeader=路径代码,60;路径名称,135CLNCPATH_CODE01.PopupMenuHeight=200SCHD_CODE_delete.Type=TTextFormatSCHD_CODE_delete.X=369SCHD_CODE_delete.Y=4SCHD_CODE_delete.Width=200SCHD_CODE_delete.Height=20SCHD_CODE_delete.Text=SCHD_CODE_delete.showDownButton=YSCHD_CODE_delete.HorizontalAlignment=2SCHD_CODE_delete.PopupMenuSQL=SELECT DURATION_CODE AS ID, DURATION_CHN_DESC AS NAME FROM CLP_DURATION ORDER BY SEQSCHD_CODE_delete.ShowColumnList=NAMESCHD_CODE_delete.ValueColumn=IDSCHD_CODE_delete.PopupMenuFilter=ID;NAMESCHD_CODE_delete.NextFocus=SCHD_CODE_delete.FocusLostAction=SCHD_CODE_delete.FormatType=comboSCHD_CODE_delete.Format=SCHD_CODE_delete.PopupMenuHeader=时程代码,60;时程名称,135SCHD_CODE_delete.PopupMenuWidth=200SCHD_CODE_delete.PopupMenuHeight=200SCHD_CODE_delete.Action=SCHD_CODE_delete.Enabled=NSCHD_CODE_delete.Visible=NCLP_THRPYSCHDM.Type=TTableCLP_THRPYSCHDM.X=1CLP_THRPYSCHDM.Y=43CLP_THRPYSCHDM.Width=1457CLP_THRPYSCHDM.Height=1383CLP_THRPYSCHDM.SpacingRow=1CLP_THRPYSCHDM.RowHeight=20CLP_THRPYSCHDM.Header=时程项目,200;入院第几天,75;治疗天数,60;时程金额,80;时程代码,80CLP_THRPYSCHDM.AutoHeight=YCLP_THRPYSCHDM.AutoWidth=YCLP_THRPYSCHDM.ParmMap=SCHD_DESC;SUSTAINED_DAYS;SCHD_DAY;SCHD_AMT;SCHD_CODECLP_THRPYSCHDM.LockColumns=allCLP_THRPYSCHDM.ColumnHorizontalAlignmentData=0,left;1,right;2,right;3,rightSUSTAINED_DAYS.Type=TTextFieldSUSTAINED_DAYS.X=838SUSTAINED_DAYS.Y=12SUSTAINED_DAYS.Width=77SUSTAINED_DAYS.Height=20SUSTAINED_DAYS.Text=SCHD_DAY.Type=TTextFieldSCHD_DAY.X=664SCHD_DAY.Y=12SCHD_DAY.Width=77SCHD_DAY.Height=20SCHD_DAY.Text=tLabel_15.Type=TLabeltLabel_15.X=760tLabel_15.Y=12tLabel_15.Width=75tLabel_15.Height=20tLabel_15.Text=入院第几天tLabel_14.Type=TLabeltLabel_14.X=601tLabel_14.Y=12tLabel_14.Width=60tLabel_14.Height=20tLabel_14.Text=治疗天数tLabel_13.Type=TLabeltLabel_13.X=304tLabel_13.Y=12tLabel_13.Width=60tLabel_13.Height=20tLabel_13.Text=时程项目tLabel_12.Type=TLabeltLabel_12.X=12tLabel_12.Y=12tLabel_12.Width=60tLabel_12.Height=20tLabel_12.Text=路径项目CLP01.Type=TPanelCLP01.X=255CLP01.Y=108CLP01.Width=81CLP01.Height=81CLP01.Name=临床路径类别CLP01.Item=tLabel_01;CLNCPATH_CODE;tLabel_02;CLNCPATH_CHN_DESC;tLabel_03;PY1;tLabel_04;DEPT_CODE;tLabel_05;FRSTVRSN_DATE;tLabel_06;LASTVRSN_DATE;tLabel_07;MODIFY_TIMES;tLabel_08;tLabel_09;tLabel_10;tLabel_11;ACPT_CODE;EXIT_CODE;STAYHOSP_DAYS;AVERAGECOST;CLP_BSCINFO;REGION_CODE;tLabel_24;VERSION;ACTIVE_FLG;tLabel_0;PY2;tLabel_1;CLNCPATH_ENG_DESC;tLabel_2;DESCRIPTION;FREQ_CODE;tLabel_3;tLabel_4;diagnose_desc;operation_diagnose_desc;diagnose;operation_diagnose;diagnose_desc_end;operation_diagnose_desc_end;diagnose_end;operation_diagnose_end;diagnoseTable;operatorDiagnoseTable;DEPT_CODE_CLP;tLabel_6;CLP_TYPE;tLabel_7;DEPT_TYPE_N;DEPT_TYPE_WCLP01.Visible=NCLP01.FocusList=CLNCPATH_CODE;CLNCPATH_CHN_DESC;CLNCPATH_ENG_DESC;PY1;PY2;DEPT_CODE;FRSTVRSN_DATE;LASTVRSN_DATE;MODIFY_TIMES;VERSION;ACTIVE_FLG;ACPT_CODE;EXIT_CODE;STAYHOSP_DAYS;AVERAGECOST;DESCRIPTIONDEPT_TYPE_W.Type=TRadioButtonDEPT_TYPE_W.X=939DEPT_TYPE_W.Y=94DEPT_TYPE_W.Width=53DEPT_TYPE_W.Height=23DEPT_TYPE_W.Text=外科DEPT_TYPE_W.Group=grpDEPT_TYPE_N.Type=TRadioButtonDEPT_TYPE_N.X=881DEPT_TYPE_N.Y=94DEPT_TYPE_N.Width=56DEPT_TYPE_N.Height=23DEPT_TYPE_N.Text=内科DEPT_TYPE_N.Group=grpDEPT_TYPE_N.Selected=YtLabel_7.Type=TLabeltLabel_7.X=812tLabel_7.Y=98tLabel_7.Width=57tLabel_7.Height=15tLabel_7.Text=科室类别CLP_TYPE.Type=TComboBoxCLP_TYPE.X=880CLP_TYPE.Y=62CLP_TYPE.Width=92CLP_TYPE.Height=23CLP_TYPE.Text=TButtonCLP_TYPE.showID=YCLP_TYPE.Editable=YCLP_TYPE.CanEdit=YCLP_TYPE.StringData=[[id,text],[,],[1,疾病治疗],[2,手术治疗]]CLP_TYPE.TableShowList=texttLabel_6.Type=TLabeltLabel_6.X=812tLabel_6.Y=67tLabel_6.Width=62tLabel_6.Height=15tLabel_6.Text=路径类别DEPT_CODE_CLP.Type=科室DEPT_CODE_CLP.X=415DEPT_CODE_CLP.Y=161DEPT_CODE_CLP.Width=81DEPT_CODE_CLP.Height=23DEPT_CODE_CLP.Text=DEPT_CODE_CLP.HorizontalAlignment=2DEPT_CODE_CLP.PopupMenuHeader=代码,100;名称,100DEPT_CODE_CLP.PopupMenuWidth=300DEPT_CODE_CLP.PopupMenuHeight=300DEPT_CODE_CLP.FormatType=comboDEPT_CODE_CLP.ShowDownButton=YDEPT_CODE_CLP.Tip=科室DEPT_CODE_CLP.ShowColumnList=NAMEDEPT_CODE_CLP.HisOneNullRow=YDEPT_CODE_CLP.IpdFitFlg=YDEPT_CODE_CLP.OpdFitFlg=DEPT_CODE_CLP.ActiveFlg=DEPT_CODE_CLP.EmgFitFlg=DEPT_CODE_CLP.HrmFigFlg=DEPT_CODE_CLP.StatisticsFlg=DEPT_CODE_CLP.FinalFlg=operatorDiagnoseTable.Type=TTableoperatorDiagnoseTable.X=518operatorDiagnoseTable.Y=59operatorDiagnoseTable.Width=263operatorDiagnoseTable.Height=89operatorDiagnoseTable.SpacingRow=1operatorDiagnoseTable.RowHeight=20operatorDiagnoseTable.Header=开始诊断,130;结束诊断,130operatorDiagnoseTable.ParmMap=operator_diagnose_desc_begin;operator_diagnose_desc_endoperatorDiagnoseTable.ColumnHorizontalAlignmentData=0,left;1,leftdiagnoseTable.Type=TTablediagnoseTable.X=100diagnoseTable.Y=58diagnoseTable.Width=263diagnoseTable.Height=90diagnoseTable.SpacingRow=1diagnoseTable.RowHeight=20diagnoseTable.Header=开始诊断,130;结束诊断,130diagnoseTable.ParmMap=diagnose_desc_begin;diagnose_desc_enddiagnoseTable.ColumnHorizontalAlignmentData=0,left;1,leftoperation_diagnose_end.Type=TTextFieldoperation_diagnose_end.X=488operation_diagnose_end.Y=35operation_diagnose_end.Width=77operation_diagnose_end.Height=20operation_diagnose_end.Text=TTextFieldoperation_diagnose_end.Visible=Ndiagnose_end.Type=TTextFielddiagnose_end.X=598diagnose_end.Y=32diagnose_end.Width=77diagnose_end.Height=20diagnose_end.Text=TTextFielddiagnose_end.Visible=Noperation_diagnose_desc_end.Type=TTextFieldoperation_diagnose_desc_end.X=227operation_diagnose_desc_end.Y=80operation_diagnose_desc_end.Width=125operation_diagnose_desc_end.Height=20operation_diagnose_desc_end.Text=operation_diagnose_desc_end.Visible=Ndiagnose_desc_end.Type=TTextFielddiagnose_desc_end.X=227diagnose_desc_end.Y=58diagnose_desc_end.Width=125diagnose_desc_end.Height=20diagnose_desc_end.Text=diagnose_desc_end.Visible=Noperation_diagnose.Type=TTextFieldoperation_diagnose.X=503operation_diagnose.Y=59operation_diagnose.Width=77operation_diagnose.Height=20operation_diagnose.Text=operation_diagnose.Visible=Ndiagnose.Type=TTextFielddiagnose.X=413diagnose.Y=35diagnose.Width=77diagnose.Height=20diagnose.Text=diagnose.Visible=Noperation_diagnose_desc.Type=TTextFieldoperation_diagnose_desc.X=72operation_diagnose_desc.Y=80operation_diagnose_desc.Width=125operation_diagnose_desc.Height=20operation_diagnose_desc.Text=operation_diagnose_desc.DoubleClickedAction=operation_diagnose_desc.Action=operation_diagnose_desc.ClickedAction=operation_diagnose_desc.Visible=Ndiagnose_desc.Type=TTextFielddiagnose_desc.X=72diagnose_desc.Y=57diagnose_desc.Width=125diagnose_desc.Height=20diagnose_desc.Text=diagnose_desc.Action=diagnose_desc.ClickedAction=diagnose_desc.DoubleClickedAction=diagnose_desc.Visible=NtLabel_4.Type=TLabeltLabel_4.X=435tLabel_4.Y=60tLabel_4.Width=72tLabel_4.Height=15tLabel_4.Text=手术ICDtLabel_3.Type=TLabeltLabel_3.X=11tLabel_3.Y=60tLabel_3.Width=72tLabel_3.Height=15tLabel_3.Text=诊断ICDDESCRIPTION.Type=TTextFieldDESCRIPTION.X=487DESCRIPTION.Y=261DESCRIPTION.Width=300DESCRIPTION.Height=20DESCRIPTION.Text=tLabel_2.Type=TLabeltLabel_2.X=451tLabel_2.Y=261tLabel_2.Width=30tLabel_2.Height=20tLabel_2.Text=备注CLNCPATH_ENG_DESC.Type=TTextFieldCLNCPATH_ENG_DESC.X=578CLNCPATH_ENG_DESC.Y=8CLNCPATH_ENG_DESC.Width=200CLNCPATH_ENG_DESC.Height=20CLNCPATH_ENG_DESC.Text=CLNCPATH_ENG_DESC.PyTag=tLabel_1.Type=TLabeltLabel_1.X=518tLabel_1.Y=8tLabel_1.Width=60tLabel_1.Height=20tLabel_1.Text=英文说明PY2.Type=TTextFieldPY2.X=1003PY2.Y=8PY2.Width=75PY2.Height=20PY2.Text=tLabel_0.Type=TLabeltLabel_0.X=955tLabel_0.Y=8tLabel_0.Width=45tLabel_0.Height=20tLabel_0.Text=助记码ORDER_TYPE.Type=TComboBoxORDER_TYPE.X=320ORDER_TYPE.Y=1ORDER_TYPE.Width=80ORDER_TYPE.Height=20ORDER_TYPE.Text=ORDER_TYPE.showID=YORDER_TYPE.Editable=YORDER_TYPE.ShowValue=YORDER_TYPE.StringData= [[ID,TEXT],[ST,临时],[UD,长期],[DS,出院带药]]ORDER_TYPE.TableShowList=TEXTORDER_TYPE.Visible=YROUT_CODE.Type=用法下拉区域ROUT_CODE.X=240ROUT_CODE.Y=1ROUT_CODE.Width=80ROUT_CODE.Height=20ROUT_CODE.Text=ROUT_CODE.HorizontalAlignment=2ROUT_CODE.PopupMenuHeader=代码,100;名称,100ROUT_CODE.PopupMenuWidth=300ROUT_CODE.PopupMenuHeight=300ROUT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1ROUT_CODE.FormatType=comboROUT_CODE.ShowDownButton=YROUT_CODE.Tip=用法ROUT_CODE.ShowColumnList=NAMEROUT_CODE.Visible=YFREQ_CODE.Type=频次FREQ_CODE.X=73FREQ_CODE.Y=13FREQ_CODE.Width=80FREQ_CODE.Height=17FREQ_CODE.Text=FREQ_CODE.HorizontalAlignment=2FREQ_CODE.PopupMenuHeader=代码,100;名称,100FREQ_CODE.PopupMenuWidth=300FREQ_CODE.PopupMenuHeight=300FREQ_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1FREQ_CODE.FormatType=comboFREQ_CODE.ShowDownButton=YFREQ_CODE.Tip=频次FREQ_CODE.ShowColumnList=NAMEFREQ_CODE.Visible=YFREQ_CODE.StatFlg=DOSE_UNIT.Type=计量单位DOSE_UNIT.X=80DOSE_UNIT.Y=1DOSE_UNIT.Width=80DOSE_UNIT.Height=20DOSE_UNIT.Text=DOSE_UNIT.HorizontalAlignment=2DOSE_UNIT.PopupMenuHeader=代码,100;名称,100DOSE_UNIT.PopupMenuWidth=300DOSE_UNIT.PopupMenuHeight=300DOSE_UNIT.PopupMenuFilter=ID,1;NAME,1;PY1,1DOSE_UNIT.FormatType=comboDOSE_UNIT.ShowDownButton=YDOSE_UNIT.Tip=计量单位DOSE_UNIT.ShowColumnList=NAMEDOSE_UNIT.Visible=YORDTYPE_CODE.Type=临床路径项目下拉区域ORDTYPE_CODE.X=10ORDTYPE_CODE.Y=91ORDTYPE_CODE.Width=81ORDTYPE_CODE.Height=23ORDTYPE_CODE.Text=ORDTYPE_CODE.HorizontalAlignment=2ORDTYPE_CODE.PopupMenuHeader=代码,100;名称,100ORDTYPE_CODE.PopupMenuWidth=300ORDTYPE_CODE.PopupMenuHeight=300ORDTYPE_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1ORDTYPE_CODE.FormatType=comboORDTYPE_CODE.ShowDownButton=YORDTYPE_CODE.Tip=临床路径项目ORDTYPE_CODE.ShowColumnList=NAMEORDTYPE_CODE.ValueColumn=IDORDTYPE_CODE.Visible=YORDTYPE_CODE.DynamicDownload=N CHKTYPE_CODE.Type=查核类别下拉区域CHKTYPE_CODE.X=10CHKTYPE_CODE.Y=91CHKTYPE_CODE.Width=81CHKTYPE_CODE.Height=23CHKTYPE_CODE.Text=CHKTYPE_CODE.HorizontalAlignment=2CHKTYPE_CODE.PopupMenuHeader=代码,100;名称,100CHKTYPE_CODE.PopupMenuWidth=300CHKTYPE_CODE.PopupMenuHeight=300CHKTYPE_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1CHKTYPE_CODE.FormatType=comboCHKTYPE_CODE.ShowDownButton=YCHKTYPE_CODE.Tip=查核类别CHKTYPE_CODE.ShowColumnList=NAMECHKTYPE_CODE.ValueColumn=IDCHKTYPE_CODE.Visible=YCHKTYPE_CODE.DynamicDownload=NCHKTYPE_CODE.HisOneNullRow=YACTIVE_FLG.Type=TCheckBoxACTIVE_FLG.X=953ACTIVE_FLG.Y=33ACTIVE_FLG.Width=60ACTIVE_FLG.Height=20ACTIVE_FLG.Text=启用ACTIVE_FLG.Action=onCheckBoxVERSION.Type=TTextFieldVERSION.X=846VERSION.Y=35VERSION.Width=75VERSION.Height=20VERSION.Text=tLabel_24.Type=TLabeltLabel_24.X=812tLabel_24.Y=35tLabel_24.Width=30tLabel_24.Height=20tLabel_24.Text=版本REGION_CODE.Type=TTextFieldREGION_CODE.X=0REGION_CODE.Y=1REGION_CODE.Width=80REGION_CODE.Height=20REGION_CODE.Text=REGION_CODE.Visible=NCLNCPATH_CODE.Type=TTextFieldCLNCPATH_CODE.X=72CLNCPATH_CODE.Y=10CLNCPATH_CODE.Width=125CLNCPATH_CODE.Height=20CLNCPATH_CODE.Text=CLNCPATH_CODE.Action=CLNCPATH_CODE.FocusLostAction=onClncpathCodeEditCLP_BSCINFO.Type=TTableCLP_BSCINFO.X=4CLP_BSCINFO.Y=286CLP_BSCINFO.Width=1454CLP_BSCINFO.Height=1140CLP_BSCINFO.SpacingRow=1CLP_BSCINFO.RowHeight=20CLP_BSCINFO.AutoWidth=YCLP_BSCINFO.AutoHeight=YCLP_BSCINFO.LockColumns=allCLP_BSCINFO.Header=院区代码,100;路径代码,100;中文名称,150;英文名称,150;拼音,100;诊断ICD,150;手术ICD,150;助记码,100;所属科别,125,DEPT_CODE;制订日期,125;最近修订日,125;修订次数,75;版本,75;加入路径条件,200;溢出路径条件,200;标准住院天数,100;平均医疗费用,100;备注,150;启用,50,Boolean;路径类型,60;科室类型,60CLP_BSCINFO.ParmMap=REGION_CODE;CLNCPATH_CODE;CLNCPATH_CHN_DESC;CLNCPATH_ENG_DESC;PY1;ICD_DESC;OPE_DESC;PY2;DEPT_CODE;FRSTVRSN_DATE;LASTVRSN_DATE;MODIFY_TIMES;VERSION;ACPT_CODE;EXIT_CODE;STAYHOSP_DAYS;AVERAGECOST;DESCRIPTION;ACTIVE_FLG;CLP_TYPE;DEPT_TYPECLP_BSCINFO.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,right;10,right;11,left;12,left;13,right;14,right;15,left;16,centerCLP_BSCINFO.HorizontalAlignmentData=CLP_BSCINFO.Item=DEPT_CODECLP_BSCINFO.RightClickedAction=AVERAGECOST.Type=TTextFieldAVERAGECOST.X=321AVERAGECOST.Y=261AVERAGECOST.Width=100AVERAGECOST.Height=20AVERAGECOST.Text=STAYHOSP_DAYS.Type=TTextFieldSTAYHOSP_DAYS.X=100STAYHOSP_DAYS.Y=261STAYHOSP_DAYS.Width=100STAYHOSP_DAYS.Height=20STAYHOSP_DAYS.Text=EXIT_CODE.Type=TTextAreaEXIT_CODE.X=100EXIT_CODE.Y=211EXIT_CODE.Width=975EXIT_CODE.Height=47EXIT_CODE.SpacingRow=1EXIT_CODE.RowHeight=20ACPT_CODE.Type=TTextAreaACPT_CODE.X=100ACPT_CODE.Y=156ACPT_CODE.Width=975ACPT_CODE.Height=49ACPT_CODE.SpacingRow=1ACPT_CODE.RowHeight=20tLabel_11.Type=TLabeltLabel_11.X=230tLabel_11.Y=261tLabel_11.Width=90tLabel_11.Height=20tLabel_11.Text=平均医疗费用tLabel_10.Type=TLabeltLabel_10.X=11tLabel_10.Y=261tLabel_10.Width=90tLabel_10.Height=20tLabel_10.Text=标准住院天数tLabel_09.Type=TLabeltLabel_09.X=10tLabel_09.Y=225tLabel_09.Width=90tLabel_09.Height=20tLabel_09.Text=溢出路径条件tLabel_08.Type=TLabeltLabel_08.X=10tLabel_08.Y=174tLabel_08.Width=90tLabel_08.Height=20tLabel_08.Text=加入路径条件MODIFY_TIMES.Type=TTextFieldMODIFY_TIMES.X=729MODIFY_TIMES.Y=35MODIFY_TIMES.Width=50MODIFY_TIMES.Height=20MODIFY_TIMES.Text=tLabel_07.Type=TLabeltLabel_07.X=668tLabel_07.Y=35tLabel_07.Width=60tLabel_07.Height=20tLabel_07.Text=修订次数LASTVRSN_DATE.Type=TTextFormatLASTVRSN_DATE.X=513LASTVRSN_DATE.Y=35LASTVRSN_DATE.Width=125LASTVRSN_DATE.Height=20LASTVRSN_DATE.Text=TTextFormatLASTVRSN_DATE.FormatType=dateLASTVRSN_DATE.Format=yyyy/MM/ddLASTVRSN_DATE.showDownButton=YLASTVRSN_DATE.HorizontalAlignment=2tLabel_06.Type=TLabeltLabel_06.X=437tLabel_06.Y=35tLabel_06.Width=75tLabel_06.Height=20tLabel_06.Text=最近修订日FRSTVRSN_DATE.Type=TTextFormatFRSTVRSN_DATE.X=287FRSTVRSN_DATE.Y=35FRSTVRSN_DATE.Width=125FRSTVRSN_DATE.Height=20FRSTVRSN_DATE.Text=TTextFormatFRSTVRSN_DATE.FormatType=dateFRSTVRSN_DATE.Format=yyyy/MM/ddFRSTVRSN_DATE.showDownButton=YFRSTVRSN_DATE.HorizontalAlignment=2tLabel_05.Type=TLabeltLabel_05.X=224tLabel_05.Y=35tLabel_05.Width=60tLabel_05.Height=20tLabel_05.Text=制订日期DEPT_CODE.Type=科室DEPT_CODE.X=72DEPT_CODE.Y=34DEPT_CODE.Width=125DEPT_CODE.Height=20DEPT_CODE.Text=DEPT_CODE.HorizontalAlignment=2DEPT_CODE.PopupMenuHeader=代码,100;名称,100DEPT_CODE.PopupMenuWidth=300DEPT_CODE.PopupMenuHeight=300DEPT_CODE.FormatType=comboDEPT_CODE.ShowDownButton=YDEPT_CODE.Tip=科室DEPT_CODE.ShowColumnList=NAMEDEPT_CODE.FinalFlg=YDEPT_CODE.ClassIfy=0DEPT_CODE.IpdFitFlg=YDEPT_CODE.HisOneNullRow=YtLabel_04.Type=TLabeltLabel_04.X=9tLabel_04.Y=34tLabel_04.Width=60tLabel_04.Height=20tLabel_04.Text=所属科别PY1.Type=TTextFieldPY1.X=846PY1.Y=8PY1.Width=75PY1.Height=20PY1.Text=tLabel_03.Type=TLabeltLabel_03.X=812tLabel_03.Y=8tLabel_03.Width=30tLabel_03.Height=20tLabel_03.Text=拼音CLNCPATH_CHN_DESC.Type=TTextFieldCLNCPATH_CHN_DESC.X=289CLNCPATH_CHN_DESC.Y=8CLNCPATH_CHN_DESC.Width=200CLNCPATH_CHN_DESC.Height=20CLNCPATH_CHN_DESC.Text=CLNCPATH_CHN_DESC.PyTag=PY1tLabel_02.Type=TLabeltLabel_02.X=226tLabel_02.Y=8tLabel_02.Width=60tLabel_02.Height=20tLabel_02.Text=中文说明tLabel_01.Type=TLabeltLabel_01.X=10tLabel_01.Y=8tLabel_01.Width=60tLabel_01.Height=20tLabel_01.Text=路径代码tLabel_01.Color=蓝