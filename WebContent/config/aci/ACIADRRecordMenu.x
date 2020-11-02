<Type=TMenuBar>
UI.Item=File;Window
UI.button=insert;delete;|;print;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=insert;delete;|;print;|;clear;|;close

insert.Type=TMenuItem
insert.Text=保存
insert.Tip=保存
insert.M=S
insert.key=Ctrl+S
insert.Action=onSave
insert.pic=save.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=D
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=C
print.Action=onPrint
print.pic=print.gif

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
clear.key=Ctrl+Q
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif