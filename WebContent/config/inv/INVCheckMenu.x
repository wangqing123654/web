#############################################
# <p>Title:���ʹ���Menu </p>
#
# <p>Description:���ʹ���Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author zhangh 2013.05.3
# @version 1.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=print;|;preview;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=print;|;preview;|;clear;|;close


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=����ҳ
close.Tip=����ҳ(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=Undo.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ(Ctrl+P)
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

preview.Type=TMenuItem
preview.Text=Ԥ��
preview.Tip=Ԥ��(Ctrl+E)
preview.M=E
preview.key=Ctrl+E
preview.Action=onPrint
preview.pic=preview.gif

