#
# TBuilder Config File 
#
# Title:套餐费用整理
#
# Company:JavaHis
#
# Author:ros 2014.05.13
#
# version 1.0
#

<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;clear;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;clear;close

save.Type=TMenuItem
save.Text=费用整理
save.Tip=费用整理
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

inCludeBatch.Type=TMenuItem
inCludeBatch.Text=套餐批次执行
inCludeBatch.Tip=套餐批次执行
inCludeBatch.M=G
inCludeBatch.key=Ctrl+G
inCludeBatch.Action=onExeIncludeBatch
inCludeBatch.pic=032.gif

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
//close.Action=onClose
close.pic=close.gif
