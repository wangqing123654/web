## TBuilder Config File ## Title:## Company:JavaHis## Author:Yuanxm 2012.10.11## version 1.0#<Type=TFrame><<<<<<< .mineUI.Title=药房麻精入智能柜=======UI.Title=麻精入智能柜>>>>>>> .r11789UI.MenuConfig=%ROOT%\config\spc\SPCStatioaInStoreMenu.xUI.Width=800UI.Height=600UI.toolbar=YUI.controlclassname=com.javahis.ui.spc.SPCStatioaInStoreControlUI.item=tPanel_1;tTabbedPane_0UI.layout=nullUI.TopMenu=YUI.TopToolBar=YtTabbedPane_0.Type=TTabbedPanetTabbedPane_0.X=7tTabbedPane_0.Y=110tTabbedPane_0.Width=788tTabbedPane_0.Height=622tTabbedPane_0.AutoWidth=YtTabbedPane_0.AutoHeight=YtTabbedPane_0.Item=PANEL_N;PANEL_YtTabbedPane_0.ChangedAction=onStockInQueryPANEL_Y.Type=TPanelPANEL_Y.X=60PANEL_Y.Y=8PANEL_Y.Width=81PANEL_Y.Height=81PANEL_Y.Name=已入库PANEL_Y.Item=tPanel_5;tPanel_0tPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=147tPanel_0.Width=773tPanel_0.Height=159tPanel_0.Border=组|明细信息tPanel_0.AutoWidth=YtPanel_0.Item=TABLE_D_YTABLE_D_Y.Type=TTableTABLE_D_Y.X=11TABLE_D_Y.Y=19TABLE_D_Y.Width=751TABLE_D_Y.Height=124TABLE_D_Y.SpacingRow=1TABLE_D_Y.RowHeight=20TABLE_D_Y.AutoX=YTABLE_D_Y.AutoY=YTABLE_D_Y.AutoWidth=YTABLE_D_Y.AutoHeight=YTABLE_D_Y.Header=序号,60,容器,150;麻精药流水号,130;批号,100;效期,100;单位,100TABLE_D_Y.ParmMap=ROWNUM;TOXIC_ID;BATCH_NO;VALID_DATE;UNIT;CONTAINER_DESC;SPECIFICATION;ORDER_DESCTABLE_D_Y.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,lefttPanel_5.Type=TPaneltPanel_5.X=5tPanel_5.Y=5tPanel_5.Width=773tPanel_5.Height=145tPanel_5.Border=组|基本信息tPanel_5.AutoWidth=YtPanel_5.AutoX=YtPanel_5.AutoY=YtPanel_5.Item=TABLE_M_YTABLE_M_Y.Type=TTableTABLE_M_Y.X=12TABLE_M_Y.Y=20TABLE_M_Y.Width=751TABLE_M_Y.Height=110TABLE_M_Y.SpacingRow=1TABLE_M_Y.RowHeight=20TABLE_M_Y.AutoX=YTABLE_M_Y.AutoY=YTABLE_M_Y.AutoHeight=YTABLE_M_Y.AutoWidth=YTABLE_M_Y.ParmMap=CONTAINER_ID;CONTAINER_DESC;ORDER_DESC;SPECIFICATION;SJ_QTY;DISPENSE_NO;DISPENSE_SEQ_NOTABLE_M_Y.Header=容器编号,100;容器名称,200;药品名称,200;规格,120;数量,70,double,####0.000TABLE_M_Y.ClickedAction=onTableMClicked1TABLE_M_Y.LockColumns=ALLTABLE_M_Y.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,rightPANEL_N.Type=TPanelPANEL_N.X=35PANEL_N.Y=2PANEL_N.Width=81PANEL_N.Height=81PANEL_N.Text=PANEL_N.Name=未入库PANEL_N.Item=tPanel_3;tPanel_6tPanel_6.Type=TPaneltPanel_6.X=5tPanel_6.Y=147tPanel_6.Width=773tPanel_6.Height=159tPanel_6.Border=组|明细信息tPanel_6.AutoWidth=YtPanel_6.Item=TABLE_D_NTABLE_D_N.Type=TTableTABLE_D_N.X=11TABLE_D_N.Y=19TABLE_D_N.Width=751TABLE_D_N.Height=124TABLE_D_N.SpacingRow=1TABLE_D_N.RowHeight=20TABLE_D_N.Header=序号,60,容器,150;麻精药流水号,130;批号,100;效期,100;单位,100TABLE_D_N.ParmMap=ROWNUM;TOXIC_ID;BATCH_NO;VALID_DATE;UNIT;CONTAINER_DESC;SPECIFICATION;ORDER_DESCTABLE_D_N.AutoX=YTABLE_D_N.AutoY=YTABLE_D_N.AutoWidth=YTABLE_D_N.AutoHeight=YTABLE_D_N.Visible=YTABLE_D_N.LockColumns=ALLTABLE_D_N.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,lefttPanel_3.Type=TPaneltPanel_3.X=5tPanel_3.Y=5tPanel_3.Width=773tPanel_3.Height=145tPanel_3.Border=组|基本信息tPanel_3.AutoX=YtPanel_3.AutoY=YtPanel_3.AutoWidth=YtPanel_3.Item=TABLE_M_NTABLE_M_N.Type=TTableTABLE_M_N.X=11TABLE_M_N.Y=20TABLE_M_N.Width=751TABLE_M_N.Height=110TABLE_M_N.SpacingRow=1TABLE_M_N.RowHeight=20TABLE_M_N.Header=容器编号,100;容器名称,200;药品名称,200;规格,120;数量,70,double,####0.000TABLE_M_N.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,rightTABLE_M_N.ParmMap=CONTAINER_ID;CONTAINER_DESC;ORDER_DESC;SPECIFICATION;SJ_QTY;DISPENSE_NO;DISPENSE_SEQ_NOTABLE_M_N.AutoX=YTABLE_M_N.AutoWidth=YTABLE_M_N.AutoY=YTABLE_M_N.AutoHeight=YTABLE_M_N.ClickedAction=onTableMClickedTABLE_M_N.LockRows=TABLE_M_N.LockColumns=ALLtPanel_1.Type=TPaneltPanel_1.X=5tPanel_1.Y=6tPanel_1.Width=791tPanel_1.Height=103tPanel_1.Border=组|操作信息tPanel_1.Item=tLabel_20;ORG_CODE;tLabel_12;DISPENSE_NO;tLabel_18;CABINET_ID;tLabel_19;CABINET_DESC;tLabel_21;CABINET_IP;tLabel_0;ORDER_TYPE;tLabel_1;BOX_ESL_IDtPanel_1.Title=tPanel_1.AutoWidth=YtPanel_1.AutoHeight=NBOX_ESL_ID.Type=TTextFieldBOX_ESL_ID.X=360BOX_ESL_ID.Y=47BOX_ESL_ID.Width=144BOX_ESL_ID.Height=20BOX_ESL_ID.Text=BOX_ESL_ID.Action=DisClickBOX_ESL_ID.Enabled=YtLabel_1.Type=TLabeltLabel_1.X=261tLabel_1.Y=46tLabel_1.Width=68tLabel_1.Height=22tLabel_1.Text=周转箱：tLabel_1.Color=bluetLabel_1.FontSize=17ORDER_TYPE.Type=TTextFieldORDER_TYPE.X=110ORDER_TYPE.Y=72ORDER_TYPE.Width=135ORDER_TYPE.Height=20ORDER_TYPE.Text=ORDER_TYPE.Enabled=NtLabel_0.Type=TLabeltLabel_0.X=12tLabel_0.Y=74tLabel_0.Width=88tLabel_0.Height=17tLabel_0.Text=单据类别：tLabel_0.FontSize=17CABINET_IP.Type=TTextFieldCABINET_IP.X=608CABINET_IP.Y=22CABINET_IP.Width=139CABINET_IP.Height=21CABINET_IP.Text=CABINET_IP.Enabled=NtLabel_21.Type=TLabeltLabel_21.X=524tLabel_21.Y=21tLabel_21.Width=90tLabel_21.Height=16tLabel_21.Text=智能柜IP：tLabel_21.FontSize=17CABINET_DESC.Type=TTextFieldCABINET_DESC.X=360CABINET_DESC.Y=22CABINET_DESC.Width=144CABINET_DESC.Height=20CABINET_DESC.Text=CABINET_DESC.Enabled=NtLabel_19.Type=TLabeltLabel_19.X=261tLabel_19.Y=21tLabel_19.Width=107tLabel_19.Height=23tLabel_19.Text=智能柜名称：tLabel_19.FontSize=17CABINET_ID.Type=TTextFieldCABINET_ID.X=110CABINET_ID.Y=22CABINET_ID.Width=135CABINET_ID.Height=20CABINET_ID.Text=CABINET_ID.Enabled=NCABINET_ID.Visible=YtLabel_18.Type=TLabeltLabel_18.X=11tLabel_18.Y=21tLabel_18.Width=105tLabel_18.Height=19tLabel_18.Text=智能柜编号：tLabel_18.FontSize=17DISPENSE_NO.Type=TTextFieldDISPENSE_NO.X=110DISPENSE_NO.Y=47DISPENSE_NO.Width=135DISPENSE_NO.Height=20DISPENSE_NO.Text=DISPENSE_NO.Enabled=YDISPENSE_NO.Action=DisClicktLabel_12.Type=TLabeltLabel_12.X=11tLabel_12.Y=48tLabel_12.Width=91tLabel_12.Height=17tLabel_12.Text=出库单号：tLabel_12.Color=蓝tLabel_12.FontSize=17ORG_CODE.Type=TTextFieldORG_CODE.X=608ORG_CODE.Y=47ORG_CODE.Width=139ORG_CODE.Height=20ORG_CODE.Text=ORG_CODE.Visible=YORG_CODE.Enabled=NORG_CODE.Action=conClicktLabel_20.Type=TLabeltLabel_20.X=523tLabel_20.Y=48tLabel_20.Width=87tLabel_20.Height=15tLabel_20.Text=出库部门：tLabel_20.FontSize=17