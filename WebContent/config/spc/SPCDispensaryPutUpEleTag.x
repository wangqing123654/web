## TBuilder Config File ## Title:药房普药上架操作UI## Company:JavaHis## Author:wangzhilei 2012.10.15## version 1.0#<Type=TFrame>UI.Title=普药上架UI.MenuConfig=%ROOT%\config\spc\SPCDispensaryPutUpEleTagMenu.xUI.Width=1024UI.Height=676UI.toolbar=YUI.controlclassname=com.javahis.ui.spc.SPCDispensaryPutUpEleTagControlUI.item=tPanel_0;tTabbedPane_1UI.layout=nullUI.TopMenu=YUI.TopToolBar=YtTabbedPane_1.Type=TTabbedPanetTabbedPane_1.X=6tTabbedPane_1.Y=81tTabbedPane_1.Width=1014tTabbedPane_1.Height=573tTabbedPane_1.AutoY=NtTabbedPane_1.AutoWidth=YtTabbedPane_1.AutoHeight=YtTabbedPane_1.AutoW=NtTabbedPane_1.AutoH=NtTabbedPane_1.AutoX=YtTabbedPane_1.Item=N_PANEL;Y_PANELtTabbedPane_1.ChangedAction=onTPanlClickY_PANEL.Type=TPanelY_PANEL.X=72Y_PANEL.Y=8Y_PANEL.Width=81Y_PANEL.Height=81Y_PANEL.Name=已上架Y_PANEL.Item=TABLE_YTABLE_Y.Type=TTableTABLE_Y.X=5TABLE_Y.Y=9TABLE_Y.Width=999TABLE_Y.Height=551TABLE_Y.SpacingRow=1TABLE_Y.RowHeight=20TABLE_Y.Header=药品名称,200;规格,120;批号,100;效期,100;数量,70;单位,70,UNIT_CODE;料位,150TABLE_Y.AutoX=YTABLE_Y.AutoY=YTABLE_Y.AutoWidth=YTABLE_Y.AutoHeight=YTABLE_Y.ParmMap=ORDER_DESC;SPECIFICATION;BATCH_NO;VALID_DATE;QTY;UNIT_CHN_DESC;MATERIAL_LOC_CODE;ORDER_CODE;ELETAG_CODE;SEQ_NOTABLE_Y.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,right;5,left;6,leftN_PANEL.Type=TPanelN_PANEL.X=29N_PANEL.Y=9N_PANEL.Width=81N_PANEL.Height=81N_PANEL.Name=未上架N_PANEL.Item=TABLE_NTABLE_N.Type=TTableTABLE_N.X=4TABLE_N.Y=5TABLE_N.Width=999TABLE_N.Height=551TABLE_N.SpacingRow=1TABLE_N.RowHeight=20TABLE_N.AutoX=YTABLE_N.AutoWidth=YTABLE_N.AutoHeight=YTABLE_N.AutoY=YTABLE_N.Header=药品名称,200;规格,120;批号,100;效期,100;数量,70;单位,70,UNIT_CODE;料位,150TABLE_N.ParmMap=ORDER_DESC;SPECIFICATION;BATCH_NO;VALID_DATE;QTY;UNIT_CHN_DESC;MATERIAL_LOC_CODE;ORDER_CODE;ELETAG_CODE;SEQ_NO;DISPENSE_NO;ORG_CODE;ACTUAL_QTY;STOCK_QTY;OUT_QTY;UNIT_CODE;STOCK_PRICE;STOCK_ATM;RETAIL_PRICE;RETAIL_ATM;PHA_TYPE;SUP_CODETABLE_N.LockColumns=allTABLE_N.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,right;5,left;6,lefttPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=10tPanel_0.Width=1014tPanel_0.Height=66tPanel_0.Border=组tPanel_0.AutoX=YtPanel_0.AutoWidth=YtPanel_0.Item=tLabel_0;BOX_ESL_ID;tLabel_2;ELETAG_CODE;tLabel_3;tLabel_4;ORG_CHN_DESC;DISPENSE_NO;START_DATE;tLabel_6;END_DATEEND_DATE.Type=TTextFormatEND_DATE.X=899END_DATE.Y=23END_DATE.Width=100END_DATE.Height=20END_DATE.Text=TTextFormatEND_DATE.Format=yyyy/MM/ddEND_DATE.FormatType=dateEND_DATE.showDownButton=YtLabel_6.Type=TLabeltLabel_6.X=879tLabel_6.Y=25tLabel_6.Width=22tLabel_6.Height=15tLabel_6.Text=～START_DATE.Type=TTextFormatSTART_DATE.X=781START_DATE.Y=23START_DATE.Width=92START_DATE.Height=20START_DATE.Text=TTextFormatSTART_DATE.FormatType=dateSTART_DATE.Format=yyyy/MM/ddSTART_DATE.showDownButton=YDISPENSE_NO.Type=TTextFieldDISPENSE_NO.X=273DISPENSE_NO.Y=21DISPENSE_NO.Width=123DISPENSE_NO.Height=20DISPENSE_NO.Text=DISPENSE_NO.Action=onDispenseNoORG_CHN_DESC.Type=TTextFieldORG_CHN_DESC.X=462ORG_CHN_DESC.Y=23ORG_CHN_DESC.Width=113ORG_CHN_DESC.Height=20ORG_CHN_DESC.Text=tLabel_4.Type=TLabeltLabel_4.X=401tLabel_4.Y=24tLabel_4.Width=64tLabel_4.Height=15tLabel_4.Text=出库部门tLabel_3.Type=TLabeltLabel_3.X=205tLabel_3.Y=25tLabel_3.Width=70tLabel_3.Height=15tLabel_3.Text=出库单号：tLabel_3.Color=蓝ELETAG_CODE.Type=TTextFieldELETAG_CODE.X=673ELETAG_CODE.Y=23ELETAG_CODE.Width=102ELETAG_CODE.Height=20ELETAG_CODE.Text=ELETAG_CODE.ClickedAction=ELETAG_CODE.Action=onClickByElectTagstLabel_2.Type=TLabeltLabel_2.X=581tLabel_2.Y=25tLabel_2.Width=94tLabel_2.Height=15tLabel_2.Text=货位电子标签:tLabel_2.Color=黑BOX_ESL_ID.Type=TTextFieldBOX_ESL_ID.X=63BOX_ESL_ID.Y=23BOX_ESL_ID.Width=134BOX_ESL_ID.Height=20BOX_ESL_ID.Text=BOX_ESL_ID.Action=onMonZHOUZXtLabel_0.Type=TLabeltLabel_0.X=5tLabel_0.Y=25tLabel_0.Width=56tLabel_0.Height=15tLabel_0.Text=周转箱：tLabel_0.Color=蓝