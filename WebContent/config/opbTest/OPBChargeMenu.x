# 
#  Title:临床路径溢出原因表
# 
#  Description:临床路径溢出原因表
# 
#  Copyright: Copyright (c) Javahis 2011
# 
#  author pangben 2011.07.07
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;delete;|;EKT;|;operation;|;selFee;|;selRFee;|;clear;|;close;|;

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;delete;|;EKT;|;operation;|;selFee;|;clear;|;close;|;

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif

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

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=N
delete.key=Delete
delete.Action=deleteRow
delete.pic=delete.gif

EKT.Type=TMenuItem
EKT.Text=医疗卡
EKT.Tip=医疗卡
EKT.M=E
EKT.key=Ctrl+E
EKT.Action=onReadCard
EKT.pic=042.gif

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

operation.Type=TMenuItem
operation.Text=计费模板
operation.Tip=计费模板
operation.M=P
operation.Action=onTmplt
operation.pic=operation.gif

selFee.Type=TMenuItem
selFee.Text=费用查询
selFee.Tip=费用查询
selFee.M=IS
selFee.Action=onSelFee
selFee.pic=inscon.gif

selRFee.Type=TMenuItem
selRFee.Text=退费查询
selRFee.Tip=退费查询
selRFee.M=IS
selRFee.Action=onSelReturnFee
selRFee.pic=inscon.gif


