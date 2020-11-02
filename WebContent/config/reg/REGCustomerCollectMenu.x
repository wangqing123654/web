# 
#  Title:北京爱育华妇儿医院客户汇总表Menu
# 
#  Description:北京爱育华妇儿医院客户汇总表Menu
# 
#  Copyright: Copyright (c) Javahis 2009
# 
#  author huzc 20151224
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;printO;|;printI;|;onExportO;|;onExportI;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;printO;|;printI;|;onExportO;|;onExportI;|;clear;|;close



Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif



close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

printO.Type=TMenuItem
printO.Text=门诊打印
printO.Tip=打印
printO.M=P
printO.Action=onPrintO
printO.pic=print.gif

printI.Type=TMenuItem
printI.Text=住院打印
printI.Tip=打印
printI.M=P
printI.Action=onPrintI
printI.pic=print.gif

onExportO.Type=TMenuItem
onExportO.Text=门诊汇出
onExportO.Tip=汇出
onExportO.M=E
onExportO.Action=onExportO
onExportO.pic=045.gif

onExportI.Type=TMenuItem
onExportI.Text=住院汇出
onExportI.Tip=汇出
onExportI.M=E
onExportI.Action=onExportI
onExportI.pic=045.gif