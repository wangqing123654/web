<Type=TFrame>UI.Title=药房主窗口西成药头文件UI.MenuConfig=UI.Width=1500UI.Height=1500UI.toolbar=YUI.controlclassname=com.javahis.ui.pha.PHAPanelWDControlUI.Border=UI.Item=tLabel_0;tLabel_1;tLabel_2;tLabel_3;tLabel_4;tLabel_5;tLabel_6;tLabel_8;RX_NO;MR_NO;PAT_NAME;from_PRESCRIPT_NO;tLabel_10;to_PRESCRIPT_NO;tLabel_9;FINISH;UNFINISH;tLabel_12;EXEC_DEPT_CODE;AGENCY_ORG_CODE;PHATYPE;tLabel_13;tLabel_14;from_ORDER_DATE;to_ORDER_DATE;tLabel_7;COUNTER_NO;CHECKBUTTON;DOSAGEBUTTON;DISPENSEBUTTON;RETURNBUTTON;tLabel_11;REGION_CODE;S_TIME;E_TIME;DEPT_CODE;DR_CODE;tLabel_15;BASKET_ID;MAIN_DIAG_LABEL;MAIN_DIAG;SECONDARY_DIAG_LABEL;SECONDARY_DIAG;PRESRT_NO;RX_PRESRT_NO;GMS;DRUGORINGRD_CODEUI.AutoWidth=YUI.AutoY=YUI.AutoX=YUI.AutoHeight=YUI.X=0UI.Y=0UI.AutoSize=0UI.FocusList=RX_NO;BASKET_IDDRUGORINGRD_CODE.Type=TLabelDRUGORINGRD_CODE.X=708DRUGORINGRD_CODE.Y=9DRUGORINGRD_CODE.Width=336DRUGORINGRD_CODE.Height=19DRUGORINGRD_CODE.Text=DRUGORINGRD_CODE.Color=redDRUGORINGRD_CODE.FontSize=14GMS.Type=TLabelGMS.X=660GMS.Y=9GMS.Width=59GMS.Height=19GMS.Text= 过敏史：GMS.Color=redGMS.Visible=NGMS.FontSize=14RX_PRESRT_NO.Type=TTextFieldRX_PRESRT_NO.X=261RX_PRESRT_NO.Y=35RX_PRESRT_NO.Width=118RX_PRESRT_NO.Height=23RX_PRESRT_NO.Text=RX_PRESRT_NO.Action=onRxNoPRESRT_NO.Type=TTextFieldPRESRT_NO.X=359PRESRT_NO.Y=34PRESRT_NO.Width=46PRESRT_NO.Height=23PRESRT_NO.Text=PRESRT_NO.Visible=NSECONDARY_DIAG.Type=TTextAreaSECONDARY_DIAG.X=1088SECONDARY_DIAG.Y=37SECONDARY_DIAG.Width=237SECONDARY_DIAG.Height=47SECONDARY_DIAG.SpacingRow=1SECONDARY_DIAG.RowHeight=20SECONDARY_DIAG.Enabled=NSECONDARY_DIAG.Visible=YSECONDARY_DIAG_LABEL.Type=TLabelSECONDARY_DIAG_LABEL.X=1042SECONDARY_DIAG_LABEL.Y=40SECONDARY_DIAG_LABEL.Width=47SECONDARY_DIAG_LABEL.Height=15SECONDARY_DIAG_LABEL.Text=次诊断MAIN_DIAG.Type=TTextFieldMAIN_DIAG.X=1087MAIN_DIAG.Y=7MAIN_DIAG.Width=239MAIN_DIAG.Height=20MAIN_DIAG.Text=MAIN_DIAG.Enabled=NMAIN_DIAG_LABEL.Type=TLabelMAIN_DIAG_LABEL.X=1043MAIN_DIAG_LABEL.Y=10MAIN_DIAG_LABEL.Width=48MAIN_DIAG_LABEL.Height=15MAIN_DIAG_LABEL.Text=主诊断MAIN_DIAG_LABEL.Visible=YBASKET_ID.Type=TTextFieldBASKET_ID.X=936BASKET_ID.Y=62BASKET_ID.Width=80BASKET_ID.Height=20BASKET_ID.Text=BASKET_ID.Action=onBasketIdtLabel_15.Type=TLabeltLabel_15.X=874tLabel_15.Y=65tLabel_15.Width=61tLabel_15.Height=15tLabel_15.Text=药框标签DR_CODE.Type=人员DR_CODE.X=935DR_CODE.Y=34DR_CODE.Width=81DR_CODE.Height=23DR_CODE.Text=DR_CODE.HorizontalAlignment=2DR_CODE.PopupMenuHeader=代码,100;名称,100DR_CODE.PopupMenuWidth=300DR_CODE.PopupMenuHeight=300DR_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DR_CODE.FormatType=comboDR_CODE.ShowDownButton=YDR_CODE.Tip=人员DR_CODE.ShowColumnList=NAMEDR_CODE.Dept=<DEPT_CODE>DR_CODE.HisOneNullRow=YDR_CODE.OpdFitFlg=YDR_CODE.EndDateFlg=1DEPT_CODE.Type=科室DEPT_CODE.X=781DEPT_CODE.Y=33DEPT_CODE.Width=81DEPT_CODE.Height=23DEPT_CODE.Text=DEPT_CODE.HorizontalAlignment=2DEPT_CODE.PopupMenuHeader=代码,100;名称,100DEPT_CODE.PopupMenuWidth=300DEPT_CODE.PopupMenuHeight=300DEPT_CODE.FormatType=comboDEPT_CODE.ShowDownButton=YDEPT_CODE.Tip=科室DEPT_CODE.ShowColumnList=NAMEDEPT_CODE.Action=DR_CODE|onQueryDEPT_CODE.HisOneNullRow=YDEPT_CODE.OpdFitFlg=YE_TIME.Type=TTextFieldE_TIME.X=591E_TIME.Y=7E_TIME.Width=67E_TIME.Height=22E_TIME.Text=23:59:59E_TIME.Enabled=NS_TIME.Type=TTextFieldS_TIME.X=380S_TIME.Y=7S_TIME.Width=67S_TIME.Height=22S_TIME.Text=00:00:00S_TIME.Enabled=NREGION_CODE.Type=区域下拉列表REGION_CODE.X=70REGION_CODE.Y=6REGION_CODE.Width=123REGION_CODE.Height=23REGION_CODE.Text=TButtonREGION_CODE.showID=YREGION_CODE.showName=YREGION_CODE.showText=NREGION_CODE.showValue=NREGION_CODE.showPy1=NREGION_CODE.showPy2=NREGION_CODE.Editable=YREGION_CODE.Tip=区域REGION_CODE.TableShowList=nameREGION_CODE.ModuleParmString=REGION_CODE.ModuleParmTag=REGION_CODE.Enabled=YREGION_CODE.ExpandWidth=80tLabel_11.Type=TLabeltLabel_11.X=11tLabel_11.Y=10tLabel_11.Width=63tLabel_11.Height=15tLabel_11.Text=区    域 tLabel_11.Color=blueRETURNBUTTON.Type=TRadioButtonRETURNBUTTON.X=876RETURNBUTTON.Y=6RETURNBUTTON.Width=63RETURNBUTTON.Height=23RETURNBUTTON.Text=退药RETURNBUTTON.Group=1RETURNBUTTON.Color=blueDISPENSEBUTTON.Type=TRadioButtonDISPENSEBUTTON.X=823DISPENSEBUTTON.Y=6DISPENSEBUTTON.Width=55DISPENSEBUTTON.Height=23DISPENSEBUTTON.Text=发药DISPENSEBUTTON.Group=1DISPENSEBUTTON.Color=blueDOSAGEBUTTON.Type=TRadioButtonDOSAGEBUTTON.X=773DOSAGEBUTTON.Y=6DOSAGEBUTTON.Width=54DOSAGEBUTTON.Height=23DOSAGEBUTTON.Text=配药DOSAGEBUTTON.Group=1DOSAGEBUTTON.Color=blueCHECKBUTTON.Type=TRadioButtonCHECKBUTTON.X=717CHECKBUTTON.Y=6CHECKBUTTON.Width=56CHECKBUTTON.Height=23CHECKBUTTON.Text=审核CHECKBUTTON.Group=1CHECKBUTTON.Color=blueCOUNTER_NO.Type=领药窗口下拉区域COUNTER_NO.X=468COUNTER_NO.Y=63COUNTER_NO.Width=121COUNTER_NO.Height=23COUNTER_NO.Text=COUNTER_NO.HorizontalAlignment=2COUNTER_NO.PopupMenuHeader=代码,100;名称,100COUNTER_NO.PopupMenuWidth=300COUNTER_NO.PopupMenuHeight=300COUNTER_NO.FormatType=comboCOUNTER_NO.ShowDownButton=YCOUNTER_NO.Tip=科室COUNTER_NO.ShowColumnList=NAMECOUNTER_NO.HisOneNullRow=YCOUNTER_NO.OrgCode=<EXEC_DEPT_CODE>tLabel_7.Type=TLabeltLabel_7.X=404tLabel_7.Y=68tLabel_7.Width=56tLabel_7.Height=15tLabel_7.Text=领药窗口tLabel_7.Color=blueto_ORDER_DATE.Type=TTextFormatto_ORDER_DATE.X=468to_ORDER_DATE.Y=6to_ORDER_DATE.Width=118to_ORDER_DATE.Height=23to_ORDER_DATE.Text=to_ORDER_DATE.showDownButton=Yto_ORDER_DATE.Format=yyyy/MM/ddto_ORDER_DATE.BKColor=to_ORDER_DATE.FormatType=datefrom_ORDER_DATE.Type=TTextFormatfrom_ORDER_DATE.X=260from_ORDER_DATE.Y=6from_ORDER_DATE.Width=118from_ORDER_DATE.Height=23from_ORDER_DATE.Text=from_ORDER_DATE.FormatType=datefrom_ORDER_DATE.showDownButton=Yfrom_ORDER_DATE.Format=yyyy/MM/ddfrom_ORDER_DATE.BKColor=tLabel_14.Type=TLabeltLabel_14.X=452tLabel_14.Y=15tLabel_14.Width=13tLabel_14.Height=15tLabel_14.Text=~tLabel_13.Type=TLabeltLabel_13.X=200tLabel_13.Y=10tLabel_13.Width=59tLabel_13.Height=15tLabel_13.Text=查询时间tLabel_13.Color=bluePHATYPE.Type=药品分类下拉列表PHATYPE.X=1042PHATYPE.Y=8PHATYPE.Width=81PHATYPE.Height=23PHATYPE.Text=TButtonPHATYPE.showID=YPHATYPE.showName=YPHATYPE.showText=NPHATYPE.showValue=NPHATYPE.showPy1=YPHATYPE.showPy2=YPHATYPE.Editable=YPHATYPE.Tip=药品分类PHATYPE.TableShowList=id,namePHATYPE.ModuleParmString=GROUP_ID:SYS_PHATYPEPHATYPE.ModuleParmTag=PHATYPE.Visible=NPHATYPE.Color=blueAGENCY_ORG_CODE.Type=药房下拉列表AGENCY_ORG_CODE.X=70AGENCY_ORG_CODE.Y=63AGENCY_ORG_CODE.Width=123AGENCY_ORG_CODE.Height=23AGENCY_ORG_CODE.Text=TButtonAGENCY_ORG_CODE.showID=YAGENCY_ORG_CODE.showName=YAGENCY_ORG_CODE.showText=NAGENCY_ORG_CODE.showValue=NAGENCY_ORG_CODE.showPy1=YAGENCY_ORG_CODE.showPy2=YAGENCY_ORG_CODE.Editable=YAGENCY_ORG_CODE.Tip=药房AGENCY_ORG_CODE.TableShowList=nameAGENCY_ORG_CODE.ModuleParmTag=AGENCY_ORG_CODE.OrgType=BAGENCY_ORG_CODE.ExpandWidth=70EXEC_DEPT_CODE.Type=药房下拉列表EXEC_DEPT_CODE.X=70EXEC_DEPT_CODE.Y=34EXEC_DEPT_CODE.Width=123EXEC_DEPT_CODE.Height=23EXEC_DEPT_CODE.Text=TButtonEXEC_DEPT_CODE.showID=YEXEC_DEPT_CODE.showName=YEXEC_DEPT_CODE.showText=NEXEC_DEPT_CODE.showValue=NEXEC_DEPT_CODE.showPy1=YEXEC_DEPT_CODE.showPy2=YEXEC_DEPT_CODE.Editable=YEXEC_DEPT_CODE.Tip=药房EXEC_DEPT_CODE.TableShowList=nameEXEC_DEPT_CODE.ModuleParmTag=EXEC_DEPT_CODE.OrgType=BEXEC_DEPT_CODE.ExpandWidth=70EXEC_DEPT_CODE.Action=EXEC_DEPT_CODE.SelectedAction=COUNTER_NO|onQuerytLabel_12.Type=TLabeltLabel_12.X=960tLabel_12.Y=8tLabel_12.Width=61tLabel_12.Height=19tLabel_12.Text=药品类型tLabel_12.HorizontalAlignment=0tLabel_12.Visible=NUNFINISH.Type=TRadioButtonUNFINISH.X=700UNFINISH.Y=63UNFINISH.Width=68UNFINISH.Height=23UNFINISH.Text=未完成UNFINISH.Group=STATUSUNFINISH.Selected=YUNFINISH.Action=onClearFINISH.Type=TRadioButtonFINISH.X=633FINISH.Y=63FINISH.Width=54FINISH.Height=23FINISH.Text=完成FINISH.Group=STATUSFINISH.Action=onCleartLabel_9.Type=TLabeltLabel_9.X=602tLabel_9.Y=67tLabel_9.Width=34tLabel_9.Height=15tLabel_9.Text=状态tLabel_9.Color=blueto_PRESCRIPT_NO.Type=TTextFieldto_PRESCRIPT_NO.X=328to_PRESCRIPT_NO.Y=63to_PRESCRIPT_NO.Width=51to_PRESCRIPT_NO.Height=23to_PRESCRIPT_NO.Text=tLabel_10.Type=TLabeltLabel_10.X=312tLabel_10.Y=70tLabel_10.Width=13tLabel_10.Height=20tLabel_10.Text=~from_PRESCRIPT_NO.Type=TTextFieldfrom_PRESCRIPT_NO.X=260from_PRESCRIPT_NO.Y=63from_PRESCRIPT_NO.Width=48from_PRESCRIPT_NO.Height=23from_PRESCRIPT_NO.Text=PAT_NAME.Type=TTextFieldPAT_NAME.X=633PAT_NAME.Y=34PAT_NAME.Width=77PAT_NAME.Height=23PAT_NAME.Text=MR_NO.Type=TTextFieldMR_NO.X=468MR_NO.Y=34MR_NO.Width=119MR_NO.Height=23MR_NO.Text=MR_NO.Action=onMrNoRX_NO.Type=TTextFieldRX_NO.X=200RX_NO.Y=34RX_NO.Width=90RX_NO.Height=23RX_NO.Text=RX_NO.Action=onRxNoRX_NO.Visible=NtLabel_8.Type=TLabeltLabel_8.X=11tLabel_8.Y=67tLabel_8.Width=58tLabel_8.Height=15tLabel_8.Text=代理药房tLabel_6.Type=TLabeltLabel_6.X=875tLabel_6.Y=38tLabel_6.Width=60tLabel_6.Height=15tLabel_6.Text=开立医生tLabel_6.Color=bluetLabel_5.Type=TLabeltLabel_5.X=722tLabel_5.Y=38tLabel_5.Width=72tLabel_5.Height=15tLabel_5.Text=开立科室tLabel_5.Color=bluetLabel_4.Type=TLabeltLabel_4.X=201tLabel_4.Y=38tLabel_4.Width=58tLabel_4.Height=15tLabel_4.Text=处方签号tLabel_4.Color=bluetLabel_3.Type=TLabeltLabel_3.X=601tLabel_3.Y=38tLabel_3.Width=33tLabel_3.Height=15tLabel_3.Text=姓名tLabel_2.Type=TLabeltLabel_2.X=416tLabel_2.Y=38tLabel_2.Width=44tLabel_2.Height=15tLabel_2.Text=病案号tLabel_2.Color=bluetLabel_1.Type=TLabeltLabel_1.X=215tLabel_1.Y=69tLabel_1.Width=44tLabel_1.Height=15tLabel_1.Text=领药号tLabel_0.Type=TLabeltLabel_0.X=11tLabel_0.Y=38tLabel_0.Width=61tLabel_0.Height=15tLabel_0.Text=执行药房tLabel_0.Color=blue