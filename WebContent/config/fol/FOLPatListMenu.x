#
# TBuilder Config File 
#
# Title:��û����б�Menu
#
# Company:BlueCore
#
# Author: 2014.01.02
#
# version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;overFol;|;clear;|;close;

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;overFol;|;clear;|;close;

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

overFol.Type=TMenuItem
overFol.Text=�������
overFol.Tip=�������
overFol.M=W
overFol.key=F8
overFol.Action=onOver
overFol.pic=closebill.gif

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
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif