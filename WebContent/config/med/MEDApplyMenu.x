# 
#  Title:检验条码打印Menu
# 
#  Description:检验条码打印Menu
# 
#  Copyright: Copyright (c) Javahis 2009
# 
#  author JiaoY 2009.05.26
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=apply;|;execute;cancel;|;print;|;send;|;read;|;query;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=apply;|;execute;cancel;|;print;|;send;|;read;|;query;|;clear;|;close

print.Type=TMenuItem
print.Text=打印
print.Tip=打印(Ctrl+P)
print.M=S
print.Action=onPrint
print.pic=print.gif

apply.Type=TMenuItem
apply.Text=采样执行
apply.Tip=采样执行(Ctrl+H)
apply.M=H
apply.Action=onApply
apply.pic=convert.gif

execute.Type=TMenuItem
execute.Text=上账
execute.Tip=上账
execute.M=E
execute.Action=onMedExe
execute.pic=openbil-1.gif

cancel.Type=TMenuItem
cancel.Text=取消上账
cancel.Tip=取消上账
cancel.M=F
cancel.Action=onCancleMedExe
cancel.pic=Undo.gif

send.Type=TMenuItem
send.Text=传送条码机
send.Tip=传送条码机(Ctrl+T)
send.M=S
send.Action=onSend
send.pic=barcode.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除(Delete)
delete.M=N
delete.key=Ctrl+D
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

read.Type=TMenuItem
read.Text=医疗卡
read.Tip=医疗卡(Ctrl+P)
read.M=Q
read.Action=onRead
read.pic=042.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif


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