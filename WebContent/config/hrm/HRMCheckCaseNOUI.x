## TBuilder Config File ## Title:查询病患就诊号## Company:JavaHis## Author:WangM 2010.07.19## version 1.0#<Type=TFrame>UI.Title=选择就诊号UI.MenuConfig=UI.Width=818UI.Height=600UI.toolbar=YUI.controlclassname=com.javahis.ui.hrm.HRMCheckCaseNOUIControlUI.item=TABLE;SEX_CODE;COMPANY_CODE;CONTRACT_CODE;PACKAGE_CODE;tLabel_0;MR_NO;tLabel_1;NAME;tLabel_2;tLabel_3;AGE;tLabel_4;START_DATE;tLabel_5;END_DATE;tButton_0UI.layout=nullUI.AutoSize=3UI.Y=0tButton_0.Type=TButtontButton_0.X=428tButton_0.Y=77tButton_0.Width=111tButton_0.Height=23tButton_0.Text=查询tButton_0.Action=onQueryEND_DATE.Type=TTextFormatEND_DATE.X=271END_DATE.Y=78END_DATE.Width=108END_DATE.Height=20END_DATE.Text=END_DATE.showDownButton=YEND_DATE.FormatType=dateEND_DATE.Format=yyyy/MM/ddtLabel_5.Type=TLabeltLabel_5.X=238tLabel_5.Y=86tLabel_5.Width=72tLabel_5.Height=15tLabel_5.Text=~START_DATE.Type=TTextFormatSTART_DATE.X=111START_DATE.Y=77START_DATE.Width=107START_DATE.Height=20START_DATE.Text=START_DATE.showDownButton=YSTART_DATE.FormatType=dateSTART_DATE.Format=yyyy/MM/ddtLabel_4.Type=TLabeltLabel_4.X=12tLabel_4.Y=81tLabel_4.Width=99tLabel_4.Height=15tLabel_4.Text=就诊起讫日:AGE.Type=TTextFieldAGE.X=645AGE.Y=35AGE.Width=107AGE.Height=20AGE.Text=AGE.Enabled=YAGE.Visible=NtLabel_3.Type=TLabeltLabel_3.X=634tLabel_3.Y=38tLabel_3.Width=72tLabel_3.Height=15tLabel_3.Text=年龄:tLabel_3.Visible=NtLabel_3.Enabled=NtLabel_2.Type=TLabeltLabel_2.X=499tLabel_2.Y=38tLabel_2.Width=72tLabel_2.Height=15tLabel_2.Text=性别:NAME.Type=TTextFieldNAME.X=332NAME.Y=36NAME.Width=105NAME.Height=20NAME.Text=NAME.Enabled=NtLabel_1.Type=TLabeltLabel_1.X=283tLabel_1.Y=39tLabel_1.Width=72tLabel_1.Height=15tLabel_1.Text=姓名:MR_NO.Type=TTextFieldMR_NO.X=76MR_NO.Y=37MR_NO.Width=151MR_NO.Height=20MR_NO.Text=MR_NO.Enabled=NtLabel_0.Type=TLabeltLabel_0.X=11tLabel_0.Y=41tLabel_0.Width=72tLabel_0.Height=15tLabel_0.Text=病案号:PACKAGE_CODE.Type=健康检查套餐下拉区域PACKAGE_CODE.X=569PACKAGE_CODE.Y=222PACKAGE_CODE.Width=114PACKAGE_CODE.Height=23PACKAGE_CODE.Text=PACKAGE_CODE.HorizontalAlignment=2PACKAGE_CODE.PopupMenuHeader=代码,100;名称,100PACKAGE_CODE.PopupMenuWidth=300PACKAGE_CODE.PopupMenuHeight=300PACKAGE_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1PACKAGE_CODE.FormatType=comboPACKAGE_CODE.ShowDownButton=YPACKAGE_CODE.Tip=健康检查套餐PACKAGE_CODE.ShowColumnList=NAMECONTRACT_CODE.Type=健康检查合同下拉区域CONTRACT_CODE.X=452CONTRACT_CODE.Y=222CONTRACT_CODE.Width=115CONTRACT_CODE.Height=23CONTRACT_CODE.Text=CONTRACT_CODE.HorizontalAlignment=2CONTRACT_CODE.PopupMenuHeader=代码,100;名称,100CONTRACT_CODE.PopupMenuWidth=300CONTRACT_CODE.PopupMenuHeight=300CONTRACT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1CONTRACT_CODE.FormatType=comboCONTRACT_CODE.ShowDownButton=YCONTRACT_CODE.Tip=健康检查合同CONTRACT_CODE.ShowColumnList=NAMECOMPANY_CODE.Type=健康检查团体下拉区域COMPANY_CODE.X=330COMPANY_CODE.Y=222COMPANY_CODE.Width=120COMPANY_CODE.Height=23COMPANY_CODE.Text=COMPANY_CODE.HorizontalAlignment=2COMPANY_CODE.PopupMenuHeader=代码,100;名称,100COMPANY_CODE.PopupMenuWidth=300COMPANY_CODE.PopupMenuHeight=300COMPANY_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1COMPANY_CODE.FormatType=comboCOMPANY_CODE.ShowDownButton=YCOMPANY_CODE.Tip=健康检查团体COMPANY_CODE.ShowColumnList=NAMESEX_CODE.Type=性别下拉列表SEX_CODE.X=546SEX_CODE.Y=34SEX_CODE.Width=80SEX_CODE.Height=23SEX_CODE.Text=TButtonSEX_CODE.showID=YSEX_CODE.showName=YSEX_CODE.showText=NSEX_CODE.showValue=NSEX_CODE.showPy1=YSEX_CODE.showPy2=YSEX_CODE.Editable=YSEX_CODE.Tip=性别SEX_CODE.TableShowList=nameSEX_CODE.ModuleParmString=GROUP_ID:SYS_SEXSEX_CODE.ModuleParmTag=SEX_CODE.Enabled=NTABLE.Type=TTableTABLE.X=4TABLE.Y=120TABLE.Width=812TABLE.Height=477TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=NTABLE.AutoY=NTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.AutoSize=3TABLE.Header=就诊序号,120;姓名,120;性别,80,SEX_CODE;公司,120,COMPANY_CODE;合同,120,CONTRACT_CODE;套餐号,120,PACKAGE_CODE;病案号,120TABLE.ParmMap=CASE_NO;PAT_NAME;SEX_CODE;COMPANY_CODE;CONTRACT_CODE;PACKAGE_CODE;MR_NOTABLE.Item=SEX_CODE;COMPANY_CODE;CONTRACT_CODE;PACKAGE_CODETABLE.LockColumns=all