## TBuilder Config File ## Title:## Company:JavaHis## Author:pangben 2014.04.23## version 1.0#<Type=TFrame>UI.Title=减免审批UI.MenuConfig=UI.Width=768UI.Height=665UI.toolbar=YUI.controlclassname=com.javahis.ui.bil.BILOPDReduceControlUI.item=tButton_0;tButton_1;tButton_2;tPanel_0;tTabbedPane_1;tLabel_5;tLabel_6;tLabel_7;tLabel_8;REDUCE_AMT;REDUCE_USER;OWN_AMT;tLabel_11;TOT_AMT;tButton_5;REDUCE_NOTEUI.layout=nullUI.FocusList=REDUCE_AMT;REDUCE_NOTE;tButton_5REDUCE_NOTE.Type=套餐销售折扣原因下拉区域REDUCE_NOTE.X=102REDUCE_NOTE.Y=562REDUCE_NOTE.Width=305REDUCE_NOTE.Height=23REDUCE_NOTE.Text=REDUCE_NOTE.HorizontalAlignment=2REDUCE_NOTE.PopupMenuHeader=代码,100;名称,100REDUCE_NOTE.PopupMenuWidth=300REDUCE_NOTE.PopupMenuHeight=300REDUCE_NOTE.PopupMenuFilter=ID,1;NAME,1REDUCE_NOTE.FormatType=comboREDUCE_NOTE.ShowDownButton=YREDUCE_NOTE.Tip=套餐销售折扣原因REDUCE_NOTE.ShowColumnList=NAMEREDUCE_NOTE.HisOneNullRow=YtButton_5.Type=TButtontButton_5.X=220tButton_5.Y=601tButton_5.Width=81tButton_5.Height=23tButton_5.Text=传回tButton_5.Action=onSendTOT_AMT.Type=TNumberTextFieldTOT_AMT.X=475TOT_AMT.Y=518TOT_AMT.Width=103TOT_AMT.Height=39TOT_AMT.Text=0.00TOT_AMT.Format=#########0.00TOT_AMT.Enabled=NtLabel_11.Type=TLabeltLabel_11.X=410tLabel_11.Y=526tLabel_11.Width=72tLabel_11.Height=15tLabel_11.Text=实收金额：OWN_AMT.Type=TNumberTextFieldOWN_AMT.X=103OWN_AMT.Y=516OWN_AMT.Width=127OWN_AMT.Height=39OWN_AMT.Text=0.00OWN_AMT.Format=#########0.00OWN_AMT.Enabled=NREDUCE_USER.Type=人员REDUCE_USER.X=650REDUCE_USER.Y=523REDUCE_USER.Width=101REDUCE_USER.Height=23REDUCE_USER.Text=REDUCE_USER.HorizontalAlignment=2REDUCE_USER.PopupMenuHeader=代码,100;名称,100REDUCE_USER.PopupMenuWidth=300REDUCE_USER.PopupMenuHeight=300REDUCE_USER.PopupMenuFilter=ID,1;NAME,1;PY1,1REDUCE_USER.FormatType=comboREDUCE_USER.ShowDownButton=YREDUCE_USER.Tip=人员REDUCE_USER.ShowColumnList=NAMEREDUCE_USER.HisOneNullRow=YREDUCE_USER.OpdFitFlg=YREDUCE_AMT.Type=TNumberTextFieldREDUCE_AMT.X=296REDUCE_AMT.Y=517REDUCE_AMT.Width=110REDUCE_AMT.Height=39REDUCE_AMT.Text=0.00REDUCE_AMT.Format=#########0.00REDUCE_AMT.Action=onSharetLabel_8.Type=TLabeltLabel_8.X=36tLabel_8.Y=565tLabel_8.Width=72tLabel_8.Height=15tLabel_8.Text=减免原因:tLabel_7.Type=TLabeltLabel_7.X=581tLabel_7.Y=527tLabel_7.Width=66tLabel_7.Height=15tLabel_7.Text=批准人员:tLabel_6.Type=TLabeltLabel_6.X=231tLabel_6.Y=525tLabel_6.Width=72tLabel_6.Height=15tLabel_6.Text=减免总额:tLabel_5.Type=TLabeltLabel_5.X=8tLabel_5.Y=526tLabel_5.Width=101tLabel_5.Height=15tLabel_5.Text=费用发生总额:tTabbedPane_1.Type=TTabbedPanetTabbedPane_1.X=6tTabbedPane_1.Y=92tTabbedPane_1.Width=757tTabbedPane_1.Height=422tTabbedPane_1.Item=tPanel_6;tPanel_8;人员类别_0tTabbedPane_1.AutoWidth=YtTabbedPane_1.ChangedAction=onSelect人员类别_0.Type=人员类别人员类别_0.X=134人员类别_0.Y=22tPanel_8.Type=TPaneltPanel_8.X=97tPanel_8.Y=3tPanel_8.Width=81tPanel_8.Height=81tPanel_8.Name=类别/项目减免tPanel_8.Item=tLabel_10;TYPE;tButton_3;tButton_4;TABLED;TABLEDD;ALLD;ALLDDALLDD.Type=TCheckBoxALLDD.X=8ALLDD.Y=190ALLDD.Width=81ALLDD.Height=23ALLDD.Text=全选ALLDD.Action=onSelAllDDALLD.Type=TCheckBoxALLD.X=6ALLD.Y=5ALLD.Width=63ALLD.Height=23ALLD.Text=全选ALLD.Action=onSelAllDTABLEDD.Type=TTableTABLEDD.X=10TABLEDD.Y=212TABLEDD.Width=737TABLEDD.Height=173TABLEDD.SpacingRow=1TABLEDD.RowHeight=20TABLEDD.Header=选,30,boolean;数据名称,150;规格,80;单位,50,UNIT;单价,60;数量,40;发生金额,100;减免金额,100;实收金额,100TABLEDD.AutoWidth=YTABLEDD.AutoHeight=YTABLEDD.LockColumns=1,2,3,4,5,6,7,8TABLEDD.Item=UNITTABLEDD.ParmMap=FLG;DATA_DESC;SPECIFICATION;UNIT;OWN_PRICE;QTY;AR_AMT;REDUCE_AMT;TOT_AMT;DATA_CODETABLEDD.ColumnHorizontalAlignmentData=1,left;2,left;3,left;4,right;5,right;6,right;7,right;8,rightTABLED.Type=TTableTABLED.X=9TABLED.Y=33TABLED.Width=738TABLED.Height=158TABLED.SpacingRow=1TABLED.RowHeight=20TABLED.Header=选,30,boolean;数据名称,80;规格,100;单位,50;单价,60;数量,40;发生金额,100;减免金额,100;实收金额,100TABLED.AutoWidth=YTABLED.Item=UNITtButton_4.Type=TButtontButton_4.X=306tButton_4.Y=6tButton_4.Width=81tButton_4.Height=23tButton_4.Text=移除tButton_4.Action=onDeletetButton_3.Type=TButtontButton_3.X=216tButton_3.Y=6tButton_3.Width=81tButton_3.Height=23tButton_3.Text=添加tButton_3.Action=onAddTYPE.Type=TComboBoxTYPE.X=117TYPE.Y=8TYPE.Width=81TYPE.Height=23TYPE.Text=TButtonTYPE.showID=YTYPE.Editable=YTYPE.StringData=[[id,text],[1,收据类别],[2,项目明细]]TYPE.Action=TYPE.TableShowList=textTYPE.SelectedAction=onChangetLabel_10.Type=TLabeltLabel_10.X=76tLabel_10.Y=10tLabel_10.Width=47tLabel_10.Height=16tLabel_10.Text=类别：tPanel_6.Type=TPaneltPanel_6.X=31tPanel_6.Y=8tPanel_6.Width=81tPanel_6.Height=81tPanel_6.Item=TABLEMtPanel_6.Name=总额减免TABLEM.Type=TTableTABLEM.X=4TABLEM.Y=5TABLEM.Width=743TABLEM.Height=380TABLEM.SpacingRow=1TABLEM.RowHeight=20TABLEM.Header=收据类别,120,REXP_CODE;发生金额,100;减免金额,100;实收金额,100TABLEM.AutoY=YTABLEM.AutoWidth=YTABLEM.AutoHeight=YTABLEM.ParmMap=REXP_CODE;AR_AMT;REDUCE_AMT;TOT_AMTTABLEM.LockColumns=ALLTABLEM.ColumnHorizontalAlignmentData=0,left;1,right;2,right;3,rightTABLEM.Item=REXP_CODEtPanel_0.Type=TPaneltPanel_0.X=1tPanel_0.Y=0tPanel_0.Width=762tPanel_0.Height=91tPanel_0.Border=组|基本信息tPanel_0.AutoWidth=YtPanel_0.Item=tLabel_0;MR_NO;tLabel_1;PAT_NAME;tLabel_2;SEX_CODE;tLabel_3;ADM_DATE;tLabel_4;tLabel_9;RECEIPT_NO;CASE_NO;UNIT;CTZ1_CODE;REXP_CODEREXP_CODE.Type=收据类别REXP_CODE.X=371REXP_CODE.Y=205REXP_CODE.Width=81REXP_CODE.Height=23REXP_CODE.Text=REXP_CODE.HorizontalAlignment=2REXP_CODE.PopupMenuHeader=代码,100;名称,100REXP_CODE.PopupMenuWidth=300REXP_CODE.PopupMenuHeight=300REXP_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1REXP_CODE.FormatType=comboREXP_CODE.ShowDownButton=YREXP_CODE.Tip=收据类别REXP_CODE.ShowColumnList=NAMEREXP_CODE.HisOneNullRow=YCTZ1_CODE.Type=身份折扣下拉列表CTZ1_CODE.X=264CTZ1_CODE.Y=55CTZ1_CODE.Width=130CTZ1_CODE.Height=23CTZ1_CODE.Text=TButtonCTZ1_CODE.showID=YCTZ1_CODE.showName=YCTZ1_CODE.showText=NCTZ1_CODE.showValue=NCTZ1_CODE.showPy1=YCTZ1_CODE.showPy2=YCTZ1_CODE.Editable=YCTZ1_CODE.Tip=身份CTZ1_CODE.TableShowList=nameCTZ1_CODE.Enabled=NUNIT.Type=计量单位UNIT.X=555UNIT.Y=32UNIT.Width=81UNIT.Height=23UNIT.Text=UNIT.HorizontalAlignment=2UNIT.PopupMenuHeader=代码,100;名称,100UNIT.PopupMenuWidth=300UNIT.PopupMenuHeight=300UNIT.PopupMenuFilter=ID,1;NAME,1;PY1,1UNIT.FormatType=comboUNIT.ShowDownButton=YUNIT.Tip=计量单位UNIT.ShowColumnList=NAMEUNIT.Visible=NCASE_NO.Type=TTextFieldCASE_NO.X=579CASE_NO.Y=36CASE_NO.Width=77CASE_NO.Height=20CASE_NO.Text=CASE_NO.Visible=NRECEIPT_NO.Type=TTextFieldRECEIPT_NO.X=468RECEIPT_NO.Y=56RECEIPT_NO.Width=81RECEIPT_NO.Height=23RECEIPT_NO.Text=RECEIPT_NO.Enabled=NtLabel_9.Type=TLabeltLabel_9.X=424tLabel_9.Y=58tLabel_9.Width=42tLabel_9.Height=16tLabel_9.Text=票号：tLabel_4.Type=TLabeltLabel_4.X=223tLabel_4.Y=61tLabel_4.Width=41tLabel_4.Height=15tLabel_4.Text=身份:ADM_DATE.Type=TTextFormatADM_DATE.X=86ADM_DATE.Y=59ADM_DATE.Width=101ADM_DATE.Height=20ADM_DATE.Text=TTextFormatADM_DATE.Enabled=NADM_DATE.FormatType=dateADM_DATE.showDownButton=YADM_DATE.Format=yyyy/MM/ddtLabel_3.Type=TLabeltLabel_3.X=17tLabel_3.Y=61tLabel_3.Width=72tLabel_3.Height=15tLabel_3.Text=就诊时间:SEX_CODE.Type=性别下拉列表SEX_CODE.X=467SEX_CODE.Y=20SEX_CODE.Width=81SEX_CODE.Height=23SEX_CODE.Text=TButtonSEX_CODE.showID=YSEX_CODE.showName=YSEX_CODE.showText=NSEX_CODE.showValue=NSEX_CODE.showPy1=NSEX_CODE.showPy2=NSEX_CODE.Editable=YSEX_CODE.Tip=性别SEX_CODE.TableShowList=nameSEX_CODE.ModuleParmString=GROUP_ID:SYS_SEXSEX_CODE.ModuleParmTag=SEX_CODE.Enabled=NtLabel_2.Type=TLabeltLabel_2.X=425tLabel_2.Y=26tLabel_2.Width=39tLabel_2.Height=15tLabel_2.Text=性别:PAT_NAME.Type=TTextFieldPAT_NAME.X=263PAT_NAME.Y=24PAT_NAME.Width=132PAT_NAME.Height=20PAT_NAME.Text=PAT_NAME.Enabled=NtLabel_1.Type=TLabeltLabel_1.X=222tLabel_1.Y=27tLabel_1.Width=37tLabel_1.Height=15tLabel_1.Text=姓名:MR_NO.Type=TTextFieldMR_NO.X=87MR_NO.Y=24MR_NO.Width=101MR_NO.Height=20MR_NO.Text=MR_NO.Enabled=NtLabel_0.Type=TLabeltLabel_0.X=31tLabel_0.Y=28tLabel_0.Width=55tLabel_0.Height=15tLabel_0.Text=病案号:tButton_2.Type=TButtontButton_2.X=483tButton_2.Y=600tButton_2.Width=81tButton_2.Height=23tButton_2.Text=取消tButton_2.Action=onCanceltButton_1.Type=TButtontButton_1.X=315tButton_1.Y=598tButton_1.Width=88tButton_1.Height=23tButton_1.Text=减免打印tButton_1.Action=onReducePrinttButton_1.Visible=NtButton_0.Type=TButtontButton_0.X=25tButton_0.Y=594tButton_0.Width=81tButton_0.Height=23tButton_0.Text=打印tButton_0.Action=onPrinttButton_0.Visible=N