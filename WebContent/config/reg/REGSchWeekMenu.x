# 
#  Title:医师周班表
# 
#  Description:医师周班表
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.10.30
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;query;spreadout;|;spreadoutNew;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;Refresh;query;spreadout;|;spreadoutNew;|;clear;|;close



save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif


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

spreadout.Type=TMenuItem
spreadout.Text=展开
spreadout.Tip=展开
spreadout.M=O
spreadout.Action=onSpreadout
spreadout.pic=025.gif

spreadoutNew.Type=TMenuItem
spreadoutNew.Text=周转日
spreadoutNew.Tip=周转日
spreadoutNew.M=
spreadoutNew.Action=onSpreadoutNew
spreadoutNew.pic=032.gif

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

