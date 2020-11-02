<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;print;printSum;|;printLumpwork;|;execl;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;clear;|;print;printSum;|;printLumpwork;|;execl;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
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
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

printSum.Type=TMenuItem
printSum.Text=汇总打印
printSum.Tip=汇总打印
printSum.M=Q
printSum.key=Ctrl+Q
printSum.Action=onPrintSum
printSum.pic=print-2.gif

printLumpwork.Type=TMenuItem
printLumpwork.Text=套餐台账打印
printLumpwork.Tip=套餐台账打印
printLumpwork.M=
printLumpwork.key=
printLumpwork.Action=onPrintLumpwork
printLumpwork.pic=print-1.gif

execl.Type=TMenuItem
execl.Text=导出Excel
execl.Tip=导出Excel
execl.M=
execl.key=
execl.Action=onExport
execl.pic=export.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
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
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

execl.Type=TMenuItem
execl.Text=导出Excel
execl.Tip=导出Excel
execl.M=
execl.key=
execl.Action=onExport
execl.pic=export.gif
