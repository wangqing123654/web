#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:sunqy 2014.05.12
#
# version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(save)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

new.Type=TMenuItem
new.Text=新增
new.Tip=新增(new)
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=new.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(query)
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
clear.Tip=清空(clear)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(close)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif


