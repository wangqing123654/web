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
UI.button=save;|;query;|;delete;|;change;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;delete;|;change;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(save)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

new.Type=TMenuItem
new.Text=����
new.Tip=����(new)
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=new.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(query)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(clear)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(close)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

change.Type=TMenuItem
change.Text=��������
change.Tip=��������
change.M=
change.key=
change.Action=onChange
change.pic=modify.gif
