 #
  # Title:��Ա��ͣ��
  #
  # Description: ��Ա��ͣ��
  #
  # Copyright: 
  #
  # @author huangtt 20140424
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;read;|;revoke;|;reprint;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;read;|;revoke;|;reprint;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

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

revoke.Type=TMenuItem
revoke.Text=����ͣ��
revoke.Tip=����ͣ��
revoke.M=P
revoke.key=Ctrl+P
revoke.Action=onRevoke
revoke.pic=030.gif

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

reprint.Type=TMenuItem
reprint.Text=��ӡ
reprint.Tip=��ӡ
reprint.M=P
reprint.key=Ctrl+P
reprint.Action=onRePrint
reprint.pic=print_red.gif
