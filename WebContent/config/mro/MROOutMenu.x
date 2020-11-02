<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;clear;print;export;delete;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu  
File.Text=文件
File.M=F
File.Item=query;|;Refresh;|;clear;|;print;|;export;|;delete;|;close

//save.Type=TMenuItem
//save.Text=保存
//save.Tip=保存(Ctrl+S)
//save.M=S
//save.key=Ctrl+S
//save.Action=onSave
//save.pic=save.gif

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

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.key=
print.Action=onPrint
print.pic=print.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=关闭
close.Tip=关闭
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

delete.Type=TMenuItem
delete.Text=取消出库
delete.Tip=取消出库
delete.M=
delete.key=
delete.Action=onDelete
delete.pic=delete.gif

export.Type=TMenuItem
export.Text=导出Excel
export.Tip=导出Excel
export.M=
export.key=
export.Action=onExport
export.pic=export.gif
