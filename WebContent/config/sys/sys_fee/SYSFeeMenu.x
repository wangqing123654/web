<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;new;delete;query;|;update;|;clear;|;export;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;Refresh;query;|;update;|;clear;|;export;|;regist;|;close

export.Type=TMenuItem
export.Text=导出
export.M=S
export.Action=onExport
export.pic=export.gif

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

new.Type=TMenuItem
new.Text=新增
new.Tip=新增
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=039.gif

//delete.Type=TMenuItem
//delete.Text=删除
//delete.Tip=删除
//delete.M=N
//delete.key=Delete
//delete.Action=onDelete
//delete.pic=delete.gif


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

regist.Type=TMenuItem
regist.Text=注册照相组件
regist.Tip=注册照相组件
regist.M=
regist.key=
regist.Action=onRegist
regist.pic=007.gif

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

update.Type=TMenuItem
update.Text=套餐价钱更新
update.Tip=套餐价钱更新
update.M=T
update.key=Ctrl+T
update.Action=onUpdate
update.pic=tempsave.gif

