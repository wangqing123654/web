## TBuilder Config File ## Title:入库作业## Company:JavaHis## Author:zhangy 2009.05.07## version 1.0#<Type=TFrame>UI.Title=一般入库作业UI.MenuConfig=%ROOT%\config\spc\SPCIncityDispenseINMenu.xUI.Width=1203UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.spc.INDDispenseINControlUI.item=tPanel_1;tPanel_0;tPanel_3;tMovePane_0UI.layout=nullUI.Text=入库作业UI.Tip=入库作业UI.TopMenu=YUI.TopToolBar=YUI.ShowTitle=NUI.FocusList=UPDATE_FLG_A;UPDATE_FLG_B;START_DATE;END_DATE;DISPENSE_NO;REQUEST_NO;APP_ORG_CODE;REQTYPE_CODE;REQUEST_DATE;REASON_CHN_DESC;TO_ORG_CODE;WAREHOUSING_DATE;DESCRIPTION;URGENT_FLGtMovePane_0.Type=TMovePanetMovePane_0.X=4tMovePane_0.Y=285tMovePane_0.Width=1193tMovePane_0.Height=5tMovePane_0.Text=tMovePane_0.MoveType=2tMovePane_0.AutoX=YtMovePane_0.AutoWidth=YtMovePane_0.Border=凸tMovePane_0.Style=3tMovePane_0.EntityData=tPanel_0,2;tPanel_3,1tPanel_3.Type=TPaneltPanel_3.X=5tPanel_3.Y=290tPanel_3.Width=1193tPanel_3.Height=453tPanel_3.Border=组|出库细项tPanel_3.AutoX=YtPanel_3.AutoWidth=YtPanel_3.AutoHeight=YtPanel_3.Item=tPanel_4;tPanel_5tPanel_5.Type=TPaneltPanel_5.X=11tPanel_5.Y=55tPanel_5.Width=1171tPanel_5.Height=387tPanel_5.AutoX=YtPanel_5.AutoWidth=YtPanel_5.AutoHeight=YtPanel_5.Border=凹tPanel_5.Item=TABLE_DTABLE_D.Type=TTableTABLE_D.X=112TABLE_D.Y=54TABLE_D.Width=1167TABLE_D.Height=383TABLE_D.SpacingRow=1TABLE_D.RowHeight=20TABLE_D.AutoX=YTABLE_D.AutoY=YTABLE_D.AutoWidth=YTABLE_D.AutoHeight=YTABLE_D.AutoSize=0TABLE_D.Header=选,30,boolean;药品名称,180;规格,120;申请数量,80,double,#####0.000;累计出库数,100,double,#####0.000;库存量,60,double,#####0.000;本次入库数,100,double,#####0.000;单位,40,UNIT;进货价,60,double,#####0.0000;进货金额,80,double,#####0.00;零售价,60,double,#####0.0000;零售金额,80,double,#####0.00;批次,80;效期,120;药品种类,80,PHA_TYPE;供应商代码,70TABLE_D.ColumnHorizontalAlignmentData=1,left;2,left;3,right;4,right;5,right;6,right;7,left;8,right;9,right;10,right;11,right;12,left;13,left;14,leftTABLE_D.ParmMap=SELECT_FLG;ORDER_DESC;SPECIFICATION;QTY;ACTUAL_QTY;STOCK_QTY;OUT_QTY;UNIT_CODE;STOCK_PRICE;STOCK_ATM;RETAIL_PRICE;RETAIL_ATM;BATCH_NO;VALID_DATE;PHA_TYPE;SUP_CODE;VERIFYIN_PRICE;INVENT_PRICETABLE_D.LockColumns=allTABLE_D.Item=UNIT;PHA_TYPETABLE_D.ClickedAction=onTableDClickedtPanel_4.Type=TPaneltPanel_4.X=11tPanel_4.Y=24tPanel_4.Width=1171tPanel_4.Height=29tPanel_4.Border=组tPanel_4.AutoX=YtPanel_4.AutoY=YtPanel_4.AutoWidth=YtPanel_4.Item=SELECT_ALL;tLabel_16;SUM_RETAIL_PRICE;tLabel_17;SUM_VERIFYIN_PRICE;tLabel_18;PRICE_DIFFERENCEPRICE_DIFFERENCE.Type=TNumberTextFieldPRICE_DIFFERENCE.X=899PRICE_DIFFERENCE.Y=4PRICE_DIFFERENCE.Width=80PRICE_DIFFERENCE.Height=20PRICE_DIFFERENCE.Text=0PRICE_DIFFERENCE.Format=#########0.00PRICE_DIFFERENCE.Enabled=NPRICE_DIFFERENCE.Visible=NtLabel_18.Type=TLabeltLabel_18.X=827tLabel_18.Y=7tLabel_18.Width=72tLabel_18.Height=15tLabel_18.Text=进销差价:tLabel_18.Visible=NSUM_VERIFYIN_PRICE.Type=TNumberTextFieldSUM_VERIFYIN_PRICE.X=721SUM_VERIFYIN_PRICE.Y=4SUM_VERIFYIN_PRICE.Width=80SUM_VERIFYIN_PRICE.Height=20SUM_VERIFYIN_PRICE.Text=0SUM_VERIFYIN_PRICE.Format=#########0.00SUM_VERIFYIN_PRICE.Enabled=NtLabel_17.Type=TLabeltLabel_17.X=640tLabel_17.Y=7tLabel_17.Width=80tLabel_17.Height=15tLabel_17.Text=采购总金额:SUM_RETAIL_PRICE.Type=TNumberTextFieldSUM_RETAIL_PRICE.X=537SUM_RETAIL_PRICE.Y=4SUM_RETAIL_PRICE.Width=80SUM_RETAIL_PRICE.Height=20SUM_RETAIL_PRICE.Text=0SUM_RETAIL_PRICE.Format=#########0.00SUM_RETAIL_PRICE.Enabled=NtLabel_16.Type=TLabeltLabel_16.X=455tLabel_16.Y=7tLabel_16.Width=80tLabel_16.Height=15tLabel_16.Text=零售总金额:SELECT_ALL.Type=TCheckBoxSELECT_ALL.X=11SELECT_ALL.Y=4SELECT_ALL.Width=81SELECT_ALL.Height=23SELECT_ALL.Text=全部选取SELECT_ALL.Action=onCheckSelectAlltPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=127tPanel_0.Width=1193tPanel_0.Height=157tPanel_0.Border=凹tPanel_0.AutoX=YtPanel_0.AutoWidth=YtPanel_0.Item=TABLE_MTABLE_M.Type=TTableTABLE_M.X=113TABLE_M.Y=40TABLE_M.Width=1189TABLE_M.Height=153TABLE_M.SpacingRow=1TABLE_M.RowHeight=20TABLE_M.AutoX=YTABLE_M.AutoY=YTABLE_M.AutoWidth=YTABLE_M.AutoHeight=YTABLE_M.AutoSize=0TABLE_M.Header=单号类别,100,REQTYPE_CODE;申请单号,100;申请部门,100,APP_ORG_CODE;接受部门,100,TO_ORG_CODE;申请原因,100,REASON_CHN_DESC;申请日期,120;入库单号,100;入库日期,120;入库人员,120;备注,120;急,30,boolean;状态,80,UPDATE_FLGTABLE_M.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;11,leftTABLE_M.LockColumns=0,1,2,3,4,5,6,7,8,9,10,11TABLE_M.Item=REQTYPE_CODE;APP_ORG_CODE;TO_ORG_CODE;REASON_CHN_DESC;UPDATE_FLGTABLE_M.ParmMap=REQTYPE_CODE;REQUEST_NO;APP_ORG_CODE;TO_ORG_CODE;REASON_CHN_DESC;REQUEST_DATE;DISPENSE_NO;WAREHOUSING_DATE;WAREHOUSING_USER;DESCRIPTION;URGENT_FLG;UPDATE_FLGTABLE_M.ClickedAction=onTableMClickedtPanel_1.Type=TPaneltPanel_1.X=5tPanel_1.Y=5tPanel_1.Width=1193tPanel_1.Height=121tPanel_1.Border=组tPanel_1.AutoX=YtPanel_1.AutoY=YtPanel_1.AutoWidth=YtPanel_1.Item=tLabel_6;UPDATE_FLG_A;UPDATE_FLG_B;tLabel_15;START_DATE;tLabel_0;END_DATE;tLabel_3;tLabel_4;tLabel_5;tLabel_7;tLabel_8;tLabel_11;URGENT_FLG;tLabel_12;tLabel_13;REQUEST_NO;REQUEST_DATE;TO_ORG_CODE;REQTYPE_CODE;WAREHOUSING_DATE;REASON_CHN_DESC;DESCRIPTION;tLabel_19;DISPENSE_NO;UPDATE_FLG;PHA_TYPE;UNIT;tLabel_1;G_DRUGS;N_DRUGS;tLabel_9;APP_ALL;APP_ARTIFICIAL;APP_PLE;APP_AUTO;APP_ORG_CODE;ALLALL.Type=TRadioButtonALL.X=950ALL.Y=3ALL.Width=55ALL.Height=23ALL.Text=不分ALL.Group=NARAPP_ORG_CODE.Type=药房下拉列表APP_ORG_CODE.X=368APP_ORG_CODE.Y=32APP_ORG_CODE.Width=130APP_ORG_CODE.Height=23APP_ORG_CODE.Text=TButtonAPP_ORG_CODE.showID=YAPP_ORG_CODE.showName=YAPP_ORG_CODE.showText=NAPP_ORG_CODE.showValue=NAPP_ORG_CODE.showPy1=YAPP_ORG_CODE.showPy2=YAPP_ORG_CODE.Editable=YAPP_ORG_CODE.Tip=药房APP_ORG_CODE.TableShowList=nameAPP_ORG_CODE.ModuleParmTag=APP_AUTO.Type=TRadioButtonAPP_AUTO.X=1045APP_AUTO.Y=29APP_AUTO.Width=81APP_AUTO.Height=23APP_AUTO.Text=自动拨补APP_AUTO.Group=APPLYAPP_PLE.Type=TRadioButtonAPP_PLE.X=950APP_PLE.Y=30APP_PLE.Width=81APP_PLE.Height=23APP_PLE.Text=请领建议APP_PLE.Group=APPLYAPP_ARTIFICIAL.Type=TRadioButtonAPP_ARTIFICIAL.X=886APP_ARTIFICIAL.Y=30APP_ARTIFICIAL.Width=54APP_ARTIFICIAL.Height=23APP_ARTIFICIAL.Text=人工APP_ARTIFICIAL.Group=APPLYAPP_ALL.Type=TRadioButtonAPP_ALL.X=819APP_ALL.Y=30APP_ALL.Width=58APP_ALL.Height=23APP_ALL.Text=全部APP_ALL.Group=APPLYAPP_ALL.Selected=YtLabel_9.Type=TLabeltLabel_9.X=749tLabel_9.Y=34tLabel_9.Width=72tLabel_9.Height=15tLabel_9.Text=申请方式：tLabel_9.Color=蓝N_DRUGS.Type=TRadioButtonN_DRUGS.X=885N_DRUGS.Y=4N_DRUGS.Width=62N_DRUGS.Height=23N_DRUGS.Text=麻精N_DRUGS.Group=NARG_DRUGS.Type=TRadioButtonG_DRUGS.X=819G_DRUGS.Y=3G_DRUGS.Width=61G_DRUGS.Height=23G_DRUGS.Text=普药G_DRUGS.Group=NARG_DRUGS.Selected=YtLabel_1.Type=TLabeltLabel_1.X=750tLabel_1.Y=9tLabel_1.Width=72tLabel_1.Height=15tLabel_1.Text=药品种类：tLabel_1.Color=蓝UNIT.Type=计量单位下拉列表UNIT.X=574UNIT.Y=140UNIT.Width=10UNIT.Height=23UNIT.Text=TButtonUNIT.showID=YUNIT.showName=YUNIT.showText=NUNIT.showValue=NUNIT.showPy1=NUNIT.showPy2=NUNIT.Editable=YUNIT.Tip=计量单位UNIT.TableShowList=namePHA_TYPE.Type=TComboBoxPHA_TYPE.X=730PHA_TYPE.Y=144PHA_TYPE.Width=10PHA_TYPE.Height=23PHA_TYPE.Text=TButtonPHA_TYPE.showID=YPHA_TYPE.Editable=YPHA_TYPE.StringData=[[id,name],[,],[W,西药],[C,中成药],[G,中草药]]PHA_TYPE.ShowText=NPHA_TYPE.ShowName=YPHA_TYPE.TableShowList=nameUPDATE_FLG.Type=TComboBoxUPDATE_FLG.X=544UPDATE_FLG.Y=140UPDATE_FLG.Width=10UPDATE_FLG.Height=23UPDATE_FLG.Text=TButtonUPDATE_FLG.showID=YUPDATE_FLG.Editable=YUPDATE_FLG.StringData=[[id,name],[,],[0,申请],[1,在途],[2,取消],[3,完成]]UPDATE_FLG.ShowName=YUPDATE_FLG.ShowText=NUPDATE_FLG.TableShowList=nameDISPENSE_NO.Type=TTextFieldDISPENSE_NO.X=825DISPENSE_NO.Y=64DISPENSE_NO.Width=120DISPENSE_NO.Height=20DISPENSE_NO.Text=DISPENSE_NO.Enabled=NtLabel_19.Type=TLabeltLabel_19.X=750tLabel_19.Y=65tLabel_19.Width=72tLabel_19.Height=15tLabel_19.Text=入库单号:tLabel_19.Color=blueDESCRIPTION.Type=TTextFieldDESCRIPTION.X=368DESCRIPTION.Y=93DESCRIPTION.Width=354DESCRIPTION.Height=20DESCRIPTION.Text=REASON_CHN_DESC.Type=TComboBoxREASON_CHN_DESC.X=601REASON_CHN_DESC.Y=61REASON_CHN_DESC.Width=120REASON_CHN_DESC.Height=23REASON_CHN_DESC.Text=TButtonREASON_CHN_DESC.showID=YREASON_CHN_DESC.Editable=YREASON_CHN_DESC.ExpandWidth=30REASON_CHN_DESC.Enabled=NWAREHOUSING_DATE.Type=TTextFormatWAREHOUSING_DATE.X=94WAREHOUSING_DATE.Y=93WAREHOUSING_DATE.Width=160WAREHOUSING_DATE.Height=20WAREHOUSING_DATE.Text=WAREHOUSING_DATE.showDownButton=YWAREHOUSING_DATE.Format=yyyy/MM/dd HH:mm:ssWAREHOUSING_DATE.FormatType=dateWAREHOUSING_DATE.HorizontalAlignment=2REQTYPE_CODE.Type=TComboBoxREQTYPE_CODE.X=601REQTYPE_CODE.Y=32REQTYPE_CODE.Width=120REQTYPE_CODE.Height=23REQTYPE_CODE.Text=TButtonREQTYPE_CODE.showID=YREQTYPE_CODE.Editable=YREQTYPE_CODE.ShowText=NREQTYPE_CODE.ShowName=YREQTYPE_CODE.TableShowList=nameREQTYPE_CODE.ExpandWidth=30REQTYPE_CODE.StringData=[[id,name],[,],[DEP,部门请领],[TEC,备药生成],[EXM,补充计费],[GIF,药房调拨],[RET,退库],[WAS,损耗],[THO,其它出库],[COS,卫耗材领用],[THI,其它入库],[ATO,自动拨补]]TO_ORG_CODE.Type=药房下拉列表TO_ORG_CODE.X=368TO_ORG_CODE.Y=61TO_ORG_CODE.Width=120TO_ORG_CODE.Height=23TO_ORG_CODE.Text=TButtonTO_ORG_CODE.showID=YTO_ORG_CODE.showName=YTO_ORG_CODE.showText=NTO_ORG_CODE.showValue=NTO_ORG_CODE.showPy1=NTO_ORG_CODE.showPy2=NTO_ORG_CODE.Editable=YTO_ORG_CODE.Tip=药房TO_ORG_CODE.TableShowList=nameTO_ORG_CODE.ModuleParmTag=TO_ORG_CODE.ExpandWidth=30TO_ORG_CODE.Enabled=NREQUEST_DATE.Type=TTextFormatREQUEST_DATE.X=94REQUEST_DATE.Y=61REQUEST_DATE.Width=160REQUEST_DATE.Height=20REQUEST_DATE.Text=REQUEST_DATE.HorizontalAlignment=2REQUEST_DATE.FormatType=dateREQUEST_DATE.Format=yyyy/MM/dd HH:mm:ssREQUEST_DATE.showDownButton=YREQUEST_DATE.Enabled=NREQUEST_NO.Type=TTextFieldREQUEST_NO.X=94REQUEST_NO.Y=34REQUEST_NO.Width=160REQUEST_NO.Height=20REQUEST_NO.Text=tLabel_13.Type=TLabeltLabel_13.X=295tLabel_13.Y=96tLabel_13.Width=72tLabel_13.Height=15tLabel_13.Text=备    注:tLabel_12.Type=TLabeltLabel_12.X=528tLabel_12.Y=65tLabel_12.Width=72tLabel_12.Height=15tLabel_12.Text=申请原因:URGENT_FLG.Type=TCheckBoxURGENT_FLG.X=748URGENT_FLG.Y=92URGENT_FLG.Width=81URGENT_FLG.Height=23URGENT_FLG.Text=急件注记URGENT_FLG.Enabled=NtLabel_11.Type=TLabeltLabel_11.X=17tLabel_11.Y=96tLabel_11.Width=72tLabel_11.Height=15tLabel_11.Text=入库日期:tLabel_8.Type=TLabeltLabel_8.X=295tLabel_8.Y=65tLabel_8.Width=72tLabel_8.Height=15tLabel_8.Text=接受部门:tLabel_7.Type=TLabeltLabel_7.X=17tLabel_7.Y=65tLabel_7.Width=72tLabel_7.Height=15tLabel_7.Text=申请日期:tLabel_5.Type=TLabeltLabel_5.X=528tLabel_5.Y=36tLabel_5.Width=72tLabel_5.Height=15tLabel_5.Text=单号类别:tLabel_5.Color=bluetLabel_4.Type=TLabeltLabel_4.X=295tLabel_4.Y=36tLabel_4.Width=72tLabel_4.Height=15tLabel_4.Text=申请部门:tLabel_4.Color=bluetLabel_3.Type=TLabeltLabel_3.X=17tLabel_3.Y=36tLabel_3.Width=72tLabel_3.Height=15tLabel_3.Text=申请单号:tLabel_3.Color=blueEND_DATE.Type=TTextFormatEND_DATE.X=561END_DATE.Y=7END_DATE.Width=160END_DATE.Height=20END_DATE.Text=END_DATE.HorizontalAlignment=2END_DATE.showDownButton=YEND_DATE.Format=yyyy/MM/dd HH:mm:ssEND_DATE.FormatType=datetLabel_0.Type=TLabeltLabel_0.X=535tLabel_0.Y=10tLabel_0.Width=20tLabel_0.Height=15tLabel_0.Text=～tLabel_0.HorizontalAlignment=0START_DATE.Type=TTextFormatSTART_DATE.X=368START_DATE.Y=7START_DATE.Width=160START_DATE.Height=20START_DATE.Text=START_DATE.HorizontalAlignment=2START_DATE.showDownButton=YSTART_DATE.Format=yyyy/MM/dd HH:mm:ssSTART_DATE.FormatType=datetLabel_15.Type=TLabeltLabel_15.X=295tLabel_15.Y=10tLabel_15.Width=72tLabel_15.Height=15tLabel_15.Text=查询区间:tLabel_15.Color=blueUPDATE_FLG_B.Type=TRadioButtonUPDATE_FLG_B.X=177UPDATE_FLG_B.Y=5UPDATE_FLG_B.Width=70UPDATE_FLG_B.Height=23UPDATE_FLG_B.Text=未入库UPDATE_FLG_B.Group=group1UPDATE_FLG_B.Selected=YUPDATE_FLG_B.Action=onChangeSelectActionUPDATE_FLG_A.Type=TRadioButtonUPDATE_FLG_A.X=93UPDATE_FLG_A.Y=5UPDATE_FLG_A.Width=55UPDATE_FLG_A.Height=23UPDATE_FLG_A.Text=完成UPDATE_FLG_A.Group=group1UPDATE_FLG_A.Action=onChangeSelectActiontLabel_6.Type=TLabeltLabel_6.X=17tLabel_6.Y=10tLabel_6.Width=72tLabel_6.Height=15tLabel_6.Text=入库状态:tLabel_6.Color=blue