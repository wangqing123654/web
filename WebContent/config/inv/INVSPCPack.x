## TBuilder Config File ## Title:## Company:JavaHis## Author:cwl 2013.07.25## version 1.0#<Type=TFrame>UI.Title=手术包传回UI.MenuConfig=UI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.inv.INVSPCPackControlUI.item=tPanel_2;tPanel_3;TABLEM;TABLED;ALLUI.layout=nullUI.zhTitle=手术包传回UI.enTitle=手术包传回ALL.Type=TCheckBoxALL.X=15ALL.Y=124ALL.Width=81ALL.Height=23ALL.Text=全选ALL.Action=onSelAllTABLED.Type=TTableTABLED.X=13TABLED.Y=150TABLED.Width=1003TABLED.Height=341TABLED.SpacingRow=1TABLED.RowHeight=20TABLED.Header=选,50,boolean;物资名称,200;配置数量,60,double;使用数量,60,double;剩余数量,60,double;单位,50,STOCK_UNIT;,10;选,50,boolean;物资名称,200;配置数量,60,double;使用数量,60,double;剩余数量,60,double;单位,50,STOCK_UNITTABLED.AutoWidth=YTABLED.AutoHeight=YTABLED.ParmMap=N_SEL;N_INV_CHN_DESC;N_QTY;N_USERD_QTY;N_NOTUSED_QTY;N_STOCK_UNIT;BLANK;S_SEL;S_INV_CHN_DESC;S_QTY;S_USERD_QTY;S_NOTUSED_QTY;S_STOCK_UNITTABLED.LockColumns=1,2,5,6,8,9,12TABLED.ColumnHorizontalAlignmentData=1,left;2,right;3,right;4,right;5,left;8,left;9,right;10,right;11,right;12,leftTABLED.Item=OPT_USER;STOCK_UNITTABLEM.Type=TTableTABLEM.X=48TABLEM.Y=570TABLEM.Width=1004TABLEM.Height=263TABLEM.SpacingRow=1TABLEM.RowHeight=20TABLEM.Header=手术包代码,120;包名,180;拼音,70;备注,200;成本价,80,double;灭菌效期,80,int;操作员,100,OPT_USER;操作日期,120;操作终端,100TABLEM.AutoWidth=YTABLEM.ParmMap=PACK_CODE;PACK_DESC;PY1;DESCRIPTION;USE_COST;VALUE_DATE;OPT_USER;OPT_DATE;OPT_TERMTABLEM.LockColumns=ALLTABLEM.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,right;4,left;5,right;6,right;7,left;8,left;9,leftTABLEM.Item=OPT_USERTABLEM.Visible=NtPanel_3.Type=TPaneltPanel_3.X=15tPanel_3.Y=66tPanel_3.Width=1004tPanel_3.Height=52tPanel_3.Border=组 |tPanel_3.AutoWidth=YtPanel_3.Item=tLabel_0;tLabel_6;PACK_CODE;PACK_DESCPACK_DESC.Type=TTextFieldPACK_DESC.X=309PACK_DESC.Y=16PACK_DESC.Width=120PACK_DESC.Height=20PACK_DESC.Text=PACK_CODE.Type=TTextFieldPACK_CODE.X=92PACK_CODE.Y=14PACK_CODE.Width=107PACK_CODE.Height=20PACK_CODE.Text=PACK_CODE.Action=onBarCodetLabel_6.Type=TLabeltLabel_6.X=241tLabel_6.Y=17tLabel_6.Width=72tLabel_6.Height=15tLabel_6.Text=手术包名：tLabel_0.Type=TLabeltLabel_0.X=11tLabel_0.Y=17tLabel_0.Width=86tLabel_0.Height=15tLabel_0.Text=手术包代码：tPanel_2.Type=TPaneltPanel_2.X=14tPanel_2.Y=12tPanel_2.Width=1005tPanel_2.Height=50tPanel_2.Border=组 |tPanel_2.AutoWidth=YtPanel_2.Item=tButton_0;tButton_1;OPT_USER;STOCK_UNITSTOCK_UNIT.Type=计量单位STOCK_UNIT.X=399STOCK_UNIT.Y=7STOCK_UNIT.Width=81STOCK_UNIT.Height=23STOCK_UNIT.Text=STOCK_UNIT.HorizontalAlignment=2STOCK_UNIT.PopupMenuHeader=代码,100;名称,100STOCK_UNIT.PopupMenuWidth=300STOCK_UNIT.PopupMenuHeight=300STOCK_UNIT.PopupMenuFilter=ID,1;NAME,1;PY1,1STOCK_UNIT.FormatType=comboSTOCK_UNIT.ShowDownButton=YSTOCK_UNIT.Tip=计量单位STOCK_UNIT.ShowColumnList=NAMESTOCK_UNIT.Visible=NOPT_USER.Type=人员OPT_USER.X=491OPT_USER.Y=15OPT_USER.Width=81OPT_USER.Height=23OPT_USER.Text=OPT_USER.HorizontalAlignment=2OPT_USER.PopupMenuHeader=代码,100;名称,100OPT_USER.PopupMenuWidth=300OPT_USER.PopupMenuHeight=300OPT_USER.PopupMenuFilter=ID,1;NAME,1;PY1,1OPT_USER.FormatType=comboOPT_USER.ShowDownButton=YOPT_USER.Tip=人员OPT_USER.ShowColumnList=NAMEOPT_USER.Visible=NtButton_1.Type=TButtontButton_1.X=149tButton_1.Y=12tButton_1.Width=81tButton_1.Height=23tButton_1.Text=关闭tButton_1.Action=onCANCLEtButton_0.Type=TButtontButton_0.X=21tButton_0.Y=12tButton_0.Width=81tButton_0.Height=23tButton_0.Text=传回tButton_0.Action=onOK