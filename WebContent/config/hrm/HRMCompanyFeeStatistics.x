## TBuilder Config File ## Title:健检团体费用统计## Company:BlueCore## Author:WangLong 2012.06.15## version 1.0#<Type=TFrame>UI.Title=健检团体费用统计UI.MenuConfig=%ROOT%\config\hrm\HRMCompanyFeeStatisticsMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.hrm.HRMCompanyFeeStatisticsControlUI.item=tLabel_0;tLabel_1;TABLE;REPORTED;ALL;COMPANY_CODE;CONTRACT_CODE;UNREPORTUI.layout=nullUI.Y=0UI.X=0UI.ShowMenu=YUI.TopToolBar=YUI.TopMenu=YUNREPORT.Type=TRadioButtonUNREPORT.X=569UNREPORT.Y=26UNREPORT.Width=81UNREPORT.Height=23UNREPORT.Text=未报到UNREPORT.Group=1CONTRACT_CODE.Type=健康检查合同下拉区域CONTRACT_CODE.X=357CONTRACT_CODE.Y=25CONTRACT_CODE.Width=188CONTRACT_CODE.Height=23CONTRACT_CODE.Text=CONTRACT_CODE.HorizontalAlignment=2CONTRACT_CODE.PopupMenuHeader=代码,100;名称,100CONTRACT_CODE.PopupMenuWidth=300CONTRACT_CODE.PopupMenuHeight=300CONTRACT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1CONTRACT_CODE.FormatType=comboCONTRACT_CODE.ShowDownButton=YCONTRACT_CODE.Tip=健康检查合同CONTRACT_CODE.ShowColumnList=NAMECONTRACT_CODE.Action=COMPANY_CODE.Type=健康检查团体下拉区域COMPANY_CODE.X=103COMPANY_CODE.Y=25COMPANY_CODE.Width=162COMPANY_CODE.Height=23COMPANY_CODE.Text=COMPANY_CODE.HorizontalAlignment=2COMPANY_CODE.PopupMenuHeader=代码,100;名称,100COMPANY_CODE.PopupMenuWidth=300COMPANY_CODE.PopupMenuHeight=300COMPANY_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1COMPANY_CODE.FormatType=comboCOMPANY_CODE.ShowDownButton=YCOMPANY_CODE.Tip=健康检查团体COMPANY_CODE.ShowColumnList=NAMECOMPANY_CODE.Action=onCompanyChooseALL.Type=TRadioButtonALL.X=737ALL.Y=26ALL.Width=81ALL.Height=23ALL.Text=全部ALL.Group=1ALL.Selected=YREPORTED.Type=TRadioButtonREPORTED.X=653REPORTED.Y=26REPORTED.Width=81REPORTED.Height=23REPORTED.Text=已报到REPORTED.Group=1TABLE.Type=TTableTABLE.X=5TABLE.Y=69TABLE.Width=1006TABLE.Height=674TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Header=员工编号,60;姓名,60;性别,60;*(保)A，B，O,100;*13碳尿素呼气,100;*CT冠状动脉造影,100;*CT头部平扫,80;*癌胚抗原,80;*丙肝抗体,80;彩色多普勒,80TABLE.LockColumns=allTABLE.Item=COMPANY_CODE;CONTRACT_CODE;PACKAGE_CODE;SEX_CODE;COVER_FLG;BILL_FLG;FINISH_FLGTABLE.ParmMap=SEQ_NO;PAT_NAME;SEX_CODE;U0001;U0002;U0003;U0004;U0005;U0006;U0007TABLE.SQL=TABLE.ColumnHorizontalAlignmentData=1,left;3,right;4,right;5,right;6,right;7,right;8,right;8,rightTABLE.FocusIndexList=TABLE.AutoModifyDataStore=YtLabel_1.Type=TLabeltLabel_1.X=288tLabel_1.Y=25tLabel_1.Width=72tLabel_1.Height=23tLabel_1.Text=合同名称tLabel_1.Color=蓝tLabel_0.Type=TLabeltLabel_0.X=34tLabel_0.Y=25tLabel_0.Width=72tLabel_0.Height=23tLabel_0.Text=团体名称tLabel_0.Color=蓝PACKAGE_CODE.Type=TTextFormatPACKAGE_CODE.X=330PACKAGE_CODE.Y=180PACKAGE_CODE.Width=104PACKAGE_CODE.Height=22PACKAGE_CODE.Text=PACKAGE_CODE.HorizontalAlignment=2PACKAGE_CODE.PopupMenuHeader=代码,100;名称,100PACKAGE_CODE.PopupMenuWidth=400PACKAGE_CODE.PopupMenuHeight=300PACKAGE_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1PACKAGE_CODE.PopupMenuSQL=SELECT PACKAGE_CODE ID,PACKAGE_DESC NAME,PY1 FROM HRM_PACKAGEM WHERE ACTIVE_FLG='Y' UNION SELECT '合计' ID,'合计' NAME,'ZS' PY1 FROM DUAL ORDER BY IDPACKAGE_CODE.FormatType=comboPACKAGE_CODE.ShowDownButton=YPACKAGE_CODE.Tip=健康检查套餐PACKAGE_CODE.ShowColumnList=NAMEPACKAGE_CODE.HisOneNullRow=YPACKAGE_CODE.ShowName=YSEX_CODE.Type=性别下拉列表SEX_CODE.X=50SEX_CODE.Y=20SEX_CODE.Width=80SEX_CODE.Height=23SEX_CODE.Text=TButtonSEX_CODE.showID=YSEX_CODE.showName=YSEX_CODE.showText=NSEX_CODE.showValue=NSEX_CODE.showPy1=NSEX_CODE.showPy2=NSEX_CODE.Editable=YSEX_CODE.Tip=性别SEX_CODE.TableShowList=nameSEX_CODE.ModuleParmString=GROUP_ID:SYS_SEXSEX_CODE.ModuleParmTag=SEX_CODE.Enabled=YCOVER_FLG.Type=TComboBoxCOVER_FLG.X=120COVER_FLG.Y=60COVER_FLG.Width=80COVER_FLG.Height=23COVER_FLG.Text=TButtonCOVER_FLG.showID=YCOVER_FLG.Editable=YCOVER_FLG.StringData=[[id,text],[Y,已报到],[N,未报到]]COVER_FLG.ExpandWidth=40COVER_FLG.TableShowList=textBILL_FLG.Type=TComboBoxBILL_FLG.X=190BILL_FLG.Y=100BILL_FLG.Width=80BILL_FLG.Height=23BILL_FLG.Text=TButtonBILL_FLG.showID=YBILL_FLG.Editable=YBILL_FLG.StringData=[[id,text],[0,未结算],[1,已结算]]BILL_FLG.ExpandWidth=40BILL_FLG.TableShowList=textFINISH_FLG.Type=TComboBoxFINISH_FLG.X=260FINISH_FLG.Y=140FINISH_FLG.Width=80FINISH_FLG.Height=23FINISH_FLG.Text=TButtonFINISH_FLG.showID=YFINISH_FLG.Editable=YFINISH_FLG.StringData=[[id,text],[Y,完成],[N,未完成]]FINISH_FLG.ExpandWidth=40FINISH_FLG.TableShowList=text