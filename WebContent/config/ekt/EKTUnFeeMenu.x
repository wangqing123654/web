 #
  # Title: ҽ�ƿ��˷Ѳ���
  #
  # Description: ҽ�ƿ��˷Ѳ���
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2011.09.28
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;read;|;clear;|;EKTprint;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;read;|;clear;|;EKTprint;|;close


save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

read.Type=TMenuItem
read.Text=ҽ�ƿ�
read.Tip=ҽ�ƿ�(Ctrl+R)
read.M=R
read.key=Ctrl+R
read.Action=onReadEKT
read.pic=042.gif

EKTprint.Type=TMenuItem
EKTprint.Text=��ӡ
EKTprint.Tip=��ӡ
EKTprint.M=P
EKTprint.key=Ctrl+P
EKTprint.Action=onRePrint
EKTprint.pic=print_red.gif

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
