<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;newData;checkOk;checkNo;delete;query;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;newData;checkOk;checkNo;delete;query;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

newData.Type=TMenuItem
newData.Text=新增
newData.Tip=新增(Ctrl+I)
newData.M=I
newData.key=Ctrl+I
newData.Action=onNewData
newData.pic=039.gif

checkOk.Type=TMenuItem
checkOk.Text=审核通过
checkOk.Tip=审核通过(Ctrl+T)
checkOk.M=S
checkOk.key=Ctrl+T
checkOk.Action=onCheckOk
checkOk.pic=openbill.gif

checkNo.Type=TMenuItem
checkNo.Text=取消审核
checkNo.Tip=取消审核(Ctrl+Q)
checkNo.M=S
checkNo.key=Ctrl+Q
checkNo.Action=onCheckNo
checkNo.pic=closebill.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
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
