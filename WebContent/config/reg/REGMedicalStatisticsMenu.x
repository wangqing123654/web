# 
#  Title:北京爱育华妇儿医院客户就诊数据统计表Menu
# 
#  Description:北京爱育华妇儿医院客户就诊数据统计表Menu
# 
#  Copyright: Copyright (c) Javahis 2009
# 
#  author huangtt 20150414
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;print;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;query;print;clear;close



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

print.Type=TMenuItem
print.Text=导出EXECL
print.Tip=导出EXECL
print.M=P
print.Action=onExecl
print.pic=export.gif