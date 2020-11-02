<Type=TMenuBar>
UI.Item=File;Window
UI.button=report;unReport;|;query;|;delete;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=report;unReport;|;query;|;delete;|;export;|;clear;|;close

report.Type=TMenuItem
report.Text=已上报
report.Tip=已上报
report.M=R
report.key=Ctrl+R
report.Action=onReport
report.pic=032.gif

unReport.Type=TMenuItem
unReport.Text=取消已上报
unReport.Tip=取消已上报
unReport.M=R
unReport.key=Ctrl+R
unReport.Action=onUnReport
unReport.pic=Undo.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=D
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

export.Type=TMenuItem
export.Text=导出
export.Tip=导出
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.key=Ctrl+Q
clear.Action=onClear
clear.pic=clear.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
