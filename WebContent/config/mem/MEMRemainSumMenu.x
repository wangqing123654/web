#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:sunqy 2014.05.12
#
# version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;print;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;print;|;export;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(query)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif


Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(clear)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif



close.Type=TMenuItem
close.Text=退出
close.Tip=退出(close)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

print.Type=TMenuItem
print.Text=打印
print.zhText=打印
print.enText=Print
print.Tip=打印(Ctrl+P)
print.zhTip=打印
print.enTip=Print
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif