#####################################################
# <p>Title:ԤԼסԺMenu </p>
#
# <p>Description: ԤԼסԺMenu</p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK
# @version 1.0
#####################################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;read;|;notify;|;patinfo;|;child;|;print;|;stop;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.enTip=Window
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.enText=File
File.enTip=File
File.M=F
File.Item=save;|;query;|;read;|;notify;|;patinfo;|;child;|;print;|;stop;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.enTip=save
save.enText=save
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.enTip=query
query.enText=query
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

read.Type=TMenuItem
read.Text=ҽ�ƿ�
read.Tip=ҽ�ƿ�(Ctrl+R)
read.M=R
read.key=Ctrl+R
read.Action=onReadEKT
read.pic=042.gif

manage.Type=TMenuItem
manage.Text=ԤԼ����
manage.Tip=ԤԼ����
manage.enText=Reservation
manage.enTip=Reservation 
manage.M=
manage.key=
manage.Action=onManage
manage.pic=time.gif

notify.Type=TMenuItem
notify.Text=ԤԼ֪ͨ
notify.Tip=ԤԼ֪ͨ
notify.enText=Appointment
notify.enTip=Appointment
notify.M=
notify.key=
notify.Action=onNotify
notify.pic=044.gif

print.Type=TMenuItem
print.Text=סԺ֤
print.Tip=סԺ֤
print.enText=Admission Note
print.enTip=Admission Note
print.M=
print.key=
print.Action=onPrint
print.pic=print.gif

stop.Type=TMenuItem
stop.Text=ȡ��ԤԼ
stop.Tip=ȡ��ԤԼ
stop.enText=Cancel
stop.enTip=Cancel
stop.M=
stop.key=
stop.Action=onCanResv
stop.pic=closebill.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.enTip=Empty
clear.enText=Empty
clear.M=X
clear.key=
clear.Action=onClear
clear.pic=clear.gif


Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.enText=Refresh
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.enTip=Log out
close.enText=Log out
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

child.Type=TMenuItem
child.Text=�������Ǽ�
child.Tip=�������Ǽ�
child.M=
child.key=
child.Action=onChild
child.pic=035.gif

immunity.Type=TMenuItem
immunity.Text=����������
immunity.Tip=����������
immunity.Action=onImmunity
immunity.pic=013.gif

patinfo.Type=TMenuItem
patinfo.Text=��������
patinfo.Tip=��������
patinfo.enTip=Pat Profile
patinfo.enText=Pat Profile
patinfo.M=
patinfo.key=
patinfo.Action=onPatInfo
patinfo.pic=038.gif
