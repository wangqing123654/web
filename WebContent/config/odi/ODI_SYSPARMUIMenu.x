#
# TBuilder Config File 
#
# Title:סԺ��������
#
# Company:JavaHis
#
# Author:WangM 2010.01.26
#
# version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;close

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=Q
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif