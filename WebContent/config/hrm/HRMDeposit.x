## TBuilder Config File ## Title:健检结算## Company:JavaHis## Author:ehui 2009.11.26## version 1.0#<Type=TFrame>UI.Title=健检结算UI.MenuConfig=%ROOT%\config\hrm\HRMDepositMenu.xUI.Width=1280UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.hrm.HRMDepositControlUI.item=tPanel_0UI.layout=nullUI.TopMenu=YUI.TopToolBar=YUI.ShowMenu=YUI.FocusList=MR_NO;COMPANY_CODE;CONTRACT_CODE;ALL_CHOOSE;UNREPORT;REPORT;ALL;DISCNT;tButton_0tPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1270tPanel_0.Height=738tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.AutoHeight=YtPanel_0.Border=组tPanel_0.Item=tLabel_0;COMPANY_CODE;tLabel_1;CONTRACT_CODE;tLabel_3;MR_NO;tLabel_6;PAT_TABLE;ALL_CHOOSE;tLabel_7;ORDER_TABLE;tLabel_8;BILL_TABLE;tLabel_9;BILL_DETAIL_TABLE;tLabel_2;PAT_NAME;DISCNT;tButton_0;COVER_FLG_COMBO;BILL_FLG_COMBO;START_SEQ;tLabel_10;END_SEQ;tButton_1;PAT_COUNT;tLabel_11;tLabel_12;AR_AMTAR_AMT.Type=TNumberTextFieldAR_AMT.X=172AR_AMT.Y=513AR_AMT.Width=144AR_AMT.Height=50AR_AMT.Text=AR_AMT.Format=#########0.00AR_AMT.Color=红AR_AMT.Enabled=YAR_AMT.Editable=NAR_AMT.FontSize=30tLabel_12.Type=TLabeltLabel_12.X=105tLabel_12.Y=530tLabel_12.Width=65tLabel_12.Height=15tLabel_12.Text=应付金额tLabel_11.Type=TLabeltLabel_11.X=418tLabel_11.Y=46tLabel_11.Width=35tLabel_11.Height=15tLabel_11.Text=序号PAT_COUNT.Type=TLabelPAT_COUNT.X=74PAT_COUNT.Y=48PAT_COUNT.Width=50PAT_COUNT.Height=15PAT_COUNT.Text=tButton_1.Type=TButtontButton_1.X=537tButton_1.Y=42tButton_1.Width=60tButton_1.Height=23tButton_1.Text=筛选tButton_1.Action=onCustomizeChooseEND_SEQ.Type=TTextFieldEND_SEQ.X=498END_SEQ.Y=43END_SEQ.Width=32END_SEQ.Height=22END_SEQ.Text=tLabel_10.Type=TLabeltLabel_10.X=482tLabel_10.Y=46tLabel_10.Width=18tLabel_10.Height=15tLabel_10.Text=至START_SEQ.Type=TTextFieldSTART_SEQ.X=448START_SEQ.Y=43START_SEQ.Width=32START_SEQ.Height=22START_SEQ.Text=BILL_FLG_COMBO.Type=TComboBoxBILL_FLG_COMBO.X=314BILL_FLG_COMBO.Y=43BILL_FLG_COMBO.Width=76BILL_FLG_COMBO.Height=22BILL_FLG_COMBO.Text=TButtonBILL_FLG_COMBO.showID=YBILL_FLG_COMBO.Editable=YBILL_FLG_COMBO.Tip=缴费状态BILL_FLG_COMBO.StringData=[[id,text],[ALL,全部],[N,未缴费],[Y,已缴费]]BILL_FLG_COMBO.ExpandWidth=20BILL_FLG_COMBO.TableShowList=textBILL_FLG_COMBO.SelectedAction=onStateChooseCOVER_FLG_COMBO.Type=TComboBoxCOVER_FLG_COMBO.X=204COVER_FLG_COMBO.Y=43COVER_FLG_COMBO.Width=76COVER_FLG_COMBO.Height=22COVER_FLG_COMBO.Text=TButtonCOVER_FLG_COMBO.showID=YCOVER_FLG_COMBO.Editable=YCOVER_FLG_COMBO.Tip=报到状态COVER_FLG_COMBO.StringData=[[id,text],[ALL,全部],[Y,已报到],[N,未报到]]COVER_FLG_COMBO.ExpandWidth=20COVER_FLG_COMBO.TableShowList=textCOVER_FLG_COMBO.SelectedAction=onStateChoosetButton_0.Type=TButtontButton_0.X=698tButton_0.Y=42tButton_0.Width=88tButton_0.Height=23tButton_0.Text=更改折扣tButton_0.Action=onChangeOrderDiscntDISCNT.Type=TNumberTextFieldDISCNT.X=653DISCNT.Y=43DISCNT.Width=40DISCNT.Height=22DISCNT.Text=0DISCNT.Format=#########0.00DISCNT.Action=onSameDiscntPAT_NAME.Type=TTextFieldPAT_NAME.X=760PAT_NAME.Y=13PAT_NAME.Width=109PAT_NAME.Height=22PAT_NAME.Text=PAT_NAME.Enabled=NtLabel_2.Type=TLabeltLabel_2.X=726tLabel_2.Y=17tLabel_2.Width=35tLabel_2.Height=15tLabel_2.Text=姓名BILL_DETAIL_TABLE.Type=TTableBILL_DETAIL_TABLE.X=652BILL_DETAIL_TABLE.Y=348BILL_DETAIL_TABLE.Width=609BILL_DETAIL_TABLE.Height=377BILL_DETAIL_TABLE.SpacingRow=1BILL_DETAIL_TABLE.RowHeight=20BILL_DETAIL_TABLE.AutoWidth=YBILL_DETAIL_TABLE.AutoHeight=YBILL_DETAIL_TABLE.Header=病案号,100;病患名称,100;医嘱名称,300;单价,60,double,############0.00;数量,40;总价,60,double,############0.00BILL_DETAIL_TABLE.ParmMap=MR_NO;PAT_NAME;ORDER_DESC;OWN_PRICE;DISPENSE_QTY;AR_AMTBILL_DETAIL_TABLE.FocusType=2BILL_DETAIL_TABLE.LockColumns=1,2,3,4,5,6BILL_DETAIL_TABLE.ColumnHorizontalAlignmentData=1,left;2,left;3,right;4,right;5,righttLabel_9.Type=TLabeltLabel_9.X=652tLabel_9.Y=326tLabel_9.Width=171tLabel_9.Height=15tLabel_9.Text=账单明细（结算后可见）BILL_TABLE.Type=TTableBILL_TABLE.X=8BILL_TABLE.Y=348BILL_TABLE.Width=630BILL_TABLE.Height=147BILL_TABLE.SpacingRow=1BILL_TABLE.RowHeight=20BILL_TABLE.AutoWidth=NBILL_TABLE.AutoHeight=NBILL_TABLE.Header=结,30,boolean;结算单号,120;账单金额,80,double,############0.00;应付金额,80,double,############0.00;减免金额,70,double,############0.00BILL_TABLE.ParmMap=REXP_FLG;BILL_NO;OWN_AMT;AR_AMT;CUT_AMT BILL_TABLE.LockColumns=0,1,2,3BILL_TABLE.FocusType=2BILL_TABLE.ColumnHorizontalAlignmentData=2,right;3,right;4,right;5,left;6,left;7,right;8,right;9,rightBILL_TABLE.AutoModifyDataStore=YBILL_TABLE.Item=BILL_TABLE.ClickedAction=onBillTableClicktLabel_8.Type=TLabeltLabel_8.X=10tLabel_8.Y=326tLabel_8.Width=62tLabel_8.Height=15tLabel_8.Text=账单列表ORDER_TABLE.Type=TTableORDER_TABLE.X=809ORDER_TABLE.Y=72ORDER_TABLE.Width=450ORDER_TABLE.Height=245ORDER_TABLE.SpacingRow=1ORDER_TABLE.RowHeight=20ORDER_TABLE.AutoHeight=NORDER_TABLE.AutoWidth=YORDER_TABLE.Header=客户姓名,70;医嘱名称,250;单价,60,double,############0.00;数量,35;总价,60,double,############0.00ORDER_TABLE.FocusType=2ORDER_TABLE.LockColumns=allORDER_TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,right;3,right;4,rightORDER_TABLE.ParmMap=PAT_NAME;ORDER_DESC;OWN_PRICE;DISPENSE_QTY;AR_AMTtLabel_7.Type=TLabeltLabel_7.X=813tLabel_7.Y=47tLabel_7.Width=61tLabel_7.Height=15tLabel_7.Text=医嘱详细PAT_TABLE.Type=TTablePAT_TABLE.X=9PAT_TABLE.Y=72PAT_TABLE.Width=786PAT_TABLE.Height=243PAT_TABLE.SpacingRow=1PAT_TABLE.RowHeight=20PAT_TABLE.Header=选,30,boolean;序号,32;病案号,100,int;姓名,100;报到时间,125,Timestamp,yyyy/MM/dd HH:mm;套餐名称,160,PACKAGE_CODE;折扣,36;应收金额,65,double,############0.00;实收金额,65,double,############0.00;结算状态,70,BILL_FLG;报到状态,70,COVER_FLG;部门,100PAT_TABLE.ParmMap=FLG;SEQ_NO;MR_NO;PAT_NAME;REPORT_DATE;PACKAGE_CODE;DISCNT;OWN_AMT;AR_AMT;BILL_FLG;COVER_FLG;PAT_DEPTPAT_TABLE.LockColumns=1,2,3,4,5,7,8,9,10,11PAT_TABLE.Item=PACKAGE_CODE;BILL_FLG;COVER_FLGPAT_TABLE.ClickedAction=showOrderTablePAT_TABLE.ColumnHorizontalAlignmentData=1,right;3,left;5,left;6,right;7,right;8,right;11,leftCOVER_FLG.Type=TComboBoxCOVER_FLG.X=280COVER_FLG.Y=280COVER_FLG.Width=80COVER_FLG.Height=23COVER_FLG.Text=TButtonCOVER_FLG.showID=YCOVER_FLG.Editable=YCOVER_FLG.StringData=[[id,text],[Y,已报到],[N,未报到]]COVER_FLG.ExpandWidth=40COVER_FLG.TableShowList=textBILL_FLG.Type=TComboBoxBILL_FLG.X=390BILL_FLG.Y=100BILL_FLG.Width=80BILL_FLG.Height=23BILL_FLG.Text=TButtonBILL_FLG.showID=YBILL_FLG.Editable=YBILL_FLG.StringData=[[id,text],[0,未结算],[1,已结算],[-1,已缴费]]BILL_FLG.ExpandWidth=40BILL_FLG.TableShowList=textPACKAGE_CODE.Type=健康检查套餐下拉区域PACKAGE_CODE.X=350PACKAGE_CODE.Y=90PACKAGE_CODE.Width=81PACKAGE_CODE.Height=23PACKAGE_CODE.Text=PACKAGE_CODE.HorizontalAlignment=2PACKAGE_CODE.PopupMenuHeader=代码1,100;名称,100,备注,200PACKAGE_CODE.PopupMenuWidth=500PACKAGE_CODE.PopupMenuHeight=300PACKAGE_CODE.PopupMenuFilter=ID,1;NAME,1;DESCRIPTION,1;PY1,1PACKAGE_CODE.FormatType=comboPACKAGE_CODE.ShowDownButton=YPACKAGE_CODE.Tip=健康检查套餐PACKAGE_CODE.ShowColumnList=NAMEALL_CHOOSE.Type=TCheckBoxALL_CHOOSE.X=126ALL_CHOOSE.Y=43ALL_CHOOSE.Width=61ALL_CHOOSE.Height=23ALL_CHOOSE.Text=全选ALL_CHOOSE.Action=onAllPattLabel_6.Type=TLabeltLabel_6.X=10tLabel_6.Y=48tLabel_6.Width=61tLabel_6.Height=15tLabel_6.Text=人员列表MR_NO.Type=TTextFieldMR_NO.X=596MR_NO.Y=13MR_NO.Width=100MR_NO.Height=22MR_NO.Text=MR_NO.Action=onQueryByMrtLabel_3.Type=TLabeltLabel_3.X=550tLabel_3.Y=17tLabel_3.Width=48tLabel_3.Height=15tLabel_3.Text=病案号tLabel_3.Color=蓝CONTRACT_CODE.Type=TTextFormatCONTRACT_CODE.X=349CONTRACT_CODE.Y=13CONTRACT_CODE.Width=169CONTRACT_CODE.Height=23CONTRACT_CODE.Text=CONTRACT_CODE.HorizontalAlignment=2CONTRACT_CODE.PopupMenuHeader=合同代码,100;合同名称,150CONTRACT_CODE.PopupMenuWidth=255CONTRACT_CODE.PopupMenuHeight=140CONTRACT_CODE.PopupMenuFilter=ID,1;PY1,1CONTRACT_CODE.FormatType=comboCONTRACT_CODE.showDownButton=YCOMPANY_CODE.Tip=健康检查合同CONTRACT_CODE.ShowColumnList=NAMECONTRACT_CODE.ValueColumn=IDCONTRACT_CODE.Action=onContractCodeChooseCONTRACT_CODE.HisOneNullRow=YtLabel_1.Type=TLabeltLabel_1.X=291tLabel_1.Y=17tLabel_1.Width=62tLabel_1.Height=15tLabel_1.Text=合同名称tLabel_1.Color=蓝COMPANY_CODE.Type=健康检查团体下拉区域COMPANY_CODE.X=70COMPANY_CODE.Y=13COMPANY_CODE.Width=202COMPANY_CODE.Height=23COMPANY_CODE.Text=COMPANY_CODE.HorizontalAlignment=2COMPANY_CODE.PopupMenuHeader=代码,100;名称,250COMPANY_CODE.PopupMenuWidth=305COMPANY_CODE.PopupMenuHeight=250COMPANY_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1COMPANY_CODE.FormatType=comboCOMPANY_CODE.ShowDownButton=YCOMPANY_CODE.Tip=健康检查团体COMPANY_CODE.ShowColumnList=NAMECOMPANY_CODE.Action=onCompanyCodeChooseCOMPANY_CODE.HisOneNullRow=YtLabel_0.Type=TLabeltLabel_0.X=10tLabel_0.Y=17tLabel_0.Width=60tLabel_0.Height=15tLabel_0.Text=团体名称tLabel_0.Color=蓝OPERATOR.Type=人员OPERATOR.X=238OPERATOR.Y=324OPERATOR.Width=81OPERATOR.Height=23OPERATOR.Text=OPERATOR.HorizontalAlignment=2OPERATOR.PopupMenuHeader=代码,100;名称,100OPERATOR.PopupMenuWidth=300OPERATOR.PopupMenuHeight=300OPERATOR.PopupMenuFilter=ID,1;NAME,1;PY1,1OPERATOR.FormatType=comboOPERATOR.ShowDownButton=YOPERATOR.Tip=人员OPERATOR.ShowColumnList=NAME