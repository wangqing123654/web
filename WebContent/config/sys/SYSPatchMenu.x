#
  # Title: ���γ���
  #
  # Description:���γ���
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author zhangy
  # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;start;|;stop;|;server;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;delete;|;start;|;stop;|;server;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
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

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

start.Type=TMenuItem
start.Text=����
start.Tip=����
start.M=S
start.Action=onStart
start.pic=openbill.gif

stop.Type=TMenuItem
stop.Text=ֹͣ
stop.Tip=ֹͣ
stop.M=S
stop.Action=onStop
stop.pic=closebill.gif

server.Type=TMenuItem
server.Text=�����趨
server.Tip=�����趨
server.M=S
server.Action=onServer
server.pic=export.gif

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

