#
# Title: 药房普通药上架menu
#
# Description:药房普通药上架menu
#
# Copyright: BlueCore (c) 2012
#
# @author wangzl
# @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=Refresh;query;clear;|;close


delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=S
delete.key=Ctrl+D
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

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