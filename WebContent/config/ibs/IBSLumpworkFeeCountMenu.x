#
# TBuilder Config File 
#
# Title:�ײͷ�������
#
# Company:JavaHis
#
# Author:ros 2014.05.13
#
# version 1.0
#

<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;clear;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;clear;close

save.Type=TMenuItem
save.Text=��������
save.Tip=��������
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

inCludeBatch.Type=TMenuItem
inCludeBatch.Text=�ײ�����ִ��
inCludeBatch.Tip=�ײ�����ִ��
inCludeBatch.M=G
inCludeBatch.key=Ctrl+G
inCludeBatch.Action=onExeIncludeBatch
inCludeBatch.pic=032.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
//close.Action=onClose
close.pic=close.gif
