## TBuilder Config File ## Title:## Company:JavaHis## Author:zhangy 2010.09.16## version 1.0#<Type=TFrame>UI.Title=住院医保对账UI.MenuConfig=%ROOT%\config\ins\INSOdiCheckAccntMenu.xUI.Width=1490UI.Height=1490UI.toolbar=YUI.controlclassname=com.javahis.ui.ins.INSOdiCheckAccntControlUI.item=tLabel_2;START_DATE;tLabel_0;END_DATE;tPanel_0;tPanel_1;tLabel_1;CTZ_CODEUI.layout=nullUI.ShowTitle=NUI.TopToolBar=YUI.TopMenu=YUI.Y=5UI.AutoY=YUI.X=5UI.AutoX=YUI.AutoWidth=YUI.AutoHeight=YUI.AutoW=YUI.AutoH=YCTZ_CODE.Type=TComboBoxCTZ_CODE.X=90CTZ_CODE.Y=10CTZ_CODE.Width=81CTZ_CODE.Height=23CTZ_CODE.Text=TButtonCTZ_CODE.showID=YCTZ_CODE.Editable=YCTZ_CODE.StringData=[[id,text],[,],[1,城职],[2,城居]]CTZ_CODE.TableShowList=texttLabel_1.Type=TLabeltLabel_1.X=19tLabel_1.Y=16tLabel_1.Width=72tLabel_1.Height=15tLabel_1.Text=人群类别:tLabel_1.Color=蓝tPanel_1.Type=TPaneltPanel_1.X=5tPanel_1.Y=341tPanel_1.Width=1480tPanel_1.Height=1144tPanel_1.AutoX=YtPanel_1.AutoWidth=YtPanel_1.AutoHeight=YtPanel_1.AutoY=NtPanel_1.AutoW=NtPanel_1.AutoH=NtPanel_1.Border=组|中心端数据tPanel_1.Item=TABLE2TABLE2.Type=TTableTABLE2.X=38TABLE2.Y=24TABLE2.Width=1458TABLE2.Height=1109TABLE2.SpacingRow=1TABLE2.RowHeight=20TABLE2.AutoX=YTABLE2.AutoY=YTABLE2.AutoHeight=YTABLE2.AutoWidth=YTABLE2.Header=就诊序号,100;资格确认书号,100;期号,100;姓名,100;人员类别,100;支付类别,100;发生金额,100,double,#########0.00;申报金额,100,double,#########0.00;全自费金额,100,double,#########0.00;增负金额,100,double,#########0.00;个人账户支付金额,130,double,#########0.00TABLE2.LockColumns=allTABLE2.ParmMap=ADM_SEQ;CONFIRM_NO;ISSUE;NAME;CTZ1_CODE;PAY_TYPE;TOTAL_AMT;TOTAL_NHI_AMT;OWN_AMT;ADDPAY_AMT;ACCOUNT_PAY_AMTTABLE2.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,right;7,right;8,right;9,right;10,righttPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=40tPanel_0.Width=1480tPanel_0.Height=299tPanel_0.Border=组|本地数据tPanel_0.AutoX=YtPanel_0.AutoWidth=YtPanel_0.Item=TABLE1TABLE1.Type=TTableTABLE1.X=10TABLE1.Y=25TABLE1.Width=1458TABLE1.Height=264TABLE1.SpacingRow=1TABLE1.RowHeight=20TABLE1.AutoX=YTABLE1.AutoY=YTABLE1.AutoWidth=YTABLE1.AutoHeight=YTABLE1.Header=就诊序号,100;资格确认书号,100;期号,100;姓名,100;人员类别,100;就医类别,100;发生金额,100,double,######0.00;申报金额,100,double,######0.00;全自费金额,100,double,######0.00;增付金额,100,double,######0.00TABLE1.ParmMap=ADM_SEQ;CONFIRM_NO;YEAR_MON;PAT_NAME;CTZ_DESC;CATEGORY_CHN_DESC;HEJI1;HEJI2;OWN_AMT;ADD_AMTTABLE1.LockRows=TABLE1.LockColumns=allTABLE1.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,right;7,right;8,right;9,right;10,rightEND_DATE.Type=TTextFormatEND_DATE.X=527END_DATE.Y=12END_DATE.Width=114END_DATE.Height=20END_DATE.Text=END_DATE.FormatType=dateEND_DATE.Format=yyyy/MM/ddEND_DATE.showDownButton=YEND_DATE.Action=tLabel_0.Type=TLabeltLabel_0.X=454tLabel_0.Y=16tLabel_0.Width=72tLabel_0.Height=15tLabel_0.Text=结束时间:tLabel_0.Color=蓝START_DATE.Type=TTextFormatSTART_DATE.X=304START_DATE.Y=12START_DATE.Width=114START_DATE.Height=20START_DATE.Text=TTextFormatSTART_DATE.FormatType=dateSTART_DATE.Format=yyyy/MM/ddSTART_DATE.showDownButton=YSTART_DATE.Action=tLabel_2.Type=TLabeltLabel_2.X=231tLabel_2.Y=16tLabel_2.Width=72tLabel_2.Height=15tLabel_2.Text=开始时间:tLabel_2.Color=蓝