<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;printReview;|;clear;print;|;export;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;Refresh;|;printReview;|;clear;|;print;|;export;|;close

save.Type=TMenuItem
save.Text=日结
save.Tip=日结
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

printReview.Type=TMenuItem
printReview.Text=打印预览
printReview.Tip=打印预览
printReview.M=F
printReview.key=Ctrl+R
printReview.Action=onPrintReview
printReview.pic=print-1.gif

print.Type=TMenuItem
print.Text=补印
print.Tip=补印
print.M=P
print.key=
print.Action=onPrint
print.pic=print.gif

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif

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

