## TBuilder Config File ## Title:健检个人报到## Company:JavaHis## Author:ehui 2009.09.21## version 1.0#<Type=TFrame>UI.Title=个人体检报到UI.MenuConfig=%ROOT%\config\hrm\HRMPersonReportMenu.xUI.Width=1280UI.Height=780UI.toolbar=YUI.controlclassname=com.javahis.ui.hrm.HRMPersonReportControlUI.item=tPanel_0UI.layout=nullUI.ShowTitle=NUI.ShowMenu=YUI.TopMenu=YUI.X=5UI.AutoX=YUI.Y=5UI.AutoY=YUI.AutoWidth=YUI.AutoHeight=YUI.FocusList=MR_NO;PAT_NAME;IDNO;SEX_CODE;BIRTH_DATE;POST_CODE;ADDRESS;TEL;DEPT_CODE;REPORT_DATE;SAVE_PATUI.TopToolBar=YtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1270tPanel_0.Height=770tPanel_0.AutoWidth=YtPanel_0.AutoHeight=YtPanel_0.Border=组|tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.Item=tLabel_0;PAT_NAME;tLabel_1;MR_NO;tLabel_2;IDNO;tLabel_3;tLabel_4;BIRTH_DATE;tLabel_5;POST_CODE;tLabel_8;ADDRESS;tLabel_9;TEL;tLabel_10;tLabel_11;tLabel_12;COM_PAY_FLG;LBL_COM;TABLE;tLabel_15;CHARGE;tLabel_16;tLabel_17;REAL;tLabel_18;tLabel_19;CHANGE;SAVE_PAT;FEE;PHOTO_PANEL;tButton_29;REPORT_DATE;tLabel_33;UPDATE_NO;PACKAGE_CODE;COMPANY_CODE;CONTRACT_CODE;OLD_PAT;tLabel_6;FOREIGNER_FLG;tLabel_7;PROVINCE;tLabel_13;CITY;tLabel_14;tLabel_20;INTRO_USER;REPORTLIST;SEX_CODE;DEPT_CODE_COM;tLabel_21;PAY_REMARK;PAY_TYPEPAY_TYPE.Type=支付方式下拉区域(医疗卡)PAY_TYPE.X=76PAY_TYPE.Y=503PAY_TYPE.Width=136PAY_TYPE.Height=23PAY_TYPE.Text=PAY_TYPE.HorizontalAlignment=2PAY_TYPE.PopupMenuHeader=代码,100;名称,100PAY_TYPE.PopupMenuWidth=300PAY_TYPE.PopupMenuHeight=300PAY_TYPE.PopupMenuFilter=ID,1;NAME,1;PY1,1PAY_TYPE.FormatType=comboPAY_TYPE.ShowDownButton=YPAY_TYPE.Tip=支付方式PAY_TYPE.ShowColumnList=NAMEPAY_TYPE.HisOneNullRow=YPAY_REMARK.Type=TTextFieldPAY_REMARK.X=76PAY_REMARK.Y=541PAY_REMARK.Width=178PAY_REMARK.Height=21PAY_REMARK.Text=tLabel_21.Type=TLabeltLabel_21.X=15tLabel_21.Y=541tLabel_21.Width=72tLabel_21.Height=21tLabel_21.Text=支票号：DEPT_CODE_COM.Type=科室DEPT_CODE_COM.X=817DEPT_CODE_COM.Y=58DEPT_CODE_COM.Width=102DEPT_CODE_COM.Height=22DEPT_CODE_COM.Text=DEPT_CODE_COM.HorizontalAlignment=2DEPT_CODE_COM.PopupMenuHeader=代码,100;名称,100DEPT_CODE_COM.PopupMenuWidth=300DEPT_CODE_COM.PopupMenuHeight=300DEPT_CODE_COM.FormatType=comboDEPT_CODE_COM.ShowDownButton=YDEPT_CODE_COM.Tip=科室DEPT_CODE_COM.ShowColumnList=NAMEDEPT_CODE_COM.HisOneNullRow=YDEPT_CODE_COM.HrmFigFlg=YDEPT_CODE_COM.ClassIfy=0DEPT_CODE_COM.FinalFlg=YDEPT_CODE_COM.Action=INTRO_USER|onQuery#;DEPTDEPT.Type=成本中心下拉区域DEPT.X=928DEPT.Y=11DEPT.Width=81DEPT.Height=23DEPT.Text=DEPT.HorizontalAlignment=2DEPT.PopupMenuHeader=代码,100;名称,100DEPT.PopupMenuWidth=300DEPT.PopupMenuHeight=300DEPT.FormatType=comboDEPT.ShowDownButton=YDEPT.Tip=成本中心DEPT.ShowColumnList=NAMEDEPT.HisOneNullRow=YSEX_CODE.Type=性别下拉列表SEX_CODE.X=675SEX_CODE.Y=11SEX_CODE.Width=54SEX_CODE.Height=23SEX_CODE.Text=TButtonSEX_CODE.showID=YSEX_CODE.showName=YSEX_CODE.showText=NSEX_CODE.showValue=NSEX_CODE.showPy1=YSEX_CODE.showPy2=YSEX_CODE.Editable=YSEX_CODE.Tip=性别SEX_CODE.TableShowList=nameSEX_CODE.ModuleParmString=GROUP_ID:SYS_SEXSEX_CODE.ModuleParmTag=REPORTLIST.Type=健检顺序队列REPORTLIST.X=619REPORTLIST.Y=104REPORTLIST.Width=105REPORTLIST.Height=23REPORTLIST.Text=REPORTLIST.HorizontalAlignment=2REPORTLIST.PopupMenuHeader=代码,100;名称,100REPORTLIST.PopupMenuWidth=300REPORTLIST.PopupMenuHeight=300REPORTLIST.FormatType=comboREPORTLIST.ShowDownButton=YREPORTLIST.Tip=健检顺序队列REPORTLIST.ShowColumnList=NAMEINTRO_USER.Type=人员INTRO_USER.X=786INTRO_USER.Y=106INTRO_USER.Width=128INTRO_USER.Height=23INTRO_USER.Text=INTRO_USER.HorizontalAlignment=2INTRO_USER.PopupMenuHeader=代码,100;名称,100INTRO_USER.PopupMenuWidth=300INTRO_USER.PopupMenuHeight=300INTRO_USER.PopupMenuFilter=ID,1;NAME,1;PY1,1INTRO_USER.FormatType=comboINTRO_USER.ShowDownButton=YINTRO_USER.Tip=人员INTRO_USER.ShowColumnList=NAMEINTRO_USER.HrmFitFlg=YINTRO_USER.FinalFlg=YINTRO_USER.PosType=3INTRO_USER.Dept=<DEPT_CODE_COM>INTRO_USER.HisOneNullRow=YtLabel_20.Type=TLabeltLabel_20.X=726tLabel_20.Y=109tLabel_20.Width=60tLabel_20.Height=15tLabel_20.Text=导诊人员tLabel_14.Type=TLabeltLabel_14.X=561tLabel_14.Y=109tLabel_14.Width=57tLabel_14.Height=15tLabel_14.Text=体检队列CITY.Type=市下拉列表CITY.X=323CITY.Y=58CITY.Width=81CITY.Height=23CITY.Text=TButtonCITY.showID=YCITY.showName=YCITY.showText=NCITY.showValue=NCITY.showPy1=NCITY.showPy2=NCITY.Editable=YCITY.Tip=市下拉列表CITY.TableShowList=nameCITY.ModuleParmTag=CITY.ExpandWidth=40CITY.PostCode=tLabel_13.Type=TLabeltLabel_13.X=277tLabel_13.Y=63tLabel_13.Width=47tLabel_13.Height=15tLabel_13.Text=县、市PROVINCE.Type=省下拉列表PROVINCE.X=192PROVINCE.Y=58PROVINCE.Width=81PROVINCE.Height=23PROVINCE.Text=TButtonPROVINCE.showID=YPROVINCE.showName=YPROVINCE.showText=NPROVINCE.showValue=NPROVINCE.showPy1=NPROVINCE.showPy2=NPROVINCE.Editable=YPROVINCE.Tip=省下拉列表PROVINCE.TableShowList=namePROVINCE.ModuleParmTag=PROVINCE.ExpandWidth=40PROVINCE.PostCode=tLabel_7.Type=TLabeltLabel_7.X=118tLabel_7.Y=63tLabel_7.Width=73tLabel_7.Height=15tLabel_7.Text=省、直辖市FOREIGNER_FLG.Type=TCheckBoxFOREIGNER_FLG.X=330FOREIGNER_FLG.Y=11FOREIGNER_FLG.Width=81FOREIGNER_FLG.Height=23FOREIGNER_FLG.Text=其他证件FOREIGNER_FLG.Action=onForeigntLabel_6.Type=TLabeltLabel_6.X=13tLabel_6.Y=504tLabel_6.Width=60tLabel_6.Height=15tLabel_6.Text=支付方式OLD_PAT.Type=TCheckBoxOLD_PAT.X=3OLD_PAT.Y=12OLD_PAT.Width=54OLD_PAT.Height=23OLD_PAT.Text=复诊OLD_PAT.Action=isOldPatDEPT_ATTRIBUTE.Type=科室属性下拉区域DEPT_ATTRIBUTE.X=723DEPT_ATTRIBUTE.Y=109DEPT_ATTRIBUTE.Width=81DEPT_ATTRIBUTE.Height=23DEPT_ATTRIBUTE.Text=DEPT_ATTRIBUTE.HorizontalAlignment=2DEPT_ATTRIBUTE.PopupMenuHeader=代码,100;名称,100DEPT_ATTRIBUTE.PopupMenuWidth=300DEPT_ATTRIBUTE.PopupMenuHeight=300DEPT_ATTRIBUTE.PopupMenuFilter=ID,1;NAME,1;PY1,1DEPT_ATTRIBUTE.FormatType=comboDEPT_ATTRIBUTE.ShowDownButton=YDEPT_ATTRIBUTE.Tip=科室属性DEPT_ATTRIBUTE.ShowColumnList=NAMEDEPT_ATTRIBUTE.HisOneNullRow=YCONTRACT_CODE.Type=健康检查合同下拉区域CONTRACT_CODE.X=458CONTRACT_CODE.Y=104CONTRACT_CODE.Width=96CONTRACT_CODE.Height=23CONTRACT_CODE.Text=CONTRACT_CODE.HorizontalAlignment=2CONTRACT_CODE.PopupMenuHeader=代码,100;名称,100CONTRACT_CODE.PopupMenuWidth=300CONTRACT_CODE.PopupMenuHeight=300CONTRACT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1CONTRACT_CODE.FormatType=comboCONTRACT_CODE.ShowDownButton=YCONTRACT_CODE.Tip=健康检查合同CONTRACT_CODE.ShowColumnList=NAMECONTRACT_CODE.Action=onContractChooseCONTRACT_CODE.Enabled=YCONTRACT_CODE.HisOneNullRow=YCOMPANY_CODE.Type=健康检查团体下拉区域COMPANY_CODE.X=321COMPANY_CODE.Y=104COMPANY_CODE.Width=127COMPANY_CODE.Height=23COMPANY_CODE.Text=COMPANY_CODE.HorizontalAlignment=2COMPANY_CODE.PopupMenuHeader=代码,100;名称,100COMPANY_CODE.PopupMenuWidth=300COMPANY_CODE.PopupMenuHeight=300COMPANY_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1COMPANY_CODE.FormatType=comboCOMPANY_CODE.ShowDownButton=YCOMPANY_CODE.Tip=健康检查团体COMPANY_CODE.ShowColumnList=NAMECOMPANY_CODE.Action=onCompanyChooseCOMPANY_CODE.Enabled=YCOMPANY_CODE.HisOneNullRow=YPACKAGE_CODE.Type=健康检查套餐下拉区域PACKAGE_CODE.X=47PACKAGE_CODE.Y=104PACKAGE_CODE.Width=150PACKAGE_CODE.Height=23PACKAGE_CODE.Text=PACKAGE_CODE.HorizontalAlignment=2PACKAGE_CODE.PopupMenuHeader=代码,100;名称,100PACKAGE_CODE.PopupMenuWidth=300PACKAGE_CODE.PopupMenuHeight=300PACKAGE_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1PACKAGE_CODE.FormatType=comboPACKAGE_CODE.ShowDownButton=YPACKAGE_CODE.Tip=健康检查套餐PACKAGE_CODE.ShowColumnList=NAMEPACKAGE_CODE.ActiveFlg=YPACKAGE_CODE.Action=onPackageChoosePACKAGE_CODE.HisOneNullRow=YUPDATE_NO.Type=TTextFieldUPDATE_NO.X=75UPDATE_NO.Y=468UPDATE_NO.Width=138UPDATE_NO.Height=20UPDATE_NO.Text=UPDATE_NO.Enabled=NtLabel_33.Type=TLabeltLabel_33.X=12tLabel_33.Y=472tLabel_33.Width=60tLabel_33.Height=15tLabel_33.Text=下一票号OPERATOR.Type=人员下拉列表OPERATOR.X=1039OPERATOR.Y=48OPERATOR.Width=81OPERATOR.Height=23OPERATOR.Text=TButtonOPERATOR.showID=YOPERATOR.showName=YOPERATOR.showText=NOPERATOR.showValue=NOPERATOR.showPy1=YOPERATOR.showPy2=YOPERATOR.Editable=YOPERATOR.Tip=人员OPERATOR.TableShowList=nameOPERATOR.ModuleParmString=OPERATOR.ModuleParmTag=UNIT_CODE.Type=计量单位下拉列表UNIT_CODE.X=1036UNIT_CODE.Y=4UNIT_CODE.Width=81UNIT_CODE.Height=23UNIT_CODE.Text=TButtonUNIT_CODE.showID=YUNIT_CODE.showName=YUNIT_CODE.showText=NUNIT_CODE.showValue=NUNIT_CODE.showPy1=YUNIT_CODE.showPy2=YUNIT_CODE.Editable=YUNIT_CODE.Tip=计量单位UNIT_CODE.TableShowList=nameREPORT_DATE.Type=TTextFormatREPORT_DATE.X=980REPORT_DATE.Y=61REPORT_DATE.Width=115REPORT_DATE.Height=20REPORT_DATE.Text=REPORT_DATE.showDownButton=YREPORT_DATE.FormatType=dateREPORT_DATE.Format=yyyy/MM/ddREPORT_DATE.HorizontalAlignment=2tButton_29.Type=TButtontButton_29.X=1037tButton_29.Y=105tButton_29.Width=60tButton_29.Height=23tButton_29.Text=拍照tButton_29.Action=onPhotoPHOTO_PANEL.Type=TPanelPHOTO_PANEL.X=1103PHOTO_PANEL.Y=7PHOTO_PANEL.Width=109PHOTO_PANEL.Height=133PHOTO_PANEL.Border=FEE.Type=TButtonFEE.X=890FEE.Y=526FEE.Width=81FEE.Height=23FEE.Text=收费FEE.Action=onFeeSAVE_PAT.Type=TButtonSAVE_PAT.X=918SAVE_PAT.Y=105SAVE_PAT.Width=119SAVE_PAT.Height=23SAVE_PAT.Text=保存病患信息SAVE_PAT.Action=onSavePatCHANGE.Type=TNumberTextFieldCHANGE.X=709CHANGE.Y=472CHANGE.Width=159CHANGE.Height=91CHANGE.Text=666CHANGE.Format=#########0.00CHANGE.Color=红CHANGE.FontSize=40CHANGE.FontStyle=0CHANGE.Enabled=NtLabel_19.Type=TLabeltLabel_19.X=740tLabel_19.Y=451tLabel_19.Width=72tLabel_19.Height=15tLabel_19.Text=找零金额tLabel_18.Type=TLabeltLabel_18.X=664tLabel_18.Y=496tLabel_18.Width=38tLabel_18.Height=41tLabel_18.Text=-tLabel_18.FontSize=48REAL.Type=TNumberTextFieldREAL.X=491REAL.Y=471REAL.Width=159REAL.Height=92REAL.Text=REAL.Format=#########0.00REAL.FontSize=40REAL.Color=红REAL.Action=onRealtLabel_17.Type=TLabeltLabel_17.X=523tLabel_17.Y=452tLabel_17.Width=72tLabel_17.Height=15tLabel_17.Text=实收金额tLabel_16.Type=TLabeltLabel_16.X=444tLabel_16.Y=494tLabel_16.Width=57tLabel_16.Height=42tLabel_16.Text==tLabel_16.FontSize=48CHARGE.Type=TNumberTextFieldCHARGE.X=270CHARGE.Y=472CHARGE.Width=159CHARGE.Height=91CHARGE.Text=2222CHARGE.Format=#########0.00CHARGE.Color=红CHARGE.FontSize=40CHARGE.Enabled=NtLabel_15.Type=TLabeltLabel_15.X=317tLabel_15.Y=452tLabel_15.Width=72tLabel_15.Height=15tLabel_15.Text=应收金额TABLE.Type=TTableTABLE.X=8TABLE.Y=145TABLE.Width=888TABLE.Height=292TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoWidth=YTABLE.Header=医嘱名称,300;数量,60;单位,60,UNIT_CODE;单价,60,double;开立人,100,OPERATOR;开立科室,100,DEPT;执行科室,100,DEPT;科别属性,100,DEPT_ATTRIBUTE;总价,60,doubleTABLE.ParmMap=ORDER_DESC;DISPENSE_QTY;DISPENSE_UNIT;OWN_PRICE_MAIN;DR_CODE;DEPT_CODE;EXEC_DEPT_CODE;DEPT_ATTRIBUTE;AR_AMT_MAINTABLE.AutoModifyDataStore=YTABLE.ColumnHorizontalAlignmentData=0,left;1,right;2,left;3,right;4,left;5,left;6,left;7,left;8,rightTABLE.LockColumns=TABLE.FocusType=2TABLE.Item=UNIT_CODE;OPERATOR;DEPT_ATTRIBUTE;DEPTTABLE.RightClickedAction=showPopMenuLBL_CON.Type=TLabelLBL_CON.X=510LBL_CON.Y=109LBL_CON.Width=30LBL_CON.Height=15LBL_CON.Text=合同LBL_COM.Type=TLabelLBL_COM.X=286LBL_COM.Y=109LBL_COM.Width=31LBL_COM.Height=15LBL_COM.Text=公司COM_PAY_FLG.Type=TCheckBoxCOM_PAY_FLG.X=200COM_PAY_FLG.Y=106COM_PAY_FLG.Width=81COM_PAY_FLG.Height=23COM_PAY_FLG.Text=公司支付COM_PAY_FLG.Action=onCompanyPaytLabel_12.Type=TLabeltLabel_12.X=920tLabel_12.Y=64tLabel_12.Width=61tLabel_12.Height=15tLabel_12.Text=报到日期tLabel_11.Type=TLabeltLabel_11.X=759tLabel_11.Y=63tLabel_11.Width=60tLabel_11.Height=15tLabel_11.Text=报到部门tLabel_10.Type=TLabeltLabel_10.X=7tLabel_10.Y=109tLabel_10.Width=33tLabel_10.Height=15tLabel_10.Text=套餐TEL.Type=TTextFieldTEL.X=656TEL.Y=60TEL.Width=100TEL.Height=20TEL.Text=tLabel_9.Type=TLabeltLabel_9.X=623tLabel_9.Y=63tLabel_9.Width=30tLabel_9.Height=15tLabel_9.Text=电话ADDRESS.Type=TTextFieldADDRESS.X=442ADDRESS.Y=60ADDRESS.Width=176ADDRESS.Height=20ADDRESS.Text=tLabel_8.Type=TLabeltLabel_8.X=409tLabel_8.Y=63tLabel_8.Width=35tLabel_8.Height=15tLabel_8.Text=地址POST_CODE.Type=TTextFieldPOST_CODE.X=46POST_CODE.Y=60POST_CODE.Width=66POST_CODE.Height=20POST_CODE.Text=POST_CODE.Action=onPostCodetLabel_5.Type=TLabeltLabel_5.X=7tLabel_5.Y=63tLabel_5.Width=33tLabel_5.Height=15tLabel_5.Text=邮编BIRTH_DATE.Type=TTextFormatBIRTH_DATE.X=800BIRTH_DATE.Y=12BIRTH_DATE.Width=117BIRTH_DATE.Height=20BIRTH_DATE.Text=BIRTH_DATE.showDownButton=YBIRTH_DATE.Format=yyyy/MM/ddBIRTH_DATE.FormatType=dateBIRTH_DATE.HorizontalAlignment=2tLabel_4.Type=TLabeltLabel_4.X=741tLabel_4.Y=15tLabel_4.Width=58tLabel_4.Height=15tLabel_4.Text=出生日期tLabel_3.Type=TLabeltLabel_3.X=637tLabel_3.Y=15tLabel_3.Width=33tLabel_3.Height=15tLabel_3.Text=性别IDNO.Type=TTextFieldIDNO.X=483IDNO.Y=12IDNO.Width=151IDNO.Height=20IDNO.Text=IDNO.FocusLostAction=IDNO.Action=onIdNotLabel_2.Type=TLabeltLabel_2.X=418tLabel_2.Y=15tLabel_2.Width=59tLabel_2.Height=15tLabel_2.Text=身份证号MR_NO.Type=TTextFieldMR_NO.X=106MR_NO.Y=13MR_NO.Width=104MR_NO.Height=20MR_NO.Text=MR_NO.Enabled=NMR_NO.Action=onMrNotLabel_1.Type=TLabeltLabel_1.X=58tLabel_1.Y=16tLabel_1.Width=47tLabel_1.Height=15tLabel_1.Text=病案号tLabel_1.Color=蓝PAT_NAME.Type=TTextFieldPAT_NAME.X=251PAT_NAME.Y=12PAT_NAME.Width=77PAT_NAME.Height=20PAT_NAME.Text=PAT_NAME.FocusLostAction=PAT_NAME.Action=onPatNametLabel_0.Type=TLabeltLabel_0.X=219tLabel_0.Y=15tLabel_0.Width=32tLabel_0.Height=15tLabel_0.Text=姓名tLabel_0.Color=蓝