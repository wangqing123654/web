#
# TBuilder Config File 
#
# Title:ҽ���걨
#
# Company:BlueCore
#
# Author:���� 2012.02.10
#
# version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;readCard;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;readCard;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
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

readCard.Type=TMenuItem
readCard.Text=ˢ��
readCard.Tip=ˢ��
readCard.M=N
readCard.Action=onReadCard
readCard.pic=008.gif
