# 
#  Title:���������ӡMenu
# 
#  Description:���������ӡMenu
# 
#  Copyright: Copyright (c) Javahis 2009
# 
#  author JiaoY 2009.05.26
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=apply;|;execute;cancel;|;print;|;send;|;read;|;query;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=apply;|;execute;cancel;|;print;|;send;|;read;|;query;|;clear;|;close

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ(Ctrl+P)
print.M=S
print.Action=onPrint
print.pic=print.gif

apply.Type=TMenuItem
apply.Text=����ִ��
apply.Tip=����ִ��(Ctrl+H)
apply.M=H
apply.Action=onApply
apply.pic=convert.gif

execute.Type=TMenuItem
execute.Text=����
execute.Tip=����
execute.M=E
execute.Action=onMedExe
execute.pic=openbil-1.gif

cancel.Type=TMenuItem
cancel.Text=ȡ������
cancel.Tip=ȡ������
cancel.M=F
cancel.Action=onCancleMedExe
cancel.pic=Undo.gif

send.Type=TMenuItem
send.Text=���������
send.Tip=���������(Ctrl+T)
send.M=S
send.Action=onSend
send.pic=barcode.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��(Delete)
delete.M=N
delete.key=Ctrl+D
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

read.Type=TMenuItem
read.Text=ҽ�ƿ�
read.Tip=ҽ�ƿ�(Ctrl+P)
read.M=Q
read.Action=onRead
read.pic=042.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
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