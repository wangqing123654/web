## TBuilder Config File ## Title:药库下架UI## Company:JavaHis## Author:wangzhilei 2012.10.15## version 1.0#<Type=TFrame>UI.Title=智能柜存货查询UI.MenuConfig=%ROOT%\config\spc\SPCContainerStaticMenu.xUI.Width=811UI.Height=536UI.toolbar=YUI.controlclassname=com.javahis.ui.spc.SPCContainerStaticControlUI.item=tPanel_1;tTabbedPane_0UI.layout=nullUI.TopToolBar=YUI.TopMenu=YUI.FocusList=UI.MoveType=0UI.Text=智能柜存货查询UI.Tip=智能柜存货查询tTabbedPane_0.Type=TTabbedPanetTabbedPane_0.X=4tTabbedPane_0.Y=58tTabbedPane_0.Width=801tTabbedPane_0.Height=473tTabbedPane_0.AutoX=YtTabbedPane_0.AutoY=NtTabbedPane_0.AutoWidth=YtTabbedPane_0.AutoHeight=YtTabbedPane_0.Item=N_PANEL;Y_PANEL;CHECK_PANELtTabbedPane_0.ChangedAction=onTPanlClickedtTabbedPane_0.Name=麻精盘点CHECK_PANEL.Type=TPanelCHECK_PANEL.X=103CHECK_PANEL.Y=3CHECK_PANEL.Width=81CHECK_PANEL.Height=81CHECK_PANEL.Name=麻精盘点CHECK_PANEL.Item=TABLE_CHECK;tButton_0;tButton_1tButton_1.Type=TButtontButton_1.X=132tButton_1.Y=11tButton_1.Width=94tButton_1.Height=23tButton_1.Text=结束盘点tButton_1.Action=endChecktButton_0.Type=TButtontButton_0.X=7tButton_0.Y=12tButton_0.Width=93tButton_0.Height=23tButton_0.Text=开始盘点tButton_0.Action=startCheckTABLE_CHECK.Type=TTableTABLE_CHECK.X=4TABLE_CHECK.Y=43TABLE_CHECK.Width=779TABLE_CHECK.Height=260TABLE_CHECK.SpacingRow=1TABLE_CHECK.RowHeight=20TABLE_CHECK.Header=药品编码,100;药品名称,250;规格,150;单位,100;容器数,50,double,####0.000;现存量,50,double,####0.000;盘盈亏,50,double,####0.000TABLE_CHECK.ParmMap=ORDER_CODE;ORDER_DESC;SPECIFICATION;UNIT_CHN_DESC;CONTAINER_QTY;TOT_QTY;CHECKNUMTABLE_CHECK.LockColumns=ALLTABLE_CHECK.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,right;5,right;6,rightY_PANEL.Type=TPanelY_PANEL.X=70Y_PANEL.Y=11Y_PANEL.Width=1011Y_PANEL.Height=81Y_PANEL.Name=明细Y_PANEL.Item=tPanel_17;tPanel_18Y_PANEL.AutoWidth=YtPanel_18.Type=TPaneltPanel_18.X=423tPanel_18.Y=4tPanel_18.Width=368tPanel_18.Height=435tPanel_18.Border=组|普药tPanel_18.Item=table_Y_RtPanel_18.AutoWidth=YtPanel_18.AutoHeight=Ytable_Y_R.Type=TTabletable_Y_R.X=10table_Y_R.Y=22table_Y_R.Width=347table_Y_R.Height=402table_Y_R.SpacingRow=1table_Y_R.RowHeight=20table_Y_R.AutoWidth=Ytable_Y_R.AutoHeight=Ytable_Y_R.Header=药品名称,120;规格,110;数量,70,double,####0.000;批号,80;效期,90;进货价,80,double,####0.000;单位,40table_Y_R.ParmMap=ORDER_DESC;SPECIFICATION;STOCK_QTY;BATCH_NO;VALID_DATE;VERIFYIN_PRICE;UNIT_CHN_DESCtable_Y_R.ColumnHorizontalAlignmentData=0,left;1,left;2,right;3,left;4,right;5,right;6,lefttable_Y_R.LockRows=table_Y_R.LockColumns=0,1,2,3,4,5,6tPanel_17.Type=TPaneltPanel_17.X=6tPanel_17.Y=4tPanel_17.Width=414tPanel_17.Height=435tPanel_17.Border=组|麻精tPanel_17.Item=table_Y_LtPanel_17.AutoHeight=Ytable_Y_L.Type=TTabletable_Y_L.X=11table_Y_L.Y=20table_Y_L.Width=392table_Y_L.Height=404table_Y_L.SpacingRow=1table_Y_L.RowHeight=20table_Y_L.AutoWidth=Ytable_Y_L.AutoHeight=Ytable_Y_L.Header=容器名称,90;麻精流水号,85;药品名称,140;规格,120;批号,80;效期,90;进货价,80,double,####0.000;单位,40table_Y_L.ParmMap=CONTAINER_DESC;TOXIC_ID;ORDER_DESC;SPECIFICATION;BATCH_NO;VALID_DATE;VERIFYIN_PRICE;UNIT_CHN_DESCtable_Y_L.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,right;7,lefttable_Y_L.LockRows=table_Y_L.LockColumns=0,1,2,3,4,5,6,7N_PANEL.Type=TPanelN_PANEL.X=10N_PANEL.Y=5N_PANEL.Width=1011N_PANEL.Height=572N_PANEL.Name=总计N_PANEL.Item=tPanel_0;tPanel_15N_PANEL.AutoHeight=YN_PANEL.AutoY=NN_PANEL.AutoWidth=YtPanel_15.Type=TPaneltPanel_15.X=381tPanel_15.Y=3tPanel_15.Width=410tPanel_15.Height=436tPanel_15.AutoWidth=YtPanel_15.AutoHeight=YtPanel_15.Border=组|普药tPanel_15.Item=table_N_Rtable_N_R.Type=TTabletable_N_R.X=10table_N_R.Y=26table_N_R.Width=389table_N_R.Height=399table_N_R.SpacingRow=1table_N_R.RowHeight=20table_N_R.AutoWidth=Ytable_N_R.AutoHeight=Ytable_N_R.Header=药品名称,230;规格,100;现存量,60,double,####0.000;单位,60;基数量,60;应补充量,60table_N_R.ParmMap=ORDER_DESC;SPECIFICATION;TOT_QTY;UNIT_CHN_DESC;SAFE_QTY;SUP_QTYtable_N_R.ColumnHorizontalAlignmentData=0,left;1,left;2,right;3,lefttable_N_R.LockColumns=0,1,2tPanel_0.Type=TPaneltPanel_0.X=4tPanel_0.Y=3tPanel_0.Width=374tPanel_0.Height=436tPanel_0.Border=组|麻精tPanel_0.Item=table_N_LtPanel_0.AutoHeight=Ytable_N_L.Type=TTabletable_N_L.X=12table_N_L.Y=23table_N_L.Width=352table_N_L.Height=401table_N_L.SpacingRow=1table_N_L.RowHeight=20table_N_L.AutoX=Ytable_N_L.AutoY=Ytable_N_L.AutoWidth=Ytable_N_L.AutoHeight=Ytable_N_L.Header=药品名称,150;规格,100;容器数,60,double,####0.000;现存量,70,double,####0.000;单位,70;基数量,70;应补充量,70table_N_L.ParmMap=ORDER_DESC;SPECIFICATION;CONTAINER_QTY;TOT_QTY;UNIT_CHN_DESC;SAFE_QTY;SUP_QTYtable_N_L.ColumnHorizontalAlignmentData=0,left;1,left;2,right;3,right;4,left;5,right;6,righttable_N_L.LockColumns=0,1,2,3table_N_L.ColumnSelectionAllowed=NtPanel_1.Type=TPaneltPanel_1.X=5tPanel_1.Y=11tPanel_1.Width=801tPanel_1.Height=41tPanel_1.Border=组tPanel_1.AutoX=YtPanel_1.AutoWidth=YtPanel_1.Item=tLabel_3;CABINET_IDCABINET_ID.Type=物联网智能柜下拉列表CABINET_ID.X=112CABINET_ID.Y=8CABINET_ID.Width=123CABINET_ID.Height=23CABINET_ID.Text=CABINET_ID.HorizontalAlignment=2CABINET_ID.PopupMenuHeader=编码,100;名称,100CABINET_ID.PopupMenuWidth=300CABINET_ID.PopupMenuHeight=300CABINET_ID.PopupMenuFilter=ID,1;NAME,1;PY1,1CABINET_ID.FormatType=comboCABINET_ID.ShowDownButton=YCABINET_ID.Tip=药库CABINET_ID.ShowColumnList=NAMEtLabel_3.Type=TLabeltLabel_3.X=14tLabel_3.Y=11tLabel_3.Width=79tLabel_3.Height=15tLabel_3.Text=智能柜编号:tLabel_3.Color=蓝