<Type=TMenuBar>
UI.Item=File;Window
UI.button=insert;update;|;examine;unexamine;|;rate;|;new;|;delete;|;query;|;sms;|;emr;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=insert;update;|;examine;unexamine;|;rate;|;new;|;delete;|;query;|;sms;|;emr;|;export;|;clear;|;close

insert.Type=TMenuItem
insert.Text=����
insert.Tip=����
insert.M=S
insert.key=Ctrl+S
insert.Action=onSave
insert.pic=save.gif

update.Type=TMenuItem
update.Text=����
update.Tip=����
update.M=S
update.key=Ctrl+S
update.Action=onUpdate
update.pic=Redo.gif

examine.Type=TMenuItem
examine.Text=���
examine.Tip=���
examine.M=U
examine.key=Ctrl+U
examine.Action=onExamine
examine.pic=execute.gif

unexamine.Type=TMenuItem
unexamine.Text=ȡ�����
unexamine.Tip=ȡ�����
unexamine.M=U
unexamine.key=Ctrl+U
unexamine.Action=onUnExamine
unexamine.pic=004.gif

rate.Type=TMenuItem
rate.Text=����
rate.Tip=����
rate.M=R
rate.key=Ctrl+R
rate.Action=onRate
rate.pic=046.gif

new.Type=TMenuItem
new.Text=����
new.Tip=����
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=039.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=D
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

sms.Type=TMenuItem
sms.Text=������
sms.Tip=������
sms.M=M
sms.key=Ctrl+M
sms.Action=onSendSMS
sms.pic=014.gif

emr.Type=TMenuItem
emr.Text=������д
emr.Tip=������д
emr.M=S
emr.Action=onEditEMR
emr.pic=emr-1.gif

export.Type=TMenuItem
export.Text=����
export.Tip=����
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif