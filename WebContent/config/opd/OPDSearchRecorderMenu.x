#
# TBuilder Config File 
#
# Title:�޸�ҩƷ���߼������ִ��״̬
#
# Company:JavaHis
#
# Author:yanj 2013.04.07
#
# version 1.0
#

<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;read;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;read;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQurey
query.pic=query.gif

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
close.pic=close.gif


read.Type=TMenuItem
read.Text=����
read.Tip=����(Ctrl+R)
read.M=
read.key=Ctrl+R
read.Action=onReadEKT
read.pic=042.gif

