<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;save;separate;merge;print;printSmall;clear;|;export;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu  
File.Text=文件
File.M=F
File.Item=query;|save;|;Refresh;|merge;|;separate;|;print;|;printSmall;|;clear;|;export;|;close

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

merge.Type=TMenuItem
merge.Text=合并
merge.Tip=合并
merge.M=
merge.key=
merge.Action=onMerge
merge.pic=mro.gif

print.Type=TMenuItem
print.Text=补打案卷册号(大)
print.Tip=补打案卷册号(大)
print.M=
print.key=
print.Action=onPrint
print.pic=barcode.gif

printSmall.Type=TMenuItem
printSmall.Text=病历标签
printSmall.Tip=病历标签
printSmall.M=
printSmall.key=
printSmall.Action=onPrintSmall
printSmall.pic=barcode.gif

export.Type=TMenuItem
export.Text=导出Excel
export.Tip=导出Excel
export.M=
export.key=
export.Action=onExport
export.pic=exportexcel.gif