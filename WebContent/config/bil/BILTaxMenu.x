# 
#  Title:门急诊挂号收费员日结报表
# 
#  Description:门急诊挂号收费员日结报表
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;read;|;query;|;cancel;|;saveinv;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;read;|;query;|;cancel;|;saveinv;|;clear;|;close

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

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

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

read.Type=TMenuItem
read.Text=医疗卡
read.Tip=医疗卡(Ctrl+R)
read.M=R
read.key=Ctrl+R
read.Action=onReadEKT
read.pic=042.gif

cancel.Type=TMenuItem
cancel.Text=取消完成
cancel.Tip=取消完成
cancel.M=F
cancel.key=Ctrl+H
cancel.Action=onCancle
cancel.pic=Undo.gif

saveinv.Type=TMenuItem
saveinv.Text=票号保存
saveinv.Tip=票号保存
saveinv.M=S
saveinv.key=Ctrl+S
saveinv.Action=onSaveInvNo
saveinv.pic=save.gif