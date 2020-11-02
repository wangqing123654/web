<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;export;|;print;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;clear;export;print;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=S
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

export.Type=TMenuItem
export.Text=导出
export.Tip=导出
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif


print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=B
print.Action=onPrint
print.pic=print.gif


close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif