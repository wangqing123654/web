<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;separate;print;export;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu  
File.Text=文件
File.M=F
File.Item=query;|;Refresh;|;separate;|;print;|;export;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

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

separate.Type=TMenuItem
separate.Text=分册
separate.Tip=分册
separate.M=
separate.key=
separate.Action=onSeparate
separate.pic=012.gif

export.Type=TMenuItem
export.Text=汇出Excel
export.Tip=汇出Excel
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=exportexcel.gif