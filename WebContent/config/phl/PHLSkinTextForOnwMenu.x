#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:yanjing 2014.08.18
#
# version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;ekt;|;skiResult;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;ekt;|;skiResult;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

ekt.Type=TMenuItem
ekt.Text=ҽ�ƿ�
ekt.Tip=ҽ�ƿ�
ekt.M=S
ekt.key=
ekt.Action=onEkt
ekt.pic=042.gif

skiResult.Type=TMenuItem
skiResult.Text=Ƥ�Խ��
skiResult.Tip=Ƥ�Խ��
skiResult.M=P
skiResult.key=Ctrl+P
skiResult.Action=onSkiResult
skiResult.pic=032.gif


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
