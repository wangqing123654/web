############################################## <p>Title:传染病报告卡查询 </p>## <p>Description:传染病报告卡查询 </p>## <p>Copyright: Copyright (c) 2008</p>## <p>Company: Javahis</p>## @author ZhangK 2009.10.14# @version 4.0#############################################<Type=TFrame>UI.Title=医保住院医疗费申请支付信息UI.MenuConfig=%ROOT%\config\ins\INSIbsMaterialSearchMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.ins.INSIbsMaterialSearchControlUI.Item=TABLE;tLabel_0;REGION_CODE;tLabel_1;OUT_DATE_START;tLabel_2;OUT_DATE_ENDUI.TopMenu=YUI.TopToolBar=YUI.ShowTitle=NUI.ShowMenu=YOUT_DATE_END.Type=TTextFormatOUT_DATE_END.X=492OUT_DATE_END.Y=27OUT_DATE_END.Width=126OUT_DATE_END.Height=20OUT_DATE_END.Text=OUT_DATE_END.FormatType=dateOUT_DATE_END.Format=yyyy/MM/ddOUT_DATE_END.showDownButton=YtLabel_2.Type=TLabeltLabel_2.X=471tLabel_2.Y=35tLabel_2.Width=14tLabel_2.Height=15tLabel_2.Text=~tLabel_2.Color=蓝OUT_DATE_START.Type=TTextFormatOUT_DATE_START.X=335OUT_DATE_START.Y=27OUT_DATE_START.Width=127OUT_DATE_START.Height=20OUT_DATE_START.Text=OUT_DATE_START.FormatType=dateOUT_DATE_START.Format=yyyy/MM/ddOUT_DATE_START.showDownButton=YtLabel_1.Type=TLabeltLabel_1.X=264tLabel_1.Y=30tLabel_1.Width=72tLabel_1.Height=15tLabel_1.Text=出院日期:tLabel_1.Color=蓝REGION_CODE.Type=区域下拉列表REGION_CODE.X=86REGION_CODE.Y=26REGION_CODE.Width=129REGION_CODE.Height=23REGION_CODE.Text=TButtonREGION_CODE.showID=YREGION_CODE.showName=YREGION_CODE.showText=NREGION_CODE.showValue=NREGION_CODE.showPy1=NREGION_CODE.showPy2=NREGION_CODE.Editable=YREGION_CODE.Tip=区域REGION_CODE.TableShowList=nameREGION_CODE.ModuleParmString=REGION_CODE.ModuleParmTag=REGION_CODE.ExpandWidth=80tLabel_0.Type=TLabeltLabel_0.X=39tLabel_0.Y=31tLabel_0.Width=40tLabel_0.Height=15tLabel_0.Text=区域:tLabel_0.Color=蓝TABLE.Type=TTableTABLE.X=6TABLE.Y=59TABLE.Width=81TABLE.Height=680TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=YTABLE.AutoY=NTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Header=病案号,100;住院号,100;病患名称,100;性别,50;年龄,50;身份,90;入院日期,100;出院日期,100;住院天数,100;科室,100;药品费,100,double,######0.00;检查费,100,double,######0.00;治疗费,100,double,######0.00;手术费,100,double,######0.00;床位费,100,double,######0.00;医用材料,100,double,######0.00;输全血,100,double,######0.00;成分输血,100,double,######0.00;其他,100,double,######0.00;合计,100,double,######0.00;自费金额,100,double,######0.00;增负金额,100,double,######0.00;个人合计,100,double,######0.00;实际自负金额,100,double,######0.00;医保基金_医疗机构申请金额,100,double,######0.00;医保基金_社保支付金额,100,double,######0.00;大额救助_医疗机构申请金额,170,double,######0.00;大额救助_社保建议支付金额,170,double,######0.00;个人账户支付_医疗机构申请金额,170,double,######0.00;个人账户支付_社保支付金额,170,double,######0.00;合计支付_社保支付合计金额,170,double,######0.00;术式,300;贵重药费,100,double,######0.00;贵重材料费,100,double,######0.00TABLE.ParmMap=MR_NO;IPD_NO;PAT_NAME;CHN_DESC;PAT_AGE;CTZ_DESC;IN_DATE;DS_DATE;IN_DAYS;DEPT_DESC;PHA_AMT;EXM_AMT;TREAT_AMT;OP_AMT;BED_AMT;MATERIAL_AMT;BLOODALL_AMT;BLOOD_AMT;OTHER_AMT;SUM_AMT;OWN_AMT;ADD_AMT;OWN_SUM_AMT;SJZFJE;NHI_PAY;NHI_PAY_REAL;HOSP_APPLY_AMT;NHI_COMMENT;ACCOUNT_PAY_AMT;GRZHZF;HJZFJE;OP_DESC;SPHA;SMATERIAL_AMTTABLE.Item=TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,right;5,left;6,left;7,left;8,right;9,left;10,right;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,right;23,right;24,right;25,right;26,right;27,right;28,right;29,right;30,right;31,left;32,right;33,rightTABLE.LockColumns=allTABLE.DoubleClickedAction=